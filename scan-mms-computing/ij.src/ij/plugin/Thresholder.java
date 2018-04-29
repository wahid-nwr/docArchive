/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.LookUpTable;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.measure.Measurements;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.StackProcessor;
/*     */ import java.awt.Color;
/*     */ 
/*     */ public class Thresholder
/*     */   implements PlugIn, Measurements
/*     */ {
/*     */   private double minThreshold;
/*     */   private double maxThreshold;
/*     */   boolean autoThreshold;
/*     */   boolean skipDialog;
/*  18 */   static boolean fill1 = true;
/*  19 */   static boolean fill2 = true;
/*  20 */   static boolean useBW = true;
/*  21 */   static boolean useLocal = true;
/*     */   boolean convertToMask;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  25 */     this.convertToMask = arg.equals("mask");
/*  26 */     this.skipDialog = ((arg.equals("skip")) || (this.convertToMask));
/*  27 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  28 */     if (imp == null) {
/*  29 */       IJ.noImage(); return;
/*  30 */     }if (imp.getStackSize() == 1) {
/*  31 */       Undo.setup(4, imp);
/*  32 */       applyThreshold(imp);
/*  33 */       Undo.setup(5, imp);
/*     */     } else {
/*  35 */       convertStack(imp);
/*     */     }
/*     */   }
/*     */ 
/*  39 */   void convertStack(ImagePlus imp) { if (imp.getProcessor().getMinThreshold() != -808080.0D)
/*  40 */       useLocal = false;
/*  41 */     GenericDialog gd = new GenericDialog("Convert to Mask");
/*  42 */     gd.addMessage("Convert all images in stack to binary?");
/*  43 */     gd.addCheckbox("Calculate Threshold for Each Image", useLocal);
/*  44 */     gd.addCheckbox("Black Background", Prefs.blackBackground);
/*  45 */     gd.showDialog();
/*  46 */     if (gd.wasCanceled()) return;
/*  47 */     useLocal = gd.getNextBoolean();
/*  48 */     Prefs.blackBackground = gd.getNextBoolean();
/*  49 */     Undo.reset();
/*  50 */     if (useLocal)
/*  51 */       convertStackToBinary(imp);
/*     */     else
/*  53 */       applyThreshold(imp); }
/*     */ 
/*     */   void applyThreshold(ImagePlus imp)
/*     */   {
/*  57 */     imp.killRoi();
/*  58 */     ImageProcessor ip = imp.getProcessor();
/*  59 */     ip.resetBinaryThreshold();
/*  60 */     int type = imp.getType();
/*  61 */     if ((type == 1) || (type == 2)) {
/*  62 */       applyShortOrFloatThreshold(imp);
/*  63 */       return;
/*     */     }
/*  65 */     if (!imp.lock()) return;
/*  66 */     double saveMinThreshold = ip.getMinThreshold();
/*  67 */     double saveMaxThreshold = ip.getMaxThreshold();
/*  68 */     this.autoThreshold = (saveMinThreshold == -808080.0D);
/*     */ 
/*  70 */     boolean useBlackAndWhite = true;
/*  71 */     boolean noArgMacro = (IJ.macroRunning()) && (Macro.getOptions() == null);
/*  72 */     if (this.skipDialog) {
/*  73 */       fill1 = Thresholder.fill2 = useBlackAndWhite = 1;
/*  74 */     } else if ((!this.autoThreshold) && (!noArgMacro)) {
/*  75 */       GenericDialog gd = new GenericDialog("Make Binary");
/*  76 */       gd.addCheckbox("Thresholded pixels to foreground color", fill1);
/*  77 */       gd.addCheckbox("Remaining pixels to background color", fill2);
/*  78 */       gd.addMessage("");
/*  79 */       gd.addCheckbox("Black foreground, white background", useBW);
/*  80 */       gd.showDialog();
/*  81 */       if (gd.wasCanceled()) {
/*  82 */         imp.unlock(); return;
/*  83 */       }fill1 = gd.getNextBoolean();
/*  84 */       fill2 = gd.getNextBoolean();
/*  85 */       useBW = useBlackAndWhite = gd.getNextBoolean();
/*     */     } else {
/*  87 */       fill1 = Thresholder.fill2 = 1;
/*  88 */       this.convertToMask = true;
/*     */     }
/*     */ 
/*  91 */     if (type != 0)
/*  92 */       convertToByte(imp);
/*  93 */     ip = imp.getProcessor();
/*     */ 
/*  95 */     if (this.autoThreshold) {
/*  96 */       autoThreshold(ip);
/*     */     } else {
/*  98 */       if ((Recorder.record) && (!Recorder.scriptMode()) && ((!IJ.isMacro()) || (Recorder.recordInMacros)))
/*  99 */         Recorder.record("setThreshold", (int)saveMinThreshold, (int)saveMaxThreshold);
/* 100 */       this.minThreshold = saveMinThreshold;
/* 101 */       this.maxThreshold = saveMaxThreshold;
/*     */     }
/*     */ 
/* 104 */     if ((this.convertToMask) && (ip.isColorLut())) {
/* 105 */       ip.setColorModel(ip.getDefaultColorModel());
/*     */     }
/* 107 */     ip.resetThreshold();
/* 108 */     int savePixel = ip.getPixel(0, 0);
/* 109 */     if (useBlackAndWhite)
/* 110 */       ip.setColor(Color.black);
/*     */     else
/* 112 */       ip.setColor(Toolbar.getForegroundColor());
/* 113 */     ip.drawPixel(0, 0);
/* 114 */     int fcolor = ip.getPixel(0, 0);
/* 115 */     if (useBlackAndWhite)
/* 116 */       ip.setColor(Color.white);
/*     */     else
/* 118 */       ip.setColor(Toolbar.getBackgroundColor());
/* 119 */     ip.drawPixel(0, 0);
/* 120 */     int bcolor = ip.getPixel(0, 0);
/* 121 */     ip.setColor(Toolbar.getForegroundColor());
/* 122 */     ip.putPixel(0, 0, savePixel);
/*     */ 
/* 124 */     int[] lut = new int[256];
/* 125 */     for (int i = 0; i < 256; i++) {
/* 126 */       if ((i >= this.minThreshold) && (i <= this.maxThreshold))
/* 127 */         lut[i] = (fill1 ? fcolor : (byte)i);
/*     */       else {
/* 129 */         lut[i] = (fill2 ? bcolor : (byte)i);
/*     */       }
/*     */     }
/* 132 */     if (imp.getStackSize() > 1)
/* 133 */       new StackProcessor(imp.getStack(), ip).applyTable(lut);
/*     */     else
/* 135 */       ip.applyTable(lut);
/* 136 */     if (this.convertToMask) {
/* 137 */       if (!imp.isInvertedLut()) {
/* 138 */         setInvertedLut(imp);
/* 139 */         fcolor = 255 - fcolor;
/* 140 */         bcolor = 255 - bcolor;
/*     */       }
/* 142 */       if (Prefs.blackBackground)
/* 143 */         ip.invertLut();
/*     */     }
/* 145 */     if ((fill1) && (fill2) && (((fcolor == 0) && (bcolor == 255)) || ((fcolor == 255) && (bcolor == 0))))
/* 146 */       imp.getProcessor().setThreshold(fcolor, fcolor, 2);
/* 147 */     imp.updateAndRepaintWindow();
/* 148 */     imp.unlock();
/*     */   }
/*     */ 
/*     */   void applyShortOrFloatThreshold(ImagePlus imp) {
/* 152 */     if (!imp.lock()) return;
/* 153 */     int width = imp.getWidth();
/* 154 */     int height = imp.getHeight();
/* 155 */     int size = width * height;
/* 156 */     boolean isFloat = imp.getType() == 2;
/* 157 */     int nSlices = imp.getStackSize();
/* 158 */     ImageStack stack1 = imp.getStack();
/* 159 */     ImageStack stack2 = new ImageStack(width, height);
/* 160 */     ImageProcessor ip = imp.getProcessor();
/* 161 */     float t1 = (float)ip.getMinThreshold();
/* 162 */     float t2 = (float)ip.getMaxThreshold();
/* 163 */     if (t1 == -808080.0D)
/*     */     {
/* 165 */       double min = ip.getMin();
/* 166 */       double max = ip.getMax();
/* 167 */       ip = ip.convertToByte(true);
/* 168 */       autoThreshold(ip);
/* 169 */       t1 = (float)(min + (max - min) * (this.minThreshold / 255.0D));
/* 170 */       t2 = (float)(min + (max - min) * (this.maxThreshold / 255.0D));
/*     */     }
/*     */ 
/* 174 */     IJ.showStatus("Converting to mask");
/* 175 */     for (int i = 1; i <= nSlices; i++) {
/* 176 */       IJ.showProgress(i, nSlices);
/* 177 */       String label = stack1.getSliceLabel(i);
/* 178 */       ImageProcessor ip1 = stack1.getProcessor(i);
/* 179 */       ImageProcessor ip2 = new ByteProcessor(width, height);
/* 180 */       for (int j = 0; j < size; j++) {
/* 181 */         float value = ip1.getf(j);
/* 182 */         if ((value >= t1) && (value <= t2))
/* 183 */           ip2.set(j, 255);
/*     */         else
/* 185 */           ip2.set(j, 0);
/*     */       }
/* 187 */       stack2.addSlice(label, ip2);
/*     */     }
/* 189 */     imp.setStack(null, stack2);
/* 190 */     ImageStack stack = imp.getStack();
/* 191 */     stack.setColorModel(LookUpTable.createGrayscaleColorModel(!Prefs.blackBackground));
/* 192 */     imp.setStack(null, stack);
/* 193 */     if (imp.isComposite()) {
/* 194 */       CompositeImage ci = (CompositeImage)imp;
/* 195 */       ci.setMode(3);
/* 196 */       ci.resetDisplayRanges();
/* 197 */       ci.updateAndDraw();
/*     */     }
/* 199 */     imp.getProcessor().setThreshold(255.0D, 255.0D, 2);
/* 200 */     IJ.showStatus("");
/* 201 */     imp.unlock();
/*     */   }
/*     */ 
/*     */   void convertStackToBinary(ImagePlus imp) {
/* 205 */     int nSlices = imp.getStackSize();
/* 206 */     if (imp.getBitDepth() != 8) {
/* 207 */       IJ.showStatus("Converting to byte");
/* 208 */       ImageStack stack1 = imp.getStack();
/* 209 */       ImageStack stack2 = new ImageStack(imp.getWidth(), imp.getHeight());
/* 210 */       for (int i = 1; i <= nSlices; i++) {
/* 211 */         IJ.showProgress(i, nSlices);
/* 212 */         String label = stack1.getSliceLabel(i);
/* 213 */         ImageProcessor ip = stack1.getProcessor(i);
/* 214 */         ip.resetMinAndMax();
/* 215 */         stack2.addSlice(label, ip.convertToByte(true));
/*     */       }
/* 217 */       imp.setStack(null, stack2);
/*     */     }
/* 219 */     ImageStack stack = imp.getStack();
/* 220 */     IJ.showStatus("Auto-thresholding");
/* 221 */     for (int i = 1; i <= nSlices; i++) {
/* 222 */       IJ.showProgress(i, nSlices);
/* 223 */       ImageProcessor ip = stack.getProcessor(i);
/* 224 */       autoThreshold(ip);
/* 225 */       int[] lut = new int[256];
/* 226 */       for (int j = 0; j < 256; j++) {
/* 227 */         if ((j >= this.minThreshold) && (j <= this.maxThreshold))
/* 228 */           lut[j] = -1;
/*     */         else
/* 230 */           lut[j] = 0;
/*     */       }
/* 232 */       ip.applyTable(lut);
/*     */     }
/* 234 */     stack.setColorModel(LookUpTable.createGrayscaleColorModel(!Prefs.blackBackground));
/* 235 */     imp.setStack(null, stack);
/* 236 */     imp.getProcessor().setThreshold(255.0D, 255.0D, 2);
/* 237 */     if (imp.isComposite()) {
/* 238 */       CompositeImage ci = (CompositeImage)imp;
/* 239 */       ci.setMode(3);
/* 240 */       ci.resetDisplayRanges();
/* 241 */       ci.updateAndDraw();
/*     */     }
/* 243 */     IJ.showStatus("");
/*     */   }
/*     */ 
/*     */   void convertToByte(ImagePlus imp)
/*     */   {
/* 248 */     int currentSlice = imp.getCurrentSlice();
/* 249 */     ImageStack stack1 = imp.getStack();
/* 250 */     ImageStack stack2 = imp.createEmptyStack();
/* 251 */     int nSlices = imp.getStackSize();
/*     */ 
/* 253 */     for (int i = 1; i <= nSlices; i++) {
/* 254 */       String label = stack1.getSliceLabel(i);
/* 255 */       ImageProcessor ip = stack1.getProcessor(i);
/* 256 */       ip.setMinAndMax(0.0D, 255.0D);
/* 257 */       stack2.addSlice(label, ip.convertToByte(true));
/*     */     }
/* 259 */     imp.setStack(null, stack2);
/* 260 */     imp.setSlice(currentSlice);
/* 261 */     imp.setCalibration(imp.getCalibration());
/*     */   }
/*     */ 
/*     */   void setInvertedLut(ImagePlus imp) {
/* 265 */     ImageProcessor ip = imp.getProcessor();
/* 266 */     ip.invertLut();
/* 267 */     int nImages = imp.getStackSize();
/* 268 */     if (nImages == 1) {
/* 269 */       ip.invert();
/*     */     } else {
/* 271 */       ImageStack stack = imp.getStack();
/* 272 */       for (int slice = 1; slice <= nImages; slice++)
/* 273 */         stack.getProcessor(slice).invert();
/* 274 */       stack.setColorModel(ip.getColorModel());
/*     */     }
/*     */   }
/*     */ 
/*     */   void autoThreshold(ImageProcessor ip) {
/* 279 */     ip.setAutoThreshold(1, 2);
/* 280 */     this.minThreshold = ip.getMinThreshold();
/* 281 */     this.maxThreshold = ip.getMaxThreshold();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Thresholder
 * JD-Core Version:    0.6.2
 */