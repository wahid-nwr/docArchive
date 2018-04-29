/*    */ package ij.util;
/*    */ 
/*    */ import ij.IJ;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.RenderingHints;
/*    */ import java.awt.geom.Rectangle2D;
/*    */ import javax.swing.UIManager;
/*    */ 
/*    */ public class Java2
/*    */ {
/*    */   private static boolean lookAndFeelSet;
/*    */ 
/*    */   public static void setAntialiased(Graphics g, boolean antialiased)
/*    */   {
/* 16 */     Graphics2D g2d = (Graphics2D)g;
/* 17 */     if (antialiased)
/* 18 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*    */     else
/* 20 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
/*    */   }
/*    */ 
/*    */   public static void setAntialiasedText(Graphics g, boolean antialiasedText) {
/* 24 */     Graphics2D g2d = (Graphics2D)g;
/* 25 */     if (antialiasedText)
/* 26 */       g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/*    */     else
/* 28 */       g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
/*    */   }
/*    */ 
/*    */   public static int getStringWidth(String s, FontMetrics fontMetrics, Graphics g) {
/* 32 */     Rectangle2D r = fontMetrics.getStringBounds(s, g);
/* 33 */     return (int)r.getWidth();
/*    */   }
/*    */ 
/*    */   public static void setBilinearInterpolation(Graphics g, boolean bilinearInterpolation) {
/* 37 */     Graphics2D g2d = (Graphics2D)g;
/* 38 */     if (bilinearInterpolation)
/* 39 */       g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
/*    */     else
/* 41 */       g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
/*    */   }
/*    */ 
/*    */   public static void setSystemLookAndFeel()
/*    */   {
/* 46 */     if ((lookAndFeelSet) || (!IJ.isWindows())) return; try
/*    */     {
/* 48 */       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Throwable t) {
/*    */     }
/* 50 */     lookAndFeelSet = true;
/* 51 */     IJ.register(Java2.class);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.util.Java2
 * JD-Core Version:    0.6.2
 */