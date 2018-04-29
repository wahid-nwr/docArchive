/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.process.FloatPolygon;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class EllipseRoi extends PolygonRoi
/*     */ {
/*     */   private static final int vertices = 72;
/*  12 */   private static double defaultRatio = 0.6D;
/*     */   private double xstart;
/*     */   private double ystart;
/*  14 */   private double aspectRatio = defaultRatio;
/*  15 */   private int[] handle = { 0, 18, 36, 54 };
/*     */ 
/*     */   public EllipseRoi(double x1, double y1, double x2, double y2, double aspectRatio) {
/*  18 */     super(new float[72], new float[72], 72, 3);
/*  19 */     if (aspectRatio < 0.0D) aspectRatio = 0.0D;
/*  20 */     if (aspectRatio > 1.0D) aspectRatio = 1.0D;
/*  21 */     this.aspectRatio = aspectRatio;
/*  22 */     makeEllipse(x1, y1, x2, y2);
/*  23 */     this.state = 3;
/*     */   }
/*     */ 
/*     */   public EllipseRoi(int sx, int sy, ImagePlus imp) {
/*  27 */     super(sx, sy, imp);
/*  28 */     this.type = 3;
/*  29 */     this.xstart = this.ic.offScreenX(sx);
/*  30 */     this.ystart = this.ic.offScreenY(sy);
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g) {
/*  34 */     super.draw(g);
/*  35 */     int size2 = 2;
/*  36 */     if (!this.overlay) {
/*  37 */       this.mag = (this.ic != null ? this.ic.getMagnification() : 1.0D);
/*  38 */       for (int i = 0; i < this.handle.length; i++)
/*  39 */         drawHandle(g, this.xp2[this.handle[i]] - size2, this.yp2[this.handle[i]] - size2);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void grow(int sx, int sy) {
/*  44 */     double x1 = this.xpf[this.handle[2]] + this.x;
/*  45 */     double y1 = this.ypf[this.handle[2]] + this.y;
/*  46 */     double x2 = this.ic.offScreenXD(sx);
/*  47 */     double y2 = this.ic.offScreenYD(sy);
/*  48 */     makeEllipse(x1, y1, x2, y2);
/*  49 */     this.imp.draw();
/*     */   }
/*     */ 
/*     */   void makeEllipse(double x1, double y1, double x2, double y2) {
/*  53 */     double centerX = (x1 + x2) / 2.0D;
/*  54 */     double centerY = (y1 + y2) / 2.0D;
/*  55 */     double dx = x2 - x1;
/*  56 */     double dy = y2 - y1;
/*  57 */     double major = Math.sqrt(dx * dx + dy * dy);
/*  58 */     double minor = major * this.aspectRatio;
/*  59 */     double phiB = Math.atan2(dy, dx);
/*  60 */     double alpha = phiB * 180.0D / 3.141592653589793D;
/*  61 */     this.nPoints = 0;
/*  62 */     for (int i = 0; i < 72; i++) {
/*  63 */       double degrees = i * 360.0D / 72.0D;
/*  64 */       double beta1 = degrees / 180.0D * 3.141592653589793D;
/*  65 */       dx = Math.cos(beta1) * major / 2.0D;
/*  66 */       dy = Math.sin(beta1) * minor / 2.0D;
/*  67 */       double beta2 = Math.atan2(dy, dx);
/*  68 */       double rad = Math.sqrt(dx * dx + dy * dy);
/*  69 */       double beta3 = beta2 + alpha / 180.0D * 3.141592653589793D;
/*  70 */       double dx2 = Math.cos(beta3) * rad;
/*  71 */       double dy2 = Math.sin(beta3) * rad;
/*  72 */       this.xpf[this.nPoints] = ((float)(centerX + dx2));
/*  73 */       this.ypf[this.nPoints] = ((float)(centerY + dy2));
/*  74 */       this.nPoints += 1;
/*     */     }
/*  76 */     makePolygonRelative();
/*  77 */     this.cachedMask = null;
/*     */   }
/*     */ 
/*     */   void makePolygonRelative() {
/*  81 */     FloatPolygon poly = new FloatPolygon(this.xpf, this.ypf, this.nPoints);
/*  82 */     Rectangle r = poly.getBounds();
/*  83 */     this.x = r.x;
/*  84 */     this.y = r.y;
/*  85 */     this.width = r.width;
/*  86 */     this.height = r.height;
/*  87 */     for (int i = 0; i < this.nPoints; i++) {
/*  88 */       this.xpf[i] -= this.x;
/*  89 */       this.ypf[i] -= this.y;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void handleMouseUp(int screenX, int screenY) {
/*  94 */     if (this.state == 0) {
/*  95 */       addOffset();
/*  96 */       finishPolygon();
/*  97 */       if (Recorder.record) {
/*  98 */         double x1 = this.xpf[this.handle[2]] + this.x;
/*  99 */         double y1 = this.ypf[this.handle[2]] + this.y;
/* 100 */         double x2 = this.xpf[this.handle[0]] + this.x;
/* 101 */         double y2 = this.ypf[this.handle[0]] + this.y;
/* 102 */         if (Recorder.scriptMode())
/* 103 */           Recorder.recordCall("imp.setRoi(new EllipseRoi(" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ", " + IJ.d2s(this.aspectRatio, 2) + "));");
/*     */         else
/* 105 */           Recorder.record("makeEllipse", (int)x1, (int)y1, (int)x2, (int)y2, this.aspectRatio);
/*     */       }
/*     */     }
/* 108 */     this.state = 3;
/*     */   }
/*     */ 
/*     */   protected void moveHandle(int sx, int sy) {
/* 112 */     double ox = this.ic.offScreenXD(sx);
/* 113 */     double oy = this.ic.offScreenYD(sy);
/* 114 */     double x1 = this.xpf[this.handle[2]] + this.x;
/* 115 */     double y1 = this.ypf[this.handle[2]] + this.y;
/* 116 */     double x2 = this.xpf[this.handle[0]] + this.x;
/* 117 */     double y2 = this.ypf[this.handle[0]] + this.y;
/*     */     double dx;
/*     */     double dy;
/* 118 */     switch (this.activeHandle) {
/*     */     case 0:
/* 120 */       x2 = ox;
/* 121 */       y2 = oy;
/* 122 */       break;
/*     */     case 1:
/* 124 */       dx = this.xpf[this.handle[3]] + this.x - ox;
/* 125 */       dy = this.ypf[this.handle[3]] + this.y - oy;
/* 126 */       updateRatio(Math.sqrt(dx * dx + dy * dy), x1, y1, x2, y2);
/* 127 */       break;
/*     */     case 2:
/* 129 */       x1 = ox;
/* 130 */       y1 = oy;
/* 131 */       break;
/*     */     case 3:
/* 133 */       dx = this.xpf[this.handle[1]] + this.x - ox;
/* 134 */       dy = this.ypf[this.handle[1]] + this.y - oy;
/* 135 */       updateRatio(Math.sqrt(dx * dx + dy * dy), x1, y1, x2, y2);
/*     */     }
/*     */ 
/* 138 */     makeEllipse(x1, y1, x2, y2);
/* 139 */     this.imp.draw();
/*     */   }
/*     */ 
/*     */   void updateRatio(double minor, double x1, double y1, double x2, double y2) {
/* 143 */     double dx = x2 - x1;
/* 144 */     double dy = y2 - y1;
/* 145 */     double major = Math.sqrt(dx * dx + dy * dy);
/* 146 */     this.aspectRatio = (minor / major);
/* 147 */     if (this.aspectRatio > 1.0D) this.aspectRatio = 1.0D;
/* 148 */     defaultRatio = this.aspectRatio;
/*     */   }
/*     */ 
/*     */   public int isHandle(int sx, int sy) {
/* 152 */     int size = 10;
/* 153 */     int halfSize = size / 2;
/* 154 */     int index = -1;
/* 155 */     for (int i = 0; i < this.handle.length; i++) {
/* 156 */       int sx2 = this.xp2[this.handle[i]] - halfSize; int sy2 = this.yp2[this.handle[i]] - halfSize;
/* 157 */       if ((sx >= sx2) && (sx <= sx2 + size) && (sy >= sy2) && (sy <= sy2 + size)) {
/* 158 */         index = i;
/* 159 */         break;
/*     */       }
/*     */     }
/* 162 */     return index;
/*     */   }
/*     */ 
/*     */   public double getLength()
/*     */   {
/* 167 */     double length = 0.0D;
/*     */ 
/* 169 */     double w2 = 1.0D; double h2 = 1.0D;
/* 170 */     if (this.imp != null) {
/* 171 */       Calibration cal = this.imp.getCalibration();
/* 172 */       w2 = cal.pixelWidth * cal.pixelWidth;
/* 173 */       h2 = cal.pixelHeight * cal.pixelHeight;
/*     */     }
/* 175 */     for (int i = 0; i < this.nPoints - 1; i++) {
/* 176 */       double dx = this.xpf[(i + 1)] - this.xpf[i];
/* 177 */       double dy = this.ypf[(i + 1)] - this.ypf[i];
/* 178 */       length += Math.sqrt(dx * dx * w2 + dy * dy * h2);
/*     */     }
/* 180 */     double dx = this.xpf[0] - this.xpf[(this.nPoints - 1)];
/* 181 */     double dy = this.ypf[0] - this.ypf[(this.nPoints - 1)];
/* 182 */     length += Math.sqrt(dx * dx * w2 + dy * dy * h2);
/* 183 */     return length;
/*     */   }
/*     */ 
/*     */   public double[] getParams()
/*     */   {
/* 188 */     double[] params = new double[5];
/* 189 */     params[0] = (this.xpf[this.handle[2]] + this.x);
/* 190 */     params[1] = (this.ypf[this.handle[2]] + this.y);
/* 191 */     params[2] = (this.xpf[this.handle[0]] + this.x);
/* 192 */     params[3] = (this.ypf[this.handle[0]] + this.y);
/* 193 */     params[4] = this.aspectRatio;
/* 194 */     return params;
/*     */   }
/*     */ 
/*     */   public double[] getFeretValues() {
/* 198 */     double[] a = super.getFeretValues();
/* 199 */     double pw = 1.0D; double ph = 1.0D;
/* 200 */     if (this.imp != null) {
/* 201 */       Calibration cal = this.imp.getCalibration();
/* 202 */       pw = cal.pixelWidth;
/* 203 */       ph = cal.pixelHeight;
/*     */     }
/* 205 */     double[] p = getParams();
/* 206 */     double dx = (p[2] - p[0]) * pw;
/* 207 */     double dy = (p[3] - p[1]) * ph;
/* 208 */     double major = Math.sqrt(dx * dx + dy * dy);
/* 209 */     double minor = major * p[4];
/* 210 */     a[0] = major;
/* 211 */     a[2] = (pw == ph ? minor : a[2]);
/* 212 */     return a;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.EllipseRoi
 * JD-Core Version:    0.6.2
 */