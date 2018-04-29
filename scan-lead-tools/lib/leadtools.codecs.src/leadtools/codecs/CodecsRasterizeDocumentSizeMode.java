/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsRasterizeDocumentSizeMode
/*    */ {
/*  4 */   NONE(RASTERIZEDOC_SIZEMODE.RASTERIZEDOC_SIZEMODE_NONE.getValue()), 
/*  5 */   FIT(RASTERIZEDOC_SIZEMODE.RASTERIZEDOC_SIZEMODE_FIT.getValue()), 
/*  6 */   FIT_ALWAYS(RASTERIZEDOC_SIZEMODE.RASTERIZEDOC_SIZEMODE_FIT_ALWAYS.getValue()), 
/*  7 */   FIT_WIDTH(RASTERIZEDOC_SIZEMODE.RASTERIZEDOC_SIZEMODE_FIT_WIDTH.getValue()), 
/*  8 */   STRETCH(RASTERIZEDOC_SIZEMODE.RASTERIZEDOC_SIZEMODE_STRETCH.getValue());
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsRasterizeDocumentSizeMode> mappings;
/*    */ 
/*    */   private static HashMap<Integer, CodecsRasterizeDocumentSizeMode> getMappings() {
/* 15 */     if (mappings == null)
/*    */     {
/* 17 */       synchronized (CodecsRasterizeDocumentSizeMode.class)
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
/*    */   private CodecsRasterizeDocumentSizeMode(int value)
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
/*    */   public static CodecsRasterizeDocumentSizeMode forValue(int value)
/*    */   {
/* 41 */     return (CodecsRasterizeDocumentSizeMode)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRasterizeDocumentSizeMode
 * JD-Core Version:    0.6.2
 */