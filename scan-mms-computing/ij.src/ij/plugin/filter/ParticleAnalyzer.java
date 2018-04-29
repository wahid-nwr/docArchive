/*      */ package ij.plugin.filter;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.ImageStack;
/*      */ import ij.LookUpTable;
/*      */ import ij.Macro;
/*      */ import ij.Prefs;
/*      */ import ij.Undo;
/*      */ import ij.WindowManager;
/*      */ import ij.gui.GenericDialog;
/*      */ import ij.gui.ImageWindow;
/*      */ import ij.gui.Overlay;
/*      */ import ij.gui.PolygonRoi;
/*      */ import ij.gui.Roi;
/*      */ import ij.gui.Wand;
/*      */ import ij.macro.Interpreter;
/*      */ import ij.measure.Calibration;
/*      */ import ij.measure.Measurements;
/*      */ import ij.measure.ResultsTable;
/*      */ import ij.plugin.frame.RoiManager;
/*      */ import ij.process.ByteProcessor;
/*      */ import ij.process.ByteStatistics;
/*      */ import ij.process.ColorProcessor;
/*      */ import ij.process.ColorStatistics;
/*      */ import ij.process.FloatProcessor;
/*      */ import ij.process.FloatStatistics;
/*      */ import ij.process.FloodFiller;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.process.ImageStatistics;
/*      */ import ij.process.PolygonFiller;
/*      */ import ij.process.ShortProcessor;
/*      */ import ij.process.ShortStatistics;
/*      */ import ij.text.TextPanel;
/*      */ import ij.text.TextWindow;
/*      */ import ij.util.Tools;
/*      */ import java.awt.Color;
/*      */ import java.awt.Font;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Polygon;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.util.Properties;
/*      */ 
/*      */ public class ParticleAnalyzer
/*      */   implements PlugInFilter, Measurements
/*      */ {
/*      */   public static final int SHOW_RESULTS = 1;
/*      */   public static final int SHOW_SUMMARY = 2;
/*      */   public static final int SHOW_OUTLINES = 4;
/*      */   public static final int EXCLUDE_EDGE_PARTICLES = 8;
/*      */   public static final int SHOW_ROI_MASKS = 16;
/*      */   public static final int SHOW_PROGRESS = 32;
/*      */   public static final int CLEAR_WORKSHEET = 64;
/*      */   public static final int RECORD_STARTS = 128;
/*      */   public static final int DISPLAY_SUMMARY = 256;
/*      */   public static final int SHOW_NONE = 512;
/*      */   public static final int INCLUDE_HOLES = 1024;
/*      */   public static final int ADD_TO_MANAGER = 2048;
/*      */   public static final int SHOW_MASKS = 4096;
/*      */   public static final int FOUR_CONNECTED = 8192;
/*      */   public static final int IN_SITU_SHOW = 16384;
/*      */   public static final int SHOW_OVERLAY_OUTLINES = 32768;
/*      */   public static final int SHOW_OVERLAY_MASKS = 65536;
/*      */   static final String OPTIONS = "ap.options";
/*      */   static final int BYTE = 0;
/*      */   static final int SHORT = 1;
/*      */   static final int FLOAT = 2;
/*      */   static final int RGB = 3;
/*      */   static final double DEFAULT_MIN_SIZE = 0.0D;
/*      */   static final double DEFAULT_MAX_SIZE = (1.0D / 0.0D);
/*   88 */   private static double staticMinSize = 0.0D;
/*   89 */   private static double staticMaxSize = (1.0D / 0.0D);
/*      */   private static boolean pixelUnits;
/*   91 */   private static int staticOptions = Prefs.getInt("ap.options", 64);
/*   92 */   private static String[] showStrings = { "Nothing", "Outlines", "Bare Outlines", "Ellipses", "Masks", "Count Masks", "Overlay Outlines", "Overlay Masks" };
/*   93 */   private static double staticMinCircularity = 0.0D; private static double staticMaxCircularity = 1.0D;
/*      */   private static String prevHdr;
/*      */   protected static final int NOTHING = 0;
/*      */   protected static final int OUTLINES = 1;
/*      */   protected static final int BARE_OUTLINES = 2;
/*      */   protected static final int ELLIPSES = 3;
/*      */   protected static final int MASKS = 4;
/*      */   protected static final int ROI_MASKS = 5;
/*      */   protected static final int OVERLAY_OUTLINES = 6;
/*      */   protected static final int OVERLAY_MASKS = 7;
/*      */   protected static int staticShowChoice;
/*      */   protected ImagePlus imp;
/*      */   protected ResultsTable rt;
/*      */   protected Analyzer analyzer;
/*      */   protected int slice;
/*      */   protected boolean processStack;
/*      */   protected boolean showResults;
/*      */   protected boolean excludeEdgeParticles;
/*      */   protected boolean showSizeDistribution;
/*      */   protected boolean resetCounter;
/*      */   protected boolean showProgress;
/*      */   protected boolean recordStarts;
/*      */   protected boolean displaySummary;
/*      */   protected boolean floodFill;
/*      */   protected boolean addToManager;
/*      */   protected boolean inSituShow;
/*  108 */   private String summaryHdr = "Slice\tCount\tTotal Area\tAverage Size\tArea Fraction";
/*      */   private double level1;
/*      */   private double level2;
/*      */   private double minSize;
/*      */   private double maxSize;
/*      */   private double minCircularity;
/*      */   private double maxCircularity;
/*      */   private int showChoice;
/*      */   private int options;
/*      */   private int measurements;
/*      */   private Calibration calibration;
/*      */   private String arg;
/*      */   private double fillColor;
/*      */   private boolean thresholdingLUT;
/*      */   private ImageProcessor drawIP;
/*      */   private int width;
/*      */   private int height;
/*      */   private boolean canceled;
/*      */   private ImageStack outlines;
/*      */   private IndexColorModel customLut;
/*      */   private int particleCount;
/*  125 */   private int maxParticleCount = 0;
/*      */   private int totalCount;
/*      */   private TextWindow tw;
/*      */   private Wand wand;
/*      */   private int imageType;
/*      */   private int imageType2;
/*      */   private boolean roiNeedsImage;
/*      */   private int minX;
/*      */   private int maxX;
/*      */   private int minY;
/*      */   private int maxY;
/*      */   private ImagePlus redirectImp;
/*      */   private ImageProcessor redirectIP;
/*      */   private PolygonFiller pf;
/*      */   private Roi saveRoi;
/*      */   private int beginningCount;
/*      */   private Rectangle r;
/*      */   private ImageProcessor mask;
/*      */   private double totalArea;
/*      */   private FloodFiller ff;
/*      */   private Polygon polygon;
/*      */   private RoiManager roiManager;
/*      */   private ImagePlus outputImage;
/*      */   private boolean hideOutputImage;
/*      */   private int roiType;
/*  146 */   private int wandMode = 1;
/*      */   private Overlay overlay;
/*      */   boolean blackBackground;
/*  149 */   private static int defaultFontSize = 9;
/*  150 */   private static int nextFontSize = defaultFontSize;
/*  151 */   private static int nextLineWidth = 1;
/*  152 */   private int fontSize = nextFontSize;
/*  153 */   private int lineWidth = nextLineWidth;
/*      */ 
/*  783 */   int counter = 0;
/*      */ 
/*      */   public ParticleAnalyzer(int options, int measurements, ResultsTable rt, double minSize, double maxSize, double minCirc, double maxCirc)
/*      */   {
/*  166 */     this.options = options;
/*  167 */     this.measurements = measurements;
/*  168 */     this.rt = rt;
/*  169 */     if (this.rt == null)
/*  170 */       this.rt = new ResultsTable();
/*  171 */     this.minSize = minSize;
/*  172 */     this.maxSize = maxSize;
/*  173 */     this.minCircularity = minCirc;
/*  174 */     this.maxCircularity = maxCirc;
/*  175 */     this.slice = 1;
/*  176 */     if ((options & 0x10) != 0)
/*  177 */       this.showChoice = 5;
/*  178 */     if ((options & 0x8000) != 0)
/*  179 */       this.showChoice = 6;
/*  180 */     if ((options & 0x10000) != 0)
/*  181 */       this.showChoice = 7;
/*  182 */     if ((options & 0x4) != 0)
/*  183 */       this.showChoice = 1;
/*  184 */     if ((options & 0x1000) != 0)
/*  185 */       this.showChoice = 4;
/*  186 */     if ((options & 0x200) != 0)
/*  187 */       this.showChoice = 0;
/*  188 */     if ((options & 0x2000) != 0) {
/*  189 */       this.wandMode = 4;
/*  190 */       options |= 1024;
/*      */     }
/*  192 */     nextFontSize = defaultFontSize; nextLineWidth = 1;
/*      */   }
/*      */ 
/*      */   public ParticleAnalyzer(int options, int measurements, ResultsTable rt, double minSize, double maxSize)
/*      */   {
/*  197 */     this(options, measurements, rt, minSize, maxSize, 0.0D, 1.0D);
/*      */   }
/*      */ 
/*      */   public ParticleAnalyzer()
/*      */   {
/*  202 */     this.slice = 1;
/*      */   }
/*      */ 
/*      */   public int setup(String arg, ImagePlus imp) {
/*  206 */     this.arg = arg;
/*  207 */     this.imp = imp;
/*  208 */     IJ.register(ParticleAnalyzer.class);
/*  209 */     if (imp == null) {
/*  210 */       IJ.noImage(); return 4096;
/*  211 */     }if ((imp.getBitDepth() == 24) && (!isThresholdedRGB(imp))) {
/*  212 */       IJ.error("Particle Analyzer", "RGB images must be thresholded using\nImage>Adjust>Color Threshold.");
/*      */ 
/*  215 */       return 4096;
/*      */     }
/*  217 */     if (!showDialog())
/*  218 */       return 4096;
/*  219 */     int baseFlags = 415;
/*  220 */     int flags = IJ.setupDialog(imp, baseFlags);
/*  221 */     this.processStack = ((flags & 0x20) != 0);
/*  222 */     this.slice = 0;
/*  223 */     this.saveRoi = imp.getRoi();
/*  224 */     if ((this.saveRoi != null) && (this.saveRoi.getType() != 0) && (this.saveRoi.isArea()))
/*  225 */       this.polygon = this.saveRoi.getPolygon();
/*  226 */     imp.startTiming();
/*  227 */     nextFontSize = defaultFontSize; nextLineWidth = 1;
/*  228 */     return flags;
/*      */   }
/*      */ 
/*      */   public void run(ImageProcessor ip) {
/*  232 */     if (this.canceled)
/*  233 */       return;
/*  234 */     this.slice += 1;
/*  235 */     if ((this.imp.getStackSize() > 1) && (this.processStack))
/*  236 */       this.imp.setSlice(this.slice);
/*  237 */     if (this.imp.getType() == 4) {
/*  238 */       ip = (ImageProcessor)this.imp.getProperty("Mask");
/*  239 */       ip.setThreshold(255.0D, 255.0D, 2);
/*      */     }
/*  241 */     if (!analyze(this.imp, ip))
/*  242 */       this.canceled = true;
/*  243 */     if (this.slice == this.imp.getStackSize()) {
/*  244 */       this.imp.updateAndDraw();
/*  245 */       if (this.saveRoi != null) this.imp.setRoi(this.saveRoi);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean showDialog()
/*      */   {
/*  251 */     Calibration cal = this.imp != null ? this.imp.getCalibration() : new Calibration();
/*  252 */     double unitSquared = cal.pixelWidth * cal.pixelHeight;
/*  253 */     if (pixelUnits)
/*  254 */       unitSquared = 1.0D;
/*  255 */     if (Macro.getOptions() != null) {
/*  256 */       boolean oldMacro = updateMacroOptions();
/*  257 */       if (oldMacro) unitSquared = 1.0D;
/*  258 */       staticMinSize = 0.0D; staticMaxSize = (1.0D / 0.0D);
/*  259 */       staticMinCircularity = 0.0D; staticMaxCircularity = 1.0D;
/*  260 */       staticShowChoice = 0;
/*      */     }
/*  262 */     GenericDialog gd = new GenericDialog("Analyze Particles");
/*  263 */     this.minSize = staticMinSize;
/*  264 */     this.maxSize = staticMaxSize;
/*  265 */     this.minCircularity = staticMinCircularity;
/*  266 */     this.maxCircularity = staticMaxCircularity;
/*  267 */     this.showChoice = staticShowChoice;
/*  268 */     if (this.maxSize == 999999.0D) this.maxSize = (1.0D / 0.0D);
/*  269 */     this.options = staticOptions;
/*  270 */     String unit = cal.getUnit();
/*  271 */     boolean scaled = cal.scaled();
/*  272 */     if (unit.equals("inch")) {
/*  273 */       unit = "pixel";
/*  274 */       unitSquared = 1.0D;
/*  275 */       scaled = false;
/*  276 */       pixelUnits = true;
/*      */     }
/*  278 */     String units = unit + "^2";
/*  279 */     int places = 0;
/*  280 */     double cmin = this.minSize * unitSquared;
/*  281 */     if ((int)cmin != cmin) places = 2;
/*  282 */     double cmax = this.maxSize * unitSquared;
/*  283 */     if (((int)cmax != cmax) && (cmax != (1.0D / 0.0D))) places = 2;
/*  284 */     String minStr = ResultsTable.d2s(cmin, places);
/*  285 */     if (minStr.indexOf("-") != -1)
/*  286 */       for (int i = places; i <= 6; i++) {
/*  287 */         minStr = ResultsTable.d2s(cmin, i);
/*  288 */         if (minStr.indexOf("-") == -1)
/*      */           break;
/*      */       }
/*  291 */     String maxStr = ResultsTable.d2s(cmax, places);
/*  292 */     if (maxStr.indexOf("-") != -1)
/*  293 */       for (int i = places; i <= 6; i++) {
/*  294 */         maxStr = ResultsTable.d2s(cmax, i);
/*  295 */         if (maxStr.indexOf("-") == -1)
/*      */           break;
/*      */       }
/*  298 */     if (scaled)
/*  299 */       gd.setInsets(5, 0, 0);
/*  300 */     gd.addStringField("Size (" + units + "):", minStr + "-" + maxStr, 12);
/*  301 */     if (scaled) {
/*  302 */       gd.setInsets(0, 40, 5);
/*  303 */       gd.addCheckbox("Pixel units", pixelUnits);
/*      */     }
/*  305 */     gd.addStringField("Circularity:", IJ.d2s(this.minCircularity) + "-" + IJ.d2s(this.maxCircularity), 12);
/*  306 */     gd.addChoice("Show:", showStrings, showStrings[this.showChoice]);
/*  307 */     String[] labels = new String[8];
/*  308 */     boolean[] states = new boolean[8];
/*  309 */     labels[0] = "Display results"; states[0] = ((this.options & 0x1) != 0 ? 1 : false);
/*  310 */     labels[1] = "Exclude on edges"; states[1] = ((this.options & 0x8) != 0 ? 1 : false);
/*  311 */     labels[2] = "Clear results"; states[2] = ((this.options & 0x40) != 0 ? 1 : false);
/*  312 */     labels[3] = "Include holes"; states[3] = ((this.options & 0x400) != 0 ? 1 : false);
/*  313 */     labels[4] = "Summarize"; states[4] = ((this.options & 0x100) != 0 ? 1 : false);
/*  314 */     labels[5] = "Record starts"; states[5] = ((this.options & 0x80) != 0 ? 1 : false);
/*  315 */     labels[6] = "Add to Manager"; states[6] = ((this.options & 0x800) != 0 ? 1 : false);
/*  316 */     labels[7] = "In_situ Show"; states[7] = ((this.options & 0x4000) != 0 ? 1 : false);
/*  317 */     gd.addCheckboxGroup(4, 2, labels, states);
/*  318 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/analyze.html#ap");
/*  319 */     gd.showDialog();
/*  320 */     if (gd.wasCanceled()) {
/*  321 */       return false;
/*      */     }
/*  323 */     String size = gd.getNextString();
/*  324 */     if (scaled)
/*  325 */       pixelUnits = gd.getNextBoolean();
/*  326 */     if (pixelUnits)
/*  327 */       unitSquared = 1.0D;
/*      */     else
/*  329 */       unitSquared = cal.pixelWidth * cal.pixelHeight;
/*  330 */     String[] minAndMax = Tools.split(size, " -");
/*  331 */     double mins = gd.parseDouble(minAndMax[0]);
/*  332 */     double maxs = minAndMax.length == 2 ? gd.parseDouble(minAndMax[1]) : (0.0D / 0.0D);
/*  333 */     this.minSize = (Double.isNaN(mins) ? 0.0D : mins / unitSquared);
/*  334 */     this.maxSize = (Double.isNaN(maxs) ? (1.0D / 0.0D) : maxs / unitSquared);
/*  335 */     if (this.minSize < 0.0D) this.minSize = 0.0D;
/*  336 */     if (this.maxSize < this.minSize) this.maxSize = (1.0D / 0.0D);
/*  337 */     staticMinSize = this.minSize;
/*  338 */     staticMaxSize = this.maxSize;
/*      */ 
/*  340 */     minAndMax = Tools.split(gd.getNextString(), " -");
/*  341 */     double minc = gd.parseDouble(minAndMax[0]);
/*  342 */     double maxc = minAndMax.length == 2 ? gd.parseDouble(minAndMax[1]) : (0.0D / 0.0D);
/*  343 */     this.minCircularity = (Double.isNaN(minc) ? 0.0D : minc);
/*  344 */     this.maxCircularity = (Double.isNaN(maxc) ? 1.0D : maxc);
/*  345 */     if ((this.minCircularity < 0.0D) || (this.minCircularity > 1.0D)) this.minCircularity = 0.0D;
/*  346 */     if ((this.maxCircularity < this.minCircularity) || (this.maxCircularity > 1.0D)) this.maxCircularity = 1.0D;
/*  347 */     if ((this.minCircularity == 1.0D) && (this.maxCircularity == 1.0D)) this.minCircularity = 0.0D;
/*  348 */     staticMinCircularity = this.minCircularity;
/*  349 */     staticMaxCircularity = this.maxCircularity;
/*      */ 
/*  351 */     if (gd.invalidNumber()) {
/*  352 */       IJ.error("Bins invalid.");
/*  353 */       this.canceled = true;
/*  354 */       return false;
/*      */     }
/*  356 */     this.showChoice = gd.getNextChoiceIndex();
/*  357 */     staticShowChoice = this.showChoice;
/*  358 */     if (gd.getNextBoolean())
/*  359 */       this.options |= 1; else this.options &= -2;
/*  360 */     if (gd.getNextBoolean())
/*  361 */       this.options |= 8; else this.options &= -9;
/*  362 */     if (gd.getNextBoolean())
/*  363 */       this.options |= 64; else this.options &= -65;
/*  364 */     if (gd.getNextBoolean())
/*  365 */       this.options |= 1024; else this.options &= -1025;
/*  366 */     if (gd.getNextBoolean())
/*  367 */       this.options |= 256; else this.options &= -257;
/*  368 */     if (gd.getNextBoolean())
/*  369 */       this.options |= 128; else this.options &= -129;
/*  370 */     if (gd.getNextBoolean())
/*  371 */       this.options |= 2048; else this.options &= -2049;
/*  372 */     if (gd.getNextBoolean())
/*  373 */       this.options |= 16384; else this.options &= -16385;
/*  374 */     staticOptions = this.options;
/*  375 */     this.options |= 32;
/*  376 */     if ((this.options & 0x100) != 0)
/*  377 */       Analyzer.setMeasurements(Analyzer.getMeasurements() | 0x1);
/*  378 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean isThresholdedRGB(ImagePlus imp) {
/*  382 */     Object obj = imp.getProperty("Mask");
/*  383 */     if ((obj == null) || (!(obj instanceof ImageProcessor)))
/*  384 */       return false;
/*  385 */     ImageProcessor mask = (ImageProcessor)obj;
/*  386 */     return (mask.getWidth() == imp.getWidth()) && (mask.getHeight() == imp.getHeight());
/*      */   }
/*      */ 
/*      */   boolean updateMacroOptions() {
/*  390 */     String options = Macro.getOptions();
/*  391 */     int index = options.indexOf("maximum=");
/*  392 */     if (index == -1) return false;
/*  393 */     index += 8;
/*  394 */     int len = options.length();
/*  395 */     while ((index < len - 1) && (options.charAt(index) != ' '))
/*  396 */       index++;
/*  397 */     if (index == len - 1) return false;
/*  398 */     int min = (int)Tools.parseDouble(Macro.getValue(options, "minimum", "1"));
/*  399 */     int max = (int)Tools.parseDouble(Macro.getValue(options, "maximum", "999999"));
/*  400 */     options = "size=" + min + "-" + max + options.substring(index, len);
/*  401 */     Macro.setOptions(options);
/*  402 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean analyze(ImagePlus imp)
/*      */   {
/*  408 */     return analyze(imp, imp.getProcessor());
/*      */   }
/*      */ 
/*      */   public boolean analyze(ImagePlus imp, ImageProcessor ip)
/*      */   {
/*  414 */     if (this.imp == null) this.imp = imp;
/*  415 */     this.showResults = ((this.options & 0x1) != 0);
/*  416 */     this.excludeEdgeParticles = ((this.options & 0x8) != 0);
/*  417 */     this.resetCounter = ((this.options & 0x40) != 0);
/*  418 */     this.showProgress = ((this.options & 0x20) != 0);
/*  419 */     this.floodFill = ((this.options & 0x400) == 0);
/*  420 */     this.recordStarts = ((this.options & 0x80) != 0);
/*  421 */     this.addToManager = ((this.options & 0x800) != 0);
/*  422 */     this.displaySummary = ((this.options & 0x100) != 0);
/*  423 */     this.inSituShow = ((this.options & 0x4000) != 0);
/*  424 */     this.outputImage = null;
/*  425 */     ip.snapshot();
/*  426 */     ip.setProgressBar(null);
/*  427 */     if (Analyzer.isRedirectImage()) {
/*  428 */       this.redirectImp = Analyzer.getRedirectImage(imp);
/*  429 */       if (this.redirectImp == null) return false;
/*  430 */       int depth = this.redirectImp.getStackSize();
/*  431 */       if ((depth > 1) && (depth == imp.getStackSize())) {
/*  432 */         ImageStack redirectStack = this.redirectImp.getStack();
/*  433 */         this.redirectIP = redirectStack.getProcessor(imp.getCurrentSlice());
/*      */       } else {
/*  435 */         this.redirectIP = this.redirectImp.getProcessor();
/*      */       } } else if (imp.getType() == 4) {
/*  437 */       ImagePlus original = (ImagePlus)imp.getProperty("OriginalImage");
/*  438 */       if ((original != null) && (original.getWidth() == imp.getWidth()) && (original.getHeight() == imp.getHeight())) {
/*  439 */         this.redirectImp = original;
/*  440 */         this.redirectIP = original.getProcessor();
/*      */       }
/*      */     }
/*  443 */     if (!setThresholdLevels(imp, ip))
/*  444 */       return false;
/*  445 */     this.width = ip.getWidth();
/*  446 */     this.height = ip.getHeight();
/*  447 */     if ((this.showChoice != 0) && (this.showChoice != 6) && (this.showChoice != 7)) {
/*  448 */       this.blackBackground = ((Prefs.blackBackground) && (this.inSituShow));
/*  449 */       if (this.slice == 1)
/*  450 */         this.outlines = new ImageStack(this.width, this.height);
/*  451 */       if (this.showChoice == 5)
/*  452 */         this.drawIP = new ShortProcessor(this.width, this.height);
/*      */       else
/*  454 */         this.drawIP = new ByteProcessor(this.width, this.height);
/*  455 */       this.drawIP.setLineWidth(this.lineWidth);
/*  456 */       if (this.showChoice != 5)
/*      */       {
/*  458 */         if ((this.showChoice == 4) && (!this.blackBackground)) {
/*  459 */           this.drawIP.invertLut();
/*  460 */         } else if (this.showChoice == 1) {
/*  461 */           if (!this.inSituShow) {
/*  462 */             if (this.customLut == null)
/*  463 */               makeCustomLut();
/*  464 */             this.drawIP.setColorModel(this.customLut);
/*      */           }
/*  466 */           this.drawIP.setFont(new Font("SansSerif", 0, this.fontSize));
/*  467 */           if ((this.fontSize > 12) && (this.inSituShow))
/*  468 */             this.drawIP.setAntialiasedText(true); 
/*      */         }
/*      */       }
/*  470 */       this.outlines.addSlice(null, this.drawIP);
/*      */ 
/*  472 */       if ((this.showChoice == 5) || (this.blackBackground)) {
/*  473 */         this.drawIP.setColor(Color.black);
/*  474 */         this.drawIP.fill();
/*  475 */         this.drawIP.setColor(Color.white);
/*      */       } else {
/*  477 */         this.drawIP.setColor(Color.white);
/*  478 */         this.drawIP.fill();
/*  479 */         this.drawIP.setColor(Color.black);
/*      */       }
/*      */     }
/*  482 */     this.calibration = (this.redirectImp != null ? this.redirectImp.getCalibration() : imp.getCalibration());
/*      */ 
/*  484 */     if (this.rt == null) {
/*  485 */       this.rt = Analyzer.getResultsTable();
/*  486 */       this.analyzer = new Analyzer(imp);
/*      */     } else {
/*  488 */       this.analyzer = new Analyzer(imp, this.measurements, this.rt);
/*  489 */     }if ((this.resetCounter) && (this.slice == 1) && 
/*  490 */       (!Analyzer.resetCounter())) {
/*  491 */       return false;
/*      */     }
/*  493 */     this.beginningCount = Analyzer.getCounter();
/*      */ 
/*  495 */     byte[] pixels = null;
/*  496 */     if ((ip instanceof ByteProcessor))
/*  497 */       pixels = (byte[])ip.getPixels();
/*  498 */     if (this.r == null) {
/*  499 */       this.r = ip.getRoi();
/*  500 */       this.mask = ip.getMask();
/*  501 */       if (this.displaySummary) {
/*  502 */         if (this.mask != null)
/*  503 */           this.totalArea = ImageStatistics.getStatistics(ip, 1, this.calibration).area;
/*      */         else
/*  505 */           this.totalArea = (this.r.width * this.calibration.pixelWidth * this.r.height * this.calibration.pixelHeight);
/*      */       }
/*      */     }
/*  508 */     this.minX = this.r.x; this.maxX = (this.r.x + this.r.width); this.minY = this.r.y; this.maxY = (this.r.y + this.r.height);
/*  509 */     if (((this.r.width < this.width) || (this.r.height < this.height) || (this.mask != null)) && 
/*  510 */       (!eraseOutsideRoi(ip, this.r, this.mask))) return false;
/*      */ 
/*  514 */     int inc = Math.max(this.r.height / 25, 1);
/*  515 */     int mi = 0;
/*  516 */     ImageWindow win = imp.getWindow();
/*  517 */     if (win != null)
/*  518 */       win.running = true;
/*  519 */     if (this.measurements == 0)
/*  520 */       this.measurements = Analyzer.getMeasurements();
/*  521 */     if (this.showChoice == 3)
/*  522 */       this.measurements |= 2048;
/*  523 */     this.measurements &= -257;
/*  524 */     this.roiNeedsImage = (((this.measurements & 0x80) != 0) || ((this.measurements & 0x2000) != 0) || ((this.measurements & 0x4000) != 0));
/*  525 */     this.particleCount = 0;
/*  526 */     this.wand = new Wand(ip);
/*  527 */     this.pf = new PolygonFiller();
/*  528 */     if (this.floodFill) {
/*  529 */       ImageProcessor ipf = ip.duplicate();
/*  530 */       ipf.setValue(this.fillColor);
/*  531 */       this.ff = new FloodFiller(ipf);
/*      */     }
/*  533 */     this.roiType = (Wand.allPoints() ? 3 : 4);
/*      */ 
/*  535 */     for (int y = this.r.y; y < this.r.y + this.r.height; y++) {
/*  536 */       int offset = y * this.width;
/*  537 */       for (int x = this.r.x; x < this.r.x + this.r.width; x++)
/*      */       {
/*      */         double value;
/*      */         double value;
/*  538 */         if (pixels != null) {
/*  539 */           value = pixels[(offset + x)] & 0xFF;
/*      */         }
/*      */         else
/*      */         {
/*      */           double value;
/*  540 */           if (this.imageType == 1)
/*  541 */             value = ip.getPixel(x, y);
/*      */           else
/*  543 */             value = ip.getPixelValue(x, y); 
/*      */         }
/*  544 */         if ((value >= this.level1) && (value <= this.level2))
/*  545 */           analyzeParticle(x, y, imp, ip);
/*      */       }
/*  547 */       if ((this.showProgress) && (y % inc == 0))
/*  548 */         IJ.showProgress((y - this.r.y) / this.r.height);
/*  549 */       if (win != null)
/*  550 */         this.canceled = (!win.running);
/*  551 */       if (this.canceled) {
/*  552 */         Macro.abort();
/*  553 */         break;
/*      */       }
/*      */     }
/*  556 */     if (this.showProgress)
/*  557 */       IJ.showProgress(1.0D);
/*  558 */     if (this.showResults)
/*  559 */       this.rt.updateResults();
/*  560 */     imp.killRoi();
/*  561 */     ip.resetRoi();
/*  562 */     ip.reset();
/*  563 */     if ((this.displaySummary) && (IJ.getInstance() != null))
/*  564 */       updateSliceSummary();
/*  565 */     if ((this.addToManager) && (this.roiManager != null))
/*  566 */       this.roiManager.setEditMode(imp, true);
/*  567 */     this.maxParticleCount = (this.particleCount > this.maxParticleCount ? this.particleCount : this.maxParticleCount);
/*  568 */     this.totalCount += this.particleCount;
/*  569 */     if (!this.canceled)
/*  570 */       showResults();
/*  571 */     return true;
/*      */   }
/*      */ 
/*      */   void updateSliceSummary() {
/*  575 */     int slices = this.imp.getStackSize();
/*  576 */     float[] areas = this.rt.getColumn(0);
/*  577 */     if (areas == null)
/*  578 */       areas = new float[0];
/*  579 */     String label = this.imp.getTitle();
/*  580 */     if (slices > 1) {
/*  581 */       label = this.imp.getStack().getShortSliceLabel(this.slice);
/*  582 */       label = "" + this.slice;
/*      */     }
/*  584 */     String aLine = null;
/*  585 */     double sum = 0.0D;
/*  586 */     int start = areas.length - this.particleCount;
/*  587 */     if (start < 0)
/*  588 */       return;
/*  589 */     for (int i = start; i < areas.length; i++)
/*  590 */       sum += areas[i];
/*  591 */     int places = Analyzer.getPrecision();
/*  592 */     Calibration cal = this.imp.getCalibration();
/*  593 */     String total = "\t" + ResultsTable.d2s(sum, places);
/*  594 */     String average = "\t" + ResultsTable.d2s(sum / this.particleCount, places);
/*  595 */     String fraction = "\t" + ResultsTable.d2s(sum * 100.0D / this.totalArea, 1);
/*  596 */     aLine = label + "\t" + this.particleCount + total + average + fraction;
/*  597 */     aLine = addMeans(aLine, areas.length > 0 ? start : -1);
/*  598 */     if (slices == 1) {
/*  599 */       Frame frame = WindowManager.getFrame("Summary");
/*  600 */       if ((frame != null) && ((frame instanceof TextWindow)) && (this.summaryHdr.equals(prevHdr)))
/*  601 */         this.tw = ((TextWindow)frame);
/*      */     }
/*  603 */     if (this.tw == null) {
/*  604 */       String title = "Summary of " + this.imp.getTitle();
/*  605 */       this.tw = new TextWindow(title, this.summaryHdr, aLine, 450, 300);
/*  606 */       prevHdr = this.summaryHdr;
/*      */     } else {
/*  608 */       this.tw.append(aLine);
/*      */     }
/*      */   }
/*      */ 
/*  612 */   String addMeans(String line, int start) { if ((this.measurements & 0x2) != 0) line = addMean(1, line, start);
/*  613 */     if ((this.measurements & 0x8) != 0) line = addMean(3, line, start);
/*  614 */     if ((this.measurements & 0x80) != 0)
/*  615 */       line = addMean(10, line, start);
/*  616 */     if ((this.measurements & 0x800) != 0) {
/*  617 */       line = addMean(15, line, start);
/*  618 */       line = addMean(16, line, start);
/*  619 */       line = addMean(17, line, start);
/*      */     }
/*  621 */     if ((this.measurements & 0x2000) != 0) {
/*  622 */       line = addMean(18, line, start);
/*  623 */       line = addMean(35, line, start);
/*      */     }
/*  625 */     if ((this.measurements & 0x4000) != 0) {
/*  626 */       line = addMean(19, line, start);
/*  627 */       line = addMean(29, line, start);
/*  628 */       line = addMean(30, line, start);
/*  629 */       line = addMean(31, line, start);
/*  630 */       line = addMean(32, line, start);
/*      */     }
/*  632 */     if ((this.measurements & 0x8000) != 0)
/*  633 */       line = addMean(20, line, start);
/*  634 */     if ((this.measurements & 0x10000) != 0)
/*  635 */       line = addMean(21, line, start);
/*  636 */     if ((this.measurements & 0x20000) != 0)
/*  637 */       line = addMean(22, line, start);
/*  638 */     if ((this.measurements & 0x40000) != 0)
/*  639 */       line = addMean(23, line, start);
/*  640 */     return line; }
/*      */ 
/*      */   private String addMean(int column, String line, int start)
/*      */   {
/*  644 */     if (start == -1) {
/*  645 */       line = line + "\tNaN";
/*  646 */       this.summaryHdr = (this.summaryHdr + "\t" + ResultsTable.getDefaultHeading(column));
/*      */     } else {
/*  648 */       float[] c = column >= 0 ? this.rt.getColumn(column) : null;
/*  649 */       if (c != null) {
/*  650 */         ImageProcessor ip = new FloatProcessor(c.length, 1, c, null);
/*  651 */         if (ip == null) return line;
/*  652 */         ip.setRoi(start, 0, ip.getWidth() - start, 1);
/*  653 */         ip = ip.crop();
/*  654 */         ImageStatistics stats = new FloatStatistics(ip);
/*  655 */         if (stats == null)
/*  656 */           return line;
/*  657 */         line = line + n(stats.mean);
/*      */       } else {
/*  659 */         line = line + "\tNaN";
/*  660 */       }this.summaryHdr = (this.summaryHdr + "\t" + this.rt.getColumnHeading(column));
/*      */     }
/*  662 */     return line;
/*      */   }
/*      */ 
/*      */   String n(double n)
/*      */   {
/*      */     String s;
/*      */     String s;
/*  667 */     if (Math.round(n) == n)
/*  668 */       s = ResultsTable.d2s(n, 0);
/*      */     else
/*  670 */       s = ResultsTable.d2s(n, Analyzer.getPrecision());
/*  671 */     return "\t" + s;
/*      */   }
/*      */ 
/*      */   boolean eraseOutsideRoi(ImageProcessor ip, Rectangle r, ImageProcessor mask) {
/*  675 */     int width = ip.getWidth();
/*  676 */     int height = ip.getHeight();
/*  677 */     ip.setRoi(r);
/*  678 */     if ((this.excludeEdgeParticles) && (this.polygon != null)) {
/*  679 */       ImageStatistics stats = ImageStatistics.getStatistics(ip, 16, null);
/*  680 */       if ((this.fillColor >= stats.min) && (this.fillColor <= stats.max)) {
/*  681 */         double replaceColor = this.level1 - 1.0D;
/*  682 */         if ((replaceColor < 0.0D) || (replaceColor == this.fillColor)) {
/*  683 */           replaceColor = this.level2 + 1.0D;
/*  684 */           int maxColor = this.imageType == 0 ? 255 : 65535;
/*  685 */           if ((replaceColor > maxColor) || (replaceColor == this.fillColor)) {
/*  686 */             IJ.error("Particle Analyzer", "Unable to remove edge particles");
/*  687 */             return false;
/*      */           }
/*      */         }
/*  690 */         for (int y = this.minY; y < this.maxY; y++) {
/*  691 */           for (int x = this.minX; x < this.maxX; x++) {
/*  692 */             int v = ip.getPixel(x, y);
/*  693 */             if (v == this.fillColor) ip.putPixel(x, y, (int)replaceColor);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  698 */     ip.setValue(this.fillColor);
/*  699 */     if (mask != null) {
/*  700 */       mask = mask.duplicate();
/*  701 */       mask.invert();
/*  702 */       ip.fill(mask);
/*      */     }
/*  704 */     ip.setRoi(0, 0, r.x, height);
/*  705 */     ip.fill();
/*  706 */     ip.setRoi(r.x, 0, r.width, r.y);
/*  707 */     ip.fill();
/*  708 */     ip.setRoi(r.x, r.y + r.height, r.width, height - (r.y + r.height));
/*  709 */     ip.fill();
/*  710 */     ip.setRoi(r.x + r.width, 0, width - (r.x + r.width), height);
/*  711 */     ip.fill();
/*  712 */     ip.resetRoi();
/*      */ 
/*  715 */     return true;
/*      */   }
/*      */ 
/*      */   boolean setThresholdLevels(ImagePlus imp, ImageProcessor ip) {
/*  719 */     double t1 = ip.getMinThreshold();
/*  720 */     double t2 = ip.getMaxThreshold();
/*  721 */     boolean invertedLut = imp.isInvertedLut();
/*  722 */     boolean byteImage = ip instanceof ByteProcessor;
/*  723 */     if ((ip instanceof ShortProcessor))
/*  724 */       this.imageType = 1;
/*  725 */     else if ((ip instanceof FloatProcessor))
/*  726 */       this.imageType = 2;
/*      */     else
/*  728 */       this.imageType = 0;
/*  729 */     if (t1 == -808080.0D) {
/*  730 */       ImageStatistics stats = imp.getStatistics();
/*  731 */       if ((this.imageType != 0) || (stats.histogram[0] + stats.histogram['ÿ'] != stats.pixelCount)) {
/*  732 */         IJ.error("Particle Analyzer", "A thresholded image or 8-bit binary image is\nrequired. Threshold levels can be set using\nthe Image->Adjust->Threshold tool.");
/*      */ 
/*  736 */         this.canceled = true;
/*  737 */         return false;
/*      */       }
/*  739 */       boolean threshold255 = invertedLut;
/*  740 */       if (Prefs.blackBackground)
/*  741 */         threshold255 = !threshold255;
/*  742 */       if (threshold255) {
/*  743 */         this.level1 = 255.0D;
/*  744 */         this.level2 = 255.0D;
/*  745 */         this.fillColor = 64.0D;
/*      */       } else {
/*  747 */         this.level1 = 0.0D;
/*  748 */         this.level2 = 0.0D;
/*  749 */         this.fillColor = 192.0D;
/*      */       }
/*      */     } else {
/*  752 */       this.level1 = t1;
/*  753 */       this.level2 = t2;
/*  754 */       if (this.imageType == 0) {
/*  755 */         if (this.level1 > 0.0D)
/*  756 */           this.fillColor = 0.0D;
/*  757 */         else if (this.level2 < 255.0D)
/*  758 */           this.fillColor = 255.0D;
/*  759 */       } else if (this.imageType == 1) {
/*  760 */         if (this.level1 > 0.0D)
/*  761 */           this.fillColor = 0.0D;
/*  762 */         else if (this.level2 < 65535.0D)
/*  763 */           this.fillColor = 65535.0D;
/*  764 */       } else if (this.imageType == 2)
/*  765 */         this.fillColor = -3.402823466385289E+38D;
/*      */       else
/*  767 */         return false;
/*      */     }
/*  769 */     this.imageType2 = this.imageType;
/*  770 */     if (this.redirectIP != null) {
/*  771 */       if ((this.redirectIP instanceof ShortProcessor))
/*  772 */         this.imageType2 = 1;
/*  773 */       else if ((this.redirectIP instanceof FloatProcessor))
/*  774 */         this.imageType2 = 2;
/*  775 */       else if ((this.redirectIP instanceof ColorProcessor))
/*  776 */         this.imageType2 = 3;
/*      */       else
/*  778 */         this.imageType2 = 0;
/*      */     }
/*  780 */     return true;
/*      */   }
/*      */ 
/*      */   void analyzeParticle(int x, int y, ImagePlus imp, ImageProcessor ip)
/*      */   {
/*  787 */     ImageProcessor ip2 = this.redirectIP != null ? this.redirectIP : ip;
/*  788 */     this.wand.autoOutline(x, y, this.level1, this.level2, this.wandMode);
/*  789 */     if (this.wand.npoints == 0) {
/*  790 */       IJ.log("wand error: " + x + " " + y); return;
/*  791 */     }Roi roi = new PolygonRoi(this.wand.xpoints, this.wand.ypoints, this.wand.npoints, this.roiType);
/*  792 */     Rectangle r = roi.getBounds();
/*  793 */     if ((r.width > 1) && (r.height > 1)) {
/*  794 */       PolygonRoi proi = (PolygonRoi)roi;
/*  795 */       this.pf.setPolygon(proi.getXCoordinates(), proi.getYCoordinates(), proi.getNCoordinates());
/*  796 */       ip2.setMask(this.pf.getMask(r.width, r.height));
/*  797 */       if (this.floodFill) this.ff.particleAnalyzerFill(x, y, this.level1, this.level2, ip2.getMask(), r);
/*      */     }
/*  799 */     ip2.setRoi(r);
/*  800 */     ip.setValue(this.fillColor);
/*  801 */     ImageStatistics stats = getStatistics(ip2, this.measurements, this.calibration);
/*  802 */     boolean include = true;
/*  803 */     if (this.excludeEdgeParticles) {
/*  804 */       if ((r.x == this.minX) || (r.y == this.minY) || (r.x + r.width == this.maxX) || (r.y + r.height == this.maxY))
/*  805 */         include = false;
/*  806 */       if (this.polygon != null) {
/*  807 */         Rectangle bounds = roi.getBounds();
/*  808 */         int x1 = bounds.x + this.wand.xpoints[(this.wand.npoints - 1)];
/*  809 */         int y1 = bounds.y + this.wand.ypoints[(this.wand.npoints - 1)];
/*      */ 
/*  811 */         for (int i = 0; i < this.wand.npoints; i++) {
/*  812 */           int x2 = bounds.x + this.wand.xpoints[i];
/*  813 */           int y2 = bounds.y + this.wand.ypoints[i];
/*  814 */           if (!this.polygon.contains(x2, y2)) {
/*  815 */             include = false; break;
/*  816 */           }if (((x1 == x2) && (ip.getPixel(x1, y1 - 1) == this.fillColor)) || ((y1 == y2) && (ip.getPixel(x1 - 1, y1) == this.fillColor))) {
/*  817 */             include = false; break;
/*  818 */           }x1 = x2; y1 = y2;
/*      */         }
/*      */       }
/*      */     }
/*  822 */     ImageProcessor mask = ip2.getMask();
/*  823 */     if ((this.minCircularity > 0.0D) || (this.maxCircularity < 1.0D)) {
/*  824 */       double perimeter = roi.getLength();
/*  825 */       double circularity = perimeter == 0.0D ? 0.0D : 12.566370614359172D * (stats.pixelCount / (perimeter * perimeter));
/*  826 */       if (circularity > 1.0D) circularity = 1.0D;
/*      */ 
/*  828 */       if ((circularity < this.minCircularity) || (circularity > this.maxCircularity)) include = false;
/*      */     }
/*  830 */     if ((stats.pixelCount >= this.minSize) && (stats.pixelCount <= this.maxSize) && (include)) {
/*  831 */       this.particleCount += 1;
/*  832 */       if (this.roiNeedsImage)
/*  833 */         roi.setImage(imp);
/*  834 */       stats.xstart = x; stats.ystart = y;
/*  835 */       saveResults(stats, roi);
/*  836 */       if (this.showChoice != 0)
/*  837 */         drawParticle(this.drawIP, roi, stats, mask);
/*      */     }
/*  839 */     if (this.redirectIP != null)
/*  840 */       ip.setRoi(r);
/*  841 */     ip.fill(mask);
/*      */   }
/*      */ 
/*      */   ImageStatistics getStatistics(ImageProcessor ip, int mOptions, Calibration cal) {
/*  845 */     switch (this.imageType2) {
/*      */     case 0:
/*  847 */       return new ByteStatistics(ip, mOptions, cal);
/*      */     case 1:
/*  849 */       return new ShortStatistics(ip, mOptions, cal);
/*      */     case 2:
/*  851 */       return new FloatStatistics(ip, mOptions, cal);
/*      */     case 3:
/*  853 */       return new ColorStatistics(ip, mOptions, cal);
/*      */     }
/*  855 */     return null;
/*      */   }
/*      */ 
/*      */   protected void saveResults(ImageStatistics stats, Roi roi)
/*      */   {
/*  862 */     this.analyzer.saveResults(stats, roi);
/*  863 */     if (this.recordStarts) {
/*  864 */       this.rt.addValue("XStart", stats.xstart);
/*  865 */       this.rt.addValue("YStart", stats.ystart);
/*      */     }
/*  867 */     if (this.addToManager) {
/*  868 */       if (this.roiManager == null) {
/*  869 */         if ((Macro.getOptions() != null) && (Interpreter.isBatchMode()))
/*  870 */           this.roiManager = Interpreter.getBatchModeRoiManager();
/*  871 */         if (this.roiManager == null) {
/*  872 */           Frame frame = WindowManager.getFrame("ROI Manager");
/*  873 */           if (frame == null)
/*  874 */             IJ.run("ROI Manager...");
/*  875 */           frame = WindowManager.getFrame("ROI Manager");
/*  876 */           if ((frame == null) || (!(frame instanceof RoiManager))) {
/*  877 */             this.addToManager = false; return;
/*  878 */           }this.roiManager = ((RoiManager)frame);
/*      */         }
/*  880 */         if (this.resetCounter)
/*  881 */           this.roiManager.runCommand("reset");
/*      */       }
/*  883 */       if (this.imp.getStackSize() > 1)
/*  884 */         roi.setPosition(this.imp.getCurrentSlice());
/*  885 */       if (this.lineWidth != 1)
/*  886 */         roi.setStrokeWidth(this.lineWidth);
/*  887 */       this.roiManager.add(this.imp, roi, this.rt.getCounter());
/*      */     }
/*  889 */     if (this.showResults)
/*  890 */       this.rt.addResults();
/*      */   }
/*      */ 
/*      */   protected void drawParticle(ImageProcessor drawIP, Roi roi, ImageStatistics stats, ImageProcessor mask)
/*      */   {
/*  897 */     switch (this.showChoice) { case 4:
/*  898 */       drawFilledParticle(drawIP, roi, mask); break;
/*      */     case 1:
/*      */     case 2:
/*      */     case 6:
/*      */     case 7:
/*  900 */       drawOutline(drawIP, roi, this.rt.getCounter()); break;
/*      */     case 3:
/*  901 */       drawEllipse(drawIP, stats, this.rt.getCounter()); break;
/*      */     case 5:
/*  902 */       drawRoiFilledParticle(drawIP, roi, mask, this.rt.getCounter()); break;
/*      */     }
/*      */   }
/*      */ 
/*      */   void drawFilledParticle(ImageProcessor ip, Roi roi, ImageProcessor mask)
/*      */   {
/*  909 */     ip.setRoi(roi.getBounds());
/*  910 */     ip.fill(mask);
/*      */   }
/*      */ 
/*      */   void drawOutline(ImageProcessor ip, Roi roi, int count) {
/*  914 */     if ((this.showChoice == 6) || (this.showChoice == 7)) {
/*  915 */       if (this.overlay == null) {
/*  916 */         this.overlay = new Overlay();
/*  917 */         this.overlay.drawLabels(true);
/*  918 */         this.overlay.setLabelFont(new Font("SansSerif", 0, this.fontSize));
/*      */       }
/*  920 */       Roi roi2 = (Roi)roi.clone();
/*  921 */       roi2.setStrokeColor(Color.cyan);
/*  922 */       if (this.lineWidth != 1)
/*  923 */         roi2.setStrokeWidth(this.lineWidth);
/*  924 */       if (this.showChoice == 7)
/*  925 */         roi2.setFillColor(Color.cyan);
/*  926 */       this.overlay.add(roi2);
/*      */     } else {
/*  928 */       Rectangle r = roi.getBounds();
/*  929 */       int nPoints = ((PolygonRoi)roi).getNCoordinates();
/*  930 */       int[] xp = ((PolygonRoi)roi).getXCoordinates();
/*  931 */       int[] yp = ((PolygonRoi)roi).getYCoordinates();
/*  932 */       int x = r.x; int y = r.y;
/*  933 */       if (!this.inSituShow)
/*  934 */         ip.setValue(0.0D);
/*  935 */       ip.moveTo(x + xp[0], y + yp[0]);
/*  936 */       for (int i = 1; i < nPoints; i++)
/*  937 */         ip.lineTo(x + xp[i], y + yp[i]);
/*  938 */       ip.lineTo(x + xp[0], y + yp[0]);
/*  939 */       if (this.showChoice != 2) {
/*  940 */         String s = ResultsTable.d2s(count, 0);
/*  941 */         ip.moveTo(r.x + r.width / 2 - ip.getStringWidth(s) / 2, r.y + r.height / 2 + this.fontSize / 2);
/*  942 */         if (!this.inSituShow)
/*  943 */           ip.setValue(1.0D);
/*  944 */         ip.drawString(s);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void drawEllipse(ImageProcessor ip, ImageStatistics stats, int count) {
/*  950 */     stats.drawEllipse(ip);
/*      */   }
/*      */ 
/*      */   void drawRoiFilledParticle(ImageProcessor ip, Roi roi, ImageProcessor mask, int count) {
/*  954 */     int grayLevel = count < 65535 ? count : 65535;
/*  955 */     ip.setValue(grayLevel);
/*  956 */     ip.setRoi(roi.getBounds());
/*  957 */     ip.fill(mask);
/*      */   }
/*      */ 
/*      */   void showResults() {
/*  961 */     int count = this.rt.getCounter();
/*      */ 
/*  963 */     boolean lastSlice = (!this.processStack) || (this.slice == this.imp.getStackSize());
/*  964 */     if (((this.showChoice == 6) || (this.showChoice == 7)) && (this.slice == 1) && (count > 0)) {
/*  965 */       this.imp.setOverlay(this.overlay);
/*  966 */     } else if ((this.outlines != null) && (lastSlice)) {
/*  967 */       String title = this.imp != null ? this.imp.getTitle() : "Outlines";
/*      */       String prefix;
/*      */       String prefix;
/*  969 */       if (this.showChoice == 4) {
/*  970 */         prefix = "Mask of ";
/*      */       }
/*      */       else
/*      */       {
/*      */         String prefix;
/*  971 */         if (this.showChoice == 5)
/*  972 */           prefix = "Count Masks of ";
/*      */         else
/*  974 */           prefix = "Drawing of "; 
/*      */       }
/*  975 */       this.outlines.update(this.drawIP);
/*  976 */       this.outputImage = new ImagePlus(prefix + title, this.outlines);
/*  977 */       if (this.inSituShow) {
/*  978 */         if (this.imp.getStackSize() == 1)
/*  979 */           Undo.setup(6, this.imp);
/*  980 */         this.imp.setStack(null, this.outputImage.getStack());
/*  981 */       } else if (!this.hideOutputImage) {
/*  982 */         this.outputImage.show();
/*      */       }
/*      */     }
/*  984 */     if ((this.showResults) && (!this.processStack)) {
/*  985 */       TextPanel tp = IJ.getTextPanel();
/*  986 */       if ((this.beginningCount > 0) && (tp != null) && (tp.getLineCount() != count))
/*  987 */         this.rt.show("Results");
/*  988 */       Analyzer.firstParticle = this.beginningCount;
/*  989 */       Analyzer.lastParticle = Analyzer.getCounter() - 1;
/*      */     } else {
/*  991 */       Analyzer.firstParticle = Analyzer.lastParticle = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public ImagePlus getOutputImage()
/*      */   {
/*  997 */     return this.outputImage;
/*      */   }
/*      */ 
/*      */   public void setHideOutputImage(boolean hideOutputImage)
/*      */   {
/* 1002 */     this.hideOutputImage = hideOutputImage;
/*      */   }
/*      */ 
/*      */   public static void setFontSize(int size)
/*      */   {
/* 1007 */     nextFontSize = size;
/*      */   }
/*      */ 
/*      */   public static void setLineWidth(int width)
/*      */   {
/* 1012 */     nextLineWidth = width;
/*      */   }
/*      */ 
/*      */   int getColumnID(String name) {
/* 1016 */     int id = this.rt.getFreeColumn(name);
/* 1017 */     if (id == -2)
/* 1018 */       id = this.rt.getColumnIndex(name);
/* 1019 */     return id;
/*      */   }
/*      */ 
/*      */   void makeCustomLut() {
/* 1023 */     IndexColorModel cm = (IndexColorModel)LookUpTable.createGrayscaleColorModel(false);
/* 1024 */     byte[] reds = new byte[256];
/* 1025 */     byte[] greens = new byte[256];
/* 1026 */     byte[] blues = new byte[256];
/* 1027 */     cm.getReds(reds);
/* 1028 */     cm.getGreens(greens);
/* 1029 */     cm.getBlues(blues);
/* 1030 */     reds[1] = -1;
/* 1031 */     greens[1] = 0;
/* 1032 */     blues[1] = 0;
/* 1033 */     this.customLut = new IndexColorModel(8, 256, reds, greens, blues);
/*      */   }
/*      */ 
/*      */   public static void savePreferences(Properties prefs)
/*      */   {
/* 1038 */     prefs.put("ap.options", Integer.toString(staticOptions));
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.ParticleAnalyzer
 * JD-Core Version:    0.6.2
 */