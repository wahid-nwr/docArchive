/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ import leadtools.LeadRect;
/*     */ import leadtools.RasterException;
/*     */ 
/*     */ public class CodecsJpeg2000SaveOptions
/*     */ {
/*     */   private CodecsOptions _owner;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   8 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.UseColorTransform", getUseColorTransform());
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.DerivedQuantization", getDerivedQuantization());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.CompressionControl", getCompressionControl().getValue());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.CompressionRatio", getCompressionRatio());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.TargetFileSize", getTargetFileSize());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.ImageAreaHorizontalOffset", getImageAreaHorizontalOffset());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.ImageAreaVerticalOffset", getImageAreaVerticalOffset());
/*  15 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.ReferenceTileWidth", getReferenceTileWidth());
/*  16 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.ReferenceTileHeight", getReferenceTileHeight());
/*  17 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.TileHorizontalOffset", getTileHorizontalOffset());
/*  18 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.TileVerticalOffset", getTileVerticalOffset());
/*  19 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.HorizontalSubSamplingValues", getHorizontalSubSamplingValues());
/*  20 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.VerticalSubSamplingValues", getVerticalSubSamplingValues());
/*  21 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.DecompositionLevels", getDecompositionLevels());
/*  22 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.ProgressingOrder", getProgressingOrder().getValue());
/*  23 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.UseSopMarker", getUseSopMarker());
/*  24 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.UseEphMarker", getUseEphMarker());
/*  25 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.RegionOfInterest", getRegionOfInterest().getValue());
/*  26 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.UseRegionOfInterest", getUseRegionOfInterest());
/*  27 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.RegionOfInterestWeight", getRegionOfInterestWeight());
/*  28 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.RegionOfInterestRectangle", getRegionOfInterestRectangle());
/*  29 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.AlphaChannelActiveBits", getAlphaChannelActiveBits());
/*  30 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.AlphaChannelLossless", getAlphaChannelLossless());
/*  31 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Save.PrecinctSize", getPrecinctSize().getValue());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  35 */     setUseColorTransform(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.UseColorTransform", getUseColorTransform()));
/*  36 */     setUseColorTransform(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.UseColorTransform", getUseColorTransform()));
/*  37 */     setDerivedQuantization(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.DerivedQuantization", getDerivedQuantization()));
/*  38 */     setCompressionControl(CodecsJpeg2000CompressionControl.forValue(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.CompressionControl", getCompressionControl().getValue())));
/*  39 */     setCompressionRatio(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.CompressionRatio", getCompressionRatio()));
/*  40 */     setTargetFileSize(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.TargetFileSize", getTargetFileSize()));
/*  41 */     setImageAreaHorizontalOffset(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.ImageAreaHorizontalOffset", getImageAreaHorizontalOffset()));
/*  42 */     setImageAreaVerticalOffset(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.ImageAreaVerticalOffset", getImageAreaVerticalOffset()));
/*  43 */     setReferenceTileWidth(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.ReferenceTileWidth", getReferenceTileWidth()));
/*  44 */     setReferenceTileHeight(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.ReferenceTileHeight", getReferenceTileHeight()));
/*  45 */     setTileHorizontalOffset(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.TileHorizontalOffset", getTileHorizontalOffset()));
/*  46 */     setTileVerticalOffset(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.TileVerticalOffset", getTileVerticalOffset()));
/*  47 */     setHorizontalSubSamplingValues(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.HorizontalSubSamplingValues", getHorizontalSubSamplingValues()));
/*  48 */     setVerticalSubSamplingValues(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.VerticalSubSamplingValues", getVerticalSubSamplingValues()));
/*  49 */     setDecompositionLevels(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.DecompositionLevels", getDecompositionLevels()));
/*  50 */     setProgressingOrder(CodecsJpeg2000ProgressionsOrder.forValue(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.ProgressingOrder", getProgressingOrder().getValue())));
/*  51 */     setUseSopMarker(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.UseSopMarker", getUseSopMarker()));
/*  52 */     setUseEphMarker(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.UseEphMarker", getUseEphMarker()));
/*  53 */     setRegionOfInterest(CodecsJpeg2000RegionOfInterest.forValue(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.RegionOfInterest", getRegionOfInterest().getValue())));
/*  54 */     setUseRegionOfInterest(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.UseRegionOfInterest", getUseRegionOfInterest()));
/*  55 */     setRegionOfInterestWeight(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.RegionOfInterestWeight", getRegionOfInterestWeight()));
/*  56 */     setRegionOfInterestRectangle(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.RegionOfInterestRectangle", getRegionOfInterestRectangle()));
/*  57 */     setAlphaChannelActiveBits(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.AlphaChannelActiveBits", getAlphaChannelActiveBits()));
/*  58 */     setAlphaChannelLossless(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.AlphaChannelLossless", getAlphaChannelLossless()));
/*  59 */     setPrecinctSize(CodecsJpeg2000PrecinctSize.forValue(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Save.PrecinctSize", getPrecinctSize().getValue())));
/*     */   }
/*     */ 
/*     */   public CodecsJpeg2000SaveOptions(CodecsOptions owner)
/*     */   {
/*  65 */     this._owner = owner;
/*     */   }
/*     */ 
/*     */   CodecsJpeg2000SaveOptions copy(CodecsOptions owner) {
/*  69 */     CodecsJpeg2000SaveOptions copy = new CodecsJpeg2000SaveOptions(owner);
/*  70 */     copy.setAlphaChannelActiveBits(getAlphaChannelActiveBits());
/*  71 */     copy.setAlphaChannelLossless(getAlphaChannelLossless());
/*  72 */     copy.setCompressionControl(getCompressionControl());
/*  73 */     copy.setCompressionRatio(getCompressionRatio());
/*  74 */     copy.setDecompositionLevels(getDecompositionLevels());
/*  75 */     copy.setDerivedQuantization(getDerivedQuantization());
/*  76 */     copy.setHorizontalSubSamplingValues(getHorizontalSubSamplingValues());
/*  77 */     copy.setImageAreaHorizontalOffset(getImageAreaHorizontalOffset());
/*  78 */     copy.setImageAreaVerticalOffset(getImageAreaVerticalOffset());
/*  79 */     copy.setPrecinctSize(getPrecinctSize());
/*  80 */     copy.setProgressingOrder(getProgressingOrder());
/*  81 */     copy.setReferenceTileHeight(getReferenceTileHeight());
/*  82 */     copy.setReferenceTileWidth(getReferenceTileWidth());
/*  83 */     copy.setRegionOfInterest(getRegionOfInterest());
/*  84 */     copy.setRegionOfInterestRectangle(getRegionOfInterestRectangle());
/*  85 */     copy.setRegionOfInterestWeight(getRegionOfInterestWeight());
/*  86 */     copy.setTargetFileSize(getTargetFileSize());
/*  87 */     copy.setTileHorizontalOffset(getTileHorizontalOffset());
/*  88 */     copy.setTileVerticalOffset(getTileVerticalOffset());
/*  89 */     copy.setUseColorTransform(getUseColorTransform());
/*  90 */     copy.setUseEphMarker(getUseEphMarker());
/*  91 */     copy.setUseRegionOfInterest(getUseRegionOfInterest());
/*  92 */     copy.setUseSopMarker(getUseSopMarker());
/*  93 */     copy.setVerticalSubSamplingValues(getVerticalSubSamplingValues());
/*     */ 
/*  95 */     return copy;
/*     */   }
/*     */ 
/*     */   public int getMaximumComponentsNumber() {
/*  99 */     return 3;
/*     */   }
/*     */ 
/*     */   public int getMaximumDecompressionLevels() {
/* 103 */     return 20;
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 107 */     FILEJ2KOPTIONS options = new FILEJ2KOPTIONS();
/* 108 */     int ret = ltfil.GetDefaultJ2KOptions(options);
/* 109 */     RasterException.checkErrorCode(ret);
/*     */ 
/* 111 */     this._owner.getThreadData().J2KOptions = options;
/*     */   }
/*     */ 
/*     */   public boolean getUseColorTransform() {
/* 115 */     return this._owner.getThreadData().J2KOptions.bUseColorTransform;
/*     */   }
/*     */ 
/*     */   public void setUseColorTransform(boolean value) {
/* 119 */     this._owner.getThreadData().J2KOptions.bUseColorTransform = value;
/*     */   }
/*     */ 
/*     */   public boolean getDerivedQuantization() {
/* 123 */     return this._owner.getThreadData().J2KOptions.bDerivedQuantization;
/*     */   }
/*     */ 
/*     */   public void setDerivedQuantization(boolean value) {
/* 127 */     this._owner.getThreadData().J2KOptions.bDerivedQuantization = value;
/*     */   }
/*     */ 
/*     */   public CodecsJpeg2000CompressionControl getCompressionControl() {
/* 131 */     return this._owner.getThreadData().J2KOptions.uCompressionControl;
/*     */   }
/*     */ 
/*     */   public void setCompressionControl(CodecsJpeg2000CompressionControl value) {
/* 135 */     this._owner.getThreadData().J2KOptions.uCompressionControl = value;
/*     */   }
/*     */ 
/*     */   public float getCompressionRatio() {
/* 139 */     return this._owner.getThreadData().J2KOptions.fCompressionRatio;
/*     */   }
/*     */ 
/*     */   public void setCompressionRatio(float value) {
/* 143 */     this._owner.getThreadData().J2KOptions.fCompressionRatio = value;
/*     */   }
/*     */ 
/*     */   public long getTargetFileSize() {
/* 147 */     return this._owner.getThreadData().J2KOptions.uTargetFileSize;
/*     */   }
/*     */ 
/*     */   public void setTargetFileSize(long value) {
/* 151 */     this._owner.getThreadData().J2KOptions.uTargetFileSize = value;
/*     */   }
/*     */ 
/*     */   public int getImageAreaHorizontalOffset() {
/* 155 */     return this._owner.getThreadData().J2KOptions.uXOsiz;
/*     */   }
/*     */ 
/*     */   public void setImageAreaHorizontalOffset(int value) {
/* 159 */     this._owner.getThreadData().J2KOptions.uXOsiz = value;
/*     */   }
/*     */ 
/*     */   public int getImageAreaVerticalOffset() {
/* 163 */     return this._owner.getThreadData().J2KOptions.uYOsiz;
/*     */   }
/*     */ 
/*     */   public void setImageAreaVerticalOffset(int value) {
/* 167 */     this._owner.getThreadData().J2KOptions.uYOsiz = value;
/*     */   }
/*     */ 
/*     */   public int getReferenceTileWidth() {
/* 171 */     return this._owner.getThreadData().J2KOptions.uXTsiz;
/*     */   }
/*     */ 
/*     */   public void setReferenceTileWidth(int value) {
/* 175 */     this._owner.getThreadData().J2KOptions.uXTsiz = value;
/*     */   }
/*     */ 
/*     */   public int getReferenceTileHeight() {
/* 179 */     return this._owner.getThreadData().J2KOptions.uYTsiz;
/*     */   }
/*     */ 
/*     */   public void setReferenceTileHeight(int value) {
/* 183 */     this._owner.getThreadData().J2KOptions.uYTsiz = value;
/*     */   }
/*     */ 
/*     */   public int getTileHorizontalOffset() {
/* 187 */     return this._owner.getThreadData().J2KOptions.uXTOsiz;
/*     */   }
/*     */ 
/*     */   public void setTileHorizontalOffset(int value) {
/* 191 */     this._owner.getThreadData().J2KOptions.uXTOsiz = value;
/*     */   }
/*     */ 
/*     */   public int getTileVerticalOffset() {
/* 195 */     return this._owner.getThreadData().J2KOptions.uYTOsiz;
/*     */   }
/*     */ 
/*     */   public void setTileVerticalOffset(int value) {
/* 199 */     this._owner.getThreadData().J2KOptions.uYTOsiz = value;
/*     */   }
/*     */ 
/*     */   public int[] getHorizontalSubSamplingValues() {
/* 203 */     return this._owner.getThreadData().J2KOptions.uXRsiz;
/*     */   }
/*     */ 
/*     */   public void setHorizontalSubSamplingValues(int[] values) {
/* 207 */     this._owner.getThreadData().J2KOptions.uXRsiz = ((int[])values.clone());
/*     */   }
/*     */ 
/*     */   public int[] getVerticalSubSamplingValues() {
/* 211 */     return this._owner.getThreadData().J2KOptions.uYRsiz;
/*     */   }
/*     */ 
/*     */   public void setVerticalSubSamplingValues(int[] values) {
/* 215 */     this._owner.getThreadData().J2KOptions.uYRsiz = ((int[])values.clone());
/*     */   }
/*     */ 
/*     */   public int getDecompositionLevels() {
/* 219 */     return this._owner.getThreadData().J2KOptions.uDecompLevel;
/*     */   }
/*     */ 
/*     */   public void setDecompositionLevels(int value) {
/* 223 */     this._owner.getThreadData().J2KOptions.uDecompLevel = value;
/*     */   }
/*     */ 
/*     */   public CodecsJpeg2000ProgressionsOrder getProgressingOrder() {
/* 227 */     return CodecsJpeg2000ProgressionsOrder.forValue(this._owner.getThreadData().J2KOptions.uProgressOrder);
/*     */   }
/*     */ 
/*     */   public void setProgressingOrder(CodecsJpeg2000ProgressionsOrder value)
/*     */   {
/* 232 */     this._owner.getThreadData().J2KOptions.uProgressOrder = value.getValue();
/*     */   }
/*     */ 
/*     */   public boolean getUseSopMarker()
/*     */   {
/* 354 */     return this._owner.getThreadData().J2KOptions.bUseSOPMarker;
/*     */   }
/*     */ 
/*     */   public void setUseSopMarker(boolean value) {
/* 358 */     this._owner.getThreadData().J2KOptions.bUseSOPMarker = value;
/*     */   }
/*     */ 
/*     */   public boolean getUseEphMarker() {
/* 362 */     return this._owner.getThreadData().J2KOptions.bUseEPHMarker;
/*     */   }
/*     */ 
/*     */   public void setUseEphMarker(boolean value) {
/* 366 */     this._owner.getThreadData().J2KOptions.bUseEPHMarker = value;
/*     */   }
/*     */ 
/*     */   public CodecsJpeg2000RegionOfInterest getRegionOfInterest() {
/* 370 */     return this._owner.getThreadData().J2KOptions.uROIControl;
/*     */   }
/*     */ 
/*     */   public void setRegionOfInterest(CodecsJpeg2000RegionOfInterest value) {
/* 374 */     this._owner.getThreadData().J2KOptions.uROIControl = value;
/*     */   }
/*     */ 
/*     */   public boolean getUseRegionOfInterest() {
/* 378 */     return this._owner.getThreadData().J2KOptions.bUseROI;
/*     */   }
/*     */ 
/*     */   public void setUseRegionOfInterest(boolean value) {
/* 382 */     this._owner.getThreadData().J2KOptions.bUseROI = value;
/*     */   }
/*     */ 
/*     */   public float getRegionOfInterestWeight() {
/* 386 */     return this._owner.getThreadData().J2KOptions.fROIWeight;
/*     */   }
/*     */ 
/*     */   public void setRegionOfInterestWeight(float value) {
/* 390 */     this._owner.getThreadData().J2KOptions.fROIWeight = value;
/*     */   }
/*     */ 
/*     */   public LeadRect getRegionOfInterestRectangle() {
/* 394 */     return this._owner.getThreadData().J2KOptions.rcROI;
/*     */   }
/*     */ 
/*     */   public void setRegionOfInterestRectangle(LeadRect value) {
/* 398 */     this._owner.getThreadData().J2KOptions.rcROI = value;
/*     */   }
/*     */ 
/*     */   public int getAlphaChannelActiveBits() {
/* 402 */     return this._owner.getThreadData().J2KOptions.uAlphaChannelActiveBits;
/*     */   }
/*     */ 
/*     */   public void setAlphaChannelActiveBits(int value) {
/* 406 */     this._owner.getThreadData().J2KOptions.uAlphaChannelActiveBits = value;
/*     */   }
/*     */ 
/*     */   public boolean getAlphaChannelLossless() {
/* 410 */     return this._owner.getThreadData().J2KOptions.bAlphaChannelLossless;
/*     */   }
/*     */ 
/*     */   public void setAlphaChannelLossless(boolean value) {
/* 414 */     this._owner.getThreadData().J2KOptions.bAlphaChannelLossless = value;
/*     */   }
/*     */ 
/*     */   public CodecsJpeg2000PrecinctSize getPrecinctSize() {
/* 418 */     return this._owner.getThreadData().J2KOptions.uPrecinctSize;
/*     */   }
/*     */ 
/*     */   public void setPrecinctSize(CodecsJpeg2000PrecinctSize value) {
/* 422 */     this._owner.getThreadData().J2KOptions.uPrecinctSize = value;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpeg2000SaveOptions
 * JD-Core Version:    0.6.2
 */