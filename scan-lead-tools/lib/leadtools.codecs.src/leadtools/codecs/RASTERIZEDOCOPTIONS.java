/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ class RASTERIZEDOCOPTIONS
/*    */ {
/*    */   public int uStructSize;
/* 22 */   public double dPageWidth = 8.5D;
/*    */ 
/* 24 */   public double dPageHeight = 11.0D;
/*    */   public double dLeftMargin;
/*    */   public double dTopMargin;
/*    */   public double dRightMargin;
/*    */   public double dBottomMargin;
/* 32 */   public RASTERIZEDOC_UNIT uUnit = RASTERIZEDOC_UNIT.RASTERIZEDOC_UNIT_INCH;
/*    */   public int uXResolution;
/*    */   public int uYResolution;
/* 37 */   public RASTERIZEDOC_SIZEMODE uSizeMode = RASTERIZEDOC_SIZEMODE.RASTERIZEDOC_SIZEMODE_NONE;
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.RASTERIZEDOCOPTIONS
 * JD-Core Version:    0.6.2
 */