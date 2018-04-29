/*     */ package leadtools;
/*     */ 
/*     */ public class RasterOverlayAttributes
/*     */ {
/*   5 */   private LeadPoint _ptOrigin = new LeadPoint();
/*     */   private int _crColor;
/*     */   private int _uFlags;
/*     */   private short _uBitPosition;
/*     */   private short _uRows;
/*     */   private short _uColumns;
/*     */   private String _szType;
/*     */   private short _uBitsAllocated;
/*     */   private String _szDescription;
/*     */   private String _szSubtype;
/*     */   private String _szLabel;
/*     */   private int _nROIArea;
/*     */   private double _fROIMean;
/*     */   private double _fROIStandardDeviation;
/*     */   private int _nNumFramesInOverlay;
/*     */   private short _uImageFrameOrigin;
/*     */   private String _szActivationLayer;
/*     */ 
/*     */   public RasterOverlayAttributes()
/*     */   {
/*  25 */     this._ptOrigin = new LeadPoint();
/*  26 */     this._crColor = 0;
/*  27 */     this._uFlags = 0;
/*  28 */     this._uBitPosition = 0;
/*  29 */     this._uRows = 0;
/*  30 */     this._uColumns = 0;
/*  31 */     this._szType = "";
/*  32 */     this._uBitsAllocated = 0;
/*  33 */     this._szDescription = "";
/*  34 */     this._szSubtype = "";
/*  35 */     this._szLabel = "";
/*  36 */     this._nROIArea = 0;
/*  37 */     this._fROIMean = 0.0D;
/*  38 */     this._fROIStandardDeviation = 0.0D;
/*  39 */     this._nNumFramesInOverlay = 0;
/*  40 */     this._uImageFrameOrigin = 0;
/*  41 */     this._szActivationLayer = "";
/*     */   }
/*     */ 
/*     */   public LeadPoint getOrigin()
/*     */   {
/* 187 */     return this._ptOrigin;
/*     */   }
/*     */ 
/*     */   public void setOrigin(LeadPoint value) {
/* 191 */     this._ptOrigin = value;
/*     */   }
/*     */ 
/*     */   public RasterColor getColor()
/*     */   {
/* 198 */     return RasterColor.fromColorRef(this._crColor);
/*     */   }
/*     */ 
/*     */   public void setColor(RasterColor value) {
/* 202 */     this._crColor = value.getColorRef();
/*     */   }
/*     */ 
/*     */   public boolean getAutoPaint()
/*     */   {
/* 207 */     return (this._uFlags & 0x1) == 1;
/*     */   }
/*     */ 
/*     */   public void setAutoPaint(boolean value) {
/* 211 */     if (value)
/* 212 */       this._uFlags |= 1;
/*     */     else
/* 214 */       this._uFlags &= -2;
/*     */   }
/*     */ 
/*     */   public boolean getAutoProcess()
/*     */   {
/* 219 */     return (this._uFlags & 0x2) == 2;
/*     */   }
/*     */ 
/*     */   public void setAutoProcess(boolean value) {
/* 223 */     if (value)
/* 224 */       this._uFlags |= 2;
/*     */     else
/* 226 */       this._uFlags &= -3;
/*     */   }
/*     */ 
/*     */   public boolean getUseBitPlane()
/*     */   {
/* 231 */     return (this._uFlags & 0x4) == 4;
/*     */   }
/*     */ 
/*     */   public void setUseBitPlane(boolean value) {
/* 235 */     if (value)
/* 236 */       this._uFlags |= 4;
/*     */     else
/* 238 */       this._uFlags &= -5;
/*     */   }
/*     */ 
/*     */   public int getBitPosition()
/*     */   {
/* 243 */     return this._uBitPosition;
/*     */   }
/*     */ 
/*     */   public void setBitPosition(int value) {
/* 247 */     this._uBitPosition = ((short)value);
/*     */   }
/*     */ 
/*     */   public int getRows()
/*     */   {
/* 252 */     return this._uRows;
/*     */   }
/*     */ 
/*     */   public void setRows(int value) {
/* 256 */     this._uRows = ((short)value);
/*     */   }
/*     */ 
/*     */   public int getColumns()
/*     */   {
/* 261 */     return this._uColumns;
/*     */   }
/*     */ 
/*     */   public void setColumns(int value) {
/* 265 */     this._uColumns = ((short)value);
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/* 270 */     return this._szType;
/*     */   }
/*     */ 
/*     */   public void setType(String value) {
/* 274 */     this._szType = value;
/*     */   }
/*     */ 
/*     */   public int getBitsAllocated()
/*     */   {
/* 279 */     return this._uBitsAllocated;
/*     */   }
/*     */ 
/*     */   public void setBitsAllocated(int value) {
/* 283 */     this._uBitsAllocated = ((short)value);
/*     */   }
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 288 */     return this._szDescription;
/*     */   }
/*     */ 
/*     */   public void setDescription(String value) {
/* 292 */     this._szDescription = value;
/*     */   }
/*     */ 
/*     */   public String getSubtype()
/*     */   {
/* 297 */     return this._szSubtype;
/*     */   }
/*     */ 
/*     */   public void setSubtype(String value) {
/* 301 */     this._szSubtype = value;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/* 306 */     return this._szLabel;
/*     */   }
/*     */ 
/*     */   public void setLabel(String value) {
/* 310 */     this._szLabel = value;
/*     */   }
/*     */ 
/*     */   public int getRoiArea()
/*     */   {
/* 315 */     return this._nROIArea;
/*     */   }
/*     */ 
/*     */   public void setRoiArea(int value) {
/* 319 */     this._nROIArea = value;
/*     */   }
/*     */ 
/*     */   public double getRoiMean()
/*     */   {
/* 324 */     return this._fROIMean;
/*     */   }
/*     */ 
/*     */   public void setRoiMean(double value) {
/* 328 */     this._fROIMean = value;
/*     */   }
/*     */ 
/*     */   public double getRoiStandardDeviation()
/*     */   {
/* 333 */     return this._fROIStandardDeviation;
/*     */   }
/*     */ 
/*     */   public void setRoiStandardDeviation(double value) {
/* 337 */     this._fROIStandardDeviation = value;
/*     */   }
/*     */ 
/*     */   public int getFramesInOverlay()
/*     */   {
/* 342 */     return this._nNumFramesInOverlay;
/*     */   }
/*     */ 
/*     */   public void setFramesInOverlay(int value) {
/* 346 */     this._nNumFramesInOverlay = value;
/*     */   }
/*     */ 
/*     */   public int getImageFrameOrigin()
/*     */   {
/* 351 */     return this._uImageFrameOrigin;
/*     */   }
/*     */ 
/*     */   public void setImageFrameOrigin(int value) {
/* 355 */     this._uImageFrameOrigin = ((short)value);
/*     */   }
/*     */ 
/*     */   public String getActivationLayer()
/*     */   {
/* 360 */     return this._szActivationLayer;
/*     */   }
/*     */ 
/*     */   public void setActivationLayer(String value) {
/* 364 */     this._szActivationLayer = value;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterOverlayAttributes
 * JD-Core Version:    0.6.2
 */