/*     */ package uk.co.mmscomputing.device.twain;
/*     */ 
/*     */ import java.io.File;
/*     */ 
/*     */ public class TwainTransfer
/*     */   implements TwainConstants, TwainITransfer
/*     */ {
/*     */   TwainSource source;
/*     */   boolean isCancelled;
/*     */ 
/*     */   public TwainTransfer(TwainSource paramTwainSource)
/*     */   {
/*  11 */     this.source = paramTwainSource;
/*  12 */     this.isCancelled = false;
/*     */   }
/*     */ 
/*     */   public void initiate() throws TwainIOException {
/*  16 */     commitCancel();
/*     */   }
/*     */ 
/*     */   public void setCancel(boolean paramBoolean) {
/*  20 */     this.isCancelled = paramBoolean;
/*     */   }
/*     */   protected void commitCancel() throws TwainIOException {
/*  23 */     if ((this.isCancelled) && (this.source.getState() == 6))
/*     */     {
/*  25 */       throw new TwainUserCancelException();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void finish()
/*     */     throws TwainIOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void cancel()
/*     */     throws TwainIOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void cleanup()
/*     */     throws TwainIOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public static class MemoryTransfer extends TwainTransfer
/*     */   {
/* 109 */     private byte[] imx = new byte[jtwain.getPtrSize() == 4 ? 38 : 42];
/*     */     private Info info;
/* 112 */     protected int minBufSize = -1;
/* 113 */     protected int maxBufSize = -1;
/* 114 */     protected int preferredSize = -1;
/*     */ 
/*     */     public MemoryTransfer(TwainSource paramTwainSource) {
/* 117 */       super();
/*     */     }
/*     */ 
/*     */     protected void retrieveBufferSizes() throws TwainIOException {
/* 121 */       byte[] arrayOfByte = new byte[12];
/* 122 */       jtwain.setINT32(arrayOfByte, 0, this.minBufSize);
/* 123 */       jtwain.setINT32(arrayOfByte, 4, this.maxBufSize);
/* 124 */       jtwain.setINT32(arrayOfByte, 8, this.preferredSize);
/* 125 */       this.source.call(1, 6, 1, arrayOfByte);
/* 126 */       this.minBufSize = jtwain.getINT32(arrayOfByte, 0);
/* 127 */       this.maxBufSize = jtwain.getINT32(arrayOfByte, 4);
/* 128 */       this.preferredSize = jtwain.getINT32(arrayOfByte, 8);
/*     */     }
/*     */ 
/*     */     public void initiate() throws TwainIOException {
/* 132 */       super.initiate();
/* 133 */       retrieveBufferSizes();
/* 134 */       jtwain.nnew(this.imx, this.preferredSize);
/* 135 */       byte[] arrayOfByte = new byte[this.preferredSize];
/* 136 */       this.info = new Info(this.imx, arrayOfByte);
/*     */       while (true) {
/* 138 */         this.source.call(2, 259, 1, this.imx);
/* 139 */         int i = jtwain.getINT32(this.imx, 22);
/* 140 */         int j = jtwain.ncopy(arrayOfByte, this.imx, i);
/* 141 */         if (j == i)
/* 142 */           jtwain.transferMemoryBuffer(this.info);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void finish() throws TwainIOException
/*     */     {
/* 148 */       int i = jtwain.getINT32(this.imx, 22);
/* 149 */       int j = jtwain.ncopy(this.info.getBuffer(), this.imx, i);
/* 150 */       if (j == i)
/* 151 */         jtwain.transferMemoryBuffer(this.info);
/*     */     }
/*     */ 
/*     */     public void cleanup() throws TwainIOException
/*     */     {
/* 156 */       jtwain.ndelete(this.imx);
/*     */     }
/*     */     public static class Info {
/*     */       private byte[] imx;
/*     */       private byte[] buf;
/*     */       private int len;
/*     */ 
/* 165 */       Info(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) { this.imx = paramArrayOfByte1; this.buf = paramArrayOfByte2; } 
/*     */       public byte[] getBuffer() {
/* 167 */         return this.buf; } 
/* 168 */       public int getCompression() { return jtwain.getINT16(this.imx, 0); } 
/* 169 */       public int getBytesPerRow() { return jtwain.getINT32(this.imx, 2); } 
/* 170 */       public int getWidth() { return jtwain.getINT32(this.imx, 6); } 
/* 171 */       public int getHeight() { return jtwain.getINT32(this.imx, 10); } 
/* 172 */       public int getLeft() { return jtwain.getINT32(this.imx, 14); } 
/* 173 */       public int getTop() { return jtwain.getINT32(this.imx, 18); } 
/* 174 */       public int getLength() { return jtwain.getINT32(this.imx, 22); } 
/*     */       public int getMemFlags() {
/* 176 */         return jtwain.getINT32(this.imx, 26); } 
/* 177 */       public int getMemLength() { return jtwain.getINT32(this.imx, 30); } 
/* 178 */       public long getMemPtr() { return jtwain.getPtr(this.imx, 34); }
/*     */ 
/*     */       public String toString() {
/* 181 */         String str = getClass().getName() + "\n";
/* 182 */         str = str + "\tcompression = " + getCompression() + "\n";
/* 183 */         str = str + "\tbytes per row = " + getBytesPerRow() + "\n";
/* 184 */         str = str + "\ttop = " + getTop() + " left = " + getLeft() + " width = " + getWidth() + " height = " + getHeight() + "\n";
/* 185 */         str = str + "\tbytes = " + getLength() + "\n";
/*     */ 
/* 187 */         str = str + "\tmemory flags   = 0x" + Integer.toHexString(getMemFlags()) + "\n";
/* 188 */         str = str + "\tmemory length  = " + getMemLength() + "\n";
/* 189 */         str = str + "\tmemory pointer = 0x" + Long.toHexString(getMemPtr()) + "\n";
/*     */ 
/* 191 */         return str;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class FileTransfer extends TwainTransfer
/*     */   {
/*     */     protected File file;
/*     */ 
/*     */     public FileTransfer(TwainSource paramTwainSource)
/*     */     {
/*  57 */       super();
/*  58 */       this.file = null;
/*     */     }
/*     */     protected int getImageFileFormat() {
/*  61 */       return this.source.getImageFileFormat();
/*     */     }
/*  63 */     public void setFile(File paramFile) { this.file = paramFile; } 
/*     */     public File getFile() {
/*  65 */       if (this.file == null) {
/*  66 */         String str = TwainConstants.ImageFileFormatExts[getImageFileFormat()];
/*     */         try {
/*  68 */           File localFile = new File(System.getProperty("user.home"), "mmsc/tmp"); localFile.mkdirs();
/*  69 */           this.file = File.createTempFile("mmsctwain", str, localFile);
/*     */         } catch (Exception localException) {
/*  71 */           this.file = new File("c:\\mmsctwain." + str);
/*     */         }
/*     */       }
/*  74 */       return this.file;
/*     */     }
/*     */ 
/*     */     public void initiate() throws TwainIOException {
/*  78 */       super.initiate();
/*     */ 
/*  80 */       String str = getFile().getPath();
/*  81 */       int i = getImageFileFormat();
/*     */ 
/*  83 */       byte[] arrayOfByte = new byte[260];
/*  84 */       jtwain.setString(arrayOfByte, 0, str);
/*  85 */       jtwain.setINT16(arrayOfByte, 256, i);
/*  86 */       jtwain.setINT16(arrayOfByte, 258, 0);
/*     */ 
/*  88 */       this.source.call(1, 7, 6, arrayOfByte);
/*  89 */       this.source.call(2, 261, 1, null);
/*     */     }
/*     */ 
/*     */     public void finish() throws TwainIOException {
/*  93 */       jtwain.transferFileImage(this.file);
/*     */     }
/*     */ 
/*     */     public void cancel() throws TwainIOException {
/*  97 */       if ((this.file != null) && (this.file.exists()))
/*  98 */         this.file.delete();
/*     */     }
/*     */ 
/*     */     public void cleanup() throws TwainIOException
/*     */     {
/* 103 */       setFile(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class NativeTransfer extends TwainTransfer
/*     */   {
/*  35 */     private byte[] imageHandle = new byte[jtwain.getPtrSize()];
/*     */ 
/*     */     public NativeTransfer(TwainSource paramTwainSource) {
/*  38 */       super();
/*     */     }
/*     */ 
/*     */     public void initiate() throws TwainIOException {
/*  42 */       super.initiate();
/*  43 */       this.source.call(2, 260, 1, this.imageHandle);
/*     */     }
/*     */ 
/*     */     public void finish() throws TwainIOException {
/*  47 */       long l = jtwain.getPtr(this.imageHandle, 0);
/*  48 */       jtwain.transferNativeImage(l);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainTransfer
 * JD-Core Version:    0.6.2
 */