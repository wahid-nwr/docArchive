/*     */ package ij.process;
/*     */ 
/*     */ class Cube
/*     */ {
/*     */   int lower;
/*     */   int upper;
/*     */   int count;
/*     */   int level;
/*     */   int rmin;
/*     */   int rmax;
/*     */   int gmin;
/*     */   int gmax;
/*     */   int bmin;
/*     */   int bmax;
/*     */ 
/*     */   Cube()
/*     */   {
/* 391 */     this.count = 0;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 395 */     String s = "lower=" + this.lower + " upper=" + this.upper;
/* 396 */     s = s + " count=" + this.count + " level=" + this.level;
/* 397 */     s = s + " rmin=" + this.rmin + " rmax=" + this.rmax;
/* 398 */     s = s + " gmin=" + this.gmin + " gmax=" + this.gmax;
/* 399 */     s = s + " bmin=" + this.bmin + " bmax=" + this.bmax;
/* 400 */     return s;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.Cube
 * JD-Core Version:    0.6.2
 */