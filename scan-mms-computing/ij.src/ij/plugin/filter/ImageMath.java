/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.macro.Program;
/*     */ import ij.macro.Tokenizer;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class ImageMath
/*     */   implements ExtendedPlugInFilter, DialogListener
/*     */ {
/*     */   public static final String MACRO_KEY = "math.macro";
/*  12 */   private int flags = 16777311;
/*     */   private String arg;
/*     */   private ImagePlus imp;
/*     */   private boolean canceled;
/*  16 */   private double lower = -1.0D; private double upper = -1.0D;
/*     */ 
/*  18 */   private static double addValue = 25.0D;
/*  19 */   private static double mulValue = 1.25D;
/*  20 */   private static double minValue = 0.0D;
/*  21 */   private static double maxValue = 255.0D;
/*     */   private static final String defaultAndValue = "11110000";
/*  23 */   private static String andValue = "11110000";
/*     */   private static final double defaultGammaValue = 0.5D;
/*  25 */   private static double gammaValue = 0.5D;
/*  26 */   private static String macro = Prefs.get("math.macro", "v=v+50*sin(d/10)");
/*     */   private int w;
/*     */   private int h;
/*     */   private int w2;
/*     */   private int h2;
/*     */   private boolean hasX;
/*     */   private boolean hasA;
/*     */   private boolean hasD;
/*     */   private boolean hasGetPixel;
/*     */   private String macro2;
/*     */   private PlugInFilterRunner pfr;
/*     */   private GenericDialog gd;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  34 */     this.arg = arg;
/*  35 */     this.imp = imp;
/*  36 */     IJ.register(ImageMath.class);
/*  37 */     if ((imp != null) && (imp.getStackSize() == 1) && (arg.equals("macro")))
/*  38 */       this.flags |= 262144;
/*     */     else
/*  40 */       this.flags |= 32768;
/*  41 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  45 */     if (this.canceled) {
/*  46 */       return;
/*     */     }
/*  48 */     if (this.arg.equals("add")) {
/*  49 */       ip.add(addValue);
/*  50 */       return;
/*     */     }
/*     */ 
/*  53 */     if (this.arg.equals("sub")) {
/*  54 */       ip.subtract(addValue);
/*  55 */       return;
/*     */     }
/*     */ 
/*  58 */     if (this.arg.equals("mul")) {
/*  59 */       ip.multiply(mulValue);
/*  60 */       return;
/*     */     }
/*     */ 
/*  63 */     if (this.arg.equals("div")) {
/*  64 */       if ((mulValue == 0.0D) && (this.imp.getBitDepth() != 32))
/*  65 */         return;
/*  66 */       ip.multiply(1.0D / mulValue);
/*  67 */       return;
/*     */     }
/*     */ 
/*  70 */     if (this.arg.equals("and")) {
/*     */       try {
/*  72 */         ip.and(Integer.parseInt(andValue, 2));
/*     */       } catch (NumberFormatException e) {
/*  74 */         andValue = "11110000";
/*  75 */         IJ.error("Binary number required");
/*     */       }
/*  77 */       return;
/*     */     }
/*     */ 
/*  80 */     if (this.arg.equals("or")) {
/*     */       try {
/*  82 */         ip.or(Integer.parseInt(andValue, 2));
/*     */       } catch (NumberFormatException e) {
/*  84 */         andValue = "11110000";
/*  85 */         IJ.error("Binary number required");
/*     */       }
/*  87 */       return;
/*     */     }
/*     */ 
/*  90 */     if (this.arg.equals("xor")) {
/*     */       try {
/*  92 */         ip.xor(Integer.parseInt(andValue, 2));
/*     */       } catch (NumberFormatException e) {
/*  94 */         andValue = "11110000";
/*  95 */         IJ.error("Binary number required");
/*     */       }
/*  97 */       return;
/*     */     }
/*     */ 
/* 100 */     if (this.arg.equals("min")) {
/* 101 */       ip.min(minValue);
/* 102 */       if (!(ip instanceof ByteProcessor))
/* 103 */         ip.resetMinAndMax();
/* 104 */       return;
/*     */     }
/*     */ 
/* 107 */     if (this.arg.equals("max")) {
/* 108 */       ip.max(maxValue);
/* 109 */       if (!(ip instanceof ByteProcessor))
/* 110 */         ip.resetMinAndMax();
/* 111 */       return;
/*     */     }
/*     */ 
/* 114 */     if (this.arg.equals("gamma")) {
/* 115 */       if (((gammaValue < 0.1D) || (gammaValue > 5.0D)) && (!previewing())) {
/* 116 */         IJ.error("Gamma must be between 0.1 and 5.0");
/* 117 */         gammaValue = 0.5D;
/* 118 */         return;
/*     */       }
/* 120 */       ip.gamma(gammaValue);
/* 121 */       return;
/*     */     }
/*     */ 
/* 124 */     if (this.arg.equals("set")) {
/* 125 */       boolean rgb = ip instanceof ColorProcessor;
/* 126 */       if (rgb) {
/* 127 */         if (addValue > 255.0D) addValue = 255.0D;
/* 128 */         if (addValue < 0.0D) addValue = 0.0D;
/* 129 */         int ival = (int)addValue;
/* 130 */         ip.setValue(ival + (ival << 8) + (ival << 16));
/*     */       } else {
/* 132 */         ip.setValue(addValue);
/* 133 */       }ip.fill();
/* 134 */       return;
/*     */     }
/*     */ 
/* 137 */     if (this.arg.equals("log")) {
/* 138 */       ip.log();
/* 139 */       return;
/*     */     }
/*     */ 
/* 142 */     if (this.arg.equals("exp")) {
/* 143 */       ip.exp();
/* 144 */       return;
/*     */     }
/*     */ 
/* 147 */     if (this.arg.equals("sqr")) {
/* 148 */       ip.sqr();
/* 149 */       return;
/*     */     }
/*     */ 
/* 152 */     if (this.arg.equals("sqrt")) {
/* 153 */       ip.sqrt();
/* 154 */       return;
/*     */     }
/*     */ 
/* 157 */     if (this.arg.equals("reciprocal")) {
/* 158 */       if (!isFloat(ip)) return;
/* 159 */       float[] pixels = (float[])ip.getPixels();
/* 160 */       for (int i = 0; i < ip.getWidth() * ip.getHeight(); i++) {
/* 161 */         if (pixels[i] == 0.0F)
/* 162 */           pixels[i] = (0.0F / 0.0F);
/*     */         else
/* 164 */           pixels[i] = (1.0F / pixels[i]);
/*     */       }
/* 166 */       ip.resetMinAndMax();
/* 167 */       return;
/*     */     }
/*     */ 
/* 170 */     if (this.arg.equals("nan")) {
/* 171 */       setBackgroundToNaN(ip);
/* 172 */       return;
/*     */     }
/*     */ 
/* 175 */     if (this.arg.equals("abs")) {
/* 176 */       if ((!(ip instanceof FloatProcessor)) && (!this.imp.getCalibration().isSigned16Bit())) {
/* 177 */         IJ.error("32-bit or signed 16-bit image required");
/* 178 */         this.canceled = true;
/*     */       } else {
/* 180 */         ip.abs();
/* 181 */         ip.resetMinAndMax();
/*     */       }
/* 183 */       return;
/*     */     }
/*     */ 
/* 186 */     if (this.arg.equals("macro")) {
/* 187 */       applyMacro(ip);
/* 188 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean previewing()
/*     */   {
/* 194 */     return (this.gd != null) && (this.gd.getPreviewCheckbox().getState());
/*     */   }
/*     */ 
/*     */   boolean isFloat(ImageProcessor ip) {
/* 198 */     if (!(ip instanceof FloatProcessor)) {
/* 199 */       IJ.error("32-bit float image required");
/* 200 */       this.canceled = true;
/* 201 */       return false;
/*     */     }
/* 203 */     return true;
/*     */   }
/*     */ 
/*     */   void getValue(String title, String prompt, double defaultValue, int digits) {
/* 207 */     int places = Analyzer.getPrecision();
/* 208 */     if ((digits > 0) || ((int)defaultValue != defaultValue))
/* 209 */       digits = Math.max(places, 1);
/* 210 */     this.gd = new GenericDialog(title);
/* 211 */     this.gd.addNumericField(prompt, defaultValue, digits, 8, null);
/* 212 */     this.gd.addPreviewCheckbox(this.pfr);
/* 213 */     this.gd.addDialogListener(this);
/* 214 */     this.gd.showDialog();
/*     */   }
/*     */ 
/*     */   void getBinaryValue(String title, String prompt, String defaultValue) {
/* 218 */     this.gd = new GenericDialog(title);
/* 219 */     this.gd.addStringField(prompt, defaultValue);
/* 220 */     this.gd.addPreviewCheckbox(this.pfr);
/* 221 */     this.gd.addDialogListener(this);
/* 222 */     this.gd.showDialog();
/*     */   }
/*     */ 
/*     */   void getGammaValue(double defaultValue) {
/* 226 */     this.gd = new GenericDialog("Gamma");
/* 227 */     this.gd.addSlider("Value:", 0.05D, 5.0D, defaultValue);
/* 228 */     this.gd.addPreviewCheckbox(this.pfr);
/* 229 */     this.gd.addDialogListener(this);
/* 230 */     this.gd.showDialog();
/*     */   }
/*     */ 
/*     */   void setBackgroundToNaN(ImageProcessor ip)
/*     */   {
/* 235 */     if ((this.lower == -1.0D) && (this.upper == -1.0D)) {
/* 236 */       this.lower = ip.getMinThreshold();
/* 237 */       this.upper = ip.getMaxThreshold();
/* 238 */       if ((this.lower == -808080.0D) || (!(ip instanceof FloatProcessor))) {
/* 239 */         IJ.error("Thresholded 32-bit float image required");
/* 240 */         this.canceled = true;
/* 241 */         return;
/*     */       }
/*     */     }
/* 244 */     float[] pixels = (float[])ip.getPixels();
/* 245 */     int width = ip.getWidth();
/* 246 */     int height = ip.getHeight();
/*     */ 
/* 248 */     for (int y = 0; y < height; y++) {
/* 249 */       for (int x = 0; x < width; x++) {
/* 250 */         double v = pixels[(y * width + x)];
/* 251 */         if ((v < this.lower) || (v > this.upper))
/* 252 */           pixels[(y * width + x)] = (0.0F / 0.0F);
/*     */       }
/*     */     }
/* 255 */     ip.resetMinAndMax();
/*     */   }
/*     */ 
/*     */   void applyMacro(ImageProcessor ip)
/*     */   {
/* 274 */     int PCStart = 23;
/* 275 */     if (this.macro2 == null) return;
/* 276 */     if (this.macro2.indexOf("=") == -1) {
/* 277 */       IJ.error("The variable 'v' must be assigned a value (e.g., \"v=255-v\")");
/* 278 */       this.canceled = true;
/* 279 */       return;
/*     */     }
/* 281 */     macro = this.macro2;
/* 282 */     Program pgm = new Tokenizer().tokenize(macro);
/* 283 */     this.hasX = pgm.hasWord("x");
/* 284 */     this.hasA = pgm.hasWord("a");
/* 285 */     this.hasD = pgm.hasWord("d");
/* 286 */     this.hasGetPixel = pgm.hasWord("getPixel");
/* 287 */     this.w = this.imp.getWidth();
/* 288 */     this.h = this.imp.getHeight();
/* 289 */     this.w2 = (this.w / 2);
/* 290 */     this.h2 = (this.h / 2);
/* 291 */     String code = "var v,x,y,z,w,h,d,a;\nfunction dummy() {}\n" + this.macro2 + ";\n";
/*     */ 
/* 295 */     Interpreter interp = new Interpreter();
/* 296 */     interp.run(code, null);
/* 297 */     if (interp.wasError())
/* 298 */       return;
/* 299 */     Prefs.set("math.macro", macro);
/* 300 */     interp.setVariable("w", this.w);
/* 301 */     interp.setVariable("h", this.h);
/* 302 */     boolean showProgress = (this.pfr.getSliceNumber() == 1) && (!Interpreter.isBatchMode());
/* 303 */     interp.setVariable("z", this.pfr.getSliceNumber() - 1);
/* 304 */     int bitDepth = this.imp.getBitDepth();
/* 305 */     Rectangle r = ip.getRoi();
/* 306 */     int inc = r.height / 50;
/* 307 */     if (inc < 1) inc = 1;
/*     */ 
/* 310 */     if (bitDepth == 8) {
/* 311 */       byte[] pixels1 = (byte[])ip.getPixels();
/* 312 */       byte[] pixels2 = pixels1;
/* 313 */       if (this.hasGetPixel)
/* 314 */         pixels2 = new byte[this.w * this.h];
/* 315 */       for (int y = r.y; y < r.y + r.height; y++) {
/* 316 */         if ((showProgress) && (y % inc == 0))
/* 317 */           IJ.showProgress(y - r.y, r.height);
/* 318 */         interp.setVariable("y", y);
/* 319 */         for (int x = r.x; x < r.x + r.width; x++) {
/* 320 */           int index = y * this.w + x;
/* 321 */           double v = pixels1[index] & 0xFF;
/* 322 */           interp.setVariable("v", v);
/* 323 */           if (this.hasX) interp.setVariable("x", x);
/* 324 */           if (this.hasA) interp.setVariable("a", getA(x, y));
/* 325 */           if (this.hasD) interp.setVariable("d", getD(x, y));
/* 326 */           interp.run(PCStart);
/* 327 */           int v2 = (int)interp.getVariable("v");
/* 328 */           if (v2 < 0) v2 = 0;
/* 329 */           if (v2 > 255) v2 = 255;
/* 330 */           pixels2[index] = ((byte)v2);
/*     */         }
/*     */       }
/* 333 */       if (this.hasGetPixel) System.arraycopy(pixels2, 0, pixels1, 0, this.w * this.h); 
/*     */     }
/* 334 */     else if (bitDepth == 24)
/*     */     {
/* 336 */       int[] pixels1 = (int[])ip.getPixels();
/* 337 */       int[] pixels2 = pixels1;
/* 338 */       if (this.hasGetPixel)
/* 339 */         pixels2 = new int[this.w * this.h];
/* 340 */       for (int y = r.y; y < r.y + r.height; y++) {
/* 341 */         if ((showProgress) && (y % inc == 0))
/* 342 */           IJ.showProgress(y - r.y, r.height);
/* 343 */         interp.setVariable("y", y);
/* 344 */         for (int x = r.x; x < r.x + r.width; x++) {
/* 345 */           if (this.hasX) interp.setVariable("x", x);
/* 346 */           if (this.hasA) interp.setVariable("a", getA(x, y));
/* 347 */           if (this.hasD) interp.setVariable("d", getD(x, y));
/* 348 */           int index = y * this.w + x;
/* 349 */           int rgb = pixels1[index];
/* 350 */           if (this.hasGetPixel) {
/* 351 */             interp.setVariable("v", rgb);
/* 352 */             interp.run(PCStart);
/* 353 */             rgb = (int)interp.getVariable("v");
/*     */           } else {
/* 355 */             int red = (rgb & 0xFF0000) >> 16;
/* 356 */             int green = (rgb & 0xFF00) >> 8;
/* 357 */             int blue = rgb & 0xFF;
/* 358 */             interp.setVariable("v", red);
/* 359 */             interp.run(PCStart);
/* 360 */             red = (int)interp.getVariable("v");
/* 361 */             if (red < 0) red = 0; if (red > 255) red = 255;
/* 362 */             interp.setVariable("v", green);
/* 363 */             interp.run(PCStart);
/* 364 */             green = (int)interp.getVariable("v");
/* 365 */             if (green < 0) green = 0; if (green > 255) green = 255;
/* 366 */             interp.setVariable("v", blue);
/* 367 */             interp.run(PCStart);
/* 368 */             blue = (int)interp.getVariable("v");
/* 369 */             if (blue < 0) blue = 0; if (blue > 255) blue = 255;
/* 370 */             rgb = 0xFF000000 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
/*     */           }
/* 372 */           pixels2[index] = rgb;
/*     */         }
/*     */       }
/* 375 */       if (this.hasGetPixel) System.arraycopy(pixels2, 0, pixels1, 0, this.w * this.h); 
/*     */     }
/* 377 */     else { for (int y = r.y; y < r.y + r.height; y++) {
/* 378 */         if ((showProgress) && (y % inc == 0))
/* 379 */           IJ.showProgress(y - r.y, r.height);
/* 380 */         interp.setVariable("y", y);
/* 381 */         for (int x = r.x; x < r.x + r.width; x++) {
/* 382 */           double v = ip.getPixelValue(x, y);
/* 383 */           interp.setVariable("v", v);
/* 384 */           if (this.hasX) interp.setVariable("x", x);
/* 385 */           if (this.hasA) interp.setVariable("a", getA(x, y));
/* 386 */           if (this.hasD) interp.setVariable("d", getD(x, y));
/* 387 */           interp.run(PCStart);
/* 388 */           ip.putPixelValue(x, y, interp.getVariable("v"));
/*     */         }
/*     */       }
/*     */     }
/* 392 */     if (showProgress)
/* 393 */       IJ.showProgress(1.0D);
/* 394 */     if (this.pfr.getSliceNumber() == 1)
/* 395 */       ip.resetMinAndMax();
/*     */   }
/*     */ 
/*     */   final double getD(int x, int y) {
/* 399 */     double dx = x - this.w2;
/* 400 */     double dy = y - this.h2;
/* 401 */     return Math.sqrt(dx * dx + dy * dy);
/*     */   }
/*     */ 
/*     */   final double getA(int x, int y) {
/* 405 */     double angle = Math.atan2(this.h - y - 1 - this.h2, x - this.w2);
/* 406 */     if (angle < 0.0D) angle += 6.283185307179586D;
/* 407 */     return angle;
/*     */   }
/*     */ 
/*     */   void getMacro(String macro) {
/* 411 */     this.gd = new GenericDialog("Expression Evaluator");
/* 412 */     this.gd.addStringField("Code:", macro, 42);
/* 413 */     this.gd.setInsets(0, 40, 0);
/* 414 */     this.gd.addMessage("v=pixel value, x,y&z=pixel coordinates, w=image width,\nh=image height, a=angle, d=distance from center\n");
/* 415 */     this.gd.setInsets(5, 40, 0);
/* 416 */     this.gd.addPreviewCheckbox(this.pfr);
/* 417 */     this.gd.addDialogListener(this);
/* 418 */     this.gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#math-macro");
/* 419 */     this.gd.showDialog();
/*     */   }
/*     */ 
/*     */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
/* 423 */     this.pfr = pfr;
/* 424 */     if (this.arg.equals("macro")) {
/* 425 */       getMacro(macro);
/* 426 */     } else if (this.arg.equals("add")) {
/* 427 */       getValue("Add", "Value: ", addValue, 0);
/* 428 */     } else if (this.arg.equals("sub")) {
/* 429 */       getValue("Subtract", "Value: ", addValue, 0);
/* 430 */     } else if (this.arg.equals("mul")) {
/* 431 */       getValue("Multiply", "Value: ", mulValue, 2);
/* 432 */     } else if (this.arg.equals("div")) {
/* 433 */       getValue("Divide", "Value: ", mulValue, 2);
/* 434 */     } else if (this.arg.equals("and")) {
/* 435 */       getBinaryValue("AND", "Value (binary): ", andValue);
/* 436 */     } else if (this.arg.equals("or")) {
/* 437 */       getBinaryValue("OR", "Value (binary): ", andValue);
/* 438 */     } else if (this.arg.equals("xor")) {
/* 439 */       getBinaryValue("XOR", "Value (binary): ", andValue);
/* 440 */     } else if (this.arg.equals("min")) {
/* 441 */       getValue("Min", "Value: ", minValue, 0);
/* 442 */     } else if (this.arg.equals("max")) {
/* 443 */       getValue("Max", "Value: ", maxValue, 0);
/* 444 */     } else if (this.arg.equals("gamma")) {
/* 445 */       getGammaValue(gammaValue);
/* 446 */     } else if (this.arg.equals("set")) {
/* 447 */       boolean rgb = imp.getBitDepth() == 24;
/* 448 */       String prompt = rgb ? "Value (0-255): " : "Value: ";
/* 449 */       getValue("Set", prompt, addValue, 0);
/*     */     }
/* 451 */     if ((this.gd != null) && (this.gd.wasCanceled())) {
/* 452 */       return 4096;
/*     */     }
/* 454 */     return IJ.setupDialog(imp, this.flags);
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 458 */     if (this.arg.equals("macro")) {
/* 459 */       String str = gd.getNextString();
/* 460 */       if ((previewing()) && (this.macro2 != null) && (!str.equals(this.macro2)))
/* 461 */         gd.getPreviewCheckbox().setState(false);
/* 462 */       this.macro2 = str;
/* 463 */     } else if ((this.arg.equals("add")) || (this.arg.equals("sub")) || (this.arg.equals("set"))) {
/* 464 */       addValue = gd.getNextNumber();
/* 465 */     } else if ((this.arg.equals("mul")) || (this.arg.equals("div"))) {
/* 466 */       mulValue = gd.getNextNumber();
/* 467 */     } else if ((this.arg.equals("and")) || (this.arg.equals("or")) || (this.arg.equals("xor"))) {
/* 468 */       andValue = gd.getNextString();
/* 469 */     } else if (this.arg.equals("min")) {
/* 470 */       minValue = gd.getNextNumber();
/* 471 */     } else if (this.arg.equals("max")) {
/* 472 */       maxValue = gd.getNextNumber();
/* 473 */     } else if (this.arg.equals("gamma")) {
/* 474 */       gammaValue = gd.getNextNumber();
/* 475 */     }this.canceled = gd.invalidNumber();
/* 476 */     if ((gd.wasOKed()) && (this.canceled)) {
/* 477 */       IJ.error("Value is invalid.");
/* 478 */       return false;
/*     */     }
/* 480 */     return true;
/*     */   }
/*     */ 
/*     */   public void setNPasses(int nPasses)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.ImageMath
 * JD-Core Version:    0.6.2
 */