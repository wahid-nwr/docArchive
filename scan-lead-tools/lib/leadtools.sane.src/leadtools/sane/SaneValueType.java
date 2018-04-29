/*     */ package leadtools.sane;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public enum SaneValueType
/*     */ {
/*     */   private static HashMap<Integer, SaneValueType> L;
/*     */   private int I;
/*     */ 
/*     */   public static SaneValueType forValue(int value)
/*     */   {
/*  82 */     return (SaneValueType)j().get(Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   private static HashMap<Integer, SaneValueType> j()
/*     */   {
/* 155 */     if (L == null)
/*     */     {
/* 193 */       synchronized (SaneValueType.class)
/*     */       {
/*  37 */         if (L == null)
/*     */         {
/* 100 */           L = new HashMap();
/*     */         }
/*     */       }
/*     */     }
/*  96 */     return L;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     int tmp7_6 = 0; BOOL = new SaneValueType("BOOL", tmp7_6, tmp7_6);
/*     */     int tmp21_20 = 1; INT = new SaneValueType("INT", tmp21_20, tmp21_20);
/*     */     int tmp35_34 = 2; FIXED = new SaneValueType("FIXED", tmp35_34, tmp35_34);
/*     */     int tmp49_48 = 3; STRING = new SaneValueType("STRING", tmp49_48, tmp49_48);
/*     */     int tmp63_62 = 4; BUTTON = new SaneValueType("BUTTON", tmp63_62, tmp63_62);
/*     */     int tmp77_76 = 5; GROUP = new SaneValueType("GROUP", tmp77_76, tmp77_76);
/*     */   }
/*     */ 
/*     */   private SaneValueType(int value)
/*     */   {
/* 116 */     this.I = value;
/*     */ 
/*  54 */     j().put(Integer.valueOf(value), this);
/*     */   }
/*     */ 
/*     */   public int getValue()
/*     */   {
/*  89 */     return this.I;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneValueType
 * JD-Core Version:    0.6.2
 */