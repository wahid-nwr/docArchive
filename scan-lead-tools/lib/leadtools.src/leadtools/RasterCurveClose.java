/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterCurveClose
/*    */ {
/*  5 */   NO_CLOSE(0), 
/*  6 */   CLOSE(1), 
/*  7 */   PARTIAL_CLOSE(2);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterCurveClose> mappings;
/*    */ 
/* 13 */   private static HashMap<Integer, RasterCurveClose> getMappings() { if (mappings == null)
/*    */     {
/* 15 */       synchronized (RasterCurveClose.class)
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
/*    */   private RasterCurveClose(int value)
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
/*    */   public static RasterCurveClose forValue(int value)
/*    */   {
/* 39 */     return (RasterCurveClose)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterCurveClose
 * JD-Core Version:    0.6.2
 */