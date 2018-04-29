/*    */ package leadtools.sane.server;
/*    */ 
/*    */ import leadtools.RasterImage;
/*    */ 
/*    */ public class PageImageProcessingEvent extends ImageProcessingEvent
/*    */ {
/*    */   private RasterImage _image;
/*    */   private int _pageNumber;
/*    */   private boolean _skip;
/*    */ 
/*    */   public RasterImage getImage()
/*    */   {
/* 12 */     return this._image;
/*    */   }
/*    */   void setImage(RasterImage image) {
/* 15 */     this._image = image;
/*    */   }
/*    */ 
/*    */   public int getPageNumber()
/*    */   {
/* 20 */     return this._pageNumber;
/*    */   }
/*    */   void setPageNumber(int pageNumber) {
/* 23 */     this._pageNumber = pageNumber;
/*    */   }
/*    */ 
/*    */   public boolean getSkip()
/*    */   {
/* 29 */     return this._skip;
/*    */   }
/*    */   public void setSkip(boolean skip) {
/* 32 */     this._skip = skip;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.PageImageProcessingEvent
 * JD-Core Version:    0.6.2
 */