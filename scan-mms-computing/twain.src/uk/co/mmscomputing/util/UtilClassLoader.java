/*    */ package uk.co.mmscomputing.util;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class UtilClassLoader extends ClassLoader
/*    */ {
/*    */   public synchronized Class loadClass(String paramString, boolean paramBoolean)
/*    */     throws ClassNotFoundException
/*    */   {
/* 18 */     Class localClass = null;
/*    */ 
/* 20 */     System.out.println(" >>>>>> Load class : " + paramString);
/*    */     try
/*    */     {
/* 23 */       localClass = super.findClass(paramString);
/* 24 */       System.out.println(" >>>>>> returning system class (in CLASSPATH).");
/* 25 */       return localClass;
/*    */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 27 */       System.out.println("        >>>>>> Not a system class.");
/*    */ 
/* 32 */       throw localClassNotFoundException;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.UtilClassLoader
 * JD-Core Version:    0.6.2
 */