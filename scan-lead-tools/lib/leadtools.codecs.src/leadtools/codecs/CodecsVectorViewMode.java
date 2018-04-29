/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum CodecsVectorViewMode
/*    */ {
/*  4 */   UseBest, 
/*  5 */   UseWidthHeight, 
/*  6 */   UseWidth, 
/*  7 */   UseHeight;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 11 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static CodecsVectorViewMode forValue(int value)
/*    */   {
/* 16 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsVectorViewMode
 * JD-Core Version:    0.6.2
 */