/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ import leadtools.LeadSize;
/*    */ import leadtools.RasterImageFormat;
/*    */ 
/*    */ public class CodecsEcwLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  8 */     CodecsOptionsSerializer.writeOption(dic, "Ecw.Load.Resolution", getResolution());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 12 */     setResolution(CodecsOptionsSerializer.readOption(dic, "Ecw.Load.Resolution", getResolution()));
/*    */   }
/*    */ 
/*    */   CodecsEcwLoadOptions(CodecsOptions owner)
/*    */   {
/* 18 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsEcwLoadOptions copy(CodecsOptions owner) {
/* 22 */     CodecsEcwLoadOptions copy = new CodecsEcwLoadOptions(owner);
/* 23 */     copy.setResolution(getResolution());
/*    */ 
/* 25 */     return copy;
/*    */   }
/*    */ 
/*    */   public LeadSize getResolution() {
/* 29 */     return this.owner.getLoadResolution(RasterImageFormat.ECW);
/*    */   }
/*    */ 
/*    */   public void setResolution(LeadSize resolution) {
/* 33 */     this.owner.setLoadResolution(RasterImageFormat.ECW, resolution);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsEcwLoadOptions
 * JD-Core Version:    0.6.2
 */