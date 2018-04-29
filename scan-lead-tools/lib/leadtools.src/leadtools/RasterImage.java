/*      */ package leadtools;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectInputStream.GetField;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.ObjectOutputStream.PutField;
/*      */ import java.io.ObjectStreamField;
/*      */ import java.io.Serializable;
/*      */ import java.util.Vector;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
/*      */ import leadtools.imageprocessing.ChangeViewPerspectiveCommand;
/*      */ import leadtools.imageprocessing.CloneCommand;
/*      */ import leadtools.imageprocessing.ColorResolutionCommand;
/*      */ import leadtools.imageprocessing.ColorResolutionCommandMode;
/*      */ import leadtools.imageprocessing.ColorResolutionCommandPaletteFlags;
/*      */ import leadtools.imageprocessing.CopyRectangleCommand;
/*      */ import leadtools.imageprocessing.SizeCommand;
/*      */ 
/*      */ public class RasterImage
/*      */   implements Serializable
/*      */ {
/*      */   private transient int _ignoreEventsCounter;
/*      */   private transient ReentrantReadWriteLock _readerWriterLock;
/*      */   private transient boolean _isDisposed;
/*      */   private transient boolean _isLoading;
/*      */   private transient long _bitmaps;
/*      */   private transient int _page;
/*      */   private transient int _pageCount;
/*      */   private transient long _currentBitmapHandle;
/*      */   private transient int _animationGlobalLoop;
/*   58 */   private transient LeadSize _animationGlobalSize = new LeadSize(0, 0);
/*      */   private transient RasterColor _animationGlobalBackground;
/*      */   private transient RasterCollection<RasterTagMetadata> _tags;
/*      */   private transient RasterCollection<RasterTagMetadata> _geoKeys;
/*      */   private transient RasterCollection<RasterMarkerMetadata> _markers;
/*      */   private transient RasterCollection<RasterCommentMetadata> _comments;
/*      */   private transient boolean _noFreeDataOnDispose;
/*      */   private Vector<RasterImageChangedListener> _Changed;
/*      */   private Vector<RasterImagePagesChangedListener> _PagesChanged;
/*      */   public static final int MAX_OVERLAYS = 16;
/*      */   private static final long serialVersionUID = 0L;
/* 3194 */   private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("_imageVersion", Integer.TYPE), new ObjectStreamField("_ignoreEventsCounter", Integer.TYPE), new ObjectStreamField("_pageCount", Integer.TYPE), new ObjectStreamField("_page", Integer.TYPE), new ObjectStreamField("_noFreeDataOnDispose", Boolean.TYPE), new ObjectStreamField("_animationGlobalLoop", Integer.TYPE), new ObjectStreamField("_animationGlobalSize", LeadSize.class), new ObjectStreamField("_animationGlobalBackground", Integer.TYPE) };
/*      */   private Vector<AsyncLoadCompletedListener> _LoadCompleted;
/*      */ 
/*      */   private RasterImage()
/*      */   {
/*  104 */     init();
/*      */   }
/*      */ 
/*      */   private void init()
/*      */   {
/*  110 */     this._readerWriterLock = new ReentrantReadWriteLock();
/*      */ 
/*  112 */     this._Changed = new Vector();
/*  113 */     this._PagesChanged = new Vector();
/*  114 */     this._LoadCompleted = new Vector();
/*      */ 
/*  116 */     RasterCollectionEventListener tagsListener = new RasterCollectionEventListener()
/*      */     {
/*      */       public void onRasterCollectionChanged(RasterCollectionEvent<RasterTagMetadata> event) {
/*  119 */         RasterImage.this.onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.TAG));
/*      */       }
/*      */     };
/*  123 */     this._tags = new RasterCollection();
/*  124 */     this._tags.addItemAddedListener(tagsListener);
/*  125 */     this._tags.addItemRemovedListener(tagsListener);
/*      */ 
/*  127 */     RasterCollectionEventListener geoKeysListener = new RasterCollectionEventListener()
/*      */     {
/*      */       public void onRasterCollectionChanged(RasterCollectionEvent<RasterTagMetadata> event) {
/*  130 */         RasterImage.this.onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.GEOKEY));
/*      */       }
/*      */     };
/*  133 */     this._geoKeys = new RasterCollection();
/*  134 */     this._geoKeys.addItemAddedListener(geoKeysListener);
/*  135 */     this._geoKeys.addItemRemovedListener(geoKeysListener);
/*      */ 
/*  137 */     RasterCollectionEventListener commentsListener = new RasterCollectionEventListener()
/*      */     {
/*      */       public void onRasterCollectionChanged(RasterCollectionEvent<RasterCommentMetadata> event) {
/*  140 */         RasterImage.this.onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.COMMENT));
/*      */       }
/*      */     };
/*  143 */     this._comments = new RasterCollection();
/*  144 */     this._comments.addItemAddedListener(commentsListener);
/*  145 */     this._comments.addItemRemovedListener(commentsListener);
/*      */ 
/*  147 */     RasterCollectionEventListener markersListener = new RasterCollectionEventListener()
/*      */     {
/*      */       public void onRasterCollectionChanged(RasterCollectionEvent<RasterMarkerMetadata> event) {
/*  150 */         RasterImage.this.onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.MARKER));
/*      */       }
/*      */     };
/*  153 */     this._markers = new RasterCollection();
/*  154 */     this._markers.addItemAddedListener(markersListener);
/*  155 */     this._markers.addItemRemovedListener(markersListener);
/*      */   }
/*      */ 
/*      */   private final void initialize(long bitmapHandle)
/*      */   {
/*  160 */     if ((bitmapHandle == 0L) || (!ltkrn.BITMAPHANDLE_getFlagsAllocated(bitmapHandle))) {
/*  161 */       RasterException.checkErrorCode(L_ERROR.ERROR_NO_BITMAP.getValue());
/*      */     }
/*      */ 
/*  164 */     this._animationGlobalLoop = -1;
/*  165 */     this._animationGlobalSize = LeadSize.getEmpty();
/*  166 */     this._animationGlobalBackground = new RasterColor(0, 255, 0);
/*      */ 
/*  169 */     long hlist = ltkrn.CreateBitmapList();
/*  170 */     if (hlist == 0L) {
/*  171 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     }
/*  173 */     if (this._bitmaps != 0L) {
/*  174 */       ltkrn.DestroyBitmapList(this._bitmaps);
/*      */     }
/*  176 */     this._bitmaps = hlist;
/*      */ 
/*  179 */     int ret = ltkrn.InsertBitmapListItem(this._bitmaps, -1, bitmapHandle);
/*  180 */     RasterException.checkErrorCode(ret);
/*      */ 
/*  182 */     this._currentBitmapHandle = ltkrn.AllocBitmapHandle();
/*  183 */     if (this._currentBitmapHandle == 0L)
/*      */     {
/*  185 */       ltkrn.DestroyBitmapList(this._bitmaps);
/*  186 */       this._bitmaps = 0L;
/*  187 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     }
/*      */ 
/*  190 */     ret = ltkrn.GetBitmapListItem(this._bitmaps, 0, this._currentBitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf());
/*  191 */     RasterException.checkErrorCode(ret);
/*      */ 
/*  193 */     this._page = 1;
/*  194 */     this._pageCount = 1;
/*      */   }
/*      */ 
/*      */   public static RasterImage create(int width, int height, int bitsPerPixel, int resolution, RasterColor backgroundColor)
/*      */   {
/*  200 */     return create(false, width, height, bitsPerPixel, resolution, backgroundColor.clone());
/*      */   }
/*      */ 
/*      */   public static RasterImage createGrayscale(int width, int height, int bitsPerPixel, int resolution)
/*      */   {
/*  206 */     switch (bitsPerPixel)
/*      */     {
/*      */     case 8:
/*      */     case 12:
/*      */     case 16:
/*      */     case 32:
/*  212 */       break;
/*      */     default:
/*  215 */       throw new ArgumentOutOfRangeException("bitsPerPixel", bitsPerPixel, "Unsupported grayscale bits/pixel value. You must specify 8, 12, 16 or 32.");
/*      */     }
/*      */ 
/*  218 */     return create(true, width, height, bitsPerPixel, resolution, RasterColor.fromArgb(0));
/*      */   }
/*      */ 
/*      */   private static RasterImage create(boolean grayscale, int width, int height, int bitsPerPixel, int resolution, RasterColor backgroundColor)
/*      */   {
/*  223 */     if (width < 1)
/*  224 */       throw new ArgumentOutOfRangeException("width", width, "Must be a value greater than 0");
/*  225 */     if (height < 1)
/*  226 */       throw new ArgumentOutOfRangeException("height", height, "Must be a value greater than 0");
/*  227 */     if (resolution < 0)
/*  228 */       throw new ArgumentOutOfRangeException("resolution", resolution, "Must be a value greater than or equal to 0");
/*      */     RasterByteOrder order;
/*      */     RasterByteOrder order;
/*  232 */     switch (bitsPerPixel)
/*      */     {
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*  241 */       order = RasterByteOrder.RGB;
/*  242 */       break;
/*      */     case 8:
/*  245 */       order = grayscale ? RasterByteOrder.GRAY : RasterByteOrder.RGB;
/*  246 */       break;
/*      */     case 12:
/*  249 */       if (grayscale)
/*      */       {
/*  251 */         order = RasterByteOrder.GRAY;
/*      */       }
/*      */       else
/*      */       {
/*  256 */         throw new ArgumentOutOfRangeException("bitsPerPixel", bitsPerPixel, "12 bits/pixel is unsupported by this method. Use RasterImage.CreateGrayscale");
/*      */       }
/*      */ 
/*      */       break;
/*      */     case 16:
/*  261 */       order = grayscale ? RasterByteOrder.GRAY : RasterByteOrder.BGR;
/*  262 */       break;
/*      */     case 24:
/*      */     case 48:
/*      */     case 64:
/*  267 */       order = RasterByteOrder.BGR;
/*  268 */       break;
/*      */     case 32:
/*  271 */       order = grayscale ? RasterByteOrder.GRAY : RasterByteOrder.BGR;
/*  272 */       break;
/*      */     default:
/*  275 */       throw new ArgumentOutOfRangeException("bitsPerPixel", bitsPerPixel, "Unsupported bits/pixel value");
/*      */     }
/*      */ 
/*  278 */     if (resolution == 0) {
/*  279 */       resolution = Math.max(ltkrn.SCREEN_DPIX, ltkrn.SCREEN_DPIY);
/*      */     }
/*  281 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/*  282 */     if (bitmapHandle == 0L) {
/*  283 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     }
/*  285 */     int ret = ltkrn.CreateBitmap(bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), 1, width, height, bitsPerPixel, order.getValue(), null, 1, null, 0L);
/*  286 */     if (ret == L_ERROR.SUCCESS.getValue())
/*      */     {
/*  289 */       ltkrn.BITMAPHANDLE_setXResolution(bitmapHandle, resolution);
/*  290 */       ltkrn.BITMAPHANDLE_setYResolution(bitmapHandle, resolution);
/*      */ 
/*  293 */       if ((!grayscale) && ((bitsPerPixel == 32) || (bitsPerPixel == 64)))
/*      */       {
/*  295 */         short alpha = (short)(bitsPerPixel == 32 ? 255 : 65535);
/*  296 */         ret = ltkrn.SetBitmapAlphaValues(bitmapHandle, alpha);
/*      */       }
/*      */ 
/*  300 */       if ((ret == L_ERROR.SUCCESS.getValue()) && (!grayscale) && ((backgroundColor.getA() != 255) || (backgroundColor.getR() != 0) || (backgroundColor.getG() != 0) || (backgroundColor.getB() != 0)))
/*      */       {
/*  302 */         ret = ltkrn.FillBitmap(bitmapHandle, backgroundColor.getColorRef());
/*      */       }
/*      */     }
/*      */ 
/*  306 */     if (ret == L_ERROR.SUCCESS.getValue())
/*      */     {
/*  308 */       RasterImage image = createFromBitmapHandle(bitmapHandle, false);
/*  309 */       ltkrn.FreeBitmapHandle(bitmapHandle);
/*  310 */       return image;
/*      */     }
/*      */ 
/*  314 */     if (ltkrn.BITMAPHANDLE_getFlagsAllocated(bitmapHandle))
/*      */     {
/*  316 */       ltkrn.FreeBitmap(bitmapHandle);
/*      */     }
/*      */ 
/*  319 */     ltkrn.FreeBitmapHandle(bitmapHandle);
/*  320 */     RasterException.checkErrorCode(ret);
/*  321 */     return null;
/*      */   }
/*      */ 
/*      */   public RasterImage(int flags, int width, int height, int bitsPerPixel, RasterByteOrder order, RasterViewPerspective viewPerspective, RasterColor[] palette, byte[] userData, int userDataLength)
/*      */   {
/*  334 */     init();
/*      */ 
/*  337 */     if (((flags & RasterMemoryFlags.USER.getValue()) == RasterMemoryFlags.USER.getValue()) && (userData != null) && (userDataLength > 0))
/*      */     {
/*  339 */       int flagsValue = flags;
/*  340 */       flagsValue &= (RasterMemoryFlags.USER.getValue() ^ 0xFFFFFFFF);
/*  341 */       flagsValue |= RasterMemoryFlags.CONVENTIONAL.getValue();
/*  342 */       flags = flagsValue;
/*      */     }
/*      */ 
/*  345 */     create(flags, width, height, bitsPerPixel, order, viewPerspective, palette, null, 0L);
/*      */ 
/*  348 */     if ((userData != null) && (userDataLength > 0))
/*      */     {
/*  350 */       access();
/*      */       try
/*      */       {
/*  353 */         int bytesPerLine = ltkrn.BITMAPHANDLE_getBytesPerLine(this._currentBitmapHandle);
/*  354 */         int bitmapHeight = ltkrn.BITMAPHANDLE_getHeight(this._currentBitmapHandle);
/*  355 */         if (userDataLength > bytesPerLine * bitmapHeight) {
/*  356 */           userDataLength = bytesPerLine * bitmapHeight;
/*      */         }
/*  358 */         ltkrn.PutBitmapRow(this._currentBitmapHandle, userData, 0, 0, userDataLength);
/*      */       }
/*      */       finally
/*      */       {
/*  362 */         release();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void create(int flags, int width, int height, int bitsPerPixel, RasterByteOrder order, RasterViewPerspective viewPerspective, RasterColor[] palette, byte[] userData, long userDataLength)
/*      */   {
/*  369 */     this._readerWriterLock = new ReentrantReadWriteLock();
/*      */ 
/*  371 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/*  372 */     if (bitmapHandle == 0L) {
/*  373 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     }
/*      */     try
/*      */     {
/*  377 */       int ret = ltkrn.CreateBitmap(bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), flags, width, height, bitsPerPixel, order.getValue(), palette, viewPerspective.getValue(), userData, userDataLength);
/*  378 */       RasterException.checkErrorCode(ret);
/*  379 */       initialize(bitmapHandle);
/*      */     }
/*      */     finally
/*      */     {
/*  383 */       ltkrn.FreeBitmapHandle(bitmapHandle);
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterImage(RasterImage srcImage)
/*      */   {
/*  389 */     if (srcImage == null) {
/*  390 */       throw new ArgumentNullException("srcImage");
/*      */     }
/*  392 */     init();
/*      */ 
/*  395 */     int ret = L_ERROR.SUCCESS.getValue();
/*  396 */     int count = srcImage.getPageCount();
/*  397 */     long srcBitmap = ltkrn.AllocBitmapHandle();
/*  398 */     if (srcBitmap == 0L) {
/*  399 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     }
/*  401 */     long destBitmap = ltkrn.AllocBitmapHandle();
/*  402 */     if (destBitmap == 0L)
/*      */     {
/*  404 */       ltkrn.FreeBitmapHandle(srcBitmap);
/*  405 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     }
/*  407 */     int bitmapHandleSize = ltkrn.BITMAPHANDLE_getSizeOf();
/*      */     try
/*      */     {
/*  411 */       for (int i = 0; (i < count) && (ret == L_ERROR.SUCCESS.getValue()); i++)
/*      */       {
/*  413 */         srcImage.getBitmapHandle(srcBitmap, i + 1);
/*      */ 
/*  415 */         ret = ltkrn.CopyBitmap(destBitmap, srcBitmap, bitmapHandleSize);
/*  416 */         if (ret == L_ERROR.SUCCESS.getValue())
/*      */         {
/*  418 */           if (i == 0) {
/*  419 */             initialize(destBitmap);
/*      */           }
/*      */           else {
/*  422 */             ret = ltkrn.InsertBitmapListItem(this._bitmaps, -1, destBitmap);
/*  423 */             if (ret != L_ERROR.SUCCESS.getValue())
/*  424 */               ltkrn.FreeBitmap(destBitmap);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  431 */       ltkrn.FreeBitmapHandle(srcBitmap);
/*  432 */       ltkrn.FreeBitmapHandle(destBitmap);
/*      */     }
/*      */ 
/*  435 */     RasterException.checkErrorCode(ret);
/*  436 */     updatePages(srcImage.getPage());
/*      */ 
/*  439 */     srcImage.copyMetadataTo(this, RasterMetadataFlags.ALL.getValue());
/*  440 */     this._animationGlobalLoop = srcImage._animationGlobalLoop;
/*  441 */     this._animationGlobalSize = srcImage._animationGlobalSize;
/*  442 */     this._animationGlobalBackground = srcImage._animationGlobalBackground.clone();
/*      */   }
/*      */ 
/*      */   public static RasterImage createFromBitmapHandle(long bitmapHandle, boolean noFreeOnDispose)
/*      */   {
/*  447 */     if (bitmapHandle == 0L) {
/*  448 */       throw new ArgumentNullException("bitmapHandle");
/*      */     }
/*  450 */     RasterImage image = new RasterImage();
/*  451 */     image.initialize(bitmapHandle);
/*  452 */     image.setNoFreeDataOnDispose(noFreeOnDispose);
/*  453 */     return image;
/*      */   }
/*      */ 
/*      */   public static RasterImage createFromBitmapHandle(long bitmapHandle)
/*      */   {
/*  458 */     return createFromBitmapHandle(bitmapHandle, false);
/*      */   }
/*      */ 
/*      */   public void initFromBitmapHandle(long bitmapHandle, boolean noFreeOnDispose)
/*      */   {
/*  463 */     initialize(bitmapHandle);
/*  464 */     setNoFreeDataOnDispose(noFreeOnDispose);
/*      */   }
/*      */ 
/*      */   public void initFromBitmapHandle(long bitmapHandle) {
/*  468 */     initFromBitmapHandle(bitmapHandle, false);
/*      */   }
/*      */ 
/*      */   public static RasterImage createFromBitmapList(long bitmapList, boolean noFreeOnDispose)
/*      */   {
/*  473 */     if (bitmapList == 0L) {
/*  474 */       throw new ArgumentNullException("bitmapList");
/*      */     }
/*  476 */     int[] params = new int[1];
/*  477 */     int ret = ltkrn.GetBitmapListCount(bitmapList, params);
/*  478 */     RasterException.checkErrorCode(ret);
/*      */ 
/*  480 */     int count = params[0];
/*  481 */     RasterImage image = null;
/*      */ 
/*  483 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/*  484 */     int bitmapHandleSize = ltkrn.BITMAPHANDLE_getSizeOf();
/*  485 */     RasterException.checkErrorCode(ret);
/*      */     try
/*      */     {
/*  489 */       for (int i = 0; i < count; i++)
/*      */       {
/*  491 */         ret = ltkrn.GetBitmapListItem(bitmapList, i, bitmapHandle, bitmapHandleSize);
/*  492 */         RasterException.checkErrorCode(ret);
/*      */ 
/*  494 */         RasterImage tempImage = createFromBitmapHandle(bitmapHandle, noFreeOnDispose);
/*  495 */         if (image == null)
/*  496 */           image = tempImage;
/*      */         else
/*  498 */           image.addPage(tempImage);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  503 */       ltkrn.FreeBitmapHandle(bitmapHandle);
/*      */     }
/*      */ 
/*  506 */     return image;
/*      */   }
/*      */ 
/*      */   public static RasterImage createFromBitmapList(long bitmapList)
/*      */   {
/*  511 */     return createFromBitmapList(bitmapList, false);
/*      */   }
/*      */ 
/*      */   protected void finalize()
/*      */   {
/*      */     try
/*      */     {
/*  518 */       dispose();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void dispose()
/*      */   {
/*  527 */     if (this._isDisposed) {
/*  528 */       return;
/*      */     }
/*  530 */     this._isDisposed = true;
/*      */ 
/*  533 */     if (this._bitmaps != 0L)
/*      */     {
/*  535 */       int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/*  540 */       if (getNoFreeDataOnDispose())
/*      */       {
/*  542 */         int count = getPageCount();
/*  543 */         int oldCount = count;
/*  544 */         while ((count > 0) && (ret == L_ERROR.SUCCESS.getValue()))
/*      */         {
/*  546 */           ret = ltkrn.RemoveBitmapListItem(this._bitmaps, 0, null);
/*  547 */           count--;
/*      */         }
/*      */ 
/*  550 */         updatePagesChanged(RasterImagePagesChangedAction.REMOVED, 1, oldCount);
/*      */       }
/*      */ 
/*  553 */       if (ret == L_ERROR.SUCCESS.getValue())
/*      */       {
/*  555 */         ret = ltkrn.DestroyBitmapList(this._bitmaps);
/*  556 */         if (ret == L_ERROR.SUCCESS.getValue()) {
/*  557 */           this._bitmaps = 0L;
/*      */         }
/*      */       }
/*  560 */       if (this._currentBitmapHandle != 0L)
/*      */       {
/*  562 */         ltkrn.FreeBitmapHandle(this._currentBitmapHandle);
/*  563 */         this._currentBitmapHandle = 0L;
/*      */       }
/*      */ 
/*  566 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */ 
/*  570 */     this._isDisposed = true;
/*  571 */     this._page = 0;
/*  572 */     this._pageCount = 0;
/*      */   }
/*      */ 
/*      */   public long getBitmapList()
/*      */   {
/*  577 */     return this._bitmaps;
/*      */   }
/*      */ 
/*      */   public long getCurrentBitmapHandle()
/*      */   {
/*  582 */     return this._currentBitmapHandle;
/*      */   }
/*      */ 
/*      */   public void updateCurrentBitmapHandle()
/*      */   {
/*  587 */     ltkrn.SetBitmapListItem(this._bitmaps, this._page - 1, this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void internalGetCurrentBitmapHandle()
/*      */   {
/*  597 */     if (this._page > 0)
/*      */     {
/*  599 */       int ret = ltkrn.GetBitmapListItem(this._bitmaps, this._page - 1, this._currentBitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf());
/*  600 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     else
/*      */     {
/*  607 */       ltkrn.FreeBitmapHandle(this._currentBitmapHandle);
/*  608 */       this._currentBitmapHandle = 0L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getBitmapHandleSize()
/*      */   {
/*  614 */     return ltkrn.BITMAPHANDLE_getSizeOf();
/*      */   }
/*      */ 
/*      */   public void getBitmapHandle(long bitmapHandle, int index)
/*      */   {
/*  619 */     int ret = ltkrn.GetBitmapListItem(this._bitmaps, index - 1, bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf());
/*  620 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   public void updateBitmapHandle(long bitmapHandle, int index)
/*      */   {
/*  625 */     int ret = ltkrn.SetBitmapListItem(this._bitmaps, index - 1, bitmapHandle);
/*  626 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   public boolean getNoFreeDataOnDispose()
/*      */   {
/*  632 */     return this._noFreeDataOnDispose;
/*      */   }
/*      */ 
/*      */   public void setNoFreeDataOnDispose(boolean value) {
/*  636 */     this._noFreeDataOnDispose = value;
/*      */   }
/*      */ 
/*      */   public synchronized void addChangedListener(RasterImageChangedListener listener)
/*      */   {
/*  647 */     if (this._Changed.contains(listener)) {
/*  648 */       return;
/*      */     }
/*  650 */     this._Changed.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeChangedListener(RasterImageChangedListener listener) {
/*  654 */     if (!this._Changed.contains(listener)) {
/*  655 */       return;
/*      */     }
/*  657 */     this._Changed.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireChanged(RasterImageChangedEvent event) {
/*  661 */     for (RasterImageChangedListener listener : this._Changed)
/*  662 */       listener.onImageChangedAlert(event);
/*      */   }
/*      */ 
/*      */   public void onChanged(RasterImageChangedEvent event)
/*      */   {
/*  667 */     if (getIgnoreEventsCounter() == 0)
/*      */     {
/*  669 */       fireChanged(event);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void addPageChangedListener(RasterImagePagesChangedListener listener)
/*      */   {
/*  680 */     if (this._PagesChanged.contains(listener)) {
/*  681 */       return;
/*      */     }
/*  683 */     this._PagesChanged.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removePageChangedListener(RasterImagePagesChangedListener listener) {
/*  687 */     if (!this._PagesChanged.contains(listener)) {
/*  688 */       return;
/*      */     }
/*  690 */     this._PagesChanged.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void firePageChanged(RasterImagePagesChangedEvent event) {
/*  694 */     for (RasterImagePagesChangedListener listener : this._PagesChanged)
/*  695 */       listener.onImagePagesChanged(event);
/*      */   }
/*      */ 
/*      */   private void updatePagesChanged(RasterImagePagesChangedAction action, int index, int count)
/*      */   {
/*  700 */     if (getIgnoreEventsCounter() == 0)
/*      */     {
/*  702 */       RasterImagePagesChangedEvent e = new RasterImagePagesChangedEvent(this, action, index, count);
/*      */ 
/*  704 */       firePageChanged(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void disableEvents()
/*      */   {
/*  710 */     this._readerWriterLock.writeLock().lock();
/*      */     try
/*      */     {
/*  713 */       this._ignoreEventsCounter += 1;
/*      */     }
/*      */     finally
/*      */     {
/*  717 */       this._readerWriterLock.writeLock().unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void enableEvents() {
/*  722 */     boolean error = false;
/*      */ 
/*  724 */     this._readerWriterLock.writeLock().lock();
/*      */     try
/*      */     {
/*  727 */       if (this._ignoreEventsCounter <= 0)
/*  728 */         error = true;
/*      */       else
/*  730 */         this._ignoreEventsCounter -= 1;
/*      */     }
/*      */     finally
/*      */     {
/*  734 */       this._readerWriterLock.writeLock().unlock();
/*      */     }
/*      */ 
/*  737 */     if (error)
/*  738 */       throw new InvalidOperationException("EnableEvents is called without a pairing DisableEvents");
/*      */   }
/*      */ 
/*      */   public RasterImage clone()
/*      */   {
/*  743 */     CloneCommand command = new CloneCommand();
/*  744 */     command.run(this);
/*  745 */     return command.getDestinationImage();
/*      */   }
/*      */ 
/*      */   public RasterImage cloneAll() {
/*  749 */     int page = this._page;
/*  750 */     CloneCommand command = new CloneCommand();
/*      */ 
/*  752 */     RasterImage destImage = null;
/*      */     try
/*      */     {
/*  756 */       for (int i = 0; i < getPageCount(); i++)
/*      */       {
/*  758 */         this._page = (i + 1);
/*  759 */         internalGetCurrentBitmapHandle();
/*  760 */         command.run(this);
/*  761 */         if (destImage == null)
/*  762 */           destImage = command.getDestinationImage();
/*      */         else {
/*  764 */           destImage.addPage(command.getDestinationImage());
/*      */         }
/*  766 */         copyMetadataTo(destImage, RasterMetadataFlags.ALL.getValue());
/*      */       }
/*      */ 
/*  769 */       return destImage;
/*      */     }
/*      */     finally
/*      */     {
/*  773 */       this._page = page;
/*  774 */       internalGetCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterImage clone(LeadRect rc) {
/*  779 */     CopyRectangleCommand command = new CopyRectangleCommand(rc, RasterMemoryFlags.NONE.getValue());
/*  780 */     command.run(this);
/*  781 */     return command.getDestinationImage();
/*      */   }
/*      */ 
/*      */   public int getPageCount()
/*      */   {
/*  786 */     return this._pageCount;
/*      */   }
/*      */ 
/*      */   public int getPage()
/*      */   {
/*  791 */     return this._page;
/*      */   }
/*      */ 
/*      */   public void setPage(int value)
/*      */   {
/*  796 */     if ((this._bitmaps == 0L) || (getPageCount() < 1)) {
/*  797 */       throw new InvalidOperationException("No pages in this image");
/*      */     }
/*  799 */     if ((value < 1) || (value > getPageCount())) {
/*  800 */       throw new ArgumentOutOfRangeException("Page", value, String.format("Page number must be between %1$s and %2$s", new Object[] { Integer.valueOf(1), Integer.valueOf(getPageCount() - 1) }));
/*      */     }
/*  802 */     this._page = value;
/*      */ 
/*  805 */     internalGetCurrentBitmapHandle();
/*  806 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.PAGE));
/*      */   }
/*      */ 
/*      */   private int updatePages(int page)
/*      */   {
/*  813 */     int[] params = new int[1];
/*  814 */     int ret = ltkrn.GetBitmapListCount(this._bitmaps, params);
/*  815 */     RasterException.checkErrorCode(ret);
/*  816 */     int count = params[0];
/*      */ 
/*  818 */     int flags = RasterImageChangedFlags.NONE;
/*      */ 
/*  820 */     if (this._pageCount != count)
/*      */     {
/*  822 */       flags |= RasterImageChangedFlags.PAGE_COUNT;
/*  823 */       this._pageCount = count;
/*      */     }
/*      */ 
/*  826 */     if ((this._page < 1) || (this._page > this._pageCount) || (page < 1))
/*      */     {
/*  828 */       flags |= RasterImageChangedFlags.PAGE;
/*  829 */       this._page = Math.min(this._page, this._pageCount);
/*  830 */       internalGetCurrentBitmapHandle();
/*      */     }
/*      */ 
/*  833 */     if (page == this._page)
/*      */     {
/*  835 */       flags |= RasterImageChangedFlags.PAGE;
/*  836 */       internalGetCurrentBitmapHandle();
/*      */     }
/*      */     else {
/*  839 */       internalGetCurrentBitmapHandle();
/*      */     }
/*  841 */     return flags;
/*      */   }
/*      */ 
/*      */   private static void swapPage(boolean replace, RasterImage srcImage, RasterImage destImage, int srcPage, int destPage, int[] srcFlags, int[] destFlags) {
/*  845 */     if (srcPage == -1)
/*  846 */       srcPage = srcImage.getPageCount();
/*  847 */     if (destPage == -1) {
/*  848 */       destPage = destImage.getPageCount();
/*      */     }
/*      */ 
/*  851 */     long[] params = new long[2];
/*      */ 
/*  853 */     int ret = ltkrn.RemoveBitmapListItem(srcImage._bitmaps, srcPage - 1, params);
/*  854 */     RasterException.checkErrorCode(ret);
/*      */ 
/*  857 */     srcFlags[0] = srcImage.updatePages(srcPage);
/*      */ 
/*  860 */     if (replace)
/*  861 */       ret = ltkrn.ReplaceBitmapListItem(destImage._bitmaps, destPage, params[0], 0L, (int)params[1]);
/*      */     else {
/*  863 */       ret = ltkrn.InsertBitmapListItem(destImage._bitmaps, destPage, params[0]);
/*      */     }
/*  865 */     ltkrn.FreeBitmapHandle(params[0]);
/*      */ 
/*  867 */     RasterException.checkErrorCode(ret);
/*      */ 
/*  870 */     destFlags[0] = destImage.updatePages(destPage);
/*      */   }
/*      */ 
/*      */   public int addPage(RasterImage image) {
/*  874 */     if (image == null) {
/*  875 */       throw new ArgumentNullException("image");
/*      */     }
/*  877 */     int addIndex = getPageCount();
/*  878 */     int addCount = 1;
/*  879 */     int removeIndex = image.getPage();
/*  880 */     int removeCount = 1;
/*      */ 
/*  882 */     int[] srcFlags = { RasterImageChangedFlags.NONE };
/*  883 */     int[] destFlags = { RasterImageChangedFlags.NONE };
/*  884 */     swapPage(false, image, this, image.getPage(), -1, srcFlags, destFlags);
/*      */ 
/*  887 */     if (image.getPageCount() == 0)
/*      */     {
/*  891 */       image.dispose();
/*      */     }
/*      */     else
/*      */     {
/*  895 */       image.onChanged(new RasterImageChangedEvent(image, srcFlags[0]));
/*  896 */       image.updatePagesChanged(RasterImagePagesChangedAction.REMOVED, removeIndex, removeCount);
/*      */     }
/*      */ 
/*  900 */     onChanged(new RasterImageChangedEvent(this, destFlags[0]));
/*  901 */     updatePagesChanged(RasterImagePagesChangedAction.ADDED, addIndex, addCount);
/*      */ 
/*  904 */     return getPageCount() + 1;
/*      */   }
/*      */ 
/*      */   public void addPages(RasterImage image, int startIndex, int count) {
/*  908 */     if (image == null) {
/*  909 */       throw new ArgumentNullException("image");
/*      */     }
/*  911 */     int[] srcFlags = { RasterImageChangedFlags.NONE };
/*  912 */     int[] destFlags = { RasterImageChangedFlags.NONE };
/*      */ 
/*  916 */     image.disableEvents();
/*  917 */     disableEvents();
/*      */ 
/*  919 */     if (count == -1) {
/*  920 */       count = image.getPageCount() - startIndex + 1;
/*      */     }
/*  922 */     int addIndex = getPageCount();
/*  923 */     int addCount = count;
/*  924 */     int removeIndex = startIndex;
/*  925 */     int removeCount = count;
/*      */     try
/*      */     {
/*  929 */       for (int i = startIndex; i < startIndex + count; i++) {
/*  930 */         swapPage(false, image, this, startIndex, -1, srcFlags, destFlags);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  935 */       enableEvents();
/*  936 */       image.enableEvents();
/*      */ 
/*  939 */       if (image.getPageCount() == 0)
/*      */       {
/*  943 */         image.dispose();
/*      */       }
/*      */       else
/*      */       {
/*  947 */         image.onChanged(new RasterImageChangedEvent(image, srcFlags[0]));
/*  948 */         image.updatePagesChanged(RasterImagePagesChangedAction.REMOVED, removeIndex, removeCount);
/*      */       }
/*      */ 
/*  952 */       onChanged(new RasterImageChangedEvent(this, destFlags[0]));
/*  953 */       updatePagesChanged(RasterImagePagesChangedAction.ADDED, addIndex, addCount);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int removePage(int pageIndex)
/*      */   {
/*  959 */     if (getPageCount() <= 1) {
/*  960 */       throw new InvalidOperationException("Cannot remove last page from the image");
/*      */     }
/*      */ 
/*  963 */     long[] params = new long[2];
/*  964 */     int ret = ltkrn.RemoveBitmapListItem(this._bitmaps, pageIndex - 1, params);
/*  965 */     if (ret == L_ERROR.SUCCESS.getValue())
/*  966 */       ltkrn.FreeBitmap(params[0]);
/*  967 */     ltkrn.FreeBitmapHandle(params[0]);
/*  968 */     RasterException.checkErrorCode(ret);
/*      */ 
/*  971 */     int flags = updatePages(pageIndex);
/*      */ 
/*  975 */     return flags;
/*      */   }
/*      */ 
/*      */   public void removePageAt(int pageIndex) {
/*  979 */     int flags = removePage(pageIndex);
/*      */ 
/*  981 */     onChanged(new RasterImageChangedEvent(this, flags));
/*  982 */     updatePagesChanged(RasterImagePagesChangedAction.REMOVED, pageIndex, 1);
/*      */   }
/*      */ 
/*      */   public void removePages(int startIndex, int count) {
/*  986 */     if (getPageCount() <= 1) {
/*  987 */       throw new InvalidOperationException("Cannot remove last page from the image");
/*      */     }
/*  989 */     disableEvents();
/*  990 */     int flags = RasterImageChangedFlags.NONE;
/*      */ 
/*  992 */     if (count == -1) {
/*  993 */       count = getPageCount() - startIndex + 1;
/*      */     }
/*  995 */     int oldCount = count;
/*      */     try
/*      */     {
/*  999 */       while (count > 0)
/*      */       {
/* 1001 */         flags |= removePage(startIndex);
/* 1002 */         count--;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1007 */       enableEvents();
/* 1008 */       onChanged(new RasterImageChangedEvent(this, flags));
/* 1009 */       updatePagesChanged(RasterImagePagesChangedAction.REMOVED, startIndex, oldCount);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeAllPages()
/*      */   {
/* 1016 */     if (getPageCount() <= 1) {
/* 1017 */       throw new InvalidOperationException("Cannot remove last page from the image");
/*      */     }
/* 1019 */     disableEvents();
/* 1020 */     int flags = RasterImageChangedFlags.NONE;
/*      */ 
/* 1022 */     int oldPageCount = this._pageCount;
/*      */     try
/*      */     {
/* 1026 */       for (int i = 1; i < this._page; i++) {
/* 1027 */         flags |= removePage(1);
/*      */       }
/* 1029 */       int count = this._pageCount;
/*      */ 
/* 1031 */       for (int i = this._page + 1; i <= count; i++)
/* 1032 */         flags |= removePage(this._page + 1);
/*      */     }
/*      */     finally
/*      */     {
/* 1036 */       enableEvents();
/* 1037 */       onChanged(new RasterImageChangedEvent(this, flags));
/* 1038 */       updatePagesChanged(RasterImagePagesChangedAction.REMOVED, 1, oldPageCount);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int insertPage(int index, RasterImage image) {
/* 1043 */     if (image == null) {
/* 1044 */       throw new ArgumentNullException("image");
/*      */     }
/*      */ 
/* 1047 */     if ((index == -1) || (index == getPageCount() + 1)) {
/* 1048 */       return addPage(image);
/*      */     }
/* 1050 */     if (index > getPageCount()) {
/* 1051 */       throw new ArgumentOutOfRangeException("index", index, String.format("Page number must be less than or equal to %1$s", new Object[] { Integer.valueOf(getPageCount()) }));
/*      */     }
/* 1053 */     int[] srcFlags = { RasterImageChangedFlags.NONE };
/* 1054 */     int[] destFlags = { RasterImageChangedFlags.NONE };
/*      */ 
/* 1056 */     int addIndex = index;
/* 1057 */     int addCount = 1;
/* 1058 */     int removeIndex = image.getPage();
/* 1059 */     int removeCount = 1;
/*      */ 
/* 1061 */     swapPage(false, image, this, image.getPage(), index, srcFlags, destFlags);
/*      */ 
/* 1064 */     if (image.getPageCount() == 0)
/*      */     {
/* 1067 */       image.dispose();
/*      */     }
/*      */     else
/*      */     {
/* 1071 */       image.onChanged(new RasterImageChangedEvent(image, srcFlags[0]));
/* 1072 */       image.updatePagesChanged(RasterImagePagesChangedAction.REMOVED, removeIndex, removeCount);
/*      */     }
/*      */ 
/* 1076 */     onChanged(new RasterImageChangedEvent(this, destFlags[0]));
/* 1077 */     updatePagesChanged(RasterImagePagesChangedAction.INSERTED, addIndex, addCount);
/*      */ 
/* 1080 */     return index;
/*      */   }
/*      */ 
/*      */   public void insertPages(int index, RasterImage image, int startIndex, int count) {
/* 1084 */     if (image == null) {
/* 1085 */       throw new ArgumentNullException("image");
/*      */     }
/* 1087 */     if (count == 0) {
/* 1088 */       return;
/*      */     }
/* 1090 */     if (count == -1) {
/* 1091 */       count = image.getPageCount() - startIndex + 1;
/*      */     }
/* 1093 */     if (count == 1)
/*      */     {
/* 1095 */       if (startIndex != 0) {
/* 1096 */         throw new ArgumentOutOfRangeException("startIndex", startIndex, "Invalid page index");
/*      */       }
/* 1098 */       insertPage(index, image);
/* 1099 */       return;
/*      */     }
/*      */ 
/* 1102 */     int[] srcFlags = { RasterImageChangedFlags.NONE };
/* 1103 */     int[] destFlags = { RasterImageChangedFlags.NONE };
/*      */ 
/* 1107 */     image.disableEvents();
/* 1108 */     disableEvents();
/*      */ 
/* 1110 */     int addIndex = index;
/* 1111 */     int addCount = count;
/* 1112 */     int removeIndex = startIndex;
/* 1113 */     int removeCount = count;
/*      */     try
/*      */     {
/* 1117 */       int destIndex = index;
/* 1118 */       for (int i = startIndex; i < startIndex + count; i++)
/*      */       {
/* 1120 */         swapPage(false, image, this, startIndex, destIndex, srcFlags, destFlags);
/* 1121 */         destIndex++;
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 1127 */       enableEvents();
/* 1128 */       image.enableEvents();
/*      */ 
/* 1131 */       if (image.getPageCount() == 0)
/*      */       {
/* 1135 */         image.dispose();
/*      */       }
/*      */       else
/*      */       {
/* 1139 */         image.onChanged(new RasterImageChangedEvent(image, srcFlags[0]));
/* 1140 */         image.updatePagesChanged(RasterImagePagesChangedAction.REMOVED, removeIndex, removeCount);
/*      */       }
/*      */ 
/* 1144 */       onChanged(new RasterImageChangedEvent(this, destFlags[0]));
/* 1145 */       updatePagesChanged(RasterImagePagesChangedAction.INSERTED, addIndex, addCount);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void replacePage(int index, RasterImage image)
/*      */   {
/* 1151 */     if (image == null) {
/* 1152 */       throw new ArgumentNullException("image");
/*      */     }
/*      */ 
/* 1155 */     if (index == -1)
/*      */     {
/* 1157 */       index = getPageCount() - 1;
/*      */     }
/*      */ 
/* 1160 */     if ((index < 1) || (index > getPageCount())) {
/* 1161 */       throw new ArgumentOutOfRangeException("index", index, String.format("Page number must be a number greater than or equal to zero and less than %1$s", new Object[] { Integer.valueOf(getPageCount()) }));
/*      */     }
/* 1163 */     int[] srcFlags = { RasterImageChangedFlags.NONE };
/* 1164 */     int[] destFlags = { RasterImageChangedFlags.NONE };
/*      */ 
/* 1166 */     int addIndex = index;
/* 1167 */     int addCount = 1;
/* 1168 */     int removeIndex = image.getPage();
/* 1169 */     int removeCount = 1;
/*      */ 
/* 1171 */     swapPage(true, image, this, image.getPage(), index - 1, srcFlags, destFlags);
/*      */ 
/* 1174 */     if (image.getPageCount() == 0)
/*      */     {
/* 1178 */       image.dispose();
/*      */     }
/*      */     else
/*      */     {
/* 1182 */       image.onChanged(new RasterImageChangedEvent(image, srcFlags[0]));
/* 1183 */       image.updatePagesChanged(RasterImagePagesChangedAction.REMOVED, removeIndex, removeCount);
/*      */     }
/*      */ 
/* 1187 */     onChanged(new RasterImageChangedEvent(this, destFlags[0]));
/* 1188 */     updatePagesChanged(RasterImagePagesChangedAction.INSERTED, addIndex, addCount);
/*      */   }
/*      */ 
/*      */   public void replacePages(int index, RasterImage image, int startIndex, int count)
/*      */   {
/* 1193 */     if (image == null) {
/* 1194 */       throw new ArgumentNullException("image");
/*      */     }
/* 1196 */     if (count == 0) {
/* 1197 */       return;
/*      */     }
/* 1199 */     if (count == -1) {
/* 1200 */       count = image.getPageCount() - startIndex + 1;
/*      */     }
/* 1202 */     if (count == 1)
/*      */     {
/* 1204 */       if (startIndex != 0) {
/* 1205 */         throw new ArgumentOutOfRangeException("startIndex", startIndex, "Invalid page index");
/*      */       }
/* 1207 */       replacePage(index, image);
/* 1208 */       return;
/*      */     }
/*      */ 
/* 1211 */     int[] srcFlags = { RasterImageChangedFlags.NONE };
/* 1212 */     int[] destFlags = { RasterImageChangedFlags.NONE };
/*      */ 
/* 1216 */     image.disableEvents();
/* 1217 */     disableEvents();
/*      */ 
/* 1219 */     int addIndex = index;
/* 1220 */     int addCount = count;
/* 1221 */     int removeIndex = startIndex;
/* 1222 */     int removeCount = count;
/*      */     try
/*      */     {
/* 1226 */       int destIndex = index;
/* 1227 */       for (int i = startIndex; i < startIndex + count; i++)
/*      */       {
/* 1229 */         swapPage(true, image, this, startIndex, destIndex - 1, srcFlags, destFlags);
/* 1230 */         destIndex++;
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 1236 */       enableEvents();
/* 1237 */       image.enableEvents();
/*      */ 
/* 1240 */       if (image.getPageCount() == 0)
/*      */       {
/* 1244 */         image.dispose();
/*      */       }
/*      */       else
/*      */       {
/* 1248 */         image.onChanged(new RasterImageChangedEvent(image, srcFlags[0]));
/* 1249 */         image.updatePagesChanged(RasterImagePagesChangedAction.REMOVED, removeIndex, removeCount);
/*      */       }
/*      */ 
/* 1253 */       onChanged(new RasterImageChangedEvent(this, destFlags[0]));
/* 1254 */       updatePagesChanged(RasterImagePagesChangedAction.INSERTED, addIndex, addCount);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isDisposed()
/*      */   {
/* 1261 */     return this._isDisposed;
/*      */   }
/*      */ 
/*      */   public int getWidth()
/*      */   {
/* 1266 */     return ltkrn.BITMAPHANDLE_getWidth(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public int getHeight()
/*      */   {
/* 1271 */     return ltkrn.BITMAPHANDLE_getHeight(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public int getImageWidth()
/*      */   {
/* 1276 */     int viewPerspective = ltkrn.BITMAPHANDLE_getViewPerspective(this._currentBitmapHandle);
/* 1277 */     return (viewPerspective == 6) || (viewPerspective == 8) || (viewPerspective == 5) || (viewPerspective == 7) ? ltkrn.BITMAPHANDLE_getHeight(this._currentBitmapHandle) : ltkrn.BITMAPHANDLE_getWidth(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public int getImageHeight()
/*      */   {
/* 1282 */     int viewPerspective = ltkrn.BITMAPHANDLE_getViewPerspective(this._currentBitmapHandle);
/* 1283 */     return (viewPerspective == 6) || (viewPerspective == 8) || (viewPerspective == 5) || (viewPerspective == 7) ? ltkrn.BITMAPHANDLE_getWidth(this._currentBitmapHandle) : ltkrn.BITMAPHANDLE_getHeight(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public LeadSize getImageSize()
/*      */   {
/* 1288 */     return new LeadSize(getImageWidth(), getImageHeight());
/*      */   }
/*      */ 
/*      */   public int getImageWidthDpi(boolean useDpi)
/*      */   {
/* 1295 */     if (useDpi) {
/* 1296 */       return getImageWidth() * ltkrn.SCREEN_DPIX / getXResolution();
/*      */     }
/* 1298 */     return getImageWidth();
/*      */   }
/*      */ 
/*      */   public int getImageHeightDpi(boolean useDpi)
/*      */   {
/* 1304 */     if (useDpi) {
/* 1305 */       return getImageHeight() * ltkrn.SCREEN_DPIY / getYResolution();
/*      */     }
/* 1307 */     return getImageHeight();
/*      */   }
/*      */ 
/*      */   public LeadSize getImageSizeDpi(boolean useDpi)
/*      */   {
/* 1312 */     return new LeadSize(getImageWidthDpi(useDpi), getImageHeightDpi(useDpi));
/*      */   }
/*      */ 
/*      */   public int getBitsPerPixel()
/*      */   {
/* 1317 */     return ltkrn.BITMAPHANDLE_getBitsPerPixel(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public int getBytesPerLine()
/*      */   {
/* 1322 */     return ltkrn.BITMAPHANDLE_getBytesPerLine(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public RasterViewPerspective getViewPerspective()
/*      */   {
/* 1327 */     return RasterViewPerspective.forValue(ltkrn.BITMAPHANDLE_getViewPerspective(this._currentBitmapHandle));
/*      */   }
/*      */ 
/*      */   public RasterByteOrder getOrder()
/*      */   {
/* 1332 */     return RasterByteOrder.forValue(ltkrn.BITMAPHANDLE_getOrder(this._currentBitmapHandle));
/*      */   }
/*      */ 
/*      */   public RasterColor[] getPalette()
/*      */   {
/* 1337 */     long bitmap = this._currentBitmapHandle;
/* 1338 */     if (ltkrn.BITMAPHANDLE_getBitsPerPixel(bitmap) <= 8)
/*      */     {
/* 1340 */       int count = ltkrn.BITMAPHANDLE_getColors(bitmap);
/* 1341 */       RasterColor[] colors = new RasterColor[count];
/*      */       try
/*      */       {
/* 1345 */         int ret = ltkrn.GetBitmapColors(bitmap, 0, count, colors);
/* 1346 */         RasterException.checkErrorCode(ret);
/*      */ 
/* 1348 */         return colors;
/*      */       }
/*      */       finally
/*      */       {
/* 1352 */         updateCurrentBitmapHandle();
/*      */       }
/*      */     }
/*      */ 
/* 1356 */     return null;
/*      */   }
/*      */ 
/*      */   public void setPalette(RasterColor[] palette, int startIndex, int count) {
/* 1360 */     boolean changed = false;
/*      */     try
/*      */     {
/* 1364 */       int ret = ltkrn.PutBitmapColors(this._currentBitmapHandle, startIndex, count, palette);
/* 1365 */       RasterException.checkErrorCode(ret);
/*      */ 
/* 1367 */       changed = true;
/*      */     }
/*      */     finally
/*      */     {
/* 1371 */       updateCurrentBitmapHandle();
/* 1372 */       if (changed)
/* 1373 */         onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.PALETTE));
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterColor[] getLookupTable()
/*      */   {
/* 1379 */     RasterColor[] palette = ltkrn.BITMAPHANDLE_getLUT(this._currentBitmapHandle);
/* 1380 */     return palette;
/*      */   }
/*      */ 
/*      */   public void setLookupTable(RasterColor[] value)
/*      */   {
/*      */     try
/*      */     {
/* 1387 */       int length = value != null ? value.length : 0;
/*      */ 
/* 1389 */       ltkrn.BITMAPHANDLE_setLUT(this._currentBitmapHandle, value, length);
/*      */     }
/*      */     finally
/*      */     {
/* 1393 */       updateCurrentBitmapHandle();
/* 1394 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.LOOKUP_TABLE_PALETTE));
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterColor16[] getLookupTable16()
/*      */   {
/* 1400 */     RasterColor16[] palette = ltkrn.BITMAPHANDLE_getLUT16(this._currentBitmapHandle);
/*      */ 
/* 1402 */     return palette;
/*      */   }
/*      */ 
/*      */   public void setLookupTable16(RasterColor16[] value) {
/* 1406 */     long bitmap = this._currentBitmapHandle;
/*      */     try
/*      */     {
/* 1410 */       ltkrn.BITMAPHANDLE_setLUT16(bitmap, value, value == null ? 0 : value.length);
/*      */     }
/*      */     finally
/*      */     {
/* 1414 */       updateCurrentBitmapHandle();
/* 1415 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.LOOKUP_TABLE_PALETTE));
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte[] getPaintLookupTable() {
/* 1420 */     byte[] buffer = ltkrn.BITMAPHANDLE_getPaintLUT(this._currentBitmapHandle);
/* 1421 */     return buffer;
/*      */   }
/*      */ 
/*      */   public void setPaintLookupTable(byte[] value) {
/* 1425 */     if ((value == null) || (value.length != 256)) {
/* 1426 */       throw new IllegalArgumentException("PaintLookupTable should have 256 values");
/*      */     }
/* 1428 */     int ret = ltkrn.BITMAPHANDLE_setPaintLUT(this._currentBitmapHandle, value);
/* 1429 */     RasterException.checkErrorCode(ret);
/*      */ 
/* 1431 */     updateCurrentBitmapHandle();
/* 1432 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.PAINT_LOOKUP_TABLE));
/*      */   }
/*      */ 
/*      */   public short[] getPaintLookupTable16() {
/* 1436 */     return ltkrn.BITMAPHANDLE_getPaintLUT16(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setPaintLookupTable16(short[] value) {
/* 1440 */     if ((value == null) || (value.length != 65536)) {
/* 1441 */       throw new IllegalArgumentException("PaintLookupTable should have 65536 values");
/*      */     }
/* 1443 */     int ret = ltkrn.BITMAPHANDLE_setPaintLUT16(this._currentBitmapHandle, value);
/* 1444 */     RasterException.checkErrorCode(ret);
/*      */ 
/* 1446 */     updateCurrentBitmapHandle();
/* 1447 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.PAINT_LOOKUP_TABLE));
/*      */   }
/*      */ 
/*      */   public boolean hasRegion()
/*      */   {
/* 1452 */     boolean ret = ltkrn.BitmapHasRgn(this._currentBitmapHandle);
/* 1453 */     updateCurrentBitmapHandle();
/* 1454 */     return ret;
/*      */   }
/*      */ 
/*      */   public void makeRegionEmpty() {
/* 1458 */     if (hasRegion())
/*      */     {
/* 1460 */       int ret = ltkrn.SetBitmapRgnHandle(this._currentBitmapHandle, null, 0L, RasterRegionCombineMode.SET.getValue());
/* 1461 */       RasterException.checkErrorCode(ret);
/* 1462 */       updateCurrentBitmapHandle();
/* 1463 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public int calculateRegionMaximumClipSegments() {
/* 1468 */     int[] params = new int[1];
/* 1469 */     int ret = ltkrn.GetBitmapClipSegmentsMax(this._currentBitmapHandle, params);
/* 1470 */     RasterException.checkErrorCode(ret);
/* 1471 */     updateCurrentBitmapHandle();
/*      */ 
/* 1473 */     return params[0];
/*      */   }
/*      */ 
/*      */   public int getRegionClipSegments(int row, int[] segmentsBuffer, int segmentsBufferOffset) {
/* 1477 */     int[] params = new int[1];
/* 1478 */     int ret = ltkrn.GetBitmapClipSegments(this._currentBitmapHandle, row, segmentsBuffer, segmentsBufferOffset, params);
/* 1479 */     RasterException.checkErrorCode(ret);
/* 1480 */     updateCurrentBitmapHandle();
/*      */ 
/* 1482 */     return params[0];
/*      */   }
/*      */ 
/*      */   public long calculateRegionArea() {
/* 1486 */     long[] area = new long[1];
/* 1487 */     int ret = ltkrn.GetBitmapRgnArea(this._currentBitmapHandle, area);
/* 1488 */     RasterException.checkErrorCode(ret);
/* 1489 */     updateCurrentBitmapHandle();
/* 1490 */     return area[0];
/*      */   }
/*      */ 
/*      */   public LeadRect getRegionBounds(RasterRegionXForm xform)
/*      */   {
/*      */     try {
/* 1496 */       LeadRect rc = new LeadRect(0, 0, 0, 0);
/* 1497 */       int ret = ltkrn.GetBitmapRgnBounds(this._currentBitmapHandle, xform, rc);
/* 1498 */       RasterException.checkErrorCode(ret);
/* 1499 */       return rc;
/*      */     }
/*      */     finally
/*      */     {
/* 1503 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public LeadRect getRegionBoundsClipped(RasterRegionXForm xform)
/*      */   {
/*      */     try {
/* 1510 */       LeadRect rc = new LeadRect(0, 0, 0, 0);
/* 1511 */       int ret = ltkrn.GetBitmapRgnBoundsClip(this._currentBitmapHandle, xform, rc);
/* 1512 */       RasterException.checkErrorCode(ret);
/* 1513 */       return rc;
/*      */     }
/*      */     finally
/*      */     {
/* 1517 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean regionContains(int row, int col) {
/* 1522 */     boolean ret = ltkrn.IsPtInBitmapRgn(this._currentBitmapHandle, row, col);
/* 1523 */     updateCurrentBitmapHandle();
/* 1524 */     return ret;
/*      */   }
/*      */ 
/*      */   public void offsetRegion(int rowOffset, int colOffset)
/*      */   {
/*      */     try {
/* 1530 */       int ret = ltkrn.OffsetBitmapRgn(this._currentBitmapHandle, rowOffset, colOffset);
/* 1531 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1535 */       updateCurrentBitmapHandle();
/* 1536 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void flipRegion()
/*      */   {
/*      */     try {
/* 1543 */       int ret = ltkrn.FlipBitmapRgn(this._currentBitmapHandle);
/* 1544 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1548 */       updateCurrentBitmapHandle();
/* 1549 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void reverseRegion()
/*      */   {
/*      */     try {
/* 1556 */       int ret = ltkrn.ReverseBitmapRgn(this._currentBitmapHandle);
/* 1557 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1561 */       updateCurrentBitmapHandle();
/* 1562 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addRectangleToRegion(RasterRegionXForm xform, LeadRect rc, RasterRegionCombineMode combineMode)
/*      */   {
/*      */     try {
/* 1569 */       int ret = ltkrn.SetBitmapRgnRect(this._currentBitmapHandle, xform, rc, combineMode.getValue());
/* 1570 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1574 */       updateCurrentBitmapHandle();
/* 1575 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public static LeadPoint[] getPointArray(RasterCollection<LeadPoint> points)
/*      */   {
/* 1581 */     if (points == null)
/* 1582 */       throw new ArgumentNullException("points");
/* 1583 */     LeadPoint[] ret = new LeadPoint[points.getCount()];
/* 1584 */     return (LeadPoint[])points.toArray(ret);
/*      */   }
/*      */ 
/*      */   public void addColorRgbRangeToRegion(RasterColor lowerColor, RasterColor upperColor, RasterRegionCombineMode combineMode)
/*      */   {
/*      */     try
/*      */     {
/* 1725 */       int ret = ltkrn.SetBitmapRgnColorRGBRange(this._currentBitmapHandle, lowerColor.getColorRef(), upperColor.getColorRef(), combineMode.getValue());
/* 1726 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1730 */       updateCurrentBitmapHandle();
/* 1731 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addColorHsvRangeToRegion(RasterHsvColor lowerColor, RasterHsvColor upperColor, RasterRegionCombineMode combineMode)
/*      */   {
/*      */     try {
/* 1738 */       int ret = ltkrn.SetBitmapRgnColorHSVRange(this._currentBitmapHandle, lowerColor.toUnmanaged(), upperColor.toUnmanaged(), combineMode.getValue());
/* 1739 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1743 */       updateCurrentBitmapHandle();
/* 1744 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addColorToRegion(RasterColor color, RasterRegionCombineMode combineMode)
/*      */   {
/*      */     try {
/* 1751 */       int ret = ltkrn.SetBitmapRgnColor(this._currentBitmapHandle, color.getColorRef(), combineMode.getValue());
/* 1752 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1756 */       updateCurrentBitmapHandle();
/* 1757 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterImage createMaskFromRegion() {
/* 1762 */     long maskBitmap = ltkrn.AllocBitmapHandle();
/* 1763 */     if (maskBitmap == 0L) {
/* 1764 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     }
/*      */     try
/*      */     {
/* 1768 */       int ret = ltkrn.CreateMaskFromBitmapRgn(this._currentBitmapHandle, maskBitmap, ltkrn.BITMAPHANDLE_getSizeOf());
/* 1769 */       RasterException.checkErrorCode(ret);
/*      */ 
/* 1771 */       return createFromBitmapHandle(maskBitmap);
/*      */     }
/*      */     finally
/*      */     {
/* 1775 */       ltkrn.FreeBitmapHandle(maskBitmap);
/* 1776 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addMaskToRegion(RasterRegionXForm xform, RasterImage maskImage, RasterRegionCombineMode combineMode)
/*      */   {
/*      */     try {
/* 1783 */       int ret = ltkrn.SetBitmapRgnFromMask(this._currentBitmapHandle, xform, maskImage.getCurrentBitmapHandle(), combineMode.getValue());
/* 1784 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1788 */       updateCurrentBitmapHandle();
/* 1789 */       if (maskImage != null)
/* 1790 */         maskImage.updateCurrentBitmapHandle();
/* 1791 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addMagicWandToRegion(int left, int top, RasterColor lowerToleranceColor, RasterColor upperToleranceColor, RasterRegionCombineMode combineMode)
/*      */   {
/*      */     try {
/* 1798 */       int ret = ltkrn.SetBitmapRgnMagicWand(this._currentBitmapHandle, left, top, lowerToleranceColor.getColorRef(), upperToleranceColor.getColorRef(), combineMode.getValue());
/* 1799 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1803 */       updateCurrentBitmapHandle();
/* 1804 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addBorderToRegion(int left, int top, RasterColor borderColor, RasterColor lowerToleranceColor, RasterColor upperToleranceColor, RasterRegionCombineMode combineMode)
/*      */   {
/*      */     try {
/* 1811 */       int ret = ltkrn.SetBitmapRgnBorder(this._currentBitmapHandle, left, top, borderColor.getColorRef(), lowerToleranceColor.getColorRef(), upperToleranceColor.getColorRef(), combineMode.getValue());
/* 1812 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1816 */       updateCurrentBitmapHandle();
/* 1817 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setRegion(RasterRegionXForm xform, RasterRegion region, RasterRegionCombineMode combineMode)
/*      */   {
/*      */     try
/*      */     {
/* 1825 */       long leadRegion = region == null ? 0L : region._leadRegion;
/* 1826 */       boolean bDuplicateRegion = false;
/* 1827 */       if ((combineMode.compareTo(RasterRegionCombineMode.SET) == 0) && (leadRegion != 0L))
/*      */       {
/* 1829 */         leadRegion = region.cloneLeadRegion();
/* 1830 */         bDuplicateRegion = true;
/*      */       }
/* 1832 */       int ret = ltkrn.SetBitmapRgn(this._currentBitmapHandle, xform, leadRegion, combineMode.getValue());
/* 1833 */       if ((ret != RasterExceptionCode.SUCCESS.getValue()) && (bDuplicateRegion))
/* 1834 */         ltkrn.DeleteLeadRgn(leadRegion);
/* 1835 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1839 */       updateCurrentBitmapHandle();
/* 1840 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.REGION));
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getAndClearRasterRegionInternal()
/*      */   {
/* 1848 */     long ret = ltkrn.BITMAPHANDLE_getRgnInfo(this._currentBitmapHandle);
/* 1849 */     if (ret != 0L)
/*      */     {
/* 1851 */       ltkrn.BITMAPHANDLE_setRgnInfo(this._currentBitmapHandle, 0L);
/* 1852 */       updateCurrentBitmapHandle();
/*      */     }
/* 1854 */     return ret;
/*      */   }
/*      */ 
/*      */   public RasterRegion getRegion(RasterRegionXForm xform)
/*      */   {
/* 1859 */     RasterRegion region = ltkrn.getRasterRegion(this._currentBitmapHandle);
/* 1860 */     if ((region != null) && (xform != null))
/* 1861 */       region.transform(xform);
/* 1862 */     return region;
/*      */   }
/*      */ 
/*      */   public int getXResolution()
/*      */   {
/* 1867 */     return ltkrn.BITMAPHANDLE_getXResolution(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setXResolution(int value) {
/* 1871 */     if (value < 0) {
/* 1872 */       throw new ArgumentOutOfRangeException("XResolution", value, "Resolution must be greater than 0");
/*      */     }
/* 1874 */     int ret = ltkrn.BITMAPHANDLE_setXResolution(this._currentBitmapHandle, value);
/* 1875 */     RasterException.checkErrorCode(ret);
/* 1876 */     updateCurrentBitmapHandle();
/* 1877 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.RESOLUTION));
/*      */   }
/*      */ 
/*      */   public int getYResolution() {
/* 1881 */     return ltkrn.BITMAPHANDLE_getYResolution(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setYResolution(int value) {
/* 1885 */     if (value < 0) {
/* 1886 */       throw new ArgumentOutOfRangeException("YResolution", value, "Resolution must be greater than 0");
/*      */     }
/* 1888 */     ltkrn.BITMAPHANDLE_setYResolution(this._currentBitmapHandle, value);
/* 1889 */     updateCurrentBitmapHandle();
/* 1890 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.RESOLUTION));
/*      */   }
/*      */ 
/*      */   public int getLowBit() {
/* 1894 */     return ltkrn.BITMAPHANDLE_getLowBit(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setLowBit(int value) {
/* 1898 */     ltkrn.BITMAPHANDLE_setLowBit(this._currentBitmapHandle, value);
/* 1899 */     updateCurrentBitmapHandle();
/* 1900 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.LOW_HIGH_BIT));
/*      */   }
/*      */ 
/*      */   public int getHighBit() {
/* 1904 */     return ltkrn.BITMAPHANDLE_getHighBit(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setHighBit(int value) {
/* 1908 */     ltkrn.BITMAPHANDLE_setHighBit(this._currentBitmapHandle, value);
/* 1909 */     updateCurrentBitmapHandle();
/* 1910 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.LOW_HIGH_BIT));
/*      */   }
/*      */ 
/*      */   public int getMinValue() {
/* 1914 */     return ltkrn.BITMAPHANDLE_getMinValue(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setMinValue(int value) {
/* 1918 */     ltkrn.BITMAPHANDLE_setMinValue(this._currentBitmapHandle, value);
/* 1919 */     updateCurrentBitmapHandle();
/* 1920 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.MIN_MAX_VALUE));
/*      */   }
/*      */ 
/*      */   public int getMaxValue() {
/* 1924 */     return ltkrn.BITMAPHANDLE_getMaxValue(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setMaxValue(int value) {
/* 1928 */     ltkrn.BITMAPHANDLE_setMaxValue(this._currentBitmapHandle, value);
/* 1929 */     updateCurrentBitmapHandle();
/* 1930 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.MIN_MAX_VALUE));
/*      */   }
/*      */ 
/*      */   public int getPaintLowBit() {
/* 1934 */     return ltkrn.BITMAPHANDLE_getPaintLowBit(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setPaintLowBit(int value) {
/* 1938 */     ltkrn.BITMAPHANDLE_setPaintLowBit(this._currentBitmapHandle, value);
/* 1939 */     updateCurrentBitmapHandle();
/* 1940 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.PAINT_PARAMETERS));
/*      */   }
/*      */ 
/*      */   public int getPaintHighBit() {
/* 1944 */     return ltkrn.BITMAPHANDLE_getPaintHighBit(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setPaintHighBit(int value) {
/* 1948 */     ltkrn.BITMAPHANDLE_setPaintHighBit(this._currentBitmapHandle, value);
/* 1949 */     updateCurrentBitmapHandle();
/* 1950 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.PAINT_PARAMETERS));
/*      */   }
/*      */ 
/*      */   public int getPaintGamma() {
/* 1954 */     return ltkrn.BITMAPHANDLE_getPaintGamma(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setPaintGamma(int value) {
/* 1958 */     if (value < 0)
/* 1959 */       throw new IllegalArgumentException("PaintGamme must be greater or equal to zero");
/*      */     try
/*      */     {
/* 1962 */       int ret = ltkrn.SetPaintGamma(this._currentBitmapHandle, value);
/*      */ 
/* 1965 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1969 */       updateCurrentBitmapHandle();
/* 1970 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.PAINT_PARAMETERS));
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getPaintContrast() {
/* 1975 */     return ltkrn.BITMAPHANDLE_getPaintContrast(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setPaintContrast(int value)
/*      */   {
/*      */     try {
/* 1981 */       int ret = ltkrn.SetPaintContrast(this._currentBitmapHandle, value);
/* 1982 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 1986 */       updateCurrentBitmapHandle();
/* 1987 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.PAINT_PARAMETERS));
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getPaintIntensity() {
/* 1992 */     return ltkrn.BITMAPHANDLE_getPaintIntensity(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setPaintIntensity(int value)
/*      */   {
/*      */     try {
/* 1998 */       int ret = ltkrn.SetPaintIntensity(this._currentBitmapHandle, value);
/* 1999 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2003 */       updateCurrentBitmapHandle();
/* 2004 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.PAINT_PARAMETERS));
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterColor getTransparentColor()
/*      */   {
/* 2010 */     return RasterColor.fromColorRef(ltkrn.BITMAPHANDLE_getTransparency(this._currentBitmapHandle));
/*      */   }
/*      */ 
/*      */   public void setTransparentColor(RasterColor value) {
/* 2014 */     ltkrn.BITMAPHANDLE_setTransparency(this._currentBitmapHandle, value.getColorRef());
/* 2015 */     updateCurrentBitmapHandle();
/* 2016 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.TRANSPARENT_COLOR));
/*      */   }
/*      */ 
/*      */   public RasterDitheringMethod getDitheringMethod() {
/* 2020 */     return RasterDitheringMethod.forValue(ltkrn.BITMAPHANDLE_getDitheringMethod(this._currentBitmapHandle));
/*      */   }
/*      */ 
/*      */   public void setDitheringMethod(RasterDitheringMethod value) {
/* 2024 */     ltkrn.BITMAPHANDLE_setDitheringMethod(this._currentBitmapHandle, value.getValue());
/* 2025 */     updateCurrentBitmapHandle();
/* 2026 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DITHERING_METHOD));
/*      */   }
/*      */ 
/*      */   public long getDataSize()
/*      */   {
/* 2038 */     return ltkrn.BITMAPHANDLE_getSize64(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public boolean isConventionalMemory()
/*      */   {
/* 2043 */     return ltkrn.BITMAPHANDLE_getFlagsConventionalMemory(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public boolean isCompressed()
/*      */   {
/* 2048 */     return ltkrn.BITMAPHANDLE_getFlagsCompressed(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public boolean isDiskMemory()
/*      */   {
/* 2053 */     return ltkrn.BITMAPHANDLE_getFlagsDiskMemory(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public boolean isTiled()
/*      */   {
/* 2058 */     return ltkrn.BITMAPHANDLE_getFlagsTiled(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public boolean isSuperCompressed()
/*      */   {
/* 2063 */     return ltkrn.BITMAPHANDLE_getFlagsSuperCompressed(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public boolean isGlobalMemory()
/*      */   {
/* 2068 */     return ltkrn.BITMAPHANDLE_getFlagsGlobal(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public boolean getTransparent()
/*      */   {
/* 2073 */     return ltkrn.BITMAPHANDLE_getFlagsTransparency(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setTransparent(boolean value) {
/* 2077 */     ltkrn.BITMAPHANDLE_setFlagsTransparency(this._currentBitmapHandle, value);
/* 2078 */     updateCurrentBitmapHandle();
/* 2079 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.TRANSPARENT_COLOR));
/*      */   }
/*      */ 
/*      */   public boolean getSigned() {
/* 2083 */     return ltkrn.BITMAPHANDLE_getFlagsSigned(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setSigned(boolean value) {
/* 2087 */     ltkrn.BITMAPHANDLE_setFlagsSigned(this._currentBitmapHandle, value);
/* 2088 */     updateCurrentBitmapHandle();
/* 2089 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.TRANSPARENT_COLOR));
/*      */   }
/*      */ 
/*      */   public boolean isMirrored()
/*      */   {
/* 2094 */     return ltkrn.BITMAPHANDLE_getFlagsMirror(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public boolean getUseLookupTable()
/*      */   {
/* 2099 */     return ltkrn.BITMAPHANDLE_getFlagsUseLUT(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setUseLookupTable(boolean value) {
/* 2103 */     ltkrn.BITMAPHANDLE_setFlagsUseLUT(this._currentBitmapHandle, value);
/* 2104 */     updateCurrentBitmapHandle();
/* 2105 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.USE_LOOKUP_TABLE));
/*      */   }
/*      */ 
/*      */   public boolean getUsePaintLookupTable() {
/* 2109 */     return ltkrn.BITMAPHANDLE_getFlagsUsePaintLUT(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setUsePaintLookupTable(boolean value) {
/* 2113 */     ltkrn.BITMAPHANDLE_setFlagsUsePaintLUT(this._currentBitmapHandle, value);
/* 2114 */     updateCurrentBitmapHandle();
/* 2115 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.USE_PAINT_LOOKUP_TABLE));
/*      */   }
/*      */ 
/*      */   public boolean getNoRegionClip() {
/* 2119 */     return ltkrn.BITMAPHANDLE_getFlagsNoClip(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public void setNoRegionClip(boolean value) {
/* 2123 */     ltkrn.BITMAPHANDLE_setFlagsNoClip(this._currentBitmapHandle, value);
/* 2124 */     updateCurrentBitmapHandle();
/* 2125 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.NO_REGION_CLIP));
/*      */   }
/*      */ 
/*      */   public boolean getPremultiplyAlpha() {
/* 2129 */     return false;
/*      */   }
/*      */ 
/*      */   public void setPremultiplyAlpha(boolean value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean isBasic()
/*      */   {
/* 2139 */     int viewPerspective = ltkrn.BITMAPHANDLE_getViewPerspective(this._currentBitmapHandle);
/* 2140 */     return (viewPerspective == 1) || (viewPerspective == 4);
/*      */   }
/*      */ 
/*      */   public boolean isRotated()
/*      */   {
/* 2145 */     int viewPerspective = ltkrn.BITMAPHANDLE_getViewPerspective(this._currentBitmapHandle);
/* 2146 */     return (viewPerspective == 6) || (viewPerspective == 8) || (viewPerspective == 5) || (viewPerspective == 7);
/*      */   }
/*      */ 
/*      */   public boolean isFlipped()
/*      */   {
/* 2151 */     int viewPerspective = ltkrn.BITMAPHANDLE_getViewPerspective(this._currentBitmapHandle);
/* 2152 */     return (viewPerspective == 4) || (viewPerspective == 5) || (viewPerspective == 2) || (viewPerspective == 7);
/*      */   }
/*      */ 
/*      */   public boolean isGray()
/*      */   {
/* 2157 */     return ltkrn.BITMAPHANDLE_getOrder(this._currentBitmapHandle) == 2;
/*      */   }
/*      */ 
/*      */   public RasterGrayscaleMode getGrayscaleMode()
/*      */   {
/*      */     try
/*      */     {
/* 2171 */       int ret = ltkrn.IsGrayScaleBitmap(this._currentBitmapHandle);
/* 2172 */       if (ret < 0) {
/* 2173 */         RasterException.checkErrorCode(ret);
/*      */       }
/* 2175 */       return RasterGrayscaleMode.forValue(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2179 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterImageFormat getOriginalFormat() {
/* 2184 */     return RasterImageFormat.forValue(ltkrn.BITMAPHANDLE_getOriginalFormat(this._currentBitmapHandle));
/*      */   }
/*      */ 
/*      */   public void setOriginalFormat(RasterImageFormat value)
/*      */   {
/*      */     try {
/* 2190 */       ltkrn.BITMAPHANDLE_setOriginalFormat(this._currentBitmapHandle, value.getValue());
/*      */     }
/*      */     finally
/*      */     {
/* 2194 */       updateCurrentBitmapHandle();
/* 2195 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.FORMAT));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void access()
/*      */   {
/*      */     try
/*      */     {
/* 2203 */       ltkrn.AccessBitmap(this._currentBitmapHandle);
/*      */     }
/*      */     finally
/*      */     {
/* 2207 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void release()
/*      */   {
/*      */     try {
/* 2214 */       ltkrn.ReleaseBitmap(this._currentBitmapHandle);
/*      */     }
/*      */     finally
/*      */     {
/* 2219 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getRow(int row, byte[] buffer, int bufferIndex, long bufferCount) {
/* 2224 */     long ret = ltkrn.GetBitmapRow(this._currentBitmapHandle, buffer, bufferIndex, row, bufferCount);
/* 2225 */     if (ret < 0L)
/* 2226 */       RasterException.checkErrorCode((int)ret);
/* 2227 */     return ret;
/*      */   }
/*      */ 
/*      */   public long getRow(int row, byte[] buffer, long bufferCount) {
/* 2231 */     return getRow(row, buffer, 0, bufferCount);
/*      */   }
/*      */ 
/*      */   public long setRow(int row, byte[] buffer, int bufferIndex, long bufferCount)
/*      */   {
/*      */     try {
/* 2237 */       long ret = ltkrn.PutBitmapRow(this._currentBitmapHandle, buffer, bufferIndex, row, bufferCount);
/* 2238 */       if (ret < 0L)
/* 2239 */         RasterException.checkErrorCode((int)ret);
/* 2240 */       return ret;
/*      */     }
/*      */     finally
/*      */     {
/* 2244 */       updateCurrentBitmapHandle();
/* 2245 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public long setRow(int row, byte[] buffer, long bufferCount) {
/* 2250 */     return setRow(row, buffer, 0, bufferCount);
/*      */   }
/*      */ 
/*      */   public long getRowColumn(int row, int column, byte[] buffer, int bufferIndex, long bufferCount)
/*      */   {
/*      */     try {
/* 2256 */       long ret = ltkrn.GetBitmapRowCol(this._currentBitmapHandle, buffer, bufferIndex, row, column, bufferCount);
/* 2257 */       if (ret < 0L)
/* 2258 */         RasterException.checkErrorCode((int)ret);
/* 2259 */       return (int)ret;
/*      */     }
/*      */     finally
/*      */     {
/* 2263 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getRowColumn(int row, int column, byte[] buffer, long bufferCount) {
/* 2268 */     return getRowColumn(row, column, buffer, 0, bufferCount);
/*      */   }
/*      */ 
/*      */   public int setRowColumn(int row, int column, byte[] buffer, int bufferIndex, long bufferCount)
/*      */   {
/*      */     try {
/* 2274 */       long ret = ltkrn.PutBitmapRowCol(this._currentBitmapHandle, buffer, bufferIndex, row, column, bufferCount);
/* 2275 */       if (ret < 0L)
/* 2276 */         RasterException.checkErrorCode((int)ret);
/* 2277 */       return (int)ret;
/*      */     }
/*      */     finally
/*      */     {
/* 2281 */       updateCurrentBitmapHandle();
/* 2282 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public int setRowColumn(int row, int column, byte[] buffer, long bufferCount) {
/* 2287 */     return setRowColumn(row, column, buffer, 0, bufferCount);
/*      */   }
/*      */ 
/*      */   public void setRowSegments(int row, int column, byte[] buffer, int bufferOffset, int[] segments, int segmentsOffset, int segmentCount, boolean preserveAlpha)
/*      */   {
/*      */     try {
/* 2293 */       int ret = ltkrn.PutBitmapRowSegments(this._currentBitmapHandle, buffer, bufferOffset, row, column, segments, segmentsOffset, segmentCount, preserveAlpha);
/* 2294 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2298 */       updateCurrentBitmapHandle();
/* 2299 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setRowSegments(int row, int column, byte[] buffer, int[] segments, int segmentCount, boolean preserveAlpha) {
/* 2304 */     setRowSegments(row, column, buffer, 0, segments, 0, segmentCount, preserveAlpha);
/*      */   }
/*      */ 
/*      */   public int getRowColumnCompressed(byte[] workBuffer, short[] runBuffer, int runBufferOffset, int row, int column, int width)
/*      */   {
/*      */     try {
/* 2310 */       long ret = ltkrn.GetBitmapRowColCompressed(this._currentBitmapHandle, workBuffer, runBuffer, runBufferOffset, row, column, width);
/* 2311 */       if (ret < 0L)
/* 2312 */         RasterException.checkErrorCode((int)ret);
/* 2313 */       return (int)ret;
/*      */     }
/*      */     finally
/*      */     {
/* 2317 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getRowColumnCompressed(byte[] workBuffer, short[] runBuffer, int row, int column, int width) {
/* 2322 */     return getRowColumnCompressed(workBuffer, runBuffer, 0, row, column, width);
/*      */   }
/*      */ 
/*      */   public void getRowCompressed(byte[] workBuffer, short[] runBuffer, int runBufferOffset, int row, int lines)
/*      */   {
/*      */     try {
/* 2328 */       int ret = ltkrn.GetBitmapRowCompressed(this._currentBitmapHandle, workBuffer, runBuffer, runBufferOffset, row, lines);
/* 2329 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2333 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void getRowCompressed(byte[] workBuffer, short[] runBuffer, int row, int lines) {
/* 2338 */     getRowCompressed(workBuffer, runBuffer, 0, row, lines);
/*      */   }
/*      */ 
/*      */   public int setRowColumnCompressed(byte[] workBuffer, short[] runBuffer, int runBufferOffset, int row, int column, int width)
/*      */   {
/*      */     try {
/* 2344 */       long ret = ltkrn.PutBitmapRowColCompressed(this._currentBitmapHandle, workBuffer, runBuffer, runBufferOffset, row, column, width);
/* 2345 */       if (ret < 0L)
/* 2346 */         RasterException.checkErrorCode((int)ret);
/* 2347 */       return (int)ret;
/*      */     }
/*      */     finally
/*      */     {
/* 2351 */       updateCurrentBitmapHandle();
/* 2352 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public int setRowColumnCompressed(byte[] workBuffer, short[] runBuffer, int row, int column, int width) {
/* 2357 */     return setRowColumnCompressed(workBuffer, runBuffer, 0, row, column, width);
/*      */   }
/*      */ 
/*      */   public void setRowCompressed(byte[] workBuffer, short[] runBuffer, int runBufferOffset, int row, int lines)
/*      */   {
/*      */     try {
/* 2363 */       int ret = ltkrn.PutBitmapRowCompressed(this._currentBitmapHandle, workBuffer, runBuffer, runBufferOffset, row, lines);
/* 2364 */       if (ret < 0)
/* 2365 */         RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2369 */       updateCurrentBitmapHandle();
/* 2370 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setRowCompressed(byte[] workBuffer, short[] runBuffer, int row, int lines) {
/* 2375 */     setRowCompressed(workBuffer, runBuffer, 0, row, lines);
/*      */   }
/*      */ 
/*      */   public void changeCompression(RasterCompression compression)
/*      */   {
/*      */     try
/*      */     {
/* 2382 */       int ret = ltkrn.ChangeBitmapCompression(this._currentBitmapHandle, compression.getValue());
/* 2383 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2387 */       updateCurrentBitmapHandle();
/* 2388 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterImage createAlphaImage()
/*      */   {
/* 2394 */     long alphaBitmap = ltkrn.AllocBitmapHandle();
/* 2395 */     if (alphaBitmap == 0L)
/* 2396 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     try
/*      */     {
/* 2399 */       int ret = ltkrn.GetBitmapAlpha(this._currentBitmapHandle, alphaBitmap, ltkrn.BITMAPHANDLE_getSizeOf());
/* 2400 */       RasterException.checkErrorCode(ret);
/* 2401 */       return createFromBitmapHandle(alphaBitmap);
/*      */     }
/*      */     finally
/*      */     {
/* 2405 */       ltkrn.FreeBitmapHandle(alphaBitmap);
/* 2406 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAlphaImage(RasterImage alphaImage) {
/* 2411 */     if (alphaImage == null) {
/* 2412 */       throw new ArgumentNullException("alphaImage");
/*      */     }
/*      */     try
/*      */     {
/* 2416 */       int ret = ltkrn.SetBitmapAlpha(this._currentBitmapHandle, alphaImage.getCurrentBitmapHandle());
/* 2417 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2421 */       if (alphaImage != null)
/* 2422 */         alphaImage.updateCurrentBitmapHandle();
/* 2423 */       updateCurrentBitmapHandle();
/* 2424 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public int translateColor(RasterImage destImage, int rgb)
/*      */   {
/*      */     try
/*      */     {
/* 2432 */       int ret = ltkrn.TranslateBitmapColor(destImage.getCurrentBitmapHandle(), this._currentBitmapHandle, rgb);
/* 2433 */       return ret;
/*      */     }
/*      */     finally
/*      */     {
/* 2437 */       if (destImage != null)
/* 2438 */         destImage.updateCurrentBitmapHandle();
/* 2439 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterColor translateColor(RasterImage destImage, RasterColor color) {
/* 2444 */     int clr = translateColor(destImage, color.getColorRef());
/* 2445 */     return RasterColor.fromColorRef(clr);
/*      */   }
/*      */ 
/*      */   public void changeHeight(int height)
/*      */   {
/*      */     try {
/* 2451 */       int ret = ltkrn.ChangeBitmapHeight(this._currentBitmapHandle, height);
/* 2452 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2456 */       updateCurrentBitmapHandle();
/* 2457 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA | RasterImageChangedFlags.SIZE));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void copyPaletteTo(RasterImage destImage)
/*      */   {
/*      */     try {
/* 2464 */       int ret = ltkrn.CopyBitmapPalette(destImage.getCurrentBitmapHandle(), this._currentBitmapHandle);
/* 2465 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2469 */       updateCurrentBitmapHandle();
/* 2470 */       if (destImage != null)
/*      */       {
/* 2472 */         destImage.updateCurrentBitmapHandle();
/* 2473 */         destImage.onChanged(new RasterImageChangedEvent(destImage, RasterImageChangedFlags.PALETTE));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterColor getPixelColor(int row, int column)
/*      */   {
/*      */     try
/*      */     {
/* 2482 */       int ret = ltkrn.GetPixelColor(this._currentBitmapHandle, row, column);
/* 2483 */       return RasterColor.fromColorRef(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2487 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setPixelColor(int row, int column, RasterColor color)
/*      */   {
/*      */     try {
/* 2494 */       int ret = ltkrn.PutPixelColor(this._currentBitmapHandle, row, column, color.getColorRef());
/* 2495 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2499 */       updateCurrentBitmapHandle();
/* 2500 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterColor getPixel(int row, int column)
/*      */   {
/*      */     try {
/* 2507 */       byte[] uAlpha = new byte[1];
/* 2508 */       uAlpha[0] = -1;
/* 2509 */       int colorRef = ltkrn.IntGetPixelColor(this._currentBitmapHandle, row, column, uAlpha);
/* 2510 */       int currentBitsPerPixel = getBitsPerPixel();
/*      */       RasterColor localRasterColor;
/* 2511 */       if ((currentBitsPerPixel == 32) || (currentBitsPerPixel == 64))
/*      */       {
/* 2513 */         return new RasterColor(uAlpha[0], RasterColor.getARValue(colorRef), RasterColor.getAGValue(colorRef), RasterColor.getABValue(colorRef));
/*      */       }
/*      */ 
/* 2517 */       return RasterColor.fromColorRef(colorRef);
/*      */     }
/*      */     finally
/*      */     {
/* 2522 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setPixel(int row, int column, RasterColor color)
/*      */   {
/*      */     try {
/* 2529 */       int colorRef = color.getColorRef();
/* 2530 */       byte uAlpha = -1;
/*      */ 
/* 2532 */       int currentBitsPerPixel = getBitsPerPixel();
/* 2533 */       if ((currentBitsPerPixel == 32) || (currentBitsPerPixel == 64)) {
/* 2534 */         uAlpha = color.getA();
/*      */       }
/* 2536 */       byte[] params = { uAlpha };
/* 2537 */       int ret = ltkrn.IntPutPixelColor(this._currentBitmapHandle, row, column, colorRef, params);
/* 2538 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2542 */       updateCurrentBitmapHandle();
/* 2543 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte[] getPixelData(int row, int column)
/*      */   {
/* 2549 */     int bitsPerPixel = getBitsPerPixel();
/* 2550 */     int size = ((column + 1) * bitsPerPixel + 7) / 8 - column * bitsPerPixel / 8;
/* 2551 */     byte[] buffer = new byte[size];
/* 2552 */     getPixelData(row, column, buffer, size);
/* 2553 */     return buffer;
/*      */   }
/*      */ 
/*      */   public void setPixelData(int row, int column, byte[] data) {
/* 2557 */     setPixelData(row, column, data, data != null ? data.length : 0);
/*      */   }
/*      */ 
/*      */   public void getPixelData(int row, int column, byte[] data, int dataSize)
/*      */   {
/*      */     try
/*      */     {
/* 2564 */       int ret = ltkrn.GetPixelData(this._currentBitmapHandle, data, row, column, dataSize);
/* 2565 */       if (ret < 0)
/* 2566 */         RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2570 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setPixelData(int row, int column, byte[] data, int dataSize)
/*      */   {
/*      */     try {
/* 2577 */       int ret = ltkrn.PutPixelData(this._currentBitmapHandle, data, row, column, dataSize);
/* 2578 */       if (ret < 0)
/* 2579 */         RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2583 */       updateCurrentBitmapHandle();
/* 2584 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void copyData(byte[] data, int dataOffset, long size)
/*      */   {
/*      */     try
/*      */     {
/* 2592 */       int ret = ltkrn.SetBitmapDataPointer(this._currentBitmapHandle, data, dataOffset, size);
/* 2593 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2597 */       updateCurrentBitmapHandle();
/* 2598 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setUserData(byte[] data, long size) {
/* 2603 */     copyData(data, 0, size);
/*      */   }
/*      */ 
/*      */   public void flipViewPerspective(boolean horizontal)
/*      */   {
/*      */     try
/*      */     {
/*      */       int ret;
/*      */       int ret;
/* 2612 */       if (horizontal)
/* 2613 */         ret = ltkrn.ReverseBitmapViewPerspective(this._currentBitmapHandle);
/*      */       else {
/* 2615 */         ret = ltkrn.FlipBitmapViewPerspective(this._currentBitmapHandle);
/*      */       }
/* 2617 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2621 */       updateCurrentBitmapHandle();
/* 2622 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.VIEW_PERSPECTIVE));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rotateViewPerspective(int degrees)
/*      */   {
/*      */     try {
/* 2629 */       int ret = ltkrn.RotateBitmapViewPerspective(this._currentBitmapHandle, degrees);
/* 2630 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2634 */       updateCurrentBitmapHandle();
/* 2635 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.VIEW_PERSPECTIVE));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeViewPerspective(RasterViewPerspective viewPerspective)
/*      */   {
/*      */     try {
/* 2642 */       int ret = ltkrn.ChangeBitmapViewPerspective(0L, this._currentBitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), viewPerspective.getValue());
/* 2643 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2647 */       updateCurrentBitmapHandle();
/* 2648 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.VIEW_PERSPECTIVE));
/*      */     }
/*      */   }
/*      */ 
/*      */   public LeadPoint pointFromImage(RasterViewPerspective viewPerspective, LeadPoint pt)
/*      */   {
/*      */     try
/*      */     {
/* 2656 */       LeadPoint newPt = new LeadPoint(pt.getX(), pt.getY());
/* 2657 */       int ret = ltkrn.PointFromBitmap(this._currentBitmapHandle, viewPerspective.getValue(), newPt);
/* 2658 */       RasterException.checkErrorCode(ret);
/* 2659 */       return newPt;
/*      */     }
/*      */     finally
/*      */     {
/* 2663 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public LeadPoint pointToImage(RasterViewPerspective viewPerspective, LeadPoint pt)
/*      */   {
/*      */     try {
/* 2670 */       LeadPoint newPt = new LeadPoint(pt.getX(), pt.getY());
/* 2671 */       int ret = ltkrn.PointToBitmap(this._currentBitmapHandle, viewPerspective.getValue(), pt);
/* 2672 */       RasterException.checkErrorCode(ret);
/* 2673 */       return newPt;
/*      */     }
/*      */     finally
/*      */     {
/* 2677 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public LeadRect rectangleFromImage(RasterViewPerspective viewPerspective, LeadRect rc)
/*      */   {
/*      */     try {
/* 2684 */       LeadRect newRect = new LeadRect(rc.getLeft(), rc.getTop(), rc.getWidth(), rc.getHeight());
/* 2685 */       int ret = ltkrn.RectFromBitmap(this._currentBitmapHandle, viewPerspective.getValue(), newRect);
/* 2686 */       RasterException.checkErrorCode(ret);
/* 2687 */       return newRect;
/*      */     }
/*      */     finally
/*      */     {
/* 2691 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public LeadRect rectangleToImage(RasterViewPerspective viewPerspective, LeadRect rc)
/*      */   {
/*      */     try {
/* 2698 */       LeadRect newRect = new LeadRect(rc.getLeft(), rc.getTop(), rc.getWidth(), rc.getHeight());
/* 2699 */       int ret = ltkrn.RectToBitmap(this._currentBitmapHandle, viewPerspective.getValue(), newRect);
/* 2700 */       RasterException.checkErrorCode(ret);
/* 2701 */       return newRect;
/*      */     }
/*      */     finally
/*      */     {
/* 2705 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startDithering(RasterColor[] palette, int colors)
/*      */   {
/*      */     try
/*      */     {
/* 2713 */       int ret = ltkrn.StartDithering(this._currentBitmapHandle, palette, colors);
/* 2714 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2718 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ditherLine(byte[] srcBuffer, int srcBufferOffset, byte[] destBuffer, int destBufferOffset)
/*      */   {
/*      */     try {
/* 2725 */       int ret = ltkrn.DitherLine(this._currentBitmapHandle, srcBuffer, srcBufferOffset, destBuffer, destBufferOffset);
/* 2726 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2730 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ditherLine(byte[] srcBuffer, byte[] destBuffer) {
/* 2735 */     ditherLine(srcBuffer, 0, destBuffer, 0);
/*      */   }
/*      */ 
/*      */   public void stopDithering()
/*      */   {
/*      */     try {
/* 2741 */       int ret = ltkrn.StopDithering(this._currentBitmapHandle);
/* 2742 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2746 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void windowLevel(int lowBit, int highBit, RasterColor[] palette, RasterWindowLevelMode mode)
/*      */   {
/*      */     try
/*      */     {
/* 2754 */       int count = palette != null ? palette.length : 0;
/*      */ 
/* 2756 */       int ret = ltkrn.WindowLevel(this._currentBitmapHandle, lowBit, highBit, palette, count, mode.getValue());
/* 2757 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/*      */       int changedFlags;
/* 2761 */       updateCurrentBitmapHandle();
/* 2762 */       int changedFlags = RasterImageChangedFlags.LOW_HIGH_BIT | RasterImageChangedFlags.PAINT_PARAMETERS | RasterImageChangedFlags.MIN_MAX_VALUE | RasterImageChangedFlags.LOOKUP_TABLE_PALETTE | RasterImageChangedFlags.USE_LOOKUP_TABLE;
/* 2763 */       onChanged(new RasterImageChangedEvent(this, changedFlags));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void windowLevelExt(int lowBit, int highBit, RasterColor16[] palette, RasterWindowLevelMode mode)
/*      */   {
/*      */     try {
/* 2770 */       int count = palette != null ? palette.length : 0;
/*      */ 
/* 2772 */       int ret = ltkrn.WindowLevelExt(this._currentBitmapHandle, lowBit, highBit, palette, count, mode.getValue());
/* 2773 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/*      */       int changedFlags;
/* 2777 */       updateCurrentBitmapHandle();
/* 2778 */       int changedFlags = RasterImageChangedFlags.LOW_HIGH_BIT | RasterImageChangedFlags.PAINT_PARAMETERS | RasterImageChangedFlags.MIN_MAX_VALUE | RasterImageChangedFlags.LOOKUP_TABLE_PALETTE | RasterImageChangedFlags.USE_LOOKUP_TABLE;
/* 2779 */       onChanged(new RasterImageChangedEvent(this, changedFlags));
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterImageMemoryInformation getMemoryInformation()
/*      */   {
/*      */     try
/*      */     {
/* 2787 */       int[] params = new int[6];
/* 2788 */       int ret = ltkrn.GetBitmapMemoryInfo(this._currentBitmapHandle, params);
/* 2789 */       RasterException.checkErrorCode(ret);
/* 2790 */       RasterImageMemoryInformation info = RasterImageMemoryInformation.getEmpty();
/* 2791 */       info.setMemoryFlags(params[0]);
/* 2792 */       info.setTileSize(params[1]);
/* 2793 */       info.setTotalTiles(params[2]);
/* 2794 */       info.setConventionalTiles(params[3]);
/* 2795 */       info.setMaximumTileViews(params[4]);
/* 2796 */       info.setTileViews(params[5]);
/* 2797 */       return info;
/*      */     }
/*      */     finally
/*      */     {
/* 2801 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setMemoryInformation(RasterImageMemoryInformation value)
/*      */   {
/*      */     try {
/* 2808 */       int ret = ltkrn.SetBitmapMemoryInfo(this._currentBitmapHandle, value.getMemoryFlags(), value.getTileSize(), value.getTotalTiles(), value.getConventionalTiles(), value.getMaximumTileViews(), value.getTileViews(), value.getFlags());
/* 2809 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 2813 */       updateCurrentBitmapHandle();
/* 2814 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public static LeadRect calculatePaintModeRectangle(int sourceWidth, int sourceHeight, LeadRect destinationRectangle, RasterPaintSizeMode sizeMode, RasterPaintAlignMode horizontalAlignMode, RasterPaintAlignMode verticalAlignMode)
/*      */   {
/* 2820 */     LeadRect rc = new LeadRect(destinationRectangle.getLeft(), destinationRectangle.getTop(), destinationRectangle.getWidth(), destinationRectangle.getHeight());
/*      */ 
/* 2822 */     int ret = ltkrn.CalculatePaintModeRectangle(sourceWidth, sourceHeight, rc, sizeMode.getValue(), horizontalAlignMode.getValue(), verticalAlignMode.getValue());
/* 2823 */     RasterException.checkErrorCode(ret);
/*      */ 
/* 2825 */     return rc;
/*      */   }
/*      */ 
/*      */   public RasterCollection<RasterTagMetadata> getTags()
/*      */   {
/* 2944 */     return this._tags;
/*      */   }
/*      */ 
/*      */   public RasterCollection<RasterTagMetadata> getGeoKeys() {
/* 2948 */     return this._geoKeys;
/*      */   }
/*      */ 
/*      */   public RasterCollection<RasterCommentMetadata> getComments() {
/* 2952 */     return this._comments;
/*      */   }
/*      */ 
/*      */   public RasterCollection<RasterMarkerMetadata> getMarkers() {
/* 2956 */     return this._markers;
/*      */   }
/*      */ 
/*      */   public void copyMetadataTo(RasterImage image, int flags) {
/* 2960 */     image.disableEvents();
/*      */     try
/*      */     {
/* 2964 */       if ((flags & RasterMetadataFlags.TAGS.getValue()) == RasterMetadataFlags.TAGS.getValue())
/*      */       {
/* 2966 */         image.getTags().removeAll();
/* 2967 */         for (RasterTagMetadata tag : getTags()) {
/* 2968 */           image.getTags().add(tag.clone());
/*      */         }
/*      */       }
/* 2971 */       if ((flags & RasterMetadataFlags.GEOKEYS.getValue()) == RasterMetadataFlags.GEOKEYS.getValue())
/*      */       {
/* 2973 */         image.getGeoKeys().removeAll();
/* 2974 */         for (RasterTagMetadata key : getGeoKeys()) {
/* 2975 */           image.getGeoKeys().add(key.clone());
/*      */         }
/*      */       }
/* 2978 */       if ((flags & RasterMetadataFlags.COMMENTS.getValue()) == RasterMetadataFlags.COMMENTS.getValue())
/*      */       {
/* 2980 */         image.getComments().removeAll();
/* 2981 */         for (RasterCommentMetadata comment : getComments()) {
/* 2982 */           image.getComments().add(comment.clone());
/*      */         }
/*      */       }
/* 2985 */       if ((flags & RasterMetadataFlags.MARKERS.getValue()) == RasterMetadataFlags.MARKERS.getValue())
/*      */       {
/* 2987 */         image.getMarkers().removeAll();
/* 2988 */         for (RasterMarkerMetadata marker : getMarkers()) {
/* 2989 */           image.getMarkers().add(marker.clone());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2994 */       image.enableEvents();
/*      */ 
/* 2996 */       int changedFlags = RasterImageChangedFlags.NONE;
/* 2997 */       if ((flags & RasterMetadataFlags.TAGS.getValue()) == RasterMetadataFlags.TAGS.getValue())
/* 2998 */         changedFlags |= RasterImageChangedFlags.TAG;
/* 2999 */       if ((flags & RasterMetadataFlags.GEOKEYS.getValue()) == RasterMetadataFlags.GEOKEYS.getValue())
/* 3000 */         changedFlags |= RasterImageChangedFlags.GEOKEY;
/* 3001 */       if ((flags & RasterMetadataFlags.COMMENTS.getValue()) == RasterMetadataFlags.COMMENTS.getValue())
/* 3002 */         changedFlags |= RasterImageChangedFlags.COMMENT;
/* 3003 */       if ((flags & RasterMetadataFlags.MARKERS.getValue()) == RasterMetadataFlags.MARKERS.getValue())
/* 3004 */         changedFlags |= RasterImageChangedFlags.MARKER;
/* 3005 */       image.onChanged(new RasterImageChangedEvent(image, changedFlags));
/*      */     }
/*      */     finally
/*      */     {
/* 2994 */       image.enableEvents();
/*      */ 
/* 2996 */       int changedFlags = RasterImageChangedFlags.NONE;
/* 2997 */       if ((flags & RasterMetadataFlags.TAGS.getValue()) == RasterMetadataFlags.TAGS.getValue())
/* 2998 */         changedFlags |= RasterImageChangedFlags.TAG;
/* 2999 */       if ((flags & RasterMetadataFlags.GEOKEYS.getValue()) == RasterMetadataFlags.GEOKEYS.getValue())
/* 3000 */         changedFlags |= RasterImageChangedFlags.GEOKEY;
/* 3001 */       if ((flags & RasterMetadataFlags.COMMENTS.getValue()) == RasterMetadataFlags.COMMENTS.getValue())
/* 3002 */         changedFlags |= RasterImageChangedFlags.COMMENT;
/* 3003 */       if ((flags & RasterMetadataFlags.MARKERS.getValue()) == RasterMetadataFlags.MARKERS.getValue())
/* 3004 */         changedFlags |= RasterImageChangedFlags.MARKER;
/* 3005 */       image.onChanged(new RasterImageChangedEvent(image, changedFlags));
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterImage getOverlayImage(int index, RasterGetSetOverlayImageMode mode)
/*      */   {
/* 3054 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/*      */     try
/*      */     {
/* 3057 */       int ret = ltkrn.GetOverlayBitmap(this._currentBitmapHandle, index, bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), mode.getValue());
/* 3058 */       RasterException.checkErrorCode(ret);
/*      */ 
/* 3060 */       RasterImage image = createFromBitmapHandle(bitmapHandle);
/*      */ 
/* 3062 */       if ((mode == RasterGetSetOverlayImageMode.NO_COPY) || (mode == RasterGetSetOverlayImageMode.MOVE)) {
/* 3063 */         image.setNoFreeDataOnDispose(true);
/*      */       }
/* 3065 */       return image;
/*      */     }
/*      */     finally
/*      */     {
/* 3069 */       ltkrn.FreeBitmapHandle(bitmapHandle);
/* 3070 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setOverlayImage(int index, RasterImage overlayImage, RasterGetSetOverlayImageMode mode) {
/* 3075 */     long bitmapHandle = 0L;
/*      */ 
/* 3077 */     if (overlayImage != null) {
/* 3078 */       bitmapHandle = overlayImage.getCurrentBitmapHandle();
/*      */     }
/*      */     try
/*      */     {
/* 3082 */       int ret = ltkrn.SetOverlayBitmap(this._currentBitmapHandle, index, bitmapHandle, mode.getValue());
/* 3083 */       RasterException.checkErrorCode(ret);
/*      */ 
/* 3085 */       if (overlayImage != null)
/*      */       {
/* 3087 */         switch (5.$SwitchMap$leadtools$RasterGetSetOverlayImageMode[mode.ordinal()])
/*      */         {
/*      */         case 1:
/* 3090 */           overlayImage.setNoFreeDataOnDispose(true);
/* 3091 */           break;
/*      */         case 2:
/* 3094 */           overlayImage.setNoFreeDataOnDispose(true);
/* 3095 */           overlayImage.dispose();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 3105 */       updateCurrentBitmapHandle();
/* 3106 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.OVERLAYS_INFO));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setOverlayImageSize(int index, int width, int height) {
/* 3111 */     if (width < 1) {
/* 3112 */       throw new ArgumentOutOfRangeException("width", width, "New image size should be greated than 0");
/*      */     }
/* 3114 */     if (height < 1) {
/* 3115 */       throw new ArgumentOutOfRangeException("height", height, "New image size should be greated than 0");
/*      */     }
/* 3117 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/* 3118 */     if (bitmapHandle == 0L)
/* 3119 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/* 3120 */     ltkrn.InitBitmap(bitmapHandle, ltkrn.BITMAPHANDLE_getSizeOf(), width, height, 1);
/* 3121 */     ltkrn.BITMAPHANDLE_setViewPerspective(bitmapHandle, ltkrn.BITMAPHANDLE_getViewPerspective(this._currentBitmapHandle));
/*      */     try
/*      */     {
/* 3125 */       int ret = ltkrn.SetOverlayBitmap(this._currentBitmapHandle, index, bitmapHandle, 0);
/* 3126 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 3130 */       updateCurrentBitmapHandle();
/* 3131 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.OVERLAYS_INFO));
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getOverlayCount()
/*      */   {
/*      */     try {
/* 3138 */       int[] count = new int[1];
/* 3139 */       count[0] = 0;
/* 3140 */       int ret = ltkrn.GetOverlayCount(this._currentBitmapHandle, count, 0);
/* 3141 */       RasterException.checkErrorCode(ret);
/* 3142 */       return count[0];
/*      */     }
/*      */     finally
/*      */     {
/* 3146 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateOverlayBits(int index, RasterUpdateOverlayBitsFlags flags)
/*      */   {
/*      */     try {
/* 3153 */       int ret = ltkrn.UpdateBitmapOverlayBits(this._currentBitmapHandle, index, flags.getValue());
/* 3154 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 3158 */       updateCurrentBitmapHandle();
/* 3159 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.OVERLAYS_INFO));
/*      */     }
/*      */   }
/*      */ 
/*      */   public RasterOverlayAttributes getOverlayAttributes(int index, int flags)
/*      */   {
/*      */     try {
/* 3166 */       RasterOverlayAttributes attr = new RasterOverlayAttributes();
/* 3167 */       int ret = ltkrn.GetOverlayAttributes(this._currentBitmapHandle, index, attr, flags);
/* 3168 */       RasterException.checkErrorCode(ret);
/* 3169 */       return attr;
/*      */     }
/*      */     finally
/*      */     {
/* 3173 */       updateCurrentBitmapHandle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateOverlayAttributes(int index, RasterOverlayAttributes attributes, int flags) {
/* 3178 */     if (attributes == null) {
/* 3179 */       throw new ArgumentNullException("attributes");
/*      */     }
/*      */     try
/*      */     {
/* 3183 */       int ret = ltkrn.SetOverlayAttributes(this._currentBitmapHandle, index, attributes, flags);
/* 3184 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 3188 */       updateCurrentBitmapHandle();
/* 3189 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.OVERLAYS_INFO));
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void writeObject(ObjectOutputStream stream)
/*      */     throws IOException
/*      */   {
/* 3209 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/* 3211 */     ObjectOutputStream.PutField fields = stream.putFields();
/* 3212 */     fields.put("_imageVersion", 0);
/* 3213 */     fields.put("_ignoreEventsCounter", this._ignoreEventsCounter);
/*      */ 
/* 3215 */     int count = getPageCount();
/* 3216 */     fields.put("_pageCount", count);
/* 3217 */     fields.put("_page", getPage());
/*      */ 
/* 3220 */     fields.put("_noFreeDataOnDispose", this._noFreeDataOnDispose);
/* 3221 */     fields.put("_animationGlobalLoop", this._animationGlobalLoop);
/* 3222 */     fields.put("_animationGlobalSize", this._animationGlobalSize);
/* 3223 */     fields.put("_animationGlobalBackground", this._animationGlobalBackground.getColorRef());
/*      */ 
/* 3225 */     stream.writeFields();
/*      */ 
/* 3228 */     stream.writeInt(0);
/* 3229 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/*      */     try
/*      */     {
/* 3232 */       for (int i = 0; (i < count) && (ret == L_ERROR.SUCCESS.getValue()); i++)
/*      */       {
/* 3234 */         getBitmapHandle(bitmapHandle, i + 1);
/* 3235 */         ret = BitmapHandleSerializer.serialize(bitmapHandle, stream);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3240 */       ltkrn.FreeBitmapHandle(bitmapHandle);
/*      */     }
/* 3242 */     if (ret == L_ERROR.SUCCESS.getValue())
/*      */     {
/* 3246 */       count = this._tags != null ? this._tags.getCount() : 0;
/* 3247 */       stream.writeInt(count);
/* 3248 */       for (int i = 0; i < count; i++)
/*      */       {
/* 3250 */         RasterTagMetadata tag = (RasterTagMetadata)this._tags.getItem(i);
/* 3251 */         stream.writeObject(tag);
/*      */       }
/*      */ 
/* 3254 */       count = this._geoKeys != null ? this._geoKeys.getCount() : 0;
/* 3255 */       stream.writeInt(count);
/* 3256 */       for (int i = 0; i < count; i++)
/*      */       {
/* 3258 */         RasterTagMetadata key = (RasterTagMetadata)this._geoKeys.getItem(i);
/* 3259 */         stream.writeObject(key);
/*      */       }
/*      */ 
/* 3262 */       count = this._markers != null ? this._markers.getCount() : 0;
/* 3263 */       stream.writeInt(count);
/* 3264 */       for (int i = 0; i < count; i++)
/*      */       {
/* 3266 */         RasterMarkerMetadata marker = (RasterMarkerMetadata)this._markers.getItem(i);
/* 3267 */         stream.writeObject(marker);
/*      */       }
/*      */ 
/* 3270 */       count = this._comments != null ? this._comments.getCount() : 0;
/* 3271 */       stream.writeInt(count);
/* 3272 */       for (int i = 0; i < count; i++)
/*      */       {
/* 3274 */         RasterCommentMetadata comment = (RasterCommentMetadata)this._comments.getItem(i);
/* 3275 */         stream.writeObject(comment);
/*      */       }
/*      */     }
/*      */ 
/* 3279 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException, RasterException
/*      */   {
/* 3284 */     init();
/*      */ 
/* 3286 */     ObjectInputStream.GetField fields = stream.readFields();
/* 3287 */     this._readerWriterLock = new ReentrantReadWriteLock();
/*      */ 
/* 3289 */     this._ignoreEventsCounter = fields.get("_ignoreEventsCounter", 0);
/*      */ 
/* 3291 */     int ret = L_ERROR.SUCCESS.getValue();
/*      */ 
/* 3294 */     int count = fields.get("_pageCount", 0);
/* 3295 */     long bitmapHandle = ltkrn.AllocBitmapHandle();
/* 3296 */     if (bitmapHandle == 0L) {
/* 3297 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*      */     }
/*      */ 
/* 3300 */     int version = stream.readInt();
/*      */     try
/*      */     {
/* 3304 */       for (int i = 0; (i < count) && (ret == L_ERROR.SUCCESS.getValue()); i++)
/*      */       {
/*      */         try
/*      */         {
/* 3308 */           ret = BitmapHandleSerializer.deserialize(bitmapHandle, stream);
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/* 3312 */           ret = L_ERROR.ERROR_EXCEPTION.getValue();
/*      */         }
/* 3314 */         if (ret == L_ERROR.SUCCESS.getValue())
/*      */         {
/* 3316 */           if (i == 0) {
/* 3317 */             initialize(bitmapHandle);
/*      */           }
/*      */           else {
/* 3320 */             ret = ltkrn.InsertBitmapListItem(this._bitmaps, -1, bitmapHandle);
/* 3321 */             if (ret != L_ERROR.SUCCESS.getValue())
/* 3322 */               ltkrn.FreeBitmap(bitmapHandle);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3329 */       ltkrn.FreeBitmapHandle(bitmapHandle);
/*      */     }
/* 3331 */     if (ret == L_ERROR.SUCCESS.getValue())
/*      */     {
/* 3334 */       this._page = fields.get("_page", 0);
/*      */ 
/* 3337 */       count = stream.readInt();
/* 3338 */       for (int i = 0; i < count; i++)
/*      */       {
/* 3340 */         RasterTagMetadata tag = (RasterTagMetadata)stream.readObject();
/* 3341 */         getTags().add(tag);
/*      */       }
/*      */ 
/* 3344 */       count = stream.readInt();
/* 3345 */       for (int i = 0; i < count; i++)
/*      */       {
/* 3347 */         RasterTagMetadata key = (RasterTagMetadata)stream.readObject();
/* 3348 */         getGeoKeys().add(key);
/*      */       }
/*      */ 
/* 3351 */       count = stream.readInt();
/* 3352 */       for (int i = 0; i < count; i++)
/*      */       {
/* 3354 */         RasterMarkerMetadata marker = (RasterMarkerMetadata)stream.readObject();
/* 3355 */         getMarkers().add(marker);
/*      */       }
/*      */ 
/* 3358 */       count = stream.readInt();
/* 3359 */       for (int i = 0; i < count; i++)
/*      */       {
/* 3361 */         RasterCommentMetadata comment = (RasterCommentMetadata)stream.readObject();
/* 3362 */         getComments().add(comment);
/*      */       }
/*      */ 
/* 3366 */       this._noFreeDataOnDispose = fields.get("_noFreeDataOnDispose", false);
/* 3367 */       this._animationGlobalLoop = fields.get("_animationGlobalLoop", 0);
/* 3368 */       this._animationGlobalSize = ((LeadSize)fields.get("_animationGlobalSize", null));
/* 3369 */       this._animationGlobalBackground = RasterColor.fromColorRef(fields.get("_animationGlobalBackground", 0));
/*      */     }
/*      */ 
/* 3372 */     updatePages(this._page);
/*      */ 
/* 3375 */     RasterException.checkErrorCode(ret);
/*      */   }
/*      */ 
/*      */   public boolean getAnimationWaitUserInput()
/*      */   {
/* 3380 */     return ltkrn.BITMAPHANDLE_getFlagsWaitUserInput(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public LeadPoint getAnimationOffset()
/*      */   {
/* 3385 */     return new LeadPoint(ltkrn.BITMAPHANDLE_getLeft(this._currentBitmapHandle), ltkrn.BITMAPHANDLE_getTop(this._currentBitmapHandle));
/*      */   }
/*      */ 
/*      */   public int getAnimationDelay()
/*      */   {
/* 3390 */     return ltkrn.BITMAPHANDLE_getDelay(this._currentBitmapHandle);
/*      */   }
/*      */ 
/*      */   public RasterColor getAnimationBackground()
/*      */   {
/* 3395 */     return RasterColor.fromColorRef(ltkrn.BITMAPHANDLE_getBackground(this._currentBitmapHandle));
/*      */   }
/*      */ 
/*      */   public void setAnimationBackground(RasterColor value) {
/* 3399 */     ltkrn.BITMAPHANDLE_setBackground(this._currentBitmapHandle, value.getColorRef());
/* 3400 */     updateCurrentBitmapHandle();
/* 3401 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.ANIMATION_PROPERTIES));
/*      */   }
/*      */ 
/*      */   public RasterImageAnimationDisposalMethod getAnimationDisposalMethod()
/*      */   {
/* 3406 */     return RasterImageAnimationDisposalMethod.forValue(ltkrn.BITMAPHANDLE_getDisposalMethod(this._currentBitmapHandle));
/*      */   }
/*      */ 
/*      */   public int getAnimationGlobalLoop()
/*      */   {
/* 3411 */     return this._animationGlobalLoop;
/*      */   }
/*      */ 
/*      */   public void setAnimationGlobalLoop(int value)
/*      */   {
/* 3416 */     if (value < -1) {
/* 3417 */       throw new ArgumentOutOfRangeException("AnimationGlobalLoop", value, "Animation global loop must be -1, 0 or a value greater than 0");
/*      */     }
/* 3419 */     this._animationGlobalLoop = value;
/* 3420 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.ANIMATION_PROPERTIES));
/*      */   }
/*      */ 
/*      */   public LeadSize getAnimationGlobalSize()
/*      */   {
/* 3425 */     return this._animationGlobalSize;
/*      */   }
/*      */ 
/*      */   public void setAnimationGlobalSize(LeadSize value)
/*      */   {
/* 3430 */     if ((value.getWidth() < 0) || (value.getHeight() < 0)) {
/* 3431 */       throw new ArgumentOutOfRangeException("AnimationGlobalSize", value.toString(), "Animation global width and height must greater or equal to 0");
/*      */     }
/* 3433 */     this._animationGlobalSize = value;
/* 3434 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.ANIMATION_PROPERTIES));
/*      */   }
/*      */ 
/*      */   public RasterColor getAnimationGlobalBackground()
/*      */   {
/* 3439 */     return this._animationGlobalBackground;
/*      */   }
/*      */ 
/*      */   public void setAnimationGlobalBackground(RasterColor value)
/*      */   {
/* 3444 */     this._animationGlobalBackground = value;
/* 3445 */     onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.ANIMATION_PROPERTIES));
/*      */   }
/*      */ 
/*      */   public RasterColor getTrueColorValue(RasterColor color)
/*      */   {
/* 3450 */     int colorRef = ltkrn.TranslateBitmapColor(this._currentBitmapHandle, this._currentBitmapHandle, color.getColorRef());
/*      */ 
/* 3453 */     if (((colorRef & 0x1000000) == 16777216) && (ltkrn.BITMAPHANDLE_getBitsPerPixel(this._currentBitmapHandle) <= 8))
/*      */     {
/* 3455 */       int index = colorRef & 0xFFFFFF;
/* 3456 */       if (index < ltkrn.BITMAPHANDLE_getColors(this._currentBitmapHandle))
/*      */       {
/* 3458 */         RasterColor[] RGB = new RasterColor[1];
/* 3459 */         int ret = ltkrn.GetBitmapColors(this._currentBitmapHandle, index, 1, RGB);
/* 3460 */         RasterException.checkErrorCode(ret);
/* 3461 */         return RGB[0];
/*      */       }
/*      */     }
/*      */ 
/* 3465 */     return RasterColor.fromColorRef(colorRef);
/*      */   }
/*      */ 
/*      */   public RasterImage createThumbnail(int thumbnailWidth, int thumbnailHeight, int bitsPerPixel, RasterViewPerspective viewPerspective, int sizeFlags)
/*      */   {
/* 3470 */     if (thumbnailWidth < 1)
/* 3471 */       throw new ArgumentOutOfRangeException("thumbnailWidth", thumbnailWidth, "Must be a value greater or equal to 1");
/* 3472 */     if (thumbnailHeight < 1) {
/* 3473 */       throw new ArgumentOutOfRangeException("thumbnailHeight", thumbnailHeight, "Must be a value greater or equal to 1");
/*      */     }
/*      */ 
/* 3476 */     RasterImage destinationImage = clone();
/*      */ 
/* 3479 */     if (destinationImage.getBitsPerPixel() != bitsPerPixel)
/*      */     {
/* 3481 */       ColorResolutionCommand colorResolutionCommand = new ColorResolutionCommand(ColorResolutionCommandMode.IN_PLACE, bitsPerPixel, RasterByteOrder.BGR, RasterDitheringMethod.CLUSTERED, ColorResolutionCommandPaletteFlags.OPTIMIZED.getValue(), null);
/* 3482 */       colorResolutionCommand.run(destinationImage);
/*      */     }
/*      */ 
/* 3486 */     if (destinationImage.getViewPerspective() != viewPerspective)
/*      */     {
/* 3488 */       ChangeViewPerspectiveCommand changeViewPerspectiveCommand = new ChangeViewPerspectiveCommand(true, viewPerspective);
/* 3489 */       changeViewPerspectiveCommand.run(destinationImage);
/*      */     }
/*      */ 
/* 3493 */     destinationImage.setXResolution(96);
/* 3494 */     destinationImage.setYResolution(96);
/*      */ 
/* 3498 */     double dpiFactor = 1.0D;
/* 3499 */     if ((getXResolution() != getYResolution()) && (getXResolution() > 0) && (getYResolution() > 0))
/*      */     {
/* 3501 */       dpiFactor = getXResolution() / getYResolution();
/*      */     }
/*      */ 
/* 3505 */     int imageWidth = getImageWidth();
/* 3506 */     int imageHeight = (int)(dpiFactor * getImageHeight() + 0.5D);
/*      */ 
/* 3508 */     if ((imageWidth > thumbnailWidth) || (imageHeight > thumbnailHeight))
/*      */     {
/*      */       double factor;
/* 3512 */       if (thumbnailWidth > thumbnailHeight)
/*      */       {
/* 3514 */         double factor = thumbnailWidth / imageWidth;
/* 3515 */         if (factor * imageHeight > thumbnailHeight)
/* 3516 */           factor = thumbnailHeight / imageHeight;
/*      */       }
/*      */       else
/*      */       {
/* 3520 */         factor = thumbnailHeight / imageHeight;
/* 3521 */         if (factor * imageWidth > thumbnailWidth) {
/* 3522 */           factor = thumbnailWidth / imageWidth;
/*      */         }
/*      */       }
/* 3525 */       int scaledImageWidth = (int)(factor * imageWidth);
/* 3526 */       int scaledImageHeight = (int)(factor * imageHeight);
/*      */ 
/* 3529 */       SizeCommand sizeCommand = new SizeCommand(scaledImageWidth, scaledImageHeight, sizeFlags);
/* 3530 */       sizeCommand.run(destinationImage);
/*      */     }
/*      */ 
/* 3533 */     return destinationImage;
/*      */   }
/*      */ 
/*      */   private int getIgnoreEventsCounter()
/*      */   {
/* 3540 */     this._readerWriterLock.readLock().lock();
/*      */     int ret;
/*      */     try
/*      */     {
/* 3543 */       ret = this._ignoreEventsCounter;
/*      */     }
/*      */     finally
/*      */     {
/* 3547 */       this._readerWriterLock.readLock().unlock();
/*      */     }
/*      */ 
/* 3550 */     return ret;
/*      */   }
/*      */ 
/*      */   public boolean isLoading()
/*      */   {
/* 3555 */     return this._isLoading;
/*      */   }
/*      */ 
/*      */   public void underlay(RasterImage underlayImage, RasterImageUnderlayFlags flags)
/*      */   {
/*      */     try
/*      */     {
/* 3562 */       int ret = ltkrn.UnderlayBitmap(this._currentBitmapHandle, underlayImage.getCurrentBitmapHandle(), flags.getValue());
/* 3563 */       RasterException.checkErrorCode(ret);
/*      */     }
/*      */     finally
/*      */     {
/* 3567 */       if (underlayImage != null)
/* 3568 */         underlayImage.updateCurrentBitmapHandle();
/* 3569 */       updateCurrentBitmapHandle();
/* 3570 */       onChanged(new RasterImageChangedEvent(this, RasterImageChangedFlags.DATA));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void addLoadCompletedListener(AsyncLoadCompletedListener listener)
/*      */   {
/* 3582 */     if (this._LoadCompleted.contains(listener)) {
/* 3583 */       return;
/*      */     }
/* 3585 */     this._LoadCompleted.addElement(listener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeLoadCompletedListener(AsyncLoadCompletedListener listener) {
/* 3589 */     if (!this._LoadCompleted.contains(listener)) {
/* 3590 */       return;
/*      */     }
/* 3592 */     this._LoadCompleted.removeElement(listener);
/*      */   }
/*      */ 
/*      */   private synchronized void fireLoadCompleted(AsyncCompletedEvent event) {
/* 3596 */     for (AsyncLoadCompletedListener listener : this._LoadCompleted)
/* 3597 */       listener.onAsyncLoadCompleted(event);
/*      */   }
/*      */ 
/*      */   public static RasterImage startLoading()
/*      */   {
/* 3602 */     RasterImage image = new RasterImage();
/* 3603 */     image._isLoading = true;
/* 3604 */     return image;
/*      */   }
/*      */ 
/*      */   public void finishLoad(AsyncCompletedEvent event) {
/* 3608 */     if (this._isDisposed)
/*      */     {
/* 3611 */       return;
/*      */     }
/*      */ 
/* 3614 */     if (!this._isLoading)
/*      */     {
/* 3617 */       throw new InvalidOperationException("Invalid image state");
/*      */     }
/*      */ 
/* 3621 */     this._isLoading = false;
/*      */ 
/* 3624 */     fireLoadCompleted(event);
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImage
 * JD-Core Version:    0.6.2
 */