/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterSizeFlags
/*    */ {
/*  5 */   NONE(0), 
/*  6 */   FAVOR_BLACK(1), 
/*  7 */   RESAMPLE(2), 
/*  8 */   BICUBIC(4), 
/*  9 */   SCALE_TO_GRAY(8), 
/* 10 */   OLD_RESAMPLE(16), 
/* 11 */   PREMULTIPLY_ALPHA(32);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterSizeFlags> mappings;
/*    */ 
/* 17 */   private static HashMap<Integer, RasterSizeFlags> getMappings() { if (mappings == null)
/*    */     {
/* 19 */       synchronized (RasterSizeFlags.class)
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
/*    */   private RasterSizeFlags(int value)
/*    */   {
/* 32 */     this.intValue = value;
/* 33 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 38 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterSizeFlags forValue(int value)
/*    */   {
/* 43 */     return (RasterSizeFlags)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterSizeFlags
 * JD-Core Version:    0.6.2
 */