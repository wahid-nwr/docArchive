/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class SetAlphaValuesCommand extends RasterCommand
/*    */ {
/*    */   private int _alpha;
/*    */ 
/*    */   public SetAlphaValuesCommand()
/*    */   {
/* 13 */     this._alpha = 65535;
/*    */   }
/*    */ 
/*    */   public SetAlphaValuesCommand(int alpha)
/*    */   {
/* 18 */     this._alpha = alpha;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 24 */     return "Set Alpha Values";
/*    */   }
/*    */ 
/*    */   public int getAlpha() {
/* 28 */     return this._alpha;
/*    */   }
/*    */   public void setAlpha(int value) {
/* 31 */     this._alpha = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/*    */     try
/*    */     {
/* 39 */       return ltkrn.SetBitmapAlphaValues(image.getCurrentBitmapHandle(), (short)this._alpha);
/*    */     }
/*    */     finally
/*    */     {
/* 43 */       changedFlags[0] |= RasterImageChangedFlags.DATA;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.SetAlphaValuesCommand
 * JD-Core Version:    0.6.2
 */