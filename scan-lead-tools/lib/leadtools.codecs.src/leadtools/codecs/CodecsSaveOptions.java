/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ import leadtools.LeadSize;
/*     */ import leadtools.RasterException;
/*     */ 
/*     */ public class CodecsSaveOptions
/*     */ {
/*     */   private CodecsOptions _owner;
/*     */   private boolean _markers;
/*     */   private boolean _tags;
/*     */   private boolean _geoKeys;
/*     */   private boolean _comments;
/*     */   private boolean _retrieveDataFromImage;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Save.Markers", getMarkers());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Save.Tags", getTags());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Save.GeoKeys", getGeoKeys());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Save.Comments", getComments());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Save.RetrieveDataFromImage", getRetrieveDataFromImage());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "Save.Password", getPassword());
/*  15 */     CodecsOptionsSerializer.writeOption(dic, "Save.MotorolaOrder", getMotorolaOrder());
/*     */ 
/*  17 */     CodecsOptionsSerializer.writeOption(dic, "Save.FixedPalette", getFixedPalette());
/*  18 */     CodecsOptionsSerializer.writeOption(dic, "Save.OptimizedPalette", getOptimizedPalette());
/*  19 */     CodecsOptionsSerializer.writeOption(dic, "Save.GrayOutput", getGrayOutput());
/*  20 */     CodecsOptionsSerializer.writeOption(dic, "Save.UseImageDitheringMethod", getUseImageDitheringMethod());
/*  21 */     CodecsOptionsSerializer.writeOption(dic, "Save.InitAlpha", getInitAlpha());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  25 */     setMarkers(CodecsOptionsSerializer.readOption(dic, "Lave.Markers", getMarkers()));
/*  26 */     setTags(CodecsOptionsSerializer.readOption(dic, "Lave.Tags", getTags()));
/*  27 */     setGeoKeys(CodecsOptionsSerializer.readOption(dic, "Lave.GeoKeys", getGeoKeys()));
/*  28 */     setComments(CodecsOptionsSerializer.readOption(dic, "Lave.Comments", getComments()));
/*  29 */     setRetrieveDataFromImage(CodecsOptionsSerializer.readOption(dic, "Lave.RetrieveDataFromImage", getRetrieveDataFromImage()));
/*  30 */     setPassword(CodecsOptionsSerializer.readOption(dic, "Lave.Password", getPassword()));
/*  31 */     setMotorolaOrder(CodecsOptionsSerializer.readOption(dic, "Lave.MotorolaOrder", getMotorolaOrder()));
/*     */ 
/*  33 */     setFixedPalette(CodecsOptionsSerializer.readOption(dic, "Lave.FixedPalette", getFixedPalette()));
/*  34 */     setOptimizedPalette(CodecsOptionsSerializer.readOption(dic, "Lave.OptimizedPalette", getOptimizedPalette()));
/*  35 */     setGrayOutput(CodecsOptionsSerializer.readOption(dic, "Lave.GrayOutput", getGrayOutput()));
/*  36 */     setUseImageDitheringMethod(CodecsOptionsSerializer.readOption(dic, "Lave.UseImageDitheringMethod", getUseImageDitheringMethod()));
/*  37 */     setInitAlpha(CodecsOptionsSerializer.readOption(dic, "Lave.InitAlpha", getInitAlpha()));
/*     */   }
/*     */ 
/*     */   CodecsSaveOptions(CodecsOptions owner)
/*     */   {
/*  49 */     this._owner = owner;
/*  50 */     this._markers = false;
/*  51 */     this._tags = false;
/*  52 */     this._geoKeys = false;
/*  53 */     this._comments = false;
/*  54 */     this._retrieveDataFromImage = false;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  59 */     this._owner.resetSaveFileOption(true);
/*  60 */     this._markers = false;
/*  61 */     this._tags = false;
/*  62 */     this._geoKeys = false;
/*  63 */     this._comments = false;
/*  64 */     this._retrieveDataFromImage = false;
/*     */   }
/*     */ 
/*     */   public boolean getMarkers()
/*     */   {
/*  69 */     return this._markers;
/*     */   }
/*     */ 
/*     */   public void setMarkers(boolean value) {
/*  73 */     this._markers = value;
/*     */   }
/*     */ 
/*     */   public boolean getTags()
/*     */   {
/*  78 */     return this._tags;
/*     */   }
/*     */ 
/*     */   public void setTags(boolean value) {
/*  82 */     this._tags = value;
/*     */   }
/*     */ 
/*     */   public boolean getGeoKeys()
/*     */   {
/*  87 */     return this._geoKeys;
/*     */   }
/*     */ 
/*     */   public void setGeoKeys(boolean value) {
/*  91 */     this._geoKeys = value;
/*     */   }
/*     */ 
/*     */   public boolean getComments()
/*     */   {
/*  96 */     return this._comments;
/*     */   }
/*     */ 
/*     */   public void setComments(boolean value) {
/* 100 */     this._comments = value;
/*     */   }
/*     */ 
/*     */   public boolean getRetrieveDataFromImage()
/*     */   {
/* 105 */     return this._retrieveDataFromImage;
/*     */   }
/*     */ 
/*     */   public void setRetrieveDataFromImage(boolean value) {
/* 109 */     this._retrieveDataFromImage = value;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/* 114 */     return this._owner.getSaveFileOption().szPassword;
/*     */   }
/*     */ 
/*     */   public void setPassword(String value) {
/* 118 */     this._owner.getSaveFileOption().szPassword = value;
/*     */   }
/*     */ 
/*     */   public boolean getMotorolaOrder() {
/* 122 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 262144);
/*     */   }
/*     */ 
/*     */   public void setMotorolaOrder(boolean value) {
/* 126 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 262144, value);
/*     */   }
/*     */ 
/*     */   public LeadSize[] getResolutions()
/*     */   {
/* 131 */     int[] count = { 0 };
/* 132 */     DIMENSION[][] resolutions = { null };
/* 133 */     int ret = ltfil.GetSaveResolution(count, resolutions);
/* 134 */     RasterException.checkErrorCode(ret);
/*     */ 
/* 136 */     LeadSize[] res = new LeadSize[count[0]];
/* 137 */     for (int i = 0; i < count[0]; i++) {
/* 138 */       res[i] = new LeadSize(resolutions[0][i].nWidth, resolutions[0][i].nHeight);
/*     */     }
/*     */ 
/* 141 */     return res;
/*     */   }
/*     */ 
/*     */   public void setResolutions(LeadSize[] resolutions)
/*     */   {
/* 146 */     if (resolutions.length <= 0) {
/* 147 */       return;
/*     */     }
/* 149 */     DIMENSION[] res = new DIMENSION[resolutions.length];
/* 150 */     for (int i = 0; i < resolutions.length; i++)
/*     */     {
/* 152 */       res[i] = new DIMENSION();
/* 153 */       res[i].nWidth = resolutions[i].getWidth();
/* 154 */       res[i].nHeight = resolutions[i].getHeight();
/*     */     }
/*     */ 
/* 157 */     int ret = ltfil.SetSaveResolution(resolutions.length, res);
/* 158 */     RasterException.checkErrorCode(ret);
/*     */   }
/*     */ 
/*     */   public boolean getFixedPalette() {
/* 162 */     return Tools.isFlagged(this._owner.getSaveFlags(), 1);
/*     */   }
/*     */ 
/*     */   public void setFixedPalette(boolean value) {
/* 166 */     this._owner.setSaveFlags(Tools.setFlag1(this._owner.getSaveFlags(), 1, value));
/*     */   }
/*     */ 
/*     */   public boolean getOptimizedPalette() {
/* 170 */     return Tools.isFlagged(this._owner.getSaveFlags(), 2);
/*     */   }
/*     */ 
/*     */   public void setOptimizedPalette(boolean value) {
/* 174 */     this._owner.setSaveFlags(Tools.setFlag1(this._owner.getSaveFlags(), 2, value));
/*     */   }
/*     */ 
/*     */   public boolean getGrayOutput() {
/* 178 */     return Tools.isFlagged(this._owner.getSaveFlags(), 8);
/*     */   }
/*     */ 
/*     */   public void setGrayOutput(boolean value) {
/* 182 */     this._owner.setSaveFlags(Tools.setFlag1(this._owner.getSaveFlags(), 8, value));
/*     */   }
/*     */ 
/*     */   public boolean getUseImageDitheringMethod() {
/* 186 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 134217728);
/*     */   }
/*     */ 
/*     */   public void setUseImageDitheringMethod(boolean value) {
/* 190 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 134217728, value);
/*     */   }
/*     */ 
/*     */   public boolean getInitAlpha() {
/* 194 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 16);
/*     */   }
/*     */ 
/*     */   public void setInitAlpha(boolean value) {
/* 198 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 16, value);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsSaveOptions
 * JD-Core Version:    0.6.2
 */