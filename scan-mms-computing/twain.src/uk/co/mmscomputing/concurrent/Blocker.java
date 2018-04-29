/*    */ package uk.co.mmscomputing.concurrent;
/*    */ 
/*    */ public class Blocker extends Semaphore
/*    */ {
/*    */   protected int timeout;
/*    */ 
/*    */   public Blocker()
/*    */   {
/*  8 */     super(0, true);
/*  9 */     this.timeout = 0;
/*    */   }
/*    */ 
/*    */   public Blocker(int paramInt) {
/* 13 */     super(0, true);
/* 14 */     this.timeout = paramInt;
/*    */   }
/*    */ 
/*    */   public void block() throws InterruptedException {
/* 18 */     if (this.timeout <= 0)
/* 19 */       acquire();
/*    */     else
/* 21 */       tryAcquire(this.timeout, TimeUnit.MILLISECONDS);
/*    */   }
/*    */ 
/*    */   public void acquire() throws InterruptedException
/*    */   {
/* 26 */     super.acquire();
/* 27 */     release();
/*    */   }
/*    */ 
/*    */   public boolean tryAcquire(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
/* 31 */     boolean bool = super.tryAcquire(paramLong, paramTimeUnit);
/* 32 */     release();
/* 33 */     return bool;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.concurrent.Blocker
 * JD-Core Version:    0.6.2
 */