/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ 
/*     */ public class EDM
/*     */   implements ExtendedPlugInFilter
/*     */ {
/*     */   public static final int BYTE_OVERWRITE = 0;
/*     */   public static final int BYTE = 1;
/*     */   public static final int SHORT = 2;
/*     */   public static final int FLOAT = 3;
/*     */   public static final int ONE = 41;
/*     */   public static final int SQRT2 = 58;
/*     */   public static final int SQRT5 = 92;
/*     */   private ImagePlus imp;
/*     */   private ImagePlus outImp;
/*     */   private PlugInFilterRunner pfr;
/*     */   private String command;
/*     */   private int outImageType;
/*     */   private ImageStack outStack;
/*     */   private int processType;
/*  78 */   private MaximumFinder maxFinder = new MaximumFinder();
/*     */   private double progressDone;
/*     */   private int nPasses;
/*     */   private boolean interrupted;
/*     */   private boolean background255;
/*  84 */   private int flags = 98305;
/*     */   private static final int EDM = 0;
/*     */   private static final int WATERSHED = 1;
/*     */   private static final int UEP = 2;
/*     */   private static final int VORONOI = 3;
/*  88 */   private static final boolean[] USES_MAX_FINDER = { false, true, true, true };
/*     */ 
/*  91 */   private static final boolean[] USES_WATERSHED = { false, true, false, true };
/*     */ 
/*  94 */   private static final String[] TITLE_PREFIX = { "EDM of ", null, "UEPs of ", "Voronoi of " };
/*     */   private static final int NO_POINT = -1;
/*     */   private static final double MAXFINDER_TOLERANCE = 0.5D;
/* 100 */   private static int outputType = 0;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/* 106 */     if (arg.equals("final")) {
/* 107 */       showOutput();
/* 108 */       return 4096;
/*     */     }
/* 110 */     this.imp = imp;
/*     */ 
/* 112 */     if (arg.equals("watershed")) {
/* 113 */       this.processType = 1;
/* 114 */       this.flags += 131072;
/* 115 */     } else if (arg.equals("points")) {
/* 116 */       this.processType = 2;
/* 117 */     } else if (arg.equals("voronoi")) {
/* 118 */       this.processType = 3;
/*     */     }
/*     */ 
/* 121 */     if (this.processType != 1)
/* 122 */       this.outImageType = outputType;
/* 123 */     if (this.outImageType != 0) {
/* 124 */       this.flags |= 128;
/*     */     }
/*     */ 
/* 127 */     if (imp != null) {
/* 128 */       ImageProcessor ip = imp.getProcessor();
/* 129 */       if (!ip.isBinary()) {
/* 130 */         IJ.error("8-bit binary image (0 and 255) required.");
/* 131 */         return 4096;
/*     */       }
/* 133 */       ip.resetRoi();
/*     */ 
/* 135 */       boolean invertedLut = imp.isInvertedLut();
/* 136 */       this.background255 = (((invertedLut) && (Prefs.blackBackground)) || ((!invertedLut) && (!Prefs.blackBackground)));
/*     */     }
/* 138 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr)
/*     */   {
/* 146 */     this.pfr = pfr;
/* 147 */     int width = imp.getWidth();
/* 148 */     int height = imp.getHeight();
/*     */ 
/* 151 */     this.flags = IJ.setupDialog(imp, this.flags);
/* 152 */     if (((this.flags & 0x20) != 0) && (this.outImageType != 0)) {
/* 153 */       this.outStack = new ImageStack(width, height, imp.getStackSize());
/* 154 */       this.maxFinder.setNPasses(imp.getStackSize());
/*     */     }
/* 156 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip)
/*     */   {
/* 161 */     if (this.interrupted) return;
/* 162 */     int width = ip.getWidth();
/* 163 */     int height = ip.getHeight();
/*     */ 
/* 165 */     int backgroundValue = this.background255 ? -1 : this.processType == 3 ? -1 : this.background255 ? 0 : 0;
/*     */ 
/* 168 */     if (USES_WATERSHED[this.processType] != 0) this.nPasses = 0;
/* 169 */     FloatProcessor floatEdm = makeFloatEDM(ip, backgroundValue, false);
/*     */ 
/* 171 */     ByteProcessor maxIp = null;
/* 172 */     if (USES_MAX_FINDER[this.processType] != 0) {
/* 173 */       if (this.processType == 3) floatEdm.multiply(-1.0D);
/* 174 */       int maxOutputType = USES_WATERSHED[this.processType] != 0 ? 2 : 0;
/* 175 */       boolean isEDM = this.processType != 3;
/* 176 */       maxIp = this.maxFinder.findMaxima(floatEdm, 0.5D, -808080.0D, maxOutputType, false, isEDM);
/*     */ 
/* 178 */       if (maxIp == null) {
/* 179 */         this.interrupted = true;
/* 180 */         return;
/* 181 */       }if (this.processType != 1) {
/* 182 */         if (this.processType == 3) floatEdm.multiply(-1.0D);
/* 183 */         resetMasked(floatEdm, maxIp, this.processType == 3 ? -1 : 0);
/*     */       }
/*     */     }
/*     */ 
/* 187 */     ImageProcessor outIp = null;
/* 188 */     if (this.processType == 1) {
/* 189 */       if (this.background255) maxIp.invert();
/* 190 */       ip.copyBits(maxIp, 0, 0, 0);
/* 191 */       ip.setBinaryThreshold(); } else {
/* 192 */       switch (this.outImageType) {
/*     */       case 3:
/* 194 */         outIp = floatEdm;
/* 195 */         break;
/*     */       case 2:
/* 197 */         floatEdm.setMinAndMax(0.0D, 65535.0D);
/* 198 */         outIp = floatEdm.convertToShort(true);
/* 199 */         break;
/*     */       case 1:
/* 201 */         floatEdm.setMinAndMax(0.0D, 255.0D);
/* 202 */         outIp = floatEdm.convertToByte(true);
/* 203 */         break;
/*     */       case 0:
/* 205 */         ip.setPixels(0, floatEdm);
/* 206 */         if (floatEdm.getMax() > 255.0D)
/* 207 */           ip.resetMinAndMax(); break;
/*     */       }
/*     */     }
/* 210 */     if (this.outImageType != 0)
/* 211 */       if (this.outStack == null)
/* 212 */         this.outImp = new ImagePlus(TITLE_PREFIX[this.processType] + this.imp.getShortTitle(), outIp);
/*     */       else
/* 214 */         this.outStack.setPixels(outIp.getPixels(), this.pfr.getSliceNumber());
/*     */   }
/*     */ 
/*     */   public void setNPasses(int nPasses)
/*     */   {
/* 223 */     this.nPasses = nPasses;
/* 224 */     this.progressDone = 0.0D;
/* 225 */     if (USES_MAX_FINDER[this.processType] != 0) this.maxFinder.setNPasses(nPasses);
/*     */   }
/*     */ 
/*     */   public void toEDM(ImageProcessor ip)
/*     */   {
/* 234 */     ip.setPixels(0, makeFloatEDM(ip, 0, false));
/* 235 */     ip.resetMinAndMax();
/*     */   }
/*     */ 
/*     */   public void toWatershed(ImageProcessor ip)
/*     */   {
/* 244 */     FloatProcessor floatEdm = makeFloatEDM(ip, 0, false);
/* 245 */     ByteProcessor maxIp = this.maxFinder.findMaxima(floatEdm, 0.5D, -808080.0D, 2, false, true);
/*     */ 
/* 247 */     if (maxIp != null) ip.copyBits(maxIp, 0, 0, 9);
/*     */   }
/*     */ 
/*     */   public ShortProcessor make16bitEDM(ImageProcessor ip)
/*     */   {
/* 256 */     FloatProcessor floatEdm = makeFloatEDM(ip, 0, false);
/* 257 */     floatEdm.setMinAndMax(0.0D, 1598.4146341463415D);
/* 258 */     return (ShortProcessor)floatEdm.convertToShort(true);
/*     */   }
/*     */ 
/*     */   public FloatProcessor makeFloatEDM(ImageProcessor ip, int backgroundValue, boolean edgesAreBackground)
/*     */   {
/* 271 */     int width = ip.getWidth();
/* 272 */     int height = ip.getHeight();
/* 273 */     FloatProcessor fp = new FloatProcessor(width, height);
/* 274 */     byte[] bPixels = (byte[])ip.getPixels();
/* 275 */     float[] fPixels = (float[])fp.getPixels();
/* 276 */     int progressInterval = 100;
/* 277 */     int nProgressUpdates = height / 100;
/* 278 */     double progressAddendum = nProgressUpdates > 0 ? 0.5D / nProgressUpdates : 0.0D;
/*     */ 
/* 280 */     for (int i = 0; i < width * height; i++) {
/* 281 */       if (bPixels[i] != backgroundValue) fPixels[i] = 3.4028235E+38F;
/*     */     }
/* 283 */     int[][] pointBufs = new int[2][width];
/* 284 */     int yDist = 2147483647;
/*     */ 
/* 286 */     for (int x = 0; x < width; x++) {
/* 287 */       pointBufs[0][x] = -1;
/* 288 */       pointBufs[1][x] = -1;
/*     */     }
/* 290 */     for (int y = 0; y < height; y++) {
/* 291 */       if (edgesAreBackground) yDist = y + 1;
/* 292 */       edmLine(bPixels, fPixels, pointBufs, width, y * width, y, backgroundValue, yDist);
/* 293 */       if (y % 100 == 0) {
/* 294 */         if (Thread.currentThread().isInterrupted()) return null;
/* 295 */         addProgress(progressAddendum);
/*     */       }
/*     */     }
/*     */ 
/* 299 */     for (int x = 0; x < width; x++) {
/* 300 */       pointBufs[0][x] = -1;
/* 301 */       pointBufs[1][x] = -1;
/*     */     }
/* 303 */     for (int y = height - 1; y >= 0; y--) {
/* 304 */       if (edgesAreBackground) yDist = height - y;
/* 305 */       edmLine(bPixels, fPixels, pointBufs, width, y * width, y, backgroundValue, yDist);
/* 306 */       if (y % 100 == 0) {
/* 307 */         if (Thread.currentThread().isInterrupted()) return null;
/* 308 */         addProgress(progressAddendum);
/*     */       }
/*     */     }
/*     */ 
/* 312 */     fp.sqrt();
/* 313 */     return fp;
/*     */   }
/*     */ 
/*     */   private void edmLine(byte[] bPixels, float[] fPixels, int[][] pointBufs, int width, int offset, int y, int backgroundValue, int yDist)
/*     */   {
/* 319 */     int[] points = pointBufs[0];
/* 320 */     int pPrev = -1;
/* 321 */     int pDiag = -1;
/*     */ 
/* 323 */     boolean edgesAreBackground = yDist != 2147483647;
/* 324 */     int distSqr = 2147483647;
/* 325 */     for (int x = 0; x < width; offset++) {
/* 326 */       int pNextDiag = points[x];
/* 327 */       if (bPixels[offset] == backgroundValue) {
/* 328 */         points[x] = (x | y << 16);
/*     */       } else {
/* 330 */         if (edgesAreBackground)
/* 331 */           distSqr = x + 1 < yDist ? (x + 1) * (x + 1) : yDist * yDist;
/* 332 */         float dist2 = minDist2(points, pPrev, pDiag, x, y, distSqr);
/* 333 */         if (fPixels[offset] > dist2) fPixels[offset] = dist2;
/*     */       }
/* 335 */       pPrev = points[x];
/* 336 */       pDiag = pNextDiag;
/*     */ 
/* 325 */       x++;
/*     */     }
/*     */ 
/* 338 */     offset--;
/* 339 */     points = pointBufs[1];
/* 340 */     pPrev = -1;
/* 341 */     pDiag = -1;
/* 342 */     for (int x = width - 1; x >= 0; offset--) {
/* 343 */       int pNextDiag = points[x];
/* 344 */       if (bPixels[offset] == backgroundValue) {
/* 345 */         points[x] = (x | y << 16);
/*     */       } else {
/* 347 */         if (edgesAreBackground)
/* 348 */           distSqr = width - x < yDist ? (width - x) * (width - x) : yDist * yDist;
/* 349 */         float dist2 = minDist2(points, pPrev, pDiag, x, y, distSqr);
/* 350 */         if (fPixels[offset] > dist2) fPixels[offset] = dist2;
/*     */       }
/* 352 */       pPrev = points[x];
/* 353 */       pDiag = pNextDiag;
/*     */ 
/* 342 */       x--;
/*     */     }
/*     */   }
/*     */ 
/*     */   private float minDist2(int[] points, int pPrev, int pDiag, int x, int y, int distSqr)
/*     */   {
/* 365 */     int p0 = points[x];
/* 366 */     int nearestPoint = p0;
/* 367 */     if (p0 != -1) {
/* 368 */       int x0 = p0 & 0xFFFF; int y0 = p0 >> 16 & 0xFFFF;
/* 369 */       int dist1Sqr = (x - x0) * (x - x0) + (y - y0) * (y - y0);
/* 370 */       if (dist1Sqr < distSqr)
/* 371 */         distSqr = dist1Sqr;
/*     */     }
/* 373 */     if ((pDiag != p0) && (pDiag != -1)) {
/* 374 */       int x1 = pDiag & 0xFFFF; int y1 = pDiag >> 16 & 0xFFFF;
/* 375 */       int dist1Sqr = (x - x1) * (x - x1) + (y - y1) * (y - y1);
/* 376 */       if (dist1Sqr < distSqr) {
/* 377 */         nearestPoint = pDiag;
/* 378 */         distSqr = dist1Sqr;
/*     */       }
/*     */     }
/* 381 */     if ((pPrev != pDiag) && (pPrev != -1)) {
/* 382 */       int x1 = pPrev & 0xFFFF; int y1 = pPrev >> 16 & 0xFFFF;
/* 383 */       int dist1Sqr = (x - x1) * (x - x1) + (y - y1) * (y - y1);
/* 384 */       if (dist1Sqr < distSqr) {
/* 385 */         nearestPoint = pPrev;
/* 386 */         distSqr = dist1Sqr;
/*     */       }
/*     */     }
/* 389 */     points[x] = nearestPoint;
/* 390 */     return distSqr;
/*     */   }
/*     */ 
/*     */   private void byteFromFloat(ImageProcessor ip, FloatProcessor floatEdm)
/*     */   {
/* 395 */     int width = ip.getWidth();
/* 396 */     int height = ip.getHeight();
/* 397 */     byte[] bPixels = (byte[])ip.getPixels();
/* 398 */     float[] fPixels = (float[])floatEdm.getPixels();
/* 399 */     for (int i = 0; i < width * height; i++) {
/* 400 */       float v = fPixels[i];
/* 401 */       bPixels[i] = (v < 255.0F ? (byte)(int)(v + 0.5D) : -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void resetMasked(FloatProcessor floatEdm, ImageProcessor mask, int resetOnThis)
/*     */   {
/* 407 */     int width = mask.getWidth();
/* 408 */     int height = mask.getHeight();
/* 409 */     byte[] mPixels = (byte[])mask.getPixels();
/* 410 */     float[] fPixels = (float[])floatEdm.getPixels();
/* 411 */     for (int i = 0; i < width * height; i++)
/* 412 */       if (mPixels[i] == resetOnThis) fPixels[i] = 0.0F;
/*     */   }
/*     */ 
/*     */   private void showOutput()
/*     */   {
/* 417 */     if (this.interrupted) return;
/* 418 */     if (this.outStack != null) {
/* 419 */       this.outImp = new ImagePlus(TITLE_PREFIX[this.processType] + this.imp.getShortTitle(), this.outStack);
/* 420 */       int[] d = this.imp.getDimensions();
/* 421 */       this.outImp.setDimensions(d[2], d[3], d[4]);
/* 422 */       for (int i = 1; i <= this.imp.getStackSize(); i++)
/* 423 */         this.outStack.setSliceLabel(this.imp.getStack().getSliceLabel(i), i);
/*     */     }
/* 425 */     if (this.outImageType != 0) {
/* 426 */       ImageProcessor ip = this.outImp.getProcessor();
/* 427 */       if (!Prefs.blackBackground) ip.invertLut();
/* 428 */       ip.resetMinAndMax();
/* 429 */       this.outImp.show();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addProgress(double deltaProgress)
/*     */   {
/* 435 */     if (this.nPasses == 0) return;
/* 436 */     this.progressDone += deltaProgress;
/* 437 */     IJ.showProgress(this.progressDone / this.nPasses);
/*     */   }
/*     */ 
/*     */   public static void setOutputType(int type)
/*     */   {
/* 442 */     if ((type < 0) || (type > 3))
/* 443 */       throw new IllegalArgumentException("Invalid type: " + type);
/* 444 */     outputType = type;
/*     */   }
/*     */ 
/*     */   public static int getOutputType()
/*     */   {
/* 449 */     return outputType;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.EDM
 * JD-Core Version:    0.6.2
 */