/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsXlsLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Xls.Load.MultiPageSheet", isMultiPageSheet());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/*  9 */     setMultiPageSheet(CodecsOptionsSerializer.readOption(dic, "Xls.Load.MultiPageSheet", isMultiPageSheet()));
/*    */   }
/*    */ 
/*    */   CodecsXlsLoadOptions(CodecsOptions owner)
/*    */   {
/* 15 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsXlsLoadOptions copy(CodecsOptions owner) {
/* 19 */     CodecsXlsLoadOptions copy = new CodecsXlsLoadOptions(owner);
/* 20 */     copy.setMultiPageSheet(isMultiPageSheet());
/* 21 */     return copy;
/*    */   }
/*    */ 
/*    */   public boolean isMultiPageSheet() {
/* 25 */     return Tools.isFlagged(this.owner.getThreadData().pThreadLoadSettings.XLSOptions.uFlags, 1);
/*    */   }
/*    */ 
/*    */   public void setMultiPageSheet(boolean multiPageSheet) {
/* 29 */     Tools.setFlag1(this.owner.getThreadData().pThreadLoadSettings.XLSOptions.uFlags, 1, multiPageSheet);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsXlsLoadOptions
 * JD-Core Version:    0.6.2
 */