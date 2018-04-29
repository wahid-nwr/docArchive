/*    */ package leadtools.sane.server;
/*    */ 
/*    */ public class SaneError
/*    */ {
/*    */   private int _code;
/*    */   private String _message;
/*    */ 
/*    */   SaneError()
/*    */   {
/*  7 */     this._code = 0;
/*  8 */     this._message = "";
/*    */   }
/*    */ 
/*    */   public int getCode()
/*    */   {
/* 13 */     return this._code;
/*    */   }
/*    */   void setCode(int code) {
/* 16 */     this._code = code;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 21 */     return this._message;
/*    */   }
/*    */   void setMessage(String message) {
/* 24 */     this._message = message;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.SaneError
 * JD-Core Version:    0.6.2
 */