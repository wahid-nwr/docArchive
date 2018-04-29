/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.RasterColor;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class FillCommand extends RasterCommand
/*    */ {
/* 10 */   private RasterColor _color = new RasterColor();
/*    */ 
/*    */   public String toString()
/*    */   {
/* 15 */     return "Clear";
/*    */   }
/*    */ 
/*    */   public FillCommand()
/*    */   {
/*    */   }
/*    */ 
/*    */   public FillCommand(RasterColor color)
/*    */   {
/* 25 */     this._color = color.clone();
/*    */   }
/*    */ 
/*    */   public RasterColor getColor()
/*    */   {
/* 30 */     return this._color;
/*    */   }
/*    */ 
/*    */   public void setColor(RasterColor value) {
/* 34 */     this._color = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/*    */     try
/*    */     {
/* 42 */       return ltkrn.FillBitmap(image.getCurrentBitmapHandle(), this._color.toRgb());
/*    */     }
/*    */     finally
/*    */     {
/* 46 */       changedFlags[0] |= RasterImageChangedFlags.DATA;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.FillCommand
 * JD-Core Version:    0.6.2
 */