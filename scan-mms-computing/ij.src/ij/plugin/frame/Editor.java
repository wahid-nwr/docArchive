/*      */ package ij.plugin.frame;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImageJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.Menus;
/*      */ import ij.Prefs;
/*      */ import ij.WindowManager;
/*      */ import ij.gui.GenericDialog;
/*      */ import ij.gui.YesNoCancelDialog;
/*      */ import ij.io.SaveDialog;
/*      */ import ij.macro.FunctionFinder;
/*      */ import ij.macro.Interpreter;
/*      */ import ij.macro.MacroConstants;
/*      */ import ij.macro.MacroRunner;
/*      */ import ij.macro.Program;
/*      */ import ij.plugin.MacroInstaller;
/*      */ import ij.text.TextWindow;
/*      */ import java.awt.CheckboxMenuItem;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Menu;
/*      */ import java.awt.MenuBar;
/*      */ import java.awt.MenuItem;
/*      */ import java.awt.MenuShortcut;
/*      */ import java.awt.PrintGraphics;
/*      */ import java.awt.PrintJob;
/*      */ import java.awt.TextArea;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.datatransfer.Clipboard;
/*      */ import java.awt.datatransfer.ClipboardOwner;
/*      */ import java.awt.datatransfer.DataFlavor;
/*      */ import java.awt.datatransfer.StringSelection;
/*      */ import java.awt.datatransfer.Transferable;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.TextEvent;
/*      */ import java.awt.event.TextListener;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.CharArrayReader;
/*      */ import java.io.EOFException;
/*      */ import java.io.File;
/*      */ import java.io.FileReader;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.LineNumberReader;
/*      */ import java.io.StringReader;
/*      */ import java.util.Locale;
/*      */ import java.util.Properties;
/*      */ 
/*      */ public class Editor extends PlugInFrame
/*      */   implements ActionListener, ItemListener, TextListener, ClipboardOwner, MacroConstants
/*      */ {
/*   22 */   public static String JavaScriptIncludes = "importPackage(Packages.ij);importPackage(Packages.ij.gui);importPackage(Packages.ij.process);importPackage(Packages.ij.measure);importPackage(Packages.ij.util);importPackage(Packages.ij.plugin);importPackage(Packages.ij.io);importPackage(Packages.ij.plugin.filter);importPackage(Packages.ij.plugin.frame);importPackage(java.lang);importPackage(java.awt);importPackage(java.awt.image);importPackage(java.awt.geom);importPackage(java.util);importPackage(java.io);function print(s) {IJ.log(s);};";
/*      */ 
/*   40 */   public static String JS_NOT_FOUND = "JavaScript.jar was not found in the plugins\nfolder. It can be downloaded from:\n \nhttp://imagej.nih.gov/ij/download/tools/JavaScript.jar";
/*      */   public static final int MAX_SIZE = 28000;
/*      */   public static final int XINC = 10;
/*      */   public static final int YINC = 18;
/*      */   public static final int MONOSPACED = 1;
/*      */   public static final int MENU_BAR = 2;
/*      */   public static final int MACROS_MENU_ITEMS = 8;
/*      */   static final String FONT_SIZE = "editor.font.size";
/*      */   static final String FONT_MONO = "editor.font.mono";
/*      */   static final String CASE_SENSITIVE = "editor.case-sensitive";
/*      */   static final String DEFAULT_DIR = "editor.dir";
/*      */   private TextArea ta;
/*      */   private String path;
/*      */   private boolean changes;
/*   52 */   private static String searchString = "";
/*   53 */   private static boolean caseSensitive = Prefs.get("editor.case-sensitive", true);
/*   54 */   private static int lineNumber = 1;
/*      */   private static int xoffset;
/*      */   private static int yoffset;
/*      */   private static int nWindows;
/*      */   private Menu fileMenu;
/*      */   private Menu editMenu;
/*   58 */   private Properties p = new Properties();
/*      */   private int[] macroStarts;
/*      */   private String[] macroNames;
/*      */   private MenuBar mb;
/*      */   private Menu macrosMenu;
/*      */   private int nMacros;
/*      */   private Program pgm;
/*      */   private int eventCount;
/*      */   private String shortcutsInUse;
/*      */   private int inUseCount;
/*      */   private MacroInstaller installer;
/*   69 */   private static String defaultDir = Prefs.get("editor.dir", null);
/*      */   private boolean dontShowWindow;
/*   71 */   private int[] sizes = { 9, 10, 11, 12, 13, 14, 16, 18, 20, 24, 36, 48, 60, 72 };
/*   72 */   private int fontSize = (int)Prefs.get("editor.font.size", 6.0D);
/*      */   private CheckboxMenuItem monospaced;
/*      */   private static boolean wholeWords;
/*      */   private boolean isMacroWindow;
/*      */   private int debugStart;
/*      */   private int debugEnd;
/*      */   private static TextWindow debugWindow;
/*      */   private boolean step;
/*      */   private int previousLine;
/*      */   private static Editor instance;
/*      */   private int runToLine;
/*      */   private boolean fixedLineEndings;
/*      */ 
/*      */   public Editor()
/*      */   {
/*   85 */     this(16, 60, 0, 2);
/*      */   }
/*      */ 
/*      */   public Editor(int rows, int columns, int fontSize, int options) {
/*   89 */     super("Editor");
/*   90 */     WindowManager.addWindow(this);
/*   91 */     addMenuBar(options);
/*   92 */     this.ta = new TextArea(rows, columns);
/*   93 */     this.ta.addTextListener(this);
/*   94 */     if (IJ.isLinux()) this.ta.setBackground(Color.white);
/*   95 */     addKeyListener(IJ.getInstance());
/*   96 */     add(this.ta);
/*   97 */     pack();
/*   98 */     if (fontSize < 0) fontSize = 0;
/*   99 */     if (fontSize >= this.sizes.length) fontSize = this.sizes.length - 1;
/*  100 */     setFont();
/*  101 */     positionWindow();
/*      */   }
/*      */ 
/*      */   void addMenuBar(int options) {
/*  105 */     this.mb = new MenuBar();
/*  106 */     if (Menus.getFontSize() != 0);
/*  107 */     this.mb.setFont(Menus.getFont());
/*  108 */     Menu m = new Menu("File");
/*  109 */     m.add(new MenuItem("New...", new MenuShortcut(78, true)));
/*  110 */     m.add(new MenuItem("Open...", new MenuShortcut(79)));
/*  111 */     m.add(new MenuItem("Save", new MenuShortcut(83)));
/*  112 */     m.add(new MenuItem("Save As..."));
/*  113 */     m.add(new MenuItem("Print...", new MenuShortcut(80)));
/*  114 */     m.addActionListener(this);
/*  115 */     this.fileMenu = m;
/*  116 */     this.mb.add(m);
/*      */ 
/*  118 */     m = new Menu("Edit");
/*  119 */     String key = IJ.isMacintosh() ? "  Cmd " : "  Ctrl+";
/*  120 */     MenuItem item = new MenuItem("Undo" + key + "Z");
/*  121 */     item.setEnabled(false);
/*  122 */     m.add(item);
/*  123 */     m.addSeparator();
/*  124 */     boolean shortcutsBroken = (IJ.isWindows()) && ((System.getProperty("java.version").indexOf("1.1.8") >= 0) || (System.getProperty("java.version").indexOf("1.5.") >= 0));
/*      */ 
/*  127 */     if (shortcutsBroken)
/*  128 */       item = new MenuItem("Cut  Ctrl+X");
/*      */     else
/*  130 */       item = new MenuItem("Cut", new MenuShortcut(88));
/*  131 */     m.add(item);
/*  132 */     if (shortcutsBroken)
/*  133 */       item = new MenuItem("Copy  Ctrl+C");
/*      */     else
/*  135 */       item = new MenuItem("Copy", new MenuShortcut(67));
/*  136 */     m.add(item);
/*  137 */     if (shortcutsBroken)
/*  138 */       item = new MenuItem("Paste  Ctrl+V");
/*      */     else
/*  140 */       item = new MenuItem("Paste", new MenuShortcut(86));
/*  141 */     m.add(item);
/*  142 */     m.addSeparator();
/*  143 */     m.add(new MenuItem("Find...", new MenuShortcut(70)));
/*  144 */     m.add(new MenuItem("Find Next", new MenuShortcut(71)));
/*  145 */     m.add(new MenuItem("Go to Line...", new MenuShortcut(76)));
/*  146 */     m.addSeparator();
/*  147 */     m.add(new MenuItem("Select All", new MenuShortcut(65)));
/*  148 */     m.add(new MenuItem("Zap Gremlins"));
/*  149 */     m.add(new MenuItem("Copy to Image Info"));
/*  150 */     m.addActionListener(this);
/*  151 */     this.mb.add(m);
/*  152 */     this.editMenu = m;
/*  153 */     if ((options & 0x2) != 0) {
/*  154 */       setMenuBar(this.mb);
/*      */     }
/*  156 */     m = new Menu("Font");
/*  157 */     m.add(new MenuItem("Make Text Smaller", new MenuShortcut(78)));
/*  158 */     m.add(new MenuItem("Make Text Larger", new MenuShortcut(77)));
/*  159 */     m.addSeparator();
/*  160 */     this.monospaced = new CheckboxMenuItem("Monospaced Font", Prefs.get("editor.font.mono", false));
/*  161 */     if ((options & 0x1) != 0) this.monospaced.setState(true);
/*  162 */     this.monospaced.addItemListener(this);
/*  163 */     m.add(this.monospaced);
/*  164 */     m.add(new MenuItem("Save Settings"));
/*  165 */     m.addActionListener(this);
/*  166 */     this.mb.add(m);
/*      */   }
/*      */ 
/*      */   public void positionWindow() {
/*  170 */     Dimension screen = IJ.getScreenSize();
/*  171 */     Dimension window = getSize();
/*  172 */     if (window.width == 0)
/*  173 */       return;
/*  174 */     int left = screen.width / 2 - window.width / 2;
/*  175 */     int top = (screen.height - window.height) / 4;
/*  176 */     if (top < 0) top = 0;
/*  177 */     if ((nWindows <= 0) || (xoffset > 80)) {
/*  178 */       xoffset = 0; yoffset = 0;
/*  179 */     }setLocation(left + xoffset, top + yoffset);
/*  180 */     xoffset += 10; yoffset += 18;
/*  181 */     nWindows += 1;
/*      */   }
/*      */ 
/*      */   void setWindowTitle(String title) {
/*  185 */     Menus.updateWindowMenuItem(getTitle(), title);
/*  186 */     setTitle(title);
/*      */   }
/*      */ 
/*      */   public void create(String name, String text) {
/*  190 */     if ((text != null) && (text.length() > 0)) this.fixedLineEndings = true;
/*  191 */     this.ta.append(text);
/*  192 */     if (IJ.isMacOSX()) IJ.wait(25);
/*  193 */     this.ta.setCaretPosition(0);
/*  194 */     setWindowTitle(name);
/*  195 */     boolean macroExtension = (name.endsWith(".txt")) || (name.endsWith(".ijm"));
/*  196 */     if ((macroExtension) || (name.endsWith(".js")) || (name.indexOf(".") == -1)) {
/*  197 */       this.macrosMenu = new Menu("Macros");
/*  198 */       this.macrosMenu.add(new MenuItem("Run Macro", new MenuShortcut(82)));
/*  199 */       this.macrosMenu.add(new MenuItem("Evaluate Line", new MenuShortcut(89)));
/*  200 */       this.macrosMenu.add(new MenuItem("Abort Macro"));
/*  201 */       this.macrosMenu.add(new MenuItem("Install Macros", new MenuShortcut(73)));
/*  202 */       this.macrosMenu.add(new MenuItem("Function Finder...", new MenuShortcut(70, true)));
/*  203 */       this.macrosMenu.addSeparator();
/*  204 */       this.macrosMenu.add(new MenuItem("Evaluate JavaScript", new MenuShortcut(74, false)));
/*  205 */       this.macrosMenu.addSeparator();
/*      */ 
/*  207 */       this.macrosMenu.addActionListener(this);
/*  208 */       this.mb.add(this.macrosMenu);
/*  209 */       if (!name.endsWith(".js")) {
/*  210 */         Menu debugMenu = new Menu("Debug");
/*  211 */         debugMenu.add(new MenuItem("Debug Macro", new MenuShortcut(68)));
/*  212 */         debugMenu.add(new MenuItem("Step", new MenuShortcut(69)));
/*  213 */         debugMenu.add(new MenuItem("Trace", new MenuShortcut(84)));
/*  214 */         debugMenu.add(new MenuItem("Fast Trace", new MenuShortcut(84, true)));
/*  215 */         debugMenu.add(new MenuItem("Run"));
/*  216 */         debugMenu.add(new MenuItem("Run to Insertion Point", new MenuShortcut(69, true)));
/*  217 */         debugMenu.add(new MenuItem("Abort"));
/*  218 */         debugMenu.addActionListener(this);
/*  219 */         this.mb.add(debugMenu);
/*      */       }
/*  221 */       if ((macroExtension) && (text.indexOf("macro ") != -1))
/*  222 */         installMacros(text, false);
/*      */     } else {
/*  224 */       this.fileMenu.addSeparator();
/*  225 */       this.fileMenu.add(new MenuItem("Compile and Run", new MenuShortcut(82)));
/*      */     }
/*  227 */     if ((IJ.getInstance() != null) && (!this.dontShowWindow))
/*  228 */       show();
/*  229 */     if (this.dontShowWindow) {
/*  230 */       dispose();
/*  231 */       this.dontShowWindow = false;
/*      */     }
/*  233 */     WindowManager.setWindow(this);
/*  234 */     this.changes = false;
/*      */   }
/*      */ 
/*      */   public void createMacro(String name, String text) {
/*  238 */     create(name, text);
/*      */   }
/*      */ 
/*      */   void installMacros(String text, boolean installInPluginsMenu) {
/*  242 */     String functions = Interpreter.getAdditionalFunctions();
/*  243 */     if ((functions != null) && (text != null)) {
/*  244 */       if ((!text.endsWith("\n")) && (!functions.startsWith("\n")))
/*  245 */         text = text + "\n" + functions;
/*      */       else
/*  247 */         text = text + functions;
/*      */     }
/*  249 */     this.installer = new MacroInstaller();
/*  250 */     this.installer.setFileName(getTitle());
/*  251 */     int nShortcutsOrTools = this.installer.install(text, this.macrosMenu);
/*  252 */     if ((installInPluginsMenu) || (nShortcutsOrTools > 0))
/*  253 */       this.installer.install(null);
/*  254 */     this.dontShowWindow = this.installer.isAutoRunAndHide();
/*      */   }
/*      */ 
/*      */   public void open(String dir, String name) {
/*  258 */     this.path = (dir + name);
/*  259 */     File file = new File(this.path);
/*      */     try {
/*  261 */       StringBuffer sb = new StringBuffer(5000);
/*  262 */       BufferedReader r = new BufferedReader(new FileReader(file));
/*      */       while (true) {
/*  264 */         String s = r.readLine();
/*  265 */         if (s == null) {
/*      */           break;
/*      */         }
/*  268 */         sb.append(s + "\n");
/*      */       }
/*  270 */       r.close();
/*  271 */       create(name, new String(sb));
/*  272 */       this.changes = false;
/*      */     }
/*      */     catch (Exception e) {
/*  275 */       IJ.handleException(e);
/*  276 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getText() {
/*  281 */     if (this.ta == null) {
/*  282 */       return "";
/*      */     }
/*  284 */     return this.ta.getText();
/*      */   }
/*      */ 
/*      */   public TextArea getTextArea() {
/*  288 */     return this.ta;
/*      */   }
/*      */ 
/*      */   public void display(String title, String text) {
/*  292 */     this.ta.selectAll();
/*  293 */     this.ta.replaceRange(text, this.ta.getSelectionStart(), this.ta.getSelectionEnd());
/*  294 */     this.ta.setCaretPosition(0);
/*  295 */     setWindowTitle(title);
/*  296 */     this.changes = false;
/*  297 */     if (IJ.getInstance() != null)
/*  298 */       show();
/*  299 */     WindowManager.setWindow(this);
/*      */   }
/*      */ 
/*      */   void save() {
/*  303 */     if (this.path == null) {
/*  304 */       saveAs();
/*  305 */       return;
/*      */     }
/*  307 */     File f = new File(this.path);
/*  308 */     if ((f.exists()) && (!f.canWrite())) {
/*  309 */       IJ.showMessage("Editor", "Unable to save because file is write-protected. \n \n" + this.path);
/*  310 */       return;
/*      */     }
/*  312 */     String text = this.ta.getText();
/*  313 */     char[] chars = new char[text.length()];
/*  314 */     text.getChars(0, text.length(), chars, 0);
/*      */     try {
/*  316 */       BufferedReader br = new BufferedReader(new CharArrayReader(chars));
/*  317 */       BufferedWriter bw = new BufferedWriter(new FileWriter(this.path));
/*      */       while (true) {
/*  319 */         String s = br.readLine();
/*  320 */         if (s == null) break;
/*  321 */         bw.write(s, 0, s.length());
/*  322 */         bw.newLine();
/*      */       }
/*  324 */       bw.close();
/*  325 */       IJ.showStatus(text.length() + " chars saved to " + this.path);
/*  326 */       this.changes = false;
/*      */     } catch (IOException e) {
/*      */     }
/*      */   }
/*      */ 
/*      */   void compileAndRun() {
/*  332 */     if (this.path == null)
/*  333 */       saveAs();
/*  334 */     if (this.path != null) {
/*  335 */       save();
/*  336 */       String text = this.ta.getText();
/*  337 */       if ((text.contains("implements PlugInFilter")) && (text.contains("IJ.run(")))
/*  338 */         IJ.log("Plugins that call IJ.run() should probably implement PlugIns, not PlugInFilters.");
/*  339 */       IJ.runPlugIn("ij.plugin.Compiler", this.path);
/*      */     }
/*      */   }
/*      */ 
/*      */   final void runMacro(boolean debug) {
/*  344 */     if (getTitle().endsWith(".js")) {
/*  345 */       evaluateJavaScript(); return;
/*  346 */     }int start = this.ta.getSelectionStart();
/*  347 */     int end = this.ta.getSelectionEnd();
/*      */     String text;
/*      */     String text;
/*  349 */     if (start == end)
/*  350 */       text = this.ta.getText();
/*      */     else
/*  352 */       text = this.ta.getSelectedText();
/*  353 */     new MacroRunner(text, debug ? this : null);
/*      */   }
/*      */ 
/*      */   void evaluateJavaScript() {
/*  357 */     if (!getTitle().endsWith(".js"))
/*  358 */       setTitle(SaveDialog.setExtension(getTitle(), ".js"));
/*  359 */     int start = this.ta.getSelectionStart();
/*  360 */     int end = this.ta.getSelectionEnd();
/*      */     String text;
/*  362 */     if (start == end)
/*  363 */       text = this.ta.getText();
/*      */     else
/*  365 */       text = this.ta.getSelectedText();
/*  366 */     if (text.equals("")) return;
/*  367 */     String text = getJSPrefix("") + text;
/*  368 */     if ((IJ.isJava16()) && ((!IJ.isMacOSX()) || (IJ.is64Bit()))) {
/*  369 */       IJ.runPlugIn("JavaScriptEvaluator", text);
/*      */     } else {
/*  371 */       Object js = IJ.runPlugIn("JavaScript", text);
/*  372 */       if (js == null) IJ.error(JS_NOT_FOUND); 
/*      */     }
/*      */   }
/*      */ 
/*      */   void evaluateLine()
/*      */   {
/*  377 */     int start = this.ta.getSelectionStart();
/*  378 */     int end = this.ta.getSelectionEnd();
/*  379 */     if (end > start) {
/*  380 */       runMacro(false); return;
/*  381 */     }String text = this.ta.getText();
/*  382 */     while (start > 0) {
/*  383 */       start--;
/*  384 */       if (text.charAt(start) == '\n')
/*  385 */         start++;
/*      */     }
/*  387 */     while (end < text.length() - 1) {
/*  388 */       end++;
/*  389 */       if (text.charAt(end) == '\n')
/*  390 */         break;
/*      */     }
/*  392 */     this.ta.setSelectionStart(start);
/*  393 */     this.ta.setSelectionEnd(end);
/*  394 */     runMacro(false);
/*      */   }
/*      */ 
/*      */   void print() {
/*  398 */     PrintJob pjob = Toolkit.getDefaultToolkit().getPrintJob(this, "Cool Stuff", this.p);
/*  399 */     if (pjob != null) {
/*  400 */       Graphics pg = pjob.getGraphics();
/*  401 */       if (pg != null) {
/*  402 */         String s = this.ta.getText();
/*  403 */         printString(pjob, pg, s);
/*  404 */         pg.dispose();
/*      */       }
/*  406 */       pjob.end();
/*      */     }
/*      */   }
/*      */ 
/*      */   void printString(PrintJob pjob, Graphics pg, String s) {
/*  411 */     int pageNum = 1;
/*  412 */     int linesForThisPage = 0;
/*  413 */     int linesForThisJob = 0;
/*  414 */     int topMargin = 30;
/*  415 */     int leftMargin = 30;
/*  416 */     int bottomMargin = 30;
/*      */ 
/*  418 */     if (!(pg instanceof PrintGraphics))
/*  419 */       throw new IllegalArgumentException("Graphics contextt not PrintGraphics");
/*  420 */     if (IJ.isMacintosh()) {
/*  421 */       topMargin = 0;
/*  422 */       leftMargin = 0;
/*  423 */       bottomMargin = 0;
/*      */     }
/*  425 */     StringReader sr = new StringReader(s);
/*  426 */     LineNumberReader lnr = new LineNumberReader(sr);
/*      */ 
/*  428 */     int pageHeight = pjob.getPageDimension().height - bottomMargin;
/*  429 */     Font helv = new Font(getFontName(), 0, 10);
/*  430 */     pg.setFont(helv);
/*  431 */     FontMetrics fm = pg.getFontMetrics(helv);
/*  432 */     int fontHeight = fm.getHeight();
/*  433 */     int fontDescent = fm.getDescent();
/*  434 */     int curHeight = topMargin;
/*      */     try {
/*      */       String nextLine;
/*      */       do { nextLine = lnr.readLine();
/*  438 */         if (nextLine != null) {
/*  439 */           nextLine = detabLine(nextLine);
/*  440 */           if (curHeight + fontHeight > pageHeight)
/*      */           {
/*  442 */             pageNum++;
/*  443 */             linesForThisPage = 0;
/*  444 */             pg.dispose();
/*  445 */             pg = pjob.getGraphics();
/*  446 */             if (pg != null)
/*  447 */               pg.setFont(helv);
/*  448 */             curHeight = topMargin;
/*      */           }
/*  450 */           curHeight += fontHeight;
/*  451 */           if (pg != null) {
/*  452 */             pg.drawString(nextLine, leftMargin, curHeight - fontDescent);
/*  453 */             linesForThisPage++;
/*  454 */             linesForThisJob++;
/*      */           }
/*      */         } }
/*  457 */       while (nextLine != null);
/*      */     } catch (EOFException eof) {
/*      */     }
/*      */     catch (Throwable t) {
/*  461 */       IJ.handleException(t);
/*      */     }
/*      */   }
/*      */ 
/*      */   String detabLine(String s) {
/*  466 */     if (s.indexOf('\t') < 0)
/*  467 */       return s;
/*  468 */     int tabSize = 4;
/*  469 */     StringBuffer sb = new StringBuffer((int)(s.length() * 1.25D));
/*      */ 
/*  471 */     for (int i = 0; i < s.length(); i++) {
/*  472 */       char c = s.charAt(i);
/*  473 */       if (c == '\t')
/*  474 */         for (int j = 0; j < tabSize; j++)
/*  475 */           sb.append(' ');
/*      */       else
/*  477 */         sb.append(c);
/*      */     }
/*  479 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   boolean copy()
/*      */   {
/*  484 */     String s = this.ta.getSelectedText();
/*  485 */     Clipboard clip = getToolkit().getSystemClipboard();
/*  486 */     if (clip != null) {
/*  487 */       StringSelection cont = new StringSelection(s);
/*  488 */       clip.setContents(cont, this);
/*  489 */       return true;
/*      */     }
/*  491 */     return false;
/*      */   }
/*      */ 
/*      */   void cut()
/*      */   {
/*  496 */     if (copy()) {
/*  497 */       int start = this.ta.getSelectionStart();
/*  498 */       int end = this.ta.getSelectionEnd();
/*  499 */       this.ta.replaceRange("", start, end);
/*  500 */       if (IJ.isMacOSX())
/*  501 */         this.ta.setCaretPosition(start);
/*      */     }
/*      */   }
/*      */ 
/*      */   void paste()
/*      */   {
/*  507 */     String s = this.ta.getSelectedText();
/*  508 */     Clipboard clipboard = getToolkit().getSystemClipboard();
/*  509 */     Transferable clipData = clipboard.getContents(s);
/*      */     try {
/*  511 */       s = (String)clipData.getTransferData(DataFlavor.stringFlavor);
/*      */     }
/*      */     catch (Exception e) {
/*  514 */       s = e.toString();
/*      */     }
/*  516 */     if ((!this.fixedLineEndings) && (IJ.isWindows()))
/*  517 */       fixLineEndings();
/*  518 */     this.fixedLineEndings = true;
/*  519 */     int start = this.ta.getSelectionStart();
/*  520 */     int end = this.ta.getSelectionEnd();
/*  521 */     this.ta.replaceRange(s, start, end);
/*  522 */     if (IJ.isMacOSX())
/*  523 */       this.ta.setCaretPosition(start + s.length());
/*      */   }
/*      */ 
/*      */   void copyToInfo() {
/*  527 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  528 */     if (imp == null) {
/*  529 */       IJ.noImage();
/*  530 */       return;
/*      */     }
/*  532 */     int start = this.ta.getSelectionStart();
/*  533 */     int end = this.ta.getSelectionEnd();
/*      */     String text;
/*      */     String text;
/*  535 */     if (start == end)
/*  536 */       text = this.ta.getText();
/*      */     else
/*  538 */       text = this.ta.getSelectedText();
/*  539 */     imp.setProperty("Info", text);
/*      */   }
/*      */ 
/*      */   public void actionPerformed(ActionEvent e) {
/*  543 */     String what = e.getActionCommand();
/*  544 */     int flags = e.getModifiers();
/*  545 */     boolean altKeyDown = (flags & 0x8) != 0;
/*  546 */     if (IJ.debugMode) IJ.log("actionPerformed: " + e);
/*      */ 
/*  548 */     if ("Save".equals(what)) {
/*  549 */       save();
/*  550 */     } else if ("Compile and Run".equals(what)) {
/*  551 */       compileAndRun();
/*  552 */     } else if ("Run Macro".equals(what)) {
/*  553 */       if (altKeyDown) {
/*  554 */         enableDebugging();
/*  555 */         runMacro(true);
/*      */       } else {
/*  557 */         runMacro(false);
/*      */       } } else if ("Debug Macro".equals(what)) {
/*  559 */       enableDebugging();
/*  560 */       runMacro(true);
/*  561 */     } else if ("Step".equals(what)) {
/*  562 */       setDebugMode(1);
/*  563 */     } else if ("Trace".equals(what)) {
/*  564 */       setDebugMode(2);
/*  565 */     } else if ("Fast Trace".equals(what)) {
/*  566 */       setDebugMode(3);
/*  567 */     } else if ("Run".equals(what)) {
/*  568 */       setDebugMode(4);
/*  569 */     } else if ("Run to Insertion Point".equals(what)) {
/*  570 */       runToInsertionPoint();
/*  571 */     } else if (("Abort".equals(what)) || ("Abort Macro".equals(what))) {
/*  572 */       Interpreter.abort();
/*  573 */       IJ.beep();
/*  574 */     } else if ("Evaluate Line".equals(what)) {
/*  575 */       evaluateLine();
/*  576 */     } else if ("Install Macros".equals(what)) {
/*  577 */       installMacros(this.ta.getText(), true);
/*  578 */     } else if ("Function Finder...".equals(what)) {
/*  579 */       new FunctionFinder();
/*  580 */     } else if ("Evaluate JavaScript".equals(what)) {
/*  581 */       evaluateJavaScript();
/*  582 */     } else if ("Print...".equals(what)) {
/*  583 */       print();
/*  584 */     } else if (what.equals("Paste")) {
/*  585 */       paste();
/*  586 */     } else if (what.equals("Copy")) {
/*  587 */       copy();
/*  588 */     } else if (what.equals("Cut")) {
/*  589 */       cut();
/*  590 */     } else if ("Save As...".equals(what)) {
/*  591 */       saveAs();
/*  592 */     } else if ("Select All".equals(what)) {
/*  593 */       selectAll();
/*  594 */     } else if ("Find...".equals(what)) {
/*  595 */       find(null);
/*  596 */     } else if ("Find Next".equals(what)) {
/*  597 */       find(searchString);
/*  598 */     } else if ("Go to Line...".equals(what)) {
/*  599 */       gotoLine();
/*  600 */     } else if ("Zap Gremlins".equals(what)) {
/*  601 */       zapGremlins();
/*  602 */     } else if ("Make Text Larger".equals(what)) {
/*  603 */       changeFontSize(true);
/*  604 */     } else if ("Make Text Smaller".equals(what)) {
/*  605 */       changeFontSize(false);
/*  606 */     } else if ("Save Settings".equals(what)) {
/*  607 */       saveSettings();
/*  608 */     } else if ("New...".equals(what)) {
/*  609 */       IJ.run("Text Window");
/*  610 */     } else if ("Open...".equals(what)) {
/*  611 */       IJ.open();
/*  612 */     } else if (what.equals("Copy to Image Info")) {
/*  613 */       copyToInfo();
/*      */     }
/*  615 */     else if (altKeyDown) {
/*  616 */       enableDebugging();
/*  617 */       this.installer.runMacro(what, this);
/*      */     } else {
/*  619 */       this.installer.runMacro(what, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   final void runToInsertionPoint() {
/*  624 */     Interpreter interp = Interpreter.getInstance();
/*  625 */     if (interp == null) {
/*  626 */       IJ.beep();
/*      */     } else {
/*  628 */       this.runToLine = getCurrentLine();
/*      */ 
/*  630 */       setDebugMode(5);
/*      */     }
/*      */   }
/*      */ 
/*      */   final int getCurrentLine() {
/*  635 */     int pos = this.ta.getCaretPosition();
/*  636 */     int currentLine = 0;
/*  637 */     String text = this.ta.getText();
/*  638 */     if ((IJ.isWindows()) && (!IJ.isVista()))
/*  639 */       text = text.replaceAll("\r\n", "\n");
/*  640 */     char[] chars = new char[text.length()];
/*  641 */     chars = text.toCharArray();
/*  642 */     int count = 0;
/*  643 */     int start = 0; int end = 0;
/*  644 */     int len = chars.length;
/*  645 */     for (int i = 0; i < len; i++) {
/*  646 */       if (chars[i] == '\n') {
/*  647 */         count++;
/*  648 */         start = end;
/*  649 */         end = i;
/*  650 */         if ((pos >= start) && (pos < end)) {
/*  651 */           currentLine = count;
/*  652 */           break;
/*      */         }
/*      */       }
/*      */     }
/*  656 */     if ((currentLine == 0) && (pos > end))
/*  657 */       currentLine = count;
/*  658 */     return currentLine;
/*      */   }
/*      */ 
/*      */   final void enableDebugging() {
/*  662 */     this.step = true;
/*  663 */     Interpreter interp = Interpreter.getInstance();
/*  664 */     if ((interp != null) && (interp.getEditor() == this)) {
/*  665 */       Interpreter.abort();
/*  666 */       IJ.wait(100);
/*      */     }
/*  668 */     int start = this.ta.getSelectionStart();
/*  669 */     int end = this.ta.getSelectionEnd();
/*  670 */     if ((start == this.debugStart) && (end == this.debugEnd))
/*  671 */       this.ta.select(start, start);
/*      */   }
/*      */ 
/*      */   final void setDebugMode(int mode) {
/*  675 */     this.step = true;
/*  676 */     Interpreter interp = Interpreter.getInstance();
/*  677 */     if (interp != null) {
/*  678 */       interp.setEditor(this);
/*  679 */       interp.setDebugMode(mode);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void textValueChanged(TextEvent evt) {
/*  684 */     if (this.isMacroWindow) return;
/*      */ 
/*  686 */     this.eventCount += 1;
/*  687 */     if ((this.eventCount > 2) || ((!IJ.isMacOSX()) && (this.eventCount > 1)))
/*  688 */       this.changes = true;
/*  689 */     if (IJ.isMacOSX())
/*  690 */       this.ta.setCaretPosition(this.ta.getCaretPosition());
/*      */   }
/*      */ 
/*      */   public void itemStateChanged(ItemEvent e) {
/*  694 */     CheckboxMenuItem item = (CheckboxMenuItem)e.getSource();
/*  695 */     setFont();
/*      */   }
/*      */ 
/*      */   public void windowActivated(WindowEvent e)
/*      */   {
/*  701 */     WindowManager.setWindow(this);
/*  702 */     instance = this;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */   {
/*  707 */     boolean okayToClose = true;
/*  708 */     ImageJ ij = IJ.getInstance();
/*  709 */     if ((!getTitle().equals("Errors")) && (this.changes) && (!IJ.isMacro()) && (ij != null)) {
/*  710 */       String msg = "Save changes to \"" + getTitle() + "\"?";
/*  711 */       YesNoCancelDialog d = new YesNoCancelDialog(this, "Editor", msg);
/*  712 */       if (d.cancelPressed())
/*  713 */         okayToClose = false;
/*  714 */       else if (d.yesPressed())
/*  715 */         save();
/*      */     }
/*  717 */     if (okayToClose)
/*      */     {
/*  719 */       dispose();
/*  720 */       WindowManager.removeWindow(this);
/*  721 */       nWindows -= 1;
/*  722 */       instance = null;
/*  723 */       this.changes = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void saveAs() {
/*  728 */     String name1 = getTitle();
/*  729 */     if (name1.indexOf(".") == -1) name1 = name1 + ".txt";
/*  730 */     if (defaultDir == null) {
/*  731 */       if ((name1.endsWith(".txt")) || (name1.endsWith(".ijm")))
/*  732 */         defaultDir = Menus.getMacrosPath();
/*      */       else
/*  734 */         defaultDir = Menus.getPlugInsPath();
/*      */     }
/*  736 */     SaveDialog sd = new SaveDialog("Save As...", defaultDir, name1, null);
/*  737 */     String name2 = sd.getFileName();
/*  738 */     String dir = sd.getDirectory();
/*  739 */     if (name2 != null) {
/*  740 */       if (name2.endsWith(".java"))
/*  741 */         updateClassName(name1, name2);
/*  742 */       this.path = (dir + name2);
/*  743 */       save();
/*  744 */       this.changes = false;
/*  745 */       setWindowTitle(name2);
/*  746 */       setDefaultDirectory(dir);
/*  747 */       if (defaultDir != null)
/*  748 */         Prefs.set("editor.dir", defaultDir);
/*  749 */       if (Recorder.record)
/*  750 */         Recorder.record("saveAs", "Text", this.path);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateClassName(String oldName, String newName)
/*      */   {
/*  756 */     if (newName.indexOf("_") < 0) {
/*  757 */       IJ.showMessage("Plugin Editor", "Plugins without an underscore in their name will not\nbe automatically installed when ImageJ is restarted.");
/*      */     }
/*  759 */     if ((oldName.equals(newName)) || (!oldName.endsWith(".java")) || (!newName.endsWith(".java")))
/*  760 */       return;
/*  761 */     oldName = oldName.substring(0, oldName.length() - 5);
/*  762 */     newName = newName.substring(0, newName.length() - 5);
/*  763 */     String text1 = this.ta.getText();
/*  764 */     int index = text1.indexOf("public class " + oldName);
/*  765 */     if (index < 0)
/*  766 */       return;
/*  767 */     String text2 = text1.substring(0, index + 13) + newName + text1.substring(index + 13 + oldName.length(), text1.length());
/*  768 */     this.ta.setText(text2);
/*      */   }
/*      */ 
/*      */   void find(String s) {
/*  772 */     if (s == null) {
/*  773 */       GenericDialog gd = new GenericDialog("Find", this);
/*  774 */       gd.addStringField("Find: ", searchString, 20);
/*  775 */       String[] labels = { "Case Sensitive", "Whole Words" };
/*  776 */       boolean[] states = { caseSensitive, wholeWords };
/*      */ 
/*  779 */       gd.addCheckboxGroup(1, 2, labels, states);
/*  780 */       gd.showDialog();
/*  781 */       if (gd.wasCanceled())
/*  782 */         return;
/*  783 */       s = gd.getNextString();
/*  784 */       caseSensitive = gd.getNextBoolean();
/*  785 */       wholeWords = gd.getNextBoolean();
/*  786 */       Prefs.set("editor.case-sensitive", caseSensitive);
/*      */     }
/*  788 */     if (s.equals(""))
/*  789 */       return;
/*  790 */     String text = this.ta.getText();
/*  791 */     String s2 = s;
/*  792 */     if (!caseSensitive) {
/*  793 */       text = text.toLowerCase(Locale.US);
/*  794 */       s = s.toLowerCase(Locale.US);
/*      */     }
/*  796 */     int index = -1;
/*  797 */     if (wholeWords) {
/*  798 */       int position = this.ta.getCaretPosition() + 1;
/*      */       do {
/*  800 */         index = text.indexOf(s, position);
/*  801 */         if ((index == -1) || 
/*  802 */           (isWholeWordMatch(text, s, index))) break;
/*  803 */         position = index + 1;
/*  804 */       }while (position < text.length() - 1);
/*  805 */       index = -1;
/*      */     }
/*      */     else {
/*  808 */       index = text.indexOf(s, this.ta.getCaretPosition() + 1);
/*  809 */     }searchString = s2;
/*  810 */     if (index < 0) {
/*  811 */       IJ.beep(); return;
/*  812 */     }this.ta.setSelectionStart(index);
/*  813 */     this.ta.setSelectionEnd(index + s.length());
/*      */   }
/*      */ 
/*      */   boolean isWholeWordMatch(String text, String word, int index) {
/*  817 */     char c = index == 0 ? ' ' : text.charAt(index - 1);
/*  818 */     if ((Character.isLetterOrDigit(c)) || (c == '_')) return false;
/*  819 */     c = index + word.length() >= text.length() ? ' ' : text.charAt(index + word.length());
/*  820 */     if ((Character.isLetterOrDigit(c)) || (c == '_')) return false;
/*  821 */     return true;
/*      */   }
/*      */ 
/*      */   void gotoLine() {
/*  825 */     GenericDialog gd = new GenericDialog("Go to Line", this);
/*  826 */     gd.addNumericField("Go to line number: ", lineNumber, 0);
/*  827 */     gd.showDialog();
/*  828 */     if (gd.wasCanceled())
/*  829 */       return;
/*  830 */     int n = (int)gd.getNextNumber();
/*  831 */     if (n < 1) return;
/*  832 */     String text = this.ta.getText();
/*  833 */     char[] chars = new char[text.length()];
/*  834 */     chars = text.toCharArray();
/*  835 */     int count = 1; int loc = 0;
/*  836 */     for (int i = 0; i < chars.length; i++) {
/*  837 */       if (chars[i] == '\n') count++;
/*  838 */       if (count == n) {
/*  839 */         loc = i + 1; break;
/*      */       }
/*      */     }
/*  841 */     this.ta.setCaretPosition(loc);
/*  842 */     lineNumber = n;
/*      */   }
/*      */ 
/*      */   void zapGremlins() {
/*  846 */     String text = this.ta.getText();
/*  847 */     char[] chars = new char[text.length()];
/*  848 */     chars = text.toCharArray();
/*  849 */     int count = 0;
/*  850 */     boolean inQuotes = false;
/*  851 */     char quoteChar = '\000';
/*  852 */     for (int i = 0; i < chars.length; i++) {
/*  853 */       char c = chars[i];
/*  854 */       if ((!inQuotes) && ((c == '"') || (c == '\''))) {
/*  855 */         inQuotes = true;
/*  856 */         quoteChar = c;
/*      */       }
/*  858 */       else if ((inQuotes) && ((c == quoteChar) || (c == '\n'))) {
/*  859 */         inQuotes = false;
/*      */       }
/*  861 */       if ((!inQuotes) && (c != '\n') && (c != '\t') && ((c < ' ') || (c > ''))) {
/*  862 */         count++;
/*  863 */         chars[i] = ' ';
/*      */       }
/*      */     }
/*      */ 
/*  867 */     if (count > 0) {
/*  868 */       text = new String(chars);
/*  869 */       this.ta.setText(text);
/*      */     }
/*  871 */     if (count > 0)
/*  872 */       IJ.showMessage("Zap Gremlins", count + " invalid characters converted to spaces");
/*      */     else
/*  874 */       IJ.showMessage("Zap Gremlins", "No invalid characters found");
/*      */   }
/*      */ 
/*      */   void selectAll() {
/*  878 */     this.ta.selectAll();
/*      */   }
/*      */ 
/*      */   void changeFontSize(boolean larger) {
/*  882 */     int in = this.fontSize;
/*  883 */     if (larger) {
/*  884 */       this.fontSize += 1;
/*  885 */       if (this.fontSize == this.sizes.length)
/*  886 */         this.fontSize = (this.sizes.length - 1);
/*      */     } else {
/*  888 */       this.fontSize -= 1;
/*  889 */       if (this.fontSize < 0)
/*  890 */         this.fontSize = 0;
/*      */     }
/*  892 */     IJ.showStatus(this.sizes[this.fontSize] + " point");
/*  893 */     setFont();
/*      */   }
/*      */ 
/*      */   void saveSettings() {
/*  897 */     Prefs.set("editor.font.size", this.fontSize);
/*  898 */     Prefs.set("editor.font.mono", this.monospaced.getState());
/*  899 */     IJ.showStatus("Font settings saved (size=" + this.sizes[this.fontSize] + ", monospaced=" + this.monospaced.getState() + ")");
/*      */   }
/*      */ 
/*      */   void setFont() {
/*  903 */     this.ta.setFont(new Font(getFontName(), 0, this.sizes[this.fontSize]));
/*      */   }
/*      */ 
/*      */   String getFontName() {
/*  907 */     return this.monospaced.getState() ? "Monospaced" : "SansSerif";
/*      */   }
/*      */ 
/*      */   public void setFont(Font font) {
/*  911 */     this.ta.setFont(font);
/*      */   }
/*      */ 
/*      */   public void append(String s) {
/*  915 */     this.ta.append(s);
/*      */   }
/*      */ 
/*      */   public void setIsMacroWindow(boolean mw) {
/*  919 */     this.isMacroWindow = mw;
/*      */   }
/*      */ 
/*      */   public static void setDefaultDirectory(String defaultDirectory) {
/*  923 */     defaultDir = defaultDirectory;
/*  924 */     if ((defaultDir != null) && (!defaultDir.endsWith(File.separator)) && (!defaultDir.endsWith("/")))
/*  925 */       defaultDir += File.separator;
/*      */   }
/*      */ 
/*      */   public void lostOwnership(Clipboard clip, Transferable cont)
/*      */   {
/*      */   }
/*      */ 
/*      */   public int debug(Interpreter interp, int mode)
/*      */   {
/*  934 */     if (IJ.debugMode)
/*  935 */       IJ.log("debug: " + interp.getLineNumber() + "  " + mode + "  " + interp);
/*  936 */     if (mode == 4)
/*  937 */       return 0;
/*  938 */     if (!isVisible()) {
/*  939 */       interp.abortMacro();
/*  940 */       return 0;
/*      */     }
/*  942 */     if (!isActive())
/*  943 */       toFront();
/*  944 */     int n = interp.getLineNumber();
/*  945 */     if (n == this.previousLine) {
/*  946 */       this.previousLine = 0; return 0;
/*  947 */     }this.previousLine = n;
/*  948 */     if (mode == 5)
/*  949 */       if (n == this.runToLine) {
/*  950 */         mode = 1;
/*  951 */         interp.setDebugMode(mode);
/*      */       } else {
/*  953 */         return 0;
/*      */       }
/*  955 */     String text = this.ta.getText();
/*  956 */     if ((IJ.isWindows()) && (!IJ.isVista()))
/*  957 */       text = text.replaceAll("\r\n", "\n");
/*  958 */     char[] chars = new char[text.length()];
/*  959 */     chars = text.toCharArray();
/*  960 */     int count = 1;
/*  961 */     this.debugStart = 0;
/*  962 */     int len = chars.length;
/*  963 */     this.debugEnd = len;
/*  964 */     for (int i = 0; i < len; i++) {
/*  965 */       if (chars[i] == '\n') count++;
/*  966 */       if ((count == n) && (this.debugStart == 0)) {
/*  967 */         this.debugStart = (i + 1);
/*  968 */       } else if (count == n + 1) {
/*  969 */         this.debugEnd = i;
/*  970 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  974 */     if (this.debugStart == 1) this.debugStart = 0;
/*  975 */     if (((this.debugStart == 0) || (this.debugStart == len)) && (this.debugEnd == len))
/*  976 */       return 0;
/*  977 */     this.ta.select(this.debugStart, this.debugEnd);
/*  978 */     if ((debugWindow != null) && (!debugWindow.isShowing())) {
/*  979 */       interp.setEditor(null);
/*  980 */       debugWindow = null;
/*      */     } else {
/*  982 */       debugWindow = interp.updateDebugWindow(interp.getVariables(), debugWindow);
/*  983 */     }if (mode == 1) {
/*  984 */       this.step = false;
/*  985 */       while ((!this.step) && (!interp.done()) && (isVisible()))
/*  986 */         IJ.wait(5);
/*      */     }
/*  988 */     if (mode == 3)
/*  989 */       IJ.wait(5);
/*      */     else {
/*  991 */       IJ.wait(150);
/*      */     }
/*  993 */     return 0;
/*      */   }
/*      */ 
/*      */   public static Editor getInstance() {
/*  997 */     return instance;
/*      */   }
/*      */ 
/*      */   public static String getJSPrefix(String arg) {
/* 1001 */     return JavaScriptIncludes + "function getArgument() {return \"" + arg + "\";};";
/*      */   }
/*      */ 
/*      */   public void fixLineEndings()
/*      */   {
/* 1006 */     String text = this.ta.getText();
/* 1007 */     text = text.replaceAll("\r\n", "\n");
/* 1008 */     text = text.replaceAll("\r", "\n");
/* 1009 */     this.ta.setText(text);
/*      */   }
/*      */ 
/*      */   public boolean fileChanged() {
/* 1013 */     return this.changes;
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.Editor
 * JD-Core Version:    0.6.2
 */