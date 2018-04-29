/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.ILeadStream;
/*    */ import leadtools.RasterImage;
/*    */ 
/*    */ public class CodecsLoadAsyncCompletedEvent extends CodecsAsyncCompletedEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private RasterImage _image;
/*    */ 
/*    */   protected CodecsLoadAsyncCompletedEvent(Object source, RasterImage image, ILeadStream stream, RuntimeException error, boolean cancelled, Object userState)
/*    */   {
/* 12 */     super(source, stream, error, cancelled, userState);
/* 13 */     this._image = image;
/*    */   }
/*    */ 
/*    */   public RasterImage getImage()
/*    */   {
/* 19 */     return this._image;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsLoadAsyncCompletedEvent
 * JD-Core Version:    0.6.2
 */