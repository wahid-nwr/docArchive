/*    */ package leadtools;
/*    */ 
/*    */ public class RasterImageProgressEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int _percent;
/*    */   private boolean _cancel;
/*    */ 
/*    */   public RasterImageProgressEvent(Object source, int percentComplete)
/*    */   {
/*  9 */     super(source);
/* 10 */     this._percent = percentComplete;
/* 11 */     this._cancel = false;
/*    */   }
/*    */ 
/*    */   public int getPercentComplete() {
/* 15 */     return this._percent;
/*    */   }
/*    */ 
/*    */   public boolean getCancel() {
/* 19 */     return this._cancel;
/*    */   }
/*    */   public void setCancel(boolean value) {
/* 22 */     this._cancel = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImageProgressEvent
 * JD-Core Version:    0.6.2
 */