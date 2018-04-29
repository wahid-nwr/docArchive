/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.AWTEvent;
/*     */ 
/*     */ public class BackgroundSubtracter
/*     */   implements ExtendedPlugInFilter, DialogListener
/*     */ {
/*  49 */   private static double radius = 50.0D;
/*  50 */   private static boolean lightBackground = Prefs.get("bs.background", true);
/*     */   private static boolean separateColors;
/*     */   private static boolean createBackground;
/*     */   private static boolean useParaboloid;
/*  54 */   private static boolean doPresmooth = true;
/*     */   private boolean isRGB;
/*     */   private boolean previewing;
/*     */   private static final int MAXIMUM = 0;
/*     */   private static final int MEAN = 1;
/*     */   private static final int X_DIRECTION = 0;
/*     */   private static final int Y_DIRECTION = 1;
/*     */   private static final int DIAGONAL_1A = 2;
/*     */   private static final int DIAGONAL_1B = 3;
/*     */   private static final int DIAGONAL_2A = 4;
/*     */   private static final int DIAGONAL_2B = 5;
/*     */   private static final int DIRECTION_PASSES = 9;
/*  62 */   private int nPasses = 9;
/*     */   private int pass;
/*  64 */   private int flags = 16875551;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp) {
/*  67 */     if (arg.equals("final")) {
/*  68 */       imp.getProcessor().resetMinAndMax();
/*  69 */       return 4096;
/*     */     }
/*  71 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
/*  75 */     this.isRGB = (imp.getProcessor() instanceof ColorProcessor);
/*  76 */     String options = Macro.getOptions();
/*  77 */     if (options != null)
/*  78 */       Macro.setOptions(options.replaceAll("white", "light"));
/*  79 */     GenericDialog gd = new GenericDialog(command);
/*  80 */     gd.addNumericField("Rolling ball radius:", radius, 1, 6, "pixels");
/*  81 */     gd.addCheckbox("Light background", lightBackground);
/*  82 */     if (this.isRGB) gd.addCheckbox("Separate colors", separateColors);
/*  83 */     gd.addCheckbox("Create background (don't subtract)", createBackground);
/*  84 */     gd.addCheckbox("Sliding paraboloid", useParaboloid);
/*  85 */     gd.addCheckbox("Disable smoothing", !doPresmooth);
/*  86 */     gd.addPreviewCheckbox(pfr);
/*  87 */     gd.addDialogListener(this);
/*  88 */     this.previewing = true;
/*  89 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#background");
/*  90 */     gd.showDialog();
/*  91 */     this.previewing = false;
/*  92 */     if (gd.wasCanceled()) return 4096;
/*  93 */     IJ.register(getClass());
/*  94 */     Prefs.set("bs.background", lightBackground);
/*  95 */     if (((imp.getProcessor() instanceof FloatProcessor)) && (!createBackground))
/*  96 */       this.flags |= 16384;
/*  97 */     return IJ.setupDialog(imp, this.flags);
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 101 */     radius = gd.getNextNumber();
/* 102 */     if ((radius <= 0.0001D) || (gd.invalidNumber()))
/* 103 */       return false;
/* 104 */     lightBackground = gd.getNextBoolean();
/* 105 */     if (this.isRGB) separateColors = gd.getNextBoolean();
/* 106 */     createBackground = gd.getNextBoolean();
/* 107 */     useParaboloid = gd.getNextBoolean();
/* 108 */     doPresmooth = !gd.getNextBoolean();
/* 109 */     return true;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip)
/*     */   {
/* 114 */     if ((this.isRGB) && (!separateColors))
/* 115 */       rollingBallBrightnessBackground((ColorProcessor)ip, radius, createBackground, lightBackground, useParaboloid, doPresmooth, true);
/*     */     else
/* 117 */       rollingBallBackground(ip, radius, createBackground, lightBackground, useParaboloid, doPresmooth, true);
/* 118 */     if ((this.previewing) && (((ip instanceof FloatProcessor)) || ((ip instanceof ShortProcessor))))
/* 119 */       ip.resetMinAndMax();
/*     */   }
/*     */ 
/*     */   public void subtractRGBBackround(ColorProcessor ip, int ballRadius)
/*     */   {
/* 125 */     rollingBallBrightnessBackground(ip, ballRadius, false, lightBackground, false, true, true);
/*     */   }
/*     */ 
/*     */   public void subtractBackround(ImageProcessor ip, int ballRadius) {
/* 129 */     rollingBallBackground(ip, ballRadius, false, lightBackground, false, true, true);
/*     */   }
/*     */ 
/*     */   public void rollingBallBrightnessBackground(ColorProcessor ip, double radius, boolean createBackground, boolean lightBackground, boolean useParaboloid, boolean doPresmooth, boolean correctCorners)
/*     */   {
/* 148 */     int width = ip.getWidth();
/* 149 */     int height = ip.getHeight();
/* 150 */     byte[] H = new byte[width * height];
/* 151 */     byte[] S = new byte[width * height];
/* 152 */     byte[] B = new byte[width * height];
/* 153 */     ip.getHSB(H, S, B);
/* 154 */     ByteProcessor bp = new ByteProcessor(width, height, B, null);
/* 155 */     rollingBallBackground(bp, radius, createBackground, lightBackground, useParaboloid, doPresmooth, correctCorners);
/* 156 */     ip.setHSB(H, S, (byte[])bp.getPixels());
/*     */   }
/*     */ 
/*     */   public void rollingBallBackground(ImageProcessor ip, double radius, boolean createBackground, boolean lightBackground, boolean useParaboloid, boolean doPresmooth, boolean correctCorners)
/*     */   {
/* 176 */     boolean invertedLut = ip.isInvertedLut();
/* 177 */     boolean invert = ((invertedLut) && (!lightBackground)) || ((!invertedLut) && (lightBackground));
/* 178 */     RollingBall ball = null;
/* 179 */     if (!useParaboloid) ball = new RollingBall(radius);
/* 180 */     FloatProcessor fp = null;
/* 181 */     for (int channelNumber = 0; channelNumber < ip.getNChannels(); channelNumber++) {
/* 182 */       fp = ip.toFloat(channelNumber, fp);
/* 183 */       if (useParaboloid)
/* 184 */         slidingParaboloidFloatBackground(fp, (float)radius, invert, doPresmooth, correctCorners);
/*     */       else {
/* 186 */         rollingBallFloatBackground(fp, (float)radius, invert, doPresmooth, ball);
/*     */       }
/* 188 */       if (createBackground) {
/* 189 */         ip.setPixels(channelNumber, fp);
/*     */       } else {
/* 191 */         float[] bgPixels = (float[])fp.getPixels();
/* 192 */         if ((ip instanceof FloatProcessor)) {
/* 193 */           float[] snapshotPixels = (float[])fp.getSnapshotPixels();
/* 194 */           for (int p = 0; p < bgPixels.length; p++)
/* 195 */             snapshotPixels[p] -= bgPixels[p];
/*     */         }
/* 197 */         else if ((ip instanceof ShortProcessor)) {
/* 198 */           float offset = invert ? 65535.5F : 0.5F;
/* 199 */           short[] pixels = (short[])ip.getPixels();
/* 200 */           for (int p = 0; p < bgPixels.length; p++) {
/* 201 */             float value = (pixels[p] & 0xFFFF) - bgPixels[p] + offset;
/* 202 */             if (value < 0.0F) value = 0.0F;
/*     */ 
/* 204 */             if (value > 65535.0F) value = 65535.0F;
/*     */ 
/* 206 */             pixels[p] = ((short)(int)value);
/*     */           }
/* 208 */         } else if ((ip instanceof ByteProcessor)) {
/* 209 */           float offset = invert ? 255.5F : 0.5F;
/* 210 */           byte[] pixels = (byte[])ip.getPixels();
/* 211 */           for (int p = 0; p < bgPixels.length; p++) {
/* 212 */             float value = (pixels[p] & 0xFF) - bgPixels[p] + offset;
/* 213 */             if (value < 0.0F) value = 0.0F;
/*     */ 
/* 215 */             if (value > 255.0F) value = 255.0F;
/*     */ 
/* 217 */             pixels[p] = ((byte)(int)value);
/*     */           }
/* 219 */         } else if ((ip instanceof ColorProcessor)) {
/* 220 */           float offset = invert ? 255.5F : 0.5F;
/* 221 */           int[] pixels = (int[])ip.getPixels();
/* 222 */           int shift = 16 - 8 * channelNumber;
/*     */ 
/* 224 */           int byteMask = 255 << shift;
/* 225 */           int resetMask = 0xFFFFFFFF ^ 255 << shift;
/*     */ 
/* 227 */           for (int p = 0; p < bgPixels.length; p++) {
/* 228 */             int pxl = pixels[p];
/* 229 */             float value = ((pxl & byteMask) >> shift) - bgPixels[p] + offset;
/* 230 */             if (value < 0.0F) value = 0.0F;
/*     */ 
/* 232 */             if (value > 255.0F) value = 255.0F;
/* 233 */             pixels[p] = (pxl & resetMask | (int)value << shift);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void slidingParaboloidFloatBackground(FloatProcessor fp, float radius, boolean invert, boolean doPresmooth, boolean correctCorners)
/*     */   {
/* 247 */     float[] pixels = (float[])fp.getPixels();
/* 248 */     int width = fp.getWidth();
/* 249 */     int height = fp.getHeight();
/* 250 */     float[] cache = new float[Math.max(width, height)];
/* 251 */     int[] nextPoint = new int[Math.max(width, height)];
/* 252 */     float coeff2 = 0.5F / radius;
/* 253 */     float coeff2diag = 1.0F / radius;
/*     */ 
/* 255 */     showProgress(1.0E-06D);
/* 256 */     if (invert) {
/* 257 */       for (int i = 0; i < pixels.length; i++)
/* 258 */         pixels[i] = (-pixels[i]);
/*     */     }
/* 260 */     float shiftBy = 0.0F;
/* 261 */     if (doPresmooth) {
/* 262 */       shiftBy = (float)filter3x3(fp, 0);
/* 263 */       showProgress(0.5D);
/* 264 */       filter3x3(fp, 1);
/* 265 */       this.pass += 1;
/*     */     }
/*     */ 
/* 269 */     if (correctCorners) {
/* 270 */       correctCorners(fp, coeff2, cache, nextPoint);
/*     */     }
/*     */ 
/* 275 */     filter1D(fp, 0, coeff2, cache, nextPoint);
/* 276 */     filter1D(fp, 1, coeff2, cache, nextPoint);
/* 277 */     filter1D(fp, 0, coeff2, cache, nextPoint);
/* 278 */     filter1D(fp, 2, coeff2diag, cache, nextPoint);
/* 279 */     filter1D(fp, 3, coeff2diag, cache, nextPoint);
/* 280 */     filter1D(fp, 4, coeff2diag, cache, nextPoint);
/* 281 */     filter1D(fp, 5, coeff2diag, cache, nextPoint);
/* 282 */     filter1D(fp, 2, coeff2diag, cache, nextPoint);
/* 283 */     filter1D(fp, 3, coeff2diag, cache, nextPoint);
/*     */ 
/* 285 */     if (invert)
/* 286 */       for (int i = 0; i < pixels.length; i++)
/* 287 */         pixels[i] = (-(pixels[i] - shiftBy));
/* 288 */     else if (doPresmooth)
/* 289 */       for (int i = 0; i < pixels.length; i++)
/* 290 */         pixels[i] -= shiftBy;
/*     */   }
/*     */ 
/*     */   void filter1D(FloatProcessor fp, int direction, float coeff2, float[] cache, int[] nextPoint)
/*     */   {
/* 297 */     float[] pixels = (float[])fp.getPixels();
/* 298 */     int width = fp.getWidth();
/* 299 */     int height = fp.getHeight();
/* 300 */     int startLine = 0;
/* 301 */     int nLines = 0;
/* 302 */     int lineInc = 0;
/* 303 */     int pointInc = 0;
/* 304 */     int length = 0;
/* 305 */     switch (direction) {
/*     */     case 0:
/* 307 */       nLines = height;
/* 308 */       lineInc = width;
/* 309 */       pointInc = 1;
/* 310 */       length = width;
/* 311 */       break;
/*     */     case 1:
/* 313 */       nLines = width;
/* 314 */       lineInc = 1;
/* 315 */       pointInc = width;
/* 316 */       length = height;
/* 317 */       break;
/*     */     case 2:
/* 319 */       nLines = width - 2;
/* 320 */       lineInc = 1;
/* 321 */       pointInc = width + 1;
/* 322 */       break;
/*     */     case 3:
/* 324 */       startLine = 1;
/* 325 */       nLines = height - 2;
/* 326 */       lineInc = width;
/* 327 */       pointInc = width + 1;
/* 328 */       break;
/*     */     case 4:
/* 330 */       startLine = 2;
/* 331 */       nLines = width;
/* 332 */       lineInc = 1;
/* 333 */       pointInc = width - 1;
/* 334 */       break;
/*     */     case 5:
/* 336 */       startLine = 0;
/* 337 */       nLines = height - 2;
/* 338 */       lineInc = width;
/* 339 */       pointInc = width - 1;
/*     */     }
/*     */ 
/* 342 */     for (int i = startLine; i < nLines; i++) {
/* 343 */       if (i % 50 == 0) {
/* 344 */         if (Thread.currentThread().isInterrupted()) return;
/* 345 */         showProgress(i / nLines);
/*     */       }
/* 347 */       int startPixel = i * lineInc;
/* 348 */       if (direction == 5) startPixel += width - 1;
/* 349 */       switch (direction) { case 2:
/* 350 */         length = Math.min(height, width - i); break;
/*     */       case 3:
/* 351 */         length = Math.min(width, height - i); break;
/*     */       case 4:
/* 352 */         length = Math.min(height, i + 1); break;
/*     */       case 5:
/* 353 */         length = Math.min(width, height - i);
/*     */       }
/* 355 */       lineSlideParabola(pixels, startPixel, pointInc, length, coeff2, cache, nextPoint, null);
/*     */     }
/* 357 */     this.pass += 1;
/*     */   }
/*     */ 
/*     */   static float[] lineSlideParabola(float[] pixels, int start, int inc, int length, float coeff2, float[] cache, int[] nextPoint, float[] correctedEdges)
/*     */   {
/* 380 */     float minValue = 3.4028235E+38F;
/* 381 */     int lastpoint = 0;
/* 382 */     int firstCorner = length - 1;
/* 383 */     int lastCorner = 0;
/* 384 */     float vPrevious1 = 0.0F;
/* 385 */     float vPrevious2 = 0.0F;
/* 386 */     float curvatureTest = 1.999F * coeff2;
/*     */ 
/* 389 */     int i = 0; for (int p = start; i < length; p += inc) {
/* 390 */       float v = pixels[p];
/* 391 */       cache[i] = v;
/* 392 */       if (v < minValue) minValue = v;
/* 393 */       if ((i >= 2) && (vPrevious1 + vPrevious1 - vPrevious2 - v < curvatureTest)) {
/* 394 */         nextPoint[lastpoint] = (i - 1);
/* 395 */         lastpoint = i - 1;
/*     */       }
/* 397 */       vPrevious2 = vPrevious1;
/* 398 */       vPrevious1 = v;
/*     */ 
/* 389 */       i++;
/*     */     }
/*     */ 
/* 400 */     nextPoint[lastpoint] = (length - 1);
/* 401 */     nextPoint[(length - 1)] = 2147483647;
/*     */ 
/* 403 */     int i1 = 0;
/* 404 */     while (i1 < length - 1) {
/* 405 */       float v1 = cache[i1];
/* 406 */       float minSlope = 3.4028235E+38F;
/* 407 */       int i2 = 0;
/* 408 */       int searchTo = length;
/* 409 */       int recalculateLimitNow = 0;
/*     */ 
/* 411 */       for (int j = nextPoint[i1]; j < searchTo; recalculateLimitNow++) {
/* 412 */         float v2 = cache[j];
/* 413 */         float slope = (v2 - v1) / (j - i1) + coeff2 * (j - i1);
/* 414 */         if (slope < minSlope) {
/* 415 */           minSlope = slope;
/* 416 */           i2 = j;
/* 417 */           recalculateLimitNow = -3;
/*     */         }
/* 419 */         if (recalculateLimitNow == 0) {
/* 420 */           double b = 0.5F * minSlope / coeff2;
/* 421 */           int maxSearch = i1 + (int)(b + Math.sqrt(b * b + (v1 - minValue) / coeff2) + 1.0D);
/* 422 */           if ((maxSearch < searchTo) && (maxSearch > 0)) searchTo = maxSearch;
/*     */         }
/* 411 */         j = nextPoint[j];
/*     */       }
/*     */ 
/* 425 */       if (i1 == 0) firstCorner = i2;
/* 426 */       if (i2 == length - 1) lastCorner = i1;
/*     */ 
/* 428 */       int j = i1 + 1; for (int p = start + j * inc; j < i2; p += inc) {
/* 429 */         pixels[p] = (v1 + (j - i1) * (minSlope - (j - i1) * coeff2));
/*     */ 
/* 428 */         j++;
/*     */       }
/* 430 */       i1 = i2;
/*     */     }
/*     */ 
/* 434 */     if (correctedEdges != null) {
/* 435 */       if (4 * firstCorner >= length) firstCorner = 0;
/* 436 */       if (4 * (length - 1 - lastCorner) >= length) lastCorner = length - 1;
/* 437 */       float v1 = cache[firstCorner];
/* 438 */       float v2 = cache[lastCorner];
/* 439 */       float slope = (v2 - v1) / (lastCorner - firstCorner);
/* 440 */       float value0 = v1 - slope * firstCorner;
/* 441 */       float coeff6 = 0.0F;
/* 442 */       float mid = 0.5F * (lastCorner + firstCorner);
/* 443 */       for (int i = (length + 2) / 3; i <= 2 * length / 3; i++) {
/* 444 */         float dx = (i - mid) * 2.0F / (lastCorner - firstCorner);
/* 445 */         float poly6 = dx * dx * dx * dx * dx * dx - 1.0F;
/* 446 */         if (cache[i] < value0 + slope * i + coeff6 * poly6) {
/* 447 */           coeff6 = -(value0 + slope * i - cache[i]) / poly6;
/*     */         }
/*     */       }
/* 450 */       float dx = (firstCorner - mid) * 2.0F / (lastCorner - firstCorner);
/* 451 */       correctedEdges[0] = (value0 + coeff6 * (dx * dx * dx * dx * dx * dx - 1.0F) + coeff2 * firstCorner * firstCorner);
/* 452 */       dx = (lastCorner - mid) * 2.0F / (lastCorner - firstCorner);
/* 453 */       correctedEdges[1] = (value0 + (length - 1) * slope + coeff6 * (dx * dx * dx * dx * dx * dx - 1.0F) + coeff2 * (length - 1 - lastCorner) * (length - 1 - lastCorner));
/*     */     }
/*     */ 
/* 457 */     return correctedEdges;
/*     */   }
/*     */ 
/*     */   void correctCorners(FloatProcessor fp, float coeff2, float[] cache, int[] nextPoint)
/*     */   {
/* 465 */     int width = fp.getWidth();
/* 466 */     int height = fp.getHeight();
/* 467 */     float[] pixels = (float[])fp.getPixels();
/* 468 */     float[] corners = new float[4];
/* 469 */     float[] correctedEdges = new float[2];
/* 470 */     correctedEdges = lineSlideParabola(pixels, 0, 1, width, coeff2, cache, nextPoint, correctedEdges);
/* 471 */     corners[0] = correctedEdges[0];
/* 472 */     corners[1] = correctedEdges[1];
/* 473 */     correctedEdges = lineSlideParabola(pixels, (height - 1) * width, 1, width, coeff2, cache, nextPoint, correctedEdges);
/* 474 */     corners[2] = correctedEdges[0];
/* 475 */     corners[3] = correctedEdges[1];
/* 476 */     correctedEdges = lineSlideParabola(pixels, 0, width, height, coeff2, cache, nextPoint, correctedEdges);
/* 477 */     corners[0] += correctedEdges[0];
/* 478 */     corners[2] += correctedEdges[1];
/* 479 */     correctedEdges = lineSlideParabola(pixels, width - 1, width, height, coeff2, cache, nextPoint, correctedEdges);
/* 480 */     corners[1] += correctedEdges[0];
/* 481 */     corners[3] += correctedEdges[1];
/* 482 */     int diagLength = Math.min(width, height);
/* 483 */     float coeff2diag = 2.0F * coeff2;
/* 484 */     correctedEdges = lineSlideParabola(pixels, 0, 1 + width, diagLength, coeff2diag, cache, nextPoint, correctedEdges);
/* 485 */     corners[0] += correctedEdges[0];
/* 486 */     correctedEdges = lineSlideParabola(pixels, width - 1, -1 + width, diagLength, coeff2diag, cache, nextPoint, correctedEdges);
/* 487 */     corners[1] += correctedEdges[0];
/* 488 */     correctedEdges = lineSlideParabola(pixels, (height - 1) * width, 1 - width, diagLength, coeff2diag, cache, nextPoint, correctedEdges);
/* 489 */     corners[2] += correctedEdges[0];
/* 490 */     correctedEdges = lineSlideParabola(pixels, width * height - 1, -1 - width, diagLength, coeff2diag, cache, nextPoint, correctedEdges);
/* 491 */     corners[3] += correctedEdges[0];
/*     */ 
/* 496 */     if (pixels[0] > corners[0] / 3.0F) corners[0] /= 3.0F;
/* 497 */     if (pixels[(width - 1)] > corners[1] / 3.0F) pixels[(width - 1)] = (corners[1] / 3.0F);
/* 498 */     if (pixels[((height - 1) * width)] > corners[2] / 3.0F) pixels[((height - 1) * width)] = (corners[2] / 3.0F);
/* 499 */     if (pixels[(width * height - 1)] > corners[3] / 3.0F) pixels[(width * height - 1)] = (corners[3] / 3.0F);
/*     */   }
/*     */ 
/*     */   void rollingBallFloatBackground(FloatProcessor fp, float radius, boolean invert, boolean doPresmooth, RollingBall ball)
/*     */   {
/* 509 */     float[] pixels = (float[])fp.getPixels();
/* 510 */     boolean shrink = ball.shrinkFactor > 1;
/*     */ 
/* 512 */     showProgress(0.0D);
/* 513 */     if (invert)
/* 514 */       for (int i = 0; i < pixels.length; i++)
/* 515 */         pixels[i] = (-pixels[i]);
/* 516 */     if (doPresmooth)
/* 517 */       filter3x3(fp, 1);
/* 518 */     double[] minmax = Tools.getMinMax(pixels);
/* 519 */     if (Thread.currentThread().isInterrupted()) return;
/* 520 */     FloatProcessor smallImage = shrink ? shrinkImage(fp, ball.shrinkFactor) : fp;
/* 521 */     if (Thread.currentThread().isInterrupted()) return;
/* 522 */     rollBall(ball, smallImage);
/* 523 */     if (Thread.currentThread().isInterrupted()) return;
/* 524 */     showProgress(0.9D);
/* 525 */     if (shrink)
/* 526 */       enlargeImage(smallImage, fp, ball.shrinkFactor);
/* 527 */     if (Thread.currentThread().isInterrupted()) return;
/*     */ 
/* 529 */     if (invert)
/* 530 */       for (int i = 0; i < pixels.length; i++)
/* 531 */         pixels[i] = (-pixels[i]);
/* 532 */     this.pass += 1;
/*     */   }
/*     */ 
/*     */   FloatProcessor shrinkImage(FloatProcessor ip, int shrinkFactor)
/*     */   {
/* 537 */     int width = ip.getWidth();
/* 538 */     int height = ip.getHeight();
/* 539 */     float[] pixels = (float[])ip.getPixels();
/* 540 */     int sWidth = (width + shrinkFactor - 1) / shrinkFactor;
/* 541 */     int sHeight = (height + shrinkFactor - 1) / shrinkFactor;
/* 542 */     showProgress(0.1D);
/* 543 */     FloatProcessor smallImage = new FloatProcessor(sWidth, sHeight);
/* 544 */     float[] sPixels = (float[])smallImage.getPixels();
/*     */ 
/* 546 */     for (int ySmall = 0; ySmall < sHeight; ySmall++) {
/* 547 */       for (int xSmall = 0; xSmall < sWidth; xSmall++) {
/* 548 */         float min = 3.4028235E+38F;
/* 549 */         int j = 0; for (int y = shrinkFactor * ySmall; (j < shrinkFactor) && (y < height); y++) {
/* 550 */           int k = 0; for (int x = shrinkFactor * xSmall; (k < shrinkFactor) && (x < width); x++) {
/* 551 */             float thispixel = pixels[(x + y * width)];
/* 552 */             if (thispixel < min)
/* 553 */               min = thispixel;
/* 550 */             k++;
/*     */           }
/* 549 */           j++;
/*     */         }
/*     */ 
/* 556 */         sPixels[(xSmall + ySmall * sWidth)] = min;
/*     */       }
/*     */     }
/*     */ 
/* 560 */     return smallImage;
/*     */   }
/*     */ 
/*     */   void rollBall(RollingBall ball, FloatProcessor fp)
/*     */   {
/* 576 */     float[] pixels = (float[])fp.getPixels();
/* 577 */     int width = fp.getWidth();
/* 578 */     int height = fp.getHeight();
/* 579 */     float[] zBall = ball.data;
/* 580 */     int ballWidth = ball.width;
/* 581 */     int radius = ballWidth / 2;
/* 582 */     float[] cache = new float[width * ballWidth];
/*     */ 
/* 584 */     Thread thread = Thread.currentThread();
/* 585 */     long lastTime = System.currentTimeMillis();
/* 586 */     for (int y = -radius; y < height + radius; y++) {
/* 587 */       long time = System.currentTimeMillis();
/* 588 */       if (time - lastTime > 100L) {
/* 589 */         lastTime = time;
/* 590 */         if (thread.isInterrupted()) return;
/* 591 */         showProgress(0.1D + 0.8D * y / (height + ballWidth));
/*     */       }
/* 593 */       int nextLineToWriteInCache = (y + radius) % ballWidth;
/* 594 */       int nextLineToRead = y + radius;
/* 595 */       if (nextLineToRead < height) {
/* 596 */         System.arraycopy(pixels, nextLineToRead * width, cache, nextLineToWriteInCache * width, width);
/* 597 */         int x = 0; for (int p = nextLineToRead * width; x < width; p++) {
/* 598 */           pixels[p] = -3.402824E+38F;
/*     */ 
/* 597 */           x++;
/*     */         }
/*     */       }
/* 600 */       int y0 = y - radius;
/* 601 */       if (y0 < 0) y0 = 0;
/* 602 */       int yBall0 = y0 - y + radius;
/* 603 */       int yend = y + radius;
/* 604 */       if (yend >= height) yend = height - 1;
/* 605 */       for (int x = -radius; x < width + radius; x++) {
/* 606 */         float z = 3.4028235E+38F;
/* 607 */         int x0 = x - radius;
/* 608 */         if (x0 < 0) x0 = 0;
/* 609 */         int xBall0 = x0 - x + radius;
/* 610 */         int xend = x + radius;
/* 611 */         if (xend >= width) xend = width - 1;
/* 612 */         int yp = y0; for (int yBall = yBall0; yp <= yend; yBall++) {
/* 613 */           int cachePointer = yp % ballWidth * width + x0;
/* 614 */           int xp = x0; for (int bp = xBall0 + yBall * ballWidth; xp <= xend; bp++) {
/* 615 */             float zReduced = cache[cachePointer] - zBall[bp];
/* 616 */             if (z > zReduced)
/* 617 */               z = zReduced;
/* 614 */             xp++; cachePointer++;
/*     */           }
/* 612 */           yp++;
/*     */         }
/*     */ 
/* 620 */         int yp = y0; for (int yBall = yBall0; yp <= yend; yBall++) {
/* 621 */           int xp = x0; int p = xp + yp * width; for (int bp = xBall0 + yBall * ballWidth; xp <= xend; bp++) {
/* 622 */             float zMin = z + zBall[bp];
/* 623 */             if (pixels[p] < zMin)
/* 624 */               pixels[p] = zMin;
/* 621 */             xp++; p++;
/*     */           }
/* 620 */           yp++;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void enlargeImage(FloatProcessor smallImage, FloatProcessor fp, int shrinkFactor)
/*     */   {
/* 638 */     int width = fp.getWidth();
/* 639 */     int height = fp.getHeight();
/* 640 */     int smallWidth = smallImage.getWidth();
/* 641 */     int smallHeight = smallImage.getHeight();
/* 642 */     float[] pixels = (float[])fp.getPixels();
/* 643 */     float[] sPixels = (float[])smallImage.getPixels();
/* 644 */     int[] xSmallIndices = new int[width];
/* 645 */     float[] xWeights = new float[width];
/* 646 */     makeInterpolationArrays(xSmallIndices, xWeights, width, smallWidth, shrinkFactor);
/* 647 */     int[] ySmallIndices = new int[height];
/* 648 */     float[] yWeights = new float[height];
/* 649 */     makeInterpolationArrays(ySmallIndices, yWeights, height, smallHeight, shrinkFactor);
/* 650 */     float[] line0 = new float[width];
/* 651 */     float[] line1 = new float[width];
/* 652 */     for (int x = 0; x < width; x++) {
/* 653 */       line1[x] = (sPixels[xSmallIndices[x]] * xWeights[x] + sPixels[(xSmallIndices[x] + 1)] * (1.0F - xWeights[x]));
/*     */     }
/* 655 */     int ySmallLine0 = -1;
/* 656 */     for (int y = 0; y < height; y++) {
/* 657 */       if (ySmallLine0 < ySmallIndices[y]) {
/* 658 */         float[] swap = line0;
/* 659 */         line0 = line1;
/* 660 */         line1 = swap;
/* 661 */         ySmallLine0++;
/* 662 */         int sYPointer = (ySmallIndices[y] + 1) * smallWidth;
/* 663 */         for (int x = 0; x < width; x++) {
/* 664 */           line1[x] = (sPixels[(sYPointer + xSmallIndices[x])] * xWeights[x] + sPixels[(sYPointer + xSmallIndices[x] + 1)] * (1.0F - xWeights[x]));
/*     */         }
/*     */       }
/* 667 */       float weight = yWeights[y];
/* 668 */       int x = 0; for (int p = y * width; x < width; p++) {
/* 669 */         pixels[p] = (line0[x] * weight + line1[x] * (1.0F - weight));
/*     */ 
/* 668 */         x++;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void makeInterpolationArrays(int[] smallIndices, float[] weights, int length, int smallLength, int shrinkFactor)
/*     */   {
/* 684 */     for (int i = 0; i < length; i++) {
/* 685 */       int smallIndex = (i - shrinkFactor / 2) / shrinkFactor;
/* 686 */       if (smallIndex >= smallLength - 1) smallIndex = smallLength - 2;
/* 687 */       smallIndices[i] = smallIndex;
/* 688 */       float distance = (i + 0.5F) / shrinkFactor - (smallIndex + 0.5F);
/* 689 */       weights[i] = (1.0F - distance);
/*     */     }
/*     */   }
/*     */ 
/*     */   double filter3x3(FloatProcessor fp, int type)
/*     */   {
/* 702 */     int width = fp.getWidth();
/* 703 */     int height = fp.getHeight();
/* 704 */     double shiftBy = 0.0D;
/* 705 */     float[] pixels = (float[])fp.getPixels();
/* 706 */     for (int y = 0; y < height; y++)
/* 707 */       shiftBy += filter3(pixels, width, y * width, 1, type);
/* 708 */     for (int x = 0; x < width; x++)
/* 709 */       shiftBy += filter3(pixels, height, x, width, type);
/* 710 */     return shiftBy / width / height;
/*     */   }
/*     */ 
/*     */   double filter3(float[] pixels, int length, int pixel0, int inc, int type)
/*     */   {
/* 715 */     double shiftBy = 0.0D;
/* 716 */     float v3 = pixels[pixel0];
/* 717 */     float v2 = v3;
/*     */ 
/* 719 */     int i = 0; for (int p = pixel0; i < length; p += inc) {
/* 720 */       float v1 = v2;
/* 721 */       v2 = v3;
/* 722 */       if (i < length - 1) v3 = pixels[(p + inc)];
/* 723 */       if (type == 0) {
/* 724 */         float max = v1 > v3 ? v1 : v3;
/* 725 */         if (v2 > max) max = v2;
/* 726 */         shiftBy += max - v2;
/* 727 */         pixels[p] = max;
/*     */       } else {
/* 729 */         pixels[p] = ((v1 + v2 + v3) * 0.3333333F);
/*     */       }
/* 719 */       i++;
/*     */     }
/*     */ 
/* 731 */     return shiftBy;
/*     */   }
/*     */ 
/*     */   public void setNPasses(int nPasses)
/*     */   {
/* 736 */     if ((this.isRGB) && (separateColors)) nPasses *= 3;
/* 737 */     this.nPasses = nPasses;
/* 738 */     if (useParaboloid) nPasses *= (doPresmooth ? 11 : 9);
/* 739 */     this.pass = 0;
/*     */   }
/*     */ 
/*     */   private void showProgress(double percent) {
/* 743 */     if (this.nPasses <= 0) return;
/* 744 */     percent = this.pass / this.nPasses + percent / this.nPasses;
/* 745 */     IJ.showProgress(percent);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.BackgroundSubtracter
 * JD-Core Version:    0.6.2
 */