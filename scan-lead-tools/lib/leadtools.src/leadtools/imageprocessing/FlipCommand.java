/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class FlipCommand extends RasterCommand
/*    */ {
/*    */   private boolean _horizontal;
/*    */ 
/*    */   public FlipCommand()
/*    */   {
/* 13 */     this._horizontal = false;
/*    */   }
/*    */ 
/*    */   public FlipCommand(boolean horizontal)
/*    */   {
/* 18 */     this._horizontal = horizontal;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 24 */     if (getHorizontal()) {
/* 25 */       return "Reverse";
/*    */     }
/* 27 */     return "Flip";
/*    */   }
/*    */ 
/*    */   public boolean getHorizontal()
/*    */   {
/* 32 */     return this._horizontal;
/*    */   }
/*    */ 
/*    */   public void setHorizontal(boolean value)
/*    */   {
/* 37 */     this._horizontal = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/*    */     try
/*    */     {
/*    */       int i;
/* 45 */       if (getHorizontal()) {
/* 46 */         return ltkrn.ReverseBitmap(bitmap);
/*    */       }
/* 48 */       return ltkrn.FlipBitmap(bitmap);
/*    */     }
/*    */     finally
/*    */     {
/* 52 */       changedFlags[0] |= RasterImageChangedFlags.DATA;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.FlipCommand
 * JD-Core Version:    0.6.2
 */