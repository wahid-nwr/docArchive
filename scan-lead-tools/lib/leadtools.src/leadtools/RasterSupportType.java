/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterSupportType
/*    */ {
/*  5 */   DOCUMENT(0), 
/*  6 */   BARCODES1D(1), 
/*  7 */   BARCODES2D(2), 
/*  8 */   RASTERPDFREAD(3), 
/*  9 */   RASTERPDFSAVE(4), 
/* 10 */   PDFADVANCED(5), 
/* 11 */   JBIG2(6), 
/* 12 */   OCRADVANTAGE(7), 
/* 13 */   OCRPLUS(8), 
/* 14 */   OCRPROFESSIONAL(9), 
/* 15 */   OCRPROFESSIONALASIAN(10), 
/* 16 */   OCRARABIC(11), 
/* 17 */   OCRADVANTAGEPDFOUTPUT(12), 
/* 18 */   OCRPLUSPDFOUTPUT(13), 
/* 19 */   OCRPROFESSIONALPDFOUTPUT(14), 
/* 20 */   OCRARABICPDFOUTPUT(15), 
/* 21 */   OMR(16), 
/* 22 */   ICRPLUS(17), 
/* 23 */   ICRPROFESSIONAL(18), 
/* 24 */   DOCUMENTWRITERS(19), 
/* 25 */   DOCUMENTWRITERSPDF(20), 
/* 26 */   PRINTDRIVER(21), 
/* 27 */   PRINTDRIVERSERVER(22), 
/* 28 */   PRINTDRIVERNETWORK(23), 
/* 29 */   FORMS(24), 
/* 30 */   MEDIAWRITER(25), 
/* 31 */   MEDICAL(26), 
/* 32 */   MEDICAL3D(27), 
/* 33 */   DICOMCOMMUNICATION(28), 
/* 34 */   CCOW(29), 
/* 35 */   VECTOR(30), 
/* 36 */   CLOUD(31), 
/* 37 */   APPSTORE(32), 
/* 38 */   BASIC(33);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterSupportType> mappings;
/*    */ 
/* 44 */   private static HashMap<Integer, RasterSupportType> getMappings() { if (mappings == null)
/*    */     {
/* 46 */       synchronized (RasterSupportType.class)
/*    */       {
/* 48 */         if (mappings == null)
/*    */         {
/* 50 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 54 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterSupportType(int value)
/*    */   {
/* 59 */     this.intValue = value;
/* 60 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 65 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterSupportType forValue(int value)
/*    */   {
/* 70 */     return (RasterSupportType)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterSupportType
 * JD-Core Version:    0.6.2
 */