/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsWmfLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Wmf.Load.XResolution", getXResolution());
/*  6 */     CodecsOptionsSerializer.writeOption(dic, "Wmf.Load.YResolution", getYResolution());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 10 */     setXResolution(CodecsOptionsSerializer.readOption(dic, "Wmf.Load.XResolution", getXResolution()));
/* 11 */     setYResolution(CodecsOptionsSerializer.readOption(dic, "Wmf.Load.YResolution", getYResolution()));
/*    */   }
/*    */ 
/*    */   CodecsWmfLoadOptions(CodecsOptions owner)
/*    */   {
/* 17 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsWmfLoadOptions copy(CodecsOptions owner) {
/* 21 */     CodecsWmfLoadOptions copy = new CodecsWmfLoadOptions(owner);
/* 22 */     copy.setXResolution(getXResolution());
/* 23 */     copy.setYResolution(getYResolution());
/* 24 */     return copy;
/*    */   }
/*    */ 
/*    */   public int getXResolution() {
/* 28 */     return this.owner.getThreadData().pThreadLoadSettings.WMFXResolution;
/*    */   }
/*    */ 
/*    */   public void setXResolution(int xResolution) {
/* 32 */     this.owner.getThreadData().pThreadLoadSettings.WMFXResolution = xResolution;
/*    */   }
/*    */ 
/*    */   public int getYResolution() {
/* 36 */     return this.owner.getThreadData().pThreadLoadSettings.WMFYResolution;
/*    */   }
/*    */ 
/*    */   public void setYResolution(int yResolution) {
/* 40 */     this.owner.getThreadData().pThreadLoadSettings.WMFYResolution = yResolution;
/*    */   }
/*    */ 
/*    */   public int getResolution() {
/* 44 */     return Math.max(getXResolution(), getYResolution());
/*    */   }
/*    */ 
/*    */   public void setResolution(int resolution) {
/* 48 */     setXResolution(resolution);
/* 49 */     setYResolution(resolution);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsWmfLoadOptions
 * JD-Core Version:    0.6.2
 */