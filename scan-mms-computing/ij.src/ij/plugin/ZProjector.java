/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ 
/*     */ public class ZProjector
/*     */   implements PlugIn
/*     */ {
/*     */   public static final int AVG_METHOD = 0;
/*     */   public static final int MAX_METHOD = 1;
/*     */   public static final int MIN_METHOD = 2;
/*     */   public static final int SUM_METHOD = 3;
/*     */   public static final int SD_METHOD = 4;
/*     */   public static final int MEDIAN_METHOD = 5;
/*  23 */   public static final String[] METHODS = { "Average Intensity", "Max Intensity", "Min Intensity", "Sum Slices", "Standard Deviation", "Median" };
/*     */   private static final String METHOD_KEY = "zproject.method";
/*  26 */   private int method = (int)Prefs.get("zproject.method", 0.0D);
/*     */   private static final int BYTE_TYPE = 0;
/*     */   private static final int SHORT_TYPE = 1;
/*     */   private static final int FLOAT_TYPE = 2;
/*     */   public static final String lutMessage = "Stacks with inverter LUTs may not project correctly.\nTo create a standard LUT, invert the stack (Edit/Invert)\nand invert the LUT (Image/Lookup Tables/Invert LUT).";
/*  38 */   private ImagePlus projImage = null;
/*     */ 
/*  41 */   private ImagePlus imp = null;
/*     */ 
/*  44 */   private int startSlice = 1;
/*     */ 
/*  46 */   private int stopSlice = 1;
/*     */ 
/*  48 */   private boolean allTimeFrames = true;
/*     */ 
/*  50 */   private String color = "";
/*     */   private boolean isHyperstack;
/*  52 */   private int increment = 1;
/*     */   private int sliceCount;
/*     */ 
/*     */   public ZProjector()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ZProjector(ImagePlus imp)
/*     */   {
/*  60 */     setImage(imp);
/*     */   }
/*     */ 
/*     */   public void setImage(ImagePlus imp)
/*     */   {
/*  67 */     this.imp = imp;
/*  68 */     this.startSlice = 1;
/*  69 */     this.stopSlice = imp.getStackSize();
/*     */   }
/*     */ 
/*     */   public void setStartSlice(int slice) {
/*  73 */     if ((this.imp == null) || (slice < 1) || (slice > this.imp.getStackSize()))
/*  74 */       return;
/*  75 */     this.startSlice = slice;
/*     */   }
/*     */ 
/*     */   public void setStopSlice(int slice) {
/*  79 */     if ((this.imp == null) || (slice < 1) || (slice > this.imp.getStackSize()))
/*  80 */       return;
/*  81 */     this.stopSlice = slice;
/*     */   }
/*     */ 
/*     */   public void setMethod(int projMethod) {
/*  85 */     this.method = projMethod;
/*     */   }
/*     */ 
/*     */   public ImagePlus getProjection()
/*     */   {
/*  90 */     return this.projImage;
/*     */   }
/*     */ 
/*     */   public void run(String arg) {
/*  94 */     this.imp = WindowManager.getCurrentImage();
/*  95 */     int stackSize = this.imp.getStackSize();
/*  96 */     if (this.imp == null) {
/*  97 */       IJ.noImage();
/*  98 */       return;
/*     */     }
/*     */ 
/* 102 */     if (stackSize == 1) {
/* 103 */       IJ.error("ZProjection", "Stack required");
/* 104 */       return;
/*     */     }
/*     */ 
/* 108 */     if ((this.imp.getProcessor().isInvertedLut()) && 
/* 109 */       (!IJ.showMessageWithCancel("ZProjection", "Stacks with inverter LUTs may not project correctly.\nTo create a standard LUT, invert the stack (Edit/Invert)\nand invert the LUT (Image/Lookup Tables/Invert LUT)."))) {
/* 110 */       return;
/*     */     }
/*     */ 
/* 114 */     int frames = this.imp.getNFrames();
/* 115 */     int slices = this.imp.getNSlices();
/* 116 */     this.isHyperstack = ((this.imp.isHyperStack()) || ((Interpreter.isBatchMode()) && (((frames > 1) && (frames < stackSize)) || ((slices > 1) && (slices < stackSize)))));
/* 117 */     this.startSlice = 1;
/* 118 */     if (this.isHyperstack) {
/* 119 */       int nSlices = this.imp.getNSlices();
/* 120 */       if (nSlices > 1)
/* 121 */         this.stopSlice = nSlices;
/*     */       else
/* 123 */         this.stopSlice = this.imp.getNFrames();
/*     */     } else {
/* 125 */       this.stopSlice = stackSize;
/*     */     }
/*     */ 
/* 128 */     GenericDialog gd = buildControlDialog(this.startSlice, this.stopSlice);
/* 129 */     gd.showDialog();
/* 130 */     if (gd.wasCanceled()) return;
/*     */ 
/* 132 */     if (!this.imp.lock()) return;
/* 133 */     long tstart = System.currentTimeMillis();
/* 134 */     setStartSlice((int)gd.getNextNumber());
/* 135 */     setStopSlice((int)gd.getNextNumber());
/* 136 */     this.method = gd.getNextChoiceIndex();
/* 137 */     Prefs.set("zproject.method", this.method);
/* 138 */     if (this.isHyperstack) {
/* 139 */       this.allTimeFrames = ((this.imp.getNFrames() > 1) && (this.imp.getNSlices() > 1) ? gd.getNextBoolean() : false);
/* 140 */       doHyperStackProjection(this.allTimeFrames);
/* 141 */     } else if (this.imp.getType() == 4) {
/* 142 */       doRGBProjection();
/*     */     } else {
/* 144 */       doProjection();
/*     */     }
/* 146 */     if ((arg.equals("")) && (this.projImage != null)) {
/* 147 */       long tstop = System.currentTimeMillis();
/* 148 */       this.projImage.setCalibration(this.imp.getCalibration());
/* 149 */       this.projImage.show("ZProjector: " + IJ.d2s((tstop - tstart) / 1000.0D, 2) + " seconds");
/*     */     }
/*     */ 
/* 152 */     this.imp.unlock();
/* 153 */     IJ.register(ZProjector.class);
/*     */   }
/*     */ 
/*     */   public void doRGBProjection()
/*     */   {
/* 158 */     doRGBProjection(this.imp.getStack());
/*     */   }
/*     */ 
/*     */   private void doRGBProjection(ImageStack stack) {
/* 162 */     ImageStack[] channels = ChannelSplitter.splitRGB(stack, true);
/* 163 */     ImagePlus red = new ImagePlus("Red", channels[0]);
/* 164 */     ImagePlus green = new ImagePlus("Green", channels[1]);
/* 165 */     ImagePlus blue = new ImagePlus("Blue", channels[2]);
/* 166 */     this.imp.unlock();
/* 167 */     ImagePlus saveImp = this.imp;
/* 168 */     this.imp = red;
/* 169 */     this.color = "(red)"; doProjection();
/* 170 */     ImagePlus red2 = this.projImage;
/* 171 */     this.imp = green;
/* 172 */     this.color = "(green)"; doProjection();
/* 173 */     ImagePlus green2 = this.projImage;
/* 174 */     this.imp = blue;
/* 175 */     this.color = "(blue)"; doProjection();
/* 176 */     ImagePlus blue2 = this.projImage;
/* 177 */     int w = red2.getWidth(); int h = red2.getHeight(); int d = red2.getStackSize();
/* 178 */     RGBStackMerge merge = new RGBStackMerge();
/* 179 */     ImageStack stack2 = merge.mergeStacks(w, h, d, red2.getStack(), green2.getStack(), blue2.getStack(), true);
/* 180 */     this.imp = saveImp;
/* 181 */     this.projImage = new ImagePlus(makeTitle(), stack2);
/*     */   }
/*     */ 
/*     */   protected GenericDialog buildControlDialog(int start, int stop)
/*     */   {
/* 188 */     GenericDialog gd = new GenericDialog("ZProjection", IJ.getInstance());
/* 189 */     gd.addNumericField("Start slice:", this.startSlice, 0);
/* 190 */     gd.addNumericField("Stop slice:", this.stopSlice, 0);
/* 191 */     gd.addChoice("Projection Type", METHODS, METHODS[this.method]);
/* 192 */     if ((this.isHyperstack) && (this.imp.getNFrames() > 1) && (this.imp.getNSlices() > 1))
/* 193 */       gd.addCheckbox("All Time Frames", this.allTimeFrames);
/* 194 */     return gd;
/*     */   }
/*     */ 
/*     */   public void doProjection()
/*     */   {
/* 199 */     if (this.imp == null)
/* 200 */       return;
/* 201 */     this.sliceCount = 0;
/* 202 */     if ((this.method < 0) || (this.method > 5))
/* 203 */       this.method = 0;
/* 204 */     for (int slice = this.startSlice; slice <= this.stopSlice; slice += this.increment)
/* 205 */       this.sliceCount += 1;
/* 206 */     if (this.method == 5) {
/* 207 */       this.projImage = doMedianProjection();
/* 208 */       return;
/*     */     }
/*     */ 
/* 212 */     FloatProcessor fp = new FloatProcessor(this.imp.getWidth(), this.imp.getHeight());
/* 213 */     ImageStack stack = this.imp.getStack();
/* 214 */     RayFunction rayFunc = getRayFunction(this.method, fp);
/* 215 */     if (IJ.debugMode == true)
/* 216 */       IJ.log("\nProjecting stack from: " + this.startSlice + " to: " + this.stopSlice);
/* 226 */     int ptype;
/* 226 */     if ((stack.getProcessor(1) instanceof ByteProcessor)) { ptype = 0; }
/*     */     else
/*     */     {
/* 227 */       int ptype;
/* 227 */       if ((stack.getProcessor(1) instanceof ShortProcessor)) { ptype = 1; }
/*     */       else
/*     */       {
/* 228 */         int ptype;
/* 228 */         if ((stack.getProcessor(1) instanceof FloatProcessor)) { ptype = 2;
/*     */         } else {
/* 230 */           IJ.error("ZProjector: Non-RGB stack required");
/*     */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */     int ptype;
/* 235 */     for (int n = this.startSlice; n <= this.stopSlice; n += this.increment) {
/* 236 */       IJ.showStatus("ZProjection " + this.color + ": " + n + "/" + this.stopSlice);
/* 237 */       IJ.showProgress(n - this.startSlice, this.stopSlice - this.startSlice);
/* 238 */       projectSlice(stack.getPixels(n), rayFunc, ptype);
/*     */     }
/*     */ 
/* 242 */     if (this.method == 3) {
/* 243 */       fp.resetMinAndMax();
/* 244 */       this.projImage = new ImagePlus(makeTitle(), fp);
/* 245 */     } else if (this.method == 4) {
/* 246 */       rayFunc.postProcess();
/* 247 */       fp.resetMinAndMax();
/* 248 */       this.projImage = new ImagePlus(makeTitle(), fp);
/*     */     } else {
/* 250 */       rayFunc.postProcess();
/* 251 */       this.projImage = makeOutputImage(this.imp, fp, ptype);
/*     */     }
/*     */ 
/* 254 */     if (this.projImage == null)
/* 255 */       IJ.error("ZProjection - error computing projection.");
/*     */   }
/*     */ 
/*     */   public void doHyperStackProjection(boolean allTimeFrames) {
/* 259 */     int start = this.startSlice;
/* 260 */     int stop = this.stopSlice;
/* 261 */     int firstFrame = 1;
/* 262 */     int lastFrame = this.imp.getNFrames();
/* 263 */     if (!allTimeFrames)
/* 264 */       firstFrame = lastFrame = this.imp.getFrame();
/* 265 */     ImageStack stack = new ImageStack(this.imp.getWidth(), this.imp.getHeight());
/* 266 */     int channels = this.imp.getNChannels();
/* 267 */     int slices = this.imp.getNSlices();
/* 268 */     if (slices == 1) {
/* 269 */       slices = this.imp.getNFrames();
/* 270 */       firstFrame = lastFrame = 1;
/*     */     }
/* 272 */     int frames = lastFrame - firstFrame + 1;
/* 273 */     this.increment = channels;
/* 274 */     boolean rgb = this.imp.getBitDepth() == 24;
/* 275 */     for (int frame = firstFrame; frame <= lastFrame; frame++) {
/* 276 */       for (int channel = 1; channel <= channels; channel++) {
/* 277 */         this.startSlice = ((frame - 1) * channels * slices + (start - 1) * channels + channel);
/* 278 */         this.stopSlice = ((frame - 1) * channels * slices + (stop - 1) * channels + channel);
/* 279 */         if (rgb)
/* 280 */           doHSRGBProjection(this.imp);
/*     */         else
/* 282 */           doProjection();
/* 283 */         stack.addSlice(null, this.projImage.getProcessor());
/*     */       }
/*     */     }
/* 286 */     this.projImage = new ImagePlus(makeTitle(), stack);
/* 287 */     this.projImage.setDimensions(channels, 1, frames);
/* 288 */     if (channels > 1) {
/* 289 */       this.projImage = new CompositeImage(this.projImage, 0);
/* 290 */       ((CompositeImage)this.projImage).copyLuts(this.imp);
/* 291 */       if ((this.method == 3) || (this.method == 4))
/* 292 */         ((CompositeImage)this.projImage).resetDisplayRanges();
/*     */     }
/* 294 */     if (frames > 1)
/* 295 */       this.projImage.setOpenAsHyperStack(true);
/* 296 */     IJ.showProgress(1, 1);
/*     */   }
/*     */ 
/*     */   private void doHSRGBProjection(ImagePlus rgbImp) {
/* 300 */     ImageStack stack = rgbImp.getStack();
/* 301 */     ImageStack stack2 = new ImageStack(stack.getWidth(), stack.getHeight());
/* 302 */     for (int i = this.startSlice; i <= this.stopSlice; i++)
/* 303 */       stack2.addSlice(null, stack.getProcessor(i));
/* 304 */     this.startSlice = 1;
/* 305 */     this.stopSlice = stack2.getSize();
/* 306 */     doRGBProjection(stack2);
/*     */   }
/*     */ 
/*     */   private RayFunction getRayFunction(int method, FloatProcessor fp) {
/* 310 */     switch (method) { case 0:
/*     */     case 3:
/* 312 */       return new AverageIntensity(fp, this.sliceCount);
/*     */     case 1:
/* 314 */       return new MaxIntensity(fp);
/*     */     case 2:
/* 316 */       return new MinIntensity(fp);
/*     */     case 4:
/* 318 */       return new StandardDeviation(fp, this.sliceCount);
/*     */     }
/* 320 */     IJ.error("ZProjection - unknown method.");
/* 321 */     return null;
/*     */   }
/*     */ 
/*     */   private ImagePlus makeOutputImage(ImagePlus imp, FloatProcessor fp, int ptype)
/*     */   {
/* 327 */     int width = imp.getWidth();
/* 328 */     int height = imp.getHeight();
/* 329 */     float[] pixels = (float[])fp.getPixels();
/* 330 */     ImageProcessor oip = null;
/*     */ 
/* 333 */     int size = pixels.length;
/* 334 */     switch (ptype) {
/*     */     case 0:
/* 336 */       oip = imp.getProcessor().createProcessor(width, height);
/* 337 */       byte[] pixels8 = (byte[])oip.getPixels();
/* 338 */       for (int i = 0; i < size; i++)
/* 339 */         pixels8[i] = ((byte)(int)pixels[i]);
/* 340 */       break;
/*     */     case 1:
/* 342 */       oip = imp.getProcessor().createProcessor(width, height);
/* 343 */       short[] pixels16 = (short[])oip.getPixels();
/* 344 */       for (int i = 0; i < size; i++)
/* 345 */         pixels16[i] = ((short)(int)pixels[i]);
/* 346 */       break;
/*     */     case 2:
/* 348 */       oip = new FloatProcessor(width, height, pixels, null);
/*     */     }
/*     */ 
/* 355 */     oip.resetMinAndMax();
/*     */ 
/* 361 */     return new ImagePlus(makeTitle(), oip);
/*     */   }
/*     */ 
/*     */   private void projectSlice(Object pixelArray, RayFunction rayFunc, int ptype)
/*     */   {
/* 369 */     switch (ptype) {
/*     */     case 0:
/* 371 */       rayFunc.projectSlice((byte[])pixelArray);
/* 372 */       break;
/*     */     case 1:
/* 374 */       rayFunc.projectSlice((short[])pixelArray);
/* 375 */       break;
/*     */     case 2:
/* 377 */       rayFunc.projectSlice((float[])pixelArray);
/*     */     }
/*     */   }
/*     */ 
/*     */   String makeTitle()
/*     */   {
/* 383 */     String prefix = "AVG_";
/* 384 */     switch (this.method) { case 3:
/* 385 */       prefix = "SUM_"; break;
/*     */     case 1:
/* 386 */       prefix = "MAX_"; break;
/*     */     case 2:
/* 387 */       prefix = "MIN_"; break;
/*     */     case 4:
/* 388 */       prefix = "STD_"; break;
/*     */     case 5:
/* 389 */       prefix = "MED_";
/*     */     }
/* 391 */     return WindowManager.makeUniqueName(prefix + this.imp.getTitle());
/*     */   }
/*     */ 
/*     */   ImagePlus doMedianProjection() {
/* 395 */     IJ.showStatus("Calculating median...");
/* 396 */     ImageStack stack = this.imp.getStack();
/* 397 */     ImageProcessor[] slices = new ImageProcessor[this.sliceCount];
/* 398 */     int index = 0;
/* 399 */     for (int slice = this.startSlice; slice <= this.stopSlice; slice += this.increment)
/* 400 */       slices[(index++)] = stack.getProcessor(slice);
/* 401 */     ImageProcessor ip2 = slices[0].duplicate();
/* 402 */     ip2 = ip2.convertToFloat();
/* 403 */     float[] values = new float[this.sliceCount];
/* 404 */     int width = ip2.getWidth();
/* 405 */     int height = ip2.getHeight();
/* 406 */     int inc = Math.max(height / 30, 1);
/* 407 */     for (int y = 0; y < height; y++) {
/* 408 */       if (y % inc == 0) IJ.showProgress(y, height - 1);
/* 409 */       for (int x = 0; x < width; x++) {
/* 410 */         for (int i = 0; i < this.sliceCount; i++)
/* 411 */           values[i] = slices[i].getPixelValue(x, y);
/* 412 */         ip2.putPixelValue(x, y, median(values));
/*     */       }
/*     */     }
/* 415 */     return new ImagePlus(makeTitle(), ip2);
/*     */   }
/*     */ 
/*     */   float median(float[] a) {
/* 419 */     sort(a);
/* 420 */     int length = a.length;
/* 421 */     if ((length & 0x1) == 0) {
/* 422 */       return (a[(length / 2 - 1)] + a[(length / 2)]) / 2.0F;
/*     */     }
/* 424 */     return a[(length / 2)];
/*     */   }
/*     */ 
/*     */   void sort(float[] a) {
/* 428 */     if (!alreadySorted(a))
/* 429 */       sort(a, 0, a.length - 1);
/*     */   }
/*     */ 
/*     */   void sort(float[] a, int from, int to) {
/* 433 */     int i = from; int j = to;
/* 434 */     float center = a[((from + to) / 2)];
/*     */     do {
/* 436 */       while ((i < to) && (center > a[i])) i++;
/* 437 */       while ((j > from) && (center < a[j])) j--;
/* 438 */       if (i < j) { float temp = a[i]; a[i] = a[j]; a[j] = temp; }
/* 439 */       if (i <= j) { i++; j--; } 
/* 440 */     }while (i <= j);
/* 441 */     if (from < j) sort(a, from, j);
/* 442 */     if (i < to) sort(a, i, to); 
/*     */   }
/*     */ 
/*     */   boolean alreadySorted(float[] a)
/*     */   {
/* 446 */     for (int i = 1; i < a.length; i++) {
/* 447 */       if (a[i] < a[(i - 1)])
/* 448 */         return false;
/*     */     }
/* 450 */     return true;
/*     */   }
/*     */ 
/*     */   class StandardDeviation extends ZProjector.RayFunction
/*     */   {
/*     */     private float[] result;
/*     */     private double[] sum;
/*     */     private double[] sum2;
/*     */     private int num;
/*     */     private int len;
/*     */ 
/*     */     public StandardDeviation(FloatProcessor fp, int num)
/*     */     {
/* 632 */       super();
/* 633 */       this.result = ((float[])fp.getPixels());
/* 634 */       this.len = this.result.length;
/* 635 */       this.num = num;
/* 636 */       this.sum = new double[this.len];
/* 637 */       this.sum2 = new double[this.len];
/*     */     }
/*     */ 
/*     */     public void projectSlice(byte[] pixels)
/*     */     {
/* 642 */       for (int i = 0; i < this.len; i++) {
/* 643 */         int v = pixels[i] & 0xFF;
/* 644 */         this.sum[i] += v;
/* 645 */         this.sum2[i] += v * v;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void projectSlice(short[] pixels)
/*     */     {
/* 651 */       for (int i = 0; i < this.len; i++) {
/* 652 */         double v = pixels[i] & 0xFFFF;
/* 653 */         this.sum[i] += v;
/* 654 */         this.sum2[i] += v * v;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void projectSlice(float[] pixels)
/*     */     {
/* 660 */       for (int i = 0; i < this.len; i++) {
/* 661 */         double v = pixels[i];
/* 662 */         this.sum[i] += v;
/* 663 */         this.sum2[i] += v * v;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void postProcess()
/*     */     {
/* 669 */       double n = this.num;
/* 670 */       for (int i = 0; i < this.len; i++)
/* 671 */         if (this.num > 1) {
/* 672 */           double stdDev = (n * this.sum2[i] - this.sum[i] * this.sum[i]) / n;
/* 673 */           if (stdDev > 0.0D)
/* 674 */             this.result[i] = ((float)Math.sqrt(stdDev / (n - 1.0D)));
/*     */           else
/* 676 */             this.result[i] = 0.0F;
/*     */         } else {
/* 678 */           this.result[i] = 0.0F;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   class MinIntensity extends ZProjector.RayFunction
/*     */   {
/*     */     private float[] fpixels;
/*     */     private int len;
/*     */ 
/*     */     public MinIntensity(FloatProcessor fp)
/*     */     {
/* 595 */       super();
/* 596 */       this.fpixels = ((float[])fp.getPixels());
/* 597 */       this.len = this.fpixels.length;
/* 598 */       for (int i = 0; i < this.len; i++)
/* 599 */         this.fpixels[i] = 3.4028235E+38F;
/*     */     }
/*     */ 
/*     */     public void projectSlice(byte[] pixels) {
/* 603 */       for (int i = 0; i < this.len; i++)
/* 604 */         if ((pixels[i] & 0xFF) < this.fpixels[i])
/* 605 */           this.fpixels[i] = (pixels[i] & 0xFF);
/*     */     }
/*     */ 
/*     */     public void projectSlice(short[] pixels)
/*     */     {
/* 610 */       for (int i = 0; i < this.len; i++)
/* 611 */         if ((pixels[i] & 0xFFFF) < this.fpixels[i])
/* 612 */           this.fpixels[i] = (pixels[i] & 0xFFFF);
/*     */     }
/*     */ 
/*     */     public void projectSlice(float[] pixels)
/*     */     {
/* 617 */       for (int i = 0; i < this.len; i++)
/* 618 */         if (pixels[i] < this.fpixels[i])
/* 619 */           this.fpixels[i] = pixels[i];
/*     */     }
/*     */   }
/*     */ 
/*     */   class MaxIntensity extends ZProjector.RayFunction
/*     */   {
/*     */     private float[] fpixels;
/*     */     private int len;
/*     */ 
/*     */     public MaxIntensity(FloatProcessor fp)
/*     */     {
/* 559 */       super();
/* 560 */       this.fpixels = ((float[])fp.getPixels());
/* 561 */       this.len = this.fpixels.length;
/* 562 */       for (int i = 0; i < this.len; i++)
/* 563 */         this.fpixels[i] = -3.402824E+38F;
/*     */     }
/*     */ 
/*     */     public void projectSlice(byte[] pixels) {
/* 567 */       for (int i = 0; i < this.len; i++)
/* 568 */         if ((pixels[i] & 0xFF) > this.fpixels[i])
/* 569 */           this.fpixels[i] = (pixels[i] & 0xFF);
/*     */     }
/*     */ 
/*     */     public void projectSlice(short[] pixels)
/*     */     {
/* 574 */       for (int i = 0; i < this.len; i++)
/* 575 */         if ((pixels[i] & 0xFFFF) > this.fpixels[i])
/* 576 */           this.fpixels[i] = (pixels[i] & 0xFFFF);
/*     */     }
/*     */ 
/*     */     public void projectSlice(float[] pixels)
/*     */     {
/* 581 */       for (int i = 0; i < this.len; i++)
/* 582 */         if (pixels[i] > this.fpixels[i])
/* 583 */           this.fpixels[i] = pixels[i];
/*     */     }
/*     */   }
/*     */ 
/*     */   class AverageIntensity extends ZProjector.RayFunction
/*     */   {
/*     */     private float[] fpixels;
/*     */     private int num;
/*     */     private int len;
/*     */ 
/*     */     public AverageIntensity(FloatProcessor fp, int num)
/*     */     {
/* 523 */       super();
/* 524 */       this.fpixels = ((float[])fp.getPixels());
/* 525 */       this.len = this.fpixels.length;
/* 526 */       this.num = num;
/*     */     }
/*     */ 
/*     */     public void projectSlice(byte[] pixels) {
/* 530 */       for (int i = 0; i < this.len; i++)
/* 531 */         this.fpixels[i] += (pixels[i] & 0xFF);
/*     */     }
/*     */ 
/*     */     public void projectSlice(short[] pixels) {
/* 535 */       for (int i = 0; i < this.len; i++)
/* 536 */         this.fpixels[i] += (pixels[i] & 0xFFFF);
/*     */     }
/*     */ 
/*     */     public void projectSlice(float[] pixels) {
/* 540 */       for (int i = 0; i < this.len; i++)
/* 541 */         this.fpixels[i] += pixels[i];
/*     */     }
/*     */ 
/*     */     public void postProcess() {
/* 545 */       float fnum = this.num;
/* 546 */       for (int i = 0; i < this.len; i++)
/* 547 */         this.fpixels[i] /= fnum;
/*     */     }
/*     */   }
/*     */ 
/*     */   abstract class RayFunction
/*     */   {
/*     */     RayFunction()
/*     */     {
/*     */     }
/*     */ 
/*     */     public abstract void projectSlice(byte[] paramArrayOfByte);
/*     */ 
/*     */     public abstract void projectSlice(short[] paramArrayOfShort);
/*     */ 
/*     */     public abstract void projectSlice(float[] paramArrayOfFloat);
/*     */ 
/*     */     public void postProcess()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ZProjector
 * JD-Core Version:    0.6.2
 */