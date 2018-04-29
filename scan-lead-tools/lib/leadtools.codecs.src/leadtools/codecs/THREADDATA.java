/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ class THREADDATA
/*    */ {
/*    */   public int uSaveResolutionCount;
/* 12 */   public THREADHEADER pThreadHeader = new THREADHEADER();
/* 13 */   public FILEJ2KOPTIONS J2KOptions = new FILEJ2KOPTIONS();
/*    */   public long pCompBuffData;
/* 15 */   public DIMENSION[] pSaveResolution = { null };
/*    */   public long hMarkers;
/* 17 */   public FILETIFOPTIONS TIFOptions = new FILETIFOPTIONS();
/* 18 */   public FILECOMMENTSTRINGS[] aComments = new FILECOMMENTSTRINGS['ø'];
/* 19 */   public PRIVATETAG pPrivateTags = new PRIVATETAG();
/* 20 */   public PRIVATETAG pGeoKeys = new PRIVATETAG();
/*    */   public int uPNGTRNSSize;
/* 22 */   public byte[] aPNGTRNSData = new byte[256];
/*    */   public String szLinkFilename;
/*    */   public String szPDFInitDir;
/* 28 */   public THREADLOADSETTINGS pThreadLoadSettings = new THREADLOADSETTINGS();
/*    */   public int nFilterType;
/*    */   public boolean bFastFileInfo;
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.THREADDATA
 * JD-Core Version:    0.6.2
 */