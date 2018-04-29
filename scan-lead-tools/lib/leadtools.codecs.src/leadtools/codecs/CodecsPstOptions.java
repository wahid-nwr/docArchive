/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsPstOptions
/*    */ {
/*    */   private CodecsPstLoadOptions load;
/*    */ 
/*    */   CodecsPstOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsPstLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsPstOptions copy(CodecsOptions owner) {
/* 11 */     CodecsPstOptions copy = new CodecsPstOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/*    */ 
/* 14 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsPstLoadOptions load) {
/* 18 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsPstLoadOptions getLoad() {
/* 22 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPstOptions
 * JD-Core Version:    0.6.2
 */