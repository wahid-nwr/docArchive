/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsRtfOptions
/*    */ {
/*    */   private CodecsRtfLoadOptions load;
/*    */ 
/*    */   CodecsRtfOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsRtfLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsRtfOptions copy(CodecsOptions owner) {
/* 11 */     CodecsRtfOptions copy = new CodecsRtfOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/* 13 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsRtfLoadOptions load) {
/* 17 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsRtfLoadOptions getLoad() {
/* 21 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRtfOptions
 * JD-Core Version:    0.6.2
 */