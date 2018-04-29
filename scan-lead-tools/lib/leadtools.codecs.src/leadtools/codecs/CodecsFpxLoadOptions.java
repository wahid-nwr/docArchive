/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.LeadSize;
/*    */ import leadtools.RasterImageFormat;
/*    */ 
/*    */ public class CodecsFpxLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   CodecsFpxLoadOptions(CodecsOptions owner)
/*    */   {
/* 10 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsFpxLoadOptions copy(CodecsOptions owner) {
/* 14 */     CodecsFpxLoadOptions copy = new CodecsFpxLoadOptions(owner);
/* 15 */     copy.setResolution(getResolution());
/*    */ 
/* 17 */     return copy;
/*    */   }
/*    */ 
/*    */   public LeadSize getResolution() {
/* 21 */     return this.owner.getLoadResolution(RasterImageFormat.FPX);
/*    */   }
/*    */ 
/*    */   public void setResolution(LeadSize resolution) {
/* 25 */     this.owner.setLoadResolution(RasterImageFormat.FPX, resolution);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsFpxLoadOptions
 * JD-Core Version:    0.6.2
 */