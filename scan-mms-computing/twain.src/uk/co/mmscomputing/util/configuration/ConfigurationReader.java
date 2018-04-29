/*    */ package uk.co.mmscomputing.util.configuration;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.FileReader;
/*    */ import java.io.FilterReader;
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import java.io.StreamTokenizer;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ConfigurationReader extends FilterReader
/*    */ {
/*    */   public ConfigurationReader(Reader paramReader)
/*    */   {
/*  9 */     super(new BufferedReader(paramReader));
/*    */   }
/*    */ 
/*    */   public ConfigurationReader(String paramString) throws IOException {
/* 13 */     this(new FileReader(paramString));
/*    */   }
/*    */ 
/*    */   public void read(Map paramMap) throws IOException {
/* 17 */     StreamTokenizer localStreamTokenizer = new StreamTokenizer(this.in);
/* 18 */     localStreamTokenizer.wordChars(95, 95);
/* 19 */     int i = 0; String str1 = ""; String str2 = "";
/* 20 */     while ((i = localStreamTokenizer.nextToken()) == -3) {
/* 21 */       str1 = localStreamTokenizer.sval;
/* 22 */       if ((i = localStreamTokenizer.nextToken()) != 61) {
/* 23 */         throw new IOException("Expect [=] have [" + i + " , " + (char)i + "]");
/*    */       }
/* 25 */       i = localStreamTokenizer.nextToken();
/* 26 */       switch (i) {
/*    */       case 34:
/* 28 */         str2 = localStreamTokenizer.sval;
/* 29 */         break;
/*    */       default:
/* 30 */         throw new IOException("Expect [Quoted String] have [" + i + "]");
/*    */       }
/* 32 */       if ((i = localStreamTokenizer.nextToken()) != 59) {
/* 33 */         throw new IOException("Expect [;] have [" + i + "]");
/*    */       }
/* 35 */       paramMap.put(str1, str2);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.configuration.ConfigurationReader
 * JD-Core Version:    0.6.2
 */