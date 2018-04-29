/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.process.ImageProcessor;
/*    */ 
/*    */ public class Shadows
/*    */   implements PlugInFilter
/*    */ {
/*    */   String arg;
/*    */   ImagePlus imp;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 14 */     this.arg = arg;
/* 15 */     this.imp = imp;
/* 16 */     if ((imp != null) && (imp.getStackSize() > 1) && (arg.equals("demo"))) {
/* 17 */       IJ.error("This demo does not work with stacks."); return 4096;
/* 18 */     }return IJ.setupDialog(imp, 95);
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/* 22 */     if (this.arg.equals("demo")) {
/* 23 */       IJ.resetEscape();
/*    */ 
/* 32 */       for (; !IJ.escapePressed(); 
/* 32 */         ip.reset())
/*    */       {
/* 25 */         north(ip); this.imp.updateAndDraw(); ip.reset();
/* 26 */         northeast(ip); this.imp.updateAndDraw(); ip.reset();
/* 27 */         east(ip); this.imp.updateAndDraw(); ip.reset();
/* 28 */         southeast(ip); this.imp.updateAndDraw(); ip.reset();
/* 29 */         south(ip); this.imp.updateAndDraw(); ip.reset();
/* 30 */         southwest(ip); this.imp.updateAndDraw(); ip.reset();
/* 31 */         west(ip); this.imp.updateAndDraw(); ip.reset();
/* 32 */         northwest(ip); this.imp.updateAndDraw();
/*    */       }
/*    */     }
/* 35 */     if (this.arg.equals("north")) north(ip);
/* 36 */     else if (this.arg.equals("northeast")) northeast(ip);
/* 37 */     else if (this.arg.equals("east")) east(ip);
/* 38 */     else if (this.arg.equals("southeast")) southeast(ip);
/* 39 */     else if (this.arg.equals("south")) south(ip);
/* 40 */     else if (this.arg.equals("southwest")) southwest(ip);
/* 41 */     else if (this.arg.equals("west")) west(ip);
/* 42 */     else if (this.arg.equals("northwest")) northwest(ip);
/*    */   }
/*    */ 
/*    */   public void north(ImageProcessor ip)
/*    */   {
/* 48 */     int[] kernel = { 1, 2, 1, 0, 1, 0, -1, -2, -1 };
/* 49 */     ip.convolve3x3(kernel);
/*    */   }
/*    */ 
/*    */   public void south(ImageProcessor ip) {
/* 53 */     int[] kernel = { -1, -2, -1, 0, 1, 0, 1, 2, 1 };
/* 54 */     ip.convolve3x3(kernel);
/*    */   }
/*    */ 
/*    */   public void east(ImageProcessor ip) {
/* 58 */     int[] kernel = { -1, 0, 1, -2, 1, 2, -1, 0, 1 };
/* 59 */     ip.convolve3x3(kernel);
/*    */   }
/*    */ 
/*    */   public void west(ImageProcessor ip) {
/* 63 */     int[] kernel = { 1, 0, -1, 2, 1, -2, 1, 0, -1 };
/* 64 */     ip.convolve3x3(kernel);
/*    */   }
/*    */ 
/*    */   public void northwest(ImageProcessor ip) {
/* 68 */     int[] kernel = { 2, 1, 0, 1, 1, -1, 0, -1, -2 };
/* 69 */     ip.convolve3x3(kernel);
/*    */   }
/*    */ 
/*    */   public void southeast(ImageProcessor ip) {
/* 73 */     int[] kernel = { -2, -1, 0, -1, 1, 1, 0, 1, 2 };
/* 74 */     ip.convolve3x3(kernel);
/*    */   }
/*    */ 
/*    */   public void northeast(ImageProcessor ip) {
/* 78 */     int[] kernel = { 0, 1, 2, -1, 1, 1, -2, -1, 0 };
/* 79 */     ip.convolve3x3(kernel);
/*    */   }
/*    */ 
/*    */   public void southwest(ImageProcessor ip) {
/* 83 */     int[] kernel = { 0, -1, -2, 1, 1, -1, 2, 1, 0 };
/* 84 */     ip.convolve3x3(kernel);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Shadows
 * JD-Core Version:    0.6.2
 */