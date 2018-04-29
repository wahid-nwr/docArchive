/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.ImageStack;
/*    */ import ij.measure.Calibration;
/*    */ import ij.process.ImageProcessor;
/*    */ import ij.process.StackProcessor;
/*    */ 
/*    */ public class Transformer
/*    */   implements PlugInFilter
/*    */ {
/*    */   ImagePlus imp;
/*    */   String arg;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 16 */     this.arg = arg;
/* 17 */     this.imp = imp;
/* 18 */     if ((arg.equals("fliph")) || (arg.equals("flipv"))) {
/* 19 */       return IJ.setupDialog(imp, 287);
/*    */     }
/* 21 */     return 415;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/* 25 */     if (this.arg.equals("fliph")) {
/* 26 */       ip.flipHorizontal();
/* 27 */       return;
/*    */     }
/* 29 */     if (this.arg.equals("flipv")) {
/* 30 */       ip.flipVertical();
/* 31 */       return;
/*    */     }
/* 33 */     if ((this.arg.equals("right")) || (this.arg.equals("left"))) {
/* 34 */       StackProcessor sp = new StackProcessor(this.imp.getStack(), ip);
/* 35 */       ImageStack s2 = null;
/* 36 */       if (this.arg.equals("right"))
/* 37 */         s2 = sp.rotateRight();
/*    */       else
/* 39 */         s2 = sp.rotateLeft();
/* 40 */       Calibration cal = this.imp.getCalibration();
/* 41 */       this.imp.setStack(null, s2);
/* 42 */       double pixelWidth = cal.pixelWidth;
/* 43 */       cal.pixelWidth = cal.pixelHeight;
/* 44 */       cal.pixelHeight = pixelWidth;
/* 45 */       return;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Transformer
 * JD-Core Version:    0.6.2
 */