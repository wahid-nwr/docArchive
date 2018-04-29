/*     */ package leadtools.codecs;
/*     */ 
/*     */ import leadtools.ArgumentOutOfRangeException;
/*     */ import leadtools.ILeadStream;
/*     */ import leadtools.LeadEvent;
/*     */ import leadtools.RasterByteOrder;
/*     */ import leadtools.RasterColor;
/*     */ import leadtools.RasterImageFormat;
/*     */ import leadtools.RasterViewPerspective;
/*     */ 
/*     */ public class CodecsLoadInformationEvent extends LeadEvent
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private ILeadStream _stream;
/*     */   private RasterColor[] _palette;
/*     */   private int[] _colorMask;
/*     */   private int _Format;
/*     */   private int _Width;
/*     */   private int _Height;
/*     */   private int _BitsPerPixel;
/*     */   private int _XResolution;
/*     */   private int _YResolution;
/*     */   private long _Offset;
/*     */   private int _Flags;
/*     */   private int _uStripSize;
/*     */   private int _nPhotoInt;
/*     */   private int _nPlanarConfig;
/*     */ 
/*     */   CodecsLoadInformationEvent(Object source, ILeadStream stream, LOADINFO loadInfo)
/*     */   {
/*  33 */     super(source);
/*     */ 
/*  35 */     this._stream = stream;
/*     */ 
/*  37 */     this._Format = loadInfo.Format;
/*  38 */     this._Width = loadInfo.Width;
/*  39 */     this._Height = loadInfo.Height;
/*  40 */     this._BitsPerPixel = loadInfo.BitsPerPixel;
/*  41 */     this._XResolution = loadInfo.XResolution;
/*  42 */     this._YResolution = loadInfo.YResolution;
/*  43 */     this._Offset = loadInfo.Offset;
/*  44 */     this._Flags = loadInfo.Flags;
/*  45 */     this._uStripSize = loadInfo.uStripSize;
/*  46 */     this._nPhotoInt = loadInfo.nPhotoInt;
/*  47 */     this._nPlanarConfig = loadInfo.nPlanarConfig;
/*     */ 
/*  49 */     if (Tools.isFlagged(this._Flags, 4096))
/*  50 */       this._palette = ((RasterColor[])loadInfo.rgbQuad.clone());
/*     */     else {
/*  52 */       this._palette = null;
/*     */     }
/*  54 */     if (Tools.isFlagged(this._Flags, 8192))
/*  55 */       this._colorMask = ((int[])loadInfo.rgbColorMask.clone());
/*     */     else
/*  57 */       this._colorMask = null;
/*     */   }
/*     */ 
/*     */   public void toUnmanaged(LOADINFO loadInfo)
/*     */   {
/*  62 */     loadInfo.Format = this._Format;
/*  63 */     loadInfo.Width = this._Width;
/*  64 */     loadInfo.Height = this._Height;
/*  65 */     loadInfo.BitsPerPixel = this._BitsPerPixel;
/*  66 */     loadInfo.XResolution = this._XResolution;
/*  67 */     loadInfo.YResolution = this._YResolution;
/*  68 */     loadInfo.Offset = this._Offset;
/*  69 */     loadInfo.Flags = this._Flags;
/*  70 */     loadInfo.uStripSize = this._uStripSize;
/*  71 */     loadInfo.nPhotoInt = this._nPhotoInt;
/*  72 */     loadInfo.nPlanarConfig = this._nPlanarConfig;
/*     */ 
/*  74 */     if ((this._palette != null) && (this._palette.length > 0))
/*     */     {
/*  76 */       loadInfo.rgbQuad = ((RasterColor[])this._palette.clone());
/*  77 */       this._Flags = Tools.setFlag1(this._Flags, 4096, true);
/*     */     }
/*     */     else
/*     */     {
/*  81 */       loadInfo.rgbQuad = null;
/*  82 */       this._Flags = Tools.setFlag1(this._Flags, 4096, false);
/*     */     }
/*     */ 
/*  85 */     if (this._colorMask != null)
/*     */     {
/*  87 */       loadInfo.rgbColorMask = ((int[])this._colorMask.clone());
/*  88 */       this._Flags = Tools.setFlag1(this._Flags, 8192, true);
/*     */     }
/*     */     else
/*     */     {
/*  92 */       loadInfo.rgbColorMask = null;
/*  93 */       this._Flags = Tools.setFlag1(this._Flags, 8192, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ILeadStream getStream()
/*     */   {
/*  99 */     return this._stream;
/*     */   }
/*     */ 
/*     */   public RasterImageFormat getFormat()
/*     */   {
/* 104 */     return RasterImageFormat.forValue(this._Format);
/*     */   }
/*     */ 
/*     */   public void setFormat(RasterImageFormat value) {
/* 108 */     this._Format = value.getValue();
/*     */   }
/*     */ 
/*     */   public int getWidth()
/*     */   {
/* 113 */     return this._Width;
/*     */   }
/*     */ 
/*     */   public void setWidth(int value) {
/* 117 */     this._Width = value;
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/* 122 */     return this._Height;
/*     */   }
/*     */ 
/*     */   public void setHeight(int value) {
/* 126 */     this._Height = value;
/*     */   }
/*     */ 
/*     */   public int getBitsPerPixel()
/*     */   {
/* 131 */     return this._BitsPerPixel;
/*     */   }
/*     */ 
/*     */   public void setBitsPerPixel(int value) {
/* 135 */     this._BitsPerPixel = value;
/*     */   }
/*     */ 
/*     */   public int getXResolution()
/*     */   {
/* 140 */     return this._XResolution;
/*     */   }
/*     */ 
/*     */   public void setXResolution(int value) {
/* 144 */     this._XResolution = value;
/*     */   }
/*     */ 
/*     */   public int getYResolution()
/*     */   {
/* 149 */     return this._YResolution;
/*     */   }
/*     */ 
/*     */   public void setYResolution(int value) {
/* 153 */     this._YResolution = value;
/*     */   }
/*     */ 
/*     */   public long getOffset()
/*     */   {
/* 158 */     return this._Offset;
/*     */   }
/*     */ 
/*     */   public void setOffset(long value) {
/* 162 */     this._Offset = value;
/*     */   }
/*     */ 
/*     */   public RasterColor[] getPalette()
/*     */   {
/* 167 */     return this._palette;
/*     */   }
/*     */ 
/*     */   public void setPalette(RasterColor[] palette) {
/* 171 */     if ((palette != null) && (palette.length > 256)) {
/* 172 */       throw new ArgumentOutOfRangeException("palette", palette.toString(), "Must be either null or an array of 256 or less values");
/*     */     }
/* 174 */     this._palette = ((RasterColor[])palette.clone());
/*     */   }
/*     */ 
/*     */   public int[] getColorMask()
/*     */   {
/* 179 */     return (int[])this._colorMask.clone();
/*     */   }
/*     */ 
/*     */   public void setColorMask(int[] colorMask)
/*     */   {
/* 184 */     if ((colorMask != null) && (colorMask.length != 3)) {
/* 185 */       throw new ArgumentOutOfRangeException("colorMask", colorMask.toString(), "Must be either null or an array of 3 values");
/*     */     }
/* 187 */     this._colorMask = ((int[])colorMask.clone());
/*     */   }
/*     */ 
/*     */   public int getStripSize()
/*     */   {
/* 192 */     return this._uStripSize;
/*     */   }
/*     */ 
/*     */   public void setStripSize(int value) {
/* 196 */     this._uStripSize = value;
/*     */   }
/*     */ 
/*     */   public CodecsPhotoInterpolation getPhotoInterpolation()
/*     */   {
/* 201 */     return CodecsPhotoInterpolation.forValue(this._nPhotoInt);
/*     */   }
/*     */ 
/*     */   public void setPhotoInterpolation(CodecsPhotoInterpolation value) {
/* 205 */     this._nPhotoInt = value.getValue();
/*     */   }
/*     */ 
/*     */   public CodecsPlanarConfiguration getPlanarConfiguration()
/*     */   {
/* 210 */     return CodecsPlanarConfiguration.forValue(this._nPlanarConfig);
/*     */   }
/*     */ 
/*     */   public void setPlanarConfiguration(CodecsPlanarConfiguration value) {
/* 214 */     this._nPlanarConfig = value.getValue();
/*     */   }
/*     */ 
/*     */   public RasterByteOrder getOrder()
/*     */   {
/* 219 */     if (Tools.isFlagged(this._Flags, 2))
/* 220 */       return RasterByteOrder.RGB;
/* 221 */     if (Tools.isFlagged(this._Flags, 16384))
/* 222 */       return RasterByteOrder.GRAY;
/* 223 */     if (Tools.isFlagged(this._Flags, 65536)) {
/* 224 */       return RasterByteOrder.ROMM;
/*     */     }
/* 226 */     return RasterByteOrder.BGR;
/*     */   }
/*     */ 
/*     */   public void setOrder(RasterByteOrder value) {
/* 230 */     this._Flags = Tools.setFlag1(this._Flags, 2, false);
/* 231 */     this._Flags = Tools.setFlag1(this._Flags, 16384, false);
/* 232 */     this._Flags = Tools.setFlag1(this._Flags, 65536, false);
/*     */ 
/* 234 */     switch (1.$SwitchMap$leadtools$RasterByteOrder[value.ordinal()])
/*     */     {
/*     */     case 1:
/* 237 */       this._Flags = Tools.setFlag1(this._Flags, 2, true);
/* 238 */       break;
/*     */     case 2:
/* 240 */       this._Flags = Tools.setFlag1(this._Flags, 16384, true);
/* 241 */       break;
/*     */     case 3:
/* 243 */       this._Flags = Tools.setFlag1(this._Flags, 65536, true);
/* 244 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public RasterViewPerspective getViewPerspective()
/*     */   {
/* 252 */     if (Tools.isFlagged(this._Flags, 1))
/* 253 */       return RasterViewPerspective.TOP_LEFT;
/* 254 */     if (Tools.isFlagged(this._Flags, 16))
/* 255 */       return RasterViewPerspective.TOP_LEFT_90;
/* 256 */     if (Tools.isFlagged(this._Flags, 32))
/* 257 */       return RasterViewPerspective.TOP_LEFT_270;
/* 258 */     if (Tools.isFlagged(this._Flags, 128))
/* 259 */       return RasterViewPerspective.TOP_LEFT_180;
/* 260 */     if (Tools.isFlagged(this._Flags, 256))
/* 261 */       return RasterViewPerspective.BOTTOM_LEFT_90;
/* 262 */     if (Tools.isFlagged(this._Flags, 512))
/* 263 */       return RasterViewPerspective.BOTTOM_LEFT_180;
/* 264 */     if (Tools.isFlagged(this._Flags, 1024)) {
/* 265 */       return RasterViewPerspective.BOTTOM_LEFT_270;
/*     */     }
/* 267 */     return RasterViewPerspective.BOTTOM_LEFT;
/*     */   }
/*     */ 
/*     */   public void setViewPerspective(RasterViewPerspective value) {
/* 271 */     this._Flags = Tools.setFlag1(this._Flags, 1, false);
/* 272 */     this._Flags = Tools.setFlag1(this._Flags, 16, false);
/* 273 */     this._Flags = Tools.setFlag1(this._Flags, 32, false);
/* 274 */     this._Flags = Tools.setFlag1(this._Flags, 128, false);
/* 275 */     this._Flags = Tools.setFlag1(this._Flags, 256, false);
/* 276 */     this._Flags = Tools.setFlag1(this._Flags, 512, false);
/* 277 */     this._Flags = Tools.setFlag1(this._Flags, 1024, false);
/*     */ 
/* 279 */     switch (1.$SwitchMap$leadtools$RasterViewPerspective[value.ordinal()])
/*     */     {
/*     */     case 1:
/* 282 */       this._Flags = Tools.setFlag1(this._Flags, 1, true);
/* 283 */       break;
/*     */     case 2:
/* 285 */       this._Flags = Tools.setFlag1(this._Flags, 16, true);
/* 286 */       break;
/*     */     case 3:
/* 288 */       this._Flags = Tools.setFlag1(this._Flags, 32, true);
/* 289 */       break;
/*     */     case 4:
/* 291 */       this._Flags = Tools.setFlag1(this._Flags, 128, true);
/* 292 */       break;
/*     */     case 5:
/* 294 */       this._Flags = Tools.setFlag1(this._Flags, 256, true);
/* 295 */       break;
/*     */     case 6:
/* 297 */       this._Flags = Tools.setFlag1(this._Flags, 512, true);
/* 298 */       break;
/*     */     case 7:
/* 300 */       this._Flags = Tools.setFlag1(this._Flags, 1024, true);
/* 301 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getWhiteOnBlack()
/*     */   {
/* 309 */     return Tools.isFlagged(this._Flags, 4);
/*     */   }
/*     */ 
/*     */   public void setWhiteOnBlack(boolean value) {
/* 313 */     this._Flags = Tools.setFlag1(this._Flags, 4, value);
/*     */   }
/*     */ 
/*     */   public boolean getLeastSignificantBitFirst()
/*     */   {
/* 318 */     return Tools.isFlagged(this._Flags, 8);
/*     */   }
/*     */ 
/*     */   public void setLeastSignificantBitFirst(boolean value) {
/* 322 */     this._Flags = Tools.setFlag1(this._Flags, 8, value);
/*     */   }
/*     */ 
/*     */   public boolean getReverse()
/*     */   {
/* 327 */     return Tools.isFlagged(this._Flags, 64);
/*     */   }
/*     */ 
/*     */   public void setReverse(boolean value) {
/* 331 */     this._Flags = Tools.setFlag1(this._Flags, 64, value);
/*     */   }
/*     */ 
/*     */   public boolean getPad4()
/*     */   {
/* 336 */     return Tools.isFlagged(this._Flags, 2048);
/*     */   }
/*     */ 
/*     */   public void setPad4(boolean value) {
/* 340 */     this._Flags = Tools.setFlag1(this._Flags, 2048, value);
/*     */   }
/*     */ 
/*     */   public boolean getMotorolaOrder()
/*     */   {
/* 345 */     return Tools.isFlagged(this._Flags, 32768);
/*     */   }
/*     */ 
/*     */   public void setMotorolaOrder(boolean value) {
/* 349 */     this._Flags = Tools.setFlag1(this._Flags, 32768, value);
/*     */   }
/*     */ 
/*     */   public boolean getSigned() {
/* 353 */     return Tools.isFlagged(this._Flags, 131072);
/*     */   }
/*     */ 
/*     */   public void setSigned(boolean value) {
/* 357 */     this._Flags = Tools.setFlag1(this._Flags, 131072, value);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsLoadInformationEvent
 * JD-Core Version:    0.6.2
 */