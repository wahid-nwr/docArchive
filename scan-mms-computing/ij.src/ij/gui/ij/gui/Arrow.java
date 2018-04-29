/*     */ package ij.gui;
/*     */ 
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Shape;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Area;
/*     */ import java.awt.geom.GeneralPath;
/*     */ 
/*     */ public class Arrow extends Line
/*     */ {
/*     */   public static final String STYLE_KEY = "arrow.style";
/*     */   public static final String WIDTH_KEY = "arrow.width";
/*     */   public static final String SIZE_KEY = "arrow.size";
/*     */   public static final String DOUBLE_HEADED_KEY = "arrow.double";
/*     */   public static final String OUTLINE_KEY = "arrow.outline";
/*     */   public static final int FILLED = 0;
/*     */   public static final int NOTCHED = 1;
/*     */   public static final int OPEN = 2;
/*     */   public static final int HEADLESS = 3;
/*  16 */   public static final String[] styles = { "Filled", "Notched", "Open", "Headless" };
/*  17 */   private static int defaultStyle = (int)Prefs.get("arrow.style", 0.0D);
/*  18 */   private static float defaultWidth = (float)Prefs.get("arrow.width", 2.0D);
/*  19 */   private static double defaultHeadSize = (int)Prefs.get("arrow.size", 10.0D);
/*  20 */   private static boolean defaultDoubleHeaded = Prefs.get("arrow.double", false);
/*  21 */   private static boolean defaultOutline = Prefs.get("arrow.outline", false);
/*     */   private int style;
/*  23 */   private double headSize = 10.0D;
/*     */   private boolean doubleHeaded;
/*     */   private boolean outline;
/*  26 */   private float[] points = new float[10];
/*  27 */   private GeneralPath path = new GeneralPath();
/*  28 */   private static Stroke defaultStroke = new BasicStroke();
/*     */ 
/*     */   public Arrow(double ox1, double oy1, double ox2, double oy2)
/*     */   {
/*  36 */     super(ox1, oy1, ox2, oy2);
/*  37 */     setStrokeWidth(2.0F);
/*     */   }
/*     */ 
/*     */   public Arrow(int sx, int sy, ImagePlus imp) {
/*  41 */     super(sx, sy, imp);
/*  42 */     setStrokeWidth(defaultWidth);
/*  43 */     this.style = defaultStyle;
/*  44 */     this.headSize = defaultHeadSize;
/*  45 */     this.doubleHeaded = defaultDoubleHeaded;
/*  46 */     this.outline = defaultOutline;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g)
/*     */   {
/*  51 */     Shape shape2 = null;
/*  52 */     if (this.doubleHeaded) {
/*  53 */       flipEnds();
/*  54 */       shape2 = getShape();
/*  55 */       flipEnds();
/*     */     }
/*  57 */     Shape shape = getShape();
/*  58 */     Color color = this.strokeColor != null ? this.strokeColor : ROIColor;
/*  59 */     if (this.fillColor != null) color = this.fillColor;
/*  60 */     g.setColor(color);
/*  61 */     Graphics2D g2 = (Graphics2D)g;
/*  62 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  63 */     AffineTransform at = g2.getDeviceConfiguration().getDefaultTransform();
/*  64 */     double mag = getMagnification();
/*  65 */     int xbase = 0; int ybase = 0;
/*  66 */     if (this.ic != null) {
/*  67 */       Rectangle r = this.ic.getSrcRect();
/*  68 */       xbase = r.x; ybase = r.y;
/*     */     }
/*  70 */     at.setTransform(mag, 0.0D, 0.0D, mag, -xbase * mag, -ybase * mag);
/*  71 */     if (this.outline) {
/*  72 */       float lineWidth = (float)(getOutlineWidth() * mag);
/*  73 */       g2.setStroke(new BasicStroke(lineWidth, 0, 1));
/*  74 */       g2.draw(at.createTransformedShape(shape));
/*  75 */       if (this.doubleHeaded) g2.draw(at.createTransformedShape(shape2));
/*  76 */       g2.setStroke(defaultStroke);
/*     */     } else {
/*  78 */       g2.fill(at.createTransformedShape(shape));
/*  79 */       if (this.doubleHeaded) g2.fill(at.createTransformedShape(shape2));
/*     */     }
/*  81 */     if ((this.state != 0) && (!this.overlay)) {
/*  82 */       int size2 = 2;
/*  83 */       this.handleColor = Color.white;
/*  84 */       drawHandle(g, screenXD(this.x1d) - size2, screenYD(this.y1d) - size2);
/*  85 */       drawHandle(g, screenXD(this.x2d) - size2, screenYD(this.y2d) - size2);
/*  86 */       drawHandle(g, screenXD(this.x1d + (this.x2d - this.x1d) / 2.0D) - size2, screenYD(this.y1d + (this.y2d - this.y1d) / 2.0D) - size2);
/*     */     }
/*  88 */     if ((this.imp != null) && (this.imp.getRoi() != null)) showStatus();
/*  89 */     if (this.updateFullWindow) {
/*  90 */       this.updateFullWindow = false; this.imp.draw();
/*     */     }
/*     */   }
/*     */ 
/*  94 */   private void flipEnds() { double tmp = this.x1R;
/*  95 */     this.x1R = this.x2R;
/*  96 */     this.x2R = tmp;
/*  97 */     tmp = this.y1R;
/*  98 */     this.y1R = this.y2R;
/*  99 */     this.y2R = tmp; }
/*     */ 
/*     */   private Shape getPath()
/*     */   {
/* 103 */     this.path.reset();
/* 104 */     this.path = new GeneralPath();
/* 105 */     calculatePoints();
/* 106 */     this.path.moveTo(this.points[0], this.points[1]);
/* 107 */     this.path.lineTo(this.points[2], this.points[3]);
/* 108 */     this.path.moveTo(this.points[2], this.points[3]);
/* 109 */     if (this.style == 2)
/* 110 */       this.path.moveTo(this.points[4], this.points[5]);
/*     */     else
/* 112 */       this.path.lineTo(this.points[4], this.points[5]);
/* 113 */     this.path.lineTo(this.points[6], this.points[7]);
/* 114 */     this.path.lineTo(this.points[8], this.points[9]);
/* 115 */     this.path.lineTo(this.points[2], this.points[3]);
/* 116 */     return this.path;
/*     */   }
/*     */ 
/*     */   private void calculatePoints()
/*     */   {
/* 122 */     double tip = 0.0D;
/*     */ 
/* 124 */     double shaftWidth = getStrokeWidth();
/* 125 */     double length = 8.0D + 10.0D * shaftWidth * 0.5D;
/* 126 */     length *= this.headSize / 10.0D;
/* 127 */     length -= shaftWidth * 1.42D;
/* 128 */     if (this.style == 1) length *= 0.74D;
/* 129 */     if (this.style == 2) length *= 1.32D;
/* 130 */     if ((length < 0.0D) || (this.style == 3)) length = 0.0D;
/* 131 */     this.x1d = (this.x + this.x1R); this.y1d = (this.y + this.y1R); this.x2d = (this.x + this.x2R); this.y2d = (this.y + this.y2R);
/* 132 */     this.x1 = ((int)this.x1d); this.y1 = ((int)this.y1d); this.x2 = ((int)this.x2d); this.y2 = ((int)this.y2d);
/* 133 */     double dx = this.x2d - this.x1d; double dy = this.y2d - this.y1d;
/* 134 */     double arrowLength = Math.sqrt(dx * dx + dy * dy);
/* 135 */     dx /= arrowLength; dy /= arrowLength;
/* 136 */     if ((this.doubleHeaded) && (this.style != 3)) {
/* 137 */       this.points[0] = ((float)(this.x1d + dx * shaftWidth * 2.0D));
/* 138 */       this.points[1] = ((float)(this.y1d + dy * shaftWidth * 2.0D));
/*     */     } else {
/* 140 */       this.points[0] = ((float)this.x1d);
/* 141 */       this.points[1] = ((float)this.y1d);
/*     */     }
/* 143 */     if (length > 0.0D) {
/* 144 */       double factor = this.style == 2 ? 1.3D : 1.42D;
/* 145 */       this.points[6] = ((float)(this.x2d - dx * shaftWidth * factor));
/* 146 */       this.points[7] = ((float)(this.y2d - dy * shaftWidth * factor));
/*     */     } else {
/* 148 */       this.points[6] = ((float)this.x2d);
/* 149 */       this.points[7] = ((float)this.y2d);
/*     */     }
/* 151 */     double alpha = Math.atan2(this.points[7] - this.points[1], this.points[6] - this.points[0]);
/* 152 */     double SL = 0.0D;
/*     */     double base;
/* 153 */     switch (this.style) { case 0:
/*     */     case 3:
/* 155 */       tip = Math.toRadians(20.0D);
/* 156 */       base = Math.toRadians(90.0D);
/* 157 */       this.points[2] = ((float)(this.points[6] - length * Math.cos(alpha)));
/* 158 */       this.points[3] = ((float)(this.points[7] - length * Math.sin(alpha)));
/* 159 */       SL = length * Math.sin(base) / Math.sin(base + tip);
/* 160 */       break;
/*     */     case 1:
/* 162 */       tip = Math.toRadians(20.0D);
/* 163 */       base = Math.toRadians(120.0D);
/* 164 */       this.points[2] = ((float)(this.points[6] - length * Math.cos(alpha)));
/* 165 */       this.points[3] = ((float)(this.points[7] - length * Math.sin(alpha)));
/* 166 */       SL = length * Math.sin(base) / Math.sin(base + tip);
/* 167 */       break;
/*     */     case 2:
/* 169 */       tip = Math.toRadians(25.0D);
/* 170 */       this.points[2] = this.points[6];
/* 171 */       this.points[3] = this.points[7];
/* 172 */       SL = length;
/*     */     }
/*     */ 
/* 176 */     this.points[4] = ((float)(this.points[6] - SL * Math.cos(alpha + tip)));
/* 177 */     this.points[5] = ((float)(this.points[7] - SL * Math.sin(alpha + tip)));
/*     */ 
/* 179 */     this.points[8] = ((float)(this.points[6] - SL * Math.cos(alpha - tip)));
/* 180 */     this.points[9] = ((float)(this.points[7] - SL * Math.sin(alpha - tip)));
/*     */   }
/*     */ 
/*     */   private Shape getShape() {
/* 184 */     Shape arrow = getPath();
/* 185 */     BasicStroke stroke = new BasicStroke(getStrokeWidth(), 0, 0);
/* 186 */     Shape outlineShape = stroke.createStrokedShape(arrow);
/* 187 */     Area a1 = new Area(arrow);
/* 188 */     Area a2 = new Area(outlineShape);
/*     */     try { a1.add(a2); } catch (Exception e) {
/* 190 */     }return a1;
/*     */   }
/*     */ 
/*     */   private ShapeRoi getShapeRoi() {
/* 194 */     Shape arrow = getPath();
/* 195 */     BasicStroke stroke = new BasicStroke(getStrokeWidth(), 0, 0);
/* 196 */     ShapeRoi sroi = new ShapeRoi(arrow);
/* 197 */     Shape outlineShape = stroke.createStrokedShape(arrow);
/* 198 */     sroi.or(new ShapeRoi(outlineShape));
/* 199 */     return sroi;
/*     */   }
/*     */ 
/*     */   public ImageProcessor getMask() {
/* 203 */     return getShapeRoi().getMask();
/*     */   }
/*     */ 
/*     */   private double getOutlineWidth() {
/* 207 */     double width = getStrokeWidth() / 8.0D;
/* 208 */     if (width < 1.0D) width = 1.0D;
/* 209 */     double head = this.headSize / 8.0D;
/* 210 */     if (head < 1.0D) head = 1.0D;
/* 211 */     double lineWidth = width * head;
/* 212 */     if (lineWidth < 1.0D) lineWidth = 1.0D;
/* 213 */     return lineWidth;
/*     */   }
/*     */ 
/*     */   public void drawPixels(ImageProcessor ip) {
/* 217 */     ShapeRoi shapeRoi = getShapeRoi();
/* 218 */     ShapeRoi shapeRoi2 = null;
/* 219 */     if (this.doubleHeaded) {
/* 220 */       flipEnds();
/* 221 */       shapeRoi2 = getShapeRoi();
/* 222 */       flipEnds();
/*     */     }
/* 224 */     if (this.outline) {
/* 225 */       int lineWidth = ip.getLineWidth();
/* 226 */       ip.setLineWidth((int)Math.round(getOutlineWidth()));
/* 227 */       shapeRoi.drawPixels(ip);
/* 228 */       if (this.doubleHeaded) shapeRoi2.drawPixels(ip);
/* 229 */       ip.setLineWidth(lineWidth);
/*     */     } else {
/* 231 */       ip.fill(shapeRoi);
/* 232 */       if (this.doubleHeaded) ip.fill(shapeRoi2); 
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean contains(int x, int y)
/*     */   {
/* 237 */     return getShapeRoi().contains(x, y);
/*     */   }
/*     */ 
/*     */   public Rectangle getBounds()
/*     */   {
/* 242 */     return getShapeRoi().getBounds();
/*     */   }
/*     */ 
/*     */   protected void handleMouseDown(int sx, int sy) {
/* 246 */     super.handleMouseDown(sx, sy);
/* 247 */     this.startxd = (this.ic != null ? this.ic.offScreenXD(sx) : sx);
/* 248 */     this.startyd = (this.ic != null ? this.ic.offScreenYD(sy) : sy);
/*     */   }
/*     */ 
/*     */   protected int clipRectMargin() {
/* 252 */     double mag = getMagnification();
/* 253 */     double arrowWidth = getStrokeWidth();
/* 254 */     double size = 8.0D + 10.0D * arrowWidth * mag * 0.5D;
/* 255 */     return (int)Math.max(size * 2.0D, this.headSize);
/*     */   }
/*     */ 
/*     */   public boolean isDrawingTool() {
/* 259 */     return true;
/*     */   }
/*     */ 
/*     */   public static void setDefaultWidth(double width) {
/* 263 */     defaultWidth = (float)width;
/*     */   }
/*     */ 
/*     */   public static double getDefaultWidth() {
/* 267 */     return defaultWidth;
/*     */   }
/*     */ 
/*     */   public void setStyle(int style) {
/* 271 */     this.style = style;
/*     */   }
/*     */ 
/*     */   public int getStyle() {
/* 275 */     return this.style;
/*     */   }
/*     */ 
/*     */   public static void setDefaultStyle(int style) {
/* 279 */     defaultStyle = style;
/*     */   }
/*     */ 
/*     */   public static int getDefaultStyle() {
/* 283 */     return defaultStyle;
/*     */   }
/*     */ 
/*     */   public void setHeadSize(double headSize) {
/* 287 */     this.headSize = headSize;
/*     */   }
/*     */ 
/*     */   public double getHeadSize() {
/* 291 */     return this.headSize;
/*     */   }
/*     */ 
/*     */   public static void setDefaultHeadSize(double size) {
/* 295 */     defaultHeadSize = size;
/*     */   }
/*     */ 
/*     */   public static double getDefaultHeadSize() {
/* 299 */     return defaultHeadSize;
/*     */   }
/*     */ 
/*     */   public void setDoubleHeaded(boolean b) {
/* 303 */     this.doubleHeaded = b;
/*     */   }
/*     */ 
/*     */   public boolean getDoubleHeaded() {
/* 307 */     return this.doubleHeaded;
/*     */   }
/*     */ 
/*     */   public static void setDefaultDoubleHeaded(boolean b) {
/* 311 */     defaultDoubleHeaded = b;
/*     */   }
/*     */ 
/*     */   public static boolean getDefaultDoubleHeaded() {
/* 315 */     return defaultDoubleHeaded;
/*     */   }
/*     */ 
/*     */   public void setOutline(boolean b) {
/* 319 */     this.outline = b;
/*     */   }
/*     */ 
/*     */   public boolean getOutline() {
/* 323 */     return this.outline;
/*     */   }
/*     */ 
/*     */   public static void setDefaultOutline(boolean b) {
/* 327 */     defaultOutline = b;
/*     */   }
/*     */ 
/*     */   public static boolean getDefaultOutline() {
/* 331 */     return defaultOutline;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  31 */     if ((defaultStyle < 0) || (defaultStyle > 3))
/*  32 */       defaultStyle = 0;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.Arrow
 * JD-Core Version:    0.6.2
 */