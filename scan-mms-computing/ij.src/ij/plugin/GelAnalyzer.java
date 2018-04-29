/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.ProfilePlot;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageConverter;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.IndexColorModel;
/*     */ 
/*     */ public class GelAnalyzer
/*     */   implements PlugIn
/*     */ {
/*     */   static final String OPTIONS = "gel.options";
/*     */   static final String VSCALE = "gel.vscale";
/*     */   static final String HSCALE = "gel.hscale";
/*     */   static final int OD = 1;
/*     */   static final int PERCENT = 2;
/*     */   static final int OUTLINE = 4;
/*     */   static final int INVERT = 8;
/*     */   static int saveID;
/*     */   static int nLanes;
/*     */   static int saveNLanes;
/*     */   static Rectangle firstRect;
/*     */   static final int MAX_LANES = 100;
/*  25 */   static int[] x = new int[101];
/*     */   static PlotsCanvas plotsCanvas;
/*     */   static ImageProcessor ipLanes;
/*     */   static ImagePlus gel;
/*     */   static int plotHeight;
/*  30 */   static int options = (int)Prefs.get("gel.options", 10.0D);
/*  31 */   static boolean uncalibratedOD = (options & 0x1) != 0;
/*  32 */   static boolean labelWithPercentages = (options & 0x2) != 0;
/*     */   static boolean outlineLanes;
/*  34 */   static boolean invertPeaks = (options & 0x8) != 0;
/*  35 */   static double verticalScaleFactor = Prefs.get("gel.vscale", 1.0D);
/*  36 */   static double horizontalScaleFactor = Prefs.get("gel.hscale", 1.0D);
/*     */   static Overlay overlay;
/*     */   boolean invertedLut;
/*     */   ImagePlus imp;
/*     */   Font f;
/*  42 */   double odMin = 1.7976931348623157E+308D; double odMax = -1.797693134862316E+308D;
/*     */   static boolean isVertical;
/*  44 */   static boolean showLaneDialog = true;
/*     */ 
/*     */   public void run(String arg) {
/*  47 */     if (arg.equals("options")) {
/*  48 */       showDialog();
/*  49 */       return;
/*     */     }
/*     */ 
/*  52 */     this.imp = WindowManager.getCurrentImage();
/*  53 */     if (this.imp == null) {
/*  54 */       IJ.noImage();
/*  55 */       return;
/*     */     }
/*     */ 
/*  58 */     if (arg.equals("reset")) {
/*  59 */       nLanes = 0;
/*  60 */       saveNLanes = 0;
/*  61 */       saveID = 0;
/*  62 */       if (plotsCanvas != null)
/*  63 */         plotsCanvas.reset();
/*  64 */       ipLanes = null;
/*  65 */       overlay = null;
/*  66 */       if (gel != null) {
/*  67 */         ImageCanvas ic = gel.getCanvas();
/*  68 */         if (ic != null) ic.setDisplayList(null);
/*  69 */         gel.draw();
/*     */       }
/*  71 */       return;
/*     */     }
/*     */ 
/*  74 */     if ((arg.equals("percent")) && (plotsCanvas != null)) {
/*  75 */       plotsCanvas.displayPercentages();
/*  76 */       return;
/*     */     }
/*     */ 
/*  79 */     if ((arg.equals("label")) && (plotsCanvas != null)) {
/*  80 */       if (plotsCanvas.counter == 0)
/*  81 */         show("There are no peak area measurements.");
/*     */       else
/*  83 */         plotsCanvas.labelPeaks();
/*  84 */       return;
/*     */     }
/*     */ 
/*  87 */     if (this.imp.getID() != saveID) {
/*  88 */       nLanes = 0;
/*  89 */       ipLanes = null;
/*  90 */       saveID = 0;
/*     */     }
/*     */ 
/*  93 */     if (arg.equals("replot")) {
/*  94 */       if (saveNLanes == 0) {
/*  95 */         show("The data needed to re-plot the lanes is not available");
/*  96 */         return;
/*     */       }
/*  98 */       nLanes = saveNLanes;
/*  99 */       plotLanes(gel, true);
/* 100 */       return;
/*     */     }
/*     */ 
/* 103 */     if (arg.equals("draw")) {
/* 104 */       outlineLanes();
/* 105 */       return;
/*     */     }
/*     */ 
/* 108 */     Roi roi = this.imp.getRoi();
/* 109 */     if ((roi == null) || (roi.getType() != 0)) {
/* 110 */       show("Rectangular selection required.");
/* 111 */       return;
/*     */     }
/* 113 */     Rectangle rect = roi.getBounds();
/* 114 */     if (nLanes == 0) {
/* 115 */       this.invertedLut = this.imp.isInvertedLut();
/* 116 */       IJ.register(GelAnalyzer.class);
/*     */     }
/*     */ 
/* 119 */     if (arg.equals("first")) {
/* 120 */       selectFirstLane(rect);
/* 121 */       return;
/*     */     }
/*     */ 
/* 124 */     if (nLanes == 0) {
/* 125 */       show("You must first use the \"Outline First Lane\" command.");
/* 126 */       return;
/*     */     }
/* 128 */     if (arg.equals("next")) {
/* 129 */       selectNextLane(rect);
/* 130 */       return;
/*     */     }
/*     */ 
/* 133 */     if (arg.equals("plot")) {
/* 134 */       if (((isVertical) && (rect.x != x[nLanes])) || ((!isVertical) && (rect.y != x[nLanes]))) {
/* 135 */         selectNextLane(rect);
/*     */       }
/* 137 */       plotLanes(gel, false);
/* 138 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   void showDialog()
/*     */   {
/* 144 */     GenericDialog gd = new GenericDialog("Gel Analyzer");
/* 145 */     gd.addNumericField("Vertical scale factor:", verticalScaleFactor, 1);
/* 146 */     gd.addNumericField("Horizontal scale factor:", horizontalScaleFactor, 1);
/* 147 */     gd.addCheckbox("Uncalibrated OD", uncalibratedOD);
/* 148 */     gd.addCheckbox("Label with percentages", labelWithPercentages);
/* 149 */     gd.addCheckbox("Invert peaks", invertPeaks);
/* 150 */     gd.showDialog();
/* 151 */     if (gd.wasCanceled())
/* 152 */       return;
/* 153 */     verticalScaleFactor = gd.getNextNumber();
/* 154 */     horizontalScaleFactor = gd.getNextNumber();
/* 155 */     uncalibratedOD = gd.getNextBoolean();
/* 156 */     labelWithPercentages = gd.getNextBoolean();
/* 157 */     invertPeaks = gd.getNextBoolean();
/* 158 */     options = 0;
/* 159 */     if (uncalibratedOD) options |= 1;
/* 160 */     if (labelWithPercentages) options |= 2;
/* 161 */     if (invertPeaks) options |= 8;
/* 162 */     Prefs.set("gel.options", options);
/* 163 */     Prefs.set("gel.vscale", verticalScaleFactor);
/* 164 */     Prefs.set("gel.hscale", horizontalScaleFactor);
/*     */   }
/*     */ 
/*     */   void selectFirstLane(Rectangle rect)
/*     */   {
/* 169 */     if ((rect.width / rect.height >= 2) || (IJ.altKeyDown())) {
/* 170 */       if (showLaneDialog) {
/* 171 */         String msg = "Are the lanes really horizontal?\n \nImageJ assumes the lanes are\nhorizontal if the selection is more\nthan twice as wide as it is tall. Note\nthat the selection can only be moved\nvertically when the lanes are horizontal.";
/*     */ 
/* 177 */         GenericDialog gd = new GenericDialog("Gel Analyzer");
/* 178 */         gd.addMessage(msg);
/* 179 */         gd.setOKLabel("Yes");
/* 180 */         gd.showDialog();
/* 181 */         if (gd.wasCanceled()) return;
/* 182 */         showLaneDialog = false;
/*     */       }
/* 184 */       isVertical = false;
/*     */     } else {
/* 186 */       isVertical = true;
/*     */     }
/*     */ 
/* 205 */     IJ.showStatus("Lane 1 selected (" + (isVertical ? "vertical" : "horizontal") + " lanes)");
/* 206 */     firstRect = rect;
/* 207 */     nLanes = 1;
/* 208 */     saveNLanes = 0;
/* 209 */     if (isVertical)
/* 210 */       x[1] = rect.x;
/*     */     else
/* 212 */       x[1] = rect.y;
/* 213 */     gel = this.imp;
/* 214 */     saveID = this.imp.getID();
/* 215 */     overlay = null;
/* 216 */     updateRoiList(rect);
/*     */   }
/*     */ 
/*     */   void selectNextLane(Rectangle rect) {
/* 220 */     if ((rect.width != firstRect.width) || (rect.height != firstRect.height)) {
/* 221 */       show("Selections must all be the same size.");
/* 222 */       return;
/*     */     }
/* 224 */     if (nLanes < 100)
/* 225 */       nLanes += 1;
/* 226 */     IJ.showStatus("Lane " + nLanes + " selected");
/*     */ 
/* 228 */     if (isVertical)
/* 229 */       x[nLanes] = rect.x;
/*     */     else
/* 231 */       x[nLanes] = rect.y;
/* 232 */     if ((isVertical) && (rect.y != firstRect.y)) {
/* 233 */       rect.y = firstRect.y;
/* 234 */       gel.setRoi(rect);
/* 235 */     } else if ((!isVertical) && (rect.x != firstRect.x)) {
/* 236 */       rect.x = firstRect.x;
/* 237 */       gel.setRoi(rect);
/*     */     }
/* 239 */     updateRoiList(rect);
/*     */   }
/*     */ 
/*     */   void updateRoiList(Rectangle rect) {
/* 243 */     if (gel == null) return;
/* 244 */     if (overlay == null) {
/* 245 */       overlay = new Overlay();
/* 246 */       overlay.drawLabels(true);
/*     */     }
/* 248 */     overlay.add(new Roi(rect.x, rect.y, rect.width, rect.height, null));
/* 249 */     gel.setOverlay(overlay);
/*     */   }
/*     */ 
/*     */   void plotLanes(ImagePlus imp, boolean replot) {
/* 253 */     int topMargin = 16;
/* 254 */     int bottomMargin = 2;
/* 255 */     double min = 1.7976931348623157E+308D;
/* 256 */     double max = -1.797693134862316E+308D;
/*     */ 
/* 259 */     double[][] profiles = new double[101][];
/* 260 */     IJ.showStatus("Plotting " + nLanes + " lanes");
/* 261 */     ImageProcessor ipRotated = imp.getProcessor();
/* 262 */     if (isVertical)
/* 263 */       ipRotated = ipRotated.rotateLeft();
/* 264 */     ImagePlus imp2 = new ImagePlus("", ipRotated);
/* 265 */     imp2.setCalibration(imp.getCalibration());
/* 266 */     if ((uncalibratedOD) && ((imp2.getType() == 1) || (imp2.getType() == 2)))
/* 267 */       new ImageConverter(imp2).convertToGray8();
/* 268 */     if (invertPeaks) {
/* 269 */       ImageProcessor ip2 = imp2.getProcessor().duplicate();
/* 270 */       ip2.invert();
/* 271 */       imp2.setProcessor(null, ip2);
/*     */     }
/*     */ 
/* 275 */     for (int i = 1; i <= nLanes; i++) {
/* 276 */       if (isVertical) {
/* 277 */         imp2.setRoi(firstRect.y, ipRotated.getHeight() - x[i] - firstRect.width, firstRect.height, firstRect.width);
/*     */       }
/*     */       else
/*     */       {
/* 281 */         imp2.setRoi(firstRect.x, x[i], firstRect.width, firstRect.height);
/* 282 */       }ProfilePlot pp = new ProfilePlot(imp2);
/* 283 */       profiles[i] = pp.getProfile();
/* 284 */       if (pp.getMin() < min)
/* 285 */         min = pp.getMin();
/* 286 */       if (pp.getMax() > max)
/* 287 */         max = pp.getMax();
/* 288 */       if (uncalibratedOD)
/* 289 */         profiles[i] = od(profiles[i]);
/*     */     }
/* 291 */     if (uncalibratedOD) {
/* 292 */       min = this.odMin;
/* 293 */       max = this.odMax;
/*     */     }
/*     */     int plotWidth;
/* 296 */     if (isVertical)
/* 297 */       plotWidth = firstRect.height;
/*     */     else
/* 299 */       plotWidth = firstRect.width;
/* 300 */     if (plotWidth < 650)
/* 301 */       plotWidth = 650;
/* 302 */     if (isVertical) {
/* 303 */       if (plotWidth > 4 * firstRect.height)
/* 304 */         plotWidth = 4 * firstRect.height;
/*     */     }
/* 306 */     else if (plotWidth > 4 * firstRect.width) {
/* 307 */       plotWidth = 4 * firstRect.width;
/*     */     }
/*     */ 
/* 310 */     Dimension screen = IJ.getScreenSize();
/* 311 */     if (plotWidth > screen.width - screen.width / 6)
/* 312 */       plotWidth = screen.width - screen.width / 6;
/* 313 */     int plotWidth = (int)(plotWidth * horizontalScaleFactor);
/* 314 */     plotHeight = plotWidth / 2;
/* 315 */     if (plotHeight < 250) plotHeight = 250;
/*     */ 
/* 317 */     plotHeight = (int)(plotHeight * verticalScaleFactor);
/* 318 */     ImageProcessor ip = new ByteProcessor(plotWidth, topMargin + nLanes * plotHeight + bottomMargin);
/* 319 */     ip.setColor(Color.white);
/* 320 */     ip.fill();
/* 321 */     ip.setColor(Color.black);
/*     */ 
/* 323 */     int h = ip.getHeight();
/* 324 */     ip.moveTo(0, 0);
/* 325 */     ip.lineTo(plotWidth - 1, 0);
/* 326 */     ip.lineTo(plotWidth - 1, h - 1);
/* 327 */     ip.lineTo(0, h - 1);
/* 328 */     ip.lineTo(0, 0);
/* 329 */     ip.moveTo(0, h - 2);
/* 330 */     ip.lineTo(plotWidth - 1, h - 2);
/* 331 */     String s = imp.getTitle() + "; ";
/* 332 */     Calibration cal = imp.getCalibration();
/* 333 */     if (cal.calibrated())
/* 334 */       s = s + cal.getValueUnit();
/* 335 */     else if (uncalibratedOD)
/* 336 */       s = s + "Uncalibrated OD";
/*     */     else
/* 338 */       s = s + "Uncalibrated";
/* 339 */     ip.moveTo(5, topMargin);
/* 340 */     ip.drawString(s);
/* 341 */     double xScale = plotWidth / profiles[1].length;
/*     */     double yScale;
/*     */     double yScale;
/* 343 */     if (max - min == 0.0D)
/* 344 */       yScale = 1.0D;
/*     */     else
/* 346 */       yScale = plotHeight / (max - min);
/* 347 */     for (int i = 1; i <= nLanes; i++) {
/* 348 */       double[] profile = profiles[i];
/* 349 */       int top = (i - 1) * plotHeight + topMargin;
/* 350 */       int base = top + plotHeight;
/* 351 */       ip.moveTo(0, base);
/* 352 */       ip.lineTo((int)(profile.length * xScale), base);
/* 353 */       ip.moveTo(0, base - (int)((profile[0] - min) * yScale));
/* 354 */       for (int j = 1; j < profile.length; j++)
/* 355 */         ip.lineTo((int)(j * xScale + 0.5D), base - (int)((profile[j] - min) * yScale + 0.5D));
/*     */     }
/* 357 */     Line.setWidth(1);
/* 358 */     ImagePlus plots = new Plots();
/* 359 */     plots.setProcessor("Plots of " + imp.getShortTitle(), ip);
/* 360 */     plots.changes = true;
/* 361 */     ip.setThreshold(0.0D, 0.0D, 2);
/* 362 */     if (cal.calibrated()) {
/* 363 */       double pixelsAveraged = isVertical ? firstRect.width : firstRect.height;
/* 364 */       double scale = Math.sqrt(xScale * yScale / pixelsAveraged);
/* 365 */       Calibration plotsCal = plots.getCalibration();
/* 366 */       plotsCal.setUnit("unit");
/* 367 */       plotsCal.pixelWidth = (1.0D / scale);
/* 368 */       plotsCal.pixelHeight = (1.0D / scale);
/*     */     }
/* 370 */     plots.show();
/* 371 */     saveNLanes = nLanes;
/* 372 */     nLanes = 0;
/* 373 */     saveID = 0;
/*     */ 
/* 375 */     ipLanes = null;
/* 376 */     Toolbar toolbar = Toolbar.getInstance();
/* 377 */     toolbar.setColor(Color.black);
/* 378 */     toolbar.setTool(4);
/* 379 */     ImageWindow win = WindowManager.getCurrentWindow();
/* 380 */     ImageCanvas canvas = win.getCanvas();
/* 381 */     if ((canvas instanceof PlotsCanvas))
/* 382 */       plotsCanvas = (PlotsCanvas)canvas;
/*     */     else
/* 384 */       plotsCanvas = null;
/*     */   }
/*     */ 
/*     */   double[] od(double[] profile)
/*     */   {
/* 389 */     for (int i = 0; i < profile.length; i++) {
/* 390 */       double v = 0.434294481D * Math.log(255.0D / (255.0D - profile[i]));
/*     */ 
/* 392 */       if (v < this.odMin) this.odMin = v;
/* 393 */       if (v > this.odMax) this.odMax = v;
/* 394 */       profile[i] = v;
/*     */     }
/* 396 */     return profile;
/*     */   }
/*     */ 
/*     */   void outlineLanes() {
/* 400 */     if ((gel == null) || (overlay == null)) {
/* 401 */       show("Data needed to outline lanes is no longer available.");
/* 402 */       return;
/*     */     }
/* 404 */     int lineWidth = (int)(1.0D / gel.getCanvas().getMagnification());
/* 405 */     if (lineWidth < 1)
/* 406 */       lineWidth = 1;
/* 407 */     Font f = new Font("Helvetica", 0, 12 * lineWidth);
/* 408 */     ImageProcessor ip = gel.getProcessor();
/* 409 */     ImageProcessor ipLanes = ip.duplicate();
/* 410 */     if (!(ipLanes instanceof ByteProcessor))
/* 411 */       ipLanes = ipLanes.convertToByte(true);
/* 412 */     ipLanes.setFont(f);
/* 413 */     ipLanes.setLineWidth(lineWidth);
/* 414 */     setCustomLut(ipLanes);
/* 415 */     ImagePlus lanes = new ImagePlus("Lanes of " + gel.getShortTitle(), ipLanes);
/* 416 */     lanes.changes = true;
/* 417 */     lanes.setRoi(gel.getRoi());
/* 418 */     gel.killRoi();
/* 419 */     for (int i = 0; i < overlay.size(); i++) {
/* 420 */       Roi roi = overlay.get(i);
/* 421 */       Rectangle r = roi.getBounds();
/* 422 */       ipLanes.drawRect(r.x, r.y, r.width, r.height);
/* 423 */       String s = "" + (i + 1);
/* 424 */       if (isVertical) {
/* 425 */         int yloc = r.y;
/* 426 */         if (yloc < lineWidth * 12) yloc += lineWidth * 14;
/* 427 */         ipLanes.drawString(s, r.x + r.width / 2 - ipLanes.getStringWidth(s) / 2, yloc);
/*     */       } else {
/* 429 */         int xloc = r.x - ipLanes.getStringWidth(s) - 2;
/* 430 */         if (xloc < lineWidth * 10) xloc = r.x + 2;
/* 431 */         ipLanes.drawString(s, xloc, r.y + r.height / 2 + 6);
/*     */       }
/*     */     }
/* 434 */     lanes.killRoi();
/* 435 */     lanes.show();
/*     */   }
/*     */ 
/*     */   void setCustomLut(ImageProcessor ip) {
/* 439 */     IndexColorModel cm = (IndexColorModel)ip.getColorModel();
/* 440 */     byte[] reds = new byte[256];
/* 441 */     byte[] greens = new byte[256];
/* 442 */     byte[] blues = new byte[256];
/* 443 */     cm.getReds(reds);
/* 444 */     cm.getGreens(greens);
/* 445 */     cm.getBlues(blues);
/* 446 */     reds[1] = -1;
/* 447 */     greens[1] = 0;
/* 448 */     blues[1] = 0;
/* 449 */     ip.setColorModel(new IndexColorModel(8, 256, reds, greens, blues));
/* 450 */     byte[] pixels = (byte[])ip.getPixels();
/* 451 */     for (int i = 0; i < pixels.length; i++)
/* 452 */       if ((pixels[i] & 0xFF) == 1)
/* 453 */         pixels[i] = 0;
/* 454 */     ip.setColor(1);
/*     */   }
/*     */ 
/*     */   void show(String msg) {
/* 458 */     IJ.showMessage("Gel Analyzer", msg);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.GelAnalyzer
 * JD-Core Version:    0.6.2
 */