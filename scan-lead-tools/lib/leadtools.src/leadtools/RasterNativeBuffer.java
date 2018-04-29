/*    */ package leadtools;
/*    */ 
/*    */ public final class RasterNativeBuffer
/*    */ {
/*    */   private byte[] _data;
/*    */   private long _length;
/*    */ 
/*    */   public RasterNativeBuffer(byte[] data, long length)
/*    */   {
/* 10 */     this._data = data;
/* 11 */     this._length = length;
/*    */   }
/*    */ 
/*    */   public static RasterNativeBuffer getEmpty()
/*    */   {
/* 16 */     return new RasterNativeBuffer(null, 0L);
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 21 */     return this._data;
/*    */   }
/*    */ 
/*    */   public void setData(byte[] value) {
/* 25 */     this._data = value;
/*    */   }
/*    */ 
/*    */   public long getLength()
/*    */   {
/* 30 */     return this._length;
/*    */   }
/*    */ 
/*    */   public void setLength(long value) {
/* 34 */     this._length = value;
/*    */   }
/*    */ 
/*    */   public void getData(int offset, byte[] buffer, int bufferOffset, int bytes)
/*    */   {
/* 39 */     System.arraycopy(this._data, offset, buffer, bufferOffset, bytes);
/*    */   }
/*    */ 
/*    */   public void setData(int offset, byte[] buffer, int bufferOffset, int bytes) {
/* 43 */     System.arraycopy(buffer, bufferOffset, this._data, offset, bytes);
/*    */   }
/*    */ 
/*    */   public RasterNativeBuffer clone()
/*    */   {
/* 48 */     return new RasterNativeBuffer((byte[])this._data.clone(), this._length);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterNativeBuffer
 * JD-Core Version:    0.6.2
 */