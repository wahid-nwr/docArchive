/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsJbig2LoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Jbig2.Load.LoadOldFiles", isLoadOldFiles());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/*  9 */     setLoadOldFiles(CodecsOptionsSerializer.readOption(dic, "jbig2.Load.LoadOldFiles", isLoadOldFiles()));
/*    */   }
/*    */ 
/*    */   CodecsJbig2LoadOptions(CodecsOptions owner)
/*    */   {
/* 15 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsJbig2LoadOptions copy(CodecsOptions owner) {
/* 19 */     CodecsJbig2LoadOptions copy = new CodecsJbig2LoadOptions(owner);
/* 20 */     copy.setLoadOldFiles(isLoadOldFiles());
/*    */ 
/* 22 */     return copy;
/*    */   }
/*    */ 
/*    */   public boolean isLoadOldFiles() {
/* 26 */     return Tools.isFlagged(this.owner.getLoadFileOption().Flags, 536870912);
/*    */   }
/*    */ 
/*    */   public void setLoadOldFiles(boolean loadOldFiles) {
/* 30 */     Tools.setFlag1(this.owner.getLoadFileOption().Flags, 536870912, loadOldFiles);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsJbig2LoadOptions
 * JD-Core Version:    0.6.2
 */