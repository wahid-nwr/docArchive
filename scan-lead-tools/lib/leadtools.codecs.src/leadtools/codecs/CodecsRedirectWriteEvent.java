/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.LeadEvent;
/*    */ 
/*    */ public class CodecsRedirectWriteEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private byte[] _buffer;
/*    */   private int _count;
/*    */   private int _written;
/*    */ 
/*    */   public CodecsRedirectWriteEvent(Object source)
/*    */   {
/* 15 */     super(source);
/* 16 */     this._buffer = null;
/* 17 */     this._count = 0;
/* 18 */     this._written = 0;
/*    */   }
/*    */ 
/*    */   public void init(byte[] buffer, int count) {
/* 22 */     this._buffer = buffer;
/* 23 */     this._count = count;
/* 24 */     this._written = 0;
/*    */   }
/*    */ 
/*    */   public byte[] getBuffer()
/*    */   {
/* 29 */     return this._buffer;
/*    */   }
/*    */ 
/*    */   public int getCount() {
/* 33 */     return this._count;
/*    */   }
/*    */ 
/*    */   public int getWritten()
/*    */   {
/* 38 */     return this._written;
/*    */   }
/*    */ 
/*    */   public void setWritten(int value) {
/* 42 */     this._written = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRedirectWriteEvent
 * JD-Core Version:    0.6.2
 */