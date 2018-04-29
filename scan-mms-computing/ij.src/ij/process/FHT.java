/*     */ package ij.process;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.plugin.ContrastEnhancer;
/*     */ import ij.plugin.FFT;
/*     */ import java.awt.image.ColorModel;
/*     */ 
/*     */ public class FHT extends FloatProcessor
/*     */ {
/*     */   private boolean isFrequencyDomain;
/*     */   private int maxN;
/*     */   private float[] C;
/*     */   private float[] S;
/*     */   private int[] bitrev;
/*     */   private float[] tempArr;
/*  23 */   private boolean showProgress = true;
/*     */   public boolean quadrantSwapNeeded;
/*     */   public ColorProcessor rgb;
/*     */   public int originalWidth;
/*     */   public int originalHeight;
/*     */   public int originalBitDepth;
/*     */   public ColorModel originalColorModel;
/*     */ 
/*     */   public FHT(ImageProcessor ip)
/*     */   {
/*  41 */     super(ip.getWidth(), ip.getHeight(), (float[])((ip instanceof FloatProcessor) ? ip.duplicate().getPixels() : ip.convertToFloat().getPixels()), null);
/*  42 */     this.maxN = getWidth();
/*  43 */     resetRoi();
/*     */   }
/*     */ 
/*     */   public FHT()
/*     */   {
/*  48 */     super(8, 8);
/*     */   }
/*     */ 
/*     */   public boolean powerOf2Size()
/*     */   {
/*  53 */     int i = 2;
/*  54 */     while (i < this.width) i *= 2;
/*  55 */     return (i == this.width) && (this.width == this.height);
/*     */   }
/*     */ 
/*     */   public void transform()
/*     */   {
/*  61 */     transform(false);
/*     */   }
/*     */ 
/*     */   public void inverseTransform()
/*     */   {
/*  67 */     transform(true);
/*     */   }
/*     */ 
/*     */   void transform(boolean inverse)
/*     */   {
/*  84 */     if (!powerOf2Size())
/*  85 */       throw new IllegalArgumentException("Image not power of 2 size or not square: " + this.width + "x" + this.height);
/*  86 */     this.maxN = this.width;
/*  87 */     if (this.S == null)
/*  88 */       initializeTables(this.maxN);
/*  89 */     float[] fht = (float[])getPixels();
/*  90 */     rc2DFHT(fht, inverse, this.maxN);
/*  91 */     this.isFrequencyDomain = (!inverse);
/*     */   }
/*     */ 
/*     */   void initializeTables(int maxN) {
/*  95 */     makeSinCosTables(maxN);
/*  96 */     makeBitReverseTable(maxN);
/*  97 */     this.tempArr = new float[maxN];
/*     */   }
/*     */ 
/*     */   void makeSinCosTables(int maxN) {
/* 101 */     int n = maxN / 4;
/* 102 */     this.C = new float[n];
/* 103 */     this.S = new float[n];
/* 104 */     double theta = 0.0D;
/* 105 */     double dTheta = 6.283185307179586D / maxN;
/* 106 */     for (int i = 0; i < n; i++) {
/* 107 */       this.C[i] = ((float)Math.cos(theta));
/* 108 */       this.S[i] = ((float)Math.sin(theta));
/* 109 */       theta += dTheta;
/*     */     }
/*     */   }
/*     */ 
/*     */   void makeBitReverseTable(int maxN) {
/* 114 */     this.bitrev = new int[maxN];
/* 115 */     int nLog2 = log2(maxN);
/* 116 */     for (int i = 0; i < maxN; i++)
/* 117 */       this.bitrev[i] = bitRevX(i, nLog2);
/*     */   }
/*     */ 
/*     */   public void rc2DFHT(float[] x, boolean inverse, int maxN)
/*     */   {
/* 123 */     if (this.S == null) initializeTables(maxN);
/* 124 */     for (int row = 0; row < maxN; row++)
/* 125 */       dfht3(x, row * maxN, inverse, maxN);
/* 126 */     progress(0.4D);
/* 127 */     transposeR(x, maxN);
/* 128 */     progress(0.5D);
/* 129 */     for (int row = 0; row < maxN; row++)
/* 130 */       dfht3(x, row * maxN, inverse, maxN);
/* 131 */     progress(0.7D);
/* 132 */     transposeR(x, maxN);
/* 133 */     progress(0.8D);
/*     */ 
/* 137 */     for (int row = 0; row <= maxN / 2; row++) {
/* 138 */       for (int col = 0; col <= maxN / 2; col++) {
/* 139 */         int mRow = (maxN - row) % maxN;
/* 140 */         int mCol = (maxN - col) % maxN;
/* 141 */         float A = x[(row * maxN + col)];
/* 142 */         float B = x[(mRow * maxN + col)];
/* 143 */         float C = x[(row * maxN + mCol)];
/* 144 */         float D = x[(mRow * maxN + mCol)];
/* 145 */         float E = (A + D - (B + C)) / 2.0F;
/* 146 */         x[(row * maxN + col)] = (A - E);
/* 147 */         x[(mRow * maxN + col)] = (B + E);
/* 148 */         x[(row * maxN + mCol)] = (C + E);
/* 149 */         x[(mRow * maxN + mCol)] = (D - E);
/*     */       }
/*     */     }
/* 152 */     progress(0.95D);
/*     */   }
/*     */ 
/*     */   void progress(double percent) {
/* 156 */     if (this.showProgress)
/* 157 */       IJ.showProgress(percent);
/*     */   }
/*     */ 
/*     */   public void dfht3(float[] x, int base, boolean inverse, int maxN)
/*     */   {
/* 167 */     if (this.S == null) initializeTables(maxN);
/* 168 */     int Nlog2 = log2(maxN);
/* 169 */     BitRevRArr(x, base, Nlog2, maxN);
/* 170 */     int gpSize = 2;
/* 171 */     int numGps = maxN / 4;
/* 172 */     for (int gpNum = 0; gpNum < numGps; gpNum++) {
/* 173 */       int Ad1 = gpNum * 4;
/* 174 */       int Ad2 = Ad1 + 1;
/* 175 */       int Ad3 = Ad1 + gpSize;
/* 176 */       int Ad4 = Ad2 + gpSize;
/* 177 */       float rt1 = x[(base + Ad1)] + x[(base + Ad2)];
/* 178 */       float rt2 = x[(base + Ad1)] - x[(base + Ad2)];
/* 179 */       float rt3 = x[(base + Ad3)] + x[(base + Ad4)];
/* 180 */       float rt4 = x[(base + Ad3)] - x[(base + Ad4)];
/* 181 */       x[(base + Ad1)] = (rt1 + rt3);
/* 182 */       x[(base + Ad2)] = (rt2 + rt4);
/* 183 */       x[(base + Ad3)] = (rt1 - rt3);
/* 184 */       x[(base + Ad4)] = (rt2 - rt4);
/*     */     }
/*     */ 
/* 187 */     if (Nlog2 > 2)
/*     */     {
/* 189 */       gpSize = 4;
/* 190 */       int numBfs = 2;
/* 191 */       numGps /= 2;
/*     */ 
/* 193 */       for (int stage = 2; stage < Nlog2; stage++) {
/* 194 */         for (gpNum = 0; gpNum < numGps; gpNum++) {
/* 195 */           int Ad0 = gpNum * gpSize * 2;
/* 196 */           int Ad1 = Ad0;
/* 197 */           int Ad2 = Ad1 + gpSize;
/* 198 */           int Ad3 = Ad1 + gpSize / 2;
/* 199 */           int Ad4 = Ad3 + gpSize;
/* 200 */           float rt1 = x[(base + Ad1)];
/* 201 */           x[(base + Ad1)] += x[(base + Ad2)];
/* 202 */           x[(base + Ad2)] = (rt1 - x[(base + Ad2)]);
/* 203 */           rt1 = x[(base + Ad3)];
/* 204 */           x[(base + Ad3)] += x[(base + Ad4)];
/* 205 */           x[(base + Ad4)] = (rt1 - x[(base + Ad4)]);
/* 206 */           for (int bfNum = 1; bfNum < numBfs; bfNum++)
/*     */           {
/* 208 */             Ad1 = bfNum + Ad0;
/* 209 */             Ad2 = Ad1 + gpSize;
/* 210 */             Ad3 = gpSize - bfNum + Ad0;
/* 211 */             Ad4 = Ad3 + gpSize;
/*     */ 
/* 213 */             int CSAd = bfNum * numGps;
/* 214 */             rt1 = x[(base + Ad2)] * this.C[CSAd] + x[(base + Ad4)] * this.S[CSAd];
/* 215 */             float rt2 = x[(base + Ad4)] * this.C[CSAd] - x[(base + Ad2)] * this.S[CSAd];
/*     */ 
/* 217 */             x[(base + Ad1)] -= rt1;
/* 218 */             x[(base + Ad1)] += rt1;
/* 219 */             x[(base + Ad3)] += rt2;
/* 220 */             x[(base + Ad3)] -= rt2;
/*     */           }
/*     */         }
/*     */ 
/* 224 */         gpSize *= 2;
/* 225 */         numBfs *= 2;
/* 226 */         numGps /= 2;
/*     */       }
/*     */     }
/*     */ 
/* 230 */     if (inverse)
/* 231 */       for (int i = 0; i < maxN; i++)
/* 232 */         x[(base + i)] /= maxN;
/*     */   }
/*     */ 
/*     */   void transposeR(float[] x, int maxN)
/*     */   {
/* 240 */     for (int r = 0; r < maxN; r++)
/* 241 */       for (int c = r; c < maxN; c++)
/* 242 */         if (r != c) {
/* 243 */           float rTemp = x[(r * maxN + c)];
/* 244 */           x[(r * maxN + c)] = x[(c * maxN + r)];
/* 245 */           x[(c * maxN + r)] = rTemp;
/*     */         }
/*     */   }
/*     */ 
/*     */   int log2(int x)
/*     */   {
/* 252 */     int count = 15;
/* 253 */     while (!btst(x, count))
/* 254 */       count--;
/* 255 */     return count;
/*     */   }
/*     */ 
/*     */   private boolean btst(int x, int bit)
/*     */   {
/* 261 */     return (x & 1 << bit) != 0;
/*     */   }
/*     */ 
/*     */   void BitRevRArr(float[] x, int base, int bitlen, int maxN) {
/* 265 */     for (int i = 0; i < maxN; i++)
/* 266 */       this.tempArr[i] = x[(base + this.bitrev[i])];
/* 267 */     for (int i = 0; i < maxN; i++)
/* 268 */       x[(base + i)] = this.tempArr[i];
/*     */   }
/*     */ 
/*     */   private int bitRevX(int x, int bitlen) {
/* 272 */     int temp = 0;
/* 273 */     for (int i = 0; i <= bitlen; i++)
/* 274 */       if ((x & 1 << i) != 0)
/* 275 */         temp |= 1 << bitlen - i - 1;
/* 276 */     return temp & 0xFFFF;
/*     */   }
/*     */ 
/*     */   private int bset(int x, int bit) {
/* 280 */     x |= 1 << bit;
/* 281 */     return x;
/*     */   }
/*     */ 
/*     */   public ImageProcessor getPowerSpectrum()
/*     */   {
/* 287 */     if (!this.isFrequencyDomain) {
/* 288 */       throw new IllegalArgumentException("Frequency domain image required");
/*     */     }
/*     */ 
/* 291 */     float min = 3.4028235E+38F;
/* 292 */     float max = 1.4E-45F;
/* 293 */     float[] fps = new float[this.maxN * this.maxN];
/* 294 */     byte[] ps = new byte[this.maxN * this.maxN];
/* 295 */     float[] fht = (float[])getPixels();
/*     */ 
/* 297 */     for (int row = 0; row < this.maxN; row++) {
/* 298 */       FHTps(row, this.maxN, fht, fps);
/* 299 */       int base = row * this.maxN;
/* 300 */       for (int col = 0; col < this.maxN; col++) {
/* 301 */         float r = fps[(base + col)];
/* 302 */         if (r < min) min = r;
/* 303 */         if (r > max) max = r;
/*     */       }
/*     */     }
/*     */ 
/* 307 */     if (min < 1.0D)
/* 308 */       min = 0.0F;
/*     */     else
/* 310 */       min = (float)Math.log(min);
/* 311 */     max = (float)Math.log(max);
/* 312 */     float scale = (float)(253.0D / (max - min));
/*     */ 
/* 314 */     for (int row = 0; row < this.maxN; row++) {
/* 315 */       int base = row * this.maxN;
/* 316 */       for (int col = 0; col < this.maxN; col++) {
/* 317 */         float r = fps[(base + col)];
/* 318 */         if (r < 1.0F)
/* 319 */           r = 0.0F;
/*     */         else
/* 321 */           r = (float)Math.log(r);
/* 322 */         ps[(base + col)] = ((byte)(int)((r - min) * scale + 0.5D + 1.0D));
/*     */       }
/*     */     }
/* 325 */     ImageProcessor ip = new ByteProcessor(this.maxN, this.maxN, ps, null);
/* 326 */     swapQuadrants(ip);
/* 327 */     if (FFT.displayRawPS) {
/* 328 */       ImageProcessor ip2 = new FloatProcessor(this.maxN, this.maxN, fps, null);
/* 329 */       swapQuadrants(ip2);
/* 330 */       new ImagePlus("PS of " + FFT.fileName, ip2).show();
/*     */     }
/* 332 */     if (FFT.displayFHT) {
/* 333 */       ImageProcessor ip3 = new FloatProcessor(this.maxN, this.maxN, fht, null);
/* 334 */       ImagePlus imp2 = new ImagePlus("FHT of " + FFT.fileName, ip3.duplicate());
/* 335 */       new ContrastEnhancer().stretchHistogram(imp2, 0.1D);
/* 336 */       imp2.show();
/*     */     }
/* 338 */     if (FFT.displayComplex) {
/* 339 */       ImageStack ct = getComplexTransform();
/* 340 */       ImagePlus imp2 = new ImagePlus("Complex of " + FFT.fileName, ct);
/* 341 */       new ContrastEnhancer().stretchHistogram(imp2, 0.1D);
/* 342 */       imp2.setProperty("FFT width", "" + this.originalWidth);
/* 343 */       imp2.setProperty("FFT height", "" + this.originalHeight);
/* 344 */       imp2.show();
/*     */     }
/* 346 */     return ip;
/*     */   }
/*     */ 
/*     */   void FHTps(int row, int maxN, float[] fht, float[] ps)
/*     */   {
/* 351 */     int base = row * maxN;
/*     */ 
/* 353 */     for (int c = 0; c < maxN; c++) {
/* 354 */       int l = (maxN - row) % maxN * maxN + (maxN - c) % maxN;
/* 355 */       ps[(base + c)] = ((sqr(fht[(base + c)]) + sqr(fht[l])) / 2.0F);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ImageStack getComplexTransform()
/*     */   {
/* 363 */     if (!this.isFrequencyDomain)
/* 364 */       throw new IllegalArgumentException("Frequency domain image required");
/* 365 */     float[] fht = (float[])getPixels();
/* 366 */     float[] re = new float[this.maxN * this.maxN];
/* 367 */     float[] im = new float[this.maxN * this.maxN];
/* 368 */     for (int i = 0; i < this.maxN; i++) {
/* 369 */       FHTreal(i, this.maxN, fht, re);
/* 370 */       FHTimag(i, this.maxN, fht, im);
/*     */     }
/* 372 */     swapQuadrants(new FloatProcessor(this.maxN, this.maxN, re, null));
/* 373 */     swapQuadrants(new FloatProcessor(this.maxN, this.maxN, im, null));
/* 374 */     ImageStack stack = new ImageStack(this.maxN, this.maxN);
/* 375 */     stack.addSlice("Real", re);
/* 376 */     stack.addSlice("Imaginary", im);
/* 377 */     return stack;
/*     */   }
/*     */ 
/*     */   void FHTreal(int row, int maxN, float[] fht, float[] real)
/*     */   {
/* 384 */     int base = row * maxN;
/* 385 */     int offs = (maxN - row) % maxN * maxN;
/* 386 */     for (int c = 0; c < maxN; c++)
/* 387 */       real[(base + c)] = ((fht[(base + c)] + fht[(offs + (maxN - c) % maxN)]) * 0.5F);
/*     */   }
/*     */ 
/*     */   void FHTimag(int row, int maxN, float[] fht, float[] imag)
/*     */   {
/* 396 */     int base = row * maxN;
/* 397 */     int offs = (maxN - row) % maxN * maxN;
/* 398 */     for (int c = 0; c < maxN; c++)
/* 399 */       imag[(base + c)] = ((-fht[(base + c)] + fht[(offs + (maxN - c) % maxN)]) * 0.5F);
/*     */   }
/*     */ 
/*     */   ImageProcessor calculateAmplitude(float[] fht, int maxN)
/*     */   {
/* 404 */     float[] amp = new float[maxN * maxN];
/* 405 */     for (int row = 0; row < maxN; row++) {
/* 406 */       amplitude(row, maxN, fht, amp);
/*     */     }
/* 408 */     ImageProcessor ip = new FloatProcessor(maxN, maxN, amp, null);
/* 409 */     swapQuadrants(ip);
/* 410 */     return ip;
/*     */   }
/*     */ 
/*     */   void amplitude(int row, int maxN, float[] fht, float[] amplitude)
/*     */   {
/* 415 */     int base = row * maxN;
/*     */ 
/* 417 */     for (int c = 0; c < maxN; c++) {
/* 418 */       int l = (maxN - row) % maxN * maxN + (maxN - c) % maxN;
/* 419 */       amplitude[(base + c)] = ((float)Math.sqrt(sqr(fht[(base + c)]) + sqr(fht[l])));
/*     */     }
/*     */   }
/*     */ 
/*     */   float sqr(float x) {
/* 424 */     return x * x;
/*     */   }
/*     */ 
/*     */   public void swapQuadrants(ImageProcessor ip)
/*     */   {
/* 437 */     int size = ip.getWidth() / 2;
/* 438 */     ip.setRoi(size, 0, size, size);
/* 439 */     ImageProcessor t1 = ip.crop();
/* 440 */     ip.setRoi(0, size, size, size);
/* 441 */     ImageProcessor t2 = ip.crop();
/* 442 */     ip.insert(t1, 0, size);
/* 443 */     ip.insert(t2, size, 0);
/* 444 */     ip.setRoi(0, 0, size, size);
/* 445 */     t1 = ip.crop();
/* 446 */     ip.setRoi(size, size, size, size);
/* 447 */     t2 = ip.crop();
/* 448 */     ip.insert(t1, size, size);
/* 449 */     ip.insert(t2, 0, 0);
/* 450 */     ip.resetRoi();
/*     */   }
/*     */ 
/*     */   public void swapQuadrants()
/*     */   {
/* 456 */     swapQuadrants(this);
/*     */   }
/*     */ 
/*     */   void changeValues(ImageProcessor ip, int v1, int v2, int v3) {
/* 460 */     byte[] pixels = (byte[])ip.getPixels();
/*     */ 
/* 463 */     for (int i = 0; i < pixels.length; i++) {
/* 464 */       int v = pixels[i] & 0xFF;
/* 465 */       if ((v >= v1) && (v <= v2))
/* 466 */         pixels[i] = ((byte)v3);
/*     */     }
/*     */   }
/*     */ 
/*     */   public FHT multiply(FHT fht)
/*     */   {
/* 475 */     return multiply(fht, false);
/*     */   }
/*     */ 
/*     */   public FHT conjugateMultiply(FHT fht)
/*     */   {
/* 483 */     return multiply(fht, true);
/*     */   }
/*     */ 
/*     */   FHT multiply(FHT fht, boolean conjugate)
/*     */   {
/* 489 */     float[] h1 = (float[])getPixels();
/* 490 */     float[] h2 = (float[])fht.getPixels();
/* 491 */     float[] tmp = new float[this.maxN * this.maxN];
/* 492 */     for (int r = 0; r < this.maxN; r++) {
/* 493 */       int rowMod = (this.maxN - r) % this.maxN;
/* 494 */       for (int c = 0; c < this.maxN; c++) {
/* 495 */         int colMod = (this.maxN - c) % this.maxN;
/* 496 */         double h2e = (h2[(r * this.maxN + c)] + h2[(rowMod * this.maxN + colMod)]) / 2.0F;
/* 497 */         double h2o = (h2[(r * this.maxN + c)] - h2[(rowMod * this.maxN + colMod)]) / 2.0F;
/* 498 */         if (conjugate)
/* 499 */           tmp[(r * this.maxN + c)] = ((float)(h1[(r * this.maxN + c)] * h2e - h1[(rowMod * this.maxN + colMod)] * h2o));
/*     */         else
/* 501 */           tmp[(r * this.maxN + c)] = ((float)(h1[(r * this.maxN + c)] * h2e + h1[(rowMod * this.maxN + colMod)] * h2o));
/*     */       }
/*     */     }
/* 504 */     FHT fht2 = new FHT(new FloatProcessor(this.maxN, this.maxN, tmp, null));
/* 505 */     fht2.isFrequencyDomain = true;
/* 506 */     return fht2;
/*     */   }
/*     */ 
/*     */   public FHT divide(FHT fht)
/*     */   {
/* 516 */     float[] h1 = (float[])getPixels();
/* 517 */     float[] h2 = (float[])fht.getPixels();
/* 518 */     float[] out = new float[this.maxN * this.maxN];
/* 519 */     for (int r = 0; r < this.maxN; r++) {
/* 520 */       int rowMod = (this.maxN - r) % this.maxN;
/* 521 */       for (int c = 0; c < this.maxN; c++) {
/* 522 */         int colMod = (this.maxN - c) % this.maxN;
/* 523 */         double mag = h2[(r * this.maxN + c)] * h2[(r * this.maxN + c)] + h2[(rowMod * this.maxN + colMod)] * h2[(rowMod * this.maxN + colMod)];
/* 524 */         if (mag < 1.0E-20D)
/* 525 */           mag = 1.0E-20D;
/* 526 */         double h2e = h2[(r * this.maxN + c)] + h2[(rowMod * this.maxN + colMod)];
/* 527 */         double h2o = h2[(r * this.maxN + c)] - h2[(rowMod * this.maxN + colMod)];
/* 528 */         double tmp = h1[(r * this.maxN + c)] * h2e - h1[(rowMod * this.maxN + colMod)] * h2o;
/* 529 */         out[(r * this.maxN + c)] = ((float)(tmp / mag));
/*     */       }
/*     */     }
/* 532 */     FHT fht2 = new FHT(new FloatProcessor(this.maxN, this.maxN, out, null));
/* 533 */     fht2.isFrequencyDomain = true;
/* 534 */     return fht2;
/*     */   }
/*     */ 
/*     */   public void setShowProgress(boolean showProgress)
/*     */   {
/* 539 */     this.showProgress = showProgress;
/*     */   }
/*     */ 
/*     */   public FHT getCopy()
/*     */   {
/* 544 */     ImageProcessor ip = super.duplicate();
/* 545 */     FHT fht = new FHT(ip);
/* 546 */     fht.isFrequencyDomain = this.isFrequencyDomain;
/* 547 */     fht.quadrantSwapNeeded = this.quadrantSwapNeeded;
/* 548 */     fht.rgb = this.rgb;
/* 549 */     fht.originalWidth = this.originalWidth;
/* 550 */     fht.originalHeight = this.originalHeight;
/* 551 */     fht.originalBitDepth = this.originalBitDepth;
/* 552 */     fht.originalColorModel = this.originalColorModel;
/* 553 */     return fht;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 558 */     return "FHT, " + getWidth() + "x" + getHeight() + ", fd=" + this.isFrequencyDomain;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.FHT
 * JD-Core Version:    0.6.2
 */