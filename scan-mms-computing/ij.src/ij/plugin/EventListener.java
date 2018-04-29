/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.CommandListener;
/*    */ import ij.Executer;
/*    */ import ij.IJ;
/*    */ import ij.IJEventListener;
/*    */ import ij.ImageListener;
/*    */ import ij.ImagePlus;
/*    */ import ij.gui.Toolbar;
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class EventListener
/*    */   implements PlugIn, IJEventListener, ImageListener, CommandListener
/*    */ {
/*    */   public void run(String arg)
/*    */   {
/* 14 */     IJ.addEventListener(this);
/* 15 */     Executer.addCommandListener(this);
/* 16 */     ImagePlus.addImageListener(this);
/* 17 */     IJ.log("EventListener started");
/*    */   }
/*    */ 
/*    */   public void eventOccurred(int eventID)
/*    */   {
/*    */     String c;
/* 21 */     switch (eventID) {
/*    */     case 0:
/* 23 */       c = Integer.toHexString(Toolbar.getForegroundColor().getRGB());
/* 24 */       c = "#" + c.substring(2);
/* 25 */       IJ.log("Changed foreground color to " + c);
/* 26 */       break;
/*    */     case 1:
/* 28 */       c = Integer.toHexString(Toolbar.getBackgroundColor().getRGB());
/* 29 */       c = "#" + c.substring(2);
/* 30 */       IJ.log("Changed background color to " + c);
/* 31 */       break;
/*    */     case 4:
/* 33 */       String name = IJ.getToolName();
/* 34 */       IJ.log("Switched to the " + name + (name.endsWith("Tool") ? "" : " tool"));
/* 35 */       break;
/*    */     case 2:
/* 37 */       IJ.log("Color picker closed");
/* 38 */       break;
/*    */     case 3:
/* 40 */       IJ.removeEventListener(this);
/* 41 */       Executer.removeCommandListener(this);
/* 42 */       ImagePlus.removeImageListener(this);
/* 43 */       IJ.showStatus("Log window closed; EventListener stopped");
/*    */     }
/*    */   }
/*    */ 
/*    */   public void imageOpened(ImagePlus imp)
/*    */   {
/* 50 */     IJ.log("Opened \"" + imp.getTitle() + "\"");
/*    */   }
/*    */ 
/*    */   public void imageClosed(ImagePlus imp)
/*    */   {
/* 55 */     IJ.log("Closed \"" + imp.getTitle() + "\"");
/*    */   }
/*    */ 
/*    */   public void imageUpdated(ImagePlus imp)
/*    */   {
/* 60 */     IJ.log("Updated \"" + imp.getTitle() + "\"");
/*    */   }
/*    */ 
/*    */   public String commandExecuting(String command) {
/* 64 */     IJ.log("Executed \"" + command + "\" command");
/* 65 */     return command;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.EventListener
 * JD-Core Version:    0.6.2
 */