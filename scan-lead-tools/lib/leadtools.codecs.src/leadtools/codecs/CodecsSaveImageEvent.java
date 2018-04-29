/*     */ package leadtools.codecs;
/*     */ 
/*     */ import leadtools.ILeadStream;
/*     */ import leadtools.LeadEvent;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.RasterNativeBuffer;
/*     */ 
/*     */ public class CodecsSaveImageEvent extends LeadEvent
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private RasterImage _image;
/*     */   private ILeadStream _stream;
/*     */   private int _page;
/*     */   private int _row;
/*     */   private int _lines;
/*     */   private RasterNativeBuffer _buffer;
/*     */   private int _firstPage;
/*     */   private int _lastPage;
/*     */   private int _imagePage;
/*     */   private int _pagePercent;
/*     */   private int _totalPercent;
/*     */   private boolean _cancel;
/*     */ 
/*     */   CodecsSaveImageEvent(Object source)
/*     */   {
/*  27 */     super(source);
/*     */   }
/*     */ 
/*     */   final void init(RasterImage image, ILeadStream stream, int firstPage, int page, int lastPage, int imagePage)
/*     */   {
/*  32 */     this._image = image;
/*  33 */     this._stream = stream;
/*  34 */     this._page = page;
/*  35 */     this._row = 0;
/*  36 */     this._lines = 0;
/*  37 */     this._cancel = false;
/*     */ 
/*  39 */     this._firstPage = firstPage;
/*  40 */     this._lastPage = lastPage;
/*  41 */     this._imagePage = imagePage;
/*  42 */     this._pagePercent = 0;
/*  43 */     this._totalPercent = 0;
/*  44 */     this._buffer = RasterNativeBuffer.getEmpty();
/*     */   }
/*     */ 
/*     */   final void init(int row, int lines, byte[] buffer) {
/*  48 */     this._row = row;
/*  49 */     this._lines = lines;
/*     */     int length;
/*     */     int length;
/*  52 */     if (this._image != null)
/*  53 */       length = lines * this._image.getBytesPerLine();
/*     */     else {
/*  55 */       length = 0;
/*     */     }
/*  57 */     this._buffer = new RasterNativeBuffer(buffer, length);
/*     */   }
/*     */ 
/*     */   public RasterImage getImage()
/*     */   {
/*  62 */     return this._image;
/*     */   }
/*     */ 
/*     */   public ILeadStream getStream()
/*     */   {
/*  67 */     return this._stream;
/*     */   }
/*     */ 
/*     */   public int getPage()
/*     */   {
/*  72 */     return this._page;
/*     */   }
/*     */ 
/*     */   public int getFirstPage()
/*     */   {
/*  77 */     return this._firstPage;
/*     */   }
/*     */ 
/*     */   public int getLastPage()
/*     */   {
/*  82 */     return this._lastPage;
/*     */   }
/*     */ 
/*     */   public int getImagePage()
/*     */   {
/*  87 */     return this._imagePage;
/*     */   }
/*     */ 
/*     */   public int getPagePercent()
/*     */   {
/*  92 */     return this._pagePercent;
/*     */   }
/*     */ 
/*     */   public int getTotalPercent()
/*     */   {
/*  97 */     return this._totalPercent;
/*     */   }
/*     */ 
/*     */   public void setPercentages(int pagePercent, int totalPercent)
/*     */   {
/* 102 */     this._pagePercent = pagePercent;
/* 103 */     this._totalPercent = totalPercent;
/*     */   }
/*     */ 
/*     */   public RasterNativeBuffer getBuffer()
/*     */   {
/* 108 */     return this._buffer;
/*     */   }
/*     */ 
/*     */   public boolean getCancel()
/*     */   {
/* 113 */     return this._cancel;
/*     */   }
/*     */ 
/*     */   public void setCancel(boolean value) {
/* 117 */     this._cancel = value;
/*     */   }
/*     */ 
/*     */   public int getRow()
/*     */   {
/* 122 */     return this._row;
/*     */   }
/*     */ 
/*     */   public int getLines()
/*     */   {
/* 127 */     return this._lines;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsSaveImageEvent
 * JD-Core Version:    0.6.2
 */