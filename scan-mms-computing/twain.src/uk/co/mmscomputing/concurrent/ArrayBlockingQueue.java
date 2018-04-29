/*     */ package uk.co.mmscomputing.concurrent;
/*     */ 
/*     */ public class ArrayBlockingQueue
/*     */ {
/*     */   protected Object[] buffer;
/*     */   protected int size;
/*     */   protected int front;
/*     */   protected int rear;
/*     */   protected boolean fcarry;
/*     */   protected boolean rcarry;
/*     */   protected Semaphore empty;
/*     */   protected Semaphore full;
/*     */   protected Object pt;
/*     */   protected Object gt;
/*     */ 
/*     */   public ArrayBlockingQueue(int paramInt)
/*     */   {
/*  16 */     if (paramInt < 1) {
/*  17 */       throw new IllegalArgumentException(getClass().getName() + ".<init>(int init)\n\tParameter init must be greater than zero.");
/*     */     }
/*     */ 
/*  21 */     this.buffer = new Object[paramInt];
/*  22 */     this.size = paramInt;
/*  23 */     this.front = 0; this.rear = 0;
/*  24 */     this.fcarry = false; this.rcarry = false;
/*  25 */     this.empty = new Semaphore(paramInt, true);
/*  26 */     this.full = new Semaphore(0, true);
/*  27 */     this.pt = new Object();
/*  28 */     this.gt = new Object();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  32 */     return (this.front == this.rear) && (this.fcarry == this.rcarry);
/*     */   }
/*     */ 
/*     */   public boolean isFull() {
/*  36 */     return (this.front == this.rear) && (this.fcarry != this.rcarry);
/*     */   }
/*     */ 
/*     */   public synchronized int size() {
/*  40 */     int i = this.front; int j = this.rear;
/*  41 */     if ((this.fcarry != this.rcarry) || (j < i)) j += this.size;
/*  42 */     return j - i;
/*     */   }
/*     */ 
/*     */   public int remainingCapacity() {
/*  46 */     return this.size - size();
/*     */   }
/*     */ 
/*     */   protected void add(Object paramObject) {
/*  50 */     this.buffer[this.rear] = paramObject;
/*  51 */     this.rear += 1; if (this.rear >= this.size) { this.rear -= this.size; this.rcarry = (!this.rcarry); }
/*     */   }
/*     */ 
/*     */   public void put(Object paramObject) throws InterruptedException {
/*  55 */     this.empty.acquire();
/*  56 */     synchronized (this.pt) { add(paramObject); }
/*  57 */     this.full.release();
/*     */   }
/*     */ 
/*     */   public boolean offer(Object paramObject) {
/*     */     try {
/*  62 */       return offer(paramObject, 0L, TimeUnit.MILLISECONDS);
/*     */     } catch (InterruptedException localInterruptedException) {
/*  64 */       localInterruptedException.printStackTrace();
/*  65 */     }return false;
/*     */   }
/*     */ 
/*     */   public boolean offer(Object paramObject, long paramLong, TimeUnit paramTimeUnit)
/*     */     throws InterruptedException
/*     */   {
/*  71 */     if (paramObject == null) {
/*  72 */       throw new NullPointerException(getClass().getName() + ".offer(Object v,long timeout,TimeUnit unit)\n\tObject v is null.");
/*     */     }
/*  74 */     if (this.empty.tryAcquire(paramLong, paramTimeUnit)) {
/*  75 */       synchronized (this.pt) { add(paramObject); }
/*  76 */       this.full.release();
/*  77 */       return true;
/*     */     }
/*  79 */     return false;
/*     */   }
/*     */ 
/*     */   protected Object remove() {
/*  83 */     Object localObject = this.buffer[this.front];
/*  84 */     this.front += 1; if (this.front >= this.size) { this.front -= this.size; this.fcarry = (!this.fcarry); }
/*  85 */     return localObject;
/*     */   }
/*     */ 
/*     */   public Object take() throws InterruptedException
/*     */   {
/*  90 */     this.full.acquire();
/*     */     Object localObject1;
/*  91 */     synchronized (this.gt) { localObject1 = remove(); }
/*  92 */     this.empty.release();
/*  93 */     return localObject1;
/*     */   }
/*     */ 
/*     */   public Object poll() {
/*     */     try {
/*  98 */       return poll(0L, TimeUnit.MILLISECONDS);
/*     */     } catch (InterruptedException localInterruptedException) {
/* 100 */       localInterruptedException.printStackTrace();
/* 101 */     }return null;
/*     */   }
/*     */ 
/*     */   public Object poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException
/*     */   {
/* 106 */     Object localObject1 = null;
/* 107 */     if (this.full.tryAcquire(paramLong, paramTimeUnit)) {
/* 108 */       synchronized (this.gt) { localObject1 = remove(); }
/* 109 */       this.empty.release();
/*     */     }
/* 111 */     return localObject1;
/*     */   }
/*     */   public void clear() {
/* 114 */     while (poll() != null);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.concurrent.ArrayBlockingQueue
 * JD-Core Version:    0.6.2
 */