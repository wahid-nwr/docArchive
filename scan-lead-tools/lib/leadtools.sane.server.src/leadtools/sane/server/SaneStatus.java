/*    */ package leadtools.sane.server;
/*    */ 
/*    */ public class SaneStatus
/*    */ {
/*    */   private boolean _isScanning;
/*    */   private int _pageCount;
/*    */   private int _currentPage;
/*    */   private SaneError _error;
/*    */   private String _selectedSource;
/*    */ 
/*    */   public boolean isScanning()
/*    */   {
/*  9 */     return this._isScanning;
/*    */   }
/*    */   void setScanning(boolean isScanning) {
/* 12 */     this._isScanning = isScanning;
/*    */   }
/*    */ 
/*    */   public int getPageCount()
/*    */   {
/* 17 */     return this._pageCount;
/*    */   }
/*    */   void setPageCount(int pageCount) {
/* 20 */     this._pageCount = pageCount;
/*    */   }
/*    */ 
/*    */   public int getCurrentPage()
/*    */   {
/* 25 */     return this._currentPage;
/*    */   }
/*    */   void setCurrentPage(int currentPage) {
/* 28 */     this._currentPage = currentPage;
/*    */   }
/*    */ 
/*    */   public SaneError getError()
/*    */   {
/* 33 */     return this._error;
/*    */   }
/*    */   void setError(SaneError error) {
/* 36 */     this._error = error;
/*    */   }
/*    */ 
/*    */   public String getSelectedSource()
/*    */   {
/* 41 */     return this._selectedSource;
/*    */   }
/*    */   void setSelectedSource(String selectedSource) {
/* 44 */     this._selectedSource = selectedSource;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.SaneStatus
 * JD-Core Version:    0.6.2
 */