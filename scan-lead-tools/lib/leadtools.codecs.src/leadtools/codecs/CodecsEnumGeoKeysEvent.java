/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.LeadEvent;
/*    */ import leadtools.RasterNativeBuffer;
/*    */ import leadtools.RasterTagMetadata;
/*    */ import leadtools.RasterTagMetadataDataType;
/*    */ 
/*    */ public class CodecsEnumGeoKeysEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int _id;
/*    */   private int _type;
/*    */   private int _count;
/*    */   private boolean _cancel;
/*    */   private RasterNativeBuffer _buffer;
/*    */ 
/*    */   public CodecsEnumGeoKeysEvent(Object source)
/*    */   {
/* 20 */     super(source);
/*    */   }
/*    */ 
/*    */   public void init(int id, int type, int count, byte[] data, int dataLength) {
/* 24 */     this._id = id;
/* 25 */     this._type = type;
/* 26 */     this._count = count;
/* 27 */     this._cancel = false;
/*    */ 
/* 29 */     this._buffer = new RasterNativeBuffer(data, dataLength);
/*    */   }
/*    */ 
/*    */   public int getId()
/*    */   {
/* 34 */     return this._id;
/*    */   }
/*    */ 
/*    */   public RasterTagMetadataDataType getMetadataType()
/*    */   {
/* 39 */     return RasterTagMetadataDataType.forValue(this._type);
/*    */   }
/*    */ 
/*    */   public int getCount()
/*    */   {
/* 44 */     return this._count;
/*    */   }
/*    */ 
/*    */   public RasterNativeBuffer getBuffer()
/*    */   {
/* 49 */     return this._buffer;
/*    */   }
/*    */ 
/*    */   public RasterTagMetadata toRasterTagMetadata()
/*    */   {
/* 54 */     RasterTagMetadata tag = new RasterTagMetadata();
/* 55 */     tag.setId(this._id);
/* 56 */     tag.setDataType(RasterTagMetadataDataType.forValue(this._type));
/* 57 */     byte[] data = new byte[this._count * RasterTagMetadata.getDataTypeSize(tag.getDataType())];
/* 58 */     this._buffer.getData(0, data, 0, data.length);
/* 59 */     tag.setData(data);
/* 60 */     return tag;
/*    */   }
/*    */ 
/*    */   public boolean getCancel()
/*    */   {
/* 65 */     return this._cancel;
/*    */   }
/*    */ 
/*    */   public void setCancel(boolean value) {
/* 69 */     this._cancel = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsEnumGeoKeysEvent
 * JD-Core Version:    0.6.2
 */