/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.RasterColor;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class ShearCommand extends RasterCommand
/*    */ {
/*    */   private int _angle;
/*    */   private boolean _horizontal;
/*    */   private RasterColor _fillColor;
/*    */ 
/*    */   public ShearCommand()
/*    */   {
/* 16 */     this._angle = 1500;
/* 17 */     this._horizontal = true;
/* 18 */     this._fillColor = new RasterColor(255, 255, 255);
/*    */   }
/*    */ 
/*    */   public ShearCommand(int angle, boolean horizontal, RasterColor fillColor)
/*    */   {
/* 23 */     this._angle = angle;
/* 24 */     this._horizontal = horizontal;
/* 25 */     this._fillColor = fillColor;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 31 */     return "Shear";
/*    */   }
/*    */ 
/*    */   public int getAngle() {
/* 35 */     return this._angle;
/*    */   }
/*    */   public void setAngle(int value) {
/* 38 */     this._angle = value;
/*    */   }
/*    */ 
/*    */   public boolean getHorizontal() {
/* 42 */     return this._horizontal;
/*    */   }
/*    */   public void setHorizontal(boolean value) {
/* 45 */     this._horizontal = value;
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
/* 60 */       return ltkrn.ShearBitmap(image.getCurrentBitmapHandle(), this._angle, this._horizontal, this._fillColor.toRgb());
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
 * Qualified Name:     leadtools.imageprocessing.ShearCommand
 * JD-Core Version:    0.6.2
 */