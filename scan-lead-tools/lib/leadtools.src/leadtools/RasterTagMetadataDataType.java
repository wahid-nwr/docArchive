/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterTagMetadataDataType
/*    */ {
/*  5 */   BYTE(1), 
/*  6 */   ASCII(2), 
/*  7 */   SBYTE(6), 
/*  8 */   UNDEFINED(7), 
/*  9 */   UINT_16(3), 
/* 10 */   INT_16(8), 
/* 11 */   UINT_32(4), 
/* 12 */   INT_32(9), 
/* 13 */   SINGLE(11), 
/* 14 */   URATIONAL(5), 
/* 15 */   RATIONAL(10), 
/* 16 */   DOUBLE(12);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterTagMetadataDataType> mappings;
/*    */ 
/* 22 */   private static HashMap<Integer, RasterTagMetadataDataType> getMappings() { if (mappings == null)
/*    */     {
/* 24 */       synchronized (RasterTagMetadataDataType.class)
/*    */       {
/* 26 */         if (mappings == null)
/*    */         {
/* 28 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 32 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterTagMetadataDataType(int value)
/*    */   {
/* 37 */     this.intValue = value;
/* 38 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 43 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterTagMetadataDataType forValue(int value)
/*    */   {
/* 48 */     return (RasterTagMetadataDataType)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterTagMetadataDataType
 * JD-Core Version:    0.6.2
 */