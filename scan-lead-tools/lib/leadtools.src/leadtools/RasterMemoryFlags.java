/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterMemoryFlags
/*    */ {
/*  6 */   NONE(0), 
/*  7 */   CONVENTIONAL(1), 
/*  8 */   USER(2), 
/*  9 */   TILED(4), 
/* 10 */   NO_TILED(8), 
/* 11 */   DISK(128), 
/* 12 */   NO_DISK(256), 
/* 13 */   COMPRESSED(512), 
/* 14 */   SUPER_COMPRESSED(1024), 
/* 15 */   WRITEABLE_BITMAP(2048);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterMemoryFlags> mappings;
/*    */ 
/* 21 */   private static HashMap<Integer, RasterMemoryFlags> getMappings() { if (mappings == null)
/*    */     {
/* 23 */       synchronized (RasterMemoryFlags.class)
/*    */       {
/* 25 */         if (mappings == null)
/*    */         {
/* 27 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 31 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterMemoryFlags(int value)
/*    */   {
/* 36 */     this.intValue = value;
/* 37 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 42 */     return this.intValue;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterMemoryFlags
 * JD-Core Version:    0.6.2
 */