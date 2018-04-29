/*    */ package ij.process;
/*    */ 
/*    */ import java.awt.Rectangle;
/*    */ 
/*    */ public class FloatPolygon
/*    */ {
/*    */   Rectangle bounds;
/*    */   public int npoints;
/*    */   public float[] xpoints;
/*    */   public float[] ypoints;
/*    */ 
/*    */   public FloatPolygon()
/*    */   {
/* 20 */     this.npoints = 0;
/* 21 */     this.xpoints = new float[10];
/* 22 */     this.ypoints = new float[10];
/*    */   }
/*    */ 
/*    */   public FloatPolygon(float[] xpoints, float[] ypoints, int npoints)
/*    */   {
/* 27 */     this.npoints = npoints;
/* 28 */     this.xpoints = xpoints;
/* 29 */     this.ypoints = ypoints;
/*    */   }
/*    */ 
/*    */   public boolean contains(float x, float y)
/*    */   {
/* 37 */     boolean inside = false;
/* 38 */     int i = 0; for (int j = this.npoints - 1; i < this.npoints; j = i++)
/* 39 */       if ((this.ypoints[i] > y ? 1 : 0) != (this.ypoints[j] > y ? 1 : 0)) if (x < (this.xpoints[j] - this.xpoints[i]) * (y - this.ypoints[i]) / (this.ypoints[j] - this.ypoints[i]) + this.xpoints[i])
/*    */         {
/* 41 */           inside = !inside;
/*    */         }
/* 43 */     return inside;
/*    */   }
/*    */ 
/*    */   public Rectangle getBounds() {
/* 47 */     if (this.npoints == 0)
/* 48 */       return new Rectangle();
/* 49 */     if (this.bounds == null)
/* 50 */       calculateBounds(this.xpoints, this.ypoints, this.npoints);
/* 51 */     return this.bounds.getBounds();
/*    */   }
/*    */ 
/*    */   void calculateBounds(float[] xpoints, float[] ypoints, int npoints) {
/* 55 */     float minX = 3.4028235E+38F;
/* 56 */     float minY = 3.4028235E+38F;
/* 57 */     float maxX = 1.4E-45F;
/* 58 */     float maxY = 1.4E-45F;
/* 59 */     for (int i = 0; i < npoints; i++) {
/* 60 */       float x = xpoints[i];
/* 61 */       minX = Math.min(minX, x);
/* 62 */       maxX = Math.max(maxX, x);
/* 63 */       float y = ypoints[i];
/* 64 */       minY = Math.min(minY, y);
/* 65 */       maxY = Math.max(maxY, y);
/*    */     }
/* 67 */     int iMinX = (int)Math.floor(minX);
/* 68 */     int iMinY = (int)Math.floor(minY);
/* 69 */     this.bounds = new Rectangle(iMinX, iMinY, (int)(maxX - iMinX + 0.5D), (int)(maxY - iMinY + 0.5D));
/*    */   }
/*    */ 
/*    */   public void addPoint(float x, float y) {
/* 73 */     if (this.npoints == this.xpoints.length) {
/* 74 */       float[] tmp = new float[this.npoints * 2];
/* 75 */       System.arraycopy(this.xpoints, 0, tmp, 0, this.npoints);
/* 76 */       this.xpoints = tmp;
/* 77 */       tmp = new float[this.npoints * 2];
/* 78 */       System.arraycopy(this.ypoints, 0, tmp, 0, this.npoints);
/* 79 */       this.ypoints = tmp;
/*    */     }
/* 81 */     this.xpoints[this.npoints] = x;
/* 82 */     this.ypoints[this.npoints] = y;
/* 83 */     this.npoints += 1;
/* 84 */     this.bounds = null;
/*    */   }
/*    */ 
/*    */   public void addPoint(double x, double y) {
/* 88 */     addPoint((float)x, (float)y);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.FloatPolygon
 * JD-Core Version:    0.6.2
 */