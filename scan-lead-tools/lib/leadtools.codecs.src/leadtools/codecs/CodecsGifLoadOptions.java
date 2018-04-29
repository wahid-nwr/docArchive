/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CodecsGifLoadOptions
/*    */ {
/*    */   private CodecsOptions owner;
/*    */ 
/*    */   void writeXml(Map<String, String> dic)
/*    */   {
/*  5 */     CodecsOptionsSerializer.writeOption(dic, "Gif.Load.AnimationLoop", getAnimationLoop());
/*    */   }
/*    */ 
/*    */   void readXml(Map<String, String> dic) {
/*  9 */     setAnimationLoop(CodecsOptionsSerializer.readOption(dic, "Gif.Load.AnimationLoop", getAnimationLoop()));
/*    */   }
/*    */ 
/*    */   CodecsGifLoadOptions(CodecsOptions owner)
/*    */   {
/* 15 */     this.owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsGifLoadOptions copy(CodecsOptions owner) {
/* 19 */     CodecsGifLoadOptions copy = new CodecsGifLoadOptions(owner);
/* 20 */     copy.setAnimationLoop(getAnimationLoop());
/*    */ 
/* 22 */     return copy;
/*    */   }
/*    */ 
/*    */   public int getAnimationLoop() {
/* 26 */     return this.owner.getLoadFileOption().GlobalLoop;
/*    */   }
/*    */ 
/*    */   public void setAnimationLoop(int loop) {
/* 30 */     this.owner.getLoadFileOption().GlobalLoop = loop;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsGifLoadOptions
 * JD-Core Version:    0.6.2
 */