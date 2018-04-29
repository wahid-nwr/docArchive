/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsDocLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Doc.Load.BitsPerPixel", getBitsPerPixel());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/*  9 */     setBitsPerPixel(CodecsOptionsSerializer.readOption(dic, "Doc.Load.BitsPerPixel", getBitsPerPixel()));
/*    */   }
/*    */ 
/*    */   CodecsDocLoadOptions(CodecsOptions owner)
/*    */   {
/* 15 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsDocLoadOptions copy(CodecsOptions owner) {
/* 19 */     CodecsDocLoadOptions copy = new CodecsDocLoadOptions(owner);
/* 20 */     copy.setBitsPerPixel(getBitsPerPixel());
/*    */ 
/* 22 */     return copy;
/*    */   }
/*    */ 
/*    */   public int getBitsPerPixel() {
/* 26 */     return this.owner.getThreadData().pThreadLoadSettings.DOCOptions.nBitsPerPixel;
/*    */   }
/*    */ 
/*    */   public void setBitsPerPixel(int bitsPerPixel) {
/* 30 */     this.owner.getThreadData().pThreadLoadSettings.DOCOptions.nBitsPerPixel = bitsPerPixel;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsDocLoadOptions
 * JD-Core Version:    0.6.2
 */