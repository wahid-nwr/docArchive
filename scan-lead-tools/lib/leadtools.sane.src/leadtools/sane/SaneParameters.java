/*     */ package leadtools.sane;
/*     */ 
/*     */ public class SaneParameters
/*     */ {
/*     */   private int k;
/*     */   private int l;
/*     */   private int a;
/*     */   private int L;
/*     */   private int D;
/*     */   private boolean I;
/*     */ 
/*     */   public int getHeight()
/*     */   {
/*  28 */     return this.L; } 
/*     */   SaneParameters(int arg1, boolean arg2, int arg3, int arg4, int arg5, int depth) { // Byte code:
/*     */     //   0: iload 6
/*     */     //   2: aload_0
/*     */     //   3: dup_x1
/*     */     //   4: iload 5
/*     */     //   6: iload 4
/*     */     //   8: aload_0
/*     */     //   9: dup_x1
/*     */     //   10: iload_3
/*     */     //   11: iload_2
/*     */     //   12: aload_0
/*     */     //   13: dup_x1
/*     */     //   14: iload_1
/*     */     //   15: aload_0
/*     */     //   16: invokespecial 1	java/lang/Object:<init>	()V
/*     */     //   19: putfield 2	leadtools/sane/SaneParameters:k	I
/*     */     //   22: putfield 3	leadtools/sane/SaneParameters:I	Z
/*     */     //   25: putfield 4	leadtools/sane/SaneParameters:D	I
/*     */     //   28: putfield 5	leadtools/sane/SaneParameters:l	I
/*     */     //   31: putfield 6	leadtools/sane/SaneParameters:L	I
/*     */     //   34: putfield 7	leadtools/sane/SaneParameters:a	I
/*     */     //   37: return } 
/*  33 */   public int getWidth() { return this.l; }
/*     */ 
/*     */ 
/*     */   public int getBytesPerLine()
/*     */   {
/* 110 */     return this.D;
/*     */   }
/*     */ 
/*     */   public int getDepth()
/*     */   {
/*  22 */     return this.a;
/*     */   }
/*     */ 
/*     */   SaneParameters()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SaneFrame getFormat()
/*     */   {
/* 154 */     return SaneFrame.forValue(this.k);
/*     */   }
/*     */ 
/*     */   public boolean isLastFrame()
/*     */   {
/* 114 */     return this.I;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneParameters
 * JD-Core Version:    0.6.2
 */