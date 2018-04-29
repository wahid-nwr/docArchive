/*    */ package leadtools;
/*    */ 
/*    */ public abstract class LeadtoolsException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   protected LeadtoolsException()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected LeadtoolsException(String message)
/*    */   {
/* 14 */     super(message);
/*    */   }
/*    */ 
/*    */   protected LeadtoolsException(String message, RuntimeException inner)
/*    */   {
/* 19 */     super(message, inner);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadtoolsException
 * JD-Core Version:    0.6.2
 */