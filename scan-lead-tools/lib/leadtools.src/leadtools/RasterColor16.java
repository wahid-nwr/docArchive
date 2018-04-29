/*     */ package leadtools;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ 
/*     */ public final class RasterColor16
/*     */ {
/*     */   private short _b;
/*     */   private short _g;
/*     */   private short _r;
/*     */   private short _reserved;
/*     */   public static final short MAXIMUM_COMPONENT = -1;
/*     */   public static final short MINIMUM_COMPONENT = 0;
/*     */   private static final int _serialVersionUID = 0;
/*     */ 
/*     */   public RasterColor16(int red, int green, int blue)
/*     */   {
/*  22 */     this._r = ((short)red);
/*  23 */     this._g = ((short)green);
/*  24 */     this._b = ((short)blue);
/*  25 */     this._reserved = 0;
/*     */   }
/*     */ 
/*     */   public RasterColor16(int red, int green, int blue, int reserved) {
/*  29 */     this._r = ((short)red);
/*  30 */     this._g = ((short)green);
/*  31 */     this._b = ((short)blue);
/*  32 */     this._reserved = ((short)reserved);
/*     */   }
/*     */ 
/*     */   public short getR()
/*     */   {
/*  37 */     return this._r;
/*     */   }
/*     */ 
/*     */   public void setR(short value) {
/*  41 */     this._r = value;
/*     */   }
/*     */ 
/*     */   public short getG()
/*     */   {
/*  46 */     return this._g;
/*     */   }
/*     */ 
/*     */   public void setG(short value) {
/*  50 */     this._g = value;
/*     */   }
/*     */ 
/*     */   public short getB()
/*     */   {
/*  55 */     return this._b;
/*     */   }
/*     */ 
/*     */   public void setB(short value) {
/*  59 */     this._b = value;
/*     */   }
/*     */ 
/*     */   public short getReserved()
/*     */   {
/*  64 */     return this._reserved;
/*     */   }
/*     */ 
/*     */   public void setReserved(short value) {
/*  68 */     this._reserved = value;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  74 */     return String.format("Color [R=%1$s, G=%2$s, B=%3$s]", new Object[] { Short.valueOf(this._r), Short.valueOf(this._g), Short.valueOf(this._b) });
/*     */   }
/*     */ 
/*     */   public RasterColor16 clone()
/*     */   {
/*  79 */     return new RasterColor16(this._b, this._g, this._r, this._reserved);
/*     */   }
/*     */ 
/*     */   static void writeSerializeVersion(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/*  85 */     stream.writeInt(0);
/*     */   }
/*     */ 
/*     */   static int readSerializeVersion(ObjectInputStream stream) throws IOException {
/*  89 */     return stream.readInt();
/*     */   }
/*     */ 
/*     */   void writeObject(ObjectOutputStream stream) throws IOException
/*     */   {
/*  94 */     stream.writeShort(this._b);
/*  95 */     stream.writeShort(this._g);
/*  96 */     stream.writeShort(this._r);
/*  97 */     stream.writeShort(this._reserved);
/*     */   }
/*     */ 
/*     */   static RasterColor16 readObject(ObjectInputStream stream, int version)
/*     */     throws IOException
/*     */   {
/* 103 */     short _b = stream.readShort();
/* 104 */     short _g = stream.readShort();
/* 105 */     short _r = stream.readShort();
/* 106 */     short _reserved = stream.readShort();
/* 107 */     return new RasterColor16(_b, _g, _r, _reserved);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterColor16
 * JD-Core Version:    0.6.2
 */