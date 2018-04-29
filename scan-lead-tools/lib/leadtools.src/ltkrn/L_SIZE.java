/*    */ package ltkrn;
/*    */ 
/*    */ public class L_SIZE
/*    */ {
/*    */   private long swigCPtr;
/*    */   protected boolean swigCMemOwn;
/*    */ 
/*    */   public L_SIZE(long cPtr, boolean cMemoryOwn)
/*    */   {
/* 16 */     this.swigCMemOwn = cMemoryOwn;
/* 17 */     this.swigCPtr = cPtr;
/*    */   }
/*    */ 
/*    */   public static long getCPtr(L_SIZE obj) {
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
/* 32 */         ltkrnJNI.delete_L_SIZE(this.swigCPtr);
/*    */       }
/* 34 */       this.swigCPtr = 0L;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setCx(int value) {
/* 39 */     ltkrnJNI.L_SIZE_cx_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getCx() {
/* 43 */     return ltkrnJNI.L_SIZE_cx_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setCy(int value) {
/* 47 */     ltkrnJNI.L_SIZE_cy_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getCy() {
/* 51 */     return ltkrnJNI.L_SIZE_cy_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public L_SIZE() {
/* 55 */     this(ltkrnJNI.new_L_SIZE(), true);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.L_SIZE
 * JD-Core Version:    0.6.2
 */