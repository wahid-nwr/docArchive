/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsLoadSvgOptions
/*    */ {
/*    */   private boolean _allowPolylineText;
/*    */   private boolean _dropShapes;
/*    */   private boolean _dropImages;
/*    */   private boolean _dropText;
/*    */   private boolean _forConversion;
/*    */   private boolean _ignoreXmlParsingErrors;
/*    */   private int _maximumElements;
/*    */ 
/*    */   public boolean getAllowPolylineText()
/*    */   {
/*  9 */     return this._allowPolylineText;
/*    */   }
/*    */   public void setAllowPolylineText(boolean value) {
/* 12 */     this._allowPolylineText = value;
/*    */   }
/*    */ 
/*    */   public boolean getDropShapes()
/*    */   {
/* 17 */     return this._dropShapes;
/*    */   }
/*    */   public void setDropShapes(boolean value) {
/* 20 */     this._dropShapes = value;
/*    */   }
/*    */ 
/*    */   public boolean getDropImages()
/*    */   {
/* 25 */     return this._dropImages;
/*    */   }
/*    */   public void setDropImages(boolean value) {
/* 28 */     this._dropImages = value;
/*    */   }
/*    */ 
/*    */   public boolean getDropText()
/*    */   {
/* 33 */     return this._dropText;
/*    */   }
/*    */   public void setDropText(boolean value) {
/* 36 */     this._dropText = value;
/*    */   }
/*    */ 
/*    */   public boolean getForConversion()
/*    */   {
/* 41 */     return this._forConversion;
/*    */   }
/*    */   public void setForConversion(boolean value) {
/* 44 */     this._forConversion = value;
/*    */   }
/*    */ 
/*    */   public boolean getIgnoreXmlParsingErrors()
/*    */   {
/* 49 */     return this._ignoreXmlParsingErrors;
/*    */   }
/*    */   public void setIgnoreXmlParsingErrors(boolean value) {
/* 52 */     this._ignoreXmlParsingErrors = value;
/*    */   }
/*    */ 
/*    */   public int getMaximumElements()
/*    */   {
/* 57 */     return this._maximumElements;
/*    */   }
/*    */   public void setMaximumElements(int value) {
/* 60 */     if (value < 0) throw new IllegalArgumentException("MaximumElements must be a value greater than or equal to 0");
/* 61 */     this._maximumElements = value;
/*    */   }
/*    */ 
/*    */   void toUnmanaged(LOADSVGOPTIONS unmanaged) {
/* 65 */     unmanaged.uStructSize = 0;
/* 66 */     unmanaged.uFlags = 0;
/* 67 */     if (this._allowPolylineText) unmanaged.uFlags |= 1;
/* 68 */     if (this._dropShapes) unmanaged.uFlags |= 2;
/* 69 */     if (this._dropImages) unmanaged.uFlags |= 4;
/* 70 */     if (this._dropText) unmanaged.uFlags |= 8;
/* 71 */     if (this._forConversion) unmanaged.uFlags |= 16;
/* 72 */     if (this._ignoreXmlParsingErrors) unmanaged.uFlags |= 32;
/* 73 */     unmanaged.uMaximumElements = this._maximumElements;
/*    */   }
/*    */ 
/*    */   public CodecsLoadSvgOptions clone()
/*    */   {
/* 78 */     CodecsLoadSvgOptions ret = new CodecsLoadSvgOptions();
/* 79 */     ret.setAllowPolylineText(getAllowPolylineText());
/* 80 */     ret.setDropShapes(getDropShapes());
/* 81 */     ret.setDropImages(getDropImages());
/* 82 */     ret.setDropText(getDropText());
/* 83 */     ret.setForConversion(getForConversion());
/* 84 */     ret.setIgnoreXmlParsingErrors(getIgnoreXmlParsingErrors());
/* 85 */     ret.setMaximumElements(getMaximumElements());
/* 86 */     return ret;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsLoadSvgOptions
 * JD-Core Version:    0.6.2
 */