/*    */ package ij.measure;
/*    */ 
/*    */ public class SplineFitter
/*    */ {
/*    */   private double[] y2;
/*    */ 
/*    */   public SplineFitter(int[] x, int[] y, int n)
/*    */   {
/* 14 */     initSpline(x, y, n);
/*    */   }
/*    */ 
/*    */   void initSpline(int[] x, int[] y, int n)
/*    */   {
/* 24 */     this.y2 = new double[n];
/* 25 */     double[] u = new double[n];
/* 26 */     for (int i = 1; i < n - 1; i++)
/*    */     {
/* 28 */       double sig = (x[i] - x[(i - 1)]) / (x[(i + 1)] - x[(i - 1)]);
/* 29 */       double p = sig * this.y2[(i - 1)] + 2.0D;
/* 30 */       this.y2[i] = ((sig - 1.0D) / p);
/* 31 */       u[i] = ((y[(i + 1)] - y[i]) / (x[(i + 1)] - x[i]) - (y[i] - y[(i - 1)]) / (x[i] - x[(i - 1)]));
/*    */ 
/* 33 */       u[i] = ((6.0D * u[i] / (x[(i + 1)] - x[(i - 1)]) - sig * u[(i - 1)]) / p);
/*    */     }
/*    */     double un;
/* 35 */     double qn = un = 0.0D;
/* 36 */     this.y2[(n - 1)] = ((un - qn * u[(n - 2)]) / (qn * this.y2[(n - 2)] + 1.0D));
/* 37 */     for (int k = n - 2; k >= 0; k--)
/* 38 */       this.y2[k] = (this.y2[k] * this.y2[(k + 1)] + u[k]);
/*    */   }
/*    */ 
/*    */   public double evalSpline(int[] x, int[] y, int n, double xp)
/*    */   {
/* 45 */     int klo = 0;
/* 46 */     int khi = n - 1;
/* 47 */     while (khi - klo > 1) {
/* 48 */       int k = khi + klo >> 1;
/* 49 */       if (x[k] > xp) khi = k; else
/* 50 */         klo = k;
/*    */     }
/* 52 */     double h = x[khi] - x[klo];
/*    */ 
/* 55 */     if (h == 0.0D) return 0.0D;
/* 56 */     double a = (x[khi] - xp) / h;
/* 57 */     double b = (xp - x[klo]) / h;
/*    */ 
/* 60 */     if (this.y2 == null) return 0.0D;
/*    */ 
/* 62 */     return a * y[klo] + b * y[khi] + ((a * a * a - a) * this.y2[klo] + (b * b * b - b) * this.y2[khi]) * (h * h) / 6.0D;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.measure.SplineFitter
 * JD-Core Version:    0.6.2
 */