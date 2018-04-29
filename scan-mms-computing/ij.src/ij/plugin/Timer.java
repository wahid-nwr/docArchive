/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ 
/*     */ public class Timer
/*     */   implements PlugIn
/*     */ {
/*   7 */   int j = 0;
/*     */   long startTime;
/*     */   long nullLoopTime;
/*     */   int numLoops;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  13 */     int j = 0;
/*  14 */     int[] a = new int[10];
/*     */ 
/*  25 */     this.numLoops = 10000;
/*     */     do {
/*  27 */       this.numLoops = ((int)(this.numLoops * 1.33D));
/*  28 */       this.startTime = System.currentTimeMillis();
/*  29 */       for (int i = 0; i < this.numLoops; i++);
/*  30 */       this.nullLoopTime = (System.currentTimeMillis() - this.startTime);
/*     */     }
/*  32 */     while (this.nullLoopTime < 250L);
/*     */ 
/*  34 */     IJ.write("");
/*  35 */     IJ.write("Timer: " + this.numLoops + " iterations (" + this.nullLoopTime + "ms)");
/*  36 */     Timer2 o = new Timer2();
/*     */ 
/*  39 */     this.startTime = System.currentTimeMillis();
/*  40 */     for (int i = 0; i < this.numLoops; i++);
/*  41 */     showTime("null loop");
/*     */ 
/*  44 */     this.startTime = System.currentTimeMillis();
/*     */     int k;
/*  45 */     for (int i = 0; i < this.numLoops; i++) k = o.getJ();
/*  46 */     showTime("i=o.getJ()");
/*     */ 
/*  49 */     this.startTime = System.currentTimeMillis();
/*     */     int k;
/*  50 */     for (int i = 0; i < this.numLoops; i++) k = o.getJFinal();
/*  51 */     showTime("i=o.getJ() (final)");
/*     */ 
/*  54 */     this.startTime = System.currentTimeMillis();
/*     */     int k;
/*  55 */     for (int i = 0; i < this.numLoops; i++) k = Timer2.getJClass();
/*  56 */     showTime("i=o.getJ() (static)");
/*     */ 
/*  59 */     this.startTime = System.currentTimeMillis();
/*     */     int k;
/*  60 */     for (int i = 0; i < this.numLoops; i++) k = o.j;
/*  61 */     showTime("i=o.j");
/*     */ 
/*  64 */     this.startTime = System.currentTimeMillis();
/*     */     int k;
/*  65 */     for (int i = 0; i < this.numLoops; i++) k = Timer2.k;
/*  66 */     showTime("i=o.j (static)");
/*     */ 
/*  69 */     this.startTime = System.currentTimeMillis();
/*     */     int k;
/*  70 */     for (int i = 0; i < this.numLoops; i++) k = j;
/*  71 */     showTime("i=j");
/*     */ 
/*  74 */     this.startTime = System.currentTimeMillis();
/*     */     int k;
/*  75 */     for (int i = 0; i < this.numLoops; i++) k = a[j];
/*  76 */     showTime("i=a[j]");
/*     */   }
/*     */ 
/*     */   void showTime(String s)
/*     */   {
/*  99 */     long elapsedTime = System.currentTimeMillis() - this.startTime - this.nullLoopTime;
/* 100 */     IJ.write("  " + s + ": " + elapsedTime * 1000000L / this.numLoops + " ns");
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Timer
 * JD-Core Version:    0.6.2
 */