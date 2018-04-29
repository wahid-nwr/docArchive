/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class NewImage
/*     */ {
/*     */   public static final int GRAY8 = 0;
/*     */   public static final int GRAY16 = 1;
/*     */   public static final int GRAY32 = 2;
/*     */   public static final int RGB = 3;
/*     */   public static final int FILL_BLACK = 1;
/*     */   public static final int FILL_RAMP = 2;
/*     */   public static final int FILL_WHITE = 4;
/*     */   public static final int CHECK_AVAILABLE_MEMORY = 8;
/*     */   private static final int OLD_FILL_WHITE = 0;
/*     */   static final String TYPE = "new.type";
/*     */   static final String FILL = "new.fill";
/*     */   static final String WIDTH = "new.width";
/*     */   static final String HEIGHT = "new.height";
/*     */   static final String SLICES = "new.slices";
/*  24 */   private static String name = "Untitled";
/*  25 */   private static int width = Prefs.getInt("new.width", 400);
/*  26 */   private static int height = Prefs.getInt("new.height", 400);
/*  27 */   private static int slices = Prefs.getInt("new.slices", 1);
/*  28 */   private static int type = Prefs.getInt("new.type", 0);
/*  29 */   private static int fillWith = Prefs.getInt("new.fill", 0);
/*  30 */   private static String[] types = { "8-bit", "16-bit", "32-bit", "RGB" };
/*  31 */   private static String[] fill = { "White", "Black", "Ramp" };
/*     */ 
/*     */   public NewImage()
/*     */   {
/*  35 */     openImage();
/*     */   }
/*     */ 
/*     */   static boolean createStack(ImagePlus imp, ImageProcessor ip, int nSlices, int type, int options) {
/*  39 */     int fill = getFill(options);
/*  40 */     int width = imp.getWidth();
/*  41 */     int height = imp.getHeight();
/*  42 */     long bytesPerPixel = 1L;
/*  43 */     if (type == 1) bytesPerPixel = 2L;
/*  44 */     else if ((type == 2) || (type == 3)) bytesPerPixel = 4L;
/*  45 */     long size = width * height * nSlices * bytesPerPixel;
/*  46 */     boolean bigStack = size / 1048576L >= 50L;
/*  47 */     String size2 = size / 1048576L + "MB (" + width + "x" + height + "x" + nSlices + ")";
/*  48 */     if ((options & 0x8) != 0) {
/*  49 */       long max = IJ.maxMemory();
/*  50 */       if (max > 0L) {
/*  51 */         long inUse = IJ.currentMemory();
/*  52 */         long available = max - inUse;
/*  53 */         if (size > available)
/*  54 */           System.gc();
/*  55 */         inUse = IJ.currentMemory();
/*  56 */         available = max - inUse;
/*  57 */         if (size > available) {
/*  58 */           IJ.error("Insufficient Memory", "There is not enough free memory to allocate a \n" + size2 + " stack.\n \n" + "Memory available: " + available / 1048576L + "MB\n" + "Memory in use: " + IJ.freeMemory() + "\n \n" + "More information can be found in the \"Memory\"\n" + "sections of the ImageJ installation notes at\n" + "\"" + "http://imagej.nih.gov/ij" + "/docs/install/\".");
/*     */ 
/*  65 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*  69 */     ImageStack stack = imp.createEmptyStack();
/*  70 */     int inc = nSlices / 40;
/*  71 */     if (inc < 1) inc = 1;
/*  72 */     IJ.showStatus("Allocating " + size2 + ". Press 'Esc' to abort.");
/*  73 */     IJ.resetEscape();
/*     */     try {
/*  75 */       stack.addSlice(null, ip);
/*  76 */       for (int i = 2; i <= nSlices; i++) {
/*  77 */         if ((i % inc == 0) && (bigStack))
/*  78 */           IJ.showProgress(i, nSlices);
/*  79 */         Object pixels2 = null;
/*  80 */         switch (type) { case 0:
/*  81 */           pixels2 = new byte[width * height]; break;
/*     */         case 1:
/*  82 */           pixels2 = new short[width * height]; break;
/*     */         case 2:
/*  83 */           pixels2 = new float[width * height]; break;
/*     */         case 3:
/*  84 */           pixels2 = new int[width * height];
/*     */         }
/*  86 */         if ((fill != 1) || (type == 3))
/*  87 */           System.arraycopy(ip.getPixels(), 0, pixels2, 0, width * height);
/*  88 */         stack.addSlice(null, pixels2);
/*  89 */         if (IJ.escapePressed()) { IJ.beep(); break; }
/*     */       }
/*     */     }
/*     */     catch (OutOfMemoryError e) {
/*  93 */       IJ.outOfMemory(imp.getTitle());
/*  94 */       stack.trim();
/*     */     }
/*  96 */     if (bigStack)
/*  97 */       IJ.showProgress(nSlices, nSlices);
/*  98 */     if (stack.getSize() > 1)
/*  99 */       imp.setStack(null, stack);
/* 100 */     return true;
/*     */   }
/*     */ 
/*     */   static ImagePlus createImagePlus()
/*     */   {
/* 108 */     return new ImagePlus();
/*     */   }
/*     */ 
/*     */   static int getFill(int options) {
/* 112 */     int fill = options & 0x7;
/* 113 */     if (fill == 0)
/* 114 */       fill = 4;
/* 115 */     if ((fill == 7) || (fill == 6) || (fill == 3) || (fill == 5))
/* 116 */       fill = 1;
/* 117 */     return fill;
/*     */   }
/*     */ 
/*     */   public static ImagePlus createByteImage(String title, int width, int height, int slices, int options) {
/* 121 */     int fill = getFill(options);
/* 122 */     int size = getSize(width, height);
/* 123 */     if (size < 0) return null;
/* 124 */     byte[] pixels = new byte[size];
/* 125 */     switch (fill) {
/*     */     case 4:
/* 127 */       for (int i = 0; i < width * height; i++)
/* 128 */         pixels[i] = -1;
/* 129 */       break;
/*     */     case 1:
/* 131 */       break;
/*     */     case 2:
/* 133 */       byte[] ramp = new byte[width];
/* 134 */       for (int i = 0; i < width; i++) {
/* 135 */         ramp[i] = ((byte)(int)(i * 256.0D / width));
/*     */       }
/* 137 */       for (int y = 0; y < height; y++) {
/* 138 */         int offset = y * width;
/* 139 */         for (int x = 0; x < width; x++)
/* 140 */           pixels[(offset++)] = ramp[x];
/*     */       }
/*     */     case 3:
/*     */     }
/* 144 */     ImageProcessor ip = new ByteProcessor(width, height, pixels, null);
/* 145 */     ImagePlus imp = createImagePlus();
/* 146 */     imp.setProcessor(title, ip);
/* 147 */     if (slices > 1) {
/* 148 */       boolean ok = createStack(imp, ip, slices, 0, options);
/* 149 */       if (!ok) imp = null;
/*     */     }
/* 151 */     return imp;
/*     */   }
/*     */ 
/*     */   public static ImagePlus createRGBImage(String title, int width, int height, int slices, int options) {
/* 155 */     int fill = getFill(options);
/* 156 */     int size = getSize(width, height);
/* 157 */     if (size < 0) return null;
/* 158 */     int[] pixels = new int[size];
/* 159 */     switch (fill) {
/*     */     case 4:
/* 161 */       for (int i = 0; i < width * height; i++)
/* 162 */         pixels[i] = -1;
/* 163 */       break;
/*     */     case 1:
/* 165 */       for (int i = 0; i < width * height; i++)
/* 166 */         pixels[i] = -16777216;
/* 167 */       break;
/*     */     case 2:
/* 170 */       int[] ramp = new int[width];
/* 171 */       for (int i = 0; i < width; i++)
/*     */       {
/*     */         int b;
/*     */         int g;
/* 172 */         int r = g = b = (byte)(int)(i * 256.0D / width);
/* 173 */         ramp[i] = (0xFF000000 | r << 16 & 0xFF0000 | g << 8 & 0xFF00 | b & 0xFF);
/*     */       }
/* 175 */       for (int y = 0; y < height; y++) {
/* 176 */         int offset = y * width;
/* 177 */         for (int x = 0; x < width; x++)
/* 178 */           pixels[(offset++)] = ramp[x];
/*     */       }
/*     */     case 3:
/*     */     }
/* 182 */     ImageProcessor ip = new ColorProcessor(width, height, pixels);
/* 183 */     ImagePlus imp = createImagePlus();
/* 184 */     imp.setProcessor(title, ip);
/* 185 */     if (slices > 1) {
/* 186 */       boolean ok = createStack(imp, ip, slices, 3, options);
/* 187 */       if (!ok) imp = null;
/*     */     }
/* 189 */     return imp;
/*     */   }
/*     */ 
/*     */   public static ImagePlus createShortImage(String title, int width, int height, int slices, int options)
/*     */   {
/* 194 */     int fill = getFill(options);
/* 195 */     int size = getSize(width, height);
/* 196 */     if (size < 0) return null;
/* 197 */     short[] pixels = new short[size];
/* 198 */     switch (fill) { case 1:
/*     */     case 4:
/* 200 */       break;
/*     */     case 2:
/* 202 */       short[] ramp = new short[width];
/* 203 */       for (int i = 0; i < width; i++) {
/* 204 */         ramp[i] = ((short)(int)(i * 65536.0D / width + 0.5D));
/*     */       }
/* 206 */       for (int y = 0; y < height; y++) {
/* 207 */         int offset = y * width;
/* 208 */         for (int x = 0; x < width; x++)
/* 209 */           pixels[(offset++)] = ramp[x];
/*     */       }
/*     */     case 3:
/*     */     }
/* 213 */     ImageProcessor ip = new ShortProcessor(width, height, pixels, null);
/* 214 */     if (fill == 4)
/* 215 */       ip.invertLut();
/* 216 */     ImagePlus imp = createImagePlus();
/* 217 */     imp.setProcessor(title, ip);
/* 218 */     if (slices > 1) {
/* 219 */       boolean ok = createStack(imp, ip, slices, 1, options);
/* 220 */       if (!ok) imp = null;
/*     */     }
/* 222 */     imp.getProcessor().setMinAndMax(0.0D, 65535.0D);
/* 223 */     return imp;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static ImagePlus createUnsignedShortImage(String title, int width, int height, int slices, int options)
/*     */   {
/* 231 */     return createShortImage(title, width, height, slices, options);
/*     */   }
/*     */ 
/*     */   public static ImagePlus createFloatImage(String title, int width, int height, int slices, int options) {
/* 235 */     int fill = getFill(options);
/* 236 */     int size = getSize(width, height);
/* 237 */     if (size < 0) return null;
/* 238 */     float[] pixels = new float[size];
/* 239 */     switch (fill) { case 1:
/*     */     case 4:
/* 241 */       break;
/*     */     case 2:
/* 243 */       float[] ramp = new float[width];
/* 244 */       for (int i = 0; i < width; i++) {
/* 245 */         ramp[i] = ((float)(i * 1.0D / width));
/*     */       }
/* 247 */       for (int y = 0; y < height; y++) {
/* 248 */         int offset = y * width;
/* 249 */         for (int x = 0; x < width; x++)
/* 250 */           pixels[(offset++)] = ramp[x];
/*     */       }
/*     */     case 3:
/*     */     }
/* 254 */     ImageProcessor ip = new FloatProcessor(width, height, pixels, null);
/* 255 */     if (fill == 4)
/* 256 */       ip.invertLut();
/* 257 */     ImagePlus imp = createImagePlus();
/* 258 */     imp.setProcessor(title, ip);
/* 259 */     if (slices > 1) {
/* 260 */       boolean ok = createStack(imp, ip, slices, 2, options);
/* 261 */       if (!ok) imp = null;
/*     */     }
/* 263 */     imp.getProcessor().setMinAndMax(0.0D, 1.0D);
/* 264 */     return imp;
/*     */   }
/*     */ 
/*     */   private static int getSize(int width, int height) {
/* 268 */     long size = width * height;
/* 269 */     if (size > 2147483647L) {
/* 270 */       IJ.error("Image is too large. ImageJ does not support\nsingle images larger than 2 gigapixels.");
/* 271 */       return -1;
/*     */     }
/* 273 */     return (int)size;
/*     */   }
/*     */ 
/*     */   public static void open(String title, int width, int height, int nSlices, int type, int options) {
/* 277 */     int bitDepth = 8;
/* 278 */     if (type == 1) bitDepth = 16;
/* 279 */     else if (type == 2) bitDepth = 32;
/* 280 */     else if (type == 3) bitDepth = 24;
/* 281 */     long startTime = System.currentTimeMillis();
/* 282 */     ImagePlus imp = createImage(title, width, height, nSlices, bitDepth, options);
/* 283 */     if (imp != null) {
/* 284 */       ij.WindowManager.checkForDuplicateName = true;
/* 285 */       imp.show();
/* 286 */       IJ.showStatus(IJ.d2s((System.currentTimeMillis() - startTime) / 1000.0D, 2) + " seconds");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static ImagePlus createImage(String title, int width, int height, int nSlices, int bitDepth, int options) {
/* 291 */     ImagePlus imp = null;
/* 292 */     switch (bitDepth) { case 8:
/* 293 */       imp = createByteImage(title, width, height, nSlices, options); break;
/*     */     case 16:
/* 294 */       imp = createShortImage(title, width, height, nSlices, options); break;
/*     */     case 32:
/* 295 */       imp = createFloatImage(title, width, height, nSlices, options); break;
/*     */     case 24:
/* 296 */       imp = createRGBImage(title, width, height, nSlices, options); break;
/*     */     default:
/* 297 */       throw new IllegalArgumentException("Invalid bitDepth: " + bitDepth);
/*     */     }
/* 299 */     return imp;
/*     */   }
/*     */ 
/*     */   boolean showDialog() {
/* 303 */     if ((type < 0) || (type > 3))
/* 304 */       type = 0;
/* 305 */     if ((fillWith < 0) || (fillWith > 2))
/* 306 */       fillWith = 0;
/* 307 */     GenericDialog gd = new GenericDialog("New Image...", IJ.getInstance());
/* 308 */     gd.addStringField("Name:", name, 12);
/* 309 */     gd.addChoice("Type:", types, types[type]);
/* 310 */     gd.addChoice("Fill With:", fill, fill[fillWith]);
/* 311 */     gd.addNumericField("Width:", width, 0, 5, "pixels");
/* 312 */     gd.addNumericField("Height:", height, 0, 5, "pixels");
/* 313 */     gd.addNumericField("Slices:", slices, 0, 5, "");
/* 314 */     gd.showDialog();
/* 315 */     if (gd.wasCanceled())
/* 316 */       return false;
/* 317 */     name = gd.getNextString();
/* 318 */     String s = gd.getNextChoice();
/* 319 */     if (s.startsWith("8"))
/* 320 */       type = 0;
/* 321 */     else if (s.startsWith("16"))
/* 322 */       type = 1;
/* 323 */     else if ((s.endsWith("RGB")) || (s.endsWith("rgb")))
/* 324 */       type = 3;
/*     */     else
/* 326 */       type = 2;
/* 327 */     fillWith = gd.getNextChoiceIndex();
/* 328 */     width = (int)gd.getNextNumber();
/* 329 */     height = (int)gd.getNextNumber();
/* 330 */     slices = (int)gd.getNextNumber();
/* 331 */     if (slices < 1) slices = 1;
/* 332 */     if ((width < 1) || (height < 1)) {
/* 333 */       IJ.error("New Image", "Width and height must be >0");
/* 334 */       return false;
/*     */     }
/* 336 */     return true;
/*     */   }
/*     */ 
/*     */   void openImage() {
/* 340 */     if (!showDialog())
/* 341 */       return; try {
/* 342 */       open(name, width, height, slices, type, fillWith); } catch (OutOfMemoryError e) {
/* 343 */       IJ.outOfMemory("New Image...");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void savePreferences(Properties prefs) {
/* 348 */     prefs.put("new.type", Integer.toString(type));
/* 349 */     prefs.put("new.fill", Integer.toString(fillWith));
/* 350 */     prefs.put("new.width", Integer.toString(width));
/* 351 */     prefs.put("new.height", Integer.toString(height));
/* 352 */     prefs.put("new.slices", Integer.toString(slices));
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.NewImage
 * JD-Core Version:    0.6.2
 */