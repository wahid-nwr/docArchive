/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.RasterColor;
/*    */ 
/*    */ public class CodecsGifImageInfo
/*    */ {
/*    */   private FILEINFO _fileInfo;
/*    */ 
/*    */   CodecsGifImageInfo(FILEINFO fileInfo)
/*    */   {
/* 10 */     this._fileInfo = fileInfo;
/*    */   }
/*    */ 
/*    */   public boolean hasAnimationLoop() {
/* 14 */     return Tools.isFlagged(this._fileInfo.Flags, 32);
/*    */   }
/*    */ 
/*    */   public int getAnimationLoop() {
/* 18 */     return this._fileInfo.GlobalLoop;
/*    */   }
/*    */ 
/*    */   public int getAnimationWidth() {
/* 22 */     return this._fileInfo.GlobalWidth;
/*    */   }
/*    */ 
/*    */   public int getAnimationHeight() {
/* 26 */     return this._fileInfo.GlobalHeight;
/*    */   }
/*    */ 
/*    */   public boolean hasAnimationBackground() {
/* 30 */     return Tools.isFlagged(this._fileInfo.Flags, 8);
/*    */   }
/*    */ 
/*    */   public RasterColor getAnimationBackground() {
/* 34 */     return RasterColor.fromColorRef(this._fileInfo.GlobalBackground);
/*    */   }
/*    */ 
/*    */   public boolean hasAnimationPalette() {
/* 38 */     return Tools.isFlagged(this._fileInfo.Flags, 16);
/*    */   }
/*    */ 
/*    */   public RasterColor[] getAnimationPalette() {
/* 42 */     return this._fileInfo.GlobalPalette;
/*    */   }
/*    */ 
/*    */   public boolean isInterlaced() {
/* 46 */     return Tools.isFlagged(this._fileInfo.Flags, 1);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsGifImageInfo
 * JD-Core Version:    0.6.2
 */