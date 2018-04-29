/*    */ package leadtools.sane.server;
/*    */ 
/*    */ public class ClientConnectionEvent
/*    */ {
/*    */   private String _id;
/*    */   private Object _userData;
/*    */ 
/*    */   public String getId()
/*    */   {
/* 10 */     return this._id;
/*    */   }
/*    */   void setId(String id) {
/* 13 */     this._id = id;
/*    */   }
/*    */ 
/*    */   public Object getUserData()
/*    */   {
/* 18 */     return this._userData;
/*    */   }
/*    */   void setUserData(Object userData) {
/* 21 */     this._userData = userData;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.ClientConnectionEvent
 * JD-Core Version:    0.6.2
 */