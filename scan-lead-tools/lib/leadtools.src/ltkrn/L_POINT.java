/*    */ package ltkrn;
/*    */ 
/*    */ public class L_POINT
/*    */ {
/*    */   private long swigCPtr;
/*    */   protected boolean swigCMemOwn;
/*    */ 
/*    */   public L_POINT(long cPtr, boolean cMemoryOwn)
/*    */   {
/* 16 */     this.swigCMemOwn = cMemoryOwn;
/* 17 */     this.swigCPtr = cPtr;
/*    */   }
/*    */ 
/*    */   public static long getCPtr(L_POINT obj) {
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
/* 32 */         ltkrnJNI.delete_L_POINT(this.swigCPtr);
/*    */       }
/* 34 */       this.swigCPtr = 0L;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setX(int value) {
/* 39 */     ltkrnJNI.L_POINT_x_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getX() {
/* 43 */     return ltkrnJNI.L_POINT_x_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setY(int value) {
/* 47 */     ltkrnJNI.L_POINT_y_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getY() {
/* 51 */     return ltkrnJNI.L_POINT_y_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public L_POINT() {
/* 55 */     this(ltkrnJNI.new_L_POINT(), true);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.L_POINT
 * JD-Core Version:    0.6.2
 */