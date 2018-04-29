/*     */ package leadtools.sane;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public enum SaneConstraintType
/*     */ {
/*     */   private int L;
/*     */   private static HashMap<Integer, SaneConstraintType> I;
/*     */ 
/*     */   public static SaneConstraintType forValue(int value)
/*     */   {
/*  14 */     return (SaneConstraintType)j().get(Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   private static HashMap<Integer, SaneConstraintType> j()
/*     */   {
/* 133 */     if (I == null)
/*     */     {
/* 135 */       synchronized (SaneConstraintType.class)
/*     */       {
/* 155 */         if (I == null)
/*     */         {
/* 193 */           I = new HashMap();
/*     */         }
/*     */       }
/*     */     }
/*  32 */     return I;
/*     */   }
/*     */ 
/*     */   private SaneConstraintType(int value)
/*     */   {
/* 199 */     this.L = value;
/*     */ 
/* 116 */     j().put(Integer.valueOf(value), this);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     int tmp7_6 = 0; NONE = new SaneConstraintType("NONE", tmp7_6, tmp7_6);
/*     */     int tmp21_20 = 1; RANGE = new SaneConstraintType("RANGE", tmp21_20, tmp21_20);
/*     */     int tmp35_34 = 2; WORD_LIST = new SaneConstraintType("WORD_LIST", tmp35_34, tmp35_34);
/*     */     int tmp49_48 = 3;
/*     */   }
/*     */ 
/*     */   public int getValue()
/*     */   {
/* 110 */     return this.L;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneConstraintType
 * JD-Core Version:    0.6.2
 */