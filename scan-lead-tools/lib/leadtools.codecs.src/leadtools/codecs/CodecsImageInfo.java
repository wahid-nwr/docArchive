/*     */ package leadtools.codecs;
/*     */ 
/*     */ import leadtools.RasterByteOrder;
/*     */ import leadtools.RasterColor;
/*     */ import leadtools.RasterImageFormat;
/*     */ import leadtools.RasterViewPerspective;
/*     */ 
/*     */ public class CodecsImageInfo
/*     */ {
/*     */   private FILEINFO _fileInfo;
/*     */   private RasterColor[] _palette;
/*     */   private CodecsGifImageInfo _gif;
/*     */   private CodecsFaxImageInfo _fax;
/*     */   private CodecsJpegImageInfo _jpeg;
/*     */   private CodecsTiffImageInfo _tiff;
/*     */   private CodecsPsdImageInfo _psd;
/*     */   private CodecsDocumentImageInfo _document;
/*     */   private CodecsVectorImageInfo _vector;
/*     */   private CodecsPstImageInfo _pst;
/*     */   private boolean _isLoading;
/*     */ 
/*     */   final FILEINFO getFileInfo()
/*     */   {
/*  23 */     return this._fileInfo;
/*     */   }
/*     */ 
/*     */   public CodecsImageInfo()
/*     */   {
/*  28 */     FILEINFO fileInfo = new FILEINFO();
/*  29 */     initialize(fileInfo);
/*     */   }
/*     */ 
/*     */   CodecsImageInfo(FILEINFO fileInfo) {
/*  33 */     initialize(fileInfo);
/*     */   }
/*     */ 
/*     */   final void initialize(FILEINFO fileInfo)
/*     */   {
/*  38 */     this._fileInfo = fileInfo.clone();
/*     */ 
/*  40 */     this._gif = new CodecsGifImageInfo(this._fileInfo);
/*  41 */     this._fax = new CodecsFaxImageInfo(this._fileInfo);
/*  42 */     this._jpeg = new CodecsJpegImageInfo(this._fileInfo);
/*  43 */     this._tiff = new CodecsTiffImageInfo(this._fileInfo);
/*  44 */     this._psd = new CodecsPsdImageInfo(this._fileInfo);
/*  45 */     this._psd = new CodecsPsdImageInfo(this._fileInfo);
/*  46 */     this._document = new CodecsDocumentImageInfo(this._fileInfo);
/*  47 */     this._vector = new CodecsVectorImageInfo(this._fileInfo);
/*  48 */     this._pst = new CodecsPstImageInfo(this._fileInfo);
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable
/*     */   {
/*  53 */     if (this._fileInfo != null)
/*  54 */       this._fileInfo = null;
/*     */   }
/*     */ 
/*     */   public CodecsImageInfo clone()
/*     */   {
/*  59 */     CodecsImageInfo dest = new CodecsImageInfo();
/*  60 */     dest._fileInfo = this._fileInfo.clone();
/*  61 */     dest.setPalette(this._palette);
/*  62 */     return dest;
/*     */   }
/*     */ 
/*     */   public RasterImageFormat getFormat()
/*     */   {
/*  67 */     return RasterImageFormat.forValue(this._fileInfo.Format);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  72 */     return new String(this._fileInfo.Name);
/*     */   }
/*     */ 
/*     */   public int getWidth()
/*     */   {
/*  77 */     return this._fileInfo.Width;
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/*  82 */     return this._fileInfo.Height;
/*     */   }
/*     */ 
/*     */   public int getBitsPerPixel()
/*     */   {
/*  87 */     return this._fileInfo.BitsPerPixel;
/*     */   }
/*     */ 
/*     */   private int DIB_WIDTH_BYTES(int b)
/*     */   {
/*  92 */     return (b + 7) / 8;
/*     */   }
/*     */ 
/*     */   public int getBytesPerLine()
/*     */   {
/*  97 */     return DIB_WIDTH_BYTES(this._fileInfo.Width * this._fileInfo.BitsPerPixel);
/*     */   }
/*     */ 
/*     */   public long getSizeDisk()
/*     */   {
/* 102 */     return this._fileInfo.SizeDisk;
/*     */   }
/*     */ 
/*     */   public long getSizeMemory()
/*     */   {
/* 107 */     return this._fileInfo.SizeMem;
/*     */   }
/*     */ 
/*     */   public String getCompression()
/*     */   {
/* 112 */     return new String(this._fileInfo.Compression);
/*     */   }
/*     */ 
/*     */   public RasterViewPerspective getViewPerspective()
/*     */   {
/* 117 */     return RasterViewPerspective.forValue(this._fileInfo.ViewPerspective);
/*     */   }
/*     */ 
/*     */   public RasterByteOrder getOrder()
/*     */   {
/* 122 */     return RasterByteOrder.forValue(this._fileInfo.Order);
/*     */   }
/*     */ 
/*     */   public CodecsColorSpaceType getColorSpace()
/*     */   {
/* 127 */     return CodecsColorSpaceType.forValue(this._fileInfo.ColorSpace);
/*     */   }
/*     */ 
/*     */   public int getPageNumber()
/*     */   {
/* 132 */     return this._fileInfo.PageNumber;
/*     */   }
/*     */ 
/*     */   public int getTotalPages()
/*     */   {
/* 137 */     return this._fileInfo.TotalPages;
/*     */   }
/*     */ 
/*     */   public boolean hasResolution()
/*     */   {
/* 142 */     return !Tools.isFlagged(this._fileInfo.Flags, 131072);
/*     */   }
/*     */ 
/*     */   public int getXResolution()
/*     */   {
/* 147 */     return this._fileInfo.XResolution;
/*     */   }
/*     */ 
/*     */   public int getYResolution()
/*     */   {
/* 152 */     return this._fileInfo.YResolution;
/*     */   }
/*     */ 
/*     */   public boolean isRotated()
/*     */   {
/* 157 */     return Tools.isFlagged(this._fileInfo.Flags, 256);
/*     */   }
/*     */ 
/*     */   public boolean isSigned()
/*     */   {
/* 162 */     return Tools.isFlagged(this._fileInfo.Flags, 512);
/*     */   }
/*     */ 
/*     */   public boolean hasAlpha()
/*     */   {
/* 167 */     return Tools.isFlagged(this._fileInfo.Flags, 2048);
/*     */   }
/*     */ 
/*     */   public boolean isLink()
/*     */   {
/* 172 */     return Tools.isFlagged(this._fileInfo.Flags, 16384);
/*     */   }
/*     */ 
/*     */   public RasterColor[] getPalette()
/*     */   {
/* 177 */     return this._palette;
/*     */   }
/*     */ 
/*     */   void setPalette(RasterColor[] palette)
/*     */   {
/* 182 */     if (palette != null)
/* 183 */       this._palette = ((RasterColor[])palette.clone());
/*     */     else
/* 185 */       this._palette = null;
/*     */   }
/*     */ 
/*     */   public CodecsGifImageInfo getGif()
/*     */   {
/* 190 */     return this._gif;
/*     */   }
/*     */ 
/*     */   public CodecsFaxImageInfo getFax()
/*     */   {
/* 195 */     return this._fax;
/*     */   }
/*     */ 
/*     */   public CodecsJpegImageInfo getJpeg()
/*     */   {
/* 200 */     return this._jpeg;
/*     */   }
/*     */ 
/*     */   public CodecsTiffImageInfo getTiff()
/*     */   {
/* 205 */     return this._tiff;
/*     */   }
/*     */ 
/*     */   public CodecsPsdImageInfo getPsd()
/*     */   {
/* 210 */     return this._psd;
/*     */   }
/*     */ 
/*     */   public CodecsDocumentImageInfo getDocument()
/*     */   {
/* 215 */     return this._document;
/*     */   }
/*     */ 
/*     */   public CodecsVectorImageInfo getVector()
/*     */   {
/* 220 */     return this._vector;
/*     */   }
/*     */ 
/*     */   public CodecsPstImageInfo getPst()
/*     */   {
/* 225 */     return this._pst;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 231 */     String eol = System.getProperty("line.separator");
/* 232 */     StringBuilder sb = new StringBuilder();
/* 233 */     sb.append(String.format("Format: %1$s%2$s", new Object[] { getFormat(), eol }));
/* 234 */     sb.append(String.format("Name: %1$s%2$s", new Object[] { getName(), eol }));
/* 235 */     sb.append(String.format("Width: %1$s%2$s", new Object[] { Integer.valueOf(getWidth()), eol }));
/* 236 */     sb.append(String.format("Height: %1$s%2$s", new Object[] { Integer.valueOf(getHeight()), eol }));
/* 237 */     sb.append(String.format("BitsPerPixel: %1$s%2$s", new Object[] { Integer.valueOf(getBitsPerPixel()), eol }));
/* 238 */     sb.append(String.format("SizeDisk: %1$s%2$s", new Object[] { Long.valueOf(getSizeDisk()), eol }));
/* 239 */     sb.append(String.format("SizeMemory: %1$s%2$s", new Object[] { Long.valueOf(getSizeMemory()), eol }));
/* 240 */     sb.append(String.format("Compression: %1$s%2$s", new Object[] { getCompression(), eol }));
/* 241 */     sb.append(String.format("ViewPerspective: %1$s%2$s", new Object[] { getViewPerspective(), eol }));
/* 242 */     sb.append(String.format("Order: %1$s%2$s", new Object[] { getOrder(), eol }));
/* 243 */     sb.append(String.format("PageNumber: %1$s%2$s", new Object[] { Integer.valueOf(getPageNumber()), eol }));
/* 244 */     sb.append(String.format("TotalPages: %1$s%2$s", new Object[] { Integer.valueOf(getTotalPages()), eol }));
/* 245 */     sb.append(String.format("HasResolution: %1$s%2$s", new Object[] { Boolean.valueOf(hasResolution()), eol }));
/* 246 */     sb.append(String.format("XResolution: %1$s%2$s", new Object[] { Integer.valueOf(getXResolution()), eol }));
/* 247 */     sb.append(String.format("YResolution: %1$s%2$s", new Object[] { Integer.valueOf(getYResolution()), eol }));
/* 248 */     sb.append(String.format("IsRotated: %1$s%2$s", new Object[] { Boolean.valueOf(isRotated()), eol }));
/* 249 */     sb.append(String.format("IsSigned: %1$s%2$s", new Object[] { Boolean.valueOf(isSigned()), eol }));
/* 250 */     sb.append(String.format("HasAlpha: %1$s%2$s", new Object[] { Boolean.valueOf(hasAlpha()), eol }));
/* 251 */     sb.append(String.format("IsLink: %1$s%2$s", new Object[] { Boolean.valueOf(isLink()), eol }));
/*     */ 
/* 253 */     sb.append(String.format("Gif%s", new Object[] { eol }));
/* 254 */     sb.append(String.format("   HasAnimationLoop: %1$s%2$s", new Object[] { Boolean.valueOf(getGif().hasAnimationLoop()), eol }));
/* 255 */     sb.append(String.format("   AnimationLoop: %1$s%2$s", new Object[] { Integer.valueOf(getGif().getAnimationLoop()), eol }));
/* 256 */     sb.append(String.format("   AnimationWidth: %1$s%2$s", new Object[] { Integer.valueOf(getGif().getAnimationWidth()), eol }));
/* 257 */     sb.append(String.format("   AnimationHeight: %1$s%2$s", new Object[] { Integer.valueOf(getGif().getAnimationHeight()), eol }));
/* 258 */     sb.append(String.format("   HasAnimationBackground: %1$s%2$s", new Object[] { Boolean.valueOf(getGif().hasAnimationBackground()), eol }));
/* 259 */     sb.append(String.format("   AnimationBackground: %1$s%2$s", new Object[] { getGif().getAnimationBackground(), eol }));
/* 260 */     sb.append(String.format("   HasAnimationPalette: %1$s%2$s", new Object[] { Boolean.valueOf(getGif().hasAnimationPalette()), eol }));
/* 261 */     sb.append(String.format("   AnimationPalette: %1$s colors%2$s", new Object[] { Integer.valueOf(getGif().getAnimationPalette().length), eol }));
/* 262 */     sb.append(String.format("   IsInterlaced: %1$s%2$s", new Object[] { Boolean.valueOf(getGif().isInterlaced()), eol }));
/*     */ 
/* 264 */     sb.append(String.format("Fax%s", new Object[] { eol }));
/* 265 */     sb.append(String.format("   IsCompressed: %1$s%2$s", new Object[] { Boolean.valueOf(getFax().isCompressed()), eol }));
/*     */ 
/* 267 */     sb.append(String.format("Tiff%s", new Object[] { eol }));
/* 268 */     sb.append(String.format("   HasNoPalette: %1$s%2$s", new Object[] { Boolean.valueOf(getTiff().hasNoPalette()), eol }));
/* 269 */     sb.append(String.format("   IsImageFileDirectoryOffsetValid: %1$s%2$s", new Object[] { Boolean.valueOf(getTiff().isImageFileDirectoryOffsetValid()), eol }));
/* 270 */     sb.append(String.format("   ImageFileDirectoryOffset: %1$s%2$s", new Object[] { Long.valueOf(getTiff().getImageFileDirectoryOffset()), eol }));
/*     */ 
/* 272 */     sb.append(String.format("Jpeg%s", new Object[] { eol }));
/* 273 */     sb.append(String.format("   IsProgressive: %1$s%2$s", new Object[] { Boolean.valueOf(getJpeg().isProgressive()), eol }));
/* 274 */     sb.append(String.format("   IsLossless: %1$s%2$s", new Object[] { Boolean.valueOf(getJpeg().isLossless()), eol }));
/* 275 */     sb.append(String.format("   HasStamp: %1$s%2$s", new Object[] { Boolean.valueOf(getJpeg().hasStamp()), eol }));
/*     */ 
/* 277 */     sb.append(String.format("Psd:%s", new Object[] { eol }));
/* 278 */     sb.append(String.format("   Layers: %1$s%2$s", new Object[] { Integer.valueOf(getPsd().getLayers()), eol }));
/* 279 */     sb.append(String.format("   Channels: %1$s%2$s", new Object[] { Integer.valueOf(getPsd().getChannels()), eol }));
/*     */ 
/* 281 */     sb.append(String.format("Document:%s", new Object[] { eol }));
/* 282 */     sb.append(String.format("   IsDocumentFile: %1$s%2$s", new Object[] { Boolean.valueOf(getDocument().isDocumentFile()), eol }));
/* 283 */     sb.append(String.format("   PageWidth:%1$s%2$s", new Object[] { Double.valueOf(getDocument().getPageWidth()), eol }));
/* 284 */     sb.append(String.format("   PageHeight: %1$s%2$s", new Object[] { Double.valueOf(getDocument().getPageHeight()), eol }));
/* 285 */     sb.append(String.format("   Unit: %1$s%2$s", new Object[] { getDocument().getUnit(), eol }));
/*     */ 
/* 287 */     sb.append(String.format("Vector:%s", new Object[] { eol }));
/* 288 */     sb.append(String.format("   IsVectorFile: %1$s%2$s", new Object[] { Boolean.valueOf(getVector().isVectorFile()), eol }));
/* 289 */     sb.append(String.format("   Unit: %1$s%2$s", new Object[] { getVector().getUnit(), eol }));
/* 290 */     sb.append(String.format("   ParallelogramMinX: %1$s%2$s", new Object[] { Double.valueOf(getVector().getParallelogramMinX()), eol }));
/* 291 */     sb.append(String.format("   ParallelogramMinY: %1$s%2$s", new Object[] { Double.valueOf(getVector().getParallelogramMinY()), eol }));
/* 292 */     sb.append(String.format("   ParallelogramMinZ: %1$s%2$s", new Object[] { Double.valueOf(getVector().getParallelogramMinZ()), eol }));
/* 293 */     sb.append(String.format("   ParallelogramMaxX: %1$s%2$s", new Object[] { Double.valueOf(getVector().getParallelogramMaxX()), eol }));
/* 294 */     sb.append(String.format("   ParallelogramMaxY: %1$s%2$s", new Object[] { Double.valueOf(getVector().getParallelogramMaxY()), eol }));
/* 295 */     sb.append(String.format("   ParallelogramMaxZ: %1$s%2$s", new Object[] { Double.valueOf(getVector().getParallelogramMaxZ()), eol }));
/*     */ 
/* 297 */     sb.append(String.format("Pst:%s", new Object[] { eol }));
/* 298 */     sb.append(String.format("   MessageCount: %1$s%2$s", new Object[] { Integer.valueOf(getPst().getMessageCount()), eol }));
/*     */ 
/* 300 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public boolean isLoading()
/*     */   {
/* 307 */     return this._isLoading;
/*     */   }
/*     */ 
/*     */   final void finishLoading()
/*     */   {
/* 312 */     this._isLoading = false;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsImageInfo
 * JD-Core Version:    0.6.2
 */