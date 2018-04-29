/*    */ package leadtools.sane.server;
/*    */ 
/*    */ public class ImageProcessingEvent
/*    */ {
/*    */   private String _id;
/*    */   private boolean _isPreview;
/*    */   private int _firstPageNumber;
/*    */   private int _lastPageNumber;
/*    */   private String _commandName;
/*    */   private String _arguments;
/*    */   private int _previewWidth;
/*    */   private int _previewHeight;
/*    */   private Object _userData;
/*    */   private boolean _isCanceled;
/*    */ 
/*    */   public String getId()
/*    */   {
/* 11 */     return this._id;
/*    */   }
/*    */   void setId(String id) {
/* 14 */     this._id = id;
/*    */   }
/*    */ 
/*    */   public boolean isPreview()
/*    */   {
/* 19 */     return this._isPreview;
/*    */   }
/*    */   void setIsPreview(boolean isPreview) {
/* 22 */     this._isPreview = isPreview;
/*    */   }
/*    */ 
/*    */   public int getFirstPageNumber()
/*    */   {
/* 27 */     return this._firstPageNumber;
/*    */   }
/*    */   void setFirstPageNumber(int firstPageNumber) {
/* 30 */     this._firstPageNumber = firstPageNumber;
/*    */   }
/*    */ 
/*    */   public int getLastPageNumber()
/*    */   {
/* 35 */     return this._lastPageNumber;
/*    */   }
/*    */   void setLastPageNumber(int lastPageNumber) {
/* 38 */     this._lastPageNumber = lastPageNumber;
/*    */   }
/*    */ 
/*    */   public String getCommandName()
/*    */   {
/* 43 */     return this._commandName;
/*    */   }
/*    */   void setCommandName(String commandName) {
/* 46 */     this._commandName = commandName;
/*    */   }
/*    */ 
/*    */   public String getArguments()
/*    */   {
/* 51 */     return this._arguments;
/*    */   }
/*    */   void setArguments(String arguments) {
/* 54 */     this._arguments = arguments;
/*    */   }
/*    */ 
/*    */   public int getPreviewWidth()
/*    */   {
/* 60 */     return this._previewWidth;
/*    */   }
/*    */   void setPreviewWidth(int previewWidth) {
/* 63 */     this._previewWidth = previewWidth;
/*    */   }
/*    */ 
/*    */   public int getPreviewHeight()
/*    */   {
/* 68 */     return this._previewHeight;
/*    */   }
/*    */   void setPreviewHeight(int previewHeight) {
/* 71 */     this._previewHeight = previewHeight;
/*    */   }
/*    */ 
/*    */   public Object getUserData()
/*    */   {
/* 76 */     return this._userData;
/*    */   }
/*    */   void setUserData(Object userData) {
/* 79 */     this._userData = userData;
/*    */   }
/*    */ 
/*    */   public boolean isCanceled()
/*    */   {
/* 84 */     return this._isCanceled;
/*    */   }
/*    */   public void setCanceled(boolean isCanceled) {
/* 87 */     this._isCanceled = isCanceled;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.ImageProcessingEvent
 * JD-Core Version:    0.6.2
 */