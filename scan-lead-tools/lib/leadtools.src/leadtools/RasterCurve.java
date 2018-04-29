/*     */ package leadtools;
/*     */ 
/*     */ public class RasterCurve
/*     */ {
/*     */   private int _type;
/*     */   private RasterCollection<LeadPoint> _points;
/*     */   private int _fillMode;
/*     */   private double _tension;
/*     */   private int _close;
/*     */ 
/*     */   public RasterCurve()
/*     */   {
/*  81 */     this._type = 1;
/*  82 */     this._fillMode = 1;
/*  83 */     this._tension = 0.5D;
/*  84 */     this._close = 1;
/*     */ 
/*  86 */     this._points = new RasterCollection();
/*     */   }
/*     */ 
/*     */   public RasterCurveType getType()
/*     */   {
/*  91 */     return RasterCurveType.forValue(this._type);
/*     */   }
/*     */ 
/*     */   public void setType(RasterCurveType value) {
/*  95 */     this._type = value.getValue();
/*     */   }
/*     */ 
/*     */   public RasterCollection<LeadPoint> getPoints()
/*     */   {
/* 100 */     return this._points;
/*     */   }
/*     */ 
/*     */   public LeadFillMode getFillMode()
/*     */   {
/* 105 */     return LeadFillMode.forValue(this._fillMode);
/*     */   }
/*     */ 
/*     */   public void setFillMode(LeadFillMode value) {
/* 109 */     this._fillMode = value.getValue();
/*     */   }
/*     */ 
/*     */   public double getTension()
/*     */   {
/* 114 */     return this._tension;
/*     */   }
/*     */ 
/*     */   public void setTension(double value) {
/* 118 */     this._tension = value;
/*     */   }
/*     */ 
/*     */   public RasterCurveClose getClose()
/*     */   {
/* 123 */     return RasterCurveClose.forValue(this._close);
/*     */   }
/*     */ 
/*     */   public void setClose(RasterCurveClose value) {
/* 127 */     this._close = value.getValue();
/*     */   }
/*     */ 
/*     */   public LeadPoint[] toBezierPoints()
/*     */   {
/* 132 */     if (this._type == RasterCurveType.BEZIER.getValue()) {
/* 133 */       return RasterImage.getPointArray(this._points);
/*     */     }
/* 135 */     int[] count = new int[1];
/* 136 */     LeadPoint[][] ppPoints = { null };
/* 137 */     int ret = ltkrn.CurveToBezier(this, count, ppPoints);
/* 138 */     RasterException.checkErrorCode(ret);
/*     */ 
/* 140 */     return ppPoints[0];
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterCurve
 * JD-Core Version:    0.6.2
 */