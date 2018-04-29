/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.RasterSizeFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class SizeCommand extends RasterCommand
/*    */ {
/*    */   private int _width;
/*    */   private int _height;
/*    */   private int _flags;
/*    */ 
/*    */   public String toString()
/*    */   {
/* 17 */     return "Size";
/*    */   }
/*    */ 
/*    */   public SizeCommand()
/*    */   {
/* 22 */     this._width = 0;
/* 23 */     this._height = 0;
/* 24 */     this._flags = RasterSizeFlags.NONE.getValue();
/*    */   }
/*    */ 
/*    */   public SizeCommand(int width, int height, int flags) {
/* 28 */     this._width = width;
/* 29 */     this._height = height;
/* 30 */     this._flags = flags;
/*    */   }
/*    */ 
/*    */   public int getWidth()
/*    */   {
/* 35 */     return this._width;
/*    */   }
/*    */ 
/*    */   public void setWidth(int value) {
/* 39 */     this._width = value;
/*    */   }
/*    */ 
/*    */   public int getHeight()
/*    */   {
/* 44 */     return this._height;
/*    */   }
/*    */ 
/*    */   public void setHeight(int value)
/*    */   {
/* 49 */     this._height = value;
/*    */   }
/*    */ 
/*    */   public int getFlags()
/*    */   {
/* 54 */     return this._flags;
/*    */   }
/*    */ 
/*    */   public void setFlags(int value)
/*    */   {
/* 59 */     this._flags = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/*    */     try
/*    */     {
/* 67 */       return ltkrn.SizeBitmap(bitmap, this._width, this._height, this._flags);
/*    */     }
/*    */     finally
/*    */     {
/* 71 */       changedFlags[0] |= RasterImageChangedFlags.DATA | RasterImageChangedFlags.SIZE;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.SizeCommand
 * JD-Core Version:    0.6.2
 */