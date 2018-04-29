/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsTiffLoadOptions
/*    */ {
/*    */   private CodecsOptions _owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  6 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Load.ImageFileDirectoryOffset", getImageFileDirectoryOffset());
/*  7 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Load.UseImageFileDirectoryOffset", getUseImageFileDirectoryOffset());
/*  8 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Load.IgnoreViewPerspective", getIgnoreViewPerspective());
/*  9 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Load.IgnorePhotometricInterpretation", getIgnorePhotometricInterpretation());
/* 10 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Load.UseFastConversion", getUseFastConversion());
/* 11 */     CodecsOptionsSerializer.writeOption(dic, "Tiff.Load.IgnoreAdobeColorTransform", getIgnoreAdobeColorTransform());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 15 */     setImageFileDirectoryOffset(CodecsOptionsSerializer.readOption(dic, "Tiff.Load.ImageFileDirectoryOffset", getImageFileDirectoryOffset()));
/* 16 */     setUseImageFileDirectoryOffset(CodecsOptionsSerializer.readOption(dic, "Tiff.Load.UseImageFileDirectoryOffset", getUseImageFileDirectoryOffset()));
/* 17 */     setIgnoreViewPerspective(CodecsOptionsSerializer.readOption(dic, "Tiff.Load.IgnoreViewPerspective", getIgnoreViewPerspective()));
/* 18 */     setIgnorePhotometricInterpretation(CodecsOptionsSerializer.readOption(dic, "Tiff.Load.IgnorePhotometricInterpretation", getIgnorePhotometricInterpretation()));
/* 19 */     setUseFastConversion(CodecsOptionsSerializer.readOption(dic, "Tiff.Load.UseFastConversion", getUseFastConversion()));
/* 20 */     setIgnoreAdobeColorTransform(CodecsOptionsSerializer.readOption(dic, "Tiff.Load.IgnoreAdobeColorTransform", getIgnoreAdobeColorTransform()));
/*    */   }
/*    */ 
/*    */   CodecsTiffLoadOptions(CodecsOptions owner)
/*    */   {
/* 27 */     this._owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsTiffLoadOptions copy(CodecsOptions owner) {
/* 31 */     CodecsTiffLoadOptions copy = new CodecsTiffLoadOptions(owner);
/* 32 */     copy.setIgnoreAdobeColorTransform(getIgnoreAdobeColorTransform());
/* 33 */     copy.setIgnorePhotometricInterpretation(getIgnorePhotometricInterpretation());
/* 34 */     copy.setIgnoreViewPerspective(getIgnoreViewPerspective());
/* 35 */     copy.setImageFileDirectoryOffset(getImageFileDirectoryOffset());
/* 36 */     copy.setUseFastConversion(getUseFastConversion());
/* 37 */     copy.setUseImageFileDirectoryOffset(getUseImageFileDirectoryOffset());
/*    */ 
/* 39 */     return copy;
/*    */   }
/*    */ 
/*    */   public long getImageFileDirectoryOffset()
/*    */   {
/* 44 */     return this._owner.getLoadFileOption().IFD;
/*    */   }
/*    */ 
/*    */   public void setImageFileDirectoryOffset(long value) {
/* 48 */     this._owner.getLoadFileOption().IFD = value;
/*    */   }
/*    */ 
/*    */   public boolean getUseImageFileDirectoryOffset()
/*    */   {
/* 53 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 1024);
/*    */   }
/*    */ 
/*    */   public void setUseImageFileDirectoryOffset(boolean value) {
/* 57 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 1024, value);
/*    */   }
/*    */ 
/*    */   public boolean getIgnoreViewPerspective()
/*    */   {
/* 62 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 2097152);
/*    */   }
/*    */ 
/*    */   public void setIgnoreViewPerspective(boolean value) {
/* 66 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 2097152, value);
/*    */   }
/*    */ 
/*    */   public boolean getIgnorePhotometricInterpretation()
/*    */   {
/* 71 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 64);
/*    */   }
/*    */ 
/*    */   public void setIgnorePhotometricInterpretation(boolean value) {
/* 75 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 64, value);
/*    */   }
/*    */ 
/*    */   public boolean getUseFastConversion()
/*    */   {
/* 80 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 4194304);
/*    */   }
/*    */ 
/*    */   public void setUseFastConversion(boolean value) {
/* 84 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 4194304, value);
/*    */   }
/*    */ 
/*    */   public boolean getIgnoreAdobeColorTransform()
/*    */   {
/* 89 */     return Tools.isFlagged(this._owner.getLoadFileOption().Flags, 67108864);
/*    */   }
/*    */ 
/*    */   public void setIgnoreAdobeColorTransform(boolean value) {
/* 93 */     this._owner.getLoadFileOption().Flags = Tools.setFlag1(this._owner.getLoadFileOption().Flags, 67108864, value);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsTiffLoadOptions
 * JD-Core Version:    0.6.2
 */