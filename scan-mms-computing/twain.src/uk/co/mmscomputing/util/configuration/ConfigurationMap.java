/*    */ package uk.co.mmscomputing.util.configuration;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.util.Hashtable;
/*    */ import uk.co.mmscomputing.util.JarFile;
/*    */ 
/*    */ public class ConfigurationMap extends Hashtable
/*    */ {
/* 10 */   private static String basePath = "base_path";
/* 11 */   private static String confPath = "config_path";
/* 12 */   private static String conffn = "configuration.txt";
/*    */ 
/*    */   public ConfigurationMap() {
/*    */   }
/*    */ 
/*    */   public ConfigurationMap(Class paramClass) {
/*    */     try {
/* 19 */       String str1 = paramClass.getName();
/* 20 */       String str2 = str1.substring(0, str1.lastIndexOf('.'));
/* 21 */       String str3 = str2.replace('.', File.separatorChar) + File.separator;
/* 22 */       put(basePath, str3);
/*    */ 
/* 24 */       String str4 = str3 + conffn;
/* 25 */       if (new JarFile(str4).exists()) {
/* 26 */         ConfigurationReader localConfigurationReader = new ConfigurationReader(str4);
/* 27 */         localConfigurationReader.read(this); localConfigurationReader.close();
/*    */       }
/* 29 */       put(confPath, str4);
/*    */     }
/*    */     catch (IOException localIOException) {
/* 32 */       System.err.println("ConfigurationMap(Class): " + localIOException.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getString(String paramString1, String paramString2) {
/* 37 */     Object localObject = get(paramString1);
/* 38 */     if (localObject != null) return (String)localObject;
/* 39 */     put(paramString1, paramString2);
/* 40 */     return paramString2;
/*    */   }
/*    */ 
/*    */   public String getString(String paramString) {
/* 44 */     Object localObject = get(paramString);
/* 45 */     if (localObject != null) return (String)localObject;
/* 46 */     put(paramString, "");
/* 47 */     return "";
/*    */   }
/*    */ 
/*    */   public int getInt(String paramString, int paramInt) {
/* 51 */     Object localObject = get(paramString);
/* 52 */     if (localObject != null)
/*    */       try {
/* 54 */         return Integer.parseInt((String)localObject);
/*    */       }
/*    */       catch (NumberFormatException localNumberFormatException) {
/*    */       }
/* 58 */     put(paramString, Integer.toString(paramInt));
/* 59 */     return paramInt;
/*    */   }
/*    */ 
/*    */   public int getInt(String paramString) {
/* 63 */     Object localObject = get(paramString);
/* 64 */     if (localObject != null)
/*    */       try {
/* 66 */         return Integer.parseInt((String)localObject);
/*    */       }
/*    */       catch (NumberFormatException localNumberFormatException) {
/*    */       }
/* 70 */     put(paramString, "0");
/* 71 */     return 0;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.configuration.ConfigurationMap
 * JD-Core Version:    0.6.2
 */