/*    */ package uk.co.mmscomputing.util.configuration;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileWriter;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.PrintWriter;
/*    */ import java.io.StreamTokenizer;
/*    */ import java.util.Dictionary;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Hashtable;
/*    */ 
/*    */ public class Parser
/*    */ {
/*  8 */   Dictionary dict = new Hashtable();
/*    */ 
/*    */   public Parser(InputStream paramInputStream) throws IOException {
/* 11 */     parse(paramInputStream);
/*    */   }
/*    */ 
/*    */   public Parser(String paramString) throws IOException {
/* 15 */     File localFile = new File(paramString);
/* 16 */     if (localFile.exists())
/* 17 */       parse(new FileInputStream(localFile));
/*    */   }
/*    */ 
/*    */   public void parse(InputStream paramInputStream) throws IOException
/*    */   {
/* 22 */     BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
/* 23 */     StreamTokenizer localStreamTokenizer = new StreamTokenizer(localBufferedReader);
/* 24 */     localStreamTokenizer.wordChars(95, 95);
/* 25 */     int i = 0; String str1 = ""; String str2 = "";
/* 26 */     while ((i = localStreamTokenizer.nextToken()) == -3) {
/* 27 */       str1 = localStreamTokenizer.sval;
/* 28 */       if ((i = localStreamTokenizer.nextToken()) != 61) {
/* 29 */         throw new IOException("Expect [=] have [" + i + " , " + (char)i + "]");
/*    */       }
/* 31 */       i = localStreamTokenizer.nextToken();
/* 32 */       switch (i) {
/*    */       case 34:
/* 34 */         str2 = localStreamTokenizer.sval;
/* 35 */         break;
/*    */       default:
/* 36 */         throw new IOException("Expect [Quoted String] have [" + i + "]");
/*    */       }
/* 38 */       if ((i = localStreamTokenizer.nextToken()) != 59) {
/* 39 */         throw new IOException("Expect [;] have [" + i + "]");
/*    */       }
/* 41 */       this.dict.put(str1, str2);
/*    */     }
/* 43 */     paramInputStream.close();
/*    */   }
/*    */ 
/*    */   public String getString(String paramString) {
/* 47 */     String str = (String)this.dict.get(paramString);
/* 48 */     return str == null ? "unknown key [" + paramString + "]" : str;
/*    */   }
/*    */ 
/*    */   public String[] getArray(String paramString) {
/* 52 */     String str = (String)this.dict.get(paramString);
/* 53 */     if (str == null) return new String[0];
/* 54 */     str = str.replaceAll("\\s", "");
/* 55 */     return str.split(",");
/*    */   }
/*    */ 
/*    */   public void setString(String paramString1, String paramString2) {
/* 59 */     this.dict.put(paramString1, paramString2);
/*    */   }
/*    */ 
/*    */   public void save(String paramString) throws IOException {
/* 63 */     PrintWriter localPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(paramString)));
/* 64 */     Enumeration localEnumeration1 = this.dict.keys();
/* 65 */     Enumeration localEnumeration2 = this.dict.elements();
/* 66 */     while ((localEnumeration2.hasMoreElements()) && (localEnumeration1.hasMoreElements())) {
/* 67 */       String str1 = (String)localEnumeration1.nextElement();
/* 68 */       String str2 = (String)localEnumeration2.nextElement();
/* 69 */       str2 = str2.replace(File.separatorChar, '/');
/* 70 */       localPrintWriter.println(str1 + " = " + "\"" + str2 + "\";");
/*    */     }
/* 72 */     localPrintWriter.close();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.configuration.Parser
 * JD-Core Version:    0.6.2
 */