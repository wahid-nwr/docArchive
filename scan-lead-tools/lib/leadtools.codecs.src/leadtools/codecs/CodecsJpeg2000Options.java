/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsJpeg2000Options
/*    */ {
/*    */   private CodecsJpeg2000LoadOptions load;
/*    */   private CodecsJpeg2000SaveOptions save;
/*    */ 
/*    */   CodecsJpeg2000Options(CodecsOptions owner)
/*    */   {
/*  8 */     this.load = new CodecsJpeg2000LoadOptions(owner);
/*  9 */     this.save = new CodecsJpeg2000SaveOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsJpeg2000Options copy(CodecsOptions owner) {
/* 13 */     CodecsJpeg2000Options copy = new CodecsJpeg2000Options(owner);
/* 14 */     copy.setLoad(getLoad().copy(owner));
/* 15 */     copy.setSave(getSave().copy(owner));
/*    */ 
/* 17 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsJpeg2000LoadOptions load) {
/* 21 */     this.load = load;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsJpeg2000SaveOptions save) {
/* 25 */     this.save = save;
/*    */   }
/*    */ 
/*    */   public CodecsJpeg2000LoadOptions getLoad() {
/* 29 */     return this.load;
/*    */   }
/*    */ 
/*    */   public CodecsJpeg2000SaveOptions getSave() {
/* 33 */     return this.save;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpeg2000Options
 * JD-Core Version:    0.6.2
 */