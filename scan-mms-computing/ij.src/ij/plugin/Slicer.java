/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.NewImage;
/*     */ import ij.gui.PolygonRoi;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.ResultsTable;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Label;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.event.TextListener;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Slicer
/*     */   implements PlugIn, TextListener, ItemListener
/*     */ {
/*  19 */   private static final String[] starts = { "Top", "Left", "Bottom", "Right" };
/*  20 */   private static String startAt = starts[0];
/*     */   private static boolean rotate;
/*     */   private static boolean flip;
/*  23 */   private static int sliceCount = 1;
/*  24 */   private boolean nointerpolate = Prefs.avoidResliceInterpolation;
/*  25 */   private double inputZSpacing = 1.0D;
/*  26 */   private double outputZSpacing = 1.0D;
/*  27 */   private int outputSlices = 1;
/*     */   private boolean noRoi;
/*     */   private boolean rgb;
/*     */   private boolean notFloat;
/*     */   private Vector fields;
/*     */   private Vector checkboxes;
/*     */   private Label message;
/*     */   private ImagePlus imp;
/*     */   private double gx1;
/*     */   private double gy1;
/*     */   private double gx2;
/*     */   private double gy2;
/*     */   private double gLength;
/*     */   private int n;
/*     */   private double[] x;
/*     */   private double[] y;
/*     */   private int xbase;
/*     */   private int ybase;
/*     */   private double length;
/*     */   private double[] segmentLengths;
/*     */   private double[] dx;
/*     */   private double[] dy;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  47 */     this.imp = WindowManager.getCurrentImage();
/*  48 */     if (this.imp == null) {
/*  49 */       IJ.noImage();
/*  50 */       return;
/*     */     }
/*  52 */     int stackSize = this.imp.getStackSize();
/*  53 */     Roi roi = this.imp.getRoi();
/*  54 */     int roiType = roi != null ? roi.getType() : 0;
/*     */ 
/*  56 */     if ((stackSize < 2) && (roi != null) && (roiType != 0)) {
/*  57 */       IJ.error("Reslice...", "Stack required");
/*  58 */       return;
/*     */     }
/*     */ 
/*  61 */     if ((roi != null) && (roiType != 0) && (roiType != 5) && (roiType != 6) && (roiType != 7)) {
/*  62 */       IJ.error("Reslice...", "Line or rectangular selection required");
/*  63 */       return;
/*     */     }
/*  65 */     if (!showDialog(this.imp))
/*  66 */       return;
/*  67 */     long startTime = System.currentTimeMillis();
/*  68 */     ImagePlus imp2 = null;
/*  69 */     this.rgb = (this.imp.getType() == 4);
/*  70 */     this.notFloat = ((!this.rgb) && (this.imp.getType() != 2));
/*  71 */     if (this.imp.isHyperStack())
/*  72 */       imp2 = resliceHyperstack(this.imp);
/*     */     else
/*  74 */       imp2 = reslice(this.imp);
/*  75 */     if (imp2 == null)
/*  76 */       return;
/*  77 */     ImageProcessor ip = this.imp.getProcessor();
/*  78 */     double min = ip.getMin();
/*  79 */     double max = ip.getMax();
/*  80 */     if (!this.rgb) imp2.getProcessor().setMinAndMax(min, max);
/*  81 */     imp2.show();
/*  82 */     if (this.noRoi)
/*  83 */       this.imp.killRoi();
/*     */     else
/*  85 */       this.imp.draw();
/*  86 */     IJ.showStatus(IJ.d2s((System.currentTimeMillis() - startTime) / 1000.0D, 2) + " seconds");
/*     */   }
/*     */ 
/*     */   public ImagePlus reslice(ImagePlus imp)
/*     */   {
/*  91 */     Roi roi = imp.getRoi();
/*  92 */     int roiType = roi != null ? roi.getType() : 0;
/*  93 */     Calibration origCal = imp.getCalibration();
/*  94 */     if (this.nointerpolate) {
/*  95 */       Calibration tmpCal = origCal.copy();
/*  96 */       tmpCal.pixelWidth = 1.0D;
/*  97 */       tmpCal.pixelHeight = 1.0D;
/*  98 */       tmpCal.pixelDepth = 1.0D;
/*  99 */       imp.setCalibration(tmpCal);
/* 100 */       this.inputZSpacing = 1.0D;
/* 101 */       if (roiType != 5)
/* 102 */         this.outputZSpacing = 1.0D;
/*     */     }
/* 104 */     double zSpacing = this.inputZSpacing / imp.getCalibration().pixelWidth;
/*     */     ImagePlus imp2;
/*     */     ImagePlus imp2;
/* 105 */     if ((roi == null) || (roiType == 0) || (roiType == 5)) {
/* 106 */       imp2 = resliceRectOrLine(imp);
/*     */     } else {
/* 108 */       String status = imp.getStack().isVirtual() ? "" : null;
/* 109 */       IJ.showStatus("Reslice...");
/* 110 */       ImageProcessor ip2 = getSlice(imp, 0.0D, 0.0D, 0.0D, 0.0D, status);
/* 111 */       imp2 = new ImagePlus("Reslice of " + imp.getShortTitle(), ip2);
/*     */     }
/* 113 */     if (this.nointerpolate) {
/* 114 */       imp.setCalibration(origCal);
/*     */     }
/*     */ 
/* 117 */     boolean horizontal = false;
/* 118 */     boolean vertical = false;
/* 119 */     if ((roi == null) || (roiType == 0)) {
/* 120 */       if ((startAt.equals(starts[0])) || (startAt.equals(starts[2])))
/* 121 */         horizontal = true;
/*     */       else
/* 123 */         vertical = true;
/*     */     }
/* 125 */     if ((roi != null) && (roiType == 5)) {
/* 126 */       Line l = (Line)roi;
/* 127 */       horizontal = l.y2 - l.y1 == 0;
/* 128 */       vertical = l.x2 - l.x1 == 0;
/*     */     }
/* 130 */     if (imp2 == null) return null;
/* 131 */     imp2.setCalibration(imp.getCalibration());
/* 132 */     Calibration cal = imp2.getCalibration();
/* 133 */     if (horizontal) {
/* 134 */       cal.pixelWidth = origCal.pixelWidth;
/* 135 */       cal.pixelHeight = (origCal.pixelDepth / zSpacing);
/* 136 */       cal.pixelDepth = (origCal.pixelHeight * this.outputZSpacing);
/* 137 */     } else if (vertical) {
/* 138 */       cal.pixelWidth = origCal.pixelHeight;
/* 139 */       cal.pixelHeight = (origCal.pixelDepth / zSpacing);
/*     */ 
/* 142 */       cal.pixelDepth = (origCal.pixelWidth * this.outputZSpacing);
/*     */     }
/* 144 */     else if (origCal.pixelHeight == origCal.pixelWidth) {
/* 145 */       cal.pixelWidth = origCal.pixelWidth;
/* 146 */       cal.pixelHeight = (origCal.pixelDepth / zSpacing);
/* 147 */       cal.pixelDepth = (origCal.pixelWidth * this.outputZSpacing);
/*     */     } else {
/* 149 */       cal.pixelWidth = (cal.pixelHeight = cal.pixelDepth = 1.0D);
/* 150 */       cal.setUnit("pixel");
/*     */     }
/*     */ 
/* 154 */     if (rotate) {
/* 155 */       double tmp = cal.pixelWidth;
/* 156 */       cal.pixelWidth = cal.pixelHeight;
/* 157 */       cal.pixelHeight = tmp;
/*     */     }
/* 159 */     return imp2;
/*     */   }
/*     */ 
/*     */   ImagePlus resliceHyperstack(ImagePlus imp) {
/* 163 */     int channels = imp.getNChannels();
/* 164 */     int slices = imp.getNSlices();
/* 165 */     int frames = imp.getNFrames();
/* 166 */     if (slices == 1) {
/* 167 */       IJ.error("Reslice...", "Cannot reslice z=1 hyperstacks");
/* 168 */       return null;
/*     */     }
/* 170 */     int c1 = imp.getChannel();
/* 171 */     int z1 = imp.getSlice();
/* 172 */     int t1 = imp.getFrame();
/* 173 */     int width = imp.getWidth();
/* 174 */     int height = imp.getHeight();
/* 175 */     ImagePlus imp2 = null;
/* 176 */     ImageStack stack2 = null;
/* 177 */     Roi roi = imp.getRoi();
/* 178 */     for (int t = 1; t <= frames; t++) {
/* 179 */       for (int c = 1; c <= channels; c++) {
/* 180 */         ImageStack tmp1Stack = new ImageStack(width, height);
/* 181 */         for (int z = 1; z <= slices; z++) {
/* 182 */           imp.setPositionWithoutUpdate(c, z, t);
/* 183 */           tmp1Stack.addSlice(null, imp.getProcessor());
/*     */         }
/* 185 */         ImagePlus tmp1 = new ImagePlus("tmp", tmp1Stack);
/* 186 */         tmp1.setCalibration(imp.getCalibration());
/* 187 */         tmp1.setRoi(roi);
/* 188 */         ImagePlus tmp2 = reslice(tmp1);
/* 189 */         int slices2 = tmp2.getStackSize();
/* 190 */         if (imp2 == null) {
/* 191 */           imp2 = tmp2.createHyperStack("Reslice of " + imp.getTitle(), channels, slices2, frames, tmp2.getBitDepth());
/* 192 */           stack2 = imp2.getStack();
/*     */         }
/* 194 */         ImageStack tmp2Stack = tmp2.getStack();
/* 195 */         for (int z = 1; z <= slices2; z++) {
/* 196 */           imp.setPositionWithoutUpdate(c, z, t);
/* 197 */           int n2 = imp2.getStackIndex(c, z, t);
/* 198 */           stack2.setPixels(tmp2Stack.getPixels(z), n2);
/*     */         }
/*     */       }
/*     */     }
/* 202 */     imp.setPosition(c1, z1, t1);
/* 203 */     if ((channels > 1) && (imp.isComposite())) {
/* 204 */       imp2 = new CompositeImage(imp2, ((CompositeImage)imp).getMode());
/* 205 */       ((CompositeImage)imp2).copyLuts(imp);
/*     */     }
/* 207 */     return imp2;
/*     */   }
/*     */ 
/*     */   boolean showDialog(ImagePlus imp) {
/* 211 */     Calibration cal = imp.getCalibration();
/* 212 */     if (cal.pixelDepth < 0.0D)
/* 213 */       cal.pixelDepth = (-cal.pixelDepth);
/* 214 */     String units = cal.getUnits();
/* 215 */     if (cal.pixelWidth == 0.0D)
/* 216 */       cal.pixelWidth = 1.0D;
/* 217 */     this.inputZSpacing = cal.pixelDepth;
/* 218 */     double outputSpacing = cal.pixelDepth;
/* 219 */     Roi roi = imp.getRoi();
/* 220 */     boolean line = (roi != null) && (roi.getType() == 5);
/* 221 */     if (line) saveLineInfo(roi);
/* 222 */     String macroOptions = Macro.getOptions();
/* 223 */     if (macroOptions != null) {
/* 224 */       if (macroOptions.indexOf("input=") != -1)
/* 225 */         macroOptions = macroOptions.replaceAll("slice=", "slice_count=");
/* 226 */       macroOptions = macroOptions.replaceAll("slice=", "output=");
/* 227 */       Macro.setOptions(macroOptions);
/* 228 */       this.nointerpolate = false;
/*     */     }
/* 230 */     GenericDialog gd = new GenericDialog("Reslice");
/* 231 */     gd.addNumericField("Output spacing (" + units + "):", outputSpacing, 3);
/* 232 */     if (line) {
/* 233 */       if (!IJ.isMacro()) this.outputSlices = sliceCount;
/* 234 */       gd.addNumericField("Slice_count:", this.outputSlices, 0);
/*     */     } else {
/* 236 */       gd.addChoice("Start at:", starts, startAt);
/* 237 */     }gd.addCheckbox("Flip vertically", flip);
/* 238 */     gd.addCheckbox("Rotate 90 degrees", rotate);
/* 239 */     gd.addCheckbox("Avoid interpolation", this.nointerpolate);
/* 240 */     gd.setInsets(0, 32, 0);
/* 241 */     gd.addMessage("(use 1 pixel spacing)");
/* 242 */     gd.setInsets(15, 0, 0);
/* 243 */     gd.addMessage("Voxel size: " + d2s(cal.pixelWidth) + "x" + d2s(cal.pixelHeight) + "x" + d2s(cal.pixelDepth) + " " + cal.getUnit());
/*     */ 
/* 245 */     gd.setInsets(5, 0, 0);
/* 246 */     gd.addMessage("Output size: " + getSize(cal.pixelDepth, outputSpacing, this.outputSlices) + "\t\t\t\t");
/* 247 */     this.fields = gd.getNumericFields();
/* 248 */     for (int i = 0; i < this.fields.size(); i++)
/* 249 */       ((TextField)this.fields.elementAt(i)).addTextListener(this);
/* 250 */     this.checkboxes = gd.getCheckboxes();
/* 251 */     ((Checkbox)this.checkboxes.elementAt(2)).addItemListener(this);
/* 252 */     this.message = ((Label)gd.getMessage());
/* 253 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/image.html#reslice");
/* 254 */     gd.showDialog();
/* 255 */     if (gd.wasCanceled()) {
/* 256 */       return false;
/*     */     }
/*     */ 
/* 259 */     this.outputZSpacing = (gd.getNextNumber() / cal.pixelWidth);
/* 260 */     if (line) {
/* 261 */       this.outputSlices = ((int)gd.getNextNumber());
/* 262 */       if (!IJ.isMacro()) sliceCount = this.outputSlices;
/* 263 */       imp.setRoi(roi);
/*     */     } else {
/* 265 */       startAt = gd.getNextChoice();
/* 266 */     }flip = gd.getNextBoolean();
/* 267 */     rotate = gd.getNextBoolean();
/* 268 */     this.nointerpolate = gd.getNextBoolean();
/* 269 */     if (!IJ.isMacro())
/* 270 */       Prefs.avoidResliceInterpolation = this.nointerpolate;
/* 271 */     return true;
/*     */   }
/*     */ 
/*     */   String d2s(double n)
/*     */   {
/*     */     String s;
/*     */     String s;
/* 276 */     if (n == (int)n)
/* 277 */       s = ResultsTable.d2s(n, 0);
/*     */     else
/* 279 */       s = ResultsTable.d2s(n, 2);
/* 280 */     if ((s.indexOf(".") != -1) && (s.endsWith("0")))
/* 281 */       s = s.substring(0, s.length() - 1);
/* 282 */     return s;
/*     */   }
/*     */ 
/*     */   void saveLineInfo(Roi roi) {
/* 286 */     Line line = (Line)roi;
/* 287 */     this.gx1 = line.x1;
/* 288 */     this.gy1 = line.y1;
/* 289 */     this.gx2 = line.x2;
/* 290 */     this.gy2 = line.y2;
/* 291 */     this.gLength = line.getRawLength();
/*     */   }
/*     */ 
/*     */   ImagePlus resliceRectOrLine(ImagePlus imp) {
/* 295 */     double x1 = 0.0D;
/* 296 */     double y1 = 0.0D;
/* 297 */     double x2 = 0.0D;
/* 298 */     double y2 = 0.0D;
/* 299 */     double xInc = 0.0D;
/* 300 */     double yInc = 0.0D;
/* 301 */     this.noRoi = false;
/*     */ 
/* 303 */     Roi roi = imp.getRoi();
/* 304 */     if (roi == null) {
/* 305 */       this.noRoi = true;
/* 306 */       imp.setRoi(0, 0, imp.getWidth(), imp.getHeight());
/* 307 */       roi = imp.getRoi();
/*     */     }
/* 309 */     if (roi.getType() == 0) {
/* 310 */       Rectangle r = roi.getBounds();
/* 311 */       if (startAt.equals(starts[0])) {
/* 312 */         x1 = r.x;
/* 313 */         y1 = r.y;
/* 314 */         x2 = r.x + r.width;
/* 315 */         y2 = r.y;
/* 316 */         xInc = 0.0D;
/* 317 */         yInc = this.outputZSpacing;
/* 318 */         this.outputSlices = ((int)(r.height / this.outputZSpacing));
/* 319 */       } else if (startAt.equals(starts[1])) {
/* 320 */         x1 = r.x;
/* 321 */         y1 = r.y;
/* 322 */         x2 = r.x;
/* 323 */         y2 = r.y + r.height;
/* 324 */         xInc = this.outputZSpacing;
/* 325 */         yInc = 0.0D;
/* 326 */         this.outputSlices = ((int)(r.width / this.outputZSpacing));
/* 327 */       } else if (startAt.equals(starts[2])) {
/* 328 */         x1 = r.x;
/* 329 */         y1 = r.y + r.height - 1;
/* 330 */         x2 = r.x + r.width;
/* 331 */         y2 = r.y + r.height - 1;
/* 332 */         xInc = 0.0D;
/* 333 */         yInc = -this.outputZSpacing;
/* 334 */         this.outputSlices = ((int)(r.height / this.outputZSpacing));
/* 335 */       } else if (startAt.equals(starts[3])) {
/* 336 */         x1 = r.x + r.width - 1;
/* 337 */         y1 = r.y;
/* 338 */         x2 = r.x + r.width - 1;
/* 339 */         y2 = r.y + r.height;
/* 340 */         xInc = -this.outputZSpacing;
/* 341 */         yInc = 0.0D;
/* 342 */         this.outputSlices = ((int)(r.width / this.outputZSpacing));
/*     */       }
/* 344 */     } else if (roi.getType() == 5) {
/* 345 */       Line line = (Line)roi;
/* 346 */       x1 = line.x1;
/* 347 */       y1 = line.y1;
/* 348 */       x2 = line.x2;
/* 349 */       y2 = line.y2;
/* 350 */       double dx = x2 - x1;
/* 351 */       double dy = y2 - y1;
/* 352 */       double nrm = Math.sqrt(dx * dx + dy * dy) / this.outputZSpacing;
/* 353 */       xInc = -(dy / nrm);
/* 354 */       yInc = dx / nrm;
/*     */     } else {
/* 356 */       return null;
/*     */     }
/* 358 */     if (this.outputSlices == 0) {
/* 359 */       IJ.error("Reslicer", "Output Z spacing (" + IJ.d2s(this.outputZSpacing, 0) + " pixels) is too large.\n" + "Is the voxel size in Image>Properties correct?.");
/*     */ 
/* 361 */       return null;
/*     */     }
/* 363 */     boolean virtualStack = imp.getStack().isVirtual();
/* 364 */     String status = null;
/* 365 */     ImagePlus imp2 = null;
/* 366 */     ImageStack stack2 = null;
/* 367 */     boolean isStack = imp.getStackSize() > 1;
/* 368 */     IJ.resetEscape();
/* 369 */     for (int i = 0; i < this.outputSlices; i++) {
/* 370 */       if (virtualStack)
/* 371 */         status = this.outputSlices > 1 ? i + 1 + "/" + this.outputSlices + ", " : "";
/* 372 */       ImageProcessor ip = getSlice(imp, x1, y1, x2, y2, status);
/*     */ 
/* 374 */       if (isStack) drawLine(x1, y1, x2, y2, imp);
/* 375 */       if (stack2 == null) {
/* 376 */         stack2 = createOutputStack(imp, ip);
/* 377 */         if ((stack2 == null) || (stack2.getSize() < this.outputSlices)) return null;
/*     */       }
/* 379 */       stack2.setPixels(ip.getPixels(), i + 1);
/* 380 */       x1 += xInc; x2 += xInc; y1 += yInc; y2 += yInc;
/* 381 */       if (IJ.escapePressed()) {
/* 382 */         IJ.beep(); imp.draw(); return null;
/*     */       }
/*     */     }
/* 384 */     return new ImagePlus("Reslice of " + imp.getShortTitle(), stack2);
/*     */   }
/*     */ 
/*     */   ImageStack createOutputStack(ImagePlus imp, ImageProcessor ip) {
/* 388 */     int bitDepth = imp.getBitDepth();
/* 389 */     int w2 = ip.getWidth(); int h2 = ip.getHeight(); int d2 = this.outputSlices;
/* 390 */     int flags = 9;
/* 391 */     ImagePlus imp2 = NewImage.createImage("temp", w2, h2, d2, bitDepth, flags);
/* 392 */     if ((imp2 != null) && (imp2.getStackSize() == d2))
/* 393 */       IJ.showStatus("Reslice... (press 'Esc' to abort)");
/* 394 */     if (imp2 == null) {
/* 395 */       return null;
/*     */     }
/* 397 */     ImageStack stack2 = imp2.getStack();
/* 398 */     stack2.setColorModel(ip.getColorModel());
/* 399 */     return stack2;
/*     */   }
/*     */ 
/*     */   ImageProcessor getSlice(ImagePlus imp, double x1, double y1, double x2, double y2, String status)
/*     */   {
/* 404 */     Roi roi = imp.getRoi();
/* 405 */     int roiType = roi != null ? roi.getType() : 0;
/* 406 */     ImageStack stack = imp.getStack();
/* 407 */     int stackSize = stack.getSize();
/* 408 */     ImageProcessor ip2 = null;
/* 409 */     float[] line = null;
/* 410 */     boolean ortho = (((int)x1 == x1) && ((int)y1 == y1) && (x1 == x2)) || (y1 == y2);
/*     */ 
/* 413 */     for (int i = 0; i < stackSize; i++) {
/* 414 */       ImageProcessor ip = stack.getProcessor(flip ? stackSize - i : i + 1);
/* 415 */       if ((roiType == 6) || (roiType == 7))
/* 416 */         line = getIrregularProfile(roi, ip);
/* 417 */       else if (ortho)
/* 418 */         line = getOrthoLine(ip, (int)x1, (int)y1, (int)x2, (int)y2, line);
/*     */       else
/* 420 */         line = getLine(ip, x1, y1, x2, y2, line);
/* 421 */       if (rotate) {
/* 422 */         if (i == 0) ip2 = ip.createProcessor(stackSize, line.length);
/* 423 */         putColumn(ip2, i, 0, line, line.length);
/*     */       } else {
/* 425 */         if (i == 0) ip2 = ip.createProcessor(line.length, stackSize);
/* 426 */         putRow(ip2, 0, i, line, line.length);
/*     */       }
/* 428 */       if (status != null) IJ.showStatus("Slicing: " + status + i + "/" + stackSize);
/*     */     }
/* 430 */     Calibration cal = imp.getCalibration();
/* 431 */     double zSpacing = this.inputZSpacing / cal.pixelWidth;
/* 432 */     if (zSpacing != 1.0D) {
/* 433 */       ip2.setInterpolate(true);
/* 434 */       if (rotate)
/* 435 */         ip2 = ip2.resize((int)(stackSize * zSpacing), line.length);
/*     */       else
/* 437 */         ip2 = ip2.resize(line.length, (int)(stackSize * zSpacing));
/*     */     }
/* 439 */     return ip2;
/*     */   }
/*     */ 
/*     */   public void putRow(ImageProcessor ip, int x, int y, float[] data, int length) {
/* 443 */     if (this.rgb)
/* 444 */       for (int i = 0; i < length; i++)
/* 445 */         ip.putPixel(x++, y, Float.floatToIntBits(data[i]));
/*     */     else
/* 447 */       for (int i = 0; i < length; i++)
/* 448 */         ip.putPixelValue(x++, y, data[i]);
/*     */   }
/*     */ 
/*     */   public void putColumn(ImageProcessor ip, int x, int y, float[] data, int length)
/*     */   {
/* 453 */     if (this.rgb)
/* 454 */       for (int i = 0; i < length; i++)
/* 455 */         ip.putPixel(x, y++, Float.floatToIntBits(data[i]));
/*     */     else
/* 457 */       for (int i = 0; i < length; i++)
/* 458 */         ip.putPixelValue(x, y++, data[i]);
/*     */   }
/*     */ 
/*     */   float[] getIrregularProfile(Roi roi, ImageProcessor ip)
/*     */   {
/* 463 */     if (this.x == null)
/* 464 */       doIrregularSetup(roi);
/* 465 */     float[] values = new float[(int)this.length];
/* 466 */     double leftOver = 1.0D;
/* 467 */     double distance = 0.0D;
/*     */ 
/* 469 */     double oldx = this.xbase; double oldy = this.ybase;
/* 470 */     for (int i = 0; i < this.n; i++) {
/* 471 */       double len = this.segmentLengths[i];
/* 472 */       if (len != 0.0D)
/*     */       {
/* 474 */         double xinc = this.dx[i] / len;
/* 475 */         double yinc = this.dy[i] / len;
/* 476 */         double start = 1.0D - leftOver;
/* 477 */         double rx = this.xbase + this.x[i] + start * xinc;
/* 478 */         double ry = this.ybase + this.y[i] + start * yinc;
/* 479 */         double len2 = len - start;
/* 480 */         int n2 = (int)len2;
/*     */ 
/* 483 */         for (int j = 0; j <= n2; j++) {
/* 484 */           int index = (int)distance + j;
/* 485 */           if (index < values.length)
/* 486 */             if (this.notFloat) {
/* 487 */               values[index] = ((float)ip.getInterpolatedPixel(rx, ry));
/* 488 */             } else if (this.rgb) {
/* 489 */               int rgbPixel = ((ColorProcessor)ip).getInterpolatedRGBPixel(rx, ry);
/* 490 */               values[index] = Float.intBitsToFloat(rgbPixel & 0xFFFFFF);
/*     */             } else {
/* 492 */               values[index] = ((float)ip.getInterpolatedValue(rx, ry));
/*     */             }
/* 494 */           rx += xinc;
/* 495 */           ry += yinc;
/*     */         }
/* 497 */         distance += len;
/* 498 */         leftOver = len2 - n2;
/*     */       }
/*     */     }
/* 501 */     return values;
/*     */   }
/*     */ 
/*     */   void doIrregularSetup(Roi roi)
/*     */   {
/* 506 */     this.n = ((PolygonRoi)roi).getNCoordinates();
/* 507 */     int[] ix = ((PolygonRoi)roi).getXCoordinates();
/* 508 */     int[] iy = ((PolygonRoi)roi).getYCoordinates();
/* 509 */     this.x = new double[this.n];
/* 510 */     this.y = new double[this.n];
/* 511 */     for (int i = 0; i < this.n; i++) {
/* 512 */       this.x[i] = ix[i];
/* 513 */       this.y[i] = iy[i];
/*     */     }
/* 515 */     if (roi.getType() == 7)
/*     */     {
/* 517 */       for (int i = 1; i < this.n - 1; i++) {
/* 518 */         this.x[i] = ((this.x[(i - 1)] + this.x[i] + this.x[(i + 1)]) / 3.0D + 0.5D);
/* 519 */         this.y[i] = ((this.y[(i - 1)] + this.y[i] + this.y[(i + 1)]) / 3.0D + 0.5D);
/*     */       }
/*     */     }
/* 522 */     Rectangle r = roi.getBounds();
/* 523 */     this.xbase = r.x;
/* 524 */     this.ybase = r.y;
/* 525 */     this.length = 0.0D;
/*     */ 
/* 528 */     this.segmentLengths = new double[this.n];
/* 529 */     this.dx = new double[this.n];
/* 530 */     this.dy = new double[this.n];
/* 531 */     for (int i = 0; i < this.n - 1; i++) {
/* 532 */       double xdelta = this.x[(i + 1)] - this.x[i];
/* 533 */       double ydelta = this.y[(i + 1)] - this.y[i];
/* 534 */       double segmentLength = Math.sqrt(xdelta * xdelta + ydelta * ydelta);
/* 535 */       this.length += segmentLength;
/* 536 */       this.segmentLengths[i] = segmentLength;
/* 537 */       this.dx[i] = xdelta;
/* 538 */       this.dy[i] = ydelta;
/*     */     }
/*     */   }
/*     */ 
/*     */   private float[] getLine(ImageProcessor ip, double x1, double y1, double x2, double y2, float[] data) {
/* 543 */     double dx = x2 - x1;
/* 544 */     double dy = y2 - y1;
/* 545 */     int n = (int)Math.round(Math.sqrt(dx * dx + dy * dy));
/* 546 */     if (data == null)
/* 547 */       data = new float[n];
/* 548 */     double xinc = dx / n;
/* 549 */     double yinc = dy / n;
/* 550 */     double rx = x1;
/* 551 */     double ry = y1;
/* 552 */     for (int i = 0; i < n; i++) {
/* 553 */       if (this.notFloat) {
/* 554 */         data[i] = ((float)ip.getInterpolatedPixel(rx, ry));
/* 555 */       } else if (this.rgb) {
/* 556 */         int rgbPixel = ((ColorProcessor)ip).getInterpolatedRGBPixel(rx, ry);
/* 557 */         data[i] = Float.intBitsToFloat(rgbPixel & 0xFFFFFF);
/*     */       } else {
/* 559 */         data[i] = ((float)ip.getInterpolatedValue(rx, ry));
/* 560 */       }rx += xinc;
/* 561 */       ry += yinc;
/*     */     }
/* 563 */     return data;
/*     */   }
/*     */ 
/*     */   private float[] getOrthoLine(ImageProcessor ip, int x1, int y1, int x2, int y2, float[] data) {
/* 567 */     int dx = x2 - x1;
/* 568 */     int dy = y2 - y1;
/* 569 */     int n = Math.max(Math.abs(dx), Math.abs(dy));
/* 570 */     if (data == null) data = new float[n];
/* 571 */     int xinc = dx / n;
/* 572 */     int yinc = dy / n;
/* 573 */     int rx = x1;
/* 574 */     int ry = y1;
/* 575 */     for (int i = 0; i < n; i++) {
/* 576 */       if (this.notFloat) {
/* 577 */         data[i] = ip.getPixel(rx, ry);
/* 578 */       } else if (this.rgb) {
/* 579 */         int rgbPixel = ((ColorProcessor)ip).getPixel(rx, ry);
/* 580 */         data[i] = Float.intBitsToFloat(rgbPixel & 0xFFFFFF);
/*     */       } else {
/* 582 */         data[i] = ip.getPixelValue(rx, ry);
/* 583 */       }rx += xinc;
/* 584 */       ry += yinc;
/*     */     }
/* 586 */     return data;
/*     */   }
/*     */ 
/*     */   void drawLine(double x1, double y1, double x2, double y2, ImagePlus imp) {
/* 590 */     ImageCanvas ic = imp.getCanvas();
/* 591 */     if (ic == null) return;
/* 592 */     Graphics g = ic.getGraphics();
/* 593 */     g.setColor(new Color(1.0F, 1.0F, 0.0F, 0.4F));
/* 594 */     g.drawLine(ic.screenX((int)(x1 + 0.5D)), ic.screenY((int)(y1 + 0.5D)), ic.screenX((int)(x2 + 0.5D)), ic.screenY((int)(y2 + 0.5D)));
/*     */   }
/*     */ 
/*     */   public void textValueChanged(TextEvent e) {
/* 598 */     updateSize();
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/* 602 */     if (IJ.isMacOSX()) IJ.wait(100);
/* 603 */     Checkbox cb = (Checkbox)this.checkboxes.elementAt(2);
/* 604 */     this.nointerpolate = cb.getState();
/* 605 */     updateSize();
/*     */   }
/*     */ 
/*     */   void updateSize()
/*     */   {
/* 610 */     double outSpacing = Tools.parseDouble(((TextField)this.fields.elementAt(0)).getText(), 0.0D);
/* 611 */     int count = 0;
/* 612 */     boolean lineSelection = this.fields.size() == 2;
/* 613 */     if (lineSelection) {
/* 614 */       count = (int)Tools.parseDouble(((TextField)this.fields.elementAt(1)).getText(), 0.0D);
/* 615 */       if (count > 0) makePolygon(count, outSpacing);
/*     */     }
/* 617 */     String size = getSize(this.inputZSpacing, outSpacing, count);
/* 618 */     this.message.setText("Output Size: " + size);
/*     */   }
/*     */ 
/*     */   String getSize(double inSpacing, double outSpacing, int count) {
/* 622 */     int size = getOutputStackSize(inSpacing, outSpacing, count);
/* 623 */     int mem = getAvailableMemory();
/* 624 */     String available = mem != -1 ? " (" + mem + "MB free)" : "";
/* 625 */     if (this.message != null)
/* 626 */       this.message.setForeground((mem != -1) && (size > mem) ? Color.red : Color.black);
/* 627 */     if (size > 0) {
/* 628 */       return size + "MB" + available;
/*     */     }
/* 630 */     return "<1MB" + available;
/*     */   }
/*     */ 
/*     */   void makePolygon(int count, double outSpacing) {
/* 634 */     int[] x = new int[4];
/* 635 */     int[] y = new int[4];
/* 636 */     Calibration cal = this.imp.getCalibration();
/* 637 */     double cx = cal.pixelWidth;
/* 638 */     double cy = cal.pixelHeight;
/* 639 */     x[0] = ((int)this.gx1);
/* 640 */     y[0] = ((int)this.gy1);
/* 641 */     x[1] = ((int)this.gx2);
/* 642 */     y[1] = ((int)this.gy2);
/* 643 */     double dx = this.gx2 - this.gx1;
/* 644 */     double dy = this.gy2 - this.gy1;
/* 645 */     double nrm = Math.sqrt(dx * dx + dy * dy) / outSpacing;
/* 646 */     double xInc = -(dy / (cx * nrm));
/* 647 */     double yInc = dx / (cy * nrm);
/* 648 */     x[2] = (x[1] + (int)(xInc * count));
/* 649 */     y[2] = (y[1] + (int)(yInc * count));
/* 650 */     x[3] = (x[0] + (int)(xInc * count));
/* 651 */     y[3] = (y[0] + (int)(yInc * count));
/* 652 */     this.imp.setRoi(new PolygonRoi(x, y, 4, 3));
/*     */   }
/*     */ 
/*     */   int getOutputStackSize(double inSpacing, double outSpacing, int count) {
/* 656 */     Roi roi = this.imp.getRoi();
/* 657 */     int width = this.imp.getWidth();
/* 658 */     int height = this.imp.getHeight();
/* 659 */     if (roi != null) {
/* 660 */       Rectangle r = roi.getBounds();
/* 661 */       width = r.width;
/* 662 */       width = r.height;
/*     */     }
/* 664 */     int type = roi != null ? roi.getType() : 0;
/* 665 */     int stackSize = this.imp.getStackSize();
/* 666 */     double size = 0.0D;
/* 667 */     if (type == 0) {
/* 668 */       size = width * height * stackSize;
/* 669 */       if ((outSpacing > 0.0D) && (!this.nointerpolate)) size *= inSpacing / outSpacing; 
/*     */     }
/* 671 */     else { size = this.gLength * count * stackSize; }
/* 672 */     int bits = this.imp.getBitDepth();
/* 673 */     switch (bits) { case 16:
/* 674 */       size *= 2.0D; break;
/*     */     case 24:
/*     */     case 32:
/* 675 */       size *= 4.0D;
/*     */     }
/* 677 */     return (int)Math.round(size / 1048576.0D);
/*     */   }
/*     */ 
/*     */   int getAvailableMemory() {
/* 681 */     long max = IJ.maxMemory();
/* 682 */     if (max == 0L) return -1;
/* 683 */     long inUse = IJ.currentMemory();
/* 684 */     long available = max - inUse;
/* 685 */     return (int)((available + 524288L) / 1048576L);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Slicer
 * JD-Core Version:    0.6.2
 */