/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.gui.DialogListener;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.AWTEvent;
/*    */ 
/*    */ public class Translator
/*    */   implements ExtendedPlugInFilter, DialogListener
/*    */ {
/* 11 */   private int flags = 32799;
/* 12 */   private static double xOffset = 15.0D;
/* 13 */   private static double yOffset = 15.0D;
/*    */   private ImagePlus imp;
/*    */   private GenericDialog gd;
/*    */   private PlugInFilterRunner pfr;
/* 17 */   private static int interpolationMethod = 0;
/* 18 */   private String[] methods = ImageProcessor.getInterpolationMethods();
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp) {
/* 21 */     this.imp = imp;
/* 22 */     return this.flags;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/* 26 */     ip.setInterpolationMethod(interpolationMethod);
/* 27 */     ip.translate(xOffset, yOffset);
/*    */   }
/*    */ 
/*    */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
/* 31 */     this.pfr = pfr;
/* 32 */     int digits = (xOffset == (int)xOffset) && (yOffset == (int)yOffset) ? 1 : 3;
/* 33 */     this.gd = new GenericDialog("Translate");
/* 34 */     this.gd.addNumericField("X Offset (pixels): ", xOffset, digits, 8, "");
/* 35 */     this.gd.addNumericField("Y Offset (pixels): ", yOffset, digits, 8, "");
/* 36 */     this.gd.addChoice("Interpolation:", this.methods, this.methods[interpolationMethod]);
/* 37 */     this.gd.addPreviewCheckbox(pfr);
/* 38 */     this.gd.addDialogListener(this);
/* 39 */     this.gd.showDialog();
/* 40 */     if (this.gd.wasCanceled())
/* 41 */       return 4096;
/* 42 */     return IJ.setupDialog(imp, this.flags);
/*    */   }
/*    */ 
/*    */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 46 */     xOffset = gd.getNextNumber();
/* 47 */     yOffset = gd.getNextNumber();
/* 48 */     interpolationMethod = gd.getNextChoiceIndex();
/* 49 */     if (gd.invalidNumber()) {
/* 50 */       if (gd.wasOKed()) IJ.error("Offset is invalid.");
/* 51 */       return false;
/*    */     }
/* 53 */     return true;
/*    */   }
/*    */ 
/*    */   public void setNPasses(int nPasses)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Translator
 * JD-Core Version:    0.6.2
 */