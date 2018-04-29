/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum CodecsSavePageMode
/*    */ {
/*  5 */   APPEND, 
/*  6 */   INSERT, 
/*  7 */   REPLACE, 
/*  8 */   OVERWRITE;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 12 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static CodecsSavePageMode forValue(int value)
/*    */   {
/* 17 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsSavePageMode
 * JD-Core Version:    0.6.2
 */