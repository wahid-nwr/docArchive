/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsJpeg2000RegionOfInterest
/*    */ {
/*  5 */   USE_LEAD_REGION(0), 
/*  6 */   USE_OPTION_RECTANGLE(1);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsJpeg2000RegionOfInterest> mappings;
/*    */ 
/* 12 */   private static HashMap<Integer, CodecsJpeg2000RegionOfInterest> getMappings() { if (mappings == null)
/*    */     {
/* 14 */       synchronized (CodecsJpeg2000RegionOfInterest.class)
/*    */       {
/* 16 */         if (mappings == null)
/*    */         {
/* 18 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 22 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsJpeg2000RegionOfInterest(int value)
/*    */   {
/* 27 */     this.intValue = value;
/* 28 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 33 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsJpeg2000RegionOfInterest forValue(int value)
/*    */   {
/* 38 */     return (CodecsJpeg2000RegionOfInterest)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpeg2000RegionOfInterest
 * JD-Core Version:    0.6.2
 */