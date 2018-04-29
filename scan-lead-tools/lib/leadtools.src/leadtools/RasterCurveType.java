/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterCurveType
/*    */ {
/*  5 */   BEZIER(0), 
/*  6 */   STANDARD(1);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterCurveType> mappings;
/*    */ 
/*    */   private static HashMap<Integer, RasterCurveType> getMappings() {
/* 13 */     if (mappings == null)
/*    */     {
/* 15 */       synchronized (RasterCurveType.class)
/*    */       {
/* 17 */         if (mappings == null)
/*    */         {
/* 19 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 23 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterCurveType(int value)
/*    */   {
/* 28 */     this.intValue = value;
/* 29 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 34 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterCurveType forValue(int value)
/*    */   {
/* 39 */     return (RasterCurveType)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterCurveType
 * JD-Core Version:    0.6.2
 */