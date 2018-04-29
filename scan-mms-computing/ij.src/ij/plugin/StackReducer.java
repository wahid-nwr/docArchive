/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.ImageStack;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.measure.Calibration;
/*    */ import ij.process.ImageProcessor;
/*    */ 
/*    */ public class StackReducer
/*    */   implements PlugIn
/*    */ {
/*    */   ImagePlus imp;
/* 11 */   private static int factor = 2;
/*    */   private boolean hyperstack;
/*    */   private boolean reduceSlices;
/*    */ 
/*    */   public void run(String arg)
/*    */   {
/* 15 */     this.imp = WindowManager.getCurrentImage();
/* 16 */     if (this.imp == null) {
/* 17 */       IJ.noImage(); return;
/* 18 */     }ImageStack stack = this.imp.getStack();
/* 19 */     int size = stack.getSize();
/* 20 */     if ((size == 1) || ((this.imp.getNChannels() == size) && (this.imp.isComposite()))) {
/* 21 */       IJ.error("Stack or hyperstack required"); return;
/* 22 */     }if (!showDialog(stack))
/* 23 */       return;
/* 24 */     if (this.hyperstack)
/* 25 */       reduceHyperstack(this.imp, factor, this.reduceSlices);
/*    */     else
/* 27 */       reduceStack(this.imp, factor);
/*    */   }
/*    */ 
/*    */   public boolean showDialog(ImageStack stack) {
/* 31 */     this.hyperstack = this.imp.isHyperStack();
/* 32 */     boolean showCheckbox = false;
/* 33 */     if ((this.hyperstack) && (this.imp.getNSlices() > 1) && (this.imp.getNFrames() > 1))
/* 34 */       showCheckbox = true;
/* 35 */     else if ((this.hyperstack) && (this.imp.getNSlices() > 1))
/* 36 */       this.reduceSlices = true;
/* 37 */     int n = stack.getSize();
/* 38 */     GenericDialog gd = new GenericDialog("Reduce Size");
/* 39 */     gd.addNumericField("Reduction Factor:", factor, 0);
/* 40 */     if (showCheckbox)
/* 41 */       gd.addCheckbox("Reduce in Z-Dimension", false);
/* 42 */     gd.showDialog();
/* 43 */     if (gd.wasCanceled()) return false;
/* 44 */     factor = (int)gd.getNextNumber();
/* 45 */     if (showCheckbox)
/* 46 */       this.reduceSlices = gd.getNextBoolean();
/* 47 */     return true;
/*    */   }
/*    */ 
/*    */   public void reduceStack(ImagePlus imp, int factor) {
/* 51 */     ImageStack stack = imp.getStack();
/* 52 */     boolean virtual = stack.isVirtual();
/* 53 */     int n = stack.getSize();
/* 54 */     ImageStack stack2 = new ImageStack(stack.getWidth(), stack.getHeight());
/* 55 */     for (int i = 1; i <= n; i += factor) {
/* 56 */       if (virtual) IJ.showProgress(i, n);
/* 57 */       stack2.addSlice(stack.getSliceLabel(i), stack.getProcessor(i));
/*    */     }
/* 59 */     imp.setStack(null, stack2);
/* 60 */     if (virtual) {
/* 61 */       IJ.showProgress(1.0D);
/* 62 */       imp.setTitle(imp.getTitle());
/*    */     }
/* 64 */     Calibration cal = imp.getCalibration();
/* 65 */     if (cal.scaled()) cal.pixelDepth *= factor; 
/*    */   }
/*    */ 
/*    */   public void reduceHyperstack(ImagePlus imp, int factor, boolean reduceSlices)
/*    */   {
/* 69 */     int channels = imp.getNChannels();
/* 70 */     int slices = imp.getNSlices();
/* 71 */     int frames = imp.getNFrames();
/* 72 */     int zfactor = reduceSlices ? factor : 1;
/* 73 */     int tfactor = reduceSlices ? 1 : factor;
/* 74 */     ImageStack stack = imp.getStack();
/* 75 */     ImageStack stack2 = new ImageStack(imp.getWidth(), imp.getHeight());
/* 76 */     boolean virtual = stack.isVirtual();
/* 77 */     int slices2 = slices / zfactor + (slices % zfactor != 0 ? 1 : 0);
/* 78 */     int frames2 = frames / tfactor + (frames % tfactor != 0 ? 1 : 0);
/* 79 */     int n = channels * slices2 * frames2;
/* 80 */     int count = 1;
/* 81 */     for (int t = 1; t <= frames; t += tfactor) {
/* 82 */       for (int z = 1; z <= slices; z += zfactor) {
/* 83 */         for (int c = 1; c <= channels; c++) {
/* 84 */           int i = imp.getStackIndex(c, z, t);
/* 85 */           IJ.showProgress(i, n);
/* 86 */           ImageProcessor ip = stack.getProcessor(imp.getStackIndex(c, z, t));
/*    */ 
/* 88 */           stack2.addSlice(stack.getSliceLabel(i), ip);
/*    */         }
/*    */       }
/*    */     }
/* 92 */     imp.setStack(stack2, channels, slices2, frames2);
/* 93 */     Calibration cal = imp.getCalibration();
/* 94 */     if (cal.scaled()) cal.pixelDepth *= zfactor;
/* 95 */     if (virtual) imp.setTitle(imp.getTitle());
/* 96 */     IJ.showProgress(1.0D);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.StackReducer
 * JD-Core Version:    0.6.2
 */