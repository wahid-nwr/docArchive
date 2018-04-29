/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsRasterizeDocumentOptions
/*    */ {
/*    */   private CodecsRasterizeDocumentLoadOptions load;
/*    */ 
/*    */   CodecsRasterizeDocumentOptions(CodecsOptions owner)
/*    */   {
/*  7 */     this.load = new CodecsRasterizeDocumentLoadOptions(owner);
/*    */   }
/*    */ 
/*    */   CodecsRasterizeDocumentOptions copy(CodecsOptions owner) {
/* 11 */     CodecsRasterizeDocumentOptions copy = new CodecsRasterizeDocumentOptions(owner);
/* 12 */     copy.setLoad(getLoad().copy(owner));
/*    */ 
/* 14 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setLoad(CodecsRasterizeDocumentLoadOptions load) {
/* 18 */     this.load = load;
/*    */   }
/*    */ 
/*    */   public CodecsRasterizeDocumentLoadOptions getLoad() {
/* 22 */     return this.load;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRasterizeDocumentOptions
 * JD-Core Version:    0.6.2
 */