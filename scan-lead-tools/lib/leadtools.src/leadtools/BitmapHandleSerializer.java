/*     */ package leadtools;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ 
/*     */ class BitmapHandleSerializer
/*     */ {
/*     */   static final int serialVersionUID = 0;
/*     */ 
/*     */   public static int serialize(long bitmapHandle, ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/*  14 */     if (ltkrn.BITMAPHANDLE_hasDitherData(bitmapHandle)) {
/*  15 */       ltkrn.StopDithering(bitmapHandle);
/*     */     }
/*     */ 
/*  20 */     stream.writeInt(ltkrn.BITMAPHANDLE_getWidth(bitmapHandle));
/*  21 */     stream.writeInt(ltkrn.BITMAPHANDLE_getHeight(bitmapHandle));
/*  22 */     stream.writeInt(ltkrn.BITMAPHANDLE_getBitsPerPixel(bitmapHandle));
/*  23 */     stream.writeInt(ltkrn.BITMAPHANDLE_getOrder(bitmapHandle));
/*  24 */     stream.writeInt(ltkrn.BITMAPHANDLE_getViewPerspective(bitmapHandle));
/*  25 */     serializePalette(stream, ltkrn.BITMAPHANDLE_getPalette(bitmapHandle), ltkrn.BITMAPHANDLE_getColors(bitmapHandle));
/*  26 */     stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsCompressed(bitmapHandle) ? 1 : 0);
/*  27 */     stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsSuperCompressed(bitmapHandle) ? 1 : 0);
/*  28 */     stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsDiskMemory(bitmapHandle) ? 1 : 0);
/*  29 */     stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsTiled(bitmapHandle) ? 1 : 0);
/*     */ 
/*  33 */     int bytesPerLine = ltkrn.BITMAPHANDLE_getBytesPerLine(bitmapHandle);
/*  34 */     int maxDataChunkSize = 32768;
/*     */ 
/*  37 */     int scanLines = maxDataChunkSize / bytesPerLine;
/*  38 */     if (scanLines == 0) {
/*  39 */       scanLines = 1;
/*     */     }
/*  41 */     int chunkSize = scanLines * bytesPerLine;
/*     */ 
/*  43 */     if (ltkrn.BITMAPHANDLE_getFlagsCompressed(bitmapHandle))
/*     */     {
/*  45 */       scanLines = 1;
/*  46 */       chunkSize = bytesPerLine;
/*     */     }
/*     */ 
/*  50 */     int scanLinesLeft = ltkrn.BITMAPHANDLE_getHeight(bitmapHandle);
/*  51 */     int row = 0;
/*     */ 
/*  53 */     byte[] buffer = new byte[chunkSize];
/*     */ 
/*  55 */     boolean locked = false;
/*  56 */     int ret = ltkrn.AccessBitmap(bitmapHandle);
/*  57 */     if (ret == L_ERROR.SUCCESS.getValue()) {
/*  58 */       locked = true;
/*     */     }
/*  60 */     while ((scanLinesLeft > 0) && (ret == L_ERROR.SUCCESS.getValue()))
/*     */     {
/*  63 */       if (scanLinesLeft < scanLines)
/*     */       {
/*  65 */         scanLines = scanLinesLeft;
/*     */ 
/*  67 */         chunkSize = scanLines * bytesPerLine;
/*     */ 
/*  69 */         buffer = new byte[chunkSize];
/*     */       }
/*     */ 
/*  73 */       long longRet = ltkrn.GetBitmapRow(bitmapHandle, buffer, 0, row, chunkSize);
/*  74 */       if (longRet <= 0L) {
/*  75 */         ret = (int)longRet;
/*     */       }
/*  77 */       if (ret == L_ERROR.SUCCESS.getValue())
/*     */       {
/*  80 */         stream.write(buffer);
/*     */ 
/*  82 */         row += scanLines;
/*  83 */         scanLinesLeft -= scanLines;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  88 */     if (ret == L_ERROR.SUCCESS.getValue())
/*     */     {
/*  90 */       if (locked) {
/*  91 */         ltkrn.ReleaseBitmap(bitmapHandle);
/*     */       }
/*     */     }
/*  94 */     if (ret == L_ERROR.SUCCESS.getValue())
/*     */     {
/*  97 */       long int64val = ltkrn.BITMAPHANDLE_getCurrentPos(bitmapHandle);
/*  98 */       stream.writeLong(int64val);
/*  99 */       stream.writeInt(ltkrn.BITMAPHANDLE_getCurrentRow(bitmapHandle));
/*     */ 
/* 102 */       ret = serializeRegionData(stream, bitmapHandle);
/* 103 */       if (ret == L_ERROR.SUCCESS.getValue())
/*     */       {
/* 105 */         stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsProgressiveAvailable(bitmapHandle) ? 1 : 0);
/* 106 */         stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsInterlaced(bitmapHandle) ? 1 : 0);
/* 107 */         stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsWaitUserInput(bitmapHandle) ? 1 : 0);
/* 108 */         stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsTransparency(bitmapHandle) ? 1 : 0);
/* 109 */         stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsSigned(bitmapHandle) ? 1 : 0);
/* 110 */         stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsUseLUT(bitmapHandle) ? 1 : 0);
/* 111 */         stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsUsePaintLUT(bitmapHandle) ? 1 : 0);
/* 112 */         stream.writeByte(ltkrn.BITMAPHANDLE_getFlagsNoClip(bitmapHandle) ? 1 : 0);
/*     */ 
/* 114 */         stream.writeInt(ltkrn.BITMAPHANDLE_getDitheringMethod(bitmapHandle));
/* 115 */         stream.writeInt(ltkrn.BITMAPHANDLE_getXResolution(bitmapHandle));
/* 116 */         stream.writeInt(ltkrn.BITMAPHANDLE_getYResolution(bitmapHandle));
/* 117 */         stream.writeInt(ltkrn.BITMAPHANDLE_getLeft(bitmapHandle));
/* 118 */         stream.writeInt(ltkrn.BITMAPHANDLE_getTop(bitmapHandle));
/* 119 */         stream.writeInt(ltkrn.BITMAPHANDLE_getDelay(bitmapHandle));
/* 120 */         stream.writeInt(ltkrn.BITMAPHANDLE_getBackground(bitmapHandle));
/* 121 */         stream.writeInt(ltkrn.BITMAPHANDLE_getTransparency(bitmapHandle));
/* 122 */         stream.writeInt(ltkrn.BITMAPHANDLE_getDisposalMethod(bitmapHandle));
/* 123 */         stream.writeInt(ltkrn.BITMAPHANDLE_getLowBit(bitmapHandle));
/* 124 */         stream.writeInt(ltkrn.BITMAPHANDLE_getHighBit(bitmapHandle));
/* 125 */         serializePalette(stream, ltkrn.BITMAPHANDLE_getLUT(bitmapHandle), ltkrn.BITMAPHANDLE_getLUTLength(bitmapHandle));
/* 126 */         serializePalette16(stream, ltkrn.BITMAPHANDLE_getLUT16(bitmapHandle), ltkrn.BITMAPHANDLE_getLUTLength16(bitmapHandle));
/* 127 */         stream.writeInt(ltkrn.BITMAPHANDLE_getMinVal(bitmapHandle));
/* 128 */         stream.writeInt(ltkrn.BITMAPHANDLE_getMaxVal(bitmapHandle));
/* 129 */         stream.writeInt(ltkrn.BITMAPHANDLE_getPaintLowBit(bitmapHandle));
/* 130 */         stream.writeInt(ltkrn.BITMAPHANDLE_getPaintHighBit(bitmapHandle));
/* 131 */         stream.writeInt(ltkrn.BITMAPHANDLE_getPaintGamma(bitmapHandle));
/* 132 */         stream.writeInt(ltkrn.BITMAPHANDLE_getPaintContrast(bitmapHandle));
/* 133 */         stream.writeInt(ltkrn.BITMAPHANDLE_getPaintIntensity(bitmapHandle));
/* 134 */         stream.writeObject(ltkrn.BITMAPHANDLE_getPaintLUT(bitmapHandle));
/* 135 */         int ret2 = serializeOverlaysInfo(stream, bitmapHandle);
/* 136 */         if (ret2 == L_ERROR.SUCCESS.getValue())
/*     */         {
/* 138 */           stream.writeInt(ltkrn.BITMAPHANDLE_getReserved(bitmapHandle));
/* 139 */           stream.writeInt(ltkrn.BITMAPHANDLE_getOriginalFormat(bitmapHandle));
/* 140 */           serializePaintLut16(stream, ltkrn.BITMAPHANDLE_getPaintLUT16(bitmapHandle));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 145 */     return ret;
/*     */   }
/*     */ 
/*     */   public static int deserialize(long bitmapHandle, ObjectInputStream stream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 152 */     int width = stream.readInt();
/* 153 */     int height = stream.readInt();
/* 154 */     int bitsPerPixel = stream.readInt();
/* 155 */     int order = stream.readInt();
/* 156 */     int viewPerspective = stream.readInt();
/* 157 */     RasterColor[] palette = deserializePalette(stream);
/*     */ 
/* 159 */     boolean compressed = stream.readByte() != 0;
/* 160 */     boolean superCompressed = stream.readByte() != 0;
/* 161 */     boolean diskMemory = stream.readByte() != 0;
/* 162 */     boolean tiled = stream.readByte() != 0;
/*     */     int memoryFlags;
/*     */     int memoryFlags;
/* 165 */     if (compressed) {
/* 166 */       memoryFlags = 512;
/*     */     }
/*     */     else
/*     */     {
/*     */       int memoryFlags;
/* 167 */       if (superCompressed) {
/* 168 */         memoryFlags = 1024;
/*     */       }
/*     */       else
/*     */       {
/*     */         int memoryFlags;
/* 169 */         if (diskMemory) {
/* 170 */           memoryFlags = 128;
/*     */         }
/*     */         else
/*     */         {
/*     */           int memoryFlags;
/* 171 */           if (tiled)
/* 172 */             memoryFlags = 4;
/*     */           else
/* 174 */             memoryFlags = 1; 
/*     */         }
/*     */       }
/*     */     }
/* 176 */     int ret = ltkrn.CreateBitmap(bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), memoryFlags, width, height, bitsPerPixel, order, palette, viewPerspective, null, 0L);
/* 177 */     if (ret == L_ERROR.SUCCESS.getValue())
/*     */     {
/* 179 */       int bytesPerLine = ltkrn.BITMAPHANDLE_getBytesPerLine(bitmapHandle);
/* 180 */       int maxDataChunkSize = 32768;
/*     */ 
/* 183 */       int scanLines = maxDataChunkSize / bytesPerLine;
/* 184 */       if (scanLines == 0)
/* 185 */         scanLines = 1;
/* 186 */       int chunkSize = scanLines * bytesPerLine;
/*     */ 
/* 188 */       if (ltkrn.BITMAPHANDLE_getFlagsCompressed(bitmapHandle))
/*     */       {
/* 190 */         scanLines = 1;
/* 191 */         chunkSize = bytesPerLine;
/*     */       }
/*     */ 
/* 194 */       int scanLinesLeft = height;
/* 195 */       int row = 0;
/*     */ 
/* 197 */       byte[] buffer = new byte[chunkSize];
/*     */ 
/* 199 */       boolean locked = false;
/* 200 */       ret = ltkrn.AccessBitmap(bitmapHandle);
/* 201 */       if (ret == L_ERROR.SUCCESS.getValue()) {
/* 202 */         locked = true;
/*     */       }
/* 204 */       while ((scanLinesLeft > 0) && (ret == L_ERROR.SUCCESS.getValue()))
/*     */       {
/* 207 */         if (scanLinesLeft < scanLines)
/*     */         {
/* 209 */           scanLines = scanLinesLeft;
/*     */ 
/* 211 */           chunkSize = scanLines * bytesPerLine;
/*     */ 
/* 213 */           buffer = new byte[chunkSize];
/*     */         }
/*     */ 
/* 217 */         stream.readFully(buffer);
/*     */ 
/* 220 */         long longRet = ltkrn.PutBitmapRow(bitmapHandle, buffer, 0, row, chunkSize);
/* 221 */         if (longRet <= 0L) {
/* 222 */           ret = (int)longRet;
/*     */         }
/* 224 */         row += scanLines;
/* 225 */         scanLinesLeft -= scanLines;
/*     */       }
/*     */ 
/* 228 */       if (ret == L_ERROR.SUCCESS.getValue())
/*     */       {
/* 230 */         if (locked) {
/* 231 */           ltkrn.ReleaseBitmap(bitmapHandle);
/*     */         }
/*     */       }
/* 234 */       if (ret == L_ERROR.SUCCESS.getValue())
/*     */       {
/* 237 */         ltkrn.BITMAPHANDLE_setCurrentPos(bitmapHandle, stream.readLong());
/* 238 */         ltkrn.BITMAPHANDLE_setCurrentRow(bitmapHandle, stream.readInt());
/*     */ 
/* 240 */         deserializeRegionData(stream, bitmapHandle);
/*     */ 
/* 242 */         ltkrn.BITMAPHANDLE_setFlagsProgressiveAvailable(bitmapHandle, stream.readByte() != 0);
/* 243 */         ltkrn.BITMAPHANDLE_setFlagsInterlaced(bitmapHandle, stream.readByte() != 0);
/* 244 */         ltkrn.BITMAPHANDLE_setFlagsWaitUserInput(bitmapHandle, stream.readByte() != 0);
/* 245 */         ltkrn.BITMAPHANDLE_setFlagsTransparency(bitmapHandle, stream.readByte() != 0);
/* 246 */         ltkrn.BITMAPHANDLE_setFlagsSigned(bitmapHandle, stream.readByte() != 0);
/* 247 */         ltkrn.BITMAPHANDLE_setFlagsUseLUT(bitmapHandle, stream.readByte() != 0);
/* 248 */         ltkrn.BITMAPHANDLE_setFlagsUsePaintLUT(bitmapHandle, stream.readByte() != 0);
/* 249 */         ltkrn.BITMAPHANDLE_setFlagsNoClip(bitmapHandle, stream.readByte() != 0);
/*     */ 
/* 252 */         ltkrn.BITMAPHANDLE_setDitheringMethod(bitmapHandle, stream.readInt());
/*     */ 
/* 255 */         ltkrn.BITMAPHANDLE_setXResolution(bitmapHandle, stream.readInt());
/* 256 */         ltkrn.BITMAPHANDLE_setYResolution(bitmapHandle, stream.readInt());
/* 257 */         ltkrn.BITMAPHANDLE_setLeft(bitmapHandle, stream.readInt());
/* 258 */         ltkrn.BITMAPHANDLE_setTop(bitmapHandle, stream.readInt());
/* 259 */         ltkrn.BITMAPHANDLE_setDelay(bitmapHandle, stream.readInt());
/* 260 */         ltkrn.BITMAPHANDLE_setBackground(bitmapHandle, stream.readInt());
/* 261 */         ltkrn.BITMAPHANDLE_setTransparency(bitmapHandle, stream.readInt());
/* 262 */         ltkrn.BITMAPHANDLE_setDisposalMethod(bitmapHandle, stream.readInt());
/* 263 */         ltkrn.BITMAPHANDLE_setLowBit(bitmapHandle, stream.readInt());
/* 264 */         ltkrn.BITMAPHANDLE_setHighBit(bitmapHandle, stream.readInt());
/* 265 */         RasterColor[] lut = deserializePalette(stream);
/* 266 */         if ((lut != null) && (lut.length > 0))
/* 267 */           ltkrn.BITMAPHANDLE_setLUT(bitmapHandle, lut, lut.length);
/*     */         else {
/* 269 */           ltkrn.BITMAPHANDLE_setLUT(bitmapHandle, null, 0);
/*     */         }
/* 271 */         RasterColor16[] lut16 = deserializePalette16(stream);
/* 272 */         if ((lut16 != null) && (lut16.length > 0))
/* 273 */           ltkrn.BITMAPHANDLE_setLUT16(bitmapHandle, lut16, lut16.length);
/*     */         else {
/* 275 */           ltkrn.BITMAPHANDLE_setLUT16(bitmapHandle, null, 0);
/*     */         }
/* 277 */         ltkrn.BITMAPHANDLE_setMinVal(bitmapHandle, stream.readInt());
/* 278 */         ltkrn.BITMAPHANDLE_setMaxVal(bitmapHandle, stream.readInt());
/* 279 */         ltkrn.BITMAPHANDLE_setPaintLowBit(bitmapHandle, stream.readInt());
/* 280 */         ltkrn.BITMAPHANDLE_setPaintHighBit(bitmapHandle, stream.readInt());
/* 281 */         ltkrn.BITMAPHANDLE_setPaintGamma(bitmapHandle, stream.readInt());
/* 282 */         ltkrn.BITMAPHANDLE_setPaintContrast(bitmapHandle, stream.readInt());
/* 283 */         ltkrn.BITMAPHANDLE_setPaintIntensity(bitmapHandle, stream.readInt());
/*     */ 
/* 285 */         byte[] paintLUT = deserializePaintLut(stream);
/* 286 */         ltkrn.BITMAPHANDLE_setPaintLUT(bitmapHandle, paintLUT);
/*     */ 
/* 288 */         int ret2 = deserializeOverlaysInfo(stream, bitmapHandle);
/* 289 */         if (ret2 == L_ERROR.SUCCESS.getValue())
/*     */         {
/* 291 */           ltkrn.BITMAPHANDLE_setReserved(bitmapHandle, stream.readInt());
/* 292 */           ltkrn.BITMAPHANDLE_setOriginalFormat(bitmapHandle, stream.readInt());
/* 293 */           ltkrn.BITMAPHANDLE_setPaintLUT16(bitmapHandle, deserializePaintLut16(stream));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 298 */     return ret;
/*     */   }
/*     */ 
/*     */   private static void serializePalette(ObjectOutputStream stream, RasterColor[] palette, int colors) throws IOException
/*     */   {
/* 303 */     stream.writeInt(colors);
/* 304 */     if (colors > 0)
/*     */     {
/* 306 */       RasterColor.writeSerializeVersion(stream);
/* 307 */       for (int i = 0; i < colors; i++)
/* 308 */         palette[i].writeObject(stream);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static RasterColor[] deserializePalette(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 314 */     int colors = stream.readInt();
/* 315 */     if (colors == 0)
/* 316 */       return null;
/* 317 */     int version = RasterColor.readSerializeVersion(stream);
/* 318 */     RasterColor[] palette = new RasterColor[colors];
/* 319 */     for (int i = 0; i < colors; i++)
/* 320 */       palette[i] = RasterColor.readObject(stream, version);
/* 321 */     return palette;
/*     */   }
/*     */ 
/*     */   public static void serializePalette16(ObjectOutputStream stream, RasterColor16[] palette, int colors) throws IOException
/*     */   {
/* 326 */     stream.writeInt(colors);
/* 327 */     if (colors > 0)
/*     */     {
/* 329 */       RasterColor16.writeSerializeVersion(stream);
/* 330 */       for (int i = 0; i < colors; i++)
/* 331 */         palette[i].writeObject(stream);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static RasterColor16[] deserializePalette16(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/* 336 */     int colors = stream.readInt();
/* 337 */     if (colors == 0)
/* 338 */       return null;
/* 339 */     int version = RasterColor16.readSerializeVersion(stream);
/* 340 */     RasterColor16[] palette = new RasterColor16[colors];
/* 341 */     for (int i = 0; i < colors; i++)
/* 342 */       palette[i] = RasterColor16.readObject(stream, version);
/* 343 */     return palette;
/*     */   }
/*     */ 
/*     */   public static void serializePaintLut16(ObjectOutputStream stream, short[] data) throws IOException
/*     */   {
/* 348 */     short[] buffer = null;
/*     */ 
/* 350 */     if (data != null)
/*     */     {
/* 352 */       int lutSize = 65536;
/* 353 */       buffer = new short[lutSize];
/* 354 */       System.arraycopy(data, 0, buffer, 0, lutSize);
/*     */     }
/*     */ 
/* 357 */     stream.writeObject(buffer);
/*     */   }
/*     */ 
/*     */   private static int serializeRegionData(ObjectOutputStream stream, long bitmapHandle) throws IOException {
/* 361 */     int ret = L_ERROR.SUCCESS.getValue();
/*     */ 
/* 363 */     byte[] buffer = null;
/* 364 */     if (ltkrn.BitmapHasRgn(bitmapHandle))
/*     */     {
/* 366 */       ret = ltkrn.GetBitmapRgnData(bitmapHandle, null, 0, null);
/* 367 */       if (ret >= 0)
/*     */       {
/* 369 */         buffer = new byte[ret];
/* 370 */         ret = ltkrn.GetBitmapRgnData(bitmapHandle, null, buffer.length, buffer);
/* 371 */         if (ret >= 0)
/*     */         {
/* 373 */           stream.writeObject(buffer);
/* 374 */           ret = L_ERROR.SUCCESS.getValue();
/*     */         }
/*     */       }
/*     */       else {
/* 378 */         stream.writeObject(buffer);
/*     */       }
/*     */     } else {
/* 381 */       stream.writeObject(buffer);
/*     */     }
/* 383 */     return ret;
/*     */   }
/*     */ 
/*     */   private static int deserializeRegionData(ObjectInputStream stream, long bitmapHandle) throws IOException, ClassNotFoundException {
/* 387 */     byte[] regionData = (byte[])stream.readObject();
/* 388 */     if (regionData != null)
/*     */     {
/* 390 */       return ltkrn.SetBitmapRgnData(bitmapHandle, null, regionData.length, regionData, 1);
/*     */     }
/* 392 */     return L_ERROR.SUCCESS.getValue();
/*     */   }
/*     */ 
/*     */   private static int serializeOverlaysInfo(ObjectOutputStream stream, long bitmapHandle)
/*     */   {
/* 397 */     return L_ERROR.SUCCESS.getValue();
/*     */   }
/*     */ 
/*     */   private static byte[] deserializePaintLut(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 402 */     byte[] buffer = (byte[])stream.readObject();
/* 403 */     return buffer;
/*     */   }
/*     */ 
/*     */   private static int deserializeOverlaysInfo(ObjectInputStream stream, long bitmapHandle)
/*     */   {
/* 408 */     return L_ERROR.SUCCESS.getValue();
/*     */   }
/*     */ 
/*     */   private static short[] deserializePaintLut16(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 413 */     short[] buffer = (short[])stream.readObject();
/* 414 */     return buffer;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.BitmapHandleSerializer
 * JD-Core Version:    0.6.2
 */