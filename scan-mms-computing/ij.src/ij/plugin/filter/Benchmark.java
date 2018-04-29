/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.process.ImageProcessor;
/*    */ 
/*    */ public class Benchmark
/*    */   implements PlugInFilter
/*    */ {
/*    */   String arg;
/*    */   ImagePlus imp;
/* 14 */   boolean showUpdates = true;
/*    */   int counter;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 18 */     this.imp = imp;
/* 19 */     return 16543;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/* 23 */     Thread.currentThread().setPriority(1);
/* 24 */     ip.setInterpolate(false);
/* 25 */     for (int i = 0; i < 4; i++) {
/* 26 */       ip.invert();
/* 27 */       updateScreen(this.imp);
/*    */     }
/* 29 */     for (int i = 0; i < 4; i++) {
/* 30 */       ip.flipVertical();
/* 31 */       updateScreen(this.imp);
/*    */     }
/* 33 */     ip.flipHorizontal(); updateScreen(this.imp);
/* 34 */     ip.flipHorizontal(); updateScreen(this.imp);
/* 35 */     for (int i = 0; i < 6; i++) {
/* 36 */       ip.smooth();
/* 37 */       updateScreen(this.imp);
/*    */     }
/* 39 */     ip.reset();
/* 40 */     for (int i = 0; i < 6; i++) {
/* 41 */       ip.sharpen();
/* 42 */       updateScreen(this.imp);
/*    */     }
/* 44 */     ip.reset();
/* 45 */     ip.smooth(); updateScreen(this.imp);
/* 46 */     ip.findEdges(); updateScreen(this.imp);
/* 47 */     ip.invert(); updateScreen(this.imp);
/* 48 */     ip.autoThreshold(); updateScreen(this.imp);
/* 49 */     ip.reset();
/* 50 */     ip.medianFilter(); updateScreen(this.imp);
/* 51 */     for (int i = 0; i < 360; i += 15) {
/* 52 */       ip.reset();
/* 53 */       ip.rotate(i);
/* 54 */       updateScreen(this.imp);
/*    */     }
/* 56 */     double scale = 1.5D;
/* 57 */     for (int i = 0; i < 8; i++) {
/* 58 */       ip.reset();
/* 59 */       ip.scale(scale, scale);
/* 60 */       updateScreen(this.imp);
/* 61 */       scale *= 1.5D;
/*    */     }
/* 63 */     for (int i = 0; i < 12; i++) {
/* 64 */       ip.reset();
/* 65 */       scale /= 1.5D;
/* 66 */       ip.scale(scale, scale);
/* 67 */       updateScreen(this.imp);
/*    */     }
/* 69 */     ip.reset();
/* 70 */     updateScreen(this.imp);
/*    */   }
/*    */ 
/*    */   void updateScreen(ImagePlus imp) {
/* 74 */     if (this.showUpdates)
/* 75 */       imp.updateAndDraw();
/* 76 */     IJ.showStatus(this.counter++ + "/" + 72);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Benchmark
 * JD-Core Version:    0.6.2
 */