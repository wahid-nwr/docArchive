/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsXpsOptions
/*    */ {
/*    */   private CodecsXpsLoadOptions load;
/*    */   private CodecsXpsSaveOptions save;
/*    */ 
/*    */   CodecsXpsOptions(CodecsOptions owner)
/*    */   {
/*  8 */     this.load = new CodecsXpsLoadOptions(owner);
/*  9 */     this.save = new CodecsXpsSaveOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsXpsOptions copy(CodecsOptions owner) {
/* 13 */     CodecsXpsOptions copy = new CodecsXpsOptions(owner);
/* 14 */     copy.setLoad(getLoad().copy(owner));
/* 15 */     copy.setSave(getSave().copy(owner));
/* 16 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsXpsSaveOptions save) {
/* 20 */     this.save = save;
/*    */   }
/*    */ 
/*    */   public CodecsXpsSaveOptions getSave() {
/* 24 */     return this.save;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsXpsLoadOptions load) {
/* 28 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsXpsLoadOptions getLoad() {
/* 32 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsXpsOptions
 * JD-Core Version:    0.6.2
 */