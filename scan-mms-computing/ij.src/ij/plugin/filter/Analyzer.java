/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.PointRoi;
/*     */ import ij.gui.PolygonRoi;
/*     */ import ij.gui.ProfilePlot;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.gui.YesNoCancelDialog;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.Measurements;
/*     */ import ij.measure.ResultsTable;
/*     */ import ij.plugin.MeasurementsWriter;
/*     */ import ij.plugin.Straightener;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.FloatStatistics;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.text.TextPanel;
/*     */ import java.awt.Color;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class Analyzer
/*     */   implements PlugInFilter, Measurements
/*     */ {
/*     */   private String arg;
/*     */   private ImagePlus imp;
/*     */   private ResultsTable rt;
/*     */   private int measurements;
/*     */   private StringBuffer min;
/*     */   private StringBuffer max;
/*     */   private StringBuffer mean;
/*     */   private StringBuffer sd;
/*  25 */   private static final int[] list = { 1, 2, 4, 8, 16, 32, 64, 128, 512, 2048, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 256, 1024, 4096, 2097152, 4194304 };
/*     */   private static final String MEASUREMENTS = "measurements";
/*     */   private static final String MARK_WIDTH = "mark.width";
/*     */   private static final String PRECISION = "precision";
/*     */   private static boolean unsavedMeasurements;
/*  35 */   public static Color darkBlue = new Color(0, 0, 160);
/*  36 */   private static int systemMeasurements = Prefs.getInt("measurements", 19);
/*  37 */   public static int markWidth = Prefs.getInt("mark.width", 0);
/*  38 */   public static int precision = Prefs.getInt("precision", 3);
/*  39 */   private static float[] umeans = new float[20];
/*  40 */   private static ResultsTable systemRT = new ResultsTable();
/*     */   private static int redirectTarget;
/*  42 */   private static String redirectTitle = "";
/*     */   private static ImagePlus redirectImage;
/*     */   static int firstParticle;
/*     */   static int lastParticle;
/*     */   private static boolean summarized;
/*     */   private static boolean switchingModes;
/*  47 */   private static boolean showMin = true;
/*     */ 
/*     */   public Analyzer() {
/*  50 */     this.rt = systemRT;
/*  51 */     this.rt.showRowNumbers(true);
/*  52 */     this.rt.setPrecision((systemMeasurements & 0x200000) != 0 ? -precision : precision);
/*  53 */     this.measurements = systemMeasurements;
/*     */   }
/*     */ 
/*     */   public Analyzer(ImagePlus imp)
/*     */   {
/*  59 */     this();
/*  60 */     this.imp = imp;
/*     */   }
/*     */ 
/*     */   public Analyzer(ImagePlus imp, int measurements, ResultsTable rt)
/*     */   {
/*  66 */     this.imp = imp;
/*  67 */     this.measurements = measurements;
/*  68 */     this.rt = rt;
/*     */   }
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp) {
/*  72 */     this.arg = arg;
/*  73 */     this.imp = imp;
/*  74 */     IJ.register(Analyzer.class);
/*  75 */     if (arg.equals("set")) {
/*  76 */       doSetDialog(); return 4096;
/*  77 */     }if (arg.equals("sum")) {
/*  78 */       summarize(); return 4096;
/*  79 */     }if (arg.equals("clear")) {
/*  80 */       if (IJ.macroRunning()) unsavedMeasurements = false;
/*  81 */       resetCounter();
/*  82 */       return 4096;
/*     */     }
/*  84 */     return 159;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  88 */     measure();
/*  89 */     displayResults();
/*  90 */     if ((this.measurements & 0x400000) != 0)
/*  91 */       addToOverlay();
/*     */   }
/*     */ 
/*     */   void addToOverlay() {
/*  95 */     Roi roi = this.imp.getRoi();
/*  96 */     if (roi == null)
/*  97 */       return;
/*  98 */     if (this.imp.getStackSize() > 1) {
/*  99 */       if ((this.imp.isHyperStack()) || (this.imp.isComposite()))
/* 100 */         roi.setPosition(0, this.imp.getSlice(), this.imp.getFrame());
/*     */       else {
/* 102 */         roi.setPosition(this.imp.getCurrentSlice());
/*     */       }
/*     */     }
/*     */ 
/* 106 */     IJ.run(this.imp, "Add Selection...", "");
/*     */   }
/*     */ 
/*     */   void doSetDialog() {
/* 110 */     String NONE = "None";
/*     */ 
/* 112 */     int[] wList = WindowManager.getIDList();
/*     */     String[] titles;
/* 113 */     if (wList == null) {
/* 114 */       String[] titles = new String[1];
/* 115 */       titles[0] = NONE;
/*     */     } else {
/* 117 */       titles = new String[wList.length + 1];
/* 118 */       titles[0] = NONE;
/* 119 */       for (int i = 0; i < wList.length; i++) {
/* 120 */         ImagePlus imp = WindowManager.getImage(wList[i]);
/* 121 */         titles[(i + 1)] = (imp != null ? imp.getTitle() : "");
/*     */       }
/*     */     }
/* 124 */     ImagePlus tImp = WindowManager.getImage(redirectTarget);
/* 125 */     String target = tImp != null ? tImp.getTitle() : NONE;
/* 126 */     String macroOptions = Macro.getOptions();
/* 127 */     if ((macroOptions != null) && (macroOptions.indexOf("circularity ") != -1))
/* 128 */       Macro.setOptions(macroOptions.replaceAll("circularity ", "shape "));
/* 129 */     if ((macroOptions != null) && (macroOptions.indexOf("slice ") != -1)) {
/* 130 */       Macro.setOptions(macroOptions.replaceAll("slice ", "stack "));
/*     */     }
/* 132 */     GenericDialog gd = new GenericDialog("Set Measurements", IJ.getInstance());
/* 133 */     String[] labels = new String[18];
/* 134 */     boolean[] states = new boolean[18];
/* 135 */     labels[0] = "Area"; states[0] = ((systemMeasurements & 0x1) != 0 ? 1 : false);
/* 136 */     labels[1] = "Mean gray value"; states[1] = ((systemMeasurements & 0x2) != 0 ? 1 : false);
/* 137 */     labels[2] = "Standard deviation"; states[2] = ((systemMeasurements & 0x4) != 0 ? 1 : false);
/* 138 */     labels[3] = "Modal gray value"; states[3] = ((systemMeasurements & 0x8) != 0 ? 1 : false);
/* 139 */     labels[4] = "Min & max gray value"; states[4] = ((systemMeasurements & 0x10) != 0 ? 1 : false);
/* 140 */     labels[5] = "Centroid"; states[5] = ((systemMeasurements & 0x20) != 0 ? 1 : false);
/* 141 */     labels[6] = "Center of mass"; states[6] = ((systemMeasurements & 0x40) != 0 ? 1 : false);
/* 142 */     labels[7] = "Perimeter"; states[7] = ((systemMeasurements & 0x80) != 0 ? 1 : false);
/* 143 */     labels[8] = "Bounding rectangle"; states[8] = ((systemMeasurements & 0x200) != 0 ? 1 : false);
/* 144 */     labels[9] = "Fit ellipse"; states[9] = ((systemMeasurements & 0x800) != 0 ? 1 : false);
/* 145 */     labels[10] = "Shape descriptors"; states[10] = ((systemMeasurements & 0x2000) != 0 ? 1 : false);
/* 146 */     labels[11] = "Feret's diameter"; states[11] = ((systemMeasurements & 0x4000) != 0 ? 1 : false);
/* 147 */     labels[12] = "Integrated density"; states[12] = ((systemMeasurements & 0x8000) != 0 ? 1 : false);
/* 148 */     labels[13] = "Median"; states[13] = ((systemMeasurements & 0x10000) != 0 ? 1 : false);
/* 149 */     labels[14] = "Skewness"; states[14] = ((systemMeasurements & 0x20000) != 0 ? 1 : false);
/* 150 */     labels[15] = "Kurtosis"; states[15] = ((systemMeasurements & 0x40000) != 0 ? 1 : false);
/* 151 */     labels[16] = "Area_fraction"; states[16] = ((systemMeasurements & 0x80000) != 0 ? 1 : false);
/* 152 */     labels[17] = "Stack position"; states[17] = ((systemMeasurements & 0x100000) != 0 ? 1 : false);
/* 153 */     gd.setInsets(0, 0, 0);
/* 154 */     gd.addCheckboxGroup(10, 2, labels, states);
/* 155 */     labels = new String[5];
/* 156 */     states = new boolean[5];
/* 157 */     labels[0] = "Limit to threshold"; states[0] = ((systemMeasurements & 0x100) != 0 ? 1 : false);
/* 158 */     labels[1] = "Display label"; states[1] = ((systemMeasurements & 0x400) != 0 ? 1 : false);
/* 159 */     labels[2] = "Invert Y coordinates"; states[2] = ((systemMeasurements & 0x1000) != 0 ? 1 : false);
/* 160 */     labels[3] = "Scientific notation"; states[3] = ((systemMeasurements & 0x200000) != 0 ? 1 : false);
/* 161 */     labels[4] = "Add to overlay"; states[4] = ((systemMeasurements & 0x400000) != 0 ? 1 : false);
/* 162 */     gd.setInsets(0, 0, 0);
/* 163 */     gd.addCheckboxGroup(3, 2, labels, states);
/* 164 */     gd.setInsets(15, 0, 0);
/* 165 */     gd.addChoice("Redirect to:", titles, target);
/* 166 */     gd.setInsets(5, 0, 0);
/* 167 */     gd.addNumericField("Decimal places (0-9):", precision, 0, 2, "");
/* 168 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/analyze.html#set");
/* 169 */     gd.showDialog();
/* 170 */     if (gd.wasCanceled())
/* 171 */       return;
/* 172 */     int oldMeasurements = systemMeasurements;
/* 173 */     setOptions(gd);
/* 174 */     int index = gd.getNextChoiceIndex();
/* 175 */     redirectTarget = index == 0 ? 0 : wList[(index - 1)];
/* 176 */     redirectTitle = titles[index];
/* 177 */     ImagePlus imp = WindowManager.getImage(redirectTarget);
/* 178 */     redirectImage = (imp != null) && (imp.getWindow() == null) ? imp : null;
/*     */ 
/* 180 */     int prec = (int)gd.getNextNumber();
/* 181 */     if (prec < 0) prec = 0;
/* 182 */     if (prec > 9) prec = 9;
/* 183 */     boolean notationChanged = (oldMeasurements & 0x200000) != (systemMeasurements & 0x200000);
/* 184 */     if ((prec != precision) || (notationChanged)) {
/* 185 */       precision = prec;
/* 186 */       this.rt.setPrecision((systemMeasurements & 0x200000) != 0 ? -precision : precision);
/* 187 */       if (this.rt.getCounter() > 0)
/* 188 */         this.rt.show("Results");
/*     */     }
/*     */   }
/*     */ 
/*     */   void setOptions(GenericDialog gd) {
/* 193 */     int oldMeasurements = systemMeasurements;
/* 194 */     int previous = 0;
/* 195 */     boolean b = false;
/* 196 */     for (int i = 0; i < list.length; i++)
/*     */     {
/* 198 */       b = gd.getNextBoolean();
/* 199 */       previous = list[i];
/* 200 */       if (b)
/* 201 */         systemMeasurements |= list[i];
/*     */       else
/* 203 */         systemMeasurements &= (list[i] ^ 0xFFFFFFFF);
/*     */     }
/* 205 */     if (((oldMeasurements & 0xFFFFFEFF & 0xFFDFFFFF) != (systemMeasurements & 0xFFFFFEFF & 0xFFDFFFFF)) && (IJ.isResultsWindow())) {
/* 206 */       this.rt.setPrecision((systemMeasurements & 0x200000) != 0 ? -precision : precision);
/* 207 */       this.rt.update(systemMeasurements, this.imp, null);
/*     */     }
/* 209 */     if ((systemMeasurements & 0x400) == 0)
/* 210 */       systemRT.disableRowLabels();
/*     */   }
/*     */ 
/*     */   public void measure()
/*     */   {
/* 215 */     String lastHdr = this.rt.getColumnHeading(35);
/* 216 */     if (((lastHdr == null) || (lastHdr.charAt(0) != 'S')) && 
/* 217 */       (!reset())) return;
/*     */ 
/* 219 */     firstParticle = Analyzer.lastParticle = 0;
/* 220 */     Roi roi = this.imp.getRoi();
/* 221 */     if ((roi != null) && (roi.getType() == 10)) {
/* 222 */       measurePoint(roi);
/* 223 */       return;
/*     */     }
/* 225 */     if ((roi != null) && (roi.isLine())) {
/* 226 */       measureLength(roi);
/* 227 */       return;
/*     */     }
/* 229 */     if ((roi != null) && (roi.getType() == 8)) {
/* 230 */       measureAngle(roi);
/*     */       return;
/*     */     }
/* 235 */     ImageStatistics stats;
/* 234 */     if (isRedirectImage()) { ImageStatistics stats = getRedirectStats(this.measurements, roi);
/* 236 */       if (stats != null);
/*     */     } else {
/* 238 */       stats = this.imp.getStatistics(this.measurements);
/* 239 */     }if ((!IJ.isResultsWindow()) && (IJ.getInstance() != null))
/* 240 */       reset();
/* 241 */     saveResults(stats, roi);
/*     */   }
/*     */ 
/*     */   boolean reset() {
/* 245 */     boolean ok = true;
/* 246 */     if (this.rt.getCounter() > 0)
/* 247 */       ok = resetCounter();
/* 248 */     if ((ok) && (this.rt.getColumnHeading(35) == null))
/* 249 */       this.rt.setDefaultHeadings();
/* 250 */     return ok;
/*     */   }
/*     */ 
/*     */   public static boolean isRedirectImage()
/*     */   {
/* 256 */     return redirectTarget != 0;
/*     */   }
/*     */ 
/*     */   public static void setRedirectImage(ImagePlus imp)
/*     */   {
/* 262 */     if (imp == null) {
/* 263 */       redirectTarget = 0;
/* 264 */       redirectTitle = null;
/* 265 */       redirectImage = null;
/*     */     } else {
/* 267 */       redirectTarget = imp.getID();
/* 268 */       redirectTitle = imp.getTitle();
/* 269 */       if (imp.getWindow() == null)
/* 270 */         redirectImage = imp;
/*     */     }
/*     */   }
/*     */ 
/*     */   private ImagePlus getRedirectImageOrStack(ImagePlus cimp) {
/* 275 */     ImagePlus rimp = getRedirectImage(cimp);
/* 276 */     if (rimp != null) {
/* 277 */       int depth = rimp.getStackSize();
/* 278 */       if ((depth > 1) && (depth == cimp.getStackSize()) && (rimp.getCurrentSlice() != cimp.getCurrentSlice()))
/* 279 */         rimp.setSlice(cimp.getCurrentSlice());
/*     */     }
/* 281 */     return rimp;
/*     */   }
/*     */ 
/*     */   public static ImagePlus getRedirectImage(ImagePlus cimp)
/*     */   {
/* 289 */     ImagePlus rimp = WindowManager.getImage(redirectTarget);
/* 290 */     if (rimp == null)
/* 291 */       rimp = redirectImage;
/* 292 */     if (rimp == null) {
/* 293 */       IJ.error("Analyzer", "Redirect image (\"" + redirectTitle + "\")\n" + "not found.");
/*     */ 
/* 295 */       redirectTarget = 0;
/* 296 */       Macro.abort();
/* 297 */       return null;
/*     */     }
/* 299 */     if ((rimp.getWidth() != cimp.getWidth()) || (rimp.getHeight() != cimp.getHeight())) {
/* 300 */       IJ.error("Analyzer", "Redirect image (\"" + redirectTitle + "\") \n" + "is not the same size as the current image.");
/*     */ 
/* 302 */       Macro.abort();
/* 303 */       return null;
/*     */     }
/* 305 */     return rimp;
/*     */   }
/*     */ 
/*     */   ImageStatistics getRedirectStats(int measurements, Roi roi) {
/* 309 */     ImagePlus redirectImp = getRedirectImageOrStack(this.imp);
/* 310 */     if (redirectImp == null)
/* 311 */       return null;
/* 312 */     ImageProcessor ip = redirectImp.getProcessor();
/* 313 */     if ((this.imp.getTitle().equals("mask")) && (this.imp.getBitDepth() == 8)) {
/* 314 */       ip.setMask(this.imp.getProcessor());
/* 315 */       ip.setRoi(0, 0, this.imp.getWidth(), this.imp.getHeight());
/*     */     } else {
/* 317 */       ip.setRoi(roi);
/* 318 */     }return ImageStatistics.getStatistics(ip, measurements, redirectImp.getCalibration());
/*     */   }
/*     */ 
/*     */   void measurePoint(Roi roi) {
/* 322 */     if (this.rt.getCounter() > 0) {
/* 323 */       if (!IJ.isResultsWindow()) reset();
/* 324 */       int index = this.rt.getColumnIndex("X");
/* 325 */       if ((index < 0) || (!this.rt.columnExists(index)))
/* 326 */         this.rt.update(this.measurements, this.imp, roi);
/*     */     }
/* 328 */     Polygon p = roi.getPolygon();
/* 329 */     ImagePlus imp2 = isRedirectImage() ? getRedirectImageOrStack(this.imp) : null;
/* 330 */     if (imp2 == null) imp2 = this.imp;
/* 331 */     for (int i = 0; i < p.npoints; i++) {
/* 332 */       ImageProcessor ip = imp2.getProcessor();
/* 333 */       ip.setRoi(p.xpoints[i], p.ypoints[i], 1, 1);
/* 334 */       ImageStatistics stats = ImageStatistics.getStatistics(ip, this.measurements, imp2.getCalibration());
/* 335 */       saveResults(stats, new PointRoi(p.xpoints[i], p.ypoints[i]));
/* 336 */       if (i != p.npoints - 1) displayResults(); 
/*     */     }
/*     */   }
/*     */ 
/*     */   void measureAngle(Roi roi)
/*     */   {
/* 341 */     if (this.rt.getCounter() > 0) {
/* 342 */       if (!IJ.isResultsWindow()) reset();
/* 343 */       int index = this.rt.getColumnIndex("Angle");
/* 344 */       if ((index < 0) || (!this.rt.columnExists(index)))
/* 345 */         this.rt.update(this.measurements, this.imp, roi);
/*     */     }
/* 347 */     ImageProcessor ip = this.imp.getProcessor();
/* 348 */     ip.setRoi(roi.getPolygon());
/* 349 */     ImageStatistics stats = new ImageStatistics();
/* 350 */     saveResults(stats, roi);
/*     */   }
/*     */ 
/*     */   void measureLength(Roi roi) {
/* 354 */     ImagePlus imp2 = isRedirectImage() ? getRedirectImageOrStack(this.imp) : null;
/* 355 */     if (imp2 != null)
/* 356 */       imp2.setRoi(roi);
/*     */     else
/* 358 */       imp2 = this.imp;
/* 359 */     if (this.rt.getCounter() > 0) {
/* 360 */       if (!IJ.isResultsWindow()) reset();
/* 361 */       boolean update = false;
/* 362 */       int index = this.rt.getColumnIndex("Length");
/* 363 */       if ((index < 0) || (!this.rt.columnExists(index))) update = true;
/* 364 */       if (roi.getType() == 5) {
/* 365 */         index = this.rt.getColumnIndex("Angle");
/* 366 */         if ((index < 0) || (!this.rt.columnExists(index))) update = true;
/*     */       }
/* 368 */       if (update) this.rt.update(this.measurements, imp2, roi);
/*     */     }
/* 370 */     boolean straightLine = roi.getType() == 5;
/* 371 */     int lineWidth = Math.round(roi.getStrokeWidth());
/*     */ 
/* 373 */     Rectangle saveR = null;
/*     */     ImageProcessor ip2;
/* 374 */     if ((straightLine) && (lineWidth > 1)) {
/* 375 */       ImageProcessor ip2 = imp2.getProcessor();
/* 376 */       saveR = ip2.getRoi();
/* 377 */       ip2.setRoi(roi.getPolygon());
/* 378 */     } else if (lineWidth > 1)
/*     */     {
/*     */       ImageProcessor ip2;
/* 379 */       if (((this.measurements & 0x1) != 0) || ((this.measurements & 0x2) != 0))
/* 380 */         ip2 = new Straightener().straightenLine(imp2, lineWidth);
/*     */       else
/* 382 */         saveResults(new ImageStatistics(), roi);
/*     */     }
/*     */     else
/*     */     {
/* 386 */       ProfilePlot profile = new ProfilePlot(imp2);
/* 387 */       double[] values = profile.getProfile();
/* 388 */       if (values == null) return;
/* 389 */       ip2 = new FloatProcessor(values.length, 1, values);
/* 390 */       if (straightLine) {
/* 391 */         Line l = (Line)roi;
/* 392 */         if (((l.y1 == l.y2) || (l.x1 == l.x2)) && (l.x1 == l.x1d) && (l.y1 == l.y1d) && (l.x2 == l.x2d) && (l.y2 == l.y2d))
/* 393 */           ip2.setRoi(0, 0, ip2.getWidth() - 1, 1);
/*     */       }
/*     */     }
/* 396 */     ImageStatistics stats = ImageStatistics.getStatistics(ip2, 31, imp2.getCalibration());
/* 397 */     if (saveR != null) ip2.setRoi(saveR);
/* 398 */     saveResults(stats, roi);
/*     */   }
/*     */ 
/*     */   public void saveResults(ImageStatistics stats, Roi roi)
/*     */   {
/* 405 */     if (this.rt.getColumnHeading(35) == null)
/* 406 */       reset();
/* 407 */     incrementCounter();
/* 408 */     int counter = this.rt.getCounter();
/* 409 */     if (counter <= 20) {
/* 410 */       if (umeans == null) umeans = new float[20];
/* 411 */       umeans[(counter - 1)] = ((float)stats.umean);
/*     */     }
/* 413 */     if ((this.measurements & 0x400) != 0)
/* 414 */       this.rt.addLabel("Label", getFileName());
/* 415 */     if ((this.measurements & 0x1) != 0) this.rt.addValue(0, stats.area);
/* 416 */     if ((this.measurements & 0x2) != 0) this.rt.addValue(1, stats.mean);
/* 417 */     if ((this.measurements & 0x4) != 0) this.rt.addValue(2, stats.stdDev);
/* 418 */     if ((this.measurements & 0x8) != 0) this.rt.addValue(3, stats.dmode);
/* 419 */     if ((this.measurements & 0x10) != 0) {
/* 420 */       if (showMin) this.rt.addValue(4, stats.min);
/* 421 */       this.rt.addValue(5, stats.max);
/*     */     }
/* 423 */     if ((this.measurements & 0x20) != 0) {
/* 424 */       this.rt.addValue(6, stats.xCentroid);
/* 425 */       this.rt.addValue(7, stats.yCentroid);
/*     */     }
/* 427 */     if ((this.measurements & 0x40) != 0) {
/* 428 */       this.rt.addValue(8, stats.xCenterOfMass);
/* 429 */       this.rt.addValue(9, stats.yCenterOfMass);
/*     */     }
/* 431 */     if (((this.measurements & 0x80) != 0) || ((this.measurements & 0x2000) != 0))
/*     */     {
/*     */       double perimeter;
/*     */       double perimeter;
/* 433 */       if (roi != null)
/* 434 */         perimeter = roi.getLength();
/*     */       else
/* 436 */         perimeter = this.imp != null ? this.imp.getWidth() * 2 + this.imp.getHeight() * 2 : 0.0D;
/* 437 */       if ((this.measurements & 0x80) != 0)
/* 438 */         this.rt.addValue(10, perimeter);
/* 439 */       if ((this.measurements & 0x2000) != 0) {
/* 440 */         double circularity = perimeter == 0.0D ? 0.0D : 12.566370614359172D * (stats.area / (perimeter * perimeter));
/* 441 */         if (circularity > 1.0D) circularity = 1.0D;
/* 442 */         this.rt.addValue(18, circularity);
/* 443 */         Polygon ch = null;
/* 444 */         boolean isArea = (roi == null) || (roi.isArea());
/* 445 */         double convexArea = roi != null ? getArea(roi.getConvexHull()) : stats.pixelCount;
/* 446 */         this.rt.addValue(33, isArea ? stats.major / stats.minor : 0.0D);
/* 447 */         this.rt.addValue(34, isArea ? 4.0D * stats.area / (3.141592653589793D * stats.major * stats.major) : 0.0D);
/* 448 */         this.rt.addValue(35, isArea ? stats.pixelCount / convexArea : (0.0D / 0.0D));
/*     */       }
/*     */     }
/*     */ 
/* 452 */     if ((this.measurements & 0x200) != 0) {
/* 453 */       if ((roi != null) && (roi.isLine())) {
/* 454 */         Rectangle bounds = roi.getBounds();
/* 455 */         this.rt.addValue(11, bounds.x);
/* 456 */         this.rt.addValue(12, bounds.y);
/* 457 */         this.rt.addValue(13, bounds.width);
/* 458 */         this.rt.addValue(14, bounds.height);
/*     */       } else {
/* 460 */         this.rt.addValue(11, stats.roiX);
/* 461 */         this.rt.addValue(12, stats.roiY);
/* 462 */         this.rt.addValue(13, stats.roiWidth);
/* 463 */         this.rt.addValue(14, stats.roiHeight);
/*     */       }
/*     */     }
/* 466 */     if ((this.measurements & 0x800) != 0) {
/* 467 */       this.rt.addValue(15, stats.major);
/* 468 */       this.rt.addValue(16, stats.minor);
/* 469 */       this.rt.addValue(17, stats.angle);
/*     */     }
/* 471 */     if ((this.measurements & 0x4000) != 0) {
/* 472 */       boolean extras = true;
/* 473 */       double FeretDiameter = (0.0D / 0.0D); double feretAngle = (0.0D / 0.0D); double minFeret = (0.0D / 0.0D);
/* 474 */       double feretX = (0.0D / 0.0D); double feretY = (0.0D / 0.0D);
/* 475 */       Roi roi2 = roi;
/* 476 */       if ((roi2 == null) && (this.imp != null))
/* 477 */         roi2 = new Roi(0, 0, this.imp.getWidth(), this.imp.getHeight());
/* 478 */       if (roi2 != null) {
/* 479 */         double[] a = roi2.getFeretValues();
/* 480 */         if (a != null) {
/* 481 */           FeretDiameter = a[0];
/* 482 */           feretAngle = a[1];
/* 483 */           minFeret = a[2];
/* 484 */           feretX = a[3];
/* 485 */           feretY = a[4];
/*     */         }
/*     */       }
/* 488 */       this.rt.addValue(19, FeretDiameter);
/* 489 */       this.rt.addValue(29, feretX);
/* 490 */       this.rt.addValue(30, feretY);
/* 491 */       this.rt.addValue(31, feretAngle);
/* 492 */       this.rt.addValue(32, minFeret);
/*     */     }
/* 494 */     if ((this.measurements & 0x8000) != 0) {
/* 495 */       this.rt.addValue(20, stats.area * stats.mean);
/* 496 */       this.rt.addValue(25, stats.pixelCount * stats.umean);
/*     */     }
/* 498 */     if ((this.measurements & 0x10000) != 0) this.rt.addValue(21, stats.median);
/* 499 */     if ((this.measurements & 0x20000) != 0) this.rt.addValue(22, stats.skewness);
/* 500 */     if ((this.measurements & 0x40000) != 0) this.rt.addValue(23, stats.kurtosis);
/* 501 */     if ((this.measurements & 0x80000) != 0) this.rt.addValue(24, stats.areaFraction);
/* 502 */     if ((this.measurements & 0x100000) != 0) {
/* 503 */       boolean update = false;
/* 504 */       if ((this.imp != null) && ((this.imp.isHyperStack()) || (this.imp.isComposite()))) {
/* 505 */         int[] position = this.imp.convertIndexToPosition(this.imp.getCurrentSlice());
/* 506 */         if (this.imp.getNChannels() > 1) {
/* 507 */           int index = this.rt.getColumnIndex("Ch");
/* 508 */           if ((index < 0) || (!this.rt.columnExists(index))) update = true;
/* 509 */           this.rt.addValue("Ch", position[0]);
/*     */         }
/* 511 */         if (this.imp.getNSlices() > 1) {
/* 512 */           int index = this.rt.getColumnIndex("Slice");
/* 513 */           if ((index < 0) || (!this.rt.columnExists(index))) update = true;
/* 514 */           this.rt.addValue("Slice", position[1]);
/*     */         }
/* 516 */         if (this.imp.getNFrames() > 1) {
/* 517 */           int index = this.rt.getColumnIndex("Frame");
/* 518 */           if ((index < 0) || (!this.rt.columnExists(index))) update = true;
/* 519 */           this.rt.addValue("Frame", position[2]);
/*     */         }
/*     */       } else {
/* 522 */         int index = this.rt.getColumnIndex("Slice");
/* 523 */         if ((index < 0) || (!this.rt.columnExists(index))) update = true;
/* 524 */         this.rt.addValue("Slice", this.imp != null ? this.imp.getCurrentSlice() : 1.0D);
/*     */       }
/* 526 */       if ((update) && (this.rt == systemRT)) this.rt.update(this.measurements, this.imp, roi);
/*     */     }
/* 528 */     if (roi != null)
/* 529 */       if (roi.isLine()) {
/* 530 */         this.rt.addValue("Length", roi.getLength());
/* 531 */         if (roi.getType() == 5) {
/* 532 */           double angle = 0.0D;
/* 533 */           Line l = (Line)roi;
/* 534 */           angle = roi.getAngle(l.x1, l.y1, l.x2, l.y2);
/* 535 */           this.rt.addValue("Angle", angle);
/*     */         }
/* 537 */       } else if (roi.getType() == 8) {
/* 538 */         double angle = ((PolygonRoi)roi).getAngle();
/* 539 */         if (Prefs.reflexAngle) angle = 360.0D - angle;
/* 540 */         this.rt.addValue("Angle", angle);
/*     */       }
/* 542 */       else if (roi.getType() == 10) {
/* 543 */         savePoints(roi);
/*     */       }
/*     */   }
/*     */ 
/*     */   final double getArea(Polygon p) {
/* 548 */     if (p == null) return (0.0D / 0.0D);
/* 549 */     int carea = 0;
/*     */ 
/* 551 */     for (int i = 0; i < p.npoints; i++) {
/* 552 */       int iminus1 = i - 1;
/* 553 */       if (iminus1 < 0) iminus1 = p.npoints - 1;
/* 554 */       carea += (p.xpoints[i] + p.xpoints[iminus1]) * (p.ypoints[i] - p.ypoints[iminus1]);
/*     */     }
/* 556 */     return Math.abs(carea / 2.0D);
/*     */   }
/*     */ 
/*     */   void savePoints(Roi roi)
/*     */   {
/* 586 */     if (this.imp == null) {
/* 587 */       this.rt.addValue("X", 0.0D);
/* 588 */       this.rt.addValue("Y", 0.0D);
/* 589 */       if (this.imp.getStackSize() > 1)
/* 590 */         this.rt.addValue("Slice", 0.0D);
/* 591 */       return;
/*     */     }
/* 593 */     if ((this.measurements & 0x1) != 0)
/* 594 */       this.rt.addValue(0, 0.0D);
/* 595 */     Polygon p = roi.getPolygon();
/* 596 */     ImageProcessor ip = this.imp.getProcessor();
/* 597 */     Calibration cal = this.imp.getCalibration();
/* 598 */     int x = p.xpoints[0];
/* 599 */     int y = p.ypoints[0];
/* 600 */     double value = ip.getPixelValue(x, y);
/* 601 */     if ((markWidth > 0) && (!Toolbar.getMultiPointMode())) {
/* 602 */       ip.setColor(Toolbar.getForegroundColor());
/* 603 */       ip.setLineWidth(markWidth);
/* 604 */       ip.moveTo(x, y);
/* 605 */       ip.lineTo(x, y);
/* 606 */       this.imp.updateAndDraw();
/* 607 */       ip.setLineWidth(Line.getWidth());
/*     */     }
/* 609 */     this.rt.addValue("X", cal.getX(x));
/* 610 */     this.rt.addValue("Y", cal.getY(y, this.imp.getHeight()));
/* 611 */     if ((this.imp.isHyperStack()) || (this.imp.isComposite())) {
/* 612 */       if (this.imp.getNChannels() > 1)
/* 613 */         this.rt.addValue("Ch", this.imp.getChannel());
/* 614 */       if (this.imp.getNSlices() > 1)
/* 615 */         this.rt.addValue("Slice", this.imp.getSlice());
/* 616 */       if (this.imp.getNFrames() > 1)
/* 617 */         this.rt.addValue("Frame", this.imp.getFrame());
/* 618 */     } else if (this.imp.getStackSize() > 1) {
/* 619 */       this.rt.addValue("Slice", cal.getZ(this.imp.getCurrentSlice()));
/* 620 */     }if (this.imp.getProperty("FHT") != null) {
/* 621 */       double center = this.imp.getWidth() / 2.0D;
/* 622 */       y = this.imp.getHeight() - y - 1;
/* 623 */       double r = Math.sqrt((x - center) * (x - center) + (y - center) * (y - center));
/* 624 */       if (r < 1.0D) r = 1.0D;
/* 625 */       double theta = Math.atan2(y - center, x - center);
/* 626 */       theta = theta * 180.0D / 3.141592653589793D;
/* 627 */       if (theta < 0.0D) theta = 360.0D + theta;
/* 628 */       this.rt.addValue("R", this.imp.getWidth() / r * cal.pixelWidth);
/* 629 */       this.rt.addValue("Theta", theta);
/*     */     }
/*     */   }
/*     */ 
/*     */   String getFileName()
/*     */   {
/* 636 */     String s = "";
/* 637 */     if (this.imp != null) {
/* 638 */       if (redirectTarget != 0) {
/* 639 */         ImagePlus rImp = WindowManager.getImage(redirectTarget);
/* 640 */         if (rImp == null) rImp = redirectImage;
/* 641 */         if (rImp != null) s = rImp.getTitle(); 
/*     */       }
/* 643 */       else { s = this.imp.getTitle(); }
/*     */ 
/*     */ 
/* 647 */       Roi roi = this.imp.getRoi();
/* 648 */       String roiName = roi != null ? roi.getName() : null;
/* 649 */       if (roiName != null)
/* 650 */         s = s + ":" + roiName;
/* 651 */       if (this.imp.getStackSize() > 1) {
/* 652 */         ImageStack stack = this.imp.getStack();
/* 653 */         int currentSlice = this.imp.getCurrentSlice();
/* 654 */         String label = stack.getShortSliceLabel(currentSlice);
/* 655 */         String colon = s.equals("") ? "" : ":";
/* 656 */         if ((label != null) && (!label.equals("")))
/* 657 */           s = s + colon + label;
/*     */         else
/* 659 */           s = s + colon + currentSlice;
/*     */       }
/*     */     }
/* 662 */     return s;
/*     */   }
/*     */ 
/*     */   public void displayResults()
/*     */   {
/* 667 */     int counter = this.rt.getCounter();
/* 668 */     if (counter == 1)
/* 669 */       IJ.setColumnHeadings(this.rt.getColumnHeadings());
/* 670 */     IJ.write(this.rt.getRowAsString(counter - 1));
/*     */   }
/*     */ 
/*     */   public void updateHeadings()
/*     */   {
/* 675 */     this.rt.show("Results");
/*     */   }
/*     */ 
/*     */   public String n(double n)
/*     */   {
/*     */     String s;
/*     */     String s;
/* 681 */     if (Math.round(n) == n)
/* 682 */       s = ResultsTable.d2s(n, 0);
/*     */     else
/* 684 */       s = ResultsTable.d2s(n, precision);
/* 685 */     return s + "\t";
/*     */   }
/*     */ 
/*     */   void incrementCounter()
/*     */   {
/* 690 */     if (this.rt == null) this.rt = systemRT;
/* 691 */     this.rt.incrementCounter();
/* 692 */     unsavedMeasurements = true;
/*     */   }
/*     */ 
/*     */   public void summarize() {
/* 696 */     this.rt = systemRT;
/* 697 */     if (this.rt.getCounter() == 0)
/* 698 */       return;
/* 699 */     if (summarized)
/* 700 */       this.rt.show("Results");
/* 701 */     this.measurements = systemMeasurements;
/* 702 */     this.min = new StringBuffer(100);
/* 703 */     this.max = new StringBuffer(100);
/* 704 */     this.mean = new StringBuffer(100);
/* 705 */     this.sd = new StringBuffer(100);
/* 706 */     this.min.append("Min\t");
/* 707 */     this.max.append("Max\t");
/* 708 */     this.mean.append("Mean\t");
/* 709 */     this.sd.append("SD\t");
/* 710 */     if ((this.measurements & 0x400) != 0) {
/* 711 */       this.min.append("\t");
/* 712 */       this.max.append("\t");
/* 713 */       this.mean.append("\t");
/* 714 */       this.sd.append("\t");
/*     */     }
/* 716 */     summarizeAreas();
/* 717 */     if ((this.measurements & 0x800) == 0) {
/* 718 */       int index = this.rt.getColumnIndex("Angle");
/* 719 */       if (this.rt.columnExists(index)) add2(index);
/*     */     }
/* 721 */     int index = this.rt.getColumnIndex("Length");
/* 722 */     if (this.rt.columnExists(index)) add2(index);
/* 723 */     TextPanel tp = IJ.getTextPanel();
/* 724 */     if (tp != null) {
/* 725 */       String worksheetHeadings = tp.getColumnHeadings();
/* 726 */       if (worksheetHeadings.equals(""))
/* 727 */         IJ.setColumnHeadings(this.rt.getColumnHeadings());
/*     */     }
/* 729 */     IJ.write("");
/* 730 */     String meanS = new String(this.mean);
/* 731 */     String sdS = new String(this.sd);
/* 732 */     String minS = new String(this.min);
/* 733 */     String maxS = new String(this.max);
/* 734 */     if (meanS.endsWith("\t"))
/* 735 */       meanS = meanS.substring(0, meanS.length() - 1);
/* 736 */     if (sdS.endsWith("\t"))
/* 737 */       sdS = sdS.substring(0, sdS.length() - 1);
/* 738 */     if (minS.endsWith("\t"))
/* 739 */       minS = minS.substring(0, minS.length() - 1);
/* 740 */     if (maxS.endsWith("\t"))
/* 741 */       maxS = maxS.substring(0, maxS.length() - 1);
/* 742 */     IJ.write(meanS);
/* 743 */     IJ.write(sdS);
/* 744 */     IJ.write(minS);
/* 745 */     IJ.write(maxS);
/* 746 */     IJ.write("");
/* 747 */     this.mean = null;
/* 748 */     this.sd = null;
/* 749 */     this.min = null;
/* 750 */     this.max = null;
/* 751 */     summarized = true;
/*     */   }
/*     */ 
/*     */   void summarizeAreas() {
/* 755 */     if ((this.measurements & 0x1) != 0) add2(0);
/* 756 */     if ((this.measurements & 0x2) != 0) add2(1);
/* 757 */     if ((this.measurements & 0x4) != 0) add2(2);
/* 758 */     if ((this.measurements & 0x8) != 0) add2(3);
/* 759 */     if ((this.measurements & 0x10) != 0) {
/* 760 */       if (showMin) add2(4);
/* 761 */       add2(5);
/*     */     }
/* 763 */     if ((this.measurements & 0x20) != 0) {
/* 764 */       add2(6);
/* 765 */       add2(7);
/*     */     }
/* 767 */     if ((this.measurements & 0x40) != 0) {
/* 768 */       add2(8);
/* 769 */       add2(9);
/*     */     }
/* 771 */     if ((this.measurements & 0x80) != 0)
/* 772 */       add2(10);
/* 773 */     if ((this.measurements & 0x200) != 0) {
/* 774 */       add2(11);
/* 775 */       add2(12);
/* 776 */       add2(13);
/* 777 */       add2(14);
/*     */     }
/* 779 */     if ((this.measurements & 0x800) != 0) {
/* 780 */       add2(15);
/* 781 */       add2(16);
/* 782 */       add2(17);
/*     */     }
/* 784 */     if ((this.measurements & 0x2000) != 0)
/* 785 */       add2(18);
/* 786 */     if ((this.measurements & 0x4000) != 0)
/* 787 */       add2(19);
/* 788 */     if ((this.measurements & 0x8000) != 0) {
/* 789 */       add2(20);
/* 790 */       add2(25);
/*     */     }
/* 792 */     if ((this.measurements & 0x10000) != 0)
/* 793 */       add2(21);
/* 794 */     if ((this.measurements & 0x20000) != 0)
/* 795 */       add2(22);
/* 796 */     if ((this.measurements & 0x40000) != 0)
/* 797 */       add2(23);
/* 798 */     if ((this.measurements & 0x80000) != 0)
/* 799 */       add2(24);
/* 800 */     if ((this.measurements & 0x100000) != 0) {
/* 801 */       int index = this.rt.getColumnIndex("Ch");
/* 802 */       if (this.rt.columnExists(index)) add2(index);
/* 803 */       index = this.rt.getColumnIndex("Slice");
/* 804 */       if (this.rt.columnExists(index)) add2(index);
/* 805 */       index = this.rt.getColumnIndex("Frame");
/* 806 */       if (this.rt.columnExists(index)) add2(index);
/*     */     }
/* 808 */     if ((this.measurements & 0x4000) != 0) {
/* 809 */       add2(29);
/* 810 */       add2(30);
/* 811 */       add2(31);
/* 812 */       add2(32);
/*     */     }
/* 814 */     if ((this.measurements & 0x2000) != 0) {
/* 815 */       add2(33);
/* 816 */       add2(34);
/* 817 */       add2(35);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void add2(int column) {
/* 822 */     float[] c = column >= 0 ? this.rt.getColumn(column) : null;
/* 823 */     if (c != null) {
/* 824 */       ImageProcessor ip = new FloatProcessor(c.length, 1, c, null);
/* 825 */       if (ip == null)
/* 826 */         return;
/* 827 */       ImageStatistics stats = new FloatStatistics(ip);
/* 828 */       if (stats == null)
/* 829 */         return;
/* 830 */       this.mean.append(n(stats.mean));
/* 831 */       this.min.append(n(stats.min));
/* 832 */       this.max.append(n(stats.max));
/* 833 */       this.sd.append(n(stats.stdDev));
/*     */     } else {
/* 835 */       this.mean.append("-\t");
/* 836 */       this.min.append("-\t");
/* 837 */       this.max.append("-\t");
/* 838 */       this.sd.append("-\t");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int getCounter()
/*     */   {
/* 844 */     return systemRT.getCounter();
/*     */   }
/*     */ 
/*     */   public static synchronized boolean resetCounter()
/*     */   {
/* 852 */     TextPanel tp = IJ.isResultsWindow() ? IJ.getTextPanel() : null;
/* 853 */     int counter = systemRT.getCounter();
/* 854 */     int lineCount = tp != null ? IJ.getTextPanel().getLineCount() : 0;
/* 855 */     ImageJ ij = IJ.getInstance();
/* 856 */     boolean macro = ((IJ.macroRunning()) && (!switchingModes)) || (Interpreter.isBatchMode());
/* 857 */     switchingModes = false;
/* 858 */     if ((counter > 0) && (lineCount > 0) && (unsavedMeasurements) && (!macro) && (ij != null) && (!ij.quitting())) {
/* 859 */       YesNoCancelDialog d = new YesNoCancelDialog(ij, "ImageJ", "Save " + counter + " measurements?");
/* 860 */       if (d.cancelPressed())
/* 861 */         return false;
/* 862 */       if ((d.yesPressed()) && 
/* 863 */         (!new MeasurementsWriter().save(""))) {
/* 864 */         return false;
/*     */       }
/*     */     }
/* 867 */     umeans = null;
/* 868 */     systemRT.reset();
/* 869 */     unsavedMeasurements = false;
/* 870 */     if (tp != null) tp.clear();
/* 871 */     summarized = false;
/* 872 */     return true;
/*     */   }
/*     */ 
/*     */   public static void setUnsavedMeasurements(boolean b) {
/* 876 */     unsavedMeasurements = b;
/*     */   }
/*     */ 
/*     */   public static int getMeasurements()
/*     */   {
/* 881 */     return systemMeasurements;
/*     */   }
/*     */ 
/*     */   public static void setMeasurements(int measurements)
/*     */   {
/* 886 */     systemMeasurements = measurements;
/*     */   }
/*     */ 
/*     */   public static void setMeasurement(int option, boolean state)
/*     */   {
/* 891 */     if (state)
/* 892 */       systemMeasurements |= option;
/*     */     else
/* 894 */       systemMeasurements &= (option ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   public static void savePreferences(Properties prefs)
/*     */   {
/* 899 */     prefs.put("measurements", Integer.toString(systemMeasurements));
/* 900 */     prefs.put("mark.width", Integer.toString(markWidth));
/* 901 */     prefs.put("precision", Integer.toString(precision));
/*     */   }
/*     */ 
/*     */   public static float[] getUMeans() {
/* 905 */     return umeans;
/*     */   }
/*     */ 
/*     */   public static ResultsTable getResultsTable()
/*     */   {
/* 911 */     return systemRT;
/*     */   }
/*     */ 
/*     */   public static int getPrecision()
/*     */   {
/* 916 */     return precision;
/*     */   }
/*     */ 
/*     */   public static void setPrecision(int decimalPlaces)
/*     */   {
/* 921 */     if (decimalPlaces < 0) decimalPlaces = 0;
/* 922 */     if (decimalPlaces > 9) decimalPlaces = 9;
/* 923 */     precision = decimalPlaces;
/*     */   }
/*     */ 
/*     */   public static int updateY(int y, int imageHeight)
/*     */   {
/* 929 */     if ((systemMeasurements & 0x1000) != 0)
/* 930 */       y = imageHeight - y - 1;
/* 931 */     return y;
/*     */   }
/*     */ 
/*     */   public static double updateY(double y, int imageHeight)
/*     */   {
/* 937 */     if ((systemMeasurements & 0x1000) != 0)
/* 938 */       y = imageHeight - y - 1.0D;
/* 939 */     return y;
/*     */   }
/*     */ 
/*     */   public static void setDefaultHeadings()
/*     */   {
/* 944 */     systemRT.setDefaultHeadings();
/*     */   }
/*     */ 
/*     */   public static void setOption(String option, boolean b) {
/* 948 */     if (option.indexOf("min") != -1)
/* 949 */       showMin = b;
/*     */   }
/*     */ 
/*     */   public static void setResultsTable(ResultsTable rt) {
/* 953 */     if (rt == null) rt = new ResultsTable();
/* 954 */     systemRT = rt;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Analyzer
 * JD-Core Version:    0.6.2
 */