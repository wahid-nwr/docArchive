/*      */ package ij;
/*      */ 
/*      */ import ij.plugin.MacroInstaller;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.util.StringSorter;
/*      */ import java.applet.Applet;
/*      */ import java.awt.CheckboxMenuItem;
/*      */ import java.awt.Font;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Menu;
/*      */ import java.awt.MenuBar;
/*      */ import java.awt.MenuItem;
/*      */ import java.awt.MenuShortcut;
/*      */ import java.awt.PopupMenu;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.LineNumberReader;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Vector;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipFile;
/*      */ 
/*      */ public class Menus
/*      */ {
/*      */   public static final char PLUGINS_MENU = 'p';
/*      */   public static final char IMPORT_MENU = 'i';
/*      */   public static final char SAVE_AS_MENU = 's';
/*      */   public static final char SHORTCUTS_MENU = 'h';
/*      */   public static final char ABOUT_MENU = 'a';
/*      */   public static final char FILTERS_MENU = 'f';
/*      */   public static final char TOOLS_MENU = 't';
/*      */   public static final char UTILITIES_MENU = 'u';
/*      */   public static final int WINDOW_MENU_ITEMS = 5;
/*      */   public static final int NORMAL_RETURN = 0;
/*      */   public static final int COMMAND_IN_USE = -1;
/*      */   public static final int INVALID_SHORTCUT = -2;
/*      */   public static final int SHORTCUT_IN_USE = -3;
/*      */   public static final int NOT_INSTALLED = -4;
/*      */   public static final int COMMAND_NOT_FOUND = -5;
/*      */   public static final int MAX_OPEN_RECENT_ITEMS = 15;
/*      */   private static Menus instance;
/*      */   private static MenuBar mbar;
/*      */   private static CheckboxMenuItem gray8Item;
/*      */   private static CheckboxMenuItem gray16Item;
/*      */   private static CheckboxMenuItem gray32Item;
/*      */   private static CheckboxMenuItem color256Item;
/*      */   private static CheckboxMenuItem colorRGBItem;
/*      */   private static CheckboxMenuItem RGBStackItem;
/*      */   private static CheckboxMenuItem HSBStackItem;
/*      */   private static PopupMenu popup;
/*      */   private static ImageJ ij;
/*      */   private static Applet applet;
/*   55 */   private Hashtable demoImagesTable = new Hashtable();
/*      */   private static String pluginsPath;
/*      */   private static String macrosPath;
/*      */   private static Properties menus;
/*      */   private static Properties menuSeparators;
/*      */   private static Menu pluginsMenu;
/*      */   private static Menu saveAsMenu;
/*      */   private static Menu shortcutsMenu;
/*      */   private static Menu utilitiesMenu;
/*      */   private static Menu macrosMenu;
/*      */   static Menu window;
/*      */   static Menu openRecentMenu;
/*      */   private static Hashtable pluginsTable;
/*      */   private static int nPlugins;
/*      */   private static int nMacros;
/*      */   private static Hashtable shortcuts;
/*      */   private static Hashtable macroShortcuts;
/*      */   private static Vector pluginsPrefs;
/*      */   static int windowMenuItems2;
/*      */   private String error;
/*      */   private String jarError;
/*      */   private String pluginError;
/*      */   private boolean isJarErrorHeading;
/*      */   private static boolean installingJars;
/*      */   private static boolean duplicateCommand;
/*      */   private static Vector jarFiles;
/*   74 */   private Map menuEntry2jarFile = new HashMap();
/*      */   private static Vector macroFiles;
/*      */   private static int userPluginsIndex;
/*      */   private static boolean addSorted;
/*   78 */   private static int defaultFontSize = IJ.isWindows() ? 14 : 0;
/*   79 */   private static int fontSize = Prefs.getInt("menu.size", defaultFontSize);
/*      */   private static Font menuFont;
/*      */   static boolean jnlp;
/*      */   static final int RGB_STACK = 10;
/*      */   static final int HSB_STACK = 11;
/*      */ 
/*      */   Menus(ImageJ ijInstance, Applet appletInstance)
/*      */   {
/*   85 */     ij = ijInstance;
/*   86 */     applet = appletInstance;
/*   87 */     instance = this;
/*      */   }
/*      */ 
/*      */   String addMenuBar() {
/*   91 */     nPlugins = Menus.nMacros = Menus.userPluginsIndex = 0;
/*   92 */     addSorted = Menus.installingJars = Menus.duplicateCommand = 0;
/*   93 */     this.error = null;
/*   94 */     mbar = null;
/*   95 */     menus = new Properties();
/*   96 */     pluginsTable = new Hashtable();
/*   97 */     shortcuts = new Hashtable();
/*   98 */     pluginsPrefs = new Vector();
/*   99 */     macroShortcuts = null;
/*  100 */     setupPluginsAndMacrosPaths();
/*  101 */     Menu file = getMenu("File");
/*  102 */     Menu newMenu = getMenu("File>New", true);
/*  103 */     addPlugInItem(file, "Open...", "ij.plugin.Commands(\"open\")", 79, false);
/*  104 */     addPlugInItem(file, "Open Next", "ij.plugin.NextImageOpener", 79, true);
/*  105 */     getMenu("File>Open Samples", true);
/*  106 */     addOpenRecentSubMenu(file);
/*  107 */     Menu importMenu = getMenu("File>Import", true);
/*  108 */     file.addSeparator();
/*  109 */     addPlugInItem(file, "Close", "ij.plugin.Commands(\"close\")", 87, false);
/*  110 */     addPlugInItem(file, "Close All", "ij.plugin.Commands(\"close-all\")", 0, false);
/*  111 */     addPlugInItem(file, "Save", "ij.plugin.Commands(\"save\")", 83, false);
/*  112 */     saveAsMenu = getMenu("File>Save As", true);
/*  113 */     addPlugInItem(file, "Revert", "ij.plugin.Commands(\"revert\")", 82, false);
/*  114 */     file.addSeparator();
/*  115 */     addPlugInItem(file, "Page Setup...", "ij.plugin.filter.Printer(\"setup\")", 0, false);
/*  116 */     addPlugInItem(file, "Print...", "ij.plugin.filter.Printer(\"print\")", 80, false);
/*      */ 
/*  118 */     Menu edit = getMenu("Edit");
/*  119 */     addPlugInItem(edit, "Undo", "ij.plugin.Commands(\"undo\")", 90, false);
/*  120 */     edit.addSeparator();
/*  121 */     addPlugInItem(edit, "Cut", "ij.plugin.Clipboard(\"cut\")", 88, false);
/*  122 */     addPlugInItem(edit, "Copy", "ij.plugin.Clipboard(\"copy\")", 67, false);
/*  123 */     addPlugInItem(edit, "Copy to System", "ij.plugin.Clipboard(\"scopy\")", 0, false);
/*  124 */     addPlugInItem(edit, "Paste", "ij.plugin.Clipboard(\"paste\")", 86, false);
/*  125 */     addPlugInItem(edit, "Paste Control...", "ij.plugin.frame.PasteController", 0, false);
/*  126 */     edit.addSeparator();
/*  127 */     addPlugInItem(edit, "Clear", "ij.plugin.filter.Filler(\"clear\")", 0, false);
/*  128 */     addPlugInItem(edit, "Clear Outside", "ij.plugin.filter.Filler(\"outside\")", 0, false);
/*  129 */     addPlugInItem(edit, "Fill", "ij.plugin.filter.Filler(\"fill\")", 70, false);
/*  130 */     addPlugInItem(edit, "Draw", "ij.plugin.filter.Filler(\"draw\")", 68, false);
/*  131 */     addPlugInItem(edit, "Invert", "ij.plugin.filter.Filters(\"invert\")", 73, true);
/*  132 */     edit.addSeparator();
/*  133 */     getMenu("Edit>Selection", true);
/*  134 */     Menu optionsMenu = getMenu("Edit>Options", true);
/*      */ 
/*  136 */     Menu image = getMenu("Image");
/*  137 */     Menu imageType = getMenu("Image>Type");
/*  138 */     gray8Item = addCheckboxItem(imageType, "8-bit", "ij.plugin.Converter(\"8-bit\")");
/*  139 */     gray16Item = addCheckboxItem(imageType, "16-bit", "ij.plugin.Converter(\"16-bit\")");
/*  140 */     gray32Item = addCheckboxItem(imageType, "32-bit", "ij.plugin.Converter(\"32-bit\")");
/*  141 */     color256Item = addCheckboxItem(imageType, "8-bit Color", "ij.plugin.Converter(\"8-bit Color\")");
/*  142 */     colorRGBItem = addCheckboxItem(imageType, "RGB Color", "ij.plugin.Converter(\"RGB Color\")");
/*  143 */     imageType.add(new MenuItem("-"));
/*  144 */     RGBStackItem = addCheckboxItem(imageType, "RGB Stack", "ij.plugin.Converter(\"RGB Stack\")");
/*  145 */     HSBStackItem = addCheckboxItem(imageType, "HSB Stack", "ij.plugin.Converter(\"HSB Stack\")");
/*  146 */     image.add(imageType);
/*      */ 
/*  148 */     image.addSeparator();
/*  149 */     getMenu("Image>Adjust", true);
/*  150 */     addPlugInItem(image, "Show Info...", "ij.plugin.filter.Info", 73, false);
/*  151 */     addPlugInItem(image, "Properties...", "ij.plugin.filter.ImageProperties", 80, true);
/*  152 */     getMenu("Image>Color", true);
/*  153 */     getMenu("Image>Stacks", true);
/*  154 */     getMenu("Image>Stacks>Tools_", true);
/*  155 */     Menu hyperstacksMenu = getMenu("Image>Hyperstacks", true);
/*  156 */     image.addSeparator();
/*  157 */     addPlugInItem(image, "Crop", "ij.plugin.Resizer(\"crop\")", 88, true);
/*  158 */     addPlugInItem(image, "Duplicate...", "ij.plugin.Duplicator", 68, true);
/*  159 */     addPlugInItem(image, "Rename...", "ij.plugin.SimpleCommands(\"rename\")", 0, false);
/*  160 */     addPlugInItem(image, "Scale...", "ij.plugin.Scaler", 69, false);
/*  161 */     getMenu("Image>Transform", true);
/*  162 */     getMenu("Image>Zoom", true);
/*  163 */     getMenu("Image>Overlay", true);
/*  164 */     image.addSeparator();
/*  165 */     getMenu("Image>Lookup Tables", true);
/*      */ 
/*  167 */     Menu process = getMenu("Process");
/*  168 */     addPlugInItem(process, "Smooth", "ij.plugin.filter.Filters(\"smooth\")", 83, true);
/*  169 */     addPlugInItem(process, "Sharpen", "ij.plugin.filter.Filters(\"sharpen\")", 0, false);
/*  170 */     addPlugInItem(process, "Find Edges", "ij.plugin.filter.Filters(\"edge\")", 0, false);
/*  171 */     addPlugInItem(process, "Find Maxima...", "ij.plugin.filter.MaximumFinder", 0, false);
/*  172 */     addPlugInItem(process, "Enhance Contrast", "ij.plugin.ContrastEnhancer", 0, false);
/*  173 */     getMenu("Process>Noise", true);
/*  174 */     getMenu("Process>Shadows", true);
/*  175 */     getMenu("Process>Binary", true);
/*  176 */     getMenu("Process>Math", true);
/*  177 */     getMenu("Process>FFT", true);
/*  178 */     Menu filtersMenu = getMenu("Process>Filters", true);
/*  179 */     process.addSeparator();
/*  180 */     getMenu("Process>Batch", true);
/*  181 */     addPlugInItem(process, "Image Calculator...", "ij.plugin.ImageCalculator", 0, false);
/*  182 */     addPlugInItem(process, "Subtract Background...", "ij.plugin.filter.BackgroundSubtracter", 0, false);
/*  183 */     addItem(process, "Repeat Command", 82, true);
/*      */ 
/*  185 */     Menu analyzeMenu = getMenu("Analyze");
/*  186 */     addPlugInItem(analyzeMenu, "Measure", "ij.plugin.filter.Analyzer", 77, false);
/*  187 */     addPlugInItem(analyzeMenu, "Analyze Particles...", "ij.plugin.filter.ParticleAnalyzer", 0, false);
/*  188 */     addPlugInItem(analyzeMenu, "Summarize", "ij.plugin.filter.Analyzer(\"sum\")", 0, false);
/*  189 */     addPlugInItem(analyzeMenu, "Distribution...", "ij.plugin.Distribution", 0, false);
/*  190 */     addPlugInItem(analyzeMenu, "Label", "ij.plugin.filter.Filler(\"label\")", 0, false);
/*  191 */     addPlugInItem(analyzeMenu, "Clear Results", "ij.plugin.filter.Analyzer(\"clear\")", 0, false);
/*  192 */     addPlugInItem(analyzeMenu, "Set Measurements...", "ij.plugin.filter.Analyzer(\"set\")", 0, false);
/*  193 */     analyzeMenu.addSeparator();
/*  194 */     addPlugInItem(analyzeMenu, "Set Scale...", "ij.plugin.filter.ScaleDialog", 0, false);
/*  195 */     addPlugInItem(analyzeMenu, "Calibrate...", "ij.plugin.filter.Calibrator", 0, false);
/*  196 */     addPlugInItem(analyzeMenu, "Histogram", "ij.plugin.Histogram", 72, false);
/*  197 */     addPlugInItem(analyzeMenu, "Plot Profile", "ij.plugin.filter.Profiler(\"plot\")", 75, false);
/*  198 */     addPlugInItem(analyzeMenu, "Surface Plot...", "ij.plugin.SurfacePlotter", 0, false);
/*  199 */     getMenu("Analyze>Gels", true);
/*  200 */     Menu toolsMenu = getMenu("Analyze>Tools", true);
/*      */ 
/*  203 */     addPluginsMenu();
/*      */ 
/*  205 */     Menu window = getMenu("Window");
/*  206 */     addPlugInItem(window, "Show All", "ij.plugin.WindowOrganizer(\"show\")", 93, false);
/*  207 */     addPlugInItem(window, "Put Behind [tab]", "ij.plugin.Commands(\"tab\")", 0, false);
/*  208 */     addPlugInItem(window, "Cascade", "ij.plugin.WindowOrganizer(\"cascade\")", 0, false);
/*  209 */     addPlugInItem(window, "Tile", "ij.plugin.WindowOrganizer(\"tile\")", 0, false);
/*  210 */     window.addSeparator();
/*      */ 
/*  212 */     Menu help = getMenu("Help");
/*  213 */     addPlugInItem(help, "ImageJ Website...", "ij.plugin.BrowserLauncher", 0, false);
/*  214 */     addPlugInItem(help, "ImageJ News...", "ij.plugin.BrowserLauncher(\"http://imagej.nih.gov/ij/notes.html\")", 0, false);
/*  215 */     addPlugInItem(help, "Documentation...", "ij.plugin.BrowserLauncher(\"http://imagej.nih.gov/ij/docs\")", 0, false);
/*  216 */     addPlugInItem(help, "Installation...", "ij.plugin.SimpleCommands(\"install\")", 0, false);
/*  217 */     addPlugInItem(help, "Mailing List...", "ij.plugin.BrowserLauncher(\"https://list.nih.gov/archives/imagej.html\")", 0, false);
/*  218 */     help.addSeparator();
/*  219 */     addPlugInItem(help, "Dev. Resources...", "ij.plugin.BrowserLauncher(\"http://imagej.nih.gov/ij/developer/index.html\")", 0, false);
/*  220 */     addPlugInItem(help, "Plugins...", "ij.plugin.BrowserLauncher(\"http://imagej.nih.gov/ij/plugins\")", 0, false);
/*  221 */     addPlugInItem(help, "Macros...", "ij.plugin.BrowserLauncher(\"http://imagej.nih.gov/ij/macros/\")", 0, false);
/*  222 */     addPlugInItem(help, "Macro Functions...", "ij.plugin.BrowserLauncher(\"http://imagej.nih.gov/ij/developer/macro/functions.html\")", 0, false);
/*  223 */     help.addSeparator();
/*  224 */     addPlugInItem(help, "Update ImageJ...", "ij.plugin.ImageJ_Updater", 0, false);
/*  225 */     addPlugInItem(help, "Refresh Menus", "ij.plugin.ImageJ_Updater(\"menus\")", 0, false);
/*  226 */     help.addSeparator();
/*  227 */     Menu aboutMenu = getMenu("Help>About Plugins", true);
/*  228 */     addPlugInItem(help, "About ImageJ...", "ij.plugin.AboutBox", 0, false);
/*      */ 
/*  230 */     if (applet == null) {
/*  231 */       menuSeparators = new Properties();
/*  232 */       installPlugins();
/*      */     }
/*      */ 
/*  236 */     file.addSeparator();
/*  237 */     addPlugInItem(file, "Quit", "ij.plugin.Commands(\"quit\")", 0, false);
/*      */ 
/*  239 */     if (fontSize != 0)
/*  240 */       mbar.setFont(getFont());
/*  241 */     if (ij != null) {
/*  242 */       ij.setMenuBar(mbar);
/*      */     }
/*  244 */     if (this.pluginError != null)
/*  245 */       this.error = (this.error != null ? (this.error = this.error + "\n" + this.pluginError) : this.pluginError);
/*  246 */     if (this.jarError != null)
/*  247 */       this.error = (this.error != null ? (this.error = this.error + "\n" + this.jarError) : this.jarError);
/*  248 */     return this.error;
/*      */   }
/*      */ 
/*      */   void addOpenRecentSubMenu(Menu menu) {
/*  252 */     openRecentMenu = getMenu("File>Open Recent");
/*  253 */     for (int i = 0; i < 15; i++) {
/*  254 */       String path = Prefs.getString("recent" + i / 10 % 10 + i % 10);
/*  255 */       if (path == null) break;
/*  256 */       MenuItem item = new MenuItem(path);
/*  257 */       openRecentMenu.add(item);
/*  258 */       item.addActionListener(ij);
/*      */     }
/*  260 */     menu.add(openRecentMenu);
/*      */   }
/*      */ 
/*      */   static void addItem(Menu menu, String label, int shortcut, boolean shift) {
/*  264 */     if (menu == null)
/*      */       return;
/*      */     MenuItem item;
/*      */     MenuItem item;
/*  267 */     if (shortcut == 0) {
/*  268 */       item = new MenuItem(label);
/*      */     }
/*  270 */     else if (shift) {
/*  271 */       MenuItem item = new MenuItem(label, new MenuShortcut(shortcut, true));
/*  272 */       shortcuts.put(new Integer(shortcut + 200), label);
/*      */     } else {
/*  274 */       item = new MenuItem(label, new MenuShortcut(shortcut));
/*  275 */       shortcuts.put(new Integer(shortcut), label);
/*      */     }
/*      */ 
/*  278 */     if (addSorted) {
/*  279 */       if (menu == pluginsMenu)
/*  280 */         addItemSorted(menu, item, userPluginsIndex);
/*      */       else
/*  282 */         addOrdered(menu, item);
/*      */     }
/*  284 */     else menu.add(item);
/*  285 */     item.addActionListener(ij);
/*      */   }
/*      */ 
/*      */   void addPlugInItem(Menu menu, String label, String className, int shortcut, boolean shift) {
/*  289 */     pluginsTable.put(label, className);
/*  290 */     nPlugins += 1;
/*  291 */     addItem(menu, label, shortcut, shift);
/*      */   }
/*      */ 
/*      */   CheckboxMenuItem addCheckboxItem(Menu menu, String label, String className) {
/*  295 */     pluginsTable.put(label, className);
/*  296 */     nPlugins += 1;
/*  297 */     CheckboxMenuItem item = new CheckboxMenuItem(label);
/*  298 */     menu.add(item);
/*  299 */     item.addItemListener(ij);
/*  300 */     item.setState(false);
/*  301 */     return item;
/*      */   }
/*      */ 
/*      */   static Menu addSubMenu(Menu menu, String name)
/*      */   {
/*  306 */     String key = name.toLowerCase(Locale.US);
/*      */ 
/*  308 */     Menu submenu = new Menu(name.replace('_', ' '));
/*  309 */     int index = key.indexOf(' ');
/*  310 */     if (index > 0)
/*  311 */       key = key.substring(0, index);
/*  312 */     for (int count = 1; count < 100; count++) {
/*  313 */       String value = Prefs.getString(key + count / 10 % 10 + count % 10);
/*  314 */       if (value == null)
/*      */         break;
/*  316 */       if (count == 1)
/*  317 */         menu.add(submenu);
/*  318 */       if (value.equals("-"))
/*  319 */         submenu.addSeparator();
/*      */       else
/*  321 */         addPluginItem(submenu, value);
/*      */     }
/*  323 */     if ((name.equals("Lookup Tables")) && (applet == null))
/*  324 */       addLuts(submenu);
/*  325 */     return submenu;
/*      */   }
/*      */ 
/*      */   static void addLuts(Menu submenu) {
/*  329 */     String path = IJ.getDirectory("luts");
/*  330 */     if (path == null) return;
/*  331 */     File f = new File(path);
/*  332 */     String[] list = null;
/*  333 */     if ((applet == null) && (f.exists()) && (f.isDirectory()))
/*  334 */       list = f.list();
/*  335 */     if (list == null) return;
/*  336 */     if (IJ.isLinux()) StringSorter.sort(list);
/*  337 */     submenu.addSeparator();
/*  338 */     for (int i = 0; i < list.length; i++) {
/*  339 */       String name = list[i];
/*  340 */       if (name.endsWith(".lut")) {
/*  341 */         name = name.substring(0, name.length() - 4);
/*  342 */         MenuItem item = new MenuItem(name);
/*  343 */         submenu.add(item);
/*  344 */         item.addActionListener(ij);
/*  345 */         nPlugins += 1;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static void addPluginItem(Menu submenu, String s) {
/*  351 */     if (s.startsWith("\"-\""))
/*      */     {
/*  353 */       addSeparator(submenu);
/*  354 */       return;
/*      */     }
/*  356 */     int lastComma = s.lastIndexOf(',');
/*  357 */     if (lastComma <= 0)
/*  358 */       return;
/*  359 */     String command = s.substring(1, lastComma - 1);
/*  360 */     int keyCode = 0;
/*  361 */     boolean shift = false;
/*  362 */     if (command.endsWith("]")) {
/*  363 */       int openBracket = command.lastIndexOf('[');
/*  364 */       if (openBracket > 0) {
/*  365 */         String shortcut = command.substring(openBracket + 1, command.length() - 1);
/*  366 */         keyCode = convertShortcutToCode(shortcut);
/*  367 */         boolean functionKey = (keyCode >= 112) && (keyCode <= 123);
/*  368 */         if ((keyCode > 0) && (!functionKey)) {
/*  369 */           command = command.substring(0, openBracket);
/*      */         }
/*      */       }
/*      */     }
/*  373 */     if ((keyCode >= 112) && (keyCode <= 123)) {
/*  374 */       shortcuts.put(new Integer(keyCode), command);
/*  375 */       keyCode = 0;
/*  376 */     } else if ((keyCode >= 265) && (keyCode <= 290)) {
/*  377 */       keyCode -= 200;
/*  378 */       shift = true;
/*      */     }
/*  380 */     addItem(submenu, command, keyCode, shift);
/*  381 */     while ((s.charAt(lastComma + 1) == ' ') && (lastComma + 2 < s.length()))
/*  382 */       lastComma++;
/*  383 */     String className = s.substring(lastComma + 1, s.length());
/*      */ 
/*  385 */     if (installingJars)
/*  386 */       duplicateCommand = pluginsTable.get(command) != null;
/*  387 */     pluginsTable.put(command, className);
/*  388 */     nPlugins += 1;
/*      */   }
/*      */ 
/*      */   void checkForDuplicate(String command) {
/*  392 */     if (pluginsTable.get(command) != null);
/*      */   }
/*      */ 
/*      */   void addPluginsMenu()
/*      */   {
/*  400 */     pluginsMenu = getMenu("Plugins");
/*  401 */     for (int count = 1; count < 100; count++) {
/*  402 */       String value = Prefs.getString("plug-in" + count / 10 % 10 + count % 10);
/*  403 */       if (value == null)
/*      */         break;
/*  405 */       char firstChar = value.charAt(0);
/*  406 */       if (firstChar == '-') {
/*  407 */         pluginsMenu.addSeparator();
/*  408 */       } else if (firstChar == '>') {
/*  409 */         String submenu = value.substring(2, value.length() - 1);
/*      */ 
/*  411 */         Menu menu = addSubMenu(pluginsMenu, submenu);
/*  412 */         if (submenu.equals("Shortcuts"))
/*  413 */           shortcutsMenu = menu;
/*  414 */         else if (submenu.equals("Utilities"))
/*  415 */           utilitiesMenu = menu;
/*  416 */         else if (submenu.equals("Macros"))
/*  417 */           macrosMenu = menu;
/*      */       } else {
/*  419 */         addPluginItem(pluginsMenu, value);
/*      */       }
/*      */     }
/*  421 */     userPluginsIndex = pluginsMenu.getItemCount();
/*  422 */     if (userPluginsIndex < 0) userPluginsIndex = 0;
/*      */   }
/*      */ 
/*      */   void installPlugins()
/*      */   {
/*  432 */     String[] pluginList = getPlugins();
/*  433 */     String[] pluginsList2 = null;
/*  434 */     Hashtable skipList = new Hashtable();
/*  435 */     for (int index = 0; index < 100; index++) {
/*  436 */       String value = Prefs.getString("plugin" + index / 10 % 10 + index % 10);
/*  437 */       if (value == null)
/*      */         break;
/*  439 */       char menuCode = value.charAt(0);
/*      */       Menu menu;
/*  440 */       switch (menuCode) { case 'b':
/*      */       case 'c':
/*      */       case 'd':
/*      */       case 'e':
/*      */       case 'g':
/*      */       case 'j':
/*      */       case 'k':
/*      */       case 'l':
/*      */       case 'm':
/*      */       case 'n':
/*      */       case 'o':
/*      */       case 'p':
/*      */       case 'q':
/*      */       case 'r':
/*      */       default:
/*  441 */         menu = pluginsMenu; break;
/*      */       case 'i':
/*  442 */         menu = getMenu("File>Import"); break;
/*      */       case 's':
/*  443 */         menu = getMenu("File>Save As"); break;
/*      */       case 'h':
/*  444 */         menu = shortcutsMenu; break;
/*      */       case 'a':
/*  445 */         menu = getMenu("Help>About Plugins"); break;
/*      */       case 'f':
/*  446 */         menu = getMenu("Process>Filters"); break;
/*      */       case 't':
/*  447 */         menu = getMenu("Analyze>Tools"); break;
/*      */       case 'u':
/*  448 */         menu = utilitiesMenu;
/*      */       }
/*  450 */       String prefsValue = value;
/*  451 */       value = value.substring(2, value.length());
/*  452 */       String className = value.substring(value.lastIndexOf(',') + 1, value.length());
/*  453 */       boolean found = className.startsWith("ij.");
/*  454 */       if ((!found) && (pluginList != null)) {
/*  455 */         if (pluginsList2 == null)
/*  456 */           pluginsList2 = getStrippedPlugins(pluginList);
/*  457 */         for (int i = 0; i < pluginsList2.length; i++) {
/*  458 */           if (className.startsWith(pluginsList2[i])) {
/*  459 */             found = true;
/*  460 */             break;
/*      */           }
/*      */         }
/*      */       }
/*  464 */       if ((found) && (menu != pluginsMenu)) {
/*  465 */         addPluginItem(menu, value);
/*  466 */         pluginsPrefs.addElement(prefsValue);
/*  467 */         if (className.endsWith("\")")) {
/*  468 */           int argStart = className.lastIndexOf("(\"");
/*  469 */           if (argStart > 0)
/*  470 */             className = className.substring(0, argStart);
/*      */         }
/*  472 */         skipList.put(className, "");
/*      */       }
/*      */     }
/*  475 */     if (pluginList != null) {
/*  476 */       for (int i = 0; i < pluginList.length; i++) {
/*  477 */         if (!skipList.containsKey(pluginList[i]))
/*  478 */           installUserPlugin(pluginList[i]);
/*      */       }
/*      */     }
/*  481 */     installJarPlugins();
/*  482 */     installMacros();
/*      */   }
/*      */ 
/*      */   void installMacros()
/*      */   {
/*  487 */     if (macroFiles == null)
/*  488 */       return;
/*  489 */     for (int i = 0; i < macroFiles.size(); i++) {
/*  490 */       String name = (String)macroFiles.elementAt(i);
/*  491 */       installMacro(name);
/*      */     }
/*      */   }
/*      */ 
/*      */   void installMacro(String name)
/*      */   {
/*  498 */     Menu menu = pluginsMenu;
/*  499 */     String dir = null;
/*  500 */     int slashIndex = name.indexOf('/');
/*  501 */     if (slashIndex > 0) {
/*  502 */       dir = name.substring(0, slashIndex);
/*  503 */       name = name.substring(slashIndex + 1, name.length());
/*  504 */       menu = getPluginsSubmenu(dir);
/*  505 */       slashIndex = name.indexOf('/');
/*  506 */       if (slashIndex > 0) {
/*  507 */         String dir2 = name.substring(0, slashIndex);
/*  508 */         name = name.substring(slashIndex + 1, name.length());
/*  509 */         String menuName = "Plugins>" + dir + ">" + dir2;
/*  510 */         menu = getMenu(menuName);
/*  511 */         dir = dir + File.separator + dir2;
/*      */       }
/*      */     }
/*  514 */     String command = name.replace('_', ' ');
/*  515 */     if (command.endsWith(".js"))
/*  516 */       command = command.substring(0, command.length() - 3);
/*      */     else
/*  518 */       command = command.substring(0, command.length() - 4);
/*  519 */     command.trim();
/*  520 */     if (pluginsTable.get(command) != null)
/*  521 */       command = command + " Macro";
/*  522 */     MenuItem item = new MenuItem(command);
/*  523 */     addOrdered(menu, item);
/*  524 */     item.addActionListener(ij);
/*  525 */     String path = (dir != null ? dir + File.separator : "") + name;
/*  526 */     pluginsTable.put(command, "ij.plugin.Macro_Runner(\"" + path + "\")");
/*  527 */     nMacros += 1;
/*      */   }
/*      */ 
/*      */   static int addPluginSeparatorIfNeeded(Menu menu) {
/*  531 */     if (menuSeparators == null)
/*  532 */       return 0;
/*  533 */     Integer i = (Integer)menuSeparators.get(menu);
/*  534 */     if (i == null) {
/*  535 */       if (menu.getItemCount() > 0)
/*  536 */         addSeparator(menu);
/*  537 */       i = new Integer(menu.getItemCount());
/*  538 */       menuSeparators.put(menu, i);
/*      */     }
/*  540 */     return i.intValue();
/*      */   }
/*      */ 
/*      */   static void addOrdered(Menu menu, MenuItem item)
/*      */   {
/*  545 */     String label = item.getLabel();
/*  546 */     int start = addPluginSeparatorIfNeeded(menu);
/*  547 */     for (int i = start; i < menu.getItemCount(); i++) {
/*  548 */       if (label.compareTo(menu.getItem(i).getLabel()) < 0) {
/*  549 */         menu.insert(item, i);
/*  550 */         return;
/*      */       }
/*      */     }
/*  553 */     menu.add(item);
/*      */   }
/*      */ 
/*      */   public static String getJarFileForMenuEntry(String menuEntry) {
/*  557 */     if (instance == null)
/*  558 */       return null;
/*  559 */     return (String)instance.menuEntry2jarFile.get(menuEntry);
/*      */   }
/*      */ 
/*      */   void installJarPlugins()
/*      */   {
/*  564 */     if (jarFiles == null)
/*  565 */       return;
/*  566 */     installingJars = true;
/*  567 */     for (int i = 0; i < jarFiles.size(); i++) {
/*  568 */       this.isJarErrorHeading = false;
/*  569 */       String jar = (String)jarFiles.elementAt(i);
/*  570 */       InputStream is = getConfigurationFile(jar);
/*  571 */       if (is != null) {
/*  572 */         int maxEntries = 100;
/*  573 */         String[] entries = new String[maxEntries];
/*  574 */         int nEntries = 0;
/*  575 */         LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
/*      */         try {
/*      */           while (true) {
/*  578 */             String s = lnr.readLine();
/*  579 */             if ((s == null) || (nEntries == maxEntries - 1)) break;
/*  580 */             if ((s.length() >= 3) && (!s.startsWith("#")))
/*  581 */               entries[(nEntries++)] = s;
/*      */           }
/*      */         } catch (IOException e) {
/*      */         } finally {
/*      */           try {
/*  586 */             if (lnr != null) lnr.close(); 
/*      */           } catch (IOException e) {  }
/*      */ 
/*      */         }
/*  589 */         for (int j = 0; j < nEntries; j++)
/*  590 */           installJarPlugin(jar, entries[j]);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void installJarPlugin(String jar, String s) {
/*  596 */     addSorted = false;
/*      */     Menu menu;
/*      */     Menu menu;
/*  598 */     if (s.startsWith("Plugins>")) {
/*  599 */       int firstComma = s.indexOf(',');
/*      */       Menu menu;
/*  600 */       if ((firstComma == -1) || (firstComma <= 8)) {
/*  601 */         menu = null;
/*      */       } else {
/*  603 */         String name = s.substring(8, firstComma);
/*  604 */         menu = getPluginsSubmenu(name);
/*      */       }
/*  606 */     } else if ((s.startsWith("\"")) || (s.startsWith("Plugins"))) {
/*  607 */       String name = getSubmenuName(jar);
/*      */       Menu menu;
/*      */       Menu menu;
/*  608 */       if (name != null)
/*  609 */         menu = getPluginsSubmenu(name);
/*      */       else
/*  611 */         menu = pluginsMenu;
/*  612 */       addSorted = true;
/*      */     } else {
/*  614 */       int firstQuote = s.indexOf('"');
/*  615 */       String name = firstQuote < 0 ? s : s.substring(0, firstQuote).trim();
/*      */ 
/*  617 */       int comma = name.indexOf(',');
/*  618 */       if (comma >= 0)
/*  619 */         name = name.substring(0, comma);
/*  620 */       if (name.startsWith("Help>About"))
/*  621 */         name = "Help>About Plugins";
/*  622 */       menu = getMenu(name);
/*      */     }
/*  624 */     int firstQuote = s.indexOf('"');
/*  625 */     if (firstQuote == -1)
/*  626 */       return;
/*  627 */     s = s.substring(firstQuote, s.length());
/*  628 */     if (menu != null) {
/*  629 */       addPluginSeparatorIfNeeded(menu);
/*  630 */       addPluginItem(menu, s);
/*  631 */       addSorted = false;
/*      */     }
/*  633 */     String menuEntry = s;
/*  634 */     if (s.startsWith("\"")) {
/*  635 */       int quote = s.indexOf('"', 1);
/*  636 */       menuEntry = quote < 0 ? s.substring(1) : s.substring(1, quote);
/*      */     } else {
/*  638 */       int comma = s.indexOf(',');
/*  639 */       if (comma > 0)
/*  640 */         menuEntry = s.substring(0, comma);
/*      */     }
/*  642 */     if (duplicateCommand) {
/*  643 */       if (this.jarError == null) this.jarError = "";
/*  644 */       addJarErrorHeading(jar);
/*  645 */       String jar2 = (String)this.menuEntry2jarFile.get(menuEntry);
/*  646 */       if ((jar2 != null) && (jar2.startsWith(pluginsPath)))
/*  647 */         jar2 = jar2.substring(pluginsPath.length());
/*  648 */       this.jarError = (this.jarError + "    Duplicate command: " + s + (jar2 != null ? " (already in " + jar2 + ")" : "") + "\n");
/*      */     }
/*      */     else
/*      */     {
/*  652 */       this.menuEntry2jarFile.put(menuEntry, jar);
/*  653 */     }duplicateCommand = false;
/*      */   }
/*      */ 
/*      */   void addJarErrorHeading(String jar) {
/*  657 */     if (!this.isJarErrorHeading) {
/*  658 */       if (!this.jarError.equals(""))
/*  659 */         this.jarError += " \n";
/*  660 */       this.jarError = (this.jarError + "Plugin configuration error: " + jar + "\n");
/*  661 */       this.isJarErrorHeading = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Menu getMenu(String menuName) {
/*  666 */     return getMenu(menuName, false);
/*      */   }
/*      */ 
/*      */   private static Menu getMenu(String menuName, boolean readFromProps) {
/*  670 */     if (menuName.endsWith(">"))
/*  671 */       menuName = menuName.substring(0, menuName.length() - 1);
/*  672 */     Menu result = (Menu)menus.get(menuName);
/*  673 */     if (result == null) {
/*  674 */       int offset = menuName.lastIndexOf('>');
/*  675 */       if (offset < 0) {
/*  676 */         result = new Menu(menuName);
/*  677 */         if (mbar == null)
/*  678 */           mbar = new MenuBar();
/*  679 */         if (menuName.equals("Help"))
/*  680 */           mbar.setHelpMenu(result);
/*      */         else
/*  682 */           mbar.add(result);
/*  683 */         if (menuName.equals("Window"))
/*  684 */           window = result;
/*  685 */         else if (menuName.equals("Plugins"))
/*  686 */           pluginsMenu = result;
/*      */       } else {
/*  688 */         String parentName = menuName.substring(0, offset);
/*  689 */         String menuItemName = menuName.substring(offset + 1);
/*  690 */         Menu parentMenu = getMenu(parentName);
/*  691 */         result = new Menu(menuItemName);
/*  692 */         addPluginSeparatorIfNeeded(parentMenu);
/*  693 */         if (readFromProps)
/*  694 */           result = addSubMenu(parentMenu, menuItemName);
/*  695 */         else if ((parentName.startsWith("Plugins")) && (menuSeparators != null))
/*  696 */           addItemSorted(parentMenu, result, parentName.equals("Plugins") ? userPluginsIndex : 0);
/*      */         else
/*  698 */           parentMenu.add(result);
/*  699 */         if (menuName.equals("File>Open Recent"))
/*  700 */           openRecentMenu = result;
/*      */       }
/*  702 */       menus.put(menuName, result);
/*      */     }
/*  704 */     return result;
/*      */   }
/*      */ 
/*      */   Menu getPluginsSubmenu(String submenuName) {
/*  708 */     return getMenu("Plugins>" + submenuName);
/*      */   }
/*      */ 
/*      */   String getSubmenuName(String jarPath)
/*      */   {
/*  713 */     if (pluginsPath == null)
/*  714 */       return null;
/*  715 */     if (jarPath.startsWith(pluginsPath))
/*  716 */       jarPath = jarPath.substring(pluginsPath.length() - 1);
/*  717 */     int index = jarPath.lastIndexOf(File.separatorChar);
/*  718 */     if (index < 0) return null;
/*  719 */     String name = jarPath.substring(0, index);
/*  720 */     index = name.lastIndexOf(File.separatorChar);
/*  721 */     if (index < 0) return null;
/*  722 */     name = name.substring(index + 1);
/*  723 */     if (name.equals("plugins")) return null;
/*  724 */     return name;
/*      */   }
/*      */ 
/*      */   static void addItemSorted(Menu menu, MenuItem item, int startingIndex) {
/*  728 */     String itemLabel = item.getLabel();
/*  729 */     int count = menu.getItemCount();
/*  730 */     boolean inserted = false;
/*  731 */     for (int i = startingIndex; i < count; i++) {
/*  732 */       MenuItem mi = menu.getItem(i);
/*  733 */       String label = mi.getLabel();
/*      */ 
/*  735 */       if (itemLabel.compareTo(label) < 0) {
/*  736 */         menu.insert(item, i);
/*  737 */         inserted = true;
/*  738 */         break;
/*      */       }
/*      */     }
/*  741 */     if (!inserted) menu.add(item); 
/*      */   }
/*      */ 
/*      */   static void addSeparator(Menu menu)
/*      */   {
/*  745 */     menu.addSeparator();
/*      */   }
/*      */ 
/*      */   InputStream getConfigurationFile(String jar)
/*      */   {
/*      */     try {
/*  751 */       ZipFile jarFile = new ZipFile(jar);
/*  752 */       Enumeration entries = jarFile.entries();
/*  753 */       while (entries.hasMoreElements()) {
/*  754 */         ZipEntry entry = (ZipEntry)entries.nextElement();
/*  755 */         if (entry.getName().endsWith("plugins.config"))
/*  756 */           return jarFile.getInputStream(entry);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  760 */       IJ.log(jar + ": " + e);
/*      */     }
/*  762 */     return autoGenerateConfigFile(jar);
/*      */   }
/*      */ 
/*      */   InputStream autoGenerateConfigFile(String jar)
/*      */   {
/*  767 */     StringBuffer sb = null;
/*      */     try {
/*  769 */       ZipFile jarFile = new ZipFile(jar);
/*  770 */       Enumeration entries = jarFile.entries();
/*  771 */       while (entries.hasMoreElements()) {
/*  772 */         ZipEntry entry = (ZipEntry)entries.nextElement();
/*  773 */         String name = entry.getName();
/*  774 */         if ((name.endsWith(".class")) && (name.indexOf("_") > 0) && (name.indexOf("$") == -1) && (name.indexOf("/_") == -1) && (!name.startsWith("_")))
/*      */         {
/*  776 */           if ((!Character.isLowerCase(name.charAt(0))) || (name.indexOf("/") == -1))
/*      */           {
/*  778 */             if (sb == null) sb = new StringBuffer();
/*  779 */             String className = name.substring(0, name.length() - 6);
/*  780 */             int slashIndex = className.lastIndexOf('/');
/*  781 */             String plugins = "Plugins";
/*  782 */             if (slashIndex >= 0) {
/*  783 */               plugins = plugins + ">" + className.substring(0, slashIndex).replace('/', '>').replace('_', ' ');
/*  784 */               name = className.substring(slashIndex + 1);
/*      */             } else {
/*  786 */               name = className;
/*  787 */             }name = name.replace('_', ' ');
/*  788 */             className = className.replace('/', '.');
/*      */ 
/*  790 */             sb.append(plugins + ", \"" + name + "\", " + className + "\n");
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Throwable e) {
/*  795 */       IJ.log(jar + ": " + e);
/*      */     }
/*      */ 
/*  798 */     if (sb == null) {
/*  799 */       return null;
/*      */     }
/*  801 */     return new ByteArrayInputStream(sb.toString().getBytes());
/*      */   }
/*      */ 
/*      */   String[] getStrippedPlugins(String[] plugins)
/*      */   {
/*  806 */     String[] plugins2 = new String[plugins.length];
/*      */ 
/*  808 */     for (int i = 0; i < plugins2.length; i++) {
/*  809 */       plugins2[i] = plugins[i];
/*  810 */       int slashPos = plugins2[i].lastIndexOf('/');
/*  811 */       if (slashPos >= 0)
/*  812 */         plugins2[i] = plugins[i].substring(slashPos + 1, plugins2[i].length());
/*      */     }
/*  814 */     return plugins2;
/*      */   }
/*      */ 
/*      */   void setupPluginsAndMacrosPaths() {
/*  818 */     pluginsPath = Menus.macrosPath = null;
/*  819 */     String homeDir = Prefs.getHomeDir();
/*  820 */     if (homeDir == null) return;
/*  821 */     if (homeDir.endsWith("plugins")) {
/*  822 */       pluginsPath = homeDir + Prefs.separator;
/*      */     } else {
/*  824 */       String property = System.getProperty("plugins.dir");
/*  825 */       if ((property != null) && ((property.endsWith("/")) || (property.endsWith("\\"))))
/*  826 */         property = property.substring(0, property.length() - 1);
/*  827 */       String pluginsDir = property;
/*  828 */       if (pluginsDir == null) {
/*  829 */         pluginsDir = homeDir;
/*  830 */       } else if (pluginsDir.equals("user.home")) {
/*  831 */         pluginsDir = System.getProperty("user.home");
/*  832 */         if (!new File(pluginsDir + Prefs.separator + "plugins").isDirectory())
/*  833 */           pluginsDir = pluginsDir + Prefs.separator + "ImageJ";
/*  834 */         property = null;
/*      */ 
/*  836 */         if (applet == null) System.setSecurityManager(null);
/*  837 */         jnlp = true;
/*      */       }
/*  839 */       pluginsPath = pluginsDir + Prefs.separator + "plugins" + Prefs.separator;
/*  840 */       if ((property != null) && (!new File(pluginsPath).isDirectory()))
/*  841 */         pluginsPath = pluginsDir + Prefs.separator;
/*  842 */       macrosPath = pluginsDir + Prefs.separator + "macros" + Prefs.separator;
/*      */     }
/*  844 */     File f = macrosPath != null ? new File(macrosPath) : null;
/*  845 */     if ((f != null) && (!f.isDirectory()))
/*  846 */       macrosPath = null;
/*  847 */     f = pluginsPath != null ? new File(pluginsPath) : null;
/*  848 */     if ((f == null) || ((f != null) && (!f.isDirectory()))) {
/*  849 */       pluginsPath = null;
/*  850 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static synchronized String[] getPlugins()
/*      */   {
/*  856 */     File f = pluginsPath != null ? new File(pluginsPath) : null;
/*  857 */     if ((f == null) || ((f != null) && (!f.isDirectory())))
/*  858 */       return null;
/*  859 */     String[] list = f.list();
/*  860 */     if (list == null)
/*  861 */       return null;
/*  862 */     Vector v = new Vector();
/*  863 */     jarFiles = null;
/*  864 */     macroFiles = null;
/*  865 */     for (int i = 0; i < list.length; i++) {
/*  866 */       String name = list[i];
/*  867 */       boolean isClassFile = name.endsWith(".class");
/*  868 */       boolean hasUnderscore = name.indexOf('_') >= 0;
/*  869 */       if ((hasUnderscore) && (isClassFile) && (name.indexOf('$') < 0)) {
/*  870 */         name = name.substring(0, name.length() - 6);
/*  871 */         v.addElement(name);
/*  872 */       } else if ((hasUnderscore) && ((name.endsWith(".jar")) || (name.endsWith(".zip")))) {
/*  873 */         if (jarFiles == null) jarFiles = new Vector();
/*  874 */         jarFiles.addElement(pluginsPath + name);
/*  875 */       } else if (((hasUnderscore) && (name.endsWith(".txt"))) || (name.endsWith(".ijm")) || (name.endsWith(".js"))) {
/*  876 */         if (macroFiles == null) macroFiles = new Vector();
/*  877 */         macroFiles.addElement(name);
/*      */       }
/*  879 */       else if (!isClassFile) {
/*  880 */         checkSubdirectory(pluginsPath, name, v);
/*      */       }
/*      */     }
/*  883 */     list = new String[v.size()];
/*  884 */     v.copyInto((String[])list);
/*  885 */     StringSorter.sort(list);
/*  886 */     return list;
/*      */   }
/*      */ 
/*      */   private static void checkSubdirectory(String path, String dir, Vector v)
/*      */   {
/*  891 */     if (dir.endsWith(".java"))
/*  892 */       return;
/*  893 */     File f = new File(path, dir);
/*  894 */     if (!f.isDirectory())
/*  895 */       return;
/*  896 */     String[] list = f.list();
/*  897 */     if (list == null)
/*  898 */       return;
/*  899 */     dir = dir + "/";
/*  900 */     int classCount = 0; int otherCount = 0;
/*  901 */     String className = null;
/*  902 */     for (int i = 0; i < list.length; i++) {
/*  903 */       String name = list[i];
/*  904 */       boolean hasUnderscore = name.indexOf('_') >= 0;
/*  905 */       if ((hasUnderscore) && (name.endsWith(".class")) && (name.indexOf('$') < 0)) {
/*  906 */         name = name.substring(0, name.length() - 6);
/*  907 */         v.addElement(dir + name);
/*  908 */         classCount++;
/*  909 */         className = name;
/*      */       }
/*  911 */       else if ((hasUnderscore) && ((name.endsWith(".jar")) || (name.endsWith(".zip")))) {
/*  912 */         if (jarFiles == null) jarFiles = new Vector();
/*  913 */         jarFiles.addElement(f.getPath() + File.separator + name);
/*  914 */         otherCount++;
/*  915 */       } else if (((hasUnderscore) && (name.endsWith(".txt"))) || (name.endsWith(".ijm")) || (name.endsWith(".js"))) {
/*  916 */         if (macroFiles == null) macroFiles = new Vector();
/*  917 */         macroFiles.addElement(dir + name);
/*  918 */         otherCount++;
/*      */       } else {
/*  920 */         File f2 = new File(f, name);
/*  921 */         if (f2.isDirectory()) installSubdirectorMacros(f2, dir + name);
/*      */       }
/*      */     }
/*  924 */     if ((Prefs.moveToMisc) && (classCount == 1) && (otherCount == 0) && (dir.indexOf("_") == -1))
/*  925 */       v.setElementAt("Miscellaneous/" + className, v.size() - 1);
/*      */   }
/*      */ 
/*      */   private static void installSubdirectorMacros(File f2, String dir)
/*      */   {
/*  931 */     if (dir.endsWith("Launchers")) return;
/*  932 */     String[] list = f2.list();
/*  933 */     if (list == null) return;
/*  934 */     for (int i = 0; i < list.length; i++) {
/*  935 */       String name = list[i];
/*  936 */       boolean hasUnderscore = name.indexOf('_') >= 0;
/*  937 */       if (((hasUnderscore) && (name.endsWith(".txt"))) || (name.endsWith(".ijm")) || (name.endsWith(".js"))) {
/*  938 */         if (macroFiles == null) macroFiles = new Vector();
/*  939 */         macroFiles.addElement(dir + "/" + name);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void installUserPlugin(String className)
/*      */   {
/*  947 */     installUserPlugin(className, false);
/*      */   }
/*      */ 
/*      */   public void installUserPlugin(String className, boolean force) {
/*  951 */     int slashIndex = className.indexOf('/');
/*  952 */     String menuName = "Plugins>" + className.substring(0, slashIndex).replace('/', '>');
/*      */ 
/*  954 */     Menu menu = getMenu(menuName);
/*  955 */     String command = className;
/*  956 */     if (slashIndex > 0) {
/*  957 */       command = className.substring(slashIndex + 1);
/*      */     }
/*  959 */     command = command.replace('_', ' ');
/*  960 */     command.trim();
/*  961 */     boolean itemExists = pluginsTable.get(command) != null;
/*  962 */     if ((force) && (itemExists)) {
/*  963 */       return;
/*      */     }
/*  965 */     if ((!force) && (itemExists))
/*  966 */       command = command + " Plugin";
/*  967 */     MenuItem item = new MenuItem(command);
/*  968 */     if (force)
/*  969 */       addItemSorted(menu, item, 0);
/*      */     else
/*  971 */       addOrdered(menu, item);
/*  972 */     item.addActionListener(ij);
/*  973 */     pluginsTable.put(command, className.replace('/', '.'));
/*  974 */     nPlugins += 1;
/*      */   }
/*      */ 
/*      */   void installPopupMenu(ImageJ ij)
/*      */   {
/*  979 */     int count = 0;
/*      */ 
/*  981 */     popup = new PopupMenu("");
/*  982 */     if (fontSize != 0)
/*  983 */       popup.setFont(getFont());
/*      */     while (true)
/*      */     {
/*  986 */       count++;
/*  987 */       String s = Prefs.getString("popup" + count / 10 % 10 + count % 10);
/*  988 */       if (s == null)
/*      */         break;
/*  990 */       if (s.equals("-")) {
/*  991 */         popup.addSeparator();
/*  992 */       } else if (!s.equals("")) {
/*  993 */         MenuItem mi = new MenuItem(s);
/*  994 */         mi.addActionListener(ij);
/*  995 */         popup.add(mi);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static MenuBar getMenuBar() {
/* 1001 */     return mbar;
/*      */   }
/*      */ 
/*      */   public static Menu getMacrosMenu() {
/* 1005 */     return macrosMenu;
/*      */   }
/*      */ 
/*      */   public int getMacroCount() {
/* 1009 */     return nMacros;
/*      */   }
/*      */ 
/*      */   public int getPluginCount() {
/* 1013 */     return nPlugins;
/*      */   }
/*      */ 
/*      */   public static void updateMenus()
/*      */   {
/* 1020 */     if (ij == null) return;
/* 1021 */     gray8Item.setState(false);
/* 1022 */     gray16Item.setState(false);
/* 1023 */     gray32Item.setState(false);
/* 1024 */     color256Item.setState(false);
/* 1025 */     colorRGBItem.setState(false);
/* 1026 */     RGBStackItem.setState(false);
/* 1027 */     HSBStackItem.setState(false);
/* 1028 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1029 */     if (imp == null)
/* 1030 */       return;
/* 1031 */     int type = imp.getType();
/* 1032 */     if (imp.getStackSize() > 1) {
/* 1033 */       ImageStack stack = imp.getStack();
/* 1034 */       if (stack.isRGB()) type = 10;
/* 1035 */       else if (stack.isHSB()) type = 11;
/*      */     }
/* 1037 */     if (type == 0) {
/* 1038 */       ImageProcessor ip = imp.getProcessor();
/* 1039 */       if ((ip != null) && (ip.getMinThreshold() == -808080.0D) && (ip.isColorLut()) && (!ip.isPseudoColorLut())) {
/* 1040 */         type = 3;
/* 1041 */         imp.setType(3);
/*      */       }
/*      */     }
/* 1044 */     switch (type) {
/*      */     case 0:
/* 1046 */       gray8Item.setState(true);
/* 1047 */       break;
/*      */     case 1:
/* 1049 */       gray16Item.setState(true);
/* 1050 */       break;
/*      */     case 2:
/* 1052 */       gray32Item.setState(true);
/* 1053 */       break;
/*      */     case 3:
/* 1055 */       color256Item.setState(true);
/* 1056 */       break;
/*      */     case 4:
/* 1058 */       colorRGBItem.setState(true);
/* 1059 */       break;
/*      */     case 10:
/* 1061 */       RGBStackItem.setState(true);
/* 1062 */       break;
/*      */     case 11:
/* 1064 */       HSBStackItem.setState(true);
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/* 1069 */     case 9: } int nItems = window.getItemCount();
/* 1070 */     int start = 5 + windowMenuItems2;
/* 1071 */     int index = start + WindowManager.getCurrentIndex();
/*      */     try {
/* 1073 */       for (int i = start; i < nItems; i++) {
/* 1074 */         CheckboxMenuItem item = (CheckboxMenuItem)window.getItem(i);
/* 1075 */         item.setState(i == index);
/*      */       }
/*      */     } catch (Exception e) {
/*      */     }
/*      */   }
/*      */ 
/* 1081 */   static boolean isColorLut(ImagePlus imp) { ImageProcessor ip = imp.getProcessor();
/* 1082 */     IndexColorModel cm = (IndexColorModel)ip.getColorModel();
/* 1083 */     if (cm == null) return false;
/* 1084 */     int mapSize = cm.getMapSize();
/* 1085 */     byte[] reds = new byte[mapSize];
/* 1086 */     byte[] greens = new byte[mapSize];
/* 1087 */     byte[] blues = new byte[mapSize];
/* 1088 */     cm.getReds(reds);
/* 1089 */     cm.getGreens(greens);
/* 1090 */     cm.getBlues(blues);
/* 1091 */     boolean isColor = false;
/* 1092 */     for (int i = 0; i < mapSize; i++) {
/* 1093 */       if ((reds[i] != greens[i]) || (greens[i] != blues[i])) {
/* 1094 */         isColor = true;
/* 1095 */         break;
/*      */       }
/*      */     }
/* 1098 */     return isColor;
/*      */   }
/*      */ 
/*      */   public static String getPlugInsPath()
/*      */   {
/* 1105 */     return pluginsPath;
/*      */   }
/*      */ 
/*      */   public static String getMacrosPath()
/*      */   {
/* 1111 */     return macrosPath;
/*      */   }
/*      */ 
/*      */   public static Hashtable getCommands()
/*      */   {
/* 1116 */     return pluginsTable;
/*      */   }
/*      */ 
/*      */   public static Hashtable getShortcuts()
/*      */   {
/* 1122 */     return shortcuts;
/*      */   }
/*      */ 
/*      */   public static Hashtable getMacroShortcuts()
/*      */   {
/* 1128 */     if (macroShortcuts == null)
/* 1129 */       macroShortcuts = new Hashtable();
/* 1130 */     return macroShortcuts;
/*      */   }
/*      */ 
/*      */   static synchronized void insertWindowMenuItem(Frame win)
/*      */   {
/* 1135 */     if ((ij == null) || (win == null))
/* 1136 */       return;
/* 1137 */     CheckboxMenuItem item = new CheckboxMenuItem(win.getTitle());
/* 1138 */     item.addItemListener(ij);
/* 1139 */     int index = 5 + windowMenuItems2;
/* 1140 */     if (windowMenuItems2 >= 2)
/* 1141 */       index--;
/* 1142 */     window.insert(item, index);
/* 1143 */     windowMenuItems2 += 1;
/* 1144 */     if (windowMenuItems2 == 1) {
/* 1145 */       window.insertSeparator(5 + windowMenuItems2);
/* 1146 */       windowMenuItems2 += 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   static synchronized void addWindowMenuItem(ImagePlus imp)
/*      */   {
/* 1154 */     if (ij == null) return;
/* 1155 */     String name = imp.getTitle();
/* 1156 */     int size = imp.getWidth() * imp.getHeight() * imp.getStackSize() / 1024;
/* 1157 */     switch (imp.getType()) { case 2:
/*      */     case 4:
/* 1159 */       size *= 4;
/* 1160 */       break;
/*      */     case 1:
/* 1162 */       size *= 2;
/* 1163 */       break;
/*      */     case 3:
/*      */     }
/*      */ 
/* 1167 */     CheckboxMenuItem item = new CheckboxMenuItem(name + " " + size + "K");
/* 1168 */     window.add(item);
/* 1169 */     item.addItemListener(ij);
/*      */   }
/*      */ 
/*      */   static synchronized void removeWindowMenuItem(int index)
/*      */   {
/* 1175 */     if (ij == null) return; try
/*      */     {
/* 1177 */       if ((index >= 0) && (index < window.getItemCount())) {
/* 1178 */         window.remove(5 + index);
/* 1179 */         if (index < windowMenuItems2) {
/* 1180 */           windowMenuItems2 -= 1;
/* 1181 */           if (windowMenuItems2 == 1) {
/* 1182 */             window.remove(5);
/* 1183 */             windowMenuItems2 = 0;
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Exception e) {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static synchronized void updateWindowMenuItem(String oldLabel, String newLabel) {
/* 1192 */     if ((oldLabel == null) || (oldLabel.equals(newLabel)))
/* 1193 */       return;
/* 1194 */     int first = 5;
/* 1195 */     int last = window.getItemCount() - 1;
/*      */     try
/*      */     {
/* 1198 */       for (int i = first; i <= last; i++) {
/* 1199 */         MenuItem item = window.getItem(i);
/*      */ 
/* 1201 */         String label = item.getLabel();
/* 1202 */         if ((item != null) && (label.startsWith(oldLabel))) {
/* 1203 */           if (label.endsWith("K")) {
/* 1204 */             int index = label.lastIndexOf(' ');
/* 1205 */             if (index > -1)
/* 1206 */               newLabel = newLabel + label.substring(index, label.length());
/*      */           }
/* 1208 */           item.setLabel(newLabel);
/* 1209 */           return;
/*      */         }
/*      */       }
/*      */     } catch (NullPointerException e) {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static synchronized void addOpenRecentItem(String path) {
/* 1217 */     if (ij == null) return;
/* 1218 */     int count = openRecentMenu.getItemCount();
/* 1219 */     for (int i = 0; i < count; )
/* 1220 */       if (openRecentMenu.getItem(i).getLabel().equals(path)) {
/* 1221 */         openRecentMenu.remove(i);
/* 1222 */         count--;
/*      */       } else {
/* 1224 */         i++;
/*      */       }
/* 1226 */     if (count == 15)
/* 1227 */       openRecentMenu.remove(14);
/* 1228 */     MenuItem item = new MenuItem(path);
/* 1229 */     openRecentMenu.insert(item, 0);
/* 1230 */     item.addActionListener(ij);
/*      */   }
/*      */ 
/*      */   public static PopupMenu getPopupMenu() {
/* 1234 */     return popup;
/*      */   }
/*      */ 
/*      */   public static Menu getSaveAsMenu() {
/* 1238 */     return saveAsMenu;
/*      */   }
/*      */ 
/*      */   public static int installPlugin(String plugin, char menuCode, String command, String shortcut, ImageJ ij)
/*      */   {
/* 1251 */     if (command.equals(""))
/*      */     {
/* 1256 */       return 0;
/*      */     }
/*      */ 
/* 1259 */     if (commandInUse(command))
/* 1260 */       return -1;
/* 1261 */     if (!validShortcut(shortcut))
/* 1262 */       return -2;
/* 1263 */     if (shortcutInUse(shortcut))
/* 1264 */       return -3;
/*      */     Menu menu;
/* 1267 */     switch (menuCode) { case 'p':
/* 1268 */       menu = pluginsMenu; break;
/*      */     case 'i':
/* 1269 */       menu = getMenu("File>Import"); break;
/*      */     case 's':
/* 1270 */       menu = getMenu("File>Save As"); break;
/*      */     case 'h':
/* 1271 */       menu = shortcutsMenu; break;
/*      */     case 'a':
/* 1272 */       menu = getMenu("Help>About Plugins"); break;
/*      */     case 'f':
/* 1273 */       menu = getMenu("Process>Filters"); break;
/*      */     case 't':
/* 1274 */       menu = getMenu("Analyze>Tools"); break;
/*      */     case 'u':
/* 1275 */       menu = utilitiesMenu; break;
/*      */     case 'b':
/*      */     case 'c':
/*      */     case 'd':
/*      */     case 'e':
/*      */     case 'g':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'l':
/*      */     case 'm':
/*      */     case 'n':
/*      */     case 'o':
/*      */     case 'q':
/*      */     case 'r':
/*      */     default:
/* 1276 */       return 0;
/*      */     }
/* 1278 */     int code = convertShortcutToCode(shortcut);
/*      */ 
/* 1280 */     boolean functionKey = (code >= 112) && (code <= 123);
/*      */     MenuItem item;
/*      */     MenuItem item;
/* 1281 */     if (code == 0) {
/* 1282 */       item = new MenuItem(command);
/*      */     }
/*      */     else
/*      */     {
/*      */       MenuItem item;
/* 1283 */       if (functionKey) {
/* 1284 */         command = command + " [F" + (code - 112 + 1) + "]";
/* 1285 */         shortcuts.put(new Integer(code), command);
/* 1286 */         item = new MenuItem(command);
/*      */       } else {
/* 1288 */         shortcuts.put(new Integer(code), command);
/* 1289 */         int keyCode = code;
/* 1290 */         boolean shift = false;
/* 1291 */         if ((keyCode >= 265) && (keyCode <= 290)) {
/* 1292 */           keyCode -= 200;
/* 1293 */           shift = true;
/*      */         }
/* 1295 */         item = new MenuItem(command, new MenuShortcut(keyCode, shift));
/*      */       }
/*      */     }
/* 1297 */     menu.add(item);
/* 1298 */     item.addActionListener(ij);
/* 1299 */     pluginsTable.put(command, plugin);
/* 1300 */     shortcut = (code > 0) && (!functionKey) ? "[" + shortcut + "]" : "";
/*      */ 
/* 1302 */     pluginsPrefs.addElement(menuCode + ",\"" + command + shortcut + "\"," + plugin);
/* 1303 */     return 0;
/*      */   }
/*      */ 
/*      */   public static int uninstallPlugin(String command)
/*      */   {
/* 1308 */     boolean found = false;
/* 1309 */     for (Enumeration en = pluginsPrefs.elements(); en.hasMoreElements(); ) {
/* 1310 */       String cmd = (String)en.nextElement();
/* 1311 */       if (cmd.indexOf(command) > 0) {
/* 1312 */         pluginsPrefs.removeElement(cmd);
/* 1313 */         found = true;
/* 1314 */         break;
/*      */       }
/*      */     }
/* 1317 */     if (found) {
/* 1318 */       return 0;
/*      */     }
/* 1320 */     return -5;
/*      */   }
/*      */ 
/*      */   public static boolean commandInUse(String command)
/*      */   {
/* 1325 */     if (pluginsTable.get(command) != null) {
/* 1326 */       return true;
/*      */     }
/* 1328 */     return false;
/*      */   }
/*      */ 
/*      */   public static int convertShortcutToCode(String shortcut) {
/* 1332 */     int code = 0;
/* 1333 */     int len = shortcut.length();
/* 1334 */     if ((len == 2) && (shortcut.charAt(0) == 'F')) {
/* 1335 */       code = 'p' + shortcut.charAt(1) - 49;
/* 1336 */       if ((code >= 112) && (code <= 120)) {
/* 1337 */         return code;
/*      */       }
/* 1339 */       return 0;
/*      */     }
/* 1341 */     if ((len == 3) && (shortcut.charAt(0) == 'F')) {
/* 1342 */       code = 'y' + shortcut.charAt(2) - 48;
/* 1343 */       if ((code >= 121) && (code <= 123)) {
/* 1344 */         return code;
/*      */       }
/* 1346 */       return 0;
/*      */     }
/* 1348 */     if ((len == 2) && (shortcut.charAt(0) == 'N')) {
/* 1349 */       code = '`' + shortcut.charAt(1) - 48;
/* 1350 */       if ((code >= 96) && (code <= 105))
/* 1351 */         return code;
/* 1352 */       switch (shortcut.charAt(1)) { case '/':
/* 1353 */         return 111;
/*      */       case '*':
/* 1354 */         return 106;
/*      */       case '-':
/* 1355 */         return 109;
/*      */       case '+':
/* 1356 */         return 107;
/*      */       case '.':
/* 1357 */         return 110;
/* 1358 */       case ',': } return 0;
/*      */     }
/*      */ 
/* 1361 */     if (len != 1)
/* 1362 */       return 0;
/* 1363 */     int c = shortcut.charAt(0);
/* 1364 */     if ((c >= 65) && (c <= 90))
/* 1365 */       code = 65 + c - 65 + 200;
/* 1366 */     else if ((c >= 97) && (c <= 122))
/* 1367 */       code = 65 + c - 97;
/* 1368 */     else if ((c >= 48) && (c <= 57)) {
/* 1369 */       code = 48 + c - 48;
/*      */     }
/*      */ 
/* 1378 */     return code;
/*      */   }
/*      */ 
/*      */   void installStartupMacroSet() {
/* 1382 */     if (applet != null) {
/* 1383 */       String docBase = "" + applet.getDocumentBase();
/* 1384 */       if (!docBase.endsWith("/")) {
/* 1385 */         int index = docBase.lastIndexOf("/");
/* 1386 */         if (index != -1)
/* 1387 */           docBase = docBase.substring(0, index + 1);
/*      */       }
/* 1389 */       IJ.runPlugIn("ij.plugin.URLOpener", docBase + "StartupMacros.txt");
/* 1390 */       return;
/*      */     }
/* 1392 */     if (macrosPath == null) {
/* 1393 */       new MacroInstaller().installFromIJJar("/macros/StartupMacros.txt");
/* 1394 */       return;
/*      */     }
/* 1396 */     String path = macrosPath + "StartupMacros.txt";
/* 1397 */     File f = new File(path);
/* 1398 */     if (!f.exists()) {
/* 1399 */       path = macrosPath + "StartupMacros.ijm";
/* 1400 */       f = new File(path);
/* 1401 */       if (!f.exists()) {
/* 1402 */         new MacroInstaller().installFromIJJar("/macros/StartupMacros.txt");
/* 1403 */         return;
/*      */       }
/*      */     }
/* 1406 */     String libraryPath = macrosPath + "Library.txt";
/* 1407 */     f = new File(libraryPath);
/* 1408 */     boolean isLibrary = f.exists();
/*      */     try {
/* 1410 */       MacroInstaller mi = new MacroInstaller();
/* 1411 */       if (isLibrary) mi.installLibrary(libraryPath);
/* 1412 */       mi.installFile(path);
/* 1413 */       nMacros += mi.getMacroCount(); } catch (Exception e) {
/*      */     }
/*      */   }
/*      */ 
/*      */   static boolean validShortcut(String shortcut) {
/* 1418 */     int len = shortcut.length();
/* 1419 */     if (shortcut.equals(""))
/* 1420 */       return true;
/* 1421 */     if (len == 1)
/* 1422 */       return true;
/* 1423 */     if ((shortcut.startsWith("F")) && ((len == 2) || (len == 3))) {
/* 1424 */       return true;
/*      */     }
/* 1426 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean shortcutInUse(String shortcut) {
/* 1430 */     int code = convertShortcutToCode(shortcut);
/* 1431 */     if (shortcuts.get(new Integer(code)) != null) {
/* 1432 */       return true;
/*      */     }
/* 1434 */     return false;
/*      */   }
/*      */ 
/*      */   public static void setFontSize(int size)
/*      */   {
/* 1440 */     if ((size < 9) && (size != 0)) size = 9;
/* 1441 */     if (size > 24) size = 24;
/* 1442 */     fontSize = size;
/*      */   }
/*      */ 
/*      */   public static int getFontSize()
/*      */   {
/* 1448 */     return IJ.isMacintosh() ? 0 : fontSize;
/*      */   }
/*      */ 
/*      */   public static Font getFont() {
/* 1452 */     if (menuFont == null)
/* 1453 */       menuFont = new Font("SanSerif", 0, fontSize == 0 ? 12 : fontSize);
/* 1454 */     return menuFont;
/*      */   }
/*      */ 
/*      */   public static void savePreferences(Properties prefs)
/*      */   {
/* 1459 */     int index = 0;
/* 1460 */     for (Enumeration en = pluginsPrefs.elements(); en.hasMoreElements(); ) {
/* 1461 */       String key = "plugin" + index / 10 % 10 + index % 10;
/* 1462 */       String value = (String)en.nextElement();
/* 1463 */       prefs.put(key, value);
/* 1464 */       index++;
/*      */     }
/* 1466 */     int n = openRecentMenu.getItemCount();
/* 1467 */     for (int i = 0; i < n; i++) {
/* 1468 */       String key = "" + i;
/* 1469 */       if (key.length() == 1) key = "0" + key;
/* 1470 */       key = "recent" + key;
/* 1471 */       prefs.put(key, openRecentMenu.getItem(i).getLabel());
/*      */     }
/* 1473 */     prefs.put("menu.size", Integer.toString(fontSize));
/*      */   }
/*      */ 
/*      */   public static void updateImageJMenus() {
/* 1477 */     jarFiles = Menus.macroFiles = null;
/* 1478 */     Menus m = new Menus(IJ.getInstance(), IJ.getApplet());
/* 1479 */     String err = m.addMenuBar();
/* 1480 */     if (err != null) IJ.error(err);
/* 1481 */     IJ.setClassLoader(null);
/* 1482 */     IJ.runPlugIn("ij.plugin.ClassChecker", "");
/* 1483 */     IJ.showStatus("Menus updated: " + nPlugins + " commands, " + nMacros + " macros");
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.Menus
 * JD-Core Version:    0.6.2
 */