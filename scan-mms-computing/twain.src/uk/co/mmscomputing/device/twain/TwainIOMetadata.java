/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ import uk.co.mmscomputing.device.scanner.ScannerDevice;
/*    */ import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
/*    */ 
/*    */ public class TwainIOMetadata extends ScannerIOMetadata
/*    */ {
/*  9 */   public static final String[] TWAIN_STATE = { "", "Pre-Session", "Source Manager Loaded", "Source Manager Open", "Source Open", "Source Enabled", "Transfer Ready", "Transferring Data" };
/*    */ 
/* 22 */   private TwainSource source = null;
/*    */ 
/* 29 */   private TwainTransfer.MemoryTransfer.Info memory = null;
/*    */ 
/*    */   public String getStateStr()
/*    */   {
/* 20 */     return TWAIN_STATE[getState()];
/*    */   }
/*    */ 
/*    */   void setSource(TwainSource paramTwainSource) {
/* 24 */     this.source = paramTwainSource; } 
/* 25 */   public TwainSource getSource() { return this.source; } 
/* 26 */   public ScannerDevice getDevice() { return this.source; }
/*    */ 
/*    */ 
/*    */   public void setMemory(TwainTransfer.MemoryTransfer.Info paramInfo)
/*    */   {
/* 31 */     this.memory = paramInfo; } 
/* 32 */   public TwainTransfer.MemoryTransfer.Info getMemory() { return this.memory; }
/*    */ 
/*    */   public boolean isFinished() {
/* 35 */     return (getState() == 3) && (getLastState() == 4);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainIOMetadata
 * JD-Core Version:    0.6.2
 */