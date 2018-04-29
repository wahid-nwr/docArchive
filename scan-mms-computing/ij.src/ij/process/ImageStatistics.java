/*     */ package ij.process;
/*     */ 
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.Measurements;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class ImageStatistics
/*     */   implements Measurements
/*     */ {
/*     */   public int[] histogram;
/*     */   public int pixelCount;
/*     */   public long longPixelCount;
/*     */   public int mode;
/*     */   public double dmode;
/*     */   public double area;
/*     */   public double min;
/*     */   public double max;
/*     */   public double mean;
/*     */   public double median;
/*     */   public double stdDev;
/*     */   public double skewness;
/*     */   public double kurtosis;
/*     */   public double xCentroid;
/*     */   public double yCentroid;
/*     */   public double xCenterOfMass;
/*     */   public double yCenterOfMass;
/*     */   public double roiX;
/*     */   public double roiY;
/*     */   public double roiWidth;
/*     */   public double roiHeight;
/*     */   public double umean;
/*     */   public double major;
/*     */   public double minor;
/*     */   public double angle;
/*     */   public int[] histogram16;
/*     */   public double areaFraction;
/*     */   public int xstart;
/*     */   public int ystart;
/*     */   public double histMin;
/*     */   public double histMax;
/*     */   public int histYMax;
/*     */   public int maxCount;
/*  44 */   public int nBins = 256;
/*  45 */   public double binSize = 1.0D;
/*     */   protected int width;
/*     */   protected int height;
/*     */   protected int rx;
/*     */   protected int ry;
/*     */   protected int rw;
/*     */   protected int rh;
/*     */   protected double pw;
/*     */   protected double ph;
/*     */   protected Calibration cal;
/*     */   EllipseFitter ef;
/*     */ 
/*     */   public static ImageStatistics getStatistics(ImageProcessor ip, int mOptions, Calibration cal)
/*     */   {
/*  56 */     Object pixels = ip.getPixels();
/*  57 */     if ((pixels instanceof byte[]))
/*  58 */       return new ByteStatistics(ip, mOptions, cal);
/*  59 */     if ((pixels instanceof short[]))
/*  60 */       return new ShortStatistics(ip, mOptions, cal);
/*  61 */     if ((pixels instanceof int[]))
/*  62 */       return new ColorStatistics(ip, mOptions, cal);
/*  63 */     if ((pixels instanceof float[])) {
/*  64 */       return new FloatStatistics(ip, mOptions, cal);
/*     */     }
/*  66 */     throw new IllegalArgumentException("Pixels are not byte, short, int or float");
/*     */   }
/*     */ 
/*     */   void getRawMinAndMax(int minThreshold, int maxThreshold) {
/*  70 */     int min = minThreshold;
/*  71 */     while ((this.histogram[min] == 0) && (min < 255))
/*  72 */       min++;
/*  73 */     this.min = min;
/*  74 */     int max = maxThreshold;
/*  75 */     while ((this.histogram[max] == 0) && (max > 0))
/*  76 */       max--;
/*  77 */     this.max = max;
/*     */   }
/*     */ 
/*     */   void getRawStatistics(int minThreshold, int maxThreshold)
/*     */   {
/*  83 */     double sum = 0.0D;
/*  84 */     double sum2 = 0.0D;
/*     */ 
/*  86 */     for (int i = minThreshold; i <= maxThreshold; i++) {
/*  87 */       int count = this.histogram[i];
/*  88 */       this.longPixelCount += count;
/*  89 */       sum += i * count;
/*  90 */       double value = i;
/*  91 */       sum2 += value * value * count;
/*  92 */       if (count > this.maxCount) {
/*  93 */         this.maxCount = count;
/*  94 */         this.mode = i;
/*     */       }
/*     */     }
/*  97 */     this.pixelCount = ((int)this.longPixelCount);
/*  98 */     this.area = (this.longPixelCount * this.pw * this.ph);
/*  99 */     this.mean = (sum / this.longPixelCount);
/* 100 */     this.umean = this.mean;
/* 101 */     this.dmode = this.mode;
/* 102 */     calculateStdDev(this.longPixelCount, sum, sum2);
/* 103 */     this.histMin = 0.0D;
/* 104 */     this.histMax = 255.0D;
/*     */   }
/*     */ 
/*     */   void calculateStdDev(double n, double sum, double sum2) {
/* 108 */     if (n > 0.0D) {
/* 109 */       this.stdDev = ((n * sum2 - sum * sum) / n);
/* 110 */       if (this.stdDev > 0.0D)
/* 111 */         this.stdDev = Math.sqrt(this.stdDev / (n - 1.0D));
/*     */       else
/* 113 */         this.stdDev = 0.0D;
/*     */     } else {
/* 115 */       this.stdDev = 0.0D;
/*     */     }
/*     */   }
/*     */ 
/* 119 */   void setup(ImageProcessor ip, Calibration cal) { this.width = ip.getWidth();
/* 120 */     this.height = ip.getHeight();
/* 121 */     this.cal = cal;
/* 122 */     Rectangle roi = ip.getRoi();
/* 123 */     if (roi != null) {
/* 124 */       this.rx = roi.x;
/* 125 */       this.ry = roi.y;
/* 126 */       this.rw = roi.width;
/* 127 */       this.rh = roi.height;
/*     */     }
/*     */     else {
/* 130 */       this.rx = 0;
/* 131 */       this.ry = 0;
/* 132 */       this.rw = this.width;
/* 133 */       this.rh = this.height;
/*     */     }
/*     */ 
/* 136 */     if (cal != null) {
/* 137 */       this.pw = cal.pixelWidth;
/* 138 */       this.ph = cal.pixelHeight;
/*     */     } else {
/* 140 */       this.pw = 1.0D;
/* 141 */       this.ph = 1.0D;
/*     */     }
/*     */ 
/* 144 */     this.roiX = (cal != null ? cal.getX(this.rx) : this.rx);
/* 145 */     this.roiY = (cal != null ? cal.getY(this.ry, this.height) : this.ry);
/* 146 */     this.roiWidth = (this.rw * this.pw);
/* 147 */     this.roiHeight = (this.rh * this.ph); }
/*     */ 
/*     */   void getCentroid(ImageProcessor ip)
/*     */   {
/* 151 */     byte[] mask = ip.getMaskArray();
/* 152 */     int count = 0;
/* 153 */     double xsum = 0.0D; double ysum = 0.0D;
/* 154 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/* 155 */       int mi = my * this.rw;
/* 156 */       for (int x = this.rx; x < this.rx + this.rw; x++)
/* 157 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/* 158 */           count++;
/* 159 */           xsum += x;
/* 160 */           ysum += y;
/*     */         }
/* 154 */       y++;
/*     */     }
/*     */ 
/* 164 */     this.xCentroid = (xsum / count + 0.5D);
/* 165 */     this.yCentroid = (ysum / count + 0.5D);
/* 166 */     if (this.cal != null) {
/* 167 */       this.xCentroid = this.cal.getX(this.xCentroid);
/* 168 */       this.yCentroid = this.cal.getY(this.yCentroid, this.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   void fitEllipse(ImageProcessor ip, int mOptions) {
/* 173 */     ImageProcessor originalMask = null;
/* 174 */     boolean limitToThreshold = ((mOptions & 0x100) != 0) && (ip.getMinThreshold() != -808080.0D);
/* 175 */     if (limitToThreshold) {
/* 176 */       ImageProcessor mask = ip.getMask();
/* 177 */       Rectangle r = ip.getRoi();
/* 178 */       if (mask == null) {
/* 179 */         mask = new ByteProcessor(r.width, r.height);
/* 180 */         mask.invert();
/*     */       } else {
/* 182 */         originalMask = mask;
/* 183 */         mask = mask.duplicate();
/*     */       }
/* 185 */       int n = r.width * r.height;
/* 186 */       double t1 = ip.getMinThreshold();
/* 187 */       double t2 = ip.getMaxThreshold();
/*     */ 
/* 189 */       for (int y = 0; y < r.height; y++) {
/* 190 */         for (int x = 0; x < r.width; x++) {
/* 191 */           double value = ip.getf(r.x + x, r.y + y);
/* 192 */           if ((value < t1) || (value > t2))
/* 193 */             mask.setf(x, y, 0.0F);
/*     */         }
/*     */       }
/* 196 */       ip.setMask(mask);
/*     */     }
/* 198 */     if (this.ef == null)
/* 199 */       this.ef = new EllipseFitter();
/* 200 */     this.ef.fit(ip, this);
/* 201 */     if (limitToThreshold) {
/* 202 */       if (originalMask == null)
/* 203 */         ip.setMask(null);
/*     */       else
/* 205 */         ip.setMask(originalMask);
/*     */     }
/* 207 */     double psize = Math.abs(this.pw - this.ph) / this.pw < 0.01D ? this.pw : 0.0D;
/* 208 */     this.major = (this.ef.major * psize);
/* 209 */     this.minor = (this.ef.minor * psize);
/* 210 */     this.angle = this.ef.angle;
/* 211 */     this.xCentroid = this.ef.xCenter;
/* 212 */     this.yCentroid = this.ef.yCenter;
/* 213 */     if (this.cal != null) {
/* 214 */       this.xCentroid = this.cal.getX(this.xCentroid);
/* 215 */       this.yCentroid = this.cal.getY(this.yCentroid, this.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void drawEllipse(ImageProcessor ip) {
/* 220 */     if (this.ef != null)
/* 221 */       this.ef.drawEllipse(ip);
/*     */   }
/*     */ 
/*     */   void calculateMedian(int[] hist, int first, int last, Calibration cal)
/*     */   {
/* 226 */     double sum = 0.0D;
/* 227 */     int i = first - 1;
/* 228 */     double halfCount = this.pixelCount / 2.0D;
/*     */     do
/* 230 */       sum += hist[(++i)];
/* 231 */     while ((sum <= halfCount) && (i < last));
/* 232 */     this.median = (cal != null ? cal.getCValue(i) : i);
/*     */   }
/*     */ 
/*     */   void calculateAreaFraction(ImageProcessor ip, int[] hist) {
/* 236 */     int sum = 0;
/* 237 */     int total = 0;
/* 238 */     int t1 = (int)ip.getMinThreshold();
/* 239 */     int t2 = (int)ip.getMaxThreshold();
/* 240 */     if (t1 == -808080.0D) {
/* 241 */       for (int i = 0; i < hist.length; i++)
/* 242 */         total += hist[i];
/* 243 */       sum = total - hist[0];
/*     */     } else {
/* 245 */       for (int i = 0; i < hist.length; i++) {
/* 246 */         if ((i >= t1) && (i <= t2))
/* 247 */           sum += hist[i];
/* 248 */         total += hist[i];
/*     */       }
/*     */     }
/* 251 */     this.areaFraction = (sum * 100.0D / total);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 255 */     return "stats[count=" + this.pixelCount + ", mean=" + this.mean + ", min=" + this.min + ", max=" + this.max + "]";
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ImageStatistics
 * JD-Core Version:    0.6.2
 */