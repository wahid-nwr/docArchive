/*      */ package ij.gui;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.Prefs;
/*      */ import ij.Undo;
/*      */ import ij.WindowManager;
/*      */ import ij.macro.Interpreter;
/*      */ import ij.measure.Calibration;
/*      */ import ij.plugin.frame.Recorder;
/*      */ import ij.process.FloatPolygon;
/*      */ import ij.process.ImageProcessor;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Polygon;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.Shape;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.RoundRectangle2D;
/*      */ import java.awt.geom.RoundRectangle2D.Float;
/*      */ import java.io.Serializable;
/*      */ 
/*      */ public class Roi
/*      */   implements Cloneable, Serializable
/*      */ {
/*      */   public static final int CONSTRUCTING = 0;
/*      */   public static final int MOVING = 1;
/*      */   public static final int RESIZING = 2;
/*      */   public static final int NORMAL = 3;
/*      */   public static final int MOVING_HANDLE = 4;
/*      */   public static final int RECTANGLE = 0;
/*      */   public static final int OVAL = 1;
/*      */   public static final int POLYGON = 2;
/*      */   public static final int FREEROI = 3;
/*      */   public static final int TRACED_ROI = 4;
/*      */   public static final int LINE = 5;
/*      */   public static final int POLYLINE = 6;
/*      */   public static final int FREELINE = 7;
/*      */   public static final int ANGLE = 8;
/*      */   public static final int COMPOSITE = 9;
/*      */   public static final int POINT = 10;
/*      */   public static final int HANDLE_SIZE = 5;
/*      */   public static final int NOT_PASTING = -1;
/*      */   static final int NO_MODS = 0;
/*      */   static final int ADD_TO_ROI = 1;
/*      */   static final int SUBTRACT_FROM_ROI = 2;
/*      */   int startX;
/*      */   int startY;
/*      */   int x;
/*      */   int y;
/*      */   int width;
/*      */   int height;
/*      */   int activeHandle;
/*      */   int state;
/*   29 */   int modState = 0;
/*      */   int cornerDiameter;
/*      */   public static Roi previousRoi;
/*   33 */   public static final BasicStroke onePixelWide = new BasicStroke(1.0F);
/*   34 */   protected static Color ROIColor = Prefs.getColor("roicolor", Color.yellow);
/*   35 */   protected static int pasteMode = 0;
/*   36 */   protected static int lineWidth = 1;
/*      */   protected static Color defaultFillColor;
/*      */   protected int type;
/*      */   protected int xMax;
/*      */   protected int yMax;
/*      */   protected ImagePlus imp;
/*      */   private int imageID;
/*      */   protected ImageCanvas ic;
/*      */   protected int oldX;
/*      */   protected int oldY;
/*      */   protected int oldWidth;
/*      */   protected int oldHeight;
/*      */   protected int clipX;
/*      */   protected int clipY;
/*      */   protected int clipWidth;
/*      */   protected int clipHeight;
/*      */   protected ImagePlus clipboard;
/*      */   protected boolean constrain;
/*      */   protected boolean center;
/*      */   protected boolean aspect;
/*      */   protected boolean updateFullWindow;
/*   51 */   protected double mag = 1.0D;
/*      */   protected double asp_bk;
/*      */   protected ImageProcessor cachedMask;
/*   54 */   protected Color handleColor = Color.white;
/*      */   protected Color strokeColor;
/*      */   protected Color instanceColor;
/*      */   protected Color fillColor;
/*      */   protected BasicStroke stroke;
/*      */   protected boolean nonScalable;
/*      */   protected boolean overlay;
/*      */   protected boolean wideLine;
/*      */   private String name;
/*      */   private int position;
/*      */   private int channel;
/*      */   private int slice;
/*      */   private int frame;
/*      */   private Overlay prototypeOverlay;
/*      */ 
/*      */   public Roi(int x, int y, int width, int height)
/*      */   {
/*   69 */     this(x, y, width, height, 0);
/*      */   }
/*      */ 
/*      */   public Roi(int x, int y, int width, int height, int cornerDiameter)
/*      */   {
/*   74 */     setImage(null);
/*   75 */     if (width < 1) width = 1;
/*   76 */     if (height < 1) height = 1;
/*   77 */     if (width > this.xMax) width = this.xMax;
/*   78 */     if (height > this.yMax) height = this.yMax;
/*   79 */     this.cornerDiameter = cornerDiameter;
/*      */ 
/*   81 */     this.x = x;
/*   82 */     this.y = y;
/*   83 */     this.startX = x; this.startY = y;
/*   84 */     this.oldX = x; this.oldY = y; this.oldWidth = 0; this.oldHeight = 0;
/*   85 */     this.width = width;
/*   86 */     this.height = height;
/*   87 */     this.oldWidth = width;
/*   88 */     this.oldHeight = height;
/*   89 */     this.clipX = x;
/*   90 */     this.clipY = y;
/*   91 */     this.clipWidth = width;
/*   92 */     this.clipHeight = height;
/*   93 */     this.state = 3;
/*   94 */     this.type = 0;
/*   95 */     if (this.ic != null) {
/*   96 */       Graphics g = this.ic.getGraphics();
/*   97 */       draw(g);
/*   98 */       g.dispose();
/*      */     }
/*  100 */     this.fillColor = defaultFillColor;
/*      */   }
/*      */ 
/*      */   public Roi(Rectangle r)
/*      */   {
/*  105 */     this(r.x, r.y, r.width, r.height);
/*      */   }
/*      */ 
/*      */   public Roi(int sx, int sy, ImagePlus imp)
/*      */   {
/*  111 */     this(sx, sy, imp, 0);
/*      */   }
/*      */ 
/*      */   public Roi(int sx, int sy, ImagePlus imp, int cornerDiameter)
/*      */   {
/*  117 */     setImage(imp);
/*  118 */     int ox = sx; int oy = sy;
/*  119 */     if (this.ic != null) {
/*  120 */       ox = this.ic.offScreenX(sx);
/*  121 */       oy = this.ic.offScreenY(sy);
/*      */     }
/*  123 */     setLocation(ox, oy);
/*  124 */     this.cornerDiameter = cornerDiameter;
/*  125 */     this.width = 0;
/*  126 */     this.height = 0;
/*  127 */     this.state = 0;
/*  128 */     this.type = 0;
/*  129 */     if (isDrawingTool()) {
/*  130 */       setStrokeColor(Toolbar.getForegroundColor());
/*  131 */       if (!(this instanceof TextRoi)) {
/*  132 */         double mag = (imp != null) && (imp.getCanvas() != null) ? imp.getCanvas().getMagnification() : 1.0D;
/*  133 */         if (mag > 1.0D) mag = 1.0D;
/*  134 */         if ((Line.getWidth() == 1) && (!Line.widthChanged))
/*  135 */           Line.setWidth((int)(2.0D / mag));
/*  136 */         if ((mag < 1.0D) && (Line.getWidth() * mag < 1.0D))
/*  137 */           Line.setWidth((int)(1.0D / mag));
/*  138 */         setStrokeWidth(Line.getWidth());
/*      */       }
/*      */     }
/*  141 */     this.fillColor = defaultFillColor;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Roi(int x, int y, int width, int height, ImagePlus imp) {
/*  146 */     this(x, y, width, height);
/*  147 */     setImage(imp);
/*      */   }
/*      */ 
/*      */   public void setLocation(int x, int y)
/*      */   {
/*  152 */     this.x = x;
/*  153 */     this.y = y;
/*  154 */     this.startX = x; this.startY = y;
/*  155 */     this.oldX = x; this.oldY = y; this.oldWidth = 0; this.oldHeight = 0;
/*      */   }
/*      */ 
/*      */   public void setImage(ImagePlus imp) {
/*  159 */     this.imp = imp;
/*  160 */     this.cachedMask = null;
/*  161 */     if (imp == null) {
/*  162 */       this.ic = null;
/*  163 */       this.clipboard = null;
/*  164 */       this.xMax = 99999;
/*  165 */       this.yMax = 99999;
/*      */     } else {
/*  167 */       this.ic = imp.getCanvas();
/*  168 */       this.xMax = imp.getWidth();
/*  169 */       this.yMax = imp.getHeight();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ImagePlus getImage()
/*      */   {
/*  175 */     return this.imp;
/*      */   }
/*      */ 
/*      */   public int getImageID()
/*      */   {
/*  180 */     return this.imp != null ? this.imp.getID() : this.imageID;
/*      */   }
/*      */ 
/*      */   public int getType() {
/*  184 */     return this.type;
/*      */   }
/*      */ 
/*      */   public int getState() {
/*  188 */     return this.state;
/*      */   }
/*      */ 
/*      */   public double getLength()
/*      */   {
/*  193 */     double pw = 1.0D; double ph = 1.0D;
/*  194 */     if (this.imp != null) {
/*  195 */       Calibration cal = this.imp.getCalibration();
/*  196 */       pw = cal.pixelWidth;
/*  197 */       ph = cal.pixelHeight;
/*      */     }
/*  199 */     return 2.0D * this.width * pw + 2.0D * this.height * ph;
/*      */   }
/*      */ 
/*      */   public double getFeretsDiameter()
/*      */   {
/*  205 */     double[] a = getFeretValues();
/*  206 */     return a != null ? a[0] : 0.0D;
/*      */   }
/*      */ 
/*      */   public double[] getFeretValues()
/*      */   {
/*  212 */     double min = 1.7976931348623157E+308D; double diameter = 0.0D; double angle = 0.0D; double feretX = 0.0D; double feretY = 0.0D;
/*  213 */     int p1 = 0; int p2 = 0;
/*  214 */     double pw = 1.0D; double ph = 1.0D;
/*  215 */     if (this.imp != null) {
/*  216 */       Calibration cal = this.imp.getCalibration();
/*  217 */       pw = cal.pixelWidth;
/*  218 */       ph = cal.pixelHeight;
/*      */     }
/*  220 */     Polygon poly = getConvexHull();
/*  221 */     if (poly == null) {
/*  222 */       poly = getPolygon();
/*  223 */       if (poly == null) return null;
/*      */     }
/*  225 */     double w2 = pw * pw; double h2 = ph * ph;
/*      */ 
/*  227 */     for (int i = 0; i < poly.npoints; i++)
/*  228 */       for (int j = i; j < poly.npoints; j++) {
/*  229 */         double dx = poly.xpoints[i] - poly.xpoints[j];
/*  230 */         double dy = poly.ypoints[i] - poly.ypoints[j];
/*  231 */         double d = Math.sqrt(dx * dx * w2 + dy * dy * h2);
/*  232 */         if (d > diameter) { diameter = d; p1 = i; p2 = j;
/*      */         }
/*      */       }
/*  235 */     Rectangle r = getBounds();
/*  236 */     double cx = r.x + r.width / 2.0D;
/*  237 */     double cy = r.y + r.height / 2.0D;
/*  238 */     int n = poly.npoints;
/*  239 */     double[] x = new double[n];
/*  240 */     double[] y = new double[n];
/*  241 */     for (int i = 0; i < n; i++) {
/*  242 */       x[i] = ((poly.xpoints[i] - cx) * pw);
/*  243 */       y[i] = ((poly.ypoints[i] - cy) * ph);
/*      */     }
/*      */ 
/*  246 */     for (double a = 0.0D; a <= 90.0D; a += 0.5D) {
/*  247 */       double cos = Math.cos(a * 3.141592653589793D / 180.0D);
/*  248 */       double sin = Math.sin(a * 3.141592653589793D / 180.0D);
/*  249 */       double xmin = 1.7976931348623157E+308D; double ymin = 1.7976931348623157E+308D;
/*  250 */       double xmax = -1.797693134862316E+308D; double ymax = -1.797693134862316E+308D;
/*  251 */       for (int i = 0; i < n; i++) {
/*  252 */         double xr = cos * x[i] - sin * y[i];
/*  253 */         double yr = sin * x[i] + cos * y[i];
/*  254 */         if (xr < xmin) xmin = xr;
/*  255 */         if (xr > xmax) xmax = xr;
/*  256 */         if (yr < ymin) ymin = yr;
/*  257 */         if (yr > ymax) ymax = yr;
/*      */       }
/*  259 */       double width = xmax - xmin;
/*  260 */       double height = ymax - ymin;
/*  261 */       double min2 = Math.min(width, height);
/*  262 */       min = Math.min(min, min2);
/*      */     }
/*  264 */     double x1 = poly.xpoints[p1]; double y1 = poly.ypoints[p1];
/*  265 */     double x2 = poly.xpoints[p2]; double y2 = poly.ypoints[p2];
/*  266 */     if (x1 > x2) {
/*  267 */       double tx1 = x1; double ty1 = y1;
/*  268 */       x1 = x2; y1 = y2; x2 = tx1; y2 = ty1;
/*      */     }
/*  270 */     feretX = x1 * pw;
/*  271 */     feretY = y1 * ph;
/*  272 */     double dx = x2 - x1; double dy = y1 - y2;
/*  273 */     angle = 57.295779513082323D * Math.atan2(dy * ph, dx * pw);
/*  274 */     if (angle < 0.0D) angle = 180.0D + angle;
/*      */ 
/*  276 */     double[] a = new double[5];
/*  277 */     a[0] = diameter;
/*  278 */     a[1] = angle;
/*  279 */     a[2] = min;
/*  280 */     a[3] = feretX;
/*  281 */     a[4] = feretY;
/*  282 */     return a;
/*      */   }
/*      */ 
/*      */   public Polygon getConvexHull() {
/*  286 */     return getPolygon();
/*      */   }
/*      */ 
/*      */   double getFeretBreadth(Shape shape, double angle, double x1, double y1, double x2, double y2) {
/*  290 */     double cx = x1 + (x2 - x1) / 2.0D;
/*  291 */     double cy = y1 + (y2 - y1) / 2.0D;
/*  292 */     AffineTransform at = new AffineTransform();
/*  293 */     at.rotate(angle * 3.141592653589793D / 180.0D, cx, cy);
/*  294 */     Shape s = at.createTransformedShape(shape);
/*  295 */     Rectangle2D r = s.getBounds2D();
/*  296 */     return Math.min(r.getWidth(), r.getHeight());
/*      */   }
/*      */ 
/*      */   public Rectangle getBounds()
/*      */   {
/*  312 */     return new Rectangle(this.x, this.y, this.width, this.height);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Rectangle getBoundingRect()
/*      */   {
/*  320 */     return getBounds();
/*      */   }
/*      */ 
/*      */   public Polygon getPolygon()
/*      */   {
/*  330 */     int[] xpoints = new int[4];
/*  331 */     int[] ypoints = new int[4];
/*  332 */     xpoints[0] = this.x;
/*  333 */     ypoints[0] = this.y;
/*  334 */     xpoints[1] = (this.x + this.width);
/*  335 */     ypoints[1] = this.y;
/*  336 */     xpoints[2] = (this.x + this.width);
/*  337 */     ypoints[2] = (this.y + this.height);
/*  338 */     xpoints[3] = this.x;
/*  339 */     ypoints[3] = (this.y + this.height);
/*  340 */     return new Polygon(xpoints, ypoints, 4);
/*      */   }
/*      */ 
/*      */   public FloatPolygon getFloatPolygon() {
/*  344 */     Polygon p = getPolygon();
/*  345 */     if (p != null) {
/*  346 */       return new FloatPolygon(toFloat(p.xpoints), toFloat(p.ypoints), p.npoints);
/*      */     }
/*  348 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized Object clone()
/*      */   {
/*      */     try
/*      */     {
/*  355 */       Roi r = (Roi)super.clone();
/*  356 */       r.setImage(null);
/*  357 */       r.setStroke(getStroke());
/*  358 */       r.setFillColor(getFillColor());
/*  359 */       r.imageID = getImageID();
/*  360 */       return r; } catch (CloneNotSupportedException e) {
/*      */     }
/*  362 */     return null;
/*      */   }
/*      */ 
/*      */   protected void grow(int sx, int sy) {
/*  366 */     if (this.clipboard != null) return;
/*  367 */     int xNew = this.ic.offScreenX(sx);
/*  368 */     int yNew = this.ic.offScreenY(sy);
/*  369 */     if (this.type == 0) {
/*  370 */       if (xNew < 0) xNew = 0;
/*  371 */       if (yNew < 0) yNew = 0;
/*      */     }
/*  373 */     if (this.constrain)
/*      */     {
/*  375 */       if (!this.center) {
/*  376 */         growConstrained(xNew, yNew); return;
/*      */       }
/*  378 */       int dx = xNew - this.x;
/*  379 */       int dy = yNew - this.y;
/*      */       int d;
/*      */       int d;
/*  380 */       if (dx < dy)
/*  381 */         d = dx;
/*      */       else
/*  383 */         d = dy;
/*  384 */       xNew = this.x + d;
/*  385 */       yNew = this.y + d;
/*      */     }
/*  387 */     if (this.center) {
/*  388 */       this.width = (Math.abs(xNew - this.startX) * 2);
/*  389 */       this.height = (Math.abs(yNew - this.startY) * 2);
/*  390 */       this.x = (this.startX - this.width / 2);
/*  391 */       this.y = (this.startY - this.height / 2);
/*      */     } else {
/*  393 */       this.width = Math.abs(xNew - this.startX);
/*  394 */       this.height = Math.abs(yNew - this.startY);
/*  395 */       this.x = (xNew >= this.startX ? this.startX : this.startX - this.width);
/*  396 */       this.y = (yNew >= this.startY ? this.startY : this.startY - this.height);
/*  397 */       if (this.type == 0) {
/*  398 */         if (this.x + this.width > this.xMax) this.width = (this.xMax - this.x);
/*  399 */         if (this.y + this.height > this.yMax) this.height = (this.yMax - this.y);
/*      */       }
/*      */     }
/*  402 */     updateClipRect();
/*  403 */     this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/*  404 */     this.oldX = this.x;
/*  405 */     this.oldY = this.y;
/*  406 */     this.oldWidth = this.width;
/*  407 */     this.oldHeight = this.height;
/*      */   }
/*      */ 
/*      */   private void growConstrained(int xNew, int yNew) {
/*  411 */     int dx = xNew - this.startX;
/*  412 */     int dy = yNew - this.startY;
/*  413 */     this.width = (this.height = (int)Math.round(Math.sqrt(dx * dx + dy * dy)));
/*  414 */     if (this.type == 0) {
/*  415 */       this.x = (xNew >= this.startX ? this.startX : this.startX - this.width);
/*  416 */       this.y = (yNew >= this.startY ? this.startY : this.startY - this.height);
/*  417 */       if (this.x < 0) this.x = 0;
/*  418 */       if (this.y < 0) this.y = 0;
/*  419 */       if (this.x + this.width > this.xMax) this.width = (this.xMax - this.x);
/*  420 */       if (this.y + this.height > this.yMax) this.height = (this.yMax - this.y); 
/*      */     }
/*  422 */     else { this.x = (this.startX + dx / 2 - this.width / 2);
/*  423 */       this.y = (this.startY + dy / 2 - this.height / 2);
/*      */     }
/*  425 */     updateClipRect();
/*  426 */     this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/*  427 */     this.oldX = this.x;
/*  428 */     this.oldY = this.y;
/*  429 */     this.oldWidth = this.width;
/*  430 */     this.oldHeight = this.height;
/*      */   }
/*      */ 
/*      */   protected void moveHandle(int sx, int sy)
/*      */   {
/*  435 */     if (this.clipboard != null) return;
/*  436 */     int ox = this.ic.offScreenX(sx);
/*  437 */     int oy = this.ic.offScreenY(sy);
/*  438 */     if (ox < 0) ox = 0; if (oy < 0) oy = 0;
/*  439 */     if (ox > this.xMax) ox = this.xMax; if (oy > this.yMax) oy = this.yMax;
/*      */ 
/*  441 */     int x1 = this.x; int y1 = this.y; int x2 = x1 + this.width; int y2 = this.y + this.height; int xc = this.x + this.width / 2; int yc = this.y + this.height / 2;
/*      */     double asp;
/*  442 */     if ((this.width > 7) && (this.height > 7)) {
/*  443 */       double asp = this.width / this.height;
/*  444 */       this.asp_bk = asp;
/*      */     } else {
/*  446 */       asp = this.asp_bk;
/*      */     }
/*      */ 
/*  449 */     switch (this.activeHandle) {
/*      */     case 0:
/*  451 */       this.x = ox; this.y = oy;
/*  452 */       break;
/*      */     case 1:
/*  454 */       this.y = oy;
/*  455 */       break;
/*      */     case 2:
/*  457 */       x2 = ox; this.y = oy;
/*  458 */       break;
/*      */     case 3:
/*  460 */       x2 = ox;
/*  461 */       break;
/*      */     case 4:
/*  463 */       x2 = ox; y2 = oy;
/*  464 */       break;
/*      */     case 5:
/*  466 */       y2 = oy;
/*  467 */       break;
/*      */     case 6:
/*  469 */       this.x = ox; y2 = oy;
/*  470 */       break;
/*      */     case 7:
/*  472 */       this.x = ox;
/*      */     }
/*      */ 
/*  475 */     if (this.x < x2) {
/*  476 */       this.width = (x2 - this.x);
/*      */     } else {
/*  478 */       this.width = 1; this.x = x2;
/*  479 */     }if (this.y < y2) {
/*  480 */       this.height = (y2 - this.y);
/*      */     } else {
/*  482 */       this.height = 1; this.y = y2;
/*      */     }
/*  484 */     if (this.center) {
/*  485 */       switch (this.activeHandle) {
/*      */       case 0:
/*  487 */         this.width = ((xc - this.x) * 2);
/*  488 */         this.height = ((yc - this.y) * 2);
/*  489 */         break;
/*      */       case 1:
/*  491 */         this.height = ((yc - this.y) * 2);
/*  492 */         break;
/*      */       case 2:
/*  494 */         this.width = ((x2 - xc) * 2);
/*  495 */         this.x = (x2 - this.width);
/*  496 */         this.height = ((yc - this.y) * 2);
/*  497 */         break;
/*      */       case 3:
/*  499 */         this.width = ((x2 - xc) * 2);
/*  500 */         this.x = (x2 - this.width);
/*  501 */         break;
/*      */       case 4:
/*  503 */         this.width = ((x2 - xc) * 2);
/*  504 */         this.x = (x2 - this.width);
/*  505 */         this.height = ((y2 - yc) * 2);
/*  506 */         this.y = (y2 - this.height);
/*  507 */         break;
/*      */       case 5:
/*  509 */         this.height = ((y2 - yc) * 2);
/*  510 */         this.y = (y2 - this.height);
/*  511 */         break;
/*      */       case 6:
/*  513 */         this.width = ((xc - this.x) * 2);
/*  514 */         this.height = ((y2 - yc) * 2);
/*  515 */         this.y = (y2 - this.height);
/*  516 */         break;
/*      */       case 7:
/*  518 */         this.width = ((xc - this.x) * 2);
/*      */       }
/*      */ 
/*  521 */       if (this.x >= x2) {
/*  522 */         this.width = 1;
/*  523 */         this.x = (x2 = xc);
/*      */       }
/*  525 */       if (this.y >= y2) {
/*  526 */         this.height = 1;
/*  527 */         this.y = (y2 = yc);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  532 */     if (this.constrain) {
/*  533 */       if ((this.activeHandle == 1) || (this.activeHandle == 5))
/*  534 */         this.width = this.height;
/*      */       else {
/*  536 */         this.height = this.width;
/*      */       }
/*  538 */       if (this.x >= x2) {
/*  539 */         this.width = 1;
/*  540 */         this.x = (x2 = xc);
/*      */       }
/*  542 */       if (this.y >= y2) {
/*  543 */         this.height = 1;
/*  544 */         this.y = (y2 = yc);
/*      */       }
/*  546 */       switch (this.activeHandle) {
/*      */       case 0:
/*  548 */         this.x = (x2 - this.width);
/*  549 */         this.y = (y2 - this.height);
/*  550 */         break;
/*      */       case 1:
/*  552 */         this.x = (xc - this.width / 2);
/*  553 */         this.y = (y2 - this.height);
/*  554 */         break;
/*      */       case 2:
/*  556 */         this.y = (y2 - this.height);
/*  557 */         break;
/*      */       case 3:
/*  559 */         this.y = (yc - this.height / 2);
/*  560 */         break;
/*      */       case 5:
/*  562 */         this.x = (xc - this.width / 2);
/*  563 */         break;
/*      */       case 6:
/*  565 */         this.x = (x2 - this.width);
/*  566 */         break;
/*      */       case 7:
/*  568 */         this.y = (yc - this.height / 2);
/*  569 */         this.x = (x2 - this.width);
/*      */       case 4:
/*      */       }
/*  572 */       if (this.center) {
/*  573 */         this.x = (xc - this.width / 2);
/*  574 */         this.y = (yc - this.height / 2);
/*      */       }
/*      */     }
/*      */ 
/*  578 */     if ((this.aspect) && (!this.constrain)) {
/*  579 */       if ((this.activeHandle == 1) || (this.activeHandle == 5)) this.width = ((int)Math.rint(this.height * asp)); else {
/*  580 */         this.height = ((int)Math.rint(this.width / asp));
/*      */       }
/*  582 */       switch (this.activeHandle) {
/*      */       case 0:
/*  584 */         this.x = (x2 - this.width);
/*  585 */         this.y = (y2 - this.height);
/*  586 */         break;
/*      */       case 1:
/*  588 */         this.x = (xc - this.width / 2);
/*  589 */         this.y = (y2 - this.height);
/*  590 */         break;
/*      */       case 2:
/*  592 */         this.y = (y2 - this.height);
/*  593 */         break;
/*      */       case 3:
/*  595 */         this.y = (yc - this.height / 2);
/*  596 */         break;
/*      */       case 5:
/*  598 */         this.x = (xc - this.width / 2);
/*  599 */         break;
/*      */       case 6:
/*  601 */         this.x = (x2 - this.width);
/*  602 */         break;
/*      */       case 7:
/*  604 */         this.y = (yc - this.height / 2);
/*  605 */         this.x = (x2 - this.width);
/*      */       case 4:
/*      */       }
/*  608 */       if (this.center) {
/*  609 */         this.x = (xc - this.width / 2);
/*  610 */         this.y = (yc - this.height / 2);
/*      */       }
/*      */ 
/*  614 */       if (this.width < 8) {
/*  615 */         if (this.width < 1) this.width = 1;
/*  616 */         this.height = ((int)Math.rint(this.width / this.asp_bk));
/*      */       }
/*  618 */       if (this.height < 8) {
/*  619 */         if (this.height < 1) this.height = 1;
/*  620 */         this.width = ((int)Math.rint(this.height * this.asp_bk));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  625 */     updateClipRect();
/*  626 */     this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/*  627 */     this.oldX = this.x; this.oldY = this.y;
/*  628 */     this.oldWidth = this.width; this.oldHeight = this.height;
/*      */   }
/*      */ 
/*      */   void move(int sx, int sy) {
/*  632 */     int xNew = this.ic.offScreenX(sx);
/*  633 */     int yNew = this.ic.offScreenY(sy);
/*  634 */     this.x += xNew - this.startX;
/*  635 */     this.y += yNew - this.startY;
/*  636 */     boolean isImageRoi = this instanceof ImageRoi;
/*  637 */     if ((this.clipboard == null) && (this.type == 0) && (!isImageRoi)) {
/*  638 */       if (this.x < 0) this.x = 0; if (this.y < 0) this.y = 0;
/*  639 */       if (this.x + this.width > this.xMax) this.x = (this.xMax - this.width);
/*  640 */       if (this.y + this.height > this.yMax) this.y = (this.yMax - this.height);
/*      */     }
/*  642 */     this.startX = xNew;
/*  643 */     this.startY = yNew;
/*  644 */     updateClipRect();
/*  645 */     if ((lineWidth > 1) && (isLine()))
/*  646 */       this.imp.draw();
/*      */     else
/*  648 */       this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/*  649 */     this.oldX = this.x;
/*  650 */     this.oldY = this.y;
/*  651 */     this.oldWidth = this.width;
/*  652 */     this.oldHeight = this.height;
/*  653 */     if (isImageRoi) showStatus();
/*      */   }
/*      */ 
/*      */   public void nudge(int key)
/*      */   {
/*  658 */     switch (key) {
/*      */     case 38:
/*  660 */       this.y -= 1;
/*  661 */       if ((this.y < 0) && ((this.type != 0) || (this.clipboard == null)))
/*  662 */         this.y = 0; break;
/*      */     case 40:
/*  665 */       this.y += 1;
/*  666 */       if ((this.y + this.height >= this.yMax) && ((this.type != 0) || (this.clipboard == null)))
/*  667 */         this.y = (this.yMax - this.height); break;
/*      */     case 37:
/*  670 */       this.x -= 1;
/*  671 */       if ((this.x < 0) && ((this.type != 0) || (this.clipboard == null)))
/*  672 */         this.x = 0; break;
/*      */     case 39:
/*  675 */       this.x += 1;
/*  676 */       if ((this.x + this.width >= this.xMax) && ((this.type != 0) || (this.clipboard == null)))
/*  677 */         this.x = (this.xMax - this.width);
/*      */       break;
/*      */     }
/*  680 */     updateClipRect();
/*  681 */     this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/*  682 */     this.oldX = this.x; this.oldY = this.y;
/*  683 */     showStatus();
/*      */   }
/*      */ 
/*      */   public void nudgeCorner(int key)
/*      */   {
/*  689 */     if ((this.type > 1) || (this.clipboard != null))
/*  690 */       return;
/*  691 */     switch (key) {
/*      */     case 38:
/*  693 */       this.height -= 1;
/*  694 */       if (this.height < 1) this.height = 1; break;
/*      */     case 40:
/*  697 */       this.height += 1;
/*  698 */       if (this.y + this.height > this.yMax) this.height = (this.yMax - this.y); break;
/*      */     case 37:
/*  701 */       this.width -= 1;
/*  702 */       if (this.width < 1) this.width = 1; break;
/*      */     case 39:
/*  705 */       this.width += 1;
/*  706 */       if (this.x + this.width > this.xMax) this.width = (this.xMax - this.x);
/*      */       break;
/*      */     }
/*  709 */     updateClipRect();
/*  710 */     this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/*  711 */     this.oldX = this.x; this.oldY = this.y;
/*  712 */     this.cachedMask = null;
/*  713 */     showStatus();
/*      */   }
/*      */ 
/*      */   protected void updateClipRect()
/*      */   {
/*  718 */     this.clipX = (this.x <= this.oldX ? this.x : this.oldX);
/*  719 */     this.clipY = (this.y <= this.oldY ? this.y : this.oldY);
/*  720 */     this.clipWidth = ((this.x + this.width >= this.oldX + this.oldWidth ? this.x + this.width : this.oldX + this.oldWidth) - this.clipX + 1);
/*  721 */     this.clipHeight = ((this.y + this.height >= this.oldY + this.oldHeight ? this.y + this.height : this.oldY + this.oldHeight) - this.clipY + 1);
/*  722 */     int m = 3;
/*  723 */     if (this.ic != null) {
/*  724 */       double mag = this.ic.getMagnification();
/*  725 */       if (mag < 1.0D)
/*  726 */         m = (int)(4.0D / mag);
/*      */     }
/*  728 */     m += clipRectMargin();
/*  729 */     m = (int)(m + getStrokeWidth() * 2.0F);
/*  730 */     this.clipX -= m; this.clipY -= m;
/*  731 */     this.clipWidth += m * 2; this.clipHeight += m * 2;
/*      */   }
/*      */ 
/*      */   protected int clipRectMargin()
/*      */   {
/*  736 */     return 0;
/*      */   }
/*      */ 
/*      */   protected void handleMouseDrag(int sx, int sy, int flags) {
/*  740 */     if (this.ic == null) return;
/*  741 */     this.constrain = ((flags & 0x1) != 0);
/*  742 */     this.center = (((flags & 0x2) != 0) || ((IJ.isMacintosh()) && ((flags & 0x4) != 0)));
/*  743 */     this.aspect = ((flags & 0x8) != 0);
/*  744 */     switch (this.state) {
/*      */     case 0:
/*  746 */       grow(sx, sy);
/*  747 */       break;
/*      */     case 1:
/*  749 */       move(sx, sy);
/*  750 */       break;
/*      */     case 4:
/*  752 */       moveHandle(sx, sy);
/*  753 */       break;
/*      */     case 2:
/*      */     case 3:
/*      */     }
/*      */   }
/*      */ 
/*      */   int getHandleSize() {
/*  760 */     double mag = this.ic != null ? this.ic.getMagnification() : 1.0D;
/*  761 */     double size = 5.0D / mag;
/*  762 */     return (int)(size * mag);
/*      */   }
/*      */ 
/*      */   public void draw(Graphics g) {
/*  766 */     Color color = this.strokeColor != null ? this.strokeColor : ROIColor;
/*  767 */     if (this.fillColor != null) color = this.fillColor;
/*  768 */     if ((Interpreter.isBatchMode()) && (this.ic != null) && (this.ic.getDisplayList() != null) && (this.strokeColor == null) && (this.fillColor == null))
/*  769 */       return;
/*  770 */     g.setColor(color);
/*  771 */     this.mag = getMagnification();
/*  772 */     int sw = (int)(this.width * this.mag);
/*  773 */     int sh = (int)(this.height * this.mag);
/*  774 */     int sx1 = screenX(this.x);
/*  775 */     int sy1 = screenY(this.y);
/*  776 */     int sx2 = sx1 + sw / 2;
/*  777 */     int sy2 = sy1 + sh / 2;
/*  778 */     int sx3 = sx1 + sw;
/*  779 */     int sy3 = sy1 + sh;
/*  780 */     Graphics2D g2d = (Graphics2D)g;
/*  781 */     if (this.stroke != null)
/*  782 */       g2d.setStroke(getScaledStroke());
/*  783 */     if (this.cornerDiameter > 0) {
/*  784 */       int sArcSize = (int)Math.round(this.cornerDiameter * this.mag);
/*  785 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  786 */       if (this.fillColor != null)
/*  787 */         g.fillRoundRect(sx1, sy1, sw, sh, sArcSize, sArcSize);
/*      */       else
/*  789 */         g.drawRoundRect(sx1, sy1, sw, sh, sArcSize, sArcSize);
/*      */     }
/*  791 */     else if (this.fillColor != null) {
/*  792 */       g.fillRect(sx1, sy1, sw, sh);
/*      */     } else {
/*  794 */       g.drawRect(sx1, sy1, sw, sh);
/*      */     }
/*  796 */     if ((this.state != 0) && (this.clipboard == null) && (!this.overlay)) {
/*  797 */       int size2 = 2;
/*  798 */       drawHandle(g, sx1 - size2, sy1 - size2);
/*  799 */       drawHandle(g, sx2 - size2, sy1 - size2);
/*  800 */       drawHandle(g, sx3 - size2, sy1 - size2);
/*  801 */       drawHandle(g, sx3 - size2, sy2 - size2);
/*  802 */       drawHandle(g, sx3 - size2, sy3 - size2);
/*  803 */       drawHandle(g, sx2 - size2, sy3 - size2);
/*  804 */       drawHandle(g, sx1 - size2, sy3 - size2);
/*  805 */       drawHandle(g, sx1 - size2, sy2 - size2);
/*      */     }
/*  807 */     drawPreviousRoi(g);
/*  808 */     if (this.state != 3) showStatus();
/*  809 */     if (this.updateFullWindow) {
/*  810 */       this.updateFullWindow = false; this.imp.draw();
/*      */     }
/*      */   }
/*      */ 
/*  814 */   public void drawOverlay(Graphics g) { this.overlay = true;
/*  815 */     draw(g);
/*  816 */     this.overlay = false; }
/*      */ 
/*      */   void drawPreviousRoi(Graphics g)
/*      */   {
/*  820 */     if ((previousRoi != null) && (previousRoi != this) && (previousRoi.modState != 0)) {
/*  821 */       if ((this.type != 10) && (previousRoi.getType() == 10) && (previousRoi.modState != 2))
/*  822 */         return;
/*  823 */       previousRoi.setImage(this.imp);
/*  824 */       previousRoi.draw(g);
/*      */     }
/*      */   }
/*      */ 
/*      */   void drawHandle(Graphics g, int x, int y) {
/*  829 */     double size = this.width * this.height * this.mag * this.mag;
/*  830 */     if (this.type == 5) {
/*  831 */       size = Math.sqrt(this.width * this.width + this.height * this.height);
/*  832 */       size *= size * this.mag * this.mag;
/*      */     }
/*  834 */     if (size > 4000.0D) {
/*  835 */       g.setColor(Color.black);
/*  836 */       g.fillRect(x, y, 5, 5);
/*  837 */       g.setColor(this.handleColor);
/*  838 */       g.fillRect(x + 1, y + 1, 3, 3);
/*  839 */     } else if (size > 1000.0D) {
/*  840 */       g.setColor(Color.black);
/*  841 */       g.fillRect(x + 1, y + 1, 4, 4);
/*  842 */       g.setColor(this.handleColor);
/*  843 */       g.fillRect(x + 2, y + 2, 2, 2);
/*      */     } else {
/*  845 */       g.setColor(Color.black);
/*  846 */       g.fillRect(x + 1, y + 1, 3, 3);
/*  847 */       g.setColor(this.handleColor);
/*  848 */       g.fillRect(x + 2, y + 2, 1, 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void drawPixels()
/*      */   {
/*  857 */     if (this.imp != null)
/*  858 */       drawPixels(this.imp.getProcessor());
/*      */   }
/*      */ 
/*      */   public void drawPixels(ImageProcessor ip)
/*      */   {
/*  866 */     endPaste();
/*  867 */     int saveWidth = ip.getLineWidth();
/*  868 */     if (getStrokeWidth() > 1.0F)
/*  869 */       ip.setLineWidth(Math.round(getStrokeWidth()));
/*  870 */     if (this.cornerDiameter > 0) {
/*  871 */       new ShapeRoi(new RoundRectangle2D.Float(this.x, this.y, this.width, this.height, this.cornerDiameter, this.cornerDiameter)).drawPixels(ip);
/*      */     }
/*  873 */     else if (ip.getLineWidth() == 1)
/*  874 */       ip.drawRect(this.x, this.y, this.width + 1, this.height + 1);
/*      */     else {
/*  876 */       ip.drawRect(this.x, this.y, this.width, this.height);
/*      */     }
/*  878 */     ip.setLineWidth(saveWidth);
/*  879 */     if ((Line.getWidth() > 1) || (getStrokeWidth() > 1.0F))
/*  880 */       this.updateFullWindow = true;
/*      */   }
/*      */ 
/*      */   public boolean contains(int x, int y) {
/*  884 */     Rectangle r = new Rectangle(this.x, this.y, this.width, this.height);
/*  885 */     boolean contains = r.contains(x, y);
/*  886 */     if ((this.cornerDiameter == 0) || (!contains))
/*  887 */       return contains;
/*  888 */     RoundRectangle2D rr = new RoundRectangle2D.Float(this.x, this.y, this.width, this.height, this.cornerDiameter, this.cornerDiameter);
/*  889 */     return rr.contains(x, y);
/*      */   }
/*      */ 
/*      */   public int isHandle(int sx, int sy)
/*      */   {
/*  895 */     if ((this.clipboard != null) || (this.ic == null)) return -1;
/*  896 */     double mag = this.ic.getMagnification();
/*  897 */     int size = 8;
/*  898 */     int halfSize = size / 2;
/*  899 */     int sx1 = this.ic.screenX(this.x) - halfSize;
/*  900 */     int sy1 = this.ic.screenY(this.y) - halfSize;
/*  901 */     int sx3 = this.ic.screenX(this.x + this.width) - halfSize;
/*  902 */     int sy3 = this.ic.screenY(this.y + this.height) - halfSize;
/*  903 */     int sx2 = sx1 + (sx3 - sx1) / 2;
/*  904 */     int sy2 = sy1 + (sy3 - sy1) / 2;
/*  905 */     if ((sx >= sx1) && (sx <= sx1 + size) && (sy >= sy1) && (sy <= sy1 + size)) return 0;
/*  906 */     if ((sx >= sx2) && (sx <= sx2 + size) && (sy >= sy1) && (sy <= sy1 + size)) return 1;
/*  907 */     if ((sx >= sx3) && (sx <= sx3 + size) && (sy >= sy1) && (sy <= sy1 + size)) return 2;
/*  908 */     if ((sx >= sx3) && (sx <= sx3 + size) && (sy >= sy2) && (sy <= sy2 + size)) return 3;
/*  909 */     if ((sx >= sx3) && (sx <= sx3 + size) && (sy >= sy3) && (sy <= sy3 + size)) return 4;
/*  910 */     if ((sx >= sx2) && (sx <= sx2 + size) && (sy >= sy3) && (sy <= sy3 + size)) return 5;
/*  911 */     if ((sx >= sx1) && (sx <= sx1 + size) && (sy >= sy3) && (sy <= sy3 + size)) return 6;
/*  912 */     if ((sx >= sx1) && (sx <= sx1 + size) && (sy >= sy2) && (sy <= sy2 + size)) return 7;
/*  913 */     return -1;
/*      */   }
/*      */ 
/*      */   protected void mouseDownInHandle(int handle, int sx, int sy) {
/*  917 */     this.state = 4;
/*  918 */     this.activeHandle = handle;
/*      */   }
/*      */ 
/*      */   protected void handleMouseDown(int sx, int sy) {
/*  922 */     if ((this.state == 3) && (this.ic != null)) {
/*  923 */       this.state = 1;
/*  924 */       this.startX = this.ic.offScreenX(sx);
/*  925 */       this.startY = this.ic.offScreenY(sy);
/*  926 */       showStatus();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void handleMouseUp(int screenX, int screenY) {
/*  931 */     this.state = 3;
/*  932 */     if (this.imp == null) return;
/*  933 */     this.imp.draw(this.clipX - 5, this.clipY - 5, this.clipWidth + 10, this.clipHeight + 10);
/*  934 */     if (Recorder.record)
/*      */     {
/*  936 */       if (this.type == 5) {
/*  937 */         Line line = (Line)this.imp.getRoi();
/*  938 */         Recorder.record("makeLine", line.x1, line.y1, line.x2, line.y2);
/*  939 */       } else if (this.type == 1) {
/*  940 */         Recorder.record("makeOval", this.x, this.y, this.width, this.height);
/*  941 */       } else if (!(this instanceof TextRoi)) {
/*  942 */         if (this.cornerDiameter == 0) {
/*  943 */           Recorder.record("makeRectangle", this.x, this.y, this.width, this.height);
/*      */         }
/*  945 */         else if (Recorder.scriptMode())
/*  946 */           Recorder.recordCall("imp.setRoi(new Roi(" + this.x + ", " + this.y + ", " + this.width + ", " + this.height + ", " + this.cornerDiameter + "));");
/*      */         else {
/*  948 */           Recorder.record("makeRectangle", this.x, this.y, this.width, this.height, this.cornerDiameter);
/*      */         }
/*      */       }
/*      */     }
/*  952 */     if ((Toolbar.getToolId() == 1) && (Toolbar.getBrushSize() > 0)) {
/*  953 */       int flags = this.ic != null ? this.ic.getModifiers() : 16;
/*  954 */       if ((flags & 0x10) == 0) {
/*  955 */         this.imp.draw(); return;
/*      */       }
/*      */     }
/*  957 */     modifyRoi();
/*      */   }
/*      */ 
/*      */   void modifyRoi() {
/*  961 */     if ((previousRoi == null) || (previousRoi.modState == 0) || (this.imp == null)) {
/*  962 */       return;
/*      */     }
/*  964 */     if ((this.type == 10) || (previousRoi.getType() == 10)) {
/*  965 */       if ((this.type == 10) && (previousRoi.getType() == 10))
/*  966 */         addPoint();
/*  967 */       else if ((isArea()) && (previousRoi.getType() == 10) && (previousRoi.modState == 2))
/*  968 */         subtractPoints();
/*  969 */       return;
/*      */     }
/*  971 */     Roi previous = (Roi)previousRoi.clone();
/*  972 */     previous.modState = 0;
/*  973 */     ShapeRoi s1 = null;
/*  974 */     ShapeRoi s2 = null;
/*  975 */     if ((previousRoi instanceof ShapeRoi))
/*  976 */       s1 = (ShapeRoi)previousRoi;
/*      */     else
/*  978 */       s1 = new ShapeRoi(previousRoi);
/*  979 */     if ((this instanceof ShapeRoi))
/*  980 */       s2 = (ShapeRoi)this;
/*      */     else
/*  982 */       s2 = new ShapeRoi(this);
/*  983 */     if (previousRoi.modState == 1)
/*  984 */       s1.or(s2);
/*      */     else
/*  986 */       s1.not(s2);
/*  987 */     previousRoi.modState = 0;
/*  988 */     Roi[] rois = s1.getRois();
/*  989 */     if (rois.length == 0) return;
/*  990 */     int type2 = rois[0].getType();
/*      */ 
/*  992 */     Roi roi2 = null;
/*  993 */     if ((rois.length == 1) && ((type2 == 2) || (type2 == 3)))
/*  994 */       roi2 = rois[0];
/*      */     else
/*  996 */       roi2 = s1;
/*  997 */     if (roi2 != null)
/*  998 */       roi2.copyAttributes(previousRoi);
/*  999 */     this.imp.setRoi(roi2);
/* 1000 */     previousRoi = previous;
/*      */   }
/*      */ 
/*      */   void addPoint() {
/* 1004 */     if ((this.type != 10) || (previousRoi.getType() != 10)) {
/* 1005 */       this.modState = 0;
/* 1006 */       this.imp.draw();
/* 1007 */       return;
/*      */     }
/* 1009 */     previousRoi.modState = 0;
/* 1010 */     PointRoi p1 = (PointRoi)previousRoi;
/* 1011 */     Rectangle r = getBounds();
/* 1012 */     this.imp.setRoi(p1.addPoint(r.x, r.y));
/*      */   }
/*      */ 
/*      */   void subtractPoints() {
/* 1016 */     previousRoi.modState = 0;
/* 1017 */     PointRoi p1 = (PointRoi)previousRoi;
/* 1018 */     PointRoi p2 = p1.subtractPoints(this);
/* 1019 */     if (p2 != null)
/* 1020 */       this.imp.setRoi(p1.subtractPoints(this));
/*      */     else
/* 1022 */       this.imp.killRoi();
/*      */   }
/*      */ 
/*      */   public void update(boolean add, boolean subtract)
/*      */   {
/* 1029 */     if (previousRoi == null) return;
/* 1030 */     if (add) {
/* 1031 */       previousRoi.modState = 1;
/* 1032 */       modifyRoi();
/* 1033 */     } else if (subtract) {
/* 1034 */       previousRoi.modState = 2;
/* 1035 */       modifyRoi();
/*      */     } else {
/* 1037 */       previousRoi.modState = 0;
/*      */     }
/*      */   }
/*      */ 
/* 1041 */   protected void showStatus() { if (this.imp == null)
/*      */       return;
/*      */     String value;
/*      */     String value;
/* 1043 */     if ((this.state != 0) && ((this.type == 0) || (this.type == 10)) && (this.width <= 25) && (this.height <= 25)) {
/* 1044 */       ImageProcessor ip = this.imp.getProcessor();
/* 1045 */       double v = ip.getPixelValue(this.x, this.y);
/* 1046 */       int digits = (this.imp.getType() == 0) || (this.imp.getType() == 1) ? 0 : 2;
/* 1047 */       value = ", value=" + IJ.d2s(v, digits);
/*      */     } else {
/* 1049 */       value = "";
/* 1050 */     }Calibration cal = this.imp.getCalibration();
/*      */     String size;
/* 1052 */     if ((cal.scaled()) && (!IJ.altKeyDown()))
/* 1053 */       size = ", w=" + IJ.d2s(this.width * cal.pixelWidth) + ", h=" + IJ.d2s(this.height * cal.pixelHeight);
/*      */     else
/* 1055 */       size = ", w=" + this.width + ", h=" + this.height;
/* 1056 */     String size = size + ", ar=" + IJ.d2s(this.width / this.height, 2);
/* 1057 */     IJ.showStatus(this.imp.getLocationAsString(this.x, this.y) + size + value);
/*      */   }
/*      */ 
/*      */   public ImageProcessor getMask()
/*      */   {
/* 1062 */     if (this.cornerDiameter > 0) {
/* 1063 */       return new ShapeRoi(new RoundRectangle2D.Float(this.x, this.y, this.width, this.height, this.cornerDiameter, this.cornerDiameter)).getMask();
/*      */     }
/* 1065 */     return null;
/*      */   }
/*      */ 
/*      */   public void startPaste(ImagePlus clipboard) {
/* 1069 */     IJ.showStatus("Pasting...");
/* 1070 */     this.clipboard = clipboard;
/* 1071 */     this.imp.getProcessor().snapshot();
/* 1072 */     updateClipRect();
/* 1073 */     this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/*      */   }
/*      */ 
/*      */   void updatePaste() {
/* 1077 */     if (this.clipboard != null) {
/* 1078 */       this.imp.getMask();
/* 1079 */       ImageProcessor ip = this.imp.getProcessor();
/* 1080 */       ip.reset();
/* 1081 */       ip.copyBits(this.clipboard.getProcessor(), this.x, this.y, pasteMode);
/* 1082 */       if (this.type != 0)
/* 1083 */         ip.reset(ip.getMask());
/* 1084 */       if (this.ic != null)
/* 1085 */         this.ic.setImageUpdated();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endPaste() {
/* 1090 */     if (this.clipboard != null) {
/* 1091 */       updatePaste();
/* 1092 */       this.clipboard = null;
/* 1093 */       Undo.setup(1, this.imp);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void abortPaste() {
/* 1098 */     this.clipboard = null;
/* 1099 */     this.imp.getProcessor().reset();
/* 1100 */     this.imp.updateAndDraw();
/*      */   }
/*      */ 
/*      */   public double getAngle(int x1, int y1, int x2, int y2)
/*      */   {
/* 1105 */     double dx = x2 - x1;
/* 1106 */     double dy = y1 - y2;
/* 1107 */     if ((this.imp != null) && (!IJ.altKeyDown())) {
/* 1108 */       Calibration cal = this.imp.getCalibration();
/* 1109 */       dx *= cal.pixelWidth;
/* 1110 */       dy *= cal.pixelHeight;
/*      */     }
/* 1112 */     return 57.295779513082323D * Math.atan2(dy, dx);
/*      */   }
/*      */ 
/*      */   public static void setColor(Color c)
/*      */   {
/* 1120 */     ROIColor = c;
/*      */   }
/*      */ 
/*      */   public static Color getColor()
/*      */   {
/* 1128 */     return ROIColor;
/*      */   }
/*      */ 
/*      */   public void setStrokeColor(Color c)
/*      */   {
/* 1138 */     this.strokeColor = c;
/*      */   }
/*      */ 
/*      */   public Color getStrokeColor()
/*      */   {
/* 1145 */     return this.strokeColor;
/*      */   }
/*      */ 
/*      */   public void setFillColor(Color color)
/*      */   {
/* 1152 */     this.fillColor = color;
/*      */   }
/*      */ 
/*      */   public Color getFillColor()
/*      */   {
/* 1159 */     return this.fillColor;
/*      */   }
/*      */ 
/*      */   public static void setDefaultFillColor(Color color) {
/* 1163 */     defaultFillColor = color;
/*      */   }
/*      */ 
/*      */   public static Color getDefaultFillColor() {
/* 1167 */     return defaultFillColor;
/*      */   }
/*      */ 
/*      */   public void copyAttributes(Roi roi2)
/*      */   {
/* 1173 */     this.strokeColor = roi2.strokeColor;
/* 1174 */     this.fillColor = roi2.fillColor;
/* 1175 */     this.stroke = roi2.stroke;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setInstanceColor(Color c)
/*      */   {
/* 1183 */     this.strokeColor = c;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setLineWidth(int width)
/*      */   {
/* 1191 */     setStrokeWidth(width);
/*      */   }
/*      */ 
/*      */   public void updateWideLine(float width)
/*      */   {
/* 1196 */     if (isLine()) {
/* 1197 */       this.wideLine = true;
/* 1198 */       setStrokeWidth(width);
/* 1199 */       if (getStrokeColor() == null) {
/* 1200 */         Color c = getColor();
/* 1201 */         setStrokeColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 77));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNonScalable(boolean nonScalable)
/*      */   {
/* 1209 */     this.nonScalable = nonScalable;
/*      */   }
/*      */ 
/*      */   public void setStrokeWidth(float width)
/*      */   {
/* 1218 */     if (width == 0.0F)
/* 1219 */       this.stroke = null;
/* 1220 */     else if (this.wideLine)
/* 1221 */       this.stroke = new BasicStroke(width, 0, 2);
/*      */     else
/* 1223 */       this.stroke = new BasicStroke(width);
/* 1224 */     if (width > 1.0F) this.fillColor = null;
/*      */   }
/*      */ 
/*      */   public void setStrokeWidth(double width)
/*      */   {
/* 1229 */     setStrokeWidth((float)width);
/*      */   }
/*      */ 
/*      */   public float getStrokeWidth()
/*      */   {
/* 1234 */     return this.stroke != null ? this.stroke.getLineWidth() : 1.0F;
/*      */   }
/*      */ 
/*      */   public void setStroke(BasicStroke stroke)
/*      */   {
/* 1239 */     this.stroke = stroke;
/*      */   }
/*      */ 
/*      */   public BasicStroke getStroke()
/*      */   {
/* 1244 */     return this.stroke;
/*      */   }
/*      */ 
/*      */   protected BasicStroke getScaledStroke() {
/* 1248 */     if (this.ic == null) return this.stroke;
/* 1249 */     double mag = this.ic.getMagnification();
/* 1250 */     if (mag != 1.0D) {
/* 1251 */       float width = this.stroke.getLineWidth();
/* 1252 */       return new BasicStroke((float)(width * mag), 0, 2);
/*      */     }
/* 1254 */     return this.stroke;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/* 1259 */     return this.name;
/*      */   }
/*      */ 
/*      */   public void setName(String name)
/*      */   {
/* 1264 */     this.name = name;
/*      */   }
/*      */ 
/*      */   public static void setPasteMode(int transferMode)
/*      */   {
/* 1271 */     if (transferMode == pasteMode) return;
/* 1272 */     pasteMode = transferMode;
/* 1273 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1274 */     if (imp != null)
/* 1275 */       imp.updateAndDraw();
/*      */   }
/*      */ 
/*      */   public void setCornerDiameter(int cornerDiameter)
/*      */   {
/* 1280 */     if (cornerDiameter < 0) cornerDiameter = 0;
/* 1281 */     this.cornerDiameter = cornerDiameter;
/* 1282 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1283 */     if ((imp != null) && (this == imp.getRoi()))
/* 1284 */       imp.updateAndDraw();
/*      */   }
/*      */ 
/*      */   public int getCornerDiameter()
/*      */   {
/* 1289 */     return this.cornerDiameter;
/*      */   }
/*      */ 
/*      */   public void setRoundRectArcSize(int cornerDiameter)
/*      */   {
/* 1294 */     setCornerDiameter(cornerDiameter);
/*      */   }
/*      */ 
/*      */   public int getRoundRectArcSize()
/*      */   {
/* 1299 */     return this.cornerDiameter;
/*      */   }
/*      */ 
/*      */   public void setPosition(int n)
/*      */   {
/* 1308 */     if (n < 0) n = 0;
/* 1309 */     this.position = n;
/* 1310 */     this.channel = (this.slice = this.frame = 0);
/*      */   }
/*      */ 
/*      */   public int getPosition()
/*      */   {
/* 1318 */     return this.position;
/*      */   }
/*      */ 
/*      */   public void setPosition(int channel, int slice, int frame)
/*      */   {
/* 1326 */     if (channel < 0) channel = 0;
/* 1327 */     this.channel = channel;
/* 1328 */     if (slice < 0) slice = 0;
/* 1329 */     this.slice = slice;
/* 1330 */     if (frame < 0) frame = 0;
/* 1331 */     this.frame = frame;
/* 1332 */     this.position = 0;
/*      */   }
/*      */ 
/*      */   public final int getCPosition()
/*      */   {
/* 1339 */     return this.channel;
/*      */   }
/*      */ 
/*      */   public final int getZPosition()
/*      */   {
/* 1346 */     return this.slice;
/*      */   }
/*      */ 
/*      */   public final int getTPosition()
/*      */   {
/* 1353 */     return this.frame;
/*      */   }
/*      */ 
/*      */   public void setPrototypeOverlay(Overlay overlay)
/*      */   {
/* 1358 */     this.prototypeOverlay = new Overlay();
/* 1359 */     this.prototypeOverlay.drawLabels(overlay.getDrawLabels());
/* 1360 */     this.prototypeOverlay.drawNames(overlay.getDrawNames());
/* 1361 */     this.prototypeOverlay.drawBackgrounds(overlay.getDrawBackgrounds());
/* 1362 */     this.prototypeOverlay.setLabelColor(overlay.getLabelColor());
/* 1363 */     this.prototypeOverlay.setLabelFont(overlay.getLabelFont());
/*      */   }
/*      */ 
/*      */   public Overlay getPrototypeOverlay()
/*      */   {
/* 1368 */     if (this.prototypeOverlay != null) {
/* 1369 */       return this.prototypeOverlay;
/*      */     }
/* 1371 */     return new Overlay();
/*      */   }
/*      */ 
/*      */   public int getPasteMode()
/*      */   {
/* 1379 */     if (this.clipboard == null) {
/* 1380 */       return -1;
/*      */     }
/* 1382 */     return pasteMode;
/*      */   }
/*      */ 
/*      */   public static int getCurrentPasteMode()
/*      */   {
/* 1387 */     return pasteMode;
/*      */   }
/*      */ 
/*      */   public boolean isArea()
/*      */   {
/* 1392 */     return ((this.type >= 0) && (this.type <= 4)) || (this.type == 9);
/*      */   }
/*      */ 
/*      */   public boolean isLine()
/*      */   {
/* 1397 */     return (this.type >= 5) && (this.type <= 7);
/*      */   }
/*      */ 
/*      */   public boolean isDrawingTool()
/*      */   {
/* 1403 */     return this.cornerDiameter > 0;
/*      */   }
/*      */ 
/*      */   protected double getMagnification() {
/* 1407 */     return this.ic != null ? this.ic.getMagnification() : 1.0D;
/*      */   }
/*      */ 
/*      */   public String getTypeAsString()
/*      */   {
/* 1412 */     String s = "";
/* 1413 */     switch (this.type) { case 2:
/* 1414 */       s = "Polygon"; break;
/*      */     case 3:
/* 1415 */       s = "Freehand"; break;
/*      */     case 4:
/* 1416 */       s = "Traced"; break;
/*      */     case 6:
/* 1417 */       s = "Polyline"; break;
/*      */     case 7:
/* 1418 */       s = "Freeline"; break;
/*      */     case 8:
/* 1419 */       s = "Angle"; break;
/*      */     case 5:
/* 1420 */       s = "Straight Line"; break;
/*      */     case 1:
/* 1421 */       s = "Oval"; break;
/*      */     case 9:
/* 1422 */       s = "Composite"; break;
/*      */     case 10:
/* 1423 */       s = "Point"; break;
/*      */     default:
/* 1424 */       s = "Rectangle";
/*      */     }
/* 1426 */     return s;
/*      */   }
/*      */ 
/*      */   public boolean isVisible()
/*      */   {
/* 1431 */     return this.ic != null;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object obj)
/*      */   {
/* 1436 */     if ((obj instanceof Roi)) {
/* 1437 */       Roi roi2 = (Roi)obj;
/* 1438 */       if (this.type != roi2.getType()) return false;
/* 1439 */       if (!getBounds().equals(roi2.getBounds())) return false;
/* 1440 */       if (getLength() != roi2.getLength()) return false;
/* 1441 */       return true;
/*      */     }
/* 1443 */     return false;
/*      */   }
/*      */   protected int screenX(int ox) {
/* 1446 */     return this.ic != null ? this.ic.screenX(ox) : ox; } 
/* 1447 */   protected int screenY(int oy) { return this.ic != null ? this.ic.screenY(oy) : oy; } 
/* 1448 */   protected int screenXD(double ox) { return this.ic != null ? this.ic.screenXD(ox) : (int)ox; } 
/* 1449 */   protected int screenYD(double oy) { return this.ic != null ? this.ic.screenYD(oy) : (int)oy; }
/*      */ 
/*      */   protected int[] toInt(float[] arr) {
/* 1452 */     int n = arr.length;
/* 1453 */     int[] temp = new int[n];
/* 1454 */     for (int i = 0; i < n; i++)
/* 1455 */       temp[i] = ((int)Math.floor(arr[i] + 0.5D));
/* 1456 */     return temp;
/*      */   }
/*      */ 
/*      */   protected float[] toFloat(int[] arr) {
/* 1460 */     int n = arr.length;
/* 1461 */     float[] temp = new float[n];
/* 1462 */     for (int i = 0; i < n; i++)
/* 1463 */       temp[i] = arr[i];
/* 1464 */     return temp;
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1468 */     return "Roi[" + getTypeAsString() + ", x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + "]";
/*      */   }
/*      */ 
/*      */   public void temporarilyHide()
/*      */   {
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.Roi
 * JD-Core Version:    0.6.2
 */