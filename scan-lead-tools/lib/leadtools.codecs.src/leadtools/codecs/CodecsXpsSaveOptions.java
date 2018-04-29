/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsXpsSaveOptions
/*    */ {
/* 16 */   private int jpegQualityFactor = 2;
/* 17 */   private int pngQualityFactor = 3;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Xps.Save.PngQualityFactor", getPngQualityFactor());
/*  6 */     CodecsOptionsSerializer.writeOption(dic, "Xps.Save.JpegQualityFactor", getJpegQualityFactor());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 10 */     setPngQualityFactor(CodecsOptionsSerializer.readOption(dic, "Xps.Save.PngQualityFactor", getPngQualityFactor()));
/* 11 */     setJpegQualityFactor(CodecsOptionsSerializer.readOption(dic, "Xps.Save.JpegQualityFactor", getJpegQualityFactor()));
/*    */   }
/*    */ 
/*    */   CodecsXpsSaveOptions(CodecsOptions owner)
/*    */   {
/*    */   }
/*    */ 
/*    */   CodecsXpsSaveOptions copy(CodecsOptions owner)
/*    */   {
/* 24 */     CodecsXpsSaveOptions copy = new CodecsXpsSaveOptions(owner);
/* 25 */     copy.setJpegQualityFactor(getJpegQualityFactor());
/* 26 */     copy.setPngQualityFactor(getPngQualityFactor());
/* 27 */     return copy;
/*    */   }
/*    */ 
/*    */   public int getJpegQualityFactor() {
/* 31 */     return this.jpegQualityFactor;
/*    */   }
/*    */ 
/*    */   public void setJpegQualityFactor(int qualityFactor) {
/* 35 */     this.jpegQualityFactor = qualityFactor;
/*    */   }
/*    */ 
/*    */   public int getPngQualityFactor() {
/* 39 */     return this.pngQualityFactor;
/*    */   }
/*    */ 
/*    */   public void setPngQualityFactor(int qualityFactor) {
/* 43 */     this.pngQualityFactor = qualityFactor;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsXpsSaveOptions
 * JD-Core Version:    0.6.2
 */