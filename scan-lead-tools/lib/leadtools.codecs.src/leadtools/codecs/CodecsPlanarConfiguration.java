/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsPlanarConfiguration
/*    */ {
/*  5 */   CHUNKY(1), 
/*  6 */   PLANAR_FORMAT(2);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsPlanarConfiguration> mappings;
/*    */ 
/* 12 */   private static HashMap<Integer, CodecsPlanarConfiguration> getMappings() { if (mappings == null)
/*    */     {
/* 14 */       synchronized (CodecsPlanarConfiguration.class)
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
/*    */   private CodecsPlanarConfiguration(int value)
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
/*    */   public static CodecsPlanarConfiguration forValue(int value)
/*    */   {
/* 38 */     return (CodecsPlanarConfiguration)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPlanarConfiguration
 * JD-Core Version:    0.6.2
 */