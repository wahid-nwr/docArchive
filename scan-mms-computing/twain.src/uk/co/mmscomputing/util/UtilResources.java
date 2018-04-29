/*    */ package uk.co.mmscomputing.util;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import java.util.MissingResourceException;
/*    */ import java.util.ResourceBundle;
/*    */ 
/*    */ public class UtilResources
/*    */ {
/* 61 */   private static Locale locale = Locale.getDefault();
/*    */   private ResourceBundle messages;
/*    */   private String classname;
/*    */ 
/*    */   public UtilResources(Class paramClass)
/*    */   {
/* 13 */     this.classname = paramClass.getName().substring(paramClass.getName().lastIndexOf(".") + 1);
/* 14 */     this.messages = ResourceBundle.getBundle(paramClass.getName(), locale);
/*    */   }
/*    */ 
/*    */   public String getString(String paramString) {
/*    */     try {
/* 19 */       return this.messages.getString(paramString);
/*    */     } catch (MissingResourceException localMissingResourceException) {
/* 21 */       return localMissingResourceException.getMessage();
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getString(String paramString1, String paramString2) {
/*    */     try {
/* 27 */       return this.messages.getString(paramString1).replaceAll("%0", paramString2);
/*    */     } catch (MissingResourceException localMissingResourceException) {
/* 29 */       return localMissingResourceException.getMessage();
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getString(String paramString, String[] paramArrayOfString) {
/*    */     try {
/* 35 */       String str = this.messages.getString(paramString);
/* 36 */       for (int i = 0; i < paramArrayOfString.length; i++) {
/* 37 */         str = str.replaceAll("%" + i, paramArrayOfString[i]);
/*    */       }
/* 39 */       return str;
/*    */     } catch (MissingResourceException localMissingResourceException) {
/* 41 */       return localMissingResourceException.getMessage();
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getErrorString(int paramInt, String paramString)
/*    */   {
/*    */     try
/*    */     {
/* 54 */       return this.messages.getString(this.classname + ".err." + paramInt); } catch (MissingResourceException localMissingResourceException) {
/*    */     }
/* 56 */     return "[" + paramInt + "] " + paramString;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.UtilResources
 * JD-Core Version:    0.6.2
 */