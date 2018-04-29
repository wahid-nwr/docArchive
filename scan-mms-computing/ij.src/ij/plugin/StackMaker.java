/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.ImageStack;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.process.ImageProcessor;
/*    */ import ij.process.StackProcessor;
/*    */ 
/*    */ public class StackMaker
/*    */   implements PlugIn
/*    */ {
/* 13 */   private static int w = 2; private static int h = 2; private static int b = 0;
/*    */ 
/*    */   public void run(String arg) {
/* 16 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 17 */     if (imp == null) {
/* 18 */       IJ.noImage(); return;
/* 19 */     }if (imp.getStackSize() > 1) {
/* 20 */       IJ.error("This command requires a montage"); return;
/* 21 */     }GenericDialog gd = new GenericDialog("Stack Maker");
/* 22 */     gd.addNumericField("Images_per_row: ", w, 0);
/* 23 */     gd.addNumericField("Images_per_column: ", h, 0);
/* 24 */     gd.addNumericField("Border width: ", b, 0);
/* 25 */     gd.showDialog();
/* 26 */     if (gd.wasCanceled())
/* 27 */       return;
/* 28 */     w = (int)gd.getNextNumber();
/* 29 */     h = (int)gd.getNextNumber();
/* 30 */     b = (int)gd.getNextNumber();
/* 31 */     ImageStack stack = makeStack(imp.getProcessor(), w, h, b);
/* 32 */     new ImagePlus("Stack", stack).show();
/*    */   }
/*    */ 
/*    */   public ImageStack makeStack(ImageProcessor ip, int w, int h, int b) {
/* 36 */     int stackSize = w * h;
/* 37 */     int width = ip.getWidth() / w;
/* 38 */     int height = ip.getHeight() / h;
/* 39 */     ImageStack stack = new ImageStack(width, height);
/* 40 */     for (int y = 0; y < h; y++)
/* 41 */       for (int x = 0; x < w; x++) {
/* 42 */         ip.setRoi(x * width, y * height, width, height);
/* 43 */         stack.addSlice(null, ip.crop());
/*    */       }
/* 45 */     if (b > 0) {
/* 46 */       int cropwidth = width - b - b / 2;
/* 47 */       int cropheight = height - b - b / 2;
/* 48 */       StackProcessor sp = new StackProcessor(stack, ip);
/* 49 */       stack = sp.crop(b, b, cropwidth, cropheight);
/*    */     }
/* 51 */     return stack;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.StackMaker
 * JD-Core Version:    0.6.2
 */