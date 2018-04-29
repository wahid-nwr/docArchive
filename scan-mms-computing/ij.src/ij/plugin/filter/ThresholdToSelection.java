/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.ShapeRoi;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class ThresholdToSelection
/*     */   implements PlugInFilter
/*     */ {
/*     */   ImagePlus image;
/*     */   ImageProcessor ip;
/*     */   float min;
/*     */   float max;
/*     */   int w;
/*     */   int h;
/*     */ 
/*     */   public void run(ImageProcessor ip)
/*     */   {
/*  26 */     this.image.setRoi(convert(ip));
/*     */   }
/*     */ 
/*     */   public static Roi run(ImagePlus imp) {
/*  30 */     ThresholdToSelection tts = new ThresholdToSelection();
/*  31 */     tts.image = imp;
/*  32 */     return tts.convert(imp.getProcessor());
/*     */   }
/*     */ 
/*     */   public Roi convert(ImageProcessor ip) {
/*  36 */     this.ip = ip;
/*  37 */     this.min = ((float)ip.getMinThreshold());
/*  38 */     this.max = ((float)ip.getMaxThreshold());
/*  39 */     this.w = ip.getWidth();
/*  40 */     this.h = ip.getHeight();
/*  41 */     return getRoi();
/*     */   }
/*     */ 
/*     */   final boolean selected(int x, int y) {
/*  45 */     float v = this.ip.getf(x, y);
/*  46 */     return (v >= this.min) && (v <= this.max);
/*     */   }
/*     */ 
/*     */   Roi getRoi()
/*     */   {
/* 174 */     IJ.showStatus("Converting threshold to selection");
/*     */ 
/* 176 */     ArrayList polygons = new ArrayList();
/*     */ 
/* 178 */     int progressInc = Math.max(this.h / 50, 1);
/*     */ 
/* 180 */     boolean[] prevRow = new boolean[this.w + 2];
/* 181 */     boolean[] thisRow = new boolean[this.w + 2];
/* 182 */     Outline[] outline = new Outline[this.w + 1];
/*     */ 
/* 184 */     for (int y = 0; y <= this.h; y++) {
/* 185 */       boolean[] b = prevRow; prevRow = thisRow; thisRow = b;
/* 186 */       for (int x = 0; x <= this.w; x++) {
/* 187 */         if ((y < this.h) && (x < this.w))
/* 188 */           thisRow[(x + 1)] = selected(x, y);
/*     */         else
/* 190 */           thisRow[(x + 1)] = false;
/* 191 */         if (thisRow[(x + 1)] != 0) {
/* 192 */           if (prevRow[(x + 1)] == 0)
/*     */           {
/* 199 */             if (outline[x] == null) {
/* 200 */               if (outline[(x + 1)] == null)
/*     */               {
/*     */                 void tmp182_179 = new Outline(); outline[x] = tmp182_179; outline[(x + 1)] = tmp182_179;
/* 202 */                 outline[x].push(x + 1, y);
/* 203 */                 outline[x].push(x, y);
/*     */               } else {
/* 205 */                 outline[x] = outline[(x + 1)];
/* 206 */                 outline[(x + 1)] = null;
/* 207 */                 outline[x].push(x, y);
/*     */               }
/*     */             }
/* 210 */             else if (outline[(x + 1)] == null) {
/* 211 */               outline[(x + 1)] = outline[x];
/* 212 */               outline[x] = null;
/* 213 */               outline[(x + 1)].shift(x + 1, y);
/* 214 */             } else if (outline[(x + 1)] == outline[x])
/*     */             {
/* 216 */               polygons.add(outline[x].getPolygon());
/*     */                tmp335_334 = null; outline[(x + 1)] = tmp335_334; outline[x] = tmp335_334;
/*     */             } else {
/* 219 */               outline[x].shift(outline[(x + 1)]);
/* 220 */               for (int x1 = 0; x1 <= this.w; x1++)
/* 221 */                 if ((x1 != x + 1) && (outline[x1] == outline[(x + 1)])) {
/* 222 */                   outline[x1] = outline[x];
/*     */                    tmp413_412 = null; outline[(x + 1)] = tmp413_412; outline[x] = tmp413_412;
/* 224 */                   break;
/*     */                 }
/* 226 */               if (outline[x] != null) {
/* 227 */                 throw new RuntimeException("assertion failed");
/*     */               }
/*     */             }
/*     */           }
/* 231 */           if (thisRow[x] == 0)
/*     */           {
/* 233 */             if (outline[x] == null)
/* 234 */               throw new RuntimeException("assertion failed!");
/* 235 */             outline[x].push(x, y + 1);
/*     */           }
/*     */         } else {
/* 238 */           if (prevRow[(x + 1)] != 0)
/*     */           {
/* 245 */             if (outline[x] == null) {
/* 246 */               if (outline[(x + 1)] == null)
/*     */               {
/*     */                 void tmp529_526 = new Outline(); outline[(x + 1)] = tmp529_526; outline[x] = tmp529_526;
/* 248 */                 outline[x].push(x, y);
/* 249 */                 outline[x].push(x + 1, y);
/*     */               } else {
/* 251 */                 outline[x] = outline[(x + 1)];
/* 252 */                 outline[(x + 1)] = null;
/* 253 */                 outline[x].shift(x, y);
/*     */               }
/* 255 */             } else if (outline[(x + 1)] == null) {
/* 256 */               outline[(x + 1)] = outline[x];
/* 257 */               outline[x] = null;
/* 258 */               outline[(x + 1)].push(x + 1, y);
/* 259 */             } else if (outline[(x + 1)] == outline[x])
/*     */             {
/* 261 */               polygons.add(outline[x].getPolygon());
/*     */                tmp682_681 = null; outline[(x + 1)] = tmp682_681; outline[x] = tmp682_681;
/*     */             } else {
/* 264 */               outline[x].push(outline[(x + 1)]);
/* 265 */               for (int x1 = 0; x1 <= this.w; x1++)
/* 266 */                 if ((x1 != x + 1) && (outline[x1] == outline[(x + 1)])) {
/* 267 */                   outline[x1] = outline[x];
/*     */                    tmp760_759 = null; outline[(x + 1)] = tmp760_759; outline[x] = tmp760_759;
/* 269 */                   break;
/*     */                 }
/* 271 */               if (outline[x] != null)
/* 272 */                 throw new RuntimeException("assertion failed");
/*     */             }
/*     */           }
/* 275 */           if (thisRow[x] != 0)
/*     */           {
/* 277 */             if (outline[x] == null)
/* 278 */               throw new RuntimeException("assertion failed");
/* 279 */             outline[x].shift(x, y + 1);
/*     */           }
/*     */         }
/*     */       }
/* 283 */       if ((y & progressInc) == 0) IJ.showProgress(y + 1, this.h + 1);
/*     */ 
/*     */     }
/*     */ 
/* 287 */     GeneralPath path = new GeneralPath(0);
/* 288 */     for (int i = 0; i < polygons.size(); i++) {
/* 289 */       path.append((Polygon)polygons.get(i), false);
/*     */     }
/* 291 */     ShapeRoi shape = new ShapeRoi(path);
/* 292 */     Roi roi = shape != null ? shape.shapeToRoi() : null;
/* 293 */     IJ.showProgress(1, 1);
/* 294 */     if (roi != null) {
/* 295 */       return roi;
/*     */     }
/* 297 */     return shape;
/*     */   }
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp) {
/* 301 */     this.image = imp;
/* 302 */     return 141;
/*     */   }
/*     */ 
/*     */   static class Outline
/*     */   {
/*     */     int[] x;
/*     */     int[] y;
/*     */     int first;
/*     */     int last;
/*     */     int reserved;
/*  61 */     final int GROW = 10;
/*     */ 
/*     */     public Outline() {
/*  64 */       this.reserved = 10;
/*  65 */       this.x = new int[this.reserved];
/*  66 */       this.y = new int[this.reserved];
/*  67 */       this.first = (this.last = 5);
/*     */     }
/*     */ 
/*     */     private void needs(int newCount, int offset) {
/*  71 */       if ((newCount > this.reserved) || (offset > this.first)) {
/*  72 */         if (newCount < this.reserved + 10 + 1)
/*  73 */           newCount = this.reserved + 10 + 1;
/*  74 */         int[] newX = new int[newCount];
/*  75 */         int[] newY = new int[newCount];
/*  76 */         System.arraycopy(this.x, 0, newX, offset, this.last);
/*  77 */         System.arraycopy(this.y, 0, newY, offset, this.last);
/*  78 */         this.x = newX;
/*  79 */         this.y = newY;
/*  80 */         this.first += offset;
/*  81 */         this.last += offset;
/*  82 */         this.reserved = newCount;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void push(int x, int y) {
/*  87 */       needs(this.last + 1, 0);
/*  88 */       this.x[this.last] = x;
/*  89 */       this.y[this.last] = y;
/*  90 */       this.last += 1;
/*     */     }
/*     */ 
/*     */     public void shift(int x, int y) {
/*  94 */       needs(this.last + 1, 10);
/*  95 */       this.first -= 1;
/*  96 */       this.x[this.first] = x;
/*  97 */       this.y[this.first] = y;
/*     */     }
/*     */ 
/*     */     public void push(Outline o) {
/* 101 */       int count = o.last - o.first;
/* 102 */       needs(this.last + count, 0);
/* 103 */       System.arraycopy(o.x, o.first, this.x, this.last, count);
/* 104 */       System.arraycopy(o.y, o.first, this.y, this.last, count);
/* 105 */       this.last += count;
/*     */     }
/*     */ 
/*     */     public void shift(Outline o) {
/* 109 */       int count = o.last - o.first;
/* 110 */       needs(this.last + count + 10, count + 10);
/* 111 */       this.first -= count;
/* 112 */       System.arraycopy(o.x, o.first, this.x, this.first, count);
/* 113 */       System.arraycopy(o.y, o.first, this.y, this.first, count);
/*     */     }
/*     */ 
/*     */     public Polygon getPolygon()
/*     */     {
/* 118 */       int j = this.first + 1;
/* 119 */       for (int i = this.first + 1; i + 1 < this.last; j++) {
/* 120 */         int x1 = this.x[j] - this.x[(j - 1)];
/* 121 */         int y1 = this.y[j] - this.y[(j - 1)];
/* 122 */         int x2 = this.x[(j + 1)] - this.x[j];
/* 123 */         int y2 = this.y[(j + 1)] - this.y[j];
/* 124 */         if (x1 * y2 == x2 * y1)
/*     */         {
/* 126 */           this.last -= 1;
/*     */         }
/*     */         else {
/* 129 */           if (i != j) {
/* 130 */             this.x[i] = this.x[j];
/* 131 */             this.y[i] = this.y[j];
/*     */           }
/* 133 */           i++;
/*     */         }
/*     */       }
/* 136 */       int x1 = this.x[j] - this.x[(j - 1)];
/* 137 */       int y1 = this.y[j] - this.y[(j - 1)];
/* 138 */       int x2 = this.x[this.first] - this.x[j];
/* 139 */       int y2 = this.y[this.first] - this.y[j];
/* 140 */       if (x1 * y2 == x2 * y1) {
/* 141 */         this.last -= 1;
/*     */       } else {
/* 143 */         this.x[i] = this.x[j];
/* 144 */         this.y[i] = this.y[j];
/*     */       }
/* 146 */       int count = this.last - this.first;
/* 147 */       int[] xNew = new int[count];
/* 148 */       int[] yNew = new int[count];
/* 149 */       System.arraycopy(this.x, this.first, xNew, 0, count);
/* 150 */       System.arraycopy(this.y, this.first, yNew, 0, count);
/* 151 */       return new Polygon(xNew, yNew, count);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 155 */       String res = "(first:" + this.first + ",last:" + this.last + ",reserved:" + this.reserved + ":";
/*     */ 
/* 157 */       if (this.last > this.x.length) System.err.println("ERROR!");
/* 158 */       for (int i = this.first; (i < this.last) && (i < this.x.length); i++)
/* 159 */         res = res + "(" + this.x[i] + "," + this.y[i] + ")";
/* 160 */       return res + ")";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.ThresholdToSelection
 * JD-Core Version:    0.6.2
 */