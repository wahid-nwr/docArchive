/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.Macro;
/*    */ import ij.gui.DialogListener;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.process.FloatProcessor;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.AWTEvent;
/*    */ import java.awt.Rectangle;
/*    */ 
/*    */ public class UnsharpMask
/*    */   implements ExtendedPlugInFilter, DialogListener
/*    */ {
/* 20 */   private static double sigma = 1.0D;
/* 21 */   private static double weight = 0.6D;
/* 22 */   private final int flags = 16801887;
/*    */   private GaussianBlur gb;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 32 */     return 16801887;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip)
/*    */   {
/* 42 */     sharpenFloat((FloatProcessor)ip, sigma, (float)weight);
/*    */   }
/*    */ 
/*    */   public void sharpenFloat(FloatProcessor fp, double sigma, float weight)
/*    */   {
/* 47 */     if (this.gb == null) this.gb = new GaussianBlur();
/* 48 */     this.gb.blurGaussian(fp, sigma, sigma, 0.01D);
/* 49 */     if (Thread.currentThread().isInterrupted()) return;
/* 50 */     float[] pixels = (float[])fp.getPixels();
/* 51 */     float[] snapshotPixels = (float[])fp.getSnapshotPixels();
/* 52 */     int width = fp.getWidth();
/* 53 */     Rectangle roi = fp.getRoi();
/* 54 */     for (int y = roi.y; y < roi.y + roi.height; y++) {
/* 55 */       int x = roi.x; for (int p = width * y + x; x < roi.x + roi.width; p++) {
/* 56 */         pixels[p] = ((snapshotPixels[p] - weight * pixels[p]) / (1.0F - weight));
/*    */ 
/* 55 */         x++;
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
/* 61 */     String options = Macro.getOptions();
/* 62 */     boolean oldMacro = false;
/* 63 */     if ((options != null) && 
/* 64 */       (options.indexOf("gaussian=") >= 0)) {
/* 65 */       oldMacro = true;
/* 66 */       Macro.setOptions(options.replaceAll("gaussian=", "radius="));
/*    */     }
/*    */ 
/* 69 */     GenericDialog gd = new GenericDialog(command);
/* 70 */     sigma = Math.abs(sigma);
/* 71 */     if (weight < 0.0D) weight = 0.0D;
/* 72 */     if (weight > 0.99D) weight = 0.99D;
/* 73 */     gd.addNumericField("Radius (Sigma)", sigma, 1, 6, "pixels");
/* 74 */     gd.addNumericField("Mask Weight (0.1-0.9)", weight, 2);
/* 75 */     gd.addPreviewCheckbox(pfr);
/* 76 */     gd.addDialogListener(this);
/* 77 */     gd.showDialog();
/* 78 */     if (gd.wasCanceled()) return 4096;
/* 79 */     if (oldMacro) sigma /= 2.5D;
/* 80 */     IJ.register(getClass());
/* 81 */     return IJ.setupDialog(imp, 16801887);
/*    */   }
/*    */ 
/*    */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 85 */     sigma = gd.getNextNumber();
/* 86 */     weight = gd.getNextNumber();
/* 87 */     if ((sigma < 0.0D) || (weight < 0.0D) || (weight > 0.99D) || (gd.invalidNumber()))
/* 88 */       return false;
/* 89 */     return true;
/*    */   }
/*    */ 
/*    */   public void setNPasses(int nPasses)
/*    */   {
/* 96 */     if (this.gb == null) this.gb = new GaussianBlur();
/* 97 */     this.gb.setNPasses(nPasses);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.UnsharpMask
 * JD-Core Version:    0.6.2
 */