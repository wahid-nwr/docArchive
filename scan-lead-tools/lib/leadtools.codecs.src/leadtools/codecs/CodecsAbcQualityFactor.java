/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsAbcQualityFactor
/*    */ {
/* 11 */   LOSSLESS(0), 
/*    */ 
/* 16 */   VIRTUALLOSSLESS(1), 
/*    */ 
/* 20 */   REMOVEBORDER(2), 
/*    */ 
/* 24 */   ENHANCE(3), 
/*    */ 
/* 29 */   MODIFIED1(4), 
/*    */ 
/* 31 */   MODIFIED1_FAST(5), 
/*    */ 
/* 35 */   MODIFIED2(6), 
/*    */ 
/* 37 */   MODIFIED2_FAST(7), 
/*    */ 
/* 41 */   MODIFIED3(8), 
/*    */ 
/* 43 */   MODIFIED3_FAST(9), 
/*    */ 
/* 49 */   LOSSLESS_FAST(10), 
/*    */ 
/* 52 */   LOSSY_FAST(11);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsAbcQualityFactor> mappings;
/*    */ 
/* 58 */   private static HashMap<Integer, CodecsAbcQualityFactor> getMappings() { if (mappings == null)
/*    */     {
/* 60 */       synchronized (CodecsAbcQualityFactor.class)
/*    */       {
/* 62 */         if (mappings == null)
/*    */         {
/* 64 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 68 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsAbcQualityFactor(int value)
/*    */   {
/* 73 */     this.intValue = value;
/* 74 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 79 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsAbcQualityFactor forValue(int value)
/*    */   {
/* 84 */     return (CodecsAbcQualityFactor)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsAbcQualityFactor
 * JD-Core Version:    0.6.2
 */