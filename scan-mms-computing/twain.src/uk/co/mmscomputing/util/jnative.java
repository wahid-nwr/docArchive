/*   */ package uk.co.mmscomputing.util;
/*   */ 
/*   */ public class jnative
/*   */ {
/*   */   public static native byte[] copy(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*   */ 
/*   */   static
/*   */   {
/* 7 */     System.loadLibrary("jnative");
/*   */   }
/*   */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.jnative
 * JD-Core Version:    0.6.2
 */