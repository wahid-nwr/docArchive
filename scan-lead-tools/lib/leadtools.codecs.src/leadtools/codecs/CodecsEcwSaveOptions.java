/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsEcwSaveOptions
/*    */ {
/* 13 */   private int qualityFactor = 1;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Ecw.Save.QualityFactor", getQualityFactor());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/*  9 */     setQualityFactor(CodecsOptionsSerializer.readOption(dic, "Ecw.Save.QualityFactor", getQualityFactor()));
/*    */   }
/*    */ 
/*    */   CodecsEcwSaveOptions(CodecsOptions owner)
/*    */   {
/*    */   }
/*    */ 
/*    */   CodecsEcwSaveOptions copy(CodecsOptions owner)
/*    */   {
/* 20 */     CodecsEcwSaveOptions copy = new CodecsEcwSaveOptions(owner);
/* 21 */     copy.setQualityFactor(getQualityFactor());
/*    */ 
/* 23 */     return copy;
/*    */   }
/*    */ 
/*    */   public int getQualityFactor()
/*    */   {
/* 28 */     return this.qualityFactor;
/*    */   }
/*    */ 
/*    */   public void setQualityFactor(int qualityFactor) {
/* 32 */     this.qualityFactor = qualityFactor;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsEcwSaveOptions
 * JD-Core Version:    0.6.2
 */