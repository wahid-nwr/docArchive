/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum CodecsCodecLoadMode
/*    */ {
/*  5 */   DYNAMIC, 
/*  6 */   FIXED, 
/*  7 */   IGNORED;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 11 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static CodecsCodecLoadMode forValue(int value)
/*    */   {
/* 16 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsCodecLoadMode
 * JD-Core Version:    0.6.2
 */