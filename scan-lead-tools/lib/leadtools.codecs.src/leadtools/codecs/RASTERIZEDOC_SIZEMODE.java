/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum RASTERIZEDOC_SIZEMODE
/*    */ {
/*  6 */   RASTERIZEDOC_SIZEMODE_NONE, 
/*    */ 
/*  8 */   RASTERIZEDOC_SIZEMODE_FIT, 
/*    */ 
/* 10 */   RASTERIZEDOC_SIZEMODE_FIT_ALWAYS, 
/*    */ 
/* 12 */   RASTERIZEDOC_SIZEMODE_FIT_WIDTH, 
/*    */ 
/* 14 */   RASTERIZEDOC_SIZEMODE_STRETCH;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 18 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static RASTERIZEDOC_SIZEMODE forValue(int value)
/*    */   {
/* 23 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.RASTERIZEDOC_SIZEMODE
 * JD-Core Version:    0.6.2
 */