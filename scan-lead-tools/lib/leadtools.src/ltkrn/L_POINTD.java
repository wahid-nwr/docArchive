/*    */ package ltkrn;
/*    */ 
/*    */ public class L_POINTD
/*    */ {
/*    */   private long swigCPtr;
/*    */   protected boolean swigCMemOwn;
/*    */ 
/*    */   public L_POINTD(long cPtr, boolean cMemoryOwn)
/*    */   {
/* 16 */     this.swigCMemOwn = cMemoryOwn;
/* 17 */     this.swigCPtr = cPtr;
/*    */   }
/*    */ 
/*    */   public static long getCPtr(L_POINTD obj) {
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
/* 32 */         ltkrnJNI.delete_L_POINTD(this.swigCPtr);
/*    */       }
/* 34 */       this.swigCPtr = 0L;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setX(double value) {
/* 39 */     ltkrnJNI.L_POINTD_x_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public double getX() {
/* 43 */     return ltkrnJNI.L_POINTD_x_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setY(double value) {
/* 47 */     ltkrnJNI.L_POINTD_y_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public double getY() {
/* 51 */     return ltkrnJNI.L_POINTD_y_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public L_POINTD() {
/* 55 */     this(ltkrnJNI.new_L_POINTD(), true);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.L_POINTD
 * JD-Core Version:    0.6.2
 */