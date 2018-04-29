/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.Straightener;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class ProfilePlot
/*     */ {
/*     */   static final int MIN_WIDTH = 350;
/*     */   static final double ASPECT_RATIO = 0.5D;
/*     */   private double min;
/*     */   private double max;
/*     */   private boolean minAndMaxCalculated;
/*  17 */   private static double fixedMin = Prefs.getDouble("pp.min", 0.0D);
/*  18 */   private static double fixedMax = Prefs.getDouble("pp.max", 0.0D);
/*     */   protected ImagePlus imp;
/*     */   protected double[] profile;
/*     */   protected double magnification;
/*     */   protected double xInc;
/*     */   protected String units;
/*     */   protected String yLabel;
/*     */   protected float[] xValues;
/*     */ 
/*     */   public ProfilePlot()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ProfilePlot(ImagePlus imp)
/*     */   {
/*  33 */     this(imp, false);
/*     */   }
/*     */ 
/*     */   public ProfilePlot(ImagePlus imp, boolean averageHorizontally) {
/*  37 */     this.imp = imp;
/*  38 */     Roi roi = imp.getRoi();
/*  39 */     if (roi == null) {
/*  40 */       IJ.error("Profile Plot", "Selection required.");
/*  41 */       return;
/*     */     }
/*  43 */     int roiType = roi.getType();
/*  44 */     if ((!roi.isLine()) && (roiType != 0)) {
/*  45 */       IJ.error("Line or rectangular selection required.");
/*  46 */       return;
/*     */     }
/*  48 */     Calibration cal = imp.getCalibration();
/*  49 */     this.xInc = cal.pixelWidth;
/*  50 */     this.units = cal.getUnits();
/*  51 */     this.yLabel = cal.getValueUnit();
/*  52 */     ImageProcessor ip = imp.getProcessor();
/*     */ 
/*  54 */     if (roiType == 5) {
/*  55 */       this.profile = getStraightLineProfile(roi, cal, ip);
/*  56 */     } else if ((roiType == 6) || (roiType == 7)) {
/*  57 */       int lineWidth = Math.round(roi.getStrokeWidth());
/*  58 */       if (lineWidth == 1)
/*  59 */         this.profile = getIrregularProfile(roi, ip, cal);
/*     */       else
/*  61 */         this.profile = getWideLineProfile(imp, lineWidth);
/*  62 */     } else if (averageHorizontally) {
/*  63 */       this.profile = getRowAverageProfile(roi.getBounds(), cal, ip);
/*     */     } else {
/*  65 */       this.profile = getColumnAverageProfile(roi.getBounds(), ip);
/*  66 */     }ip.setCalibrationTable(null);
/*  67 */     ImageCanvas ic = imp.getCanvas();
/*  68 */     if (ic != null)
/*  69 */       this.magnification = ic.getMagnification();
/*     */     else
/*  71 */       this.magnification = 1.0D;
/*     */   }
/*     */ 
/*     */   public Dimension getPlotSize()
/*     */   {
/*  84 */     if (this.profile == null) return null;
/*  85 */     int width = (int)(this.profile.length * this.magnification);
/*  86 */     int height = (int)(width * 0.5D);
/*  87 */     if (width < 350) {
/*  88 */       width = 350;
/*  89 */       height = (int)(width * 0.5D);
/*     */     }
/*  91 */     Dimension screen = IJ.getScreenSize();
/*  92 */     int maxWidth = Math.min(screen.width - 200, 1000);
/*  93 */     if (width > maxWidth) {
/*  94 */       width = maxWidth;
/*  95 */       height = (int)(width * 0.5D);
/*     */     }
/*  97 */     return new Dimension(width, height);
/*     */   }
/*     */ 
/*     */   public void createWindow()
/*     */   {
/* 102 */     Plot plot = getPlot();
/* 103 */     if (plot == null) return;
/* 104 */     plot.setSourceImageID(this.imp.getID());
/* 105 */     plot.show();
/*     */   }
/*     */ 
/*     */   Plot getPlot() {
/* 109 */     if (this.profile == null)
/* 110 */       return null;
/* 111 */     Dimension d = getPlotSize();
/* 112 */     String xLabel = "Distance (" + this.units + ")";
/* 113 */     int n = this.profile.length;
/* 114 */     if (this.xValues == null) {
/* 115 */       this.xValues = new float[n];
/* 116 */       for (int i = 0; i < n; i++)
/* 117 */         this.xValues[i] = ((float)(i * this.xInc));
/*     */     }
/* 119 */     float[] yValues = new float[n];
/* 120 */     for (int i = 0; i < n; i++)
/* 121 */       yValues[i] = ((float)this.profile[i]);
/* 122 */     boolean fixedYScale = (fixedMin != 0.0D) || (fixedMax != 0.0D);
/* 123 */     Plot plot = new Plot("Plot of " + getShortTitle(this.imp), xLabel, this.yLabel, this.xValues, yValues);
/* 124 */     if (fixedYScale) {
/* 125 */       double[] a = Tools.getMinMax(this.xValues);
/* 126 */       plot.setLimits(a[0], a[1], fixedMin, fixedMax);
/*     */     }
/* 128 */     return plot;
/*     */   }
/*     */ 
/*     */   String getShortTitle(ImagePlus imp) {
/* 132 */     String title = imp.getTitle();
/* 133 */     int index = title.lastIndexOf('.');
/* 134 */     if ((index > 0) && (title.length() - index <= 5))
/* 135 */       title = title.substring(0, index);
/* 136 */     return title;
/*     */   }
/*     */ 
/*     */   public double[] getProfile()
/*     */   {
/* 141 */     return this.profile;
/*     */   }
/*     */ 
/*     */   public double getMin()
/*     */   {
/* 146 */     if (!this.minAndMaxCalculated)
/* 147 */       findMinAndMax();
/* 148 */     return this.min;
/*     */   }
/*     */ 
/*     */   public double getMax()
/*     */   {
/* 153 */     if (!this.minAndMaxCalculated)
/* 154 */       findMinAndMax();
/* 155 */     return this.max;
/*     */   }
/*     */ 
/*     */   public static void setMinAndMax(double min, double max)
/*     */   {
/* 160 */     fixedMin = min;
/* 161 */     fixedMax = max;
/* 162 */     IJ.register(ProfilePlot.class);
/*     */   }
/*     */ 
/*     */   public static double getFixedMin()
/*     */   {
/* 167 */     return fixedMin;
/*     */   }
/*     */ 
/*     */   public static double getFixedMax()
/*     */   {
/* 172 */     return fixedMax;
/*     */   }
/*     */ 
/*     */   double[] getStraightLineProfile(Roi roi, Calibration cal, ImageProcessor ip) {
/* 176 */     ip.setInterpolate(PlotWindow.interpolate);
/* 177 */     Line line = (Line)roi;
/* 178 */     double[] values = line.getPixels();
/* 179 */     if (values == null) return null;
/* 180 */     if ((cal != null) && (cal.pixelWidth != cal.pixelHeight)) {
/* 181 */       double dx = cal.pixelWidth * (line.x2 - line.x1);
/* 182 */       double dy = cal.pixelHeight * (line.y2 - line.y1);
/* 183 */       double length = Math.round(Math.sqrt(dx * dx + dy * dy));
/* 184 */       if (values.length > 1)
/* 185 */         this.xInc = (length / (values.length - 1));
/*     */     }
/* 187 */     return values;
/*     */   }
/*     */ 
/*     */   double[] getRowAverageProfile(Rectangle rect, Calibration cal, ImageProcessor ip) {
/* 191 */     double[] profile = new double[rect.height];
/*     */ 
/* 194 */     ip.setInterpolate(false);
/* 195 */     for (int x = rect.x; x < rect.x + rect.width; x++) {
/* 196 */       double[] aLine = ip.getLine(x, rect.y, x, rect.y + rect.height - 1);
/* 197 */       for (int i = 0; i < rect.height; i++)
/* 198 */         profile[i] += aLine[i];
/*     */     }
/* 200 */     for (int i = 0; i < rect.height; i++)
/* 201 */       profile[i] /= rect.width;
/* 202 */     if (cal != null)
/* 203 */       this.xInc = cal.pixelHeight;
/* 204 */     return profile;
/*     */   }
/*     */ 
/*     */   double[] getColumnAverageProfile(Rectangle rect, ImageProcessor ip) {
/* 208 */     double[] profile = new double[rect.width];
/*     */ 
/* 211 */     ip.setInterpolate(false);
/* 212 */     for (int y = rect.y; y < rect.y + rect.height; y++) {
/* 213 */       double[] aLine = ip.getLine(rect.x, y, rect.x + rect.width - 1, y);
/* 214 */       for (int i = 0; i < rect.width; i++)
/* 215 */         profile[i] += aLine[i];
/*     */     }
/* 217 */     for (int i = 0; i < rect.width; i++)
/* 218 */       profile[i] /= rect.height;
/* 219 */     return profile;
/*     */   }
/*     */ 
/*     */   double[] getIrregularProfile(Roi roi, ImageProcessor ip, Calibration cal) {
/* 223 */     boolean interpolate = PlotWindow.interpolate;
/* 224 */     boolean calcXValues = (cal != null) && (cal.pixelWidth != cal.pixelHeight);
/* 225 */     int n = ((PolygonRoi)roi).getNCoordinates();
/* 226 */     int[] x = ((PolygonRoi)roi).getXCoordinates();
/* 227 */     int[] y = ((PolygonRoi)roi).getYCoordinates();
/* 228 */     Rectangle r = roi.getBounds();
/* 229 */     int xbase = r.x;
/* 230 */     int ybase = r.y;
/* 231 */     double length = 0.0D;
/*     */ 
/* 234 */     double[] segmentLengths = new double[n];
/* 235 */     int[] dx = new int[n];
/* 236 */     int[] dy = new int[n];
/* 237 */     for (int i = 0; i < n - 1; i++) {
/* 238 */       int xdelta = x[(i + 1)] - x[i];
/* 239 */       int ydelta = y[(i + 1)] - y[i];
/* 240 */       double segmentLength = Math.sqrt(xdelta * xdelta + ydelta * ydelta);
/* 241 */       length += segmentLength;
/* 242 */       segmentLengths[i] = segmentLength;
/* 243 */       dx[i] = xdelta;
/* 244 */       dy[i] = ydelta;
/*     */     }
/* 246 */     double[] values = new double[(int)length];
/* 247 */     if (calcXValues) this.xValues = new float[(int)length];
/* 248 */     double leftOver = 1.0D;
/* 249 */     double distance = 0.0D;
/*     */ 
/* 251 */     double oldrx = 0.0D; double oldry = 0.0D; double xvalue = 0.0D;
/* 252 */     for (int i = 0; i < n; i++) {
/* 253 */       double len = segmentLengths[i];
/* 254 */       if (len != 0.0D)
/*     */       {
/* 256 */         double xinc = dx[i] / len;
/* 257 */         double yinc = dy[i] / len;
/* 258 */         double start = 1.0D - leftOver;
/* 259 */         double rx = xbase + x[i] + start * xinc;
/* 260 */         double ry = ybase + y[i] + start * yinc;
/* 261 */         double len2 = len - start;
/* 262 */         int n2 = (int)len2;
/* 263 */         for (int j = 0; j <= n2; j++) {
/* 264 */           int index = (int)distance + j;
/*     */ 
/* 266 */           if (index < values.length) {
/* 267 */             if (interpolate)
/* 268 */               values[index] = ip.getInterpolatedValue(rx, ry);
/*     */             else
/* 270 */               values[index] = ip.getPixelValue((int)(rx + 0.5D), (int)(ry + 0.5D));
/* 271 */             if ((calcXValues) && (index > 0)) {
/* 272 */               double deltax = cal.pixelWidth * (rx - oldrx);
/* 273 */               double deltay = cal.pixelHeight * (ry - oldry);
/* 274 */               xvalue += Math.sqrt(deltax * deltax + deltay * deltay);
/* 275 */               this.xValues[index] = ((float)xvalue);
/*     */             }
/* 277 */             oldrx = rx; oldry = ry;
/*     */           }
/* 279 */           rx += xinc;
/* 280 */           ry += yinc;
/*     */         }
/* 282 */         distance += len;
/* 283 */         leftOver = len2 - n2;
/*     */       }
/*     */     }
/* 285 */     return values;
/*     */   }
/*     */ 
/*     */   double[] getWideLineProfile(ImagePlus imp, int lineWidth) {
/* 289 */     Roi roi = (Roi)imp.getRoi().clone();
/* 290 */     ImageProcessor ip2 = new Straightener().straightenLine(imp, lineWidth);
/* 291 */     int width = ip2.getWidth();
/* 292 */     int height = ip2.getHeight();
/* 293 */     this.profile = new double[width];
/*     */ 
/* 295 */     ip2.setInterpolate(false);
/* 296 */     for (int y = 0; y < height; y++) {
/* 297 */       double[] aLine = ip2.getLine(0.0D, y, width - 1, y);
/* 298 */       for (int i = 0; i < width; i++)
/* 299 */         this.profile[i] += aLine[i];
/*     */     }
/* 301 */     for (int i = 0; i < width; i++)
/* 302 */       this.profile[i] /= height;
/* 303 */     imp.setRoi(roi);
/* 304 */     if ((roi.getType() == 6) && (!((PolygonRoi)roi).isSplineFit())) {
/* 305 */       ((PolygonRoi)roi).fitSpline();
/* 306 */       imp.draw();
/*     */     }
/* 308 */     return this.profile;
/*     */   }
/*     */ 
/*     */   void findMinAndMax() {
/* 312 */     if (this.profile == null) return;
/* 313 */     double min = 1.7976931348623157E+308D;
/* 314 */     double max = -1.797693134862316E+308D;
/*     */ 
/* 316 */     for (int i = 0; i < this.profile.length; i++) {
/* 317 */       double value = this.profile[i];
/* 318 */       if (value < min) min = value;
/* 319 */       if (value > max) max = value;
/*     */     }
/* 321 */     this.min = min;
/* 322 */     this.max = max;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.ProfilePlot
 * JD-Core Version:    0.6.2
 */