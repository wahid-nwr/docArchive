/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsJbig2Options
/*    */ {
/*    */   public CodecsJbig2LoadOptions load;
/*    */   public CodecsJbig2SaveOptions save;
/*    */ 
/*    */   CodecsJbig2Options(CodecsOptions owner)
/*    */   {
/*  8 */     this.load = new CodecsJbig2LoadOptions(owner);
/*  9 */     this.save = new CodecsJbig2SaveOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsJbig2Options copy(CodecsOptions owner) {
/* 13 */     CodecsJbig2Options copy = new CodecsJbig2Options(owner);
/* 14 */     copy.setLoad(getLoad().copy(owner));
/* 15 */     copy.setSave(getSave().copy(owner));
/*    */ 
/* 17 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsJbig2LoadOptions load) {
/* 21 */     this.load = load;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsJbig2SaveOptions save) {
/* 25 */     this.save = save;
/*    */   }
/*    */ 
/*    */   public CodecsJbig2LoadOptions getLoad() {
/* 29 */     return this.load;
/*    */   }
/*    */ 
/*    */   public CodecsJbig2SaveOptions getSave() {
/* 33 */     return this.save;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJbig2Options
 * JD-Core Version:    0.6.2
 */