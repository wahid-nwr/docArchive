/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterImageUnderlayFlags
/*    */ {
/*  6 */   NONE(0), 
/*  7 */   STRETCH(1);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterImageUnderlayFlags> mappings;
/*    */ 
/* 13 */   private static HashMap<Integer, RasterImageUnderlayFlags> getMappings() { if (mappings == null)
/*    */     {
/* 15 */       synchronized (RasterImageUnderlayFlags.class)
/*    */       {
/* 17 */         if (mappings == null)
/*    */         {
/* 19 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 23 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterImageUnderlayFlags(int value)
/*    */   {
/* 28 */     this.intValue = value;
/* 29 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 34 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterImageUnderlayFlags forValue(int value)
/*    */   {
/* 39 */     return (RasterImageUnderlayFlags)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImageUnderlayFlags
 * JD-Core Version:    0.6.2
 */