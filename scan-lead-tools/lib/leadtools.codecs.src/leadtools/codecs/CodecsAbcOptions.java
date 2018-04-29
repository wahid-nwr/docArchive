/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsAbcOptions
/*    */ {
/*    */   private CodecsAbcSaveOptions save;
/*    */   private CodecsAbcLoadOptions load;
/*    */ 
/*    */   CodecsAbcOptions(CodecsOptions owner)
/*    */   {
/* 10 */     this.save = new CodecsAbcSaveOptions(owner);
/* 11 */     this.load = new CodecsAbcLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsAbcOptions copy(CodecsOptions owner) {
/* 15 */     CodecsAbcOptions copy = new CodecsAbcOptions(owner);
/* 16 */     copy.setLoad(getLoad().copy(owner));
/* 17 */     copy.setSave(getSave().copy(owner));
/*    */ 
/* 19 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsAbcSaveOptions options) {
/* 23 */     this.save = options;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsAbcLoadOptions options) {
/* 27 */     this.load = options;
/*    */   }
/*    */ 
/*    */   public CodecsAbcSaveOptions getSave() {
/* 31 */     return this.save;
/*    */   }
/*    */ 
/*    */   public CodecsAbcLoadOptions getLoad() {
/* 35 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsAbcOptions
 * JD-Core Version:    0.6.2
 */