/*    */ package leadtools.sane.server;
/*    */ 
/*    */ class HttpResponse
/*    */ {
/*    */   private byte[] _response;
/*    */   private String _responseType;
/*    */ 
/*    */   public HttpResponse()
/*    */   {
/* 15 */     this._response = "".getBytes();
/* 16 */     this._responseType = "text/plain";
/*    */   }
/*    */ 
/*    */   byte[] getReponse()
/*    */   {
/* 22 */     return this._response;
/*    */   }
/*    */   void setResponse(byte[] response) {
/* 25 */     this._response = response;
/*    */   }
/*    */ 
/*    */   String getReponseType()
/*    */   {
/* 30 */     return this._responseType;
/*    */   }
/*    */   void setResponseType(String newValue) {
/* 33 */     this._responseType = newValue;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.HttpResponse
 * JD-Core Version:    0.6.2
 */