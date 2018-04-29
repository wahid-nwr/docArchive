/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ import leadtools.LeadSize;
/*    */ import leadtools.RasterImageFormat;
/*    */ 
/*    */ public class CodecsJbigLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  8 */     CodecsOptionsSerializer.writeOption(dic, "Jbig.Load.Resolution", getResolution());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 12 */     setResolution(CodecsOptionsSerializer.readOption(dic, "Jbig.Load.Resolution", getResolution()));
/*    */   }
/*    */ 
/*    */   CodecsJbigLoadOptions(CodecsOptions owner)
/*    */   {
/* 18 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsJbigLoadOptions copy(CodecsOptions owner) {
/* 22 */     CodecsJbigLoadOptions copy = new CodecsJbigLoadOptions(owner);
/* 23 */     copy.setResolution(getResolution());
/*    */ 
/* 25 */     return copy;
/*    */   }
/*    */ 
/*    */   public LeadSize getResolution() {
/* 29 */     return this.owner.getLoadResolution(RasterImageFormat.JBIG);
/*    */   }
/*    */ 
/*    */   public void setResolution(LeadSize value) {
/* 33 */     this.owner.setLoadResolution(RasterImageFormat.JBIG, value);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJbigLoadOptions
 * JD-Core Version:    0.6.2
 */