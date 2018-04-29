/*    */ package leadtools.sane;
/*    */ 
/*    */ public class SaneStringListConstraint extends SaneConstraint
/*    */ {
/* 59 */   private String[] I = null;
/*    */ 
/*    */   public String[] getValue()
/*    */   {
/* 64 */     return this.I;
/*    */   }
/*    */ 
/*    */   SaneStringListConstraint(String[] value)
/*    */   {
/* 59 */     super(SaneConstraintType.STRING_LIST); this.I = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneStringListConstraint
 * JD-Core Version:    0.6.2
 */