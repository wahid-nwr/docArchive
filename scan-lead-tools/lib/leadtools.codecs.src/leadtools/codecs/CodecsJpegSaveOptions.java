/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ import leadtools.ArgumentOutOfRangeException;
/*     */ 
/*     */ public class CodecsJpegSaveOptions
/*     */ {
/*     */   private CodecsOptions _owner;
/*  43 */   private int _jpegQualityFactor = 2;
/*  44 */   private CodecsCmpQualityFactorPredefined _cmpQualityFactorPredefined = CodecsCmpQualityFactorPredefined.CUSTOM;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.QualityFactor", getQualityFactor());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.CmpQualityFactorPredefined", getCmpQualityFactorPredefined().getValue());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.YccStamp", getYccStamp());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.JpegStamp", getJpegStamp());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.SaveWithStamp", getSaveWithStamp());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.FixedPaletteStamp", getFixedPaletteStamp());
/*  15 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.SaveWithoutTimestamp", getSaveWithoutTimestamp());
/*  16 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.StampWidth", getStampWidth());
/*  17 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.StampHeight", getStampHeight());
/*  18 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.StampBitsPerPixel", getStampBitsPerPixel());
/*  19 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.DisableMmx", getDisableMmx());
/*  20 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.SaveOldJtif", getSaveOldJtif());
/*  21 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.DisableP3", getDisableP3());
/*  22 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Save.Passes", getPasses());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  26 */     setQualityFactor(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.QualityFactor", getQualityFactor()));
/*  27 */     setCmpQualityFactorPredefined(CodecsCmpQualityFactorPredefined.forValue(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.CmpQualityFactorPredefined", getCmpQualityFactorPredefined().getValue())));
/*  28 */     setYccStamp(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.YccStamp", getYccStamp()));
/*  29 */     setJpegStamp(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.JpegStamp", getJpegStamp()));
/*  30 */     setSaveWithStamp(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.SaveWithStamp", getSaveWithStamp()));
/*  31 */     setFixedPaletteStamp(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.FixedPaletteStamp", getFixedPaletteStamp()));
/*  32 */     setSaveWithoutTimestamp(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.SaveWithoutTimestamp", getSaveWithoutTimestamp()));
/*  33 */     setStampWidth(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.StampWidth", getStampWidth()));
/*  34 */     setStampHeight(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.StampHeight", getStampHeight()));
/*  35 */     setStampBitsPerPixel(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.StampBitsPerPixel", getStampBitsPerPixel()));
/*  36 */     setDisableMmx(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.DisableMmx", getDisableMmx()));
/*  37 */     setSaveOldJtif(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.SaveOldJtif", getSaveOldJtif()));
/*  38 */     setDisableP3(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.DisableP3", getDisableP3()));
/*  39 */     setPasses(CodecsOptionsSerializer.readOption(dic, "Jpeg.Save.Passes", getPasses()));
/*     */   }
/*     */ 
/*     */   CodecsJpegSaveOptions(CodecsOptions owner)
/*     */   {
/*  48 */     this._owner = owner;
/*     */   }
/*     */ 
/*     */   CodecsJpegSaveOptions copy(CodecsOptions owner) {
/*  52 */     CodecsJpegSaveOptions copy = new CodecsJpegSaveOptions(owner);
/*  53 */     copy.setCmpQualityFactorPredefined(getCmpQualityFactorPredefined());
/*  54 */     copy.setFixedPaletteStamp(getFixedPaletteStamp());
/*  55 */     copy.setJpegStamp(getJpegStamp());
/*  56 */     copy.setPasses(getPasses());
/*  57 */     copy.setQualityFactor(getQualityFactor());
/*  58 */     copy.setSaveOldJtif(getSaveOldJtif());
/*  59 */     copy.setSaveWithoutTimestamp(getSaveWithoutTimestamp());
/*  60 */     copy.setSaveWithStamp(getSaveWithStamp());
/*  61 */     copy.setStampBitsPerPixel(getStampBitsPerPixel());
/*  62 */     copy.setStampHeight(getStampHeight());
/*  63 */     copy.setStampWidth(getStampWidth());
/*     */ 
/*  65 */     return copy;
/*     */   }
/*     */ 
/*     */   public int getQualityFactor()
/*     */   {
/*  70 */     return this._jpegQualityFactor;
/*     */   }
/*     */ 
/*     */   public void setQualityFactor(int qualityFactor)
/*     */   {
/*  75 */     if ((qualityFactor < 2) || (qualityFactor > 255))
/*  76 */       throw new ArgumentOutOfRangeException("qualityFactor", qualityFactor, "JPEG quality factor must be a value from 2 to 255, where 2 is the highest quality and 255 is the most compression.");
/*  77 */     this._jpegQualityFactor = qualityFactor;
/*     */   }
/*     */ 
/*     */   public CodecsCmpQualityFactorPredefined getCmpQualityFactorPredefined()
/*     */   {
/*  82 */     return this._cmpQualityFactorPredefined;
/*     */   }
/*     */ 
/*     */   public void setCmpQualityFactorPredefined(CodecsCmpQualityFactorPredefined value)
/*     */   {
/*  87 */     this._cmpQualityFactorPredefined = value;
/*     */   }
/*     */ 
/*     */   public boolean getYccStamp()
/*     */   {
/*  92 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 512);
/*     */   }
/*     */ 
/*     */   public void setYccStamp(boolean value)
/*     */   {
/*  97 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 512, value);
/*     */   }
/*     */ 
/*     */   public boolean getJpegStamp()
/*     */   {
/* 102 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 4096);
/*     */   }
/*     */ 
/*     */   public void setJpegStamp(boolean value)
/*     */   {
/* 107 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 4096, value);
/*     */   }
/*     */ 
/*     */   public boolean getSaveWithStamp()
/*     */   {
/* 112 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 128);
/*     */   }
/*     */ 
/*     */   public void setSaveWithStamp(boolean value)
/*     */   {
/* 117 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 128, value);
/*     */   }
/*     */ 
/*     */   public boolean getFixedPaletteStamp()
/*     */   {
/* 122 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 256);
/*     */   }
/*     */ 
/*     */   public void setFixedPaletteStamp(boolean value) {
/* 126 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 256, value);
/*     */   }
/*     */ 
/*     */   public boolean getSaveWithoutTimestamp()
/*     */   {
/* 131 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 524288);
/*     */   }
/*     */ 
/*     */   public void setSaveWithoutTimestamp(boolean value) {
/* 135 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 524288, value);
/*     */   }
/*     */ 
/*     */   public int getStampWidth()
/*     */   {
/* 140 */     return this._owner.getSaveFileOption().StampWidth;
/*     */   }
/*     */ 
/*     */   public void setStampWidth(int value) {
/* 144 */     this._owner.getSaveFileOption().StampWidth = value;
/*     */   }
/*     */ 
/*     */   public int getStampHeight()
/*     */   {
/* 149 */     return this._owner.getSaveFileOption().StampHeight;
/*     */   }
/*     */ 
/*     */   public void setStampHeight(int value) {
/* 153 */     this._owner.getSaveFileOption().StampHeight = value;
/*     */   }
/*     */ 
/*     */   public int getStampBitsPerPixel()
/*     */   {
/* 158 */     return this._owner.getSaveFileOption().StampBits;
/*     */   }
/*     */ 
/*     */   public void setStampBitsPerPixel(int value) {
/* 162 */     this._owner.getSaveFileOption().StampBits = value;
/*     */   }
/*     */ 
/*     */   public boolean getSaveOldJtif()
/*     */   {
/* 167 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 16384);
/*     */   }
/*     */ 
/*     */   public void setSaveOldJtif(boolean value) {
/* 171 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 16384, value);
/*     */   }
/*     */ 
/*     */   public boolean getDisableMmx()
/*     */   {
/* 176 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 8192);
/*     */   }
/*     */ 
/*     */   public void setDisableMmx(boolean value) {
/* 180 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 8192, value);
/*     */   }
/*     */ 
/*     */   public boolean getDisableP3() {
/* 184 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 65536);
/*     */   }
/*     */ 
/*     */   public void setDisableP3(boolean value)
/*     */   {
/* 189 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 65536, value);
/*     */   }
/*     */ 
/*     */   public int getPasses()
/*     */   {
/* 194 */     return this._owner.getSaveFileOption().Passes;
/*     */   }
/*     */ 
/*     */   public void setPasses(int value) {
/* 198 */     this._owner.getSaveFileOption().Passes = value;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpegSaveOptions
 * JD-Core Version:    0.6.2
 */