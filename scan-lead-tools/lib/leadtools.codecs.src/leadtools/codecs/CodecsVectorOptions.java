/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsVectorOptions
/*    */ {
/*    */   private CodecsVectorLoadOptions load;
/*    */ 
/*    */   CodecsVectorOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsVectorLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsVectorOptions copy(CodecsOptions owner) {
/* 11 */     CodecsVectorOptions copy = new CodecsVectorOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/* 13 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsVectorLoadOptions load) {
/* 17 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsVectorLoadOptions getLoad() {
/* 21 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsVectorOptions
 * JD-Core Version:    0.6.2
 */