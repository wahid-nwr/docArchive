/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.plugin.frame.ContrastAdjuster;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.StackProcessor;
/*     */ import java.awt.Frame;
/*     */ 
/*     */ public class LutApplier
/*     */   implements PlugInFilter
/*     */ {
/*     */   ImagePlus imp;
/*     */   int min;
/*     */   int max;
/*     */   boolean canceled;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  18 */     this.imp = imp;
/*  19 */     int baseOptions = 83;
/*  20 */     if ((imp != null) && (imp.getType() == 4)) {
/*  21 */       return baseOptions + 256;
/*     */     }
/*  23 */     return baseOptions;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  27 */     apply(this.imp, ip);
/*     */   }
/*     */ 
/*     */   void apply(ImagePlus imp, ImageProcessor ip) {
/*  31 */     if (ip.getMinThreshold() != -808080.0D) {
/*  32 */       imp.unlock();
/*  33 */       IJ.runPlugIn("ij.plugin.Thresholder", "skip");
/*  34 */       return;
/*     */     }
/*  36 */     this.min = ((int)ip.getMin());
/*  37 */     this.max = ((int)ip.getMax());
/*  38 */     if ((this.min == 0) && (this.max == 255)) {
/*  39 */       IJ.error("Apply LUT", "The display range must first be updated\nusing Image>Adjust>Brightness/Contrast\nor threshold levels defined using\nImage>Adjust>Threshold.");
/*     */ 
/*  43 */       return;
/*     */     }
/*  45 */     if (imp.getType() == 4) {
/*  46 */       if (imp.getStackSize() > 1) {
/*  47 */         applyRGBStack(imp);
/*     */       } else {
/*  49 */         ip.reset();
/*  50 */         Undo.setup(6, imp);
/*  51 */         ip.setMinAndMax(this.min, this.max);
/*     */       }
/*     */ 
/*  54 */       if (this.canceled) ip.reset();
/*  55 */       resetContrastAdjuster();
/*  56 */       return;
/*     */     }
/*  58 */     ip.resetMinAndMax();
/*  59 */     int[] table = new int[256];
/*  60 */     for (int i = 0; i < 256; i++) {
/*  61 */       if (i <= this.min)
/*  62 */         table[i] = 0;
/*  63 */       else if (i >= this.max)
/*  64 */         table[i] = 255;
/*     */       else
/*  66 */         table[i] = ((int)((i - this.min) / (this.max - this.min) * 255.0D));
/*     */     }
/*  68 */     if (imp.getStackSize() > 1) {
/*  69 */       ImageStack stack = imp.getStack();
/*     */ 
/*  71 */       int flags = IJ.setupDialog(imp, 0);
/*  72 */       if (flags == 4096) {
/*  73 */         ip.setMinAndMax(this.min, this.max); return;
/*  74 */       }if (flags == 32) {
/*  75 */         new StackProcessor(stack, ip).applyTable(table);
/*  76 */         Undo.reset();
/*     */       } else {
/*  78 */         ip.applyTable(table);
/*     */       }
/*     */     } else { ip.applyTable(table); }
/*  81 */     resetContrastAdjuster();
/*     */   }
/*     */ 
/*     */   void resetContrastAdjuster() {
/*  85 */     Frame frame = WindowManager.getFrame("B&C");
/*  86 */     if (frame == null)
/*  87 */       frame = WindowManager.getFrame("W&L");
/*  88 */     if ((frame != null) && ((frame instanceof ContrastAdjuster)))
/*  89 */       ((ContrastAdjuster)frame).updateAndDraw();
/*     */   }
/*     */ 
/*     */   void applyRGBStack(ImagePlus imp) {
/*  93 */     int current = imp.getCurrentSlice();
/*  94 */     int n = imp.getStackSize();
/*  95 */     if (!IJ.showMessageWithCancel("Update Entire Stack?", "Apply brightness and contrast settings\nto all " + n + " slices in the stack?\n \n" + "NOTE: There is no Undo for this operation."))
/*     */     {
/*  99 */       this.canceled = true;
/* 100 */       return;
/*     */     }
/* 102 */     for (int i = 1; i <= n; i++) {
/* 103 */       if (i != current) {
/* 104 */         imp.setSlice(i);
/* 105 */         ImageProcessor ip = imp.getProcessor();
/* 106 */         ip.setMinAndMax(this.min, this.max);
/* 107 */         IJ.showProgress(i / n);
/*     */       }
/*     */     }
/* 110 */     imp.setSlice(current);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.LutApplier
 * JD-Core Version:    0.6.2
 */