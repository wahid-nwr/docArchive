/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.ImageStack;
/*    */ import ij.gui.Plot;
/*    */ import ij.gui.ProfilePlot;
/*    */ import ij.gui.Roi;
/*    */ import ij.measure.Calibration;
/*    */ import ij.measure.Measurements;
/*    */ import ij.process.ImageProcessor;
/*    */ import ij.process.ImageStatistics;
/*    */ import ij.util.Tools;
/*    */ import java.awt.Rectangle;
/*    */ 
/*    */ public class ZAxisProfiler
/*    */   implements PlugInFilter, Measurements
/*    */ {
/*    */   ImagePlus imp;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 16 */     this.imp = imp;
/* 17 */     return 159;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/* 21 */     if (this.imp.getStackSize() < 2) {
/* 22 */       IJ.error("ZAxisProfiler", "This command requires a stack.");
/* 23 */       return;
/*    */     }
/* 25 */     Roi roi = this.imp.getRoi();
/* 26 */     if ((roi != null) && (roi.isLine())) {
/* 27 */       IJ.error("ZAxisProfiler", "This command does not work with line selections.");
/* 28 */       return;
/*    */     }
/* 30 */     double minThreshold = ip.getMinThreshold();
/* 31 */     double maxThreshold = ip.getMaxThreshold();
/* 32 */     float[] y = getZAxisProfile(roi, minThreshold, maxThreshold);
/* 33 */     if (y != null) {
/* 34 */       float[] x = new float[y.length];
/* 35 */       for (int i = 0; i < x.length; i++)
/* 36 */         x[i] = (i + 1);
/*    */       String title;
/*    */       String title;
/* 38 */       if (roi != null) {
/* 39 */         Rectangle r = this.imp.getRoi().getBounds();
/* 40 */         title = this.imp.getTitle() + "-" + r.x + "-" + r.y;
/*    */       } else {
/* 42 */         title = this.imp.getTitle() + "-0-0";
/* 43 */       }Plot plot = new Plot(title, "Slice", "Mean", x, y);
/* 44 */       double ymin = ProfilePlot.getFixedMin();
/* 45 */       double ymax = ProfilePlot.getFixedMax();
/* 46 */       if ((ymin != 0.0D) || (ymax != 0.0D)) {
/* 47 */         double[] a = Tools.getMinMax(x);
/* 48 */         double xmin = a[0]; double xmax = a[1];
/* 49 */         plot.setLimits(xmin, xmax, ymin, ymax);
/*    */       }
/* 51 */       plot.show();
/*    */     }
/*    */   }
/*    */ 
/*    */   float[] getZAxisProfile(Roi roi, double minThreshold, double maxThreshold) {
/* 56 */     ImageStack stack = this.imp.getStack();
/* 57 */     int size = stack.getSize();
/* 58 */     float[] values = new float[size];
/* 59 */     Calibration cal = this.imp.getCalibration();
/* 60 */     Analyzer analyzer = new Analyzer(this.imp);
/* 61 */     int measurements = Analyzer.getMeasurements();
/* 62 */     boolean showResults = (measurements != 0) && (measurements != 256);
/* 63 */     boolean showingLabels = ((measurements & 0x400) != 0) || ((measurements & 0x100000) != 0);
/* 64 */     measurements |= 2;
/* 65 */     if ((showResults) && 
/* 66 */       (!Analyzer.resetCounter())) {
/* 67 */       return null;
/*    */     }
/* 69 */     int current = this.imp.getCurrentSlice();
/* 70 */     for (int i = 1; i <= size; i++) {
/* 71 */       if (showingLabels) this.imp.setSlice(i);
/* 72 */       ImageProcessor ip = stack.getProcessor(i);
/* 73 */       if (minThreshold != -808080.0D)
/* 74 */         ip.setThreshold(minThreshold, maxThreshold, 2);
/* 75 */       ip.setRoi(roi);
/* 76 */       ImageStatistics stats = ImageStatistics.getStatistics(ip, measurements, cal);
/* 77 */       analyzer.saveResults(stats, roi);
/* 78 */       if (showResults)
/* 79 */         analyzer.displayResults();
/* 80 */       values[(i - 1)] = ((float)stats.mean);
/*    */     }
/* 82 */     if (showingLabels) this.imp.setSlice(current);
/* 83 */     return values;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.ZAxisProfiler
 * JD-Core Version:    0.6.2
 */