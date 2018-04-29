/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsVffLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Vff.Load.View", getView().getValue());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/*  9 */     setView(CodecsVffView.forValue(CodecsOptionsSerializer.readOption(dic, "Vff.Load.View", getView().getValue())));
/*    */   }
/*    */ 
/*    */   CodecsVffLoadOptions(CodecsOptions owner)
/*    */   {
/* 15 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsVffLoadOptions copy(CodecsOptions owner) {
/* 19 */     CodecsVffLoadOptions copy = new CodecsVffLoadOptions(owner);
/* 20 */     copy.setView(getView());
/* 21 */     return copy;
/*    */   }
/*    */ 
/*    */   public CodecsVffView getView() {
/* 25 */     return this.owner.getThreadData().pThreadLoadSettings.VFFOptions.View;
/*    */   }
/*    */ 
/*    */   public void setView(CodecsVffView view) {
/* 29 */     this.owner.getThreadData().pThreadLoadSettings.VFFOptions.View = view;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsVffLoadOptions
 * JD-Core Version:    0.6.2
 */