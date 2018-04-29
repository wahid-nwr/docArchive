/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsTiffOptions
/*    */ {
/*    */   private CodecsTiffLoadOptions load;
/*    */   private CodecsTiffSaveOptions save;
/*    */ 
/*    */   CodecsTiffOptions(CodecsOptions owner)
/*    */   {
/*  8 */     this.load = new CodecsTiffLoadOptions(owner);
/*  9 */     this.save = new CodecsTiffSaveOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsTiffOptions copy(CodecsOptions owner) {
/* 13 */     CodecsTiffOptions copy = new CodecsTiffOptions(owner);
/* 14 */     copy.setLoad(getLoad().copy(owner));
/* 15 */     copy.setSave(getSave().copy(owner));
/* 16 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsTiffLoadOptions load) {
/* 20 */     this.load = load;
/*    */   }
/*    */ 
/*    */   private void setSave(CodecsTiffSaveOptions save) {
/* 24 */     this.save = save;
/*    */   }
/*    */ 
/*    */   public CodecsTiffLoadOptions getLoad() {
/* 28 */     return this.load;
/*    */   }
/*    */ 
/*    */   public CodecsTiffSaveOptions getSave() {
/* 32 */     return this.save;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsTiffOptions
 * JD-Core Version:    0.6.2
 */