/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterUpdateOverlayBitsFlags
/*    */ {
/*  5 */   NONE(0), 
/*  6 */   FROM_OVERLAY(1), 
/*  7 */   FROM_IMAGE(2), 
/*  8 */   CLEAR(4);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterUpdateOverlayBitsFlags> mappings;
/*    */ 
/* 14 */   private static HashMap<Integer, RasterUpdateOverlayBitsFlags> getMappings() { if (mappings == null)
/*    */     {
/* 16 */       synchronized (RasterUpdateOverlayBitsFlags.class)
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
/*    */   private RasterUpdateOverlayBitsFlags(int value)
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
/*    */   public static RasterUpdateOverlayBitsFlags forValue(int value)
/*    */   {
/* 40 */     return (RasterUpdateOverlayBitsFlags)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterUpdateOverlayBitsFlags
 * JD-Core Version:    0.6.2
 */