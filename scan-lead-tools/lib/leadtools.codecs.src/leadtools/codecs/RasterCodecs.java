/*      */ package leadtools.codecs;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.util.Vector;
/*      */ import leadtools.ArgumentNullException;
/*      */ import leadtools.ArgumentOutOfRangeException;
/*      */ import leadtools.ILeadStream;
/*      */ import leadtools.ISvgDocument;
/*      */ import leadtools.InvalidOperationException;
/*      */ import leadtools.L_ERROR;
/*      */ import leadtools.LeadFileStream;
/*      */ import leadtools.LeadPoint;
/*      */ import leadtools.LeadRect;
/*      */ import leadtools.LeadSeekOrigin;
/*      */ import leadtools.LeadSize;
/*      */ import leadtools.LeadStream;
/*      */ import leadtools.LeadStreamFactory;
/*      */ import leadtools.LeadURIStream;
/*      */ import leadtools.RasterByteOrder;
/*      */ import leadtools.RasterCollection;
/*      */ import leadtools.RasterColor;
/*      */ import leadtools.RasterCommentMetadata;
/*      */ import leadtools.RasterCommentMetadataDataType;
/*      */ import leadtools.RasterCommentMetadataType;
/*      */ import leadtools.RasterException;
/*      */ import leadtools.RasterExceptionCode;
/*      */ import leadtools.RasterImage;
/*      */ import leadtools.RasterImageFormat;
/*      */ import leadtools.RasterMarkerMetadata;
/*      */ import leadtools.RasterNativeBuffer;
/*      */ import leadtools.RasterSizeFlags;
/*      */ import leadtools.RasterTagMetadata;
/*      */ import leadtools.RasterTagMetadataDataType;
/*      */ import leadtools.RasterViewPerspective;
/*      */ import leadtools.internal.ILeadTaskWorker;
/*      */ import leadtools.internal.ILeadTaskWorker.Worker;
/*      */ import leadtools.internal.LeadPlatform;
/*      */ import leadtools.ltkrn;
/*      */ 
/*      */ public class RasterCodecs
/*      */ {
/*      */   private FILEINFO _tempFileInfo;
/*      */   private static final int HTTP_STATUS_SC_CREATED = 201;
/*      */   private CodecsOptions _options;
/*      */   private boolean _throwExceptionsOnInvalidImages;
/*      */   private long _uriOperationBufferSize;
/*      */   private int _redirectOpenSubclassed;
/*      */   private CodecsRedirectOpenEvent _redirectOpenEventArgs;
/*      */   private int _redirectReadSubclassed;
/*      */   private CodecsRedirectReadEvent _redirectReadEventArgs;
/*      */   private int _redirectWriteSubclassed;
/*      */   private CodecsRedirectWriteEvent _redirectWriteEventArgs;
/*      */   private int _redirectSeekSubclassed;
/*      */   private CodecsRedirectSeekEvent _redirectSeekEventArgs;
/*      */   private int _redirectCloseSubclassed;
/*      */   private CodecsRedirectCloseEvent _redirectCloseEventArgs;
/*      */   private long _redirectThunk;
/*      */   private CodecsLoadImageEvent _loadImageEventArgs;
/*      */   private CodecsSaveImageEvent _saveImageEventArgs;
/*      */   private RasterCollection<RasterMarkerMetadata> _enumMarkersCollection;
/*      */   private long _hmarkers;
/*      */   private Object _oldOverlayCallback;
/*      */   private int _oldOverlayFlags;
/*      */   private CodecsOverlayListener _overlayCallback;
/*      */   private boolean _overlaying;
/*      */   private CodecsEnumTagsEvent _enumTagsEventArgs;
/*      */   private CodecsEnumGeoKeysEvent _enumGeoKeysEventArgs;
/*      */   private CodecsTransformMarkerListener _transformMarkerCallback;
/*      */   private int _leadWriteMarkerCallback;
/*      */   private int _leadWriteMarkerCallbackData;
/*      */   private Object _compressDecompressThunk;
/*      */   private long _compressDataHandle;
/*      */   private long _compressDataBitmapHandle;
/*      */   private CodecsCompressDataListener _compressDataCallback;
/*      */   private RasterImage _fileReadCallbackImage;
/*      */   private int _fileReadCallbackFirstPage;
/*      */   private boolean _useFileReadCallbackImage;
/*      */   private boolean _loadThenResize;
/*      */   private RasterImage _userImage;
/*      */   private CodecsImageInfo _userImageInfo;
/*      */   private ILeadStream _currentStream;
/*  124 */   private transient boolean _isDisposed = false;
/*      */   private long _currentSavedPageBitmapHandle;
/*      */   private byte[] _currentSavedPageBitmapHandleBuffer;
/*  431 */   private Vector<CodecsRedirectOpenListener> _RedirectOpen = new Vector();
/*      */ 
/*  458 */   private Vector<CodecsRedirectReadListener> _redirectRead = new Vector();
/*      */ 
/*  485 */   private Vector<CodecsRedirectWriteListener> _redirectWrite = new Vector();
/*      */ 
/*  512 */   private Vector<CodecsRedirectSeekListener> _redirectSeek = new Vector();
/*      */ 
/*  539 */   private Vector<CodecsRedirectCloseListener> _redirectClose = new Vector();
/*      */ 
/*  693 */   private Vector<CodecsLoadInformationListener> _loadInformation = new Vector();
/*      */ 
/*  789 */   private boolean _allowUserInteraction = false;
/*      */ 
/* 1010 */   private Vector<CodecsLoadPageListener> _loadPage = new Vector();
/*      */ 
/* 1036 */   private Vector<CodecsLoadImageListener> _loadImage = new Vector();
/*      */ 
/* 1879 */   private Vector<CodecsSavePageListener> _savePage = new Vector();
/*      */ 
/* 1905 */   private Vector<CodecsSaveImageListener> _saveImage = new Vector();
/*      */ 
/* 3217 */   private Vector<CodecsTagFoundListener> _TagFound = new Vector();
/*      */ 
/* 3297 */   private Vector<CodecsGeoKeyFoundListener> _GeoKeyFound = new Vector();
/*      */ 
/* 4765 */   private Vector<CodecsGetInformationAsyncCompletedListener> _GetInformationAsyncCompleted = new Vector();
/*      */ 
/* 4842 */   private Vector<CodecsLoadAsyncCompletedListener> _LoadAsyncCompleted = new Vector();
/*      */ 
/* 4865 */   private Vector<CodecsLoadSvgAsyncCompletedListener> _LoadSvgAsyncCompleted = new Vector();
/*      */ 
/*      */   public RasterCodecs()
/*      */   {
/*  128 */     Init();
/*      */   }
/*      */ 
/*      */   private void Init()
/*      */   {
/*  139 */     this._options = new CodecsOptions();
/*  140 */     this._options.getLoad().setCompressed(true);
/*  141 */     this._throwExceptionsOnInvalidImages = true;
/*  142 */     this._uriOperationBufferSize = 4096L;
/*      */ 
/*  146 */     this._redirectThunk = 0L;
/*      */ 
/*  148 */     this._redirectOpenSubclassed = 0;
/*  149 */     this._redirectOpenEventArgs = new CodecsRedirectOpenEvent(this);
/*  150 */     this._redirectReadSubclassed = 0;
/*  151 */     this._redirectReadEventArgs = new CodecsRedirectReadEvent(this);
/*  152 */     this._redirectWriteSubclassed = 0;
/*  153 */     this._redirectWriteEventArgs = new CodecsRedirectWriteEvent(this);
/*  154 */     this._redirectSeekSubclassed = 0;
/*  155 */     this._redirectSeekEventArgs = new CodecsRedirectSeekEvent(this);
/*  156 */     this._redirectCloseSubclassed = 0;
/*  157 */     this._redirectCloseEventArgs = new CodecsRedirectCloseEvent(this);
/*      */ 
/*  167 */     this._loadImageEventArgs = new CodecsLoadImageEvent(this);
/*  168 */     this._saveImageEventArgs = new CodecsSaveImageEvent(this);
/*      */ 
/*  170 */     this._enumMarkersCollection = null;
/*      */ 
/*  172 */     this._hmarkers = 0L;
/*      */ 
/*  174 */     this._oldOverlayCallback = Integer.valueOf(0);
/*  175 */     this._oldOverlayFlags = 0;
/*  176 */     this._overlayCallback = null;
/*  177 */     this._overlaying = false;
/*      */ 
/*  179 */     this._enumTagsEventArgs = new CodecsEnumTagsEvent(this);
/*  180 */     this._enumGeoKeysEventArgs = new CodecsEnumGeoKeysEvent(this);
/*      */ 
/*  182 */     this._transformMarkerCallback = null;
/*  183 */     this._leadWriteMarkerCallback = 0;
/*  184 */     this._leadWriteMarkerCallbackData = 0;
/*      */ 
/*  186 */     this._compressDecompressThunk = Integer.valueOf(0);
/*  187 */     this._compressDataHandle = 0L;
/*  188 */     this._compressDataBitmapHandle = 0L;
/*  189 */     this._compressDataCallback = null;
/*      */ 
/*  191 */     this._loadThenResize = false;
/*      */ 
/*  194 */     this._tempFileInfo = new FILEINFO();
/*      */ 
/*  196 */     this._currentSavedPageBitmapHandle = 0L;
/*  197 */     this._currentSavedPageBitmapHandleBuffer = null;
/*      */ 
/*  199 */     this._currentStream = null;
/*      */   }
/*      */ 
/*      */   protected void finalize()
/*      */   {
/*      */     try {
/*  205 */       dispose();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void dispose() {
/*  213 */     if (this._isDisposed)
/*  214 */       return;
/*  215 */     this._isDisposed = true;
/*      */ 
/*  217 */     if (this._redirectThunk != 0L)
/*      */     {
/*  219 */       stopRedirecting();
/*      */     }
/*      */ 
/*  222 */     this._options = null;
/*      */ 
/*  224 */     if (this._tempFileInfo != null)
/*      */     {
/*  226 */       this._tempFileInfo = null;
/*      */     }
/*      */ 
/*  229 */     this._overlayCallback = null;
/*      */ 
/*  231 */     this._transformMarkerCallback = null;
/*      */ 
/*  233 */     if (this._compressDecompressThunk != null)
/*      */     {
/*  235 */       this._compressDecompressThunk = null;
/*      */     }
/*      */ 
/*  238 */     if (this._compressDataHandle != 0L)
/*      */     {
/*  240 */       stopCompress();
/*  241 */       this._compressDataHandle = 0L;
/*      */     }
/*  243 */     this._compressDataBitmapHandle = 0L;
/*  244 */     this._compressDataCallback = null;
/*      */ 
/*  246 */     this._fileReadCallbackImage = null;
/*  247 */     this._fileReadCallbackFirstPage = -1;
/*  248 */     this._useFileReadCallbackImage = false;
/*      */   }
/*      */ 
/*      */   public boolean getFastFileInfo()
/*      */   {
/*  256 */     boolean bOldVal = ltfil.EnableFastFileInfo(false);
/*  257 */     ltfil.EnableFastFileInfo(bOldVal);
/*  258 */     return bOldVal;
/*      */   }
/*      */ 
/*      */   public void setFastFileInfo(boolean value) {
/*  262 */     ltfil.EnableFastFileInfo(value);
/*      */   }
/*      */ 
/*      */   public void preloadCodecs(int fixedCodecs, int cachedCodecs, String codecs)
/*      */   {
/*  267 */     this._options.use();
/*      */ 
/*  269 */     int ret = ltfil.PreLoadFilters(fixedCodecs, cachedCodecs, codecs);
/*  270 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   public void ignoreCodecs(String codecs) {
/*  274 */     if (codecs == null) {
/*  275 */       throw new ArgumentNullException("codecs");
/*      */     }
/*  277 */     this._options.use();
/*      */ 
/*  279 */     int ret = ltfil.IgnoreFilters(codecs);
/*  280 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   public String getPreloadCodecsList() {
/*  284 */     this._options.use();
/*      */ 
/*  286 */     String[] filters = { null };
/*  287 */     int[] params = new int[2];
/*  288 */     ltfil.GetPreLoadFilters(filters, params);
/*  289 */     return filters[0];
/*      */   }
/*      */ 
/*      */   public int getFixedPreloadCodecs() {
/*  293 */     this._options.use();
/*      */ 
/*  295 */     String[] filters = { null };
/*  296 */     int[] params = new int[2];
/*  297 */     ltfil.GetPreLoadFilters(filters, params);
/*  298 */     return params[0];
/*      */   }
/*      */ 
/*      */   public int getCachedPreloadCodecs() {
/*  302 */     this._options.use();
/*      */ 
/*  304 */     String[] filters = { null };
/*  305 */     int[] params = new int[2];
/*  306 */     ltfil.GetPreLoadFilters(filters, params);
/*  307 */     return params[1];
/*      */   }
/*      */ 
/*      */   public String getIgnoreCodecsList() {
/*  311 */     this._options.use();
/*      */ 
/*  313 */     String[] filters = { null };
/*  314 */     ltfil.GetIgnoreFilters(filters);
/*  315 */     return filters[0];
/*      */   }
/*      */ 
/*      */   public CodecsCodecInformation getCodecInformation(String codecName)
/*      */   {
/*  320 */     if (codecName == null) {
/*  321 */       throw new ArgumentNullException("codecName");
/*      */     }
/*  323 */     this._options.use();
/*      */ 
/*  325 */     CodecsCodecInformation fi = new CodecsCodecInformation();
/*  326 */     int ret = ltfil.GetFilterInfo(codecName, fi);
/*      */ 
/*  329 */     RasterException.checkErrorCode(ret);
/*  330 */     return fi;
/*      */   }
/*      */ 
/*      */   public void setCodecInformation(CodecsCodecInformation codecInfo) {
/*  334 */     this._options.use();
/*      */ 
/*  337 */     CodecsCodecInformation[] arr = { codecInfo };
/*  338 */     int ret = ltfil.SetFilterInfo(arr, 1, 0);
/*  339 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   public CodecsCodecInformation[] getCodecsInformation()
/*      */   {
/*  344 */     this._options.use();
/*      */ 
/*  346 */     int[] count = new int[1];
/*  347 */     CodecsCodecInformation[][] fi = new CodecsCodecInformation[1][];
/*  348 */     int ret = ltfil.GetFilterListInfo(fi, count);
/*  349 */     RasterException.checkErrorCode(ret);
/*      */ 
/*  351 */     return fi[0];
/*      */   }
/*      */ 
/*      */   public void setCodecsInformation(CodecsCodecInformation[] information)
/*      */   {
/*  356 */     this._options.use();
/*      */ 
/*  358 */     if (information == null) {
/*  359 */       throw new ArgumentNullException("information");
/*      */     }
/*  361 */     if (information.length < 1) {
/*  362 */       return;
/*      */     }
/*  364 */     int ret = ltfil.SetFilterInfo(information, information.length, 0);
/*  365 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   public CodecsOptions getOptions()
/*      */   {
/*  370 */     return this._options;
/*      */   }
/*      */ 
/*      */   public void setOptions(CodecsOptions value) {
/*  374 */     this._options = value;
/*      */   }
/*      */ 
/*      */   public boolean getThrowExceptionsOnInvalidImages()
/*      */   {
/*  379 */     return this._throwExceptionsOnInvalidImages;
/*      */   }
/*      */ 
/*      */   public void setThrowExceptionsOnInvalidImages(boolean value) {
/*  383 */     this._throwExceptionsOnInvalidImages = value;
/*      */   }
/*      */ 
/*      */   public int getUriOperationBufferSize()
/*      */   {
/*  388 */     return (int)this._uriOperationBufferSize;
/*      */   }
/*      */ 
/*      */   public void setUriOperationBufferSize(int value)
/*      */   {
/*  393 */     if (value < 1) {
/*  394 */       throw new ArgumentOutOfRangeException("UriOperationBufferSize", value, "Cannot be less than 1");
/*      */     }
/*  396 */     this._uriOperationBufferSize = value;
/*      */   }
/*      */ 
/*      */   private String getFileUri(URI uri)
/*      */   {
/*  401 */     if (!uri.isAbsolute()) {
/*  402 */       return uri.toString();
/*      */     }
/*      */ 
/*  405 */     if (uri.getScheme().equalsIgnoreCase("file")) {
/*  406 */       return uri.getPath();
/*      */     }
/*  408 */     return null;
/*      */   }
/*      */ 
/*      */   private boolean checkExceptions(int code)
/*      */   {
/*  414 */     if (code < 0)
/*      */     {
/*  416 */       if (getThrowExceptionsOnInvalidImages())
/*  417 */         RasterException.checkErrorCode(code);
/*      */       else {
/*  419 */         return false;
/*      */       }
/*      */     }
/*  422 */     return true;
/*      */   }
/*      */ 
/*      */   public synchronized void addRedirectOpenListener(CodecsRedirectOpenListener listener)
/*      */   {
/*  434 */     if (this._RedirectOpen.contains(listener)) {
/*  435 */       return;
/*      */     }
/*  437 */     this._RedirectOpen.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeRedirectOpenListener(CodecsRedirectOpenListener listener) {
/*  441 */     if (!this._RedirectOpen.contains(listener)) {
/*  442 */       return;
/*      */     }
/*  444 */     this._RedirectOpen.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireRedirectOpen(CodecsRedirectOpenEvent event) {
/*  448 */     for (CodecsRedirectOpenListener listener : this._RedirectOpen)
/*  449 */       listener.onRedirectOpen(event);
/*      */   }
/*      */ 
/*      */   public synchronized void addRedirectReadListener(CodecsRedirectReadListener listener)
/*      */   {
/*  461 */     if (this._redirectRead.contains(listener)) {
/*  462 */       return;
/*      */     }
/*  464 */     this._redirectRead.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeRedirectReadListener(CodecsRedirectReadListener listener) {
/*  468 */     if (!this._redirectRead.contains(listener)) {
/*  469 */       return;
/*      */     }
/*  471 */     this._redirectRead.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireRedirectRead(CodecsRedirectReadEvent event) {
/*  475 */     for (CodecsRedirectReadListener listener : this._redirectRead)
/*  476 */       listener.onRedirectRead(event);
/*      */   }
/*      */ 
/*      */   public synchronized void addRedirectWriteListener(CodecsRedirectWriteListener listener)
/*      */   {
/*  488 */     if (this._redirectWrite.contains(listener)) {
/*  489 */       return;
/*      */     }
/*  491 */     this._redirectWrite.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeRedirectWriteListener(CodecsRedirectWriteListener listener) {
/*  495 */     if (!this._redirectWrite.contains(listener)) {
/*  496 */       return;
/*      */     }
/*  498 */     this._redirectWrite.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireRedirectWrite(CodecsRedirectWriteEvent event) {
/*  502 */     for (CodecsRedirectWriteListener listener : this._redirectWrite)
/*  503 */       listener.onRedirectWrite(event);
/*      */   }
/*      */ 
/*      */   public synchronized void addRedirectSeekListener(CodecsRedirectSeekListener listener)
/*      */   {
/*  515 */     if (this._redirectSeek.contains(listener)) {
/*  516 */       return;
/*      */     }
/*  518 */     this._redirectSeek.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeRedirectSeekListener(CodecsRedirectSeekListener listener) {
/*  522 */     if (!this._redirectSeek.contains(listener)) {
/*  523 */       return;
/*      */     }
/*  525 */     this._redirectSeek.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireRedirectSeek(CodecsRedirectSeekEvent event) {
/*  529 */     for (CodecsRedirectSeekListener listener : this._redirectSeek)
/*  530 */       listener.onRedirectSeek(event);
/*      */   }
/*      */ 
/*      */   public synchronized void addRedirectCloseListener(CodecsRedirectCloseListener listener)
/*      */   {
/*  542 */     if (this._redirectClose.contains(listener)) {
/*  543 */       return;
/*      */     }
/*  545 */     this._redirectClose.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeRedirectCloseListener(CodecsRedirectCloseListener listener) {
/*  549 */     if (!this._redirectClose.contains(listener)) {
/*  550 */       return;
/*      */     }
/*  552 */     this._redirectClose.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireRedirectClose(CodecsRedirectCloseEvent event) {
/*  556 */     for (CodecsRedirectCloseListener listener : this._redirectClose)
/*  557 */       listener.onRedirectClose(event);
/*      */   }
/*      */ 
/*      */   public void startRedirecting()
/*      */   {
/*  563 */     if (this._redirectThunk != 0L) {
/*  564 */       throw new InvalidOperationException("Redirecting operation already started");
/*      */     }
/*  566 */     this._redirectThunk = ltfil.StartRedirecting(this);
/*  567 */     if (this._redirectThunk == 0L)
/*  568 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */   }
/*      */ 
/*      */   public void stopRedirecting() {
/*  572 */     if (this._redirectThunk != 0L)
/*      */     {
/*  574 */       ltfil.StopRedirecting(this._redirectThunk);
/*  575 */       this._redirectThunk = 0L;
/*      */     }
/*      */   }
/*      */ 
/*      */   final Object CallRedirectOpen(String pFile, int nMode, int nShare, Object pUserData)
/*      */   {
/*  581 */     if (this._redirectOpenSubclassed > 0)
/*      */     {
/*      */       String mode;
/*      */       String mode;
/*  584 */       if ((nMode & 0x0) != 0)
/*  585 */         mode = "r";
/*      */       else {
/*  587 */         mode = "rw";
/*      */       }
/*  589 */       boolean bTruncate = (nMode & 0x200) != 0;
/*      */ 
/*  611 */       String fileName = new String(pFile);
/*  612 */       this._redirectOpenEventArgs.init(fileName, mode, bTruncate);
/*      */ 
/*  614 */       fireRedirectOpen(this._redirectOpenEventArgs);
/*  615 */       if (this._redirectOpenEventArgs.getSuccess()) {
/*  616 */         return Integer.valueOf(1);
/*      */       }
/*  618 */       return Integer.valueOf(-1);
/*      */     }
/*      */ 
/*  621 */     return Long.valueOf(ltfil.RedirectedOpen(pFile, nMode));
/*      */   }
/*      */ 
/*      */   final int CallRedirectRead(Object nFd, byte[] pBuf, int uCount, Object pUserData) {
/*  625 */     if (this._redirectReadSubclassed > 0)
/*      */     {
/*  627 */       this._redirectReadEventArgs.init(pBuf, uCount);
/*      */ 
/*  629 */       fireRedirectRead(this._redirectReadEventArgs);
/*  630 */       return this._redirectReadEventArgs.getRead();
/*      */     }
/*      */ 
/*  633 */     return ltfil.RedirectedRead(((Integer)nFd).intValue(), pBuf, uCount);
/*      */   }
/*      */ 
/*      */   final int CallRedirectWrite(Object nFd, byte[] pBuf, int uCount, Object pUserData) {
/*  637 */     if (this._redirectWriteSubclassed > 0)
/*      */     {
/*  639 */       this._redirectWriteEventArgs.init(pBuf, uCount);
/*      */ 
/*  641 */       fireRedirectWrite(this._redirectWriteEventArgs);
/*  642 */       return this._redirectWriteEventArgs.getWritten();
/*      */     }
/*      */ 
/*  645 */     return ltfil.RedirectedWrite(((Integer)nFd).intValue(), pBuf, uCount);
/*      */   }
/*      */ 
/*      */   final long CallRedirectSeek(Object nFd, long nPos, int nOrigin, Object pUserData) {
/*  649 */     if (this._redirectSeekSubclassed > 0)
/*      */     {
/*  667 */       this._redirectSeekEventArgs.init(nPos, LeadSeekOrigin.forValue(nOrigin));
/*      */ 
/*  669 */       fireRedirectSeek(this._redirectSeekEventArgs);
/*  670 */       return (int)this._redirectSeekEventArgs.getOffset();
/*      */     }
/*      */ 
/*  673 */     return ltfil.RedirectedSeek(((Integer)nFd).intValue(), nPos, nOrigin);
/*      */   }
/*      */ 
/*      */   final int CallRedirectClose(Object nFd, Object pUserData) {
/*  677 */     if (this._redirectCloseSubclassed > 0)
/*      */     {
/*  679 */       this._redirectCloseEventArgs.init();
/*      */ 
/*  681 */       fireRedirectClose(this._redirectCloseEventArgs);
/*  682 */       return L_ERROR.SUCCESS.getValue();
/*      */     }
/*      */ 
/*  685 */     return ltfil.RedirectedClose(((Integer)nFd).intValue());
/*      */   }
/*      */ 
/*      */   public synchronized void addLoadInformationListener(CodecsLoadInformationListener listener)
/*      */   {
/*  696 */     if (this._loadInformation.contains(listener)) {
/*  697 */       return;
/*      */     }
/*  699 */     this._loadInformation.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeLoadInformationListener(CodecsLoadInformationListener listener) {
/*  703 */     if (!this._loadInformation.contains(listener)) {
/*  704 */       return;
/*      */     }
/*  706 */     this._loadInformation.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireLoadInformation(CodecsLoadInformationEvent event) {
/*  710 */     for (CodecsLoadInformationListener listener : this._loadInformation)
/*  711 */       listener.onLoadInformation(event);
/*      */   }
/*      */ 
/*      */   final int LoadInfoCallback(int hFile, LOADINFO pInfo)
/*      */   {
/*  716 */     if (this._loadInformation.size() > 0)
/*      */     {
/*  724 */       CodecsLoadInformationEvent e = new CodecsLoadInformationEvent(this, this._currentStream, new LOADINFO());
/*      */ 
/*  726 */       fireLoadInformation(e);
/*  727 */       e.toUnmanaged(pInfo);
/*      */     }
/*      */ 
/*  730 */     return L_ERROR.SUCCESS.getValue();
/*      */   }
/*      */ 
/*      */   public CodecsImageInfo getInformation(ILeadStream stream, boolean totalPages)
/*      */   {
/*  735 */     return doGetInformation(stream, totalPages, 1, false);
/*      */   }
/*      */ 
/*      */   public CodecsImageInfo getInformation(ILeadStream stream, boolean totalPages, int pageNumber)
/*      */   {
/*  740 */     return doGetInformation(stream, totalPages, pageNumber, false);
/*      */   }
/*      */ 
/*      */   private URLConnection openURLConnection(URI uri)
/*      */   {
/*      */     URL url;
/*      */     try
/*      */     {
/*  762 */       url = uri.toURL();
/*      */     } catch (MalformedURLException e) {
/*  764 */       return null;
/*      */     }
/*      */     try {
/*  767 */       URLConnection connection = url.openConnection();
/*  768 */       connection.setAllowUserInteraction(this._allowUserInteraction);
/*  769 */       return connection; } catch (IOException e) {
/*      */     }
/*  771 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean getAllowUserInteraction()
/*      */   {
/*  793 */     return this._allowUserInteraction;
/*      */   }
/*      */ 
/*      */   public void setAllowUserInteraction(boolean value) {
/*  797 */     this._allowUserInteraction = value;
/*      */   }
/*      */ 
/*      */   private final CodecsImageInfo getInformation(URI uri, boolean totalPages, int pageNumber, AsyncOperationData data)
/*      */   {
/*  803 */     if (uri == null) {
/*  804 */       throw new ArgumentNullException("uri");
/*      */     }
/*      */ 
/*  807 */     String path = getFileUri(uri);
/*  808 */     if (path != null)
/*      */     {
/*  811 */       LeadFileStream fileStream = new LeadFileStream(path);
/*  812 */       return getInformation(fileStream, totalPages, pageNumber);
/*      */     }
/*      */ 
/*  816 */     URLConnection connection = openURLConnection(uri);
/*  817 */     if (connection == null) {
/*  818 */       return null;
/*      */     }
/*  820 */     InputStream stream = null;
/*  821 */     FeedCallbackThunk thunk = null;
/*      */     try
/*      */     {
/*  824 */       stream = connection.getInputStream();
/*  825 */       if (stream == null)
/*      */       {
/*      */         HttpURLConnection httpConnection;
/*  826 */         return null;
/*  828 */       }byte[] buffer = new byte[getUriOperationBufferSize()];
/*      */ 
/*  831 */       boolean more = false;
/*      */ 
/*  833 */       thunk = startFeedGetInformation(totalPages, pageNumber);
/*      */       CodecsImageInfo localCodecsImageInfo2;
/*      */       do {
/*  837 */         bytes = stream.read(buffer, 0, buffer.length);
/*  838 */         if (bytes > 0)
/*  839 */           more = thunk.feedGetInformation(buffer, 0, bytes);
/*  840 */         if ((data != null) && (data.Cancelled))
/*      */         {
/*  842 */           thunk.cancelFeedGetInformation();
/*      */           HttpURLConnection httpConnection;
/*  843 */           return null;
/*      */         }
/*      */       }
/*  846 */       while ((bytes > 0) && (more));
/*      */       HttpURLConnection httpConnection;
/*  848 */       return thunk.stopFeedGetInformation();
/*      */     }
/*      */     catch (IOException e1)
/*      */     {
/*      */       int bytes;
/*  850 */       e1.printStackTrace();
/*  851 */       if (thunk != null)
/*  852 */         thunk.cancelFeedGetInformation();
/*      */       HttpURLConnection httpConnection;
/*  853 */       return null;
/*      */     }
/*      */     finally
/*      */     {
/*  857 */       if (uri.getScheme().equalsIgnoreCase("http"))
/*      */       {
/*  859 */         HttpURLConnection httpConnection = (HttpURLConnection)connection;
/*  860 */         httpConnection.disconnect();
/*      */       }
/*  862 */       safeClose(stream);
/*      */     }
/*      */   }
/*      */ 
/*      */   private CodecsImageInfo doGetInformation(ILeadStream stream, boolean totalPages, int pageNumber, boolean resetStreamPosition)
/*      */   {
/*  868 */     if (stream == null) {
/*  869 */       throw new ArgumentNullException("stream");
/*      */     }
/*  871 */     if ((stream instanceof LeadURIStream)) {
/*  872 */       LeadURIStream uriStream = (LeadURIStream)stream;
/*  873 */       return getInformation(uriStream.getURI(), totalPages, pageNumber, null);
/*      */     }
/*  875 */     if (!stream.canSeek())
/*      */     {
/*  877 */       return doGetInformationNonSeekable(stream, totalPages, pageNumber, resetStreamPosition);
/*      */     }
/*      */ 
/*  880 */     int flags = 0;
/*  881 */     if (totalPages) {
/*  882 */       flags |= 1;
/*      */     }
/*  884 */     this._options.use();
/*  885 */     this._options.getLoadFileOption().PageNumber = pageNumber;
/*  886 */     FILEINFO fi = new FILEINFO();
/*      */ 
/*  888 */     boolean loadInfoUsed = false;
/*  889 */     Object oldCallback = null;
/*      */ 
/*  893 */     if (this._loadInformation.size() > 0)
/*      */     {
/*  895 */       oldCallback = ltfil.SetLoadInfoCallback(this);
/*  896 */       loadInfoUsed = true;
/*      */     }
/*      */ 
/*  899 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/*  902 */     CodecsStreamer streamer = new CodecsStreamer(stream, resetStreamPosition);
/*      */     try
/*      */     {
/*  905 */       ret = streamer.start();
/*  906 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/*  908 */         this._currentStream = streamer.getStream();
/*      */ 
/*  910 */         if (this._options.getLoad().getFormat() != RasterImageFormat.UNKNOWN)
/*      */         {
/*  912 */           fi.Format = this._options.getLoad().getFormat().getValue();
/*  913 */           fi.Flags |= 4096;
/*      */         }
/*      */ 
/*  916 */         String fileName = stream.getFileName();
/*  917 */         ret = ltfil.FileInfo(fileName != null ? fileName : "Stream", fi, flags, this._options.getLoadFileOption());
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  922 */       if (loadInfoUsed) {
/*  923 */         ltfil.SetLoadInfoCallback(oldCallback);
/*      */       }
/*  925 */       if (stream != null) {
/*  926 */         streamer.dispose();
/*      */       }
/*  928 */       this._currentStream = null;
/*      */     }
/*      */ 
/*  932 */     checkExceptions(ret);
/*      */     CodecsImageInfo info;
/*      */     CodecsImageInfo info;
/*  936 */     if (this._userImageInfo != null)
/*      */     {
/*  938 */       this._userImageInfo.initialize(fi);
/*  939 */       info = this._userImageInfo;
/*      */     }
/*      */     else
/*      */     {
/*  943 */       info = new CodecsImageInfo(fi);
/*      */     }
/*      */ 
/*  946 */     return info;
/*      */   }
/*      */ 
/*      */   private CodecsImageInfo doGetInformationNonSeekable(ILeadStream stream, boolean totalPages, int pageNumber, boolean resetStreamPosition)
/*      */   {
/*  951 */     InputStream inputStream = null;
/*  952 */     LeadStream leadStream = (LeadStream)((stream instanceof LeadStream) ? stream : null);
/*  953 */     if (leadStream != null) {
/*  954 */       inputStream = leadStream.getInputStream();
/*      */     }
/*  956 */     if (inputStream == null) {
/*  957 */       throw new ArgumentNullException("Unknown stream type");
/*      */     }
/*  959 */     FeedCallbackThunk thunk = null;
/*      */     try
/*      */     {
/*  962 */       byte[] buffer = new byte[getUriOperationBufferSize()];
/*      */ 
/*  965 */       boolean more = false;
/*  966 */       thunk = startFeedGetInformation(totalPages, pageNumber);
/*      */       do
/*      */       {
/*  970 */         bytes = inputStream.read(buffer, 0, buffer.length);
/*  971 */         if (bytes > 0)
/*  972 */           more = thunk.feedGetInformation(buffer, 0, bytes);
/*      */       }
/*  974 */       while ((bytes > 0) && (more));
/*      */ 
/*  976 */       CodecsImageInfo localCodecsImageInfo = thunk.stopFeedGetInformation(); return localCodecsImageInfo; } catch (IOException e1) {
/*  977 */       e1 = e1;
/*      */ 
/*  978 */       e1.printStackTrace();
/*  979 */       if (thunk != null)
/*  980 */         thunk.cancelFeedGetInformation();
/*  981 */       int bytes = null; return bytes;
/*      */     }
/*      */     finally
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getTotalPages(ILeadStream stream)
/*      */   {
/*  990 */     CodecsImageInfo imageInfo = getInformation(stream, true);
/*  991 */     if (imageInfo != null) {
/*  992 */       return imageInfo.getTotalPages();
/*      */     }
/*  994 */     return 0;
/*      */   }
/*      */ 
/*      */   public RasterImageFormat getFormat(ILeadStream stream) {
/*  998 */     CodecsImageInfo imageInfo = getInformation(stream, true);
/*  999 */     if (imageInfo != null) {
/* 1000 */       return imageInfo.getFormat();
/*      */     }
/* 1002 */     return RasterImageFormat.UNKNOWN;
/*      */   }
/*      */ 
/*      */   public synchronized void addLoadPageListener(CodecsLoadPageListener listener)
/*      */   {
/* 1013 */     if (this._loadPage.contains(listener)) {
/* 1014 */       return;
/*      */     }
/* 1016 */     this._loadPage.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeLoadPageListener(CodecsLoadPageListener listener) {
/* 1020 */     if (!this._loadPage.contains(listener)) {
/* 1021 */       return;
/*      */     }
/* 1023 */     this._loadPage.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireLoadPage(CodecsPageEvent event) {
/* 1027 */     for (CodecsLoadPageListener listener : this._loadPage)
/* 1028 */       listener.onLoadPage(event);
/*      */   }
/*      */ 
/*      */   public synchronized void addLoadImageListener(CodecsLoadImageListener listener)
/*      */   {
/* 1039 */     if (this._loadImage.contains(listener)) {
/* 1040 */       return;
/*      */     }
/* 1042 */     this._loadImage.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeLoadImageListener(CodecsLoadImageListener listener)
/*      */   {
/* 1047 */     if (!this._loadImage.contains(listener)) {
/* 1048 */       return;
/*      */     }
/* 1050 */     this._loadImage.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireLoadImage(CodecsLoadImageEvent event)
/*      */   {
/* 1055 */     for (CodecsLoadImageListener listener : this._loadImage)
/* 1056 */       listener.onLoadImage(event);
/*      */   }
/*      */ 
/*      */   public RasterImage load(String fileName)
/*      */   {
/* 1061 */     return load(LeadStreamFactory.create(fileName));
/*      */   }
/*      */ 
/*      */   public RasterImage load(String fileName, int pageNumber)
/*      */   {
/* 1066 */     return load(LeadStreamFactory.create(fileName), pageNumber);
/*      */   }
/*      */ 
/*      */   public RasterImage load(String fileName, LeadRect tile)
/*      */   {
/* 1071 */     return load(LeadStreamFactory.create(fileName), tile);
/*      */   }
/*      */ 
/*      */   public RasterImage load(String fileName, LeadRect tile, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*      */   {
/* 1076 */     return load(LeadStreamFactory.create(fileName), tile, bitsPerPixel, order, firstPage, lastPage);
/*      */   }
/*      */ 
/*      */   public RasterImage load(String fileName, int width, int height, int bitsPerPixel, RasterSizeFlags flags, CodecsLoadByteOrder order)
/*      */   {
/* 1081 */     return load(LeadStreamFactory.create(fileName), width, height, bitsPerPixel, flags, order);
/*      */   }
/*      */ 
/*      */   public RasterImage load(String fileName, int width, int height, int bitsPerPixel, RasterSizeFlags flags, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*      */   {
/* 1086 */     return load(LeadStreamFactory.create(fileName), width, height, bitsPerPixel, flags, order, firstPage, lastPage);
/*      */   }
/*      */ 
/*      */   public RasterImage load(String fileName, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*      */   {
/* 1091 */     return load(LeadStreamFactory.create(fileName), bitsPerPixel, order, firstPage, lastPage);
/*      */   }
/*      */ 
/*      */   public RasterImage load(ILeadStream stream)
/*      */   {
/* 1096 */     return doLoad(new LoadParams(stream, 0, CodecsLoadByteOrder.BGR_OR_GRAY, 1, getLastLoadPage(1)));
/*      */   }
/*      */ 
/*      */   public RasterImage load(ILeadStream stream, int pageNumber)
/*      */   {
/* 1101 */     return doLoad(new LoadParams(stream, 0, CodecsLoadByteOrder.BGR_OR_GRAY, pageNumber, pageNumber));
/*      */   }
/*      */ 
/*      */   public RasterImage load(ILeadStream stream, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*      */   {
/* 1106 */     return doLoad(new LoadParams(stream, bitsPerPixel, order, firstPage, lastPage));
/*      */   }
/*      */ 
/*      */   public RasterImage load(ILeadStream stream, LeadRect tile)
/*      */   {
/* 1111 */     return doLoad(new LoadParams(stream, 0, CodecsLoadByteOrder.BGR_OR_GRAY, 1, getLastLoadPage(1), tile));
/*      */   }
/*      */ 
/*      */   public RasterImage load(ILeadStream stream, LeadRect tile, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*      */   {
/* 1116 */     return doLoad(new LoadParams(stream, bitsPerPixel, order, firstPage, lastPage, tile));
/*      */   }
/*      */ 
/*      */   public RasterImage load(ILeadStream stream, long offset, long count)
/*      */   {
/* 1121 */     return load(stream, offset, count, 0, CodecsLoadByteOrder.BGR_OR_GRAY, 1, getLastLoadPage(1));
/*      */   }
/*      */ 
/*      */   public RasterImage load(ILeadStream stream, long offset, long count, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*      */   {
/* 1126 */     return doLoad(new LoadParams(stream, offset, count, bitsPerPixel, order, firstPage, lastPage));
/*      */   }
/*      */ 
/*      */   public RasterImage load(ILeadStream stream, int width, int height, int bitsPerPixel, RasterSizeFlags flags, CodecsLoadByteOrder order)
/*      */   {
/* 1131 */     return doLoad(new LoadParams(stream, bitsPerPixel, order, 1, getLastLoadPage(1), width, height, flags));
/*      */   }
/*      */ 
/*      */   public RasterImage load(ILeadStream stream, int width, int height, int bitsPerPixel, RasterSizeFlags flags, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*      */   {
/* 1136 */     return doLoad(new LoadParams(stream, bitsPerPixel, order, firstPage, lastPage, width, height, flags));
/*      */   }
/*      */ 
/*      */   static void safeClose(InputStream stream)
/*      */   {
/* 1141 */     if (stream != null)
/*      */       try
/*      */       {
/* 1144 */         stream.close();
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   public RasterImage load(URI uri)
/*      */   {
/* 1153 */     return load(uri, 1);
/*      */   }
/*      */ 
/*      */   public RasterImage load(URI uri, int pageNumber)
/*      */   {
/* 1158 */     return load(uri, 0, CodecsLoadByteOrder.BGR_OR_GRAY, pageNumber, pageNumber);
/*      */   }
/*      */ 
/*      */   public RasterImage load(URI uri, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*      */   {
/* 1163 */     LeadURIStream stream = new LeadURIStream(uri);
/* 1164 */     AsyncOperationData data = new AsyncOperationData();
/* 1165 */     data.Stream = stream;
/* 1166 */     data.LoadBitsPerPixel = bitsPerPixel;
/* 1167 */     data.LoadOrder = order;
/* 1168 */     data.LoadFirstPage = firstPage;
/* 1169 */     data.LoadLastPage = lastPage;
/* 1170 */     return loadURI(new LoadParams(stream, bitsPerPixel, order, firstPage, lastPage), data);
/*      */   }
/*      */ 
/*      */   private final RasterImage loadURI(LoadParams loadParams, AsyncOperationData data)
/*      */   {
/* 1175 */     LeadURIStream uriStream = (LeadURIStream)loadParams.Stream;
/* 1176 */     URI uri = uriStream.getURI();
/* 1177 */     if (uri == null) {
/* 1178 */       throw new ArgumentNullException("uri");
/*      */     }
/*      */ 
/* 1181 */     String path = getFileUri(uri);
/* 1182 */     if (path != null)
/*      */     {
/* 1185 */       LeadFileStream fileStream = new LeadFileStream(path);
/* 1186 */       if (loadParams.UseSize) {
/* 1187 */         return load(fileStream, loadParams.BitsPerPixel, loadParams.SizeWidth, loadParams.SizeHeight, loadParams.SizeFlags, loadParams.Order, loadParams.FirstPage, loadParams.LastPage);
/*      */       }
/* 1189 */       return load(fileStream, loadParams.BitsPerPixel, loadParams.Order, loadParams.FirstPage, loadParams.LastPage);
/*      */     }
/*      */ 
/* 1194 */     URLConnection connection = openURLConnection(uri);
/* 1195 */     if (connection == null) {
/* 1196 */       return null;
/*      */     }
/* 1198 */     InputStream stream = null;
/*      */     try
/*      */     {
/* 1202 */       stream = new BufferedInputStream(connection.getInputStream(), getUriOperationBufferSize());
/* 1203 */       loadParams.Stream = new LeadStream(stream, true);
/*      */ 
/* 1205 */       return doLoadNonSeekable(loadParams, data);
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/*      */       HttpURLConnection httpConnection;
/* 1209 */       e.printStackTrace();
/*      */       HttpURLConnection httpConnection;
/* 1210 */       return null;
/*      */     }
/*      */     finally
/*      */     {
/* 1214 */       if (uri.getScheme().equalsIgnoreCase("http"))
/*      */       {
/* 1216 */         HttpURLConnection httpConnection = (HttpURLConnection)connection;
/* 1217 */         httpConnection.disconnect();
/*      */       }
/* 1219 */       safeClose(stream);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int save(RasterImage image, URI uri, RasterImageFormat format, int bitsPerPixel)
/*      */     throws IOException
/*      */   {
/* 1234 */     return save(image, uri, format, bitsPerPixel, 1, -1);
/*      */   }
/*      */ 
/*      */   public int save(RasterImage image, URI uri, RasterImageFormat format, int bitsPerPixel, int firstPage, int lastPage)
/*      */     throws IOException
/*      */   {
/* 1250 */     return saveURI(new SaveParams(image, new LeadURIStream(uri), format, bitsPerPixel, firstPage, lastPage, 1, CodecsSavePageMode.OVERWRITE));
/*      */   }
/*      */ 
/*      */   private int saveURI(SaveParams saveParams)
/*      */     throws IOException
/*      */   {
/* 1261 */     RasterImage image = saveParams.Image;
/* 1262 */     if (image == null) {
/* 1263 */       throw new ArgumentNullException("image");
/*      */     }
/* 1265 */     URI uri = ((LeadURIStream)saveParams.Stream).getURI();
/* 1266 */     if (uri == null) {
/* 1267 */       throw new ArgumentNullException("uri");
/*      */     }
/*      */ 
/* 1270 */     String path = getFileUri(uri);
/* 1271 */     if (path != null)
/*      */     {
/* 1274 */       LeadFileStream fileStream = new LeadFileStream(path);
/* 1275 */       save(image, fileStream, saveParams.Format, saveParams.BitsPerPixel, saveParams.FirstPage, saveParams.LastPage, 1, CodecsSavePageMode.OVERWRITE);
/* 1276 */       return 201;
/*      */     }
/*      */ 
/* 1280 */     RasterMemoryRedirectIO ms = new RasterMemoryRedirectIO();
/* 1281 */     save(image, ms, saveParams.Format, saveParams.BitsPerPixel, saveParams.FirstPage, saveParams.LastPage, 1, CodecsSavePageMode.OVERWRITE);
/*      */ 
/* 1283 */     byte[] buffer = ms.getBuffer(false);
/*      */ 
/* 1285 */     URLConnection connection = openURLConnection(uri);
/* 1286 */     if (connection == null)
/* 1287 */       return RasterExceptionCode.INVALID_URL.getValue();
/* 1288 */     connection.setDoOutput(true);
/* 1289 */     connection.setAllowUserInteraction(this._allowUserInteraction);
/* 1290 */     OutputStream stream = connection.getOutputStream();
/* 1291 */     stream.write(buffer);
/* 1292 */     stream.flush();
/* 1293 */     stream.close();
/*      */ 
/* 1295 */     return ((HttpURLConnection)connection).getResponseCode();
/*      */   }
/*      */ 
/*      */   private RasterImage doLoad(LoadParams loadParams)
/*      */   {
/* 1302 */     if ((loadParams.FirstPage < 1) || ((loadParams.LastPage < loadParams.FirstPage) && (loadParams.LastPage != -1))) {
/* 1303 */       RasterException.checkErrorCode(L_ERROR.ERROR_PAGE_NOT_FOUND.getValue());
/*      */     }
/* 1305 */     if ((loadParams.Stream instanceof LeadURIStream)) {
/* 1306 */       return loadURI(loadParams, null);
/*      */     }
/* 1308 */     if ((loadParams.Stream != null) && (!loadParams.Stream.canSeek()))
/*      */     {
/* 1310 */       return doLoadNonSeekable(loadParams, null);
/*      */     }
/*      */ 
/* 1313 */     this._options.use();
/*      */ 
/* 1315 */     if (loadParams.FirstPage != loadParams.LastPage)
/*      */     {
/* 1317 */       CodecsImageInfo info = doGetInformation(loadParams.Stream, true, 1, true);
/* 1318 */       if ((info == null) || (info.getFormat() == RasterImageFormat.UNKNOWN)) {
/* 1319 */         return null;
/*      */       }
/* 1321 */       int totalPages = info.getTotalPages();
/*      */ 
/* 1324 */       if (loadParams.LastPage == -1)
/* 1325 */         loadParams.LastPage = Math.max(totalPages, 1);
/* 1326 */       else if (loadParams.LastPage > totalPages) {
/* 1327 */         RasterException.checkErrorCode(L_ERROR.ERROR_PAGE_NOT_FOUND.getValue());
/*      */       }
/*      */     }
/* 1330 */     RasterImage image = null;
/*      */ 
/* 1332 */     CodecsPageEvent e = new CodecsPageEvent(this);
/*      */ 
/* 1334 */     loadParams.LoadFlags = this._options.getLoadFlags();
/* 1335 */     if (this._options.getLoad().getAllocateImage())
/* 1336 */       loadParams.LoadFlags |= 1;
/* 1337 */     if (this._options.getLoad().getStoreDataInImage()) {
/* 1338 */       loadParams.LoadFlags |= 2;
/*      */     }
/*      */ 
/* 1342 */     this._fileReadCallbackImage = null;
/* 1343 */     this._fileReadCallbackFirstPage = loadParams.FirstPage;
/* 1344 */     this._useFileReadCallbackImage = ((loadParams.LoadFlags & 0x1) == 1);
/*      */ 
/* 1346 */     boolean done = false;
/* 1347 */     int ret = L_ERROR.SUCCESS.getValue();
/* 1348 */     long bitmapHandle = 0L;
/*      */     try
/*      */     {
/* 1352 */       boolean more = true;
/*      */ 
/* 1354 */       for (int i = loadParams.FirstPage; (i <= loadParams.LastPage) && (e.getCommand() != CodecsPageEventCommand.STOP) && (more) && (ret == L_ERROR.SUCCESS.getValue()); i++)
/*      */       {
/* 1356 */         bitmapHandle = ltkrn.AllocBitmapHandle();
/* 1357 */         this._options.getLoadFileOption().PageNumber = i;
/*      */ 
/* 1359 */         e.init(null, i, loadParams.LastPage, loadParams.Stream, CodecsPageEventState.BEFORE);
/* 1360 */         if (this._loadPage.size() > 0)
/*      */         {
/* 1362 */           fireLoadPage(e);
/*      */         }
/* 1364 */         if (e.getCommand() == CodecsPageEventCommand.CONTINUE)
/*      */         {
/* 1366 */           loadParams.Image = image;
/*      */ 
/* 1368 */           loadParams.Page = i;
/* 1369 */           this._loadImageEventArgs.init(loadParams.Image, loadParams.FirstPage, loadParams.Page, loadParams.LastPage, i - loadParams.FirstPage + 1, loadParams.Stream, loadParams.UseTile, loadParams.Tile);
/*      */ 
/* 1379 */           boolean loadInfoUsed = false;
/* 1380 */           Object oldCallback = null;
/*      */ 
/* 1382 */           if (this._loadInformation.size() > 0)
/*      */           {
/* 1384 */             oldCallback = ltfil.SetLoadInfoCallback(this);
/* 1385 */             loadInfoUsed = true;
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/* 1390 */             ret = loadOnePage(bitmapHandle, loadParams.clone());
/*      */           }
/*      */           finally
/*      */           {
/* 1394 */             if (loadInfoUsed) {
/* 1395 */               ltfil.SetLoadInfoCallback(oldCallback);
/*      */             }
/*      */           }
/* 1398 */           if (ret != L_ERROR.SUCCESS.getValue())
/*      */           {
/* 1400 */             if (i > loadParams.FirstPage)
/*      */             {
/* 1402 */               if (image != null)
/*      */               {
/* 1405 */                 if ((ret != L_ERROR.ERROR_INV_RANGE.getValue()) && (image.getPageCount() == i - loadParams.FirstPage + 1)) {
/* 1406 */                   image.removePageAt(image.getPageCount());
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 1412 */               if ((ret == L_ERROR.ERROR_CMP_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_JBIG_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_JB2_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_DXF_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_J2K_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_CMW_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_DCR_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_KDC_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_DCS_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_LTSGM_MISSING.getValue()) || (ret == L_ERROR.ERROR_ABC_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_ABI_FILTER_MISSING.getValue()) || (ret == L_ERROR.ERROR_DCF_FILTER_MISSING.getValue()))
/*      */               {
/* 1426 */                 if (this._loadPage.size() > 0) {
/* 1427 */                   e.init(null, i, loadParams.LastPage, loadParams.Stream, CodecsPageEventState.AFTER);
/*      */ 
/* 1429 */                   fireLoadPage(e);
/*      */                 }
/*      */               }
/* 1432 */               else more = false; 
/*      */             }
/*      */             else
/*      */             {
/* 1435 */               if (ret != L_ERROR.ERROR_USER_ABORT.getValue()) {
/* 1436 */                 if (this._fileReadCallbackImage != null) {
/* 1437 */                   this._fileReadCallbackImage.setNoFreeDataOnDispose(true);
/* 1438 */                   this._fileReadCallbackImage = null;
/*      */                 }
/*      */ 
/* 1441 */                 this._loadImageEventArgs.setImage(null);
/* 1442 */                 ret = L_ERROR.ERROR_USER_ABORT.getValue();
/* 1443 */                 checkExceptions(ret);
/*      */               }
/*      */ 
/* 1446 */               done = true;
/* 1447 */               return null;
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1452 */             if (this._fileReadCallbackImage == null)
/*      */             {
/* 1454 */               if ((loadParams.LoadFlags & 0x1) == 1)
/*      */               {
/* 1456 */                 if (image == null)
/*      */                 {
/* 1458 */                   if (this._userImage == null)
/*      */                   {
/* 1460 */                     image = RasterImage.createFromBitmapHandle(bitmapHandle);
/*      */                   }
/*      */                   else
/*      */                   {
/* 1464 */                     image = this._userImage;
/* 1465 */                     this._userImage = null;
/* 1466 */                     image.initFromBitmapHandle(bitmapHandle);
/*      */                   }
/*      */ 
/* 1469 */                   loadParams.Image = image;
/*      */                 }
/*      */                 else {
/* 1472 */                   image.addPage(RasterImage.createFromBitmapHandle(bitmapHandle));
/*      */                 }
/*      */               }
/* 1475 */               else loadParams.Image = null;
/*      */             }
/*      */             else {
/* 1478 */               image = this._fileReadCallbackImage;
/*      */             }
/* 1480 */             e.init(image, i, loadParams.LastPage, loadParams.Stream, CodecsPageEventState.AFTER);
/* 1481 */             if (this._loadPage.size() > 0)
/*      */             {
/* 1483 */               fireLoadPage(e);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 1488 */         if (bitmapHandle != 0L)
/*      */         {
/* 1490 */           ltkrn.FreeBitmapHandle(bitmapHandle);
/* 1491 */           bitmapHandle = 0L;
/*      */         }
/*      */       }
/*      */ 
/* 1495 */       done = true;
/*      */     }
/*      */     finally
/*      */     {
/* 1499 */       if (bitmapHandle != 0L)
/*      */       {
/* 1501 */         ltkrn.FreeBitmapHandle(bitmapHandle);
/* 1502 */         bitmapHandle = 0L;
/*      */       }
/*      */ 
/* 1505 */       if ((!done) && (image != null))
/*      */       {
/*      */         try
/*      */         {
/* 1509 */           image.dispose();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/*      */       }
/*      */ 
/* 1516 */       this._fileReadCallbackImage = null;
/* 1517 */       this._fileReadCallbackFirstPage = -1;
/*      */     }
/*      */ 
/* 1520 */     if (!checkExceptions(ret)) {
/* 1521 */       return null;
/*      */     }
/* 1523 */     if ((image != null) && (!this._loadImageEventArgs.getCancel()) && (!loadParams.UseTile))
/*      */     {
/* 1525 */       if ((ret == L_ERROR.SUCCESS.getValue()) && (this._options.getLoad().getMarkers()))
/*      */       {
/* 1527 */         RasterCollection markers = readMarkers(loadParams.Stream);
/* 1528 */         for (RasterMarkerMetadata marker : markers)
/* 1529 */           image.getMarkers().add(marker);
/*      */       }
/*      */       else
/*      */       {
/* 1533 */         if ((this._options.getLoad().getTags()) && (tagsSupported(image.getOriginalFormat())))
/*      */         {
/*      */           try
/*      */           {
/* 1537 */             RasterCollection tags = readTags(loadParams.Stream, loadParams.FirstPage);
/* 1538 */             image.getTags().addRange(tags);
/*      */           }
/*      */           catch (RasterException ex)
/*      */           {
/* 1542 */             if (ex.getCode() != RasterExceptionCode.FEATURE_NOT_SUPPORTED) {
/* 1543 */               throw ex;
/*      */             }
/*      */           }
/*      */         }
/* 1547 */         if ((this._options.getLoad().getGeoKeys()) && (geoKeysSupported(image.getOriginalFormat())))
/*      */         {
/*      */           try
/*      */           {
/* 1551 */             RasterCollection geokeys = readGeoKeys(loadParams.Stream, loadParams.FirstPage);
/* 1552 */             image.getGeoKeys().addRange(geokeys);
/*      */           }
/*      */           catch (RasterException ex)
/*      */           {
/* 1556 */             if (ex.getCode() != RasterExceptionCode.FEATURE_NOT_SUPPORTED) {
/* 1557 */               throw ex;
/*      */             }
/*      */           }
/*      */         }
/* 1561 */         if ((this._options.getLoad().getComments()) && (commentsSupported(image.getOriginalFormat())))
/*      */         {
/*      */           try
/*      */           {
/* 1565 */             RasterCollection comments = readComments(loadParams.Stream, loadParams.FirstPage);
/* 1566 */             image.getComments().addRange(comments);
/*      */           }
/*      */           catch (RasterException ex)
/*      */           {
/* 1570 */             if (ex.getCode() != RasterExceptionCode.FEATURE_NOT_SUPPORTED) {
/* 1571 */               throw ex;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1576 */       if (ret != L_ERROR.SUCCESS.getValue())
/*      */       {
/* 1578 */         if (checkExceptions(ret)) {
/* 1579 */           return null;
/*      */         }
/*      */       }
/*      */     }
/* 1583 */     if ((image != null) && (!this._loadImageEventArgs.getCancel())) {
/* 1584 */       setAnimationParameters(image, this._tempFileInfo);
/*      */     }
/* 1586 */     if ((image != null) && (this._loadImageEventArgs.getCancel()))
/*      */     {
/* 1588 */       image = null;
/*      */     }
/*      */ 
/* 1591 */     return image;
/*      */   }
/*      */ 
/*      */   private RasterImage doLoadNonSeekable(LoadParams loadParams, AsyncOperationData data)
/*      */   {
/* 1596 */     if (loadParams.UseSize)
/* 1597 */       throw new InvalidOperationException("Load with size is not supported by non seekable streams");
/* 1598 */     if (loadParams.UseTile) {
/* 1599 */       throw new InvalidOperationException("Load tile is not supported by non seekable streams");
/*      */     }
/* 1601 */     InputStream inputStream = null;
/*      */ 
/* 1603 */     LeadStream leadStream = (LeadStream)((loadParams.Stream instanceof LeadStream) ? loadParams.Stream : null);
/* 1604 */     if (leadStream != null) {
/* 1605 */       inputStream = leadStream.getInputStream();
/*      */     }
/* 1607 */     if (inputStream == null) {
/* 1608 */       throw new ArgumentNullException("Unknown stream type");
/*      */     }
/* 1610 */     byte[] buffer = new byte[getUriOperationBufferSize()];
/*      */ 
/* 1612 */     int maxByteCount = 0;
/*      */ 
/* 1614 */     FeedCallbackThunk thunk = startFeedLoad(loadParams.BitsPerPixel, loadParams.Order, loadParams.FirstPage, loadParams.LastPage);
/*      */     try {
/*      */       int bytesToRead;
/*      */       int bytes;
/*      */       do {
/* 1620 */         bytesToRead = buffer.length;
/* 1621 */         if ((maxByteCount > 0) && (maxByteCount < bytesToRead)) {
/* 1622 */           bytesToRead = maxByteCount;
/*      */         }
/* 1624 */         bytes = inputStream.read(buffer, 0, bytesToRead);
/* 1625 */         if (bytes > 0) {
/* 1626 */           thunk.feedLoad(buffer, 0, bytes);
/*      */         }
/* 1628 */         if ((data != null) && (data.Cancelled))
/*      */         {
/* 1630 */           thunk.cancelFeedLoad();
/* 1631 */           return null;
/*      */         }
/*      */ 
/* 1634 */         if (maxByteCount > 0) {
/* 1635 */           maxByteCount -= bytes;
/* 1636 */           if (maxByteCount <= 0) break;
/*      */         }
/*      */       }
/* 1639 */       while ((bytes > 0) && (!thunk.isFeedLoadDone()));
/*      */ 
/* 1641 */       return thunk.stopFeedLoad();
/*      */     }
/*      */     catch (IOException e) {
/* 1644 */       e.printStackTrace();
/* 1645 */       thunk.cancelFeedLoad();
/*      */     }
/*      */     finally {
/* 1648 */       thunk.cancelFeedLoad();
/*      */     }
/*      */ 
/* 1651 */     return null;
/*      */   }
/*      */ 
/*      */   private int loadOnePage(long bitmapHandle, LoadParams loadParams)
/*      */   {
/* 1657 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/* 1659 */     FILEINFO fi = new FILEINFO();
/* 1660 */     if (this._options.getLoad().getFormat() != RasterImageFormat.UNKNOWN)
/*      */     {
/* 1662 */       fi.Format = this._options.getLoad().getFormat().getValue();
/* 1663 */       fi.Flags |= 4096;
/*      */     }
/*      */ 
/* 1668 */     CodecsStreamer streamer = new CodecsStreamer(loadParams.Stream, false);
/* 1669 */     ret = streamer.start();
/*      */     try
/*      */     {
/* 1672 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 1675 */         if (loadParams.UseTile) {
/* 1676 */           ret = ltfil.LoadFileTile(loadParams.Stream.getFileName(), bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), loadParams.Tile.getLeft(), loadParams.Tile.getTop(), loadParams.Tile.getWidth(), loadParams.Tile.getHeight(), loadParams.BitsPerPixel, loadParams.Order.getValue(), loadParams.LoadFlags, this._loadImage.size() > 0 ? this : null, this._options.getLoadFileOption(), fi);
/*      */         }
/* 1678 */         else if (loadParams.UseSize)
/*      */         {
/* 1683 */           if (this._loadThenResize)
/*      */           {
/* 1685 */             ret = ltfil.LoadFile(loadParams.Stream.getFileName(), bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), loadParams.BitsPerPixel, loadParams.Order.getValue(), loadParams.LoadFlags, null, this._options.getLoadFileOption(), fi);
/* 1686 */             if (ret == L_ERROR.SUCCESS.getValue())
/* 1687 */               ret = ltkrn.SizeBitmap(bitmapHandle, loadParams.SizeWidth, loadParams.SizeHeight, loadParams.SizeFlags.getValue());
/*      */           }
/*      */           else {
/* 1690 */             ret = ltfil.LoadBitmapResize(loadParams.Stream.getFileName(), bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), loadParams.SizeWidth, loadParams.SizeHeight, loadParams.BitsPerPixel, loadParams.SizeFlags.getValue(), loadParams.Order.getValue(), this._options.getLoadFileOption(), fi);
/*      */           }
/*      */         }
/* 1693 */         else ret = ltfil.LoadFile(loadParams.Stream.getFileName(), bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), loadParams.BitsPerPixel, loadParams.Order.getValue(), loadParams.LoadFlags, this._loadImage.size() > 0 ? this : null, this._options.getLoadFileOption(), fi); 
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1697 */       streamer.dispose();
/*      */     }
/*      */ 
/* 1700 */     if (ret == L_ERROR.SUCCESS.getValue()) {
/* 1701 */       ltkrn.BITMAPHANDLE_setOriginalFormat(bitmapHandle, fi.Format);
/*      */     }
/*      */ 
/* 1704 */     if (this._fileReadCallbackImage != null)
/*      */     {
/* 1707 */       long pImageBitmap = this._fileReadCallbackImage.getCurrentBitmapHandle();
/* 1708 */       unlockBitmap(pImageBitmap);
/*      */     }
/*      */ 
/* 1711 */     if (ret == L_ERROR.SUCCESS.getValue())
/*      */     {
/* 1713 */       ltkrn.BITMAPHANDLE_setOriginalFormat(bitmapHandle, fi.Format);
/* 1714 */       this._tempFileInfo = fi.clone();
/*      */ 
/* 1719 */       switch (fi.Format)
/*      */       {
/*      */       case 75:
/*      */       case 321:
/* 1723 */         THREADDATA threadData = this._options.getThreadData();
/* 1724 */         ltfil.GetPNGTRNS(threadData.aPNGTRNSData);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1729 */     return ret;
/*      */   }
/*      */ 
/*      */   private void unlockBitmap(long bitmapHandle)
/*      */   {
/* 1734 */     if (ltkrn.BITMAPHANDLE_getFlagsAllocated(bitmapHandle))
/*      */     {
/* 1736 */       while (ltkrn.BITMAPHANDLE_getLockCount(bitmapHandle) > 0)
/* 1737 */         ltkrn.ReleaseBitmap(bitmapHandle);
/*      */     }
/* 1739 */     this._fileReadCallbackImage.updateCurrentBitmapHandle();
/*      */   }
/*      */ 
/*      */   final int FileReadCallback(FILEINFO pFileInfo, long pBitmap, byte[] pBuffer, int uFlags, int nRow, int nLines)
/*      */   {
/* 1744 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/* 1746 */     if ((this._useFileReadCallbackImage) && (this._fileReadCallbackImage == null))
/*      */     {
/* 1748 */       if (this._userImage == null)
/*      */       {
/* 1750 */         this._fileReadCallbackImage = RasterImage.createFromBitmapHandle(pBitmap);
/*      */       }
/*      */       else
/*      */       {
/* 1754 */         this._fileReadCallbackImage = this._userImage;
/* 1755 */         this._userImage = null;
/* 1756 */         this._fileReadCallbackImage.initFromBitmapHandle(pBitmap);
/*      */       }
/*      */ 
/* 1759 */       this._loadImageEventArgs.setImage(this._fileReadCallbackImage);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1765 */       if (this._fileReadCallbackImage != null)
/*      */       {
/* 1767 */         long pImageBitmap = this._fileReadCallbackImage.getCurrentBitmapHandle();
/* 1768 */         int[] flags1 = new int[1];
/* 1769 */         int[] flags2 = new int[1];
/* 1770 */         ltkrn.BITMAPHANDLE_getFlagsValue(pImageBitmap, flags1);
/* 1771 */         ltkrn.BITMAPHANDLE_getFlagsValue(pBitmap, flags2);
/* 1772 */         if ((ltkrn.BITMAPHANDLE_getFlagsAllocated(pImageBitmap)) && (ltkrn.BITMAPHANDLE_getFlagsAllocated(pBitmap)) && (flags1[0] != flags2[0]))
/*      */         {
/* 1774 */           ltkrn.CopyBitmap(pImageBitmap, pBitmap, ltkrn.BITMAPHANDLE_getSizeOf());
/*      */ 
/* 1776 */           this._fileReadCallbackImage.updateCurrentBitmapHandle();
/*      */         }
/*      */       }
/*      */ 
/* 1780 */       if ((this._useFileReadCallbackImage) && ((uFlags & 0xA) == 10))
/*      */       {
/* 1782 */         if (pFileInfo.PageNumber > this._fileReadCallbackFirstPage)
/*      */         {
/* 1784 */           RasterImage image = RasterImage.createFromBitmapHandle(pBitmap);
/* 1785 */           if (this._fileReadCallbackImage != null)
/*      */           {
/* 1787 */             this._fileReadCallbackImage.addPage(image);
/*      */           }
/*      */         }
/*      */ 
/* 1791 */         ltkrn.MarkAsFreed(pBitmap);
/*      */       }
/*      */ 
/* 1794 */       this._loadImageEventArgs.init(pFileInfo, pBuffer, uFlags, nRow, nLines);
/*      */ 
/* 1796 */       int imageHeight = this._fileReadCallbackImage != null ? this._fileReadCallbackImage.getHeight() : ltkrn.BITMAPHANDLE_getHeight(pBitmap);
/* 1797 */       int pagePercent = imageHeight != 0 ? (int)(100 > nRow + nLines ? 100.0D / imageHeight * (nRow + nLines) : (nRow + nLines) / imageHeight * 100.0D) : 0;
/* 1798 */       int totalPageCount = this._loadImageEventArgs.getLastPage() - this._loadImageEventArgs.getFirstPage() + 1;
/* 1799 */       int totalPercent = 100 * totalPageCount != 0 ? (int)(100 > 100 * (this._loadImageEventArgs.getImagePage() - 1) + pagePercent ? 100.0D / (100 * totalPageCount) * (100 * (this._loadImageEventArgs.getImagePage() - 1) + pagePercent) : (100 * (this._loadImageEventArgs.getImagePage() - 1) + pagePercent) / (100 * totalPageCount) * 100.0D) : 0;
/* 1800 */       this._loadImageEventArgs.setPercentages(pagePercent, totalPercent);
/*      */ 
/* 1803 */       fireLoadImage(this._loadImageEventArgs);
/* 1804 */       if (this._loadImageEventArgs.getCancel())
/*      */       {
/* 1806 */         if (this._fileReadCallbackImage != null)
/*      */         {
/* 1808 */           this._fileReadCallbackImage.setNoFreeDataOnDispose(true);
/* 1809 */           this._fileReadCallbackImage = null;
/*      */         }
/*      */ 
/* 1812 */         this._loadImageEventArgs.setImage(null);
/* 1813 */         ret = L_ERROR.ERROR_USER_ABORT.getValue();
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1818 */       this._loadImageEventArgs.freeImageInfo();
/*      */     }
/*      */ 
/* 1821 */     return ret;
/*      */   }
/*      */ 
/*      */   private static void setAnimationParameters(RasterImage image, FILEINFO fileInfo)
/*      */   {
/* 1826 */     int animationGlobalLoop = -1;
/* 1827 */     int animationGlobalWidth = 0;
/* 1828 */     int animationGlobalHeight = 0;
/* 1829 */     int animationGlobalBackground = 0;
/*      */ 
/* 1831 */     switch (1.$SwitchMap$leadtools$RasterImageFormat[image.getOriginalFormat().ordinal()])
/*      */     {
/*      */     case 1:
/* 1834 */       if (image.getPageCount() > 1)
/*      */       {
/* 1836 */         if ((fileInfo.Flags & 0x20) == 32) {
/* 1837 */           animationGlobalLoop = fileInfo.GlobalLoop;
/*      */         }
/* 1839 */         animationGlobalWidth = fileInfo.GlobalWidth;
/* 1840 */         animationGlobalHeight = fileInfo.GlobalHeight;
/*      */ 
/* 1842 */         if ((fileInfo.Flags & 0x8) == 8)
/* 1843 */           animationGlobalBackground = fileInfo.GlobalBackground;  } break;
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/* 1851 */       if ((fileInfo.Flags & 0x20) == 32) {
/* 1852 */         animationGlobalLoop = fileInfo.GlobalLoop;
/*      */       }
/* 1854 */       animationGlobalWidth = fileInfo.GlobalWidth;
/* 1855 */       animationGlobalHeight = fileInfo.GlobalHeight;
/*      */ 
/* 1857 */       if ((fileInfo.Flags & 0x8) == 8)
/* 1858 */         animationGlobalBackground = fileInfo.GlobalBackground; break;
/*      */     }
/*      */ 
/* 1864 */     image.setAnimationGlobalLoop(animationGlobalLoop);
/* 1865 */     image.setAnimationGlobalSize(new LeadSize(animationGlobalWidth, animationGlobalHeight));
/* 1866 */     image.setAnimationGlobalBackground(RasterColor.fromColorRef(animationGlobalBackground));
/*      */   }
/*      */ 
/*      */   private static boolean isAnimationFileFormat(RasterImageFormat format)
/*      */   {
/* 1871 */     return (format == RasterImageFormat.GIF) || (format == RasterImageFormat.FLC) || (format == RasterImageFormat.FLI) || (format == RasterImageFormat.ANI);
/*      */   }
/*      */ 
/*      */   public synchronized void addSavePageListener(CodecsSavePageListener listener)
/*      */   {
/* 1882 */     if (this._savePage.contains(listener)) {
/* 1883 */       return;
/*      */     }
/* 1885 */     this._savePage.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeSavePageListener(CodecsSavePageListener listener) {
/* 1889 */     if (!this._savePage.contains(listener)) {
/* 1890 */       return;
/*      */     }
/* 1892 */     this._savePage.removeElement(listener);
/*      */   }
/*      */ 
/*      */   synchronized void fireSavePage(CodecsPageEvent event) {
/* 1896 */     for (CodecsSavePageListener listener : this._savePage)
/* 1897 */       listener.onSavePage(event);
/*      */   }
/*      */ 
/*      */   public synchronized void addSaveImageListener(CodecsSaveImageListener listener)
/*      */   {
/* 1908 */     if (this._saveImage.contains(listener)) {
/* 1909 */       return;
/*      */     }
/* 1911 */     this._saveImage.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeSaveImageListener(CodecsSaveImageListener listener) {
/* 1915 */     if (!this._saveImage.contains(listener)) {
/* 1916 */       return;
/*      */     }
/* 1918 */     this._saveImage.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireSaveImage(CodecsSaveImageEvent event) {
/* 1922 */     for (CodecsSaveImageListener listener : this._saveImage)
/* 1923 */       listener.onSaveImage(event);
/*      */   }
/*      */ 
/*      */   public void save(RasterImage image, String fileName, RasterImageFormat format, int bitsPerPixel)
/*      */   {
/* 1928 */     save(image, fileName, format, bitsPerPixel, 1, -1, 1, CodecsSavePageMode.OVERWRITE);
/*      */   }
/*      */ 
/*      */   public void save(RasterImage image, String fileName, RasterImageFormat format, int bitsPerPixel, int firstPage, int lastPage, int firstSavePageNumber, CodecsSavePageMode pageMode)
/*      */   {
/* 1933 */     if (fileName == null) {
/* 1934 */       throw new ArgumentNullException("fileName");
/*      */     }
/* 1936 */     ILeadStream stream = LeadStreamFactory.create(fileName);
/*      */ 
/* 1938 */     save(image, stream, format, bitsPerPixel, firstPage, lastPage, firstSavePageNumber, pageMode);
/*      */   }
/*      */ 
/*      */   public void save(RasterImage image, ILeadStream stream, RasterImageFormat format, int bitsPerPixel)
/*      */   {
/* 1943 */     doSave(new SaveParams(image, stream, format, bitsPerPixel, image.getPage(), -1, 1, CodecsSavePageMode.OVERWRITE));
/*      */   }
/*      */ 
/*      */   public void save(RasterImage image, ILeadStream stream, RasterImageFormat format, int bitsPerPixel, int firstPage, int lastPage, int firstSavePageNumber, CodecsSavePageMode pageMode)
/*      */   {
/* 1948 */     doSave(new SaveParams(image, stream, format, bitsPerPixel, firstPage, lastPage, firstSavePageNumber, pageMode));
/*      */   }
/*      */ 
/*      */   public void save(RasterImage image, ILeadStream stream, long offset, RasterImageFormat format, int bitsPerPixel)
/*      */   {
/* 1953 */     save(image, stream, offset, format, bitsPerPixel, 1, -1, 1, CodecsSavePageMode.OVERWRITE);
/*      */   }
/*      */ 
/*      */   public void save(RasterImage image, ILeadStream stream, long offset, RasterImageFormat format, int bitsPerPixel, int firstPage, int lastPage, int firstSavePageNumber, CodecsSavePageMode pageMode)
/*      */   {
/* 1958 */     doSave(new SaveParams(image, stream, offset, format, bitsPerPixel, firstPage, lastPage, firstSavePageNumber, pageMode));
/*      */   }
/*      */ 
/*      */   public void saveStamp(RasterImage image, ILeadStream stream)
/*      */   {
/* 1963 */     doSave(new SaveParams(image, stream, RasterImageFormat.UNKNOWN, 0, image.getPage(), image.getPage(), 1, CodecsSavePageMode.OVERWRITE, true));
/*      */   }
/*      */ 
/*      */   public void saveStamp(RasterImage image, ILeadStream stream, int firstPage, int lastPage, int firstSavePageNumber, CodecsSavePageMode pageMode)
/*      */   {
/* 1968 */     doSave(new SaveParams(image, stream, RasterImageFormat.UNKNOWN, 0, firstPage, lastPage, firstSavePageNumber, pageMode, true));
/*      */   }
/*      */ 
/*      */   private long doSave(SaveParams saveParams)
/*      */   {
/* 1973 */     if ((saveParams.Stream instanceof LeadURIStream)) {
/*      */       try {
/* 1975 */         return saveURI(saveParams);
/*      */       } catch (Exception e) {
/* 1977 */         throw new RasterException(e.getMessage());
/*      */       }
/*      */     }
/*      */ 
/* 1981 */     this._options.use();
/*      */ 
/* 1983 */     if (saveParams.LastPage == -1) {
/* 1984 */       saveParams.LastPage = saveParams.Image.getPageCount();
/*      */     }
/* 1986 */     CodecsPageEvent e = new CodecsPageEvent(this);
/* 1987 */     long doSaveRet = L_ERROR.SUCCESS.getValue();
/* 1988 */     boolean more = true;
/*      */     try
/*      */     {
/* 1992 */       if ((!saveParams.UseStamp) && (this._options.getSave().getTags())) {
/* 1993 */         setTags(saveParams.Image.getTags());
/*      */       }
/* 1995 */       if ((!saveParams.UseStamp) && (this._options.getSave().getGeoKeys())) {
/* 1996 */         setGeoKeys(saveParams.Image.getGeoKeys());
/*      */       }
/* 1998 */       if ((!saveParams.UseStamp) && (this._options.getSave().getComments())) {
/* 1999 */         setComments(saveParams.Image.getComments());
/*      */       }
/* 2001 */       if ((!saveParams.UseStamp) && (this._options.getSave().getMarkers())) {
/* 2002 */         setMarkers(saveParams.Image.getMarkers());
/*      */       }
/*      */ 
/* 2006 */       if (isAnimationFileFormat(saveParams.Format))
/*      */       {
/* 2009 */         if (this._options.getSaveFileOption().GlobalLoop != -1)
/*      */         {
/* 2011 */           this._options.getSaveFileOption().GlobalLoop = saveParams.Image.getAnimationGlobalLoop();
/* 2012 */           this._options.getSaveFileOption().Flags |= 32;
/*      */ 
/* 2014 */           LeadSize animationGlobalSize = saveParams.Image.getAnimationGlobalSize();
/* 2015 */           this._options.getSaveFileOption().GlobalWidth = animationGlobalSize.getWidth();
/* 2016 */           this._options.getSaveFileOption().GlobalHeight = animationGlobalSize.getHeight();
/*      */ 
/* 2018 */           this._options.getSaveFileOption().GlobalBackground = saveParams.Image.getAnimationGlobalBackground().getColorRef();
/* 2019 */           this._options.getSaveFileOption().Flags |= 4;
/*      */         }
/*      */       }
/*      */ 
/* 2023 */       for (int i = saveParams.FirstPage; (i <= saveParams.LastPage) && (e.getCommand() != CodecsPageEventCommand.STOP); i++)
/*      */       {
/* 2025 */         e.init(saveParams.Image, i, saveParams.LastPage, saveParams.Stream, CodecsPageEventState.BEFORE);
/*      */ 
/* 2027 */         if (this._savePage.size() > 0)
/*      */         {
/* 2029 */           fireSavePage(e);
/*      */         }
/* 2031 */         if (e.getCommand() == CodecsPageEventCommand.CONTINUE)
/*      */         {
/* 2033 */           doSaveRet = saveOnePage(i, saveParams);
/* 2034 */           saveParams.FirstSavePageNumber += 1;
/*      */ 
/* 2036 */           if (saveParams.PageMode == CodecsSavePageMode.OVERWRITE) {
/* 2037 */             saveParams.PageMode = CodecsSavePageMode.APPEND;
/*      */           }
/* 2039 */           e.init(saveParams.Image, i, saveParams.LastPage, saveParams.Stream, CodecsPageEventState.AFTER);
/*      */ 
/* 2041 */           if (this._savePage.size() > 0)
/*      */           {
/* 2043 */             fireSavePage(e);
/*      */           }
/* 2045 */           if (!doesFormatSupportSaveMultipage(saveParams.Format)) {
/* 2046 */             return doSaveRet;
/*      */           }
/*      */         }
/*      */       }
/* 2050 */       return doSaveRet;
/*      */     }
/*      */     finally
/*      */     {
/* 2054 */       if ((!saveParams.UseStamp) && (this._options.getSave().getTags())) {
/* 2055 */         setTags(null);
/*      */       }
/* 2057 */       if ((!saveParams.UseStamp) && (this._options.getSave().getGeoKeys())) {
/* 2058 */         setGeoKeys(null);
/*      */       }
/* 2060 */       if ((!saveParams.UseStamp) && (this._options.getSave().getComments())) {
/* 2061 */         setComments(null);
/*      */       }
/* 2063 */       if ((!saveParams.UseStamp) && (this._options.getSave().getMarkers()))
/* 2064 */         setMarkers(null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private long saveOnePage(int page, SaveParams saveParams)
/*      */   {
/* 2070 */     long saveOnePageRet = 0L;
/*      */ 
/* 2072 */     this._options.getSaveFileOption().Flags &= -3073;
/*      */ 
/* 2074 */     switch (1.$SwitchMap$leadtools$codecs$CodecsSavePageMode[saveParams.PageMode.ordinal()])
/*      */     {
/*      */     case 1:
/* 2077 */       this._options.getSaveFileOption().PageNumber = 1;
/* 2078 */       break;
/*      */     case 2:
/* 2081 */       this._options.getSaveFileOption().PageNumber = 2;
/* 2082 */       break;
/*      */     case 3:
/* 2085 */       this._options.getSaveFileOption().PageNumber = saveParams.FirstSavePageNumber;
/* 2086 */       this._options.getSaveFileOption().Flags |= 2048;
/* 2087 */       break;
/*      */     case 4:
/* 2090 */       this._options.getSaveFileOption().PageNumber = saveParams.FirstSavePageNumber;
/* 2091 */       this._options.getSaveFileOption().Flags |= 1024;
/*      */     }
/*      */ 
/* 2095 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/* 2096 */     saveParams.Image.getBitmapHandle(bitmapHandle, page);
/* 2097 */     int qFactor = this._options.getRealQualityFactor(saveParams.Format, saveParams.BitsPerPixel);
/*      */ 
/* 2099 */     this._currentSavedPageBitmapHandle = 0L;
/* 2100 */     this._currentSavedPageBitmapHandleBuffer = null;
/*      */ 
/* 2102 */     if ((this._options.getSave().getRetrieveDataFromImage()) && (!saveParams.UseStamp))
/*      */     {
/* 2104 */       this._currentSavedPageBitmapHandle = bitmapHandle;
/* 2105 */       this._currentSavedPageBitmapHandleBuffer = new byte[ltkrn.BITMAPHANDLE_getBytesPerLine(this._currentSavedPageBitmapHandle)];
/* 2106 */       if (this._currentSavedPageBitmapHandleBuffer == null) {
/* 2107 */         throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */       }
/* 2109 */       ltkrn.AccessBitmap(this._currentSavedPageBitmapHandle);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 2118 */       int saveFlags = this._options.getSaveFlags();
/* 2119 */       if ((saveFlags & 0x1) == 0)
/*      */       {
/* 2121 */         if ((ltkrn.BITMAPHANDLE_getBitsPerPixel(bitmapHandle) != saveParams.BitsPerPixel) && (saveParams.BitsPerPixel == 1))
/* 2122 */           saveFlags |= 1;
/*      */         else {
/* 2124 */           saveFlags |= 2;
/*      */         }
/*      */       }
/* 2127 */       if (this._saveImage.size() > 0) {
/* 2128 */         this._saveImageEventArgs.init(saveParams.Image, saveParams.Stream, saveParams.OriginalFirstSavePageNumber, page - saveParams.FirstPage + 1, saveParams.LastPage - saveParams.FirstPage + saveParams.OriginalFirstSavePageNumber, page);
/*      */       }
/*      */ 
/* 2132 */       CodecsStreamer streamer = new CodecsStreamer(saveParams.Stream, false);
/* 2133 */       int ret = streamer.start();
/* 2134 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/*      */         try
/*      */         {
/* 2138 */           if (saveParams.UseStamp)
/* 2139 */             ret = ltfil.WriteFileStamp(saveParams.Stream.getFileName(), bitmapHandle, this._options.getSaveFileOption());
/*      */           else
/* 2141 */             ret = ltfil.SaveFile(saveParams.Stream.getFileName(), bitmapHandle, saveParams.Format.getValue(), saveParams.BitsPerPixel, qFactor, saveFlags, this._saveImage.size() > 0 ? this : null, this._options.getSaveFileOption());
/*      */         }
/*      */         finally {
/* 2144 */           streamer.dispose();
/*      */         }
/*      */       }
/*      */       else {
/* 2148 */         throw new InvalidOperationException("invalid stream");
/*      */       }
/* 2150 */       RasterException.checkErrorCode(ret);
/*      */ 
/* 2154 */       if (bitmapHandle != 0L) {
/* 2155 */         ltkrn.FreeBitmapHandle(bitmapHandle);
/*      */       }
/* 2157 */       if (this._currentSavedPageBitmapHandleBuffer != null)
/*      */       {
/* 2159 */         this._currentSavedPageBitmapHandleBuffer = null;
/*      */       }
/*      */ 
/* 2162 */       if (this._currentSavedPageBitmapHandle != 0L)
/*      */       {
/* 2164 */         ltkrn.ReleaseBitmap(this._currentSavedPageBitmapHandle);
/* 2165 */         this._currentSavedPageBitmapHandle = 0L;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 2154 */       if (bitmapHandle != 0L) {
/* 2155 */         ltkrn.FreeBitmapHandle(bitmapHandle);
/*      */       }
/* 2157 */       if (this._currentSavedPageBitmapHandleBuffer != null)
/*      */       {
/* 2159 */         this._currentSavedPageBitmapHandleBuffer = null;
/*      */       }
/*      */ 
/* 2162 */       if (this._currentSavedPageBitmapHandle != 0L)
/*      */       {
/* 2164 */         ltkrn.ReleaseBitmap(this._currentSavedPageBitmapHandle);
/* 2165 */         this._currentSavedPageBitmapHandle = 0L;
/*      */       }
/*      */     }
/*      */ 
/* 2169 */     return saveOnePageRet;
/*      */   }
/*      */ 
/*      */   final int FileSaveCallback(long pBitmap, byte[] pBuffer, int uRow, int uLines)
/*      */   {
/* 2174 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/* 2176 */     this._saveImageEventArgs.init(uRow, uLines, pBuffer);
/*      */ 
/* 2178 */     int bitmapHeight = ltkrn.BITMAPHANDLE_getHeight(pBitmap);
/* 2179 */     int pagePercent = bitmapHeight != 0 ? (int)(100 > uRow + uLines ? 100.0D / bitmapHeight * (uRow + uLines) : (uRow + uLines) / bitmapHeight * 100.0D) : 0;
/* 2180 */     int pageCount = this._saveImageEventArgs.getLastPage() - this._saveImageEventArgs.getFirstPage() + 1;
/* 2181 */     int totalPercent = 100 * pageCount != 0 ? (int)(100 > 100 * (this._saveImageEventArgs.getPage() - 1) + pagePercent ? 100.0D / (100 * pageCount) * (100 * (this._saveImageEventArgs.getPage() - 1) + pagePercent) : (100 * (this._saveImageEventArgs.getPage() - 1) + pagePercent) / (100 * pageCount) * 100.0D) : 0;
/* 2182 */     this._saveImageEventArgs.setPercentages(pagePercent, totalPercent);
/*      */ 
/* 2185 */     if ((this._currentSavedPageBitmapHandle != 0L) && (this._currentSavedPageBitmapHandleBuffer != null))
/*      */     {
/* 2187 */       int lastRow = uRow + uLines;
/* 2188 */       int bytesPerLine = ltkrn.BITMAPHANDLE_getBytesPerLine(this._currentSavedPageBitmapHandle);
/*      */ 
/* 2190 */       for (int y = uRow; (y < lastRow) && (ret == L_ERROR.SUCCESS.getValue()); y++)
/*      */       {
/* 2192 */         long sRet = ltkrn.GetBitmapRow(this._currentSavedPageBitmapHandle, this._currentSavedPageBitmapHandleBuffer, 0, y, bytesPerLine);
/* 2193 */         if (ret < 0)
/*      */         {
/* 2195 */           ret = (int)sRet;
/*      */         }
/*      */         else
/*      */         {
/* 2199 */           System.arraycopy(this._currentSavedPageBitmapHandleBuffer, 0, pBuffer, (uRow - y) * bytesPerLine, bytesPerLine);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2204 */     if (ret == L_ERROR.SUCCESS.getValue())
/*      */     {
/* 2208 */       fireSaveImage(this._saveImageEventArgs);
/* 2209 */       if (this._saveImageEventArgs.getCancel()) {
/* 2210 */         ret = L_ERROR.ERROR_USER_ABORT.getValue();
/*      */       }
/*      */     }
/* 2213 */     return ret;
/*      */   }
/*      */ 
/*      */   private boolean doesFormatSupportSaveMultipage(RasterImageFormat format) {
/* 2217 */     switch (1.$SwitchMap$leadtools$RasterImageFormat[format.ordinal()])
/*      */     {
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 14:
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/*      */     case 21:
/*      */     case 22:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     case 26:
/*      */     case 27:
/*      */     case 28:
/*      */     case 29:
/*      */     case 30:
/*      */     case 31:
/*      */     case 32:
/*      */     case 33:
/*      */     case 34:
/*      */     case 35:
/*      */     case 36:
/*      */     case 37:
/*      */     case 38:
/*      */     case 39:
/*      */     case 40:
/*      */     case 41:
/*      */     case 42:
/*      */     case 43:
/*      */     case 44:
/*      */     case 45:
/*      */     case 46:
/*      */     case 47:
/*      */     case 48:
/*      */     case 49:
/*      */     case 50:
/*      */     case 51:
/*      */     case 52:
/*      */     case 53:
/*      */     case 54:
/*      */     case 55:
/*      */     case 56:
/*      */     case 57:
/*      */     case 58:
/*      */     case 59:
/*      */     case 60:
/*      */     case 61:
/*      */     case 62:
/*      */     case 63:
/*      */     case 64:
/*      */     case 65:
/*      */     case 66:
/*      */     case 67:
/*      */     case 68:
/*      */     case 69:
/*      */     case 70:
/*      */     case 71:
/*      */     case 72:
/*      */     case 73:
/*      */     case 74:
/*      */     case 75:
/*      */     case 76:
/*      */     case 77:
/*      */     case 78:
/*      */     case 79:
/*      */     case 80:
/*      */     case 81:
/*      */     case 82:
/*      */     case 83:
/*      */     case 84:
/* 2304 */       return true;
/*      */     }
/*      */ 
/* 2308 */     return false;
/*      */   }
/*      */ 
/*      */   static int MulDiv(int a, int b, int c)
/*      */   {
/* 2313 */     return (a * b + c / 2) / c;
/*      */   }
/*      */ 
/*      */   public FeedCallbackThunk startFeedLoad(int bitsPerPixel, CodecsLoadByteOrder order)
/*      */   {
/* 2652 */     return startFeedLoad(bitsPerPixel, order, 1, getLastLoadPage(1));
/*      */   }
/*      */ 
/*      */   public FeedCallbackThunk startFeedLoad(int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage) {
/* 2656 */     if ((firstPage < 1) || ((lastPage < firstPage) && (lastPage != -1))) {
/* 2657 */       RasterException.checkErrorCode(RasterExceptionCode.PAGE_NOT_FOUND);
/*      */     }
/* 2659 */     FeedCallbackThunk thunk = new FeedCallbackThunk();
/* 2660 */     thunk.startFeedLoad(bitsPerPixel, order, firstPage, lastPage);
/* 2661 */     return thunk;
/*      */   }
/*      */ 
/*      */   public FeedCallbackThunk startFeedGetInformation(boolean totalPages, int pageNumber)
/*      */   {
/* 2666 */     FeedCallbackThunk thunk = new FeedCallbackThunk();
/* 2667 */     thunk.startFeedGetInformation(totalPages, pageNumber);
/* 2668 */     return thunk;
/*      */   }
/*      */ 
/*      */   private void setTags(RasterCollection<RasterTagMetadata> tags)
/*      */   {
/* 2673 */     this._options.use();
/*      */ 
/* 2675 */     if (tags != null)
/*      */     {
/* 2677 */       for (RasterTagMetadata tag : tags)
/*      */       {
/* 2679 */         int ret = ltfil.SetTag((short)tag.getId(), (short)tag.getDataType().getValue(), tag.getCount(), tag.getData());
/* 2680 */         RasterException.checkErrorCode(ret);
/*      */       }
/*      */     }
/*      */     else
/* 2684 */       ltfil.SetTag((short)0, (short)0, 0, null);
/*      */   }
/*      */ 
/*      */   private void setGeoKeys(RasterCollection<RasterTagMetadata> keys) {
/* 2688 */     this._options.use();
/*      */ 
/* 2690 */     if (keys != null)
/*      */     {
/* 2692 */       for (RasterTagMetadata key : keys)
/*      */       {
/* 2694 */         int ret = ltfil.SetGeoKey((short)key.getId(), key.getDataType().getValue(), key.getCount(), key.getData());
/* 2695 */         RasterException.checkErrorCode(ret);
/*      */       }
/*      */     }
/*      */     else
/* 2699 */       ltfil.SetGeoKey((short)0, 0, 0, null);
/*      */   }
/*      */ 
/*      */   private void setComments(RasterCollection<RasterCommentMetadata> comments)
/*      */   {
/* 2704 */     this._options.use();
/*      */ 
/* 2706 */     if (comments != null)
/*      */     {
/* 2708 */       for (RasterCommentMetadata comment : comments)
/*      */       {
/* 2710 */         byte[] data = comment.getData();
/* 2711 */         int length = data != null ? data.length : 0;
/* 2712 */         if ((comment.getDataType().equals(RasterCommentMetadataDataType.ASCII)) && (data != null)) {
/* 2713 */           length++;
/*      */         }
/* 2715 */         int ret = ltfil.SetComment(comment.getType().getValue(), length, data);
/* 2716 */         if (ret < 0) {
/* 2717 */           RasterException.checkErrorCode(ret);
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/* 2722 */       for (int i = 0; i < 247; i++)
/* 2723 */         ltfil.SetComment(i, 0, null);
/*      */   }
/*      */ 
/*      */   private void setMarkers(RasterCollection<RasterMarkerMetadata> markers)
/*      */   {
/* 2729 */     this._options.use();
/*      */ 
/* 2731 */     if (markers != null)
/*      */     {
/* 2733 */       freeMarkers();
/*      */       try
/*      */       {
/* 2737 */         long[] hmarkers = new long[1];
/* 2738 */         int ret = ltfil.CreateMarkers(hmarkers);
/* 2739 */         RasterException.checkErrorCode(ret);
/*      */ 
/* 2741 */         this._hmarkers = hmarkers[0];
/*      */ 
/* 2743 */         for (int i = 0; i < markers.size(); i++)
/*      */         {
/* 2745 */           RasterMarkerMetadata marker = (RasterMarkerMetadata)markers.get(i);
/* 2746 */           byte[] data = marker.getData();
/* 2747 */           ret = ltfil.InsertMarker(this._hmarkers, i, marker.getId(), data != null ? data.length : 0, data);
/* 2748 */           RasterException.checkErrorCode(ret);
/*      */         }
/*      */ 
/* 2751 */         ret = ltfil.SetMarkers(this._hmarkers, 0);
/* 2752 */         RasterException.checkErrorCode(ret);
/*      */       }
/*      */       catch (RasterException e)
/*      */       {
/* 2756 */         freeMarkers();
/* 2757 */         throw e;
/*      */       }
/*      */     }
/*      */     else {
/* 2761 */       freeMarkers();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void freeMarkers() {
/* 2766 */     this._options.use();
/*      */ 
/* 2768 */     if (this._hmarkers != 0L)
/*      */     {
/* 2770 */       ltfil.FreeMarkers(this._hmarkers);
/* 2771 */       this._hmarkers = 0L;
/*      */     }
/*      */ 
/* 2774 */     ltfil.SetMarkers(0L, 0);
/*      */   }
/*      */ 
/*      */   public void deletePage(ILeadStream stream, int page)
/*      */   {
/* 2779 */     if (stream == null) {
/* 2780 */       throw new ArgumentNullException("stream");
/*      */     }
/* 2782 */     this._options.use();
/*      */ 
/* 2784 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 2785 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 2789 */       String fileName = stream.getFileName();
/* 2790 */       if (ret == L_ERROR.SUCCESS.getValue())
/* 2791 */         ret = ltfil.DeletePage(fileName != null ? fileName : "Stream", page, 0, this._options.getSaveFileOption());
/* 2792 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2796 */       if (stream != null)
/* 2797 */         streamer.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public LeadSize[] readLoadResolutions(ILeadStream stream)
/*      */   {
/* 2803 */     return readLoadResolutions(stream, 1);
/*      */   }
/*      */ 
/*      */   public LeadSize[] readLoadResolutions(ILeadStream stream, int pageNumber)
/*      */   {
/* 2808 */     if (stream == null) {
/* 2809 */       throw new ArgumentNullException("stream");
/*      */     }
/* 2811 */     if (!stream.canSeek()) {
/* 2812 */       throw new IllegalArgumentException("Stream must be seekable");
/*      */     }
/* 2814 */     this._options.use();
/*      */ 
/* 2816 */     int ret = L_ERROR.SUCCESS.getValue();
/* 2817 */     LeadPoint[][] dim = { null };
/*      */ 
/* 2819 */     int oldPage = this._options.getLoadFileOption().PageNumber;
/* 2820 */     this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 2822 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 2823 */     ret = streamer.start();
/* 2824 */     if (ret == L_ERROR.SUCCESS.getValue())
/*      */     {
/*      */       try
/*      */       {
/* 2828 */         String fileName = stream.getFileName();
/* 2829 */         ret = ltfil.ReadLoadResolutions(fileName != null ? fileName : "Stream", dim, this._options.getLoadFileOption());
/* 2830 */         RasterException.checkErrorCode(ret);
/*      */         int count;
/* 2832 */         if (dim[0].length > 0) {
/* 2833 */           count = dim[0].length;
/* 2834 */           LeadSize[] res = new LeadSize[dim[0].length];
/*      */ 
/* 2836 */           for (int i = 0; i < count; i++) {
/* 2837 */             res[i] = new LeadSize(dim[0][i].getX(), dim[0][i].getY());
/*      */           }
/*      */ 
/* 2840 */           return res;
/*      */         }
/*      */ 
/* 2843 */         return null;
/*      */       }
/*      */       finally
/*      */       {
/* 2847 */         if (stream != null) {
/* 2848 */           streamer.dispose();
/*      */         }
/* 2850 */         this._options.getLoadFileOption().PageNumber = oldPage;
/*      */       }
/*      */     }
/* 2853 */     return null;
/*      */   }
/*      */ 
/*      */   public void convert(ILeadStream srcStream, ILeadStream destStream, RasterImageFormat format, int width, int height, int bitsPerPixel, CodecsImageInfo info)
/*      */   {
/* 2859 */     if (srcStream == null) {
/* 2860 */       throw new ArgumentNullException("srcStream");
/*      */     }
/* 2862 */     if (destStream == null) {
/* 2863 */       throw new ArgumentNullException("destStream");
/*      */     }
/* 2865 */     this._options.use();
/* 2866 */     int qFactor = this._options.getRealQualityFactor(format, bitsPerPixel);
/*      */ 
/* 2868 */     long bitmap = 0L;
/* 2869 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */     try
/*      */     {
/* 2873 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 2875 */         CodecsStreamer streamer = new CodecsStreamer(srcStream, false);
/* 2876 */         ret = streamer.start();
/* 2877 */         if (ret == L_ERROR.SUCCESS.getValue())
/*      */         {
/* 2879 */           bitmap = ltkrn.AllocBitmapHandle();
/* 2880 */           if (bitmap == 0L) {
/* 2881 */             throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */           }
/* 2883 */           ret = ltfil.LoadFile(srcStream.getFileName(), bitmap, ltkrn.BITMAPHANDLE_getSizeOf(), 0, CodecsLoadByteOrder.BGR_OR_GRAY.getValue(), 3, null, this._options.getLoadFileOption(), null);
/*      */         }
/*      */       }
/*      */ 
/* 2887 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 2889 */         int bitmapWidth = ltkrn.BITMAPHANDLE_getWidth(bitmap);
/* 2890 */         int bitmapHeight = ltkrn.BITMAPHANDLE_getHeight(bitmap);
/* 2891 */         if ((width != bitmapWidth) || (height != bitmapHeight))
/*      */         {
/* 2893 */           if ((width != 0) && (height != 0)) {
/* 2894 */             ret = ltkrn.SizeBitmap(bitmap, width, height, 0);
/*      */           }
/*      */         }
/*      */       }
/* 2898 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 2900 */         CodecsStreamer streamer = new CodecsStreamer(destStream, false);
/* 2901 */         ret = streamer.start();
/*      */ 
/* 2903 */         int saveFlags = this._options.getSaveFlags();
/* 2904 */         if ((saveFlags & 0x1) == 0)
/*      */         {
/* 2906 */           if ((ltkrn.BITMAPHANDLE_getBitsPerPixel(bitmap) != bitsPerPixel) && (bitsPerPixel == 1))
/* 2907 */             saveFlags |= 1;
/*      */           else {
/* 2909 */             saveFlags |= 2;
/*      */           }
/*      */         }
/* 2912 */         if (ret == L_ERROR.SUCCESS.getValue())
/*      */         {
/* 2914 */           ret = ltfil.SaveFile(destStream.getFileName(), bitmap, format.getValue(), bitsPerPixel, qFactor, saveFlags, this._saveImage.size() > 0 ? this : null, this._options.getSaveFileOption());
/*      */         }
/*      */       }
/* 2917 */       RasterException.checkErrorCode(ret);
/*      */ 
/* 2920 */       if ((bitmap != 0L) && (ltkrn.BITMAPHANDLE_getFlagsAllocated(bitmap))) {
/* 2921 */         ltkrn.FreeBitmap(bitmap);
/*      */       }
/* 2923 */       if (bitmap != 0L)
/* 2924 */         ltkrn.FreeBitmapHandle(bitmap);
/*      */     }
/*      */     finally
/*      */     {
/* 2920 */       if ((bitmap != 0L) && (ltkrn.BITMAPHANDLE_getFlagsAllocated(bitmap))) {
/* 2921 */         ltkrn.FreeBitmap(bitmap);
/*      */       }
/* 2923 */       if (bitmap != 0L)
/* 2924 */         ltkrn.FreeBitmapHandle(bitmap);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void convert(String srcFileName, String destFileName, RasterImageFormat format, int width, int height, int bitsPerPixel, CodecsImageInfo info)
/*      */   {
/* 2958 */     if (srcFileName == null) {
/* 2959 */       throw new ArgumentNullException("srcFileName");
/*      */     }
/* 2961 */     if (destFileName == null) {
/* 2962 */       throw new ArgumentNullException("destFileName");
/*      */     }
/* 2964 */     convert(LeadStreamFactory.create(srcFileName), LeadStreamFactory.create(destFileName), format, width, height, bitsPerPixel, info);
/*      */   }
/*      */ 
/*      */   public RasterCollection<RasterMarkerMetadata> readMarkers(ILeadStream stream)
/*      */   {
/* 2969 */     if (stream == null) {
/* 2970 */       throw new ArgumentNullException("stream");
/*      */     }
/* 2972 */     this._options.use();
/*      */ 
/* 2974 */     long[] hmarkers = new long[1];
/*      */ 
/* 2976 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 2977 */     int ret = streamer.start();
/*      */     try
/*      */     {
/*      */       String fileName;
/* 2981 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 2983 */         fileName = stream.getFileName();
/* 2984 */         ret = ltfil.LoadMarkers(fileName != null ? fileName : "Stream", hmarkers, 0);
/*      */       }
/*      */ 
/* 2987 */       checkExceptions(ret);
/* 2988 */       if (hmarkers[0] == 0L) {
/* 2989 */         return new RasterCollection();
/*      */       }
/* 2991 */       RasterCollection markers = new RasterCollection();
/* 2992 */       this._enumMarkersCollection = markers;
/*      */       try
/*      */       {
/* 2996 */         ret = ltfil.EnumMarkers(hmarkers[0], 0, this);
/* 2997 */         RasterException.checkErrorCode(ret);
/*      */       }
/*      */       finally
/*      */       {
/* 3001 */         this._enumMarkersCollection = null;
/*      */       }
/*      */ 
/* 3004 */       return markers;
/*      */     }
/*      */     finally
/*      */     {
/* 3008 */       if (stream != null) {
/* 3009 */         streamer.dispose();
/*      */       }
/* 3011 */       if (hmarkers[0] != 0L) {
/* 3012 */         ltfil.FreeMarkers(hmarkers[0]);
/*      */       }
/* 3014 */       ltfil.SetMarkers(0L, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   final int EnumMarkersCallback(int uMarker, int uMarkerSize, byte[] pMarkerData, long LEADMarkerCallback, long pLEADUserData)
/*      */   {
/* 3020 */     byte[] data = new byte[uMarkerSize];
/* 3021 */     if (uMarkerSize > 0)
/*      */     {
/* 3023 */       System.arraycopy(pMarkerData, 0, data, 0, uMarkerSize);
/*      */     }
/*      */ 
/* 3026 */     RasterMarkerMetadata marker = new RasterMarkerMetadata(uMarker, data);
/* 3027 */     this._enumMarkersCollection.add(marker);
/*      */ 
/* 3029 */     return L_ERROR.SUCCESS.getValue();
/*      */   }
/*      */ 
/*      */   public RasterImage readThumbnail(ILeadStream stream, CodecsThumbnailOptions options, int pageNumber)
/*      */   {
/* 3034 */     if (stream == null) {
/* 3035 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3037 */     if (options == null) {
/* 3038 */       throw new ArgumentNullException("options");
/*      */     }
/* 3040 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/*      */ 
/* 3042 */     this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 3044 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3045 */     int ret = streamer.start();
/*      */ 
/* 3047 */     RasterImage image = null;
/*      */     try
/*      */     {
/*      */       String fileName;
/* 3050 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 3052 */         fileName = stream.getFileName();
/* 3053 */         ret = ltfil.CreateThumbnailFromFile(fileName != null ? fileName : "Stream", bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), options, null, this._options.getLoadFileOption(), null);
/*      */       }
/*      */ 
/* 3056 */       if (!checkExceptions(ret)) {
/* 3057 */         return null;
/*      */       }
/* 3059 */       if (this._userImage == null)
/*      */       {
/* 3061 */         image = RasterImage.createFromBitmapHandle(bitmapHandle);
/*      */       }
/*      */       else
/*      */       {
/* 3065 */         image = this._userImage;
/* 3066 */         this._userImage = null;
/* 3067 */         image.initFromBitmapHandle(bitmapHandle);
/*      */       }
/*      */ 
/* 3070 */       return image;
/*      */     }
/*      */     finally
/*      */     {
/* 3074 */       if (stream != null)
/* 3075 */         streamer.dispose();
/* 3076 */       if (bitmapHandle != 0L)
/*      */       {
/* 3081 */         ltkrn.FreeBitmapHandle(bitmapHandle);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterImage readStamp(ILeadStream stream, int pageNumber)
/*      */   {
/* 3088 */     if (stream == null) {
/* 3089 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3091 */     this._options.use();
/*      */ 
/* 3093 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/*      */ 
/* 3095 */     this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 3097 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/*      */ 
/* 3099 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 3103 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 3105 */         String fileName = stream.getFileName();
/* 3106 */         ret = ltfil.ReadFileStamp(fileName != null ? fileName : "Stream", bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), this._options.getLoadFileOption());
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3111 */       if (stream != null) {
/* 3112 */         streamer.dispose();
/*      */       }
/*      */     }
/* 3115 */     RasterImage image = null;
/*      */     try
/*      */     {
/*      */       RasterImage localRasterImage1;
/* 3119 */       if ((ret == L_ERROR.ERROR_FEATURE_NOT_SUPPORTED.getValue()) || (ret == L_ERROR.ERROR_NO_STAMP.getValue())) {
/* 3120 */         return null;
/*      */       }
/* 3122 */       if (!checkExceptions(ret)) {
/* 3123 */         return null;
/*      */       }
/* 3125 */       if (this._userImage == null)
/*      */       {
/* 3127 */         image = RasterImage.createFromBitmapHandle(bitmapHandle);
/*      */       }
/*      */       else
/*      */       {
/* 3131 */         image = this._userImage;
/* 3132 */         this._userImage = null;
/* 3133 */         image.initFromBitmapHandle(bitmapHandle);
/*      */       }
/*      */ 
/* 3138 */       if (bitmapHandle != 0L)
/*      */       {
/* 3143 */         ltkrn.FreeBitmapHandle(bitmapHandle);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3138 */       if (bitmapHandle != 0L)
/*      */       {
/* 3143 */         ltkrn.FreeBitmapHandle(bitmapHandle);
/*      */       }
/*      */     }
/* 3146 */     return image;
/*      */   }
/*      */ 
/*      */   public void startOverlay(CodecsOverlayListener listener, CodecsOverlayCallbackMode mode)
/*      */   {
/* 3151 */     this._options.use();
/*      */ 
/* 3153 */     stopOverlay();
/*      */ 
/* 3155 */     Object[] callback = { this };
/* 3156 */     int[] flags = { mode.getValue() };
/*      */ 
/* 3158 */     int ret = ltfil.SetOverlayCallback(callback, flags, true);
/* 3159 */     RasterException.checkErrorCode(ret);
/*      */ 
/* 3161 */     this._oldOverlayCallback = callback[0];
/* 3162 */     this._oldOverlayFlags = flags[0];
/*      */ 
/* 3164 */     this._overlayCallback = listener;
/* 3165 */     this._overlaying = true;
/*      */   }
/*      */ 
/*      */   public void stopOverlay() {
/* 3169 */     this._options.use();
/*      */ 
/* 3171 */     if (this._overlaying)
/*      */     {
/*      */       try
/*      */       {
/* 3175 */         Object[] callback = { this._oldOverlayCallback };
/* 3176 */         int[] flags = { this._oldOverlayFlags };
/*      */ 
/* 3178 */         int ret = ltfil.SetOverlayCallback(callback, flags, false);
/* 3179 */         RasterException.checkErrorCode(ret);
/*      */       }
/*      */       finally
/*      */       {
/* 3183 */         this._overlaying = false;
/* 3184 */         this._overlayCallback = null;
/* 3185 */         this._oldOverlayCallback = null;
/* 3186 */         this._oldOverlayFlags = 0;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   final int OverlayCallback(CodecsOverlayData data)
/*      */   {
/* 3193 */     if (this._overlayCallback != null)
/*      */     {
/* 3196 */       this._overlayCallback.onOverlay(data);
/*      */     }
/*      */ 
/* 3209 */     return L_ERROR.SUCCESS.getValue();
/*      */   }
/*      */ 
/*      */   public synchronized void addTagFoundListener(CodecsTagFoundListener listener)
/*      */   {
/* 3220 */     if (this._TagFound.contains(listener)) {
/* 3221 */       return;
/*      */     }
/* 3223 */     this._TagFound.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeTagFoundListener(CodecsTagFoundListener listener) {
/* 3227 */     if (!this._TagFound.contains(listener)) {
/* 3228 */       return;
/*      */     }
/* 3230 */     this._TagFound.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireTagFound(CodecsEnumTagsEvent event) {
/* 3234 */     for (CodecsTagFoundListener listener : this._TagFound)
/* 3235 */       listener.onTagFound(event);
/*      */   }
/*      */ 
/*      */   public void enumTags(ILeadStream stream, int pageNumber)
/*      */   {
/* 3240 */     if (stream == null) {
/* 3241 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3243 */     this._options.use();
/*      */ 
/* 3245 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3246 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 3250 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 3252 */         String fileName = stream.getFileName();
/* 3253 */         this._options.getLoadFileOption().PageNumber = pageNumber;
/* 3254 */         ret = ltfil.EnumFileTags(fileName != null ? fileName : "Stream", 0, this, this._options.getLoadFileOption());
/*      */       }
/* 3256 */       if (ret != L_ERROR.ERROR_USER_ABORT.getValue())
/* 3257 */         RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 3261 */       if (stream != null)
/* 3262 */         streamer.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   final int EnumTagsCallback(short uTag, short uType, int uCount)
/*      */   {
/* 3268 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/* 3270 */     this._enumTagsEventArgs.init(uTag, uType, uCount);
/*      */ 
/* 3272 */     fireTagFound(this._enumTagsEventArgs);
/* 3273 */     if (this._enumTagsEventArgs.getCancel()) {
/* 3274 */       ret = L_ERROR.ERROR_USER_ABORT.getValue();
/*      */     }
/* 3276 */     return ret;
/*      */   }
/*      */ 
/*      */   public void extractXMPMetadata(ILeadStream sourceStream, ILeadStream destStream)
/*      */   {
/* 3281 */     if (sourceStream == null) {
/* 3282 */       throw new ArgumentNullException("sourceStream");
/*      */     }
/* 3284 */     if (destStream == null)
/* 3285 */       throw new ArgumentNullException("destStream");
/*      */   }
/*      */ 
/*      */   public synchronized void addGeoKeyFoundListener(CodecsGeoKeyFoundListener listener)
/*      */   {
/* 3300 */     if (this._GeoKeyFound.contains(listener)) {
/* 3301 */       return;
/*      */     }
/* 3303 */     this._GeoKeyFound.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeGeoKeyFoundListener(CodecsGeoKeyFoundListener listener) {
/* 3307 */     if (!this._GeoKeyFound.contains(listener)) {
/* 3308 */       return;
/*      */     }
/* 3310 */     this._GeoKeyFound.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireGeoKeyFound(CodecsEnumGeoKeysEvent event) {
/* 3314 */     for (CodecsGeoKeyFoundListener listener : this._GeoKeyFound)
/* 3315 */       listener.onGeoKeyFound(this._enumGeoKeysEventArgs);
/*      */   }
/*      */ 
/*      */   public void enumGeoKeys(ILeadStream stream, int pageNumber)
/*      */   {
/* 3320 */     if (stream == null) {
/* 3321 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3323 */     this._options.use();
/*      */ 
/* 3325 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3326 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 3329 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 3331 */         String fileName = stream.getFileName();
/*      */ 
/* 3333 */         this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 3335 */         ret = ltfil.EnumFileGeoKeys(fileName != null ? fileName : "Stream", 0, this, this._options.getLoadFileOption());
/*      */       }
/* 3337 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 3341 */       if (stream != null)
/* 3342 */         streamer.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   final int EnumGeoKeysCallback(short uTag, short uType, int uCount, byte[] pData) {
/* 3347 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/* 3349 */     int dataLength = uCount;
/* 3350 */     switch (uType)
/*      */     {
/*      */     case 3:
/* 3353 */       dataLength *= 2;
/* 3354 */       break;
/*      */     case 12:
/* 3357 */       dataLength *= 8;
/* 3358 */       break;
/*      */     case 2:
/*      */     }
/*      */ 
/* 3366 */     this._enumGeoKeysEventArgs.init(uTag, uType, uCount, pData, dataLength);
/*      */ 
/* 3368 */     fireGeoKeyFound(this._enumGeoKeysEventArgs);
/* 3369 */     if (this._enumGeoKeysEventArgs.getCancel()) {
/* 3370 */       ret = L_ERROR.ERROR_USER_ABORT.getValue();
/*      */     }
/* 3372 */     return ret;
/*      */   }
/*      */ 
/*      */   public void deleteTag(ILeadStream stream, int pageNumber, int id)
/*      */   {
/* 3377 */     if (stream == null) {
/* 3378 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3380 */     this._options.use();
/*      */ 
/* 3382 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3383 */     int ret = streamer.start();
/* 3384 */     if (ret == L_ERROR.SUCCESS.getValue())
/*      */     {
/*      */       try
/*      */       {
/* 3388 */         this._options.getSaveFileOption().PageNumber = pageNumber;
/* 3389 */         ret = ltfil.DeleteTag(stream.getFileName() != null ? stream.getFileName() : "Stream", pageNumber, id, 0, this._options.getSaveFileOption());
/* 3390 */         RasterException.checkErrorCode(ret);
/*      */       }
/*      */       finally
/*      */       {
/* 3394 */         if (stream != null)
/* 3395 */           streamer.dispose();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterTagMetadata readTag(ILeadStream stream, int pageNumber, int id)
/*      */   {
/* 3402 */     if (stream == null) {
/* 3403 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3405 */     this._options.use();
/*      */ 
/* 3407 */     this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 3409 */     int[] params = { 0, 0 };
/* 3410 */     byte[][] data = { null };
/*      */ 
/* 3412 */     CodecsStreamer streamer = new CodecsStreamer(stream, true);
/* 3413 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 3416 */       if (ret == L_ERROR.SUCCESS.getValue())
/* 3417 */         ret = ltfil.ReadFileTag(stream.getFileName() != null ? stream.getFileName() : "Stream", (short)id, params, data, this._options.getLoadFileOption());
/*      */       RasterTagMetadata localRasterTagMetadata;
/* 3419 */       if ((ret == L_ERROR.ERROR_FEATURE_NOT_SUPPORTED.getValue()) || (ret == L_ERROR.ERROR_TAG_MISSING.getValue())) {
/* 3420 */         return null;
/*      */       }
/* 3422 */       if (ret < 0)
/*      */       {
/* 3424 */         if (!checkExceptions(ret))
/* 3425 */           return null;
/*      */       }
/* 3427 */       return new RasterTagMetadata(id, RasterTagMetadataDataType.forValue(params[0]), data[0]);
/*      */     }
/*      */     finally
/*      */     {
/* 3431 */       if (stream != null) {
/* 3432 */         streamer.dispose();
/*      */       }
/* 3434 */       this._options.getLoadFileOption().PageNumber = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterCollection<RasterTagMetadata> readTags(ILeadStream stream, int pageNumber)
/*      */   {
/* 3440 */     if (stream == null) {
/* 3441 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3443 */     this._options.use();
/*      */ 
/* 3445 */     int ret = L_ERROR.SUCCESS.getValue();
/* 3446 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3447 */     ret = streamer.start();
/*      */     try
/*      */     {
/* 3451 */       this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 3453 */       int[] uTagCount = { 0 };
/* 3454 */       LEADFILETAG[][] pTags = { null };
/* 3455 */       long[] uDataSize = { 0L };
/* 3456 */       byte[][] pData = { null };
/*      */       try
/*      */       {
/* 3461 */         if (ret == L_ERROR.SUCCESS.getValue()) {
/* 3462 */           ret = ltfil.ReadFileTags(stream.getFileName() != null ? stream.getFileName() : "Stream", 0, uTagCount, pTags, uDataSize, pData, this._options.getLoadFileOption());
/*      */         }
/* 3464 */         checkExceptions(ret);
/*      */ 
/* 3466 */         RasterCollection tags = new RasterCollection();
/*      */ 
/* 3468 */         for (int tagIndex = 0; tagIndex < uTagCount[0]; tagIndex++)
/*      */         {
/* 3470 */           LEADFILETAG pTag = pTags[0][tagIndex];
/*      */ 
/* 3472 */           byte[] data = new byte[pTag.uDataSize];
/* 3473 */           System.arraycopy(pData[0], pTag.uDataOffset, data, 0, pTag.uDataSize);
/*      */ 
/* 3475 */           RasterTagMetadata tag = new RasterTagMetadata(pTag.uTag, RasterTagMetadataDataType.forValue(pTag.uType), data);
/*      */ 
/* 3477 */           tags.add(tag);
/*      */         }
/*      */ 
/* 3480 */         tagIndex = tags;
/*      */ 
/* 3484 */         this._options.getLoadFileOption().PageNumber = 0;
/*      */ 
/* 3490 */         return tagIndex;
/*      */       }
/*      */       finally
/*      */       {
/* 3484 */         this._options.getLoadFileOption().PageNumber = 0;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3489 */       if (stream != null)
/* 3490 */         streamer.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterCollection<RasterTagMetadata> readTagsWithOffsets(ILeadStream stream, int pageNumber, long[][] offsets)
/*      */   {
/* 3496 */     if (stream == null) {
/* 3497 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3499 */     this._options.use();
/*      */ 
/* 3501 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3502 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 3505 */       this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 3507 */       int[] uTagCount = { 0 };
/* 3508 */       LEADFILETAG[][] pTags = { null };
/*      */       try
/*      */       {
/* 3512 */         if (ret == L_ERROR.SUCCESS.getValue()) {
/* 3513 */           ret = ltfil.ReadFileTags(stream.getFileName() != null ? stream.getFileName() : "Stream", 1, uTagCount, pTags, null, (byte[][])null, this._options.getLoadFileOption());
/*      */         }
/* 3515 */         checkExceptions(ret);
/*      */ 
/* 3517 */         RasterCollection tags = new RasterCollection();
/* 3518 */         offsets[0] = new long[uTagCount[0]];
/*      */ 
/* 3520 */         for (int tagIndex = 0; tagIndex < uTagCount[0]; tagIndex++)
/*      */         {
/* 3522 */           LEADFILETAG pTag = pTags[0][tagIndex];
/*      */ 
/* 3524 */           RasterTagMetadata tag = new RasterTagMetadata(pTag.uTag, RasterTagMetadataDataType.forValue(pTag.uType), null);
/*      */ 
/* 3526 */           tags.add(tag);
/* 3527 */           offsets[0][tagIndex] = pTag.uDataOffset;
/*      */         }
/*      */ 
/* 3530 */         tagIndex = tags;
/*      */ 
/* 3534 */         this._options.getLoadFileOption().PageNumber = 0;
/*      */ 
/* 3540 */         return tagIndex;
/*      */       }
/*      */       finally
/*      */       {
/* 3534 */         this._options.getLoadFileOption().PageNumber = 0;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3539 */       if (stream != null)
/* 3540 */         streamer.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterTagMetadata readGeoKey(ILeadStream stream, int pageNumber, int id)
/*      */   {
/* 3546 */     if (stream == null) {
/* 3547 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3549 */     this._options.use();
/*      */ 
/* 3551 */     this._options.getLoadFileOption().PageNumber = pageNumber;
/* 3552 */     int[] params = { 0, 0 };
/* 3553 */     byte[][] data = { null };
/*      */ 
/* 3556 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3557 */     int ret = streamer.start();
/*      */     try
/*      */     {
/*      */       String fileName;
/* 3561 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 3563 */         fileName = stream.getFileName();
/* 3564 */         ret = ltfil.ReadFileGeoKey(fileName != null ? fileName : "Stream", (short)id, params, data, this._options.getLoadFileOption());
/*      */       }
/*      */ 
/* 3567 */       if ((ret == L_ERROR.ERROR_FEATURE_NOT_SUPPORTED.getValue()) || (ret == L_ERROR.ERROR_TAG_MISSING.getValue())) {
/* 3568 */         return null;
/*      */       }
/* 3570 */       if (ret < 0)
/*      */       {
/* 3572 */         if (!checkExceptions(ret)) {
/* 3573 */           return null;
/*      */         }
/*      */       }
/* 3576 */       return new RasterTagMetadata(id, RasterTagMetadataDataType.forValue(params[0]), data[0]);
/*      */     }
/*      */     finally
/*      */     {
/* 3580 */       if (stream != null) {
/* 3581 */         streamer.dispose();
/*      */       }
/* 3583 */       this._options.getLoadFileOption().PageNumber = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterCollection<RasterTagMetadata> readGeoKeys(ILeadStream stream, int pageNumber)
/*      */   {
/* 3589 */     if (stream == null) {
/* 3590 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3592 */     this._options.use();
/*      */ 
/* 3594 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3595 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 3599 */       this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 3601 */       int[] uTagCount = { 0 };
/* 3602 */       LEADFILETAG[][] pTags = { null };
/* 3603 */       byte[][] pData = { null };
/* 3604 */       long[] uDataSize = { 0L };
/*      */       try
/*      */       {
/* 3608 */         if (ret == L_ERROR.SUCCESS.getValue())
/*      */         {
/* 3610 */           String fileName = stream.getFileName();
/* 3611 */           ret = ltfil.ReadFileGeoKeys(fileName != null ? fileName : "Stream", 0, uTagCount, pTags, uDataSize, pData, this._options.getLoadFileOption());
/*      */         }
/* 3613 */         checkExceptions(ret);
/*      */ 
/* 3615 */         RasterCollection geokeys = new RasterCollection();
/*      */ 
/* 3617 */         for (int tagIndex = 0; tagIndex < uTagCount[0]; tagIndex++)
/*      */         {
/* 3619 */           LEADFILETAG pTag = pTags[0][tagIndex];
/*      */ 
/* 3621 */           byte[] data = new byte[pTag.uDataSize];
/* 3622 */           System.arraycopy(pData[0], pTag.uDataOffset, data, 0, pTag.uDataSize);
/*      */ 
/* 3624 */           RasterTagMetadata geokey = new RasterTagMetadata(pTag.uTag, RasterTagMetadataDataType.forValue(pTag.uType), data);
/*      */ 
/* 3626 */           geokeys.add(geokey);
/*      */         }
/*      */ 
/* 3629 */         tagIndex = geokeys;
/*      */ 
/* 3633 */         this._options.getLoadFileOption().PageNumber = 0;
/*      */ 
/* 3639 */         return tagIndex;
/*      */       }
/*      */       finally
/*      */       {
/* 3633 */         this._options.getLoadFileOption().PageNumber = 0;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3638 */       if (stream != null)
/* 3639 */         streamer.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeTag(ILeadStream stream, int pageNumber, RasterTagMetadata tag)
/*      */   {
/* 3645 */     if (stream == null) {
/* 3646 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3648 */     RasterCollection tags = new RasterCollection();
/* 3649 */     tags.add(tag);
/* 3650 */     writeTags(stream, pageNumber, tags);
/*      */   }
/*      */ 
/*      */   public void writeTags(ILeadStream stream, int pageNumber, RasterCollection<RasterTagMetadata> tags)
/*      */   {
/* 3655 */     if (stream == null) {
/* 3656 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3658 */     this._options.use();
/*      */ 
/* 3660 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3661 */     int ret = streamer.start();
/* 3662 */     setTags(tags);
/*      */     try
/*      */     {
/* 3666 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 3668 */         this._options.getSaveFileOption().PageNumber = pageNumber;
/* 3669 */         ret = ltfil.WriteFileTag(stream.getFileName() != null ? stream.getFileName() : "Stream", this._options.getSaveFileOption());
/*      */       }
/*      */ 
/* 3672 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 3676 */       if (stream != null) {
/* 3677 */         streamer.dispose();
/*      */       }
/* 3679 */       this._options.getSaveFileOption().PageNumber = 0;
/* 3680 */       setTags(null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeGeoKey(ILeadStream stream, int pageNumber, RasterTagMetadata geoKey)
/*      */   {
/* 3686 */     if (stream == null) {
/* 3687 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3689 */     RasterCollection geoKeys = new RasterCollection();
/* 3690 */     geoKeys.add(geoKey);
/* 3691 */     writeGeoKeys(stream, pageNumber, geoKeys);
/*      */   }
/*      */ 
/*      */   public void writeGeoKeys(ILeadStream stream, int pageNumber, RasterCollection<RasterTagMetadata> geoKeys)
/*      */   {
/* 3696 */     if (stream == null) {
/* 3697 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3699 */     this._options.use();
/*      */ 
/* 3701 */     setGeoKeys(geoKeys);
/*      */ 
/* 3703 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3704 */     int ret = streamer.start();
/* 3705 */     if (ret == L_ERROR.SUCCESS.getValue())
/*      */       try
/*      */       {
/* 3708 */         this._options.getSaveFileOption().PageNumber = pageNumber;
/* 3709 */         ret = ltfil.WriteFileGeoKey(stream.getFileName(), this._options.getSaveFileOption());
/* 3710 */         RasterException.checkErrorCode(ret);
/*      */       } finally {
/* 3712 */         if (stream != null) {
/* 3713 */           streamer.dispose();
/*      */         }
/* 3715 */         this._options.getSaveFileOption().PageNumber = 0;
/* 3716 */         setGeoKeys(null);
/*      */       }
/*      */   }
/*      */ 
/*      */   public RasterCommentMetadata readComment(String fileName, int pageNumber, RasterCommentMetadataType type)
/*      */   {
/* 3723 */     return readComment(LeadStreamFactory.create(fileName), pageNumber, type);
/*      */   }
/*      */ 
/*      */   public RasterCommentMetadata readComment(ILeadStream stream, int pageNumber, RasterCommentMetadataType type)
/*      */   {
/* 3728 */     if (stream == null) {
/* 3729 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3731 */     this._options.use();
/* 3732 */     this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 3735 */     CodecsStreamer streamer = new CodecsStreamer(stream, true);
/* 3736 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 3740 */       int[] size = { 0 };
/* 3741 */       byte[][] data = { null };
/*      */       String fileName;
/* 3743 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 3745 */         fileName = stream.getFileName();
/* 3746 */         ret = ltfil.ReadFileComment(fileName != null ? fileName : "Stream", (short)type.getValue(), size, data, this._options.getLoadFileOption());
/*      */       }
/*      */ 
/* 3749 */       if ((ret == L_ERROR.ERROR_FEATURE_NOT_SUPPORTED.getValue()) || (ret == L_ERROR.ERROR_TAG_MISSING.getValue()) || (ret == L_ERROR.ERROR_INV_PARAMETER.getValue())) {
/* 3750 */         return null;
/*      */       }
/* 3752 */       if (ret < 0)
/*      */       {
/* 3754 */         if (!checkExceptions(ret))
/* 3755 */           return null;
/*      */       }
/* 3757 */       return new RasterCommentMetadata(type, data[0]);
/*      */     }
/*      */     finally
/*      */     {
/* 3761 */       if (stream != null) {
/* 3762 */         streamer.dispose();
/*      */       }
/* 3764 */       this._options.getLoadFileOption().PageNumber = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterCollection<RasterCommentMetadata> readComments(ILeadStream stream, int pageNumber)
/*      */   {
/* 3770 */     if (stream == null) {
/* 3771 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3773 */     this._options.use();
/*      */ 
/* 3775 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3776 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 3780 */       this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 3782 */       int[] uCommentCount = { 0 };
/* 3783 */       LEADFILECOMMENT[][] pComments = { null };
/* 3784 */       byte[][] pData = { null };
/*      */       try
/*      */       {
/* 3788 */         if (ret == L_ERROR.SUCCESS.getValue())
/*      */         {
/* 3790 */           String fileName = stream.getFileName();
/* 3791 */           ret = ltfil.ReadFileComments(fileName != null ? fileName : "Stream", 0, uCommentCount, pComments, pData, this._options.getLoadFileOption());
/*      */         }
/*      */ 
/* 3794 */         checkExceptions(ret);
/*      */ 
/* 3796 */         RasterCollection comments = new RasterCollection();
/*      */ 
/* 3798 */         for (int commentIndex = 0; commentIndex < uCommentCount[0]; commentIndex++)
/*      */         {
/* 3800 */           LEADFILECOMMENT pComment = pComments[0][commentIndex];
/*      */ 
/* 3802 */           byte[] data = new byte[pComment.uDataSize];
/* 3803 */           System.arraycopy(pData[0], pComment.uDataOffset, data, 0, pComment.uDataSize);
/*      */ 
/* 3805 */           RasterCommentMetadata comment = new RasterCommentMetadata(RasterCommentMetadataType.forValue(pComment.uType), data);
/*      */ 
/* 3807 */           comments.add(comment);
/*      */         }
/*      */ 
/* 3810 */         commentIndex = comments;
/*      */ 
/* 3814 */         this._options.getLoadFileOption().PageNumber = 0;
/*      */ 
/* 3820 */         return commentIndex;
/*      */       }
/*      */       finally
/*      */       {
/* 3814 */         this._options.getLoadFileOption().PageNumber = 0;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3819 */       if (stream != null)
/* 3820 */         streamer.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeComment(ILeadStream stream, int pageNumber, RasterCommentMetadata comment)
/*      */   {
/* 3826 */     if (stream == null) {
/* 3827 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3829 */     RasterCollection comments = new RasterCollection();
/* 3830 */     comments.add(comment);
/* 3831 */     writeComments(stream, pageNumber, comments);
/*      */   }
/*      */ 
/*      */   public void writeComments(ILeadStream stream, int pageNumber, RasterCollection<RasterCommentMetadata> comments)
/*      */   {
/* 3836 */     if (stream == null) {
/* 3837 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3839 */     this._options.use();
/*      */ 
/* 3841 */     setComments(comments);
/*      */ 
/* 3843 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3844 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 3848 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 3850 */         this._options.getSaveFileOption().PageNumber = pageNumber;
/* 3851 */         String fileName = stream.getFileName();
/* 3852 */         ret = ltfil.WriteFileComment(fileName != null ? fileName : "Stream", this._options.getSaveFileOption());
/*      */       }
/* 3854 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 3858 */       if (stream != null) {
/* 3859 */         streamer.dispose();
/*      */       }
/* 3861 */       this._options.getSaveFileOption().PageNumber = 0;
/* 3862 */       setComments(null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeMarker(ILeadStream stream, int pageNumber, RasterMarkerMetadata marker)
/*      */   {
/* 3868 */     if (stream == null) {
/* 3869 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3871 */     RasterCollection markers = new RasterCollection();
/* 3872 */     markers.add(marker);
/* 3873 */     writeMarkers(stream, pageNumber, markers);
/*      */   }
/*      */ 
/*      */   public void writeMarkers(ILeadStream stream, int pageNumber, RasterCollection<RasterMarkerMetadata> markers)
/*      */   {
/* 3878 */     if (stream == null) {
/* 3879 */       throw new ArgumentNullException("stream");
/*      */     }
/* 3881 */     this._options.use();
/*      */ 
/* 3883 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 3884 */     int ret = streamer.start();
/*      */ 
/* 3886 */     setMarkers(markers);
/*      */     try
/*      */     {
/* 3890 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 3892 */         this._options.getSaveFileOption().PageNumber = pageNumber;
/* 3893 */         String fileName = stream.getFileName();
/* 3894 */         ret = ltfil.WriteFileMetaData(fileName != null ? fileName : "Stream", 4, this._options.getSaveFileOption());
/*      */       }
/*      */ 
/* 3897 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 3901 */       if (stream != null) {
/* 3902 */         streamer.dispose();
/*      */       }
/* 3904 */       this._options.getSaveFileOption().PageNumber = 0;
/* 3905 */       setMarkers(null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void transform(String srcFileName, String destFileName, CodecsTransformFlags flags, int pageNumber, CodecsTransformMarkerListener callback)
/*      */   {
/* 3917 */     if (srcFileName == null) {
/* 3918 */       throw new ArgumentNullException("srcFileName");
/*      */     }
/* 3920 */     if (destFileName == null) {
/* 3921 */       throw new ArgumentNullException("destFileName");
/*      */     }
/* 3923 */     this._options.use();
/*      */ 
/* 3925 */     this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 3927 */     this._transformMarkerCallback = callback;
/* 3928 */     this._leadWriteMarkerCallback = 0;
/* 3929 */     this._leadWriteMarkerCallbackData = 0;
/*      */     try
/*      */     {
/* 3933 */       int ret = ltfil.TransformFile(srcFileName, destFileName, flags.getValue(), this._transformMarkerCallback != null ? this : null, this._options.getLoadFileOption());
/* 3934 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 3938 */       this._options.getLoadFileOption().PageNumber = 0;
/* 3939 */       this._transformMarkerCallback = null;
/* 3940 */       this._leadWriteMarkerCallback = 0;
/* 3941 */       this._leadWriteMarkerCallbackData = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeTransformMarker(int id, byte[] data, int dataOffset, int dataLength)
/*      */   {
/* 3947 */     if (this._leadWriteMarkerCallback == 0) {
/* 3948 */       throw new InvalidOperationException("This method must be called from within the transform marker callback");
/*      */     }
/* 3950 */     this._options.use();
/*      */ 
/* 3952 */     int ret = ltfil.CallLEADTransformMarkerCallback(id, dataLength, data, dataOffset, this._leadWriteMarkerCallback, this._leadWriteMarkerCallbackData);
/* 3953 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   final int TransformFileCallback(int uMarker, int uMarkerSize, byte[] pMarkerData, int uTransform, int pfnLEADCallback, int pLEADCallbackUserData)
/*      */   {
/* 3958 */     this._leadWriteMarkerCallback = pfnLEADCallback;
/* 3959 */     this._leadWriteMarkerCallbackData = pLEADCallbackUserData;
/*      */ 
/* 3961 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/* 3963 */     if (this._transformMarkerCallback != null)
/*      */     {
/* 3965 */       CodecsTransformMarkerAction action = this._transformMarkerCallback.OnCodecsTransformMarkerCallback(uMarker, new RasterNativeBuffer(pMarkerData, uMarkerSize), CodecsTransformFlags.forValue(uTransform));
/* 3966 */       this._leadWriteMarkerCallback = 0;
/* 3967 */       this._leadWriteMarkerCallbackData = 0;
/*      */ 
/* 3969 */       switch (1.$SwitchMap$leadtools$codecs$CodecsTransformMarkerAction[action.ordinal()])
/*      */       {
/*      */       case 1:
/* 3972 */         ret = L_ERROR.SUCCESS_IGNOREMARKER.getValue();
/* 3973 */         break;
/*      */       case 2:
/* 3976 */         ret = L_ERROR.ERROR_USER_ABORT.getValue();
/* 3977 */         break;
/*      */       case 3:
/*      */       default:
/* 3981 */         ret = L_ERROR.SUCCESS.getValue();
/*      */       }
/*      */     }
/*      */ 
/* 3985 */     return ret;
/*      */   }
/*      */ 
/*      */   public void startCompress(int width, int height, int bitsPerPixel, RasterByteOrder order, RasterViewPerspective viewPerspective, int inputDataLength, byte[] outputData, int outputDataIndex, int outputDataLength, CodecsCompression compression, CodecsCompressDataListener callback)
/*      */   {
/* 3990 */     this._options.use();
/*      */ 
/* 3992 */     stopCompress();
/*      */ 
/* 3994 */     RasterImageFormat format = compression == CodecsCompression.CMP ? RasterImageFormat.CMP : RasterImageFormat.JPEG;
/* 3995 */     int qFactor = this._options.getRealQualityFactor(format, bitsPerPixel);
/*      */ 
/* 3997 */     if (callback != null) {
/* 3998 */       this._compressDataCallback = callback;
/*      */     }
/* 4000 */     this._compressDataBitmapHandle = ltkrn.AllocBitmapHandle();
/* 4001 */     if (this._compressDataBitmapHandle == 0L) {
/* 4002 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     }
/* 4004 */     int ret = ltkrn.InitBitmap(this._compressDataBitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), width, height, bitsPerPixel);
/* 4005 */     RasterException.checkErrorCode(ret);
/*      */ 
/* 4009 */     ltkrn.BITMAPHANDLE_setOrder(this._compressDataBitmapHandle, order.getValue());
/* 4010 */     ltkrn.BITMAPHANDLE_setViewPerspective(this._compressDataBitmapHandle, viewPerspective.getValue());
/*      */ 
/* 4012 */     long[] handle = { 0L };
/* 4013 */     ret = ltfil.StartCompressBuffer(handle, this._compressDataBitmapHandle, callback != null ? this : null, inputDataLength, outputDataLength, outputData, compression.getValue(), qFactor, this._options.getSaveFileOption());
/* 4014 */     if (ret != L_ERROR.SUCCESS.getValue())
/* 4015 */       stopCompress();
/*      */     else {
/* 4017 */       this._compressDataHandle = handle[0];
/*      */     }
/* 4019 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   public void compress(byte[] data, int dataIndex)
/*      */   {
/* 4024 */     if (this._compressDataBitmapHandle == 0L) {
/* 4025 */       throw new InvalidOperationException("You must call StartCompress before calling this method");
/*      */     }
/* 4027 */     this._options.use();
/*      */ 
/* 4029 */     int ret = ltfil.CompressBuffer(data, dataIndex);
/* 4030 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   public void stopCompress()
/*      */   {
/*      */     try {
/* 4036 */       if (this._compressDataHandle != 0L)
/*      */       {
/* 4038 */         this._options.use();
/* 4039 */         int ret = ltfil.EndCompressBuffer(this._compressDataHandle);
/* 4040 */         RasterException.checkErrorCode(ret);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 4045 */       this._compressDataCallback = null;
/* 4046 */       this._compressDataHandle = 0L;
/*      */ 
/* 4048 */       if (this._compressDataBitmapHandle != 0L)
/*      */       {
/* 4050 */         ltkrn.FreeBitmapHandle(this._compressDataBitmapHandle);
/* 4051 */         this._compressDataBitmapHandle = 0L;
/*      */       }
/*      */ 
/* 4054 */       if (this._compressDecompressThunk != null)
/*      */       {
/* 4056 */         this._compressDecompressThunk = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   final int CompBuffCallback(long pBitmap, byte[] pBuffer, int uBytes) {
/* 4062 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/* 4064 */     if (this._compressDataCallback != null)
/*      */     {
/* 4066 */       if (!this._compressDataCallback.OnCodecsCompressDataCallback(ltkrn.BITMAPHANDLE_getWidth(this._compressDataBitmapHandle), ltkrn.BITMAPHANDLE_getHeight(this._compressDataBitmapHandle), ltkrn.BITMAPHANDLE_getBitsPerPixel(this._compressDataBitmapHandle), RasterByteOrder.forValue(ltkrn.BITMAPHANDLE_getOrder(this._compressDataBitmapHandle)), RasterViewPerspective.forValue(ltkrn.BITMAPHANDLE_getViewPerspective(this._compressDataBitmapHandle)), new RasterNativeBuffer(pBuffer, uBytes))) {
/* 4067 */         ret = L_ERROR.ERROR_USER_ABORT.getValue();
/*      */       }
/*      */     }
/* 4070 */     return ret;
/*      */   }
/*      */ 
/*      */   public Object startDecompress(CodecsStartDecompressOptions options)
/*      */   {
/* 4087 */     this._options.use();
/*      */ 
/* 4089 */     options.setBitmapHandle(ltkrn.AllocBitmapHandle());
/* 4090 */     if (options.getBitmapHandle() == 0L) {
/* 4091 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     }
/* 4093 */     long[] hdecompress = { 0L };
/*      */     try
/*      */     {
/* 4096 */       int ret = ltfil.StartDecompressBuffer(hdecompress, options);
/* 4097 */       RasterException.checkErrorCode(ret);
/*      */ 
/* 4099 */       DecompressData dd = new DecompressData(hdecompress[0], options.getBitmapHandle());
/* 4100 */       return dd;
/*      */     }
/*      */     catch (RasterException e)
/*      */     {
/* 4104 */       ltkrn.FreeBitmapHandle(options.getBitmapHandle());
/* 4105 */       throw e;
/*      */     }
/*      */     finally
/*      */     {
/* 4109 */       options.setBitmapHandle(0L);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void decompress(Object decompressContext, byte[] data, int dataOffset, int dataLength, int width, int height, int row, int column, CodecsDecompressDataFlags flags)
/*      */   {
/* 4115 */     DecompressData dd = (DecompressData)((decompressContext instanceof DecompressData) ? decompressContext : null);
/* 4116 */     if (dd == null) {
/* 4117 */       throw new InvalidOperationException("Invalid decompressContext object");
/*      */     }
/* 4119 */     this._options.use();
/*      */ 
/* 4121 */     int ret = ltfil.DecompressBuffer(dd.DecompressHandle, data, width, height, dataOffset, dataLength, row, column, flags.getValue());
/* 4122 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   public RasterImage stopDecompress(Object decompressContext)
/*      */   {
/* 4127 */     DecompressData dd = (DecompressData)((decompressContext instanceof DecompressData) ? decompressContext : null);
/* 4128 */     if (dd == null) {
/* 4129 */       throw new InvalidOperationException("Invalid decompressContext object");
/*      */     }
/* 4131 */     this._options.use();
/*      */     try
/*      */     {
/* 4135 */       int ret = ltfil.StopDecompressBuffer(dd.DecompressHandle);
/* 4136 */       RasterException.checkErrorCode(ret);
/*      */       RasterImage image;
/*      */       RasterImage image;
/* 4140 */       if (this._userImage == null)
/*      */       {
/* 4142 */         image = RasterImage.createFromBitmapHandle(dd.BitmapHandle);
/*      */       }
/*      */       else
/*      */       {
/* 4146 */         image = this._userImage;
/* 4147 */         this._userImage = null;
/* 4148 */         image.initFromBitmapHandle(dd.BitmapHandle);
/*      */       }
/*      */ 
/* 4151 */       return image;
/*      */     }
/*      */     finally
/*      */     {
/* 4155 */       if (dd.BitmapHandle != 0L)
/*      */       {
/* 4157 */         ltkrn.FreeBitmapHandle(dd.BitmapHandle);
/* 4158 */         dd.BitmapHandle = 0L;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterExceptionCode getLoadStatus()
/*      */   {
/* 4165 */     int ret = ltfil.GetLoadStatus();
/* 4166 */     return RasterExceptionCode.forValue(ret);
/*      */   }
/*      */ 
/*      */   public RasterImage loadCmykPlanes(ILeadStream stream, int bitsPerPixel, int pageNumber)
/*      */   {
/* 4171 */     if (stream == null) {
/* 4172 */       throw new ArgumentNullException("stream");
/*      */     }
/* 4174 */     int bitmapCount = 5;
/* 4175 */     long[] bitmapHandle = new long[5];
/*      */     try
/*      */     {
/* 4179 */       for (int i = 0; i < 5; i++)
/*      */       {
/* 4181 */         bitmapHandle[i] = ltkrn.AllocBitmapHandle();
/* 4182 */         if (bitmapHandle[i] == 0L) {
/* 4183 */           throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */         }
/*      */       }
/* 4186 */       CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 4187 */       int ret = streamer.start();
/*      */ 
/* 4189 */       this._options.use();
/* 4190 */       this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 4192 */       int flags = 0;
/*      */ 
/* 4194 */       if (this._options.getLoad().getAllocateImage())
/* 4195 */         flags |= 1;
/* 4196 */       if (this._options.getLoad().getStoreDataInImage())
/* 4197 */         flags |= 2;
/* 4198 */       if (this._options.getLoad().getSuperCompressed()) {
/* 4199 */         flags |= 128;
/*      */       }
/*      */       try
/*      */       {
/* 4203 */         if (ret == L_ERROR.SUCCESS.getValue())
/*      */         {
/* 4205 */           String fileName = stream.getFileName();
/*      */ 
/* 4207 */           ret = ltfil.LoadFileCMYKArray(fileName != null ? fileName : "Stream", bitmapHandle, bitmapHandle.length, bitsPerPixel, flags, this._loadImage.size() > 0 ? this : null, this._options.getLoadFileOption(), null);
/*      */         }
/*      */ 
/* 4210 */         RasterException.checkErrorCode(ret);
/*      */       }
/*      */       finally
/*      */       {
/* 4214 */         if (stream != null) {
/* 4215 */           streamer.dispose();
/*      */         }
/*      */       }
/* 4218 */       RasterImage image = null;
/* 4219 */       for (int i = 0; i < 5; i++)
/*      */       {
/* 4221 */         if (ltkrn.BITMAPHANDLE_getFlagsAllocated(bitmapHandle[i]))
/*      */         {
/* 4223 */           boolean freeTempImage = false;
/* 4224 */           RasterImage tempImage = null;
/* 4225 */           if (this._userImage == null)
/*      */           {
/* 4227 */             tempImage = RasterImage.createFromBitmapHandle(bitmapHandle[i]);
/*      */ 
/* 4229 */             freeTempImage = image != null;
/*      */           }
/*      */           else
/*      */           {
/* 4233 */             tempImage = this._userImage;
/* 4234 */             this._userImage = null;
/* 4235 */             tempImage.initFromBitmapHandle(bitmapHandle[i]);
/*      */           }
/*      */ 
/* 4238 */           if (image == null)
/* 4239 */             image = tempImage;
/*      */           else {
/* 4241 */             image.addPage(tempImage);
/*      */           }
/* 4243 */           if (freeTempImage) {
/* 4244 */             tempImage.dispose();
/* 4245 */             tempImage = null;
/*      */           }
/*      */         }
/*      */       }
/*      */       int i;
/* 4250 */       return image;
/*      */     }
/*      */     finally
/*      */     {
/* 4254 */       for (int i = 0; i < 5; i++)
/*      */       {
/* 4256 */         if (bitmapHandle[i] != 0L)
/* 4257 */           ltkrn.FreeBitmapHandle(bitmapHandle[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void saveCmykPlanes(RasterImage image, ILeadStream stream, RasterImageFormat format, int bitsPerPlane, int pageNumber, CodecsSavePageMode pageMode)
/*      */   {
/* 4264 */     if (stream == null) {
/* 4265 */       throw new ArgumentNullException("stream");
/*      */     }
/* 4267 */     if (image == null) {
/* 4268 */       throw new ArgumentNullException("image");
/*      */     }
/* 4270 */     int saveFlags = this._options.getSaveFlags();
/* 4271 */     this._options.getSaveFileOption().Flags &= -3073;
/*      */ 
/* 4273 */     switch (1.$SwitchMap$leadtools$codecs$CodecsSavePageMode[pageMode.ordinal()])
/*      */     {
/*      */     case 1:
/* 4276 */       this._options.getSaveFileOption().PageNumber = 1;
/* 4277 */       break;
/*      */     case 2:
/* 4280 */       this._options.getSaveFileOption().PageNumber = 2;
/* 4281 */       break;
/*      */     case 3:
/* 4284 */       this._options.getSaveFileOption().PageNumber = pageNumber;
/* 4285 */       this._options.getSaveFileOption().Flags |= 2048;
/* 4286 */       break;
/*      */     case 4:
/* 4289 */       this._options.getSaveFileOption().PageNumber = pageNumber;
/* 4290 */       this._options.getSaveFileOption().Flags |= 1024;
/*      */     }
/*      */ 
/* 4294 */     int bitmapCount = image.getPageCount();
/* 4295 */     long[] bitmapHandle = new long[bitmapCount];
/*      */     try
/*      */     {
/* 4300 */       int savePage = image.getPage();
/* 4301 */       int bitmapHandleSize = ltkrn.BITMAPHANDLE_getSizeOf();
/* 4302 */       for (i = 1; i <= bitmapCount; i++)
/*      */       {
/* 4304 */         image.setPage(i);
/* 4305 */         bitmapHandle[(i - 1)] = ltkrn.AllocBitmapHandle();
/* 4306 */         if (bitmapHandle[(i - 1)] == 0L)
/* 4307 */           throw new RasterException(RasterExceptionCode.NO_MEMORY);
/* 4308 */         ltkrn.memcpy(bitmapHandle[(i - 1)], image.getCurrentBitmapHandle(), bitmapHandleSize);
/*      */       }
/* 4310 */       image.setPage(savePage);
/*      */ 
/* 4312 */       this._options.use();
/*      */ 
/* 4314 */       CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 4315 */       int ret = streamer.start();
/*      */       try
/*      */       {
/* 4319 */         if (ret == L_ERROR.SUCCESS.getValue())
/*      */         {
/* 4321 */           String fileName = stream.getFileName();
/* 4322 */           ret = ltfil.SaveFileCMYKArray(fileName != null ? fileName : "Stream", bitmapHandle, bitmapCount, format.getValue(), bitsPerPlane, this._options.getRealQualityFactor(format, bitsPerPlane * 4), saveFlags, this._saveImage.size() > 0 ? this : null, this._options.getSaveFileOption());
/*      */         }
/*      */ 
/* 4325 */         RasterException.checkErrorCode(ret);
/*      */       }
/*      */       finally
/*      */       {
/* 4329 */         if (stream != null)
/* 4330 */           streamer.dispose();
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*      */       int i;
/* 4335 */       if ((bitmapCount > 0) && (bitmapHandle != null))
/*      */       {
/* 4337 */         for (i = 1; i <= bitmapCount; i++)
/* 4338 */           if (bitmapHandle[(i - 1)] != 0L)
/* 4339 */             ltkrn.FreeBitmapHandle(bitmapHandle[(i - 1)]);
/* 4340 */         bitmapHandle = null;
/*      */       }
/*      */     }
/*      */     int i;
/*      */   }
/*      */ 
/*      */   public CodecsExtensionList readExtensions(ILeadStream stream, int pageNumber)
/*      */   {
/* 4403 */     if (stream == null) {
/* 4404 */       throw new ArgumentNullException("stream");
/*      */     }
/* 4406 */     this._options.use();
/*      */ 
/* 4408 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 4409 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 4413 */       CodecsExtensionList extensionList = new CodecsExtensionList();
/*      */       String fileName;
/* 4414 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/* 4416 */         this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */ 
/* 4418 */         fileName = stream.getFileName();
/* 4419 */         ret = ltfil.ReadFileExtensions(fileName != null ? fileName : "Stream", extensionList, this._options.getLoadFileOption());
/*      */       }
/* 4421 */       if (ret < 0)
/*      */       {
/* 4423 */         if (!checkExceptions(ret))
/* 4424 */           return null;
/*      */       }
/* 4426 */       else if (ret == 0) {
/* 4427 */         return null;
/*      */       }
/* 4429 */       return extensionList;
/*      */     }
/*      */     finally
/*      */     {
/* 4433 */       if (stream != null) {
/* 4434 */         streamer.dispose();
/*      */       }
/* 4436 */       this._options.getLoadFileOption().PageNumber = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getLoadThenResize()
/*      */   {
/* 4442 */     return this._loadThenResize;
/*      */   }
/*      */ 
/*      */   public void setLoadThenResize(boolean value) {
/* 4446 */     this._loadThenResize = value;
/*      */   }
/*      */ 
/*      */   public static boolean tagsSupported(RasterImageFormat format)
/*      */   {
/* 4451 */     return ltfil.TagsSupported(format.getValue());
/*      */   }
/*      */ 
/*      */   public static boolean geoKeysSupported(RasterImageFormat format) {
/* 4455 */     return ltfil.GeoKeysSupported(format.getValue());
/*      */   }
/*      */ 
/*      */   public static boolean commentsSupported(RasterImageFormat format) {
/* 4459 */     return ltfil.CommentsSupported(format.getValue());
/*      */   }
/*      */ 
/*      */   public static boolean markersSupported(RasterImageFormat format) {
/* 4463 */     return ltfil.MarkersSupported(format.getValue());
/*      */   }
/*      */ 
/*      */   public boolean isAsyncBusy(Object operation)
/*      */   {
/* 4592 */     AsyncOperationData data = (AsyncOperationData)operation;
/* 4593 */     return (data != null) && (data.Task != null) && (!data.Task.isFinished());
/*      */   }
/*      */ 
/*      */   public void cancelAsync(Object operation)
/*      */   {
/* 4598 */     AsyncOperationData data = (AsyncOperationData)operation;
/* 4599 */     if (isAsyncBusy(operation))
/*      */     {
/* 4601 */       data.Cancelled = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void finishAsyncGetInformation(AsyncOperationData data)
/*      */   {
/*      */     try
/*      */     {
/* 4609 */       if ((data.Stream instanceof LeadURIStream)) {
/* 4610 */         data.Info = getInformation(((LeadURIStream)data.Stream).getURI(), data.InfoTotalPages, data.InfoPageNumber, data);
/*      */       }
/*      */       else {
/* 4613 */         setOptions(data.CodecsOptions.clone());
/* 4614 */         data.Info = doGetInformation(data.Stream, data.InfoTotalPages, data.InfoPageNumber, false);
/*      */       }
/*      */ 
/* 4617 */       data.EventArgs = new CodecsGetInformationAsyncCompletedEvent(this, data.Info, data.Stream, null, data.Cancelled, data.UserState);
/*      */     }
/*      */     catch (RuntimeException ex)
/*      */     {
/* 4621 */       data.EventArgs = new CodecsGetInformationAsyncCompletedEvent(this, null, data.Stream, ex, data.Cancelled, data.UserState);
/* 4622 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void finishAsyncLoad(AsyncOperationData data)
/*      */   {
/*      */     try
/*      */     {
/* 4630 */       if ((data.Stream instanceof LeadURIStream)) {
/* 4631 */         if (data.UseSize)
/* 4632 */           data.Image = loadURI(new LoadParams(data.Stream, data.LoadBitsPerPixel, data.LoadOrder, data.LoadFirstPage, data.LoadLastPage, data.Width, data.Height, data.SizeFlags), data);
/*      */         else
/* 4634 */           data.Image = loadURI(new LoadParams(data.Stream, data.LoadBitsPerPixel, data.LoadOrder, data.LoadFirstPage, data.LoadLastPage), data);
/*      */       }
/*      */       else {
/* 4637 */         setOptions(data.CodecsOptions.clone());
/* 4638 */         if (data.UseSize)
/* 4639 */           data.Image = doLoad(new LoadParams(data.Stream, data.LoadBitsPerPixel, data.LoadOrder, data.LoadFirstPage, data.LoadLastPage, data.Width, data.Height, data.SizeFlags));
/*      */         else {
/* 4641 */           data.Image = doLoad(new LoadParams(data.Stream, data.LoadBitsPerPixel, data.LoadOrder, data.LoadFirstPage, data.LoadLastPage, data.UseTile ? data.Tile : null));
/*      */         }
/*      */       }
/* 4644 */       data.EventArgs = new CodecsLoadAsyncCompletedEvent(this, data.Image, data.Stream, null, data.Cancelled, data.UserState);
/*      */     }
/*      */     catch (RasterException ex)
/*      */     {
/* 4648 */       if ((data.Image != null) && (!data.Image.isDisposed()))
/*      */       {
/* 4650 */         data.Image.dispose();
/*      */       }
/*      */ 
/* 4653 */       if (ex.getCode() == RasterExceptionCode.USER_ABORT)
/*      */       {
/* 4655 */         data.EventArgs = new CodecsLoadAsyncCompletedEvent(this, null, data.Stream, null, true, data.UserState);
/*      */       }
/*      */       else
/*      */       {
/* 4659 */         data.EventArgs = new CodecsLoadAsyncCompletedEvent(this, null, data.Stream, ex, data.Cancelled, data.UserState);
/*      */       }
/*      */     }
/*      */     catch (RuntimeException ex)
/*      */     {
/* 4664 */       data.EventArgs = new CodecsLoadAsyncCompletedEvent(this, null, data.Stream, ex, data.Cancelled, data.UserState);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void finishAsyncLoadSvg(AsyncOperationData data)
/*      */   {
/* 4670 */     ILeadStream streamToUse = null;
/*      */     try
/*      */     {
/* 4676 */       if ((data.Stream instanceof LeadURIStream))
/*      */       {
/* 4678 */         URI uri = ((LeadURIStream)data.Stream).getURI();
/*      */         try
/*      */         {
/* 4681 */           byte[] buffer = downloadData(uri, (int)this._uriOperationBufferSize);
/* 4682 */           streamToUse = LeadStreamFactory.create(buffer);
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/* 4686 */           throw new RuntimeException(ex);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 4691 */         streamToUse = data.Stream;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 4696 */         data.SvgDocument = loadSvg(streamToUse, data.LoadFirstPage, data.SvgOptions);
/*      */       }
/*      */       catch (Exception ex)
/*      */       {
/* 4700 */         throw new RuntimeException(ex);
/*      */       }
/* 4702 */       data.EventArgs = new CodecsLoadSvgAsyncCompletedEvent(this, data.SvgDocument, data.Stream, null, data.Cancelled, data.UserState);
/*      */     }
/*      */     catch (RasterException ex)
/*      */     {
/* 4706 */       if (data.SvgDocument != null) {
/* 4707 */         data.SvgDocument.dispose();
/*      */       }
/* 4709 */       if (ex.getCode() == RasterExceptionCode.USER_ABORT)
/*      */       {
/* 4711 */         data.EventArgs = new CodecsLoadSvgAsyncCompletedEvent(this, null, data.Stream, null, true, data.UserState);
/*      */       }
/*      */       else
/*      */       {
/* 4715 */         data.EventArgs = new CodecsLoadSvgAsyncCompletedEvent(this, null, data.Stream, ex, data.Cancelled, data.UserState);
/*      */       }
/*      */     }
/*      */     catch (RuntimeException ex)
/*      */     {
/* 4720 */       data.EventArgs = new CodecsLoadSvgAsyncCompletedEvent(this, null, data.Stream, ex, data.Cancelled, data.UserState);
/* 4721 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static byte[] downloadData(URI uri, int bufferSize) throws Exception
/*      */   {
/* 4727 */     ByteArrayOutputStream outStream = null;
/* 4728 */     URLConnection connection = null;
/* 4729 */     InputStream inStream = null;
/*      */     try
/*      */     {
/* 4734 */       outStream = new ByteArrayOutputStream();
/*      */ 
/* 4736 */       URL url = uri.toURL();
/* 4737 */       connection = url.openConnection();
/* 4738 */       inStream = connection.getInputStream();
/* 4739 */       byte[] buffer = new byte[bufferSize];
/* 4740 */       int bytes = 0;
/*      */       do {
/* 4742 */         bytes = inStream.read(buffer);
/* 4743 */         if (bytes != -1) {
/* 4744 */           outStream.write(buffer, 0, bytes);
/*      */         }
/*      */       }
/* 4747 */       while (bytes != -1);
/* 4748 */       return outStream.toByteArray();
/*      */     }
/*      */     finally
/*      */     {
/*      */       try
/*      */       {
/* 4754 */         if (inStream != null)
/* 4755 */           inStream.close();
/* 4756 */         if (outStream != null)
/* 4757 */           outStream.close();
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void addGetInformationAsyncCompletedListener(CodecsGetInformationAsyncCompletedListener listener)
/*      */   {
/* 4768 */     if (this._GetInformationAsyncCompleted.contains(listener))
/* 4769 */       return;
/* 4770 */     this._GetInformationAsyncCompleted.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeGetInformationAsyncCompletedListener(CodecsGetInformationAsyncCompletedListener listener)
/*      */   {
/* 4775 */     if (!this._GetInformationAsyncCompleted.contains(listener))
/* 4776 */       return;
/* 4777 */     this._GetInformationAsyncCompleted.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireGetInformationAsyncCompleted(CodecsGetInformationAsyncCompletedEvent event)
/*      */   {
/* 4782 */     for (CodecsGetInformationAsyncCompletedListener listener : this._GetInformationAsyncCompleted)
/* 4783 */       listener.onGetInformationAsyncCompleted(event);
/*      */   }
/*      */ 
/*      */   private void onGetInformationAsyncCompleted(CodecsGetInformationAsyncCompletedEvent event)
/*      */   {
/* 4788 */     CodecsImageInfo info = event.getInfo();
/*      */ 
/* 4790 */     if (info != null)
/*      */     {
/* 4792 */       info.finishLoading();
/*      */     }
/*      */ 
/* 4796 */     fireGetInformationAsyncCompleted(event);
/*      */   }
/*      */ 
/*      */   public Object getInformationAsync(ILeadStream stream, boolean totalPages, int pageNumber, Object userState)
/*      */   {
/* 4807 */     if (stream == null) {
/* 4808 */       throw new ArgumentNullException("stream");
/*      */     }
/* 4810 */     return doGetInformationAsync(stream, totalPages, pageNumber, userState);
/*      */   }
/*      */ 
/*      */   private Object doGetInformationAsync(ILeadStream stream, boolean totalPages, int pageNumber, Object userState)
/*      */   {
/* 4822 */     AsyncOperationData data = new AsyncOperationData();
/* 4823 */     data.UserState = userState;
/* 4824 */     data.Operation = RasterCodecs.AsyncOperationData.OperationType.GET_INFO;
/*      */ 
/* 4826 */     data.Stream = stream;
/*      */ 
/* 4828 */     data.InfoTotalPages = totalPages;
/* 4829 */     data.InfoPageNumber = pageNumber;
/* 4830 */     data.CodecsOptions = getOptions().clone();
/* 4831 */     data.Cancelled = false;
/*      */ 
/* 4833 */     data.Start(LeadPlatform.createTaskWorker(new CodecsAsyncWorker(data)));
/* 4834 */     return data;
/*      */   }
/*      */ 
/*      */   public synchronized void addLoadAsyncCompletedListener(CodecsLoadAsyncCompletedListener listener)
/*      */   {
/* 4845 */     if (this._LoadAsyncCompleted.contains(listener)) {
/* 4846 */       return;
/*      */     }
/* 4848 */     this._LoadAsyncCompleted.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeLoadAsyncCompletedListener(CodecsLoadAsyncCompletedListener listener) {
/* 4852 */     if (!this._LoadAsyncCompleted.contains(listener)) {
/* 4853 */       return;
/*      */     }
/* 4855 */     this._LoadAsyncCompleted.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void onLoadAsyncCompleted(CodecsLoadAsyncCompletedEvent event)
/*      */   {
/* 4861 */     for (CodecsLoadAsyncCompletedListener listener : this._LoadAsyncCompleted)
/* 4862 */       listener.onLoadAsyncCompleted(event);
/*      */   }
/*      */ 
/*      */   public synchronized void addLoadSvgAsyncCompletedListener(CodecsLoadSvgAsyncCompletedListener listener)
/*      */   {
/* 4868 */     if (this._LoadSvgAsyncCompleted.contains(listener)) {
/* 4869 */       return;
/*      */     }
/* 4871 */     this._LoadSvgAsyncCompleted.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeLoadSvgAsyncCompletedListener(CodecsLoadSvgAsyncCompletedListener listener) {
/* 4875 */     if (!this._LoadSvgAsyncCompleted.contains(listener)) {
/* 4876 */       return;
/*      */     }
/* 4878 */     this._LoadSvgAsyncCompleted.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void onLoadSvgAsyncCompleted(CodecsLoadSvgAsyncCompletedEvent event)
/*      */   {
/* 4884 */     for (CodecsLoadSvgAsyncCompletedListener listener : this._LoadSvgAsyncCompleted)
/* 4885 */       listener.onLoadSvgAsyncCompleted(event);
/*      */   }
/*      */ 
/*      */   public Object loadSvgAsync(ILeadStream stream, int pageNumber, CodecsLoadSvgOptions options, Object userState)
/*      */   {
/* 4890 */     if (stream == null)
/* 4891 */       throw new ArgumentNullException("stream");
/* 4892 */     if (pageNumber < 1) {
/* 4893 */       throw new IllegalArgumentException("pageNumber must be a value greater than or equal to 1");
/*      */     }
/* 4895 */     return doLoadSvgAsync(stream, pageNumber, options, userState);
/*      */   }
/*      */ 
/*      */   private Object doLoadSvgAsync(ILeadStream stream, int pageNumber, CodecsLoadSvgOptions options, Object userState)
/*      */   {
/* 4900 */     AsyncOperationData data = new AsyncOperationData();
/* 4901 */     data.UserState = userState;
/* 4902 */     data.Operation = RasterCodecs.AsyncOperationData.OperationType.LOAD_SVG;
/*      */ 
/* 4904 */     data.Stream = stream;
/*      */ 
/* 4906 */     data.LoadFirstPage = pageNumber;
/* 4907 */     data.LoadLastPage = pageNumber;
/* 4908 */     data.SvgOptions = (options != null ? options.clone() : null);
/* 4909 */     data.Cancelled = false;
/*      */ 
/* 4911 */     data.Start(LeadPlatform.createTaskWorker(new CodecsAsyncWorker(data)));
/* 4912 */     return data;
/*      */   }
/*      */ 
/*      */   private int getLastLoadPage(int pageNumber)
/*      */   {
/* 4918 */     if (this._options.getLoad().getAllPages()) {
/* 4919 */       return -1;
/*      */     }
/* 4921 */     return pageNumber;
/*      */   }
/*      */ 
/*      */   public Object loadAsync(ILeadStream stream, Object userState)
/*      */   {
/* 4926 */     if (stream == null) {
/* 4927 */       throw new ArgumentNullException("stream");
/*      */     }
/* 4929 */     return doLoadAsync(stream, 0, CodecsLoadByteOrder.BGR_OR_GRAY, 1, getLastLoadPage(1), false, null, false, -1, -1, RasterSizeFlags.NONE, userState);
/*      */   }
/*      */ 
/*      */   public Object loadAsync(ILeadStream stream, int pageNumber, Object userState)
/*      */   {
/* 4934 */     if (stream == null)
/* 4935 */       throw new ArgumentNullException("stream");
/* 4936 */     if (pageNumber < 1) {
/* 4937 */       throw new ArgumentOutOfRangeException("pageNumber", pageNumber, "Must be a value greater than or equal to 1");
/*      */     }
/* 4939 */     return doLoadAsync(stream, 0, CodecsLoadByteOrder.BGR_OR_GRAY, pageNumber, pageNumber, false, null, false, -1, -1, RasterSizeFlags.NONE, userState);
/*      */   }
/*      */ 
/*      */   public Object loadAsync(ILeadStream stream, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage, Object userState)
/*      */   {
/* 4944 */     if (stream == null) {
/* 4945 */       throw new ArgumentNullException("stream");
/*      */     }
/* 4947 */     return doLoadAsync(stream, bitsPerPixel, order, firstPage, lastPage, false, null, false, -1, -1, RasterSizeFlags.NONE, userState);
/*      */   }
/*      */ 
/*      */   public Object loadAsync(ILeadStream stream, LeadRect tile, Object userState) {
/* 4951 */     if (stream == null) {
/* 4952 */       throw new ArgumentNullException("stream");
/*      */     }
/* 4954 */     return doLoadAsync(stream, 0, CodecsLoadByteOrder.BGR_OR_GRAY, 1, -1, true, tile, false, -1, -1, RasterSizeFlags.NONE, userState);
/*      */   }
/*      */ 
/*      */   public Object loadAsync(ILeadStream stream, LeadRect tile, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage, Object userState) {
/* 4958 */     if (stream == null) {
/* 4959 */       throw new ArgumentNullException("stream");
/*      */     }
/* 4961 */     return doLoadAsync(stream, bitsPerPixel, order, firstPage, lastPage, true, tile, false, -1, -1, RasterSizeFlags.NONE, userState);
/*      */   }
/*      */ 
/*      */   public Object loadAsync(ILeadStream stream, int width, int height, int bitsPerPixel, RasterSizeFlags flags, CodecsLoadByteOrder order, Object userState) {
/* 4965 */     if (stream == null) {
/* 4966 */       throw new ArgumentNullException("stream");
/*      */     }
/* 4968 */     return doLoadAsync(stream, bitsPerPixel, order, 1, getLastLoadPage(1), false, null, true, width, height, flags, userState);
/*      */   }
/*      */ 
/*      */   public Object loadAsync(ILeadStream stream, int width, int height, int bitsPerPixel, RasterSizeFlags flags, CodecsLoadByteOrder order, int firstPage, int lastPage, Object userState) {
/* 4972 */     if (stream == null) {
/* 4973 */       throw new ArgumentNullException("stream");
/*      */     }
/* 4975 */     return doLoadAsync(stream, bitsPerPixel, order, firstPage, lastPage, false, null, true, width, height, flags, userState);
/*      */   }
/*      */ 
/*      */   private Object doLoadAsync(ILeadStream stream, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage, boolean useTile, LeadRect tile, boolean useSize, int width, int height, RasterSizeFlags sizeFlags, Object userState)
/*      */   {
/* 4980 */     AsyncOperationData data = new AsyncOperationData();
/* 4981 */     data.UserState = userState;
/* 4982 */     data.Operation = RasterCodecs.AsyncOperationData.OperationType.LOAD;
/* 4983 */     data.Stream = stream;
/* 4984 */     data.LoadBitsPerPixel = bitsPerPixel;
/* 4985 */     data.LoadOrder = order;
/* 4986 */     data.LoadFirstPage = firstPage;
/* 4987 */     data.LoadLastPage = lastPage;
/* 4988 */     data.UseTile = useTile;
/* 4989 */     data.Tile = tile;
/* 4990 */     data.Cancelled = false;
/* 4991 */     data.CodecsOptions = getOptions();
/*      */ 
/* 4993 */     if (useSize) {
/* 4994 */       data.UseSize = true;
/* 4995 */       data.Width = width;
/* 4996 */       data.Height = height;
/* 4997 */       data.SizeFlags = sizeFlags;
/*      */     }
/* 4999 */     data.Start(LeadPlatform.createTaskWorker(new CodecsAsyncWorker(data)));
/* 5000 */     return data;
/*      */   }
/*      */ 
/*      */   final RasterImage GetUserImage()
/*      */   {
/* 5005 */     return this._userImage;
/*      */   }
/*      */ 
/*      */   final void SetUserImage(RasterImage value) {
/* 5009 */     this._userImage = value;
/*      */   }
/*      */ 
/*      */   final CodecsImageInfo GetUserImageInfo()
/*      */   {
/* 5014 */     return this._userImageInfo;
/*      */   }
/*      */ 
/*      */   final void SetUserImageInfo(CodecsImageInfo value) {
/* 5018 */     this._userImageInfo = value;
/*      */   }
/*      */ 
/*      */   public CodecsRasterPdfInfo getRasterPdfInfo(ILeadStream stream, int pageNumber)
/*      */   {
/* 5023 */     if (stream == null) {
/* 5024 */       throw new ArgumentNullException("stream");
/*      */     }
/* 5026 */     this._options.use();
/*      */ 
/* 5028 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/* 5029 */     int ret = streamer.start();
/*      */     try
/*      */     {
/* 5033 */       CodecsRasterPdfInfo pdfInfo = new CodecsRasterPdfInfo();
/* 5034 */       String fileName = stream.getFileName();
/* 5035 */       if (ret == L_ERROR.SUCCESS.getValue())
/* 5036 */         ret = ltfil.GetRasterPdfInfo(fileName != null ? fileName : "Stream", pageNumber, pdfInfo);
/* 5037 */       checkExceptions(ret);
/*      */ 
/* 5039 */       return pdfInfo;
/*      */     }
/*      */     finally
/*      */     {
/* 5043 */       if (stream != null)
/* 5044 */         streamer.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void saveOptions(String fileName) throws Exception {
/* 5049 */     if (fileName == null) throw new ArgumentNullException("fileName");
/*      */ 
/* 5051 */     CodecsOptionsSerializer.save(fileName, null, this._options);
/*      */   }
/*      */ 
/*      */   public void saveOptions(OutputStream stream) throws Exception {
/* 5055 */     if (stream == null) throw new ArgumentNullException("stream");
/*      */ 
/* 5057 */     CodecsOptionsSerializer.save(null, stream, this._options);
/*      */   }
/*      */ 
/*      */   public void loadOptions(String fileName) throws Exception {
/* 5061 */     if (fileName == null) throw new ArgumentNullException("fileName");
/*      */ 
/* 5063 */     CodecsOptions options = CodecsOptionsSerializer.load(fileName, null);
/* 5064 */     setOptions(options);
/*      */   }
/*      */ 
/*      */   public void loadOptions(InputStream stream) throws Exception {
/* 5068 */     if (stream == null) throw new ArgumentNullException("stream");
/*      */ 
/* 5070 */     CodecsOptions options = CodecsOptionsSerializer.load(null, stream);
/* 5071 */     setOptions(options);
/*      */   }
/*      */ 
/*      */   public boolean canLoadSvg(ILeadStream stream) {
/* 5075 */     if (stream == null) {
/* 5076 */       throw new ArgumentNullException("stream");
/*      */     }
/* 5078 */     if (!stream.canSeek())
/*      */     {
/* 5081 */       throw new IllegalArgumentException("Stream is not seekable");
/*      */     }
/*      */ 
/* 5084 */     this._options.use();
/*      */ 
/* 5086 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/*      */     try {
/* 5088 */       boolean[] isSupported = new boolean[1];
/*      */ 
/* 5090 */       int ret = streamer.start();
/*      */       String fileName;
/* 5091 */       if (ret == L_ERROR.SUCCESS.getValue()) {
/* 5092 */         this._options.getLoadFileOption().PageNumber = 0;
/*      */ 
/* 5094 */         fileName = stream.getFileName();
/* 5095 */         isSupported[0] = false;
/* 5096 */         ret = ltfil.CanLoadSvg(fileName, isSupported, this._options.getLoadFileOption());
/*      */       }
/* 5098 */       checkExceptions(ret);
/*      */ 
/* 5100 */       return isSupported[0];
/*      */     }
/*      */     finally {
/* 5103 */       if (stream != null)
/* 5104 */         streamer.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ISvgDocument loadSvg(ILeadStream stream, int pageNumber, CodecsLoadSvgOptions options) throws Exception {
/* 5109 */     if (stream == null) {
/* 5110 */       throw new ArgumentNullException("stream");
/*      */     }
/* 5112 */     if ((stream instanceof LeadURIStream))
/*      */     {
/* 5114 */       LeadURIStream uriStream = (LeadURIStream)stream;
/* 5115 */       byte[] buffer = downloadData(uriStream.getURI(), (int)this._uriOperationBufferSize);
/* 5116 */       stream = LeadStreamFactory.create(buffer);
/*      */     }
/*      */ 
/* 5119 */     this._options.use();
/* 5120 */     this._options.getLoadFileOption().PageNumber = pageNumber;
/* 5121 */     LOADSVGOPTIONS loadSvgOptions = new LOADSVGOPTIONS();
/* 5122 */     if (options != null) {
/* 5123 */       options.toUnmanaged(loadSvgOptions);
/*      */     }
/* 5125 */     CodecsStreamer streamer = new CodecsStreamer(stream, false);
/*      */     try {
/* 5127 */       ISvgDocument svg = null;
/* 5128 */       int ret = streamer.start();
/*      */       String fileName;
/* 5129 */       if (ret == L_ERROR.SUCCESS.getValue()) {
/* 5130 */         fileName = stream.getFileName();
/* 5131 */         ret = ltfil.LoadSvg(fileName, loadSvgOptions, this._options.getLoadFileOption());
/*      */       }
/*      */ 
/* 5134 */       if (ret == L_ERROR.SUCCESS.getValue()) {
/* 5135 */         svg = call_SvgCreateDocument(loadSvgOptions.SvgHandle);
/*      */       }
/*      */ 
/* 5138 */       if (ret != L_ERROR.SUCCESS.getValue()) {
/* 5139 */         checkExceptions(ret);
/*      */       }
/* 5141 */       return svg;
/*      */     }
/*      */     finally {
/* 5144 */       if (stream != null)
/* 5145 */         streamer.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void freeSvg(ISvgDocument svg) throws Exception {
/* 5150 */     if (svg == null) {
/* 5151 */       throw new ArgumentNullException("svg");
/*      */     }
/* 5153 */     int ret = ltfil.FreeSvg(svg.getHandle());
/* 5154 */     checkExceptions(ret);
/*      */   }
/*      */ 
/*      */   static ISvgDocument call_SvgCreateDocument(long svgHandle) throws Exception {
/* 5158 */     String fullName = "leadtools.svg.SvgDocument";
/* 5159 */     Class theClass = Class.forName(fullName);
/*      */ 
/* 5161 */     Method method = theClass.getMethod("createFromHandle", new Class[] { Long.TYPE });
/* 5162 */     Object result = method.invoke(null, new Object[] { Long.valueOf(svgHandle) });
/* 5163 */     return (ISvgDocument)result;
/*      */   }
/*      */ 
/*      */   public static boolean formatSupportsMultipageSave(RasterImageFormat format) {
/* 5167 */     return ltfil.FormatSupportsMultipageSave(format.getValue());
/*      */   }
/*      */ 
/*      */   public static String getMimeType(RasterImageFormat format) {
/* 5171 */     String res = ltfil.GetFormatMimeType(format.getValue());
/* 5172 */     return res;
/*      */   }
/*      */ 
/*      */   public static String getExtension(RasterImageFormat format) {
/* 5176 */     String res = ltfil.GetFormatFileExtension(format.getValue());
/* 5177 */     return res;
/*      */   }
/*      */ 
/*      */   public static boolean isTiffFormat(RasterImageFormat format) {
/* 5181 */     int res = ltfil.IsTiffFormat(format.getValue());
/* 5182 */     return res != 0;
/*      */   }
/*      */ 
/*      */   private class CodecsAsyncWorker
/*      */     implements ILeadTaskWorker.Worker
/*      */   {
/*      */     RasterCodecs.AsyncOperationData _data;
/*      */ 
/*      */     public CodecsAsyncWorker(RasterCodecs.AsyncOperationData data)
/*      */     {
/* 4553 */       this._data = data;
/*      */     }
/*      */ 
/*      */     public void onCompleted()
/*      */     {
/* 4558 */       if (this._data != null)
/* 4559 */         switch (RasterCodecs.1.$SwitchMap$leadtools$codecs$RasterCodecs$AsyncOperationData$OperationType[this._data.Operation.ordinal()])
/*      */         {
/*      */         case 1:
/* 4562 */           RasterCodecs.this.onGetInformationAsyncCompleted((CodecsGetInformationAsyncCompletedEvent)this._data.EventArgs);
/* 4563 */           break;
/*      */         case 2:
/* 4565 */           RasterCodecs.this.onLoadAsyncCompleted((CodecsLoadAsyncCompletedEvent)this._data.EventArgs);
/* 4566 */           break;
/*      */         case 3:
/* 4568 */           RasterCodecs.this.onLoadSvgAsyncCompleted((CodecsLoadSvgAsyncCompletedEvent)this._data.EventArgs);
/*      */         }
/*      */     }
/*      */ 
/*      */     public void onWorking()
/*      */     {
/* 4575 */       switch (RasterCodecs.1.$SwitchMap$leadtools$codecs$RasterCodecs$AsyncOperationData$OperationType[this._data.Operation.ordinal()])
/*      */       {
/*      */       case 1:
/* 4578 */         RasterCodecs.this.finishAsyncGetInformation(this._data);
/* 4579 */         break;
/*      */       case 2:
/* 4581 */         RasterCodecs.this.finishAsyncLoad(this._data);
/* 4582 */         break;
/*      */       case 3:
/* 4584 */         RasterCodecs.this.finishAsyncLoadSvg(this._data);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AsyncOperationData
/*      */   {
/*      */     public CodecsOptions CodecsOptions;
/*      */     public RasterImage Image;
/*      */     public CodecsImageInfo Info;
/*      */     public OperationType Operation;
/*      */     public Object UserState;
/*      */     public ILeadStream Stream;
/*      */     public boolean InfoTotalPages;
/*      */     public int InfoPageNumber;
/*      */     public int LoadBitsPerPixel;
/*      */     public CodecsLoadByteOrder LoadOrder;
/*      */     public int LoadFirstPage;
/*      */     public int LoadLastPage;
/*      */     public boolean UseTile;
/* 4526 */     public LeadRect Tile = new LeadRect(0, 0, 0, 0);
/*      */     public ISvgDocument SvgDocument;
/*      */     public CodecsLoadSvgOptions SvgOptions;
/* 4530 */     public CodecsAsyncCompletedEvent EventArgs = null;
/* 4531 */     public ILeadTaskWorker Task = null;
/* 4532 */     public boolean Cancelled = false;
/*      */ 
/* 4534 */     public boolean UseSize = false;
/*      */     public int Width;
/*      */     public int Height;
/*      */     public RasterSizeFlags SizeFlags;
/*      */ 
/*      */     public void Start(ILeadTaskWorker worker)
/*      */     {
/* 4544 */       this.Task = worker;
/* 4545 */       this.Task.start();
/*      */     }
/*      */ 
/*      */     public static enum OperationType
/*      */     {
/* 4498 */       GET_INFO, 
/* 4499 */       LOAD, 
/* 4500 */       LOAD_SVG;
/*      */     }
/*      */   }
/*      */ 
/*      */   class DecompressData
/*      */   {
/*      */     public long DecompressHandle;
/*      */     public long BitmapHandle;
/*      */ 
/*      */     public DecompressData(long decompressContext, long bitmapHandle)
/*      */     {
/* 4080 */       this.DecompressHandle = decompressContext;
/* 4081 */       this.BitmapHandle = bitmapHandle;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class FeedCallbackThunk
/*      */   {
/*      */     private long _hfeedLoad;
/*      */     private RasterImage _feedLoadImage;
/*      */     private long _feedLoadBitmapHandle;
/*      */     private int _feedLoadFirstPage;
/*      */     private int _feedLoadLastPage;
/*      */     private int _feedLoadCurrentPage;
/*      */     private long _hfeedInfo;
/*      */     private FILEINFO _fileInfo;
/*      */ 
/*      */     private int FileReadCallback(FILEINFO fileInfo, long bitmapHandle, byte[] buffer, int uFlags, int nRow, int nLines)
/*      */     {
/* 2331 */       int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/* 2333 */       this._fileInfo = fileInfo;
/*      */ 
/* 2335 */       if (RasterCodecs.this._loadImage.size() > 0)
/*      */       {
/*      */         try
/*      */         {
/* 2339 */           RasterCodecs.this._loadImageEventArgs.init(fileInfo, buffer, uFlags, nRow, nLines);
/*      */ 
/* 2341 */           int pagePercent = RasterCodecs.MulDiv(100, nRow + nLines, RasterCodecs.this._fileReadCallbackImage != null ? RasterCodecs.this._fileReadCallbackImage.getHeight() : ltkrn.BITMAPHANDLE_getHeight(bitmapHandle));
/* 2342 */           int totalPercent = RasterCodecs.MulDiv(100, 100 * (RasterCodecs.this._loadImageEventArgs.getImagePage() - 1) + pagePercent, 100 * (RasterCodecs.this._loadImageEventArgs.getLastPage() - RasterCodecs.this._loadImageEventArgs.getFirstPage() + 1));
/* 2343 */           RasterCodecs.this._loadImageEventArgs.setPercentages(pagePercent, totalPercent);
/*      */ 
/* 2346 */           RasterCodecs.this.fireLoadImage(RasterCodecs.this._loadImageEventArgs);
/* 2347 */           if (RasterCodecs.this._loadImageEventArgs.getCancel())
/* 2348 */             ret = L_ERROR.ERROR_USER_ABORT.getValue();
/*      */         }
/*      */         finally
/*      */         {
/* 2352 */           RasterCodecs.this._loadImageEventArgs.freeImageInfo();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2358 */       boolean bLastRow = (uFlags & 0x8) == 8;
/* 2359 */       if ((RasterCodecs.this._loadImage.size() < 1) && (((bLastRow) && ((uFlags & 0x2) == 2)) || (((fileInfo.Flags & 0x1) != 1) && ((bLastRow) || (nRow + nLines >= ltkrn.BITMAPHANDLE_getHeight(bitmapHandle))))))
/*      */       {
/* 2361 */         this._feedLoadCurrentPage += 1;
/*      */ 
/* 2363 */         if ((this._feedLoadCurrentPage >= this._feedLoadFirstPage) && ((this._feedLoadLastPage == -1) || (this._feedLoadCurrentPage <= this._feedLoadLastPage)))
/*      */         {
/* 2365 */           ltkrn.ReleaseBitmap(bitmapHandle);
/* 2366 */           RasterImage image = RasterImage.createFromBitmapHandle(bitmapHandle);
/* 2367 */           ltkrn.MarkAsFreed(bitmapHandle);
/* 2368 */           if (this._feedLoadImage == null)
/* 2369 */             this._feedLoadImage = image;
/*      */           else
/* 2371 */             this._feedLoadImage.addPage(image);
/*      */         }
/*      */         else
/*      */         {
/* 2375 */           ltkrn.ReleaseBitmap(bitmapHandle);
/* 2376 */           ltkrn.FreeBitmap(bitmapHandle);
/* 2377 */           ltkrn.MarkAsFreed(bitmapHandle);
/*      */         }
/*      */       }
/*      */ 
/* 2381 */       return ret;
/*      */     }
/*      */ 
/*      */     public FeedCallbackThunk() {
/* 2385 */       this._hfeedLoad = 0L;
/* 2386 */       this._feedLoadImage = null;
/* 2387 */       this._feedLoadBitmapHandle = ltkrn.AllocBitmapHandle();
/* 2388 */       this._feedLoadFirstPage = 1;
/* 2389 */       this._feedLoadLastPage = -1;
/* 2390 */       this._feedLoadCurrentPage = 0;
/*      */ 
/* 2392 */       this._hfeedInfo = 0L;
/*      */ 
/* 2394 */       this._fileInfo = new FILEINFO();
/*      */     }
/*      */ 
/*      */     public void startFeedLoad(int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*      */     {
/* 2407 */       if (RasterCodecs.this._loadImage.size() > 0)
/*      */       {
/* 2409 */         RasterCodecs.this._loadImageEventArgs.init(this._feedLoadImage, 1, 1, 1, 1, null, false, null);
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 2414 */         RasterCodecs.this._options.setLoadFlags(RasterCodecs.this._options.getLoadFlags() | 0x1 | 0x2);
/* 2415 */         RasterCodecs.this._options.getLoadFileOption().PageNumber = 1;
/* 2416 */         RasterCodecs.this._options.use();
/*      */ 
/* 2418 */         long[] hfeedLoad = new long[1];
/* 2419 */         this._feedLoadFirstPage = firstPage;
/* 2420 */         this._feedLoadLastPage = lastPage;
/* 2421 */         this._feedLoadCurrentPage = 0;
/*      */ 
/* 2423 */         this._fileInfo = new FILEINFO();
/*      */ 
/* 2425 */         int ret = ltfil.StartFeedLoad(this._feedLoadBitmapHandle, bitsPerPixel, order.getValue(), RasterCodecs.this._options.getLoadFlags() | 0x10, this, hfeedLoad, RasterCodecs.this._options.getLoadFileOption(), null);
/* 2426 */         RasterException.checkErrorCode(ret);
/*      */ 
/* 2428 */         this._hfeedLoad = hfeedLoad[0];
/*      */       }
/*      */       catch (RasterException e)
/*      */       {
/* 2432 */         cancelFeedLoad();
/* 2433 */         throw e;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void feedLoad(byte[] data, int offset, int length) {
/* 2438 */       if (this._hfeedLoad == 0L) {
/* 2439 */         throw new InvalidOperationException("Feed load operation not in progress");
/*      */       }
/* 2441 */       if (isFeedLoadDone()) {
/* 2442 */         return;
/*      */       }
/*      */       try
/*      */       {
/* 2446 */         RasterCodecs.this._options.use();
/* 2447 */         int ret = ltfil.FeedLoad(this._hfeedLoad, data, offset, length);
/* 2448 */         if (ret != L_ERROR.SUCCESS.getValue())
/*      */         {
/* 2450 */           cancelFeedLoad();
/* 2451 */           RasterException.checkErrorCode(ret);
/*      */         }
/*      */       }
/*      */       catch (RasterException e)
/*      */       {
/* 2456 */         cancelFeedLoad();
/* 2457 */         throw e;
/*      */       }
/*      */     }
/*      */ 
/*      */     public RasterImage stopFeedLoad() {
/* 2462 */       if (this._hfeedLoad == 0L) {
/* 2463 */         throw new InvalidOperationException("Feed load operation not in progress");
/*      */       }
/* 2465 */       RasterCodecs.this._options.use();
/*      */       try
/*      */       {
/* 2469 */         int ret = ltfil.StopFeedLoad(this._hfeedLoad);
/* 2470 */         if ((ret != L_ERROR.SUCCESS.getValue()) && (this._feedLoadImage != null) && (this._feedLoadImage.getPageCount() > 0))
/* 2471 */           ret = L_ERROR.SUCCESS.getValue();
/* 2472 */         this._hfeedLoad = 0L;
/* 2473 */         RasterException.checkErrorCode(ret);
/*      */ 
/* 2475 */         RasterImage temp = this._feedLoadImage;
/*      */ 
/* 2477 */         if (this._fileInfo.Format != 0)
/*      */         {
/* 2479 */           temp.setOriginalFormat(RasterImageFormat.forValue(this._fileInfo.Format));
/* 2480 */           RasterCodecs.setAnimationParameters(temp, this._fileInfo);
/*      */         }
/* 2482 */         this._feedLoadImage = null;
/*      */ 
/* 2484 */         return temp;
/*      */       }
/*      */       finally
/*      */       {
/* 2488 */         freeFeedLoadParams();
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean isFeedLoadDone()
/*      */     {
/* 2494 */       if (this._hfeedLoad == 0L) {
/* 2495 */         return true;
/*      */       }
/*      */ 
/* 2498 */       if ((this._feedLoadLastPage == -1) || (this._feedLoadLastPage > this._feedLoadCurrentPage)) {
/* 2499 */         return false;
/*      */       }
/* 2501 */       return true;
/*      */     }
/*      */ 
/*      */     public void cancelFeedLoad()
/*      */     {
/*      */       try {
/* 2507 */         RasterCodecs.this._options.use();
/*      */ 
/* 2510 */         if (this._hfeedLoad != 0L)
/*      */         {
/* 2512 */           ltfil.StopFeedLoad(this._hfeedLoad);
/* 2513 */           this._hfeedLoad = 0L;
/*      */         }
/* 2515 */         if (this._feedLoadBitmapHandle != 0L)
/*      */         {
/* 2517 */           ltkrn.FreeBitmapHandle(this._feedLoadBitmapHandle);
/* 2518 */           this._feedLoadBitmapHandle = 0L;
/*      */         }
/*      */       }
/*      */       catch (RasterException e)
/*      */       {
/*      */       }
/*      */       finally
/*      */       {
/* 2526 */         freeFeedLoadParams();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void startFeedGetInformation(boolean totalPages, int pageNumber)
/*      */     {
/* 2532 */       if (pageNumber != -1)
/* 2533 */         RasterCodecs.this._options.getLoadFileOption().PageNumber = pageNumber;
/*      */       else {
/* 2535 */         RasterCodecs.this._options.getLoadFileOption().PageNumber = 1;
/*      */       }
/* 2537 */       RasterCodecs.this._options.use();
/*      */ 
/* 2539 */       long[] hfeedInfo = { 0L };
/* 2540 */       int ret = ltfil.StartFeedInfo(hfeedInfo, this._fileInfo, totalPages ? 1 : 0, RasterCodecs.this._options.getLoadFileOption());
/* 2541 */       RasterException.checkErrorCode(ret);
/*      */ 
/* 2543 */       this._hfeedInfo = hfeedInfo[0];
/*      */     }
/*      */ 
/*      */     public boolean feedGetInformation(byte[] data, int offset, int length)
/*      */     {
/* 2548 */       if (this._hfeedInfo == 0L) {
/* 2549 */         throw new InvalidOperationException("Feed get information operation not in progress");
/*      */       }
/*      */       try
/*      */       {
/* 2553 */         RasterCodecs.this._options.use();
/* 2554 */         int ret = ltfil.FeedInfo(this._hfeedInfo, data, offset, length);
/*      */ 
/* 2556 */         if ((ret != L_ERROR.SUCCESS.getValue()) && (ret != L_ERROR.SUCCESS_ABORT.getValue()))
/*      */         {
/* 2558 */           cancelFeedGetInformation();
/* 2559 */           RasterException.checkErrorCode(ret);
/*      */         }
/*      */ 
/* 2562 */         return ret == L_ERROR.SUCCESS.getValue();
/*      */       }
/*      */       catch (RasterException e)
/*      */       {
/* 2566 */         cancelFeedGetInformation();
/* 2567 */         throw e;
/*      */       }
/*      */     }
/*      */ 
/*      */     public CodecsImageInfo stopFeedGetInformation() {
/* 2572 */       if (this._hfeedInfo == 0L) {
/* 2573 */         throw new InvalidOperationException("Feed get information operation not in progress");
/*      */       }
/* 2575 */       RasterCodecs.this._options.use();
/*      */       try
/*      */       {
/* 2579 */         int ret = ltfil.StopFeedInfo(this._hfeedInfo);
/* 2580 */         RasterException.checkErrorCode(ret);
/* 2581 */         this._hfeedInfo = 0L;
/*      */ 
/* 2583 */         return new CodecsImageInfo(this._fileInfo);
/*      */       }
/*      */       finally
/*      */       {
/* 2587 */         freeFeedGetInformationParams();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void cancelFeedGetInformation()
/*      */     {
/*      */       try {
/* 2594 */         RasterCodecs.this._options.use();
/*      */ 
/* 2596 */         if (this._hfeedInfo != 0L)
/*      */         {
/* 2598 */           ltfil.StopFeedInfo(this._hfeedInfo);
/* 2599 */           this._hfeedInfo = 0L;
/*      */         }
/*      */       }
/*      */       catch (RasterException e)
/*      */       {
/*      */       }
/*      */       finally
/*      */       {
/* 2607 */         freeFeedGetInformationParams();
/*      */       }
/*      */     }
/*      */ 
/*      */     private void freeFeedGetInformationParams() {
/* 2612 */       dispose();
/*      */     }
/*      */ 
/*      */     private void freeFeedLoadParams() {
/* 2616 */       dispose();
/*      */     }
/*      */ 
/*      */     private void dispose() {
/* 2620 */       if (this._hfeedLoad != 0L)
/*      */       {
/* 2622 */         ltfil.StopFeedLoad(this._hfeedLoad);
/* 2623 */         this._hfeedLoad = 0L;
/*      */       }
/*      */ 
/* 2626 */       if (this._fileInfo != null)
/*      */       {
/* 2628 */         this._fileInfo = null;
/*      */       }
/*      */ 
/* 2631 */       if (this._feedLoadImage != null)
/*      */       {
/* 2633 */         this._feedLoadImage.dispose();
/* 2634 */         this._feedLoadImage = null;
/*      */       }
/* 2636 */       if (this._feedLoadBitmapHandle != 0L)
/*      */       {
/* 2638 */         ltkrn.FreeBitmapHandle(this._feedLoadBitmapHandle);
/* 2639 */         this._feedLoadBitmapHandle = 0L;
/*      */       }
/*      */ 
/* 2642 */       if (this._hfeedInfo != 0L)
/*      */       {
/* 2644 */         ltfil.StopFeedInfo(this._hfeedInfo);
/* 2645 */         this._hfeedInfo = 0L;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.RasterCodecs
 * JD-Core Version:    0.6.2
 */