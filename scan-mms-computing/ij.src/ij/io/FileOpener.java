/*     */ package ij.io;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.LookUpTable;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.ProgressBar;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.frame.ThresholdAdjuster;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.MemoryImageSource;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Properties;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ 
/*     */ public class FileOpener
/*     */ {
/*     */   private FileInfo fi;
/*     */   private int width;
/*     */   private int height;
/*  36 */   private static boolean showConflictMessage = true;
/*     */   private double minValue;
/*     */   private double maxValue;
/*     */   private static boolean silentMode;
/*     */ 
/*     */   public FileOpener(FileInfo fi)
/*     */   {
/*  41 */     this.fi = fi;
/*  42 */     if (fi != null) {
/*  43 */       this.width = fi.width;
/*  44 */       this.height = fi.height;
/*     */     }
/*  46 */     if (IJ.debugMode) IJ.log("FileInfo: " + fi);
/*     */   }
/*     */ 
/*     */   public void open()
/*     */   {
/*  51 */     open(true);
/*     */   }
/*     */ 
/*     */   public ImagePlus open(boolean show)
/*     */   {
/*  57 */     ImagePlus imp = null;
/*     */ 
/*  59 */     ProgressBar pb = null;
/*     */ 
/*  62 */     ColorModel cm = createColorModel(this.fi);
/*  63 */     if (this.fi.nImages > 1)
/*  64 */       return openStack(cm, show);
/*     */     Object pixels;
/*     */     ImageProcessor ip;
/*  65 */     switch (this.fi.fileType) {
/*     */     case 0:
/*     */     case 5:
/*     */     case 8:
/*  69 */       pixels = readPixels(this.fi);
/*  70 */       if (pixels == null) return null;
/*  71 */       ip = new ByteProcessor(this.width, this.height, (byte[])pixels, cm);
/*  72 */       imp = new ImagePlus(this.fi.fileName, ip);
/*  73 */       break;
/*     */     case 1:
/*     */     case 2:
/*     */     case 13:
/*  77 */       pixels = readPixels(this.fi);
/*  78 */       if (pixels == null) return null;
/*  79 */       ip = new ShortProcessor(this.width, this.height, (short[])pixels, cm);
/*  80 */       imp = new ImagePlus(this.fi.fileName, ip);
/*  81 */       break;
/*     */     case 3:
/*     */     case 4:
/*     */     case 11:
/*     */     case 14:
/*     */     case 16:
/*  87 */       pixels = readPixels(this.fi);
/*  88 */       if (pixels == null) return null;
/*  89 */       ip = new FloatProcessor(this.width, this.height, (float[])pixels, cm);
/*  90 */       imp = new ImagePlus(this.fi.fileName, ip);
/*  91 */       break;
/*     */     case 6:
/*     */     case 7:
/*     */     case 9:
/*     */     case 10:
/*     */     case 15:
/*     */     case 18:
/*  98 */       pixels = readPixels(this.fi);
/*  99 */       if (pixels == null) return null;
/* 100 */       ip = new ColorProcessor(this.width, this.height, (int[])pixels);
/* 101 */       imp = new ImagePlus(this.fi.fileName, ip);
/* 102 */       break;
/*     */     case 12:
/*     */     case 17:
/* 105 */       boolean planar = this.fi.fileType == 17;
/* 106 */       Object[] pixelArray = (Object[])readPixels(this.fi);
/* 107 */       if (pixelArray == null) return null;
/* 108 */       ImageStack stack = new ImageStack(this.width, this.height);
/* 109 */       stack.addSlice("Red", pixelArray[0]);
/* 110 */       stack.addSlice("Green", pixelArray[1]);
/* 111 */       stack.addSlice("Blue", pixelArray[2]);
/* 112 */       imp = new ImagePlus(this.fi.fileName, stack);
/* 113 */       imp.setDimensions(3, 1, 1);
/* 114 */       if (planar)
/* 115 */         imp.getProcessor().resetMinAndMax();
/* 116 */       imp.setFileInfo(this.fi);
/* 117 */       int mode = 1;
/* 118 */       if (this.fi.description != null) {
/* 119 */         if (this.fi.description.indexOf("mode=color") != -1)
/* 120 */           mode = 2;
/* 121 */         else if (this.fi.description.indexOf("mode=gray") != -1)
/* 122 */           mode = 3;
/*     */       }
/* 124 */       imp = new CompositeImage(imp, mode);
/* 125 */       if ((!planar) && (this.fi.displayRanges == null)) {
/* 126 */         for (int c = 1; c <= 3; c++) {
/* 127 */           imp.setPosition(c, 1, 1);
/* 128 */           imp.setDisplayRange(this.minValue, this.maxValue);
/*     */         }
/* 130 */         imp.setPosition(1, 1, 1);
/*     */       }
/*     */       break;
/*     */     }
/* 134 */     imp.setFileInfo(this.fi);
/* 135 */     setCalibration(imp);
/* 136 */     if (this.fi.info != null)
/* 137 */       imp.setProperty("Info", this.fi.info);
/* 138 */     if ((this.fi.sliceLabels != null) && (this.fi.sliceLabels.length == 1) && (this.fi.sliceLabels[0] != null))
/* 139 */       imp.setProperty("Label", this.fi.sliceLabels[0]);
/* 140 */     if (this.fi.roi != null)
/* 141 */       imp.setRoi(RoiDecoder.openFromByteArray(this.fi.roi));
/* 142 */     if (this.fi.overlay != null)
/* 143 */       setOverlay(imp, this.fi.overlay);
/* 144 */     if (show) imp.show();
/* 145 */     return imp;
/*     */   }
/*     */ 
/*     */   void setOverlay(ImagePlus imp, byte[][] rois) {
/* 149 */     Overlay overlay = new Overlay();
/* 150 */     for (int i = 0; i < rois.length; i++) {
/* 151 */       Roi roi = RoiDecoder.openFromByteArray(rois[i]);
/* 152 */       if (i == 0) {
/* 153 */         Overlay proto = roi.getPrototypeOverlay();
/* 154 */         overlay.drawLabels(proto.getDrawLabels());
/* 155 */         overlay.drawNames(proto.getDrawNames());
/* 156 */         overlay.drawBackgrounds(proto.getDrawBackgrounds());
/* 157 */         overlay.setLabelColor(proto.getLabelColor());
/* 158 */         overlay.setLabelFont(proto.getLabelFont());
/*     */       }
/* 160 */       overlay.add(roi);
/*     */     }
/* 162 */     imp.setOverlay(overlay);
/*     */   }
/*     */ 
/*     */   ImagePlus openStack(ColorModel cm, boolean show)
/*     */   {
/* 167 */     ImageStack stack = new ImageStack(this.fi.width, this.fi.height, cm);
/* 168 */     long skip = this.fi.getOffset();
/*     */     try
/*     */     {
/* 171 */       ImageReader reader = new ImageReader(this.fi);
/* 172 */       InputStream is = createInputStream(this.fi);
/* 173 */       if (is == null) return null;
/* 174 */       IJ.resetEscape();
/* 175 */       for (int i = 1; i <= this.fi.nImages; i++) {
/* 176 */         if (!silentMode)
/* 177 */           IJ.showStatus("Reading: " + i + "/" + this.fi.nImages);
/* 178 */         if (IJ.escapePressed()) {
/* 179 */           IJ.beep();
/* 180 */           IJ.showProgress(1.0D);
/* 181 */           silentMode = false;
/* 182 */           return null;
/*     */         }
/* 184 */         Object pixels = reader.readPixels(is, skip);
/* 185 */         if (pixels == null) break;
/* 186 */         stack.addSlice(null, pixels);
/* 187 */         skip = this.fi.gapBetweenImages;
/* 188 */         if (!silentMode)
/* 189 */           IJ.showProgress(i, this.fi.nImages);
/*     */       }
/* 191 */       is.close();
/*     */     }
/*     */     catch (Exception e) {
/* 194 */       IJ.log("" + e);
/*     */     }
/*     */     catch (OutOfMemoryError e) {
/* 197 */       IJ.outOfMemory(this.fi.fileName);
/* 198 */       stack.trim();
/*     */     }
/* 200 */     if (!silentMode) IJ.showProgress(1.0D);
/* 201 */     if (stack.getSize() == 0)
/* 202 */       return null;
/* 203 */     if ((this.fi.sliceLabels != null) && (this.fi.sliceLabels.length <= stack.getSize())) {
/* 204 */       for (int i = 0; i < this.fi.sliceLabels.length; i++)
/* 205 */         stack.setSliceLabel(this.fi.sliceLabels[i], i + 1);
/*     */     }
/* 207 */     ImagePlus imp = new ImagePlus(this.fi.fileName, stack);
/* 208 */     if (this.fi.info != null)
/* 209 */       imp.setProperty("Info", this.fi.info);
/* 210 */     if (this.fi.roi != null)
/* 211 */       imp.setRoi(RoiDecoder.openFromByteArray(this.fi.roi));
/* 212 */     if (this.fi.overlay != null)
/* 213 */       setOverlay(imp, this.fi.overlay);
/* 214 */     if (show) imp.show();
/* 215 */     imp.setFileInfo(this.fi);
/* 216 */     setCalibration(imp);
/* 217 */     ImageProcessor ip = imp.getProcessor();
/* 218 */     if (ip.getMin() == ip.getMax())
/* 219 */       setStackDisplayRange(imp);
/* 220 */     if (!silentMode) IJ.showProgress(1.0D);
/*     */ 
/* 222 */     return imp;
/*     */   }
/*     */ 
/*     */   void setStackDisplayRange(ImagePlus imp) {
/* 226 */     ImageStack stack = imp.getStack();
/* 227 */     double min = 1.7976931348623157E+308D;
/* 228 */     double max = -1.797693134862316E+308D;
/* 229 */     int n = stack.getSize();
/* 230 */     for (int i = 1; i <= n; i++) {
/* 231 */       if (!silentMode)
/* 232 */         IJ.showStatus("Calculating stack min and max: " + i + "/" + n);
/* 233 */       ImageProcessor ip = stack.getProcessor(i);
/* 234 */       ip.resetMinAndMax();
/* 235 */       if (ip.getMin() < min)
/* 236 */         min = ip.getMin();
/* 237 */       if (ip.getMax() > max)
/* 238 */         max = ip.getMax();
/*     */     }
/* 240 */     imp.getProcessor().setMinAndMax(min, max);
/* 241 */     imp.updateAndDraw();
/*     */   }
/*     */ 
/*     */   public void revertToSaved(ImagePlus imp)
/*     */   {
/* 248 */     String path = this.fi.directory + this.fi.fileName;
/*     */ 
/* 250 */     if (this.fi.fileFormat == 3)
/*     */     {
/* 252 */       Image img = Toolkit.getDefaultToolkit().createImage(path);
/* 253 */       imp.setImage(img);
/* 254 */       if (imp.getType() == 4)
/* 255 */         Opener.convertGrayJpegTo8Bits(imp);
/* 256 */       return;
/*     */     }
/*     */ 
/* 260 */     if (this.fi.fileFormat == 6)
/*     */     {
/* 262 */       ImagePlus imp2 = (ImagePlus)IJ.runPlugIn("ij.plugin.DICOM", path);
/* 263 */       if (imp2 != null)
/* 264 */         imp.setProcessor(null, imp2.getProcessor());
/* 265 */       if ((this.fi.fileType == 2) || (this.fi.fileType == 4))
/* 266 */         ThresholdAdjuster.update();
/* 267 */       return;
/*     */     }
/*     */ 
/* 270 */     if (this.fi.fileFormat == 5)
/*     */     {
/* 272 */       ImagePlus imp2 = (ImagePlus)IJ.runPlugIn("ij.plugin.BMP_Reader", path);
/* 273 */       if (imp2 != null)
/* 274 */         imp.setProcessor(null, imp2.getProcessor());
/* 275 */       return;
/*     */     }
/*     */ 
/* 278 */     if (this.fi.fileFormat == 8)
/*     */     {
/* 280 */       ImagePlus imp2 = (ImagePlus)IJ.runPlugIn("ij.plugin.PGM_Reader", path);
/* 281 */       if (imp2 != null)
/* 282 */         imp.setProcessor(null, imp2.getProcessor());
/* 283 */       return;
/*     */     }
/*     */ 
/* 286 */     if (this.fi.fileFormat == 4)
/*     */     {
/* 288 */       ImagePlus imp2 = (ImagePlus)IJ.runPlugIn("ij.plugin.FITS_Reader", path);
/* 289 */       if (imp2 != null)
/* 290 */         imp.setProcessor(null, imp2.getProcessor());
/* 291 */       return;
/*     */     }
/*     */ 
/* 294 */     if (this.fi.fileFormat == 7)
/*     */     {
/* 296 */       ImagePlus imp2 = new Opener().openZip(path);
/* 297 */       if (imp2 != null)
/* 298 */         imp.setProcessor(null, imp2.getProcessor());
/* 299 */       return;
/*     */     }
/*     */ 
/* 303 */     if (this.fi.fileFormat == 9) {
/* 304 */       ImagePlus imp2 = new Opener().openUsingImageIO(path);
/* 305 */       if (imp2 != null) imp.setProcessor(null, imp2.getProcessor());
/* 306 */       return;
/*     */     }
/*     */ 
/* 309 */     if (this.fi.nImages > 1) {
/* 310 */       return;
/*     */     }
/*     */ 
/* 313 */     if ((this.fi.url == null) || (this.fi.url.equals("")))
/* 314 */       IJ.showStatus("Loading: " + path);
/*     */     else
/* 316 */       IJ.showStatus("Loading: " + this.fi.url + this.fi.fileName);
/* 317 */     Object pixels = readPixels(this.fi);
/* 318 */     if (pixels == null) return;
/* 319 */     ColorModel cm = createColorModel(this.fi);
/*     */     ImageProcessor ip;
/* 320 */     switch (this.fi.fileType) {
/*     */     case 0:
/*     */     case 5:
/*     */     case 8:
/* 324 */       ip = new ByteProcessor(this.width, this.height, (byte[])pixels, cm);
/* 325 */       imp.setProcessor(null, ip);
/* 326 */       break;
/*     */     case 1:
/*     */     case 2:
/*     */     case 13:
/* 330 */       ip = new ShortProcessor(this.width, this.height, (short[])pixels, cm);
/* 331 */       imp.setProcessor(null, ip);
/* 332 */       break;
/*     */     case 3:
/*     */     case 4:
/* 335 */       ip = new FloatProcessor(this.width, this.height, (float[])pixels, cm);
/* 336 */       imp.setProcessor(null, ip);
/* 337 */       break;
/*     */     case 6:
/*     */     case 7:
/*     */     case 9:
/*     */     case 10:
/*     */     case 18:
/* 343 */       Image img = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(this.width, this.height, (int[])pixels, 0, this.width));
/* 344 */       imp.setImage(img);
/*     */     case 11:
/*     */     case 12:
/*     */     case 14:
/*     */     case 15:
/*     */     case 16:
/*     */     case 17: }  } 
/* 350 */   void setCalibration(ImagePlus imp) { if (this.fi.fileType == 1) {
/* 351 */       if (IJ.debugMode) IJ.log("16-bit signed");
/* 352 */       imp.getLocalCalibration().setSigned16BitCalibration();
/*     */     }
/* 354 */     Properties props = decodeDescriptionString(this.fi);
/* 355 */     Calibration cal = imp.getCalibration();
/* 356 */     boolean calibrated = false;
/* 357 */     if ((this.fi.pixelWidth > 0.0D) && (this.fi.unit != null)) {
/* 358 */       cal.pixelWidth = this.fi.pixelWidth;
/* 359 */       cal.pixelHeight = this.fi.pixelHeight;
/* 360 */       cal.pixelDepth = this.fi.pixelDepth;
/* 361 */       cal.setUnit(this.fi.unit);
/* 362 */       calibrated = true;
/*     */     }
/*     */ 
/* 365 */     if (this.fi.valueUnit != null) {
/* 366 */       int f = this.fi.calibrationFunction;
/* 367 */       if (((f >= 0) && (f <= 10) && (this.fi.coefficients != null)) || (f == 21))
/*     */       {
/* 369 */         boolean zeroClip = (props != null) && (props.getProperty("zeroclip", "false").equals("true"));
/* 370 */         cal.setFunction(f, this.fi.coefficients, this.fi.valueUnit, zeroClip);
/* 371 */         calibrated = true;
/*     */       }
/*     */     }
/*     */ 
/* 375 */     if (calibrated) {
/* 376 */       checkForCalibrationConflict(imp, cal);
/*     */     }
/* 378 */     if (this.fi.frameInterval != 0.0D) {
/* 379 */       cal.frameInterval = this.fi.frameInterval;
/*     */     }
/* 381 */     if (props == null) {
/* 382 */       return;
/*     */     }
/* 384 */     cal.xOrigin = getDouble(props, "xorigin");
/* 385 */     cal.yOrigin = getDouble(props, "yorigin");
/* 386 */     cal.zOrigin = getDouble(props, "zorigin");
/* 387 */     cal.info = props.getProperty("info");
/*     */ 
/* 389 */     cal.fps = getDouble(props, "fps");
/* 390 */     cal.loop = getBoolean(props, "loop");
/* 391 */     cal.frameInterval = getDouble(props, "finterval");
/* 392 */     cal.setTimeUnit(props.getProperty("tunit", "sec"));
/*     */ 
/* 394 */     double displayMin = getDouble(props, "min");
/* 395 */     double displayMax = getDouble(props, "max");
/* 396 */     if ((displayMin != 0.0D) || (displayMax != 0.0D)) {
/* 397 */       int type = imp.getType();
/* 398 */       ImageProcessor ip = imp.getProcessor();
/* 399 */       if ((type == 0) || (type == 3))
/* 400 */         ip.setMinAndMax(displayMin, displayMax);
/* 401 */       else if (((type == 1) || (type == 2)) && (
/* 402 */         (ip.getMin() != displayMin) || (ip.getMax() != displayMax))) {
/* 403 */         ip.setMinAndMax(displayMin, displayMax);
/*     */       }
/*     */     }
/*     */ 
/* 407 */     int stackSize = imp.getStackSize();
/* 408 */     if (stackSize > 1) {
/* 409 */       int channels = (int)getDouble(props, "channels");
/* 410 */       int slices = (int)getDouble(props, "slices");
/* 411 */       int frames = (int)getDouble(props, "frames");
/* 412 */       if (channels == 0) channels = 1;
/* 413 */       if (slices == 0) slices = 1;
/* 414 */       if (frames == 0) frames = 1;
/*     */ 
/* 416 */       if (channels * slices * frames == stackSize) {
/* 417 */         imp.setDimensions(channels, slices, frames);
/* 418 */         if (getBoolean(props, "hyperstack"))
/* 419 */           imp.setOpenAsHyperStack(true);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void checkForCalibrationConflict(ImagePlus imp, Calibration cal)
/*     */   {
/* 426 */     Calibration gcal = imp.getGlobalCalibration();
/* 427 */     if ((gcal == null) || (!showConflictMessage) || (IJ.isMacro()))
/* 428 */       return;
/* 429 */     if ((cal.pixelWidth == gcal.pixelWidth) && (cal.getUnit().equals(gcal.getUnit())))
/* 430 */       return;
/* 431 */     GenericDialog gd = new GenericDialog(imp.getTitle());
/* 432 */     gd.addMessage("The calibration of this image conflicts\nwith the current global calibration.");
/* 433 */     gd.addCheckbox("Disable_Global Calibration", true);
/* 434 */     gd.addCheckbox("Disable_these Messages", false);
/* 435 */     gd.showDialog();
/* 436 */     if (gd.wasCanceled()) return;
/* 437 */     boolean disable = gd.getNextBoolean();
/* 438 */     if (disable) {
/* 439 */       imp.setGlobalCalibration(null);
/* 440 */       imp.setCalibration(cal);
/* 441 */       WindowManager.repaintImageWindows();
/*     */     }
/* 443 */     boolean dontShow = gd.getNextBoolean();
/* 444 */     if (dontShow) showConflictMessage = false;
/*     */   }
/*     */ 
/*     */   public ColorModel createColorModel(FileInfo fi)
/*     */   {
/* 449 */     if ((fi.fileType == 5) && (fi.lutSize > 0)) {
/* 450 */       return new IndexColorModel(8, fi.lutSize, fi.reds, fi.greens, fi.blues);
/*     */     }
/* 452 */     return LookUpTable.createGrayscaleColorModel(fi.whiteIsZero);
/*     */   }
/*     */ 
/*     */   public InputStream createInputStream(FileInfo fi) throws IOException, MalformedURLException
/*     */   {
/* 457 */     InputStream is = null;
/* 458 */     boolean gzip = (fi.fileName != null) && ((fi.fileName.endsWith(".gz")) || (fi.fileName.endsWith(".GZ")));
/* 459 */     if (fi.inputStream != null) {
/* 460 */       is = fi.inputStream;
/* 461 */     } else if ((fi.url != null) && (!fi.url.equals(""))) {
/* 462 */       is = new URL(fi.url + fi.fileName).openStream();
/*     */     } else {
/* 464 */       if ((fi.directory.length() > 0) && (!fi.directory.endsWith(Prefs.separator)))
/* 465 */         fi.directory += Prefs.separator;
/* 466 */       File f = new File(fi.directory + fi.fileName);
/* 467 */       if (gzip) fi.compression = 0;
/* 468 */       if ((f == null) || (!f.exists()) || (f.isDirectory()) || (!validateFileInfo(f, fi)))
/* 469 */         is = null;
/*     */       else
/* 471 */         is = new FileInputStream(f);
/*     */     }
/* 473 */     if (is != null) {
/* 474 */       if (fi.compression >= 2)
/* 475 */         is = new RandomAccessStream(is);
/* 476 */       else if (gzip)
/* 477 */         is = new GZIPInputStream(is, 50000);
/*     */     }
/* 479 */     return is;
/*     */   }
/*     */ 
/*     */   static boolean validateFileInfo(File f, FileInfo fi) {
/* 483 */     long offset = fi.getOffset();
/* 484 */     long length = 0L;
/* 485 */     if ((fi.width <= 0) || (fi.height <= 0)) {
/* 486 */       error("Width or height <= 0.", fi, offset, length);
/* 487 */       return false;
/*     */     }
/* 489 */     if ((offset >= 0L) && (offset < 1000L))
/* 490 */       return true;
/* 491 */     if (offset < 0L) {
/* 492 */       error("Offset is negative.", fi, offset, length);
/* 493 */       return false;
/*     */     }
/* 495 */     if ((fi.fileType == 8) || (fi.compression != 1))
/* 496 */       return true;
/* 497 */     length = f.length();
/* 498 */     long size = fi.width * fi.height * fi.getBytesPerPixel();
/* 499 */     size = fi.nImages > 1 ? size : size / 4L;
/* 500 */     if (fi.height == 1) size = 0L;
/* 501 */     if (offset + size > length) {
/* 502 */       error("Offset + image size > file length.", fi, offset, length);
/* 503 */       return false;
/*     */     }
/* 505 */     return true;
/*     */   }
/*     */ 
/*     */   static void error(String msg, FileInfo fi, long offset, long length) {
/* 509 */     String msg2 = "FileInfo parameter error. \n" + msg + "\n \n" + "  Width: " + fi.width + "\n" + "  Height: " + fi.height + "\n" + "  Offset: " + offset + "\n" + "  Bytes/pixel: " + fi.getBytesPerPixel() + "\n" + (length > 0L ? "  File length: " + length + "\n" : "");
/*     */ 
/* 516 */     if (silentMode) {
/* 517 */       IJ.log("Error opening " + fi.directory + fi.fileName);
/* 518 */       IJ.log(msg2);
/*     */     } else {
/* 520 */       IJ.error("FileOpener", msg2);
/*     */     }
/*     */   }
/*     */ 
/*     */   Object readPixels(FileInfo fi)
/*     */   {
/* 526 */     Object pixels = null;
/*     */     try {
/* 528 */       InputStream is = createInputStream(fi);
/* 529 */       if (is == null)
/* 530 */         return null;
/* 531 */       ImageReader reader = new ImageReader(fi);
/* 532 */       pixels = reader.readPixels(is);
/* 533 */       this.minValue = reader.min;
/* 534 */       this.maxValue = reader.max;
/* 535 */       is.close();
/*     */     }
/*     */     catch (Exception e) {
/* 538 */       if (!"Macro canceled".equals(e.getMessage()))
/* 539 */         IJ.handleException(e);
/*     */     }
/* 541 */     return pixels;
/*     */   }
/*     */ 
/*     */   public Properties decodeDescriptionString(FileInfo fi) {
/* 545 */     if ((fi.description == null) || (fi.description.length() < 7))
/* 546 */       return null;
/* 547 */     if (IJ.debugMode)
/* 548 */       IJ.log("Image Description: " + new String(fi.description).replace('\n', ' '));
/* 549 */     if (!fi.description.startsWith("ImageJ"))
/* 550 */       return null;
/* 551 */     Properties props = new Properties();
/* 552 */     InputStream is = new ByteArrayInputStream(fi.description.getBytes());
/*     */     try { props.load(is); is.close(); } catch (IOException e) {
/* 554 */       return null;
/* 555 */     }fi.unit = props.getProperty("unit", "");
/* 556 */     Double n = getNumber(props, "cf");
/* 557 */     if (n != null) fi.calibrationFunction = n.intValue();
/* 558 */     double[] c = new double[5];
/* 559 */     int count = 0;
/* 560 */     for (int i = 0; i < 5; i++) {
/* 561 */       n = getNumber(props, "c" + i);
/* 562 */       if (n == null) break;
/* 563 */       c[i] = n.doubleValue();
/* 564 */       count++;
/*     */     }
/* 566 */     if (count >= 2) {
/* 567 */       fi.coefficients = new double[count];
/* 568 */       for (int i = 0; i < count; i++)
/* 569 */         fi.coefficients[i] = c[i];
/*     */     }
/* 571 */     fi.valueUnit = props.getProperty("vunit");
/* 572 */     n = getNumber(props, "images");
/* 573 */     if ((n != null) && (n.doubleValue() > 1.0D))
/* 574 */       fi.nImages = ((int)n.doubleValue());
/* 575 */     if (fi.nImages > 1) {
/* 576 */       double spacing = getDouble(props, "spacing");
/* 577 */       if (spacing != 0.0D) {
/* 578 */         if (spacing < 0.0D) spacing = -spacing;
/* 579 */         fi.pixelDepth = spacing;
/*     */       }
/*     */     }
/* 582 */     String name = props.getProperty("name");
/* 583 */     if (name != null)
/* 584 */       fi.fileName = name;
/* 585 */     return props;
/*     */   }
/*     */ 
/*     */   private Double getNumber(Properties props, String key) {
/* 589 */     String s = props.getProperty(key);
/* 590 */     if (s != null)
/*     */       try {
/* 592 */         return Double.valueOf(s);
/*     */       } catch (NumberFormatException e) {
/*     */       }
/* 595 */     return null;
/*     */   }
/*     */ 
/*     */   private double getDouble(Properties props, String key) {
/* 599 */     Double n = getNumber(props, key);
/* 600 */     return n != null ? n.doubleValue() : 0.0D;
/*     */   }
/*     */ 
/*     */   private boolean getBoolean(Properties props, String key) {
/* 604 */     String s = props.getProperty(key);
/* 605 */     return (s != null) && (s.equals("true"));
/*     */   }
/*     */ 
/*     */   public static void setShowConflictMessage(boolean b) {
/* 609 */     showConflictMessage = b;
/*     */   }
/*     */ 
/*     */   static void setSilentMode(boolean mode) {
/* 613 */     silentMode = mode;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.FileOpener
 * JD-Core Version:    0.6.2
 */