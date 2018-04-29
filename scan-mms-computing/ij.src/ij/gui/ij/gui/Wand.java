/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Polygon;
/*     */ 
/*     */ public class Wand
/*     */ {
/*     */   public static final int FOUR_CONNECTED = 4;
/*     */   public static final int EIGHT_CONNECTED = 8;
/*     */   public static final int LEGACY_MODE = 1;
/*     */   public int npoints;
/*  29 */   private int maxPoints = 1000;
/*     */ 
/*  32 */   public int[] xpoints = new int[this.maxPoints];
/*     */ 
/*  35 */   public int[] ypoints = new int[this.maxPoints];
/*     */   private static final int THRESHOLDED_MODE = 256;
/*     */   private ImageProcessor ip;
/*     */   private byte[] bpixels;
/*     */   private int[] cpixels;
/*     */   private short[] spixels;
/*     */   private float[] fpixels;
/*     */   private int width;
/*     */   private int height;
/*     */   private float lowerThreshold;
/*     */   private float upperThreshold;
/*     */   private int xmin;
/*     */   private boolean exactPixelValue;
/*     */   private static boolean allPoints;
/*     */ 
/*     */   public Wand(ImageProcessor ip)
/*     */   {
/*  52 */     this.ip = ip;
/*  53 */     Object pixels = ip.getPixels();
/*  54 */     if ((pixels instanceof byte[]))
/*  55 */       this.bpixels = ((byte[])pixels);
/*  56 */     else if ((pixels instanceof int[]))
/*  57 */       this.cpixels = ((int[])pixels);
/*  58 */     else if ((pixels instanceof short[]))
/*  59 */       this.spixels = ((short[])pixels);
/*  60 */     else if ((pixels instanceof float[]))
/*  61 */       this.fpixels = ((float[])pixels);
/*  62 */     this.width = ip.getWidth();
/*  63 */     this.height = ip.getHeight();
/*     */   }
/*     */ 
/*     */   public void autoOutline(int startX, int startY, double lower, double upper, int mode)
/*     */   {
/*  77 */     this.lowerThreshold = ((float)lower);
/*  78 */     this.upperThreshold = ((float)upper);
/*  79 */     autoOutline(startX, startY, 0.0D, mode | 0x100);
/*     */   }
/*     */ 
/*     */   public void autoOutline(int startX, int startY, double lower, double upper)
/*     */   {
/*  89 */     autoOutline(startX, startY, lower, upper, 257);
/*     */   }
/*     */ 
/*     */   public void autoOutline(int startX, int startY, int lower, int upper)
/*     */   {
/*  94 */     autoOutline(startX, startY, lower, upper, 257);
/*     */   }
/*     */ 
/*     */   public void autoOutline(int startX, int startY)
/*     */   {
/* 105 */     autoOutline(startX, startY, 0.0D, 1);
/*     */   }
/*     */ 
/*     */   public void autoOutline(int startX, int startY, double tolerance, int mode)
/*     */   {
/* 119 */     if ((startX < 0) || (startX >= this.width) || (startY < 0) || (startY >= this.height)) return;
/* 120 */     if ((this.fpixels != null) && (Float.isNaN(getPixel(startX, startY)))) return;
/* 121 */     this.exactPixelValue = (tolerance == 0.0D);
/* 122 */     boolean thresholdMode = (mode & 0x100) != 0;
/* 123 */     boolean legacyMode = ((mode & 0x1) != 0) && (tolerance == 0.0D);
/* 124 */     if (!thresholdMode) {
/* 125 */       double startValue = getPixel(startX, startY);
/* 126 */       this.lowerThreshold = ((float)(startValue - tolerance));
/* 127 */       this.upperThreshold = ((float)(startValue + tolerance));
/*     */     }
/* 129 */     int x = startX;
/* 130 */     int y = startY;
/*     */     int seedX;
/* 132 */     if (inside(x, y)) {
/* 133 */       int seedX = x;
/*     */       do x++; while (inside(x, y));
/*     */     } else {
/*     */       do {
/* 137 */         x++;
/* 138 */         if (x >= this.width) return; 
/*     */       }
/* 139 */       while (!inside(x, y));
/* 140 */       seedX = x;
/*     */     }
/*     */     boolean fourConnected;
/*     */     boolean fourConnected;
/* 143 */     if (legacyMode)
/* 144 */       fourConnected = (!thresholdMode) && (!isLine(x, y));
/*     */     else {
/* 146 */       fourConnected = (mode & 0x4) != 0;
/*     */     }
/* 148 */     boolean first = true;
/*     */     while (true) {
/* 150 */       boolean insideSelected = traceEdge(x, y, fourConnected);
/* 151 */       if (legacyMode) return;
/* 152 */       if (insideSelected) {
/* 153 */         if (first) return;
/* 154 */         if (this.xmin <= seedX) {
/* 155 */           Polygon poly = new Polygon(this.xpoints, this.ypoints, this.npoints);
/* 156 */           if (poly.contains(seedX, startY))
/* 157 */             return;
/*     */         }
/*     */       }
/* 160 */       first = false;
/*     */ 
/* 162 */       if (!inside(x, y)) do {
/* 163 */           x++;
/* 164 */           if (x > this.width) throw new RuntimeException("Wand Malfunction"); 
/*     */         }
/* 165 */         while (!inside(x, y)); do
/* 166 */         x++; while (inside(x, y));
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean traceEdge(int startX, int startY, boolean fourConnected)
/*     */   {
/* 223 */     this.npoints = 0;
/* 224 */     this.xmin = this.width;
/*     */     int startDirection;
/*     */     int startDirection;
/* 226 */     if (inside(startX, startY)) {
/* 227 */       startDirection = 1;
/*     */     } else {
/* 229 */       startDirection = 3;
/* 230 */       startY++;
/*     */     }
/* 232 */     int x = startX;
/* 233 */     int y = startY;
/* 234 */     int direction = startDirection;
/*     */     do
/*     */     {
/*     */       int newDirection;
/* 237 */       if (fourConnected) {
/* 238 */         int newDirection = direction;
/*     */         do {
/* 240 */           if (!inside(x, y, newDirection)) break;
/* 241 */           newDirection++;
/* 242 */         }while (newDirection < direction + 2);
/* 243 */         newDirection--;
/*     */       } else {
/* 245 */         newDirection = direction + 1;
/*     */         do {
/* 247 */           if (inside(x, y, newDirection)) break;
/* 248 */           newDirection--;
/* 249 */         }while (newDirection >= direction);
/*     */       }
/* 251 */       if ((allPoints) || (newDirection != direction))
/* 252 */         addPoint(x, y);
/* 253 */       switch (newDirection & 0x3) { case 0:
/* 254 */         x++; break;
/*     */       case 1:
/* 255 */         y--; break;
/*     */       case 2:
/* 256 */         x--; break;
/*     */       case 3:
/* 257 */         y++;
/*     */       }
/* 259 */       direction = newDirection;
/* 260 */     }while ((x != startX) || (y != startY) || ((direction & 0x3) != startDirection));
/* 261 */     if ((allPoints) || (this.xpoints[0] != x))
/* 262 */       addPoint(x, y);
/* 263 */     return direction <= 0;
/*     */   }
/*     */ 
/*     */   private void addPoint(int x, int y)
/*     */   {
/* 268 */     if (this.npoints == this.maxPoints) {
/* 269 */       int[] xtemp = new int[this.maxPoints * 2];
/* 270 */       int[] ytemp = new int[this.maxPoints * 2];
/* 271 */       System.arraycopy(this.xpoints, 0, xtemp, 0, this.maxPoints);
/* 272 */       System.arraycopy(this.ypoints, 0, ytemp, 0, this.maxPoints);
/* 273 */       this.xpoints = xtemp;
/* 274 */       this.ypoints = ytemp;
/* 275 */       this.maxPoints *= 2;
/*     */     }
/* 277 */     this.xpoints[this.npoints] = x;
/* 278 */     this.ypoints[this.npoints] = y;
/* 279 */     this.npoints += 1;
/* 280 */     if (this.xmin > x) this.xmin = x;
/*     */   }
/*     */ 
/*     */   private boolean inside(int x, int y)
/*     */   {
/* 285 */     if ((x < 0) || (x >= this.width) || (y < 0) || (y >= this.height))
/* 286 */       return false;
/* 287 */     float value = getPixel(x, y);
/* 288 */     return (value >= this.lowerThreshold) && (value <= this.upperThreshold);
/*     */   }
/*     */ 
/*     */   private boolean inside(int x, int y, int direction)
/*     */   {
/* 293 */     switch (direction & 0x3) { case 0:
/* 294 */       return inside(x, y);
/*     */     case 1:
/* 295 */       return inside(x, y - 1);
/*     */     case 2:
/* 296 */       return inside(x - 1, y - 1);
/*     */     case 3:
/* 297 */       return inside(x - 1, y);
/*     */     }
/* 299 */     return false;
/*     */   }
/*     */ 
/*     */   private float getPixel(int x, int y)
/*     */   {
/* 304 */     if ((x < 0) || (x >= this.width) || (y < 0) || (y >= this.height))
/* 305 */       return (0.0F / 0.0F);
/* 306 */     if (this.bpixels != null)
/* 307 */       return this.bpixels[(y * this.width + x)] & 0xFF;
/* 308 */     if (this.spixels != null)
/* 309 */       return this.spixels[(y * this.width + x)] & 0xFFFF;
/* 310 */     if (this.fpixels != null)
/* 311 */       return this.fpixels[(y * this.width + x)];
/* 312 */     if (this.exactPixelValue) {
/* 313 */       return this.cpixels[(y * this.width + x)] & 0xFFFFFF;
/*     */     }
/* 315 */     return this.ip.getPixelValue(x, y);
/*     */   }
/*     */ 
/*     */   private boolean isLine(int xs, int ys)
/*     */   {
/* 320 */     int r = 5;
/* 321 */     int xmin = xs;
/* 322 */     int xmax = xs + 2 * r;
/* 323 */     if (xmax >= this.width) xmax = this.width - 1;
/* 324 */     int ymin = ys - r;
/* 325 */     if (ymin < 0) ymin = 0;
/* 326 */     int ymax = ys + r;
/* 327 */     if (ymax >= this.height) ymax = this.height - 1;
/* 328 */     int area = 0;
/* 329 */     int insideCount = 0;
/* 330 */     for (int x = xmin; x <= xmax; x++)
/* 331 */       for (int y = ymin; y <= ymax; y++) {
/* 332 */         area++;
/* 333 */         if (inside(x, y))
/* 334 */           insideCount++;
/*     */       }
/* 336 */     if (IJ.debugMode)
/* 337 */       IJ.log((insideCount / area < 0.25D ? "line " : "blob ") + insideCount + " " + area + " " + IJ.d2s(insideCount / area));
/* 338 */     return insideCount / area < 0.25D;
/*     */   }
/*     */ 
/*     */   public static void setAllPoints(boolean b) {
/* 342 */     allPoints = b;
/*     */   }
/*     */ 
/*     */   public static boolean allPoints() {
/* 346 */     return allPoints;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.Wand
 * JD-Core Version:    0.6.2
 */