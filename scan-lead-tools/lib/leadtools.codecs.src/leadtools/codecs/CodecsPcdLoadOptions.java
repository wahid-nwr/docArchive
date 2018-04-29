/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ import leadtools.LeadSize;
/*    */ import leadtools.RasterImageFormat;
/*    */ 
/*    */ public class CodecsPcdLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  8 */     CodecsOptionsSerializer.writeOption(dic, "Pcd.Load.Resolution", getResolution());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 12 */     setResolution(CodecsOptionsSerializer.readOption(dic, "Pcd.Load.Resolution", getResolution()));
/*    */   }
/*    */ 
/*    */   public CodecsPcdLoadOptions(CodecsOptions owner)
/*    */   {
/* 18 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsPcdLoadOptions copy(CodecsOptions owner) {
/* 22 */     CodecsPcdLoadOptions copy = new CodecsPcdLoadOptions(owner);
/* 23 */     copy.setResolution(getResolution());
/*    */ 
/* 25 */     return copy;
/*    */   }
/*    */ 
/*    */   public LeadSize getResolution() {
/* 29 */     return this.owner.getLoadResolution(RasterImageFormat.PCD);
/*    */   }
/*    */ 
/*    */   public void setResolution(LeadSize resolution) {
/* 33 */     this.owner.setLoadResolution(RasterImageFormat.PCD, resolution);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPcdLoadOptions
 * JD-Core Version:    0.6.2
 */