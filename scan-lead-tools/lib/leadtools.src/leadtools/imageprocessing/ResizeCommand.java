/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedEvent;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.RasterSizeFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class ResizeCommand extends RasterCommand
/*    */ {
/*    */   private RasterSizeFlags _flags;
/*    */   private RasterImage _destinationImage;
/*    */ 
/*    */   public ResizeCommand()
/*    */   {
/* 16 */     this._destinationImage = null;
/* 17 */     this._flags = RasterSizeFlags.BICUBIC;
/*    */   }
/*    */ 
/*    */   public ResizeCommand(RasterImage destinationImage, RasterSizeFlags flags)
/*    */   {
/* 22 */     this._destinationImage = destinationImage;
/* 23 */     this._flags = flags;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 29 */     return "Size";
/*    */   }
/*    */ 
/*    */   public RasterImage getDestinationImage()
/*    */   {
/* 34 */     return this._destinationImage;
/*    */   }
/*    */ 
/*    */   public void setDestinationImage(RasterImage value)
/*    */   {
/* 39 */     this._destinationImage = value;
/*    */   }
/*    */ 
/*    */   public RasterSizeFlags getFlags()
/*    */   {
/* 44 */     return this._flags;
/*    */   }
/*    */ 
/*    */   public void setFlags(RasterSizeFlags value)
/*    */   {
/* 49 */     this._flags = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/* 55 */     this._destinationImage.disableEvents();
/*    */     try
/*    */     {
/* 58 */       return ltkrn.ResizeBitmap(bitmap, this._destinationImage.getCurrentBitmapHandle(), this._flags.getValue());
/*    */     }
/*    */     finally
/*    */     {
/* 62 */       this._destinationImage.updateCurrentBitmapHandle();
/* 63 */       this._destinationImage.enableEvents();
/* 64 */       this._destinationImage.onChanged(new RasterImageChangedEvent(this._destinationImage, RasterImageChangedFlags.DATA));
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.ResizeCommand
 * JD-Core Version:    0.6.2
 */