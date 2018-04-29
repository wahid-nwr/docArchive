/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsPngOptions
/*    */ {
/*    */   private CodecsPngLoadOptions load;
/*    */   private CodecsPngSaveOptions save;
/*    */ 
/*    */   CodecsPngOptions(CodecsOptions owner)
/*    */   {
/* 10 */     this.load = new CodecsPngLoadOptions(owner);
/* 11 */     this.save = new CodecsPngSaveOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsPngOptions copy(CodecsOptions owner) {
/* 15 */     CodecsPngOptions copy = new CodecsPngOptions(owner);
/* 16 */     copy.setLoad(getLoad().copy(owner));
/* 17 */     copy.setSave(getSave().copy(owner));
/*    */ 
/* 19 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsPngLoadOptions load)
/*    */   {
/* 24 */     this.load = load;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsPngSaveOptions save) {
/* 28 */     this.save = save;
/*    */   }
/*    */ 
/*    */   public CodecsPngLoadOptions getLoad()
/*    */   {
/* 33 */     return this.load;
/*    */   }
/*    */ 
/*    */   public CodecsPngSaveOptions getSave() {
/* 37 */     return this.save;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPngOptions
 * JD-Core Version:    0.6.2
 */