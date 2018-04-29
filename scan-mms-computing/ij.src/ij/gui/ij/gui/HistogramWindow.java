/*     */ package ij.gui;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImageListener;
/*     */ import ij.ImagePlus;
/*     */ import ij.LookUpTable;
/*     */ import ij.WindowManager;
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.Measurements;
/*     */ import ij.measure.ResultsTable;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.process.LUT;
/*     */ import ij.text.TextWindow;
/*     */ import java.awt.Button;
/*     */ import java.awt.Color;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Label;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.datatransfer.Clipboard;
/*     */ import java.awt.datatransfer.ClipboardOwner;
/*     */ import java.awt.datatransfer.StringSelection;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.io.CharArrayWriter;
/*     */ import java.io.PrintWriter;
/*     */ 
/*     */ public class HistogramWindow extends ImageWindow
/*     */   implements Measurements, ActionListener, ClipboardOwner, MouseListener, MouseMotionListener, ImageListener, KeyListener, Runnable
/*     */ {
/*     */   static final int WIN_WIDTH = 300;
/*     */   static final int WIN_HEIGHT = 240;
/*     */   static final int HIST_WIDTH = 256;
/*     */   static final int HIST_HEIGHT = 128;
/*     */   static final int BAR_HEIGHT = 12;
/*     */   static final int XMARGIN = 20;
/*     */   static final int YMARGIN = 10;
/*     */   static final int INTENSITY = 0;
/*     */   static final int RED = 1;
/*     */   static final int GREEN = 2;
/*     */   static final int BLUE = 3;
/*     */   protected ImageStatistics stats;
/*     */   protected int[] histogram;
/*     */   protected LookUpTable lut;
/*  30 */   protected Rectangle frame = null;
/*     */   protected Button list;
/*     */   protected Button save;
/*     */   protected Button copy;
/*     */   protected Button log;
/*     */   protected Button live;
/*     */   protected Button rgb;
/*     */   protected Label value;
/*     */   protected Label count;
/*  33 */   protected static String defaultDirectory = null;
/*     */   protected int decimalPlaces;
/*     */   protected int digits;
/*     */   protected int newMaxCount;
/*  37 */   protected int plotScale = 1;
/*     */   protected boolean logScale;
/*     */   protected Calibration cal;
/*     */   protected int yMax;
/*  41 */   public static int nBins = 256;
/*     */   private int srcImageID;
/*     */   private ImagePlus srcImp;
/*     */   private Thread bgThread;
/*     */   private boolean doUpdate;
/*     */   private int channel;
/*     */   private String blankLabel;
/*     */ 
/*     */   public HistogramWindow(ImagePlus imp)
/*     */   {
/*  52 */     super(NewImage.createRGBImage("Histogram of " + imp.getShortTitle(), 300, 240, 1, 4));
/*  53 */     showHistogram(imp, 256, 0.0D, 0.0D);
/*     */   }
/*     */ 
/*     */   public HistogramWindow(String title, ImagePlus imp, int bins)
/*     */   {
/*  59 */     super(NewImage.createRGBImage(title, 300, 240, 1, 4));
/*  60 */     showHistogram(imp, bins, 0.0D, 0.0D);
/*     */   }
/*     */ 
/*     */   public HistogramWindow(String title, ImagePlus imp, int bins, double histMin, double histMax)
/*     */   {
/*  67 */     super(NewImage.createRGBImage(title, 300, 240, 1, 4));
/*  68 */     showHistogram(imp, bins, histMin, histMax);
/*     */   }
/*     */ 
/*     */   public HistogramWindow(String title, ImagePlus imp, int bins, double histMin, double histMax, int yMax)
/*     */   {
/*  73 */     super(NewImage.createRGBImage(title, 300, 240, 1, 4));
/*  74 */     this.yMax = yMax;
/*  75 */     showHistogram(imp, bins, histMin, histMax);
/*     */   }
/*     */ 
/*     */   public HistogramWindow(String title, ImagePlus imp, ImageStatistics stats)
/*     */   {
/*  80 */     super(NewImage.createRGBImage(title, 300, 240, 1, 4));
/*     */ 
/*  82 */     this.yMax = stats.histYMax;
/*  83 */     showHistogram(imp, stats);
/*     */   }
/*     */ 
/*     */   public void showHistogram(ImagePlus imp, int bins)
/*     */   {
/*  89 */     showHistogram(imp, bins, 0.0D, 0.0D);
/*     */   }
/*     */ 
/*     */   public void showHistogram(ImagePlus imp, int bins, double histMin, double histMax)
/*     */   {
/*  96 */     boolean limitToThreshold = (Analyzer.getMeasurements() & 0x100) != 0;
/*  97 */     if ((this.channel != 0) && (imp.getType() == 4)) {
/*  98 */       ColorProcessor cp = (ColorProcessor)imp.getProcessor();
/*  99 */       byte[] bytes = cp.getChannel(this.channel);
/* 100 */       ImageProcessor ip = new ByteProcessor(imp.getWidth(), imp.getHeight(), bytes, null);
/* 101 */       ImagePlus imp2 = new ImagePlus("", ip);
/* 102 */       imp2.setRoi(imp.getRoi());
/* 103 */       this.stats = imp2.getStatistics(27, bins, histMin, histMax);
/*     */     } else {
/* 105 */       this.stats = imp.getStatistics(27 + (limitToThreshold ? 256 : 0), bins, histMin, histMax);
/* 106 */     }showHistogram(imp, this.stats);
/*     */   }
/*     */ 
/*     */   public void showHistogram(ImagePlus imp, ImageStatistics stats)
/*     */   {
/* 111 */     if (this.list == null)
/* 112 */       setup(imp);
/* 113 */     this.stats = stats;
/* 114 */     this.cal = imp.getCalibration();
/* 115 */     boolean limitToThreshold = (Analyzer.getMeasurements() & 0x100) != 0;
/* 116 */     imp.getMask();
/* 117 */     this.histogram = stats.histogram;
/* 118 */     if ((limitToThreshold) && (this.histogram.length == 256)) {
/* 119 */       ImageProcessor ip = imp.getProcessor();
/* 120 */       if (ip.getMinThreshold() != -808080.0D) {
/* 121 */         int lower = scaleDown(ip, ip.getMinThreshold());
/* 122 */         int upper = scaleDown(ip, ip.getMaxThreshold());
/* 123 */         for (int i = 0; i < lower; i++)
/* 124 */           this.histogram[i] = 0;
/* 125 */         for (int i = upper + 1; i < 256; i++)
/* 126 */           this.histogram[i] = 0;
/*     */       }
/*     */     }
/* 129 */     this.lut = imp.createLut();
/* 130 */     int type = imp.getType();
/* 131 */     boolean fixedRange = (type == 0) || (type == 3) || (type == 4);
/* 132 */     ImageProcessor ip = this.imp.getProcessor();
/* 133 */     ip.setColor(Color.white);
/* 134 */     ip.resetRoi();
/* 135 */     ip.fill();
/* 136 */     ImageProcessor srcIP = imp.getProcessor();
/* 137 */     drawHistogram(imp, ip, fixedRange, stats.histMin, stats.histMax);
/* 138 */     this.imp.updateAndDraw();
/*     */   }
/*     */ 
/*     */   private void setup(ImagePlus imp) {
/* 142 */     boolean isRGB = imp.getType() == 4;
/* 143 */     Panel buttons = new Panel();
/* 144 */     int hgap = (IJ.isMacOSX()) || (isRGB) ? 1 : 5;
/* 145 */     buttons.setLayout(new FlowLayout(2, hgap, 0));
/* 146 */     this.list = new Button("List");
/* 147 */     this.list.addActionListener(this);
/* 148 */     buttons.add(this.list);
/* 149 */     this.copy = new Button("Copy");
/* 150 */     this.copy.addActionListener(this);
/* 151 */     buttons.add(this.copy);
/* 152 */     this.log = new Button("Log");
/* 153 */     this.log.addActionListener(this);
/* 154 */     buttons.add(this.log);
/* 155 */     this.live = new Button("Live");
/* 156 */     this.live.addActionListener(this);
/* 157 */     buttons.add(this.live);
/* 158 */     if ((imp != null) && (isRGB)) {
/* 159 */       this.rgb = new Button("RGB");
/* 160 */       this.rgb.addActionListener(this);
/* 161 */       buttons.add(this.rgb);
/*     */     }
/* 163 */     if ((!IJ.isMacOSX()) || (!isRGB)) {
/* 164 */       Panel valueAndCount = new Panel();
/* 165 */       valueAndCount.setLayout(new GridLayout(2, 1, 0, 0));
/* 166 */       this.blankLabel = (IJ.isMacOSX() ? "         " : "                ");
/* 167 */       this.value = new Label(this.blankLabel);
/* 168 */       Font font = new Font("Monospaced", 0, 12);
/* 169 */       this.value.setFont(font);
/* 170 */       valueAndCount.add(this.value);
/* 171 */       this.count = new Label(this.blankLabel);
/* 172 */       this.count.setFont(font);
/* 173 */       valueAndCount.add(this.count);
/* 174 */       buttons.add(valueAndCount);
/*     */     }
/* 176 */     add(buttons);
/* 177 */     pack();
/*     */   }
/*     */   public void setup() {
/* 180 */     setup(null);
/*     */   }
/*     */   public void mouseMoved(int x, int y) {
/* 183 */     if ((this.value == null) || (this.count == null))
/* 184 */       return;
/* 185 */     if ((this.frame != null) && (x >= this.frame.x) && (x <= this.frame.x + this.frame.width)) {
/* 186 */       x -= this.frame.x;
/* 187 */       if (x > 255) x = 255;
/* 188 */       int index = (int)(x * this.histogram.length / 256.0D);
/* 189 */       String vlabel = null; String clabel = null;
/* 190 */       if (this.blankLabel.length() == 9) {
/* 191 */         vlabel = " "; clabel = " ";
/*     */       } else {
/* 193 */         vlabel = " value="; clabel = " count=";
/* 194 */       }String v = vlabel + ResultsTable.d2s(this.cal.getCValue(this.stats.histMin + index * this.stats.binSize), this.digits) + this.blankLabel;
/* 195 */       String c = clabel + this.histogram[index] + this.blankLabel;
/* 196 */       int len = vlabel.length() + this.blankLabel.length();
/* 197 */       this.value.setText(v.substring(0, len));
/* 198 */       this.count.setText(c.substring(0, len));
/*     */     } else {
/* 200 */       this.value.setText(this.blankLabel);
/* 201 */       this.count.setText(this.blankLabel);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void drawHistogram(ImageProcessor ip, boolean fixedRange) {
/* 206 */     drawHistogram(null, ip, fixedRange, 0.0D, 0.0D);
/*     */   }
/*     */ 
/*     */   void drawHistogram(ImagePlus imp, ImageProcessor ip, boolean fixedRange, double xMin, double xMax)
/*     */   {
/* 211 */     int maxCount2 = 0;
/* 212 */     int mode2 = 0;
/*     */ 
/* 215 */     ip.setColor(Color.black);
/* 216 */     ip.setLineWidth(1);
/* 217 */     this.decimalPlaces = Analyzer.getPrecision();
/* 218 */     this.digits = ((this.cal.calibrated()) || (this.stats.binSize != 1.0D) ? this.decimalPlaces : 0);
/* 219 */     int saveModalCount = this.histogram[this.stats.mode];
/* 220 */     for (int i = 0; i < this.histogram.length; i++) {
/* 221 */       if ((this.histogram[i] > maxCount2) && (i != this.stats.mode)) {
/* 222 */         maxCount2 = this.histogram[i];
/* 223 */         mode2 = i;
/*     */       }
/*     */     }
/* 226 */     this.newMaxCount = this.stats.maxCount;
/* 227 */     if ((this.newMaxCount > maxCount2 * 2) && (maxCount2 != 0)) {
/* 228 */       this.newMaxCount = ((int)(maxCount2 * 1.5D));
/*     */     }
/*     */ 
/* 231 */     if ((this.logScale) || ((IJ.shiftKeyDown()) && (!liveMode())))
/* 232 */       drawLogPlot(this.yMax > 0 ? this.yMax : this.newMaxCount, ip);
/* 233 */     drawPlot(this.yMax > 0 ? this.yMax : this.newMaxCount, ip);
/* 234 */     this.histogram[this.stats.mode] = saveModalCount;
/* 235 */     int x = 21;
/* 236 */     int y = 140;
/* 237 */     if (imp == null)
/* 238 */       this.lut.drawUnscaledColorBar(ip, x - 1, y, 256, 12);
/*     */     else
/* 240 */       drawAlignedColorBar(imp, xMin, xMax, ip, x - 1, y, 256, 12);
/* 241 */     y += 27;
/* 242 */     drawText(ip, x, y, fixedRange);
/* 243 */     this.srcImageID = imp.getID();
/*     */   }
/*     */ 
/*     */   void drawAlignedColorBar(ImagePlus imp, double xMin, double xMax, ImageProcessor ip, int x, int y, int width, int height) {
/* 247 */     ImageProcessor ipSource = imp.getProcessor();
/* 248 */     float[] pixels = null;
/* 249 */     ImageProcessor ipRamp = null;
/* 250 */     if ((ipSource instanceof ColorProcessor)) {
/* 251 */       ipRamp = new FloatProcessor(width, height);
/* 252 */       if (this.channel == 1)
/* 253 */         ipRamp.setColorModel(LUT.createLutFromColor(Color.red));
/* 254 */       else if (this.channel == 2)
/* 255 */         ipRamp.setColorModel(LUT.createLutFromColor(Color.green));
/* 256 */       else if (this.channel == 3)
/* 257 */         ipRamp.setColorModel(LUT.createLutFromColor(Color.blue));
/* 258 */       pixels = (float[])ipRamp.getPixels();
/*     */     } else {
/* 260 */       pixels = new float[width * height];
/* 261 */     }for (int j = 0; j < height; j++) {
/* 262 */       for (int i = 0; i < width; i++)
/* 263 */         pixels[(i + width * j)] = ((float)(xMin + i * (xMax - xMin) / (width - 1)));
/*     */     }
/* 265 */     if (!(ipSource instanceof ColorProcessor)) {
/* 266 */       ColorModel cm = null;
/* 267 */       if (imp.isComposite())
/* 268 */         cm = ((CompositeImage)imp).getChannelLut();
/* 269 */       else if (ipSource.getMinThreshold() == -808080.0D)
/* 270 */         cm = ipSource.getColorModel();
/*     */       else
/* 272 */         cm = ipSource.getCurrentColorModel();
/* 273 */       ipRamp = new FloatProcessor(width, height, pixels, cm);
/*     */     }
/* 275 */     double min = ipSource.getMin();
/* 276 */     double max = ipSource.getMax();
/* 277 */     ipRamp.setMinAndMax(min, max);
/* 278 */     ImageProcessor bar = null;
/* 279 */     if ((ip instanceof ColorProcessor))
/* 280 */       bar = ipRamp.convertToRGB();
/*     */     else
/* 282 */       bar = ipRamp.convertToByte(true);
/* 283 */     ip.insert(bar, x, y);
/* 284 */     ip.setColor(Color.black);
/* 285 */     ip.drawRect(x - 1, y, width + 2, height);
/*     */   }
/*     */ 
/*     */   int scaleDown(ImageProcessor ip, double threshold)
/*     */   {
/* 290 */     double min = ip.getMin();
/* 291 */     double max = ip.getMax();
/* 292 */     if (max > min) {
/* 293 */       return (int)((threshold - min) / (max - min) * 255.0D);
/*     */     }
/* 295 */     return 0;
/*     */   }
/*     */ 
/*     */   void drawPlot(int maxCount, ImageProcessor ip) {
/* 299 */     if (maxCount == 0) maxCount = 1;
/* 300 */     this.frame = new Rectangle(20, 10, 256, 128);
/* 301 */     ip.drawRect(this.frame.x - 1, this.frame.y, this.frame.width + 2, this.frame.height + 1);
/*     */ 
/* 303 */     for (int i = 0; i < 256; i++) {
/* 304 */       int index = (int)(i * this.histogram.length / 256.0D);
/* 305 */       int y = (int)(128.0D * this.histogram[index] / maxCount);
/* 306 */       if (y > 128) y = 128;
/* 307 */       ip.drawLine(i + 20, 138, i + 20, 138 - y);
/*     */     }
/*     */   }
/*     */ 
/*     */   void drawLogPlot(int maxCount, ImageProcessor ip) {
/* 312 */     this.frame = new Rectangle(20, 10, 256, 128);
/* 313 */     ip.drawRect(this.frame.x - 1, this.frame.y, this.frame.width + 2, this.frame.height + 1);
/* 314 */     double max = Math.log(maxCount);
/* 315 */     ip.setColor(Color.gray);
/*     */ 
/* 317 */     for (int i = 0; i < 256; i++) {
/* 318 */       int index = (int)(i * this.histogram.length / 256.0D);
/* 319 */       int y = this.histogram[index] == 0 ? 0 : (int)(128.0D * Math.log(this.histogram[index]) / max);
/* 320 */       if (y > 128)
/* 321 */         y = 128;
/* 322 */       ip.drawLine(i + 20, 138, i + 20, 138 - y);
/*     */     }
/* 324 */     ip.setColor(Color.black);
/*     */   }
/*     */ 
/*     */   void drawText(ImageProcessor ip, int x, int y, boolean fixedRange) {
/* 328 */     ip.setFont(new Font("SansSerif", 0, 12));
/* 329 */     ip.setAntialiasedText(true);
/* 330 */     double hmin = this.cal.getCValue(this.stats.histMin);
/* 331 */     double hmax = this.cal.getCValue(this.stats.histMax);
/* 332 */     double range = hmax - hmin;
/* 333 */     if ((fixedRange) && (!this.cal.calibrated()) && (hmin == 0.0D) && (hmax == 255.0D))
/* 334 */       range = 256.0D;
/* 335 */     ip.drawString(d2s(hmin), x - 4, y);
/* 336 */     ip.drawString(d2s(hmax), x + 256 - getWidth(hmax, ip) + 10, y);
/*     */ 
/* 338 */     double binWidth = range / this.stats.nBins;
/* 339 */     binWidth = Math.abs(binWidth);
/* 340 */     boolean showBins = (binWidth != 1.0D) || (!fixedRange);
/* 341 */     int col1 = 25;
/* 342 */     int col2 = 148;
/* 343 */     int row1 = y + 25;
/* 344 */     if (showBins) row1 -= 8;
/* 345 */     int row2 = row1 + 15;
/* 346 */     int row3 = row2 + 15;
/* 347 */     int row4 = row3 + 15;
/* 348 */     long count = this.stats.longPixelCount > 0L ? this.stats.longPixelCount : this.stats.pixelCount;
/* 349 */     ip.drawString("Count: " + count, col1, row1);
/* 350 */     ip.drawString("Mean: " + d2s(this.stats.mean), col1, row2);
/* 351 */     ip.drawString("StdDev: " + d2s(this.stats.stdDev), col1, row3);
/* 352 */     ip.drawString("Mode: " + d2s(this.stats.dmode) + " (" + this.stats.maxCount + ")", col2, row3);
/* 353 */     ip.drawString("Min: " + d2s(this.stats.min), col2, row1);
/* 354 */     ip.drawString("Max: " + d2s(this.stats.max), col2, row2);
/*     */ 
/* 356 */     if (showBins) {
/* 357 */       ip.drawString("Bins: " + d2s(this.stats.nBins), col1, row4);
/* 358 */       ip.drawString("Bin Width: " + d2s(binWidth), col2, row4);
/*     */     }
/*     */   }
/*     */ 
/*     */   String d2s(double d) {
/* 363 */     if ((d == 1.7976931348623157E+308D) || (d == -1.797693134862316E+308D))
/* 364 */       return "0";
/* 365 */     if (Double.isNaN(d))
/* 366 */       return "NaN";
/* 367 */     if (Double.isInfinite(d))
/* 368 */       return "Infinity";
/* 369 */     if ((int)d == d) {
/* 370 */       return ResultsTable.d2s(d, 0);
/*     */     }
/* 372 */     return ResultsTable.d2s(d, this.decimalPlaces);
/*     */   }
/*     */ 
/*     */   int getWidth(double d, ImageProcessor ip) {
/* 376 */     return ip.getStringWidth(d2s(d));
/*     */   }
/*     */ 
/*     */   protected void showList() {
/* 380 */     StringBuffer sb = new StringBuffer();
/* 381 */     String vheading = this.stats.binSize == 1.0D ? "value" : "bin start";
/*     */     TextWindow tw;
/*     */     TextWindow tw;
/* 382 */     if ((this.cal.calibrated()) && (!this.cal.isSigned16Bit())) {
/* 383 */       for (int i = 0; i < this.stats.nBins; i++)
/* 384 */         sb.append(i + "\t" + ResultsTable.d2s(this.cal.getCValue(this.stats.histMin + i * this.stats.binSize), this.digits) + "\t" + this.histogram[i] + "\n");
/* 385 */       tw = new TextWindow(getTitle(), "level\t" + vheading + "\tcount", sb.toString(), 200, 400);
/*     */     } else {
/* 387 */       for (int i = 0; i < this.stats.nBins; i++)
/* 388 */         sb.append(ResultsTable.d2s(this.cal.getCValue(this.stats.histMin + i * this.stats.binSize), this.digits) + "\t" + this.histogram[i] + "\n");
/* 389 */       tw = new TextWindow(getTitle(), vheading + "\tcount", sb.toString(), 200, 400);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void copyToClipboard() {
/* 394 */     Clipboard systemClipboard = null;
/*     */     try { systemClipboard = getToolkit().getSystemClipboard(); } catch (Exception e) {
/* 396 */       systemClipboard = null;
/* 397 */     }if (systemClipboard == null) {
/* 398 */       IJ.error("Unable to copy to Clipboard."); return;
/* 399 */     }IJ.showStatus("Copying histogram values...");
/* 400 */     CharArrayWriter aw = new CharArrayWriter(this.stats.nBins * 4);
/* 401 */     PrintWriter pw = new PrintWriter(aw);
/* 402 */     for (int i = 0; i < this.stats.nBins; i++)
/* 403 */       pw.print(ResultsTable.d2s(this.cal.getCValue(this.stats.histMin + i * this.stats.binSize), this.digits) + "\t" + this.histogram[i] + "\n");
/* 404 */     String text = aw.toString();
/* 405 */     pw.close();
/* 406 */     StringSelection contents = new StringSelection(text);
/* 407 */     systemClipboard.setContents(contents, this);
/* 408 */     IJ.showStatus(text.length() + " characters copied to Clipboard");
/*     */   }
/*     */ 
/*     */   void replot() {
/* 412 */     ImageProcessor ip = this.imp.getProcessor();
/* 413 */     this.frame = new Rectangle(20, 10, 256, 128);
/* 414 */     ip.setColor(Color.white);
/* 415 */     ip.setRoi(this.frame.x - 1, this.frame.y, this.frame.width + 2, this.frame.height);
/* 416 */     ip.fill();
/* 417 */     ip.resetRoi();
/* 418 */     ip.setColor(Color.black);
/* 419 */     if (this.logScale) {
/* 420 */       drawLogPlot(this.yMax > 0 ? this.yMax : this.newMaxCount, ip);
/* 421 */       drawPlot(this.yMax > 0 ? this.yMax : this.newMaxCount, ip);
/*     */     } else {
/* 423 */       drawPlot(this.yMax > 0 ? this.yMax : this.newMaxCount, ip);
/* 424 */     }this.imp.updateAndDraw();
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 446 */     Object b = e.getSource();
/* 447 */     if (b == this.live) {
/* 448 */       toggleLiveMode();
/* 449 */     } else if (b == this.rgb) {
/* 450 */       changeChannel();
/* 451 */     } else if (b == this.list) {
/* 452 */       showList();
/* 453 */     } else if (b == this.copy) {
/* 454 */       copyToClipboard();
/* 455 */     } else if (b == this.log) {
/* 456 */       this.logScale = (!this.logScale);
/* 457 */       replot();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void lostOwnership(Clipboard clipboard, Transferable contents) {
/*     */   }
/*     */ 
/* 464 */   public int[] getHistogram() { return this.histogram; }
/*     */ 
/*     */   public double[] getXValues()
/*     */   {
/* 468 */     double[] values = new double[this.stats.nBins];
/* 469 */     for (int i = 0; i < this.stats.nBins; i++)
/* 470 */       values[i] = this.cal.getCValue(this.stats.histMin + i * this.stats.binSize);
/* 471 */     return values;
/*     */   }
/*     */ 
/*     */   private void toggleLiveMode() {
/* 475 */     if (liveMode())
/* 476 */       removeListeners();
/*     */     else
/* 478 */       enableLiveMode();
/*     */   }
/*     */ 
/*     */   private void changeChannel() {
/* 482 */     ImagePlus imp = WindowManager.getImage(this.srcImageID);
/* 483 */     if ((imp == null) || (imp.getType() != 4)) {
/* 484 */       this.channel = 0;
/* 485 */       return;
/*     */     }
/* 487 */     this.channel += 1;
/* 488 */     if (this.channel > 3) this.channel = 0;
/* 489 */     showHistogram(imp, 256);
/* 490 */     String name = this.imp.getTitle();
/* 491 */     if (name.startsWith("Red ")) name = name.substring(4);
/* 492 */     else if (name.startsWith("Green ")) name = name.substring(6);
/* 493 */     else if (name.startsWith("Blue ")) name = name.substring(5);
/* 494 */     switch (this.channel) { case 0:
/* 495 */       this.imp.setTitle(name); break;
/*     */     case 1:
/* 496 */       this.imp.setTitle("Red " + name); break;
/*     */     case 2:
/* 497 */       this.imp.setTitle("Green " + name); break;
/*     */     case 3:
/* 498 */       this.imp.setTitle("Blue " + name);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean liveMode()
/*     */   {
/* 504 */     return (this.live != null) && (this.live.getForeground() == Color.red);
/*     */   }
/*     */ 
/*     */   private void enableLiveMode() {
/* 508 */     if (this.bgThread == null) {
/* 509 */       this.srcImp = WindowManager.getImage(this.srcImageID);
/* 510 */       if (this.srcImp == null) return;
/* 511 */       this.bgThread = new Thread(this, "Live Profiler");
/* 512 */       this.bgThread.setPriority(Math.max(this.bgThread.getPriority() - 3, 1));
/* 513 */       this.bgThread.start();
/* 514 */       imageUpdated(this.srcImp);
/*     */     }
/* 516 */     createListeners();
/* 517 */     if (this.srcImp != null)
/* 518 */       imageUpdated(this.srcImp);
/*     */   }
/*     */ 
/*     */   public synchronized void mousePressed(MouseEvent e) {
/* 522 */     this.doUpdate = true; notify(); } 
/* 523 */   public synchronized void mouseDragged(MouseEvent e) { this.doUpdate = true; notify(); } 
/* 524 */   public synchronized void mouseClicked(MouseEvent e) { this.doUpdate = true; notify(); }
/*     */ 
/*     */   public synchronized void keyPressed(KeyEvent e) {
/* 527 */     ImagePlus imp = WindowManager.getImage(this.srcImageID);
/* 528 */     if ((imp == null) || (imp.getRoi() != null)) {
/* 529 */       this.doUpdate = true;
/* 530 */       notify();
/*     */     }
/*     */   }
/*     */   public void mouseReleased(MouseEvent e) {
/*     */   }
/*     */   public void mouseExited(MouseEvent e) {
/*     */   }
/*     */   public void mouseEntered(MouseEvent e) {
/*     */   }
/*     */   public void mouseMoved(MouseEvent e) {
/*     */   }
/*     */   public void imageOpened(ImagePlus imp) {
/*     */   }
/*     */   public void keyTyped(KeyEvent e) {  }
/*     */ 
/*     */   public void keyReleased(KeyEvent e) {  } 
/* 545 */   public synchronized void imageUpdated(ImagePlus imp) { if (imp == this.srcImp) {
/* 546 */       this.doUpdate = true;
/* 547 */       notify();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void imageClosed(ImagePlus imp)
/*     */   {
/* 553 */     if ((imp == this.srcImp) || (imp == this.imp)) {
/* 554 */       if (this.bgThread != null)
/* 555 */         this.bgThread.interrupt();
/* 556 */       this.bgThread = null;
/* 557 */       removeListeners();
/* 558 */       this.srcImp = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     while (true) {
/* 565 */       if ((this.doUpdate) && (this.srcImp != null)) {
/* 566 */         if (this.srcImp.getRoi() != null)
/* 567 */           IJ.wait(50);
/* 568 */         showHistogram(this.srcImp, 256);
/*     */       }
/* 570 */       synchronized (this) {
/* 571 */         if (this.doUpdate)
/* 572 */           this.doUpdate = false;
/*     */         else try {
/* 574 */             wait();
/*     */           } catch (InterruptedException e) {
/* 576 */             return;
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createListeners()
/*     */   {
/* 585 */     if (this.srcImp == null) return;
/* 586 */     ImageCanvas ic = this.srcImp.getCanvas();
/* 587 */     if (ic == null) return;
/* 588 */     ic.addMouseListener(this);
/* 589 */     ic.addMouseMotionListener(this);
/* 590 */     ic.addKeyListener(this);
/* 591 */     ImagePlus.addImageListener(this);
/* 592 */     Font font = this.live.getFont();
/* 593 */     this.live.setFont(new Font(font.getName(), 1, font.getSize()));
/* 594 */     this.live.setForeground(Color.red);
/*     */   }
/*     */ 
/*     */   private void removeListeners()
/*     */   {
/* 599 */     if (this.srcImp == null) return;
/* 600 */     ImageCanvas ic = this.srcImp.getCanvas();
/* 601 */     ic.removeMouseListener(this);
/* 602 */     ic.removeMouseMotionListener(this);
/* 603 */     ic.removeKeyListener(this);
/* 604 */     ImagePlus.removeImageListener(this);
/* 605 */     Font font = this.live.getFont();
/* 606 */     this.live.setFont(new Font(font.getName(), 0, font.getSize()));
/* 607 */     this.live.setForeground(Color.black);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.HistogramWindow
 * JD-Core Version:    0.6.2
 */