/*      */ package ij.plugin.filter;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.ImageStack;
/*      */ import ij.Prefs;
/*      */ import ij.WindowManager;
/*      */ import ij.gui.DialogListener;
/*      */ import ij.gui.GenericDialog;
/*      */ import ij.gui.PointRoi;
/*      */ import ij.gui.PolygonRoi;
/*      */ import ij.gui.Roi;
/*      */ import ij.gui.Wand;
/*      */ import ij.measure.Calibration;
/*      */ import ij.measure.ResultsTable;
/*      */ import ij.process.ByteProcessor;
/*      */ import ij.process.FloatProcessor;
/*      */ import ij.process.ImageProcessor;
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.Checkbox;
/*      */ import java.awt.Label;
/*      */ import java.awt.Rectangle;
/*      */ import java.util.Arrays;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class MaximumFinder
/*      */   implements ExtendedPlugInFilter, DialogListener
/*      */ {
/*   43 */   private static double tolerance = 10.0D;
/*      */   public static final int SINGLE_POINTS = 0;
/*      */   public static final int IN_TOLERANCE = 1;
/*      */   public static final int SEGMENTED = 2;
/*      */   public static final int POINT_SELECTION = 3;
/*      */   public static final int LIST = 4;
/*      */   public static final int COUNT = 5;
/*      */   private static int outputType;
/*   59 */   private static int dialogOutputType = 3;
/*      */ 
/*   61 */   static final String[] outputTypeNames = { "Single Points", "Maxima Within Tolerance", "Segmented Particles", "Point Selection", "List", "Count" };
/*      */   private static boolean excludeOnEdges;
/*      */   private static boolean useMinThreshold;
/*      */   private static boolean lightBackground;
/*      */   private ImagePlus imp;
/*   70 */   private int flags = 415;
/*      */   private boolean thresholded;
/*      */   private boolean roiSaved;
/*      */   private boolean previewing;
/*      */   private Vector checkboxes;
/*   75 */   private boolean thresholdWarningShown = false;
/*      */   private Label messageArea;
/*      */   private double progressDone;
/*   78 */   private int nPasses = 0;
/*      */   private int width;
/*      */   private int height;
/*      */   private int intEncodeXMask;
/*      */   private int intEncodeYMask;
/*      */   private int intEncodeShift;
/*      */   private int[] dirOffset;
/*   86 */   static final int[] DIR_X_OFFSET = { 0, 1, 1, 1, 0, -1, -1, -1 };
/*   87 */   static final int[] DIR_Y_OFFSET = { -1, -1, 0, 1, 1, 1, 0, -1 };
/*      */   static final byte MAXIMUM = 1;
/*      */   static final byte LISTED = 2;
/*      */   static final byte PROCESSED = 4;
/*      */   static final byte MAX_AREA = 8;
/*      */   static final byte EQUAL = 16;
/*      */   static final byte MAX_POINT = 32;
/*      */   static final byte ELIMINATED = 64;
/*   97 */   static final byte[] outputTypeMasks = { 32, 8, 8 };
/*      */   static final float SQRT2 = 1.414214F;
/*      */ 
/*      */   public int setup(String arg, ImagePlus imp)
/*      */   {
/*  108 */     this.imp = imp;
/*  109 */     return this.flags;
/*      */   }
/*      */ 
/*      */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
/*  113 */     ImageProcessor ip = imp.getProcessor();
/*  114 */     ip.resetBinaryThreshold();
/*  115 */     this.thresholded = (ip.getMinThreshold() != -808080.0D);
/*  116 */     GenericDialog gd = new GenericDialog(command);
/*  117 */     int digits = (ip instanceof FloatProcessor) ? 2 : 0;
/*  118 */     String unit = imp.getCalibration() != null ? imp.getCalibration().getValueUnit() : null;
/*  119 */     unit = " (" + unit + "):";
/*  120 */     gd.addNumericField("Noise tolerance" + unit, tolerance, digits);
/*  121 */     gd.addChoice("Output type:", outputTypeNames, outputTypeNames[dialogOutputType]);
/*  122 */     gd.addCheckbox("Exclude edge maxima", excludeOnEdges);
/*  123 */     if (this.thresholded)
/*  124 */       gd.addCheckbox("Above lower threshold", useMinThreshold);
/*  125 */     gd.addCheckbox("Light background", lightBackground);
/*  126 */     gd.addPreviewCheckbox(pfr, "Preview point selection");
/*  127 */     gd.addMessage("    ");
/*  128 */     this.messageArea = ((Label)gd.getMessage());
/*  129 */     gd.addDialogListener(this);
/*  130 */     this.checkboxes = gd.getCheckboxes();
/*  131 */     this.previewing = true;
/*  132 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#find-maxima");
/*  133 */     gd.showDialog();
/*  134 */     if (gd.wasCanceled())
/*  135 */       return 4096;
/*  136 */     this.previewing = false;
/*  137 */     if (!dialogItemChanged(gd, null))
/*  138 */       return 4096;
/*  139 */     IJ.register(getClass());
/*  140 */     return this.flags;
/*      */   }
/*      */ 
/*      */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
/*      */   {
/*  145 */     tolerance = gd.getNextNumber();
/*  146 */     if (tolerance < 0.0D) tolerance = 0.0D;
/*  147 */     dialogOutputType = gd.getNextChoiceIndex();
/*  148 */     outputType = this.previewing ? 3 : dialogOutputType;
/*  149 */     excludeOnEdges = gd.getNextBoolean();
/*  150 */     if (this.thresholded)
/*  151 */       useMinThreshold = gd.getNextBoolean();
/*      */     else
/*  153 */       useMinThreshold = false;
/*  154 */     lightBackground = gd.getNextBoolean();
/*  155 */     boolean invertedLut = this.imp.isInvertedLut();
/*  156 */     if ((useMinThreshold) && (((invertedLut) && (!lightBackground)) || ((!invertedLut) && (lightBackground)))) {
/*  157 */       if ((!this.thresholdWarningShown) && 
/*  158 */         (!IJ.showMessageWithCancel("Find Maxima", "\"Above Lower Threshold\" option cannot be used\nwhen finding minima (image with light background\nor image with dark background and inverting LUT).")) && (!this.previewing))
/*      */       {
/*  164 */         return false;
/*  165 */       }this.thresholdWarningShown = true;
/*  166 */       useMinThreshold = false;
/*  167 */       ((Checkbox)this.checkboxes.elementAt(1)).setState(false);
/*      */     }
/*  169 */     if (!gd.getPreviewCheckbox().getState())
/*  170 */       this.messageArea.setText("");
/*  171 */     return !gd.invalidNumber();
/*      */   }
/*      */ 
/*      */   public void setNPasses(int nPasses)
/*      */   {
/*  177 */     this.nPasses = nPasses;
/*      */   }
/*      */ 
/*      */   public void run(ImageProcessor ip)
/*      */   {
/*  184 */     Roi roi = this.imp.getRoi();
/*  185 */     if ((outputType == 3) && (!this.roiSaved)) {
/*  186 */       this.imp.saveRoi();
/*  187 */       this.roiSaved = true;
/*      */     }
/*  189 */     if ((roi != null) && ((!roi.isArea()) || (outputType == 2))) {
/*  190 */       this.imp.killRoi();
/*  191 */       roi = null;
/*      */     }
/*  193 */     boolean invertedLut = this.imp.isInvertedLut();
/*  194 */     double threshold = useMinThreshold ? ip.getMinThreshold() : -808080.0D;
/*  195 */     if (((invertedLut) && (!lightBackground)) || ((!invertedLut) && (lightBackground))) {
/*  196 */       threshold = -808080.0D;
/*  197 */       float[] cTable = ip.getCalibrationTable();
/*  198 */       ip = ip.duplicate();
/*  199 */       if (cTable == null) {
/*  200 */         ip.invert();
/*      */       } else {
/*  202 */         float[] invertedCTable = new float[cTable.length];
/*  203 */         for (int i = cTable.length - 1; i >= 0; i--)
/*  204 */           invertedCTable[i] = (-cTable[i]);
/*  205 */         ip.setCalibrationTable(invertedCTable);
/*      */       }
/*  207 */       ip.setRoi(roi);
/*      */     }
/*  209 */     ByteProcessor outIp = null;
/*  210 */     outIp = findMaxima(ip, tolerance, threshold, outputType, excludeOnEdges, false);
/*  211 */     if (outIp == null) return;
/*  212 */     if (!Prefs.blackBackground)
/*  213 */       outIp.invertLut();
/*      */     String resultName;
/*      */     String resultName;
/*  215 */     if (outputType == 2)
/*  216 */       resultName = " Segmented";
/*      */     else
/*  218 */       resultName = " Maxima";
/*  219 */     String outname = this.imp.getTitle();
/*  220 */     if (this.imp.getNSlices() > 1)
/*  221 */       outname = outname + "(" + this.imp.getCurrentSlice() + ")";
/*  222 */     outname = outname + resultName;
/*  223 */     if (WindowManager.getImage(outname) != null)
/*  224 */       outname = WindowManager.getUniqueName(outname);
/*  225 */     ImagePlus maxImp = new ImagePlus(outname, outIp);
/*  226 */     Calibration cal = this.imp.getCalibration().copy();
/*  227 */     cal.disableDensityCalibration();
/*  228 */     maxImp.setCalibration(cal);
/*  229 */     maxImp.show();
/*      */   }
/*      */ 
/*      */   public ByteProcessor findMaxima(ImageProcessor ip, double tolerance, double threshold, int outputType, boolean excludeOnEdges, boolean isEDM)
/*      */   {
/*  248 */     if (this.dirOffset == null) makeDirectionOffsets(ip);
/*  249 */     Rectangle roi = ip.getRoi();
/*  250 */     byte[] mask = ip.getMaskArray();
/*  251 */     if ((threshold != -808080.0D) && (ip.getCalibrationTable() != null) && (threshold > 0.0D) && (threshold < ip.getCalibrationTable().length))
/*      */     {
/*  253 */       threshold = ip.getCalibrationTable()[((int)threshold)];
/*  254 */     }ByteProcessor typeP = new ByteProcessor(this.width, this.height);
/*  255 */     byte[] types = (byte[])typeP.getPixels();
/*  256 */     float globalMin = 3.4028235E+38F;
/*  257 */     float globalMax = -3.402824E+38F;
/*  258 */     for (int y = roi.y; y < roi.y + roi.height; y++) {
/*  259 */       for (int x = roi.x; x < roi.x + roi.width; x++) {
/*  260 */         float v = ip.getPixelValue(x, y);
/*  261 */         if (globalMin > v) globalMin = v;
/*  262 */         if (globalMax < v) globalMax = v;
/*      */       }
/*      */     }
/*  265 */     if (threshold != -808080.0D) {
/*  266 */       threshold -= (globalMax - globalMin) * 1.0E-06D;
/*      */     }
/*  268 */     boolean excludeEdgesNow = (excludeOnEdges) && (outputType != 2);
/*      */ 
/*  270 */     if (Thread.currentThread().isInterrupted()) return null;
/*  271 */     IJ.showStatus("Getting sorted maxima...");
/*  272 */     long[] maxPoints = getSortedMaxPoints(ip, typeP, excludeEdgesNow, isEDM, globalMin, globalMax, threshold);
/*  273 */     if (Thread.currentThread().isInterrupted()) return null;
/*  274 */     IJ.showStatus("Analyzing  maxima...");
/*  275 */     float maxSortingError = 0.0F;
/*  276 */     if ((ip instanceof FloatProcessor))
/*  277 */       maxSortingError = 1.1F * (isEDM ? 0.7071068F : (globalMax - globalMin) / 2.0E+09F);
/*  278 */     analyzeAndMarkMaxima(ip, typeP, maxPoints, excludeEdgesNow, isEDM, globalMin, tolerance, outputType, maxSortingError);
/*      */ 
/*  280 */     if ((outputType == 3) || (outputType == 4) || (outputType == 5))
/*  281 */       return null;
/*      */     ByteProcessor outIp;
/*  285 */     if (outputType == 2)
/*      */     {
/*  288 */       ByteProcessor outIp = make8bit(ip, typeP, isEDM, globalMin, globalMax, threshold);
/*      */ 
/*  290 */       cleanupMaxima(outIp, typeP, maxPoints);
/*      */ 
/*  293 */       if (!watershedSegment(outIp))
/*  294 */         return null;
/*  295 */       if (!isEDM) cleanupExtraLines(outIp);
/*  296 */       watershedPostProcess(outIp);
/*  297 */       if (excludeOnEdges) deleteEdgeParticles(outIp, typeP); 
/*      */     }
/*  299 */     else { for (int i = 0; i < this.width * this.height; i++)
/*  300 */         types[i] = ((byte)((types[i] & outputTypeMasks[outputType]) != 0 ? 'ÿ' : 0));
/*  301 */       outIp = typeP;
/*      */     }
/*  303 */     byte[] outPixels = (byte[])outIp.getPixels();
/*      */ 
/*  305 */     if (roi != null) {
/*  306 */       int y = 0; for (int i = 0; y < outIp.getHeight(); y++) {
/*  307 */         for (int x = 0; x < outIp.getWidth(); i++) {
/*  308 */           if ((x < roi.x) || (x >= roi.x + roi.width) || (y < roi.y) || (y >= roi.y + roi.height)) outPixels[i] = 0;
/*  309 */           else if ((mask != null) && (mask[(x - roi.x + roi.width * (y - roi.y))] == 0)) outPixels[i] = 0;
/*  307 */           x++;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  314 */     return outIp;
/*      */   }
/*      */ 
/*      */   long[] getSortedMaxPoints(ImageProcessor ip, ByteProcessor typeP, boolean excludeEdgesNow, boolean isEDM, float globalMin, float globalMax, double threshold)
/*      */   {
/*  331 */     Rectangle roi = ip.getRoi();
/*  332 */     byte[] types = (byte[])typeP.getPixels();
/*  333 */     int nMax = 0;
/*  334 */     boolean checkThreshold = threshold != -808080.0D;
/*  335 */     Thread thread = Thread.currentThread();
/*      */ 
/*  337 */     for (int y = roi.y; y < roi.y + roi.height; y++) {
/*  338 */       if ((y % 50 == 0) && (thread.isInterrupted())) return null;
/*  339 */       int x = roi.x; for (int i = x + y * this.width; x < roi.x + roi.width; i++) {
/*  340 */         float v = ip.getPixelValue(x, y);
/*  341 */         float vTrue = isEDM ? trueEdmHeight(x, y, ip) : v;
/*  342 */         if ((v != globalMin) && 
/*  343 */           ((!excludeEdgesNow) || ((x != 0) && (x != this.width - 1) && (y != 0) && (y != this.height - 1))) && (
/*  344 */           (!checkThreshold) || (v >= threshold))) {
/*  345 */           boolean isMax = true;
/*      */ 
/*  349 */           boolean isInner = (y != 0) && (y != this.height - 1) && (x != 0) && (x != this.width - 1);
/*  350 */           for (int d = 0; d < 8; d++) {
/*  351 */             if ((isInner) || (isWithin(x, y, d))) {
/*  352 */               float vNeighbor = ip.getPixelValue(x + DIR_X_OFFSET[d], y + DIR_Y_OFFSET[d]);
/*  353 */               float vNeighborTrue = isEDM ? trueEdmHeight(x + DIR_X_OFFSET[d], y + DIR_Y_OFFSET[d], ip) : vNeighbor;
/*  354 */               if ((vNeighbor > v) && (vNeighborTrue > vTrue)) {
/*  355 */                 isMax = false;
/*  356 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*  360 */           if (isMax) {
/*  361 */             types[i] = 1;
/*  362 */             nMax++;
/*      */           }
/*      */         }
/*  339 */         x++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  366 */     if (thread.isInterrupted()) return null;
/*      */ 
/*  369 */     float vFactor = (float)(2000000000.0D / (globalMax - globalMin));
/*  370 */     long[] maxPoints = new long[nMax];
/*  371 */     int iMax = 0;
/*  372 */     for (int y = roi.y; y < roi.y + roi.height; y++) {
/*  373 */       int x = roi.x; for (int p = x + y * this.width; x < roi.x + roi.width; p++) {
/*  374 */         if (types[p] == 1) {
/*  375 */           float fValue = isEDM ? trueEdmHeight(x, y, ip) : ip.getPixelValue(x, y);
/*  376 */           int iValue = (int)((fValue - globalMin) * vFactor);
/*  377 */           maxPoints[(iMax++)] = (iValue << 32 | p);
/*      */         }
/*  373 */         x++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  380 */     if (thread.isInterrupted()) return null;
/*  381 */     Arrays.sort(maxPoints);
/*      */ 
/*  383 */     return maxPoints;
/*      */   }
/*      */ 
/*      */   void analyzeAndMarkMaxima(ImageProcessor ip, ByteProcessor typeP, long[] maxPoints, boolean excludeEdgesNow, boolean isEDM, float globalMin, double tolerance, int outputType, float maxSortingError)
/*      */   {
/*  402 */     byte[] types = (byte[])typeP.getPixels();
/*  403 */     int nMax = maxPoints.length;
/*  404 */     int[] pList = new int[this.width * this.height];
/*  405 */     Vector xyVector = null;
/*  406 */     Roi roi = null;
/*  407 */     boolean displayOrCount = (outputType == 3) || (outputType == 4) || (outputType == 5);
/*  408 */     if (displayOrCount)
/*  409 */       xyVector = new Vector();
/*  410 */     if (this.imp != null) {
/*  411 */       roi = this.imp.getRoi();
/*      */     }
/*  413 */     for (int iMax = nMax - 1; iMax >= 0; iMax--) {
/*  414 */       if ((iMax % 100 == 0) && (Thread.currentThread().isInterrupted())) return;
/*  415 */       int offset0 = (int)maxPoints[iMax];
/*      */ 
/*  417 */       if ((types[offset0] & 0x4) == 0) {
/*  420 */         int x0 = offset0 % this.width;
/*  421 */         int y0 = offset0 / this.width;
/*  422 */         float v0 = isEDM ? trueEdmHeight(x0, y0, ip) : ip.getPixelValue(x0, y0);
/*      */         boolean sortingError;
/*      */         do { pList[0] = offset0;
/*      */           int tmp194_192 = offset0;
/*      */           byte[] tmp194_190 = types; tmp194_190[tmp194_192] = ((byte)(tmp194_190[tmp194_192] | 0x12));
/*  427 */           int listLen = 1;
/*  428 */           int listI = 0;
/*  429 */           boolean isEdgeMaximum = (x0 == 0) || (x0 == this.width - 1) || (y0 == 0) || (y0 == this.height - 1);
/*  430 */           sortingError = false;
/*  431 */           boolean maxPossible = true;
/*  432 */           double xEqual = x0;
/*  433 */           double yEqual = y0;
/*  434 */           int nEqual = 1;
/*      */           do {
/*  436 */             int offset = pList[listI];
/*  437 */             int x = offset % this.width;
/*  438 */             int y = offset / this.width;
/*      */ 
/*  440 */             boolean isInner = (y != 0) && (y != this.height - 1) && (x != 0) && (x != this.width - 1);
/*  441 */             for (int d = 0; d < 8; d++) {
/*  442 */               int offset2 = offset + this.dirOffset[d];
/*  443 */               if (((isInner) || (isWithin(x, y, d))) && ((types[offset2] & 0x2) == 0)) {
/*  444 */                 if ((types[offset2] & 0x4) != 0) {
/*  445 */                   maxPossible = false;
/*      */ 
/*  447 */                   break;
/*      */                 }
/*  449 */                 int x2 = x + DIR_X_OFFSET[d];
/*  450 */                 int y2 = y + DIR_Y_OFFSET[d];
/*  451 */                 float v2 = isEDM ? trueEdmHeight(x2, y2, ip) : ip.getPixelValue(x2, y2);
/*  452 */                 if (v2 > v0 + maxSortingError) {
/*  453 */                   maxPossible = false;
/*      */ 
/*  455 */                   break;
/*  456 */                 }if (v2 >= v0 - (float)tolerance) {
/*  457 */                   if (v2 > v0) {
/*  458 */                     sortingError = true;
/*  459 */                     offset0 = offset2;
/*  460 */                     v0 = v2;
/*  461 */                     x0 = x2;
/*  462 */                     y0 = y2;
/*      */                   }
/*      */ 
/*  465 */                   pList[listLen] = offset2;
/*  466 */                   listLen++;
/*      */                   int tmp514_512 = offset2;
/*      */                   byte[] tmp514_510 = types; tmp514_510[tmp514_512] = ((byte)(tmp514_510[tmp514_512] | 0x2));
/*  468 */                   if ((x2 == 0) || (x2 == this.width - 1) || (y2 == 0) || (y2 == this.height - 1)) {
/*  469 */                     isEdgeMaximum = true;
/*  470 */                     if (excludeEdgesNow) {
/*  471 */                       maxPossible = false;
/*  472 */                       break;
/*      */                     }
/*      */                   }
/*  475 */                   if (v2 == v0)
/*      */                   {
/*      */                     int tmp578_576 = offset2;
/*      */                     byte[] tmp578_574 = types; tmp578_574[tmp578_576] = ((byte)(tmp578_574[tmp578_576] | 0x10));
/*  477 */                     xEqual += x2;
/*  478 */                     yEqual += y2;
/*  479 */                     nEqual++;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*  484 */             listI++;
/*  485 */           }while (listI < listLen);
/*      */ 
/*  487 */           if (sortingError) {
/*  488 */             for (listI = 0; listI < listLen; listI++)
/*  489 */               types[pList[listI]] = 0;
/*      */           }
/*  491 */           int resetMask = (maxPossible ? 2 : 18) ^ 0xFFFFFFFF;
/*  492 */           xEqual /= nEqual;
/*  493 */           yEqual /= nEqual;
/*  494 */           double minDist2 = 1.0E+20D;
/*  495 */           int nearestI = 0;
/*  496 */           for (listI = 0; listI < listLen; listI++) {
/*  497 */             int offset = pList[listI];
/*  498 */             int x = offset % this.width;
/*  499 */             int y = offset / this.width;
/*      */             int tmp728_726 = offset;
/*      */             byte[] tmp728_724 = types; tmp728_724[tmp728_726] = ((byte)(tmp728_724[tmp728_726] & resetMask));
/*      */             int tmp739_737 = offset;
/*      */             byte[] tmp739_735 = types; tmp739_735[tmp739_737] = ((byte)(tmp739_735[tmp739_737] | 0x4));
/*  502 */             if (maxPossible)
/*      */             {
/*      */               int tmp754_752 = offset;
/*      */               byte[] tmp754_750 = types; tmp754_750[tmp754_752] = ((byte)(tmp754_750[tmp754_752] | 0x8));
/*  504 */               if ((types[offset] & 0x10) != 0) {
/*  505 */                 double dist2 = (xEqual - x) * (xEqual - x) + (yEqual - y) * (yEqual - y);
/*  506 */                 if (dist2 < minDist2) {
/*  507 */                   minDist2 = dist2;
/*  508 */                   nearestI = listI;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*  513 */           if (maxPossible) {
/*  514 */             int offset = pList[nearestI];
/*      */             int tmp839_837 = offset;
/*      */             byte[] tmp839_835 = types; tmp839_835[tmp839_837] = ((byte)(tmp839_835[tmp839_837] | 0x20));
/*  516 */             if ((displayOrCount) && ((!excludeOnEdges) || (!isEdgeMaximum))) {
/*  517 */               int x = offset % this.width;
/*  518 */               int y = offset / this.width;
/*  519 */               if ((roi == null) || (roi.contains(x, y)))
/*  520 */                 xyVector.addElement(new int[] { x, y });
/*      */             }
/*      */           }
/*      */         }
/*  524 */         while (sortingError);
/*      */       }
/*      */     }
/*  527 */     if (Thread.currentThread().isInterrupted()) return;
/*  528 */     if ((displayOrCount) && (xyVector != null)) {
/*  529 */       int npoints = xyVector.size();
/*  530 */       if ((outputType == 3) && (npoints > 0) && (this.imp != null)) {
/*  531 */         int[] xpoints = new int[npoints];
/*  532 */         int[] ypoints = new int[npoints];
/*  533 */         for (int i = 0; i < npoints; i++) {
/*  534 */           int[] xy = (int[])xyVector.elementAt(i);
/*  535 */           xpoints[i] = xy[0];
/*  536 */           ypoints[i] = xy[1];
/*      */         }
/*  538 */         Roi points = new PointRoi(xpoints, ypoints, npoints);
/*  539 */         ((PointRoi)points).setHideLabels(true);
/*  540 */         this.imp.setRoi(points);
/*  541 */       } else if (outputType == 4) {
/*  542 */         Analyzer.resetCounter();
/*  543 */         ResultsTable rt = ResultsTable.getResultsTable();
/*  544 */         for (int i = 0; i < npoints; i++) {
/*  545 */           int[] xy = (int[])xyVector.elementAt(i);
/*  546 */           rt.incrementCounter();
/*  547 */           rt.addValue("X", xy[0]);
/*  548 */           rt.addValue("Y", xy[1]);
/*      */         }
/*  550 */         rt.show("Results");
/*  551 */       } else if (outputType == 5) {
/*  552 */         ResultsTable rt = ResultsTable.getResultsTable();
/*  553 */         rt.incrementCounter();
/*  554 */         rt.setValue("Count", rt.getCounter() - 1, npoints);
/*  555 */         rt.show("Results");
/*      */       }
/*      */     }
/*  558 */     if (this.previewing)
/*  559 */       this.messageArea.setText((xyVector == null ? 0 : xyVector.size()) + " Maxima");
/*      */   }
/*      */ 
/*      */   ByteProcessor make8bit(ImageProcessor ip, ByteProcessor typeP, boolean isEDM, float globalMin, float globalMax, double threshold)
/*      */   {
/*  573 */     byte[] types = (byte[])typeP.getPixels();
/*      */     double minValue;
/*      */     double minValue;
/*  575 */     if (isEDM) {
/*  576 */       threshold = 0.5D;
/*  577 */       minValue = 1.0D;
/*      */     } else {
/*  579 */       minValue = threshold == -808080.0D ? globalMin : threshold;
/*  580 */     }double offset = minValue - (globalMax - minValue) * 0.001975284584980237D;
/*  581 */     double factor = 253.0D / (globalMax - minValue);
/*      */ 
/*  583 */     if ((isEDM) && (factor > 1.0D))
/*  584 */       factor = 1.0D;
/*  585 */     ByteProcessor outIp = new ByteProcessor(this.width, this.height);
/*      */ 
/*  587 */     byte[] pixels = (byte[])outIp.getPixels();
/*      */ 
/*  589 */     int y = 0; for (int i = 0; y < this.height; y++) {
/*  590 */       for (int x = 0; x < this.width; i++) {
/*  591 */         float rawValue = ip.getPixelValue(x, y);
/*  592 */         if ((threshold != -808080.0D) && (rawValue < threshold)) {
/*  593 */           pixels[i] = 0;
/*  594 */         } else if ((types[i] & 0x8) != 0) {
/*  595 */           pixels[i] = -1;
/*      */         } else {
/*  597 */           long v = 1L + Math.round((rawValue - offset) * factor);
/*  598 */           if (v < 1L) pixels[i] = 1;
/*  599 */           else if (v <= 254L) pixels[i] = ((byte)(int)(v & 0xFF)); else
/*  600 */             pixels[i] = -2;
/*      */         }
/*  590 */         x++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  604 */     return outIp;
/*      */   }
/*      */ 
/*      */   float trueEdmHeight(int x, int y, ImageProcessor ip)
/*      */   {
/*  616 */     int xmax = this.width - 1;
/*  617 */     int ymax = ip.getHeight() - 1;
/*  618 */     float[] pixels = (float[])ip.getPixels();
/*  619 */     int offset = x + y * this.width;
/*  620 */     float v = pixels[offset];
/*  621 */     if ((x == 0) || (y == 0) || (x == xmax) || (y == ymax) || (v == 0.0F)) {
/*  622 */       return v;
/*      */     }
/*  624 */     float trueH = v + 0.7071068F;
/*  625 */     boolean ridgeOrMax = false;
/*  626 */     for (int d = 0; d < 4; d++) {
/*  627 */       int d2 = (d + 4) % 8;
/*  628 */       float v1 = pixels[(offset + this.dirOffset[d])];
/*  629 */       float v2 = pixels[(offset + this.dirOffset[d2])];
/*      */       float h;
/*      */       float h;
/*  631 */       if ((v >= v1) && (v >= v2)) {
/*  632 */         ridgeOrMax = true;
/*  633 */         h = (v1 + v2) / 2.0F;
/*      */       } else {
/*  635 */         h = Math.min(v1, v2);
/*      */       }
/*  637 */       h += (d % 2 == 0 ? 1.0F : 1.414214F);
/*  638 */       if (trueH > h) trueH = h;
/*      */     }
/*  640 */     if (!ridgeOrMax) trueH = v;
/*  641 */     return trueH;
/*      */   }
/*      */ 
/*      */   void cleanupMaxima(ByteProcessor outIp, ByteProcessor typeP, long[] maxPoints)
/*      */   {
/*  654 */     byte[] pixels = (byte[])outIp.getPixels();
/*  655 */     byte[] types = (byte[])typeP.getPixels();
/*  656 */     int nMax = maxPoints.length;
/*  657 */     int[] pList = new int[this.width * this.height];
/*  658 */     for (int iMax = nMax - 1; iMax >= 0; iMax--) {
/*  659 */       int offset0 = (int)maxPoints[iMax];
/*  660 */       if ((types[offset0] & 0x48) == 0) {
/*  661 */         int level = pixels[offset0] & 0xFF;
/*  662 */         int loLevel = level + 1;
/*  663 */         pList[0] = offset0;
/*      */         int tmp100_98 = offset0;
/*      */         byte[] tmp100_96 = types; tmp100_96[tmp100_98] = ((byte)(tmp100_96[tmp100_98] | 0x2));
/*  666 */         int listLen = 1;
/*  667 */         int lastLen = 1;
/*  668 */         int listI = 0;
/*  669 */         boolean saddleFound = false;
/*  670 */         while ((!saddleFound) && (loLevel > 0)) {
/*  671 */           loLevel--;
/*  672 */           lastLen = listLen;
/*  673 */           listI = 0;
/*      */           do {
/*  675 */             int offset = pList[listI];
/*  676 */             int x = offset % this.width;
/*  677 */             int y = offset / this.width;
/*  678 */             boolean isInner = (y != 0) && (y != this.height - 1) && (x != 0) && (x != this.width - 1);
/*  679 */             for (int d = 0; d < 8; d++) {
/*  680 */               int offset2 = offset + this.dirOffset[d];
/*  681 */               if (((isInner) || (isWithin(x, y, d))) && ((types[offset2] & 0x2) == 0)) {
/*  682 */                 if (((types[offset2] & 0x8) != 0) || (((types[offset2] & 0x40) != 0) && ((pixels[offset2] & 0xFF) >= loLevel))) {
/*  683 */                   saddleFound = true;
/*      */ 
/*  685 */                   break;
/*  686 */                 }if (((pixels[offset2] & 0xFF) >= loLevel) && ((types[offset2] & 0x40) == 0)) {
/*  687 */                   pList[listLen] = offset2;
/*      */ 
/*  690 */                   listLen++;
/*      */                   int tmp333_331 = offset2;
/*      */                   byte[] tmp333_329 = types; tmp333_329[tmp333_331] = ((byte)(tmp333_329[tmp333_331] | 0x2));
/*      */                 }
/*      */               }
/*      */             }
/*  695 */             if (saddleFound) break;
/*  696 */             listI++;
/*  697 */           }while (listI < listLen);
/*      */         }
/*  699 */         for (listI = 0; listI < listLen; listI++)
/*      */         {
/*      */           int tmp383_382 = pList[listI];
/*      */           byte[] tmp383_376 = types; tmp383_376[tmp383_382] = ((byte)(tmp383_376[tmp383_382] & 0xFFFFFFFD));
/*  701 */         }for (listI = 0; listI < lastLen; listI++) {
/*  702 */           int offset = pList[listI];
/*  703 */           pixels[offset] = ((byte)loLevel);
/*      */           int tmp425_423 = offset;
/*      */           byte[] tmp425_421 = types; tmp425_421[tmp425_423] = ((byte)(tmp425_421[tmp425_423] | 0x40));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void cleanupExtraLines(ImageProcessor ip)
/*      */   {
/*  715 */     byte[] pixels = (byte[])ip.getPixels();
/*  716 */     int y = 0; for (int i = 0; y < this.height; y++)
/*  717 */       for (int x = 0; x < this.width; i++) {
/*  718 */         int v = pixels[i];
/*  719 */         if ((v != -1) && (v != 0)) {
/*  720 */           int nRadii = nRadii(pixels, x, y);
/*  721 */           if (nRadii == 0)
/*  722 */             pixels[i] = -1;
/*  723 */           else if (nRadii == 1)
/*  724 */             removeLineFrom(pixels, x, y);
/*      */         }
/*  717 */         x++;
/*      */       }
/*      */   }
/*      */ 
/*      */   void removeLineFrom(byte[] pixels, int x, int y)
/*      */   {
/*  734 */     pixels[(x + this.width * y)] = -1;
/*      */     boolean continues;
/*      */     do
/*      */     {
/*  737 */       continues = false;
/*  738 */       boolean isInner = (y != 0) && (y != this.height - 1) && (x != 0) && (x != this.width - 1);
/*  739 */       for (int d = 0; d < 8; d += 2) {
/*  740 */         if ((isInner) || (isWithin(x, y, d))) {
/*  741 */           int v = pixels[(x + this.width * y + this.dirOffset[d])];
/*  742 */           if ((v != -1) && (v != 0)) {
/*  743 */             int nRadii = nRadii(pixels, x + DIR_X_OFFSET[d], y + DIR_Y_OFFSET[d]);
/*  744 */             if (nRadii <= 1) {
/*  745 */               x += DIR_X_OFFSET[d];
/*  746 */               y += DIR_Y_OFFSET[d];
/*  747 */               pixels[(x + this.width * y)] = -1;
/*  748 */               continues = nRadii == 1;
/*  749 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  755 */     while (continues);
/*      */   }
/*      */ 
/*      */   int nRadii(byte[] pixels, int x, int y)
/*      */   {
/*  768 */     int offset = x + y * this.width;
/*  769 */     int countTransitions = 0;
/*  770 */     boolean prevPixelSet = true;
/*  771 */     boolean firstPixelSet = true;
/*  772 */     boolean isInner = (y != 0) && (y != this.height - 1) && (x != 0) && (x != this.width - 1);
/*  773 */     for (int d = 0; d < 8; d++) {
/*  774 */       boolean pixelSet = prevPixelSet;
/*  775 */       if ((isInner) || (isWithin(x, y, d))) {
/*  776 */         boolean isSet = pixels[(offset + this.dirOffset[d])] != -1;
/*  777 */         if ((d & 0x1) == 0) pixelSet = isSet;
/*  778 */         else if (!isSet)
/*  779 */           pixelSet = false;
/*      */       } else {
/*  781 */         pixelSet = true;
/*      */       }
/*  783 */       if ((pixelSet) && (!prevPixelSet))
/*  784 */         countTransitions++;
/*  785 */       prevPixelSet = pixelSet;
/*  786 */       if (d == 0)
/*  787 */         firstPixelSet = pixelSet;
/*      */     }
/*  789 */     if ((firstPixelSet) && (!prevPixelSet))
/*  790 */       countTransitions++;
/*  791 */     return countTransitions;
/*      */   }
/*      */ 
/*      */   private void watershedPostProcess(ImageProcessor ip)
/*      */   {
/*  798 */     byte[] pixels = (byte[])ip.getPixels();
/*  799 */     int size = ip.getWidth() * ip.getHeight();
/*  800 */     for (int i = 0; i < size; i++)
/*  801 */       if ((pixels[i] & 0xFF) < 255)
/*  802 */         pixels[i] = 0;
/*      */   }
/*      */ 
/*      */   void deleteEdgeParticles(ByteProcessor ip, ByteProcessor typeP)
/*      */   {
/*  814 */     byte[] pixels = (byte[])ip.getPixels();
/*  815 */     byte[] types = (byte[])typeP.getPixels();
/*  816 */     this.width = ip.getWidth();
/*  817 */     this.height = ip.getHeight();
/*  818 */     ip.setValue(0.0D);
/*  819 */     Wand wand = new Wand(ip);
/*  820 */     for (int x = 0; x < this.width; x++) {
/*  821 */       int y = 0;
/*  822 */       if (((types[(x + y * this.width)] & 0x8) != 0) && (pixels[(x + y * this.width)] != 0))
/*  823 */         deleteParticle(x, y, ip, wand);
/*  824 */       y = this.height - 1;
/*  825 */       if (((types[(x + y * this.width)] & 0x8) != 0) && (pixels[(x + y * this.width)] != 0))
/*  826 */         deleteParticle(x, y, ip, wand);
/*      */     }
/*  828 */     for (int y = 1; y < this.height - 1; y++) {
/*  829 */       int x = 0;
/*  830 */       if (((types[(x + y * this.width)] & 0x8) != 0) && (pixels[(x + y * this.width)] != 0))
/*  831 */         deleteParticle(x, y, ip, wand);
/*  832 */       x = this.width - 1;
/*  833 */       if (((types[(x + y * this.width)] & 0x8) != 0) && (pixels[(x + y * this.width)] != 0))
/*  834 */         deleteParticle(x, y, ip, wand);
/*      */     }
/*      */   }
/*      */ 
/*      */   void deleteParticle(int x, int y, ByteProcessor ip, Wand wand)
/*      */   {
/*  842 */     wand.autoOutline(x, y, 255, 255);
/*  843 */     if (wand.npoints == 0) {
/*  844 */       IJ.log("wand error selecting edge particle at x, y = " + x + ", " + y);
/*  845 */       return;
/*      */     }
/*  847 */     Roi roi = new PolygonRoi(wand.xpoints, wand.ypoints, wand.npoints, 4);
/*  848 */     ip.snapshot();
/*  849 */     ip.setRoi(roi);
/*  850 */     ip.fill();
/*  851 */     ip.reset(ip.getMask());
/*      */   }
/*      */ 
/*      */   private boolean watershedSegment(ByteProcessor ip)
/*      */   {
/*  863 */     boolean debug = IJ.debugMode;
/*  864 */     ImageStack movie = null;
/*  865 */     if (debug) {
/*  866 */       movie = new ImageStack(ip.getWidth(), ip.getHeight());
/*  867 */       movie.addSlice("pre-watershed EDM", ip.duplicate());
/*      */     }
/*  869 */     byte[] pixels = (byte[])ip.getPixels();
/*      */ 
/*  873 */     int[] histogram = ip.getHistogram();
/*  874 */     int arraySize = this.width * this.height - histogram[0] - histogram['ÿ'];
/*  875 */     int[] coordinates = new int[arraySize];
/*  876 */     int highestValue = 0;
/*  877 */     int maxBinSize = 0;
/*  878 */     int offset = 0;
/*  879 */     int[] levelStart = new int[256];
/*  880 */     for (int v = 1; v < 255; v++) {
/*  881 */       levelStart[v] = offset;
/*  882 */       offset += histogram[v];
/*  883 */       if (histogram[v] > 0) highestValue = v;
/*  884 */       if (histogram[v] > maxBinSize) maxBinSize = histogram[v];
/*      */     }
/*  886 */     int[] levelOffset = new int[highestValue + 1];
/*  887 */     int y = 0; for (int i = 0; y < this.height; y++) {
/*  888 */       for (int x = 0; x < this.width; i++) {
/*  889 */         int v = pixels[i] & 0xFF;
/*  890 */         if ((v > 0) && (v < 255)) {
/*  891 */           offset = levelStart[v] + levelOffset[v];
/*  892 */           coordinates[offset] = (x | y << this.intEncodeShift);
/*  893 */           levelOffset[v] += 1;
/*      */         }
/*  888 */         x++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  899 */     int[] setPointList = new int[Math.min(maxBinSize, (this.width * this.height + 2) / 3)];
/*      */ 
/*  904 */     int[] table = makeFateTable();
/*  905 */     IJ.showStatus("Segmenting (Esc to cancel)");
/*  906 */     int[] directionSequence = { 7, 3, 1, 5, 0, 4, 2, 6 };
/*  907 */     for (int level = highestValue; level >= 1; level--) {
/*  908 */       int remaining = histogram[level];
/*  909 */       int idle = 0;
/*  910 */       while ((remaining > 0) && (idle < 8)) {
/*  911 */         int sumN = 0;
/*  912 */         int dIndex = 0;
/*      */         do {
/*  914 */           int n = processLevel(directionSequence[(dIndex % 8)], ip, table, levelStart[level], remaining, coordinates, setPointList);
/*      */ 
/*  917 */           remaining -= n;
/*  918 */           sumN += n;
/*  919 */           if (n > 0) idle = 0;
/*  920 */           dIndex++;
/*  921 */         }while ((remaining > 0) && (idle++ < 8));
/*  922 */         addProgress(sumN / arraySize);
/*  923 */         if (IJ.escapePressed()) {
/*  924 */           IJ.beep();
/*  925 */           IJ.showProgress(1.0D);
/*  926 */           return false;
/*      */         }
/*      */       }
/*  929 */       if ((remaining > 0) && (level > 1)) {
/*  930 */         int nextLevel = level;
/*      */         do
/*  932 */           nextLevel--;
/*  933 */         while ((nextLevel > 1) && (histogram[nextLevel] == 0));
/*      */ 
/*  939 */         if (nextLevel > 0) {
/*  940 */           int newNextLevelEnd = levelStart[nextLevel] + histogram[nextLevel];
/*  941 */           int i = 0; for (int p = levelStart[level]; i < remaining; p++) {
/*  942 */             int xy = coordinates[p];
/*  943 */             int x = xy & this.intEncodeXMask;
/*  944 */             int y = (xy & this.intEncodeYMask) >> this.intEncodeShift;
/*  945 */             int pOffset = x + y * this.width;
/*  946 */             if ((pixels[pOffset] & 0xFF) == 255) IJ.log("ERROR");
/*  947 */             boolean addToNext = false;
/*  948 */             if ((x == 0) || (y == 0) || (x == this.width - 1) || (y == this.height - 1))
/*  949 */               addToNext = true;
/*  950 */             else for (int d = 0; d < 8; d++)
/*  951 */                 if ((isWithin(x, y, d)) && (pixels[(pOffset + this.dirOffset[d])] == 0)) {
/*  952 */                   addToNext = true;
/*  953 */                   break;
/*      */                 }
/*  955 */             if (addToNext)
/*  956 */               coordinates[(newNextLevelEnd++)] = xy;
/*  941 */             i++;
/*      */           }
/*      */ 
/*  960 */           histogram[nextLevel] = (newNextLevelEnd - levelStart[nextLevel]);
/*      */         }
/*      */       }
/*  963 */       if ((debug) && ((level > 170) || ((level > 100) && (level < 110)) || (level < 10)))
/*  964 */         movie.addSlice("level " + level, ip.duplicate());
/*      */     }
/*  966 */     if (debug)
/*  967 */       new ImagePlus("Segmentation Movie", movie).show();
/*  968 */     return true;
/*      */   }
/*      */ 
/*      */   private int processLevel(int pass, ImageProcessor ip, int[] fateTable, int levelStart, int levelNPoints, int[] coordinates, int[] setPointList)
/*      */   {
/*  984 */     int xmax = this.width - 1;
/*  985 */     int ymax = this.height - 1;
/*  986 */     byte[] pixels = (byte[])ip.getPixels();
/*      */ 
/*  988 */     int nChanged = 0;
/*  989 */     int nUnchanged = 0;
/*  990 */     int i = 0; for (int p = levelStart; i < levelNPoints; p++) {
/*  991 */       int xy = coordinates[p];
/*  992 */       int x = xy & this.intEncodeXMask;
/*  993 */       int y = (xy & this.intEncodeYMask) >> this.intEncodeShift;
/*  994 */       int offset = x + y * this.width;
/*  995 */       int index = 0;
/*  996 */       if ((y > 0) && ((pixels[(offset - this.width)] & 0xFF) == 255))
/*  997 */         index ^= 1;
/*  998 */       if ((x < xmax) && (y > 0) && ((pixels[(offset - this.width + 1)] & 0xFF) == 255))
/*  999 */         index ^= 2;
/* 1000 */       if ((x < xmax) && ((pixels[(offset + 1)] & 0xFF) == 255))
/* 1001 */         index ^= 4;
/* 1002 */       if ((x < xmax) && (y < ymax) && ((pixels[(offset + this.width + 1)] & 0xFF) == 255))
/* 1003 */         index ^= 8;
/* 1004 */       if ((y < ymax) && ((pixels[(offset + this.width)] & 0xFF) == 255))
/* 1005 */         index ^= 16;
/* 1006 */       if ((x > 0) && (y < ymax) && ((pixels[(offset + this.width - 1)] & 0xFF) == 255))
/* 1007 */         index ^= 32;
/* 1008 */       if ((x > 0) && ((pixels[(offset - 1)] & 0xFF) == 255))
/* 1009 */         index ^= 64;
/* 1010 */       if ((x > 0) && (y > 0) && ((pixels[(offset - this.width - 1)] & 0xFF) == 255))
/* 1011 */         index ^= 128;
/* 1012 */       int mask = 1 << pass;
/* 1013 */       if ((fateTable[index] & mask) == mask)
/* 1014 */         setPointList[(nChanged++)] = offset;
/*      */       else
/* 1016 */         coordinates[(levelStart + nUnchanged++)] = xy;
/*  990 */       i++;
/*      */     }
/*      */ 
/* 1020 */     for (int i = 0; i < nChanged; i++)
/* 1021 */       pixels[setPointList[i]] = -1;
/* 1022 */     return nChanged;
/*      */   }
/*      */ 
/*      */   private int[] makeFateTable()
/*      */   {
/* 1041 */     int[] table = new int[256];
/* 1042 */     boolean[] isSet = new boolean[8];
/* 1043 */     for (int item = 0; item < 256; item++) {
/* 1044 */       int i = 0; for (int mask = 1; i < 8; i++) {
/* 1045 */         isSet[i] = ((item & mask) == mask ? 1 : false);
/* 1046 */         mask *= 2;
/*      */       }
/* 1048 */       int i = 0; for (int mask = 1; i < 8; i++) {
/* 1049 */         if (isSet[((i + 4) % 8)] != 0) table[item] |= mask;
/* 1050 */         mask *= 2;
/*      */       }
/* 1052 */       for (int i = 0; i < 8; i += 2)
/* 1053 */         if (isSet[i] != 0) {
/* 1054 */           isSet[((i + 1) % 8)] = true;
/* 1055 */           isSet[((i + 7) % 8)] = true;
/*      */         }
/* 1057 */       int transitions = 0;
/* 1058 */       int i = 0; for (int mask = 1; i < 8; i++) {
/* 1059 */         if (isSet[i] != isSet[((i + 1) % 8)])
/* 1060 */           transitions++;
/*      */       }
/* 1062 */       if (transitions >= 4) {
/* 1063 */         table[item] = 0;
/*      */       }
/*      */     }
/*      */ 
/* 1067 */     return table;
/*      */   }
/*      */ 
/*      */   void makeDirectionOffsets(ImageProcessor ip)
/*      */   {
/* 1081 */     this.width = ip.getWidth();
/* 1082 */     this.height = ip.getHeight();
/* 1083 */     int shift = 0; int mult = 1;
/*      */     do {
/* 1085 */       shift++; mult *= 2;
/*      */     }
/* 1087 */     while (mult < this.width);
/* 1088 */     this.intEncodeXMask = (mult - 1);
/* 1089 */     this.intEncodeYMask = (this.intEncodeXMask ^ 0xFFFFFFFF);
/* 1090 */     this.intEncodeShift = shift;
/*      */ 
/* 1092 */     this.dirOffset = new int[] { -this.width, -this.width + 1, 1, this.width + 1, this.width, this.width - 1, -1, -this.width - 1 };
/*      */   }
/*      */ 
/*      */   boolean isWithin(int x, int y, int direction)
/*      */   {
/* 1106 */     int xmax = this.width - 1;
/* 1107 */     int ymax = this.height - 1;
/* 1108 */     switch (direction) {
/*      */     case 0:
/* 1110 */       return y > 0;
/*      */     case 1:
/* 1112 */       return (x < xmax) && (y > 0);
/*      */     case 2:
/* 1114 */       return x < xmax;
/*      */     case 3:
/* 1116 */       return (x < xmax) && (y < ymax);
/*      */     case 4:
/* 1118 */       return y < ymax;
/*      */     case 5:
/* 1120 */       return (x > 0) && (y < ymax);
/*      */     case 6:
/* 1122 */       return x > 0;
/*      */     case 7:
/* 1124 */       return (x > 0) && (y > 0);
/*      */     }
/* 1126 */     return false;
/*      */   }
/*      */ 
/*      */   private void addProgress(double deltaProgress)
/*      */   {
/* 1131 */     if (this.nPasses == 0) return;
/* 1132 */     this.progressDone += deltaProgress;
/* 1133 */     IJ.showProgress(this.progressDone / this.nPasses);
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.MaximumFinder
 * JD-Core Version:    0.6.2
 */