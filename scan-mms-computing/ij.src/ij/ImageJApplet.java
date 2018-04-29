/*    */ package ij;
/*    */ 
/*    */ import java.applet.Applet;
/*    */ 
/*    */ public class ImageJApplet extends Applet
/*    */ {
/*    */   public void init()
/*    */   {
/* 27 */     ImageJ ij = IJ.getInstance();
/* 28 */     if ((ij == null) || ((ij != null) && (!ij.isShowing())))
/* 29 */       new ImageJ(this);
/* 30 */     for (int i = 1; i <= 9; i++) {
/* 31 */       String url = getParameter("url" + i);
/* 32 */       if (url == null) break;
/* 33 */       ImagePlus imp = new ImagePlus(url);
/* 34 */       if (imp != null) imp.show(); 
/*    */     }
/*    */   }
/*    */ 
/*    */   public void destroy()
/*    */   {
/* 39 */     ImageJ ij = IJ.getInstance();
/* 40 */     if (ij != null) ij.quit();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.ImageJApplet
 * JD-Core Version:    0.6.2
 */