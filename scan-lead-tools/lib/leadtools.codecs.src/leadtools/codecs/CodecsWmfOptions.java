/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsWmfOptions
/*    */ {
/*    */   private CodecsWmfLoadOptions load;
/*    */ 
/*    */   CodecsWmfOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsWmfLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsWmfOptions copy(CodecsOptions owner) {
/* 11 */     CodecsWmfOptions copy = new CodecsWmfOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/* 13 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsWmfLoadOptions load) {
/* 17 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsWmfLoadOptions getLoad() {
/* 21 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsWmfOptions
 * JD-Core Version:    0.6.2
 */