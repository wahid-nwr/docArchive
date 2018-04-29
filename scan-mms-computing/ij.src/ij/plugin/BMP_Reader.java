/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.io.FileInfo;
/*    */ import ij.io.OpenDialog;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.Image;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.image.MemoryImageSource;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class BMP_Reader extends ImagePlus
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String arg)
/*    */   {
/* 19 */     OpenDialog od = new OpenDialog("Open BMP...", arg);
/* 20 */     String directory = od.getDirectory();
/* 21 */     String name = od.getFileName();
/* 22 */     if (name == null)
/* 23 */       return;
/* 24 */     String path = directory + name;
/*    */ 
/* 27 */     BMPDecoder bmp = new BMPDecoder();
/* 28 */     FileInputStream is = null;
/*    */     try {
/* 30 */       is = new FileInputStream(path);
/* 31 */       bmp.read(is);
/*    */     } catch (Exception e) {
/* 33 */       String msg = e.getMessage();
/* 34 */       if ((msg == null) || (msg.equals("")))
/* 35 */         msg = "" + e; IJ.error("BMP Decoder", msg);
/*    */       return;
/*    */     } finally {
/* 39 */       if (is != null)
/*    */         try {
/* 41 */           is.close();
/*    */         }
/*    */         catch (IOException e) {
/*    */         }
/*    */     }
/* 46 */     MemoryImageSource mis = bmp.makeImageSource();
/* 47 */     if (mis == null) IJ.write("mis=null");
/* 48 */     Image img = Toolkit.getDefaultToolkit().createImage(mis);
/* 49 */     FileInfo fi = new FileInfo();
/* 50 */     fi.fileFormat = 5;
/* 51 */     fi.fileName = name;
/* 52 */     fi.directory = directory;
/* 53 */     setImage(img);
/* 54 */     setTitle(name);
/* 55 */     setFileInfo(fi);
/* 56 */     if (bmp.topDown)
/* 57 */       getProcessor().flipVertical();
/* 58 */     if (arg.equals(""))
/* 59 */       show();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.BMP_Reader
 * JD-Core Version:    0.6.2
 */