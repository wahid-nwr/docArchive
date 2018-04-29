/*     */ package ij.gui;
/*     */ 
/*     */ import ij.ImageJ;
/*     */ import ij.macro.Interpreter;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ 
/*     */ public class ProgressBar extends Canvas
/*     */ {
/*     */   private int canvasWidth;
/*     */   private int canvasHeight;
/*     */   private int x;
/*     */   private int y;
/*     */   private int width;
/*     */   private int height;
/*     */   private double percent;
/*  14 */   private long lastTime = 0L;
/*     */   private boolean showBar;
/*     */   private boolean batchMode;
/*  18 */   private Color barColor = Color.gray;
/*  19 */   private Color fillColor = new Color(204, 204, 255);
/*  20 */   private Color backgroundColor = ImageJ.backgroundColor;
/*  21 */   private Color frameBrighter = this.backgroundColor.brighter();
/*  22 */   private Color frameDarker = this.backgroundColor.darker();
/*     */ 
/*     */   public ProgressBar(int canvasWidth, int canvasHeight)
/*     */   {
/*  26 */     this.canvasWidth = canvasWidth;
/*  27 */     this.canvasHeight = canvasHeight;
/*  28 */     this.x = 3;
/*  29 */     this.y = 5;
/*  30 */     this.width = (canvasWidth - 8);
/*  31 */     this.height = (canvasHeight - 7);
/*     */   }
/*     */ 
/*     */   void fill3DRect(Graphics g, int x, int y, int width, int height) {
/*  35 */     g.setColor(this.fillColor);
/*  36 */     g.fillRect(x + 1, y + 1, width - 2, height - 2);
/*  37 */     g.setColor(this.frameDarker);
/*  38 */     g.drawLine(x, y, x, y + height);
/*  39 */     g.drawLine(x + 1, y, x + width - 1, y);
/*  40 */     g.setColor(this.frameBrighter);
/*  41 */     g.drawLine(x + 1, y + height, x + width, y + height);
/*  42 */     g.drawLine(x + width, y, x + width, y + height - 1);
/*     */   }
/*     */ 
/*     */   public void show(double percent)
/*     */   {
/*  47 */     show(percent, false);
/*     */   }
/*     */ 
/*     */   public void show(double percent, boolean showInBatchMode)
/*     */   {
/*  59 */     if ((!showInBatchMode) && ((this.batchMode) || (Interpreter.isBatchMode()))) return;
/*  60 */     if (percent >= 1.0D) {
/*  61 */       percent = 0.0D;
/*  62 */       this.showBar = false;
/*  63 */       repaint();
/*  64 */       return;
/*     */     }
/*  66 */     long time = System.currentTimeMillis();
/*  67 */     if ((time - this.lastTime < 90L) && (percent != 1.0D)) return;
/*  68 */     this.lastTime = time;
/*  69 */     this.showBar = true;
/*  70 */     this.percent = percent;
/*  71 */     repaint();
/*     */   }
/*     */ 
/*     */   public void show(int currentIndex, int finalIndex)
/*     */   {
/*  79 */     show((currentIndex + 1.0D) / finalIndex, true);
/*     */   }
/*     */ 
/*     */   public void update(Graphics g) {
/*  83 */     paint(g);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g) {
/*  87 */     if (this.showBar) {
/*  88 */       fill3DRect(g, this.x - 1, this.y - 1, this.width + 1, this.height + 1);
/*  89 */       drawBar(g);
/*     */     } else {
/*  91 */       g.setColor(this.backgroundColor);
/*  92 */       g.fillRect(0, 0, this.canvasWidth, this.canvasHeight);
/*     */     }
/*     */   }
/*     */ 
/*     */   void drawBar(Graphics g) {
/*  97 */     if (this.percent < 0.0D)
/*  98 */       this.percent = 0.0D;
/*  99 */     int barEnd = (int)(this.width * this.percent);
/*     */ 
/* 104 */     g.setColor(this.barColor);
/* 105 */     g.fillRect(this.x, this.y, barEnd, this.height);
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize()
/*     */   {
/* 110 */     return new Dimension(this.canvasWidth, this.canvasHeight);
/*     */   }
/*     */ 
/*     */   public void setBatchMode(boolean batchMode) {
/* 114 */     this.batchMode = batchMode;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.ProgressBar
 * JD-Core Version:    0.6.2
 */