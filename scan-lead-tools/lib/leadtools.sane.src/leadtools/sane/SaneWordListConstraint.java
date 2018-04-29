/*    */ package leadtools.sane;
/*    */ 
/*    */ public class SaneWordListConstraint extends SaneConstraint
/*    */ {
/* 59 */   private int[] I = null;
/*    */ 
/* 59 */   SaneWordListConstraint(int[] value) { super(SaneConstraintType.WORD_LIST); this.I = value;
/*    */   }
/*    */ 
/*    */   public int[] getValue()
/*    */   {
/* 64 */     return this.I;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneWordListConstraint
 * JD-Core Version:    0.6.2
 */