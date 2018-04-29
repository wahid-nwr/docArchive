/*     */ package uk.co.mmscomputing.device.twain;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import uk.co.mmscomputing.concurrent.Semaphore;
/*     */ import uk.co.mmscomputing.concurrent.TimeUnit;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerDevice;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerIOException;
/*     */ 
/*     */ public class TwainSource extends TwainIdentity
/*     */   implements TwainConstants, ScannerDevice
/*     */ {
/*     */   private boolean busy;
/*     */   private int state;
/*     */   private long hWnd;
/*  14 */   private int showUI = 1;
/*  15 */   private int modalUI = 0;
/*     */ 
/*  17 */   private int iff = 2;
/*     */   private TwainITransferFactory transferFactory;
/*     */   private boolean userCancelled;
/*  22 */   private Semaphore tw20Semaphore = null;
/*  23 */   private boolean tw20HaveImage = false;
/*     */ 
/*     */   TwainSource(TwainSourceManager paramTwainSourceManager, long paramLong, boolean paramBoolean) {
/*  26 */     super(paramTwainSourceManager);
/*  27 */     this.hWnd = paramLong;
/*  28 */     this.busy = paramBoolean;
/*  29 */     this.state = 3;
/*     */ 
/*  31 */     this.userCancelled = false;
/*  32 */     this.transferFactory = new TwainDefaultTransferFactory();
/*     */   }
/*     */   byte[] getIdentity() {
/*  35 */     return this.identity;
/*     */   }
/*  37 */   public boolean isBusy() { return this.busy; } 
/*  38 */   void setBusy(boolean paramBoolean) { this.busy = paramBoolean; jtwain.signalStateChange(this); } 
/*  39 */   public int getState() { return this.state; } 
/*  40 */   void setState(int paramInt) { this.state = paramInt; jtwain.signalStateChange(this); } 
/*     */   public void setCancel(boolean paramBoolean) {
/*  42 */     this.userCancelled = paramBoolean; } 
/*  43 */   public boolean getCancel() { return this.userCancelled; }
/*     */ 
/*     */   void checkState(int paramInt) throws TwainIOException
/*     */   {
/*  47 */     if (this.state == paramInt) return;
/*  48 */     throw new TwainIOException(getClass().getName() + ".checkState:\n\tSource not in state " + paramInt + " but in state " + this.state + ".");
/*     */   }
/*     */ 
/*     */   int getConditionCode() throws TwainIOException {
/*  52 */     byte[] arrayOfByte = new byte[4];
/*  53 */     int i = jtwain.callSource(this.identity, 1, 8, 1, arrayOfByte);
/*  54 */     if (i != 0) {
/*  55 */       throw new TwainResultException("Cannot retrieve twain source's status.", i);
/*     */     }
/*  57 */     return jtwain.getINT16(arrayOfByte, 0);
/*     */   }
/*     */ 
/*     */   private void checkrc(int paramInt) throws TwainIOException {
/*  61 */     switch (paramInt) { case 0:
/*  62 */       return;
/*     */     case 1:
/*  63 */       throw TwainFailureException.create(getConditionCode());
/*     */     case 2:
/*  64 */       throw new TwainResultException.CheckStatus();
/*     */     case 3:
/*  65 */       throw new TwainResultException.Cancel();
/*     */     case 4:
/*  66 */       return;
/*     */     case 5:
/*  67 */       throw new TwainResultException.NotDSEvent();
/*     */     case 6:
/*  68 */       throw new TwainResultException.TransferDone();
/*     */     case 7:
/*  69 */       throw new TwainResultException.EndOfList();
/*     */     case 8:
/*  70 */       throw new TwainResultException.InfoNotSupported();
/*     */     case 9:
/*  71 */       throw new TwainResultException.DataNotAvailable();
/*     */     }
/*  73 */     System.err.println(getClass().getName() + ".checkrc\n\trc=" + paramInt);
/*  74 */     throw new TwainResultException("Failed to call source.", paramInt);
/*     */   }
/*     */ 
/*     */   public void call(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte) throws TwainIOException
/*     */   {
/*  79 */     checkrc(jtwain.callSource(this.identity, paramInt1, paramInt2, paramInt3, paramArrayOfByte));
/*     */   }
/*     */ 
/*     */   private void setCallbackProcedure()
/*     */     throws TwainIOException
/*     */   {
/*  91 */     byte[] arrayOfByte = new byte[jtwain.getPtrSize() == 4 ? 10 : 14];
/*  92 */     int i = jtwain.setPtr(arrayOfByte, 0, jtwain.getCallBackMethod());
/*  93 */     jtwain.setINT32(arrayOfByte, i, 0);
/*  94 */     i += 4; jtwain.setINT16(arrayOfByte, i, 0);
/*  95 */     call(1, 16, 2306, arrayOfByte);
/*     */   }
/*     */ 
/*     */   void open() throws TwainIOException {
/*  99 */     super.open();
/* 100 */     if (isTwain20Source())
/*     */       try {
/* 102 */         setCallbackProcedure();
/*     */       } catch (Exception localException) {
/* 104 */         maskTwain20Source();
/* 105 */         System.out.println("3\b" + getClass().getName() + ".open:\n\tCannot set twain 2.0 callback method.");
/* 106 */         System.err.println(getClass().getName() + ".open:\n\tCannot set twain 2.0 callback method.");
/*     */       }
/*     */   }
/*     */ 
/*     */   public TwainCapability[] getCapabilities() throws TwainIOException
/*     */   {
/* 112 */     return TwainCapability.getCapabilities(this);
/*     */   }
/*     */ 
/*     */   public TwainCapability getCapability(int paramInt) throws TwainIOException {
/* 116 */     return new TwainCapability(this, paramInt);
/*     */   }
/*     */ 
/*     */   public TwainCapability getCapability(int paramInt1, int paramInt2) throws TwainIOException {
/* 120 */     return new TwainCapability(this, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public TwainITransferFactory getTransferFactory() {
/* 124 */     return this.transferFactory;
/*     */   }
/*     */ 
/*     */   public void setTransferFactory(TwainITransferFactory paramTwainITransferFactory) {
/* 128 */     if (paramTwainITransferFactory == null) {
/* 129 */       throw new IllegalArgumentException(getClass().getName() + ".setTransferFactory\n\tTwain transfer factory cannot be null.");
/*     */     }
/* 131 */     this.transferFactory = paramTwainITransferFactory;
/*     */   }
/*     */   public void setShowUI(boolean paramBoolean) {
/* 134 */     this.showUI = (paramBoolean ? 1 : 0); } 
/* 135 */   public boolean isModalUI() { return this.modalUI == 1; }
/*     */ 
/*     */   public void setCapability(int paramInt, boolean paramBoolean) throws ScannerIOException {
/* 138 */     TwainCapability localTwainCapability = getCapability(paramInt, 2);
/* 139 */     if (localTwainCapability.booleanValue() != paramBoolean) {
/* 140 */       localTwainCapability.setCurrentValue(paramBoolean);
/* 141 */       if (getCapability(paramInt).booleanValue() != paramBoolean)
/* 142 */         throw new ScannerIOException(getClass().getName() + ".setCapability:\n\tCannot set capability " + TwainCapability.getCapName(paramInt) + " to " + paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setCapability(int paramInt1, int paramInt2) throws ScannerIOException
/*     */   {
/* 148 */     TwainCapability localTwainCapability = getCapability(paramInt1, 2);
/* 149 */     if (localTwainCapability.intValue() != paramInt2) {
/* 150 */       localTwainCapability.setCurrentValue(paramInt2);
/* 151 */       if (getCapability(paramInt1).intValue() != paramInt2)
/* 152 */         throw new ScannerIOException(getClass().getName() + ".setCapability:\n\tCannot set capability " + TwainCapability.getCapName(paramInt1) + " to " + paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setCapability(int paramInt, double paramDouble) throws ScannerIOException
/*     */   {
/* 158 */     TwainCapability localTwainCapability = getCapability(paramInt, 2);
/* 159 */     if (localTwainCapability.doubleValue() != paramDouble) {
/* 160 */       localTwainCapability.setCurrentValue(paramDouble);
/* 161 */       if (getCapability(paramInt).doubleValue() != paramDouble)
/* 162 */         throw new ScannerIOException(getClass().getName() + ".setCapability:\n\tCannot set capability " + TwainCapability.getCapName(paramInt) + " to " + paramDouble);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isUIControllable()
/*     */   {
/*     */     try
/*     */     {
/* 171 */       return getCapability(4110).booleanValue();
/*     */     } catch (Exception localException) {
/* 173 */       jtwain.signalException(getClass().getName() + ".isUIControllable:\n\t" + localException); } return false;
/*     */   }
/*     */ 
/*     */   public boolean isDeviceOnline()
/*     */   {
/*     */     try {
/* 179 */       return getCapability(4111).booleanValue();
/*     */     } catch (Exception localException) {
/* 181 */       jtwain.signalException(getClass().getName() + ".isOnline:\n\t" + localException); } return true;
/*     */   }
/*     */ 
/*     */   public void setShowUserInterface(boolean paramBoolean) throws ScannerIOException
/*     */   {
/* 186 */     setShowUI(paramBoolean);
/*     */   }
/*     */ 
/*     */   public void setShowProgressBar(boolean paramBoolean) throws ScannerIOException {
/* 190 */     setCapability(4107, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void setResolution(double paramDouble) throws ScannerIOException {
/* 194 */     setCapability(258, 0);
/* 195 */     setCapability(4376, paramDouble);
/* 196 */     setCapability(4377, paramDouble);
/*     */   }
/*     */ 
/*     */   public void setRegionOfInterest(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws ScannerIOException {
/* 200 */     if ((paramInt1 == -1) && (paramInt2 == -1) && (paramInt3 == -1) && (paramInt4 == -1)) {
/* 201 */       new TwainImageLayout(this).reset();
/*     */     } else {
/* 203 */       setCapability(258, 5);
/* 204 */       TwainImageLayout localTwainImageLayout = new TwainImageLayout(this);
/* 205 */       localTwainImageLayout.get();
/* 206 */       localTwainImageLayout.setLeft(paramInt1); localTwainImageLayout.setTop(paramInt2);
/* 207 */       localTwainImageLayout.setRight(paramInt1 + paramInt3); localTwainImageLayout.setBottom(paramInt2 + paramInt4);
/* 208 */       localTwainImageLayout.set();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setRegionOfInterest(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) throws ScannerIOException {
/* 213 */     if ((paramDouble1 == -1.0D) && (paramDouble2 == -1.0D) && (paramDouble3 == -1.0D) && (paramDouble4 == -1.0D)) {
/* 214 */       new TwainImageLayout(this).reset();
/*     */     } else {
/* 216 */       setCapability(258, 1);
/* 217 */       TwainImageLayout localTwainImageLayout = new TwainImageLayout(this);
/* 218 */       localTwainImageLayout.get();
/* 219 */       localTwainImageLayout.setLeft(paramDouble1 / 10.0D); localTwainImageLayout.setTop(paramDouble2 / 10.0D);
/* 220 */       localTwainImageLayout.setRight((paramDouble1 + paramDouble3) / 10.0D); localTwainImageLayout.setBottom((paramDouble2 + paramDouble4) / 10.0D);
/* 221 */       localTwainImageLayout.set();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void select(String paramString) throws ScannerIOException
/*     */   {
/* 227 */     checkState(3);
/* 228 */     TwainSourceManager localTwainSourceManager = jtwain.getSourceManager();
/*     */     try {
/* 230 */       TwainIdentity localTwainIdentity = new TwainIdentity(localTwainSourceManager);
/* 231 */       localTwainIdentity.getFirst();
/*     */       while (true) {
/* 233 */         if (localTwainIdentity.getProductName().equals(paramString)) {
/* 234 */           System.arraycopy(localTwainIdentity.identity, 0, this.identity, 0, this.identity.length); break;
/*     */         }
/* 236 */         localTwainIdentity.getNext();
/*     */       }
/*     */     } catch (TwainResultException.EndOfList localEndOfList) {
/* 239 */       throw new TwainIOException(getClass().getName() + ".select(String name)\n\tCannot find twain data source: '" + paramString + "'");
/*     */     }
/*     */   }
/*     */ 
/*     */   void enable()
/*     */     throws TwainIOException
/*     */   {
/* 248 */     checkState(4);
/* 249 */     jtwain.negotiateCapabilities(this);
/* 250 */     if (getState() < 4) return;
/*     */ 
/* 252 */     int i = new TwainCapability.XferMech(this).intValue();
/* 253 */     if ((i != 0) && 
/* 254 */       (i == 1)) {
/*     */       try {
/* 256 */         this.iff = getCapability(4364).intValue();
/*     */       } catch (Exception localException) {
/* 258 */         this.iff = 2;
/*     */       }
/*     */     }
/* 261 */     if (isTwain20Source()) {
/* 262 */       this.tw20Semaphore = new Semaphore(0, true);
/* 263 */       this.tw20HaveImage = false;
/*     */     }
/* 265 */     byte[] arrayOfByte = null;
/*     */     try {
/* 267 */       arrayOfByte = new byte[jtwain.getPtrSize() == 4 ? 8 : 12];
/* 268 */       jtwain.setINT16(arrayOfByte, 0, this.showUI);
/* 269 */       jtwain.setINT16(arrayOfByte, 2, this.modalUI);
/* 270 */       jtwain.setPtr(arrayOfByte, 4, this.hWnd);
/*     */ 
/* 272 */       call(1, 9, 1282, arrayOfByte);
/* 273 */       this.modalUI = jtwain.getINT16(arrayOfByte, 2);
/* 274 */       setState(5);
/*     */     }
/*     */     catch (TwainResultException.CheckStatus localCheckStatus) {
/* 277 */       setState(5);
/* 278 */       this.showUI = ((this.showUI ^ 0xFFFFFFFF) & 0x1);
/*     */     } catch (TwainResultException.Cancel localCancel) {
/* 280 */       disable();
/* 281 */       close();
/*     */     }
/* 283 */     if (isTwain20Source())
/*     */       try {
/* 285 */         this.tw20Semaphore.tryAcquire(60000L, TimeUnit.MILLISECONDS);
/* 286 */         this.tw20Semaphore.release();
/*     */ 
/* 288 */         if (this.tw20HaveImage == true) {
/* 289 */           transferImage();
/*     */         } else {
/* 291 */           System.out.println("9\b" + getClass().getName() + ".enable:\n\tscan timed out. Close data source.");
/* 292 */           System.err.println(getClass().getName() + ".enable:\n\tscan timed out Close data source.");
/*     */         }
/*     */       } catch (TwainIOException localTwainIOException) {
/* 295 */         System.out.println("9\b" + getClass().getName() + ".enable:\n\t" + localTwainIOException);
/* 296 */         System.err.println(getClass().getName() + ".enable:\n\t" + localTwainIOException);
/*     */       } catch (InterruptedException localInterruptedException) {
/* 298 */         System.out.println("9\b" + getClass().getName() + ".enable:\n\tscan interrupted");
/* 299 */         System.err.println(getClass().getName() + ".enable:\n\tscan interrupted");
/*     */       }
/*     */   }
/*     */ 
/*     */   private void transfer(TwainITransfer paramTwainITransfer) throws TwainIOException
/*     */   {
/*     */     try {
/* 306 */       byte[] arrayOfByte = new byte[6];
/*     */       do {
/* 308 */         setState(6);
/* 309 */         jtwain.setINT16(arrayOfByte, 0, -1);
/*     */         try {
/* 311 */           paramTwainITransfer.setCancel(this.userCancelled);
/* 312 */           paramTwainITransfer.initiate();
/*     */         } catch (TwainResultException.TransferDone localTransferDone) {
/* 314 */           setState(7);
/* 315 */           paramTwainITransfer.finish();
/* 316 */           call(1, 5, 1793, arrayOfByte);
/* 317 */           if (jtwain.getINT16(arrayOfByte, 0) == 0)
/* 318 */             setState(5);
/*     */         }
/*     */         catch (TwainUserCancelException localTwainUserCancelException) {
/* 321 */           call(1, 5, 7, arrayOfByte);
/* 322 */           setState(5);
/*     */         } catch (TwainResultException.Cancel localCancel) {
/* 324 */           paramTwainITransfer.cancel();
/*     */ 
/* 326 */           call(1, 5, 1793, arrayOfByte);
/* 327 */           if (jtwain.getINT16(arrayOfByte, 0) > 0) {
/* 328 */             call(1, 5, 7, arrayOfByte);
/*     */           }
/* 330 */           setState(5);
/*     */         } catch (TwainFailureException localTwainFailureException) {
/* 332 */           jtwain.signalException(getClass().getName() + ".transfer:\n\t" + localTwainFailureException);
/*     */ 
/* 334 */           call(1, 5, 1793, arrayOfByte);
/* 335 */           if (jtwain.getINT16(arrayOfByte, 0) > 0) {
/* 336 */             call(1, 5, 7, arrayOfByte);
/*     */           }
/* 338 */           setState(5);
/*     */         } finally {
/* 340 */           paramTwainITransfer.cleanup();
/*     */         }
/*     */       }
/* 342 */       while (jtwain.getINT16(arrayOfByte, 0) != 0);
/*     */     } finally {
/* 344 */       if ((this.userCancelled) || (this.showUI == 0)) {
/* 345 */         this.userCancelled = false;
/* 346 */         disable();
/* 347 */         close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void transferImage() throws TwainIOException {
/* 353 */     switch (getXferMech()) { case 0:
/* 354 */       transfer(this.transferFactory.createNativeTransfer(this)); break;
/*     */     case 1:
/* 355 */       transfer(this.transferFactory.createFileTransfer(this)); break;
/*     */     case 2:
/* 356 */       transfer(this.transferFactory.createMemoryTransfer(this)); break;
/*     */     default:
/* 358 */       System.out.println(getClass().getName() + ".transferImage:\n\tDo not support this transfer mode: " + getXferMech());
/* 359 */       System.err.println(getClass().getName() + ".transferImage:\n\tDo not support this transfer mode: " + getXferMech()); }
/*     */   }
/*     */ 
/*     */   void disable()
/*     */     throws TwainIOException
/*     */   {
/* 365 */     if (this.state < 5) return;
/*     */ 
/* 367 */     byte[] arrayOfByte = new byte[jtwain.getPtrSize() == 4 ? 8 : 12];
/* 368 */     jtwain.setINT16(arrayOfByte, 0, -1);
/* 369 */     jtwain.setINT16(arrayOfByte, 2, 0);
/* 370 */     jtwain.setPtr(arrayOfByte, 4, this.hWnd);
/*     */ 
/* 372 */     call(1, 9, 1281, arrayOfByte);
/* 373 */     setState(4);
/*     */   }
/*     */ 
/*     */   void close() throws TwainIOException {
/* 377 */     if (this.state != 4) return;
/* 378 */     call(1, 3, 1026, this.identity);
/* 379 */     this.busy = false;
/* 380 */     setState(3);
/*     */   }
/*     */ 
/*     */   int callback(int paramInt1, int paramInt2, int paramInt3, long paramLong) throws TwainIOException {
/* 384 */     switch (paramInt3) {
/*     */     case 257:
/* 386 */       this.tw20HaveImage = true;
/* 387 */       this.tw20Semaphore.release();
/* 388 */       break;
/*     */     case 258:
/*     */     case 259:
/* 392 */       System.err.println("MSG_CLOSEDSOK,MSG_CLOSEDSREQ in callback routine");
/* 393 */       this.tw20HaveImage = false;
/* 394 */       this.tw20Semaphore.release();
/* 395 */       break;
/*     */     case 0:
/*     */     case 260:
/* 398 */       break;
/*     */     default:
/* 400 */       System.err.println("9\b" + getClass().getName() + ".callback:\n\tUnknown message in Twain 2.0 callback routine");
/* 401 */       return 1;
/*     */     }
/* 403 */     return 0;
/*     */   }
/*     */ 
/*     */   int handleGetMessage(long paramLong) throws TwainIOException {
/* 407 */     if (this.state < 5) return 5; try
/*     */     {
/* 409 */       byte[] arrayOfByte = new byte[jtwain.getPtrSize() == 4 ? 6 : 10];
/* 410 */       int i = jtwain.setPtr(arrayOfByte, 0, paramLong);
/* 411 */       jtwain.setINT16(arrayOfByte, i, 0);
/* 412 */       call(1, 2, 1537, arrayOfByte);
/* 413 */       int j = jtwain.getINT16(arrayOfByte, i);
/* 414 */       switch (j) {
/*     */       case 257:
/* 416 */         transferImage();
/* 417 */         break;
/*     */       case 258:
/*     */       case 259:
/* 421 */         disable();
/* 422 */         close();
/* 423 */         break;
/*     */       case 0:
/*     */       case 260:
/*     */       }
/*     */ 
/* 428 */       return 4; } catch (TwainResultException.NotDSEvent localNotDSEvent) {
/*     */     }
/* 430 */     return 5;
/*     */   }
/*     */ 
/*     */   public int getXferMech() throws TwainIOException
/*     */   {
/* 435 */     return new TwainCapability.XferMech(this).intValue();
/*     */   }
/*     */ 
/*     */   public void setXferMech(int paramInt) {
/*     */     try {
/* 440 */       switch (paramInt) { case 0:
/*     */       case 1:
/*     */       case 2:
/* 443 */         break;
/*     */       default:
/* 444 */         paramInt = 0;
/*     */       }
/*     */ 
/* 447 */       TwainCapability localTwainCapability = getCapability(259, 2);
/* 448 */       if (localTwainCapability.intValue() != paramInt) {
/* 449 */         localTwainCapability.setCurrentValue(paramInt);
/* 450 */         if (getCapability(259).intValue() != paramInt)
/* 451 */           jtwain.signalException(getClass().getName() + ".setXferMech:\n\tCannot change transfer mechanism to mode=" + paramInt);
/*     */       }
/*     */     }
/*     */     catch (TwainIOException localTwainIOException) {
/* 455 */       jtwain.signalException(getClass().getName() + ".setXferMech:\n\t" + localTwainIOException);
/*     */     }
/*     */   }
/*     */ 
/* 459 */   int getImageFileFormat() { return this.iff; }
/*     */ 
/*     */   public void setImageFileFormat(int paramInt)
/*     */   {
/*     */     try {
/* 464 */       switch (paramInt) { case 0:
/*     */       case 2:
/*     */       case 4:
/*     */       case 6:
/*     */       case 7:
/* 467 */         break;
/*     */       case 1:
/*     */       case 3:
/*     */       case 5:
/*     */       default:
/* 469 */         paramInt = 2;
/*     */       }
/*     */ 
/* 472 */       TwainCapability localTwainCapability = getCapability(4364, 2);
/* 473 */       if (localTwainCapability.intValue() != paramInt) {
/* 474 */         localTwainCapability.setCurrentValue(paramInt);
/* 475 */         if (getCapability(4364).intValue() != paramInt)
/* 476 */           jtwain.signalException(getClass().getName() + ".setImageFileFormat:\n\tCannot change file format to format=" + paramInt);
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {
/* 480 */       jtwain.signalException(getClass().getName() + ".setImageFileFormat:\n\t" + localException);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainSource
 * JD-Core Version:    0.6.2
 */