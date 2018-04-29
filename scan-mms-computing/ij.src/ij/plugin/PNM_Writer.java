/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.io.SaveDialog;
/*    */ import ij.process.ColorProcessor;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class PNM_Writer
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String path)
/*    */   {
/* 23 */     ImagePlus img = IJ.getImage();
/* 24 */     boolean isGray = false;
/* 25 */     String extension = null;
/* 26 */     ImageProcessor ip = img.getProcessor();
/* 27 */     if (img.getBitDepth() == 24) {
/* 28 */       extension = ".pnm";
/*    */     } else {
/* 30 */       if ((img.getBitDepth() == 8) && (ip.isInvertedLut())) {
/* 31 */         ip = ip.duplicate();
/* 32 */         ip.invert();
/*    */       }
/* 34 */       ip = ip.convertToByte(true);
/* 35 */       isGray = true;
/* 36 */       extension = ".pgm";
/*    */     }
/* 38 */     String title = img.getTitle();
/* 39 */     int length = title.length();
/* 40 */     for (int i = 2; i < 5; i++) {
/* 41 */       if ((length > i + 1) && (title.charAt(length - i) == '.')) {
/* 42 */         title = title.substring(0, length - i);
/* 43 */         break;
/*    */       }
/*    */     }
/* 46 */     if ((path == null) || (path.equals(""))) {
/* 47 */       SaveDialog od = new SaveDialog("PNM Writer", title, extension);
/* 48 */       String dir = od.getDirectory();
/* 49 */       String name = od.getFileName();
/* 50 */       if (name == null)
/* 51 */         return;
/* 52 */       path = dir + name;
/*    */     }
/*    */     try
/*    */     {
/* 56 */       IJ.showStatus("Writing PNM " + path + "...");
/* 57 */       OutputStream fileOutput = new FileOutputStream(path);
/*    */ 
/* 59 */       DataOutputStream output = new DataOutputStream(fileOutput);
/*    */ 
/* 62 */       int w = img.getWidth(); int h = img.getHeight();
/* 63 */       output.writeBytes((isGray ? "P5" : "P6") + "\n# Written by ImageJ PNM Writer\n" + w + " " + h + "\n255\n");
/*    */ 
/* 66 */       if (isGray) {
/* 67 */         output.write((byte[])ip.getPixels(), 0, w * h);
/*    */       }
/*    */       else
/*    */       {
/* 71 */         byte[] pixels = new byte[w * h * 3];
/* 72 */         ColorProcessor proc = (ColorProcessor)ip;
/*    */ 
/* 74 */         for (int j = 0; j < h; j++) {
/* 75 */           for (int i = 0; i < w; i++) {
/* 76 */             int c = proc.getPixel(i, j);
/* 77 */             pixels[(3 * (i + w * j) + 0)] = ((byte)((c & 0xFF0000) >> 16));
/*    */ 
/* 79 */             pixels[(3 * (i + w * j) + 1)] = ((byte)((c & 0xFF00) >> 8));
/*    */ 
/* 81 */             pixels[(3 * (i + w * j) + 2)] = ((byte)(c & 0xFF));
/*    */           }
/*    */         }
/* 84 */         output.write(pixels, 0, pixels.length);
/*    */       }
/* 86 */       output.flush();
/*    */     } catch (IOException e) {
/* 88 */       IJ.handleException(e);
/*    */     }
/* 90 */     IJ.showStatus("");
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.PNM_Writer
 * JD-Core Version:    0.6.2
 */