/*   */ package uk.co.mmscomputing.device.twain;
/*   */ 
/*   */ import uk.co.mmscomputing.util.JarLib;
/*   */ 
/*   */ public class TwainDefaultNativeLoadStrategy
/*   */   implements TwainINativeLoadStrategy
/*   */ {
/*   */   public boolean load(Class paramClass, String paramString)
/*   */   {
/* 9 */     return JarLib.load(jtwain.class, "jtwain");
/*   */   }
/*   */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainDefaultNativeLoadStrategy
 * JD-Core Version:    0.6.2
 */