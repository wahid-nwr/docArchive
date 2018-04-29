/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsEpsOptions
/*    */ {
/*    */   private CodecsEpsLoadOptions load;
/*    */ 
/*    */   CodecsEpsOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsEpsLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsEpsOptions copy(CodecsOptions owner) {
/* 11 */     CodecsEpsOptions copy = new CodecsEpsOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/*    */ 
/* 14 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsEpsLoadOptions load) {
/* 18 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsEpsLoadOptions getLoad() {
/* 22 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsEpsOptions
 * JD-Core Version:    0.6.2
 */