/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsPdfOptions
/*    */ {
/*    */   private CodecsOptions _owner;
/*    */   private CodecsPdfLoadOptions _load;
/*    */   private CodecsPdfSaveOptions _save;
/*  8 */   private static Object _pdfInitialPathLockObject = new Object();
/*  9 */   private static String _globalPdfInitialPath = null;
/*    */ 
/*    */   static String getGlobalPdfInitialPath()
/*    */   {
/* 13 */     synchronized (_pdfInitialPathLockObject)
/*    */     {
/* 15 */       return _globalPdfInitialPath;
/*    */     }
/*    */   }
/*    */ 
/*    */   CodecsPdfOptions(CodecsOptions owner)
/*    */   {
/* 21 */     this._owner = owner;
/* 22 */     this._load = new CodecsPdfLoadOptions(this._owner);
/* 23 */     this._save = new CodecsPdfSaveOptions(this._owner);
/*    */   }
/*    */ 
/*    */   CodecsPdfOptions copy(CodecsOptions owner) {
/* 27 */     CodecsPdfOptions copy = new CodecsPdfOptions(owner);
/* 28 */     copy.setLoad(getLoad().copy(owner));
/* 29 */     copy.setSave(getSave().copy(owner));
/*    */ 
/* 31 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsPdfLoadOptions load) {
/* 35 */     this._load = load;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsPdfSaveOptions save) {
/* 39 */     this._save = save;
/*    */   }
/*    */ 
/*    */   public CodecsPdfLoadOptions getLoad()
/*    */   {
/* 44 */     return this._load;
/*    */   }
/*    */ 
/*    */   public CodecsPdfSaveOptions getSave() {
/* 48 */     return this._save;
/*    */   }
/*    */ 
/*    */   public String getInitialPath()
/*    */   {
/* 53 */     return new String(this._owner.getThreadData().szPDFInitDir);
/*    */   }
/*    */ 
/*    */   public void setInitialPath(String value)
/*    */   {
/* 58 */     synchronized (_pdfInitialPathLockObject)
/*    */     {
/* 60 */       _globalPdfInitialPath = value;
/*    */     }
/*    */ 
/* 64 */     this._owner.getThreadData().szPDFInitDir = new String(value);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPdfOptions
 * JD-Core Version:    0.6.2
 */