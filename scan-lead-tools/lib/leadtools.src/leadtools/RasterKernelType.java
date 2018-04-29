/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterKernelType
/*    */ {
/*  5 */   RELEASE(0), 
/*  6 */   EVALUATION(1), 
/*  7 */   NAG(2);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterKernelType> mappings;
/*    */ 
/* 13 */   private static HashMap<Integer, RasterKernelType> getMappings() { if (mappings == null)
/*    */     {
/* 15 */       synchronized (RasterKernelType.class)
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
/*    */   private RasterKernelType(int value)
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
/*    */   public static RasterKernelType forValue(int value)
/*    */   {
/* 39 */     return (RasterKernelType)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterKernelType
 * JD-Core Version:    0.6.2
 */