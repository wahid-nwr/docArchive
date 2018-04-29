/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterMetadataFlags
/*    */ {
/*  6 */   NONE(0), 
/*  7 */   COMMENTS(1), 
/*  8 */   TAGS(2), 
/*  9 */   MARKERS(4), 
/* 10 */   GEOKEYS(8), 
/* 11 */   ALL(15);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterMetadataFlags> mappings;
/*    */ 
/* 17 */   private static HashMap<Integer, RasterMetadataFlags> getMappings() { if (mappings == null)
/*    */     {
/* 19 */       synchronized (RasterMetadataFlags.class)
/*    */       {
/* 21 */         if (mappings == null)
/*    */         {
/* 23 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 27 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterMetadataFlags(int value)
/*    */   {
/* 32 */     this.intValue = value;
/* 33 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 38 */     return this.intValue;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterMetadataFlags
 * JD-Core Version:    0.6.2
 */