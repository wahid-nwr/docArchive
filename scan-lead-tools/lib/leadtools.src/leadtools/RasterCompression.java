/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterCompression
/*    */ {
/*  5 */   NONE(0), 
/*  6 */   RLE(1), 
/*  7 */   SUPER(2);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterCompression> mappings;
/*    */ 
/* 13 */   private static HashMap<Integer, RasterCompression> getMappings() { if (mappings == null)
/*    */     {
/* 15 */       synchronized (RasterCompression.class)
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
/*    */   private RasterCompression(int value)
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
/*    */   public static RasterCompression forValue(int value)
/*    */   {
/* 39 */     return (RasterCompression)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterCompression
 * JD-Core Version:    0.6.2
 */