/*      */ package ij;
/*      */ 
/*      */ import ij.gui.GenericDialog;
/*      */ import ij.gui.HTMLDialog;
/*      */ import ij.gui.ImageCanvas;
/*      */ import ij.gui.ImageWindow;
/*      */ import ij.gui.Line;
/*      */ import ij.gui.MessageDialog;
/*      */ import ij.gui.NewImage;
/*      */ import ij.gui.OvalRoi;
/*      */ import ij.gui.PointRoi;
/*      */ import ij.gui.PolygonRoi;
/*      */ import ij.gui.ProgressBar;
/*      */ import ij.gui.Roi;
/*      */ import ij.gui.Toolbar;
/*      */ import ij.gui.Wand;
/*      */ import ij.gui.YesNoCancelDialog;
/*      */ import ij.io.DirectoryChooser;
/*      */ import ij.io.FileInfo;
/*      */ import ij.io.OpenDialog;
/*      */ import ij.io.Opener;
/*      */ import ij.io.PluginClassLoader;
/*      */ import ij.io.SaveDialog;
/*      */ import ij.macro.Interpreter;
/*      */ import ij.measure.Calibration;
/*      */ import ij.measure.ResultsTable;
/*      */ import ij.plugin.Macro_Runner;
/*      */ import ij.plugin.Memory;
/*      */ import ij.plugin.PlugIn;
/*      */ import ij.plugin.filter.Analyzer;
/*      */ import ij.plugin.filter.PlugInFilter;
/*      */ import ij.plugin.filter.PlugInFilterRunner;
/*      */ import ij.plugin.frame.Recorder;
/*      */ import ij.process.AutoThresholder;
/*      */ import ij.process.ColorProcessor;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.process.ImageStatistics;
/*      */ import ij.process.StackStatistics;
/*      */ import ij.text.TextPanel;
/*      */ import ij.text.TextWindow;
/*      */ import ij.util.Tools;
/*      */ import java.applet.Applet;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Frame;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GraphicsDevice;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.Polygon;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.InputEvent;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.CharArrayWriter;
/*      */ import java.io.File;
/*      */ import java.io.FileReader;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.DecimalFormatSymbols;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class IJ
/*      */ {
/*      */   public static final String URL = "http://imagej.nih.gov/ij";
/*      */   public static final int ALL_KEYS = -1;
/*      */   public static boolean debugMode;
/*      */   public static boolean hideProcessStackDialog;
/*      */   public static final char micronSymbol = 'µ';
/*      */   public static final char angstromSymbol = 'Å';
/*      */   public static final char degreeSymbol = '°';
/*      */   private static ImageJ ij;
/*      */   private static Applet applet;
/*      */   private static ProgressBar progressBar;
/*      */   private static TextPanel textPanel;
/*      */   private static String osname;
/*      */   private static String osarch;
/*      */   private static boolean isMac;
/*      */   private static boolean isWin;
/*      */   private static boolean isJava2;
/*      */   private static boolean isJava14;
/*      */   private static boolean isJava15;
/*      */   private static boolean isJava16;
/*      */   private static boolean isJava17;
/*      */   private static boolean isLinux;
/*      */   private static boolean isVista;
/*      */   private static boolean is64Bit;
/*      */   private static boolean controlDown;
/*      */   private static boolean altDown;
/*      */   private static boolean spaceDown;
/*      */   private static boolean shiftDown;
/*      */   private static boolean macroRunning;
/*      */   private static Thread previousThread;
/*      */   private static TextPanel logPanel;
/*   45 */   private static boolean checkForDuplicatePlugins = true;
/*      */   private static ClassLoader classLoader;
/*      */   private static boolean memMessageDisplayed;
/*      */   private static long maxMemory;
/*      */   private static boolean escapePressed;
/*      */   private static boolean redirectErrorMessages;
/*      */   private static boolean redirectErrorMessages2;
/*      */   private static boolean suppressPluginNotFoundError;
/*      */   private static Hashtable commandTable;
/*   53 */   private static Vector eventListeners = new Vector();
/*      */   public static final int CANCELED = -2147483648;
/*      */   private static DecimalFormat[] df;
/*      */   private static DecimalFormat[] sf;
/*      */   private static DecimalFormatSymbols dfs;
/*      */   static ExceptionHandler exceptionHandler;
/*      */ 
/*      */   static void init(ImageJ imagej, Applet theApplet)
/*      */   {
/*   72 */     ij = imagej;
/*   73 */     applet = theApplet;
/*   74 */     progressBar = ij.getProgressBar();
/*      */   }
/*      */ 
/*      */   static void cleanup() {
/*   78 */     ij = null; applet = null; progressBar = null; textPanel = null;
/*      */   }
/*      */ 
/*      */   public static ImageJ getInstance()
/*      */   {
/*   83 */     return ij;
/*      */   }
/*      */ 
/*      */   public static String runMacro(String macro)
/*      */   {
/*   91 */     return runMacro(macro, "");
/*      */   }
/*      */ 
/*      */   public static String runMacro(String macro, String arg)
/*      */   {
/*  101 */     Macro_Runner mr = new Macro_Runner();
/*  102 */     return mr.runMacro(macro, arg);
/*      */   }
/*      */ 
/*      */   public static String runMacroFile(String name, String arg)
/*      */   {
/*  114 */     if ((ij == null) && (Menus.getCommands() == null))
/*  115 */       init();
/*  116 */     Macro_Runner mr = new Macro_Runner();
/*  117 */     return mr.runMacroFile(name, arg);
/*      */   }
/*      */ 
/*      */   public static String runMacroFile(String name)
/*      */   {
/*  122 */     return runMacroFile(name, null);
/*      */   }
/*      */ 
/*      */   public static Object runPlugIn(ImagePlus imp, String className, String arg)
/*      */   {
/*  127 */     if (imp != null) {
/*  128 */       ImagePlus temp = WindowManager.getTempCurrentImage();
/*  129 */       WindowManager.setTempCurrentImage(imp);
/*  130 */       Object o = runPlugIn("", className, arg);
/*  131 */       WindowManager.setTempCurrentImage(temp);
/*  132 */       return o;
/*      */     }
/*  134 */     return runPlugIn(className, arg);
/*      */   }
/*      */ 
/*      */   public static Object runPlugIn(String className, String arg)
/*      */   {
/*  139 */     return runPlugIn("", className, arg);
/*      */   }
/*      */ 
/*      */   static Object runPlugIn(String commandName, String className, String arg)
/*      */   {
/*  144 */     if (debugMode)
/*  145 */       log("runPlugin: " + className + " " + arg);
/*  146 */     if (arg == null) arg = "";
/*      */ 
/*  149 */     if ((!className.startsWith("ij.")) && (applet == null))
/*  150 */       return runUserPlugIn(commandName, className, arg, false);
/*  151 */     Object thePlugIn = null;
/*      */     try {
/*  153 */       Class c = Class.forName(className);
/*  154 */       thePlugIn = c.newInstance();
/*  155 */       if ((thePlugIn instanceof PlugIn))
/*  156 */         ((PlugIn)thePlugIn).run(arg);
/*      */       else
/*  158 */         new PlugInFilterRunner(thePlugIn, commandName, arg);
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/*  161 */       if (getApplet() == null)
/*  162 */         log("Plugin or class not found: \"" + className + "\"\n(" + e + ")");
/*      */     } catch (InstantiationException e) {
/*  164 */       log("Unable to load plugin (ins)"); } catch (IllegalAccessException e) {
/*  165 */       log("Unable to load plugin, possibly \nbecause it is not public.");
/*  166 */     }redirectErrorMessages = false;
/*  167 */     return thePlugIn;
/*      */   }
/*      */ 
/*      */   static Object runUserPlugIn(String commandName, String className, String arg, boolean createNewLoader) {
/*  171 */     if (applet != null) return null;
/*  172 */     if (checkForDuplicatePlugins)
/*      */     {
/*  174 */       runPlugIn("ij.plugin.ClassChecker", "");
/*  175 */       checkForDuplicatePlugins = false;
/*      */     }
/*  177 */     if (createNewLoader) classLoader = null;
/*  178 */     ClassLoader loader = getClassLoader();
/*  179 */     Object thePlugIn = null;
/*      */     try {
/*  181 */       thePlugIn = loader.loadClass(className).newInstance();
/*  182 */       if ((thePlugIn instanceof PlugIn))
/*  183 */         ((PlugIn)thePlugIn).run(arg);
/*  184 */       else if ((thePlugIn instanceof PlugInFilter))
/*  185 */         new PlugInFilterRunner(thePlugIn, commandName, arg);
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/*  188 */       if ((className.indexOf('_') != -1) && (!suppressPluginNotFoundError))
/*  189 */         error("Plugin or class not found: \"" + className + "\"\n(" + e + ")");
/*      */     }
/*      */     catch (NoClassDefFoundError e) {
/*  192 */       int dotIndex = className.indexOf('.');
/*  193 */       if (dotIndex >= 0)
/*  194 */         return runUserPlugIn(commandName, className.substring(dotIndex + 1), arg, createNewLoader);
/*  195 */       if ((className.indexOf('_') != -1) && (!suppressPluginNotFoundError))
/*  196 */         error("Plugin or class not found: \"" + className + "\"\n(" + e + ")");
/*      */     } catch (InstantiationException e) {
/*  198 */       error("Unable to load plugin (ins)"); } catch (IllegalAccessException e) {
/*  199 */       error("Unable to load plugin, possibly \nbecause it is not public.");
/*  200 */     }if ((redirectErrorMessages) && (!"HandleExtraFileTypes".equals(className)))
/*  201 */       redirectErrorMessages = false;
/*  202 */     suppressPluginNotFoundError = false;
/*  203 */     return thePlugIn;
/*      */   }
/*      */ 
/*      */   static void wrongType(int capabilities, String cmd) {
/*  207 */     String s = "\"" + cmd + "\" requires an image of type:\n \n";
/*  208 */     if ((capabilities & 0x1) != 0) s = s + "    8-bit grayscale\n";
/*  209 */     if ((capabilities & 0x2) != 0) s = s + "    8-bit color\n";
/*  210 */     if ((capabilities & 0x4) != 0) s = s + "    16-bit grayscale\n";
/*  211 */     if ((capabilities & 0x8) != 0) s = s + "    32-bit (float) grayscale\n";
/*  212 */     if ((capabilities & 0x10) != 0) s = s + "    RGB color\n";
/*  213 */     error(s);
/*      */   }
/*      */ 
/*      */   public static void doCommand(String command)
/*      */   {
/*  218 */     if (ij != null)
/*  219 */       ij.doCommand(command);
/*      */   }
/*      */ 
/*      */   public static void run(String command)
/*      */   {
/*  227 */     run(command, null);
/*      */   }
/*      */ 
/*      */   public static void run(String command, String options)
/*      */   {
/*  235 */     if ((ij == null) && (Menus.getCommands() == null))
/*  236 */       init();
/*  237 */     Macro.abort = false;
/*  238 */     Macro.setOptions(options);
/*  239 */     Thread thread = Thread.currentThread();
/*  240 */     if ((previousThread == null) || (thread != previousThread)) {
/*  241 */       String name = thread.getName();
/*  242 */       if (!name.startsWith("Run$_"))
/*  243 */         thread.setName("Run$_" + name);
/*      */     }
/*  245 */     command = convert(command);
/*  246 */     previousThread = thread;
/*  247 */     macroRunning = true;
/*  248 */     Executer e = new Executer(command);
/*  249 */     e.run();
/*  250 */     macroRunning = false;
/*  251 */     Macro.setOptions(null);
/*  252 */     testAbort();
/*      */   }
/*      */ 
/*      */   private static String convert(String command)
/*      */   {
/*  259 */     if (commandTable == null) {
/*  260 */       commandTable = new Hashtable(23);
/*  261 */       commandTable.put("New...", "Image...");
/*  262 */       commandTable.put("Threshold", "Make Binary");
/*  263 */       commandTable.put("Display...", "Appearance...");
/*  264 */       commandTable.put("Start Animation", "Start Animation [\\]");
/*  265 */       commandTable.put("Convert Images to Stack", "Images to Stack");
/*  266 */       commandTable.put("Convert Stack to Images", "Stack to Images");
/*  267 */       commandTable.put("Convert Stack to RGB", "Stack to RGB");
/*  268 */       commandTable.put("Convert to Composite", "Make Composite");
/*  269 */       commandTable.put("New HyperStack...", "New Hyperstack...");
/*  270 */       commandTable.put("Stack to HyperStack...", "Stack to Hyperstack...");
/*  271 */       commandTable.put("HyperStack to Stack", "Hyperstack to Stack");
/*  272 */       commandTable.put("RGB Split", "Split Channels");
/*  273 */       commandTable.put("RGB Merge...", "Merge Channels...");
/*  274 */       commandTable.put("Channels...", "Channels Tool...");
/*  275 */       commandTable.put("New... ", "Table...");
/*  276 */       commandTable.put("Arbitrarily...", "Rotate... ");
/*  277 */       commandTable.put("Measurements...", "Results... ");
/*  278 */       commandTable.put("List Commands...", "Find Commands...");
/*  279 */       commandTable.put("Capture Screen ", "Capture Screen");
/*  280 */       commandTable.put("Add to Manager ", "Add to Manager");
/*  281 */       commandTable.put("In", "In [+]");
/*  282 */       commandTable.put("Out", "Out [-]");
/*      */     }
/*  284 */     String command2 = (String)commandTable.get(command);
/*  285 */     if (command2 != null) {
/*  286 */       return command2;
/*      */     }
/*  288 */     return command;
/*      */   }
/*      */ 
/*      */   public static void run(ImagePlus imp, String command, String options)
/*      */   {
/*  293 */     if (imp != null) {
/*  294 */       ImagePlus temp = WindowManager.getTempCurrentImage();
/*  295 */       WindowManager.setTempCurrentImage(imp);
/*  296 */       run(command, options);
/*  297 */       WindowManager.setTempCurrentImage(temp);
/*      */     } else {
/*  299 */       run(command, options);
/*      */     }
/*      */   }
/*      */ 
/*  303 */   static void init() { Menus m = new Menus(null, null);
/*  304 */     Prefs.load(m, null);
/*  305 */     m.addMenuBar(); }
/*      */ 
/*      */   private static void testAbort()
/*      */   {
/*  309 */     if (Macro.abort)
/*  310 */       abort();
/*      */   }
/*      */ 
/*      */   public static boolean macroRunning()
/*      */   {
/*  315 */     return macroRunning;
/*      */   }
/*      */ 
/*      */   public static boolean isMacro()
/*      */   {
/*  321 */     return (macroRunning) || (Interpreter.getInstance() != null);
/*      */   }
/*      */ 
/*      */   public static Applet getApplet()
/*      */   {
/*  326 */     return applet;
/*      */   }
/*      */ 
/*      */   public static void showStatus(String s)
/*      */   {
/*  331 */     if (ij != null) ij.showStatus(s);
/*  332 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  333 */     ImageCanvas ic = imp != null ? imp.getCanvas() : null;
/*  334 */     if (ic != null)
/*  335 */       ic.setShowCursorStatus(s.length() == 0);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static void write(String s)
/*      */   {
/*  345 */     if ((textPanel == null) && (ij != null))
/*  346 */       showResults();
/*  347 */     if (textPanel != null)
/*  348 */       textPanel.append(s);
/*      */     else
/*  350 */       System.out.println(s);
/*      */   }
/*      */ 
/*      */   private static void showResults() {
/*  354 */     TextWindow resultsWindow = new TextWindow("Results", "", 400, 250);
/*  355 */     textPanel = resultsWindow.getTextPanel();
/*  356 */     textPanel.setResultsTable(Analyzer.getResultsTable());
/*      */   }
/*      */ 
/*      */   public static synchronized void log(String s) {
/*  360 */     if (s == null) return;
/*  361 */     if ((logPanel == null) && (ij != null)) {
/*  362 */       TextWindow logWindow = new TextWindow("Log", "", 400, 250);
/*  363 */       logPanel = logWindow.getTextPanel();
/*  364 */       logPanel.setFont(new Font("SansSerif", 0, 16));
/*      */     }
/*  366 */     if (logPanel != null) {
/*  367 */       if (s.startsWith("\\"))
/*  368 */         handleLogCommand(s);
/*      */       else
/*  370 */         logPanel.append(s);
/*      */     }
/*  372 */     else System.out.println(s); 
/*      */   }
/*      */ 
/*      */   static void handleLogCommand(String s)
/*      */   {
/*  376 */     if (s.equals("\\Closed")) {
/*  377 */       logPanel = null;
/*  378 */     } else if (s.startsWith("\\Update:")) {
/*  379 */       int n = logPanel.getLineCount();
/*  380 */       String s2 = s.substring(8, s.length());
/*  381 */       if (n == 0)
/*  382 */         logPanel.append(s2);
/*      */       else
/*  384 */         logPanel.setLine(n - 1, s2);
/*  385 */     } else if (s.startsWith("\\Update")) {
/*  386 */       int cindex = s.indexOf(":");
/*  387 */       if (cindex == -1) {
/*  388 */         logPanel.append(s); return;
/*  389 */       }String nstr = s.substring(7, cindex);
/*  390 */       int line = (int)Tools.parseDouble(nstr, -1.0D);
/*  391 */       if ((line < 0) || (line > 25)) {
/*  392 */         logPanel.append(s); return;
/*  393 */       }int count = logPanel.getLineCount();
/*  394 */       while (line >= count) {
/*  395 */         log("");
/*  396 */         count++;
/*      */       }
/*  398 */       String s2 = s.substring(cindex + 1, s.length());
/*  399 */       logPanel.setLine(line, s2);
/*  400 */     } else if (s.equals("\\Clear")) {
/*  401 */       logPanel.clear();
/*  402 */     } else if (s.equals("\\Close")) {
/*  403 */       Frame f = WindowManager.getFrame("Log");
/*  404 */       if ((f != null) && ((f instanceof TextWindow)))
/*  405 */         ((TextWindow)f).close();
/*      */     } else {
/*  407 */       logPanel.append(s);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static synchronized String getLog() {
/*  412 */     if ((logPanel == null) || (ij == null)) {
/*  413 */       return null;
/*      */     }
/*  415 */     return logPanel.getText();
/*      */   }
/*      */ 
/*      */   public static void setColumnHeadings(String headings)
/*      */   {
/*  422 */     if ((textPanel == null) && (ij != null))
/*  423 */       showResults();
/*  424 */     if (textPanel != null)
/*  425 */       textPanel.setColumnHeadings(headings);
/*      */     else
/*  427 */       System.out.println(headings);
/*      */   }
/*      */ 
/*      */   public static boolean isResultsWindow()
/*      */   {
/*  432 */     return textPanel != null;
/*      */   }
/*      */ 
/*      */   public static void renameResults(String title)
/*      */   {
/*  437 */     Frame frame = WindowManager.getFrontWindow();
/*  438 */     if ((frame != null) && ((frame instanceof TextWindow))) {
/*  439 */       TextWindow tw = (TextWindow)frame;
/*  440 */       if (tw.getTextPanel().getResultsTable() == null) {
/*  441 */         error("Rename", "\"" + tw.getTitle() + "\" is not a results table");
/*  442 */         return;
/*      */       }
/*  444 */       tw.rename(title);
/*  445 */     } else if (isResultsWindow()) {
/*  446 */       TextPanel tp = getTextPanel();
/*  447 */       TextWindow tw = (TextWindow)tp.getParent();
/*  448 */       tw.rename(title);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void deleteRows(int row1, int row2)
/*      */   {
/*  455 */     int n = row2 - row1 + 1;
/*  456 */     ResultsTable rt = Analyzer.getResultsTable();
/*  457 */     for (int i = row1; i < row1 + n; i++)
/*  458 */       rt.deleteRow(row1);
/*  459 */     rt.show("Results");
/*      */   }
/*      */ 
/*      */   public static TextPanel getTextPanel()
/*      */   {
/*  466 */     if ((textPanel == null) && (ij != null))
/*  467 */       showResults();
/*  468 */     return textPanel;
/*      */   }
/*      */ 
/*      */   public static void setTextPanel(TextPanel tp)
/*      */   {
/*  473 */     textPanel = tp;
/*      */   }
/*      */ 
/*      */   public static void noImage()
/*      */   {
/*  478 */     error("No Image", "There are no images open.");
/*      */   }
/*      */ 
/*      */   public static void outOfMemory(String name)
/*      */   {
/*  483 */     Undo.reset();
/*  484 */     System.gc();
/*  485 */     String tot = Runtime.getRuntime().totalMemory() / 1048576L + "MB";
/*  486 */     if (!memMessageDisplayed)
/*  487 */       log(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
/*  488 */     log("<Out of memory>");
/*  489 */     if (!memMessageDisplayed) {
/*  490 */       log("<All available memory (" + tot + ") has been>");
/*  491 */       log("<used. Instructions for making more>");
/*  492 */       log("<available can be found in the \"Memory\" >");
/*  493 */       log("<sections of the installation notes at>");
/*  494 */       log("<http://imagej.nih.gov/ij/docs/install/>");
/*  495 */       log(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
/*  496 */       memMessageDisplayed = true;
/*      */     }
/*  498 */     Macro.abort();
/*      */   }
/*      */ 
/*      */   public static void showProgress(double progress)
/*      */   {
/*  506 */     if (progressBar != null) progressBar.show(progress, false);
/*      */   }
/*      */ 
/*      */   public static void showProgress(int currentIndex, int finalIndex)
/*      */   {
/*  515 */     if (progressBar != null) {
/*  516 */       progressBar.show(currentIndex, finalIndex);
/*  517 */       if (currentIndex == finalIndex)
/*  518 */         progressBar.setBatchMode(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void showMessage(String msg)
/*      */   {
/*  525 */     showMessage("Message", msg);
/*      */   }
/*      */ 
/*      */   public static void showMessage(String title, String msg)
/*      */   {
/*  531 */     if (ij != null) {
/*  532 */       if ((msg != null) && (msg.startsWith("<html>")))
/*  533 */         new HTMLDialog(title, msg);
/*      */       else
/*  535 */         new MessageDialog(ij, title, msg);
/*      */     }
/*  537 */     else System.out.println(msg);
/*      */   }
/*      */ 
/*      */   public static void error(String msg)
/*      */   {
/*  544 */     error(null, msg);
/*  545 */     if (Thread.currentThread().getName().endsWith("JavaScript")) {
/*  546 */       throw new RuntimeException("Macro canceled");
/*      */     }
/*  548 */     Macro.abort();
/*      */   }
/*      */ 
/*      */   public static void error(String title, String msg)
/*      */   {
/*  555 */     String title2 = title != null ? title : "ImageJ";
/*  556 */     boolean abortMacro = title != null;
/*  557 */     if ((redirectErrorMessages) || (redirectErrorMessages2)) {
/*  558 */       log(title2 + ": " + msg);
/*  559 */       if ((abortMacro) && ((title.equals("Opener")) || (title.equals("Open URL")) || (title.equals("DicomDecoder"))))
/*  560 */         abortMacro = false;
/*      */     } else {
/*  562 */       showMessage(title2, msg);
/*  563 */     }redirectErrorMessages = false;
/*  564 */     if (abortMacro) Macro.abort();
/*      */   }
/*      */ 
/*      */   public static boolean showMessageWithCancel(String title, String msg)
/*      */   {
/*  570 */     GenericDialog gd = new GenericDialog(title);
/*  571 */     gd.addMessage(msg);
/*  572 */     gd.showDialog();
/*  573 */     return !gd.wasCanceled();
/*      */   }
/*      */ 
/*      */   public static double getNumber(String prompt, double defaultValue)
/*      */   {
/*  582 */     GenericDialog gd = new GenericDialog("");
/*  583 */     int decimalPlaces = (int)defaultValue == defaultValue ? 0 : 2;
/*  584 */     gd.addNumericField(prompt, defaultValue, decimalPlaces);
/*  585 */     gd.showDialog();
/*  586 */     if (gd.wasCanceled())
/*  587 */       return -2147483648.0D;
/*  588 */     double v = gd.getNextNumber();
/*  589 */     if (gd.invalidNumber()) {
/*  590 */       return defaultValue;
/*      */     }
/*  592 */     return v;
/*      */   }
/*      */ 
/*      */   public static String getString(String prompt, String defaultString)
/*      */   {
/*  598 */     GenericDialog gd = new GenericDialog("");
/*  599 */     gd.addStringField(prompt, defaultString, 20);
/*  600 */     gd.showDialog();
/*  601 */     if (gd.wasCanceled())
/*  602 */       return "";
/*  603 */     return gd.getNextString();
/*      */   }
/*      */ 
/*      */   public static void wait(int msecs) {
/*      */     try {
/*  608 */       Thread.sleep(msecs);
/*      */     } catch (InterruptedException e) {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void beep() {
/*  614 */     Toolkit.getDefaultToolkit().beep();
/*      */   }
/*      */ 
/*      */   public static String freeMemory()
/*      */   {
/*  622 */     long inUse = currentMemory();
/*  623 */     String inUseStr = inUse / 1048576L + "MB";
/*  624 */     String maxStr = "";
/*  625 */     long max = maxMemory();
/*  626 */     if (max > 0L) {
/*  627 */       double percent = inUse * 100L / max;
/*  628 */       maxStr = " of " + max / 1048576L + "MB (" + (percent < 1.0D ? "<1" : d2s(percent, 0)) + "%)";
/*      */     }
/*  630 */     return inUseStr + maxStr;
/*      */   }
/*      */ 
/*      */   public static long currentMemory()
/*      */   {
/*  635 */     long freeMem = Runtime.getRuntime().freeMemory();
/*  636 */     long totMem = Runtime.getRuntime().totalMemory();
/*  637 */     return totMem - freeMem;
/*      */   }
/*      */ 
/*      */   public static long maxMemory()
/*      */   {
/*  643 */     if (maxMemory == 0L) {
/*  644 */       Memory mem = new Memory();
/*  645 */       maxMemory = mem.getMemorySetting();
/*  646 */       if (maxMemory == 0L) maxMemory = mem.maxMemory();
/*      */     }
/*  648 */     return maxMemory;
/*      */   }
/*      */ 
/*      */   public static void showTime(ImagePlus imp, long start, String str) {
/*  652 */     showTime(imp, start, str, 1);
/*      */   }
/*      */ 
/*      */   public static void showTime(ImagePlus imp, long start, String str, int nslices) {
/*  656 */     if (Interpreter.isBatchMode()) return;
/*  657 */     double seconds = (System.currentTimeMillis() - start) / 1000.0D;
/*  658 */     double pixels = imp.getWidth() * imp.getHeight();
/*  659 */     double rate = pixels * nslices / seconds;
/*      */     String str2;
/*      */     String str2;
/*  661 */     if (rate > 1000000000.0D) {
/*  662 */       str2 = "";
/*      */     }
/*      */     else
/*      */     {
/*      */       String str2;
/*  663 */       if (rate < 1000000.0D)
/*  664 */         str2 = ", " + d2s(rate, 0) + " pixels/second";
/*      */       else
/*  666 */         str2 = ", " + d2s(rate / 1000000.0D, 1) + " million pixels/second"; 
/*      */     }
/*  667 */     showStatus(str + seconds + " seconds" + str2);
/*      */   }
/*      */ 
/*      */   public static String time(ImagePlus imp, long startNanoTime)
/*      */   {
/*  672 */     double planes = imp.getStackSize();
/*  673 */     double seconds = (System.nanoTime() - startNanoTime) / 1000000000.0D;
/*  674 */     double mpixels = imp.getWidth() * imp.getHeight() * planes / 1000000.0D;
/*  675 */     String time = d2s(seconds, 1) + " seconds";
/*  676 */     return time + ", " + d2s(mpixels / seconds, 1) + " million pixels/second";
/*      */   }
/*      */ 
/*      */   public static String d2s(double n)
/*      */   {
/*  682 */     return d2s(n, 2);
/*      */   }
/*      */ 
/*      */   public static String d2s(double n, int decimalPlaces)
/*      */   {
/*  694 */     if ((Double.isNaN(n)) || (Double.isInfinite(n)))
/*  695 */       return "" + n;
/*  696 */     if (n == 3.402823466385289E+38D)
/*  697 */       return "3.4e38";
/*  698 */     double np = n;
/*  699 */     if (n < 0.0D) np = -n;
/*  700 */     if (df == null) {
/*  701 */       dfs = new DecimalFormatSymbols(Locale.US);
/*  702 */       df = new DecimalFormat[10];
/*  703 */       df[0] = new DecimalFormat("0", dfs);
/*  704 */       df[1] = new DecimalFormat("0.0", dfs);
/*  705 */       df[2] = new DecimalFormat("0.00", dfs);
/*  706 */       df[3] = new DecimalFormat("0.000", dfs);
/*  707 */       df[4] = new DecimalFormat("0.0000", dfs);
/*  708 */       df[5] = new DecimalFormat("0.00000", dfs);
/*  709 */       df[6] = new DecimalFormat("0.000000", dfs);
/*  710 */       df[7] = new DecimalFormat("0.0000000", dfs);
/*  711 */       df[8] = new DecimalFormat("0.00000000", dfs);
/*  712 */       df[9] = new DecimalFormat("0.000000000", dfs);
/*      */     }
/*  714 */     if (decimalPlaces < 0) {
/*  715 */       decimalPlaces = -decimalPlaces;
/*  716 */       if (decimalPlaces > 9) decimalPlaces = 9;
/*  717 */       if (sf == null) {
/*  718 */         sf = new DecimalFormat[10];
/*  719 */         sf[1] = new DecimalFormat("0.0E0", dfs);
/*  720 */         sf[2] = new DecimalFormat("0.00E0", dfs);
/*  721 */         sf[3] = new DecimalFormat("0.000E0", dfs);
/*  722 */         sf[4] = new DecimalFormat("0.0000E0", dfs);
/*  723 */         sf[5] = new DecimalFormat("0.00000E0", dfs);
/*  724 */         sf[6] = new DecimalFormat("0.000000E0", dfs);
/*  725 */         sf[7] = new DecimalFormat("0.0000000E0", dfs);
/*  726 */         sf[8] = new DecimalFormat("0.00000000E0", dfs);
/*  727 */         sf[9] = new DecimalFormat("0.000000000E0", dfs);
/*      */       }
/*  729 */       return sf[decimalPlaces].format(n);
/*      */     }
/*  731 */     if (decimalPlaces < 0) decimalPlaces = 0;
/*  732 */     if (decimalPlaces > 9) decimalPlaces = 9;
/*  733 */     return df[decimalPlaces].format(n);
/*      */   }
/*      */ 
/*      */   public static String pad(int n, int digits)
/*      */   {
/*  738 */     String str = "" + n;
/*  739 */     while (str.length() < digits)
/*  740 */       str = "0" + str;
/*  741 */     return str;
/*      */   }
/*      */ 
/*      */   public static void register(Class c)
/*      */   {
/*  748 */     if (ij != null) ij.register(c);
/*      */   }
/*      */ 
/*      */   public static boolean spaceBarDown()
/*      */   {
/*  753 */     return spaceDown;
/*      */   }
/*      */ 
/*      */   public static boolean controlKeyDown()
/*      */   {
/*  758 */     return controlDown;
/*      */   }
/*      */ 
/*      */   public static boolean altKeyDown()
/*      */   {
/*  763 */     return altDown;
/*      */   }
/*      */ 
/*      */   public static boolean shiftKeyDown()
/*      */   {
/*  768 */     return shiftDown;
/*      */   }
/*      */ 
/*      */   public static void setKeyDown(int key) {
/*  772 */     if (debugMode) log("setKeyDown: " + key);
/*  773 */     switch (key) {
/*      */     case 17:
/*  775 */       controlDown = true;
/*  776 */       break;
/*      */     case 157:
/*  778 */       if (isMacintosh()) controlDown = true; break;
/*      */     case 18:
/*  781 */       altDown = true;
/*  782 */       break;
/*      */     case 16:
/*  784 */       shiftDown = true;
/*  785 */       if (debugMode) beep(); break;
/*      */     case 32:
/*  788 */       spaceDown = true;
/*  789 */       ImageWindow win = WindowManager.getCurrentWindow();
/*  790 */       if (win != null) win.getCanvas().setCursor(-1, -1, -1, -1); break;
/*      */     case 27:
/*  794 */       escapePressed = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setKeyUp(int key)
/*      */   {
/*  801 */     if (debugMode) log("setKeyUp: " + key);
/*  802 */     switch (key) { case 17:
/*  803 */       controlDown = false; break;
/*      */     case 157:
/*  804 */       if (isMacintosh()) controlDown = false; break;
/*      */     case 18:
/*  805 */       altDown = false; break;
/*      */     case 16:
/*  806 */       shiftDown = false; if (debugMode) beep(); break;
/*      */     case 32:
/*  808 */       spaceDown = false;
/*  809 */       ImageWindow win = WindowManager.getCurrentWindow();
/*  810 */       if (win != null) win.getCanvas().setCursor(-1, -1, -1, -1); break;
/*      */     case -1:
/*  813 */       shiftDown = IJ.controlDown = IJ.altDown = IJ.spaceDown = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setInputEvent(InputEvent e)
/*      */   {
/*  819 */     altDown = e.isAltDown();
/*  820 */     shiftDown = e.isShiftDown();
/*      */   }
/*      */ 
/*      */   public static boolean isMacintosh()
/*      */   {
/*  825 */     return isMac;
/*      */   }
/*      */ 
/*      */   public static boolean isMacOSX()
/*      */   {
/*  830 */     return isMacintosh();
/*      */   }
/*      */ 
/*      */   public static boolean isWindows()
/*      */   {
/*  835 */     return isWin;
/*      */   }
/*      */ 
/*      */   public static boolean isJava2()
/*      */   {
/*  840 */     return isJava2;
/*      */   }
/*      */ 
/*      */   public static boolean isJava14()
/*      */   {
/*  845 */     return isJava14;
/*      */   }
/*      */ 
/*      */   public static boolean isJava15()
/*      */   {
/*  850 */     return isJava15;
/*      */   }
/*      */ 
/*      */   public static boolean isJava16()
/*      */   {
/*  855 */     return isJava16;
/*      */   }
/*      */ 
/*      */   public static boolean isJava17()
/*      */   {
/*  860 */     return isJava17;
/*      */   }
/*      */ 
/*      */   public static boolean isLinux()
/*      */   {
/*  865 */     return isLinux;
/*      */   }
/*      */ 
/*      */   public static boolean isVista()
/*      */   {
/*  870 */     return isVista;
/*      */   }
/*      */ 
/*      */   public static boolean is64Bit()
/*      */   {
/*  875 */     if (osarch == null)
/*  876 */       osarch = System.getProperty("os.arch");
/*  877 */     return (osarch != null) && (osarch.indexOf("64") != -1);
/*      */   }
/*      */ 
/*      */   public static boolean versionLessThan(String version)
/*      */   {
/*  883 */     boolean lessThan = "1.45s".compareTo(version) < 0;
/*  884 */     if (lessThan)
/*  885 */       error("This plugin or macro requires ImageJ " + version + " or later. Use\nHelp>Update ImageJ to upgrade to the latest version.");
/*  886 */     return lessThan;
/*      */   }
/*      */ 
/*      */   public static int setupDialog(ImagePlus imp, int flags)
/*      */   {
/*  895 */     if ((imp == null) || ((ij != null) && (ij.hotkey)))
/*  896 */       return flags;
/*  897 */     int stackSize = imp.getStackSize();
/*  898 */     if (stackSize > 1) {
/*  899 */       if ((imp.isComposite()) && (((CompositeImage)imp).getMode() == 1))
/*  900 */         return flags + 32;
/*  901 */       String macroOptions = Macro.getOptions();
/*  902 */       if (macroOptions != null) {
/*  903 */         if (macroOptions.indexOf("stack ") >= 0) {
/*  904 */           return flags + 32;
/*      */         }
/*  906 */         return flags;
/*      */       }
/*  908 */       if (hideProcessStackDialog)
/*  909 */         return flags;
/*  910 */       String note = (flags & 0x80) == 0 ? " There is\nno Undo if you select \"Yes\"." : "";
/*  911 */       YesNoCancelDialog d = new YesNoCancelDialog(getInstance(), "Process Stack?", "Process all " + stackSize + " images?" + note);
/*      */ 
/*  913 */       if (d.cancelPressed())
/*  914 */         return 4096;
/*  915 */       if (d.yesPressed()) {
/*  916 */         if ((imp.getStack().isVirtual()) && ((flags & 0x80) == 0)) {
/*  917 */           int size = (stackSize * imp.getWidth() * imp.getHeight() * imp.getBytesPerPixel() + 524288) / 1048576;
/*  918 */           String msg = "Use the Process>Batch>Virtual Stack command\nto process a virtual stack ike this one or convert\nit to a normal stack using Image>Duplicate, which\nwill require " + size + "MB of additional memory.";
/*      */ 
/*  923 */           error(msg);
/*  924 */           return 4096;
/*      */         }
/*  926 */         if (Recorder.record)
/*  927 */           Recorder.recordOption("stack");
/*  928 */         return flags + 32;
/*      */       }
/*  930 */       if (Recorder.record)
/*  931 */         Recorder.recordOption("slice");
/*      */     }
/*  933 */     return flags;
/*      */   }
/*      */ 
/*      */   public static void makeRectangle(int x, int y, int width, int height)
/*      */   {
/*  939 */     if ((width <= 0) || (height < 0)) {
/*  940 */       getImage().killRoi();
/*      */     } else {
/*  942 */       ImagePlus img = getImage();
/*  943 */       if (Interpreter.isBatchMode())
/*  944 */         img.setRoi(new Roi(x, y, width, height), false);
/*      */       else
/*  946 */         img.setRoi(x, y, width, height);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void makeOval(int x, int y, int width, int height)
/*      */   {
/*  953 */     if ((width <= 0) || (height < 0)) {
/*  954 */       getImage().killRoi();
/*      */     } else {
/*  956 */       ImagePlus img = getImage();
/*  957 */       img.setRoi(new OvalRoi(x, y, width, height));
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void makeLine(int x1, int y1, int x2, int y2)
/*      */   {
/*  963 */     getImage().setRoi(new Line(x1, y1, x2, y2));
/*      */   }
/*      */ 
/*      */   public static void makePoint(int x, int y)
/*      */   {
/*  968 */     ImagePlus img = getImage();
/*  969 */     Roi roi = img.getRoi();
/*  970 */     if ((shiftKeyDown()) && (roi != null) && (roi.getType() == 10)) {
/*  971 */       Polygon p = roi.getPolygon();
/*  972 */       p.addPoint(x, y);
/*  973 */       img.setRoi(new PointRoi(p.xpoints, p.ypoints, p.npoints));
/*  974 */       setKeyUp(16);
/*  975 */     } else if ((altKeyDown()) && (roi != null) && (roi.getType() == 10)) {
/*  976 */       ((PolygonRoi)roi).deleteHandle(x, y);
/*  977 */       setKeyUp(18);
/*      */     } else {
/*  979 */       img.setRoi(new PointRoi(x, y));
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void makeLine(double x1, double y1, double x2, double y2) {
/*  984 */     getImage().setRoi(new Line(x1, y1, x2, y2));
/*      */   }
/*      */ 
/*      */   public static void setMinAndMax(double min, double max)
/*      */   {
/*  989 */     setMinAndMax(getImage(), min, max, 7);
/*      */   }
/*      */ 
/*      */   public static void setMinAndMax(ImagePlus img, double min, double max)
/*      */   {
/*  994 */     setMinAndMax(img, min, max, 7);
/*      */   }
/*      */ 
/*      */   public static void setMinAndMax(double min, double max, int channels)
/*      */   {
/* 1000 */     setMinAndMax(getImage(), min, max, channels);
/*      */   }
/*      */ 
/*      */   private static void setMinAndMax(ImagePlus img, double min, double max, int channels) {
/* 1004 */     Calibration cal = img.getCalibration();
/* 1005 */     min = cal.getRawValue(min);
/* 1006 */     max = cal.getRawValue(max);
/* 1007 */     if (channels == 7)
/* 1008 */       img.setDisplayRange(min, max);
/*      */     else
/* 1010 */       img.setDisplayRange(min, max, channels);
/* 1011 */     img.updateAndDraw();
/*      */   }
/*      */ 
/*      */   public static void resetMinAndMax()
/*      */   {
/* 1017 */     resetMinAndMax(getImage());
/*      */   }
/*      */ 
/*      */   public static void resetMinAndMax(ImagePlus img)
/*      */   {
/* 1023 */     img.resetDisplayRange();
/* 1024 */     img.updateAndDraw();
/*      */   }
/*      */ 
/*      */   public static void setThreshold(double lowerThreshold, double upperThresold)
/*      */   {
/* 1032 */     setThreshold(lowerThreshold, upperThresold, null);
/*      */   }
/*      */ 
/*      */   public static void setThreshold(double lowerThreshold, double upperThreshold, String displayMode)
/*      */   {
/* 1038 */     setThreshold(getImage(), lowerThreshold, upperThreshold, displayMode);
/*      */   }
/*      */ 
/*      */   public static void setThreshold(ImagePlus img, double lowerThreshold, double upperThreshold)
/*      */   {
/* 1043 */     setThreshold(img, lowerThreshold, upperThreshold, "Red");
/*      */   }
/*      */ 
/*      */   public static void setThreshold(ImagePlus img, double lowerThreshold, double upperThreshold, String displayMode)
/*      */   {
/* 1049 */     int mode = 0;
/* 1050 */     if (displayMode != null) {
/* 1051 */       displayMode = displayMode.toLowerCase(Locale.US);
/* 1052 */       if (displayMode.indexOf("black") != -1)
/* 1053 */         mode = 1;
/* 1054 */       else if (displayMode.indexOf("over") != -1)
/* 1055 */         mode = 3;
/* 1056 */       else if (displayMode.indexOf("no") != -1)
/* 1057 */         mode = 2;
/*      */     }
/* 1059 */     Calibration cal = img.getCalibration();
/* 1060 */     lowerThreshold = cal.getRawValue(lowerThreshold);
/* 1061 */     upperThreshold = cal.getRawValue(upperThreshold);
/* 1062 */     img.getProcessor().setThreshold(lowerThreshold, upperThreshold, mode);
/* 1063 */     if (mode != 2) {
/* 1064 */       img.getProcessor().setLutAnimation(true);
/* 1065 */       img.updateAndDraw();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setAutoThreshold(ImagePlus imp, String method) {
/* 1070 */     ImageProcessor ip = imp.getProcessor();
/* 1071 */     if ((ip instanceof ColorProcessor))
/* 1072 */       throw new IllegalArgumentException("Non-RGB image required");
/* 1073 */     ip.setRoi(imp.getRoi());
/* 1074 */     if (method != null)
/*      */       try {
/* 1076 */         if (method.indexOf("stack") != -1)
/* 1077 */           setStackThreshold(imp, ip, method);
/*      */         else
/* 1079 */           ip.setAutoThreshold(method);
/*      */       } catch (Exception e) {
/* 1081 */         log(e.getMessage());
/*      */       }
/*      */     else
/* 1084 */       ip.setAutoThreshold(1, 0);
/* 1085 */     imp.updateAndDraw();
/*      */   }
/*      */ 
/*      */   private static void setStackThreshold(ImagePlus imp, ImageProcessor ip, String method) {
/* 1089 */     boolean darkBackground = method.indexOf("dark") != -1;
/* 1090 */     ImageStatistics stats = new StackStatistics(imp);
/* 1091 */     AutoThresholder thresholder = new AutoThresholder();
/* 1092 */     double min = 0.0D; double max = 255.0D;
/* 1093 */     if (imp.getBitDepth() != 8) {
/* 1094 */       min = stats.min;
/* 1095 */       max = stats.max;
/*      */     }
/* 1097 */     int threshold = thresholder.getThreshold(method, stats.histogram);
/*      */     double upper;
/*      */     double lower;
/*      */     double upper;
/* 1099 */     if (darkBackground)
/*      */     {
/*      */       double upper;
/* 1100 */       if (ip.isInvertedLut()) {
/* 1101 */         double lower = 0.0D; upper = threshold;
/*      */       } else {
/* 1103 */         double lower = threshold + 1; upper = 255.0D;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       double upper;
/* 1105 */       if (ip.isInvertedLut()) {
/* 1106 */         double lower = threshold + 1; upper = 255.0D;
/*      */       } else {
/* 1108 */         lower = 0.0D; upper = threshold;
/*      */       }
/*      */     }
/* 1110 */     if (lower > 255.0D) lower = 255.0D;
/* 1111 */     if (max > min) {
/* 1112 */       lower = min + lower / 255.0D * (max - min);
/* 1113 */       upper = min + upper / 255.0D * (max - min);
/*      */     } else {
/* 1115 */       lower = upper = min;
/* 1116 */     }ip.setMinAndMax(min, max);
/* 1117 */     ip.setThreshold(lower, upper, 0);
/* 1118 */     imp.updateAndDraw();
/*      */   }
/*      */ 
/*      */   public static void resetThreshold()
/*      */   {
/* 1123 */     resetThreshold(getImage());
/*      */   }
/*      */ 
/*      */   public static void resetThreshold(ImagePlus img)
/*      */   {
/* 1128 */     ImageProcessor ip = img.getProcessor();
/* 1129 */     ip.resetThreshold();
/* 1130 */     ip.setLutAnimation(true);
/* 1131 */     img.updateAndDraw();
/*      */   }
/*      */ 
/*      */   public static void selectWindow(int id)
/*      */   {
/* 1137 */     if (id > 0)
/* 1138 */       id = WindowManager.getNthImageID(id);
/* 1139 */     ImagePlus imp = WindowManager.getImage(id);
/* 1140 */     if (imp == null)
/* 1141 */       error("Macro Error", "Image " + id + " not found or no images are open.");
/* 1142 */     if (Interpreter.isBatchMode()) {
/* 1143 */       ImagePlus imp2 = WindowManager.getCurrentImage();
/* 1144 */       if ((imp2 != null) && (imp2 != imp)) imp2.saveRoi();
/* 1145 */       WindowManager.setTempCurrentImage(imp);
/* 1146 */       WindowManager.setWindow(null);
/*      */     } else {
/* 1148 */       ImageWindow win = imp.getWindow();
/* 1149 */       win.toFront();
/* 1150 */       WindowManager.setWindow(win);
/* 1151 */       long start = System.currentTimeMillis();
/*      */ 
/* 1153 */       String thread = Thread.currentThread().getName();
/* 1154 */       int timeout = (thread != null) && (thread.indexOf("EventQueue") != -1) ? 0 : 2000;
/*      */       do {
/* 1156 */         wait(10);
/* 1157 */         imp = WindowManager.getCurrentImage();
/* 1158 */         if ((imp != null) && (imp.getID() == id))
/* 1159 */           return; 
/*      */       }
/* 1160 */       while (System.currentTimeMillis() - start <= timeout);
/* 1161 */       WindowManager.setCurrentWindow(win);
/* 1162 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void selectWindow(String title)
/*      */   {
/* 1170 */     if ((title.equals("ImageJ")) && (ij != null)) {
/* 1171 */       ij.toFront(); return;
/* 1172 */     }long start = System.currentTimeMillis();
/* 1173 */     while (System.currentTimeMillis() - start < 3000L) {
/* 1174 */       Frame frame = WindowManager.getFrame(title);
/* 1175 */       if ((frame != null) && (!(frame instanceof ImageWindow))) {
/* 1176 */         selectWindow(frame);
/* 1177 */         return;
/*      */       }
/* 1179 */       int[] wList = WindowManager.getIDList();
/* 1180 */       int len = wList != null ? wList.length : 0;
/* 1181 */       for (int i = 0; i < len; i++) {
/* 1182 */         ImagePlus imp = WindowManager.getImage(wList[i]);
/* 1183 */         if ((imp != null) && 
/* 1184 */           (imp.getTitle().equals(title))) {
/* 1185 */           selectWindow(imp.getID());
/* 1186 */           return;
/*      */         }
/*      */       }
/*      */ 
/* 1190 */       wait(10);
/*      */     }
/* 1192 */     error("Macro Error", "No window with the title \"" + title + "\" found.");
/*      */   }
/*      */ 
/*      */   static void selectWindow(Frame frame) {
/* 1196 */     frame.toFront();
/* 1197 */     long start = System.currentTimeMillis();
/*      */     do {
/* 1199 */       wait(10);
/* 1200 */       if (WindowManager.getFrontWindow() == frame)
/* 1201 */         return; 
/*      */     }
/* 1202 */     while (System.currentTimeMillis() - start <= 1000L);
/* 1203 */     WindowManager.setWindow(frame);
/*      */   }
/*      */ 
/*      */   public static void setForegroundColor(int red, int green, int blue)
/*      */   {
/* 1211 */     setColor(red, green, blue, true);
/*      */   }
/*      */ 
/*      */   public static void setBackgroundColor(int red, int green, int blue)
/*      */   {
/* 1216 */     setColor(red, green, blue, false);
/*      */   }
/*      */ 
/*      */   static void setColor(int red, int green, int blue, boolean foreground) {
/* 1220 */     if (red < 0) red = 0; if (green < 0) green = 0; if (blue < 0) blue = 0;
/* 1221 */     if (red > 255) red = 255; if (green > 255) green = 255; if (blue > 255) blue = 255;
/* 1222 */     Color c = new Color(red, green, blue);
/* 1223 */     if (foreground) {
/* 1224 */       Toolbar.setForegroundColor(c);
/* 1225 */       ImagePlus img = WindowManager.getCurrentImage();
/* 1226 */       if (img != null)
/* 1227 */         img.getProcessor().setColor(c);
/*      */     } else {
/* 1229 */       Toolbar.setBackgroundColor(c);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setTool(int id)
/*      */   {
/* 1235 */     Toolbar.getInstance().setTool(id);
/*      */   }
/*      */ 
/*      */   public static boolean setTool(String name)
/*      */   {
/* 1241 */     return Toolbar.getInstance().setTool(name);
/*      */   }
/*      */ 
/*      */   public static String getToolName()
/*      */   {
/* 1246 */     return Toolbar.getToolName();
/*      */   }
/*      */ 
/*      */   public static int doWand(int x, int y)
/*      */   {
/* 1252 */     return doWand(getImage(), x, y, 0.0D, null);
/*      */   }
/*      */ 
/*      */   public static int doWand(int x, int y, double tolerance, String mode)
/*      */   {
/* 1263 */     return doWand(getImage(), x, y, tolerance, mode);
/*      */   }
/*      */ 
/*      */   public static int doWand(ImagePlus img, int x, int y, double tolerance, String mode)
/*      */   {
/* 1268 */     ImageProcessor ip = img.getProcessor();
/* 1269 */     if ((img.getType() == 2) && (Double.isNaN(ip.getPixelValue(x, y))))
/* 1270 */       return 0;
/* 1271 */     int imode = 1;
/* 1272 */     if (mode != null) {
/* 1273 */       if (mode.startsWith("4"))
/* 1274 */         imode = 4;
/* 1275 */       else if (mode.startsWith("8"))
/* 1276 */         imode = 8;
/*      */     }
/* 1278 */     Wand w = new Wand(ip);
/* 1279 */     double t1 = ip.getMinThreshold();
/* 1280 */     if ((t1 == -808080.0D) || ((ip.getLutUpdateMode() == 2) && (tolerance > 0.0D)))
/* 1281 */       w.autoOutline(x, y, tolerance, imode);
/*      */     else
/* 1283 */       w.autoOutline(x, y, t1, ip.getMaxThreshold(), imode);
/* 1284 */     if (w.npoints > 0) {
/* 1285 */       Roi previousRoi = img.getRoi();
/* 1286 */       int type = Wand.allPoints() ? 3 : 4;
/* 1287 */       Roi roi = new PolygonRoi(w.xpoints, w.ypoints, w.npoints, type);
/* 1288 */       img.killRoi();
/* 1289 */       img.setRoi(roi);
/*      */ 
/* 1291 */       if (previousRoi != null)
/* 1292 */         roi.update(shiftKeyDown(), altKeyDown());
/*      */     }
/* 1294 */     return w.npoints;
/*      */   }
/*      */ 
/*      */   public static void setPasteMode(String mode)
/*      */   {
/* 1300 */     mode = mode.toLowerCase(Locale.US);
/* 1301 */     int m = 0;
/* 1302 */     if ((mode.startsWith("ble")) || (mode.startsWith("ave")))
/* 1303 */       m = 7;
/* 1304 */     else if (mode.startsWith("diff"))
/* 1305 */       m = 8;
/* 1306 */     else if (mode.indexOf("zero") != -1)
/* 1307 */       m = 14;
/* 1308 */     else if (mode.startsWith("tran"))
/* 1309 */       m = 2;
/* 1310 */     else if (mode.startsWith("and"))
/* 1311 */       m = 9;
/* 1312 */     else if (mode.startsWith("or"))
/* 1313 */       m = 10;
/* 1314 */     else if (mode.startsWith("xor"))
/* 1315 */       m = 11;
/* 1316 */     else if (mode.startsWith("sub"))
/* 1317 */       m = 4;
/* 1318 */     else if (mode.startsWith("add"))
/* 1319 */       m = 3;
/* 1320 */     else if (mode.startsWith("div"))
/* 1321 */       m = 6;
/* 1322 */     else if (mode.startsWith("mul"))
/* 1323 */       m = 5;
/* 1324 */     else if (mode.startsWith("min"))
/* 1325 */       m = 12;
/* 1326 */     else if (mode.startsWith("max"))
/* 1327 */       m = 13;
/* 1328 */     Roi.setPasteMode(m);
/*      */   }
/*      */ 
/*      */   public static ImagePlus getImage()
/*      */   {
/* 1334 */     ImagePlus img = WindowManager.getCurrentImage();
/* 1335 */     if (img == null) {
/* 1336 */       noImage();
/* 1337 */       if (ij == null)
/* 1338 */         System.exit(0);
/*      */       else
/* 1340 */         abort();
/*      */     }
/* 1342 */     return img;
/*      */   }
/*      */ 
/*      */   public static void setSlice(int slice)
/*      */   {
/* 1347 */     getImage().setSlice(slice);
/*      */   }
/*      */ 
/*      */   public static String getVersion()
/*      */   {
/* 1352 */     return "1.45s";
/*      */   }
/*      */ 
/*      */   public static String getDirectory(String title)
/*      */   {
/* 1363 */     if (title.equals("plugins"))
/* 1364 */       return Menus.getPlugInsPath();
/* 1365 */     if (title.equals("macros"))
/* 1366 */       return Menus.getMacrosPath();
/* 1367 */     if (title.equals("luts")) {
/* 1368 */       String ijdir = getIJDir();
/* 1369 */       if (ijdir != null) {
/* 1370 */         return ijdir + "luts" + File.separator;
/*      */       }
/* 1372 */       return null;
/* 1373 */     }if (title.equals("home"))
/* 1374 */       return System.getProperty("user.home") + File.separator;
/* 1375 */     if (title.equals("startup"))
/* 1376 */       return Prefs.getHomeDir() + File.separator;
/* 1377 */     if (title.equals("imagej"))
/* 1378 */       return getIJDir();
/* 1379 */     if (title.equals("current"))
/* 1380 */       return OpenDialog.getDefaultDirectory();
/* 1381 */     if (title.equals("temp")) {
/* 1382 */       String dir = System.getProperty("java.io.tmpdir");
/* 1383 */       if (isMacintosh()) dir = "/tmp/";
/* 1384 */       if ((dir != null) && (!dir.endsWith(File.separator))) dir = dir + File.separator;
/* 1385 */       return dir;
/* 1386 */     }if (title.equals("image")) {
/* 1387 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 1388 */       FileInfo fi = imp != null ? imp.getOriginalFileInfo() : null;
/* 1389 */       if ((fi != null) && (fi.directory != null)) {
/* 1390 */         return fi.directory;
/*      */       }
/* 1392 */       return null;
/*      */     }
/* 1394 */     DirectoryChooser dc = new DirectoryChooser(title);
/* 1395 */     String dir = dc.getDirectory();
/* 1396 */     if (dir == null) Macro.abort();
/* 1397 */     return dir;
/*      */   }
/*      */ 
/*      */   private static String getIJDir()
/*      */   {
/* 1402 */     String path = Menus.getPlugInsPath();
/* 1403 */     if (path == null) return null;
/* 1404 */     String ijdir = new File(path).getParent();
/* 1405 */     if (ijdir != null) ijdir = ijdir + File.separator;
/* 1406 */     return ijdir;
/*      */   }
/*      */ 
/*      */   public static void open()
/*      */   {
/* 1414 */     open(null);
/*      */   }
/*      */ 
/*      */   public static void open(String path)
/*      */   {
/* 1423 */     if ((ij == null) && (Menus.getCommands() == null)) init();
/* 1424 */     Opener o = new Opener();
/* 1425 */     macroRunning = true;
/* 1426 */     if ((path == null) || (path.equals("")))
/* 1427 */       o.open();
/*      */     else
/* 1429 */       o.open(path);
/* 1430 */     macroRunning = false;
/*      */   }
/*      */ 
/*      */   public static void open(String path, int n)
/*      */   {
/* 1435 */     if ((ij == null) && (Menus.getCommands() == null)) init();
/* 1436 */     ImagePlus imp = openImage(path, n);
/* 1437 */     if (imp != null) imp.show();
/*      */   }
/*      */ 
/*      */   public static ImagePlus openImage(String path)
/*      */   {
/* 1447 */     return new Opener().openImage(path);
/*      */   }
/*      */ 
/*      */   public static ImagePlus openImage(String path, int n)
/*      */   {
/* 1452 */     return new Opener().openImage(path, n);
/*      */   }
/*      */ 
/*      */   public static ImagePlus openImage()
/*      */   {
/* 1457 */     return openImage(null);
/*      */   }
/*      */ 
/*      */   public static String openUrlAsString(String url)
/*      */   {
/* 1464 */     StringBuffer sb = null;
/* 1465 */     url = url.replaceAll(" ", "%20");
/*      */     try {
/* 1467 */       URL u = new URL(url);
/* 1468 */       URLConnection uc = u.openConnection();
/* 1469 */       long len = uc.getContentLength();
/* 1470 */       if (len > 1048576L)
/* 1471 */         return "<Error: file is larger than 1MB>";
/* 1472 */       InputStream in = u.openStream();
/* 1473 */       BufferedReader br = new BufferedReader(new InputStreamReader(in));
/* 1474 */       sb = new StringBuffer();
/*      */       String line;
/* 1476 */       while ((line = br.readLine()) != null)
/* 1477 */         sb.append(line + "\n");
/* 1478 */       in.close();
/*      */     } catch (Exception e) {
/* 1480 */       return "<Error: " + e + ">";
/*      */     }
/* 1482 */     if (sb != null) {
/* 1483 */       return new String(sb);
/*      */     }
/* 1485 */     return "";
/*      */   }
/*      */ 
/*      */   public static void save(String path)
/*      */   {
/* 1491 */     save(null, path);
/*      */   }
/*      */ 
/*      */   public static void save(ImagePlus imp, String path)
/*      */   {
/* 1498 */     int dotLoc = path.lastIndexOf('.');
/* 1499 */     if (dotLoc != -1) {
/* 1500 */       ImagePlus imp2 = imp;
/* 1501 */       if (imp2 == null) imp2 = WindowManager.getCurrentImage();
/* 1502 */       String title = imp2 != null ? imp2.getTitle() : null;
/* 1503 */       saveAs(imp, path.substring(dotLoc + 1), path);
/* 1504 */       if (title != null) imp2.setTitle(title); 
/*      */     }
/* 1506 */     else { error("The file path passed to IJ.save() method or save()\nmacro function is missing the required extension.\n \n\"" + path + "\""); }
/*      */ 
/*      */   }
/*      */ 
/*      */   public static void saveAs(String format, String path)
/*      */   {
/* 1515 */     saveAs(null, format, path);
/*      */   }
/*      */ 
/*      */   public static void saveAs(ImagePlus imp, String format, String path)
/*      */   {
/* 1522 */     if (format == null) return;
/* 1523 */     if ((path != null) && (path.length() == 0)) path = null;
/* 1524 */     format = format.toLowerCase(Locale.US);
/* 1525 */     if (format.indexOf("tif") != -1) {
/* 1526 */       if ((path != null) && (!path.endsWith(".tiff")))
/* 1527 */         path = updateExtension(path, ".tif");
/* 1528 */       format = "Tiff...";
/* 1529 */     } else if ((format.indexOf("jpeg") != -1) || (format.indexOf("jpg") != -1)) {
/* 1530 */       path = updateExtension(path, ".jpg");
/* 1531 */       format = "Jpeg...";
/* 1532 */     } else if (format.indexOf("gif") != -1) {
/* 1533 */       path = updateExtension(path, ".gif");
/* 1534 */       format = "Gif...";
/* 1535 */     } else if (format.indexOf("text image") != -1) {
/* 1536 */       path = updateExtension(path, ".txt");
/* 1537 */       format = "Text Image...";
/* 1538 */     } else if ((format.indexOf("text") != -1) || (format.indexOf("txt") != -1)) {
/* 1539 */       if ((path != null) && (!path.endsWith(".xls")))
/* 1540 */         path = updateExtension(path, ".txt");
/* 1541 */       format = "Text...";
/* 1542 */     } else if (format.indexOf("zip") != -1) {
/* 1543 */       path = updateExtension(path, ".zip");
/* 1544 */       format = "ZIP...";
/* 1545 */     } else if (format.indexOf("raw") != -1)
/*      */     {
/* 1547 */       format = "Raw Data...";
/* 1548 */     } else if (format.indexOf("avi") != -1) {
/* 1549 */       path = updateExtension(path, ".avi");
/* 1550 */       format = "AVI... ";
/* 1551 */     } else if (format.indexOf("bmp") != -1) {
/* 1552 */       path = updateExtension(path, ".bmp");
/* 1553 */       format = "BMP...";
/* 1554 */     } else if (format.indexOf("fits") != -1) {
/* 1555 */       path = updateExtension(path, ".fits");
/* 1556 */       format = "FITS...";
/* 1557 */     } else if (format.indexOf("png") != -1) {
/* 1558 */       path = updateExtension(path, ".png");
/* 1559 */       format = "PNG...";
/* 1560 */     } else if (format.indexOf("pgm") != -1) {
/* 1561 */       path = updateExtension(path, ".pgm");
/* 1562 */       format = "PGM...";
/* 1563 */     } else if (format.indexOf("lut") != -1) {
/* 1564 */       path = updateExtension(path, ".lut");
/* 1565 */       format = "LUT...";
/* 1566 */     } else if ((format.indexOf("results") != -1) || (format.indexOf("measurements") != -1)) {
/* 1567 */       format = "Results...";
/* 1568 */     } else if ((format.indexOf("selection") != -1) || (format.indexOf("roi") != -1)) {
/* 1569 */       path = updateExtension(path, ".roi");
/* 1570 */       format = "Selection...";
/* 1571 */     } else if ((format.indexOf("xy") != -1) || (format.indexOf("coordinates") != -1)) {
/* 1572 */       path = updateExtension(path, ".txt");
/* 1573 */       format = "XY Coordinates...";
/*      */     } else {
/* 1575 */       error("Unsupported save() or saveAs() file format: \"" + format + "\"\n \n\"" + path + "\"");
/* 1576 */     }if (path == null)
/* 1577 */       run(format);
/*      */     else
/* 1579 */       run(imp, format, "save=[" + path + "]");
/*      */   }
/*      */ 
/*      */   static String updateExtension(String path, String extension) {
/* 1583 */     if (path == null) return null;
/* 1584 */     int dotIndex = path.lastIndexOf(".");
/* 1585 */     int separatorIndex = path.lastIndexOf(File.separator);
/* 1586 */     if ((dotIndex >= 0) && (dotIndex > separatorIndex) && (path.length() - dotIndex <= 5)) {
/* 1587 */       if ((dotIndex + 1 < path.length()) && (Character.isDigit(path.charAt(dotIndex + 1))))
/* 1588 */         path = path + extension;
/*      */       else
/* 1590 */         path = path.substring(0, dotIndex) + extension;
/*      */     }
/* 1592 */     else path = path + extension;
/* 1593 */     return path;
/*      */   }
/*      */ 
/*      */   public static String saveString(String string, String path)
/*      */   {
/* 1600 */     return write(string, path, false);
/*      */   }
/*      */ 
/*      */   public static String append(String string, String path)
/*      */   {
/* 1607 */     return write(string + "\n", path, true);
/*      */   }
/*      */ 
/*      */   private static String write(String string, String path, boolean append) {
/* 1611 */     if ((path == null) || (path.equals(""))) {
/* 1612 */       String msg = append ? "Append String..." : "Save String...";
/* 1613 */       SaveDialog sd = new SaveDialog(msg, "Untitled", ".txt");
/* 1614 */       String name = sd.getFileName();
/* 1615 */       if (name == null) return null;
/* 1616 */       path = sd.getDirectory() + name;
/*      */     }
/*      */     try {
/* 1619 */       BufferedWriter out = new BufferedWriter(new FileWriter(path, append));
/* 1620 */       out.write(string);
/* 1621 */       out.close();
/*      */     } catch (IOException e) {
/* 1623 */       return "" + e;
/*      */     }
/* 1625 */     return null;
/*      */   }
/*      */ 
/*      */   public static String openAsString(String path)
/*      */   {
/* 1633 */     if ((path == null) || (path.equals(""))) {
/* 1634 */       OpenDialog od = new OpenDialog("Open Text File", "");
/* 1635 */       String directory = od.getDirectory();
/* 1636 */       String name = od.getFileName();
/* 1637 */       if (name == null) return null;
/* 1638 */       path = directory + name;
/*      */     }
/* 1640 */     String str = "";
/* 1641 */     File file = new File(path);
/* 1642 */     if (!file.exists())
/* 1643 */       return "Error: file not found";
/*      */     try {
/* 1645 */       StringBuffer sb = new StringBuffer(5000);
/* 1646 */       BufferedReader r = new BufferedReader(new FileReader(file));
/*      */       while (true) {
/* 1648 */         String s = r.readLine();
/* 1649 */         if (s == null) {
/*      */           break;
/*      */         }
/* 1652 */         sb.append(s + "\n");
/*      */       }
/* 1654 */       r.close();
/* 1655 */       str = new String(sb);
/*      */     }
/*      */     catch (Exception e) {
/* 1658 */       str = "Error: " + e.getMessage();
/*      */     }
/* 1660 */     return str;
/*      */   }
/*      */ 
/*      */   public static ImagePlus createImage(String title, String type, int width, int height, int depth)
/*      */   {
/* 1668 */     type = type.toLowerCase(Locale.US);
/* 1669 */     int bitDepth = 8;
/* 1670 */     if (type.indexOf("16") != -1) bitDepth = 16;
/* 1671 */     if ((type.indexOf("24") != -1) || (type.indexOf("rgb") != -1)) bitDepth = 24;
/* 1672 */     if (type.indexOf("32") != -1) bitDepth = 32;
/* 1673 */     int options = 4;
/* 1674 */     if ((bitDepth == 16) || (bitDepth == 32))
/* 1675 */       options = 1;
/* 1676 */     if (type.indexOf("white") != -1)
/* 1677 */       options = 4;
/* 1678 */     else if (type.indexOf("black") != -1)
/* 1679 */       options = 1;
/* 1680 */     else if (type.indexOf("ramp") != -1)
/* 1681 */       options = 2;
/* 1682 */     options += 8;
/* 1683 */     return NewImage.createImage(title, width, height, depth, bitDepth, options);
/*      */   }
/*      */ 
/*      */   public static void newImage(String title, String type, int width, int height, int depth)
/*      */   {
/* 1691 */     ImagePlus imp = createImage(title, type, width, height, depth);
/* 1692 */     if (imp != null) {
/* 1693 */       macroRunning = true;
/* 1694 */       imp.show();
/* 1695 */       macroRunning = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static boolean escapePressed()
/*      */   {
/* 1702 */     return escapePressed;
/*      */   }
/*      */ 
/*      */   public static void resetEscape()
/*      */   {
/* 1709 */     escapePressed = false;
/*      */   }
/*      */ 
/*      */   public static void redirectErrorMessages()
/*      */   {
/* 1714 */     redirectErrorMessages = true;
/*      */   }
/*      */ 
/*      */   public static void redirectErrorMessages(boolean redirect)
/*      */   {
/* 1719 */     redirectErrorMessages2 = redirect;
/*      */   }
/*      */ 
/*      */   public static boolean redirectingErrorMessages()
/*      */   {
/* 1724 */     return (redirectErrorMessages) || (redirectErrorMessages2);
/*      */   }
/*      */ 
/*      */   public static void suppressPluginNotFoundError()
/*      */   {
/* 1729 */     suppressPluginNotFoundError = true;
/*      */   }
/*      */ 
/*      */   public static ClassLoader getClassLoader()
/*      */   {
/* 1735 */     if (classLoader == null) {
/* 1736 */       String pluginsDir = Menus.getPlugInsPath();
/* 1737 */       if (pluginsDir == null) {
/* 1738 */         String home = System.getProperty("plugins.dir");
/* 1739 */         if (home != null) {
/* 1740 */           if (!home.endsWith(Prefs.separator)) home = home + Prefs.separator;
/* 1741 */           pluginsDir = home + "plugins" + Prefs.separator;
/* 1742 */           if (!new File(pluginsDir).isDirectory()) pluginsDir = home;
/*      */         }
/*      */       }
/* 1745 */       if (pluginsDir == null) {
/* 1746 */         return IJ.class.getClassLoader();
/*      */       }
/* 1748 */       if (Menus.jnlp)
/* 1749 */         classLoader = new PluginClassLoader(pluginsDir, true);
/*      */       else {
/* 1751 */         classLoader = new PluginClassLoader(pluginsDir);
/*      */       }
/*      */     }
/* 1754 */     return classLoader;
/*      */   }
/*      */ 
/*      */   public static Dimension getScreenSize()
/*      */   {
/* 1759 */     if (isWindows())
/* 1760 */       return Toolkit.getDefaultToolkit().getScreenSize();
/* 1761 */     if (GraphicsEnvironment.isHeadless()) {
/* 1762 */       return new Dimension(0, 0);
/*      */     }
/*      */ 
/* 1765 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 1766 */     GraphicsDevice[] gd = ge.getScreenDevices();
/* 1767 */     GraphicsConfiguration[] gc = gd[0].getConfigurations();
/* 1768 */     Rectangle bounds = gc[0].getBounds();
/* 1769 */     if ((bounds.x == 0) && (bounds.y == 0)) {
/* 1770 */       return new Dimension(bounds.width, bounds.height);
/*      */     }
/* 1772 */     return Toolkit.getDefaultToolkit().getScreenSize();
/*      */   }
/*      */ 
/*      */   static void abort() {
/* 1776 */     if ((ij != null) || (Interpreter.isBatchMode()))
/* 1777 */       throw new RuntimeException("Macro canceled");
/*      */   }
/*      */ 
/*      */   static void setClassLoader(ClassLoader loader) {
/* 1781 */     classLoader = loader;
/*      */   }
/*      */ 
/*      */   public static void handleException(Throwable e)
/*      */   {
/* 1787 */     if (exceptionHandler != null) {
/* 1788 */       exceptionHandler.handle(e);
/* 1789 */       return;
/*      */     }
/* 1791 */     CharArrayWriter caw = new CharArrayWriter();
/* 1792 */     PrintWriter pw = new PrintWriter(caw);
/* 1793 */     e.printStackTrace(pw);
/* 1794 */     String s = caw.toString();
/* 1795 */     if (getInstance() != null)
/* 1796 */       new TextWindow("Exception", s, 500, 300);
/*      */     else
/* 1798 */       log(s);
/*      */   }
/*      */ 
/*      */   public static void setExceptionHandler(ExceptionHandler handler)
/*      */   {
/* 1804 */     exceptionHandler = handler;
/*      */   }
/*      */ 
/*      */   public static void addEventListener(IJEventListener listener)
/*      */   {
/* 1814 */     eventListeners.addElement(listener);
/*      */   }
/*      */ 
/*      */   public static void removeEventListener(IJEventListener listener) {
/* 1818 */     eventListeners.removeElement(listener);
/*      */   }
/*      */ 
/*      */   public static void notifyEventListeners(int eventID) {
/* 1822 */     synchronized (eventListeners) {
/* 1823 */       for (int i = 0; i < eventListeners.size(); i++) {
/* 1824 */         IJEventListener listener = (IJEventListener)eventListeners.elementAt(i);
/* 1825 */         listener.eventOccurred(eventID);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   56 */     osname = System.getProperty("os.name");
/*   57 */     isWin = osname.startsWith("Windows");
/*   58 */     isMac = (!isWin) && (osname.startsWith("Mac"));
/*   59 */     isLinux = osname.startsWith("Linux");
/*   60 */     isVista = (isWin) && ((osname.indexOf("Vista") != -1) || (osname.indexOf(" 7") != -1));
/*   61 */     String version = System.getProperty("java.version").substring(0, 3);
/*   62 */     if (version.compareTo("2.9") <= 0) {
/*   63 */       isJava2 = version.compareTo("1.1") > 0;
/*   64 */       isJava14 = version.compareTo("1.3") > 0;
/*   65 */       isJava15 = version.compareTo("1.4") > 0;
/*   66 */       isJava16 = version.compareTo("1.5") > 0;
/*   67 */       isJava17 = version.compareTo("1.6") > 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface ExceptionHandler
/*      */   {
/*      */     public abstract void handle(Throwable paramThrowable);
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.IJ
 * JD-Core Version:    0.6.2
 */