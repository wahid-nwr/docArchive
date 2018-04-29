/*     */ package leadtools.codecs;
/*     */ 
/*     */ import leadtools.ILeadStream;
/*     */ import leadtools.LeadEvent;
/*     */ import leadtools.LeadRect;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.RasterNativeBuffer;
/*     */ 
/*     */ public class CodecsLoadImageEvent extends LeadEvent
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private RasterImage _image;
/*     */   private int _firstPage;
/*     */   private int _page;
/*     */   private int _lastPage;
/*     */   private int _imagePage;
/*     */   private int _pagePercent;
/*     */   private int _totalPercent;
/*     */   private ILeadStream _stream;
/*     */   private boolean _tileValid;
/*  23 */   private LeadRect _tile = new LeadRect(0, 0, 0, 0);
/*     */   private boolean _cancel;
/*     */   private CodecsImageInfo _info;
/*     */   private CodecsLoadImageFlags _flags;
/*     */   private int _row;
/*     */   private int _lines;
/*     */   private RasterNativeBuffer _buffer;
/*     */ 
/*     */   CodecsLoadImageEvent(Object source)
/*     */   {
/*  33 */     super(source);
/*     */   }
/*     */ 
/*     */   final void init(RasterImage image, int firstPage, int page, int lastPage, int imagePage, ILeadStream stream, boolean tileValid, LeadRect tile) {
/*  37 */     this._image = image;
/*  38 */     this._firstPage = firstPage;
/*  39 */     this._page = page;
/*  40 */     this._lastPage = lastPage;
/*  41 */     this._imagePage = imagePage;
/*  42 */     this._stream = stream;
/*  43 */     this._tileValid = tileValid;
/*  44 */     this._tile = tile;
/*  45 */     this._cancel = false;
/*  46 */     this._info = null;
/*  47 */     this._flags = CodecsLoadImageFlags.NONE;
/*  48 */     this._row = 0;
/*  49 */     this._lines = 0;
/*  50 */     this._buffer = RasterNativeBuffer.getEmpty();
/*     */   }
/*     */ 
/*     */   final void init(FILEINFO fileInfo, byte[] buffer, int flags, int row, int lines)
/*     */   {
/*  55 */     if (this._info == null)
/*     */     {
/*  57 */       this._info = new CodecsImageInfo(fileInfo);
/*  58 */       if (this._image != null) {
/*  59 */         this._info.setPalette(this._image.getPalette());
/*     */       }
/*     */     }
/*  62 */     flags &= -33;
/*  63 */     flags &= -65;
/*  64 */     this._flags = CodecsLoadImageFlags.forValue(flags);
/*  65 */     this._row = row;
/*  66 */     this._lines = lines;
/*     */ 
/*  68 */     this._buffer = new RasterNativeBuffer(buffer, lines * this._info.getBytesPerLine());
/*     */   }
/*     */ 
/*     */   final void setImage(RasterImage image) {
/*  72 */     this._image = image;
/*     */   }
/*     */ 
/*     */   final void freeImageInfo() {
/*  76 */     if (this._info != null)
/*  77 */       this._info = null;
/*     */   }
/*     */ 
/*     */   public RasterImage getImage()
/*     */   {
/*  82 */     return this._image;
/*     */   }
/*     */ 
/*     */   public int getFirstPage()
/*     */   {
/*  87 */     return this._firstPage;
/*     */   }
/*     */ 
/*     */   public int getPage()
/*     */   {
/*  92 */     return this._page;
/*     */   }
/*     */ 
/*     */   public int getLastPage()
/*     */   {
/*  97 */     return this._lastPage;
/*     */   }
/*     */ 
/*     */   public int getImagePage()
/*     */   {
/* 102 */     return this._imagePage;
/*     */   }
/*     */ 
/*     */   public int getPagePercent()
/*     */   {
/* 107 */     return this._pagePercent;
/*     */   }
/*     */ 
/*     */   public int getTotalPercent()
/*     */   {
/* 112 */     return this._totalPercent;
/*     */   }
/*     */ 
/*     */   void setPercentages(int pagePercent, int totalPercent)
/*     */   {
/* 117 */     this._pagePercent = pagePercent;
/* 118 */     this._totalPercent = totalPercent;
/*     */   }
/*     */ 
/*     */   public ILeadStream getStream()
/*     */   {
/* 123 */     return this._stream;
/*     */   }
/*     */ 
/*     */   public boolean getTileValid()
/*     */   {
/* 128 */     return this._tileValid;
/*     */   }
/*     */ 
/*     */   public LeadRect getTile()
/*     */   {
/* 133 */     return this._tile;
/*     */   }
/*     */ 
/*     */   public boolean getCancel()
/*     */   {
/* 138 */     return this._cancel;
/*     */   }
/*     */ 
/*     */   public void setCancel(boolean value) {
/* 142 */     this._cancel = value;
/*     */   }
/*     */ 
/*     */   public CodecsImageInfo getInfo()
/*     */   {
/* 147 */     return this._info;
/*     */   }
/*     */ 
/*     */   public CodecsLoadImageFlags getFlags()
/*     */   {
/* 152 */     return this._flags;
/*     */   }
/*     */ 
/*     */   public int getRow()
/*     */   {
/* 157 */     return this._row;
/*     */   }
/*     */ 
/*     */   public int getLines()
/*     */   {
/* 162 */     return this._lines;
/*     */   }
/*     */ 
/*     */   public RasterNativeBuffer getBuffer()
/*     */   {
/* 167 */     return this._buffer;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsLoadImageEvent
 * JD-Core Version:    0.6.2
 */