/*     */ package leadtools.codecs;
/*     */ 
/*     */ import leadtools.RasterByteOrder;
/*     */ import leadtools.RasterColor;
/*     */ import leadtools.RasterImageFormat;
/*     */ import leadtools.RasterViewPerspective;
/*     */ 
/*     */ public final class CodecsStartDecompressOptions
/*     */ {
/*     */   private CodecsStartDecompressDataType _dataType;
/*     */   private RasterImageFormat _format;
/*     */   private int _width;
/*     */   private int _height;
/*     */   private int _bitsPerPixel;
/*     */   private RasterViewPerspective _viewPerspective;
/*     */   private RasterByteOrder _rawOrder;
/*     */   private CodecsLoadByteOrder _loadOrder;
/*     */   private int _xResolution;
/*     */   private int _yResolution;
/*     */   private RasterColor[] _palette;
/*     */   private boolean _leastSignificantBitFirst;
/*     */   private boolean _pad4;
/*     */   private boolean _usePalette;
/*     */   private int[] _colorMask;
/*     */   private CodecsTiffPhotometricInterpretation _tiffPhotometricInterpretation;
/*     */   private CodecsPlanarConfiguration _planarConfiguration;
/*     */   private long _bitmapHandle;
/*     */ 
/*     */   private CodecsStartDecompressOptions(int dummy)
/*     */   {
/*  31 */     this._dataType = CodecsStartDecompressDataType.STRIPS;
/*  32 */     this._format = RasterImageFormat.UNKNOWN;
/*  33 */     this._width = 0;
/*  34 */     this._height = 0;
/*  35 */     this._bitsPerPixel = 0;
/*  36 */     this._viewPerspective = RasterViewPerspective.TOP_LEFT;
/*  37 */     this._rawOrder = RasterByteOrder.BGR;
/*  38 */     this._loadOrder = CodecsLoadByteOrder.BGR_OR_GRAY;
/*  39 */     this._xResolution = 0;
/*  40 */     this._yResolution = 0;
/*  41 */     this._palette = null;
/*  42 */     this._leastSignificantBitFirst = false;
/*  43 */     this._pad4 = false;
/*  44 */     this._usePalette = false;
/*  45 */     this._colorMask = null;
/*  46 */     this._tiffPhotometricInterpretation = CodecsTiffPhotometricInterpretation.MINIMUM_IS_WHITE;
/*  47 */     this._planarConfiguration = CodecsPlanarConfiguration.CHUNKY;
/*  48 */     this._bitmapHandle = 0L;
/*     */   }
/*     */ 
/*     */   public static CodecsStartDecompressOptions getEmpty() {
/*  52 */     return new CodecsStartDecompressOptions(0);
/*     */   }
/*     */ 
/*     */   public CodecsStartDecompressDataType getDataType()
/*     */   {
/*  57 */     return this._dataType;
/*     */   }
/*     */ 
/*     */   public void setDataType(CodecsStartDecompressDataType value) {
/*  61 */     this._dataType = value;
/*     */   }
/*     */ 
/*     */   public RasterImageFormat getFormat()
/*     */   {
/*  66 */     return this._format;
/*     */   }
/*     */ 
/*     */   public void setFormat(RasterImageFormat value) {
/*  70 */     this._format = value;
/*     */   }
/*     */ 
/*     */   public int getWidth()
/*     */   {
/*  75 */     return this._width;
/*     */   }
/*     */ 
/*     */   public void setWidth(int value) {
/*  79 */     this._width = value;
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/*  84 */     return this._height;
/*     */   }
/*     */ 
/*     */   public void setHeight(int value) {
/*  88 */     this._height = value;
/*     */   }
/*     */ 
/*     */   public int getBitsPerPixel()
/*     */   {
/*  93 */     return this._bitsPerPixel;
/*     */   }
/*     */ 
/*     */   public void setBitsPerPixel(int value) {
/*  97 */     this._bitsPerPixel = value;
/*     */   }
/*     */ 
/*     */   public RasterViewPerspective getViewPerspective()
/*     */   {
/* 102 */     return this._viewPerspective;
/*     */   }
/*     */ 
/*     */   public void setViewPerspective(RasterViewPerspective value) {
/* 106 */     this._viewPerspective = value;
/*     */   }
/*     */ 
/*     */   public RasterByteOrder getRawOrder()
/*     */   {
/* 111 */     return this._rawOrder;
/*     */   }
/*     */ 
/*     */   public void setRawOrder(RasterByteOrder value) {
/* 115 */     this._rawOrder = value;
/*     */   }
/*     */ 
/*     */   public CodecsLoadByteOrder getLoadOrder()
/*     */   {
/* 120 */     return this._loadOrder;
/*     */   }
/*     */ 
/*     */   public void setLoadOrder(CodecsLoadByteOrder value) {
/* 124 */     this._loadOrder = value;
/*     */   }
/*     */ 
/*     */   public int getXResolution()
/*     */   {
/* 129 */     return this._xResolution;
/*     */   }
/*     */ 
/*     */   public void setXResolution(int value) {
/* 133 */     this._xResolution = value;
/*     */   }
/*     */ 
/*     */   public int getYResolution()
/*     */   {
/* 138 */     return this._yResolution;
/*     */   }
/*     */ 
/*     */   public void setYResolution(int value) {
/* 142 */     this._yResolution = value;
/*     */   }
/*     */ 
/*     */   public RasterColor[] getPalette()
/*     */   {
/* 147 */     return this._palette;
/*     */   }
/*     */ 
/*     */   public void setPalette(RasterColor[] palette)
/*     */   {
/* 152 */     if ((palette != null) && (palette.length > 256))
/* 153 */       throw new IllegalArgumentException("palette must not have more than 256 values");
/* 154 */     this._palette = palette;
/*     */   }
/*     */ 
/*     */   public boolean getLeastSignificantBitFirst()
/*     */   {
/* 159 */     return this._leastSignificantBitFirst;
/*     */   }
/*     */ 
/*     */   public void setLeastSignificantBitFirst(boolean value) {
/* 163 */     this._leastSignificantBitFirst = value;
/*     */   }
/*     */ 
/*     */   public boolean getPad4()
/*     */   {
/* 168 */     return this._pad4;
/*     */   }
/*     */ 
/*     */   public void setPad4(boolean value) {
/* 172 */     this._pad4 = value;
/*     */   }
/*     */ 
/*     */   public boolean getUsePalette()
/*     */   {
/* 177 */     return this._usePalette;
/*     */   }
/*     */ 
/*     */   public void setUsePalette(boolean value) {
/* 181 */     this._usePalette = value;
/*     */   }
/*     */ 
/*     */   public int[] getColorMask()
/*     */   {
/* 186 */     return this._colorMask;
/*     */   }
/*     */ 
/*     */   public void setColorMask(int[] colorMask)
/*     */   {
/* 191 */     if ((colorMask != null) && (colorMask.length != 3))
/* 192 */       throw new IllegalArgumentException("colorMask must be an array of 3 values");
/* 193 */     this._colorMask = colorMask;
/*     */   }
/*     */ 
/*     */   public CodecsTiffPhotometricInterpretation getTiffPhotometricInterpretation()
/*     */   {
/* 198 */     return this._tiffPhotometricInterpretation;
/*     */   }
/*     */ 
/*     */   public void setTiffPhotometricInterpretation(CodecsTiffPhotometricInterpretation value) {
/* 202 */     this._tiffPhotometricInterpretation = value;
/*     */   }
/*     */ 
/*     */   public CodecsPlanarConfiguration getPlanarConfiguration()
/*     */   {
/* 207 */     return this._planarConfiguration;
/*     */   }
/*     */ 
/*     */   public void setPlanarConfiguration(CodecsPlanarConfiguration value) {
/* 211 */     this._planarConfiguration = value;
/*     */   }
/*     */ 
/*     */   public long getBitmapHandle()
/*     */   {
/* 216 */     return this._bitmapHandle;
/*     */   }
/*     */ 
/*     */   public void setBitmapHandle(long value) {
/* 220 */     this._bitmapHandle = value;
/*     */   }
/*     */ 
/*     */   public CodecsStartDecompressOptions clone()
/*     */   {
/* 225 */     CodecsStartDecompressOptions varCopy = new CodecsStartDecompressOptions(0);
/*     */ 
/* 227 */     varCopy._dataType = this._dataType;
/* 228 */     varCopy._format = this._format;
/* 229 */     varCopy._width = this._width;
/* 230 */     varCopy._height = this._height;
/* 231 */     varCopy._bitsPerPixel = this._bitsPerPixel;
/* 232 */     varCopy._viewPerspective = this._viewPerspective;
/* 233 */     varCopy._rawOrder = this._rawOrder;
/* 234 */     varCopy._loadOrder = this._loadOrder;
/* 235 */     varCopy._xResolution = this._xResolution;
/* 236 */     varCopy._yResolution = this._yResolution;
/* 237 */     varCopy._palette = this._palette;
/* 238 */     varCopy._leastSignificantBitFirst = this._leastSignificantBitFirst;
/* 239 */     varCopy._pad4 = this._pad4;
/* 240 */     varCopy._usePalette = this._usePalette;
/* 241 */     varCopy._colorMask = this._colorMask;
/* 242 */     varCopy._tiffPhotometricInterpretation = this._tiffPhotometricInterpretation;
/* 243 */     varCopy._planarConfiguration = this._planarConfiguration;
/* 244 */     varCopy._bitmapHandle = this._bitmapHandle;
/*     */ 
/* 246 */     return varCopy;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsStartDecompressOptions
 * JD-Core Version:    0.6.2
 */