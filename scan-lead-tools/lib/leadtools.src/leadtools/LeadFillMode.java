/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum LeadFillMode
/*    */ {
/*  5 */   ALTERNATE(1), 
/*  6 */   WINDING(0);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, LeadFillMode> mappings;
/*    */ 
/* 12 */   private static HashMap<Integer, LeadFillMode> getMappings() { if (mappings == null)
/*    */     {
/* 14 */       synchronized (LeadFillMode.class)
/*    */       {
/* 16 */         if (mappings == null)
/*    */         {
/* 18 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 22 */     return mappings;
/*    */   }
/*    */ 
/*    */   private LeadFillMode(int value)
/*    */   {
/* 27 */     this.intValue = value;
/* 28 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 33 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static LeadFillMode forValue(int value)
/*    */   {
/* 38 */     return (LeadFillMode)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadFillMode
 * JD-Core Version:    0.6.2
 */