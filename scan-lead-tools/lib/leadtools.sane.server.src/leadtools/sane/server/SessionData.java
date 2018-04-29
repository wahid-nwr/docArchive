/*     */ package leadtools.sane.server;
/*     */ 
/*     */ import leadtools.RasterExceptionCode;
/*     */ import leadtools.sane.SaneDevice;
/*     */ import leadtools.sane.SaneSession;
/*     */ 
/*     */ class SessionData
/*     */ {
/*     */   private String _id;
/*     */   private SaveData _saveData;
/*     */   public boolean _isScanning;
/*     */   public boolean _closed;
/*     */   public int _lastErrorCode;
/*     */   public String _lastErrorMessage;
/*     */   public String _sourceName;
/*     */   private SaneSession _session;
/*     */   private SaneDevice _activeDevice;
/*     */ 
/*     */   public String getId()
/*     */   {
/*  47 */     return this._id;
/*     */   }
/*     */ 
/*     */   public SessionData(String id) {
/*  51 */     this._id = id;
/*     */ 
/*  53 */     setSaveData(new SaveData());
/*  54 */     setScanning(false);
/*  55 */     setClosed(true);
/*  56 */     setLastErrorCode(RasterExceptionCode.SUCCESS.getValue());
/*  57 */     setLastErrorMessage("");
/*  58 */     setSourceName("");
/*  59 */     setSession(null);
/*  60 */     setActiveDevice(null);
/*     */   }
/*     */ 
/*     */   public SaveData getSaveData()
/*     */   {
/*  66 */     return this._saveData;
/*     */   }
/*     */   public void setSaveData(SaveData saveData) {
/*  69 */     this._saveData = saveData;
/*     */   }
/*     */ 
/*     */   public boolean isScanning()
/*     */   {
/*  75 */     return this._isScanning;
/*     */   }
/*     */   public void setScanning(boolean isScanning) {
/*  78 */     this._isScanning = isScanning;
/*     */   }
/*     */ 
/*     */   public boolean getClosed()
/*     */   {
/*  83 */     return this._closed;
/*     */   }
/*     */   public void setClosed(boolean closed) {
/*  86 */     this._closed = closed;
/*     */   }
/*     */ 
/*     */   public int getLastErrorCode()
/*     */   {
/*  92 */     return this._lastErrorCode;
/*     */   }
/*     */   public void setLastErrorCode(int lastErrorCode) {
/*  95 */     this._lastErrorCode = lastErrorCode;
/*     */   }
/*     */ 
/*     */   public String getLastErrorMessage()
/*     */   {
/* 100 */     return this._lastErrorMessage;
/*     */   }
/*     */   public void setLastErrorMessage(String lastErrorMessage) {
/* 103 */     this._lastErrorMessage = lastErrorMessage;
/*     */   }
/*     */ 
/*     */   public String getSourceName()
/*     */   {
/* 108 */     return this._sourceName;
/*     */   }
/*     */   public void setSourceName(String sourceName) {
/* 111 */     this._sourceName = sourceName;
/*     */   }
/*     */ 
/*     */   public SaneSession getSession()
/*     */   {
/* 116 */     return this._session;
/*     */   }
/*     */   public void setSession(SaneSession session) {
/* 119 */     this._session = session;
/*     */   }
/*     */ 
/*     */   public SaneDevice getActiveDevice()
/*     */   {
/* 124 */     return this._activeDevice;
/*     */   }
/*     */   protected void setActiveDevice(SaneDevice activeDevice) {
/* 127 */     this._activeDevice = activeDevice;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.SessionData
 * JD-Core Version:    0.6.2
 */