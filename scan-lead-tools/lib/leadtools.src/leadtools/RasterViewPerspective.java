/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterViewPerspective
/*    */ {
/*  5 */   TOP_LEFT(1), 
/*  6 */   BOTTOM_LEFT(4), 
/*  7 */   TOP_RIGHT(2), 
/*  8 */   BOTTOM_LEFT_180(2), 
/*  9 */   BOTTOM_RIGHT(3), 
/* 10 */   TOP_LEFT_180(3), 
/* 11 */   RIGHT_TOP(6), 
/* 12 */   TOP_LEFT_90(6), 
/* 13 */   LEFT_BOTTOM(8), 
/* 14 */   TOP_LEFT_270(8), 
/* 15 */   LEFT_TOP(5), 
/* 16 */   BOTTOM_LEFT_90(5), 
/* 17 */   RIGHT_BOTTOM(7), 
/* 18 */   BOTTOM_LEFT_270(7);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterViewPerspective> mappings;
/*    */ 
/* 24 */   private static HashMap<Integer, RasterViewPerspective> getMappings() { if (mappings == null)
/*    */     {
/* 26 */       synchronized (RasterViewPerspective.class)
/*    */       {
/* 28 */         if (mappings == null)
/*    */         {
/* 30 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 34 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterViewPerspective(int value)
/*    */   {
/* 39 */     this.intValue = value;
/* 40 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 45 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterViewPerspective forValue(int value)
/*    */   {
/* 50 */     return (RasterViewPerspective)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterViewPerspective
 * JD-Core Version:    0.6.2
 */