/*     */ package leadtools.codecs;
/*     */ 
/*     */ import leadtools.ArgumentNullException;
/*     */ import leadtools.ILeadStream;
/*     */ import leadtools.LeadRect;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.RasterSizeFlags;
/*     */ import leadtools.internal.IsInternal;
/*     */ 
/*     */ @IsInternal
/*     */ final class LoadParams
/*     */ {
/*     */   public RasterImage Image;
/*     */   public int Page;
/*     */   public ILeadStream Stream;
/*     */   public int BitsPerPixel;
/*     */   public CodecsLoadByteOrder Order;
/*     */   public int FirstPage;
/*     */   public int LastPage;
/*     */   public boolean UseOffset;
/*     */   public long Offset;
/*     */   public long Count;
/*     */   public boolean UseTile;
/*  25 */   public LeadRect Tile = new LeadRect(0, 0, 0, 0);
/*     */   public boolean UseSize;
/*     */   public int SizeWidth;
/*     */   public int SizeHeight;
/*     */   public RasterSizeFlags SizeFlags;
/*     */   public int LoadFlags;
/*     */ 
/*     */   private LoadParams()
/*     */   {
/*     */   }
/*     */ 
/*     */   public LoadParams(ILeadStream stream, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*     */   {
/*  38 */     init(stream, bitsPerPixel, order, firstPage, lastPage);
/*     */   }
/*     */ 
/*     */   public LoadParams(ILeadStream stream, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage, long offset, long count) {
/*  42 */     init(stream, bitsPerPixel, order, firstPage, lastPage);
/*  43 */     this.UseOffset = true;
/*  44 */     this.Offset = offset;
/*  45 */     this.Count = count;
/*     */   }
/*     */ 
/*     */   public LoadParams(ILeadStream stream, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage, LeadRect tile) {
/*  49 */     init(stream, bitsPerPixel, order, firstPage, lastPage);
/*  50 */     if (tile != null) {
/*  51 */       this.UseTile = true;
/*  52 */       this.Tile = tile;
/*     */     } else {
/*  54 */       this.UseTile = false;
/*     */     }
/*     */   }
/*     */ 
/*  58 */   public LoadParams(ILeadStream stream, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage, int sizeWidth, int sizeHeight, RasterSizeFlags sizeFlags) { init(stream, bitsPerPixel, order, firstPage, lastPage);
/*  59 */     this.UseSize = true;
/*  60 */     this.SizeWidth = sizeWidth;
/*  61 */     this.SizeHeight = sizeHeight;
/*  62 */     this.SizeFlags = sizeFlags;
/*     */   }
/*     */ 
/*     */   public LoadParams(ILeadStream stream, long offset, long count, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*     */   {
/*  67 */     init(stream, bitsPerPixel, order, firstPage, lastPage);
/*  68 */     this.Offset = offset;
/*  69 */     this.Count = count;
/*     */   }
/*     */ 
/*     */   private void init(ILeadStream stream, int bitsPerPixel, CodecsLoadByteOrder order, int firstPage, int lastPage)
/*     */   {
/*  74 */     if (stream == null) {
/*  75 */       throw new ArgumentNullException("stream");
/*     */     }
/*  77 */     this.Image = null;
/*  78 */     this.Page = 0;
/*  79 */     this.Stream = stream;
/*  80 */     this.BitsPerPixel = bitsPerPixel;
/*  81 */     this.Order = order;
/*  82 */     this.FirstPage = firstPage;
/*  83 */     this.LastPage = lastPage;
/*  84 */     this.UseOffset = false;
/*  85 */     this.Offset = -1L;
/*  86 */     this.Count = -1L;
/*  87 */     this.UseTile = false;
/*  88 */     this.Tile = null;
/*  89 */     this.UseSize = false;
/*  90 */     this.SizeWidth = 0;
/*  91 */     this.SizeHeight = 0;
/*  92 */     this.SizeFlags = RasterSizeFlags.NONE;
/*  93 */     this.LoadFlags = 0;
/*     */   }
/*     */ 
/*     */   public LoadParams clone()
/*     */   {
/*  98 */     LoadParams varCopy = new LoadParams();
/*     */ 
/* 100 */     varCopy.Image = this.Image;
/* 101 */     varCopy.Page = this.Page;
/* 102 */     varCopy.Stream = this.Stream;
/* 103 */     varCopy.BitsPerPixel = this.BitsPerPixel;
/* 104 */     varCopy.Order = this.Order;
/* 105 */     varCopy.FirstPage = this.FirstPage;
/* 106 */     varCopy.LastPage = this.LastPage;
/* 107 */     varCopy.UseOffset = this.UseOffset;
/* 108 */     varCopy.Offset = this.Offset;
/* 109 */     varCopy.Count = this.Count;
/* 110 */     varCopy.UseTile = this.UseTile;
/* 111 */     varCopy.Tile = this.Tile;
/* 112 */     varCopy.UseSize = this.UseSize;
/* 113 */     varCopy.SizeWidth = this.SizeWidth;
/* 114 */     varCopy.SizeHeight = this.SizeHeight;
/* 115 */     varCopy.SizeFlags = this.SizeFlags;
/* 116 */     varCopy.LoadFlags = this.LoadFlags;
/*     */ 
/* 118 */     return varCopy;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.LoadParams
 * JD-Core Version:    0.6.2
 */