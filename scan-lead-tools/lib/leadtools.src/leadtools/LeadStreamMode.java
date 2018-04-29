/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum LeadStreamMode
/*    */ {
/*  4 */   OPEN(0), 
/*  5 */   CREATE(1), 
/*  6 */   TRUNCATE(2);
/*    */ 
/*    */   private int mIntValue;
/*    */   private static HashMap<Integer, LeadStreamMode> mappings;
/*    */ 
/* 12 */   private static HashMap<Integer, LeadStreamMode> getMappings() { if (mappings == null) {
/* 13 */       synchronized (LeadStreamMode.class) {
/* 14 */         if (mappings == null) {
/* 15 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 19 */     return mappings; }
/*    */ 
/*    */   private LeadStreamMode(int value)
/*    */   {
/* 23 */     this.mIntValue = value;
/* 24 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue() {
/* 28 */     return this.mIntValue;
/*    */   }
/*    */ 
/*    */   public static LeadStreamMode forValue(int value) {
/* 32 */     return (LeadStreamMode)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadStreamMode
 * JD-Core Version:    0.6.2
 */