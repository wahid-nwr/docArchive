/*    */ package leadtools;
/*    */ 
/*    */ public class ltkrn
/*    */ {
/* 14 */   public static final int SCREEN_DPIX = GetScreenDpiX();
/* 15 */   public static final int SCREEN_DPIY = GetScreenDpiY();
/*    */ 
/*    */   public static native int Initialize(Object paramObject);
/*    */ 
/*    */   public static native String GetLibName(int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native String GetLibFullPath(String paramString);
/*    */ 
/*    */   public static native String GetResourceDirectory(int paramInt);
/*    */ 
/*    */   public static native int SetResourceDirectory(int paramInt, String paramString);
/*    */ 
/*    */   public static native int GetVersion(int[] paramArrayOfInt);
/*    */ 
/*    */   public static native int ResetLicense();
/*    */ 
/*    */   public static native int SetLicenseFile(String paramString1, String paramString2);
/*    */ 
/*    */   public static native int SetLicense(String paramString1, String paramString2);
/*    */ 
/*    */   public static native void UnlockSupport(int paramInt, String paramString);
/*    */ 
/*    */   public static native boolean IsSupportLocked(int paramInt);
/*    */ 
/*    */   public static native int GetKernelType();
/*    */ 
/*    */   public static native boolean HasExpired();
/*    */ 
/*    */   public static native Object setstatuscallback(Object paramObject);
/*    */ 
/*    */   public static native boolean IsNewLinkImage(long paramLong);
/*    */ 
/*    */   private static native int GetScreenDpiX();
/*    */ 
/*    */   private static native int GetScreenDpiY();
/*    */ 
/*    */   public static native long AllocBitmapHandle();
/*    */ 
/*    */   public static native int FreeBitmapHandle(long paramLong);
/*    */ 
/*    */   public static native int FreeBitmap(long paramLong);
/*    */ 
/*    */   public static native int MarkAsFreed(long paramLong);
/*    */ 
/*    */   public static native int CopyBitmap(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int CopyBitmapData(long paramLong1, long paramLong2);
/*    */ 
/*    */   public static native int CopyBitmap2(long paramLong1, long paramLong2, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native int CopyBitmapRect(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
/*    */ 
/*    */   public static native int CopyBitmapRect2(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*    */ 
/*    */   public static native int TrimBitmap(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*    */ 
/*    */   public static native int CombineBitmapKrn(long paramLong1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong2, int paramInt5, int paramInt6, int paramInt7);
/*    */ 
/*    */   public static native int CreateBitmap(long paramLong1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, RasterColor[] paramArrayOfRasterColor, int paramInt7, byte[] paramArrayOfByte, long paramLong2);
/*    */ 
/*    */   public static native int CreateGrayScaleBitmap(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native long CreateBitmapList();
/*    */ 
/*    */   public static native int DestroyBitmapList(long paramLong);
/*    */ 
/*    */   public static native int GetBitmapListCount(long paramLong, int[] paramArrayOfInt);
/*    */ 
/*    */   public static native int GetBitmapListItem(long paramLong1, int paramInt1, long paramLong2, int paramInt2);
/*    */ 
/*    */   public static native int SetBitmapListItem(long paramLong1, int paramInt, long paramLong2);
/*    */ 
/*    */   public static native int InsertBitmapListItem(long paramLong1, int paramInt, long paramLong2);
/*    */ 
/*    */   public static native int RemoveBitmapListItem(long paramLong, int paramInt, long[] paramArrayOfLong);
/*    */ 
/*    */   public static native int ReplaceBitmapListItem(long paramLong1, int paramInt1, long paramLong2, long paramLong3, int paramInt2);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getLockCount(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getSizeOf();
/*    */ 
/*    */   public static native int BITMAPHANDLE_getXResolution(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getYResolution(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setXResolution(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setYResolution(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getBitsPerPixel(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getBytesPerLine(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getWidth(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getHeight(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getColors(long paramLong);
/*    */ 
/*    */   public static native RasterColor[] BITMAPHANDLE_getPalette(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getViewPerspective(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setViewPerspective(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getOrder(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setOrder(long paramLong, int paramInt);
/*    */ 
/*    */   public static native RasterColor[] BITMAPHANDLE_getLUT(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getLUTLength(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setLUT(long paramLong, RasterColor[] paramArrayOfRasterColor, int paramInt);
/*    */ 
/*    */   public static native RasterColor16[] BITMAPHANDLE_getLUT16(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getLUTLength16(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setLUT16(long paramLong, RasterColor16[] paramArrayOfRasterColor16, int paramInt);
/*    */ 
/*    */   public static native byte[] BITMAPHANDLE_getPaintLUT(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setPaintLUT(long paramLong, byte[] paramArrayOfByte);
/*    */ 
/*    */   public static native short[] BITMAPHANDLE_getPaintLUT16(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setPaintLUT16(long paramLong, short[] paramArrayOfShort);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getBackground(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setBackground(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getDisposalMethod(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getLowBit(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getHighBit(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setLowBit(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setHighBit(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getPaintLowBit(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getPaintHighBit(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setPaintLowBit(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setPaintHighBit(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getMinValue(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getMaxValue(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setMinValue(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setMaxValue(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getPaintGamma(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getPaintContrast(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getPaintIntensity(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getTransparency(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setTransparency(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getDitheringMethod(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setDitheringMethod(long paramLong, int paramInt);
/*    */ 
/*    */   public static native long BITMAPHANDLE_getSize64(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getOriginalFormat(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setOriginalFormat(long paramLong, int paramInt);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_hasDitherData(long paramLong);
/*    */ 
/*    */   public static native long BITMAPHANDLE_getCurrentPos(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getCurrentRow(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getLeft(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getTop(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getDelay(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getMinVal(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getMaxVal(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getReserved(long paramLong);
/*    */ 
/*    */   public static native long BITMAPHANDLE_getRgnInfo(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_getFlagsValue(long paramLong, int[] paramArrayOfInt);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsAllocated(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsConventionalMemory(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsCompressed(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsDiskMemory(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsTiled(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsSuperCompressed(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsGlobal(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsTransparency(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsSigned(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsMirror(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsUseLUT(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsUsePaintLUT(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsNoClip(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsProgressiveAvailable(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsInterlaced(long paramLong);
/*    */ 
/*    */   public static native boolean BITMAPHANDLE_getFlagsWaitUserInput(long paramLong);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setFlagsTransparency(long paramLong, boolean paramBoolean);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setFlagsSigned(long paramLong, boolean paramBoolean);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setFlagsUseLUT(long paramLong, boolean paramBoolean);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setFlagsUsePaintLUT(long paramLong, boolean paramBoolean);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setFlagsNoClip(long paramLong, boolean paramBoolean);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setCurrentPos(long paramLong1, long paramLong2);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setCurrentRow(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setFlagsProgressiveAvailable(long paramLong, boolean paramBoolean);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setFlagsInterlaced(long paramLong, boolean paramBoolean);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setFlagsWaitUserInput(long paramLong, boolean paramBoolean);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setLeft(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setTop(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setDelay(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setDisposalMethod(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setMinVal(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setMaxVal(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setPaintGamma(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setPaintContrast(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setPaintIntensity(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setReserved(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int BITMAPHANDLE_setRgnInfo(long paramLong1, long paramLong2);
/*    */ 
/*    */   public static native int SetPaintGamma(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int SetPaintContrast(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int SetPaintIntensity(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int SetBitmapAlphaValues(long paramLong, short paramShort);
/*    */ 
/*    */   public static native long GetBitmapRow(long paramLong1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong2);
/*    */ 
/*    */   public static native long PutBitmapRow(long paramLong1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong2);
/*    */ 
/*    */   public static native long GetBitmapRowCol(long paramLong1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, long paramLong2);
/*    */ 
/*    */   public static native long PutBitmapRowCol(long paramLong1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, long paramLong2);
/*    */ 
/*    */   public static native int PutBitmapRowSegments(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt, int paramInt4, int paramInt5, boolean paramBoolean);
/*    */ 
/*    */   public static native long GetBitmapRowColCompressed(long paramLong, byte[] paramArrayOfByte, short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*    */ 
/*    */   public static native long PutBitmapRowColCompressed(long paramLong, byte[] paramArrayOfByte, short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*    */ 
/*    */   public static native int GetBitmapRowCompressed(long paramLong, byte[] paramArrayOfByte, short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native int PutBitmapRowCompressed(long paramLong, byte[] paramArrayOfByte, short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native int FillBitmap(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int ClearBitmap(long paramLong);
/*    */ 
/*    */   public static native int ClearNegativePixels(long paramLong);
/*    */ 
/*    */   public static native int GetBitmapColors(long paramLong, int paramInt1, int paramInt2, RasterColor[] paramArrayOfRasterColor);
/*    */ 
/*    */   public static native int PutBitmapColors(long paramLong, int paramInt1, int paramInt2, RasterColor[] paramArrayOfRasterColor);
/*    */ 
/*    */   public static native int IsGrayScaleBitmap(long paramLong);
/*    */ 
/*    */   public static native int BitmapHasMeaningfulAlpha(long paramLong);
/*    */ 
/*    */   public static native int AccessBitmap(long paramLong);
/*    */ 
/*    */   public static native int ReleaseBitmap(long paramLong);
/*    */ 
/*    */   public static native int ChangeBitmapCompression(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int GetBitmapAlpha(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int SetBitmapAlpha(long paramLong1, long paramLong2);
/*    */ 
/*    */   public static native int TranslateBitmapColor(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int ChangeBitmapHeight(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int CopyBitmapPalette(long paramLong1, long paramLong2);
/*    */ 
/*    */   public static native int GetPixelColor(long paramLong, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native int PutPixelColor(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native int IntGetPixelColor(long paramLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte);
/*    */ 
/*    */   public static native int IntPutPixelColor(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte);
/*    */ 
/*    */   public static native int GetPixelData(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native int PutPixelData(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native int SetBitmapDataPointer(long paramLong1, byte[] paramArrayOfByte, int paramInt, long paramLong2);
/*    */ 
/*    */   public static native int ReverseBitmapViewPerspective(long paramLong);
/*    */ 
/*    */   public static native int FlipBitmapViewPerspective(long paramLong);
/*    */ 
/*    */   public static native int RotateBitmapViewPerspective(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int ChangeBitmapViewPerspective(long paramLong1, long paramLong2, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native int PointFromBitmap(long paramLong, int paramInt, LeadPoint paramLeadPoint);
/*    */ 
/*    */   public static native int PointToBitmap(long paramLong, int paramInt, LeadPoint paramLeadPoint);
/*    */ 
/*    */   public static native int RectFromBitmap(long paramLong, int paramInt, LeadRect paramLeadRect);
/*    */ 
/*    */   public static native int RectToBitmap(long paramLong, int paramInt, LeadRect paramLeadRect);
/*    */ 
/*    */   public static native int StartDithering(long paramLong, RasterColor[] paramArrayOfRasterColor, int paramInt);
/*    */ 
/*    */   public static native int DitherLine(long paramLong, byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2);
/*    */ 
/*    */   public static native int StopDithering(long paramLong);
/*    */ 
/*    */   public static native int WindowLevel(long paramLong, int paramInt1, int paramInt2, RasterColor[] paramArrayOfRasterColor, int paramInt3, int paramInt4);
/*    */ 
/*    */   public static native int WindowLevelExt(long paramLong, int paramInt1, int paramInt2, RasterColor16[] paramArrayOfRasterColor16, int paramInt3, int paramInt4);
/*    */ 
/*    */   public static native int GetBitmapClipSegmentsMax(long paramLong, int[] paramArrayOfInt);
/*    */ 
/*    */   public static native int GetBitmapClipSegments(long paramLong, int paramInt1, int[] paramArrayOfInt1, int paramInt2, int[] paramArrayOfInt2);
/*    */ 
/*    */   public static native int GetBitmapRgnData(long paramLong, RasterRegionXForm paramRasterRegionXForm, int paramInt, byte[] paramArrayOfByte);
/*    */ 
/*    */   public static native int GetBitmapRgnArea(long paramLong, long[] paramArrayOfLong);
/*    */ 
/*    */   public static native int GetBitmapRgnBounds(long paramLong, RasterRegionXForm paramRasterRegionXForm, LeadRect paramLeadRect);
/*    */ 
/*    */   public static native int GetBitmapRgnBoundsClip(long paramLong, RasterRegionXForm paramRasterRegionXForm, LeadRect paramLeadRect);
/*    */ 
/*    */   public static native boolean IsPtInBitmapRgn(long paramLong, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native int OffsetBitmapRgn(long paramLong, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native int FlipBitmapRgn(long paramLong);
/*    */ 
/*    */   public static native int ReverseBitmapRgn(long paramLong);
/*    */ 
/*    */   public static native int SetBitmapRgn(long paramLong1, RasterRegionXForm paramRasterRegionXForm, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int SetBitmapRgnHandle(long paramLong1, RasterRegionXForm paramRasterRegionXForm, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int SetBitmapRgnRect(long paramLong, RasterRegionXForm paramRasterRegionXForm, LeadRect paramLeadRect, int paramInt);
/*    */ 
/*    */   public static native int SetBitmapRgnColor(long paramLong, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native int SetBitmapRgnColorRGBRange(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native int SetBitmapRgnColorHSVRange(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native int CreateMaskFromBitmapRgn(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int SetBitmapRgnFromMask(long paramLong1, RasterRegionXForm paramRasterRegionXForm, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int SetBitmapRgnMagicWand(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
/*    */ 
/*    */   public static native int SetBitmapRgnBorder(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*    */ 
/*    */   public static native int CurveToBezier(RasterCurve paramRasterCurve, int[] paramArrayOfInt, LeadPoint[][] paramArrayOfLeadPoint);
/*    */ 
/*    */   public static native int RegionToSegments(long paramLong, int[][] paramArrayOfInt);
/*    */ 
/*    */   public static native int GetBitmapMemoryInfo(long paramLong, int[] paramArrayOfInt);
/*    */ 
/*    */   public static native int SetBitmapMemoryInfo(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
/*    */ 
/*    */   public static native int CalculatePaintModeRectangle(int paramInt1, int paramInt2, LeadRect paramLeadRect, int paramInt3, int paramInt4, int paramInt5);
/*    */ 
/*    */   public static native int GetOverlayBitmap(long paramLong1, int paramInt1, long paramLong2, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native int AllocateBitmap(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int InitBitmap(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*    */ 
/*    */   public static native int SetOverlayBitmap(long paramLong1, int paramInt1, long paramLong2, int paramInt2);
/*    */ 
/*    */   public static native int GetOverlayCount(long paramLong, int[] paramArrayOfInt, int paramInt);
/*    */ 
/*    */   public static native int UpdateBitmapOverlayBits(long paramLong, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native int GetOverlayAttributes(long paramLong, int paramInt1, RasterOverlayAttributes paramRasterOverlayAttributes, int paramInt2);
/*    */ 
/*    */   public static native int SetOverlayAttributes(long paramLong, int paramInt1, RasterOverlayAttributes paramRasterOverlayAttributes, int paramInt2);
/*    */ 
/*    */   public static native int SetBitmapRgnData(long paramLong, RasterRegionXForm paramRasterRegionXForm, int paramInt1, byte[] paramArrayOfByte, int paramInt2);
/*    */ 
/*    */   public static native int ColorResBitmap(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, RasterColor[] paramArrayOfRasterColor, long paramLong3, int paramInt4, Object paramObject);
/*    */ 
/*    */   public static native int ColorResBitmapList(long paramLong1, int paramInt1, int paramInt2, RasterColor[] paramArrayOfRasterColor, long paramLong2, int paramInt3);
/*    */ 
/*    */   public static native int GrayScaleBitmap(long paramLong, int paramInt);
/*    */ 
/*    */   public static native int UnderlayBitmap(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int ReverseBitmap(long paramLong);
/*    */ 
/*    */   public static native int FlipBitmap(long paramLong);
/*    */ 
/*    */   public static native int ResizeBitmap(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int SizeBitmap(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native int RotateBitmap(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*    */ 
/*    */   public static native int ShearBitmap(long paramLong, int paramInt1, boolean paramBoolean, int paramInt2);
/*    */ 
/*    */   public static native int ScrambleBitmap(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*    */ 
/*    */   public static native int RGBtoHSV(int paramInt);
/*    */ 
/*    */   public static native int HSVtoRGB(int paramInt);
/*    */ 
/*    */   public static native boolean BitmapHasRgn(long paramLong);
/*    */ 
/*    */   public static native long CreateLeadRectRgn(LeadRect paramLeadRect);
/*    */ 
/*    */   public static native long CopyLeadRgn(long paramLong);
/*    */ 
/*    */   public static native int DeleteLeadRgn(long paramLong);
/*    */ 
/*    */   public static native int GetLeadRgnBounds(long paramLong, LeadRect paramLeadRect);
/*    */ 
/*    */   public static native int CombineLeadRgnRect(long[] paramArrayOfLong, LeadRect paramLeadRect, int paramInt);
/*    */ 
/*    */   public static native int CombineLeadRgn(long[] paramArrayOfLong, int paramInt);
/*    */ 
/*    */   public static native boolean IsPtInRgn(long paramLong, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native int ClipLeadRgnRect(long paramLong, LeadRect paramLeadRect);
/*    */ 
/*    */   public static native boolean IsLeadRgnEmpty(long paramLong);
/*    */ 
/*    */   public static native int TransformLeadRgn(long paramLong, RasterRegionXForm paramRasterRegionXForm);
/*    */ 
/*    */   public static native byte[] GetLeadRgnData(long paramLong);
/*    */ 
/*    */   public static native int SetLeadRgnData(long[] paramArrayOfLong, byte[] paramArrayOfByte);
/*    */ 
/*    */   public static native int getLeadRgn(long paramLong);
/*    */ 
/*    */   public static native RasterRegion getRasterRegion(long paramLong);
/*    */ 
/*    */   public static native long SetFilePointer(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int ReadFile(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native int WriteFile(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native long GetFileSize(long paramLong);
/*    */ 
/*    */   public static native boolean SetEndOfFile(long paramLong);
/*    */ 
/*    */   public static native boolean memcpy(long paramLong1, long paramLong2, int paramInt);
/*    */ 
/*    */   public static native int memset(long paramLong, int paramInt1, int paramInt2);
/*    */ 
/*    */   public static native int ConvertBufferExt(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, RasterColor[] paramArrayOfRasterColor1, RasterColor[] paramArrayOfRasterColor2, int paramInt6, int paramInt7, int paramInt8);
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.ltkrn
 * JD-Core Version:    0.6.2
 */