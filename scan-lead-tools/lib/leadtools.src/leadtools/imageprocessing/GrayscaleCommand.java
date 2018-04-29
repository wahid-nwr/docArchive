/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class GrayscaleCommand extends RasterCommand
/*    */ {
/*    */   private int _bitsPerPixel;
/*    */ 
/*    */   public GrayscaleCommand()
/*    */   {
/* 13 */     this._bitsPerPixel = 16;
/*    */   }
/*    */ 
/*    */   public GrayscaleCommand(int bitsPerPixel)
/*    */   {
/* 18 */     this._bitsPerPixel = bitsPerPixel;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 24 */     return "Grayscale";
/*    */   }
/*    */ 
/*    */   public int getBitsPerPixel() {
/* 28 */     return this._bitsPerPixel;
/*    */   }
/*    */   public void setBitsPerPixel(int value) {
/* 31 */     this._bitsPerPixel = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/*    */     try
/*    */     {
/* 39 */       return ltkrn.GrayScaleBitmap(image.getCurrentBitmapHandle(), this._bitsPerPixel);
/*    */     }
/*    */     finally
/*    */     {
/* 43 */       changedFlags[0] |= RasterImageChangedFlags.DATA;
/* 44 */       if (image.hasRegion())
/* 45 */         changedFlags[0] |= RasterImageChangedFlags.REGION;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.GrayscaleCommand
 * JD-Core Version:    0.6.2
 */