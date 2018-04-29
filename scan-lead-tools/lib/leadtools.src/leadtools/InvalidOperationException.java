/*    */ package leadtools;
/*    */ 
/*    */ public class InvalidOperationException extends LeadtoolsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private String _operationName;
/*    */ 
/*    */   public InvalidOperationException(String operationName)
/*    */   {
/* 11 */     super("Invalid operation: " + operationName);
/* 12 */     this._operationName = operationName;
/*    */   }
/*    */ 
/*    */   public String getOperationName()
/*    */   {
/* 17 */     return this._operationName;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 22 */     return super.getMessage();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.InvalidOperationException
 * JD-Core Version:    0.6.2
 */