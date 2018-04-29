/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum CodecsStartDecompressDataType
/*    */ {
/*  5 */   STRIPS, 
/*  6 */   TILES;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 10 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static CodecsStartDecompressDataType forValue(int value)
/*    */   {
/* 15 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsStartDecompressDataType
 * JD-Core Version:    0.6.2
 */