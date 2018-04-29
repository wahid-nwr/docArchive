/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ import leadtools.ArgumentOutOfRangeException;
/*     */ 
/*     */ public class CodecsPdfLoadOptions
/*     */ {
/*     */   private CodecsOptions _owner;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   8 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.UseLibFonts", getUseLibFonts());
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.DisplayDepth", getDisplayDepth());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.TextAlpha", getTextAlpha());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.GraphicsAlpha", getGraphicsAlpha());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.Password", getPassword());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.DisableCropping", getDisableCropping());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.DisableCieColors", getDisableCieColors());
/*  15 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.EnableInterpolate", getEnableInterpolate());
/*  16 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.UsePdfEngine", getUsePdfEngine());
/*  17 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.HideAnnotations", getHideAnnotations());
/*  18 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.HideFormFields", getHideFormFields());
/*  19 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Load.HideDigitalSignatures", getHideDigitalSignatures());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  23 */     setUseLibFonts(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.UseLibFonts", getUseLibFonts()));
/*  24 */     setDisplayDepth(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.DisplayDepth", getDisplayDepth()));
/*  25 */     setTextAlpha(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.TextAlpha", getTextAlpha()));
/*  26 */     setGraphicsAlpha(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.GraphicsAlpha", getGraphicsAlpha()));
/*  27 */     setPassword(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.Password", getPassword()));
/*  28 */     setDisableCropping(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.DisableCropping", getDisableCropping()));
/*  29 */     setDisableCieColors(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.DisableCieColors", getDisableCieColors()));
/*  30 */     setEnableInterpolate(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.EnableInterpolate", getEnableInterpolate()));
/*  31 */     setUsePdfEngine(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.UsePdfEngine", getUsePdfEngine()));
/*     */ 
/*  33 */     setHideAnnotations(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.HideAnnotations", getHideAnnotations()));
/*  34 */     setHideFormFields(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.HideFormFields", getHideFormFields()));
/*  35 */     setHideDigitalSignatures(CodecsOptionsSerializer.readOption(dic, "Pdf.Load.HideDigitalSignatures", getHideDigitalSignatures()));
/*     */ 
/*  38 */     if (CodecsOptionsSerializer.readOption(dic, "Pdf.Load.HideAllAnnotations", false))
/*  39 */       setHideAnnotations(true);
/*     */   }
/*     */ 
/*     */   CodecsPdfLoadOptions(CodecsOptions owner)
/*     */   {
/*  46 */     this._owner = owner;
/*     */   }
/*     */ 
/*     */   CodecsPdfLoadOptions copy(CodecsOptions owner) {
/*  50 */     CodecsPdfLoadOptions copy = new CodecsPdfLoadOptions(owner);
/*  51 */     copy.setUseLibFonts(getUseLibFonts());
/*  52 */     copy.setDisableCieColors(getDisableCieColors());
/*  53 */     copy.setDisableCropping(getDisableCropping());
/*  54 */     copy.setDisplayDepth(getDisplayDepth());
/*  55 */     copy.setEnableInterpolate(getEnableInterpolate());
/*  56 */     copy.setGraphicsAlpha(getGraphicsAlpha());
/*  57 */     copy.setPassword(getPassword());
/*  58 */     copy.setTextAlpha(getTextAlpha());
/*  59 */     copy.setUseLibFonts(getUseLibFonts());
/*     */ 
/*  61 */     return copy;
/*     */   }
/*     */ 
/*     */   public boolean getUseLibFonts()
/*     */   {
/*  66 */     return this._owner.getThreadData().pThreadLoadSettings.PDFOptions.bUseLibFonts;
/*     */   }
/*     */ 
/*     */   public void setUseLibFonts(boolean value) {
/*  70 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.bUseLibFonts = value;
/*     */   }
/*     */ 
/*     */   public int getDisplayDepth()
/*     */   {
/*  75 */     return this._owner.getThreadData().pThreadLoadSettings.PDFOptions.nDisplayDepth;
/*     */   }
/*     */ 
/*     */   public void setDisplayDepth(int value) {
/*  79 */     if ((value != 0) && (value != 1) && (value != 4) && (value != 8) && (value != 24))
/*  80 */       throw new ArgumentOutOfRangeException("DisplayDepth", value, "Must be 0, 1, 4, 8, or 24");
/*  81 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.nDisplayDepth = value;
/*     */   }
/*     */ 
/*     */   public int getTextAlpha()
/*     */   {
/*  86 */     return this._owner.getThreadData().pThreadLoadSettings.PDFOptions.nTextAlpha;
/*     */   }
/*     */ 
/*     */   public void setTextAlpha(int value)
/*     */   {
/*  91 */     if ((value != 1) && (value != 2) && (value != 4)) {
/*  92 */       throw new ArgumentOutOfRangeException("TextAlpha", value, "Must be 1, 2, 4");
/*     */     }
/*  94 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.nTextAlpha = value;
/*     */   }
/*     */ 
/*     */   public int getGraphicsAlpha()
/*     */   {
/*  99 */     return this._owner.getThreadData().pThreadLoadSettings.PDFOptions.nGraphicsAlpha;
/*     */   }
/*     */ 
/*     */   public void setGraphicsAlpha(int value) {
/* 103 */     if ((value != 1) && (value != 2) && (value != 4)) {
/* 104 */       throw new ArgumentOutOfRangeException("GraphicsAlpha", value, "Must be 1, 2, 4");
/*     */     }
/* 106 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.nGraphicsAlpha = value;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/* 111 */     return this._owner.getThreadData().pThreadLoadSettings.PDFOptions.szPassword;
/*     */   }
/*     */ 
/*     */   public void setPassword(String value) {
/* 115 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.szPassword = value;
/*     */   }
/*     */ 
/*     */   public boolean getDisableCropping()
/*     */   {
/* 120 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 1);
/*     */   }
/*     */ 
/*     */   public void setDisableCropping(boolean value) {
/* 124 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 1, value);
/*     */   }
/*     */ 
/*     */   public boolean getDisableCieColors()
/*     */   {
/* 129 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 16);
/*     */   }
/*     */ 
/*     */   public void setDisableCieColors(boolean value) {
/* 133 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 16, value);
/*     */   }
/*     */ 
/*     */   public boolean getEnableInterpolate()
/*     */   {
/* 138 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 256);
/*     */   }
/*     */ 
/*     */   public void setEnableInterpolate(boolean value) {
/* 142 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 256, value);
/*     */   }
/*     */ 
/*     */   public boolean getUsePdfEngine() {
/* 146 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 4096);
/*     */   }
/*     */ 
/*     */   public void setUsePdfEngine(boolean value) {
/* 150 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 4096, value);
/*     */   }
/*     */ 
/*     */   public boolean getHideAnnotations()
/*     */   {
/* 155 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 1048576);
/*     */   }
/*     */ 
/*     */   public void setHideAnnotations(boolean value) {
/* 159 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 1048576, value);
/*     */   }
/*     */ 
/*     */   public boolean getHideFormFields()
/*     */   {
/* 164 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 2097152);
/*     */   }
/*     */ 
/*     */   public void setHideFormFields(boolean value) {
/* 168 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 2097152, value);
/*     */   }
/*     */ 
/*     */   public boolean getHideDigitalSignatures()
/*     */   {
/* 173 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 2097152);
/*     */   }
/*     */ 
/*     */   public void setHideDigitalSignatures(boolean value) {
/* 177 */     this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFOptions.uFlags, 2097152, value);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPdfLoadOptions
 * JD-Core Version:    0.6.2
 */