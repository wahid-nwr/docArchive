/*    */ package ltkrn;
/*    */ 
/*    */ public class L_RECTD
/*    */ {
/*    */   private long swigCPtr;
/*    */   protected boolean swigCMemOwn;
/*    */ 
/*    */   public L_RECTD(long cPtr, boolean cMemoryOwn)
/*    */   {
/* 16 */     this.swigCMemOwn = cMemoryOwn;
/* 17 */     this.swigCPtr = cPtr;
/*    */   }
/*    */ 
/*    */   public static long getCPtr(L_RECTD obj) {
/* 21 */     return obj == null ? 0L : obj.swigCPtr;
/*    */   }
/*    */ 
/*    */   protected void finalize() {
/* 25 */     delete();
/*    */   }
/*    */ 
/*    */   public synchronized void delete() {
/* 29 */     if (this.swigCPtr != 0L) {
/* 30 */       if (this.swigCMemOwn) {
/* 31 */         this.swigCMemOwn = false;
/* 32 */         ltkrnJNI.delete_L_RECTD(this.swigCPtr);
/*    */       }
/* 34 */       this.swigCPtr = 0L;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setX(double value) {
/* 39 */     ltkrnJNI.L_RECTD_x_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public double getX() {
/* 43 */     return ltkrnJNI.L_RECTD_x_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setY(double value) {
/* 47 */     ltkrnJNI.L_RECTD_y_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public double getY() {
/* 51 */     return ltkrnJNI.L_RECTD_y_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setWidth(double value) {
/* 55 */     ltkrnJNI.L_RECTD_width_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public double getWidth() {
/* 59 */     return ltkrnJNI.L_RECTD_width_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setHeight(double value) {
/* 63 */     ltkrnJNI.L_RECTD_height_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public double getHeight() {
/* 67 */     return ltkrnJNI.L_RECTD_height_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public L_RECTD() {
/* 71 */     this(ltkrnJNI.new_L_RECTD(), true);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.L_RECTD
 * JD-Core Version:    0.6.2
 */