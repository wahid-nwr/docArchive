/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.ArgumentNullException;
/*    */ import leadtools.ILeadStream;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageFormat;
/*    */ 
/*    */ final class SaveParams
/*    */ {
/*    */   public RasterImage Image;
/*    */   public ILeadStream Stream;
/*    */   public RasterImageFormat Format;
/*    */   public int BitsPerPixel;
/*    */   public int FirstPage;
/*    */   public int LastPage;
/*    */   public int FirstSavePageNumber;
/*    */   public int OriginalFirstSavePageNumber;
/*    */   public CodecsSavePageMode PageMode;
/*    */   public boolean UseOffset;
/*    */   public long Offset;
/*    */   public boolean UseStamp;
/*    */ 
/*    */   public SaveParams(RasterImage image, ILeadStream stream, RasterImageFormat format, int bitsPerPixel, int firstPage, int lastPage, int firstSavePageNumber, CodecsSavePageMode pageMode)
/*    */   {
/* 25 */     init(image, stream, format, bitsPerPixel, firstPage, lastPage, firstSavePageNumber, pageMode);
/*    */   }
/*    */ 
/*    */   public SaveParams(RasterImage image, ILeadStream stream, RasterImageFormat format, int bitsPerPixel, int firstPage, int lastPage, int firstSavePageNumber, CodecsSavePageMode pageMode, boolean useStamp) {
/* 29 */     init(image, stream, format, bitsPerPixel, firstPage, lastPage, firstSavePageNumber, pageMode);
/* 30 */     this.UseStamp = useStamp;
/*    */   }
/*    */ 
/*    */   public SaveParams(RasterImage image, ILeadStream stream, long offset, RasterImageFormat format, int bitsPerPixel, int firstPage, int lastPage, int firstSavePageNumber, CodecsSavePageMode pageMode)
/*    */   {
/* 35 */     init(image, stream, format, bitsPerPixel, firstPage, lastPage, firstSavePageNumber, pageMode);
/* 36 */     this.UseOffset = true;
/* 37 */     this.Offset = offset;
/*    */   }
/*    */ 
/*    */   void init(RasterImage image, ILeadStream stream, RasterImageFormat format, int bitsPerPixel, int firstPage, int lastPage, int firstSavePageNumber, CodecsSavePageMode pageMode)
/*    */   {
/* 42 */     if (image == null) {
/* 43 */       throw new ArgumentNullException("image");
/*    */     }
/* 45 */     if (stream == null) {
/* 46 */       throw new ArgumentNullException("stream");
/*    */     }
/* 48 */     this.Image = image;
/* 49 */     this.Stream = stream;
/* 50 */     this.Format = format;
/* 51 */     this.BitsPerPixel = bitsPerPixel;
/* 52 */     this.FirstPage = firstPage;
/* 53 */     this.LastPage = lastPage;
/* 54 */     this.FirstSavePageNumber = firstSavePageNumber;
/* 55 */     this.OriginalFirstSavePageNumber = firstSavePageNumber;
/* 56 */     this.PageMode = pageMode;
/* 57 */     this.UseStamp = false;
/*    */   }
/*    */ 
/*    */   private SaveParams()
/*    */   {
/*    */   }
/*    */ 
/*    */   public SaveParams clone() {
/* 65 */     SaveParams varCopy = new SaveParams();
/*    */ 
/* 67 */     varCopy.Image = this.Image;
/* 68 */     varCopy.Stream = this.Stream;
/* 69 */     varCopy.Format = this.Format;
/* 70 */     varCopy.BitsPerPixel = this.BitsPerPixel;
/* 71 */     varCopy.FirstPage = this.FirstPage;
/* 72 */     varCopy.LastPage = this.LastPage;
/* 73 */     varCopy.FirstSavePageNumber = this.FirstSavePageNumber;
/* 74 */     varCopy.OriginalFirstSavePageNumber = this.OriginalFirstSavePageNumber;
/* 75 */     varCopy.PageMode = this.PageMode;
/* 76 */     varCopy.UseOffset = this.UseOffset;
/* 77 */     varCopy.Offset = this.Offset;
/* 78 */     varCopy.UseStamp = this.UseStamp;
/*    */ 
/* 80 */     return varCopy;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.SaveParams
 * JD-Core Version:    0.6.2
 */