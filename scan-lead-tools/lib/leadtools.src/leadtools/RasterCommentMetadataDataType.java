/*    */ package leadtools;
/*    */ 
/*    */ public enum RasterCommentMetadataDataType
/*    */ {
/*  5 */   ASCII, 
/*  6 */   URATIONAL, 
/*  7 */   RATIONAL, 
/*  8 */   BYTE, 
/*  9 */   INT_16;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 13 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static RasterCommentMetadataDataType forValue(int value)
/*    */   {
/* 18 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterCommentMetadataDataType
 * JD-Core Version:    0.6.2
 */