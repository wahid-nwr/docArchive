/*     */ package leadtools.codecs;
/*     */ 
/*     */ import leadtools.RasterImageFormat;
/*     */ 
/*     */ public final class CodecsRasterPdfInfo
/*     */ {
/*     */   private boolean _bIsLeadPdf;
/*     */   private int _nBitsPerPixel;
/*     */   private int _Format;
/*     */   private int _nWidth;
/*     */   private int _nHeight;
/*     */   private int _XResolution;
/*     */   private int _YResolution;
/*     */   private int _Version;
/*     */ 
/*     */   public boolean isLeadPdf()
/*     */   {
/*  22 */     return this._bIsLeadPdf;
/*     */   }
/*     */ 
/*     */   public void setIsLeadPdf(boolean value) {
/*  26 */     this._bIsLeadPdf = value;
/*     */   }
/*     */ 
/*     */   public int getBitsPerPixel()
/*     */   {
/*  31 */     return this._nBitsPerPixel;
/*     */   }
/*     */ 
/*     */   public void setBitsPerPixel(int value) {
/*  35 */     this._nBitsPerPixel = value;
/*     */   }
/*     */ 
/*     */   public RasterImageFormat getFormat()
/*     */   {
/*  40 */     return RasterImageFormat.forValue(this._Format);
/*     */   }
/*     */ 
/*     */   public void setFormat(RasterImageFormat value) {
/*  44 */     this._Format = value.getValue();
/*     */   }
/*     */ 
/*     */   public int getWidth()
/*     */   {
/*  49 */     return this._nWidth;
/*     */   }
/*     */ 
/*     */   public void setWidth(int value) {
/*  53 */     this._nWidth = value;
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/*  58 */     return this._nHeight;
/*     */   }
/*     */ 
/*     */   public void setHeight(int value) {
/*  62 */     this._nHeight = value;
/*     */   }
/*     */ 
/*     */   public int getXResolution()
/*     */   {
/*  67 */     return this._XResolution;
/*     */   }
/*     */ 
/*     */   public void setXResolution(int value) {
/*  71 */     this._XResolution = value;
/*     */   }
/*     */ 
/*     */   public int getYResolution()
/*     */   {
/*  76 */     return this._YResolution;
/*     */   }
/*     */ 
/*     */   public void setYResolution(int value) {
/*  80 */     this._YResolution = value;
/*     */   }
/*     */ 
/*     */   public CodecsRasterPdfVersion getVersion()
/*     */   {
/*  85 */     return CodecsRasterPdfVersion.forValue(this._Version);
/*     */   }
/*     */ 
/*     */   public void setVersion(CodecsRasterPdfVersion value) {
/*  89 */     this._Version = value.getValue();
/*     */   }
/*     */ 
/*     */   public CodecsRasterPdfInfo clone()
/*     */   {
/*  94 */     CodecsRasterPdfInfo varCopy = new CodecsRasterPdfInfo();
/*     */ 
/*  96 */     varCopy._bIsLeadPdf = this._bIsLeadPdf;
/*  97 */     varCopy._nBitsPerPixel = this._nBitsPerPixel;
/*  98 */     varCopy._Format = this._Format;
/*  99 */     varCopy._nWidth = this._nWidth;
/* 100 */     varCopy._nHeight = this._nHeight;
/* 101 */     varCopy._XResolution = this._XResolution;
/* 102 */     varCopy._YResolution = this._YResolution;
/* 103 */     varCopy._Version = this._Version;
/*     */ 
/* 105 */     return varCopy;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRasterPdfInfo
 * JD-Core Version:    0.6.2
 */