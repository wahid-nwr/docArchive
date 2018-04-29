/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsGifOptions
/*    */ {
/*    */   private CodecsGifLoadOptions load;
/*    */   private CodecsGifSaveOptions save;
/*    */ 
/*    */   public CodecsGifOptions(CodecsOptions owner)
/*    */   {
/*  8 */     this.load = new CodecsGifLoadOptions(owner);
/*  9 */     this.save = new CodecsGifSaveOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsGifOptions copy(CodecsOptions owner) {
/* 13 */     CodecsGifOptions copy = new CodecsGifOptions(owner);
/* 14 */     copy.setLoad(getLoad().copy(owner));
/* 15 */     copy.setSave(getSave().copy(owner));
/*    */ 
/* 17 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsGifLoadOptions load) {
/* 21 */     this.load = load;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsGifSaveOptions save) {
/* 25 */     this.save = save;
/*    */   }
/*    */ 
/*    */   public CodecsGifLoadOptions getLoad() {
/* 29 */     return this.load;
/*    */   }
/*    */ 
/*    */   public CodecsGifSaveOptions getSave() {
/* 33 */     return this.save;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsGifOptions
 * JD-Core Version:    0.6.2
 */