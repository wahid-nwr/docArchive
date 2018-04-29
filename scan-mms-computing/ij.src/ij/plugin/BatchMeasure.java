/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.plugin.filter.Analyzer;
/*    */ import java.io.File;
/*    */ 
/*    */ public class BatchMeasure
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String arg)
/*    */   {
/* 12 */     String dir = IJ.getDirectory("Choose a Folder");
/* 13 */     if (dir == null) return;
/* 14 */     String[] list = new File(dir).list();
/* 15 */     if (list == null) return;
/* 16 */     Analyzer.setMeasurement(1024, true);
/* 17 */     for (int i = 0; i < list.length; i++)
/* 18 */       if (!list[i].startsWith(".")) {
/* 19 */         String path = dir + list[i];
/* 20 */         IJ.showProgress(i + 1, list.length);
/* 21 */         ImagePlus imp = !path.endsWith("/") ? IJ.openImage(path) : null;
/* 22 */         if (imp != null) {
/* 23 */           IJ.run(imp, "Measure", "");
/* 24 */           imp.close();
/*    */         }
/*    */       }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.BatchMeasure
 * JD-Core Version:    0.6.2
 */