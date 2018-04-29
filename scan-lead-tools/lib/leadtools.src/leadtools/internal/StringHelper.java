/*    */ package leadtools.internal;
/*    */ 
/*    */ public class StringHelper
/*    */ {
/*    */   public static String trimEnd(String inputString, String trimString)
/*    */   {
/*  6 */     if ((isNullOrEmpty(inputString)) || (isNullOrEmpty(trimString))) {
/*  7 */       return inputString;
/*    */     }
/*  9 */     int trimLength = trimString.length();
/* 10 */     int inputLength = inputString.length();
/*    */ 
/* 12 */     if (trimLength > inputLength) {
/* 13 */       return inputString;
/*    */     }
/* 15 */     String outputString = inputString.replaceAll("[" + trimString + "]+$", "");
/*    */ 
/* 18 */     return outputString;
/*    */   }
/*    */ 
/*    */   public static String trimStart(String inputString, String trimString) {
/* 22 */     return inputString.replaceFirst(trimString, "");
/*    */   }
/*    */ 
/*    */   public static String[] split(String inputString, String seperator) {
/* 26 */     if ((isNullOrEmpty(inputString)) || (isNullOrEmpty(seperator))) {
/* 27 */       return null;
/*    */     }
/* 29 */     return inputString.split(seperator);
/*    */   }
/*    */ 
/*    */   public static boolean toBoolean(String stringValue) {
/* 33 */     if (isNullOrEmpty(stringValue)) {
/* 34 */       return false;
/*    */     }
/* 36 */     if (stringValue == "1") {
/* 37 */       return true;
/*    */     }
/* 39 */     return false;
/*    */   }
/*    */ 
/*    */   public static String toLowerCase(String inputString) {
/* 43 */     return inputString.toLowerCase();
/*    */   }
/*    */ 
/*    */   public static boolean isNullOrEmpty(String value) {
/* 47 */     return (value == null) || (value.length() < 1);
/*    */   }
/*    */ 
/*    */   public static String join(String separator, String[] stringarray) {
/* 51 */     if (stringarray == null) {
/* 52 */       return null;
/*    */     }
/* 54 */     return join(separator, stringarray, 0, stringarray.length);
/*    */   }
/*    */ 
/*    */   public static String join(String separator, String[] stringarray, int startindex, int count) {
/* 58 */     String result = "";
/*    */ 
/* 60 */     if (stringarray == null) {
/* 61 */       return null;
/*    */     }
/* 63 */     for (int index = startindex; (index < stringarray.length) && (index - startindex < count); index++) {
/* 64 */       if ((separator != null) && (index > startindex)) {
/* 65 */         result = result + separator;
/*    */       }
/* 67 */       if (stringarray[index] != null) {
/* 68 */         result = result + stringarray[index];
/*    */       }
/*    */     }
/* 71 */     return result;
/*    */   }
/*    */ 
/*    */   public static String substring(String str, int startindex, int length) {
/* 75 */     return str.substring(startindex, startindex + length);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.StringHelper
 * JD-Core Version:    0.6.2
 */