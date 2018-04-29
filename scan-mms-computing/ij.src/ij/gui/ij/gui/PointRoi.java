/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Java2;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Polygon;
/*     */ 
/*     */ public class PointRoi extends PolygonRoi
/*     */ {
/*     */   private static Font font;
/*  16 */   private static int fontSize = 9;
/*     */   private double saveMag;
/*     */   private boolean hideLabels;
/*     */ 
/*     */   public PointRoi(int[] ox, int[] oy, int points)
/*     */   {
/*  22 */     super(ox, oy, points, 10);
/*  23 */     this.width += 1; this.height += 1;
/*     */   }
/*     */ 
/*     */   public PointRoi(Polygon poly)
/*     */   {
/*  28 */     this(poly.xpoints, poly.ypoints, poly.npoints);
/*     */   }
/*     */ 
/*     */   public PointRoi(int ox, int oy)
/*     */   {
/*  33 */     super(makeXArray(ox, null), makeYArray(oy, null), 1, 10);
/*  34 */     this.width = 1; this.height = 1;
/*     */   }
/*     */ 
/*     */   public PointRoi(int sx, int sy, ImagePlus imp)
/*     */   {
/*  39 */     super(makeXArray(sx, imp), makeYArray(sy, imp), 1, 10);
/*  40 */     setImage(imp);
/*  41 */     this.width = 1; this.height = 1;
/*  42 */     if (imp != null) imp.draw(this.x - 5, this.y - 5, this.width + 10, this.height + 10);
/*  43 */     if ((Recorder.record) && (!Recorder.scriptMode()))
/*  44 */       Recorder.record("makePoint", this.x, this.y);
/*     */   }
/*     */ 
/*     */   static int[] makeXArray(int value, ImagePlus imp)
/*     */   {
/*  49 */     int[] array = new int[1];
/*  50 */     array[0] = (imp != null ? imp.getCanvas().offScreenX(value) : value);
/*  51 */     return array;
/*     */   }
/*     */ 
/*     */   static int[] makeYArray(int value, ImagePlus imp) {
/*  55 */     int[] array = new int[1];
/*  56 */     array[0] = (imp != null ? imp.getCanvas().offScreenY(value) : value);
/*  57 */     return array;
/*     */   }
/*     */ 
/*     */   void handleMouseMove(int ox, int oy)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void handleMouseUp(int sx, int sy) {
/*  65 */     super.handleMouseUp(sx, sy);
/*  66 */     modifyRoi();
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g)
/*     */   {
/*  72 */     updatePolygon();
/*  73 */     if (this.ic != null) this.mag = this.ic.getMagnification();
/*  74 */     int size2 = 2;
/*  75 */     if ((!Prefs.noPointLabels) && (!this.hideLabels) && (this.nPoints > 1)) {
/*  76 */       fontSize = 9;
/*  77 */       if (this.mag > 1.0D)
/*  78 */         fontSize = (int)(((this.mag - 1.0D) / 3.0D + 1.0D) * 9.0D);
/*  79 */       if (fontSize > 18) fontSize = 18;
/*  80 */       if ((font == null) || (this.mag != this.saveMag))
/*  81 */         font = new Font("SansSerif", 0, fontSize);
/*  82 */       g.setFont(font);
/*  83 */       if (fontSize > 9)
/*  84 */         Java2.setAntialiasedText(g, true);
/*  85 */       this.saveMag = this.mag;
/*     */     }
/*  87 */     for (int i = 0; i < this.nPoints; i++) {
/*  88 */       drawPoint(g, this.xp2[i] - size2, this.yp2[i] - size2, i + 1);
/*     */     }
/*  90 */     if (this.updateFullWindow) {
/*  91 */       this.updateFullWindow = false; this.imp.draw();
/*     */     }
/*     */   }
/*     */ 
/*  95 */   void drawPoint(Graphics g, int x, int y, int n) { g.setColor(this.fillColor != null ? this.fillColor : Color.white);
/*  96 */     g.drawLine(x - 4, y + 2, x + 8, y + 2);
/*  97 */     g.drawLine(x + 2, y - 4, x + 2, y + 8);
/*  98 */     g.setColor(this.strokeColor != null ? this.strokeColor : ROIColor);
/*  99 */     g.fillRect(x + 1, y + 1, 3, 3);
/* 100 */     if ((!Prefs.noPointLabels) && (!this.hideLabels) && (this.nPoints > 1))
/* 101 */       g.drawString("" + n, x + 6, y + fontSize + 4);
/* 102 */     g.setColor(Color.black);
/* 103 */     g.drawRect(x, y, 4, 4); }
/*     */ 
/*     */   public void drawPixels(ImageProcessor ip)
/*     */   {
/* 107 */     ip.setLineWidth(Analyzer.markWidth);
/* 108 */     for (int i = 0; i < this.nPoints; i++) {
/* 109 */       ip.moveTo(this.x + this.xp[i], this.y + this.yp[i]);
/* 110 */       ip.lineTo(this.x + this.xp[i], this.y + this.yp[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public PointRoi addPoint(int x, int y)
/*     */   {
/* 116 */     Polygon poly = getPolygon();
/* 117 */     poly.addPoint(x, y);
/* 118 */     PointRoi p = new PointRoi(poly.xpoints, poly.ypoints, poly.npoints);
/* 119 */     p.setHideLabels(this.hideLabels);
/* 120 */     IJ.showStatus("count=" + poly.npoints);
/* 121 */     return p;
/*     */   }
/*     */ 
/*     */   public PointRoi subtractPoints(Roi roi)
/*     */   {
/* 127 */     Polygon points = getPolygon();
/* 128 */     Polygon poly = roi.getPolygon();
/* 129 */     Polygon points2 = new Polygon();
/* 130 */     for (int i = 0; i < points.npoints; i++) {
/* 131 */       if (!poly.contains(points.xpoints[i], points.ypoints[i]))
/* 132 */         points2.addPoint(points.xpoints[i], points.ypoints[i]);
/*     */     }
/* 134 */     if (points2.npoints == 0) {
/* 135 */       return null;
/*     */     }
/* 137 */     return new PointRoi(points2.xpoints, points2.ypoints, points2.npoints);
/*     */   }
/*     */ 
/*     */   public ImageProcessor getMask() {
/* 141 */     if ((this.cachedMask != null) && (this.cachedMask.getPixels() != null))
/* 142 */       return this.cachedMask;
/* 143 */     ImageProcessor mask = new ByteProcessor(this.width, this.height);
/* 144 */     for (int i = 0; i < this.nPoints; i++) {
/* 145 */       mask.putPixel(this.xp[i], this.yp[i], 255);
/*     */     }
/* 147 */     this.cachedMask = mask;
/* 148 */     return mask;
/*     */   }
/*     */ 
/*     */   public boolean contains(int x, int y)
/*     */   {
/* 153 */     for (int i = 0; i < this.nPoints; i++) {
/* 154 */       if ((x == this.x + this.xp[i]) && (y == this.y + this.yp[i])) return true;
/*     */     }
/* 156 */     return false;
/*     */   }
/*     */ 
/*     */   public void setHideLabels(boolean hideLabels) {
/* 160 */     this.hideLabels = hideLabels;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 164 */     if (this.nPoints > 1) {
/* 165 */       return "Roi[Points, count=" + this.nPoints + "]";
/*     */     }
/* 167 */     return "Roi[Point, x=" + this.x + ", y=" + this.y + "]";
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.PointRoi
 * JD-Core Version:    0.6.2
 */