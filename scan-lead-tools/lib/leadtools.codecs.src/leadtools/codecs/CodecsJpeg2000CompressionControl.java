/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsJpeg2000CompressionControl
/*    */ {
/*  5 */   LOSSLESS(0), 
/*  6 */   RATIO(1), 
/*  7 */   TARGET_SIZE(2), 
/*  8 */   QUALITY_FACTOR(3);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsJpeg2000CompressionControl> mappings;
/*    */ 
/* 14 */   private static HashMap<Integer, CodecsJpeg2000CompressionControl> getMappings() { if (mappings == null)
/*    */     {
/* 16 */       synchronized (CodecsJpeg2000CompressionControl.class)
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
/*    */   private CodecsJpeg2000CompressionControl(int value)
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
/*    */   public static CodecsJpeg2000CompressionControl forValue(int value)
/*    */   {
/* 40 */     return (CodecsJpeg2000CompressionControl)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpeg2000CompressionControl
 * JD-Core Version:    0.6.2
 */