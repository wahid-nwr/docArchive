/*    */ package leadtools;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RasterImageAnimationDisposalMethod
/*    */ {
/*  5 */   NONE(0), 
/*  6 */   LEAVE(1), 
/*  7 */   RESTORE_BACKGROUND(2), 
/*  8 */   RESTORE_PREVIOUS(3);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RasterImageAnimationDisposalMethod> mappings;
/*    */ 
/* 14 */   private static HashMap<Integer, RasterImageAnimationDisposalMethod> getMappings() { if (mappings == null)
/*    */     {
/* 16 */       synchronized (RasterImageAnimationDisposalMethod.class)
/*    */       {
/* 18 */         if (mappings == null)
/*    */         {
/* 20 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 24 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RasterImageAnimationDisposalMethod(int value)
/*    */   {
/* 29 */     this.intValue = value;
/* 30 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 35 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterImageAnimationDisposalMethod forValue(int value)
/*    */   {
/* 40 */     return (RasterImageAnimationDisposalMethod)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImageAnimationDisposalMethod
 * JD-Core Version:    0.6.2
 */