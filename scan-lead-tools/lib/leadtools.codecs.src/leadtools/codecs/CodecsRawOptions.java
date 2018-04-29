/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsRawOptions
/*    */ {
/*    */   private CodecsRawSaveOptions save;
/*    */ 
/*    */   public CodecsRawOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.save = new CodecsRawSaveOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsRawOptions copy(CodecsOptions owner) {
/* 11 */     CodecsRawOptions copy = new CodecsRawOptions(owner);
/* 12 */     copy.setSave(getSave().copy(owner));
/* 13 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsRawSaveOptions save) {
/* 17 */     this.save = save;
/*    */   }
/*    */ 
/*    */   public CodecsRawSaveOptions getSave() {
/* 21 */     return this.save;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRawOptions
 * JD-Core Version:    0.6.2
 */