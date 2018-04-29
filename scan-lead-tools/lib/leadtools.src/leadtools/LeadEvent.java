/*    */ package leadtools;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public class LeadEvent extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = -1503592614724867725L;
/*    */ 
/*    */   public static LeadEvent getEmpty(Object source)
/*    */   {
/* 14 */     return new LeadEvent(source);
/*    */   }
/*    */ 
/*    */   public LeadEvent(Object source) {
/* 18 */     super(source);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadEvent
 * JD-Core Version:    0.6.2
 */