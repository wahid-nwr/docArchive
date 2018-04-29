/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.L_ERROR;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class CreateGrayscaleImageCommand extends RasterCommand
/*    */ {
/*    */   private int _bitsPerPixel;
/*    */   private RasterImage _destinationImage;
/*    */   private int _lowBit;
/*    */   private int _highBit;
/*    */ 
/*    */   public CreateGrayscaleImageCommand()
/*    */   {
/* 16 */     this._bitsPerPixel = 16;
/* 17 */     this._lowBit = 0;
/* 18 */     this._highBit = 15;
/*    */   }
/*    */ 
/*    */   public CreateGrayscaleImageCommand(int bitsPerPixel, int lowBit, int highBit)
/*    */   {
/* 23 */     this._bitsPerPixel = bitsPerPixel;
/* 24 */     this._lowBit = lowBit;
/* 25 */     this._highBit = highBit;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 31 */     return "Create Grayscale Image";
/*    */   }
/*    */ 
/*    */   public int getBitsPerPixel() {
/* 35 */     return this._bitsPerPixel;
/*    */   }
/*    */   public void setBitsPerPixel(int value) {
/* 38 */     this._bitsPerPixel = value;
/*    */   }
/*    */ 
/*    */   public int getLowBit() {
/* 42 */     return this._lowBit;
/*    */   }
/*    */   public void setLowBit(int value) {
/* 45 */     this._lowBit = value;
/*    */   }
/*    */ 
/*    */   public int getHighBit() {
/* 49 */     return this._highBit;
/*    */   }
/*    */   public void setHighBit(int value) {
/* 52 */     this._highBit = value;
/*    */   }
/*    */ 
/*    */   public RasterImage getDestinationImage()
/*    */   {
/* 57 */     return this._destinationImage;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/* 65 */     this._destinationImage = null;
/*    */     try
/*    */     {
/* 69 */       long destBitmap = ltkrn.AllocBitmapHandle();
/* 70 */       if (destBitmap == 0L) {
/* 71 */         i = L_ERROR.ERROR_NO_MEMORY.getValue(); return i;
/*    */       }
/*    */       int ret;
/*    */       try {
/* 75 */         ltkrn.BITMAPHANDLE_setLowBit(destBitmap, this._lowBit);
/* 76 */         ltkrn.BITMAPHANDLE_setHighBit(destBitmap, this._highBit);
/*    */ 
/* 78 */         ret = ltkrn.CreateGrayScaleBitmap(destBitmap, bitmap, this._bitsPerPixel);
/*    */ 
/* 80 */         if (ret == L_ERROR.SUCCESS.getValue())
/*    */         {
/* 82 */           this._destinationImage = RasterImage.createFromBitmapHandle(destBitmap);
/*    */         }
/*    */         else
/* 85 */           ltkrn.FreeBitmap(destBitmap);
/*    */       }
/*    */       finally
/*    */       {
/* 89 */         ltkrn.FreeBitmapHandle(destBitmap);
/*    */       }
/* 91 */       int i = ret; return i;
/*    */     }
/*    */     finally
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.CreateGrayscaleImageCommand
 * JD-Core Version:    0.6.2
 */