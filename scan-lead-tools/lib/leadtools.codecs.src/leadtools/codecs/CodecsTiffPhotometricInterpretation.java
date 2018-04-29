/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsTiffPhotometricInterpretation
/*    */ {
/*  5 */   MINIMUM_IS_WHITE(0), 
/*  6 */   MINIMUM_IS_BLACK(1), 
/*  7 */   RGB(2), 
/*  8 */   PALETTE(3), 
/*  9 */   MASK(4), 
/* 10 */   SEPARATED(5), 
/* 11 */   YCBCR(6), 
/* 12 */   CIELAB(8);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsTiffPhotometricInterpretation> mappings;
/*    */ 
/* 18 */   private static HashMap<Integer, CodecsTiffPhotometricInterpretation> getMappings() { if (mappings == null)
/*    */     {
/* 20 */       synchronized (CodecsTiffPhotometricInterpretation.class)
/*    */       {
/* 22 */         if (mappings == null)
/*    */         {
/* 24 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 28 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsTiffPhotometricInterpretation(int value)
/*    */   {
/* 33 */     this.intValue = value;
/* 34 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 39 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsTiffPhotometricInterpretation forValue(int value)
/*    */   {
/* 44 */     return (CodecsTiffPhotometricInterpretation)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsTiffPhotometricInterpretation
 * JD-Core Version:    0.6.2
 */