/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsRawSaveOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Raw.Save.ReverseBits", isReverseBits());
/*  6 */     CodecsOptionsSerializer.writeOption(dic, "Raw.Save.Pad4", isPad4());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 10 */     setReverseBits(CodecsOptionsSerializer.readOption(dic, "Raw.Save.ReverseBits", isReverseBits()));
/* 11 */     setPad4(CodecsOptionsSerializer.readOption(dic, "Raw.Save.Pad4", isPad4()));
/*    */   }
/*    */ 
/*    */   CodecsRawSaveOptions(CodecsOptions owner)
/*    */   {
/* 17 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsRawSaveOptions copy(CodecsOptions owner) {
/* 21 */     CodecsRawSaveOptions copy = new CodecsRawSaveOptions(owner);
/* 22 */     copy.setPad4(isPad4());
/* 23 */     copy.setReverseBits(isReverseBits());
/*    */ 
/* 25 */     return copy;
/*    */   }
/*    */ 
/*    */   public boolean isPad4() {
/* 29 */     return Tools.isFlagged(this.owner.getSaveFileOption().Flags, 4194304);
/*    */   }
/*    */ 
/*    */   public void setPad4(boolean pad4) {
/* 33 */     Tools.setFlag1(this.owner.getSaveFileOption().Flags, 4194304, pad4);
/*    */   }
/*    */ 
/*    */   public boolean isReverseBits() {
/* 37 */     return Tools.isFlagged(this.owner.getSaveFileOption().Flags, 4194304);
/*    */   }
/*    */ 
/*    */   public void setReverseBits(boolean reverseBits) {
/* 41 */     Tools.setFlag1(this.owner.getSaveFileOption().Flags, 4194304, reverseBits);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRawSaveOptions
 * JD-Core Version:    0.6.2
 */