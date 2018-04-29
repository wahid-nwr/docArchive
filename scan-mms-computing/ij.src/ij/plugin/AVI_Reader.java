/*      */ package ij.plugin;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.ImageStack;
/*      */ import ij.VirtualStack;
/*      */ import ij.gui.GenericDialog;
/*      */ import ij.io.FileInfo;
/*      */ import ij.io.OpenDialog;
/*      */ import ij.process.ByteProcessor;
/*      */ import ij.process.ColorProcessor;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.process.ShortProcessor;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.util.Vector;
/*      */ import javax.imageio.ImageIO;
/*      */ 
/*      */ public class AVI_Reader extends VirtualStack
/*      */   implements PlugIn
/*      */ {
/*      */   private static final int FOURCC_RIFF = 1179011410;
/*      */   private static final int FOURCC_AVI = 541677121;
/*      */   private static final int FOURCC_LIST = 1414744396;
/*      */   private static final int FOURCC_hdrl = 1819436136;
/*      */   private static final int FOURCC_avih = 1751742049;
/*      */   private static final int FOURCC_strl = 1819440243;
/*      */   private static final int FOURCC_strh = 1752331379;
/*      */   private static final int FOURCC_strf = 1718776947;
/*      */   private static final int FOURCC_movi = 1769369453;
/*      */   private static final int FOURCC_rec = 543384946;
/*      */   private static final int FOURCC_JUNK = 1263424842;
/*      */   private static final int FOURCC_vids = 1935960438;
/*      */   private static final int FOURCC_00db = 1650733104;
/*      */   private static final int FOURCC_00dc = 1667510320;
/*      */   private static final int NO_COMPRESSION = 0;
/*      */   private static final int NO_COMPRESSION_RGB = 541214546;
/*      */   private static final int NO_COMPRESSION_RAW = 542589266;
/*      */   private static final int NO_COMPRESSION_Y800 = 808466521;
/*      */   private static final int NO_COMPRESSION_Y8 = 538982489;
/*      */   private static final int NO_COMPRESSION_GREY = 1497715271;
/*      */   private static final int NO_COMPRESSION_Y16 = 540422489;
/*      */   private static final int NO_COMPRESSION_MIL = 541870413;
/*      */   private static final int AYUV_COMPRESSION = 1448433985;
/*      */   private static final int UYVY_COMPRESSION = 1498831189;
/*      */   private static final int Y422_COMPRESSION = 1447975253;
/*      */   private static final int UYNV_COMPRESSION = 842151001;
/*      */   private static final int CYUV_COMPRESSION = 1987410275;
/*      */   private static final int V422_COMPRESSION = 842150998;
/*      */   private static final int YUY2_COMPRESSION = 844715353;
/*      */   private static final int YUNV_COMPRESSION = 1447974233;
/*      */   private static final int YUYV_COMPRESSION = 1448695129;
/*      */   private static final int YVYU_COMPRESSION = 1431918169;
/*      */   private static final int JPEG_COMPRESSION = 1734701162;
/*      */   private static final int JPEG_COMPRESSION2 = 1195724874;
/*      */   private static final int JPEG_COMPRESSION3 = 4;
/*      */   private static final int MJPG_COMPRESSION = 1196444237;
/*      */   private static final int PNG_COMPRESSION = 543649392;
/*      */   private static final int PNG_COMPRESSION2 = 541544016;
/*      */   private static final int PNG_COMPRESSION3 = 5;
/*      */   private static final int BITMASK24 = 65536;
/*      */   private static final long SIZE_MASK = 4294967295L;
/*  127 */   private static int firstFrameNumber = 1;
/*  128 */   private static int lastFrameNumber = 0;
/*      */   private static boolean convertToGray;
/*      */   private static boolean flipVertical;
/*      */   private static boolean isVirtual;
/*      */   private RandomAccessFile raFile;
/*      */   private String raFilePath;
/*      */   private boolean headerOK;
/*      */   private int streamNumber;
/*      */   private long fileSize;
/*      */   private int paddingGranularity;
/*      */   private int dataCompression;
/*      */   private int scanLineSize;
/*      */   private boolean dataTopDown;
/*      */   private ColorModel cm;
/*      */   private boolean variableLength;
/*      */   private Vector frameInfos;
/*      */   private ImageStack stack;
/*      */   private ImagePlus imp;
/*      */   private boolean verbose;
/*      */   private long startTime;
/*      */   private int dwMicroSecPerFrame;
/*      */   private int dwMaxBytesPerSec;
/*      */   private int dwReserved1;
/*      */   private int dwFlags;
/*      */   private int dwTotalFrames;
/*      */   private int dwInitialFrames;
/*      */   private int dwStreams;
/*      */   private int dwSuggestedBufferSize;
/*      */   private int dwWidth;
/*      */   private int dwHeight;
/*      */   private int fccStreamHandler;
/*      */   private int dwStreamFlags;
/*      */   private int dwPriorityLanguage;
/*      */   private int dwStreamInitialFrames;
/*      */   private int dwStreamScale;
/*      */   private int dwStreamRate;
/*      */   private int dwStreamStart;
/*      */   private int dwStreamLength;
/*      */   private int dwStreamSuggestedBufferSize;
/*      */   private int dwStreamQuality;
/*      */   private int dwStreamSampleSize;
/*      */   private int biSize;
/*      */   private int biWidth;
/*      */   private int biHeight;
/*      */   private short biPlanes;
/*      */   private short biBitCount;
/*      */   private int biCompression;
/*      */   private int biSizeImage;
/*      */   private int biXPelsPerMeter;
/*      */   private int biYPelsPerMeter;
/*      */   private int biClrUsed;
/*      */   private int biClrImportant;
/*      */ 
/*      */   public AVI_Reader()
/*      */   {
/*  135 */     this.headerOK = false;
/*      */ 
/*  138 */     this.fileSize = 0L;
/*  139 */     this.paddingGranularity = 2;
/*      */ 
/*  151 */     this.verbose = IJ.debugMode;
/*      */   }
/*      */ 
/*      */   public void run(String arg)
/*      */   {
/*  199 */     OpenDialog od = new OpenDialog("Select AVI File", arg);
/*  200 */     String fileName = od.getFileName();
/*  201 */     if (fileName == null) return;
/*  202 */     String fileDir = od.getDirectory();
/*  203 */     String path = fileDir + fileName;
/*      */     try {
/*  205 */       openAndReadHeader(path);
/*      */     } catch (Exception e) {
/*  207 */       IJ.showMessage("AVI Reader", exceptionMessage(e));
/*  208 */       return;
/*      */     }
/*  210 */     if (!showDialog(fileName)) return; try
/*      */     {
/*  212 */       stack = makeStack(path, firstFrameNumber, lastFrameNumber, isVirtual, convertToGray, flipVertical);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */       ImageStack stack;
/*  215 */       IJ.showMessage("AVI Reader", exceptionMessage(e));
/*  216 */       return;
/*      */     }
/*  218 */     if (this.stack == null) return;
/*  219 */     if (this.stack.getSize() == 0) {
/*  220 */       String rangeText = "";
/*  221 */       if ((firstFrameNumber > 1) || (lastFrameNumber != 0)) {
/*  222 */         rangeText = "\nin Range " + firstFrameNumber + (lastFrameNumber > 0 ? " - " + lastFrameNumber : " - end");
/*      */       }
/*  224 */       IJ.showMessage("AVI Reader", "Error: No Frames Found" + rangeText);
/*  225 */       return;
/*      */     }
/*  227 */     this.imp = new ImagePlus(fileName, this.stack);
/*  228 */     if (this.imp.getBitDepth() == 16)
/*  229 */       this.imp.getProcessor().resetMinAndMax();
/*  230 */     setFramesPerSecond(this.imp);
/*  231 */     FileInfo fi = new FileInfo();
/*  232 */     fi.fileName = fileName;
/*  233 */     fi.directory = fileDir;
/*  234 */     this.imp.setFileInfo(fi);
/*  235 */     if (arg.equals(""))
/*  236 */       this.imp.show();
/*  237 */     IJ.showTime(this.imp, this.startTime, "Read AVI in ", this.stack.getSize());
/*      */   }
/*      */ 
/*      */   public ImagePlus getImagePlus()
/*      */   {
/*  242 */     return this.imp;
/*      */   }
/*      */ 
/*      */   public ImageStack makeStack(String path, int firstFrameNumber, int lastFrameNumber, boolean isVirtual, boolean convertToGray, boolean flipVertical)
/*      */   {
/*  256 */     firstFrameNumber = firstFrameNumber;
/*  257 */     lastFrameNumber = lastFrameNumber;
/*  258 */     isVirtual = isVirtual;
/*  259 */     convertToGray = convertToGray;
/*  260 */     flipVertical = flipVertical;
/*  261 */     String exceptionMessage = null;
/*  262 */     IJ.showProgress(0.001D);
/*      */     try {
/*  264 */       readAVI(path);
/*      */     } catch (OutOfMemoryError e) {
/*  266 */       this.stack.trim();
/*  267 */       IJ.showMessage("AVI Reader", "Out of memory.  " + this.stack.getSize() + " of " + this.dwTotalFrames + " frames will be opened.");
/*      */     } catch (Exception e) {
/*  269 */       exceptionMessage = exceptionMessage(e);
/*      */     } finally {
/*      */       try {
/*  272 */         this.raFile.close();
/*  273 */         if (this.verbose)
/*  274 */           IJ.log("File closed."); 
/*      */       } catch (Exception e) {  }
/*      */ 
/*  276 */       IJ.showProgress(1.0D);
/*      */     }
/*  278 */     if (exceptionMessage != null) throw new RuntimeException(exceptionMessage);
/*  279 */     if ((isVirtual) && (this.frameInfos != null))
/*  280 */       this.stack = this;
/*  281 */     if ((this.stack != null) && (this.cm != null))
/*  282 */       this.stack.setColorModel(this.cm);
/*  283 */     return this.stack;
/*      */   }
/*      */ 
/*      */   public synchronized ImageProcessor getProcessor(int n)
/*      */   {
/*  290 */     if ((this.frameInfos == null) || (this.frameInfos.size() == 0) || (this.raFilePath == null))
/*  291 */       return null;
/*  292 */     if ((n < 1) || (n > this.frameInfos.size()))
/*  293 */       throw new IllegalArgumentException("Argument out of range: " + n);
/*  294 */     Object pixels = null;
/*  295 */     RandomAccessFile rFile = null;
/*  296 */     String exceptionMessage = null;
/*      */     try {
/*  298 */       rFile = new RandomAccessFile(new File(this.raFilePath), "r");
/*  299 */       long[] frameInfo = (long[])this.frameInfos.get(n - 1);
/*  300 */       pixels = readFrame(rFile, frameInfo[0], (int)frameInfo[1]);
/*      */     } catch (Exception e) {
/*  302 */       exceptionMessage = exceptionMessage(e);
/*      */     } finally {
/*      */       try {
/*  305 */         rFile.close(); } catch (Exception e) {
/*      */       }
/*      */     }
/*  308 */     if (exceptionMessage != null) throw new RuntimeException(exceptionMessage);
/*  309 */     if (pixels == null) return null;
/*  310 */     if ((pixels instanceof byte[]))
/*  311 */       return new ByteProcessor(this.dwWidth, this.biHeight, (byte[])pixels, this.cm);
/*  312 */     if ((pixels instanceof short[])) {
/*  313 */       return new ShortProcessor(this.dwWidth, this.biHeight, (short[])pixels, this.cm);
/*      */     }
/*  315 */     return new ColorProcessor(this.dwWidth, this.biHeight, (int[])pixels);
/*      */   }
/*      */ 
/*      */   public int getWidth()
/*      */   {
/*  320 */     return this.dwWidth;
/*      */   }
/*      */ 
/*      */   public int getHeight()
/*      */   {
/*  325 */     return this.biHeight;
/*      */   }
/*      */ 
/*      */   public int getSize()
/*      */   {
/*  330 */     if (this.frameInfos == null) return 0;
/*  331 */     return this.frameInfos.size();
/*      */   }
/*      */ 
/*      */   public String getSliceLabel(int n)
/*      */   {
/*  336 */     if ((this.frameInfos == null) || (n < 1) || (n > this.frameInfos.size()))
/*  337 */       throw new IllegalArgumentException("No Virtual Stack or argument out of range: " + n);
/*  338 */     return frameLabel(((long[])(long[])this.frameInfos.get(n - 1))[2]);
/*      */   }
/*      */ 
/*      */   public void deleteSlice(int n)
/*      */   {
/*  344 */     if ((this.frameInfos == null) || (this.frameInfos.size() == 0)) return;
/*  345 */     if ((n < 1) || (n > this.frameInfos.size()))
/*  346 */       throw new IllegalArgumentException("Argument out of range: " + n);
/*  347 */     this.frameInfos.removeElementAt(n - 1);
/*      */   }
/*      */ 
/*      */   private boolean showDialog(String fileName)
/*      */   {
/*  352 */     if (lastFrameNumber != -1)
/*  353 */       lastFrameNumber = this.dwTotalFrames;
/*  354 */     if (IJ.macroRunning()) {
/*  355 */       firstFrameNumber = 1;
/*  356 */       lastFrameNumber = this.dwTotalFrames;
/*      */     }
/*  358 */     GenericDialog gd = new GenericDialog("AVI Reader");
/*  359 */     gd.addNumericField("First Frame: ", firstFrameNumber, 0);
/*  360 */     gd.addNumericField("Last Frame: ", lastFrameNumber, 0, 6, "");
/*  361 */     gd.addCheckbox("Use Virtual Stack", isVirtual);
/*  362 */     gd.addCheckbox("Convert to Grayscale", convertToGray);
/*  363 */     gd.addCheckbox("Flip Vertical", flipVertical);
/*  364 */     gd.showDialog();
/*  365 */     if (gd.wasCanceled()) return false;
/*  366 */     firstFrameNumber = (int)gd.getNextNumber();
/*  367 */     lastFrameNumber = (int)gd.getNextNumber();
/*  368 */     isVirtual = gd.getNextBoolean();
/*  369 */     convertToGray = gd.getNextBoolean();
/*  370 */     flipVertical = gd.getNextBoolean();
/*  371 */     IJ.register(getClass());
/*  372 */     return true;
/*      */   }
/*      */ 
/*      */   private void readAVI(String path) throws Exception, IOException {
/*  376 */     if (!this.headerOK)
/*  377 */       openAndReadHeader(path);
/*  378 */     this.startTime += System.currentTimeMillis();
/*  379 */     findFourccAndRead(1769369453, true, this.fileSize, true);
/*      */   }
/*      */ 
/*      */   private void openAndReadHeader(String path)
/*      */     throws Exception, IOException
/*      */   {
/*  385 */     this.startTime = System.currentTimeMillis();
/*  386 */     if (this.verbose)
/*  387 */       IJ.log("OPEN AND READ AVI FILE HEADER " + timeString());
/*  388 */     File file = new File(path);
/*  389 */     this.raFile = new RandomAccessFile(file, "r");
/*  390 */     this.raFilePath = path;
/*  391 */     this.fileSize = this.raFile.length();
/*  392 */     int fileType = readInt();
/*  393 */     if (this.verbose)
/*  394 */       IJ.log("File header: File type='" + fourccString(fileType) + "' (should be 'RIFF')" + timeString());
/*  395 */     if (fileType != 1179011410)
/*  396 */       throw new Exception("Not an AVI file.");
/*  397 */     readInt();
/*  398 */     int riffType = readInt();
/*  399 */     if (this.verbose)
/*  400 */       IJ.log("File header: RIFF type='" + fourccString(riffType) + "' (should be 'AVI ')");
/*  401 */     if (riffType != 541677121)
/*  402 */       throw new Exception("Not an AVI file.");
/*  403 */     findFourccAndRead(1819436136, true, this.fileSize, true);
/*  404 */     this.startTime -= System.currentTimeMillis();
/*  405 */     this.headerOK = true;
/*      */   }
/*      */ 
/*      */   private long findFourccAndRead(int fourcc, boolean isList, long endPosition, boolean throwNotFoundException)
/*      */     throws Exception, IOException
/*      */   {
/*  414 */     boolean contentOk = false;
/*      */     long nextPos;
/*      */     do
/*      */     {
/*  416 */       int type = readType(endPosition);
/*  417 */       if (type == 0) {
/*  418 */         if (throwNotFoundException) {
/*  419 */           throw new Exception("Required item '" + fourccString(fourcc) + "' not found");
/*      */         }
/*  421 */         return -1L;
/*      */       }
/*  423 */       long size = readInt() & 0xFFFFFFFF;
/*  424 */       nextPos = this.raFile.getFilePointer() + size;
/*  425 */       boolean foundList = false;
/*  426 */       if ((isList) && (type == 1414744396)) {
/*  427 */         foundList = true;
/*  428 */         type = readInt();
/*      */       }
/*  430 */       if (this.verbose) {
/*  431 */         IJ.log("Searching for '" + fourccString(fourcc) + "', found " + (foundList ? "LIST '" : "'") + fourccString(type) + "' " + posSizeString(nextPos - size, size));
/*      */       }
/*  433 */       if (type == fourcc)
/*  434 */         contentOk = readContents(fourcc, nextPos);
/*  435 */       else if (this.verbose)
/*  436 */         IJ.log("Discarded '" + fourccString(type) + "': Contents does not fit");
/*  437 */       this.raFile.seek(nextPos);
/*  438 */       if (contentOk)
/*  439 */         return nextPos; 
/*      */     }
/*  440 */     while (!contentOk);
/*  441 */     return nextPos;
/*      */   }
/*      */ 
/*      */   private boolean readContents(int fourcc, long endPosition) throws Exception, IOException
/*      */   {
/*  446 */     switch (fourcc) {
/*      */     case 1819436136:
/*  448 */       findFourccAndRead(1751742049, false, endPosition, true);
/*  449 */       findFourccAndRead(1819440243, true, endPosition, true);
/*  450 */       return true;
/*      */     case 1751742049:
/*  452 */       readAviHeader();
/*  453 */       return true;
/*      */     case 1819440243:
/*  455 */       long nextPosition = findFourccAndRead(1752331379, false, endPosition, false);
/*  456 */       if (nextPosition < 0L) return false;
/*  457 */       findFourccAndRead(1718776947, false, endPosition, true);
/*  458 */       return true;
/*      */     case 1752331379:
/*  460 */       int streamType = readInt();
/*  461 */       if (streamType != 1935960438) {
/*  462 */         if (this.verbose)
/*  463 */           IJ.log("Non-video Stream '" + fourccString(streamType) + " skipped");
/*  464 */         this.streamNumber += 1;
/*  465 */         return false;
/*      */       }
/*  467 */       readStreamHeader();
/*  468 */       return true;
/*      */     case 1718776947:
/*  470 */       readBitMapInfo(endPosition);
/*  471 */       return true;
/*      */     case 1769369453:
/*  473 */       readMovieData(endPosition);
/*  474 */       return true;
/*      */     }
/*  476 */     throw new Exception("Program error, type " + fourccString(fourcc));
/*      */   }
/*      */ 
/*      */   void readAviHeader() throws Exception, IOException {
/*  480 */     this.dwMicroSecPerFrame = readInt();
/*  481 */     this.dwMaxBytesPerSec = readInt();
/*  482 */     this.dwReserved1 = readInt();
/*  483 */     this.dwFlags = readInt();
/*  484 */     this.dwTotalFrames = readInt();
/*  485 */     this.dwInitialFrames = readInt();
/*  486 */     this.dwStreams = readInt();
/*  487 */     this.dwSuggestedBufferSize = readInt();
/*  488 */     this.dwWidth = readInt();
/*  489 */     this.dwHeight = readInt();
/*      */ 
/*  492 */     if (this.verbose) {
/*  493 */       IJ.log("AVI HEADER (avih):" + timeString());
/*  494 */       IJ.log("   dwMicroSecPerFrame=" + this.dwMicroSecPerFrame);
/*  495 */       IJ.log("   dwMaxBytesPerSec=" + this.dwMaxBytesPerSec);
/*  496 */       IJ.log("   dwReserved1=" + this.dwReserved1);
/*  497 */       IJ.log("   dwFlags=" + this.dwFlags);
/*  498 */       IJ.log("   dwTotalFrames=" + this.dwTotalFrames);
/*  499 */       IJ.log("   dwInitialFrames=" + this.dwInitialFrames);
/*  500 */       IJ.log("   dwStreams=" + this.dwStreams);
/*  501 */       IJ.log("   dwSuggestedBufferSize=" + this.dwSuggestedBufferSize);
/*  502 */       IJ.log("   dwWidth=" + this.dwWidth);
/*  503 */       IJ.log("   dwHeight=" + this.dwHeight);
/*      */     }
/*      */   }
/*      */ 
/*      */   void readStreamHeader() throws Exception, IOException {
/*  508 */     this.fccStreamHandler = readInt();
/*  509 */     this.dwStreamFlags = readInt();
/*  510 */     this.dwPriorityLanguage = readInt();
/*  511 */     this.dwStreamInitialFrames = readInt();
/*  512 */     this.dwStreamScale = readInt();
/*  513 */     this.dwStreamRate = readInt();
/*  514 */     this.dwStreamStart = readInt();
/*  515 */     this.dwStreamLength = readInt();
/*  516 */     this.dwStreamSuggestedBufferSize = readInt();
/*  517 */     this.dwStreamQuality = readInt();
/*  518 */     this.dwStreamSampleSize = readInt();
/*      */ 
/*  520 */     if (this.verbose) {
/*  521 */       IJ.log("VIDEO STREAM HEADER (strh):");
/*  522 */       IJ.log("   fccStreamHandler='" + fourccString(this.fccStreamHandler) + "'");
/*  523 */       IJ.log("   dwStreamFlags=" + this.dwStreamFlags);
/*  524 */       IJ.log("   wPriority,wLanguage=" + this.dwPriorityLanguage);
/*  525 */       IJ.log("   dwStreamInitialFrames=" + this.dwStreamInitialFrames);
/*  526 */       IJ.log("   dwStreamScale=" + this.dwStreamScale);
/*  527 */       IJ.log("   dwStreamRate=" + this.dwStreamRate);
/*  528 */       IJ.log("   dwStreamStart=" + this.dwStreamStart);
/*  529 */       IJ.log("   dwStreamLength=" + this.dwStreamLength);
/*  530 */       IJ.log("   dwStreamSuggestedBufferSize=" + this.dwStreamSuggestedBufferSize);
/*  531 */       IJ.log("   dwStreamQuality=" + this.dwStreamQuality);
/*  532 */       IJ.log("   dwStreamSampleSize=" + this.dwStreamSampleSize);
/*      */     }
/*  534 */     if (this.dwStreamSampleSize > 1)
/*  535 */       throw new Exception("Video stream with " + this.dwStreamSampleSize + " (more than 1) frames/chunk not supported");
/*      */   }
/*      */ 
/*      */   void readBitMapInfo(long endPosition)
/*      */     throws Exception, IOException
/*      */   {
/*  541 */     this.biSize = readInt();
/*  542 */     this.biWidth = readInt();
/*  543 */     this.biHeight = readInt();
/*  544 */     this.biPlanes = readShort();
/*  545 */     this.biBitCount = readShort();
/*  546 */     this.biCompression = readInt();
/*  547 */     this.biSizeImage = readInt();
/*  548 */     this.biXPelsPerMeter = readInt();
/*  549 */     this.biYPelsPerMeter = readInt();
/*  550 */     this.biClrUsed = readInt();
/*  551 */     this.biClrImportant = readInt();
/*  552 */     if (this.verbose) {
/*  553 */       IJ.log("   biSize=" + this.biSize);
/*  554 */       IJ.log("   biWidth=" + this.biWidth);
/*  555 */       IJ.log("   biHeight=" + this.biHeight);
/*  556 */       IJ.log("   biPlanes=" + this.biPlanes);
/*  557 */       IJ.log("   biBitCount=" + this.biBitCount);
/*  558 */       IJ.log("   biCompression=0x" + Integer.toHexString(this.biCompression) + " '" + fourccString(this.biCompression) + "'");
/*  559 */       IJ.log("   biSizeImage=" + this.biSizeImage);
/*  560 */       IJ.log("   biXPelsPerMeter=" + this.biXPelsPerMeter);
/*  561 */       IJ.log("   biYPelsPerMeter=" + this.biYPelsPerMeter);
/*  562 */       IJ.log("   biClrUsed=" + this.biClrUsed);
/*  563 */       IJ.log("   biClrImportant=" + this.biClrImportant);
/*      */     }
/*      */ 
/*  566 */     int allowedBitCount = 0;
/*  567 */     boolean readPalette = false;
/*  568 */     switch (this.biCompression) {
/*      */     case 0:
/*      */     case 541214546:
/*      */     case 542589266:
/*  572 */       this.dataCompression = 0;
/*  573 */       this.dataTopDown = (this.biHeight < 0);
/*  574 */       allowedBitCount = 65576;
/*  575 */       readPalette = this.biBitCount <= 8;
/*  576 */       break;
/*      */     case 538982489:
/*      */     case 808466521:
/*      */     case 1497715271:
/*  580 */       this.dataTopDown = true;
/*  581 */       this.dataCompression = 0;
/*  582 */       allowedBitCount = 8;
/*  583 */       break;
/*      */     case 540422489:
/*      */     case 541870413:
/*  586 */       this.dataCompression = 0;
/*  587 */       allowedBitCount = 16;
/*  588 */       break;
/*      */     case 1448433985:
/*  590 */       this.dataCompression = 1448433985;
/*  591 */       allowedBitCount = 32;
/*  592 */       break;
/*      */     case 842151001:
/*      */     case 1498831189:
/*  595 */       this.dataTopDown = true;
/*      */     case 842150998:
/*      */     case 1987410275:
/*  598 */       this.dataCompression = 1498831189;
/*  599 */       allowedBitCount = 16;
/*  600 */       break;
/*      */     case 844715353:
/*      */     case 1447974233:
/*      */     case 1448695129:
/*  604 */       this.dataTopDown = true;
/*  605 */       this.dataCompression = 844715353;
/*  606 */       allowedBitCount = 16;
/*  607 */       break;
/*      */     case 1431918169:
/*  609 */       this.dataTopDown = true;
/*  610 */       this.dataCompression = 1431918169;
/*  611 */       allowedBitCount = 16;
/*  612 */       break;
/*      */     case 4:
/*      */     case 1195724874:
/*      */     case 1196444237:
/*      */     case 1734701162:
/*  617 */       this.dataCompression = 1734701162;
/*  618 */       this.variableLength = true;
/*  619 */       break;
/*      */     case 5:
/*      */     case 541544016:
/*      */     case 543649392:
/*  623 */       this.variableLength = true;
/*  624 */       this.dataCompression = 543649392;
/*  625 */       break;
/*      */     default:
/*  627 */       throw new Exception("Unsupported compression: " + Integer.toHexString(this.biCompression) + (this.biCompression >= 538976288 ? " '" + fourccString(this.biCompression) + "'" : ""));
/*      */     }
/*      */ 
/*  631 */     int bitCountTest = this.biBitCount == 24 ? 65536 : this.biBitCount;
/*  632 */     if ((allowedBitCount != 0) && ((bitCountTest & allowedBitCount) == 0)) {
/*  633 */       throw new Exception("Unsupported: " + this.biBitCount + " bits/pixel for compression '" + fourccString(this.biCompression) + "'");
/*      */     }
/*      */ 
/*  636 */     if (this.biHeight < 0) {
/*  637 */       this.biHeight = (-this.biHeight);
/*      */     }
/*      */ 
/*  640 */     this.scanLineSize = ((this.biWidth * this.biBitCount + 31) / 32 * 4);
/*      */ 
/*  643 */     if ((readPalette) && (this.biClrUsed == 0)) {
/*  644 */       this.biClrUsed = (1 << this.biBitCount);
/*      */     }
/*  646 */     if (this.verbose) {
/*  647 */       IJ.log("   > data compression=0x" + Integer.toHexString(this.dataCompression) + " '" + fourccString(this.dataCompression) + "'");
/*  648 */       IJ.log("   > palette colors=" + this.biClrUsed);
/*  649 */       IJ.log("   > scan line size=" + this.scanLineSize);
/*  650 */       IJ.log("   > data top down=" + this.dataTopDown);
/*      */     }
/*      */ 
/*  654 */     if (readPalette) {
/*  655 */       long spaceForPalette = endPosition - this.raFile.getFilePointer();
/*  656 */       if (this.verbose)
/*  657 */         IJ.log("   Reading " + this.biClrUsed + " Palette colors: " + posSizeString(spaceForPalette));
/*  658 */       if (spaceForPalette < this.biClrUsed * 4)
/*  659 */         throw new Exception("Not enough data (" + spaceForPalette + ") for palette of size " + this.biClrUsed * 4);
/*  660 */       byte[] pr = new byte[this.biClrUsed];
/*  661 */       byte[] pg = new byte[this.biClrUsed];
/*  662 */       byte[] pb = new byte[this.biClrUsed];
/*  663 */       for (int i = 0; i < this.biClrUsed; i++) {
/*  664 */         pb[i] = this.raFile.readByte();
/*  665 */         pg[i] = this.raFile.readByte();
/*  666 */         pr[i] = this.raFile.readByte();
/*  667 */         this.raFile.readByte();
/*      */       }
/*  669 */       this.cm = new IndexColorModel(this.biBitCount, this.biClrUsed, pr, pg, pb);
/*      */     }
/*      */   }
/*      */ 
/*      */   void readMovieData(long endPosition) throws Exception, IOException
/*      */   {
/*  675 */     if (this.verbose) {
/*  676 */       IJ.log("MOVIE DATA " + posSizeString(endPosition - this.raFile.getFilePointer()) + timeString());
/*      */     }
/*  678 */     int type0xdb = 1650733104 + (this.streamNumber << 8);
/*  679 */     int type0xdc = 1667510320 + (this.streamNumber << 8);
/*  680 */     if (this.verbose) {
/*  681 */       IJ.log("Searching for stream " + this.streamNumber + ": '" + fourccString(type0xdb) + "' or '" + fourccString(type0xdc) + "' chunks");
/*      */     }
/*  683 */     int lastFrameToRead = 2147483647;
/*  684 */     if (lastFrameNumber > 0)
/*  685 */       lastFrameToRead = lastFrameNumber;
/*  686 */     if ((lastFrameNumber < 0) && (this.dwTotalFrames > 0))
/*  687 */       lastFrameToRead = this.dwTotalFrames + lastFrameNumber;
/*  688 */     if (isVirtual)
/*  689 */       this.frameInfos = new Vector(100);
/*      */     else
/*  691 */       this.stack = new ImageStack(this.dwWidth, this.biHeight);
/*  692 */     int frameNumber = 1;
/*      */     while (true) {
/*  694 */       int type = readType(endPosition);
/*  695 */       if (type == 0) break;
/*  696 */       long size = readInt() & 0xFFFFFFFF;
/*  697 */       long pos = this.raFile.getFilePointer();
/*  698 */       long nextPos = pos + size;
/*  699 */       if (((type == type0xdb) || (type == type0xdc)) && (size > 0L)) {
/*  700 */         updateProgress();
/*  701 */         if (this.verbose)
/*  702 */           IJ.log("movie data '" + fourccString(type) + "' " + posSizeString(size) + timeString());
/*  703 */         if (frameNumber >= firstFrameNumber) {
/*  704 */           if (isVirtual) {
/*  705 */             this.frameInfos.add(new long[] { pos, size, frameNumber * this.dwMicroSecPerFrame });
/*      */           } else {
/*  707 */             Object pixels = readFrame(this.raFile, pos, (int)size);
/*  708 */             String label = frameLabel(frameNumber * this.dwMicroSecPerFrame);
/*  709 */             this.stack.addSlice(label, pixels);
/*      */           }
/*      */         }
/*  712 */         frameNumber++;
/*  713 */         if (frameNumber > lastFrameToRead) break; 
/*      */       }
/*  714 */       else if (this.verbose) {
/*  715 */         IJ.log("skipped '" + fourccString(type) + "' " + posSizeString(size));
/*  716 */       }if (nextPos > endPosition) break;
/*  717 */       this.raFile.seek(nextPos);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object readFrame(RandomAccessFile rFile, long filePos, int size)
/*      */     throws Exception, IOException
/*      */   {
/*  724 */     rFile.seek(filePos);
/*  725 */     if (this.variableLength) {
/*  726 */       return readCompressedFrame(rFile, size);
/*      */     }
/*  728 */     return readFixedLengthFrame(rFile, size);
/*      */   }
/*      */ 
/*      */   private Object readCompressedFrame(RandomAccessFile rFile, int size)
/*      */     throws Exception, IOException
/*      */   {
/*  736 */     InputStream inputStream = new raInputStream(rFile, size, this.biCompression == 1196444237);
/*  737 */     BufferedImage bi = ImageIO.read(inputStream);
/*  738 */     int type = bi.getType();
/*  739 */     ImageProcessor ip = null;
/*      */ 
/*  741 */     if (type == 10) {
/*  742 */       ip = new ByteProcessor(bi);
/*  743 */     } else if (type == 13) {
/*  744 */       this.cm = bi.getColorModel();
/*  745 */       ip = new ByteProcessor(bi);
/*      */     } else {
/*  747 */       ip = new ColorProcessor(bi);
/*  748 */     }if (convertToGray)
/*  749 */       ip = ip.convertToByte(false);
/*  750 */     if (flipVertical)
/*  751 */       ip.flipVertical();
/*  752 */     return ip.getPixels();
/*      */   }
/*      */ 
/*      */   private Object readFixedLengthFrame(RandomAccessFile rFile, int size)
/*      */     throws Exception, IOException
/*      */   {
/*  759 */     if (size < this.scanLineSize * this.biHeight)
/*  760 */       throw new Exception("Data chunk size " + size + " too short (" + this.scanLineSize * this.biHeight + " required)");
/*  761 */     byte[] rawData = new byte[size];
/*  762 */     int n = rFile.read(rawData, 0, size);
/*  763 */     if (n < rawData.length)
/*  764 */       throw new Exception("Frame ended prematurely after " + n + " bytes");
/*  765 */     boolean topDown = flipVertical ? false : !this.dataTopDown ? true : this.dataTopDown;
/*  766 */     Object pixels = null;
/*  767 */     byte[] bPixels = null;
/*  768 */     int[] cPixels = null;
/*  769 */     short[] sPixels = null;
/*  770 */     if ((this.biBitCount <= 8) || (convertToGray)) {
/*  771 */       bPixels = new byte[this.dwWidth * this.biHeight];
/*  772 */       pixels = bPixels;
/*  773 */     } else if ((this.biBitCount == 16) && (this.dataCompression == 0)) {
/*  774 */       sPixels = new short[this.dwWidth * this.biHeight];
/*  775 */       pixels = sPixels;
/*      */     } else {
/*  777 */       cPixels = new int[this.dwWidth * this.biHeight];
/*  778 */       pixels = cPixels;
/*      */     }
/*  780 */     int offset = topDown ? 0 : (this.biHeight - 1) * this.dwWidth;
/*  781 */     int rawOffset = 0;
/*  782 */     for (int i = this.biHeight - 1; i >= 0; i--) {
/*  783 */       if (this.biBitCount <= 8)
/*  784 */         unpack8bit(rawData, rawOffset, bPixels, offset, this.dwWidth);
/*  785 */       else if (convertToGray)
/*  786 */         unpackGray(rawData, rawOffset, bPixels, offset, this.dwWidth);
/*  787 */       else if ((this.biBitCount == 16) && (this.dataCompression == 0))
/*  788 */         unpackShort(rawData, rawOffset, sPixels, offset, this.dwWidth);
/*      */       else
/*  790 */         unpack(rawData, rawOffset, cPixels, offset, this.dwWidth);
/*  791 */       rawOffset += this.scanLineSize;
/*  792 */       offset += (topDown ? this.dwWidth : -this.dwWidth);
/*      */     }
/*  794 */     return pixels;
/*      */   }
/*      */ 
/*      */   void unpack8bit(byte[] rawData, int rawOffset, byte[] pixels, int byteOffset, int w)
/*      */   {
/*  799 */     for (int i = 0; i < w; i++)
/*  800 */       pixels[(byteOffset + i)] = rawData[(rawOffset + i)];
/*      */   }
/*      */ 
/*      */   void unpackGray(byte[] rawData, int rawOffset, byte[] pixels, int byteOffset, int w)
/*      */   {
/*  805 */     int j = byteOffset;
/*  806 */     int k = rawOffset;
/*  807 */     if (this.dataCompression == 0) {
/*  808 */       for (int i = 0; i < w; i++) {
/*  809 */         int b0 = rawData[(k++)] & 0xFF;
/*  810 */         int b1 = rawData[(k++)] & 0xFF;
/*  811 */         int b2 = rawData[(k++)] & 0xFF;
/*  812 */         if (this.biBitCount == 32) k++;
/*  813 */         pixels[(j++)] = ((byte)(b0 * 934 + b1 * 4809 + b2 * 2449 + 4096 >> 13));
/*      */       }
/*      */     } else {
/*  816 */       if ((this.dataCompression == 1498831189) || (this.dataCompression == 1448433985))
/*  817 */         k++;
/*  818 */       int step = this.dataCompression == 1448433985 ? 4 : 2;
/*  819 */       for (int i = 0; i < w; i++) {
/*  820 */         pixels[(j++)] = rawData[k];
/*  821 */         k += step;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void unpackShort(byte[] rawData, int rawOffset, short[] pixels, int shortOffset, int w)
/*      */   {
/*  828 */     int j = shortOffset;
/*  829 */     int k = rawOffset;
/*  830 */     for (int i = 0; i < w; i++)
/*  831 */       pixels[(j++)] = ((short)(rawData[(k++)] & 0xFF | (rawData[(k++)] & 0xFF) << 8));
/*      */   }
/*      */ 
/*      */   void unpack(byte[] rawData, int rawOffset, int[] pixels, int intOffset, int w)
/*      */   {
/*  837 */     int j = intOffset;
/*  838 */     int k = rawOffset;
/*  839 */     switch (this.dataCompression) {
/*      */     case 0:
/*  841 */       for (int i = 0; i < w; i++) {
/*  842 */         int b0 = rawData[(k++)] & 0xFF;
/*  843 */         int b1 = (rawData[(k++)] & 0xFF) << 8;
/*  844 */         int b2 = (rawData[(k++)] & 0xFF) << 16;
/*  845 */         if (this.biBitCount == 32) k++;
/*  846 */         pixels[(j++)] = (0xFF000000 | b0 | b1 | b2);
/*      */       }
/*  848 */       break;
/*      */     case 844715353:
/*  850 */       for (int i = 0; i < w / 2; i++) {
/*  851 */         int y0 = rawData[(k++)] & 0xFF;
/*  852 */         int u = rawData[(k++)] ^ 0xFFFFFF80;
/*  853 */         int y1 = rawData[(k++)] & 0xFF;
/*  854 */         int v = rawData[(k++)] ^ 0xFFFFFF80;
/*  855 */         writeRGBfromYUV(y0, u, v, pixels, j++);
/*  856 */         writeRGBfromYUV(y1, u, v, pixels, j++);
/*      */       }
/*  858 */       break;
/*      */     case 1498831189:
/*  860 */       for (int i = 0; i < w / 2; i++) {
/*  861 */         int u = rawData[(k++)] ^ 0xFFFFFF80;
/*  862 */         int y0 = rawData[(k++)] & 0xFF;
/*  863 */         int v = rawData[(k++)] ^ 0xFFFFFF80;
/*  864 */         int y1 = rawData[(k++)] & 0xFF;
/*  865 */         writeRGBfromYUV(y0, u, v, pixels, j++);
/*  866 */         writeRGBfromYUV(y1, u, v, pixels, j++);
/*      */       }
/*  868 */       break;
/*      */     case 1431918169:
/*  870 */       for (int i = 0; i < w / 2; i++) {
/*  871 */         int y0 = rawData[(k++)] & 0xFF;
/*  872 */         int v = rawData[(k++)] ^ 0xFFFFFF80;
/*  873 */         int y1 = rawData[(k++)] & 0xFF;
/*  874 */         int u = rawData[(k++)] ^ 0xFFFFFF80;
/*  875 */         writeRGBfromYUV(y0, u, v, pixels, j++);
/*  876 */         writeRGBfromYUV(y1, u, v, pixels, j++);
/*      */       }
/*  878 */       break;
/*      */     case 1448433985:
/*  880 */       for (int i = 0; i < w; i++) {
/*  881 */         k++;
/*  882 */         int y = rawData[(k++)] & 0xFF;
/*  883 */         int v = rawData[(k++)] ^ 0xFFFFFF80;
/*  884 */         int u = rawData[(k++)] ^ 0xFFFFFF80;
/*  885 */         writeRGBfromYUV(y, u, v, pixels, j++);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void writeRGBfromYUV(int y, int u, int v, int[] pixels, int intArrayIndex)
/*      */   {
/*  900 */     int r = 9535 * y + 13074 * v - 148464 >> 13;
/*  901 */     int g = 9535 * y - 6660 * v - 3203 * u - 148464 >> 13;
/*  902 */     int b = 9535 * y + 16531 * u - 148464 >> 13;
/*  903 */     if (r > 255) r = 255; if (r < 0) r = 0;
/*  904 */     if (g > 255) g = 255; if (g < 0) g = 0;
/*  905 */     if (b > 255) b = 255; if (b < 0) b = 0;
/*  906 */     pixels[intArrayIndex] = (0xFF000000 | r << 16 | g << 8 | b);
/*      */   }
/*      */ 
/*      */   int readInt()
/*      */     throws IOException
/*      */   {
/*  912 */     int result = 0;
/*  913 */     for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
/*  914 */       result |= (this.raFile.readByte() & 0xFF) << shiftBy;
/*  915 */     return result;
/*      */   }
/*      */ 
/*      */   short readShort()
/*      */     throws IOException
/*      */   {
/*  921 */     int low = this.raFile.readByte() & 0xFF;
/*  922 */     int high = this.raFile.readByte() & 0xFF;
/*  923 */     return (short)(high << 8 | low);
/*      */   }
/*      */ 
/*      */   private int readType(long endPosition) throws IOException
/*      */   {
/*      */     while (true)
/*      */     {
/*  930 */       long pos = this.raFile.getFilePointer();
/*  931 */       if (pos % this.paddingGranularity != 0L) {
/*  932 */         pos = (pos / this.paddingGranularity + 1L) * this.paddingGranularity;
/*  933 */         this.raFile.seek(pos);
/*      */       }
/*  935 */       if (pos >= endPosition) return 0;
/*  936 */       int type = readInt();
/*  937 */       if (type != 1263424842)
/*  938 */         return type;
/*  939 */       long size = readInt() & 0xFFFFFFFF;
/*  940 */       if (this.verbose)
/*  941 */         IJ.log("Skip JUNK: " + posSizeString(size));
/*  942 */       this.raFile.seek(this.raFile.getFilePointer() + size);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setFramesPerSecond(ImagePlus imp) {
/*  947 */     if ((this.dwMicroSecPerFrame < 1000) && (this.dwStreamRate > 0))
/*  948 */       this.dwMicroSecPerFrame = ((int)(this.dwStreamScale * 1000000.0D / this.dwStreamRate));
/*  949 */     if (this.dwMicroSecPerFrame >= 1000)
/*  950 */       imp.getCalibration().fps = (1000000.0D / this.dwMicroSecPerFrame);
/*      */   }
/*      */ 
/*      */   private String frameLabel(long timeMicroSec) {
/*  954 */     return IJ.d2s(timeMicroSec / 1000000.0D) + " s";
/*      */   }
/*      */ 
/*      */   private String posSizeString(long size) throws IOException {
/*  958 */     return posSizeString(this.raFile.getFilePointer(), size);
/*      */   }
/*      */ 
/*      */   private String posSizeString(long pos, long size) throws IOException {
/*  962 */     return "0x" + Long.toHexString(pos) + "-0x" + Long.toHexString(pos + size - 1L) + " (" + size + " Bytes)";
/*      */   }
/*      */ 
/*      */   private String timeString() {
/*  966 */     return " (t=" + (System.currentTimeMillis() - this.startTime) + " ms)";
/*      */   }
/*      */ 
/*      */   private String fourccString(int fourcc)
/*      */   {
/*  971 */     String s = "";
/*  972 */     for (int i = 0; i < 4; i++) {
/*  973 */       int c = fourcc & 0xFF;
/*  974 */       s = s + Character.toString((char)c);
/*  975 */       fourcc >>= 8;
/*      */     }
/*  977 */     return s;
/*      */   }
/*      */ 
/*      */   private String exceptionMessage(Exception e)
/*      */   {
/*      */     String msg;
/*      */     String msg;
/*  982 */     if (e.getClass() == Exception.class)
/*  983 */       msg = e.getMessage();
/*      */     else
/*  985 */       msg = e + "\n" + e.getStackTrace()[0] + "\n" + e.getStackTrace()[1];
/*  986 */     if (msg.indexOf("Huffman table") != -1) {
/*  987 */       return "Cannot open M_JPEG AVIs that are missing Huffman tables";
/*      */     }
/*  989 */     return "An error occurred reading the file.\n \n" + msg;
/*      */   }
/*      */ 
/*      */   void updateProgress() throws IOException {
/*  993 */     IJ.showProgress(this.raFile.getFilePointer() / this.fileSize);
/*      */   }
/*      */ 
/*      */   class raInputStream extends InputStream
/*      */   {
/*      */     RandomAccessFile rFile;
/*      */     int readableSize;
/*      */     boolean fixMJPG;
/*      */ 
/*      */     raInputStream(RandomAccessFile rFile, int readableSize, boolean fixMJPG)
/*      */     {
/* 1009 */       this.rFile = rFile;
/* 1010 */       this.readableSize = readableSize;
/* 1011 */       this.fixMJPG = fixMJPG;
/*      */     }
/*      */     public int available() {
/* 1014 */       return this.readableSize;
/*      */     }
/*      */ 
/*      */     public int read()
/*      */       throws IOException
/*      */     {
/* 1020 */       this.fixMJPG = false;
/* 1021 */       return this.rFile.read();
/*      */     }
/*      */     public int read(byte[] b) throws IOException {
/* 1024 */       return read(b, 0, b.length);
/*      */     }
/*      */     public int read(byte[] b, int off, int len) throws IOException {
/* 1027 */       int nBytes = this.rFile.read(b, off, len);
/* 1028 */       if (this.fixMJPG) {
/* 1029 */         doFixMJPG(b, nBytes);
/* 1030 */         this.fixMJPG = false;
/*      */       }
/* 1032 */       return nBytes;
/*      */     }
/*      */ 
/*      */     private void doFixMJPG(byte[] b, int len)
/*      */     {
/* 1039 */       if ((readShort(b, 0) != 65496) || (len < 6)) return;
/* 1040 */       int offset = 2;
/*      */       while (true) {
/* 1042 */         int code = readShort(b, offset);
/* 1043 */         if ((code == 65498) || (code == 65497)) return;
/* 1044 */         if (code == 65506) {
/* 1045 */           b[(offset + 1)] = -60;
/* 1046 */           return;
/*      */         }
/* 1048 */         offset += 2;
/* 1049 */         int segmentLength = readShort(b, offset);
/* 1050 */         offset += segmentLength;
/* 1051 */         if ((offset > len - 4) || (segmentLength < 0)) return; 
/*      */       }
/*      */     }
/*      */ 
/* 1055 */     private int readShort(byte[] b, int offset) { return (b[offset] & 0xFF) << 8 | b[(offset + 1)] & 0xFF; }
/*      */ 
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.AVI_Reader
 * JD-Core Version:    0.6.2
 */