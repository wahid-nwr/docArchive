/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.ILeadStream;
/*    */ import leadtools.LeadEvent;
/*    */ 
/*    */ public abstract class CodecsAsyncCompletedEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private RuntimeException _error;
/*    */   private boolean _cancelled;
/*    */   private Object _userState;
/*    */   ILeadStream _stream;
/*    */ 
/*    */   private CodecsAsyncCompletedEvent(Object source, RuntimeException error, boolean cancelled, Object userState)
/*    */   {
/* 16 */     super(source);
/* 17 */     this._error = error;
/* 18 */     this._cancelled = cancelled;
/* 19 */     this._userState = userState;
/*    */   }
/*    */ 
/*    */   protected CodecsAsyncCompletedEvent(Object source, ILeadStream stream, RuntimeException error, boolean cancelled, Object userState) {
/* 23 */     super(source);
/* 24 */     this._error = error;
/* 25 */     this._cancelled = cancelled;
/* 26 */     this._userState = userState;
/* 27 */     this._stream = stream;
/*    */   }
/*    */ 
/*    */   public ILeadStream getStream() {
/* 31 */     return this._stream;
/*    */   }
/*    */ 
/*    */   public RuntimeException getError()
/*    */   {
/* 36 */     return this._error;
/*    */   }
/*    */ 
/*    */   public boolean getCancelled()
/*    */   {
/* 41 */     return this._cancelled;
/*    */   }
/*    */ 
/*    */   public Object getUserState()
/*    */   {
/* 46 */     return this._userState;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsAsyncCompletedEvent
 * JD-Core Version:    0.6.2
 */