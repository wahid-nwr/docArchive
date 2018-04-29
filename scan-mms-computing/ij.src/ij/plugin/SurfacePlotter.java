/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.LookUpTable;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.Blitter;
/*     */ import ij.process.ByteBlitter;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorBlitter;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class SurfacePlotter
/*     */   implements PlugIn
/*     */ {
/*     */   static final int fontSize = 14;
/*  16 */   static int plotWidth = 350;
/*  17 */   static int polygonMultiplier = 100;
/*     */   static boolean oneToOne;
/*  19 */   static boolean firstTime = true;
/*     */ 
/*  21 */   static boolean showWireframe = false;
/*  22 */   static boolean showGrayscale = true;
/*  23 */   static boolean showAxis = true;
/*  24 */   static boolean whiteBackground = false;
/*  25 */   static boolean blackFill = false;
/*  26 */   static boolean smooth = true;
/*     */   ImagePlus img;
/*     */   int[] x;
/*     */   int[] y;
/*     */   boolean invertedLut;
/*  31 */   double angleInDegrees = 35.0D;
/*  32 */   double angle = this.angleInDegrees / 360.0D * 2.0D * 3.141592653589793D;
/*  33 */   double angle2InDegrees = 15.0D;
/*  34 */   double angle2 = this.angle2InDegrees / 360.0D * 2.0D * 3.141592653589793D;
/*  35 */   double yinc2 = Math.sin(this.angle2);
/*     */   double p1x;
/*     */   double p1y;
/*     */   double p2x;
/*     */   double p2y;
/*     */   double p3x;
/*     */   double p3y;
/*     */   LookUpTable lut;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  43 */     this.img = WindowManager.getCurrentImage();
/*  44 */     if (this.img == null) {
/*  45 */       IJ.noImage(); return;
/*  46 */     }if (this.img.getType() == 4) {
/*  47 */       IJ.error("Surface Plotter", "Grayscale or pseudo-color image required"); return;
/*  48 */     }this.invertedLut = this.img.getProcessor().isInvertedLut();
/*  49 */     if (firstTime) {
/*  50 */       if (this.invertedLut)
/*  51 */         whiteBackground = true;
/*  52 */       firstTime = false;
/*     */     }
/*  54 */     if (!showDialog()) {
/*  55 */       return;
/*     */     }
/*  57 */     int stackFlags = IJ.setupDialog(this.img, 0);
/*  58 */     if (stackFlags == 4096)
/*  59 */       return;
/*  60 */     Date start = new Date();
/*  61 */     this.lut = this.img.createLut();
/*     */ 
/*  63 */     if ((stackFlags == 32) && (this.img.getStack().getSize() > 1)) {
/*  64 */       ImageStack stackSource = this.img.getStack();
/*  65 */       ImageProcessor ip = stackSource.getProcessor(1);
/*  66 */       ImageProcessor plot = makeSurfacePlot(ip);
/*  67 */       ImageStack stack = new ImageStack(plot.getWidth(), plot.getHeight());
/*  68 */       stack.setColorModel(plot.getColorModel());
/*  69 */       for (int i = 1; i <= stackSource.getSize(); i++)
/*  70 */         stack.addSlice(null, new byte[plot.getWidth() * plot.getHeight()]);
/*  71 */       stack.setPixels(plot.getPixels(), 1);
/*  72 */       ImagePlus plots = new ImagePlus("Surface Plot", stack);
/*  73 */       plots.show();
/*  74 */       for (int i = 2; i <= stackSource.getSize(); i++) {
/*  75 */         IJ.showStatus("Drawing slice " + i + "..." + " (" + 100 * (i - 1) / stackSource.getSize() + "% done)");
/*  76 */         ip = stackSource.getProcessor(i);
/*  77 */         plot = makeSurfacePlot(ip);
/*  78 */         ImageWindow win = plots.getWindow();
/*  79 */         if ((win != null) && (win.isClosed())) break;
/*  80 */         stack.setPixels(plot.getPixels(), i);
/*  81 */         plots.setSlice(i);
/*     */       }
/*     */     } else {
/*  84 */       ImageProcessor plot = makeSurfacePlot(this.img.getProcessor());
/*  85 */       new ImagePlus("Surface Plot", plot).show();
/*     */     }
/*     */ 
/*  88 */     Date end = new Date();
/*  89 */     long lstart = start.getTime();
/*  90 */     long lend = end.getTime();
/*  91 */     long difference = lend - lstart;
/*  92 */     IJ.register(SurfacePlotter.class);
/*  93 */     IJ.showStatus("Done in " + difference + " msec.");
/*     */   }
/*     */ 
/*     */   boolean showDialog() {
/*  97 */     GenericDialog gd = new GenericDialog("Surface Plotter");
/*     */ 
/* 100 */     gd.addNumericField("Polygon Multiplier (10-200%):", polygonMultiplier, 0);
/* 101 */     gd.addCheckbox("Draw_Wireframe", showWireframe);
/* 102 */     gd.addCheckbox("Shade", showGrayscale);
/* 103 */     gd.addCheckbox("Draw_Axis", showAxis);
/* 104 */     gd.addCheckbox("Source Background is Lighter", whiteBackground);
/* 105 */     gd.addCheckbox("Fill Plot Background with Black", blackFill);
/* 106 */     gd.addCheckbox("One Polygon Per Line", oneToOne);
/* 107 */     gd.addCheckbox("Smooth", smooth);
/* 108 */     gd.showDialog();
/* 109 */     if (gd.wasCanceled()) {
/* 110 */       return false;
/*     */     }
/*     */ 
/* 113 */     polygonMultiplier = (int)gd.getNextNumber();
/* 114 */     showWireframe = gd.getNextBoolean();
/* 115 */     showGrayscale = gd.getNextBoolean();
/* 116 */     showAxis = gd.getNextBoolean();
/* 117 */     whiteBackground = gd.getNextBoolean();
/* 118 */     blackFill = gd.getNextBoolean();
/* 119 */     oneToOne = gd.getNextBoolean();
/* 120 */     smooth = gd.getNextBoolean();
/* 121 */     if ((showWireframe) && (!showGrayscale))
/* 122 */       blackFill = false;
/* 123 */     if (polygonMultiplier > 400) polygonMultiplier = 400;
/* 124 */     if (polygonMultiplier < 10) polygonMultiplier = 10;
/* 125 */     return true;
/*     */   }
/*     */ 
/*     */   public ImageProcessor makeSurfacePlot(ImageProcessor ip) {
/* 129 */     ip = ip.duplicate();
/* 130 */     Rectangle roi = this.img.getProcessor().getRoi();
/* 131 */     ip.setRoi(roi);
/* 132 */     if (!(ip instanceof ByteProcessor)) {
/* 133 */       ip.setMinAndMax(this.img.getProcessor().getMin(), this.img.getProcessor().getMax());
/* 134 */       ip = ip.convertToByte(true);
/* 135 */       ip.setRoi(roi);
/*     */     }
/* 137 */     double angle = this.angleInDegrees / 360.0D * 2.0D * 3.141592653589793D;
/* 138 */     int polygons = (int)(plotWidth * (polygonMultiplier / 100.0D) / 4.0D);
/* 139 */     if (oneToOne)
/* 140 */       polygons = roi.height;
/* 141 */     double xinc = 0.8D * plotWidth * Math.sin(angle) / polygons;
/* 142 */     double yinc = 0.8D * plotWidth * Math.cos(angle) / polygons;
/* 143 */     IJ.showProgress(0.01D);
/* 144 */     ip.setInterpolate(!oneToOne);
/* 145 */     ip = ip.resize(plotWidth, polygons);
/* 146 */     int width = ip.getWidth();
/* 147 */     int height = ip.getHeight();
/* 148 */     double min = ip.getMin();
/* 149 */     double max = ip.getMax();
/*     */ 
/* 151 */     if (this.invertedLut) ip.invert();
/* 152 */     if (whiteBackground) ip.invert();
/* 153 */     if (smooth) ip.smooth();
/*     */ 
/* 155 */     this.x = new int[width + 2];
/* 156 */     this.y = new int[width + 2];
/* 157 */     double xstart = 10.0D;
/* 158 */     if (xinc < 0.0D)
/* 159 */       xstart += Math.abs(xinc) * polygons;
/* 160 */     ByteProcessor ipProfile = new ByteProcessor(width, (int)(256.0D + width * this.yinc2));
/* 161 */     ipProfile.setValue(255.0D);
/* 162 */     ipProfile.fill();
/* 163 */     double ystart = this.yinc2 * width;
/* 164 */     int ybase = (int)(ystart + 0.5D);
/* 165 */     int windowWidth = (int)(plotWidth + polygons * Math.abs(xinc) + 20.0D);
/* 166 */     int windowHeight = (int)(ipProfile.getHeight() + polygons * yinc + 10.0D);
/*     */ 
/* 168 */     if (showAxis) {
/* 169 */       xstart += 70.0D;
/* 170 */       ystart += 10.0D;
/* 171 */       windowWidth += 80;
/* 172 */       windowHeight += 20;
/* 173 */       this.p1x = xstart;
/* 174 */       this.p1y = (ystart + 255.0D);
/* 175 */       this.p2x = (xstart + xinc * height);
/* 176 */       this.p2y = (this.p1y + yinc * height);
/* 177 */       this.p3x = (this.p2x + width - 1.0D);
/* 178 */       this.p3y = (this.p2y - this.yinc2 * width);
/*     */     }
/*     */ 
/* 181 */     if (showGrayscale)
/*     */     {
/* 183 */       int[] column = new int['ÿ'];
/* 184 */       for (int row = 0; row < 255; row++)
/*     */       {
/*     */         int v;
/*     */         int v;
/* 185 */         if (whiteBackground)
/* 186 */           v = row;
/*     */         else
/* 188 */           v = 255 - row;
/* 189 */         column[row] = v;
/*     */       }
/* 191 */       int base = ipProfile.getHeight() - 255;
/* 192 */       for (int col = 0; col < width; col++)
/* 193 */         ipProfile.putColumn(col, base - (int)(this.yinc2 * col + 0.5D), column, 255);
/*     */     }
/*     */     else {
/* 196 */       ipProfile.setValue(254.0D);
/* 197 */       ipProfile.fill();
/*     */     }
/*     */ 
/* 200 */     ipProfile.snapshot();
/*     */ 
/* 202 */     ImageProcessor ip2 = new ByteProcessor(windowWidth, windowHeight);
/* 203 */     if (showGrayscale) {
/* 204 */       ip2.setColorModel(ip.getColorModel());
/* 205 */       if (this.invertedLut)
/* 206 */         ip2.invertLut();
/* 207 */       fixLut(ip2);
/*     */     }
/* 209 */     if (!blackFill)
/* 210 */       ip2.setValue(255.0D);
/*     */     else
/* 212 */       ip2.setValue(0.0D);
/* 213 */     ip2.fill();
/*     */ 
/* 215 */     for (int row = 0; row < height; row++) {
/* 216 */       double[] profile = ip.getLine(0.0D, row, width - 1, row);
/* 217 */       clearAboveProfile(ipProfile, profile, width, this.yinc2);
/* 218 */       int ixstart = (int)(xstart + 0.5D);
/* 219 */       int iystart = (int)(ystart + 0.5D);
/*     */ 
/* 221 */       ip2.copyBits(ipProfile, ixstart, iystart - ybase, 2);
/* 222 */       ipProfile.reset();
/*     */ 
/* 224 */       if (showWireframe) {
/* 225 */         ip2.setValue(0.0D);
/* 226 */         double ydelta = 0.0D;
/* 227 */         ip2.moveTo(ixstart, (int)(ystart + 255.5D - profile[0]));
/* 228 */         for (int i = 1; i < width; i++) {
/* 229 */           ydelta += this.yinc2;
/* 230 */           ip2.lineTo(ixstart + i, (int)(ystart + 255.5D - (profile[i] + ydelta)));
/*     */         }
/* 232 */         ip2.drawLine(ixstart, iystart + 255, ixstart + width - 1, (int)(ystart + 255.5D - ydelta));
/* 233 */         ip2.drawLine(ixstart, iystart + 255 - (int)(profile[0] + 0.5D), ixstart, iystart + 255);
/* 234 */         ip2.drawLine(ixstart + width - 1, (int)(ystart + 255.5D - ydelta), ixstart + width - 1, (int)(ystart + 255.5D - (profile[(width - 1)] + ydelta)));
/*     */       }
/*     */ 
/* 237 */       xstart += xinc;
/* 238 */       ystart += yinc;
/* 239 */       if (row % 10 == 0) IJ.showProgress(row / height);
/*     */     }
/*     */ 
/* 242 */     IJ.showProgress(1.0D);
/*     */ 
/* 244 */     if (this.invertedLut) {
/* 245 */       ip.invert();
/* 246 */       ip.invertLut();
/*     */     }
/* 248 */     if (whiteBackground) {
/* 249 */       ip.invert();
/*     */     }
/* 251 */     if (showAxis) {
/* 252 */       if ((!this.lut.isGrayscale()) && (showGrayscale))
/* 253 */         ip2 = ip2.convertToRGB();
/* 254 */       drawAndLabelAxis(ip, ip2, roi);
/*     */     }
/*     */ 
/* 257 */     if (this.img.getStackSize() == 1) {
/* 258 */       ip2 = trimPlot(ip2, ybase);
/*     */     }
/* 260 */     return ip2;
/*     */   }
/*     */ 
/*     */   void drawAndLabelAxis(ImageProcessor ip, ImageProcessor ip2, Rectangle roi) {
/* 264 */     ip2.setFont(new Font("SansSerif", 0, 14));
/* 265 */     if (!blackFill)
/* 266 */       ip2.setColor(Color.black);
/*     */     else
/* 268 */       ip2.setColor(Color.white);
/* 269 */     ip2.setAntialiasedText(true);
/*     */ 
/* 272 */     Calibration cal = this.img.getCalibration();
/*     */ 
/* 275 */     String s = cal.getValueUnit();
/* 276 */     if (s.equals("Gray Value"))
/* 277 */       s = "";
/* 278 */     int w = ip2.getFontMetrics().stringWidth(s);
/* 279 */     drawAxis(ip2, (int)this.p1x, (int)this.p1y - 255, (int)this.p1x, (int)this.p1y, s, 10, -1, 0, 1);
/*     */     double max;
/*     */     double min;
/*     */     double max;
/* 281 */     if (this.img.getBitDepth() == 8) {
/* 282 */       double min = 0.0D;
/* 283 */       max = 255.0D;
/*     */     } else {
/* 285 */       min = this.img.getProcessor().getMin();
/* 286 */       max = this.img.getProcessor().getMax();
/*     */     }
/*     */ 
/* 293 */     if (cal.calibrated()) {
/* 294 */       min = cal.getCValue((int)min);
/* 295 */       max = cal.getCValue((int)max);
/*     */     }
/*     */ 
/* 299 */     ip2.setAntialiasedText(true);
/* 300 */     s = String.valueOf(Math.round(max * 10.0D) / 10.0D);
/* 301 */     w = ip.getFontMetrics().stringWidth(s);
/* 302 */     int h = ip.getFontMetrics().getHeight();
/* 303 */     ip2.drawString(s, (int)this.p1x - 18 - w, (int)this.p1y - 255 + h / 2);
/* 304 */     s = String.valueOf(Math.round(min * 10.0D) / 10.0D);
/* 305 */     w = ip2.getFontMetrics().stringWidth(s);
/* 306 */     ip2.drawString(s, (int)this.p1x - 18 - w, (int)this.p1y + h / 2);
/*     */ 
/* 309 */     s = Math.round(roi.height * cal.pixelHeight * 10.0D) / 10.0D + " " + cal.getUnits();
/* 310 */     w = ip2.getFontMetrics().stringWidth(s);
/* 311 */     drawAxis(ip2, (int)this.p1x, (int)this.p1y, (int)this.p2x, (int)this.p2y, s, 10, -1, 1, 1);
/*     */ 
/* 314 */     s = Math.round(roi.width * cal.pixelWidth * 10.0D) / 10.0D + " " + cal.getUnits();
/* 315 */     w = ip2.getFontMetrics().stringWidth(s);
/*     */ 
/* 317 */     drawAxis(ip2, (int)this.p2x, (int)this.p2y, (int)this.p3x, (int)this.p3y, s, 10, 1, -1, 1);
/*     */   }
/*     */ 
/*     */   void drawAxis(ImageProcessor ip, int x1, int y1, int x2, int y2, String label, int offset, int offsetXDirection, int offsetYDirection, int labelSide)
/*     */   {
/* 322 */     if (blackFill)
/* 323 */       ip.setColor(Color.white);
/*     */     else {
/* 325 */       ip.setColor(Color.black);
/*     */     }
/* 327 */     double m = -(y2 - y1) / (x2 - x1);
/*     */ 
/* 329 */     if (m == 0.0D)
/* 330 */       m = 0.0001D;
/* 331 */     double mTangent = -1.0D / m;
/* 332 */     double theta = Math.atan(mTangent);
/*     */ 
/* 334 */     int dy = -offsetXDirection * (int)(7.0D * Math.sin(theta));
/* 335 */     int dx = -offsetXDirection * (int)(7.0D * Math.cos(theta));
/*     */ 
/* 337 */     x1 += offsetXDirection * (int)(offset * Math.cos(theta));
/* 338 */     x2 += offsetXDirection * (int)(offset * Math.cos(theta));
/*     */ 
/* 340 */     y1 += offsetYDirection * (int)(offset * Math.sin(theta));
/* 341 */     y2 += offsetYDirection * (int)(offset * Math.sin(theta));
/*     */ 
/* 343 */     ip.drawLine(x1, y1, x2, y2);
/*     */ 
/* 345 */     ip.drawLine(x1, y1, x1 + dx, y1 - dy);
/* 346 */     ip.drawLine(x2, y2, x2 + dx, y2 - dy);
/* 347 */     ImageProcessor ipText = drawString(ip, label, (int)(Math.atan(m) / 2.0D / 3.141592653589793D * 360.0D));
/* 348 */     if (blackFill)
/* 349 */       ipText.invert();
/*     */     Blitter b;
/*     */     Blitter b;
/* 352 */     if ((ip instanceof ByteProcessor))
/* 353 */       b = new ByteBlitter((ByteProcessor)ip);
/*     */     else
/* 355 */       b = new ColorBlitter((ColorProcessor)ip);
/* 356 */     Color c = blackFill ? Color.black : Color.white;
/* 357 */     b.setTransparentColor(c);
/* 358 */     int xloc = (x1 + x2) / 2 - ipText.getWidth() / 2 + offsetXDirection * labelSide * (int)(15.0D * Math.cos(theta));
/* 359 */     int yloc = (y1 + y2) / 2 - ipText.getHeight() / 2 + offsetYDirection * labelSide * (int)(15.0D * Math.sin(theta));
/* 360 */     b.copyBits(ipText, xloc, yloc, 2);
/*     */   }
/*     */ 
/*     */   ImageProcessor drawString(ImageProcessor ip, String s, int a)
/*     */   {
/* 366 */     int w = ip.getFontMetrics().stringWidth(s);
/* 367 */     int h = ip.getFontMetrics().getHeight();
/*     */ 
/* 370 */     double r = Math.sqrt(w / 2 * (w / 2) + h / 2 * (h / 2));
/* 371 */     double aR = a / 360.0D * 2.0D * 3.141592653589793D;
/* 372 */     double aBaseR = Math.acos(w / 2 / r);
/*     */ 
/* 374 */     int ipW = (int)Math.abs(r * Math.cos(aBaseR + aR));
/* 375 */     int ipH = (int)Math.abs(r * Math.sin(aBaseR + aR));
/*     */ 
/* 377 */     if ((int)Math.abs(r * Math.cos(-aBaseR + aR)) > ipW)
/* 378 */       ipW = (int)Math.abs(r * Math.cos(-aBaseR + aR));
/* 379 */     if ((int)Math.abs(r * Math.sin(-aBaseR + aR)) > ipH) {
/* 380 */       ipH = (int)Math.abs(r * Math.sin(-aBaseR + aR));
/*     */     }
/* 382 */     ipW *= 2;
/* 383 */     ipH *= 2;
/*     */ 
/* 385 */     int tW = w;
/* 386 */     if (ipW > w)
/* 387 */       tW = ipW;
/* 388 */     ImageProcessor ipText = new ByteProcessor(tW, ipH);
/* 389 */     ipText.setFont(new Font("SansSerif", 0, 14));
/* 390 */     ipText.setColor(Color.white);
/* 391 */     ipText.fill();
/* 392 */     ipText.setColor(Color.black);
/* 393 */     ipText.setAntialiasedText(true);
/* 394 */     ipText.drawString(s, tW / 2 - w / 2, ipH / 2 + h / 2);
/* 395 */     ipText.setInterpolate(true);
/* 396 */     ipText.rotate(-a);
/* 397 */     ipText.setRoi(tW / 2 - ipW / 2, 0, ipW, ipH);
/* 398 */     ipText = ipText.crop();
/*     */ 
/* 403 */     return ipText;
/*     */   }
/*     */ 
/*     */   void clearAboveProfile(ImageProcessor ipProfile, double[] profile, int width, double yinc2) {
/* 407 */     byte[] pixels = (byte[])ipProfile.getPixels();
/* 408 */     double ydelta = 0.0D;
/* 409 */     int height = ipProfile.getHeight();
/* 410 */     for (int x = 0; x < width; x++) {
/* 411 */       ydelta += yinc2;
/* 412 */       int top = height - (int)(profile[x] + ydelta);
/* 413 */       int y = 0; for (int index = x; y < top; index += width) {
/* 414 */         pixels[index] = -1;
/*     */ 
/* 413 */         y++;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   ImageProcessor trimPlot(ImageProcessor plot, int maxTrim) {
/* 419 */     int background = plot.getPixel(0, 0);
/* 420 */     int width = plot.getWidth();
/* 421 */     int height = plot.getHeight();
/* 422 */     int trim = maxTrim - 5;
/* 423 */     for (int y = 0; y < maxTrim - 5; y++)
/* 424 */       for (int x = 0; x < width; x++)
/* 425 */         if (plot.getPixel(x, y) != background) {
/* 426 */           trim = y - 5; break label78;
/*     */         }
/* 427 */     label78: if (trim > 10) {
/* 428 */       plot.setRoi(0, trim, width, height - trim);
/* 429 */       plot = plot.crop();
/*     */     }
/* 431 */     return plot;
/*     */   }
/*     */ 
/*     */   void fixLut(ImageProcessor ip)
/*     */   {
/* 436 */     if ((!this.lut.isGrayscale()) && (this.lut.getMapSize() == 256))
/*     */     {
/* 438 */       for (int y = 0; y < ip.getHeight(); y++) {
/* 439 */         for (int x = 0; x < ip.getWidth(); x++) {
/* 440 */           if (ip.getPixelValue(x, y) == 0.0F)
/* 441 */             ip.putPixelValue(x, y, 1.0D);
/* 442 */           else if (ip.getPixelValue(x, y) == 255.0F) {
/* 443 */             ip.putPixelValue(x, y, 254.0D);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 449 */       byte[] rLUT = this.lut.getReds();
/* 450 */       byte[] gLUT = this.lut.getGreens();
/* 451 */       byte[] bLUT = this.lut.getBlues();
/*     */ 
/* 453 */       rLUT[0] = 0;
/* 454 */       gLUT[0] = 0;
/* 455 */       bLUT[0] = 0;
/* 456 */       rLUT['ÿ'] = -1;
/* 457 */       gLUT['ÿ'] = -1;
/* 458 */       bLUT['ÿ'] = -1;
/*     */ 
/* 460 */       ip.setColorModel(new IndexColorModel(8, 256, rLUT, gLUT, bLUT));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.SurfacePlotter
 * JD-Core Version:    0.6.2
 */