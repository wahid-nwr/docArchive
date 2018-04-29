/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsAbcLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  7 */     CodecsOptionsSerializer.writeOption(dic, "Abc.Load.FastLoad", isFastLoad());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/* 11 */     setFastLoad(CodecsOptionsSerializer.readOption(dic, "Abc.Load.FastLoad", isFastLoad()));
/*    */   }
/*    */ 
/*    */   CodecsAbcLoadOptions(CodecsOptions owner)
/*    */   {
/* 17 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsAbcLoadOptions copy(CodecsOptions owner)
/*    */   {
/* 22 */     CodecsAbcLoadOptions copy = new CodecsAbcLoadOptions(owner);
/* 23 */     copy.setFastLoad(isFastLoad());
/*    */ 
/* 25 */     return copy;
/*    */   }
/*    */ 
/*    */   public boolean isFastLoad()
/*    */   {
/* 30 */     return (this.owner.getLoadFileOption().Flags & LOADFILEOPTION.ELO_FLAGS.ELO_FAST.getValue()) != 0;
/*    */   }
/*    */ 
/*    */   public void setFastLoad(boolean fastLoad)
/*    */   {
/* 35 */     if (fastLoad)
/* 36 */       this.owner.getLoadFileOption().Flags |= LOADFILEOPTION.ELO_FLAGS.ELO_FAST.getValue();
/* 37 */     else if (isFastLoad())
/* 38 */       this.owner.getLoadFileOption().Flags ^= LOADFILEOPTION.ELO_FLAGS.ELO_FAST.getValue();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsAbcLoadOptions
 * JD-Core Version:    0.6.2
 */