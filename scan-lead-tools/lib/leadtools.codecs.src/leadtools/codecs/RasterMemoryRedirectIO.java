/*     */ package leadtools.codecs;
/*     */ 
/*     */ import leadtools.ILeadStream;
/*     */ import leadtools.LeadSeekOrigin;
/*     */ import leadtools.LeadStreamAccess;
/*     */ import leadtools.LeadStreamMode;
/*     */ import leadtools.LeadStreamShare;
/*     */ 
/*     */ class RasterMemoryRedirectIO
/*     */   implements ILeadStream
/*     */ {
/*     */   byte[] _buffer;
/*     */   int _position;
/*     */   int _maxSize;
/*     */   private boolean _isStarted;
/*     */ 
/*     */   public RasterMemoryRedirectIO()
/*     */   {
/*  17 */     this._buffer = null;
/*  18 */     this._position = 0;
/*  19 */     this._maxSize = 0;
/*     */   }
/*     */ 
/*     */   public String getFileName() {
/*  23 */     return "Stream";
/*     */   }
/*     */ 
/*     */   public boolean canSeek() {
/*  27 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean canRead() {
/*  31 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean canWrite() {
/*  35 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isStarted()
/*     */   {
/*  40 */     return this._isStarted;
/*     */   }
/*     */ 
/*     */   public boolean start() {
/*  44 */     this._isStarted = true;
/*  45 */     return true;
/*     */   }
/*     */ 
/*     */   public void stop(boolean resetPosition) {
/*  49 */     this._isStarted = false;
/*     */   }
/*     */ 
/*     */   public boolean openFile(String fileName, LeadStreamMode mode, LeadStreamAccess access, LeadStreamShare share) {
/*  53 */     return false;
/*     */   }
/*     */ 
/*     */   public int read(byte[] pBuf, int uCount)
/*     */   {
/*  59 */     if ((this._buffer == null) || (this._position > this._maxSize))
/*  60 */       return 0;
/*  61 */     int nRead = this._maxSize - this._position;
/*  62 */     if (nRead > uCount)
/*  63 */       nRead = uCount;
/*  64 */     System.arraycopy(this._buffer, this._position, pBuf, 0, nRead);
/*  65 */     return nRead;
/*     */   }
/*     */ 
/*     */   public int write(byte[] pBuf, int uCount)
/*     */   {
/*  71 */     if ((this._buffer == null) || (this._position + uCount < this._buffer.length))
/*     */     {
/*  73 */       int newArraySize = this._position + (uCount < 1024 ? 1024 : uCount);
/*  74 */       byte[] newBuffer = new byte[newArraySize];
/*  75 */       if (this._buffer != null)
/*  76 */         System.arraycopy(this._buffer, 0, newBuffer, 0, this._maxSize);
/*  77 */       this._buffer = newBuffer;
/*     */     }
/*  79 */     System.arraycopy(pBuf, 0, this._buffer, this._position, uCount);
/*  80 */     this._position += uCount;
/*  81 */     if (this._position > this._maxSize)
/*  82 */       this._maxSize = this._position;
/*  83 */     return uCount;
/*     */   }
/*     */ 
/*     */   public long seek(LeadSeekOrigin origin, long offset) {
/*  87 */     if (origin == LeadSeekOrigin.BEGIN)
/*  88 */       this._position = ((int)offset);
/*  89 */     else if (origin == LeadSeekOrigin.CURRENT)
/*  90 */       this._position = ((int)(this._position + offset));
/*  91 */     else if (origin == LeadSeekOrigin.END)
/*  92 */       this._position += this._maxSize;
/*     */     else {
/*  94 */       return -1L;
/*     */     }
/*  96 */     return this._position;
/*     */   }
/*     */ 
/*     */   public byte[] getBuffer(boolean bCopy)
/*     */   {
/* 101 */     if (bCopy)
/*     */     {
/* 103 */       byte[] ret = new byte[this._maxSize];
/* 104 */       System.arraycopy(this._buffer, 0, ret, 0, this._maxSize);
/* 105 */       return ret;
/*     */     }
/* 107 */     return this._buffer;
/*     */   }
/*     */ 
/*     */   public int getUsefulSize()
/*     */   {
/* 112 */     return this._maxSize;
/*     */   }
/*     */ 
/*     */   public void closeFile()
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.RasterMemoryRedirectIO
 * JD-Core Version:    0.6.2
 */