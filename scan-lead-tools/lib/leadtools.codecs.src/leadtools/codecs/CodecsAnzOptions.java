/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsAnzOptions
/*    */ {
/*    */   private CodecsAnzLoadOptions load;
/*    */ 
/*    */   CodecsAnzOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsAnzLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsAnzOptions copy(CodecsOptions owner) {
/* 11 */     CodecsAnzOptions copy = new CodecsAnzOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/*    */ 
/* 14 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsAnzLoadOptions load) {
/* 18 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsAnzLoadOptions getLoad() {
/* 22 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsAnzOptions
 * JD-Core Version:    0.6.2
 */