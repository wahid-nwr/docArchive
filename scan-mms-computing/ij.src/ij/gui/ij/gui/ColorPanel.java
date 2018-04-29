/*     */ package ij.gui;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Panel;
/*     */ 
/*     */ class ColorPanel extends Panel
/*     */ {
/*     */   static final int WIDTH = 100;
/*     */   static final int HEIGHT = 50;
/*     */   Color c;
/*     */ 
/*     */   ColorPanel(Color c)
/*     */   {
/*  86 */     this.c = c;
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize() {
/*  90 */     return new Dimension(100, 50);
/*     */   }
/*     */ 
/*     */   void setColor(Color c) {
/*  94 */     this.c = c;
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize() {
/*  98 */     return new Dimension(100, 50);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g) {
/* 102 */     g.setColor(this.c);
/* 103 */     g.fillRect(0, 0, 100, 50);
/* 104 */     g.setColor(Color.black);
/* 105 */     g.drawRect(0, 0, 99, 49);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.ColorPanel
 * JD-Core Version:    0.6.2
 */