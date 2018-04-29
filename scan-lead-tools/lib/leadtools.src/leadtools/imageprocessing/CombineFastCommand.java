/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.LeadPoint;
/*    */ import leadtools.LeadRect;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedEvent;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class CombineFastCommand extends RasterCommand
/*    */ {
/*    */   private RasterImage _destinationImage;
/*    */   private LeadRect _destinationRectangle;
/*    */   private LeadPoint _sourcePoint;
/*    */   private int _flags;
/*    */ 
/*    */   public String toString()
/*    */   {
/* 20 */     return "Combine Fast";
/*    */   }
/*    */ 
/*    */   public CombineFastCommand()
/*    */   {
/* 25 */     this._destinationImage = null;
/* 26 */     this._destinationRectangle = LeadRect.getEmpty();
/* 27 */     this._sourcePoint = new LeadPoint();
/*    */ 
/* 29 */     this._flags = CombineFastCommandFlags.SOURCE_COPY.getValue();
/*    */   }
/*    */ 
/*    */   public CombineFastCommand(RasterImage destinationImage, LeadRect destinationRectangle, LeadPoint sourcePoint, int flags)
/*    */   {
/* 35 */     this._destinationImage = destinationImage;
/* 36 */     this._destinationRectangle = destinationRectangle;
/* 37 */     this._sourcePoint = sourcePoint;
/* 38 */     this._flags = flags;
/*    */   }
/*    */ 
/*    */   public final RasterImage getDestinationImage() {
/* 42 */     return this._destinationImage;
/*    */   }
/*    */   public final void setDestinationImage(RasterImage value) {
/* 45 */     this._destinationImage = value;
/*    */   }
/*    */ 
/*    */   public final LeadRect getDestinationRectangle() {
/* 49 */     return this._destinationRectangle;
/*    */   }
/*    */   public final void setDestinationRectangle(LeadRect value) {
/* 52 */     this._destinationRectangle = value;
/*    */   }
/*    */ 
/*    */   public final LeadPoint getSourcePoint() {
/* 56 */     return this._sourcePoint;
/*    */   }
/*    */   public final void setSourcePoint(LeadPoint value) {
/* 59 */     this._sourcePoint = value;
/*    */   }
/*    */ 
/*    */   public final int getFlags()
/*    */   {
/* 69 */     return this._flags;
/*    */   }
/*    */   public final void setFlags(int value) {
/* 72 */     this._flags = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/* 78 */     RasterImage destinationImage = getDestinationImage();
/* 79 */     destinationImage.disableEvents();
/*    */ 
/* 83 */     long srcBitmap = bitmap;
/* 84 */     long destBitmap = this._destinationImage.getCurrentBitmapHandle();
/*    */     try
/*    */     {
/* 88 */       int ret = ltkrn.CombineBitmapKrn(destBitmap, getDestinationRectangle().getLeft(), getDestinationRectangle().getTop(), getDestinationRectangle().getWidth(), getDestinationRectangle().getHeight(), srcBitmap, getSourcePoint().getX(), getSourcePoint().getY(), getFlags());
/*    */ 
/* 98 */       return ret;
/*    */     }
/*    */     finally
/*    */     {
/* 102 */       destinationImage.updateCurrentBitmapHandle();
/*    */ 
/* 104 */       destinationImage.enableEvents();
/* 105 */       destinationImage.onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.CombineFastCommand
 * JD-Core Version:    0.6.2
 */