/*    */ package leadtools.demos;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.PrintStream;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.net.URL;
/*    */ import java.net.URLDecoder;
/*    */ import java.security.CodeSource;
/*    */ import java.security.ProtectionDomain;
/*    */ import leadtools.LEADResourceDirectory;
/*    */ import leadtools.Platform;
/*    */ import leadtools.RasterDefaults;
/*    */ import leadtools.RasterSupport;
/*    */ 
/*    */ public class DemoUtilities
/*    */ {
/*    */   public static boolean setLicense()
/*    */   {
/* 11 */     String licenseFile = "";
/* 12 */     String developerKey = "Nag";
/*    */     try
/*    */     {
/* 15 */       RasterSupport.setLicense(licenseFile, developerKey);
/*    */     } catch (Exception e) {
/* 17 */       System.out.println(e.getMessage());
/* 18 */       return false;
/*    */     }
/*    */ 
/* 21 */     return !RasterSupport.getKernelExpired();
/*    */   }
/*    */ 
/*    */   public static String getLibPath() throws UnsupportedEncodingException {
/* 25 */     File libDirectory = new File(DemoUtilities.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
/* 26 */     String libPath = URLDecoder.decode(libDirectory.getAbsolutePath(), "UTF-8");
/*    */ 
/* 28 */     if (Platform.isWindows()) {
/* 29 */       libPath = libPath + "\\..\\CDLLVC10\\";
/* 30 */       if (Platform.is64Bit())
/* 31 */         libPath = libPath + "x64";
/*    */       else
/* 33 */         libPath = libPath + "Win32";
/* 34 */     } else if (Platform.isLinux()) {
/* 35 */       libPath = libPath + "/../Lib/";
/* 36 */       if (Platform.is64Bit())
/* 37 */         libPath = libPath + "x64";
/*    */       else
/* 39 */         libPath = libPath + "x86";
/*    */     }
/* 41 */     System.out.println("LEADTOOLS libPath: " + libPath);
/* 42 */     return libPath;
/*    */   }
/*    */ 
/*    */   public static String getOcrEngineRuntimePath() throws UnsupportedEncodingException {
/* 46 */     String ocrEngineRuntimePath = null;
/* 47 */     File libDirectory = new File(DemoUtilities.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
/* 48 */     String libPath = URLDecoder.decode(libDirectory.getAbsolutePath(), "UTF-8");
/* 49 */     ocrEngineRuntimePath = libPath + "/../Common/OcrAdvantageRuntime";
/*    */ 
/* 51 */     return ocrEngineRuntimePath;
/*    */   }
/*    */ 
/*    */   public static void setShadowFonts() {
/*    */     try {
/* 56 */       if (!Platform.isWindows()) {
/* 57 */         File libDirectory = new File(DemoUtilities.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
/* 58 */         String libPath = URLDecoder.decode(libDirectory.getAbsolutePath(), "UTF-8");
/* 59 */         File shadowFontsFile = new File(libPath + "/../Common/ShadowFonts");
/*    */ 
/* 61 */         if (!shadowFontsFile.exists()) {
/* 62 */           System.out.println(String.format("Unable to set shadow fonts because the directory %s does not exist. The demo will continue to run as normal but OCR and Document Writer operations might produce results with less than optimal accuracy.", new Object[] { shadowFontsFile.getAbsolutePath() }));
/* 63 */           return;
/*    */         }
/*    */ 
/* 66 */         RasterDefaults.setResourceDirectory(LEADResourceDirectory.FONTS, shadowFontsFile.getAbsolutePath());
/*    */       }
/*    */     } catch (Exception ex) {
/* 69 */       System.out.println(ex.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public static boolean isNullOrEmpty(String stringText) {
/* 74 */     return (stringText == null) || (stringText.equals(""));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.host.jar
 * Qualified Name:     leadtools.demos.DemoUtilities
 * JD-Core Version:    0.6.2
 */