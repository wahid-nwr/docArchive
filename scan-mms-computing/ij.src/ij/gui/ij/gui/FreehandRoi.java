/*    */ package ij.gui;
/*    */ 
/*    */ import ij.ImagePlus;
/*    */ 
/*    */ public class FreehandRoi extends PolygonRoi
/*    */ {
/*    */   public FreehandRoi(int sx, int sy, ImagePlus imp)
/*    */   {
/* 12 */     super(sx, sy, imp);
/* 13 */     if (Toolbar.getToolId() == 3)
/* 14 */       this.type = 3;
/*    */     else
/* 16 */       this.type = 7;
/* 17 */     if (this.nPoints == 2) this.nPoints -= 1; 
/*    */   }
/*    */ 
/*    */   protected void grow(int sx, int sy)
/*    */   {
/* 21 */     int ox = this.ic.offScreenX(sx);
/* 22 */     int oy = this.ic.offScreenY(sy);
/* 23 */     if (ox < 0) ox = 0;
/* 24 */     if (oy < 0) oy = 0;
/* 25 */     if (ox > this.xMax) ox = this.xMax;
/* 26 */     if (oy > this.yMax) oy = this.yMax;
/* 27 */     if ((ox != this.xp[(this.nPoints - 1)] + this.x) || (oy != this.yp[(this.nPoints - 1)] + this.y)) {
/* 28 */       this.xp[this.nPoints] = (ox - this.x);
/* 29 */       this.yp[this.nPoints] = (oy - this.y);
/* 30 */       this.nPoints += 1;
/* 31 */       if (this.nPoints == this.xp.length)
/* 32 */         enlargeArrays();
/* 33 */       drawLine();
/*    */     }
/*    */   }
/*    */ 
/*    */   void drawLine() {
/* 38 */     int x1 = this.xp[(this.nPoints - 2)] + this.x;
/* 39 */     int y1 = this.yp[(this.nPoints - 2)] + this.y;
/* 40 */     int x2 = this.xp[(this.nPoints - 1)] + this.x;
/* 41 */     int y2 = this.yp[(this.nPoints - 1)] + this.y;
/* 42 */     int xmin = Math.min(x1, x2);
/* 43 */     int xmax = Math.max(x1, x2);
/* 44 */     int ymin = Math.min(y1, y2);
/* 45 */     int ymax = Math.max(y1, y2);
/* 46 */     int margin = 4;
/* 47 */     if ((lineWidth > margin) && (isLine()))
/* 48 */       margin = lineWidth;
/* 49 */     if (this.ic != null) {
/* 50 */       double mag = this.ic.getMagnification();
/* 51 */       if (mag < 1.0D) margin = (int)(margin / mag);
/*    */     }
/* 53 */     this.imp.draw(xmin - margin, ymin - margin, xmax - xmin + margin * 2, ymax - ymin + margin * 2);
/*    */   }
/*    */ 
/*    */   protected void handleMouseUp(int screenX, int screenY) {
/* 57 */     if (this.state == 0) {
/* 58 */       addOffset();
/* 59 */       finishPolygon();
/*    */     }
/* 61 */     this.state = 3;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.FreehandRoi
 * JD-Core Version:    0.6.2
 */