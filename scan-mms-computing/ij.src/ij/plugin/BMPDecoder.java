/*     */ package ij.plugin;
/*     */ 
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.MemoryImageSource;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ class BMPDecoder
/*     */ {
/*     */   InputStream is;
/*  68 */   int curPos = 0;
/*     */   int bitmapOffset;
/*     */   int width;
/*     */   int height;
/*     */   short bitsPerPixel;
/*     */   int compression;
/*     */   int actualSizeOfBitmap;
/*     */   int scanLineSize;
/*     */   int actualColorsUsed;
/*     */   byte[] r;
/*     */   byte[] g;
/*     */   byte[] b;
/*     */   int noOfEntries;
/*     */   byte[] byteData;
/*     */   int[] intData;
/*     */   boolean topDown;
/*     */ 
/*     */   private int readInt()
/*     */     throws IOException
/*     */   {
/*  89 */     int b1 = this.is.read();
/*  90 */     int b2 = this.is.read();
/*  91 */     int b3 = this.is.read();
/*  92 */     int b4 = this.is.read();
/*  93 */     this.curPos += 4;
/*  94 */     return (b4 << 24) + (b3 << 16) + (b2 << 8) + (b1 << 0);
/*     */   }
/*     */ 
/*     */   private short readShort() throws IOException
/*     */   {
/*  99 */     int b1 = this.is.read();
/* 100 */     int b2 = this.is.read();
/* 101 */     this.curPos += 2;
/* 102 */     return (short)((b2 << 8) + b1);
/*     */   }
/*     */ 
/*     */   void getFileHeader()
/*     */     throws IOException, Exception
/*     */   {
/* 108 */     short fileType = 19778;
/*     */ 
/* 110 */     short reserved1 = 0;
/* 111 */     short reserved2 = 0;
/*     */ 
/* 113 */     fileType = readShort();
/* 114 */     if (fileType != 19778)
/* 115 */       throw new Exception("Not a BMP file");
/* 116 */     int fileSize = readInt();
/* 117 */     reserved1 = readShort();
/* 118 */     reserved2 = readShort();
/* 119 */     this.bitmapOffset = readInt();
/*     */   }
/*     */ 
/*     */   void getBitmapHeader()
/*     */     throws IOException
/*     */   {
/* 134 */     int size = readInt();
/* 135 */     this.width = readInt();
/* 136 */     this.height = readInt();
/* 137 */     short planes = readShort();
/* 138 */     this.bitsPerPixel = readShort();
/* 139 */     this.compression = readInt();
/* 140 */     int sizeOfBitmap = readInt();
/* 141 */     int horzResolution = readInt();
/* 142 */     int vertResolution = readInt();
/* 143 */     int colorsUsed = readInt();
/* 144 */     int colorsImportant = readInt();
/*     */ 
/* 146 */     this.topDown = (this.height < 0);
/* 147 */     if (this.topDown) this.height = (-this.height);
/* 148 */     int noOfPixels = this.width * this.height;
/*     */ 
/* 151 */     this.scanLineSize = ((this.width * this.bitsPerPixel + 31) / 32 * 4);
/*     */ 
/* 153 */     this.actualSizeOfBitmap = (this.scanLineSize * this.height);
/*     */ 
/* 155 */     if (colorsUsed != 0) {
/* 156 */       this.actualColorsUsed = colorsUsed;
/*     */     }
/* 159 */     else if (this.bitsPerPixel < 16)
/* 160 */       this.actualColorsUsed = (1 << this.bitsPerPixel);
/*     */     else
/* 162 */       this.actualColorsUsed = 0;
/*     */   }
/*     */ 
/*     */   void getPalette() throws IOException {
/* 166 */     this.noOfEntries = this.actualColorsUsed;
/*     */ 
/* 168 */     if (this.noOfEntries > 0) {
/* 169 */       this.r = new byte[this.noOfEntries];
/* 170 */       this.g = new byte[this.noOfEntries];
/* 171 */       this.b = new byte[this.noOfEntries];
/*     */ 
/* 174 */       for (int i = 0; i < this.noOfEntries; i++) {
/* 175 */         this.b[i] = ((byte)this.is.read());
/* 176 */         this.g[i] = ((byte)this.is.read());
/* 177 */         this.r[i] = ((byte)this.is.read());
/* 178 */         int reserved = this.is.read();
/* 179 */         this.curPos += 4;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void unpack(byte[] rawData, int rawOffset, int bpp, byte[] byteData, int byteOffset, int w) throws Exception {
/* 185 */     int j = byteOffset;
/* 186 */     int k = rawOffset;
/*     */     byte mask;
/*     */     int pixPerByte;
/* 190 */     switch (bpp) { case 1:
/* 191 */       mask = 1; pixPerByte = 8; break;
/*     */     case 4:
/* 192 */       mask = 15; pixPerByte = 2; break;
/*     */     case 8:
/* 193 */       mask = -1; pixPerByte = 1; break;
/*     */     default:
/* 195 */       throw new Exception("Unsupported bits-per-pixel value: " + bpp);
/*     */     }
/*     */ 
/* 198 */     int i = 0;
/*     */     while (true) { int shift = 8 - bpp;
/* 200 */       for (int ii = 0; ii < pixPerByte; ii++) {
/* 201 */         byte br = rawData[k];
/* 202 */         br = (byte)(br >> shift);
/* 203 */         byteData[j] = ((byte)(br & mask));
/*     */ 
/* 205 */         j++;
/* 206 */         i++;
/* 207 */         if (i == w) return;
/* 208 */         shift -= bpp;
/*     */       }
/* 210 */       k++; }
/*     */   }
/*     */ 
/*     */   void unpack24(byte[] rawData, int rawOffset, int[] intData, int intOffset, int w)
/*     */   {
/* 215 */     int j = intOffset;
/* 216 */     int k = rawOffset;
/* 217 */     int mask = 255;
/* 218 */     for (int i = 0; i < w; i++) {
/* 219 */       int b0 = rawData[(k++)] & mask;
/* 220 */       int b1 = (rawData[(k++)] & mask) << 8;
/* 221 */       int b2 = (rawData[(k++)] & mask) << 16;
/* 222 */       intData[j] = (0xFF000000 | b0 | b1 | b2);
/* 223 */       j++;
/*     */     }
/*     */   }
/*     */ 
/*     */   void unpack32(byte[] rawData, int rawOffset, int[] intData, int intOffset, int w) {
/* 228 */     int j = intOffset;
/* 229 */     int k = rawOffset;
/* 230 */     int mask = 255;
/* 231 */     for (int i = 0; i < w; i++) {
/* 232 */       int b0 = rawData[(k++)] & mask;
/* 233 */       int b1 = (rawData[(k++)] & mask) << 8;
/* 234 */       int b2 = (rawData[(k++)] & mask) << 16;
/* 235 */       int b3 = (rawData[(k++)] & mask) << 24;
/* 236 */       intData[j] = (0xFF000000 | b0 | b1 | b2);
/* 237 */       j++;
/*     */     }
/*     */   }
/*     */ 
/*     */   void getPixelData()
/*     */     throws IOException, Exception
/*     */   {
/* 245 */     long skip = this.bitmapOffset - this.curPos;
/* 246 */     if (skip > 0L) {
/* 247 */       this.is.skip(skip);
/* 248 */       this.curPos = ((int)(this.curPos + skip));
/*     */     }
/*     */ 
/* 251 */     int len = this.scanLineSize;
/* 252 */     if (this.bitsPerPixel > 8)
/* 253 */       this.intData = new int[this.width * this.height];
/*     */     else
/* 255 */       this.byteData = new byte[this.width * this.height];
/* 256 */     byte[] rawData = new byte[this.actualSizeOfBitmap];
/* 257 */     int rawOffset = 0;
/* 258 */     int offset = (this.height - 1) * this.width;
/* 259 */     for (int i = this.height - 1; i >= 0; i--) {
/* 260 */       int n = this.is.read(rawData, rawOffset, len);
/* 261 */       if (n < len) throw new Exception("Scan line ended prematurely after " + n + " bytes");
/* 262 */       if (this.bitsPerPixel == 24)
/* 263 */         unpack24(rawData, rawOffset, this.intData, offset, this.width);
/* 264 */       else if (this.bitsPerPixel == 32)
/* 265 */         unpack32(rawData, rawOffset, this.intData, offset, this.width);
/*     */       else
/* 267 */         unpack(rawData, rawOffset, this.bitsPerPixel, this.byteData, offset, this.width);
/* 268 */       rawOffset += len;
/* 269 */       offset -= this.width;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void read(InputStream is) throws IOException, Exception
/*     */   {
/* 275 */     this.is = is;
/* 276 */     getFileHeader();
/* 277 */     getBitmapHeader();
/* 278 */     if (this.compression != 0)
/* 279 */       throw new Exception("Compression not supported");
/* 280 */     getPalette();
/* 281 */     getPixelData();
/*     */   }
/*     */ 
/*     */   public MemoryImageSource makeImageSource()
/*     */   {
/*     */     ColorModel cm;
/*     */     ColorModel cm;
/* 289 */     if ((this.noOfEntries > 0) && (this.bitsPerPixel != 24))
/*     */     {
/* 291 */       cm = new IndexColorModel(this.bitsPerPixel, this.noOfEntries, this.r, this.g, this.b);
/*     */     }
/*     */     else
/* 294 */       cm = ColorModel.getRGBdefault();
/*     */     MemoryImageSource mis;
/*     */     MemoryImageSource mis;
/* 299 */     if (this.bitsPerPixel > 8)
/*     */     {
/* 301 */       mis = new MemoryImageSource(this.width, this.height, cm, this.intData, 0, this.width);
/*     */     }
/*     */     else
/*     */     {
/* 305 */       mis = new MemoryImageSource(this.width, this.height, cm, this.byteData, 0, this.width);
/*     */     }
/*     */ 
/* 309 */     return mis;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.BMPDecoder
 * JD-Core Version:    0.6.2
 */