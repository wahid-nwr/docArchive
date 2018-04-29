/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.RasterColor;
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ class SAVEFILEOPTION
/*    */ {
/*    */   public int Reserved1;
/*    */   public int Reserved2;
/*    */   public int Flags;
/*    */   public int Passes;
/*    */   public int PageNumber;
/*    */   public int GlobalWidth;
/*    */   public int GlobalHeight;
/*    */   public int GlobalLoop;
/*    */   public int GlobalBackground;
/* 18 */   public RasterColor[] GlobalPalette = new RasterColor[256];
/*    */   public int StampWidth;
/*    */   public int StampHeight;
/*    */   public int StampBits;
/*    */   public long IFD;
/* 24 */   public String szPassword = new String(new char['ÿ']);
/*    */   public int PhotometricInterpretation;
/*    */   public int TileWidth;
/*    */   public int TileHeight;
/*    */   public int Flags2;
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.SAVEFILEOPTION
 * JD-Core Version:    0.6.2
 */