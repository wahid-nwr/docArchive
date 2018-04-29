/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ import leadtools.RasterImageFormat;
/*     */ 
/*     */ public class CodecsLoadOptions
/*     */ {
/*     */   private CodecsOptions _owner;
/*     */   private boolean _markers;
/*     */   private boolean _tags;
/*     */   private boolean _comments;
/*     */   private boolean _geoKeys;
/*     */   private boolean _allocateImage;
/*     */   private boolean _storeDataInImage;
/*     */   private RasterImageFormat _format;
/*     */   private boolean _allPages;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   8 */     CodecsOptionsSerializer.writeOption(dic, "Load.Markers", getMarkers());
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Load.Tags", getTags());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Load.GeoKeys", getGeoKeys());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Load.Comments", getComments());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Load.AllocateImage", getAllocateImage());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Load.StoreDataInImage", getStoreDataInImage());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "Load.Format", getFormat().getValue());
/*  15 */     CodecsOptionsSerializer.writeOption(dic, "Load.AllPages", getAllPages());
/*  16 */     CodecsOptionsSerializer.writeOption(dic, "Load.XResolution", getXResolution());
/*  17 */     CodecsOptionsSerializer.writeOption(dic, "Load.YResolution", getYResolution());
/*  18 */     CodecsOptionsSerializer.writeOption(dic, "Load.Passes", getPasses());
/*  19 */     CodecsOptionsSerializer.writeOption(dic, "Load.Rotated", getRotated());
/*  20 */     CodecsOptionsSerializer.writeOption(dic, "Load.Signed", getSigned());
/*  21 */     CodecsOptionsSerializer.writeOption(dic, "Load.InitAlpha", getInitAlpha());
/*  22 */     CodecsOptionsSerializer.writeOption(dic, "Load.PremultiplyAlpha", getPremultiplyAlpha());
/*  23 */     CodecsOptionsSerializer.writeOption(dic, "Load.UseNativeLoad", getUseNativeLoad());
/*  24 */     CodecsOptionsSerializer.writeOption(dic, "Load.AutoDetectAlpha", getAutoDetectAlpha());
/*  25 */     CodecsOptionsSerializer.writeOption(dic, "Load.FixedPalette", getFixedPalette());
/*  26 */     CodecsOptionsSerializer.writeOption(dic, "Load.NoInterlace", getNoInterlace());
/*  27 */     CodecsOptionsSerializer.writeOption(dic, "Load.Compressed", getCompressed());
/*  28 */     CodecsOptionsSerializer.writeOption(dic, "Load.SuperCompressed", getSuperCompressed());
/*  29 */     CodecsOptionsSerializer.writeOption(dic, "Load.TiledMemory", getTiledMemory());
/*  30 */     CodecsOptionsSerializer.writeOption(dic, "Load.NoTiledMemory", getNoTiledMemory());
/*  31 */     CodecsOptionsSerializer.writeOption(dic, "Load.DiskMemory", getDiskMemory());
/*  32 */     CodecsOptionsSerializer.writeOption(dic, "Load.NoDiskMemory", getNoDiskMemory());
/*  33 */     CodecsOptionsSerializer.writeOption(dic, "Load.LoadCorrupted", getLoadCorrupted());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  37 */     setMarkers(CodecsOptionsSerializer.readOption(dic, "Load.Markers", getMarkers()));
/*  38 */     setTags(CodecsOptionsSerializer.readOption(dic, "Load.Tags", getTags()));
/*  39 */     setGeoKeys(CodecsOptionsSerializer.readOption(dic, "Load.GeoKeys", getGeoKeys()));
/*  40 */     setComments(CodecsOptionsSerializer.readOption(dic, "Load.Comments", getComments()));
/*  41 */     setAllocateImage(CodecsOptionsSerializer.readOption(dic, "Load.AllocateImage", getAllocateImage()));
/*  42 */     setStoreDataInImage(CodecsOptionsSerializer.readOption(dic, "Load.StoreDataInImage", getStoreDataInImage()));
/*  43 */     setFormat(RasterImageFormat.forValue(CodecsOptionsSerializer.readOption(dic, "Load.Format", getFormat().getValue())));
/*  44 */     setAllPages(CodecsOptionsSerializer.readOption(dic, "Load.AllPages", getAllPages()));
/*  45 */     setXResolution(CodecsOptionsSerializer.readOption(dic, "Load.XResolution", getXResolution()));
/*  46 */     setYResolution(CodecsOptionsSerializer.readOption(dic, "Load.YResolution", getYResolution()));
/*  47 */     setPasses(CodecsOptionsSerializer.readOption(dic, "Load.Passes", getPasses()));
/*  48 */     setRotated(CodecsOptionsSerializer.readOption(dic, "Load.Rotated", getRotated()));
/*  49 */     setSigned(CodecsOptionsSerializer.readOption(dic, "Load.Signed", getSigned()));
/*  50 */     setInitAlpha(CodecsOptionsSerializer.readOption(dic, "Load.InitAlpha", getInitAlpha()));
/*  51 */     setPremultiplyAlpha(CodecsOptionsSerializer.readOption(dic, "Load.PremultiplyAlpha", getPremultiplyAlpha()));
/*  52 */     setUseNativeLoad(CodecsOptionsSerializer.readOption(dic, "Load.UseNativeLoad", getUseNativeLoad()));
/*  53 */     setAutoDetectAlpha(CodecsOptionsSerializer.readOption(dic, "Load.AutoDetectAlpha", getAutoDetectAlpha()));
/*  54 */     setFixedPalette(CodecsOptionsSerializer.readOption(dic, "Load.FixedPalette", getFixedPalette()));
/*  55 */     setNoInterlace(CodecsOptionsSerializer.readOption(dic, "Load.NoInterlace", getNoInterlace()));
/*  56 */     setCompressed(CodecsOptionsSerializer.readOption(dic, "Load.Compressed", getCompressed()));
/*  57 */     setSuperCompressed(CodecsOptionsSerializer.readOption(dic, "Load.SuperCompressed", getSuperCompressed()));
/*  58 */     setTiledMemory(CodecsOptionsSerializer.readOption(dic, "Load.TiledMemory", getTiledMemory()));
/*  59 */     setNoTiledMemory(CodecsOptionsSerializer.readOption(dic, "Load.NoTiledMemory", getNoTiledMemory()));
/*  60 */     setDiskMemory(CodecsOptionsSerializer.readOption(dic, "Load.DiskMemory", getDiskMemory()));
/*  61 */     setNoDiskMemory(CodecsOptionsSerializer.readOption(dic, "Load.NoDiskMemory", getNoDiskMemory()));
/*  62 */     setLoadCorrupted(CodecsOptionsSerializer.readOption(dic, "Load.LoadCorrupted", getLoadCorrupted()));
/*     */   }
/*     */ 
/*     */   CodecsLoadOptions(CodecsOptions owner)
/*     */   {
/*  82 */     this._owner = owner;
/*  83 */     this._markers = false;
/*  84 */     this._tags = false;
/*  85 */     this._geoKeys = false;
/*  86 */     this._comments = false;
/*  87 */     this._allocateImage = true;
/*  88 */     this._storeDataInImage = true;
/*  89 */     this._format = RasterImageFormat.UNKNOWN;
/*  90 */     this._allPages = false;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  95 */     this._owner.resetLoadFileOption(true);
/*  96 */     this._markers = false;
/*  97 */     this._tags = false;
/*  98 */     this._geoKeys = false;
/*  99 */     this._comments = false;
/* 100 */     this._allocateImage = true;
/* 101 */     this._storeDataInImage = true;
/* 102 */     this._allPages = false;
/*     */   }
/*     */ 
/*     */   public boolean getMarkers()
/*     */   {
/* 107 */     return this._markers;
/*     */   }
/*     */ 
/*     */   public void setMarkers(boolean value) {
/* 111 */     this._markers = value;
/*     */   }
/*     */ 
/*     */   public boolean getTags()
/*     */   {
/* 116 */     return this._tags;
/*     */   }
/*     */ 
/*     */   public void setTags(boolean value) {
/* 120 */     this._tags = value;
/*     */   }
/*     */ 
/*     */   public boolean getGeoKeys()
/*     */   {
/* 125 */     return this._geoKeys;
/*     */   }
/*     */ 
/*     */   public void setGeoKeys(boolean value) {
/* 129 */     this._geoKeys = value;
/*     */   }
/*     */ 
/*     */   public boolean getComments()
/*     */   {
/* 134 */     return this._comments;
/*     */   }
/*     */ 
/*     */   public void setComments(boolean value) {
/* 138 */     this._comments = value;
/*     */   }
/*     */ 
/*     */   public boolean getAllocateImage()
/*     */   {
/* 143 */     return this._allocateImage;
/*     */   }
/*     */ 
/*     */   public void setAllocateImage(boolean value) {
/* 147 */     this._allocateImage = value;
/*     */   }
/*     */ 
/*     */   public boolean getStoreDataInImage()
/*     */   {
/* 152 */     return this._storeDataInImage;
/*     */   }
/*     */ 
/*     */   public void setStoreDataInImage(boolean value)
/*     */   {
/* 157 */     this._storeDataInImage = value;
/*     */   }
/*     */ 
/*     */   public RasterImageFormat getFormat()
/*     */   {
/* 162 */     return this._format;
/*     */   }
/*     */ 
/*     */   public void setFormat(RasterImageFormat value)
/*     */   {
/* 167 */     this._format = value;
/*     */   }
/*     */ 
/*     */   public boolean getAllPages()
/*     */   {
/* 172 */     return this._allPages;
/*     */   }
/*     */ 
/*     */   public void setAllPages(boolean value)
/*     */   {
/* 177 */     this._allPages = value;
/*     */   }
/*     */ 
/*     */   public int getXResolution()
/*     */   {
/* 182 */     return this._owner.getLoadFileOption().XResolution;
/*     */   }
/*     */ 
/*     */   public void setXResolution(int value) {
/* 186 */     this._owner.getLoadFileOption().XResolution = value;
/*     */   }
/*     */ 
/*     */   public int getYResolution() {
/* 190 */     return this._owner.getLoadFileOption().YResolution;
/*     */   }
/*     */ 
/*     */   public void setYResolution(int value) {
/* 194 */     this._owner.getLoadFileOption().YResolution = value;
/*     */   }
/*     */ 
/*     */   public int getResolution() {
/* 198 */     return Math.max(this._owner.getLoadFileOption().XResolution, this._owner.getLoadFileOption().YResolution);
/*     */   }
/*     */ 
/*     */   public void setResolution(int value) {
/* 202 */     this._owner.getLoadFileOption().XResolution = value;
/* 203 */     this._owner.getLoadFileOption().YResolution = value;
/*     */   }
/*     */ 
/*     */   public int getPasses() {
/* 207 */     return this._owner.getLoadFileOption().Passes;
/*     */   }
/*     */ 
/*     */   public void setPasses(int value) {
/* 211 */     this._owner.getLoadFileOption().Passes = value;
/*     */   }
/*     */ 
/*     */   public boolean getRotated() {
/* 215 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 16);
/*     */   }
/*     */ 
/*     */   public void setRotated(boolean value) {
/* 219 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 16, value);
/*     */   }
/*     */ 
/*     */   public boolean getSigned() {
/* 223 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 128);
/*     */   }
/*     */ 
/*     */   public void setSigned(boolean value) {
/* 227 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 128, value);
/*     */   }
/*     */ 
/*     */   public boolean getInitAlpha() {
/* 231 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 16777216);
/*     */   }
/*     */ 
/*     */   public void setInitAlpha(boolean value) {
/* 235 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 16777216, value);
/*     */   }
/*     */ 
/*     */   public boolean getUseNativeLoad()
/*     */   {
/* 248 */     return false;
/*     */   }
/*     */ 
/*     */   public void setUseNativeLoad(boolean value) {
/*     */   }
/*     */ 
/*     */   public boolean getAutoDetectAlpha() {
/* 255 */     return false;
/*     */   }
/*     */ 
/*     */   public void setAutoDetectAlpha(boolean value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean getFixedPalette() {
/* 263 */     return Tools.isFlagged(this._owner.getLoadFlags(), 4);
/*     */   }
/*     */ 
/*     */   public void setFixedPalette(boolean value) {
/* 267 */     this._owner.setLoadFlags(Tools.setFlag1(this._owner.getLoadFlags(), 4, value));
/*     */   }
/*     */ 
/*     */   public boolean getNoInterlace() {
/* 271 */     return Tools.isFlagged(this._owner.getLoadFlags(), 8);
/*     */   }
/*     */ 
/*     */   public void setNoInterlace(boolean value) {
/* 275 */     this._owner.setLoadFlags(Tools.setFlag1(this._owner.getLoadFlags(), 8, value));
/*     */   }
/*     */ 
/*     */   public boolean getCompressed() {
/* 279 */     return Tools.isFlagged(this._owner.getLoadFlags(), 64);
/*     */   }
/*     */ 
/*     */   public void setCompressed(boolean value) {
/* 283 */     this._owner.setLoadFlags(Tools.setFlag1(this._owner.getLoadFlags(), 64, value));
/*     */   }
/*     */ 
/*     */   public boolean getSuperCompressed() {
/* 287 */     return Tools.isFlagged(this._owner.getLoadFlags(), 128);
/*     */   }
/*     */ 
/*     */   public void setSuperCompressed(boolean value) {
/* 291 */     this._owner.setLoadFlags(Tools.setFlag1(this._owner.getLoadFlags(), 128, value));
/*     */   }
/*     */ 
/*     */   public boolean getTiledMemory() {
/* 295 */     return Tools.isFlagged(this._owner.getLoadFlags(), 512);
/*     */   }
/*     */ 
/*     */   public void setTiledMemory(boolean value) {
/* 299 */     this._owner.setLoadFlags(Tools.setFlag1(this._owner.getLoadFlags(), 512, value));
/*     */   }
/*     */ 
/*     */   public boolean getNoTiledMemory() {
/* 303 */     return Tools.isFlagged(this._owner.getLoadFlags(), 1024);
/*     */   }
/*     */ 
/*     */   public void setNoTiledMemory(boolean value) {
/* 307 */     this._owner.setLoadFlags(Tools.setFlag1(this._owner.getLoadFlags(), 1024, value));
/*     */   }
/*     */ 
/*     */   public boolean getDiskMemory() {
/* 311 */     return Tools.isFlagged(this._owner.getLoadFlags(), 2048);
/*     */   }
/*     */ 
/*     */   public void setDiskMemory(boolean value) {
/* 315 */     this._owner.setLoadFlags(Tools.setFlag1(this._owner.getLoadFlags(), 2048, value));
/*     */   }
/*     */ 
/*     */   public boolean getNoDiskMemory() {
/* 319 */     return Tools.isFlagged(this._owner.getLoadFlags(), 4096);
/*     */   }
/*     */ 
/*     */   public void setNoDiskMemory(boolean value) {
/* 323 */     this._owner.setLoadFlags(Tools.setFlag1(this._owner.getLoadFlags(), 4096, value));
/*     */   }
/*     */ 
/*     */   public boolean getLoadCorrupted() {
/* 327 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 65536);
/*     */   }
/*     */ 
/*     */   public void setLoadCorrupted(boolean value) {
/* 331 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 65536, value);
/*     */   }
/*     */ 
/*     */   public boolean getPremultiplyAlpha() {
/* 335 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 1073741824);
/*     */   }
/*     */ 
/*     */   public void setPremultiplyAlpha(boolean value) {
/* 339 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 1073741824, value);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsLoadOptions
 * JD-Core Version:    0.6.2
 */