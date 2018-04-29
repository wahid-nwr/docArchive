/*    */ package leadtools.sane;
/*    */ 
/*    */ public class SaneConstraint
/*    */ {
/*    */   private SaneConstraintType I;
/*    */ 
/*    */   SaneConstraint(SaneConstraintType type)
/*    */   {
/* 191 */     this.I = type;
/*    */   }
/*    */ 
/*    */   public SaneConstraintType getType()
/*    */   {
/* 64 */     return this.I;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneConstraint
 * JD-Core Version:    0.6.2
 */