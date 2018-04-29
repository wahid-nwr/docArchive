/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterGetSetOverlayAttributesFlags
/*    */ {
/*  6 */   NONE(0), 
/*  7 */   ORIGIN(1), 
/*  8 */   COLOR(2), 
/*  9 */   FLAGS(4), 
/* 10 */   BIT_INDEX(8), 
/* 11 */   DICOM(16);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterGetSetOverlayAttributesFlags> mappings;
/*    */ 
/* 17 */   private static HashMap<Integer, RasterGetSetOverlayAttributesFlags> getMappings() { if (mappings == null)
/*    */     {
/* 19 */       synchronized (RasterGetSetOverlayAttributesFlags.class)
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
/*    */   private RasterGetSetOverlayAttributesFlags(int value)
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
/*    */   public static RasterGetSetOverlayAttributesFlags forValue(int value)
/*    */   {
/* 43 */     return (RasterGetSetOverlayAttributesFlags)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterGetSetOverlayAttributesFlags
 * JD-Core Version:    0.6.2
 */