/*     */ package ij.process;
/*     */ 
/*     */ import ij.measure.Calibration;
/*     */ 
/*     */ public class ShortStatistics extends ImageStatistics
/*     */ {
/*     */   public ShortStatistics(ImageProcessor ip)
/*     */   {
/*  11 */     this(ip, 27, null);
/*     */   }
/*     */ 
/*     */   public ShortStatistics(ImageProcessor ip, int mOptions, Calibration cal)
/*     */   {
/*  18 */     this.width = ip.getWidth();
/*  19 */     this.height = ip.getHeight();
/*  20 */     setup(ip, cal);
/*  21 */     this.nBins = 256;
/*  22 */     double minT = ip.getMinThreshold();
/*     */     int maxThreshold;
/*     */     int minThreshold;
/*     */     int maxThreshold;
/*  24 */     if (((mOptions & 0x100) == 0) || (minT == -808080.0D)) {
/*  25 */       int minThreshold = 0; maxThreshold = 65535;
/*     */     } else {
/*  27 */       minThreshold = (int)minT; maxThreshold = (int)ip.getMaxThreshold();
/*  28 */     }int[] hist = ip.getHistogram();
/*  29 */     this.histogram16 = hist;
/*  30 */     float[] cTable = cal != null ? cal.getCTable() : null;
/*  31 */     getRawMinAndMax(hist, minThreshold, maxThreshold);
/*  32 */     this.histMin = this.min;
/*  33 */     this.histMax = this.max;
/*  34 */     getStatistics(ip, hist, (int)this.min, (int)this.max, cTable);
/*  35 */     if ((mOptions & 0x8) != 0)
/*  36 */       getMode();
/*  37 */     if (((mOptions & 0x800) != 0) || ((mOptions & 0x2000) != 0))
/*  38 */       fitEllipse(ip, mOptions);
/*  39 */     else if ((mOptions & 0x20) != 0)
/*  40 */       getCentroid(ip, minThreshold, maxThreshold);
/*  41 */     if ((mOptions & 0x60040) != 0)
/*  42 */       calculateMoments(ip, minThreshold, maxThreshold, cTable);
/*  43 */     if (((mOptions & 0x10) != 0) && (cTable != null))
/*  44 */       getCalibratedMinAndMax(hist, (int)this.min, (int)this.max, cTable);
/*  45 */     if ((mOptions & 0x10000) != 0)
/*  46 */       calculateMedian(hist, minThreshold, maxThreshold, cal);
/*  47 */     if ((mOptions & 0x80000) != 0)
/*  48 */       calculateAreaFraction(ip, hist);
/*     */   }
/*     */ 
/*     */   void getRawMinAndMax(int[] hist, int minThreshold, int maxThreshold) {
/*  52 */     int min = minThreshold;
/*  53 */     while ((hist[min] == 0) && (min < 65535))
/*  54 */       min++;
/*  55 */     this.min = min;
/*  56 */     int max = maxThreshold;
/*  57 */     while ((hist[max] == 0) && (max > 0))
/*  58 */       max--;
/*  59 */     this.max = max;
/*     */   }
/*     */ 
/*     */   void getStatistics(ImageProcessor ip, int[] hist, int min, int max, float[] cTable)
/*     */   {
/*  65 */     double sum = 0.0D;
/*  66 */     double sum2 = 0.0D;
/*  67 */     this.nBins = ip.getHistogramSize();
/*  68 */     this.histMin = ip.getHistogramMin();
/*  69 */     this.histMax = ip.getHistogramMax();
/*  70 */     if ((this.histMin == 0.0D) && (this.histMax == 0.0D)) {
/*  71 */       this.histMin = min;
/*  72 */       this.histMax = max;
/*     */     } else {
/*  74 */       if (min < this.histMin) min = (int)this.histMin;
/*  75 */       if (max > this.histMax) max = (int)this.histMax;
/*     */     }
/*  77 */     this.binSize = ((this.histMax - this.histMin) / this.nBins);
/*  78 */     double scale = 1.0D / this.binSize;
/*  79 */     int hMin = (int)this.histMin;
/*  80 */     this.histogram = new int[this.nBins];
/*     */ 
/*  82 */     int maxCount = 0;
/*     */ 
/*  84 */     for (int i = min; i <= max; i++) {
/*  85 */       int count = hist[i];
/*  86 */       if (count > maxCount) {
/*  87 */         maxCount = count;
/*  88 */         this.dmode = i;
/*     */       }
/*  90 */       this.pixelCount += count;
/*  91 */       double value = cTable == null ? i : cTable[i];
/*  92 */       sum += value * count;
/*  93 */       sum2 += value * value * count;
/*  94 */       int index = (int)(scale * (i - hMin));
/*  95 */       if (index >= this.nBins)
/*  96 */         index = this.nBins - 1;
/*  97 */       this.histogram[index] += count;
/*     */     }
/*  99 */     this.area = (this.pixelCount * this.pw * this.ph);
/* 100 */     this.mean = (sum / this.pixelCount);
/* 101 */     this.umean = this.mean;
/* 102 */     calculateStdDev(this.pixelCount, sum, sum2);
/* 103 */     if (cTable != null)
/* 104 */       this.dmode = cTable[((int)this.dmode)];
/*     */   }
/*     */ 
/*     */   void getMode()
/*     */   {
/* 109 */     this.maxCount = 0;
/* 110 */     for (int i = 0; i < this.nBins; i++) {
/* 111 */       int count = this.histogram[i];
/* 112 */       if (count > this.maxCount) {
/* 113 */         this.maxCount = count;
/* 114 */         this.mode = i;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void getCentroid(ImageProcessor ip, int minThreshold, int maxThreshold)
/*     */   {
/* 122 */     short[] pixels = (short[])ip.getPixels();
/* 123 */     byte[] mask = ip.getMaskArray();
/* 124 */     boolean limit = (minThreshold > 0) || (maxThreshold < 65535);
/* 125 */     int count = 0;
/* 126 */     double xsum = 0.0D; double ysum = 0.0D;
/* 127 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/* 128 */       int i = y * this.width + this.rx;
/* 129 */       int mi = my * this.rw;
/* 130 */       for (int x = this.rx; x < this.rx + this.rw; x++) {
/* 131 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/* 132 */           if (limit) {
/* 133 */             int v = pixels[i] & 0xFFFF;
/* 134 */             if ((v >= minThreshold) && (v <= maxThreshold)) {
/* 135 */               count++;
/* 136 */               xsum += x;
/* 137 */               ysum += y;
/*     */             }
/*     */           } else {
/* 140 */             count++;
/* 141 */             xsum += x;
/* 142 */             ysum += y;
/*     */           }
/*     */         }
/* 145 */         i++;
/*     */       }
/* 127 */       y++;
/*     */     }
/*     */ 
/* 148 */     this.xCentroid = (xsum / count + 0.5D);
/* 149 */     this.yCentroid = (ysum / count + 0.5D);
/* 150 */     if (this.cal != null) {
/* 151 */       this.xCentroid = this.cal.getX(this.xCentroid);
/* 152 */       this.yCentroid = this.cal.getY(this.yCentroid, this.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   void calculateMoments(ImageProcessor ip, int minThreshold, int maxThreshold, float[] cTable) {
/* 157 */     short[] pixels = (short[])ip.getPixels();
/* 158 */     byte[] mask = ip.getMaskArray();
/*     */ 
/* 160 */     double sum1 = 0.0D; double sum2 = 0.0D; double sum3 = 0.0D; double sum4 = 0.0D; double xsum = 0.0D; double ysum = 0.0D;
/* 161 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/* 162 */       int i = y * this.width + this.rx;
/* 163 */       int mi = my * this.rw;
/* 164 */       for (int x = this.rx; x < this.rx + this.rw; x++) {
/* 165 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/* 166 */           int iv = pixels[i] & 0xFFFF;
/* 167 */           if ((iv >= minThreshold) && (iv <= maxThreshold)) {
/* 168 */             double v = cTable != null ? cTable[iv] : iv;
/* 169 */             double v2 = v * v;
/* 170 */             sum1 += v;
/* 171 */             sum2 += v2;
/* 172 */             sum3 += v * v2;
/* 173 */             sum4 += v2 * v2;
/* 174 */             xsum += x * v;
/* 175 */             ysum += y * v;
/*     */           }
/*     */         }
/* 178 */         i++;
/*     */       }
/* 161 */       y++;
/*     */     }
/*     */ 
/* 181 */     double mean2 = this.mean * this.mean;
/* 182 */     double variance = sum2 / this.pixelCount - mean2;
/* 183 */     double sDeviation = Math.sqrt(variance);
/* 184 */     this.skewness = (((sum3 - 3.0D * this.mean * sum2) / this.pixelCount + 2.0D * this.mean * mean2) / (variance * sDeviation));
/* 185 */     this.kurtosis = (((sum4 - 4.0D * this.mean * sum3 + 6.0D * mean2 * sum2) / this.pixelCount - 3.0D * mean2 * mean2) / (variance * variance) - 3.0D);
/* 186 */     this.xCenterOfMass = (xsum / sum1 + 0.5D);
/* 187 */     this.yCenterOfMass = (ysum / sum1 + 0.5D);
/* 188 */     if (this.cal != null) {
/* 189 */       this.xCenterOfMass = this.cal.getX(this.xCenterOfMass);
/* 190 */       this.yCenterOfMass = this.cal.getY(this.yCenterOfMass, this.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   void getCalibratedMinAndMax(int[] hist, int minValue, int maxValue, float[] cTable) {
/* 195 */     this.min = 1.7976931348623157E+308D;
/* 196 */     this.max = -1.797693134862316E+308D;
/* 197 */     double v = 0.0D;
/* 198 */     for (int i = minValue; i <= maxValue; i++)
/* 199 */       if (hist[i] > 0) {
/* 200 */         v = cTable[i];
/* 201 */         if (v < this.min) this.min = v;
/* 202 */         if (v > this.max) this.max = v;
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ShortStatistics
 * JD-Core Version:    0.6.2
 */