/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.RasterColor;
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ class FILEINFO
/*    */ {
/*    */   public int Format;
/*    */   public String Name;
/*    */   public int Width;
/*    */   public int Height;
/*    */   public int BitsPerPixel;
/*    */   public long SizeDisk;
/*    */   public long SizeMem;
/*    */   public String Compression;
/*    */   public int ViewPerspective;
/*    */   public int Order;
/*    */   public int PageNumber;
/*    */   public int TotalPages;
/*    */   public int XResolution;
/*    */   public int YResolution;
/*    */   public int Flags;
/*    */   public int GlobalLoop;
/*    */   public int GlobalWidth;
/*    */   public int GlobalHeight;
/*    */   public int GlobalBackground;
/*    */   public RasterColor[] GlobalPalette;
/*    */   public long IFD;
/*    */   public int Layers;
/*    */   public int ColorSpace;
/*    */   public int Channels;
/*    */   public boolean bIsDocFile;
/*    */   public double dDocPageWidth;
/*    */   public double dDocPageHeight;
/* 36 */   public RASTERIZEDOC_UNIT uDocUnit = RASTERIZEDOC_UNIT.RASTERIZEDOC_UNIT_PIXEL;
/*    */   public boolean bIsVectorFile;
/*    */   public double VectorParallelogram_Min_x;
/*    */   public double VectorParallelogram_Min_y;
/*    */   public double VectorParallelogram_Min_z;
/*    */   public double VectorParallelogram_Max_x;
/*    */   public double VectorParallelogram_Max_y;
/*    */   public double VectorParallelogram_Max_z;
/*    */   public int VectorParallelogram_Unit;
/*    */   public int MessageCount;
/*    */   public int nHorzTiles;
/*    */   public int nVertTiles;
/*    */ 
/*    */   public FILEINFO clone()
/*    */   {
/* 51 */     FILEINFO ret = new FILEINFO();
/* 52 */     ret.Format = this.Format;
/* 53 */     ret.Name = this.Name;
/* 54 */     ret.Width = this.Width;
/* 55 */     ret.Height = this.Height;
/* 56 */     ret.BitsPerPixel = this.BitsPerPixel;
/* 57 */     ret.SizeDisk = this.SizeDisk;
/* 58 */     ret.SizeMem = this.SizeMem;
/* 59 */     if (this.Compression != null)
/* 60 */       ret.Compression = new String(this.Compression);
/* 61 */     ret.ViewPerspective = this.ViewPerspective;
/* 62 */     ret.Order = this.Order;
/* 63 */     ret.PageNumber = this.PageNumber;
/* 64 */     ret.TotalPages = this.TotalPages;
/* 65 */     ret.XResolution = this.XResolution;
/* 66 */     ret.YResolution = this.YResolution;
/* 67 */     ret.Flags = this.Flags;
/* 68 */     ret.GlobalLoop = this.GlobalLoop;
/* 69 */     ret.GlobalWidth = this.GlobalWidth;
/* 70 */     ret.GlobalHeight = this.GlobalHeight;
/* 71 */     ret.GlobalBackground = this.GlobalBackground;
/* 72 */     if (this.GlobalPalette != null)
/* 73 */       ret.GlobalPalette = ((RasterColor[])this.GlobalPalette.clone());
/* 74 */     ret.IFD = this.IFD;
/* 75 */     ret.Layers = this.Layers;
/* 76 */     ret.ColorSpace = this.ColorSpace;
/* 77 */     ret.Channels = this.Channels;
/* 78 */     ret.bIsDocFile = this.bIsDocFile;
/* 79 */     ret.dDocPageWidth = this.dDocPageWidth;
/* 80 */     ret.dDocPageHeight = this.dDocPageHeight;
/* 81 */     ret.uDocUnit = this.uDocUnit;
/* 82 */     ret.bIsVectorFile = this.bIsVectorFile;
/* 83 */     ret.VectorParallelogram_Min_x = this.VectorParallelogram_Min_x;
/* 84 */     ret.VectorParallelogram_Min_y = this.VectorParallelogram_Min_y;
/* 85 */     ret.VectorParallelogram_Min_z = this.VectorParallelogram_Min_z;
/* 86 */     ret.VectorParallelogram_Max_x = this.VectorParallelogram_Max_x;
/* 87 */     ret.VectorParallelogram_Max_y = this.VectorParallelogram_Max_y;
/* 88 */     ret.VectorParallelogram_Max_z = this.VectorParallelogram_Max_z;
/* 89 */     ret.VectorParallelogram_Unit = this.VectorParallelogram_Unit;
/* 90 */     ret.MessageCount = this.MessageCount;
/* 91 */     ret.nHorzTiles = this.nHorzTiles;
/* 92 */     ret.nVertTiles = this.nVertTiles;
/* 93 */     return ret;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.FILEINFO
 * JD-Core Version:    0.6.2
 */