/*     */ package leadtools;
/*     */ 
/*     */ public final class LeadBufferStream
/*     */   implements ILeadStream
/*     */ {
/*     */   private int _position;
/*     */   private int _length;
/*     */   private byte[] _buffer;
/*     */   private boolean _isStarted;
/*     */ 
/*     */   public LeadBufferStream(byte[] buffer)
/*     */   {
/*   5 */     if (buffer == null) {
/*   6 */       throw new ArgumentNullException("buffer");
/*     */     }
/*   8 */     this._buffer = buffer;
/*   9 */     this._length = this._buffer.length;
/*  10 */     this._position = 0;
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/*  18 */     return "Stream";
/*     */   }
/*     */ 
/*     */   public boolean canSeek() {
/*  22 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean canRead() {
/*  26 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean canWrite() {
/*  30 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isStarted()
/*     */   {
/*  35 */     return this._isStarted;
/*     */   }
/*     */ 
/*     */   public boolean start() {
/*  39 */     this._isStarted = true;
/*  40 */     this._position = 0;
/*  41 */     return true;
/*     */   }
/*     */ 
/*     */   public void stop(boolean resetPosition) {
/*  45 */     this._isStarted = false;
/*     */   }
/*     */ 
/*     */   public boolean openFile(String fileName, LeadStreamMode mode, LeadStreamAccess access, LeadStreamShare share) {
/*  49 */     return true;
/*     */   }
/*     */ 
/*     */   public int read(byte[] buffer, int count)
/*     */   {
/*  64 */     if (count == 0) {
/*  65 */       return 0;
/*     */     }
/*     */ 
/*  68 */     int read = Math.min(count, Math.abs(this._length - this._position));
/*  69 */     if (read > 0) {
/*  70 */       System.arraycopy(this._buffer, this._position, buffer, 0, read);
/*  71 */       this._position += read;
/*     */     }
/*     */ 
/*  74 */     return read;
/*     */   }
/*     */ 
/*     */   public int write(byte[] buffer, int count) {
/*  78 */     int available = this._length - this._position;
/*  79 */     if (available > count)
/*  80 */       available = count;
/*  81 */     if (available > buffer.length)
/*  82 */       available = buffer.length;
/*  83 */     if (available <= 0)
/*     */     {
/*  85 */       return 0;
/*     */     }
/*  87 */     System.arraycopy(buffer, 0, this._buffer, this._position, available);
/*  88 */     this._position += available;
/*  89 */     return available;
/*     */   }
/*     */ 
/*     */   public long seek(LeadSeekOrigin origin, long offset) {
/*  93 */     if (origin == LeadSeekOrigin.CURRENT)
/*  94 */       this._position = ((int)(this._position + offset));
/*  95 */     else if (origin == LeadSeekOrigin.END)
/*  96 */       this._position = ((int)(this._length - offset));
/*     */     else {
/*  98 */       this._position = ((int)offset);
/*     */     }
/*     */ 
/* 101 */     this._position = Math.max(0, Math.min(this._length, this._position));
/* 102 */     return this._position;
/*     */   }
/*     */ 
/*     */   public void closeFile() {
/* 106 */     this._position = 0;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadBufferStream
 * JD-Core Version:    0.6.2
 */