/*     */ package ij;
/*     */ 
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.ProgressBar;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.TextRoi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.plugin.JavaProperties;
/*     */ import ij.plugin.MacroInstaller;
/*     */ import ij.plugin.Orthogonal_Views;
/*     */ import ij.plugin.filter.PlugInFilterRunner;
/*     */ import ij.plugin.frame.Editor;
/*     */ import ij.util.Tools;
/*     */ import java.applet.Applet;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Image;
/*     */ import java.awt.Label;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuComponent;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Point;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.awt.image.ImageProducer;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ 
/*     */ public class ImageJ extends Frame
/*     */   implements ActionListener, MouseListener, KeyListener, WindowListener, ItemListener, Runnable
/*     */ {
/*     */   public static final String VERSION = "1.45s";
/*     */   public static final String BUILD = "";
/*  77 */   public static Color backgroundColor = new Color(220, 220, 220);
/*     */ 
/*  79 */   public static final Font SansSerif12 = new Font("SansSerif", 0, 12);
/*     */   public static final int DEFAULT_PORT = 57294;
/*     */   public static final int STANDALONE = 0;
/*     */   public static final int EMBEDDED = 1;
/*     */   public static final int NO_SHOW = 2;
/*     */   private static final String IJ_X = "ij.x";
/*     */   private static final String IJ_Y = "ij.y";
/*  91 */   private static int port = 57294;
/*     */   private static String[] arguments;
/*     */   private Toolbar toolbar;
/*     */   private Panel statusBar;
/*     */   private ProgressBar progressBar;
/*     */   private Label statusLine;
/*  98 */   private boolean firstTime = true;
/*     */   private Applet applet;
/* 100 */   private Vector classes = new Vector();
/*     */   private boolean exitWhenQuitting;
/*     */   private boolean quitting;
/*     */   private long keyPressedTime;
/*     */   private long actionPerformedTime;
/*     */   private String lastKeyCommand;
/*     */   private boolean embedded;
/*     */   private boolean windowClosed;
/*     */   boolean hotkey;
/*     */ 
/*     */   public ImageJ()
/*     */   {
/* 112 */     this(null, 0);
/*     */   }
/*     */ 
/*     */   public ImageJ(int mode)
/*     */   {
/* 117 */     this(null, mode);
/*     */   }
/*     */ 
/*     */   public ImageJ(Applet applet)
/*     */   {
/* 122 */     this(applet, 0);
/*     */   }
/*     */ 
/*     */   public ImageJ(Applet applet, int mode)
/*     */   {
/* 129 */     super("ImageJ");
/* 130 */     this.embedded = ((applet == null) && ((mode == 1) || (mode == 2)));
/* 131 */     this.applet = applet;
/* 132 */     String err1 = Prefs.load(this, applet);
/* 133 */     if (IJ.isLinux()) {
/* 134 */       backgroundColor = new Color(240, 240, 240);
/* 135 */       setBackground(backgroundColor);
/*     */     }
/* 137 */     Menus m = new Menus(this, applet);
/* 138 */     String err2 = m.addMenuBar();
/* 139 */     m.installPopupMenu(this);
/* 140 */     setLayout(new GridLayout(2, 1));
/*     */ 
/* 143 */     this.toolbar = new Toolbar();
/* 144 */     this.toolbar.addKeyListener(this);
/* 145 */     add(this.toolbar);
/*     */ 
/* 148 */     this.statusBar = new Panel();
/* 149 */     this.statusBar.setLayout(new BorderLayout());
/* 150 */     this.statusBar.setForeground(Color.black);
/* 151 */     this.statusBar.setBackground(backgroundColor);
/* 152 */     this.statusLine = new Label();
/* 153 */     this.statusLine.setFont(SansSerif12);
/* 154 */     this.statusLine.addKeyListener(this);
/* 155 */     this.statusLine.addMouseListener(this);
/* 156 */     this.statusBar.add("Center", this.statusLine);
/* 157 */     this.progressBar = new ProgressBar(120, 20);
/* 158 */     this.progressBar.addKeyListener(this);
/* 159 */     this.progressBar.addMouseListener(this);
/* 160 */     this.statusBar.add("East", this.progressBar);
/* 161 */     this.statusBar.setSize(this.toolbar.getPreferredSize());
/* 162 */     add(this.statusBar);
/*     */ 
/* 164 */     IJ.init(this, applet);
/* 165 */     addKeyListener(this);
/* 166 */     addWindowListener(this);
/* 167 */     setFocusTraversalKeysEnabled(false);
/*     */ 
/* 169 */     Point loc = getPreferredLocation();
/* 170 */     Dimension tbSize = this.toolbar.getPreferredSize();
/* 171 */     int ijWidth = tbSize.width + 10;
/* 172 */     int ijHeight = 100;
/* 173 */     setCursor(Cursor.getDefaultCursor());
/* 174 */     if (mode != 2) {
/* 175 */       if (IJ.isWindows()) try { setIcon(); } catch (Exception e) {
/*     */         } setBounds(loc.x, loc.y, ijWidth, ijHeight);
/* 177 */       setLocation(loc.x, loc.y);
/* 178 */       pack();
/* 179 */       setResizable((!IJ.isMacintosh()) && (!IJ.isWindows()));
/* 180 */       show();
/*     */     }
/* 182 */     if (err1 != null)
/* 183 */       IJ.error(err1);
/* 184 */     if (err2 != null) {
/* 185 */       IJ.error(err2);
/* 186 */       IJ.runPlugIn("ij.plugin.ClassChecker", "");
/*     */     }
/* 188 */     m.installStartupMacroSet();
/* 189 */     if ((IJ.isMacintosh()) && (applet == null)) {
/* 190 */       Object qh = null;
/* 191 */       qh = IJ.runPlugIn("MacAdapter", "");
/* 192 */       if (qh == null)
/* 193 */         IJ.runPlugIn("QuitHandler", "");
/*     */     }
/* 195 */     if (applet == null)
/* 196 */       IJ.runPlugIn("ij.plugin.DragAndDrop", "");
/* 197 */     String str = m.getMacroCount() == 1 ? " macro" : " macros";
/* 198 */     IJ.showStatus(version() + m.getPluginCount() + " commands; " + m.getMacroCount() + str);
/* 199 */     if ((applet == null) && (!this.embedded) && (Prefs.runSocketListener))
/* 200 */       new SocketListener();
/* 201 */     configureProxy();
/*     */   }
/*     */ 
/*     */   void configureProxy() {
/* 205 */     if (Prefs.useSystemProxies) {
/*     */       try {
/* 207 */         System.setProperty("java.net.useSystemProxies", "true"); } catch (Exception e) {
/*     */       }
/*     */     } else {
/* 210 */       String server = Prefs.get("proxy.server", null);
/* 211 */       if ((server == null) || (server.equals("")))
/* 212 */         return;
/* 213 */       int port = (int)Prefs.get("proxy.port", 0.0D);
/* 214 */       if (port == 0) return;
/* 215 */       Properties props = System.getProperties();
/* 216 */       props.put("proxySet", "true");
/* 217 */       props.put("http.proxyHost", server);
/* 218 */       props.put("http.proxyPort", "" + port);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setIcon() throws Exception
/*     */   {
/* 224 */     URL url = getClass().getResource("/microscope.gif");
/* 225 */     if (url == null) return;
/* 226 */     Image img = createImage((ImageProducer)url.getContent());
/* 227 */     if (img != null) setIconImage(img); 
/*     */   }
/*     */ 
/*     */   public Point getPreferredLocation()
/*     */   {
/* 231 */     if (!IJ.isJava14()) return new Point(0, 0);
/* 232 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 233 */     Rectangle maxBounds = ge.getMaximumWindowBounds();
/* 234 */     int ijX = Prefs.getInt("ij.x", -99);
/* 235 */     int ijY = Prefs.getInt("ij.y", -99);
/* 236 */     if ((ijX >= 0) && (ijY > 0) && (ijX < maxBounds.x + maxBounds.width - 75))
/* 237 */       return new Point(ijX, ijY);
/* 238 */     Dimension tbsize = this.toolbar.getPreferredSize();
/* 239 */     int ijWidth = tbsize.width + 10;
/* 240 */     double percent = maxBounds.width > 832 ? 0.8D : 0.9D;
/* 241 */     ijX = (int)(percent * (maxBounds.width - ijWidth));
/* 242 */     if (ijX < 10) ijX = 10;
/* 243 */     return new Point(ijX, maxBounds.y);
/*     */   }
/*     */ 
/*     */   void showStatus(String s) {
/* 247 */     this.statusLine.setText(s);
/*     */   }
/*     */ 
/*     */   public ProgressBar getProgressBar() {
/* 251 */     return this.progressBar;
/*     */   }
/*     */ 
/*     */   public Panel getStatusBar() {
/* 255 */     return this.statusBar;
/*     */   }
/*     */ 
/*     */   void doCommand(String name)
/*     */   {
/* 260 */     new Executer(name, null);
/*     */   }
/*     */ 
/*     */   public void runFilterPlugIn(Object theFilter, String cmd, String arg) {
/* 264 */     new PlugInFilterRunner(theFilter, cmd, arg);
/*     */   }
/*     */ 
/*     */   public Object runUserPlugIn(String commandName, String className, String arg, boolean createNewLoader) {
/* 268 */     return IJ.runUserPlugIn(commandName, className, arg, createNewLoader);
/*     */   }
/*     */ 
/*     */   public static String modifiers(int flags)
/*     */   {
/* 273 */     String s = " [ ";
/* 274 */     if (flags == 0) return "";
/* 275 */     if ((flags & 0x1) != 0) s = s + "Shift ";
/* 276 */     if ((flags & 0x2) != 0) s = s + "Control ";
/* 277 */     if ((flags & 0x4) != 0) s = s + "Meta ";
/* 278 */     if ((flags & 0x8) != 0) s = s + "Alt ";
/* 279 */     s = s + "] ";
/* 280 */     return s;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 285 */     if ((e.getSource() instanceof MenuItem)) {
/* 286 */       MenuItem item = (MenuItem)e.getSource();
/* 287 */       String cmd = e.getActionCommand();
/* 288 */       ImagePlus imp = null;
/* 289 */       if (item.getParent() == Menus.openRecentMenu) {
/* 290 */         new RecentOpener(cmd);
/* 291 */         return;
/* 292 */       }if (item.getParent() == Menus.getPopupMenu()) {
/* 293 */         Object parent = Menus.getPopupMenu().getParent();
/* 294 */         if ((parent instanceof ImageCanvas))
/* 295 */           imp = ((ImageCanvas)parent).getImage();
/*     */       }
/* 297 */       int flags = e.getModifiers();
/*     */ 
/* 299 */       this.hotkey = false;
/* 300 */       this.actionPerformedTime = System.currentTimeMillis();
/* 301 */       long ellapsedTime = this.actionPerformedTime - this.keyPressedTime;
/* 302 */       if ((cmd != null) && ((ellapsedTime >= 200L) || (!cmd.equals(this.lastKeyCommand)))) {
/* 303 */         if ((flags & 0x8) != 0)
/* 304 */           IJ.setKeyDown(18);
/* 305 */         if ((flags & 0x1) != 0)
/* 306 */           IJ.setKeyDown(16);
/* 307 */         new Executer(cmd, imp);
/*     */       }
/* 309 */       this.lastKeyCommand = null;
/* 310 */       if (IJ.debugMode) IJ.log("actionPerformed: time=" + ellapsedTime + ", " + e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e)
/*     */   {
/* 316 */     MenuItem item = (MenuItem)e.getSource();
/* 317 */     MenuComponent parent = (MenuComponent)item.getParent();
/* 318 */     String cmd = e.getItem().toString();
/* 319 */     if ((Menu)parent == Menus.window)
/* 320 */       WindowManager.activateWindow(cmd, item);
/*     */     else
/* 322 */       doCommand(cmd);
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e) {
/* 326 */     Undo.reset();
/* 327 */     if (!Prefs.noClickToGC)
/* 328 */       System.gc();
/* 329 */     IJ.showStatus(version() + IJ.freeMemory());
/* 330 */     if (IJ.debugMode)
/* 331 */       IJ.log("Windows: " + WindowManager.getWindowCount());
/*     */   }
/*     */ 
/*     */   private String version() {
/* 335 */     return "ImageJ 1.45s; Java " + System.getProperty("java.version") + (IJ.is64Bit() ? " [64-bit]; " : " [32-bit]; ");
/*     */   }
/*     */   public void mouseReleased(MouseEvent e) {
/*     */   }
/*     */   public void mouseExited(MouseEvent e) {
/*     */   }
/*     */   public void mouseClicked(MouseEvent e) {
/*     */   }
/*     */   public void mouseEntered(MouseEvent e) {
/*     */   }
/* 345 */   public void keyPressed(KeyEvent e) { int keyCode = e.getKeyCode();
/* 346 */     IJ.setKeyDown(keyCode);
/* 347 */     this.hotkey = false;
/* 348 */     if ((keyCode == 17) || (keyCode == 16))
/* 349 */       return;
/* 350 */     char keyChar = e.getKeyChar();
/* 351 */     int flags = e.getModifiers();
/* 352 */     if (IJ.debugMode) IJ.log("keyPressed: code=" + keyCode + " (" + KeyEvent.getKeyText(keyCode) + "), char=\"" + keyChar + "\" (" + keyChar + "), flags=" + KeyEvent.getKeyModifiersText(flags));
/*     */ 
/* 355 */     boolean shift = (flags & 0x1) != 0;
/* 356 */     boolean control = (flags & 0x2) != 0;
/* 357 */     boolean alt = (flags & 0x8) != 0;
/* 358 */     boolean meta = (flags & 0x4) != 0;
/* 359 */     String cmd = null;
/* 360 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 361 */     boolean isStack = (imp != null) && (imp.getStackSize() > 1);
/*     */ 
/* 363 */     if ((imp != null) && (!control) && (((keyChar >= ' ') && (keyChar <= 'ÿ')) || (keyChar == '\b') || (keyChar == '\n'))) {
/* 364 */       Roi roi = imp.getRoi();
/* 365 */       if ((roi instanceof TextRoi)) {
/* 366 */         if (((flags & 0x4) != 0) && (IJ.isMacOSX())) return;
/* 367 */         if (alt) {
/* 368 */           switch (keyChar) { case 'm':
/*     */           case 'u':
/* 369 */             keyChar = 'µ'; break;
/*     */           case 'A':
/* 370 */             keyChar = 'Å'; break;
/*     */           }
/*     */         }
/* 373 */         ((TextRoi)roi).addChar(keyChar);
/* 374 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 379 */     if ((!control) && (!meta)) {
/* 380 */       Hashtable macroShortcuts = Menus.getMacroShortcuts();
/* 381 */       if (macroShortcuts.size() > 0) {
/* 382 */         if (shift)
/* 383 */           cmd = (String)macroShortcuts.get(new Integer(keyCode + 200));
/*     */         else
/* 385 */           cmd = (String)macroShortcuts.get(new Integer(keyCode));
/* 386 */         if (cmd != null)
/*     */         {
/* 388 */           MacroInstaller.runMacroShortcut(cmd);
/* 389 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 394 */     if (((!Prefs.requireControlKey) || (control) || (meta)) && (keyChar != '+')) {
/* 395 */       Hashtable shortcuts = Menus.getShortcuts();
/* 396 */       if (shift)
/* 397 */         cmd = (String)shortcuts.get(new Integer(keyCode + 200));
/*     */       else {
/* 399 */         cmd = (String)shortcuts.get(new Integer(keyCode));
/*     */       }
/*     */     }
/* 402 */     if (cmd == null)
/* 403 */       switch (keyChar) { case ',':
/*     */       case '<':
/* 404 */         cmd = "Previous Slice [<]"; break;
/*     */       case '.':
/*     */       case ';':
/*     */       case '>':
/* 405 */         cmd = "Next Slice [>]"; break;
/*     */       case '+':
/*     */       case '=':
/* 406 */         cmd = "In [+]"; break;
/*     */       case '-':
/* 407 */         cmd = "Out [-]"; break;
/*     */       case '/':
/* 408 */         cmd = "Reslice [/]..."; break;
/*     */       case '0':
/*     */       case '1':
/*     */       case '2':
/*     */       case '3':
/*     */       case '4':
/*     */       case '5':
/*     */       case '6':
/*     */       case '7':
/*     */       case '8':
/*     */       case '9':
/* 413 */       case ':': }  if (cmd == null) {
/* 414 */       switch (keyCode) { case 9:
/* 415 */         WindowManager.putBehind(); return;
/*     */       case 8:
/* 416 */         cmd = "Clear"; this.hotkey = true; break;
/*     */       case 61:
/* 418 */         cmd = "In [+]"; break;
/*     */       case 45:
/* 419 */         cmd = "Out [-]"; break;
/*     */       case 47:
/*     */       case 191:
/* 420 */         cmd = "Reslice [/]..."; break;
/*     */       case 44:
/*     */       case 188:
/* 421 */         cmd = "Previous Slice [<]"; break;
/*     */       case 46:
/*     */       case 190:
/* 422 */         cmd = "Next Slice [>]"; break;
/*     */       case 37:
/*     */       case 38:
/*     */       case 39:
/*     */       case 40:
/* 424 */         if (imp == null) return;
/* 425 */         Roi roi = imp.getRoi();
/* 426 */         if ((IJ.shiftKeyDown()) && (imp == Orthogonal_Views.getImage()))
/* 427 */           return;
/* 428 */         boolean stackKey = (imp.getStackSize() > 1) && ((roi == null) || (IJ.shiftKeyDown()));
/* 429 */         boolean zoomKey = (roi == null) || (IJ.shiftKeyDown()) || (IJ.controlKeyDown());
/* 430 */         if ((stackKey) && (keyCode == 39)) {
/* 431 */           cmd = "Next Slice [>]";
/* 432 */         } else if ((stackKey) && (keyCode == 37)) {
/* 433 */           cmd = "Previous Slice [<]";
/* 434 */         } else if ((zoomKey) && (keyCode == 40) && (!ignoreArrowKeys(imp))) {
/* 435 */           cmd = "Out [-]";
/* 436 */         } else if ((zoomKey) && (keyCode == 38) && (!ignoreArrowKeys(imp))) {
/* 437 */           cmd = "In [+]";
/* 438 */         } else if (roi != null) {
/* 439 */           if ((flags & 0x8) != 0)
/* 440 */             roi.nudgeCorner(keyCode);
/*     */           else
/* 442 */             roi.nudge(keyCode);
/*     */           return;
/*     */         }
/*     */         break;
/*     */       case 27:
/* 447 */         abortPluginOrMacro(imp);
/* 448 */         return;
/*     */       case 10:
/* 449 */         WindowManager.toFront(this); return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 454 */     if ((cmd != null) && (!cmd.equals(""))) {
/* 455 */       if ((cmd.equals("Fill")) || (cmd.equals("Draw")))
/* 456 */         this.hotkey = true;
/* 457 */       if (cmd.charAt(0) == '^') {
/* 458 */         MacroInstaller.runMacroShortcut(cmd);
/*     */       } else {
/* 460 */         doCommand(cmd);
/* 461 */         this.keyPressedTime = System.currentTimeMillis();
/* 462 */         this.lastKeyCommand = cmd;
/*     */       }
/*     */     } }
/*     */ 
/*     */   private boolean ignoreArrowKeys(ImagePlus imp)
/*     */   {
/* 468 */     Frame frame = WindowManager.getFrontWindow();
/* 469 */     String title = frame.getTitle();
/* 470 */     if ((title != null) && (title.equals("ROI Manager"))) {
/* 471 */       return true;
/*     */     }
/* 473 */     if ((frame != null) && ((frame instanceof JFrame)))
/* 474 */       return true;
/* 475 */     ImageWindow win = imp.getWindow();
/*     */ 
/* 477 */     if ((imp.getStackSize() > 1) && (win != null) && (win.getClass().getName().startsWith("loci")))
/* 478 */       return true;
/* 479 */     return false;
/*     */   }
/*     */ 
/*     */   public void keyTyped(KeyEvent e) {
/* 483 */     char keyChar = e.getKeyChar();
/* 484 */     int flags = e.getModifiers();
/* 485 */     if (IJ.debugMode) IJ.log("keyTyped: char=\"" + keyChar + "\" (" + keyChar + "), flags= " + Integer.toHexString(flags) + " (" + KeyEvent.getKeyModifiersText(flags) + ")");
/*     */ 
/* 487 */     if ((keyChar == '\\') || (keyChar == '«') || (keyChar == 'ß'))
/* 488 */       if ((flags & 0x8) != 0)
/* 489 */         doCommand("Animation Options...");
/*     */       else
/* 491 */         doCommand("Start Animation [\\]");
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent e)
/*     */   {
/* 496 */     IJ.setKeyUp(e.getKeyCode());
/*     */   }
/*     */ 
/*     */   void abortPluginOrMacro(ImagePlus imp) {
/* 500 */     if (imp != null) {
/* 501 */       ImageWindow win = imp.getWindow();
/* 502 */       if (win != null) {
/* 503 */         win.running = false;
/* 504 */         win.running2 = false;
/*     */       }
/*     */     }
/* 507 */     Macro.abort();
/* 508 */     Interpreter.abort();
/* 509 */     if (Interpreter.getInstance() != null) IJ.beep(); 
/*     */   }
/*     */ 
/*     */   public void windowClosing(WindowEvent e)
/*     */   {
/* 513 */     doCommand("Quit");
/* 514 */     this.windowClosed = true;
/*     */   }
/*     */ 
/*     */   public void windowActivated(WindowEvent e) {
/* 518 */     if ((IJ.isMacintosh()) && (!this.quitting)) {
/* 519 */       IJ.wait(10);
/* 520 */       setMenuBar(Menus.getMenuBar());
/*     */     }
/*     */   }
/*     */   public void windowClosed(WindowEvent e) {
/*     */   }
/*     */   public void windowDeactivated(WindowEvent e) {
/*     */   }
/*     */   public void windowDeiconified(WindowEvent e) {
/*     */   }
/*     */   public void windowIconified(WindowEvent e) {
/*     */   }
/*     */   public void windowOpened(WindowEvent e) {
/*     */   }
/* 533 */   public void register(Class c) { if (!this.classes.contains(c))
/* 534 */       this.classes.addElement(c);
/*     */   }
/*     */ 
/*     */   public void quit()
/*     */   {
/* 539 */     Thread thread = new Thread(this, "Quit");
/* 540 */     thread.setPriority(5);
/* 541 */     thread.start();
/* 542 */     IJ.wait(10);
/*     */   }
/*     */ 
/*     */   public boolean quitting()
/*     */   {
/* 547 */     return this.quitting;
/*     */   }
/*     */ 
/*     */   public void savePreferences(Properties prefs)
/*     */   {
/* 552 */     Point loc = getLocation();
/* 553 */     prefs.put("ij.x", Integer.toString(loc.x));
/* 554 */     prefs.put("ij.y", Integer.toString(loc.y));
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 560 */     if (System.getProperty("java.version").substring(0, 3).compareTo("1.5") < 0) {
/* 561 */       JOptionPane.showMessageDialog(null, "ImageJ 1.45s requires Java 1.5 or later.");
/* 562 */       System.exit(0);
/*     */     }
/*     */ 
/* 565 */     boolean noGUI = false;
/* 566 */     int mode = 0;
/* 567 */     arguments = args;
/*     */ 
/* 569 */     int nArgs = args != null ? args.length : 0;
/* 570 */     for (int i = 0; i < nArgs; i++) {
/* 571 */       String arg = args[i];
/* 572 */       if (arg != null)
/*     */       {
/* 574 */         if (args[i].startsWith("-")) {
/* 575 */           if (args[i].startsWith("-batch")) {
/* 576 */             noGUI = true;
/* 577 */           } else if (args[i].startsWith("-debug")) {
/* 578 */             IJ.debugMode = true;
/* 579 */           } else if ((args[i].startsWith("-ijpath")) && (i + 1 < nArgs)) {
/* 580 */             Prefs.setHomeDir(args[(i + 1)]);
/* 581 */             args[(i + 1)] = null;
/* 582 */           } else if (args[i].startsWith("-port")) {
/* 583 */             int delta = (int)Tools.parseDouble(args[i].substring(5, args[i].length()), 0.0D);
/* 584 */             if (delta == 0)
/* 585 */               mode = 1;
/* 586 */             else if ((delta > 0) && (57294 + delta < 65536)) {
/* 587 */               port = 57294 + delta;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 593 */     if ((!noGUI) && (mode == 0) && (isRunning(args)))
/* 594 */       return;
/* 595 */     ImageJ ij = IJ.getInstance();
/* 596 */     if ((!noGUI) && ((ij == null) || ((ij != null) && (!ij.isShowing())))) {
/* 597 */       ij = new ImageJ(null, mode);
/* 598 */       ij.exitWhenQuitting = true;
/*     */     }
/* 600 */     int macros = 0;
/* 601 */     for (int i = 0; i < nArgs; i++) {
/* 602 */       String arg = args[i];
/* 603 */       if (arg != null)
/* 604 */         if (arg.startsWith("-")) {
/* 605 */           if (((arg.startsWith("-macro")) || (arg.startsWith("-batch"))) && (i + 1 < nArgs)) {
/* 606 */             String arg2 = i + 2 < nArgs ? args[(i + 2)] : null;
/* 607 */             Prefs.commandLineMacro = true;
/* 608 */             if ((noGUI) && (args[(i + 1)].endsWith(".js")))
/* 609 */               Interpreter.batchMode = true;
/* 610 */             IJ.runMacroFile(args[(i + 1)], arg2);
/* 611 */             break;
/* 612 */           }if ((arg.startsWith("-eval")) && (i + 1 < nArgs)) {
/* 613 */             String rtn = IJ.runMacro(args[(i + 1)]);
/* 614 */             if (rtn != null)
/* 615 */               System.out.print(rtn);
/* 616 */             args[(i + 1)] = null;
/* 617 */           } else if ((arg.startsWith("-run")) && (i + 1 < nArgs)) {
/* 618 */             IJ.run(args[(i + 1)]);
/* 619 */             args[(i + 1)] = null;
/*     */           }
/* 621 */         } else if ((macros == 0) && ((arg.endsWith(".ijm")) || (arg.endsWith(".txt")))) {
/* 622 */           IJ.runMacroFile(arg);
/* 623 */           macros++;
/* 624 */         } else if ((arg.length() > 0) && (arg.indexOf("ij.ImageJ") == -1)) {
/* 625 */           File file = new File(arg);
/* 626 */           IJ.open(file.getAbsolutePath());
/*     */         }
/*     */     }
/* 629 */     if ((IJ.debugMode) && (IJ.getInstance() == null))
/* 630 */       new JavaProperties().run("");
/* 631 */     if (noGUI) System.exit(0);
/*     */   }
/*     */ 
/*     */   static boolean isRunning(String[] args)
/*     */   {
/* 636 */     if (IJ.debugMode) IJ.log("isRunning: " + args.length);
/* 637 */     int macros = 0;
/* 638 */     int nArgs = args != null ? args.length : 0;
/*     */ 
/* 641 */     int nCommands = 0;
/*     */     try {
/* 643 */       sendArgument("user.dir " + System.getProperty("user.dir"));
/* 644 */       for (int i = 0; i < nArgs; i++) {
/* 645 */         String arg = args[i];
/* 646 */         if (IJ.debugMode) IJ.log("isRunning: " + i + " " + arg);
/* 647 */         if (arg != null) {
/* 648 */           String cmd = null;
/* 649 */           if ((macros == 0) && (arg.endsWith(".ijm"))) {
/* 650 */             cmd = "macro " + arg;
/* 651 */             macros++; } else {
/* 652 */             if ((arg.startsWith("-macro")) && (i + 1 < nArgs)) {
/* 653 */               String macroArg = i + 2 < nArgs ? "(" + args[(i + 2)] + ")" : "";
/* 654 */               cmd = "macro " + args[(i + 1)] + macroArg;
/* 655 */               sendArgument(cmd);
/* 656 */               nCommands++;
/* 657 */               break;
/* 658 */             }if ((arg.startsWith("-eval")) && (i + 1 < nArgs)) {
/* 659 */               cmd = "eval " + args[(i + 1)];
/* 660 */               args[(i + 1)] = null;
/* 661 */             } else if ((arg.startsWith("-run")) && (i + 1 < nArgs)) {
/* 662 */               cmd = "run " + args[(i + 1)];
/* 663 */               args[(i + 1)] = null;
/* 664 */             } else if ((arg.indexOf("ij.ImageJ") == -1) && (arg.length() > 0) && (!arg.startsWith("-"))) {
/* 665 */               cmd = "open " + arg; } 
/* 666 */           }if (cmd != null) {
/* 667 */             sendArgument(cmd);
/* 668 */             nCommands++;
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (IOException e) { if (IJ.debugMode) IJ.log("" + e);
/* 673 */       return false;
/*     */     }
/* 675 */     return true;
/*     */   }
/*     */ 
/*     */   static void sendArgument(String arg) throws IOException {
/* 679 */     Socket socket = new Socket("localhost", port);
/* 680 */     PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
/* 681 */     out.println(arg);
/* 682 */     out.close();
/* 683 */     socket.close();
/*     */   }
/*     */ 
/*     */   public static int getPort()
/*     */   {
/* 691 */     return port;
/*     */   }
/*     */ 
/*     */   public static String[] getArgs()
/*     */   {
/* 696 */     return arguments;
/*     */   }
/*     */ 
/*     */   public void exitWhenQuitting(boolean ewq)
/*     */   {
/* 701 */     this.exitWhenQuitting = ewq;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 706 */     this.quitting = true;
/* 707 */     boolean changes = false;
/* 708 */     int[] wList = WindowManager.getIDList();
/* 709 */     if (wList != null) {
/* 710 */       for (int i = 0; i < wList.length; i++) {
/* 711 */         ImagePlus imp = WindowManager.getImage(wList[i]);
/* 712 */         if ((imp != null) && (imp.changes == true)) {
/* 713 */           changes = true;
/* 714 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 718 */     Frame[] frames = WindowManager.getNonImageWindows();
/* 719 */     if (frames != null) {
/* 720 */       for (int i = 0; i < frames.length; i++) {
/* 721 */         if ((frames[i] != null) && ((frames[i] instanceof Editor)) && 
/* 722 */           (((Editor)frames[i]).fileChanged())) {
/* 723 */           changes = true;
/* 724 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 729 */     if ((this.windowClosed) && (!changes) && (Menus.window.getItemCount() > 5) && ((!IJ.macroRunning()) || (WindowManager.getImageCount() != 0))) {
/* 730 */       GenericDialog gd = new GenericDialog("ImageJ", this);
/* 731 */       gd.addMessage("Are you sure you want to quit ImageJ?");
/* 732 */       gd.showDialog();
/* 733 */       this.quitting = (!gd.wasCanceled());
/* 734 */       this.windowClosed = false;
/*     */     }
/* 736 */     if (!this.quitting)
/* 737 */       return;
/* 738 */     if (!WindowManager.closeAllWindows()) {
/* 739 */       this.quitting = false;
/* 740 */       return;
/*     */     }
/*     */ 
/* 743 */     if (this.applet == null) {
/* 744 */       saveWindowLocations();
/* 745 */       Prefs.savePreferences();
/*     */     }
/* 747 */     IJ.cleanup();
/*     */ 
/* 750 */     dispose();
/* 751 */     if (this.exitWhenQuitting)
/* 752 */       System.exit(0);
/*     */   }
/*     */ 
/*     */   void saveWindowLocations() {
/* 756 */     Frame frame = WindowManager.getFrame("B&C");
/* 757 */     if (frame != null)
/* 758 */       Prefs.saveLocation("b&c.loc", frame.getLocation());
/* 759 */     frame = WindowManager.getFrame("Threshold");
/* 760 */     if (frame != null)
/* 761 */       Prefs.saveLocation("threshold.loc", frame.getLocation());
/* 762 */     frame = WindowManager.getFrame("Results");
/* 763 */     if (frame != null) {
/* 764 */       Prefs.saveLocation("results.loc", frame.getLocation());
/* 765 */       Dimension d = frame.getSize();
/* 766 */       Prefs.set("results.width", d.width);
/* 767 */       Prefs.set("results.height", d.height);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.ImageJ
 * JD-Core Version:    0.6.2
 */