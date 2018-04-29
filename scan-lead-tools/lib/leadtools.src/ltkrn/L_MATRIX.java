/*     */ package ltkrn;
/*     */ 
/*     */ public class L_MATRIX
/*     */ {
/*     */   private long swigCPtr;
/*     */   protected boolean swigCMemOwn;
/*     */ 
/*     */   public L_MATRIX(long cPtr, boolean cMemoryOwn)
/*     */   {
/*  16 */     this.swigCMemOwn = cMemoryOwn;
/*  17 */     this.swigCPtr = cPtr;
/*     */   }
/*     */ 
/*     */   public static long getCPtr(L_MATRIX obj) {
/*  21 */     return obj == null ? 0L : obj.swigCPtr;
/*     */   }
/*     */ 
/*     */   protected void finalize() {
/*  25 */     delete();
/*     */   }
/*     */ 
/*     */   public synchronized void delete() {
/*  29 */     if (this.swigCPtr != 0L) {
/*  30 */       if (this.swigCMemOwn) {
/*  31 */         this.swigCMemOwn = false;
/*  32 */         ltkrnJNI.delete_L_MATRIX(this.swigCPtr);
/*     */       }
/*  34 */       this.swigCPtr = 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setM11(double value) {
/*  39 */     ltkrnJNI.L_MATRIX_m11_set(this.swigCPtr, this, value);
/*     */   }
/*     */ 
/*     */   public double getM11() {
/*  43 */     return ltkrnJNI.L_MATRIX_m11_get(this.swigCPtr, this);
/*     */   }
/*     */ 
/*     */   public void setM12(double value) {
/*  47 */     ltkrnJNI.L_MATRIX_m12_set(this.swigCPtr, this, value);
/*     */   }
/*     */ 
/*     */   public double getM12() {
/*  51 */     return ltkrnJNI.L_MATRIX_m12_get(this.swigCPtr, this);
/*     */   }
/*     */ 
/*     */   public void setM21(double value) {
/*  55 */     ltkrnJNI.L_MATRIX_m21_set(this.swigCPtr, this, value);
/*     */   }
/*     */ 
/*     */   public double getM21() {
/*  59 */     return ltkrnJNI.L_MATRIX_m21_get(this.swigCPtr, this);
/*     */   }
/*     */ 
/*     */   public void setM22(double value) {
/*  63 */     ltkrnJNI.L_MATRIX_m22_set(this.swigCPtr, this, value);
/*     */   }
/*     */ 
/*     */   public double getM22() {
/*  67 */     return ltkrnJNI.L_MATRIX_m22_get(this.swigCPtr, this);
/*     */   }
/*     */ 
/*     */   public void setOffsetX(double value) {
/*  71 */     ltkrnJNI.L_MATRIX_offsetX_set(this.swigCPtr, this, value);
/*     */   }
/*     */ 
/*     */   public double getOffsetX() {
/*  75 */     return ltkrnJNI.L_MATRIX_offsetX_get(this.swigCPtr, this);
/*     */   }
/*     */ 
/*     */   public void setOffsetY(double value) {
/*  79 */     ltkrnJNI.L_MATRIX_offsetY_set(this.swigCPtr, this, value);
/*     */   }
/*     */ 
/*     */   public double getOffsetY() {
/*  83 */     return ltkrnJNI.L_MATRIX_offsetY_get(this.swigCPtr, this);
/*     */   }
/*     */ 
/*     */   public void setType(int value) {
/*  87 */     ltkrnJNI.L_MATRIX_type_set(this.swigCPtr, this, value);
/*     */   }
/*     */ 
/*     */   public int getType() {
/*  91 */     return ltkrnJNI.L_MATRIX_type_get(this.swigCPtr, this);
/*     */   }
/*     */ 
/*     */   public void setPadding(int value) {
/*  95 */     ltkrnJNI.L_MATRIX_padding_set(this.swigCPtr, this, value);
/*     */   }
/*     */ 
/*     */   public int getPadding() {
/*  99 */     return ltkrnJNI.L_MATRIX_padding_get(this.swigCPtr, this);
/*     */   }
/*     */ 
/*     */   public L_MATRIX() {
/* 103 */     this(ltkrnJNI.new_L_MATRIX(), true);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.L_MATRIX
 * JD-Core Version:    0.6.2
 */