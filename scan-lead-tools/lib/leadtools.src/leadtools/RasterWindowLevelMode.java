/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterWindowLevelMode
/*    */ {
/*  5 */   PAINT(0), 
/*  6 */   PAINT_AND_PROCESSING(1);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterWindowLevelMode> mappings;
/*    */ 
/* 12 */   private static HashMap<Integer, RasterWindowLevelMode> getMappings() { if (mappings == null)
/*    */     {
/* 14 */       synchronized (RasterWindowLevelMode.class)
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
/*    */   private RasterWindowLevelMode(int value)
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
/*    */   public static RasterWindowLevelMode forValue(int value)
/*    */   {
/* 38 */     return (RasterWindowLevelMode)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterWindowLevelMode
 * JD-Core Version:    0.6.2
 */