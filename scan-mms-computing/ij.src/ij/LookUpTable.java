/*     */ package ij;
/*     */ 
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.PixelGrabber;
/*     */ 
/*     */ public class LookUpTable
/*     */ {
/*     */   private int width;
/*     */   private int height;
/*     */   private byte[] pixels;
/*  10 */   private int mapSize = 0;
/*     */   private ColorModel cm;
/*     */   private byte[] rLUT;
/*     */   private byte[] gLUT;
/*     */   private byte[] bLUT;
/*     */ 
/*     */   public LookUpTable(Image img)
/*     */   {
/*  16 */     PixelGrabber pg = new PixelGrabber(img, 0, 0, 1, 1, false);
/*     */     try {
/*  18 */       pg.grabPixels();
/*  19 */       this.cm = pg.getColorModel();
/*     */     } catch (InterruptedException e) {
/*     */     }
/*  22 */     getColors(this.cm);
/*     */   }
/*     */ 
/*     */   public LookUpTable(ColorModel cm)
/*     */   {
/*  27 */     this.cm = cm;
/*  28 */     getColors(cm);
/*     */   }
/*     */ 
/*     */   void getColors(ColorModel cm) {
/*  32 */     if ((cm instanceof IndexColorModel)) {
/*  33 */       IndexColorModel m = (IndexColorModel)cm;
/*  34 */       this.mapSize = m.getMapSize();
/*  35 */       this.rLUT = new byte[this.mapSize];
/*  36 */       this.gLUT = new byte[this.mapSize];
/*  37 */       this.bLUT = new byte[this.mapSize];
/*  38 */       m.getReds(this.rLUT);
/*  39 */       m.getGreens(this.gLUT);
/*  40 */       m.getBlues(this.bLUT);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMapSize() {
/*  45 */     return this.mapSize;
/*     */   }
/*     */ 
/*     */   public byte[] getReds() {
/*  49 */     return this.rLUT;
/*     */   }
/*     */ 
/*     */   public byte[] getGreens() {
/*  53 */     return this.gLUT;
/*     */   }
/*     */ 
/*     */   public byte[] getBlues() {
/*  57 */     return this.bLUT;
/*     */   }
/*     */ 
/*     */   public ColorModel getColorModel() {
/*  61 */     return this.cm;
/*     */   }
/*     */ 
/*     */   public boolean isGrayscale()
/*     */   {
/*  68 */     boolean isGray = true;
/*     */ 
/*  70 */     if (this.mapSize < 256)
/*  71 */       return false;
/*  72 */     for (int i = 0; i < this.mapSize; i++)
/*  73 */       if ((this.rLUT[i] != this.gLUT[i]) || (this.gLUT[i] != this.bLUT[i]))
/*  74 */         isGray = false;
/*  75 */     return isGray;
/*     */   }
/*     */ 
/*     */   public void drawColorBar(Graphics g, int x, int y, int width, int height) {
/*  79 */     if (this.mapSize == 0)
/*  80 */       return;
/*  81 */     ColorProcessor cp = new ColorProcessor(width, height);
/*  82 */     double scale = 256.0D / this.mapSize;
/*  83 */     for (int i = 0; i < 256; i++) {
/*  84 */       int index = (int)(i / scale);
/*  85 */       cp.setColor(new Color(this.rLUT[index] & 0xFF, this.gLUT[index] & 0xFF, this.bLUT[index] & 0xFF));
/*  86 */       cp.moveTo(i, 0); cp.lineTo(i, height);
/*     */     }
/*  88 */     g.drawImage(cp.createImage(), x, y, null);
/*  89 */     g.setColor(Color.black);
/*  90 */     g.drawRect(x, y, width, height);
/*     */   }
/*     */ 
/*     */   public void drawUnscaledColorBar(ImageProcessor ip, int x, int y, int width, int height) {
/*  94 */     ImageProcessor bar = null;
/*  95 */     if ((ip instanceof ColorProcessor))
/*  96 */       bar = new ColorProcessor(width, height);
/*     */     else
/*  98 */       bar = new ByteProcessor(width, height);
/*  99 */     if (this.mapSize == 0) {
/* 100 */       for (int i = 0; i < 256; i++) {
/* 101 */         bar.setColor(new Color(i, i, i));
/* 102 */         bar.moveTo(i, 0); bar.lineTo(i, height);
/*     */       }
/*     */     }
/*     */     else {
/* 106 */       for (int i = 0; i < this.mapSize; i++) {
/* 107 */         bar.setColor(new Color(this.rLUT[i] & 0xFF, this.gLUT[i] & 0xFF, this.bLUT[i] & 0xFF));
/* 108 */         bar.moveTo(i, 0); bar.lineTo(i, height);
/*     */       }
/*     */     }
/* 111 */     ip.insert(bar, x, y);
/* 112 */     ip.setColor(Color.black);
/* 113 */     ip.drawRect(x - 1, y, width + 2, height);
/*     */   }
/*     */ 
/*     */   public static ColorModel createGrayscaleColorModel(boolean invert) {
/* 117 */     byte[] rLUT = new byte[256];
/* 118 */     byte[] gLUT = new byte[256];
/* 119 */     byte[] bLUT = new byte[256];
/* 120 */     if (invert)
/* 121 */       for (int i = 0; i < 256; i++) {
/* 122 */         rLUT[(255 - i)] = ((byte)i);
/* 123 */         gLUT[(255 - i)] = ((byte)i);
/* 124 */         bLUT[(255 - i)] = ((byte)i);
/*     */       }
/*     */     else {
/* 127 */       for (int i = 0; i < 256; i++) {
/* 128 */         rLUT[i] = ((byte)i);
/* 129 */         gLUT[i] = ((byte)i);
/* 130 */         bLUT[i] = ((byte)i);
/*     */       }
/*     */     }
/* 133 */     return new IndexColorModel(8, 256, rLUT, gLUT, bLUT);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.LookUpTable
 * JD-Core Version:    0.6.2
 */