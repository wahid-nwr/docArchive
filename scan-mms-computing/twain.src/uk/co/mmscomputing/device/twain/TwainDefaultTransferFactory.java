/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ public class TwainDefaultTransferFactory
/*    */   implements TwainITransferFactory
/*    */ {
/*    */   public TwainITransfer createFileTransfer(TwainSource paramTwainSource)
/*    */   {
/* 12 */     return new TwainTransfer.FileTransfer(paramTwainSource);
/*    */   }
/*    */ 
/*    */   public TwainITransfer createMemoryTransfer(TwainSource paramTwainSource) {
/* 16 */     return new TwainTransfer.MemoryTransfer(paramTwainSource);
/*    */   }
/*    */ 
/*    */   public TwainITransfer createNativeTransfer(TwainSource paramTwainSource) {
/* 20 */     return new TwainTransfer.NativeTransfer(paramTwainSource);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainDefaultTransferFactory
 * JD-Core Version:    0.6.2
 */