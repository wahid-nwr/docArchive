/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Macro;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.FileOpener;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.event.TextListener;
/*     */ import java.util.Locale;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class ImageProperties
/*     */   implements PlugInFilter, TextListener
/*     */ {
/*     */   ImagePlus imp;
/*     */   static final int NANOMETER = 0;
/*     */   static final int MICROMETER = 1;
/*     */   static final int MILLIMETER = 2;
/*     */   static final int CENTIMETER = 3;
/*     */   static final int METER = 4;
/*     */   static final int KILOMETER = 5;
/*     */   static final int INCH = 6;
/*     */   static final int FOOT = 7;
/*     */   static final int MILE = 8;
/*     */   static final int PIXEL = 9;
/*     */   static final int OTHER_UNIT = 10;
/*     */   int oldUnitIndex;
/*     */   double oldUnitsPerCm;
/*     */   Vector nfields;
/*     */   Vector sfields;
/*  19 */   boolean duplicatePixelWidth = true;
/*     */   String calUnit;
/*     */   double calPixelWidth;
/*     */   double calPixelHeight;
/*     */   double calPixelDepth;
/*     */   TextField pixelWidthField;
/*     */   TextField pixelHeightField;
/*     */   TextField pixelDepthField;
/*     */   int textChangedCount;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  26 */     this.imp = imp;
/*  27 */     return 159;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  31 */     showDialog(this.imp);
/*     */   }
/*     */ 
/*     */   void showDialog(ImagePlus imp) {
/*  35 */     String options = Macro.getOptions();
/*  36 */     if (options != null) {
/*  37 */       String options2 = options.replaceAll(" depth=", " slices=");
/*  38 */       options2 = options2.replaceAll(" interval=", " frame=");
/*  39 */       Macro.setOptions(options2);
/*     */     }
/*  41 */     Calibration cal = imp.getCalibration();
/*  42 */     Calibration calOrig = cal.copy();
/*  43 */     this.oldUnitIndex = getUnitIndex(cal.getUnit());
/*  44 */     this.oldUnitsPerCm = getUnitsPerCm(this.oldUnitIndex);
/*  45 */     int stackSize = imp.getImageStackSize();
/*  46 */     int channels = imp.getNChannels();
/*  47 */     int slices = imp.getNSlices();
/*  48 */     int frames = imp.getNFrames();
/*  49 */     boolean global1 = imp.getGlobalCalibration() != null;
/*     */ 
/*  51 */     int digits = (cal.pixelWidth < 1.0D) || (cal.pixelHeight < 1.0D) || (cal.pixelDepth < 1.0D) ? 7 : 4;
/*  52 */     GenericDialog gd = new GenericDialog(imp.getTitle());
/*  53 */     gd.addNumericField("Channels (c):", channels, 0);
/*  54 */     gd.addNumericField("Slices (z):", slices, 0);
/*  55 */     gd.addNumericField("Frames (t):", frames, 0);
/*  56 */     gd.setInsets(0, 5, 0);
/*  57 */     gd.addMessage("Note: c*z*t must equal " + stackSize);
/*  58 */     gd.setInsets(15, 0, 0);
/*  59 */     gd.addStringField("Unit of Length:", cal.getUnit());
/*  60 */     gd.addNumericField("Pixel_Width:", cal.pixelWidth, digits, 8, null);
/*  61 */     gd.addNumericField("Pixel_Height:", cal.pixelHeight, digits, 8, null);
/*  62 */     gd.addNumericField("Voxel_Depth:", cal.pixelDepth, digits, 8, null);
/*  63 */     gd.setInsets(10, 0, 5);
/*  64 */     double interval = cal.frameInterval;
/*  65 */     String intervalStr = IJ.d2s(interval, (int)interval == interval ? 0 : 2) + " " + cal.getTimeUnit();
/*  66 */     gd.addStringField("Frame Interval:", intervalStr);
/*  67 */     String xo = cal.xOrigin == (int)cal.xOrigin ? IJ.d2s(cal.xOrigin, 0) : IJ.d2s(cal.xOrigin, 2);
/*  68 */     String yo = cal.yOrigin == (int)cal.yOrigin ? IJ.d2s(cal.yOrigin, 0) : IJ.d2s(cal.yOrigin, 2);
/*  69 */     String zo = "";
/*  70 */     if (cal.zOrigin != 0.0D) {
/*  71 */       zo = cal.zOrigin == (int)cal.zOrigin ? IJ.d2s(cal.zOrigin, 0) : IJ.d2s(cal.zOrigin, 2);
/*  72 */       zo = "," + zo;
/*     */     }
/*  74 */     gd.addStringField("Origin (pixels):", xo + "," + yo + zo);
/*  75 */     gd.setInsets(5, 20, 0);
/*  76 */     gd.addCheckbox("Global", global1);
/*  77 */     this.nfields = gd.getNumericFields();
/*  78 */     this.pixelWidthField = ((TextField)this.nfields.elementAt(3));
/*  79 */     this.pixelHeightField = ((TextField)this.nfields.elementAt(4));
/*  80 */     this.pixelDepthField = ((TextField)this.nfields.elementAt(5));
/*  81 */     for (int i = 0; i < this.nfields.size(); i++)
/*  82 */       ((TextField)this.nfields.elementAt(i)).addTextListener(this);
/*  83 */     this.sfields = gd.getStringFields();
/*  84 */     for (int i = 0; i < this.sfields.size(); i++)
/*  85 */       ((TextField)this.sfields.elementAt(i)).addTextListener(this);
/*  86 */     this.calUnit = cal.getUnit();
/*  87 */     this.calPixelWidth = cal.pixelWidth;
/*  88 */     this.calPixelHeight = cal.pixelHeight;
/*  89 */     this.calPixelDepth = cal.pixelDepth;
/*  90 */     gd.showDialog();
/*  91 */     if (gd.wasCanceled())
/*  92 */       return;
/*  93 */     channels = (int)gd.getNextNumber();
/*  94 */     if (channels < 1) channels = 1;
/*  95 */     slices = (int)gd.getNextNumber();
/*  96 */     if (slices < 1) slices = 1;
/*  97 */     frames = (int)gd.getNextNumber();
/*  98 */     if (frames < 1) frames = 1;
/*  99 */     if (channels * slices * frames == stackSize)
/* 100 */       imp.setDimensions(channels, slices, frames);
/*     */     else {
/* 102 */       IJ.error("Properties", "The product of channels (" + channels + "), slices (" + slices + ")\n and frames (" + frames + ") must equal the stack size (" + stackSize + ").");
/*     */     }
/*     */ 
/* 105 */     String unit = gd.getNextString();
/* 106 */     if (unit.equals("u"))
/* 107 */       unit = "µ";
/* 108 */     else if (unit.equals("A"))
/* 109 */       unit = "Å";
/* 110 */     double pixelWidth = gd.getNextNumber();
/* 111 */     double pixelHeight = gd.getNextNumber();
/* 112 */     double pixelDepth = gd.getNextNumber();
/*     */ 
/* 117 */     if ((unit.equals("")) || (unit.equalsIgnoreCase("none")) || (pixelWidth == 0.0D)) {
/* 118 */       cal.setUnit(null);
/* 119 */       cal.pixelWidth = 1.0D;
/* 120 */       cal.pixelHeight = 1.0D;
/* 121 */       cal.pixelDepth = 1.0D;
/*     */     } else {
/* 123 */       cal.setUnit(unit);
/* 124 */       cal.pixelWidth = pixelWidth;
/* 125 */       cal.pixelHeight = pixelHeight;
/* 126 */       cal.pixelDepth = pixelDepth;
/*     */     }
/*     */ 
/* 129 */     String frameInterval = validateInterval(gd.getNextString());
/* 130 */     String[] intAndUnit = Tools.split(frameInterval, " -");
/* 131 */     interval = Tools.parseDouble(intAndUnit[0]);
/* 132 */     cal.frameInterval = (Double.isNaN(interval) ? 0.0D : interval);
/* 133 */     String timeUnit = intAndUnit.length >= 2 ? intAndUnit[1] : "sec";
/* 134 */     if ((timeUnit.equals("sec")) && (cal.frameInterval <= 2.0D) && (cal.frameInterval >= 0.03333333333333333D))
/* 135 */       cal.fps = (1.0D / cal.frameInterval);
/* 136 */     if (timeUnit.equals("usec"))
/* 137 */       timeUnit = "µsec";
/* 138 */     cal.setTimeUnit(timeUnit);
/*     */ 
/* 140 */     String[] origin = Tools.split(gd.getNextString(), " ,");
/* 141 */     double x = Tools.parseDouble(origin[0]);
/* 142 */     double y = origin.length >= 2 ? Tools.parseDouble(origin[1]) : (0.0D / 0.0D);
/* 143 */     double z = origin.length >= 3 ? Tools.parseDouble(origin[2]) : (0.0D / 0.0D);
/* 144 */     cal.xOrigin = (Double.isNaN(x) ? 0.0D : x);
/* 145 */     cal.yOrigin = (Double.isNaN(y) ? cal.xOrigin : y);
/* 146 */     cal.zOrigin = (Double.isNaN(z) ? 0.0D : z);
/* 147 */     boolean global2 = gd.getNextBoolean();
/* 148 */     if (!cal.equals(calOrig))
/* 149 */       imp.setCalibration(cal);
/* 150 */     imp.setGlobalCalibration(global2 ? cal : null);
/* 151 */     if ((global2) || (global2 != global1))
/* 152 */       WindowManager.repaintImageWindows();
/*     */     else
/* 154 */       imp.repaintWindow();
/* 155 */     if ((global2) && (global2 != global1))
/* 156 */       FileOpener.setShowConflictMessage(true);
/*     */   }
/*     */ 
/*     */   String validateInterval(String interval) {
/* 160 */     if (interval.indexOf(" ") != -1)
/* 161 */       return interval;
/* 162 */     int firstLetter = -1;
/* 163 */     for (int i = 0; i < interval.length(); i++) {
/* 164 */       char c = interval.charAt(i);
/* 165 */       if (Character.isLetter(c)) {
/* 166 */         firstLetter = i;
/* 167 */         break;
/*     */       }
/*     */     }
/* 170 */     if ((firstLetter > 0) && (firstLetter < interval.length() - 1))
/* 171 */       interval = interval.substring(0, firstLetter) + " " + interval.substring(firstLetter, interval.length());
/* 172 */     return interval;
/*     */   }
/*     */ 
/*     */   double getNewScale(String newUnit, double oldScale)
/*     */   {
/* 177 */     if (this.oldUnitsPerCm == 0.0D) return 0.0D;
/* 178 */     double newScale = 0.0D;
/* 179 */     int newUnitIndex = getUnitIndex(newUnit);
/* 180 */     if (newUnitIndex != this.oldUnitIndex) {
/* 181 */       double newUnitsPerCm = getUnitsPerCm(newUnitIndex);
/* 182 */       if ((this.oldUnitsPerCm != 0.0D) && (newUnitsPerCm != 0.0D)) {
/* 183 */         newScale = oldScale * (this.oldUnitsPerCm / newUnitsPerCm);
/*     */       }
/*     */     }
/* 186 */     return newScale;
/*     */   }
/*     */ 
/*     */   static int getUnitIndex(String unit) {
/* 190 */     unit = unit.toLowerCase(Locale.US);
/* 191 */     if ((unit.equals("cm")) || (unit.startsWith("cent")))
/* 192 */       return 3;
/* 193 */     if ((unit.equals("mm")) || (unit.startsWith("milli")))
/* 194 */       return 2;
/* 195 */     if (unit.startsWith("inch"))
/* 196 */       return 6;
/* 197 */     if ((unit.startsWith("µ")) || (unit.startsWith("u")) || (unit.startsWith("micro")))
/* 198 */       return 1;
/* 199 */     if ((unit.equals("nm")) || (unit.startsWith("nano")))
/* 200 */       return 0;
/* 201 */     if (unit.startsWith("meter"))
/* 202 */       return 4;
/* 203 */     if ((unit.equals("km")) || (unit.startsWith("kilo")))
/* 204 */       return 5;
/* 205 */     if ((unit.equals("ft")) || (unit.equals("foot")) || (unit.equals("feet")))
/* 206 */       return 7;
/* 207 */     if ((unit.equals("mi")) || (unit.startsWith("mile"))) {
/* 208 */       return 2;
/*     */     }
/* 210 */     return 10;
/*     */   }
/*     */ 
/*     */   static double getUnitsPerCm(int unitIndex) {
/* 214 */     switch (unitIndex) { case 0:
/* 215 */       return 10000000.0D;
/*     */     case 1:
/* 216 */       return 10000.0D;
/*     */     case 2:
/* 217 */       return 10.0D;
/*     */     case 3:
/* 218 */       return 1.0D;
/*     */     case 4:
/* 219 */       return 0.01D;
/*     */     case 5:
/* 220 */       return 1.E-05D;
/*     */     case 6:
/* 221 */       return 0.3937D;
/*     */     case 7:
/* 222 */       return 0.0328083D;
/*     */     case 8:
/* 223 */       return 6.213E-06D; }
/* 224 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public void textValueChanged(TextEvent e)
/*     */   {
/* 229 */     this.textChangedCount += 1;
/* 230 */     Object source = e.getSource();
/*     */ 
/* 232 */     int channels = (int)Tools.parseDouble(((TextField)this.nfields.elementAt(2)).getText(), -99.0D);
/* 233 */     int depth = (int)Tools.parseDouble(((TextField)this.nfields.elementAt(3)).getText(), -99.0D);
/* 234 */     int frames = (int)Tools.parseDouble(((TextField)this.nfields.elementAt(4)).getText(), -99.0D);
/*     */ 
/* 236 */     double newPixelWidth = this.calPixelWidth;
/* 237 */     String newWidthText = this.pixelWidthField.getText();
/* 238 */     if (source == this.pixelWidthField)
/* 239 */       newPixelWidth = Tools.parseDouble(newWidthText, -99.0D);
/* 240 */     double newPixelHeight = this.calPixelHeight;
/* 241 */     if (source == this.pixelHeightField) {
/* 242 */       String newHeightText = this.pixelHeightField.getText();
/* 243 */       newPixelHeight = Tools.parseDouble(newHeightText, -99.0D);
/* 244 */       if (!newHeightText.equals(newWidthText))
/* 245 */         this.duplicatePixelWidth = false;
/*     */     }
/* 247 */     double newPixelDepth = this.calPixelDepth;
/* 248 */     if (source == this.pixelDepthField) {
/* 249 */       String newDepthText = this.pixelDepthField.getText();
/* 250 */       newPixelDepth = Tools.parseDouble(newDepthText, -99.0D);
/* 251 */       if (!newDepthText.equals(newWidthText))
/* 252 */         this.duplicatePixelWidth = false;
/*     */     }
/* 254 */     if ((this.textChangedCount == 1) && ((this.calPixelHeight != 1.0D) || (this.calPixelDepth != 1.0D)))
/* 255 */       this.duplicatePixelWidth = false;
/* 256 */     if ((source == this.pixelWidthField) && (newPixelWidth != -99.0D) && (this.duplicatePixelWidth)) {
/* 257 */       this.pixelHeightField.setText(newWidthText);
/* 258 */       this.pixelDepthField.setText(newWidthText);
/* 259 */       this.calPixelHeight = this.calPixelWidth;
/* 260 */       this.calPixelDepth = this.calPixelWidth;
/*     */     }
/* 262 */     this.calPixelWidth = newPixelWidth;
/* 263 */     this.calPixelHeight = newPixelHeight;
/* 264 */     this.calPixelDepth = newPixelDepth;
/* 265 */     TextField unitField = (TextField)this.sfields.elementAt(0);
/* 266 */     String newUnit = unitField.getText();
/* 267 */     if (!newUnit.equals(this.calUnit)) {
/* 268 */       double oldXScale = newPixelWidth != 0.0D ? 1.0D / newPixelWidth : 0.0D;
/* 269 */       double oldYScale = newPixelHeight != 0.0D ? 1.0D / newPixelHeight : 0.0D;
/* 270 */       double oldZScale = newPixelDepth != 0.0D ? 1.0D / newPixelDepth : 0.0D;
/* 271 */       double newXScale = getNewScale(newUnit, oldXScale);
/* 272 */       double newYScale = getNewScale(newUnit, oldYScale);
/* 273 */       double newZScale = getNewScale(newUnit, oldZScale);
/* 274 */       if (newXScale != 0.0D) {
/* 275 */         double w = 1.0D / newXScale;
/* 276 */         double h = 1.0D / newYScale;
/* 277 */         double d = 1.0D / newZScale;
/* 278 */         int digits = (w < 1.0D) || (h < 1.0D) || (d < 1.0D) ? 7 : 4;
/* 279 */         this.pixelWidthField.setText(IJ.d2s(1.0D / newXScale, digits));
/* 280 */         this.pixelHeightField.setText(IJ.d2s(1.0D / newYScale, digits));
/* 281 */         this.pixelDepthField.setText(IJ.d2s(1.0D / newZScale, digits));
/* 282 */         this.calPixelWidth = (1.0D / newXScale);
/* 283 */         this.calPixelHeight = (1.0D / newYScale);
/* 284 */         this.calPixelDepth = (1.0D / newZScale);
/* 285 */         this.oldUnitIndex = getUnitIndex(newUnit);
/* 286 */         this.oldUnitsPerCm = getUnitsPerCm(this.oldUnitIndex);
/*     */       }
/* 288 */       this.calUnit = newUnit;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.ImageProperties
 * JD-Core Version:    0.6.2
 */