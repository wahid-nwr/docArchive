/*      */ package ij.gui;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.Prefs;
/*      */ import ij.measure.Calibration;
/*      */ import ij.measure.SplineFitter;
/*      */ import ij.plugin.frame.LineWidthAdjuster;
/*      */ import ij.plugin.frame.Recorder;
/*      */ import ij.process.FloatPolygon;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.process.PolygonFiller;
/*      */ import java.awt.Color;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Polygon;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.geom.GeneralPath;
/*      */ 
/*      */ public class PolygonRoi extends Roi
/*      */ {
/*   13 */   protected int maxPoints = 1000;
/*      */   protected int[] xp;
/*      */   protected int[] yp;
/*      */   protected float[] xpf;
/*      */   protected float[] ypf;
/*      */   protected int[] xp2;
/*      */   protected int[] yp2;
/*      */   protected int nPoints;
/*      */   protected float[] xSpline;
/*      */   protected float[] ySpline;
/*   19 */   protected int splinePoints = 200;
/*      */   Rectangle clip;
/*      */   private double angle1;
/*   22 */   private double degrees = (0.0D / 0.0D);
/*      */   private int xClipMin;
/*      */   private int yClipMin;
/*      */   private int xClipMax;
/*      */   private int yClipMax;
/*      */   private boolean userCreated;
/*   26 */   long mouseUpTime = 0L;
/*      */ 
/*      */   public PolygonRoi(int[] xPoints, int[] yPoints, int nPoints, int type)
/*      */   {
/*   31 */     super(0, 0, null);
/*   32 */     init1(nPoints, type);
/*   33 */     this.xp = xPoints;
/*   34 */     this.yp = yPoints;
/*   35 */     if (type != 4) {
/*   36 */       this.xp = new int[nPoints];
/*   37 */       this.yp = new int[nPoints];
/*   38 */       for (int i = 0; i < nPoints; i++) {
/*   39 */         this.xp[i] = xPoints[i];
/*   40 */         this.yp[i] = yPoints[i];
/*      */       }
/*      */     }
/*   43 */     this.xp2 = new int[nPoints];
/*   44 */     this.yp2 = new int[nPoints];
/*   45 */     init2(type);
/*      */   }
/*      */ 
/*      */   public PolygonRoi(float[] xPoints, float[] yPoints, int nPoints, int type)
/*      */   {
/*   51 */     super(0, 0, null);
/*   52 */     init1(nPoints, type);
/*   53 */     this.xpf = xPoints;
/*   54 */     this.ypf = yPoints;
/*   55 */     this.xp2 = new int[nPoints];
/*   56 */     this.yp2 = new int[nPoints];
/*   57 */     init2(type);
/*      */   }
/*      */ 
/*      */   private void init1(int nPoints, int type) throws IllegalArgumentException {
/*   61 */     this.maxPoints = nPoints;
/*   62 */     this.nPoints = nPoints;
/*   63 */     if (type == 2)
/*   64 */       this.type = 2;
/*   65 */     else if (type == 3)
/*   66 */       this.type = 3;
/*   67 */     else if (type == 4)
/*   68 */       this.type = 4;
/*   69 */     else if (type == 6)
/*   70 */       this.type = 6;
/*   71 */     else if (type == 7)
/*   72 */       this.type = 7;
/*   73 */     else if (type == 8)
/*   74 */       this.type = 8;
/*   75 */     else if (type == 10)
/*   76 */       this.type = 10;
/*      */     else
/*   78 */       throw new IllegalArgumentException("Invalid type");
/*      */   }
/*      */ 
/*      */   private void init2(int type) {
/*   82 */     if ((type == 8) && (this.nPoints == 3))
/*   83 */       getAngleAsString();
/*   84 */     if ((type == 10) && (Toolbar.getMultiPointMode())) {
/*   85 */       Prefs.pointAutoMeasure = false;
/*   86 */       Prefs.pointAutoNextSlice = false;
/*   87 */       Prefs.pointAddToManager = false;
/*   88 */       this.userCreated = true;
/*      */     }
/*   90 */     if ((lineWidth > 1) && (isLine()))
/*   91 */       updateWideLine(lineWidth);
/*   92 */     finishPolygon();
/*      */   }
/*      */ 
/*      */   public PolygonRoi(Polygon p, int type)
/*      */   {
/*   98 */     this(p.xpoints, p.ypoints, p.npoints, type);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public PolygonRoi(int[] xPoints, int[] yPoints, int nPoints, ImagePlus imp, int type) {
/*  103 */     this(xPoints, yPoints, nPoints, type);
/*  104 */     setImage(imp);
/*      */   }
/*      */ 
/*      */   public PolygonRoi(int sx, int sy, ImagePlus imp)
/*      */   {
/*  109 */     super(sx, sy, imp);
/*  110 */     int tool = Toolbar.getToolId();
/*  111 */     switch (tool) { case 2:
/*  112 */       this.type = 2; break;
/*      */     case 3:
/*  113 */       this.type = 3; break;
/*      */     case 6:
/*  114 */       this.type = 7; break;
/*      */     case 14:
/*  115 */       this.type = 8; break;
/*      */     default:
/*  116 */       this.type = 6;
/*      */     }
/*  118 */     if ((this instanceof EllipseRoi)) {
/*  119 */       this.xpf = new float[this.maxPoints];
/*  120 */       this.ypf = new float[this.maxPoints];
/*      */     } else {
/*  122 */       this.xp = new int[this.maxPoints];
/*  123 */       this.yp = new int[this.maxPoints];
/*      */     }
/*  125 */     this.xp2 = new int[this.maxPoints];
/*  126 */     this.yp2 = new int[this.maxPoints];
/*  127 */     this.nPoints = 2;
/*  128 */     this.x = this.ic.offScreenX(sx);
/*  129 */     this.y = this.ic.offScreenY(sy);
/*  130 */     this.width = 1;
/*  131 */     this.height = 1;
/*  132 */     this.clipX = this.x;
/*  133 */     this.clipY = this.y;
/*  134 */     this.clipWidth = 1;
/*  135 */     this.clipHeight = 1;
/*  136 */     this.state = 0;
/*  137 */     this.userCreated = true;
/*  138 */     if ((lineWidth > 1) && (isLine()))
/*  139 */       updateWideLine(lineWidth);
/*      */   }
/*      */ 
/*      */   private void drawStartBox(Graphics g) {
/*  143 */     if (this.type != 8)
/*  144 */       g.drawRect(this.ic.screenX(this.startX) - 4, this.ic.screenY(this.startY) - 4, 8, 8);
/*      */   }
/*      */ 
/*      */   public void draw(Graphics g) {
/*  148 */     updatePolygon();
/*  149 */     Color color = this.strokeColor != null ? this.strokeColor : ROIColor;
/*  150 */     boolean fill = false;
/*  151 */     if ((this.fillColor != null) && (!isLine()) && (this.state != 0)) {
/*  152 */       color = this.fillColor;
/*  153 */       fill = true;
/*      */     }
/*  155 */     g.setColor(color);
/*  156 */     Graphics2D g2d = (Graphics2D)g;
/*  157 */     if (this.stroke != null)
/*  158 */       g2d.setStroke(getScaledStroke());
/*  159 */     if (this.xSpline != null) {
/*  160 */       if ((this.type == 6) || (this.type == 7)) {
/*  161 */         drawSpline(g, this.xSpline, this.ySpline, this.splinePoints, false, fill);
/*  162 */         if ((this.wideLine) && (!this.overlay)) {
/*  163 */           g2d.setStroke(onePixelWide);
/*  164 */           g.setColor(getColor());
/*  165 */           drawSpline(g, this.xSpline, this.ySpline, this.splinePoints, false, fill);
/*      */         }
/*      */       } else {
/*  168 */         drawSpline(g, this.xSpline, this.ySpline, this.splinePoints, true, fill);
/*      */       }
/*      */     } else { if ((this.type == 6) || (this.type == 7) || (this.type == 8) || (this.state == 0)) {
/*  171 */         g.drawPolyline(this.xp2, this.yp2, this.nPoints);
/*  172 */         if ((this.wideLine) && (!this.overlay)) {
/*  173 */           g2d.setStroke(onePixelWide);
/*  174 */           g.setColor(getColor());
/*  175 */           g.drawPolyline(this.xp2, this.yp2, this.nPoints);
/*      */         }
/*      */       }
/*  178 */       else if (fill) {
/*  179 */         g.fillPolygon(this.xp2, this.yp2, this.nPoints);
/*      */       } else {
/*  181 */         g.drawPolygon(this.xp2, this.yp2, this.nPoints);
/*      */       }
/*  183 */       if ((this.state == 0) && (this.type != 3) && (this.type != 7))
/*  184 */         drawStartBox(g);
/*      */     }
/*  186 */     if (((this.xSpline != null) || (this.type == 2) || (this.type == 6) || (this.type == 8)) && (this.state != 0) && (this.clipboard == null) && (!this.overlay))
/*      */     {
/*  188 */       this.mag = getMagnification();
/*  189 */       int size2 = 2;
/*  190 */       if (this.activeHandle > 0)
/*  191 */         drawHandle(g, this.xp2[(this.activeHandle - 1)] - size2, this.yp2[(this.activeHandle - 1)] - size2);
/*  192 */       if (this.activeHandle < this.nPoints - 1)
/*  193 */         drawHandle(g, this.xp2[(this.activeHandle + 1)] - size2, this.yp2[(this.activeHandle + 1)] - size2);
/*  194 */       this.handleColor = (this.strokeColor != null ? this.strokeColor : ROIColor); drawHandle(g, this.xp2[0] - size2, this.yp2[0] - size2); this.handleColor = Color.white;
/*  195 */       for (int i = 1; i < this.nPoints; i++)
/*  196 */         drawHandle(g, this.xp2[i] - size2, this.yp2[i] - size2);
/*      */     }
/*  198 */     drawPreviousRoi(g);
/*  199 */     if ((this.state != 4) && (this.state != 0) && (this.state != 3))
/*  200 */       showStatus();
/*  201 */     if (this.updateFullWindow) {
/*  202 */       this.updateFullWindow = false; this.imp.draw();
/*      */     }
/*      */   }
/*      */ 
/*  206 */   private void drawSpline(Graphics g, float[] xpoints, float[] ypoints, int npoints, boolean closed, boolean fill) { float srcx = 0.0F; float srcy = 0.0F; float mag = 1.0F;
/*  207 */     if (this.ic != null) {
/*  208 */       Rectangle srcRect = this.ic.getSrcRect();
/*  209 */       srcx = srcRect.x; srcy = srcRect.y;
/*  210 */       mag = (float)this.ic.getMagnification();
/*      */     }
/*  212 */     float xf = this.x; float yf = this.y;
/*  213 */     Graphics2D g2d = (Graphics2D)g;
/*  214 */     GeneralPath path = new GeneralPath();
/*  215 */     if ((mag == 1.0F) && (srcx == 0.0F) && (srcy == 0.0F)) {
/*  216 */       path.moveTo(xpoints[0] + xf, ypoints[0] + yf);
/*  217 */       for (int i = 1; i < npoints; i++)
/*  218 */         path.lineTo(xpoints[i] + xf, ypoints[i] + yf);
/*      */     } else {
/*  220 */       path.moveTo((xpoints[0] - srcx + xf) * mag, (ypoints[0] - srcy + yf) * mag);
/*  221 */       for (int i = 1; i < npoints; i++)
/*  222 */         path.lineTo((xpoints[i] - srcx + xf) * mag, (ypoints[i] - srcy + yf) * mag);
/*      */     }
/*  224 */     if (closed)
/*  225 */       path.lineTo((xpoints[0] - srcx + xf) * mag, (ypoints[0] - srcy + yf) * mag);
/*  226 */     if (fill)
/*  227 */       g2d.fill(path);
/*      */     else
/*  229 */       g2d.draw(path); }
/*      */ 
/*      */   public void drawPixels(ImageProcessor ip)
/*      */   {
/*  233 */     int saveWidth = ip.getLineWidth();
/*  234 */     if (getStrokeWidth() > 1.0F)
/*  235 */       ip.setLineWidth(Math.round(getStrokeWidth()));
/*  236 */     if (this.xSpline != null) {
/*  237 */       ip.moveTo(this.x + (int)(Math.floor(this.xSpline[0]) + 0.5D), this.y + (int)Math.floor(this.ySpline[0] + 0.5D));
/*  238 */       for (int i = 1; i < this.splinePoints; i++)
/*  239 */         ip.lineTo(this.x + (int)(Math.floor(this.xSpline[i]) + 0.5D), this.y + (int)Math.floor(this.ySpline[i] + 0.5D));
/*  240 */       if ((this.type == 2) || (this.type == 3) || (this.type == 4))
/*  241 */         ip.lineTo(this.x + (int)(Math.floor(this.xSpline[0]) + 0.5D), this.y + (int)Math.floor(this.ySpline[0] + 0.5D));
/*  242 */     } else if (this.xpf != null) {
/*  243 */       ip.moveTo(this.x + (int)(Math.floor(this.xpf[0]) + 0.5D), this.y + (int)Math.floor(this.ypf[0] + 0.5D));
/*  244 */       for (int i = 1; i < this.nPoints; i++)
/*  245 */         ip.lineTo(this.x + (int)(Math.floor(this.xpf[i]) + 0.5D), this.y + (int)Math.floor(this.ypf[i] + 0.5D));
/*  246 */       if ((this.type == 2) || (this.type == 3) || (this.type == 4))
/*  247 */         ip.lineTo(this.x + (int)(Math.floor(this.xpf[0]) + 0.5D), this.y + (int)Math.floor(this.ypf[0] + 0.5D));
/*      */     } else {
/*  249 */       ip.moveTo(this.x + this.xp[0], this.y + this.yp[0]);
/*  250 */       for (int i = 1; i < this.nPoints; i++)
/*  251 */         ip.lineTo(this.x + this.xp[i], this.y + this.yp[i]);
/*  252 */       if ((this.type == 2) || (this.type == 3) || (this.type == 4))
/*  253 */         ip.lineTo(this.x + this.xp[0], this.y + this.yp[0]);
/*      */     }
/*  255 */     ip.setLineWidth(saveWidth);
/*  256 */     this.updateFullWindow = true;
/*      */   }
/*      */ 
/*      */   protected void grow(int sx, int sy)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void updatePolygon()
/*      */   {
/*  265 */     int basex = 0; int basey = 0;
/*  266 */     if (this.ic != null) {
/*  267 */       Rectangle srcRect = this.ic.getSrcRect();
/*  268 */       basex = srcRect.x; basey = srcRect.y;
/*      */     }
/*  270 */     if ((getMagnification() == 1.0D) && (basex == 0) && (basey == 0)) {
/*  271 */       if (this.xpf != null)
/*  272 */         for (int i = 0; i < this.nPoints; i++) {
/*  273 */           this.xp2[i] = ((int)(this.xpf[i] + this.x));
/*  274 */           this.yp2[i] = ((int)(this.ypf[i] + this.y));
/*      */         }
/*      */       else {
/*  277 */         for (int i = 0; i < this.nPoints; i++) {
/*  278 */           this.xp2[i] = (this.xp[i] + this.x);
/*  279 */           this.yp2[i] = (this.yp[i] + this.y);
/*      */         }
/*      */       }
/*      */     }
/*  283 */     else if (this.xpf != null)
/*  284 */       for (int i = 0; i < this.nPoints; i++) {
/*  285 */         this.xp2[i] = this.ic.screenXD(this.xpf[i] + this.x);
/*  286 */         this.yp2[i] = this.ic.screenYD(this.ypf[i] + this.y);
/*      */       }
/*      */     else
/*  289 */       for (int i = 0; i < this.nPoints; i++) {
/*  290 */         this.xp2[i] = this.ic.screenX(this.xp[i] + this.x);
/*  291 */         this.yp2[i] = this.ic.screenY(this.yp[i] + this.y);
/*      */       }
/*      */   }
/*      */ 
/*      */   void handleMouseMove(int ox, int oy)
/*      */   {
/*  299 */     int tool = Toolbar.getToolId();
/*  300 */     if ((tool != 2) && (tool != 5) && (tool != 14)) {
/*  301 */       this.imp.killRoi();
/*  302 */       this.imp.draw();
/*  303 */       return;
/*      */     }
/*  305 */     drawRubberBand(ox, oy);
/*  306 */     this.degrees = (0.0D / 0.0D);
/*  307 */     double len = -1.0D;
/*  308 */     if (this.nPoints > 1) {
/*  309 */       int x1 = this.xp[(this.nPoints - 2)];
/*  310 */       int y1 = this.yp[(this.nPoints - 2)];
/*  311 */       int x2 = this.xp[(this.nPoints - 1)];
/*  312 */       int y2 = this.yp[(this.nPoints - 1)];
/*  313 */       this.degrees = getAngle(x1, y1, x2, y2);
/*  314 */       if (tool != 14) {
/*  315 */         Calibration cal = this.imp.getCalibration();
/*  316 */         double pw = cal.pixelWidth; double ph = cal.pixelHeight;
/*  317 */         if (IJ.altKeyDown()) { pw = 1.0D; ph = 1.0D; }
/*  318 */         len = Math.sqrt((x2 - x1) * pw * (x2 - x1) * pw + (y2 - y1) * ph * (y2 - y1) * ph);
/*      */       }
/*      */     }
/*  321 */     if (tool == 14) {
/*  322 */       if (this.nPoints == 2) {
/*  323 */         this.angle1 = this.degrees;
/*  324 */       } else if (this.nPoints == 3) {
/*  325 */         double angle2 = getAngle(this.xp[1], this.yp[1], this.xp[2], this.yp[2]);
/*  326 */         this.degrees = Math.abs(180.0D - Math.abs(this.angle1 - angle2));
/*  327 */         if (this.degrees > 180.0D)
/*  328 */           this.degrees = (360.0D - this.degrees);
/*      */       }
/*      */     }
/*  331 */     String length = len != -1.0D ? ", length=" + IJ.d2s(len) : "";
/*  332 */     double degrees2 = (tool == 14) && (this.nPoints == 3) && (Prefs.reflexAngle) ? 360.0D - this.degrees : this.degrees;
/*  333 */     String angle = !Double.isNaN(this.degrees) ? ", angle=" + IJ.d2s(degrees2) : "";
/*  334 */     IJ.showStatus(this.imp.getLocationAsString(ox, oy) + length + angle);
/*      */   }
/*      */ 
/*      */   void drawRubberBand(int ox, int oy) {
/*  338 */     int x1 = this.xp[(this.nPoints - 2)] + this.x;
/*  339 */     int y1 = this.yp[(this.nPoints - 2)] + this.y;
/*  340 */     int x2 = this.xp[(this.nPoints - 1)] + this.x;
/*  341 */     int y2 = this.yp[(this.nPoints - 1)] + this.y;
/*  342 */     int xmin = 9999; int ymin = 9999; int xmax = 0; int ymax = 0;
/*  343 */     if (x1 < xmin) xmin = x1;
/*  344 */     if (x2 < xmin) xmin = x2;
/*  345 */     if (ox < xmin) xmin = ox;
/*  346 */     if (x1 > xmax) xmax = x1;
/*  347 */     if (x2 > xmax) xmax = x2;
/*  348 */     if (ox > xmax) xmax = ox;
/*  349 */     if (y1 < ymin) ymin = y1;
/*  350 */     if (y2 < ymin) ymin = y2;
/*  351 */     if (oy < ymin) ymin = oy;
/*  352 */     if (y1 > ymax) ymax = y1;
/*  353 */     if (y2 > ymax) ymax = y2;
/*  354 */     if (oy > ymax) ymax = oy;
/*      */ 
/*  356 */     int margin = 4;
/*  357 */     if (this.ic != null) {
/*  358 */       double mag = this.ic.getMagnification();
/*  359 */       if (mag < 1.0D) margin = (int)(margin / mag);
/*      */     }
/*  361 */     margin = (int)(margin + getStrokeWidth());
/*  362 */     this.xp[(this.nPoints - 1)] = (ox - this.x);
/*  363 */     this.yp[(this.nPoints - 1)] = (oy - this.y);
/*  364 */     this.imp.draw(xmin - margin, ymin - margin, xmax - xmin + margin * 2, ymax - ymin + margin * 2);
/*      */   }
/*      */ 
/*      */   void finishPolygon()
/*      */   {
/*      */     Rectangle r;
/*      */     Rectangle r;
/*  369 */     if (this.xpf != null) {
/*  370 */       FloatPolygon poly = new FloatPolygon(this.xpf, this.ypf, this.nPoints);
/*  371 */       r = poly.getBounds();
/*      */     } else {
/*  373 */       Polygon poly = new Polygon(this.xp, this.yp, this.nPoints);
/*  374 */       r = poly.getBounds();
/*      */     }
/*  376 */     this.x = r.x;
/*  377 */     this.y = r.y;
/*  378 */     this.width = r.width;
/*  379 */     this.height = r.height;
/*  380 */     if (this.xpf != null)
/*  381 */       for (int i = 0; i < this.nPoints; i++) {
/*  382 */         this.xpf[i] -= this.x;
/*  383 */         this.ypf[i] -= this.y;
/*      */       }
/*      */     else {
/*  386 */       for (int i = 0; i < this.nPoints; i++) {
/*  387 */         this.xp[i] -= this.x;
/*  388 */         this.yp[i] -= this.y;
/*      */       }
/*      */     }
/*  391 */     if ((this.nPoints < 2) || ((this.type != 7) && (this.type != 6) && (this.type != 8) && ((this.nPoints < 3) || (this.width == 0) || (this.height == 0)))) {
/*  392 */       if (this.imp != null) this.imp.killRoi();
/*  393 */       if (this.type != 10) return;
/*      */     }
/*  395 */     this.state = 3;
/*  396 */     if ((this.imp != null) && (this.type != 4))
/*  397 */       this.imp.draw(this.x - 5, this.y - 5, this.width + 10, this.height + 10);
/*  398 */     this.oldX = this.x; this.oldY = this.y; this.oldWidth = this.width; this.oldHeight = this.height;
/*  399 */     if ((Recorder.record) && (this.userCreated) && ((this.type == 2) || (this.type == 6) || (this.type == 8) || ((this.type == 10) && (Recorder.scriptMode()) && (this.nPoints == 3))))
/*      */     {
/*  401 */       Recorder.recordRoi(getPolygon(), this.type);
/*  402 */     }if (this.type != 10) modifyRoi();
/*  403 */     LineWidthAdjuster.update();
/*      */   }
/*      */ 
/*      */   public void exitConstructingMode() {
/*  407 */     if ((this.type == 6) && (this.state == 0)) {
/*  408 */       addOffset();
/*  409 */       finishPolygon();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void moveHandle(int sx, int sy) {
/*  414 */     if (this.clipboard != null) return;
/*  415 */     int ox = this.ic.offScreenX(sx);
/*  416 */     int oy = this.ic.offScreenY(sy);
/*  417 */     this.xp[this.activeHandle] = (ox - this.x);
/*  418 */     this.yp[this.activeHandle] = (oy - this.y);
/*  419 */     if (this.xSpline != null) {
/*  420 */       fitSpline(this.splinePoints);
/*  421 */       updateClipRect();
/*  422 */       this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/*  423 */       this.oldX = this.x; this.oldY = this.y;
/*  424 */       this.oldWidth = this.width; this.oldHeight = this.height;
/*      */     } else {
/*  426 */       resetBoundingRect();
/*  427 */       if ((this.type == 10) && (this.width == 0) && (this.height == 0)) {
/*  428 */         this.width = 1; this.height = 1;
/*  429 */       }updateClipRectAndDraw();
/*      */     }
/*  431 */     String angle = this.type == 8 ? getAngleAsString() : "";
/*  432 */     IJ.showStatus(this.imp.getLocationAsString(ox, oy) + angle);
/*      */   }
/*      */ 
/*      */   void updateClipRectAndDraw()
/*      */   {
/*  437 */     int xmin = 2147483647; int ymin = 2147483647; int xmax = 0; int ymax = 0;
/*      */     int y2;
/*  439 */     if (this.activeHandle > 0) {
/*  440 */       int x2 = this.x + this.xp[(this.activeHandle - 1)]; y2 = this.y + this.yp[(this.activeHandle - 1)];
/*      */     } else {
/*  442 */       x2 = this.x + this.xp[(this.nPoints - 1)]; y2 = this.y + this.yp[(this.nPoints - 1)];
/*  443 */     }if (x2 < xmin) xmin = x2;
/*  444 */     if (y2 < ymin) ymin = y2;
/*  445 */     if (x2 > xmax) xmax = x2;
/*  446 */     if (y2 > ymax) ymax = y2;
/*  447 */     int x2 = this.x + this.xp[this.activeHandle]; int y2 = this.y + this.yp[this.activeHandle];
/*  448 */     if (x2 < xmin) xmin = x2;
/*  449 */     if (y2 < ymin) ymin = y2;
/*  450 */     if (x2 > xmax) xmax = x2;
/*  451 */     if (y2 > ymax) ymax = y2;
/*  452 */     if (this.activeHandle < this.nPoints - 1) {
/*  453 */       x2 = this.x + this.xp[(this.activeHandle + 1)]; y2 = this.y + this.yp[(this.activeHandle + 1)];
/*      */     } else {
/*  455 */       x2 = this.x + this.xp[0]; y2 = this.y + this.yp[0];
/*  456 */     }if (x2 < xmin) xmin = x2;
/*  457 */     if (y2 < ymin) ymin = y2;
/*  458 */     if (x2 > xmax) xmax = x2;
/*  459 */     if (y2 > ymax) ymax = y2;
/*  460 */     int xmin2 = xmin; int ymin2 = ymin; int xmax2 = xmax; int ymax2 = ymax;
/*  461 */     if (this.xClipMin < xmin2) xmin2 = this.xClipMin;
/*  462 */     if (this.yClipMin < ymin2) ymin2 = this.yClipMin;
/*  463 */     if (this.xClipMax > xmax2) xmax2 = this.xClipMax;
/*  464 */     if (this.yClipMax > ymax2) ymax2 = this.yClipMax;
/*  465 */     this.xClipMin = xmin; this.yClipMin = ymin; this.xClipMax = xmax; this.yClipMax = ymax;
/*  466 */     double mag = this.ic.getMagnification();
/*  467 */     int handleSize = this.type == 10 ? 13 : 5;
/*  468 */     if ((handleSize < getStrokeWidth()) && (isLine())) handleSize = (int)getStrokeWidth();
/*  469 */     int m = mag < 1.0D ? (int)(handleSize / mag) : handleSize;
/*  470 */     m = (int)(m * getStrokeWidth());
/*  471 */     this.imp.draw(xmin2 - m, ymin2 - m, xmax2 - xmin2 + m * 2, ymax2 - ymin2 + m * 2);
/*      */   }
/*      */ 
/*      */   void resetBoundingRect() {
/*  475 */     int xmin = 2147483647; int xmax = -xmin; int ymin = xmin; int ymax = xmax;
/*      */ 
/*  477 */     for (int i = 0; i < this.nPoints; i++) {
/*  478 */       int xx = this.xp[i];
/*  479 */       if (xx < xmin) xmin = xx;
/*  480 */       if (xx > xmax) xmax = xx;
/*  481 */       int yy = this.yp[i];
/*  482 */       if (yy < ymin) ymin = yy;
/*  483 */       if (yy > ymax) ymax = yy;
/*      */     }
/*  485 */     if (xmin != 0)
/*  486 */       for (int i = 0; i < this.nPoints; i++)
/*  487 */         this.xp[i] -= xmin;
/*  488 */     if (ymin != 0) {
/*  489 */       for (int i = 0; i < this.nPoints; i++)
/*  490 */         this.yp[i] -= ymin;
/*      */     }
/*  492 */     this.x += xmin; this.y += ymin;
/*  493 */     this.width = (xmax - xmin); this.height = (ymax - ymin);
/*      */   }
/*      */ 
/*      */   String getAngleAsString() {
/*  497 */     double angle1 = getAngle(this.xp[0], this.yp[0], this.xp[1], this.yp[1]);
/*  498 */     double angle2 = getAngle(this.xp[1], this.yp[1], this.xp[2], this.yp[2]);
/*  499 */     this.degrees = Math.abs(180.0D - Math.abs(angle1 - angle2));
/*  500 */     if (this.degrees > 180.0D)
/*  501 */       this.degrees = (360.0D - this.degrees);
/*  502 */     double degrees2 = (Prefs.reflexAngle) && (this.type == 8) ? 360.0D - this.degrees : this.degrees;
/*  503 */     return ", angle=" + IJ.d2s(degrees2);
/*      */   }
/*      */ 
/*      */   protected void mouseDownInHandle(int handle, int sx, int sy) {
/*  507 */     if (this.state == 0)
/*  508 */       return;
/*  509 */     int ox = this.ic.offScreenX(sx); int oy = this.ic.offScreenY(sy);
/*  510 */     if ((IJ.altKeyDown()) && ((this.nPoints > 3) || (this.type == 10))) {
/*  511 */       deleteHandle(ox, oy);
/*  512 */       return;
/*  513 */     }if ((IJ.shiftKeyDown()) && (this.type != 10)) {
/*  514 */       addHandle(ox, oy);
/*  515 */       return;
/*      */     }
/*  517 */     this.state = 4;
/*  518 */     this.activeHandle = handle;
/*  519 */     int m = (int)(10.0D / this.ic.getMagnification());
/*  520 */     this.xClipMin = (ox - m); this.yClipMin = (oy - m); this.xClipMax = (ox + m); this.yClipMax = (oy + m);
/*      */   }
/*      */ 
/*      */   public void deleteHandle(int ox, int oy) {
/*  524 */     if (this.imp == null) return;
/*  525 */     if (this.nPoints <= 1) {
/*  526 */       this.imp.killRoi(); return;
/*  527 */     }boolean splineFit = this.xSpline != null;
/*  528 */     this.xSpline = null;
/*  529 */     Polygon points = getPolygon();
/*  530 */     this.modState = 0;
/*  531 */     if (previousRoi != null) previousRoi.modState = 0;
/*  532 */     int pointToDelete = getClosestPoint(ox, oy, points);
/*  533 */     Polygon points2 = new Polygon();
/*  534 */     for (int i = 0; i < points.npoints; i++) {
/*  535 */       if (i != pointToDelete)
/*  536 */         points2.addPoint(points.xpoints[i], points.ypoints[i]);
/*      */     }
/*  538 */     if (this.type == 10) {
/*  539 */       this.imp.setRoi(new PointRoi(points2.xpoints, points2.ypoints, points2.npoints));
/*      */     } else {
/*  541 */       this.imp.setRoi(new PolygonRoi(points2, this.type));
/*  542 */       if (splineFit)
/*  543 */         ((PolygonRoi)this.imp.getRoi()).fitSpline(this.splinePoints);
/*      */     }
/*      */   }
/*      */ 
/*      */   void addHandle(int ox, int oy) {
/*  548 */     if ((this.imp == null) || (this.type == 8)) return;
/*  549 */     boolean splineFit = this.xSpline != null;
/*  550 */     this.xSpline = null;
/*  551 */     Polygon points = getPolygon();
/*  552 */     int n = points.npoints;
/*  553 */     this.modState = 0;
/*  554 */     if (previousRoi != null) previousRoi.modState = 0;
/*  555 */     int pointToDuplicate = getClosestPoint(ox, oy, points);
/*  556 */     Polygon points2 = new Polygon();
/*  557 */     for (int i2 = 0; i2 < n; i2++)
/*  558 */       if (i2 == pointToDuplicate) {
/*  559 */         int i1 = i2 - 1;
/*  560 */         if (i1 == -1) i1 = isLine() ? i2 : n - 1;
/*  561 */         int i3 = i2 + 1;
/*  562 */         if (i3 == n) i3 = isLine() ? i2 : 0;
/*  563 */         int x1 = points.xpoints[i1] + 2 * (points.xpoints[i2] - points.xpoints[i1]) / 3;
/*  564 */         int y1 = points.ypoints[i1] + 2 * (points.ypoints[i2] - points.ypoints[i1]) / 3;
/*  565 */         int x2 = points.xpoints[i2] + (points.xpoints[i3] - points.xpoints[i2]) / 3;
/*  566 */         int y2 = points.ypoints[i2] + (points.ypoints[i3] - points.ypoints[i2]) / 3;
/*  567 */         points2.addPoint(x1, y1);
/*  568 */         points2.addPoint(x2, y2);
/*      */       } else {
/*  570 */         points2.addPoint(points.xpoints[i2], points.ypoints[i2]);
/*      */       }
/*  572 */     if (this.type == 10) {
/*  573 */       this.imp.setRoi(new PointRoi(points2.xpoints, points2.ypoints, points2.npoints));
/*      */     } else {
/*  575 */       this.imp.setRoi(new PolygonRoi(points2, this.type));
/*  576 */       if (splineFit)
/*  577 */         ((PolygonRoi)this.imp.getRoi()).fitSpline(this.splinePoints);
/*      */     }
/*      */   }
/*      */ 
/*      */   int getClosestPoint(int x, int y, Polygon points) {
/*  582 */     int index = 0;
/*  583 */     double distance = 1.7976931348623157E+308D;
/*  584 */     for (int i = 0; i < points.npoints; i++) {
/*  585 */       double dx = points.xpoints[i] - x;
/*  586 */       double dy = points.ypoints[i] - y;
/*  587 */       double distance2 = dx * dx + dy * dy;
/*  588 */       if (distance2 < distance) {
/*  589 */         distance = distance2;
/*  590 */         index = i;
/*      */       }
/*      */     }
/*  593 */     return index;
/*      */   }
/*      */ 
/*      */   public void fitSpline(int evaluationPoints) {
/*  597 */     if ((this.xSpline == null) || (this.splinePoints != evaluationPoints)) {
/*  598 */       this.splinePoints = evaluationPoints;
/*  599 */       this.xSpline = new float[this.splinePoints];
/*  600 */       this.ySpline = new float[this.splinePoints];
/*      */     }
/*  602 */     int nNodes = this.nPoints;
/*  603 */     if (this.type == 2) {
/*  604 */       nNodes++;
/*  605 */       if (nNodes >= this.xp.length)
/*  606 */         enlargeArrays();
/*  607 */       this.xp[(nNodes - 1)] = this.xp[0];
/*  608 */       this.yp[(nNodes - 1)] = this.yp[0];
/*      */     }
/*  610 */     int[] xindex = new int[nNodes];
/*  611 */     for (int i = 0; i < nNodes; i++)
/*  612 */       xindex[i] = i;
/*  613 */     SplineFitter sfx = new SplineFitter(xindex, this.xp, nNodes);
/*  614 */     SplineFitter sfy = new SplineFitter(xindex, this.yp, nNodes);
/*      */ 
/*  617 */     double scale = (nNodes - 1) / (this.splinePoints - 1);
/*  618 */     float xs = 0.0F; float ys = 0.0F;
/*  619 */     float xmin = 3.4028235E+38F; float xmax = -xmin; float ymin = xmin; float ymax = xmax;
/*  620 */     for (int i = 0; i < this.splinePoints; i++) {
/*  621 */       double xvalue = i * scale;
/*  622 */       xs = (float)sfx.evalSpline(xindex, this.xp, nNodes, xvalue);
/*  623 */       if (xs < xmin) xmin = xs;
/*  624 */       if (xs > xmax) xmax = xs;
/*  625 */       this.xSpline[i] = xs;
/*  626 */       ys = (float)sfy.evalSpline(xindex, this.yp, nNodes, xvalue);
/*  627 */       if (ys < ymin) ymin = ys;
/*  628 */       if (ys > ymax) ymax = ys;
/*  629 */       this.ySpline[i] = ys;
/*      */     }
/*  631 */     int ixmin = (int)Math.floor(xmin + 0.5F);
/*  632 */     int ixmax = (int)Math.floor(xmax + 0.5F);
/*  633 */     int iymin = (int)Math.floor(ymin + 0.5F);
/*  634 */     int iymax = (int)Math.floor(ymax + 0.5F);
/*  635 */     if (ixmin != 0) {
/*  636 */       for (int i = 0; i < this.nPoints; i++)
/*  637 */         this.xp[i] -= ixmin;
/*  638 */       for (int i = 0; i < this.splinePoints; i++)
/*  639 */         this.xSpline[i] -= ixmin;
/*      */     }
/*  641 */     if (iymin != 0) {
/*  642 */       for (int i = 0; i < this.nPoints; i++)
/*  643 */         this.yp[i] -= iymin;
/*  644 */       for (int i = 0; i < this.splinePoints; i++)
/*  645 */         this.ySpline[i] -= iymin;
/*      */     }
/*  647 */     this.x += ixmin; this.y += iymin;
/*  648 */     this.width = (ixmax - ixmin); this.height = (iymax - iymin);
/*  649 */     this.cachedMask = null;
/*      */   }
/*      */ 
/*      */   public void fitSpline() {
/*  653 */     double length = getUncalibratedLength();
/*  654 */     int evaluationPoints = (int)(length / 2.0D);
/*  655 */     if (this.ic != null) {
/*  656 */       double mag = this.ic.getMagnification();
/*  657 */       if (mag < 1.0D)
/*  658 */         evaluationPoints = (int)(evaluationPoints * mag);
/*      */     }
/*  660 */     if (evaluationPoints < 100)
/*  661 */       evaluationPoints = 100;
/*  662 */     fitSpline(evaluationPoints);
/*      */   }
/*      */ 
/*      */   public void removeSplineFit() {
/*  666 */     this.xSpline = null;
/*  667 */     this.ySpline = null;
/*      */   }
/*      */ 
/*      */   public boolean isSplineFit()
/*      */   {
/*  672 */     return this.xSpline != null;
/*      */   }
/*      */ 
/*      */   public void fitSplineForStraightening()
/*      */   {
/*  678 */     fitSpline((int)getUncalibratedLength() * 2);
/*  679 */     if (this.splinePoints == 0) return;
/*  680 */     float[] xpoints = new float[this.splinePoints * 2];
/*  681 */     float[] ypoints = new float[this.splinePoints * 2];
/*  682 */     xpoints[0] = this.xSpline[0];
/*  683 */     ypoints[0] = this.ySpline[0];
/*  684 */     int n = 1;
/*  685 */     double inc = 0.01D;
/*  686 */     double distance = 0.0D; double distance2 = 0.0D; double dx = 0.0D; double dy = 0.0D;
/*  687 */     double x2 = this.xSpline[0]; double y2 = this.ySpline[0];
/*  688 */     for (int i = 1; i < this.splinePoints; i++) {
/*  689 */       double x1 = x2; double y1 = y2;
/*  690 */       double x = x1; double y = y1;
/*  691 */       x2 = this.xSpline[i]; y2 = this.ySpline[i];
/*  692 */       dx = x2 - x1;
/*  693 */       dy = y2 - y1;
/*  694 */       distance = Math.sqrt(dx * dx + dy * dy);
/*  695 */       double xinc = dx * inc / distance;
/*  696 */       double yinc = dy * inc / distance;
/*  697 */       double lastx = xpoints[(n - 1)]; double lasty = ypoints[(n - 1)];
/*      */ 
/*  699 */       int n2 = (int)(distance / inc);
/*  700 */       if (this.splinePoints == 2) n2++; do
/*      */       {
/*  702 */         dx = x - lastx;
/*  703 */         dy = y - lasty;
/*  704 */         distance2 = Math.sqrt(dx * dx + dy * dy);
/*      */ 
/*  706 */         if ((distance2 >= 1.0D - inc / 2.0D) && (n < xpoints.length - 1)) {
/*  707 */           xpoints[n] = ((float)x);
/*  708 */           ypoints[n] = ((float)y);
/*      */ 
/*  710 */           n++;
/*  711 */           lastx = x; lasty = y;
/*      */         }
/*  713 */         x += xinc;
/*  714 */         y += yinc;
/*  715 */         n2--; } while (n2 > 0);
/*      */     }
/*  717 */     this.xSpline = xpoints;
/*  718 */     this.ySpline = ypoints;
/*  719 */     this.splinePoints = n;
/*      */   }
/*      */ 
/*      */   public double getUncalibratedLength() {
/*  723 */     ImagePlus saveImp = this.imp;
/*  724 */     this.imp = null;
/*  725 */     double length = getLength();
/*  726 */     this.imp = saveImp;
/*  727 */     return length;
/*      */   }
/*      */ 
/*      */   protected void handleMouseUp(int sx, int sy) {
/*  731 */     if (this.state == 1) {
/*  732 */       this.state = 3; return;
/*  733 */     }if (this.state == 4) {
/*  734 */       this.cachedMask = null;
/*  735 */       this.state = 3;
/*  736 */       updateClipRect();
/*  737 */       this.oldX = this.x; this.oldY = this.y;
/*  738 */       this.oldWidth = this.width; this.oldHeight = this.height;
/*  739 */       return;
/*      */     }
/*  741 */     if (this.state != 0)
/*  742 */       return;
/*  743 */     if (IJ.spaceBarDown())
/*  744 */       return;
/*  745 */     boolean samePoint = (this.xp[(this.nPoints - 2)] == this.xp[(this.nPoints - 1)]) && (this.yp[(this.nPoints - 2)] == this.yp[(this.nPoints - 1)]);
/*  746 */     Rectangle biggerStartBox = new Rectangle(this.ic.screenX(this.startX) - 5, this.ic.screenY(this.startY) - 5, 10, 10);
/*  747 */     if ((this.nPoints > 2) && ((biggerStartBox.contains(sx, sy)) || ((this.ic.offScreenX(sx) == this.startX) && (this.ic.offScreenY(sy) == this.startY)) || ((samePoint) && (System.currentTimeMillis() - this.mouseUpTime <= 500L))))
/*      */     {
/*  750 */       this.nPoints -= 1;
/*  751 */       addOffset();
/*  752 */       finishPolygon();
/*  753 */       return;
/*  754 */     }if (!samePoint) {
/*  755 */       this.mouseUpTime = System.currentTimeMillis();
/*  756 */       if ((this.type == 8) && (this.nPoints == 3)) {
/*  757 */         addOffset();
/*  758 */         finishPolygon();
/*  759 */         return;
/*      */       }
/*      */ 
/*  762 */       this.xp[this.nPoints] = this.xp[(this.nPoints - 1)];
/*  763 */       this.yp[this.nPoints] = this.yp[(this.nPoints - 1)];
/*  764 */       this.nPoints += 1;
/*  765 */       if (this.nPoints == this.xp.length)
/*  766 */         enlargeArrays();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void addOffset()
/*      */   {
/*  772 */     if (this.xpf != null)
/*  773 */       for (int i = 0; i < this.nPoints; i++) {
/*  774 */         this.xpf[i] += this.x;
/*  775 */         this.ypf[i] += this.y;
/*      */       }
/*      */     else
/*  778 */       for (int i = 0; i < this.nPoints; i++) {
/*  779 */         this.xp[i] += this.x;
/*  780 */         this.yp[i] += this.y;
/*      */       }
/*      */   }
/*      */ 
/*      */   public boolean contains(int x, int y)
/*      */   {
/*  786 */     if (!super.contains(x, y))
/*  787 */       return false;
/*  788 */     if (this.xSpline != null) {
/*  789 */       FloatPolygon poly = new FloatPolygon(this.xSpline, this.ySpline, this.splinePoints);
/*  790 */       return poly.contains(x - this.x, y - this.y);
/*  791 */     }if (this.xpf != null) {
/*  792 */       FloatPolygon poly = new FloatPolygon(this.xpf, this.ypf, this.nPoints);
/*  793 */       return poly.contains(x - this.x, y - this.y);
/*      */     }
/*  795 */     Polygon poly = new Polygon(this.xp, this.yp, this.nPoints);
/*  796 */     return poly.contains(x - this.x, y - this.y);
/*      */   }
/*      */ 
/*      */   public int isHandle(int sx, int sy)
/*      */   {
/*  803 */     if (((this.xSpline == null) && (this.type != 2) && (this.type != 6) && (this.type != 8) && (this.type != 10)) || (this.clipboard != null))
/*  804 */       return -1;
/*  805 */     int size = 10;
/*  806 */     int halfSize = size / 2;
/*  807 */     int handle = -1;
/*      */ 
/*  809 */     for (int i = 0; i < this.nPoints; i++) {
/*  810 */       int sx2 = this.xp2[i] - halfSize; int sy2 = this.yp2[i] - halfSize;
/*  811 */       if ((sx >= sx2) && (sx <= sx2 + size) && (sy >= sy2) && (sy <= sy2 + size)) {
/*  812 */         handle = i;
/*  813 */         break;
/*      */       }
/*      */     }
/*  816 */     return handle;
/*      */   }
/*      */ 
/*      */   public ImageProcessor getMask()
/*      */   {
/*  830 */     if ((this.cachedMask != null) && (this.cachedMask.getPixels() != null))
/*  831 */       return this.cachedMask;
/*  832 */     PolygonFiller pf = new PolygonFiller();
/*  833 */     if (this.xSpline != null)
/*  834 */       pf.setPolygon(toInt(this.xSpline), toInt(this.ySpline), this.splinePoints);
/*  835 */     else if (this.xpf != null)
/*  836 */       pf.setPolygon(toInt(this.xpf), toInt(this.ypf), this.nPoints);
/*      */     else
/*  838 */       pf.setPolygon(this.xp, this.yp, this.nPoints);
/*  839 */     this.cachedMask = pf.getMask(this.width, this.height);
/*  840 */     return this.cachedMask;
/*      */   }
/*      */ 
/*      */   double getSmoothedLineLength()
/*      */   {
/*  846 */     double length = 0.0D;
/*  847 */     double w2 = 1.0D;
/*  848 */     double h2 = 1.0D;
/*      */ 
/*  850 */     if (this.imp != null) {
/*  851 */       Calibration cal = this.imp.getCalibration();
/*  852 */       w2 = cal.pixelWidth * cal.pixelWidth;
/*  853 */       h2 = cal.pixelHeight * cal.pixelHeight;
/*      */     }
/*  855 */     double dx = (this.xp[0] + this.xp[1] + this.xp[2]) / 3.0D - this.xp[0];
/*  856 */     double dy = (this.yp[0] + this.yp[1] + this.yp[2]) / 3.0D - this.yp[0];
/*  857 */     length += Math.sqrt(dx * dx * w2 + dy * dy * h2);
/*  858 */     for (int i = 1; i < this.nPoints - 2; i++) {
/*  859 */       dx = (this.xp[(i + 2)] - this.xp[(i - 1)]) / 3.0D;
/*  860 */       dy = (this.yp[(i + 2)] - this.yp[(i - 1)]) / 3.0D;
/*  861 */       length += Math.sqrt(dx * dx * w2 + dy * dy * h2);
/*      */     }
/*  863 */     dx = this.xp[(this.nPoints - 1)] - (this.xp[(this.nPoints - 3)] + this.xp[(this.nPoints - 2)] + this.xp[(this.nPoints - 1)]) / 3.0D;
/*  864 */     dy = this.yp[(this.nPoints - 1)] - (this.yp[(this.nPoints - 3)] + this.yp[(this.nPoints - 2)] + this.yp[(this.nPoints - 1)]) / 3.0D;
/*  865 */     length += Math.sqrt(dx * dx * w2 + dy * dy * h2);
/*  866 */     return length;
/*      */   }
/*      */ 
/*      */   double getSmoothedPerimeter()
/*      */   {
/*  872 */     double length = getSmoothedLineLength();
/*  873 */     double w2 = 1.0D; double h2 = 1.0D;
/*  874 */     if (this.imp != null) {
/*  875 */       Calibration cal = this.imp.getCalibration();
/*  876 */       w2 = cal.pixelWidth * cal.pixelWidth;
/*  877 */       h2 = cal.pixelHeight * cal.pixelHeight;
/*      */     }
/*  879 */     double dx = this.xp[(this.nPoints - 1)] - this.xp[0];
/*  880 */     double dy = this.yp[(this.nPoints - 1)] - this.yp[0];
/*  881 */     length += Math.sqrt(dx * dx * w2 + dy * dy * h2);
/*  882 */     return length;
/*      */   }
/*      */ 
/*      */   double getTracedPerimeter()
/*      */   {
/*  895 */     int sumdx = 0;
/*  896 */     int sumdy = 0;
/*  897 */     int nCorners = 0;
/*  898 */     int dx1 = this.xp[0] - this.xp[(this.nPoints - 1)];
/*  899 */     int dy1 = this.yp[0] - this.yp[(this.nPoints - 1)];
/*  900 */     int side1 = Math.abs(dx1) + Math.abs(dy1);
/*  901 */     boolean corner = false;
/*      */ 
/*  903 */     for (int i = 0; i < this.nPoints; i++) {
/*  904 */       int nexti = i + 1;
/*  905 */       if (nexti == this.nPoints)
/*  906 */         nexti = 0;
/*  907 */       int dx2 = this.xp[nexti] - this.xp[i];
/*  908 */       int dy2 = this.yp[nexti] - this.yp[i];
/*  909 */       sumdx += Math.abs(dx1);
/*  910 */       sumdy += Math.abs(dy1);
/*  911 */       int side2 = Math.abs(dx2) + Math.abs(dy2);
/*  912 */       if ((side1 > 1) || (!corner)) {
/*  913 */         corner = true;
/*  914 */         nCorners++;
/*      */       } else {
/*  916 */         corner = false;
/*  917 */       }dx1 = dx2;
/*  918 */       dy1 = dy2;
/*  919 */       side1 = side2;
/*      */     }
/*  921 */     double w = 1.0D; double h = 1.0D;
/*  922 */     if (this.imp != null) {
/*  923 */       Calibration cal = this.imp.getCalibration();
/*  924 */       w = cal.pixelWidth;
/*  925 */       h = cal.pixelHeight;
/*      */     }
/*  927 */     return sumdx * w + sumdy * h - nCorners * (w + h - Math.sqrt(w * w + h * h));
/*      */   }
/*      */ 
/*      */   public double getLength()
/*      */   {
/*  932 */     if (this.type == 4) {
/*  933 */       return getTracedPerimeter();
/*      */     }
/*  935 */     if (this.nPoints > 2) {
/*  936 */       if (this.type == 3)
/*  937 */         return getSmoothedPerimeter();
/*  938 */       if ((this.type == 7) && (this.width != 0) && (this.height != 0)) {
/*  939 */         return getSmoothedLineLength();
/*      */       }
/*      */     }
/*  942 */     double length = 0.0D;
/*      */ 
/*  944 */     double w2 = 1.0D; double h2 = 1.0D;
/*  945 */     if (this.imp != null) {
/*  946 */       Calibration cal = this.imp.getCalibration();
/*  947 */       w2 = cal.pixelWidth * cal.pixelWidth;
/*  948 */       h2 = cal.pixelHeight * cal.pixelHeight;
/*      */     }
/*  950 */     if (this.xSpline != null)
/*      */     {
/*  952 */       for (int i = 0; i < this.splinePoints - 1; i++) {
/*  953 */         double fdx = this.xSpline[(i + 1)] - this.xSpline[i];
/*  954 */         double fdy = this.ySpline[(i + 1)] - this.ySpline[i];
/*  955 */         length += Math.sqrt(fdx * fdx * w2 + fdy * fdy * h2);
/*      */       }
/*  957 */       if (this.type == 2) {
/*  958 */         double fdx = this.xSpline[0] - this.xSpline[(this.splinePoints - 1)];
/*  959 */         double fdy = this.ySpline[0] - this.ySpline[(this.splinePoints - 1)];
/*  960 */         length += Math.sqrt(fdx * fdx * w2 + fdy * fdy * h2);
/*      */       }
/*      */     } else {
/*  963 */       for (int i = 0; i < this.nPoints - 1; i++) {
/*  964 */         int dx = this.xp[(i + 1)] - this.xp[i];
/*  965 */         int dy = this.yp[(i + 1)] - this.yp[i];
/*  966 */         length += Math.sqrt(dx * dx * w2 + dy * dy * h2);
/*      */       }
/*  968 */       if (this.type == 2) {
/*  969 */         int dx = this.xp[0] - this.xp[(this.nPoints - 1)];
/*  970 */         int dy = this.yp[0] - this.yp[(this.nPoints - 1)];
/*  971 */         length += Math.sqrt(dx * dx * w2 + dy * dy * h2);
/*      */       }
/*      */     }
/*  974 */     return length;
/*      */   }
/*      */ 
/*      */   public double getAngle()
/*      */   {
/*  979 */     return this.degrees;
/*      */   }
/*      */ 
/*      */   public int getNCoordinates()
/*      */   {
/*  984 */     if (this.xSpline != null) {
/*  985 */       return this.splinePoints;
/*      */     }
/*  987 */     return this.nPoints;
/*      */   }
/*      */ 
/*      */   public int[] getXCoordinates()
/*      */   {
/*  992 */     if (this.xSpline != null)
/*  993 */       return toInt(this.xSpline);
/*  994 */     if (this.xpf != null) {
/*  995 */       return toInt(this.xpf);
/*      */     }
/*  997 */     return this.xp;
/*      */   }
/*      */ 
/*      */   public int[] getYCoordinates()
/*      */   {
/* 1002 */     if (this.xSpline != null)
/* 1003 */       return toInt(this.ySpline);
/* 1004 */     if (this.xpf != null) {
/* 1005 */       return toInt(this.ypf);
/*      */     }
/* 1007 */     return this.yp;
/*      */   }
/*      */ 
/*      */   public Polygon getNonSplineCoordinates() {
/* 1011 */     if (this.xpf != null) {
/* 1012 */       return new Polygon(toInt(this.xpf), toInt(this.ypf), this.nPoints);
/*      */     }
/* 1014 */     return new Polygon(this.xp, this.yp, this.nPoints);
/*      */   }
/*      */ 
/*      */   public Polygon getPolygon()
/*      */   {
/*      */     int[] ypoints1;
/*      */     int n;
/*      */     int[] xpoints1;
/*      */     int[] ypoints1;
/* 1025 */     if (this.xSpline != null) {
/* 1026 */       int n = this.splinePoints;
/* 1027 */       int[] xpoints1 = toInt(this.xSpline);
/* 1028 */       ypoints1 = toInt(this.ySpline);
/*      */     }
/*      */     else
/*      */     {
/*      */       int[] ypoints1;
/* 1029 */       if (this.xpf != null) {
/* 1030 */         int n = this.nPoints;
/* 1031 */         int[] xpoints1 = toInt(this.xpf);
/* 1032 */         ypoints1 = toInt(this.xpf);
/*      */       } else {
/* 1034 */         n = this.nPoints;
/* 1035 */         xpoints1 = this.xp;
/* 1036 */         ypoints1 = this.yp;
/*      */       }
/*      */     }
/* 1038 */     int[] xpoints2 = new int[n];
/* 1039 */     int[] ypoints2 = new int[n];
/* 1040 */     for (int i = 0; i < n; i++) {
/* 1041 */       xpoints1[i] += this.x;
/* 1042 */       ypoints1[i] += this.y;
/*      */     }
/* 1044 */     return new Polygon(xpoints2, ypoints2, n);
/*      */   }
/*      */ 
/*      */   public FloatPolygon getFloatPolygon()
/*      */   {
/* 1049 */     int n = this.xSpline != null ? this.splinePoints : this.nPoints;
/* 1050 */     float[] xpoints2 = new float[n];
/* 1051 */     float[] ypoints2 = new float[n];
/* 1052 */     if (this.xSpline != null)
/* 1053 */       for (int i = 0; i < n; i++) {
/* 1054 */         xpoints2[i] = (this.xSpline[i] + this.x);
/* 1055 */         ypoints2[i] = (this.ySpline[i] + this.y);
/*      */       }
/* 1057 */     else if (this.xpf != null)
/* 1058 */       for (int i = 0; i < n; i++) {
/* 1059 */         xpoints2[i] = (this.xpf[i] + this.x);
/* 1060 */         ypoints2[i] = (this.ypf[i] + this.y);
/*      */       }
/*      */     else {
/* 1063 */       for (int i = 0; i < n; i++) {
/* 1064 */         xpoints2[i] = (this.xp[i] + this.x);
/* 1065 */         ypoints2[i] = (this.yp[i] + this.y);
/*      */       }
/*      */     }
/* 1068 */     return new FloatPolygon(xpoints2, ypoints2, n);
/*      */   }
/*      */ 
/*      */   public Polygon getConvexHull()
/*      */   {
/* 1074 */     int n = getNCoordinates();
/* 1075 */     int[] xCoordinates = getXCoordinates();
/* 1076 */     int[] yCoordinates = getYCoordinates();
/* 1077 */     Rectangle r = getBounds();
/* 1078 */     int xbase = r.x;
/* 1079 */     int ybase = r.y;
/* 1080 */     int[] xx = new int[n];
/* 1081 */     int[] yy = new int[n];
/* 1082 */     int n2 = 0;
/* 1083 */     int smallestY = 2147483647;
/*      */ 
/* 1085 */     for (int i = 0; i < n; i++) {
/* 1086 */       int y = yCoordinates[i];
/* 1087 */       if (y < smallestY)
/* 1088 */         smallestY = y;
/*      */     }
/* 1090 */     int smallestX = 2147483647;
/* 1091 */     int p1 = 0;
/* 1092 */     for (int i = 0; i < n; i++) {
/* 1093 */       int x = xCoordinates[i];
/* 1094 */       int y = yCoordinates[i];
/* 1095 */       if ((y == smallestY) && (x < smallestX)) {
/* 1096 */         smallestX = x;
/* 1097 */         p1 = i;
/*      */       }
/*      */     }
/* 1100 */     int pstart = p1;
/*      */ 
/* 1103 */     int count = 0;
/*      */     do {
/* 1105 */       int x1 = xCoordinates[p1];
/* 1106 */       int y1 = yCoordinates[p1];
/* 1107 */       int p2 = p1 + 1; if (p2 == n) p2 = 0;
/* 1108 */       int x2 = xCoordinates[p2];
/* 1109 */       int y2 = yCoordinates[p2];
/* 1110 */       int p3 = p2 + 1; if (p3 == n) p3 = 0; do
/*      */       {
/* 1112 */         int x3 = xCoordinates[p3];
/* 1113 */         int y3 = yCoordinates[p3];
/* 1114 */         int determinate = x1 * (y2 - y3) - y1 * (x2 - x3) + (y3 * x2 - y2 * x3);
/* 1115 */         if (determinate > 0) {
/* 1116 */           x2 = x3; y2 = y3; p2 = p3;
/* 1117 */         }p3++;
/* 1118 */         if (p3 == n) p3 = 0; 
/*      */       }
/* 1119 */       while (p3 != p1);
/* 1120 */       if (n2 < n) {
/* 1121 */         xx[n2] = (xbase + x1);
/* 1122 */         yy[n2] = (ybase + y1);
/* 1123 */         n2++;
/*      */       } else {
/* 1125 */         count++;
/* 1126 */         if (count > 10) return null;
/*      */       }
/* 1128 */       p1 = p2;
/* 1129 */     }while (p1 != pstart);
/* 1130 */     return new Polygon(xx, yy, n2);
/*      */   }
/*      */ 
/*      */   protected int clipRectMargin() {
/* 1134 */     return this.type == 10 ? 4 : 0;
/*      */   }
/*      */ 
/*      */   public synchronized Object clone()
/*      */   {
/* 1139 */     PolygonRoi r = (PolygonRoi)super.clone();
/* 1140 */     if (this.xpf != null) {
/* 1141 */       r.xpf = new float[this.maxPoints];
/* 1142 */       r.ypf = new float[this.maxPoints];
/*      */     } else {
/* 1144 */       r.xp = new int[this.maxPoints];
/* 1145 */       r.yp = new int[this.maxPoints];
/*      */     }
/* 1147 */     r.xp2 = new int[this.maxPoints];
/* 1148 */     r.yp2 = new int[this.maxPoints];
/* 1149 */     for (int i = 0; i < this.nPoints; i++) {
/* 1150 */       if (this.xpf != null) {
/* 1151 */         r.xpf[i] = this.xpf[i];
/* 1152 */         r.ypf[i] = this.ypf[i];
/*      */       } else {
/* 1154 */         r.xp[i] = this.xp[i];
/* 1155 */         r.yp[i] = this.yp[i];
/*      */       }
/* 1157 */       r.xp2[i] = this.xp2[i];
/* 1158 */       r.yp2[i] = this.yp2[i];
/*      */     }
/* 1160 */     if (this.xSpline != null) {
/* 1161 */       r.xSpline = new float[this.splinePoints];
/* 1162 */       r.ySpline = new float[this.splinePoints];
/* 1163 */       r.splinePoints = this.splinePoints;
/* 1164 */       for (int i = 0; i < this.splinePoints; i++) {
/* 1165 */         r.xSpline[i] = this.xSpline[i];
/* 1166 */         r.ySpline[i] = this.ySpline[i];
/*      */       }
/*      */     }
/* 1169 */     return r;
/*      */   }
/*      */ 
/*      */   void enlargeArrays() {
/* 1173 */     int[] xptemp = new int[this.maxPoints * 2];
/* 1174 */     int[] yptemp = new int[this.maxPoints * 2];
/* 1175 */     int[] xp2temp = new int[this.maxPoints * 2];
/* 1176 */     int[] yp2temp = new int[this.maxPoints * 2];
/* 1177 */     System.arraycopy(this.xp, 0, xptemp, 0, this.maxPoints);
/* 1178 */     System.arraycopy(this.yp, 0, yptemp, 0, this.maxPoints);
/* 1179 */     System.arraycopy(this.xp2, 0, xp2temp, 0, this.maxPoints);
/* 1180 */     System.arraycopy(this.yp2, 0, yp2temp, 0, this.maxPoints);
/* 1181 */     this.xp = xptemp; this.yp = yptemp;
/* 1182 */     this.xp2 = xp2temp; this.yp2 = yp2temp;
/* 1183 */     if (IJ.debugMode) IJ.log("PolygonRoi: " + this.maxPoints + " points");
/* 1184 */     this.maxPoints *= 2;
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.PolygonRoi
 * JD-Core Version:    0.6.2
 */