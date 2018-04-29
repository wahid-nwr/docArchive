/*    */ package leadtools;
/*    */ 
/*    */ public class ArgumentOutOfRangeException extends LeadtoolsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int _argumentValue;
/*  8 */   private String _argumentValueAsString = null;
/*    */   private String _argumentName;
/*    */ 
/*    */   public ArgumentOutOfRangeException(String argumentName, int argumentValue, String message)
/*    */   {
/* 13 */     super(message + " " + argumentName + " = " + argumentValue);
/* 14 */     this._argumentValue = argumentValue;
/* 15 */     this._argumentName = argumentName;
/*    */   }
/*    */ 
/*    */   public ArgumentOutOfRangeException(String argumentName, String argumentValue, String message)
/*    */   {
/* 20 */     super(message + " " + argumentName + " = " + argumentValue);
/* 21 */     this._argumentValue = 0;
/* 22 */     this._argumentValueAsString = argumentValue;
/* 23 */     this._argumentName = argumentName;
/*    */   }
/*    */ 
/*    */   public String getArgumentName()
/*    */   {
/* 28 */     return this._argumentName;
/*    */   }
/*    */ 
/*    */   public void setArgumentName(String argumentName)
/*    */   {
/* 33 */     this._argumentName = argumentName;
/*    */   }
/*    */ 
/*    */   public int getArgumentValue()
/*    */   {
/* 38 */     return this._argumentValue;
/*    */   }
/*    */ 
/*    */   public void setArgumentValue(int argumentValue)
/*    */   {
/* 43 */     this._argumentValue = argumentValue;
/*    */   }
/*    */ 
/*    */   public String getArgumentValueAsString()
/*    */   {
/* 48 */     return this._argumentValueAsString != null ? this._argumentValueAsString : Integer.toString(this._argumentValue);
/*    */   }
/*    */ 
/*    */   public void setArgumentValueAsString(String argumentValue)
/*    */   {
/* 53 */     this._argumentValueAsString = argumentValue;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 58 */     return super.getMessage();
/*    */   }
/*    */ 
/*    */   public String getArgumentMessage(RasterExceptionCode code)
/*    */   {
/* 63 */     return this._argumentName + " = " + this._argumentValue;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.ArgumentOutOfRangeException
 * JD-Core Version:    0.6.2
 */