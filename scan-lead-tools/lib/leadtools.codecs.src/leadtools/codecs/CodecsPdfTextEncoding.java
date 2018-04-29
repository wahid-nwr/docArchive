/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum CodecsPdfTextEncoding
/*    */ {
/*  5 */   NONE, 
/*  6 */   BASE85, 
/*  7 */   HEX;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 11 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static CodecsPdfTextEncoding forValue(int value)
/*    */   {
/* 16 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPdfTextEncoding
 * JD-Core Version:    0.6.2
 */