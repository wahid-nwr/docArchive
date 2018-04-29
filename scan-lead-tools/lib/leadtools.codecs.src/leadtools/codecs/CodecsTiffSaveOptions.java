/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ 
/*     */ public class CodecsTiffSaveOptions
/*     */ {
/*     */   private CodecsOptions _owner;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   6 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.NoSubFileType", getNoSubFileType());
/*   7 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.NoPageNumber", getNoPageNumber());
/*   8 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.NoPalette", getNoPalette());
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.UseImageFileDirectoryOffset", getUseImageFileDirectoryOffset());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.ImageFileDirectoryOffset", getImageFileDirectoryOffset());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.UsePhotometricInterpretation", getUsePhotometricInterpretation());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.PhotometricInterpretation", getPhotometricInterpretation().getValue());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.UseTileSize", getUseTileSize());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.TileWidth", getTileWidth());
/*  15 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.TileHeight", getTileHeight());
/*  16 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.PreservePalette", getPreservePalette());
/*  17 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.UsePredictor", getUsePredictor());
/*  18 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.SavePlanar", getSavePlanar());
/*  19 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Save.NoLzwAutoClear", getNoLzwAutoClear());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  23 */     setNoSubFileType(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.NoSubFileType", getNoSubFileType()));
/*  24 */     setNoPageNumber(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.NoPageNumber", getNoPageNumber()));
/*  25 */     setNoPalette(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.NoPalette", getNoPalette()));
/*  26 */     setUseImageFileDirectoryOffset(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.UseImageFileDirectoryOffset", getUseImageFileDirectoryOffset()));
/*  27 */     setImageFileDirectoryOffset(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.ImageFileDirectoryOffset", getImageFileDirectoryOffset()));
/*  28 */     setUsePhotometricInterpretation(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.UsePhotometricInterpretation", getUsePhotometricInterpretation()));
/*  29 */     setPhotometricInterpretation(CodecsTiffPhotometricInterpretation.forValue(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.PhotometricInterpretation", getPhotometricInterpretation().getValue())));
/*  30 */     setUseTileSize(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.UseTileSize", getUseTileSize()));
/*  31 */     setTileWidth(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.TileWidth", getTileWidth()));
/*  32 */     setTileHeight(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.TileHeight", getTileHeight()));
/*  33 */     setPreservePalette(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.PreservePalette", getPreservePalette()));
/*  34 */     setUsePredictor(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.UsePredictor", getUsePredictor()));
/*  35 */     setSavePlanar(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.SavePlanar", getSavePlanar()));
/*  36 */     setNoLzwAutoClear(CodecsOptionsSerializer.readOption(dic, "Tiff.Save.NoLzwAutoClear", getNoLzwAutoClear()));
/*     */   }
/*     */ 
/*     */   CodecsTiffSaveOptions(CodecsOptions owner)
/*     */   {
/*  43 */     this._owner = owner;
/*     */   }
/*     */ 
/*     */   CodecsTiffSaveOptions copy(CodecsOptions owner) {
/*  47 */     CodecsTiffSaveOptions copy = new CodecsTiffSaveOptions(owner);
/*  48 */     copy.setImageFileDirectoryOffset(getImageFileDirectoryOffset());
/*  49 */     copy.setNoLzwAutoClear(getNoLzwAutoClear());
/*  50 */     copy.setNoPageNumber(getNoPageNumber());
/*  51 */     copy.setNoPalette(getNoPalette());
/*  52 */     copy.setNoSubFileType(getNoSubFileType());
/*  53 */     copy.setPhotometricInterpretation(getPhotometricInterpretation());
/*  54 */     copy.setPreservePalette(getPreservePalette());
/*  55 */     copy.setSavePlanar(getSavePlanar());
/*  56 */     copy.setTileHeight(getTileHeight());
/*  57 */     copy.setTileWidth(getTileWidth());
/*  58 */     copy.setUseImageFileDirectoryOffset(getUseImageFileDirectoryOffset());
/*  59 */     copy.setUsePhotometricInterpretation(getUsePhotometricInterpretation());
/*  60 */     copy.setUsePredictor(getUsePredictor());
/*  61 */     copy.setUseTileSize(getUseTileSize());
/*     */ 
/*  63 */     return copy;
/*     */   }
/*     */ 
/*     */   public boolean getNoSubFileType()
/*     */   {
/*  68 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 2);
/*     */   }
/*     */ 
/*     */   public void setNoSubFileType(boolean value) {
/*  72 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 2, value);
/*     */   }
/*     */ 
/*     */   public boolean getNoPageNumber()
/*     */   {
/*  77 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 32768);
/*     */   }
/*     */ 
/*     */   public void setNoPageNumber(boolean value) {
/*  81 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 32768, value);
/*     */   }
/*     */ 
/*     */   public boolean getNoPalette()
/*     */   {
/*  86 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 64);
/*     */   }
/*     */ 
/*     */   public void setNoPalette(boolean value) {
/*  90 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 64, value);
/*     */   }
/*     */ 
/*     */   public boolean getUseImageFileDirectoryOffset()
/*     */   {
/*  95 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 131072);
/*     */   }
/*     */ 
/*     */   public void setUseImageFileDirectoryOffset(boolean value) {
/*  99 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 131072, value);
/*     */   }
/*     */ 
/*     */   public long getImageFileDirectoryOffset()
/*     */   {
/* 104 */     return this._owner.getSaveFileOption().IFD;
/*     */   }
/*     */ 
/*     */   public void setImageFileDirectoryOffset(long value) {
/* 108 */     this._owner.getSaveFileOption().IFD = value;
/*     */   }
/*     */ 
/*     */   public boolean getUsePhotometricInterpretation()
/*     */   {
/* 113 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 33554432);
/*     */   }
/*     */ 
/*     */   public void setUsePhotometricInterpretation(boolean value) {
/* 117 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 33554432, value);
/*     */   }
/*     */ 
/*     */   public CodecsTiffPhotometricInterpretation getPhotometricInterpretation()
/*     */   {
/* 122 */     return CodecsTiffPhotometricInterpretation.forValue(this._owner.getSaveFileOption().PhotometricInterpretation);
/*     */   }
/*     */ 
/*     */   public void setPhotometricInterpretation(CodecsTiffPhotometricInterpretation value) {
/* 126 */     this._owner.getSaveFileOption().PhotometricInterpretation = value.getValue();
/*     */   }
/*     */ 
/*     */   public boolean getUseTileSize()
/*     */   {
/* 131 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 67108864);
/*     */   }
/*     */ 
/*     */   public void setUseTileSize(boolean value) {
/* 135 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 67108864, value);
/*     */   }
/*     */ 
/*     */   public int getTileWidth()
/*     */   {
/* 140 */     return this._owner.getSaveFileOption().TileWidth;
/*     */   }
/*     */ 
/*     */   public void setTileWidth(int value) {
/* 144 */     this._owner.getSaveFileOption().TileWidth = value;
/*     */   }
/*     */ 
/*     */   public int getTileHeight()
/*     */   {
/* 149 */     return this._owner.getSaveFileOption().TileHeight;
/*     */   }
/*     */ 
/*     */   public void setTileHeight(int value) {
/* 153 */     this._owner.getSaveFileOption().TileHeight = value;
/*     */   }
/*     */ 
/*     */   public boolean getPreservePalette()
/*     */   {
/* 158 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 268435456);
/*     */   }
/*     */ 
/*     */   public void setPreservePalette(boolean value) {
/* 162 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 268435456, value);
/*     */   }
/*     */ 
/*     */   public boolean getUsePredictor()
/*     */   {
/* 167 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, -2147483648);
/*     */   }
/*     */ 
/*     */   public void setUsePredictor(boolean value) {
/* 171 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, -2147483648, value);
/*     */   }
/*     */ 
/*     */   public boolean getSavePlanar()
/*     */   {
/* 177 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 2);
/*     */   }
/*     */ 
/*     */   public void setSavePlanar(boolean value) {
/* 181 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 2, value);
/*     */   }
/*     */ 
/*     */   public boolean getNoLzwAutoClear()
/*     */   {
/* 186 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 4);
/*     */   }
/*     */ 
/*     */   public void setNoLzwAutoClear(boolean value) {
/* 190 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 4, value);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsTiffSaveOptions
 * JD-Core Version:    0.6.2
 */