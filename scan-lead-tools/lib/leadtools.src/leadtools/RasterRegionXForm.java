/*    */ package leadtools;
/*    */ 
/*    */ public class RasterRegionXForm
/*    */ {
/*    */   private int _uViewPerspective;
/*    */   private int _nXScalarNum;
/*    */   private int _nXScalarDen;
/*    */   private int _nYScalarNum;
/*    */   private int _nYScalarDen;
/*    */   private int _nXOffset;
/*    */   private int _nYOffset;
/*    */ 
/*    */   public RasterRegionXForm()
/*    */   {
/* 14 */     this._uViewPerspective = RasterViewPerspective.TOP_LEFT.getValue();
/* 15 */     this._nXScalarNum = 1;
/* 16 */     this._nXScalarDen = 1;
/* 17 */     this._nYScalarNum = 1;
/* 18 */     this._nYScalarDen = 1;
/* 19 */     this._nXOffset = 0;
/* 20 */     this._nYOffset = 0;
/*    */   }
/*    */ 
/*    */   public RasterRegionXForm clone()
/*    */   {
/* 25 */     RasterRegionXForm xform = Default();
/* 26 */     xform._uViewPerspective = this._uViewPerspective;
/* 27 */     xform._nXScalarNum = this._nXScalarNum;
/* 28 */     xform._nXScalarDen = this._nXScalarDen;
/* 29 */     xform._nYScalarNum = this._nYScalarNum;
/* 30 */     xform._nYScalarDen = this._nYScalarDen;
/* 31 */     xform._nXOffset = this._nXOffset;
/* 32 */     xform._nYOffset = this._nYOffset;
/* 33 */     return xform;
/*    */   }
/*    */ 
/*    */   public static RasterRegionXForm Default()
/*    */   {
/* 38 */     return new RasterRegionXForm();
/*    */   }
/*    */ 
/*    */   public int getViewPerspective()
/*    */   {
/* 43 */     return this._uViewPerspective;
/*    */   }
/*    */ 
/*    */   public RasterViewPerspective getViewPerspectiveAsInt()
/*    */   {
/* 48 */     return RasterViewPerspective.forValue(this._uViewPerspective);
/*    */   }
/*    */ 
/*    */   public void setRasterViewPerspective(RasterViewPerspective value)
/*    */   {
/* 53 */     this._uViewPerspective = value.getValue();
/*    */   }
/*    */ 
/*    */   public void setRasterViewPerspective(int value)
/*    */   {
/* 58 */     this._uViewPerspective = value;
/*    */   }
/*    */   public int getXScalarNumerator() {
/* 61 */     return this._nXScalarNum; } 
/* 62 */   public void setXScalarNumerator(int value) { this._nXScalarNum = value; } 
/*    */   public int getXScalarDenominator() {
/* 64 */     return this._nXScalarDen; } 
/* 65 */   public void setXScalarDenominator(int value) { this._nXScalarDen = value; } 
/*    */   public int getYScalarNumerator() {
/* 67 */     return this._nYScalarNum; } 
/* 68 */   public void setYScalarNumerator(int value) { this._nYScalarNum = value; } 
/*    */   public int getYScalarDenominator() {
/* 70 */     return this._nYScalarDen; } 
/* 71 */   public void setYScalarDenominator(int value) { this._nYScalarDen = value; } 
/*    */   public int getXOffset() {
/* 73 */     return this._nXOffset; } 
/* 74 */   public void setXOffset(int value) { this._nXOffset = value; } 
/* 75 */   public int getYOffset() { return this._nYOffset; } 
/* 76 */   public void setYOffset(int value) { this._nYOffset = value; }
/*    */ 
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterRegionXForm
 * JD-Core Version:    0.6.2
 */