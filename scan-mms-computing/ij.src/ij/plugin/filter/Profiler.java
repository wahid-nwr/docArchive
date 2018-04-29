/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.Prefs;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.gui.PlotWindow;
/*    */ import ij.gui.ProfilePlot;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.Dimension;
/*    */ 
/*    */ public class Profiler
/*    */   implements PlugInFilter
/*    */ {
/*    */   ImagePlus imp;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 14 */     if (arg.equals("set")) {
/* 15 */       doOptions(); return 4096;
/* 16 */     }this.imp = imp;
/* 17 */     return 1439;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/* 21 */     boolean averageHorizontally = (Prefs.verticalProfile) || (IJ.altKeyDown());
/* 22 */     new ProfilePlot(this.imp, averageHorizontally).createWindow();
/*    */   }
/*    */ 
/*    */   public void doOptions() {
/* 26 */     double ymin = ProfilePlot.getFixedMin();
/* 27 */     double ymax = ProfilePlot.getFixedMax();
/* 28 */     boolean fixedScale = (ymin != 0.0D) || (ymax != 0.0D);
/* 29 */     boolean wasFixedScale = fixedScale;
/*    */ 
/* 31 */     GenericDialog gd = new GenericDialog("Profile Plot Options", IJ.getInstance());
/* 32 */     gd.addNumericField("Width (pixels):", PlotWindow.plotWidth, 0);
/* 33 */     gd.addNumericField("Height (pixels):", PlotWindow.plotHeight, 0);
/* 34 */     gd.addNumericField("Minimum Y:", ymin, 2);
/* 35 */     gd.addNumericField("Maximum Y:", ymax, 2);
/* 36 */     gd.addCheckbox("Fixed y-axis scale", fixedScale);
/* 37 */     gd.addCheckbox("Do not save x-values", !PlotWindow.saveXValues);
/* 38 */     gd.addCheckbox("Auto-close", PlotWindow.autoClose);
/* 39 */     gd.addCheckbox("Vertical profile", Prefs.verticalProfile);
/* 40 */     gd.addCheckbox("List values", PlotWindow.listValues);
/* 41 */     gd.addCheckbox("Interpolate line profiles", PlotWindow.interpolate);
/* 42 */     gd.addCheckbox("Draw grid lines", !PlotWindow.noGridLines);
/* 43 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/edit.html#plot-options");
/* 44 */     gd.showDialog();
/* 45 */     if (gd.wasCanceled())
/* 46 */       return;
/* 47 */     Dimension screen = IJ.getScreenSize();
/* 48 */     int w = (int)gd.getNextNumber();
/* 49 */     int h = (int)gd.getNextNumber();
/* 50 */     if (w < 100) w = 100;
/* 51 */     if (w > screen.width - 140) w = screen.width - 140;
/* 52 */     if (h < 50) h = 50;
/* 53 */     if (h > screen.height - 300) h = screen.height - 300;
/* 54 */     PlotWindow.plotWidth = w;
/* 55 */     PlotWindow.plotHeight = h;
/* 56 */     ymin = gd.getNextNumber();
/* 57 */     ymax = gd.getNextNumber();
/* 58 */     fixedScale = gd.getNextBoolean();
/* 59 */     PlotWindow.saveXValues = !gd.getNextBoolean();
/* 60 */     PlotWindow.autoClose = gd.getNextBoolean();
/* 61 */     Prefs.verticalProfile = gd.getNextBoolean();
/* 62 */     PlotWindow.listValues = gd.getNextBoolean();
/* 63 */     PlotWindow.interpolate = gd.getNextBoolean();
/* 64 */     PlotWindow.noGridLines = !gd.getNextBoolean();
/* 65 */     if ((!fixedScale) && (!wasFixedScale) && ((ymin != 0.0D) || (ymax != 0.0D)))
/* 66 */       fixedScale = true;
/* 67 */     if (!fixedScale) {
/* 68 */       ymin = 0.0D;
/* 69 */       ymax = 0.0D;
/* 70 */     } else if (ymin > ymax) {
/* 71 */       double tmp = ymin;
/* 72 */       ymin = ymax;
/* 73 */       ymax = tmp;
/*    */     }
/* 75 */     ProfilePlot.setMinAndMax(ymin, ymax);
/* 76 */     IJ.register(Profiler.class);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Profiler
 * JD-Core Version:    0.6.2
 */