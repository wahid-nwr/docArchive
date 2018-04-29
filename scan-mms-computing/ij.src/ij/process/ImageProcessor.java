/*      */ package ij.process;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.Prefs;
/*      */ import ij.gui.OvalRoi;
/*      */ import ij.gui.Overlay;
/*      */ import ij.gui.ProgressBar;
/*      */ import ij.gui.Roi;
/*      */ import ij.gui.ShapeRoi;
/*      */ import ij.plugin.filter.GaussianBlur;
/*      */ import ij.util.Java2;
/*      */ import ij.util.Tools;
/*      */ import java.awt.Color;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Image;
/*      */ import java.awt.Polygon;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.MemoryImageSource;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.awt.image.WritableRaster;
/*      */ 
/*      */ public abstract class ImageProcessor
/*      */   implements Cloneable
/*      */ {
/*      */   public static final int BLACK = -16777216;
/*      */   public static final double NO_THRESHOLD = -808080.0D;
/*      */   public static final int LEFT_JUSTIFY = 0;
/*      */   public static final int CENTER_JUSTIFY = 1;
/*      */   public static final int RIGHT_JUSTIFY = 2;
/*      */   public static final int ISODATA = 0;
/*      */   public static final int ISODATA2 = 1;
/*      */   public static final int NEAREST_NEIGHBOR = 0;
/*      */   public static final int NONE = 0;
/*      */   public static final int BILINEAR = 1;
/*      */   public static final int BICUBIC = 2;
/*      */   public static final int BLUR_MORE = 0;
/*      */   public static final int FIND_EDGES = 1;
/*      */   public static final int MEDIAN_FILTER = 2;
/*      */   public static final int MIN = 3;
/*      */   public static final int MAX = 4;
/*      */   public static final int CONVOLVE = 5;
/*      */   public static final int RED_LUT = 0;
/*      */   public static final int BLACK_AND_WHITE_LUT = 1;
/*      */   public static final int NO_LUT_UPDATE = 2;
/*      */   public static final int OVER_UNDER_LUT = 3;
/*      */   static final int INVERT = 0;
/*      */   static final int FILL = 1;
/*      */   static final int ADD = 2;
/*      */   static final int MULT = 3;
/*      */   static final int AND = 4;
/*      */   static final int OR = 5;
/*      */   static final int XOR = 6;
/*      */   static final int GAMMA = 7;
/*      */   static final int LOG = 8;
/*      */   static final int MINIMUM = 9;
/*      */   static final int MAXIMUM = 10;
/*      */   static final int SQR = 11;
/*      */   static final int SQRT = 12;
/*      */   static final int EXP = 13;
/*      */   static final int ABS = 14;
/*      */   static final String WRONG_LENGTH = "width*height!=pixels.length";
/*   56 */   int fgColor = 0;
/*   57 */   protected int lineWidth = 1;
/*      */   protected int cx;
/*      */   protected int cy;
/*      */   protected Font font;
/*      */   protected FontMetrics fontMetrics;
/*      */   protected boolean antialiasedText;
/*      */   protected boolean boldFont;
/*      */   private static String[] interpolationMethods;
/*      */   private static int overRed;
/*   65 */   private static int overGreen = 255;
/*      */   private static int overBlue;
/*      */   private static int underRed;
/*      */   private static int underGreen;
/*   66 */   private static int underBlue = 255;
/*      */   private static boolean useBicubic;
/*      */   private int sliceNumber;
/*      */   ProgressBar progressBar;
/*      */   protected int width;
/*      */   protected int snapshotWidth;
/*      */   protected int height;
/*      */   protected int snapshotHeight;
/*      */   protected int roiX;
/*      */   protected int roiY;
/*      */   protected int roiWidth;
/*      */   protected int roiHeight;
/*      */   protected int xMin;
/*      */   protected int xMax;
/*      */   protected int yMin;
/*      */   protected int yMax;
/*      */   boolean snapshotCopyMode;
/*      */   ImageProcessor mask;
/*      */   protected ColorModel baseCM;
/*      */   protected ColorModel cm;
/*      */   protected byte[] rLUT1;
/*      */   protected byte[] gLUT1;
/*      */   protected byte[] bLUT1;
/*      */   protected byte[] rLUT2;
/*      */   protected byte[] gLUT2;
/*      */   protected byte[] bLUT2;
/*      */   protected boolean interpolate;
/*   82 */   protected int interpolationMethod = 0;
/*   83 */   protected double minThreshold = -808080.0D; protected double maxThreshold = -808080.0D;
/*   84 */   protected int histogramSize = 256;
/*      */   protected double histogramMin;
/*      */   protected double histogramMax;
/*      */   protected float[] cTable;
/*      */   protected boolean lutAnimation;
/*      */   protected MemoryImageSource source;
/*      */   protected Image img;
/*      */   protected boolean newPixels;
/*   91 */   protected Color drawingColor = Color.black;
/*      */   protected int clipXMin;
/*      */   protected int clipXMax;
/*      */   protected int clipYMin;
/*      */   protected int clipYMax;
/*   93 */   protected int justification = 0;
/*      */   protected int lutUpdateMode;
/*      */   protected WritableRaster raster;
/*      */   protected BufferedImage image;
/*      */   protected BufferedImage fmImage;
/*      */   protected ColorModel cm2;
/*      */   protected SampleModel sampleModel;
/*      */   protected static IndexColorModel defaultColorModel;
/*      */   protected boolean minMaxSet;
/*  226 */   protected boolean inversionTested = false;
/*      */   protected boolean invertedLut;
/*      */   private ImageProcessor dotMask;
/*      */   static final double a = 0.5D;
/*      */   private byte[] bytes;
/*      */   private int[] reds;
/*      */   private int[] greens;
/*      */   private int[] blues;
/*      */ 
/*      */   protected void showProgress(double percentDone)
/*      */   {
/*  104 */     if (this.progressBar != null)
/*  105 */       this.progressBar.show(percentDone);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   protected void hideProgress() {
/*  110 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public int getWidth()
/*      */   {
/*  115 */     return this.width;
/*      */   }
/*      */ 
/*      */   public int getHeight()
/*      */   {
/*  120 */     return this.height;
/*      */   }
/*      */ 
/*      */   public ColorModel getColorModel()
/*      */   {
/*  127 */     if (this.cm == null)
/*  128 */       makeDefaultColorModel();
/*  129 */     if (this.baseCM != null) {
/*  130 */       return this.baseCM;
/*      */     }
/*  132 */     return this.cm;
/*      */   }
/*      */ 
/*      */   public ColorModel getCurrentColorModel()
/*      */   {
/*  138 */     if (this.cm == null) makeDefaultColorModel();
/*  139 */     return this.cm;
/*      */   }
/*      */ 
/*      */   public void setColorModel(ColorModel cm)
/*      */   {
/*  145 */     if ((!(this instanceof ColorProcessor)) && (!(cm instanceof IndexColorModel)))
/*  146 */       throw new IllegalArgumentException("Must be IndexColorModel");
/*  147 */     this.cm = cm;
/*  148 */     this.baseCM = null;
/*  149 */     this.rLUT1 = (this.rLUT2 = null);
/*  150 */     this.newPixels = true;
/*  151 */     this.inversionTested = false;
/*  152 */     this.minThreshold = -808080.0D;
/*  153 */     this.source = null;
/*      */   }
/*      */ 
/*      */   protected void makeDefaultColorModel() {
/*  157 */     this.cm = getDefaultColorModel();
/*      */   }
/*      */ 
/*      */   public void invertLut()
/*      */   {
/*  163 */     if (this.cm == null)
/*  164 */       makeDefaultColorModel();
/*  165 */     IndexColorModel icm = (IndexColorModel)this.cm;
/*  166 */     int mapSize = icm.getMapSize();
/*  167 */     byte[] reds = new byte[mapSize];
/*  168 */     byte[] greens = new byte[mapSize];
/*  169 */     byte[] blues = new byte[mapSize];
/*  170 */     byte[] reds2 = new byte[mapSize];
/*  171 */     byte[] greens2 = new byte[mapSize];
/*  172 */     byte[] blues2 = new byte[mapSize];
/*  173 */     icm.getReds(reds);
/*  174 */     icm.getGreens(greens);
/*  175 */     icm.getBlues(blues);
/*  176 */     for (int i = 0; i < mapSize; i++) {
/*  177 */       reds2[i] = ((byte)(reds[(mapSize - i - 1)] & 0xFF));
/*  178 */       greens2[i] = ((byte)(greens[(mapSize - i - 1)] & 0xFF));
/*  179 */       blues2[i] = ((byte)(blues[(mapSize - i - 1)] & 0xFF));
/*      */     }
/*  181 */     ColorModel cm = new IndexColorModel(8, mapSize, reds2, greens2, blues2);
/*  182 */     setColorModel(cm);
/*      */   }
/*      */ 
/*      */   public int getBestIndex(Color c)
/*      */   {
/*  188 */     if (this.cm == null)
/*  189 */       makeDefaultColorModel();
/*      */     IndexColorModel icm;
/*  190 */     if (this.minThreshold != -808080.0D) {
/*  191 */       double saveMin = getMinThreshold();
/*  192 */       double saveMax = getMaxThreshold();
/*  193 */       resetThreshold();
/*  194 */       IndexColorModel icm = (IndexColorModel)this.cm;
/*  195 */       setThreshold(saveMin, saveMax, this.lutUpdateMode);
/*      */     } else {
/*  197 */       icm = (IndexColorModel)this.cm;
/*  198 */     }int mapSize = icm.getMapSize();
/*  199 */     byte[] rLUT = new byte[mapSize];
/*  200 */     byte[] gLUT = new byte[mapSize];
/*  201 */     byte[] bLUT = new byte[mapSize];
/*  202 */     icm.getReds(rLUT);
/*  203 */     icm.getGreens(gLUT);
/*  204 */     icm.getBlues(bLUT);
/*  205 */     int minDistance = 2147483647;
/*      */ 
/*  207 */     int minIndex = 0;
/*  208 */     int r1 = c.getRed();
/*  209 */     int g1 = c.getGreen();
/*  210 */     int b1 = c.getBlue();
/*      */ 
/*  212 */     for (int i = 0; i < mapSize; i++) {
/*  213 */       int r2 = rLUT[i] & 0xFF; int g2 = gLUT[i] & 0xFF; int b2 = bLUT[i] & 0xFF;
/*  214 */       int distance = (r2 - r1) * (r2 - r1) + (g2 - g1) * (g2 - g1) + (b2 - b1) * (b2 - b1);
/*      */ 
/*  216 */       if (distance < minDistance) {
/*  217 */         minDistance = distance;
/*  218 */         minIndex = i;
/*      */       }
/*  220 */       if (minDistance == 0.0D)
/*      */         break;
/*      */     }
/*  223 */     return minIndex;
/*      */   }
/*      */ 
/*      */   public boolean isInvertedLut()
/*      */   {
/*  232 */     if (this.inversionTested)
/*  233 */       return this.invertedLut;
/*  234 */     this.inversionTested = true;
/*  235 */     if ((this.cm == null) || (!(this.cm instanceof IndexColorModel)))
/*  236 */       return this.invertedLut = 0;
/*  237 */     IndexColorModel icm = (IndexColorModel)this.cm;
/*  238 */     this.invertedLut = true;
/*      */ 
/*  240 */     for (int i = 1; i < 255; i++) {
/*  241 */       int v1 = icm.getRed(i - 1) + icm.getGreen(i - 1) + icm.getBlue(i - 1);
/*  242 */       int v2 = icm.getRed(i) + icm.getGreen(i) + icm.getBlue(i);
/*  243 */       if (v1 < v2) {
/*  244 */         this.invertedLut = false;
/*  245 */         break;
/*      */       }
/*      */     }
/*  248 */     return this.invertedLut;
/*      */   }
/*      */ 
/*      */   public boolean isColorLut()
/*      */   {
/*  253 */     if ((this.cm == null) || (!(this.cm instanceof IndexColorModel)))
/*  254 */       return false;
/*  255 */     IndexColorModel icm = (IndexColorModel)this.cm;
/*  256 */     int mapSize = icm.getMapSize();
/*  257 */     byte[] reds = new byte[mapSize];
/*  258 */     byte[] greens = new byte[mapSize];
/*  259 */     byte[] blues = new byte[mapSize];
/*  260 */     icm.getReds(reds);
/*  261 */     icm.getGreens(greens);
/*  262 */     icm.getBlues(blues);
/*  263 */     boolean isColor = false;
/*  264 */     for (int i = 0; i < mapSize; i++) {
/*  265 */       if ((reds[i] != greens[i]) || (greens[i] != blues[i])) {
/*  266 */         isColor = true;
/*  267 */         break;
/*      */       }
/*      */     }
/*  270 */     return isColor;
/*      */   }
/*      */ 
/*      */   public boolean isPseudoColorLut()
/*      */   {
/*  276 */     if ((this.cm == null) || (!(this.cm instanceof IndexColorModel)))
/*  277 */       return false;
/*  278 */     if (getMinThreshold() != -808080.0D)
/*  279 */       return true;
/*  280 */     IndexColorModel icm = (IndexColorModel)this.cm;
/*  281 */     int mapSize = icm.getMapSize();
/*  282 */     if (mapSize != 256)
/*  283 */       return false;
/*  284 */     byte[] reds = new byte[mapSize];
/*  285 */     byte[] greens = new byte[mapSize];
/*  286 */     byte[] blues = new byte[mapSize];
/*  287 */     icm.getReds(reds);
/*  288 */     icm.getGreens(greens);
/*  289 */     icm.getBlues(blues);
/*      */ 
/*  291 */     int r2 = reds[0] & 0xFF; int g2 = greens[0] & 0xFF; int b2 = blues[0] & 0xFF;
/*  292 */     double sum = 0.0D; double sum2 = 0.0D;
/*  293 */     for (int i = 0; i < mapSize; i++) {
/*  294 */       int r = reds[i] & 0xFF; int g = greens[i] & 0xFF; int b = blues[i] & 0xFF;
/*  295 */       int d = r - r2; sum += d; sum2 += d * d;
/*  296 */       d = g - g2; sum += d; sum2 += d * d;
/*  297 */       d = b - b2; sum += d; sum2 += d * d;
/*  298 */       r2 = r; g2 = g; b2 = b;
/*      */     }
/*  300 */     double stdDev = (768.0D * sum2 - sum * sum) / 768.0D;
/*  301 */     if (stdDev > 0.0D)
/*  302 */       stdDev = Math.sqrt(stdDev / 767.0D);
/*      */     else
/*  304 */       stdDev = 0.0D;
/*  305 */     boolean isPseudoColor = stdDev < 20.0D;
/*  306 */     if ((int)stdDev == 67) isPseudoColor = true;
/*  307 */     if (IJ.debugMode)
/*  308 */       IJ.log("isPseudoColorLut: " + isPseudoColor + " " + stdDev);
/*  309 */     return isPseudoColor;
/*      */   }
/*      */ 
/*      */   public boolean isDefaultLut()
/*      */   {
/*  314 */     if (this.cm == null)
/*  315 */       makeDefaultColorModel();
/*  316 */     if (!(this.cm instanceof IndexColorModel))
/*  317 */       return false;
/*  318 */     IndexColorModel icm = (IndexColorModel)this.cm;
/*  319 */     int mapSize = icm.getMapSize();
/*  320 */     if (mapSize != 256)
/*  321 */       return false;
/*  322 */     byte[] reds = new byte[mapSize];
/*  323 */     byte[] greens = new byte[mapSize];
/*  324 */     byte[] blues = new byte[mapSize];
/*  325 */     icm.getReds(reds);
/*  326 */     icm.getGreens(greens);
/*  327 */     icm.getBlues(blues);
/*  328 */     boolean isDefault = true;
/*  329 */     for (int i = 0; i < mapSize; i++) {
/*  330 */       if (((reds[i] & 0xFF) != i) || ((greens[i] & 0xFF) != i) || ((blues[i] & 0xFF) != i)) {
/*  331 */         isDefault = false;
/*  332 */         break;
/*      */       }
/*      */     }
/*  335 */     return isDefault;
/*      */   }
/*      */ 
/*      */   public abstract void setColor(Color paramColor);
/*      */ 
/*      */   public void setColor(int value)
/*      */   {
/*  344 */     setValue(value);
/*      */   }
/*      */ 
/*      */   public abstract void setValue(double paramDouble);
/*      */ 
/*      */   public abstract void setBackgroundValue(double paramDouble);
/*      */ 
/*      */   public abstract double getBackgroundValue();
/*      */ 
/*      */   public abstract double getMin();
/*      */ 
/*      */   public abstract double getMax();
/*      */ 
/*      */   public abstract void setMinAndMax(double paramDouble1, double paramDouble2);
/*      */ 
/*      */   public void resetMinAndMax()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setThreshold(double minThreshold, double maxThreshold, int lutUpdate)
/*      */   {
/*  379 */     if ((this instanceof ColorProcessor))
/*  380 */       return;
/*  381 */     this.minThreshold = minThreshold;
/*  382 */     this.maxThreshold = maxThreshold;
/*  383 */     this.lutUpdateMode = lutUpdate;
/*      */ 
/*  385 */     if (minThreshold == -808080.0D) {
/*  386 */       resetThreshold();
/*  387 */       return;
/*      */     }
/*      */ 
/*  390 */     if (lutUpdate == 2)
/*  391 */       return;
/*  392 */     if (this.rLUT1 == null) {
/*  393 */       if (this.cm == null)
/*  394 */         makeDefaultColorModel();
/*  395 */       this.baseCM = this.cm;
/*  396 */       IndexColorModel m = (IndexColorModel)this.cm;
/*  397 */       this.rLUT1 = new byte[256]; this.gLUT1 = new byte[256]; this.bLUT1 = new byte[256];
/*  398 */       m.getReds(this.rLUT1); m.getGreens(this.gLUT1); m.getBlues(this.bLUT1);
/*  399 */       this.rLUT2 = new byte[256]; this.gLUT2 = new byte[256]; this.bLUT2 = new byte[256];
/*      */     }
/*  401 */     int t1 = (int)minThreshold;
/*  402 */     int t2 = (int)maxThreshold;
/*      */ 
/*  404 */     if (lutUpdate == 0) {
/*  405 */       for (int i = 0; i < 256; i++) {
/*  406 */         if ((i >= t1) && (i <= t2)) {
/*  407 */           this.rLUT2[i] = -1;
/*  408 */           this.gLUT2[i] = 0;
/*  409 */           this.bLUT2[i] = 0;
/*      */         } else {
/*  411 */           this.rLUT2[i] = this.rLUT1[i];
/*  412 */           this.gLUT2[i] = this.gLUT1[i];
/*  413 */           this.bLUT2[i] = this.bLUT1[i];
/*      */         }
/*      */       }
/*      */     }
/*  417 */     else if (lutUpdate == 1)
/*      */     {
/*  420 */       byte foreground = Prefs.blackBackground ? -1 : 0;
/*      */ 
/*  422 */       byte background = (byte)(255 - foreground);
/*      */ 
/*  424 */       for (int i = 0; i < 256; i++)
/*      */       {
/*  426 */         if ((i >= t1) && (i <= t2))
/*      */         {
/*  428 */           this.rLUT2[i] = foreground;
/*      */ 
/*  430 */           this.gLUT2[i] = foreground;
/*      */ 
/*  432 */           this.bLUT2[i] = foreground;
/*      */         }
/*      */         else
/*      */         {
/*  436 */           this.rLUT2[i] = background;
/*      */ 
/*  438 */           this.gLUT2[i] = background;
/*      */ 
/*  440 */           this.bLUT2[i] = background;
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  446 */       for (int i = 0; i < 256; i++) {
/*  447 */         if ((i >= t1) && (i <= t2)) {
/*  448 */           this.rLUT2[i] = this.rLUT1[i];
/*  449 */           this.gLUT2[i] = this.gLUT1[i];
/*  450 */           this.bLUT2[i] = this.bLUT1[i];
/*      */         }
/*  452 */         else if (i > t2) {
/*  453 */           this.rLUT2[i] = ((byte)overRed);
/*  454 */           this.gLUT2[i] = ((byte)overGreen);
/*  455 */           this.bLUT2[i] = ((byte)overBlue);
/*      */         } else {
/*  457 */           this.rLUT2[i] = ((byte)underRed);
/*  458 */           this.gLUT2[i] = ((byte)underGreen);
/*  459 */           this.bLUT2[i] = ((byte)underBlue);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  464 */     this.cm = new IndexColorModel(8, 256, this.rLUT2, this.gLUT2, this.bLUT2);
/*  465 */     this.newPixels = true;
/*  466 */     this.source = null;
/*      */   }
/*      */ 
/*      */   public void setAutoThreshold(String mString) {
/*  470 */     if (mString == null)
/*  471 */       throw new IllegalArgumentException("Null method");
/*  472 */     boolean darkBackground = mString.indexOf("dark") != -1;
/*  473 */     int index = mString.indexOf(" ");
/*  474 */     if (index != -1)
/*  475 */       mString = mString.substring(0, index);
/*  476 */     setAutoThreshold(mString, darkBackground, 0);
/*      */   }
/*      */ 
/*      */   public void setAutoThreshold(String mString, boolean darkBackground, int lutUpdate) {
/*  480 */     AutoThresholder.Method m = null;
/*      */     try {
/*  482 */       m = (AutoThresholder.Method)AutoThresholder.Method.valueOf(AutoThresholder.Method.class, mString);
/*      */     } catch (Exception e) {
/*  484 */       m = null;
/*      */     }
/*  486 */     if (m == null)
/*  487 */       throw new IllegalArgumentException("Invalid method (\"" + mString + "\")");
/*  488 */     setAutoThreshold(m, darkBackground, lutUpdate);
/*      */   }
/*      */ 
/*      */   public void setAutoThreshold(AutoThresholder.Method method, boolean darkBackground) {
/*  492 */     setAutoThreshold(method, darkBackground, 0);
/*      */   }
/*      */ 
/*      */   public void setAutoThreshold(AutoThresholder.Method method, boolean darkBackground, int lutUpdate) {
/*  496 */     if ((method == null) || ((this instanceof ColorProcessor)))
/*  497 */       return;
/*  498 */     double min = 0.0D; double max = 0.0D;
/*  499 */     boolean notByteData = !(this instanceof ByteProcessor);
/*  500 */     ImageProcessor ip2 = this;
/*  501 */     if (notByteData) {
/*  502 */       ImageProcessor mask = ip2.getMask();
/*  503 */       Rectangle rect = ip2.getRoi();
/*  504 */       resetMinAndMax();
/*  505 */       min = getMin(); max = getMax();
/*  506 */       ip2 = convertToByte(true);
/*  507 */       ip2.setMask(mask);
/*  508 */       ip2.setRoi(rect);
/*      */     }
/*  510 */     ImageStatistics stats = ip2.getStatistics();
/*  511 */     AutoThresholder thresholder = new AutoThresholder();
/*  512 */     int threshold = thresholder.getThreshold(method, stats.histogram);
/*      */     double upper;
/*      */     double lower;
/*      */     double upper;
/*  514 */     if (darkBackground)
/*      */     {
/*      */       double upper;
/*  515 */       if (isInvertedLut()) {
/*  516 */         double lower = 0.0D; upper = threshold;
/*      */       } else {
/*  518 */         double lower = threshold + 1; upper = 255.0D;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       double upper;
/*  520 */       if (isInvertedLut()) {
/*  521 */         double lower = threshold + 1; upper = 255.0D;
/*      */       } else {
/*  523 */         lower = 0.0D; upper = threshold;
/*      */       }
/*      */     }
/*  525 */     if (lower > 255.0D) lower = 255.0D;
/*  526 */     if (notByteData)
/*  527 */       if (max > min) {
/*  528 */         lower = min + lower / 255.0D * (max - min);
/*  529 */         upper = min + upper / 255.0D * (max - min);
/*      */       } else {
/*  531 */         lower = upper = min;
/*      */       }
/*  533 */     setThreshold(lower, upper, lutUpdate);
/*      */   }
/*      */ 
/*      */   public void setAutoThreshold(int method, int lutUpdate)
/*      */   {
/*  541 */     if ((method < 0) || (method > 1))
/*  542 */       throw new IllegalArgumentException("Invalid thresholding method");
/*  543 */     if ((this instanceof ColorProcessor))
/*  544 */       return;
/*  545 */     double min = 0.0D; double max = 0.0D;
/*  546 */     boolean notByteData = !(this instanceof ByteProcessor);
/*  547 */     ImageProcessor ip2 = this;
/*  548 */     if (notByteData) {
/*  549 */       ImageProcessor mask = ip2.getMask();
/*  550 */       Rectangle rect = ip2.getRoi();
/*  551 */       resetMinAndMax();
/*  552 */       min = getMin(); max = getMax();
/*  553 */       ip2 = convertToByte(true);
/*  554 */       ip2.setMask(mask);
/*  555 */       ip2.setRoi(rect);
/*      */     }
/*  557 */     ImageStatistics stats = ip2.getStatistics();
/*  558 */     int[] histogram = stats.histogram;
/*  559 */     int originalModeCount = histogram[stats.mode];
/*  560 */     if (method == 1) {
/*  561 */       int maxCount2 = 0;
/*  562 */       for (int i = 0; i < stats.nBins; i++) {
/*  563 */         if ((histogram[i] > maxCount2) && (i != stats.mode))
/*  564 */           maxCount2 = histogram[i];
/*      */       }
/*  566 */       int hmax = stats.maxCount;
/*  567 */       if ((hmax > maxCount2 * 2) && (maxCount2 != 0)) {
/*  568 */         hmax = (int)(maxCount2 * 1.5D);
/*  569 */         histogram[stats.mode] = hmax;
/*      */       }
/*      */     }
/*  572 */     int threshold = ip2.getAutoThreshold(stats.histogram);
/*  573 */     histogram[stats.mode] = originalModeCount;
/*  574 */     float[] hist = new float[256];
/*  575 */     for (int i = 0; i < 256; i++)
/*  576 */       hist[i] = stats.histogram[i];
/*  577 */     FloatProcessor fp = new FloatProcessor(256, 1, hist, null);
/*  578 */     GaussianBlur gb = new GaussianBlur();
/*  579 */     gb.blur1Direction(fp, 2.0D, 0.01D, true, 0);
/*  580 */     float maxCount = 0.0F; float sum = 0.0F;
/*  581 */     int mode = 0;
/*  582 */     for (int i = 0; i < 256; i++) {
/*  583 */       float count = hist[i];
/*  584 */       sum += count;
/*  585 */       if (count > maxCount) {
/*  586 */         maxCount = count;
/*  587 */         mode = i;
/*      */       }
/*      */     }
/*  590 */     double avg = sum / 256.0D;
/*      */     double upper;
/*      */     double lower;
/*      */     double upper;
/*  592 */     if (maxCount / avg > 1.5D)
/*      */     {
/*      */       double upper;
/*  593 */       if (stats.max - mode > mode - stats.min) {
/*  594 */         double lower = threshold; upper = 255.0D;
/*      */       } else {
/*  596 */         double lower = 0.0D; upper = threshold;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       double upper;
/*  598 */       if (isInvertedLut()) {
/*  599 */         double lower = threshold; upper = 255.0D;
/*      */       } else {
/*  601 */         lower = 0.0D; upper = threshold;
/*      */       }
/*      */     }
/*  603 */     if (notByteData)
/*  604 */       if (max > min) {
/*  605 */         lower = min + lower / 255.0D * (max - min);
/*  606 */         upper = min + upper / 255.0D * (max - min);
/*      */       } else {
/*  608 */         lower = upper = min;
/*      */       }
/*  610 */     setThreshold(lower, upper, lutUpdate);
/*      */   }
/*      */ 
/*      */   public void resetThreshold()
/*      */   {
/*  617 */     this.minThreshold = -808080.0D;
/*  618 */     if (this.baseCM != null) {
/*  619 */       this.cm = this.baseCM;
/*  620 */       this.baseCM = null;
/*      */     }
/*  622 */     this.rLUT1 = (this.rLUT2 = null);
/*  623 */     this.inversionTested = false;
/*  624 */     this.newPixels = true;
/*  625 */     this.source = null;
/*      */   }
/*      */ 
/*      */   public double getMinThreshold()
/*      */   {
/*  631 */     return this.minThreshold;
/*      */   }
/*      */ 
/*      */   public double getMaxThreshold()
/*      */   {
/*  636 */     return this.maxThreshold;
/*      */   }
/*      */ 
/*      */   public int getLutUpdateMode()
/*      */   {
/*  642 */     return this.lutUpdateMode;
/*      */   }
/*      */ 
/*      */   public void setBinaryThreshold()
/*      */   {
/*  651 */     if (!(this instanceof ByteProcessor)) return;
/*  652 */     double t1 = 255.0D; double t2 = 255.0D;
/*  653 */     boolean invertedLut = isInvertedLut();
/*  654 */     if (((invertedLut) && (Prefs.blackBackground)) || ((!invertedLut) && (!Prefs.blackBackground))) {
/*  655 */       t1 = 0.0D;
/*  656 */       t2 = 0.0D;
/*      */     }
/*      */ 
/*  659 */     setThreshold(t1, t2, 2);
/*      */   }
/*      */ 
/*      */   public void resetBinaryThreshold()
/*      */   {
/*  667 */     if ((this.minThreshold == this.maxThreshold) && (this.lutUpdateMode == 2))
/*  668 */       resetThreshold();
/*      */   }
/*      */ 
/*      */   public void setRoi(Rectangle roi)
/*      */   {
/*  676 */     if (roi == null)
/*  677 */       resetRoi();
/*      */     else
/*  679 */       setRoi(roi.x, roi.y, roi.width, roi.height);
/*      */   }
/*      */ 
/*      */   public void setRoi(int x, int y, int rwidth, int rheight)
/*      */   {
/*  687 */     if ((x < 0) || (y < 0) || (x + rwidth > this.width) || (y + rheight > this.height))
/*      */     {
/*  689 */       Rectangle r1 = new Rectangle(x, y, rwidth, rheight);
/*  690 */       Rectangle r2 = r1.intersection(new Rectangle(0, 0, this.width, this.height));
/*  691 */       if ((r2.width <= 0) || (r2.height <= 0)) {
/*  692 */         this.roiX = 0; this.roiY = 0; this.roiWidth = 0; this.roiHeight = 0;
/*  693 */         this.xMin = 0; this.xMax = 0; this.yMin = 0; this.yMax = 0;
/*  694 */         this.mask = null;
/*  695 */         return;
/*      */       }
/*  697 */       if ((this.mask != null) && (this.mask.getWidth() == rwidth) && (this.mask.getHeight() == rheight)) {
/*  698 */         Rectangle r3 = new Rectangle(0, 0, r2.width, r2.height);
/*  699 */         if (x < 0) r3.x = (-x);
/*  700 */         if (y < 0) r3.y = (-y);
/*  701 */         this.mask.setRoi(r3);
/*  702 */         this.mask = this.mask.crop();
/*      */       }
/*  704 */       this.roiX = r2.x; this.roiY = r2.y; this.roiWidth = r2.width; this.roiHeight = r2.height;
/*      */     } else {
/*  706 */       this.roiX = x; this.roiY = y; this.roiWidth = rwidth; this.roiHeight = rheight;
/*      */     }
/*  708 */     if ((this.mask != null) && ((this.mask.getWidth() != this.roiWidth) || (this.mask.getHeight() != this.roiHeight))) {
/*  709 */       this.mask = null;
/*      */     }
/*  711 */     this.xMin = Math.max(this.roiX, 1);
/*  712 */     this.xMax = Math.min(this.roiX + this.roiWidth - 1, this.width - 2);
/*  713 */     this.yMin = Math.max(this.roiY, 1);
/*  714 */     this.yMax = Math.min(this.roiY + this.roiHeight - 1, this.height - 2);
/*      */   }
/*      */ 
/*      */   public void setRoi(Roi roi)
/*      */   {
/*  731 */     if (roi == null) {
/*  732 */       resetRoi();
/*      */     } else {
/*  734 */       setMask(roi.getMask());
/*  735 */       setRoi(roi.getBounds());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setRoi(Polygon roi)
/*      */   {
/*  757 */     if (roi == null) {
/*  758 */       resetRoi(); return;
/*  759 */     }Rectangle bounds = roi.getBounds();
/*  760 */     for (int i = 0; i < roi.npoints; i++) {
/*  761 */       roi.xpoints[i] -= bounds.x;
/*  762 */       roi.ypoints[i] -= bounds.y;
/*      */     }
/*  764 */     PolygonFiller pf = new PolygonFiller();
/*  765 */     pf.setPolygon(roi.xpoints, roi.ypoints, roi.npoints);
/*  766 */     ImageProcessor mask = pf.getMask(bounds.width, bounds.height);
/*  767 */     setMask(mask);
/*  768 */     setRoi(bounds);
/*  769 */     for (int i = 0; i < roi.npoints; i++) {
/*  770 */       roi.xpoints[i] += bounds.x;
/*  771 */       roi.ypoints[i] += bounds.y;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void resetRoi()
/*      */   {
/*  777 */     this.roiX = 0; this.roiY = 0; this.roiWidth = this.width; this.roiHeight = this.height;
/*  778 */     this.xMin = 1; this.xMax = (this.width - 2); this.yMin = 1; this.yMax = (this.height - 2);
/*  779 */     this.mask = null;
/*  780 */     this.clipXMin = 0; this.clipXMax = (this.width - 1); this.clipYMin = 0; this.clipYMax = (this.height - 1);
/*      */   }
/*      */ 
/*      */   public Rectangle getRoi()
/*      */   {
/*  786 */     return new Rectangle(this.roiX, this.roiY, this.roiWidth, this.roiHeight);
/*      */   }
/*      */ 
/*      */   public void setMask(ImageProcessor mask)
/*      */   {
/*  793 */     this.mask = mask;
/*      */   }
/*      */ 
/*      */   public ImageProcessor getMask()
/*      */   {
/*  799 */     return this.mask;
/*      */   }
/*      */ 
/*      */   public byte[] getMaskArray()
/*      */   {
/*  804 */     return this.mask != null ? (byte[])this.mask.getPixels() : null;
/*      */   }
/*      */ 
/*      */   public void setProgressBar(ProgressBar pb)
/*      */   {
/*  810 */     this.progressBar = pb;
/*      */   }
/*      */ 
/*      */   public void setInterpolate(boolean interpolate)
/*      */   {
/*  815 */     this.interpolate = interpolate;
/*  816 */     if (interpolate)
/*  817 */       this.interpolationMethod = (useBicubic ? 2 : 1);
/*      */     else
/*  819 */       this.interpolationMethod = 0;
/*      */   }
/*      */ 
/*      */   public void setInterpolationMethod(int method)
/*      */   {
/*  825 */     if ((method < 0) || (method > 2))
/*  826 */       throw new IllegalArgumentException("Invalid interpolation method");
/*  827 */     this.interpolationMethod = method;
/*  828 */     this.interpolate = (method != 0);
/*      */   }
/*      */ 
/*      */   public int getInterpolationMethod()
/*      */   {
/*  833 */     return this.interpolationMethod;
/*      */   }
/*      */ 
/*      */   public static String[] getInterpolationMethods() {
/*  837 */     if (interpolationMethods == null)
/*  838 */       interpolationMethods = new String[] { "None", "Bilinear", "Bicubic" };
/*  839 */     return interpolationMethods;
/*      */   }
/*      */ 
/*      */   public boolean getInterpolate()
/*      */   {
/*  844 */     return this.interpolate;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public boolean isKillable() {
/*  849 */     return false;
/*      */   }
/*      */ 
/*      */   private void process(int op, double value) {
/*  853 */     double SCALE = 255.0D / Math.log(255.0D);
/*      */ 
/*  856 */     int[] lut = new int[256];
/*  857 */     for (int i = 0; i < 256; i++)
/*      */     {
/*      */       int v;
/*      */       int v;
/*      */       int v;
/*      */       int v;
/*  858 */       switch (op) {
/*      */       case 0:
/*  860 */         v = 255 - i;
/*  861 */         break;
/*      */       case 1:
/*  863 */         v = this.fgColor;
/*  864 */         break;
/*      */       case 2:
/*  866 */         v = i + (int)value;
/*  867 */         break;
/*      */       case 3:
/*  869 */         v = (int)Math.round(i * value);
/*  870 */         break;
/*      */       case 4:
/*  872 */         v = i & (int)value;
/*  873 */         break;
/*      */       case 5:
/*  875 */         v = i | (int)value;
/*  876 */         break;
/*      */       case 6:
/*  878 */         v = i ^ (int)value;
/*  879 */         break;
/*      */       case 7:
/*  881 */         v = (int)(Math.exp(Math.log(i / 255.0D) * value) * 255.0D);
/*  882 */         break;
/*      */       case 8:
/*  884 */         if (i == 0)
/*  885 */           v = 0;
/*      */         else
/*  887 */           v = (int)(Math.log(i) * SCALE);
/*  888 */         break;
/*      */       case 13:
/*  890 */         v = (int)Math.exp(i / SCALE);
/*  891 */         break;
/*      */       case 11:
/*  893 */         v = i * i;
/*  894 */         break;
/*      */       case 12:
/*  896 */         v = (int)Math.sqrt(i);
/*  897 */         break;
/*      */       case 9:
/*  899 */         if (i < value)
/*  900 */           v = (int)value;
/*      */         else
/*  902 */           v = i;
/*  903 */         break;
/*      */       case 10:
/*  905 */         if (i > value)
/*  906 */           v = (int)value;
/*      */         else
/*  908 */           v = i;
/*  909 */         break;
/*      */       default:
/*  911 */         v = i;
/*      */       }
/*  913 */       if (v < 0)
/*  914 */         v = 0;
/*  915 */       if (v > 255)
/*  916 */         v = 255;
/*  917 */       lut[i] = v;
/*      */     }
/*  919 */     applyTable(lut);
/*      */   }
/*      */ 
/*      */   public double[] getLine(double x1, double y1, double x2, double y2)
/*      */   {
/*  930 */     double dx = x2 - x1;
/*  931 */     double dy = y2 - y1;
/*  932 */     int n = (int)Math.round(Math.sqrt(dx * dx + dy * dy));
/*  933 */     double xinc = dx / n;
/*  934 */     double yinc = dy / n;
/*  935 */     if (((xinc != 0.0D) || (n != this.height)) && ((yinc != 0.0D) || (n != this.width)))
/*  936 */       n++;
/*  937 */     double[] data = new double[n];
/*  938 */     double rx = x1;
/*  939 */     double ry = y1;
/*  940 */     if (this.interpolate)
/*  941 */       for (int i = 0; i < n; i++) {
/*  942 */         data[i] = getInterpolatedValue(rx, ry);
/*  943 */         rx += xinc;
/*  944 */         ry += yinc;
/*      */       }
/*      */     else {
/*  947 */       for (int i = 0; i < n; i++) {
/*  948 */         data[i] = getPixelValue((int)(rx + 0.5D), (int)(ry + 0.5D));
/*  949 */         rx += xinc;
/*  950 */         ry += yinc;
/*      */       }
/*      */     }
/*  953 */     return data;
/*      */   }
/*      */ 
/*      */   public void getRow(int x, int y, int[] data, int length)
/*      */   {
/*  958 */     for (int i = 0; i < length; i++)
/*  959 */       data[i] = getPixel(x++, y);
/*      */   }
/*      */ 
/*      */   public void getColumn(int x, int y, int[] data, int length)
/*      */   {
/*  964 */     for (int i = 0; i < length; i++)
/*  965 */       data[i] = getPixel(x, y++);
/*      */   }
/*      */ 
/*      */   public void putRow(int x, int y, int[] data, int length)
/*      */   {
/*  971 */     for (int i = 0; i < length; i++)
/*  972 */       putPixel(x++, y, data[i]);
/*      */   }
/*      */ 
/*      */   public void putColumn(int x, int y, int[] data, int length)
/*      */   {
/*  981 */     for (int i = 0; i < length; i++)
/*  982 */       putPixel(x, y++, data[i]);
/*      */   }
/*      */ 
/*      */   public void moveTo(int x, int y)
/*      */   {
/*  991 */     this.cx = x;
/*  992 */     this.cy = y;
/*      */   }
/*      */ 
/*      */   public void setLineWidth(int width)
/*      */   {
/*  997 */     this.lineWidth = width;
/*  998 */     if (this.lineWidth < 1) this.lineWidth = 1;
/*      */   }
/*      */ 
/*      */   public int getLineWidth()
/*      */   {
/* 1003 */     return this.lineWidth;
/*      */   }
/*      */ 
/*      */   public void lineTo(int x2, int y2)
/*      */   {
/* 1008 */     int dx = x2 - this.cx;
/* 1009 */     int dy = y2 - this.cy;
/* 1010 */     int absdx = dx >= 0 ? dx : -dx;
/* 1011 */     int absdy = dy >= 0 ? dy : -dy;
/* 1012 */     int n = absdy > absdx ? absdy : absdx;
/* 1013 */     double xinc = dx / n;
/* 1014 */     double yinc = dy / n;
/* 1015 */     double x = this.cx;
/* 1016 */     double y = this.cy;
/* 1017 */     n++;
/* 1018 */     this.cx = x2; this.cy = y2;
/* 1019 */     if (n > 1000000) return;
/*      */     do {
/* 1021 */       if (this.lineWidth == 1)
/* 1022 */         drawPixel((int)Math.round(x), (int)Math.round(y));
/* 1023 */       else if (this.lineWidth == 2)
/* 1024 */         drawDot2((int)Math.round(x), (int)Math.round(y));
/*      */       else
/* 1026 */         drawDot((int)x, (int)y);
/* 1027 */       x += xinc;
/* 1028 */       y += yinc;
/* 1029 */       n--; } while (n > 0);
/*      */   }
/*      */ 
/*      */   public void drawLine(int x1, int y1, int x2, int y2)
/*      */   {
/* 1035 */     moveTo(x1, y1);
/* 1036 */     lineTo(x2, y2);
/*      */   }
/*      */ 
/*      */   public void drawRect(int x, int y, int width, int height)
/*      */   {
/* 1041 */     if ((width < 1) || (height < 1))
/* 1042 */       return;
/* 1043 */     if (this.lineWidth == 1) {
/* 1044 */       moveTo(x, y);
/* 1045 */       lineTo(x + width - 1, y);
/* 1046 */       lineTo(x + width - 1, y + height - 1);
/* 1047 */       lineTo(x, y + height - 1);
/* 1048 */       lineTo(x, y);
/*      */     } else {
/* 1050 */       moveTo(x, y);
/* 1051 */       lineTo(x + width, y);
/* 1052 */       lineTo(x + width, y + height);
/* 1053 */       lineTo(x, y + height);
/* 1054 */       lineTo(x, y);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawOval(int x, int y, int width, int height)
/*      */   {
/* 1060 */     if (width * height > 4 * this.width * this.height) return;
/* 1061 */     OvalRoi oval = new OvalRoi(x, y, width, height);
/* 1062 */     drawPolygon(oval.getPolygon());
/*      */   }
/*      */ 
/*      */   public void fillOval(int x, int y, int width, int height)
/*      */   {
/* 1067 */     if (width * height > 4 * this.width * this.height) return;
/* 1068 */     OvalRoi oval = new OvalRoi(x, y, width, height);
/* 1069 */     fillPolygon(oval.getPolygon());
/*      */   }
/*      */ 
/*      */   public void drawPolygon(Polygon p)
/*      */   {
/* 1074 */     moveTo(p.xpoints[0], p.ypoints[0]);
/* 1075 */     for (int i = 0; i < p.npoints; i++)
/* 1076 */       lineTo(p.xpoints[i], p.ypoints[i]);
/* 1077 */     lineTo(p.xpoints[0], p.ypoints[0]);
/*      */   }
/*      */ 
/*      */   public void fillPolygon(Polygon p)
/*      */   {
/* 1082 */     setRoi(p);
/* 1083 */     fill(getMask());
/* 1084 */     resetRoi();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void drawDot2(int x, int y) {
/* 1089 */     drawPixel(x, y);
/* 1090 */     drawPixel(x - 1, y);
/* 1091 */     drawPixel(x, y - 1);
/* 1092 */     drawPixel(x - 1, y - 1);
/*      */   }
/*      */ 
/*      */   public void drawDot(int xcenter, int ycenter)
/*      */   {
/* 1097 */     double r = this.lineWidth / 2.0D;
/* 1098 */     int xmin = (int)(xcenter - r + 0.5D); int ymin = (int)(ycenter - r + 0.5D);
/* 1099 */     int xmax = xmin + this.lineWidth; int ymax = ymin + this.lineWidth;
/* 1100 */     if ((xmin < this.clipXMin) || (ymin < this.clipYMin) || (xmax > this.clipXMax) || (ymax > this.clipYMax))
/*      */     {
/* 1102 */       double r2 = r * r;
/* 1103 */       r -= 0.5D;
/* 1104 */       double xoffset = xmin + r; double yoffset = ymin + r;
/*      */ 
/* 1106 */       for (int y = ymin; y < ymax; y++)
/* 1107 */         for (int x = xmin; x < xmax; x++) {
/* 1108 */           double xx = x - xoffset; double yy = y - yoffset;
/* 1109 */           if (xx * xx + yy * yy <= r2)
/* 1110 */             drawPixel(x, y);
/*      */         }
/*      */     }
/*      */     else {
/* 1114 */       if ((this.dotMask == null) || (this.lineWidth != this.dotMask.getWidth())) {
/* 1115 */         OvalRoi oval = new OvalRoi(0, 0, this.lineWidth, this.lineWidth);
/* 1116 */         this.dotMask = oval.getMask();
/*      */       }
/* 1118 */       setRoi(xmin, ymin, this.lineWidth, this.lineWidth);
/* 1119 */       fill(this.dotMask);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setupFontMetrics()
/*      */   {
/* 1126 */     if (this.fmImage == null)
/* 1127 */       this.fmImage = new BufferedImage(1, 1, 1);
/* 1128 */     if (this.font == null)
/* 1129 */       this.font = new Font("SansSerif", 0, 12);
/* 1130 */     if (this.fontMetrics == null) {
/* 1131 */       Graphics g = this.fmImage.getGraphics();
/* 1132 */       this.fontMetrics = g.getFontMetrics(this.font);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawString(String s)
/*      */   {
/* 1139 */     if ((s == null) || (s.equals(""))) return;
/* 1140 */     setupFontMetrics();
/* 1141 */     if (IJ.isMacOSX()) s = s + " ";
/* 1142 */     if (s.indexOf("\n") == -1) {
/* 1143 */       drawString2(s);
/*      */     } else {
/* 1145 */       String[] s2 = Tools.split(s, "\n");
/* 1146 */       for (int i = 0; i < s2.length; i++)
/* 1147 */         drawString2(s2[i]);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void drawString2(String s) {
/* 1152 */     int w = getStringWidth(s);
/* 1153 */     int cxx = this.cx;
/* 1154 */     if (this.justification == 1)
/* 1155 */       cxx -= w / 2;
/* 1156 */     else if (this.justification == 2)
/* 1157 */       cxx -= w;
/* 1158 */     int h = this.fontMetrics.getHeight();
/* 1159 */     if ((w <= 0) || (h <= 0)) return;
/* 1160 */     Image bi = new BufferedImage(w, h, 1);
/* 1161 */     Graphics g = bi.getGraphics();
/* 1162 */     FontMetrics metrics = g.getFontMetrics(this.font);
/* 1163 */     int fontHeight = metrics.getHeight();
/* 1164 */     int descent = metrics.getDescent();
/* 1165 */     g.setFont(this.font);
/*      */ 
/* 1167 */     if ((this.antialiasedText) && (cxx >= 0) && (this.cy - h >= 0)) {
/* 1168 */       Java2.setAntialiasedText(g, true);
/* 1169 */       setRoi(cxx, this.cy - h, w, h);
/* 1170 */       ImageProcessor ip = crop();
/* 1171 */       resetRoi();
/* 1172 */       if ((ip.getWidth() == 0) || (ip.getHeight() == 0))
/* 1173 */         return;
/* 1174 */       g.drawImage(ip.createImage(), 0, 0, null);
/* 1175 */       g.setColor(this.drawingColor);
/* 1176 */       g.drawString(s, 0, h - descent);
/* 1177 */       g.dispose();
/* 1178 */       ip = new ColorProcessor(bi);
/* 1179 */       if ((this instanceof ByteProcessor)) {
/* 1180 */         ip = ip.convertToByte(false);
/* 1181 */         if (isInvertedLut()) ip.invert();
/*      */       }
/*      */ 
/* 1184 */       insert(ip, cxx, this.cy - h);
/* 1185 */       this.cy += h;
/* 1186 */       return;
/*      */     }
/*      */ 
/* 1189 */     Java2.setAntialiasedText(g, false);
/* 1190 */     g.setColor(Color.white);
/* 1191 */     g.fillRect(0, 0, w, h);
/* 1192 */     g.setColor(Color.black);
/* 1193 */     g.drawString(s, 0, h - descent);
/* 1194 */     g.dispose();
/* 1195 */     ImageProcessor ip = new ColorProcessor(bi);
/* 1196 */     ImageProcessor textMask = ip.convertToByte(false);
/* 1197 */     byte[] mpixels = (byte[])textMask.getPixels();
/*      */ 
/* 1199 */     textMask.invert();
/* 1200 */     if ((cxx < this.width) && (this.cy - h < this.height)) {
/* 1201 */       setMask(textMask);
/* 1202 */       setRoi(cxx, this.cy - h, w, h);
/* 1203 */       fill(getMask());
/*      */     }
/* 1205 */     resetRoi();
/* 1206 */     this.cy += h;
/*      */   }
/*      */ 
/*      */   public void drawString(String s, int x, int y)
/*      */   {
/* 1211 */     moveTo(x, y);
/* 1212 */     drawString(s);
/*      */   }
/*      */ 
/*      */   public void drawString(String s, int x, int y, Color background)
/*      */   {
/* 1220 */     Color foreground = this.drawingColor;
/* 1221 */     FontMetrics metrics = getFontMetrics();
/* 1222 */     int w = 0;
/* 1223 */     int h = metrics.getAscent() + metrics.getDescent();
/* 1224 */     int y2 = y;
/* 1225 */     if (s.indexOf("\n") != -1) {
/* 1226 */       String[] s2 = Tools.split(s, "\n");
/* 1227 */       for (int i = 0; i < s2.length; i++) {
/* 1228 */         int w2 = getStringWidth(s2[i]);
/* 1229 */         if (w2 > w) w = w2;
/*      */       }
/* 1231 */       int h2 = metrics.getHeight();
/* 1232 */       y2 += h2 * (s2.length - 1);
/* 1233 */       h += h2 * (s2.length - 1);
/*      */     } else {
/* 1235 */       w = getStringWidth(s);
/* 1236 */     }int x2 = x;
/* 1237 */     if (this.justification == 1)
/* 1238 */       x2 -= w / 2;
/* 1239 */     else if (this.justification == 2)
/* 1240 */       x2 -= w;
/* 1241 */     setColor(background);
/* 1242 */     setRoi(x2, y2 - h, w, h);
/* 1243 */     fill();
/* 1244 */     resetRoi();
/* 1245 */     setColor(foreground);
/* 1246 */     drawString(s, x, y);
/*      */   }
/*      */ 
/*      */   public void setJustification(int justification)
/*      */   {
/* 1252 */     this.justification = justification;
/*      */   }
/*      */ 
/*      */   public void setFont(Font font)
/*      */   {
/* 1257 */     this.font = font;
/* 1258 */     this.fontMetrics = null;
/* 1259 */     this.boldFont = font.isBold();
/*      */   }
/*      */ 
/*      */   public void setAntialiasedText(boolean antialiasedText)
/*      */   {
/* 1266 */     if ((antialiasedText) && ((((this instanceof ByteProcessor)) && (getMin() == 0.0D) && (getMax() == 255.0D)) || ((this instanceof ColorProcessor))))
/* 1267 */       this.antialiasedText = true;
/*      */     else
/* 1269 */       this.antialiasedText = false;
/*      */   }
/*      */ 
/*      */   public int getStringWidth(String s)
/*      */   {
/* 1274 */     setupFontMetrics();
/*      */     int w;
/* 1276 */     if (this.antialiasedText) {
/* 1277 */       Graphics g = this.fmImage.getGraphics();
/* 1278 */       if (g == null) {
/* 1279 */         this.fmImage = null;
/* 1280 */         setupFontMetrics();
/* 1281 */         g = this.fmImage.getGraphics();
/*      */       }
/* 1283 */       Java2.setAntialiasedText(g, true);
/* 1284 */       int w = Java2.getStringWidth(s, this.fontMetrics, g);
/* 1285 */       g.dispose();
/*      */     } else {
/* 1287 */       w = this.fontMetrics.stringWidth(s);
/* 1288 */     }return w;
/*      */   }
/*      */ 
/*      */   public Font getFont()
/*      */   {
/* 1293 */     setupFontMetrics();
/* 1294 */     return this.font;
/*      */   }
/*      */ 
/*      */   public FontMetrics getFontMetrics()
/*      */   {
/* 1299 */     setupFontMetrics();
/* 1300 */     return this.fontMetrics;
/*      */   }
/*      */ 
/*      */   public void smooth()
/*      */   {
/* 1305 */     if (this.width > 1)
/* 1306 */       filter(0);
/*      */   }
/*      */ 
/*      */   public void sharpen()
/*      */   {
/* 1311 */     if (this.width > 1) {
/* 1312 */       int[] kernel = { -1, -1, -1, -1, 12, -1, -1, -1, -1 };
/*      */ 
/* 1315 */       convolve3x3(kernel);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void findEdges()
/*      */   {
/* 1321 */     if (this.width > 1)
/* 1322 */       filter(1);
/*      */   }
/*      */ 
/*      */   public abstract void flipVertical();
/*      */ 
/*      */   public void flipHorizontal()
/*      */   {
/* 1330 */     int[] col1 = new int[this.roiHeight];
/* 1331 */     int[] col2 = new int[this.roiHeight];
/* 1332 */     for (int x = 0; x < this.roiWidth / 2; x++) {
/* 1333 */       getColumn(this.roiX + x, this.roiY, col1, this.roiHeight);
/* 1334 */       getColumn(this.roiX + this.roiWidth - x - 1, this.roiY, col2, this.roiHeight);
/* 1335 */       putColumn(this.roiX + x, this.roiY, col2, this.roiHeight);
/* 1336 */       putColumn(this.roiX + this.roiWidth - x - 1, this.roiY, col1, this.roiHeight);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ImageProcessor rotateRight()
/*      */   {
/* 1343 */     int width2 = this.height;
/* 1344 */     int height2 = this.width;
/* 1345 */     ImageProcessor ip2 = createProcessor(width2, height2);
/* 1346 */     int[] arow = new int[this.width];
/* 1347 */     for (int row = 0; row < this.height; row++) {
/* 1348 */       getRow(0, row, arow, this.width);
/* 1349 */       ip2.putColumn(width2 - row - 1, 0, arow, height2);
/*      */     }
/* 1351 */     return ip2;
/*      */   }
/*      */ 
/*      */   public ImageProcessor rotateLeft()
/*      */   {
/* 1357 */     int width2 = this.height;
/* 1358 */     int height2 = this.width;
/* 1359 */     ImageProcessor ip2 = createProcessor(width2, height2);
/* 1360 */     int[] arow = new int[this.width];
/* 1361 */     int[] arow2 = new int[this.width];
/* 1362 */     for (int row = 0; row < this.height; row++) {
/* 1363 */       getRow(0, row, arow, this.width);
/* 1364 */       for (int i = 0; i < this.width; i++) {
/* 1365 */         arow2[i] = arow[(this.width - i - 1)];
/*      */       }
/* 1367 */       ip2.putColumn(row, 0, arow2, height2);
/*      */     }
/* 1369 */     return ip2;
/*      */   }
/*      */ 
/*      */   public void insert(ImageProcessor ip, int xloc, int yloc)
/*      */   {
/* 1374 */     copyBits(ip, xloc, yloc, 0);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1379 */     return "ip[width=" + this.width + ", height=" + this.height + ", min=" + getMin() + ", max=" + getMax() + "]";
/*      */   }
/*      */ 
/*      */   public void fill()
/*      */   {
/* 1389 */     process(1, 0.0D);
/*      */   }
/*      */ 
/*      */   public abstract void fill(ImageProcessor paramImageProcessor);
/*      */ 
/*      */   public void fill(Roi roi)
/*      */   {
/* 1410 */     ImageProcessor m = getMask();
/* 1411 */     Rectangle r = getRoi();
/* 1412 */     setRoi(roi);
/* 1413 */     fill(getMask());
/* 1414 */     setMask(m);
/* 1415 */     setRoi(r);
/*      */   }
/*      */ 
/*      */   public void fillOutside(Roi roi)
/*      */   {
/* 1420 */     if ((roi == null) || (!roi.isArea())) return;
/* 1421 */     ImageProcessor m = getMask();
/* 1422 */     Rectangle r = getRoi();
/*      */     ShapeRoi s1;
/*      */     ShapeRoi s1;
/* 1424 */     if ((roi instanceof ShapeRoi))
/* 1425 */       s1 = (ShapeRoi)roi;
/*      */     else
/* 1427 */       s1 = new ShapeRoi(roi);
/* 1428 */     ShapeRoi s2 = new ShapeRoi(new Roi(0, 0, this.width, this.height));
/* 1429 */     setRoi(s1.xor(s2));
/* 1430 */     fill(getMask());
/* 1431 */     setMask(m);
/* 1432 */     setRoi(r);
/*      */   }
/*      */ 
/*      */   public void draw(Roi roi)
/*      */   {
/* 1440 */     roi.drawPixels(this);
/*      */   }
/*      */ 
/*      */   public void drawRoi(Roi roi)
/*      */   {
/* 1452 */     Image img = createImage();
/* 1453 */     Graphics g = img.getGraphics();
/* 1454 */     ImagePlus imp = roi.getImage();
/* 1455 */     if (imp != null) {
/* 1456 */       roi.setImage(null);
/* 1457 */       roi.drawOverlay(g);
/* 1458 */       roi.setImage(imp);
/*      */     } else {
/* 1460 */       roi.drawOverlay(g);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawOverlay(Overlay overlay)
/*      */   {
/* 1469 */     Roi[] rois = overlay.toArray();
/* 1470 */     for (int i = 0; i < rois.length; i++)
/* 1471 */       drawRoi(rois[i]);
/*      */   }
/*      */ 
/*      */   public void setCalibrationTable(float[] cTable)
/*      */   {
/* 1481 */     this.cTable = cTable;
/*      */   }
/*      */ 
/*      */   public float[] getCalibrationTable()
/*      */   {
/* 1486 */     return this.cTable;
/*      */   }
/*      */ 
/*      */   public void setHistogramSize(int size)
/*      */   {
/* 1491 */     this.histogramSize = size;
/* 1492 */     if (this.histogramSize < 1) this.histogramSize = 1;
/*      */   }
/*      */ 
/*      */   public int getHistogramSize()
/*      */   {
/* 1498 */     return this.histogramSize;
/*      */   }
/*      */ 
/*      */   public void setHistogramRange(double histMin, double histMax)
/*      */   {
/* 1504 */     if (histMin > histMax) {
/* 1505 */       histMin = 0.0D;
/* 1506 */       histMax = 0.0D;
/*      */     }
/* 1508 */     this.histogramMin = histMin;
/* 1509 */     this.histogramMax = histMax;
/*      */   }
/*      */ 
/*      */   public double getHistogramMin()
/*      */   {
/* 1514 */     return this.histogramMin;
/*      */   }
/*      */ 
/*      */   public double getHistogramMax()
/*      */   {
/* 1519 */     return this.histogramMax;
/*      */   }
/*      */ 
/*      */   public abstract Object getPixels();
/*      */ 
/*      */   public abstract Object getPixelsCopy();
/*      */ 
/*      */   public abstract int getPixel(int paramInt1, int paramInt2);
/*      */ 
/*      */   public int getPixelCount()
/*      */   {
/* 1541 */     return this.width * this.height;
/*      */   }
/*      */ 
/*      */   public abstract int get(int paramInt1, int paramInt2);
/*      */ 
/*      */   public abstract int get(int paramInt);
/*      */ 
/*      */   public abstract void set(int paramInt1, int paramInt2, int paramInt3);
/*      */ 
/*      */   public abstract void set(int paramInt1, int paramInt2);
/*      */ 
/*      */   public abstract float getf(int paramInt1, int paramInt2);
/*      */ 
/*      */   public abstract float getf(int paramInt);
/*      */ 
/*      */   public abstract void setf(int paramInt1, int paramInt2, float paramFloat);
/*      */ 
/*      */   public abstract void setf(int paramInt, float paramFloat);
/*      */ 
/*      */   public int[][] getIntArray()
/*      */   {
/* 1569 */     int[][] a = new int[this.width][this.height];
/* 1570 */     for (int y = 0; y < this.height; y++) {
/* 1571 */       for (int x = 0; x < this.width; x++)
/* 1572 */         a[x][y] = get(x, y);
/*      */     }
/* 1574 */     return a;
/*      */   }
/*      */ 
/*      */   public void setIntArray(int[][] a)
/*      */   {
/* 1579 */     for (int y = 0; y < this.height; y++)
/* 1580 */       for (int x = 0; x < this.width; x++)
/* 1581 */         set(x, y, a[x][y]);
/*      */   }
/*      */ 
/*      */   public float[][] getFloatArray()
/*      */   {
/* 1588 */     float[][] a = new float[this.width][this.height];
/* 1589 */     for (int y = 0; y < this.height; y++) {
/* 1590 */       for (int x = 0; x < this.width; x++)
/* 1591 */         a[x][y] = getf(x, y);
/*      */     }
/* 1593 */     return a;
/*      */   }
/*      */ 
/*      */   public void setFloatArray(float[][] a)
/*      */   {
/* 1598 */     for (int y = 0; y < this.height; y++)
/* 1599 */       for (int x = 0; x < this.width; x++)
/* 1600 */         setf(x, y, a[x][y]);
/*      */   }
/*      */ 
/*      */   public void getNeighborhood(int x, int y, double[][] arr)
/*      */   {
/* 1606 */     int nx = arr.length;
/* 1607 */     int ny = arr[0].length;
/* 1608 */     int nx2 = (nx - 1) / 2;
/* 1609 */     int ny2 = (ny - 1) / 2;
/* 1610 */     if ((x >= nx2) && (y >= ny2) && (x < this.width - nx2 - 1) && (y < this.height - ny2 - 1)) {
/* 1611 */       int index = (y - ny2) * this.width + (x - nx2);
/* 1612 */       for (int y2 = 0; y2 < ny; y2++) {
/* 1613 */         for (int x2 = 0; x2 < nx; x2++)
/* 1614 */           arr[x2][y2] = getf(index++);
/* 1615 */         index += this.width - nx;
/*      */       }
/*      */     } else {
/* 1618 */       for (int y2 = 0; y2 < ny; y2++)
/* 1619 */         for (int x2 = 0; x2 < nx; x2++)
/* 1620 */           arr[x2][y2] = getPixelValue(x2, y2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int[] getPixel(int x, int y, int[] iArray)
/*      */   {
/* 1630 */     if (iArray == null) iArray = new int[1];
/* 1631 */     iArray[0] = getPixel(x, y);
/* 1632 */     return iArray;
/*      */   }
/*      */ 
/*      */   public void putPixel(int x, int y, int[] iArray)
/*      */   {
/* 1638 */     putPixel(x, y, iArray[0]);
/*      */   }
/*      */ 
/*      */   public abstract double getInterpolatedPixel(double paramDouble1, double paramDouble2);
/*      */ 
/*      */   public abstract int getPixelInterpolated(double paramDouble1, double paramDouble2);
/*      */ 
/*      */   public final double getInterpolatedValue(double x, double y)
/*      */   {
/* 1654 */     if (useBicubic)
/* 1655 */       return getBicubicInterpolatedPixel(x, y, this);
/* 1656 */     if ((x < 0.0D) || (x >= this.width - 1.0D) || (y < 0.0D) || (y >= this.height - 1.0D)) {
/* 1657 */       if ((x < -1.0D) || (x >= this.width) || (y < -1.0D) || (y >= this.height)) {
/* 1658 */         return 0.0D;
/*      */       }
/* 1660 */       return getInterpolatedEdgeValue(x, y);
/*      */     }
/* 1662 */     int xbase = (int)x;
/* 1663 */     int ybase = (int)y;
/* 1664 */     double xFraction = x - xbase;
/* 1665 */     double yFraction = y - ybase;
/* 1666 */     if (xFraction < 0.0D) xFraction = 0.0D;
/* 1667 */     if (yFraction < 0.0D) yFraction = 0.0D;
/* 1668 */     double lowerLeft = getPixelValue(xbase, ybase);
/* 1669 */     double lowerRight = getPixelValue(xbase + 1, ybase);
/* 1670 */     double upperRight = getPixelValue(xbase + 1, ybase + 1);
/* 1671 */     double upperLeft = getPixelValue(xbase, ybase + 1);
/* 1672 */     double upperAverage = upperLeft + xFraction * (upperRight - upperLeft);
/* 1673 */     double lowerAverage = lowerLeft + xFraction * (lowerRight - lowerLeft);
/* 1674 */     return lowerAverage + yFraction * (upperAverage - lowerAverage);
/*      */   }
/*      */ 
/*      */   public double getBicubicInterpolatedPixel(double x0, double y0, ImageProcessor ip2)
/*      */   {
/* 1681 */     int u0 = (int)Math.floor(x0);
/* 1682 */     int v0 = (int)Math.floor(y0);
/* 1683 */     if ((u0 <= 0) || (u0 >= this.width - 2) || (v0 <= 0) || (v0 >= this.height - 2))
/* 1684 */       return ip2.getBilinearInterpolatedPixel(x0, y0);
/* 1685 */     double q = 0.0D;
/* 1686 */     for (int j = 0; j <= 3; j++) {
/* 1687 */       int v = v0 - 1 + j;
/* 1688 */       double p = 0.0D;
/* 1689 */       for (int i = 0; i <= 3; i++) {
/* 1690 */         int u = u0 - 1 + i;
/* 1691 */         p += ip2.get(u, v) * cubic(x0 - u);
/*      */       }
/* 1693 */       q += p * cubic(y0 - v);
/*      */     }
/* 1695 */     return q;
/*      */   }
/*      */ 
/*      */   final double getBilinearInterpolatedPixel(double x, double y) {
/* 1699 */     if ((x >= -1.0D) && (x < this.width) && (y >= -1.0D) && (y < this.height)) {
/* 1700 */       int method = this.interpolationMethod;
/* 1701 */       this.interpolationMethod = 1;
/* 1702 */       double value = getInterpolatedPixel(x, y);
/* 1703 */       this.interpolationMethod = method;
/* 1704 */       return value;
/*      */     }
/* 1706 */     return getBackgroundValue();
/*      */   }
/*      */ 
/*      */   public static final double cubic(double x)
/*      */   {
/* 1711 */     if (x < 0.0D) x = -x;
/* 1712 */     double z = 0.0D;
/* 1713 */     if (x < 1.0D)
/* 1714 */       z = x * x * (x * 1.5D + -2.5D) + 1.0D;
/* 1715 */     else if (x < 2.0D)
/* 1716 */       z = -0.5D * x * x * x + 2.5D * x * x - 4.0D * x + 2.0D;
/* 1717 */     return z;
/*      */   }
/*      */ 
/*      */   private final double getInterpolatedEdgeValue(double x, double y)
/*      */   {
/* 1734 */     int xbase = (int)x;
/* 1735 */     int ybase = (int)y;
/* 1736 */     double xFraction = x - xbase;
/* 1737 */     double yFraction = y - ybase;
/* 1738 */     if (xFraction < 0.0D) xFraction = 0.0D;
/* 1739 */     if (yFraction < 0.0D) yFraction = 0.0D;
/* 1740 */     double lowerLeft = getEdgeValue(xbase, ybase);
/* 1741 */     double lowerRight = getEdgeValue(xbase + 1, ybase);
/* 1742 */     double upperRight = getEdgeValue(xbase + 1, ybase + 1);
/* 1743 */     double upperLeft = getEdgeValue(xbase, ybase + 1);
/* 1744 */     double upperAverage = upperLeft + xFraction * (upperRight - upperLeft);
/* 1745 */     double lowerAverage = lowerLeft + xFraction * (lowerRight - lowerLeft);
/* 1746 */     return lowerAverage + yFraction * (upperAverage - lowerAverage);
/*      */   }
/*      */ 
/*      */   private float getEdgeValue(int x, int y) {
/* 1750 */     if (x <= 0) x = 0;
/* 1751 */     if (x >= this.width) x = this.width - 1;
/* 1752 */     if (y <= 0) y = 0;
/* 1753 */     if (y >= this.height) y = this.height - 1;
/* 1754 */     return getPixelValue(x, y);
/*      */   }
/*      */ 
/*      */   public abstract void putPixel(int paramInt1, int paramInt2, int paramInt3);
/*      */ 
/*      */   public abstract float getPixelValue(int paramInt1, int paramInt2);
/*      */ 
/*      */   public abstract void putPixelValue(int paramInt1, int paramInt2, double paramDouble);
/*      */ 
/*      */   public abstract void drawPixel(int paramInt1, int paramInt2);
/*      */ 
/*      */   public abstract void setPixels(Object paramObject);
/*      */ 
/*      */   public abstract void copyBits(ImageProcessor paramImageProcessor, int paramInt1, int paramInt2, int paramInt3);
/*      */ 
/*      */   public abstract void applyTable(int[] paramArrayOfInt);
/*      */ 
/*      */   public void invert()
/*      */   {
/* 1793 */     process(0, 0.0D);
/*      */   }
/*      */   public void add(int value) {
/* 1796 */     process(2, value);
/*      */   }
/*      */   public void add(double value) {
/* 1799 */     process(2, value);
/*      */   }
/*      */ 
/*      */   public void subtract(double value) {
/* 1803 */     add(-value);
/*      */   }
/*      */ 
/*      */   public void multiply(double value) {
/* 1807 */     process(3, value);
/*      */   }
/*      */   public void and(int value) {
/* 1810 */     process(4, value);
/*      */   }
/*      */   public void or(int value) {
/* 1813 */     process(5, value);
/*      */   }
/*      */   public void xor(int value) {
/* 1816 */     process(6, value);
/*      */   }
/*      */   public void gamma(double value) {
/* 1819 */     process(7, value);
/*      */   }
/*      */   public void log() {
/* 1822 */     process(8, 0.0D);
/*      */   }
/*      */   public void exp() {
/* 1825 */     process(13, 0.0D);
/*      */   }
/*      */   public void sqr() {
/* 1828 */     process(11, 0.0D);
/*      */   }
/*      */   public void sqrt() {
/* 1831 */     process(12, 0.0D);
/*      */   }
/*      */ 
/*      */   public void abs() {
/*      */   }
/*      */ 
/*      */   public void min(double value) {
/* 1838 */     process(9, value);
/*      */   }
/*      */   public void max(double value) {
/* 1841 */     process(10, value);
/*      */   }
/*      */ 
/*      */   public abstract Image createImage();
/*      */ 
/*      */   public BufferedImage getBufferedImage()
/*      */   {
/* 1848 */     BufferedImage bi = new BufferedImage(this.width, this.height, 1);
/* 1849 */     Graphics2D g = (Graphics2D)bi.getGraphics();
/* 1850 */     g.drawImage(createImage(), 0, 0, null);
/* 1851 */     return bi;
/*      */   }
/*      */ 
/*      */   public abstract ImageProcessor createProcessor(int paramInt1, int paramInt2);
/*      */ 
/*      */   public abstract void snapshot();
/*      */ 
/*      */   public abstract void reset();
/*      */ 
/*      */   public abstract void swapPixelArrays();
/*      */ 
/*      */   public abstract void reset(ImageProcessor paramImageProcessor);
/*      */ 
/*      */   public abstract void setSnapshotPixels(Object paramObject);
/*      */ 
/*      */   public abstract Object getSnapshotPixels();
/*      */ 
/*      */   public abstract void convolve3x3(int[] paramArrayOfInt);
/*      */ 
/*      */   public abstract void filter(int paramInt);
/*      */ 
/*      */   public abstract void medianFilter();
/*      */ 
/*      */   public abstract void noise(double paramDouble);
/*      */ 
/*      */   public abstract ImageProcessor crop();
/*      */ 
/*      */   public abstract void threshold(int paramInt);
/*      */ 
/*      */   public abstract ImageProcessor duplicate();
/*      */ 
/*      */   public abstract void scale(double paramDouble1, double paramDouble2);
/*      */ 
/*      */   public abstract ImageProcessor resize(int paramInt1, int paramInt2);
/*      */ 
/*      */   public ImageProcessor resize(int dstWidth)
/*      */   {
/* 1922 */     return resize(dstWidth, (int)(dstWidth * (this.roiHeight / this.roiWidth)));
/*      */   }
/*      */ 
/*      */   public ImageProcessor resize(int dstWidth, int dstHeight, boolean useAverging)
/*      */   {
/* 1936 */     Rectangle r = getRoi();
/* 1937 */     int rWidth = r.width;
/* 1938 */     int rHeight = r.height;
/* 1939 */     if (((dstWidth >= rWidth) && (dstHeight >= rHeight)) || (!useAverging)) {
/* 1940 */       return resize(dstWidth, dstHeight);
/*      */     }
/* 1942 */     ImageProcessor ip2 = createProcessor(dstWidth, dstHeight);
/* 1943 */     FloatProcessor fp = null;
/* 1944 */     for (int channelNumber = 0; channelNumber < getNChannels(); channelNumber++) {
/* 1945 */       fp = toFloat(channelNumber, fp);
/* 1946 */       fp.setInterpolationMethod(this.interpolationMethod);
/* 1947 */       fp.setRoi(getRoi());
/* 1948 */       FloatProcessor fp2 = fp.downsize(dstWidth, dstHeight);
/* 1949 */       ip2.setPixels(channelNumber, fp2);
/*      */     }
/* 1951 */     return ip2;
/*      */   }
/*      */ 
/*      */   public abstract void rotate(double paramDouble);
/*      */ 
/*      */   public void translate(double xOffset, double yOffset)
/*      */   {
/* 1966 */     ImageProcessor ip2 = duplicate();
/* 1967 */     ip2.setBackgroundValue(0.0D);
/* 1968 */     boolean integerOffsets = (xOffset == (int)xOffset) && (yOffset == (int)yOffset);
/* 1969 */     if ((integerOffsets) || (this.interpolationMethod == 0)) {
/* 1970 */       for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/* 1971 */         for (int x = this.roiX; x < this.roiX + this.roiWidth; x++)
/* 1972 */           putPixel(x, y, ip2.getPixel(x - (int)xOffset, y - (int)yOffset));
/*      */       }
/*      */     }
/* 1975 */     else if ((this.interpolationMethod == 2) && ((this instanceof ColorProcessor))) {
/* 1976 */       ((ColorProcessor)this).filterRGB(9, xOffset, yOffset);
/*      */     } else {
/* 1978 */       for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/* 1979 */         if (y % 30 == 0) showProgress((y - this.roiY) / this.roiHeight);
/* 1980 */         for (int x = this.roiX; x < this.roiX + this.roiWidth; x++)
/* 1981 */           putPixel(x, y, ip2.getPixelInterpolated(x - xOffset, y - yOffset));
/*      */       }
/* 1983 */       showProgress(1.0D);
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void translate(int xOffset, int yOffset, boolean eraseBackground)
/*      */   {
/* 1993 */     translate(xOffset, yOffset);
/*      */   }
/*      */ 
/*      */   public abstract int[] getHistogram();
/*      */ 
/*      */   public abstract void erode();
/*      */ 
/*      */   public abstract void dilate();
/*      */ 
/*      */   public void setLutAnimation(boolean lutAnimation)
/*      */   {
/* 2011 */     this.lutAnimation = lutAnimation;
/* 2012 */     this.newPixels = true;
/* 2013 */     this.source = null;
/*      */   }
/*      */ 
/*      */   void resetPixels(Object pixels) {
/* 2017 */     if (pixels == null) {
/* 2018 */       if (this.img != null) {
/* 2019 */         this.img.flush();
/* 2020 */         this.img = null;
/*      */       }
/* 2022 */       this.source = null;
/*      */     }
/* 2024 */     this.newPixels = true;
/* 2025 */     this.source = null;
/*      */   }
/*      */ 
/*      */   public ImageProcessor convertToByte(boolean doScaling)
/*      */   {
/* 2030 */     TypeConverter tc = new TypeConverter(this, doScaling);
/* 2031 */     return tc.convertToByte();
/*      */   }
/*      */ 
/*      */   public ImageProcessor convertToShort(boolean doScaling)
/*      */   {
/* 2036 */     TypeConverter tc = new TypeConverter(this, doScaling);
/* 2037 */     return tc.convertToShort();
/*      */   }
/*      */ 
/*      */   public ImageProcessor convertToFloat()
/*      */   {
/* 2044 */     TypeConverter tc = new TypeConverter(this, false);
/* 2045 */     return tc.convertToFloat(this.cTable);
/*      */   }
/*      */ 
/*      */   public ImageProcessor convertToRGB()
/*      */   {
/* 2050 */     TypeConverter tc = new TypeConverter(this, true);
/* 2051 */     return tc.convertToRGB();
/*      */   }
/*      */ 
/*      */   public abstract void convolve(float[] paramArrayOfFloat, int paramInt1, int paramInt2);
/*      */ 
/*      */   public void autoThreshold()
/*      */   {
/* 2064 */     threshold(getAutoThreshold());
/*      */   }
/*      */ 
/*      */   public int getAutoThreshold()
/*      */   {
/* 2075 */     return getAutoThreshold(getHistogram());
/*      */   }
/*      */ 
/*      */   public int getAutoThreshold(int[] histogram)
/*      */   {
/* 2081 */     int maxValue = histogram.length - 1;
/*      */ 
/* 2084 */     int count0 = histogram[0];
/* 2085 */     histogram[0] = 0;
/* 2086 */     int countMax = histogram[maxValue];
/* 2087 */     histogram[maxValue] = 0;
/* 2088 */     int min = 0;
/* 2089 */     while ((histogram[min] == 0) && (min < maxValue))
/* 2090 */       min++;
/* 2091 */     int max = maxValue;
/* 2092 */     while ((histogram[max] == 0) && (max > 0))
/* 2093 */       max--;
/* 2094 */     if (min >= max) {
/* 2095 */       histogram[0] = count0; histogram[maxValue] = countMax;
/* 2096 */       int level = histogram.length / 2;
/* 2097 */       return level;
/*      */     }
/*      */ 
/* 2100 */     int movingIndex = min;
/* 2101 */     int inc = Math.max(max / 40, 1);
/*      */     double result;
/*      */     do
/*      */     {
/*      */       double sum4;
/*      */       double sum3;
/*      */       double sum2;
/* 2103 */       double sum1 = sum2 = sum3 = sum4 = 0.0D;
/* 2104 */       for (int i = min; i <= movingIndex; i++) {
/* 2105 */         sum1 += i * histogram[i];
/* 2106 */         sum2 += histogram[i];
/*      */       }
/* 2108 */       for (int i = movingIndex + 1; i <= max; i++) {
/* 2109 */         sum3 += i * histogram[i];
/* 2110 */         sum4 += histogram[i];
/*      */       }
/* 2112 */       result = (sum1 / sum2 + sum3 / sum4) / 2.0D;
/* 2113 */       movingIndex++;
/* 2114 */       if ((max > 255) && (movingIndex % inc == 0))
/* 2115 */         showProgress(movingIndex / max); 
/*      */     }
/* 2116 */     while ((movingIndex + 1 <= result) && (movingIndex < max - 1));
/*      */ 
/* 2118 */     showProgress(1.0D);
/* 2119 */     histogram[0] = count0; histogram[maxValue] = countMax;
/* 2120 */     int level = (int)Math.round(result);
/* 2121 */     return level;
/*      */   }
/*      */ 
/*      */   public void setClipRect(Rectangle clipRect)
/*      */   {
/* 2127 */     if (clipRect == null) {
/* 2128 */       this.clipXMin = 0;
/* 2129 */       this.clipXMax = (this.width - 1);
/* 2130 */       this.clipYMin = 0;
/* 2131 */       this.clipYMax = (this.height - 1);
/*      */     } else {
/* 2133 */       this.clipXMin = clipRect.x;
/* 2134 */       this.clipXMax = (clipRect.x + clipRect.width - 1);
/* 2135 */       this.clipYMin = clipRect.y;
/* 2136 */       this.clipYMax = (clipRect.y + clipRect.height - 1);
/* 2137 */       if (this.clipXMin < 0) this.clipXMin = 0;
/* 2138 */       if (this.clipXMax >= this.width) this.clipXMax = (this.width - 1);
/* 2139 */       if (this.clipYMin < 0) this.clipYMin = 0;
/* 2140 */       if (this.clipYMax >= this.height) this.clipYMax = (this.height - 1); 
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String maskSizeError(ImageProcessor mask)
/*      */   {
/* 2145 */     return "Mask size (" + mask.getWidth() + "x" + mask.getHeight() + ") != ROI size (" + this.roiWidth + "x" + this.roiHeight + ")";
/*      */   }
/*      */ 
/*      */   protected SampleModel getIndexSampleModel()
/*      */   {
/* 2150 */     if (this.sampleModel == null) {
/* 2151 */       IndexColorModel icm = getDefaultColorModel();
/* 2152 */       WritableRaster wr = icm.createCompatibleWritableRaster(1, 1);
/* 2153 */       this.sampleModel = wr.getSampleModel();
/* 2154 */       this.sampleModel = this.sampleModel.createCompatibleSampleModel(this.width, this.height);
/*      */     }
/* 2156 */     return this.sampleModel;
/*      */   }
/*      */ 
/*      */   public IndexColorModel getDefaultColorModel()
/*      */   {
/* 2161 */     if (defaultColorModel == null) {
/* 2162 */       byte[] r = new byte[256];
/* 2163 */       byte[] g = new byte[256];
/* 2164 */       byte[] b = new byte[256];
/* 2165 */       for (int i = 0; i < 256; i++) {
/* 2166 */         r[i] = ((byte)i);
/* 2167 */         g[i] = ((byte)i);
/* 2168 */         b[i] = ((byte)i);
/*      */       }
/* 2170 */       defaultColorModel = new IndexColorModel(8, 256, r, g, b);
/*      */     }
/* 2172 */     return defaultColorModel;
/*      */   }
/*      */ 
/*      */   public void setSnapshotCopyMode(boolean b)
/*      */   {
/* 2181 */     this.snapshotCopyMode = b;
/*      */   }
/*      */ 
/*      */   public int getNChannels()
/*      */   {
/* 2189 */     return 1;
/*      */   }
/*      */ 
/*      */   public abstract FloatProcessor toFloat(int paramInt, FloatProcessor paramFloatProcessor);
/*      */ 
/*      */   public abstract void setPixels(int paramInt, FloatProcessor paramFloatProcessor);
/*      */ 
/*      */   public double minValue()
/*      */   {
/* 2212 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public double maxValue()
/*      */   {
/* 2217 */     return 255.0D;
/*      */   }
/*      */ 
/*      */   public void updateComposite(int[] rgbPixels, int channel)
/*      */   {
/* 2223 */     int size = this.width * this.height;
/* 2224 */     if ((this.bytes == null) || (!this.lutAnimation))
/* 2225 */       this.bytes = create8BitImage();
/* 2226 */     if (this.cm == null)
/* 2227 */       makeDefaultColorModel();
/* 2228 */     if ((this.reds == null) || (this.cm != this.cm2))
/* 2229 */       updateLutBytes();
/* 2230 */     switch (channel) {
/*      */     case 1:
/* 2232 */       for (int i = 0; i < size; i++)
/* 2233 */         rgbPixels[i] = (rgbPixels[i] & 0xFF00FFFF | this.reds[(this.bytes[i] & 0xFF)]);
/* 2234 */       break;
/*      */     case 2:
/* 2236 */       for (int i = 0; i < size; i++)
/* 2237 */         rgbPixels[i] = (rgbPixels[i] & 0xFFFF00FF | this.greens[(this.bytes[i] & 0xFF)]);
/* 2238 */       break;
/*      */     case 3:
/* 2240 */       for (int i = 0; i < size; i++)
/* 2241 */         rgbPixels[i] = (rgbPixels[i] & 0xFFFFFF00 | this.blues[(this.bytes[i] & 0xFF)]);
/* 2242 */       break;
/*      */     case 4:
/* 2244 */       for (int i = 0; i < size; i++) {
/* 2245 */         int redValue = this.reds[(this.bytes[i] & 0xFF)];
/* 2246 */         int greenValue = this.greens[(this.bytes[i] & 0xFF)];
/* 2247 */         int blueValue = this.blues[(this.bytes[i] & 0xFF)];
/* 2248 */         rgbPixels[i] = (redValue | greenValue | blueValue);
/*      */       }
/* 2250 */       break;
/*      */     case 5:
/* 2253 */       for (int i = 0; i < size; i++) {
/* 2254 */         int pixel = rgbPixels[i];
/* 2255 */         int redValue = (pixel & 0xFF0000) + this.reds[(this.bytes[i] & 0xFF)];
/* 2256 */         int greenValue = (pixel & 0xFF00) + this.greens[(this.bytes[i] & 0xFF)];
/* 2257 */         int blueValue = (pixel & 0xFF) + this.blues[(this.bytes[i] & 0xFF)];
/* 2258 */         if (redValue > 16711680) redValue = 16711680;
/* 2259 */         if (greenValue > 65280) greenValue = 65280;
/* 2260 */         if (blueValue > 255) blueValue = 255;
/* 2261 */         rgbPixels[i] = (redValue | greenValue | blueValue);
/*      */       }
/*      */     }
/*      */ 
/* 2265 */     this.lutAnimation = false;
/*      */   }
/*      */ 
/*      */   byte[] create8BitImage() {
/* 2269 */     return null;
/*      */   }
/*      */ 
/*      */   void updateLutBytes()
/*      */   {
/* 2274 */     IndexColorModel icm = (IndexColorModel)this.cm;
/* 2275 */     int mapSize = icm.getMapSize();
/* 2276 */     if ((this.reds == null) || (this.reds.length != mapSize)) {
/* 2277 */       this.reds = new int[mapSize];
/* 2278 */       this.greens = new int[mapSize];
/* 2279 */       this.blues = new int[mapSize];
/*      */     }
/* 2281 */     byte[] tmp = new byte[mapSize];
/* 2282 */     icm.getReds(tmp);
/* 2283 */     for (int i = 0; i < mapSize; i++) this.reds[i] = ((tmp[i] & 0xFF) << 16);
/* 2284 */     icm.getGreens(tmp);
/* 2285 */     for (int i = 0; i < mapSize; i++) this.greens[i] = ((tmp[i] & 0xFF) << 8);
/* 2286 */     icm.getBlues(tmp);
/* 2287 */     for (int i = 0; i < mapSize; i++) this.blues[i] = (tmp[i] & 0xFF);
/* 2288 */     this.cm2 = this.cm;
/*      */   }
/*      */ 
/*      */   public static void setOverColor(int red, int green, int blue)
/*      */   {
/* 2294 */     overRed = red; overGreen = green; overBlue = blue;
/*      */   }
/*      */ 
/*      */   public static void setUnderColor(int red, int green, int blue)
/*      */   {
/* 2299 */     underRed = red; underGreen = green; underBlue = blue;
/*      */   }
/*      */ 
/*      */   public boolean isBinary()
/*      */   {
/* 2304 */     return false;
/*      */   }
/*      */ 
/*      */   public static void setUseBicubic(boolean b)
/*      */   {
/* 2309 */     useBicubic = b;
/*      */   }
/*      */ 
/*      */   public ImageStatistics getStatistics()
/*      */   {
/* 2316 */     return ImageStatistics.getStatistics(this, 127, null);
/*      */   }
/*      */ 
/*      */   public int getSliceNumber()
/*      */   {
/* 2321 */     if (this.sliceNumber < 1) {
/* 2322 */       return 1;
/*      */     }
/* 2324 */     return this.sliceNumber;
/*      */   }
/*      */ 
/*      */   public void setSliceNumber(int slice)
/*      */   {
/* 2329 */     this.sliceNumber = slice;
/*      */   }
/*      */ 
/*      */   public synchronized Object clone()
/*      */   {
/*      */     try {
/* 2335 */       return super.clone(); } catch (CloneNotSupportedException e) {
/*      */     }
/* 2337 */     return null;
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ImageProcessor
 * JD-Core Version:    0.6.2
 */