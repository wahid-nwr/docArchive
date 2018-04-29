/*    */ package ltkrn;
/*    */ 
/*    */ public class L_SIZED
/*    */ {
/*    */   private long swigCPtr;
/*    */   protected boolean swigCMemOwn;
/*    */ 
/*    */   public L_SIZED(long cPtr, boolean cMemoryOwn)
/*    */   {
/* 16 */     this.swigCMemOwn = cMemoryOwn;
/* 17 */     this.swigCPtr = cPtr;
/*    */   }
/*    */ 
/*    */   public static long getCPtr(L_SIZED obj) {
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
/* 32 */         ltkrnJNI.delete_L_SIZED(this.swigCPtr);
/*    */       }
/* 34 */       this.swigCPtr = 0L;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setWidth(double value) {
/* 39 */     ltkrnJNI.L_SIZED_width_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public double getWidth() {
/* 43 */     return ltkrnJNI.L_SIZED_width_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setHeight(double value) {
/* 47 */     ltkrnJNI.L_SIZED_height_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public double getHeight() {
/* 51 */     return ltkrnJNI.L_SIZED_height_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public L_SIZED() {
/* 55 */     this(ltkrnJNI.new_L_SIZED(), true);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.L_SIZED
 * JD-Core Version:    0.6.2
 */