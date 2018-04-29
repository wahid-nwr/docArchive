/*    */ package ltkrn;
/*    */ 
/*    */ public class L_RGBQUAD16
/*    */ {
/*    */   private long swigCPtr;
/*    */   protected boolean swigCMemOwn;
/*    */ 
/*    */   public L_RGBQUAD16(long cPtr, boolean cMemoryOwn)
/*    */   {
/* 16 */     this.swigCMemOwn = cMemoryOwn;
/* 17 */     this.swigCPtr = cPtr;
/*    */   }
/*    */ 
/*    */   public static long getCPtr(L_RGBQUAD16 obj) {
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
/* 32 */         ltkrnJNI.delete_L_RGBQUAD16(this.swigCPtr);
/*    */       }
/* 34 */       this.swigCPtr = 0L;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setRgbBlue(int value) {
/* 39 */     ltkrnJNI.L_RGBQUAD16_rgbBlue_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getRgbBlue() {
/* 43 */     return ltkrnJNI.L_RGBQUAD16_rgbBlue_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setRgbGreen(int value) {
/* 47 */     ltkrnJNI.L_RGBQUAD16_rgbGreen_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getRgbGreen() {
/* 51 */     return ltkrnJNI.L_RGBQUAD16_rgbGreen_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setRgbRed(int value) {
/* 55 */     ltkrnJNI.L_RGBQUAD16_rgbRed_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getRgbRed() {
/* 59 */     return ltkrnJNI.L_RGBQUAD16_rgbRed_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setRgbReserved(int value) {
/* 63 */     ltkrnJNI.L_RGBQUAD16_rgbReserved_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public int getRgbReserved() {
/* 67 */     return ltkrnJNI.L_RGBQUAD16_rgbReserved_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public L_RGBQUAD16() {
/* 71 */     this(ltkrnJNI.new_L_RGBQUAD16(), true);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.L_RGBQUAD16
 * JD-Core Version:    0.6.2
 */