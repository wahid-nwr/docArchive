/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsEpsLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Eps.Load.ForceThumbnail", isForceThumbnail());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/*  9 */     setForceThumbnail(CodecsOptionsSerializer.readOption(dic, "Eps.Load.ForceThumbnail", isForceThumbnail()));
/*    */   }
/*    */ 
/*    */   CodecsEpsLoadOptions(CodecsOptions owner)
/*    */   {
/* 16 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsEpsLoadOptions copy(CodecsOptions owner) {
/* 20 */     CodecsEpsLoadOptions copy = new CodecsEpsLoadOptions(owner);
/* 21 */     copy.setForceThumbnail(isForceThumbnail());
/*    */ 
/* 23 */     return copy;
/*    */   }
/*    */ 
/*    */   public void setForceThumbnail(boolean forceThumbnail) {
/* 27 */     Tools.setFlag1(this.owner.getLoadFileOption().Flags, 131072, forceThumbnail);
/*    */   }
/*    */ 
/*    */   public boolean isForceThumbnail() {
/* 31 */     return Tools.isFlagged(this.owner.getLoadFileOption().Flags, 131072);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsEpsLoadOptions
 * JD-Core Version:    0.6.2
 */