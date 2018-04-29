/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ import leadtools.RasterColor;
/*     */ 
/*     */ public class CodecsGifSaveOptions
/*     */ {
/*     */   private CodecsOptions owner;
/*     */ 
/*     */   void writeXml(Map<String, String> dic)
/*     */   {
/*   7 */     CodecsOptionsSerializer.writeOption(dic, "Gif.Save.Interlaced", isInterlaced());
/*   8 */     CodecsOptionsSerializer.writeOption(dic, "Gif.Save.UseAnimationLoop", isUseAnimationLoop());
/*   9 */     CodecsOptionsSerializer.writeOption(dic, "Gif.Save.AnimationLoop", getAnimationLoop());
/*  10 */     CodecsOptionsSerializer.writeOption(dic, "Gif.Save.AnimationWidth", getAnimationWidth());
/*  11 */     CodecsOptionsSerializer.writeOption(dic, "Gif.Save.AnimationHeight", getAnimationHeight());
/*  12 */     CodecsOptionsSerializer.writeOption(dic, "Gif.Save.UseAnimationBackground", isUseAnimationBackground());
/*  13 */     CodecsOptionsSerializer.writeOption(dic, "Gif.Save.AnimationBackground", getAnimationBackground());
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/*  17 */     setInterlaced(CodecsOptionsSerializer.readOption(dic, "Gif.Save.Interlaced", isInterlaced()));
/*  18 */     setUseAnimationLoop(CodecsOptionsSerializer.readOption(dic, "Gif.Save.UseAnimationLoop", isUseAnimationLoop()));
/*  19 */     setAnimationLoop(CodecsOptionsSerializer.readOption(dic, "Gif.Save.AnimationLoop", getAnimationLoop()));
/*  20 */     setAnimationWidth(CodecsOptionsSerializer.readOption(dic, "Gif.Save.AnimationWidth", getAnimationWidth()));
/*  21 */     setAnimationHeight(CodecsOptionsSerializer.readOption(dic, "Gif.Save.AnimationHeight", getAnimationHeight()));
/*  22 */     setUseAnimationBackground(CodecsOptionsSerializer.readOption(dic, "Gif.Save.UseAnimationBackground", isUseAnimationBackground()));
/*  23 */     setAnimationBackground(CodecsOptionsSerializer.readOption(dic, "Gif.Save.AnimationBackground", getAnimationBackground()));
/*     */   }
/*     */ 
/*     */   CodecsGifSaveOptions(CodecsOptions owner)
/*     */   {
/*  29 */     this.owner = owner;
/*     */   }
/*     */ 
/*     */   CodecsGifSaveOptions copy(CodecsOptions owner) {
/*  33 */     CodecsGifSaveOptions copy = new CodecsGifSaveOptions(owner);
/*  34 */     copy.setAnimationBackground(getAnimationBackground());
/*  35 */     copy.setAnimationHeight(getAnimationHeight());
/*  36 */     copy.setAnimationLoop(getAnimationLoop());
/*  37 */     copy.setAnimationWidth(getAnimationWidth());
/*  38 */     copy.setInterlaced(isInterlaced());
/*  39 */     copy.setUseAnimationBackground(isUseAnimationBackground());
/*  40 */     copy.setUseAnimationLoop(isUseAnimationLoop());
/*  41 */     copy.setUseAnimationPalette(isUseAnimationPalette());
/*     */ 
/*  43 */     return copy;
/*     */   }
/*     */ 
/*     */   public RasterColor getAnimationBackground()
/*     */   {
/*  48 */     return RasterColor.fromColorRef(this.owner.getSaveFileOption().GlobalBackground);
/*     */   }
/*     */ 
/*     */   public void setAnimationBackground(RasterColor color) {
/*  52 */     this.owner.getSaveFileOption().GlobalBackground = color.getColorRef();
/*     */   }
/*     */ 
/*     */   public int getAnimationHeight() {
/*  56 */     return this.owner.getSaveFileOption().GlobalHeight;
/*     */   }
/*     */ 
/*     */   public void setAnimationHeight(int height) {
/*  60 */     this.owner.getSaveFileOption().GlobalHeight = height;
/*     */   }
/*     */ 
/*     */   public int getAnimationLoop() {
/*  64 */     return this.owner.getSaveFileOption().GlobalLoop;
/*     */   }
/*     */ 
/*     */   public void setAnimationLoop(int loop) {
/*  68 */     this.owner.getSaveFileOption().GlobalLoop = loop;
/*     */   }
/*     */ 
/*     */   public int getAnimationWidth() {
/*  72 */     return this.owner.getSaveFileOption().GlobalWidth;
/*     */   }
/*     */ 
/*     */   public void setAnimationWidth(int width) {
/*  76 */     this.owner.getSaveFileOption().GlobalWidth = width;
/*     */   }
/*     */ 
/*     */   public boolean isInterlaced() {
/*  80 */     return Tools.isFlagged(this.owner.getSaveFileOption().Flags, 16);
/*     */   }
/*     */ 
/*     */   public void setInterlaced(boolean interlace) {
/*  84 */     Tools.setFlag1(this.owner.getSaveFileOption().Flags, 16, interlace);
/*     */   }
/*     */ 
/*     */   public boolean isUseAnimationBackground() {
/*  88 */     return Tools.isFlagged(this.owner.getSaveFileOption().Flags, 4);
/*     */   }
/*     */ 
/*     */   public void setUseAnimationBackground(boolean useAnimationBackground) {
/*  92 */     Tools.setFlag1(this.owner.getSaveFileOption().Flags, 4, useAnimationBackground);
/*     */   }
/*     */ 
/*     */   public boolean isUseAnimationLoop() {
/*  96 */     return Tools.isFlagged(this.owner.getSaveFileOption().Flags, 32);
/*     */   }
/*     */ 
/*     */   public void setUseAnimationLoop(boolean useAnimationLoop) {
/* 100 */     Tools.setFlag1(this.owner.getSaveFileOption().Flags, 32, useAnimationLoop);
/*     */   }
/*     */ 
/*     */   public boolean isUseAnimationPalette() {
/* 104 */     return Tools.isFlagged(this.owner.getSaveFileOption().Flags, 8);
/*     */   }
/*     */ 
/*     */   public void setUseAnimationPalette(boolean useAnimationPalette) {
/* 108 */     Tools.setFlag1(this.owner.getSaveFileOption().Flags, 8, useAnimationPalette);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsGifSaveOptions
 * JD-Core Version:    0.6.2
 */