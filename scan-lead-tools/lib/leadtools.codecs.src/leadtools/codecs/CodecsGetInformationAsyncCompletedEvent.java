/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.ILeadStream;
/*    */ 
/*    */ public class CodecsGetInformationAsyncCompletedEvent extends CodecsAsyncCompletedEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private CodecsImageInfo _info;
/*    */ 
/*    */   private CodecsGetInformationAsyncCompletedEvent(Object source)
/*    */   {
/* 10 */     super(source, null, null, false, null);
/*    */   }
/*    */ 
/*    */   public CodecsGetInformationAsyncCompletedEvent(Object source, CodecsImageInfo info, ILeadStream stream, RuntimeException error, boolean cancelled, Object userState) {
/* 14 */     super(source, stream, error, cancelled, userState);
/* 15 */     this._info = info;
/*    */   }
/*    */ 
/*    */   public CodecsImageInfo getInfo()
/*    */   {
/* 21 */     return this._info;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsGetInformationAsyncCompletedEvent
 * JD-Core Version:    0.6.2
 */