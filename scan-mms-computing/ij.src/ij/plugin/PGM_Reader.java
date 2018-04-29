/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.io.FileInfo;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.StreamTokenizer;
/*     */ 
/*     */ public class PGM_Reader extends ImagePlus
/*     */   implements PlugIn
/*     */ {
/*     */   private int width;
/*     */   private int height;
/*     */   private boolean rawBits;
/*     */   private boolean sixteenBits;
/*     */   private boolean isColor;
/*     */   private boolean isBlackWhite;
/*     */   private int maxValue;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  73 */     OpenDialog od = new OpenDialog("PBM/PGM/PPM Reader...", arg);
/*  74 */     String directory = od.getDirectory();
/*  75 */     String name = od.getFileName();
/*  76 */     if (name == null)
/*  77 */       return; String path = directory + name;
/*     */ 
/*  80 */     IJ.showStatus("Opening: " + path);
/*     */     ImageStack stack;
/*     */     try { stack = openFile(path);
/*     */     } catch (IOException e)
/*     */     {
/*  86 */       String msg = e.getMessage();
/*  87 */       IJ.showMessage("PBM/PGM/PPM Reader", msg.equals("") ? "" + e : msg);
/*  88 */       return;
/*     */     }
/*  90 */     setStack(name, stack);
/*  91 */     FileInfo fi = new FileInfo();
/*  92 */     fi.fileFormat = 8;
/*  93 */     fi.directory = directory;
/*  94 */     fi.fileName = name;
/*  95 */     setFileInfo(fi);
/*  96 */     if (arg.equals("")) show(); 
/*     */   }
/*     */ 
/*     */   public ImageStack openFile(String path) throws IOException {
/* 100 */     InputStream is = new BufferedInputStream(new FileInputStream(path));
/*     */     try {
/* 102 */       StreamTokenizer tok = new StreamTokenizer(is);
/*     */ 
/* 105 */       tok.resetSyntax();
/* 106 */       tok.wordChars(33, 255);
/* 107 */       tok.whitespaceChars(0, 32);
/* 108 */       tok.parseNumbers();
/* 109 */       tok.eolIsSignificant(true);
/* 110 */       tok.commentChar(35);
/* 111 */       openHeader(tok);
/*     */ 
/* 114 */       if ((!this.isColor) && (this.sixteenBits))
/*     */       {
/*     */         ImageStack localImageStack1;
/* 115 */         if (this.rawBits) {
/* 116 */           ImageProcessor ip = open16bitRawImage(is, this.width, this.height);
/* 117 */           ImageStack stack = new ImageStack(this.width, this.height);
/* 118 */           stack.addSlice("", ip);
/* 119 */           return stack;
/*     */         }
/* 121 */         ImageProcessor ip = open16bitAsciiImage(tok, this.width, this.height);
/* 122 */         ImageStack stack = new ImageStack(this.width, this.height);
/* 123 */         stack.addSlice("", ip);
/* 124 */         return stack;
/*     */       }
/*     */ 
/* 128 */       if (!this.isColor) {
/* 129 */         byte[] pixels = new byte[this.width * this.height];
/* 130 */         ImageProcessor ip = new ByteProcessor(this.width, this.height, pixels, null);
/* 131 */         if (this.rawBits)
/* 132 */           openRawImage(is, this.width * this.height, pixels);
/*     */         else
/* 134 */           openAsciiImage(tok, this.width * this.height, pixels);
/*     */         int bit;
/* 135 */         for (int i = pixels.length - 1; i >= 0; i--) {
/* 136 */           if (this.isBlackWhite) {
/* 137 */             if (this.rawBits) {
/* 138 */               if (i < pixels.length / 8) {
/* 139 */                 for (bit = 7; bit >= 0; bit--)
/* 140 */                   pixels[(8 * i + 7 - bit)] = ((byte)((pixels[i] & (int)Math.pow(2.0D, bit)) == 0 ? 'ÿ' : 0));
/*     */               }
/*     */             }
/*     */             else
/* 144 */               pixels[i] = ((byte)(pixels[i] == 0 ? 'ÿ' : 0));
/*     */           }
/* 146 */           else pixels[i] = ((byte)(0xFF & 255 * (0xFF & pixels[i]) / this.maxValue));
/*     */         }
/* 148 */         ImageStack stack = new ImageStack(this.width, this.height);
/* 149 */         stack.addSlice("", ip);
/* 150 */         return stack;
/*     */       }
/*     */ 
/* 153 */       if (!this.sixteenBits) {
/* 154 */         int[] pixels = new int[this.width * this.height];
/* 155 */         byte[] bytePixels = new byte[3 * this.width * this.height];
/* 156 */         Object ip = new ColorProcessor(this.width, this.height, pixels);
/* 157 */         if (this.rawBits)
/* 158 */           openRawImage(is, 3 * this.width * this.height, bytePixels);
/*     */         else
/* 160 */           openAsciiImage(tok, 3 * this.width * this.height, bytePixels);
/*     */         int r;
/* 161 */         for (int i = 0; i < this.width * this.height; i++) {
/* 162 */           r = 0xFF & bytePixels[(i * 3)];
/* 163 */           int g = 0xFF & bytePixels[(i * 3 + 1)];
/* 164 */           int b = 0xFF & bytePixels[(i * 3 + 2)];
/* 165 */           r = r * 255 / this.maxValue << 16;
/* 166 */           g = g * 255 / this.maxValue << 8;
/* 167 */           b = b * 255 / this.maxValue;
/* 168 */           pixels[i] = (0xFF000000 | r | g | b);
/*     */         }
/* 170 */         ImageStack stack = new ImageStack(this.width, this.height);
/* 171 */         stack.addSlice("", (ImageProcessor)ip);
/* 172 */         return stack;
/*     */       }
/*     */ 
/* 176 */       short[] red = new short[this.width * this.height];
/* 177 */       short[] green = new short[this.width * this.height];
/* 178 */       short[] blue = new short[this.width * this.height];
/*     */       short[] pixels;
/* 179 */       if (this.rawBits) {
/* 180 */         byte[] bytePixels = new byte[6 * this.width * this.height];
/* 181 */         openRawImage(is, 6 * this.width * this.height, bytePixels);
/* 182 */         for (int i = 0; i < this.width * this.height; i++) {
/* 183 */           int r1 = 0xFF & bytePixels[(i * 6)];
/* 184 */           int r2 = 0xFF & bytePixels[(i * 6 + 1)];
/* 185 */           int g1 = 0xFF & bytePixels[(i * 6 + 2)];
/* 186 */           int g2 = 0xFF & bytePixels[(i * 6 + 3)];
/* 187 */           int b1 = 0xFF & bytePixels[(i * 6 + 4)];
/* 188 */           int b2 = 0xFF & bytePixels[(i * 6 + 5)];
/* 189 */           red[i] = ((short)(0xFFFF & r1 * 256 + r2));
/* 190 */           green[i] = ((short)(0xFFFF & g1 * 256 + g2));
/* 191 */           blue[i] = ((short)(0xFFFF & b1 * 256 + b2));
/*     */         }
/*     */       } else {
/* 194 */         ImageProcessor ip = open16bitAsciiImage(tok, 3 * this.width, this.height);
/* 195 */         pixels = (short[])ip.getPixels();
/* 196 */         for (int i = 0; i < this.width * this.height; i++) {
/* 197 */           red[i] = ((short)(pixels[(i * 3)] & 0xFFFFFF));
/* 198 */           green[i] = ((short)(pixels[(i * 3 + 1)] & 0xFFFFFF));
/* 199 */           blue[i] = ((short)(pixels[(i * 3 + 2)] & 0xFFFFFF));
/*     */         }
/*     */       }
/* 202 */       ImageStack stack = new ImageStack(this.width, this.height);
/* 203 */       stack.addSlice("red", new ShortProcessor(this.width, this.height, red, null));
/* 204 */       stack.addSlice("green", new ShortProcessor(this.width, this.height, green, null));
/* 205 */       stack.addSlice("blue", new ShortProcessor(this.width, this.height, blue, null));
/* 206 */       return stack;
/*     */     } finally {
/* 208 */       if (is != null) is.close(); 
/*     */     }
/*     */   }
/*     */ 
/*     */   public void openHeader(StreamTokenizer tok) throws IOException {
/* 213 */     String magicNumber = getWord(tok);
/* 214 */     if (magicNumber.equals("P1")) {
/* 215 */       this.rawBits = false;
/* 216 */       this.isColor = false;
/* 217 */       this.isBlackWhite = true;
/* 218 */     } else if (magicNumber.equals("P4")) {
/* 219 */       this.rawBits = true;
/* 220 */       this.isColor = false;
/* 221 */       this.isBlackWhite = true;
/* 222 */     } else if (magicNumber.equals("P2")) {
/* 223 */       this.rawBits = false;
/* 224 */       this.isColor = false;
/* 225 */       this.isBlackWhite = false;
/* 226 */     } else if (magicNumber.equals("P5")) {
/* 227 */       this.rawBits = true;
/* 228 */       this.isColor = false;
/* 229 */       this.isBlackWhite = false;
/* 230 */     } else if (magicNumber.equals("P3")) {
/* 231 */       this.rawBits = false;
/* 232 */       this.isColor = true;
/* 233 */       this.isBlackWhite = false;
/* 234 */     } else if (magicNumber.equals("P6")) {
/* 235 */       this.rawBits = true;
/* 236 */       this.isColor = true;
/* 237 */       this.isBlackWhite = false;
/*     */     } else {
/* 239 */       throw new IOException("PxM files must start with \"P1\" or \"P2\" or \"P3\" or \"P4\" or \"P5\" or \"P6\"");
/* 240 */     }this.width = getInt(tok);
/* 241 */     this.height = getInt(tok);
/* 242 */     if ((this.width == -1) || (this.height == -1))
/* 243 */       throw new IOException("Error opening PxM header..");
/* 244 */     if (!this.isBlackWhite) {
/* 245 */       this.maxValue = getInt(tok);
/* 246 */       if (this.maxValue == -1)
/* 247 */         throw new IOException("Error opening PxM header..");
/* 248 */       this.sixteenBits = (this.maxValue > 255);
/*     */     } else {
/* 250 */       this.maxValue = 255;
/* 251 */     }if ((this.sixteenBits) && (this.maxValue > 65535))
/* 252 */       throw new IOException("The maximum gray value is larger than 65535.");
/*     */   }
/*     */ 
/*     */   public void openAsciiImage(StreamTokenizer tok, int size, byte[] pixels) throws IOException {
/* 256 */     int i = 0;
/* 257 */     int inc = size / 20;
/* 258 */     if (inc == 0) inc = 1;
/* 259 */     while (tok.nextToken() != -1) {
/* 260 */       if (tok.ttype == -2) {
/* 261 */         pixels[(i++)] = ((byte)((int)tok.nval & 0xFF));
/* 262 */         if (i % inc == 0)
/* 263 */           IJ.showProgress(0.5D + i / size / 2.0D);
/*     */       }
/*     */     }
/* 266 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   public void openRawImage(InputStream is, int size, byte[] pixels) throws IOException {
/* 270 */     int count = 0;
/* 271 */     while ((count < size) && (count >= 0))
/* 272 */       count = is.read(pixels, count, size - count);
/*     */   }
/*     */ 
/*     */   public ImageProcessor open16bitRawImage(InputStream is, int width, int height) throws IOException {
/* 276 */     int size = width * height * 2;
/* 277 */     byte[] bytes = new byte[size];
/* 278 */     int count = 0;
/* 279 */     while ((count < size) && (count >= 0))
/* 280 */       count = is.read(bytes, count, size - count);
/* 281 */     short[] pixels = new short[size / 2];
/* 282 */     int i = 0; for (int j = 0; i < size / 2; j += 2) {
/* 283 */       pixels[i] = ((short)((bytes[j] & 0xFF) << 8 | bytes[(j + 1)] & 0xFF));
/*     */ 
/* 282 */       i++;
/*     */     }
/* 284 */     return new ShortProcessor(width, height, pixels, null);
/*     */   }
/*     */ 
/*     */   public ImageProcessor open16bitAsciiImage(StreamTokenizer tok, int width, int height) throws IOException {
/* 288 */     int i = 0;
/* 289 */     int size = width * height;
/* 290 */     int inc = size / 20;
/* 291 */     if (inc == 0) inc = 1;
/* 292 */     short[] pixels = new short[size];
/* 293 */     while (tok.nextToken() != -1) {
/* 294 */       if (tok.ttype == -2) {
/* 295 */         pixels[(i++)] = ((short)((int)tok.nval & 0xFFFF));
/* 296 */         if (i % inc == 0)
/* 297 */           IJ.showProgress(0.5D + i / size / 2.0D);
/*     */       }
/*     */     }
/* 300 */     IJ.showProgress(1.0D);
/* 301 */     return new ShortProcessor(width, height, pixels, null);
/*     */   }
/*     */ 
/*     */   String getWord(StreamTokenizer tok) throws IOException {
/* 305 */     while (tok.nextToken() != -1) {
/* 306 */       if (tok.ttype == -3)
/* 307 */         return tok.sval;
/*     */     }
/* 309 */     return null;
/*     */   }
/*     */ 
/*     */   int getInt(StreamTokenizer tok) throws IOException {
/* 313 */     while (tok.nextToken() != -1) {
/* 314 */       if (tok.ttype == -2)
/* 315 */         return (int)tok.nval;
/*     */     }
/* 317 */     return -1;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.PGM_Reader
 * JD-Core Version:    0.6.2
 */