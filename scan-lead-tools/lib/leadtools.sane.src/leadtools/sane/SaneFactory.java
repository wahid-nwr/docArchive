/*    */ package leadtools.sane;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ 
/*    */ public class SaneFactory
/*    */ {
/*    */   public static SaneSession createSession(InetAddress address, int port)
/*    */   {
/* 34 */     return new NetworkSaneSession(address, port);
/*    */   }
/*    */ 
/*    */   public static SaneSession createSession()
/*    */   {
/* 66 */     return new NativeSaneSession();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneFactory
 * JD-Core Version:    0.6.2
 */