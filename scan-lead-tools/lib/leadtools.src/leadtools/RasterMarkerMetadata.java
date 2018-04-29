/*    */ package leadtools;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class RasterMarkerMetadata extends RasterMetadata
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */   private int _id;
/*    */   private byte[] _data;
/*    */   public static final transient int SOS = 218;
/*    */   public static final transient int APP0 = 224;
/*    */   public static final transient int APP1 = 225;
/*    */   public static final transient int APP2 = 226;
/*    */   public static final transient int COM = 254;
/*    */   public static final transient int RST0 = 208;
/*    */   public static final transient int RST7 = 215;
/*    */   public static final transient int SOI = 216;
/*    */   public static final transient int EOI = 217;
/*    */ 
/*    */   public RasterMarkerMetadata()
/*    */   {
/* 25 */     init(0, null);
/*    */   }
/*    */ 
/*    */   public RasterMarkerMetadata(int id, byte[] data) {
/* 29 */     init(id, data);
/*    */   }
/*    */ 
/*    */   private void init(int id, byte[] data)
/*    */   {
/* 34 */     this._id = id;
/* 35 */     this._data = data;
/*    */   }
/*    */ 
/*    */   public RasterMarkerMetadata clone()
/*    */   {
/* 40 */     return new RasterMarkerMetadata(getId(), this._data);
/*    */   }
/*    */ 
/*    */   public int getId()
/*    */   {
/* 45 */     return this._id;
/*    */   }
/*    */ 
/*    */   public void setId(int value) {
/* 49 */     this._id = value;
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 54 */     return this._data;
/*    */   }
/*    */ 
/*    */   public void setData(byte[] value)
/*    */   {
/* 59 */     this._data = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterMarkerMetadata
 * JD-Core Version:    0.6.2
 */