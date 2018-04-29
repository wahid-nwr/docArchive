/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum RASTERIZEDOC_UNIT
/*    */ {
/*  5 */   RASTERIZEDOC_UNIT_PIXEL, 
/*    */ 
/*  7 */   RASTERIZEDOC_UNIT_INCH, 
/*    */ 
/*  9 */   RASTERIZEDOC_UNIT_MILLIMETER;
/*    */ 
/*    */   public int getValue() {
/* 12 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static RASTERIZEDOC_UNIT forValue(int value) {
/* 16 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.RASTERIZEDOC_UNIT
 * JD-Core Version:    0.6.2
 */