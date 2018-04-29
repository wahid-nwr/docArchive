/*    */ package leadtools.sane;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.net.InetAddress;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.Socket;
/*    */ import leadtools.RasterException;
/*    */ 
/*    */ class SaneUtils
/*    */ {
/*    */   public static Socket createSaneSocket(InetAddress address, int port)
/*    */   {
/*    */     try
/*    */     {
/*    */       Socket a;
/*    */       void tmp9_8 = 
/* 193 */         (a = new Socket());
/*    */ 
/* 37 */       tmp9_8.setTcpNoDelay(true); tmp9_8
/* 100 */         .connect(new InetSocketAddress(address, port));
/*    */ 
/* 154 */       return a;
/*    */     }
/*    */     catch (Exception a)
/*    */     {
/* 63 */       throw new RasterException(a.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public static InputStream getInputStream(Socket socket)
/*    */   {
/*    */     try
/*    */     {
/* 82 */       return socket.getInputStream();
/*    */     }
/*    */     catch (Exception a)
/*    */     {
/* 186 */       throw new RasterException(a.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public static OutputStream getOutputStream(Socket socket)
/*    */   {
/*    */     try
/*    */     {
/* 60 */       return socket.getOutputStream();
/*    */     }
/*    */     catch (Exception a)
/*    */     {
/* 189 */       throw new RasterException(a.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public static Socket createSocket(InetAddress address, int port)
/*    */   {
/*    */     try
/*    */     {
/* 54 */       return new Socket(address, port);
/*    */     }
/*    */     catch (Exception a)
/*    */     {
/* 110 */       throw new RasterException(a.getMessage());
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneUtils
 * JD-Core Version:    0.6.2
 */