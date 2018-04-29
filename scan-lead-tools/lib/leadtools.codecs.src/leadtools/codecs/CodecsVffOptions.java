/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsVffOptions
/*    */ {
/*    */   private CodecsVffLoadOptions load;
/*    */ 
/*    */   CodecsVffOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsVffLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsVffOptions copy(CodecsOptions owner) {
/* 11 */     CodecsVffOptions copy = new CodecsVffOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/* 13 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsVffLoadOptions load) {
/* 17 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsVffLoadOptions getLoad() {
/* 21 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsVffOptions
 * JD-Core Version:    0.6.2
 */