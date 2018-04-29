/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsDecompressDataFlags
/*    */ {
/*  5 */   NONE(0), 
/*  6 */   START(1), 
/*  7 */   END(2), 
/*  8 */   COMPLETE(3);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsDecompressDataFlags> mappings;
/*    */ 
/* 14 */   private static HashMap<Integer, CodecsDecompressDataFlags> getMappings() { if (mappings == null)
/*    */     {
/* 16 */       synchronized (CodecsDecompressDataFlags.class)
/*    */       {
/* 18 */         if (mappings == null)
/*    */         {
/* 20 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 24 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsDecompressDataFlags(int value)
/*    */   {
/* 29 */     this.intValue = value;
/* 30 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 35 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsDecompressDataFlags forValue(int value)
/*    */   {
/* 40 */     return (CodecsDecompressDataFlags)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsDecompressDataFlags
 * JD-Core Version:    0.6.2
 */