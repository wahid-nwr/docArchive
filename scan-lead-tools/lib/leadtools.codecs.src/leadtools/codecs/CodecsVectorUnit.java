/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum CodecsVectorUnit
/*    */ {
/*  4 */   INCHES, 
/*  5 */   FEET, 
/*  6 */   YARDS, 
/*  7 */   MILES, 
/*  8 */   MICROMETERS, 
/*  9 */   MILIMETERS, 
/* 10 */   CENTIMETERS, 
/* 11 */   METERS, 
/* 12 */   KILOMETERS, 
/* 13 */   TWIPS, 
/* 14 */   POINTS, 
/* 15 */   PIXELS, 
/* 16 */   DUMMY;
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 20 */     return ordinal();
/*    */   }
/*    */ 
/*    */   public static CodecsVectorUnit forValue(int value)
/*    */   {
/* 25 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsVectorUnit
 * JD-Core Version:    0.6.2
 */