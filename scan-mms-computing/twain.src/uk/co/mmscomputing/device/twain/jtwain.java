/*     */ package uk.co.mmscomputing.device.twain;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Vector;
/*     */ import uk.co.mmscomputing.concurrent.Semaphore;
/*     */ import uk.co.mmscomputing.concurrent.TimeUnit;
/*     */ 
/*     */ public class jtwain
/*     */   implements TwainConstants
/*     */ {
/*     */   private static boolean installed;
/*     */   private static Thread nativeThread;
/*     */   private static TwainSourceManager sourceManager;
/*     */   private static WeakReference scanner;
/*  26 */   private static int ptrSize = 0;
/*     */ 
/*  42 */   private static long ptrCallback = 0L;
/*     */ 
/*     */   private static native long ninitLib();
/*     */ 
/*     */   private static native void nstart();
/*     */ 
/*     */   private static native int ngetPtrSize();
/*     */ 
/*     */   static int getPtrSize()
/*     */   {
/*  27 */     return ptrSize;
/*     */   }
/*     */   private static native void ntrigger(Object paramObject, int paramInt);
/*     */ 
/*     */   private static native int ncallSourceManager(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte) throws TwainException;
/*     */ 
/*     */   static native byte[] ngetContainer(byte[] paramArrayOfByte);
/*     */ 
/*     */   static native void nsetContainer(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*     */ 
/*     */   static native void nfreeContainer(byte[] paramArrayOfByte);
/*     */ 
/*     */   private static native int ncallSource(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte2) throws TwainException;
/*     */ 
/*     */   private static native long ngetCallBackMethod();
/*     */ 
/*  43 */   static long getCallBackMethod() { return ptrCallback; }
/*     */ 
/*     */   private static native Object ntransferImage(long paramLong);
/*     */ 
/*     */   static native void nnew(byte[] paramArrayOfByte, int paramInt);
/*     */ 
/*     */   static native int ncopy(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt);
/*     */ 
/*     */   static native void ndelete(byte[] paramArrayOfByte);
/*     */ 
/*     */   public static boolean isInstalled() {
/*  54 */     return installed;
/*     */   }
/*  56 */   static int getINT16(byte[] paramArrayOfByte, int paramInt) { return paramArrayOfByte[(paramInt++)] & 0xFF | paramArrayOfByte[paramInt] << 8; } 
/*  57 */   static void setINT16(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { paramArrayOfByte[(paramInt1++)] = ((byte)paramInt2); paramArrayOfByte[(paramInt1++)] = ((byte)(paramInt2 >> 8)); } 
/*  58 */   static int getINT32(byte[] paramArrayOfByte, int paramInt) { return paramArrayOfByte[(paramInt++)] & 0xFF | (paramArrayOfByte[(paramInt++)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt++)] & 0xFF) << 16 | paramArrayOfByte[paramInt] << 24; } 
/*  59 */   static void setINT32(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { paramArrayOfByte[(paramInt1++)] = ((byte)paramInt2); paramArrayOfByte[(paramInt1++)] = ((byte)(paramInt2 >> 8)); paramArrayOfByte[(paramInt1++)] = ((byte)(paramInt2 >> 16)); paramArrayOfByte[(paramInt1++)] = ((byte)(paramInt2 >> 24)); }
/*     */ 
/*     */   static long getINT64(byte[] paramArrayOfByte, int paramInt) {
/*  62 */     return paramArrayOfByte[(paramInt++)] & 0xFF | (paramArrayOfByte[(paramInt++)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt++)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt++)] & 0xFF) << 24 | (paramArrayOfByte[(paramInt++)] & 0xFF) << 32 | (paramArrayOfByte[(paramInt++)] & 0xFF) << 40 | (paramArrayOfByte[(paramInt++)] & 0xFF) << 48 | (paramArrayOfByte[(paramInt++)] & 0xFF) << 56;
/*     */   }
/*     */ 
/*     */   static void setINT64(byte[] paramArrayOfByte, int paramInt, long paramLong)
/*     */   {
/*  67 */     paramArrayOfByte[(paramInt++)] = ((byte)(int)paramLong); paramArrayOfByte[(paramInt++)] = ((byte)(int)(paramLong >> 8)); paramArrayOfByte[(paramInt++)] = ((byte)(int)(paramLong >> 16)); paramArrayOfByte[(paramInt++)] = ((byte)(int)(paramLong >> 24));
/*  68 */     paramArrayOfByte[(paramInt++)] = ((byte)(int)(paramLong >> 32)); paramArrayOfByte[(paramInt++)] = ((byte)(int)(paramLong >> 40)); paramArrayOfByte[(paramInt++)] = ((byte)(int)(paramLong >> 48)); paramArrayOfByte[(paramInt++)] = ((byte)(int)(paramLong >> 56));
/*     */   }
/*     */ 
/*     */   static long getPtr(byte[] paramArrayOfByte, int paramInt) {
/*  72 */     if (ptrSize == 8) return getINT64(paramArrayOfByte, paramInt);
/*  73 */     return getINT32(paramArrayOfByte, paramInt);
/*     */   }
/*     */ 
/*     */   static int setPtr(byte[] paramArrayOfByte, int paramInt, long paramLong)
/*     */   {
/*  78 */     if (ptrSize == 8) { setINT64(paramArrayOfByte, paramInt, paramLong); return 8; }
/*  79 */     setINT32(paramArrayOfByte, paramInt, (int)paramLong); return 4;
/*     */   }
/*     */ 
/*     */   static double getFIX32(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/*  84 */     int i = paramArrayOfByte[(paramInt++)] & 0xFF | paramArrayOfByte[(paramInt++)] << 8;
/*  85 */     int j = paramArrayOfByte[(paramInt++)] & 0xFF | (paramArrayOfByte[paramInt] & 0xFF) << 8;
/*  86 */     return i + j / 65536.0D;
/*     */   }
/*     */ 
/*     */   static void setFIX32(byte[] paramArrayOfByte, int paramInt, double paramDouble) {
/*  90 */     int i = (int)(paramDouble * 65536.0D + (paramDouble < 0.0D ? -0.5D : 0.5D));
/*  91 */     setINT16(paramArrayOfByte, paramInt, i >> 16);
/*  92 */     setINT16(paramArrayOfByte, paramInt + 2, i & 0xFFFF);
/*     */   }
/*     */ 
/*     */   static void setString(byte[] paramArrayOfByte, int paramInt, String paramString) {
/*  96 */     System.arraycopy(paramString.getBytes(), 0, paramArrayOfByte, 0, paramString.length());
/*     */   }
/*     */ 
/*     */   static int callSourceManager(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte) throws TwainIOException {
/* 100 */     if (Thread.currentThread() != nativeThread)
/* 101 */       throw new TwainIOException("Call twain source manager only from within native thread.");
/*     */     try
/*     */     {
/* 104 */       return ncallSourceManager(paramInt1, paramInt2, paramInt3, paramArrayOfByte);
/*     */     } catch (TwainException localTwainException) {
/* 106 */       installed = false; throw localTwainException;
/*     */     }
/*     */   }
/*     */ 
/*     */   static int callSource(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte2) throws TwainIOException {
/* 111 */     if (Thread.currentThread() != nativeThread)
/* 112 */       throw new TwainIOException("Call twain source only from within native thread.");
/*     */     try
/*     */     {
/* 115 */       return ncallSource(paramArrayOfByte1, paramInt1, paramInt2, paramInt3, paramArrayOfByte2);
/*     */     } catch (TwainException localTwainException) {
/* 117 */       installed = false; throw localTwainException;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static TwainSourceManager getSourceManager() throws TwainIOException {
/*     */     try {
/* 123 */       for (; (sourceManager == null) && (installed); Thread.sleep(100L)) Thread.currentThread(); 
/*     */     } catch (Exception localException) {  }
/*     */ 
/* 125 */     if (!installed) {
/* 126 */       throw new TwainIOException("Cannot load Twain Source Manager.");
/*     */     }
/* 128 */     return sourceManager;
/*     */   }
/*     */ 
/*     */   public static TwainSource getSource() throws TwainIOException {
/* 132 */     return getSourceManager().getSource();
/*     */   }
/*     */ 
/*     */   private static void trigger(Object paramObject, int paramInt) {
/*     */     try {
/* 137 */       for (; (sourceManager == null) && (installed); Thread.sleep(100L)) Thread.currentThread();
/* 138 */       if (installed) ntrigger(paramObject, paramInt);  } catch (Exception localException) {
/*     */     }
/*     */   }
/*     */ 
/* 142 */   public static void callSetSource(Object paramObject) { trigger(paramObject, 0); }
/*     */ 
/*     */   public static void setScanner(TwainScanner paramTwainScanner) {
/* 145 */     scanner = new WeakReference(paramTwainScanner);
/*     */   }
/*     */ 
/*     */   private static TwainScanner getScanner() {
/* 149 */     return (TwainScanner)scanner.get();
/*     */   }
/*     */ 
/*     */   public static void select(TwainScanner paramTwainScanner) throws TwainIOException {
/* 153 */     setScanner(paramTwainScanner);
/* 154 */     TwainSourceManager localTwainSourceManager = getSourceManager();
/* 155 */     localTwainSourceManager.getSource().checkState(3);
/* 156 */     trigger(paramTwainScanner, 1);
/*     */   }
/*     */ 
/*     */   static void getIdentities(TwainScanner paramTwainScanner, Vector paramVector)
/*     */     throws TwainIOException
/*     */   {
/* 162 */     setScanner(paramTwainScanner);
/* 163 */     TwainSourceManager localTwainSourceManager = getSourceManager();
/* 164 */     localTwainSourceManager.getSource().checkState(3);
/*     */ 
/* 166 */     Semaphore localSemaphore = new Semaphore(0, true);
/* 167 */     Object[] arrayOfObject = { paramVector, localSemaphore };
/* 168 */     trigger(arrayOfObject, 2);
/*     */     try {
/* 170 */       localSemaphore.tryAcquire(3000L, TimeUnit.MILLISECONDS);
/* 171 */       if ((paramVector.isEmpty()) && (arrayOfObject[1] != null))
/* 172 */         throw new TwainIOException(jtwain.class.getName() + ".getIdentities\n\tCould not retrieve device names. Request timed out.");
/*     */     }
/*     */     catch (InterruptedException localInterruptedException) {
/* 175 */       throw new TwainIOException(jtwain.class.getName() + ".getIdentities\n\tCould not retrieve device names. Request was interrupted.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void select(TwainScanner paramTwainScanner, String paramString) throws TwainIOException {
/* 180 */     setScanner(paramTwainScanner);
/* 181 */     TwainSourceManager localTwainSourceManager = getSourceManager();
/* 182 */     localTwainSourceManager.getSource().checkState(3);
/* 183 */     trigger(paramString, 3);
/*     */   }
/*     */ 
/*     */   public static void acquire(TwainScanner paramTwainScanner) throws TwainIOException {
/* 187 */     setScanner(paramTwainScanner);
/* 188 */     TwainSourceManager localTwainSourceManager = getSourceManager();
/* 189 */     localTwainSourceManager.getSource().checkState(3);
/* 190 */     trigger(paramTwainScanner, 4);
/*     */   }
/*     */ 
/*     */   public static void setCancel(TwainScanner paramTwainScanner, boolean paramBoolean) throws TwainIOException {
/* 194 */     getSourceManager().getSource().setCancel(paramBoolean);
/*     */   }
/*     */ 
/*     */   private static Method getMethod(Class paramClass, String paramString, Class[] paramArrayOfClass) throws NoSuchMethodException {
/* 198 */     if (paramClass == null) throw new NoSuchMethodException(); try
/*     */     {
/* 200 */       return paramClass.getDeclaredMethod(paramString, paramArrayOfClass); } catch (NoSuchMethodException localNoSuchMethodException) {
/*     */     }
/* 202 */     return getMethod(paramClass.getSuperclass(), paramString, paramArrayOfClass);
/*     */   }
/*     */ 
/*     */   private static void cbexecute(Object paramObject, int paramInt)
/*     */   {
/*     */     try
/*     */     {
/*     */       TwainSource localTwainSource;
/* 209 */       switch (paramInt) {
/*     */       case 0:
/* 211 */         Class localClass = paramObject.getClass();
/*     */         try {
/* 213 */           localTwainSource = sourceManager.getSource();
/* 214 */           Method localMethod = getMethod(localClass, "setSource", new Class[] { TwainSource.class });
/* 215 */           localMethod.invoke(paramObject, new Object[] { localTwainSource }); } catch (Throwable localThrowable2) {
/* 216 */           localThrowable2.printStackTrace();
/*     */         }
/*     */       case 1:
/* 219 */         sourceManager.selectSource();
/* 220 */         break;
/*     */       case 2:
/* 222 */         Object[] arrayOfObject = (Object[])paramObject;
/* 223 */         Vector localVector = (Vector)arrayOfObject[0];
/* 224 */         Semaphore localSemaphore = (Semaphore)arrayOfObject[1];
/* 225 */         sourceManager.getIdentities(localVector);
/* 226 */         arrayOfObject[1] = null;
/* 227 */         localSemaphore.release();
/* 228 */         break;
/*     */       case 3:
/* 230 */         String str = (String)paramObject;
/* 231 */         sourceManager.selectSource(str);
/* 232 */         break;
/*     */       case 4:
/* 234 */         localTwainSource = sourceManager.openSource();
/*     */         try {
/* 236 */           localTwainSource.enable();
/*     */         } finally {
/* 238 */           localTwainSource.close();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1) {
/* 243 */       signalException(localThrowable1.getMessage());
/* 244 */       localThrowable1.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static int cbhandleGetMessage(long paramLong) {
/*     */     try {
/* 250 */       return sourceManager.getSource().handleGetMessage(paramLong);
/*     */     } catch (Throwable localThrowable) {
/* 252 */       signalException(localThrowable.getMessage());
/* 253 */       localThrowable.printStackTrace();
/* 254 */     }return 5;
/*     */   }
/*     */ 
/*     */   private static int cbmethod20(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3, long paramLong)
/*     */   {
/*     */     try {
/* 260 */       TwainIdentity localTwainIdentity = new TwainIdentity(sourceManager, paramArrayOfByte1);
/*     */ 
/* 265 */       TwainSource localTwainSource = sourceManager.getSource();
/* 266 */       if (localTwainIdentity.getId() != localTwainSource.getId()) {
/* 267 */         return 1;
/*     */       }
/* 269 */       return localTwainSource.callback(paramInt1, paramInt2, paramInt3, paramLong);
/*     */     } catch (Throwable localThrowable) {
/* 271 */       signalException(localThrowable.getMessage());
/* 272 */       localThrowable.printStackTrace();
/* 273 */     }return 1;
/*     */   }
/*     */ 
/*     */   static void signalStateChange(TwainSource paramTwainSource)
/*     */   {
/* 278 */     TwainScanner localTwainScanner = getScanner();
/* 279 */     if (localTwainScanner != null)
/* 280 */       localTwainScanner.setState(paramTwainSource);
/*     */   }
/*     */ 
/*     */   static void signalException(String paramString)
/*     */   {
/* 285 */     TwainScanner localTwainScanner = getScanner();
/* 286 */     if (localTwainScanner != null)
/* 287 */       localTwainScanner.signalException(paramString);
/*     */   }
/*     */ 
/*     */   static void negotiateCapabilities(TwainSource paramTwainSource)
/*     */   {
/* 292 */     TwainScanner localTwainScanner = getScanner();
/* 293 */     if (localTwainScanner != null)
/* 294 */       localTwainScanner.negotiateCapabilities(paramTwainSource);
/*     */   }
/*     */ 
/*     */   static void transferNativeImage(long paramLong)
/*     */   {
/* 299 */     BufferedImage localBufferedImage = (BufferedImage)ntransferImage(paramLong);
/* 300 */     if (localBufferedImage != null) {
/* 301 */       TwainScanner localTwainScanner = getScanner();
/* 302 */       if (localTwainScanner != null)
/* 303 */         localTwainScanner.setImage(localBufferedImage);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void transferFileImage(File paramFile)
/*     */   {
/* 309 */     if (paramFile != null) {
/* 310 */       TwainScanner localTwainScanner = getScanner();
/* 311 */       if (localTwainScanner != null)
/* 312 */         localTwainScanner.setImage(paramFile);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void transferMemoryBuffer(TwainTransfer.MemoryTransfer.Info paramInfo)
/*     */   {
/* 318 */     TwainScanner localTwainScanner = getScanner();
/* 319 */     if (localTwainScanner != null)
/* 320 */       localTwainScanner.setImageBuffer(paramInfo);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 327 */     installed = TwainNativeLoadStrategySingleton.getInstance().getNativeLoadStrategy().load(jtwain.class, "jtwain");
/* 328 */     if (installed) {
/* 329 */       ptrSize = ngetPtrSize();
/* 330 */       ptrCallback = ngetCallBackMethod();
/*     */ 
/* 332 */       nativeThread = new Thread() {
/*     */         public void run() {
/*     */           try {
/* 335 */             long l = jtwain.access$000();
/* 336 */             if (l != 0L) {
/* 337 */               jtwain.access$102(new TwainSourceManager(l));
/* 338 */               jtwain.access$200();
/*     */             }
/*     */           } catch (Throwable localThrowable) {
/* 341 */             System.err.println("uk.co.mmscomputing.device.twain.jtwain\t\n" + localThrowable.getMessage());
/* 342 */             System.err.println(localThrowable);
/* 343 */             localThrowable.printStackTrace();
/*     */           }
/* 345 */           jtwain.access$302(false);
/*     */         }
/*     */       };
/* 348 */       nativeThread.start();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.jtwain
 * JD-Core Version:    0.6.2
 */