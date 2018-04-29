/*    */ package ltkrn;
/*    */ 
/*    */ public class L_RECT
/*    */ {
/*    */   private long swigCPtr;
/*    */   protected boolean swigCMemOwn;
/*    */ 
/*    */   public L_RECT(long cPtr, boolean cMemoryOwn)
/*    */   {
/* 16 */     this.swigCMemOwn = cMemoryOwn;
/* 17 */     this.swigCPtr = cPtr;
/*    */   }
/*    */ 
/*    */   public static long getCPtr(L_RECT obj) {
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
/* 32 */         ltkrnJNI.delete_L_RECT(this.swigCPtr);
/*    */       }
/* 34 */       this.swigCPtr = 0L;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setLeft(int value) {
/* 39 */     ltkrnJNI.L_RECT_left_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getLeft() {
/* 43 */     return ltkrnJNI.L_RECT_left_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setTop(int value) {
/* 47 */     ltkrnJNI.L_RECT_top_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getTop() {
/* 51 */     return ltkrnJNI.L_RECT_top_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setRight(int value) {
/* 55 */     ltkrnJNI.L_RECT_right_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getRight() {
/* 59 */     return ltkrnJNI.L_RECT_right_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setBottom(int value) {
/* 63 */     ltkrnJNI.L_RECT_bottom_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getBottom() {
/* 67 */     return ltkrnJNI.L_RECT_bottom_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public L_RECT() {
/* 71 */     this(ltkrnJNI.new_L_RECT(), true);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.L_RECT
 * JD-Core Version:    0.6.2
 */