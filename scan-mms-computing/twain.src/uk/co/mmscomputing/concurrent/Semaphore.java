/*    */ package uk.co.mmscomputing.concurrent;
/*    */ 
/*    */ public class Semaphore
/*    */ {
/*    */   private int initpermits;
/*    */   private int available;
/*    */   private int waiting;
/*    */ 
/*    */   public Semaphore(int paramInt, boolean paramBoolean)
/*    */   {
/* 20 */     this.initpermits = paramInt;
/* 21 */     this.available = this.initpermits;
/* 22 */     this.waiting = 0;
/*    */   }
/*    */   public boolean isFair() {
/* 25 */     return true; } 
/* 26 */   public int availablePermits() { return this.available; }
/*    */ 
/*    */ 
/*    */   public synchronized void acquire()
/*    */     throws InterruptedException
/*    */   {
/* 37 */     while (this.available <= 0) {
/*    */       try {
/* 39 */         this.waiting += 1;
/* 40 */         wait();
/*    */ 
/* 42 */         this.waiting -= 1; } finally { this.waiting -= 1; }
/*    */ 
/*    */     }
/* 45 */     this.available -= 1;
/*    */   }
/*    */ 
/*    */   public synchronized boolean tryAcquire(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
/* 49 */     if (this.available <= 0) {
/* 50 */       if (paramLong <= 0L) return false; try
/*    */       {
/* 52 */         this.waiting += 1;
/* 53 */         wait(paramLong);
/*    */       } finally {
/* 55 */         this.waiting -= 1;
/*    */       }
/* 57 */       if (this.available <= 0) return false;
/*    */     }
/* 59 */     this.available -= 1;
/* 60 */     return true;
/*    */   }
/*    */ 
/*    */   public synchronized void release() {
/* 64 */     this.available += 1;
/* 65 */     if ((this.available > 0) && (this.waiting > 0))
/* 66 */       notify();
/*    */   }
/*    */ 
/*    */   public void release(int paramInt)
/*    */   {
/* 71 */     for (int i = 0; i < paramInt; i++)
/* 72 */       release();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.concurrent.Semaphore
 * JD-Core Version:    0.6.2
 */