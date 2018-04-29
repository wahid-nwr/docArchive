/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.ImagePlus;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ class GifEncoder
/*     */ {
/*     */   int width;
/*     */   int height;
/*     */   boolean transparent;
/*     */   int transIndex;
/*  99 */   int repeat = 0;
/* 100 */   protected int delay = 50;
/* 101 */   boolean started = false;
/*     */   OutputStream out;
/*     */   ImagePlus image;
/*     */   byte[] pixels;
/*     */   byte[] indexedPixels;
/*     */   int colorDepth;
/*     */   byte[] colorTab;
/* 108 */   int lctSize = 7;
/* 109 */   int dispose = 0;
/* 110 */   boolean closeStream = false;
/* 111 */   boolean firstFrame = true;
/* 112 */   boolean sizeSet = false;
/* 113 */   int sample = 2;
/* 114 */   byte[] gct = null;
/* 115 */   boolean GCTextracted = false;
/* 116 */   boolean GCTloadedExternal = false;
/* 117 */   int GCTred = 0;
/* 118 */   int GCTgrn = 0;
/* 119 */   int GCTbl = 0;
/* 120 */   int GCTcindex = 0;
/* 121 */   boolean GCTsetTransparent = false;
/* 122 */   boolean GCToverideIndex = false;
/* 123 */   boolean GCToverideColor = false;
/*     */ 
/*     */   public boolean addFrame(ImagePlus image)
/*     */   {
/* 136 */     if ((image == null) || (!this.started)) return false;
/* 137 */     boolean ok = true;
/*     */     try {
/* 139 */       if (this.firstFrame) {
/* 140 */         if (!this.sizeSet)
/*     */         {
/* 142 */           setSize(image.getWidth(), image.getHeight());
/*     */         }
/* 144 */         writeLSD();
/* 145 */         if (this.repeat >= 0) writeNetscapeExt();
/* 146 */         this.firstFrame = false;
/*     */       }
/* 148 */       int bitDepth = image.getBitDepth();
/*     */ 
/* 151 */       Process8bitCLT(image);
/* 152 */       writeGraphicCtrlExt();
/* 153 */       writeImageDesc();
/* 154 */       writePalette();
/* 155 */       writePixels(); } catch (IOException e) {
/* 156 */       ok = false;
/* 157 */     }return ok;
/*     */   }
/*     */ 
/*     */   void Process8bitCLT(ImagePlus image)
/*     */   {
/* 187 */     this.colorDepth = 8;
/*     */ 
/* 189 */     ImageProcessor ip = image.getProcessor();
/* 190 */     ip = ip.convertToByte(true);
/* 191 */     ColorModel cm = ip.getColorModel();
/* 192 */     this.indexedPixels = ((byte[])ip.getPixels());
/* 193 */     IndexColorModel m = (IndexColorModel)cm;
/* 194 */     int mapSize = m.getMapSize();
/* 195 */     if (this.transIndex >= mapSize) {
/* 196 */       setTransparent(false);
/* 197 */       this.transIndex = 0;
/*     */     }
/*     */ 
/* 200 */     this.colorTab = new byte[mapSize * 3];
/* 201 */     for (int i = 0; i < mapSize; i++) {
/* 202 */       int k = i * 3;
/* 203 */       this.colorTab[k] = ((byte)m.getRed(i));
/* 204 */       this.colorTab[(k + 1)] = ((byte)m.getGreen(i));
/* 205 */       this.colorTab[(k + 2)] = ((byte)m.getBlue(i));
/*     */     }
/* 207 */     m.finalize();
/*     */   }
/*     */ 
/*     */   public boolean finish()
/*     */   {
/* 217 */     if (!this.started) return false;
/* 218 */     boolean ok = true;
/* 219 */     this.started = false;
/*     */     try {
/* 221 */       this.out.write(59);
/* 222 */       this.out.flush();
/* 223 */       if (this.closeStream)
/* 224 */         this.out.close(); 
/*     */     } catch (IOException e) { ok = false; }
/*     */ 
/*     */ 
/* 228 */     this.GCTextracted = false;
/* 229 */     this.GCTloadedExternal = false;
/* 230 */     this.transIndex = 0;
/* 231 */     this.transparent = false;
/* 232 */     this.gct = null;
/* 233 */     this.out = null;
/* 234 */     this.image = null;
/* 235 */     this.pixels = null;
/* 236 */     this.indexedPixels = null;
/* 237 */     this.colorTab = null;
/* 238 */     this.closeStream = false;
/* 239 */     this.firstFrame = true;
/*     */ 
/* 241 */     return ok;
/*     */   }
/*     */ 
/*     */   public void setDelay(int ms)
/*     */   {
/* 251 */     this.delay = Math.round(ms / 10.0F);
/*     */   }
/*     */ 
/*     */   public void setDispose(int code)
/*     */   {
/* 262 */     if (code >= 0)
/* 263 */       this.dispose = code;
/*     */   }
/*     */ 
/*     */   public void setFrameRate(float fps)
/*     */   {
/* 274 */     if (fps != 0.0F)
/* 275 */       this.delay = Math.round(100.0F / fps);
/*     */   }
/*     */ 
/*     */   public void setQuality(int quality)
/*     */   {
/* 292 */     if (quality < 1) quality = 1;
/* 293 */     this.sample = quality;
/*     */   }
/*     */ 
/*     */   public void setRepeat(int iter)
/*     */   {
/* 306 */     if (iter >= 0)
/* 307 */       this.repeat = iter;
/*     */   }
/*     */ 
/*     */   public void setSize(int w, int h)
/*     */   {
/* 320 */     if ((this.started) && (!this.firstFrame)) return;
/* 321 */     this.width = w;
/* 322 */     this.height = h;
/* 323 */     if (this.width < 1) this.width = 320;
/* 324 */     if (this.height < 1) this.height = 240;
/* 325 */     this.sizeSet = true;
/*     */   }
/*     */ 
/*     */   public void setTransparent(boolean c)
/*     */   {
/* 341 */     this.transparent = c;
/*     */   }
/*     */ 
/*     */   public boolean start(OutputStream os)
/*     */   {
/* 353 */     if (os == null) return false;
/* 354 */     boolean ok = true;
/* 355 */     this.closeStream = false;
/* 356 */     this.out = os;
/*     */     try {
/* 358 */       writeString("GIF89a"); } catch (IOException e) {
/* 359 */       ok = false;
/* 360 */     }return this.started = ok;
/*     */   }
/*     */ 
/*     */   public boolean start(String file)
/*     */   {
/* 371 */     boolean ok = true;
/*     */     try {
/* 373 */       this.out = new BufferedOutputStream(new FileOutputStream(file));
/* 374 */       ok = start(this.out);
/* 375 */       this.closeStream = true; } catch (IOException e) {
/* 376 */       ok = false;
/* 377 */     }return this.started = ok;
/*     */   }
/*     */ 
/*     */   public void OverRideQuality(int npixs)
/*     */   {
/* 384 */     if (npixs > 100000) this.sample = 10; else
/* 385 */       this.sample = (npixs / 10000);
/* 386 */     if (this.sample < 1) this.sample = 1;
/*     */   }
/*     */ 
/*     */   protected void writeGraphicCtrlExt()
/*     */     throws IOException
/*     */   {
/* 394 */     this.out.write(33);
/* 395 */     this.out.write(249);
/* 396 */     this.out.write(4);
/*     */     int disp;
/*     */     int transp;
/*     */     int disp;
/* 398 */     if (!this.transparent) {
/* 399 */       int transp = 0;
/* 400 */       disp = 0;
/*     */     } else {
/* 402 */       transp = 1;
/* 403 */       disp = 2;
/*     */     }
/* 405 */     if (this.dispose >= 0)
/* 406 */       disp = this.dispose & 0x7;
/* 407 */     disp <<= 2;
/*     */ 
/* 410 */     this.out.write(0x0 | disp | 0x0 | transp);
/*     */ 
/* 415 */     writeShort(this.delay);
/* 416 */     this.out.write(this.transIndex);
/* 417 */     this.out.write(0);
/*     */   }
/*     */ 
/*     */   protected void writeImageDesc()
/*     */     throws IOException
/*     */   {
/* 425 */     this.out.write(44);
/* 426 */     writeShort(0);
/* 427 */     writeShort(0);
/* 428 */     writeShort(this.width);
/* 429 */     writeShort(this.height);
/*     */ 
/* 431 */     this.out.write(0x80 | this.lctSize);
/*     */   }
/*     */ 
/*     */   protected void writeLSDgct()
/*     */     throws IOException
/*     */   {
/* 444 */     writeShort(this.width);
/* 445 */     writeShort(this.height);
/*     */ 
/* 447 */     this.out.write(0xF0 | this.lctSize);
/*     */ 
/* 452 */     this.out.write(0);
/* 453 */     this.out.write(0);
/*     */   }
/*     */ 
/*     */   protected void writeLSD()
/*     */     throws IOException
/*     */   {
/* 461 */     writeShort(this.width);
/* 462 */     writeShort(this.height);
/*     */ 
/* 464 */     this.out.write(112);
/*     */ 
/* 469 */     this.out.write(0);
/* 470 */     this.out.write(0);
/*     */   }
/*     */ 
/*     */   protected void writeNetscapeExt()
/*     */     throws IOException
/*     */   {
/* 479 */     this.out.write(33);
/* 480 */     this.out.write(255);
/* 481 */     this.out.write(11);
/* 482 */     writeString("NETSCAPE2.0");
/* 483 */     this.out.write(3);
/* 484 */     this.out.write(1);
/* 485 */     writeShort(this.repeat);
/* 486 */     this.out.write(0);
/*     */   }
/*     */ 
/*     */   protected void writePalette()
/*     */     throws IOException
/*     */   {
/* 494 */     this.out.write(this.colorTab, 0, this.colorTab.length);
/* 495 */     int n = 768 - this.colorTab.length;
/* 496 */     for (int i = 0; i < n; i++)
/* 497 */       this.out.write(0);
/*     */   }
/*     */ 
/*     */   protected void writePixels()
/*     */     throws IOException
/*     */   {
/* 505 */     LZWEncoder encoder = new LZWEncoder(this.width, this.height, this.indexedPixels, this.colorDepth);
/* 506 */     encoder.encode(this.out);
/*     */   }
/*     */ 
/*     */   protected void writeShort(int value)
/*     */     throws IOException
/*     */   {
/* 514 */     this.out.write(value & 0xFF);
/* 515 */     this.out.write(value >> 8 & 0xFF);
/*     */   }
/*     */ 
/*     */   protected void writeString(String s)
/*     */     throws IOException
/*     */   {
/* 523 */     for (int i = 0; i < s.length(); i++)
/* 524 */       this.out.write((byte)s.charAt(i));
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.GifEncoder
 * JD-Core Version:    0.6.2
 */