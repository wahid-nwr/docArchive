/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ import leadtools.ArgumentOutOfRangeException;
/*    */ 
/*    */ public class CodecsPngSaveOptions
/*    */ {
/*    */   private CodecsOptions _owner;
/* 17 */   private int qualityFactor = 3;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  8 */     CodecsOptionsSerializer.writeOption(dic, "Png.Save.QualityFactor", getQualityFactor());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 12 */     setQualityFactor(CodecsOptionsSerializer.readOption(dic, "Png.Save.QualityFactor", getQualityFactor()));
/*    */   }
/*    */ 
/*    */   CodecsPngSaveOptions(CodecsOptions owner)
/*    */   {
/* 21 */     this._owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsPngSaveOptions copy(CodecsOptions owner) {
/* 25 */     CodecsPngSaveOptions copy = new CodecsPngSaveOptions(owner);
/* 26 */     copy.setQualityFactor(getQualityFactor());
/*    */ 
/* 28 */     return copy;
/*    */   }
/*    */ 
/*    */   public int getQualityFactor()
/*    */   {
/* 33 */     return this.qualityFactor;
/*    */   }
/*    */ 
/*    */   public void setQualityFactor(int value)
/*    */   {
/* 38 */     if ((value < 0) || (value > 9)) {
/* 39 */       throw new ArgumentOutOfRangeException("PngQualityFactor", value, "PNG quality factor must a value than 0 and less than 9");
/*    */     }
/* 41 */     this.qualityFactor = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPngSaveOptions
 * JD-Core Version:    0.6.2
 */