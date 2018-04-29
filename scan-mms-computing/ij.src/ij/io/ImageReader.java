/*     */ package ij.io;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ import javax.imageio.ImageIO;
/*     */ 
/*     */ public class ImageReader
/*     */ {
/*     */   private static final int CLEAR_CODE = 256;
/*     */   private static final int EOI_CODE = 257;
/*     */   private FileInfo fi;
/*     */   private int width;
/*     */   private int height;
/*     */   private long skipCount;
/*     */   private int bytesPerPixel;
/*     */   private int bufferSize;
/*     */   private int nPixels;
/*     */   private long byteCount;
/*  24 */   private boolean showProgressBar = true;
/*     */   private int eofErrorCount;
/*     */   private long startTime;
/*     */   public double min;
/*     */   public double max;
/*     */ 
/*     */   public ImageReader(FileInfo fi)
/*     */   {
/*  34 */     this.fi = fi;
/*  35 */     this.width = fi.width;
/*  36 */     this.height = fi.height;
/*  37 */     this.skipCount = fi.getOffset();
/*     */   }
/*     */ 
/*     */   void eofError() {
/*  41 */     this.eofErrorCount += 1;
/*     */   }
/*     */ 
/*     */   byte[] read8bitImage(InputStream in) throws IOException {
/*  45 */     if (this.fi.compression > 1)
/*  46 */       return readCompressed8bitImage(in);
/*  47 */     byte[] pixels = new byte[this.nPixels];
/*     */ 
/*  50 */     int totalRead = 0;
/*  51 */     while (totalRead < this.byteCount)
/*     */     {
/*     */       int count;
/*     */       int count;
/*  52 */       if (totalRead + this.bufferSize > this.byteCount)
/*  53 */         count = (int)(this.byteCount - totalRead);
/*     */       else
/*  55 */         count = this.bufferSize;
/*  56 */       int actuallyRead = in.read(pixels, totalRead, count);
/*  57 */       if (actuallyRead == -1) { eofError(); break; }
/*  58 */       totalRead += actuallyRead;
/*  59 */       showProgress(totalRead, this.byteCount);
/*     */     }
/*  61 */     return pixels;
/*     */   }
/*     */ 
/*     */   byte[] readCompressed8bitImage(InputStream in) throws IOException {
/*  65 */     byte[] pixels = new byte[this.nPixels];
/*  66 */     int current = 0;
/*  67 */     byte last = 0;
/*  68 */     for (int i = 0; i < this.fi.stripOffsets.length; i++) {
/*  69 */       if ((in instanceof RandomAccessStream)) {
/*  70 */         ((RandomAccessStream)in).seek(this.fi.stripOffsets[i]);
/*  71 */       } else if (i > 0) {
/*  72 */         long skip = (this.fi.stripOffsets[i] & 0xFFFFFFFF) - (this.fi.stripOffsets[(i - 1)] & 0xFFFFFFFF) - this.fi.stripLengths[(i - 1)];
/*  73 */         if (skip > 0L) in.skip(skip);
/*     */       }
/*  75 */       byte[] byteArray = new byte[this.fi.stripLengths[i]];
/*  76 */       int read = 0; int left = byteArray.length;
/*  77 */       while (left > 0) {
/*  78 */         int r = in.read(byteArray, read, left);
/*  79 */         if (r == -1) { eofError(); break; }
/*  80 */         read += r;
/*  81 */         left -= r;
/*     */       }
/*  83 */       byteArray = uncompress(byteArray);
/*  84 */       int length = byteArray.length;
/*  85 */       length -= length % this.fi.width;
/*  86 */       if (this.fi.compression == 3) {
/*  87 */         for (int b = 0; b < length; b++)
/*     */         {
/*     */           int tmp245_243 = b;
/*     */           byte[] tmp245_241 = byteArray; tmp245_241[tmp245_243] = ((byte)(tmp245_241[tmp245_243] + last));
/*  89 */           last = b % this.fi.width == this.fi.width - 1 ? 0 : byteArray[b];
/*     */         }
/*     */       }
/*  92 */       if (current + length > pixels.length) length = pixels.length - current;
/*  93 */       System.arraycopy(byteArray, 0, pixels, current, length);
/*  94 */       current += length;
/*  95 */       showProgress(i + 1, this.fi.stripOffsets.length);
/*     */     }
/*  97 */     return pixels;
/*     */   }
/*     */ 
/*     */   short[] read16bitImage(InputStream in) throws IOException
/*     */   {
/* 102 */     if ((this.fi.compression > 1) || ((this.fi.stripOffsets != null) && (this.fi.stripOffsets.length > 1))) {
/* 103 */       return readCompressed16bitImage(in);
/*     */     }
/* 105 */     byte[] buffer = new byte[this.bufferSize];
/* 106 */     short[] pixels = new short[this.nPixels];
/* 107 */     long totalRead = 0L;
/* 108 */     int base = 0;
/*     */ 
/* 112 */     while (totalRead < this.byteCount) {
/* 113 */       if (totalRead + this.bufferSize > this.byteCount)
/* 114 */         this.bufferSize = ((int)(this.byteCount - totalRead));
/* 115 */       int bufferCount = 0;
/* 116 */       while (bufferCount < this.bufferSize) {
/* 117 */         int count = in.read(buffer, bufferCount, this.bufferSize - bufferCount);
/* 118 */         if (count == -1) {
/* 119 */           if (bufferCount > 0)
/* 120 */             for (int i = bufferCount; i < this.bufferSize; i++) buffer[i] = 0;
/* 121 */           totalRead = this.byteCount;
/* 122 */           eofError();
/* 123 */           break;
/*     */         }
/* 125 */         bufferCount += count;
/*     */       }
/* 127 */       totalRead += this.bufferSize;
/* 128 */       showProgress(totalRead, this.byteCount);
/* 129 */       int pixelsRead = this.bufferSize / this.bytesPerPixel;
/* 130 */       if (this.fi.intelByteOrder) {
/* 131 */         if (this.fi.fileType == 1) {
/* 132 */           int i = base; for (int j = 0; i < base + pixelsRead; j += 2) {
/* 133 */             pixels[i] = ((short)(((buffer[(j + 1)] & 0xFF) << 8 | buffer[j] & 0xFF) + 32768));
/*     */ 
/* 132 */             i++;
/*     */           }
/*     */         } else {
/* 135 */           int i = base; for (int j = 0; i < base + pixelsRead; j += 2) {
/* 136 */             pixels[i] = ((short)((buffer[(j + 1)] & 0xFF) << 8 | buffer[j] & 0xFF));
/*     */ 
/* 135 */             i++;
/*     */           }
/*     */         }
/* 138 */       } else if (this.fi.fileType == 1) {
/* 139 */         int i = base; for (int j = 0; i < base + pixelsRead; j += 2) {
/* 140 */           pixels[i] = ((short)(((buffer[j] & 0xFF) << 8 | buffer[(j + 1)] & 0xFF) + 32768));
/*     */ 
/* 139 */           i++;
/*     */         }
/*     */       } else {
/* 142 */         int i = base; for (int j = 0; i < base + pixelsRead; j += 2) {
/* 143 */           pixels[i] = ((short)((buffer[j] & 0xFF) << 8 | buffer[(j + 1)] & 0xFF));
/*     */ 
/* 142 */           i++;
/*     */         }
/*     */       }
/* 145 */       base += pixelsRead;
/*     */     }
/* 147 */     return pixels;
/*     */   }
/*     */ 
/*     */   short[] readCompressed16bitImage(InputStream in) throws IOException {
/* 151 */     short[] pixels = new short[this.nPixels];
/* 152 */     int base = 0;
/* 153 */     short last = 0;
/* 154 */     for (int k = 0; k < this.fi.stripOffsets.length; k++)
/*     */     {
/* 156 */       if ((in instanceof RandomAccessStream)) {
/* 157 */         ((RandomAccessStream)in).seek(this.fi.stripOffsets[k]);
/* 158 */       } else if (k > 0) {
/* 159 */         long skip = (this.fi.stripOffsets[k] & 0xFFFFFFFF) - (this.fi.stripOffsets[(k - 1)] & 0xFFFFFFFF) - this.fi.stripLengths[(k - 1)];
/* 160 */         if (skip > 0L) in.skip(skip);
/*     */       }
/* 162 */       byte[] byteArray = new byte[this.fi.stripLengths[k]];
/* 163 */       int read = 0; int left = byteArray.length;
/* 164 */       while (left > 0) {
/* 165 */         int r = in.read(byteArray, read, left);
/* 166 */         if (r == -1) { eofError(); break; }
/* 167 */         read += r;
/* 168 */         left -= r;
/*     */       }
/* 170 */       byteArray = uncompress(byteArray);
/* 171 */       int pixelsRead = byteArray.length / this.bytesPerPixel;
/* 172 */       pixelsRead -= pixelsRead % this.fi.width;
/* 173 */       int pmax = base + pixelsRead;
/* 174 */       if (pmax > this.nPixels) pmax = this.nPixels;
/* 175 */       if (this.fi.intelByteOrder) {
/* 176 */         int i = base; for (int j = 0; i < pmax; j += 2) {
/* 177 */           pixels[i] = ((short)((byteArray[(j + 1)] & 0xFF) << 8 | byteArray[j] & 0xFF));
/*     */ 
/* 176 */           i++;
/*     */         }
/*     */       } else {
/* 179 */         int i = base; for (int j = 0; i < pmax; j += 2) {
/* 180 */           pixels[i] = ((short)((byteArray[j] & 0xFF) << 8 | byteArray[(j + 1)] & 0xFF));
/*     */ 
/* 179 */           i++;
/*     */         }
/*     */       }
/* 182 */       if (this.fi.compression == 3) {
/* 183 */         for (int b = base; b < pmax; b++)
/*     */         {
/*     */           int tmp385_383 = b;
/*     */           short[] tmp385_382 = pixels; tmp385_382[tmp385_383] = ((short)(tmp385_382[tmp385_383] + last));
/* 185 */           last = b % this.fi.width == this.fi.width - 1 ? 0 : pixels[b];
/*     */         }
/*     */       }
/* 188 */       base += pixelsRead;
/* 189 */       showProgress(k + 1, this.fi.stripOffsets.length);
/*     */     }
/* 191 */     if (this.fi.fileType == 1)
/*     */     {
/* 193 */       for (int i = 0; i < this.nPixels; i++)
/* 194 */         pixels[i] = ((short)(pixels[i] + 32768));
/*     */     }
/* 196 */     return pixels;
/*     */   }
/*     */ 
/*     */   float[] read32bitImage(InputStream in) throws IOException {
/* 200 */     if (this.fi.compression > 1) {
/* 201 */       return readCompressed32bitImage(in);
/*     */     }
/* 203 */     byte[] buffer = new byte[this.bufferSize];
/* 204 */     float[] pixels = new float[this.nPixels];
/* 205 */     long totalRead = 0L;
/* 206 */     int base = 0;
/*     */ 
/* 211 */     while (totalRead < this.byteCount) {
/* 212 */       if (totalRead + this.bufferSize > this.byteCount)
/* 213 */         this.bufferSize = ((int)(this.byteCount - totalRead));
/* 214 */       int bufferCount = 0;
/* 215 */       while (bufferCount < this.bufferSize) {
/* 216 */         int count = in.read(buffer, bufferCount, this.bufferSize - bufferCount);
/* 217 */         if (count == -1) {
/* 218 */           if (bufferCount > 0)
/* 219 */             for (int i = bufferCount; i < this.bufferSize; i++) buffer[i] = 0;
/* 220 */           totalRead = this.byteCount;
/* 221 */           eofError();
/* 222 */           break;
/*     */         }
/* 224 */         bufferCount += count;
/*     */       }
/* 226 */       totalRead += this.bufferSize;
/* 227 */       showProgress(totalRead, this.byteCount);
/* 228 */       int pixelsRead = this.bufferSize / this.bytesPerPixel;
/* 229 */       int pmax = base + pixelsRead;
/* 230 */       if (pmax > this.nPixels) pmax = this.nPixels;
/* 231 */       int j = 0;
/* 232 */       if (this.fi.intelByteOrder)
/* 233 */         for (int i = base; i < pmax; i++) {
/* 234 */           int tmp = (buffer[(j + 3)] & 0xFF) << 24 | (buffer[(j + 2)] & 0xFF) << 16 | (buffer[(j + 1)] & 0xFF) << 8 | buffer[j] & 0xFF;
/* 235 */           if (this.fi.fileType == 4)
/* 236 */             pixels[i] = Float.intBitsToFloat(tmp);
/* 237 */           else if (this.fi.fileType == 11)
/* 238 */             pixels[i] = ((float)(tmp & 0xFFFFFFFF));
/*     */           else
/* 240 */             pixels[i] = tmp;
/* 241 */           j += 4;
/*     */         }
/*     */       else
/* 244 */         for (int i = base; i < pmax; i++) {
/* 245 */           int tmp = (buffer[j] & 0xFF) << 24 | (buffer[(j + 1)] & 0xFF) << 16 | (buffer[(j + 2)] & 0xFF) << 8 | buffer[(j + 3)] & 0xFF;
/* 246 */           if (this.fi.fileType == 4)
/* 247 */             pixels[i] = Float.intBitsToFloat(tmp);
/* 248 */           else if (this.fi.fileType == 11)
/* 249 */             pixels[i] = ((float)(tmp & 0xFFFFFFFF));
/*     */           else
/* 251 */             pixels[i] = tmp;
/* 252 */           j += 4;
/*     */         }
/* 254 */       base += pixelsRead;
/*     */     }
/* 256 */     return pixels;
/*     */   }
/*     */ 
/*     */   float[] readCompressed32bitImage(InputStream in) throws IOException {
/* 260 */     float[] pixels = new float[this.nPixels];
/* 261 */     int base = 0;
/* 262 */     float last = 0.0F;
/* 263 */     for (int k = 0; k < this.fi.stripOffsets.length; k++)
/*     */     {
/* 265 */       if ((in instanceof RandomAccessStream)) {
/* 266 */         ((RandomAccessStream)in).seek(this.fi.stripOffsets[k]);
/* 267 */       } else if (k > 0) {
/* 268 */         long skip = (this.fi.stripOffsets[k] & 0xFFFFFFFF) - (this.fi.stripOffsets[(k - 1)] & 0xFFFFFFFF) - this.fi.stripLengths[(k - 1)];
/* 269 */         if (skip > 0L) in.skip(skip);
/*     */       }
/* 271 */       byte[] byteArray = new byte[this.fi.stripLengths[k]];
/* 272 */       int read = 0; int left = byteArray.length;
/* 273 */       while (left > 0) {
/* 274 */         int r = in.read(byteArray, read, left);
/* 275 */         if (r == -1) { eofError(); break; }
/* 276 */         read += r;
/* 277 */         left -= r;
/*     */       }
/* 279 */       byteArray = uncompress(byteArray);
/* 280 */       int pixelsRead = byteArray.length / this.bytesPerPixel;
/* 281 */       pixelsRead -= pixelsRead % this.fi.width;
/* 282 */       int pmax = base + pixelsRead;
/* 283 */       if (pmax > this.nPixels) pmax = this.nPixels;
/*     */ 
/* 285 */       if (this.fi.intelByteOrder) {
/* 286 */         int i = base; for (int j = 0; i < pmax; j += 4) {
/* 287 */           int tmp = (byteArray[(j + 3)] & 0xFF) << 24 | (byteArray[(j + 2)] & 0xFF) << 16 | (byteArray[(j + 1)] & 0xFF) << 8 | byteArray[j] & 0xFF;
/* 288 */           pixels[i] = Float.intBitsToFloat(tmp);
/*     */ 
/* 286 */           i++;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 291 */         int i = base; for (int j = 0; i < pmax; j += 4) {
/* 292 */           int tmp = (byteArray[j] & 0xFF) << 24 | (byteArray[(j + 1)] & 0xFF) << 16 | (byteArray[(j + 2)] & 0xFF) << 8 | byteArray[(j + 3)] & 0xFF;
/* 293 */           pixels[i] = Float.intBitsToFloat(tmp);
/*     */ 
/* 291 */           i++;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 296 */       if (this.fi.compression == 3) {
/* 297 */         for (int b = base; b < pmax; b++) {
/* 298 */           pixels[b] += last;
/* 299 */           last = b % this.fi.width == this.fi.width - 1 ? 0.0F : pixels[b];
/*     */         }
/*     */       }
/* 302 */       base += pixelsRead;
/* 303 */       showProgress(k + 1, this.fi.stripOffsets.length);
/*     */     }
/* 305 */     return pixels;
/*     */   }
/*     */ 
/*     */   float[] read64bitImage(InputStream in) throws IOException
/*     */   {
/* 310 */     byte[] buffer = new byte[this.bufferSize];
/* 311 */     float[] pixels = new float[this.nPixels];
/* 312 */     long totalRead = 0L;
/* 313 */     int base = 0;
/*     */ 
/* 319 */     while (totalRead < this.byteCount) {
/* 320 */       if (totalRead + this.bufferSize > this.byteCount)
/* 321 */         this.bufferSize = ((int)(this.byteCount - totalRead));
/* 322 */       int bufferCount = 0;
/* 323 */       while (bufferCount < this.bufferSize) {
/* 324 */         int count = in.read(buffer, bufferCount, this.bufferSize - bufferCount);
/* 325 */         if (count == -1) {
/* 326 */           if (bufferCount > 0)
/* 327 */             for (int i = bufferCount; i < this.bufferSize; i++) buffer[i] = 0;
/* 328 */           totalRead = this.byteCount;
/* 329 */           eofError();
/* 330 */           break;
/*     */         }
/* 332 */         bufferCount += count;
/*     */       }
/* 334 */       totalRead += this.bufferSize;
/* 335 */       showProgress(totalRead, this.byteCount);
/* 336 */       int pixelsRead = this.bufferSize / this.bytesPerPixel;
/* 337 */       int j = 0;
/* 338 */       for (int i = base; i < base + pixelsRead; i++) {
/* 339 */         long b1 = buffer[(j + 7)] & 0xFF; long b2 = buffer[(j + 6)] & 0xFF; long b3 = buffer[(j + 5)] & 0xFF; long b4 = buffer[(j + 4)] & 0xFF;
/* 340 */         long b5 = buffer[(j + 3)] & 0xFF; long b6 = buffer[(j + 2)] & 0xFF; long b7 = buffer[(j + 1)] & 0xFF; long b8 = buffer[j] & 0xFF;
/*     */         long tmp;
/*     */         long tmp;
/* 341 */         if (this.fi.intelByteOrder)
/* 342 */           tmp = b1 << 56 | b2 << 48 | b3 << 40 | b4 << 32 | b5 << 24 | b6 << 16 | b7 << 8 | b8;
/*     */         else
/* 344 */           tmp = b8 << 56 | b7 << 48 | b6 << 40 | b5 << 32 | b4 << 24 | b3 << 16 | b2 << 8 | b1;
/* 345 */         pixels[i] = ((float)Double.longBitsToDouble(tmp));
/* 346 */         j += 8;
/*     */       }
/* 348 */       base += pixelsRead;
/*     */     }
/* 350 */     return pixels;
/*     */   }
/*     */ 
/*     */   int[] readChunkyRGB(InputStream in) throws IOException {
/* 354 */     if (this.fi.compression == 4)
/* 355 */       return readJPEG(in);
/* 356 */     if (this.fi.compression > 1) {
/* 357 */       return readCompressedChunkyRGB(in);
/*     */     }
/* 359 */     this.bufferSize = (24 * this.width);
/* 360 */     byte[] buffer = new byte[this.bufferSize];
/* 361 */     int[] pixels = new int[this.nPixels];
/* 362 */     long totalRead = 0L;
/* 363 */     int base = 0;
/*     */ 
/* 368 */     while (totalRead < this.byteCount) {
/* 369 */       if (totalRead + this.bufferSize > this.byteCount)
/* 370 */         this.bufferSize = ((int)(this.byteCount - totalRead));
/* 371 */       int bufferCount = 0;
/* 372 */       while (bufferCount < this.bufferSize) {
/* 373 */         int count = in.read(buffer, bufferCount, this.bufferSize - bufferCount);
/* 374 */         if (count == -1) {
/* 375 */           if (bufferCount > 0)
/* 376 */             for (int i = bufferCount; i < this.bufferSize; i++) buffer[i] = 0;
/* 377 */           totalRead = this.byteCount;
/* 378 */           eofError();
/* 379 */           break;
/*     */         }
/* 381 */         bufferCount += count;
/*     */       }
/* 383 */       totalRead += this.bufferSize;
/* 384 */       showProgress(totalRead, this.byteCount);
/* 385 */       int pixelsRead = this.bufferSize / this.bytesPerPixel;
/* 386 */       boolean bgr = this.fi.fileType == 10;
/* 387 */       int j = 0;
/* 388 */       for (int i = base; i < base + pixelsRead; i++)
/*     */       {
/*     */         int r;
/*     */         int g;
/*     */         int b;
/* 389 */         if (this.bytesPerPixel == 4)
/*     */         {
/*     */           int g;
/* 390 */           if (this.fi.fileType == 15) {
/* 391 */             int b = buffer[(j++)] & 0xFF;
/* 392 */             j++;
/* 393 */             int r = buffer[(j++)] & 0xFF;
/* 394 */             g = buffer[(j++)] & 0xFF;
/* 395 */           } else if (this.fi.fileType == 18) {
/* 396 */             int b = buffer[(j++)] & 0xFF;
/* 397 */             int g = buffer[(j++)] & 0xFF;
/* 398 */             int r = buffer[(j++)] & 0xFF;
/* 399 */             j++;
/*     */           } else {
/* 401 */             int r = buffer[(j++)] & 0xFF;
/* 402 */             int g = buffer[(j++)] & 0xFF;
/* 403 */             int b = buffer[(j++)] & 0xFF;
/* 404 */             j++;
/*     */           }
/*     */         } else {
/* 407 */           r = buffer[(j++)] & 0xFF;
/* 408 */           g = buffer[(j++)] & 0xFF;
/* 409 */           b = buffer[(j++)] & 0xFF;
/*     */         }
/* 411 */         if (bgr)
/* 412 */           pixels[i] = (0xFF000000 | b << 16 | g << 8 | r);
/*     */         else
/* 414 */           pixels[i] = (0xFF000000 | r << 16 | g << 8 | b);
/*     */       }
/* 416 */       base += pixelsRead;
/*     */     }
/* 418 */     return pixels;
/*     */   }
/*     */ 
/*     */   int[] readCompressedChunkyRGB(InputStream in) throws IOException {
/* 422 */     int[] pixels = new int[this.nPixels];
/* 423 */     int base = 0;
/* 424 */     int lastRed = 0; int lastGreen = 0; int lastBlue = 0;
/*     */ 
/* 426 */     int red = 0; int green = 0; int blue = 0;
/* 427 */     boolean bgr = this.fi.fileType == 10;
/* 428 */     boolean differencing = this.fi.compression == 3;
/* 429 */     for (int i = 0; i < this.fi.stripOffsets.length; i++) {
/* 430 */       if (i > 0) {
/* 431 */         long skip = (this.fi.stripOffsets[i] & 0xFFFFFFFF) - (this.fi.stripOffsets[(i - 1)] & 0xFFFFFFFF) - this.fi.stripLengths[(i - 1)];
/* 432 */         if (skip > 0L) in.skip(skip);
/*     */       }
/* 434 */       byte[] byteArray = new byte[this.fi.stripLengths[i]];
/* 435 */       int read = 0; int left = byteArray.length;
/* 436 */       while (left > 0) {
/* 437 */         int r = in.read(byteArray, read, left);
/* 438 */         if (r == -1) { eofError(); break; }
/* 439 */         read += r;
/* 440 */         left -= r;
/*     */       }
/* 442 */       byteArray = uncompress(byteArray);
/* 443 */       if (differencing) {
/* 444 */         for (int b = 0; b < byteArray.length; b++)
/* 445 */           if (b / this.bytesPerPixel % this.fi.width != 0)
/*     */           {
/*     */             int tmp266_264 = b;
/*     */             byte[] tmp266_262 = byteArray; tmp266_262[tmp266_264] = ((byte)(tmp266_262[tmp266_264] + byteArray[(b - this.bytesPerPixel)]));
/*     */           }
/*     */       }
/* 449 */       int k = 0;
/* 450 */       int pixelsRead = byteArray.length / this.bytesPerPixel;
/* 451 */       pixelsRead -= pixelsRead % this.fi.width;
/* 452 */       int pmax = base + pixelsRead;
/* 453 */       if (pmax > this.nPixels) pmax = this.nPixels;
/* 454 */       for (int j = base; j < pmax; j++) {
/* 455 */         if (this.bytesPerPixel == 4) {
/* 456 */           red = byteArray[(k++)] & 0xFF;
/* 457 */           green = byteArray[(k++)] & 0xFF;
/* 458 */           blue = byteArray[(k++)] & 0xFF;
/* 459 */           k++;
/*     */         } else {
/* 461 */           red = byteArray[(k++)] & 0xFF;
/* 462 */           green = byteArray[(k++)] & 0xFF;
/* 463 */           blue = byteArray[(k++)] & 0xFF;
/*     */         }
/* 465 */         if (bgr)
/* 466 */           pixels[j] = (0xFF000000 | blue << 16 | green << 8 | red);
/*     */         else
/* 468 */           pixels[j] = (0xFF000000 | red << 16 | green << 8 | blue);
/*     */       }
/* 470 */       base += pixelsRead;
/* 471 */       showProgress(i + 1, this.fi.stripOffsets.length);
/*     */     }
/* 473 */     return pixels;
/*     */   }
/*     */ 
/*     */   int[] readJPEG(InputStream in) throws IOException {
/* 477 */     BufferedImage bi = ImageIO.read(in);
/* 478 */     ImageProcessor ip = new ColorProcessor(bi);
/* 479 */     return (int[])ip.getPixels();
/*     */   }
/*     */ 
/*     */   int[] readPlanarRGB(InputStream in) throws IOException {
/* 483 */     if (this.fi.compression > 1)
/* 484 */       return readCompressedPlanarRGBImage(in);
/* 485 */     DataInputStream dis = new DataInputStream(in);
/* 486 */     int planeSize = this.nPixels;
/* 487 */     byte[] buffer = new byte[planeSize];
/* 488 */     int[] pixels = new int[this.nPixels];
/*     */ 
/* 491 */     this.startTime = 0L;
/* 492 */     showProgress(10, 100);
/* 493 */     dis.readFully(buffer);
/* 494 */     for (int i = 0; i < planeSize; i++) {
/* 495 */       int r = buffer[i] & 0xFF;
/* 496 */       pixels[i] = (0xFF000000 | r << 16);
/*     */     }
/*     */ 
/* 499 */     showProgress(40, 100);
/* 500 */     dis.readFully(buffer);
/* 501 */     for (int i = 0; i < planeSize; i++) {
/* 502 */       int g = buffer[i] & 0xFF;
/* 503 */       pixels[i] |= g << 8;
/*     */     }
/*     */ 
/* 506 */     showProgress(70, 100);
/* 507 */     dis.readFully(buffer);
/* 508 */     for (int i = 0; i < planeSize; i++) {
/* 509 */       int b = buffer[i] & 0xFF;
/* 510 */       pixels[i] |= b;
/*     */     }
/*     */ 
/* 513 */     showProgress(90, 100);
/* 514 */     return pixels;
/*     */   }
/*     */ 
/*     */   int[] readCompressedPlanarRGBImage(InputStream in) throws IOException {
/* 518 */     int[] pixels = new int[this.nPixels];
/*     */ 
/* 520 */     this.nPixels *= 3;
/* 521 */     byte[] buffer = readCompressed8bitImage(in);
/* 522 */     this.nPixels /= 3;
/* 523 */     for (int i = 0; i < this.nPixels; i++) {
/* 524 */       int r = buffer[i] & 0xFF;
/* 525 */       pixels[i] = (0xFF000000 | r << 16);
/*     */     }
/* 527 */     for (int i = 0; i < this.nPixels; i++) {
/* 528 */       int g = buffer[(this.nPixels + i)] & 0xFF;
/* 529 */       pixels[i] |= g << 8;
/*     */     }
/* 531 */     for (int i = 0; i < this.nPixels; i++) {
/* 532 */       int b = buffer[(this.nPixels * 2 + i)] & 0xFF;
/* 533 */       pixels[i] |= b;
/*     */     }
/* 535 */     return pixels;
/*     */   }
/*     */ 
/*     */   private void showProgress(int current, int last) {
/* 539 */     if ((this.showProgressBar) && (System.currentTimeMillis() - this.startTime > 500L))
/* 540 */       IJ.showProgress(current, last);
/*     */   }
/*     */ 
/*     */   private void showProgress(long current, long last) {
/* 544 */     showProgress((int)(current / 10L), (int)(last / 10L));
/*     */   }
/*     */ 
/*     */   Object readRGB48(InputStream in) throws IOException {
/* 548 */     if (this.fi.compression > 1)
/* 549 */       return readCompressedRGB48(in);
/* 550 */     int channels = 3;
/* 551 */     short[][] stack = new short[channels][this.nPixels];
/* 552 */     DataInputStream dis = new DataInputStream(in);
/* 553 */     int pixel = 0;
/* 554 */     int min = 65535; int max = 0;
/* 555 */     if (this.fi.stripLengths == null) {
/* 556 */       this.fi.stripLengths = new int[this.fi.stripOffsets.length];
/* 557 */       this.fi.stripLengths[0] = (this.width * this.height * this.bytesPerPixel);
/*     */     }
/* 559 */     for (int i = 0; i < this.fi.stripOffsets.length; i++) {
/* 560 */       if (i > 0) {
/* 561 */         long skip = (this.fi.stripOffsets[i] & 0xFFFFFFFF) - (this.fi.stripOffsets[(i - 1)] & 0xFFFFFFFF) - this.fi.stripLengths[(i - 1)];
/* 562 */         if (skip > 0L) dis.skip(skip);
/*     */       }
/* 564 */       int len = this.fi.stripLengths[i];
/* 565 */       int bytesToGo = (this.nPixels - pixel) * channels * 2;
/* 566 */       if (len > bytesToGo) len = bytesToGo;
/* 567 */       byte[] buffer = new byte[len];
/* 568 */       dis.readFully(buffer);
/*     */ 
/* 570 */       int channel = 0;
/* 571 */       boolean intel = this.fi.intelByteOrder;
/* 572 */       for (int base = 0; base < len; base += 2)
/*     */       {
/*     */         int value;
/*     */         int value;
/* 573 */         if (intel)
/* 574 */           value = (buffer[(base + 1)] & 0xFF) << 8 | buffer[base] & 0xFF;
/*     */         else
/* 576 */           value = (buffer[base] & 0xFF) << 8 | buffer[(base + 1)] & 0xFF;
/* 577 */         if (value < min) min = value;
/* 578 */         if (value > max) max = value;
/* 579 */         stack[channel][pixel] = ((short)value);
/* 580 */         channel++;
/* 581 */         if (channel == channels) {
/* 582 */           channel = 0;
/* 583 */           pixel++;
/*     */         }
/*     */       }
/* 586 */       showProgress(i + 1, this.fi.stripOffsets.length);
/*     */     }
/* 588 */     this.min = min; this.max = max;
/* 589 */     return stack;
/*     */   }
/*     */ 
/*     */   Object readCompressedRGB48(InputStream in) throws IOException {
/* 593 */     if (this.fi.compression == 3)
/* 594 */       throw new IOException("ImageJ cannot open 48-bit LZW compressed TIFFs with predictor");
/* 595 */     int channels = 3;
/* 596 */     short[][] stack = new short[channels][this.nPixels];
/* 597 */     DataInputStream dis = new DataInputStream(in);
/* 598 */     int pixel = 0;
/* 599 */     int min = 65535; int max = 0;
/* 600 */     for (int i = 0; i < this.fi.stripOffsets.length; i++) {
/* 601 */       if (i > 0) {
/* 602 */         long skip = (this.fi.stripOffsets[i] & 0xFFFFFFFF) - (this.fi.stripOffsets[(i - 1)] & 0xFFFFFFFF) - this.fi.stripLengths[(i - 1)];
/* 603 */         if (skip > 0L) dis.skip(skip);
/*     */       }
/* 605 */       int len = this.fi.stripLengths[i];
/* 606 */       byte[] buffer = new byte[len];
/* 607 */       dis.readFully(buffer);
/* 608 */       buffer = uncompress(buffer);
/* 609 */       len = buffer.length;
/* 610 */       if (len % 2 != 0) len--;
/*     */ 
/* 612 */       int channel = 0;
/* 613 */       boolean intel = this.fi.intelByteOrder;
/* 614 */       for (int base = 0; (base < len) && (pixel < this.nPixels); base += 2)
/*     */       {
/*     */         int value;
/*     */         int value;
/* 615 */         if (intel)
/* 616 */           value = (buffer[(base + 1)] & 0xFF) << 8 | buffer[base] & 0xFF;
/*     */         else
/* 618 */           value = (buffer[base] & 0xFF) << 8 | buffer[(base + 1)] & 0xFF;
/* 619 */         if (value < min) min = value;
/* 620 */         if (value > max) max = value;
/* 621 */         stack[channel][pixel] = ((short)value);
/* 622 */         channel++;
/* 623 */         if (channel == channels) {
/* 624 */           channel = 0;
/* 625 */           pixel++;
/*     */         }
/*     */       }
/* 628 */       showProgress(i + 1, this.fi.stripOffsets.length);
/*     */     }
/* 630 */     this.min = min; this.max = max;
/* 631 */     return stack;
/*     */   }
/*     */ 
/*     */   Object readRGB48Planar(InputStream in) throws IOException {
/* 635 */     short[] red = read16bitImage(in);
/* 636 */     short[] green = read16bitImage(in);
/* 637 */     short[] blue = read16bitImage(in);
/* 638 */     Object[] stack = new Object[3];
/* 639 */     stack[0] = red;
/* 640 */     stack[1] = green;
/* 641 */     stack[2] = blue;
/* 642 */     return stack;
/*     */   }
/*     */ 
/*     */   short[] read12bitImage(InputStream in) throws IOException {
/* 646 */     int bytesPerLine = (int)(this.width * 1.5D);
/* 647 */     if ((this.width & 0x1) == 1) bytesPerLine++;
/* 648 */     byte[] buffer = new byte[bytesPerLine * this.height];
/* 649 */     short[] pixels = new short[this.nPixels];
/* 650 */     DataInputStream dis = new DataInputStream(in);
/* 651 */     dis.readFully(buffer);
/* 652 */     for (int y = 0; y < this.height; y++) {
/* 653 */       int index1 = y * bytesPerLine;
/* 654 */       int index2 = y * this.width;
/* 655 */       int count = 0;
/*     */ 
/* 661 */       for (; count < this.width; 
/* 661 */         index1 += 3)
/*     */       {
/* 657 */         pixels[(index2 + count)] = ((short)((buffer[index1] & 0xFF) * 16 + (buffer[(index1 + 1)] >> 4 & 0xF)));
/* 658 */         count++;
/* 659 */         if (count == this.width) break;
/* 660 */         pixels[(index2 + count)] = ((short)((buffer[(index1 + 1)] & 0xF) * 256 + (buffer[(index1 + 2)] & 0xFF)));
/* 661 */         count++;
/*     */       }
/*     */     }
/* 664 */     return pixels;
/*     */   }
/*     */ 
/*     */   float[] read24bitImage(InputStream in) throws IOException {
/* 668 */     byte[] buffer = new byte[this.width * 3];
/* 669 */     float[] pixels = new float[this.nPixels];
/*     */ 
/* 671 */     DataInputStream dis = new DataInputStream(in);
/* 672 */     for (int y = 0; y < this.height; y++)
/*     */     {
/* 674 */       dis.readFully(buffer);
/* 675 */       int b = 0;
/* 676 */       for (int x = 0; x < this.width; x++) {
/* 677 */         int b1 = buffer[(b++)] & 0xFF;
/* 678 */         int b2 = buffer[(b++)] & 0xFF;
/* 679 */         int b3 = buffer[(b++)] & 0xFF;
/* 680 */         pixels[(x + y * this.width)] = (b3 << 16 | b2 << 8 | b1);
/*     */       }
/*     */     }
/* 683 */     return pixels;
/*     */   }
/*     */ 
/*     */   byte[] read1bitImage(InputStream in) throws IOException {
/* 687 */     if (this.fi.compression == 2)
/* 688 */       throw new IOException("ImageJ cannot open 1-bit LZW compressed TIFFs");
/* 689 */     int scan = (int)Math.ceil(this.width / 8.0D);
/* 690 */     int len = scan * this.height;
/* 691 */     byte[] buffer = new byte[len];
/* 692 */     byte[] pixels = new byte[this.nPixels];
/* 693 */     DataInputStream dis = new DataInputStream(in);
/* 694 */     dis.readFully(buffer);
/*     */ 
/* 696 */     for (int y = 0; y < this.height; y++) {
/* 697 */       int offset = y * scan;
/* 698 */       int index = y * this.width;
/* 699 */       for (int x = 0; x < scan; x++) {
/* 700 */         int value1 = buffer[(offset + x)] & 0xFF;
/* 701 */         for (int i = 7; i >= 0; i--) {
/* 702 */           int value2 = (value1 & 1 << i) != 0 ? 255 : 0;
/* 703 */           if (index < pixels.length)
/* 704 */             pixels[(index++)] = ((byte)value2);
/*     */         }
/*     */       }
/*     */     }
/* 708 */     return pixels;
/*     */   }
/*     */ 
/*     */   void skip(InputStream in) throws IOException {
/* 712 */     if (this.skipCount > 0L) {
/* 713 */       long bytesRead = 0L;
/* 714 */       int skipAttempts = 0;
/*     */ 
/* 716 */       while (bytesRead < this.skipCount) {
/* 717 */         long count = in.skip(this.skipCount - bytesRead);
/* 718 */         skipAttempts++;
/* 719 */         if ((count == -1L) || (skipAttempts > 5)) break;
/* 720 */         bytesRead += count;
/*     */       }
/*     */     }
/*     */ 
/* 724 */     this.byteCount = (this.width * this.height * this.bytesPerPixel);
/* 725 */     if (this.fi.fileType == 8) {
/* 726 */       int scan = this.width / 8; int pad = this.width % 8;
/* 727 */       if (pad > 0) scan++;
/* 728 */       this.byteCount = (scan * this.height);
/*     */     }
/* 730 */     this.nPixels = (this.width * this.height);
/* 731 */     this.bufferSize = ((int)(this.byteCount / 25L));
/* 732 */     if (this.bufferSize < 8192)
/* 733 */       this.bufferSize = 8192;
/*     */     else
/* 735 */       this.bufferSize = (this.bufferSize / 8192 * 8192);
/*     */   }
/*     */ 
/*     */   public Object readPixels(InputStream in)
/*     */   {
/* 745 */     this.startTime = System.currentTimeMillis();
/*     */     try
/*     */     {
/*     */       Object pixels;
/* 747 */       switch (this.fi.fileType) {
/*     */       case 0:
/*     */       case 5:
/* 750 */         this.bytesPerPixel = 1;
/* 751 */         skip(in);
/* 752 */         pixels = read8bitImage(in);
/* 753 */         break;
/*     */       case 1:
/*     */       case 2:
/* 756 */         this.bytesPerPixel = 2;
/* 757 */         skip(in);
/* 758 */         pixels = read16bitImage(in);
/* 759 */         break;
/*     */       case 3:
/*     */       case 4:
/*     */       case 11:
/* 763 */         this.bytesPerPixel = 4;
/* 764 */         skip(in);
/* 765 */         pixels = read32bitImage(in);
/* 766 */         break;
/*     */       case 16:
/* 768 */         this.bytesPerPixel = 8;
/* 769 */         skip(in);
/* 770 */         pixels = read64bitImage(in);
/* 771 */         break;
/*     */       case 6:
/*     */       case 9:
/*     */       case 10:
/*     */       case 15:
/*     */       case 18:
/* 777 */         this.bytesPerPixel = this.fi.getBytesPerPixel();
/* 778 */         skip(in);
/* 779 */         pixels = readChunkyRGB(in);
/* 780 */         break;
/*     */       case 7:
/* 782 */         this.bytesPerPixel = 3;
/* 783 */         skip(in);
/* 784 */         pixels = readPlanarRGB(in);
/* 785 */         break;
/*     */       case 8:
/* 787 */         this.bytesPerPixel = 1;
/* 788 */         skip(in);
/* 789 */         pixels = read1bitImage(in);
/* 790 */         break;
/*     */       case 12:
/* 792 */         this.bytesPerPixel = 6;
/* 793 */         skip(in);
/* 794 */         pixels = readRGB48(in);
/* 795 */         break;
/*     */       case 17:
/* 797 */         this.bytesPerPixel = 2;
/* 798 */         skip(in);
/* 799 */         pixels = readRGB48Planar(in);
/* 800 */         break;
/*     */       case 13:
/* 802 */         skip(in);
/* 803 */         short[] data = read12bitImage(in);
/* 804 */         pixels = data;
/* 805 */         break;
/*     */       case 14:
/* 807 */         skip(in);
/* 808 */         pixels = read24bitImage(in);
/* 809 */         break;
/*     */       default:
/* 811 */         pixels = null;
/*     */       }
/* 813 */       showProgress(1, 1);
/* 814 */       return pixels;
/*     */     }
/*     */     catch (IOException e) {
/* 817 */       IJ.log("" + e);
/* 818 */     }return null;
/*     */   }
/*     */ 
/*     */   public Object readPixels(InputStream in, long skipCount)
/*     */   {
/* 828 */     this.skipCount = skipCount;
/* 829 */     this.showProgressBar = false;
/* 830 */     Object pixels = readPixels(in);
/* 831 */     if (this.eofErrorCount > 0) {
/* 832 */       return null;
/*     */     }
/* 834 */     return pixels;
/*     */   }
/*     */ 
/*     */   public Object readPixels(String url)
/*     */   {
/*     */     URL theURL;
/*     */     try
/*     */     {
/* 844 */       theURL = new URL(url); } catch (MalformedURLException e) {
/* 845 */       IJ.log("" + e); return null; } InputStream is;
/*     */     try { is = theURL.openStream(); } catch (IOException e) {
/* 847 */       IJ.log("" + e); return null;
/* 848 */     }return readPixels(is);
/*     */   }
/*     */ 
/*     */   byte[] uncompress(byte[] input) {
/* 852 */     if (this.fi.compression == 5)
/* 853 */       return packBitsUncompress(input, this.fi.rowsPerStrip * this.fi.width * this.fi.getBytesPerPixel());
/* 854 */     if ((this.fi.compression == 2) || (this.fi.compression == 3))
/* 855 */       return lzwUncompress(input);
/* 856 */     if (this.fi.compression == 6) {
/* 857 */       return zipUncompress(input);
/*     */     }
/* 859 */     return input;
/*     */   }
/*     */ 
/*     */   public byte[] zipUncompress(byte[] input)
/*     */   {
/* 864 */     ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
/* 865 */     byte[] buffer = new byte[1024];
/* 866 */     Inflater decompressor = new Inflater();
/* 867 */     decompressor.setInput(input);
/*     */     try {
/* 869 */       while (!decompressor.finished()) {
/* 870 */         int rlen = decompressor.inflate(buffer);
/* 871 */         imageBuffer.write(buffer, 0, rlen);
/*     */       }
/*     */     } catch (DataFormatException e) {
/* 874 */       IJ.log(e.toString());
/*     */     }
/* 876 */     decompressor.end();
/* 877 */     return imageBuffer.toByteArray();
/*     */   }
/*     */ 
/*     */   public byte[] lzwUncompress(byte[] input)
/*     */   {
/* 887 */     if ((input == null) || (input.length == 0))
/* 888 */       return input;
/* 889 */     byte[][] symbolTable = new byte[4096][1];
/* 890 */     int bitsToRead = 9;
/* 891 */     int nextSymbol = 258;
/*     */ 
/* 893 */     int oldCode = -1;
/* 894 */     ByteVector out = new ByteVector(8192);
/* 895 */     BitBuffer bb = new BitBuffer(input);
/* 896 */     byte[] byteBuffer1 = new byte[16];
/* 897 */     byte[] byteBuffer2 = new byte[16];
/*     */ 
/* 899 */     while (out.size() < this.byteCount) {
/* 900 */       int code = bb.getBits(bitsToRead);
/* 901 */       if ((code == 257) || (code == -1))
/*     */         break;
/* 903 */       if (code == 256)
/*     */       {
/* 905 */         for (int i = 0; i < 256; i++)
/* 906 */           symbolTable[i][0] = ((byte)i);
/* 907 */         nextSymbol = 258;
/* 908 */         bitsToRead = 9;
/* 909 */         code = bb.getBits(bitsToRead);
/* 910 */         if ((code == 257) || (code == -1))
/*     */           break;
/* 912 */         out.add(symbolTable[code]);
/* 913 */         oldCode = code;
/*     */       } else {
/* 915 */         if (code < nextSymbol)
/*     */         {
/* 917 */           out.add(symbolTable[code]);
/*     */ 
/* 919 */           ByteVector symbol = new ByteVector(byteBuffer1);
/* 920 */           symbol.add(symbolTable[oldCode]);
/* 921 */           symbol.add(symbolTable[code][0]);
/* 922 */           symbolTable[nextSymbol] = symbol.toByteArray();
/* 923 */           oldCode = code;
/* 924 */           nextSymbol++;
/*     */         }
/*     */         else {
/* 927 */           ByteVector symbol = new ByteVector(byteBuffer2);
/* 928 */           symbol.add(symbolTable[oldCode]);
/* 929 */           symbol.add(symbolTable[oldCode][0]);
/* 930 */           byte[] outString = symbol.toByteArray();
/* 931 */           out.add(outString);
/* 932 */           symbolTable[nextSymbol] = outString;
/* 933 */           oldCode = code;
/* 934 */           nextSymbol++;
/*     */         }
/* 936 */         if (nextSymbol == 511) bitsToRead = 10;
/* 937 */         if (nextSymbol == 1023) bitsToRead = 11;
/* 938 */         if (nextSymbol == 2047) bitsToRead = 12;
/*     */       }
/*     */     }
/* 941 */     return out.toByteArray();
/*     */   }
/*     */ 
/*     */   public byte[] packBitsUncompress(byte[] input, int expected)
/*     */   {
/* 946 */     if (expected == 0) expected = 2147483647;
/* 947 */     ByteVector output = new ByteVector(1024);
/* 948 */     int index = 0;
/* 949 */     while ((output.size() < expected) && (index < input.length)) {
/* 950 */       byte n = input[(index++)];
/* 951 */       if (n >= 0) {
/* 952 */         byte[] b = new byte[n + 1];
/* 953 */         for (int i = 0; i < n + 1; i++)
/* 954 */           b[i] = input[(index++)];
/* 955 */         output.add(b);
/* 956 */         b = null;
/* 957 */       } else if (n != -128) {
/* 958 */         int len = -n + 1;
/* 959 */         byte inp = input[(index++)];
/* 960 */         for (int i = 0; i < len; i++) output.add(inp);
/*     */       }
/*     */     }
/* 963 */     return output.toByteArray();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.ImageReader
 * JD-Core Version:    0.6.2
 */