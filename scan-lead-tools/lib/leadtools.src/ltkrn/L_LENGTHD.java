/*    */ package ltkrn;
/*    */ 
/*    */ public class L_LENGTHD
/*    */ {
/*    */   private long swigCPtr;
/*    */   protected boolean swigCMemOwn;
/*    */ 
/*    */   public L_LENGTHD(long cPtr, boolean cMemoryOwn)
/*    */   {
/* 16 */     this.swigCMemOwn = cMemoryOwn;
/* 17 */     this.swigCPtr = cPtr;
/*    */   }
/*    */ 
/*    */   public static long getCPtr(L_LENGTHD obj) {
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
/* 32 */         ltkrnJNI.delete_L_LENGTHD(this.swigCPtr);
/*    */       }
/* 34 */       this.swigCPtr = 0L;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setValue(double value) {
/* 39 */     ltkrnJNI.L_LENGTHD_value_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public double getValue() {
/* 43 */     return ltkrnJNI.L_LENGTHD_value_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public L_LENGTHD() {
/* 47 */     this(ltkrnJNI.new_L_LENGTHD(), true);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.L_LENGTHD
 * JD-Core Version:    0.6.2
 */