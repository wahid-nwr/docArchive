/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsDocumentImageInfo
/*    */ {
/*    */   private FILEINFO _fileInfo;
/*    */ 
/*    */   CodecsDocumentImageInfo(FILEINFO fileInfo)
/*    */   {
/*  8 */     this._fileInfo = fileInfo;
/*    */   }
/*    */ 
/*    */   public boolean isDocumentFile() {
/* 12 */     return this._fileInfo.bIsDocFile;
/*    */   }
/*    */ 
/*    */   public double getPageWidth() {
/* 16 */     return this._fileInfo.dDocPageWidth;
/*    */   }
/*    */ 
/*    */   public double getPageHeight() {
/* 20 */     return this._fileInfo.dDocPageHeight;
/*    */   }
/*    */ 
/*    */   public CodecsRasterizeDocumentUnit getUnit() {
/* 24 */     return CodecsRasterizeDocumentUnit.forValue(this._fileInfo.uDocUnit.getValue());
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsDocumentImageInfo
 * JD-Core Version:    0.6.2
 */