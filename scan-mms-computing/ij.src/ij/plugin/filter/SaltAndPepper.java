/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.Rectangle;
/*    */ import java.util.Random;
/*    */ 
/*    */ public class SaltAndPepper
/*    */   implements PlugInFilter
/*    */ {
/* 10 */   Random r = new Random();
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp) {
/* 13 */     return IJ.setupDialog(imp, 67);
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/* 17 */     add(ip, 0.05D);
/*    */   }
/*    */ 
/*    */   public int rand(int min, int max) {
/* 21 */     return min + (int)(this.r.nextDouble() * (max - min));
/*    */   }
/*    */ 
/*    */   public void add(ImageProcessor ip, double percent) {
/* 25 */     Rectangle roi = ip.getRoi();
/* 26 */     int n = (int)(percent * roi.width * roi.height);
/* 27 */     byte[] pixels = (byte[])ip.getPixels();
/*    */ 
/* 29 */     int width = ip.getWidth();
/* 30 */     int xmin = roi.x;
/* 31 */     int xmax = roi.x + roi.width - 1;
/* 32 */     int ymin = roi.y;
/* 33 */     int ymax = roi.y + roi.height - 1;
/*    */ 
/* 35 */     for (int i = 0; i < n / 2; i++) {
/* 36 */       int rx = rand(xmin, xmax);
/* 37 */       int ry = rand(ymin, ymax);
/* 38 */       pixels[(ry * width + rx)] = -1;
/* 39 */       rx = rand(xmin, xmax);
/* 40 */       ry = rand(ymin, ymax);
/* 41 */       pixels[(ry * width + rx)] = 0;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.SaltAndPepper
 * JD-Core Version:    0.6.2
 */