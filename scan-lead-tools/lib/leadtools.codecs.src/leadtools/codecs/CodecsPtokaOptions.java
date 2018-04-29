/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsPtokaOptions
/*    */ {
/*    */   CodecsPtokaLoadOptions load;
/*    */ 
/*    */   CodecsPtokaOptions(CodecsOptions owner)
/*    */   {
/*  8 */     this.load = new CodecsPtokaLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsPtokaOptions copy(CodecsOptions owner) {
/* 12 */     CodecsPtokaOptions copy = new CodecsPtokaOptions(owner);
/* 13 */     copy.setLoad(getLoad().copy(owner));
/*    */ 
/* 15 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsPtokaLoadOptions load) {
/* 19 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsPtokaLoadOptions getLoad() {
/* 23 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPtokaOptions
 * JD-Core Version:    0.6.2
 */