/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import java.awt.AWTEventMulticaster;
/*     */ import java.awt.Adjustable;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.Panel;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.AdjustmentListener;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.geom.GeneralPath;
/*     */ 
/*     */ public class ScrollbarWithLabel extends Panel
/*     */   implements Adjustable, AdjustmentListener
/*     */ {
/*     */   Scrollbar bar;
/*     */   private Icon icon;
/*     */   private StackWindow stackWindow;
/*     */   transient AdjustmentListener adjustmentListener;
/*     */ 
/*     */   public ScrollbarWithLabel()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ScrollbarWithLabel(StackWindow stackWindow, int value, int visible, int minimum, int maximum, char label)
/*     */   {
/*  22 */     super(new BorderLayout(2, 0));
/*  23 */     this.stackWindow = stackWindow;
/*  24 */     this.bar = new Scrollbar(0, value, visible, minimum, maximum);
/*  25 */     this.icon = new Icon(label);
/*  26 */     add(this.icon, "West");
/*  27 */     add(this.bar, "Center");
/*  28 */     this.bar.addAdjustmentListener(this);
/*  29 */     addKeyListener(IJ.getInstance());
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize()
/*     */   {
/*  36 */     Dimension dim = new Dimension(0, 0);
/*  37 */     int width = this.bar.getPreferredSize().width;
/*  38 */     Dimension minSize = getMinimumSize();
/*  39 */     if (width < minSize.width) width = minSize.width;
/*  40 */     int height = this.bar.getPreferredSize().height;
/*  41 */     dim = new Dimension(width, height);
/*  42 */     return dim;
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize() {
/*  46 */     return new Dimension(80, 15);
/*     */   }
/*     */ 
/*     */   public synchronized void addKeyListener(KeyListener l)
/*     */   {
/*  52 */     super.addKeyListener(l);
/*  53 */     this.bar.addKeyListener(l);
/*     */   }
/*     */ 
/*     */   public synchronized void removeKeyListener(KeyListener l)
/*     */   {
/*  59 */     super.removeKeyListener(l);
/*  60 */     this.bar.removeKeyListener(l);
/*     */   }
/*     */ 
/*     */   public synchronized void addAdjustmentListener(AdjustmentListener l)
/*     */   {
/*  67 */     if (l == null) {
/*  68 */       return;
/*     */     }
/*  70 */     this.adjustmentListener = AWTEventMulticaster.add(this.adjustmentListener, l);
/*     */   }
/*     */   public int getBlockIncrement() {
/*  73 */     return this.bar.getBlockIncrement();
/*     */   }
/*     */   public int getMaximum() {
/*  76 */     return this.bar.getMaximum();
/*     */   }
/*     */   public int getMinimum() {
/*  79 */     return this.bar.getMinimum();
/*     */   }
/*     */   public int getOrientation() {
/*  82 */     return this.bar.getOrientation();
/*     */   }
/*     */   public int getUnitIncrement() {
/*  85 */     return this.bar.getUnitIncrement();
/*     */   }
/*     */   public int getValue() {
/*  88 */     return this.bar.getValue();
/*     */   }
/*     */   public int getVisibleAmount() {
/*  91 */     return this.bar.getVisibleAmount();
/*     */   }
/*     */   public synchronized void removeAdjustmentListener(AdjustmentListener l) {
/*  94 */     if (l == null) {
/*  95 */       return;
/*     */     }
/*  97 */     this.adjustmentListener = AWTEventMulticaster.remove(this.adjustmentListener, l);
/*     */   }
/*     */   public void setBlockIncrement(int b) {
/* 100 */     this.bar.setBlockIncrement(b);
/*     */   }
/*     */   public void setMaximum(int max) {
/* 103 */     this.bar.setMaximum(max);
/*     */   }
/*     */   public void setMinimum(int min) {
/* 106 */     this.bar.setMinimum(min);
/*     */   }
/*     */   public void setUnitIncrement(int u) {
/* 109 */     this.bar.setUnitIncrement(u);
/*     */   }
/*     */   public void setValue(int v) {
/* 112 */     this.bar.setValue(v);
/*     */   }
/*     */   public void setVisibleAmount(int v) {
/* 115 */     this.bar.setVisibleAmount(v);
/*     */   }
/*     */ 
/*     */   public void setFocusable(boolean focusable) {
/* 119 */     super.setFocusable(focusable);
/* 120 */     this.bar.setFocusable(focusable);
/*     */   }
/*     */ 
/*     */   public void adjustmentValueChanged(AdjustmentEvent e)
/*     */   {
/* 127 */     if ((this.bar != null) && (e.getSource() == this.bar)) {
/* 128 */       AdjustmentEvent myE = new AdjustmentEvent(this, e.getID(), e.getAdjustmentType(), e.getValue(), e.getValueIsAdjusting());
/*     */ 
/* 130 */       AdjustmentListener listener = this.adjustmentListener;
/* 131 */       if (listener != null)
/* 132 */         listener.adjustmentValueChanged(myE);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updatePlayPauseIcon()
/*     */   {
/* 138 */     this.icon.repaint(); } 
/*     */   class Icon extends Canvas implements MouseListener { private static final int WIDTH = 12;
/*     */     private static final int HEIGHT = 14;
/* 144 */     private BasicStroke stroke = new BasicStroke(2.0F);
/*     */     private char type;
/*     */     private Image image;
/*     */ 
/* 149 */     public Icon(char type) { addMouseListener(this);
/* 150 */       addKeyListener(IJ.getInstance());
/* 151 */       setSize(12, 14);
/* 152 */       this.type = type;
/*     */     }
/*     */ 
/*     */     public Dimension getPreferredSize()
/*     */     {
/* 157 */       return new Dimension(12, 14);
/*     */     }
/*     */ 
/*     */     public void update(Graphics g) {
/* 161 */       paint(g);
/*     */     }
/*     */ 
/*     */     public void paint(Graphics g) {
/* 165 */       g.setColor(Color.white);
/* 166 */       g.fillRect(0, 0, 12, 14);
/* 167 */       Graphics2D g2d = (Graphics2D)g;
/* 168 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 169 */       if (this.type == 't')
/* 170 */         drawPlayPauseButton(g2d);
/*     */       else
/* 172 */         drawLetter(g);
/*     */     }
/*     */ 
/*     */     private void drawLetter(Graphics g) {
/* 176 */       g.setFont(new Font("SansSerif", 0, 14));
/* 177 */       g.setColor(Color.black);
/* 178 */       g.drawString(String.valueOf(this.type), 2, 12);
/*     */     }
/*     */ 
/*     */     private void drawPlayPauseButton(Graphics2D g) {
/* 182 */       if (ScrollbarWithLabel.this.stackWindow.getAnimate()) {
/* 183 */         g.setColor(Color.black);
/* 184 */         g.setStroke(this.stroke);
/* 185 */         g.drawLine(3, 3, 3, 11);
/* 186 */         g.drawLine(8, 3, 8, 11);
/*     */       } else {
/* 188 */         g.setColor(Color.darkGray);
/* 189 */         GeneralPath path = new GeneralPath();
/* 190 */         path.moveTo(3.0F, 2.0F);
/* 191 */         path.lineTo(10.0F, 7.0F);
/* 192 */         path.lineTo(3.0F, 12.0F);
/* 193 */         path.lineTo(3.0F, 2.0F);
/* 194 */         g.fill(path);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void mousePressed(MouseEvent e) {
/* 199 */       if (this.type != 't') return;
/* 200 */       int flags = e.getModifiers();
/* 201 */       if ((flags & 0xE) != 0)
/* 202 */         IJ.doCommand("Animation Options...");
/*     */       else
/* 204 */         IJ.doCommand("Start Animation [\\]");
/*     */     }
/*     */ 
/*     */     public void mouseReleased(MouseEvent e)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mouseExited(MouseEvent e)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mouseClicked(MouseEvent e)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mouseEntered(MouseEvent e)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.ScrollbarWithLabel
 * JD-Core Version:    0.6.2
 */