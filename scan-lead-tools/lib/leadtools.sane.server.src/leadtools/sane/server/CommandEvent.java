/*    */ package leadtools.sane.server;
/*    */ 
/*    */ public class CommandEvent
/*    */ {
/*    */   private String _id;
/*    */   private String _commandName;
/*    */   private String _arguments;
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
/*    */   public String getCommandName()
/*    */   {
/* 18 */     return this._commandName;
/*    */   }
/*    */   void setCommandName(String commandName) {
/* 21 */     this._commandName = commandName;
/*    */   }
/*    */ 
/*    */   public String getArguments()
/*    */   {
/* 26 */     return this._arguments;
/*    */   }
/*    */   void setArguments(String arguments) {
/* 29 */     this._arguments = arguments;
/*    */   }
/*    */ 
/*    */   public Object getUserData()
/*    */   {
/* 34 */     return this._userData;
/*    */   }
/*    */   void setUserData(Object userData) {
/* 37 */     this._userData = userData;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.CommandEvent
 * JD-Core Version:    0.6.2
 */