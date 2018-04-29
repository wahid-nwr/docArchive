/*    */ package uk.co.mmscomputing.concurrent;
/*    */ 
/*    */ public class Timer extends Semaphore
/*    */ {
/*    */   private boolean waiting;
/*    */   protected int timeout;
/*    */   protected int delay;
/*    */   protected TimerListener listener;
/*    */ 
/*    */   public Timer(int paramInt)
/*    */   {
/* 11 */     super(0, true);
/* 12 */     this.timeout = paramInt;
/*    */   }
/*    */   public void setDelay(int paramInt) {
/* 15 */     this.delay = paramInt; } 
/* 16 */   public void setListener(TimerListener paramTimerListener) { this.listener = paramTimerListener; }
/*    */ 
/*    */   public void acquire() throws InterruptedException {
/* 19 */     this.waiting = true;
/* 20 */     new CountdownThread().start();
/* 21 */     super.acquire();
/* 22 */     this.waiting = false;
/*    */   }
/*    */ 
/*    */   public boolean tryAcquire(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
/* 26 */     this.waiting = true;
/* 27 */     new CountdownThread().start();
/* 28 */     boolean bool = super.tryAcquire(paramLong, paramTimeUnit);
/* 29 */     this.waiting = false;
/* 30 */     return bool;
/*    */   }
/*    */   class CountdownThread extends Thread {
/*    */     CountdownThread() {
/*    */     }
/* 35 */     public void run() { int i = Timer.this.timeout;
/*    */ 
/* 37 */       Timer.this.listener.begin(i);
/* 38 */       while (Timer.this.waiting)
/*    */         try {
/* 40 */           i -= Timer.this.delay;
/* 41 */           sleep(Timer.this.delay);
/* 42 */           Timer.this.listener.tick(i);
/*    */         } catch (InterruptedException localInterruptedException) {
/*    */         }
/* 45 */       Timer.this.listener.end(i);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.concurrent.Timer
 * JD-Core Version:    0.6.2
 */