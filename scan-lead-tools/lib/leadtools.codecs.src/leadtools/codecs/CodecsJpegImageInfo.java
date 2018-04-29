/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsJpegImageInfo
/*    */ {
/*    */   private FILEINFO _fileInfo;
/*    */ 
/*    */   CodecsJpegImageInfo(FILEINFO fileInfo)
/*    */   {
/*  9 */     this._fileInfo = fileInfo;
/*    */   }
/*    */ 
/*    */   public boolean isProgressive()
/*    */   {
/* 14 */     return Tools.isFlagged(this._fileInfo.Flags, 2);
/*    */   }
/*    */ 
/*    */   public boolean isLossless()
/*    */   {
/* 19 */     return Tools.isFlagged(this._fileInfo.Flags, 1024);
/*    */   }
/*    */ 
/*    */   public boolean hasStamp()
/*    */   {
/* 24 */     return Tools.isFlagged(this._fileInfo.Flags, 4);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpegImageInfo
 * JD-Core Version:    0.6.2
 */