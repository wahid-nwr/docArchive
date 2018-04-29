/*     */ package ij.plugin.frame;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageListener;
/*     */ import ij.ImagePlus;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GUI;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.TextRoi;
/*     */ import ij.plugin.NewPlugin;
/*     */ import ij.plugin.PlugIn;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Button;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Color;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Font;
/*     */ import java.awt.Label;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.util.Locale;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class Recorder extends PlugInFrame
/*     */   implements PlugIn, ActionListener, ImageListener, ItemListener
/*     */ {
/*     */   public static boolean record;
/*     */   public static boolean recordInMacros;
/*     */   private static final int MACRO = 0;
/*     */   private static final int JAVASCRIPT = 1;
/*     */   private static final int PLUGIN = 2;
/*  26 */   private static final String[] modes = { "Macro", "JavaScript", "Plugin" };
/*     */   private Choice mode;
/*     */   private Button makeMacro;
/*     */   private Button help;
/*     */   private TextField fileName;
/*  30 */   private String fitTypeStr = ij.measure.CurveFitter.fitList[0];
/*     */   private static TextArea textArea;
/*     */   private static Recorder instance;
/*     */   private static String commandName;
/*     */   private static String commandOptions;
/*  35 */   private static String defaultName = "Macro.ijm";
/*  36 */   private static boolean recordPath = true;
/*     */   private static boolean scriptMode;
/*     */   private static boolean imageUpdated;
/*     */   private static int imageID;
/*     */ 
/*     */   public Recorder()
/*     */   {
/*  42 */     super("Recorder");
/*  43 */     if (instance != null) {
/*  44 */       WindowManager.toFront(instance);
/*  45 */       return;
/*     */     }
/*  47 */     WindowManager.addWindow(this);
/*  48 */     instance = this;
/*  49 */     record = true;
/*  50 */     scriptMode = false;
/*  51 */     recordInMacros = false;
/*  52 */     Panel panel = new Panel(new FlowLayout(0, 0, 0));
/*  53 */     panel.add(new Label("  Record:"));
/*  54 */     this.mode = new Choice();
/*  55 */     for (int i = 0; i < modes.length; i++)
/*  56 */       this.mode.addItem(modes[i]);
/*  57 */     this.mode.addItemListener(this);
/*  58 */     this.mode.select(Prefs.get("recorder.mode", modes[0]));
/*  59 */     panel.add(this.mode);
/*  60 */     panel.add(new Label("    Name:"));
/*  61 */     this.fileName = new TextField(defaultName, 15);
/*  62 */     setFileName();
/*  63 */     panel.add(this.fileName);
/*  64 */     panel.add(new Label("   "));
/*  65 */     this.makeMacro = new Button("Create");
/*  66 */     this.makeMacro.addActionListener(this);
/*  67 */     panel.add(this.makeMacro);
/*  68 */     panel.add(new Label("   "));
/*  69 */     this.help = new Button("?");
/*  70 */     this.help.addActionListener(this);
/*  71 */     panel.add(this.help);
/*  72 */     add("North", panel);
/*  73 */     textArea = new TextArea("", 15, 80, 1);
/*  74 */     textArea.setFont(new Font("Monospaced", 0, 12));
/*  75 */     if (IJ.isLinux()) textArea.setBackground(Color.white);
/*  76 */     add("Center", textArea);
/*  77 */     pack();
/*  78 */     GUI.center(this);
/*  79 */     show();
/*  80 */     IJ.register(Recorder.class);
/*     */   }
/*     */ 
/*     */   public static void record(String method) {
/*  84 */     if (textArea == null)
/*  85 */       return;
/*  86 */     textArea.append(method + "();\n");
/*     */   }
/*     */ 
/*     */   public static void setCommand(String command)
/*     */   {
/*  93 */     boolean isMacro = Thread.currentThread().getName().startsWith("Run$_");
/*  94 */     if ((textArea == null) || ((isMacro) && (!recordInMacros)))
/*  95 */       return;
/*  96 */     commandName = command;
/*  97 */     commandOptions = null;
/*  98 */     recordPath = true;
/*  99 */     imageUpdated = false;
/* 100 */     imageID = 0;
/* 101 */     if (scriptMode) {
/* 102 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 103 */       imageID = imp != null ? imp.getID() : 0;
/* 104 */       if (imageID != 0)
/* 105 */         ImagePlus.addImageListener(instance);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getCommand()
/*     */   {
/* 112 */     return commandName;
/*     */   }
/*     */ 
/*     */   static String fixPath(String path) {
/* 116 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 118 */     for (int i = 0; i < path.length(); i++)
/*     */     {
/*     */       char c;
/* 119 */       sb.append(c = path.charAt(i));
/* 120 */       if (c == '\\')
/* 121 */         sb.append("\\");
/*     */     }
/* 123 */     return new String(sb);
/*     */   }
/*     */ 
/*     */   public static void record(String method, String arg) {
/* 127 */     if (IJ.debugMode) IJ.log("record: " + method + "  " + arg);
/* 128 */     boolean sw = method.equals("selectWindow");
/* 129 */     if ((textArea != null) && ((!scriptMode) || (!sw)) && ((commandName == null) || (!sw)))
/* 130 */       if ((scriptMode) && (method.equals("roiManager"))) {
/* 131 */         textArea.append("rm.runCommand(\"" + arg + "\");\n");
/*     */       } else {
/* 133 */         if (method.equals("setTool"))
/* 134 */           method = "//" + (scriptMode ? "IJ." : "") + method;
/* 135 */         textArea.append(method + "(\"" + arg + "\");\n");
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void record(String method, String arg1, String arg2)
/*     */   {
/* 141 */     if (textArea == null) return;
/* 142 */     if ((arg1.equals("Open")) || (arg1.equals("Save")) || (method.equals("saveAs")))
/* 143 */       arg2 = fixPath(arg2);
/* 144 */     if ((scriptMode) && (method.equals("roiManager"))) {
/* 145 */       textArea.append("rm.runCommand(\"" + arg1 + "\", \"" + arg2 + "\");\n");
/*     */     } else {
/* 147 */       if ((scriptMode) && (method.equals("saveAs")))
/* 148 */         method = "IJ." + method;
/* 149 */       textArea.append(method + "(\"" + arg1 + "\", \"" + arg2 + "\");\n");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void record(String method, String arg1, String arg2, String arg3) {
/* 154 */     if (textArea == null) return;
/* 155 */     textArea.append(method + "(\"" + arg1 + "\", \"" + arg2 + "\",\"" + arg3 + "\");\n");
/*     */   }
/*     */ 
/*     */   public static void record(String method, int a1) {
/* 159 */     if (textArea == null) return;
/* 160 */     textArea.append(method + "(" + a1 + ");\n");
/*     */   }
/*     */ 
/*     */   public static void record(String method, int a1, int a2) {
/* 164 */     if (textArea == null) return;
/* 165 */     textArea.append(method + "(" + a1 + ", " + a2 + ");\n");
/*     */   }
/*     */ 
/*     */   public static void record(String method, double a1, double a2) {
/* 169 */     if (textArea == null) return;
/* 170 */     int places = (Math.abs(a1) < 0.0001D) || (Math.abs(a2) < 0.0001D) ? 9 : 4;
/* 171 */     textArea.append(method + "(" + IJ.d2s(a1, places) + ", " + IJ.d2s(a2, places) + ");\n");
/*     */   }
/*     */ 
/*     */   public static void record(String method, int a1, int a2, int a3) {
/* 175 */     if (textArea == null) return;
/* 176 */     if ((scriptMode) && (method.endsWith("groundColor"))) method = "IJ." + method;
/* 177 */     textArea.append(method + "(" + a1 + ", " + a2 + ", " + a3 + ");\n");
/*     */   }
/*     */ 
/*     */   public static void record(String method, String a1, int a2) {
/* 181 */     textArea.append(method + "(\"" + a1 + "\", " + a2 + ");\n");
/*     */   }
/*     */ 
/*     */   public static void record(String method, String args, int a1, int a2) {
/* 185 */     if (textArea == null) return;
/* 186 */     method = "//" + method;
/* 187 */     textArea.append(method + "(\"" + args + "\", " + a1 + ", " + a2 + ");\n");
/*     */   }
/*     */ 
/*     */   public static void record(String method, int a1, int a2, int a3, int a4) {
/* 191 */     if (textArea == null) return;
/* 192 */     if ((scriptMode) && (method.startsWith("make"))) {
/* 193 */       if (method.equals("makeRectangle"))
/* 194 */         recordString("imp.setRoi(" + a1 + ", " + a2 + ", " + a3 + ", " + a4 + ");\n");
/* 195 */       else if (method.equals("makeOval"))
/* 196 */         recordString("imp.setRoi(new OvalRoi(" + a1 + ", " + a2 + ", " + a3 + ", " + a4 + "));\n");
/* 197 */       else if (method.equals("makeLine"))
/* 198 */         recordString("imp.setRoi(new Line(" + a1 + ", " + a2 + ", " + a3 + ", " + a4 + "));\n");
/*     */     }
/* 200 */     else textArea.append(method + "(" + a1 + ", " + a2 + ", " + a3 + ", " + a4 + ");\n"); 
/*     */   }
/*     */ 
/*     */   public static void record(String method, int a1, int a2, int a3, int a4, int a5)
/*     */   {
/* 204 */     textArea.append(method + "(" + a1 + ", " + a2 + ", " + a3 + ", " + a4 + ", " + a5 + ");\n");
/*     */   }
/*     */ 
/*     */   public static void record(String method, int a1, int a2, int a3, int a4, double a5) {
/* 208 */     textArea.append(method + "(" + a1 + ", " + a2 + ", " + a3 + ", " + a4 + ", " + IJ.d2s(a5, 2) + ");\n");
/*     */   }
/*     */ 
/*     */   public static void record(String method, String path, String args, int a1, int a2, int a3, int a4, int a5) {
/* 212 */     if (textArea == null) return;
/* 213 */     path = fixPath(path);
/* 214 */     method = "//" + method;
/* 215 */     textArea.append(method + "(\"" + path + "\", " + "\"" + args + "\", " + a1 + ", " + a2 + ", " + a3 + ", " + a4 + ", " + a5 + ");\n");
/*     */   }
/*     */ 
/*     */   public static void recordString(String str) {
/* 219 */     if (textArea != null)
/* 220 */       textArea.append(str);
/*     */   }
/*     */ 
/*     */   public static void recordCall(String call) {
/* 224 */     if (IJ.debugMode) IJ.log("recordCall: " + call + "  " + commandName);
/* 225 */     if ((textArea != null) && (scriptMode) && (!IJ.macroRunning())) {
/* 226 */       textArea.append(call + "\n");
/* 227 */       commandName = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void recordRoi(Polygon p, int type) {
/* 232 */     if (textArea == null) return;
/* 233 */     if (scriptMode) {
/* 234 */       recordScriptRoi(p, type); return;
/* 235 */     }if ((type == 8) || (type == 10)) {
/* 236 */       String xarr = "newArray("; String yarr = "newArray(";
/* 237 */       xarr = xarr + p.xpoints[0] + ",";
/* 238 */       yarr = yarr + p.ypoints[0] + ",";
/* 239 */       xarr = xarr + p.xpoints[1] + ",";
/* 240 */       yarr = yarr + p.ypoints[1] + ",";
/* 241 */       xarr = xarr + p.xpoints[2] + ")";
/* 242 */       yarr = yarr + p.ypoints[2] + ")";
/* 243 */       String typeStr = type == 8 ? "angle" : "point";
/* 244 */       textArea.append("makeSelection(\"" + typeStr + "\"," + xarr + "," + yarr + ");\n");
/*     */     } else {
/* 246 */       String method = type == 2 ? "makePolygon" : "makeLine";
/* 247 */       StringBuffer args = new StringBuffer();
/* 248 */       for (int i = 0; i < p.npoints; i++) {
/* 249 */         args.append(p.xpoints[i] + ",");
/* 250 */         args.append("" + p.ypoints[i]);
/* 251 */         if (i != p.npoints - 1) args.append(",");
/*     */       }
/* 253 */       textArea.append(method + "(" + args.toString() + ");\n");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void recordScriptRoi(Polygon p, int type) {
/* 258 */     StringBuffer x = new StringBuffer();
/* 259 */     for (int i = 0; i < p.npoints; i++) {
/* 260 */       x.append(p.xpoints[i]);
/* 261 */       if (i != p.npoints - 1) x.append(",");
/*     */     }
/* 263 */     String xpoints = x.toString();
/* 264 */     StringBuffer y = new StringBuffer();
/* 265 */     for (int i = 0; i < p.npoints; i++) {
/* 266 */       y.append(p.ypoints[i]);
/* 267 */       if (i != p.npoints - 1) y.append(",");
/*     */     }
/* 269 */     String ypoints = y.toString();
/*     */ 
/* 271 */     boolean java = (instance != null) && (instance.mode.getSelectedItem().equals(modes[2]));
/* 272 */     if (java) {
/* 273 */       textArea.append("int[] xpoints = {" + xpoints + "};\n");
/* 274 */       textArea.append("int[] ypoints = {" + ypoints + "};\n");
/*     */     } else {
/* 276 */       textArea.append("xpoints = [" + xpoints + "];\n");
/* 277 */       textArea.append("ypoints = [" + ypoints + "];\n");
/*     */     }
/* 279 */     String typeStr = "POLYGON";
/* 280 */     switch (type) { case 6:
/* 281 */       typeStr = "POLYLINE"; break;
/*     */     case 8:
/* 282 */       typeStr = "ANGLE";
/*     */     }
/* 284 */     typeStr = "Roi." + typeStr;
/* 285 */     if (type == 10)
/* 286 */       textArea.append("imp.setRoi(new PointRoi(xpoints,ypoints," + p.npoints + "));\n");
/*     */     else
/* 288 */       textArea.append("imp.setRoi(new PolygonRoi(xpoints,ypoints," + p.npoints + "," + typeStr + "));\n");
/*     */   }
/*     */ 
/*     */   public static void recordOption(String key, String value) {
/* 292 */     if (key == null) return;
/* 293 */     key = trimKey(key);
/* 294 */     value = addQuotes(value);
/* 295 */     checkForDuplicate(key + "=", value);
/* 296 */     if (commandOptions == null)
/* 297 */       commandOptions = key + "=" + value;
/*     */     else
/* 299 */       commandOptions = commandOptions + " " + key + "=" + value;
/*     */   }
/*     */ 
/*     */   public static void recordPath(String key, String path) {
/* 303 */     if ((key == null) || (!recordPath)) {
/* 304 */       recordPath = true;
/* 305 */       return;
/*     */     }
/* 307 */     key = trimKey(key);
/* 308 */     path = fixPath(path);
/* 309 */     path = addQuotes(path);
/* 310 */     checkForDuplicate(key + "=", path);
/* 311 */     if (commandOptions == null)
/* 312 */       commandOptions = key + "=" + path;
/*     */     else
/* 314 */       commandOptions = commandOptions + " " + key + "=" + path;
/*     */   }
/*     */ 
/*     */   public static void recordOption(String key)
/*     */   {
/* 319 */     if (key == null) return;
/* 320 */     if ((commandOptions == null) && (key.equals(" "))) {
/* 321 */       commandOptions = " ";
/*     */     } else {
/* 323 */       key = trimKey(key);
/* 324 */       checkForDuplicate(" " + key, "");
/* 325 */       if (commandOptions == null)
/* 326 */         commandOptions = key;
/*     */       else
/* 328 */         commandOptions = commandOptions + " " + key;
/*     */     }
/*     */   }
/*     */ 
/*     */   static void checkForDuplicate(String key, String value) {
/* 333 */     if ((commandOptions != null) && (commandName != null) && (commandOptions.indexOf(key) != -1) && ((value.equals("")) || (commandOptions.indexOf(value) == -1))) {
/* 334 */       if (key.endsWith("=")) key = key.substring(0, key.length() - 1);
/* 335 */       IJ.showMessage("Recorder", "Duplicate keyword:\n \n    Command: \"" + commandName + "\"\n" + "    Keyword: " + "\"" + key + "\"\n" + "    Value: " + value + "\n \n" + "Add an underscore to the corresponding label\n" + "in the dialog to make the first word unique.");
/*     */     }
/*     */   }
/*     */ 
/*     */   static String trimKey(String key)
/*     */   {
/* 345 */     int index = key.indexOf(" ");
/* 346 */     if (index > -1)
/* 347 */       key = key.substring(0, index);
/* 348 */     index = key.indexOf(":");
/* 349 */     if (index > -1)
/* 350 */       key = key.substring(0, index);
/* 351 */     key = key.toLowerCase(Locale.US);
/* 352 */     return key;
/*     */   }
/*     */ 
/*     */   public static void saveCommand()
/*     */   {
/* 357 */     String name = commandName;
/* 358 */     if (name != null) {
/* 359 */       if ((commandOptions == null) && ((name.equals("Fill")) || (name.equals("Clear"))))
/* 360 */         commandOptions = "slice";
/* 361 */       if (commandOptions != null) {
/* 362 */         if (name.equals("Open...")) {
/* 363 */           String s = scriptMode ? "imp = IJ.openImage" : "open";
/* 364 */           if ((scriptMode) && (isTextOrTable(commandOptions)))
/* 365 */             s = "IJ.open";
/* 366 */           textArea.append(s + "(\"" + strip(commandOptions) + "\");\n");
/* 367 */         } else if (isSaveAs()) {
/* 368 */           if (name.endsWith("..."))
/* 369 */             name = name.substring(0, name.length() - 3);
/* 370 */           String path = strip(commandOptions);
/* 371 */           String s = scriptMode ? "IJ.saveAs(imp, " : "saveAs(";
/* 372 */           textArea.append(s + "\"" + name + "\", \"" + path + "\");\n");
/* 373 */         } else if (name.equals("Image...")) {
/* 374 */           appendNewImage();
/* 375 */         } else if (name.equals("Set Slice...")) {
/* 376 */           textArea.append((scriptMode ? "imp." : "") + "setSlice(" + strip(commandOptions) + ");\n");
/* 377 */         } else if (name.equals("Rename...")) {
/* 378 */           textArea.append((scriptMode ? "imp.setTitle" : "rename") + "(\"" + strip(commandOptions) + "\");\n");
/* 379 */         } else if (name.equals("Wand Tool...")) {
/* 380 */           textArea.append("//run(\"" + name + "\", \"" + commandOptions + "\");\n");
/* 381 */         } else if ((name.equals("Results... ")) && (commandOptions.indexOf(".txt") == -1)) {
/* 382 */           textArea.append((scriptMode ? "IJ." : "") + "open(\"" + strip(commandOptions) + "\");\n");
/* 383 */         } else if (!name.equals("Results..."))
/*     */         {
/* 385 */           if (!name.equals("Run..."))
/*     */           {
/* 388 */             String prefix = "run(";
/* 389 */             if (scriptMode) prefix = imageUpdated ? "IJ.run(imp, " : "IJ.run(";
/* 390 */             textArea.append(prefix + "\"" + name + "\", \"" + commandOptions + "\");\n");
/*     */           }
/*     */         }
/* 393 */       } else if ((name.equals("Threshold...")) || (name.equals("Fonts...")) || (name.equals("Brightness/Contrast...")))
/* 394 */         textArea.append("//run(\"" + name + "\");\n");
/* 395 */       else if (name.equals("Start Animation [\\]"))
/* 396 */         textArea.append("doCommand(\"Start Animation [\\\\]\");\n");
/* 397 */       else if (!name.equals("Add to Manager "))
/*     */       {
/* 399 */         if ((name.equals("Draw")) && (!scriptMode)) {
/* 400 */           ImagePlus imp = WindowManager.getCurrentImage();
/* 401 */           Roi roi = imp.getRoi();
/* 402 */           if ((roi != null) && ((roi instanceof TextRoi)))
/* 403 */             textArea.append(((TextRoi)roi).getMacroCode(imp.getProcessor()));
/*     */           else
/* 405 */             textArea.append("run(\"" + name + "\");\n");
/*     */         } else {
/* 407 */           if ((IJ.altKeyDown()) && ((name.equals("Open Next")) || (name.equals("Plot Profile"))))
/* 408 */             textArea.append("setKeyDown(\"alt\"); ");
/* 409 */           if (scriptMode) {
/* 410 */             String prefix = (imageUpdated) || (name.equals("Select None")) ? "IJ.run(imp, " : "IJ.run(";
/* 411 */             textArea.append(prefix + "\"" + name + "\", \"\");\n");
/*     */           } else {
/* 413 */             textArea.append("run(\"" + name + "\");\n");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 417 */     commandName = null;
/* 418 */     commandOptions = null;
/* 419 */     if (imageID != 0) {
/* 420 */       ImagePlus.removeImageListener(instance);
/* 421 */       imageID = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean isTextOrTable(String path) {
/* 426 */     return (path.endsWith(".txt")) || (path.endsWith(".csv")) || (path.endsWith(".xls"));
/*     */   }
/*     */ 
/*     */   static boolean isSaveAs() {
/* 430 */     return (commandName.equals("Tiff...")) || (commandName.equals("Gif...")) || (commandName.equals("Jpeg...")) || (commandName.equals("Text Image...")) || (commandName.equals("ZIP...")) || (commandName.equals("Raw Data...")) || (commandName.equals("BMP...")) || (commandName.equals("PNG...")) || (commandName.equals("PGM...")) || (commandName.equals("FITS...")) || (commandName.equals("LUT...")) || (commandName.equals("Selection...")) || (commandName.equals("XY Coordinates...")) || (commandName.equals("Text... "));
/*     */   }
/*     */ 
/*     */   static void appendNewImage()
/*     */   {
/* 448 */     String options = getCommandOptions() + " ";
/* 449 */     String title = Macro.getValue(options, "name", "Untitled");
/* 450 */     String type = Macro.getValue(options, "type", "8-bit");
/* 451 */     String fill = Macro.getValue(options, "fill", "");
/* 452 */     if (!fill.equals("")) type = type + " " + fill;
/* 453 */     int width = (int)Tools.parseDouble(Macro.getValue(options, "width", "512"));
/* 454 */     int height = (int)Tools.parseDouble(Macro.getValue(options, "height", "512"));
/* 455 */     int depth = (int)Tools.parseDouble(Macro.getValue(options, "slices", "1"));
/* 456 */     textArea.append((scriptMode ? "imp = IJ.createImage" : "newImage") + "(\"" + title + "\", " + "\"" + type + "\", " + width + ", " + height + ", " + depth + ");\n");
/*     */   }
/*     */ 
/*     */   static String strip(String value)
/*     */   {
/* 461 */     int index = value.indexOf('=');
/* 462 */     if (index >= 0)
/* 463 */       value = value.substring(index + 1);
/* 464 */     if (value.startsWith("[")) {
/* 465 */       int index2 = value.indexOf(']');
/* 466 */       if (index2 == -1) index2 = value.length();
/* 467 */       value = value.substring(1, index2);
/*     */     } else {
/* 469 */       index = value.indexOf(' ');
/* 470 */       if (index != -1)
/* 471 */         value = value.substring(0, index);
/*     */     }
/* 473 */     return value;
/*     */   }
/*     */ 
/*     */   static String addQuotes(String value) {
/* 477 */     int index = value.indexOf(' ');
/* 478 */     if (index > -1)
/* 479 */       value = "[" + value + "]";
/* 480 */     return value;
/*     */   }
/*     */ 
/*     */   public static String getCommandOptions()
/*     */   {
/* 485 */     return commandOptions;
/*     */   }
/*     */ 
/*     */   void createMacro() {
/* 489 */     String text = textArea.getText();
/* 490 */     if ((text == null) || (text.equals(""))) {
/* 491 */       IJ.showMessage("Recorder", "A macro cannot be created until at least\none command has been recorded.");
/* 492 */       return;
/*     */     }
/* 494 */     Editor ed = (Editor)IJ.runPlugIn("ij.plugin.frame.Editor", "");
/* 495 */     if (ed == null)
/* 496 */       return;
/* 497 */     boolean java = this.mode.getSelectedItem().equals(modes[2]);
/* 498 */     String name = this.fileName.getText();
/* 499 */     int dotIndex = name.lastIndexOf(".");
/* 500 */     if (scriptMode) {
/* 501 */       if (dotIndex >= 0) name = name.substring(0, dotIndex);
/* 502 */       if (text.indexOf("rm.") != -1) {
/* 503 */         text = (java ? "RoiManager " : "") + "rm = RoiManager.getInstance();\n" + "if (rm==null) rm = new RoiManager();\n" + "rm.runCommand(\"reset\");\n" + text;
/*     */       }
/*     */ 
/* 508 */       if ((text.indexOf("imp =") == -1) && (text.indexOf("IJ.openImage") == -1) && (text.indexOf("IJ.createImage") == -1))
/* 509 */         text = (java ? "ImagePlus " : "") + "imp = IJ.getImage();\n" + text;
/* 510 */       if ((text.indexOf("imp =") != -1) && (text.indexOf("IJ.getImage") == -1) && (text.indexOf("IJ.saveAs") == -1) && (text.indexOf("imp.close") == -1))
/* 511 */         text = text + "imp.show();\n";
/* 512 */       if (java) {
/* 513 */         name = name + ".java";
/* 514 */         createPlugin(text, name);
/* 515 */         return;
/*     */       }
/* 517 */       name = name + ".js";
/*     */     }
/* 519 */     else if (!name.endsWith(".txt")) {
/* 520 */       if (dotIndex >= 0) name = name.substring(0, dotIndex);
/* 521 */       name = name + ".ijm";
/*     */     }
/*     */ 
/* 524 */     ed.createMacro(name, text);
/*     */   }
/*     */ 
/*     */   void createPlugin(String text, String name) {
/* 528 */     StringTokenizer st = new StringTokenizer(text, "\n");
/* 529 */     int n = st.countTokens();
/* 530 */     boolean impDeclared = false;
/*     */ 
/* 532 */     StringBuffer sb = new StringBuffer();
/* 533 */     for (int i = 0; i < n; i++) {
/* 534 */       String line = st.nextToken();
/* 535 */       if ((line != null) && (line.length() > 3)) {
/* 536 */         sb.append("\t\t");
/* 537 */         if ((line.startsWith("imp =")) && (!impDeclared)) {
/* 538 */           sb.append("ImagePlus ");
/* 539 */           impDeclared = true;
/*     */         }
/* 541 */         sb.append(line);
/* 542 */         sb.append('\n');
/*     */       }
/*     */     }
/* 545 */     String text2 = new String(sb);
/* 546 */     text2 = text2.replaceAll("print", "IJ.log");
/* 547 */     NewPlugin np = (NewPlugin)IJ.runPlugIn("ij.plugin.NewPlugin", text2);
/* 548 */     Editor ed = np.getEditor();
/* 549 */     ed.updateClassName(ed.getTitle(), name);
/* 550 */     ed.setTitle(name);
/*     */   }
/*     */ 
/*     */   public static void disablePathRecording()
/*     */   {
/* 555 */     recordPath = false;
/*     */   }
/*     */ 
/*     */   public static boolean scriptMode() {
/* 559 */     return scriptMode;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 563 */     if (e.getSource() == this.makeMacro)
/* 564 */       createMacro();
/* 565 */     else if (e.getSource() == this.help)
/* 566 */       showHelp();
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/* 570 */     setFileName();
/*     */   }
/*     */ 
/*     */   void setFileName() {
/* 574 */     String name = this.mode.getSelectedItem();
/* 575 */     scriptMode = (name.equals(modes[1])) || (name.equals(modes[2]));
/* 576 */     if (name.equals(modes[0]))
/* 577 */       this.fileName.setText("Macro.ijm");
/* 578 */     else if (name.equals(modes[1]))
/* 579 */       this.fileName.setText("script.js");
/*     */     else
/* 581 */       this.fileName.setText("My_Plugin.java");
/*     */   }
/*     */ 
/*     */   public void imageUpdated(ImagePlus imp) {
/* 585 */     if (imp.getID() == imageID)
/* 586 */       imageUpdated = true; 
/*     */   }
/*     */   public void imageOpened(ImagePlus imp) {
/*     */   }
/*     */ 
/*     */   public void imageClosed(ImagePlus imp) {
/*     */   }
/*     */ 
/* 594 */   void showHelp() { IJ.showMessage("Recorder", "Click \"Create\" to open recorded commands\nas a macro in an editor window.\n \nIn the editor:\n \n    Type ctrl+R (Macros>Run Macro) to\n    run the macro.\n \n    Use File>Save As to save it and\n    ImageJ's Open command to open it.\n \n    To create a command, save in the plugins\n    folder and run Help>Refresh Menus.\n"); }
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 612 */     super.close();
/* 613 */     record = false;
/* 614 */     textArea = null;
/* 615 */     commandName = null;
/* 616 */     instance = null;
/* 617 */     Prefs.set("recorder.mode", this.mode.getSelectedItem());
/*     */   }
/*     */ 
/*     */   public String getText() {
/* 621 */     if (textArea == null) {
/* 622 */       return "";
/*     */     }
/* 624 */     return textArea.getText();
/*     */   }
/*     */ 
/*     */   public static Recorder getInstance() {
/* 628 */     return instance;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.Recorder
 * JD-Core Version:    0.6.2
 */