/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ import leadtools.LeadSize;
/*    */ import leadtools.RasterImageFormat;
/*    */ 
/*    */ public class CodecsJpeg2000LoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  8 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Load.Jp2Resolution", getJp2Resolution());
/*  9 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Load.J2kResolution", getJ2kResolution());
/* 10 */     CodecsOptionsSerializer.writeOption(dic, "Jpeg2000.Load.CmwResolution", getCmwResolution());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 14 */     setJp2Resolution(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Load.Jp2Resolution", getJp2Resolution()));
/* 15 */     setJ2kResolution(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Load.J2kResolution", getJ2kResolution()));
/* 16 */     setCmwResolution(CodecsOptionsSerializer.readOption(dic, "Jpeg2000.Load.CmwResolution", getCmwResolution()));
/*    */   }
/*    */ 
/*    */   CodecsJpeg2000LoadOptions(CodecsOptions owner)
/*    */   {
/* 22 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsJpeg2000LoadOptions copy(CodecsOptions owner) {
/* 26 */     CodecsJpeg2000LoadOptions copy = new CodecsJpeg2000LoadOptions(owner);
/* 27 */     copy.setCmwResolution(getCmwResolution());
/* 28 */     copy.setJ2kResolution(getJ2kResolution());
/* 29 */     copy.setJp2Resolution(getJp2Resolution());
/*    */ 
/* 31 */     return copy;
/*    */   }
/*    */ 
/*    */   public LeadSize getJp2Resolution() {
/* 35 */     return this.owner.getLoadResolution(RasterImageFormat.JP2);
/*    */   }
/*    */ 
/*    */   public void setJp2Resolution(LeadSize value) {
/* 39 */     this.owner.setLoadResolution(RasterImageFormat.JP2, value);
/*    */   }
/*    */ 
/*    */   public LeadSize getJ2kResolution() {
/* 43 */     return this.owner.getLoadResolution(RasterImageFormat.J2K);
/*    */   }
/*    */ 
/*    */   public void setJ2kResolution(LeadSize value) {
/* 47 */     this.owner.setLoadResolution(RasterImageFormat.J2K, value);
/*    */   }
/*    */ 
/*    */   public LeadSize getCmwResolution() {
/* 51 */     return this.owner.getLoadResolution(RasterImageFormat.CMW);
/*    */   }
/*    */ 
/*    */   public void setCmwResolution(LeadSize value) {
/* 55 */     this.owner.setLoadResolution(RasterImageFormat.CMW, value);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJpeg2000LoadOptions
 * JD-Core Version:    0.6.2
 */