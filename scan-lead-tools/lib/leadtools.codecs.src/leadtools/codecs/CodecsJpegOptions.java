/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsJpegOptions
/*    */ {
/*    */   private CodecsJpegLoadOptions _load;
/*    */   private CodecsJpegSaveOptions _save;
/*    */ 
/*    */   CodecsJpegOptions(CodecsOptions owner)
/*    */   {
/* 10 */     this._load = new CodecsJpegLoadOptions(owner);
/* 11 */     this._save = new CodecsJpegSaveOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsJpegOptions copy(CodecsOptions owner) {
/* 15 */     CodecsJpegOptions copy = new CodecsJpegOptions(owner);
/* 16 */     copy.setLoad(this._load.copy(owner));
/* 17 */     copy.setSave(this._save.copy(owner));
/*    */ 
/* 19 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsJpegLoadOptions options) {
/* 23 */     this._load = options;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsJpegSaveOptions options) {
/* 27 */     this._save = options;
/*    */   }
/*    */ 
/*    */   public CodecsJpegLoadOptions getLoad() {
/* 31 */     return this._load;
/*    */   }
/*    */ 
/*    */   public CodecsJpegSaveOptions getSave() {
/* 35 */     return this._save;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpegOptions
 * JD-Core Version:    0.6.2
 */