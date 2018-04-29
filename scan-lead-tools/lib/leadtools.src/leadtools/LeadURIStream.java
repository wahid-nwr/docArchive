/*    */ package leadtools;
/*    */ 
/*    */ import java.net.URI;
/*    */ 
/*    */ public final class LeadURIStream
/*    */   implements ILeadStream
/*    */ {
/*    */   private URI _uri;
/*    */ 
/*    */   public LeadURIStream(URI uri)
/*    */   {
/*  7 */     if (uri == null) {
/*  8 */       throw new ArgumentNullException("uri");
/*    */     }
/* 10 */     this._uri = uri;
/*    */   }
/*    */ 
/*    */   public URI getURI()
/*    */   {
/* 15 */     return this._uri;
/*    */   }
/*    */ 
/*    */   public boolean canSeek() {
/* 19 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean canRead() {
/* 23 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean canWrite() {
/* 27 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean isStarted() {
/* 31 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean start() {
/* 35 */     return true;
/*    */   }
/*    */ 
/*    */   public void stop(boolean resetPosition) {
/*    */   }
/*    */ 
/*    */   public boolean openFile(String fileName, LeadStreamMode mode, LeadStreamAccess access, LeadStreamShare share) {
/* 42 */     return false;
/*    */   }
/*    */ 
/*    */   public int read(byte[] buffer, int count) {
/* 46 */     return 0;
/*    */   }
/*    */ 
/*    */   public int write(byte[] buffer, int count) {
/* 50 */     return 0;
/*    */   }
/*    */ 
/*    */   public long seek(LeadSeekOrigin origin, long offset) {
/* 54 */     return -1L;
/*    */   }
/*    */ 
/*    */   public void closeFile() {
/*    */   }
/*    */ 
/*    */   public String getFileName() {
/* 61 */     return "URI";
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadURIStream
 * JD-Core Version:    0.6.2
 */