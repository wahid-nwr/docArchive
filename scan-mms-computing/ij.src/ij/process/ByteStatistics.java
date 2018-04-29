/*     */ package ij.process;
/*     */ 
/*     */ import ij.measure.Calibration;
/*     */ 
/*     */ public class ByteStatistics extends ImageStatistics
/*     */ {
/*     */   public ByteStatistics(ImageProcessor ip)
/*     */   {
/*  11 */     this(ip, 27, null);
/*     */   }
/*     */ 
/*     */   public ByteStatistics(ImageProcessor ip, int mOptions, Calibration cal)
/*     */   {
/*  17 */     ByteProcessor bp = (ByteProcessor)ip;
/*  18 */     this.histogram = bp.getHistogram();
/*  19 */     setup(ip, cal);
/*  20 */     double minT = ip.getMinThreshold();
/*     */     int maxThreshold;
/*     */     int minThreshold;
/*     */     int maxThreshold;
/*  22 */     if (((mOptions & 0x100) == 0) || (minT == -808080.0D)) {
/*  23 */       int minThreshold = 0; maxThreshold = 255;
/*     */     } else {
/*  25 */       minThreshold = (int)minT; maxThreshold = (int)ip.getMaxThreshold();
/*  26 */     }float[] cTable = cal != null ? cal.getCTable() : null;
/*  27 */     if (cTable != null)
/*  28 */       getCalibratedStatistics(minThreshold, maxThreshold, cTable);
/*     */     else
/*  30 */       getRawStatistics(minThreshold, maxThreshold);
/*  31 */     if ((mOptions & 0x10) != 0) {
/*  32 */       if (cTable != null)
/*  33 */         getCalibratedMinAndMax(minThreshold, maxThreshold, cTable);
/*     */       else
/*  35 */         getRawMinAndMax(minThreshold, maxThreshold);
/*     */     }
/*  37 */     if (((mOptions & 0x800) != 0) || ((mOptions & 0x2000) != 0))
/*  38 */       fitEllipse(ip, mOptions);
/*  39 */     else if ((mOptions & 0x20) != 0)
/*  40 */       getCentroid(ip, minThreshold, maxThreshold);
/*  41 */     if ((mOptions & 0x60040) != 0)
/*  42 */       calculateMoments(ip, minThreshold, maxThreshold, cTable);
/*  43 */     if ((mOptions & 0x10000) != 0)
/*  44 */       calculateMedian(this.histogram, minThreshold, maxThreshold, cal);
/*  45 */     if ((mOptions & 0x80000) != 0)
/*  46 */       calculateAreaFraction(ip, this.histogram);
/*     */   }
/*     */ 
/*     */   void getCalibratedStatistics(int minThreshold, int maxThreshold, float[] cTable)
/*     */   {
/*  52 */     double sum = 0.0D;
/*  53 */     double sum2 = 0.0D;
/*  54 */     double isum = 0.0D;
/*     */ 
/*  56 */     for (int i = minThreshold; i <= maxThreshold; i++) {
/*  57 */       int count = this.histogram[i];
/*  58 */       double value = cTable[i];
/*  59 */       if ((count > 0) && (!Double.isNaN(value))) {
/*  60 */         this.pixelCount += count;
/*  61 */         sum += value * count;
/*  62 */         isum += i * count;
/*  63 */         sum2 += value * value * count;
/*  64 */         if (count > this.maxCount) {
/*  65 */           this.maxCount = count;
/*  66 */           this.mode = i;
/*     */         }
/*     */       }
/*     */     }
/*  70 */     this.area = (this.pixelCount * this.pw * this.ph);
/*  71 */     this.mean = (sum / this.pixelCount);
/*  72 */     this.umean = (isum / this.pixelCount);
/*  73 */     this.dmode = cTable[this.mode];
/*  74 */     calculateStdDev(this.pixelCount, sum, sum2);
/*  75 */     this.histMin = 0.0D;
/*  76 */     this.histMax = 255.0D;
/*     */   }
/*     */ 
/*     */   void getCentroid(ImageProcessor ip, int minThreshold, int maxThreshold) {
/*  80 */     byte[] pixels = (byte[])ip.getPixels();
/*  81 */     byte[] mask = ip.getMaskArray();
/*  82 */     boolean limit = (minThreshold > 0) || (maxThreshold < 255);
/*  83 */     double xsum = 0.0D; double ysum = 0.0D;
/*  84 */     int count = 0;
/*  85 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/*  86 */       int i = y * this.width + this.rx;
/*  87 */       int mi = my * this.rw;
/*  88 */       for (int x = this.rx; x < this.rx + this.rw; x++) {
/*  89 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/*  90 */           if (limit) {
/*  91 */             int v = pixels[i] & 0xFF;
/*  92 */             if ((v >= minThreshold) && (v <= maxThreshold)) {
/*  93 */               count++;
/*  94 */               xsum += x;
/*  95 */               ysum += y;
/*     */             }
/*     */           } else {
/*  98 */             count++;
/*  99 */             xsum += x;
/* 100 */             ysum += y;
/*     */           }
/*     */         }
/* 103 */         i++;
/*     */       }
/*  85 */       y++;
/*     */     }
/*     */ 
/* 106 */     this.xCentroid = (xsum / count + 0.5D);
/* 107 */     this.yCentroid = (ysum / count + 0.5D);
/* 108 */     if (this.cal != null) {
/* 109 */       this.xCentroid = this.cal.getX(this.xCentroid);
/* 110 */       this.yCentroid = this.cal.getY(this.yCentroid, this.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   void calculateMoments(ImageProcessor ip, int minThreshold, int maxThreshold, float[] cTable) {
/* 115 */     byte[] pixels = (byte[])ip.getPixels();
/* 116 */     byte[] mask = ip.getMaskArray();
/*     */ 
/* 118 */     double sum1 = 0.0D; double sum2 = 0.0D; double sum3 = 0.0D; double sum4 = 0.0D; double xsum = 0.0D; double ysum = 0.0D;
/* 119 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/* 120 */       int i = y * this.width + this.rx;
/* 121 */       int mi = my * this.rw;
/* 122 */       for (int x = this.rx; x < this.rx + this.rw; x++) {
/* 123 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/* 124 */           int v = pixels[i] & 0xFF;
/* 125 */           if ((v >= minThreshold) && (v <= maxThreshold)) {
/* 126 */             double dv = (cTable != null ? cTable[v] : v) + 4.9E-324D;
/* 127 */             double dv2 = dv * dv;
/* 128 */             sum1 += dv;
/* 129 */             sum2 += dv2;
/* 130 */             sum3 += dv * dv2;
/* 131 */             sum4 += dv2 * dv2;
/* 132 */             xsum += x * dv;
/* 133 */             ysum += y * dv;
/*     */           }
/*     */         }
/* 136 */         i++;
/*     */       }
/* 119 */       y++;
/*     */     }
/*     */ 
/* 139 */     double mean2 = this.mean * this.mean;
/* 140 */     double variance = sum2 / this.pixelCount - mean2;
/* 141 */     double sDeviation = Math.sqrt(variance);
/* 142 */     this.skewness = (((sum3 - 3.0D * this.mean * sum2) / this.pixelCount + 2.0D * this.mean * mean2) / (variance * sDeviation));
/* 143 */     this.kurtosis = (((sum4 - 4.0D * this.mean * sum3 + 6.0D * mean2 * sum2) / this.pixelCount - 3.0D * mean2 * mean2) / (variance * variance) - 3.0D);
/* 144 */     this.xCenterOfMass = (xsum / sum1 + 0.5D);
/* 145 */     this.yCenterOfMass = (ysum / sum1 + 0.5D);
/* 146 */     if (this.cal != null) {
/* 147 */       this.xCenterOfMass = this.cal.getX(this.xCenterOfMass);
/* 148 */       this.yCenterOfMass = this.cal.getY(this.yCenterOfMass, this.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   void getCalibratedMinAndMax(int minThreshold, int maxThreshold, float[] cTable) {
/* 153 */     if (this.pixelCount == 0) {
/* 154 */       this.min = 0.0D; this.max = 0.0D; return;
/* 155 */     }this.min = 1.7976931348623157E+308D;
/* 156 */     this.max = -1.797693134862316E+308D;
/* 157 */     double v = 0.0D;
/* 158 */     for (int i = minThreshold; i <= maxThreshold; i++)
/* 159 */       if (this.histogram[i] > 0) {
/* 160 */         v = cTable[i];
/* 161 */         if (v < this.min) this.min = v;
/* 162 */         if (v > this.max) this.max = v;
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ByteStatistics
 * JD-Core Version:    0.6.2
 */