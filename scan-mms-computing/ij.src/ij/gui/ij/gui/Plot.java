/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class Plot
/*     */ {
/*     */   public static final int CIRCLE = 0;
/*     */   public static final int X = 1;
/*     */   public static final int BOX = 3;
/*     */   public static final int TRIANGLE = 4;
/*     */   public static final int CROSS = 5;
/*     */   public static final int DOT = 6;
/*     */   public static final int LINE = 2;
/*     */   public static final int X_NUMBERS = 1;
/*     */   public static final int Y_NUMBERS = 2;
/*     */   public static final int X_TICKS = 4;
/*     */   public static final int Y_TICKS = 8;
/*     */   public static final int X_GRID = 16;
/*     */   public static final int Y_GRID = 32;
/*     */   public static final int X_FORCE2GRID = 64;
/*     */   public static final int Y_FORCE2GRID = 128;
/*     */   public static final int DEFAULT_FLAGS = 51;
/*     */   public static final int LEFT_MARGIN = 60;
/*     */   public static final int RIGHT_MARGIN = 18;
/*     */   public static final int TOP_MARGIN = 15;
/*     */   public static final int BOTTOM_MARGIN = 40;
/*     */   private static final int WIDTH = 450;
/*     */   private static final int HEIGHT = 200;
/*     */   private static final int MAX_INTERVALS = 12;
/*     */   private static final int MIN_X_GRIDWIDTH = 60;
/*     */   private static final int MIN_Y_GRIDWIDTH = 40;
/*     */   private static final int TICK_LENGTH = 3;
/*  66 */   private final Color gridColor = new Color(12632256);
/*     */   private int frameWidth;
/*     */   private int frameHeight;
/*  70 */   Rectangle frame = null;
/*     */   float[] xValues;
/*     */   float[] yValues;
/*     */   float[] errorBars;
/*     */   int nPoints;
/*     */   double xMin;
/*     */   double xMax;
/*     */   double yMin;
/*     */   double yMax;
/*     */   private double xScale;
/*     */   private double yScale;
/*  77 */   private static String defaultDirectory = null;
/*     */   private String xLabel;
/*     */   private String yLabel;
/*     */   private int flags;
/*  81 */   private Font font = new Font("Helvetica", 0, 12);
/*     */   private boolean fixedYScale;
/*  83 */   private int lineWidth = 1;
/*  84 */   private int markSize = 5;
/*     */   private ImageProcessor ip;
/*     */   private String title;
/*     */   private boolean initialized;
/*     */   private boolean plotDrawn;
/*  89 */   private int plotWidth = PlotWindow.plotWidth;
/*  90 */   private int plotHeight = PlotWindow.plotHeight;
/*     */   private boolean multiplePlots;
/*     */   private boolean drawPending;
/*     */   private int sourceImageID;
/*     */   ArrayList storedData;
/*     */ 
/*     */   public Plot(String title, String xLabel, String yLabel, float[] xValues, float[] yValues, int flags)
/*     */   {
/* 107 */     this.title = title;
/* 108 */     this.xLabel = xLabel;
/* 109 */     this.yLabel = yLabel;
/* 110 */     this.flags = flags;
/* 111 */     this.storedData = new ArrayList();
/* 112 */     if ((xValues == null) || (yValues == null)) {
/* 113 */       xValues = new float[0];
/* 114 */       yValues = new float[0];
/*     */     } else {
/* 116 */       storeData(xValues, yValues);
/* 117 */     }this.xValues = xValues;
/* 118 */     this.yValues = yValues;
/*     */ 
/* 120 */     double[] a = Tools.getMinMax(xValues);
/* 121 */     this.xMin = a[0]; this.xMax = a[1];
/* 122 */     a = Tools.getMinMax(yValues);
/* 123 */     this.yMin = a[0]; this.yMax = a[1];
/* 124 */     this.fixedYScale = false;
/* 125 */     this.nPoints = xValues.length;
/* 126 */     this.drawPending = true;
/*     */   }
/*     */ 
/*     */   public Plot(String title, String xLabel, String yLabel, float[] xValues, float[] yValues)
/*     */   {
/* 131 */     this(title, xLabel, yLabel, xValues, yValues, 51);
/*     */   }
/*     */ 
/*     */   public Plot(String title, String xLabel, String yLabel, double[] xValues, double[] yValues, int flags)
/*     */   {
/* 136 */     this(title, xLabel, yLabel, xValues != null ? Tools.toFloat(xValues) : null, yValues != null ? Tools.toFloat(yValues) : null, flags);
/*     */   }
/*     */ 
/*     */   public Plot(String title, String xLabel, String yLabel, double[] xValues, double[] yValues)
/*     */   {
/* 141 */     this(title, xLabel, yLabel, xValues != null ? Tools.toFloat(xValues) : null, yValues != null ? Tools.toFloat(yValues) : null, 51);
/*     */   }
/*     */ 
/*     */   public void setLimits(double xMin, double xMax, double yMin, double yMax)
/*     */   {
/* 146 */     this.xMin = xMin;
/* 147 */     this.xMax = xMax;
/* 148 */     this.yMin = yMin;
/* 149 */     this.yMax = yMax;
/* 150 */     this.fixedYScale = true;
/* 151 */     if (this.initialized) {
/* 152 */       this.ip.setColor(Color.white);
/* 153 */       this.ip.resetRoi();
/* 154 */       this.ip.fill();
/* 155 */       this.ip.setColor(Color.black);
/* 156 */       setScaleAndDrawAxisLabels();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setSize(int width, int height)
/*     */   {
/* 164 */     if ((!this.initialized) && (width > 98) && (height > 75)) {
/* 165 */       this.plotWidth = (width - 60 - 18);
/* 166 */       this.plotHeight = (height - 15 - 40);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFrameSize(int width, int height)
/*     */   {
/* 172 */     this.plotWidth = width;
/* 173 */     this.plotHeight = height;
/*     */   }
/*     */ 
/*     */   public void addPoints(float[] x, float[] y, int shape)
/*     */   {
/* 182 */     setup();
/* 183 */     switch (shape) { case 0:
/*     */     case 1:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/* 185 */       this.ip.setClipRect(this.frame);
/* 186 */       for (int i = 0; i < x.length; i++) {
/* 187 */         int xt = 60 + (int)((x[i] - this.xMin) * this.xScale);
/* 188 */         int yt = 15 + this.frameHeight - (int)((y[i] - this.yMin) * this.yScale);
/* 189 */         drawShape(shape, xt, yt, this.markSize);
/*     */       }
/* 191 */       this.ip.setClipRect(null);
/* 192 */       break;
/*     */     case 2:
/* 194 */       drawFloatPolyline(this.ip, x, y, x.length);
/*     */     }
/*     */ 
/* 197 */     this.multiplePlots = true;
/* 198 */     if ((this.xValues == null) || (this.xValues.length == 0)) {
/* 199 */       this.xValues = x;
/* 200 */       this.yValues = y;
/* 201 */       this.nPoints = x.length;
/* 202 */       this.drawPending = false;
/*     */     }
/* 204 */     storeData(x, y);
/*     */   }
/*     */ 
/*     */   public void addPoints(double[] x, double[] y, int shape)
/*     */   {
/* 210 */     addPoints(Tools.toFloat(x), Tools.toFloat(y), shape);
/*     */   }
/*     */ 
/*     */   void drawShape(int shape, int x, int y, int size) {
/* 214 */     int xbase = x - size / 2;
/* 215 */     int ybase = y - size / 2;
/* 216 */     switch (shape) {
/*     */     case 1:
/* 218 */       this.ip.drawLine(xbase, ybase, xbase + size, ybase + size);
/* 219 */       this.ip.drawLine(xbase + size, ybase, xbase, ybase + size);
/* 220 */       break;
/*     */     case 3:
/* 222 */       this.ip.drawLine(xbase, ybase, xbase + size, ybase);
/* 223 */       this.ip.drawLine(xbase + size, ybase, xbase + size, ybase + size);
/* 224 */       this.ip.drawLine(xbase + size, ybase + size, xbase, ybase + size);
/* 225 */       this.ip.drawLine(xbase, ybase + size, xbase, ybase);
/* 226 */       break;
/*     */     case 4:
/* 228 */       this.ip.drawLine(x, ybase, xbase + size, ybase + size);
/* 229 */       this.ip.drawLine(xbase + size, ybase + size, xbase, ybase + size);
/* 230 */       this.ip.drawLine(xbase, ybase + size, x, ybase);
/* 231 */       break;
/*     */     case 5:
/* 233 */       this.ip.drawLine(xbase, y, xbase + size, y);
/* 234 */       this.ip.drawLine(x, ybase, x, ybase + size);
/* 235 */       break;
/*     */     case 6:
/* 237 */       this.ip.drawDot(x, y);
/* 238 */       break;
/*     */     case 2:
/*     */     default:
/* 240 */       this.ip.drawLine(x - 1, y - 2, x + 1, y - 2);
/* 241 */       this.ip.drawLine(x - 1, y + 2, x + 1, y + 2);
/* 242 */       this.ip.drawLine(x + 2, y + 1, x + 2, y - 1);
/* 243 */       this.ip.drawLine(x - 2, y + 1, x - 2, y - 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addErrorBars(float[] errorBars)
/*     */   {
/* 250 */     this.errorBars = errorBars;
/*     */   }
/*     */ 
/*     */   public void addErrorBars(double[] errorBars)
/*     */   {
/* 255 */     addErrorBars(Tools.toFloat(errorBars));
/*     */   }
/*     */ 
/*     */   public void addLabel(double x, double y, String label)
/*     */   {
/* 262 */     setup();
/* 263 */     int xt = 60 + (int)(x * this.frameWidth);
/* 264 */     int yt = 15 + (int)(y * this.frameHeight);
/* 265 */     this.ip.drawString(label, xt, yt);
/*     */   }
/*     */ 
/*     */   public void setJustification(int justification)
/*     */   {
/* 284 */     setup();
/* 285 */     this.ip.setJustification(justification);
/*     */   }
/*     */ 
/*     */   public void setColor(Color c)
/*     */   {
/* 292 */     setup();
/* 293 */     if (!(this.ip instanceof ColorProcessor)) {
/* 294 */       this.ip = this.ip.convertToRGB();
/* 295 */       this.ip.setLineWidth(this.lineWidth);
/* 296 */       this.ip.setFont(this.font);
/* 297 */       this.ip.setAntialiasedText(true);
/*     */     }
/* 299 */     this.ip.setColor(c);
/*     */   }
/*     */ 
/*     */   public void setLineWidth(int lineWidth)
/*     */   {
/* 304 */     if (lineWidth < 1) lineWidth = 1;
/* 305 */     setup();
/* 306 */     this.ip.setLineWidth(lineWidth);
/* 307 */     this.lineWidth = lineWidth;
/* 308 */     this.markSize = (lineWidth == 1 ? 5 : 7);
/*     */   }
/*     */ 
/*     */   public void drawLine(double x1, double y1, double x2, double y2)
/*     */   {
/* 313 */     setup();
/* 314 */     int ix1 = 60 + (int)Math.round((x1 - this.xMin) * this.xScale);
/* 315 */     int iy1 = 15 + this.frameHeight - (int)Math.round((y1 - this.yMin) * this.yScale);
/* 316 */     int ix2 = 60 + (int)Math.round((x2 - this.xMin) * this.xScale);
/* 317 */     int iy2 = 15 + this.frameHeight - (int)Math.round((y2 - this.yMin) * this.yScale);
/* 318 */     this.ip.drawLine(ix1, iy1, ix2, iy2);
/*     */   }
/*     */ 
/*     */   public void changeFont(Font font)
/*     */   {
/* 323 */     setup();
/* 324 */     this.ip.setFont(font);
/* 325 */     this.font = font;
/*     */   }
/*     */ 
/*     */   void setup() {
/* 329 */     if (this.initialized)
/* 330 */       return;
/* 331 */     this.initialized = true;
/* 332 */     createImage();
/* 333 */     this.ip.setColor(Color.black);
/* 334 */     if (this.lineWidth > 3)
/* 335 */       this.lineWidth = 3;
/* 336 */     this.ip.setLineWidth(this.lineWidth);
/* 337 */     this.ip.setFont(this.font);
/* 338 */     this.ip.setAntialiasedText(true);
/* 339 */     if (this.frameWidth == 0) {
/* 340 */       this.frameWidth = this.plotWidth;
/* 341 */       this.frameHeight = this.plotHeight;
/*     */     }
/* 343 */     this.frame = new Rectangle(60, 15, this.frameWidth, this.frameHeight);
/* 344 */     setScaleAndDrawAxisLabels();
/*     */   }
/*     */ 
/*     */   void setScaleAndDrawAxisLabels() {
/* 348 */     if (this.xMax - this.xMin == 0.0D)
/* 349 */       this.xScale = 1.0D;
/*     */     else
/* 351 */       this.xScale = (this.frame.width / (this.xMax - this.xMin));
/* 352 */     if (this.yMax - this.yMin == 0.0D)
/* 353 */       this.yScale = 1.0D;
/*     */     else
/* 355 */       this.yScale = (this.frame.height / (this.yMax - this.yMin));
/* 356 */     if (PlotWindow.noGridLines)
/* 357 */       drawAxisLabels();
/*     */     else
/* 359 */       drawTicksEtc();
/*     */   }
/*     */ 
/*     */   void drawAxisLabels() {
/* 363 */     int digits = getDigits(this.yMin, this.yMax);
/* 364 */     String s = IJ.d2s(this.yMax, digits);
/* 365 */     int sw = this.ip.getStringWidth(s);
/* 366 */     if (sw + 4 > 60)
/* 367 */       this.ip.drawString(s, 4, 11);
/*     */     else
/* 369 */       this.ip.drawString(s, 60 - this.ip.getStringWidth(s) - 4, 25);
/* 370 */     s = IJ.d2s(this.yMin, digits);
/* 371 */     sw = this.ip.getStringWidth(s);
/* 372 */     if (sw + 4 > 60)
/* 373 */       this.ip.drawString(s, 4, 15 + this.frame.height);
/*     */     else
/* 375 */       this.ip.drawString(s, 60 - this.ip.getStringWidth(s) - 4, 15 + this.frame.height);
/* 376 */     FontMetrics fm = this.ip.getFontMetrics();
/* 377 */     int x = 60;
/* 378 */     int y = 15 + this.frame.height + fm.getAscent() + 6;
/* 379 */     digits = getDigits(this.xMin, this.xMax);
/* 380 */     this.ip.drawString(IJ.d2s(this.xMin, digits), x, y);
/* 381 */     s = IJ.d2s(this.xMax, digits);
/* 382 */     this.ip.drawString(s, x + this.frame.width - this.ip.getStringWidth(s) + 6, y);
/* 383 */     this.ip.drawString(this.xLabel, 60 + (this.frame.width - this.ip.getStringWidth(this.xLabel)) / 2, y + 3);
/* 384 */     drawYLabel(this.yLabel, 56, 15, this.frame.height, fm);
/*     */   }
/*     */ 
/*     */   void drawTicksEtc() {
/* 388 */     int fontAscent = this.ip.getFontMetrics().getAscent();
/* 389 */     int fontMaxAscent = this.ip.getFontMetrics().getMaxAscent();
/* 390 */     if ((this.flags & 0x15) != 0) {
/* 391 */       double step = Math.abs(Math.max(this.frame.width / 12, 60) / this.xScale);
/* 392 */       step = niceNumber(step);
/*     */       int i1;
/*     */       int i2;
/* 394 */       if ((this.flags & 0x40) != 0) {
/* 395 */         int i1 = (int)Math.floor(Math.min(this.xMin, this.xMax) / step + 1.0E-10D);
/* 396 */         int i2 = (int)Math.ceil(Math.max(this.xMin, this.xMax) / step - 1.0E-10D);
/* 397 */         this.xMin = (i1 * step);
/* 398 */         this.xMax = (i2 * step);
/* 399 */         this.xScale = (this.frame.width / (this.xMax - this.xMin));
/*     */       } else {
/* 401 */         i1 = (int)Math.ceil(Math.min(this.xMin, this.xMax) / step - 1.0E-10D);
/* 402 */         i2 = (int)Math.floor(Math.max(this.xMin, this.xMax) / step + 1.0E-10D);
/*     */       }
/* 404 */       int digits = -(int)Math.floor(Math.log(step) / Math.log(10.0D) + 1.0E-06D);
/* 405 */       if (digits < 0) digits = 0;
/* 406 */       if (digits > 5) digits = -3;
/* 407 */       int y1 = 15;
/* 408 */       int y2 = 15 + this.frame.height;
/* 409 */       int yNumbers = y2 + fontAscent + 7;
/* 410 */       for (int i = 0; i <= i2 - i1; i++) {
/* 411 */         double v = (i + i1) * step;
/* 412 */         int x = (int)Math.round((v - this.xMin) * this.xScale) + 60;
/* 413 */         if ((this.flags & 0x10) != 0) {
/* 414 */           this.ip.setColor(this.gridColor);
/* 415 */           this.ip.drawLine(x, y1, x, y2);
/* 416 */           this.ip.setColor(Color.black);
/*     */         }
/* 418 */         if ((this.flags & 0x4) != 0) {
/* 419 */           this.ip.drawLine(x, y1, x, y1 + 3);
/* 420 */           this.ip.drawLine(x, y2, x, y2 - 3);
/*     */         }
/* 422 */         if ((this.flags & 0x1) != 0) {
/* 423 */           String s = IJ.d2s(v, digits);
/* 424 */           this.ip.drawString(s, x - this.ip.getStringWidth(s) / 2, yNumbers);
/*     */         }
/*     */       }
/*     */     }
/* 428 */     int maxNumWidth = 0;
/* 429 */     if ((this.flags & 0x2A) != 0) {
/* 430 */       double step = Math.abs(Math.max(this.frame.width / 12, 40) / this.yScale);
/* 431 */       step = niceNumber(step);
/*     */       int i1;
/*     */       int i2;
/* 433 */       if ((this.flags & 0x40) != 0) {
/* 434 */         int i1 = (int)Math.floor(Math.min(this.yMin, this.yMax) / step + 1.0E-10D);
/* 435 */         int i2 = (int)Math.ceil(Math.max(this.yMin, this.yMax) / step - 1.0E-10D);
/* 436 */         this.yMin = (i1 * step);
/* 437 */         this.yMax = (i2 * step);
/* 438 */         this.yScale = (this.frame.height / (this.yMax - this.yMin));
/*     */       } else {
/* 440 */         i1 = (int)Math.ceil(Math.min(this.yMin, this.yMax) / step - 1.0E-10D);
/* 441 */         i2 = (int)Math.floor(Math.max(this.yMin, this.yMax) / step + 1.0E-10D);
/*     */       }
/* 443 */       int digits = -(int)Math.floor(Math.log(step) / Math.log(10.0D) + 1.0E-06D);
/* 444 */       if (digits < 0) digits = 0;
/* 445 */       if (digits > 5) digits = -3;
/* 446 */       int x1 = 60;
/* 447 */       int x2 = 60 + this.frame.width;
/* 448 */       for (int i = 0; i <= i2 - i1; i++) {
/* 449 */         double v = (i + i1) * step;
/* 450 */         int y = 15 + this.frame.height - (int)Math.round((v - this.yMin) * this.yScale);
/* 451 */         if ((this.flags & 0x20) != 0) {
/* 452 */           this.ip.setColor(this.gridColor);
/* 453 */           this.ip.drawLine(x1, y, x2, y);
/* 454 */           this.ip.setColor(Color.black);
/*     */         }
/* 456 */         if ((this.flags & 0x8) != 0) {
/* 457 */           this.ip.drawLine(x1, y, x1 + 3, y);
/* 458 */           this.ip.drawLine(x2, y, x2 - 3, y);
/*     */         }
/* 460 */         if ((this.flags & 0x2) != 0) {
/* 461 */           String s = IJ.d2s(v, digits);
/* 462 */           int w = this.ip.getStringWidth(s);
/* 463 */           if (w > maxNumWidth) maxNumWidth = w;
/* 464 */           this.ip.drawString(s, 60 - w - 4, y + fontMaxAscent / 2 + 1);
/*     */         }
/*     */       }
/*     */     }
/* 468 */     if ((this.flags & 0x2) == 0) {
/* 469 */       int digits = getDigits(this.yMin, this.yMax);
/* 470 */       String s = IJ.d2s(this.yMax, digits);
/* 471 */       int sw = this.ip.getStringWidth(s);
/* 472 */       if (sw + 4 > 60)
/* 473 */         this.ip.drawString(s, 4, 11);
/*     */       else
/* 475 */         this.ip.drawString(s, 60 - this.ip.getStringWidth(s) - 4, 25);
/* 476 */       s = IJ.d2s(this.yMin, digits);
/* 477 */       sw = this.ip.getStringWidth(s);
/* 478 */       if (sw + 4 > 60)
/* 479 */         this.ip.drawString(s, 4, 15 + this.frame.height);
/*     */       else
/* 481 */         this.ip.drawString(s, 60 - this.ip.getStringWidth(s) - 4, 15 + this.frame.height);
/*     */     }
/* 483 */     FontMetrics fm = this.ip.getFontMetrics();
/* 484 */     int x = 60;
/* 485 */     int y = 15 + this.frame.height + fm.getAscent() + 6;
/* 486 */     if ((this.flags & 0x1) == 0) {
/* 487 */       int digits = getDigits(this.xMin, this.xMax);
/* 488 */       this.ip.drawString(IJ.d2s(this.xMin, digits), x, y);
/* 489 */       String s = IJ.d2s(this.xMax, digits);
/* 490 */       this.ip.drawString(s, x + this.frame.width - this.ip.getStringWidth(s) + 6, y);
/*     */     } else {
/* 492 */       y += fm.getAscent();
/* 493 */     }this.ip.drawString(this.xLabel, 60 + (this.frame.width - this.ip.getStringWidth(this.xLabel)) / 2, y + 6);
/* 494 */     drawYLabel(this.yLabel, 60 - maxNumWidth - 4, 15, this.frame.height, fm);
/*     */   }
/*     */ 
/*     */   double niceNumber(double v) {
/* 498 */     double base = Math.pow(10.0D, Math.floor(Math.log(v) / Math.log(10.0D) - 1.0E-06D));
/* 499 */     if (v > 5.0000001D * base) return 10.0D * base;
/* 500 */     if (v > 2.0000001D * base) return 5.0D * base;
/* 501 */     return 2.0D * base;
/*     */   }
/*     */ 
/*     */   void createImage() {
/* 505 */     if (this.ip != null) return;
/* 506 */     int width = this.plotWidth + 60 + 18;
/* 507 */     int height = this.plotHeight + 15 + 40;
/* 508 */     byte[] pixels = new byte[width * height];
/* 509 */     for (int i = 0; i < width * height; i++)
/* 510 */       pixels[i] = -1;
/* 511 */     this.ip = new ByteProcessor(width, height, pixels, null);
/*     */   }
/*     */ 
/*     */   int getDigits(double n1, double n2) {
/* 515 */     if ((Math.round(n1) == n1) && (Math.round(n2) == n2)) {
/* 516 */       return 0;
/*     */     }
/* 518 */     n1 = Math.abs(n1);
/* 519 */     n2 = Math.abs(n2);
/* 520 */     double n = (n1 < n2) && (n1 > 0.0D) ? n1 : n2;
/* 521 */     double diff = Math.abs(n2 - n1);
/* 522 */     if ((diff > 0.0D) && (diff < n)) n = diff;
/* 523 */     int digits = 1;
/* 524 */     if (n < 10.0D) digits = 2;
/* 525 */     if (n < 0.01D) digits = 3;
/* 526 */     if (n < 0.001D) digits = 4;
/* 527 */     if (n < 0.0001D) digits = -3;
/* 528 */     return digits;
/*     */   }
/*     */ 
/*     */   public void draw()
/*     */   {
/* 537 */     if (this.plotDrawn)
/* 538 */       return;
/* 539 */     this.plotDrawn = true;
/* 540 */     createImage();
/* 541 */     setup();
/*     */ 
/* 543 */     if (this.drawPending) {
/* 544 */       drawFloatPolyline(this.ip, this.xValues, this.yValues, this.nPoints);
/* 545 */       if (this.errorBars != null) {
/* 546 */         int nPoints2 = this.nPoints;
/* 547 */         if (this.errorBars.length < this.nPoints)
/* 548 */           nPoints2 = this.errorBars.length;
/* 549 */         int[] xpoints = new int[2];
/* 550 */         int[] ypoints = new int[2];
/* 551 */         for (int i = 0; i < nPoints2; i++)
/*     */         {
/*     */           int tmp128_127 = (60 + (int)((this.xValues[i] - this.xMin) * this.xScale)); xpoints[1] = tmp128_127; xpoints[0] = tmp128_127;
/* 553 */           ypoints[0] = (15 + this.frame.height - (int)((this.yValues[i] - this.yMin - this.errorBars[i]) * this.yScale));
/* 554 */           ypoints[1] = (15 + this.frame.height - (int)((this.yValues[i] - this.yMin + this.errorBars[i]) * this.yScale));
/* 555 */           drawPolyline(this.ip, xpoints, ypoints, 2, false);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 560 */     if ((this.ip instanceof ColorProcessor))
/* 561 */       this.ip.setColor(Color.black);
/* 562 */     if (this.lineWidth > 5) this.ip.setLineWidth(5);
/* 563 */     this.ip.drawRect(this.frame.x, this.frame.y, this.frame.width + 1, this.frame.height + 1);
/* 564 */     this.ip.setLineWidth(this.lineWidth);
/*     */   }
/*     */ 
/*     */   void drawPolyline(ImageProcessor ip, int[] x, int[] y, int n, boolean clip) {
/* 568 */     if (clip) ip.setClipRect(this.frame);
/* 569 */     ip.moveTo(x[0], y[0]);
/* 570 */     for (int i = 0; i < n; i++)
/* 571 */       ip.lineTo(x[i], y[i]);
/* 572 */     if (clip) ip.setClipRect(null); 
/*     */   }
/*     */ 
/*     */   void drawFloatPolyline(ImageProcessor ip, float[] x, float[] y, int n)
/*     */   {
/* 576 */     if ((x == null) || (x.length == 0)) return;
/* 577 */     ip.setClipRect(this.frame);
/*     */ 
/* 580 */     int x2 = 60 + (int)((x[0] - this.xMin) * this.xScale);
/* 581 */     int y2 = 15 + this.frame.height - (int)((y[0] - this.yMin) * this.yScale);
/* 582 */     boolean y2IsNaN = Float.isNaN(y[0]);
/* 583 */     for (int i = 1; i < n; i++) {
/* 584 */       int x1 = x2;
/* 585 */       int y1 = y2;
/* 586 */       boolean y1IsNaN = y2IsNaN;
/* 587 */       x2 = 60 + (int)((x[i] - this.xMin) * this.xScale);
/* 588 */       y2 = 15 + this.frame.height - (int)((y[i] - this.yMin) * this.yScale);
/* 589 */       y2IsNaN = Float.isNaN(y[i]);
/* 590 */       if ((!y1IsNaN) && (!y2IsNaN)) {
/* 591 */         ip.drawLine(x1, y1, x2, y2);
/*     */       }
/*     */     }
/* 594 */     ip.setClipRect(null);
/*     */   }
/*     */ 
/*     */   void drawYLabel(String yLabel, int x, int y, int height, FontMetrics fm) {
/* 598 */     if (yLabel.equals(""))
/* 599 */       return;
/* 600 */     int w = fm.stringWidth(yLabel) + 5;
/* 601 */     int h = fm.getHeight() + 5;
/* 602 */     ImageProcessor label = new ByteProcessor(w, h);
/* 603 */     label.setColor(Color.white);
/* 604 */     label.fill();
/* 605 */     label.setColor(Color.black);
/* 606 */     label.setFont(this.font);
/* 607 */     label.setAntialiasedText(true);
/* 608 */     int descent = fm.getDescent();
/* 609 */     label.drawString(yLabel, 0, h - descent);
/* 610 */     label = label.rotateLeft();
/* 611 */     int y2 = y + (height - this.ip.getStringWidth(yLabel)) / 2;
/* 612 */     if (y2 < y) y2 = y;
/* 613 */     int x2 = Math.max(x - h, 0);
/* 614 */     this.ip.insert(label, x2, y2);
/*     */   }
/*     */ 
/*     */   ImageProcessor getBlankProcessor() {
/* 618 */     createImage();
/* 619 */     return this.ip;
/*     */   }
/*     */ 
/*     */   String getCoordinates(int x, int y) {
/* 623 */     String text = "";
/* 624 */     if (!this.frame.contains(x, y))
/* 625 */       return text;
/* 626 */     if ((this.fixedYScale) || (this.multiplePlots)) {
/* 627 */       double xv = (x - 60) / this.xScale + this.xMin;
/* 628 */       double yv = (15 + this.frameHeight - y) / this.yScale + this.yMin;
/* 629 */       text = "X=" + IJ.d2s(xv, getDigits(xv, xv)) + ", Y=" + IJ.d2s(yv, getDigits(yv, yv));
/*     */     } else {
/* 631 */       int index = (int)((x - this.frame.x) / (this.frame.width / this.nPoints));
/* 632 */       if ((index > 0) && (index < this.nPoints)) {
/* 633 */         double xv = this.xValues[index];
/* 634 */         double yv = this.yValues[index];
/* 635 */         text = "X=" + IJ.d2s(xv, getDigits(xv, xv)) + ", Y=" + IJ.d2s(yv, getDigits(yv, yv));
/*     */       }
/*     */     }
/* 638 */     return text;
/*     */   }
/*     */ 
/*     */   public ImageProcessor getProcessor()
/*     */   {
/* 643 */     draw();
/* 644 */     return this.ip;
/*     */   }
/*     */ 
/*     */   public ImagePlus getImagePlus()
/*     */   {
/* 649 */     draw();
/* 650 */     ImagePlus img = new ImagePlus(this.title, this.ip);
/* 651 */     Calibration cal = img.getCalibration();
/* 652 */     cal.xOrigin = (60.0D - this.xMin * this.xScale);
/* 653 */     cal.yOrigin = (15 + this.frameHeight + this.yMin * this.yScale);
/* 654 */     cal.pixelWidth = (1.0D / this.xScale);
/* 655 */     cal.pixelHeight = (1.0D / this.yScale);
/* 656 */     cal.setInvertY(true);
/* 657 */     return img;
/*     */   }
/*     */ 
/*     */   public PlotWindow show()
/*     */   {
/* 662 */     draw();
/* 663 */     if ((Prefs.useInvertingLut) && ((this.ip instanceof ByteProcessor)) && (!Interpreter.isBatchMode()) && (IJ.getInstance() != null)) {
/* 664 */       this.ip.invertLut();
/* 665 */       this.ip.invert();
/*     */     }
/* 667 */     if (((IJ.macroRunning()) && (IJ.getInstance() == null)) || (Interpreter.isBatchMode())) {
/* 668 */       ImagePlus imp = new ImagePlus(this.title, this.ip);
/* 669 */       WindowManager.setTempCurrentImage(imp);
/* 670 */       imp.setProperty("XValues", this.xValues);
/* 671 */       imp.setProperty("YValues", this.yValues);
/* 672 */       Interpreter.addBatchModeImage(imp);
/* 673 */       return null;
/*     */     }
/* 675 */     ImageWindow.centerNextImage();
/* 676 */     return new PlotWindow(this);
/*     */   }
/*     */ 
/*     */   private void storeData(float[] xvalues, float[] yvalues)
/*     */   {
/* 682 */     this.storedData.add(xvalues);
/* 683 */     this.storedData.add(yvalues);
/*     */   }
/*     */ 
/*     */   void setSourceImageID(int id) {
/* 687 */     this.sourceImageID = id;
/*     */   }
/*     */ 
/*     */   int getSourceImageID() {
/* 691 */     return this.sourceImageID;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.Plot
 * JD-Core Version:    0.6.2
 */