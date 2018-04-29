/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.RasterViewPerspective;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class ChangeViewPerspectiveCommand extends RasterCommand
/*    */ {
/*    */   private RasterImage _destinationImage;
/*    */   private boolean _inPlace;
/*    */   private RasterViewPerspective _viewPerspective;
/*    */ 
/*    */   public ChangeViewPerspectiveCommand()
/*    */   {
/* 16 */     this._destinationImage = null;
/* 17 */     this._inPlace = true;
/* 18 */     this._viewPerspective = RasterViewPerspective.TOP_LEFT;
/*    */   }
/*    */ 
/*    */   public ChangeViewPerspectiveCommand(boolean inPlace, RasterViewPerspective viewPerspective) {
/* 22 */     this._destinationImage = null;
/* 23 */     this._inPlace = inPlace;
/* 24 */     this._viewPerspective = viewPerspective;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 30 */     return "Change View Perspective";
/*    */   }
/*    */ 
/*    */   public RasterImage getDestinationImage()
/*    */   {
/* 35 */     return this._destinationImage;
/*    */   }
/*    */ 
/*    */   public boolean getInPlace()
/*    */   {
/* 40 */     return this._inPlace;
/*    */   }
/*    */ 
/*    */   public void setInPlace(boolean value) {
/* 44 */     this._inPlace = value;
/*    */   }
/*    */ 
/*    */   public RasterViewPerspective getViewPerspective()
/*    */   {
/* 49 */     return this._viewPerspective;
/*    */   }
/*    */ 
/*    */   public void setViewPerspective(RasterViewPerspective value) {
/* 53 */     this._viewPerspective = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/* 61 */     this._destinationImage = null;
/*    */ 
/* 63 */     long destBitmap = ltkrn.AllocBitmapHandle();
/*    */     try
/*    */     {
/* 66 */       int ret = ltkrn.ChangeBitmapViewPerspective(getInPlace() ? 0L : destBitmap, bitmap, ltkrn.BITMAPHANDLE_getSizeOf(), getViewPerspective().getValue());
/*    */ 
/* 68 */       if (!getInPlace()) {
/* 69 */         this._destinationImage = RasterImage.createFromBitmapHandle(destBitmap);
/*    */       }
/* 71 */       return ret;
/*    */     }
/*    */     finally
/*    */     {
/* 75 */       ltkrn.FreeBitmapHandle(destBitmap);
/* 76 */       if (getInPlace())
/* 77 */         changedFlags[0] |= RasterImageChangedFlags.VIEW_PERSPECTIVE;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.ChangeViewPerspectiveCommand
 * JD-Core Version:    0.6.2
 */