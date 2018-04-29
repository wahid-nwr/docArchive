/*     */ package leadtools.sane;
/*     */ 
/*     */ import leadtools.RasterException;
/*     */ import leadtools.RasterExceptionCode;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.ltkrn;
/*     */ 
/*     */ class NativeSaneDevice extends SaneDevice
/*     */ {
/*     */   private DeviceInfo D;
/*     */   private long I;
/*     */ 
/*     */   NativeSaneDevice(String name, String vendor, String model, String type)
/*     */   {
/*  37 */     this.D = 
/* 100 */       new DeviceInfo(name, vendor, model, type);
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/*   3 */     RasterException.checkErrorCode(
/*  28 */       ltsane.SaneStart(this.I));
/*     */   }
/*     */ 
/*     */   public void setOptionValue(String optionName, String value)
/*     */   {
/* 188 */     RasterException.checkErrorCode(ltsane.SaneSetOptionValue(this.I, optionName, value));
/*     */   }
/*     */ 
/*     */   public void cancel()
/*     */   {
/*  22 */     RasterException.checkErrorCode(
/*  60 */       ltsane.SaneCancel(this.I));
/*     */   }
/*     */ 
/*     */   NativeSaneDevice(String name)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: aload_1
/*     */     //   2: aconst_null
/*     */     //   3: dup
/*     */     //   4: dup_x1
/*     */     //   5: invokespecial 5	leadtools/sane/NativeSaneDevice:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
/*     */     //   8: return
/*     */   }
/*     */ 
/*     */   byte[] acquireData(SaneParameters parameters)
/*     */   {
/*     */     byte[] a;
/*  51 */     start();
/*     */ 
/* 169 */     if (read(a = new byte[
/*  51 */       parameters.getBytesPerLine() * parameters.getHeight()], 
/* 105 */       0) <= 0);
/*  68 */     return a;
/*     */   }
/*     */ 
/*     */   public DeviceInfo getInfo()
/*     */   {
/* 135 */     return this.D;
/*     */   }
/*     */ 
/*     */   public RasterImage acquireImage()
/*     */   {
/* 194 */     long a = 0L;
/*     */     try
/*     */     {
/*  58 */       if (
/* 171 */         (a = ltkrn.AllocBitmapHandle()) == 0L)
/*     */       {
/* 107 */         throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*     */       }
/*  38 */       RasterException.checkErrorCode(
/*  46 */         ltsane.SaneAcquire(this.I, a));
/*     */ 
/*  42 */       return 
/* 146 */         RasterImage.createFromBitmapHandle(a);
/*     */     }
/*     */     finally
/*     */     {
/* 166 */       if (a != 0L)
/*  42 */         ltkrn.FreeBitmapHandle(a);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SaneParameters getParameters()
/*     */   {
/*  92 */     SaneParameters a = new SaneParameters();
/*     */     SaneParameters tmp13_12 = a;
/*     */ 
/* 147 */     RasterException.checkErrorCode(ltsane.SaneGetParameters(this.I, tmp13_12));
/*     */ 
/*  83 */     return tmp13_12;
/*     */   }
/*     */ 
/*     */   public SaneOptionDescriptor getOptionDescriptor(int optionIndex)
/*     */   {
/*     */     SaneOptionDescriptor[] arrayOfSaneOptionDescriptor1;
/*     */     SaneOptionDescriptor[] a;
/*  67 */     RasterException.checkErrorCode(ltsane.SaneGetOptionDescriptor(this.I, optionIndex, a));
/*  69 */     return 
/* 126 */       (arrayOfSaneOptionDescriptor1 = new SaneOptionDescriptor[1])[0];
/*     */   }
/*     */ 
/*     */   public void open()
/*     */   {
/*     */     long[] arrayOfLong1;
/*     */     long[] a;
/* 136 */     RasterException.checkErrorCode(ltsane.SaneOpen(this.D.getName(), a)); this.I = 
/*  56 */       (arrayOfLong1 = new long[1])[0];
/*     */   }
/*     */ 
/*     */   public int read(byte[] data, int offset)
/*     */   {
/*  75 */     int[] a = new int[1];
/*     */     byte[] tmp9_8 = data;
/*     */     int a;
/*  25 */     if (
/*  39 */       (a = ltsane.SaneRead(this.I, tmp9_8, tmp9_8.length, offset, a)) < 1)
/*     */     {
/* 115 */       RasterException.checkErrorCode(a);
/*     */ 
/*  98 */       ltsane.SaneCancel(this.I);
/*     */     }
/*     */ 
/* 120 */     return a[0];
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*  82 */     RasterException.checkErrorCode(ltsane.SaneClose(this.I));
/*     */   }
/*     */ 
/*     */   public String getOptionValue(String optionName)
/*     */   {
/*  19 */     int[] a = new int[1];
/*     */ 
/* 174 */     RasterException.checkErrorCode(a[0]);
/*     */ 
/* 117 */     return ltsane.SaneGetOptionValue(this.I, optionName, a);
/*     */   }
/*     */ 
/*     */   public SaneOptionDescriptor[] getOptionDescriptors()
/*     */   {
/*     */     SaneOptionDescriptor a;
/*     */     int a;
/* 145 */     SaneOptionDescriptor[] arrayOfSaneOptionDescriptor1 = new SaneOptionDescriptor[a = Integer.parseInt(getOptionValue((a = getOptionDescriptor(0))
/*  99 */       .getName()))];
/*     */     SaneOptionDescriptor[] a;
/* 124 */     a[0] = a;
/*     */     int a;
/*     */     int tmp39_37 = a; tmp39_37[getOptionDescriptor(tmp39_37)] = (a++);
/*     */ 
/*  30 */     return a;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.NativeSaneDevice
 * JD-Core Version:    0.6.2
 */