/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.LeadEvent;
/*    */ 
/*    */ public class CodecsRedirectOpenEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private String _fileName;
/*    */   private String _mode;
/*    */   private boolean _truncate;
/*    */   private boolean _success;
/*    */ 
/*    */   CodecsRedirectOpenEvent(Object source)
/*    */   {
/* 22 */     super(source);
/* 23 */     this._fileName = null;
/* 24 */     this._mode = "r";
/*    */ 
/* 27 */     this._success = true;
/*    */   }
/*    */ 
/*    */   final void init(String fileName, String mode, boolean bTruncate) {
/* 31 */     this._fileName = fileName;
/* 32 */     this._mode = mode;
/* 33 */     this._truncate = bTruncate;
/*    */   }
/*    */ 
/*    */   public String getFileName()
/*    */   {
/* 40 */     return this._fileName;
/*    */   }
/*    */ 
/*    */   public String getMode() {
/* 44 */     return this._mode;
/*    */   }
/*    */ 
/*    */   public boolean getTruncate()
/*    */   {
/* 58 */     return this._truncate;
/*    */   }
/*    */ 
/*    */   public void setTruncate(boolean value) {
/* 62 */     this._truncate = value;
/*    */   }
/*    */ 
/*    */   public boolean getSuccess()
/*    */   {
/* 67 */     return this._success;
/*    */   }
/*    */ 
/*    */   public void setSuccess(boolean value) {
/* 71 */     this._success = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRedirectOpenEvent
 * JD-Core Version:    0.6.2
 */