/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ import leadtools.RasterColor;
/*    */ 
/*    */ public class CodecsRtfLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  7 */     CodecsOptionsSerializer.writeOption(dic, "Rtf.Load.BackColor", getBackColor());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 11 */     setBackColor(CodecsOptionsSerializer.readOption(dic, "Rtf.Load.BackColor", getBackColor()));
/*    */   }
/*    */ 
/*    */   CodecsRtfLoadOptions(CodecsOptions owner)
/*    */   {
/* 17 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsRtfLoadOptions copy(CodecsOptions owner) {
/* 21 */     CodecsRtfLoadOptions copy = new CodecsRtfLoadOptions(owner);
/* 22 */     copy.setBackColor(getBackColor());
/* 23 */     return copy;
/*    */   }
/*    */ 
/*    */   public RasterColor getBackColor() {
/* 27 */     return RasterColor.fromColorRef(this.owner.getThreadData().pThreadLoadSettings.RTFOptions.crBackColor);
/*    */   }
/*    */ 
/*    */   public void setBackColor(RasterColor backColor) {
/* 31 */     this.owner.getThreadData().pThreadLoadSettings.RTFOptions.crBackColor = backColor.getColorRef();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRtfLoadOptions
 * JD-Core Version:    0.6.2
 */