/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.StackProcessor;
/*     */ 
/*     */ public class ImageCalculator
/*     */   implements PlugIn
/*     */ {
/*  22 */   private static String[] operators = { "Add", "Subtract", "Multiply", "Divide", "AND", "OR", "XOR", "Min", "Max", "Average", "Difference", "Copy", "Transparent-zero" };
/*  23 */   private static String[] lcOperators = { "add", "sub", "mul", "div", "and", "or", "xor", "min", "max", "ave", "diff", "copy", "zero" };
/*     */   private static int operator;
/*  25 */   private static String title1 = "";
/*  26 */   private static String title2 = "";
/*  27 */   private static boolean createWindow = true;
/*     */   private static boolean floatResult;
/*     */   private boolean processStack;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  32 */     int[] wList = WindowManager.getIDList();
/*  33 */     if (wList == null) {
/*  34 */       IJ.noImage();
/*  35 */       return;
/*     */     }
/*  37 */     IJ.register(ImageCalculator.class);
/*  38 */     String[] titles = new String[wList.length];
/*  39 */     for (int i = 0; i < wList.length; i++) {
/*  40 */       ImagePlus imp = WindowManager.getImage(wList[i]);
/*  41 */       if (imp != null)
/*  42 */         titles[i] = imp.getTitle();
/*     */       else
/*  44 */         titles[i] = "";
/*     */     }
/*  46 */     GenericDialog gd = new GenericDialog("Image Calculator", IJ.getInstance());
/*     */     String defaultItem;
/*     */     String defaultItem;
/*  48 */     if (title1.equals(""))
/*  49 */       defaultItem = titles[0];
/*     */     else
/*  51 */       defaultItem = title1;
/*  52 */     gd.addChoice("Image1:", titles, defaultItem);
/*  53 */     gd.addChoice("Operation:", operators, operators[operator]);
/*  54 */     if (title2.equals(""))
/*  55 */       defaultItem = titles[0];
/*     */     else
/*  57 */       defaultItem = title2;
/*  58 */     gd.addChoice("Image2:", titles, defaultItem);
/*     */ 
/*  60 */     gd.addCheckbox("Create new window", createWindow);
/*  61 */     gd.addCheckbox("32-bit (float) result", floatResult);
/*  62 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#calculator");
/*  63 */     gd.showDialog();
/*  64 */     if (gd.wasCanceled())
/*  65 */       return;
/*  66 */     int index1 = gd.getNextChoiceIndex();
/*  67 */     title1 = titles[index1];
/*  68 */     operator = gd.getNextChoiceIndex();
/*  69 */     int index2 = gd.getNextChoiceIndex();
/*     */ 
/*  71 */     createWindow = gd.getNextBoolean();
/*  72 */     floatResult = gd.getNextBoolean();
/*  73 */     title2 = titles[index2];
/*  74 */     ImagePlus img1 = WindowManager.getImage(wList[index1]);
/*  75 */     ImagePlus img2 = WindowManager.getImage(wList[index2]);
/*  76 */     ImagePlus img3 = calculate(img1, img2, false);
/*  77 */     if (img3 != null) img3.show();
/*     */   }
/*     */ 
/*     */   public ImagePlus run(String params, ImagePlus img1, ImagePlus img2)
/*     */   {
/*  92 */     if ((img1 == null) || (img2 == null) || (params == null)) return null;
/*  93 */     operator = getOperator(params);
/*  94 */     if (operator == -1)
/*  95 */       throw new IllegalArgumentException("No valid operator");
/*  96 */     createWindow = params.indexOf("create") != -1;
/*  97 */     floatResult = (params.indexOf("32") != -1) || (params.indexOf("float") != -1);
/*  98 */     this.processStack = (params.indexOf("stack") != -1);
/*  99 */     return calculate(img1, img2, true);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void calculate(String params, ImagePlus img1, ImagePlus img2)
/*     */   {
/* 107 */     if ((img1 == null) || (img2 == null) || (params == null)) return;
/* 108 */     operator = getOperator(params);
/* 109 */     if (operator == -1) {
/* 110 */       IJ.error("Image Calculator", "No valid operator"); return;
/* 111 */     }createWindow = params.indexOf("create") != -1;
/* 112 */     floatResult = (params.indexOf("32") != -1) || (params.indexOf("float") != -1);
/* 113 */     this.processStack = (params.indexOf("stack") != -1);
/* 114 */     ImagePlus img3 = calculate(img1, img2, true);
/* 115 */     if (img3 != null) img3.show(); 
/*     */   }
/*     */ 
/*     */   int getOperator(String params)
/*     */   {
/* 119 */     params = params.toLowerCase();
/* 120 */     int op = -1;
/* 121 */     if (params.indexOf("xor") != -1)
/* 122 */       op = 6;
/* 123 */     if (op == -1) {
/* 124 */       for (int i = 0; i < lcOperators.length; i++) {
/* 125 */         if (params.indexOf(lcOperators[i]) != -1) {
/* 126 */           op = i;
/* 127 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 131 */     return op;
/*     */   }
/*     */ 
/*     */   ImagePlus calculate(ImagePlus img1, ImagePlus img2, boolean apiCall) {
/* 135 */     ImagePlus img3 = null;
/* 136 */     if ((img1.getCalibration().isSigned16Bit()) || (img2.getCalibration().isSigned16Bit()))
/* 137 */       floatResult = true;
/* 138 */     if ((floatResult) && ((img1.getBitDepth() != 32) || (img2.getBitDepth() != 32)))
/* 139 */       createWindow = true;
/* 140 */     int size1 = img1.getStackSize();
/* 141 */     int size2 = img2.getStackSize();
/* 142 */     if (apiCall) {
/* 143 */       if ((this.processStack) && ((size1 > 1) || (size2 > 1)))
/* 144 */         img3 = doStackOperation(img1, img2);
/*     */       else
/* 146 */         img3 = doOperation(img1, img2);
/* 147 */       return img3;
/*     */     }
/* 149 */     boolean stackOp = false;
/* 150 */     if (size1 > 1) {
/* 151 */       int result = IJ.setupDialog(img1, 0);
/* 152 */       if (result == 4096)
/* 153 */         return null;
/* 154 */       if (result == 32) {
/* 155 */         img3 = doStackOperation(img1, img2);
/* 156 */         stackOp = true;
/*     */       } else {
/* 158 */         img3 = doOperation(img1, img2);
/*     */       }
/*     */     } else { img3 = doOperation(img1, img2); }
/* 161 */     if (Recorder.record) {
/* 162 */       String params = operators[operator];
/* 163 */       if (createWindow) params = params + " create";
/* 164 */       if (floatResult) params = params + " 32-bit";
/* 165 */       if (stackOp) params = params + " stack";
/* 166 */       if (Recorder.scriptMode())
/* 167 */         Recorder.recordCall("ic = new ImageCalculator();\nimp3 = ic.run(\"" + params + "\", imp1, imp2);");
/*     */       else
/* 169 */         Recorder.record("imageCalculator", params, img1.getTitle(), img2.getTitle());
/* 170 */       Recorder.setCommand(null);
/*     */     }
/* 172 */     return img3;
/*     */   }
/*     */ 
/*     */   ImagePlus doStackOperation(ImagePlus img1, ImagePlus img2)
/*     */   {
/* 177 */     ImagePlus img3 = null;
/* 178 */     int size1 = img1.getStackSize();
/* 179 */     int size2 = img2.getStackSize();
/* 180 */     if ((size1 > 1) && (size2 > 1) && (size1 != size2)) {
/* 181 */       IJ.error("Image Calculator", "'Image1' and 'image2' must be stacks with the same\nnumber of slices, or 'image2' must be a single image.");
/* 182 */       return null;
/*     */     }
/* 184 */     if (createWindow) {
/* 185 */       img1 = duplicateStack(img1);
/* 186 */       if (img1 == null) {
/* 187 */         IJ.error("Calculator", "Out of memory");
/* 188 */         return null;
/*     */       }
/* 190 */       img3 = img1;
/*     */     }
/* 192 */     int mode = getBlitterMode();
/* 193 */     ImageWindow win = img1.getWindow();
/* 194 */     if (win != null)
/* 195 */       WindowManager.setCurrentWindow(win);
/* 196 */     Undo.reset();
/* 197 */     ImageStack stack1 = img1.getStack();
/* 198 */     StackProcessor sp = new StackProcessor(stack1, img1.getProcessor());
/*     */     try {
/* 200 */       if (size2 == 1)
/* 201 */         sp.copyBits(img2.getProcessor(), 0, 0, mode);
/*     */       else
/* 203 */         sp.copyBits(img2.getStack(), 0, 0, mode);
/*     */     }
/*     */     catch (IllegalArgumentException e) {
/* 206 */       IJ.error("\"" + img1.getTitle() + "\": " + e.getMessage());
/* 207 */       return null;
/*     */     }
/* 209 */     img1.setStack(null, stack1);
/* 210 */     if (img1.getType() != 0) {
/* 211 */       img1.getProcessor().resetMinAndMax();
/*     */     }
/* 213 */     if (img3 == null)
/* 214 */       img1.updateAndDraw();
/* 215 */     return img3;
/*     */   }
/*     */ 
/*     */   ImagePlus doOperation(ImagePlus img1, ImagePlus img2) {
/* 219 */     ImagePlus img3 = null;
/* 220 */     int mode = getBlitterMode();
/* 221 */     ImageProcessor ip1 = img1.getProcessor();
/* 222 */     ImageProcessor ip2 = img2.getProcessor();
/* 223 */     Calibration cal1 = img1.getCalibration();
/* 224 */     Calibration cal2 = img2.getCalibration();
/* 225 */     if (createWindow) {
/* 226 */       ip1 = createNewImage(ip1, ip2);
/*     */     } else {
/* 228 */       ImageWindow win = img1.getWindow();
/* 229 */       if (win != null)
/* 230 */         WindowManager.setCurrentWindow(win);
/* 231 */       ip1.snapshot();
/* 232 */       Undo.setup(1, img1);
/*     */     }
/* 234 */     if (floatResult) ip2 = ip2.convertToFloat(); try
/*     */     {
/* 236 */       ip1.copyBits(ip2, 0, 0, mode);
/*     */     }
/*     */     catch (IllegalArgumentException e) {
/* 239 */       IJ.error("\"" + img1.getTitle() + "\": " + e.getMessage());
/* 240 */       return null;
/*     */     }
/* 242 */     if (!(ip1 instanceof ByteProcessor))
/* 243 */       ip1.resetMinAndMax();
/* 244 */     if (createWindow) {
/* 245 */       img3 = new ImagePlus("Result of " + img1.getTitle(), ip1);
/* 246 */       img3.setCalibration(cal1);
/*     */     } else {
/* 248 */       img1.updateAndDraw();
/* 249 */     }return img3;
/*     */   }
/*     */ 
/*     */   ImageProcessor createNewImage(ImageProcessor ip1, ImageProcessor ip2) {
/* 253 */     int width = Math.min(ip1.getWidth(), ip2.getWidth());
/* 254 */     int height = Math.min(ip1.getHeight(), ip2.getHeight());
/* 255 */     ImageProcessor ip3 = ip1.createProcessor(width, height);
/* 256 */     if (floatResult) {
/* 257 */       ip1 = ip1.convertToFloat();
/* 258 */       ip3 = ip3.convertToFloat();
/*     */     }
/* 260 */     ip3.insert(ip1, 0, 0);
/* 261 */     return ip3;
/*     */   }
/*     */ 
/*     */   private int getBlitterMode() {
/* 265 */     int mode = 0;
/* 266 */     switch (operator) { case 0:
/* 267 */       mode = 3; break;
/*     */     case 1:
/* 268 */       mode = 4; break;
/*     */     case 2:
/* 269 */       mode = 5; break;
/*     */     case 3:
/* 270 */       mode = 6; break;
/*     */     case 4:
/* 271 */       mode = 9; break;
/*     */     case 5:
/* 272 */       mode = 10; break;
/*     */     case 6:
/* 273 */       mode = 11; break;
/*     */     case 7:
/* 274 */       mode = 12; break;
/*     */     case 8:
/* 275 */       mode = 13; break;
/*     */     case 9:
/* 276 */       mode = 7; break;
/*     */     case 10:
/* 277 */       mode = 8; break;
/*     */     case 11:
/* 278 */       mode = 0; break;
/*     */     case 12:
/* 279 */       mode = 14;
/*     */     }
/* 281 */     return mode;
/*     */   }
/*     */ 
/*     */   ImagePlus duplicateStack(ImagePlus img1) {
/* 285 */     Calibration cal = img1.getCalibration();
/* 286 */     ImageStack stack1 = img1.getStack();
/* 287 */     int width = stack1.getWidth();
/* 288 */     int height = stack1.getHeight();
/* 289 */     int n = stack1.getSize();
/* 290 */     ImageStack stack2 = img1.createEmptyStack();
/*     */     try {
/* 292 */       for (int i = 1; i <= n; i++) {
/* 293 */         ImageProcessor ip1 = stack1.getProcessor(i);
/* 294 */         ip1.resetRoi();
/* 295 */         ImageProcessor ip2 = ip1.crop();
/* 296 */         if (floatResult) {
/* 297 */           ip2.setCalibrationTable(cal.getCTable());
/* 298 */           ip2 = ip2.convertToFloat();
/*     */         }
/* 300 */         stack2.addSlice(stack1.getSliceLabel(i), ip2);
/*     */       }
/*     */     }
/*     */     catch (OutOfMemoryError e) {
/* 304 */       stack2.trim();
/* 305 */       stack2 = null;
/* 306 */       return null;
/*     */     }
/* 308 */     ImagePlus img3 = new ImagePlus("Result of " + img1.getTitle(), stack2);
/* 309 */     img3.setCalibration(cal);
/* 310 */     if (img3.getStackSize() == n) {
/* 311 */       int[] dim = img1.getDimensions();
/* 312 */       img3.setDimensions(dim[2], dim[3], dim[4]);
/* 313 */       if (img1.isComposite()) {
/* 314 */         img3 = new CompositeImage(img3, 0);
/* 315 */         ((CompositeImage)img3).copyLuts(img1);
/*     */       }
/* 317 */       if (img1.isHyperStack())
/* 318 */         img3.setOpenAsHyperStack(true);
/*     */     }
/* 320 */     return img3;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ImageCalculator
 * JD-Core Version:    0.6.2
 */