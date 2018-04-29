/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ public class TwainResultException extends TwainIOException
/*    */ {
/*  5 */   private int rc = -1;
/*    */ 
/*    */   public TwainResultException(String paramString, int paramInt) {
/*  8 */     super(paramString + "\n\trc=" + paramInt);
/*  9 */     this.rc = paramInt;
/*    */   }
/*    */   public int getResultCode() {
/* 12 */     return this.rc;
/*    */   }
/*    */ 
/*    */   public static class DataNotAvailable extends TwainResultException
/*    */   {
/*    */     public DataNotAvailable()
/*    */     {
/* 43 */       super(9);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class InfoNotSupported extends TwainResultException
/*    */   {
/*    */     public InfoNotSupported()
/*    */     {
/* 39 */       super(8);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class EndOfList extends TwainResultException
/*    */   {
/*    */     public EndOfList()
/*    */     {
/* 35 */       super(7);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class TransferDone extends TwainResultException
/*    */   {
/*    */     public TransferDone()
/*    */     {
/* 31 */       super(6);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class NotDSEvent extends TwainResultException
/*    */   {
/*    */     public NotDSEvent()
/*    */     {
/* 27 */       super(5);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class DSEvent extends TwainResultException
/*    */   {
/*    */     public DSEvent()
/*    */     {
/* 23 */       super(4);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class Cancel extends TwainResultException
/*    */   {
/*    */     public Cancel()
/*    */     {
/* 19 */       super(3);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class CheckStatus extends TwainResultException
/*    */   {
/*    */     public CheckStatus()
/*    */     {
/* 15 */       super(2);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainResultException
 * JD-Core Version:    0.6.2
 */