/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum CodecsPageEventState
/*    */ {
/*  5 */   BEFORE, 
/*  6 */   AFTER;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 10 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static CodecsPageEventState forValue(int value)
/*    */   {
/* 15 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPageEventState
 * JD-Core Version:    0.6.2
 */