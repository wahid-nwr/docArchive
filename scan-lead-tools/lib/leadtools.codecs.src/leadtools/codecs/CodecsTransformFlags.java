/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsTransformFlags
/*    */ {
/*  5 */   NONE(0), 
/*  6 */   FLIP(1), 
/*  7 */   REVERSE(2), 
/*  8 */   ROTATE_90(4), 
/*  9 */   ROTATE_180(8), 
/* 10 */   ROTATE_270(12);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsTransformFlags> mappings;
/*    */ 
/* 16 */   private static HashMap<Integer, CodecsTransformFlags> getMappings() { if (mappings == null)
/*    */     {
/* 18 */       synchronized (CodecsTransformFlags.class)
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
/*    */   private CodecsTransformFlags(int value)
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
/*    */   public static CodecsTransformFlags forValue(int value)
/*    */   {
/* 42 */     return (CodecsTransformFlags)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsTransformFlags
 * JD-Core Version:    0.6.2
 */