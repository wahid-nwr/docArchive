/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.plugin.ContrastEnhancer;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class RankFilters
/*     */   implements ExtendedPlugInFilter, DialogListener
/*     */ {
/*     */   public static final int MEAN = 0;
/*     */   public static final int MIN = 1;
/*     */   public static final int MAX = 2;
/*     */   public static final int VARIANCE = 3;
/*     */   public static final int MEDIAN = 4;
/*     */   public static final int OUTLIERS = 5;
/*     */   public static final int DESPECKLE = 6;
/*     */   public static final int REMOVE_NAN = 7;
/*     */   public static final int OPEN = 8;
/*     */   public static final int CLOSE = 9;
/*  19 */   private static int HIGHEST_FILTER = 9;
/*     */   private static final int BRIGHT_OUTLIERS = 0;
/*     */   private static final int DARK_OUTLIERS = 1;
/*  21 */   private static final String[] outlierStrings = { "Bright", "Dark" };
/*     */   private double radius;
/*     */   private double threshold;
/*     */   private int whichOutliers;
/*     */   private int filterType;
/*  28 */   private static double[] lastRadius = new double[HIGHEST_FILTER + 1];
/*  29 */   private static double lastThreshold = 50.0D;
/*  30 */   private static int lastWhichOutliers = 0;
/*     */ 
/*  33 */   int flags = 16777311;
/*     */   private ImagePlus imp;
/*  35 */   private int nPasses = 1;
/*     */   private PlugInFilterRunner pfr;
/*     */   private int pass;
/*  39 */   private int numThreads = Prefs.getThreads();
/*     */   private int highestYinCache;
/*     */   private boolean threadWaiting;
/*     */   private boolean copyingToCache;
/*     */ 
/*     */   private boolean isMultiStepFilter(int filterType)
/*     */   {
/*  47 */     return filterType >= 8;
/*     */   }
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  58 */     this.imp = imp;
/*  59 */     if (arg.equals("mean")) {
/*  60 */       this.filterType = 0;
/*  61 */     } else if (arg.equals("min")) {
/*  62 */       this.filterType = 1;
/*  63 */     } else if (arg.equals("max")) {
/*  64 */       this.filterType = 2;
/*  65 */     } else if (arg.equals("variance")) {
/*  66 */       this.filterType = 3;
/*  67 */       this.flags |= 65536;
/*  68 */     } else if (arg.equals("median")) {
/*  69 */       this.filterType = 4;
/*  70 */     } else if (arg.equals("outliers")) {
/*  71 */       this.filterType = 5;
/*  72 */     } else if (arg.equals("despeckle")) {
/*  73 */       this.filterType = 6;
/*  74 */     } else if (arg.equals("close")) {
/*  75 */       this.filterType = 9;
/*  76 */     } else if (arg.equals("open")) {
/*  77 */       this.filterType = 8;
/*  78 */     } else if (arg.equals("nan")) {
/*  79 */       this.filterType = 7;
/*  80 */       if ((imp != null) && (imp.getBitDepth() != 32)) {
/*  81 */         IJ.error("RankFilters", "\"Remove NaNs\" requires a 32-bit image");
/*  82 */         return 4096;
/*     */       }
/*  84 */     } else if (arg.equals("final")) {
/*  85 */       if ((imp != null) && (imp.getBitDepth() != 8) && (imp.getBitDepth() != 24) && (imp.getRoi() == null))
/*  86 */         new ContrastEnhancer().stretchHistogram(imp.getProcessor(), 0.5D); 
/*     */     } else { if (arg.equals("masks")) {
/*  88 */         showMasks();
/*  89 */         return 4096;
/*     */       }
/*  91 */       IJ.error("RankFilters", "Argument missing or undefined: " + arg);
/*  92 */       return 4096;
/*     */     }
/*  94 */     if ((isMultiStepFilter(this.filterType)) && (imp != null)) {
/*  95 */       Roi roi = imp.getRoi();
/*  96 */       if ((roi != null) && (!roi.getBounds().contains(new Rectangle(imp.getWidth(), imp.getHeight()))))
/*     */       {
/*  98 */         this.flags |= 16384;
/*     */       }
/*     */     }
/* 100 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
/* 104 */     if (this.filterType == 6) {
/* 105 */       this.filterType = 4;
/* 106 */       this.radius = 1.0D;
/*     */     } else {
/* 108 */       GenericDialog gd = new GenericDialog(command + "...");
/* 109 */       this.radius = (lastRadius[this.filterType] <= 0.0D ? 2.0D : lastRadius[this.filterType]);
/* 110 */       gd.addNumericField("Radius", this.radius, 1, 6, "pixels");
/* 111 */       int digits = imp.getType() == 2 ? 2 : 0;
/* 112 */       if (this.filterType == 5) {
/* 113 */         gd.addNumericField("Threshold", lastThreshold, digits);
/* 114 */         gd.addChoice("Which outliers", outlierStrings, outlierStrings[lastWhichOutliers]);
/* 115 */         gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#outliers");
/* 116 */       } else if (this.filterType == 7) {
/* 117 */         gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#nans");
/* 118 */       }gd.addPreviewCheckbox(pfr);
/* 119 */       gd.addDialogListener(this);
/* 120 */       gd.showDialog();
/* 121 */       if (gd.wasCanceled()) return 4096;
/* 122 */       IJ.register(getClass());
/* 123 */       if (Macro.getOptions() == null) {
/* 124 */         lastRadius[this.filterType] = this.radius;
/* 125 */         if (this.filterType == 5) {
/* 126 */           lastThreshold = this.threshold;
/* 127 */           lastWhichOutliers = this.whichOutliers;
/*     */         }
/*     */       }
/*     */     }
/* 131 */     this.pfr = pfr;
/* 132 */     this.flags = IJ.setupDialog(imp, this.flags);
/* 133 */     if ((this.flags & 0x20) != 0) {
/* 134 */       int size = imp.getWidth() * imp.getHeight();
/* 135 */       Roi roi = imp.getRoi();
/* 136 */       if (roi != null) {
/* 137 */         Rectangle roiRect = roi.getBounds();
/* 138 */         size = roiRect.width * roiRect.height;
/*     */       }
/* 140 */       double workToDo = size * this.radius;
/* 141 */       if ((this.filterType == 0) || (this.filterType == 3)) workToDo *= 0.5D;
/* 142 */       else if (this.filterType == 4) workToDo *= this.radius * 0.5D;
/* 143 */       if ((workToDo < 1000000.0D) && (imp.getImageStackSize() >= this.numThreads)) {
/* 144 */         this.numThreads = 1;
/* 145 */         this.flags |= 32768;
/*     */       }
/*     */     }
/* 148 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 152 */     this.radius = gd.getNextNumber();
/* 153 */     if (this.filterType == 5) {
/* 154 */       this.threshold = gd.getNextNumber();
/* 155 */       this.whichOutliers = gd.getNextChoiceIndex();
/*     */     }
/* 157 */     int maxRadius = (this.filterType == 4) || (this.filterType == 5) || (this.filterType == 7) ? 100 : 1000;
/* 158 */     if ((gd.invalidNumber()) || (this.radius < 0.0D) || (this.radius > maxRadius) || ((this.filterType == 5) && (this.threshold < 0.0D)))
/* 159 */       return false;
/* 160 */     return true;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/* 164 */     rank(ip, this.radius, this.filterType, this.whichOutliers, (float)this.threshold);
/* 165 */     if (IJ.escapePressed())
/* 166 */       ip.reset();
/*     */   }
/*     */ 
/*     */   public void rank(ImageProcessor ip, double radius, int filterType)
/*     */   {
/* 177 */     rank(ip, radius, filterType, 0, 50.0F);
/*     */   }
/*     */ 
/*     */   public void rank(ImageProcessor ip, double radius, int filterType, int whichOutliers, float threshold)
/*     */   {
/* 189 */     Rectangle roi = ip.getRoi();
/* 190 */     ImageProcessor mask = ip.getMask();
/* 191 */     Rectangle roi1 = null;
/* 192 */     int[] lineRadii = makeLineRadii(radius);
/* 193 */     boolean isImagePart = (roi.width < ip.getWidth()) || (roi.height < ip.getHeight());
/* 194 */     boolean[] aborted = new boolean[1];
/* 195 */     for (int ch = 0; ch < ip.getNChannels(); ch++) {
/* 196 */       int filterType1 = filterType;
/* 197 */       if (isMultiStepFilter(filterType)) {
/* 198 */         filterType1 = filterType == 8 ? 1 : 2;
/* 199 */         if (isImagePart) {
/* 200 */           int kRadius = kRadius(lineRadii);
/* 201 */           int kHeight = kHeight(lineRadii);
/* 202 */           Rectangle roiClone = (Rectangle)roi.clone();
/* 203 */           roiClone.grow(kRadius, kHeight / 2);
/* 204 */           roi1 = roiClone.intersection(new Rectangle(ip.getWidth(), ip.getHeight()));
/* 205 */           ip.setRoi(roi1);
/*     */         }
/*     */       }
/* 208 */       doFiltering(ip, lineRadii, filterType1, whichOutliers, threshold, ch, aborted);
/* 209 */       if (aborted[0] != 0) break;
/* 210 */       if (isMultiStepFilter(filterType)) {
/* 211 */         ip.setRoi(roi);
/* 212 */         ip.setMask(mask);
/* 213 */         int filterType2 = filterType == 8 ? 2 : 1;
/* 214 */         doFiltering(ip, lineRadii, filterType2, whichOutliers, threshold, ch, aborted);
/* 215 */         if (aborted[0] != 0) break;
/* 216 */         if (isImagePart)
/* 217 */           resetRoiBoundary(ip, roi, roi1);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doFiltering(final ImageProcessor ip, final int[] lineRadii, final int filterType, final int whichOutliers, final float threshold, final int colorChannel, final boolean[] aborted)
/*     */   {
/* 230 */     Rectangle roi = ip.getRoi();
/* 231 */     int width = ip.getWidth();
/* 232 */     Object pixels = ip.getPixels();
/*     */ 
/* 234 */     int numThreads = Math.min(roi.height, this.numThreads);
/*     */ 
/* 236 */     int kHeight = kHeight(lineRadii);
/* 237 */     int kRadius = kRadius(lineRadii);
/* 238 */     final int cacheWidth = roi.width + 2 * kRadius;
/* 239 */     final int cacheHeight = kHeight + (numThreads > 1 ? 2 * numThreads : 0);
/*     */ 
/* 241 */     final float[] cache = new float[cacheWidth * cacheHeight];
/* 242 */     this.highestYinCache = (Math.max(roi.y - kHeight / 2, 0) - 1);
/*     */ 
/* 244 */     final int[] yForThread = new int[numThreads];
/* 245 */     Arrays.fill(yForThread, -1);
/* 246 */     yForThread[(numThreads - 1)] = (roi.y - 1);
/*     */ 
/* 248 */     Thread[] threads = new Thread[numThreads];
/* 249 */     for (int t = numThreads - 1; t > 0; t--) {
/* 250 */       final int ti = t;
/* 251 */       Thread thread = new Thread(new Runnable()
/*     */       {
/*     */         public final void run() {
/* 254 */           RankFilters.this.doFiltering(ip, lineRadii, cache, cacheWidth, cacheHeight, filterType, whichOutliers, threshold, colorChannel, yForThread, ti, aborted);
/*     */         }
/*     */       }
/*     */       , "RankFilters-" + t);
/*     */ 
/* 260 */       thread.setPriority(Thread.currentThread().getPriority());
/* 261 */       thread.start();
/* 262 */       threads[ti] = thread;
/*     */     }
/*     */ 
/* 265 */     doFiltering(ip, lineRadii, cache, cacheWidth, cacheHeight, filterType, whichOutliers, threshold, colorChannel, yForThread, 0, aborted);
/*     */     try
/*     */     {
/* 269 */       for (Thread thread : threads)
/* 270 */         if (thread != null) thread.join(); 
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/* 273 */       aborted[0] = true;
/*     */     }
/* 275 */     showProgress(1.0D, ip instanceof ColorProcessor);
/* 276 */     this.pass += 1;
/*     */   }
/*     */ 
/*     */   private void doFiltering(ImageProcessor ip, int[] lineRadii, float[] cache, int cacheWidth, int cacheHeight, int filterType, int whichOutliers, float threshold, int colorChannel, int[] yForThread, int threadNumber, boolean[] aborted)
/*     */   {
/* 305 */     if ((aborted[0] != 0) || (Thread.currentThread().isInterrupted())) return;
/* 306 */     int width = ip.getWidth();
/* 307 */     int height = ip.getHeight();
/* 308 */     Rectangle roi = ip.getRoi();
/*     */ 
/* 310 */     int kHeight = kHeight(lineRadii);
/* 311 */     int kRadius = kRadius(lineRadii);
/* 312 */     int kNPoints = kNPoints(lineRadii);
/*     */ 
/* 314 */     int xmin = roi.x - kRadius;
/* 315 */     int xmax = roi.x + roi.width + kRadius;
/* 316 */     int[] cachePointers = makeCachePointers(lineRadii, cacheWidth);
/*     */ 
/* 318 */     int padLeft = xmin < 0 ? -xmin : 0;
/* 319 */     int padRight = xmax > width ? xmax - width : 0;
/* 320 */     int xminInside = xmin > 0 ? xmin : 0;
/* 321 */     int xmaxInside = xmax < width ? xmax : width;
/* 322 */     int widthInside = xmaxInside - xminInside;
/*     */ 
/* 324 */     boolean minOrMax = (filterType == 1) || (filterType == 2);
/* 325 */     boolean minOrMaxOrOutliers = (minOrMax) || (filterType == 5);
/* 326 */     boolean sumFilter = (filterType == 0) || (filterType == 3);
/* 327 */     boolean medianFilter = (filterType == 4) || (filterType == 5);
/* 328 */     double[] sums = sumFilter ? new double[2] : null;
/* 329 */     float[] medianBuf1 = (medianFilter) || (filterType == 7) ? new float[kNPoints] : null;
/* 330 */     float[] medianBuf2 = (medianFilter) || (filterType == 7) ? new float[kNPoints] : null;
/* 331 */     float sign = filterType == 1 ? -1.0F : 1.0F;
/* 332 */     if (filterType == 5)
/* 333 */       sign = ip.isInvertedLut() == (whichOutliers == 1) ? -1.0F : 1.0F;
/* 334 */     boolean smallKernel = kRadius < 2;
/*     */ 
/* 336 */     Object pixels = ip.getPixels();
/* 337 */     boolean isFloat = pixels instanceof float[];
/* 338 */     float maxValue = isFloat ? (0.0F / 0.0F) : (float)ip.maxValue();
/* 339 */     float[] values = isFloat ? (float[])pixels : new float[roi.width];
/*     */ 
/* 341 */     int numThreads = yForThread.length;
/* 342 */     long lastTime = System.currentTimeMillis();
/* 343 */     int previousY = kHeight / 2 - cacheHeight;
/* 344 */     boolean rgb = ip instanceof ColorProcessor;
/*     */ 
/* 346 */     while (aborted[0] == 0) {
/* 347 */       int y = arrayMax(yForThread) + 1;
/* 348 */       yForThread[threadNumber] = y;
/*     */ 
/* 350 */       boolean threadFinished = y >= roi.y + roi.height;
/* 351 */       if ((numThreads > 1) && ((this.threadWaiting) || (threadFinished))) {
/* 352 */         synchronized (this) {
/* 353 */           notifyAll();
/*     */         }
/*     */       }
/* 356 */       if (threadFinished) return;
/*     */ 
/* 358 */       if (threadNumber == 0) {
/* 359 */         long time = System.currentTimeMillis();
/* 360 */         if (time - lastTime > 100L) {
/* 361 */           lastTime = time;
/* 362 */           showProgress((y - roi.y) / roi.height, rgb);
/* 363 */           if ((Thread.currentThread().isInterrupted()) || ((this.imp != null) && (IJ.escapePressed()))) {
/* 364 */             aborted[0] = true;
/* 365 */             synchronized (this) { notifyAll(); }
/* 366 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 371 */       for (int i = 0; i < cachePointers.length; i++)
/* 372 */         cachePointers[i] = ((cachePointers[i] + cacheWidth * (y - previousY)) % cache.length);
/* 373 */       previousY = y;
/*     */ 
/* 375 */       if (numThreads > 1) {
/* 376 */         int slowestThreadY = arrayMinNonNegative(yForThread);
/* 377 */         if (y - slowestThreadY + kHeight > cacheHeight) {
/* 378 */           synchronized (this) {
/*     */             do {
/* 380 */               notifyAll();
/* 381 */               this.threadWaiting = true;
/*     */               try
/*     */               {
/* 384 */                 wait();
/* 385 */                 if (aborted[0] != 0) return; 
/*     */               }
/* 387 */               catch (InterruptedException e) { aborted[0] = true;
/* 388 */                 notifyAll();
/* 389 */                 return;
/*     */               }
/* 391 */               slowestThreadY = arrayMinNonNegative(yForThread);
/* 392 */             }while (y - slowestThreadY + kHeight > cacheHeight);
/* 393 */             this.threadWaiting = false;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 398 */       if (numThreads == 1) {
/* 399 */         int yStartReading = y == roi.y ? Math.max(roi.y - kHeight / 2, 0) : y + kHeight / 2;
/* 400 */         for (int yNew = yStartReading; yNew <= y + kHeight / 2; yNew++) {
/* 401 */           readLineToCacheOrPad(pixels, width, height, roi.y, xminInside, widthInside, cache, cacheWidth, cacheHeight, padLeft, padRight, colorChannel, kHeight, yNew);
/*     */         }
/*     */ 
/*     */       }
/* 405 */       else if ((!this.copyingToCache) || (this.highestYinCache < y + kHeight / 2)) { synchronized (cache) {
/* 406 */           this.copyingToCache = true;
/* 407 */           while (this.highestYinCache < arrayMinNonNegative(yForThread) - kHeight / 2 + cacheHeight - 1) {
/* 408 */             int yNew = this.highestYinCache + 1;
/* 409 */             readLineToCacheOrPad(pixels, width, height, roi.y, xminInside, widthInside, cache, cacheWidth, cacheHeight, padLeft, padRight, colorChannel, kHeight, yNew);
/*     */ 
/* 411 */             this.highestYinCache = yNew;
/*     */           }
/* 413 */           this.copyingToCache = false;
/*     */         }
/*     */       }
/*     */ 
/* 417 */       int cacheLineP = cacheWidth * (y % cacheHeight) + kRadius;
/* 418 */       filterLine(values, width, cache, cachePointers, kNPoints, cacheLineP, roi, y, sums, medianBuf1, medianBuf2, sign, maxValue, isFloat, filterType, smallKernel, sumFilter, minOrMax, minOrMaxOrOutliers);
/*     */ 
/* 421 */       if (!isFloat)
/* 422 */         writeLineToPixels(values, pixels, roi.x + y * width, roi.width, colorChannel);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int arrayMax(int[] array)
/*     */   {
/* 428 */     int max = -2147483648;
/* 429 */     for (int i = 0; i < array.length; i++)
/* 430 */       if (array[i] > max) max = array[i];
/* 431 */     return max;
/*     */   }
/*     */ 
/*     */   private int arrayMinNonNegative(int[] array)
/*     */   {
/* 436 */     int min = 2147483647;
/* 437 */     for (int i = 0; i < array.length; i++)
/* 438 */       if ((array[i] >= 0) && (array[i] < min)) min = array[i];
/* 439 */     return min;
/*     */   }
/*     */ 
/*     */   private void filterLine(float[] values, int width, float[] cache, int[] cachePointers, int kNPoints, int cacheLineP, Rectangle roi, int y, double[] sums, float[] medianBuf1, float[] medianBuf2, float sign, float maxValue, boolean isFloat, int filterType, boolean smallKernel, boolean sumFilter, boolean minOrMax, boolean minOrMaxOrOutliers)
/*     */   {
/* 445 */     int valuesP = isFloat ? roi.x + y * width : 0;
/* 446 */     float max = 0.0F;
/* 447 */     float median = Float.isNaN(cache[cacheLineP]) ? 0.0F : cache[cacheLineP];
/* 448 */     boolean fullCalculation = true;
/* 449 */     for (int x = 0; x < roi.width; valuesP++) {
/* 450 */       if (fullCalculation) {
/* 451 */         fullCalculation = smallKernel;
/* 452 */         if (minOrMaxOrOutliers)
/* 453 */           max = getAreaMax(cache, x, cachePointers, 0, -3.402824E+38F, sign);
/* 454 */         if (minOrMax) {
/* 455 */           values[valuesP] = (max * sign);
/* 456 */           break label477;
/*     */         }
/* 458 */         if (sumFilter)
/* 459 */           getAreaSums(cache, x, cachePointers, sums);
/*     */       }
/* 461 */       else if (minOrMaxOrOutliers) {
/* 462 */         float newPointsMax = getSideMax(cache, x, cachePointers, true, sign);
/* 463 */         if (newPointsMax >= max) {
/* 464 */           max = newPointsMax;
/*     */         } else {
/* 466 */           float removedPointsMax = getSideMax(cache, x, cachePointers, false, sign);
/* 467 */           if (removedPointsMax >= max)
/* 468 */             max = getAreaMax(cache, x, cachePointers, 1, newPointsMax, sign);
/*     */         }
/* 470 */         if (minOrMax) {
/* 471 */           values[valuesP] = (max * sign);
/* 472 */           break label477;
/*     */         }
/* 474 */       } else if (sumFilter) {
/* 475 */         addSideSums(cache, x, cachePointers, sums);
/* 476 */         if (Double.isNaN(sums[0])) {
/* 477 */           fullCalculation = true;
/*     */         }
/*     */       }
/* 480 */       if (sumFilter) {
/* 481 */         if (filterType == 0) {
/* 482 */           values[valuesP] = ((float)(sums[0] / kNPoints));
/*     */         } else {
/* 484 */           float value = (float)((sums[1] - sums[0] * sums[0] / kNPoints) / kNPoints);
/* 485 */           if (value > maxValue) value = maxValue;
/* 486 */           values[valuesP] = value;
/*     */         }
/* 488 */       } else if (filterType == 4) {
/* 489 */         median = getMedian(cache, x, cachePointers, medianBuf1, medianBuf2, kNPoints, median);
/* 490 */         values[valuesP] = median;
/* 491 */       } else if (filterType == 5) {
/* 492 */         float v = cache[(cacheLineP + x)];
/* 493 */         if (v * sign + this.threshold < max) {
/* 494 */           median = getMedian(cache, x, cachePointers, medianBuf1, medianBuf2, kNPoints, median);
/* 495 */           if (v * sign + this.threshold < median * sign)
/* 496 */             v = median;
/*     */         }
/* 498 */         values[valuesP] = v;
/* 499 */       } else if (filterType == 7) {
/* 500 */         if (Float.isNaN(values[valuesP]))
/* 501 */           values[valuesP] = getNaNAwareMedian(cache, x, cachePointers, medianBuf1, medianBuf2, kNPoints, median);
/*     */         else
/* 503 */           median = values[valuesP];
/*     */       }
/* 449 */       label477: x++;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void readLineToCacheOrPad(Object pixels, int width, int height, int roiY, int xminInside, int widthInside, float[] cache, int cacheWidth, int cacheHeight, int padLeft, int padRight, int colorChannel, int kHeight, int y)
/*     */   {
/* 516 */     int lineInCache = y % cacheHeight;
/* 517 */     if (y < height) {
/* 518 */       readLineToCache(pixels, y * width, xminInside, widthInside, cache, lineInCache * cacheWidth, padLeft, padRight, colorChannel);
/*     */ 
/* 520 */       if (y == 0) for (int prevY = roiY - kHeight / 2; prevY < 0; prevY++) {
/* 521 */           int prevLineInCache = cacheHeight + prevY;
/* 522 */           System.arraycopy(cache, 0, cache, prevLineInCache * cacheWidth, cacheWidth);
/*     */         } 
/*     */     }
/* 525 */     else { System.arraycopy(cache, cacheWidth * ((height - 1) % cacheHeight), cache, lineInCache * cacheWidth, cacheWidth); }
/*     */ 
/*     */   }
/*     */ 
/*     */   private static void readLineToCache(Object pixels, int pixelLineP, int xminInside, int widthInside, float[] cache, int cacheLineP, int padLeft, int padRight, int colorChannel)
/*     */   {
/* 531 */     if ((pixels instanceof byte[])) {
/* 532 */       byte[] bPixels = (byte[])pixels;
/* 533 */       int pp = pixelLineP + xminInside; for (int cp = cacheLineP + padLeft; pp < pixelLineP + xminInside + widthInside; cp++) {
/* 534 */         cache[cp] = (bPixels[pp] & 0xFF);
/*     */ 
/* 533 */         pp++;
/*     */       }
/* 535 */     } else if ((pixels instanceof short[])) {
/* 536 */       short[] sPixels = (short[])pixels;
/* 537 */       int pp = pixelLineP + xminInside; for (int cp = cacheLineP + padLeft; pp < pixelLineP + xminInside + widthInside; cp++) {
/* 538 */         cache[cp] = (sPixels[pp] & 0xFFFF);
/*     */ 
/* 537 */         pp++;
/*     */       }
/* 539 */     } else if ((pixels instanceof float[])) {
/* 540 */       System.arraycopy(pixels, pixelLineP + xminInside, cache, cacheLineP + padLeft, widthInside);
/*     */     } else {
/* 542 */       int[] cPixels = (int[])pixels;
/* 543 */       int shift = 16 - 8 * colorChannel;
/* 544 */       int byteMask = 255 << shift;
/* 545 */       int pp = pixelLineP + xminInside; for (int cp = cacheLineP + padLeft; pp < pixelLineP + xminInside + widthInside; cp++) {
/* 546 */         cache[cp] = ((cPixels[pp] & byteMask) >> shift);
/*     */ 
/* 545 */         pp++;
/*     */       }
/*     */     }
/* 548 */     for (int cp = cacheLineP; cp < cacheLineP + padLeft; cp++)
/* 549 */       cache[cp] = cache[(cacheLineP + padLeft)];
/* 550 */     for (int cp = cacheLineP + padLeft + widthInside; cp < cacheLineP + padLeft + widthInside + padRight; cp++)
/* 551 */       cache[cp] = cache[(cacheLineP + padLeft + widthInside - 1)];
/*     */   }
/*     */ 
/*     */   private static void writeLineToPixels(float[] values, Object pixels, int pixelP, int length, int colorChannel)
/*     */   {
/* 558 */     if ((pixels instanceof byte[])) {
/* 559 */       byte[] bPixels = (byte[])pixels;
/* 560 */       int i = 0; for (int p = pixelP; i < length; p++) {
/* 561 */         bPixels[p] = ((byte)((int)(values[i] + 0.5F) & 0xFF));
/*     */ 
/* 560 */         i++;
/*     */       }
/* 562 */     } else if ((pixels instanceof short[])) {
/* 563 */       short[] sPixels = (short[])pixels;
/* 564 */       int i = 0; for (int p = pixelP; i < length; p++) {
/* 565 */         sPixels[p] = ((short)((int)(values[i] + 0.5F) & 0xFFFF));
/*     */ 
/* 564 */         i++;
/*     */       }
/*     */     } else {
/* 567 */       int[] cPixels = (int[])pixels;
/* 568 */       int shift = 16 - 8 * colorChannel;
/* 569 */       int resetMask = 0xFFFFFFFF ^ 255 << shift;
/* 570 */       int i = 0; for (int p = pixelP; i < length; p++) {
/* 571 */         cPixels[p] = (cPixels[p] & resetMask | (int)(values[i] + 0.5F) << shift);
/*     */ 
/* 570 */         i++;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static float getAreaMax(float[] cache, int xCache0, int[] kernel, int ignoreRight, float max, float sign)
/*     */   {
/* 580 */     for (int kk = 0; kk < kernel.length; kk++) {
/* 581 */       for (int p = kernel[(kk++)] + xCache0; p <= kernel[kk] + xCache0 - ignoreRight; p++) {
/* 582 */         float v = cache[p] * sign;
/* 583 */         if (max < v) max = v;
/*     */       }
/*     */     }
/* 586 */     return max;
/*     */   }
/*     */ 
/*     */   private static float getSideMax(float[] cache, int xCache0, int[] kernel, boolean isRight, float sign)
/*     */   {
/* 592 */     float max = -3.402824E+38F;
/* 593 */     if (!isRight) xCache0--;
/* 594 */     for (int kk = isRight ? 1 : 0; kk < kernel.length; kk += 2) {
/* 595 */       float v = cache[(xCache0 + kernel[kk])] * sign;
/* 596 */       if (max < v) max = v;
/*     */     }
/* 598 */     return max;
/*     */   }
/*     */ 
/*     */   private static void getAreaSums(float[] cache, int xCache0, int[] kernel, double[] sums)
/*     */   {
/* 605 */     double sum = 0.0D; double sum2 = 0.0D;
/* 606 */     for (int kk = 0; kk < kernel.length; kk++) {
/* 607 */       for (int p = kernel[(kk++)] + xCache0; p <= kernel[kk] + xCache0; p++) {
/* 608 */         float v = cache[p];
/* 609 */         sum += v;
/* 610 */         sum2 += v * v;
/*     */       }
/*     */     }
/* 613 */     sums[0] = sum;
/* 614 */     sums[1] = sum2;
/*     */   }
/*     */ 
/*     */   private static void addSideSums(float[] cache, int xCache0, int[] kernel, double[] sums)
/*     */   {
/* 622 */     double sum = 0.0D; double sum2 = 0.0D;
/* 623 */     for (int kk = 0; kk < kernel.length; ) {
/* 624 */       float v = cache[(kernel[(kk++)] + (xCache0 - 1))];
/* 625 */       sum -= v;
/* 626 */       sum2 -= v * v;
/* 627 */       v = cache[(kernel[(kk++)] + xCache0)];
/* 628 */       sum += v;
/* 629 */       sum2 += v * v;
/*     */     }
/* 631 */     sums[0] += sum;
/* 632 */     sums[1] += sum2;
/*     */   }
/*     */ 
/*     */   private static float getMedian(float[] cache, int xCache0, int[] kernel, float[] aboveBuf, float[] belowBuf, int kNPoints, float guess)
/*     */   {
/* 640 */     int nAbove = 0; int nBelow = 0;
/* 641 */     for (int kk = 0; kk < kernel.length; kk++) {
/* 642 */       for (int p = kernel[(kk++)] + xCache0; p <= kernel[kk] + xCache0; p++) {
/* 643 */         float v = cache[p];
/* 644 */         if (v > guess) {
/* 645 */           aboveBuf[nAbove] = v;
/* 646 */           nAbove++;
/*     */         }
/* 648 */         else if (v < guess) {
/* 649 */           belowBuf[nBelow] = v;
/* 650 */           nBelow++;
/*     */         }
/*     */       }
/*     */     }
/* 654 */     int half = kNPoints / 2;
/* 655 */     if (nAbove > half)
/* 656 */       return findNthLowestNumber(aboveBuf, nAbove, nAbove - half - 1);
/* 657 */     if (nBelow > half) {
/* 658 */       return findNthLowestNumber(belowBuf, nBelow, half);
/*     */     }
/* 660 */     return guess;
/*     */   }
/*     */ 
/*     */   private static float getNaNAwareMedian(float[] cache, int xCache0, int[] kernel, float[] aboveBuf, float[] belowBuf, int kNPoints, float guess)
/*     */   {
/* 668 */     int nAbove = 0; int nBelow = 0;
/* 669 */     for (int kk = 0; kk < kernel.length; kk++) {
/* 670 */       for (int p = kernel[(kk++)] + xCache0; p <= kernel[kk] + xCache0; p++) {
/* 671 */         float v = cache[p];
/* 672 */         if (Float.isNaN(v)) {
/* 673 */           kNPoints--;
/* 674 */         } else if (v > guess) {
/* 675 */           aboveBuf[nAbove] = v;
/* 676 */           nAbove++;
/*     */         }
/* 678 */         else if (v < guess) {
/* 679 */           belowBuf[nBelow] = v;
/* 680 */           nBelow++;
/*     */         }
/*     */       }
/*     */     }
/* 684 */     if (kNPoints == 0) return (0.0F / 0.0F);
/* 685 */     int half = kNPoints / 2;
/* 686 */     if (nAbove > half)
/* 687 */       return findNthLowestNumber(aboveBuf, nAbove, nAbove - half - 1);
/* 688 */     if (nBelow > half) {
/* 689 */       return findNthLowestNumber(belowBuf, nBelow, half);
/*     */     }
/* 691 */     return guess;
/*     */   }
/*     */ 
/*     */   public static final float findNthLowestNumber(float[] buf, int bufLength, int n)
/*     */   {
/* 703 */     int l = 0;
/* 704 */     int m = bufLength - 1;
/* 705 */     float med = buf[n];
/*     */ 
/* 708 */     while (l < m) {
/* 709 */       int i = l;
/* 710 */       int j = m;
/*     */       do {
/* 712 */         while (buf[i] < med) i++;
/* 713 */         while (med < buf[j]) j--;
/* 714 */         float dum = buf[j];
/* 715 */         buf[j] = buf[i];
/* 716 */         buf[i] = dum;
/* 717 */         i++; j--;
/* 718 */       }while ((j >= n) && (i <= n));
/* 719 */       if (j < n) l = i;
/* 720 */       if (n < i) m = j;
/* 721 */       med = buf[n];
/*     */     }
/* 723 */     return med;
/*     */   }
/*     */ 
/*     */   private void resetRoiBoundary(ImageProcessor ip, Rectangle roi, Rectangle roi1)
/*     */   {
/* 728 */     int width = ip.getWidth();
/* 729 */     Object pixels = ip.getPixels();
/* 730 */     Object snapshot = ip.getSnapshotPixels();
/* 731 */     int y = roi1.y; for (int p = roi1.x + y * width; y < roi.y; p += width) {
/* 732 */       System.arraycopy(snapshot, p, pixels, p, roi1.width);
/*     */ 
/* 731 */       y++;
/*     */     }
/* 733 */     int leftWidth = roi.x - roi1.x;
/* 734 */     int rightWidth = roi1.x + roi1.width - (roi.x + roi.width);
/* 735 */     int y = roi.y; int pL = roi1.x + y * width; for (int pR = roi.x + roi.width + y * width; y < roi.y + roi.height; pR += width) {
/* 736 */       if (leftWidth > 0)
/* 737 */         System.arraycopy(snapshot, pL, pixels, pL, leftWidth);
/* 738 */       if (rightWidth > 0)
/* 739 */         System.arraycopy(snapshot, pR, pixels, pR, rightWidth);
/* 735 */       y++; pL += width;
/*     */     }
/*     */ 
/* 741 */     int y = roi.y + roi.height; for (int p = roi1.x + y * width; y < roi1.y + roi1.height; p += width) {
/* 742 */       System.arraycopy(snapshot, p, pixels, p, roi1.width);
/*     */ 
/* 741 */       y++;
/*     */     }
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void makeKernel(double radius)
/*     */   {
/* 748 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */   protected int[] makeLineRadii(double radius)
/*     */   {
/* 771 */     if ((radius >= 1.5D) && (radius < 1.75D))
/* 772 */       radius = 1.75D;
/* 773 */     else if ((radius >= 2.5D) && (radius < 2.85D))
/* 774 */       radius = 2.85D;
/* 775 */     int r2 = (int)(radius * radius) + 1;
/* 776 */     int kRadius = (int)Math.sqrt(r2 + 1.0E-10D);
/* 777 */     int kHeight = 2 * kRadius + 1;
/* 778 */     int[] kernel = new int[2 * kHeight + 2];
/* 779 */     kernel[(2 * kRadius)] = (-kRadius);
/* 780 */     kernel[(2 * kRadius + 1)] = kRadius;
/* 781 */     int nPoints = 2 * kRadius + 1;
/* 782 */     for (int y = 1; y <= kRadius; y++) {
/* 783 */       int dx = (int)Math.sqrt(r2 - y * y + 1.0E-10D);
/* 784 */       kernel[(2 * (kRadius - y))] = (-dx);
/* 785 */       kernel[(2 * (kRadius - y) + 1)] = dx;
/* 786 */       kernel[(2 * (kRadius + y))] = (-dx);
/* 787 */       kernel[(2 * (kRadius + y) + 1)] = dx;
/* 788 */       nPoints += 4 * dx + 2;
/*     */     }
/* 790 */     kernel[(kernel.length - 2)] = nPoints;
/* 791 */     kernel[(kernel.length - 1)] = kRadius;
/*     */ 
/* 793 */     return kernel;
/*     */   }
/*     */ 
/*     */   private int kHeight(int[] lineRadii)
/*     */   {
/* 798 */     return (lineRadii.length - 2) / 2;
/*     */   }
/*     */ 
/*     */   private int kRadius(int[] lineRadii)
/*     */   {
/* 803 */     return lineRadii[(lineRadii.length - 1)];
/*     */   }
/*     */ 
/*     */   private int kNPoints(int[] lineRadii)
/*     */   {
/* 808 */     return lineRadii[(lineRadii.length - 2)];
/*     */   }
/*     */ 
/*     */   private int[] makeCachePointers(int[] lineRadii, int cacheWidth)
/*     */   {
/* 813 */     int kRadius = kRadius(lineRadii);
/* 814 */     int kHeight = kHeight(lineRadii);
/* 815 */     int[] cachePointers = new int[2 * kHeight];
/* 816 */     for (int i = 0; i < kHeight; i++) {
/* 817 */       cachePointers[(2 * i)] = (i * cacheWidth + kRadius + lineRadii[(2 * i)]);
/* 818 */       cachePointers[(2 * i + 1)] = (i * cacheWidth + kRadius + lineRadii[(2 * i + 1)]);
/*     */     }
/* 820 */     return cachePointers;
/*     */   }
/*     */ 
/*     */   void showMasks() {
/* 824 */     int w = 150; int h = 150;
/* 825 */     ImageStack stack = new ImageStack(w, h);
/*     */ 
/* 827 */     for (double r = 0.5D; r < 50.0D; r += 0.5D) {
/* 828 */       ImageProcessor ip = new FloatProcessor(w, h, new int[w * h]);
/* 829 */       float[] pixels = (float[])ip.getPixels();
/* 830 */       int[] lineRadii = makeLineRadii(r);
/* 831 */       int kHeight = kHeight(lineRadii);
/* 832 */       int kRadius = kRadius(lineRadii);
/* 833 */       int y0 = h / 2 - kHeight / 2;
/* 834 */       int i = 0; for (int y = y0; i < kHeight; y++) {
/* 835 */         int x = w / 2 + lineRadii[(2 * i)]; for (int p = x + y * w; x <= w / 2 + lineRadii[(2 * i + 1)]; p++) {
/* 836 */           pixels[p] = 1.0F;
/*     */ 
/* 835 */           x++;
/*     */         }
/* 834 */         i++;
/*     */       }
/*     */ 
/* 837 */       stack.addSlice("radius=" + r + ", size=" + (2 * kRadius + 1), ip);
/*     */     }
/* 839 */     new ImagePlus("Masks", stack).show();
/*     */   }
/*     */ 
/*     */   public void setNPasses(int nPasses)
/*     */   {
/* 845 */     this.nPasses = nPasses;
/* 846 */     this.pass = 0;
/*     */   }
/*     */ 
/*     */   private void showProgress(double percent, boolean rgb) {
/* 850 */     int nPasses2 = rgb ? this.nPasses * 3 : this.nPasses;
/* 851 */     percent = this.pass / nPasses2 + percent / nPasses2;
/* 852 */     IJ.showProgress(percent);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.RankFilters
 * JD-Core Version:    0.6.2
 */