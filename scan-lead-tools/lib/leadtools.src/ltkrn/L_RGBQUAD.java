/*    */ package ltkrn;
/*    */ 
/*    */ public class L_RGBQUAD
/*    */ {
/*    */   private long swigCPtr;
/*    */   protected boolean swigCMemOwn;
/*    */ 
/*    */   public L_RGBQUAD(long cPtr, boolean cMemoryOwn)
/*    */   {
/* 16 */     this.swigCMemOwn = cMemoryOwn;
/* 17 */     this.swigCPtr = cPtr;
/*    */   }
/*    */ 
/*    */   public static long getCPtr(L_RGBQUAD obj) {
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
/* 32 */         ltkrnJNI.delete_L_RGBQUAD(this.swigCPtr);
/*    */       }
/* 34 */       this.swigCPtr = 0L;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setRgbBlue(short value) {
/* 39 */     ltkrnJNI.L_RGBQUAD_rgbBlue_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public short getRgbBlue() {
/* 43 */     return ltkrnJNI.L_RGBQUAD_rgbBlue_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setRgbGreen(short value) {
/* 47 */     ltkrnJNI.L_RGBQUAD_rgbGreen_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public short getRgbGreen() {
/* 51 */     return ltkrnJNI.L_RGBQUAD_rgbGreen_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setRgbRed(short value) {
/* 55 */     ltkrnJNI.L_RGBQUAD_rgbRed_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public short getRgbRed() {
/* 59 */     return ltkrnJNI.L_RGBQUAD_rgbRed_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public void setRgbReserved(short value) {
/* 63 */     ltkrnJNI.L_RGBQUAD_rgbReserved_set(this.swigCPtr, this, value);
/*    */   }
/*    */ 
/*    */   public short getRgbReserved() {
/* 67 */     return ltkrnJNI.L_RGBQUAD_rgbReserved_get(this.swigCPtr, this);
/*    */   }
/*    */ 
/*    */   public L_RGBQUAD() {
/* 71 */     this(ltkrnJNI.new_L_RGBQUAD(), true);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.L_RGBQUAD
 * JD-Core Version:    0.6.2
 */