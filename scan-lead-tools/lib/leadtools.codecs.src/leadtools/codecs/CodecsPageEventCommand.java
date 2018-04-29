/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum CodecsPageEventCommand
/*    */ {
/*  5 */   CONTINUE, 
/*  6 */   SKIP, 
/*  7 */   STOP;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 11 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static CodecsPageEventCommand forValue(int value)
/*    */   {
/* 16 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPageEventCommand
 * JD-Core Version:    0.6.2
 */