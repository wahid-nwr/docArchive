/*    */ package leadtools.sane.server;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.net.ServerSocket;
/*    */ 
/*    */ public class SaneServer
/*    */ {
/*    */   private ServerSocket _serverSocket;
/*    */   private ISaneScanningService _saneScanningService;
/*    */ 
/*    */   public SaneServer(int portNumber, ISaneScanningService saneScanningService)
/*    */   {
/* 43 */     if (saneScanningService == null)
/* 44 */       throw new NullPointerException("saneScanningService could not be null");
/*    */     try
/*    */     {
/* 47 */       this._saneScanningService = saneScanningService;
/* 48 */       this._serverSocket = new ServerSocket(portNumber);
/*    */     }
/*    */     catch (Exception e) {
/* 51 */       stop();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void start() {
/*    */     try {
/* 57 */       while (!this._serverSocket.isClosed())
/* 58 */         new ThreadSocket(this._serverSocket.accept(), this._saneScanningService);
/*    */     }
/*    */     catch (Exception e) {
/* 61 */       stop();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void stop() {
/*    */     try {
/* 67 */       if ((this._serverSocket != null) && 
/* 68 */         (!this._serverSocket.isClosed())) {
/* 69 */         this._serverSocket.close();
/*    */       }
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 74 */       System.err.println(e.getMessage());
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.SaneServer
 * JD-Core Version:    0.6.2
 */