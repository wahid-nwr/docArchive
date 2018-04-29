/*    */ package uk.co.mmscomputing.concurrent;
/*    */ 
/*    */ public class SignalBlockingQueue extends ArrayBlockingQueue
/*    */ {
/*    */   public SignalBlockingQueue(int paramInt)
/*    */   {
/*  6 */     super(paramInt);
/*    */   }
/*    */ 
/*    */   public Object take() throws InterruptedException
/*    */   {
/* 11 */     this.full.acquire();
/*    */     Object localObject1;
/* 12 */     synchronized (this.gt) {
/* 13 */       localObject1 = this.buffer[this.front];
/* 14 */       this.front += 1; if (this.front >= this.size) { this.front -= this.size; this.fcarry = (!this.fcarry);
/*    */       }
/*    */     }
/* 17 */     return localObject1;
/*    */   }
/*    */ 
/*    */   public Object poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
/* 21 */     Object localObject1 = null;
/* 22 */     if (this.full.tryAcquire(paramLong, paramTimeUnit)) {
/* 23 */       synchronized (this.gt) {
/* 24 */         localObject1 = this.buffer[this.front];
/* 25 */         this.front += 1; if (this.front >= this.size) { this.front -= this.size; this.fcarry = (!this.fcarry);
/*    */         }
/*    */       }
/*    */     }
/* 29 */     return localObject1;
/*    */   }
/*    */ 
/*    */   public void signal() {
/* 33 */     this.empty.release();
/*    */   }
/*    */   public int getPutIndex() {
/* 36 */     return this.rear; } 
/* 37 */   public int getTakeIndex() { return this.front; } 
/* 38 */   public Object getObjectAtIndex(int paramInt) { return this.buffer[paramInt]; }
/*    */ 
/*    */   public void acquireWhenEmpty() throws InterruptedException {
/*    */     boolean bool;
/*    */     do {
/* 43 */       this.empty.acquire();
/* 44 */       bool = isEmpty();
/* 45 */       this.empty.release();
/* 46 */     }while (!bool);
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 50 */     while (poll() != null) signal();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.concurrent.SignalBlockingQueue
 * JD-Core Version:    0.6.2
 */