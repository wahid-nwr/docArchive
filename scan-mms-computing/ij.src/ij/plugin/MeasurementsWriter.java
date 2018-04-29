/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.Prefs;
/*    */ import ij.WindowManager;
/*    */ import ij.io.SaveDialog;
/*    */ import ij.measure.ResultsTable;
/*    */ import ij.text.TextPanel;
/*    */ import ij.text.TextWindow;
/*    */ import java.awt.Frame;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class MeasurementsWriter
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String path)
/*    */   {
/* 13 */     save(path);
/*    */   }
/*    */ 
/*    */   public boolean save(String path) {
/* 17 */     Frame frame = WindowManager.getFrontWindow();
/* 18 */     if ((frame != null) && ((frame instanceof TextWindow))) {
/* 19 */       TextWindow tw = (TextWindow)frame;
/* 20 */       if (tw.getTextPanel().getResultsTable() == null) {
/* 21 */         IJ.error("Save As>Results", "\"" + tw.getTitle() + "\" is not a results table");
/* 22 */         return false;
/*    */       }
/* 24 */       return tw.getTextPanel().saveAs(path);
/* 25 */     }if (IJ.isResultsWindow()) {
/* 26 */       TextPanel tp = IJ.getTextPanel();
/* 27 */       if ((tp != null) && 
/* 28 */         (!tp.saveAs(path)))
/* 29 */         return false;
/*    */     }
/*    */     else {
/* 32 */       ResultsTable rt = ResultsTable.getResultsTable();
/* 33 */       if ((rt == null) || (rt.getCounter() == 0))
/* 34 */         return false;
/* 35 */       if (path.equals("")) {
/* 36 */         SaveDialog sd = new SaveDialog("Save as Text", "Results", Prefs.get("options.ext", ".xls"));
/* 37 */         String file = sd.getFileName();
/* 38 */         if (file == null) return false;
/* 39 */         path = sd.getDirectory() + file;
/*    */       }
/*    */       try {
/* 42 */         rt.saveAs(path);
/*    */       } catch (IOException e) {
/* 44 */         IJ.error("" + e);
/*    */       }
/*    */     }
/* 47 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.MeasurementsWriter
 * JD-Core Version:    0.6.2
 */