/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.WindowManager;
/*    */ import ij.plugin.frame.Editor;
/*    */ import ij.text.TextPanel;
/*    */ import ij.text.TextWindow;
/*    */ import java.awt.Frame;
/*    */ 
/*    */ public class TextWriter
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String arg)
/*    */   {
/* 14 */     saveText();
/*    */   }
/*    */ 
/*    */   void saveText() {
/* 18 */     Frame frame = WindowManager.getFrontWindow();
/* 19 */     if ((frame != null) && ((frame instanceof TextWindow))) {
/* 20 */       TextPanel tp = ((TextWindow)frame).getTextPanel();
/* 21 */       tp.saveAs("");
/* 22 */     } else if ((frame != null) && ((frame instanceof Editor))) {
/* 23 */       Editor ed = (Editor)frame;
/* 24 */       ed.saveAs();
/*    */     } else {
/* 26 */       IJ.error("Save As Text", "This command requires a TextWindow, such\nas the \"Log\" window, or an Editor window. Use\nFile>Save>Text Image to save an image as text.");
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.TextWriter
 * JD-Core Version:    0.6.2
 */