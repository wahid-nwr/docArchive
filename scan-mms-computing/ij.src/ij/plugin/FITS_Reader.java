/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.ImageStack;
/*    */ import ij.io.FileInfo;
/*    */ import ij.io.FileOpener;
/*    */ import ij.io.OpenDialog;
/*    */ import ij.measure.Calibration;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class FITS_Reader extends ImagePlus
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String arg)
/*    */   {
/* 16 */     OpenDialog od = new OpenDialog("Open FITS...", arg);
/* 17 */     String directory = od.getDirectory();
/* 18 */     String fileName = od.getFileName();
/* 19 */     if (fileName == null)
/* 20 */       return;
/* 21 */     IJ.showStatus("Opening: " + directory + fileName);
/* 22 */     FitsDecoder fd = new FitsDecoder(directory, fileName);
/* 23 */     FileInfo fi = null;
/*    */     try { fi = fd.getInfo(); } catch (IOException e) {
/*    */     }
/* 26 */     if ((fi != null) && (fi.width > 0) && (fi.height > 0) && (fi.offset > 0)) {
/* 27 */       FileOpener fo = new FileOpener(fi);
/* 28 */       ImagePlus imp = fo.open(false);
/* 29 */       if (fi.nImages == 1) {
/* 30 */         ImageProcessor ip = imp.getProcessor();
/* 31 */         ip.flipVertical();
/* 32 */         setProcessor(fileName, ip);
/*    */       } else {
/* 34 */         ImageStack stack = imp.getStack();
/* 35 */         for (int i = 1; i <= stack.getSize(); i++)
/* 36 */           stack.getProcessor(i).flipVertical();
/* 37 */         setStack(fileName, stack);
/*    */       }
/* 39 */       Calibration cal = imp.getCalibration();
/* 40 */       if ((fi.fileType == 1) && (fd.bscale == 1.0D) && (fd.bzero == 32768.0D))
/* 41 */         cal.setFunction(20, null, "Gray Value");
/* 42 */       setCalibration(cal);
/* 43 */       setProperty("Info", fd.getHeaderInfo());
/* 44 */       setFileInfo(fi);
/* 45 */       if (arg.equals("")) show(); 
/*    */     }
/* 47 */     else { IJ.error("This does not appear to be a FITS file."); }
/* 48 */     IJ.showStatus("");
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.FITS_Reader
 * JD-Core Version:    0.6.2
 */