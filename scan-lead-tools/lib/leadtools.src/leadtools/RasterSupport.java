/*    */ package leadtools;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ 
/*    */ public final class RasterSupport
/*    */ {
/*    */   public static void initialize(Object context)
/*    */   {
/* 11 */     ltkrn.Initialize(context);
/*    */   }
/*    */ 
/*    */   public static void setLicense(String licenseFile, String developerKey) throws Exception {
/* 15 */     int ret = ltkrn.SetLicenseFile(licenseFile, developerKey);
/* 16 */     RasterException.checkErrorCode(ret);
/*    */   }
/*    */ 
/*    */   public static void setLicense(InputStream licenseStream, String developerKey) throws Exception {
/* 20 */     if (licenseStream == null) {
/* 21 */       throw new ArgumentNullException("Null Stream");
/*    */     }
/* 23 */     BufferedReader reader = new BufferedReader(new InputStreamReader(licenseStream));
/*    */ 
/* 26 */     String licenseString = "";
/* 27 */     String line = null;
/*    */     try {
/* 29 */       while ((line = reader.readLine()) != null)
/* 30 */         licenseString = licenseString + line;
/*    */     }
/*    */     catch (IOException e) {
/* 33 */       throw new RasterException("Invalid License");
/*    */     }
/*    */ 
/* 37 */     int ret = ltkrn.SetLicense(licenseString, developerKey);
/* 38 */     RasterException.checkErrorCode(ret);
/*    */   }
/*    */ 
/*    */   public static void resetLicense()
/*    */   {
/* 43 */     ltkrn.ResetLicense();
/*    */   }
/*    */ 
/*    */   public static boolean isLocked(RasterSupportType support) {
/* 47 */     return ltkrn.IsSupportLocked(support.getValue());
/*    */   }
/*    */ 
/*    */   public static RasterKernelType getKernelType() {
/* 51 */     int kernelType = ltkrn.GetKernelType();
/* 52 */     return RasterKernelType.forValue(kernelType);
/*    */   }
/*    */ 
/*    */   public static boolean getKernelExpired() {
/* 56 */     return ltkrn.HasExpired();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterSupport
 * JD-Core Version:    0.6.2
 */