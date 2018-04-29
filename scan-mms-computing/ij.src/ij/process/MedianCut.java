/*     */ package ij.process;
/*     */ 
/*     */ import ij.IJ;
/*     */ import java.awt.Color;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.IndexColorModel;
/*     */ 
/*     */ public class MedianCut
/*     */ {
/*     */   static final int MAXCOLORS = 256;
/*     */   static final int HSIZE = 32768;
/*     */   private int[] hist;
/*     */   private int[] histPtr;
/*     */   private Cube[] list;
/*     */   private int[] pixels32;
/*     */   private int width;
/*     */   private int height;
/*     */   private IndexColorModel cm;
/*     */ 
/*     */   public MedianCut(int[] pixels, int width, int height)
/*     */   {
/*  25 */     this.pixels32 = pixels;
/*  26 */     this.width = width;
/*  27 */     this.height = height;
/*     */ 
/*  30 */     IJ.showProgress(0.3D);
/*  31 */     IJ.showStatus("Building 32x32x32 RGB histogram");
/*  32 */     this.hist = new int[32768];
/*  33 */     for (int i = 0; i < width * height; i++) {
/*  34 */       int color16 = rgb(this.pixels32[i]);
/*  35 */       this.hist[color16] += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public MedianCut(ColorProcessor ip) {
/*  40 */     this((int[])ip.getPixels(), ip.getWidth(), ip.getHeight());
/*     */   }
/*     */ 
/*     */   int getColorCount() {
/*  44 */     int count = 0;
/*  45 */     for (int i = 0; i < 32768; i++)
/*  46 */       if (this.hist[i] > 0) count++;
/*  47 */     return count;
/*     */   }
/*     */ 
/*     */   Color getModalColor()
/*     */   {
/*  52 */     int max = 0;
/*  53 */     int c = 0;
/*  54 */     for (int i = 0; i < 32768; i++)
/*  55 */       if (this.hist[i] > max) {
/*  56 */         max = this.hist[i];
/*  57 */         c = i;
/*     */       }
/*  59 */     return new Color(red(c), green(c), blue(c));
/*     */   }
/*     */ 
/*     */   private final int rgb(int c)
/*     */   {
/*  65 */     int r = (c & 0xF80000) >> 19;
/*  66 */     int g = (c & 0xF800) >> 6;
/*  67 */     int b = (c & 0xF8) << 7;
/*  68 */     return b | g | r;
/*     */   }
/*     */ 
/*     */   private final int red(int x)
/*     */   {
/*  73 */     return (x & 0x1F) << 3;
/*     */   }
/*     */ 
/*     */   private final int green(int x)
/*     */   {
/*  78 */     return x >> 2 & 0xF8;
/*     */   }
/*     */ 
/*     */   private final int blue(int x)
/*     */   {
/*  83 */     return x >> 7 & 0xF8;
/*     */   }
/*     */ 
/*     */   public Image convert(int maxcubes)
/*     */   {
/*  92 */     ImageProcessor ip = convertToByte(maxcubes);
/*  93 */     return ip.createImage();
/*     */   }
/*     */ 
/*     */   public ImageProcessor convertToByte(int maxcubes)
/*     */   {
/* 103 */     int longdim = 0;
/*     */ 
/* 107 */     IJ.showStatus("Median cut");
/* 108 */     this.list = new Cube[256];
/* 109 */     this.histPtr = new int[32768];
/* 110 */     int ncubes = 0;
/* 111 */     Cube cube = new Cube();
/* 112 */     int i = 0; for (int color = 0; i <= 32767; i++) {
/* 113 */       if (this.hist[i] != 0) {
/* 114 */         this.histPtr[(color++)] = i;
/* 115 */         cube.count += this.hist[i];
/*     */       }
/*     */     }
/* 118 */     cube.lower = 0; cube.upper = (color - 1);
/* 119 */     cube.level = 0;
/* 120 */     Shrink(cube);
/* 121 */     this.list[(ncubes++)] = cube;
/*     */ 
/* 124 */     while (ncubes < maxcubes)
/*     */     {
/* 127 */       int level = 255; int splitpos = -1;
/* 128 */       for (int k = 0; k <= ncubes - 1; k++)
/* 129 */         if (this.list[k].lower != this.list[k].upper)
/*     */         {
/* 131 */           if (this.list[k].level < level) {
/* 132 */             level = this.list[k].level;
/* 133 */             splitpos = k;
/*     */           }
/*     */         }
/* 136 */       if (splitpos == -1)
/*     */       {
/*     */         break;
/*     */       }
/* 140 */       cube = this.list[splitpos];
/* 141 */       int lr = cube.rmax - cube.rmin;
/* 142 */       int lg = cube.gmax - cube.gmin;
/* 143 */       int lb = cube.bmax - cube.bmin;
/* 144 */       if ((lr >= lg) && (lr >= lb)) longdim = 0;
/* 145 */       if ((lg >= lr) && (lg >= lb)) longdim = 1;
/* 146 */       if ((lb >= lr) && (lb >= lg)) longdim = 2;
/*     */ 
/* 149 */       reorderColors(this.histPtr, cube.lower, cube.upper, longdim);
/* 150 */       quickSort(this.histPtr, cube.lower, cube.upper);
/* 151 */       restoreColorOrder(this.histPtr, cube.lower, cube.upper, longdim);
/*     */ 
/* 154 */       int count = 0;
/* 155 */       for (i = cube.lower; (i <= cube.upper - 1) && 
/* 156 */         (count < cube.count / 2); i++)
/*     */       {
/* 157 */         color = this.histPtr[i];
/* 158 */         count += this.hist[color];
/*     */       }
/* 160 */       int median = i;
/*     */ 
/* 164 */       Cube cubeA = new Cube();
/* 165 */       cubeA.lower = cube.lower;
/* 166 */       cubeA.upper = (median - 1);
/* 167 */       cubeA.count = count;
/* 168 */       cube.level += 1;
/* 169 */       Shrink(cubeA);
/* 170 */       this.list[splitpos] = cubeA;
/*     */ 
/* 172 */       Cube cubeB = new Cube();
/* 173 */       cubeB.lower = median;
/* 174 */       cubeB.upper = cube.upper;
/* 175 */       cube.count -= count;
/* 176 */       cube.level += 1;
/* 177 */       Shrink(cubeB);
/* 178 */       this.list[(ncubes++)] = cubeB;
/* 179 */       if (ncubes % 15 == 0) {
/* 180 */         IJ.showProgress(0.3D + 0.6D * ncubes / maxcubes);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 186 */     IJ.showProgress(0.9D);
/* 187 */     makeInverseMap(this.hist, ncubes);
/* 188 */     IJ.showProgress(0.95D);
/* 189 */     return makeImage();
/*     */   }
/*     */ 
/*     */   void Shrink(Cube cube)
/*     */   {
/* 200 */     int rmin = 255; int rmax = 0;
/* 201 */     int gmin = 255; int gmax = 0;
/* 202 */     int bmin = 255; int bmax = 0;
/* 203 */     for (int i = cube.lower; i <= cube.upper; i++) {
/* 204 */       int color = this.histPtr[i];
/* 205 */       int r = red(color);
/* 206 */       int g = green(color);
/* 207 */       int b = blue(color);
/* 208 */       if (r > rmax) rmax = r;
/* 209 */       if (r < rmin) rmin = r;
/* 210 */       if (g > gmax) gmax = g;
/* 211 */       if (g < gmin) gmin = g;
/* 212 */       if (b > bmax) bmax = b;
/* 213 */       if (b < bmin) bmin = b;
/*     */     }
/* 215 */     cube.rmin = rmin; cube.rmax = rmax;
/* 216 */     cube.gmin = gmin; cube.gmax = gmax;
/* 217 */     cube.bmin = bmin; cube.bmax = bmax;
/*     */   }
/*     */ 
/*     */   void makeInverseMap(int[] hist, int ncubes)
/*     */   {
/* 231 */     byte[] rLUT = new byte[256];
/* 232 */     byte[] gLUT = new byte[256];
/* 233 */     byte[] bLUT = new byte[256];
/*     */ 
/* 235 */     IJ.showStatus("Making inverse map");
/* 236 */     for (int k = 0; k <= ncubes - 1; k++) {
/* 237 */       Cube cube = this.list[k];
/*     */       float bsum;
/*     */       float gsum;
/* 238 */       float rsum = gsum = bsum = 0.0F;
/* 239 */       for (int i = cube.lower; i <= cube.upper; i++) {
/* 240 */         int color = this.histPtr[i];
/* 241 */         int r = red(color);
/* 242 */         rsum += r * hist[color];
/* 243 */         int g = green(color);
/* 244 */         gsum += g * hist[color];
/* 245 */         int b = blue(color);
/* 246 */         bsum += b * hist[color];
/*     */       }
/*     */ 
/* 250 */       int r = (int)(rsum / cube.count);
/* 251 */       int g = (int)(gsum / cube.count);
/* 252 */       int b = (int)(bsum / cube.count);
/* 253 */       if ((r == 248) && (g == 248) && (b == 248))
/* 254 */         r = g = b = 'ÿ';
/* 255 */       rLUT[k] = ((byte)r);
/* 256 */       gLUT[k] = ((byte)g);
/* 257 */       bLUT[k] = ((byte)b);
/*     */     }
/* 259 */     this.cm = new IndexColorModel(8, ncubes, rLUT, gLUT, bLUT);
/*     */ 
/* 263 */     for (int k = 0; k <= ncubes - 1; k++) {
/* 264 */       Cube cube = this.list[k];
/* 265 */       for (int i = cube.lower; i <= cube.upper; i++) {
/* 266 */         int color = this.histPtr[i];
/* 267 */         hist[color] = k;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void reorderColors(int[] a, int lo, int hi, int longDim)
/*     */   {
/* 278 */     switch (longDim) {
/*     */     case 0:
/* 280 */       for (int i = lo; i <= hi; i++) {
/* 281 */         int c = a[i];
/* 282 */         int r = c & 0x1F;
/* 283 */         a[i] = (r << 10 | c >> 5);
/*     */       }
/* 285 */       break;
/*     */     case 1:
/* 287 */       for (int i = lo; i <= hi; i++) {
/* 288 */         int c = a[i];
/* 289 */         int r = c & 0x1F;
/* 290 */         int g = c >> 5 & 0x1F;
/* 291 */         int b = c >> 10;
/* 292 */         a[i] = (g << 10 | b << 5 | r);
/*     */       }
/* 294 */       break;
/*     */     case 2:
/*     */     }
/*     */   }
/*     */ 
/*     */   void restoreColorOrder(int[] a, int lo, int hi, int longDim)
/*     */   {
/* 305 */     switch (longDim) {
/*     */     case 0:
/* 307 */       for (int i = lo; i <= hi; i++) {
/* 308 */         int c = a[i];
/* 309 */         int r = c >> 10;
/* 310 */         a[i] = ((c & 0x3FF) << 5 | r);
/*     */       }
/* 312 */       break;
/*     */     case 1:
/* 314 */       for (int i = lo; i <= hi; i++) {
/* 315 */         int c = a[i];
/* 316 */         int r = c & 0x1F;
/* 317 */         int g = c >> 10;
/* 318 */         int b = c >> 5 & 0x1F;
/* 319 */         a[i] = (b << 10 | g << 5 | r);
/*     */       }
/* 321 */       break;
/*     */     case 2:
/*     */     }
/*     */   }
/*     */ 
/*     */   void quickSort(int[] a, int lo0, int hi0)
/*     */   {
/* 331 */     int lo = lo0;
/* 332 */     int hi = hi0;
/*     */ 
/* 335 */     if (hi0 > lo0) {
/* 336 */       int mid = a[((lo0 + hi0) / 2)];
/* 337 */       while (lo <= hi) {
/* 338 */         while ((lo < hi0) && (a[lo] < mid))
/* 339 */           lo++;
/* 340 */         while ((hi > lo0) && (a[hi] > mid))
/* 341 */           hi--;
/* 342 */         if (lo <= hi) {
/* 343 */           int t = a[lo];
/* 344 */           a[lo] = a[hi];
/* 345 */           a[hi] = t;
/* 346 */           lo++;
/* 347 */           hi--;
/*     */         }
/*     */       }
/* 350 */       if (lo0 < hi)
/* 351 */         quickSort(a, lo0, hi);
/* 352 */       if (lo < hi0)
/* 353 */         quickSort(a, lo, hi0);
/*     */     }
/*     */   }
/*     */ 
/*     */   ImageProcessor makeImage()
/*     */   {
/* 366 */     IJ.showStatus("Creating 8-bit image");
/* 367 */     byte[] pixels8 = new byte[this.width * this.height];
/* 368 */     for (int i = 0; i < this.width * this.height; i++) {
/* 369 */       int color16 = rgb(this.pixels32[i]);
/* 370 */       pixels8[i] = ((byte)this.hist[color16]);
/*     */     }
/* 372 */     ImageProcessor ip = new ByteProcessor(this.width, this.height, pixels8, this.cm);
/* 373 */     IJ.showProgress(1.0D);
/* 374 */     return ip;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.MedianCut
 * JD-Core Version:    0.6.2
 */