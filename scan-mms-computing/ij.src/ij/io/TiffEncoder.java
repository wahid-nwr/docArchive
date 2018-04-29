/*     */ package ij.io;
/*     */ 
/*     */ import ij.Prefs;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class TiffEncoder
/*     */ {
/*     */   static final int HDR_SIZE = 8;
/*     */   static final int MAP_SIZE = 768;
/*     */   static final int BPS_DATA_SIZE = 6;
/*     */   static final int SCALE_DATA_SIZE = 16;
/*     */   private FileInfo fi;
/*     */   private int bitsPerSample;
/*     */   private int photoInterp;
/*     */   private int samplesPerPixel;
/*     */   private int nEntries;
/*     */   private int ifdSize;
/*     */   private long imageOffset;
/*     */   private int imageSize;
/*     */   private long stackSize;
/*     */   private byte[] description;
/*     */   private int metaDataSize;
/*     */   private int nMetaDataTypes;
/*     */   private int nMetaDataEntries;
/*     */   private int nSliceLabels;
/*     */   private int extraMetaDataEntries;
/*     */   private int scaleSize;
/*  27 */   private boolean littleEndian = Prefs.intelByteOrder;
/*  28 */   private byte[] buffer = new byte[8];
/*     */ 
/*     */   public TiffEncoder(FileInfo fi) {
/*  31 */     this.fi = fi;
/*  32 */     fi.intelByteOrder = this.littleEndian;
/*  33 */     this.bitsPerSample = 8;
/*  34 */     this.samplesPerPixel = 1;
/*  35 */     this.nEntries = 9;
/*  36 */     int bytesPerPixel = 1;
/*  37 */     int bpsSize = 0;
/*  38 */     int colorMapSize = 0;
/*     */ 
/*  40 */     switch (fi.fileType) {
/*     */     case 0:
/*  42 */       this.photoInterp = (fi.whiteIsZero ? 0 : 1);
/*  43 */       break;
/*     */     case 1:
/*     */     case 2:
/*  46 */       this.bitsPerSample = 16;
/*  47 */       this.photoInterp = (fi.whiteIsZero ? 0 : 1);
/*  48 */       bytesPerPixel = 2;
/*  49 */       break;
/*     */     case 4:
/*  51 */       this.bitsPerSample = 32;
/*  52 */       this.photoInterp = (fi.whiteIsZero ? 0 : 1);
/*  53 */       bytesPerPixel = 4;
/*  54 */       break;
/*     */     case 6:
/*  56 */       this.photoInterp = 2;
/*  57 */       this.samplesPerPixel = 3;
/*  58 */       bytesPerPixel = 3;
/*  59 */       bpsSize = 6;
/*  60 */       break;
/*     */     case 12:
/*  62 */       this.bitsPerSample = 16;
/*  63 */       this.photoInterp = 2;
/*  64 */       this.samplesPerPixel = 3;
/*  65 */       bytesPerPixel = 6;
/*  66 */       fi.nImages /= 3;
/*  67 */       bpsSize = 6;
/*  68 */       break;
/*     */     case 5:
/*  70 */       this.photoInterp = 3;
/*  71 */       this.nEntries = 10;
/*  72 */       colorMapSize = 1536;
/*  73 */       break;
/*     */     case 3:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/*     */     default:
/*  75 */       this.photoInterp = 0;
/*     */     }
/*  77 */     if ((fi.unit != null) && (fi.pixelWidth != 0.0D) && (fi.pixelHeight != 0.0D))
/*  78 */       this.nEntries += 3;
/*  79 */     if (fi.fileType == 4)
/*  80 */       this.nEntries += 1;
/*  81 */     makeDescriptionString();
/*  82 */     if (this.description != null)
/*  83 */       this.nEntries += 1;
/*  84 */     long size = fi.width * fi.height * bytesPerPixel;
/*  85 */     this.imageSize = (size <= 4294967295L ? (int)size : 0);
/*  86 */     this.stackSize = (this.imageSize * fi.nImages);
/*  87 */     this.metaDataSize = getMetaDataSize();
/*  88 */     if (this.metaDataSize > 0)
/*  89 */       this.nEntries += 2;
/*  90 */     this.ifdSize = (2 + this.nEntries * 12 + 4);
/*  91 */     int descriptionSize = this.description != null ? this.description.length : 0;
/*  92 */     this.scaleSize = ((fi.unit != null) && (fi.pixelWidth != 0.0D) && (fi.pixelHeight != 0.0D) ? 16 : 0);
/*  93 */     this.imageOffset = (8 + this.ifdSize + bpsSize + descriptionSize + this.scaleSize + colorMapSize + this.nMetaDataEntries * 4 + this.metaDataSize);
/*  94 */     fi.offset = ((int)this.imageOffset);
/*     */   }
/*     */ 
/*     */   public void write(OutputStream out)
/*     */     throws IOException
/*     */   {
/* 102 */     writeHeader(out);
/* 103 */     long nextIFD = 0L;
/* 104 */     if (this.fi.nImages > 1)
/* 105 */       nextIFD = this.imageOffset + this.stackSize;
/* 106 */     if (nextIFD + this.fi.nImages * this.ifdSize >= 4294967295L)
/* 107 */       nextIFD = 0L;
/* 108 */     writeIFD(out, (int)this.imageOffset, (int)nextIFD);
/* 109 */     if ((this.fi.fileType == 6) || (this.fi.fileType == 12))
/* 110 */       writeBitsPerPixel(out);
/* 111 */     if (this.description != null)
/* 112 */       writeDescription(out);
/* 113 */     if (this.scaleSize > 0)
/* 114 */       writeScale(out);
/* 115 */     if (this.fi.fileType == 5)
/* 116 */       writeColorMap(out);
/* 117 */     if (this.metaDataSize > 0)
/* 118 */       writeMetaData(out);
/* 119 */     new ImageWriter(this.fi).write(out);
/* 120 */     if (nextIFD > 0L) {
/* 121 */       int ifdSize2 = this.ifdSize;
/* 122 */       if (this.metaDataSize > 0) {
/* 123 */         this.metaDataSize = 0;
/* 124 */         this.nEntries -= 2;
/* 125 */         ifdSize2 -= 24;
/*     */       }
/* 127 */       for (int i = 2; i <= this.fi.nImages; i++) {
/* 128 */         if (i == this.fi.nImages)
/* 129 */           nextIFD = 0L;
/*     */         else
/* 131 */           nextIFD += ifdSize2;
/* 132 */         this.imageOffset += this.imageSize;
/* 133 */         writeIFD(out, (int)this.imageOffset, (int)nextIFD);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void write(DataOutputStream out) throws IOException {
/* 139 */     write(out);
/*     */   }
/*     */ 
/*     */   int getMetaDataSize()
/*     */   {
/* 144 */     this.nSliceLabels = 0;
/* 145 */     this.nMetaDataEntries = 0;
/* 146 */     int size = 0;
/* 147 */     int nTypes = 0;
/* 148 */     if ((this.fi.info != null) && (this.fi.info.length() > 0)) {
/* 149 */       this.nMetaDataEntries = 1;
/* 150 */       size = this.fi.info.length() * 2;
/* 151 */       nTypes++;
/*     */     }
/* 153 */     if (this.fi.sliceLabels != null) {
/* 154 */       int max = Math.min(this.fi.sliceLabels.length, this.fi.nImages);
/* 155 */       boolean isNonNullLabel = false;
/* 156 */       for (int i = 0; i < max; i++) {
/* 157 */         if ((this.fi.sliceLabels[i] != null) && (this.fi.sliceLabels[i].length() > 0)) {
/* 158 */           isNonNullLabel = true;
/* 159 */           break;
/*     */         }
/*     */       }
/* 162 */       if (isNonNullLabel) {
/* 163 */         for (int i = 0; i < max; i++) {
/* 164 */           this.nSliceLabels += 1;
/* 165 */           if (this.fi.sliceLabels[i] != null)
/* 166 */             size += this.fi.sliceLabels[i].length() * 2;
/*     */         }
/* 168 */         if (this.nSliceLabels > 0) nTypes++;
/* 169 */         this.nMetaDataEntries += this.nSliceLabels;
/*     */       }
/*     */     }
/*     */ 
/* 173 */     if (this.fi.displayRanges != null) {
/* 174 */       this.nMetaDataEntries += 1;
/* 175 */       size += this.fi.displayRanges.length * 8;
/* 176 */       nTypes++;
/*     */     }
/*     */ 
/* 179 */     if (this.fi.channelLuts != null) {
/* 180 */       for (int i = 0; i < this.fi.channelLuts.length; i++) {
/* 181 */         if (this.fi.channelLuts[i] != null)
/* 182 */           size += this.fi.channelLuts[i].length;
/*     */       }
/* 184 */       nTypes++;
/* 185 */       this.nMetaDataEntries += this.fi.channelLuts.length;
/*     */     }
/*     */ 
/* 188 */     if (this.fi.roi != null) {
/* 189 */       this.nMetaDataEntries += 1;
/* 190 */       size += this.fi.roi.length;
/* 191 */       nTypes++;
/*     */     }
/*     */ 
/* 194 */     if (this.fi.overlay != null) {
/* 195 */       for (int i = 0; i < this.fi.overlay.length; i++) {
/* 196 */         if (this.fi.overlay[i] != null)
/* 197 */           size += this.fi.overlay[i].length;
/*     */       }
/* 199 */       nTypes++;
/* 200 */       this.nMetaDataEntries += this.fi.overlay.length;
/*     */     }
/*     */ 
/* 203 */     if ((this.fi.metaDataTypes != null) && (this.fi.metaData != null) && (this.fi.metaData[0] != null) && (this.fi.metaDataTypes.length == this.fi.metaData.length))
/*     */     {
/* 205 */       this.extraMetaDataEntries = this.fi.metaData.length;
/* 206 */       nTypes += this.extraMetaDataEntries;
/* 207 */       this.nMetaDataEntries += this.extraMetaDataEntries;
/* 208 */       for (int i = 0; i < this.extraMetaDataEntries; i++) {
/* 209 */         if (this.fi.metaData[i] != null)
/* 210 */           size += this.fi.metaData[i].length;
/*     */       }
/*     */     }
/* 213 */     if (this.nMetaDataEntries > 0) this.nMetaDataEntries += 1;
/* 214 */     int hdrSize = 4 + nTypes * 8;
/* 215 */     if (size > 0) size += hdrSize;
/* 216 */     this.nMetaDataTypes = nTypes;
/* 217 */     return size;
/*     */   }
/*     */ 
/*     */   void writeHeader(OutputStream out) throws IOException
/*     */   {
/* 222 */     byte[] hdr = new byte[8];
/* 223 */     if (this.littleEndian) {
/* 224 */       hdr[0] = 73;
/* 225 */       hdr[1] = 73;
/* 226 */       hdr[2] = 42;
/* 227 */       hdr[3] = 0;
/* 228 */       hdr[4] = 8;
/* 229 */       hdr[5] = 0;
/* 230 */       hdr[6] = 0;
/* 231 */       hdr[7] = 0;
/*     */     } else {
/* 233 */       hdr[0] = 77;
/* 234 */       hdr[1] = 77;
/* 235 */       hdr[2] = 0;
/* 236 */       hdr[3] = 42;
/* 237 */       hdr[4] = 0;
/* 238 */       hdr[5] = 0;
/* 239 */       hdr[6] = 0;
/* 240 */       hdr[7] = 8;
/*     */     }
/* 242 */     out.write(hdr);
/*     */   }
/*     */ 
/*     */   void writeEntry(OutputStream out, int tag, int fieldType, int count, int value) throws IOException
/*     */   {
/* 247 */     writeShort(out, tag);
/* 248 */     writeShort(out, fieldType);
/* 249 */     writeInt(out, count);
/* 250 */     if ((count == 1) && (fieldType == 3)) {
/* 251 */       writeShort(out, value);
/* 252 */       writeShort(out, 0);
/*     */     } else {
/* 254 */       writeInt(out, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   void writeIFD(OutputStream out, int imageOffset, int nextIFD) throws IOException {
/* 259 */     int tagDataOffset = 8 + this.ifdSize;
/* 260 */     writeShort(out, this.nEntries);
/* 261 */     writeEntry(out, 254, 4, 1, 0);
/* 262 */     writeEntry(out, 256, 4, 1, this.fi.width);
/* 263 */     writeEntry(out, 257, 4, 1, this.fi.height);
/* 264 */     if ((this.fi.fileType == 6) || (this.fi.fileType == 12)) {
/* 265 */       writeEntry(out, 258, 3, 3, tagDataOffset);
/* 266 */       tagDataOffset += 6;
/*     */     } else {
/* 268 */       writeEntry(out, 258, 3, 1, this.bitsPerSample);
/* 269 */     }writeEntry(out, 262, 3, 1, this.photoInterp);
/* 270 */     if (this.description != null) {
/* 271 */       writeEntry(out, 270, 2, this.description.length, tagDataOffset);
/* 272 */       tagDataOffset += this.description.length;
/*     */     }
/* 274 */     writeEntry(out, 273, 4, 1, imageOffset);
/* 275 */     writeEntry(out, 277, 3, 1, this.samplesPerPixel);
/* 276 */     writeEntry(out, 278, 3, 1, this.fi.height);
/* 277 */     writeEntry(out, 279, 4, 1, this.imageSize);
/* 278 */     if ((this.fi.unit != null) && (this.fi.pixelWidth != 0.0D) && (this.fi.pixelHeight != 0.0D)) {
/* 279 */       writeEntry(out, 282, 5, 1, tagDataOffset);
/* 280 */       writeEntry(out, 283, 5, 1, tagDataOffset + 8);
/* 281 */       tagDataOffset += 16;
/* 282 */       int unit = 1;
/* 283 */       if (this.fi.unit.equals("inch"))
/* 284 */         unit = 2;
/* 285 */       else if (this.fi.unit.equals("cm"))
/* 286 */         unit = 3;
/* 287 */       writeEntry(out, 296, 3, 1, unit);
/*     */     }
/* 289 */     if (this.fi.fileType == 4) {
/* 290 */       int format = 3;
/* 291 */       writeEntry(out, 339, 3, 1, format);
/*     */     }
/* 293 */     if (this.fi.fileType == 5) {
/* 294 */       writeEntry(out, 320, 3, 768, tagDataOffset);
/* 295 */       tagDataOffset += 1536;
/*     */     }
/* 297 */     if (this.metaDataSize > 0) {
/* 298 */       writeEntry(out, 50838, 4, this.nMetaDataEntries, tagDataOffset);
/* 299 */       writeEntry(out, 50839, 1, this.metaDataSize, tagDataOffset + 4 * this.nMetaDataEntries);
/* 300 */       tagDataOffset += this.nMetaDataEntries * 4 + this.metaDataSize;
/*     */     }
/* 302 */     writeInt(out, nextIFD);
/*     */   }
/*     */ 
/*     */   void writeBitsPerPixel(OutputStream out) throws IOException
/*     */   {
/* 307 */     int bitsPerPixel = this.fi.fileType == 12 ? 16 : 8;
/* 308 */     writeShort(out, bitsPerPixel);
/* 309 */     writeShort(out, bitsPerPixel);
/* 310 */     writeShort(out, bitsPerPixel);
/*     */   }
/*     */ 
/*     */   void writeScale(OutputStream out) throws IOException
/*     */   {
/* 315 */     double xscale = 1.0D / this.fi.pixelWidth;
/* 316 */     double yscale = 1.0D / this.fi.pixelHeight;
/* 317 */     double scale = 1000000.0D;
/* 318 */     if (xscale > 1000.0D) scale = 1000.0D;
/* 319 */     writeInt(out, (int)(xscale * scale));
/* 320 */     writeInt(out, (int)scale);
/* 321 */     writeInt(out, (int)(yscale * scale));
/* 322 */     writeInt(out, (int)scale);
/*     */   }
/*     */ 
/*     */   void writeDescription(OutputStream out) throws IOException
/*     */   {
/* 327 */     out.write(this.description, 0, this.description.length);
/*     */   }
/*     */ 
/*     */   void writeColorMap(OutputStream out) throws IOException
/*     */   {
/* 332 */     byte[] colorTable16 = new byte[1536];
/* 333 */     int j = this.littleEndian ? 1 : 0;
/* 334 */     for (int i = 0; i < this.fi.lutSize; i++) {
/* 335 */       colorTable16[j] = this.fi.reds[i];
/* 336 */       colorTable16[(512 + j)] = this.fi.greens[i];
/* 337 */       colorTable16[(1024 + j)] = this.fi.blues[i];
/* 338 */       j += 2;
/*     */     }
/* 340 */     out.write(colorTable16);
/*     */   }
/*     */ 
/*     */   void writeMetaData(OutputStream out)
/*     */     throws IOException
/*     */   {
/* 349 */     writeInt(out, 4 + this.nMetaDataTypes * 8);
/* 350 */     if ((this.fi.info != null) && (this.fi.info.length() > 0))
/* 351 */       writeInt(out, this.fi.info.length() * 2);
/* 352 */     for (int i = 0; i < this.nSliceLabels; i++) {
/* 353 */       if (this.fi.sliceLabels[i] == null)
/* 354 */         writeInt(out, 0);
/*     */       else
/* 356 */         writeInt(out, this.fi.sliceLabels[i].length() * 2);
/*     */     }
/* 358 */     if (this.fi.displayRanges != null)
/* 359 */       writeInt(out, this.fi.displayRanges.length * 8);
/* 360 */     if (this.fi.channelLuts != null) {
/* 361 */       for (int i = 0; i < this.fi.channelLuts.length; i++)
/* 362 */         writeInt(out, this.fi.channelLuts[i].length);
/*     */     }
/* 364 */     if (this.fi.roi != null)
/* 365 */       writeInt(out, this.fi.roi.length);
/* 366 */     if (this.fi.overlay != null) {
/* 367 */       for (int i = 0; i < this.fi.overlay.length; i++)
/* 368 */         writeInt(out, this.fi.overlay[i].length);
/*     */     }
/* 370 */     for (int i = 0; i < this.extraMetaDataEntries; i++) {
/* 371 */       writeInt(out, this.fi.metaData[i].length);
/*     */     }
/*     */ 
/* 374 */     writeInt(out, 1229605194);
/* 375 */     if (this.fi.info != null) {
/* 376 */       writeInt(out, 1768842863);
/* 377 */       writeInt(out, 1);
/*     */     }
/* 379 */     if (this.nSliceLabels > 0) {
/* 380 */       writeInt(out, 1818321516);
/* 381 */       writeInt(out, this.nSliceLabels);
/*     */     }
/* 383 */     if (this.fi.displayRanges != null) {
/* 384 */       writeInt(out, 1918987879);
/* 385 */       writeInt(out, 1);
/*     */     }
/* 387 */     if (this.fi.channelLuts != null) {
/* 388 */       writeInt(out, 1819636851);
/* 389 */       writeInt(out, this.fi.channelLuts.length);
/*     */     }
/* 391 */     if (this.fi.roi != null) {
/* 392 */       writeInt(out, 1919904032);
/* 393 */       writeInt(out, 1);
/*     */     }
/* 395 */     if (this.fi.overlay != null) {
/* 396 */       writeInt(out, 1870030194);
/* 397 */       writeInt(out, this.fi.overlay.length);
/*     */     }
/* 399 */     for (int i = 0; i < this.extraMetaDataEntries; i++) {
/* 400 */       writeInt(out, this.fi.metaDataTypes[i]);
/* 401 */       writeInt(out, 1);
/*     */     }
/*     */ 
/* 405 */     if (this.fi.info != null)
/* 406 */       writeChars(out, this.fi.info);
/* 407 */     for (int i = 0; i < this.nSliceLabels; i++) {
/* 408 */       if (this.fi.sliceLabels[i] != null)
/* 409 */         writeChars(out, this.fi.sliceLabels[i]);
/*     */     }
/* 411 */     if (this.fi.displayRanges != null) {
/* 412 */       for (int i = 0; i < this.fi.displayRanges.length; i++)
/* 413 */         writeDouble(out, this.fi.displayRanges[i]);
/*     */     }
/* 415 */     if (this.fi.channelLuts != null) {
/* 416 */       for (int i = 0; i < this.fi.channelLuts.length; i++)
/* 417 */         out.write(this.fi.channelLuts[i]);
/*     */     }
/* 419 */     if (this.fi.roi != null)
/* 420 */       out.write(this.fi.roi);
/* 421 */     if (this.fi.overlay != null) {
/* 422 */       for (int i = 0; i < this.fi.overlay.length; i++)
/* 423 */         out.write(this.fi.overlay[i]);
/*     */     }
/* 425 */     for (int i = 0; i < this.extraMetaDataEntries; i++)
/* 426 */       out.write(this.fi.metaData[i]);
/*     */   }
/*     */ 
/*     */   void makeDescriptionString()
/*     */   {
/* 434 */     if (this.fi.description != null) {
/* 435 */       if (this.fi.description.charAt(this.fi.description.length() - 1) != 0)
/* 436 */         this.fi.description += " ";
/* 437 */       this.description = this.fi.description.getBytes();
/* 438 */       this.description[(this.description.length - 1)] = 0;
/*     */     } else {
/* 440 */       this.description = null;
/*     */     }
/*     */   }
/*     */ 
/* 444 */   final void writeShort(OutputStream out, int v) throws IOException { if (this.littleEndian) {
/* 445 */       out.write(v & 0xFF);
/* 446 */       out.write(v >>> 8 & 0xFF);
/*     */     } else {
/* 448 */       out.write(v >>> 8 & 0xFF);
/* 449 */       out.write(v & 0xFF);
/*     */     } }
/*     */ 
/*     */   final void writeInt(OutputStream out, int v) throws IOException
/*     */   {
/* 454 */     if (this.littleEndian) {
/* 455 */       out.write(v & 0xFF);
/* 456 */       out.write(v >>> 8 & 0xFF);
/* 457 */       out.write(v >>> 16 & 0xFF);
/* 458 */       out.write(v >>> 24 & 0xFF);
/*     */     } else {
/* 460 */       out.write(v >>> 24 & 0xFF);
/* 461 */       out.write(v >>> 16 & 0xFF);
/* 462 */       out.write(v >>> 8 & 0xFF);
/* 463 */       out.write(v & 0xFF);
/*     */     }
/*     */   }
/*     */ 
/*     */   final void writeLong(OutputStream out, long v) throws IOException {
/* 468 */     if (this.littleEndian) {
/* 469 */       this.buffer[7] = ((byte)(int)(v >>> 56));
/* 470 */       this.buffer[6] = ((byte)(int)(v >>> 48));
/* 471 */       this.buffer[5] = ((byte)(int)(v >>> 40));
/* 472 */       this.buffer[4] = ((byte)(int)(v >>> 32));
/* 473 */       this.buffer[3] = ((byte)(int)(v >>> 24));
/* 474 */       this.buffer[2] = ((byte)(int)(v >>> 16));
/* 475 */       this.buffer[1] = ((byte)(int)(v >>> 8));
/* 476 */       this.buffer[0] = ((byte)(int)v);
/* 477 */       out.write(this.buffer, 0, 8);
/*     */     } else {
/* 479 */       this.buffer[0] = ((byte)(int)(v >>> 56));
/* 480 */       this.buffer[1] = ((byte)(int)(v >>> 48));
/* 481 */       this.buffer[2] = ((byte)(int)(v >>> 40));
/* 482 */       this.buffer[3] = ((byte)(int)(v >>> 32));
/* 483 */       this.buffer[4] = ((byte)(int)(v >>> 24));
/* 484 */       this.buffer[5] = ((byte)(int)(v >>> 16));
/* 485 */       this.buffer[6] = ((byte)(int)(v >>> 8));
/* 486 */       this.buffer[7] = ((byte)(int)v);
/* 487 */       out.write(this.buffer, 0, 8);
/*     */     }
/*     */   }
/*     */ 
/*     */   final void writeDouble(OutputStream out, double v) throws IOException {
/* 492 */     writeLong(out, Double.doubleToLongBits(v));
/*     */   }
/*     */ 
/*     */   final void writeChars(OutputStream out, String s) throws IOException {
/* 496 */     int len = s.length();
/* 497 */     if (this.littleEndian)
/* 498 */       for (int i = 0; i < len; i++) {
/* 499 */         int v = s.charAt(i);
/* 500 */         out.write(v & 0xFF);
/* 501 */         out.write(v >>> 8 & 0xFF);
/*     */       }
/*     */     else
/* 504 */       for (int i = 0; i < len; i++) {
/* 505 */         int v = s.charAt(i);
/* 506 */         out.write(v >>> 8 & 0xFF);
/* 507 */         out.write(v & 0xFF);
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.TiffEncoder
 * JD-Core Version:    0.6.2
 */