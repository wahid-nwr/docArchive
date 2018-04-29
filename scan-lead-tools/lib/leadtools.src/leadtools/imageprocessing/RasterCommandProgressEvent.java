/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.LeadEvent;
/*    */ 
/*    */ public class RasterCommandProgressEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int _percent;
/*    */   private boolean _cancel;
/*    */ 
/*    */   public RasterCommandProgressEvent(Object source, int percent)
/*    */   {
/* 13 */     super(source);
/* 14 */     this._percent = percent;
/* 15 */     this._cancel = false;
/*    */   }
/*    */ 
/*    */   public int getPercent()
/*    */   {
/* 20 */     return this._percent;
/*    */   }
/*    */ 
/*    */   public boolean getCancel()
/*    */   {
/* 25 */     return this._cancel;
/*    */   }
/*    */ 
/*    */   public void setCancel(boolean value) {
/* 29 */     this._cancel = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.RasterCommandProgressEvent
 * JD-Core Version:    0.6.2
 */