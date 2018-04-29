/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.ImageStack;
/*    */ import ij.Prefs;
/*    */ import ij.io.SaveDialog;
/*    */ import ij.measure.Calibration;
/*    */ 
/*    */ public class GifWriter
/*    */   implements PlugIn
/*    */ {
/* 47 */   static int transparentIndex = Prefs.getTransparentIndex();
/*    */ 
/*    */   public void run(String path) {
/* 50 */     ImagePlus imp = IJ.getImage();
/* 51 */     if (path.equals("")) {
/* 52 */       SaveDialog sd = new SaveDialog("Save as Gif", imp.getTitle(), ".gif");
/* 53 */       if (sd.getFileName() == null) return;
/* 54 */       path = sd.getDirectory() + sd.getFileName();
/*    */     }
/*    */ 
/* 57 */     ImageStack stack = imp.getStack();
/* 58 */     ImagePlus tmp = new ImagePlus();
/* 59 */     int nSlices = stack.getSize();
/* 60 */     GifEncoder ge = new GifEncoder();
/* 61 */     double fps = imp.getCalibration().fps;
/* 62 */     if (fps == 0.0D) fps = Animator.getFrameRate();
/* 63 */     if (fps <= 0.2D) fps = 0.2D;
/* 64 */     if (fps > 60.0D) fps = 60.0D;
/* 65 */     ge.setDelay((int)(1.0D / fps * 1000.0D));
/* 66 */     if (transparentIndex != -1) {
/* 67 */       ge.transparent = true;
/* 68 */       ge.transIndex = transparentIndex;
/*    */     }
/* 70 */     ge.start(path);
/*    */ 
/* 72 */     for (int i = 1; i <= nSlices; i++) {
/* 73 */       IJ.showStatus("writing: " + i + "/" + nSlices);
/* 74 */       IJ.showProgress(i / nSlices);
/* 75 */       tmp.setProcessor(null, stack.getProcessor(i));
/*    */       try
/*    */       {
/* 78 */         ge.addFrame(tmp);
/*    */       } catch (Exception e) {
/* 80 */         IJ.showMessage("Save as Gif", "" + e);
/* 81 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 85 */     ge.finish();
/* 86 */     IJ.showStatus("");
/* 87 */     IJ.showProgress(1.0D);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.GifWriter
 * JD-Core Version:    0.6.2
 */