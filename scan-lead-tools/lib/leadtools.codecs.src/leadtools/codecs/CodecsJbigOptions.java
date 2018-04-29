/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsJbigOptions
/*    */ {
/*    */   private CodecsJbigLoadOptions load;
/*    */ 
/*    */   CodecsJbigOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsJbigLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsJbigOptions copy(CodecsOptions owner) {
/* 11 */     CodecsJbigOptions copy = new CodecsJbigOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/*    */ 
/* 14 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsJbigLoadOptions load) {
/* 18 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsJbigLoadOptions getLoad() {
/* 22 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJbigOptions
 * JD-Core Version:    0.6.2
 */