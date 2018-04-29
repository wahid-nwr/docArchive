/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Image;
/*     */ 
/*     */ public class MontageMaker
/*     */   implements PlugIn
/*     */ {
/*     */   private static int columns;
/*     */   private static int rows;
/*     */   private static int first;
/*     */   private static int last;
/*     */   private static int inc;
/*     */   private static int borderWidth;
/*     */   private static double scale;
/*     */   private static boolean label;
/*     */   private static boolean useForegroundColor;
/*     */   private static int saveID;
/*     */   private static int saveStackSize;
/*  19 */   private static int fontSize = 12;
/*     */   private boolean hyperstack;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  23 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  24 */     if ((imp == null) || (imp.getStackSize() == 1)) {
/*  25 */       error("Stack required");
/*  26 */       return;
/*     */     }
/*  28 */     this.hyperstack = imp.isHyperStack();
/*  29 */     if ((this.hyperstack) && (imp.getNSlices() > 1) && (imp.getNFrames() > 1)) {
/*  30 */       error("5D hyperstacks are not supported");
/*  31 */       return;
/*     */     }
/*  33 */     int channels = imp.getNChannels();
/*  34 */     if ((!this.hyperstack) && (imp.isComposite()) && (channels > 1)) {
/*  35 */       int channel = imp.getChannel();
/*  36 */       CompositeImage ci = (CompositeImage)imp;
/*  37 */       int mode = ci.getMode();
/*  38 */       if (mode == 1)
/*  39 */         ci.setMode(2);
/*  40 */       ImageStack stack = new ImageStack(imp.getWidth(), imp.getHeight());
/*  41 */       for (int c = 1; c <= channels; c++) {
/*  42 */         imp.setPositionWithoutUpdate(c, imp.getSlice(), imp.getFrame());
/*  43 */         Image img = imp.getImage();
/*  44 */         stack.addSlice(null, new ColorProcessor(img));
/*     */       }
/*  46 */       if (ci.getMode() != mode)
/*  47 */         ci.setMode(mode);
/*  48 */       imp.setPosition(channel, imp.getSlice(), imp.getFrame());
/*  49 */       imp = new ImagePlus(imp.getTitle(), stack);
/*     */     }
/*  51 */     makeMontage(imp);
/*  52 */     imp.updateImage();
/*  53 */     saveID = imp.getID();
/*  54 */     IJ.register(MontageMaker.class);
/*     */   }
/*     */ 
/*     */   public void makeMontage(ImagePlus imp) {
/*  58 */     int nSlices = imp.getStackSize();
/*  59 */     if (this.hyperstack) {
/*  60 */       nSlices = imp.getNSlices();
/*  61 */       if (nSlices == 1)
/*  62 */         nSlices = imp.getNFrames();
/*     */     }
/*  64 */     if ((columns == 0) || ((imp.getID() != saveID) && (nSlices != saveStackSize))) {
/*  65 */       columns = (int)Math.sqrt(nSlices);
/*  66 */       rows = columns;
/*  67 */       int n = nSlices - columns * rows;
/*  68 */       if (n > 0) columns += (int)Math.ceil(n / rows);
/*  69 */       scale = 1.0D;
/*  70 */       if (imp.getWidth() * columns > 800)
/*  71 */         scale = 0.5D;
/*  72 */       if (imp.getWidth() * columns > 1600)
/*  73 */         scale = 0.25D;
/*  74 */       inc = 1;
/*  75 */       first = 1;
/*  76 */       last = nSlices;
/*     */     }
/*  78 */     saveStackSize = nSlices;
/*     */ 
/*  80 */     GenericDialog gd = new GenericDialog("Make Montage", IJ.getInstance());
/*  81 */     gd.addNumericField("Columns:", columns, 0);
/*  82 */     gd.addNumericField("Rows:", rows, 0);
/*  83 */     gd.addNumericField("Scale Factor:", scale, 2);
/*  84 */     if (!this.hyperstack) {
/*  85 */       gd.addNumericField("First Slice:", first, 0);
/*  86 */       gd.addNumericField("Last Slice:", last, 0);
/*     */     }
/*  88 */     gd.addNumericField("Increment:", inc, 0);
/*  89 */     gd.addNumericField("Border Width:", borderWidth, 0);
/*  90 */     gd.addNumericField("Font Size:", fontSize, 0);
/*  91 */     gd.addCheckbox("Label Slices", label);
/*  92 */     gd.addCheckbox("Use Foreground Color", useForegroundColor);
/*  93 */     gd.showDialog();
/*  94 */     if (gd.wasCanceled())
/*  95 */       return;
/*  96 */     columns = (int)gd.getNextNumber();
/*  97 */     rows = (int)gd.getNextNumber();
/*  98 */     scale = gd.getNextNumber();
/*  99 */     if (!this.hyperstack) {
/* 100 */       first = (int)gd.getNextNumber();
/* 101 */       last = (int)gd.getNextNumber();
/*     */     }
/* 103 */     inc = (int)gd.getNextNumber();
/* 104 */     borderWidth = (int)gd.getNextNumber();
/* 105 */     fontSize = (int)gd.getNextNumber();
/* 106 */     if (borderWidth < 0) borderWidth = 0;
/* 107 */     if (first < 1) first = 1;
/* 108 */     if (last > nSlices) last = nSlices;
/* 109 */     if (first > last) {
/* 110 */       first = 1; last = nSlices;
/* 111 */     }if (inc < 1) inc = 1;
/* 112 */     if (gd.invalidNumber()) {
/* 113 */       error("Invalid number");
/* 114 */       return;
/*     */     }
/* 116 */     label = gd.getNextBoolean();
/* 117 */     useForegroundColor = gd.getNextBoolean();
/* 118 */     ImagePlus imp2 = null;
/* 119 */     if (this.hyperstack)
/* 120 */       imp2 = makeHyperstackMontage(imp, columns, rows, scale, inc, borderWidth, label);
/*     */     else
/* 122 */       imp2 = makeMontage2(imp, columns, rows, scale, first, last, inc, borderWidth, label);
/* 123 */     if (imp2 != null)
/* 124 */       imp2.show();
/*     */   }
/*     */ 
/*     */   public void makeMontage(ImagePlus imp, int columns, int rows, double scale, int first, int last, int inc, int borderWidth, boolean labels)
/*     */   {
/* 129 */     ImagePlus imp2 = makeMontage2(imp, columns, rows, scale, first, last, inc, borderWidth, labels);
/* 130 */     imp2.show();
/*     */   }
/*     */ 
/*     */   public ImagePlus makeMontage2(ImagePlus imp, int columns, int rows, double scale, int first, int last, int inc, int borderWidth, boolean labels)
/*     */   {
/* 135 */     int stackWidth = imp.getWidth();
/* 136 */     int stackHeight = imp.getHeight();
/* 137 */     int nSlices = imp.getStackSize();
/* 138 */     int width = (int)(stackWidth * scale);
/* 139 */     int height = (int)(stackHeight * scale);
/* 140 */     int montageWidth = width * columns;
/* 141 */     int montageHeight = height * rows;
/* 142 */     ImageProcessor ip = imp.getProcessor();
/* 143 */     ImageProcessor montage = ip.createProcessor(montageWidth + borderWidth / 2, montageHeight + borderWidth / 2);
/* 144 */     ImagePlus imp2 = new ImagePlus("Montage", montage);
/* 145 */     imp2.setCalibration(imp.getCalibration());
/* 146 */     montage = imp2.getProcessor();
/* 147 */     Color fgColor = Color.white;
/* 148 */     Color bgColor = Color.black;
/* 149 */     if (useForegroundColor) {
/* 150 */       fgColor = Toolbar.getForegroundColor();
/* 151 */       bgColor = Toolbar.getBackgroundColor();
/*     */     } else {
/* 153 */       boolean whiteBackground = false;
/* 154 */       if (((ip instanceof ByteProcessor)) || ((ip instanceof ColorProcessor))) {
/* 155 */         ip.setRoi(0, stackHeight - 12, stackWidth, 12);
/* 156 */         ImageStatistics stats = ImageStatistics.getStatistics(ip, 8, null);
/* 157 */         ip.resetRoi();
/* 158 */         whiteBackground = stats.mode >= 200;
/* 159 */         if (imp.isInvertedLut())
/* 160 */           whiteBackground = !whiteBackground;
/*     */       }
/* 162 */       if (whiteBackground) {
/* 163 */         fgColor = Color.black;
/* 164 */         bgColor = Color.white;
/*     */       }
/*     */     }
/* 167 */     montage.setColor(bgColor);
/* 168 */     montage.fill();
/* 169 */     montage.setColor(fgColor);
/* 170 */     Dimension screen = IJ.getScreenSize();
/* 171 */     montage.setFont(new Font("SansSerif", 0, fontSize));
/* 172 */     montage.setAntialiasedText(true);
/* 173 */     ImageStack stack = imp.getStack();
/* 174 */     int x = 0;
/* 175 */     int y = 0;
/*     */ 
/* 177 */     int slice = first;
/* 178 */     while (slice <= last) {
/* 179 */       ImageProcessor aSlice = stack.getProcessor(slice);
/* 180 */       if (scale != 1.0D)
/* 181 */         aSlice = aSlice.resize(width, height);
/* 182 */       montage.insert(aSlice, x, y);
/* 183 */       String label = stack.getShortSliceLabel(slice);
/* 184 */       if (borderWidth > 0) drawBorder(montage, x, y, width, height, borderWidth);
/* 185 */       if (labels) drawLabel(montage, slice, label, x, y, width, height, borderWidth);
/* 186 */       x += width;
/* 187 */       if (x >= montageWidth) {
/* 188 */         x = 0;
/* 189 */         y += height;
/* 190 */         if (y >= montageHeight)
/*     */           break;
/*     */       }
/* 193 */       IJ.showProgress((slice - first) / (last - first));
/* 194 */       slice += inc;
/*     */     }
/* 196 */     if (borderWidth > 0) {
/* 197 */       int w2 = borderWidth / 2;
/* 198 */       drawBorder(montage, w2, w2, montageWidth - w2, montageHeight - w2, borderWidth);
/*     */     }
/* 200 */     IJ.showProgress(1.0D);
/* 201 */     Calibration cal = imp2.getCalibration();
/* 202 */     if (cal.scaled()) {
/* 203 */       cal.pixelWidth /= scale;
/* 204 */       cal.pixelHeight /= scale;
/*     */     }
/* 206 */     imp2.setProperty("Info", "xMontage=" + columns + "\nyMontage=" + rows + "\n");
/* 207 */     return imp2;
/*     */   }
/*     */ 
/*     */   private ImagePlus makeHyperstackMontage(ImagePlus imp, int columns, int rows, double scale, int inc, int borderWidth, boolean labels)
/*     */   {
/* 212 */     ImagePlus[] channels = ChannelSplitter.split(imp);
/* 213 */     int n = channels.length;
/* 214 */     ImagePlus[] montages = new ImagePlus[n];
/* 215 */     for (int i = 0; i < n; i++) {
/* 216 */       int last = channels[i].getStackSize();
/* 217 */       montages[i] = makeMontage2(channels[i], columns, rows, scale, 1, last, inc, borderWidth, labels);
/*     */     }
/* 219 */     ImagePlus montage = new RGBStackMerge().mergeHyperstacks(montages, false);
/* 220 */     montage.setTitle("Montage");
/* 221 */     return montage;
/*     */   }
/*     */ 
/*     */   private void error(String msg) {
/* 225 */     IJ.error("Make Montage", msg);
/*     */   }
/*     */ 
/*     */   void drawBorder(ImageProcessor montage, int x, int y, int width, int height, int borderWidth) {
/* 229 */     montage.setLineWidth(borderWidth);
/* 230 */     montage.moveTo(x, y);
/* 231 */     montage.lineTo(x + width, y);
/* 232 */     montage.lineTo(x + width, y + height);
/* 233 */     montage.lineTo(x, y + height);
/* 234 */     montage.lineTo(x, y);
/*     */   }
/*     */ 
/*     */   void drawLabel(ImageProcessor montage, int slice, String label, int x, int y, int width, int height, int borderWidth) {
/* 238 */     if ((label != null) && (!label.equals("")) && (montage.getStringWidth(label) >= width)) {
/*     */       do
/* 240 */         label = label.substring(0, label.length() - 1);
/* 241 */       while ((label.length() > 1) && (montage.getStringWidth(label) >= width));
/*     */     }
/* 243 */     if ((label == null) || (label.equals("")))
/* 244 */       label = "" + slice;
/* 245 */     int swidth = montage.getStringWidth(label);
/* 246 */     x += width / 2 - swidth / 2;
/* 247 */     y -= borderWidth / 2;
/* 248 */     y += height;
/* 249 */     montage.drawString(label, x, y);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.MontageMaker
 * JD-Core Version:    0.6.2
 */