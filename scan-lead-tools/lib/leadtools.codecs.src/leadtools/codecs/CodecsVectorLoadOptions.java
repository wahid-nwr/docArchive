/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ import leadtools.RasterColor;
/*    */ 
/*    */ public class CodecsVectorLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  7 */     CodecsOptionsSerializer.writeOption(dic, "Vector.Load.ViewWidth", getViewWidth());
/*  8 */     CodecsOptionsSerializer.writeOption(dic, "Vector.Load.ViewHeight", getViewHeight());
/*  9 */     CodecsOptionsSerializer.writeOption(dic, "Vector.Load.ViewMode", getViewMode().getValue());
/* 10 */     CodecsOptionsSerializer.writeOption(dic, "Vector.Load.BitsPerPixel", getBitsPerPixel());
/* 11 */     CodecsOptionsSerializer.writeOption(dic, "Vector.Load.ForceBackgroundColor", isForceBackgroundColor());
/* 12 */     CodecsOptionsSerializer.writeOption(dic, "Vector.Load.BackgroundColor", getBackgroundColor());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 16 */     setViewWidth(CodecsOptionsSerializer.readOption(dic, "Vector.Load.ViewWidth", getViewWidth()));
/* 17 */     setViewHeight(CodecsOptionsSerializer.readOption(dic, "Vector.Load.ViewHeight", getViewHeight()));
/* 18 */     setViewMode(CodecsVectorViewMode.forValue(CodecsOptionsSerializer.readOption(dic, "Vector.Load.ViewMode", getViewMode().getValue())));
/* 19 */     setBitsPerPixel(CodecsOptionsSerializer.readOption(dic, "Vector.Load.BitsPerPixel", getBitsPerPixel()));
/* 20 */     setForceBackgroundColor(CodecsOptionsSerializer.readOption(dic, "Vector.Load.ForceBackgroundColor", isForceBackgroundColor()));
/* 21 */     setBackgroundColor(CodecsOptionsSerializer.readOption(dic, "Vector.Load.BackgroundColor", getBackgroundColor()));
/*    */   }
/*    */ 
/*    */   CodecsVectorLoadOptions(CodecsOptions owner)
/*    */   {
/* 27 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsVectorLoadOptions copy(CodecsOptions owner) {
/* 31 */     CodecsVectorLoadOptions copy = new CodecsVectorLoadOptions(owner);
/* 32 */     copy.setBackgroundColor(getBackgroundColor());
/* 33 */     copy.setBitsPerPixel(getBitsPerPixel());
/* 34 */     copy.setForceBackgroundColor(isForceBackgroundColor());
/* 35 */     copy.setViewHeight(getViewHeight());
/* 36 */     copy.setViewMode(getViewMode());
/* 37 */     copy.setViewWidth(getViewWidth());
/*    */ 
/* 39 */     return copy;
/*    */   }
/*    */ 
/*    */   private VECTOROPTIONS getOpts() {
/* 43 */     return this.owner.getThreadData().pThreadLoadSettings.VectorOptions;
/*    */   }
/*    */ 
/*    */   public RasterColor getBackgroundColor() {
/* 47 */     return RasterColor.fromColorRef(getOpts().BackgroundColor);
/*    */   }
/*    */ 
/*    */   public void setBackgroundColor(RasterColor backgroundColor) {
/* 51 */     getOpts().BackgroundColor = backgroundColor.getColorRef();
/*    */   }
/*    */ 
/*    */   public int getBitsPerPixel() {
/* 55 */     return getOpts().nBitsPerPixel;
/*    */   }
/*    */ 
/*    */   public void setBitsPerPixel(int bitsPerPixel) {
/* 59 */     getOpts().nBitsPerPixel = bitsPerPixel;
/*    */   }
/*    */ 
/*    */   public boolean isForceBackgroundColor() {
/* 63 */     return getOpts().bForceBackgroundColor;
/*    */   }
/*    */ 
/*    */   public void setForceBackgroundColor(boolean forceBGColor) {
/* 67 */     getOpts().bForceBackgroundColor = forceBGColor;
/*    */   }
/*    */ 
/*    */   public int getViewHeight() {
/* 71 */     return getOpts().Vec2DOptions.nViewHeight;
/*    */   }
/*    */ 
/*    */   public void setViewHeight(int viewHeight) {
/* 75 */     getOpts().Vec2DOptions.nViewHeight = viewHeight;
/*    */   }
/*    */ 
/*    */   public int getViewWidth() {
/* 79 */     return getOpts().Vec2DOptions.nViewWidth;
/*    */   }
/*    */ 
/*    */   public void setViewWidth(int viewWidth) {
/* 83 */     getOpts().Vec2DOptions.nViewWidth = viewWidth;
/*    */   }
/*    */ 
/*    */   public CodecsVectorViewMode getViewMode() {
/* 87 */     return getOpts().Vec2DOptions.ViewMode;
/*    */   }
/*    */ 
/*    */   public void setViewMode(CodecsVectorViewMode viewMode) {
/* 91 */     getOpts().Vec2DOptions.ViewMode = viewMode;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsVectorLoadOptions
 * JD-Core Version:    0.6.2
 */