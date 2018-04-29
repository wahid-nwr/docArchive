/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsPstLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Pst.Load.MessageNumber", getMessageNumber());
/*    */ 
/*  7 */     CodecsOptionsSerializer.writeOption(dic, "Pst.Load.PlainText", isPlainText());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 11 */     setMessageNumber(CodecsOptionsSerializer.readOption(dic, "Pst.Load.MessageNumber", getMessageNumber()));
/*    */ 
/* 13 */     setPlainText(CodecsOptionsSerializer.readOption(dic, "Pst.Load.PlainText", isPlainText()));
/*    */   }
/*    */ 
/*    */   CodecsPstLoadOptions(CodecsOptions owner)
/*    */   {
/* 36 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsPstLoadOptions copy(CodecsOptions owner) {
/* 40 */     CodecsPstLoadOptions copy = new CodecsPstLoadOptions(owner);
/* 41 */     copy.setMessageNumber(getMessageNumber());
/* 42 */     copy.setPlainText(isPlainText());
/*    */ 
/* 44 */     return copy;
/*    */   }
/*    */ 
/*    */   public int getMessageNumber() {
/* 48 */     return (int)this.owner.getThreadData().pThreadLoadSettings.PSTOptions.uMessageNumber;
/*    */   }
/*    */ 
/*    */   public void setMessageNumber(int messageNumber) {
/* 52 */     this.owner.getThreadData().pThreadLoadSettings.PSTOptions.uMessageNumber = messageNumber;
/*    */   }
/*    */ 
/*    */   public boolean isPlainText() {
/* 56 */     int flags = this.owner.getThreadData().pThreadLoadSettings.PSTOptions.uFlags;
/* 57 */     return (flags & PST_FLAGS.PST_FLAGS_MESSAGE_PLAINTEXT.getValue()) != 0;
/*    */   }
/*    */ 
/*    */   public void setPlainText(boolean plainText) {
/* 61 */     if (plainText)
/* 62 */       this.owner.getThreadData().pThreadLoadSettings.PSTOptions.uFlags |= PST_FLAGS.PST_FLAGS_MESSAGE_PLAINTEXT.getValue();
/* 63 */     else if (isPlainText())
/* 64 */       this.owner.getThreadData().pThreadLoadSettings.PSTOptions.uFlags ^= PST_FLAGS.PST_FLAGS_MESSAGE_PLAINTEXT.getValue();
/*    */   }
/*    */ 
/*    */   private static enum PST_FLAGS
/*    */   {
/* 19 */     PST_FLAGS_MESSAGE_PLAINTEXT(1);
/*    */ 
/*    */     private int value;
/*    */ 
/*    */     private PST_FLAGS(int value) {
/* 24 */       this.value = value;
/*    */     }
/*    */ 
/*    */     public int getValue() {
/* 28 */       return this.value;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPstLoadOptions
 * JD-Core Version:    0.6.2
 */