/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.LookUpTable;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.IndexColorModel;
/*     */ 
/*     */ public class LutViewer
/*     */   implements PlugInFilter
/*     */ {
/*     */   ImagePlus imp;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  16 */     this.imp = imp;
/*  17 */     return 399;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  21 */     int xMargin = 35;
/*  22 */     int yMargin = 20;
/*  23 */     int width = 256;
/*  24 */     int height = 128;
/*     */ 
/*  27 */     int barHeight = 12;
/*     */ 
/*  31 */     ip = this.imp.getChannelProcessor();
/*  32 */     IndexColorModel cm = (IndexColorModel)ip.getColorModel();
/*  33 */     LookUpTable lut = new LookUpTable(cm);
/*  34 */     int mapSize = lut.getMapSize();
/*  35 */     byte[] reds = lut.getReds();
/*  36 */     byte[] greens = lut.getGreens();
/*  37 */     byte[] blues = lut.getBlues();
/*  38 */     boolean isGray = lut.isGrayscale();
/*     */ 
/*  40 */     int imageWidth = width + 2 * xMargin;
/*  41 */     int imageHeight = height + 3 * yMargin;
/*  42 */     Image img = IJ.getInstance().createImage(imageWidth, imageHeight);
/*  43 */     Graphics g = img.getGraphics();
/*  44 */     g.setColor(Color.white);
/*  45 */     g.fillRect(0, 0, imageWidth, imageHeight);
/*  46 */     g.setColor(Color.black);
/*  47 */     g.drawRect(xMargin, yMargin, width, height);
/*     */ 
/*  49 */     double scale = 256.0D / mapSize;
/*  50 */     if (isGray)
/*  51 */       g.setColor(Color.black);
/*     */     else
/*  53 */       g.setColor(Color.red);
/*  54 */     int x1 = xMargin;
/*  55 */     int y1 = yMargin + height - (reds[0] & 0xFF) / 2;
/*  56 */     for (int i = 1; i < 256; i++) {
/*  57 */       int x2 = xMargin + i;
/*  58 */       int y2 = yMargin + height - (reds[((int)(i / scale))] & 0xFF) / 2;
/*  59 */       g.drawLine(x1, y1, x2, y2);
/*  60 */       x1 = x2;
/*  61 */       y1 = y2;
/*     */     }
/*     */ 
/*  64 */     if (!isGray) {
/*  65 */       g.setColor(Color.green);
/*  66 */       x1 = xMargin;
/*  67 */       y1 = yMargin + height - (greens[0] & 0xFF) / 2;
/*  68 */       for (int i = 1; i < 256; i++) {
/*  69 */         int x2 = xMargin + i;
/*  70 */         int y2 = yMargin + height - (greens[((int)(i / scale))] & 0xFF) / 2;
/*  71 */         g.drawLine(x1, y1, x2, y2);
/*  72 */         x1 = x2;
/*  73 */         y1 = y2;
/*     */       }
/*     */     }
/*     */ 
/*  77 */     if (!isGray) {
/*  78 */       g.setColor(Color.blue);
/*  79 */       x1 = xMargin;
/*  80 */       y1 = yMargin + height - (blues[0] & 0xFF) / 2;
/*  81 */       for (int i = 1; i < 255; i++) {
/*  82 */         int x2 = xMargin + i;
/*  83 */         int y2 = yMargin + height - (blues[((int)(i / scale))] & 0xFF) / 2;
/*  84 */         g.drawLine(x1, y1, x2, y2);
/*  85 */         x1 = x2;
/*  86 */         y1 = y2;
/*     */       }
/*     */     }
/*     */ 
/*  90 */     int x = xMargin;
/*  91 */     int y = yMargin + height + 2;
/*  92 */     lut.drawColorBar(g, x, y, 256, barHeight);
/*     */ 
/*  94 */     y += barHeight + 15;
/*  95 */     g.setColor(Color.black);
/*  96 */     g.drawString("0", x - 4, y);
/*  97 */     g.drawString("" + (mapSize - 1), x + width - 10, y);
/*  98 */     g.drawString("255", 7, yMargin + 4);
/*  99 */     g.dispose();
/*     */ 
/* 101 */     ImagePlus imp = new ImagePlus("Look-Up Table", img);
/*     */ 
/* 103 */     new LutWindow(imp, new ImageCanvas(imp), ip);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.LutViewer
 * JD-Core Version:    0.6.2
 */