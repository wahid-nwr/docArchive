/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.ILeadStream;
/*    */ import leadtools.ISvgDocument;
/*    */ 
/*    */ public class CodecsLoadSvgAsyncCompletedEvent extends CodecsAsyncCompletedEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private ISvgDocument _document;
/*    */ 
/*    */   protected CodecsLoadSvgAsyncCompletedEvent(Object source, ISvgDocument document, ILeadStream stream, RuntimeException error, boolean cancelled, Object userState)
/*    */   {
/* 12 */     super(source, stream, error, cancelled, userState);
/* 13 */     this._document = document;
/*    */   }
/*    */ 
/*    */   public ISvgDocument getDocument()
/*    */   {
/* 19 */     return this._document;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsLoadSvgAsyncCompletedEvent
 * JD-Core Version:    0.6.2
 */