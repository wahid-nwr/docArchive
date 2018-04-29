/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsTxtOptions
/*    */ {
/*    */   private CodecsTxtLoadOptions load;
/*    */ 
/*    */   CodecsTxtOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsTxtLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsTxtOptions copy(CodecsOptions owner) {
/* 11 */     CodecsTxtOptions copy = new CodecsTxtOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/* 13 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsTxtLoadOptions load) {
/* 17 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsTxtLoadOptions getLoad() {
/* 21 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsTxtOptions
 * JD-Core Version:    0.6.2
 */