/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.Prefs;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.DialogListener;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.gui.NonBlockingGenericDialog;
/*    */ import ij.gui.Roi;
/*    */ import ij.gui.Toolbar;
/*    */ import java.awt.AWTEvent;
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class RectToolOptions
/*    */   implements PlugIn, DialogListener
/*    */ {
/*    */   private String strokeColorName;
/*    */   private String fillColorName;
/*    */   private static GenericDialog gd;
/* 10 */   private static double defaultStrokeWidth = 2.0D;
/*    */ 
/*    */   public void run(String arg) {
/* 13 */     if ((gd != null) && (gd.isVisible()))
/* 14 */       gd.toFront();
/*    */     else
/* 16 */       rectToolOptions();
/*    */   }
/*    */ 
/*    */   void rectToolOptions() {
/* 20 */     Color strokeColor = Toolbar.getForegroundColor();
/* 21 */     Color fillColor = null;
/* 22 */     double strokeWidth = defaultStrokeWidth;
/* 23 */     int cornerDiameter = (int)Prefs.get("toolbar.arc.size", 20.0D);
/* 24 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 25 */     Roi roi = imp != null ? imp.getRoi() : null;
/* 26 */     if ((roi != null) && (roi.getType() == 0)) {
/* 27 */       strokeColor = roi.getStrokeColor();
/* 28 */       if (strokeColor == null)
/* 29 */         strokeColor = Roi.getColor();
/* 30 */       fillColor = roi.getFillColor();
/* 31 */       strokeWidth = roi.getStrokeWidth();
/* 32 */       cornerDiameter = roi.getCornerDiameter();
/*    */     }
/* 34 */     String strokec = Colors.colorToString(strokeColor);
/* 35 */     String fillc = Colors.colorToString(fillColor);
/*    */ 
/* 37 */     gd = new NonBlockingGenericDialog("Rounded Rectangle Tool");
/* 38 */     gd.addSlider("Stroke width:", 1.0D, 25.0D, (int)strokeWidth);
/* 39 */     gd.addSlider("Corner diameter:", 0.0D, 200.0D, cornerDiameter);
/* 40 */     gd.addStringField("Stroke color: ", strokec);
/* 41 */     gd.addStringField("Fill color: ", fillc);
/* 42 */     gd.addDialogListener(this);
/* 43 */     gd.showDialog();
/*    */   }
/*    */ 
/*    */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 47 */     double strokeWidth2 = gd.getNextNumber();
/* 48 */     int cornerDiameter2 = (int)gd.getNextNumber();
/* 49 */     String strokec2 = gd.getNextString();
/* 50 */     String fillc2 = gd.getNextString();
/* 51 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 52 */     Roi roi = imp != null ? imp.getRoi() : null;
/* 53 */     Color strokeColor = null;
/* 54 */     if ((roi != null) && (roi.getType() == 0)) {
/* 55 */       roi.setStrokeWidth((int)strokeWidth2);
/* 56 */       roi.setCornerDiameter(cornerDiameter2);
/* 57 */       strokeColor = Colors.decode(strokec2, roi.getStrokeColor());
/* 58 */       Color fillColor = Colors.decode(fillc2, roi.getFillColor());
/* 59 */       roi.setStrokeColor(strokeColor);
/* 60 */       roi.setFillColor(fillColor);
/*    */     }
/* 62 */     defaultStrokeWidth = strokeWidth2;
/* 63 */     if (strokeColor != null)
/* 64 */       Toolbar.setForegroundColor(strokeColor);
/* 65 */     Toolbar.setRoundRectArcSize(cornerDiameter2);
/* 66 */     if ((cornerDiameter2 > 0) && 
/* 67 */       (!Toolbar.getToolName().equals("roundrect"))) {
/* 68 */       IJ.setTool("roundrect");
/*    */     }
/* 70 */     return true;
/*    */   }
/*    */ 
/*    */   public static float getDefaultStrokeWidth() {
/* 74 */     return (float)defaultStrokeWidth;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.RectToolOptions
 * JD-Core Version:    0.6.2
 */