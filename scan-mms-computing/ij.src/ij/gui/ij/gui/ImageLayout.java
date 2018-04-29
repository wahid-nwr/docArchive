/*     */ package ij.gui;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.Scrollbar;
/*     */ 
/*     */ public class ImageLayout
/*     */   implements LayoutManager
/*     */ {
/*     */   int hgap;
/*     */   int vgap;
/*     */   ImageCanvas ic;
/*     */ 
/*     */   public ImageLayout(ImageCanvas ic)
/*     */   {
/*  15 */     this.ic = ic;
/*  16 */     this.hgap = 5;
/*  17 */     this.vgap = 5;
/*     */   }
/*     */ 
/*     */   public void addLayoutComponent(String name, Component comp)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void removeLayoutComponent(Component comp)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Dimension preferredLayoutSize(Container target)
/*     */   {
/*  30 */     Dimension dim = new Dimension(0, 0);
/*  31 */     int nmembers = target.getComponentCount();
/*  32 */     for (int i = 0; i < nmembers; i++) {
/*  33 */       Component m = target.getComponent(i);
/*  34 */       Dimension d = m.getPreferredSize();
/*  35 */       dim.width = Math.max(dim.width, d.width);
/*  36 */       if (i > 0) dim.height += this.vgap;
/*  37 */       dim.height += d.height;
/*     */     }
/*  39 */     Insets insets = target.getInsets();
/*  40 */     dim.width += insets.left + insets.right + this.hgap * 2;
/*  41 */     dim.height += insets.top + insets.bottom + this.vgap * 2;
/*  42 */     return dim;
/*     */   }
/*     */ 
/*     */   public Dimension minimumLayoutSize(Container target)
/*     */   {
/*  47 */     return preferredLayoutSize(target);
/*     */   }
/*     */ 
/*     */   private void moveComponents(Container target, int x, int y, int width, int height, int nmembers)
/*     */   {
/*  52 */     int x2 = 0;
/*  53 */     y += height / 2;
/*  54 */     for (int i = 0; i < nmembers; i++) {
/*  55 */       Component m = target.getComponent(i);
/*  56 */       Dimension d = m.getSize();
/*  57 */       if ((i == 0) || (d.height > 60))
/*  58 */         x2 = x + (width - d.width) / 2;
/*  59 */       m.setLocation(x2, y);
/*  60 */       y += this.vgap + d.height;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void layoutContainer(Container target)
/*     */   {
/*  67 */     Insets insets = target.getInsets();
/*  68 */     int nmembers = target.getComponentCount();
/*     */ 
/*  70 */     int extraHeight = 0;
/*  71 */     for (int i = 1; i < nmembers; i++) {
/*  72 */       Component m = target.getComponent(i);
/*  73 */       Dimension d = m.getPreferredSize();
/*  74 */       extraHeight += d.height;
/*     */     }
/*  76 */     Dimension d = target.getSize();
/*  77 */     int preferredImageWidth = d.width - (insets.left + insets.right + this.hgap * 2);
/*  78 */     int preferredImageHeight = d.height - (insets.top + insets.bottom + this.vgap * 2 + extraHeight);
/*  79 */     this.ic.resizeCanvas(preferredImageWidth, preferredImageHeight);
/*  80 */     int maxwidth = d.width - (insets.left + insets.right + this.hgap * 2);
/*  81 */     int maxheight = d.height - (insets.top + insets.bottom + this.vgap * 2);
/*  82 */     Dimension psize = preferredLayoutSize(target);
/*  83 */     int x = insets.left + this.hgap + (d.width - psize.width) / 2;
/*  84 */     int y = 0;
/*  85 */     int colw = 0;
/*     */ 
/*  87 */     for (int i = 0; i < nmembers; i++) {
/*  88 */       Component m = target.getComponent(i);
/*  89 */       d = m.getPreferredSize();
/*  90 */       if (((m instanceof ScrollbarWithLabel)) || ((m instanceof Scrollbar))) {
/*  91 */         int scrollbarWidth = target.getComponent(0).getPreferredSize().width;
/*  92 */         Dimension minSize = m.getMinimumSize();
/*  93 */         if (scrollbarWidth < minSize.width) scrollbarWidth = minSize.width;
/*  94 */         m.setSize(scrollbarWidth, d.height);
/*     */       } else {
/*  96 */         m.setSize(d.width, d.height);
/*  97 */       }if (y > 0) y += this.vgap;
/*  98 */       y += d.height;
/*  99 */       colw = Math.max(colw, d.width);
/*     */     }
/* 101 */     moveComponents(target, x, insets.top + this.vgap, colw, maxheight - y, nmembers);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.ImageLayout
 * JD-Core Version:    0.6.2
 */