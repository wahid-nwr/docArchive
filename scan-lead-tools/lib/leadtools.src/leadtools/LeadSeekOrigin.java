/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum LeadSeekOrigin
/*    */ {
/*  4 */   BEGIN(0), 
/*  5 */   CURRENT(1), 
/*  6 */   END(2);
/*    */ 
/*    */   private int mIntValue;
/*    */   private static HashMap<Integer, LeadSeekOrigin> mappings;
/*    */ 
/* 12 */   private static HashMap<Integer, LeadSeekOrigin> getMappings() { if (mappings == null) {
/* 13 */       synchronized (LeadSeekOrigin.class) {
/* 14 */         if (mappings == null) {
/* 15 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 19 */     return mappings; }
/*    */ 
/*    */   private LeadSeekOrigin(int value)
/*    */   {
/* 23 */     this.mIntValue = value;
/* 24 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue() {
/* 28 */     return this.mIntValue;
/*    */   }
/*    */ 
/*    */   public static LeadSeekOrigin forValue(int value) {
/* 32 */     return (LeadSeekOrigin)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadSeekOrigin
 * JD-Core Version:    0.6.2
 */