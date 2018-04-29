/*     */ package uk.co.mmscomputing.concurrent;
/*     */ 
/*     */ public class BufferBlockingQueue
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
/*     */   public BufferBlockingQueue(int paramInt1, int paramInt2)
/*     */   {
/*  17 */     if (paramInt1 < 1) {
/*  18 */       throw new IllegalArgumentException(getClass().getName() + ".<init>(int init)\n\tParameter init must be greater than zero.");
/*     */     }
/*     */ 
/*  22 */     this.buffer = new Object[paramInt1];
/*  23 */     for (int i = 0; i < paramInt1; i++) {
/*  24 */       this.buffer[i] = new byte[paramInt2];
/*     */     }
/*  26 */     this.size = paramInt1;
/*  27 */     this.front = 0; this.rear = 0;
/*  28 */     this.fcarry = false; this.rcarry = false;
/*  29 */     this.empty = new Semaphore(paramInt1, true);
/*  30 */     this.full = new Semaphore(0, true);
/*  31 */     this.pt = new Object();
/*  32 */     this.gt = new Object();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  36 */     return (this.front == this.rear) && (this.fcarry == this.rcarry);
/*     */   }
/*     */ 
/*     */   public boolean isFull() {
/*  40 */     return (this.front == this.rear) && (this.fcarry != this.rcarry);
/*     */   }
/*     */ 
/*     */   public int size() {
/*  44 */     int i = this.front; int j = this.rear;
/*  45 */     if ((this.fcarry != this.rcarry) || (j < i)) j += this.size;
/*  46 */     return j - i;
/*     */   }
/*     */ 
/*     */   public int remainingCapacity() {
/*  50 */     return this.size - size();
/*     */   }
/*     */ 
/*     */   public void put(byte[] paramArrayOfByte)
/*     */     throws InterruptedException
/*     */   {
/*  56 */     this.empty.acquire();
/*  57 */     synchronized (this.pt) {
/*  58 */       byte[] arrayOfByte = (byte[])this.buffer[this.rear];
/*  59 */       System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, arrayOfByte.length);
/*  60 */       this.rear += 1; if (this.rear >= this.size) { this.rear -= this.size; this.rcarry = (!this.rcarry); }
/*     */     }
/*  62 */     this.full.release();
/*     */   }
/*     */ 
/*     */   public boolean offer(byte[] paramArrayOfByte) {
/*     */     try {
/*  67 */       return offer(paramArrayOfByte, 0L, TimeUnit.MILLISECONDS);
/*     */     } catch (InterruptedException localInterruptedException) {
/*  69 */       localInterruptedException.printStackTrace();
/*  70 */     }return false;
/*     */   }
/*     */ 
/*     */   public boolean offer(byte[] paramArrayOfByte, long paramLong, TimeUnit paramTimeUnit)
/*     */     throws InterruptedException
/*     */   {
/*  77 */     if (paramArrayOfByte == null) {
/*  78 */       throw new NullPointerException(getClass().getName() + ".offer(byte[] v,long timeout,TimeUnit unit)\n\tbyte[] v is null.");
/*     */     }
/*  80 */     if (this.empty.tryAcquire(paramLong, paramTimeUnit)) {
/*  81 */       synchronized (this.pt) {
/*  82 */         byte[] arrayOfByte = (byte[])this.buffer[this.rear];
/*  83 */         System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, arrayOfByte.length);
/*  84 */         this.rear += 1; if (this.rear >= this.size) { this.rear -= this.size; this.rcarry = (!this.rcarry); }
/*     */       }
/*  86 */       this.full.release();
/*  87 */       return true;
/*     */     }
/*  89 */     return false;
/*     */   }
/*     */ 
/*     */   public byte[] take() throws InterruptedException
/*     */   {
/*  94 */     this.full.acquire();
/*     */     byte[] arrayOfByte;
/*  95 */     synchronized (this.gt) {
/*  96 */       arrayOfByte = (byte[])this.buffer[this.front];
/*  97 */       this.front += 1; if (this.front >= this.size) { this.front -= this.size; this.fcarry = (!this.fcarry); }
/*     */     }
/*  99 */     this.empty.release();
/* 100 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public byte[] poll() {
/*     */     try {
/* 105 */       return poll(0L, TimeUnit.MILLISECONDS);
/*     */     } catch (InterruptedException localInterruptedException) {
/* 107 */       localInterruptedException.printStackTrace();
/* 108 */     }return null;
/*     */   }
/*     */ 
/*     */   public byte[] poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException
/*     */   {
/* 113 */     byte[] arrayOfByte = null;
/* 114 */     if (this.full.tryAcquire(paramLong, paramTimeUnit)) {
/* 115 */       synchronized (this.gt) {
/* 116 */         arrayOfByte = (byte[])this.buffer[this.front];
/* 117 */         this.front += 1; if (this.front >= this.size) { this.front -= this.size; this.fcarry = (!this.fcarry); }
/*     */       }
/* 119 */       this.empty.release();
/*     */     }
/* 121 */     return arrayOfByte;
/*     */   }
/*     */   public void clear() {
/* 124 */     while (poll() != null);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.concurrent.BufferBlockingQueue
 * JD-Core Version:    0.6.2
 */