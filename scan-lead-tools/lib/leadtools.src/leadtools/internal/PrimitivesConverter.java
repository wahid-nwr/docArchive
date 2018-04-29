/*     */ package leadtools.internal;
/*     */ 
/*     */ import leadtools.LeadLengthD;
/*     */ import leadtools.LeadMatrix;
/*     */ import leadtools.LeadPoint;
/*     */ import leadtools.LeadPointD;
/*     */ import leadtools.LeadRect;
/*     */ import leadtools.LeadRectD;
/*     */ import leadtools.LeadSize;
/*     */ import leadtools.LeadSizeD;
/*     */ import ltkrn.L_LENGTHD;
/*     */ import ltkrn.L_MATRIX;
/*     */ import ltkrn.L_POINT;
/*     */ import ltkrn.L_POINTD;
/*     */ import ltkrn.L_RECT;
/*     */ import ltkrn.L_RECTD;
/*     */ import ltkrn.L_SIZE;
/*     */ import ltkrn.L_SIZED;
/*     */ import ltkrn.ltkrn;
/*     */ 
/*     */ public class PrimitivesConverter
/*     */ {
/*     */   public static LeadLengthD convert(L_LENGTHD length)
/*     */   {
/*  23 */     return new LeadLengthD(length.getValue());
/*     */   }
/*     */ 
/*     */   public static L_LENGTHD convert(LeadLengthD length) {
/*  27 */     L_LENGTHD ret = new L_LENGTHD();
/*  28 */     ret.setValue(length.getValue());
/*  29 */     return ret;
/*     */   }
/*     */ 
/*     */   public static LeadMatrix convert(L_MATRIX matrix) {
/*  33 */     return new LeadMatrix(matrix.getM11(), matrix.getM12(), matrix.getM21(), matrix.getM22(), matrix.getOffsetX(), matrix.getOffsetY());
/*     */   }
/*     */ 
/*     */   public static L_MATRIX convert(LeadMatrix matrix) {
/*  37 */     L_MATRIX ret = new L_MATRIX();
/*  38 */     ltkrn.L_Matrix_Set(ret, matrix.getM11(), matrix.getM12(), matrix.getM21(), matrix.getM22(), matrix.getOffsetX(), matrix.getOffsetY());
/*  39 */     return ret;
/*     */   }
/*     */ 
/*     */   public static LeadPoint convert(L_POINT pt) {
/*  43 */     return new LeadPoint(pt.getX(), pt.getY());
/*     */   }
/*     */ 
/*     */   public static L_POINT convert(LeadPoint pt) {
/*  47 */     L_POINT ret = new L_POINT();
/*  48 */     ltkrn.L_Point_Make(ret, pt.getX(), pt.getY());
/*  49 */     return ret;
/*     */   }
/*     */ 
/*     */   public static LeadPointD convert(L_POINTD pt) {
/*  53 */     return new LeadPointD(pt.getX(), pt.getY());
/*     */   }
/*     */ 
/*     */   public static L_POINTD convert(LeadPointD pt) {
/*  57 */     L_POINTD ret = new L_POINTD();
/*  58 */     ltkrn.L_PointD_Make(ret, pt.getX(), pt.getY());
/*  59 */     return ret;
/*     */   }
/*     */ 
/*     */   public static LeadPointD[] convert(L_POINTD[] pts) {
/*  63 */     LeadPointD[] ret = new LeadPointD[pts.length];
/*  64 */     for (int i = 0; i < pts.length; i++) {
/*  65 */       ret[i] = convert(pts[i]);
/*     */     }
/*  67 */     return ret;
/*     */   }
/*     */ 
/*     */   public static L_POINTD[] convert(LeadPointD[] pts) {
/*  71 */     L_POINTD[] ret = new L_POINTD[pts.length];
/*  72 */     for (int i = 0; i < pts.length; i++) {
/*  73 */       ret[i] = convert(pts[i]);
/*     */     }
/*  75 */     return ret;
/*     */   }
/*     */ 
/*     */   public static LeadRect convert(L_RECT rc) {
/*  79 */     return LeadRect.fromLTRB(rc.getLeft(), rc.getTop(), rc.getRight(), rc.getBottom());
/*     */   }
/*     */ 
/*     */   public static L_RECT convert(LeadRect rc) {
/*  83 */     L_RECT ret = new L_RECT();
/*  84 */     ltkrn.L_Rect_Make(ret, rc.getX(), rc.getY(), rc.getWidth(), rc.getHeight());
/*  85 */     return ret;
/*     */   }
/*     */ 
/*     */   public static LeadRectD convert(L_RECTD rc) {
/*  89 */     return new LeadRectD(rc.getX(), rc.getY(), rc.getWidth(), rc.getHeight());
/*     */   }
/*     */ 
/*     */   public static L_RECTD convert(LeadRectD rc) {
/*  93 */     L_RECTD ret = new L_RECTD();
/*  94 */     ltkrn.L_RectD_Make(ret, rc.getX(), rc.getY(), rc.getWidth(), rc.getHeight());
/*  95 */     return ret;
/*     */   }
/*     */ 
/*     */   public static LeadSize convert(L_SIZE size) {
/*  99 */     return new LeadSize(size.getCx(), size.getCy());
/*     */   }
/*     */ 
/*     */   public static L_SIZE convert(LeadSize size) {
/* 103 */     L_SIZE ret = new L_SIZE();
/* 104 */     ltkrn.L_Size_Make(ret, size.getWidth(), size.getHeight());
/* 105 */     return ret;
/*     */   }
/*     */ 
/*     */   public static LeadSizeD convert(L_SIZED size) {
/* 109 */     return new LeadSizeD(size.getWidth(), size.getWidth());
/*     */   }
/*     */ 
/*     */   public static L_SIZED convert(LeadSizeD size) {
/* 113 */     L_SIZED ret = new L_SIZED();
/* 114 */     ltkrn.L_SizeD_Make(ret, size.getWidth(), size.getHeight());
/* 115 */     return ret;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.PrimitivesConverter
 * JD-Core Version:    0.6.2
 */