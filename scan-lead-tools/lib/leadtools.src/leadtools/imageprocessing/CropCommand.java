/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.LeadRect;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class CropCommand extends RasterCommand
/*    */ {
/*    */   private LeadRect _rc;
/*    */ 
/*    */   public CropCommand()
/*    */   {
/* 14 */     this._rc = LeadRect.getEmpty();
/*    */   }
/*    */ 
/*    */   public CropCommand(LeadRect rc)
/*    */   {
/* 19 */     this._rc = rc;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 25 */     return "Crop";
/*    */   }
/*    */ 
/*    */   public LeadRect getRectangle() {
/* 29 */     return this._rc;
/*    */   }
/*    */   public void setRectangle(LeadRect value) {
/* 32 */     this._rc = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/*    */     try
/*    */     {
/* 42 */       long srcBitmap = image.getCurrentBitmapHandle();
/*    */       try
/*    */       {
/* 46 */         LeadRect rc = getRectangle();
/* 47 */         if ((rc.getLeft() > rc.getRight()) || (rc.getTop() > rc.getBottom())) rc = LeadRect.fromLTRB(Math.min(rc.getLeft(), rc.getRight()), Math.min(rc.getTop(), rc.getBottom()), Math.max(rc.getLeft(), rc.getRight()), Math.max(rc.getTop(), rc.getBottom()));
/*    */ 
/* 49 */         int ret = ltkrn.TrimBitmap(srcBitmap, rc.getLeft(), rc.getTop(), rc.getWidth(), rc.getHeight());
/* 50 */         return ret;
/*    */       }
/*    */       finally
/*    */       {
/* 55 */         changedFlags[0] = (RasterImageChangedFlags.DATA | RasterImageChangedFlags.SIZE);
/*    */       }
/*    */     }
/*    */     finally
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean getUseCopyStatusCallback()
/*    */   {
/* 65 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.CropCommand
 * JD-Core Version:    0.6.2
 */