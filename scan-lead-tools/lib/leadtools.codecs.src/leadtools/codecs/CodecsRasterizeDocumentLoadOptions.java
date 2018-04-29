/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ import leadtools.ArgumentOutOfRangeException;
/*     */ 
/*     */ public class CodecsRasterizeDocumentLoadOptions
/*     */ {
/*     */   private CodecsOptions _owner;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   7 */     CodecsOptionsSerializer.writeOption(dic, "RasterizeDocument.Load.PageWidth", getPageWidth());
/*   8 */     CodecsOptionsSerializer.writeOption(dic, "RasterizeDocument.Load.PageHeight", getPageHeight());
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "RasterizeDocument.Load.LeftMargin", getLeftMargin());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "RasterizeDocument.Load.TopMargin", getTopMargin());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "RasterizeDocument.Load.RightMargin", getRightMargin());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "RasterizeDocument.Load.BottomMargin", getBottomMargin());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "RasterizeDocument.Load.Unit", getUnit().getValue());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "RasterizeDocument.Load.XResolution", getXResolution());
/*  15 */     CodecsOptionsSerializer.writeOption(dic, "RasterizeDocument.Load.YResolution", getYResolution());
/*  16 */     CodecsOptionsSerializer.writeOption(dic, "RasterizeDocument.Load.SizeMode", getSizeMode().getValue());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  20 */     setPageWidth(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.PageWidth", getPageWidth()));
/*  21 */     setPageHeight(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.PageHeight", getPageHeight()));
/*  22 */     setLeftMargin(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.LeftMargin", getLeftMargin()));
/*  23 */     setTopMargin(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.TopMargin", getTopMargin()));
/*  24 */     setRightMargin(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.RightMargin", getRightMargin()));
/*  25 */     setBottomMargin(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.BottomMargin", getBottomMargin()));
/*  26 */     setUnit(CodecsRasterizeDocumentUnit.forValue(RASTERIZEDOC_UNIT.forValue(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.Unit", getUnit().getValue())).getValue()));
/*  27 */     setUnit(CodecsRasterizeDocumentUnit.forValue(RASTERIZEDOC_UNIT.forValue(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.Unit", getUnit().getValue())).getValue()));
/*  28 */     setXResolution(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.XResolution", getXResolution()));
/*  29 */     setYResolution(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.YResolution", getYResolution()));
/*  30 */     setSizeMode(CodecsRasterizeDocumentSizeMode.forValue(RASTERIZEDOC_SIZEMODE.forValue(CodecsOptionsSerializer.readOption(dic, "RasterizeDocument.Load.SizeMode", getSizeMode().getValue())).getValue()));
/*     */   }
/*     */ 
/*     */   CodecsRasterizeDocumentLoadOptions(CodecsOptions owner)
/*     */   {
/*  37 */     this._owner = owner;
/*     */   }
/*     */ 
/*     */   CodecsRasterizeDocumentLoadOptions copy(CodecsOptions owner) {
/*  41 */     CodecsRasterizeDocumentLoadOptions copy = new CodecsRasterizeDocumentLoadOptions(owner);
/*  42 */     copy.setBottomMargin(getBottomMargin());
/*  43 */     copy.setLeftMargin(getLeftMargin());
/*  44 */     copy.setPageHeight(getPageHeight());
/*  45 */     copy.setPageWidth(getPageWidth());
/*  46 */     copy.setRightMargin(getRightMargin());
/*  47 */     copy.setSizeMode(getSizeMode());
/*  48 */     copy.setTopMargin(getTopMargin());
/*  49 */     copy.setUnit(getUnit());
/*  50 */     copy.setXResolution(getXResolution());
/*  51 */     copy.setYResolution(getYResolution());
/*     */ 
/*  53 */     return copy;
/*     */   }
/*     */ 
/*     */   public double getPageWidth() {
/*  57 */     return this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dPageWidth;
/*     */   }
/*     */ 
/*     */   public void setPageWidth(double value) {
/*  61 */     if (value <= 0.0D) {
/*  62 */       throw new ArgumentOutOfRangeException("PageWidth", Double.toString(value), "Must be a value greater than 0");
/*     */     }
/*     */ 
/*  65 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dPageWidth = value;
/*     */   }
/*     */ 
/*     */   public double getPageHeight() {
/*  69 */     return this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dPageHeight;
/*     */   }
/*     */ 
/*     */   public void setPageHeight(double value) {
/*  73 */     if (value <= 0.0D) {
/*  74 */       throw new ArgumentOutOfRangeException("PageHeight", Double.toString(value), "Must be a value greater than 0");
/*     */     }
/*     */ 
/*  77 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dPageHeight = value;
/*     */   }
/*     */ 
/*     */   public double getLeftMargin() {
/*  81 */     return this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dLeftMargin;
/*     */   }
/*     */ 
/*     */   public void setLeftMargin(double value) {
/*  85 */     if (value < 0.0D) {
/*  86 */       throw new ArgumentOutOfRangeException("LeftMargin", Double.toString(value), "Must be a value greater or equal to 0");
/*     */     }
/*     */ 
/*  90 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dLeftMargin = value;
/*     */   }
/*     */ 
/*     */   public double getTopMargin() {
/*  94 */     return this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dTopMargin;
/*     */   }
/*     */ 
/*     */   public void setTopMargin(double value) {
/*  98 */     if (value < 0.0D) {
/*  99 */       throw new ArgumentOutOfRangeException("TopMargin", Double.toString(value), "Must be a value greater or equal to 0");
/*     */     }
/*     */ 
/* 103 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dTopMargin = value;
/*     */   }
/*     */ 
/*     */   public double getRightMargin() {
/* 107 */     return this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dRightMargin;
/*     */   }
/*     */ 
/*     */   public void setRightMargin(double value) {
/* 111 */     if (value < 0.0D) {
/* 112 */       throw new ArgumentOutOfRangeException("RightMargin", Double.toString(value), "Must be a value greater or equal to 0");
/*     */     }
/*     */ 
/* 116 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dRightMargin = value;
/*     */   }
/*     */ 
/*     */   public double getBottomMargin() {
/* 120 */     return this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dBottomMargin;
/*     */   }
/*     */ 
/*     */   public void setBottomMargin(double value) {
/* 124 */     if (value < 0.0D) {
/* 125 */       throw new ArgumentOutOfRangeException("BottomMargin", Double.toString(value), "Must be a value greater or equal to 0");
/*     */     }
/*     */ 
/* 129 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.dBottomMargin = value;
/*     */   }
/*     */ 
/*     */   public CodecsRasterizeDocumentUnit getUnit() {
/* 133 */     return CodecsRasterizeDocumentUnit.forValue(this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uUnit.getValue());
/*     */   }
/*     */ 
/*     */   public void setUnit(CodecsRasterizeDocumentUnit value) {
/* 137 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uUnit = RASTERIZEDOC_UNIT.forValue(value.getValue());
/*     */   }
/*     */ 
/*     */   public int getXResolution() {
/* 141 */     return this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uXResolution;
/*     */   }
/*     */ 
/*     */   public void setXResolution(int value) {
/* 145 */     if (value < 0) {
/* 146 */       throw new ArgumentOutOfRangeException("XResolution", value, "Must be a value greater or equal to 0");
/*     */     }
/*     */ 
/* 149 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uXResolution = value;
/*     */   }
/*     */ 
/*     */   public int getYResolution() {
/* 153 */     return this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uYResolution;
/*     */   }
/*     */ 
/*     */   public void setYResolution(int value) {
/* 157 */     if (value < 0) {
/* 158 */       throw new ArgumentOutOfRangeException("YResolution", value, "Must be a value greater or equal to 0");
/*     */     }
/*     */ 
/* 161 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uYResolution = value;
/*     */   }
/*     */ 
/*     */   public int getResolution() {
/* 165 */     return Math.max(this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uXResolution, this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uYResolution);
/*     */   }
/*     */ 
/*     */   public void setResolution(int value)
/*     */   {
/* 171 */     if (value < 0) {
/* 172 */       throw new ArgumentOutOfRangeException("Resolution", value, "Must be a value greater or equal to 0");
/*     */     }
/* 174 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uXResolution = value;
/* 175 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uYResolution = value;
/*     */   }
/*     */ 
/*     */   public CodecsRasterizeDocumentSizeMode getSizeMode() {
/* 179 */     return CodecsRasterizeDocumentSizeMode.forValue(this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uSizeMode.getValue());
/*     */   }
/*     */ 
/*     */   public void setSizeMode(CodecsRasterizeDocumentSizeMode value) {
/* 183 */     this._owner.getThreadData().pThreadLoadSettings.RasterizeDocOptions.uSizeMode = RASTERIZEDOC_SIZEMODE.forValue(value.getValue());
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRasterizeDocumentLoadOptions
 * JD-Core Version:    0.6.2
 */