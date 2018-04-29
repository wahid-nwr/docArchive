/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsRasterizeDocumentUnit
/*    */ {
/*  4 */   PIXEL(RASTERIZEDOC_UNIT.RASTERIZEDOC_UNIT_PIXEL.getValue()), 
/*  5 */   INCH(RASTERIZEDOC_UNIT.RASTERIZEDOC_UNIT_INCH.getValue()), 
/*  6 */   MILLIMETER(RASTERIZEDOC_UNIT.RASTERIZEDOC_UNIT_MILLIMETER.getValue());
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsRasterizeDocumentUnit> mappings;
/*    */ 
/* 12 */   private static HashMap<Integer, CodecsRasterizeDocumentUnit> getMappings() { if (mappings == null)
/*    */     {
/* 14 */       synchronized (CodecsRasterizeDocumentUnit.class)
/*    */       {
/* 16 */         if (mappings == null)
/*    */         {
/* 18 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 22 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsRasterizeDocumentUnit(int value)
/*    */   {
/* 27 */     this.intValue = value;
/* 28 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 33 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsRasterizeDocumentUnit forValue(int value)
/*    */   {
/* 38 */     return (CodecsRasterizeDocumentUnit)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRasterizeDocumentUnit
 * JD-Core Version:    0.6.2
 */