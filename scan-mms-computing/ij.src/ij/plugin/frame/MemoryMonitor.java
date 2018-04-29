/*     */ package ij.plugin.frame;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GUI;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.RenderingHints;
/*     */ 
/*     */ public class MemoryMonitor extends PlugInFrame
/*     */ {
/*     */   private static final int WIDTH = 250;
/*     */   private static final int HEIGHT = 90;
/*     */   private static final String LOC_KEY = "memory.loc";
/*     */   private static MemoryMonitor instance;
/*     */   private Image image;
/*     */   private Graphics2D g;
/*     */   private int frames;
/*     */   private double[] mem;
/*     */   private int index;
/*     */   private long value;
/*  21 */   private double defaultMax = 18493440.0D;
/*  22 */   private double max = this.defaultMax;
/*  23 */   private long maxMemory = IJ.maxMemory();
/*     */ 
/*     */   public MemoryMonitor() {
/*  26 */     super("Memory");
/*  27 */     if (instance != null) {
/*  28 */       WindowManager.toFront(instance);
/*  29 */       return;
/*     */     }
/*  31 */     instance = this;
/*  32 */     WindowManager.addWindow(this);
/*     */ 
/*  34 */     setLayout(new BorderLayout());
/*  35 */     Canvas ic = new Canvas();
/*  36 */     ic.setSize(250, 90);
/*  37 */     add(ic);
/*  38 */     setResizable(false);
/*  39 */     pack();
/*  40 */     Point loc = Prefs.getLocation("memory.loc");
/*  41 */     if (loc != null)
/*  42 */       setLocation(loc);
/*     */     else
/*  44 */       GUI.center(this);
/*  45 */     this.image = createImage(250, 90);
/*  46 */     this.g = ((Graphics2D)this.image.getGraphics());
/*  47 */     this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/*  48 */     this.g.setColor(Color.white);
/*  49 */     this.g.fillRect(0, 0, 250, 90);
/*  50 */     this.g.setFont(new Font("SansSerif", 0, 12));
/*  51 */     Graphics icg = ic.getGraphics();
/*  52 */     icg.drawImage(this.image, 0, 0, null);
/*  53 */     show();
/*  54 */     ImageJ ij = IJ.getInstance();
/*  55 */     if (ij != null) {
/*  56 */       addKeyListener(ij);
/*  57 */       ic.addKeyListener(ij);
/*  58 */       ic.addMouseListener(ij);
/*     */     }
/*  60 */     this.mem = new double['û'];
/*  61 */     Thread.currentThread().setPriority(1);
/*     */     while (true) {
/*  63 */       updatePlot();
/*  64 */       addText();
/*  65 */       icg.drawImage(this.image, 0, 0, null);
/*  66 */       IJ.wait(50);
/*  67 */       this.frames += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   void addText() {
/*  72 */     double value2 = this.value / 1048576.0D;
/*  73 */     String s = IJ.d2s(value2, value2 > 50.0D ? 0 : 2) + "MB";
/*  74 */     if (this.maxMemory > 0L) {
/*  75 */       double percent = this.value * 100L / this.maxMemory;
/*  76 */       s = s + " (" + (percent < 1.0D ? "<1" : IJ.d2s(percent, 0)) + "%)";
/*     */     }
/*  78 */     this.g.drawString(s, 2, 15);
/*  79 */     String images = "" + WindowManager.getImageCount();
/*  80 */     this.g.drawString(images, 250 - (5 + images.length() * 8), 15);
/*     */   }
/*     */ 
/*     */   void updatePlot() {
/*  84 */     double used = IJ.currentMemory();
/*  85 */     if (this.frames % 10 == 0) this.value = (()used);
/*  86 */     if (used > 0.86D * this.max) this.max *= 2.0D;
/*  87 */     this.mem[(this.index++)] = used;
/*  88 */     if (this.index == this.mem.length) this.index = 0;
/*  89 */     double maxmax = 0.0D;
/*  90 */     for (int i = 0; i < this.mem.length; i++) {
/*  91 */       if (this.mem[i] > maxmax) maxmax = this.mem[i];
/*     */     }
/*  93 */     if (maxmax < this.defaultMax) this.max = (this.defaultMax * 2.0D);
/*  94 */     if (maxmax < this.defaultMax / 2.0D) this.max = this.defaultMax;
/*  95 */     int index2 = this.index + 1;
/*  96 */     if (index2 == this.mem.length) index2 = 0;
/*  97 */     this.g.setColor(Color.white);
/*  98 */     this.g.fillRect(0, 0, 250, 90);
/*  99 */     this.g.setColor(Color.black);
/* 100 */     double scale = 90.0D / this.max;
/* 101 */     int x1 = 0;
/* 102 */     int y1 = 90 - (int)(this.mem[index2] * scale);
/* 103 */     for (int x2 = 1; x2 < 250; x2++) {
/* 104 */       index2++;
/* 105 */       if (index2 == this.mem.length) index2 = 0;
/* 106 */       int y2 = 90 - (int)(this.mem[index2] * scale);
/* 107 */       this.g.drawLine(x1, y1, x2, y2);
/* 108 */       x1 = x2; y1 = y2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close() {
/* 113 */     super.close();
/* 114 */     instance = null;
/* 115 */     Prefs.saveLocation("memory.loc", getLocation());
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.MemoryMonitor
 * JD-Core Version:    0.6.2
 */