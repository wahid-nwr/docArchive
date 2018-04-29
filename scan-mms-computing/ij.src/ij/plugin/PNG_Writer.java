/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.Prefs;
/*    */ import ij.WindowManager;
/*    */ import ij.io.SaveDialog;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.image.DataBufferByte;
/*    */ import java.awt.image.IndexColorModel;
/*    */ import java.awt.image.WritableRaster;
/*    */ import java.io.File;
/*    */ import javax.imageio.ImageIO;
/*    */ 
/*    */ public class PNG_Writer
/*    */   implements PlugIn
/*    */ {
/*    */   ImagePlus imp;
/*    */ 
/*    */   public void run(String path)
/*    */   {
/* 19 */     this.imp = WindowManager.getCurrentImage();
/* 20 */     if (this.imp == null) {
/* 21 */       IJ.noImage(); return;
/*    */     }
/* 23 */     if (path.equals("")) {
/* 24 */       SaveDialog sd = new SaveDialog("Save as PNG...", this.imp.getTitle(), ".png");
/* 25 */       String name = sd.getFileName();
/* 26 */       if (name == null)
/* 27 */         return;
/* 28 */       String dir = sd.getDirectory();
/* 29 */       path = dir + name;
/*    */     }
/*    */     try
/*    */     {
/* 33 */       writeImage(this.imp, path, Prefs.getTransparentIndex());
/*    */     } catch (Exception e) {
/* 35 */       String msg = e.getMessage();
/* 36 */       if ((msg == null) || (msg.equals("")))
/* 37 */         msg = "" + e;
/* 38 */       IJ.showMessage("PNG Writer", "An error occured writing the file.\n \n" + msg);
/*    */     }
/* 40 */     IJ.showStatus("");
/*    */   }
/*    */ 
/*    */   public void writeImage(ImagePlus imp, String path, int transparentIndex) throws Exception {
/* 44 */     if ((transparentIndex >= 0) && (transparentIndex <= 255) && (imp.getBitDepth() == 8))
/* 45 */       writeImageWithTransparency(imp, path, transparentIndex);
/* 46 */     else if ((imp.getOverlay() != null) && (!imp.getHideOverlay()))
/* 47 */       ImageIO.write(imp.flatten().getBufferedImage(), "png", new File(path));
/* 48 */     else if ((imp.getBitDepth() == 16) && (!imp.isComposite()) && (imp.getProcessor().isDefaultLut()))
/* 49 */       write16gs(imp, path);
/*    */     else
/* 51 */       ImageIO.write(imp.getBufferedImage(), "png", new File(path));
/*    */   }
/*    */ 
/*    */   void writeImageWithTransparency(ImagePlus imp, String path, int transparentIndex) throws Exception {
/* 55 */     int width = imp.getWidth();
/* 56 */     int height = imp.getHeight();
/* 57 */     ImageProcessor ip = imp.getProcessor();
/* 58 */     IndexColorModel cm = (IndexColorModel)ip.getColorModel();
/* 59 */     int size = cm.getMapSize();
/* 60 */     byte[] reds = new byte[256];
/* 61 */     byte[] greens = new byte[256];
/* 62 */     byte[] blues = new byte[256];
/* 63 */     cm.getReds(reds);
/* 64 */     cm.getGreens(greens);
/* 65 */     cm.getBlues(blues);
/* 66 */     cm = new IndexColorModel(8, 256, reds, greens, blues, transparentIndex);
/* 67 */     WritableRaster wr = cm.createCompatibleWritableRaster(width, height);
/* 68 */     DataBufferByte db = (DataBufferByte)wr.getDataBuffer();
/* 69 */     byte[] biPixels = db.getData();
/* 70 */     System.arraycopy(ip.getPixels(), 0, biPixels, 0, biPixels.length);
/* 71 */     BufferedImage bi = new BufferedImage(cm, wr, false, null);
/* 72 */     ImageIO.write(bi, "png", new File(path));
/*    */   }
/*    */ 
/*    */   void write16gs(ImagePlus imp, String path) throws Exception
/*    */   {
/* 77 */     int width = imp.getWidth();
/* 78 */     int height = imp.getHeight();
/* 79 */     BufferedImage bi = new BufferedImage(width, height, 11);
/*    */ 
/* 81 */     Graphics2D g = (Graphics2D)bi.getGraphics();
/* 82 */     g.drawImage(imp.getImage(), 0, 0, null);
/* 83 */     File f = new File(path);
/* 84 */     ImageIO.write(bi, "png", f);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.PNG_Writer
 * JD-Core Version:    0.6.2
 */