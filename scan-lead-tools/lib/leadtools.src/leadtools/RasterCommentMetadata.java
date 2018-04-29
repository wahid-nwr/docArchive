/*     */ package leadtools;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class RasterCommentMetadata extends RasterMetadata
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   private RasterCommentMetadataType _type;
/*     */   private byte[] _data;
/*     */   public static final int IPTC_SEPARATOR = 1;
/*     */ 
/*     */   private void init(RasterCommentMetadataType type, byte[] data)
/*     */   {
/*  14 */     this._type = type;
/*  15 */     this._data = data;
/*     */   }
/*     */ 
/*     */   private void checkType(RasterCommentMetadataDataType[] dataType) {
/*  19 */     for (RasterCommentMetadataDataType i : dataType)
/*     */     {
/*  21 */       if (getDataType() == i) {
/*  22 */         return;
/*     */       }
/*     */     }
/*  25 */     throw new IllegalArgumentException("Invalid data type");
/*     */   }
/*     */ 
/*     */   private void checkType(RasterCommentMetadataDataType dataType) {
/*  29 */     if (dataType != getDataType())
/*  30 */       throw new IllegalArgumentException("Invalid data type");
/*     */   }
/*     */ 
/*     */   public static int getDataTypeSize(RasterCommentMetadataDataType dataType)
/*     */   {
/*  37 */     switch (1.$SwitchMap$leadtools$RasterCommentMetadataDataType[dataType.ordinal()])
/*     */     {
/*     */     case 1:
/*     */     case 2:
/*  41 */       return 8;
/*     */     case 3:
/*  44 */       return 2;
/*     */     }
/*     */ 
/*  47 */     return 1;
/*     */   }
/*     */ 
/*     */   public static RasterCommentMetadataDataType getDataType(RasterCommentMetadataType type)
/*     */   {
/*  53 */     switch (1.$SwitchMap$leadtools$RasterCommentMetadataType[type.ordinal()])
/*     */     {
/*     */     case 1:
/*  56 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 2:
/*  58 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 3:
/*  60 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 4:
/*  62 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 5:
/*  64 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 6:
/*  66 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 7:
/*  68 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 8:
/*  70 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 9:
/*  72 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 10:
/*  74 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 11:
/*  76 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 12:
/*  78 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 13:
/*  80 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 14:
/*  82 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 15:
/*  84 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 16:
/*  86 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 17:
/*  88 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 18:
/*  90 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 19:
/*  92 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 20:
/*  94 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 21:
/*  96 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 22:
/*  98 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 23:
/* 100 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 24:
/* 102 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 25:
/* 104 */       return RasterCommentMetadataDataType.RATIONAL;
/*     */     case 26:
/* 106 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 27:
/* 108 */       return RasterCommentMetadataDataType.RATIONAL;
/*     */     case 28:
/* 110 */       return RasterCommentMetadataDataType.RATIONAL;
/*     */     case 29:
/* 112 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 30:
/* 114 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 31:
/* 116 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 32:
/* 118 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 33:
/* 120 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 34:
/* 122 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 35:
/* 124 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 36:
/* 126 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 37:
/* 128 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 38:
/* 130 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 39:
/* 132 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 40:
/* 134 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 41:
/* 136 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 42:
/* 138 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 43:
/* 140 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 44:
/* 142 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 45:
/* 144 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 46:
/* 146 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 47:
/* 148 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 48:
/* 150 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 49:
/* 152 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 50:
/* 154 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 51:
/* 156 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 52:
/* 158 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 53:
/* 160 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 54:
/* 162 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 55:
/* 164 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 56:
/* 166 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 57:
/* 168 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 58:
/* 170 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 59:
/* 172 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 60:
/* 174 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 61:
/* 176 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 62:
/* 178 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 63:
/* 180 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 64:
/* 182 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 65:
/* 184 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 66:
/* 186 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 67:
/* 188 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 68:
/* 190 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 69:
/* 192 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 70:
/* 194 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 71:
/* 196 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 72:
/* 198 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 73:
/* 200 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 74:
/* 202 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 75:
/* 204 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 76:
/* 206 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 77:
/* 208 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 78:
/* 210 */       return RasterCommentMetadataDataType.URATIONAL;
/*     */     case 79:
/* 212 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 80:
/* 214 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 81:
/* 216 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 82:
/* 218 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 83:
/* 220 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 84:
/* 222 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 85:
/* 224 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 86:
/* 226 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 87:
/* 228 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 88:
/* 230 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 89:
/* 232 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 90:
/* 234 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 91:
/* 236 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 92:
/* 238 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 93:
/* 240 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 94:
/* 242 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 95:
/* 244 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 96:
/* 246 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 97:
/* 248 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 98:
/* 250 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 99:
/* 252 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 100:
/* 254 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 101:
/* 256 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 102:
/* 258 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 103:
/* 260 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 104:
/* 262 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 105:
/* 264 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 106:
/* 266 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 107:
/* 268 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 108:
/* 270 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 109:
/* 272 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 110:
/* 274 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 111:
/* 276 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 112:
/* 278 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 113:
/* 280 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 114:
/* 282 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 115:
/* 284 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 116:
/* 286 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 117:
/* 288 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 118:
/* 290 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 119:
/* 292 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 120:
/* 294 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 121:
/* 296 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 122:
/* 298 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 123:
/* 300 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 124:
/* 302 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 125:
/* 304 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 126:
/* 306 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 127:
/* 308 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 128:
/* 310 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 129:
/* 312 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 130:
/* 314 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 131:
/* 316 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 132:
/* 318 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 133:
/* 320 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 134:
/* 322 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 135:
/* 324 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 136:
/* 326 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 137:
/* 328 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 138:
/* 330 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 139:
/* 332 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 140:
/* 334 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 141:
/* 336 */       return RasterCommentMetadataDataType.RATIONAL;
/*     */     case 142:
/* 338 */       return RasterCommentMetadataDataType.RATIONAL;
/*     */     case 143:
/* 340 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 144:
/* 342 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 145:
/* 344 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 146:
/* 346 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 147:
/* 348 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 148:
/* 350 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 149:
/* 352 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 150:
/* 354 */       return RasterCommentMetadataDataType.INT_16;
/*     */     case 151:
/* 356 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 152:
/* 358 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 153:
/* 360 */       return RasterCommentMetadataDataType.BYTE;
/*     */     case 154:
/* 362 */       return RasterCommentMetadataDataType.ASCII;
/*     */     case 155:
/* 364 */       return RasterCommentMetadataDataType.INT_16;
/*     */     }
/* 366 */     return RasterCommentMetadataDataType.BYTE;
/*     */   }
/*     */ 
/*     */   public RasterCommentMetadata()
/*     */   {
/* 372 */     init(RasterCommentMetadataType.DESCRIPTION, null);
/*     */   }
/*     */ 
/*     */   public RasterCommentMetadata(RasterCommentMetadataType type, byte[] data) {
/* 376 */     init(type, data);
/*     */   }
/*     */ 
/*     */   public RasterCommentMetadata clone()
/*     */   {
/* 381 */     return new RasterCommentMetadata(this._type, this._data);
/*     */   }
/*     */ 
/*     */   public RasterCommentMetadataType getType()
/*     */   {
/* 386 */     return this._type;
/*     */   }
/*     */ 
/*     */   public void setType(RasterCommentMetadataType value) {
/* 390 */     this._type = value;
/*     */   }
/*     */ 
/*     */   public RasterCommentMetadataDataType getDataType()
/*     */   {
/* 395 */     return getDataType(this._type);
/*     */   }
/*     */ 
/*     */   public byte[] getData()
/*     */   {
/* 400 */     return this._data;
/*     */   }
/*     */ 
/*     */   public void setData(byte[] value)
/*     */   {
/* 405 */     this._data = value;
/*     */   }
/*     */ 
/*     */   public byte[] toByte()
/*     */   {
/* 410 */     checkType(RasterCommentMetadataDataType.BYTE);
/*     */ 
/* 412 */     return this._data != null ? (byte[])this._data.clone() : new byte[0];
/*     */   }
/*     */ 
/*     */   public void fromByte(byte[] value)
/*     */   {
/* 417 */     checkType(RasterCommentMetadataDataType.BYTE);
/*     */ 
/* 419 */     this._data = (value != null ? (byte[])value.clone() : null);
/*     */   }
/*     */ 
/*     */   public String toAscii()
/*     */   {
/* 424 */     checkType(RasterCommentMetadataDataType.ASCII);
/*     */ 
/* 426 */     if (this._data != null)
/*     */     {
/* 428 */       int lenNoNulls = this._data.length;
/* 429 */       while ((lenNoNulls > 0) && (this._data[(lenNoNulls - 1)] == 0)) lenNoNulls--;
/*     */ 
/* 432 */       return new String(this._data, 0, lenNoNulls);
/*     */     }
/*     */ 
/* 435 */     return "";
/*     */   }
/*     */ 
/*     */   public void fromAscii(String value) {
/* 439 */     checkType(RasterCommentMetadataDataType.ASCII);
/*     */ 
/* 441 */     this._data = (value != null ? value : "").getBytes();
/*     */   }
/*     */ 
/*     */   public final RasterMetadataURational[] toURational()
/*     */   {
/* 446 */     checkType(RasterCommentMetadataDataType.URATIONAL);
/*     */ 
/* 448 */     return RasterTagMetadata.bytesToURationals(this._data);
/*     */   }
/*     */ 
/*     */   public void fromURational(RasterMetadataURational[] value)
/*     */   {
/* 453 */     checkType(RasterCommentMetadataDataType.URATIONAL);
/*     */ 
/* 455 */     this._data = RasterTagMetadata.uRationalsToBytes(value);
/*     */   }
/*     */ 
/*     */   public RasterMetadataRational[] toRational()
/*     */   {
/* 460 */     checkType(new RasterCommentMetadataDataType[] { RasterCommentMetadataDataType.RATIONAL, RasterCommentMetadataDataType.URATIONAL });
/*     */ 
/* 462 */     return RasterTagMetadata.bytesToRationals(this._data);
/*     */   }
/*     */ 
/*     */   public void fromRational(RasterMetadataRational[] value)
/*     */   {
/* 467 */     checkType(new RasterCommentMetadataDataType[] { RasterCommentMetadataDataType.RATIONAL, RasterCommentMetadataDataType.URATIONAL });
/*     */ 
/* 469 */     this._data = RasterTagMetadata.rationalsToBytes(value);
/*     */   }
/*     */ 
/*     */   public short[] toInt16()
/*     */   {
/* 474 */     return RasterTagMetadata.bytesToShorts(this._data);
/*     */   }
/*     */ 
/*     */   public void fromInt16(short[] value)
/*     */   {
/* 479 */     checkType(RasterCommentMetadataDataType.INT_16);
/*     */ 
/* 481 */     this._data = RasterTagMetadata.shortsToBytes(value);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterCommentMetadata
 * JD-Core Version:    0.6.2
 */