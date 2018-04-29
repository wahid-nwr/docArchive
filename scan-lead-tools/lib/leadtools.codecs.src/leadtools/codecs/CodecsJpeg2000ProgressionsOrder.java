/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsJpeg2000ProgressionsOrder
/*    */ {
/*  5 */   LAYER_RESOLUTION_COMPONENT_POSITION(0), 
/*  6 */   RESOLUTION_LAYER_COMPONENT_POSITION(1), 
/*  7 */   RESOLUTION_POSITION_COMPONENT_LAYER(2), 
/*  8 */   POSITION_COMPONENT_RESOLUTION_LAYER(3), 
/*  9 */   COMPONENT_POSITION_RESOLUTION_LAYER(4);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsJpeg2000ProgressionsOrder> mappings;
/*    */ 
/* 15 */   private static HashMap<Integer, CodecsJpeg2000ProgressionsOrder> getMappings() { if (mappings == null)
/*    */     {
/* 17 */       synchronized (CodecsJpeg2000ProgressionsOrder.class)
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
/*    */   private CodecsJpeg2000ProgressionsOrder(int value)
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
/*    */   public static CodecsJpeg2000ProgressionsOrder forValue(int value)
/*    */   {
/* 41 */     return (CodecsJpeg2000ProgressionsOrder)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpeg2000ProgressionsOrder
 * JD-Core Version:    0.6.2
 */