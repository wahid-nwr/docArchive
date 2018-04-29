/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.gui.EllipseRoi;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.PointRoi;
/*     */ import ij.gui.PolygonRoi;
/*     */ import ij.gui.Roi;
/*     */ import ij.io.FileInfo;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.text.TextWindow;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class Info
/*     */   implements PlugInFilter
/*     */ {
/*     */   private ImagePlus imp;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  17 */     this.imp = imp;
/*  18 */     return 159;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  22 */     String info = getImageInfo(this.imp, ip);
/*  23 */     if (info.indexOf("----") > 0)
/*  24 */       showInfo(info, 450, 500);
/*     */     else
/*  26 */       showInfo(info, 300, 300);
/*     */   }
/*     */ 
/*     */   public String getImageInfo(ImagePlus imp, ImageProcessor ip) {
/*  30 */     String infoProperty = null;
/*  31 */     if (imp.getStackSize() > 1) {
/*  32 */       ImageStack stack = imp.getStack();
/*  33 */       String label = stack.getSliceLabel(imp.getCurrentSlice());
/*  34 */       if ((label != null) && (label.indexOf('\n') > 0))
/*  35 */         infoProperty = label;
/*     */     }
/*  37 */     if (infoProperty == null)
/*  38 */       infoProperty = (String)imp.getProperty("Info");
/*  39 */     String info = getInfo(imp, ip);
/*  40 */     if (infoProperty != null) {
/*  41 */       return infoProperty + "\n------------------------\n" + info;
/*     */     }
/*  43 */     return info;
/*     */   }
/*     */ 
/*     */   String getInfo(ImagePlus imp, ImageProcessor ip) {
/*  47 */     String s = new String("\n");
/*  48 */     s = s + "Title: " + imp.getTitle() + "\n";
/*  49 */     Calibration cal = imp.getCalibration();
/*  50 */     int stackSize = imp.getStackSize();
/*  51 */     int channels = imp.getNChannels();
/*  52 */     int slices = imp.getNSlices();
/*  53 */     int frames = imp.getNFrames();
/*  54 */     int digits = imp.getBitDepth() == 32 ? 4 : 0;
/*  55 */     if (cal.scaled()) {
/*  56 */       String unit = cal.getUnit();
/*  57 */       String units = cal.getUnits();
/*  58 */       s = s + "Width:  " + IJ.d2s(imp.getWidth() * cal.pixelWidth, 2) + " " + units + " (" + imp.getWidth() + ")\n";
/*  59 */       s = s + "Height:  " + IJ.d2s(imp.getHeight() * cal.pixelHeight, 2) + " " + units + " (" + imp.getHeight() + ")\n";
/*  60 */       if (slices > 1)
/*  61 */         s = s + "Depth:  " + IJ.d2s(slices * cal.pixelDepth, 2) + " " + units + " (" + slices + ")\n";
/*  62 */       double xResolution = 1.0D / cal.pixelWidth;
/*  63 */       double yResolution = 1.0D / cal.pixelHeight;
/*  64 */       int places = Tools.getDecimalPlaces(xResolution, yResolution);
/*  65 */       if (xResolution == yResolution) {
/*  66 */         s = s + "Resolution:  " + IJ.d2s(xResolution, places) + " pixels per " + unit + "\n";
/*     */       } else {
/*  68 */         s = s + "X Resolution:  " + IJ.d2s(xResolution, places) + " pixels per " + unit + "\n";
/*  69 */         s = s + "Y Resolution:  " + IJ.d2s(yResolution, places) + " pixels per " + unit + "\n";
/*     */       }
/*     */     } else {
/*  72 */       s = s + "Width:  " + imp.getWidth() + " pixels\n";
/*  73 */       s = s + "Height:  " + imp.getHeight() + " pixels\n";
/*  74 */       if (stackSize > 1)
/*  75 */         s = s + "Depth:  " + slices + " pixels\n";
/*     */     }
/*  77 */     if (stackSize > 1)
/*  78 */       s = s + "Voxel size: " + d2s(cal.pixelWidth) + "x" + d2s(cal.pixelHeight) + "x" + d2s(cal.pixelDepth) + " " + cal.getUnit() + "\n";
/*     */     else {
/*  80 */       s = s + "Pixel size: " + d2s(cal.pixelWidth) + "x" + d2s(cal.pixelHeight) + " " + cal.getUnit() + "\n";
/*     */     }
/*  82 */     s = s + "ID: " + imp.getID() + "\n";
/*  83 */     String zOrigin = (stackSize > 1) || (cal.zOrigin != 0.0D) ? "," + d2s(cal.zOrigin) : "";
/*  84 */     s = s + "Coordinate origin:  " + d2s(cal.xOrigin) + "," + d2s(cal.yOrigin) + zOrigin + "\n";
/*  85 */     int type = imp.getType();
/*  86 */     switch (type) {
/*     */     case 0:
/*  88 */       s = s + "Bits per pixel: 8 ";
/*  89 */       String lut = "LUT";
/*  90 */       if (imp.getProcessor().isColorLut())
/*  91 */         lut = "color " + lut;
/*     */       else
/*  93 */         lut = "grayscale " + lut;
/*  94 */       if (imp.isInvertedLut())
/*  95 */         lut = "inverting " + lut;
/*  96 */       s = s + "(" + lut + ")\n";
/*  97 */       s = s + "Display range: " + (int)ip.getMin() + "-" + (int)ip.getMax() + "\n";
/*  98 */       break;
/*     */     case 1:
/*     */     case 2:
/* 100 */       if (type == 1) {
/* 101 */         String sign = cal.isSigned16Bit() ? "signed" : "unsigned";
/* 102 */         s = s + "Bits per pixel: 16 (" + sign + ")\n";
/*     */       } else {
/* 104 */         s = s + "Bits per pixel: 32 (float)\n";
/* 105 */       }s = s + "Display range: ";
/* 106 */       double min = ip.getMin();
/* 107 */       double max = ip.getMax();
/* 108 */       if (cal.calibrated()) {
/* 109 */         min = cal.getCValue((int)min);
/* 110 */         max = cal.getCValue((int)max);
/*     */       }
/* 112 */       s = s + IJ.d2s(min, digits) + " - " + IJ.d2s(max, digits) + "\n";
/* 113 */       break;
/*     */     case 3:
/* 115 */       s = s + "Bits per pixel: 8 (color LUT)\n";
/* 116 */       break;
/*     */     case 4:
/* 118 */       s = s + "Bits per pixel: 32 (RGB)\n";
/*     */     }
/*     */ 
/* 121 */     double interval = cal.frameInterval;
/* 122 */     double fps = cal.fps;
/* 123 */     if (stackSize > 1) {
/* 124 */       ImageStack stack = imp.getStack();
/* 125 */       int slice = imp.getCurrentSlice();
/* 126 */       String number = slice + "/" + stackSize;
/* 127 */       String label = stack.getShortSliceLabel(slice);
/* 128 */       if ((label != null) && (label.length() > 0))
/* 129 */         label = " (" + label + ")";
/*     */       else
/* 131 */         label = "";
/* 132 */       if ((interval > 0.0D) || (fps != 0.0D)) {
/* 133 */         s = s + "Frame: " + number + label + "\n";
/* 134 */         if (fps != 0.0D) {
/* 135 */           String sRate = Math.abs(fps - Math.round(fps)) < 1.E-05D ? IJ.d2s(fps, 0) : IJ.d2s(fps, 5);
/* 136 */           s = s + "Frame rate: " + sRate + " fps\n";
/*     */         }
/* 138 */         if (interval != 0.0D)
/* 139 */           s = s + "Frame interval: " + ((int)interval == interval ? IJ.d2s(interval, 0) : IJ.d2s(interval, 5)) + " " + cal.getTimeUnit() + "\n";
/*     */       } else {
/* 141 */         s = s + "Image: " + number + label + "\n";
/* 142 */       }if (imp.isHyperStack()) {
/* 143 */         if (channels > 1)
/* 144 */           s = s + "  Channel: " + imp.getChannel() + "/" + channels + "\n";
/* 145 */         if (slices > 1)
/* 146 */           s = s + "  Slice: " + imp.getSlice() + "/" + slices + "\n";
/* 147 */         if (frames > 1)
/* 148 */           s = s + "  Frame: " + imp.getFrame() + "/" + frames + "\n";
/*     */       }
/* 150 */       if (imp.isComposite()) {
/* 151 */         if ((!imp.isHyperStack()) && (channels > 1))
/* 152 */           s = s + "  Channels: " + channels + "\n";
/* 153 */         String mode = ((CompositeImage)imp).getModeAsString();
/* 154 */         s = s + "  Composite mode: \"" + mode + "\"\n";
/*     */       }
/*     */     }
/*     */ 
/* 158 */     if (ip.getMinThreshold() == -808080.0D) {
/* 159 */       s = s + "No Threshold\n";
/*     */     } else {
/* 161 */       double lower = ip.getMinThreshold();
/* 162 */       double upper = ip.getMaxThreshold();
/* 163 */       int dp = digits;
/* 164 */       if (cal.calibrated()) {
/* 165 */         lower = cal.getCValue((int)lower);
/* 166 */         upper = cal.getCValue((int)upper);
/* 167 */         dp = cal.isSigned16Bit() ? 0 : 4;
/*     */       }
/* 169 */       s = s + "Threshold: " + IJ.d2s(lower, dp) + "-" + IJ.d2s(upper, dp) + "\n";
/*     */     }
/* 171 */     ImageCanvas ic = imp.getCanvas();
/* 172 */     double mag = ic != null ? ic.getMagnification() : 1.0D;
/* 173 */     if (mag != 1.0D) {
/* 174 */       s = s + "Magnification: " + mag + "\n";
/*     */     }
/* 176 */     if (cal.calibrated()) {
/* 177 */       s = s + " \n";
/* 178 */       int curveFit = cal.getFunction();
/* 179 */       s = s + "Calibration Function: ";
/* 180 */       if (curveFit == 21)
/* 181 */         s = s + "Uncalibrated OD\n";
/* 182 */       else if (curveFit == 22)
/* 183 */         s = s + "Custom lookup table\n";
/*     */       else
/* 185 */         s = s + ij.measure.CurveFitter.fList[curveFit] + "\n";
/* 186 */       double[] c = cal.getCoefficients();
/* 187 */       if (c != null) {
/* 188 */         s = s + "  a: " + IJ.d2s(c[0], 6) + "\n";
/* 189 */         s = s + "  b: " + IJ.d2s(c[1], 6) + "\n";
/* 190 */         if (c.length >= 3)
/* 191 */           s = s + "  c: " + IJ.d2s(c[2], 6) + "\n";
/* 192 */         if (c.length >= 4)
/* 193 */           s = s + "  c: " + IJ.d2s(c[3], 6) + "\n";
/* 194 */         if (c.length >= 5)
/* 195 */           s = s + "  c: " + IJ.d2s(c[4], 6) + "\n";
/*     */       }
/* 197 */       s = s + "  Unit: \"" + cal.getValueUnit() + "\"\n";
/*     */     } else {
/* 199 */       s = s + "Uncalibrated\n";
/*     */     }
/* 201 */     FileInfo fi = imp.getOriginalFileInfo();
/* 202 */     if (fi != null) {
/* 203 */       if ((fi.url != null) && (!fi.url.equals("")))
/* 204 */         s = s + "URL: " + fi.url + "\n";
/* 205 */       else if ((fi.directory != null) && (fi.fileName != null)) {
/* 206 */         s = s + "Path: " + fi.directory + fi.fileName + "\n";
/*     */       }
/*     */     }
/* 209 */     Roi roi = imp.getRoi();
/* 210 */     if (roi == null) {
/* 211 */       if (cal.calibrated())
/* 212 */         s = s + " \n";
/* 213 */       s = s + "No Selection\n";
/* 214 */     } else if ((roi instanceof EllipseRoi)) {
/* 215 */       s = s + "\nElliptical Selection\n";
/* 216 */       double[] p = ((EllipseRoi)roi).getParams();
/* 217 */       double dx = p[2] - p[0];
/* 218 */       double dy = p[3] - p[1];
/* 219 */       double major = Math.sqrt(dx * dx + dy * dy);
/* 220 */       s = s + "  Major: " + IJ.d2s(major, 2) + "\n";
/* 221 */       s = s + "  Minor: " + IJ.d2s(major * p[4], 2) + "\n";
/* 222 */       s = s + "  X1: " + IJ.d2s(p[0], 2) + "\n";
/* 223 */       s = s + "  Y1: " + IJ.d2s(p[1], 2) + "\n";
/* 224 */       s = s + "  X2: " + IJ.d2s(p[2], 2) + "\n";
/* 225 */       s = s + "  Y2: " + IJ.d2s(p[3], 2) + "\n";
/* 226 */       s = s + "  Aspect ratio: " + IJ.d2s(p[4], 2) + "\n";
/*     */     } else {
/* 228 */       s = s + " \n";
/* 229 */       s = s + roi.getTypeAsString() + " Selection";
/* 230 */       String points = null;
/* 231 */       if ((roi instanceof PointRoi)) {
/* 232 */         int npoints = ((PolygonRoi)roi).getNCoordinates();
/* 233 */         String suffix = npoints > 1 ? "s)" : ")";
/* 234 */         points = " (" + npoints + " point" + suffix;
/*     */       }
/* 236 */       String name = roi.getName();
/* 237 */       if (name != null) {
/* 238 */         s = s + " (\"" + name + "\")";
/* 239 */         if (points != null) s = s + "\n " + points; 
/*     */       }
/* 240 */       else if (points != null) {
/* 241 */         s = s + points;
/* 242 */       }s = s + "\n";
/* 243 */       Rectangle r = roi.getBounds();
/* 244 */       if ((roi instanceof Line)) {
/* 245 */         Line line = (Line)roi;
/* 246 */         s = s + "  X1: " + IJ.d2s(line.x1d * cal.pixelWidth) + "\n";
/* 247 */         s = s + "  Y1: " + IJ.d2s(yy(line.y1d, imp) * cal.pixelHeight) + "\n";
/* 248 */         s = s + "  X2: " + IJ.d2s(line.x2d * cal.pixelWidth) + "\n";
/* 249 */         s = s + "  Y2: " + IJ.d2s(yy(line.y2d, imp) * cal.pixelHeight) + "\n";
/* 250 */       } else if (cal.scaled()) {
/* 251 */         s = s + "  X: " + IJ.d2s(r.x * cal.pixelWidth) + " (" + r.x + ")\n";
/* 252 */         s = s + "  Y: " + IJ.d2s(yy(r.y, imp) * cal.pixelHeight) + " (" + r.y + ")\n";
/* 253 */         s = s + "  Width: " + IJ.d2s(r.width * cal.pixelWidth) + " (" + r.width + ")\n";
/* 254 */         s = s + "  Height: " + IJ.d2s(r.height * cal.pixelHeight) + " (" + r.height + ")\n";
/*     */       } else {
/* 256 */         s = s + "  X: " + r.x + "\n";
/* 257 */         s = s + "  Y: " + yy(r.y, imp) + "\n";
/* 258 */         s = s + "  Width: " + r.width + "\n";
/* 259 */         s = s + "  Height: " + r.height + "\n";
/*     */       }
/*     */     }
/*     */ 
/* 263 */     return s;
/*     */   }
/*     */ 
/*     */   String d2s(double n) {
/* 267 */     return n == (int)n ? Integer.toString((int)n) : IJ.d2s(n);
/*     */   }
/*     */ 
/*     */   int yy(int y, ImagePlus imp)
/*     */   {
/* 272 */     return Analyzer.updateY(y, imp.getHeight());
/*     */   }
/*     */ 
/*     */   double yy(double y, ImagePlus imp)
/*     */   {
/* 277 */     return Analyzer.updateY(y, imp.getHeight());
/*     */   }
/*     */ 
/*     */   void showInfo(String info, int width, int height) {
/* 281 */     new TextWindow("Info for " + this.imp.getTitle(), info, width, height);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Info
 * JD-Core Version:    0.6.2
 */