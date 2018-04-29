/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsPsdImageInfo
/*    */ {
/*    */   private FILEINFO _fileInfo;
/*    */ 
/*    */   CodecsPsdImageInfo(FILEINFO fileInfo)
/*    */   {
/*  8 */     this._fileInfo = fileInfo;
/*    */   }
/*    */ 
/*    */   public int getLayers() {
/* 12 */     return this._fileInfo.Layers;
/*    */   }
/*    */ 
/*    */   public int getChannels() {
/* 16 */     return this._fileInfo.Channels;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPsdImageInfo
 * JD-Core Version:    0.6.2
 */