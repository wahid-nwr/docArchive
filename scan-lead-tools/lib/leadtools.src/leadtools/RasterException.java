/*    */ package leadtools;
/*    */ 
/*    */ public class RasterException extends LeadtoolsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private RasterExceptionCode _code;
/*    */   private boolean _userMessage;
/*    */ 
/*    */   public RasterException()
/*    */   {
/* 13 */     this._code = RasterExceptionCode.SUCCESS;
/* 14 */     this._userMessage = false;
/*    */   }
/*    */ 
/*    */   public RasterException(String message)
/*    */   {
/* 19 */     super(message);
/* 20 */     this._code = RasterExceptionCode.SUCCESS;
/* 21 */     this._userMessage = true;
/*    */   }
/*    */ 
/*    */   public RasterException(String message, RuntimeException inner)
/*    */   {
/* 26 */     super(message, inner);
/* 27 */     this._code = RasterExceptionCode.SUCCESS;
/* 28 */     this._userMessage = true;
/*    */   }
/*    */ 
/*    */   public RasterException(String message, RasterExceptionCode code)
/*    */   {
/* 33 */     super(message);
/* 34 */     this._code = (code != null ? code : RasterExceptionCode.FAILURE);
/* 35 */     this._userMessage = true;
/*    */   }
/*    */ 
/*    */   public RasterException(RasterExceptionCode code)
/*    */   {
/* 41 */     this._code = (code != null ? code : RasterExceptionCode.FAILURE);
/* 42 */     this._userMessage = false;
/*    */   }
/*    */ 
/*    */   public RasterExceptionCode getCode()
/*    */   {
/* 47 */     return this._code;
/*    */   }
/*    */ 
/*    */   public void setCode(RasterExceptionCode value)
/*    */   {
/* 52 */     this._code = (value != null ? value : RasterExceptionCode.FAILURE);
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 57 */     if (this._userMessage) {
/* 58 */       return super.getMessage();
/*    */     }
/* 60 */     return getCodeMessage(this._code);
/*    */   }
/*    */ 
/*    */   public static String getCodeMessage(RasterExceptionCode code)
/*    */   {
/* 65 */     RasterExceptionCode toCheck = code != null ? code : RasterExceptionCode.FAILURE;
/*    */ 
/* 67 */     for (int i = 0; i < RasterExceptionMessages.Messages.length; i++) {
/* 68 */       if (RasterExceptionMessages.Messages[i].code == toCheck) {
/* 69 */         return RasterExceptionMessages.Messages[i].message;
/*    */       }
/*    */     }
/* 72 */     return String.format("%d", new Object[] { Integer.valueOf(toCheck.getValue()) });
/*    */   }
/*    */ 
/*    */   public static void checkErrorCode(int code)
/*    */   {
/* 77 */     if (code < 1)
/* 78 */       throw new RasterException(RasterExceptionCode.forValue(code));
/*    */   }
/*    */ 
/*    */   public static void checkErrorCode(RasterExceptionCode code)
/*    */   {
/* 83 */     if ((code != null) && (!code.equals(L_ERROR.SUCCESS)))
/* 84 */       throw new RasterException(code);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterException
 * JD-Core Version:    0.6.2
 */