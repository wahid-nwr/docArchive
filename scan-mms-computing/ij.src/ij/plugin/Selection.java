/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.EllipseRoi;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.PolygonRoi;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.RoiProperties;
/*     */ import ij.gui.ShapeRoi;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.Measurements;
/*     */ import ij.plugin.filter.GaussianBlur;
/*     */ import ij.plugin.filter.ThresholdToSelection;
/*     */ import ij.plugin.frame.LineWidthAdjuster;
/*     */ import ij.plugin.frame.RoiManager;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Color;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class Selection
/*     */   implements PlugIn, Measurements
/*     */ {
/*     */   private ImagePlus imp;
/*  19 */   private float[] kernel = { 1.0F, 1.0F, 1.0F, 1.0F, 1.0F };
/*  20 */   private float[] kernel3 = { 1.0F, 1.0F, 1.0F };
/*  21 */   private static String angle = "15";
/*  22 */   private static String enlarge = "15";
/*  23 */   private static int bandSize = 15;
/*     */   private static boolean nonScalable;
/*     */   private static Color linec;
/*     */   private static Color fillc;
/*  26 */   private static int lineWidth = 1;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  30 */     this.imp = WindowManager.getCurrentImage();
/*  31 */     if (arg.equals("add")) {
/*  32 */       addToRoiManager(this.imp); return;
/*  33 */     }if (this.imp == null) {
/*  34 */       IJ.noImage(); return;
/*  35 */     }if (arg.equals("all")) {
/*  36 */       this.imp.setRoi(0, 0, this.imp.getWidth(), this.imp.getHeight());
/*  37 */     } else if (arg.equals("none")) {
/*  38 */       this.imp.killRoi();
/*  39 */     } else if (arg.equals("restore")) {
/*  40 */       this.imp.restoreRoi();
/*  41 */     } else if (arg.equals("spline")) {
/*  42 */       fitSpline();
/*  43 */     } else if (arg.equals("circle")) {
/*  44 */       fitCircle(this.imp);
/*  45 */     } else if (arg.equals("ellipse")) {
/*  46 */       createEllipse(this.imp);
/*  47 */     } else if (arg.equals("hull")) {
/*  48 */       convexHull(this.imp);
/*  49 */     } else if (arg.equals("mask")) {
/*  50 */       createMask(this.imp);
/*  51 */     } else if (arg.equals("from")) {
/*  52 */       createSelectionFromMask(this.imp);
/*  53 */     } else if (arg.equals("inverse")) {
/*  54 */       invert(this.imp);
/*  55 */     } else if (arg.equals("tobox")) {
/*  56 */       toBoundingBox(this.imp);
/*  57 */     } else if (arg.equals("toarea")) {
/*  58 */       lineToArea(this.imp);
/*  59 */     } else if (arg.equals("toline")) {
/*  60 */       areaToLine(this.imp);
/*  61 */     } else if (arg.equals("properties")) {
/*  62 */       setProperties("Properties", this.imp.getRoi()); this.imp.draw();
/*  63 */     } else if (arg.equals("band")) {
/*  64 */       makeBand(this.imp);
/*     */     } else {
/*  66 */       runMacro(arg);
/*     */     }
/*     */   }
/*     */ 
/*  70 */   void runMacro(String arg) { Roi roi = this.imp.getRoi();
/*  71 */     if (IJ.macroRunning()) {
/*  72 */       String options = Macro.getOptions();
/*  73 */       if ((options != null) && ((options.indexOf("grid=") != -1) || (options.indexOf("interpolat") != -1))) {
/*  74 */         IJ.run("Rotate... ", options);
/*  75 */         return;
/*     */       }
/*     */     }
/*  78 */     if (roi == null) {
/*  79 */       IJ.error("Rotate>Selection", "This command requires a selection");
/*  80 */       return;
/*     */     }
/*  82 */     roi = (Roi)roi.clone();
/*  83 */     if (arg.equals("rotate")) {
/*  84 */       double d = Tools.parseDouble(angle);
/*  85 */       if (Double.isNaN(d)) angle = "15";
/*  86 */       String value = IJ.runMacroFile("ij.jar:RotateSelection", angle);
/*  87 */       if (value != null) angle = value; 
/*     */     }
/*  88 */     else if (arg.equals("enlarge")) {
/*  89 */       String value = IJ.runMacroFile("ij.jar:EnlargeSelection", enlarge);
/*  90 */       if (value != null) enlarge = value;
/*  91 */       Roi.previousRoi = roi;
/*     */     }
/*     */   }
/*     */ 
/*     */   void fitCircle(ImagePlus imp)
/*     */   {
/* 105 */     Roi roi = imp.getRoi();
/* 106 */     if (roi == null) {
/* 107 */       IJ.error("Fit Circle", "Selection required");
/* 108 */       return;
/*     */     }
/*     */ 
/* 111 */     if (roi.isArea()) {
/* 112 */       ImageProcessor ip = imp.getProcessor();
/* 113 */       ip.setRoi(roi);
/* 114 */       ImageStatistics stats = ImageStatistics.getStatistics(ip, 33, null);
/* 115 */       double r = Math.sqrt(stats.pixelCount / 3.141592653589793D);
/* 116 */       imp.killRoi();
/* 117 */       int d = (int)Math.round(2.0D * r);
/* 118 */       IJ.makeOval((int)Math.round(stats.xCentroid - r), (int)Math.round(stats.yCentroid - r), d, d);
/* 119 */       return;
/*     */     }
/*     */ 
/* 122 */     Polygon poly = roi.getPolygon();
/* 123 */     int n = poly.npoints;
/* 124 */     int[] x = poly.xpoints;
/* 125 */     int[] y = poly.ypoints;
/* 126 */     if (n < 3) {
/* 127 */       IJ.error("Fit Circle", "At least 3 points are required to fit a circle.");
/* 128 */       return;
/*     */     }
/*     */ 
/* 132 */     double sumx = 0.0D; double sumy = 0.0D;
/* 133 */     for (int i = 0; i < n; i++) {
/* 134 */       sumx += poly.xpoints[i];
/* 135 */       sumy += poly.ypoints[i];
/*     */     }
/* 137 */     double meanx = sumx / n;
/* 138 */     double meany = sumy / n;
/*     */ 
/* 141 */     double[] X = new double[n]; double[] Y = new double[n];
/* 142 */     double Mxx = 0.0D; double Myy = 0.0D; double Mxy = 0.0D; double Mxz = 0.0D; double Myz = 0.0D; double Mzz = 0.0D;
/* 143 */     for (int i = 0; i < n; i++) {
/* 144 */       X[i] = (x[i] - meanx);
/* 145 */       Y[i] = (y[i] - meany);
/* 146 */       double Zi = X[i] * X[i] + Y[i] * Y[i];
/* 147 */       Mxy += X[i] * Y[i];
/* 148 */       Mxx += X[i] * X[i];
/* 149 */       Myy += Y[i] * Y[i];
/* 150 */       Mxz += X[i] * Zi;
/* 151 */       Myz += Y[i] * Zi;
/* 152 */       Mzz += Zi * Zi;
/*     */     }
/* 154 */     Mxx /= n;
/* 155 */     Myy /= n;
/* 156 */     Mxy /= n;
/* 157 */     Mxz /= n;
/* 158 */     Myz /= n;
/* 159 */     Mzz /= n;
/*     */ 
/* 162 */     double Mz = Mxx + Myy;
/* 163 */     double Cov_xy = Mxx * Myy - Mxy * Mxy;
/* 164 */     double Mxz2 = Mxz * Mxz;
/* 165 */     double Myz2 = Myz * Myz;
/* 166 */     double A2 = 4.0D * Cov_xy - 3.0D * Mz * Mz - Mzz;
/* 167 */     double A1 = Mzz * Mz + 4.0D * Cov_xy * Mz - Mxz2 - Myz2 - Mz * Mz * Mz;
/* 168 */     double A0 = Mxz2 * Myy + Myz2 * Mxx - Mzz * Cov_xy - 2.0D * Mxz * Myz * Mxy + Mz * Mz * Cov_xy;
/* 169 */     double A22 = A2 + A2;
/* 170 */     double epsilon = 1.0E-12D;
/* 171 */     double ynew = 1.0E+20D;
/* 172 */     int IterMax = 20;
/* 173 */     double xnew = 0.0D;
/* 174 */     int iterations = 0;
/*     */ 
/* 177 */     for (int iter = 1; iter <= IterMax; iter++) {
/* 178 */       iterations = iter;
/* 179 */       double yold = ynew;
/* 180 */       ynew = A0 + xnew * (A1 + xnew * (A2 + 4.0D * xnew * xnew));
/* 181 */       if (Math.abs(ynew) > Math.abs(yold)) {
/* 182 */         if (IJ.debugMode) IJ.log("Fit Circle: wrong direction: |ynew| > |yold|");
/* 183 */         xnew = 0.0D;
/* 184 */         break;
/*     */       }
/* 186 */       double Dy = A1 + xnew * (A22 + 16.0D * xnew * xnew);
/* 187 */       double xold = xnew;
/* 188 */       xnew = xold - ynew / Dy;
/* 189 */       if (Math.abs((xnew - xold) / xnew) < epsilon)
/*     */         break;
/* 191 */       if (iter >= IterMax) {
/* 192 */         if (IJ.debugMode) IJ.log("Fit Circle: will not converge");
/* 193 */         xnew = 0.0D;
/*     */       }
/* 195 */       if (xnew < 0.0D) {
/* 196 */         if (IJ.debugMode) IJ.log("Fit Circle: negative root:  x = " + xnew);
/* 197 */         xnew = 0.0D;
/*     */       }
/*     */     }
/* 200 */     if (IJ.debugMode) IJ.log("Fit Circle: n=" + n + ", xnew=" + IJ.d2s(xnew, 2) + ", iterations=" + iterations);
/*     */ 
/* 203 */     double DET = xnew * xnew - xnew * Mz + Cov_xy;
/* 204 */     double CenterX = (Mxz * (Myy - xnew) - Myz * Mxy) / (2.0D * DET);
/* 205 */     double CenterY = (Myz * (Mxx - xnew) - Mxz * Mxy) / (2.0D * DET);
/* 206 */     double radius = Math.sqrt(CenterX * CenterX + CenterY * CenterY + Mz + 2.0D * xnew);
/* 207 */     if (Double.isNaN(radius)) {
/* 208 */       IJ.error("Fit Circle", "Points are collinear.");
/* 209 */       return;
/*     */     }
/* 211 */     CenterX += meanx;
/* 212 */     CenterY += meany;
/* 213 */     imp.killRoi();
/* 214 */     IJ.makeOval((int)Math.round(CenterX - radius), (int)Math.round(CenterY - radius), (int)Math.round(2.0D * radius), (int)Math.round(2.0D * radius));
/*     */   }
/*     */ 
/*     */   void fitSpline() {
/* 218 */     Roi roi = this.imp.getRoi();
/* 219 */     if (roi == null) {
/* 220 */       IJ.error("Spline", "Selection required"); return;
/* 221 */     }int type = roi.getType();
/* 222 */     boolean segmentedSelection = (type == 2) || (type == 6);
/* 223 */     if ((!segmentedSelection) && (type != 3) && (type != 4) && (type != 7)) {
/* 224 */       IJ.error("Spline", "Polygon or polyline selection required"); return;
/* 225 */     }if ((roi instanceof EllipseRoi))
/* 226 */       return;
/* 227 */     PolygonRoi p = (PolygonRoi)roi;
/* 228 */     if (!segmentedSelection)
/* 229 */       p = trimPolygon(p, p.getUncalibratedLength());
/* 230 */     String options = Macro.getOptions();
/* 231 */     if ((options != null) && (options.indexOf("straighten") != -1))
/* 232 */       p.fitSplineForStraightening();
/* 233 */     else if ((options != null) && (options.indexOf("remove") != -1))
/* 234 */       p.removeSplineFit();
/*     */     else
/* 236 */       p.fitSpline();
/* 237 */     this.imp.draw();
/* 238 */     LineWidthAdjuster.update();
/*     */   }
/*     */ 
/*     */   PolygonRoi trimPolygon(PolygonRoi roi, double length) {
/* 242 */     int[] x = roi.getXCoordinates();
/* 243 */     int[] y = roi.getYCoordinates();
/* 244 */     int n = roi.getNCoordinates();
/* 245 */     x = smooth(x, n);
/* 246 */     y = smooth(y, n);
/* 247 */     float[] curvature = getCurvature(x, y, n);
/* 248 */     Rectangle r = roi.getBounds();
/* 249 */     double threshold = rodbard(length);
/*     */ 
/* 251 */     double distance = Math.sqrt((x[1] - x[0]) * (x[1] - x[0]) + (y[1] - y[0]) * (y[1] - y[0]));
/* 252 */     x[0] += r.x; y[0] += r.y;
/* 253 */     int i2 = 1;
/* 254 */     int x2 = 0; int y2 = 0;
/* 255 */     for (int i = 1; i < n - 1; i++) {
/* 256 */       int x1 = x[i]; int y1 = y[i]; x2 = x[(i + 1)]; y2 = y[(i + 1)];
/* 257 */       distance += Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) + 1.0D;
/* 258 */       distance += curvature[i] * 2.0F;
/* 259 */       if (distance >= threshold) {
/* 260 */         x[i2] = (x2 + r.x);
/* 261 */         y[i2] = (y2 + r.y);
/* 262 */         i2++;
/* 263 */         distance = 0.0D;
/*     */       }
/*     */     }
/* 266 */     int type = roi.getType() == 7 ? 6 : 2;
/* 267 */     if ((type == 6) && (distance > 0.0D)) {
/* 268 */       x[i2] = (x2 + r.x);
/* 269 */       y[i2] = (y2 + r.y);
/* 270 */       i2++;
/*     */     }
/* 272 */     PolygonRoi p = new PolygonRoi(x, y, i2, type);
/* 273 */     this.imp.setRoi(p);
/* 274 */     return p;
/*     */   }
/*     */ 
/*     */   double rodbard(double x)
/*     */   {
/*     */     double ex;
/*     */     double ex;
/* 281 */     if (x == 0.0D)
/* 282 */       ex = 5.0D;
/*     */     else
/* 284 */       ex = Math.exp(Math.log(x / 700.0D) * 0.88D);
/* 285 */     double y = -40.100000000000001D;
/* 286 */     y /= (1.0D + ex);
/* 287 */     return y + 44.0D;
/*     */   }
/*     */ 
/*     */   int[] smooth(int[] a, int n) {
/* 291 */     FloatProcessor fp = new FloatProcessor(n, 1);
/* 292 */     for (int i = 0; i < n; i++)
/* 293 */       fp.putPixelValue(i, 0, a[i]);
/* 294 */     GaussianBlur gb = new GaussianBlur();
/* 295 */     gb.blur1Direction(fp, 2.0D, 0.01D, true, 0);
/* 296 */     for (int i = 0; i < n; i++)
/* 297 */       a[i] = Math.round(fp.getPixelValue(i, 0));
/* 298 */     return a;
/*     */   }
/*     */ 
/*     */   float[] getCurvature(int[] x, int[] y, int n) {
/* 302 */     float[] x2 = new float[n];
/* 303 */     float[] y2 = new float[n];
/* 304 */     for (int i = 0; i < n; i++) {
/* 305 */       x2[i] = x[i];
/* 306 */       y2[i] = y[i];
/*     */     }
/* 308 */     ImageProcessor ipx = new FloatProcessor(n, 1, x2, null);
/* 309 */     ImageProcessor ipy = new FloatProcessor(n, 1, y2, null);
/* 310 */     ipx.convolve(this.kernel, this.kernel.length, 1);
/* 311 */     ipy.convolve(this.kernel, this.kernel.length, 1);
/* 312 */     float[] indexes = new float[n];
/* 313 */     float[] curvature = new float[n];
/* 314 */     for (int i = 0; i < n; i++) {
/* 315 */       indexes[i] = i;
/* 316 */       curvature[i] = ((float)Math.sqrt((x2[i] - x[i]) * (x2[i] - x[i]) + (y2[i] - y[i]) * (y2[i] - y[i])));
/*     */     }
/* 318 */     return curvature;
/*     */   }
/*     */ 
/*     */   void createEllipse(ImagePlus imp) {
/* 322 */     IJ.showStatus("Fitting ellipse");
/* 323 */     Roi roi = imp.getRoi();
/* 324 */     if (roi == null) {
/* 325 */       IJ.error("Fit Ellipse", "Selection required"); return;
/* 326 */     }if (roi.isLine()) {
/* 327 */       IJ.error("Fit Ellipse", "\"Fit Ellipse\" does not work with line selections"); return;
/* 328 */     }ImageProcessor ip = imp.getProcessor();
/* 329 */     ip.setRoi(roi);
/* 330 */     int options = 2080;
/* 331 */     ImageStatistics stats = ImageStatistics.getStatistics(ip, options, null);
/* 332 */     double dx = stats.major * Math.cos(stats.angle / 180.0D * 3.141592653589793D) / 2.0D;
/* 333 */     double dy = -stats.major * Math.sin(stats.angle / 180.0D * 3.141592653589793D) / 2.0D;
/* 334 */     double x1 = stats.xCentroid - dx;
/* 335 */     double x2 = stats.xCentroid + dx;
/* 336 */     double y1 = stats.yCentroid - dy;
/* 337 */     double y2 = stats.yCentroid + dy;
/* 338 */     double aspectRatio = stats.minor / stats.major;
/* 339 */     imp.killRoi();
/* 340 */     imp.setRoi(new EllipseRoi(x1, y1, x2, y2, aspectRatio));
/*     */   }
/*     */ 
/*     */   void convexHull(ImagePlus imp) {
/* 344 */     Roi roi = imp.getRoi();
/* 345 */     int type = roi != null ? roi.getType() : -1;
/* 346 */     if ((type != 3) && (type != 4) && (type != 2) && (type != 10)) {
/* 347 */       IJ.error("Convex Hull", "Polygonal or point selection required"); return;
/* 348 */     }if ((roi instanceof EllipseRoi))
/* 349 */       return;
/* 350 */     Polygon p = roi.getConvexHull();
/* 351 */     if (p != null)
/* 352 */       imp.setRoi(new PolygonRoi(p.xpoints, p.ypoints, p.npoints, 2));
/*     */   }
/*     */ 
/*     */   int findFirstPoint(int[] xCoordinates, int[] yCoordinates, int n, ImagePlus imp)
/*     */   {
/* 357 */     int smallestY = imp.getHeight();
/*     */ 
/* 359 */     for (int i = 0; i < n; i++) {
/* 360 */       int y = yCoordinates[i];
/* 361 */       if (y < smallestY)
/* 362 */         smallestY = y;
/*     */     }
/* 364 */     int smallestX = imp.getWidth();
/* 365 */     int p1 = 0;
/* 366 */     for (int i = 0; i < n; i++) {
/* 367 */       int x = xCoordinates[i];
/* 368 */       int y = yCoordinates[i];
/* 369 */       if ((y == smallestY) && (x < smallestX)) {
/* 370 */         smallestX = x;
/* 371 */         p1 = i;
/*     */       }
/*     */     }
/* 374 */     return p1;
/*     */   }
/*     */ 
/*     */   void createMask(ImagePlus imp) {
/* 378 */     Roi roi = imp.getRoi();
/* 379 */     boolean useInvertingLut = Prefs.useInvertingLut;
/* 380 */     Prefs.useInvertingLut = false;
/* 381 */     if ((roi == null) || ((!roi.isArea()) && (roi.getType() != 10))) {
/* 382 */       createMaskFromThreshold(imp);
/* 383 */       Prefs.useInvertingLut = useInvertingLut;
/* 384 */       return;
/*     */     }
/* 386 */     ImagePlus maskImp = null;
/* 387 */     Frame frame = WindowManager.getFrame("Mask");
/* 388 */     if ((frame != null) && ((frame instanceof ImageWindow)))
/* 389 */       maskImp = ((ImageWindow)frame).getImagePlus();
/* 390 */     if (maskImp == null) {
/* 391 */       ImageProcessor ip = new ByteProcessor(imp.getWidth(), imp.getHeight());
/* 392 */       if (!Prefs.blackBackground)
/* 393 */         ip.invertLut();
/* 394 */       maskImp = new ImagePlus("Mask", ip);
/* 395 */       maskImp.show();
/*     */     }
/* 397 */     ImageProcessor ip = maskImp.getProcessor();
/* 398 */     ip.setRoi(roi);
/* 399 */     ip.setValue(255.0D);
/* 400 */     ip.fill(ip.getMask());
/* 401 */     maskImp.updateAndDraw();
/* 402 */     Prefs.useInvertingLut = useInvertingLut;
/*     */   }
/*     */ 
/*     */   void createMaskFromThreshold(ImagePlus imp) {
/* 406 */     ImageProcessor ip = imp.getProcessor();
/* 407 */     if (ip.getMinThreshold() == -808080.0D) {
/* 408 */       IJ.error("Create Mask", "Area selection or thresholded image required"); return;
/* 409 */     }double t1 = ip.getMinThreshold();
/* 410 */     double t2 = ip.getMaxThreshold();
/* 411 */     IJ.run("Duplicate...", "title=mask");
/* 412 */     ImagePlus imp2 = WindowManager.getCurrentImage();
/* 413 */     ImageProcessor ip2 = imp2.getProcessor();
/* 414 */     ip2.setThreshold(t1, t2, 2);
/* 415 */     IJ.run("Convert to Mask");
/*     */   }
/*     */ 
/*     */   void createSelectionFromMask(ImagePlus imp) {
/* 419 */     ImageProcessor ip = imp.getProcessor();
/* 420 */     if (ip.getMinThreshold() != -808080.0D) {
/* 421 */       IJ.runPlugIn("ij.plugin.filter.ThresholdToSelection", "");
/* 422 */       return;
/*     */     }
/* 424 */     if (!ip.isBinary()) {
/* 425 */       IJ.error("Create Selection", "This command creates a composite selection from\na mask (8-bit binary image with white background)\nor from an image that has been thresholded using\nthe Image>Adjust>Threshold tool. The current\nimage is not a mask and has not been thresholded.");
/*     */ 
/* 431 */       return;
/*     */     }
/* 433 */     int threshold = ip.isInvertedLut() ? 255 : 0;
/* 434 */     ip.setThreshold(threshold, threshold, 2);
/* 435 */     IJ.runPlugIn("ij.plugin.filter.ThresholdToSelection", "");
/*     */   }
/*     */ 
/*     */   void invert(ImagePlus imp) {
/* 439 */     Roi roi = imp.getRoi();
/* 440 */     if ((roi == null) || (!roi.isArea())) {
/* 441 */       IJ.error("Inverse", "Area selection required");
/*     */       return;
/*     */     }
/*     */     ShapeRoi s1;
/*     */     ShapeRoi s1;
/* 443 */     if ((roi instanceof ShapeRoi))
/* 444 */       s1 = (ShapeRoi)roi;
/*     */     else
/* 446 */       s1 = new ShapeRoi(roi);
/* 447 */     ShapeRoi s2 = new ShapeRoi(new Roi(0, 0, imp.getWidth(), imp.getHeight()));
/* 448 */     imp.setRoi(s1.xor(s2));
/*     */   }
/*     */ 
/*     */   void lineToArea(ImagePlus imp) {
/* 452 */     Roi roi = imp.getRoi();
/* 453 */     if ((roi == null) || (!roi.isLine())) {
/* 454 */       IJ.error("Line to Area", "Line selection required"); return;
/* 455 */     }if ((roi.getType() == 5) && (roi.getStrokeWidth() == 1.0F)) {
/* 456 */       IJ.error("Line to Area", "Straight line width must be > 1"); return;
/* 457 */     }ImageProcessor ip2 = new ByteProcessor(imp.getWidth(), imp.getHeight());
/* 458 */     ip2.setColor(255);
/* 459 */     if (roi.getType() == 5)
/* 460 */       ip2.fillPolygon(roi.getPolygon());
/*     */     else {
/* 462 */       roi.drawPixels(ip2);
/*     */     }
/*     */ 
/* 471 */     ip2.setThreshold(255.0D, 255.0D, 2);
/* 472 */     ThresholdToSelection tts = new ThresholdToSelection();
/* 473 */     Roi roi2 = tts.convert(ip2);
/* 474 */     imp.setRoi(roi2);
/* 475 */     Roi.previousRoi = (Roi)roi.clone();
/*     */   }
/*     */ 
/*     */   void areaToLine(ImagePlus imp) {
/* 479 */     Roi roi = imp.getRoi();
/* 480 */     if ((roi == null) || (!roi.isArea())) {
/* 481 */       IJ.error("Area to Line", "Area selection required");
/* 482 */       return;
/*     */     }
/* 484 */     Polygon p = roi.getPolygon();
/* 485 */     if (p == null) return;
/* 486 */     int type1 = roi.getType();
/* 487 */     if (type1 == 9) {
/* 488 */       IJ.error("Area to Line", "Composite selections cannot be converted to lines.");
/* 489 */       return;
/*     */     }
/* 491 */     int type2 = 6;
/* 492 */     if ((type1 == 1) || (type1 == 3) || (type1 == 4) || (((roi instanceof PolygonRoi)) && (((PolygonRoi)roi).isSplineFit())))
/*     */     {
/* 494 */       type2 = 7;
/* 495 */     }Roi roi2 = new PolygonRoi(p.xpoints, p.ypoints, p.npoints, type2);
/* 496 */     imp.setRoi(roi2);
/*     */   }
/*     */ 
/*     */   void toBoundingBox(ImagePlus imp) {
/* 500 */     Roi roi = imp.getRoi();
/* 501 */     Rectangle r = roi.getBounds();
/* 502 */     imp.killRoi();
/* 503 */     imp.setRoi(new Roi(r.x, r.y, r.width, r.height));
/*     */   }
/*     */ 
/*     */   void addToRoiManager(ImagePlus imp) {
/* 507 */     if ((IJ.macroRunning()) && (Interpreter.isBatchModeRoiManager()))
/* 508 */       IJ.error("run(\"Add to Manager\") may not work in batch mode macros");
/* 509 */     Frame frame = WindowManager.getFrame("ROI Manager");
/* 510 */     if (frame == null)
/* 511 */       IJ.run("ROI Manager...");
/* 512 */     if (imp == null) return;
/* 513 */     Roi roi = imp.getRoi();
/* 514 */     if (roi == null) return;
/* 515 */     frame = WindowManager.getFrame("ROI Manager");
/* 516 */     if ((frame == null) || (!(frame instanceof RoiManager)))
/* 517 */       IJ.error("ROI Manager not found");
/* 518 */     RoiManager rm = (RoiManager)frame;
/* 519 */     boolean altDown = IJ.altKeyDown();
/* 520 */     IJ.setKeyUp(-1);
/* 521 */     if ((altDown) && (!IJ.macroRunning()))
/* 522 */       IJ.setKeyDown(16);
/* 523 */     rm.runCommand("add");
/* 524 */     IJ.setKeyUp(-1);
/*     */   }
/*     */ 
/*     */   boolean setProperties(String title, Roi roi) {
/* 528 */     Frame f = WindowManager.getFrontWindow();
/* 529 */     if ((f != null) && (f.getTitle().indexOf("3D Viewer") != -1))
/* 530 */       return false;
/* 531 */     if (roi == null) {
/* 532 */       IJ.error("This command requires a selection.");
/* 533 */       return false;
/*     */     }
/* 535 */     RoiProperties rp = new RoiProperties(title, roi);
/* 536 */     return rp.showDialog();
/*     */   }
/*     */ 
/*     */   private void makeBand(ImagePlus imp) {
/* 540 */     Roi roi = imp.getRoi();
/* 541 */     if (roi == null) {
/* 542 */       IJ.error("Make Band", "Selection required");
/* 543 */       return;
/*     */     }
/* 545 */     if (!roi.isArea()) {
/* 546 */       IJ.error("Make Band", "Area selection required");
/* 547 */       return;
/*     */     }
/* 549 */     Calibration cal = imp.getCalibration();
/* 550 */     double pixels = bandSize;
/* 551 */     double size = pixels * cal.pixelWidth;
/* 552 */     int decimalPlaces = 0;
/* 553 */     if ((int)size != size)
/* 554 */       decimalPlaces = 2;
/* 555 */     GenericDialog gd = new GenericDialog("Make Band");
/* 556 */     gd.addNumericField("Band Size:", size, decimalPlaces, 4, cal.getUnits());
/* 557 */     gd.showDialog();
/* 558 */     if (gd.wasCanceled())
/* 559 */       return;
/* 560 */     size = gd.getNextNumber();
/* 561 */     if (Double.isNaN(size)) {
/* 562 */       IJ.error("Make Band", "invalid number");
/* 563 */       return;
/*     */     }
/* 565 */     int n = (int)Math.round(size / cal.pixelWidth);
/* 566 */     if (n > 255) {
/* 567 */       IJ.error("Make Band", "Cannot make bands wider that 255 pixels");
/* 568 */       return;
/*     */     }
/* 570 */     int width = imp.getWidth();
/* 571 */     int height = imp.getHeight();
/* 572 */     Rectangle r = roi.getBounds();
/* 573 */     ImageProcessor ip = roi.getMask();
/* 574 */     if (ip == null) {
/* 575 */       ip = new ByteProcessor(r.width, r.height);
/* 576 */       ip.invert();
/*     */     }
/* 578 */     ImageProcessor mask = new ByteProcessor(width, height);
/* 579 */     mask.insert(ip, r.x, r.y);
/* 580 */     ImagePlus edm = new ImagePlus("mask", mask);
/* 581 */     boolean saveBlackBackground = Prefs.blackBackground;
/* 582 */     Prefs.blackBackground = false;
/* 583 */     IJ.run(edm, "Distance Map", "");
/* 584 */     Prefs.blackBackground = saveBlackBackground;
/* 585 */     ip = edm.getProcessor();
/* 586 */     ip.setThreshold(0.0D, n, 2);
/* 587 */     int xx = -1; int yy = -1;
/* 588 */     for (int x = r.x; x < r.x + r.width; x++) {
/* 589 */       for (int y = r.y; y < r.y + r.height; y++) {
/* 590 */         if (ip.getPixel(x, y) < n) {
/* 591 */           xx = x; yy = y;
/* 592 */           break;
/*     */         }
/*     */       }
/* 595 */       if ((xx >= 0) || (yy >= 0))
/*     */         break;
/*     */     }
/* 598 */     int count = IJ.doWand(edm, xx, yy, 0.0D, null);
/* 599 */     if (count <= 0) {
/* 600 */       IJ.error("Make Band", "Unable to make band");
/* 601 */       return;
/*     */     }
/* 603 */     ShapeRoi roi2 = new ShapeRoi(edm.getRoi());
/* 604 */     if (!(roi instanceof ShapeRoi))
/* 605 */       roi = new ShapeRoi(roi);
/* 606 */     ShapeRoi roi1 = (ShapeRoi)roi;
/* 607 */     roi2 = roi2.not(roi1);
/* 608 */     imp.setRoi(roi2);
/* 609 */     bandSize = n;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Selection
 * JD-Core Version:    0.6.2
 */