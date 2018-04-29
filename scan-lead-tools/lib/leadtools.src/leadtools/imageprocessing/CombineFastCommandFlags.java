/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CombineFastCommandFlags
/*    */ {
/*  8 */   NONE(0), 
/*  9 */   SOURCE_NOT(1), 
/* 10 */   SOURCE_0(2), 
/* 11 */   SOURCE_1(3), 
/* 12 */   DESTINATION_NOT(16), 
/* 13 */   DESTINATION_0(32), 
/* 14 */   DESTINATION_1(48), 
/* 15 */   OPERATION_OR(256), 
/* 16 */   OPERATION_XOR(512), 
/* 17 */   OPERATION_ADD(768), 
/* 18 */   OPERATION_SUBTRACT_SOURCE(1024), 
/* 19 */   OPERATION_SUBTRACT_DESTINATION(1280), 
/* 20 */   OPERATION_MULTIPLY(1536), 
/* 21 */   OPERATION_DIVIDE_SOURCE(1792), 
/* 22 */   OPERATION_DIVIDE_DESTINATION(2048), 
/* 23 */   OPERATION_AVERAGE(2304), 
/* 24 */   OPERATION_MINIMUM(2560), 
/* 25 */   OPERATION_MAXIMUM(2816), 
/* 26 */   RESULT_NOT(4096), 
/* 27 */   RESULT_0(8192), 
/* 28 */   RESULT_1(12288), 
/* 29 */   SOURCE_COPY(288);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CombineFastCommandFlags> mappings;
/*    */ 
/*    */   private static HashMap<Integer, CombineFastCommandFlags> getMappings() {
/* 36 */     if (mappings == null)
/*    */     {
/* 38 */       synchronized (CombineFastCommandFlags.class)
/*    */       {
/* 40 */         if (mappings == null)
/*    */         {
/* 42 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 46 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CombineFastCommandFlags(int value)
/*    */   {
/* 51 */     this.intValue = value;
/* 52 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 57 */     return this.intValue;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.CombineFastCommandFlags
 * JD-Core Version:    0.6.2
 */