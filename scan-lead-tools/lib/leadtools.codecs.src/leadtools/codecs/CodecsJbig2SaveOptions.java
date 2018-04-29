/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ 
/*     */ public class CodecsJbig2SaveOptions
/*     */ {
/*     */   private CodecsOptions owner;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   5 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageTypicalPredictionOn", isImageTypicalPredictionOn());
/*   6 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageTemplateType", getImageTemplateType());
/*   7 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageQualityFactor", getImageQualityFactor());
/*   8 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageGbatX1", getImageGbatX1());
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageGbatY1", getImageGbatY1());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageGbatX2", getImageGbatX2());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageGbatY2", getImageGbatY2());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageGbatX3", getImageGbatX3());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageGbatY3", getImageGbatY3());
/*  14 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageGbatX4", getImageGbatX4());
/*  15 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.ImageGbatY4", getImageGbatY4());
/*  16 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextTemplateType", getTextTemplateType());
/*  17 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextGbatX1", getTextGbatX1());
/*  18 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextGbatY1", getTextGbatY1());
/*  19 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextGbatX2", getTextGbatX2());
/*  20 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextGbatY2", getTextGbatY2());
/*  21 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextGbatX3", getTextGbatX3());
/*  22 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextGbatY3", getTextGbatY3());
/*  23 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextGbatX4", getTextGbatX4());
/*  24 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextGbatY4", getTextGbatY4());
/*  25 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextMinimumSymbolArea", getTextMinimumSymbolArea());
/*  26 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextMinimumSymbolWidth", getTextMinimumSymbolWidth());
/*  27 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextMinimumSymbolHeight", getTextMinimumSymbolHeight());
/*  28 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextMaximumSymbolArea", getTextMaximumSymbolArea());
/*  29 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextMaximumSymbolWidth", getTextMaximumSymbolWidth());
/*  30 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.TextMaximumSymbolHeight", getTextMaximumSymbolHeight());
/*  31 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.XResolution", getXResolution());
/*  32 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.YResolution", getYResolution());
/*  33 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.RemoveMarker", isRemoveMarker());
/*  34 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.RemoveHeaderSegment", isRemoveHeaderSegment());
/*  35 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.RemoveEopSegment", isRemoveEopSegment());
/*  36 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.RemoveEofSegment", isRemoveEofSegment());
/*  37 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Save.EnableDictionary", isEnableDictionary());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  41 */     setImageTypicalPredictionOn(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageTypicalPredictionOn", isImageTypicalPredictionOn()));
/*  42 */     setImageTemplateType(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageTemplateType", getImageTemplateType()));
/*  43 */     setImageQualityFactor(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageQualityFactor", getImageQualityFactor()));
/*  44 */     setImageGbatX1(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageGbatX1", getImageGbatX1()));
/*  45 */     setImageGbatY1(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageGbatY1", getImageGbatY1()));
/*  46 */     setImageGbatX2(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageGbatX2", getImageGbatX2()));
/*  47 */     setImageGbatY2(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageGbatY2", getImageGbatY2()));
/*  48 */     setImageGbatX3(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageGbatX3", getImageGbatX3()));
/*  49 */     setImageGbatY3(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageGbatY3", getImageGbatY3()));
/*  50 */     setImageGbatX4(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageGbatX4", getImageGbatX4()));
/*  51 */     setImageGbatY4(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.ImageGbatY4", getImageGbatY4()));
/*  52 */     setTextTemplateType(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextTemplateType", getTextTemplateType()));
/*  53 */     setTextGbatX1(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextGbatX1", getTextGbatX1()));
/*  54 */     setTextGbatY1(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextGbatY1", getTextGbatY1()));
/*  55 */     setTextGbatX2(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextGbatX2", getTextGbatX2()));
/*  56 */     setTextGbatY2(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextGbatY2", getTextGbatY2()));
/*  57 */     setTextGbatX3(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextGbatX3", getTextGbatX3()));
/*  58 */     setTextGbatY3(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextGbatY3", getTextGbatY3()));
/*  59 */     setTextGbatX4(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextGbatX4", getTextGbatX4()));
/*  60 */     setTextGbatY4(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextGbatY4", getTextGbatY4()));
/*  61 */     setTextMinimumSymbolArea(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextMinimumSymbolArea", getTextMinimumSymbolArea()));
/*  62 */     setTextMinimumSymbolWidth(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextMinimumSymbolWidth", getTextMinimumSymbolWidth()));
/*  63 */     setTextMinimumSymbolHeight(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextMinimumSymbolHeight", getTextMinimumSymbolHeight()));
/*  64 */     setTextMaximumSymbolArea(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextMaximumSymbolArea", getTextMaximumSymbolArea()));
/*  65 */     setTextMaximumSymbolWidth(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextMaximumSymbolWidth", getTextMaximumSymbolWidth()));
/*  66 */     setTextMaximumSymbolHeight(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.TextMaximumSymbolHeight", getTextMaximumSymbolHeight()));
/*  67 */     setXResolution(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.XResolution", getXResolution()));
/*  68 */     setYResolution(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.YResolution", getYResolution()));
/*  69 */     setRemoveMarker(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.RemoveMarker", isRemoveMarker()));
/*  70 */     setRemoveHeaderSegment(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.RemoveHeaderSegment", isRemoveHeaderSegment()));
/*  71 */     setRemoveEopSegment(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.RemoveEopSegment", isRemoveEopSegment()));
/*  72 */     setRemoveEofSegment(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.RemoveEofSegment", isRemoveEofSegment()));
/*  73 */     setEnableDictionary(CodecsOptionsSerializer.readOption(dic, "Jbig2.Save.EnableDictionary", isEnableDictionary()));
/*     */   }
/*     */ 
/*     */   CodecsJbig2SaveOptions(CodecsOptions owner)
/*     */   {
/*  79 */     this.owner = owner;
/*     */   }
/*     */ 
/*     */   CodecsJbig2SaveOptions copy(CodecsOptions owner) {
/*  83 */     CodecsJbig2SaveOptions copy = new CodecsJbig2SaveOptions(owner);
/*  84 */     copy.setEnableDictionary(isEnableDictionary());
/*  85 */     copy.setImageGbatX1(getImageGbatX1());
/*  86 */     copy.setImageGbatX2(getImageGbatX2());
/*  87 */     copy.setImageGbatX3(getImageGbatX3());
/*  88 */     copy.setImageGbatX4(getImageGbatX4());
/*  89 */     copy.setImageGbatY1(getImageGbatY1());
/*  90 */     copy.setImageGbatY2(getImageGbatY2());
/*  91 */     copy.setImageGbatY3(getImageGbatY3());
/*  92 */     copy.setImageGbatY4(getImageGbatY4());
/*  93 */     copy.setImageQualityFactor(getImageQualityFactor());
/*  94 */     copy.setImageTemplateType(getImageTemplateType());
/*  95 */     copy.setImageTypicalPredictionOn(isImageTypicalPredictionOn());
/*  96 */     copy.setRemoveEofSegment(isRemoveEofSegment());
/*  97 */     copy.setRemoveEopSegment(isRemoveEopSegment());
/*  98 */     copy.setRemoveHeaderSegment(isRemoveHeaderSegment());
/*  99 */     copy.setRemoveMarker(isRemoveMarker());
/* 100 */     copy.setTextDifferentialThreshold(getTextDifferentialThreshold());
/* 101 */     copy.setTextGbatX1(getTextGbatX1());
/* 102 */     copy.setTextGbatX2(getTextGbatX2());
/* 103 */     copy.setTextGbatX3(getTextGbatX3());
/* 104 */     copy.setTextGbatX4(getTextGbatX4());
/* 105 */     copy.setTextGbatY1(getTextGbatY1());
/* 106 */     copy.setTextGbatY2(getTextGbatY2());
/* 107 */     copy.setTextGbatY3(getTextGbatY3());
/* 108 */     copy.setTextGbatY4(getTextGbatY4());
/* 109 */     copy.setTextKeepAllSymbols(isTextKeepAllSymbols());
/* 110 */     copy.setTextMaximumSymbolArea(getTextMaximumSymbolArea());
/* 111 */     copy.setTextMaximumSymbolHeight(getTextMaximumSymbolHeight());
/* 112 */     copy.setTextMaximumSymbolWidth(getTextMaximumSymbolWidth());
/* 113 */     copy.setTextMinimumSymbolArea(getTextMinimumSymbolArea());
/* 114 */     copy.setTextMinimumSymbolHeight(getTextMinimumSymbolHeight());
/* 115 */     copy.setTextMinimumSymbolWidth(getTextMinimumSymbolWidth());
/* 116 */     copy.setTextQualityFactor(getTextQualityFactor());
/* 117 */     copy.setTextRemoveUnrepeatedSymbol(isTextRemoveUnrepeatedSymbol());
/* 118 */     copy.setTextTemplateType(getTextTemplateType());
/* 119 */     copy.setXResolution(getXResolution());
/* 120 */     copy.setYResolution(getYResolution());
/*     */ 
/* 122 */     return copy;
/*     */   }
/*     */ 
/*     */   private FILEJBIG2OPTIONS getOpts() {
/* 126 */     return this.owner.getThreadData().pThreadLoadSettings.JBIG2Options;
/*     */   }
/*     */ 
/*     */   public boolean isEnableDictionary() {
/* 130 */     return Tools.isFlagged(getOpts().uFlags, 256);
/*     */   }
/*     */ 
/*     */   public void setEnableDictionary(boolean enableDictionary) {
/* 134 */     Tools.setFlag1(getOpts().uFlags, 256, enableDictionary);
/*     */   }
/*     */ 
/*     */   public int getImageGbatX1() {
/* 138 */     return getOpts().ImageGBATX1;
/*     */   }
/*     */ 
/*     */   public void setImageGbatX1(int value) {
/* 142 */     getOpts().ImageGBATX1 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getImageGbatX2() {
/* 146 */     return getOpts().ImageGBATX2;
/*     */   }
/*     */ 
/*     */   public void setImageGbatX2(int value) {
/* 150 */     getOpts().ImageGBATX2 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getImageGbatX3() {
/* 154 */     return getOpts().ImageGBATX3;
/*     */   }
/*     */ 
/*     */   public void setImageGbatX3(int value) {
/* 158 */     getOpts().ImageGBATX3 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getImageGbatX4() {
/* 162 */     return getOpts().ImageGBATX4;
/*     */   }
/*     */ 
/*     */   public void setImageGbatX4(int value) {
/* 166 */     getOpts().ImageGBATX4 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getImageGbatY1() {
/* 170 */     return getOpts().ImageGBATY1;
/*     */   }
/*     */ 
/*     */   public void setImageGbatY1(int value) {
/* 174 */     getOpts().ImageGBATY1 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getImageGbatY2() {
/* 178 */     return getOpts().ImageGBATY2;
/*     */   }
/*     */ 
/*     */   public void setImageGbatY2(int value) {
/* 182 */     getOpts().ImageGBATY2 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getImageGbatY3() {
/* 186 */     return getOpts().ImageGBATY3;
/*     */   }
/*     */ 
/*     */   public void setImageGbatY3(int value) {
/* 190 */     getOpts().ImageGBATY3 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getImageGbatY4() {
/* 194 */     return getOpts().ImageGBATY4;
/*     */   }
/*     */ 
/*     */   public void setImageGbatY4(int value) {
/* 198 */     getOpts().ImageGBATY4 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getImageQualityFactor() {
/* 202 */     return getOpts().uImageQFactor;
/*     */   }
/*     */ 
/*     */   public void setImageQualityFactor(int imageQualityFactor) {
/* 206 */     getOpts().uImageQFactor = imageQualityFactor;
/*     */   }
/*     */ 
/*     */   public int getImageTemplateType() {
/* 210 */     return getOpts().ucImageTemplateType;
/*     */   }
/*     */ 
/*     */   public void setImageTemplateType(int imageTemplateType) {
/* 214 */     getOpts().ucImageTemplateType = ((byte)imageTemplateType);
/*     */   }
/*     */ 
/*     */   public boolean isImageTypicalPredictionOn() {
/* 218 */     return Tools.isFlagged(getOpts().uFlags, 16);
/*     */   }
/*     */ 
/*     */   public void setImageTypicalPredictionOn(boolean imageTypicalPrediction) {
/* 222 */     Tools.setFlag1(getOpts().uFlags, 16, imageTypicalPrediction);
/*     */   }
/*     */ 
/*     */   public boolean isRemoveEofSegment() {
/* 226 */     return Tools.isFlagged(getOpts().uFlags, 8);
/*     */   }
/*     */ 
/*     */   public void setRemoveEofSegment(boolean removeEofSegment) {
/* 230 */     Tools.setFlag1(getOpts().uFlags, 8, removeEofSegment);
/*     */   }
/*     */ 
/*     */   public boolean isRemoveEopSegment() {
/* 234 */     return Tools.isFlagged(getOpts().uFlags, 4);
/*     */   }
/*     */ 
/*     */   public void setRemoveEopSegment(boolean removeEopSegment) {
/* 238 */     Tools.setFlag1(getOpts().uFlags, 4, removeEopSegment);
/*     */   }
/*     */ 
/*     */   public boolean isRemoveHeaderSegment() {
/* 242 */     return Tools.isFlagged(getOpts().uFlags, 2);
/*     */   }
/*     */ 
/*     */   public void setRemoveHeaderSegment(boolean removeHeaderSegment) {
/* 246 */     Tools.setFlag1(getOpts().uFlags, 2, removeHeaderSegment);
/*     */   }
/*     */ 
/*     */   public boolean isRemoveMarker() {
/* 250 */     return Tools.isFlagged(getOpts().uFlags, 1);
/*     */   }
/*     */ 
/*     */   public void setRemoveMarker(boolean removeMarker) {
/* 254 */     Tools.setFlag1(getOpts().uFlags, 1, removeMarker);
/*     */   }
/*     */ 
/*     */   public int getTextDifferentialThreshold() {
/* 258 */     return getOpts().uTextDifThreshold;
/*     */   }
/*     */ 
/*     */   public void setTextDifferentialThreshold(int threshold) {
/* 262 */     getOpts().uTextDifThreshold = threshold;
/*     */   }
/*     */ 
/*     */   public int getTextGbatX1() {
/* 266 */     return getOpts().TextGBATX1;
/*     */   }
/*     */ 
/*     */   public void setTextGbatX1(int value) {
/* 270 */     getOpts().TextGBATX1 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getTextGbatX2() {
/* 274 */     return getOpts().TextGBATX2;
/*     */   }
/*     */ 
/*     */   public void setTextGbatX2(int value) {
/* 278 */     getOpts().TextGBATX2 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getTextGbatX3() {
/* 282 */     return getOpts().TextGBATX3;
/*     */   }
/*     */ 
/*     */   public void setTextGbatX3(int value) {
/* 286 */     getOpts().TextGBATX3 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getTextGbatX4() {
/* 290 */     return getOpts().TextGBATX4;
/*     */   }
/*     */ 
/*     */   public void setTextGbatX4(int value) {
/* 294 */     getOpts().TextGBATX4 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getTextGbatY1() {
/* 298 */     return getOpts().TextGBATY1;
/*     */   }
/*     */ 
/*     */   public void setTextGbatY1(int value) {
/* 302 */     getOpts().TextGBATY1 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getTextGbatY2() {
/* 306 */     return getOpts().TextGBATY2;
/*     */   }
/*     */ 
/*     */   public void setTextGbatY2(int value) {
/* 310 */     getOpts().TextGBATY2 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getTextGbatY3() {
/* 314 */     return getOpts().TextGBATY3;
/*     */   }
/*     */ 
/*     */   public void setTextGbatY3(int value) {
/* 318 */     getOpts().TextGBATY3 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public int getTextGbatY4() {
/* 322 */     return getOpts().TextGBATY4;
/*     */   }
/*     */ 
/*     */   public void setTextGbatY4(int value) {
/* 326 */     getOpts().TextGBATY4 = ((byte)value);
/*     */   }
/*     */ 
/*     */   public boolean isTextKeepAllSymbols() {
/* 330 */     return Tools.isFlagged(getOpts().uFlags, 8192);
/*     */   }
/*     */ 
/*     */   public void setTextKeepAllSymbols(boolean keepAllSymbols) {
/* 334 */     Tools.setFlag1(getOpts().uFlags, 8192, keepAllSymbols);
/*     */   }
/*     */ 
/*     */   public int getTextMaximumSymbolArea() {
/* 338 */     return getOpts().uTextMaxSymArea;
/*     */   }
/*     */ 
/*     */   public void setTextMaximumSymbolArea(int maxSymbolArea) {
/* 342 */     getOpts().uTextMaxSymArea = maxSymbolArea;
/*     */   }
/*     */ 
/*     */   public int getTextMaximumSymbolHeight() {
/* 346 */     return getOpts().uTextMaxSymHeight;
/*     */   }
/*     */ 
/*     */   public void setTextMaximumSymbolHeight(int maxSymbolHeight) {
/* 350 */     getOpts().uTextMaxSymHeight = maxSymbolHeight;
/*     */   }
/*     */ 
/*     */   public int getTextMaximumSymbolWidth() {
/* 354 */     return getOpts().uTextMaxSymWidth;
/*     */   }
/*     */ 
/*     */   public void setTextMaximumSymbolWidth(int maxSymbolWidth) {
/* 358 */     getOpts().uTextMaxSymWidth = maxSymbolWidth;
/*     */   }
/*     */ 
/*     */   public int getTextMinimumSymbolArea() {
/* 362 */     return getOpts().uTextMinSymArea;
/*     */   }
/*     */ 
/*     */   public void setTextMinimumSymbolArea(int minSymbolArea) {
/* 366 */     getOpts().uTextMinSymArea = minSymbolArea;
/*     */   }
/*     */ 
/*     */   public int getTextMinimumSymbolHeight() {
/* 370 */     return getOpts().uTextMinSymHeight;
/*     */   }
/*     */ 
/*     */   public void setTextMinimumSymbolHeight(int minSymbolHeight) {
/* 374 */     getOpts().uTextMinSymHeight = minSymbolHeight;
/*     */   }
/*     */ 
/*     */   public int getTextMinimumSymbolWidth() {
/* 378 */     return getOpts().uTextMinSymWidth;
/*     */   }
/*     */ 
/*     */   public void setTextMinimumSymbolWidth(int minSymbolWidth) {
/* 382 */     getOpts().uTextMinSymWidth = minSymbolWidth;
/*     */   }
/*     */ 
/*     */   public int getTextQualityFactor() {
/* 386 */     return getOpts().uTextQFactor;
/*     */   }
/*     */ 
/*     */   public void setTextQualityFactor(int qualityFactor) {
/* 390 */     getOpts().uTextQFactor = qualityFactor;
/*     */   }
/*     */ 
/*     */   public boolean isTextRemoveUnrepeatedSymbol() {
/* 394 */     return Tools.isFlagged(getOpts().uFlags, 4096);
/*     */   }
/*     */ 
/*     */   public void setTextRemoveUnrepeatedSymbol(boolean removeUnrepeatedSymbol) {
/* 398 */     Tools.setFlag1(getOpts().uFlags, 4096, removeUnrepeatedSymbol);
/*     */   }
/*     */ 
/*     */   public int getTextTemplateType() {
/* 402 */     return getOpts().ucTextTemplateType;
/*     */   }
/*     */ 
/*     */   public void setTextTemplateType(int templateType) {
/* 406 */     getOpts().ucTextTemplateType = ((byte)templateType);
/*     */   }
/*     */ 
/*     */   public int getXResolution() {
/* 410 */     return getOpts().uXResolution;
/*     */   }
/*     */ 
/*     */   public void setXResolution(int xResolution) {
/* 414 */     getOpts().uXResolution = xResolution;
/*     */   }
/*     */ 
/*     */   public int getYResolution() {
/* 418 */     return getOpts().uYResolution;
/*     */   }
/*     */ 
/*     */   public void setYResolution(int yResolution) {
/* 422 */     getOpts().uYResolution = yResolution;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJbig2SaveOptions
 * JD-Core Version:    0.6.2
 */