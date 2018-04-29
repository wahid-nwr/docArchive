/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.io.ImportDialog;
/*    */ import ij.io.OpenDialog;
/*    */ 
/*    */ public class Raw
/*    */   implements PlugIn
/*    */ {
/* 11 */   private static String defaultDirectory = null;
/*    */ 
/*    */   public void run(String arg) {
/* 14 */     OpenDialog od = new OpenDialog("Open Raw...", arg);
/* 15 */     String directory = od.getDirectory();
/* 16 */     String fileName = od.getFileName();
/* 17 */     if (fileName == null)
/* 18 */       return;
/* 19 */     ImportDialog d = new ImportDialog(fileName, directory);
/* 20 */     d.openImage();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Raw
 * JD-Core Version:    0.6.2
 */