/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.Prefs;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.Arrow;
/*    */ import ij.gui.DialogListener;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.gui.NonBlockingGenericDialog;
/*    */ import ij.gui.Roi;
/*    */ import ij.gui.Toolbar;
/*    */ import java.awt.AWTEvent;
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class ArrowToolOptions
/*    */   implements PlugIn, DialogListener
/*    */ {
/*    */   private String colorName;
/*    */   private static GenericDialog gd;
/*    */ 
/*    */   public void run(String arg)
/*    */   {
/* 12 */     if ((gd != null) && (gd.isVisible()))
/* 13 */       gd.toFront();
/*    */     else
/* 15 */       arrowToolOptions();
/*    */   }
/*    */ 
/*    */   void arrowToolOptions() {
/* 19 */     if (!Toolbar.getToolName().equals("arrow"))
/* 20 */       IJ.setTool("arrow");
/* 21 */     double width = Arrow.getDefaultWidth();
/* 22 */     double headSize = Arrow.getDefaultHeadSize();
/* 23 */     Color color = Toolbar.getForegroundColor();
/* 24 */     this.colorName = Colors.getColorName(color, "red");
/* 25 */     int style = Arrow.getDefaultStyle();
/* 26 */     gd = new NonBlockingGenericDialog("Arrow Tool");
/* 27 */     gd.addSlider("Width:", 1.0D, 50.0D, (int)width);
/* 28 */     gd.addSlider("Size:", 0.0D, 30.0D, headSize);
/* 29 */     gd.addChoice("Color:", Colors.colors, this.colorName);
/* 30 */     gd.addChoice("Style:", Arrow.styles, Arrow.styles[style]);
/* 31 */     gd.addCheckbox("Outline", Arrow.getDefaultOutline());
/* 32 */     gd.addCheckbox("Double head", Arrow.getDefaultDoubleHeaded());
/* 33 */     gd.addDialogListener(this);
/* 34 */     gd.showDialog();
/*    */   }
/*    */ 
/*    */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 38 */     double width2 = gd.getNextNumber();
/* 39 */     double headSize2 = gd.getNextNumber();
/* 40 */     String colorName2 = gd.getNextChoice();
/* 41 */     int style2 = gd.getNextChoiceIndex();
/* 42 */     boolean outline2 = gd.getNextBoolean();
/* 43 */     boolean doubleHeaded2 = gd.getNextBoolean();
/* 44 */     if ((this.colorName != null) && (!colorName2.equals(this.colorName))) {
/* 45 */       Color color = Colors.getColor(colorName2, Color.black);
/* 46 */       Toolbar.setForegroundColor(color);
/*    */     }
/* 48 */     this.colorName = colorName2;
/* 49 */     Arrow.setDefaultWidth(width2);
/* 50 */     Arrow.setDefaultHeadSize(headSize2);
/* 51 */     Arrow.setDefaultStyle(style2);
/* 52 */     Arrow.setDefaultOutline(outline2);
/* 53 */     Arrow.setDefaultDoubleHeaded(doubleHeaded2);
/* 54 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 55 */     if (imp == null) return true;
/* 56 */     Roi roi = imp.getRoi();
/* 57 */     if (roi == null) return true;
/* 58 */     if ((roi instanceof Arrow)) {
/* 59 */       Arrow arrow = (Arrow)roi;
/* 60 */       roi.setStrokeWidth((float)width2);
/* 61 */       arrow.setHeadSize(headSize2);
/* 62 */       arrow.setStyle(style2);
/* 63 */       arrow.setOutline(outline2);
/* 64 */       arrow.setDoubleHeaded(doubleHeaded2);
/* 65 */       imp.draw();
/*    */     }
/* 67 */     Prefs.set("arrow.style", style2);
/* 68 */     Prefs.set("arrow.width", width2);
/* 69 */     Prefs.set("arrow.size", headSize2);
/* 70 */     Prefs.set("arrow.outline", outline2);
/* 71 */     Prefs.set("arrow.double", doubleHeaded2);
/* 72 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ArrowToolOptions
 * JD-Core Version:    0.6.2
 */