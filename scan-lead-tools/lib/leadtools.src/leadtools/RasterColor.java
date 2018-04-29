/*     */ package leadtools;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.text.DecimalFormat;
/*     */ import leadtools.internal.ColorParser;
/*     */ 
/*     */ public final class RasterColor
/*     */ {
/*     */   private byte _b;
/*     */   private byte _g;
/*     */   private byte _r;
/*     */   private byte _reserved;
/*     */   private byte _a;
/*     */   private static final int serialVersionUID = 0;
/*     */ 
/*     */   private int RGB(byte r, byte g, byte b)
/*     */   {
/*  20 */     return r & 0xFF | (g & 0xFF) << 8 | (b & 0xFF) << 16;
/*     */   }
/*     */ 
/*     */   private static byte L_GET_R_VALUE(int rgb)
/*     */   {
/*  25 */     return (byte)rgb;
/*     */   }
/*     */ 
/*     */   private static byte L_GET_G_VALUE(int rgb) {
/*  29 */     return (byte)((short)rgb >> 8);
/*     */   }
/*     */ 
/*     */   private static byte L_GET_B_VALUE(int rgb) {
/*  33 */     return (byte)(rgb >> 16);
/*     */   }
/*     */ 
/*     */   private static byte L_GET_A_VALUE(int rgb) {
/*  37 */     return (byte)(rgb >> 24);
/*     */   }
/*     */ 
/*     */   public static byte getAAValue(int v)
/*     */   {
/*  42 */     return L_GET_R_VALUE(v);
/*     */   }
/*     */ 
/*     */   public static byte getARValue(int v) {
/*  46 */     return L_GET_G_VALUE(v);
/*     */   }
/*     */ 
/*     */   public static byte getAGValue(int v) {
/*  50 */     return L_GET_B_VALUE(v);
/*     */   }
/*     */ 
/*     */   public static byte getABValue(int v) {
/*  54 */     return getAValue(v);
/*     */   }
/*     */ 
/*     */   private static byte getAValue(int rgb) {
/*  58 */     return L_GET_A_VALUE(rgb);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  64 */     StringBuilder sb = new StringBuilder();
/*  65 */     sb.append(String.format("#%02X", new Object[] { Byte.valueOf(this._a) }));
/*  66 */     sb.append(String.format("%02X", new Object[] { Byte.valueOf(this._r) }));
/*  67 */     sb.append(String.format("%02X", new Object[] { Byte.valueOf(this._g) }));
/*  68 */     sb.append(String.format("%02X", new Object[] { Byte.valueOf(this._b) }));
/*  69 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public String toHtml()
/*     */   {
/*  74 */     if (this._a == 255)
/*     */     {
/*  76 */       StringBuilder sb = new StringBuilder();
/*  77 */       sb.append(String.format("%02X", new Object[] { Byte.valueOf(this._r) }));
/*  78 */       sb.append(String.format("%02X", new Object[] { Byte.valueOf(this._g) }));
/*  79 */       sb.append(String.format("%02X", new Object[] { Byte.valueOf(this._b) }));
/*  80 */       return sb.toString();
/*     */     }
/*     */ 
/*  84 */     DecimalFormat df = new DecimalFormat("0.##");
/*  85 */     return String.format("rgba(%d,%d,%d,%s)", new Object[] { Byte.valueOf(this._r), Byte.valueOf(this._g), Byte.valueOf(this._b), df.format((int)(100.0D * this._a / 255.0D) / 100.0D) });
/*     */   }
/*     */ 
/*     */   public static RasterColor fromHtml(String value)
/*     */   {
/*  91 */     return ColorParser.parseColor(value);
/*     */   }
/*     */ 
/*     */   public static RasterColor fromKnownColor(long knownColor) {
/*  95 */     byte a = (byte)(int)((knownColor & 0xFF000000) >> 24);
/*  96 */     byte r = (byte)(int)((knownColor & 0xFF0000) >> 16);
/*  97 */     byte g = (byte)(int)((knownColor & 0xFF00) >> 8);
/*  98 */     byte b = (byte)(int)(knownColor & 0xFF);
/*     */ 
/* 100 */     return new RasterColor(a, r, g, b);
/*     */   }
/*     */ 
/*     */   public RasterColor()
/*     */   {
/*     */   }
/*     */ 
/*     */   public RasterColor clone()
/*     */   {
/* 109 */     RasterColor varCopy = new RasterColor();
/*     */ 
/* 111 */     varCopy._b = this._b;
/* 112 */     varCopy._g = this._g;
/* 113 */     varCopy._r = this._r;
/* 114 */     varCopy._reserved = this._reserved;
/* 115 */     varCopy._a = this._a;
/*     */ 
/* 117 */     return varCopy;
/*     */   }
/*     */ 
/*     */   public RasterColor(int red, int green, int blue)
/*     */   {
/* 122 */     this._r = ((byte)red);
/* 123 */     this._g = ((byte)green);
/* 124 */     this._b = ((byte)blue);
/* 125 */     this._reserved = 0;
/* 126 */     this._a = -1;
/*     */   }
/*     */ 
/*     */   public RasterColor(int alpha, int red, int green, int blue)
/*     */   {
/* 131 */     this._r = ((byte)red);
/* 132 */     this._g = ((byte)green);
/* 133 */     this._b = ((byte)blue);
/* 134 */     this._reserved = 0;
/* 135 */     this._a = ((byte)alpha);
/*     */   }
/*     */ 
/*     */   public static RasterColor fromArgb(int argb)
/*     */   {
/* 140 */     return new RasterColor(getAAValue(argb), getARValue(argb), getAGValue(argb), getABValue(argb));
/*     */   }
/*     */ 
/*     */   public int toRgb()
/*     */   {
/* 145 */     return RGB(this._r, this._g, this._b);
/*     */   }
/*     */ 
/*     */   public byte getA()
/*     */   {
/* 150 */     return this._a;
/*     */   }
/*     */ 
/*     */   public void setA(byte value) {
/* 154 */     this._a = value;
/*     */   }
/*     */ 
/*     */   public byte getR()
/*     */   {
/* 159 */     return this._r;
/*     */   }
/*     */ 
/*     */   public void setR(byte value) {
/* 163 */     this._r = value;
/*     */   }
/*     */ 
/*     */   public byte getG()
/*     */   {
/* 168 */     return this._g;
/*     */   }
/*     */ 
/*     */   public void setG(byte value) {
/* 172 */     this._g = value;
/*     */   }
/*     */ 
/*     */   public byte getB()
/*     */   {
/* 177 */     return this._b;
/*     */   }
/*     */ 
/*     */   public void setB(byte value) {
/* 181 */     this._b = value;
/*     */   }
/*     */ 
/*     */   public byte getReserved()
/*     */   {
/* 186 */     return this._reserved;
/*     */   }
/*     */ 
/*     */   public void setReserved(byte value) {
/* 190 */     this._reserved = value;
/*     */   }
/*     */ 
/*     */   public int getColorRef()
/*     */   {
/* 195 */     return RGB(this._r, this._g, this._b) | this._reserved << 24;
/*     */   }
/*     */ 
/*     */   public static RasterColor fromColorRef(int value)
/*     */   {
/* 200 */     RasterColor clr = new RasterColor(L_GET_R_VALUE(value), L_GET_G_VALUE(value), L_GET_B_VALUE(value));
/* 201 */     if ((value & 0x1000000) == 16777216)
/* 202 */       clr._reserved = 1;
/* 203 */     return clr;
/*     */   }
/*     */ 
/*     */   static void writeSerializeVersion(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 209 */     stream.writeInt(0);
/*     */   }
/*     */ 
/*     */   static int readSerializeVersion(ObjectInputStream stream) throws IOException {
/* 213 */     return stream.readInt();
/*     */   }
/*     */ 
/*     */   void writeObject(ObjectOutputStream stream) throws IOException
/*     */   {
/* 218 */     stream.writeByte(this._b);
/* 219 */     stream.writeByte(this._g);
/* 220 */     stream.writeByte(this._r);
/* 221 */     stream.writeByte(this._reserved);
/* 222 */     stream.writeByte(this._a);
/*     */   }
/*     */ 
/*     */   static RasterColor readObject(ObjectInputStream stream, int version)
/*     */     throws IOException
/*     */   {
/* 228 */     RasterColor clr = new RasterColor();
/* 229 */     clr._b = stream.readByte();
/* 230 */     clr._g = stream.readByte();
/* 231 */     clr._r = stream.readByte();
/* 232 */     clr._reserved = stream.readByte();
/* 233 */     clr._a = stream.readByte();
/*     */ 
/* 235 */     return clr;
/*     */   }
/*     */ 
/*     */   public RasterColor(String value)
/*     */   {
/*     */     RasterColor clr;
/*     */     RasterColor clr;
/* 241 */     if ((value != null) && (value.length() > 0))
/* 242 */       clr = ColorParser.parseColor(value);
/*     */     else {
/* 244 */       clr = new RasterColor(0, 0, 0);
/*     */     }
/*     */ 
/* 247 */     this._r = clr._r;
/* 248 */     this._g = clr._g;
/* 249 */     this._b = clr._b;
/* 250 */     this._a = clr._a;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterColor
 * JD-Core Version:    0.6.2
 */