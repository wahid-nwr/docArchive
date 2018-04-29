/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.TextRoi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class ScaleBar
/*     */   implements PlugIn
/*     */ {
/*  15 */   static final String[] locations = { "Upper Right", "Lower Right", "Lower Left", "Upper Left", "At Selection" };
/*     */   static final int UPPER_RIGHT = 0;
/*     */   static final int LOWER_RIGHT = 1;
/*     */   static final int LOWER_LEFT = 2;
/*     */   static final int UPPER_LEFT = 3;
/*     */   static final int AT_SELECTION = 4;
/*  17 */   static final String[] colors = { "White", "Black", "Light Gray", "Gray", "Dark Gray", "Red", "Green", "Blue", "Yellow" };
/*  18 */   static final String[] bcolors = { "None", "Black", "White", "Dark Gray", "Gray", "Light Gray", "Yellow", "Blue", "Green", "Red" };
/*  19 */   static final String[] checkboxLabels = { "Bold Text", "Hide Text", "Serif Font", "Overlay" };
/*     */   static double barWidth;
/*  21 */   static int defaultBarHeight = 4;
/*  22 */   static int barHeightInPixels = defaultBarHeight;
/*  23 */   static String location = locations[1];
/*  24 */   static String color = colors[0];
/*  25 */   static String bcolor = bcolors[0];
/*  26 */   static boolean boldText = true;
/*     */   static boolean hideText;
/*     */   static boolean createOverlay;
/*  29 */   static int defaultFontSize = 14;
/*     */   static int fontSize;
/*     */   static boolean labelAll;
/*     */   ImagePlus imp;
/*     */   double imageWidth;
/*     */   double mag;
/*     */   int xloc;
/*     */   int yloc;
/*     */   int barWidthInPixels;
/*     */   int roiX;
/*     */   int roiY;
/*     */   int roiWidth;
/*     */   int roiHeight;
/*     */   boolean serifFont;
/*     */   boolean[] checkboxStates;
/*     */   boolean showingOverlay;
/*     */   boolean drawnScaleBar;
/*     */   Overlay baseOverlay;
/*     */ 
/*     */   public ScaleBar()
/*     */   {
/*  37 */     this.roiX = -1;
/*     */ 
/*  39 */     this.checkboxStates = new boolean[4];
/*     */   }
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  45 */     this.imp = WindowManager.getCurrentImage();
/*  46 */     if (this.imp != null) {
/*  47 */       this.baseOverlay = this.imp.getOverlay();
/*  48 */       if ((showDialog(this.imp)) && (this.imp.getStackSize() > 1) && (labelAll))
/*  49 */         labelSlices(this.imp);
/*     */     } else {
/*  51 */       IJ.noImage();
/*     */     }
/*     */   }
/*     */ 
/*  55 */   void labelSlices(ImagePlus imp) { if (createOverlay) return;
/*  56 */     ImageStack stack = imp.getStack();
/*  57 */     String units = getUnits(imp);
/*  58 */     for (int i = 1; i <= stack.getSize(); i++)
/*  59 */       drawScaleBar(stack.getProcessor(i), units);
/*  60 */     imp.setStack(stack); }
/*     */ 
/*     */   boolean showDialog(ImagePlus imp)
/*     */   {
/*  64 */     Roi roi = imp.getRoi();
/*  65 */     if (roi != null) {
/*  66 */       Rectangle r = roi.getBounds();
/*  67 */       this.roiX = r.x;
/*  68 */       this.roiY = r.y;
/*  69 */       this.roiWidth = r.width;
/*  70 */       this.roiHeight = r.height;
/*  71 */       location = locations[4];
/*  72 */     } else if (location.equals(locations[4])) {
/*  73 */       location = locations[0];
/*     */     }
/*  75 */     Calibration cal = imp.getCalibration();
/*  76 */     ImageWindow win = imp.getWindow();
/*  77 */     this.mag = (win != null ? win.getCanvas().getMagnification() : 1.0D);
/*  78 */     if (this.mag > 1.0D)
/*  79 */       this.mag = 1.0D;
/*  80 */     if (fontSize < defaultFontSize / this.mag)
/*  81 */       fontSize = (int)(defaultFontSize / this.mag);
/*  82 */     String units = cal.getUnits();
/*     */ 
/*  84 */     if (units.equals("micron"))
/*  85 */       units = "µm";
/*  86 */     double pixelWidth = cal.pixelWidth;
/*  87 */     if (pixelWidth == 0.0D)
/*  88 */       pixelWidth = 1.0D;
/*  89 */     double scale = 1.0D / pixelWidth;
/*  90 */     this.imageWidth = (imp.getWidth() * pixelWidth);
/*  91 */     if ((this.roiX > 0) && (this.roiWidth > 10)) {
/*  92 */       barWidth = this.roiWidth * pixelWidth;
/*  93 */     } else if ((barWidth == 0.0D) || (barWidth > 0.67D * this.imageWidth)) {
/*  94 */       barWidth = 80.0D * pixelWidth / this.mag;
/*  95 */       if (barWidth > 0.67D * this.imageWidth)
/*  96 */         barWidth = 0.67D * this.imageWidth;
/*  97 */       if (barWidth > 5.0D)
/*  98 */         barWidth = (int)barWidth;
/*     */     }
/* 100 */     int stackSize = imp.getStackSize();
/* 101 */     int digits = (int)barWidth == barWidth ? 0 : 1;
/* 102 */     if (barWidth < 1.0D)
/* 103 */       digits = 2;
/* 104 */     int percent = (int)(barWidth * 100.0D / this.imageWidth);
/* 105 */     if ((this.mag < 1.0D) && (barHeightInPixels < defaultBarHeight / this.mag))
/* 106 */       barHeightInPixels = (int)(defaultBarHeight / this.mag);
/* 107 */     imp.getProcessor().snapshot();
/* 108 */     if (IJ.macroRunning())
/* 109 */       boldText = ScaleBar.hideText = this.serifFont = ScaleBar.createOverlay = 0;
/*     */     else
/* 111 */       updateScalebar();
/* 112 */     GenericDialog gd = new BarDialog("ScaleBar Plus");
/* 113 */     gd.addNumericField("Width in " + units + ": ", barWidth, digits);
/* 114 */     gd.addNumericField("Height in pixels: ", barHeightInPixels, 0);
/* 115 */     gd.addNumericField("Font size: ", fontSize, 0);
/* 116 */     gd.addChoice("Color: ", colors, color);
/* 117 */     gd.addChoice("Background: ", bcolors, bcolor);
/* 118 */     gd.addChoice("Location: ", locations, location);
/* 119 */     this.checkboxStates[0] = boldText; this.checkboxStates[1] = hideText;
/* 120 */     this.checkboxStates[2] = this.serifFont; this.checkboxStates[3] = createOverlay;
/* 121 */     gd.setInsets(10, 25, 0);
/* 122 */     gd.addCheckboxGroup(2, 2, checkboxLabels, this.checkboxStates);
/* 123 */     if (stackSize > 1) {
/* 124 */       gd.setInsets(0, 25, 0);
/* 125 */       gd.addCheckbox("Label all slices", labelAll);
/*     */     }
/* 127 */     gd.showDialog();
/* 128 */     if (gd.wasCanceled()) {
/* 129 */       imp.getProcessor().reset();
/* 130 */       imp.updateAndDraw();
/* 131 */       if (this.showingOverlay)
/* 132 */         imp.setOverlay(null);
/* 133 */       return false;
/*     */     }
/* 135 */     barWidth = gd.getNextNumber();
/* 136 */     barHeightInPixels = (int)gd.getNextNumber();
/* 137 */     fontSize = (int)gd.getNextNumber();
/* 138 */     color = gd.getNextChoice();
/* 139 */     bcolor = gd.getNextChoice();
/* 140 */     location = gd.getNextChoice();
/* 141 */     boldText = gd.getNextBoolean();
/* 142 */     hideText = gd.getNextBoolean();
/* 143 */     this.serifFont = gd.getNextBoolean();
/* 144 */     createOverlay = gd.getNextBoolean();
/* 145 */     if (stackSize > 1)
/* 146 */       labelAll = gd.getNextBoolean();
/* 147 */     if (IJ.macroRunning()) updateScalebar();
/* 148 */     return true;
/*     */   }
/*     */ 
/*     */   void drawScaleBar(ImagePlus imp) {
/* 152 */     if (!updateLocation())
/* 153 */       return;
/* 154 */     Undo.setup(1, imp);
/* 155 */     drawScaleBar(imp.getProcessor(), getUnits(imp));
/* 156 */     imp.updateAndDraw();
/*     */   }
/*     */ 
/*     */   String getUnits(ImagePlus imp) {
/* 160 */     String units = imp.getCalibration().getUnits();
/* 161 */     if (units.equals("microns"))
/* 162 */       units = "µm";
/* 163 */     return units;
/*     */   }
/*     */ 
/*     */   void createOverlay(ImagePlus imp) {
/* 167 */     Overlay overlay = this.baseOverlay;
/* 168 */     if (overlay != null)
/* 169 */       overlay = overlay.duplicate();
/*     */     else
/* 171 */       overlay = new Overlay();
/* 172 */     Color color = getColor();
/* 173 */     Color bcolor = getBColor();
/* 174 */     int x = this.xloc;
/* 175 */     int y = this.yloc;
/* 176 */     int fontType = boldText ? 1 : 0;
/* 177 */     String face = this.serifFont ? "Serif" : "SanSerif";
/* 178 */     Font font = new Font(face, fontType, fontSize);
/* 179 */     String label = getLength(barWidth) + " " + getUnits(imp);
/* 180 */     ImageProcessor ip = imp.getProcessor();
/* 181 */     ip.setFont(font);
/* 182 */     int swidth = hideText ? 0 : ip.getStringWidth(label);
/* 183 */     int xoffset = (this.barWidthInPixels - swidth) / 2;
/* 184 */     int yoffset = barHeightInPixels + (hideText ? 0 : fontSize + fontSize / 4);
/* 185 */     if (bcolor != null) {
/* 186 */       int w = this.barWidthInPixels;
/* 187 */       int h = yoffset;
/* 188 */       if (w < swidth) w = swidth;
/* 189 */       int x2 = x;
/* 190 */       if (x + xoffset < x2) x2 = x + xoffset;
/* 191 */       int margin = w / 20;
/* 192 */       if (margin < 2) margin = 2;
/* 193 */       x2 -= margin;
/* 194 */       int y2 = y - margin;
/* 195 */       w += margin * 2;
/* 196 */       h += margin * 2;
/* 197 */       Roi background = new Roi(x2, y2, w, h);
/* 198 */       background.setFillColor(bcolor);
/* 199 */       overlay.add(background);
/*     */     }
/* 201 */     Roi bar = new Roi(x, y, this.barWidthInPixels, barHeightInPixels);
/* 202 */     bar.setFillColor(color);
/* 203 */     overlay.add(bar);
/* 204 */     if (!hideText) {
/* 205 */       TextRoi text = new TextRoi(x + xoffset, y + barHeightInPixels, label, font);
/* 206 */       text.setStrokeColor(color);
/* 207 */       overlay.add(text);
/*     */     }
/* 209 */     imp.setOverlay(overlay);
/* 210 */     this.showingOverlay = true;
/*     */   }
/*     */ 
/*     */   void drawScaleBar(ImageProcessor ip, String units) {
/* 214 */     Color color = getColor();
/* 215 */     Color bcolor = getBColor();
/* 216 */     int x = this.xloc;
/* 217 */     int y = this.yloc;
/* 218 */     int fontType = boldText ? 1 : 0;
/* 219 */     String font = this.serifFont ? "Serif" : "SanSerif";
/* 220 */     ip.setFont(new Font(font, fontType, fontSize));
/* 221 */     ip.setAntialiasedText(true);
/* 222 */     String label = getLength(barWidth) + " " + units;
/* 223 */     int swidth = hideText ? 0 : ip.getStringWidth(label);
/* 224 */     int xoffset = (this.barWidthInPixels - swidth) / 2;
/* 225 */     int yoffset = barHeightInPixels + (hideText ? 0 : fontSize + fontSize / (this.serifFont ? 8 : 4));
/*     */ 
/* 228 */     if (bcolor != null) {
/* 229 */       int w = this.barWidthInPixels;
/* 230 */       int h = yoffset;
/* 231 */       if (w < swidth) w = swidth;
/* 232 */       int x2 = x;
/* 233 */       if (x + xoffset < x2) x2 = x + xoffset;
/* 234 */       int margin = w / 20;
/* 235 */       if (margin < 2) margin = 2;
/* 236 */       x2 -= margin;
/* 237 */       int y2 = y - margin;
/* 238 */       w += margin * 2;
/* 239 */       h += margin * 2;
/* 240 */       ip.setColor(bcolor);
/* 241 */       ip.setRoi(x2, y2, w, h);
/* 242 */       ip.fill();
/*     */     }
/*     */ 
/* 245 */     ip.resetRoi();
/* 246 */     ip.setColor(color);
/* 247 */     ip.setRoi(x, y, this.barWidthInPixels, barHeightInPixels);
/* 248 */     ip.fill();
/* 249 */     ip.resetRoi();
/* 250 */     if (!hideText)
/* 251 */       ip.drawString(label, x + xoffset, y + yoffset);
/* 252 */     this.drawnScaleBar = true;
/*     */   }
/*     */ 
/*     */   String getLength(double barWidth) {
/* 256 */     int digits = (int)barWidth == barWidth ? 0 : 1;
/* 257 */     if (barWidth < 1.0D) digits = 1;
/* 258 */     if (digits == 1) {
/* 259 */       String s = IJ.d2s(barWidth / 0.1D, 2);
/* 260 */       if (!s.endsWith(".00")) digits = 2;
/*     */     }
/* 262 */     return IJ.d2s(barWidth, digits);
/*     */   }
/*     */ 
/*     */   boolean updateLocation() {
/* 266 */     Calibration cal = this.imp.getCalibration();
/* 267 */     this.barWidthInPixels = ((int)(barWidth / cal.pixelWidth));
/* 268 */     int width = this.imp.getWidth();
/* 269 */     int height = this.imp.getHeight();
/* 270 */     int fraction = 20;
/* 271 */     int x = width - width / fraction - this.barWidthInPixels;
/* 272 */     int y = 0;
/* 273 */     if (location.equals(locations[0])) {
/* 274 */       y = height / fraction;
/* 275 */     } else if (location.equals(locations[1])) {
/* 276 */       y = height - height / fraction - barHeightInPixels - fontSize;
/* 277 */     } else if (location.equals(locations[3])) {
/* 278 */       x = width / fraction;
/* 279 */       y = height / fraction;
/* 280 */     } else if (location.equals(locations[2])) {
/* 281 */       x = width / fraction;
/* 282 */       y = height - height / fraction - barHeightInPixels - fontSize;
/*     */     } else {
/* 284 */       if (this.roiX == -1)
/* 285 */         return false;
/* 286 */       x = this.roiX;
/* 287 */       y = this.roiY;
/*     */     }
/* 289 */     this.xloc = x;
/* 290 */     this.yloc = y;
/* 291 */     return true;
/*     */   }
/*     */ 
/*     */   Color getColor() {
/* 295 */     Color c = Color.black;
/* 296 */     if (color.equals(colors[0])) c = Color.white;
/* 297 */     else if (color.equals(colors[2])) c = Color.lightGray;
/* 298 */     else if (color.equals(colors[3])) c = Color.gray;
/* 299 */     else if (color.equals(colors[4])) c = Color.darkGray;
/* 300 */     else if (color.equals(colors[5])) c = Color.red;
/* 301 */     else if (color.equals(colors[6])) c = Color.green;
/* 302 */     else if (color.equals(colors[7])) c = Color.blue;
/* 303 */     else if (color.equals(colors[8])) c = Color.yellow;
/* 304 */     return c;
/*     */   }
/*     */ 
/*     */   Color getBColor()
/*     */   {
/* 309 */     if ((bcolor == null) || (bcolor.equals(bcolors[0]))) return null;
/* 310 */     Color bc = Color.white;
/* 311 */     if (bcolor.equals(bcolors[1])) bc = Color.black;
/* 312 */     else if (bcolor.equals(bcolors[3])) bc = Color.darkGray;
/* 313 */     else if (bcolor.equals(bcolors[4])) bc = Color.gray;
/* 314 */     else if (bcolor.equals(bcolors[5])) bc = Color.lightGray;
/* 315 */     else if (bcolor.equals(bcolors[6])) bc = Color.yellow;
/* 316 */     else if (bcolor.equals(bcolors[7])) bc = Color.blue;
/* 317 */     else if (bcolor.equals(bcolors[8])) bc = Color.green;
/* 318 */     else if (bcolor.equals(bcolors[9])) bc = Color.red;
/* 319 */     return bc;
/*     */   }
/*     */ 
/*     */   void updateScalebar() {
/* 323 */     updateLocation();
/* 324 */     this.imp.getProcessor().reset();
/* 325 */     if (createOverlay) {
/* 326 */       if (this.drawnScaleBar) {
/* 327 */         this.imp.updateAndDraw();
/* 328 */         this.drawnScaleBar = false;
/*     */       }
/* 330 */       createOverlay(this.imp);
/*     */     } else {
/* 332 */       if (this.showingOverlay) {
/* 333 */         this.imp.setOverlay(null);
/* 334 */         this.showingOverlay = false;
/*     */       }
/* 336 */       drawScaleBar(this.imp);
/*     */     }
/*     */   }
/*     */ 
/*     */   class BarDialog extends GenericDialog
/*     */   {
/*     */     BarDialog(String title) {
/* 343 */       super();
/*     */     }
/*     */ 
/*     */     public void textValueChanged(TextEvent e) {
/* 347 */       TextField widthField = (TextField)this.numberField.elementAt(0);
/* 348 */       Double d = getValue(widthField.getText());
/* 349 */       if (d == null)
/* 350 */         return;
/* 351 */       ScaleBar.barWidth = d.doubleValue();
/* 352 */       TextField heightField = (TextField)this.numberField.elementAt(1);
/* 353 */       d = getValue(heightField.getText());
/* 354 */       if (d == null)
/* 355 */         return;
/* 356 */       ScaleBar.barHeightInPixels = (int)d.doubleValue();
/* 357 */       TextField fontSizeField = (TextField)this.numberField.elementAt(2);
/* 358 */       d = getValue(fontSizeField.getText());
/* 359 */       if (d == null)
/* 360 */         return;
/* 361 */       int size = (int)d.doubleValue();
/* 362 */       if (size > 5)
/* 363 */         ScaleBar.fontSize = size;
/* 364 */       ScaleBar.this.updateScalebar();
/*     */     }
/*     */ 
/*     */     public void itemStateChanged(ItemEvent e) {
/* 368 */       Choice col = (Choice)this.choice.elementAt(0);
/* 369 */       ScaleBar.color = col.getSelectedItem();
/* 370 */       Choice bcol = (Choice)this.choice.elementAt(1);
/* 371 */       ScaleBar.bcolor = bcol.getSelectedItem();
/* 372 */       Choice loc = (Choice)this.choice.elementAt(2);
/* 373 */       ScaleBar.location = loc.getSelectedItem();
/* 374 */       ScaleBar.boldText = ((Checkbox)this.checkbox.elementAt(0)).getState();
/* 375 */       ScaleBar.hideText = ((Checkbox)this.checkbox.elementAt(1)).getState();
/* 376 */       ScaleBar.this.serifFont = ((Checkbox)this.checkbox.elementAt(2)).getState();
/* 377 */       ScaleBar.createOverlay = ((Checkbox)this.checkbox.elementAt(3)).getState();
/* 378 */       ScaleBar.this.updateScalebar();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ScaleBar
 * JD-Core Version:    0.6.2
 */