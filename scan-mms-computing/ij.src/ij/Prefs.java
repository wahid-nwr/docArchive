/*     */ package ij;
/*     */ 
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.NewImage;
/*     */ import ij.gui.PlotWindow;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.io.FileSaver;
/*     */ import ij.io.ImportDialog;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.plugin.Animator;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ import ij.plugin.filter.Filters;
/*     */ import ij.plugin.filter.ParticleAnalyzer;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatBlitter;
/*     */ import ij.text.TextWindow;
/*     */ import ij.util.Tools;
/*     */ import java.applet.Applet;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class Prefs
/*     */ {
/*     */   public static final String PROPS_NAME = "IJ_Props.txt";
/*     */   public static final String PREFS_NAME = "IJ_Prefs.txt";
/*     */   public static final String DIR_IMAGE = "dir.image";
/*     */   public static final String FCOLOR = "fcolor";
/*     */   public static final String BCOLOR = "bcolor";
/*     */   public static final String ROICOLOR = "roicolor";
/*     */   public static final String SHOW_ALL_COLOR = "showcolor";
/*     */   public static final String JPEG = "jpeg";
/*     */   public static final String FPS = "fps";
/*     */   public static final String DIV_BY_ZERO_VALUE = "div-by-zero";
/*     */   public static final String NOISE_SD = "noise.sd";
/*     */   public static final String MENU_SIZE = "menu.size";
/*     */   public static final String THREADS = "threads";
/*     */   public static final String KEY_PREFIX = ".";
/*     */   private static final int USE_POINTER = 1;
/*     */   private static final int ANTIALIASING = 2;
/*     */   private static final int INTERPOLATE = 4;
/*     */   private static final int ONE_HUNDRED_PERCENT = 8;
/*     */   private static final int BLACK_BACKGROUND = 16;
/*     */   private static final int JFILE_CHOOSER = 32;
/*     */   private static final int UNUSED = 64;
/*     */   private static final int BLACK_CANVAS = 128;
/*     */   private static final int WEIGHTED = 256;
/*     */   private static final int AUTO_MEASURE = 512;
/*     */   private static final int REQUIRE_CONTROL = 1024;
/*     */   private static final int USE_INVERTING_LUT = 2048;
/*     */   private static final int ANTIALIASED_TOOLS = 4096;
/*     */   private static final int INTEL_BYTE_ORDER = 8192;
/*     */   private static final int DOUBLE_BUFFER = 16384;
/*     */   private static final int NO_POINT_LABELS = 32768;
/*     */   private static final int NO_BORDER = 65536;
/*     */   private static final int SHOW_ALL_SLICE_ONLY = 131072;
/*     */   private static final int COPY_HEADERS = 262144;
/*     */   private static final int NO_ROW_NUMBERS = 524288;
/*     */   private static final int MOVE_TO_MISC = 1048576;
/*     */   private static final int ADD_TO_MANAGER = 2097152;
/*     */   private static final int RUN_SOCKET_LISTENER = 4194304;
/*     */   private static final int MULTI_POINT_MODE = 8388608;
/*     */   private static final int ROTATE_YZ = 16777216;
/*     */   private static final int FLIP_XZ = 33554432;
/*     */   private static final int DONT_SAVE_HEADERS = 67108864;
/*     */   private static final int DONT_SAVE_ROW_NUMBERS = 134217728;
/*     */   private static final int NO_CLICK_TO_GC = 268435456;
/*     */   private static final int AVOID_RESLICE_INTERPOLATION = 536870912;
/*     */   private static final int KEEP_UNDO_BUFFERS = 1073741824;
/*     */   public static final String OPTIONS = "prefs.options";
/*     */   private static final int USE_SYSTEM_PROXIES = 1;
/*     */   private static final int USE_FILE_CHOOSER = 2;
/*     */   public static final String OPTIONS2 = "prefs.options2";
/*     */   public static final String vistaHint = "\n \nOn Windows Vista, ImageJ must be installed in a directory that\nthe user can write to, such as \"Desktop\" or \"Documents\"";
/*  60 */   public static String separator = System.getProperty("file.separator");
/*     */   public static boolean usePointerCursor;
/*     */   public static boolean antialiasedText;
/*     */   public static boolean interpolateScaledImages;
/*     */   public static boolean open100Percent;
/*     */   public static boolean blackBackground;
/*     */   public static boolean useJFileChooser;
/*     */   public static boolean weightedColor;
/*     */   public static boolean blackCanvas;
/*     */   public static boolean pointAutoMeasure;
/*     */   public static boolean pointAutoNextSlice;
/*     */   public static boolean requireControlKey;
/*     */   public static boolean useInvertingLut;
/*  86 */   public static boolean antialiasedTools = true;
/*     */   public static boolean intelByteOrder;
/*  90 */   public static boolean doubleBuffer = true;
/*     */   public static boolean noPointLabels;
/*     */   public static boolean disableUndo;
/*     */   public static boolean noBorder;
/*     */   public static boolean showAllSliceOnly;
/*     */   public static boolean copyColumnHeaders;
/*     */   public static boolean noRowNumbers;
/*     */   public static boolean moveToMisc;
/*     */   public static boolean pointAddToManager;
/*     */   public static boolean padEdges;
/*     */   public static boolean runSocketListener;
/*     */   public static boolean multiPointMode;
/*     */   public static boolean openDicomsAsFloat;
/*     */   public static boolean verticalProfile;
/*     */   public static boolean rotateYZ;
/*     */   public static boolean flipXZ;
/*     */   public static boolean dontSaveHeaders;
/*     */   public static boolean dontSaveRowNumbers;
/*     */   public static boolean noClickToGC;
/*     */   public static boolean reflexAngle;
/*     */   public static boolean avoidResliceInterpolation;
/*     */   public static boolean keepUndoBuffers;
/*     */   public static boolean useNamesAsLabels;
/*     */   public static boolean useSystemProxies;
/*     */   public static boolean useFileChooser;
/* 141 */   static Properties ijPrefs = new Properties();
/* 142 */   static Properties props = new Properties(ijPrefs);
/*     */   static String prefsDir;
/*     */   static String imagesURL;
/*     */   static String homeDir;
/*     */   static int threads;
/* 147 */   static int transparentIndex = -1;
/*     */   static boolean commandLineMacro;
/*     */ 
/*     */   public static String load(Object ij, Applet applet)
/*     */   {
/* 154 */     InputStream f = ij.getClass().getResourceAsStream("/IJ_Props.txt");
/* 155 */     if (applet != null)
/* 156 */       return loadAppletProps(f, applet);
/* 157 */     if (homeDir == null)
/* 158 */       homeDir = System.getProperty("user.dir");
/* 159 */     String userHome = System.getProperty("user.home");
/* 160 */     if (IJ.isWindows()) {
/* 161 */       prefsDir = homeDir;
/* 162 */       if (prefsDir.endsWith("Desktop"))
/* 163 */         prefsDir = userHome;
/*     */     } else {
/* 165 */       prefsDir = userHome;
/* 166 */       if (IJ.isMacOSX())
/* 167 */         prefsDir += "/Library/Preferences";
/*     */       else
/* 169 */         prefsDir += "/.imagej";
/*     */     }
/* 171 */     if (f == null) try {
/* 172 */         f = new FileInputStream(homeDir + "/" + "IJ_Props.txt"); } catch (FileNotFoundException e) {
/* 173 */         f = null;
/*     */       }
/* 175 */     if (f == null)
/* 176 */       return "IJ_Props.txt not found in ij.jar or in " + homeDir;
/* 177 */     f = new BufferedInputStream(f);
/*     */     try { props.load(f); f.close(); } catch (IOException e) {
/* 179 */       return "Error loading IJ_Props.txt";
/* 180 */     }imagesURL = props.getProperty("images.location");
/* 181 */     loadPreferences();
/* 182 */     loadOptions();
/* 183 */     return null;
/*     */   }
/*     */ 
/*     */   static String loadAppletProps(InputStream f, Applet applet)
/*     */   {
/* 199 */     if (f == null)
/* 200 */       return "IJ_Props.txt not found in ij.jar";
/*     */     try {
/* 202 */       props.load(f);
/* 203 */       f.close();
/*     */     } catch (IOException e) {
/* 205 */       return "Error loading IJ_Props.txt";
/*     */     }try {
/* 207 */       URL url = new URL(applet.getDocumentBase(), "images/");
/* 208 */       imagesURL = url.toString();
/*     */     } catch (Exception e) {
/*     */     }
/* 211 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getImagesURL()
/*     */   {
/* 216 */     return imagesURL;
/*     */   }
/*     */ 
/*     */   public static void setImagesURL(String url)
/*     */   {
/* 221 */     imagesURL = url;
/*     */   }
/*     */ 
/*     */   public static String getHomeDir()
/*     */   {
/* 226 */     return homeDir;
/*     */   }
/*     */ 
/*     */   public static String getPrefsDir()
/*     */   {
/* 232 */     return prefsDir;
/*     */   }
/*     */ 
/*     */   static void setHomeDir(String path)
/*     */   {
/* 237 */     if (path.endsWith(File.separator))
/* 238 */       path = path.substring(0, path.length() - 1);
/* 239 */     homeDir = path;
/*     */   }
/*     */ 
/*     */   public static String getDefaultDirectory()
/*     */   {
/* 244 */     if (commandLineMacro) {
/* 245 */       return null;
/*     */     }
/* 247 */     return getString("dir.image");
/*     */   }
/*     */ 
/*     */   public static String getString(String key)
/*     */   {
/* 252 */     return props.getProperty(key);
/*     */   }
/*     */ 
/*     */   public static String getString(String key, String defaultString)
/*     */   {
/* 257 */     if (props == null)
/* 258 */       return defaultString;
/* 259 */     String s = props.getProperty(key);
/* 260 */     if (s == null) {
/* 261 */       return defaultString;
/*     */     }
/* 263 */     return s;
/*     */   }
/*     */ 
/*     */   public static boolean getBoolean(String key, boolean defaultValue)
/*     */   {
/* 268 */     if (props == null) return defaultValue;
/* 269 */     String s = props.getProperty(key);
/* 270 */     if (s == null) {
/* 271 */       return defaultValue;
/*     */     }
/* 273 */     return s.equals("true");
/*     */   }
/*     */ 
/*     */   public static int getInt(String key, int defaultValue)
/*     */   {
/* 278 */     if (props == null)
/* 279 */       return defaultValue;
/* 280 */     String s = props.getProperty(key);
/* 281 */     if (s != null)
/*     */       try {
/* 283 */         return Integer.decode(s).intValue(); } catch (NumberFormatException e) {
/* 284 */         IJ.write("" + e);
/*     */       }
/* 286 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static double getDouble(String key, double defaultValue)
/*     */   {
/* 291 */     if (props == null)
/* 292 */       return defaultValue;
/* 293 */     String s = props.getProperty(key);
/* 294 */     Double d = null;
/* 295 */     if (s != null) {
/*     */       try { d = new Double(s); } catch (NumberFormatException e) {
/* 297 */         d = null;
/* 298 */       }if (d != null)
/* 299 */         return d.doubleValue();
/*     */     }
/* 301 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static Color getColor(String key, Color defaultColor)
/*     */   {
/* 306 */     int i = getInt(key, 2730);
/* 307 */     if (i == 2730)
/* 308 */       return defaultColor;
/* 309 */     return new Color(i >> 16 & 0xFF, i >> 8 & 0xFF, i & 0xFF);
/*     */   }
/*     */ 
/*     */   public static String getFileSeparator()
/*     */   {
/* 314 */     return separator;
/*     */   }
/*     */ 
/*     */   static void loadPreferences()
/*     */   {
/* 319 */     String path = prefsDir + separator + "IJ_Prefs.txt";
/* 320 */     boolean ok = loadPrefs(path);
/* 321 */     if ((!ok) && (!IJ.isWindows())) {
/* 322 */       path = System.getProperty("user.home") + separator + "IJ_Prefs.txt";
/* 323 */       ok = loadPrefs(path);
/* 324 */       if (ok) new File(path).delete(); 
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean loadPrefs(String path)
/*     */   {
/*     */     try
/*     */     {
/* 331 */       InputStream is = new BufferedInputStream(new FileInputStream(path));
/* 332 */       ijPrefs.load(is);
/* 333 */       is.close();
/* 334 */       return true; } catch (Exception e) {
/*     */     }
/* 336 */     return false;
/*     */   }
/*     */ 
/*     */   public static void savePreferences()
/*     */   {
/*     */     try
/*     */     {
/* 343 */       Properties prefs = new Properties();
/* 344 */       String dir = OpenDialog.getDefaultDirectory();
/* 345 */       if (dir != null)
/* 346 */         prefs.put("dir.image", dir);
/* 347 */       prefs.put("roicolor", Tools.c2hex(Roi.getColor()));
/* 348 */       prefs.put("showcolor", Tools.c2hex(ImageCanvas.getShowAllColor()));
/* 349 */       prefs.put("fcolor", Tools.c2hex(Toolbar.getForegroundColor()));
/* 350 */       prefs.put("bcolor", Tools.c2hex(Toolbar.getBackgroundColor()));
/* 351 */       prefs.put("jpeg", Integer.toString(FileSaver.getJpegQuality()));
/* 352 */       prefs.put("fps", Double.toString(Animator.getFrameRate()));
/* 353 */       prefs.put("div-by-zero", Double.toString(FloatBlitter.divideByZeroValue));
/* 354 */       prefs.put("noise.sd", Double.toString(Filters.getSD()));
/* 355 */       if (threads > 1) prefs.put("threads", Integer.toString(threads));
/* 356 */       if (IJ.isMacOSX()) useJFileChooser = false;
/* 357 */       saveOptions(prefs);
/* 358 */       savePluginPrefs(prefs);
/* 359 */       IJ.getInstance().savePreferences(prefs);
/* 360 */       Menus.savePreferences(prefs);
/* 361 */       ParticleAnalyzer.savePreferences(prefs);
/* 362 */       Analyzer.savePreferences(prefs);
/* 363 */       ImportDialog.savePreferences(prefs);
/* 364 */       PlotWindow.savePreferences(prefs);
/* 365 */       NewImage.savePreferences(prefs);
/* 366 */       String path = prefsDir + separator + "IJ_Prefs.txt";
/* 367 */       if (prefsDir.endsWith(".imagej")) {
/* 368 */         File f = new File(prefsDir);
/* 369 */         if (!f.exists()) f.mkdir();
/*     */       }
/* 371 */       savePrefs(prefs, path);
/*     */     } catch (Throwable t) {
/* 373 */       String msg = t.getMessage();
/* 374 */       if (msg == null) msg = "" + t;
/* 375 */       int delay = 4000;
/* 376 */       if (IJ.isVista()) {
/* 377 */         msg = msg + "\n \nOn Windows Vista, ImageJ must be installed in a directory that\nthe user can write to, such as \"Desktop\" or \"Documents\"";
/* 378 */         delay = 8000;
/*     */       }
/*     */       try {
/* 381 */         new TextWindow("Error Saving Preferences", msg, 500, 200);
/* 382 */         IJ.wait(delay); } catch (Throwable t2) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void loadOptions() {
/* 388 */     int defaultOptions = 536870914 + (!IJ.isMacOSX() ? 4194304 : 0);
/*     */ 
/* 390 */     int options = getInt("prefs.options", defaultOptions);
/* 391 */     usePointerCursor = (options & 0x1) != 0;
/*     */ 
/* 393 */     antialiasedText = false;
/* 394 */     interpolateScaledImages = (options & 0x4) != 0;
/* 395 */     open100Percent = (options & 0x8) != 0;
/* 396 */     open100Percent = (options & 0x8) != 0;
/* 397 */     blackBackground = (options & 0x10) != 0;
/* 398 */     useJFileChooser = (options & 0x20) != 0;
/* 399 */     weightedColor = (options & 0x100) != 0;
/* 400 */     if (weightedColor)
/* 401 */       ColorProcessor.setWeightingFactors(0.299D, 0.587D, 0.114D);
/* 402 */     blackCanvas = (options & 0x80) != 0;
/* 403 */     pointAutoMeasure = (options & 0x200) != 0;
/* 404 */     requireControlKey = (options & 0x400) != 0;
/* 405 */     useInvertingLut = (options & 0x800) != 0;
/* 406 */     antialiasedTools = (options & 0x1000) != 0;
/* 407 */     intelByteOrder = (options & 0x2000) != 0;
/*     */ 
/* 410 */     noBorder = (options & 0x10000) != 0;
/* 411 */     showAllSliceOnly = (options & 0x20000) != 0;
/* 412 */     copyColumnHeaders = (options & 0x40000) != 0;
/* 413 */     noRowNumbers = (options & 0x80000) != 0;
/* 414 */     moveToMisc = (options & 0x100000) != 0;
/* 415 */     pointAddToManager = (options & 0x200000) != 0;
/* 416 */     runSocketListener = (options & 0x400000) != 0;
/* 417 */     multiPointMode = (options & 0x800000) != 0;
/* 418 */     rotateYZ = (options & 0x1000000) != 0;
/* 419 */     flipXZ = (options & 0x2000000) != 0;
/* 420 */     dontSaveHeaders = (options & 0x4000000) != 0;
/* 421 */     dontSaveRowNumbers = (options & 0x8000000) != 0;
/* 422 */     noClickToGC = (options & 0x10000000) != 0;
/* 423 */     avoidResliceInterpolation = (options & 0x20000000) != 0;
/* 424 */     keepUndoBuffers = (options & 0x40000000) != 0;
/*     */ 
/* 426 */     defaultOptions = !IJ.isMacOSX() ? 2 : 0;
/* 427 */     int options2 = getInt("prefs.options2", defaultOptions);
/* 428 */     useSystemProxies = (options2 & 0x1) != 0;
/* 429 */     useFileChooser = (options2 & 0x2) != 0;
/*     */   }
/*     */ 
/*     */   static void saveOptions(Properties prefs) {
/* 433 */     int options = (usePointerCursor ? 1 : 0) + (antialiasedText ? 2 : 0) + (interpolateScaledImages ? 4 : 0) + (open100Percent ? 8 : 0) + (blackBackground ? 16 : 0) + (useJFileChooser ? 32 : 0) + (blackCanvas ? 128 : 0) + (weightedColor ? 256 : 0) + (pointAutoMeasure ? 512 : 0) + (requireControlKey ? 1024 : 0) + (useInvertingLut ? 2048 : 0) + (antialiasedTools ? 4096 : 0) + (intelByteOrder ? 8192 : 0) + (doubleBuffer ? 16384 : 0) + (noPointLabels ? 32768 : 0) + (noBorder ? 65536 : 0) + (showAllSliceOnly ? 131072 : 0) + (copyColumnHeaders ? 262144 : 0) + (noRowNumbers ? 524288 : 0) + (moveToMisc ? 1048576 : 0) + (pointAddToManager ? 2097152 : 0) + (runSocketListener ? 4194304 : 0) + (multiPointMode ? 8388608 : 0) + (rotateYZ ? 16777216 : 0) + (flipXZ ? 33554432 : 0) + (dontSaveHeaders ? 67108864 : 0) + (dontSaveRowNumbers ? 134217728 : 0) + (noClickToGC ? 268435456 : 0) + (avoidResliceInterpolation ? 536870912 : 0) + (keepUndoBuffers ? 1073741824 : 0);
/*     */ 
/* 449 */     prefs.put("prefs.options", Integer.toString(options));
/*     */ 
/* 451 */     int options2 = (useSystemProxies ? 1 : 0) + (useFileChooser ? 2 : 0);
/*     */ 
/* 453 */     prefs.put("prefs.options2", Integer.toString(options2));
/*     */   }
/*     */ 
/*     */   public static void set(String key, String text)
/*     */   {
/* 460 */     if (key.indexOf('.') < 1)
/* 461 */       throw new IllegalArgumentException("Key must have a prefix");
/* 462 */     ijPrefs.put("." + key, text);
/*     */   }
/*     */ 
/*     */   public static void set(String key, int value)
/*     */   {
/* 469 */     set(key, Integer.toString(value));
/*     */   }
/*     */ 
/*     */   public static void set(String key, double value)
/*     */   {
/* 476 */     set(key, "" + value);
/*     */   }
/*     */ 
/*     */   public static void set(String key, boolean value)
/*     */   {
/* 483 */     set(key, "" + value);
/*     */   }
/*     */ 
/*     */   public static String get(String key, String defaultValue)
/*     */   {
/* 490 */     String value = ijPrefs.getProperty("." + key);
/* 491 */     if (value == null) {
/* 492 */       return defaultValue;
/*     */     }
/* 494 */     return value;
/*     */   }
/*     */ 
/*     */   public static double get(String key, double defaultValue)
/*     */   {
/* 501 */     String s = ijPrefs.getProperty("." + key);
/* 502 */     Double d = null;
/* 503 */     if (s != null) {
/*     */       try { d = new Double(s); } catch (NumberFormatException e) {
/* 505 */         d = null;
/* 506 */       }if (d != null)
/* 507 */         return d.doubleValue();
/*     */     }
/* 509 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static boolean get(String key, boolean defaultValue)
/*     */   {
/* 516 */     String value = ijPrefs.getProperty("." + key);
/* 517 */     if (value == null) {
/* 518 */       return defaultValue;
/*     */     }
/* 520 */     return value.equals("true");
/*     */   }
/*     */ 
/*     */   public static void saveLocation(String key, Point loc)
/*     */   {
/* 526 */     set(key, loc.x + "," + loc.y);
/*     */   }
/*     */ 
/*     */   public static Point getLocation(String key)
/*     */   {
/* 533 */     String value = ijPrefs.getProperty("." + key);
/* 534 */     if (value == null) return null;
/* 535 */     int index = value.indexOf(",");
/* 536 */     if (index == -1) return null;
/* 537 */     double xloc = Tools.parseDouble(value.substring(0, index));
/* 538 */     if ((Double.isNaN(xloc)) || (index == value.length() - 1)) return null;
/* 539 */     double yloc = Tools.parseDouble(value.substring(index + 1));
/* 540 */     if (Double.isNaN(yloc)) return null;
/* 541 */     Point p = new Point((int)xloc, (int)yloc);
/* 542 */     Dimension screen = IJ.getScreenSize();
/* 543 */     if ((p.x > screen.width - 100) || (p.y > screen.height - 40)) {
/* 544 */       return null;
/*     */     }
/* 546 */     return p;
/*     */   }
/*     */ 
/*     */   static void savePluginPrefs(Properties prefs)
/*     */   {
/* 551 */     Enumeration e = ijPrefs.keys();
/* 552 */     while (e.hasMoreElements()) {
/* 553 */       String key = (String)e.nextElement();
/* 554 */       if (key.indexOf(".") == 0)
/* 555 */         prefs.put(key, ijPrefs.getProperty(key));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void savePrefs(Properties prefs, String path) throws IOException {
/* 560 */     FileOutputStream fos = new FileOutputStream(path);
/* 561 */     BufferedOutputStream bos = new BufferedOutputStream(fos);
/* 562 */     prefs.store(bos, "ImageJ 1.45s Preferences");
/* 563 */     bos.close();
/*     */   }
/*     */ 
/*     */   public static int getThreads()
/*     */   {
/* 568 */     if (threads == 0) {
/* 569 */       threads = getInt("threads", 0);
/* 570 */       int processors = Runtime.getRuntime().availableProcessors();
/* 571 */       if ((threads < 1) || (threads > processors)) threads = processors;
/*     */     }
/* 573 */     return threads;
/*     */   }
/*     */ 
/*     */   public static void setThreads(int n)
/*     */   {
/* 578 */     if (n < 1) n = 1;
/* 579 */     if (n > 32) n = 32;
/* 580 */     threads = n;
/*     */   }
/*     */ 
/*     */   public static void setTransparentIndex(int index)
/*     */   {
/* 585 */     if ((index < -1) || (index > 255)) index = -1;
/* 586 */     transparentIndex = index;
/*     */   }
/*     */ 
/*     */   public static int getTransparentIndex()
/*     */   {
/* 591 */     return transparentIndex;
/*     */   }
/*     */ 
/*     */   public static Properties getControlPanelProperties() {
/* 595 */     return ijPrefs;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.Prefs
 * JD-Core Version:    0.6.2
 */