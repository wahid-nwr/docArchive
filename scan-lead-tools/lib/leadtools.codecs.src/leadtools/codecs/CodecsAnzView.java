/*    */ package leadtools.codecs;
/*    */ 
/*    */ public enum CodecsAnzView
/*    */ {
/*  4 */   TRANSVERSE, 
/*  5 */   SAGITTAL, 
/*  6 */   CORONAL;
/*    */ 
/*    */   int getValue() {
/*  9 */     return ordinal();
/*    */   }
/*    */ 
/*    */   static CodecsAnzView forValue(int value) {
/* 13 */     return values()[value];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsAnzView
 * JD-Core Version:    0.6.2
 */