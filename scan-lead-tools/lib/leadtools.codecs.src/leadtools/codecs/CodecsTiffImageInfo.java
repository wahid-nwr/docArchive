/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsTiffImageInfo
/*    */ {
/*    */   private FILEINFO _fileInfo;
/*    */ 
/*    */   CodecsTiffImageInfo(FILEINFO fileInfo)
/*    */   {
/*  9 */     this._fileInfo = fileInfo;
/*    */   }
/*    */ 
/*    */   public boolean hasNoPalette()
/*    */   {
/* 14 */     return Tools.isFlagged(this._fileInfo.Flags, 128);
/*    */   }
/*    */ 
/*    */   public boolean isImageFileDirectoryOffsetValid()
/*    */   {
/* 19 */     return Tools.isFlagged(this._fileInfo.Flags, 32768);
/*    */   }
/*    */ 
/*    */   public long getImageFileDirectoryOffset()
/*    */   {
/* 24 */     return this._fileInfo.IFD;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsTiffImageInfo
 * JD-Core Version:    0.6.2
 */