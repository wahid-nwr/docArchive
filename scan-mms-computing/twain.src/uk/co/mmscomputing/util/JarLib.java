/*     */ package uk.co.mmscomputing.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class JarLib
/*     */ {
/*     */   public static boolean load(Class paramClass, String paramString)
/*     */   {
/*     */     try
/*     */     {
/*  10 */       loadX(paramClass, paramString);
/*  11 */       return true;
/*     */     } catch (Exception localException1) {
/*  13 */       System.out.println("JarLib.load\n\tException = " + localException1.getMessage());
/*  14 */       System.err.println("JarLib.load\n\tException = " + localException1.getMessage());
/*  15 */       localException1.printStackTrace();
/*     */     } catch (Error localError1) {
/*  17 */       System.out.println("JarLib.load\n\tError = " + localError1);
/*  18 */       System.err.println("JarLib.load\n\tError = " + localError1);
/*  19 */       localError1.printStackTrace();
/*     */     }
/*     */     try {
/*  22 */       System.loadLibrary(paramString);
/*  23 */       System.out.println("JarLib.load: Successfully loaded library [" + paramString + "] from some default system folder");
/*  24 */       return true;
/*     */     } catch (Exception localException2) {
/*  26 */       System.out.println("JarLib.load\n\tException = " + localException2.getMessage());
/*  27 */       System.err.println("JarLib.load\n\tException = " + localException2.getMessage());
/*     */     } catch (Error localError2) {
/*  29 */       System.out.println("JarLib.load\n\tError = " + localError2);
/*  30 */       System.err.println("JarLib.load\n\tError = " + localError2);
/*  31 */       localError2.printStackTrace();
/*     */     }
/*  33 */     return false;
/*     */   }
/*     */ 
/*     */   private static void loadX(Class paramClass, String paramString) throws IOException, UnsatisfiedLinkError {
/*  37 */     String str1 = System.mapLibraryName(paramString);
/*  38 */     URL localURL = paramClass.getResource(getOsSubDir() + "/" + str1);
/*  39 */     if (localURL == null)
/*  40 */       throw new UnsatisfiedLinkError(JarLib.class.getName() + ".loadX: Could not find library [" + str1 + "]");
/*     */     try
/*     */     {
/*  43 */       URI localURI = new URI(localURL.toString());
/*  44 */       String str2 = localURI.getScheme();
/*  45 */       if (str2.equals("file")) {
/*  46 */         System.load(new File(localURI).getAbsolutePath());
/*  47 */         System.out.println("JarLib.load: Successfully loaded library [" + localURL + "] from mmsc standard file location");
/*  48 */       } else if ((str2.equals("jar")) || (str2.equals("nbjcl")))
/*     */       {
/*  51 */         File localFile1 = new File(System.getProperty("java.io.tmpdir"), "mmsc"); localFile1.mkdirs();
/*     */ 
/*  53 */         File[] arrayOfFile = localFile1.listFiles();
/*  54 */         for (int i = 0; i < arrayOfFile.length; i++) {
/*  55 */           if (arrayOfFile[i].getName().endsWith(str1)) {
/*  56 */             arrayOfFile[i].delete();
/*     */           }
/*     */         }
/*     */ 
/*  60 */         File localFile2 = File.createTempFile("mmsc", str1, localFile1);
/*  61 */         extract(localFile2, localURL);
/*  62 */         System.load(localFile2.getAbsolutePath());
/*     */ 
/*  64 */         System.out.println("JarLib.load: Successfully loaded library [" + localURL + "] from jar file location");
/*     */       } else {
/*  66 */         throw new UnsatisfiedLinkError(JarLib.class.getName() + ".loadX:\n\tUnknown URI-Scheme [+scheme+]; Could not load library [" + localURI + "]");
/*     */       }
/*     */     } catch (URISyntaxException localURISyntaxException) {
/*  69 */       throw new UnsatisfiedLinkError(JarLib.class.getName() + ".loadX:\n\tURI-Syntax Exception; Could not load library [" + localURL + "]");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void extract(File paramFile, URL paramURL) throws IOException {
/*  74 */     InputStream localInputStream = paramURL.openStream();
/*  75 */     FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
/*  76 */     byte[] arrayOfByte = new byte[4096];
/*  77 */     int i = 0;
/*  78 */     while ((i = localInputStream.read(arrayOfByte)) > 0) {
/*  79 */       localFileOutputStream.write(arrayOfByte, 0, i);
/*     */     }
/*  81 */     localFileOutputStream.close();
/*  82 */     localInputStream.close();
/*     */   }
/*     */ 
/*     */   private static String getOsSubDir()
/*     */   {
/*  91 */     String str1 = System.getProperty("os.name");
/*     */     String str2;
/*  93 */     if (str1.startsWith("Linux")) {
/*  94 */       str2 = System.getProperty("os.arch");
/*  95 */       if (str2.endsWith("64")) {
/*  96 */         return "lin64";
/*     */       }
/*  98 */       return "lin32";
/*     */     }
/*     */ 
/* 101 */     if (str1.startsWith("Windows")) {
/* 102 */       str2 = System.getProperty("os.arch");
/* 103 */       if (str2.endsWith("64")) {
/* 104 */         return "win64";
/*     */       }
/* 106 */       return "win32";
/*     */     }
/*     */ 
/* 109 */     if (str1.startsWith("Mac")) return "mac";
/* 110 */     return "";
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.JarLib
 * JD-Core Version:    0.6.2
 */