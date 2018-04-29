/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsPstImageInfo
/*    */ {
/*    */   private FILEINFO _fileInfo;
/*    */ 
/*    */   CodecsPstImageInfo(FILEINFO fileInfo)
/*    */   {
/*  8 */     this._fileInfo = fileInfo;
/*    */   }
/*    */ 
/*    */   public int getMessageCount() {
/* 12 */     return this._fileInfo.MessageCount;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPstImageInfo
 * JD-Core Version:    0.6.2
 */