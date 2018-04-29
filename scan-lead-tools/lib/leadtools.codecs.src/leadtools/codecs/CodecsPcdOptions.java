/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsPcdOptions
/*    */ {
/*    */   private CodecsPcdLoadOptions load;
/*    */ 
/*    */   public CodecsPcdOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsPcdLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsPcdOptions copy(CodecsOptions owner) {
/* 11 */     CodecsPcdOptions copy = new CodecsPcdOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/*    */ 
/* 14 */     return copy;
/*    */   }
/*    */ 
/*    */   void setLoad(CodecsPcdLoadOptions load) {
/* 18 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsPcdLoadOptions getLoad() {
/* 22 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPcdOptions
 * JD-Core Version:    0.6.2
 */