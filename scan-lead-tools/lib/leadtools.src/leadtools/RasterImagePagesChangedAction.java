/*    */ package leadtools;
/*    */ 
/*    */ public enum RasterImagePagesChangedAction
/*    */ {
/*  5 */   ADDED, 
/*  6 */   REMOVED, 
/*  7 */   INSERTED;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 11 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static RasterImagePagesChangedAction forValue(int value)
/*    */   {
/* 16 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImagePagesChangedAction
 * JD-Core Version:    0.6.2
 */