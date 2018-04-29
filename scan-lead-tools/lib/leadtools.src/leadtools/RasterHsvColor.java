/*    */ package leadtools;
/*    */ 
/*    */ public final class RasterHsvColor
/*    */ {
/*    */   private byte _h;
/*    */   private byte _s;
/*    */   private byte _v;
/*    */   private byte _reserved;
/*    */ 
/*    */   public RasterHsvColor(int value)
/*    */   {
/* 13 */     fromUnmanaged(value);
/*    */   }
/*    */ 
/*    */   public void fromUnmanaged(int value) {
/* 17 */     this._h = ((byte)(value & 0xFF));
/* 18 */     this._s = ((byte)(value >> 8 & 0xFF));
/* 19 */     this._v = ((byte)(value >> 16 & 0xFF));
/* 20 */     this._reserved = ((byte)(value >> 24 & 0xFF));
/*    */   }
/*    */ 
/*    */   public int toUnmanaged() {
/* 24 */     int ret = this._h & 0xFF | (this._s & 0xFF) << 8 | (this._v & 0xFF) << 16 | (this._reserved & 0xFF) << 24;
/* 25 */     return ret;
/*    */   }
/*    */ 
/*    */   public RasterHsvColor getEmpty()
/*    */   {
/* 30 */     return new RasterHsvColor(0);
/*    */   }
/*    */ 
/*    */   public static RasterHsvColor fromRasterColor(RasterColor color)
/*    */   {
/* 35 */     return new RasterHsvColor(color.clone());
/*    */   }
/*    */ 
/*    */   public RasterHsvColor(RasterColor color) {
/* 39 */     int hsv = ltkrn.RGBtoHSV(color.getColorRef());
/* 40 */     fromUnmanaged(hsv);
/*    */   }
/*    */ 
/*    */   public RasterHsvColor(int h, int s, int v, int reserved) {
/* 44 */     this._h = ((byte)(h & 0xFF));
/* 45 */     this._s = ((byte)(s & 0xFF));
/* 46 */     this._v = ((byte)(v & 0xFF));
/* 47 */     this._reserved = ((byte)(reserved & 0xFF));
/*    */   }
/*    */ 
/*    */   public byte getH()
/*    */   {
/* 52 */     return this._h;
/*    */   }
/*    */ 
/*    */   public void setH(byte value) {
/* 56 */     this._h = value;
/*    */   }
/*    */ 
/*    */   public byte getS()
/*    */   {
/* 61 */     return this._s;
/*    */   }
/*    */ 
/*    */   public void setS(byte value) {
/* 65 */     this._s = value;
/*    */   }
/*    */ 
/*    */   public byte getV()
/*    */   {
/* 70 */     return this._v;
/*    */   }
/*    */ 
/*    */   public void setV(byte value) {
/* 74 */     this._v = value;
/*    */   }
/*    */ 
/*    */   public byte getReserved()
/*    */   {
/* 79 */     return this._reserved;
/*    */   }
/*    */ 
/*    */   public void setReserved(byte value) {
/* 83 */     this._reserved = value;
/*    */   }
/*    */ 
/*    */   public RasterColor toRasterColor()
/*    */   {
/* 88 */     int clr = ltkrn.HSVtoRGB(toUnmanaged());
/* 89 */     return RasterColor.fromArgb(clr);
/*    */   }
/*    */ 
/*    */   public RasterHsvColor clone()
/*    */   {
/* 94 */     return new RasterHsvColor(this._h, this._s, this._v, this._reserved);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterHsvColor
 * JD-Core Version:    0.6.2
 */