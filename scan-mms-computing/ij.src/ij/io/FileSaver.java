/*     */ package ij.io;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.LookUpTable;
/*     */ import ij.Prefs;
/*     */ import ij.VirtualStack;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.JpegWriter;
/*     */ import ij.plugin.Orthogonal_Views;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.LUT;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ 
/*     */ public class FileSaver
/*     */ {
/*     */   public static final int DEFAULT_JPEG_QUALITY = 85;
/*     */   private static int jpegQuality;
/*  24 */   private static String defaultDirectory = null;
/*     */   private ImagePlus imp;
/*     */   private FileInfo fi;
/*     */   private String name;
/*     */   private String directory;
/*     */   private boolean saveName;
/*     */ 
/*     */   public FileSaver(ImagePlus imp)
/*     */   {
/*  33 */     this.imp = imp;
/*  34 */     this.fi = imp.getFileInfo();
/*     */   }
/*     */ 
/*     */   public boolean save()
/*     */   {
/*  41 */     FileInfo ofi = null;
/*  42 */     if (this.imp != null) ofi = this.imp.getOriginalFileInfo();
/*  43 */     boolean validName = (ofi != null) && (this.imp.getTitle().equals(ofi.fileName));
/*  44 */     if ((validName) && (ofi.fileFormat == 2) && (ofi.directory != null) && (!ofi.directory.equals("")) && ((ofi.url == null) || (ofi.url.equals("")))) {
/*  45 */       this.name = this.imp.getTitle();
/*  46 */       this.directory = ofi.directory;
/*  47 */       String path = this.directory + this.name;
/*  48 */       File f = new File(path);
/*  49 */       if ((f == null) || (!f.exists()))
/*  50 */         return saveAsTiff();
/*  51 */       if (!IJ.isMacro()) {
/*  52 */         GenericDialog gd = new GenericDialog("Save as TIFF");
/*  53 */         gd.addMessage("\"" + ofi.fileName + "\" already exists.\nDo you want to replace it?");
/*  54 */         gd.setOKLabel("Replace");
/*  55 */         gd.showDialog();
/*  56 */         if (gd.wasCanceled())
/*  57 */           return false;
/*     */       }
/*  59 */       IJ.showStatus("Saving " + path);
/*  60 */       if (this.imp.getStackSize() > 1) {
/*  61 */         IJ.saveAs(this.imp, "tif", path);
/*  62 */         return true;
/*     */       }
/*  64 */       return saveAsTiff(path);
/*     */     }
/*  66 */     return saveAsTiff();
/*     */   }
/*     */ 
/*     */   String getPath(String type, String extension) {
/*  70 */     this.name = this.imp.getTitle();
/*  71 */     SaveDialog sd = new SaveDialog("Save as " + type, this.name, extension);
/*  72 */     this.name = sd.getFileName();
/*  73 */     if (this.name == null)
/*  74 */       return null;
/*  75 */     this.directory = sd.getDirectory();
/*  76 */     this.imp.startTiming();
/*  77 */     String path = this.directory + this.name;
/*  78 */     return path;
/*     */   }
/*     */ 
/*     */   public boolean saveAsTiff()
/*     */   {
/*  84 */     String path = getPath("TIFF", ".tif");
/*  85 */     if (path == null)
/*  86 */       return false;
/*  87 */     if (this.fi.nImages > 1) {
/*  88 */       return saveAsTiffStack(path);
/*     */     }
/*  90 */     return saveAsTiff(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsTiff(String path)
/*     */   {
/*  95 */     this.fi.nImages = 1;
/*  96 */     Object info = this.imp.getProperty("Info");
/*  97 */     if ((info != null) && ((info instanceof String)))
/*  98 */       this.fi.info = ((String)info);
/*  99 */     Object label = this.imp.getProperty("Label");
/* 100 */     if ((label != null) && ((label instanceof String))) {
/* 101 */       this.fi.sliceLabels = new String[1];
/* 102 */       this.fi.sliceLabels[0] = ((String)label);
/*     */     }
/* 104 */     this.fi.description = getDescriptionString();
/* 105 */     this.fi.roi = RoiEncoder.saveAsByteArray(this.imp.getRoi());
/* 106 */     this.fi.overlay = getOverlay(this.imp);
/*     */     try {
/* 108 */       TiffEncoder file = new TiffEncoder(this.fi);
/* 109 */       DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
/* 110 */       file.write(out);
/* 111 */       out.close();
/*     */     } catch (IOException e) {
/* 113 */       showErrorMessage(e);
/* 114 */       return false;
/*     */     }
/* 116 */     updateImp(this.fi, 2);
/* 117 */     return true;
/*     */   }
/*     */ 
/*     */   byte[][] getOverlay(ImagePlus imp) {
/* 121 */     if (imp.getHideOverlay())
/* 122 */       return (byte[][])null;
/* 123 */     Overlay overlay = imp.getOverlay();
/* 124 */     if (overlay == null) {
/* 125 */       ImageCanvas ic = imp.getCanvas();
/* 126 */       if (ic == null) return (byte[][])null;
/* 127 */       overlay = ic.getShowAllList();
/* 128 */       if (overlay == null) return (byte[][])null;
/*     */     }
/* 130 */     int n = overlay.size();
/* 131 */     if (n == 0) return (byte[][])null;
/* 132 */     if (Orthogonal_Views.isOrthoViewsImage(imp))
/* 133 */       return (byte[][])null;
/* 134 */     byte[][] array = new byte[n][];
/* 135 */     for (int i = 0; i < overlay.size(); i++) {
/* 136 */       Roi roi = overlay.get(i);
/* 137 */       if (i == 0)
/* 138 */         roi.setPrototypeOverlay(overlay);
/* 139 */       array[i] = RoiEncoder.saveAsByteArray(roi);
/*     */     }
/* 141 */     return array;
/*     */   }
/*     */ 
/*     */   public boolean saveAsTiffStack(String path)
/*     */   {
/* 146 */     if (this.fi.nImages == 1) {
/* 147 */       IJ.error("This is not a stack"); return false;
/* 148 */     }boolean virtualStack = this.imp.getStack().isVirtual();
/* 149 */     if (virtualStack)
/* 150 */       this.fi.virtualStack = ((VirtualStack)this.imp.getStack());
/* 151 */     Object info = this.imp.getProperty("Info");
/* 152 */     if ((info != null) && ((info instanceof String)))
/* 153 */       this.fi.info = ((String)info);
/* 154 */     this.fi.description = getDescriptionString();
/* 155 */     if (virtualStack) {
/* 156 */       FileInfo fi = this.imp.getOriginalFileInfo();
/* 157 */       if ((path != null) && (path.equals(fi.directory + fi.fileName))) {
/* 158 */         IJ.error("TIFF virtual stacks cannot be saved in place.");
/* 159 */         return false;
/*     */       }
/* 161 */       String[] labels = null;
/* 162 */       ImageStack vs = this.imp.getStack();
/* 163 */       for (int i = 1; i <= vs.getSize(); i++) {
/* 164 */         ImageProcessor ip = vs.getProcessor(i);
/* 165 */         String label = vs.getSliceLabel(i);
/* 166 */         if ((i == 1) && ((label == null) || (label.length() < 200))) break;
/* 167 */         if (labels == null) labels = new String[vs.getSize()];
/* 168 */         labels[(i - 1)] = label;
/*     */       }
/* 170 */       fi.sliceLabels = labels;
/*     */     } else {
/* 172 */       this.fi.sliceLabels = this.imp.getStack().getSliceLabels();
/* 173 */     }this.fi.roi = RoiEncoder.saveAsByteArray(this.imp.getRoi());
/* 174 */     this.fi.overlay = getOverlay(this.imp);
/* 175 */     if (this.imp.isComposite()) saveDisplayRangesAndLuts(this.imp, this.fi); try
/*     */     {
/* 177 */       TiffEncoder file = new TiffEncoder(this.fi);
/* 178 */       DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
/* 179 */       file.write(out);
/* 180 */       out.close();
/*     */     }
/*     */     catch (IOException e) {
/* 183 */       showErrorMessage(e);
/* 184 */       return false;
/*     */     }
/* 186 */     updateImp(this.fi, 2);
/* 187 */     return true;
/*     */   }
/*     */ 
/*     */   public byte[] serialize()
/*     */   {
/* 193 */     if (this.imp.getStack().isVirtual())
/* 194 */       return null;
/* 195 */     Object info = this.imp.getProperty("Info");
/* 196 */     if ((info != null) && ((info instanceof String)))
/* 197 */       this.fi.info = ((String)info);
/* 198 */     this.saveName = true;
/* 199 */     this.fi.description = getDescriptionString();
/* 200 */     this.saveName = false;
/* 201 */     this.fi.sliceLabels = this.imp.getStack().getSliceLabels();
/* 202 */     this.fi.roi = RoiEncoder.saveAsByteArray(this.imp.getRoi());
/* 203 */     this.fi.overlay = getOverlay(this.imp);
/* 204 */     if (this.imp.isComposite()) saveDisplayRangesAndLuts(this.imp, this.fi);
/* 205 */     ByteArrayOutputStream out = null;
/*     */     try {
/* 207 */       TiffEncoder encoder = new TiffEncoder(this.fi);
/* 208 */       out = new ByteArrayOutputStream();
/* 209 */       encoder.write(out);
/* 210 */       out.close();
/*     */     } catch (IOException e) {
/* 212 */       return null;
/*     */     }
/* 214 */     return out.toByteArray();
/*     */   }
/*     */ 
/*     */   void saveDisplayRangesAndLuts(ImagePlus imp, FileInfo fi) {
/* 218 */     CompositeImage ci = (CompositeImage)imp;
/* 219 */     int channels = imp.getNChannels();
/* 220 */     fi.displayRanges = new double[channels * 2];
/* 221 */     for (int i = 1; i <= channels; i++) {
/* 222 */       LUT lut = ci.getChannelLut(i);
/* 223 */       fi.displayRanges[((i - 1) * 2)] = lut.min;
/* 224 */       fi.displayRanges[((i - 1) * 2 + 1)] = lut.max;
/*     */     }
/* 226 */     if (ci.hasCustomLuts()) {
/* 227 */       fi.channelLuts = new byte[channels][];
/* 228 */       for (int i = 0; i < channels; i++) {
/* 229 */         LUT lut = ci.getChannelLut(i + 1);
/* 230 */         byte[] bytes = lut.getBytes();
/* 231 */         if (bytes == null) {
/* 232 */           fi.channelLuts = ((byte[][])null); break;
/* 233 */         }fi.channelLuts[i] = bytes;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean saveAsZip()
/*     */   {
/* 241 */     String path = getPath("TIFF/ZIP", ".zip");
/* 242 */     if (path == null) {
/* 243 */       return false;
/*     */     }
/* 245 */     return saveAsZip(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsZip(String path)
/*     */   {
/* 251 */     if (!path.endsWith(".zip"))
/* 252 */       path = path + ".zip";
/* 253 */     if (this.name == null)
/* 254 */       this.name = this.imp.getTitle();
/* 255 */     if (this.name.endsWith(".zip"))
/* 256 */       this.name = this.name.substring(0, this.name.length() - 4);
/* 257 */     if (!this.name.endsWith(".tif"))
/* 258 */       this.name += ".tif";
/* 259 */     this.fi.description = getDescriptionString();
/* 260 */     Object info = this.imp.getProperty("Info");
/* 261 */     if ((info != null) && ((info instanceof String)))
/* 262 */       this.fi.info = ((String)info);
/* 263 */     this.fi.roi = RoiEncoder.saveAsByteArray(this.imp.getRoi());
/* 264 */     this.fi.overlay = getOverlay(this.imp);
/* 265 */     this.fi.sliceLabels = this.imp.getStack().getSliceLabels();
/* 266 */     if (this.imp.isComposite()) saveDisplayRangesAndLuts(this.imp, this.fi);
/* 267 */     if ((this.fi.nImages > 1) && (this.imp.getStack().isVirtual()))
/* 268 */       this.fi.virtualStack = ((VirtualStack)this.imp.getStack());
/*     */     try {
/* 270 */       ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path));
/* 271 */       DataOutputStream out = new DataOutputStream(new BufferedOutputStream(zos));
/* 272 */       zos.putNextEntry(new ZipEntry(this.name));
/* 273 */       TiffEncoder te = new TiffEncoder(this.fi);
/* 274 */       te.write(out);
/* 275 */       out.close();
/*     */     }
/*     */     catch (IOException e) {
/* 278 */       showErrorMessage(e);
/* 279 */       return false;
/*     */     }
/* 281 */     updateImp(this.fi, 2);
/* 282 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean okForGif(ImagePlus imp) {
/* 286 */     int type = imp.getType();
/* 287 */     if (type == 4) {
/* 288 */       IJ.error("To save as Gif, the image must be converted to \"8-bit Color\".");
/* 289 */       return false;
/*     */     }
/* 291 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAsGif()
/*     */   {
/* 298 */     if (!okForGif(this.imp))
/* 299 */       return false;
/* 300 */     String path = getPath("GIF", ".gif");
/* 301 */     if (path == null) {
/* 302 */       return false;
/*     */     }
/* 304 */     return saveAsGif(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsGif(String path)
/*     */   {
/* 310 */     if (!okForGif(this.imp)) return false;
/* 311 */     IJ.runPlugIn(this.imp, "ij.plugin.GifWriter", path);
/* 312 */     updateImp(this.fi, 3);
/* 313 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean okForJpeg(ImagePlus imp)
/*     */   {
/* 318 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAsJpeg()
/*     */   {
/* 327 */     String type = "JPEG (" + getJpegQuality() + ")";
/* 328 */     String path = getPath(type, ".jpg");
/* 329 */     if (path == null) {
/* 330 */       return false;
/*     */     }
/* 332 */     return saveAsJpeg(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsJpeg(String path)
/*     */   {
/* 340 */     String err = JpegWriter.save(this.imp, path, jpegQuality);
/* 341 */     if ((err == null) && (this.imp.getType() != 1) && (this.imp.getType() != 2))
/* 342 */       updateImp(this.fi, 3);
/* 343 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAsBmp()
/*     */   {
/* 349 */     String path = getPath("BMP", ".bmp");
/* 350 */     if (path == null) {
/* 351 */       return false;
/*     */     }
/* 353 */     return saveAsBmp(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsBmp(String path)
/*     */   {
/* 358 */     IJ.runPlugIn(this.imp, "ij.plugin.BMP_Writer", path);
/* 359 */     updateImp(this.fi, 5);
/* 360 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAsPgm()
/*     */   {
/* 369 */     String extension = this.imp.getBitDepth() == 24 ? ".pnm" : ".pgm";
/* 370 */     String path = getPath("PGM", extension);
/* 371 */     if (path == null) {
/* 372 */       return false;
/*     */     }
/* 374 */     return saveAsPgm(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsPgm(String path)
/*     */   {
/* 381 */     IJ.runPlugIn(this.imp, "ij.plugin.PNM_Writer", path);
/* 382 */     updateImp(this.fi, 8);
/* 383 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAsPng()
/*     */   {
/* 389 */     String path = getPath("PNG", ".png");
/* 390 */     if (path == null) {
/* 391 */       return false;
/*     */     }
/* 393 */     return saveAsPng(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsPng(String path)
/*     */   {
/* 398 */     IJ.runPlugIn(this.imp, "ij.plugin.PNG_Writer", path);
/* 399 */     updateImp(this.fi, 9);
/* 400 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAsFits()
/*     */   {
/* 406 */     if (!okForFits(this.imp)) return false;
/* 407 */     String path = getPath("FITS", ".fits");
/* 408 */     if (path == null) {
/* 409 */       return false;
/*     */     }
/* 411 */     return saveAsFits(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsFits(String path)
/*     */   {
/* 416 */     if (!okForFits(this.imp)) return false;
/* 417 */     IJ.runPlugIn(this.imp, "ij.plugin.FITS_Writer", path);
/* 418 */     updateImp(this.fi, 4);
/* 419 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean okForFits(ImagePlus imp) {
/* 423 */     if (imp.getBitDepth() == 24) {
/* 424 */       IJ.error("FITS Writer", "Grayscale image required");
/* 425 */       return false;
/*     */     }
/* 427 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAsRaw()
/*     */   {
/* 433 */     String path = getPath("Raw", ".raw");
/* 434 */     if (path == null)
/* 435 */       return false;
/* 436 */     if (this.imp.getStackSize() == 1) {
/* 437 */       return saveAsRaw(path);
/*     */     }
/* 439 */     return saveAsRawStack(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsRaw(String path)
/*     */   {
/* 445 */     this.fi.nImages = 1;
/* 446 */     this.fi.intelByteOrder = Prefs.intelByteOrder;
/* 447 */     boolean signed16Bit = false;
/* 448 */     short[] pixels = null;
/* 449 */     int n = 0;
/*     */     try {
/* 451 */       signed16Bit = this.imp.getCalibration().isSigned16Bit();
/* 452 */       if (signed16Bit) {
/* 453 */         pixels = (short[])this.imp.getProcessor().getPixels();
/* 454 */         n = this.imp.getWidth() * this.imp.getHeight();
/* 455 */         for (int i = 0; i < n; i++)
/* 456 */           pixels[i] = ((short)(pixels[i] - 32768));
/*     */       }
/* 458 */       ImageWriter file = new ImageWriter(this.fi);
/* 459 */       OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
/* 460 */       file.write(out);
/* 461 */       out.close();
/*     */     }
/*     */     catch (IOException e) {
/* 464 */       showErrorMessage(e);
/* 465 */       return false;
/*     */     }
/* 467 */     if (signed16Bit) {
/* 468 */       for (int i = 0; i < n; i++)
/* 469 */         pixels[i] = ((short)(pixels[i] + 32768));
/*     */     }
/* 471 */     updateImp(this.fi, 1);
/* 472 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAsRawStack(String path)
/*     */   {
/* 477 */     if (this.fi.nImages == 1) {
/* 478 */       IJ.write("This is not a stack"); return false;
/* 479 */     }this.fi.intelByteOrder = Prefs.intelByteOrder;
/* 480 */     boolean signed16Bit = false;
/* 481 */     Object[] stack = null;
/* 482 */     int n = 0;
/* 483 */     boolean virtualStack = (this.imp.getStackSize() > 1) && (this.imp.getStack().isVirtual());
/* 484 */     if (virtualStack) {
/* 485 */       this.fi.virtualStack = ((VirtualStack)this.imp.getStack());
/* 486 */       if (this.imp.getProperty("AnalyzeFormat") != null) this.fi.fileName = "FlipTheseImages"; 
/*     */     }
/*     */     try
/*     */     {
/* 489 */       signed16Bit = this.imp.getCalibration().isSigned16Bit();
/* 490 */       if ((signed16Bit) && (!virtualStack)) {
/* 491 */         stack = (Object[])this.fi.pixels;
/* 492 */         n = this.imp.getWidth() * this.imp.getHeight();
/* 493 */         for (int slice = 0; slice < this.fi.nImages; slice++) {
/* 494 */           short[] pixels = (short[])stack[slice];
/* 495 */           for (int i = 0; i < n; i++)
/* 496 */             pixels[i] = ((short)(pixels[i] - 32768));
/*     */         }
/*     */       }
/* 499 */       ImageWriter file = new ImageWriter(this.fi);
/* 500 */       OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
/* 501 */       file.write(out);
/* 502 */       out.close();
/*     */     }
/*     */     catch (IOException e) {
/* 505 */       showErrorMessage(e);
/* 506 */       return false;
/*     */     }
/* 508 */     if (signed16Bit) {
/* 509 */       for (int slice = 0; slice < this.fi.nImages; slice++) {
/* 510 */         short[] pixels = (short[])stack[slice];
/* 511 */         for (int i = 0; i < n; i++)
/* 512 */           pixels[i] = ((short)(pixels[i] + 32768));
/*     */       }
/*     */     }
/* 515 */     updateImp(this.fi, 1);
/* 516 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAsText()
/*     */   {
/* 522 */     String path = getPath("Text", ".txt");
/* 523 */     if (path == null)
/* 524 */       return false;
/* 525 */     return saveAsText(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsText(String path)
/*     */   {
/*     */     try {
/* 531 */       Calibration cal = this.imp.getCalibration();
/* 532 */       int precision = Analyzer.getPrecision();
/* 533 */       int measurements = Analyzer.getMeasurements();
/* 534 */       boolean scientificNotation = (measurements & 0x200000) != 0;
/* 535 */       if (scientificNotation)
/* 536 */         precision = -precision;
/* 537 */       TextEncoder file = new TextEncoder(this.imp.getProcessor(), cal, precision);
/* 538 */       DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
/* 539 */       file.write(out);
/* 540 */       out.close();
/*     */     }
/*     */     catch (IOException e) {
/* 543 */       showErrorMessage(e);
/* 544 */       return false;
/*     */     }
/* 546 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean saveAsLut()
/*     */   {
/* 552 */     if (this.imp.getType() == 4) {
/* 553 */       IJ.error("RGB Images do not have a LUT.");
/* 554 */       return false;
/*     */     }
/* 556 */     String path = getPath("LUT", ".lut");
/* 557 */     if (path == null)
/* 558 */       return false;
/* 559 */     return saveAsLut(path);
/*     */   }
/*     */ 
/*     */   public boolean saveAsLut(String path)
/*     */   {
/* 564 */     LookUpTable lut = this.imp.createLut();
/* 565 */     int mapSize = lut.getMapSize();
/* 566 */     if (mapSize == 0) {
/* 567 */       IJ.error("RGB Images do not have a LUT.");
/* 568 */       return false;
/*     */     }
/* 570 */     if (mapSize < 256) {
/* 571 */       IJ.error("Cannot save LUTs with less than 256 entries.");
/* 572 */       return false;
/*     */     }
/* 574 */     byte[] reds = lut.getReds();
/* 575 */     byte[] greens = lut.getGreens();
/* 576 */     byte[] blues = lut.getBlues();
/* 577 */     byte[] pixels = new byte[768];
/* 578 */     for (int i = 0; i < 256; i++) {
/* 579 */       pixels[i] = reds[i];
/* 580 */       pixels[(i + 256)] = greens[i];
/* 581 */       pixels[(i + 512)] = blues[i];
/*     */     }
/* 583 */     FileInfo fi = new FileInfo();
/* 584 */     fi.width = 768;
/* 585 */     fi.height = 1;
/* 586 */     fi.pixels = pixels;
/*     */     try
/*     */     {
/* 589 */       ImageWriter file = new ImageWriter(fi);
/* 590 */       OutputStream out = new FileOutputStream(path);
/* 591 */       file.write(out);
/* 592 */       out.close();
/*     */     }
/*     */     catch (IOException e) {
/* 595 */       showErrorMessage(e);
/* 596 */       return false;
/*     */     }
/* 598 */     return true;
/*     */   }
/*     */ 
/*     */   private void updateImp(FileInfo fi, int fileFormat) {
/* 602 */     this.imp.changes = false;
/* 603 */     if (this.name != null) {
/* 604 */       fi.fileFormat = fileFormat;
/* 605 */       FileInfo ofi = this.imp.getOriginalFileInfo();
/* 606 */       if (ofi != null) {
/* 607 */         if (ofi.openNextName == null) {
/* 608 */           fi.openNextName = ofi.fileName;
/* 609 */           fi.openNextDir = ofi.directory;
/*     */         } else {
/* 611 */           fi.openNextName = ofi.openNextName;
/* 612 */           fi.openNextDir = ofi.openNextDir;
/*     */         }
/*     */       }
/* 615 */       fi.fileName = this.name;
/* 616 */       fi.directory = this.directory;
/*     */ 
/* 619 */       fi.description = null;
/* 620 */       this.imp.setTitle(this.name);
/* 621 */       this.imp.setFileInfo(fi);
/*     */     }
/*     */   }
/*     */ 
/*     */   void showErrorMessage(IOException e) {
/* 626 */     String msg = e.getMessage();
/* 627 */     if (msg.length() > 100)
/* 628 */       msg = msg.substring(0, 100);
/* 629 */     IJ.error("FileSaver", "An error occured writing the file.\n \n" + msg);
/*     */   }
/*     */ 
/*     */   public String getDescriptionString()
/*     */   {
/* 634 */     Calibration cal = this.imp.getCalibration();
/* 635 */     StringBuffer sb = new StringBuffer(100);
/* 636 */     sb.append("ImageJ=1.45s\n");
/* 637 */     if ((this.fi.nImages > 1) && (this.fi.fileType != 12))
/* 638 */       sb.append("images=" + this.fi.nImages + "\n");
/* 639 */     int channels = this.imp.getNChannels();
/* 640 */     if (channels > 1)
/* 641 */       sb.append("channels=" + channels + "\n");
/* 642 */     int slices = this.imp.getNSlices();
/* 643 */     if (slices > 1)
/* 644 */       sb.append("slices=" + slices + "\n");
/* 645 */     int frames = this.imp.getNFrames();
/* 646 */     if (frames > 1)
/* 647 */       sb.append("frames=" + frames + "\n");
/* 648 */     if (this.imp.isHyperStack()) sb.append("hyperstack=true\n");
/* 649 */     if (this.imp.isComposite()) {
/* 650 */       String mode = ((CompositeImage)this.imp).getModeAsString();
/* 651 */       sb.append("mode=" + mode + "\n");
/*     */     }
/* 653 */     if (this.fi.unit != null)
/* 654 */       sb.append("unit=" + (this.fi.unit.equals("µm") ? "um" : this.fi.unit) + "\n");
/* 655 */     if ((this.fi.valueUnit != null) && (this.fi.calibrationFunction != 22)) {
/* 656 */       sb.append("cf=" + this.fi.calibrationFunction + "\n");
/* 657 */       if (this.fi.coefficients != null) {
/* 658 */         for (int i = 0; i < this.fi.coefficients.length; i++)
/* 659 */           sb.append("c" + i + "=" + this.fi.coefficients[i] + "\n");
/*     */       }
/* 661 */       sb.append("vunit=" + this.fi.valueUnit + "\n");
/* 662 */       if (cal.zeroClip()) sb.append("zeroclip=true\n");
/*     */ 
/*     */     }
/*     */ 
/* 666 */     if (cal.frameInterval != 0.0D) {
/* 667 */       if ((int)cal.frameInterval == cal.frameInterval)
/* 668 */         sb.append("finterval=" + (int)cal.frameInterval + "\n");
/*     */       else
/* 670 */         sb.append("finterval=" + cal.frameInterval + "\n");
/*     */     }
/* 672 */     if (!cal.getTimeUnit().equals("sec"))
/* 673 */       sb.append("tunit=" + cal.getTimeUnit() + "\n");
/* 674 */     if (this.fi.nImages > 1) {
/* 675 */       if ((this.fi.pixelDepth != 0.0D) && (this.fi.pixelDepth != 1.0D))
/* 676 */         sb.append("spacing=" + this.fi.pixelDepth + "\n");
/* 677 */       if (cal.fps != 0.0D) {
/* 678 */         if ((int)cal.fps == cal.fps)
/* 679 */           sb.append("fps=" + (int)cal.fps + "\n");
/*     */         else
/* 681 */           sb.append("fps=" + cal.fps + "\n");
/*     */       }
/* 683 */       sb.append("loop=" + (cal.loop ? "true" : "false") + "\n");
/*     */     }
/*     */ 
/* 687 */     ImageProcessor ip = this.imp.getProcessor();
/* 688 */     double min = ip.getMin();
/* 689 */     double max = ip.getMax();
/* 690 */     int type = this.imp.getType();
/* 691 */     boolean enhancedLut = ((type == 0) || (type == 3)) && ((min != 0.0D) || (max != 255.0D));
/* 692 */     if ((enhancedLut) || (type == 1) || (type == 2)) {
/* 693 */       sb.append("min=" + min + "\n");
/* 694 */       sb.append("max=" + max + "\n");
/*     */     }
/*     */ 
/* 698 */     if (cal.xOrigin != 0.0D)
/* 699 */       sb.append("xorigin=" + cal.xOrigin + "\n");
/* 700 */     if (cal.yOrigin != 0.0D)
/* 701 */       sb.append("yorigin=" + cal.yOrigin + "\n");
/* 702 */     if (cal.zOrigin != 0.0D)
/* 703 */       sb.append("zorigin=" + cal.zOrigin + "\n");
/* 704 */     if ((cal.info != null) && (cal.info.length() <= 64) && (cal.info.indexOf('=') == -1) && (cal.info.indexOf('\n') == -1))
/* 705 */       sb.append("info=" + cal.info + "\n");
/* 706 */     if (this.saveName)
/* 707 */       sb.append("name=" + this.imp.getTitle() + "\n");
/* 708 */     sb.append('\000');
/* 709 */     return new String(sb);
/*     */   }
/*     */ 
/*     */   public static void setJpegQuality(int quality)
/*     */   {
/* 715 */     jpegQuality = quality;
/* 716 */     if (jpegQuality < 0) jpegQuality = 0;
/* 717 */     if (jpegQuality > 100) jpegQuality = 100;
/*     */   }
/*     */ 
/*     */   public static int getJpegQuality()
/*     */   {
/* 722 */     return jpegQuality;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  22 */     setJpegQuality(Prefs.getInt("jpeg", 85));
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.FileSaver
 * JD-Core Version:    0.6.2
 */