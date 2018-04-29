/*    */ package leadtools.internal;
/*    */ 
/*    */ public class AutoResetEvent
/*    */ {
/*  4 */   private final Object _obj = new Object();
/*    */ 
/*    */   public void waitOne() throws Exception
/*    */   {
/*  8 */     synchronized (this._obj)
/*    */     {
/* 12 */       this._obj.wait();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void set()
/*    */   {
/* 18 */     synchronized (this._obj)
/*    */     {
/* 20 */       this._obj.notify();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.AutoResetEvent
 * JD-Core Version:    0.6.2
 */