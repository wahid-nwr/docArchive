/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.GenericDialog;
/*    */ 
/*    */ public class WandToolOptions
/*    */   implements PlugIn
/*    */ {
/* 13 */   private static final String[] modes = { "Legacy", "4-connected", "8-connected" };
/* 14 */   private static String mode = modes[0];
/*    */   private static double tolerance;
/*    */ 
/*    */   public void run(String arg)
/*    */   {
/* 18 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 19 */     boolean showCheckbox = (imp != null) && (imp.getBitDepth() != 24) && (WindowManager.getFrame("Threshold") == null);
/* 20 */     GenericDialog gd = new GenericDialog("Wand Tool");
/* 21 */     gd.addChoice("Mode: ", modes, mode);
/* 22 */     gd.addNumericField("Tolerance: ", tolerance, 1);
/* 23 */     if (showCheckbox)
/* 24 */       gd.addCheckbox("Enable Thresholding", false);
/* 25 */     gd.showDialog();
/* 26 */     if (gd.wasCanceled()) return;
/* 27 */     mode = gd.getNextChoice();
/* 28 */     tolerance = gd.getNextNumber();
/* 29 */     if ((showCheckbox) && 
/* 30 */       (gd.getNextBoolean())) {
/* 31 */       imp.killRoi();
/* 32 */       IJ.run("Threshold...");
/*    */     }
/*    */   }
/*    */ 
/*    */   public static String getMode()
/*    */   {
/* 38 */     return mode;
/*    */   }
/*    */ 
/*    */   public static double getTolerance() {
/* 42 */     return tolerance;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.WandToolOptions
 * JD-Core Version:    0.6.2
 */