/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum CodecsVffView
/*    */ {
/*  4 */   VFF_VIEW_UPTODOWN, 
/*  5 */   VFF_VIEW_DOWNTOUP, 
/*  6 */   VFF_VIEW_LEFTTORIGHT, 
/*  7 */   VFF_VIEW_RIGHTTOLEFT, 
/*  8 */   VFF_VIEW_FRONTTOREAR, 
/*  9 */   VFF_VIEW_REARTOFRONT;
/*    */ 
/*    */   public int getValue() {
/* 12 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static CodecsVffView forValue(int value) {
/* 16 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsVffView
 * JD-Core Version:    0.6.2
 */