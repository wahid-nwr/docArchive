/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterPaintAlignMode
/*    */ {
/*  5 */   NEAR(0), 
/*  6 */   CENTER(1), 
/*  7 */   CENTER_ALWAYS(2), 
/*  8 */   FAR(3);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterPaintAlignMode> mappings;
/*    */ 
/* 14 */   private static HashMap<Integer, RasterPaintAlignMode> getMappings() { if (mappings == null)
/*    */     {
/* 16 */       synchronized (RasterPaintAlignMode.class)
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
/*    */   private RasterPaintAlignMode(int value)
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
/*    */   public static RasterPaintAlignMode forValue(int value)
/*    */   {
/* 40 */     return (RasterPaintAlignMode)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterPaintAlignMode
 * JD-Core Version:    0.6.2
 */