/*      */ package ij.macro;
/*      */ 
/*      */ import ij.CompositeImage;
/*      */ import ij.IJ;
/*      */ import ij.ImageJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.ImageStack;
/*      */ import ij.Menus;
/*      */ import ij.Prefs;
/*      */ import ij.Undo;
/*      */ import ij.WindowManager;
/*      */ import ij.gui.EllipseRoi;
/*      */ import ij.gui.GenericDialog;
/*      */ import ij.gui.HistogramWindow;
/*      */ import ij.gui.ImageCanvas;
/*      */ import ij.gui.ImageWindow;
/*      */ import ij.gui.Line;
/*      */ import ij.gui.Overlay;
/*      */ import ij.gui.Plot;
/*      */ import ij.gui.PlotWindow;
/*      */ import ij.gui.PointRoi;
/*      */ import ij.gui.PolygonRoi;
/*      */ import ij.gui.ProfilePlot;
/*      */ import ij.gui.ProgressBar;
/*      */ import ij.gui.Roi;
/*      */ import ij.gui.ShapeRoi;
/*      */ import ij.gui.StackWindow;
/*      */ import ij.gui.TextRoi;
/*      */ import ij.gui.Toolbar;
/*      */ import ij.gui.WaitForUserDialog;
/*      */ import ij.gui.Wand;
/*      */ import ij.gui.YesNoCancelDialog;
/*      */ import ij.io.FileInfo;
/*      */ import ij.io.FileSaver;
/*      */ import ij.io.OpenDialog;
/*      */ import ij.io.Opener;
/*      */ import ij.io.SaveDialog;
/*      */ import ij.measure.Calibration;
/*      */ import ij.measure.CurveFitter;
/*      */ import ij.measure.Measurements;
/*      */ import ij.measure.ResultsTable;
/*      */ import ij.plugin.ImageCalculator;
/*      */ import ij.plugin.Macro_Runner;
/*      */ import ij.plugin.Straightener;
/*      */ import ij.plugin.filter.Analyzer;
/*      */ import ij.plugin.filter.Info;
/*      */ import ij.plugin.frame.Editor;
/*      */ import ij.plugin.frame.Fitter;
/*      */ import ij.plugin.frame.Recorder;
/*      */ import ij.plugin.frame.RoiManager;
/*      */ import ij.plugin.frame.ThresholdAdjuster;
/*      */ import ij.process.AutoThresholder;
/*      */ import ij.process.ColorProcessor;
/*      */ import ij.process.FloatBlitter;
/*      */ import ij.process.FloatPolygon;
/*      */ import ij.process.FloatProcessor;
/*      */ import ij.process.FloodFiller;
/*      */ import ij.process.ImageConverter;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.process.ImageStatistics;
/*      */ import ij.process.LUT;
/*      */ import ij.process.ShortProcessor;
/*      */ import ij.process.StackStatistics;
/*      */ import ij.text.TextPanel;
/*      */ import ij.text.TextWindow;
/*      */ import ij.util.DicomTools;
/*      */ import ij.util.StringSorter;
/*      */ import ij.util.Tools;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Frame;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.List;
/*      */ import java.awt.Point;
/*      */ import java.awt.Polygon;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.TextArea;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.datatransfer.Clipboard;
/*      */ import java.awt.datatransfer.DataFlavor;
/*      */ import java.awt.datatransfer.StringSelection;
/*      */ import java.awt.datatransfer.Transferable;
/*      */ import java.awt.geom.Ellipse2D.Float;
/*      */ import java.awt.geom.GeneralPath;
/*      */ import java.awt.geom.Rectangle2D.Float;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.CharArrayWriter;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FileReader;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintWriter;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.Properties;
/*      */ import java.util.Random;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class Functions
/*      */   implements MacroConstants, Measurements
/*      */ {
/*      */   Interpreter interp;
/*      */   Program pgm;
/*      */   boolean updateNeeded;
/*   28 */   boolean autoUpdate = true;
/*      */   ImageProcessor defaultIP;
/*      */   int imageType;
/*      */   boolean colorSet;
/*      */   boolean fontSet;
/*      */   Color defaultColor;
/*   33 */   double defaultValue = (0.0D / 0.0D);
/*      */   Plot plot;
/*      */   static int plotID;
/*   36 */   int justification = 0;
/*      */   Font font;
/*      */   GenericDialog gd;
/*      */   PrintWriter writer;
/*      */   boolean altKeyDown;
/*      */   boolean shiftKeyDown;
/*      */   boolean antialiasedText;
/*      */   StringBuffer buffer;
/*      */   RoiManager roiManager;
/*      */   Properties props;
/*      */   CurveFitter fitter;
/*      */   boolean showFitDialog;
/*      */   boolean logFitResults;
/*      */   boolean resultsPending;
/*      */   Overlay offscreenOverlay;
/*      */   Overlay overlayClipboard;
/*      */   GeneralPath overlayPath;
/*      */   boolean saveSettingsCalled;
/*      */   boolean usePointerCursor;
/*      */   boolean hideProcessStackDialog;
/*      */   float divideByZeroValue;
/*      */   int jpegQuality;
/*      */   int saveLineWidth;
/*      */   boolean doScaling;
/*      */   boolean weightedColor;
/*      */   double[] weights;
/*      */   boolean interpolateScaledImages;
/*      */   boolean open100Percent;
/*      */   boolean blackCanvas;
/*      */   boolean useJFileChooser;
/*      */   boolean debugMode;
/*      */   Color foregroundColor;
/*      */   Color backgroundColor;
/*      */   Color roiColor;
/*      */   boolean pointAutoMeasure;
/*      */   boolean requireControlKey;
/*      */   boolean useInvertingLut;
/*      */   boolean disablePopup;
/*      */   int measurements;
/*      */   int decimalPlaces;
/*      */   boolean blackBackground;
/*      */   static WaitForUserDialog waitForUserDialog;
/*      */   int pasteMode;
/*   71 */   int lineWidth = 1;
/*      */   boolean expandableArrays;
/*      */   Random ran;
/*      */ 
/*      */   Functions(Interpreter interp, Program pgm)
/*      */   {
/*   76 */     this.interp = interp;
/*   77 */     this.pgm = pgm;
/*      */   }
/*      */ 
/*      */   void doFunction(int type) {
/*   81 */     switch (type) { case 300:
/*   82 */       doRun(); break;
/*      */     case 302:
/*   83 */       IJ.selectWindow(getStringArg()); resetImage(); break;
/*      */     case 303:
/*   84 */       IJ.wait((int)getArg()); break;
/*      */     case 304:
/*   85 */       this.interp.getParens(); IJ.beep(); break;
/*      */     case 305:
/*   86 */       this.interp.getParens(); IJ.resetMinAndMax(); resetImage(); break;
/*      */     case 306:
/*   87 */       this.interp.getParens(); IJ.resetThreshold(); resetImage(); break;
/*      */     case 307:
/*      */     case 308:
/*   88 */       print(); break;
/*      */     case 309:
/*   89 */       doWand(); break;
/*      */     case 310:
/*   90 */       setMinAndMax(); break;
/*      */     case 311:
/*   91 */       setThreshold(); break;
/*      */     case 312:
/*   92 */       setTool(); break;
/*      */     case 313:
/*   93 */       setForegroundColor(); break;
/*      */     case 314:
/*   94 */       setBackgroundColor(); break;
/*      */     case 336:
/*   95 */       setColor(); break;
/*      */     case 315:
/*   96 */       makeLine(); break;
/*      */     case 316:
/*   97 */       makeOval(); break;
/*      */     case 317:
/*   98 */       makeRectangle(); break;
/*      */     case 318:
/*   99 */       this.interp.dump(); break;
/*      */     case 320:
/*  100 */       lineTo(); break;
/*      */     case 319:
/*  101 */       moveTo(); break;
/*      */     case 321:
/*  102 */       drawLine(); break;
/*      */     case 322:
/*  103 */       requires(); break;
/*      */     case 323:
/*  104 */       this.autoUpdate = getBooleanArg(); break;
/*      */     case 324:
/*  105 */       this.interp.getParens(); updateDisplay(); break;
/*      */     case 325:
/*  106 */       drawString(); break;
/*      */     case 326:
/*  107 */       IJ.setPasteMode(getStringArg()); break;
/*      */     case 327:
/*  108 */       doCommand(); break;
/*      */     case 328:
/*  109 */       IJ.showStatus(getStringArg()); this.interp.statusUpdated = true; break;
/*      */     case 329:
/*  110 */       showProgress(); break;
/*      */     case 330:
/*  111 */       showMessage(false); break;
/*      */     case 351:
/*  112 */       showMessage(true); break;
/*      */     case 331:
/*      */     case 332:
/*  113 */       setPixel(); break;
/*      */     case 333:
/*      */     case 334:
/*      */     case 335:
/*  114 */       doIPMethod(type); break;
/*      */     case 337:
/*  115 */       setLineWidth((int)getArg()); break;
/*      */     case 338:
/*  116 */       changeValues(); break;
/*      */     case 339:
/*  117 */       selectImage(); break;
/*      */     case 340:
/*  118 */       exit(); break;
/*      */     case 341:
/*  119 */       setLocation(); break;
/*      */     case 342:
/*  120 */       getCursorLoc(); break;
/*      */     case 343:
/*  121 */       getLine(); break;
/*      */     case 344:
/*  122 */       getVoxelSize(); break;
/*      */     case 345:
/*  123 */       getHistogram(); break;
/*      */     case 347:
/*      */     case 375:
/*  124 */       getBounds(); break;
/*      */     case 348:
/*  125 */       getLut(); break;
/*      */     case 349:
/*  126 */       setLut(); break;
/*      */     case 350:
/*  127 */       getCoordinates(); break;
/*      */     case 352:
/*  128 */       makeSelection(); break;
/*      */     case 353:
/*  129 */       setResult(); break;
/*      */     case 354:
/*  130 */       updateResults(); break;
/*      */     case 355:
/*  131 */       setBatchMode(); break;
/*      */     case 356:
/*  132 */       doPlot(); break;
/*      */     case 357:
/*  133 */       setJustification(); break;
/*      */     case 358:
/*  134 */       setZCoordinate(); break;
/*      */     case 359:
/*  135 */       getThreshold(); break;
/*      */     case 360:
/*  136 */       getPixelSize(); break;
/*      */     case 361:
/*  137 */       this.interp.getParens(); Undo.setup(6, getImage()); break;
/*      */     case 362:
/*  138 */       saveSettings(); break;
/*      */     case 363:
/*  139 */       restoreSettings(); break;
/*      */     case 364:
/*  140 */       setKeyDown(); break;
/*      */     case 365:
/*  141 */       open(); break;
/*      */     case 366:
/*  142 */       setFont(); break;
/*      */     case 367:
/*  143 */       getMinAndMax(); break;
/*      */     case 368:
/*  144 */       close(); break;
/*      */     case 369:
/*  145 */       setSlice(); break;
/*      */     case 370:
/*  146 */       newImage(); break;
/*      */     case 372:
/*  147 */       IJ.save(getStringArg()); break;
/*      */     case 371:
/*  148 */       saveAs(); break;
/*      */     case 373:
/*  149 */       setAutoThreshold(); break;
/*      */     case 374:
/*  150 */       getImage().setTitle(getStringArg()); break;
/*      */     case 346:
/*  151 */       getStatistics(true); break;
/*      */     case 377:
/*  152 */       getStatistics(false); break;
/*      */     case 378:
/*  153 */       floodFill(); break;
/*      */     case 379:
/*  154 */       restorePreviousTool(); break;
/*      */     case 380:
/*  155 */       setVoxelSize(); break;
/*      */     case 381:
/*  156 */       getLocationAndSize(); break;
/*      */     case 382:
/*  157 */       getDateAndTime(); break;
/*      */     case 383:
/*  158 */       setMetadata(); break;
/*      */     case 384:
/*  159 */       imageCalculator(); break;
/*      */     case 385:
/*  160 */       setRGBWeights(); break;
/*      */     case 386:
/*  161 */       makePolygon(); break;
/*      */     case 387:
/*  162 */       setSelectionName(); break;
/*      */     case 376:
/*      */     case 388:
/*      */     case 389:
/*      */     case 390:
/*  163 */       drawOrFill(type); break;
/*      */     case 391:
/*  164 */       setOption(); break;
/*      */     case 392:
/*  165 */       showText(); break;
/*      */     case 393:
/*  166 */       setSelectionLocation(); break;
/*      */     case 394:
/*  167 */       getDimensions(); break;
/*      */     case 395:
/*  168 */       waitForUser(); break;
/*      */     case 396:
/*  169 */       makePoint(); break;
/*      */     case 397:
/*  170 */       makeText(); break;
/*      */     case 398:
/*  171 */       makeEllipse(); break;
/*      */     case 399:
/*  172 */       getDisplayedArea(); break;
/*      */     case 400:
/*  173 */       toScaled(); break;
/*      */     case 401:
/*  174 */       toUnscaled();
/*      */     case 301: }
/*      */   }
/*      */ 
/*      */   final double getFunctionValue(int type) {
/*  179 */     double value = 0.0D;
/*  180 */     switch (type) { case 1000:
/*  181 */       value = getPixel(); break;
/*      */     case 1001:
/*      */     case 1002:
/*      */     case 1003:
/*      */     case 1004:
/*      */     case 1005:
/*      */     case 1009:
/*      */     case 1010:
/*      */     case 1011:
/*      */     case 1012:
/*      */     case 1027:
/*      */     case 1046:
/*      */     case 1047:
/*  184 */       value = math(type);
/*  185 */       break;
/*      */     case 1006:
/*      */     case 1007:
/*      */     case 1008:
/*      */     case 1036:
/*  186 */       value = math2(type); break;
/*      */     case 1013:
/*  187 */       this.interp.getParens(); value = System.currentTimeMillis(); break;
/*      */     case 1014:
/*  188 */       this.interp.getParens(); value = getImage().getWidth(); break;
/*      */     case 1015:
/*  189 */       this.interp.getParens(); value = getImage().getHeight(); break;
/*      */     case 1016:
/*  190 */       value = random(); break;
/*      */     case 1018:
/*      */     case 1023:
/*  191 */       value = getResultsCount(); break;
/*      */     case 1017:
/*  192 */       value = getResult(); break;
/*      */     case 1019:
/*  193 */       value = getNumber(); break;
/*      */     case 1020:
/*  194 */       value = getImageCount(); break;
/*      */     case 1021:
/*  195 */       value = getStackSize(); break;
/*      */     case 1022:
/*  196 */       value = lengthOf(); break;
/*      */     case 1024:
/*  197 */       this.interp.getParens(); value = getImage().getID(); break;
/*      */     case 1025:
/*  198 */       this.interp.getParens(); value = getImage().getBitDepth(); break;
/*      */     case 1026:
/*  199 */       value = getSelectionType(); break;
/*      */     case 1028:
/*  200 */       value = isOpen(); break;
/*      */     case 1029:
/*  201 */       value = isActive(); break;
/*      */     case 1030:
/*  202 */       value = indexOf(); break;
/*      */     case 1031:
/*  203 */       value = getFirstString().lastIndexOf(getLastString()); break;
/*      */     case 1032:
/*  204 */       value = charCodeAt(); break;
/*      */     case 1033:
/*  205 */       value = getBoolean(); break;
/*      */     case 1034:
/*      */     case 1035:
/*  206 */       value = startsWithEndsWith(type); break;
/*      */     case 1037:
/*  207 */       value = Double.isNaN(getArg()) ? 1.0D : 0.0D; break;
/*      */     case 1038:
/*  208 */       value = getZoom(); break;
/*      */     case 1040:
/*  209 */       value = parseDouble(getStringArg()); break;
/*      */     case 1039:
/*  210 */       value = parseInt(); break;
/*      */     case 1041:
/*  211 */       value = isKeyDown(); break;
/*      */     case 1042:
/*  212 */       this.interp.getParens(); value = getImage().getCurrentSlice(); break;
/*      */     case 1043:
/*      */     case 1044:
/*  213 */       value = getScreenDimension(type); break;
/*      */     case 1045:
/*  214 */       value = getImage().getCalibration().getCValue(getArg()); break;
/*      */     case 1048:
/*  215 */       value = roiManager(); break;
/*      */     case 1049:
/*  216 */       this.interp.getParens(); value = Toolbar.getToolId(); break;
/*      */     case 1050:
/*  217 */       value = is(); break;
/*      */     case 1051:
/*  218 */       value = getValue(); break;
/*      */     case 1052:
/*  219 */       value = doStack(); break;
/*      */     case 1053:
/*  220 */       value = matches(); break;
/*      */     case 1054:
/*  221 */       value = getStringWidth(); break;
/*      */     case 1055:
/*  222 */       value = fit(); break;
/*      */     case 1056:
/*  223 */       value = overlay(); break;
/*      */     case 1057:
/*  224 */       value = selectionContains(); break;
/*      */     default:
/*  226 */       this.interp.error("Numeric function expected");
/*      */     }
/*  228 */     return value;
/*      */   }
/*      */ 
/*      */   String getStringFunction(int type)
/*      */   {
/*      */     String str;
/*  233 */     switch (type) { case 2000:
/*  234 */       str = d2s(); break;
/*      */     case 2001:
/*  235 */       str = toString(16); break;
/*      */     case 2002:
/*  236 */       str = toString(2); break;
/*      */     case 2003:
/*  237 */       this.interp.getParens(); str = getImage().getTitle(); break;
/*      */     case 2004:
/*  238 */       str = getStringDialog(); break;
/*      */     case 2005:
/*  239 */       str = substring(); break;
/*      */     case 2006:
/*  240 */       str = fromCharCode(); break;
/*      */     case 2007:
/*  241 */       str = getInfo(); break;
/*      */     case 2010:
/*  242 */       this.interp.getParens(); str = getImageInfo(); break;
/*      */     case 2008:
/*  243 */       str = getDirectory(); break;
/*      */     case 2009:
/*  244 */       this.interp.getParens(); str = this.interp.argument != null ? this.interp.argument : ""; break;
/*      */     case 2011:
/*  245 */       str = getStringArg().toLowerCase(Locale.US); break;
/*      */     case 2012:
/*  246 */       str = getStringArg().toUpperCase(Locale.US); break;
/*      */     case 2013:
/*  247 */       str = runMacro(false); break;
/*      */     case 2014:
/*  248 */       str = runMacro(true); break;
/*      */     case 2015:
/*  249 */       str = doToString(); break;
/*      */     case 2016:
/*  250 */       str = replace(); break;
/*      */     case 2017:
/*  251 */       str = doDialog(); break;
/*      */     case 2018:
/*  252 */       str = getMetadata(); break;
/*      */     case 2019:
/*  253 */       str = doFile(); break;
/*      */     case 2020:
/*  254 */       str = selectionName(); break;
/*      */     case 2021:
/*  255 */       this.interp.getParens(); str = IJ.getVersion(); break;
/*      */     case 2022:
/*  256 */       str = getResultLabel(); break;
/*      */     case 2023:
/*  257 */       str = call(); break;
/*      */     case 2024:
/*  258 */       str = doString(); break;
/*      */     case 2025:
/*  259 */       str = doExt(); break;
/*      */     case 2026:
/*  260 */       str = exec(); break;
/*      */     case 2027:
/*  261 */       str = doList(); break;
/*      */     case 2028:
/*  262 */       str = debug(); break;
/*      */     case 2029:
/*  263 */       str = ijCall(); break;
/*      */     default:
/*  265 */       str = "";
/*  266 */       this.interp.error("String function expected");
/*      */     }
/*  268 */     return str;
/*      */   }
/*      */ 
/*      */   private void setLineWidth(int width) {
/*  272 */     this.lineWidth = width;
/*  273 */     getProcessor().setLineWidth(width);
/*      */   }
/*      */ 
/*      */   Variable[] getArrayFunction(int type)
/*      */   {
/*      */     Variable[] array;
/*  278 */     switch (type) { case 3000:
/*  279 */       array = getProfile(); break;
/*      */     case 3001:
/*  280 */       array = newArray(); break;
/*      */     case 3002:
/*  281 */       array = split(); break;
/*      */     case 3003:
/*  282 */       array = getFileList(); break;
/*      */     case 3004:
/*  283 */       array = getFontList(); break;
/*      */     case 3005:
/*  284 */       array = newMenu(); break;
/*      */     case 3006:
/*  285 */       array = getList(); break;
/*      */     case 3007:
/*  286 */       array = doArray(); break;
/*      */     default:
/*  288 */       array = null;
/*  289 */       this.interp.error("Array function expected");
/*      */     }
/*  291 */     return array;
/*      */   }
/*      */ 
/*      */   final double math(int type) {
/*  295 */     double arg = getArg();
/*  296 */     switch (type) { case 1001:
/*  297 */       return Math.abs(arg);
/*      */     case 1002:
/*  298 */       return Math.cos(arg);
/*      */     case 1003:
/*  299 */       return Math.exp(arg);
/*      */     case 1004:
/*  300 */       return Math.floor(arg);
/*      */     case 1005:
/*  301 */       return Math.log(arg);
/*      */     case 1009:
/*  302 */       return Math.floor(arg + 0.5D);
/*      */     case 1010:
/*  303 */       return Math.sin(arg);
/*      */     case 1011:
/*  304 */       return Math.sqrt(arg);
/*      */     case 1012:
/*  305 */       return Math.tan(arg);
/*      */     case 1027:
/*  306 */       return Math.atan(arg);
/*      */     case 1046:
/*  307 */       return Math.asin(arg);
/*      */     case 1047:
/*  308 */       return Math.acos(arg);
/*      */     case 1006:
/*      */     case 1007:
/*      */     case 1008:
/*      */     case 1013:
/*      */     case 1014:
/*      */     case 1015:
/*      */     case 1016:
/*      */     case 1017:
/*      */     case 1018:
/*      */     case 1019:
/*      */     case 1020:
/*      */     case 1021:
/*      */     case 1022:
/*      */     case 1023:
/*      */     case 1024:
/*      */     case 1025:
/*      */     case 1026:
/*      */     case 1028:
/*      */     case 1029:
/*      */     case 1030:
/*      */     case 1031:
/*      */     case 1032:
/*      */     case 1033:
/*      */     case 1034:
/*      */     case 1035:
/*      */     case 1036:
/*      */     case 1037:
/*      */     case 1038:
/*      */     case 1039:
/*      */     case 1040:
/*      */     case 1041:
/*      */     case 1042:
/*      */     case 1043:
/*      */     case 1044:
/*  309 */     case 1045: } return 0.0D;
/*      */   }
/*      */ 
/*      */   final double math2(int type)
/*      */   {
/*  314 */     double a1 = getFirstArg();
/*  315 */     double a2 = getLastArg();
/*  316 */     switch (type) { case 1007:
/*  317 */       return Math.min(a1, a2);
/*      */     case 1006:
/*  318 */       return Math.max(a1, a2);
/*      */     case 1008:
/*  319 */       return Math.pow(a1, a2);
/*      */     case 1036:
/*  320 */       return Math.atan2(a1, a2); }
/*  321 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   final String getString()
/*      */   {
/*  326 */     String str = this.interp.getStringTerm();
/*      */     while (true) {
/*  328 */       this.interp.getToken();
/*  329 */       if (this.interp.token != 43) break;
/*  330 */       str = str + this.interp.getStringTerm();
/*      */     }
/*  332 */     this.interp.putTokenBack();
/*      */ 
/*  336 */     return str;
/*      */   }
/*      */ 
/*      */   final boolean isStringFunction() {
/*  340 */     Symbol symbol = this.pgm.table[this.interp.tokenAddress];
/*  341 */     return symbol.type == 2000;
/*      */   }
/*      */ 
/*      */   final double getArg() {
/*  345 */     this.interp.getLeftParen();
/*  346 */     double arg = this.interp.getExpression();
/*  347 */     this.interp.getRightParen();
/*  348 */     return arg;
/*      */   }
/*      */ 
/*      */   final double getFirstArg() {
/*  352 */     this.interp.getLeftParen();
/*  353 */     return this.interp.getExpression();
/*      */   }
/*      */ 
/*      */   final double getNextArg() {
/*  357 */     this.interp.getComma();
/*  358 */     return this.interp.getExpression();
/*      */   }
/*      */ 
/*      */   final double getLastArg() {
/*  362 */     this.interp.getComma();
/*  363 */     double arg = this.interp.getExpression();
/*  364 */     this.interp.getRightParen();
/*  365 */     return arg;
/*      */   }
/*      */ 
/*      */   String getStringArg() {
/*  369 */     this.interp.getLeftParen();
/*  370 */     String arg = getString();
/*  371 */     this.interp.getRightParen();
/*  372 */     return arg;
/*      */   }
/*      */ 
/*      */   final String getFirstString() {
/*  376 */     this.interp.getLeftParen();
/*  377 */     return getString();
/*      */   }
/*      */ 
/*      */   final String getNextString() {
/*  381 */     this.interp.getComma();
/*  382 */     return getString();
/*      */   }
/*      */ 
/*      */   final String getLastString() {
/*  386 */     this.interp.getComma();
/*  387 */     String arg = getString();
/*  388 */     this.interp.getRightParen();
/*  389 */     return arg;
/*      */   }
/*      */ 
/*      */   boolean getBooleanArg() {
/*  393 */     this.interp.getLeftParen();
/*  394 */     double arg = this.interp.getBooleanExpression();
/*  395 */     this.interp.checkBoolean(arg);
/*  396 */     this.interp.getRightParen();
/*  397 */     return arg != 0.0D;
/*      */   }
/*      */ 
/*      */   final Variable getVariableArg() {
/*  401 */     this.interp.getLeftParen();
/*  402 */     Variable v = getVariable();
/*  403 */     this.interp.getRightParen();
/*  404 */     return v;
/*      */   }
/*      */ 
/*      */   final Variable getFirstVariable() {
/*  408 */     this.interp.getLeftParen();
/*  409 */     return getVariable();
/*      */   }
/*      */ 
/*      */   final Variable getNextVariable() {
/*  413 */     this.interp.getComma();
/*  414 */     return getVariable();
/*      */   }
/*      */ 
/*      */   final Variable getLastVariable() {
/*  418 */     this.interp.getComma();
/*  419 */     Variable v = getVariable();
/*  420 */     this.interp.getRightParen();
/*  421 */     return v;
/*      */   }
/*      */ 
/*      */   final Variable getVariable() {
/*  425 */     this.interp.getToken();
/*  426 */     if (this.interp.token != 129)
/*  427 */       this.interp.error("Variable expected");
/*  428 */     Variable v = this.interp.lookupLocalVariable(this.interp.tokenAddress);
/*  429 */     if (v == null)
/*  430 */       v = this.interp.push(this.interp.tokenAddress, 0.0D, null, this.interp);
/*  431 */     Variable[] array = v.getArray();
/*  432 */     if (array != null) {
/*  433 */       int index = this.interp.getIndex();
/*  434 */       checkIndex(index, 0, v.getArraySize() - 1);
/*  435 */       v = array[index];
/*      */     }
/*  437 */     return v;
/*      */   }
/*      */ 
/*      */   final Variable getFirstArrayVariable() {
/*  441 */     this.interp.getLeftParen();
/*  442 */     return getArrayVariable();
/*      */   }
/*      */ 
/*      */   final Variable getNextArrayVariable() {
/*  446 */     this.interp.getComma();
/*  447 */     return getArrayVariable();
/*      */   }
/*      */ 
/*      */   final Variable getLastArrayVariable() {
/*  451 */     this.interp.getComma();
/*  452 */     Variable v = getArrayVariable();
/*  453 */     this.interp.getRightParen();
/*  454 */     return v;
/*      */   }
/*      */ 
/*      */   final Variable getArrayVariable() {
/*  458 */     this.interp.getToken();
/*  459 */     if (this.interp.token != 129)
/*  460 */       this.interp.error("Variable expected");
/*  461 */     Variable v = this.interp.lookupLocalVariable(this.interp.tokenAddress);
/*  462 */     if (v == null)
/*  463 */       v = this.interp.push(this.interp.tokenAddress, 0.0D, null, this.interp);
/*  464 */     return v;
/*      */   }
/*      */ 
/*      */   final double[] getFirstArray() {
/*  468 */     this.interp.getLeftParen();
/*  469 */     return getNumericArray();
/*      */   }
/*      */ 
/*      */   final double[] getNextArray() {
/*  473 */     this.interp.getComma();
/*  474 */     return getNumericArray();
/*      */   }
/*      */ 
/*      */   final double[] getLastArray() {
/*  478 */     this.interp.getComma();
/*  479 */     double[] a = getNumericArray();
/*  480 */     this.interp.getRightParen();
/*  481 */     return a;
/*      */   }
/*      */ 
/*      */   double[] getNumericArray() {
/*  485 */     Variable[] a1 = getArray();
/*  486 */     double[] a2 = new double[a1.length];
/*  487 */     for (int i = 0; i < a1.length; i++)
/*  488 */       a2[i] = a1[i].getValue();
/*  489 */     return a2;
/*      */   }
/*      */ 
/*      */   String[] getStringArray() {
/*  493 */     Variable[] a1 = getArray();
/*  494 */     String[] a2 = new String[a1.length];
/*  495 */     for (int i = 0; i < a1.length; i++) {
/*  496 */       String s = a1[i].getString();
/*  497 */       if (s == null) s = "" + a1[i].getValue();
/*  498 */       a2[i] = s;
/*      */     }
/*  500 */     return a2;
/*      */   }
/*      */ 
/*      */   Variable[] getArray() {
/*  504 */     this.interp.getToken();
/*  505 */     boolean newArray = (this.interp.token == 137) && (this.pgm.table[this.interp.tokenAddress].type == 3001);
/*  506 */     if ((this.interp.token != 129) && (!newArray))
/*  507 */       this.interp.error("Array expected");
/*      */     Variable[] a;
/*      */     Variable[] a;
/*  509 */     if (newArray) {
/*  510 */       a = getArrayFunction(3001);
/*      */     } else {
/*  512 */       Variable v = this.interp.lookupVariable();
/*  513 */       a = v.getArray();
/*  514 */       int size = v.getArraySize();
/*  515 */       if ((a != null) && (a.length != size)) {
/*  516 */         Variable[] a2 = new Variable[size];
/*  517 */         for (int i = 0; i < size; i++)
/*  518 */           a2[i] = a[i];
/*  519 */         v.setArray(a2);
/*  520 */         a = v.getArray();
/*      */       }
/*      */     }
/*  523 */     if (a == null)
/*  524 */       this.interp.error("Array expected");
/*  525 */     return a;
/*      */   }
/*      */ 
/*      */   Color getColor() {
/*  529 */     String color = getString();
/*  530 */     color = color.toLowerCase(Locale.US);
/*  531 */     if (color.equals("black"))
/*  532 */       return Color.black;
/*  533 */     if (color.equals("white"))
/*  534 */       return Color.white;
/*  535 */     if (color.equals("red"))
/*  536 */       return Color.red;
/*  537 */     if (color.equals("green"))
/*  538 */       return Color.green;
/*  539 */     if (color.equals("blue"))
/*  540 */       return Color.blue;
/*  541 */     if (color.equals("cyan"))
/*  542 */       return Color.cyan;
/*  543 */     if (color.equals("darkgray"))
/*  544 */       return Color.darkGray;
/*  545 */     if (color.equals("gray"))
/*  546 */       return Color.gray;
/*  547 */     if (color.equals("lightgray"))
/*  548 */       return Color.lightGray;
/*  549 */     if (color.equals("magenta"))
/*  550 */       return Color.magenta;
/*  551 */     if (color.equals("orange"))
/*  552 */       return Color.orange;
/*  553 */     if (color.equals("yellow"))
/*  554 */       return Color.yellow;
/*  555 */     if (color.equals("pink")) {
/*  556 */       return Color.pink;
/*      */     }
/*  558 */     this.interp.error("'red', 'green', etc. expected");
/*  559 */     return null;
/*      */   }
/*      */ 
/*      */   void checkIndex(int index, int lower, int upper) {
/*  563 */     if ((index < lower) || (index > upper))
/*  564 */       this.interp.error("Index (" + index + ") is outside of the " + lower + "-" + upper + " range");
/*      */   }
/*      */ 
/*      */   void doRun() {
/*  568 */     this.interp.getLeftParen();
/*  569 */     String arg1 = getString();
/*  570 */     this.interp.getToken();
/*  571 */     if ((this.interp.token != 41) && (this.interp.token != 44))
/*  572 */       this.interp.error("',' or ')'  expected");
/*  573 */     String arg2 = null;
/*  574 */     if (this.interp.token == 44) {
/*  575 */       arg2 = getString();
/*  576 */       this.interp.getRightParen();
/*      */     }
/*  578 */     if (arg2 != null)
/*  579 */       IJ.run(arg1, arg2);
/*      */     else
/*  581 */       IJ.run(arg1);
/*  582 */     resetImage();
/*  583 */     IJ.setKeyUp(-1);
/*  584 */     this.shiftKeyDown = (this.altKeyDown = 0);
/*      */   }
/*      */ 
/*      */   void setForegroundColor() {
/*  588 */     boolean isImage = WindowManager.getCurrentImage() != null;
/*  589 */     int lineWidth = 0;
/*  590 */     if (isImage)
/*  591 */       lineWidth = getProcessor().getLineWidth();
/*  592 */     IJ.setForegroundColor((int)getFirstArg(), (int)getNextArg(), (int)getLastArg());
/*  593 */     resetImage();
/*  594 */     if (isImage)
/*  595 */       setLineWidth(lineWidth);
/*  596 */     this.defaultColor = null;
/*  597 */     this.defaultValue = (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   void setBackgroundColor() {
/*  601 */     IJ.setBackgroundColor((int)getFirstArg(), (int)getNextArg(), (int)getLastArg());
/*  602 */     resetImage();
/*      */   }
/*      */ 
/*      */   void setColor() {
/*  606 */     this.colorSet = true;
/*  607 */     this.interp.getLeftParen();
/*  608 */     if (isStringArg()) {
/*  609 */       this.defaultColor = getColor();
/*  610 */       getProcessor().setColor(this.defaultColor);
/*  611 */       this.defaultValue = (0.0D / 0.0D);
/*  612 */       this.interp.getRightParen();
/*  613 */       return;
/*      */     }
/*  615 */     double arg1 = this.interp.getExpression();
/*  616 */     if (this.interp.nextToken() == 41) {
/*  617 */       this.interp.getRightParen(); setColor(arg1); return;
/*  618 */     }int red = (int)arg1; int green = (int)getNextArg(); int blue = (int)getLastArg();
/*  619 */     if (red < 0) red = 0; if (green < 0) green = 0; if (blue < 0) blue = 0;
/*  620 */     if (red > 255) red = 255; if (green > 255) green = 255; if (blue > 255) blue = 255;
/*  621 */     this.defaultColor = new Color(red, green, blue);
/*  622 */     getProcessor().setColor(this.defaultColor);
/*  623 */     this.defaultValue = (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   void setColor(double value) {
/*  627 */     ImageProcessor ip = getProcessor();
/*  628 */     ImagePlus imp = getImage();
/*  629 */     switch (imp.getBitDepth()) {
/*      */     case 8:
/*  631 */       if ((value < 0.0D) || (value > 255.0D))
/*  632 */         this.interp.error("Argument out of 8-bit range (0-255)");
/*  633 */       ip.setValue(value);
/*  634 */       break;
/*      */     case 16:
/*  636 */       if (imp.getLocalCalibration().isSigned16Bit())
/*  637 */         value += 32768.0D;
/*  638 */       if ((value < 0.0D) || (value > 65535.0D))
/*  639 */         this.interp.error("Argument out of 16-bit range (0-65535)");
/*  640 */       ip.setValue(value);
/*  641 */       break;
/*      */     default:
/*  643 */       ip.setValue(value);
/*      */     }
/*      */ 
/*  646 */     this.defaultValue = value;
/*  647 */     this.defaultColor = null;
/*      */   }
/*      */ 
/*      */   void makeLine() {
/*  651 */     double x1d = getFirstArg();
/*  652 */     double y1d = getNextArg();
/*  653 */     double x2d = getNextArg();
/*  654 */     this.interp.getComma();
/*  655 */     double y2d = this.interp.getExpression();
/*  656 */     this.interp.getToken();
/*  657 */     if (this.interp.token == 41) {
/*  658 */       IJ.makeLine(x1d, y1d, x2d, y2d);
/*      */     } else {
/*  660 */       int x1 = (int)Math.round(x1d);
/*  661 */       int y1 = (int)Math.round(y1d);
/*  662 */       int x2 = (int)Math.round(x2d);
/*  663 */       int y2 = (int)Math.round(y2d);
/*  664 */       int max = 200;
/*  665 */       int[] x = new int[max];
/*  666 */       int[] y = new int[max];
/*  667 */       x[0] = x1; y[0] = y1; x[1] = x2; y[1] = y2;
/*  668 */       int n = 2;
/*  669 */       while ((this.interp.token == 44) && (n < max)) {
/*  670 */         x[n] = ((int)Math.round(this.interp.getExpression()));
/*  671 */         if ((n == 2) && (this.interp.nextToken() == 41)) {
/*  672 */           this.interp.getRightParen();
/*  673 */           Roi line = new Line(x1, y1, x2, y2);
/*  674 */           line.updateWideLine(x[n]);
/*  675 */           getImage().setRoi(line);
/*  676 */           return;
/*      */         }
/*  678 */         this.interp.getComma();
/*  679 */         y[n] = ((int)Math.round(this.interp.getExpression()));
/*  680 */         this.interp.getToken();
/*  681 */         n++;
/*      */       }
/*  683 */       if ((n == max) && (this.interp.token != 41))
/*  684 */         this.interp.error("More than " + max + " points");
/*  685 */       getImage().setRoi(new PolygonRoi(x, y, n, 6));
/*      */     }
/*  687 */     resetImage();
/*      */   }
/*      */ 
/*      */   void makeOval() {
/*  691 */     Roi previousRoi = getImage().getRoi();
/*  692 */     if ((this.shiftKeyDown) || (this.altKeyDown)) getImage().saveRoi();
/*  693 */     IJ.makeOval((int)Math.round(getFirstArg()), (int)Math.round(getNextArg()), (int)Math.round(getNextArg()), (int)Math.round(getLastArg()));
/*      */ 
/*  695 */     Roi roi = getImage().getRoi();
/*  696 */     if ((previousRoi != null) && (roi != null))
/*  697 */       updateRoi(roi);
/*  698 */     resetImage();
/*      */   }
/*      */ 
/*      */   void makeRectangle() {
/*  702 */     Roi previousRoi = getImage().getRoi();
/*  703 */     if ((this.shiftKeyDown) || (this.altKeyDown)) getImage().saveRoi();
/*  704 */     int x = (int)Math.round(getFirstArg());
/*  705 */     int y = (int)Math.round(getNextArg());
/*  706 */     int w = (int)Math.round(getNextArg());
/*  707 */     int h = (int)Math.round(getNextArg());
/*  708 */     int arcSize = 0;
/*  709 */     if (this.interp.nextToken() == 44) {
/*  710 */       this.interp.getComma();
/*  711 */       arcSize = (int)this.interp.getExpression();
/*      */     }
/*  713 */     this.interp.getRightParen();
/*  714 */     if (arcSize < 1) {
/*  715 */       IJ.makeRectangle(x, y, w, h);
/*      */     } else {
/*  717 */       ImagePlus imp = getImage();
/*  718 */       imp.setRoi(new Roi(x, y, w, h, arcSize));
/*      */     }
/*  720 */     Roi roi = getImage().getRoi();
/*  721 */     if ((previousRoi != null) && (roi != null))
/*  722 */       updateRoi(roi);
/*  723 */     resetImage();
/*      */   }
/*      */ 
/*      */   ImagePlus getImage() {
/*  727 */     ImagePlus imp = IJ.getImage();
/*  728 */     if ((imp.getWindow() == null) && (IJ.getInstance() != null) && (!Interpreter.isBatchMode()) && (WindowManager.getTempCurrentImage() == null))
/*  729 */       throw new RuntimeException("Macro canceled");
/*  730 */     this.defaultIP = null;
/*  731 */     return imp;
/*      */   }
/*      */ 
/*      */   void resetImage() {
/*  735 */     this.defaultIP = null;
/*  736 */     this.colorSet = (this.fontSet = 0);
/*  737 */     this.lineWidth = 1;
/*      */   }
/*      */ 
/*      */   ImageProcessor getProcessor() {
/*  741 */     if (this.defaultIP == null) {
/*  742 */       this.defaultIP = getImage().getProcessor();
/*  743 */       if (this.lineWidth != 1)
/*  744 */         this.defaultIP.setLineWidth(this.lineWidth);
/*      */     }
/*  746 */     return this.defaultIP;
/*      */   }
/*      */ 
/*      */   int getType() {
/*  750 */     this.imageType = getImage().getType();
/*  751 */     return this.imageType;
/*      */   }
/*      */ 
/*      */   void setPixel() {
/*  755 */     this.interp.getLeftParen();
/*  756 */     int a1 = (int)this.interp.getExpression();
/*  757 */     this.interp.getComma();
/*  758 */     double a2 = this.interp.getExpression();
/*  759 */     this.interp.getToken();
/*  760 */     if (this.interp.token == 44) {
/*  761 */       double a3 = this.interp.getExpression();
/*  762 */       this.interp.getRightParen();
/*  763 */       if (getType() == 2)
/*  764 */         getProcessor().putPixelValue(a1, (int)a2, a3);
/*      */       else
/*  766 */         getProcessor().putPixel(a1, (int)a2, (int)a3);
/*      */     } else {
/*  768 */       if (this.interp.token != 41) this.interp.error("')' expected");
/*  769 */       getProcessor().setf(a1, (float)a2);
/*      */     }
/*  771 */     this.updateNeeded = true;
/*      */   }
/*      */ 
/*      */   double getPixel() {
/*  775 */     this.interp.getLeftParen();
/*  776 */     double a1 = this.interp.getExpression();
/*  777 */     ImageProcessor ip = getProcessor();
/*  778 */     double value = 0.0D;
/*  779 */     this.interp.getToken();
/*  780 */     if (this.interp.token == 44) {
/*  781 */       double a2 = this.interp.getExpression();
/*  782 */       this.interp.getRightParen();
/*  783 */       int ia1 = (int)a1;
/*  784 */       int ia2 = (int)a2;
/*  785 */       if ((a1 == ia1) && (a2 == ia2)) {
/*  786 */         if (getType() == 2)
/*  787 */           value = ip.getPixelValue(ia1, ia2);
/*      */         else
/*  789 */           value = ip.getPixel(ia1, ia2);
/*      */       }
/*  791 */       else if (getType() == 4)
/*  792 */         value = ip.getPixelInterpolated(a1, a2);
/*      */       else
/*  794 */         value = ip.getInterpolatedValue(a1, a2);
/*      */     }
/*      */     else {
/*  797 */       if (this.interp.token != 41) this.interp.error("')' expected");
/*  798 */       value = ip.getf((int)a1);
/*      */     }
/*  800 */     return value;
/*      */   }
/*      */ 
/*      */   void setZCoordinate() {
/*  804 */     int z = (int)getArg();
/*  805 */     ImagePlus imp = getImage();
/*  806 */     ImageStack stack = imp.getStack();
/*  807 */     int size = stack.getSize();
/*  808 */     if ((z < 0) || (z >= size))
/*  809 */       this.interp.error("Z coordinate (" + z + ") is out of 0-" + (size - 1) + " range");
/*  810 */     this.defaultIP = stack.getProcessor(z + 1);
/*      */   }
/*      */ 
/*      */   void moveTo() {
/*  814 */     this.interp.getLeftParen();
/*  815 */     int a1 = (int)Math.round(this.interp.getExpression());
/*  816 */     this.interp.getComma();
/*  817 */     int a2 = (int)Math.round(this.interp.getExpression());
/*  818 */     this.interp.getRightParen();
/*  819 */     getProcessor().moveTo(a1, a2);
/*      */   }
/*      */ 
/*      */   void lineTo() {
/*  823 */     this.interp.getLeftParen();
/*  824 */     int a1 = (int)Math.round(this.interp.getExpression());
/*  825 */     this.interp.getComma();
/*  826 */     int a2 = (int)Math.round(this.interp.getExpression());
/*  827 */     this.interp.getRightParen();
/*  828 */     ImageProcessor ip = getProcessor();
/*  829 */     if (!this.colorSet) setForegroundColor(ip);
/*  830 */     ip.lineTo(a1, a2);
/*  831 */     updateAndDraw();
/*      */   }
/*      */ 
/*      */   void drawLine() {
/*  835 */     this.interp.getLeftParen();
/*  836 */     int x1 = (int)Math.round(this.interp.getExpression());
/*  837 */     this.interp.getComma();
/*  838 */     int y1 = (int)Math.round(this.interp.getExpression());
/*  839 */     this.interp.getComma();
/*  840 */     int x2 = (int)Math.round(this.interp.getExpression());
/*  841 */     this.interp.getComma();
/*  842 */     int y2 = (int)Math.round(this.interp.getExpression());
/*  843 */     this.interp.getRightParen();
/*  844 */     ImageProcessor ip = getProcessor();
/*  845 */     if (!this.colorSet) setForegroundColor(ip);
/*  846 */     ip.drawLine(x1, y1, x2, y2);
/*  847 */     updateAndDraw();
/*      */   }
/*      */ 
/*      */   void setForegroundColor(ImageProcessor ip) {
/*  851 */     if (this.defaultColor != null)
/*  852 */       ip.setColor(this.defaultColor);
/*  853 */     else if (!Double.isNaN(this.defaultValue))
/*  854 */       ip.setValue(this.defaultValue);
/*      */     else
/*  856 */       ip.setColor(Toolbar.getForegroundColor());
/*  857 */     this.colorSet = true;
/*      */   }
/*      */ 
/*      */   void doIPMethod(int type) {
/*  861 */     this.interp.getParens();
/*  862 */     ImageProcessor ip = getProcessor();
/*  863 */     switch (type) { case 333:
/*  864 */       ip.snapshot(); break;
/*      */     case 334:
/*  866 */       ip.reset();
/*  867 */       this.updateNeeded = true;
/*  868 */       break;
/*      */     case 335:
/*  870 */       ImagePlus imp = getImage();
/*  871 */       Roi roi = imp.getRoi();
/*  872 */       if (!this.colorSet) setForegroundColor(ip);
/*  873 */       if (roi == null) {
/*  874 */         ip.resetRoi();
/*  875 */         ip.fill();
/*      */       } else {
/*  877 */         ip.setRoi(roi);
/*  878 */         ip.fill(ip.getMask());
/*      */       }
/*  880 */       imp.updateAndDraw();
/*      */     }
/*      */   }
/*      */ 
/*      */   void updateAndDraw()
/*      */   {
/*  886 */     if (this.autoUpdate) {
/*  887 */       ImagePlus imp = getImage();
/*  888 */       imp.updateChannelAndDraw();
/*  889 */       imp.changes = true;
/*      */     } else {
/*  891 */       this.updateNeeded = true;
/*      */     }
/*      */   }
/*      */ 
/*  895 */   void updateDisplay() { if ((this.updateNeeded) && (WindowManager.getImageCount() > 0)) {
/*  896 */       ImagePlus imp = getImage();
/*  897 */       imp.updateAndDraw();
/*  898 */       this.updateNeeded = false;
/*      */     } }
/*      */ 
/*      */   void drawString()
/*      */   {
/*  903 */     this.interp.getLeftParen();
/*  904 */     String str = getString();
/*  905 */     this.interp.getComma();
/*  906 */     int x = (int)(this.interp.getExpression() + 0.5D);
/*  907 */     this.interp.getComma();
/*  908 */     int y = (int)(this.interp.getExpression() + 0.5D);
/*  909 */     Color background = null;
/*  910 */     if (this.interp.nextToken() == 44) {
/*  911 */       this.interp.getComma();
/*  912 */       background = getColor();
/*      */     }
/*  914 */     this.interp.getRightParen();
/*  915 */     ImageProcessor ip = getProcessor();
/*  916 */     if (!this.colorSet)
/*  917 */       setForegroundColor(ip);
/*  918 */     setFont(ip);
/*  919 */     ip.setJustification(this.justification);
/*  920 */     ip.setAntialiasedText(this.antialiasedText);
/*  921 */     if (background != null)
/*  922 */       ip.drawString(str, x, y, background);
/*      */     else
/*  924 */       ip.drawString(str, x, y);
/*  925 */     updateAndDraw();
/*      */   }
/*      */ 
/*      */   void setFont(ImageProcessor ip) {
/*  929 */     if ((this.font != null) && (!this.fontSet))
/*  930 */       ip.setFont(this.font);
/*  931 */     this.fontSet = true;
/*      */   }
/*      */ 
/*      */   void setJustification() {
/*  935 */     String str = getStringArg().toLowerCase(Locale.US);
/*  936 */     int just = 0;
/*  937 */     if (str.equals("center"))
/*  938 */       just = 1;
/*  939 */     else if (str.equals("right"))
/*  940 */       just = 2;
/*  941 */     this.justification = just;
/*      */   }
/*      */ 
/*      */   void changeValues() {
/*  945 */     double darg1 = getFirstArg();
/*  946 */     double darg2 = getNextArg();
/*  947 */     double darg3 = getLastArg();
/*  948 */     ImagePlus imp = getImage();
/*  949 */     ImageProcessor ip = getProcessor();
/*  950 */     Roi roi = imp.getRoi();
/*  951 */     ImageProcessor mask = null;
/*  952 */     if ((roi == null) || (!roi.isArea())) {
/*  953 */       ip.resetRoi();
/*  954 */       roi = null;
/*      */     } else {
/*  956 */       ip.setRoi(roi);
/*  957 */       mask = ip.getMask();
/*  958 */       if (mask != null) ip.snapshot();
/*      */     }
/*  960 */     int xmin = 0; int ymin = 0; int xmax = imp.getWidth(); int ymax = imp.getHeight();
/*  961 */     if (roi != null) {
/*  962 */       Rectangle r = roi.getBounds();
/*  963 */       xmin = r.x; ymin = r.y; xmax = r.x + r.width; ymax = r.y + r.height;
/*      */     }
/*  965 */     boolean isFloat = getType() == 2;
/*  966 */     if (imp.getBitDepth() == 24) {
/*  967 */       darg1 = (int)darg1 & 0xFFFFFF;
/*  968 */       darg2 = (int)darg2 & 0xFFFFFF;
/*      */     }
/*      */ 
/*  971 */     for (int y = ymin; y < ymax; y++) {
/*  972 */       for (int x = xmin; x < xmax; x++) {
/*  973 */         double v = isFloat ? ip.getPixelValue(x, y) : ip.getPixel(x, y) & 0xFFFFFF;
/*  974 */         if ((v >= darg1) && (v <= darg2)) {
/*  975 */           if (isFloat)
/*  976 */             ip.putPixelValue(x, y, darg3);
/*      */           else
/*  978 */             ip.putPixel(x, y, (int)darg3);
/*      */         }
/*      */       }
/*      */     }
/*  982 */     if (mask != null) ip.reset(mask);
/*  983 */     if ((imp.getType() == 1) || (imp.getType() == 2))
/*  984 */       ip.resetMinAndMax();
/*  985 */     imp.updateAndDraw();
/*  986 */     this.updateNeeded = false;
/*      */   }
/*      */ 
/*      */   void requires() {
/*  990 */     if (IJ.versionLessThan(getStringArg()))
/*  991 */       this.interp.done = true;
/*      */   }
/*      */ 
/*      */   double random()
/*      */   {
/*  996 */     double dseed = (0.0D / 0.0D);
/*  997 */     if (this.interp.nextToken() == 40) {
/*  998 */       this.interp.getLeftParen();
/*  999 */       if (isStringArg()) {
/* 1000 */         String arg = getString().toLowerCase(Locale.US);
/* 1001 */         if (arg.indexOf("seed") == -1)
/* 1002 */           this.interp.error("'seed' expected");
/* 1003 */         this.interp.getComma();
/* 1004 */         dseed = this.interp.getExpression();
/* 1005 */         long seed = ()dseed;
/* 1006 */         if (seed != dseed)
/* 1007 */           this.interp.error("Seed not integer");
/* 1008 */         this.ran = new Random(seed);
/*      */       }
/* 1010 */       this.interp.getRightParen();
/* 1011 */       if (!Double.isNaN(dseed)) return (0.0D / 0.0D);
/*      */     }
/* 1013 */     this.interp.getParens();
/* 1014 */     if (this.ran == null)
/* 1015 */       this.ran = new Random();
/* 1016 */     return this.ran.nextDouble();
/*      */   }
/*      */ 
/*      */   double getResult()
/*      */   {
/* 1028 */     this.interp.getLeftParen();
/* 1029 */     String column = getString();
/* 1030 */     int row = -1;
/* 1031 */     if (this.interp.nextToken() == 44) {
/* 1032 */       this.interp.getComma();
/* 1033 */       row = (int)this.interp.getExpression();
/*      */     }
/* 1035 */     this.interp.getRightParen();
/* 1036 */     ResultsTable rt = Analyzer.getResultsTable();
/* 1037 */     int counter = rt.getCounter();
/* 1038 */     if (counter == 0)
/* 1039 */       this.interp.error("\"Results\" table empty");
/* 1040 */     if (row == -1) row = counter - 1;
/* 1041 */     if ((row < 0) || (row >= counter))
/* 1042 */       this.interp.error("Row (" + row + ") out of range");
/* 1043 */     int col = rt.getColumnIndex(column);
/* 1044 */     if (!rt.columnExists(col)) {
/* 1045 */       return (0.0D / 0.0D);
/*      */     }
/* 1047 */     return rt.getValueAsDouble(col, row);
/*      */   }
/*      */ 
/*      */   String getResultLabel() {
/* 1051 */     int row = (int)getArg();
/* 1052 */     ResultsTable rt = Analyzer.getResultsTable();
/* 1053 */     int counter = rt.getCounter();
/* 1054 */     if (counter == 0)
/* 1055 */       this.interp.error("\"Results\" table empty");
/* 1056 */     if ((row < 0) || (row >= counter))
/* 1057 */       this.interp.error("Row (" + row + ") out of range");
/* 1058 */     String label = rt.getLabel(row);
/* 1059 */     return label != null ? label : "";
/*      */   }
/*      */ 
/*      */   void setResult() {
/* 1063 */     this.interp.getLeftParen();
/* 1064 */     String column = getString();
/* 1065 */     this.interp.getComma();
/* 1066 */     int row = (int)this.interp.getExpression();
/* 1067 */     this.interp.getComma();
/* 1068 */     double value = 0.0D;
/* 1069 */     String label = null;
/* 1070 */     if (column.equals("Label"))
/* 1071 */       label = getString();
/*      */     else
/* 1073 */       value = this.interp.getExpression();
/* 1074 */     this.interp.getRightParen();
/* 1075 */     ResultsTable rt = Analyzer.getResultsTable();
/* 1076 */     if ((row < 0) || (row > rt.getCounter()))
/* 1077 */       this.interp.error("Row (" + row + ") out of range");
/* 1078 */     if (row == rt.getCounter())
/* 1079 */       rt.incrementCounter();
/*      */     try {
/* 1081 */       if (label != null)
/* 1082 */         rt.setLabel(label, row);
/*      */       else
/* 1084 */         rt.setValue(column, row, value);
/* 1085 */       this.resultsPending = true;
/*      */     } catch (Exception e) {
/* 1087 */       this.interp.error("" + e.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   void updateResults() {
/* 1092 */     this.interp.getParens();
/* 1093 */     ResultsTable rt = Analyzer.getResultsTable();
/* 1094 */     rt.show("Results");
/* 1095 */     this.resultsPending = false;
/*      */   }
/*      */ 
/*      */   double getNumber() {
/* 1099 */     String prompt = getFirstString();
/* 1100 */     double defaultValue = getLastArg();
/* 1101 */     String title = this.interp.macroName != null ? this.interp.macroName : "";
/* 1102 */     if (title.endsWith(" Options"))
/* 1103 */       title = title.substring(0, title.length() - 8);
/* 1104 */     GenericDialog gd = new GenericDialog(title);
/* 1105 */     int decimalPlaces = (int)defaultValue == defaultValue ? 0 : 2;
/* 1106 */     gd.addNumericField(prompt, defaultValue, decimalPlaces);
/* 1107 */     gd.showDialog();
/* 1108 */     if (gd.wasCanceled()) {
/* 1109 */       this.interp.done = true;
/* 1110 */       return defaultValue;
/*      */     }
/* 1112 */     double v = gd.getNextNumber();
/* 1113 */     if (gd.invalidNumber()) {
/* 1114 */       return defaultValue;
/*      */     }
/* 1116 */     return v;
/*      */   }
/*      */ 
/*      */   double getBoolean() {
/* 1120 */     String prompt = getStringArg();
/* 1121 */     String title = this.interp.macroName != null ? this.interp.macroName : "";
/* 1122 */     if (title.endsWith(" Options"))
/* 1123 */       title = title.substring(0, title.length() - 8);
/* 1124 */     YesNoCancelDialog d = new YesNoCancelDialog(IJ.getInstance(), title, prompt);
/* 1125 */     if (d.cancelPressed()) {
/* 1126 */       this.interp.done = true;
/* 1127 */       return 0.0D;
/* 1128 */     }if (d.yesPressed()) {
/* 1129 */       return 1.0D;
/*      */     }
/* 1131 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   double getBoolean2() {
/* 1135 */     String prompt = getFirstString();
/* 1136 */     this.interp.getComma();
/* 1137 */     double defaultValue = this.interp.getBooleanExpression();
/* 1138 */     this.interp.checkBoolean(defaultValue);
/* 1139 */     this.interp.getRightParen();
/* 1140 */     String title = this.interp.macroName != null ? this.interp.macroName : "";
/* 1141 */     if (title.endsWith(" Options"))
/* 1142 */       title = title.substring(0, title.length() - 8);
/* 1143 */     GenericDialog gd = new GenericDialog(title);
/* 1144 */     gd.addCheckbox(prompt, defaultValue == 1.0D);
/* 1145 */     gd.showDialog();
/* 1146 */     if (gd.wasCanceled()) {
/* 1147 */       this.interp.done = true;
/* 1148 */       return 0.0D;
/*      */     }
/* 1150 */     return gd.getNextBoolean() ? 1.0D : 0.0D;
/*      */   }
/*      */ 
/*      */   String getStringDialog() {
/* 1154 */     this.interp.getLeftParen();
/* 1155 */     String prompt = getString();
/* 1156 */     this.interp.getComma();
/* 1157 */     String defaultStr = getString();
/* 1158 */     this.interp.getRightParen();
/*      */ 
/* 1160 */     String title = this.interp.macroName != null ? this.interp.macroName : "";
/* 1161 */     if (title.endsWith(" Options"))
/* 1162 */       title = title.substring(0, title.length() - 8);
/* 1163 */     GenericDialog gd = new GenericDialog(title);
/* 1164 */     gd.addStringField(prompt, defaultStr, 20);
/* 1165 */     gd.showDialog();
/* 1166 */     String str = "";
/* 1167 */     if (gd.wasCanceled())
/* 1168 */       this.interp.done = true;
/*      */     else
/* 1170 */       str = gd.getNextString();
/* 1171 */     return str;
/*      */   }
/*      */ 
/*      */   String d2s() {
/* 1175 */     return IJ.d2s(getFirstArg(), (int)getLastArg());
/*      */   }
/*      */ 
/*      */   String toString(int base) {
/* 1179 */     int arg = (int)getArg();
/* 1180 */     if (base == 2) {
/* 1181 */       return Integer.toBinaryString(arg);
/*      */     }
/* 1183 */     return Integer.toHexString(arg);
/*      */   }
/*      */ 
/*      */   double getStackSize() {
/* 1187 */     this.interp.getParens();
/* 1188 */     return getImage().getStackSize();
/*      */   }
/*      */ 
/*      */   double getImageCount() {
/* 1192 */     this.interp.getParens();
/* 1193 */     return WindowManager.getImageCount();
/*      */   }
/*      */ 
/*      */   double getResultsCount() {
/* 1197 */     this.interp.getParens();
/* 1198 */     return Analyzer.getResultsTable().getCounter();
/*      */   }
/*      */ 
/*      */   void getCoordinates() {
/* 1202 */     Variable xCoordinates = getFirstArrayVariable();
/* 1203 */     Variable yCoordinates = getLastArrayVariable();
/* 1204 */     ImagePlus imp = getImage();
/* 1205 */     Roi roi = imp.getRoi();
/* 1206 */     if (roi == null)
/* 1207 */       this.interp.error("Selection required");
/*      */     Variable[] xa;
/*      */     Variable[] ya;
/* 1209 */     if (roi.getType() == 5) {
/* 1210 */       Variable[] xa = new Variable[2];
/* 1211 */       Variable[] ya = new Variable[2];
/* 1212 */       Line line = (Line)roi;
/* 1213 */       xa[0] = new Variable(line.x1d);
/* 1214 */       ya[0] = new Variable(line.y1d);
/* 1215 */       xa[1] = new Variable(line.x2d);
/* 1216 */       ya[1] = new Variable(line.y2d);
/*      */     } else {
/* 1218 */       FloatPolygon fp = roi.getFloatPolygon();
/* 1219 */       if (fp != null) {
/* 1220 */         Variable[] xa = new Variable[fp.npoints];
/* 1221 */         Variable[] ya = new Variable[fp.npoints];
/* 1222 */         for (int i = 0; i < fp.npoints; i++)
/* 1223 */           xa[i] = new Variable(fp.xpoints[i]);
/* 1224 */         for (int i = 0; i < fp.npoints; i++)
/* 1225 */           ya[i] = new Variable(fp.ypoints[i]);
/*      */       } else {
/* 1227 */         Polygon p = roi.getPolygon();
/* 1228 */         xa = new Variable[p.npoints];
/* 1229 */         ya = new Variable[p.npoints];
/* 1230 */         for (int i = 0; i < p.npoints; i++)
/* 1231 */           xa[i] = new Variable(p.xpoints[i]);
/* 1232 */         for (int i = 0; i < p.npoints; i++)
/* 1233 */           ya[i] = new Variable(p.ypoints[i]);
/*      */       }
/*      */     }
/* 1236 */     xCoordinates.setArray(xa);
/* 1237 */     yCoordinates.setArray(ya);
/*      */   }
/*      */ 
/*      */   Variable[] getProfile() {
/* 1241 */     this.interp.getParens();
/* 1242 */     ImagePlus imp = getImage();
/* 1243 */     if (imp.getRoi() == null)
/* 1244 */       this.interp.error("Selection required");
/* 1245 */     ProfilePlot pp = new ProfilePlot(imp, IJ.altKeyDown());
/* 1246 */     double[] array = pp.getProfile();
/* 1247 */     if (array == null) {
/* 1248 */       this.interp.done = true; return null;
/*      */     }
/* 1250 */     return new Variable(array).getArray();
/*      */   }
/*      */ 
/*      */   Variable[] newArray() {
/* 1254 */     if ((this.interp.nextToken() != 40) || (this.interp.nextNextToken() == 41)) {
/* 1255 */       this.interp.getParens();
/* 1256 */       return new Variable[0];
/*      */     }
/* 1258 */     this.interp.getLeftParen();
/* 1259 */     int next = this.interp.nextToken();
/* 1260 */     int nextNext = this.interp.nextNextToken();
/* 1261 */     if ((next == 133) || (nextNext == 44) || (nextNext == 91) || (next == 45) || (next == 211))
/*      */     {
/* 1263 */       return initNewArray();
/* 1264 */     }int size = (int)this.interp.getExpression();
/* 1265 */     if (size < 0) this.interp.error("Negative array size");
/* 1266 */     this.interp.getRightParen();
/* 1267 */     Variable[] array = new Variable[size];
/* 1268 */     for (int i = 0; i < size; i++)
/* 1269 */       array[i] = new Variable();
/* 1270 */     return array;
/*      */   }
/*      */ 
/*      */   Variable[] split() {
/* 1274 */     String s1 = getFirstString();
/* 1275 */     String s2 = null;
/* 1276 */     if (this.interp.nextToken() == 41)
/* 1277 */       this.interp.getRightParen();
/*      */     else
/* 1279 */       s2 = getLastString();
/* 1280 */     if (s1 == null) return null;
/* 1281 */     String[] strings = (s2 == null) || (s2.equals("")) ? Tools.split(s1) : Tools.split(s1, s2);
/* 1282 */     Variable[] array = new Variable[strings.length];
/* 1283 */     for (int i = 0; i < strings.length; i++)
/* 1284 */       array[i] = new Variable(0, 0.0D, strings[i]);
/* 1285 */     return array;
/*      */   }
/*      */ 
/*      */   Variable[] getFileList() {
/* 1289 */     String dir = getStringArg();
/* 1290 */     File f = new File(dir);
/* 1291 */     if ((!f.exists()) || (!f.isDirectory()))
/* 1292 */       return new Variable[0];
/* 1293 */     String[] list = f.list();
/* 1294 */     if (list == null)
/* 1295 */       return new Variable[0];
/* 1296 */     if (System.getProperty("os.name").indexOf("Linux") != -1) {
/* 1297 */       StringSorter.sort(list);
/*      */     }
/* 1299 */     int hidden = 0;
/* 1300 */     for (int i = 0; i < list.length; i++) {
/* 1301 */       if ((list[i].startsWith(".")) || (list[i].equals("Thumbs.db"))) {
/* 1302 */         list[i] = null;
/* 1303 */         hidden++;
/*      */       } else {
/* 1305 */         File f2 = new File(dir, list[i]);
/* 1306 */         if (f2.isDirectory())
/* 1307 */           list[i] = (list[i] + "/");
/*      */       }
/*      */     }
/* 1310 */     int n = list.length - hidden;
/* 1311 */     if (n <= 0)
/* 1312 */       return new Variable[0];
/* 1313 */     if (hidden > 0) {
/* 1314 */       String[] list2 = new String[n];
/* 1315 */       int j = 0;
/* 1316 */       for (int i = 0; i < list.length; i++) {
/* 1317 */         if (list[i] != null)
/* 1318 */           list2[(j++)] = list[i];
/*      */       }
/* 1320 */       list = list2;
/*      */     }
/* 1322 */     Variable[] array = new Variable[n];
/* 1323 */     for (int i = 0; i < n; i++)
/* 1324 */       array[i] = new Variable(0, 0.0D, list[i]);
/* 1325 */     return array;
/*      */   }
/*      */ 
/*      */   Variable[] initNewArray() {
/* 1329 */     Vector vector = new Vector();
/* 1330 */     int size = 0;
/*      */     do {
/* 1332 */       Variable v = new Variable();
/* 1333 */       if (isStringArg())
/* 1334 */         v.setString(getString());
/*      */       else
/* 1336 */         v.setValue(this.interp.getExpression());
/* 1337 */       vector.addElement(v);
/* 1338 */       size++;
/* 1339 */       this.interp.getToken();
/* 1340 */     }while (this.interp.token == 44);
/* 1341 */     if (this.interp.token != 41)
/* 1342 */       this.interp.error("';' expected");
/* 1343 */     Variable[] array = new Variable[size];
/* 1344 */     vector.copyInto((Variable[])array);
/* 1345 */     if ((array.length == 1) && (array[0].getString() == null)) {
/* 1346 */       size = (int)array[0].getValue();
/* 1347 */       if (size < 0) this.interp.error("Negative array size");
/* 1348 */       Variable[] array2 = new Variable[size];
/* 1349 */       for (int i = 0; i < size; i++)
/* 1350 */         array2[i] = new Variable();
/* 1351 */       return array2;
/*      */     }
/* 1353 */     return array;
/*      */   }
/*      */ 
/*      */   String fromCharCode() {
/* 1357 */     char[] chars = new char[100];
/* 1358 */     int count = 0;
/* 1359 */     this.interp.getLeftParen();
/* 1360 */     while (this.interp.nextToken() != 41) {
/* 1361 */       int value = (int)this.interp.getExpression();
/* 1362 */       if ((value < 0) || (value > 65535))
/* 1363 */         this.interp.error("Value (" + value + ") out of 0-65535 range");
/* 1364 */       chars[(count++)] = ((char)value);
/* 1365 */       if (this.interp.nextToken() == 44)
/* 1366 */         this.interp.getToken();
/*      */     }
/* 1368 */     this.interp.getRightParen();
/* 1369 */     return new String(chars, 0, count);
/*      */   }
/*      */ 
/*      */   String getInfo() {
/* 1373 */     if ((this.interp.nextNextToken() == 133) || ((this.interp.nextToken() == 40) && (this.interp.nextNextToken() != 41)))
/*      */     {
/* 1375 */       return getInfo(getStringArg());
/*      */     }
/* 1377 */     this.interp.getParens();
/* 1378 */     return getWindowContents();
/*      */   }
/*      */ 
/*      */   String getInfo(String key)
/*      */   {
/* 1383 */     int len = key.length();
/* 1384 */     if ((len == 9) && (key.charAt(4) == ',')) {
/* 1385 */       String tag = DicomTools.getTag(getImage(), key);
/* 1386 */       return tag != null ? tag : "";
/* 1387 */     }if (key.equals("overlay")) {
/* 1388 */       Overlay overlay = getImage().getOverlay();
/* 1389 */       if (overlay == null) {
/* 1390 */         return "";
/*      */       }
/* 1392 */       return overlay.toString();
/* 1393 */     }if ((key.equals("log")) || (key.equals("Log"))) {
/* 1394 */       String log = IJ.getLog();
/* 1395 */       return log != null ? log : "";
/* 1396 */     }if (key.indexOf(".") == -1) {
/* 1397 */       String value = getMetadataValue(key);
/* 1398 */       if (value != null) return value; 
/*      */     } else { if (key.equals("micrometer.abbreviation"))
/* 1400 */         return "µm";
/* 1401 */       if (key.equals("image.subtitle")) {
/* 1402 */         ImagePlus imp = getImage();
/* 1403 */         ImageWindow win = imp.getWindow();
/* 1404 */         return win != null ? win.createSubtitle() : "";
/* 1405 */       }if (key.equals("slice.label")) {
/* 1406 */         ImagePlus imp = getImage();
/* 1407 */         if (imp.getStackSize() == 1) return "";
/* 1408 */         String label = imp.getStack().getShortSliceLabel(imp.getCurrentSlice());
/* 1409 */         return label != null ? label : "";
/* 1410 */       }if (key.equals("window.contents"))
/* 1411 */         return getWindowContents();
/* 1412 */       if (key.equals("image.description")) {
/* 1413 */         String description = "";
/* 1414 */         FileInfo fi = getImage().getOriginalFileInfo();
/* 1415 */         if (fi != null) description = fi.description;
/* 1416 */         if (description == null) description = "";
/* 1417 */         return description;
/* 1418 */       }if (key.equals("image.filename")) {
/* 1419 */         String name = "";
/* 1420 */         FileInfo fi = getImage().getOriginalFileInfo();
/* 1421 */         if ((fi != null) && (fi.fileName != null)) name = fi.fileName;
/* 1422 */         return name;
/* 1423 */       }if (key.equals("image.directory")) {
/* 1424 */         String dir = "";
/* 1425 */         FileInfo fi = getImage().getOriginalFileInfo();
/* 1426 */         if ((fi != null) && (fi.directory != null)) dir = fi.directory;
/* 1427 */         return dir;
/* 1428 */       }if ((key.equals("selection.name")) || (key.equals("roi.name"))) {
/* 1429 */         ImagePlus imp = getImage();
/* 1430 */         Roi roi = imp.getRoi();
/* 1431 */         String name = roi != null ? roi.getName() : null;
/* 1432 */         return name != null ? name : "";
/* 1433 */       }if (key.equals("font.name")) {
/* 1434 */         resetImage();
/* 1435 */         ImageProcessor ip = getProcessor();
/* 1436 */         setFont(ip);
/* 1437 */         return ip.getFont().getName();
/* 1438 */       }if (key.equals("threshold.method"))
/* 1439 */         return ThresholdAdjuster.getMethod();
/* 1440 */       if (key.equals("threshold.mode")) {
/* 1441 */         return ThresholdAdjuster.getMode();
/*      */       }
/* 1443 */       String value = "";
/*      */       try { value = System.getProperty(key); } catch (Exception e) {
/*      */       }
/* 1446 */       return value != null ? value : "";
/*      */     }
/* 1448 */     return "";
/*      */   }
/*      */ 
/*      */   String getMetadataValue(String key) {
/* 1452 */     String metadata = getMetadataAsString();
/* 1453 */     if (metadata == null) return null;
/* 1454 */     int index1 = metadata.indexOf(key + " =");
/* 1455 */     if (index1 != -1) {
/* 1456 */       index1 += key.length() + 2;
/*      */     } else {
/* 1458 */       index1 = metadata.indexOf(key + ":");
/* 1459 */       if (index1 != -1)
/* 1460 */         index1 += key.length() + 1;
/*      */       else
/* 1462 */         return null;
/*      */     }
/* 1464 */     int index2 = metadata.indexOf("\n", index1);
/* 1465 */     if (index2 == -1) return null;
/* 1466 */     String value = metadata.substring(index1 + 1, index2);
/* 1467 */     if (value.startsWith(" ")) value = value.substring(1, value.length());
/* 1468 */     return value;
/*      */   }
/*      */ 
/*      */   String getMetadataAsString() {
/* 1472 */     ImagePlus imp = getImage();
/* 1473 */     String metadata = null;
/* 1474 */     if (imp.getStackSize() > 1) {
/* 1475 */       ImageStack stack = imp.getStack();
/* 1476 */       String label = stack.getSliceLabel(imp.getCurrentSlice());
/* 1477 */       if ((label != null) && (label.indexOf('\n') > 0)) metadata = label;
/*      */     }
/* 1479 */     if (metadata == null)
/* 1480 */       metadata = (String)imp.getProperty("Info");
/* 1481 */     return metadata;
/*      */   }
/*      */ 
/*      */   String getWindowContents() {
/* 1485 */     Frame frame = WindowManager.getFrontWindow();
/* 1486 */     if ((frame != null) && ((frame instanceof TextWindow))) {
/* 1487 */       TextPanel tp = ((TextWindow)frame).getTextPanel();
/* 1488 */       return tp.getText();
/* 1489 */     }if ((frame != null) && ((frame instanceof Editor)))
/* 1490 */       return ((Editor)frame).getText();
/* 1491 */     if ((frame != null) && ((frame instanceof Recorder))) {
/* 1492 */       return ((Recorder)frame).getText();
/*      */     }
/* 1494 */     return getImageInfo();
/*      */   }
/*      */ 
/*      */   String getImageInfo() {
/* 1498 */     ImagePlus imp = getImage();
/* 1499 */     Info infoPlugin = new Info();
/* 1500 */     return infoPlugin.getImageInfo(imp, getProcessor());
/*      */   }
/*      */ 
/*      */   public String getDirectory() {
/* 1504 */     String dir = IJ.getDirectory(getStringArg());
/* 1505 */     if (dir == null) dir = "";
/* 1506 */     return dir;
/*      */   }
/*      */ 
/*      */   double getSelectionType() {
/* 1510 */     this.interp.getParens();
/* 1511 */     double type = -1.0D;
/* 1512 */     ImagePlus imp = getImage();
/* 1513 */     Roi roi = imp.getRoi();
/* 1514 */     if (roi != null)
/* 1515 */       type = roi.getType();
/* 1516 */     return type;
/*      */   }
/*      */ 
/*      */   void showMessage(boolean withCancel)
/*      */   {
/* 1521 */     this.interp.getLeftParen();
/* 1522 */     String title = getString();
/*      */     String message;
/*      */     String message;
/* 1523 */     if (this.interp.nextToken() == 44) {
/* 1524 */       this.interp.getComma();
/* 1525 */       message = getString();
/*      */     } else {
/* 1527 */       message = title;
/* 1528 */       title = "";
/*      */     }
/* 1530 */     this.interp.getRightParen();
/* 1531 */     if (withCancel)
/* 1532 */       IJ.showMessageWithCancel(title, message);
/*      */     else
/* 1534 */       IJ.showMessage(title, message);
/*      */   }
/*      */ 
/*      */   double lengthOf() {
/* 1538 */     int length = 0;
/* 1539 */     this.interp.getLeftParen();
/* 1540 */     switch (this.interp.nextToken()) {
/*      */     case 133:
/*      */     case 136:
/*      */     case 138:
/* 1544 */       length = getString().length();
/* 1545 */       break;
/*      */     case 129:
/* 1547 */       if (this.pgm.code[(this.interp.pc + 2)] == 91) {
/* 1548 */         length = getString().length();
/*      */       }
/*      */       else {
/* 1551 */         this.interp.getToken();
/* 1552 */         Variable v = this.interp.lookupVariable();
/* 1553 */         if (v == null) return 0.0D;
/* 1554 */         String s = v.getString();
/* 1555 */         if (s != null) {
/* 1556 */           length = s.length();
/*      */         } else {
/* 1558 */           Variable[] array = v.getArray();
/* 1559 */           if (array != null)
/* 1560 */             length = v.getArraySize();
/*      */           else
/* 1562 */             this.interp.error("String or array expected"); 
/*      */         }
/*      */       }
/* 1564 */       break;
/*      */     case 130:
/*      */     case 131:
/*      */     case 132:
/*      */     case 134:
/*      */     case 135:
/*      */     case 137:
/*      */     default:
/* 1566 */       this.interp.error("String or array expected");
/*      */     }
/* 1568 */     this.interp.getRightParen();
/* 1569 */     return length;
/*      */   }
/*      */ 
/*      */   void getCursorLoc() {
/* 1573 */     Variable x = getFirstVariable();
/* 1574 */     Variable y = getNextVariable();
/* 1575 */     Variable z = getNextVariable();
/* 1576 */     Variable flags = getLastVariable();
/* 1577 */     ImagePlus imp = getImage();
/* 1578 */     ImageCanvas ic = imp.getCanvas();
/* 1579 */     if (ic == null) return;
/* 1580 */     Point p = ic.getCursorLoc();
/* 1581 */     x.setValue(p.x);
/* 1582 */     y.setValue(p.y);
/* 1583 */     z.setValue(imp.getCurrentSlice() - 1);
/* 1584 */     Roi roi = imp.getRoi();
/* 1585 */     flags.setValue(ic.getModifiers() + ((roi != null) && (roi.contains(p.x, p.y)) ? 32 : 0));
/*      */   }
/*      */ 
/*      */   void getLine() {
/* 1589 */     Variable vx1 = getFirstVariable();
/* 1590 */     Variable vy1 = getNextVariable();
/* 1591 */     Variable vx2 = getNextVariable();
/* 1592 */     Variable vy2 = getNextVariable();
/* 1593 */     Variable lineWidth = getLastVariable();
/* 1594 */     ImagePlus imp = getImage();
/* 1595 */     double x1 = -1.0D; double y1 = -1.0D; double x2 = -1.0D; double y2 = -1.0D;
/* 1596 */     Roi roi = imp.getRoi();
/* 1597 */     if ((roi != null) && (roi.getType() == 5)) {
/* 1598 */       Line line = (Line)roi;
/* 1599 */       x1 = line.x1d; y1 = line.y1d; x2 = line.x2d; y2 = line.y2d;
/*      */     }
/* 1601 */     vx1.setValue(x1);
/* 1602 */     vy1.setValue(y1);
/* 1603 */     vx2.setValue(x2);
/* 1604 */     vy2.setValue(y2);
/* 1605 */     lineWidth.setValue(roi != null ? roi.getStrokeWidth() : 1.0D);
/*      */   }
/*      */ 
/*      */   void getVoxelSize() {
/* 1609 */     Variable width = getFirstVariable();
/* 1610 */     Variable height = getNextVariable();
/* 1611 */     Variable depth = getNextVariable();
/* 1612 */     Variable unit = getLastVariable();
/* 1613 */     ImagePlus imp = getImage();
/* 1614 */     Calibration cal = imp.getCalibration();
/* 1615 */     width.setValue(cal.pixelWidth);
/* 1616 */     height.setValue(cal.pixelHeight);
/* 1617 */     depth.setValue(cal.pixelDepth);
/* 1618 */     unit.setString(cal.getUnits());
/*      */   }
/*      */ 
/*      */   void getHistogram() {
/* 1622 */     this.interp.getLeftParen();
/* 1623 */     Variable values = null;
/* 1624 */     if (this.interp.nextToken() == 130)
/* 1625 */       this.interp.getExpression();
/*      */     else
/* 1627 */       values = getArrayVariable();
/* 1628 */     Variable counts = getNextArrayVariable();
/* 1629 */     this.interp.getComma();
/* 1630 */     int nBins = (int)this.interp.getExpression();
/* 1631 */     ImagePlus imp = getImage();
/* 1632 */     double histMin = 0.0D; double histMax = 0.0D;
/* 1633 */     boolean setMinMax = false;
/* 1634 */     int bitDepth = imp.getBitDepth();
/* 1635 */     if (this.interp.nextToken() == 44) {
/* 1636 */       histMin = getNextArg();
/* 1637 */       histMax = getLastArg();
/* 1638 */       if ((bitDepth == 8) || (bitDepth == 24))
/* 1639 */         this.interp.error("16 or 32-bit image required to set histMin and histMax");
/* 1640 */       setMinMax = true;
/*      */     } else {
/* 1642 */       this.interp.getRightParen();
/* 1643 */     }if (((bitDepth == 8) || (bitDepth == 24)) && (nBins != 256))
/* 1644 */       this.interp.error("Bin count (" + nBins + ") must be 256 for 8-bit and RGB images");
/* 1645 */     if ((nBins == 65536) && (bitDepth == 16)) {
/* 1646 */       Variable[] array = counts.getArray();
/* 1647 */       int[] hist = getProcessor().getHistogram();
/* 1648 */       if ((array != null) && (array.length == nBins))
/* 1649 */         for (int i = 0; i < nBins; i++)
/* 1650 */           array[i].setValue(hist[i]);
/*      */       else
/* 1652 */         counts.setArray(new Variable(hist).getArray());
/*      */       return;
/*      */     }
/*      */     ImageStatistics stats;
/*      */     ImageStatistics stats;
/* 1656 */     if (setMinMax)
/* 1657 */       stats = imp.getStatistics(27, nBins, histMin, histMax);
/*      */     else
/* 1659 */       stats = imp.getStatistics(27, nBins);
/* 1660 */     if (values != null) {
/* 1661 */       Calibration cal = imp.getCalibration();
/* 1662 */       double[] array = new double[nBins];
/* 1663 */       double value = cal.getCValue(stats.histMin);
/* 1664 */       double inc = 1.0D;
/* 1665 */       if ((bitDepth == 16) || (bitDepth == 32) || (cal.calibrated()))
/* 1666 */         inc = (cal.getCValue(stats.histMax) - cal.getCValue(stats.histMin)) / stats.nBins;
/* 1667 */       for (int i = 0; i < nBins; i++) {
/* 1668 */         array[i] = value;
/* 1669 */         value += inc;
/*      */       }
/* 1671 */       values.setArray(new Variable(array).getArray());
/*      */     }
/* 1673 */     Variable[] array = counts.getArray();
/* 1674 */     if ((array != null) && (array.length == nBins))
/* 1675 */       for (int i = 0; i < nBins; i++)
/* 1676 */         array[i].setValue(stats.histogram[i]);
/*      */     else
/* 1678 */       counts.setArray(new Variable(stats.histogram).getArray());
/*      */   }
/*      */ 
/*      */   void getLut() {
/* 1682 */     Variable reds = getFirstArrayVariable();
/* 1683 */     Variable greens = getNextArrayVariable();
/* 1684 */     Variable blues = getLastArrayVariable();
/* 1685 */     ImagePlus imp = getImage();
/* 1686 */     IndexColorModel cm = null;
/* 1687 */     if (imp.isComposite()) {
/* 1688 */       cm = ((CompositeImage)imp).getChannelLut();
/*      */     } else {
/* 1690 */       ImageProcessor ip = imp.getProcessor();
/* 1691 */       if ((ip instanceof ColorProcessor))
/* 1692 */         this.interp.error("Non-RGB image expected");
/* 1693 */       cm = (IndexColorModel)ip.getColorModel();
/*      */     }
/* 1695 */     int mapSize = cm.getMapSize();
/* 1696 */     byte[] rLUT = new byte[mapSize];
/* 1697 */     byte[] gLUT = new byte[mapSize];
/* 1698 */     byte[] bLUT = new byte[mapSize];
/* 1699 */     cm.getReds(rLUT);
/* 1700 */     cm.getGreens(gLUT);
/* 1701 */     cm.getBlues(bLUT);
/* 1702 */     reds.setArray(new Variable(rLUT).getArray());
/* 1703 */     greens.setArray(new Variable(gLUT).getArray());
/* 1704 */     blues.setArray(new Variable(bLUT).getArray());
/*      */   }
/*      */ 
/*      */   void setLut() {
/* 1708 */     double[] reds = getFirstArray();
/* 1709 */     double[] greens = getNextArray();
/* 1710 */     double[] blues = getLastArray();
/* 1711 */     int length = reds.length;
/* 1712 */     if ((greens.length != length) || (blues.length != length))
/* 1713 */       this.interp.error("Arrays are not the same length");
/* 1714 */     ImagePlus imp = getImage();
/* 1715 */     if (imp.getBitDepth() == 24)
/* 1716 */       this.interp.error("Non-RGB image expected");
/* 1717 */     ImageProcessor ip = getProcessor();
/* 1718 */     byte[] r = new byte[length];
/* 1719 */     byte[] g = new byte[length];
/* 1720 */     byte[] b = new byte[length];
/* 1721 */     for (int i = 0; i < length; i++) {
/* 1722 */       r[i] = ((byte)(int)reds[i]);
/* 1723 */       g[i] = ((byte)(int)greens[i]);
/* 1724 */       b[i] = ((byte)(int)blues[i]);
/*      */     }
/* 1726 */     LUT lut = new LUT(8, length, r, g, b);
/* 1727 */     if (imp.isComposite())
/* 1728 */       ((CompositeImage)imp).setChannelLut(lut);
/*      */     else
/* 1730 */       ip.setColorModel(lut);
/* 1731 */     imp.updateAndDraw();
/* 1732 */     this.updateNeeded = false;
/*      */   }
/*      */ 
/*      */   void getThreshold() {
/* 1736 */     Variable lower = getFirstVariable();
/* 1737 */     Variable upper = getLastVariable();
/* 1738 */     ImagePlus imp = getImage();
/* 1739 */     ImageProcessor ip = getProcessor();
/* 1740 */     double t1 = ip.getMinThreshold();
/* 1741 */     double t2 = ip.getMaxThreshold();
/* 1742 */     if (t1 == -808080.0D) {
/* 1743 */       t1 = -1.0D;
/* 1744 */       t2 = -1.0D;
/*      */     } else {
/* 1746 */       Calibration cal = imp.getCalibration();
/* 1747 */       t1 = cal.getCValue(t1);
/* 1748 */       t2 = cal.getCValue(t2);
/*      */     }
/* 1750 */     lower.setValue(t1);
/* 1751 */     upper.setValue(t2);
/*      */   }
/*      */ 
/*      */   void getPixelSize() {
/* 1755 */     Variable unit = getFirstVariable();
/* 1756 */     Variable width = getNextVariable();
/* 1757 */     Variable height = getNextVariable();
/* 1758 */     Variable depth = null;
/* 1759 */     if (this.interp.nextToken() == 44)
/* 1760 */       depth = getNextVariable();
/* 1761 */     this.interp.getRightParen();
/* 1762 */     Calibration cal = getImage().getCalibration();
/* 1763 */     unit.setString(cal.getUnits());
/* 1764 */     width.setValue(cal.pixelWidth);
/* 1765 */     height.setValue(cal.pixelHeight);
/* 1766 */     if (depth != null)
/* 1767 */       depth.setValue(cal.pixelDepth);
/*      */   }
/*      */ 
/*      */   void makeSelection() {
/* 1771 */     String type = null;
/* 1772 */     int roiType = -1;
/* 1773 */     this.interp.getLeftParen();
/* 1774 */     if (isStringArg()) {
/* 1775 */       type = getString().toLowerCase();
/* 1776 */       roiType = 2;
/* 1777 */       if (type.indexOf("free") != -1)
/* 1778 */         roiType = 3;
/* 1779 */       if (type.indexOf("traced") != -1)
/* 1780 */         roiType = 4;
/* 1781 */       if (type.indexOf("line") != -1) {
/* 1782 */         if (type.indexOf("free") != -1)
/* 1783 */           roiType = 7;
/*      */         else
/* 1785 */           roiType = 6;
/*      */       }
/* 1787 */       if (type.indexOf("angle") != -1)
/* 1788 */         roiType = 8;
/* 1789 */       if (type.indexOf("point") != -1)
/* 1790 */         roiType = 10;
/*      */     } else {
/* 1792 */       roiType = (int)this.interp.getExpression();
/* 1793 */       if ((roiType < 0) || (roiType == 9))
/* 1794 */         this.interp.error("Invalid selection type (" + roiType + ")");
/* 1795 */       if (roiType == 0) roiType = 2;
/* 1796 */       if (roiType == 1) roiType = 3;
/*      */     }
/* 1798 */     double[] x = getNextArray();
/* 1799 */     int n = x.length;
/* 1800 */     this.interp.getComma();
/* 1801 */     double[] y = getNumericArray();
/* 1802 */     if (this.interp.nextToken() == 44) {
/* 1803 */       n = (int)getLastArg();
/* 1804 */       if ((n > x.length) || (n > y.length))
/* 1805 */         this.interp.error("Array too short");
/*      */     } else {
/* 1807 */       this.interp.getRightParen();
/* 1808 */       if (y.length != n)
/* 1809 */         this.interp.error("Arrays are not the same length");
/*      */     }
/* 1811 */     ImagePlus imp = getImage();
/* 1812 */     int[] xcoord = new int[n];
/* 1813 */     int[] ycoord = new int[n];
/* 1814 */     int height = imp.getHeight();
/* 1815 */     for (int i = 0; i < n; i++) {
/* 1816 */       xcoord[i] = ((int)Math.round(x[i]));
/* 1817 */       ycoord[i] = ((int)Math.round(y[i]));
/*      */     }
/* 1819 */     Roi roi = null;
/* 1820 */     if (roiType == 5) {
/* 1821 */       if (xcoord.length != 2)
/* 1822 */         this.interp.error("2 element arrays expected");
/* 1823 */       roi = new Line(xcoord[0], ycoord[0], xcoord[1], ycoord[1]);
/* 1824 */     } else if (roiType == 10) {
/* 1825 */       roi = new PointRoi(xcoord, ycoord, n);
/*      */     } else {
/* 1827 */       roi = new PolygonRoi(xcoord, ycoord, n, roiType);
/* 1828 */     }Roi previousRoi = imp.getRoi();
/* 1829 */     if ((this.shiftKeyDown) || (this.altKeyDown)) imp.saveRoi();
/* 1830 */     imp.setRoi(roi);
/* 1831 */     if ((roiType == 2) || (roiType == 3)) {
/* 1832 */       roi = imp.getRoi();
/* 1833 */       if ((previousRoi != null) && (roi != null))
/* 1834 */         updateRoi(roi);
/*      */     }
/* 1836 */     this.updateNeeded = false;
/*      */   }
/*      */ 
/*      */   void doPlot() {
/* 1840 */     this.interp.getToken();
/* 1841 */     if (this.interp.token != 46)
/* 1842 */       this.interp.error("'.' expected");
/* 1843 */     this.interp.getToken();
/* 1844 */     if ((this.interp.token != 129) && (this.interp.token != 134))
/* 1845 */       this.interp.error("Function name expected: ");
/* 1846 */     String name = this.interp.tokenString;
/* 1847 */     if (name.equals("create")) {
/* 1848 */       newPlot();
/* 1849 */       return;
/* 1850 */     }if (name.equals("getValues")) {
/* 1851 */       getPlotValues();
/* 1852 */       return;
/*      */     }
/* 1854 */     if (this.plot == null)
/* 1855 */       this.interp.error("No plot defined");
/* 1856 */     if (name.equals("show")) {
/* 1857 */       showPlot();
/* 1858 */       return;
/* 1859 */     }if (name.equals("update")) {
/* 1860 */       updatePlot();
/* 1861 */       return;
/* 1862 */     }if (name.equals("setFrameSize")) {
/* 1863 */       this.plot.setFrameSize((int)getFirstArg(), (int)getLastArg());
/* 1864 */       return;
/* 1865 */     }if (name.equals("setLimits")) {
/* 1866 */       this.plot.setLimits(getFirstArg(), getNextArg(), getNextArg(), getLastArg());
/* 1867 */       return;
/* 1868 */     }if ((name.equals("addText")) || (name.equals("drawLabel"))) {
/* 1869 */       addPlotText();
/* 1870 */       return;
/* 1871 */     }if (name.equals("drawLine")) {
/* 1872 */       drawPlotLine();
/* 1873 */       return;
/* 1874 */     }if (name.equals("setColor")) {
/* 1875 */       setPlotColor();
/* 1876 */       return;
/* 1877 */     }if (name.equals("add")) {
/* 1878 */       String arg = getFirstString();
/* 1879 */       arg = arg.toLowerCase(Locale.US);
/* 1880 */       int what = 0;
/* 1881 */       if ((arg.indexOf("curve") != -1) || (arg.indexOf("line") != -1))
/* 1882 */         what = 2;
/* 1883 */       else if (arg.indexOf("box") != -1)
/* 1884 */         what = 3;
/* 1885 */       else if (arg.indexOf("triangle") != -1)
/* 1886 */         what = 4;
/* 1887 */       else if (arg.indexOf("cross") != -1)
/* 1888 */         what = 5;
/* 1889 */       else if (arg.indexOf("dot") != -1)
/* 1890 */         what = 6;
/* 1891 */       else if (arg.indexOf("x") != -1)
/* 1892 */         what = 1;
/* 1893 */       else if (arg.indexOf("error") != -1)
/* 1894 */         what = -1;
/* 1895 */       addToPlot(what);
/* 1896 */       return;
/* 1897 */     }if (name.startsWith("setLineWidth")) {
/* 1898 */       this.plot.setLineWidth((int)getArg());
/* 1899 */       return;
/* 1900 */     }if (name.startsWith("setJustification")) {
/* 1901 */       doFunction(357);
/* 1902 */       return;
/*      */     }
/* 1904 */     this.interp.error("Unrecognized plot function");
/*      */   }
/*      */ 
/*      */   void getPlotValues() {
/* 1908 */     Variable xvar = getFirstArrayVariable();
/* 1909 */     Variable yvar = getLastArrayVariable();
/* 1910 */     float[] xvalues = new float[0];
/* 1911 */     float[] yvalues = new float[0];
/* 1912 */     ImagePlus imp = getImage();
/* 1913 */     ImageWindow win = imp.getWindow();
/* 1914 */     if (imp.getProperty("XValues") != null) {
/* 1915 */       xvalues = (float[])imp.getProperty("XValues");
/* 1916 */       yvalues = (float[])imp.getProperty("YValues");
/* 1917 */     } else if ((win != null) && ((win instanceof PlotWindow))) {
/* 1918 */       PlotWindow pw = (PlotWindow)win;
/* 1919 */       xvalues = pw.getXValues();
/* 1920 */       yvalues = pw.getYValues();
/* 1921 */     } else if ((win != null) && ((win instanceof HistogramWindow))) {
/* 1922 */       HistogramWindow hw = (HistogramWindow)win;
/* 1923 */       double[] x = hw.getXValues();
/* 1924 */       xvalues = new float[x.length];
/* 1925 */       for (int i = 0; i < x.length; i++)
/* 1926 */         xvalues[i] = ((float)x[i]);
/* 1927 */       int[] y = hw.getHistogram();
/* 1928 */       yvalues = new float[y.length];
/* 1929 */       for (int i = 0; i < y.length; i++)
/* 1930 */         yvalues[i] = y[i];
/*      */     } else {
/* 1932 */       this.interp.error("No plot or histogram window");
/* 1933 */     }Variable[] xa = new Variable[xvalues.length];
/* 1934 */     Variable[] ya = new Variable[yvalues.length];
/* 1935 */     for (int i = 0; i < xvalues.length; i++)
/* 1936 */       xa[i] = new Variable(xvalues[i]);
/* 1937 */     for (int i = 0; i < yvalues.length; i++)
/* 1938 */       ya[i] = new Variable(yvalues[i]);
/* 1939 */     xvar.setArray(xa);
/* 1940 */     yvar.setArray(ya);
/*      */   }
/*      */ 
/*      */   void newPlot() {
/* 1944 */     String title = getFirstString();
/* 1945 */     String xLabel = getNextString();
/* 1946 */     String yLabel = getNextString();
/*      */     double[] x;
/*      */     double[] x;
/*      */     double[] y;
/* 1948 */     if (this.interp.nextToken() == 41)
/*      */     {
/*      */       double[] y;
/* 1949 */       x = y = null;
/*      */     } else {
/* 1951 */       x = getNextArray();
/* 1952 */       if (this.interp.nextToken() == 41) {
/* 1953 */         double[] y = x;
/* 1954 */         x = new double[y.length];
/* 1955 */         for (int i = 0; i < y.length; i++)
/* 1956 */           x[i] = i;
/*      */       } else {
/* 1958 */         y = getNextArray();
/*      */       }
/*      */     }
/* 1960 */     this.interp.getRightParen();
/* 1961 */     this.plot = new Plot(title, xLabel, yLabel, x, y);
/*      */   }
/*      */ 
/*      */   void showPlot() {
/* 1965 */     if (this.plot != null) {
/* 1966 */       PlotWindow plotWindow = this.plot.show();
/* 1967 */       if (plotWindow != null)
/* 1968 */         plotID = plotWindow.getImagePlus().getID();
/*      */     }
/* 1970 */     this.plot = null;
/* 1971 */     this.interp.getParens();
/*      */   }
/*      */ 
/*      */   void updatePlot() {
/* 1975 */     if (this.plot != null) {
/* 1976 */       ImagePlus plotImage = WindowManager.getImage(plotID);
/* 1977 */       ImageWindow win = plotImage != null ? plotImage.getWindow() : null;
/* 1978 */       if (win != null) {
/* 1979 */         ((PlotWindow)win).drawPlot(this.plot);
/*      */       } else {
/* 1981 */         PlotWindow plotWindow = this.plot.show();
/* 1982 */         if (plotWindow != null)
/* 1983 */           plotID = plotWindow.getImagePlus().getID();
/*      */       }
/*      */     }
/* 1986 */     this.plot = null;
/* 1987 */     this.interp.getParens();
/*      */   }
/*      */ 
/*      */   void addPlotText() {
/* 1991 */     String str = getFirstString();
/* 1992 */     double x = getNextArg();
/* 1993 */     double y = getLastArg();
/* 1994 */     this.plot.setJustification(this.justification);
/* 1995 */     this.plot.addLabel(x, y, str);
/*      */   }
/*      */ 
/*      */   void drawPlotLine() {
/* 1999 */     double x1 = getFirstArg();
/* 2000 */     double y1 = getNextArg();
/* 2001 */     double x2 = getNextArg();
/* 2002 */     double y2 = getLastArg();
/* 2003 */     this.plot.drawLine(x1, y1, x2, y2);
/*      */   }
/*      */ 
/*      */   void setPlotColor() {
/* 2007 */     this.interp.getLeftParen();
/* 2008 */     this.plot.setColor(getColor());
/* 2009 */     this.interp.getRightParen();
/*      */   }
/*      */ 
/*      */   void addToPlot(int what) {
/* 2013 */     double[] x = getNextArray();
/*      */     double[] y;
/* 2015 */     if (this.interp.nextToken() == 41) {
/* 2016 */       double[] y = x;
/* 2017 */       x = new double[y.length];
/* 2018 */       for (int i = 0; i < y.length; i++)
/* 2019 */         x[i] = i;
/*      */     } else {
/* 2021 */       this.interp.getComma();
/* 2022 */       y = getNumericArray();
/*      */     }
/* 2024 */     this.interp.getRightParen();
/* 2025 */     if (what == -1)
/* 2026 */       this.plot.addErrorBars(y);
/*      */     else
/* 2028 */       this.plot.addPoints(x, y, what);
/*      */   }
/*      */ 
/*      */   void getBounds() {
/* 2032 */     Variable x = getFirstVariable();
/* 2033 */     Variable y = getNextVariable();
/* 2034 */     Variable width = getNextVariable();
/* 2035 */     Variable height = getLastVariable();
/* 2036 */     ImagePlus imp = getImage();
/* 2037 */     Roi roi = imp.getRoi();
/* 2038 */     if (roi != null) {
/* 2039 */       Rectangle r = roi.getBounds();
/* 2040 */       x.setValue(r.x);
/* 2041 */       y.setValue(r.y);
/* 2042 */       width.setValue(r.width);
/* 2043 */       height.setValue(r.height);
/*      */     } else {
/* 2045 */       x.setValue(0.0D);
/* 2046 */       y.setValue(0.0D);
/* 2047 */       width.setValue(imp.getWidth());
/* 2048 */       height.setValue(imp.getHeight());
/*      */     }
/*      */   }
/*      */ 
/*      */   String substring() {
/* 2053 */     String s = getFirstString();
/* 2054 */     int index1 = (int)getNextArg();
/* 2055 */     int index2 = s.length();
/* 2056 */     if (this.interp.nextToken() == 44)
/* 2057 */       index2 = (int)getLastArg();
/*      */     else
/* 2059 */       this.interp.getRightParen();
/* 2060 */     if (index1 > index2)
/* 2061 */       this.interp.error("beginIndex>endIndex");
/* 2062 */     checkIndex(index1, 0, s.length());
/* 2063 */     checkIndex(index2, 0, s.length());
/* 2064 */     return s.substring(index1, index2);
/*      */   }
/*      */ 
/*      */   int indexOf() {
/* 2068 */     String s1 = getFirstString();
/* 2069 */     String s2 = getNextString();
/* 2070 */     int fromIndex = 0;
/* 2071 */     if (this.interp.nextToken() == 44) {
/* 2072 */       fromIndex = (int)getLastArg();
/* 2073 */       checkIndex(fromIndex, 0, s1.length() - 1);
/*      */     } else {
/* 2075 */       this.interp.getRightParen();
/* 2076 */     }if (fromIndex == 0) {
/* 2077 */       return s1.indexOf(s2);
/*      */     }
/* 2079 */     return s1.indexOf(s2, fromIndex);
/*      */   }
/*      */ 
/*      */   int startsWithEndsWith(int type) {
/* 2083 */     String s1 = getFirstString();
/* 2084 */     String s2 = getLastString();
/* 2085 */     if (type == 1034) {
/* 2086 */       return s1.startsWith(s2) ? 1 : 0;
/*      */     }
/* 2088 */     return s1.endsWith(s2) ? 1 : 0;
/*      */   }
/*      */ 
/*      */   double isActive() {
/* 2092 */     int id = (int)getArg();
/* 2093 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 2094 */     if ((imp == null) || (imp.getID() != id)) {
/* 2095 */       return 0.0D;
/*      */     }
/* 2097 */     return 1.0D;
/*      */   }
/*      */ 
/*      */   double isOpen() {
/* 2101 */     this.interp.getLeftParen();
/* 2102 */     if (isStringArg()) {
/* 2103 */       String title = getString();
/* 2104 */       this.interp.getRightParen();
/* 2105 */       return isOpen(title) ? 1.0D : 0.0D;
/*      */     }
/* 2107 */     int id = (int)this.interp.getExpression();
/* 2108 */     this.interp.getRightParen();
/* 2109 */     return WindowManager.getImage(id) == null ? 0.0D : 1.0D;
/*      */   }
/*      */ 
/*      */   boolean isOpen(String title)
/*      */   {
/* 2114 */     boolean open = WindowManager.getFrame(title) != null;
/* 2115 */     if (open)
/* 2116 */       return true;
/*      */     Enumeration en;
/* 2117 */     if ((Interpreter.isBatchMode()) && (Interpreter.imageTable != null)) {
/* 2118 */       for (en = Interpreter.imageTable.elements(); en.hasMoreElements(); ) {
/* 2119 */         ImagePlus imp = (ImagePlus)en.nextElement();
/* 2120 */         if ((imp != null) && (imp.getTitle().equals(title)))
/* 2121 */           return true;
/*      */       }
/*      */     }
/* 2124 */     return false;
/*      */   }
/*      */ 
/*      */   boolean isStringArg() {
/* 2128 */     int nextToken = this.pgm.code[(this.interp.pc + 1)];
/* 2129 */     int tok = nextToken & 0xFF;
/* 2130 */     if ((tok == 133) || (tok == 136)) return true;
/* 2131 */     if (tok != 129) return false;
/* 2132 */     Variable v = this.interp.lookupVariable(nextToken >> 12);
/* 2133 */     if (v == null) return false;
/* 2134 */     int type = v.getType();
/* 2135 */     if (type != 1)
/* 2136 */       return v.getType() == 2;
/* 2137 */     Variable[] array = v.getArray();
/* 2138 */     if (array.length == 0) return false;
/* 2139 */     return array[0].getType() == 2;
/*      */   }
/*      */ 
/*      */   void exit() {
/* 2143 */     String msg = null;
/* 2144 */     if (this.interp.nextToken() == 40) {
/* 2145 */       this.interp.getLeftParen();
/* 2146 */       if (isStringArg())
/* 2147 */         msg = getString();
/* 2148 */       this.interp.getRightParen();
/*      */     }
/* 2150 */     this.interp.finishUp();
/* 2151 */     if (msg != null)
/* 2152 */       IJ.showMessage("Macro", msg);
/* 2153 */     throw new RuntimeException("Macro canceled");
/*      */   }
/*      */ 
/*      */   void showProgress() {
/* 2157 */     ImageJ ij = IJ.getInstance();
/* 2158 */     ProgressBar progressBar = ij != null ? ij.getProgressBar() : null;
/* 2159 */     this.interp.getLeftParen();
/* 2160 */     double arg1 = this.interp.getExpression();
/* 2161 */     if (this.interp.nextToken() == 44) {
/* 2162 */       this.interp.getComma();
/* 2163 */       double arg2 = this.interp.getExpression();
/* 2164 */       if (progressBar != null) progressBar.show((arg1 + 1.0D) / arg2, true);
/*      */     }
/* 2166 */     else if (progressBar != null) { progressBar.show(arg1, true); }
/* 2167 */     this.interp.getRightParen();
/* 2168 */     this.interp.showingProgress = true;
/*      */   }
/*      */ 
/*      */   void saveSettings() {
/* 2172 */     this.interp.getParens();
/* 2173 */     this.usePointerCursor = Prefs.usePointerCursor;
/* 2174 */     this.hideProcessStackDialog = IJ.hideProcessStackDialog;
/* 2175 */     this.divideByZeroValue = FloatBlitter.divideByZeroValue;
/* 2176 */     this.jpegQuality = FileSaver.getJpegQuality();
/* 2177 */     this.saveLineWidth = Line.getWidth();
/* 2178 */     this.doScaling = ImageConverter.getDoScaling();
/* 2179 */     this.weightedColor = Prefs.weightedColor;
/* 2180 */     this.weights = ColorProcessor.getWeightingFactors();
/* 2181 */     this.interpolateScaledImages = Prefs.interpolateScaledImages;
/* 2182 */     this.open100Percent = Prefs.open100Percent;
/* 2183 */     this.blackCanvas = Prefs.blackCanvas;
/* 2184 */     this.useJFileChooser = Prefs.useJFileChooser;
/* 2185 */     this.debugMode = IJ.debugMode;
/* 2186 */     this.foregroundColor = Toolbar.getForegroundColor();
/* 2187 */     this.backgroundColor = Toolbar.getBackgroundColor();
/* 2188 */     this.roiColor = Roi.getColor();
/* 2189 */     this.pointAutoMeasure = Prefs.pointAutoMeasure;
/* 2190 */     this.requireControlKey = Prefs.requireControlKey;
/* 2191 */     this.useInvertingLut = Prefs.useInvertingLut;
/* 2192 */     this.saveSettingsCalled = true;
/* 2193 */     this.measurements = Analyzer.getMeasurements();
/* 2194 */     this.decimalPlaces = Analyzer.getPrecision();
/* 2195 */     this.blackBackground = Prefs.blackBackground;
/* 2196 */     this.pasteMode = Roi.getCurrentPasteMode();
/*      */   }
/*      */ 
/*      */   void restoreSettings() {
/* 2200 */     this.interp.getParens();
/* 2201 */     if (!this.saveSettingsCalled)
/* 2202 */       this.interp.error("saveSettings() not called");
/* 2203 */     Prefs.usePointerCursor = this.usePointerCursor;
/* 2204 */     IJ.hideProcessStackDialog = this.hideProcessStackDialog;
/* 2205 */     FloatBlitter.divideByZeroValue = this.divideByZeroValue;
/* 2206 */     FileSaver.setJpegQuality(this.jpegQuality);
/* 2207 */     Line.setWidth(this.saveLineWidth);
/* 2208 */     ImageConverter.setDoScaling(this.doScaling);
/* 2209 */     if (this.weightedColor != Prefs.weightedColor) {
/* 2210 */       ColorProcessor.setWeightingFactors(this.weights[0], this.weights[1], this.weights[2]);
/* 2211 */       Prefs.weightedColor = (this.weights[0] != 0.3333333333333333D) || (this.weights[1] != 0.3333333333333333D) || (this.weights[2] != 0.3333333333333333D);
/*      */     }
/* 2213 */     Prefs.interpolateScaledImages = this.interpolateScaledImages;
/* 2214 */     Prefs.open100Percent = this.open100Percent;
/* 2215 */     Prefs.blackCanvas = this.blackCanvas;
/* 2216 */     Prefs.useJFileChooser = this.useJFileChooser;
/* 2217 */     Prefs.useInvertingLut = this.useInvertingLut;
/* 2218 */     IJ.debugMode = this.debugMode;
/* 2219 */     Toolbar.setForegroundColor(this.foregroundColor);
/* 2220 */     Toolbar.setBackgroundColor(this.backgroundColor);
/* 2221 */     Roi.setColor(this.roiColor);
/* 2222 */     Analyzer.setMeasurements(this.measurements);
/* 2223 */     Analyzer.setPrecision(this.decimalPlaces);
/* 2224 */     ColorProcessor.setWeightingFactors(this.weights[0], this.weights[1], this.weights[2]);
/* 2225 */     Prefs.blackBackground = this.blackBackground;
/* 2226 */     Roi.setPasteMode(this.pasteMode);
/*      */   }
/*      */ 
/*      */   void setKeyDown() {
/* 2230 */     String keys = getStringArg();
/* 2231 */     keys = keys.toLowerCase(Locale.US);
/* 2232 */     this.altKeyDown = (keys.indexOf("alt") != -1);
/* 2233 */     if (this.altKeyDown)
/* 2234 */       IJ.setKeyDown(18);
/*      */     else
/* 2236 */       IJ.setKeyUp(18);
/* 2237 */     this.shiftKeyDown = (keys.indexOf("shift") != -1);
/* 2238 */     if (this.shiftKeyDown)
/* 2239 */       IJ.setKeyDown(16);
/*      */     else
/* 2241 */       IJ.setKeyUp(16);
/* 2242 */     if (keys.equals("space"))
/* 2243 */       IJ.setKeyDown(32);
/*      */     else
/* 2245 */       IJ.setKeyUp(32);
/* 2246 */     if (keys.indexOf("esc") != -1)
/* 2247 */       abortPluginOrMacro();
/*      */     else
/* 2249 */       this.interp.keysSet = true;
/*      */   }
/*      */ 
/*      */   void abortPluginOrMacro() {
/* 2253 */     Interpreter.abortPrevious();
/* 2254 */     IJ.setKeyDown(27);
/* 2255 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 2256 */     if (imp != null) {
/* 2257 */       ImageWindow win = imp.getWindow();
/* 2258 */       if (win != null) {
/* 2259 */         win.running = false;
/* 2260 */         win.running2 = false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void open()
/*      */   {
/* 2267 */     this.interp.getLeftParen();
/* 2268 */     if (this.interp.nextToken() == 41) {
/* 2269 */       this.interp.getRightParen();
/* 2270 */       IJ.open();
/*      */     } else {
/* 2272 */       double n = (0.0D / 0.0D);
/* 2273 */       String path = getString();
/* 2274 */       if (this.interp.nextToken() == 44) {
/* 2275 */         this.interp.getComma();
/* 2276 */         n = this.interp.getExpression();
/*      */       }
/* 2278 */       this.interp.getRightParen();
/* 2279 */       if (!Double.isNaN(n))
/*      */         try {
/* 2281 */           IJ.open(path, (int)n);
/*      */         } catch (Exception e) {
/* 2283 */           String msg = e.getMessage();
/* 2284 */           if ((msg != null) && (msg.indexOf("canceled") == -1))
/* 2285 */             this.interp.error("" + msg);
/*      */         }
/*      */       else
/* 2288 */         IJ.open(path);
/* 2289 */       if ((path != null) && (!path.equals(""))) {
/* 2290 */         int index = path.lastIndexOf('/');
/* 2291 */         if (index == -1)
/* 2292 */           index = path.lastIndexOf('\\');
/* 2293 */         String name = (index >= 0) && (index < path.length()) ? path.substring(index + 1) : path;
/* 2294 */         OpenDialog.setLastName(name);
/*      */       }
/*      */     }
/* 2297 */     resetImage();
/*      */   }
/*      */ 
/*      */   double roiManager() {
/* 2301 */     String cmd = getFirstString();
/* 2302 */     cmd = cmd.toLowerCase();
/* 2303 */     String path = null;
/* 2304 */     String color = null;
/* 2305 */     double lineWidth = 1.0D;
/* 2306 */     int index = 0;
/* 2307 */     double countOrIndex = (0.0D / 0.0D);
/* 2308 */     boolean twoArgCommand = (cmd.equals("open")) || (cmd.equals("save")) || (cmd.equals("rename")) || (cmd.equals("set color")) || (cmd.equals("set fill color")) || (cmd.equals("set line width")) || (cmd.equals("associate")) || (cmd.equals("centered")) || (cmd.equals("usenames"));
/*      */ 
/* 2311 */     boolean select = cmd.equals("select");
/* 2312 */     boolean multiSelect = false;
/* 2313 */     boolean add = cmd.equals("add");
/* 2314 */     if (twoArgCommand) {
/* 2315 */       path = getLastString();
/* 2316 */     } else if (add) {
/* 2317 */       if (this.interp.nextToken() == 44) {
/* 2318 */         this.interp.getComma();
/* 2319 */         color = this.interp.getString();
/*      */       }
/* 2321 */       if (this.interp.nextToken() == 44) {
/* 2322 */         this.interp.getComma();
/* 2323 */         lineWidth = this.interp.getExpression();
/*      */       }
/* 2325 */       this.interp.getRightParen();
/* 2326 */     } else if (select) {
/* 2327 */       this.interp.getComma();
/* 2328 */       multiSelect = isArrayArg();
/* 2329 */       if (!multiSelect) {
/* 2330 */         index = (int)this.interp.getExpression();
/* 2331 */         this.interp.getRightParen();
/*      */       }
/*      */     } else {
/* 2334 */       this.interp.getRightParen();
/* 2335 */     }if ((RoiManager.getInstance() == null) && (this.roiManager == null)) {
/* 2336 */       if (Interpreter.isBatchMode())
/* 2337 */         this.roiManager = new RoiManager(true);
/*      */       else
/* 2339 */         IJ.run("ROI Manager...");
/*      */     }
/* 2341 */     RoiManager rm = this.roiManager != null ? this.roiManager : RoiManager.getInstance();
/* 2342 */     if (rm == null)
/* 2343 */       this.interp.error("ROI Manager not found");
/* 2344 */     if (multiSelect)
/* 2345 */       return setMultipleIndexes(rm);
/* 2346 */     if (twoArgCommand) {
/* 2347 */       rm.runCommand(cmd, path);
/* 2348 */     } else if (add) {
/* 2349 */       rm.runCommand("Add", color, lineWidth);
/* 2350 */     } else if (select) {
/* 2351 */       int n = rm.getList().getItemCount();
/* 2352 */       checkIndex(index, 0, n - 1);
/* 2353 */       if ((this.shiftKeyDown) || (this.altKeyDown)) {
/* 2354 */         rm.select(index, this.shiftKeyDown, this.altKeyDown);
/* 2355 */         this.shiftKeyDown = (this.altKeyDown = 0);
/*      */       } else {
/* 2357 */         rm.select(index);
/*      */       } } else if (cmd.equals("count")) {
/* 2359 */       countOrIndex = rm.getList().getItemCount();
/* 2360 */     } else if (cmd.equals("index")) {
/* 2361 */       countOrIndex = rm.getList().getSelectedIndex();
/*      */     }
/* 2363 */     else if (!rm.runCommand(cmd)) {
/* 2364 */       this.interp.error("Invalid ROI Manager command");
/*      */     }
/* 2366 */     return countOrIndex;
/*      */   }
/*      */ 
/*      */   boolean isArrayArg() {
/* 2370 */     int nextToken = this.pgm.code[(this.interp.pc + 1)];
/* 2371 */     int tok = nextToken & 0xFF;
/* 2372 */     if (tok == 137) return true;
/* 2373 */     if (tok != 129) return false;
/* 2374 */     Variable v = this.interp.lookupVariable(nextToken >> 12);
/* 2375 */     if (v == null) return false;
/* 2376 */     return v.getType() == 1;
/*      */   }
/*      */ 
/*      */   double setMultipleIndexes(RoiManager rm) {
/* 2380 */     if (this.interp.nextToken() == 44)
/* 2381 */       this.interp.getComma();
/* 2382 */     double[] indexes = getNumericArray();
/* 2383 */     this.interp.getRightParen();
/* 2384 */     int[] selectedIndexes = new int[indexes.length];
/* 2385 */     int count = rm.getList().getItemCount();
/* 2386 */     for (int i = 0; i < indexes.length; i++) {
/* 2387 */       selectedIndexes[i] = ((int)indexes[i]);
/* 2388 */       if ((selectedIndexes[i] < 0) || (selectedIndexes[i] >= count))
/* 2389 */         this.interp.error("Invalid index: " + selectedIndexes[i]);
/*      */     }
/* 2391 */     rm.setSelectedIndexes(selectedIndexes);
/* 2392 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   void setFont() {
/* 2396 */     String name = getFirstString();
/* 2397 */     int size = 0;
/* 2398 */     int style = 0;
/* 2399 */     if (name.equals("user")) {
/* 2400 */       name = TextRoi.getFont();
/* 2401 */       size = TextRoi.getSize();
/* 2402 */       style = TextRoi.getStyle();
/* 2403 */       this.antialiasedText = TextRoi.isAntialiased();
/* 2404 */       this.interp.getRightParen();
/*      */     } else {
/* 2406 */       size = (int)getNextArg();
/* 2407 */       this.antialiasedText = false;
/* 2408 */       if (this.interp.nextToken() == 44) {
/* 2409 */         String styles = getLastString().toLowerCase();
/* 2410 */         if (styles.indexOf("bold") != -1) style++;
/* 2411 */         if (styles.indexOf("italic") != -1) style += 2;
/* 2412 */         if (styles.indexOf("anti") != -1) this.antialiasedText = true; 
/*      */       }
/* 2414 */       else { this.interp.getRightParen(); }
/*      */     }
/* 2416 */     this.font = new Font(name, style, size);
/* 2417 */     this.fontSet = false;
/*      */   }
/*      */ 
/*      */   void getMinAndMax() {
/* 2421 */     Variable min = getFirstVariable();
/* 2422 */     Variable max = getLastVariable();
/* 2423 */     ImagePlus imp = getImage();
/* 2424 */     double v1 = imp.getDisplayRangeMin();
/* 2425 */     double v2 = imp.getDisplayRangeMax();
/* 2426 */     Calibration cal = imp.getCalibration();
/* 2427 */     v1 = cal.getCValue(v1);
/* 2428 */     v2 = cal.getCValue(v2);
/* 2429 */     min.setValue(v1);
/* 2430 */     max.setValue(v2);
/*      */   }
/*      */ 
/*      */   void selectImage() {
/* 2434 */     this.interp.getLeftParen();
/* 2435 */     if (isStringArg()) {
/* 2436 */       String title = getString();
/* 2437 */       if (!isOpen(title))
/* 2438 */         this.interp.error("\"" + title + "\" not found");
/* 2439 */       selectImage(title);
/* 2440 */       this.interp.getRightParen();
/*      */     } else {
/* 2442 */       int id = (int)this.interp.getExpression();
/* 2443 */       if (WindowManager.getImage(id) == null)
/* 2444 */         this.interp.error("Image " + id + " not found");
/* 2445 */       IJ.selectWindow(id);
/* 2446 */       this.interp.getRightParen();
/*      */     }
/* 2448 */     resetImage();
/*      */   }
/*      */ 
/*      */   void selectImage(String title) {
/* 2452 */     if (Interpreter.isBatchMode())
/*      */     {
/*      */       Enumeration en;
/* 2453 */       if (Interpreter.imageTable != null) {
/* 2454 */         for (en = Interpreter.imageTable.elements(); en.hasMoreElements(); ) {
/* 2455 */           ImagePlus imp = (ImagePlus)en.nextElement();
/* 2456 */           if ((imp != null) && 
/* 2457 */             (imp.getTitle().equals(title))) {
/* 2458 */             ImagePlus imp2 = WindowManager.getCurrentImage();
/* 2459 */             if ((imp2 != null) && (imp2 != imp)) imp2.saveRoi();
/* 2460 */             WindowManager.setTempCurrentImage(imp);
/* 2461 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2466 */       selectWindowManagerImage(title);
/*      */     } else {
/* 2468 */       selectWindowManagerImage(title);
/*      */     }
/*      */   }
/*      */ 
/* 2472 */   void notFound(String title) { this.interp.error(title + " not found"); }
/*      */ 
/*      */   void selectWindowManagerImage(String title)
/*      */   {
/* 2476 */     long start = System.currentTimeMillis();
/* 2477 */     while (System.currentTimeMillis() - start < 4000L) {
/* 2478 */       int[] wList = WindowManager.getIDList();
/* 2479 */       int len = wList != null ? wList.length : 0;
/* 2480 */       for (int i = 0; i < len; i++) {
/* 2481 */         ImagePlus imp = WindowManager.getImage(wList[i]);
/* 2482 */         if ((imp != null) && 
/* 2483 */           (imp.getTitle().equals(title))) {
/* 2484 */           IJ.selectWindow(imp.getID());
/* 2485 */           return;
/*      */         }
/*      */       }
/*      */ 
/* 2489 */       IJ.wait(10);
/*      */     }
/* 2491 */     notFound(title);
/*      */   }
/*      */ 
/*      */   void close() {
/* 2495 */     this.interp.getParens();
/* 2496 */     ImagePlus imp = getImage();
/* 2497 */     ImageWindow win = imp.getWindow();
/* 2498 */     if (win != null) {
/* 2499 */       imp.changes = false;
/* 2500 */       win.close();
/*      */     } else {
/* 2502 */       imp.saveRoi();
/* 2503 */       WindowManager.setTempCurrentImage(null);
/* 2504 */       Interpreter.removeBatchModeImage(imp);
/*      */     }
/* 2506 */     resetImage();
/*      */   }
/*      */ 
/*      */   void setBatchMode() {
/* 2510 */     boolean enterBatchMode = false;
/* 2511 */     String sarg = null;
/* 2512 */     this.interp.getLeftParen();
/* 2513 */     if (isStringArg()) {
/* 2514 */       sarg = getString();
/*      */     } else {
/* 2516 */       double arg = this.interp.getBooleanExpression();
/* 2517 */       this.interp.checkBoolean(arg);
/* 2518 */       enterBatchMode = arg == 1.0D;
/*      */     }
/* 2520 */     this.interp.getRightParen();
/* 2521 */     if (!Interpreter.isBatchMode())
/* 2522 */       this.interp.calledMacro = false;
/* 2523 */     resetImage();
/* 2524 */     if (enterBatchMode) {
/* 2525 */       if (Interpreter.isBatchMode()) return;
/* 2526 */       Interpreter.setBatchMode(true);
/* 2527 */       ImagePlus tmp = WindowManager.getTempCurrentImage();
/* 2528 */       if (tmp != null)
/* 2529 */         Interpreter.addBatchModeImage(tmp);
/* 2530 */       return;
/*      */     }
/* 2532 */     IJ.showProgress(0, 0);
/* 2533 */     ImagePlus imp2 = WindowManager.getCurrentImage();
/* 2534 */     WindowManager.setTempCurrentImage(null);
/* 2535 */     this.roiManager = null;
/* 2536 */     if (sarg == null) {
/* 2537 */       Interpreter.setBatchMode(false);
/* 2538 */       displayBatchModeImage(imp2);
/*      */     } else {
/* 2540 */       Vector v = Interpreter.imageTable;
/* 2541 */       if (v == null) return;
/* 2542 */       Interpreter.setBatchMode(false);
/* 2543 */       for (int i = 0; i < v.size(); i++) {
/* 2544 */         imp2 = (ImagePlus)v.elementAt(i);
/* 2545 */         if (imp2 != null)
/* 2546 */           displayBatchModeImage(imp2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void displayBatchModeImage(ImagePlus imp2) {
/* 2552 */     if (imp2 != null) {
/* 2553 */       ImageWindow win = imp2.getWindow();
/* 2554 */       if (win == null) {
/* 2555 */         imp2.show();
/*      */       } else {
/* 2557 */         if (!win.isVisible()) win.show();
/* 2558 */         imp2.updateAndDraw();
/*      */       }
/* 2560 */       Roi roi = imp2.getRoi();
/* 2561 */       if (roi != null) imp2.setRoi(roi); 
/*      */     }
/*      */   }
/*      */ 
/*      */   void setLocation()
/*      */   {
/* 2566 */     int x = (int)getFirstArg();
/* 2567 */     int y = (int)getNextArg();
/* 2568 */     int width = 0; int height = 0;
/* 2569 */     if (this.interp.nextToken() == 44) {
/* 2570 */       width = (int)getNextArg();
/* 2571 */       height = (int)getNextArg();
/*      */     }
/* 2573 */     this.interp.getRightParen();
/* 2574 */     if ((width == 0) && (height == 0)) {
/* 2575 */       Frame frame = WindowManager.getFrontWindow();
/* 2576 */       if (frame != null)
/* 2577 */         frame.setLocation(x, y);
/*      */     } else {
/* 2579 */       ImagePlus imp = getImage();
/* 2580 */       ImageWindow win = imp.getWindow();
/* 2581 */       if (win != null)
/* 2582 */         win.setLocationAndSize(x, y, width, height);
/*      */     }
/*      */   }
/*      */ 
/*      */   void setSlice() {
/* 2587 */     int n = (int)getArg();
/* 2588 */     ImagePlus imp = getImage();
/* 2589 */     int nSlices = imp.getStackSize();
/* 2590 */     if ((n == 1) && (nSlices == 1))
/* 2591 */       return;
/* 2592 */     if ((n < 1) || (n > nSlices)) {
/* 2593 */       this.interp.error("Argument must be >=1 and <=" + nSlices);
/*      */     }
/* 2595 */     else if (imp.isHyperStack())
/* 2596 */       imp.setPosition(n);
/*      */     else {
/* 2598 */       imp.setSlice(n);
/*      */     }
/* 2600 */     resetImage();
/*      */   }
/*      */ 
/*      */   void newImage() {
/* 2604 */     String title = getFirstString();
/* 2605 */     String type = getNextString();
/* 2606 */     int width = (int)getNextArg();
/* 2607 */     int height = (int)getNextArg();
/* 2608 */     int depth = (int)getLastArg();
/* 2609 */     if ((width < 1) || (height < 1))
/* 2610 */       this.interp.error("Width or height < 1");
/* 2611 */     IJ.newImage(title, type, width, height, depth);
/* 2612 */     resetImage();
/*      */   }
/*      */ 
/*      */   void saveAs() {
/* 2616 */     String format = getFirstString();
/* 2617 */     String path = null;
/* 2618 */     if (this.interp.nextToken() == 44)
/* 2619 */       path = getLastString();
/*      */     else
/* 2621 */       this.interp.getRightParen();
/* 2622 */     IJ.saveAs(format, path);
/*      */   }
/*      */ 
/*      */   double getZoom() {
/* 2626 */     this.interp.getParens();
/* 2627 */     ImagePlus imp = getImage();
/* 2628 */     ImageCanvas ic = imp.getCanvas();
/* 2629 */     if (ic == null) {
/* 2630 */       this.interp.error("Image not displayed"); return 0.0D;
/*      */     }
/* 2632 */     return ic.getMagnification();
/*      */   }
/*      */ 
/*      */   void setAutoThreshold() {
/* 2636 */     String mString = null;
/* 2637 */     if (this.interp.nextToken() == 40) {
/* 2638 */       this.interp.getLeftParen();
/* 2639 */       if (isStringArg())
/* 2640 */         mString = getString();
/* 2641 */       this.interp.getRightParen();
/*      */     }
/* 2643 */     ImagePlus img = getImage();
/* 2644 */     ImageProcessor ip = getProcessor();
/* 2645 */     if ((ip instanceof ColorProcessor))
/* 2646 */       this.interp.error("Non-RGB image expected");
/* 2647 */     ip.setRoi(img.getRoi());
/* 2648 */     if (mString != null)
/*      */       try {
/* 2650 */         if (mString.indexOf("stack") != -1)
/* 2651 */           IJ.setAutoThreshold(img, mString);
/*      */         else
/* 2653 */           ip.setAutoThreshold(mString);
/*      */       } catch (Exception e) {
/* 2655 */         this.interp.error("" + e.getMessage());
/*      */       }
/*      */     else
/* 2658 */       ip.setAutoThreshold(1, 0);
/* 2659 */     img.updateAndDraw();
/* 2660 */     resetImage();
/*      */   }
/*      */ 
/*      */   double parseDouble(String s) {
/* 2664 */     if (s == null) return 0.0D;
/* 2665 */     s = s.trim();
/* 2666 */     if (s.indexOf(' ') != -1) s = s.substring(0, s.indexOf(' '));
/* 2667 */     return Tools.parseDouble(s);
/*      */   }
/*      */ 
/*      */   double parseInt() {
/* 2671 */     String s = getFirstString();
/* 2672 */     int radix = 10;
/* 2673 */     if (this.interp.nextToken() == 44) {
/* 2674 */       this.interp.getComma();
/* 2675 */       radix = (int)this.interp.getExpression();
/* 2676 */       if ((radix < 2) || (radix > 36)) radix = 10; 
/*      */     }
/* 2678 */     this.interp.getRightParen();
/*      */     double n;
/*      */     try { if (radix == 10) {
/* 2682 */         double n = parseDouble(s);
/* 2683 */         if (!Double.isNaN(n)) n = Math.round(n); 
/*      */       }
/* 2685 */       else { n = Integer.parseInt(s, radix); }
/*      */     } catch (NumberFormatException e) {
/* 2687 */       n = (0.0D / 0.0D);
/*      */     }
/* 2689 */     return n;
/*      */   }
/*      */ 
/*      */   void print() {
/* 2693 */     this.interp.inPrint = true;
/* 2694 */     String s = getFirstString();
/* 2695 */     if (this.interp.nextToken() == 44) {
/* 2696 */       if ((s.startsWith("[")) && (s.endsWith("]"))) {
/* 2697 */         printToWindow(s);
/* 2698 */         return;
/* 2699 */       }if (s.equals("~0~")) {
/* 2700 */         if (this.writer == null)
/* 2701 */           this.interp.error("File not open");
/* 2702 */         String s2 = getLastString();
/* 2703 */         if (s2.endsWith("\n"))
/* 2704 */           this.writer.print(s2);
/*      */         else
/* 2706 */           this.writer.println(s2);
/* 2707 */         this.interp.inPrint = false;
/* 2708 */         return;
/*      */       }
/* 2710 */       StringBuffer sb = new StringBuffer(s);
/*      */       do {
/* 2712 */         sb.append(" ");
/* 2713 */         sb.append(getNextString());
/* 2714 */       }while (this.interp.nextToken() == 44);
/* 2715 */       s = sb.toString();
/*      */     }
/* 2717 */     this.interp.getRightParen();
/* 2718 */     IJ.log(s);
/* 2719 */     this.interp.inPrint = false;
/*      */   }
/*      */ 
/*      */   void printToWindow(String s) {
/* 2723 */     String title = s.substring(1, s.length() - 1);
/* 2724 */     String s2 = getLastString();
/* 2725 */     boolean isCommand = s2.startsWith("\\");
/* 2726 */     Frame frame = WindowManager.getFrame(title);
/* 2727 */     if (frame == null) {
/* 2728 */       if (isCommand) {
/* 2729 */         this.interp.done = true;
/* 2730 */         return;
/*      */       }
/* 2732 */       this.interp.error("Window not found");
/*      */     }
/* 2734 */     boolean isEditor = frame instanceof Editor;
/* 2735 */     if ((!isEditor) && (!(frame instanceof TextWindow)))
/* 2736 */       this.interp.error("Window is not text window");
/* 2737 */     if (isEditor) {
/* 2738 */       Editor ed = (Editor)frame;
/* 2739 */       ed.setIsMacroWindow(true);
/* 2740 */       if (isCommand)
/* 2741 */         handleEditorCommand(ed, s2);
/*      */       else
/* 2743 */         ed.append(s2);
/*      */     } else {
/* 2745 */       TextWindow tw = (TextWindow)frame;
/* 2746 */       if (isCommand)
/* 2747 */         handleTextWindowCommand(tw, s2);
/*      */       else
/* 2749 */         tw.append(s2);
/*      */     }
/*      */   }
/*      */ 
/*      */   void handleEditorCommand(Editor ed, String s) {
/* 2754 */     if (s.startsWith("\\Update:")) {
/* 2755 */       TextArea ta = ed.getTextArea();
/* 2756 */       ta.setText(s.substring(8, s.length()));
/* 2757 */       ta.setEditable(false);
/* 2758 */     } else if (s.equals("\\Close")) {
/* 2759 */       ed.close();
/*      */     } else {
/* 2761 */       ed.append(s);
/*      */     }
/*      */   }
/*      */ 
/* 2765 */   void handleTextWindowCommand(TextWindow tw, String s) { TextPanel tp = tw.getTextPanel();
/* 2766 */     if (s.startsWith("\\Update:")) {
/* 2767 */       int n = tp.getLineCount();
/* 2768 */       String s2 = s.substring(8, s.length());
/* 2769 */       if (n == 0)
/* 2770 */         tp.append(s2);
/*      */       else
/* 2772 */         tp.setLine(n - 1, s2);
/* 2773 */     } else if (s.startsWith("\\Update")) {
/* 2774 */       int cindex = s.indexOf(":");
/* 2775 */       if (cindex == -1) {
/* 2776 */         tp.append(s); return;
/* 2777 */       }String nstr = s.substring(7, cindex);
/* 2778 */       int line = (int)Tools.parseDouble(nstr, -1.0D);
/* 2779 */       if (line < 0) this.interp.error("Row index<0 or NaN");
/* 2780 */       int count = tp.getLineCount();
/* 2781 */       while (line >= count) {
/* 2782 */         tp.append("");
/* 2783 */         count++;
/*      */       }
/* 2785 */       String s2 = s.substring(cindex + 1, s.length());
/* 2786 */       tp.setLine(line, s2);
/* 2787 */     } else if (s.equals("\\Clear")) {
/* 2788 */       tp.clear();
/* 2789 */     } else if (s.equals("\\Close")) {
/* 2790 */       tw.close();
/* 2791 */     } else if (s.startsWith("\\Headings:")) {
/* 2792 */       tp.setColumnHeadings(s.substring(10));
/*      */     } else {
/* 2794 */       tp.append(s);
/*      */     } }
/*      */ 
/*      */   double isKeyDown()
/*      */   {
/* 2799 */     double value = 0.0D;
/* 2800 */     String key = getStringArg().toLowerCase(Locale.US);
/* 2801 */     if (key.indexOf("alt") != -1) value = IJ.altKeyDown() == true ? 1.0D : 0.0D;
/* 2802 */     else if (key.indexOf("shift") != -1) value = IJ.shiftKeyDown() == true ? 1.0D : 0.0D;
/* 2803 */     else if (key.indexOf("space") != -1) value = IJ.spaceBarDown() == true ? 1.0D : 0.0D; else
/* 2804 */       this.interp.error("Invalid key");
/* 2805 */     return value;
/*      */   }
/*      */ 
/*      */   String runMacro(boolean eval) {
/* 2809 */     this.interp.getLeftParen();
/* 2810 */     String name = getString();
/* 2811 */     String arg = null;
/* 2812 */     if (this.interp.nextToken() == 44) {
/* 2813 */       this.interp.getComma();
/* 2814 */       arg = getString();
/*      */     }
/* 2816 */     this.interp.getRightParen();
/* 2817 */     if (eval) {
/* 2818 */       if ((arg != null) && ((name.equals("script")) || (name.equals("js")))) {
/* 2819 */         return new Macro_Runner().runJavaScript(arg, "");
/*      */       }
/* 2821 */       return IJ.runMacro(name, arg);
/*      */     }
/* 2823 */     return IJ.runMacroFile(name, arg);
/*      */   }
/*      */ 
/*      */   void setThreshold() {
/* 2827 */     double lower = getFirstArg();
/* 2828 */     double upper = getNextArg();
/* 2829 */     String mode = null;
/* 2830 */     if (this.interp.nextToken() == 44) {
/* 2831 */       this.interp.getComma();
/* 2832 */       mode = getString();
/*      */     }
/* 2834 */     this.interp.getRightParen();
/* 2835 */     IJ.setThreshold(lower, upper, mode);
/* 2836 */     resetImage();
/*      */   }
/*      */ 
/*      */   void drawOrFill(int type) {
/* 2840 */     int x = (int)getFirstArg();
/* 2841 */     int y = (int)getNextArg();
/* 2842 */     int width = (int)getNextArg();
/* 2843 */     int height = (int)getLastArg();
/* 2844 */     ImageProcessor ip = getProcessor();
/* 2845 */     if (!this.colorSet) setForegroundColor(ip);
/* 2846 */     switch (type) { case 388:
/* 2847 */       ip.drawRect(x, y, width, height); break;
/*      */     case 376:
/* 2848 */       ip.setRoi(x, y, width, height); ip.fill(); break;
/*      */     case 389:
/* 2849 */       ip.drawOval(x, y, width, height); break;
/*      */     case 390:
/* 2850 */       ip.fillOval(x, y, width, height);
/*      */     }
/* 2852 */     updateAndDraw();
/*      */   }
/*      */ 
/*      */   double getScreenDimension(int type) {
/* 2856 */     this.interp.getParens();
/* 2857 */     Dimension screen = IJ.getScreenSize();
/* 2858 */     if (type == 1043) {
/* 2859 */       return screen.width;
/*      */     }
/* 2861 */     return screen.height;
/*      */   }
/*      */ 
/*      */   void getStatistics(boolean calibrated) {
/* 2865 */     Variable count = getFirstVariable();
/* 2866 */     Variable mean = null; Variable min = null; Variable max = null; Variable std = null; Variable hist = null;
/* 2867 */     int params = 19;
/* 2868 */     this.interp.getToken();
/* 2869 */     int arg = 1;
/* 2870 */     while (this.interp.token == 44) {
/* 2871 */       arg++;
/* 2872 */       switch (arg) { case 2:
/* 2873 */         mean = getVariable(); break;
/*      */       case 3:
/* 2874 */         min = getVariable(); break;
/*      */       case 4:
/* 2875 */         max = getVariable(); break;
/*      */       case 5:
/* 2876 */         std = getVariable(); params += 4; break;
/*      */       case 6:
/* 2877 */         hist = getArrayVariable(); break;
/*      */       default:
/* 2878 */         this.interp.error("')' expected");
/*      */       }
/* 2880 */       this.interp.getToken();
/*      */     }
/* 2882 */     if (this.interp.token != 41) this.interp.error("')' expected");
/* 2883 */     ImagePlus imp = getImage();
/* 2884 */     Calibration cal = calibrated ? imp.getCalibration() : null;
/* 2885 */     ImageProcessor ip = getProcessor();
/* 2886 */     ImageStatistics stats = null;
/* 2887 */     Roi roi = imp.getRoi();
/* 2888 */     int lineWidth = Line.getWidth();
/* 2889 */     if ((roi != null) && (roi.isLine()) && (lineWidth > 1))
/*      */     {
/* 2891 */       if (roi.getType() == 5) {
/* 2892 */         ImageProcessor ip2 = ip;
/* 2893 */         Rectangle saveR = ip2.getRoi();
/* 2894 */         ip2.setRoi(roi.getPolygon());
/* 2895 */         stats = ImageStatistics.getStatistics(ip2, params, cal);
/* 2896 */         ip2.setRoi(saveR);
/*      */       } else {
/* 2898 */         ImageProcessor ip2 = new Straightener().straightenLine(imp, lineWidth);
/* 2899 */         stats = ImageStatistics.getStatistics(ip2, params, cal);
/*      */       }
/* 2901 */     } else if ((roi != null) && (roi.isLine())) {
/* 2902 */       ProfilePlot profile = new ProfilePlot(imp);
/* 2903 */       double[] values = profile.getProfile();
/* 2904 */       ImageProcessor ip2 = new FloatProcessor(values.length, 1, values);
/* 2905 */       if ((roi instanceof Line)) {
/* 2906 */         Line l = (Line)roi;
/* 2907 */         if (((l.y1 == l.y2) || (l.x1 == l.x2)) && (l.x1 == l.x1d) && (l.y1 == l.y1d) && (l.x2 == l.x2d) && (l.y2 == l.y2d))
/* 2908 */           ip2.setRoi(0, 0, ip2.getWidth() - 1, 1);
/*      */       }
/* 2910 */       stats = ImageStatistics.getStatistics(ip2, params, cal);
/*      */     } else {
/* 2912 */       ip.setRoi(roi);
/* 2913 */       stats = ImageStatistics.getStatistics(ip, params, cal);
/*      */     }
/* 2915 */     if (calibrated)
/* 2916 */       count.setValue(stats.area);
/*      */     else
/* 2918 */       count.setValue(stats.pixelCount);
/* 2919 */     if (mean != null) mean.setValue(stats.mean);
/* 2920 */     if (min != null) min.setValue(stats.min);
/* 2921 */     if (max != null) max.setValue(stats.max);
/* 2922 */     if (std != null) std.setValue(stats.stdDev);
/* 2923 */     if (hist != null) {
/* 2924 */       boolean is16bit = (!calibrated) && ((ip instanceof ShortProcessor)) && (stats.histogram16 != null);
/* 2925 */       int[] histogram = is16bit ? stats.histogram16 : stats.histogram;
/* 2926 */       int bins = is16bit ? (int)(stats.max + 1.0D) : histogram.length;
/* 2927 */       Variable[] array = new Variable[bins];
/* 2928 */       int hmax = is16bit ? (int)stats.max : 255;
/* 2929 */       for (int i = 0; i <= hmax; i++)
/* 2930 */         array[i] = new Variable(histogram[i]);
/* 2931 */       hist.setArray(array);
/*      */     }
/*      */   }
/*      */ 
/*      */   String replace() {
/* 2936 */     String s1 = getFirstString();
/* 2937 */     String s2 = getNextString();
/* 2938 */     String s3 = getLastString();
/* 2939 */     if ((s2.length() == 1) && (s3.length() == 1))
/* 2940 */       return s1.replace(s2.charAt(0), s3.charAt(0));
/*      */     try
/*      */     {
/* 2943 */       return s1.replaceAll(s2, s3);
/*      */     } catch (Exception e) {
/* 2945 */       this.interp.error("" + e);
/* 2946 */     }return null;
/*      */   }
/*      */ 
/*      */   void floodFill()
/*      */   {
/* 2952 */     int x = (int)getFirstArg();
/* 2953 */     int y = (int)getNextArg();
/* 2954 */     boolean fourConnected = true;
/* 2955 */     if (this.interp.nextToken() == 44) {
/* 2956 */       String s = getLastString();
/* 2957 */       if (s.indexOf("8") != -1)
/* 2958 */         fourConnected = false;
/*      */     } else {
/* 2960 */       this.interp.getRightParen();
/* 2961 */     }ImageProcessor ip = getProcessor();
/* 2962 */     if (!this.colorSet) setForegroundColor(ip);
/* 2963 */     FloodFiller ff = new FloodFiller(ip);
/* 2964 */     if (fourConnected)
/* 2965 */       ff.fill(x, y);
/*      */     else
/* 2967 */       ff.fill8(x, y);
/* 2968 */     updateAndDraw();
/* 2969 */     if ((Recorder.record) && (this.pgm.hasVars))
/* 2970 */       Recorder.record("floodFill", x, y);
/*      */   }
/*      */ 
/*      */   void restorePreviousTool() {
/* 2974 */     this.interp.getParens();
/* 2975 */     Toolbar tb = Toolbar.getInstance();
/* 2976 */     if (tb != null) tb.restorePreviousTool(); 
/*      */   }
/*      */ 
/*      */   void setVoxelSize()
/*      */   {
/* 2980 */     double width = getFirstArg();
/* 2981 */     double height = getNextArg();
/* 2982 */     double depth = getNextArg();
/* 2983 */     String unit = getLastString();
/* 2984 */     ImagePlus imp = getImage();
/* 2985 */     Calibration cal = imp.getCalibration();
/* 2986 */     cal.pixelWidth = width;
/* 2987 */     cal.pixelHeight = height;
/* 2988 */     cal.pixelDepth = depth;
/* 2989 */     cal.setUnit(unit);
/* 2990 */     imp.repaintWindow();
/*      */   }
/*      */ 
/*      */   void getLocationAndSize() {
/* 2994 */     Variable v1 = getFirstVariable();
/* 2995 */     Variable v2 = getNextVariable();
/* 2996 */     Variable v3 = getNextVariable();
/* 2997 */     Variable v4 = getLastVariable();
/* 2998 */     ImagePlus imp = getImage();
/* 2999 */     int x = 0; int y = 0; int w = 0; int h = 0;
/* 3000 */     ImageWindow win = imp.getWindow();
/* 3001 */     if (win != null) {
/* 3002 */       Point loc = win.getLocation();
/* 3003 */       Dimension size = win.getSize();
/* 3004 */       x = loc.x; y = loc.y; w = size.width; h = size.height;
/*      */     }
/* 3006 */     v1.setValue(x);
/* 3007 */     v2.setValue(y);
/* 3008 */     v3.setValue(w);
/* 3009 */     v4.setValue(h);
/*      */   }
/*      */ 
/*      */   String doDialog() {
/* 3013 */     this.interp.getToken();
/* 3014 */     if (this.interp.token != 46)
/* 3015 */       this.interp.error("'.' expected");
/* 3016 */     this.interp.getToken();
/* 3017 */     if ((this.interp.token != 129) && (this.interp.token != 136) && (this.interp.token != 135))
/* 3018 */       this.interp.error("Function name expected: ");
/* 3019 */     String name = this.interp.tokenString;
/*      */     try {
/* 3021 */       if (name.equals("create")) {
/* 3022 */         this.gd = new GenericDialog(getStringArg());
/* 3023 */         return null;
/*      */       }
/* 3025 */       if (this.gd == null) {
/* 3026 */         this.interp.error("No dialog created with Dialog.create()");
/* 3027 */         return null;
/*      */       }
/* 3029 */       if (name.equals("addString")) {
/* 3030 */         String label = getFirstString();
/* 3031 */         String defaultStr = getNextString();
/* 3032 */         int columns = 8;
/* 3033 */         if (this.interp.nextToken() == 44)
/* 3034 */           columns = (int)getNextArg();
/* 3035 */         this.interp.getRightParen();
/* 3036 */         this.gd.addStringField(label, defaultStr, columns);
/* 3037 */       } else if (name.equals("addNumber")) {
/* 3038 */         int columns = 6;
/* 3039 */         String units = null;
/* 3040 */         String prompt = getFirstString();
/* 3041 */         double defaultNumber = getNextArg();
/* 3042 */         int decimalPlaces = (int)defaultNumber == defaultNumber ? 0 : 3;
/* 3043 */         if (this.interp.nextToken() == 44) {
/* 3044 */           decimalPlaces = (int)getNextArg();
/* 3045 */           columns = (int)getNextArg();
/* 3046 */           units = getLastString();
/*      */         } else {
/* 3048 */           this.interp.getRightParen();
/* 3049 */         }this.gd.addNumericField(prompt, defaultNumber, decimalPlaces, columns, units);
/* 3050 */       } else if (name.equals("addSlider")) {
/* 3051 */         String label = getFirstString();
/* 3052 */         double minValue = getNextArg();
/* 3053 */         double maxValue = getNextArg();
/* 3054 */         double defaultValue = getLastArg();
/* 3055 */         this.gd.addSlider(label, minValue, maxValue, defaultValue);
/* 3056 */       } else if (name.equals("addCheckbox")) {
/* 3057 */         this.gd.addCheckbox(getFirstString(), getLastArg() == 1.0D);
/* 3058 */       } else if (name.equals("addCheckboxGroup")) {
/* 3059 */         addCheckboxGroup(this.gd);
/* 3060 */       } else if (name.equals("addMessage")) {
/* 3061 */         this.gd.addMessage(getStringArg());
/* 3062 */       } else if (name.equals("addHelp")) {
/* 3063 */         this.gd.addHelp(getStringArg());
/* 3064 */       } else if (name.equals("addChoice")) {
/* 3065 */         String prompt = getFirstString();
/* 3066 */         this.interp.getComma();
/* 3067 */         String[] choices = getStringArray();
/* 3068 */         String defaultChoice = null;
/* 3069 */         if (this.interp.nextToken() == 44) {
/* 3070 */           this.interp.getComma();
/* 3071 */           defaultChoice = getString();
/*      */         } else {
/* 3073 */           defaultChoice = choices[0];
/* 3074 */         }this.interp.getRightParen();
/* 3075 */         this.gd.addChoice(prompt, choices, defaultChoice);
/* 3076 */       } else if (name.equals("setInsets")) {
/* 3077 */         this.gd.setInsets((int)getFirstArg(), (int)getNextArg(), (int)getLastArg());
/* 3078 */       } else if (name.equals("show")) {
/* 3079 */         this.interp.getParens();
/* 3080 */         this.gd.showDialog();
/* 3081 */         if (this.gd.wasCanceled()) {
/* 3082 */           this.interp.finishUp();
/* 3083 */           throw new RuntimeException("Macro canceled");
/*      */         }
/*      */       } else { if (name.equals("getString")) {
/* 3086 */           this.interp.getParens();
/* 3087 */           return this.gd.getNextString();
/* 3088 */         }if (name.equals("getNumber")) {
/* 3089 */           this.interp.getParens();
/* 3090 */           return "" + this.gd.getNextNumber();
/* 3091 */         }if (name.equals("getCheckbox")) {
/* 3092 */           this.interp.getParens();
/* 3093 */           return this.gd.getNextBoolean() == true ? "1" : "0";
/* 3094 */         }if (name.equals("getChoice")) {
/* 3095 */           this.interp.getParens();
/* 3096 */           return this.gd.getNextChoice();
/*      */         }
/* 3098 */         this.interp.error("Unrecognized Dialog function " + name); }
/*      */     } catch (IndexOutOfBoundsException e) {
/* 3100 */       this.interp.error("Dialog error");
/*      */     }
/* 3102 */     return null;
/*      */   }
/*      */ 
/*      */   void addCheckboxGroup(GenericDialog gd) {
/* 3106 */     int rows = (int)getFirstArg();
/* 3107 */     int columns = (int)getNextArg();
/* 3108 */     this.interp.getComma();
/* 3109 */     String[] labels = getStringArray();
/* 3110 */     int n = labels.length;
/* 3111 */     double[] dstates = getLastArray();
/* 3112 */     if (n != dstates.length)
/* 3113 */       this.interp.error("labels.length!=states.length");
/* 3114 */     boolean[] states = new boolean[n];
/* 3115 */     for (int i = 0; i < n; i++)
/* 3116 */       states[i] = (dstates[i] == 1.0D ? 1 : false);
/* 3117 */     gd.addCheckboxGroup(rows, columns, labels, states);
/*      */   }
/*      */ 
/*      */   void getDateAndTime() {
/* 3121 */     Variable year = getFirstVariable();
/* 3122 */     Variable month = getNextVariable();
/* 3123 */     Variable dayOfWeek = getNextVariable();
/* 3124 */     Variable dayOfMonth = getNextVariable();
/* 3125 */     Variable hour = getNextVariable();
/* 3126 */     Variable minute = getNextVariable();
/* 3127 */     Variable second = getNextVariable();
/* 3128 */     Variable millisecond = getLastVariable();
/* 3129 */     Calendar date = Calendar.getInstance();
/* 3130 */     year.setValue(date.get(1));
/* 3131 */     month.setValue(date.get(2));
/* 3132 */     dayOfWeek.setValue(date.get(7) - 1);
/* 3133 */     dayOfMonth.setValue(date.get(5));
/* 3134 */     hour.setValue(date.get(11));
/* 3135 */     minute.setValue(date.get(12));
/* 3136 */     second.setValue(date.get(13));
/* 3137 */     millisecond.setValue(date.get(14));
/*      */   }
/*      */ 
/*      */   void setMetadata() {
/* 3141 */     String metadata = null;
/* 3142 */     String arg1 = getFirstString();
/* 3143 */     boolean oneArg = false;
/* 3144 */     if (this.interp.nextToken() == 44)
/* 3145 */       metadata = getLastString();
/*      */     else
/* 3147 */       this.interp.getRightParen();
/* 3148 */     boolean isInfo = false;
/* 3149 */     if (metadata == null) {
/* 3150 */       metadata = arg1;
/* 3151 */       oneArg = true;
/* 3152 */       if (metadata.startsWith("Info:")) {
/* 3153 */         metadata = metadata.substring(5);
/* 3154 */         isInfo = true;
/*      */       }
/*      */     } else {
/* 3157 */       isInfo = (arg1.startsWith("info")) || (arg1.startsWith("Info"));
/* 3158 */     }ImagePlus imp = getImage();
/* 3159 */     if (isInfo) {
/* 3160 */       imp.setProperty("Info", metadata);
/*      */     }
/* 3162 */     else if (imp.getStackSize() == 1) {
/* 3163 */       if (oneArg) {
/* 3164 */         imp.setProperty("Info", metadata);
/*      */       } else {
/* 3166 */         imp.setProperty("Label", metadata);
/* 3167 */         if (!Interpreter.isBatchMode()) imp.repaintWindow(); 
/*      */       }
/*      */     }
/* 3170 */     else { imp.getStack().setSliceLabel(metadata, imp.getCurrentSlice());
/* 3171 */       if (!Interpreter.isBatchMode()) imp.repaintWindow();
/*      */     }
/*      */   }
/*      */ 
/*      */   String getMetadata()
/*      */   {
/* 3177 */     String type = "label";
/* 3178 */     boolean noArg = true;
/* 3179 */     if ((this.interp.nextToken() == 40) && (this.interp.nextNextToken() != 41)) {
/* 3180 */       type = getStringArg().toLowerCase(Locale.US);
/* 3181 */       noArg = false;
/*      */     } else {
/* 3183 */       this.interp.getParens();
/* 3184 */     }ImagePlus imp = getImage();
/* 3185 */     String metadata = null;
/* 3186 */     if (type.indexOf("label") != -1) {
/* 3187 */       if (imp.getStackSize() == 1) {
/* 3188 */         metadata = (String)imp.getProperty("Label");
/* 3189 */         if ((metadata == null) && (noArg))
/* 3190 */           metadata = (String)imp.getProperty("Info");
/*      */       } else {
/* 3192 */         metadata = imp.getStack().getSliceLabel(imp.getCurrentSlice());
/*      */       }
/*      */     } else metadata = (String)imp.getProperty("Info");
/* 3195 */     if (metadata == null) metadata = "";
/* 3196 */     return metadata;
/*      */   }
/*      */ 
/*      */   ImagePlus getImageArg() {
/* 3200 */     ImagePlus img = null;
/* 3201 */     if (isStringArg()) {
/* 3202 */       String title = getString();
/* 3203 */       img = WindowManager.getImage(title);
/*      */     } else {
/* 3205 */       int id = (int)this.interp.getExpression();
/* 3206 */       img = WindowManager.getImage(id);
/*      */     }
/* 3208 */     if (img == null) this.interp.error("Image not found");
/* 3209 */     return img;
/*      */   }
/*      */ 
/*      */   void imageCalculator() {
/* 3213 */     String operator = getFirstString();
/* 3214 */     this.interp.getComma();
/* 3215 */     ImagePlus img1 = getImageArg();
/* 3216 */     this.interp.getComma();
/* 3217 */     ImagePlus img2 = getImageArg();
/* 3218 */     this.interp.getRightParen();
/* 3219 */     ImageCalculator ic = new ImageCalculator();
/* 3220 */     ic.calculate(operator, img1, img2);
/* 3221 */     resetImage();
/*      */   }
/*      */ 
/*      */   void setRGBWeights() {
/* 3225 */     double r = getFirstArg();
/* 3226 */     double g = getNextArg();
/* 3227 */     double b = getLastArg();
/* 3228 */     if (this.interp.rgbWeights == null)
/* 3229 */       this.interp.rgbWeights = ColorProcessor.getWeightingFactors();
/* 3230 */     ColorProcessor.setWeightingFactors(r, g, b);
/*      */   }
/*      */ 
/*      */   void makePolygon() {
/* 3234 */     int max = 200;
/* 3235 */     int[] x = new int[max];
/* 3236 */     int[] y = new int[max];
/* 3237 */     x[0] = ((int)Math.round(getFirstArg()));
/* 3238 */     y[0] = ((int)Math.round(getNextArg()));
/* 3239 */     this.interp.getToken();
/* 3240 */     int n = 1;
/* 3241 */     while ((this.interp.token == 44) && (n < max)) {
/* 3242 */       x[n] = ((int)Math.round(this.interp.getExpression()));
/* 3243 */       this.interp.getComma();
/* 3244 */       y[n] = ((int)Math.round(this.interp.getExpression()));
/* 3245 */       this.interp.getToken();
/* 3246 */       n++;
/*      */     }
/* 3248 */     if (n < 3)
/* 3249 */       this.interp.error("Fewer than 3 points");
/* 3250 */     if ((n == max) && (this.interp.token != 41))
/* 3251 */       this.interp.error("More than " + max + " points");
/* 3252 */     ImagePlus imp = getImage();
/* 3253 */     Roi previousRoi = imp.getRoi();
/* 3254 */     if ((this.shiftKeyDown) || (this.altKeyDown)) imp.saveRoi();
/* 3255 */     imp.setRoi(new PolygonRoi(x, y, n, 2));
/* 3256 */     Roi roi = imp.getRoi();
/* 3257 */     if ((previousRoi != null) && (roi != null))
/* 3258 */       updateRoi(roi);
/* 3259 */     resetImage();
/*      */   }
/*      */ 
/*      */   void updateRoi(Roi roi) {
/* 3263 */     if ((this.shiftKeyDown) || (this.altKeyDown))
/* 3264 */       roi.update(this.shiftKeyDown, this.altKeyDown);
/* 3265 */     this.shiftKeyDown = (this.altKeyDown = 0);
/*      */   }
/*      */ 
/*      */   String doFile() {
/* 3269 */     this.interp.getToken();
/* 3270 */     if (this.interp.token != 46)
/* 3271 */       this.interp.error("'.' expected");
/* 3272 */     this.interp.getToken();
/* 3273 */     if ((this.interp.token != 129) && (this.interp.token != 136) && (this.interp.token != 135) && (this.interp.token != 134))
/* 3274 */       this.interp.error("Function name expected: ");
/* 3275 */     String name = this.interp.tokenString;
/* 3276 */     if (name.equals("open"))
/* 3277 */       return openFile();
/* 3278 */     if (name.equals("openAsString"))
/* 3279 */       return openAsString(false);
/* 3280 */     if (name.equals("openAsRawString"))
/* 3281 */       return openAsString(true);
/* 3282 */     if (name.equals("openUrlAsString"))
/* 3283 */       return IJ.openUrlAsString(getStringArg());
/* 3284 */     if (name.equals("openDialog"))
/* 3285 */       return openDialog();
/* 3286 */     if (name.equals("close"))
/* 3287 */       return closeFile();
/* 3288 */     if (name.equals("separator")) {
/* 3289 */       this.interp.getParens();
/* 3290 */       return File.separator;
/* 3291 */     }if (name.equals("directory")) {
/* 3292 */       this.interp.getParens();
/* 3293 */       String lastDir = OpenDialog.getLastDirectory();
/* 3294 */       return lastDir != null ? lastDir : "";
/* 3295 */     }if (name.equals("name")) {
/* 3296 */       this.interp.getParens();
/* 3297 */       String lastName = OpenDialog.getLastName();
/* 3298 */       return lastName != null ? lastName : "";
/* 3299 */     }if (name.equals("nameWithoutExtension")) {
/* 3300 */       this.interp.getParens();
/* 3301 */       return nameWithoutExtension();
/* 3302 */     }if (name.equals("rename")) {
/* 3303 */       File f1 = new File(getFirstString());
/* 3304 */       File f2 = new File(getLastString());
/* 3305 */       if ((checkPath(f1)) && (checkPath(f2))) {
/* 3306 */         return f1.renameTo(f2) ? "1" : "0";
/*      */       }
/* 3308 */       return "0";
/* 3309 */     }if (name.equals("append")) {
/* 3310 */       String err = IJ.append(getFirstString(), getLastString());
/* 3311 */       if (err != null) this.interp.error(err);
/* 3312 */       return null;
/* 3313 */     }if (name.equals("saveString")) {
/* 3314 */       String err = IJ.saveString(getFirstString(), getLastString());
/* 3315 */       if (err != null) this.interp.error(err);
/* 3316 */       return null;
/*      */     }
/* 3318 */     File f = new File(getStringArg());
/* 3319 */     if ((name.equals("getLength")) || (name.equals("length")))
/* 3320 */       return "" + f.length();
/* 3321 */     if (name.equals("getName"))
/* 3322 */       return f.getName();
/* 3323 */     if (name.equals("getAbsolutePath"))
/* 3324 */       return f.getAbsolutePath();
/* 3325 */     if (name.equals("getParent"))
/* 3326 */       return f.getParent();
/* 3327 */     if (name.equals("exists"))
/* 3328 */       return f.exists() ? "1" : "0";
/* 3329 */     if (name.equals("isDirectory"))
/* 3330 */       return f.isDirectory() ? "1" : "0";
/* 3331 */     if ((name.equals("makeDirectory")) || (name.equals("mkdir"))) {
/* 3332 */       f.mkdir(); return null;
/* 3333 */     }if (name.equals("lastModified"))
/* 3334 */       return "" + f.lastModified();
/* 3335 */     if (name.equals("dateLastModified"))
/* 3336 */       return new Date(f.lastModified()).toString();
/* 3337 */     if (name.equals("delete")) {
/* 3338 */       return f.delete() ? "1" : "0";
/*      */     }
/* 3340 */     this.interp.error("Unrecognized File function " + name);
/* 3341 */     return null;
/*      */   }
/*      */ 
/*      */   String nameWithoutExtension() {
/* 3345 */     String name = OpenDialog.getLastName();
/* 3346 */     if (name == null) return "";
/* 3347 */     int dotIndex = name.lastIndexOf(".");
/* 3348 */     if ((dotIndex >= 0) && (name.length() - dotIndex <= 5))
/* 3349 */       name = name.substring(0, dotIndex);
/* 3350 */     return name;
/*      */   }
/*      */ 
/*      */   boolean checkPath(File f) {
/* 3354 */     String path = f.getPath();
/* 3355 */     if ((path.equals("0")) || (path.equals("NaN"))) {
/* 3356 */       this.interp.error("Invalid path");
/* 3357 */       return false;
/*      */     }
/* 3359 */     return true;
/*      */   }
/*      */ 
/*      */   String openDialog() {
/* 3363 */     String title = getStringArg();
/* 3364 */     OpenDialog od = new OpenDialog(title, null);
/* 3365 */     String directory = od.getDirectory();
/* 3366 */     String name = od.getFileName();
/* 3367 */     if (name == null) {
/* 3368 */       return "";
/*      */     }
/* 3370 */     return directory + name;
/*      */   }
/*      */ 
/*      */   void setSelectionName() {
/* 3374 */     Roi roi = getImage().getRoi();
/* 3375 */     if (roi == null)
/* 3376 */       this.interp.error("No selection");
/*      */     else
/* 3378 */       roi.setName(getStringArg());
/*      */   }
/*      */ 
/*      */   String selectionName() {
/* 3382 */     Roi roi = getImage().getRoi();
/* 3383 */     String name = null;
/* 3384 */     if (roi == null)
/* 3385 */       this.interp.error("No selection");
/*      */     else
/* 3387 */       name = roi.getName();
/* 3388 */     return name != null ? name : "";
/*      */   }
/*      */ 
/*      */   String openFile() {
/* 3392 */     if (this.writer != null) {
/* 3393 */       this.interp.error("Currently, only one file can be open at a time");
/* 3394 */       return "";
/*      */     }
/* 3396 */     String path = getFirstString();
/* 3397 */     String defaultName = null;
/* 3398 */     if (this.interp.nextToken() == 41)
/* 3399 */       this.interp.getRightParen();
/*      */     else
/* 3401 */       defaultName = getLastString();
/* 3402 */     if ((path.equals("")) || (defaultName != null)) {
/* 3403 */       String title = defaultName != null ? path : "openFile";
/* 3404 */       defaultName = defaultName != null ? defaultName : "log.txt";
/* 3405 */       SaveDialog sd = new SaveDialog(title, defaultName, ".txt");
/* 3406 */       if (sd.getFileName() == null) return "";
/* 3407 */       path = sd.getDirectory() + sd.getFileName();
/*      */     } else {
/* 3409 */       File file = new File(path);
/* 3410 */       if ((file.exists()) && (!path.endsWith(".txt")) && (!path.endsWith(".java")) && (!path.endsWith(".xls")) && (!path.endsWith(".ijm")) && (!path.endsWith(".html")) && (!path.endsWith(".htm")))
/*      */       {
/* 3412 */         this.interp.error("File exists and suffix is not '.txt', '.java', etc.");
/*      */       }
/*      */     }
/*      */     try { FileOutputStream fos = new FileOutputStream(path);
/* 3416 */       BufferedOutputStream bos = new BufferedOutputStream(fos);
/* 3417 */       this.writer = new PrintWriter(bos);
/*      */     } catch (IOException e)
/*      */     {
/* 3420 */       this.interp.error("File open error \n\"" + e.getMessage() + "\"\n");
/* 3421 */       return "";
/*      */     }
/* 3423 */     return "~0~";
/*      */   }
/*      */ 
/*      */   String openAsString(boolean raw) {
/* 3427 */     int max = 5000;
/* 3428 */     String path = getFirstString();
/* 3429 */     boolean specifiedMax = false;
/* 3430 */     if ((raw) && (this.interp.nextToken() == 44)) {
/* 3431 */       max = (int)getNextArg();
/* 3432 */       specifiedMax = true;
/*      */     }
/* 3434 */     this.interp.getRightParen();
/* 3435 */     if (path.equals("")) {
/* 3436 */       OpenDialog od = new OpenDialog("Open As String", "");
/* 3437 */       String directory = od.getDirectory();
/* 3438 */       String name = od.getFileName();
/* 3439 */       if (name == null) return "";
/* 3440 */       path = directory + name;
/*      */     }
/* 3442 */     String str = "";
/* 3443 */     File file = new File(path);
/* 3444 */     if (!file.exists())
/* 3445 */       this.interp.error("File not found");
/*      */     try {
/* 3447 */       StringBuffer sb = new StringBuffer(5000);
/* 3448 */       if (raw) {
/* 3449 */         int len = (int)file.length();
/* 3450 */         if ((max > len) || ((path.endsWith(".txt")) && (!specifiedMax)))
/* 3451 */           max = len;
/* 3452 */         InputStream in = new BufferedInputStream(new FileInputStream(path));
/* 3453 */         DataInputStream dis = new DataInputStream(in);
/* 3454 */         byte[] buffer = new byte[max];
/* 3455 */         dis.readFully(buffer);
/* 3456 */         dis.close();
/* 3457 */         char[] buffer2 = new char[buffer.length];
/* 3458 */         for (int i = 0; i < buffer.length; i++)
/* 3459 */           buffer2[i] = ((char)(buffer[i] & 0xFF));
/* 3460 */         str = new String(buffer2);
/*      */       } else {
/* 3462 */         BufferedReader r = new BufferedReader(new FileReader(file));
/*      */         while (true) {
/* 3464 */           String s = r.readLine();
/* 3465 */           if (s == null) {
/*      */             break;
/*      */           }
/* 3468 */           sb.append(s + "\n");
/*      */         }
/* 3470 */         r.close();
/* 3471 */         str = new String(sb);
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/* 3475 */       this.interp.error("File open error \n\"" + e.getMessage() + "\"\n");
/*      */     }
/* 3477 */     return str;
/*      */   }
/*      */ 
/*      */   String closeFile() {
/* 3481 */     String f = getStringArg();
/* 3482 */     if (!f.equals("~0~"))
/* 3483 */       this.interp.error("Invalid file variable");
/* 3484 */     if (this.writer != null) {
/* 3485 */       this.writer.close();
/* 3486 */       this.writer = null;
/*      */     }
/* 3488 */     return null;
/*      */   }
/*      */ 
/*      */   String call()
/*      */   {
/* 3496 */     String fullName = getFirstString();
/* 3497 */     int dot = fullName.lastIndexOf('.');
/* 3498 */     if (dot < 0) {
/* 3499 */       this.interp.error("'classname.methodname' expected");
/* 3500 */       return null;
/*      */     }
/* 3502 */     String className = fullName.substring(0, dot);
/* 3503 */     String methodName = fullName.substring(dot + 1);
/*      */ 
/* 3506 */     Object[] args = null;
/* 3507 */     if (this.interp.nextToken() == 44) {
/* 3508 */       Vector vargs = new Vector();
/*      */       do
/* 3510 */         vargs.add(getNextString());
/* 3511 */       while (this.interp.nextToken() == 44);
/* 3512 */       args = vargs.toArray();
/*      */     }
/* 3514 */     this.interp.getRightParen();
/* 3515 */     if (args == null) args = new Object[0];
/*      */ 
/*      */     Class c;
/*      */     try
/*      */     {
/* 3520 */       c = IJ.getClassLoader().loadClass(className);
/*      */     } catch (Exception ex) {
/* 3522 */       this.interp.error("Could not load class " + className);
/* 3523 */       return null;
/*      */     }
/*      */ 
/*      */     Method m;
/*      */     try
/*      */     {
/* 3529 */       Class[] argClasses = null;
/* 3530 */       if (args.length > 0) {
/* 3531 */         argClasses = new Class[args.length];
/* 3532 */         for (int i = 0; i < args.length; i++)
/* 3533 */           argClasses[i] = args[i].getClass();
/*      */       }
/* 3535 */       m = c.getMethod(methodName, argClasses);
/*      */     } catch (Exception ex) {
/* 3537 */       m = null;
/*      */     }
/* 3539 */     if ((m == null) && (args.length > 0)) {
/*      */       try {
/* 3541 */         Class[] argClasses = new Class[args.length];
/* 3542 */         for (int i = 0; i < args.length; i++) {
/* 3543 */           double value = Tools.parseDouble((String)args[i]);
/* 3544 */           if (!Double.isNaN(value)) {
/* 3545 */             args[i] = new Integer((int)value);
/* 3546 */             argClasses[i] = Integer.TYPE;
/*      */           } else {
/* 3548 */             argClasses[i] = args[i].getClass();
/*      */           }
/*      */         }
/* 3550 */         m = c.getMethod(methodName, argClasses);
/*      */       } catch (Exception ex) {
/* 3552 */         m = null;
/*      */       }
/*      */     }
/* 3555 */     if (m == null) {
/* 3556 */       this.interp.error("Could not find the method " + methodName + " with " + args.length + " parameter(s) in class " + className);
/*      */     }
/*      */     try
/*      */     {
/* 3560 */       Object obj = m.invoke(null, args);
/* 3561 */       return obj != null ? obj.toString() : null;
/*      */     } catch (InvocationTargetException e) {
/* 3563 */       CharArrayWriter caw = new CharArrayWriter();
/* 3564 */       PrintWriter pw = new PrintWriter(caw);
/* 3565 */       e.getCause().printStackTrace(pw);
/* 3566 */       String s = caw.toString();
/* 3567 */       if (IJ.getInstance() != null)
/* 3568 */         new TextWindow("Exception", s, 400, 400);
/*      */       else
/* 3570 */         IJ.log(s);
/* 3571 */       return null;
/*      */     } catch (Exception e) {
/* 3573 */       IJ.log("Call error (" + e + ")");
/* 3574 */     }return null;
/*      */   }
/*      */ 
/*      */   Variable[] getFontList()
/*      */   {
/* 3580 */     this.interp.getParens();
/* 3581 */     String[] fonts = null;
/* 3582 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 3583 */     fonts = ge.getAvailableFontFamilyNames();
/* 3584 */     if (fonts == null) return null;
/* 3585 */     Variable[] array = new Variable[fonts.length];
/* 3586 */     for (int i = 0; i < fonts.length; i++)
/* 3587 */       array[i] = new Variable(0, 0.0D, fonts[i]);
/* 3588 */     return array;
/*      */   }
/*      */ 
/*      */   void setOption() {
/* 3592 */     String arg1 = getFirstString();
/* 3593 */     boolean state = true;
/* 3594 */     if (this.interp.nextToken() == 44) {
/* 3595 */       this.interp.getComma();
/* 3596 */       double arg2 = this.interp.getBooleanExpression();
/* 3597 */       this.interp.checkBoolean(arg2);
/* 3598 */       state = arg2 != 0.0D;
/*      */     }
/* 3600 */     this.interp.getRightParen();
/* 3601 */     arg1 = arg1.toLowerCase(Locale.US);
/* 3602 */     if (arg1.equals("disablepopupmenu")) {
/* 3603 */       ImageCanvas ic = getImage().getCanvas();
/* 3604 */       if (ic != null) ic.disablePopupMenu(state); 
/*      */     }
/* 3605 */     else if (arg1.startsWith("show all")) {
/* 3606 */       ImagePlus img = getImage();
/* 3607 */       ImageCanvas ic = img.getCanvas();
/* 3608 */       if (ic != null) {
/* 3609 */         boolean previousState = ic.getShowAllROIs();
/* 3610 */         ic.setShowAllROIs(state);
/* 3611 */         if (state != previousState) img.draw(); 
/*      */       }
/*      */     }
/* 3613 */     else if (arg1.equals("changes")) {
/* 3614 */       getImage().changes = state;
/* 3615 */     } else if (arg1.equals("debugmode")) {
/* 3616 */       IJ.debugMode = state;
/* 3617 */     } else if (arg1.equals("openusingplugins")) {
/* 3618 */       Opener.setOpenUsingPlugins(state);
/* 3619 */     } else if (arg1.equals("queuemacros")) {
/* 3620 */       this.pgm.queueCommands = state;
/* 3621 */     } else if (arg1.equals("disableundo")) {
/* 3622 */       Prefs.disableUndo = state;
/* 3623 */     } else if (arg1.startsWith("openashyper")) {
/* 3624 */       getImage().setOpenAsHyperStack(true);
/* 3625 */     } else if (arg1.startsWith("black")) {
/* 3626 */       Prefs.blackBackground = state;
/* 3627 */     } else if (arg1.startsWith("display lab")) {
/* 3628 */       Analyzer.setMeasurement(1024, state);
/* 3629 */     } else if (arg1.startsWith("limit to")) {
/* 3630 */       Analyzer.setMeasurement(256, state);
/* 3631 */     } else if (arg1.equals("area")) {
/* 3632 */       Analyzer.setMeasurement(1, state);
/* 3633 */     } else if (arg1.equals("mean")) {
/* 3634 */       Analyzer.setMeasurement(2, state);
/* 3635 */     } else if (arg1.startsWith("std")) {
/* 3636 */       Analyzer.setMeasurement(4, state);
/* 3637 */     } else if (arg1.equals("showrownumbers")) {
/* 3638 */       ResultsTable.getResultsTable().showRowNumbers(state);
/* 3639 */     } else if (arg1.startsWith("show")) {
/* 3640 */       Analyzer.setOption(arg1, state);
/* 3641 */     } else if (arg1.startsWith("bicubic")) {
/* 3642 */       ImageProcessor.setUseBicubic(state);
/* 3643 */     } else if ((arg1.startsWith("wand")) || (arg1.indexOf("points") != -1)) {
/* 3644 */       Wand.setAllPoints(state);
/* 3645 */     } else if (arg1.startsWith("expandablearrays")) {
/* 3646 */       this.expandableArrays = state;
/* 3647 */     } else if (arg1.startsWith("loop")) {
/* 3648 */       Calibration.setLoopBackAndForth(state);
/*      */     } else {
/* 3650 */       this.interp.error("Invalid option");
/*      */     }
/*      */   }
/*      */ 
/*      */   void setMeasurementOption(String option) {
/*      */   }
/*      */ 
/* 3657 */   void showText() { String title = getFirstString();
/* 3658 */     String text = getLastString();
/* 3659 */     Editor ed = new Editor();
/* 3660 */     ed.setSize(350, 300);
/* 3661 */     ed.create(title, text); }
/*      */ 
/*      */   Variable[] newMenu()
/*      */   {
/* 3665 */     String name = getFirstString();
/* 3666 */     this.interp.getComma();
/* 3667 */     String[] commands = getStringArray();
/* 3668 */     this.interp.getRightParen();
/* 3669 */     if (this.pgm.menus == null)
/* 3670 */       this.pgm.menus = new Hashtable();
/* 3671 */     this.pgm.menus.put(name, commands);
/* 3672 */     Variable[] commands2 = new Variable[commands.length];
/* 3673 */     for (int i = 0; i < commands.length; i++)
/* 3674 */       commands2[i] = new Variable(0, 0.0D, commands[i]);
/* 3675 */     return commands2;
/*      */   }
/*      */ 
/*      */   void setSelectionLocation() {
/* 3679 */     int x = (int)Math.round(getFirstArg());
/* 3680 */     int y = (int)Math.round(getLastArg());
/* 3681 */     ImagePlus imp = getImage();
/* 3682 */     Roi roi = imp.getRoi();
/* 3683 */     if (roi == null)
/* 3684 */       this.interp.error("Selection required");
/* 3685 */     roi.setLocation(x, y);
/* 3686 */     imp.draw();
/*      */   }
/*      */ 
/*      */   double is() {
/* 3690 */     boolean state = false;
/* 3691 */     String arg = getStringArg();
/* 3692 */     arg = arg.toLowerCase(Locale.US);
/* 3693 */     if (arg.equals("locked")) {
/* 3694 */       state = getImage().isLocked();
/* 3695 */     } else if (arg.indexOf("invert") != -1) {
/* 3696 */       state = getImage().isInvertedLut();
/* 3697 */     } else if (arg.indexOf("hyper") != -1) {
/* 3698 */       state = getImage().isHyperStack();
/* 3699 */     } else if (arg.indexOf("batch") != -1) {
/* 3700 */       state = Interpreter.isBatchMode();
/* 3701 */     } else if (arg.indexOf("applet") != -1) {
/* 3702 */       state = IJ.getApplet() != null;
/* 3703 */     } else if (arg.indexOf("virtual") != -1) {
/* 3704 */       state = getImage().getStack().isVirtual();
/* 3705 */     } else if (arg.indexOf("composite") != -1) {
/* 3706 */       state = getImage().isComposite();
/* 3707 */     } else if (arg.indexOf("caps") != -1) {
/* 3708 */       state = getCapsLockState();
/* 3709 */     } else if (arg.indexOf("changes") != -1) {
/* 3710 */       state = getImage().changes;
/* 3711 */     } else if (arg.indexOf("binary") != -1) {
/* 3712 */       state = getProcessor().isBinary();
/* 3713 */     } else if (arg.indexOf("animated") != -1) {
/* 3714 */       ImageWindow win = getImage().getWindow();
/* 3715 */       state = (win != null) && ((win instanceof StackWindow)) && (((StackWindow)win).getAnimate());
/*      */     } else {
/* 3717 */       this.interp.error("Invalid argument");
/* 3718 */     }return state ? 1.0D : 0.0D;
/*      */   }
/*      */ 
/*      */   final boolean getCapsLockState() {
/* 3722 */     boolean capsDown = false;
/*      */     try {
/* 3724 */       capsDown = Toolkit.getDefaultToolkit().getLockingKeyState(20); } catch (Exception e) {
/*      */     }
/* 3726 */     return capsDown;
/*      */   }
/*      */ 
/*      */   Variable[] getList() {
/* 3730 */     String key = getStringArg();
/* 3731 */     if (key.equals("java.properties")) {
/* 3732 */       Properties props = System.getProperties();
/* 3733 */       Vector v = new Vector();
/* 3734 */       for (Enumeration en = props.keys(); en.hasMoreElements(); )
/* 3735 */         v.addElement((String)en.nextElement());
/* 3736 */       Variable[] array = new Variable[v.size()];
/* 3737 */       for (int i = 0; i < array.length; i++)
/* 3738 */         array[i] = new Variable(0, 0.0D, (String)v.elementAt(i));
/* 3739 */       return array;
/* 3740 */     }if (key.equals("window.titles")) {
/* 3741 */       Frame[] list = WindowManager.getNonImageWindows();
/* 3742 */       Variable[] array = new Variable[list.length];
/* 3743 */       for (int i = 0; i < list.length; i++) {
/* 3744 */         Frame frame = list[i];
/* 3745 */         array[i] = new Variable(0, 0.0D, frame.getTitle());
/*      */       }
/* 3747 */       return array;
/* 3748 */     }if (key.equals("threshold.methods")) {
/* 3749 */       String[] list = AutoThresholder.getMethods();
/* 3750 */       Variable[] array = new Variable[list.length];
/* 3751 */       for (int i = 0; i < list.length; i++)
/* 3752 */         array[i] = new Variable(0, 0.0D, list[i]);
/* 3753 */       return array;
/*      */     }
/* 3755 */     this.interp.error("Unvalid key");
/* 3756 */     return null;
/*      */   }
/*      */ 
/*      */   String doString()
/*      */   {
/* 3761 */     this.interp.getToken();
/* 3762 */     if (this.interp.token != 46)
/* 3763 */       this.interp.error("'.' expected");
/* 3764 */     this.interp.getToken();
/* 3765 */     if (this.interp.token != 129)
/* 3766 */       this.interp.error("Function name expected: ");
/* 3767 */     String name = this.interp.tokenString;
/* 3768 */     if (name.equals("append"))
/* 3769 */       return appendToBuffer();
/* 3770 */     if (name.equals("copy"))
/* 3771 */       return copyStringToClipboard();
/* 3772 */     if (name.equals("copyResults"))
/* 3773 */       return copyResults();
/* 3774 */     if (name.equals("paste"))
/* 3775 */       return getClipboardContents();
/* 3776 */     if (name.equals("resetBuffer"))
/* 3777 */       return resetBuffer();
/* 3778 */     if (name.equals("buffer")) {
/* 3779 */       return getBuffer();
/*      */     }
/* 3781 */     this.interp.error("Unrecognized String function");
/* 3782 */     return null;
/*      */   }
/*      */ 
/*      */   String appendToBuffer() {
/* 3786 */     String text = getStringArg();
/* 3787 */     if (this.buffer == null)
/* 3788 */       this.buffer = new StringBuffer(256);
/* 3789 */     this.buffer.append(text);
/* 3790 */     return null;
/*      */   }
/*      */ 
/*      */   String copyStringToClipboard() {
/* 3794 */     String text = getStringArg();
/* 3795 */     StringSelection ss = new StringSelection(text);
/* 3796 */     Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 3797 */     clipboard.setContents(ss, null);
/* 3798 */     return null;
/*      */   }
/*      */ 
/*      */   String getClipboardContents() {
/* 3802 */     this.interp.getParens();
/* 3803 */     Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 3804 */     Transferable data = clipboard.getContents(null);
/* 3805 */     String s = null;
/*      */     try { s = (String)data.getTransferData(DataFlavor.stringFlavor); } catch (Exception e) {
/* 3807 */       s = data.toString();
/* 3808 */     }return s;
/*      */   }
/*      */ 
/*      */   String copyResults() {
/* 3812 */     this.interp.getParens();
/* 3813 */     if (!IJ.isResultsWindow())
/* 3814 */       this.interp.error("No results");
/* 3815 */     TextPanel tp = IJ.getTextPanel();
/* 3816 */     if (tp == null) return null;
/* 3817 */     StringSelection ss = new StringSelection(tp.getText());
/* 3818 */     Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 3819 */     clipboard.setContents(ss, null);
/* 3820 */     return null;
/*      */   }
/*      */ 
/*      */   String resetBuffer() {
/* 3824 */     this.interp.getParens();
/* 3825 */     this.buffer = new StringBuffer(256);
/* 3826 */     return null;
/*      */   }
/*      */ 
/*      */   String getBuffer() {
/* 3830 */     this.interp.getParens();
/* 3831 */     if (this.buffer == null)
/* 3832 */       this.buffer = new StringBuffer(256);
/* 3833 */     return this.buffer.toString();
/*      */   }
/*      */ 
/*      */   void doCommand() {
/* 3837 */     String arg = getStringArg();
/* 3838 */     if (arg.equals("Start Animation"))
/* 3839 */       arg = "Start Animation [\\]";
/* 3840 */     IJ.doCommand(arg);
/*      */   }
/*      */ 
/*      */   void getDimensions() {
/* 3844 */     Variable width = getFirstVariable();
/* 3845 */     Variable height = getNextVariable();
/* 3846 */     Variable channels = getNextVariable();
/* 3847 */     Variable slices = getNextVariable();
/* 3848 */     Variable frames = getLastVariable();
/* 3849 */     ImagePlus imp = getImage();
/* 3850 */     int[] dim = imp.getDimensions();
/* 3851 */     width.setValue(dim[0]);
/* 3852 */     height.setValue(dim[1]);
/* 3853 */     channels.setValue(dim[2]);
/* 3854 */     slices.setValue(dim[3]);
/* 3855 */     frames.setValue(dim[4]);
/*      */   }
/*      */ 
/*      */   public static void registerExtensions(MacroExtension extensions) {
/* 3859 */     Interpreter interp = Interpreter.getInstance();
/* 3860 */     if (interp == null) {
/* 3861 */       IJ.error("Macro must be running to install macro extensions");
/* 3862 */       return;
/*      */     }
/* 3864 */     interp.pgm.extensionRegistry = new Hashtable();
/* 3865 */     ExtensionDescriptor[] descriptors = extensions.getExtensionFunctions();
/* 3866 */     for (int i = 0; i < descriptors.length; i++)
/* 3867 */       interp.pgm.extensionRegistry.put(descriptors[i].name, descriptors[i]);
/*      */   }
/*      */ 
/*      */   String doExt() {
/* 3871 */     this.interp.getToken();
/* 3872 */     if (this.interp.token != 46)
/* 3873 */       this.interp.error("'.' expected");
/* 3874 */     this.interp.getToken();
/* 3875 */     if ((this.interp.token != 129) && (this.interp.token != 136) && (this.interp.token != 135) && (this.interp.token != 134))
/* 3876 */       this.interp.error("Function name expected: ");
/* 3877 */     String name = this.interp.tokenString;
/* 3878 */     if (name.equals("install")) {
/* 3879 */       Object plugin = IJ.runPlugIn(getStringArg(), "");
/* 3880 */       if (plugin == null) this.interp.error("Plugin not found");
/* 3881 */       return null;
/*      */     }
/* 3883 */     ExtensionDescriptor desc = null;
/* 3884 */     if (this.pgm.extensionRegistry != null)
/* 3885 */       desc = (ExtensionDescriptor)this.pgm.extensionRegistry.get(name);
/* 3886 */     if (desc == null) {
/* 3887 */       this.interp.error("Unrecognized Ext function");
/* 3888 */       return null;
/*      */     }
/* 3890 */     return desc.dispatch(this);
/*      */   }
/*      */ 
/*      */   String exec()
/*      */   {
/* 3895 */     StringBuffer sb = new StringBuffer(256);
/* 3896 */     String arg1 = getFirstString();
/*      */     String[] cmd;
/* 3897 */     if (this.interp.nextToken() == 44) {
/* 3898 */       Vector v = new Vector();
/* 3899 */       v.add(arg1);
/*      */       do
/* 3901 */         v.add(getNextString());
/* 3902 */       while (this.interp.nextToken() == 44);
/* 3903 */       String[] cmd = new String[v.size()];
/* 3904 */       v.copyInto((String[])cmd);
/*      */     } else {
/* 3906 */       cmd = Tools.split(arg1);
/* 3907 */     }this.interp.getRightParen();
/* 3908 */     boolean openingDoc = ((cmd.length == 2) && (cmd[0].equals("open"))) || ((cmd.length == 5) && (cmd[3].equals("excel.exe")));
/* 3909 */     if ((openingDoc) && (IJ.isWindows())) {
/* 3910 */       String path = cmd[1];
/* 3911 */       if ((path.startsWith("http://")) || (path.startsWith("HTTP://"))) {
/* 3912 */         cmd = new String[4];
/* 3913 */         cmd[2] = "start";
/* 3914 */         cmd[3] = path;
/*      */       } else {
/* 3916 */         cmd = new String[3];
/* 3917 */         cmd[2] = path;
/*      */       }
/* 3919 */       cmd[0] = "cmd";
/* 3920 */       cmd[1] = "/c";
/*      */     }
/* 3922 */     BufferedReader reader = null;
/*      */     try {
/* 3924 */       Process p = Runtime.getRuntime().exec(cmd);
/* 3925 */       if (openingDoc) return null;
/* 3926 */       reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
/* 3927 */       int count = 1;
/*      */       String line;
/* 3928 */       while ((line = reader.readLine()) != null) {
/* 3929 */         sb.append(line + "\n");
/* 3930 */         if ((count++ == 1) && (line.startsWith("Microsoft Windows")))
/* 3931 */           break;
/*      */       }
/*      */     } catch (Exception e) {
/* 3934 */       sb.append(e.getMessage() + "\n");
/*      */     } finally {
/* 3936 */       if (reader != null) try { reader.close(); } catch (IOException e) {  }
/*      */  
/*      */     }
/* 3938 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   double getValue() {
/* 3942 */     String key = getStringArg();
/* 3943 */     if (key.indexOf("foreground") != -1)
/* 3944 */       return Toolbar.getForegroundColor().getRGB() & 0xFFFFFF;
/* 3945 */     if (key.indexOf("background") != -1)
/* 3946 */       return Toolbar.getBackgroundColor().getRGB() & 0xFFFFFF;
/* 3947 */     if (key.equals("font.size")) {
/* 3948 */       resetImage();
/* 3949 */       ImageProcessor ip = getProcessor();
/* 3950 */       setFont(ip);
/* 3951 */       return ip.getFont().getSize();
/* 3952 */     }if (key.equals("font.height")) {
/* 3953 */       resetImage();
/* 3954 */       ImageProcessor ip = getProcessor();
/* 3955 */       setFont(ip);
/* 3956 */       return ip.getFontMetrics().getHeight();
/*      */     }
/* 3958 */     this.interp.error("Invalid key");
/* 3959 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   double doStack()
/*      */   {
/* 3964 */     this.interp.getToken();
/* 3965 */     if (this.interp.token != 46)
/* 3966 */       this.interp.error("'.' expected");
/* 3967 */     this.interp.getToken();
/* 3968 */     if ((this.interp.token != 129) && (this.interp.token != 134))
/* 3969 */       this.interp.error("Function name expected: ");
/* 3970 */     String name = this.interp.tokenString;
/* 3971 */     if ((name.equals("isHyperstack")) || (name.equals("isHyperStack")))
/* 3972 */       return getImage().isHyperStack() ? 1.0D : 0.0D;
/* 3973 */     if (name.equals("getDimensions")) {
/* 3974 */       getDimensions(); return (0.0D / 0.0D);
/* 3975 */     }ImagePlus imp = getImage();
/* 3976 */     if (name.equals("setPosition")) {
/* 3977 */       setPosition(imp); return (0.0D / 0.0D);
/* 3978 */     }if (name.equals("getPosition")) {
/* 3979 */       getPosition(imp); return (0.0D / 0.0D);
/* 3980 */     }Calibration cal = imp.getCalibration();
/* 3981 */     if (name.equals("getFrameRate")) {
/* 3982 */       this.interp.getParens(); return cal.fps;
/* 3983 */     }if (name.equals("setFrameRate")) {
/* 3984 */       cal.fps = getArg(); return (0.0D / 0.0D);
/* 3985 */     }if (name.equals("getFrameInterval")) {
/* 3986 */       this.interp.getParens(); return cal.frameInterval;
/* 3987 */     }if (name.equals("setFrameInterval")) {
/* 3988 */       cal.frameInterval = getArg(); return (0.0D / 0.0D);
/* 3989 */     }if (name.equals("setTUnit")) {
/* 3990 */       cal.setTimeUnit(getStringArg()); return (0.0D / 0.0D);
/* 3991 */     }if (name.equals("setZUnit")) {
/* 3992 */       cal.setZUnit(getStringArg()); return (0.0D / 0.0D);
/* 3993 */     }if (name.equals("getUnits")) {
/* 3994 */       getStackUnits(cal); return (0.0D / 0.0D);
/* 3995 */     }if (imp.getStackSize() == 1)
/* 3996 */       this.interp.error("Stack required");
/* 3997 */     if (name.equals("setDimensions"))
/* 3998 */       setDimensions(imp);
/* 3999 */     else if (name.equals("setChannel"))
/* 4000 */       imp.setPosition((int)getArg(), imp.getSlice(), imp.getFrame());
/* 4001 */     else if (name.equals("setSlice"))
/* 4002 */       imp.setPosition(imp.getChannel(), (int)getArg(), imp.getFrame());
/* 4003 */     else if (name.equals("setFrame"))
/* 4004 */       imp.setPosition(imp.getChannel(), imp.getSlice(), (int)getArg());
/* 4005 */     else if (name.equals("setDisplayMode"))
/* 4006 */       setDisplayMode(imp, getStringArg());
/* 4007 */     else if (name.equals("getDisplayMode"))
/* 4008 */       getDisplayMode(imp);
/* 4009 */     else if (name.equals("setActiveChannels"))
/* 4010 */       setActiveChannels(imp, getStringArg());
/* 4011 */     else if (name.equals("getActiveChannels"))
/* 4012 */       getActiveChannels(imp);
/* 4013 */     else if (name.equals("swap"))
/* 4014 */       swapStackImages(imp);
/* 4015 */     else if (name.equals("getStatistics"))
/* 4016 */       getStackStatistics(imp, true);
/*      */     else
/* 4018 */       this.interp.error("Unrecognized Stack function");
/* 4019 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   void getStackUnits(Calibration cal) {
/* 4023 */     Variable x = getFirstVariable();
/* 4024 */     Variable y = getNextVariable();
/* 4025 */     Variable z = getNextVariable();
/* 4026 */     Variable t = getNextVariable();
/* 4027 */     Variable v = getLastVariable();
/* 4028 */     x.setString(cal.getXUnit());
/* 4029 */     y.setString(cal.getYUnit());
/* 4030 */     z.setString(cal.getZUnit());
/* 4031 */     t.setString(cal.getTimeUnit());
/* 4032 */     v.setString(cal.getValueUnit());
/*      */   }
/*      */ 
/*      */   void getStackStatistics(ImagePlus imp, boolean calibrated) {
/* 4036 */     Variable count = getFirstVariable();
/* 4037 */     Variable mean = null; Variable min = null; Variable max = null; Variable std = null; Variable hist = null;
/* 4038 */     int params = 19;
/* 4039 */     this.interp.getToken();
/* 4040 */     int arg = 1;
/* 4041 */     while (this.interp.token == 44) {
/* 4042 */       arg++;
/* 4043 */       switch (arg) { case 2:
/* 4044 */         mean = getVariable(); break;
/*      */       case 3:
/* 4045 */         min = getVariable(); break;
/*      */       case 4:
/* 4046 */         max = getVariable(); break;
/*      */       case 5:
/* 4047 */         std = getVariable(); params += 4; break;
/*      */       case 6:
/* 4048 */         hist = getArrayVariable(); break;
/*      */       default:
/* 4049 */         this.interp.error("')' expected");
/*      */       }
/* 4051 */       this.interp.getToken();
/*      */     }
/* 4053 */     if (this.interp.token != 41) this.interp.error("')' expected");
/* 4054 */     ImageStatistics stats = new StackStatistics(imp);
/* 4055 */     count.setValue(stats.pixelCount);
/* 4056 */     if (mean != null) mean.setValue(stats.mean);
/* 4057 */     if (min != null) min.setValue(stats.min);
/* 4058 */     if (max != null) max.setValue(stats.max);
/* 4059 */     if (std != null) std.setValue(stats.stdDev);
/* 4060 */     if (hist != null) {
/* 4061 */       int[] histogram = stats.histogram;
/* 4062 */       int bins = histogram.length;
/* 4063 */       Variable[] array = new Variable[bins];
/* 4064 */       int hmax = 255;
/* 4065 */       for (int i = 0; i <= hmax; i++)
/* 4066 */         array[i] = new Variable(histogram[i]);
/* 4067 */       hist.setArray(array);
/*      */     }
/*      */   }
/*      */ 
/*      */   void setActiveChannels(ImagePlus imp, String channels) {
/* 4072 */     if (!imp.isComposite())
/* 4073 */       this.interp.error("Composite image required");
/* 4074 */     boolean[] active = ((CompositeImage)imp).getActiveChannels();
/* 4075 */     for (int i = 0; i < active.length; i++) {
/* 4076 */       boolean b = false;
/* 4077 */       if ((channels.length() > i) && (channels.charAt(i) == '1'))
/* 4078 */         b = true;
/* 4079 */       active[i] = b;
/*      */     }
/* 4081 */     imp.updateAndDraw();
/*      */   }
/*      */ 
/*      */   void getActiveChannels(ImagePlus imp) {
/* 4085 */     if (!imp.isComposite())
/* 4086 */       this.interp.error("Composite image required");
/* 4087 */     boolean[] active = ((CompositeImage)imp).getActiveChannels();
/* 4088 */     int n = active.length;
/* 4089 */     char[] chars = new char[n];
/* 4090 */     int nChannels = imp.getNChannels();
/* 4091 */     for (int i = 0; i < n; i++) {
/* 4092 */       if (i < nChannels)
/* 4093 */         chars[i] = (active[i] != 0 ? 49 : '0');
/*      */       else
/* 4095 */         chars[i] = '0';
/*      */     }
/* 4097 */     Variable channels = getVariableArg();
/* 4098 */     channels.setString(new String(chars));
/*      */   }
/*      */ 
/*      */   void setDisplayMode(ImagePlus imp, String mode) {
/* 4102 */     mode = mode.toLowerCase(Locale.US);
/* 4103 */     if (!imp.isComposite())
/* 4104 */       this.interp.error("Composite image required");
/* 4105 */     int m = -1;
/* 4106 */     if (mode.equals("composite"))
/* 4107 */       m = 1;
/* 4108 */     else if (mode.equals("color"))
/* 4109 */       m = 2;
/* 4110 */     else if (mode.startsWith("gray"))
/* 4111 */       m = 3;
/* 4112 */     if (m == -1)
/* 4113 */       this.interp.error("Invalid mode");
/* 4114 */     ((CompositeImage)imp).setMode(m);
/* 4115 */     imp.updateAndDraw();
/*      */   }
/*      */ 
/*      */   void swapStackImages(ImagePlus imp) {
/* 4119 */     int n1 = (int)getFirstArg();
/* 4120 */     int n2 = (int)getLastArg();
/* 4121 */     ImageStack stack = imp.getStack();
/* 4122 */     int size = stack.getSize();
/* 4123 */     if ((n1 < 1) || (n1 > size) || (n2 < 1) || (n2 > size))
/* 4124 */       this.interp.error("Argument out of range");
/* 4125 */     Object pixels = stack.getPixels(n1);
/* 4126 */     String label = stack.getSliceLabel(n1);
/* 4127 */     stack.setPixels(stack.getPixels(n2), n1);
/* 4128 */     stack.setSliceLabel(stack.getSliceLabel(n2), n1);
/* 4129 */     stack.setPixels(pixels, n2);
/* 4130 */     stack.setSliceLabel(label, n2);
/* 4131 */     int current = imp.getCurrentSlice();
/* 4132 */     if (imp.isComposite()) {
/* 4133 */       CompositeImage ci = (CompositeImage)imp;
/* 4134 */       if (ci.getMode() == 1) {
/* 4135 */         ci.reset();
/* 4136 */         imp.updateAndDraw();
/* 4137 */         imp.repaintWindow();
/* 4138 */         return;
/*      */       }
/*      */     }
/* 4141 */     if ((n1 == current) || (n2 == current))
/* 4142 */       imp.setStack(null, stack);
/*      */   }
/*      */ 
/*      */   void getDisplayMode(ImagePlus imp) {
/* 4146 */     Variable v = getVariableArg();
/* 4147 */     String mode = "";
/* 4148 */     if (imp.isComposite())
/* 4149 */       mode = ((CompositeImage)imp).getModeAsString();
/* 4150 */     v.setString(mode);
/*      */   }
/*      */ 
/*      */   void getPosition(ImagePlus imp) {
/* 4154 */     Variable channel = getFirstVariable();
/* 4155 */     Variable slice = getNextVariable();
/* 4156 */     Variable frame = getLastVariable();
/* 4157 */     int c = imp.getChannel();
/* 4158 */     int z = imp.getSlice();
/* 4159 */     int t = imp.getFrame();
/* 4160 */     if (c * z * t > imp.getStackSize()) {
/* 4161 */       c = 1; z = imp.getCurrentSlice(); t = 1;
/* 4162 */     }channel.setValue(c);
/* 4163 */     slice.setValue(z);
/* 4164 */     frame.setValue(t);
/*      */   }
/*      */ 
/*      */   void setPosition(ImagePlus img) {
/* 4168 */     int channel = (int)getFirstArg();
/* 4169 */     int slice = (int)getNextArg();
/* 4170 */     int frame = (int)getLastArg();
/* 4171 */     if (Interpreter.isBatchMode())
/* 4172 */       img.setPositionWithoutUpdate(channel, slice, frame);
/*      */     else
/* 4174 */       img.setPosition(channel, slice, frame);
/*      */   }
/*      */ 
/*      */   void setDimensions(ImagePlus img) {
/* 4178 */     int c = (int)getFirstArg();
/* 4179 */     int z = (int)getNextArg();
/* 4180 */     int t = (int)getLastArg();
/* 4181 */     img.setDimensions(c, z, t);
/* 4182 */     if (img.getWindow() == null) img.setOpenAsHyperStack(true); 
/*      */   }
/*      */ 
/*      */   void setTool()
/*      */   {
/* 4186 */     this.interp.getLeftParen();
/* 4187 */     if (isStringArg()) {
/* 4188 */       boolean ok = IJ.setTool(getString());
/* 4189 */       if (!ok) this.interp.error("Unrecognized tool name"); 
/*      */     }
/* 4191 */     else { IJ.setTool((int)this.interp.getExpression()); }
/* 4192 */     this.interp.getRightParen();
/*      */   }
/*      */ 
/*      */   String doToString() {
/* 4196 */     String s = getFirstString();
/* 4197 */     this.interp.getToken();
/* 4198 */     if (this.interp.token == 44) {
/* 4199 */       double value = Tools.parseDouble(s);
/* 4200 */       s = IJ.d2s(value, (int)this.interp.getExpression());
/* 4201 */       this.interp.getToken();
/*      */     }
/* 4203 */     if (this.interp.token != 41) this.interp.error("')' expected");
/* 4204 */     return s;
/*      */   }
/*      */ 
/*      */   double matches() {
/* 4208 */     String str = getFirstString();
/* 4209 */     String regex = getLastString();
/* 4210 */     boolean matches = str.matches(regex);
/* 4211 */     return matches ? 1.0D : 0.0D;
/*      */   }
/*      */ 
/*      */   void waitForUser() {
/* 4215 */     if ((waitForUserDialog != null) && (waitForUserDialog.isVisible()))
/* 4216 */       this.interp.error("Duplicate call");
/* 4217 */     String title = "Action Required";
/* 4218 */     String text = "   Click \"OK\" to continue     ";
/* 4219 */     if (this.interp.nextToken() == 40) {
/* 4220 */       title = getFirstString();
/* 4221 */       if (this.interp.nextToken() == 44) {
/* 4222 */         text = getLastString();
/*      */       } else {
/* 4224 */         text = title;
/* 4225 */         title = "Action Required";
/* 4226 */         this.interp.getRightParen();
/*      */       }
/*      */     }
/* 4229 */     waitForUserDialog = new WaitForUserDialog(title, text);
/* 4230 */     waitForUserDialog.show();
/* 4231 */     if (waitForUserDialog.escPressed())
/* 4232 */       throw new RuntimeException("Macro canceled");
/*      */   }
/*      */ 
/*      */   void abortDialog() {
/* 4236 */     if ((waitForUserDialog != null) && (waitForUserDialog.isVisible()))
/* 4237 */       waitForUserDialog.close();
/*      */   }
/*      */ 
/*      */   double getStringWidth() {
/* 4241 */     resetImage();
/* 4242 */     ImageProcessor ip = getProcessor();
/* 4243 */     setFont(ip);
/* 4244 */     return ip.getStringWidth(getStringArg());
/*      */   }
/*      */ 
/*      */   String doList() {
/* 4248 */     this.interp.getToken();
/* 4249 */     if (this.interp.token != 46)
/* 4250 */       this.interp.error("'.' expected");
/* 4251 */     this.interp.getToken();
/* 4252 */     if ((this.interp.token != 129) && (this.interp.token != 137) && (this.interp.token != 135))
/* 4253 */       this.interp.error("Function name expected: ");
/* 4254 */     if (this.props == null)
/* 4255 */       this.props = new Properties();
/* 4256 */     String value = null;
/* 4257 */     String name = this.interp.tokenString;
/* 4258 */     if (name.equals("get")) {
/* 4259 */       value = this.props.getProperty(getStringArg());
/* 4260 */       value = value != null ? value : "";
/* 4261 */     } else if (name.equals("getValue")) {
/* 4262 */       value = this.props.getProperty(getStringArg());
/* 4263 */       if (value == null) this.interp.error("Value not found"); 
/*      */     }
/* 4264 */     else if ((name.equals("set")) || (name.equals("add")) || (name.equals("put"))) {
/* 4265 */       this.props.setProperty(getFirstString(), getLastString());
/* 4266 */     } else if ((name.equals("clear")) || (name.equals("reset"))) {
/* 4267 */       this.interp.getParens();
/* 4268 */       this.props.clear();
/* 4269 */     } else if (name.equals("setList")) {
/* 4270 */       setProperties();
/* 4271 */     } else if (name.equals("getList")) {
/* 4272 */       value = getProperties();
/* 4273 */     } else if ((name.equals("size")) || (name.equals("getSize"))) {
/* 4274 */       this.interp.getParens();
/* 4275 */       value = "" + this.props.size();
/* 4276 */     } else if (name.equals("setMeasurements")) {
/* 4277 */       setMeasurements();
/* 4278 */     } else if (name.equals("setCommands")) {
/* 4279 */       setCommands();
/*      */     } else {
/* 4281 */       this.interp.error("Unrecognized List function");
/* 4282 */     }return value;
/*      */   }
/*      */ 
/*      */   void setCommands() {
/* 4286 */     this.interp.getParens();
/* 4287 */     Hashtable commands = Menus.getCommands();
/* 4288 */     this.props = new Properties();
/* 4289 */     for (Enumeration en = commands.keys(); en.hasMoreElements(); ) {
/* 4290 */       String command = (String)en.nextElement();
/* 4291 */       this.props.setProperty(command, (String)commands.get(command));
/*      */     }
/*      */   }
/*      */ 
/*      */   void setMeasurements() {
/* 4296 */     this.interp.getParens();
/* 4297 */     this.props.clear();
/* 4298 */     ImagePlus imp = getImage();
/* 4299 */     int measurements = 1043199;
/*      */ 
/* 4303 */     ImageStatistics stats = imp.getStatistics(measurements);
/* 4304 */     ResultsTable rt = new ResultsTable();
/* 4305 */     Analyzer analyzer = new Analyzer(imp, measurements, rt);
/* 4306 */     analyzer.saveResults(stats, imp.getRoi());
/* 4307 */     for (int i = 0; i <= rt.getLastColumn(); i++)
/* 4308 */       if (rt.columnExists(i)) {
/* 4309 */         String name = rt.getColumnHeading(i);
/* 4310 */         String value = "" + rt.getValueAsDouble(i, 0);
/* 4311 */         this.props.setProperty(name, value);
/*      */       }
/*      */   }
/*      */ 
/*      */   void setProperties()
/*      */   {
/* 4317 */     String list = getStringArg();
/* 4318 */     this.props.clear();
/*      */     try {
/* 4320 */       InputStream is = new ByteArrayInputStream(list.getBytes("utf-8"));
/* 4321 */       this.props.load(is);
/*      */     } catch (Exception e) {
/* 4323 */       this.interp.error("" + e);
/*      */     }
/*      */   }
/*      */ 
/*      */   String getProperties() {
/* 4328 */     this.interp.getParens();
/* 4329 */     Vector v = new Vector();
/* 4330 */     for (Enumeration en = this.props.keys(); en.hasMoreElements(); )
/* 4331 */       v.addElement(en.nextElement());
/* 4332 */     String[] keys = new String[v.size()];
/* 4333 */     for (int i = 0; i < keys.length; i++)
/* 4334 */       keys[i] = ((String)v.elementAt(i));
/* 4335 */     Arrays.sort(keys);
/* 4336 */     StringBuffer sb = new StringBuffer();
/* 4337 */     for (int i = 0; i < keys.length; i++) {
/* 4338 */       sb.append(keys[i]);
/* 4339 */       sb.append("=");
/* 4340 */       sb.append(this.props.get(keys[i]));
/* 4341 */       sb.append("\n");
/*      */     }
/* 4343 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   void makePoint() {
/* 4347 */     int x = (int)getFirstArg();
/* 4348 */     int y = (int)getLastArg();
/* 4349 */     IJ.makePoint(x, y);
/* 4350 */     resetImage();
/*      */   }
/*      */ 
/*      */   void makeText() {
/* 4354 */     String text = getFirstString();
/* 4355 */     int x = (int)getNextArg();
/* 4356 */     int y = (int)getLastArg();
/* 4357 */     ImagePlus imp = getImage();
/* 4358 */     Font font = this.font;
/* 4359 */     boolean nullFont = font == null;
/* 4360 */     if (nullFont)
/* 4361 */       font = imp.getProcessor().getFont();
/* 4362 */     TextRoi roi = new TextRoi(x, y, text, font);
/* 4363 */     if (!nullFont)
/* 4364 */       roi.setAntialiased(this.antialiasedText);
/* 4365 */     imp.setRoi(roi);
/*      */   }
/*      */ 
/*      */   void makeEllipse() {
/* 4369 */     ImagePlus imp = getImage();
/* 4370 */     Roi previousRoi = imp.getRoi();
/* 4371 */     if ((this.shiftKeyDown) || (this.altKeyDown))
/* 4372 */       imp.saveRoi();
/* 4373 */     double x1 = getFirstArg();
/* 4374 */     double y1 = getNextArg();
/* 4375 */     double x2 = getNextArg();
/* 4376 */     double y2 = getNextArg();
/* 4377 */     double aspectRatio = getLastArg();
/* 4378 */     Roi roi = new EllipseRoi(x1, y1, x2, y2, aspectRatio);
/* 4379 */     imp.setRoi(roi);
/* 4380 */     if ((previousRoi != null) && (roi != null))
/* 4381 */       updateRoi(roi);
/* 4382 */     resetImage();
/*      */   }
/*      */ 
/*      */   double fit() {
/* 4386 */     this.interp.getToken();
/* 4387 */     if (this.interp.token != 46)
/* 4388 */       this.interp.error("'.' expected");
/* 4389 */     this.interp.getToken();
/* 4390 */     if ((this.interp.token != 129) && (this.interp.token != 137))
/* 4391 */       this.interp.error("Function name expected: ");
/* 4392 */     if (this.props == null)
/* 4393 */       this.props = new Properties();
/* 4394 */     String name = this.interp.tokenString;
/* 4395 */     if (name.equals("doFit"))
/* 4396 */       return fitCurve();
/* 4397 */     if (name.equals("getEquation"))
/* 4398 */       return getEquation();
/* 4399 */     if (name.equals("nEquations")) {
/* 4400 */       this.interp.getParens();
/* 4401 */       return CurveFitter.fitList.length;
/* 4402 */     }if (name.equals("showDialog")) {
/* 4403 */       this.showFitDialog = true;
/* 4404 */       return (0.0D / 0.0D);
/* 4405 */     }if (name.equals("logResults")) {
/* 4406 */       this.logFitResults = true;
/* 4407 */       return (0.0D / 0.0D);
/*      */     }
/* 4409 */     if (this.fitter == null)
/* 4410 */       this.interp.error("No fit");
/* 4411 */     if (name.equals("f"))
/* 4412 */       return this.fitter.f(this.fitter.getParams(), getArg());
/* 4413 */     if (name.equals("plot")) {
/* 4414 */       this.interp.getParens();
/* 4415 */       Fitter.plot(this.fitter);
/* 4416 */       return (0.0D / 0.0D);
/* 4417 */     }if (name.equals("nParams")) {
/* 4418 */       this.interp.getParens();
/* 4419 */       return this.fitter.getNumParams();
/* 4420 */     }if (name.equals("p")) {
/* 4421 */       int index = (int)getArg();
/* 4422 */       checkIndex(index, 0, this.fitter.getNumParams() - 1);
/* 4423 */       double[] p = this.fitter.getParams();
/* 4424 */       return p[index];
/* 4425 */     }if (name.equals("rSquared")) {
/* 4426 */       this.interp.getParens();
/* 4427 */       return this.fitter.getRSquared();
/*      */     }
/* 4429 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   double fitCurve() {
/* 4433 */     this.interp.getLeftParen();
/* 4434 */     int fit = -1;
/* 4435 */     String name = null;
/* 4436 */     double[] initialValues = null;
/* 4437 */     if (isStringArg()) {
/* 4438 */       name = getString().toLowerCase(Locale.US);
/* 4439 */       String[] list = CurveFitter.fitList;
/* 4440 */       for (int i = 0; i < list.length; i++) {
/* 4441 */         if (name.equals(list[i].toLowerCase(Locale.US))) {
/* 4442 */           fit = i;
/* 4443 */           break;
/*      */         }
/*      */       }
/* 4446 */       boolean isCustom = (name.indexOf("y=") != -1) || (name.indexOf("y =") != -1);
/* 4447 */       if ((fit == -1) && (!isCustom))
/* 4448 */         this.interp.error("Unrecognized fit");
/*      */     } else {
/* 4450 */       fit = (int)this.interp.getExpression();
/* 4451 */     }double[] x = getNextArray();
/* 4452 */     this.interp.getComma();
/* 4453 */     double[] y = getNumericArray();
/* 4454 */     if (this.interp.nextToken() == 44) {
/* 4455 */       this.interp.getComma();
/* 4456 */       initialValues = getNumericArray();
/*      */     }
/* 4458 */     this.interp.getRightParen();
/* 4459 */     if (x.length != y.length)
/* 4460 */       this.interp.error("Arrays not same length");
/* 4461 */     if (x.length == 0)
/* 4462 */       this.interp.error("Zero length array");
/* 4463 */     this.fitter = new CurveFitter(x, y);
/* 4464 */     if ((fit == -1) && (name != null)) {
/* 4465 */       Interpreter instance = Interpreter.getInstance();
/* 4466 */       int params = this.fitter.doCustomFit(name, initialValues, this.showFitDialog);
/* 4467 */       Interpreter.instance = instance;
/* 4468 */       if (params == 0)
/* 4469 */         this.interp.error("Invalid custom function");
/*      */     } else {
/* 4471 */       this.fitter.doFit(fit, this.showFitDialog);
/* 4472 */     }if (this.logFitResults) {
/* 4473 */       IJ.log(this.fitter.getResultString());
/* 4474 */       this.logFitResults = false;
/*      */     }
/* 4476 */     this.showFitDialog = false;
/* 4477 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   double getEquation() {
/* 4481 */     int index = (int)getFirstArg();
/* 4482 */     Variable name = getNextVariable();
/* 4483 */     Variable formula = getLastVariable();
/* 4484 */     checkIndex(index, 0, CurveFitter.fitList.length - 1);
/* 4485 */     name.setString(CurveFitter.fitList[index]);
/* 4486 */     formula.setString(CurveFitter.fList[index]);
/* 4487 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   void setMinAndMax() {
/* 4491 */     double min = getFirstArg();
/* 4492 */     double max = getNextArg();
/* 4493 */     int channels = 7;
/* 4494 */     if (this.interp.nextToken() == 44) {
/* 4495 */       channels = (int)getLastArg();
/* 4496 */       if (getImage().getBitDepth() != 24)
/* 4497 */         this.interp.error("RGB image required");
/*      */     } else {
/* 4499 */       this.interp.getRightParen();
/* 4500 */     }IJ.setMinAndMax(min, max, channels);
/* 4501 */     resetImage();
/*      */   }
/*      */ 
/*      */   String debug() {
/* 4505 */     String arg = "break";
/* 4506 */     if (this.interp.nextToken() == 40)
/* 4507 */       arg = getStringArg().toLowerCase(Locale.US);
/*      */     else
/* 4509 */       this.interp.getParens();
/* 4510 */     if ((this.interp.editor == null) && (!arg.equals("throw")) && (!arg.equals("dump"))) {
/* 4511 */       Editor ed = Editor.getInstance();
/* 4512 */       if (ed == null)
/* 4513 */         this.interp.error("Macro editor not available");
/*      */       else
/* 4515 */         this.interp.setEditor(ed);
/*      */     }
/* 4517 */     if (arg.equals("run")) {
/* 4518 */       this.interp.setDebugMode(4);
/* 4519 */     } else if (arg.equals("break")) {
/* 4520 */       this.interp.setDebugMode(1);
/* 4521 */     } else if (arg.equals("trace")) {
/* 4522 */       this.interp.setDebugMode(2);
/* 4523 */     } else if (arg.indexOf("fast") != -1) {
/* 4524 */       this.interp.setDebugMode(3);
/* 4525 */     } else if (arg.equals("dump")) {
/* 4526 */       this.interp.dump(); } else {
/* 4527 */       if (arg.indexOf("throw") != -1) {
/* 4528 */         throw new IllegalArgumentException();
/*      */       }
/* 4530 */       this.interp.error("Argument must be 'run', 'break', 'trace', 'fast-trace' or 'dump'");
/* 4531 */     }IJ.setKeyUp(-1);
/* 4532 */     return null;
/*      */   }
/*      */ 
/*      */   Variable[] doArray() {
/* 4536 */     this.interp.getToken();
/* 4537 */     if (this.interp.token != 46)
/* 4538 */       this.interp.error("'.' expected");
/* 4539 */     this.interp.getToken();
/* 4540 */     if ((this.interp.token != 129) && (this.interp.token != 134))
/* 4541 */       this.interp.error("Function name expected: ");
/* 4542 */     String name = this.interp.tokenString;
/* 4543 */     if (name.equals("copy"))
/* 4544 */       return copyArray();
/* 4545 */     if (name.equals("trim"))
/* 4546 */       return trimArray();
/* 4547 */     if (name.equals("sort"))
/* 4548 */       return sortArray();
/* 4549 */     if (name.equals("rankPositions"))
/* 4550 */       return getRankPositions();
/* 4551 */     if (name.equals("getStatistics"))
/* 4552 */       return getArrayStatistics();
/* 4553 */     if (name.equals("fill"))
/* 4554 */       return fillArray();
/* 4555 */     if (name.equals("invert")) {
/* 4556 */       return invertArray();
/*      */     }
/* 4558 */     this.interp.error("Unrecognized Array function");
/* 4559 */     return null;
/*      */   }
/*      */ 
/*      */   Variable[] copyArray() {
/* 4563 */     this.interp.getLeftParen();
/* 4564 */     Variable[] a = getArray();
/* 4565 */     this.interp.getRightParen();
/* 4566 */     return duplicate(a);
/*      */   }
/*      */ 
/*      */   Variable[] duplicate(Variable[] a1) {
/* 4570 */     Variable[] a2 = new Variable[a1.length];
/* 4571 */     for (int i = 0; i < a1.length; i++)
/* 4572 */       a2[i] = ((Variable)a1[i].clone());
/* 4573 */     return a2;
/*      */   }
/*      */ 
/*      */   Variable[] trimArray() {
/* 4577 */     this.interp.getLeftParen();
/* 4578 */     Variable[] a1 = getArray();
/* 4579 */     int len = a1.length;
/* 4580 */     int size = (int)getLastArg();
/* 4581 */     if (size < 0) size = 0;
/* 4582 */     if (size > len) size = len;
/* 4583 */     Variable[] a2 = new Variable[size];
/* 4584 */     for (int i = 0; i < size; i++)
/* 4585 */       a2[i] = ((Variable)a1[i].clone());
/* 4586 */     return a2;
/*      */   }
/*      */ 
/*      */   Variable[] sortArray() {
/* 4590 */     this.interp.getLeftParen();
/* 4591 */     Variable[] a = getArray();
/* 4592 */     this.interp.getRightParen();
/* 4593 */     int len = a.length;
/* 4594 */     int nNumbers = 0;
/* 4595 */     for (int i = 0; i < len; i++) {
/* 4596 */       if (a[i].getString() == null) nNumbers++;
/*      */     }
/* 4598 */     if (nNumbers == len) {
/* 4599 */       double[] d = new double[len];
/* 4600 */       for (int i = 0; i < len; i++)
/* 4601 */         d[i] = a[i].getValue();
/* 4602 */       Arrays.sort(d);
/* 4603 */       for (int i = 0; i < len; i++)
/* 4604 */         a[i].setValue(d[i]);
/* 4605 */     } else if (nNumbers == 0) {
/* 4606 */       String[] s = new String[len];
/* 4607 */       for (int i = 0; i < len; i++) {
/* 4608 */         s[i] = a[i].getString();
/*      */       }
/* 4610 */       Arrays.sort(s, String.CASE_INSENSITIVE_ORDER);
/* 4611 */       for (int i = 0; i < len; i++)
/* 4612 */         a[i].setString(s[i]);
/*      */     } else {
/* 4614 */       this.interp.error("Mixed strings and numbers");
/* 4615 */     }return a;
/*      */   }
/*      */ 
/*      */   Variable[] getRankPositions() {
/* 4619 */     this.interp.getLeftParen();
/* 4620 */     Variable[] a = getArray();
/* 4621 */     this.interp.getRightParen();
/* 4622 */     int len = a.length;
/* 4623 */     int nNumbers = 0;
/* 4624 */     int[] indexes = new int[len];
/* 4625 */     for (int i = 0; i < len; i++) {
/* 4626 */       indexes[i] = i;
/* 4627 */       if (a[i].getString() == null)
/* 4628 */         nNumbers++;
/*      */     }
/* 4630 */     if ((nNumbers != len) && (nNumbers != 0)) {
/* 4631 */       this.interp.error("Mixed strings and numbers");
/* 4632 */       return a;
/*      */     }
/* 4634 */     Variable[] varArray = new Variable[len];
/* 4635 */     if (nNumbers == len) {
/* 4636 */       double[] doubles = new double[len];
/* 4637 */       for (int i = 0; i < len; i++)
/* 4638 */         doubles[i] = a[i].getValue();
/* 4639 */       Tools.quicksort(doubles, indexes);
/* 4640 */     } else if (nNumbers == 0) {
/* 4641 */       String[] strings = new String[len];
/* 4642 */       for (int i = 0; i < len; i++)
/* 4643 */         strings[i] = a[i].getString();
/* 4644 */       Tools.quicksort(strings, indexes);
/*      */     }
/* 4646 */     for (int i = 0; i < len; i++)
/* 4647 */       varArray[i] = new Variable(indexes[i]);
/* 4648 */     return varArray;
/*      */   }
/*      */ 
/*      */   Variable[] getArrayStatistics() {
/* 4652 */     this.interp.getLeftParen();
/* 4653 */     Variable[] a = getArray();
/* 4654 */     Variable minv = getNextVariable();
/* 4655 */     Variable maxv = null; Variable mean = null; Variable std = null;
/* 4656 */     this.interp.getToken();
/* 4657 */     int arg = 1;
/* 4658 */     while (this.interp.token == 44) {
/* 4659 */       arg++;
/* 4660 */       switch (arg) { case 2:
/* 4661 */         maxv = getVariable(); break;
/*      */       case 3:
/* 4662 */         mean = getVariable(); break;
/*      */       case 4:
/* 4663 */         std = getVariable(); break;
/*      */       default:
/* 4664 */         this.interp.error("')' expected");
/*      */       }
/* 4666 */       this.interp.getToken();
/*      */     }
/* 4668 */     if (this.interp.token != 41) this.interp.error("')' expected");
/* 4669 */     int n = a.length;
/* 4670 */     double sum = 0.0D; double sum2 = 0.0D;
/* 4671 */     double min = (1.0D / 0.0D);
/* 4672 */     double max = (-1.0D / 0.0D);
/* 4673 */     for (int i = 0; i < n; i++) {
/* 4674 */       double value = a[i].getValue();
/* 4675 */       sum += value;
/* 4676 */       sum2 += value * value;
/* 4677 */       if (value < min) min = value;
/* 4678 */       if (value > max) max = value;
/*      */     }
/* 4680 */     minv.setValue(min);
/* 4681 */     if (maxv != null) maxv.setValue(max);
/* 4682 */     if (mean != null) mean.setValue(sum / n);
/* 4683 */     if (std != null) {
/* 4684 */       double stdDev = (n * sum2 - sum * sum) / n;
/* 4685 */       stdDev = Math.sqrt(stdDev / (n - 1.0D));
/* 4686 */       std.setValue(stdDev);
/*      */     }
/* 4688 */     return a;
/*      */   }
/*      */ 
/*      */   Variable[] fillArray() {
/* 4692 */     this.interp.getLeftParen();
/* 4693 */     Variable[] a = getArray();
/* 4694 */     double v = getLastArg();
/* 4695 */     for (int i = 0; i < a.length; i++)
/* 4696 */       a[i].setValue(v);
/* 4697 */     return a;
/*      */   }
/*      */ 
/*      */   Variable[] invertArray() {
/* 4701 */     this.interp.getLeftParen();
/* 4702 */     Variable[] a = getArray();
/* 4703 */     this.interp.getRightParen();
/* 4704 */     int n = a.length;
/* 4705 */     for (int i = 0; i < n / 2; i++) {
/* 4706 */       Variable temp = a[i];
/* 4707 */       a[i] = a[(n - i - 1)];
/* 4708 */       a[(n - i - 1)] = temp;
/*      */     }
/* 4710 */     return a;
/*      */   }
/*      */ 
/*      */   double charCodeAt() {
/* 4714 */     String str = getFirstString();
/* 4715 */     int index = (int)getLastArg();
/* 4716 */     checkIndex(index, 0, str.length() - 1);
/* 4717 */     return str.charAt(index);
/*      */   }
/*      */ 
/*      */   void doWand() {
/* 4721 */     int x = (int)getFirstArg();
/* 4722 */     int y = (int)getNextArg();
/* 4723 */     double tolerance = 0.0D;
/* 4724 */     String mode = null;
/* 4725 */     if (this.interp.nextToken() == 44) {
/* 4726 */       tolerance = getNextArg();
/* 4727 */       mode = getNextString();
/*      */     }
/* 4729 */     this.interp.getRightParen();
/* 4730 */     IJ.doWand(x, y, tolerance, mode);
/* 4731 */     resetImage();
/*      */   }
/*      */ 
/*      */   String ijCall() {
/* 4735 */     this.interp.getToken();
/* 4736 */     if (this.interp.token != 46)
/* 4737 */       this.interp.error("'.' expected");
/* 4738 */     this.interp.getToken();
/* 4739 */     if ((this.interp.token != 129) && (this.interp.token != 135))
/* 4740 */       this.interp.error("Function name expected: ");
/* 4741 */     String name = this.interp.tokenString;
/* 4742 */     if (name.equals("pad"))
/* 4743 */       return IJ.pad((int)getFirstArg(), (int)getLastArg());
/* 4744 */     if (name.equals("deleteRows")) {
/* 4745 */       IJ.deleteRows((int)getFirstArg(), (int)getLastArg());
/* 4746 */     } else if (name.equals("log")) {
/* 4747 */       IJ.log(getStringArg()); } else {
/* 4748 */       if (name.equals("freeMemory")) {
/* 4749 */         this.interp.getParens(); return IJ.freeMemory();
/* 4750 */       }if (name.equals("currentMemory")) {
/* 4751 */         this.interp.getParens(); return "" + IJ.currentMemory();
/* 4752 */       }if (name.equals("maxMemory")) {
/* 4753 */         this.interp.getParens(); return "" + IJ.maxMemory();
/* 4754 */       }if (name.equals("getToolName")) {
/* 4755 */         this.interp.getParens(); return "" + IJ.getToolName();
/* 4756 */       }if (name.equals("redirectErrorMessages")) {
/* 4757 */         this.interp.getParens(); IJ.redirectErrorMessages(); return null;
/* 4758 */       }if (name.equals("renameResults"))
/* 4759 */         IJ.renameResults(getStringArg());
/*      */       else
/* 4761 */         this.interp.error("Unrecognized IJ function name"); 
/*      */     }
/* 4762 */     return null;
/*      */   }
/*      */ 
/*      */   double overlay() {
/* 4766 */     this.interp.getToken();
/* 4767 */     if (this.interp.token != 46)
/* 4768 */       this.interp.error("'.' expected");
/* 4769 */     this.interp.getToken();
/* 4770 */     if ((this.interp.token != 129) && (this.interp.token != 137) && (this.interp.token != 134) && (this.interp.token != 138))
/*      */     {
/* 4772 */       this.interp.error("Function name expected: ");
/* 4773 */     }String name = this.interp.tokenString;
/* 4774 */     ImagePlus imp = getImage();
/* 4775 */     if (name.equals("lineTo"))
/* 4776 */       return overlayLineTo();
/* 4777 */     if (name.equals("moveTo"))
/* 4778 */       return overlayMoveTo();
/* 4779 */     if (name.equals("drawLine"))
/* 4780 */       return overlayDrawLine();
/* 4781 */     if (name.equals("drawRect"))
/* 4782 */       return overlayDrawRectOrEllipse(imp, false);
/* 4783 */     if (name.equals("drawEllipse"))
/* 4784 */       return overlayDrawRectOrEllipse(imp, true);
/* 4785 */     if (name.equals("drawString"))
/* 4786 */       return overlayDrawString(imp);
/* 4787 */     if (name.equals("add"))
/* 4788 */       return addDrawing(imp);
/* 4789 */     if (name.equals("show"))
/* 4790 */       return showOverlay(imp);
/* 4791 */     if (name.equals("hide"))
/* 4792 */       return hideOverlay(imp);
/* 4793 */     if (name.equals("remove"))
/* 4794 */       return removeOverlay(imp);
/* 4795 */     if (name.equals("paste")) {
/* 4796 */       this.interp.getParens();
/* 4797 */       if (this.overlayClipboard == null)
/* 4798 */         this.interp.error("Overlay clipboard empty");
/* 4799 */       getImage().setOverlay(this.overlayClipboard);
/* 4800 */       return (0.0D / 0.0D);
/*      */     }
/* 4802 */     Overlay overlay = imp.getOverlay();
/* 4803 */     if ((overlay == null) && (name.equals("size")))
/* 4804 */       return 0.0D;
/* 4805 */     if (overlay == null)
/* 4806 */       this.interp.error("No overlay");
/* 4807 */     int size = overlay.size();
/* 4808 */     if ((name.equals("size")) || (name.equals("getSize")))
/* 4809 */       return size;
/* 4810 */     if (name.equals("copy")) {
/* 4811 */       this.interp.getParens();
/* 4812 */       this.overlayClipboard = getImage().getOverlay();
/* 4813 */       return (0.0D / 0.0D);
/* 4814 */     }if ((name.equals("removeSelection")) || (name.equals("removeRoi"))) {
/* 4815 */       int index = (int)getArg();
/* 4816 */       checkIndex(index, 0, size - 1);
/* 4817 */       overlay.remove(index);
/* 4818 */       imp.draw();
/* 4819 */       return (0.0D / 0.0D);
/* 4820 */     }if (name.equals("setPosition")) {
/* 4821 */       int n = (int)getArg();
/* 4822 */       if (size > 0)
/* 4823 */         overlay.get(size - 1).setPosition(n);
/* 4824 */       return (0.0D / 0.0D);
/*      */     }
/* 4826 */     this.interp.error("Unrecognized function name");
/* 4827 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   double overlayMoveTo() {
/* 4831 */     if (this.overlayPath == null) this.overlayPath = new GeneralPath();
/* 4832 */     this.interp.getLeftParen();
/* 4833 */     float x = (float)this.interp.getExpression();
/* 4834 */     this.interp.getComma();
/* 4835 */     float y = (float)this.interp.getExpression();
/* 4836 */     this.interp.getRightParen();
/* 4837 */     this.overlayPath.moveTo(x, y);
/* 4838 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   double overlayLineTo() {
/* 4842 */     if (this.overlayPath == null) this.overlayPath = new GeneralPath();
/* 4843 */     this.interp.getLeftParen();
/* 4844 */     float x = (float)this.interp.getExpression();
/* 4845 */     this.interp.getComma();
/* 4846 */     float y = (float)this.interp.getExpression();
/* 4847 */     this.interp.getRightParen();
/* 4848 */     this.overlayPath.lineTo(x, y);
/* 4849 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   double overlayDrawLine() {
/* 4853 */     if (this.overlayPath == null) this.overlayPath = new GeneralPath();
/* 4854 */     this.interp.getLeftParen();
/* 4855 */     float x1 = (float)this.interp.getExpression();
/* 4856 */     this.interp.getComma();
/* 4857 */     float y1 = (float)this.interp.getExpression();
/* 4858 */     this.interp.getComma();
/* 4859 */     float x2 = (float)this.interp.getExpression();
/* 4860 */     this.interp.getComma();
/* 4861 */     float y2 = (float)this.interp.getExpression();
/* 4862 */     this.interp.getRightParen();
/* 4863 */     this.overlayPath.moveTo(x1, y1);
/* 4864 */     this.overlayPath.lineTo(x2, y2);
/* 4865 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   double overlayDrawRectOrEllipse(ImagePlus imp, boolean ellipse) {
/* 4869 */     addDrawingToOverlay(imp);
/* 4870 */     float x = (float)Math.round(getFirstArg());
/* 4871 */     float y = (float)Math.round(getNextArg());
/* 4872 */     float w = (float)Math.round(getNextArg());
/* 4873 */     float h = (float)Math.round(getLastArg());
/* 4874 */     Shape shape = null;
/* 4875 */     if (ellipse)
/* 4876 */       shape = new Ellipse2D.Float(x, y, w, h);
/*      */     else
/* 4878 */       shape = new Rectangle2D.Float(x, y, w, h);
/* 4879 */     Roi roi = new ShapeRoi(shape);
/* 4880 */     addRoi(imp, roi);
/* 4881 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   double overlayDrawString(ImagePlus imp) {
/* 4885 */     addDrawingToOverlay(imp);
/* 4886 */     String text = getFirstString();
/* 4887 */     int x = (int)getNextArg();
/* 4888 */     int y = (int)getLastArg();
/* 4889 */     Font font = this.font;
/* 4890 */     boolean nullFont = font == null;
/* 4891 */     if (nullFont)
/* 4892 */       font = imp.getProcessor().getFont();
/* 4893 */     FontMetrics metrics = imp.getProcessor().getFontMetrics();
/* 4894 */     int fontHeight = metrics.getHeight();
/* 4895 */     TextRoi roi = new TextRoi(x, y - fontHeight, text, font);
/* 4896 */     if (!nullFont)
/* 4897 */       roi.setAntialiased(this.antialiasedText);
/* 4898 */     addRoi(imp, roi);
/* 4899 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   double addDrawing(ImagePlus imp) {
/* 4903 */     this.interp.getParens();
/* 4904 */     addDrawingToOverlay(imp);
/* 4905 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   void addDrawingToOverlay(ImagePlus imp) {
/* 4909 */     if (this.overlayPath == null) return;
/* 4910 */     Roi roi = new ShapeRoi(this.overlayPath);
/* 4911 */     this.overlayPath = null;
/* 4912 */     addRoi(imp, roi);
/*      */   }
/*      */ 
/*      */   void addRoi(ImagePlus imp, Roi roi) {
/* 4916 */     Overlay overlay = imp.getOverlay();
/* 4917 */     if (overlay == null) {
/* 4918 */       if (this.offscreenOverlay == null)
/* 4919 */         this.offscreenOverlay = new Overlay();
/* 4920 */       overlay = this.offscreenOverlay;
/*      */     }
/* 4922 */     if (this.defaultColor != null)
/* 4923 */       roi.setStrokeColor(this.defaultColor);
/* 4924 */     roi.setLineWidth(getProcessor().getLineWidth());
/* 4925 */     overlay.add(roi);
/*      */   }
/*      */ 
/*      */   double showOverlay(ImagePlus imp) {
/* 4929 */     this.interp.getParens();
/* 4930 */     addDrawingToOverlay(imp);
/* 4931 */     if (this.offscreenOverlay != null) {
/* 4932 */       imp.setOverlay(this.offscreenOverlay);
/* 4933 */       this.offscreenOverlay = null;
/*      */     } else {
/* 4935 */       IJ.run(imp, "Show Overlay", "");
/* 4936 */     }return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   double hideOverlay(ImagePlus imp) {
/* 4940 */     this.interp.getParens();
/* 4941 */     IJ.run(imp, "Hide Overlay", "");
/* 4942 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   double removeOverlay(ImagePlus imp) {
/* 4946 */     this.interp.getParens();
/* 4947 */     imp.setOverlay(null);
/* 4948 */     this.offscreenOverlay = null;
/* 4949 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   final double selectionContains() {
/* 4953 */     int x = (int)Math.round(getFirstArg());
/* 4954 */     int y = (int)Math.round(getLastArg());
/* 4955 */     ImagePlus imp = getImage();
/* 4956 */     Roi roi = imp.getRoi();
/* 4957 */     if (roi == null)
/* 4958 */       this.interp.error("Selection required");
/* 4959 */     return roi.contains(x, y) ? 1.0D : 0.0D;
/*      */   }
/*      */ 
/*      */   void getDisplayedArea() {
/* 4963 */     Variable x = getFirstVariable();
/* 4964 */     Variable y = getNextVariable();
/* 4965 */     Variable w = getNextVariable();
/* 4966 */     Variable h = getLastVariable();
/* 4967 */     ImagePlus imp = getImage();
/* 4968 */     ImageCanvas ic = imp.getCanvas();
/* 4969 */     if (ic == null) return;
/* 4970 */     Rectangle r = ic.getSrcRect();
/* 4971 */     x.setValue(r.x);
/* 4972 */     y.setValue(r.y);
/* 4973 */     w.setValue(r.width);
/* 4974 */     h.setValue(r.height);
/*      */   }
/*      */ 
/*      */   void toScaled() {
/* 4978 */     ImagePlus imp = getImage();
/* 4979 */     int height = imp.getHeight();
/* 4980 */     Calibration cal = imp.getCalibration();
/* 4981 */     this.interp.getLeftParen();
/* 4982 */     if (isArrayArg()) {
/* 4983 */       Variable[] x = getArray();
/* 4984 */       this.interp.getComma();
/* 4985 */       Variable[] y = getArray();
/* 4986 */       this.interp.getRightParen();
/* 4987 */       for (int i = 0; i < x.length; i++)
/* 4988 */         x[i].setValue(cal.getX(x[i].getValue()));
/* 4989 */       for (int i = 0; i < y.length; i++)
/* 4990 */         y[i].setValue(cal.getY(y[i].getValue(), height));
/*      */     } else {
/* 4992 */       Variable xv = getVariable();
/* 4993 */       Variable yv = null;
/* 4994 */       boolean twoArgs = this.interp.nextToken() == 44;
/* 4995 */       if (twoArgs) {
/* 4996 */         this.interp.getComma();
/* 4997 */         yv = getVariable();
/*      */       }
/* 4999 */       this.interp.getRightParen();
/* 5000 */       double x = xv.getValue();
/* 5001 */       if (twoArgs) {
/* 5002 */         double y = yv.getValue();
/* 5003 */         xv.setValue(cal.getX(x));
/* 5004 */         yv.setValue(cal.getY(y, height));
/*      */       } else {
/* 5006 */         xv.setValue(x * cal.pixelWidth);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void toUnscaled() {
/* 5012 */     ImagePlus imp = getImage();
/* 5013 */     int height = imp.getHeight();
/* 5014 */     Calibration cal = imp.getCalibration();
/* 5015 */     this.interp.getLeftParen();
/* 5016 */     if (isArrayArg()) {
/* 5017 */       Variable[] x = getArray();
/* 5018 */       this.interp.getComma();
/* 5019 */       Variable[] y = getArray();
/* 5020 */       this.interp.getRightParen();
/* 5021 */       for (int i = 0; i < x.length; i++)
/* 5022 */         x[i].setValue(cal.getRawX(x[i].getValue()));
/* 5023 */       for (int i = 0; i < y.length; i++)
/* 5024 */         y[i].setValue(cal.getRawY(y[i].getValue(), height));
/*      */     } else {
/* 5026 */       Variable xv = getVariable();
/* 5027 */       Variable yv = null;
/* 5028 */       boolean twoArgs = this.interp.nextToken() == 44;
/* 5029 */       if (twoArgs) {
/* 5030 */         this.interp.getComma();
/* 5031 */         yv = getVariable();
/*      */       }
/* 5033 */       this.interp.getRightParen();
/* 5034 */       double x = xv.getValue();
/* 5035 */       if (twoArgs) {
/* 5036 */         double y = yv.getValue();
/* 5037 */         xv.setValue(cal.getRawX(x));
/* 5038 */         yv.setValue(cal.getRawY(y, height));
/*      */       } else {
/* 5040 */         xv.setValue(x / cal.pixelWidth);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.macro.Functions
 * JD-Core Version:    0.6.2
 */