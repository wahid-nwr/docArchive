/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterGetSetOverlayImageMode
/*    */ {
/*  5 */   COPY(0), 
/*  6 */   NO_COPY(1), 
/*  7 */   MOVE(3);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterGetSetOverlayImageMode> mappings;
/*    */ 
/* 13 */   private static HashMap<Integer, RasterGetSetOverlayImageMode> getMappings() { if (mappings == null)
/*    */     {
/* 15 */       synchronized (RasterGetSetOverlayImageMode.class)
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
/*    */   private RasterGetSetOverlayImageMode(int value)
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
/*    */   public static RasterGetSetOverlayImageMode forValue(int value)
/*    */   {
/* 39 */     return (RasterGetSetOverlayImageMode)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterGetSetOverlayImageMode
 * JD-Core Version:    0.6.2
 */