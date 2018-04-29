/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.WindowManager;
/*    */ import ij.io.FileSaver;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Image;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.File;
/*    */ import java.util.Iterator;
/*    */ import javax.imageio.IIOImage;
/*    */ import javax.imageio.ImageIO;
/*    */ import javax.imageio.ImageWriteParam;
/*    */ import javax.imageio.ImageWriter;
/*    */ import javax.imageio.stream.ImageOutputStream;
/*    */ 
/*    */ public class JpegWriter
/*    */   implements PlugIn
/*    */ {
/*    */   public static final int DEFAULT_QUALITY = 75;
/*    */ 
/*    */   public void run(String arg)
/*    */   {
/* 18 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 19 */     if (imp == null) return;
/* 20 */     imp.startTiming();
/* 21 */     saveAsJpeg(imp, arg, FileSaver.getJpegQuality());
/* 22 */     IJ.showTime(imp, imp.getStartTime(), "JpegWriter: ");
/*    */   }
/*    */ 
/*    */   public static String save(ImagePlus imp, String path, int quality)
/*    */   {
/* 27 */     imp.startTiming();
/* 28 */     String error = new JpegWriter().saveAsJpeg(imp, path, quality);
/* 29 */     IJ.showTime(imp, imp.getStartTime(), "JpegWriter: ");
/* 30 */     return error;
/*    */   }
/*    */ 
/*    */   String saveAsJpeg(ImagePlus imp, String path, int quality) {
/* 34 */     int width = imp.getWidth();
/* 35 */     int height = imp.getHeight();
/* 36 */     int biType = 1;
/* 37 */     boolean overlay = (imp.getOverlay() != null) && (!imp.getHideOverlay());
/* 38 */     if ((imp.getProcessor().isDefaultLut()) && (!imp.isComposite()) && (!overlay))
/* 39 */       biType = 10;
/* 40 */     BufferedImage bi = new BufferedImage(width, height, biType);
/* 41 */     String error = null;
/*    */     try {
/* 43 */       Graphics g = bi.createGraphics();
/* 44 */       Image img = imp.getImage();
/* 45 */       if (overlay)
/* 46 */         img = imp.flatten().getImage();
/* 47 */       g.drawImage(img, 0, 0, null);
/* 48 */       g.dispose();
/* 49 */       Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
/* 50 */       ImageWriter writer = (ImageWriter)iter.next();
/* 51 */       File f = new File(path);
/* 52 */       String originalPath = null;
/* 53 */       boolean replacing = f.exists();
/* 54 */       if (replacing) {
/* 55 */         originalPath = path;
/* 56 */         path = path + ".temp";
/* 57 */         f = new File(path);
/*    */       }
/* 59 */       ImageOutputStream ios = ImageIO.createImageOutputStream(f);
/* 60 */       writer.setOutput(ios);
/* 61 */       ImageWriteParam param = writer.getDefaultWriteParam();
/* 62 */       param.setCompressionMode(2);
/* 63 */       param.setCompressionQuality(quality / 100.0F);
/* 64 */       if (quality == 100)
/* 65 */         param.setSourceSubsampling(1, 1, 0, 0);
/* 66 */       IIOImage iioImage = new IIOImage(bi, null, null);
/* 67 */       writer.write(null, iioImage, param);
/* 68 */       ios.close();
/* 69 */       writer.dispose();
/* 70 */       if (replacing) {
/* 71 */         File f2 = new File(originalPath);
/* 72 */         boolean ok = f2.delete();
/* 73 */         if (ok) f.renameTo(f2); 
/*    */       }
/*    */     }
/* 76 */     catch (Exception e) { error = "" + e;
/* 77 */       IJ.error("Jpeg Writer", "" + error);
/*    */     }
/* 79 */     return error;
/*    */   }
/*    */ 
/*    */   /** @deprecated */
/*    */   public static void setQuality(int jpegQuality)
/*    */   {
/* 87 */     FileSaver.setJpegQuality(jpegQuality);
/*    */   }
/*    */ 
/*    */   /** @deprecated */
/*    */   public static int getQuality()
/*    */   {
/* 95 */     return FileSaver.getJpegQuality();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.JpegWriter
 * JD-Core Version:    0.6.2
 */