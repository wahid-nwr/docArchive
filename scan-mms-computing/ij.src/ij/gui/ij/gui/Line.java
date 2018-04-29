/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.Straightener;
/*     */ import ij.process.FloatPolygon;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class Line extends Roi
/*     */ {
/*     */   public int x1;
/*     */   public int y1;
/*     */   public int x2;
/*     */   public int y2;
/*     */   public double x1d;
/*     */   public double y1d;
/*     */   public double x2d;
/*     */   public double y2d;
/*     */   protected double x1R;
/*     */   protected double y1R;
/*     */   protected double x2R;
/*     */   protected double y2R;
/*     */   private double xHandleOffset;
/*     */   private double yHandleOffset;
/*     */   protected double startxd;
/*     */   protected double startyd;
/*     */   static boolean widthChanged;
/*     */ 
/*     */   public Line(int ox1, int oy1, int ox2, int oy2)
/*     */   {
/*  25 */     this(ox1, oy1, ox2, oy2);
/*     */   }
/*     */ 
/*     */   public Line(double ox1, double oy1, double ox2, double oy2)
/*     */   {
/*  31 */     super((int)ox1, (int)oy1, 0, 0);
/*  32 */     this.type = 5;
/*  33 */     this.x1d = ox1; this.y1d = oy1; this.x2d = ox2; this.y2d = oy2;
/*  34 */     this.x1 = ((int)this.x1d); this.y1 = ((int)this.y1d); this.x2 = ((int)this.x2d); this.y2 = ((int)this.y2d);
/*  35 */     this.x = ((int)Math.min(this.x1d, this.x2d)); this.y = ((int)Math.min(this.y1d, this.y2d));
/*  36 */     this.x1R = (this.x1d - this.x); this.y1R = (this.y1d - this.y); this.x2R = (this.x2d - this.x); this.y2R = (this.y2d - this.y);
/*  37 */     this.width = ((int)Math.abs(this.x2R - this.x1R)); this.height = ((int)Math.abs(this.y2R - this.y1R));
/*  38 */     if ((!(this instanceof Arrow)) && (lineWidth > 1))
/*  39 */       updateWideLine(lineWidth);
/*  40 */     updateClipRect();
/*  41 */     this.oldX = this.x; this.oldY = this.y; this.oldWidth = this.width; this.oldHeight = this.height;
/*  42 */     this.state = 3;
/*     */   }
/*     */ 
/*     */   public Line(int sx, int sy, ImagePlus imp)
/*     */   {
/*  50 */     super(sx, sy, imp);
/*  51 */     this.startxd = this.ic.offScreenXD(sx);
/*  52 */     this.startyd = this.ic.offScreenYD(sy);
/*  53 */     this.x1R = (this.x2R = this.startxd - this.startX);
/*  54 */     this.y1R = (this.y2R = this.startyd - this.startY);
/*  55 */     this.type = 5;
/*  56 */     if ((!(this instanceof Arrow)) && (lineWidth > 1))
/*  57 */       updateWideLine(lineWidth);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public Line(int ox1, int oy1, int ox2, int oy2, ImagePlus imp)
/*     */   {
/*  65 */     this(ox1, oy1, ox2, oy2);
/*  66 */     setImage(imp);
/*     */   }
/*     */ 
/*     */   protected void grow(int sx, int sy) {
/*  70 */     double xend = this.ic != null ? this.ic.offScreenXD(sx) : sx;
/*  71 */     double yend = this.ic != null ? this.ic.offScreenYD(sy) : sy;
/*  72 */     if (xend < 0.0D) xend = 0.0D; if (yend < 0.0D) yend = 0.0D;
/*  73 */     if (xend > this.xMax) xend = this.xMax; if (yend > this.yMax) yend = this.yMax;
/*  74 */     double xstart = this.x + this.x1R; double ystart = this.y + this.y1R;
/*  75 */     if (this.constrain) {
/*  76 */       double dx = Math.abs(xend - xstart);
/*  77 */       double dy = Math.abs(yend - ystart);
/*  78 */       if (dx >= dy)
/*  79 */         yend = ystart;
/*     */       else
/*  81 */         xend = xstart;
/*     */     }
/*  83 */     this.x = ((int)Math.min(this.x + this.x1R, xend)); this.y = ((int)Math.min(this.y + this.y1R, yend));
/*  84 */     this.x1R = (xstart - this.x); this.y1R = (ystart - this.y);
/*  85 */     this.x2R = (xend - this.x); this.y2R = (yend - this.y);
/*  86 */     if (IJ.controlKeyDown()) {
/*  87 */       this.x1R = ((int)Math.round(this.x1R)); this.y1R = ((int)Math.round(this.y1R));
/*  88 */       this.x2R = ((int)Math.round(this.x2R)); this.y2R = ((int)Math.round(this.y2R));
/*     */     }
/*  90 */     this.width = ((int)Math.abs(this.x2R - this.x1R)); this.height = ((int)Math.abs(this.y2R - this.y1R));
/*  91 */     if (this.width < 1) this.width = 1; if (this.height < 1) this.height = 1;
/*  92 */     updateClipRect();
/*  93 */     this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/*  94 */     this.oldX = this.x; this.oldY = this.y;
/*  95 */     this.oldWidth = this.width; this.oldHeight = this.height;
/*     */   }
/*     */ 
/*     */   void move(int sx, int sy) {
/*  99 */     int xNew = this.ic.offScreenX(sx);
/* 100 */     int yNew = this.ic.offScreenY(sy);
/* 101 */     this.x = ((int)(this.x + (xNew - this.startxd)));
/* 102 */     this.y = ((int)(this.y + (yNew - this.startyd)));
/* 103 */     this.clipboard = null;
/* 104 */     this.startxd = xNew;
/* 105 */     this.startyd = yNew;
/* 106 */     updateClipRect();
/* 107 */     this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/* 108 */     this.oldX = this.x;
/* 109 */     this.oldY = this.y;
/* 110 */     this.oldWidth = this.width;
/* 111 */     this.oldHeight = this.height;
/*     */   }
/*     */ 
/*     */   protected void moveHandle(int sx, int sy) {
/* 115 */     double ox = this.ic.offScreenXD(sx);
/* 116 */     double oy = this.ic.offScreenYD(sy);
/* 117 */     this.x1d = (this.x + this.x1R); this.y1d = (this.y + this.y1R); this.x2d = (this.x + this.x2R); this.y2d = (this.y + this.y2R);
/* 118 */     double length = Math.sqrt((this.x2d - this.x1d) * (this.x2d - this.x1d) + (this.y2d - this.y1d) * (this.y2d - this.y1d));
/*     */     double dx;
/*     */     double dy;
/* 119 */     switch (this.activeHandle) {
/*     */     case 0:
/* 121 */       dx = ox - this.x1d;
/* 122 */       dy = oy - this.y1d;
/* 123 */       this.x1d = ox;
/* 124 */       this.y1d = oy;
/* 125 */       if (this.center) {
/* 126 */         this.x2d -= dx;
/* 127 */         this.y2d -= dy;
/*     */       }
/* 129 */       if (this.aspect) {
/* 130 */         double ratio = length / Math.sqrt((this.x2d - this.x1d) * (this.x2d - this.x1d) + (this.y2d - this.y1d) * (this.y2d - this.y1d));
/* 131 */         double xcd = this.x1d + (this.x2d - this.x1d) / 2.0D;
/* 132 */         double ycd = this.y1d + (this.y2d - this.y1d) / 2.0D;
/*     */ 
/* 134 */         if (this.center) {
/* 135 */           this.x1d = (xcd - ratio * (xcd - this.x1d));
/* 136 */           this.x2d = (xcd + ratio * (this.x2d - xcd));
/* 137 */           this.y1d = (ycd - ratio * (ycd - this.y1d));
/* 138 */           this.y2d = (ycd + ratio * (this.y2d - ycd));
/*     */         } else {
/* 140 */           this.x1d = (this.x2d - ratio * (this.x2d - this.x1d));
/* 141 */           this.y1d = (this.y2d - ratio * (this.y2d - this.y1d));
/*     */         }
/*     */       }
/* 144 */       break;
/*     */     case 1:
/* 147 */       dx = ox - this.x2d;
/* 148 */       dy = oy - this.y2d;
/* 149 */       this.x2d = ox;
/* 150 */       this.y2d = oy;
/* 151 */       if (this.center) {
/* 152 */         this.x1d -= dx;
/* 153 */         this.y1d -= dy;
/*     */       }
/* 155 */       if (this.aspect) {
/* 156 */         double ratio = length / Math.sqrt((this.x2d - this.x1d) * (this.x2d - this.x1d) + (this.y2d - this.y1d) * (this.y2d - this.y1d));
/* 157 */         double xcd = this.x1d + (this.x2d - this.x1d) / 2.0D;
/* 158 */         double ycd = this.y1d + (this.y2d - this.y1d) / 2.0D;
/*     */ 
/* 160 */         if (this.center) {
/* 161 */           this.x1d = (xcd - ratio * (xcd - this.x1d));
/* 162 */           this.x2d = (xcd + ratio * (this.x2d - xcd));
/* 163 */           this.y1d = (ycd - ratio * (ycd - this.y1d));
/* 164 */           this.y2d = (ycd + ratio * (this.y2d - ycd));
/*     */         } else {
/* 166 */           this.x2d = (this.x1d + ratio * (this.x2d - this.x1d));
/* 167 */           this.y2d = (this.y1d + ratio * (this.y2d - this.y1d));
/*     */         }
/*     */       }
/* 170 */       break;
/*     */     case 2:
/* 173 */       dx = ox - (this.x1d + (this.x2d - this.x1d) / 2.0D);
/* 174 */       dy = oy - (this.y1d + (this.y2d - this.y1d) / 2.0D);
/* 175 */       this.x1d += dx; this.y1d += dy; this.x2d += dx; this.y2d += dy;
/* 176 */       if (getStrokeWidth() > 1.0F) {
/* 177 */         this.x1d += this.xHandleOffset; this.y1d += this.yHandleOffset;
/* 178 */         this.x2d += this.xHandleOffset; this.y2d += this.yHandleOffset;
/*     */       }
/*     */       break;
/*     */     }
/* 182 */     if (this.constrain) {
/* 183 */       double dx = Math.abs(this.x1d - this.x2d);
/* 184 */       double dy = Math.abs(this.y1d - this.y2d);
/* 185 */       double xcd = Math.min(this.x1d, this.x2d) + dx / 2.0D;
/* 186 */       double ycd = Math.min(this.y1d, this.y2d) + dy / 2.0D;
/*     */ 
/* 189 */       if (this.activeHandle == 0) {
/* 190 */         if (dx >= dy) {
/* 191 */           if (this.aspect) {
/* 192 */             if (this.x2d > this.x1d) this.x1d = (this.x2d - length); else
/* 193 */               this.x1d = (this.x2d + length);
/*     */           }
/* 195 */           this.y1d = this.y2d;
/* 196 */           if (this.center) {
/* 197 */             this.y1d = (this.y2d = ycd);
/* 198 */             if (this.aspect)
/* 199 */               if (xcd > this.x1d) {
/* 200 */                 this.x1d = (xcd - length / 2.0D);
/* 201 */                 this.x2d = (xcd + length / 2.0D);
/*     */               }
/*     */               else {
/* 204 */                 this.x1d = (xcd + length / 2.0D);
/* 205 */                 this.x2d = (xcd - length / 2.0D);
/*     */               }
/*     */           }
/*     */         }
/*     */         else {
/* 210 */           if (this.aspect) {
/* 211 */             if (this.y2d > this.y1d) this.y1d = (this.y2d - length); else
/* 212 */               this.y1d = (this.y2d + length);
/*     */           }
/* 214 */           this.x1d = this.x2d;
/* 215 */           if (this.center) {
/* 216 */             this.x1d = (this.x2d = xcd);
/* 217 */             if (this.aspect)
/* 218 */               if (ycd > this.y1d) {
/* 219 */                 this.y1d = (ycd - length / 2.0D);
/* 220 */                 this.y2d = (ycd + length / 2.0D);
/*     */               }
/*     */               else {
/* 223 */                 this.y1d = (ycd + length / 2.0D);
/* 224 */                 this.y2d = (ycd - length / 2.0D);
/*     */               }
/*     */           }
/*     */         }
/*     */       }
/* 229 */       else if (this.activeHandle == 1) {
/* 230 */         if (dx >= dy) {
/* 231 */           if (this.aspect) {
/* 232 */             if (this.x1d > this.x2d) this.x2d = (this.x1d - length); else
/* 233 */               this.x2d = (this.x1d + length);
/*     */           }
/* 235 */           this.y2d = this.y1d;
/* 236 */           if (this.center) {
/* 237 */             this.y1d = (this.y2d = ycd);
/* 238 */             if (this.aspect)
/* 239 */               if (xcd > this.x1d) {
/* 240 */                 this.x1d = (xcd - length / 2.0D);
/* 241 */                 this.x2d = (xcd + length / 2.0D);
/*     */               }
/*     */               else {
/* 244 */                 this.x1d = (xcd + length / 2.0D);
/* 245 */                 this.x2d = (xcd - length / 2.0D);
/*     */               }
/*     */           }
/*     */         }
/*     */         else {
/* 250 */           if (this.aspect) {
/* 251 */             if (this.y1d > this.y2d) this.y2d = (this.y1d - length); else
/* 252 */               this.y2d = (this.y1d + length);
/*     */           }
/* 254 */           this.x2d = this.x1d;
/* 255 */           if (this.center) {
/* 256 */             this.x1d = (this.x2d = xcd);
/* 257 */             if (this.aspect) {
/* 258 */               if (ycd > this.y1d) {
/* 259 */                 this.y1d = (ycd - length / 2.0D);
/* 260 */                 this.y2d = (ycd + length / 2.0D);
/*     */               }
/*     */               else {
/* 263 */                 this.y1d = (ycd + length / 2.0D);
/* 264 */                 this.y2d = (ycd - length / 2.0D);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 271 */     this.x = ((int)Math.min(this.x1d, this.x2d)); this.y = ((int)Math.min(this.y1d, this.y2d));
/* 272 */     this.x1R = (this.x1d - this.x); this.y1R = (this.y1d - this.y);
/* 273 */     this.x2R = (this.x2d - this.x); this.y2R = (this.y2d - this.y);
/* 274 */     this.width = ((int)Math.abs(this.x2R - this.x1R)); this.height = ((int)Math.abs(this.y2R - this.y1R));
/* 275 */     updateClipRect();
/* 276 */     this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/* 277 */     this.oldX = this.x;
/* 278 */     this.oldY = this.y;
/* 279 */     this.oldWidth = this.width;
/* 280 */     this.oldHeight = this.height;
/*     */   }
/*     */ 
/*     */   protected void mouseDownInHandle(int handle, int sx, int sy) {
/* 284 */     this.state = 4;
/* 285 */     this.activeHandle = handle;
/* 286 */     if (getStrokeWidth() <= 3.0F)
/* 287 */       this.ic.setCursor(new Cursor(1));
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g)
/*     */   {
/* 292 */     Color color = this.strokeColor != null ? this.strokeColor : ROIColor;
/* 293 */     g.setColor(color);
/* 294 */     this.x1d = (this.x + this.x1R); this.y1d = (this.y + this.y1R); this.x2d = (this.x + this.x2R); this.y2d = (this.y + this.y2R);
/* 295 */     this.x1 = ((int)this.x1d); this.y1 = ((int)this.y1d); this.x2 = ((int)this.x2d); this.y2 = ((int)this.y2d);
/* 296 */     int sx1 = screenXD(this.x1d);
/* 297 */     int sy1 = screenYD(this.y1d);
/* 298 */     int sx2 = screenXD(this.x2d);
/* 299 */     int sy2 = screenYD(this.y2d);
/* 300 */     int sx3 = sx1 + (sx2 - sx1) / 2;
/* 301 */     int sy3 = sy1 + (sy2 - sy1) / 2;
/* 302 */     Graphics2D g2d = (Graphics2D)g;
/* 303 */     if (this.stroke != null)
/* 304 */       g2d.setStroke(getScaledStroke());
/* 305 */     g.drawLine(sx1, sy1, sx2, sy2);
/* 306 */     if ((this.wideLine) && (!this.overlay)) {
/* 307 */       g2d.setStroke(onePixelWide);
/* 308 */       g.setColor(getColor());
/* 309 */       g.drawLine(sx1, sy1, sx2, sy2);
/*     */     }
/* 311 */     if ((this.state != 0) && (!this.overlay)) {
/* 312 */       int size2 = 2;
/* 313 */       this.handleColor = (this.strokeColor != null ? this.strokeColor : ROIColor);
/* 314 */       drawHandle(g, sx1 - size2, sy1 - size2);
/* 315 */       this.handleColor = Color.white;
/* 316 */       drawHandle(g, sx2 - size2, sy2 - size2);
/* 317 */       drawHandle(g, sx3 - size2, sy3 - size2);
/*     */     }
/* 319 */     if (this.state != 3)
/* 320 */       IJ.showStatus(this.imp.getLocationAsString(this.x2, this.y2) + ", angle=" + IJ.d2s(getAngle(this.x1, this.y1, this.x2, this.y2)) + ", length=" + IJ.d2s(getLength()));
/* 321 */     if (this.updateFullWindow) {
/* 322 */       this.updateFullWindow = false; this.imp.draw();
/*     */     }
/*     */   }
/*     */ 
/*     */   public double getLength() {
/* 327 */     if ((this.imp == null) || (IJ.altKeyDown())) {
/* 328 */       return getRawLength();
/*     */     }
/* 330 */     Calibration cal = this.imp.getCalibration();
/* 331 */     return Math.sqrt((this.x2d - this.x1d) * cal.pixelWidth * (this.x2d - this.x1d) * cal.pixelWidth + (this.y2d - this.y1d) * cal.pixelHeight * (this.y2d - this.y1d) * cal.pixelHeight);
/*     */   }
/*     */ 
/*     */   public double getRawLength()
/*     */   {
/* 338 */     return Math.sqrt((this.x2d - this.x1d) * (this.x2d - this.x1d) + (this.y2d - this.y1d) * (this.y2d - this.y1d));
/*     */   }
/*     */ 
/*     */   public double[] getPixels()
/*     */   {
/*     */     double[] profile;
/*     */     double[] profile;
/* 344 */     if (getStrokeWidth() == 1.0F) {
/* 345 */       ImageProcessor ip = this.imp.getProcessor();
/* 346 */       profile = ip.getLine(this.x1d, this.y1d, this.x2d, this.y2d);
/*     */     } else {
/* 348 */       ImageProcessor ip2 = new Straightener().rotateLine(this.imp, (int)getStrokeWidth());
/* 349 */       if (ip2 == null) return null;
/* 350 */       int width = ip2.getWidth();
/* 351 */       int height = ip2.getHeight();
/* 352 */       profile = new double[width];
/*     */ 
/* 354 */       ip2.setInterpolate(false);
/* 355 */       for (int y = 0; y < height; y++) {
/* 356 */         double[] aLine = ip2.getLine(0.0D, y, width - 1, y);
/* 357 */         for (int i = 0; i < width; i++)
/* 358 */           profile[i] += aLine[i];
/*     */       }
/* 360 */       for (int i = 0; i < width; i++)
/* 361 */         profile[i] /= height;
/*     */     }
/* 363 */     return profile;
/*     */   }
/*     */ 
/*     */   public Polygon getPolygon() {
/* 367 */     FloatPolygon p = getFloatPolygon();
/* 368 */     return new Polygon(toInt(p.xpoints), toInt(p.ypoints), p.npoints);
/*     */   }
/*     */ 
/*     */   public FloatPolygon getFloatPolygon() {
/* 372 */     FloatPolygon p = new FloatPolygon();
/* 373 */     if (getStrokeWidth() == 1.0F) {
/* 374 */       p.addPoint((float)this.x1d, (float)this.y1d);
/* 375 */       p.addPoint((float)this.x2d, (float)this.y2d);
/*     */     } else {
/* 377 */       double angle = Math.atan2(this.y1 - this.y2, this.x2 - this.x1);
/* 378 */       double width2 = getStrokeWidth() / 2.0D;
/* 379 */       double p1x = this.x1 + Math.cos(angle + 1.570796326794897D) * width2;
/* 380 */       double p1y = this.y1 - Math.sin(angle + 1.570796326794897D) * width2;
/* 381 */       double p2x = this.x1 + Math.cos(angle - 1.570796326794897D) * width2;
/* 382 */       double p2y = this.y1 - Math.sin(angle - 1.570796326794897D) * width2;
/* 383 */       double p3x = this.x2 + Math.cos(angle - 1.570796326794897D) * width2;
/* 384 */       double p3y = this.y2 - Math.sin(angle - 1.570796326794897D) * width2;
/* 385 */       double p4x = this.x2 + Math.cos(angle + 1.570796326794897D) * width2;
/* 386 */       double p4y = this.y2 - Math.sin(angle + 1.570796326794897D) * width2;
/* 387 */       p.addPoint((float)p1x, (float)p1y);
/* 388 */       p.addPoint((float)p2x, (float)p2y);
/* 389 */       p.addPoint((float)p3x, (float)p3y);
/* 390 */       p.addPoint((float)p4x, (float)p4y);
/*     */     }
/* 392 */     return p;
/*     */   }
/*     */ 
/*     */   public void drawPixels(ImageProcessor ip) {
/* 396 */     ip.setLineWidth(1);
/* 397 */     if (getStrokeWidth() == 1.0F) {
/* 398 */       ip.moveTo(this.x1, this.y1);
/* 399 */       ip.lineTo(this.x2, this.y2);
/*     */     } else {
/* 401 */       ip.drawPolygon(getPolygon());
/* 402 */       this.updateFullWindow = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean contains(int x, int y) {
/* 407 */     if (getStrokeWidth() > 1.0F) {
/* 408 */       if (((x == this.x1) && (y == this.y1)) || ((x == this.x2) && (y == this.y2))) {
/* 409 */         return true;
/*     */       }
/* 411 */       return getPolygon().contains(x, y);
/*     */     }
/* 413 */     return false;
/*     */   }
/*     */ 
/*     */   public int isHandle(int sx, int sy)
/*     */   {
/* 419 */     int size = 10;
/* 420 */     if (getStrokeWidth() > 1.0F) size += (int)Math.log(getStrokeWidth());
/* 421 */     int halfSize = size / 2;
/* 422 */     int sx1 = this.ic.screenXD(this.x + this.x1R) - halfSize;
/* 423 */     int sy1 = this.ic.screenYD(this.y + this.y1R) - halfSize;
/* 424 */     int sx2 = this.ic.screenXD(this.x + this.x2R) - halfSize;
/* 425 */     int sy2 = this.ic.screenYD(this.y + this.y2R) - halfSize;
/* 426 */     int sx3 = sx1 + (sx2 - sx1) / 2 - 1;
/* 427 */     int sy3 = sy1 + (sy2 - sy1) / 2 - 1;
/* 428 */     if ((sx >= sx1) && (sx <= sx1 + size) && (sy >= sy1) && (sy <= sy1 + size)) return 0;
/* 429 */     if ((sx >= sx2) && (sx <= sx2 + size) && (sy >= sy2) && (sy <= sy2 + size)) return 1;
/* 430 */     if ((sx >= sx3) && (sx <= sx3 + size + 2) && (sy >= sy3) && (sy <= sy3 + size + 2)) return 2;
/* 431 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int getWidth() {
/* 435 */     return lineWidth;
/*     */   }
/*     */ 
/*     */   public static void setWidth(int w) {
/* 439 */     if (w < 1) w = 1;
/* 440 */     int max = 500;
/* 441 */     if (w > max) {
/* 442 */       ImagePlus imp2 = WindowManager.getCurrentImage();
/* 443 */       if (imp2 != null) {
/* 444 */         max = Math.max(max, imp2.getWidth());
/* 445 */         max = Math.max(max, imp2.getHeight());
/*     */       }
/* 447 */       if (w > max) w = max;
/*     */     }
/* 449 */     lineWidth = w;
/* 450 */     widthChanged = true;
/*     */   }
/*     */ 
/*     */   public void setStrokeWidth(float width) {
/* 454 */     super.setStrokeWidth(width);
/* 455 */     if (getStrokeColor() == Roi.getColor())
/* 456 */       this.wideLine = true;
/*     */   }
/*     */ 
/*     */   public Rectangle getBounds()
/*     */   {
/* 461 */     int xmin = (int)Math.round(Math.min(this.x1d, this.x2d));
/* 462 */     int ymin = (int)Math.round(Math.min(this.y1d, this.y2d));
/* 463 */     int w = (int)Math.round(Math.abs(this.x2d - this.x1d));
/* 464 */     int h = (int)Math.round(Math.abs(this.y2d - this.y1d));
/* 465 */     return new Rectangle(xmin, ymin, w, h);
/*     */   }
/*     */ 
/*     */   protected int clipRectMargin() {
/* 469 */     return 4;
/*     */   }
/*     */ 
/*     */   public void nudgeCorner(int key)
/*     */   {
/* 474 */     if (this.ic == null) return;
/* 475 */     double inc = 1.0D / this.ic.getMagnification();
/* 476 */     switch (key) { case 38:
/* 477 */       this.y2R -= inc; break;
/*     */     case 40:
/* 478 */       this.y2R += inc; break;
/*     */     case 37:
/* 479 */       this.x2R -= inc; break;
/*     */     case 39:
/* 480 */       this.x2R += inc;
/*     */     }
/* 482 */     grow(this.ic.screenXD(this.x + this.x2R), this.ic.screenYD(this.y + this.y2R));
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.Line
 * JD-Core Version:    0.6.2
 */