/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.LeadEvent;
/*    */ 
/*    */ public class CodecsRedirectReadEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private byte[] _buffer;
/*    */   private int _count;
/*    */   private int _read;
/*    */ 
/*    */   public CodecsRedirectReadEvent(Object source)
/*    */   {
/* 16 */     super(source);
/* 17 */     this._buffer = null;
/* 18 */     this._count = 0;
/* 19 */     this._read = 0;
/*    */   }
/*    */ 
/*    */   public void init(byte[] buffer, int count) {
/* 23 */     this._buffer = buffer;
/* 24 */     this._count = count;
/* 25 */     this._read = 0;
/*    */   }
/*    */ 
/*    */   public byte[] getBuffer()
/*    */   {
/* 30 */     return this._buffer;
/*    */   }
/*    */ 
/*    */   public int getCount() {
/* 34 */     return this._count;
/*    */   }
/*    */ 
/*    */   public int getRead()
/*    */   {
/* 39 */     return this._read;
/*    */   }
/*    */ 
/*    */   public void setRead(int value) {
/* 43 */     this._read = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRedirectReadEvent
 * JD-Core Version:    0.6.2
 */