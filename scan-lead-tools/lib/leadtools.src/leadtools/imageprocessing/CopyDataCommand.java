/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.L_ERROR;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedEvent;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class CopyDataCommand extends RasterCommand
/*    */ {
/*    */   private RasterImage _destinationImage;
/*    */ 
/*    */   public CopyDataCommand()
/*    */   {
/* 15 */     this._destinationImage = null;
/*    */   }
/*    */ 
/*    */   public CopyDataCommand(RasterImage destinationImage)
/*    */   {
/* 20 */     this._destinationImage = destinationImage;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 26 */     return "Copy Data";
/*    */   }
/*    */ 
/*    */   public RasterImage getDestinationImage()
/*    */   {
/* 31 */     return this._destinationImage;
/*    */   }
/*    */ 
/*    */   public void setDestinationImage(RasterImage value) {
/* 35 */     this._destinationImage = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/* 43 */     RasterImage destinationImage = getDestinationImage();
/* 44 */     destinationImage.disableEvents();
/*    */     try
/*    */     {
/* 48 */       long srcBitmap = image.getCurrentBitmapHandle();
/* 49 */       long destBitmap = this._destinationImage.getCurrentBitmapHandle();
/*    */       int i;
/* 50 */       if (destBitmap == 0L)
/* 51 */         return L_ERROR.ERROR_NO_MEMORY.getValue();
/* 52 */       this._destinationImage.disableEvents();
/*    */ 
/* 54 */       int ret = ltkrn.CopyBitmapData(destBitmap, srcBitmap);
/* 55 */       return ret;
/*    */     }
/*    */     finally
/*    */     {
/* 59 */       destinationImage.updateCurrentBitmapHandle();
/*    */ 
/* 61 */       destinationImage.enableEvents();
/* 62 */       destinationImage.onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean getUseCopyStatusCallback()
/*    */   {
/* 69 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.CopyDataCommand
 * JD-Core Version:    0.6.2
 */