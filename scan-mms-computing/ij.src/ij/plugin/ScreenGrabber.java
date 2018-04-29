/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.ImageCanvas;
/*    */ import ij.gui.ImageWindow;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Image;
/*    */ import java.awt.Point;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.Robot;
/*    */ 
/*    */ public class ScreenGrabber
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String arg)
/*    */   {
/* 12 */     ImagePlus imp2 = null;
/* 13 */     if ((arg.equals("image")) || (arg.equals("flatten")))
/* 14 */       imp2 = captureImage();
/*    */     else
/* 16 */       imp2 = captureScreen();
/* 17 */     if (imp2 != null)
/* 18 */       imp2.show();
/*    */   }
/*    */ 
/*    */   public ImagePlus captureScreen()
/*    */   {
/* 38 */     ImagePlus imp = null;
/*    */     try {
/* 40 */       Robot robot = new Robot();
/* 41 */       Dimension dimension = IJ.getScreenSize();
/* 42 */       Rectangle r = new Rectangle(dimension);
/* 43 */       Image img = robot.createScreenCapture(r);
/* 44 */       if (img != null) imp = new ImagePlus("Screen", img); 
/*    */     } catch (Exception e) {  }
/*    */ 
/* 46 */     return imp;
/*    */   }
/*    */ 
/*    */   public ImagePlus captureImage()
/*    */   {
/* 51 */     ImagePlus imp = IJ.getImage();
/* 52 */     if (imp == null) {
/* 53 */       IJ.noImage();
/* 54 */       return null;
/*    */     }
/* 56 */     ImagePlus imp2 = null;
/*    */     try {
/* 58 */       ImageWindow win = imp.getWindow();
/* 59 */       if (win == null) return null;
/* 60 */       win.toFront();
/* 61 */       IJ.wait(500);
/* 62 */       Point loc = win.getLocation();
/* 63 */       ImageCanvas ic = win.getCanvas();
/* 64 */       Rectangle bounds = ic.getBounds();
/* 65 */       loc.x += bounds.x;
/* 66 */       loc.y += bounds.y;
/* 67 */       Rectangle r = new Rectangle(loc.x, loc.y, bounds.width, bounds.height);
/* 68 */       Robot robot = new Robot();
/* 69 */       Image img = robot.createScreenCapture(r);
/* 70 */       if (img != null) {
/* 71 */         String title = WindowManager.getUniqueName(imp.getTitle());
/* 72 */         imp2 = new ImagePlus(title, img);
/*    */       }
/*    */     } catch (Exception e) {  }
/*    */ 
/* 75 */     return imp2;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ScreenGrabber
 * JD-Core Version:    0.6.2
 */