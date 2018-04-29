/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.LeadEvent;
/*    */ import leadtools.RasterTagMetadataDataType;
/*    */ 
/*    */ public class CodecsEnumTagsEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int _id;
/*    */   private int _type;
/*    */   private int _count;
/*    */   private boolean _cancel;
/*    */ 
/*    */   public CodecsEnumTagsEvent(Object source)
/*    */   {
/* 17 */     super(source);
/*    */   }
/*    */ 
/*    */   public void init(int id, int type, int count) {
/* 21 */     this._id = id;
/* 22 */     this._type = type;
/* 23 */     this._count = count;
/* 24 */     this._cancel = false;
/*    */   }
/*    */ 
/*    */   public int getId()
/*    */   {
/* 29 */     return this._id;
/*    */   }
/*    */ 
/*    */   public RasterTagMetadataDataType getMetadataType()
/*    */   {
/* 34 */     return RasterTagMetadataDataType.forValue(this._type);
/*    */   }
/*    */ 
/*    */   public int getCount()
/*    */   {
/* 39 */     return this._count;
/*    */   }
/*    */ 
/*    */   public boolean getCancel()
/*    */   {
/* 44 */     return this._cancel;
/*    */   }
/*    */ 
/*    */   public void setCancel(boolean value) {
/* 48 */     this._cancel = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsEnumTagsEvent
 * JD-Core Version:    0.6.2
 */