/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.NewImage;
/*     */ import ij.gui.Roi;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.ColorModel;
/*     */ 
/*     */ public class Projector
/*     */   implements PlugIn
/*     */ {
/*     */   private static final int xAxis = 0;
/*     */   private static final int yAxis = 1;
/*     */   private static final int zAxis = 2;
/*     */   private static final int nearestPoint = 0;
/*     */   private static final int brightestPoint = 1;
/*     */   private static final int meanValue = 2;
/*     */   private static final int BIGPOWEROF2 = 8192;
/*  24 */   private static final String[] axisList = { "X-Axis", "Y-Axis", "Z-Axis" };
/*  25 */   private static final String[] methodList = { "Nearest Point", "Brightest Point", "Mean Value" };
/*     */ 
/*  27 */   private static int axisOfRotationS = 1;
/*  28 */   private static int projectionMethodS = 1;
/*  29 */   private static int initAngleS = 0;
/*  30 */   private static int totalAngleS = 360;
/*  31 */   private static int angleIncS = 10;
/*  32 */   private static int opacityS = 0;
/*  33 */   private static int depthCueSurfS = 0;
/*  34 */   private static int depthCueIntS = 50;
/*     */   private static boolean interpolateS;
/*     */   private static boolean allTimePointsS;
/*  38 */   private int axisOfRotation = axisOfRotationS;
/*  39 */   private int projectionMethod = projectionMethodS;
/*  40 */   private int initAngle = initAngleS;
/*  41 */   private int totalAngle = totalAngleS;
/*  42 */   private int angleInc = angleIncS;
/*  43 */   private int opacity = opacityS;
/*  44 */   private int depthCueSurf = depthCueSurfS;
/*  45 */   private int depthCueInt = depthCueIntS;
/*  46 */   private boolean interpolate = interpolateS;
/*  47 */   private boolean allTimePoints = allTimePointsS;
/*     */   private boolean debugMode;
/*  50 */   private double sliceInterval = 1.0D;
/*  51 */   private int transparencyLower = 1;
/*  52 */   private int transparencyUpper = 255;
/*     */   private ImagePlus imp;
/*     */   private ImageStack stack;
/*     */   private ImageStack stack2;
/*     */   private int width;
/*     */   private int height;
/*     */   private int imageWidth;
/*     */   private int left;
/*     */   private int right;
/*     */   private int top;
/*     */   private int bottom;
/*     */   private byte[] projArray;
/*     */   private byte[] opaArray;
/*     */   private byte[] brightCueArray;
/*     */   private short[] zBuffer;
/*     */   private short[] cueZBuffer;
/*     */   private short[] countBuffer;
/*     */   private int[] sumBuffer;
/*     */   private boolean isRGB;
/*  62 */   private String label = "";
/*     */   private boolean done;
/*  64 */   private boolean batchMode = Interpreter.isBatchMode();
/*  65 */   private double progressBase = 0.0D; private double progressScale = 1.0D;
/*  66 */   private boolean showMicroProgress = true;
/*     */ 
/*     */   public void run(String arg) {
/*  69 */     this.imp = IJ.getImage();
/*  70 */     if ((this.imp.getBitDepth() == 16) || (this.imp.getBitDepth() == 32)) {
/*  71 */       if ((!IJ.isMacro()) && 
/*  72 */         (!IJ.showMessageWithCancel("3D Project", "Convert this stack to 8-bits?"))) {
/*  73 */         return;
/*     */       }
/*  75 */       IJ.run(this.imp, "8-bit", "");
/*     */     }
/*  77 */     ImageProcessor ip = this.imp.getProcessor();
/*  78 */     if ((ip.isInvertedLut()) && (!IJ.isMacro()) && 
/*  79 */       (!IJ.showMessageWithCancel("3D Project", "Stacks with inverter LUTs may not project correctly.\nTo create a standard LUT, invert the stack (Edit/Invert)\nand invert the LUT (Image/Lookup Tables/Invert LUT)."))) {
/*  80 */       return;
/*     */     }
/*  82 */     if (!showDialog())
/*  83 */       return;
/*  84 */     this.imp.startTiming();
/*  85 */     this.isRGB = (this.imp.getType() == 4);
/*  86 */     if (this.imp.isHyperStack()) {
/*  87 */       if (this.imp.getNSlices() > 1)
/*  88 */         doHyperstackProjections(this.imp);
/*     */       else
/*  90 */         IJ.error("Hyperstack Z dimension must be greater than 1");
/*  91 */       return;
/*     */     }
/*  93 */     if ((this.interpolate) && (this.sliceInterval > 1.0D)) {
/*  94 */       this.imp = zScale(this.imp, true);
/*  95 */       if (this.imp == null) return;
/*  96 */       this.sliceInterval = 1.0D;
/*     */     }
/*  98 */     if (this.isRGB) {
/*  99 */       doRGBProjections(this.imp);
/*     */     } else {
/* 101 */       ImagePlus imp2 = doProjections(this.imp);
/* 102 */       if (imp2 != null)
/* 103 */         imp2.show();
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean showDialog() {
/* 108 */     ImageProcessor ip = this.imp.getProcessor();
/* 109 */     double lower = ip.getMinThreshold();
/* 110 */     if (lower != -808080.0D) {
/* 111 */       this.transparencyLower = ((int)lower);
/* 112 */       this.transparencyUpper = ((int)ip.getMaxThreshold());
/*     */     }
/* 114 */     Calibration cal = this.imp.getCalibration();
/* 115 */     boolean hyperstack = (this.imp.isHyperStack()) && (this.imp.getNFrames() > 1);
/* 116 */     GenericDialog gd = new GenericDialog("3D Projection");
/* 117 */     gd.addChoice("Projection Method:", methodList, methodList[this.projectionMethod]);
/* 118 */     gd.addChoice("Axis of Rotation:", axisList, axisList[this.axisOfRotation]);
/*     */ 
/* 120 */     gd.addNumericField("Slice Spacing (" + cal.getUnits() + "):", cal.pixelDepth, 2);
/*     */ 
/* 122 */     gd.addNumericField("Initial Angle (0-359 degrees):", this.initAngle, 0);
/* 123 */     gd.addNumericField("Total Rotation (0-359 degrees):", this.totalAngle, 0);
/* 124 */     gd.addNumericField("Rotation Angle Increment:", this.angleInc, 0);
/* 125 */     gd.addNumericField("Lower Transparency Bound:", this.transparencyLower, 0);
/* 126 */     gd.addNumericField("Upper Transparency Bound:", this.transparencyUpper, 0);
/* 127 */     gd.addNumericField("Opacity (0-100%):", this.opacity, 0);
/* 128 */     gd.addNumericField("Surface Depth-Cueing (0-100%):", 100 - this.depthCueSurf, 0);
/* 129 */     gd.addNumericField("Interior Depth-Cueing (0-100%):", 100 - this.depthCueInt, 0);
/* 130 */     gd.addCheckbox("Interpolate", this.interpolate);
/* 131 */     if (hyperstack) {
/* 132 */       gd.addCheckbox("All time points", this.allTimePoints);
/*     */     }
/*     */ 
/* 135 */     gd.showDialog();
/* 136 */     if (gd.wasCanceled())
/* 137 */       return false;
/* 138 */     this.projectionMethod = gd.getNextChoiceIndex();
/* 139 */     this.axisOfRotation = gd.getNextChoiceIndex();
/* 140 */     cal.pixelDepth = gd.getNextNumber();
/* 141 */     if (cal.pixelWidth == 0.0D) cal.pixelWidth = 1.0D;
/* 142 */     this.sliceInterval = (cal.pixelDepth / cal.pixelWidth);
/* 143 */     this.initAngle = ((int)gd.getNextNumber());
/* 144 */     this.totalAngle = ((int)gd.getNextNumber());
/* 145 */     this.angleInc = ((int)gd.getNextNumber());
/* 146 */     this.transparencyLower = ((int)gd.getNextNumber());
/* 147 */     this.transparencyUpper = ((int)gd.getNextNumber());
/* 148 */     this.opacity = ((int)gd.getNextNumber());
/* 149 */     this.depthCueSurf = (100 - (int)gd.getNextNumber());
/* 150 */     this.depthCueInt = (100 - (int)gd.getNextNumber());
/* 151 */     this.interpolate = gd.getNextBoolean();
/* 152 */     if (hyperstack) {
/* 153 */       this.allTimePoints = gd.getNextBoolean();
/*     */     }
/* 155 */     axisOfRotationS = this.axisOfRotation;
/* 156 */     projectionMethodS = this.projectionMethod;
/* 157 */     initAngleS = this.initAngle;
/* 158 */     totalAngleS = this.totalAngle;
/* 159 */     angleIncS = this.angleInc;
/* 160 */     opacityS = this.opacity;
/* 161 */     depthCueSurfS = this.depthCueSurf;
/* 162 */     depthCueIntS = this.depthCueInt;
/* 163 */     interpolateS = this.interpolate;
/* 164 */     allTimePointsS = this.allTimePoints;
/* 165 */     return true;
/*     */   }
/*     */ 
/*     */   private void doHyperstackProjections(ImagePlus imp) {
/* 169 */     double originalSliceInterval = this.sliceInterval;
/* 170 */     ImagePlus buildImp = null;
/* 171 */     ImagePlus projImpD = null;
/* 172 */     int finalChannels = imp.getNChannels();
/* 173 */     int finalSlices = imp.getNSlices();
/* 174 */     int finalFrames = imp.getNFrames();
/* 175 */     int f1 = 0;
/* 176 */     int f2 = imp.getNFrames() - 1;
/* 177 */     if (imp.getBitDepth() == 24)
/* 178 */       this.allTimePoints = false;
/* 179 */     if (!this.allTimePoints) {
/* 180 */       f1 = f2 = imp.getFrame();
/*     */     }
/* 182 */     int channels = imp.getNChannels();
/* 183 */     this.progressScale = (1.0D / channels);
/* 184 */     if (this.allTimePoints)
/* 185 */       this.showMicroProgress = false;
/* 186 */     int count = 1;
/* 187 */     for (int c = 0; c < channels; c++) {
/* 188 */       for (int f = f1; f <= f2; f++) {
/* 189 */         if (this.allTimePoints)
/* 190 */           IJ.showProgress(count++, channels * imp.getNFrames());
/* 191 */         this.sliceInterval = originalSliceInterval;
/* 192 */         ImagePlus impD = new Duplicator().run(imp, c + 1, c + 1, 1, imp.getNSlices(), f + 1, f + 1);
/* 193 */         impD.setCalibration(imp.getCalibration());
/* 194 */         if ((this.interpolate) && (this.sliceInterval > 1.0D)) {
/* 195 */           impD = zScale(impD, false);
/* 196 */           if (impD == null) return;
/* 197 */           this.sliceInterval = 1.0D;
/*     */         }
/* 199 */         if (this.isRGB) {
/* 200 */           doRGBProjections(impD);
/*     */         } else {
/* 202 */           this.progressBase = (c / channels);
/* 203 */           projImpD = doProjections(impD);
/* 204 */           if (projImpD == null) return;
/* 205 */           finalSlices = projImpD.getNSlices();
/* 206 */           impD.close();
/* 207 */           if (((f == 0) || (!this.allTimePoints)) && (c == 0)) {
/* 208 */             buildImp = projImpD;
/* 209 */             buildImp.setTitle("BuildStack");
/*     */           }
/*     */           else {
/* 212 */             Concatenator concat = new Concatenator();
/* 213 */             buildImp = concat.concatenate(buildImp, projImpD, false);
/*     */           }
/*     */         }
/* 216 */         if (this.done) return;
/*     */       }
/*     */     }
/* 219 */     if ((imp.getNFrames() == 1) || (!this.allTimePoints)) {
/* 220 */       finalFrames = finalSlices;
/* 221 */       finalSlices = 1;
/*     */     }
/* 223 */     if (imp.getNChannels() > 1) {
/* 224 */       IJ.run(buildImp, "Stack to Hyperstack...", "order=xyztc channels=" + finalChannels + " slices=" + finalSlices + " frames=" + finalFrames + " display=Composite");
/*     */     }
/* 226 */     buildImp = WindowManager.getCurrentImage();
/* 227 */     if (imp.isComposite()) {
/* 228 */       CompositeImage buildImp2 = new CompositeImage(buildImp, 0);
/* 229 */       buildImp2.copyLuts(imp);
/*     */ 
/* 231 */       buildImp = buildImp2;
/*     */     }
/* 233 */     buildImp.setTitle("Projections of " + imp.getShortTitle());
/* 234 */     buildImp.show();
/* 235 */     if (WindowManager.getImage("Concatenated Stacks") != null)
/* 236 */       WindowManager.getImage("Concatenated Stacks").hide();
/*     */   }
/*     */ 
/*     */   private void doRGBProjections(ImagePlus imp) {
/* 240 */     boolean saveUseInvertingLut = Prefs.useInvertingLut;
/* 241 */     Prefs.useInvertingLut = false;
/* 242 */     ImageStack[] channels = ChannelSplitter.splitRGB(imp.getStack(), true);
/* 243 */     ImagePlus red = new ImagePlus("Red", channels[0]);
/* 244 */     ImagePlus green = new ImagePlus("Green", channels[1]);
/* 245 */     ImagePlus blue = new ImagePlus("Blue", channels[2]);
/* 246 */     Calibration cal = imp.getCalibration();
/* 247 */     Roi roi = imp.getRoi();
/* 248 */     if (roi != null) {
/* 249 */       red.setRoi(roi); green.setRoi(roi); blue.setRoi(roi);
/* 250 */     }red.setCalibration(cal); green.setCalibration(cal); blue.setCalibration(cal);
/* 251 */     this.label = "Red: ";
/* 252 */     this.progressBase = 0.0D;
/* 253 */     this.progressScale = 0.3333333333333333D;
/* 254 */     red = doProjections(red);
/* 255 */     if ((red == null) || (this.done)) return;
/* 256 */     this.label = "Green: ";
/* 257 */     this.progressBase = 0.3333333333333333D;
/* 258 */     green = doProjections(green);
/* 259 */     if ((green == null) || (this.done)) return;
/* 260 */     this.label = "Blue: ";
/* 261 */     this.progressBase = 0.6666666666666666D;
/* 262 */     blue = doProjections(blue);
/* 263 */     if ((blue == null) || (this.done)) return;
/* 264 */     int w = red.getWidth(); int h = red.getHeight(); int d = red.getStackSize();
/* 265 */     RGBStackMerge merge = new RGBStackMerge();
/* 266 */     ImageStack stack = merge.mergeStacks(w, h, d, red.getStack(), green.getStack(), blue.getStack(), true);
/* 267 */     new ImagePlus("Projection of  " + imp.getShortTitle(), stack).show();
/* 268 */     Prefs.useInvertingLut = saveUseInvertingLut;
/*     */   }
/*     */ 
/*     */   private ImagePlus doProjections(ImagePlus imp)
/*     */   {
/* 281 */     boolean minProjSize = true;
/*     */ 
/* 283 */     this.stack = imp.getStack();
/* 284 */     if ((this.angleInc == 0) && (this.totalAngle != 0))
/* 285 */       this.angleInc = 5;
/* 286 */     boolean negInc = this.angleInc < 0;
/* 287 */     if (negInc) this.angleInc = (-this.angleInc);
/* 288 */     int angle = 0;
/* 289 */     int nProjections = 0;
/* 290 */     if (this.angleInc == 0)
/* 291 */       nProjections = 1;
/*     */     else {
/* 293 */       while (angle <= this.totalAngle) {
/* 294 */         nProjections++;
/* 295 */         angle += this.angleInc;
/*     */       }
/*     */     }
/* 298 */     if (angle > 360)
/* 299 */       nProjections--;
/* 300 */     if (nProjections <= 0)
/* 301 */       nProjections = 1;
/* 302 */     if (negInc) this.angleInc = (-this.angleInc);
/*     */ 
/* 304 */     ImageProcessor ip = imp.getProcessor();
/* 305 */     Rectangle r = ip.getRoi();
/* 306 */     this.left = r.x;
/* 307 */     this.top = r.y;
/* 308 */     this.right = (r.x + r.width);
/* 309 */     this.bottom = (r.y + r.height);
/* 310 */     int nSlices = imp.getStackSize();
/* 311 */     this.imageWidth = imp.getWidth();
/* 312 */     this.width = (this.right - this.left);
/* 313 */     this.height = (this.bottom - this.top);
/* 314 */     int xcenter = (this.left + this.right) / 2;
/* 315 */     int ycenter = (this.top + this.bottom) / 2;
/* 316 */     int zcenter = (int)(nSlices * this.sliceInterval / 2.0D + 0.5D);
/*     */ 
/* 318 */     int projwidth = 0;
/* 319 */     int projheight = 0;
/* 320 */     if ((minProjSize) && (this.axisOfRotation != 2)) {
/* 321 */       switch (this.axisOfRotation) {
/*     */       case 0:
/* 323 */         projheight = (int)(Math.sqrt(nSlices * this.sliceInterval * nSlices * this.sliceInterval + this.height * this.height) + 0.5D);
/* 324 */         projwidth = this.width;
/* 325 */         break;
/*     */       case 1:
/* 327 */         projwidth = (int)(Math.sqrt(nSlices * this.sliceInterval * nSlices * this.sliceInterval + this.width * this.width) + 0.5D);
/* 328 */         projheight = this.height;
/*     */       }
/*     */     }
/*     */     else {
/* 332 */       projwidth = (int)(Math.sqrt(nSlices * this.sliceInterval * nSlices * this.sliceInterval + this.width * this.width) + 0.5D);
/* 333 */       projheight = (int)(Math.sqrt(nSlices * this.sliceInterval * nSlices * this.sliceInterval + this.height * this.height) + 0.5D);
/*     */     }
/* 335 */     if (projwidth % 2 == 1)
/* 336 */       projwidth++;
/* 337 */     int projsize = projwidth * projheight;
/*     */ 
/* 339 */     if ((projwidth <= 0) || (projheight <= 0)) {
/* 340 */       IJ.error("'projwidth' or 'projheight' <= 0");
/* 341 */       return null;
/*     */     }
/*     */     try {
/* 344 */       allocateArrays(nProjections, projwidth, projheight);
/*     */     } catch (OutOfMemoryError e) {
/* 346 */       Object[] images = this.stack2.getImageArray();
/* 347 */       if (images != null)
/* 348 */         for (int i = 0; i < images.length; i++) images[i] = null;
/* 349 */       this.stack2 = null;
/* 350 */       IJ.error("Projector - Out of Memory", "To use less memory, use a rectanguar\nselection,  reduce \"Total Rotation\",\nand/or increase \"Angle Increment\".");
/*     */ 
/* 355 */       return null;
/*     */     }
/* 357 */     ImagePlus projections = new ImagePlus("Projections of " + imp.getShortTitle(), this.stack2);
/* 358 */     projections.setCalibration(imp.getCalibration());
/*     */ 
/* 361 */     IJ.resetEscape();
/* 362 */     int theta = this.initAngle;
/* 363 */     IJ.resetEscape();
/* 364 */     for (int n = 0; n < nProjections; n++) {
/* 365 */       IJ.showStatus(n + "/" + nProjections);
/* 366 */       showProgress(n / nProjections);
/* 367 */       double thetarad = theta * 3.141592653589793D / 180.0D;
/* 368 */       int costheta = (int)(8192.0D * Math.cos(thetarad) + 0.5D);
/* 369 */       int sintheta = (int)(8192.0D * Math.sin(thetarad) + 0.5D);
/*     */ 
/* 371 */       this.projArray = ((byte[])this.stack2.getPixels(n + 1));
/* 372 */       if (this.projArray == null)
/*     */         break;
/* 374 */       if ((this.projectionMethod == 0) || (this.opacity > 0)) {
/* 375 */         for (int i = 0; i < projsize; i++)
/* 376 */           this.zBuffer[i] = 32767;
/*     */       }
/* 378 */       if ((this.opacity > 0) && (this.projectionMethod != 0)) {
/* 379 */         for (int i = 0; i < projsize; i++)
/* 380 */           this.opaArray[i] = 0;
/*     */       }
/* 382 */       if ((this.projectionMethod == 1) && (this.depthCueInt < 100)) {
/* 383 */         for (int i = 0; i < projsize; i++)
/* 384 */           this.brightCueArray[i] = 0;
/* 385 */         for (int i = 0; i < projsize; i++)
/* 386 */           this.cueZBuffer[i] = 0;
/*     */       }
/* 388 */       if (this.projectionMethod == 2) {
/* 389 */         for (int i = 0; i < projsize; i++)
/* 390 */           this.sumBuffer[i] = 0;
/* 391 */         for (int i = 0; i < projsize; i++)
/* 392 */           this.countBuffer[i] = 0;
/*     */       }
/* 394 */       switch (this.axisOfRotation) {
/*     */       case 0:
/* 396 */         doOneProjectionX(nSlices, ycenter, zcenter, projwidth, projheight, costheta, sintheta);
/* 397 */         break;
/*     */       case 1:
/* 399 */         doOneProjectionY(nSlices, xcenter, zcenter, projwidth, projheight, costheta, sintheta);
/* 400 */         break;
/*     */       case 2:
/* 402 */         doOneProjectionZ(nSlices, xcenter, ycenter, zcenter, projwidth, projheight, costheta, sintheta);
/*     */       }
/*     */ 
/* 406 */       if (this.projectionMethod == 2)
/*     */       {
/* 408 */         for (int i = 0; i < projsize; i++) {
/* 409 */           int count = this.countBuffer[i];
/* 410 */           if (count != 0)
/* 411 */             this.projArray[i] = ((byte)(this.sumBuffer[i] / count));
/*     */         }
/*     */       }
/* 414 */       if ((this.opacity > 0) && (this.projectionMethod != 0)) {
/* 415 */         for (int i = 0; i < projsize; i++)
/* 416 */           this.projArray[i] = ((byte)((this.opacity * (this.opaArray[i] & 0xFF) + (100 - this.opacity) * (this.projArray[i] & 0xFF)) / 100));
/*     */       }
/* 418 */       if (this.axisOfRotation == 2) {
/* 419 */         for (int i = projwidth; i < projsize - projwidth; i++) {
/* 420 */           int curval = this.projArray[i] & 0xFF;
/* 421 */           int prevval = this.projArray[(i - 1)] & 0xFF;
/* 422 */           int nextval = this.projArray[(i + 1)] & 0xFF;
/* 423 */           int aboveval = this.projArray[(i - projwidth)] & 0xFF;
/* 424 */           int belowval = this.projArray[(i + projwidth)] & 0xFF;
/* 425 */           if ((curval == 0) && (prevval != 0) && (nextval != 0) && (aboveval != 0) && (belowval != 0)) {
/* 426 */             this.projArray[i] = ((byte)((prevval + nextval + aboveval + belowval) / 4));
/*     */           }
/*     */         }
/*     */       }
/* 430 */       theta = (theta + this.angleInc) % 360;
/*     */ 
/* 433 */       if (IJ.escapePressed()) {
/* 434 */         this.done = true;
/* 435 */         IJ.beep();
/* 436 */         IJ.showProgress(1.0D);
/* 437 */         IJ.showStatus("aborted");
/* 438 */         break;
/*     */       }
/* 440 */       projections.setSlice(n + 1);
/*     */     }
/* 442 */     showProgress(1.0D);
/*     */ 
/* 444 */     if (this.debugMode) {
/* 445 */       if (this.projArray != null) new ImagePlus("projArray", new ByteProcessor(projwidth, projheight, this.projArray, null)).show();
/* 446 */       if (this.opaArray != null) new ImagePlus("opaArray", new ByteProcessor(projwidth, projheight, this.opaArray, null)).show();
/* 447 */       if (this.brightCueArray != null) new ImagePlus("brightCueArray", new ByteProcessor(projwidth, projheight, this.brightCueArray, null)).show();
/* 448 */       if (this.zBuffer != null) new ImagePlus("zBuffer", new ShortProcessor(projwidth, projheight, this.zBuffer, null)).show();
/* 449 */       if (this.cueZBuffer != null) new ImagePlus("cueZBuffer", new ShortProcessor(projwidth, projheight, this.cueZBuffer, null)).show();
/* 450 */       if (this.countBuffer != null) new ImagePlus("countBuffer", new ShortProcessor(projwidth, projheight, this.countBuffer, null)).show();
/* 451 */       if (this.sumBuffer != null) {
/* 452 */         float[] tmp = new float[projwidth * projheight];
/* 453 */         for (int i = 0; i < projwidth * projheight; i++)
/* 454 */           tmp[i] = this.sumBuffer[i];
/* 455 */         new ImagePlus("sumBuffer", new FloatProcessor(projwidth, projheight, tmp, null)).show();
/*     */       }
/*     */     }
/*     */ 
/* 459 */     return projections;
/*     */   }
/*     */ 
/*     */   private void allocateArrays(int nProjections, int projwidth, int projheight)
/*     */   {
/* 465 */     int projsize = projwidth * projheight;
/* 466 */     ColorModel cm = this.imp.getProcessor().getColorModel();
/* 467 */     if (this.isRGB) cm = null;
/* 468 */     this.stack2 = new ImageStack(projwidth, projheight, cm);
/* 469 */     this.projArray = new byte[projsize];
/* 470 */     for (int i = 0; i < nProjections; i++)
/* 471 */       this.stack2.addSlice(null, new byte[projsize]);
/* 472 */     if ((this.projectionMethod == 0) || (this.opacity > 0))
/* 473 */       this.zBuffer = new short[projsize];
/* 474 */     if ((this.opacity > 0) && (this.projectionMethod != 0))
/* 475 */       this.opaArray = new byte[projsize];
/* 476 */     if ((this.projectionMethod == 1) && (this.depthCueInt < 100)) {
/* 477 */       this.brightCueArray = new byte[projsize];
/* 478 */       this.cueZBuffer = new short[projsize];
/*     */     }
/* 480 */     if (this.projectionMethod == 2) {
/* 481 */       this.sumBuffer = new int[projsize];
/* 482 */       this.countBuffer = new short[projsize];
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doOneProjectionX(int nSlices, int ycenter, int zcenter, int projwidth, int projheight, int costheta, int sintheta)
/*     */   {
/* 508 */     int projsize = projwidth * projheight;
/*     */ 
/* 511 */     int zmax = zcenter + projheight / 2;
/* 512 */     int zmin = zcenter - projheight / 2;
/* 513 */     int zmaxminuszmintimes100 = 100 * (zmax - zmin);
/* 514 */     int c100minusDepthCueInt = 100 - this.depthCueInt;
/* 515 */     int c100minusDepthCueSurf = 100 - this.depthCueSurf;
/* 516 */     boolean DepthCueIntLessThan100 = this.depthCueInt < 100;
/* 517 */     boolean DepthCueSurfLessThan100 = this.depthCueSurf < 100;
/* 518 */     boolean OpacityOrNearestPt = (this.projectionMethod == 0) || (this.opacity > 0);
/* 519 */     boolean OpacityAndNotNearestPt = (this.opacity > 0) && (this.projectionMethod != 0);
/* 520 */     boolean MeanVal = this.projectionMethod == 2;
/* 521 */     boolean BrightestPt = this.projectionMethod == 1;
/* 522 */     int ycosthetainit = (this.top - ycenter - 1) * costheta;
/* 523 */     int ysinthetainit = (this.top - ycenter - 1) * sintheta;
/* 524 */     int offsetinit = (projheight - this.bottom + this.top) / 2 * projwidth + (projwidth - this.right + this.left) / 2 - 1;
/*     */ 
/* 526 */     for (int k = 1; k <= nSlices; k++) {
/* 527 */       byte[] pixels = (byte[])this.stack.getPixels(k);
/* 528 */       int z = (int)((k - 1) * this.sliceInterval + 0.5D) - zcenter;
/* 529 */       int zcostheta = z * costheta;
/* 530 */       int zsintheta = z * sintheta;
/* 531 */       int ycostheta = ycosthetainit;
/* 532 */       int ysintheta = ysinthetainit;
/* 533 */       for (int j = this.top; j < this.bottom; j++) {
/* 534 */         ycostheta += costheta;
/* 535 */         ysintheta += sintheta;
/* 536 */         int ynew = (ycostheta - zsintheta) / 8192 + ycenter - this.top;
/* 537 */         int znew = (ysintheta + zcostheta) / 8192 + zcenter;
/* 538 */         int offset = offsetinit + ynew * projwidth;
/*     */ 
/* 541 */         int lineIndex = j * this.imageWidth;
/* 542 */         for (int i = this.left; i < this.right; i++) {
/* 543 */           int thispixel = pixels[(lineIndex + i)] & 0xFF;
/*     */ 
/* 545 */           offset++;
/* 546 */           if ((offset >= projsize) || (offset < 0))
/* 547 */             offset = 0;
/* 548 */           if ((thispixel <= this.transparencyUpper) && (thispixel >= this.transparencyLower)) {
/* 549 */             if ((OpacityOrNearestPt) && 
/* 550 */               (znew < this.zBuffer[offset])) {
/* 551 */               this.zBuffer[offset] = ((short)znew);
/* 552 */               if (OpacityAndNotNearestPt) {
/* 553 */                 if (DepthCueSurfLessThan100) {
/* 554 */                   this.opaArray[offset] = ((byte)(this.depthCueSurf * thispixel / 100 + c100minusDepthCueSurf * thispixel * (zmax - znew) / zmaxminuszmintimes100));
/*     */                 }
/*     */                 else {
/* 557 */                   this.opaArray[offset] = ((byte)thispixel);
/*     */                 }
/*     */               }
/* 560 */               else if (DepthCueSurfLessThan100) {
/* 561 */                 this.projArray[offset] = ((byte)(this.depthCueSurf * thispixel / 100 + c100minusDepthCueSurf * thispixel * (zmax - znew) / zmaxminuszmintimes100));
/*     */               }
/*     */               else {
/* 564 */                 this.projArray[offset] = ((byte)thispixel);
/*     */               }
/*     */             }
/*     */ 
/* 568 */             if (MeanVal)
/*     */             {
/* 570 */               this.sumBuffer[offset] += thispixel;
/*     */               int tmp588_586 = offset;
/*     */               short[] tmp588_583 = this.countBuffer; tmp588_583[tmp588_586] = ((short)(tmp588_583[tmp588_586] + 1));
/*     */             }
/* 574 */             else if (BrightestPt) {
/* 575 */               if (DepthCueIntLessThan100) {
/* 576 */                 if ((thispixel > (this.brightCueArray[offset] & 0xFF)) || ((thispixel == (this.brightCueArray[offset] & 0xFF)) && (znew > this.cueZBuffer[offset]))) {
/* 577 */                   this.brightCueArray[offset] = ((byte)thispixel);
/* 578 */                   this.cueZBuffer[offset] = ((short)znew);
/* 579 */                   this.projArray[offset] = ((byte)(this.depthCueInt * thispixel / 100 + c100minusDepthCueInt * thispixel * (zmax - znew) / zmaxminuszmintimes100));
/*     */                 }
/*     */ 
/*     */               }
/* 583 */               else if (thispixel > (this.projArray[offset] & 0xFF))
/* 584 */                 this.projArray[offset] = ((byte)thispixel);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doOneProjectionY(int nSlices, int xcenter, int zcenter, int projwidth, int projheight, int costheta, int sintheta)
/*     */   {
/* 610 */     int projsize = projwidth * projheight;
/*     */ 
/* 613 */     int zmax = zcenter + projwidth / 2;
/* 614 */     int zmin = zcenter - projwidth / 2;
/* 615 */     int zmaxminuszmintimes100 = 100 * (zmax - zmin);
/* 616 */     int c100minusDepthCueInt = 100 - this.depthCueInt;
/* 617 */     int c100minusDepthCueSurf = 100 - this.depthCueSurf;
/* 618 */     boolean DepthCueIntLessThan100 = this.depthCueInt < 100;
/* 619 */     boolean DepthCueSurfLessThan100 = this.depthCueSurf < 100;
/* 620 */     boolean OpacityOrNearestPt = (this.projectionMethod == 0) || (this.opacity > 0);
/* 621 */     boolean OpacityAndNotNearestPt = (this.opacity > 0) && (this.projectionMethod != 0);
/* 622 */     boolean MeanVal = this.projectionMethod == 2;
/* 623 */     boolean BrightestPt = this.projectionMethod == 1;
/* 624 */     int xcosthetainit = (this.left - xcenter - 1) * costheta;
/* 625 */     int xsinthetainit = (this.left - xcenter - 1) * sintheta;
/* 626 */     for (int k = 1; k <= nSlices; k++) {
/* 627 */       byte[] pixels = (byte[])this.stack.getPixels(k);
/* 628 */       int z = (int)((k - 1) * this.sliceInterval + 0.5D) - zcenter;
/* 629 */       int zcostheta = z * costheta;
/* 630 */       int zsintheta = z * sintheta;
/* 631 */       int offsetinit = (projheight - this.bottom + this.top) / 2 * projwidth + (projwidth - this.right + this.left) / 2 - projwidth;
/* 632 */       for (int j = this.top; j < this.bottom; j++) {
/* 633 */         int xcostheta = xcosthetainit;
/* 634 */         int xsintheta = xsinthetainit;
/* 635 */         offsetinit += projwidth;
/* 636 */         int lineOffset = j * this.imageWidth;
/*     */ 
/* 638 */         for (int i = this.left; i < this.right; i++) {
/* 639 */           int thispixel = pixels[(lineOffset + i)] & 0xFF;
/* 640 */           xcostheta += costheta;
/* 641 */           xsintheta += sintheta;
/*     */ 
/* 643 */           if ((thispixel <= this.transparencyUpper) && (thispixel >= this.transparencyLower)) {
/* 644 */             int xnew = (xcostheta + zsintheta) / 8192 + xcenter - this.left;
/* 645 */             int znew = (zcostheta - xsintheta) / 8192 + zcenter;
/* 646 */             int offset = offsetinit + xnew;
/* 647 */             if ((offset >= projsize) || (offset < 0))
/* 648 */               offset = 0;
/* 649 */             if ((OpacityOrNearestPt) && 
/* 650 */               (znew < this.zBuffer[offset])) {
/* 651 */               this.zBuffer[offset] = ((short)znew);
/* 652 */               if (OpacityAndNotNearestPt) {
/* 653 */                 if (DepthCueSurfLessThan100) {
/* 654 */                   this.opaArray[offset] = ((byte)(this.depthCueSurf * thispixel / 100 + c100minusDepthCueSurf * thispixel * (zmax - znew) / zmaxminuszmintimes100));
/*     */                 }
/*     */                 else
/* 657 */                   this.opaArray[offset] = ((byte)thispixel);
/*     */               }
/* 659 */               else if (DepthCueSurfLessThan100) {
/* 660 */                 this.projArray[offset] = ((byte)(this.depthCueSurf * thispixel / 100 + c100minusDepthCueSurf * thispixel * (zmax - znew) / zmaxminuszmintimes100));
/*     */               }
/*     */               else {
/* 663 */                 this.projArray[offset] = ((byte)thispixel);
/*     */               }
/*     */             }
/*     */ 
/* 667 */             if (MeanVal) {
/* 668 */               this.sumBuffer[offset] += thispixel;
/*     */               int tmp590_588 = offset;
/*     */               short[] tmp590_585 = this.countBuffer; tmp590_585[tmp590_588] = ((short)(tmp590_585[tmp590_588] + 1));
/* 670 */             } else if (BrightestPt) {
/* 671 */               if (DepthCueIntLessThan100) {
/* 672 */                 if ((thispixel > (this.brightCueArray[offset] & 0xFF)) || ((thispixel == (this.brightCueArray[offset] & 0xFF)) && (znew > this.cueZBuffer[offset]))) {
/* 673 */                   this.brightCueArray[offset] = ((byte)thispixel);
/* 674 */                   this.cueZBuffer[offset] = ((short)znew);
/* 675 */                   this.projArray[offset] = ((byte)(this.depthCueInt * thispixel / 100 + c100minusDepthCueInt * thispixel * (zmax - znew) / zmaxminuszmintimes100));
/*     */                 }
/*     */ 
/*     */               }
/* 679 */               else if (thispixel > (this.projArray[offset] & 0xFF))
/* 680 */                 this.projArray[offset] = ((byte)thispixel);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doOneProjectionZ(int nSlices, int xcenter, int ycenter, int zcenter, int projwidth, int projheight, int costheta, int sintheta)
/*     */   {
/* 705 */     int projsize = projwidth * projheight;
/*     */ 
/* 710 */     int zmax = (int)((nSlices - 1) * this.sliceInterval + 0.5D) - zcenter;
/* 711 */     int zmin = -zcenter;
/*     */ 
/* 713 */     int zmaxminuszmintimes100 = 100 * (zmax - zmin);
/* 714 */     int c100minusDepthCueInt = 100 - this.depthCueInt;
/* 715 */     int c100minusDepthCueSurf = 100 - this.depthCueSurf;
/* 716 */     boolean DepthCueIntLessThan100 = this.depthCueInt < 100;
/* 717 */     boolean DepthCueSurfLessThan100 = this.depthCueSurf < 100;
/* 718 */     boolean OpacityOrNearestPt = (this.projectionMethod == 0) || (this.opacity > 0);
/* 719 */     boolean OpacityAndNotNearestPt = (this.opacity > 0) && (this.projectionMethod != 0);
/* 720 */     boolean MeanVal = this.projectionMethod == 2;
/* 721 */     boolean BrightestPt = this.projectionMethod == 1;
/* 722 */     int xcosthetainit = (this.left - xcenter - 1) * costheta;
/* 723 */     int xsinthetainit = (this.left - xcenter - 1) * sintheta;
/* 724 */     int ycosthetainit = (this.top - ycenter - 1) * costheta;
/* 725 */     int ysinthetainit = (this.top - ycenter - 1) * sintheta;
/*     */ 
/* 734 */     int offsetinit = (projheight - this.bottom + this.top) / 2 * projwidth + (projwidth - this.right + this.left) / 2 - 1;
/* 735 */     for (int k = 1; k <= nSlices; k++) {
/* 736 */       byte[] pixels = (byte[])this.stack.getPixels(k);
/* 737 */       int z = (int)((k - 1) * this.sliceInterval + 0.5D) - zcenter;
/* 738 */       int ycostheta = ycosthetainit;
/* 739 */       int ysintheta = ysinthetainit;
/* 740 */       for (int j = this.top; j < this.bottom; j++) {
/* 741 */         ycostheta += costheta;
/* 742 */         ysintheta += sintheta;
/* 743 */         int xcostheta = xcosthetainit;
/* 744 */         int xsintheta = xsinthetainit;
/*     */ 
/* 746 */         int lineIndex = j * this.imageWidth;
/*     */ 
/* 748 */         for (int i = this.left; i < this.right; i++) {
/* 749 */           int thispixel = pixels[(lineIndex + i)] & 0xFF;
/* 750 */           xcostheta += costheta;
/* 751 */           xsintheta += sintheta;
/* 752 */           if ((thispixel <= this.transparencyUpper) && (thispixel >= this.transparencyLower)) {
/* 753 */             int xnew = (xcostheta - ysintheta) / 8192 + xcenter - this.left;
/* 754 */             int ynew = (xsintheta + ycostheta) / 8192 + ycenter - this.top;
/* 755 */             int offset = offsetinit + ynew * projwidth + xnew;
/* 756 */             if ((offset >= projsize) || (offset < 0))
/* 757 */               offset = 0;
/* 758 */             if ((OpacityOrNearestPt) && 
/* 759 */               (z < this.zBuffer[offset])) {
/* 760 */               this.zBuffer[offset] = ((short)z);
/* 761 */               if (OpacityAndNotNearestPt) {
/* 762 */                 if (DepthCueSurfLessThan100)
/* 763 */                   this.opaArray[offset] = ((byte)(this.depthCueSurf * thispixel / 100 + c100minusDepthCueSurf * thispixel * (zmax - z) / zmaxminuszmintimes100));
/*     */                 else
/* 765 */                   this.opaArray[offset] = ((byte)thispixel);
/*     */               }
/* 767 */               else if (DepthCueSurfLessThan100) {
/* 768 */                 int v = this.depthCueSurf * thispixel / 100 + c100minusDepthCueSurf * thispixel * (zmax - z) / zmaxminuszmintimes100;
/*     */ 
/* 770 */                 this.projArray[offset] = ((byte)v);
/*     */               } else {
/* 772 */                 this.projArray[offset] = ((byte)thispixel);
/*     */               }
/*     */             }
/*     */ 
/* 776 */             if (MeanVal) {
/* 777 */               this.sumBuffer[offset] += thispixel;
/*     */               int tmp640_638 = offset;
/*     */               short[] tmp640_635 = this.countBuffer; tmp640_635[tmp640_638] = ((short)(tmp640_635[tmp640_638] + 1));
/* 779 */             } else if (BrightestPt) {
/* 780 */               if (DepthCueIntLessThan100) {
/* 781 */                 if ((thispixel > (this.brightCueArray[offset] & 0xFF)) || ((thispixel == (this.brightCueArray[offset] & 0xFF)) && (z > this.cueZBuffer[offset]))) {
/* 782 */                   this.brightCueArray[offset] = ((byte)thispixel);
/* 783 */                   this.cueZBuffer[offset] = ((short)z);
/* 784 */                   this.projArray[offset] = ((byte)(this.depthCueInt * thispixel / 100 + c100minusDepthCueInt * thispixel * (zmax - z) / zmaxminuszmintimes100));
/*     */                 }
/*     */ 
/*     */               }
/* 788 */               else if (thispixel > (this.projArray[offset] & 0xFF))
/* 789 */                 this.projArray[offset] = ((byte)thispixel);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private ImagePlus zScale(ImagePlus imp, boolean showProgress)
/*     */   {
/* 800 */     IJ.showStatus("Z Scaling...");
/* 801 */     ImageStack stack1 = imp.getStack();
/* 802 */     int depth1 = stack1.getSize();
/* 803 */     ImagePlus imp2 = null;
/* 804 */     String title = imp.getTitle();
/* 805 */     ImageProcessor ip = imp.getProcessor();
/* 806 */     ColorModel cm = ip.getColorModel();
/* 807 */     int width1 = imp.getWidth();
/* 808 */     int height1 = imp.getHeight();
/* 809 */     Rectangle r = ip.getRoi();
/* 810 */     int width2 = r.width;
/* 811 */     int height2 = r.height;
/* 812 */     int depth2 = (int)(stack1.getSize() * this.sliceInterval + 0.5D);
/* 813 */     imp2 = NewImage.createImage(title, width2, height2, depth2, this.isRGB ? 24 : 8, 1);
/* 814 */     if ((imp2 == null) || (depth2 != imp2.getStackSize())) return null;
/* 815 */     ImageStack stack2 = imp2.getStack();
/* 816 */     ImageProcessor xzPlane1 = ip.createProcessor(width2, depth1);
/* 817 */     xzPlane1.setInterpolate(true);
/*     */ 
/* 819 */     int[] line = new int[width2];
/* 820 */     for (int y = 0; y < height2; y++) {
/* 821 */       for (int z = 0; z < depth1; z++) {
/* 822 */         if (this.isRGB)
/* 823 */           getRGBRow(stack1, r.x, r.y + y, z, width1, width2, line);
/*     */         else
/* 825 */           getByteRow(stack1, r.x, r.y + y, z, width1, width2, line);
/* 826 */         xzPlane1.putRow(0, z, line, width2);
/*     */       }
/*     */ 
/* 829 */       xzPlane1.setProgressBar(null);
/* 830 */       ImageProcessor xzPlane2 = xzPlane1.resize(width2, depth2);
/* 831 */       for (int z = 0; z < depth2; z++) {
/* 832 */         xzPlane2.getRow(0, z, line, width2);
/* 833 */         if (this.isRGB)
/* 834 */           putRGBRow(stack2, y, z, width2, line);
/*     */         else
/* 836 */           putByteRow(stack2, y, z, width2, line);
/*     */       }
/* 838 */       if (showProgress) {
/* 839 */         IJ.showProgress(y, height2 - 1);
/*     */       }
/*     */     }
/*     */ 
/* 843 */     ImageProcessor ip2 = imp2.getProcessor();
/* 844 */     ip2.setColorModel(cm);
/* 845 */     return imp2;
/*     */   }
/*     */ 
/*     */   private void showProgress(double percent) {
/* 849 */     if ((this.showMicroProgress) && (!this.done))
/* 850 */       IJ.showProgress(this.progressBase + percent * this.progressScale);
/*     */   }
/*     */ 
/*     */   private void getByteRow(ImageStack stack, int x, int y, int z, int width1, int width2, int[] line) {
/* 854 */     byte[] pixels = (byte[])stack.getPixels(z + 1);
/* 855 */     int j = x + y * width1;
/* 856 */     for (int i = 0; i < width2; i++)
/* 857 */       line[i] = (pixels[(j++)] & 0xFF);
/*     */   }
/*     */ 
/*     */   private void putByteRow(ImageStack stack, int y, int z, int width, int[] line) {
/* 861 */     byte[] pixels = (byte[])stack.getPixels(z + 1);
/* 862 */     int j = y * width;
/* 863 */     for (int i = 0; i < width; i++)
/* 864 */       pixels[(j++)] = ((byte)line[i]);
/*     */   }
/*     */ 
/*     */   private void getRGBRow(ImageStack stack, int x, int y, int z, int width1, int width2, int[] line) {
/* 868 */     int[] pixels = (int[])stack.getPixels(z + 1);
/* 869 */     int j = x + y * width1;
/* 870 */     for (int i = 0; i < width2; i++)
/* 871 */       line[i] = pixels[(j++)];
/*     */   }
/*     */ 
/*     */   private void putRGBRow(ImageStack stack, int y, int z, int width, int[] line) {
/* 875 */     int[] pixels = (int[])stack.getPixels(z + 1);
/* 876 */     int j = y * width;
/* 877 */     for (int i = 0; i < width; i++)
/* 878 */       pixels[(j++)] = line[i];
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Projector
 * JD-Core Version:    0.6.2
 */