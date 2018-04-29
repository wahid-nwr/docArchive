/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.gui.Plot;
/*    */ import ij.measure.Calibration;
/*    */ import ij.measure.Measurements;
/*    */ import ij.measure.ResultsTable;
/*    */ import ij.process.ByteProcessor;
/*    */ import ij.process.ImageProcessor;
/*    */ import ij.util.Tools;
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class LineGraphAnalyzer
/*    */   implements PlugInFilter, Measurements
/*    */ {
/*    */   ImagePlus imp;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 15 */     this.imp = imp;
/* 16 */     return 129;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/* 20 */     analyze(this.imp);
/*    */   }
/*    */ 
/*    */   public void analyze(ImagePlus imp)
/*    */   {
/* 26 */     ByteProcessor ip = (ByteProcessor)imp.getProcessor();
/* 27 */     ImageProcessor ip2 = ip.crop();
/* 28 */     int width = ip2.getWidth();
/* 29 */     int height = ip2.getHeight();
/* 30 */     ip2.setColor(Color.white);
/* 31 */     for (int i = 1; i < width; i += 2) {
/* 32 */       ip2.moveTo(i, 0);
/* 33 */       ip2.lineTo(i, height - 1);
/*    */     }
/* 35 */     ip2 = ip2.rotateRight();
/* 36 */     ImagePlus imp2 = imp.createImagePlus();
/* 37 */     ip2.setThreshold(ip.getMinThreshold(), ip.getMaxThreshold(), 2);
/* 38 */     imp2.setProcessor("Temp", ip2);
/* 39 */     Calibration cal = imp2.getCalibration();
/* 40 */     double pw = cal.pixelWidth;
/* 41 */     double ph = cal.pixelHeight;
/* 42 */     cal.pixelWidth = ph;
/* 43 */     cal.pixelHeight = pw;
/* 44 */     imp2.setCalibration(cal);
/* 45 */     if (IJ.altKeyDown()) imp2.show();
/*    */ 
/* 47 */     int options = 32;
/* 48 */     int measurements = 32;
/* 49 */     int minSize = 1;
/* 50 */     int maxSize = 999999;
/* 51 */     ResultsTable rt = new ResultsTable();
/* 52 */     ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, rt, minSize, maxSize);
/* 53 */     if (!pa.analyze(imp2))
/* 54 */       return;
/* 55 */     float[] y = rt.getColumn(6);
/* 56 */     if (y == null)
/* 57 */       return;
/* 58 */     float[] x = rt.getColumn(7);
/* 59 */     double[] a = Tools.getMinMax(x);
/* 60 */     double xmin = a[0]; double xmax = a[1];
/* 61 */     a = Tools.getMinMax(y);
/* 62 */     double ymin = a[0]; double ymax = a[1];
/*    */ 
/* 64 */     String units = " (" + cal.getUnits() + ")";
/* 65 */     String xLabel = "X" + units;
/* 66 */     String yLabel = "Y" + units;
/* 67 */     Plot plot = new Plot("Line Graph", xLabel, yLabel, x, y);
/* 68 */     plot.setLimits(0.0D, width * ph, 0.0D, height * pw);
/* 69 */     plot.show();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.LineGraphAnalyzer
 * JD-Core Version:    0.6.2
 */