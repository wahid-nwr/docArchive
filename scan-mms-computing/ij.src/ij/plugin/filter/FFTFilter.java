/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.measure.Measurements;
/*     */ import ij.plugin.ContrastEnhancer;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FHT;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class FFTFilter
/*     */   implements PlugInFilter, Measurements
/*     */ {
/*     */   private ImagePlus imp;
/*     */   private String arg;
/*  23 */   private static int filterIndex = 1;
/*     */   private FHT fht;
/*     */   private int slice;
/*  26 */   private int stackSize = 1;
/*     */ 
/*  28 */   private static double filterLargeDia = 40.0D;
/*  29 */   private static double filterSmallDia = 3.0D;
/*  30 */   private static int choiceIndex = 0;
/*  31 */   private static String[] choices = { "None", "Horizontal", "Vertical" };
/*  32 */   private static String choiceDia = choices[0];
/*  33 */   private static double toleranceDia = 5.0D;
/*  34 */   private static boolean doScalingDia = true;
/*  35 */   private static boolean saturateDia = true;
/*     */   private static boolean displayFilter;
/*     */   private static boolean processStack;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  40 */     this.arg = arg;
/*  41 */     this.imp = imp;
/*  42 */     if (imp == null) {
/*  43 */       IJ.noImage(); return 4096;
/*  44 */     }this.stackSize = imp.getStackSize();
/*  45 */     this.fht = ((FHT)imp.getProperty("FHT"));
/*  46 */     if (this.fht != null) {
/*  47 */       IJ.error("FFT Filter", "Spatial domain image required");
/*  48 */       return 4096;
/*     */     }
/*  50 */     if (!showBandpassDialog(imp)) {
/*  51 */       return 4096;
/*     */     }
/*  53 */     return processStack ? 32831 : 31;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  57 */     this.slice += 1;
/*  58 */     filter(ip);
/*     */   }
/*     */ 
/*     */   void filter(ImageProcessor ip) {
/*  62 */     ImageProcessor ip2 = ip;
/*  63 */     if ((ip2 instanceof ColorProcessor)) {
/*  64 */       showStatus("Extracting brightness");
/*  65 */       ip2 = ((ColorProcessor)ip2).getBrightness();
/*     */     }
/*  67 */     Rectangle roiRect = ip2.getRoi();
/*  68 */     int maxN = Math.max(roiRect.width, roiRect.height);
/*  69 */     double sharpness = (100.0D - toleranceDia) / 100.0D;
/*  70 */     boolean doScaling = doScalingDia;
/*  71 */     boolean saturate = saturateDia;
/*     */ 
/*  73 */     IJ.showProgress(1, 20);
/*     */ 
/*  79 */     int i = 2;
/*  80 */     while (i < 1.5D * maxN) i *= 2;
/*     */ 
/*  83 */     double filterLarge = 2.0D * filterLargeDia / i;
/*  84 */     double filterSmall = 2.0D * filterSmallDia / i;
/*     */ 
/*  87 */     Rectangle fitRect = new Rectangle();
/*  88 */     fitRect.x = ((int)Math.round((i - roiRect.width) / 2.0D));
/*  89 */     fitRect.y = ((int)Math.round((i - roiRect.height) / 2.0D));
/*  90 */     fitRect.width = roiRect.width;
/*  91 */     fitRect.height = roiRect.height;
/*     */ 
/*  95 */     showStatus("Pad to " + i + "x" + i);
/*  96 */     ip2 = tileMirror(ip2, i, i, fitRect.x, fitRect.y);
/*  97 */     IJ.showProgress(2, 20);
/*     */ 
/* 100 */     showStatus(i + "x" + i + " forward transform");
/* 101 */     FHT fht = new FHT(ip2);
/* 102 */     fht.setShowProgress(false);
/* 103 */     fht.transform();
/* 104 */     IJ.showProgress(9, 20);
/*     */ 
/* 108 */     showStatus("Filter in frequency domain");
/* 109 */     filterLargeSmall(fht, filterLarge, filterSmall, choiceIndex, sharpness);
/*     */ 
/* 111 */     IJ.showProgress(11, 20);
/*     */ 
/* 114 */     showStatus("Inverse transform");
/* 115 */     fht.inverseTransform();
/* 116 */     IJ.showProgress(19, 20);
/*     */ 
/* 120 */     showStatus("Crop and convert to original type");
/* 121 */     fht.setRoi(fitRect);
/* 122 */     ip2 = fht.crop();
/* 123 */     if (doScaling) {
/* 124 */       ImagePlus imp2 = new ImagePlus(this.imp.getTitle() + "-filtered", ip2);
/* 125 */       new ContrastEnhancer().stretchHistogram(imp2, saturate ? 1.0D : 0.0D);
/* 126 */       ip2 = imp2.getProcessor();
/*     */     }
/*     */ 
/* 130 */     int bitDepth = this.imp.getBitDepth();
/* 131 */     switch (bitDepth) { case 8:
/* 132 */       ip2 = ip2.convertToByte(doScaling); break;
/*     */     case 16:
/* 133 */       ip2 = ip2.convertToShort(doScaling); break;
/*     */     case 24:
/* 135 */       ip.snapshot();
/* 136 */       showStatus("Setting brightness");
/* 137 */       ((ColorProcessor)ip).setBrightness((FloatProcessor)ip2);
/* 138 */       break;
/*     */     case 32:
/*     */     }
/*     */ 
/* 143 */     if (bitDepth != 24) {
/* 144 */       ip.snapshot();
/* 145 */       ip.copyBits(ip2, roiRect.x, roiRect.y, 0);
/*     */     }
/* 147 */     ip.resetMinAndMax();
/* 148 */     IJ.showProgress(20, 20);
/*     */   }
/*     */ 
/*     */   void showStatus(String msg) {
/* 152 */     if ((this.stackSize > 1) && (processStack))
/* 153 */       IJ.showStatus("FFT Filter: " + this.slice + "/" + this.stackSize);
/*     */     else
/* 155 */       IJ.showStatus(msg);
/*     */   }
/*     */ 
/*     */   public ImageProcessor tileMirror(ImageProcessor ip, int width, int height, int x, int y)
/*     */   {
/* 161 */     if (IJ.debugMode) IJ.log("FFT.tileMirror: " + width + "x" + height + " " + ip);
/* 162 */     if ((x < 0) || (x > width - 1) || (y < 0) || (y > height - 1)) {
/* 163 */       IJ.error("Image to be tiled is out of bounds.");
/* 164 */       return null;
/*     */     }
/*     */ 
/* 167 */     ImageProcessor ipout = ip.createProcessor(width, height);
/*     */ 
/* 169 */     ImageProcessor ip2 = ip.crop();
/* 170 */     int w2 = ip2.getWidth();
/* 171 */     int h2 = ip2.getHeight();
/*     */ 
/* 174 */     int i1 = (int)Math.ceil(x / w2);
/* 175 */     int i2 = (int)Math.ceil((width - x) / w2);
/* 176 */     int j1 = (int)Math.ceil(y / h2);
/* 177 */     int j2 = (int)Math.ceil((height - y) / h2);
/*     */ 
/* 180 */     if (i1 % 2 > 0.5D)
/* 181 */       ip2.flipHorizontal();
/* 182 */     if (j1 % 2 > 0.5D) {
/* 183 */       ip2.flipVertical();
/*     */     }
/* 185 */     for (int i = -i1; i < i2; i += 2) {
/* 186 */       for (int j = -j1; j < j2; j += 2) {
/* 187 */         ipout.insert(ip2, x - i * w2, y - j * h2);
/*     */       }
/*     */     }
/*     */ 
/* 191 */     ip2.flipHorizontal();
/* 192 */     for (int i = -i1 + 1; i < i2; i += 2) {
/* 193 */       for (int j = -j1; j < j2; j += 2) {
/* 194 */         ipout.insert(ip2, x - i * w2, y - j * h2);
/*     */       }
/*     */     }
/*     */ 
/* 198 */     ip2.flipVertical();
/* 199 */     for (int i = -i1 + 1; i < i2; i += 2) {
/* 200 */       for (int j = -j1 + 1; j < j2; j += 2) {
/* 201 */         ipout.insert(ip2, x - i * w2, y - j * h2);
/*     */       }
/*     */     }
/*     */ 
/* 205 */     ip2.flipHorizontal();
/* 206 */     for (int i = -i1; i < i2; i += 2) {
/* 207 */       for (int j = -j1 + 1; j < j2; j += 2) {
/* 208 */         ipout.insert(ip2, x - i * w2, y - j * h2);
/*     */       }
/*     */     }
/*     */ 
/* 212 */     return ipout;
/*     */   }
/*     */ 
/*     */   void filterLargeSmall(ImageProcessor ip, double filterLarge, double filterSmall, int stripesHorVert, double scaleStripes)
/*     */   {
/* 227 */     int maxN = ip.getWidth();
/*     */ 
/* 229 */     float[] fht = (float[])ip.getPixels();
/* 230 */     float[] filter = new float[maxN * maxN];
/* 231 */     for (int i = 0; i < maxN * maxN; i++) {
/* 232 */       filter[i] = 1.0F;
/*     */     }
/*     */ 
/* 249 */     double scaleLarge = filterLarge * filterLarge;
/* 250 */     double scaleSmall = filterSmall * filterSmall;
/* 251 */     scaleStripes *= scaleStripes;
/*     */ 
/* 255 */     for (int j = 1; j < maxN / 2; j++) {
/* 256 */       int row = j * maxN;
/* 257 */       int backrow = (maxN - j) * maxN;
/* 258 */       float rowFactLarge = (float)Math.exp(-(j * j) * scaleLarge);
/* 259 */       float rowFactSmall = (float)Math.exp(-(j * j) * scaleSmall);
/*     */ 
/* 263 */       for (int col = 1; col < maxN / 2; col++) {
/* 264 */         int backcol = maxN - col;
/* 265 */         float colFactLarge = (float)Math.exp(-(col * col) * scaleLarge);
/* 266 */         float colFactSmall = (float)Math.exp(-(col * col) * scaleSmall);
/* 267 */         float factor = (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall;
/* 268 */         switch (stripesHorVert) { case 1:
/* 269 */           factor *= (1.0F - (float)Math.exp(-(col * col) * scaleStripes)); break;
/*     */         case 2:
/* 270 */           factor *= (1.0F - (float)Math.exp(-(j * j) * scaleStripes));
/*     */         }
/*     */ 
/* 273 */         fht[(col + row)] *= factor;
/* 274 */         fht[(col + backrow)] *= factor;
/* 275 */         fht[(backcol + row)] *= factor;
/* 276 */         fht[(backcol + backrow)] *= factor;
/* 277 */         filter[(col + row)] *= factor;
/* 278 */         filter[(col + backrow)] *= factor;
/* 279 */         filter[(backcol + row)] *= factor;
/* 280 */         filter[(backcol + backrow)] *= factor;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 285 */     int rowmid = maxN * (maxN / 2);
/* 286 */     float rowFactLarge = (float)Math.exp(-(maxN / 2) * (maxN / 2) * scaleLarge);
/* 287 */     float rowFactSmall = (float)Math.exp(-(maxN / 2) * (maxN / 2) * scaleSmall);
/* 288 */     float factStripes = (float)Math.exp(-(maxN / 2) * (maxN / 2) * scaleStripes);
/*     */ 
/* 290 */     fht[(maxN / 2)] *= (1.0F - rowFactLarge) * rowFactSmall;
/* 291 */     fht[rowmid] *= (1.0F - rowFactLarge) * rowFactSmall;
/* 292 */     fht[(maxN / 2 + rowmid)] *= (1.0F - rowFactLarge * rowFactLarge) * rowFactSmall * rowFactSmall;
/* 293 */     filter[(maxN / 2)] *= (1.0F - rowFactLarge) * rowFactSmall;
/* 294 */     filter[rowmid] *= (1.0F - rowFactLarge) * rowFactSmall;
/* 295 */     filter[(maxN / 2 + rowmid)] *= (1.0F - rowFactLarge * rowFactLarge) * rowFactSmall * rowFactSmall;
/*     */ 
/* 297 */     switch (stripesHorVert) { case 1:
/* 298 */       fht[(maxN / 2)] *= (1.0F - factStripes);
/* 299 */       fht[rowmid] = 0.0F;
/* 300 */       fht[(maxN / 2 + rowmid)] *= (1.0F - factStripes);
/* 301 */       filter[(maxN / 2)] *= (1.0F - factStripes);
/* 302 */       filter[rowmid] = 0.0F;
/* 303 */       filter[(maxN / 2 + rowmid)] *= (1.0F - factStripes);
/* 304 */       break;
/*     */     case 2:
/* 305 */       fht[(maxN / 2)] = 0.0F;
/* 306 */       fht[rowmid] *= (1.0F - factStripes);
/* 307 */       fht[(maxN / 2 + rowmid)] *= (1.0F - factStripes);
/* 308 */       filter[(maxN / 2)] = 0.0F;
/* 309 */       filter[rowmid] *= (1.0F - factStripes);
/* 310 */       filter[(maxN / 2 + rowmid)] *= (1.0F - factStripes);
/*     */     }
/*     */ 
/* 315 */     rowFactLarge = (float)Math.exp(-(maxN / 2) * (maxN / 2) * scaleLarge);
/* 316 */     rowFactSmall = (float)Math.exp(-(maxN / 2) * (maxN / 2) * scaleSmall);
/* 317 */     for (int col = 1; col < maxN / 2; col++) {
/* 318 */       int backcol = maxN - col;
/* 319 */       float colFactLarge = (float)Math.exp(-(col * col) * scaleLarge);
/* 320 */       float colFactSmall = (float)Math.exp(-(col * col) * scaleSmall);
/*     */ 
/* 322 */       switch (stripesHorVert) {
/*     */       case 0:
/* 324 */         fht[col] *= (1.0F - colFactLarge) * colFactSmall;
/* 325 */         fht[backcol] *= (1.0F - colFactLarge) * colFactSmall;
/* 326 */         fht[(col + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall;
/* 327 */         fht[(backcol + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall;
/* 328 */         filter[col] *= (1.0F - colFactLarge) * colFactSmall;
/* 329 */         filter[backcol] *= (1.0F - colFactLarge) * colFactSmall;
/* 330 */         filter[(col + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall;
/* 331 */         filter[(backcol + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall;
/* 332 */         break;
/*     */       case 1:
/* 334 */         factStripes = (float)Math.exp(-(col * col) * scaleStripes);
/* 335 */         fht[col] *= (1.0F - colFactLarge) * colFactSmall * (1.0F - factStripes);
/* 336 */         fht[backcol] *= (1.0F - colFactLarge) * colFactSmall * (1.0F - factStripes);
/* 337 */         fht[(col + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall * (1.0F - factStripes);
/* 338 */         fht[(backcol + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall * (1.0F - factStripes);
/* 339 */         filter[col] *= (1.0F - colFactLarge) * colFactSmall * (1.0F - factStripes);
/* 340 */         filter[backcol] *= (1.0F - colFactLarge) * colFactSmall * (1.0F - factStripes);
/* 341 */         filter[(col + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall * (1.0F - factStripes);
/* 342 */         filter[(backcol + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall * (1.0F - factStripes);
/* 343 */         break;
/*     */       case 2:
/* 345 */         factStripes = (float)Math.exp(-(maxN / 2) * (maxN / 2) * scaleStripes);
/* 346 */         fht[col] = 0.0F;
/* 347 */         fht[backcol] = 0.0F;
/* 348 */         fht[(col + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall * (1.0F - factStripes);
/* 349 */         fht[(backcol + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall * (1.0F - factStripes);
/* 350 */         filter[col] = 0.0F;
/* 351 */         filter[backcol] = 0.0F;
/* 352 */         filter[(col + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall * (1.0F - factStripes);
/* 353 */         filter[(backcol + rowmid)] *= (1.0F - colFactLarge * rowFactLarge) * colFactSmall * rowFactSmall * (1.0F - factStripes);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 358 */     float colFactLarge = (float)Math.exp(-(maxN / 2) * (maxN / 2) * scaleLarge);
/* 359 */     float colFactSmall = (float)Math.exp(-(maxN / 2) * (maxN / 2) * scaleSmall);
/* 360 */     for (int j = 1; j < maxN / 2; j++) {
/* 361 */       int row = j * maxN;
/* 362 */       int backrow = (maxN - j) * maxN;
/* 363 */       rowFactLarge = (float)Math.exp(-(j * j) * scaleLarge);
/* 364 */       rowFactSmall = (float)Math.exp(-(j * j) * scaleSmall);
/*     */ 
/* 366 */       switch (stripesHorVert) {
/*     */       case 0:
/* 368 */         fht[row] *= (1.0F - rowFactLarge) * rowFactSmall;
/* 369 */         fht[backrow] *= (1.0F - rowFactLarge) * rowFactSmall;
/* 370 */         fht[(row + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall;
/* 371 */         fht[(backrow + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall;
/* 372 */         filter[row] *= (1.0F - rowFactLarge) * rowFactSmall;
/* 373 */         filter[backrow] *= (1.0F - rowFactLarge) * rowFactSmall;
/* 374 */         filter[(row + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall;
/* 375 */         filter[(backrow + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall;
/* 376 */         break;
/*     */       case 1:
/* 378 */         factStripes = (float)Math.exp(-(maxN / 2) * (maxN / 2) * scaleStripes);
/* 379 */         fht[row] = 0.0F;
/* 380 */         fht[backrow] = 0.0F;
/* 381 */         fht[(row + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall * (1.0F - factStripes);
/* 382 */         fht[(backrow + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall * (1.0F - factStripes);
/* 383 */         filter[row] = 0.0F;
/* 384 */         filter[backrow] = 0.0F;
/* 385 */         filter[(row + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall * (1.0F - factStripes);
/* 386 */         filter[(backrow + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall * (1.0F - factStripes);
/* 387 */         break;
/*     */       case 2:
/* 389 */         factStripes = (float)Math.exp(-(j * j) * scaleStripes);
/* 390 */         fht[row] *= (1.0F - rowFactLarge) * rowFactSmall * (1.0F - factStripes);
/* 391 */         fht[backrow] *= (1.0F - rowFactLarge) * rowFactSmall * (1.0F - factStripes);
/* 392 */         fht[(row + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall * (1.0F - factStripes);
/* 393 */         fht[(backrow + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall * (1.0F - factStripes);
/* 394 */         filter[row] *= (1.0F - rowFactLarge) * rowFactSmall * (1.0F - factStripes);
/* 395 */         filter[backrow] *= (1.0F - rowFactLarge) * rowFactSmall * (1.0F - factStripes);
/* 396 */         filter[(row + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall * (1.0F - factStripes);
/* 397 */         filter[(backrow + maxN / 2)] *= (1.0F - rowFactLarge * colFactLarge) * rowFactSmall * colFactSmall * (1.0F - factStripes);
/*     */       }
/*     */     }
/* 400 */     if ((displayFilter) && (this.slice == 1)) {
/* 401 */       FHT f = new FHT(new FloatProcessor(maxN, maxN, filter, null));
/* 402 */       f.swapQuadrants();
/* 403 */       new ImagePlus("Filter", f).show();
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean showBandpassDialog(ImagePlus imp) {
/* 408 */     GenericDialog gd = new GenericDialog("FFT Bandpass Filter");
/* 409 */     gd.addNumericField("Filter_large structures down to", filterLargeDia, 0, 4, "pixels");
/* 410 */     gd.addNumericField("Filter_small structures up to", filterSmallDia, 0, 4, "pixels");
/* 411 */     gd.addChoice("Suppress stripes:", choices, choiceDia);
/* 412 */     gd.addNumericField("Tolerance of direction:", toleranceDia, 0, 2, "%");
/* 413 */     gd.addCheckbox("Autoscale after filtering", doScalingDia);
/* 414 */     gd.addCheckbox("Saturate image when autoscaling", saturateDia);
/* 415 */     gd.addCheckbox("Display filter", displayFilter);
/* 416 */     if (this.stackSize > 1)
/* 417 */       gd.addCheckbox("Process entire stack", processStack);
/* 418 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#fft-bandpass");
/* 419 */     gd.showDialog();
/* 420 */     if (gd.wasCanceled())
/* 421 */       return false;
/* 422 */     if (gd.invalidNumber()) {
/* 423 */       IJ.error("Error", "Invalid input number");
/* 424 */       return false;
/*     */     }
/* 426 */     filterLargeDia = gd.getNextNumber();
/* 427 */     filterSmallDia = gd.getNextNumber();
/* 428 */     choiceIndex = gd.getNextChoiceIndex();
/* 429 */     choiceDia = choices[choiceIndex];
/* 430 */     toleranceDia = gd.getNextNumber();
/* 431 */     doScalingDia = gd.getNextBoolean();
/* 432 */     saturateDia = gd.getNextBoolean();
/* 433 */     displayFilter = gd.getNextBoolean();
/* 434 */     if (this.stackSize > 1)
/* 435 */       processStack = gd.getNextBoolean();
/* 436 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.FFTFilter
 * JD-Core Version:    0.6.2
 */