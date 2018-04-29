/*    */ package leadtools;
/*    */ 
/*    */ public class AsyncCompletedEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private RuntimeException _error;
/*    */   private boolean _cancelled;
/*    */   private Object _userState;
/*    */ 
/*    */   public AsyncCompletedEvent(Object source, RuntimeException error, boolean cancelled, Object userState)
/*    */   {
/* 14 */     super(source);
/* 15 */     this._error = error;
/* 16 */     this._cancelled = cancelled;
/* 17 */     this._userState = userState;
/*    */   }
/*    */ 
/*    */   public RuntimeException getError()
/*    */   {
/* 22 */     return this._error;
/*    */   }
/*    */ 
/*    */   public void setError(RuntimeException error) {
/* 26 */     this._error = error;
/*    */   }
/*    */ 
/*    */   public boolean getCancelled()
/*    */   {
/* 31 */     return this._cancelled;
/*    */   }
/*    */ 
/*    */   public void setCancelled(boolean cancelled) {
/* 35 */     this._cancelled = cancelled;
/*    */   }
/*    */ 
/*    */   public Object getUserState()
/*    */   {
/* 40 */     return this._userState;
/*    */   }
/*    */ 
/*    */   public void setUserState(Object userState) {
/* 44 */     this._userState = userState;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.AsyncCompletedEvent
 * JD-Core Version:    0.6.2
 */