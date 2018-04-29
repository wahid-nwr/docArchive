/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Menus;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.io.FileSaver;
/*     */ import ij.plugin.frame.LineWidthAdjuster;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatBlitter;
/*     */ import ij.process.ImageConverter;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ 
/*     */ public class Options
/*     */   implements PlugIn
/*     */ {
/*     */   public void run(String arg)
/*     */   {
/*  15 */     if (arg.equals("misc")) {
/*  16 */       miscOptions(); return;
/*  17 */     }if (arg.equals("line")) {
/*  18 */       lineWidth(); return;
/*  19 */     }if (arg.equals("io")) {
/*  20 */       io(); return;
/*  21 */     }if (arg.equals("conv")) {
/*  22 */       conversions(); return;
/*  23 */     }if (arg.equals("display")) {
/*  24 */       appearance(); return;
/*  25 */     }if (arg.equals("dicom")) {
/*  26 */       dicom(); return;
/*     */     }
/*     */   }
/*     */ 
/*     */   void miscOptions() {
/*  31 */     String key = IJ.isMacintosh() ? "command" : "control";
/*  32 */     GenericDialog gd = new GenericDialog("Miscellaneous Options", IJ.getInstance());
/*  33 */     gd.addStringField("Divide by zero value:", "" + FloatBlitter.divideByZeroValue, 10);
/*  34 */     gd.addCheckbox("Use pointer cursor", Prefs.usePointerCursor);
/*  35 */     gd.addCheckbox("Hide \"Process Stack?\" dialog", IJ.hideProcessStackDialog);
/*  36 */     gd.addCheckbox("Require " + key + " key for shortcuts", Prefs.requireControlKey);
/*  37 */     gd.addCheckbox("Move isolated plugins to Misc. menu", Prefs.moveToMisc);
/*  38 */     gd.addCheckbox("Run single instance listener", Prefs.runSocketListener);
/*  39 */     gd.addCheckbox("Debug mode", IJ.debugMode);
/*  40 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/edit.html#misc");
/*  41 */     gd.showDialog();
/*  42 */     if (gd.wasCanceled()) {
/*  43 */       return;
/*     */     }
/*  45 */     String divValue = gd.getNextString();
/*  46 */     if ((divValue.equalsIgnoreCase("infinity")) || (divValue.equalsIgnoreCase("infinite"))) {
/*  47 */       FloatBlitter.divideByZeroValue = (1.0F / 1.0F);
/*  48 */     } else if (divValue.equalsIgnoreCase("NaN")) {
/*  49 */       FloatBlitter.divideByZeroValue = (0.0F / 0.0F);
/*  50 */     } else if (divValue.equalsIgnoreCase("max")) {
/*  51 */       FloatBlitter.divideByZeroValue = 3.4028235E+38F; } else {
/*     */       Float f;
/*     */       try {
/*  54 */         f = new Float(divValue); } catch (NumberFormatException e) {
/*  55 */         f = null;
/*  56 */       }if (f != null)
/*  57 */         FloatBlitter.divideByZeroValue = f.floatValue();
/*     */     }
/*  59 */     IJ.register(FloatBlitter.class);
/*     */ 
/*  61 */     Prefs.usePointerCursor = gd.getNextBoolean();
/*  62 */     IJ.hideProcessStackDialog = gd.getNextBoolean();
/*  63 */     Prefs.requireControlKey = gd.getNextBoolean();
/*  64 */     Prefs.moveToMisc = gd.getNextBoolean();
/*  65 */     Prefs.runSocketListener = gd.getNextBoolean();
/*  66 */     IJ.debugMode = gd.getNextBoolean();
/*     */   }
/*     */ 
/*     */   void lineWidth() {
/*  70 */     int width = (int)IJ.getNumber("Line Width:", Line.getWidth());
/*  71 */     if (width == -2147483648) return;
/*  72 */     Line.setWidth(width);
/*  73 */     LineWidthAdjuster.update();
/*  74 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  75 */     if ((imp != null) && (imp.isProcessor())) {
/*  76 */       ImageProcessor ip = imp.getProcessor();
/*  77 */       ip.setLineWidth(Line.getWidth());
/*  78 */       Roi roi = imp.getRoi();
/*  79 */       if ((roi != null) && (roi.isLine())) imp.draw();
/*     */     }
/*     */   }
/*     */ 
/*     */   void io()
/*     */   {
/*  85 */     GenericDialog gd = new GenericDialog("I/O Options");
/*  86 */     gd.addNumericField("JPEG quality (0-100):", FileSaver.getJpegQuality(), 0, 3, "");
/*  87 */     gd.addNumericField("GIF and PNG transparent index:", Prefs.getTransparentIndex(), 0, 3, "");
/*  88 */     gd.addStringField("File extension for tables:", Prefs.get("options.ext", ".txt"), 4);
/*  89 */     gd.addCheckbox("Use JFileChooser to open/save", Prefs.useJFileChooser);
/*  90 */     if (!IJ.isMacOSX())
/*  91 */       gd.addCheckbox("Use_file chooser to import sequences", Prefs.useFileChooser);
/*  92 */     gd.addCheckbox("Save TIFF and raw in Intel byte order", Prefs.intelByteOrder);
/*     */ 
/*  94 */     gd.setInsets(15, 20, 0);
/*  95 */     gd.addMessage("Results Table Options");
/*  96 */     gd.setInsets(3, 40, 0);
/*  97 */     gd.addCheckbox("Copy_column headers", Prefs.copyColumnHeaders);
/*  98 */     gd.setInsets(0, 40, 0);
/*  99 */     gd.addCheckbox("Copy_row numbers", !Prefs.noRowNumbers);
/* 100 */     gd.setInsets(0, 40, 0);
/* 101 */     gd.addCheckbox("Save_column headers", !Prefs.dontSaveHeaders);
/* 102 */     gd.setInsets(0, 40, 0);
/* 103 */     gd.addCheckbox("Save_row numbers", !Prefs.dontSaveRowNumbers);
/*     */ 
/* 105 */     gd.showDialog();
/* 106 */     if (gd.wasCanceled())
/* 107 */       return;
/* 108 */     int quality = (int)gd.getNextNumber();
/* 109 */     if (quality < 0) quality = 0;
/* 110 */     if (quality > 100) quality = 100;
/* 111 */     FileSaver.setJpegQuality(quality);
/* 112 */     int transparentIndex = (int)gd.getNextNumber();
/* 113 */     Prefs.setTransparentIndex(transparentIndex);
/* 114 */     String extension = gd.getNextString();
/* 115 */     if (!extension.startsWith("."))
/* 116 */       extension = "." + extension;
/* 117 */     Prefs.set("options.ext", extension);
/* 118 */     Prefs.useJFileChooser = gd.getNextBoolean();
/* 119 */     if (!IJ.isMacOSX())
/* 120 */       Prefs.useFileChooser = gd.getNextBoolean();
/* 121 */     Prefs.intelByteOrder = gd.getNextBoolean();
/* 122 */     Prefs.copyColumnHeaders = gd.getNextBoolean();
/* 123 */     Prefs.noRowNumbers = !gd.getNextBoolean();
/* 124 */     Prefs.dontSaveHeaders = !gd.getNextBoolean();
/* 125 */     Prefs.dontSaveRowNumbers = !gd.getNextBoolean();
/*     */   }
/*     */ 
/*     */   void conversions()
/*     */   {
/* 131 */     double[] weights = ColorProcessor.getWeightingFactors();
/* 132 */     boolean weighted = (weights[0] != 0.3333333333333333D) || (weights[1] != 0.3333333333333333D) || (weights[2] != 0.3333333333333333D);
/*     */ 
/* 134 */     GenericDialog gd = new GenericDialog("Conversion Options");
/* 135 */     gd.addCheckbox("Scale When Converting", ImageConverter.getDoScaling());
/* 136 */     String prompt = "Weighted RGB Conversions";
/* 137 */     if (weighted)
/* 138 */       prompt = prompt + " (" + IJ.d2s(weights[0]) + "," + IJ.d2s(weights[1]) + "," + IJ.d2s(weights[2]) + ")";
/* 139 */     gd.addCheckbox(prompt, weighted);
/* 140 */     gd.showDialog();
/* 141 */     if (gd.wasCanceled())
/* 142 */       return;
/* 143 */     ImageConverter.setDoScaling(gd.getNextBoolean());
/* 144 */     Prefs.weightedColor = gd.getNextBoolean();
/* 145 */     if (!Prefs.weightedColor)
/* 146 */       ColorProcessor.setWeightingFactors(0.3333333333333333D, 0.3333333333333333D, 0.3333333333333333D);
/* 147 */     else if ((Prefs.weightedColor) && (!weighted))
/* 148 */       ColorProcessor.setWeightingFactors(0.299D, 0.587D, 0.114D);
/*     */   }
/*     */ 
/*     */   void appearance()
/*     */   {
/* 153 */     GenericDialog gd = new GenericDialog("Appearance", IJ.getInstance());
/* 154 */     gd.addCheckbox("Interpolate zoomed images", Prefs.interpolateScaledImages);
/* 155 */     gd.addCheckbox("Open images at 100%", Prefs.open100Percent);
/* 156 */     gd.addCheckbox("Black canvas", Prefs.blackCanvas);
/* 157 */     gd.addCheckbox("No image border", Prefs.noBorder);
/* 158 */     gd.addCheckbox("Use inverting lookup table", Prefs.useInvertingLut);
/* 159 */     gd.addCheckbox("Antialiased tool icons", Prefs.antialiasedTools);
/* 160 */     gd.addNumericField("Menu font size:", Menus.getFontSize(), 0, 3, "points");
/* 161 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/edit.html#appearance");
/* 162 */     gd.showDialog();
/* 163 */     if (gd.wasCanceled())
/* 164 */       return;
/* 165 */     boolean interpolate = gd.getNextBoolean();
/* 166 */     Prefs.open100Percent = gd.getNextBoolean();
/* 167 */     boolean blackCanvas = gd.getNextBoolean();
/* 168 */     boolean noBorder = gd.getNextBoolean();
/* 169 */     boolean useInvertingLut = gd.getNextBoolean();
/* 170 */     boolean antialiasedTools = gd.getNextBoolean();
/* 171 */     boolean change = antialiasedTools != Prefs.antialiasedTools;
/* 172 */     Prefs.antialiasedTools = antialiasedTools;
/* 173 */     if (change) Toolbar.getInstance().repaint();
/* 174 */     int menuSize = (int)gd.getNextNumber();
/* 175 */     if (interpolate != Prefs.interpolateScaledImages) {
/* 176 */       Prefs.interpolateScaledImages = interpolate;
/* 177 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 178 */       if (imp != null)
/* 179 */         imp.draw();
/*     */     }
/* 181 */     if (blackCanvas != Prefs.blackCanvas) {
/* 182 */       Prefs.blackCanvas = blackCanvas;
/* 183 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 184 */       if (imp != null) {
/* 185 */         ImageWindow win = imp.getWindow();
/* 186 */         if (win != null) {
/* 187 */           if (Prefs.blackCanvas) {
/* 188 */             win.setForeground(Color.white);
/* 189 */             win.setBackground(Color.black);
/*     */           } else {
/* 191 */             win.setForeground(Color.black);
/* 192 */             win.setBackground(Color.white);
/*     */           }
/* 194 */           imp.repaintWindow();
/*     */         }
/*     */       }
/*     */     }
/* 198 */     if (noBorder != Prefs.noBorder) {
/* 199 */       Prefs.noBorder = noBorder;
/* 200 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 201 */       if (imp != null) imp.repaintWindow();
/*     */     }
/* 203 */     if (useInvertingLut != Prefs.useInvertingLut) {
/* 204 */       invertLuts(useInvertingLut);
/* 205 */       Prefs.useInvertingLut = useInvertingLut;
/*     */     }
/* 207 */     if ((menuSize != Menus.getFontSize()) && (!IJ.isMacintosh())) {
/* 208 */       Menus.setFontSize(menuSize);
/* 209 */       IJ.showMessage("Appearance", "Restart ImageJ to use the new font size");
/*     */     }
/*     */   }
/*     */ 
/*     */   void invertLuts(boolean useInvertingLut) {
/* 214 */     int[] list = WindowManager.getIDList();
/* 215 */     if (list == null) return;
/* 216 */     for (int i = 0; i < list.length; i++) {
/* 217 */       ImagePlus imp = WindowManager.getImage(list[i]);
/* 218 */       if (imp == null) return;
/* 219 */       ImageProcessor ip = imp.getProcessor();
/* 220 */       if ((useInvertingLut != ip.isInvertedLut()) && (!ip.isColorLut())) {
/* 221 */         ip.invertLut();
/* 222 */         int nImages = imp.getStackSize();
/* 223 */         if (nImages == 1) {
/* 224 */           ip.invert();
/*     */         } else {
/* 226 */           ImageStack stack2 = imp.getStack();
/* 227 */           for (int slice = 1; slice <= nImages; slice++)
/* 228 */             stack2.getProcessor(slice).invert();
/* 229 */           stack2.setColorModel(ip.getColorModel());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void dicom()
/*     */   {
/* 237 */     GenericDialog gd = new GenericDialog("DICOM Options");
/* 238 */     gd.addCheckbox("Open as 32-bit float", Prefs.openDicomsAsFloat);
/*     */ 
/* 240 */     gd.addMessage("Orthogonal Views");
/* 241 */     gd.setInsets(5, 40, 0);
/* 242 */     gd.addCheckbox("Rotate YZ", Prefs.rotateYZ);
/* 243 */     gd.setInsets(0, 40, 0);
/* 244 */     gd.addCheckbox("Flip XZ", Prefs.flipXZ);
/* 245 */     gd.showDialog();
/* 246 */     if (gd.wasCanceled())
/* 247 */       return;
/* 248 */     Prefs.openDicomsAsFloat = gd.getNextBoolean();
/*     */ 
/* 250 */     Prefs.rotateYZ = gd.getNextBoolean();
/* 251 */     Prefs.flipXZ = gd.getNextBoolean();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Options
 * JD-Core Version:    0.6.2
 */