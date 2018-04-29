/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsAbcSaveOptions
/*    */ {
/*    */   private CodecsOptions owner;
/* 16 */   private CodecsAbcQualityFactor qualityFactor = CodecsAbcQualityFactor.LOSSLESS;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  7 */     CodecsOptionsSerializer.writeOption(dic, "Abc.Save.QualityFactor", getQualityFactor().getValue());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 11 */     setQuailtyFactor(CodecsAbcQualityFactor.forValue(CodecsOptionsSerializer.readOption(dic, "Abc.Save.QualityFactor", getQualityFactor().getValue())));
/*    */   }
/*    */ 
/*    */   CodecsAbcSaveOptions(CodecsOptions owner)
/*    */   {
/* 19 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsAbcSaveOptions copy(CodecsOptions owner) {
/* 23 */     CodecsAbcSaveOptions copy = new CodecsAbcSaveOptions(owner);
/* 24 */     copy.setQuailtyFactor(this.qualityFactor);
/*    */ 
/* 26 */     return copy;
/*    */   }
/*    */ 
/*    */   public CodecsAbcQualityFactor getQualityFactor() {
/* 30 */     return this.qualityFactor;
/*    */   }
/*    */ 
/*    */   public void setQuailtyFactor(CodecsAbcQualityFactor qualityFactor) {
/* 34 */     this.qualityFactor = qualityFactor;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsAbcSaveOptions
 * JD-Core Version:    0.6.2
 */