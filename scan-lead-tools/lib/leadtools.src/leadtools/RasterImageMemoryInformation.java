/*     */ package leadtools;
/*     */ 
/*     */ public final class RasterImageMemoryInformation
/*     */ {
/*     */   private int _memoryFlags;
/*     */   private long _tileSize;
/*     */   private int _totalTiles;
/*     */   private int _conventionalTiles;
/*     */   private int _maximumTileViews;
/*     */   private int _tileViews;
/*     */   private int _flags;
/*     */ 
/*     */   private RasterImageMemoryInformation()
/*     */   {
/*  15 */     this._memoryFlags = RasterMemoryFlags.CONVENTIONAL.getValue();
/*  16 */     this._tileSize = 0L;
/*  17 */     this._totalTiles = 0;
/*  18 */     this._conventionalTiles = 0;
/*  19 */     this._maximumTileViews = 0;
/*  20 */     this._tileViews = 0;
/*  21 */     this._flags = RasterImageInformationFlags.NONE.getValue();
/*     */   }
/*     */ 
/*     */   public static RasterImageMemoryInformation getEmpty()
/*     */   {
/*  26 */     RasterImageMemoryInformation info = new RasterImageMemoryInformation();
/*  27 */     return info;
/*     */   }
/*     */ 
/*     */   public int getMemoryFlags()
/*     */   {
/*  32 */     return this._memoryFlags;
/*     */   }
/*     */ 
/*     */   public void setMemoryFlags(int value) {
/*  36 */     this._memoryFlags = value;
/*     */   }
/*     */ 
/*     */   public int getTileSize()
/*     */   {
/*  41 */     return (int)this._tileSize;
/*     */   }
/*     */ 
/*     */   public void setTileSize(int value) {
/*  45 */     if (value < 0)
/*  46 */       throw new ArgumentOutOfRangeException("value", value, "Must be greater than zero");
/*  47 */     this._tileSize = value;
/*     */   }
/*     */ 
/*     */   public int getTotalTiles()
/*     */   {
/*  52 */     return this._totalTiles;
/*     */   }
/*     */ 
/*     */   public void setTotalTiles(int value) {
/*  56 */     if (value < 0)
/*  57 */       throw new ArgumentOutOfRangeException("value", value, "Must be greater than zero");
/*  58 */     this._totalTiles = value;
/*     */   }
/*     */ 
/*     */   public int getConventionalTiles()
/*     */   {
/*  63 */     return this._conventionalTiles;
/*     */   }
/*     */ 
/*     */   public void setConventionalTiles(int value) {
/*  67 */     if (value < 0)
/*  68 */       throw new ArgumentOutOfRangeException("value", value, "Must be greater than zero");
/*  69 */     this._conventionalTiles = value;
/*     */   }
/*     */ 
/*     */   public int getMaximumTileViews()
/*     */   {
/*  74 */     return this._maximumTileViews;
/*     */   }
/*     */ 
/*     */   public void setMaximumTileViews(int value) {
/*  78 */     if (value < 0)
/*  79 */       throw new ArgumentOutOfRangeException("value", value, "Must be greater than zero");
/*  80 */     this._maximumTileViews = value;
/*     */   }
/*     */ 
/*     */   public int getTileViews()
/*     */   {
/*  85 */     return this._tileViews;
/*     */   }
/*     */ 
/*     */   public void setTileViews(int value) {
/*  89 */     if (value < 0)
/*  90 */       throw new ArgumentOutOfRangeException("value", value, "Must be greater than zero");
/*  91 */     this._tileViews = value;
/*     */   }
/*     */ 
/*     */   public int getFlags()
/*     */   {
/*  96 */     return this._flags;
/*     */   }
/*     */ 
/*     */   public void setFlags(int value) {
/* 100 */     this._flags = value;
/*     */   }
/*     */ 
/*     */   public RasterImageMemoryInformation clone()
/*     */   {
/* 105 */     RasterImageMemoryInformation varCopy = new RasterImageMemoryInformation();
/*     */ 
/* 107 */     varCopy._memoryFlags = this._memoryFlags;
/* 108 */     varCopy._tileSize = this._tileSize;
/* 109 */     varCopy._totalTiles = this._totalTiles;
/* 110 */     varCopy._conventionalTiles = this._conventionalTiles;
/* 111 */     varCopy._maximumTileViews = this._maximumTileViews;
/* 112 */     varCopy._tileViews = this._tileViews;
/* 113 */     varCopy._flags = this._flags;
/*     */ 
/* 115 */     return varCopy;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImageMemoryInformation
 * JD-Core Version:    0.6.2
 */