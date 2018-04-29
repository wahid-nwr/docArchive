/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsRasterPdfVersion
/*    */ {
/*  5 */   V12(RASTERPDFVERSION.RASTERPDFVERSION_V12.getValue()), 
/*  6 */   V14(RASTERPDFVERSION.RASTERPDFVERSION_V14.getValue()), 
/*  7 */   PDFA(RASTERPDFVERSION.RASTERPDFVERSION_PDFA.getValue()), 
/*  8 */   V15(RASTERPDFVERSION.RASTERPDFVERSION_V15.getValue()), 
/*  9 */   V16(RASTERPDFVERSION.RASTERPDFVERSION_V16.getValue()), 
/* 10 */   V13(RASTERPDFVERSION.RASTERPDFVERSION_V13.getValue());
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsRasterPdfVersion> mappings;
/*    */ 
/* 16 */   private static HashMap<Integer, CodecsRasterPdfVersion> getMappings() { if (mappings == null)
/*    */     {
/* 18 */       synchronized (CodecsRasterPdfVersion.class)
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
/*    */   private CodecsRasterPdfVersion(int value)
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
/*    */   public static CodecsRasterPdfVersion forValue(int value)
/*    */   {
/* 42 */     return (CodecsRasterPdfVersion)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRasterPdfVersion
 * JD-Core Version:    0.6.2
 */