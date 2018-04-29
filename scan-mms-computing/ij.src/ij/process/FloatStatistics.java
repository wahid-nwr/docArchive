/*     */ package ij.process;
/*     */ 
/*     */ import ij.measure.Calibration;
/*     */ 
/*     */ public class FloatStatistics extends ImageStatistics
/*     */ {
/*     */   public FloatStatistics(ImageProcessor ip)
/*     */   {
/*  11 */     this(ip, 27, null);
/*     */   }
/*     */ 
/*     */   public FloatStatistics(ImageProcessor ip, int mOptions, Calibration cal)
/*     */   {
/*  18 */     this.width = ip.getWidth();
/*  19 */     this.height = ip.getHeight();
/*  20 */     setup(ip, cal);
/*  21 */     double minT = ip.getMinThreshold();
/*     */     double maxThreshold;
/*     */     double minThreshold;
/*     */     double maxThreshold;
/*  23 */     if (((mOptions & 0x100) == 0) || (minT == -808080.0D)) {
/*  24 */       double minThreshold = -3.402823466385289E+38D; maxThreshold = 3.402823466385289E+38D;
/*     */     } else {
/*  26 */       minThreshold = minT; maxThreshold = ip.getMaxThreshold();
/*  27 */     }getStatistics(ip, minThreshold, maxThreshold);
/*  28 */     if ((mOptions & 0x8) != 0)
/*  29 */       getMode();
/*  30 */     if (((mOptions & 0x800) != 0) || ((mOptions & 0x2000) != 0))
/*  31 */       fitEllipse(ip, mOptions);
/*  32 */     else if ((mOptions & 0x20) != 0)
/*  33 */       getCentroid(ip, minThreshold, maxThreshold);
/*  34 */     if ((mOptions & 0x60040) != 0)
/*  35 */       calculateMoments(ip, minThreshold, maxThreshold);
/*  36 */     if ((mOptions & 0x10000) != 0) {
/*  37 */       if ((Double.isInfinite(this.binSize)) || (Double.isNaN(this.binSize))) {
/*  38 */         this.median = 0.0D;
/*     */       } else {
/*  40 */         calculateMedian(this.histogram, 0, this.histogram.length - 1, null);
/*  41 */         this.median = (this.histMin + this.median * this.binSize);
/*  42 */         if (this.binSize != 1.0D) this.median += this.binSize / 2.0D;
/*     */       }
/*     */     }
/*  45 */     if ((mOptions & 0x80000) != 0)
/*  46 */       calculateAreaFraction(ip);
/*     */   }
/*     */ 
/*     */   void getStatistics(ImageProcessor ip, double minThreshold, double maxThreshold)
/*     */   {
/*  51 */     float[] pixels = (float[])ip.getPixels();
/*  52 */     this.nBins = ip.getHistogramSize();
/*  53 */     this.histMin = ip.getHistogramMin();
/*  54 */     this.histMax = ip.getHistogramMax();
/*  55 */     this.histogram = new int[this.nBins];
/*  56 */     double sum = 0.0D;
/*  57 */     double sum2 = 0.0D;
/*  58 */     byte[] mask = ip.getMaskArray();
/*     */ 
/*  61 */     double roiMin = 1.7976931348623157E+308D;
/*  62 */     double roiMax = -1.797693134862316E+308D;
/*  63 */     double roiMin2 = 1.7976931348623157E+308D;
/*  64 */     double roiMax2 = -1.797693134862316E+308D;
/*  65 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/*  66 */       int i = y * this.width + this.rx;
/*  67 */       int mi = my * this.rw;
/*  68 */       for (int x = this.rx; x < this.rx + this.rw; x++) {
/*  69 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/*  70 */           double v = pixels[i];
/*  71 */           if ((v >= minThreshold) && (v <= maxThreshold)) {
/*  72 */             if (v < roiMin) roiMin = v;
/*  73 */             if (v > roiMax) roiMax = v;
/*     */           }
/*     */         }
/*  76 */         i++;
/*     */       }
/*  65 */       y++;
/*     */     }
/*     */ 
/*  79 */     this.min = roiMin; this.max = roiMax;
/*  80 */     if ((this.histMin == 0.0D) && (this.histMax == 0.0D)) {
/*  81 */       this.histMin = this.min;
/*  82 */       this.histMax = this.max;
/*     */     } else {
/*  84 */       if (this.min < this.histMin) this.min = this.histMin;
/*  85 */       if (this.max > this.histMax) this.max = this.histMax;
/*     */     }
/*  87 */     this.binSize = ((this.histMax - this.histMin) / this.nBins);
/*     */ 
/*  90 */     double scale = this.nBins / (this.histMax - this.histMin);
/*     */ 
/*  92 */     this.pixelCount = 0;
/*  93 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/*  94 */       int i = y * this.width + this.rx;
/*  95 */       int mi = my * this.rw;
/*  96 */       for (int x = this.rx; x < this.rx + this.rw; x++) {
/*  97 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/*  98 */           double v = pixels[i];
/*  99 */           if ((v >= minThreshold) && (v <= maxThreshold) && (v >= this.histMin) && (v <= this.histMax)) {
/* 100 */             this.pixelCount += 1;
/* 101 */             sum += v;
/* 102 */             sum2 += v * v;
/* 103 */             int index = (int)(scale * (v - this.histMin));
/* 104 */             if (index >= this.nBins)
/* 105 */               index = this.nBins - 1;
/* 106 */             this.histogram[index] += 1;
/*     */           }
/*     */         }
/* 109 */         i++;
/*     */       }
/*  93 */       y++;
/*     */     }
/*     */ 
/* 112 */     this.area = (this.pixelCount * this.pw * this.ph);
/* 113 */     this.mean = (sum / this.pixelCount);
/* 114 */     this.umean = this.mean;
/* 115 */     calculateStdDev(this.pixelCount, sum, sum2);
/*     */   }
/*     */ 
/*     */   void getMode()
/*     */   {
/* 120 */     this.maxCount = 0;
/* 121 */     for (int i = 0; i < this.nBins; i++) {
/* 122 */       int count = this.histogram[i];
/* 123 */       if (count > this.maxCount) {
/* 124 */         this.maxCount = count;
/* 125 */         this.mode = i;
/*     */       }
/*     */     }
/* 128 */     this.dmode = (this.histMin + this.mode * this.binSize);
/* 129 */     if (this.binSize != 1.0D)
/* 130 */       this.dmode += this.binSize / 2.0D;
/*     */   }
/*     */ 
/*     */   void calculateMoments(ImageProcessor ip, double minThreshold, double maxThreshold) {
/* 134 */     float[] pixels = (float[])ip.getPixels();
/* 135 */     byte[] mask = ip.getMaskArray();
/*     */ 
/* 137 */     double sum1 = 0.0D; double sum2 = 0.0D; double sum3 = 0.0D; double sum4 = 0.0D; double xsum = 0.0D; double ysum = 0.0D;
/* 138 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/* 139 */       int i = y * this.width + this.rx;
/* 140 */       int mi = my * this.rw;
/* 141 */       for (int x = this.rx; x < this.rx + this.rw; x++) {
/* 142 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/* 143 */           double v = pixels[i] + 4.9E-324D;
/* 144 */           if ((v >= minThreshold) && (v <= maxThreshold)) {
/* 145 */             double v2 = v * v;
/* 146 */             sum1 += v;
/* 147 */             sum2 += v2;
/* 148 */             sum3 += v * v2;
/* 149 */             sum4 += v2 * v2;
/* 150 */             xsum += x * v;
/* 151 */             ysum += y * v;
/*     */           }
/*     */         }
/* 154 */         i++;
/*     */       }
/* 138 */       y++;
/*     */     }
/*     */ 
/* 157 */     double mean2 = this.mean * this.mean;
/* 158 */     double variance = sum2 / this.pixelCount - mean2;
/* 159 */     double sDeviation = Math.sqrt(variance);
/* 160 */     this.skewness = (((sum3 - 3.0D * this.mean * sum2) / this.pixelCount + 2.0D * this.mean * mean2) / (variance * sDeviation));
/* 161 */     this.kurtosis = (((sum4 - 4.0D * this.mean * sum3 + 6.0D * mean2 * sum2) / this.pixelCount - 3.0D * mean2 * mean2) / (variance * variance) - 3.0D);
/* 162 */     this.xCenterOfMass = (xsum / sum1 + 0.5D);
/* 163 */     this.yCenterOfMass = (ysum / sum1 + 0.5D);
/* 164 */     if (this.cal != null) {
/* 165 */       this.xCenterOfMass = this.cal.getX(this.xCenterOfMass);
/* 166 */       this.yCenterOfMass = this.cal.getY(this.yCenterOfMass, this.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   void getCentroid(ImageProcessor ip, double minThreshold, double maxThreshold) {
/* 171 */     float[] pixels = (float[])ip.getPixels();
/* 172 */     byte[] mask = ip.getMaskArray();
/* 173 */     double count = 0.0D; double xsum = 0.0D; double ysum = 0.0D;
/*     */ 
/* 175 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/* 176 */       int i = y * this.width + this.rx;
/* 177 */       int mi = my * this.rw;
/* 178 */       for (int x = this.rx; x < this.rx + this.rw; x++) {
/* 179 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/* 180 */           double v = pixels[i];
/* 181 */           if ((v >= minThreshold) && (v <= maxThreshold)) {
/* 182 */             count += 1.0D;
/* 183 */             xsum += x;
/* 184 */             ysum += y;
/*     */           }
/*     */         }
/* 187 */         i++;
/*     */       }
/* 175 */       y++;
/*     */     }
/*     */ 
/* 190 */     this.xCentroid = (xsum / count + 0.5D);
/* 191 */     this.yCentroid = (ysum / count + 0.5D);
/* 192 */     if (this.cal != null) {
/* 193 */       this.xCentroid = this.cal.getX(this.xCentroid);
/* 194 */       this.yCentroid = this.cal.getY(this.yCentroid, this.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   void calculateAreaFraction(ImageProcessor ip) {
/* 199 */     int sum = 0;
/* 200 */     int total = 0;
/* 201 */     float t1 = (float)ip.getMinThreshold();
/* 202 */     float t2 = (float)ip.getMaxThreshold();
/*     */ 
/* 204 */     float[] pixels = (float[])ip.getPixels();
/* 205 */     boolean noThresh = t1 == -808080.0D;
/* 206 */     byte[] mask = ip.getMaskArray();
/*     */ 
/* 208 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/* 209 */       int i = y * this.width + this.rx;
/* 210 */       int mi = my * this.rw;
/* 211 */       for (int x = this.rx; x < this.rx + this.rw; x++) {
/* 212 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/* 213 */           float v = pixels[i];
/* 214 */           total++;
/* 215 */           if (noThresh) {
/* 216 */             if (v != 0.0F) sum++; 
/*     */           }
/* 217 */           else if ((v >= t1) && (v <= t2))
/* 218 */             sum++;
/*     */         }
/* 220 */         i++;
/*     */       }
/* 208 */       y++;
/*     */     }
/*     */ 
/* 223 */     this.areaFraction = (sum * 100.0D / total);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.FloatStatistics
 * JD-Core Version:    0.6.2
 */