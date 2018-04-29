/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.RasterColor;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class RotateCommand extends RasterCommand
/*    */ {
/*    */   private int _angle;
/*    */   private int _flags;
/*    */   private RasterColor _fillColor;
/*    */ 
/*    */   public RotateCommand()
/*    */   {
/* 16 */     this._angle = 4500;
/* 17 */     this._flags = (RotateCommandFlags.BICUBIC.getValue() | RotateCommandFlags.RESIZE.getValue());
/* 18 */     this._fillColor = RasterColor.fromColorRef(0);
/*    */   }
/*    */ 
/*    */   public RotateCommand(int angle, int flags, RasterColor fillColor)
/*    */   {
/* 23 */     this._angle = angle;
/* 24 */     this._flags = flags;
/* 25 */     this._fillColor = fillColor;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 31 */     return "Rotate";
/*    */   }
/*    */ 
/*    */   public int getAngle() {
/* 35 */     return this._angle;
/*    */   }
/*    */   public void setAngle(int value) {
/* 38 */     this._angle = value;
/*    */   }
/*    */ 
/*    */   public int getFlags() {
/* 42 */     return this._flags;
/*    */   }
/*    */   public void setFlags(int value) {
/* 45 */     this._flags = value;
/*    */   }
/*    */ 
/*    */   public RasterColor getFillColor() {
/* 49 */     return this._fillColor;
/*    */   }
/*    */   public void setFillColor(RasterColor value) {
/* 52 */     this._fillColor = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/*    */     try
/*    */     {
/* 60 */       return ltkrn.RotateBitmap(image.getCurrentBitmapHandle(), this._angle, this._flags, this._fillColor.toRgb());
/*    */     }
/*    */     finally
/*    */     {
/* 64 */       changedFlags[0] |= RasterImageChangedFlags.DATA | RasterImageChangedFlags.SIZE;
/* 65 */       if (image.hasRegion())
/* 66 */         changedFlags[0] |= RasterImageChangedFlags.REGION;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.RotateCommand
 * JD-Core Version:    0.6.2
 */