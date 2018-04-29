/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsXlsOptions
/*    */ {
/*    */   private CodecsXlsLoadOptions load;
/*    */ 
/*    */   CodecsXlsOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsXlsLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsXlsOptions copy(CodecsOptions owner) {
/* 11 */     CodecsXlsOptions copy = new CodecsXlsOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/* 13 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsXlsLoadOptions load) {
/* 17 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsXlsLoadOptions getLoad() {
/* 21 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsXlsOptions
 * JD-Core Version:    0.6.2
 */