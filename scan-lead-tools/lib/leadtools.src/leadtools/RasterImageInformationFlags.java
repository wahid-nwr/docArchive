/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterImageInformationFlags
/*    */ {
/*  6 */   NONE(0), 
/*  7 */   MEMORY(1), 
/*  8 */   TILE_SIZE(2), 
/*  9 */   TOTAL_TILES(4), 
/* 10 */   CONVENTIONAL_TILES(8), 
/* 11 */   MAXIMUM_TILE_VIEWS(16), 
/* 12 */   TILE_VIEWS(32);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterImageInformationFlags> mappings;
/*    */ 
/* 18 */   private static HashMap<Integer, RasterImageInformationFlags> getMappings() { if (mappings == null)
/*    */     {
/* 20 */       synchronized (RasterImageInformationFlags.class)
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
/*    */   private RasterImageInformationFlags(int value)
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
/*    */   public static RasterImageInformationFlags forValue(int value)
/*    */   {
/* 44 */     return (RasterImageInformationFlags)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImageInformationFlags
 * JD-Core Version:    0.6.2
 */