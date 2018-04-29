/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.LookUpTable;
/*     */ import ij.gui.GUI;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import java.awt.Button;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.CheckboxGroup;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.Label;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class CalibrationBar
/*     */   implements PlugInFilter
/*     */ {
/*     */   static final int BAR_LENGTH = 128;
/*     */   static final int BAR_THICKNESS = 12;
/*     */   static final int XMARGIN = 10;
/*     */   static final int YMARGIN = 10;
/*     */   static final int WIN_HEIGHT = 128;
/*     */   static final int BOX_PAD = 0;
/*     */   static final int LINE_WIDTH = 1;
/*  35 */   static int nBins = 256;
/*  36 */   static final String[] colors = { "White", "Light Gray", "Dark Gray", "Black", "Red", "Green", "Blue", "Yellow", "None" };
/*  37 */   static final String[] locations = { "Upper Right", "Lower Right", "Lower Left", "Upper Left", "At Selection" };
/*     */   static final int UPPER_RIGHT = 0;
/*     */   static final int LOWER_RIGHT = 1;
/*     */   static final int LOWER_LEFT = 2;
/*     */   static final int UPPER_LEFT = 3;
/*     */   static final int AT_SELECTION = 4;
/*  40 */   static String fillColor = colors[0];
/*  41 */   static String textColor = colors[3];
/*  42 */   static String location = locations[0];
/*  43 */   static double zoom = 1.0D;
/*  44 */   static int numLabels = 5;
/*  45 */   static int fontSize = 12;
/*  46 */   static int decimalPlaces = 0;
/*     */   ImagePlus imp;
/*     */   ImagePlus impOriginal;
/*     */   LiveDialog gd;
/*     */   ImageStatistics stats;
/*     */   Calibration cal;
/*     */   int[] histogram;
/*     */   LookUpTable lut;
/*     */   Image img;
/*     */   Button setup;
/*     */   Button redraw;
/*     */   Button insert;
/*     */   Button unInsert;
/*     */   Checkbox ne;
/*     */   Checkbox nw;
/*     */   Checkbox se;
/*     */   Checkbox sw;
/*     */   CheckboxGroup locGroup;
/*     */   Label value;
/*     */   Label note;
/*     */   int newMaxCount;
/*     */   boolean logScale;
/*     */   int win_width;
/*     */   int userPadding;
/*     */   int fontHeight;
/*     */   boolean boldText;
/*     */   Object backupPixels;
/*     */   byte[] byteStorage;
/*     */   int[] intStorage;
/*     */   short[] shortStorage;
/*     */   float[] floatStorage;
/*     */   String boxOutlineColor;
/*     */   String barOutlineColor;
/*     */   ImagePlus impData;
/*     */   ImageProcessor ip;
/*     */   String[] fieldNames;
/*     */   int insetPad;
/*     */   boolean decimalPlacesChanged;
/*     */ 
/*     */   public CalibrationBar()
/*     */   {
/*  65 */     this.userPadding = 0;
/*  66 */     this.fontHeight = 0;
/*  67 */     this.boldText = false;
/*     */ 
/*  73 */     this.boxOutlineColor = colors[8];
/*  74 */     this.barOutlineColor = colors[3];
/*     */ 
/*  78 */     this.fieldNames = null;
/*     */   }
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  83 */     if (imp != null) {
/*  84 */       this.imp = imp;
/*  85 */       this.impData = imp;
/*  86 */       if (imp.getRoi() != null)
/*  87 */         location = locations[4];
/*     */     }
/*  89 */     return 143;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ipPassed) {
/*  93 */     ImageCanvas ic = this.imp.getCanvas();
/*  94 */     double mag = ic != null ? ic.getMagnification() : 1.0D;
/*  95 */     if ((zoom <= 1.0D) && (mag < 1.0D))
/*  96 */       zoom = 1.0D / mag;
/*  97 */     this.ip = ipPassed.duplicate().convertToRGB();
/*  98 */     this.impOriginal = this.imp;
/*  99 */     this.imp = new ImagePlus(this.imp.getTitle() + " with bar", this.ip);
/* 100 */     this.imp.setCalibration(this.impData.getCalibration());
/* 101 */     if (this.impOriginal.getRoi() != null)
/* 102 */       this.imp.setRoi(this.impOriginal.getRoi());
/* 103 */     this.imp.show();
/* 104 */     this.ip.snapshot();
/* 105 */     this.insetPad = (this.imp.getWidth() / 50);
/* 106 */     if (this.insetPad < 4)
/* 107 */       this.insetPad = 4;
/* 108 */     updateColorBar();
/* 109 */     if (!showDialog()) {
/* 110 */       this.imp.hide();
/* 111 */       this.ip.reset();
/* 112 */       this.imp.updateAndDraw();
/* 113 */       return;
/*     */     }
/* 115 */     updateColorBar();
/*     */   }
/*     */ 
/*     */   private void updateColorBar()
/*     */   {
/* 120 */     this.ip.reset();
/* 121 */     Roi roi = this.imp.getRoi();
/* 122 */     if ((roi != null) && (location.equals(locations[4]))) {
/* 123 */       Rectangle r = roi.getBounds();
/* 124 */       drawColorBar(this.imp, r.x, r.y);
/* 125 */     } else if (location.equals(locations[3])) {
/* 126 */       drawColorBar(this.imp, this.insetPad, this.insetPad);
/* 127 */     } else if (location.equals(locations[0])) {
/* 128 */       calculateWidth();
/* 129 */       drawColorBar(this.imp, this.imp.getWidth() - this.insetPad - this.win_width, this.insetPad);
/* 130 */     } else if (location.equals(locations[2])) {
/* 131 */       drawColorBar(this.imp, this.insetPad, this.imp.getHeight() - (int)(128.0D * zoom + 2 * (int)(10.0D * zoom)) - (int)(this.insetPad * zoom));
/* 132 */     } else if (location.equals(locations[1])) {
/* 133 */       calculateWidth();
/* 134 */       drawColorBar(this.imp, this.imp.getWidth() - this.win_width - this.insetPad, this.imp.getHeight() - (int)(128.0D * zoom + 2 * (int)(10.0D * zoom)) - this.insetPad);
/*     */     }
/*     */ 
/* 138 */     this.imp.updateAndDraw();
/*     */   }
/*     */ 
/*     */   private boolean showDialog() {
/* 142 */     this.gd = new LiveDialog("Calibration Bar");
/* 143 */     this.gd.addChoice("Location:", locations, location);
/* 144 */     this.gd.addChoice("Fill Color: ", colors, fillColor);
/* 145 */     this.gd.addChoice("Label Color: ", colors, textColor);
/* 146 */     this.gd.addNumericField("Number of Labels:", numLabels, 0);
/* 147 */     this.gd.addNumericField("Decimal Places:", decimalPlaces, 0);
/* 148 */     this.gd.addNumericField("Font Size:", fontSize, 0);
/* 149 */     this.gd.addNumericField("Zoom Factor:", zoom, 1);
/* 150 */     this.gd.addCheckbox("Bold Text", this.boldText);
/* 151 */     this.gd.showDialog();
/* 152 */     if (this.gd.wasCanceled())
/* 153 */       return false;
/* 154 */     location = this.gd.getNextChoice();
/* 155 */     fillColor = this.gd.getNextChoice();
/* 156 */     textColor = this.gd.getNextChoice();
/* 157 */     numLabels = (int)this.gd.getNextNumber();
/* 158 */     decimalPlaces = (int)this.gd.getNextNumber();
/* 159 */     fontSize = (int)this.gd.getNextNumber();
/* 160 */     zoom = this.gd.getNextNumber();
/* 161 */     this.boldText = this.gd.getNextBoolean();
/* 162 */     return true;
/*     */   }
/*     */ 
/*     */   public void verticalColorBar(ImageProcessor ip, int x, int y, int thickness, int length) {
/* 166 */     int width = thickness;
/* 167 */     int height = length;
/*     */ 
/* 169 */     int mapSize = 0;
/* 170 */     ColorModel cm = this.lut.getColorModel();
/*     */     byte[] rLUT;
/*     */     byte[] gLUT;
/*     */     byte[] bLUT;
/* 171 */     if ((cm instanceof IndexColorModel)) {
/* 172 */       IndexColorModel m = (IndexColorModel)cm;
/* 173 */       mapSize = m.getMapSize();
/* 174 */       byte[] rLUT = new byte[mapSize];
/* 175 */       byte[] gLUT = new byte[mapSize];
/* 176 */       byte[] bLUT = new byte[mapSize];
/* 177 */       m.getReds(rLUT);
/* 178 */       m.getGreens(gLUT);
/* 179 */       m.getBlues(bLUT);
/*     */     } else {
/* 181 */       mapSize = 256;
/* 182 */       rLUT = new byte[mapSize];
/* 183 */       gLUT = new byte[mapSize];
/* 184 */       bLUT = new byte[mapSize];
/* 185 */       for (int i = 0; i < mapSize; i++) {
/* 186 */         rLUT[i] = ((byte)i);
/* 187 */         gLUT[i] = ((byte)i);
/* 188 */         bLUT[i] = ((byte)i);
/*     */       }
/*     */     }
/* 191 */     double colors = mapSize;
/* 192 */     int start = 0;
/* 193 */     ImageProcessor ipOrig = this.impOriginal.getProcessor();
/* 194 */     if ((ipOrig instanceof ByteProcessor)) {
/* 195 */       int min = (int)ipOrig.getMin();
/* 196 */       if (min < 0) min = 0;
/* 197 */       int max = (int)ipOrig.getMax();
/* 198 */       if (max > 255) max = 255;
/* 199 */       colors = max - min + 1;
/* 200 */       start = min;
/*     */     }
/* 202 */     for (int i = 0; i < (int)(128.0D * zoom); i++) {
/* 203 */       int iMap = start + (int)Math.round(i * colors / (128.0D * zoom));
/* 204 */       if (iMap >= mapSize) {
/* 205 */         iMap = mapSize - 1;
/*     */       }
/* 207 */       ip.setColor(new Color(rLUT[iMap] & 0xFF, gLUT[iMap] & 0xFF, bLUT[iMap] & 0xFF));
/* 208 */       int j = (int)(128.0D * zoom) - i - 1;
/* 209 */       ip.drawLine(x, j + y, thickness + x, j + y);
/*     */     }
/*     */ 
/* 212 */     Color c = getColor(this.barOutlineColor);
/* 213 */     if (c != null) {
/* 214 */       ip.setColor(c);
/* 215 */       ip.moveTo(x, y);
/* 216 */       ip.lineTo(x + width, y);
/* 217 */       ip.lineTo(x + width, y + height);
/* 218 */       ip.lineTo(x, y + height);
/* 219 */       ip.lineTo(x, y);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void drawColorBar(ImageProcessor ip, int xOffset, int yOffset)
/*     */   {
/* 226 */     ip.setColor(Color.black);
/* 227 */     if (decimalPlaces == -1)
/* 228 */       decimalPlaces = Analyzer.getPrecision();
/* 229 */     int x = (int)(10.0D * zoom) + xOffset;
/* 230 */     int y = (int)(10.0D * zoom) + yOffset;
/*     */ 
/* 232 */     verticalColorBar(ip, x, y, (int)(12.0D * zoom), (int)(128.0D * zoom));
/* 233 */     drawText(ip, x + (int)(12.0D * zoom), y, true);
/*     */ 
/* 235 */     Color c = getColor(this.boxOutlineColor);
/* 236 */     if ((c != null) && (!fillColor.equals("None"))) {
/* 237 */       ip.setColor(c);
/* 238 */       ip.setLineWidth(1);
/* 239 */       ip.moveTo(xOffset + 0, yOffset + 0);
/* 240 */       ip.lineTo(xOffset + this.win_width - 0, yOffset + 0);
/* 241 */       ip.lineTo(xOffset + this.win_width - 0, yOffset + (int)(128.0D * zoom + 2 * (int)(10.0D * zoom)) - 0);
/* 242 */       ip.lineTo(xOffset + 0, yOffset + (int)(128.0D * zoom + 2 * (int)(10.0D * zoom)) - 0);
/* 243 */       ip.lineTo(xOffset + 0, yOffset + 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   int drawText(ImageProcessor ip, int x, int y, boolean active)
/*     */   {
/* 250 */     Color c = getColor(textColor);
/* 251 */     if (c == null)
/* 252 */       return 0;
/* 253 */     ip.setColor(c);
/*     */ 
/* 255 */     double hmin = this.cal.getCValue(this.stats.histMin);
/* 256 */     double hmax = this.cal.getCValue(this.stats.histMax);
/* 257 */     double barStep = 128.0D * zoom;
/* 258 */     if (numLabels > 2) {
/* 259 */       barStep /= (numLabels - 1);
/*     */     }
/* 261 */     int fontType = this.boldText ? 1 : 0;
/* 262 */     Font font = null;
/* 263 */     if (fontSize < 9)
/* 264 */       font = new Font("SansSerif", fontType, 9);
/*     */     else
/* 266 */       font = new Font("SansSerif", fontType, (int)(fontSize * zoom));
/* 267 */     ip.setFont(font);
/* 268 */     ip.setAntialiasedText(true);
/* 269 */     int maxLength = 0;
/*     */ 
/* 272 */     Image img = GUI.createBlankImage(128, 64);
/* 273 */     Graphics g = img.getGraphics();
/* 274 */     FontMetrics metrics = g.getFontMetrics(font);
/* 275 */     this.fontHeight = metrics.getHeight();
/*     */ 
/* 277 */     for (int i = 0; i < numLabels; i++) {
/* 278 */       double yLabelD = (int)(10.0D * zoom + 128.0D * zoom - i * barStep - 1.0D);
/* 279 */       int yLabel = (int)Math.round(y + 128.0D * zoom - i * barStep - 1.0D);
/* 280 */       Calibration cal = this.impOriginal.getCalibration();
/*     */ 
/* 282 */       ImageProcessor ipOrig = this.impOriginal.getProcessor();
/* 283 */       double min = ipOrig.getMin();
/* 284 */       double max = ipOrig.getMax();
/* 285 */       if ((ipOrig instanceof ByteProcessor)) {
/* 286 */         if (min < 0.0D) min = 0.0D;
/* 287 */         if (max > 255.0D) max = 255.0D;
/*     */       }
/*     */ 
/* 290 */       double grayLabel = min + (max - min) / (numLabels - 1) * i;
/* 291 */       if (cal.calibrated()) {
/* 292 */         grayLabel = cal.getCValue(grayLabel);
/* 293 */         double cmin = cal.getCValue(min);
/* 294 */         double cmax = cal.getCValue(max);
/* 295 */         if ((!this.decimalPlacesChanged) && (decimalPlaces == 0) && (((int)cmax != cmax) || ((int)cmin != cmin))) {
/* 296 */           decimalPlaces = 2;
/*     */         }
/*     */       }
/* 299 */       if (active) {
/* 300 */         ip.drawString(d2s(grayLabel), x + 5, yLabel + this.fontHeight / 2);
/*     */       }
/* 302 */       int iLength = metrics.stringWidth(d2s(grayLabel));
/* 303 */       if (iLength > maxLength) {
/* 304 */         maxLength = iLength;
/*     */       }
/*     */     }
/* 307 */     return maxLength;
/*     */   }
/*     */ 
/*     */   String d2s(double d) {
/* 311 */     return IJ.d2s(d, decimalPlaces);
/*     */   }
/*     */ 
/*     */   int getFontHeight() {
/* 315 */     Image img = GUI.createBlankImage(64, 64);
/* 316 */     Graphics g = img.getGraphics();
/* 317 */     int fontType = this.boldText ? 1 : 0;
/* 318 */     Font font = new Font("SansSerif", fontType, (int)(fontSize * zoom));
/* 319 */     FontMetrics metrics = g.getFontMetrics(font);
/* 320 */     return metrics.getHeight();
/*     */   }
/*     */ 
/*     */   Color getColor(String color) {
/* 324 */     Color c = Color.white;
/* 325 */     if (color.equals(colors[1]))
/* 326 */       c = Color.lightGray;
/* 327 */     else if (color.equals(colors[2]))
/* 328 */       c = Color.darkGray;
/* 329 */     else if (color.equals(colors[3]))
/* 330 */       c = Color.black;
/* 331 */     else if (color.equals(colors[4]))
/* 332 */       c = Color.red;
/* 333 */     else if (color.equals(colors[5]))
/* 334 */       c = Color.green;
/* 335 */     else if (color.equals(colors[6]))
/* 336 */       c = Color.blue;
/* 337 */     else if (color.equals(colors[7]))
/* 338 */       c = Color.yellow;
/* 339 */     else if (color.equals(colors[8]))
/* 340 */       c = null;
/* 341 */     return c;
/*     */   }
/*     */ 
/*     */   void calculateWidth() {
/* 345 */     drawColorBar(this.imp, -1, -1);
/*     */   }
/*     */ 
/*     */   public void drawColorBar(ImagePlus imp, int x, int y) {
/* 349 */     Roi roi = this.impOriginal.getRoi();
/* 350 */     if (roi != null)
/* 351 */       this.impOriginal.killRoi();
/* 352 */     this.stats = this.impOriginal.getStatistics(16, nBins);
/* 353 */     if (roi != null)
/* 354 */       this.impOriginal.setRoi(roi);
/* 355 */     this.histogram = this.stats.histogram;
/* 356 */     this.lut = this.impOriginal.createLut();
/* 357 */     this.cal = this.impOriginal.getCalibration();
/*     */ 
/* 359 */     int maxTextWidth = drawText(this.ip, 0, 0, false);
/* 360 */     this.win_width = ((int)(10.0D * zoom) + 5 + (int)(12.0D * zoom) + maxTextWidth + (int)(5.0D * zoom));
/* 361 */     if ((x == -1) && (y == -1)) {
/* 362 */       return;
/*     */     }
/* 364 */     Color c = getColor(fillColor);
/* 365 */     if (c != null) {
/* 366 */       this.ip.setColor(c);
/* 367 */       this.ip.setRoi(x, y, this.win_width, (int)(128.0D * zoom + 2 * (int)(10.0D * zoom)));
/* 368 */       this.ip.fill();
/*     */     }
/* 370 */     this.ip.resetRoi();
/*     */ 
/* 372 */     drawColorBar(this.ip, x, y);
/* 373 */     imp.updateAndDraw();
/*     */ 
/* 375 */     this.ip.setColor(Color.black);
/*     */   }
/*     */ 
/*     */   class LiveDialog extends GenericDialog
/*     */   {
/*     */     LiveDialog(String title)
/*     */     {
/* 383 */       super();
/*     */     }
/*     */ 
/*     */     public void textValueChanged(TextEvent e)
/*     */     {
/* 388 */       if (CalibrationBar.this.fieldNames == null) {
/* 389 */         CalibrationBar.this.fieldNames = new String[4];
/* 390 */         for (int i = 0; i < 4; i++) {
/* 391 */           CalibrationBar.this.fieldNames[i] = ((TextField)this.numberField.elementAt(i)).getName();
/*     */         }
/*     */       }
/* 394 */       TextField tf = (TextField)e.getSource();
/* 395 */       String name = tf.getName();
/* 396 */       String value = tf.getText();
/*     */ 
/* 398 */       if (value.equals("")) {
/* 399 */         return;
/*     */       }
/* 401 */       int i = 0;
/* 402 */       boolean needsRefresh = false;
/*     */ 
/* 404 */       if (name.equals(CalibrationBar.this.fieldNames[0]))
/*     */       {
/* 406 */         i = getValue(value).intValue();
/* 407 */         if (i < 1) {
/* 408 */           return;
/*     */         }
/* 410 */         needsRefresh = true;
/* 411 */         CalibrationBar.numLabels = i;
/*     */       }
/* 413 */       else if (name.equals(CalibrationBar.this.fieldNames[1])) {
/* 414 */         i = getValue(value).intValue();
/* 415 */         if (i < 0) {
/* 416 */           return;
/*     */         }
/* 418 */         needsRefresh = true;
/* 419 */         CalibrationBar.decimalPlaces = i;
/* 420 */         CalibrationBar.this.decimalPlacesChanged = true;
/*     */       }
/* 423 */       else if (name.equals(CalibrationBar.this.fieldNames[2])) {
/* 424 */         i = getValue(value).intValue();
/* 425 */         if (i < 1) {
/* 426 */           return;
/*     */         }
/* 428 */         needsRefresh = true;
/* 429 */         CalibrationBar.fontSize = i;
/*     */       }
/* 433 */       else if (name.equals(CalibrationBar.this.fieldNames[3])) {
/* 434 */         double d = 0.0D;
/* 435 */         d = getValue("0" + value).doubleValue();
/* 436 */         if (d <= 0.0D) {
/* 437 */           return;
/*     */         }
/* 439 */         needsRefresh = true;
/* 440 */         CalibrationBar.zoom = d;
/*     */       }
/*     */ 
/* 444 */       if (needsRefresh)
/* 445 */         CalibrationBar.this.updateColorBar();
/*     */     }
/*     */ 
/*     */     public void itemStateChanged(ItemEvent e)
/*     */     {
/* 450 */       CalibrationBar.location = ((Choice)this.choice.elementAt(0)).getSelectedItem();
/* 451 */       CalibrationBar.fillColor = ((Choice)this.choice.elementAt(1)).getSelectedItem();
/* 452 */       CalibrationBar.textColor = ((Choice)this.choice.elementAt(2)).getSelectedItem();
/* 453 */       CalibrationBar.this.boldText = ((Checkbox)this.checkbox.elementAt(0)).getState();
/* 454 */       CalibrationBar.this.updateColorBar();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.CalibrationBar
 * JD-Core Version:    0.6.2
 */