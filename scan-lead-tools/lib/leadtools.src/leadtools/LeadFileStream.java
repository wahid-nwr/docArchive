/*    */ package leadtools;
/*    */ 
/*    */ public final class LeadFileStream
/*    */   implements ILeadStream
/*    */ {
/*    */   private String _fileName;
/*    */ 
/*    */   public LeadFileStream(String fileName)
/*    */   {
/*  5 */     if (fileName == null) {
/*  6 */       throw new ArgumentNullException("fileName");
/*    */     }
/*  8 */     this._fileName = fileName;
/*    */   }
/*    */ 
/*    */   public String getFileName()
/*    */   {
/* 14 */     return this._fileName;
/*    */   }
/*    */ 
/*    */   public boolean canSeek() {
/* 18 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean canRead() {
/* 22 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean canWrite() {
/* 26 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean isStarted() {
/* 30 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean start() {
/* 34 */     return true;
/*    */   }
/*    */ 
/*    */   public void stop(boolean resetPosition) {
/*    */   }
/*    */ 
/*    */   public boolean openFile(String fileName, LeadStreamMode mode, LeadStreamAccess access, LeadStreamShare share) {
/* 41 */     return false;
/*    */   }
/*    */ 
/*    */   public int read(byte[] buffer, int count) {
/* 45 */     return 0;
/*    */   }
/*    */ 
/*    */   public int write(byte[] buffer, int count) {
/* 49 */     return 0;
/*    */   }
/*    */ 
/*    */   public long seek(LeadSeekOrigin origin, long offset) {
/* 53 */     return -1L;
/*    */   }
/*    */ 
/*    */   public void closeFile()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadFileStream
 * JD-Core Version:    0.6.2
 */