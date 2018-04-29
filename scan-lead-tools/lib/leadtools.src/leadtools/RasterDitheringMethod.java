/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterDitheringMethod
/*    */ {
/*  5 */   NONE(0), 
/*  6 */   FLOYD_STEIN(1), 
/*  7 */   STUCKI(2), 
/*  8 */   BURKES(3), 
/*  9 */   SIERRA(4), 
/* 10 */   STEVENSON_ARCE(5), 
/* 11 */   JARVIS(6), 
/* 12 */   ORDERED(7), 
/* 13 */   CLUSTERED(8);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterDitheringMethod> mappings;
/*    */ 
/* 19 */   private static HashMap<Integer, RasterDitheringMethod> getMappings() { if (mappings == null)
/*    */     {
/* 21 */       synchronized (RasterDitheringMethod.class)
/*    */       {
/* 23 */         if (mappings == null)
/*    */         {
/* 25 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 29 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterDitheringMethod(int value)
/*    */   {
/* 34 */     this.intValue = value;
/* 35 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 40 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterDitheringMethod forValue(int value)
/*    */   {
/* 45 */     return (RasterDitheringMethod)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterDitheringMethod
 * JD-Core Version:    0.6.2
 */