/*     */ package leadtools.sane;
/*     */ 
/*     */ import leadtools.RasterException;
/*     */ import leadtools.RasterExceptionCode;
/*     */ import leadtools.RasterSupport;
/*     */ import leadtools.RasterSupportType;
/*     */ 
/*     */ class NativeSaneSession extends SaneSession
/*     */ {
/* 191 */   private static int I = 0;
/*     */ 
/*     */   public void stop()
/*     */   {
/*  25 */     if (I == 0)
/*     */     {
/*  98 */       return;
/*     */     }
/*     */ 
/* 119 */     RasterException.checkErrorCode(ltsane.SaneExit());
/*     */ 
/* 197 */     I -= 1;
/*     */   }
/*     */ 
/*     */   private void j()
/*     */   {
/*  37 */     if (RasterSupport.isLocked(RasterSupportType.DOCUMENT))
/*     */     {
/* 100 */       throw new RasterException(RasterExceptionCode.DOCUMENT_NOT_ENABLED);
/*     */     }
/*     */   }
/*     */ 
/*     */   public NativeSaneSession()
/*     */   {
/* 133 */     j();
/*     */   }
/*     */ 
/*     */   public SaneDevice getDevice(String deviceName)
/*     */   {
/* 176 */     return new NativeSaneDevice(deviceName);
/*     */   }
/*     */ 
/*     */   public SaneDevice[] getDevices()
/*     */   {
/* 152 */     int[] a = new int[1];
/*     */ 
/* 163 */     RasterException.checkErrorCode(a[0]);
/*     */ 
/*  51 */     return 
/*  52 */       ltsane.SaneGetDevices(true, a);
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 167 */     if (I > 0) {
/*  28 */       return;
/*     */     }
/*     */ 
/*  60 */     RasterException.checkErrorCode(
/* 113 */       ltsane.SaneInit(
/* 150 */       new int[1]));
/*     */ 
/* 189 */     I += 1;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.NativeSaneSession
 * JD-Core Version:    0.6.2
 */