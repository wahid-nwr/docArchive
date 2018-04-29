/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class ClearNegativePixelsCommand extends RasterCommand
/*    */ {
/*    */   public String toString()
/*    */   {
/* 12 */     return "Clear Negative Pixels";
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/*    */     try
/*    */     {
/* 22 */       int ret = ltkrn.ClearNegativePixels(image.getCurrentBitmapHandle());
/* 23 */       return ret;
/*    */     }
/*    */     finally
/*    */     {
/* 27 */       changedFlags[0] |= RasterImageChangedFlags.DATA;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.ClearNegativePixelsCommand
 * JD-Core Version:    0.6.2
 */