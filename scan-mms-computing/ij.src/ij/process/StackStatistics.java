/*     */ package ij.process;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class StackStatistics extends ImageStatistics
/*     */ {
/*     */   public StackStatistics(ImagePlus imp)
/*     */   {
/*  12 */     this(imp, 256, 0.0D, 0.0D);
/*     */   }
/*     */ 
/*     */   public StackStatistics(ImagePlus imp, int nBins, double histMin, double histMax) {
/*  16 */     int bits = imp.getBitDepth();
/*  17 */     if (((bits == 8) || (bits == 24)) && (nBins == 256) && (histMin == 0.0D) && (histMax == 256.0D))
/*  18 */       sum8BitHistograms(imp);
/*  19 */     else if ((bits == 16) && (nBins == 256) && (histMin == 0.0D) && (histMax == 0.0D) && (!imp.getCalibration().calibrated()))
/*  20 */       sum16BitHistograms(imp);
/*     */     else
/*  22 */       doCalculations(imp, nBins, histMin, histMax);
/*     */   }
/*     */ 
/*     */   void doCalculations(ImagePlus imp, int bins, double histogramMin, double histogramMax) {
/*  26 */     ImageProcessor ip = imp.getProcessor();
/*  27 */     boolean limitToThreshold = (Analyzer.getMeasurements() & 0x100) != 0;
/*  28 */     double minThreshold = -3.402823466385289E+38D;
/*  29 */     double maxThreshold = 3.402823466385289E+38D;
/*  30 */     Calibration cal = imp.getCalibration();
/*  31 */     if ((limitToThreshold) && (ip.getMinThreshold() != -808080.0D)) {
/*  32 */       minThreshold = cal.getCValue(ip.getMinThreshold());
/*  33 */       maxThreshold = cal.getCValue(ip.getMaxThreshold());
/*     */     }
/*  35 */     this.nBins = bins;
/*  36 */     this.histMin = histogramMin;
/*  37 */     this.histMax = histogramMax;
/*  38 */     ImageStack stack = imp.getStack();
/*  39 */     int size = stack.getSize();
/*  40 */     ip.setRoi(imp.getRoi());
/*  41 */     byte[] mask = ip.getMaskArray();
/*  42 */     float[] cTable = imp.getCalibration().getCTable();
/*  43 */     this.histogram = new int[this.nBins];
/*     */ 
/*  45 */     double sum = 0.0D;
/*  46 */     double sum2 = 0.0D;
/*     */ 
/*  51 */     int width = ip.getWidth();
/*  52 */     int height = ip.getHeight();
/*  53 */     Rectangle roi = ip.getRoi();
/*     */     int rh;
/*     */     int rx;
/*     */     int ry;
/*     */     int rw;
/*     */     int rh;
/*  54 */     if (roi != null) {
/*  55 */       int rx = roi.x;
/*  56 */       int ry = roi.y;
/*  57 */       int rw = roi.width;
/*  58 */       rh = roi.height;
/*     */     } else {
/*  60 */       rx = 0;
/*  61 */       ry = 0;
/*  62 */       rw = width;
/*  63 */       rh = height;
/*     */     }
/*     */ 
/*  66 */     double pw = 1.0D;
/*  67 */     double ph = 1.0D;
/*  68 */     this.roiX = (rx * pw);
/*  69 */     this.roiY = (ry * ph);
/*  70 */     this.roiWidth = (rw * pw);
/*  71 */     this.roiHeight = (rh * ph);
/*  72 */     boolean fixedRange = (this.histMin != 0.0D) || (this.histMax != 0.0D);
/*     */ 
/*  75 */     double roiMin = 1.7976931348623157E+308D;
/*  76 */     double roiMax = -1.797693134862316E+308D;
/*  77 */     for (int slice = 1; slice <= size; slice++) {
/*  78 */       IJ.showStatus("Calculating stack histogram...");
/*  79 */       IJ.showProgress(slice / 2, size);
/*  80 */       ip = stack.getProcessor(slice);
/*     */ 
/*  82 */       int y = ry; for (int my = 0; y < ry + rh; my++) {
/*  83 */         int i = y * width + rx;
/*  84 */         int mi = my * rw;
/*  85 */         for (int x = rx; x < rx + rw; x++) {
/*  86 */           if ((mask == null) || (mask[(mi++)] != 0)) {
/*  87 */             double v = ip.getPixelValue(x, y);
/*  88 */             if ((v >= minThreshold) && (v <= maxThreshold)) {
/*  89 */               if (v < roiMin) roiMin = v;
/*  90 */               if (v > roiMax) roiMax = v;
/*     */             }
/*     */           }
/*  93 */           i++;
/*     */         }
/*  82 */         y++;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  97 */     this.min = roiMin;
/*  98 */     this.max = roiMax;
/*  99 */     if (fixedRange) {
/* 100 */       if (this.min < this.histMin) this.min = this.histMin;
/* 101 */       if (this.max > this.histMax) this.max = this.histMax; 
/*     */     }
/* 103 */     else { this.histMin = this.min;
/* 104 */       this.histMax = this.max;
/*     */     }
/*     */ 
/* 108 */     double scale = this.nBins / (this.histMax - this.histMin);
/* 109 */     this.pixelCount = 0;
/*     */ 
/* 111 */     boolean first = true;
/* 112 */     for (int slice = 1; slice <= size; slice++) {
/* 113 */       IJ.showProgress(size / 2 + slice / 2, size);
/* 114 */       ip = stack.getProcessor(slice);
/* 115 */       ip.setCalibrationTable(cTable);
/* 116 */       int y = ry; for (int my = 0; y < ry + rh; my++) {
/* 117 */         int i = y * width + rx;
/* 118 */         int mi = my * rw;
/* 119 */         for (int x = rx; x < rx + rw; x++) {
/* 120 */           if ((mask == null) || (mask[(mi++)] != 0)) {
/* 121 */             double v = ip.getPixelValue(x, y);
/* 122 */             if ((v >= minThreshold) && (v <= maxThreshold) && (v >= this.histMin) && (v <= this.histMax)) {
/* 123 */               this.longPixelCount += 1L;
/* 124 */               sum += v;
/* 125 */               sum2 += v * v;
/* 126 */               int index = (int)(scale * (v - this.histMin));
/* 127 */               if (index >= this.nBins)
/* 128 */                 index = this.nBins - 1;
/* 129 */               this.histogram[index] += 1;
/*     */             }
/*     */           }
/* 132 */           i++;
/*     */         }
/* 116 */         y++;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 136 */     this.pixelCount = ((int)this.longPixelCount);
/* 137 */     this.area = (this.longPixelCount * pw * ph);
/* 138 */     this.mean = (sum / this.longPixelCount);
/* 139 */     calculateStdDev(this.longPixelCount, sum, sum2);
/* 140 */     this.histMin = cal.getRawValue(this.histMin);
/* 141 */     this.histMax = cal.getRawValue(this.histMax);
/* 142 */     this.binSize = ((this.histMax - this.histMin) / this.nBins);
/* 143 */     int bits = imp.getBitDepth();
/* 144 */     if ((this.histMin == 0.0D) && (this.histMax == 256.0D) && ((bits == 8) || (bits == 24)))
/* 145 */       this.histMax = 255.0D;
/* 146 */     this.dmode = getMode(cal);
/* 147 */     IJ.showStatus("");
/* 148 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   void sum8BitHistograms(ImagePlus imp) {
/* 152 */     Calibration cal = imp.getCalibration();
/* 153 */     boolean limitToThreshold = (Analyzer.getMeasurements() & 0x100) != 0;
/* 154 */     int minThreshold = 0;
/* 155 */     int maxThreshold = 255;
/* 156 */     ImageProcessor ip = imp.getProcessor();
/* 157 */     if ((limitToThreshold) && (ip.getMinThreshold() != -808080.0D)) {
/* 158 */       minThreshold = (int)ip.getMinThreshold();
/* 159 */       maxThreshold = (int)ip.getMaxThreshold();
/*     */     }
/* 161 */     ImageStack stack = imp.getStack();
/* 162 */     Roi roi = imp.getRoi();
/* 163 */     long[] longHistogram = new long[256];
/* 164 */     int n = stack.getSize();
/* 165 */     for (int slice = 1; slice <= n; slice++) {
/* 166 */       IJ.showProgress(slice, n);
/* 167 */       ip = stack.getProcessor(slice);
/* 168 */       if (roi != null) ip.setRoi(roi);
/* 169 */       int[] hist = ip.getHistogram();
/* 170 */       for (int i = 0; i < 256; i++)
/* 171 */         longHistogram[i] += hist[i];
/*     */     }
/* 173 */     this.pw = 1.0D; this.ph = 1.0D;
/* 174 */     getRawStatistics(longHistogram, minThreshold, maxThreshold);
/* 175 */     getRawMinAndMax(longHistogram, minThreshold, maxThreshold);
/* 176 */     this.histogram = new int[256];
/* 177 */     for (int i = 0; i < 256; i++) {
/* 178 */       long count = longHistogram[i];
/* 179 */       if (count <= 2147483647L)
/* 180 */         this.histogram[i] = ((int)count);
/*     */       else
/* 182 */         this.histogram[i] = 2147483647;
/*     */     }
/* 184 */     IJ.showStatus("");
/* 185 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   void getRawStatistics(long[] histogram, int minThreshold, int maxThreshold)
/*     */   {
/* 190 */     long longMaxCount = 0L;
/*     */ 
/* 192 */     double sum = 0.0D;
/* 193 */     double sum2 = 0.0D;
/*     */ 
/* 195 */     for (int i = minThreshold; i <= maxThreshold; i++) {
/* 196 */       long count = histogram[i];
/* 197 */       this.longPixelCount += count;
/* 198 */       sum += i * count;
/* 199 */       double value = i;
/* 200 */       sum2 += value * value * count;
/* 201 */       if (count > longMaxCount) {
/* 202 */         longMaxCount = count;
/* 203 */         this.mode = i;
/*     */       }
/*     */     }
/* 206 */     this.maxCount = ((int)longMaxCount);
/* 207 */     this.pixelCount = ((int)this.longPixelCount);
/* 208 */     this.area = (this.longPixelCount * this.pw * this.ph);
/* 209 */     this.mean = (sum / this.longPixelCount);
/* 210 */     this.umean = this.mean;
/* 211 */     this.dmode = this.mode;
/* 212 */     calculateStdDev(this.longPixelCount, sum, sum2);
/* 213 */     this.histMin = 0.0D;
/* 214 */     this.histMax = 255.0D;
/*     */   }
/*     */ 
/*     */   void getRawMinAndMax(long[] histogram, int minThreshold, int maxThreshold) {
/* 218 */     int min = minThreshold;
/* 219 */     while ((histogram[min] == 0L) && (min < 255))
/* 220 */       min++;
/* 221 */     this.min = min;
/* 222 */     int max = maxThreshold;
/* 223 */     while ((histogram[max] == 0L) && (max > 0))
/* 224 */       max--;
/* 225 */     this.max = max;
/*     */   }
/*     */ 
/*     */   void sum16BitHistograms(ImagePlus imp) {
/* 229 */     Calibration cal = imp.getCalibration();
/* 230 */     boolean limitToThreshold = (Analyzer.getMeasurements() & 0x100) != 0;
/* 231 */     int minThreshold = 0;
/* 232 */     int maxThreshold = 65535;
/* 233 */     ImageProcessor ip = imp.getProcessor();
/* 234 */     if ((limitToThreshold) && (ip.getMinThreshold() != -808080.0D)) {
/* 235 */       minThreshold = (int)ip.getMinThreshold();
/* 236 */       maxThreshold = (int)ip.getMaxThreshold();
/*     */     }
/* 238 */     ImageStack stack = imp.getStack();
/* 239 */     Roi roi = imp.getRoi();
/* 240 */     int[] hist16 = new int[65536];
/* 241 */     int n = stack.getSize();
/* 242 */     for (int slice = 1; slice <= n; slice++) {
/* 243 */       IJ.showProgress(slice, n);
/* 244 */       IJ.showStatus(slice + "/" + n);
/* 245 */       ip = stack.getProcessor(slice);
/* 246 */       if (roi != null) ip.setRoi(roi);
/* 247 */       int[] hist = ip.getHistogram();
/* 248 */       for (int i = 0; i < 65536; i++)
/* 249 */         hist16[i] += hist[i];
/*     */     }
/* 251 */     this.pw = 1.0D; this.ph = 1.0D;
/* 252 */     getRaw16BitMinAndMax(hist16, minThreshold, maxThreshold);
/* 253 */     get16BitStatistics(hist16, (int)this.min, (int)this.max);
/* 254 */     IJ.showStatus("");
/* 255 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   void getRaw16BitMinAndMax(int[] hist, int minThreshold, int maxThreshold) {
/* 259 */     int min = minThreshold;
/* 260 */     while ((hist[min] == 0) && (min < 65535))
/* 261 */       min++;
/* 262 */     this.min = min;
/* 263 */     int max = maxThreshold;
/* 264 */     while ((hist[max] == 0) && (max > 0))
/* 265 */       max--;
/* 266 */     this.max = max;
/*     */   }
/*     */ 
/*     */   void get16BitStatistics(int[] hist, int min, int max)
/*     */   {
/* 272 */     double sum = 0.0D;
/* 273 */     double sum2 = 0.0D;
/* 274 */     this.nBins = 256;
/* 275 */     this.histMin = min;
/* 276 */     this.histMax = max;
/* 277 */     this.binSize = ((this.histMax - this.histMin) / this.nBins);
/* 278 */     double scale = 1.0D / this.binSize;
/* 279 */     int hMin = (int)this.histMin;
/* 280 */     this.histogram = new int[this.nBins];
/*     */ 
/* 282 */     this.maxCount = 0;
/* 283 */     for (int i = min; i <= max; i++) {
/* 284 */       int count = hist[i];
/* 285 */       this.longPixelCount += count;
/* 286 */       double value = i;
/* 287 */       sum += value * count;
/* 288 */       sum2 += value * value * count;
/* 289 */       int index = (int)(scale * (i - hMin));
/* 290 */       if (index >= this.nBins)
/* 291 */         index = this.nBins - 1;
/* 292 */       this.histogram[index] += count;
/*     */     }
/* 294 */     this.pixelCount = ((int)this.longPixelCount);
/* 295 */     this.area = (this.longPixelCount * this.pw * this.ph);
/* 296 */     this.mean = (sum / this.longPixelCount);
/* 297 */     this.umean = this.mean;
/* 298 */     this.dmode = getMode(null);
/* 299 */     calculateStdDev(this.longPixelCount, sum, sum2);
/*     */   }
/*     */ 
/*     */   double getMode(Calibration cal)
/*     */   {
/* 304 */     this.maxCount = 0;
/* 305 */     for (int i = 0; i < this.nBins; i++) {
/* 306 */       int count = this.histogram[i];
/* 307 */       if (count > this.maxCount) {
/* 308 */         this.maxCount = count;
/* 309 */         this.mode = i;
/*     */       }
/*     */     }
/* 312 */     double tmode = this.histMin + this.mode * this.binSize;
/* 313 */     if (cal != null) tmode = cal.getCValue(tmode);
/* 314 */     return tmode;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.StackStatistics
 * JD-Core Version:    0.6.2
 */