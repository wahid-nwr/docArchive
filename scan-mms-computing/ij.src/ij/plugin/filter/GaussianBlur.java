/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class GaussianBlur
/*     */   implements ExtendedPlugInFilter, DialogListener
/*     */ {
/*  43 */   private static double sigma = 2.0D;
/*     */ 
/*  45 */   private static boolean sigmaScaled = false;
/*     */ 
/*  47 */   private int flags = 16777311;
/*     */   private ImagePlus imp;
/*  49 */   private boolean hasScale = false;
/*  50 */   private int nPasses = 1;
/*  51 */   private int nChannels = 1;
/*     */   private int pass;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  61 */     this.imp = imp;
/*  62 */     if ((imp != null) && (imp.getRoi() != null)) {
/*  63 */       Rectangle roiRect = imp.getRoi().getBoundingRect();
/*  64 */       if ((roiRect.y > 0) || (roiRect.y + roiRect.height < imp.getDimensions()[1]))
/*  65 */         this.flags |= 16384;
/*     */     }
/*  67 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr)
/*     */   {
/*  73 */     String options = Macro.getOptions();
/*  74 */     boolean oldMacro = false;
/*  75 */     this.nChannels = imp.getProcessor().getNChannels();
/*  76 */     if ((options != null) && 
/*  77 */       (options.indexOf("radius=") >= 0)) {
/*  78 */       oldMacro = true;
/*  79 */       Macro.setOptions(options.replaceAll("radius=", "sigma="));
/*     */     }
/*     */ 
/*  82 */     GenericDialog gd = new GenericDialog(command);
/*  83 */     sigma = Math.abs(sigma);
/*  84 */     gd.addNumericField("Sigma (Radius)", sigma, 2);
/*  85 */     if ((imp.getCalibration() != null) && (!imp.getCalibration().getUnits().equals("pixels"))) {
/*  86 */       this.hasScale = true;
/*  87 */       gd.addCheckbox("Scaled Units (" + imp.getCalibration().getUnits() + ")", sigmaScaled);
/*     */     } else {
/*  89 */       sigmaScaled = false;
/*  90 */     }gd.addPreviewCheckbox(pfr);
/*  91 */     gd.addDialogListener(this);
/*  92 */     gd.showDialog();
/*  93 */     if (gd.wasCanceled()) return 4096;
/*  94 */     if (oldMacro) sigma /= 2.5D;
/*  95 */     IJ.register(getClass());
/*  96 */     return IJ.setupDialog(imp, this.flags);
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
/*     */   {
/* 101 */     sigma = gd.getNextNumber();
/* 102 */     if ((sigma < 0.0D) || (gd.invalidNumber()))
/* 103 */       return false;
/* 104 */     if (this.hasScale)
/* 105 */       sigmaScaled = gd.getNextBoolean();
/* 106 */     return true;
/*     */   }
/*     */ 
/*     */   public void setNPasses(int nPasses)
/*     */   {
/* 116 */     this.nPasses = (2 * this.nChannels * nPasses);
/* 117 */     this.pass = 0;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip)
/*     */   {
/* 125 */     double sigmaX = sigmaScaled ? sigma / this.imp.getCalibration().pixelWidth : sigma;
/* 126 */     double sigmaY = sigmaScaled ? sigma / this.imp.getCalibration().pixelHeight : sigma;
/* 127 */     double accuracy = ((ip instanceof ByteProcessor)) || ((ip instanceof ColorProcessor)) ? 0.002D : 0.0002D;
/*     */ 
/* 129 */     Rectangle roi = ip.getRoi();
/* 130 */     blurGaussian(ip, sigmaX, sigmaY, accuracy);
/*     */   }
/*     */ 
/*     */   public boolean blur(ImageProcessor ip, double radius)
/*     */   {
/* 137 */     Rectangle roi = ip.getRoi();
/* 138 */     if ((roi.height != ip.getHeight()) && (ip.getMask() == null))
/* 139 */       ip.snapshot();
/* 140 */     blurGaussian(ip, 0.4D * radius, 0.4D * radius, 0.01D);
/* 141 */     return true;
/*     */   }
/*     */ 
/*     */   public void blurGaussian(ImageProcessor ip, double sigmaX, double sigmaY, double accuracy)
/*     */   {
/* 153 */     if (this.nPasses <= 1)
/* 154 */       this.nPasses = (ip.getNChannels() * ((sigmaX > 0.0D) && (sigmaY > 0.0D) ? 2 : 1));
/* 155 */     FloatProcessor fp = null;
/* 156 */     for (int i = 0; i < ip.getNChannels(); i++) {
/* 157 */       fp = ip.toFloat(i, fp);
/* 158 */       if (Thread.currentThread().isInterrupted()) return;
/* 159 */       blurFloat(fp, sigmaX, sigmaY, accuracy);
/* 160 */       if (Thread.currentThread().isInterrupted()) return;
/* 161 */       ip.setPixels(i, fp);
/*     */     }
/* 163 */     if ((ip.getRoi().height != ip.getHeight()) && (sigmaX > 0.0D) && (sigmaY > 0.0D))
/* 164 */       resetOutOfRoi(ip, (int)Math.ceil(5.0D * sigmaY));
/*     */   }
/*     */ 
/*     */   public void blurFloat(FloatProcessor ip, double sigmaX, double sigmaY, double accuracy)
/*     */   {
/* 179 */     if (sigmaX > 0.0D)
/* 180 */       blur1Direction(ip, sigmaX, accuracy, true, (int)Math.ceil(5.0D * sigmaY));
/* 181 */     if (Thread.currentThread().isInterrupted()) return;
/* 182 */     if (sigmaY > 0.0D)
/* 183 */       blur1Direction(ip, sigmaY, accuracy, false, 0);
/*     */   }
/*     */ 
/*     */   public void blur1Direction(FloatProcessor ip, double sigma, double accuracy, boolean xDirection, int extraLines)
/*     */   {
/* 198 */     int UPSCALE_K_RADIUS = 2;
/* 199 */     double MIN_DOWNSCALED_SIGMA = 4.0D;
/* 200 */     final float[] pixels = (float[])ip.getPixels();
/* 201 */     int width = ip.getWidth();
/* 202 */     int height = ip.getHeight();
/* 203 */     Rectangle roi = ip.getRoi();
/* 204 */     final int length = xDirection ? width : height;
/* 205 */     final int pointInc = xDirection ? 1 : width;
/* 206 */     final int lineInc = xDirection ? width : 1;
/* 207 */     int lineFromA = (xDirection ? roi.y : roi.x) - extraLines;
/*     */     int lineFrom;
/*     */     final int lineFrom;
/* 209 */     if (lineFromA < 0) lineFrom = 0; else
/* 210 */       lineFrom = lineFromA;
/* 211 */     int lineToA = (xDirection ? roi.y + roi.height : roi.x + roi.width) + extraLines;
/*     */     int lineTo;
/*     */     final int lineTo;
/* 213 */     if (lineToA > (xDirection ? height : width)) lineTo = xDirection ? height : width; else
/* 214 */       lineTo = lineToA;
/* 215 */     final int writeFrom = xDirection ? roi.x : roi.y;
/* 216 */     final int writeTo = xDirection ? roi.x + roi.width : roi.y + roi.height;
/* 217 */     int inc = Math.max((lineTo - lineFrom) / (100 / (this.nPasses > 0 ? this.nPasses : 1) + 1), 20);
/* 218 */     this.pass += 1;
/* 219 */     if (this.pass > this.nPasses) this.pass = 1;
/*     */ 
/* 221 */     final int numThreads = Math.min(Prefs.getThreads(), lineTo - lineFrom);
/* 222 */     final Thread[] lineThreads = new Thread[numThreads];
/*     */ 
/* 225 */     final boolean doDownscaling = sigma > 8.5D;
/* 226 */     final int reduceBy = doDownscaling ? Math.min((int)Math.floor(sigma / 4.0D), length) : 1;
/*     */ 
/* 233 */     double sigmaGauss = doDownscaling ? Math.sqrt(sigma * sigma / (reduceBy * reduceBy) - 0.3333333333333333D - 0.25D) : sigma;
/*     */ 
/* 236 */     int maxLength = doDownscaling ? (length + reduceBy - 1) / reduceBy + 6 : length;
/*     */ 
/* 239 */     final float[][] gaussKernel = makeGaussianKernel(sigmaGauss, accuracy, maxLength);
/* 240 */     int kRadius = gaussKernel[0].length * reduceBy;
/* 241 */     final int readFrom = writeFrom - kRadius < 0 ? 0 : writeFrom - kRadius;
/* 242 */     final int readTo = writeTo + kRadius > length ? length : writeTo + kRadius;
/* 243 */     final int newLength = doDownscaling ? (readTo - readFrom + reduceBy - 1) / reduceBy + 6 : length;
/*     */ 
/* 246 */     final int unscaled0 = readFrom - 3 * reduceBy;
/*     */ 
/* 249 */     final float[] downscaleKernel = doDownscaling ? makeDownscaleKernel(reduceBy) : null;
/* 250 */     final float[] upscaleKernel = doDownscaling ? makeUpscaleKernel(reduceBy) : null;
/*     */ 
/* 252 */     for (int t = 0; t < numThreads; t++) {
/* 253 */       final int ti = t;
/* 254 */       final float[] cache1 = new float[newLength];
/* 255 */       final float[] cache2 = doDownscaling ? new float[newLength] : null;
/*     */ 
/* 257 */       Thread thread = new Thread(new Runnable()
/*     */       {
/*     */         public final void run() {
/* 260 */           long lastTime = System.currentTimeMillis();
/* 261 */           boolean canShowProgress = Thread.currentThread() == lineThreads[0];
/* 262 */           int pixel0 = (lineFrom + ti) * lineInc;
/* 263 */           for (int line = lineFrom + ti; line < lineTo; pixel0 += numThreads * lineInc) {
/* 264 */             long time = System.currentTimeMillis();
/* 265 */             if (time - lastTime > 110L) {
/* 266 */               if (canShowProgress)
/* 267 */                 GaussianBlur.this.showProgress((line - lineFrom) / (lineTo - lineFrom));
/* 268 */               if (Thread.currentThread().isInterrupted()) return;
/* 269 */               lastTime = time;
/*     */             }
/* 271 */             if (doDownscaling) {
/* 272 */               GaussianBlur.downscaleLine(pixels, cache1, downscaleKernel, reduceBy, pixel0, unscaled0, length, pointInc, newLength);
/* 273 */               GaussianBlur.convolveLine(cache1, cache2, gaussKernel, 0, newLength, 1, newLength - 1, 0, 1);
/* 274 */               GaussianBlur.upscaleLine(cache2, pixels, upscaleKernel, reduceBy, pixel0, unscaled0, writeFrom, writeTo, pointInc);
/*     */             } else {
/* 276 */               int p = pixel0 + readFrom * pointInc;
/* 277 */               for (int i = readFrom; i < readTo; p += pointInc) {
/* 278 */                 cache1[i] = pixels[p];
/*     */ 
/* 277 */                 i++;
/*     */               }
/* 279 */               GaussianBlur.convolveLine(cache1, pixels, gaussKernel, readFrom, readTo, writeFrom, writeTo, pixel0, pointInc);
/*     */             }
/* 263 */             line += numThreads;
/*     */           }
/*     */         }
/*     */       }
/*     */       , "GaussianBlur-" + t);
/*     */ 
/* 287 */       thread.setPriority(Thread.currentThread().getPriority());
/* 288 */       lineThreads[ti] = thread;
/* 289 */       thread.start();
/*     */     }
/*     */     try {
/* 292 */       for (Thread thread : lineThreads)
/* 293 */         if (thread != null) thread.join(); 
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/* 296 */       for (Thread thread : lineThreads)
/* 297 */         thread.interrupt();
/*     */       try {
/* 299 */         for (Thread thread : lineThreads)
/* 300 */           thread.join();
/*     */       } catch (InterruptedException f) {
/*     */       }
/* 303 */       Thread.currentThread().interrupt();
/*     */     }
/*     */ 
/* 306 */     showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   private static final void downscaleLine(float[] pixels, float[] cache, float[] kernel, int reduceBy, int pixel0, int unscaled0, int length, int pointInc, int newLength)
/*     */   {
/* 328 */     int p = pixel0 + pointInc * (unscaled0 - reduceBy * 3 / 2);
/* 329 */     int pLast = pixel0 + pointInc * (length - 1);
/* 330 */     for (int xout = -1; xout <= newLength; xout++) {
/* 331 */       float sum0 = 0.0F; float sum1 = 0.0F; float sum2 = 0.0F;
/* 332 */       for (int x = 0; x < reduceBy; p += pointInc) {
/* 333 */         float v = pixels[p];
/* 334 */         sum0 += v * kernel[(x + 2 * reduceBy)];
/* 335 */         sum1 += v * kernel[(x + reduceBy)];
/* 336 */         sum2 += v * kernel[x];
/*     */ 
/* 332 */         x++;
/*     */       }
/*     */ 
/* 338 */       if (xout > 0) cache[(xout - 1)] += sum0;
/* 339 */       if ((xout >= 0) && (xout < newLength)) cache[xout] += sum1;
/* 340 */       if (xout + 1 < newLength) cache[(xout + 1)] = sum2;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final float[] makeDownscaleKernel(int unitLength)
/*     */   {
/* 373 */     int mid = unitLength * 3 / 2;
/* 374 */     float[] kernel = new float[3 * unitLength];
/* 375 */     for (int i = 0; i <= unitLength / 2; i++) {
/* 376 */       double x = i / unitLength;
/* 377 */       float v = (float)((0.75D - x * x) / unitLength);
/* 378 */       kernel[(mid - i)] = v;
/* 379 */       kernel[(mid + i)] = v;
/*     */     }
/* 381 */     for (int i = unitLength / 2 + 1; i < (unitLength * 3 + 1) / 2; i++) {
/* 382 */       double x = i / unitLength;
/* 383 */       float v = (float)((0.125D + 0.5D * (x - 1.0D) * (x - 2.0D)) / unitLength);
/* 384 */       kernel[(mid - i)] = v;
/* 385 */       kernel[(mid + i)] = v;
/*     */     }
/* 387 */     return kernel;
/*     */   }
/*     */ 
/*     */   private static final void upscaleLine(float[] cache, float[] pixels, float[] kernel, int reduceBy, int pixel0, int unscaled0, int writeFrom, int writeTo, int pointInc)
/*     */   {
/* 395 */     int p = pixel0 + pointInc * writeFrom;
/* 396 */     for (int xout = writeFrom; xout < writeTo; p += pointInc) {
/* 397 */       int xin = (xout - unscaled0 + reduceBy - 1) / reduceBy;
/* 398 */       int x = reduceBy - 1 - (xout - unscaled0 + reduceBy - 1) % reduceBy;
/* 399 */       pixels[p] = (cache[(xin - 2)] * kernel[x] + cache[(xin - 1)] * kernel[(x + reduceBy)] + cache[xin] * kernel[(x + 2 * reduceBy)] + cache[(xin + 1)] * kernel[(x + 3 * reduceBy)]);
/*     */ 
/* 396 */       xout++;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final float[] makeUpscaleKernel(int unitLength)
/*     */   {
/* 415 */     float[] kernel = new float[4 * unitLength];
/* 416 */     int mid = 2 * unitLength;
/* 417 */     kernel[0] = 0.0F;
/* 418 */     for (int i = 0; i < unitLength; i++) {
/* 419 */       double x = i / unitLength;
/* 420 */       float v = (float)(0.6666666666666666D - x * x * (1.0D - 0.5D * x));
/* 421 */       kernel[(mid + i)] = v;
/* 422 */       kernel[(mid - i)] = v;
/*     */     }
/* 424 */     for (int i = unitLength; i < 2 * unitLength; i++) {
/* 425 */       double x = i / unitLength;
/* 426 */       float v = (float)((2.0D - x) * (2.0D - x) * (2.0D - x) / 6.0D);
/* 427 */       kernel[(mid + i)] = v;
/* 428 */       kernel[(mid - i)] = v;
/*     */     }
/* 430 */     return kernel;
/*     */   }
/*     */ 
/*     */   private static final void convolveLine(float[] input, float[] pixels, float[][] kernel, int readFrom, int readTo, int writeFrom, int writeTo, int point0, int pointInc)
/*     */   {
/* 457 */     int length = input.length;
/* 458 */     float first = input[0];
/* 459 */     float last = input[(length - 1)];
/* 460 */     float[] kern = kernel[0];
/* 461 */     float kern0 = kern[0];
/* 462 */     float[] kernSum = kernel[1];
/* 463 */     int kRadius = kern.length;
/* 464 */     int firstPart = kRadius < length ? kRadius : length;
/* 465 */     int p = point0 + writeFrom * pointInc;
/* 466 */     int i = writeFrom;
/* 467 */     for (; i < firstPart; p += pointInc) {
/* 468 */       float result = input[i] * kern0;
/* 469 */       result += kernSum[i] * first;
/* 470 */       if (i + kRadius > length) result += kernSum[(length - i - 1)] * last;
/* 471 */       for (int k = 1; k < kRadius; k++) {
/* 472 */         float v = 0.0F;
/* 473 */         if (i - k >= 0) v += input[(i - k)];
/* 474 */         if (i + k < length) v += input[(i + k)];
/* 475 */         result += kern[k] * v;
/*     */       }
/* 477 */       pixels[p] = result;
/*     */ 
/* 467 */       i++;
/*     */     }
/*     */ 
/* 479 */     int iEndInside = length - kRadius < writeTo ? length - kRadius : writeTo;
/* 480 */     for (; i < iEndInside; p += pointInc) {
/* 481 */       float result = input[i] * kern0;
/* 482 */       for (int k = 1; k < kRadius; k++)
/* 483 */         result += kern[k] * (input[(i - k)] + input[(i + k)]);
/* 484 */       pixels[p] = result;
/*     */ 
/* 480 */       i++;
/*     */     }
/*     */ 
/* 486 */     for (; i < writeTo; p += pointInc) {
/* 487 */       float result = input[i] * kern0;
/* 488 */       if (i < kRadius) result += kernSum[i] * first;
/* 489 */       if (i + kRadius >= length) result += kernSum[(length - i - 1)] * last;
/* 490 */       for (int k = 1; k < kRadius; k++) {
/* 491 */         float v = 0.0F;
/* 492 */         if (i - k >= 0) v += input[(i - k)];
/* 493 */         if (i + k < length) v += input[(i + k)];
/* 494 */         result += kern[k] * v;
/*     */       }
/* 496 */       pixels[p] = result;
/*     */ 
/* 486 */       i++;
/*     */     }
/*     */   }
/*     */ 
/*     */   public float[][] makeGaussianKernel(double sigma, double accuracy, int maxRadius)
/*     */   {
/* 526 */     int kRadius = (int)Math.ceil(sigma * Math.sqrt(-2.0D * Math.log(accuracy))) + 1;
/* 527 */     if (maxRadius < 50) maxRadius = 50;
/* 528 */     if (kRadius > maxRadius) kRadius = maxRadius;
/* 529 */     float[][] kernel = new float[2][kRadius];
/* 530 */     for (int i = 0; i < kRadius; i++)
/* 531 */       kernel[0][i] = ((float)Math.exp(-0.5D * i * i / sigma / sigma));
/* 532 */     if ((kRadius < maxRadius) && (kRadius > 3)) {
/* 533 */       double sqrtSlope = 1.7976931348623157E+308D;
/* 534 */       int r = kRadius;
/* 535 */       while (r > kRadius / 2) {
/* 536 */         r--;
/* 537 */         double a = Math.sqrt(kernel[0][r]) / (kRadius - r);
/* 538 */         if (a >= sqrtSlope) break;
/* 539 */         sqrtSlope = a;
/*     */       }
/*     */ 
/* 543 */       for (int r1 = r + 2; r1 < kRadius; r1++)
/* 544 */         kernel[0][r1] = ((float)((kRadius - r1) * (kRadius - r1) * sqrtSlope * sqrtSlope));
/*     */     }
/*     */     double sum;
/* 547 */     if (kRadius < maxRadius) {
/* 548 */       double sum = kernel[0][0];
/* 549 */       for (int i = 1; i < kRadius; i++)
/* 550 */         sum += 2.0F * kernel[0][i];
/*     */     } else {
/* 552 */       sum = sigma * Math.sqrt(6.283185307179586D);
/*     */     }
/* 554 */     double rsum = 0.5D + 0.5D * kernel[0][0] / sum;
/* 555 */     for (int i = 0; i < kRadius; i++) {
/* 556 */       double v = kernel[0][i] / sum;
/* 557 */       kernel[0][i] = ((float)v);
/* 558 */       rsum -= v;
/* 559 */       kernel[1][i] = ((float)rsum);
/*     */     }
/*     */ 
/* 562 */     return kernel;
/*     */   }
/*     */ 
/*     */   public static void resetOutOfRoi(ImageProcessor ip, int radius)
/*     */   {
/* 573 */     Rectangle roi = ip.getRoi();
/* 574 */     int width = ip.getWidth();
/* 575 */     int height = ip.getHeight();
/* 576 */     Object pixels = ip.getPixels();
/* 577 */     Object snapshot = ip.getSnapshotPixels();
/* 578 */     int y0 = roi.y - radius;
/* 579 */     if (y0 < 0) y0 = 0;
/* 580 */     int y = y0; for (int p = width * y + roi.x; y < roi.y; p += width) {
/* 581 */       System.arraycopy(snapshot, p, pixels, p, roi.width);
/*     */ 
/* 580 */       y++;
/*     */     }
/* 582 */     int yEnd = roi.y + roi.height + radius;
/* 583 */     if (yEnd > height) yEnd = height;
/* 584 */     int y = roi.y + roi.height; for (int p = width * y + roi.x; y < yEnd; p += width) {
/* 585 */       System.arraycopy(snapshot, p, pixels, p, roi.width);
/*     */ 
/* 584 */       y++;
/*     */     }
/*     */   }
/*     */ 
/*     */   void showProgress(double percent) {
/* 589 */     percent = (this.pass - 1) / this.nPasses + percent / this.nPasses;
/* 590 */     IJ.showProgress(percent);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.GaussianBlur
 * JD-Core Version:    0.6.2
 */