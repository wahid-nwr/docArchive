/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterByteOrder
/*    */ {
/*  5 */   RGB(0), 
/*  6 */   BGR(1), 
/*  7 */   GRAY(2), 
/*  8 */   ROMM(5), 
/*  9 */   RGB_565(7);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterByteOrder> mappings;
/*    */ 
/* 15 */   private static HashMap<Integer, RasterByteOrder> getMappings() { if (mappings == null)
/*    */     {
/* 17 */       synchronized (RasterByteOrder.class)
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
/*    */   private RasterByteOrder(int value)
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
/*    */   public static RasterByteOrder forValue(int value)
/*    */   {
/* 41 */     return (RasterByteOrder)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterByteOrder
 * JD-Core Version:    0.6.2
 */