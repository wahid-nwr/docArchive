/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ public enum ColorResolutionCommandMode
/*    */ {
/*  5 */   IN_PLACE, 
/*  6 */   CREATE_DESTINATION_IMAGE, 
/*  7 */   ONLY_DATA, 
/*  8 */   ALL_PAGES;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 12 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static ColorResolutionCommandMode forValue(int value)
/*    */   {
/* 17 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.ColorResolutionCommandMode
 * JD-Core Version:    0.6.2
 */