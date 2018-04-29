/*    */ package leadtools.sane.server;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import leadtools.RasterImageFormat;
/*    */ 
/*    */ class SaveData
/*    */ {
/*    */   private String _ext;
/*    */   private RasterImageFormat _format;
/*    */   private ArrayList<String> _filesList;
/*    */ 
/*    */   public SaveData()
/*    */   {
/* 14 */     setExt(".png");
/* 15 */     setFormat(RasterImageFormat.PNG);
/* 16 */     setFilesList(new ArrayList());
/*    */   }
/*    */ 
/*    */   public String getExt()
/*    */   {
/* 21 */     return this._ext;
/*    */   }
/*    */   protected void setExt(String ext) {
/* 24 */     this._ext = ext;
/*    */   }
/*    */ 
/*    */   public RasterImageFormat getFormat()
/*    */   {
/* 29 */     return this._format;
/*    */   }
/*    */   protected void setFormat(RasterImageFormat format) {
/* 32 */     this._format = format;
/*    */   }
/*    */ 
/*    */   public ArrayList<String> getFilesList()
/*    */   {
/* 37 */     return this._filesList;
/*    */   }
/*    */   protected void setFilesList(ArrayList<String> filesList) {
/* 40 */     this._filesList = filesList;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.SaveData
 * JD-Core Version:    0.6.2
 */