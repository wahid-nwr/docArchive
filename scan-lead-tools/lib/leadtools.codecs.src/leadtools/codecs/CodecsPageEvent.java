/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.ILeadStream;
/*    */ import leadtools.LeadEvent;
/*    */ import leadtools.RasterImage;
/*    */ 
/*    */ public class CodecsPageEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private RasterImage _image;
/*    */   private int _page;
/*    */   private int _pageCount;
/*    */   private ILeadStream _stream;
/*    */   private CodecsPageEventCommand _command;
/*    */   private CodecsPageEventState _state;
/*    */ 
/*    */   CodecsPageEvent(Object source)
/*    */   {
/* 19 */     super(source);
/*    */   }
/*    */ 
/*    */   final void init(RasterImage image, int page, int pageCount, ILeadStream stream, CodecsPageEventState state) {
/* 23 */     this._image = image;
/* 24 */     this._page = page;
/* 25 */     this._pageCount = pageCount;
/* 26 */     this._stream = stream;
/* 27 */     this._state = state;
/* 28 */     this._command = CodecsPageEventCommand.CONTINUE;
/*    */   }
/*    */ 
/*    */   public RasterImage getImage()
/*    */   {
/* 33 */     return this._image;
/*    */   }
/*    */ 
/*    */   public int getPage()
/*    */   {
/* 38 */     return this._page;
/*    */   }
/*    */ 
/*    */   public int getPageCount()
/*    */   {
/* 43 */     return this._pageCount;
/*    */   }
/*    */ 
/*    */   public ILeadStream getStream()
/*    */   {
/* 48 */     return this._stream;
/*    */   }
/*    */ 
/*    */   public CodecsPageEventState getState()
/*    */   {
/* 53 */     return this._state;
/*    */   }
/*    */ 
/*    */   public CodecsPageEventCommand getCommand()
/*    */   {
/* 58 */     return this._command;
/*    */   }
/*    */ 
/*    */   public void setCommand(CodecsPageEventCommand value) {
/* 62 */     this._command = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPageEvent
 * JD-Core Version:    0.6.2
 */