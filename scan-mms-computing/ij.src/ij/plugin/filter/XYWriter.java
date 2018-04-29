/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.gui.PolygonRoi;
/*    */ import ij.gui.Roi;
/*    */ import ij.io.SaveDialog;
/*    */ import ij.measure.Calibration;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.Rectangle;
/*    */ import java.io.BufferedOutputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ 
/*    */ public class XYWriter
/*    */   implements PlugInFilter
/*    */ {
/*    */   ImagePlus imp;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 18 */     this.imp = imp;
/* 19 */     return 1183;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/*    */     try {
/* 24 */       saveXYCoordinates(this.imp);
/*    */     } catch (IllegalArgumentException e) {
/* 26 */       IJ.error("XYWriter", e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void saveXYCoordinates(ImagePlus imp) {
/* 31 */     Roi roi = imp.getRoi();
/* 32 */     if (roi == null)
/* 33 */       throw new IllegalArgumentException("ROI required");
/* 34 */     if (!(roi instanceof PolygonRoi)) {
/* 35 */       throw new IllegalArgumentException("Irregular area or line selection required");
/*    */     }
/* 37 */     SaveDialog sd = new SaveDialog("Save Coordinates as Text...", imp.getTitle(), ".txt");
/* 38 */     String name = sd.getFileName();
/* 39 */     if (name == null)
/* 40 */       return;
/* 41 */     String directory = sd.getDirectory();
/* 42 */     PrintWriter pw = null;
/*    */     try {
/* 44 */       FileOutputStream fos = new FileOutputStream(directory + name);
/* 45 */       BufferedOutputStream bos = new BufferedOutputStream(fos);
/* 46 */       pw = new PrintWriter(bos);
/*    */     }
/*    */     catch (IOException e) {
/* 49 */       IJ.error("XYWriter", "" + e);
/* 50 */       return;
/*    */     }
/*    */ 
/* 53 */     Rectangle r = roi.getBounds();
/* 54 */     PolygonRoi p = (PolygonRoi)roi;
/* 55 */     int n = p.getNCoordinates();
/* 56 */     int[] x = p.getXCoordinates();
/* 57 */     int[] y = p.getYCoordinates();
/*    */ 
/* 59 */     Calibration cal = imp.getCalibration();
/* 60 */     String ls = System.getProperty("line.separator");
/* 61 */     boolean scaled = cal.scaled();
/* 62 */     for (int i = 0; i < n; i++) {
/* 63 */       if (scaled)
/* 64 */         pw.print(IJ.d2s((r.x + x[i]) * cal.pixelWidth) + "\t" + IJ.d2s((r.y + y[i]) * cal.pixelHeight) + ls);
/*    */       else
/* 66 */         pw.print(r.x + x[i] + "\t" + (r.y + y[i]) + ls);
/*    */     }
/* 68 */     pw.close();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.XYWriter
 * JD-Core Version:    0.6.2
 */