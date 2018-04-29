/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Undo;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Measurements;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.process.ShortProcessor;
/*     */ import ij.process.StackStatistics;
/*     */ 
/*     */ public class ContrastEnhancer
/*     */   implements PlugIn, Measurements
/*     */ {
/*     */   int max;
/*     */   int range;
/*     */   boolean classicEqualization;
/*     */   int stackSize;
/*     */   boolean updateSelectionOnly;
/*     */   boolean equalize;
/*     */   boolean normalize;
/*     */   boolean processStack;
/*     */   boolean useStackHistogram;
/*     */   boolean entireImage;
/*  16 */   static double saturated = 0.35D;
/*     */   static boolean gEqualize;
/*     */   static boolean gNormalize;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  20 */     ImagePlus imp = IJ.getImage();
/*  21 */     this.stackSize = imp.getStackSize();
/*  22 */     imp.trimProcessor();
/*  23 */     if (!showDialog(imp))
/*  24 */       return;
/*  25 */     Roi roi = imp.getRoi();
/*  26 */     if (roi != null) roi.endPaste();
/*  27 */     if (this.stackSize == 1)
/*  28 */       Undo.setup(6, imp);
/*     */     else
/*  30 */       Undo.reset();
/*  31 */     if (this.equalize)
/*  32 */       equalize(imp);
/*     */     else
/*  34 */       stretchHistogram(imp, saturated);
/*  35 */     if ((this.equalize) || (this.normalize))
/*  36 */       imp.getProcessor().resetMinAndMax();
/*  37 */     imp.updateAndDraw();
/*     */   }
/*     */ 
/*     */   boolean showDialog(ImagePlus imp) {
/*  41 */     this.equalize = gEqualize; this.normalize = gNormalize;
/*  42 */     int bitDepth = imp.getBitDepth();
/*  43 */     boolean composite = imp.isComposite();
/*  44 */     if (composite) this.stackSize = 1;
/*  45 */     Roi roi = imp.getRoi();
/*  46 */     boolean areaRoi = (roi != null) && (roi.isArea()) && (!composite);
/*  47 */     GenericDialog gd = new GenericDialog("Enhance Contrast");
/*  48 */     gd.addNumericField("Saturated Pixels:", saturated, 1, 4, "%");
/*  49 */     if ((bitDepth != 24) && (!composite))
/*  50 */       gd.addCheckbox("Normalize", this.normalize);
/*  51 */     if (areaRoi) {
/*  52 */       String label = bitDepth == 24 ? "Update Entire Image" : "Update All When Normalizing";
/*  53 */       gd.addCheckbox(label, this.entireImage);
/*     */     }
/*  55 */     gd.addCheckbox("Equalize Histogram", this.equalize);
/*  56 */     if (this.stackSize > 1) {
/*  57 */       if (!composite)
/*  58 */         gd.addCheckbox("Normalize_All " + this.stackSize + " Slices", this.processStack);
/*  59 */       gd.addCheckbox("Use Stack Histogram", this.useStackHistogram);
/*     */     }
/*  61 */     gd.showDialog();
/*  62 */     if (gd.wasCanceled())
/*  63 */       return false;
/*  64 */     saturated = gd.getNextNumber();
/*  65 */     if ((bitDepth != 24) && (!composite))
/*  66 */       this.normalize = gd.getNextBoolean();
/*     */     else
/*  68 */       this.normalize = false;
/*  69 */     if (areaRoi) {
/*  70 */       this.entireImage = gd.getNextBoolean();
/*  71 */       this.updateSelectionOnly = (!this.entireImage);
/*  72 */       if ((!this.normalize) && (bitDepth != 24))
/*  73 */         this.updateSelectionOnly = false;
/*     */     }
/*  75 */     this.equalize = gd.getNextBoolean();
/*  76 */     this.processStack = (this.stackSize > 1 ? gd.getNextBoolean() : false);
/*  77 */     this.useStackHistogram = (this.stackSize > 1 ? gd.getNextBoolean() : false);
/*  78 */     if (saturated < 0.0D) saturated = 0.0D;
/*  79 */     if (saturated > 100.0D) saturated = 100.0D;
/*  80 */     if (this.processStack)
/*  81 */       this.normalize = true;
/*  82 */     gEqualize = this.equalize; gNormalize = this.normalize;
/*  83 */     return true;
/*     */   }
/*     */ 
/*     */   public void stretchHistogram(ImagePlus imp, double saturated) {
/*  87 */     ImageStatistics stats = null;
/*  88 */     if (this.useStackHistogram)
/*  89 */       stats = new StackStatistics(imp);
/*  90 */     if (this.processStack) {
/*  91 */       ImageStack stack = imp.getStack();
/*  92 */       for (int i = 1; i <= this.stackSize; i++) {
/*  93 */         IJ.showProgress(i, this.stackSize);
/*  94 */         ImageProcessor ip = stack.getProcessor(i);
/*  95 */         ip.setRoi(imp.getRoi());
/*  96 */         if (!this.useStackHistogram)
/*  97 */           stats = ImageStatistics.getStatistics(ip, 16, null);
/*  98 */         stretchHistogram(ip, saturated, stats);
/*     */       }
/*     */     } else {
/* 101 */       ImageProcessor ip = imp.getProcessor();
/* 102 */       ip.setRoi(imp.getRoi());
/* 103 */       if (stats == null)
/* 104 */         stats = ImageStatistics.getStatistics(ip, 16, null);
/* 105 */       if (imp.isComposite())
/* 106 */         stretchCompositeImageHistogram((CompositeImage)imp, saturated, stats);
/*     */       else
/* 108 */         stretchHistogram(ip, saturated, stats);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stretchHistogram(ImageProcessor ip, double saturated) {
/* 113 */     this.useStackHistogram = false;
/* 114 */     stretchHistogram(new ImagePlus("", ip), saturated);
/*     */   }
/*     */ 
/*     */   public void stretchHistogram(ImageProcessor ip, double saturated, ImageStatistics stats) {
/* 118 */     int[] a = getMinAndMax(ip, saturated, stats);
/* 119 */     int hmin = a[0]; int hmax = a[1];
/*     */ 
/* 121 */     if (hmax > hmin) {
/* 122 */       double min = stats.histMin + hmin * stats.binSize;
/* 123 */       double max = stats.histMin + hmax * stats.binSize;
/* 124 */       if ((stats.histogram16 != null) && ((ip instanceof ShortProcessor))) {
/* 125 */         min = hmin;
/* 126 */         max = hmax;
/*     */       }
/* 128 */       if (!this.updateSelectionOnly)
/* 129 */         ip.resetRoi();
/* 130 */       if (this.normalize) {
/* 131 */         normalize(ip, min, max);
/*     */       }
/* 133 */       else if (this.updateSelectionOnly) {
/* 134 */         ImageProcessor mask = ip.getMask();
/* 135 */         if (mask != null) ip.snapshot();
/* 136 */         ip.setMinAndMax(min, max);
/* 137 */         if (mask != null) ip.reset(mask); 
/*     */       }
/* 139 */       else { ip.setMinAndMax(min, max); }
/*     */     }
/*     */   }
/*     */ 
/*     */   void stretchCompositeImageHistogram(CompositeImage imp, double saturated, ImageStatistics stats)
/*     */   {
/* 145 */     ImageProcessor ip = imp.getProcessor();
/* 146 */     int[] a = getMinAndMax(ip, saturated, stats);
/* 147 */     int hmin = a[0]; int hmax = a[1];
/* 148 */     if (hmax > hmin) {
/* 149 */       double min = stats.histMin + hmin * stats.binSize;
/* 150 */       double max = stats.histMin + hmax * stats.binSize;
/* 151 */       if ((stats.histogram16 != null) && (imp.getBitDepth() == 16)) {
/* 152 */         min = hmin;
/* 153 */         max = hmax;
/*     */       }
/* 155 */       imp.setDisplayRange(min, max);
/*     */     }
/*     */   }
/*     */ 
/*     */   int[] getMinAndMax(ImageProcessor ip, double saturated, ImageStatistics stats)
/*     */   {
/* 180 */     int[] histogram = stats.histogram;
/* 181 */     if ((stats.histogram16 != null) && ((ip instanceof ShortProcessor)))
/* 182 */       histogram = stats.histogram16;
/* 183 */     int hsize = histogram.length;
/*     */     int threshold;
/*     */     int threshold;
/* 184 */     if (saturated > 0.0D)
/* 185 */       threshold = (int)(stats.pixelCount * saturated / 200.0D);
/*     */     else
/* 187 */       threshold = 0;
/* 188 */     int i = -1;
/* 189 */     boolean found = false;
/* 190 */     int count = 0;
/* 191 */     int maxindex = hsize - 1;
/*     */     do {
/* 193 */       i++;
/* 194 */       count += histogram[i];
/* 195 */       found = count > threshold;
/* 196 */     }while ((!found) && (i < maxindex));
/* 197 */     int hmin = i;
/*     */ 
/* 199 */     i = hsize;
/* 200 */     count = 0;
/*     */     do {
/* 202 */       i--;
/* 203 */       count += histogram[i];
/* 204 */       found = count > threshold;
/* 205 */     }while ((!found) && (i > 0));
/* 206 */     int hmax = i;
/* 207 */     int[] a = new int[2];
/* 208 */     a[0] = hmin; a[1] = hmax;
/* 209 */     return a;
/*     */   }
/*     */ 
/*     */   void normalize(ImageProcessor ip, double min, double max) {
/* 213 */     int min2 = 0;
/* 214 */     int max2 = 255;
/* 215 */     int range = 256;
/* 216 */     if ((ip instanceof ShortProcessor)) {
/* 217 */       max2 = 65535; range = 65536;
/* 218 */     } else if ((ip instanceof FloatProcessor)) {
/* 219 */       normalizeFloat(ip, min, max);
/*     */     }
/*     */ 
/* 222 */     int[] lut = new int[range];
/* 223 */     for (int i = 0; i < range; i++) {
/* 224 */       if (i <= min)
/* 225 */         lut[i] = 0;
/* 226 */       else if (i >= max)
/* 227 */         lut[i] = max2;
/*     */       else
/* 229 */         lut[i] = ((int)((i - min) / (max - min) * max2));
/*     */     }
/* 231 */     applyTable(ip, lut);
/*     */   }
/*     */ 
/*     */   void applyTable(ImageProcessor ip, int[] lut) {
/* 235 */     if (this.updateSelectionOnly) {
/* 236 */       ImageProcessor mask = ip.getMask();
/* 237 */       if (mask != null) ip.snapshot();
/* 238 */       ip.applyTable(lut);
/* 239 */       if (mask != null) ip.reset(mask); 
/*     */     }
/* 241 */     else { ip.applyTable(lut); }
/*     */   }
/*     */ 
/*     */   void normalizeFloat(ImageProcessor ip, double min, double max) {
/* 245 */     double scale = max > min ? 1.0D / (max - min) : 1.0D;
/* 246 */     int size = ip.getWidth() * ip.getHeight();
/* 247 */     float[] pixels = (float[])ip.getPixels();
/*     */ 
/* 249 */     for (int i = 0; i < size; i++) {
/* 250 */       double v = pixels[i] - min;
/* 251 */       if (v < 0.0D) v = 0.0D;
/* 252 */       v *= scale;
/* 253 */       if (v > 1.0D) v = 1.0D;
/* 254 */       pixels[i] = ((float)v);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void equalize(ImagePlus imp) {
/* 259 */     if (imp.getBitDepth() == 32) {
/* 260 */       IJ.showMessage("Contrast Enhancer", "Equalization of 32-bit images not supported.");
/* 261 */       return;
/*     */     }
/* 263 */     this.classicEqualization = IJ.altKeyDown();
/* 264 */     if (this.processStack)
/*     */     {
/* 267 */       ImageStack stack = imp.getStack();
/* 268 */       for (int i = 1; i <= this.stackSize; i++) {
/* 269 */         IJ.showProgress(i, this.stackSize);
/* 270 */         ImageProcessor ip = stack.getProcessor(i);
/* 271 */         equalize(ip);
/*     */       }
/*     */     } else {
/* 274 */       equalize(imp.getProcessor());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void equalize(ImageProcessor ip)
/*     */   {
/* 289 */     int[] histogram = ip.getHistogram();
/* 290 */     ip.resetRoi();
/* 291 */     if ((ip instanceof ShortProcessor)) {
/* 292 */       this.max = 65535;
/* 293 */       this.range = 65535;
/*     */     } else {
/* 295 */       this.max = 255;
/* 296 */       this.range = 255;
/*     */     }
/*     */ 
/* 301 */     double sum = getWeightedValue(histogram, 0);
/* 302 */     for (int i = 1; i < this.max; i++)
/* 303 */       sum += 2.0D * getWeightedValue(histogram, i);
/* 304 */     sum += getWeightedValue(histogram, this.max);
/*     */ 
/* 306 */     double scale = this.range / sum;
/* 307 */     int[] lut = new int[this.range + 1];
/*     */ 
/* 309 */     lut[0] = 0;
/* 310 */     sum = getWeightedValue(histogram, 0);
/* 311 */     for (int i = 1; i < this.max; i++) {
/* 312 */       double delta = getWeightedValue(histogram, i);
/* 313 */       sum += delta;
/* 314 */       lut[i] = ((int)Math.round(sum * scale));
/* 315 */       sum += delta;
/*     */     }
/* 317 */     lut[this.max] = this.max;
/*     */ 
/* 319 */     applyTable(ip, lut);
/*     */   }
/*     */ 
/*     */   private double getWeightedValue(int[] histogram, int i) {
/* 323 */     int h = histogram[i];
/* 324 */     if ((h < 2) || (this.classicEqualization)) return h;
/* 325 */     return Math.sqrt(h);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ContrastEnhancer
 * JD-Core Version:    0.6.2
 */