/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.Roi;
/*    */ import ij.io.OpenDialog;
/*    */ import ij.io.RoiDecoder;
/*    */ import ij.process.ByteProcessor;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.Color;
/*    */ import java.awt.Rectangle;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class RoiReader
/*    */   implements PlugIn
/*    */ {
/* 15 */   final int polygon = 0; final int rect = 1; final int oval = 2; final int line = 3; final int freeLine = 4; final int segLine = 5; final int noRoi = 6; final int freehand = 7; final int traced = 8;
/*    */ 
/*    */   public void run(String arg) {
/* 18 */     OpenDialog od = new OpenDialog("Open ROI...", arg);
/* 19 */     String dir = od.getDirectory();
/* 20 */     String name = od.getFileName();
/* 21 */     if (name == null)
/* 22 */       return;
/*    */     try {
/* 24 */       openRoi(dir, name);
/*    */     } catch (IOException e) {
/* 26 */       String msg = e.getMessage();
/* 27 */       if ((msg == null) || (msg.equals("")))
/* 28 */         msg = "" + e;
/* 29 */       IJ.error("ROI Reader", msg);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void openRoi(String dir, String name) throws IOException {
/* 34 */     String path = dir + name;
/* 35 */     RoiDecoder rd = new RoiDecoder(path);
/* 36 */     Roi roi = rd.getRoi();
/* 37 */     Rectangle r = roi.getBounds();
/* 38 */     ImagePlus img = WindowManager.getCurrentImage();
/* 39 */     if ((img == null) || (img.getWidth() < r.x + r.width) || (img.getHeight() < r.y + r.height)) {
/* 40 */       ImageProcessor ip = new ByteProcessor(r.x + r.width + 10, r.y + r.height + 10);
/* 41 */       ip.setColor(Color.white);
/* 42 */       ip.fill();
/* 43 */       img = new ImagePlus(name, ip);
/* 44 */       img.show();
/*    */     }
/* 46 */     img.setRoi(roi);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.RoiReader
 * JD-Core Version:    0.6.2
 */