/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterRegionCombineMode
/*    */ {
/*  5 */   AND(0), 
/*  6 */   SET(1), 
/*  7 */   AND_NOT_IMAGE(2), 
/*  8 */   AND_NOT_REGION(3), 
/*  9 */   OR(4), 
/* 10 */   XOR(5), 
/* 11 */   SET_NOT(6);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterRegionCombineMode> mappings;
/*    */ 
/* 17 */   private static HashMap<Integer, RasterRegionCombineMode> getMappings() { if (mappings == null)
/*    */     {
/* 19 */       synchronized (RasterRegionCombineMode.class)
/*    */       {
/* 21 */         if (mappings == null)
/*    */         {
/* 23 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 27 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterRegionCombineMode(int value)
/*    */   {
/* 32 */     this.intValue = value;
/* 33 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 38 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterRegionCombineMode forValue(int value)
/*    */   {
/* 43 */     return (RasterRegionCombineMode)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterRegionCombineMode
 * JD-Core Version:    0.6.2
 */