/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum LEADResourceDirectory
/*    */ {
/*  4 */   LIBRARIES(0), 
/*  5 */   FONTS(1);
/*    */ 
/*    */   private int mIntValue;
/*    */   private static HashMap<Integer, LEADResourceDirectory> mappings;
/*    */ 
/* 11 */   private static HashMap<Integer, LEADResourceDirectory> getMappings() { if (mappings == null) {
/* 12 */       synchronized (LEADResourceDirectory.class) {
/* 13 */         if (mappings == null) {
/* 14 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 18 */     return mappings; }
/*    */ 
/*    */   private LEADResourceDirectory(int value)
/*    */   {
/* 22 */     this.mIntValue = value;
/* 23 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue() {
/* 27 */     return this.mIntValue;
/*    */   }
/*    */ 
/*    */   public static LEADResourceDirectory forValue(int value) {
/* 31 */     return (LEADResourceDirectory)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LEADResourceDirectory
 * JD-Core Version:    0.6.2
 */