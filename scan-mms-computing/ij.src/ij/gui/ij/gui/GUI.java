/*    */ package ij.gui;
/*    */ 
/*    */ import ij.IJ;
/*    */ import java.awt.Color;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Frame;
/*    */ import java.awt.Image;
/*    */ import java.awt.Window;
/*    */ 
/*    */ public class GUI
/*    */ {
/*    */   private static Frame frame;
/*    */ 
/*    */   public static void center(Window w)
/*    */   {
/* 10 */     Dimension screen = IJ.getScreenSize();
/* 11 */     Dimension window = w.getSize();
/* 12 */     if (window.width == 0)
/* 13 */       return;
/* 14 */     int left = screen.width / 2 - window.width / 2;
/* 15 */     int top = (screen.height - window.height) / 4;
/* 16 */     if (top < 0) top = 0;
/* 17 */     w.setLocation(left, top);
/*    */   }
/*    */ 
/*    */   public static Image createBlankImage(int width, int height)
/*    */   {
/* 24 */     if ((width == 0) || (height == 0))
/* 25 */       throw new IllegalArgumentException("");
/* 26 */     if (frame == null) {
/* 27 */       frame = new Frame();
/* 28 */       frame.pack();
/* 29 */       frame.setBackground(Color.white);
/*    */     }
/* 31 */     Image img = frame.createImage(width, height);
/* 32 */     return img;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.GUI
 * JD-Core Version:    0.6.2
 */