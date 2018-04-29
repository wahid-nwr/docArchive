/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum RASTERPDFVERSION
/*    */ {
/*  5 */   RASTERPDFVERSION_V12, 
/*  6 */   RASTERPDFVERSION_V14, 
/*  7 */   RASTERPDFVERSION_PDFA, 
/*  8 */   RASTERPDFVERSION_V15, 
/*  9 */   RASTERPDFVERSION_V16, 
/* 10 */   RASTERPDFVERSION_V13;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 14 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static RASTERPDFVERSION forValue(int value)
/*    */   {
/* 19 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.RASTERPDFVERSION
 * JD-Core Version:    0.6.2
 */