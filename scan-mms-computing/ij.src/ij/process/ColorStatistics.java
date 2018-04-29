/*    */ package ij.process;
/*    */ 
/*    */ import ij.measure.Calibration;
/*    */ 
/*    */ public class ColorStatistics extends ImageStatistics
/*    */ {
/*    */   public ColorStatistics(ImageProcessor ip)
/*    */   {
/* 11 */     this(ip, 27, null);
/*    */   }
/*    */ 
/*    */   public ColorStatistics(ImageProcessor ip, int mOptions, Calibration cal)
/*    */   {
/* 18 */     ColorProcessor cp = (ColorProcessor)ip;
/* 19 */     this.histogram = cp.getHistogram();
/* 20 */     setup(ip, cal);
/* 21 */     getRawStatistics(0, 255);
/* 22 */     if ((mOptions & 0x10) != 0)
/* 23 */       getRawMinAndMax(0, 255);
/* 24 */     if (((mOptions & 0x800) != 0) || ((mOptions & 0x2000) != 0))
/* 25 */       fitEllipse(ip, mOptions);
/* 26 */     else if ((mOptions & 0x20) != 0)
/* 27 */       getCentroid(ip);
/* 28 */     if ((mOptions & 0x60040) != 0)
/* 29 */       calculateMoments(ip);
/* 30 */     if ((mOptions & 0x10000) != 0)
/* 31 */       calculateMedian(this.histogram, 0, 255, cal);
/*    */   }
/*    */ 
/*    */   void calculateMoments(ImageProcessor ip) {
/* 35 */     byte[] mask = ip.getMaskArray();
/*    */ 
/* 37 */     double sum1 = 0.0D; double sum2 = 0.0D; double sum3 = 0.0D; double sum4 = 0.0D; double xsum = 0.0D; double ysum = 0.0D;
/* 38 */     int y = this.ry; for (int my = 0; y < this.ry + this.rh; my++) {
/* 39 */       int i = y * this.width + this.rx;
/* 40 */       int mi = my * this.rw;
/* 41 */       for (int x = this.rx; x < this.rx + this.rw; x++) {
/* 42 */         if ((mask == null) || (mask[(mi++)] != 0)) {
/* 43 */           double v = ip.getPixelValue(x, y);
/* 44 */           double v2 = v * v;
/* 45 */           sum1 += v;
/* 46 */           sum2 += v2;
/* 47 */           sum3 += v * v2;
/* 48 */           sum4 += v2 * v2;
/* 49 */           xsum += x * v;
/* 50 */           ysum += y * v;
/*    */         }
/* 52 */         i++;
/*    */       }
/* 38 */       y++;
/*    */     }
/*    */ 
/* 55 */     double mean2 = this.mean * this.mean;
/* 56 */     double variance = sum2 / this.pixelCount - mean2;
/* 57 */     double sDeviation = Math.sqrt(variance);
/* 58 */     this.skewness = (((sum3 - 3.0D * this.mean * sum2) / this.pixelCount + 2.0D * this.mean * mean2) / (variance * sDeviation));
/* 59 */     this.kurtosis = (((sum4 - 4.0D * this.mean * sum3 + 6.0D * mean2 * sum2) / this.pixelCount - 3.0D * mean2 * mean2) / (variance * variance) - 3.0D);
/* 60 */     this.xCenterOfMass = (xsum / sum1 + 0.5D);
/* 61 */     this.yCenterOfMass = (ysum / sum1 + 0.5D);
/* 62 */     if (this.cal != null) {
/* 63 */       this.xCenterOfMass = this.cal.getX(this.xCenterOfMass);
/* 64 */       this.yCenterOfMass = this.cal.getY(this.yCenterOfMass, this.height);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ColorStatistics
 * JD-Core Version:    0.6.2
 */