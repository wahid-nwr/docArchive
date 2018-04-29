/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsDocOptions
/*    */ {
/*    */   private CodecsDocLoadOptions load;
/*    */ 
/*    */   CodecsDocOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsDocLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsDocOptions copy(CodecsOptions owner) {
/* 11 */     CodecsDocOptions copy = new CodecsDocOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/*    */ 
/* 14 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsDocLoadOptions load) {
/* 18 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsDocLoadOptions getLoad() {
/* 22 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsDocOptions
 * JD-Core Version:    0.6.2
 */