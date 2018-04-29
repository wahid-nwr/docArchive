/*    */ package leadtools.sane;
/*    */ 
/*    */ public class SaneRangeConstraint extends SaneConstraint
/*    */ {
/*    */   private int L;
/*    */   private int D;
/*    */   private int I;
/*    */ 
/*    */   public int getMin()
/*    */   {
/* 64 */     return this.L;
/*    */   }
/*    */ 
/*    */   SaneRangeConstraint(int arg1, int arg2, int quant)
/*    */   {
/*    */   }
/*    */ 
/*    */   public int getQuant()
/*    */   {
/* 37 */     return this.I;
/*    */   }
/*    */ 
/*    */   public int getMax()
/*    */   {
/* 34 */     return this.D;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneRangeConstraint
 * JD-Core Version:    0.6.2
 */