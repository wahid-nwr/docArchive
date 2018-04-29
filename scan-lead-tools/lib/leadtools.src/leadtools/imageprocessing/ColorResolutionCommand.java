/*     */ package leadtools.imageprocessing;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import leadtools.L_ERROR;
/*     */ import leadtools.RasterByteOrder;
/*     */ import leadtools.RasterColor;
/*     */ import leadtools.RasterDitheringMethod;
/*     */ import leadtools.RasterException;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.RasterImageChangedEvent;
/*     */ import leadtools.RasterImageChangedFlags;
/*     */ import leadtools.ltkrn;
/*     */ 
/*     */ public class ColorResolutionCommand extends RasterCommand
/*     */ {
/*     */   private RasterImage _destinationImage;
/*     */   private ColorResolutionCommandMode _mode;
/*     */   private int _bitsPerPixel;
/*     */   private RasterByteOrder _order;
/*     */   private RasterDitheringMethod _ditheringMethod;
/*     */   private int _paletteFlags;
/*     */   private RasterColor[] _palette;
/*     */   private int _colors;
/*     */   private boolean _initAlpha;
/*     */   private RasterImage _dataOwner;
/*     */   private Vector<ColorResolutionCommandDataListener> _data;
/*     */ 
/*     */   public ColorResolutionCommand()
/*     */   {
/*  32 */     this._destinationImage = null;
/*  33 */     this._mode = ColorResolutionCommandMode.IN_PLACE;
/*  34 */     this._bitsPerPixel = 24;
/*  35 */     this._order = RasterByteOrder.BGR;
/*  36 */     this._ditheringMethod = RasterDitheringMethod.NONE;
/*  37 */     this._paletteFlags = ColorResolutionCommandPaletteFlags.OPTIMIZED.getValue();
/*  38 */     this._palette = null;
/*  39 */     this._colors = 0;
/*  40 */     this._initAlpha = true;
/*  41 */     this._dataOwner = null;
/*     */ 
/*  43 */     this._data = new Vector();
/*     */   }
/*     */ 
/*     */   public ColorResolutionCommand(ColorResolutionCommandMode mode, int bitsPerPixel, RasterByteOrder order, RasterDitheringMethod ditheringMethod, int paletteFlags, RasterColor[] palette)
/*     */   {
/*  48 */     this._destinationImage = null;
/*     */ 
/*  50 */     this._mode = mode;
/*  51 */     this._bitsPerPixel = bitsPerPixel;
/*  52 */     this._order = order;
/*  53 */     this._ditheringMethod = ditheringMethod;
/*  54 */     this._paletteFlags = paletteFlags;
/*  55 */     this._palette = palette;
/*  56 */     this._colors = 0;
/*  57 */     this._initAlpha = true;
/*     */ 
/*  59 */     this._data = new Vector();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  65 */     return "Color Resolution";
/*     */   }
/*     */ 
/*     */   public RasterImage getDestinationImage()
/*     */   {
/*  70 */     return this._destinationImage;
/*     */   }
/*     */ 
/*     */   public ColorResolutionCommandMode getMode()
/*     */   {
/*  75 */     return this._mode;
/*     */   }
/*     */ 
/*     */   public void setMode(ColorResolutionCommandMode value) {
/*  79 */     this._mode = value;
/*     */   }
/*     */ 
/*     */   public int getBitsPerPixel()
/*     */   {
/*  84 */     return this._bitsPerPixel;
/*     */   }
/*     */ 
/*     */   public void setBitsPerPixel(int value) {
/*  88 */     this._bitsPerPixel = value;
/*     */   }
/*     */ 
/*     */   public RasterByteOrder getOrder()
/*     */   {
/*  93 */     return this._order;
/*     */   }
/*     */ 
/*     */   public void setOrder(RasterByteOrder value) {
/*  97 */     this._order = value;
/*     */   }
/*     */ 
/*     */   public RasterDitheringMethod getDitheringMethod()
/*     */   {
/* 102 */     return this._ditheringMethod;
/*     */   }
/*     */ 
/*     */   public void setDitheringMethod(RasterDitheringMethod value) {
/* 106 */     this._ditheringMethod = value;
/*     */   }
/*     */ 
/*     */   public int getPaletteFlags()
/*     */   {
/* 111 */     return this._paletteFlags;
/*     */   }
/*     */ 
/*     */   public void setPaletteFlags(int value) {
/* 115 */     this._paletteFlags = value;
/*     */   }
/*     */ 
/*     */   public RasterColor[] getPalette()
/*     */   {
/* 120 */     return this._palette;
/*     */   }
/*     */ 
/*     */   public void setPalette(RasterColor[] value)
/*     */   {
/* 125 */     this._palette = value;
/*     */   }
/*     */ 
/*     */   public int getColors()
/*     */   {
/* 130 */     return this._colors;
/*     */   }
/*     */ 
/*     */   public void setColors(int value) {
/* 134 */     if ((value < 0) || (value > 256))
/* 135 */       throw new IllegalArgumentException("Colors must be a value between 0 and 256");
/* 136 */     this._colors = value;
/*     */   }
/*     */ 
/*     */   public boolean getInitAlpha()
/*     */   {
/* 141 */     return this._initAlpha;
/*     */   }
/*     */ 
/*     */   public void setInitAlpha(boolean value) {
/* 145 */     this._initAlpha = value;
/*     */   }
/*     */ 
/*     */   public int run(RasterImage image)
/*     */   {
/* 151 */     if ((getMode() != ColorResolutionCommandMode.ALL_PAGES) || (image.getPageCount() == 1)) {
/* 152 */       return super.run(image);
/*     */     }
/*     */ 
/* 155 */     this._destinationImage = null;
/*     */ 
/* 157 */     image.disableEvents();
/*     */ 
/* 160 */     int changed = RasterImageChangedFlags.NONE;
/*     */ 
/* 162 */     Object callbackObj = null;
/*     */ 
/* 164 */     if (hasProgress()) {
/* 165 */       callbackObj = ltkrn.setstatuscallback(this);
/*     */     }
/*     */     try
/*     */     {
/* 169 */       int flags = getFlags();
/* 170 */       int colors = this._palette != null ? this._palette.length : getColors();
/*     */ 
/* 172 */       int ret = ltkrn.ColorResBitmapList(image.getBitmapList(), getBitsPerPixel(), flags, this._palette, 0L, colors);
/*     */ 
/* 174 */       image.internalGetCurrentBitmapHandle();
/*     */ 
/* 176 */       if ((ret != L_ERROR.SUCCESS.getValue()) && (ret != L_ERROR.SUCCESS_ABORT.getValue()) && (ret != L_ERROR.ERROR_USER_ABORT.getValue())) {
/* 177 */         RasterException.checkErrorCode(ret);
/*     */       }
/* 179 */       return changed;
/*     */     }
/*     */     finally
/*     */     {
/* 183 */       if (callbackObj != null) {
/* 184 */         ltkrn.setstatuscallback(callbackObj);
/*     */       }
/* 186 */       image.enableEvents();
/*     */ 
/* 188 */       changed = RasterImageChangedFlags.DATA | RasterImageChangedFlags.PALETTE;
/* 189 */       image.onChanged(new RasterImageChangedEvent(image, changed));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*     */   {
/* 198 */     this._destinationImage = null;
/*     */ 
/* 200 */     long srcBitmap = bitmap;
/* 201 */     long destBitmap = ltkrn.AllocBitmapHandle();
/* 202 */     long destBitmapPtr = 0L;
/*     */     try
/*     */     {
/* 205 */       RasterImage destinationImage = null;
/*     */ 
/* 207 */       switch (1.$SwitchMap$leadtools$imageprocessing$ColorResolutionCommandMode[getMode().ordinal()])
/*     */       {
/*     */       case 1:
/*     */       case 2:
/* 211 */         destBitmapPtr = srcBitmap;
/* 212 */         this._dataOwner = image;
/* 213 */         break;
/*     */       case 3:
/* 216 */         destBitmapPtr = destBitmap;
/* 217 */         this._dataOwner = destinationImage;
/* 218 */         break;
/*     */       case 4:
/* 221 */         destBitmapPtr = 0L;
/* 222 */         this._dataOwner = image;
/*     */       }
/*     */ 
/* 226 */       int flags = getFlags();
/* 227 */       int colors = this._palette != null ? this._palette.length : getColors();
/*     */       int ret;
/*     */       try {
/* 231 */         ret = ltkrn.ColorResBitmap(srcBitmap, destBitmapPtr, ltkrn.BITMAPHANDLE_getSizeOf(), getBitsPerPixel(), flags, this._palette, 0L, colors, this._data.size() > 0 ? this : null);
/*     */       }
/*     */       finally
/*     */       {
/*     */       }
/*     */ 
/* 237 */       if (getMode() == ColorResolutionCommandMode.CREATE_DESTINATION_IMAGE) {
/* 238 */         this._destinationImage = RasterImage.createFromBitmapHandle(destBitmap);
/*     */       }
/* 240 */       return ret;
/*     */     }
/*     */     finally
/*     */     {
/* 244 */       ltkrn.FreeBitmapHandle(destBitmap);
/* 245 */       if (getMode() == ColorResolutionCommandMode.IN_PLACE)
/*     */       {
/* 247 */         changedFlags[0] |= RasterImageChangedFlags.DATA | RasterImageChangedFlags.PALETTE;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void addColorResolutionCommandDataListener(ColorResolutionCommandDataListener listener)
/*     */   {
/* 256 */     if (this._data.contains(listener)) {
/* 257 */       return;
/*     */     }
/* 259 */     this._data.addElement(listener);
/*     */   }
/*     */ 
/*     */   public synchronized void removeColorResolutionCommandDataListener(ColorResolutionCommandDataListener listener) {
/* 263 */     if (!this._data.contains(listener)) {
/* 264 */       return;
/*     */     }
/* 266 */     this._data.removeElement(listener);
/*     */   }
/*     */ 
/*     */   private synchronized void fireColorResolutionCommandData(ColorResolutionCommandDataEvent event) {
/* 270 */     for (ColorResolutionCommandDataListener listener : this._data)
/* 271 */       listener.onData(event);
/*     */   }
/*     */ 
/*     */   private int DoCallback(int bitmap, byte[] buffer, int lines)
/*     */   {
/* 278 */     int ret = L_ERROR.SUCCESS.getValue();
/*     */ 
/* 280 */     if (this._data.size() > 0)
/*     */     {
/* 282 */       ColorResolutionCommandDataEvent e = new ColorResolutionCommandDataEvent(this, this._dataOwner, lines, buffer);
/*     */ 
/* 284 */       fireColorResolutionCommandData(e);
/* 285 */       if (e.getCancel()) {
/* 286 */         ret = L_ERROR.ERROR_USER_ABORT.getValue();
/*     */       }
/*     */     }
/* 289 */     return ret;
/*     */   }
/*     */ 
/*     */   private int getFlags()
/*     */   {
/* 294 */     int flags = 0;
/*     */ 
/* 296 */     switch (1.$SwitchMap$leadtools$RasterByteOrder[getOrder().ordinal()])
/*     */     {
/*     */     case 1:
/* 299 */       flags |= 4;
/* 300 */       break;
/*     */     case 2:
/* 302 */       flags |= 0;
/* 303 */       break;
/*     */     case 3:
/* 305 */       flags |= 128;
/* 306 */       break;
/*     */     case 4:
/* 308 */       flags |= 2048;
/* 309 */       break;
/*     */     case 5:
/* 311 */       flags |= 16384;
/*     */     }
/*     */ 
/* 315 */     switch (1.$SwitchMap$leadtools$RasterDitheringMethod[getDitheringMethod().ordinal()])
/*     */     {
/*     */     case 1:
/* 318 */       break;
/*     */     case 2:
/* 320 */       flags |= 65536;
/* 321 */       break;
/*     */     case 3:
/* 323 */       flags |= 131072;
/* 324 */       break;
/*     */     case 4:
/* 326 */       flags |= 196608;
/* 327 */       break;
/*     */     case 5:
/* 329 */       flags |= 262144;
/* 330 */       break;
/*     */     case 6:
/* 332 */       flags |= 327680;
/* 333 */       break;
/*     */     case 7:
/* 335 */       flags |= 393216;
/* 336 */       break;
/*     */     case 8:
/* 338 */       flags |= 458752;
/* 339 */       break;
/*     */     case 9:
/* 341 */       flags |= 524288;
/*     */     }
/*     */ 
/* 345 */     flags |= getPaletteFlags();
/*     */ 
/* 347 */     if (this._initAlpha)
/*     */     {
/* 350 */       flags |= 16777216;
/*     */     }
/*     */ 
/* 353 */     return flags;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.ColorResolutionCommand
 * JD-Core Version:    0.6.2
 */