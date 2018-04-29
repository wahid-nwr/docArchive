package leadtools.codecs;

import leadtools.LeadPoint;

class ltfil
{
  public static native int InitThreadData(THREADDATA paramTHREADDATA);

  public static native int SetThreadData(THREADDATA paramTHREADDATA);

  public static native int GetDefaultLoadFileOption(LOADFILEOPTION paramLOADFILEOPTION);

  public static native int GetDefaultSaveFileOption(SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int GetSaveResolution(int[] paramArrayOfInt, DIMENSION[][] paramArrayOfDIMENSION);

  public static native int SetSaveResolution(int paramInt, DIMENSION[] paramArrayOfDIMENSION);

  public static native int SetLoadResolution(int paramInt1, int paramInt2, int paramInt3);

  public static native int GetLoadResolution(int paramInt, int[] paramArrayOfInt, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int intSetLoadResolution(int paramInt1, int paramInt2, int paramInt3, THREADLOADSETTINGS paramTHREADLOADSETTINGS);

  public static native int GetDefaultJ2KOptions(FILEJ2KOPTIONS paramFILEJ2KOPTIONS);

  public static native boolean EnableFastFileInfo(boolean paramBoolean);

  public static native int GetPreLoadFilters(String[] paramArrayOfString, int[] paramArrayOfInt);

  public static native int PreLoadFilters(int paramInt1, int paramInt2, String paramString);

  public static native int IgnoreFilters(String paramString);

  public static native int GetIgnoreFilters(String[] paramArrayOfString);

  public static native int GetFilterInfo(String paramString, CodecsCodecInformation paramCodecsCodecInformation);

  public static native int SetFilterInfo(CodecsCodecInformation[] paramArrayOfCodecsCodecInformation, int paramInt1, int paramInt2);

  public static native int GetFilterListInfo(CodecsCodecInformation[][] paramArrayOfCodecsCodecInformation, int[] paramArrayOfInt);

  public static native long StartRedirecting(Object paramObject);

  public static native int StopRedirecting(long paramLong);

  public static native long RedirectedOpen(String paramString, int paramInt);

  public static native int RedirectedRead(long paramLong, byte[] paramArrayOfByte, int paramInt);

  public static native int RedirectedWrite(long paramLong, byte[] paramArrayOfByte, int paramInt);

  public static native int RedirectedSeek(long paramLong1, long paramLong2, int paramInt);

  public static native int RedirectedClose(long paramLong);

  public static native Object SetLoadInfoCallback(Object paramObject);

  public static native int FileInfo(String paramString, FILEINFO paramFILEINFO, int paramInt, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int StartFeedLoad(long paramLong, int paramInt1, int paramInt2, int paramInt3, Object paramObject, long[] paramArrayOfLong, LOADFILEOPTION paramLOADFILEOPTION, FILEINFO paramFILEINFO);

  public static native int FeedLoad(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public static native int StopFeedLoad(long paramLong);

  public static native int StartFeedInfo(long[] paramArrayOfLong, FILEINFO paramFILEINFO, int paramInt, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int FeedInfo(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public static native int StopFeedInfo(long paramLong);

  public static native int SetTag(short paramShort1, short paramShort2, int paramInt, byte[] paramArrayOfByte);

  public static native int SetGeoKey(short paramShort, int paramInt1, int paramInt2, byte[] paramArrayOfByte);

  public static native int SetComment(int paramInt1, int paramInt2, byte[] paramArrayOfByte);

  public static native int CreateMarkers(long[] paramArrayOfLong);

  public static native int InsertMarker(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte);

  public static native int SetMarkers(long paramLong, int paramInt);

  public static native int FreeMarkers(long paramLong);

  public static native int LoadMarkers(String paramString, long[] paramArrayOfLong, int paramInt);

  public static native int EnumMarkers(long paramLong, int paramInt, Object paramObject);

  public static native int DecodeABIC(byte[] paramArrayOfByte, int paramInt1, byte[][] paramArrayOfByte1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

  public static native int EncodeABIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, byte[][] paramArrayOfByte1, int[] paramArrayOfInt, boolean paramBoolean);

  public static native int LoadExtensionStamp(CodecsExtensionList paramCodecsExtensionList, long paramLong, int paramInt);

  public static native int GetExtensionAudio(CodecsExtensionList paramCodecsExtensionList, int paramInt, byte[][] paramArrayOfByte, int[] paramArrayOfInt);

  public static native int ReadFileExtensions(String paramString, CodecsExtensionList paramCodecsExtensionList, LOADFILEOPTION paramLOADFILEOPTION);

  public static native long StartRedirectIO(Object paramObject);

  public static native void StopRedirectIO(long paramLong);

  public static native int GetRasterPdfInfo(String paramString, int paramInt, CodecsRasterPdfInfo paramCodecsRasterPdfInfo);

  public static native int LoadFile(String paramString, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, LOADFILEOPTION paramLOADFILEOPTION, FILEINFO paramFILEINFO);

  public static native int LoadFileTile(String paramString, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Object paramObject, LOADFILEOPTION paramLOADFILEOPTION, FILEINFO paramFILEINFO);

  public static native int LoadBitmapResize(String paramString, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, LOADFILEOPTION paramLOADFILEOPTION, FILEINFO paramFILEINFO);

  public static native int LoadFileOffset(Object paramObject1, long paramLong1, long paramLong2, long paramLong3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject2, LOADFILEOPTION paramLOADFILEOPTION, FILEINFO paramFILEINFO);

  public static native int WriteFileStamp(String paramString, long paramLong, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int SaveFile(String paramString, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int SaveFileOffset(Object paramObject1, long paramLong1, long[] paramArrayOfLong, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject2, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int DeletePage(String paramString, int paramInt1, int paramInt2, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int ReadLoadResolutions(String paramString, LeadPoint[][] paramArrayOfLeadPoint, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int FileConvert(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, LOADFILEOPTION paramLOADFILEOPTION, SAVEFILEOPTION paramSAVEFILEOPTION, FILEINFO paramFILEINFO);

  public static native int CreateThumbnailFromFile(String paramString, long paramLong, int paramInt, CodecsThumbnailOptions paramCodecsThumbnailOptions, Object paramObject, LOADFILEOPTION paramLOADFILEOPTION, FILEINFO paramFILEINFO);

  public static native int ReadFileStamp(String paramString, long paramLong, int paramInt, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int SetOverlayCallback(Object[] paramArrayOfObject, int[] paramArrayOfInt, boolean paramBoolean);

  public static native int EnumFileTags(String paramString, int paramInt, Object paramObject, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int EnumFileGeoKeys(String paramString, int paramInt, Object paramObject, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int DeleteTag(String paramString, int paramInt1, int paramInt2, int paramInt3, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int ReadFileTag(String paramString, short paramShort, int[] paramArrayOfInt, byte[][] paramArrayOfByte, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int ReadFileTags(String paramString, int paramInt, int[] paramArrayOfInt, LEADFILETAG[][] paramArrayOfLEADFILETAG, long[] paramArrayOfLong, byte[][] paramArrayOfByte, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int ReadFileGeoKey(String paramString, short paramShort, int[] paramArrayOfInt, byte[][] paramArrayOfByte, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int ReadFileGeoKeys(String paramString, int paramInt, int[] paramArrayOfInt, LEADFILETAG[][] paramArrayOfLEADFILETAG, long[] paramArrayOfLong, byte[][] paramArrayOfByte, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int ReadFileComment(String paramString, short paramShort, int[] paramArrayOfInt, byte[][] paramArrayOfByte, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int ReadFileComments(String paramString, int paramInt, int[] paramArrayOfInt, LEADFILECOMMENT[][] paramArrayOfLEADFILECOMMENT, byte[][] paramArrayOfByte, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int WriteFileTag(String paramString, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int WriteFileGeoKey(String paramString, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int WriteFileComment(String paramString, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int WriteFileMetaData(String paramString, int paramInt, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int TransformFile(String paramString1, String paramString2, int paramInt, Object paramObject, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int CallLEADTransformMarkerCallback(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, long paramLong1, long paramLong2);

  public static native int StartCompressBuffer(long[] paramArrayOfLong, long paramLong1, Object paramObject, long paramLong2, long paramLong3, byte[] paramArrayOfByte, int paramInt1, int paramInt2, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int CompressBuffer(byte[] paramArrayOfByte, int paramInt);

  public static native int EndCompressBuffer(long paramLong);

  public static native int StartDecompressBuffer(long[] paramArrayOfLong, CodecsStartDecompressOptions paramCodecsStartDecompressOptions);

  public static native int DecompressBuffer(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);

  public static native int StopDecompressBuffer(long paramLong);

  public static native int CompactFile(String paramString1, String paramString2, int paramInt, LOADFILEOPTION paramLOADFILEOPTION, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native int GetLoadStatus();

  public static native int LoadFileCMYKArray(String paramString, long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3, Object paramObject, LOADFILEOPTION paramLOADFILEOPTION, FILEINFO paramFILEINFO);

  public static native int SaveFileCMYKArray(String paramString, long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Object paramObject, SAVEFILEOPTION paramSAVEFILEOPTION);

  public static native boolean TagsSupported(int paramInt);

  public static native boolean GeoKeysSupported(int paramInt);

  public static native boolean CommentsSupported(int paramInt);

  public static native boolean MarkersSupported(int paramInt);

  public static native int GetPNGTRNS(byte[] paramArrayOfByte);

  public static native int CanLoadSvg(String paramString, boolean[] paramArrayOfBoolean, LOADFILEOPTION paramLOADFILEOPTION);

  public static native int LoadSvg(String paramString, LOADSVGOPTIONS paramLOADSVGOPTIONS, LOADFILEOPTION paramLOADFILEOPTION);

  public static native boolean FormatSupportsMultipageSave(int paramInt);

  public static native String GetFormatMimeType(int paramInt);

  public static native String GetFormatFileExtension(int paramInt);

  public static native int IsTiffFormat(int paramInt);

  public static native int CopyLoadFileOption(LOADFILEOPTION paramLOADFILEOPTION1, LOADFILEOPTION paramLOADFILEOPTION2);

  public static native int CopySaveFileOption(SAVEFILEOPTION paramSAVEFILEOPTION1, SAVEFILEOPTION paramSAVEFILEOPTION2);

  public static native int CopyThreadData(THREADDATA paramTHREADDATA1, THREADDATA paramTHREADDATA2);

  public static native int FreeSvg(long paramLong);
}

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.ltfil
 * JD-Core Version:    0.6.2
 */