/*     */ package ij.io;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.VirtualStack;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class ImageWriter
/*     */ {
/*     */   private FileInfo fi;
/*   9 */   private boolean showProgressBar = true;
/*     */ 
/*     */   public ImageWriter(FileInfo fi) {
/*  12 */     this.fi = fi;
/*     */   }
/*     */ 
/*     */   private void showProgress(double progress) {
/*  16 */     if (this.showProgressBar)
/*  17 */       IJ.showProgress(progress);
/*     */   }
/*     */ 
/*     */   void write8BitImage(OutputStream out, byte[] pixels) throws IOException {
/*  21 */     int bytesWritten = 0;
/*  22 */     int size = this.fi.width * this.fi.height;
/*  23 */     int count = 8192;
/*     */ 
/*  25 */     while (bytesWritten < size) {
/*  26 */       if (bytesWritten + count > size) {
/*  27 */         count = size - bytesWritten;
/*     */       }
/*  29 */       out.write(pixels, bytesWritten, count);
/*  30 */       bytesWritten += count;
/*  31 */       showProgress(bytesWritten / size);
/*     */     }
/*     */   }
/*     */ 
/*     */   void write8BitStack(OutputStream out, Object[] stack) throws IOException {
/*  36 */     this.showProgressBar = false;
/*  37 */     for (int i = 0; i < this.fi.nImages; i++) {
/*  38 */       IJ.showStatus("Writing: " + (i + 1) + "/" + this.fi.nImages);
/*  39 */       write8BitImage(out, (byte[])stack[i]);
/*  40 */       IJ.showProgress((i + 1) / this.fi.nImages);
/*     */     }
/*     */   }
/*     */ 
/*     */   void write8BitVirtualStack(OutputStream out, VirtualStack virtualStack) throws IOException {
/*  45 */     this.showProgressBar = false;
/*  46 */     boolean flip = "FlipTheseImages".equals(this.fi.fileName);
/*  47 */     for (int i = 1; i <= this.fi.nImages; i++) {
/*  48 */       IJ.showStatus("Writing: " + i + "/" + this.fi.nImages);
/*  49 */       ImageProcessor ip = virtualStack.getProcessor(i);
/*  50 */       if (flip) ip.flipVertical();
/*  51 */       byte[] pixels = (byte[])ip.getPixels();
/*  52 */       write8BitImage(out, pixels);
/*  53 */       IJ.showProgress(i / this.fi.nImages);
/*     */     }
/*     */   }
/*     */ 
/*     */   void write16BitImage(OutputStream out, short[] pixels) throws IOException {
/*  58 */     long bytesWritten = 0L;
/*  59 */     long size = 2L * this.fi.width * this.fi.height;
/*  60 */     int count = 8192;
/*  61 */     byte[] buffer = new byte[count];
/*     */ 
/*  63 */     while (bytesWritten < size) {
/*  64 */       if (bytesWritten + count > size)
/*  65 */         count = (int)(size - bytesWritten);
/*  66 */       int j = (int)(bytesWritten / 2L);
/*     */ 
/*  68 */       if (this.fi.intelByteOrder)
/*  69 */         for (int i = 0; i < count; i += 2) {
/*  70 */           int value = pixels[j];
/*  71 */           buffer[i] = ((byte)value);
/*  72 */           buffer[(i + 1)] = ((byte)(value >>> 8));
/*  73 */           j++;
/*     */         }
/*     */       else
/*  76 */         for (int i = 0; i < count; i += 2) {
/*  77 */           int value = pixels[j];
/*  78 */           buffer[i] = ((byte)(value >>> 8));
/*  79 */           buffer[(i + 1)] = ((byte)value);
/*  80 */           j++;
/*     */         }
/*  82 */       out.write(buffer, 0, count);
/*  83 */       bytesWritten += count;
/*  84 */       showProgress(bytesWritten / size);
/*     */     }
/*     */   }
/*     */ 
/*     */   void write16BitStack(OutputStream out, Object[] stack) throws IOException {
/*  89 */     this.showProgressBar = false;
/*  90 */     for (int i = 0; i < this.fi.nImages; i++) {
/*  91 */       IJ.showStatus("Writing: " + (i + 1) + "/" + this.fi.nImages);
/*  92 */       write16BitImage(out, (short[])stack[i]);
/*  93 */       IJ.showProgress((i + 1) / this.fi.nImages);
/*     */     }
/*     */   }
/*     */ 
/*     */   void write16BitVirtualStack(OutputStream out, VirtualStack virtualStack) throws IOException {
/*  98 */     this.showProgressBar = false;
/*  99 */     boolean flip = "FlipTheseImages".equals(this.fi.fileName);
/* 100 */     for (int i = 1; i <= this.fi.nImages; i++) {
/* 101 */       IJ.showStatus("Writing: " + i + "/" + this.fi.nImages);
/* 102 */       ImageProcessor ip = virtualStack.getProcessor(i);
/* 103 */       if (flip) ip.flipVertical();
/* 104 */       short[] pixels = (short[])ip.getPixels();
/* 105 */       write16BitImage(out, pixels);
/* 106 */       IJ.showProgress(i / this.fi.nImages);
/*     */     }
/*     */   }
/*     */ 
/*     */   void writeRGB48Image(OutputStream out, Object[] stack) throws IOException {
/* 111 */     short[] r = (short[])stack[0];
/* 112 */     short[] g = (short[])stack[1];
/* 113 */     short[] b = (short[])stack[2];
/* 114 */     int size = this.fi.width * this.fi.height;
/* 115 */     int count = this.fi.width * 6;
/* 116 */     byte[] buffer = new byte[count];
/* 117 */     for (int line = 0; line < this.fi.height; line++) {
/* 118 */       int index2 = 0;
/* 119 */       int index1 = line * this.fi.width;
/*     */ 
/* 121 */       if (this.fi.intelByteOrder)
/* 122 */         for (int i = 0; i < this.fi.width; i++) {
/* 123 */           int value = r[index1];
/* 124 */           buffer[(index2++)] = ((byte)value);
/* 125 */           buffer[(index2++)] = ((byte)(value >>> 8));
/* 126 */           value = g[index1];
/* 127 */           buffer[(index2++)] = ((byte)value);
/* 128 */           buffer[(index2++)] = ((byte)(value >>> 8));
/* 129 */           value = b[index1];
/* 130 */           buffer[(index2++)] = ((byte)value);
/* 131 */           buffer[(index2++)] = ((byte)(value >>> 8));
/* 132 */           index1++;
/*     */         }
/*     */       else {
/* 135 */         for (int i = 0; i < this.fi.width; i++) {
/* 136 */           int value = r[index1];
/* 137 */           buffer[(index2++)] = ((byte)(value >>> 8));
/* 138 */           buffer[(index2++)] = ((byte)value);
/* 139 */           value = g[index1];
/* 140 */           buffer[(index2++)] = ((byte)(value >>> 8));
/* 141 */           buffer[(index2++)] = ((byte)value);
/* 142 */           value = b[index1];
/* 143 */           buffer[(index2++)] = ((byte)(value >>> 8));
/* 144 */           buffer[(index2++)] = ((byte)value);
/* 145 */           index1++;
/*     */         }
/*     */       }
/* 148 */       out.write(buffer, 0, count);
/*     */     }
/*     */   }
/*     */ 
/*     */   void writeFloatImage(OutputStream out, float[] pixels) throws IOException {
/* 153 */     long bytesWritten = 0L;
/* 154 */     long size = 4L * this.fi.width * this.fi.height;
/* 155 */     int count = 8192;
/* 156 */     byte[] buffer = new byte[count];
/*     */ 
/* 159 */     while (bytesWritten < size) {
/* 160 */       if (bytesWritten + count > size)
/* 161 */         count = (int)(size - bytesWritten);
/* 162 */       int j = (int)(bytesWritten / 4L);
/* 163 */       if (this.fi.intelByteOrder)
/* 164 */         for (int i = 0; i < count; i += 4) {
/* 165 */           int tmp = Float.floatToRawIntBits(pixels[j]);
/* 166 */           buffer[i] = ((byte)tmp);
/* 167 */           buffer[(i + 1)] = ((byte)(tmp >> 8));
/* 168 */           buffer[(i + 2)] = ((byte)(tmp >> 16));
/* 169 */           buffer[(i + 3)] = ((byte)(tmp >> 24));
/* 170 */           j++;
/*     */         }
/*     */       else
/* 173 */         for (int i = 0; i < count; i += 4) {
/* 174 */           int tmp = Float.floatToRawIntBits(pixels[j]);
/* 175 */           buffer[i] = ((byte)(tmp >> 24));
/* 176 */           buffer[(i + 1)] = ((byte)(tmp >> 16));
/* 177 */           buffer[(i + 2)] = ((byte)(tmp >> 8));
/* 178 */           buffer[(i + 3)] = ((byte)tmp);
/* 179 */           j++;
/*     */         }
/* 181 */       out.write(buffer, 0, count);
/* 182 */       bytesWritten += count;
/* 183 */       showProgress(bytesWritten / size);
/*     */     }
/*     */   }
/*     */ 
/*     */   void writeFloatStack(OutputStream out, Object[] stack) throws IOException {
/* 188 */     this.showProgressBar = false;
/* 189 */     for (int i = 0; i < this.fi.nImages; i++) {
/* 190 */       IJ.showStatus("Writing: " + (i + 1) + "/" + this.fi.nImages);
/* 191 */       writeFloatImage(out, (float[])stack[i]);
/* 192 */       IJ.showProgress((i + 1) / this.fi.nImages);
/*     */     }
/*     */   }
/*     */ 
/*     */   void writeFloatVirtualStack(OutputStream out, VirtualStack virtualStack) throws IOException {
/* 197 */     this.showProgressBar = false;
/* 198 */     boolean flip = "FlipTheseImages".equals(this.fi.fileName);
/* 199 */     for (int i = 1; i <= this.fi.nImages; i++) {
/* 200 */       IJ.showStatus("Writing: " + i + "/" + this.fi.nImages);
/* 201 */       ImageProcessor ip = virtualStack.getProcessor(i);
/* 202 */       if (flip) ip.flipVertical();
/* 203 */       float[] pixels = (float[])ip.getPixels();
/* 204 */       writeFloatImage(out, pixels);
/* 205 */       IJ.showProgress(i / this.fi.nImages);
/*     */     }
/*     */   }
/*     */ 
/*     */   void writeRGBImage(OutputStream out, int[] pixels) throws IOException {
/* 210 */     long bytesWritten = 0L;
/* 211 */     long size = 3L * this.fi.width * this.fi.height;
/* 212 */     int count = this.fi.width * 24;
/* 213 */     byte[] buffer = new byte[count];
/* 214 */     while (bytesWritten < size) {
/* 215 */       if (bytesWritten + count > size)
/* 216 */         count = (int)(size - bytesWritten);
/* 217 */       int j = (int)(bytesWritten / 3L);
/* 218 */       for (int i = 0; i < count; i += 3) {
/* 219 */         buffer[i] = ((byte)(pixels[j] >> 16));
/* 220 */         buffer[(i + 1)] = ((byte)(pixels[j] >> 8));
/* 221 */         buffer[(i + 2)] = ((byte)pixels[j]);
/* 222 */         j++;
/*     */       }
/* 224 */       out.write(buffer, 0, count);
/* 225 */       bytesWritten += count;
/* 226 */       showProgress(bytesWritten / size);
/*     */     }
/*     */   }
/*     */ 
/*     */   void writeRGBStack(OutputStream out, Object[] stack) throws IOException {
/* 231 */     this.showProgressBar = false;
/* 232 */     for (int i = 0; i < this.fi.nImages; i++) {
/* 233 */       IJ.showStatus("Writing: " + (i + 1) + "/" + this.fi.nImages);
/* 234 */       writeRGBImage(out, (int[])stack[i]);
/* 235 */       IJ.showProgress((i + 1) / this.fi.nImages);
/*     */     }
/*     */   }
/*     */ 
/*     */   void writeRGBVirtualStack(OutputStream out, VirtualStack virtualStack) throws IOException {
/* 240 */     this.showProgressBar = false;
/* 241 */     boolean flip = "FlipTheseImages".equals(this.fi.fileName);
/* 242 */     for (int i = 1; i <= this.fi.nImages; i++) {
/* 243 */       IJ.showStatus("Writing: " + i + "/" + this.fi.nImages);
/* 244 */       ImageProcessor ip = virtualStack.getProcessor(i);
/* 245 */       if (flip) ip.flipVertical();
/* 246 */       int[] pixels = (int[])ip.getPixels();
/* 247 */       writeRGBImage(out, pixels);
/* 248 */       IJ.showProgress(i / this.fi.nImages);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void write(OutputStream out)
/*     */     throws IOException
/*     */   {
/* 259 */     if ((this.fi.pixels == null) && (this.fi.virtualStack == null))
/* 260 */       throw new IOException("ImageWriter: fi.pixels==null");
/* 261 */     if ((this.fi.nImages > 1) && (this.fi.virtualStack == null) && (!(this.fi.pixels instanceof Object[])))
/* 262 */       throw new IOException("ImageWriter: fi.pixels not a stack");
/* 263 */     switch (this.fi.fileType) {
/*     */     case 0:
/*     */     case 5:
/* 266 */       if ((this.fi.nImages > 1) && (this.fi.virtualStack != null))
/* 267 */         write8BitVirtualStack(out, this.fi.virtualStack);
/* 268 */       else if (this.fi.nImages > 1)
/* 269 */         write8BitStack(out, (Object[])this.fi.pixels);
/*     */       else
/* 271 */         write8BitImage(out, (byte[])this.fi.pixels);
/* 272 */       break;
/*     */     case 1:
/*     */     case 2:
/* 275 */       if ((this.fi.nImages > 1) && (this.fi.virtualStack != null))
/* 276 */         write16BitVirtualStack(out, this.fi.virtualStack);
/* 277 */       else if (this.fi.nImages > 1)
/* 278 */         write16BitStack(out, (Object[])this.fi.pixels);
/*     */       else
/* 280 */         write16BitImage(out, (short[])this.fi.pixels);
/* 281 */       break;
/*     */     case 12:
/* 283 */       writeRGB48Image(out, (Object[])this.fi.pixels);
/* 284 */       break;
/*     */     case 4:
/* 286 */       if ((this.fi.nImages > 1) && (this.fi.virtualStack != null))
/* 287 */         writeFloatVirtualStack(out, this.fi.virtualStack);
/* 288 */       else if (this.fi.nImages > 1)
/* 289 */         writeFloatStack(out, (Object[])this.fi.pixels);
/*     */       else
/* 291 */         writeFloatImage(out, (float[])this.fi.pixels);
/* 292 */       break;
/*     */     case 6:
/* 294 */       if ((this.fi.nImages > 1) && (this.fi.virtualStack != null))
/* 295 */         writeRGBVirtualStack(out, this.fi.virtualStack);
/* 296 */       else if (this.fi.nImages > 1)
/* 297 */         writeRGBStack(out, (Object[])this.fi.pixels);
/*     */       else
/* 299 */         writeRGBImage(out, (int[])this.fi.pixels);
/* 300 */       break;
/*     */     case 3:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.ImageWriter
 * JD-Core Version:    0.6.2
 */