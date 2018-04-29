/*    */ package ij.plugin.frame;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImageJ;
/*    */ import ij.Menus;
/*    */ import ij.WindowManager;
/*    */ import ij.plugin.PlugIn;
/*    */ import java.awt.Frame;
/*    */ import java.awt.Image;
/*    */ import java.awt.event.FocusEvent;
/*    */ import java.awt.event.FocusListener;
/*    */ import java.awt.event.WindowEvent;
/*    */ import java.awt.event.WindowListener;
/*    */ 
/*    */ public class PlugInFrame extends Frame
/*    */   implements PlugIn, WindowListener, FocusListener
/*    */ {
/*    */   String title;
/*    */ 
/*    */   public PlugInFrame(String title)
/*    */   {
/* 13 */     super(title);
/* 14 */     enableEvents(64L);
/* 15 */     this.title = title;
/* 16 */     ImageJ ij = IJ.getInstance();
/* 17 */     addWindowListener(this);
/* 18 */     addFocusListener(this);
/* 19 */     if (IJ.isLinux()) setBackground(ImageJ.backgroundColor);
/* 20 */     if (ij != null) {
/* 21 */       Image img = ij.getIconImage();
/* 22 */       if (img != null) try {
/* 23 */           setIconImage(img); } catch (Exception e) {
/*    */         } 
/*    */     }
/*    */   }
/*    */ 
/*    */   public void run(String arg) {
/*    */   }
/*    */ 
/* 31 */   public void windowClosing(WindowEvent e) { if (e.getSource() == this) {
/* 32 */       close();
/* 33 */       if (Recorder.record)
/* 34 */         Recorder.record("run", "Close");
/*    */     }
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/* 41 */     dispose();
/* 42 */     WindowManager.removeWindow(this);
/*    */   }
/*    */ 
/*    */   public void windowActivated(WindowEvent e) {
/* 46 */     if ((IJ.isMacintosh()) && (IJ.getInstance() != null)) {
/* 47 */       IJ.wait(10);
/* 48 */       setMenuBar(Menus.getMenuBar());
/*    */     }
/* 50 */     WindowManager.setWindow(this);
/*    */   }
/*    */ 
/*    */   public void focusGained(FocusEvent e)
/*    */   {
/* 55 */     WindowManager.setWindow(this);
/*    */   }
/*    */ 
/*    */   public void windowOpened(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowClosed(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowIconified(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowDeiconified(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowDeactivated(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void focusLost(FocusEvent e)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.PlugInFrame
 * JD-Core Version:    0.6.2
 */