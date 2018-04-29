/*    */ package leadtools;
/*    */ 
/*    */ public class RasterCommandProgressEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int _percent;
/*    */   private boolean _cancel;
/*    */ 
/*    */   public RasterCommandProgressEvent(Object source, int percent)
/*    */   {
/* 12 */     super(source);
/* 13 */     this._percent = percent;
/* 14 */     this._cancel = false;
/*    */   }
/*    */ 
/*    */   public int getPercent()
/*    */   {
/* 19 */     return this._percent;
/*    */   }
/*    */ 
/*    */   public boolean getCancel()
/*    */   {
/* 24 */     return this._cancel;
/*    */   }
/*    */ 
/*    */   public void setCancel(boolean value) {
/* 28 */     this._cancel = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterCommandProgressEvent
 * JD-Core Version:    0.6.2
 */