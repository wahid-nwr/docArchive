/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsEcwOptions
/*    */ {
/*    */   private CodecsEcwSaveOptions save;
/*    */   private CodecsEcwLoadOptions load;
/*    */ 
/*    */   CodecsEcwOptions(CodecsOptions owner)
/*    */   {
/*  9 */     this.save = new CodecsEcwSaveOptions(owner);
/* 10 */     this.load = new CodecsEcwLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsEcwOptions copy(CodecsOptions owner) {
/* 14 */     CodecsEcwOptions copy = new CodecsEcwOptions(owner);
/* 15 */     copy.setLoad(getLoad().copy(owner));
/* 16 */     copy.setSave(getSave().copy(owner));
/*    */ 
/* 18 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsEcwSaveOptions save) {
/* 22 */     this.save = save;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsEcwLoadOptions load) {
/* 26 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsEcwSaveOptions getSave() {
/* 30 */     return this.save;
/*    */   }
/*    */ 
/*    */   public CodecsEcwLoadOptions getLoad() {
/* 34 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsEcwOptions
 * JD-Core Version:    0.6.2
 */