/*     */ package leadtools.imageprocessing;
/*     */ 
/*     */ import leadtools.L_ERROR;
/*     */ import leadtools.RasterException;
/*     */ import leadtools.RasterExceptionCode;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.RasterImageChangedFlags;
/*     */ import leadtools.RasterMemoryFlags;
/*     */ import leadtools.ltkrn;
/*     */ 
/*     */ public class CloneCommand extends RasterCommand
/*     */ {
/*     */   private RasterImage _destinationImage;
/*     */   private int _createFlags;
/*     */   private boolean _allPages;
/*     */   private RasterImage _sourceImage;
/*     */ 
/*     */   public CloneCommand()
/*     */   {
/*  20 */     this._destinationImage = null;
/*  21 */     this._createFlags = RasterMemoryFlags.NONE.getValue();
/*     */   }
/*     */ 
/*     */   public CloneCommand(int createFlags) {
/*  25 */     this._destinationImage = null;
/*  26 */     this._createFlags = createFlags;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  32 */     return "Clone";
/*     */   }
/*     */ 
/*     */   public RasterImage getDestinationImage()
/*     */   {
/*  37 */     return this._destinationImage;
/*     */   }
/*     */ 
/*     */   public RasterImage getSourceImage()
/*     */   {
/*  42 */     return this._sourceImage;
/*     */   }
/*     */ 
/*     */   public int getCreateFlags()
/*     */   {
/*  47 */     return this._createFlags;
/*     */   }
/*     */ 
/*     */   public void setCreateFlags(int value) {
/*  51 */     this._createFlags = value;
/*     */   }
/*     */ 
/*     */   public final boolean getAllPages()
/*     */   {
/*  56 */     return this._allPages;
/*     */   }
/*     */ 
/*     */   public final void setAllPages(boolean value) {
/*  60 */     this._allPages = value;
/*     */   }
/*     */ 
/*     */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*     */   {
/*  66 */     int ret = L_ERROR.SUCCESS.getValue();
/*     */ 
/*  68 */     this._destinationImage = null;
/*  69 */     this._sourceImage = image;
/*     */     try
/*     */     {
/*     */       int savePageNumber;
/*  73 */       if ((!getAllPages()) || (image.getPageCount() == 1))
/*     */       {
/*  75 */         long srcBitmap = image.getCurrentBitmapHandle();
/*  76 */         long destBitmap = ltkrn.AllocBitmapHandle();
/*  77 */         if (destBitmap == 0L) {
/*  78 */           throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*     */         }
/*     */         try
/*     */         {
/*  82 */           if (getCreateFlags() == RasterMemoryFlags.NONE.getValue())
/*  83 */             ret = ltkrn.CopyBitmap(destBitmap, srcBitmap, ltkrn.BITMAPHANDLE_getSizeOf());
/*     */           else {
/*  85 */             ret = ltkrn.CopyBitmap2(destBitmap, srcBitmap, ltkrn.BITMAPHANDLE_getSizeOf(), getCreateFlags());
/*     */           }
/*  87 */           if (ret == L_ERROR.SUCCESS.getValue())
/*     */           {
/*  89 */             this._destinationImage = RasterImage.createFromBitmapHandle(destBitmap);
/*  90 */             changedFlags[0] = (RasterImageChangedFlags.DATA | RasterImageChangedFlags.SIZE);
/*     */           }
/*     */           else {
/*  93 */             ltkrn.FreeBitmap(destBitmap);
/*     */           }
/*     */         }
/*     */         finally {
/*  97 */           ltkrn.FreeBitmapHandle(destBitmap);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 103 */         savePageNumber = image.getPage();
/* 104 */         RasterImage destinationImage = null;
/*     */         try
/*     */         {
/* 108 */           long destBitmap = ltkrn.AllocBitmapHandle();
/* 109 */           if (destBitmap == 0L) {
/* 110 */             int i = L_ERROR.ERROR_NO_MEMORY.getValue();
/*     */ 
/* 148 */             image.setPage(savePageNumber);
/*     */ 
/* 162 */             return i;
/*     */           }
/*     */           try
/*     */           {
/* 114 */             for (int page = 1; (page <= image.getPageCount()) && (ret == L_ERROR.SUCCESS.getValue()); page++)
/*     */             {
/* 116 */               image.setPage(page);
/*     */ 
/* 118 */               long srcBitmap = image.getCurrentBitmapHandle();
/*     */ 
/* 120 */               if (getCreateFlags() == RasterMemoryFlags.NONE.getValue())
/* 121 */                 ret = ltkrn.CopyBitmap(destBitmap, srcBitmap, ltkrn.BITMAPHANDLE_getSizeOf());
/*     */               else {
/* 123 */                 ret = ltkrn.CopyBitmap2(destBitmap, srcBitmap, ltkrn.BITMAPHANDLE_getSizeOf(), getCreateFlags());
/*     */               }
/* 125 */               if (ret == L_ERROR.SUCCESS.getValue())
/*     */               {
/* 127 */                 if (destinationImage == null) {
/* 128 */                   destinationImage = RasterImage.createFromBitmapHandle(destBitmap);
/*     */                 }
/*     */                 else {
/* 131 */                   RasterImage tempImage = RasterImage.createFromBitmapHandle(destBitmap, true);
/* 132 */                   destinationImage.addPage(tempImage);
/* 133 */                   disposeImage(tempImage);
/*     */                 }
/* 135 */                 changedFlags[0] = (RasterImageChangedFlags.DATA | RasterImageChangedFlags.SIZE);
/*     */               }
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 141 */             ltkrn.FreeBitmapHandle(destBitmap);
/*     */           }
/*     */ 
/* 144 */           this._destinationImage = destinationImage;
/*     */         }
/*     */         finally
/*     */         {
/* 148 */           image.setPage(savePageNumber);
/*     */         }
/*     */ 
/* 151 */         if ((ret != L_ERROR.SUCCESS.getValue()) && (destinationImage != null))
/*     */         {
/* 153 */           disposeImage(destinationImage);
/* 154 */           destinationImage = null;
/*     */         }
/*     */       }
/*     */ 
/* 158 */       return ret;
/*     */     }
/*     */     finally
/*     */     {
/* 162 */       this._sourceImage = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getUseCopyStatusCallback()
/*     */   {
/* 169 */     return true;
/*     */   }
/*     */ 
/*     */   private static void disposeImage(RasterImage image)
/*     */   {
/* 174 */     image.dispose();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.CloneCommand
 * JD-Core Version:    0.6.2
 */