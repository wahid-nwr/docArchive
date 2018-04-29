/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.L_ERROR;
/*    */ import leadtools.RasterException;
/*    */ import leadtools.RasterExceptionCode;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterNativeBuffer;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class CodecsExtensionList
/*    */ {
/*    */   private CodecsExtension[] _extensions;
/*    */   private int _uFlags;
/*    */ 
/*    */   public int getFlags()
/*    */   {
/* 21 */     return this._uFlags;
/*    */   }
/*    */ 
/*    */   public CodecsExtension[] getExtensions()
/*    */   {
/* 26 */     if (this._extensions != null) {
/* 27 */       return (CodecsExtension[])this._extensions.clone();
/*    */     }
/* 29 */     return null;
/*    */   }
/*    */ 
/*    */   public RasterImage createStamp()
/*    */   {
/* 34 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/* 35 */     if (bitmapHandle == 0L) {
/* 36 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*    */     }
/*    */     try
/*    */     {
/* 40 */       int ret = ltfil.LoadExtensionStamp(this, bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf());
/* 41 */       RasterException.checkErrorCode(ret);
/* 42 */       return RasterImage.createFromBitmapHandle(bitmapHandle);
/*    */     }
/*    */     finally
/*    */     {
/* 46 */       ltkrn.FreeBitmapHandle(bitmapHandle);
/*    */     }
/*    */   }
/*    */ 
/*    */   public RasterNativeBuffer getAudioData(int stream) {
/* 51 */     byte[][] buffer = new byte[1][];
/* 52 */     int[] size = new int[1];
/* 53 */     int ret = ltfil.GetExtensionAudio(this, stream, buffer, size);
/*    */ 
/* 58 */     if (ret == L_ERROR.ERROR_AUDIO_MISSING.getValue()) {
/* 59 */       return RasterNativeBuffer.getEmpty();
/*    */     }
/* 61 */     RasterException.checkErrorCode(ret);
/* 62 */     return new RasterNativeBuffer(buffer[0], size[0]);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsExtensionList
 * JD-Core Version:    0.6.2
 */