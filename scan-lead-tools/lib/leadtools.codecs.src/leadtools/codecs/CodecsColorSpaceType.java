/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsColorSpaceType
/*    */ {
/*  5 */   BGR(0), 
/*  6 */   YUV(1), 
/*  7 */   CMYK(2), 
/*  8 */   CIELAB(3);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsColorSpaceType> mappings;
/*    */ 
/* 14 */   private static HashMap<Integer, CodecsColorSpaceType> getMappings() { if (mappings == null)
/*    */     {
/* 16 */       synchronized (CodecsColorSpaceType.class)
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
/*    */   private CodecsColorSpaceType(int value)
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
/*    */   public static CodecsColorSpaceType forValue(int value)
/*    */   {
/* 40 */     return (CodecsColorSpaceType)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsColorSpaceType
 * JD-Core Version:    0.6.2
 */