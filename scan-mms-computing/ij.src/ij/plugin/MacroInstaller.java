/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.Menus;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.macro.MacroConstants;
/*     */ import ij.macro.MacroRunner;
/*     */ import ij.macro.Program;
/*     */ import ij.macro.Symbol;
/*     */ import ij.macro.Tokenizer;
/*     */ import ij.plugin.frame.Editor;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.MenuContainer;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class MacroInstaller
/*     */   implements PlugIn, MacroConstants, ActionListener
/*     */ {
/*     */   public static final int MAX_SIZE = 28000;
/*     */   public static final int MAX_MACROS = 100;
/*     */   public static final int XINC = 10;
/*     */   public static final int YINC = 18;
/*     */   public static final char commandPrefix = '^';
/*     */   static final String commandPrefixS = "^";
/*     */   static final int MACROS_MENU_COMMANDS = 6;
/*     */   private int[] macroStarts;
/*     */   private String[] macroNames;
/*  26 */   private MenuBar mb = new MenuBar();
/*     */   private int nMacros;
/*     */   private Program pgm;
/*  29 */   private boolean firstEvent = true;
/*     */   private String shortcutsInUse;
/*     */   private int inUseCount;
/*     */   private int nShortcuts;
/*     */   private int toolCount;
/*     */   private String text;
/*     */   private String anonymousName;
/*     */   private Menu macrosMenu;
/*     */   private int autoRunCount;
/*     */   private int autoRunAndHideCount;
/*     */   private boolean openingStartupMacrosInEditor;
/*     */   private static String defaultDir;
/*     */   private static String fileName;
/*     */   private static MacroInstaller instance;
/*     */   private static MacroInstaller listener;
/*     */   private Thread macroToolThread;
/*     */ 
/*     */   public void run(String path)
/*     */   {
/*  45 */     if ((path == null) || (path.equals("")))
/*  46 */       path = showDialog();
/*  47 */     if (path == null) return;
/*  48 */     this.openingStartupMacrosInEditor = (path.indexOf("StartupMacros") != -1);
/*  49 */     String text = open(path);
/*  50 */     if (text != null) {
/*  51 */       String functions = Interpreter.getAdditionalFunctions();
/*  52 */       if (functions != null) {
/*  53 */         if ((!text.endsWith("\n")) && (!functions.startsWith("\n")))
/*  54 */           text = text + "\n" + functions;
/*     */         else
/*  56 */           text = text + functions;
/*     */       }
/*  58 */       install(text);
/*     */     }
/*     */   }
/*     */ 
/*     */   void install() {
/*  63 */     if (this.text != null) {
/*  64 */       Tokenizer tok = new Tokenizer();
/*  65 */       this.pgm = tok.tokenize(this.text);
/*     */     }
/*  67 */     IJ.showStatus("");
/*  68 */     int[] code = this.pgm.getCode();
/*  69 */     Symbol[] symbolTable = this.pgm.getSymbolTable();
/*  70 */     int count = 0;
/*     */ 
/*  73 */     this.shortcutsInUse = null;
/*  74 */     this.inUseCount = 0;
/*  75 */     this.nShortcuts = 0;
/*  76 */     this.toolCount = 0;
/*  77 */     this.macroStarts = new int[100];
/*  78 */     this.macroNames = new String[100];
/*  79 */     int itemCount = this.macrosMenu.getItemCount();
/*  80 */     boolean isPluginsMacrosMenu = this.macrosMenu == Menus.getMacrosMenu();
/*  81 */     int baseCount = isPluginsMacrosMenu ? 6 : 8;
/*  82 */     if (itemCount > baseCount) {
/*  83 */       for (int i = itemCount - 1; i >= baseCount; i--)
/*  84 */         this.macrosMenu.remove(i);
/*     */     }
/*  86 */     if ((this.pgm.hasVars()) && (this.pgm.macroCount() > 0) && (this.pgm.getGlobals() == null))
/*  87 */       new Interpreter().saveGlobals(this.pgm);
/*  88 */     for (int i = 0; i < code.length; i++) {
/*  89 */       int token = code[i] & 0xFFF;
/*  90 */       if (token == 200) {
/*  91 */         int nextToken = code[(i + 1)] & 0xFFF;
/*  92 */         if (nextToken == 133) {
/*  93 */           if (count == 100) {
/*  94 */             if (!isPluginsMacrosMenu) break;
/*  95 */             IJ.error("Macro Installer", "Macro sets are limited to 100 macros."); break;
/*     */           }
/*     */ 
/*  98 */           int address = code[(i + 1)] >> 12;
/*  99 */           Symbol symbol = symbolTable[address];
/* 100 */           String name = symbol.str;
/* 101 */           this.macroStarts[count] = (i + 2);
/* 102 */           this.macroNames[count] = name;
/* 103 */           if ((name.indexOf('-') != -1) && ((name.indexOf("Tool") != -1) || (name.indexOf("tool") != -1))) {
/* 104 */             Toolbar.getInstance().addMacroTool(name, this, this.toolCount);
/* 105 */             this.toolCount += 1;
/* 106 */           } else if (name.startsWith("AutoRun")) {
/* 107 */             if ((this.autoRunCount == 0) && (!this.openingStartupMacrosInEditor)) {
/* 108 */               new MacroRunner(this.pgm, this.macroStarts[count], name, (String)null);
/* 109 */               if (name.equals("AutoRunAndHide"))
/* 110 */                 this.autoRunAndHideCount += 1;
/*     */             }
/* 112 */             this.autoRunCount += 1;
/* 113 */             count--;
/* 114 */           } else if (name.equals("Popup Menu")) {
/* 115 */             installPopupMenu(name, this.pgm);
/* 116 */           } else if (!name.endsWith("Tool Selected")) {
/* 117 */             addShortcut(name);
/* 118 */             this.macrosMenu.add(new MenuItem(name));
/*     */           }
/*     */ 
/* 121 */           count++;
/*     */         }
/*     */       } else { if (token == 128)
/*     */           break; }
/*     */     }
/* 126 */     this.nMacros = count;
/* 127 */     if (this.toolCount > 0) {
/* 128 */       Toolbar tb = Toolbar.getInstance();
/* 129 */       if (Toolbar.getToolId() >= 15)
/* 130 */         tb.setTool(0);
/* 131 */       tb.repaint();
/*     */     }
/* 133 */     instance = this;
/* 134 */     if ((this.shortcutsInUse != null) && (this.text != null)) {
/* 135 */       IJ.showMessage("Install Macros", (this.inUseCount == 1 ? "This keyboard shortcut is" : "These keyboard shortcuts are") + " already in use:" + this.shortcutsInUse);
/*     */     }
/* 137 */     if ((this.nMacros == 0) && (fileName != null)) {
/* 138 */       if ((this.text == null) || (this.text.length() == 0))
/* 139 */         return;
/* 140 */       int dotIndex = fileName.lastIndexOf('.');
/* 141 */       if (dotIndex > 0)
/* 142 */         this.anonymousName = fileName.substring(0, dotIndex);
/*     */       else
/* 144 */         this.anonymousName = fileName;
/* 145 */       this.macrosMenu.add(new MenuItem(this.anonymousName));
/* 146 */       this.macroNames[0] = this.anonymousName;
/* 147 */       this.nMacros = 1;
/*     */     }
/* 149 */     String word = this.nMacros == 1 ? " macro" : " macros";
/* 150 */     if (isPluginsMacrosMenu)
/* 151 */       IJ.showStatus(this.nMacros + word + " installed");
/*     */   }
/*     */ 
/*     */   public int install(String text) {
/* 155 */     if ((text == null) && (this.pgm == null))
/* 156 */       return 0;
/* 157 */     this.text = text;
/* 158 */     this.macrosMenu = Menus.getMacrosMenu();
/* 159 */     if (listener != null)
/* 160 */       this.macrosMenu.removeActionListener(listener);
/* 161 */     this.macrosMenu.addActionListener(this);
/* 162 */     listener = this;
/* 163 */     install();
/* 164 */     return this.nShortcuts;
/*     */   }
/*     */ 
/*     */   public int install(String text, Menu menu) {
/* 168 */     this.text = text;
/* 169 */     this.macrosMenu = menu;
/* 170 */     install();
/* 171 */     int count = this.nShortcuts + this.toolCount;
/* 172 */     if ((count == 0) && (this.nMacros > 1))
/* 173 */       count = this.nMacros;
/* 174 */     return count;
/*     */   }
/*     */ 
/*     */   public void installFile(String path) {
/* 178 */     if (path != null) {
/* 179 */       String text = open(path);
/* 180 */       if (text != null) install(text); 
/*     */     }
/*     */   }
/*     */ 
/*     */   public void installLibrary(String path)
/*     */   {
/* 185 */     String text = open(path);
/* 186 */     if (text != null)
/* 187 */       Interpreter.setAdditionalFunctions(text);
/*     */   }
/*     */ 
/*     */   public void installFromIJJar(String path)
/*     */   {
/* 192 */     String text = openFromIJJar(path);
/* 193 */     if (text != null) install(text); 
/*     */   }
/*     */ 
/*     */   void installPopupMenu(String name, Program pgm)
/*     */   {
/* 197 */     Hashtable h = pgm.getMenus();
/* 198 */     if (h == null) return;
/* 199 */     String[] commands = (String[])h.get(name);
/* 200 */     if (commands == null) return;
/* 201 */     PopupMenu popup = Menus.getPopupMenu();
/* 202 */     if (popup == null) return;
/* 203 */     popup.removeAll();
/* 204 */     for (int i = 0; i < commands.length; i++)
/* 205 */       if (commands[i].equals("-")) {
/* 206 */         popup.addSeparator();
/*     */       } else {
/* 208 */         MenuItem mi = new MenuItem(commands[i]);
/* 209 */         mi.addActionListener(this);
/* 210 */         popup.add(mi);
/*     */       }
/*     */   }
/*     */ 
/*     */   void removeShortcuts()
/*     */   {
/* 216 */     Menus.getMacroShortcuts().clear();
/* 217 */     Hashtable shortcuts = Menus.getShortcuts();
/* 218 */     for (Enumeration en = shortcuts.keys(); en.hasMoreElements(); ) {
/* 219 */       Integer key = (Integer)en.nextElement();
/* 220 */       String value = (String)shortcuts.get(key);
/* 221 */       if (value.charAt(0) == '^')
/* 222 */         shortcuts.remove(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   void addShortcut(String name) {
/* 227 */     int index1 = name.indexOf('[');
/* 228 */     if (index1 == -1)
/* 229 */       return;
/* 230 */     int index2 = name.lastIndexOf(']');
/* 231 */     if (index2 <= index1 + 1)
/* 232 */       return;
/* 233 */     String shortcut = name.substring(index1 + 1, index2);
/* 234 */     int len = shortcut.length();
/* 235 */     if (len > 1)
/* 236 */       shortcut = shortcut.toUpperCase(Locale.US);
/* 237 */     if ((len > 3) || ((len > 1) && (shortcut.charAt(0) != 'F') && (shortcut.charAt(0) != 'N')))
/* 238 */       return;
/* 239 */     int code = Menus.convertShortcutToCode(shortcut);
/* 240 */     if (code == 0)
/* 241 */       return;
/* 242 */     if (this.nShortcuts == 0) {
/* 243 */       removeShortcuts();
/*     */     }
/*     */ 
/* 246 */     if ((len == 1) || (shortcut.equals("N+")) || (shortcut.equals("N-"))) {
/* 247 */       Hashtable macroShortcuts = Menus.getMacroShortcuts();
/* 248 */       macroShortcuts.put(new Integer(code), '^' + name);
/* 249 */       this.nShortcuts += 1;
/* 250 */       return;
/*     */     }
/* 252 */     Hashtable shortcuts = Menus.getShortcuts();
/* 253 */     if (shortcuts.get(new Integer(code)) != null) {
/* 254 */       if (this.shortcutsInUse == null)
/* 255 */         this.shortcutsInUse = "\n \n";
/* 256 */       this.shortcutsInUse = (this.shortcutsInUse + "\t  " + name + "\n");
/* 257 */       this.inUseCount += 1;
/* 258 */       return;
/*     */     }
/* 260 */     shortcuts.put(new Integer(code), '^' + name);
/* 261 */     this.nShortcuts += 1;
/*     */   }
/*     */ 
/*     */   String showDialog()
/*     */   {
/* 266 */     if (defaultDir == null) defaultDir = Menus.getMacrosPath();
/* 267 */     OpenDialog od = new OpenDialog("Install Macros", defaultDir, fileName);
/* 268 */     String name = od.getFileName();
/* 269 */     if (name == null) return null;
/* 270 */     String dir = od.getDirectory();
/* 271 */     if ((!name.endsWith(".txt")) && (!name.endsWith(".ijm"))) {
/* 272 */       IJ.showMessage("Macro Installer", "File name must end with \".txt\" or \".ijm\" .");
/* 273 */       return null;
/*     */     }
/* 275 */     fileName = name;
/* 276 */     defaultDir = dir;
/* 277 */     return dir + name;
/*     */   }
/*     */ 
/*     */   String open(String path) {
/*     */     try {
/* 282 */       StringBuffer sb = new StringBuffer(5000);
/* 283 */       BufferedReader r = new BufferedReader(new FileReader(path));
/*     */       while (true) {
/* 285 */         String s = r.readLine();
/* 286 */         if (s == null) {
/*     */           break;
/*     */         }
/* 289 */         sb.append(s + "\n");
/*     */       }
/* 291 */       r.close();
/* 292 */       return new String(sb);
/*     */     }
/*     */     catch (Exception e) {
/* 295 */       IJ.error(e.getMessage());
/* 296 */     }return null;
/*     */   }
/*     */ 
/*     */   public String openFromIJJar(String path)
/*     */   {
/* 304 */     String text = null;
/*     */     try {
/* 306 */       InputStream is = getClass().getResourceAsStream(path);
/*     */ 
/* 308 */       if (is == null) return null;
/* 309 */       InputStreamReader isr = new InputStreamReader(is);
/* 310 */       StringBuffer sb = new StringBuffer();
/* 311 */       char[] b = new char[8192];
/*     */       int n;
/* 313 */       while ((n = isr.read(b)) > 0)
/* 314 */         sb.append(b, 0, n);
/* 315 */       text = sb.toString();
/*     */     } catch (IOException e) {
/*     */     }
/* 318 */     return text;
/*     */   }
/*     */ 
/*     */   public boolean runMacroTool(String name)
/*     */   {
/* 326 */     for (int i = 0; i < this.nMacros; i++) {
/* 327 */       if (this.macroNames[i].startsWith(name)) {
/* 328 */         if ((this.macroToolThread != null) && (this.macroToolThread.getName().indexOf(name) != -1) && (this.macroToolThread.isAlive()))
/* 329 */           return false;
/* 330 */         MacroRunner mw = new MacroRunner(this.pgm, this.macroStarts[i], name, (String)null);
/* 331 */         this.macroToolThread = mw.getThread();
/*     */ 
/* 333 */         return true;
/*     */       }
/*     */     }
/* 336 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean runMenuTool(String name, String command) {
/* 340 */     for (int i = 0; i < this.nMacros; i++) {
/* 341 */       if (this.macroNames[i].startsWith(name)) {
/* 342 */         ij.plugin.frame.Recorder.recordInMacros = true;
/* 343 */         new MacroRunner(this.pgm, this.macroStarts[i], name, command);
/* 344 */         return true;
/*     */       }
/*     */     }
/* 347 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean runMacroCommand(String name) {
/* 351 */     if (instance == null) return false;
/* 352 */     if (name.startsWith("^"))
/* 353 */       name = name.substring(1);
/* 354 */     for (int i = 0; i < instance.nMacros; i++) {
/* 355 */       if (name.equals(instance.macroNames[i])) {
/* 356 */         new MacroRunner(instance.pgm, instance.macroStarts[i], name, (String)null);
/* 357 */         return true;
/*     */       }
/*     */     }
/* 360 */     return false;
/*     */   }
/*     */ 
/*     */   public static void runMacroShortcut(String name) {
/* 364 */     if (instance == null) return;
/* 365 */     if (name.startsWith("^"))
/* 366 */       name = name.substring(1);
/* 367 */     for (int i = 0; i < instance.nMacros; i++)
/* 368 */       if (name.equals(instance.macroNames[i])) {
/* 369 */         new MacroRunner().runShortcut(instance.pgm, instance.macroStarts[i], name);
/* 370 */         return;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void runMacro(String name)
/*     */   {
/* 376 */     runMacro(name, null);
/*     */   }
/*     */ 
/*     */   public void runMacro(String name, Editor editor)
/*     */   {
/* 381 */     if ((this.anonymousName != null) && (name.equals(this.anonymousName))) {
/* 382 */       new MacroRunner(this.pgm, 0, this.anonymousName, editor);
/* 383 */       return;
/*     */     }
/* 385 */     for (int i = 0; i < this.nMacros; i++)
/* 386 */       if (name.equals(this.macroNames[i])) {
/* 387 */         new MacroRunner(this.pgm, this.macroStarts[i], name, editor);
/* 388 */         return;
/*     */       }
/*     */   }
/*     */ 
/*     */   public int getMacroCount() {
/* 393 */     return this.nMacros;
/*     */   }
/*     */ 
/*     */   public Program getProgram() {
/* 397 */     return this.pgm;
/*     */   }
/*     */ 
/*     */   public boolean isAutoRunAndHide()
/*     */   {
/* 403 */     return this.autoRunAndHideCount > 0;
/*     */   }
/*     */ 
/*     */   public void setFileName(String fileName) {
/* 407 */     fileName = fileName;
/* 408 */     this.openingStartupMacrosInEditor = fileName.startsWith("StartupMacros");
/*     */   }
/*     */ 
/*     */   public static String getFileName() {
/* 412 */     return fileName;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent evt) {
/* 416 */     String cmd = evt.getActionCommand();
/* 417 */     MenuItem item = (MenuItem)evt.getSource();
/* 418 */     MenuContainer parent = item.getParent();
/* 419 */     if ((parent instanceof PopupMenu)) {
/* 420 */       for (int i = 0; i < this.nMacros; i++) {
/* 421 */         if (this.macroNames[i].equals("Popup Menu")) {
/* 422 */           new MacroRunner(this.pgm, this.macroStarts[i], "Popup Menu", cmd);
/* 423 */           return;
/*     */         }
/*     */       }
/*     */     }
/* 427 */     runMacro(cmd);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.MacroInstaller
 * JD-Core Version:    0.6.2
 */