/*    */ package leadtools;
/*    */ 
/*    */ public class ArgumentNullException extends LeadtoolsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private String _argumentName;
/*    */ 
/*    */   public ArgumentNullException(String argumentName)
/*    */   {
/* 11 */     super("Exception: " + argumentName + " = null");
/* 12 */     this._argumentName = argumentName;
/*    */   }
/*    */ 
/*    */   public String getArgumentName()
/*    */   {
/* 17 */     return this._argumentName;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 22 */     return super.getMessage();
/*    */   }
/*    */ 
/*    */   public String getArgumentMessage(RasterExceptionCode code)
/*    */   {
/* 27 */     return this._argumentName + " = null";
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.ArgumentNullException
 * JD-Core Version:    0.6.2
 */