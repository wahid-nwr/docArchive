/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.PolygonRoi;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatPolygon;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Image;
/*     */ import java.awt.Polygon;
/*     */ 
/*     */ public class Straightener
/*     */   implements PlugIn
/*     */ {
/*     */   static boolean processStack;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  13 */     ImagePlus imp = IJ.getImage();
/*  14 */     Roi roi = imp.getRoi();
/*  15 */     if ((roi == null) || (!roi.isLine())) {
/*  16 */       IJ.error("Straightener", "Line selection required");
/*  17 */       return;
/*     */     }
/*  19 */     int width = Math.round(roi.getStrokeWidth());
/*  20 */     int originalWidth = width;
/*  21 */     boolean isMacro = (IJ.macroRunning()) && (Macro.getOptions() != null);
/*  22 */     int stackSize = imp.getStackSize();
/*  23 */     if (stackSize == 1) processStack = false;
/*  24 */     if ((width == 1) || (isMacro) || (stackSize > 1)) {
/*  25 */       if (width == 1) width = 20;
/*  26 */       GenericDialog gd = new GenericDialog("Straightener");
/*  27 */       gd.addNumericField("Line Width:", width, 0, 3, "pixels");
/*  28 */       if (stackSize > 1)
/*  29 */         gd.addCheckbox("Process Entire Stack", processStack);
/*  30 */       gd.showDialog();
/*  31 */       if (gd.wasCanceled()) return;
/*  32 */       width = (int)gd.getNextNumber();
/*  33 */       Line.setWidth(width);
/*  34 */       if (stackSize > 1)
/*  35 */         processStack = gd.getNextBoolean();
/*     */     }
/*  37 */     roi = (Roi)imp.getRoi().clone();
/*  38 */     int type = roi.getType();
/*  39 */     if (type == 7)
/*  40 */       IJ.run(imp, "Fit Spline", "");
/*  41 */     ImageProcessor ip2 = null;
/*  42 */     ImagePlus imp2 = null;
/*  43 */     if (processStack) {
/*  44 */       ImageStack stack2 = straightenStack(imp, roi, width);
/*  45 */       imp2 = new ImagePlus(WindowManager.getUniqueName(imp.getTitle()), stack2);
/*     */     } else {
/*  47 */       ip2 = straighten(imp, roi, width);
/*  48 */       imp2 = new ImagePlus(WindowManager.getUniqueName(imp.getTitle()), ip2);
/*     */     }
/*  50 */     if (imp2 == null)
/*  51 */       return;
/*  52 */     Calibration cal = imp.getCalibration();
/*  53 */     if (cal.pixelWidth == cal.pixelHeight)
/*  54 */       imp2.setCalibration(cal);
/*  55 */     imp2.show();
/*     */ 
/*  61 */     if (isMacro) Line.setWidth(originalWidth);
/*     */   }
/*     */ 
/*     */   public ImageProcessor straighten(ImagePlus imp, Roi roi, int width)
/*     */   {
/*     */     ImageProcessor ip2;
/*     */     ImageProcessor ip2;
/*  66 */     if ((imp.getBitDepth() == 24) && (roi.getType() != 5)) {
/*  67 */       ip2 = straightenRGB(imp, width);
/*     */     }
/*     */     else
/*     */     {
/*     */       ImageProcessor ip2;
/*  68 */       if ((imp.isComposite()) && (((CompositeImage)imp).getMode() == 1)) {
/*  69 */         ip2 = straightenComposite(imp, width);
/*     */       }
/*     */       else
/*     */       {
/*     */         ImageProcessor ip2;
/*  70 */         if (roi.getType() == 5)
/*  71 */           ip2 = rotateLine(imp, width);
/*     */         else
/*  73 */           ip2 = straightenLine(imp, width); 
/*     */       }
/*     */     }
/*  74 */     return ip2;
/*     */   }
/*     */ 
/*     */   public ImageStack straightenStack(ImagePlus imp, Roi roi, int width) {
/*  78 */     int current = imp.getCurrentSlice();
/*  79 */     int n = imp.getStackSize();
/*  80 */     ImageStack stack2 = null;
/*  81 */     for (int i = 1; i <= n; i++) {
/*  82 */       IJ.showProgress(i, n);
/*  83 */       imp.setSlice(i);
/*  84 */       ImageProcessor ip2 = straighten(imp, roi, width);
/*  85 */       if (stack2 == null)
/*  86 */         stack2 = new ImageStack(ip2.getWidth(), ip2.getHeight());
/*  87 */       stack2.addSlice(null, ip2);
/*     */     }
/*  89 */     imp.setSlice(current);
/*  90 */     return stack2;
/*     */   }
/*     */ 
/*     */   public ImageProcessor straightenLine(ImagePlus imp, int width) {
/*  94 */     PolygonRoi roi = (PolygonRoi)imp.getRoi();
/*  95 */     if (roi == null) return null;
/*  96 */     if (roi.getState() == 0)
/*  97 */       roi.exitConstructingMode();
/*  98 */     boolean isSpline = roi.isSplineFit();
/*  99 */     int type = roi.getType();
/* 100 */     int n = roi.getNCoordinates();
/* 101 */     double len = roi.getLength();
/* 102 */     if ((!isSpline) || (Math.abs(1.0D - roi.getLength() / n) >= 0.5D))
/* 103 */       roi.fitSplineForStraightening();
/* 104 */     if (roi.getNCoordinates() < 2) return null;
/* 105 */     FloatPolygon p = roi.getFloatPolygon();
/* 106 */     n = p.npoints;
/* 107 */     ImageProcessor ip = imp.getProcessor();
/* 108 */     ImageProcessor ip2 = new FloatProcessor(n, width);
/* 109 */     ImageProcessor distances = null;
/* 110 */     if (IJ.debugMode) distances = new FloatProcessor(n, 1);
/* 111 */     float[] pixels = (float[])ip2.getPixels();
/*     */ 
/* 113 */     double x2 = p.xpoints[0] - (p.xpoints[1] - p.xpoints[0]);
/* 114 */     double y2 = p.ypoints[0] - (p.ypoints[1] - p.ypoints[0]);
/* 115 */     if (width == 1)
/* 116 */       ip2.putPixelValue(0, 0, ip.getInterpolatedValue(x2, y2));
/* 117 */     for (int i = 0; i < n; i++) {
/* 118 */       if ((!processStack) && (i % 10 == 0)) IJ.showProgress(i, n);
/* 119 */       double x1 = x2; double y1 = y2;
/* 120 */       x2 = p.xpoints[i]; y2 = p.ypoints[i];
/* 121 */       if (distances != null) distances.putPixelValue(i, 0, (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
/* 122 */       if (width == 1) {
/* 123 */         ip2.putPixelValue(i, 0, ip.getInterpolatedValue(x2, y2));
/*     */       }
/*     */       else {
/* 126 */         double dx = x2 - x1;
/* 127 */         double dy = y1 - y2;
/* 128 */         double length = (float)Math.sqrt(dx * dx + dy * dy);
/* 129 */         dx /= length;
/* 130 */         dy /= length;
/*     */ 
/* 132 */         double x = x2 - dy * width / 2.0D;
/* 133 */         double y = y2 - dx * width / 2.0D;
/* 134 */         int j = 0;
/* 135 */         int n2 = width;
/*     */         do {
/* 137 */           ip2.putPixelValue(i, j++, ip.getInterpolatedValue(x, y));
/*     */ 
/* 139 */           x += dy;
/* 140 */           y += dx;
/* 141 */           n2--; } while (n2 > 0);
/*     */       }
/*     */     }
/* 143 */     if (!processStack) IJ.showProgress(n, n);
/*     */ 
/* 145 */     if (!isSpline) {
/* 146 */       if (type == 7)
/* 147 */         roi.removeSplineFit();
/*     */       else
/* 149 */         imp.draw();
/*     */     }
/* 151 */     if (imp.getBitDepth() != 24) {
/* 152 */       ip2.setColorModel(ip.getColorModel());
/* 153 */       ip2.resetMinAndMax();
/*     */     }
/* 155 */     if (distances != null) {
/* 156 */       distances.resetMinAndMax();
/* 157 */       new ImagePlus("Distances", distances).show();
/*     */     }
/* 159 */     return ip2;
/*     */   }
/*     */ 
/*     */   public ImageProcessor rotateLine(ImagePlus imp, int width) {
/* 163 */     Roi roi = imp.getRoi();
/* 164 */     float saveStrokeWidth = roi.getStrokeWidth();
/* 165 */     roi.setStrokeWidth(1.0F);
/* 166 */     Polygon p = roi.getPolygon();
/* 167 */     roi.setStrokeWidth(saveStrokeWidth);
/* 168 */     imp.setRoi(new PolygonRoi(p.xpoints, p.ypoints, 2, 6));
/* 169 */     ImageProcessor ip2 = imp.getBitDepth() == 24 ? straightenRGB(imp, width) : straightenLine(imp, width);
/* 170 */     imp.setRoi(roi);
/* 171 */     return ip2;
/*     */   }
/*     */ 
/*     */   ImageProcessor straightenRGB(ImagePlus imp, int width) {
/* 175 */     int w = imp.getWidth(); int h = imp.getHeight();
/* 176 */     int size = w * h;
/* 177 */     byte[] r = new byte[size];
/* 178 */     byte[] g = new byte[size];
/* 179 */     byte[] b = new byte[size];
/* 180 */     ColorProcessor cp = (ColorProcessor)imp.getProcessor();
/* 181 */     cp.getRGB(r, g, b);
/* 182 */     ImagePlus imp2 = new ImagePlus("red", new ByteProcessor(w, h, r, null));
/* 183 */     imp2.setRoi((Roi)imp.getRoi().clone());
/* 184 */     ImageProcessor red = straightenLine(imp2, width);
/* 185 */     if (red == null) return null;
/* 186 */     imp2 = new ImagePlus("green", new ByteProcessor(w, h, g, null));
/* 187 */     imp2.setRoi((Roi)imp.getRoi().clone());
/* 188 */     ImageProcessor green = straightenLine(imp2, width);
/* 189 */     if (green == null) return null;
/* 190 */     imp2 = new ImagePlus("blue", new ByteProcessor(w, h, b, null));
/* 191 */     imp2.setRoi((Roi)imp.getRoi().clone());
/* 192 */     ImageProcessor blue = straightenLine(imp2, width);
/* 193 */     if (blue == null) return null;
/* 194 */     ColorProcessor cp2 = new ColorProcessor(red.getWidth(), red.getHeight());
/* 195 */     red = red.convertToByte(false);
/* 196 */     green = green.convertToByte(false);
/* 197 */     blue = blue.convertToByte(false);
/* 198 */     cp2.setRGB((byte[])red.getPixels(), (byte[])green.getPixels(), (byte[])blue.getPixels());
/* 199 */     imp.setRoi(imp2.getRoi());
/* 200 */     return cp2;
/*     */   }
/*     */ 
/*     */   ImageProcessor straightenComposite(ImagePlus imp, int width) {
/* 204 */     Image img = imp.getImage();
/* 205 */     ImagePlus imp2 = new ImagePlus("temp", new ColorProcessor(img));
/* 206 */     imp2.setRoi(imp.getRoi());
/* 207 */     ImageProcessor ip2 = straightenRGB(imp2, width);
/* 208 */     imp.setRoi(imp2.getRoi());
/* 209 */     return ip2;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Straightener
 * JD-Core Version:    0.6.2
 */