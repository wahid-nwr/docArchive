/*     */ package leadtools.codecs;
/*     */ 
/*     */ import leadtools.RasterByteOrder;
/*     */ import leadtools.RasterColor;
/*     */ import leadtools.RasterDitheringMethod;
/*     */ import leadtools.imageprocessing.ColorResolutionCommandPaletteFlags;
/*     */ 
/*     */ public final class CodecsThumbnailOptions
/*     */ {
/*     */   private int _nWidth;
/*     */   private int _nHeight;
/*     */   private int _nBits;
/*     */   private int _uCRFlags;
/*     */   private boolean _bMaintainAspect;
/*     */   private boolean _bForceSize;
/*     */   private int _crBackColor;
/*     */   private boolean _bLoadStamp;
/*     */   private boolean _bResample;
/*     */   private RasterByteOrder _order;
/*     */   private RasterDitheringMethod _ditheringMethod;
/*     */   private ColorResolutionCommandPaletteFlags _paletteFlags;
/*     */ 
/*     */   private CodecsThumbnailOptions()
/*     */   {
/*  25 */     this._nWidth = 80;
/*  26 */     this._nHeight = 80;
/*  27 */     this._nBits = 24;
/*  28 */     this._uCRFlags = 0;
/*  29 */     this._bMaintainAspect = true;
/*  30 */     this._bForceSize = false;
/*  31 */     this._crBackColor = 0;
/*  32 */     this._bLoadStamp = true;
/*  33 */     this._bResample = false;
/*     */ 
/*  35 */     this._order = RasterByteOrder.BGR;
/*  36 */     this._ditheringMethod = RasterDitheringMethod.NONE;
/*  37 */     this._paletteFlags = ColorResolutionCommandPaletteFlags.NONE;
/*     */   }
/*     */ 
/*     */   public static CodecsThumbnailOptions getDefault() {
/*  41 */     return new CodecsThumbnailOptions();
/*     */   }
/*     */ 
/*     */   public int getFlags()
/*     */   {
/*  46 */     int flags = 0;
/*     */ 
/*  48 */     switch (1.$SwitchMap$leadtools$RasterByteOrder[getOrder().ordinal()])
/*     */     {
/*     */     case 1:
/*  51 */       flags |= 4;
/*  52 */       break;
/*     */     case 2:
/*  54 */       flags |= 0;
/*  55 */       break;
/*     */     case 3:
/*  57 */       flags |= 128;
/*  58 */       break;
/*     */     case 4:
/*  60 */       flags |= 2048;
/*  61 */       break;
/*     */     }
/*     */ 
/*  66 */     switch (1.$SwitchMap$leadtools$RasterDitheringMethod[getDitheringMethod().ordinal()])
/*     */     {
/*     */     case 1:
/*  69 */       flags |= 0;
/*  70 */       break;
/*     */     case 2:
/*  72 */       flags |= 65536;
/*  73 */       break;
/*     */     case 3:
/*  75 */       flags |= 131072;
/*  76 */       break;
/*     */     case 4:
/*  78 */       flags |= 196608;
/*  79 */       break;
/*     */     case 5:
/*  81 */       flags |= 262144;
/*  82 */       break;
/*     */     case 6:
/*  84 */       flags |= 327680;
/*  85 */       break;
/*     */     case 7:
/*  87 */       flags |= 393216;
/*  88 */       break;
/*     */     case 8:
/*  90 */       flags |= 458752;
/*  91 */       break;
/*     */     case 9:
/*  93 */       flags |= 524288;
/*     */     }
/*     */ 
/*  97 */     flags |= getPaletteFlags().getValue();
/*     */ 
/*  99 */     return flags;
/*     */   }
/*     */ 
/*     */   public int getWidth()
/*     */   {
/* 104 */     return this._nWidth;
/*     */   }
/*     */ 
/*     */   public void setWidth(int value) {
/* 108 */     this._nWidth = value;
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/* 113 */     return this._nHeight;
/*     */   }
/*     */ 
/*     */   public void setHeight(int value) {
/* 117 */     this._nHeight = value;
/*     */   }
/*     */ 
/*     */   public int getBitsPerPixel()
/*     */   {
/* 122 */     return this._nBits;
/*     */   }
/*     */ 
/*     */   public void setBitsPerPixel(int value) {
/* 126 */     this._nBits = value;
/*     */   }
/*     */ 
/*     */   public RasterByteOrder getOrder()
/*     */   {
/* 131 */     return this._order;
/*     */   }
/*     */ 
/*     */   public void setOrder(RasterByteOrder value) {
/* 135 */     this._order = value;
/*     */   }
/*     */ 
/*     */   public RasterDitheringMethod getDitheringMethod()
/*     */   {
/* 140 */     return this._ditheringMethod;
/*     */   }
/*     */ 
/*     */   public void setDitheringMethod(RasterDitheringMethod value) {
/* 144 */     this._ditheringMethod = value;
/*     */   }
/*     */ 
/*     */   public ColorResolutionCommandPaletteFlags getPaletteFlags()
/*     */   {
/* 149 */     return this._paletteFlags;
/*     */   }
/*     */ 
/*     */   public void setPaletteFlags(ColorResolutionCommandPaletteFlags value) {
/* 153 */     this._paletteFlags = value;
/*     */   }
/*     */ 
/*     */   public boolean getMaintainAspectRatio()
/*     */   {
/* 158 */     return this._bMaintainAspect;
/*     */   }
/*     */ 
/*     */   public void setMaintainAspectRatio(boolean value) {
/* 162 */     this._bMaintainAspect = value;
/*     */   }
/*     */ 
/*     */   public boolean getForceSize()
/*     */   {
/* 167 */     return this._bForceSize;
/*     */   }
/*     */ 
/*     */   public void setForceSize(boolean value) {
/* 171 */     this._bForceSize = value;
/*     */   }
/*     */ 
/*     */   public RasterColor getBackColor()
/*     */   {
/* 176 */     return RasterColor.fromColorRef(this._crBackColor);
/*     */   }
/*     */ 
/*     */   public void setBackColor(RasterColor value) {
/* 180 */     this._crBackColor = value.getColorRef();
/*     */   }
/*     */ 
/*     */   public boolean getLoadStamp()
/*     */   {
/* 185 */     return this._bLoadStamp;
/*     */   }
/*     */ 
/*     */   public void setLoadStamp(boolean value) {
/* 189 */     this._bLoadStamp = value;
/*     */   }
/*     */ 
/*     */   public boolean getResample()
/*     */   {
/* 194 */     return this._bResample;
/*     */   }
/*     */ 
/*     */   public void setResample(boolean value) {
/* 198 */     this._bResample = value;
/*     */   }
/*     */ 
/*     */   public CodecsThumbnailOptions clone()
/*     */   {
/* 203 */     CodecsThumbnailOptions varCopy = new CodecsThumbnailOptions();
/*     */ 
/* 205 */     varCopy._nWidth = this._nWidth;
/* 206 */     varCopy._nHeight = this._nHeight;
/* 207 */     varCopy._nBits = this._nBits;
/* 208 */     varCopy._uCRFlags = this._uCRFlags;
/* 209 */     varCopy._bMaintainAspect = this._bMaintainAspect;
/* 210 */     varCopy._bForceSize = this._bForceSize;
/* 211 */     varCopy._crBackColor = this._crBackColor;
/* 212 */     varCopy._bLoadStamp = this._bLoadStamp;
/* 213 */     varCopy._bResample = this._bResample;
/* 214 */     varCopy._order = this._order;
/* 215 */     varCopy._ditheringMethod = this._ditheringMethod;
/* 216 */     varCopy._paletteFlags = this._paletteFlags;
/*     */ 
/* 218 */     return varCopy;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsThumbnailOptions
 * JD-Core Version:    0.6.2
 */