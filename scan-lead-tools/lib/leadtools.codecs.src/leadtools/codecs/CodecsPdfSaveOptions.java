/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ 
/*     */ public class CodecsPdfSaveOptions
/*     */ {
/*     */   private CodecsOptions _owner;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   6 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.UserPassword", getUserPassword());
/*   7 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.OwnerPassword", getOwnerPassword());
/*   8 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.Use128BitEncryption", getUse128BitEncryption());
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.PrintDocument", getPrintDocument());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.PrintFaithful", getPrintFaithful());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.ModifyDocument", getModifyDocument());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.ExtractText", getExtractText());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.ExtractTextGraphics", getExtractTextGraphics());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.ModifyAnnotation", getModifyAnnotation());
/*  15 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.FillForm", getFillForm());
/*  16 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.AssembleDocument", getAssembleDocument());
/*  17 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.TextEncoding", getTextEncoding().getValue());
/*  18 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.LowMemoryUsage", getLowMemoryUsage());
/*  19 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.UseImageResolution", getUseImageResolution());
/*  20 */     CodecsOptionsSerializer.writeOption(dic, "Pdf.Save.Version", getVersion().getValue());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  24 */     setUserPassword(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.UserPassword", getUserPassword()));
/*  25 */     setOwnerPassword(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.OwnerPassword", getOwnerPassword()));
/*  26 */     setUse128BitEncryption(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.Use128BitEncryption", getUse128BitEncryption()));
/*  27 */     setPrintDocument(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.PrintDocument", getPrintDocument()));
/*  28 */     setPrintFaithful(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.PrintFaithful", getPrintFaithful()));
/*  29 */     setModifyDocument(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.ModifyDocument", getModifyDocument()));
/*  30 */     setExtractText(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.ExtractText", getExtractText()));
/*  31 */     setExtractTextGraphics(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.ExtractTextGraphics", getExtractTextGraphics()));
/*  32 */     setModifyAnnotation(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.ModifyAnnotation", getModifyAnnotation()));
/*  33 */     setFillForm(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.FillForm", getFillForm()));
/*  34 */     setAssembleDocument(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.AssembleDocument", getAssembleDocument()));
/*  35 */     setTextEncoding(CodecsPdfTextEncoding.forValue(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.TextEncoding", getTextEncoding().getValue())));
/*  36 */     setLowMemoryUsage(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.LowMemoryUsage", getLowMemoryUsage()));
/*  37 */     setUseImageResolution(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.UseImageResolution", getUseImageResolution()));
/*  38 */     setVersion(CodecsRasterPdfVersion.forValue(CodecsOptionsSerializer.readOption(dic, "Pdf.Save.Version", getVersion().getValue())));
/*     */   }
/*     */ 
/*     */   CodecsPdfSaveOptions(CodecsOptions owner)
/*     */   {
/*  45 */     this._owner = owner;
/*     */   }
/*     */ 
/*     */   CodecsPdfSaveOptions copy(CodecsOptions owner) {
/*  49 */     CodecsPdfSaveOptions copy = new CodecsPdfSaveOptions(owner);
/*  50 */     copy.setAssembleDocument(getAssembleDocument());
/*  51 */     copy.setExtractText(getExtractText());
/*  52 */     copy.setExtractTextGraphics(getExtractTextGraphics());
/*  53 */     copy.setFillForm(getFillForm());
/*  54 */     copy.setLowMemoryUsage(getLowMemoryUsage());
/*  55 */     copy.setModifyAnnotation(getModifyAnnotation());
/*  56 */     copy.setModifyDocument(getModifyDocument());
/*  57 */     copy.setOwnerPassword(getOwnerPassword());
/*  58 */     copy.setPrintDocument(getPrintDocument());
/*  59 */     copy.setPrintFaithful(getPrintFaithful());
/*  60 */     copy.setSavePdfA(getSavePdfA());
/*  61 */     copy.setSavePdfv13(getSavePdfv13());
/*  62 */     copy.setSavePdfv14(getSavePdfv14());
/*  63 */     copy.setSavePdfv15(getSavePdfv15());
/*  64 */     copy.setSavePdfv16(getSavePdfv16());
/*  65 */     copy.setTextEncoding(getTextEncoding());
/*  66 */     copy.setUse128BitEncryption(getUse128BitEncryption());
/*  67 */     copy.setUseImageResolution(getUseImageResolution());
/*  68 */     copy.setUserPassword(getUserPassword());
/*  69 */     copy.setVersion(getVersion());
/*     */ 
/*  71 */     return copy;
/*     */   }
/*     */ 
/*     */   public String getUserPassword()
/*     */   {
/*  76 */     return this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.szUserPassword;
/*     */   }
/*     */ 
/*     */   public void setUserPassword(String value) {
/*  80 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.szUserPassword = value;
/*     */   }
/*     */ 
/*     */   public String getOwnerPassword()
/*     */   {
/*  85 */     return this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.szOwnerPassword;
/*     */   }
/*     */ 
/*     */   public void setOwnerPassword(String value) {
/*  89 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.szOwnerPassword = value;
/*     */   }
/*     */ 
/*     */   public boolean getUse128BitEncryption()
/*     */   {
/*  94 */     return this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.b128bit;
/*     */   }
/*     */ 
/*     */   public void setUse128BitEncryption(boolean value) {
/*  98 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.b128bit = value;
/*     */   }
/*     */ 
/*     */   public boolean getPrintDocument()
/*     */   {
/* 103 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 4);
/*     */   }
/*     */ 
/*     */   public void setPrintDocument(boolean value) {
/* 107 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 4, value);
/*     */   }
/*     */ 
/*     */   public boolean getPrintFaithful()
/*     */   {
/* 112 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 2048);
/*     */   }
/*     */ 
/*     */   public void setPrintFaithful(boolean value) {
/* 116 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 2048, value);
/*     */   }
/*     */ 
/*     */   public boolean getModifyDocument()
/*     */   {
/* 121 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 8);
/*     */   }
/*     */ 
/*     */   public void setModifyDocument(boolean value) {
/* 125 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 8, value);
/*     */   }
/*     */ 
/*     */   public boolean getExtractText()
/*     */   {
/* 130 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 16);
/*     */   }
/*     */ 
/*     */   public void setExtractText(boolean value) {
/* 134 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 16, value);
/*     */   }
/*     */ 
/*     */   public boolean getExtractTextGraphics()
/*     */   {
/* 139 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 512);
/*     */   }
/*     */ 
/*     */   public void setExtractTextGraphics(boolean value) {
/* 143 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 512, value);
/*     */   }
/*     */ 
/*     */   public boolean getModifyAnnotation()
/*     */   {
/* 148 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 32);
/*     */   }
/*     */ 
/*     */   public void setModifyAnnotation(boolean value) {
/* 152 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 32, value);
/*     */   }
/*     */ 
/*     */   public boolean getFillForm()
/*     */   {
/* 157 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 256);
/*     */   }
/*     */ 
/*     */   public void setFillForm(boolean value) {
/* 161 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 256, value);
/*     */   }
/*     */ 
/*     */   public boolean getAssembleDocument()
/*     */   {
/* 166 */     return Tools.isFlagged(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 1024);
/*     */   }
/*     */ 
/*     */   public void setAssembleDocument(boolean value) {
/* 170 */     this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags = Tools.setFlag1(this._owner.getThreadData().pThreadLoadSettings.PDFSaveOptions.dwEncryptFlags, 1024, value);
/*     */   }
/*     */ 
/*     */   public CodecsPdfTextEncoding getTextEncoding()
/*     */   {
/* 175 */     if (Tools.isFlagged(this._owner.getSaveFileOption().Flags, 2097152))
/* 176 */       return CodecsPdfTextEncoding.HEX;
/* 177 */     if (Tools.isFlagged(this._owner.getSaveFileOption().Flags, 1048576)) {
/* 178 */       return CodecsPdfTextEncoding.BASE85;
/*     */     }
/* 180 */     return CodecsPdfTextEncoding.NONE;
/*     */   }
/*     */ 
/*     */   public void setTextEncoding(CodecsPdfTextEncoding value) {
/* 184 */     if (value == CodecsPdfTextEncoding.HEX)
/*     */     {
/* 186 */       this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 1048576, false);
/* 187 */       this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 2097152, true);
/*     */     }
/* 189 */     else if (value == CodecsPdfTextEncoding.BASE85)
/*     */     {
/* 191 */       this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 2097152, false);
/* 192 */       this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 1048576, true);
/*     */     }
/*     */     else
/*     */     {
/* 196 */       this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 2097152, false);
/* 197 */       this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 1048576, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getLowMemoryUsage()
/*     */   {
/* 203 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 536870912);
/*     */   }
/*     */ 
/*     */   public void setLowMemoryUsage(boolean value) {
/* 207 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 536870912, value);
/*     */   }
/*     */ 
/*     */   public boolean getUseImageResolution()
/*     */   {
/* 212 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags, 16777216);
/*     */   }
/*     */ 
/*     */   public void setUseImageResolution(boolean value) {
/* 216 */     this._owner.getSaveFileOption().Flags = Tools.setFlag1(this._owner.getSaveFileOption().Flags, 16777216, value);
/*     */   }
/*     */ 
/*     */   private void resetPdfSaveFileOptionFlags()
/*     */   {
/* 221 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 256, false);
/* 222 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 512, false);
/* 223 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 1024, false);
/* 224 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 8192, false);
/* 225 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 16384, false);
/*     */   }
/*     */ 
/*     */   public CodecsRasterPdfVersion getVersion() {
/* 229 */     if (Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 256))
/* 230 */       return CodecsRasterPdfVersion.PDFA;
/* 231 */     if (Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 512))
/* 232 */       return CodecsRasterPdfVersion.V14;
/* 233 */     if (Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 1024))
/* 234 */       return CodecsRasterPdfVersion.V15;
/* 235 */     if (Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 8192))
/* 236 */       return CodecsRasterPdfVersion.V16;
/* 237 */     if (Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 16384)) {
/* 238 */       return CodecsRasterPdfVersion.V13;
/*     */     }
/* 240 */     return CodecsRasterPdfVersion.V12;
/*     */   }
/*     */ 
/*     */   public void setVersion(CodecsRasterPdfVersion value) {
/* 244 */     resetPdfSaveFileOptionFlags();
/*     */ 
/* 246 */     switch (1.$SwitchMap$leadtools$codecs$CodecsRasterPdfVersion[value.ordinal()])
/*     */     {
/*     */     case 1:
/* 249 */       this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 256, true);
/* 250 */       break;
/*     */     case 2:
/* 253 */       this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 512, true);
/* 254 */       break;
/*     */     case 3:
/* 257 */       this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 1024, true);
/* 258 */       break;
/*     */     case 4:
/* 261 */       this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 8192, true);
/* 262 */       break;
/*     */     case 5:
/* 265 */       this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 16384, true);
/* 266 */       break;
/*     */     case 6:
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getSavePdfA()
/*     */   {
/* 277 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 256);
/*     */   }
/*     */ 
/*     */   public void setSavePdfA(boolean value) {
/* 281 */     resetPdfSaveFileOptionFlags();
/* 282 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 256, value);
/*     */   }
/*     */ 
/*     */   public boolean getSavePdfv14()
/*     */   {
/* 287 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 512);
/*     */   }
/*     */ 
/*     */   public void setSavePdfv14(boolean value) {
/* 291 */     resetPdfSaveFileOptionFlags();
/* 292 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 512, value);
/*     */   }
/*     */ 
/*     */   public boolean getSavePdfv15()
/*     */   {
/* 297 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 1024);
/*     */   }
/*     */ 
/*     */   public void setSavePdfv15(boolean value) {
/* 301 */     resetPdfSaveFileOptionFlags();
/* 302 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 1024, value);
/*     */   }
/*     */ 
/*     */   public boolean getSavePdfv16()
/*     */   {
/* 307 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 8192);
/*     */   }
/*     */ 
/*     */   public void setSavePdfv16(boolean value) {
/* 311 */     resetPdfSaveFileOptionFlags();
/* 312 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 8192, value);
/*     */   }
/*     */ 
/*     */   public boolean getSavePdfv13()
/*     */   {
/* 317 */     return Tools.isFlagged(this._owner.getSaveFileOption().Flags2, 16384);
/*     */   }
/*     */ 
/*     */   public void setSavePdfv13(boolean value) {
/* 321 */     resetPdfSaveFileOptionFlags();
/* 322 */     this._owner.getSaveFileOption().Flags2 = Tools.setFlag1(this._owner.getSaveFileOption().Flags2, 16384, value);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPdfSaveOptions
 * JD-Core Version:    0.6.2
 */