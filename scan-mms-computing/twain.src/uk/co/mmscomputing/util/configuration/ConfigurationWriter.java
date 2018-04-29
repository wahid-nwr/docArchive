/*    */ package uk.co.mmscomputing.util.configuration;
/*    */ 
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.File;
/*    */ import java.io.FileWriter;
/*    */ import java.io.FilterWriter;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ import java.io.Writer;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class ConfigurationWriter extends FilterWriter
/*    */ {
/*    */   public ConfigurationWriter(Writer paramWriter)
/*    */   {
/* 11 */     super(new PrintWriter(new BufferedWriter(paramWriter)));
/*    */   }
/*    */ 
/*    */   public ConfigurationWriter(String paramString) throws IOException {
/* 15 */     this(new FileWriter(paramString));
/*    */   }
/*    */ 
/*    */   public void write(Map paramMap) throws IOException {
/* 19 */     PrintWriter localPrintWriter = (PrintWriter)this.out;
/* 20 */     Set localSet = paramMap.entrySet();
/* 21 */     Iterator localIterator = localSet.iterator();
/* 22 */     while (localIterator.hasNext()) {
/* 23 */       Map.Entry localEntry = (Map.Entry)localIterator.next();
/* 24 */       String str1 = (String)localEntry.getKey();
/* 25 */       Object localObject = localEntry.getValue();
/* 26 */       if ((localObject instanceof String)) {
/* 27 */         String str2 = (String)localObject;
/* 28 */         str2 = str2.replace(File.separatorChar, '/');
/* 29 */         localPrintWriter.println(str1 + " = " + "\"" + str2 + "\";");
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.configuration.ConfigurationWriter
 * JD-Core Version:    0.6.2
 */