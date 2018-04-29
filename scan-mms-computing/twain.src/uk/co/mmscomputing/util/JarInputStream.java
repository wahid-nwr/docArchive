/*    */ package uk.co.mmscomputing.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class JarInputStream extends FilterInputStream
/*    */ {
/*  8 */   private static ClassLoader cl = JarInputStream.class.getClassLoader();
/*    */ 
/*    */   public JarInputStream(String paramString) throws IOException {
/* 11 */     super(load(paramString));
/*    */   }
/*    */ 
/*    */   private static InputStream load(String paramString)
/*    */     throws IOException
/*    */   {
/* 17 */     String str = System.getProperty("user.dir") + File.separator + paramString.replace('/', File.separatorChar);
/* 18 */     if (new File(str).exists()) return new FileInputStream(str);
/*    */ 
/* 29 */     InputStream localInputStream = cl.getResourceAsStream(paramString);
/* 30 */     if (localInputStream == null) {
/* 31 */       throw new IOException("uk.co.mmscomputing.util.JarInputStream.load\n\tFile " + paramString + " does not exist");
/*    */     }
/* 33 */     return localInputStream;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.JarInputStream
 * JD-Core Version:    0.6.2
 */