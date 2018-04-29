/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsFaxImageInfo
/*    */ {
/*    */   private FILEINFO _fileInfo;
/*    */ 
/*    */   CodecsFaxImageInfo(FILEINFO fileInfo)
/*    */   {
/*  9 */     this._fileInfo = fileInfo;
/*    */   }
/*    */ 
/*    */   public boolean isCompressed()
/*    */   {
/* 14 */     return Tools.isFlagged(this._fileInfo.Flags, 64);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsFaxImageInfo
 * JD-Core Version:    0.6.2
 */