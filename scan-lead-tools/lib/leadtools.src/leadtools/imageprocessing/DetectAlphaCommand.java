/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.L_ERROR;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class DetectAlphaCommand extends RasterCommand
/*    */ {
/*    */   private boolean _hasMeaningfulAlpha;
/*    */ 
/*    */   public DetectAlphaCommand()
/*    */   {
/* 13 */     this._hasMeaningfulAlpha = false;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 19 */     return "Detect Alpha";
/*    */   }
/*    */ 
/*    */   public boolean hasMeaningfulAlpha() {
/* 23 */     return this._hasMeaningfulAlpha;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/*    */     try
/*    */     {
/* 31 */       this._hasMeaningfulAlpha = (ltkrn.BitmapHasMeaningfulAlpha(bitmap) > 0);
/* 32 */       int i = L_ERROR.SUCCESS.getValue(); return i;
/*    */     }
/*    */     finally
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.DetectAlphaCommand
 * JD-Core Version:    0.6.2
 */