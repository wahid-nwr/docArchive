/*     */ package leadtools;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class RasterTagMetadata extends RasterMetadata
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   private int _id;
/*     */   private RasterTagMetadataDataType _dataType;
/*     */   private byte[] _data;
/*     */   public static final transient int COPYRIGHT = 33432;
/*     */   public static final transient int GENERAL_EXIF = 34665;
/*     */   public static final transient int EXIF_GPS = 34853;
/*     */   public static final transient int ANNOTATION_TIFF = 32932;
/*     */ 
/*     */   private void init(int id, RasterTagMetadataDataType dataType, byte[] data)
/*     */   {
/*  21 */     this._id = id;
/*  22 */     this._dataType = dataType;
/*  23 */     this._data = data;
/*     */   }
/*     */ 
/*     */   private void checkType(RasterTagMetadataDataType[] dataType)
/*     */   {
/*  28 */     for (RasterTagMetadataDataType i : dataType)
/*     */     {
/*  30 */       if (getDataType() == i) {
/*  31 */         return;
/*     */       }
/*     */     }
/*  34 */     throw new IllegalArgumentException("Invalid data type");
/*     */   }
/*     */ 
/*     */   private void checkType(RasterTagMetadataDataType dataType)
/*     */   {
/*  39 */     if (getDataType() != dataType)
/*  40 */       throw new IllegalArgumentException("Invalid data type");
/*     */   }
/*     */ 
/*     */   public RasterTagMetadata()
/*     */   {
/*  45 */     init(32768, RasterTagMetadataDataType.BYTE, new byte[] { 0 });
/*     */   }
/*     */ 
/*     */   public RasterTagMetadata(int id, RasterTagMetadataDataType dataType, byte[] data) {
/*  49 */     init(id, dataType, data);
/*     */   }
/*     */ 
/*     */   public RasterTagMetadata clone()
/*     */   {
/*  54 */     return new RasterTagMetadata(getId(), getDataType(), this._data);
/*     */   }
/*     */ 
/*     */   public int getId()
/*     */   {
/*  59 */     return this._id;
/*     */   }
/*     */ 
/*     */   public void setId(int value) {
/*  63 */     this._id = value;
/*     */   }
/*     */ 
/*     */   public RasterTagMetadataDataType getDataType()
/*     */   {
/*  68 */     return this._dataType;
/*     */   }
/*     */ 
/*     */   public void setDataType(RasterTagMetadataDataType value) {
/*  72 */     this._dataType = value;
/*     */   }
/*     */ 
/*     */   public byte[] getData()
/*     */   {
/*  77 */     return this._data;
/*     */   }
/*     */ 
/*     */   public void setData(byte[] value)
/*     */   {
/*  82 */     this._data = value;
/*     */   }
/*     */ 
/*     */   public int getCount()
/*     */   {
/*  87 */     if (this._data != null) {
/*  88 */       return this._data.length / getDataTypeSize(getDataType());
/*     */     }
/*  90 */     return 0;
/*     */   }
/*     */ 
/*     */   public static int getDataTypeSize(RasterTagMetadataDataType dataType)
/*     */   {
/*  95 */     switch (1.$SwitchMap$leadtools$RasterTagMetadataDataType[dataType.ordinal()])
/*     */     {
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/* 101 */       return 1;
/*     */     case 5:
/*     */     case 6:
/* 105 */       return 2;
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/* 110 */       return 4;
/*     */     case 10:
/*     */     case 11:
/*     */     case 12:
/* 115 */       return 8;
/*     */     }
/*     */ 
/* 118 */     return 0;
/*     */   }
/*     */ 
/*     */   public byte[] toByte()
/*     */   {
/* 124 */     checkType(new RasterTagMetadataDataType[] { RasterTagMetadataDataType.BYTE, RasterTagMetadataDataType.SBYTE });
/* 125 */     return this._data;
/*     */   }
/*     */ 
/*     */   public void fromByte(byte[] value) {
/* 129 */     checkType(RasterTagMetadataDataType.BYTE);
/* 130 */     this._data = value;
/*     */   }
/*     */ 
/*     */   public byte[] toSByte()
/*     */   {
/* 135 */     checkType(RasterTagMetadataDataType.SBYTE);
/*     */ 
/* 137 */     if (this._data != null)
/*     */     {
/* 139 */       int size = getDataTypeSize(getDataType());
/* 140 */       byte[] ret = new byte[this._data.length / size];
/* 141 */       for (int i = 0; i < ret.length; i++)
/* 142 */         ret[i] = this._data[i];
/* 143 */       return ret;
/*     */     }
/*     */ 
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */   public void fromSByte(byte[] value)
/*     */   {
/* 151 */     checkType(RasterTagMetadataDataType.SBYTE);
/*     */ 
/* 153 */     if (value != null)
/*     */     {
/* 155 */       this._data = new byte[value.length];
/* 156 */       for (int i = 0; i < value.length; i++)
/* 157 */         this._data[i] = value[i];
/*     */     }
/*     */     else {
/* 160 */       this._data = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toAscii() {
/* 165 */     checkType(RasterTagMetadataDataType.ASCII);
/*     */ 
/* 167 */     if ((this._data != null) && (this._data.length > 0))
/*     */     {
/* 169 */       String ret = new String(this._data);
/* 170 */       int nullIndex = ret.indexOf(0);
/* 171 */       if (nullIndex != -1) {
/* 172 */         ret = ret.substring(0, nullIndex) + ret.substring(nullIndex + 1);
/*     */       }
/* 174 */       return ret;
/*     */     }
/*     */ 
/* 177 */     return "";
/*     */   }
/*     */ 
/*     */   public void fromAscii(String value) {
/* 181 */     checkType(RasterTagMetadataDataType.ASCII);
/*     */ 
/* 183 */     if (value != null)
/*     */     {
/* 186 */       byte[] bytes = value.getBytes();
/* 187 */       this._data = new byte[value.length() + 1];
/* 188 */       System.arraycopy(bytes, 0, this._data, 0, value.length());
/*     */     }
/*     */     else {
/* 191 */       this._data = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   static byte LOBYTE(int w) {
/* 196 */     return (byte)(w & 0xFF);
/*     */   }
/*     */ 
/*     */   private static byte HIBYTE(int w)
/*     */   {
/* 201 */     return (byte)(w >> 8 & 0xFF);
/*     */   }
/*     */ 
/*     */   static short MAKEWORD(byte a, byte b) {
/* 205 */     return (short)((short)a & 0xFF | ((short)b & 0xFF) << 8);
/*     */   }
/*     */ 
/*     */   static int MAKELONG(byte a, byte b)
/*     */   {
/* 210 */     return a & 0xFFFF | (b & 0xFFFF) << 16;
/*     */   }
/*     */ 
/*     */   public void fromSingle(short[] value)
/*     */   {
/* 215 */     checkType(RasterTagMetadataDataType.UINT_16);
/*     */ 
/* 217 */     shortsToBytes(value);
/*     */   }
/*     */ 
/*     */   static byte[] shortsToBytes(short[] value)
/*     */   {
/* 222 */     if (value != null)
/*     */     {
/* 224 */       byte[] ret = new byte[value.length * 2];
/* 225 */       for (int i = 0; i < value.length; i++)
/*     */       {
/* 227 */         ret[(i * 2)] = LOBYTE(value[i]);
/* 228 */         ret[(i * 2 + 1)] = HIBYTE(value[i]);
/*     */       }
/* 230 */       return ret;
/*     */     }
/*     */ 
/* 233 */     return null;
/*     */   }
/*     */ 
/*     */   public short[] toInt16()
/*     */   {
/* 238 */     checkType(new RasterTagMetadataDataType[] { RasterTagMetadataDataType.INT_16, RasterTagMetadataDataType.UINT_16 });
/*     */ 
/* 240 */     return bytesToShorts(this._data);
/*     */   }
/*     */ 
/*     */   static short[] bytesToShorts(byte[] data)
/*     */   {
/* 245 */     if (data != null)
/*     */     {
/* 247 */       short[] ret = new short[data.length / 2];
/* 248 */       for (int i = 0; i < ret.length; i++)
/* 249 */         ret[i] = MAKEWORD(data[(i + i)], data[(i + i + 1)]);
/* 250 */       return ret;
/*     */     }
/*     */ 
/* 253 */     return null;
/*     */   }
/*     */ 
/*     */   public void fromInt16(short[] value)
/*     */   {
/* 258 */     checkType(RasterTagMetadataDataType.INT_16);
/*     */ 
/* 260 */     this._data = shortsToBytes(value);
/*     */   }
/*     */ 
/*     */   public int[] toUInt32()
/*     */   {
/* 265 */     checkType(RasterTagMetadataDataType.UINT_32);
/*     */ 
/* 267 */     return toIntArray();
/*     */   }
/*     */ 
/*     */   private int[] toIntArray()
/*     */   {
/* 272 */     if (this._data != null)
/*     */     {
/* 274 */       int[] ret = new int[this._data.length / 4];
/* 275 */       for (int i = 0; i < ret.length; i++)
/* 276 */         ret[i] = bytesToInt(this._data, i * 4);
/* 277 */       return ret;
/*     */     }
/*     */ 
/* 280 */     return null;
/*     */   }
/*     */ 
/*     */   public void fromUInt32(int[] value) {
/* 284 */     checkType(RasterTagMetadataDataType.UINT_32);
/*     */ 
/* 286 */     fromIntArray(value);
/*     */   }
/*     */ 
/*     */   private static int bytesToInt(byte[] buf, int index)
/*     */   {
/* 291 */     return buf[index] & 0xFF | (buf[(index + 1)] & 0xFF) << 8 | (buf[(index + 2)] & 0xFF) << 16 | (buf[(index + 3)] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */   private static void intToBytes(byte[] data, int index, int val)
/*     */   {
/* 296 */     data[index] = ((byte)(val & 0xFF));
/* 297 */     data[(index + 1)] = ((byte)(val >> 8 & 0xFF));
/* 298 */     data[(index + 2)] = ((byte)(val >> 16 & 0xFF));
/* 299 */     data[(index + 3)] = ((byte)(val >> 24 & 0xFF));
/*     */   }
/*     */ 
/*     */   private static long bytesToLong(byte[] buf, int index)
/*     */   {
/* 304 */     int low = bytesToInt(buf, index);
/* 305 */     int high = bytesToInt(buf, index + 4);
/* 306 */     return low & 0xFFFFFFFF | high << 32;
/*     */   }
/*     */ 
/*     */   private static void longToBytes(byte[] data, int index, long val)
/*     */   {
/* 311 */     intToBytes(data, index, (int)(val & 0xFFFFFFFF));
/* 312 */     intToBytes(data, index + 4, (int)(val >> 32 & 0xFFFFFFFF));
/*     */   }
/*     */ 
/*     */   private void fromIntArray(int[] value)
/*     */   {
/* 317 */     if (value != null)
/*     */     {
/* 319 */       this._data = new byte[value.length * 4];
/* 320 */       for (int i = 0; i < value.length; i++)
/* 321 */         intToBytes(this._data, i * 4, value[i]);
/*     */     }
/*     */     else {
/* 324 */       this._data = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int[] toInt32() {
/* 329 */     checkType(new RasterTagMetadataDataType[] { RasterTagMetadataDataType.INT_32, RasterTagMetadataDataType.UINT_32 });
/*     */ 
/* 331 */     return toIntArray();
/*     */   }
/*     */ 
/*     */   public void fromInt32(int[] value)
/*     */   {
/* 336 */     checkType(RasterTagMetadataDataType.INT_32);
/*     */ 
/* 338 */     fromIntArray(value);
/*     */   }
/*     */ 
/*     */   static RasterMetadataURational[] bytesToURationals(byte[] data)
/*     */   {
/* 343 */     if (data != null)
/*     */     {
/* 345 */       RasterMetadataURational[] ret = new RasterMetadataURational[data.length / 8];
/* 346 */       for (int i = 0; i < ret.length; i++)
/* 347 */         ret[i] = new RasterMetadataURational(bytesToInt(data, i * 8), bytesToInt(data, i * 8 + 4));
/* 348 */       return ret;
/*     */     }
/*     */ 
/* 351 */     return null;
/*     */   }
/*     */ 
/*     */   public RasterMetadataURational[] toURational()
/*     */   {
/* 356 */     checkType(RasterTagMetadataDataType.URATIONAL);
/* 357 */     return bytesToURationals(this._data);
/*     */   }
/*     */ 
/*     */   public void fromURational(RasterMetadataURational[] value)
/*     */   {
/* 362 */     checkType(RasterTagMetadataDataType.URATIONAL);
/*     */ 
/* 364 */     this._data = uRationalsToBytes(value);
/*     */   }
/*     */ 
/*     */   static byte[] uRationalsToBytes(RasterMetadataURational[] value)
/*     */   {
/*     */     byte[] data;
/* 370 */     if (value != null)
/*     */     {
/* 372 */       byte[] data = new byte[value.length * 8];
/* 373 */       for (int i = 0; i < value.length; i++)
/*     */       {
/* 375 */         intToBytes(data, i * 8, value[i].getNumerator());
/* 376 */         intToBytes(data, i * 8 + 4, value[i].getDenominator());
/*     */       }
/*     */     }
/*     */     else {
/* 380 */       data = null;
/* 381 */     }return data;
/*     */   }
/*     */ 
/*     */   public RasterMetadataRational[] toRational()
/*     */   {
/* 386 */     checkType(RasterTagMetadataDataType.RATIONAL);
/*     */ 
/* 388 */     return bytesToRationals(this._data);
/*     */   }
/*     */ 
/*     */   static RasterMetadataRational[] bytesToRationals(byte[] data)
/*     */   {
/* 393 */     if (data != null)
/*     */     {
/* 395 */       RasterMetadataRational[] ret = new RasterMetadataRational[data.length / 8];
/* 396 */       for (int i = 0; i < ret.length; i++)
/* 397 */         ret[i] = new RasterMetadataRational(bytesToInt(data, i * 8), bytesToInt(data, i * 8 + 4));
/* 398 */       return ret;
/*     */     }
/*     */ 
/* 401 */     return null;
/*     */   }
/*     */ 
/*     */   public void fromRational(RasterMetadataRational[] value)
/*     */   {
/* 406 */     checkType(RasterTagMetadataDataType.RATIONAL);
/*     */ 
/* 408 */     this._data = rationalsToBytes(value);
/*     */   }
/*     */ 
/*     */   static byte[] rationalsToBytes(RasterMetadataRational[] value)
/*     */   {
/*     */     byte[] data;
/* 414 */     if (value != null)
/*     */     {
/* 416 */       byte[] data = new byte[value.length * 8];
/* 417 */       for (int i = 0; i < value.length; i++)
/*     */       {
/* 419 */         intToBytes(data, i * 8, value[i].getNumerator());
/* 420 */         intToBytes(data, i * 8 + 4, value[i].getDenominator());
/*     */       }
/*     */     }
/*     */     else {
/* 424 */       data = null;
/* 425 */     }return data;
/*     */   }
/*     */ 
/*     */   public float[] toSingle()
/*     */   {
/* 430 */     checkType(RasterTagMetadataDataType.SINGLE);
/*     */ 
/* 432 */     if (this._data != null)
/*     */     {
/* 434 */       int size = getDataTypeSize(getDataType());
/* 435 */       float[] ret = new float[this._data.length / size];
/* 436 */       for (int i = 0; i < ret.length; i++)
/* 437 */         ret[i] = Float.intBitsToFloat(bytesToInt(this._data, i * size));
/* 438 */       return ret;
/*     */     }
/*     */ 
/* 441 */     return null;
/*     */   }
/*     */ 
/*     */   public void fromSingle(float[] value) {
/* 445 */     checkType(RasterTagMetadataDataType.SINGLE);
/*     */ 
/* 447 */     if (value != null)
/*     */     {
/* 449 */       int size = getDataTypeSize(getDataType());
/* 450 */       this._data = new byte[value.length * size];
/* 451 */       for (int i = 0; i < value.length; i++)
/* 452 */         intToBytes(this._data, i * size, Float.floatToRawIntBits(value[i]));
/*     */     }
/*     */     else {
/* 455 */       this._data = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public double[] toDouble() {
/* 460 */     checkType(RasterTagMetadataDataType.DOUBLE);
/*     */ 
/* 462 */     if (this._data != null)
/*     */     {
/* 464 */       int size = getDataTypeSize(getDataType());
/* 465 */       double[] ret = new double[this._data.length / size];
/* 466 */       for (int i = 0; i < ret.length; i++)
/* 467 */         ret[i] = Double.longBitsToDouble(bytesToLong(this._data, i * size));
/* 468 */       return ret;
/*     */     }
/*     */ 
/* 471 */     return null;
/*     */   }
/*     */ 
/*     */   public void fromDouble(double[] value) {
/* 475 */     checkType(RasterTagMetadataDataType.DOUBLE);
/*     */ 
/* 477 */     if (value != null)
/*     */     {
/* 479 */       int size = getDataTypeSize(getDataType());
/* 480 */       this._data = new byte[value.length * size];
/* 481 */       for (int i = 0; i < value.length; i++)
/* 482 */         longToBytes(this._data, i * size, Double.doubleToRawLongBits(value[i]));
/*     */     }
/*     */     else {
/* 485 */       this._data = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterTagMetadata
 * JD-Core Version:    0.6.2
 */