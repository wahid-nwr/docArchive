/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterPaintSizeMode
/*    */ {
/*  5 */   NORMAL(0), 
/*  6 */   FIT(1), 
/*  7 */   FIT_ALWAYS(2), 
/*  8 */   FIT_WIDTH(3), 
/*  9 */   STRETCH(4);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterPaintSizeMode> mappings;
/*    */ 
/* 15 */   private static HashMap<Integer, RasterPaintSizeMode> getMappings() { if (mappings == null)
/*    */     {
/* 17 */       synchronized (RasterPaintSizeMode.class)
/*    */       {
/* 19 */         if (mappings == null)
/*    */         {
/* 21 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 25 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterPaintSizeMode(int value)
/*    */   {
/* 30 */     this.intValue = value;
/* 31 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 36 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterPaintSizeMode forValue(int value)
/*    */   {
/* 41 */     return (RasterPaintSizeMode)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterPaintSizeMode
 * JD-Core Version:    0.6.2
 */