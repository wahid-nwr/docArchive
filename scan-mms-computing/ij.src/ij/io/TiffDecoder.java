/*     */ package ij.io;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.util.Tools;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class TiffDecoder
/*     */ {
/*     */   public static final int NEW_SUBFILE_TYPE = 254;
/*     */   public static final int IMAGE_WIDTH = 256;
/*     */   public static final int IMAGE_LENGTH = 257;
/*     */   public static final int BITS_PER_SAMPLE = 258;
/*     */   public static final int COMPRESSION = 259;
/*     */   public static final int PHOTO_INTERP = 262;
/*     */   public static final int IMAGE_DESCRIPTION = 270;
/*     */   public static final int STRIP_OFFSETS = 273;
/*     */   public static final int ORIENTATION = 274;
/*     */   public static final int SAMPLES_PER_PIXEL = 277;
/*     */   public static final int ROWS_PER_STRIP = 278;
/*     */   public static final int STRIP_BYTE_COUNT = 279;
/*     */   public static final int X_RESOLUTION = 282;
/*     */   public static final int Y_RESOLUTION = 283;
/*     */   public static final int PLANAR_CONFIGURATION = 284;
/*     */   public static final int RESOLUTION_UNIT = 296;
/*     */   public static final int SOFTWARE = 305;
/*     */   public static final int DATE_TIME = 306;
/*     */   public static final int ARTEST = 315;
/*     */   public static final int HOST_COMPUTER = 316;
/*     */   public static final int PREDICTOR = 317;
/*     */   public static final int COLOR_MAP = 320;
/*     */   public static final int TILE_WIDTH = 322;
/*     */   public static final int SAMPLE_FORMAT = 339;
/*     */   public static final int JPEG_TABLES = 347;
/*     */   public static final int METAMORPH1 = 33628;
/*     */   public static final int METAMORPH2 = 33629;
/*     */   public static final int IPLAB = 34122;
/*     */   public static final int NIH_IMAGE_HDR = 43314;
/*     */   public static final int META_DATA_BYTE_COUNTS = 50838;
/*     */   public static final int META_DATA = 50839;
/*     */   static final int UNSIGNED = 1;
/*     */   static final int SIGNED = 2;
/*     */   static final int FLOATING_POINT = 3;
/*     */   static final int SHORT = 3;
/*     */   static final int LONG = 4;
/*     */   static final int MAGIC_NUMBER = 1229605194;
/*     */   static final int INFO = 1768842863;
/*     */   static final int LABELS = 1818321516;
/*     */   static final int RANGES = 1918987879;
/*     */   static final int LUTS = 1819636851;
/*     */   static final int ROI = 1919904032;
/*     */   static final int OVERLAY = 1870030194;
/*     */   private String directory;
/*     */   private String name;
/*     */   private String url;
/*     */   protected RandomAccessStream in;
/*     */   protected boolean debugMode;
/*     */   private boolean littleEndian;
/*     */   private String dInfo;
/*     */   private int ifdCount;
/*     */   private int[] metaDataCounts;
/*     */   private String tiffMetadata;
/*     */ 
/*     */   public TiffDecoder(String directory, String name)
/*     */   {
/*  76 */     this.directory = directory;
/*  77 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public TiffDecoder(InputStream in, String name) {
/*  81 */     this.directory = "";
/*  82 */     this.name = name;
/*  83 */     this.url = "";
/*  84 */     this.in = new RandomAccessStream(in);
/*     */   }
/*     */ 
/*     */   final int getInt() throws IOException {
/*  88 */     int b1 = this.in.read();
/*  89 */     int b2 = this.in.read();
/*  90 */     int b3 = this.in.read();
/*  91 */     int b4 = this.in.read();
/*  92 */     if (this.littleEndian) {
/*  93 */       return (b4 << 24) + (b3 << 16) + (b2 << 8) + (b1 << 0);
/*     */     }
/*  95 */     return (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
/*     */   }
/*     */ 
/*     */   final int getShort() throws IOException {
/*  99 */     int b1 = this.in.read();
/* 100 */     int b2 = this.in.read();
/* 101 */     if (this.littleEndian) {
/* 102 */       return (b2 << 8) + b1;
/*     */     }
/* 104 */     return (b1 << 8) + b2;
/*     */   }
/*     */ 
/*     */   final long readLong() throws IOException {
/* 108 */     if (this.littleEndian) {
/* 109 */       return (getInt() & 0xFFFFFFFF) + (getInt() << 32);
/*     */     }
/* 111 */     return (getInt() << 32) + (getInt() & 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   final double readDouble() throws IOException
/*     */   {
/* 116 */     return Double.longBitsToDouble(readLong());
/*     */   }
/*     */ 
/*     */   long OpenImageFileHeader()
/*     */     throws IOException
/*     */   {
/* 123 */     int byteOrder = this.in.readShort();
/* 124 */     if (byteOrder == 18761) {
/* 125 */       this.littleEndian = true;
/* 126 */     } else if (byteOrder == 19789) {
/* 127 */       this.littleEndian = false;
/*     */     } else {
/* 129 */       this.in.close();
/* 130 */       return -1L;
/*     */     }
/* 132 */     int magicNumber = getShort();
/* 133 */     long offset = getInt() & 0xFFFFFFFF;
/* 134 */     return offset;
/*     */   }
/*     */ 
/*     */   int getValue(int fieldType, int count) throws IOException {
/* 138 */     int value = 0;
/*     */     int unused;
/* 140 */     if ((fieldType == 3) && (count == 1)) {
/* 141 */       value = getShort();
/* 142 */       unused = getShort();
/*     */     } else {
/* 144 */       value = getInt();
/* 145 */     }return value;
/*     */   }
/*     */ 
/*     */   void getColorMap(long offset, FileInfo fi) throws IOException {
/* 149 */     byte[] colorTable16 = new byte[1536];
/* 150 */     long saveLoc = this.in.getLongFilePointer();
/* 151 */     this.in.seek(offset);
/* 152 */     this.in.readFully(colorTable16);
/* 153 */     this.in.seek(saveLoc);
/* 154 */     fi.lutSize = 256;
/* 155 */     fi.reds = new byte[256];
/* 156 */     fi.greens = new byte[256];
/* 157 */     fi.blues = new byte[256];
/* 158 */     int j = 0;
/* 159 */     if (this.littleEndian) j++;
/* 160 */     int sum = 0;
/* 161 */     for (int i = 0; i < 256; i++) {
/* 162 */       fi.reds[i] = colorTable16[j];
/* 163 */       sum += fi.reds[i];
/* 164 */       fi.greens[i] = colorTable16[(512 + j)];
/* 165 */       sum += fi.greens[i];
/* 166 */       fi.blues[i] = colorTable16[(1024 + j)];
/* 167 */       sum += fi.blues[i];
/* 168 */       j += 2;
/*     */     }
/* 170 */     if (sum != 0)
/* 171 */       fi.fileType = 5;
/*     */   }
/*     */ 
/*     */   byte[] getString(int count, long offset) throws IOException {
/* 175 */     count--;
/* 176 */     if (count <= 3)
/* 177 */       return null;
/* 178 */     byte[] bytes = new byte[count];
/* 179 */     long saveLoc = this.in.getLongFilePointer();
/* 180 */     this.in.seek(offset);
/* 181 */     this.in.readFully(bytes);
/* 182 */     this.in.seek(saveLoc);
/* 183 */     return bytes;
/*     */   }
/*     */ 
/*     */   public void saveImageDescription(byte[] description, FileInfo fi)
/*     */   {
/* 191 */     String id = new String(description);
/* 192 */     if (!id.startsWith("ImageJ"))
/* 193 */       saveMetadata(getName(270), id);
/* 194 */     if (id.length() < 7) return;
/* 195 */     fi.description = id;
/* 196 */     int index1 = id.indexOf("images=");
/* 197 */     if (index1 > 0) {
/* 198 */       int index2 = id.indexOf("\n", index1);
/* 199 */       if (index2 > 0) {
/* 200 */         String images = id.substring(index1 + 7, index2);
/* 201 */         int n = (int)Tools.parseDouble(images, 0.0D);
/* 202 */         if (n > 1) fi.nImages = n; 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void saveMetadata(String name, String data)
/*     */   {
/* 208 */     if (data == null) return;
/* 209 */     String str = name + ": " + data + "\n";
/* 210 */     if (this.tiffMetadata == null)
/* 211 */       this.tiffMetadata = str;
/*     */     else
/* 213 */       this.tiffMetadata += str;
/*     */   }
/*     */ 
/*     */   void decodeNIHImageHeader(int offset, FileInfo fi) throws IOException {
/* 217 */     long saveLoc = this.in.getLongFilePointer();
/*     */ 
/* 219 */     this.in.seek(offset + 12);
/* 220 */     int version = this.in.readShort();
/*     */ 
/* 222 */     this.in.seek(offset + 160);
/* 223 */     double scale = this.in.readDouble();
/* 224 */     if ((version > 106) && (scale != 0.0D)) {
/* 225 */       fi.pixelWidth = (1.0D / scale);
/* 226 */       fi.pixelHeight = fi.pixelWidth;
/*     */     }
/*     */ 
/* 230 */     this.in.seek(offset + 172);
/* 231 */     int units = this.in.readShort();
/* 232 */     if (version <= 153) units += 5;
/* 233 */     switch (units) { case 5:
/* 234 */       fi.unit = "nanometer"; break;
/*     */     case 6:
/* 235 */       fi.unit = "micrometer"; break;
/*     */     case 7:
/* 236 */       fi.unit = "mm"; break;
/*     */     case 8:
/* 237 */       fi.unit = "cm"; break;
/*     */     case 9:
/* 238 */       fi.unit = "meter"; break;
/*     */     case 10:
/* 239 */       fi.unit = "km"; break;
/*     */     case 11:
/* 240 */       fi.unit = "inch"; break;
/*     */     case 12:
/* 241 */       fi.unit = "ft"; break;
/*     */     case 13:
/* 242 */       fi.unit = "mi";
/*     */     }
/*     */ 
/* 246 */     this.in.seek(offset + 182);
/* 247 */     int fitType = this.in.read();
/* 248 */     int unused = this.in.read();
/* 249 */     int nCoefficients = this.in.readShort();
/* 250 */     if (fitType == 11) {
/* 251 */       fi.calibrationFunction = 21;
/* 252 */       fi.valueUnit = "U. OD";
/* 253 */     } else if ((fitType >= 0) && (fitType <= 8) && (nCoefficients >= 1) && (nCoefficients <= 5)) {
/* 254 */       switch (fitType) { case 0:
/* 255 */         fi.calibrationFunction = 0; break;
/*     */       case 1:
/* 256 */         fi.calibrationFunction = 1; break;
/*     */       case 2:
/* 257 */         fi.calibrationFunction = 2; break;
/*     */       case 3:
/* 258 */         fi.calibrationFunction = 3; break;
/*     */       case 5:
/* 259 */         fi.calibrationFunction = 4; break;
/*     */       case 6:
/* 260 */         fi.calibrationFunction = 5; break;
/*     */       case 7:
/* 261 */         fi.calibrationFunction = 6; break;
/*     */       case 8:
/* 262 */         fi.calibrationFunction = 10;
/*     */       case 4: }
/* 264 */       fi.coefficients = new double[nCoefficients];
/* 265 */       for (int i = 0; i < nCoefficients; i++) {
/* 266 */         fi.coefficients[i] = this.in.readDouble();
/*     */       }
/* 268 */       this.in.seek(offset + 234);
/* 269 */       int size = this.in.read();
/* 270 */       StringBuffer sb = new StringBuffer();
/* 271 */       if ((size >= 1) && (size <= 16)) {
/* 272 */         for (int i = 0; i < size; i++)
/* 273 */           sb.append((char)this.in.read());
/* 274 */         fi.valueUnit = new String(sb);
/*     */       } else {
/* 276 */         fi.valueUnit = " ";
/*     */       }
/*     */     }
/* 279 */     this.in.seek(offset + 260);
/* 280 */     int nImages = this.in.readShort();
/* 281 */     if ((nImages >= 2) && ((fi.fileType == 0) || (fi.fileType == 5))) {
/* 282 */       fi.nImages = nImages;
/* 283 */       fi.pixelDepth = this.in.readFloat();
/* 284 */       int skip = this.in.readShort();
/* 285 */       fi.frameInterval = this.in.readFloat();
/*     */     }
/*     */ 
/* 289 */     this.in.seek(offset + 272);
/* 290 */     float aspectRatio = this.in.readFloat();
/* 291 */     if ((version > 140) && (aspectRatio != 0.0D)) {
/* 292 */       fi.pixelHeight = (fi.pixelWidth / aspectRatio);
/*     */     }
/* 294 */     this.in.seek(saveLoc);
/*     */   }
/*     */ 
/*     */   void dumpTag(int tag, int count, int value, FileInfo fi) {
/* 298 */     long lvalue = value & 0xFFFFFFFF;
/* 299 */     String name = getName(tag);
/* 300 */     String cs = ", count=" + count;
/* 301 */     this.dInfo = (this.dInfo + "    " + tag + ", \"" + name + "\", value=" + lvalue + cs + "\n");
/*     */   }
/*     */ 
/*     */   String getName(int tag)
/*     */   {
/*     */     String name;
/* 307 */     switch (tag) { case 254:
/* 308 */       name = "NewSubfileType"; break;
/*     */     case 256:
/* 309 */       name = "ImageWidth"; break;
/*     */     case 257:
/* 310 */       name = "ImageLength"; break;
/*     */     case 273:
/* 311 */       name = "StripOffsets"; break;
/*     */     case 274:
/* 312 */       name = "Orientation"; break;
/*     */     case 262:
/* 313 */       name = "PhotoInterp"; break;
/*     */     case 270:
/* 314 */       name = "ImageDescription"; break;
/*     */     case 258:
/* 315 */       name = "BitsPerSample"; break;
/*     */     case 277:
/* 316 */       name = "SamplesPerPixel"; break;
/*     */     case 278:
/* 317 */       name = "RowsPerStrip"; break;
/*     */     case 279:
/* 318 */       name = "StripByteCount"; break;
/*     */     case 282:
/* 319 */       name = "XResolution"; break;
/*     */     case 283:
/* 320 */       name = "YResolution"; break;
/*     */     case 296:
/* 321 */       name = "ResolutionUnit"; break;
/*     */     case 305:
/* 322 */       name = "Software"; break;
/*     */     case 306:
/* 323 */       name = "DateTime"; break;
/*     */     case 315:
/* 324 */       name = "Artest"; break;
/*     */     case 316:
/* 325 */       name = "HostComputer"; break;
/*     */     case 284:
/* 326 */       name = "PlanarConfiguration"; break;
/*     */     case 259:
/* 327 */       name = "Compression"; break;
/*     */     case 317:
/* 328 */       name = "Predictor"; break;
/*     */     case 320:
/* 329 */       name = "ColorMap"; break;
/*     */     case 339:
/* 330 */       name = "SampleFormat"; break;
/*     */     case 347:
/* 331 */       name = "JPEGTables"; break;
/*     */     case 43314:
/* 332 */       name = "NIHImageHeader"; break;
/*     */     case 50838:
/* 333 */       name = "MetaDataByteCounts"; break;
/*     */     case 50839:
/* 334 */       name = "MetaData"; break;
/*     */     default:
/* 335 */       name = "???";
/*     */     }
/* 337 */     return name;
/*     */   }
/*     */ 
/*     */   double getRational(long loc) throws IOException {
/* 341 */     long saveLoc = this.in.getLongFilePointer();
/* 342 */     this.in.seek(loc);
/* 343 */     int numerator = getInt();
/* 344 */     int denominator = getInt();
/* 345 */     this.in.seek(saveLoc);
/*     */ 
/* 348 */     if (denominator != 0) {
/* 349 */       return numerator / denominator;
/*     */     }
/* 351 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   FileInfo OpenIFD()
/*     */     throws IOException
/*     */   {
/* 357 */     int nEntries = getShort();
/* 358 */     if ((nEntries < 1) || (nEntries > 1000))
/* 359 */       return null;
/* 360 */     this.ifdCount += 1;
/* 361 */     if ((this.ifdCount % 100 == 0) && (this.ifdCount > 0))
/* 362 */       IJ.showStatus("" + this.ifdCount);
/* 363 */     FileInfo fi = new FileInfo();
/* 364 */     for (int i = 0; i < nEntries; i++) {
/* 365 */       int tag = getShort();
/* 366 */       int fieldType = getShort();
/* 367 */       int count = getInt();
/* 368 */       int value = getValue(fieldType, count);
/* 369 */       long lvalue = value & 0xFFFFFFFF;
/* 370 */       if ((this.debugMode) && (this.ifdCount < 10)) dumpTag(tag, count, value, fi);
/*     */ 
/* 373 */       switch (tag) {
/*     */       case 256:
/* 375 */         fi.width = value;
/* 376 */         fi.intelByteOrder = this.littleEndian;
/* 377 */         break;
/*     */       case 257:
/* 379 */         fi.height = value;
/* 380 */         break;
/*     */       case 273:
/* 382 */         if (count == 1) {
/* 383 */           fi.stripOffsets = new int[] { value };
/*     */         } else {
/* 385 */           long saveLoc = this.in.getLongFilePointer();
/* 386 */           this.in.seek(lvalue);
/* 387 */           fi.stripOffsets = new int[count];
/* 388 */           for (int c = 0; c < count; c++)
/* 389 */             fi.stripOffsets[c] = getInt();
/* 390 */           this.in.seek(saveLoc);
/*     */         }
/* 392 */         fi.offset = (count > 0 ? fi.stripOffsets[0] : value);
/* 393 */         if ((count > 1) && (fi.stripOffsets[(count - 1)] < fi.stripOffsets[0]))
/* 394 */           fi.offset = fi.stripOffsets[(count - 1)]; break;
/*     */       case 279:
/* 397 */         if (count == 1) {
/* 398 */           fi.stripLengths = new int[] { value };
/*     */         } else {
/* 400 */           long saveLoc = this.in.getLongFilePointer();
/* 401 */           this.in.seek(lvalue);
/* 402 */           fi.stripLengths = new int[count];
/* 403 */           for (int c = 0; c < count; c++) {
/* 404 */             if (fieldType == 3)
/* 405 */               fi.stripLengths[c] = getShort();
/*     */             else
/* 407 */               fi.stripLengths[c] = getInt();
/*     */           }
/* 409 */           this.in.seek(saveLoc);
/*     */         }
/* 411 */         break;
/*     */       case 262:
/* 413 */         fi.whiteIsZero = (value == 0);
/* 414 */         break;
/*     */       case 258:
/* 416 */         if (count == 1) {
/* 417 */           if (value == 8)
/* 418 */             fi.fileType = 0;
/* 419 */           else if (value == 16)
/* 420 */             fi.fileType = 2;
/* 421 */           else if (value == 32)
/* 422 */             fi.fileType = 3;
/* 423 */           else if (value == 12)
/* 424 */             fi.fileType = 13;
/* 425 */           else if (value == 1)
/* 426 */             fi.fileType = 8;
/*     */           else
/* 428 */             error("Unsupported BitsPerSample: " + value);
/* 429 */         } else if (count == 3) {
/* 430 */           long saveLoc = this.in.getLongFilePointer();
/* 431 */           this.in.seek(lvalue);
/* 432 */           int bitDepth = getShort();
/* 433 */           if ((bitDepth != 8) && (bitDepth != 16))
/* 434 */             error("ImageJ can only open 8 and 16 bit/channel RGB images (" + bitDepth + ")");
/* 435 */           if (bitDepth == 16)
/* 436 */             fi.fileType = 12;
/* 437 */           this.in.seek(saveLoc);
/* 438 */         }break;
/*     */       case 277:
/* 441 */         fi.samplesPerPixel = value;
/* 442 */         if ((value == 3) && (fi.fileType != 12))
/* 443 */           fi.fileType = (fi.fileType == 2 ? 12 : 6);
/* 444 */         else if ((value == 4) && (fi.fileType == 0))
/* 445 */           fi.fileType = 9; break;
/*     */       case 278:
/* 448 */         fi.rowsPerStrip = value;
/* 449 */         break;
/*     */       case 282:
/* 451 */         double xScale = getRational(lvalue);
/* 452 */         if (xScale != 0.0D) fi.pixelWidth = (1.0D / xScale); break;
/*     */       case 283:
/* 455 */         double yScale = getRational(lvalue);
/* 456 */         if (yScale != 0.0D) fi.pixelHeight = (1.0D / yScale); break;
/*     */       case 296:
/* 459 */         if ((value == 1) && (fi.unit == null))
/* 460 */           fi.unit = " ";
/* 461 */         else if (value == 2) {
/* 462 */           if (fi.pixelWidth == 0.01388888888888889D) {
/* 463 */             fi.pixelWidth = 1.0D;
/* 464 */             fi.pixelHeight = 1.0D;
/*     */           } else {
/* 466 */             fi.unit = "inch";
/*     */           } } else if (value == 3)
/* 468 */           fi.unit = "cm"; break;
/*     */       case 284:
/* 471 */         if ((value == 2) && (fi.fileType == 12)) {
/* 472 */           fi.fileType = 2;
/* 473 */         } else if ((value == 2) && (fi.fileType == 6)) {
/* 474 */           fi.fileType = 7;
/* 475 */         } else if ((value == 1) && (fi.samplesPerPixel == 4)) {
/* 476 */           fi.fileType = 9;
/* 477 */         } else if ((value != 2) && (fi.samplesPerPixel != 1) && (fi.samplesPerPixel != 3)) {
/* 478 */           String msg = "Unsupported SamplesPerPixel: " + fi.samplesPerPixel;
/* 479 */           error(msg);
/* 480 */         }break;
/*     */       case 259:
/* 483 */         if (value == 5) {
/* 484 */           fi.compression = 2;
/* 485 */         } else if (value == 32773) {
/* 486 */           fi.compression = 5;
/* 487 */         } else if ((value == 32946) || (value == 8)) {
/* 488 */           fi.compression = 6;
/* 489 */         } else if ((value != 1) && (value != 0) && ((value != 7) || (fi.width >= 500)))
/*     */         {
/* 492 */           fi.compression = 0;
/* 493 */           error("ImageJ cannot open TIFF files compressed in this fashion (" + value + ")"); } break;
/*     */       case 305:
/*     */       case 306:
/*     */       case 315:
/*     */       case 316:
/* 498 */         if (this.ifdCount == 1) {
/* 499 */           byte[] bytes = getString(count, lvalue);
/* 500 */           String s = bytes != null ? new String(bytes) : null;
/* 501 */           saveMetadata(getName(tag), s);
/* 502 */         }break;
/*     */       case 317:
/* 505 */         if ((value == 2) && (fi.compression == 2))
/* 506 */           fi.compression = 3; break;
/*     */       case 320:
/* 509 */         if ((count == 768) && (fi.fileType == 0))
/* 510 */           getColorMap(lvalue, fi); break;
/*     */       case 322:
/* 513 */         error("ImageJ cannot open tiled TIFFs");
/* 514 */         break;
/*     */       case 339:
/* 516 */         if ((fi.fileType == 3) && (value == 3))
/* 517 */           fi.fileType = 4;
/* 518 */         if (fi.fileType == 2) {
/* 519 */           if (value == 2)
/* 520 */             fi.fileType = 1;
/* 521 */           if (value == 3)
/* 522 */             error("ImageJ cannot open 16-bit float TIFFs");  } break;
/*     */       case 347:
/* 526 */         if (fi.compression == 4)
/* 527 */           error("Cannot open JPEG-compressed TIFFs with separate tables"); break;
/*     */       case 270:
/* 530 */         if (this.ifdCount == 1) {
/* 531 */           byte[] s = getString(count, lvalue);
/* 532 */           if (s != null) saveImageDescription(s, fi); 
/*     */         }
/* 533 */         break;
/*     */       case 274:
/* 536 */         fi.nImages = 0;
/* 537 */         break;
/*     */       case 33628:
/*     */       case 33629:
/* 539 */         if (((this.name.indexOf(".STK") > 0) || (this.name.indexOf(".stk") > 0)) && (fi.compression == 1))
/* 540 */           if (tag == 33629)
/* 541 */             fi.nImages = count;
/*     */           else
/* 543 */             fi.nImages = 9999; 
/* 543 */         break;
/*     */       case 34122:
/* 547 */         fi.nImages = value;
/* 548 */         break;
/*     */       case 43314:
/* 550 */         if (count == 256)
/* 551 */           decodeNIHImageHeader(value, fi); break;
/*     */       case 50838:
/* 554 */         long saveLoc = this.in.getLongFilePointer();
/* 555 */         this.in.seek(lvalue);
/* 556 */         this.metaDataCounts = new int[count];
/* 557 */         for (int c = 0; c < count; c++)
/* 558 */           this.metaDataCounts[c] = getInt();
/* 559 */         this.in.seek(saveLoc);
/* 560 */         break;
/*     */       case 50839:
/* 562 */         getMetaData(value, fi);
/* 563 */         break;
/*     */       default:
/* 565 */         if ((tag > 10000) && (tag < 32768) && (this.ifdCount > 1))
/* 566 */           return null; break;
/*     */       }
/*     */     }
/* 569 */     fi.fileFormat = 2;
/* 570 */     fi.fileName = this.name;
/* 571 */     fi.directory = this.directory;
/* 572 */     if (this.url != null)
/* 573 */       fi.url = this.url;
/* 574 */     return fi;
/*     */   }
/*     */ 
/*     */   void getMetaData(int loc, FileInfo fi) throws IOException {
/* 578 */     if ((this.metaDataCounts == null) || (this.metaDataCounts.length == 0))
/* 579 */       return;
/* 580 */     int maxTypes = 10;
/* 581 */     long saveLoc = this.in.getLongFilePointer();
/* 582 */     this.in.seek(loc);
/* 583 */     int n = this.metaDataCounts.length;
/* 584 */     int hdrSize = this.metaDataCounts[0];
/* 585 */     if ((hdrSize < 12) || (hdrSize > 804)) {
/* 586 */       this.in.seek(saveLoc); return;
/* 587 */     }int magicNumber = getInt();
/* 588 */     if (magicNumber != 1229605194) {
/* 589 */       this.in.seek(saveLoc); return;
/* 590 */     }int nTypes = (hdrSize - 4) / 8;
/* 591 */     int[] types = new int[nTypes];
/* 592 */     int[] counts = new int[nTypes];
/*     */ 
/* 594 */     if (this.debugMode) this.dInfo += "Metadata:\n";
/* 595 */     int extraMetaDataEntries = 0;
/* 596 */     for (int i = 0; i < nTypes; i++) {
/* 597 */       types[i] = getInt();
/* 598 */       counts[i] = getInt();
/* 599 */       if (types[i] < 16777215)
/* 600 */         extraMetaDataEntries += counts[i];
/* 601 */       if (this.debugMode) {
/* 602 */         String id = "";
/* 603 */         if (types[i] == 1768842863) id = " (Info property)";
/* 604 */         if (types[i] == 1818321516) id = " (slice labels)";
/* 605 */         if (types[i] == 1918987879) id = " (display ranges)";
/* 606 */         if (types[i] == 1819636851) id = " (luts)";
/* 607 */         if (types[i] == 1919904032) id = " (roi)";
/* 608 */         if (types[i] == 1870030194) id = " (overlay)";
/* 609 */         this.dInfo = (this.dInfo + "   " + i + " " + Integer.toHexString(types[i]) + " " + counts[i] + id + "\n");
/*     */       }
/*     */     }
/* 612 */     fi.metaDataTypes = new int[extraMetaDataEntries];
/* 613 */     fi.metaData = new byte[extraMetaDataEntries][];
/* 614 */     int start = 1;
/* 615 */     int eMDindex = 0;
/* 616 */     for (int i = 0; i < nTypes; i++) {
/* 617 */       if (types[i] == 1768842863)
/* 618 */         getInfoProperty(start, fi);
/* 619 */       else if (types[i] == 1818321516)
/* 620 */         getSliceLabels(start, start + counts[i] - 1, fi);
/* 621 */       else if (types[i] == 1918987879)
/* 622 */         getDisplayRanges(start, fi);
/* 623 */       else if (types[i] == 1819636851)
/* 624 */         getLuts(start, start + counts[i] - 1, fi);
/* 625 */       else if (types[i] == 1919904032)
/* 626 */         getRoi(start, fi);
/* 627 */       else if (types[i] == 1870030194)
/* 628 */         getOverlay(start, start + counts[i] - 1, fi);
/* 629 */       else if (types[i] < 16777215)
/* 630 */         for (int j = start; j < start + counts[i]; j++) {
/* 631 */           int len = this.metaDataCounts[j];
/* 632 */           fi.metaData[eMDindex] = new byte[len];
/* 633 */           this.in.readFully(fi.metaData[eMDindex], len);
/* 634 */           fi.metaDataTypes[eMDindex] = types[i];
/* 635 */           eMDindex++;
/*     */         }
/*     */       else
/* 638 */         skipUnknownType(start, start + counts[i] - 1);
/* 639 */       start += counts[i];
/*     */     }
/* 641 */     this.in.seek(saveLoc);
/*     */   }
/*     */ 
/*     */   void getInfoProperty(int first, FileInfo fi) throws IOException {
/* 645 */     int len = this.metaDataCounts[first];
/* 646 */     byte[] buffer = new byte[len];
/* 647 */     this.in.readFully(buffer, len);
/* 648 */     len /= 2;
/* 649 */     char[] chars = new char[len];
/* 650 */     if (this.littleEndian) {
/* 651 */       int j = 0; for (int k = 0; j < len; j++)
/* 652 */         chars[j] = ((char)(buffer[(k++)] & 255 + ((buffer[(k++)] & 0xFF) << 8)));
/*     */     } else {
/* 654 */       int j = 0; for (int k = 0; j < len; j++)
/* 655 */         chars[j] = ((char)(((buffer[(k++)] & 0xFF) << 8) + buffer[(k++)] & 0xFF));
/*     */     }
/* 657 */     fi.info = new String(chars);
/*     */   }
/*     */ 
/*     */   void getSliceLabels(int first, int last, FileInfo fi) throws IOException {
/* 661 */     fi.sliceLabels = new String[last - first + 1];
/* 662 */     int index = 0;
/* 663 */     byte[] buffer = new byte[this.metaDataCounts[first]];
/* 664 */     for (int i = first; i <= last; i++) {
/* 665 */       int len = this.metaDataCounts[i];
/* 666 */       if (len > 0) {
/* 667 */         if (len > buffer.length)
/* 668 */           buffer = new byte[len];
/* 669 */         this.in.readFully(buffer, len);
/* 670 */         len /= 2;
/* 671 */         char[] chars = new char[len];
/* 672 */         if (this.littleEndian) {
/* 673 */           int j = 0; for (int k = 0; j < len; j++)
/* 674 */             chars[j] = ((char)(buffer[(k++)] & 255 + ((buffer[(k++)] & 0xFF) << 8)));
/*     */         } else {
/* 676 */           int j = 0; for (int k = 0; j < len; j++)
/* 677 */             chars[j] = ((char)(((buffer[(k++)] & 0xFF) << 8) + buffer[(k++)] & 0xFF));
/*     */         }
/* 679 */         fi.sliceLabels[(index++)] = new String(chars);
/*     */       }
/*     */       else {
/* 682 */         fi.sliceLabels[(index++)] = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 687 */   void getDisplayRanges(int first, FileInfo fi) throws IOException { int n = this.metaDataCounts[first] / 8;
/* 688 */     fi.displayRanges = new double[n];
/* 689 */     for (int i = 0; i < n; i++)
/* 690 */       fi.displayRanges[i] = readDouble(); }
/*     */ 
/*     */   void getLuts(int first, int last, FileInfo fi) throws IOException
/*     */   {
/* 694 */     fi.channelLuts = new byte[last - first + 1][];
/* 695 */     int index = 0;
/* 696 */     for (int i = first; i <= last; i++) {
/* 697 */       int len = this.metaDataCounts[i];
/* 698 */       fi.channelLuts[index] = new byte[len];
/* 699 */       this.in.readFully(fi.channelLuts[index], len);
/* 700 */       index++;
/*     */     }
/*     */   }
/*     */ 
/*     */   void getRoi(int first, FileInfo fi) throws IOException {
/* 705 */     int len = this.metaDataCounts[first];
/* 706 */     fi.roi = new byte[len];
/* 707 */     this.in.readFully(fi.roi, len);
/*     */   }
/*     */ 
/*     */   void getOverlay(int first, int last, FileInfo fi) throws IOException {
/* 711 */     fi.overlay = new byte[last - first + 1][];
/* 712 */     int index = 0;
/* 713 */     for (int i = first; i <= last; i++) {
/* 714 */       int len = this.metaDataCounts[i];
/* 715 */       fi.overlay[index] = new byte[len];
/* 716 */       this.in.readFully(fi.overlay[index], len);
/* 717 */       index++;
/*     */     }
/*     */   }
/*     */ 
/*     */   void error(String message) throws IOException {
/* 722 */     if (this.in != null) this.in.close();
/* 723 */     throw new IOException(message);
/*     */   }
/*     */ 
/*     */   void skipUnknownType(int first, int last) throws IOException {
/* 727 */     byte[] buffer = new byte[this.metaDataCounts[first]];
/* 728 */     for (int i = first; i <= last; i++) {
/* 729 */       int len = this.metaDataCounts[i];
/* 730 */       if (len > buffer.length)
/* 731 */         buffer = new byte[len];
/* 732 */       this.in.readFully(buffer, len);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enableDebugging() {
/* 737 */     this.debugMode = true;
/*     */   }
/*     */ 
/*     */   public FileInfo[] getTiffInfo()
/*     */     throws IOException
/*     */   {
/* 744 */     if (this.in == null)
/* 745 */       this.in = new RandomAccessStream(new RandomAccessFile(new File(this.directory, this.name), "r"));
/* 746 */     Vector info = new Vector();
/* 747 */     long ifdOffset = OpenImageFileHeader();
/* 748 */     if (ifdOffset < 0L) {
/* 749 */       this.in.close();
/* 750 */       return null;
/*     */     }
/* 752 */     if (this.debugMode) this.dInfo = ("\n  " + this.name + ": opening\n");
/* 753 */     while (ifdOffset > 0L) {
/* 754 */       this.in.seek(ifdOffset);
/* 755 */       FileInfo fi = OpenIFD();
/* 756 */       if (fi != null) {
/* 757 */         info.addElement(fi);
/* 758 */         ifdOffset = getInt() & 0xFFFFFFFF;
/*     */       } else {
/* 760 */         ifdOffset = 0L;
/* 761 */       }if ((this.debugMode) && (this.ifdCount < 10)) this.dInfo = (this.dInfo + "  nextIFD=" + ifdOffset + "\n");
/* 762 */       if ((fi != null) && 
/* 763 */         (fi.nImages > 1)) {
/* 764 */         ifdOffset = 0L;
/*     */       }
/*     */     }
/* 767 */     if (info.size() == 0) {
/* 768 */       this.in.close();
/* 769 */       return null;
/*     */     }
/* 771 */     FileInfo[] fi = new FileInfo[info.size()];
/* 772 */     info.copyInto((Object[])fi);
/* 773 */     if (this.debugMode) fi[0].debugInfo = this.dInfo;
/* 774 */     if (this.url != null) {
/* 775 */       this.in.seek(0);
/* 776 */       fi[0].inputStream = this.in;
/*     */     } else {
/* 778 */       this.in.close();
/* 779 */     }if (fi[0].info == null)
/* 780 */       fi[0].info = this.tiffMetadata;
/* 781 */     if (this.debugMode) {
/* 782 */       int n = fi.length;
/*     */       FileInfo tmp356_355 = fi[0]; tmp356_355.debugInfo = (tmp356_355.debugInfo + "number of images: " + n + "\n");
/*     */       FileInfo tmp395_394 = fi[0]; tmp395_394.debugInfo = (tmp395_394.debugInfo + "offset to first image: " + fi[0].getOffset() + "\n");
/*     */       FileInfo tmp439_438 = fi[0]; tmp439_438.debugInfo = (tmp439_438.debugInfo + "gap between images: " + getGapInfo(fi) + "\n");
/*     */       FileInfo tmp482_481 = fi[0]; tmp482_481.debugInfo = (tmp482_481.debugInfo + "little-endian byte order: " + fi[0].intelByteOrder + "\n");
/*     */     }
/* 788 */     return fi;
/*     */   }
/*     */ 
/*     */   String getGapInfo(FileInfo[] fi)
/*     */   {
/* 793 */     if (fi.length < 2) return "0";
/* 794 */     long minGap = 9223372036854775807L;
/* 795 */     long maxGap = -9223372036854775807L;
/* 796 */     for (int i = 1; i < fi.length; i++) {
/* 797 */       long gap = fi[i].getOffset() - fi[(i - 1)].getOffset();
/* 798 */       if (gap < minGap) minGap = gap;
/* 799 */       if (gap > maxGap) maxGap = gap;
/*     */     }
/* 801 */     long imageSize = fi[0].width * fi[0].height * fi[0].getBytesPerPixel();
/* 802 */     minGap -= imageSize;
/* 803 */     maxGap -= imageSize;
/* 804 */     if (minGap == maxGap) {
/* 805 */       return "" + minGap;
/*     */     }
/* 807 */     return "varies (" + minGap + " to " + maxGap + ")";
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.TiffDecoder
 * JD-Core Version:    0.6.2
 */