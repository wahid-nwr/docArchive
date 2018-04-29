/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsJpeg2000PrecinctSize
/*    */ {
/*  5 */   FULL(0), 
/*  6 */   UNIFORM_64(1), 
/*  7 */   UNIFORM_128(2), 
/*  8 */   UNIFORM_256(3), 
/*  9 */   UNIFORM_512(4), 
/* 10 */   UNIFORM_1024(5), 
/* 11 */   UNIFORM_2048(6), 
/* 12 */   HIERARCHICAL_ONE_64(7), 
/* 13 */   HIERARCHICAL_ONE1_28(8), 
/* 14 */   HIERARCHICAL_ONE_256(9), 
/* 15 */   HIERARCHICAL_ONE_512(10), 
/* 16 */   HIERARCHICAL_TWO_64(11), 
/* 17 */   HIERARCHICAL_TWO_128(12), 
/* 18 */   HIERARCHICAL_TWO_256(13), 
/* 19 */   HIERARCHICAL_TWO_512(14);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsJpeg2000PrecinctSize> mappings;
/*    */ 
/* 25 */   private static HashMap<Integer, CodecsJpeg2000PrecinctSize> getMappings() { if (mappings == null)
/*    */     {
/* 27 */       synchronized (CodecsJpeg2000PrecinctSize.class)
/*    */       {
/* 29 */         if (mappings == null)
/*    */         {
/* 31 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 35 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsJpeg2000PrecinctSize(int value)
/*    */   {
/* 40 */     this.intValue = value;
/* 41 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 46 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsJpeg2000PrecinctSize forValue(int value)
/*    */   {
/* 51 */     return (CodecsJpeg2000PrecinctSize)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpeg2000PrecinctSize
 * JD-Core Version:    0.6.2
 */