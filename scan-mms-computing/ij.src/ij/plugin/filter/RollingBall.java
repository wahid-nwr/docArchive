/*     */ package ij.plugin.filter;
/*     */ 
/*     */ class RollingBall
/*     */ {
/*     */   float[] data;
/*     */   int width;
/*     */   int shrinkFactor;
/*     */ 
/*     */   RollingBall(double radius)
/*     */   {
/*     */     int arcTrimPer;
/*     */     int arcTrimPer;
/* 762 */     if (radius <= 10.0D) {
/* 763 */       this.shrinkFactor = 1;
/* 764 */       arcTrimPer = 24;
/*     */     }
/*     */     else
/*     */     {
/*     */       int arcTrimPer;
/* 765 */       if (radius <= 30.0D) {
/* 766 */         this.shrinkFactor = 2;
/* 767 */         arcTrimPer = 24;
/*     */       }
/*     */       else
/*     */       {
/*     */         int arcTrimPer;
/* 768 */         if (radius <= 100.0D) {
/* 769 */           this.shrinkFactor = 4;
/* 770 */           arcTrimPer = 32;
/*     */         } else {
/* 772 */           this.shrinkFactor = 8;
/* 773 */           arcTrimPer = 40;
/*     */         }
/*     */       }
/*     */     }
/* 775 */     buildRollingBall(radius, arcTrimPer);
/*     */   }
/*     */ 
/*     */   void buildRollingBall(double ballradius, int arcTrimPer)
/*     */   {
/* 790 */     this.shrinkFactor = this.shrinkFactor;
/* 791 */     double smallballradius = ballradius / this.shrinkFactor;
/* 792 */     if (smallballradius < 1.0D)
/* 793 */       smallballradius = 1.0D;
/* 794 */     double rsquare = smallballradius * smallballradius;
/* 795 */     int xtrim = (int)(arcTrimPer * smallballradius) / 100;
/* 796 */     int halfWidth = (int)Math.round(smallballradius - xtrim);
/* 797 */     this.width = (2 * halfWidth + 1);
/* 798 */     this.data = new float[this.width * this.width];
/*     */ 
/* 800 */     int y = 0; for (int p = 0; y < this.width; y++)
/* 801 */       for (int x = 0; x < this.width; p++) {
/* 802 */         int xval = x - halfWidth;
/* 803 */         int yval = y - halfWidth;
/* 804 */         double temp = rsquare - xval * xval - yval * yval;
/* 805 */         this.data[p] = (temp > 0.0D ? (float)Math.sqrt(temp) : 0.0F);
/*     */ 
/* 801 */         x++;
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.RollingBall
 * JD-Core Version:    0.6.2
 */