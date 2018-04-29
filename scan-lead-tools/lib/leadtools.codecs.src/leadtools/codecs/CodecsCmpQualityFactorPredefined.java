/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsCmpQualityFactorPredefined
/*    */ {
/*  5 */   PERFECT_QUALITY1(-1), 
/*  6 */   PERFECT_QUALITY2(-2), 
/*  7 */   SUPER_QUALITY(-3), 
/*  8 */   QUALITY(-4), 
/*  9 */   QUALITY_AND_SIZE(-5), 
/* 10 */   SHARP(-6), 
/* 11 */   LESS_TILING(-7), 
/* 12 */   MAXIMUM_QUALITY(-8), 
/* 13 */   MAXIMUM_COMPRESSION(-9), 
/* 14 */   CUSTOM(-10);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsCmpQualityFactorPredefined> mappings;
/*    */ 
/* 20 */   private static HashMap<Integer, CodecsCmpQualityFactorPredefined> getMappings() { if (mappings == null)
/*    */     {
/* 22 */       synchronized (CodecsCmpQualityFactorPredefined.class)
/*    */       {
/* 24 */         if (mappings == null)
/*    */         {
/* 26 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 30 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsCmpQualityFactorPredefined(int value)
/*    */   {
/* 35 */     this.intValue = value;
/* 36 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 41 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsCmpQualityFactorPredefined forValue(int value)
/*    */   {
/* 46 */     return (CodecsCmpQualityFactorPredefined)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsCmpQualityFactorPredefined
 * JD-Core Version:    0.6.2
 */