/*    */ package leadtools;
/*    */ 
/*    */ public class RasterExceptionMessage
/*    */ {
/*    */   public RasterExceptionCode code;
/*    */   public String message;
/*    */ 
/*    */   public RasterExceptionMessage(RasterExceptionCode code, String message)
/*    */   {
/* 10 */     this.code = code;
/* 11 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public RasterExceptionMessage clone()
/*    */   {
/* 16 */     RasterExceptionMessage varCopy = new RasterExceptionMessage(this.code, this.message);
/* 17 */     return varCopy;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterExceptionMessage
 * JD-Core Version:    0.6.2
 */