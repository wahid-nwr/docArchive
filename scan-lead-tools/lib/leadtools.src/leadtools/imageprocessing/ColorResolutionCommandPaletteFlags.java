/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum ColorResolutionCommandPaletteFlags
/*    */ {
/*  8 */   NONE(0), 
/*  9 */   FIXED(1), 
/* 10 */   OPTIMIZED(2), 
/* 11 */   IDENTITY(8), 
/* 12 */   USE_PALETTE(16), 
/* 13 */   FAST_MATCH(32), 
/* 14 */   NETSCAPE(64), 
/* 15 */   SLOW_MATCH(512), 
/* 16 */   FAVOR_PURE_COLORS(1024);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, ColorResolutionCommandPaletteFlags> mappings;
/*    */ 
/* 22 */   private static HashMap<Integer, ColorResolutionCommandPaletteFlags> getMappings() { if (mappings == null)
/*    */     {
/* 24 */       synchronized (ColorResolutionCommandPaletteFlags.class)
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
/*    */   private ColorResolutionCommandPaletteFlags(int value)
/*    */   {
/* 37 */     this.intValue = value;
/* 38 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 43 */     return this.intValue;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.ColorResolutionCommandPaletteFlags
 * JD-Core Version:    0.6.2
 */