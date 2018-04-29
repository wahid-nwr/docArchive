/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.LeadRect;
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ class FILEJ2KOPTIONS
/*    */ {
/*    */   public int uStructSize;
/*    */   public boolean bUseColorTransform;
/*    */   public boolean bDerivedQuantization;
/* 14 */   public CodecsJpeg2000CompressionControl uCompressionControl = CodecsJpeg2000CompressionControl.LOSSLESS;
/*    */   public float fCompressionRatio;
/*    */   public long uTargetFileSize;
/*    */   public int uXOsiz;
/*    */   public int uYOsiz;
/*    */   public int uXTsiz;
/*    */   public int uYTsiz;
/*    */   public int uXTOsiz;
/*    */   public int uYTOsiz;
/* 25 */   public int[] uXRsiz = new int[3];
/* 26 */   public int[] uYRsiz = new int[3];
/*    */   public int uDecompLevel;
/*    */   public int uProgressOrder;
/*    */   public int nCodBlockWidth;
/*    */   public int nCodBlockHeight;
/* 33 */   public CodBlockStyle CodBlockStyleFlags = new CodBlockStyle();
/*    */   public int uGuardBits;
/*    */   public int nDerivedBaseMantissa;
/*    */   public int nDerivedBaseExponent;
/*    */   public boolean bUseSOPMarker;
/*    */   public boolean bUseEPHMarker;
/* 45 */   public CodecsJpeg2000RegionOfInterest uROIControl = CodecsJpeg2000RegionOfInterest.USE_LEAD_REGION;
/*    */   public boolean bUseROI;
/*    */   public float fROIWeight;
/* 48 */   public LeadRect rcROI = new LeadRect(0, 0, 0, 0);
/*    */   public boolean bAlphaChannelLossless;
/*    */   public int uAlphaChannelActiveBits;
/* 54 */   public CodecsJpeg2000PrecinctSize uPrecinctSize = CodecsJpeg2000PrecinctSize.FULL;
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.FILEJ2KOPTIONS
 * JD-Core Version:    0.6.2
 */