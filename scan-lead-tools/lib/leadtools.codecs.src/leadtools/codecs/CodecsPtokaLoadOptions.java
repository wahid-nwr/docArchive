/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ import leadtools.ArgumentOutOfRangeException;
/*    */ 
/*    */ public class CodecsPtokaLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  7 */     CodecsOptionsSerializer.writeOption(dic, "Ptoka.Load.Resolution", getResolution());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 11 */     setResolution(CodecsOptionsSerializer.readOption(dic, "Ptoka.Load.Resolution", getResolution()));
/*    */   }
/*    */ 
/*    */   public CodecsPtokaLoadOptions(CodecsOptions owner)
/*    */   {
/* 17 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsPtokaLoadOptions copy(CodecsOptions owner) {
/* 21 */     CodecsPtokaLoadOptions copy = new CodecsPtokaLoadOptions(owner);
/* 22 */     copy.setResolution(getResolution());
/*    */ 
/* 24 */     return copy;
/*    */   }
/*    */ 
/*    */   public int getResolution()
/*    */   {
/* 29 */     return this.owner.getThreadData().pThreadLoadSettings.PTKOptions.nPTKResolution;
/*    */   }
/*    */ 
/*    */   public void setResolution(int resolution)
/*    */   {
/* 34 */     if (resolution < 0) {
/* 35 */       throw new ArgumentOutOfRangeException("resolution", resolution, "Resolution cannot be less than 0");
/*    */     }
/* 37 */     this.owner.getThreadData().pThreadLoadSettings.PTKOptions.nPTKResolution = resolution;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPtokaLoadOptions
 * JD-Core Version:    0.6.2
 */