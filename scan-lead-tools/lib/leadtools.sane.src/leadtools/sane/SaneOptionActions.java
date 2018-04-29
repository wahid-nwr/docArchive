/*     */ package leadtools.sane;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ 
/*     */  enum SaneOptionActions
/*     */ {
/*     */   private int L;
/*     */   private static HashMap<Integer, SaneOptionActions> D;
/*     */ 
/*     */   public static SaneOptionActions forValue(int value)
/*     */   {
/* 102 */     return (SaneOptionActions)j().get(Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   private static HashMap<Integer, SaneOptionActions> j()
/*     */   {
/*  34 */     if (D == null)
/*     */     {
/* 133 */       synchronized (SaneOptionActions.class)
/*     */       {
/* 135 */         if (D == null)
/*     */         {
/* 155 */           D = new HashMap();
/*     */         }
/*     */       }
/*     */     }
/* 154 */     return D;
/*     */   }
/*     */ 
/*     */   private SaneOptionActions(int value)
/*     */   {
/*  96 */     this.L = value;
/*     */ 
/* 114 */     j().put(Integer.valueOf(value), this);
/*     */   }
/*     */ 
/*     */   public int getValue()
/*     */   {
/* 136 */     return this.L;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     int tmp7_6 = 0; GET = new SaneOptionActions("GET", tmp7_6, tmp7_6);
/*     */     int tmp21_20 = 1; SET = new SaneOptionActions("SET", tmp21_20, tmp21_20);
/*     */     int tmp35_34 = 2;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneOptionActions
 * JD-Core Version:    0.6.2
 */