/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsFpxOptions
/*    */ {
/*    */   private CodecsFpxLoadOptions load;
/*    */ 
/*    */   CodecsFpxOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsFpxLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsFpxOptions copy(CodecsOptions owner) {
/* 11 */     CodecsFpxOptions copy = new CodecsFpxOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/*    */ 
/* 14 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsFpxLoadOptions load) {
/* 18 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsFpxLoadOptions getLoad() {
/* 22 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsFpxOptions
 * JD-Core Version:    0.6.2
 */