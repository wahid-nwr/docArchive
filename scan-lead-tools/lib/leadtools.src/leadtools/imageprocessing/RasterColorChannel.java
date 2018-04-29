/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterColorChannel
/*    */ {
/*  7 */   MASTER(0), 
/*  8 */   RED(1), 
/*  9 */   GREEN(2), 
/* 10 */   BLUE(3);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterColorChannel> mappings;
/*    */ 
/* 16 */   private static HashMap<Integer, RasterColorChannel> getMappings() { if (mappings == null)
/*    */     {
/* 18 */       synchronized (RasterColorChannel.class)
/*    */       {
/* 20 */         if (mappings == null)
/*    */         {
/* 22 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 26 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterColorChannel(int value)
/*    */   {
/* 31 */     this.intValue = value;
/* 32 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 37 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterColorChannel forValue(int value)
/*    */   {
/* 42 */     return (RasterColorChannel)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.RasterColorChannel
 * JD-Core Version:    0.6.2
 */