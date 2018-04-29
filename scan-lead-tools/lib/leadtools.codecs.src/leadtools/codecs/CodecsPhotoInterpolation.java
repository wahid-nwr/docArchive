/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsPhotoInterpolation
/*    */ {
/*  5 */   WHITE_IS_ZERO(0), 
/*  6 */   BLACK_IS_ZERO(1), 
/*  7 */   NO_COLOR_MAP(2), 
/*  8 */   CMYK(5), 
/*  9 */   YCBCR(6), 
/* 10 */   CIELAB(8);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsPhotoInterpolation> mappings;
/*    */ 
/* 16 */   private static HashMap<Integer, CodecsPhotoInterpolation> getMappings() { if (mappings == null)
/*    */     {
/* 18 */       synchronized (CodecsPhotoInterpolation.class)
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
/*    */   private CodecsPhotoInterpolation(int value)
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
/*    */   public static CodecsPhotoInterpolation forValue(int value)
/*    */   {
/* 42 */     return (CodecsPhotoInterpolation)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPhotoInterpolation
 * JD-Core Version:    0.6.2
 */