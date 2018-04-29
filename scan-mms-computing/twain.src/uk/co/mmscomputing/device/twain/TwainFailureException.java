/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ public class TwainFailureException extends TwainResultException
/*    */ {
/*    */   private static final int TWCC_BADCAP = 6;
/*    */   private static final int TWCC_CAPUNSUPPORTED = 13;
/*    */   private int cc;
/*    */ 
/*    */   public TwainFailureException(int paramInt)
/*    */   {
/* 33 */     super(createMessage("Failed during call to twain source.", paramInt), 1);
/*    */   }
/*    */ 
/*    */   public TwainFailureException(String paramString, int paramInt) {
/* 37 */     super(createMessage(paramString, paramInt), 1);
/*    */   }
/*    */   public int getConditionCode() {
/* 40 */     return this.cc;
/*    */   }
/*    */   private static String createMessage(String paramString, int paramInt) {
/*    */     try {
/* 44 */       return paramString + "\n\tcc=" + TwainConstants.info[paramInt]; } catch (Exception localException) {
/*    */     }
/* 46 */     return paramString + "\n\tcc=0x" + Integer.toHexString(paramInt);
/*    */   }
/*    */ 
/*    */   public static TwainFailureException create(int paramInt)
/*    */   {
/* 51 */     switch (paramInt) { case 6:
/* 52 */       return new BadCap();
/*    */     case 13:
/* 53 */       return new CapUnsupported();
/*    */     }
/* 55 */     return new TwainFailureException(paramInt);
/*    */   }
/*    */ 
/*    */   public static class CapUnsupported extends TwainFailureException
/*    */   {
/*    */     public CapUnsupported()
/*    */     {
/* 63 */       super();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class BadCap extends TwainFailureException
/*    */   {
/*    */     public BadCap()
/*    */     {
/* 59 */       super();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainFailureException
 * JD-Core Version:    0.6.2
 */