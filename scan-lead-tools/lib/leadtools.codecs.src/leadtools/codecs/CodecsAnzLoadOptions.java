/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsAnzLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Anz.Load.View", getView().getValue());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/*  9 */     setView(CodecsAnzView.forValue(CodecsOptionsSerializer.readOption(dic, "Anz.Load.View", getView().getValue())));
/*    */   }
/*    */ 
/*    */   CodecsAnzLoadOptions(CodecsOptions owner)
/*    */   {
/* 15 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsAnzLoadOptions copy(CodecsOptions owner) {
/* 19 */     CodecsAnzLoadOptions copy = new CodecsAnzLoadOptions(owner);
/* 20 */     copy.setView(getView());
/* 21 */     return copy;
/*    */   }
/*    */ 
/*    */   private void setView(CodecsAnzView view)
/*    */   {
/* 26 */     this.owner.getThreadData().pThreadLoadSettings.ANZOptions.View = view;
/*    */   }
/*    */ 
/*    */   public CodecsAnzView getView()
/*    */   {
/* 31 */     return this.owner.getThreadData().pThreadLoadSettings.ANZOptions.View;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsAnzLoadOptions
 * JD-Core Version:    0.6.2
 */