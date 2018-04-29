/*     */ package leadtools.codecs;
/*     */ 
/*     */ import leadtools.RasterColor;
/*     */ import leadtools.internal.IsInternal;
/*     */ 
/*     */ @IsInternal
/*     */ class LOADINFO
/*     */ {
/*     */   public int uStructSize;
/*     */   public int Format;
/*     */   public int Width;
/*     */   public int Height;
/*     */   public int BitsPerPixel;
/*     */   public int XResolution;
/*     */   public int YResolution;
/*     */   public long Offset;
/*     */   public int Flags;
/* 124 */   public RasterColor[] rgbQuad = new RasterColor[256];
/*     */ 
/* 127 */   public int[] rgbColorMask = new int[3];
/*     */   public int uStripSize;
/*     */   public int nPhotoInt;
/*     */   public int nPlanarConfig;
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.LOADINFO
 * JD-Core Version:    0.6.2
 */