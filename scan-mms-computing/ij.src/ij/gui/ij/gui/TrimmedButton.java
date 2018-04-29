/*    */ package ij.gui;
/*    */ 
/*    */ import java.awt.Button;
/*    */ import java.awt.Dimension;
/*    */ 
/*    */ public class TrimmedButton extends Button
/*    */ {
/*  6 */   private int trim = 0;
/*    */ 
/*    */   public TrimmedButton(String title, int trim) {
/*  9 */     super(title);
/* 10 */     this.trim = trim;
/*    */   }
/*    */ 
/*    */   public Dimension getMinimumSize() {
/* 14 */     return new Dimension(super.getMinimumSize().width - this.trim, super.getMinimumSize().height);
/*    */   }
/*    */ 
/*    */   public Dimension getPreferredSize() {
/* 18 */     return getMinimumSize();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.TrimmedButton
 * JD-Core Version:    0.6.2
 */