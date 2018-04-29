/*    */ package leadtools;
/*    */ 
/*    */ public class RasterImageChangedEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int _flags;
/*    */ 
/*    */   public RasterImageChangedEvent(Object source, int flags)
/*    */   {
/* 11 */     super(source);
/* 12 */     this._flags = flags;
/*    */   }
/*    */ 
/*    */   public final int getFlags()
/*    */   {
/* 17 */     return this._flags;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImageChangedEvent
 * JD-Core Version:    0.6.2
 */