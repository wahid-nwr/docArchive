/*    */ package leadtools;
/*    */ 
/*    */ public class RasterImagePagesChangedEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private RasterImagePagesChangedAction _action;
/*    */   private int _startIndex;
/*    */   private int _count;
/*    */ 
/*    */   public RasterImagePagesChangedEvent(Object source, RasterImagePagesChangedAction action, int startIndex, int count)
/*    */   {
/* 13 */     super(source);
/* 14 */     this._action = action;
/* 15 */     this._startIndex = startIndex;
/* 16 */     this._count = count;
/*    */   }
/*    */ 
/*    */   public RasterImagePagesChangedAction getAction()
/*    */   {
/* 21 */     return this._action;
/*    */   }
/*    */ 
/*    */   public int getStartIndex()
/*    */   {
/* 26 */     return this._startIndex;
/*    */   }
/*    */ 
/*    */   public int getCount()
/*    */   {
/* 31 */     return this._count;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImagePagesChangedEvent
 * JD-Core Version:    0.6.2
 */