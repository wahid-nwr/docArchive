/*     */ package leadtools.imageprocessing;
/*     */ 
/*     */ import leadtools.L_ERROR;
/*     */ import leadtools.LeadRect;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.RasterImageChangedFlags;
/*     */ import leadtools.RasterMemoryFlags;
/*     */ import leadtools.ltkrn;
/*     */ 
/*     */ public class CopyRectangleCommand extends RasterCommand
/*     */ {
/*     */   private RasterImage _destinationImage;
/*     */   private LeadRect _rc;
/*     */   private int _createFlags;
/*     */ 
/*     */   public CopyRectangleCommand()
/*     */   {
/*  18 */     this._destinationImage = null;
/*  19 */     this._rc = new LeadRect(0, 0, 0, 0);
/*  20 */     this._createFlags = RasterMemoryFlags.CONVENTIONAL.getValue();
/*     */   }
/*     */ 
/*     */   public CopyRectangleCommand(LeadRect rc, int createFlags) {
/*  24 */     this._destinationImage = null;
/*  25 */     this._rc = rc;
/*  26 */     this._createFlags = createFlags;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  32 */     return "Copy Rectangle";
/*     */   }
/*     */ 
/*     */   public RasterImage getDestinationImage()
/*     */   {
/*  37 */     return this._destinationImage;
/*     */   }
/*     */ 
/*     */   public LeadRect getRectangle()
/*     */   {
/*  42 */     return this._rc;
/*     */   }
/*     */ 
/*     */   public void setRectangle(LeadRect value) {
/*  46 */     this._rc = value;
/*     */   }
/*     */ 
/*     */   public int getCreateFlags()
/*     */   {
/*  51 */     return this._createFlags;
/*     */   }
/*     */ 
/*     */   public void setCreateFlags(int value) {
/*  55 */     this._createFlags = value;
/*     */   }
/*     */ 
/*     */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*     */   {
/*  63 */     this._destinationImage = null;
/*     */     try
/*     */     {
/*  67 */       long srcBitmap = image.getCurrentBitmapHandle();
/*  68 */       long destBitmap = ltkrn.AllocBitmapHandle();
/*  69 */       if (destBitmap == 0L) {
/*  70 */         int i = L_ERROR.ERROR_NO_MEMORY.getValue(); return i;
/*     */       }LeadRect rc;
/*     */       int ret;
/*     */       try { rc = getRectangle();
/*  75 */         if ((rc.getLeft() > rc.getRight()) || (rc.getTop() > rc.getBottom()))
/*  76 */           rc = new LeadRect(Math.min(rc.getLeft(), rc.getRight()), Math.min(rc.getTop(), rc.getBottom()), rc.getWidth(), rc.getHeight());
/*     */         int ret;
/*  79 */         if (getCreateFlags() == RasterMemoryFlags.NONE.getValue())
/*  80 */           ret = ltkrn.CopyBitmapRect(destBitmap, srcBitmap, ltkrn.BITMAPHANDLE_getSizeOf(), rc.getLeft(), rc.getTop(), rc.getWidth(), rc.getHeight());
/*     */         else {
/*  82 */           ret = ltkrn.CopyBitmapRect2(destBitmap, srcBitmap, ltkrn.BITMAPHANDLE_getSizeOf(), rc.getLeft(), rc.getTop(), rc.getWidth(), rc.getHeight(), getCreateFlags());
/*     */         }
/*  84 */         if (ret == L_ERROR.SUCCESS.getValue())
/*     */         {
/*  86 */           this._destinationImage = RasterImage.createFromBitmapHandle(destBitmap);
/*  87 */           changedFlags[0] = (RasterImageChangedFlags.DATA | RasterImageChangedFlags.SIZE);
/*     */         }
/*     */         else {
/*  90 */           ltkrn.FreeBitmap(destBitmap);
/*     */         }
/*     */       } finally
/*     */       {
/*  94 */         ltkrn.FreeBitmapHandle(destBitmap);
/*     */       }
/*  96 */       int j = ret; return j;
/*     */     }
/*     */     finally
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getUseCopyStatusCallback()
/*     */   {
/* 105 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.CopyRectangleCommand
 * JD-Core Version:    0.6.2
 */