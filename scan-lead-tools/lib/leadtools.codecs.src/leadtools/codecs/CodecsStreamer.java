/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.ILeadStream;
/*    */ import leadtools.L_ERROR;
/*    */ import leadtools.LeadBufferStream;
/*    */ import leadtools.LeadSeekOrigin;
/*    */ import leadtools.LeadStream;
/*    */ 
/*    */ class CodecsStreamer
/*    */ {
/*    */   private ILeadStream _stream;
/*    */   private boolean _resetPosition;
/*    */   private long _redirectHandle;
/*    */   private int _position;
/*    */ 
/*    */   public CodecsStreamer(ILeadStream stream, boolean resetPosition)
/*    */   {
/* 18 */     this._stream = stream;
/* 19 */     this._resetPosition = resetPosition;
/* 20 */     this._position = 0;
/*    */   }
/*    */ 
/*    */   public final int start()
/*    */   {
/* 25 */     if (!this._stream.start()) {
/* 26 */       return L_ERROR.ERROR_NO_MEMORY.getValue();
/*    */     }
/* 28 */     if (((this._stream instanceof LeadStream)) || ((this._stream instanceof LeadBufferStream))) {
/* 29 */       this._redirectHandle = ltfil.StartRedirectIO(this);
/*    */     }
/* 31 */     return L_ERROR.SUCCESS.getValue();
/*    */   }
/*    */ 
/*    */   public void dispose()
/*    */   {
/* 36 */     if (((this._stream instanceof LeadStream)) || ((this._stream instanceof LeadBufferStream))) {
/* 37 */       ltfil.StopRedirectIO(this._redirectHandle);
/*    */     }
/* 39 */     if (this._stream != null)
/*    */     {
/* 41 */       if (this._stream.isStarted()) {
/* 42 */         this._stream.stop(this._resetPosition);
/*    */       }
/* 44 */       this._stream = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   public final ILeadStream getStream()
/*    */   {
/* 50 */     return this._stream;
/*    */   }
/*    */ 
/*    */   public Object open(String UnnamedParameter1, int UnnamedParameter2, int UnnamedParameter3)
/*    */   {
/* 55 */     this._stream.seek(LeadSeekOrigin.BEGIN, this._position);
/* 56 */     return new Integer(1);
/*    */   }
/*    */ 
/*    */   public int read(Object hFile, byte[] pBuf, int uCount) {
/* 60 */     return this._stream.read(pBuf, uCount);
/*    */   }
/*    */ 
/*    */   public int write(Object hFile, byte[] pBuf, int uCount) {
/* 64 */     return this._stream.write(pBuf, uCount);
/*    */   }
/*    */ 
/*    */   public long seek(Object hFile, long nPos, int nOrigin) {
/* 68 */     if (nOrigin == LeadSeekOrigin.BEGIN.getValue())
/* 69 */       nPos += this._position;
/* 70 */     long ret = this._stream.seek(LeadSeekOrigin.forValue(nOrigin), nPos);
/* 71 */     if (ret != -1L)
/* 72 */       ret -= this._position;
/* 73 */     return ret;
/*    */   }
/*    */ 
/*    */   public int close(Object UnnamedParameter1) {
/* 77 */     return L_ERROR.SUCCESS.getValue();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsStreamer
 * JD-Core Version:    0.6.2
 */