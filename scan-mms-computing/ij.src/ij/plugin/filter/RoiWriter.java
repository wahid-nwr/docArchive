/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.gui.Roi;
/*    */ import ij.io.RoiEncoder;
/*    */ import ij.io.SaveDialog;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class RoiWriter
/*    */   implements PlugInFilter
/*    */ {
/*    */   ImagePlus imp;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 20 */     this.imp = imp;
/* 21 */     return 1183;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/*    */     try {
/* 26 */       saveRoi(this.imp);
/*    */     } catch (IOException e) {
/* 28 */       String msg = e.getMessage();
/* 29 */       if ((msg == null) || (msg.equals("")))
/* 30 */         msg = "" + e;
/* 31 */       IJ.error("ROI Writer", msg);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void saveRoi(ImagePlus imp) throws IOException {
/* 36 */     Roi roi = imp.getRoi();
/* 37 */     if (roi == null)
/* 38 */       throw new IllegalArgumentException("ROI required");
/* 39 */     String name = roi.getName();
/* 40 */     if (name == null)
/* 41 */       name = imp.getTitle();
/* 42 */     SaveDialog sd = new SaveDialog("Save Selection...", name, ".roi");
/* 43 */     name = sd.getFileName();
/* 44 */     if (name == null)
/* 45 */       return;
/* 46 */     String dir = sd.getDirectory();
/* 47 */     RoiEncoder re = new RoiEncoder(dir + name);
/* 48 */     re.write(roi);
/* 49 */     if (name.endsWith(".roi"))
/* 50 */       name = name.substring(0, name.length() - 4);
/* 51 */     roi.setName(name);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.RoiWriter
 * JD-Core Version:    0.6.2
 */