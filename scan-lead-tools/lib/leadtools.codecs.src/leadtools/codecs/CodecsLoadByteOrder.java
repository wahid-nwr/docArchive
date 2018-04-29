/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsLoadByteOrder
/*    */ {
/*  5 */   RGB(0), 
/*  6 */   BGR(1), 
/*  7 */   GRAY(2), 
/*  8 */   RGB_OR_GRAY(3), 
/*  9 */   BGR_OR_GRAY(4), 
/* 10 */   ROMM(5), 
/* 11 */   BGR_OR_GRAY_OR_ROMM(6), 
/* 12 */   RGB_565(7);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsLoadByteOrder> mappings;
/*    */ 
/* 18 */   private static HashMap<Integer, CodecsLoadByteOrder> getMappings() { if (mappings == null)
/*    */     {
/* 20 */       synchronized (CodecsLoadByteOrder.class)
/*    */       {
/* 22 */         if (mappings == null)
/*    */         {
/* 24 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 28 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsLoadByteOrder(int value)
/*    */   {
/* 33 */     this.intValue = value;
/* 34 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 39 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsLoadByteOrder forValue(int value)
/*    */   {
/* 44 */     return (CodecsLoadByteOrder)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsLoadByteOrder
 * JD-Core Version:    0.6.2
 */