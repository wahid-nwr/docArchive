/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterGrayscaleMode
/*    */ {
/*  5 */   NONE(0), 
/*  6 */   ORDERED_NORMAL(1), 
/*  7 */   ORDERED_INVERSE(2), 
/*  8 */   NOT_ORDERED(3);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterGrayscaleMode> mappings;
/*    */ 
/* 14 */   private static HashMap<Integer, RasterGrayscaleMode> getMappings() { if (mappings == null)
/*    */     {
/* 16 */       synchronized (RasterGrayscaleMode.class)
/*    */       {
/* 18 */         if (mappings == null)
/*    */         {
/* 20 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 24 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterGrayscaleMode(int value)
/*    */   {
/* 29 */     this.intValue = value;
/* 30 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 35 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterGrayscaleMode forValue(int value)
/*    */   {
/* 40 */     return (RasterGrayscaleMode)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterGrayscaleMode
 * JD-Core Version:    0.6.2
 */