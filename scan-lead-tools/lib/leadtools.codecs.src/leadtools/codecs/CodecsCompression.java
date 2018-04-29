/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsCompression
/*    */ {
/*  5 */   CMP(0), 
/*  6 */   JPEG_444(1), 
/*  7 */   TIF_JPEG_444(2), 
/*  8 */   JPEG_411(3), 
/*  9 */   TIF_JPEG_411(4), 
/* 10 */   JPEG_422(5), 
/* 11 */   TIF_JPEG_422(6), 
/* 12 */   LEAD0(0), 
/* 13 */   LEAD1(1), 
/* 14 */   TIFF_CCITT(3), 
/* 15 */   TIFF_CCITTG3_FAX1DIM(4), 
/* 16 */   TIFF_CCITTG3_FAX2DIM(5), 
/* 17 */   TIFF_CCITTG4_FAX(6), 
/* 18 */   J2K(7), 
/* 19 */   JP2(8);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsCompression> mappings;
/*    */ 
/* 25 */   private static HashMap<Integer, CodecsCompression> getMappings() { if (mappings == null)
/*    */     {
/* 27 */       synchronized (CodecsCompression.class)
/*    */       {
/* 29 */         if (mappings == null)
/*    */         {
/* 31 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 35 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsCompression(int value)
/*    */   {
/* 40 */     this.intValue = value;
/* 41 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 46 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsCompression forValue(int value)
/*    */   {
/* 51 */     return (CodecsCompression)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsCompression
 * JD-Core Version:    0.6.2
 */