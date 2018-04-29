/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ import leadtools.RasterColor;
/*     */ 
/*     */ public class CodecsTxtLoadOptions
/*     */ {
/*     */   private CodecsOptions owner;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   7 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.Enabled", isEnabled());
/*   8 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.FontColor", getFontColor());
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.Highlight", getHighlight());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.FontSize", getFontSize());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.FaceName", getFaceName());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.Bold", isBold());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.Italic", isItalic());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.Underline", isUnderline());
/*  15 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.Strikethrough", isStrikethrough());
/*  16 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.UseSystemLocale", isUseSystemLocale());
/*  17 */     CodecsOptionsSerializer.writeOption(dic, "Txt.Load.BackColor", getBackColor());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  21 */     setEnabled(CodecsOptionsSerializer.readOption(dic, "Txt.Load.Enabled", isEnabled()));
/*  22 */     setFontColor(CodecsOptionsSerializer.readOption(dic, "Txt.Load.FontColor", getFontColor()));
/*  23 */     setHighlight(CodecsOptionsSerializer.readOption(dic, "Txt.Load.Highlight", getHighlight()));
/*  24 */     setFontSize(CodecsOptionsSerializer.readOption(dic, "Txt.Load.FontSize", getFontSize()));
/*  25 */     setFaceName(CodecsOptionsSerializer.readOption(dic, "Txt.Load.FaceName", getFaceName()));
/*  26 */     setBold(CodecsOptionsSerializer.readOption(dic, "Txt.Load.Bold", isBold()));
/*  27 */     setItalic(CodecsOptionsSerializer.readOption(dic, "Txt.Load.Italic", isItalic()));
/*  28 */     setUnderline(CodecsOptionsSerializer.readOption(dic, "Txt.Load.Underline", isUnderline()));
/*  29 */     setStrikethrough(CodecsOptionsSerializer.readOption(dic, "Txt.Load.Strikethrough", isStrikethrough()));
/*  30 */     setUseSystemLocale(CodecsOptionsSerializer.readOption(dic, "Txt.Load.UseSystemLocale", isUseSystemLocale()));
/*  31 */     setBackColor(CodecsOptionsSerializer.readOption(dic, "Txt.Load.BackColor", getBackColor()));
/*     */   }
/*     */ 
/*     */   CodecsTxtLoadOptions(CodecsOptions owner)
/*     */   {
/*  37 */     this.owner = owner;
/*     */   }
/*     */ 
/*     */   CodecsTxtLoadOptions copy(CodecsOptions owner) {
/*  41 */     CodecsTxtLoadOptions copy = new CodecsTxtLoadOptions(owner);
/*  42 */     copy.setBackColor(getBackColor());
/*  43 */     copy.setBold(isBold());
/*  44 */     copy.setEnabled(isEnabled());
/*  45 */     copy.setFaceName(getFaceName());
/*  46 */     copy.setFontColor(getFontColor());
/*  47 */     copy.setFontSize(getFontSize());
/*  48 */     copy.setHighlight(getHighlight());
/*  49 */     copy.setItalic(isItalic());
/*  50 */     copy.setStrikethrough(isStrikethrough());
/*  51 */     copy.setUseSystemLocale(isUseSystemLocale());
/*  52 */     copy.setUnderline(isUnderline());
/*     */ 
/*  54 */     return copy;
/*     */   }
/*     */ 
/*     */   private FILETXTOPTIONS getOpts() {
/*  58 */     return this.owner.getThreadData().pThreadLoadSettings.TXTOptions;
/*     */   }
/*     */ 
/*     */   public RasterColor getBackColor() {
/*  62 */     return RasterColor.fromColorRef(getOpts().crBackColor);
/*     */   }
/*     */ 
/*     */   public void setBackColor(RasterColor backColor) {
/*  66 */     getOpts().crBackColor = backColor.getColorRef();
/*     */   }
/*     */ 
/*     */   public boolean isBold() {
/*  70 */     return getOpts().bBold;
/*     */   }
/*     */ 
/*     */   public void setBold(boolean bold) {
/*  74 */     getOpts().bBold = bold;
/*     */   }
/*     */ 
/*     */   public boolean isEnabled() {
/*  78 */     return getOpts().bEnabled;
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean enabled) {
/*  82 */     getOpts().bEnabled = enabled;
/*     */   }
/*     */ 
/*     */   public String getFaceName() {
/*  86 */     return getOpts().szFaceName;
/*     */   }
/*     */ 
/*     */   public void setFaceName(String faceName) {
/*  90 */     getOpts().szFaceName = faceName;
/*     */   }
/*     */ 
/*     */   public RasterColor getFontColor() {
/*  94 */     return RasterColor.fromColorRef(getOpts().crFontColor);
/*     */   }
/*     */ 
/*     */   public void setFontColor(RasterColor fontColor) {
/*  98 */     getOpts().crFontColor = fontColor.getColorRef();
/*     */   }
/*     */ 
/*     */   public int getFontSize() {
/* 102 */     return getOpts().nFontSize;
/*     */   }
/*     */ 
/*     */   public void setFontSize(int fontSize) {
/* 106 */     getOpts().nFontSize = fontSize;
/*     */   }
/*     */ 
/*     */   public RasterColor getHighlight() {
/* 110 */     return RasterColor.fromColorRef(getOpts().crHighlight);
/*     */   }
/*     */ 
/*     */   public void setHighlight(RasterColor highlightColor) {
/* 114 */     getOpts().crHighlight = highlightColor.getColorRef();
/*     */   }
/*     */ 
/*     */   public boolean isItalic() {
/* 118 */     return getOpts().bItalic;
/*     */   }
/*     */ 
/*     */   public void setItalic(boolean italic) {
/* 122 */     getOpts().bItalic = italic;
/*     */   }
/*     */ 
/*     */   public boolean isStrikethrough() {
/* 126 */     return getOpts().bStrikeThrough;
/*     */   }
/*     */ 
/*     */   public void setStrikethrough(boolean strikethrough) {
/* 130 */     getOpts().bStrikeThrough = strikethrough;
/*     */   }
/*     */ 
/*     */   public boolean isUnderline() {
/* 134 */     return getOpts().bUnderLine;
/*     */   }
/*     */ 
/*     */   public void setUnderline(boolean underline) {
/* 138 */     getOpts().bUnderLine = underline;
/*     */   }
/*     */ 
/*     */   public boolean isUseSystemLocale() {
/* 142 */     return getOpts().bUseSystemLocale;
/*     */   }
/*     */ 
/*     */   public void setUseSystemLocale(boolean useSystemLocale) {
/* 146 */     getOpts().bUseSystemLocale = useSystemLocale;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsTxtLoadOptions
 * JD-Core Version:    0.6.2
 */