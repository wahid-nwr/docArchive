/*    */ package uk.co.mmscomputing.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ 
/*    */ public class JarFile extends File
/*    */ {
/*  8 */   private static ClassLoader cl = JarFile.class.getClassLoader();
/*    */ 
/*    */   public JarFile(String paramString) throws IOException {
/* 11 */     super(load(paramString));
/*    */   }
/*    */ 
/*    */   private static String load(String paramString) throws IOException {
/* 15 */     String str1 = System.getProperty("user.dir") + File.separator + paramString.replace('/', File.separatorChar);
/* 16 */     String str2 = new File(str1).getParent();
/* 17 */     if (str2 != null) new File(str2).mkdirs();
/* 18 */     if (new File(str1).exists()) return str1;
/*    */ 
/* 20 */     paramString = paramString.replace(File.separatorChar, '/');
/* 21 */     URL localURL = cl.getResource(paramString);
/* 22 */     if (localURL == null) return str1;
/*    */ 
/* 24 */     if (localURL.toString().startsWith("file:"))
/* 25 */       str1 = localURL.getFile();
/* 26 */     else if (localURL.toString().startsWith("jar:")) {
/* 27 */       extract(str1, localURL);
/*    */     }
/* 29 */     return str1;
/*    */   }
/*    */ 
/*    */   private static void extract(String paramString, URL paramURL) throws IOException {
/* 33 */     InputStream localInputStream = paramURL.openStream();
/* 34 */     FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
/* 35 */     byte[] arrayOfByte = new byte[4096];
/* 36 */     int i = 0;
/* 37 */     while ((i = localInputStream.read(arrayOfByte)) > 0) {
/* 38 */       localFileOutputStream.write(arrayOfByte, 0, i);
/*    */     }
/* 40 */     localFileOutputStream.close();
/* 41 */     localInputStream.close();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.JarFile
 * JD-Core Version:    0.6.2
 */