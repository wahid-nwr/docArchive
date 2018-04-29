/*    */ package leadtools;
/*    */ 
/*    */ public class RasterCollectionEvent<T> extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private T _item;
/*    */   private RasterCollection<T> _collection;
/*    */ 
/*    */   public void setItem(T item)
/*    */   {
/* 15 */     this._item = item;
/*    */   }
/*    */ 
/*    */   public RasterCollectionEvent(Object source)
/*    */   {
/* 20 */     super(source);
/*    */   }
/*    */ 
/*    */   public RasterCollectionEvent(Object source, T item)
/*    */   {
/* 25 */     super(source);
/* 26 */     this._item = item;
/*    */   }
/*    */ 
/*    */   public T getItem()
/*    */   {
/* 31 */     return this._item;
/*    */   }
/*    */ 
/*    */   public RasterCollection<T> getCollection()
/*    */   {
/* 36 */     return this._collection;
/*    */   }
/*    */ 
/*    */   public void setCollection(RasterCollection<T> item) {
/* 40 */     this._collection = item;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterCollectionEvent
 * JD-Core Version:    0.6.2
 */