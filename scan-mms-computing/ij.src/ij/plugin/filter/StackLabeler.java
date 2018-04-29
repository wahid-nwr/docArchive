/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.TextRoi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.util.Tools;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class StackLabeler
/*     */   implements ExtendedPlugInFilter, DialogListener
/*     */ {
/*  11 */   private static final String[] formats = { "0", "0000", "00:00", "00:00:00", "Text" };
/*     */   private static final int NUMBER = 0;
/*     */   private static final int ZERO_PADDED_NUMBER = 1;
/*     */   private static final int MIN_SEC = 2;
/*     */   private static final int HOUR_MIN_SEC = 3;
/*     */   private static final int TEXT = 4;
/*  13 */   private static int format = (int)Prefs.get("label.format", 0.0D);
/*  14 */   private int flags = 31;
/*     */   private ImagePlus imp;
/*  16 */   private static int x = 5;
/*  17 */   private static int y = 20;
/*  18 */   private static int fontSize = 18;
/*     */   private int maxWidth;
/*     */   private Font font;
/*  21 */   private static double start = 0.0D;
/*  22 */   private static double interval = 1.0D;
/*  23 */   private static String text = "";
/*  24 */   private static int decimalPlaces = 0;
/*     */   private static boolean useOverlay;
/*     */   private static boolean useTextToolFont;
/*     */   private int fieldWidth;
/*     */   private Color color;
/*     */   private int firstFrame;
/*     */   private int lastFrame;
/*     */   private int defaultLastFrame;
/*     */   private Overlay overlay;
/*     */   private boolean previewing;
/*     */   private boolean virtualStack;
/*     */   private int yoffset;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  36 */     if (imp != null) {
/*  37 */       this.virtualStack = imp.getStack().isVirtual();
/*  38 */       if (this.virtualStack) useOverlay = true;
/*  39 */       this.flags += (this.virtualStack ? 0 : 32);
/*  40 */       this.firstFrame = 1; this.lastFrame = (this.defaultLastFrame = imp.getStackSize());
/*     */     }
/*  42 */     this.imp = imp;
/*  43 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
/*  47 */     ImageProcessor ip = imp.getProcessor();
/*  48 */     Rectangle roi = ip.getRoi();
/*  49 */     if ((roi.width < ip.getWidth()) || (roi.height < ip.getHeight())) {
/*  50 */       x = roi.x;
/*  51 */       y = roi.y + roi.height;
/*  52 */       fontSize = (int)((roi.height - 1.10526D) / 0.934211D);
/*  53 */       if (fontSize < 7) fontSize = 7;
/*  54 */       if (fontSize > 80) fontSize = 80;
/*     */     }
/*  56 */     if (IJ.macroRunning()) {
/*  57 */       format = 0;
/*  58 */       decimalPlaces = 0;
/*  59 */       interval = 1.0D;
/*  60 */       text = "";
/*  61 */       start = 0.0D;
/*  62 */       useOverlay = false;
/*  63 */       useTextToolFont = false;
/*  64 */       String options = Macro.getOptions();
/*  65 */       if (options != null) {
/*  66 */         if ((options.indexOf("interval=0") != -1) && (options.indexOf("format=") == -1))
/*  67 */           format = 4;
/*  68 */         if (options.indexOf(" slice=") != -1) {
/*  69 */           options = options.replaceAll(" slice=", " range=");
/*  70 */           Macro.setOptions(options);
/*     */         }
/*     */       }
/*     */     }
/*  74 */     if ((format < 0) || (format > 4)) format = 0;
/*  75 */     int defaultLastFrame = imp.getStackSize();
/*  76 */     if (imp.isHyperStack()) {
/*  77 */       if (imp.getNFrames() > 1)
/*  78 */         defaultLastFrame = imp.getNFrames();
/*  79 */       else if (imp.getNSlices() > 1)
/*  80 */         defaultLastFrame = imp.getNSlices();
/*     */     }
/*  82 */     GenericDialog gd = new GenericDialog("StackLabeler");
/*  83 */     gd.setInsets(2, 5, 0);
/*  84 */     gd.addChoice("Format:", formats, formats[format]);
/*  85 */     gd.addStringField("Starting value:", IJ.d2s(start, decimalPlaces));
/*  86 */     gd.addStringField("Interval:", "" + IJ.d2s(interval, decimalPlaces));
/*  87 */     gd.addNumericField("X location:", x, 0);
/*  88 */     gd.addNumericField("Y location:", y, 0);
/*  89 */     gd.addNumericField("Font size:", fontSize, 0);
/*  90 */     gd.addStringField("Text:", text, 10);
/*  91 */     addRange(gd, "Range:", 1, defaultLastFrame);
/*  92 */     gd.setInsets(10, 20, 0);
/*  93 */     gd.addCheckbox(" Use overlay", useOverlay);
/*  94 */     gd.addCheckbox(" Use text tool font", useTextToolFont);
/*  95 */     gd.addPreviewCheckbox(pfr);
/*  96 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/image.html#label");
/*  97 */     gd.addDialogListener(this);
/*  98 */     this.previewing = true;
/*  99 */     gd.showDialog();
/* 100 */     this.previewing = false;
/* 101 */     if (gd.wasCanceled()) {
/* 102 */       return 4096;
/*     */     }
/* 104 */     return this.flags;
/*     */   }
/*     */ 
/*     */   void addRange(GenericDialog gd, String label, int start, int end) {
/* 108 */     gd.addStringField(label, start + "-" + end);
/*     */   }
/*     */ 
/*     */   double[] getRange(GenericDialog gd, int start, int end) {
/* 112 */     String[] range = Tools.split(gd.getNextString(), " -");
/* 113 */     double d1 = Tools.parseDouble(range[0]);
/* 114 */     double d2 = range.length == 2 ? Tools.parseDouble(range[1]) : (0.0D / 0.0D);
/* 115 */     double[] result = new double[2];
/* 116 */     result[0] = (Double.isNaN(d1) ? 1.0D : (int)d1);
/* 117 */     result[1] = (Double.isNaN(d2) ? end : (int)d2);
/* 118 */     if (result[0] < start) result[0] = start;
/* 119 */     if (result[1] > end) result[1] = end;
/* 120 */     if (result[0] > result[1]) {
/* 121 */       result[0] = start;
/* 122 */       result[1] = end;
/*     */     }
/* 124 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 128 */     format = gd.getNextChoiceIndex();
/* 129 */     start = Tools.parseDouble(gd.getNextString());
/* 130 */     String str = gd.getNextString();
/* 131 */     interval = Tools.parseDouble(str);
/* 132 */     x = (int)gd.getNextNumber();
/* 133 */     y = (int)gd.getNextNumber();
/* 134 */     fontSize = (int)gd.getNextNumber();
/* 135 */     text = gd.getNextString();
/* 136 */     double[] range = getRange(gd, 1, this.defaultLastFrame);
/* 137 */     useOverlay = gd.getNextBoolean();
/* 138 */     useTextToolFont = gd.getNextBoolean();
/* 139 */     if (this.virtualStack) useOverlay = true;
/* 140 */     this.firstFrame = ((int)range[0]); this.lastFrame = ((int)range[1]);
/* 141 */     int index = str.indexOf(".");
/* 142 */     if (index != -1)
/* 143 */       decimalPlaces = str.length() - index - 1;
/*     */     else
/* 145 */       decimalPlaces = 0;
/* 146 */     if (gd.invalidNumber()) return false;
/* 147 */     if (useTextToolFont)
/* 148 */       this.font = new Font(TextRoi.getFont(), TextRoi.getStyle(), fontSize);
/*     */     else
/* 150 */       this.font = new Font("SansSerif", 0, fontSize);
/* 151 */     if (y < fontSize) y = fontSize + 5;
/* 152 */     ImageProcessor ip = this.imp.getProcessor();
/* 153 */     ip.setFont(this.font);
/* 154 */     int size = this.defaultLastFrame;
/* 155 */     this.maxWidth = ip.getStringWidth(getString(size, interval, format));
/* 156 */     this.fieldWidth = 1;
/* 157 */     if (size >= 10) this.fieldWidth = 2;
/* 158 */     if (size >= 100) this.fieldWidth = 3;
/* 159 */     if (size >= 1000) this.fieldWidth = 4;
/* 160 */     if (size >= 10000) this.fieldWidth = 5;
/* 161 */     Prefs.set("label.format", format);
/* 162 */     return true;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/* 166 */     int image = ip.getSliceNumber();
/* 167 */     int n = image - 1;
/* 168 */     if (this.imp.isHyperStack()) n = updateIndex(n);
/* 169 */     if (this.virtualStack) {
/* 170 */       int nSlices = this.imp.getStackSize();
/* 171 */       if (this.previewing) nSlices = 1;
/* 172 */       for (int i = 1; i <= nSlices; i++) {
/* 173 */         image = i; n = i - 1;
/* 174 */         if (this.imp.isHyperStack()) n = updateIndex(n);
/* 175 */         drawLabel(ip, image, n);
/*     */       }
/*     */     } else {
/* 178 */       if ((this.previewing) && (this.overlay != null)) {
/* 179 */         this.imp.setOverlay(null);
/* 180 */         this.overlay = null;
/*     */       }
/* 182 */       drawLabel(ip, image, n);
/*     */     }
/*     */   }
/*     */ 
/*     */   int updateIndex(int n) {
/* 187 */     if (this.imp.getNFrames() > 1)
/* 188 */       return (int)(n * (this.imp.getNFrames() / this.imp.getStackSize()));
/* 189 */     if (this.imp.getNSlices() > 1) {
/* 190 */       return (int)(n * (this.imp.getNSlices() / this.imp.getStackSize()));
/*     */     }
/* 192 */     return n;
/*     */   }
/*     */ 
/*     */   void drawLabel(ImageProcessor ip, int image, int n) {
/* 196 */     String s = getString(n, interval, format);
/* 197 */     ip.setFont(this.font);
/* 198 */     int textWidth = ip.getStringWidth(s);
/* 199 */     if (this.color == null) {
/* 200 */       this.color = Toolbar.getForegroundColor();
/* 201 */       if ((this.color.getRGB() & 0xFFFFFF) == 0) {
/* 202 */         ip.setRoi(x, y - fontSize, this.maxWidth + textWidth, fontSize);
/* 203 */         double mean = ImageStatistics.getStatistics(ip, 2, null).mean;
/* 204 */         if ((mean < 50.0D) && (!ip.isInvertedLut())) this.color = Color.white;
/* 205 */         ip.resetRoi();
/*     */       }
/*     */     }
/* 208 */     int frame = image;
/* 209 */     if (this.imp.isHyperStack()) {
/* 210 */       int[] pos = this.imp.convertIndexToPosition(image);
/* 211 */       if (this.imp.getNFrames() > 1)
/* 212 */         frame = pos[2];
/* 213 */       else if (this.imp.getNSlices() > 1)
/* 214 */         frame = pos[1];
/*     */     }
/* 216 */     if (useOverlay) {
/* 217 */       if (image == 1) {
/* 218 */         this.overlay = new Overlay();
/* 219 */         Roi roi = this.imp.getRoi();
/* 220 */         Rectangle r = roi != null ? roi.getBounds() : null;
/* 221 */         this.yoffset = (r != null ? r.height : fontSize);
/*     */       }
/* 223 */       if ((frame >= this.firstFrame) && (frame <= this.lastFrame)) {
/* 224 */         Roi roi = new TextRoi(x + this.maxWidth - textWidth, y - this.yoffset, s, this.font);
/* 225 */         roi.setStrokeColor(this.color);
/* 226 */         roi.setNonScalable(true);
/* 227 */         roi.setPosition(image);
/* 228 */         this.overlay.add(roi);
/*     */       }
/* 230 */       if ((image == this.imp.getStackSize()) || (this.previewing))
/* 231 */         this.imp.setOverlay(this.overlay);
/* 232 */     } else if ((frame >= this.firstFrame) && (frame <= this.lastFrame)) {
/* 233 */       ip.setColor(this.color);
/* 234 */       ip.setAntialiasedText(fontSize >= 18);
/* 235 */       ip.moveTo(x + this.maxWidth - textWidth, y);
/* 236 */       ip.drawString(s);
/*     */     }
/*     */   }
/*     */ 
/*     */   String getString(int index, double interval, int format) {
/* 241 */     double time = start + (index + 1 - this.firstFrame) * interval;
/* 242 */     int itime = (int)Math.floor(time);
/* 243 */     int sign = 1;
/* 244 */     if (itime < 0) sign = -1;
/* 245 */     itime *= sign;
/* 246 */     String str = "";
/* 247 */     switch (format) { case 0:
/* 248 */       str = IJ.d2s(time, decimalPlaces) + " " + text; break;
/*     */     case 1:
/* 250 */       if (decimalPlaces == 0)
/* 251 */         str = zeroFill((int)time);
/*     */       else
/* 253 */         str = IJ.d2s(time, decimalPlaces);
/* 254 */       str = text + " " + str;
/* 255 */       break;
/*     */     case 2:
/* 257 */       str = pad((int)Math.floor(itime / 60 % 60)) + ":" + pad(itime % 60) + " " + text;
/* 258 */       if (sign == -1) str = "-" + str; break;
/*     */     case 3:
/* 261 */       str = pad((int)Math.floor(itime / 3600)) + ":" + pad((int)Math.floor(itime / 60 % 60)) + ":" + pad(itime % 60) + " " + text;
/* 262 */       if (sign == -1) str = "-" + str; break;
/*     */     case 4:
/* 264 */       str = text;
/*     */     }
/* 266 */     return str;
/*     */   }
/*     */ 
/*     */   String pad(int n) {
/* 270 */     String str = "" + n;
/* 271 */     if (str.length() == 1) str = "0" + str;
/* 272 */     return str;
/*     */   }
/*     */ 
/*     */   String zeroFill(int n) {
/* 276 */     String str = "" + n;
/* 277 */     while (str.length() < this.fieldWidth)
/* 278 */       str = "0" + str;
/* 279 */     return str;
/*     */   }
/*     */ 
/*     */   public void setNPasses(int nPasses)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.StackLabeler
 * JD-Core Version:    0.6.2
 */