/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ 
/*     */ public class CodecsJpegLoadOptions
/*     */ {
/*     */   private CodecsOptions _owner;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   8 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Load.DisableMmx", getDisableMmx());
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Load.DisableP3", getDisableP3());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Load.ForceCieLab", getForceCieLab());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Load.UseBadJpegPredictor", getUseBadJpegPredictor());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Load.ForceRgbFile", getForceRgbFile());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Load.UseFastConversion", getUseFastConversion());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg.Load.IgnoreAdobeColorTransform", getIgnoreAdobeColorTransform());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  18 */     setDisableMmx(CodecsOptionsSerializer.readOption(dic, "Jpeg.Load.DisableMmx", getDisableMmx()));
/*  19 */     setDisableP3(CodecsOptionsSerializer.readOption(dic, "Jpeg.Load.DisableP3", getDisableP3()));
/*  20 */     setForceCieLab(CodecsOptionsSerializer.readOption(dic, "Jpeg.Load.ForceCieLab", getForceCieLab()));
/*  21 */     setUseBadJpegPredictor(CodecsOptionsSerializer.readOption(dic, "Jpeg.Load.UseBadJpegPredictor", getUseBadJpegPredictor()));
/*  22 */     setForceRgbFile(CodecsOptionsSerializer.readOption(dic, "Jpeg.Load.ForceRgbFile", getForceRgbFile()));
/*  23 */     setUseFastConversion(CodecsOptionsSerializer.readOption(dic, "Jpeg.Load.UseFastConversion", getUseFastConversion()));
/*  24 */     setIgnoreAdobeColorTransform(CodecsOptionsSerializer.readOption(dic, "Jpeg.Load.IgnoreAdobeColorTransform", getIgnoreAdobeColorTransform()));
/*     */   }
/*     */ 
/*     */   CodecsJpegLoadOptions(CodecsOptions owner)
/*     */   {
/*  31 */     this._owner = owner;
/*     */   }
/*     */ 
/*     */   CodecsJpegLoadOptions copy(CodecsOptions owner) {
/*  35 */     CodecsJpegLoadOptions copy = new CodecsJpegLoadOptions(owner);
/*  36 */     copy.setForceCieLab(getForceCieLab());
/*  37 */     copy.setForceRgbFile(getForceRgbFile());
/*  38 */     copy.setIgnoreAdobeColorTransform(getIgnoreAdobeColorTransform());
/*  39 */     copy.setUseBadJpegPredictor(getUseBadJpegPredictor());
/*  40 */     copy.setUseFastConversion(getUseFastConversion());
/*     */ 
/*  42 */     return copy;
/*     */   }
/*     */ 
/*     */   public boolean getDisableMmx()
/*     */   {
/*  47 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 256);
/*     */   }
/*     */   public void setDisableMmx(boolean value) {
/*  50 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 256, value);
/*     */   }
/*     */ 
/*     */   public boolean getDisableP3() {
/*  54 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 512);
/*     */   }
/*     */   public void setDisableP3(boolean value) {
/*  57 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 512, value);
/*     */   }
/*     */ 
/*     */   public boolean getForceCieLab()
/*     */   {
/*  62 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 2048);
/*     */   }
/*     */ 
/*     */   public void setForceCieLab(boolean value) {
/*  66 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 2048, value);
/*     */   }
/*     */ 
/*     */   public boolean getUseBadJpegPredictor()
/*     */   {
/*  71 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 4096);
/*     */   }
/*     */ 
/*     */   public void setUseBadJpegPredictor(boolean value) {
/*  75 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 4096, value);
/*     */   }
/*     */ 
/*     */   public boolean getForceRgbFile()
/*     */   {
/*  80 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 16384);
/*     */   }
/*     */ 
/*     */   public void setForceRgbFile(boolean value) {
/*  84 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 16384, value);
/*     */   }
/*     */ 
/*     */   public boolean getUseFastConversion()
/*     */   {
/*  89 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 4194304);
/*     */   }
/*     */ 
/*     */   public void setUseFastConversion(boolean value) {
/*  93 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 4194304, value);
/*     */   }
/*     */ 
/*     */   public boolean getIgnoreAdobeColorTransform()
/*     */   {
/*  98 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 67108864);
/*     */   }
/*     */ 
/*     */   public void setIgnoreAdobeColorTransform(boolean value) {
/* 102 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 67108864, value);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpegLoadOptions
 * JD-Core Version:    0.6.2
 */