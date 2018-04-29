/*    */ package uk.co.mmscomputing.util;
/*    */ 
/*    */ import javax.swing.ImageIcon;
/*    */ 
/*    */ public class JarImageIcon extends ImageIcon
/*    */ {
/*  8 */   private static String icons = "uk/co/mmscomputing/images/icons/";
/*    */ 
/*    */   public JarImageIcon(Class paramClass, String paramString1, String paramString2) {
/* 11 */     super(paramClass.getClassLoader().getResource(icons + paramString1), paramString2);
/*    */   }
/*    */ 
/*    */   public JarImageIcon(Class paramClass, String paramString) {
/* 15 */     super(paramClass.getClassLoader().getResource(icons + paramString));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.JarImageIcon
 * JD-Core Version:    0.6.2
 */