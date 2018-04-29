/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.LeadEvent;
/*    */ import leadtools.LeadSeekOrigin;
/*    */ 
/*    */ public class CodecsRedirectSeekEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private long _offset;
/*    */   private LeadSeekOrigin _origin;
/*    */ 
/*    */   public CodecsRedirectSeekEvent(Object source)
/*    */   {
/* 15 */     super(source);
/* 16 */     this._offset = 0L;
/* 17 */     this._origin = LeadSeekOrigin.BEGIN;
/*    */   }
/*    */ 
/*    */   public void init(long offset, LeadSeekOrigin origin) {
/* 21 */     this._offset = offset;
/* 22 */     this._origin = origin;
/*    */   }
/*    */ 
/*    */   public long getOffset()
/*    */   {
/* 27 */     return this._offset;
/*    */   }
/*    */ 
/*    */   public void setOffset(long value) {
/* 31 */     this._offset = value;
/*    */   }
/*    */ 
/*    */   public LeadSeekOrigin getOrigin()
/*    */   {
/* 36 */     return this._origin;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsRedirectSeekEvent
 * JD-Core Version:    0.6.2
 */