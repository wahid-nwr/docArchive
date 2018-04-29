/*      */ package ij.gui;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImageJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.Menus;
/*      */ import ij.Prefs;
/*      */ import ij.WindowManager;
/*      */ import ij.macro.Program;
/*      */ import ij.plugin.MacroInstaller;
/*      */ import ij.plugin.frame.Editor;
/*      */ import ij.plugin.frame.Recorder;
/*      */ import java.awt.Canvas;
/*      */ import java.awt.CheckboxMenuItem;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.MenuItem;
/*      */ import java.awt.PopupMenu;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.event.MouseMotionListener;
/*      */ import java.io.File;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ 
/*      */ public class Toolbar extends Canvas
/*      */   implements MouseListener, MouseMotionListener, ItemListener, ActionListener
/*      */ {
/*      */   public static final int RECTANGLE = 0;
/*      */   public static final int OVAL = 1;
/*      */   public static final int POLYGON = 2;
/*      */   public static final int FREEROI = 3;
/*      */   public static final int LINE = 4;
/*      */   public static final int POLYLINE = 5;
/*      */   public static final int FREELINE = 6;
/*      */   public static final int POINT = 7;
/*      */   public static final int CROSSHAIR = 7;
/*      */   public static final int WAND = 8;
/*      */   public static final int TEXT = 9;
/*      */   public static final int SPARE1 = 10;
/*      */   public static final int MAGNIFIER = 11;
/*      */   public static final int HAND = 12;
/*      */   public static final int DROPPER = 13;
/*      */   public static final int ANGLE = 14;
/*      */   public static final int SPARE2 = 15;
/*      */   public static final int SPARE3 = 16;
/*      */   public static final int SPARE4 = 17;
/*      */   public static final int SPARE5 = 18;
/*      */   public static final int SPARE6 = 19;
/*      */   public static final int SPARE7 = 20;
/*      */   public static final int SPARE8 = 21;
/*      */   public static final int SPARE9 = 22;
/*      */   public static final int DOUBLE_CLICK_THRESHOLD = 650;
/*      */   public static final int OVAL_ROI = 0;
/*      */   public static final int ELLIPSE_ROI = 1;
/*      */   public static final int BRUSH_ROI = 2;
/*      */   private static final int NUM_TOOLS = 23;
/*      */   private static final int NUM_BUTTONS = 21;
/*      */   private static final int SIZE = 26;
/*      */   private static final int OFFSET = 5;
/*      */   private static final String BRUSH_SIZE = "toolbar.brush.size";
/*      */   public static final String CORNER_DIAMETER = "toolbar.arc.size";
/*   52 */   private Dimension ps = new Dimension(546, 26);
/*      */   private boolean[] down;
/*      */   private static int current;
/*      */   private int previous;
/*      */   private int x;
/*      */   private int y;
/*      */   private int xOffset;
/*      */   private int yOffset;
/*      */   private long mouseDownTime;
/*      */   private Graphics g;
/*      */   private static Toolbar instance;
/*   61 */   private int mpPrevious = 0;
/*   62 */   private String[] names = new String[23];
/*   63 */   private String[] icons = new String[23];
/*   64 */   private PopupMenu[] menus = new PopupMenu[23];
/*      */   private int pc;
/*      */   private String icon;
/*      */   private MacroInstaller macroInstaller;
/*      */   private int startupTime;
/*      */   private PopupMenu rectPopup;
/*      */   private PopupMenu ovalPopup;
/*      */   private PopupMenu pointPopup;
/*      */   private PopupMenu linePopup;
/*      */   private PopupMenu switchPopup;
/*      */   private CheckboxMenuItem rectItem;
/*      */   private CheckboxMenuItem roundRectItem;
/*      */   private CheckboxMenuItem ovalItem;
/*      */   private CheckboxMenuItem ellipseItem;
/*      */   private CheckboxMenuItem brushItem;
/*      */   private CheckboxMenuItem pointItem;
/*      */   private CheckboxMenuItem multiPointItem;
/*      */   private CheckboxMenuItem straightLineItem;
/*      */   private CheckboxMenuItem polyLineItem;
/*      */   private CheckboxMenuItem freeLineItem;
/*      */   private CheckboxMenuItem arrowItem;
/*   74 */   private String currentSet = "Startup Macros";
/*      */ 
/*   76 */   private static Color foregroundColor = Prefs.getColor("fcolor", Color.black);
/*   77 */   private static Color backgroundColor = Prefs.getColor("bcolor", Color.white);
/*   78 */   private static int ovalType = 0;
/*   79 */   private static boolean multiPointMode = Prefs.multiPointMode;
/*      */   private static boolean roundRectMode;
/*      */   private static boolean arrowMode;
/*   82 */   private static int brushSize = (int)Prefs.get("toolbar.brush.size", 15.0D);
/*   83 */   private static int arcSize = (int)Prefs.get("toolbar.arc.size", 20.0D);
/*   84 */   private int lineType = 4;
/*      */ 
/*   86 */   private Color gray = ImageJ.backgroundColor;
/*   87 */   private Color brighter = this.gray.brighter();
/*   88 */   private Color darker = new Color(175, 175, 175);
/*   89 */   private Color evenDarker = new Color(110, 110, 110);
/*   90 */   private Color triangleColor = new Color(150, 0, 0);
/*   91 */   private Color toolColor = new Color(0, 25, 45);
/*      */ 
/*      */   public Toolbar()
/*      */   {
/*   95 */     this.down = new boolean[23];
/*   96 */     resetButtons();
/*   97 */     this.down[0] = true;
/*   98 */     setForeground(Color.black);
/*   99 */     setBackground(this.gray);
/*  100 */     addMouseListener(this);
/*  101 */     addMouseMotionListener(this);
/*  102 */     instance = this;
/*  103 */     this.names[22] = "Switch to alternate macro tool sets";
/*  104 */     this.icons[22] = "C900T1c12>T7c12>";
/*  105 */     addPopupMenus();
/*  106 */     if ((IJ.isMacOSX()) || (IJ.isVista())) Prefs.antialiasedTools = true; 
/*      */   }
/*      */ 
/*      */   void addPopupMenus()
/*      */   {
/*  110 */     this.rectPopup = new PopupMenu();
/*  111 */     if (Menus.getFontSize() != 0)
/*  112 */       this.rectPopup.setFont(Menus.getFont());
/*  113 */     this.rectItem = new CheckboxMenuItem("Rectangle Tool", !roundRectMode);
/*  114 */     this.rectItem.addItemListener(this);
/*  115 */     this.rectPopup.add(this.rectItem);
/*  116 */     this.roundRectItem = new CheckboxMenuItem("Rounded Rectangle Tool", roundRectMode);
/*  117 */     this.roundRectItem.addItemListener(this);
/*  118 */     this.rectPopup.add(this.roundRectItem);
/*  119 */     add(this.rectPopup);
/*      */ 
/*  121 */     this.ovalPopup = new PopupMenu();
/*  122 */     if (Menus.getFontSize() != 0)
/*  123 */       this.ovalPopup.setFont(Menus.getFont());
/*  124 */     this.ovalItem = new CheckboxMenuItem("Oval selections", ovalType == 0);
/*  125 */     this.ovalItem.addItemListener(this);
/*  126 */     this.ovalPopup.add(this.ovalItem);
/*  127 */     this.ellipseItem = new CheckboxMenuItem("Elliptical selections", ovalType == 1);
/*  128 */     this.ellipseItem.addItemListener(this);
/*  129 */     this.ovalPopup.add(this.ellipseItem);
/*  130 */     this.brushItem = new CheckboxMenuItem("Selection Brush Tool", ovalType == 2);
/*  131 */     this.brushItem.addItemListener(this);
/*  132 */     this.ovalPopup.add(this.brushItem);
/*  133 */     add(this.ovalPopup);
/*      */ 
/*  135 */     this.pointPopup = new PopupMenu();
/*  136 */     if (Menus.getFontSize() != 0)
/*  137 */       this.pointPopup.setFont(Menus.getFont());
/*  138 */     this.pointItem = new CheckboxMenuItem("Point Tool", !multiPointMode);
/*  139 */     this.pointItem.addItemListener(this);
/*  140 */     this.pointPopup.add(this.pointItem);
/*  141 */     this.multiPointItem = new CheckboxMenuItem("Multi-point Tool", multiPointMode);
/*  142 */     this.multiPointItem.addItemListener(this);
/*  143 */     this.pointPopup.add(this.multiPointItem);
/*  144 */     add(this.pointPopup);
/*      */ 
/*  146 */     this.linePopup = new PopupMenu();
/*  147 */     if (Menus.getFontSize() != 0)
/*  148 */       this.linePopup.setFont(Menus.getFont());
/*  149 */     this.straightLineItem = new CheckboxMenuItem("Straight Line", (this.lineType == 4) && (!arrowMode));
/*  150 */     this.straightLineItem.addItemListener(this);
/*  151 */     this.linePopup.add(this.straightLineItem);
/*  152 */     this.polyLineItem = new CheckboxMenuItem("Segmented Line", this.lineType == 5);
/*  153 */     this.polyLineItem.addItemListener(this);
/*  154 */     this.linePopup.add(this.polyLineItem);
/*  155 */     this.freeLineItem = new CheckboxMenuItem("Freehand Line", this.lineType == 6);
/*  156 */     this.freeLineItem.addItemListener(this);
/*  157 */     this.linePopup.add(this.freeLineItem);
/*  158 */     this.arrowItem = new CheckboxMenuItem("Arrow tool", (this.lineType == 4) && (!arrowMode));
/*  159 */     this.arrowItem.addItemListener(this);
/*  160 */     this.linePopup.add(this.arrowItem);
/*  161 */     add(this.linePopup);
/*      */ 
/*  163 */     this.switchPopup = new PopupMenu();
/*  164 */     if (Menus.getFontSize() != 0)
/*  165 */       this.switchPopup.setFont(Menus.getFont());
/*  166 */     add(this.switchPopup);
/*      */   }
/*      */ 
/*      */   public static int getToolId()
/*      */   {
/*  172 */     return current;
/*      */   }
/*      */ 
/*      */   public int getToolId(String name)
/*      */   {
/*  178 */     int tool = -1;
/*  179 */     for (int i = 0; i <= 22; i++) {
/*  180 */       if ((this.names[i] != null) && (this.names[i].startsWith(name))) {
/*  181 */         tool = i;
/*  182 */         break;
/*      */       }
/*      */     }
/*  185 */     return tool;
/*      */   }
/*      */ 
/*      */   public static Toolbar getInstance()
/*      */   {
/*  190 */     return instance;
/*      */   }
/*      */ 
/*      */   private void drawButtons(Graphics g) {
/*  194 */     if (Prefs.antialiasedTools) {
/*  195 */       Graphics2D g2d = (Graphics2D)g;
/*  196 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  197 */       g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/*      */     }
/*  199 */     for (int i = 0; i < 4; i++)
/*  200 */       drawButton(g, i);
/*  201 */     drawButton(g, this.lineType);
/*  202 */     for (int i = 7; i < 23; i++)
/*  203 */       drawButton(g, i);
/*      */   }
/*      */ 
/*      */   private void fill3DRect(Graphics g, int x, int y, int width, int height, boolean raised) {
/*  207 */     if (null == g) return;
/*  208 */     if (raised)
/*  209 */       g.setColor(this.gray);
/*      */     else
/*  211 */       g.setColor(this.darker);
/*  212 */     g.fillRect(x + 1, y + 1, width - 2, height - 2);
/*  213 */     g.setColor(raised ? this.brighter : this.evenDarker);
/*  214 */     g.drawLine(x, y, x, y + height - 1);
/*  215 */     g.drawLine(x + 1, y, x + width - 2, y);
/*  216 */     g.setColor(raised ? this.evenDarker : this.brighter);
/*  217 */     g.drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
/*  218 */     g.drawLine(x + width - 1, y, x + width - 1, y + height - 2);
/*      */   }
/*      */ 
/*      */   private void drawButton(Graphics g, int tool) {
/*  222 */     if (g == null) return;
/*  223 */     int index = toolIndex(tool);
/*  224 */     fill3DRect(g, index * 26 + 1, 1, 26, 25, this.down[tool] == 0);
/*  225 */     g.setColor(this.toolColor);
/*  226 */     int x = index * 26 + 5;
/*  227 */     int y = 5;
/*  228 */     if (this.down[tool] != 0) { x++; y++; }
/*  229 */     this.g = g;
/*  230 */     if ((tool >= 10) && (tool <= 22) && (this.icons[tool] != null)) {
/*  231 */       drawIcon(g, tool, x, y);
/*  232 */       return;
/*      */     }
/*  234 */     switch (tool) {
/*      */     case 0:
/*  236 */       this.xOffset = x; this.yOffset = y;
/*  237 */       if (roundRectMode)
/*  238 */         g.drawRoundRect(x + 1, y + 2, 15, 12, 8, 8);
/*      */       else
/*  240 */         g.drawRect(x + 1, y + 2, 15, 12);
/*  241 */       drawTriangle(15, 14);
/*  242 */       return;
/*      */     case 1:
/*  244 */       this.xOffset = x; this.yOffset = y;
/*  245 */       if (ovalType == 2) {
/*  246 */         m(9, 2); d(13, 2); d(13, 2); d(15, 5); d(15, 8);
/*  247 */         d(13, 10); d(10, 10); d(8, 13); d(4, 13);
/*  248 */         d(2, 11); d(2, 7); d(4, 5); d(7, 5); d(9, 2);
/*  249 */       } else if (ovalType == 1) {
/*  250 */         this.yOffset = (y + 1);
/*  251 */         m(11, 0); d(13, 0); d(14, 1); d(15, 1); d(16, 2); d(17, 3); d(17, 7);
/*  252 */         d(12, 12); d(11, 12); d(10, 13); d(8, 13); d(7, 14); d(4, 14); d(3, 13);
/*  253 */         d(2, 13); d(1, 12); d(1, 11); d(0, 10); d(0, 9); d(1, 8); d(1, 7);
/*  254 */         d(6, 2); d(7, 2); d(8, 1); d(10, 1); d(11, 0);
/*      */       } else {
/*  256 */         g.drawOval(x + 1, y + 2, 15, 12);
/*  257 */       }drawTriangle(15, 14);
/*  258 */       return;
/*      */     case 2:
/*  260 */       this.xOffset = (x + 1); this.yOffset = (y + 3);
/*  261 */       m(4, 0); d(14, 0); d(14, 1); d(10, 5); d(10, 6);
/*  262 */       d(13, 9); d(13, 10); d(0, 10); d(0, 4); d(4, 0);
/*  263 */       return;
/*      */     case 3:
/*  265 */       this.xOffset = (x + 1); this.yOffset = (y + 3);
/*  266 */       m(3, 0); d(5, 0); d(7, 2); d(9, 2); d(11, 0); d(13, 0); d(14, 1); d(15, 2);
/*  267 */       d(15, 4); d(14, 5); d(14, 6); d(12, 8); d(11, 8); d(10, 9); d(9, 9); d(8, 10);
/*  268 */       d(5, 10); d(3, 8); d(2, 8); d(1, 7); d(1, 6); d(0, 5); d(0, 2); d(1, 1); d(2, 1);
/*  269 */       return;
/*      */     case 4:
/*  271 */       this.xOffset = x; this.yOffset = y;
/*  272 */       if (arrowMode) {
/*  273 */         m(1, 14); d(14, 1); m(6, 5); d(14, 1); m(10, 9); d(14, 1); m(6, 5); d(10, 9);
/*      */       } else {
/*  275 */         m(0, 12); d(17, 3);
/*      */       }
/*  277 */       drawTriangle(12, 14);
/*  278 */       return;
/*      */     case 5:
/*  280 */       this.xOffset = x; this.yOffset = y;
/*  281 */       m(14, 6); d(11, 3); d(1, 3); d(1, 4); d(6, 9); d(2, 13);
/*  282 */       drawTriangle(12, 14);
/*  283 */       return;
/*      */     case 6:
/*  285 */       this.xOffset = x; this.yOffset = y;
/*  286 */       m(16, 4); d(14, 6); d(12, 6); d(9, 3); d(8, 3); d(6, 7); d(2, 11); d(1, 11);
/*  287 */       drawTriangle(12, 14);
/*  288 */       return;
/*      */     case 7:
/*  290 */       this.xOffset = x; this.yOffset = y;
/*  291 */       if (multiPointMode) {
/*  292 */         drawPoint(1, 3); drawPoint(9, 1); drawPoint(15, 5);
/*  293 */         drawPoint(10, 11); drawPoint(2, 12);
/*      */       } else {
/*  295 */         m(1, 8); d(6, 8); d(6, 6); d(10, 6); d(10, 10); d(6, 10); d(6, 9);
/*  296 */         m(8, 1); d(8, 5); m(11, 8); d(15, 8); m(8, 11); d(8, 15);
/*  297 */         m(8, 8); d(8, 8);
/*  298 */         g.setColor(Roi.getColor());
/*  299 */         g.fillRect(x + 7, y + 7, 3, 3);
/*      */       }
/*  301 */       drawTriangle(14, 14);
/*  302 */       return;
/*      */     case 8:
/*  304 */       this.xOffset = (x + 2); this.yOffset = (y + 2);
/*  305 */       dot(4, 0); m(2, 0); d(3, 1); d(4, 2); m(0, 0); d(1, 1);
/*  306 */       m(0, 2); d(1, 3); d(2, 4); dot(0, 4); m(3, 3); d(12, 12);
/*  307 */       return;
/*      */     case 9:
/*  309 */       this.xOffset = (x + 2); this.yOffset = (y + 1);
/*  310 */       m(0, 13); d(3, 13);
/*  311 */       m(1, 12); d(7, 0); d(12, 13);
/*  312 */       m(11, 13); d(14, 13);
/*  313 */       m(3, 8); d(10, 8);
/*  314 */       return;
/*      */     case 11:
/*  316 */       this.xOffset = (x + 2); this.yOffset = (y + 2);
/*  317 */       m(3, 0); d(3, 0); d(5, 0); d(8, 3); d(8, 5); d(7, 6); d(7, 7);
/*  318 */       d(6, 7); d(5, 8); d(3, 8); d(0, 5); d(0, 3); d(3, 0);
/*  319 */       m(8, 8); d(9, 8); d(13, 12); d(13, 13); d(12, 13); d(8, 9); d(8, 8);
/*  320 */       return;
/*      */     case 12:
/*  322 */       this.xOffset = (x + 1); this.yOffset = (y + 1);
/*  323 */       m(5, 14); d(2, 11); d(2, 10); d(0, 8); d(0, 7); d(1, 6); d(2, 6); d(4, 8);
/*  324 */       d(4, 6); d(3, 5); d(3, 4); d(2, 3); d(2, 2); d(3, 1); d(4, 1); d(5, 2); d(5, 3);
/*  325 */       m(6, 5); d(6, 1); d(7, 0); d(8, 0); d(9, 1); d(9, 5);
/*  326 */       m(9, 1); d(11, 1); d(12, 2); d(12, 6);
/*  327 */       m(13, 4); d(14, 3); d(15, 4); d(15, 7); d(14, 8);
/*  328 */       d(14, 10); d(13, 11); d(13, 12); d(12, 13); d(12, 14);
/*  329 */       return;
/*      */     case 13:
/*  331 */       this.xOffset = x; this.yOffset = y;
/*  332 */       g.setColor(foregroundColor);
/*      */ 
/*  334 */       m(12, 2); d(14, 2);
/*  335 */       m(11, 3); d(15, 3);
/*  336 */       m(11, 4); d(15, 4);
/*  337 */       m(8, 5); d(15, 5);
/*  338 */       m(9, 6); d(14, 6);
/*  339 */       m(10, 7); d(12, 7); d(12, 9);
/*  340 */       m(8, 7); d(2, 13); d(2, 15); d(4, 15); d(11, 8);
/*  341 */       g.setColor(backgroundColor);
/*  342 */       m(0, 0); d(16, 0); d(16, 16); d(0, 16); d(0, 0);
/*  343 */       return;
/*      */     case 14:
/*  345 */       this.xOffset = (x + 1); this.yOffset = (y + 2);
/*  346 */       m(0, 11); d(11, 0); m(0, 11); d(15, 11);
/*  347 */       m(10, 11); d(10, 8); m(9, 7); d(9, 6); dot(8, 5);
/*  348 */       return;
/*      */     case 10:
/*      */     }
/*      */   }
/*      */ 
/*  353 */   void drawTriangle(int x, int y) { this.g.setColor(this.triangleColor);
/*  354 */     this.xOffset += x; this.yOffset += y;
/*  355 */     m(0, 0); d(4, 0); m(1, 1); d(3, 1); dot(2, 2); }
/*      */ 
/*      */   void drawPoint(int x, int y)
/*      */   {
/*  359 */     this.g.setColor(this.toolColor);
/*  360 */     m(x - 2, y); d(x + 2, y);
/*  361 */     m(x, y - 2); d(x, y + 2);
/*  362 */     this.g.setColor(Roi.getColor());
/*  363 */     dot(x, y);
/*      */   }
/*      */ 
/*      */   void drawIcon(Graphics g, int tool, int x, int y) {
/*  367 */     if (null == g) return;
/*  368 */     this.icon = this.icons[tool];
/*  369 */     if (this.icon == null) return;
/*  370 */     this.icon = this.icon;
/*  371 */     int length = this.icon.length();
/*      */ 
/*  373 */     this.pc = 0;
/*      */     while (true) {
/*  375 */       char command = this.icon.charAt(this.pc++);
/*  376 */       if (this.pc >= length)
/*      */         break;
/*      */       int x2;
/*      */       int y2;
/*  377 */       switch (command) { case 'B':
/*  378 */         x += v(); y += v(); break;
/*      */       case 'R':
/*  379 */         g.drawRect(x + v(), y + v(), v(), v()); break;
/*      */       case 'F':
/*  380 */         g.fillRect(x + v(), y + v(), v(), v()); break;
/*      */       case 'O':
/*  381 */         g.drawOval(x + v(), y + v(), v(), v()); break;
/*      */       case 'o':
/*  382 */         g.fillOval(x + v(), y + v(), v(), v()); break;
/*      */       case 'C':
/*  383 */         g.setColor(new Color(v() * 16, v() * 16, v() * 16)); break;
/*      */       case 'L':
/*  384 */         g.drawLine(x + v(), y + v(), x + v(), y + v()); break;
/*      */       case 'D':
/*  385 */         g.fillRect(x + v(), y + v(), 1, 1); break;
/*      */       case 'P':
/*  387 */         int x1 = x + v(); int y1 = y + v();
/*      */         while (true) {
/*  389 */           x2 = v(); if (x2 == 0) break;
/*  390 */           y2 = v(); if (y2 == 0) break;
/*  391 */           x2 += x; y2 += y;
/*  392 */           g.drawLine(x1, y1, x2, y2);
/*  393 */           x1 = x2; y1 = y2;
/*      */         }
/*      */ 
/*      */       case 'T':
/*  397 */         x2 = x + v();
/*  398 */         y2 = y + v();
/*  399 */         int size = v() * 10 + v();
/*  400 */         char[] c = new char[1];
/*  401 */         c[0] = (this.pc < this.icon.length() ? this.icon.charAt(this.pc++) : 'e');
/*  402 */         g.setFont(new Font("SansSerif", 1, size));
/*  403 */         g.drawString(new String(c), x2, y2);
/*  404 */         break;
/*      */       }
/*      */ 
/*  407 */       if (this.pc >= length) break;
/*      */     }
/*  409 */     if ((this.menus[tool] != null) && (this.menus[tool].getItemCount() > 0)) {
/*  410 */       this.xOffset = x; this.yOffset = y;
/*  411 */       drawTriangle(14, 14);
/*      */     }
/*      */   }
/*      */ 
/*      */   int v() {
/*  416 */     if (this.pc >= this.icon.length()) return 0;
/*  417 */     char c = this.icon.charAt(this.pc++);
/*      */ 
/*  419 */     switch (c) { case '0':
/*  420 */       return 0;
/*      */     case '1':
/*  421 */       return 1;
/*      */     case '2':
/*  422 */       return 2;
/*      */     case '3':
/*  423 */       return 3;
/*      */     case '4':
/*  424 */       return 4;
/*      */     case '5':
/*  425 */       return 5;
/*      */     case '6':
/*  426 */       return 6;
/*      */     case '7':
/*  427 */       return 7;
/*      */     case '8':
/*  428 */       return 8;
/*      */     case '9':
/*  429 */       return 9;
/*      */     case 'a':
/*  430 */       return 10;
/*      */     case 'b':
/*  431 */       return 11;
/*      */     case 'c':
/*  432 */       return 12;
/*      */     case 'd':
/*  433 */       return 13;
/*      */     case 'e':
/*  434 */       return 14;
/*      */     case 'f':
/*  435 */       return 15;
/*      */     case ':':
/*      */     case ';':
/*      */     case '<':
/*      */     case '=':
/*      */     case '>':
/*      */     case '?':
/*      */     case '@':
/*      */     case 'A':
/*      */     case 'B':
/*      */     case 'C':
/*      */     case 'D':
/*      */     case 'E':
/*      */     case 'F':
/*      */     case 'G':
/*      */     case 'H':
/*      */     case 'I':
/*      */     case 'J':
/*      */     case 'K':
/*      */     case 'L':
/*      */     case 'M':
/*      */     case 'N':
/*      */     case 'O':
/*      */     case 'P':
/*      */     case 'Q':
/*      */     case 'R':
/*      */     case 'S':
/*      */     case 'T':
/*      */     case 'U':
/*      */     case 'V':
/*      */     case 'W':
/*      */     case 'X':
/*      */     case 'Y':
/*      */     case 'Z':
/*      */     case '[':
/*      */     case '\\':
/*      */     case ']':
/*      */     case '^':
/*      */     case '_':
/*  436 */     case '`': } return 0;
/*      */   }
/*      */ 
/*      */   private void showMessage(int tool)
/*      */   {
/*  441 */     if ((tool >= 10) && (tool <= 22) && (this.names[tool] != null)) {
/*  442 */       String name = this.names[tool];
/*  443 */       int index = name.indexOf("Action Tool");
/*  444 */       if (index != -1) {
/*  445 */         name = name.substring(0, index);
/*      */       } else {
/*  447 */         index = name.indexOf("Menu Tool");
/*  448 */         if (index != -1)
/*  449 */           name = name.substring(0, index + 4);
/*      */       }
/*  451 */       IJ.showStatus(name);
/*  452 */       return;
/*      */     }
/*  454 */     String hint = " (right click to switch)";
/*  455 */     switch (tool) {
/*      */     case 0:
/*  457 */       if (roundRectMode)
/*  458 */         IJ.showStatus("Rectangular or *rounded rectangular* selections" + hint);
/*      */       else
/*  460 */         IJ.showStatus("*Rectangular* or rounded rectangular selections" + hint);
/*  461 */       return;
/*      */     case 1:
/*  463 */       if (ovalType == 2)
/*  464 */         IJ.showStatus("Oval, elliptical or *brush* selections" + hint);
/*  465 */       else if (ovalType == 1)
/*  466 */         IJ.showStatus("Oval, *elliptical* or brush selections" + hint);
/*      */       else
/*  468 */         IJ.showStatus("*Oval*, elliptical or brush selections" + hint);
/*  469 */       return;
/*      */     case 2:
/*  471 */       IJ.showStatus("Polygon selections");
/*  472 */       return;
/*      */     case 3:
/*  474 */       IJ.showStatus("Freehand selections");
/*  475 */       return;
/*      */     case 4:
/*  477 */       if (arrowMode)
/*  478 */         IJ.showStatus("Straight, segmented or freehand lines, or *arrows*" + hint);
/*      */       else
/*  480 */         IJ.showStatus("*Straight*, segmented or freehand lines, or arrows" + hint);
/*  481 */       return;
/*      */     case 5:
/*  483 */       IJ.showStatus("Straight, *segmented* or freehand lines, or arrows" + hint);
/*  484 */       return;
/*      */     case 6:
/*  486 */       IJ.showStatus("Straight, segmented or *freehand* lines, or arrows" + hint);
/*  487 */       return;
/*      */     case 7:
/*  489 */       if (multiPointMode)
/*  490 */         IJ.showStatus("Point or *multi-point* selections" + hint);
/*      */       else
/*  492 */         IJ.showStatus("*Point* or multi-point selections" + hint);
/*  493 */       return;
/*      */     case 8:
/*  495 */       IJ.showStatus("Wand (tracing) tool");
/*  496 */       return;
/*      */     case 9:
/*  498 */       IJ.showStatus("Text tool");
/*  499 */       TextRoi.recordSetFont();
/*  500 */       return;
/*      */     case 11:
/*  502 */       IJ.showStatus("Magnifying glass (or use \"+\" and \"-\" keys)");
/*  503 */       return;
/*      */     case 12:
/*  505 */       IJ.showStatus("Scrolling tool (or press space bar and drag)");
/*  506 */       return;
/*      */     case 13:
/*  508 */       IJ.showStatus("Color picker (" + foregroundColor.getRed() + "," + foregroundColor.getGreen() + "," + foregroundColor.getBlue() + ")");
/*      */ 
/*  510 */       return;
/*      */     case 14:
/*  512 */       IJ.showStatus("Angle tool");
/*  513 */       return;
/*      */     case 10:
/*  515 */     }IJ.showStatus("ImageJ " + IJ.getVersion() + " / Java " + System.getProperty("java.version") + (IJ.is64Bit() ? " (64-bit)" : " (32-bit)"));
/*      */   }
/*      */ 
/*      */   private void m(int x, int y)
/*      */   {
/*  521 */     this.x = (this.xOffset + x);
/*  522 */     this.y = (this.yOffset + y);
/*      */   }
/*      */ 
/*      */   private void d(int x, int y) {
/*  526 */     x += this.xOffset;
/*  527 */     y += this.yOffset;
/*  528 */     this.g.drawLine(this.x, this.y, x, y);
/*  529 */     this.x = x;
/*  530 */     this.y = y;
/*      */   }
/*      */ 
/*      */   private void dot(int x, int y) {
/*  534 */     this.g.fillRect(x + this.xOffset, y + this.yOffset, 1, 1);
/*      */   }
/*      */ 
/*      */   private void resetButtons() {
/*  538 */     for (int i = 0; i < 23; i++)
/*  539 */       this.down[i] = false;
/*      */   }
/*      */ 
/*      */   public void paint(Graphics g) {
/*  543 */     if (null == g) return;
/*  544 */     drawButtons(g);
/*      */   }
/*      */ 
/*      */   public boolean setTool(String name) {
/*  548 */     if (name == null) return false;
/*  549 */     if (name.indexOf(" Tool") != -1) {
/*  550 */       for (int i = 10; i <= 22; i++) {
/*  551 */         if (name.equals(this.names[i])) {
/*  552 */           setTool(i);
/*  553 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  557 */     name = name.toLowerCase(Locale.US);
/*  558 */     boolean ok = true;
/*  559 */     if (name.indexOf("round") != -1) {
/*  560 */       roundRectMode = true;
/*  561 */       setTool(0);
/*  562 */     } else if (name.indexOf("rect") != -1) {
/*  563 */       roundRectMode = false;
/*  564 */       setTool(0);
/*  565 */     } else if (name.indexOf("oval") != -1) {
/*  566 */       ovalType = 0;
/*  567 */       setTool(1);
/*  568 */     } else if (name.indexOf("ellip") != -1) {
/*  569 */       ovalType = 1;
/*  570 */       setTool(1);
/*  571 */     } else if (name.indexOf("brush") != -1) {
/*  572 */       ovalType = 2;
/*  573 */       setTool(1);
/*  574 */     } else if (name.indexOf("polygon") != -1) {
/*  575 */       setTool(2);
/*  576 */     } else if (name.indexOf("polyline") != -1) {
/*  577 */       setTool(5);
/*  578 */     } else if (name.indexOf("freeline") != -1) {
/*  579 */       setTool(6);
/*  580 */     } else if (name.indexOf("line") != -1) {
/*  581 */       arrowMode = false;
/*  582 */       setTool(4);
/*  583 */     } else if (name.indexOf("arrow") != -1) {
/*  584 */       arrowMode = true;
/*  585 */       setTool(4);
/*  586 */     } else if (name.indexOf("free") != -1) {
/*  587 */       setTool(3);
/*  588 */     } else if (name.indexOf("multi") != -1) {
/*  589 */       multiPointMode = true;
/*  590 */       Prefs.multiPointMode = true;
/*  591 */       setTool(7);
/*  592 */     } else if (name.indexOf("point") != -1) {
/*  593 */       multiPointMode = false;
/*  594 */       Prefs.multiPointMode = false;
/*  595 */       setTool(7);
/*  596 */     } else if (name.indexOf("wand") != -1) {
/*  597 */       setTool(8);
/*  598 */     } else if (name.indexOf("text") != -1) {
/*  599 */       setTool(9);
/*  600 */     } else if (name.indexOf("hand") != -1) {
/*  601 */       setTool(12);
/*  602 */     } else if (name.indexOf("zoom") != -1) {
/*  603 */       setTool(11);
/*  604 */     } else if ((name.indexOf("dropper") != -1) || (name.indexOf("color") != -1)) {
/*  605 */       setTool(13);
/*  606 */     } else if (name.indexOf("angle") != -1) {
/*  607 */       setTool(14);
/*      */     } else {
/*  609 */       ok = false;
/*  610 */     }return ok;
/*      */   }
/*      */ 
/*      */   public static String getToolName()
/*      */   {
/*  615 */     String name = instance.getName(current);
/*  616 */     if ((current >= 10) && (current <= 22) && (instance.names[current] != null))
/*  617 */       name = instance.names[current];
/*  618 */     return name != null ? name : "";
/*      */   }
/*      */ 
/*      */   String getName(int id)
/*      */   {
/*  623 */     switch (id) { case 0:
/*  624 */       return roundRectMode ? "roundrect" : "rectangle";
/*      */     case 1:
/*  626 */       switch (ovalType) { case 0:
/*  627 */         return "oval";
/*      */       case 1:
/*  628 */         return "ellipse";
/*      */       case 2:
/*  629 */         return "brush"; }
/*      */     case 2:
/*  631 */       return "polygon";
/*      */     case 3:
/*  632 */       return "freehand";
/*      */     case 4:
/*  633 */       return arrowMode ? "arrow" : "line";
/*      */     case 5:
/*  634 */       return "polyline";
/*      */     case 6:
/*  635 */       return "freeline";
/*      */     case 14:
/*  636 */       return "angle";
/*      */     case 7:
/*  637 */       return Prefs.multiPointMode ? "multipoint" : "point";
/*      */     case 8:
/*  638 */       return "wand";
/*      */     case 9:
/*  639 */       return "text";
/*      */     case 12:
/*  640 */       return "hand";
/*      */     case 11:
/*  641 */       return "zoom";
/*      */     case 13:
/*  642 */       return "dropper";
/*  643 */     case 10: } return null;
/*      */   }
/*      */ 
/*      */   public void setTool(int tool)
/*      */   {
/*  649 */     if (((tool == current) && (tool != 0) && (tool != 1) && (tool != 7)) || (tool < 0) || (tool >= 22))
/*  650 */       return;
/*  651 */     if ((tool == 10) || ((tool >= 15) && (tool <= 21))) {
/*  652 */       if (this.names[tool] == null)
/*  653 */         this.names[tool] = "Spare tool";
/*  654 */       if (this.names[tool].indexOf("Action Tool") != -1)
/*  655 */         return;
/*      */     }
/*  657 */     if (isLine(tool)) this.lineType = tool;
/*  658 */     setTool2(tool);
/*      */   }
/*      */ 
/*      */   private void setTool2(int tool) {
/*  662 */     if (!isValidTool(tool)) return;
/*  663 */     String previousName = getToolName();
/*  664 */     current = tool;
/*  665 */     this.down[current] = true;
/*  666 */     if (current != this.previous)
/*  667 */       this.down[this.previous] = false;
/*  668 */     Graphics g = getGraphics();
/*  669 */     if (g == null)
/*  670 */       return;
/*  671 */     if (Prefs.antialiasedTools) {
/*  672 */       Graphics2D g2d = (Graphics2D)g;
/*  673 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*      */     }
/*  675 */     drawButton(g, this.previous);
/*  676 */     drawButton(g, current);
/*  677 */     if (null == g) return;
/*  678 */     g.dispose();
/*  679 */     showMessage(current);
/*  680 */     this.previous = current;
/*  681 */     if (Recorder.record) {
/*  682 */       String name = getName(current);
/*  683 */       if (name != null) Recorder.record("setTool", name);
/*      */     }
/*  685 */     if (IJ.isMacOSX())
/*  686 */       repaint();
/*  687 */     if (!previousName.equals(getToolName()))
/*  688 */       IJ.notifyEventListeners(4);
/*      */   }
/*      */ 
/*      */   boolean isValidTool(int tool) {
/*  692 */     if ((tool < 0) || (tool >= 23))
/*  693 */       return false;
/*  694 */     if (((tool == 10) || ((tool >= 15) && (tool <= 22))) && (this.names[tool] == null))
/*  695 */       return false;
/*  696 */     return true;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Color getColor()
/*      */   {
/*  704 */     return foregroundColor;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setColor(Color c)
/*      */   {
/*  712 */     if (c != null) {
/*  713 */       foregroundColor = c;
/*  714 */       drawButton(getGraphics(), 13);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Color getForegroundColor() {
/*  719 */     return foregroundColor;
/*      */   }
/*      */ 
/*      */   public static void setForegroundColor(Color c) {
/*  723 */     if (c != null) {
/*  724 */       foregroundColor = c;
/*  725 */       repaintTool(13);
/*  726 */       if (!IJ.isMacro()) setRoiColor(c);
/*  727 */       IJ.notifyEventListeners(0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Color getBackgroundColor() {
/*  732 */     return backgroundColor;
/*      */   }
/*      */ 
/*      */   public static void setBackgroundColor(Color c) {
/*  736 */     if (c != null) {
/*  737 */       backgroundColor = c;
/*  738 */       repaintTool(13);
/*  739 */       IJ.notifyEventListeners(1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void setRoiColor(Color c) {
/*  744 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  745 */     if (imp == null) return;
/*  746 */     Roi roi = imp.getRoi();
/*  747 */     if ((roi != null) && (roi.isDrawingTool())) {
/*  748 */       roi.setStrokeColor(c);
/*  749 */       imp.draw();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static int getBrushSize()
/*      */   {
/*  755 */     if (ovalType == 2) {
/*  756 */       return brushSize;
/*      */     }
/*  758 */     return 0;
/*      */   }
/*      */ 
/*      */   public static void setBrushSize(int size)
/*      */   {
/*  763 */     brushSize = size;
/*  764 */     if (brushSize < 1) brushSize = 1;
/*  765 */     Prefs.set("toolbar.brush.size", brushSize);
/*      */   }
/*      */ 
/*      */   public static int getRoundRectArcSize()
/*      */   {
/*  770 */     if (!roundRectMode) {
/*  771 */       return 0;
/*      */     }
/*  773 */     return arcSize;
/*      */   }
/*      */ 
/*      */   public static void setRoundRectArcSize(int size)
/*      */   {
/*  778 */     if (size <= 0) {
/*  779 */       roundRectMode = false;
/*      */     } else {
/*  781 */       arcSize = size;
/*  782 */       Prefs.set("toolbar.arc.size", arcSize);
/*      */     }
/*  784 */     repaintTool(0);
/*  785 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  786 */     Roi roi = imp != null ? imp.getRoi() : null;
/*  787 */     if ((roi != null) && (roi.getType() == 0))
/*  788 */       roi.setCornerDiameter(roundRectMode ? arcSize : 0);
/*      */   }
/*      */ 
/*      */   public static boolean getMultiPointMode()
/*      */   {
/*  793 */     return multiPointMode;
/*      */   }
/*      */ 
/*      */   public static int getOvalToolType()
/*      */   {
/*  798 */     return ovalType;
/*      */   }
/*      */ 
/*      */   public static int getButtonSize() {
/*  802 */     return 26;
/*      */   }
/*      */ 
/*      */   static void repaintTool(int tool) {
/*  806 */     if (IJ.getInstance() != null) {
/*  807 */       Toolbar tb = getInstance();
/*  808 */       Graphics g = tb.getGraphics();
/*  809 */       if (g == null) return;
/*  810 */       if (Prefs.antialiasedTools)
/*  811 */         ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  812 */       tb.drawButton(g, tool);
/*  813 */       if (g != null) g.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   int toolIndex(int tool)
/*      */   {
/*  821 */     switch (tool) { case 0:
/*  822 */       return 0;
/*      */     case 1:
/*  823 */       return 1;
/*      */     case 2:
/*  824 */       return 2;
/*      */     case 3:
/*  825 */       return 3;
/*      */     case 4:
/*  826 */       return 4;
/*      */     case 5:
/*  827 */       return 4;
/*      */     case 6:
/*  828 */       return 4;
/*      */     case 7:
/*  829 */       return 6;
/*      */     case 8:
/*  830 */       return 7;
/*      */     case 9:
/*  831 */       return 8;
/*      */     case 11:
/*  832 */       return 9;
/*      */     case 12:
/*  833 */       return 10;
/*      */     case 13:
/*  834 */       return 11;
/*      */     case 14:
/*  835 */       return 5;
/*      */     case 10:
/*  836 */       return 12; }
/*  837 */     return tool - 2;
/*      */   }
/*      */ 
/*      */   int toolID(int index)
/*      */   {
/*  843 */     switch (index) { case 0:
/*  844 */       return 0;
/*      */     case 1:
/*  845 */       return 1;
/*      */     case 2:
/*  846 */       return 2;
/*      */     case 3:
/*  847 */       return 3;
/*      */     case 4:
/*  848 */       return this.lineType;
/*      */     case 5:
/*  849 */       return 14;
/*      */     case 6:
/*  850 */       return 7;
/*      */     case 7:
/*  851 */       return 8;
/*      */     case 8:
/*  852 */       return 9;
/*      */     case 9:
/*  853 */       return 11;
/*      */     case 10:
/*  854 */       return 12;
/*      */     case 11:
/*  855 */       return 13;
/*      */     case 12:
/*  856 */       return 10; }
/*  857 */     return index + 2;
/*      */   }
/*      */ 
/*      */   public void mousePressed(MouseEvent e)
/*      */   {
/*  862 */     int x = e.getX();
/*  863 */     int newTool = 0;
/*  864 */     for (int i = 0; i < 21; i++) {
/*  865 */       if ((x > i * 26) && (x < i * 26 + 26))
/*  866 */         newTool = toolID(i);
/*      */     }
/*  868 */     if (newTool == 22) {
/*  869 */       showSwitchPopupMenu(e);
/*  870 */       return;
/*      */     }
/*  872 */     if (!isValidTool(newTool)) return;
/*  873 */     if ((this.menus[newTool] != null) && (this.menus[newTool].getItemCount() > 0)) {
/*  874 */       this.menus[newTool].show(e.getComponent(), e.getX(), e.getY());
/*  875 */       return;
/*      */     }
/*  877 */     boolean doubleClick = (newTool == current) && (System.currentTimeMillis() - this.mouseDownTime <= 650L);
/*  878 */     this.mouseDownTime = System.currentTimeMillis();
/*  879 */     if (!doubleClick) {
/*  880 */       this.mpPrevious = current;
/*  881 */       if (isMacroTool(newTool)) {
/*  882 */         String name = this.names[newTool];
/*  883 */         if (name.indexOf("Unused Tool") != -1)
/*  884 */           return;
/*  885 */         if (name.indexOf("Action Tool") != -1) {
/*  886 */           if ((e.isPopupTrigger()) || (e.isMetaDown())) {
/*  887 */             name = name + " ";
/*  888 */             this.macroInstaller.runMacroTool(name + "Options");
/*      */           } else {
/*  890 */             drawTool(newTool, true);
/*  891 */             IJ.wait(50);
/*  892 */             drawTool(newTool, false);
/*  893 */             runMacroTool(newTool);
/*      */           }
/*  895 */           return;
/*      */         }
/*  897 */         name = name + " ";
/*  898 */         this.macroInstaller.runMacroTool(name + "Selected");
/*      */       }
/*      */ 
/*  901 */       setTool2(newTool);
/*  902 */       boolean isRightClick = (e.isPopupTrigger()) || (e.isMetaDown());
/*  903 */       if ((current == 0) && (isRightClick)) {
/*  904 */         this.rectItem.setState(!roundRectMode);
/*  905 */         this.roundRectItem.setState(roundRectMode);
/*  906 */         if (IJ.isMacOSX()) IJ.wait(10);
/*  907 */         this.rectPopup.show(e.getComponent(), x, this.y);
/*  908 */         this.mouseDownTime = 0L;
/*      */       }
/*  910 */       if ((current == 1) && (isRightClick)) {
/*  911 */         this.ovalItem.setState(ovalType == 0);
/*  912 */         this.ellipseItem.setState(ovalType == 1);
/*  913 */         this.brushItem.setState(ovalType == 2);
/*  914 */         if (IJ.isMacOSX()) IJ.wait(10);
/*  915 */         this.ovalPopup.show(e.getComponent(), x, this.y);
/*  916 */         this.mouseDownTime = 0L;
/*      */       }
/*  918 */       if ((current == 7) && (isRightClick)) {
/*  919 */         this.pointItem.setState(!multiPointMode);
/*  920 */         this.multiPointItem.setState(multiPointMode);
/*  921 */         if (IJ.isMacOSX()) IJ.wait(10);
/*  922 */         this.pointPopup.show(e.getComponent(), x, this.y);
/*  923 */         this.mouseDownTime = 0L;
/*      */       }
/*  925 */       if ((isLine(current)) && (isRightClick)) {
/*  926 */         this.straightLineItem.setState((this.lineType == 4) && (!arrowMode));
/*  927 */         this.polyLineItem.setState(this.lineType == 5);
/*  928 */         this.freeLineItem.setState(this.lineType == 6);
/*  929 */         this.arrowItem.setState((this.lineType == 4) && (arrowMode));
/*  930 */         if (IJ.isMacOSX()) IJ.wait(10);
/*  931 */         this.linePopup.show(e.getComponent(), x, this.y);
/*  932 */         this.mouseDownTime = 0L;
/*      */       }
/*  934 */       if ((isMacroTool(current)) && (isRightClick)) {
/*  935 */         String name = this.names[current] + " ";
/*  936 */         this.macroInstaller.runMacroTool(name + "Options");
/*      */       }
/*      */     } else {
/*  939 */       if (isMacroTool(current)) {
/*  940 */         String name = this.names[current] + " ";
/*  941 */         this.macroInstaller.runMacroTool(name + "Options");
/*  942 */         return;
/*      */       }
/*  944 */       ImagePlus imp = WindowManager.getCurrentImage();
/*  945 */       switch (current) {
/*      */       case 0:
/*  947 */         if (roundRectMode)
/*  948 */           IJ.doCommand("Rounded Rect Tool..."); break;
/*      */       case 1:
/*  951 */         showBrushDialog();
/*  952 */         break;
/*      */       case 11:
/*  954 */         if (imp != null) {
/*  955 */           ImageCanvas ic = imp.getCanvas();
/*  956 */           if (ic != null) ic.unzoom(); 
/*      */         }
/*  957 */         break;
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*  960 */         if ((current == 4) && (arrowMode))
/*  961 */           IJ.doCommand("Arrow Tool...");
/*      */         else
/*  963 */           IJ.runPlugIn("ij.plugin.frame.LineWidthAdjuster", "");
/*  964 */         break;
/*      */       case 14:
/*  966 */         showAngleDialog();
/*  967 */         break;
/*      */       case 7:
/*  969 */         if (multiPointMode) {
/*  970 */           if ((imp != null) && (imp.getRoi() != null))
/*  971 */             IJ.doCommand("Add Selection...");
/*      */         }
/*  973 */         else IJ.doCommand("Point Tool...");
/*  974 */         break;
/*      */       case 8:
/*  976 */         IJ.doCommand("Wand Tool...");
/*  977 */         break;
/*      */       case 9:
/*  979 */         IJ.doCommand("Fonts...");
/*  980 */         break;
/*      */       case 13:
/*  982 */         IJ.doCommand("Color Picker...");
/*  983 */         setTool2(this.mpPrevious);
/*  984 */         break;
/*      */       case 2:
/*      */       case 3:
/*      */       case 10:
/*      */       case 12: } 
/*      */     }
/*      */   }
/*  991 */   void showSwitchPopupMenu(MouseEvent e) { String path = IJ.getDirectory("macros") + "toolsets/";
/*  992 */     if (path == null) {
/*  993 */       return;
/*      */     }
/*  995 */     boolean applet = IJ.getApplet() != null;
/*  996 */     File f = new File(path);
/*      */     String[] list;
/*  998 */     if ((!applet) && (f.exists()) && (f.isDirectory())) { String[] list = f.list();
/* 1000 */       if (list != null);
/*      */     } else {
/* 1002 */       list = new String[0];
/* 1003 */     }boolean stackTools = false;
/* 1004 */     for (int i = 0; i < list.length; i++) {
/* 1005 */       if (list[i].equals("Stack Tools.txt")) {
/* 1006 */         stackTools = true;
/* 1007 */         break;
/*      */       }
/*      */     }
/* 1010 */     this.switchPopup.removeAll();
/* 1011 */     path = IJ.getDirectory("macros") + "StartupMacros.txt";
/* 1012 */     f = new File(path);
/* 1013 */     if ((!applet) && (f.exists()))
/* 1014 */       addItem("Startup Macros");
/*      */     else
/* 1016 */       addItem("StartupMacros*");
/* 1017 */     if (!stackTools) addItem("Stack Tools*");
/* 1018 */     for (int i = 0; i < list.length; i++) {
/* 1019 */       String name = list[i];
/* 1020 */       if (name.endsWith(".txt")) {
/* 1021 */         name = name.substring(0, name.length() - 4);
/* 1022 */         addItem(name);
/* 1023 */       } else if (name.endsWith(".ijm")) {
/* 1024 */         name = name.substring(0, name.length() - 4) + " ";
/* 1025 */         addItem(name);
/*      */       }
/*      */     }
/* 1028 */     addItem("Help...");
/* 1029 */     add(this.ovalPopup);
/* 1030 */     if (IJ.isMacOSX()) IJ.wait(10);
/* 1031 */     this.switchPopup.show(e.getComponent(), e.getX(), e.getY()); }
/*      */ 
/*      */   void addItem(String name)
/*      */   {
/* 1035 */     CheckboxMenuItem item = new CheckboxMenuItem(name, name.equals(this.currentSet));
/* 1036 */     item.addItemListener(this);
/* 1037 */     this.switchPopup.add(item);
/*      */   }
/*      */ 
/*      */   void drawTool(int tool, boolean drawDown) {
/* 1041 */     this.down[tool] = drawDown;
/* 1042 */     Graphics g = getGraphics();
/* 1043 */     if ((!drawDown) && (Prefs.antialiasedTools)) {
/* 1044 */       Graphics2D g2d = (Graphics2D)g;
/* 1045 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*      */     }
/* 1047 */     drawButton(g, tool);
/* 1048 */     if (null == g) return;
/* 1049 */     g.dispose();
/*      */   }
/*      */ 
/*      */   boolean isLine(int tool) {
/* 1053 */     return (tool == 4) || (tool == 5) || (tool == 6);
/*      */   }
/*      */ 
/*      */   public void restorePreviousTool() {
/* 1057 */     setTool2(this.mpPrevious);
/*      */   }
/*      */ 
/*      */   boolean isMacroTool(int tool) {
/* 1061 */     return (tool >= 10) && (tool <= 22) && (this.names[tool] != null) && (this.macroInstaller != null);
/*      */   }
/*      */   public void mouseReleased(MouseEvent e) {
/*      */   }
/*      */   public void mouseExited(MouseEvent e) {
/*      */   }
/*      */   public void mouseClicked(MouseEvent e) {
/*      */   }
/*      */   public void mouseEntered(MouseEvent e) {  } 
/*      */   public void mouseDragged(MouseEvent e) {  } 
/* 1071 */   public void itemStateChanged(ItemEvent e) { CheckboxMenuItem item = (CheckboxMenuItem)e.getSource();
/* 1072 */     String previousName = getToolName();
/* 1073 */     if ((item == this.rectItem) || (item == this.roundRectItem)) {
/* 1074 */       roundRectMode = item == this.roundRectItem;
/* 1075 */       repaintTool(0);
/* 1076 */       showMessage(0);
/* 1077 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 1078 */       Roi roi = imp != null ? imp.getRoi() : null;
/* 1079 */       if ((roi != null) && (roi.getType() == 0))
/* 1080 */         roi.setCornerDiameter(roundRectMode ? arcSize : 0);
/* 1081 */       if (!previousName.equals(getToolName()))
/* 1082 */         IJ.notifyEventListeners(4);
/* 1083 */     } else if ((item == this.ovalItem) || (item == this.ellipseItem) || (item == this.brushItem)) {
/* 1084 */       if (item == this.brushItem)
/* 1085 */         ovalType = 2;
/* 1086 */       else if (item == this.ellipseItem)
/* 1087 */         ovalType = 1;
/*      */       else
/* 1089 */         ovalType = 0;
/* 1090 */       repaintTool(1);
/* 1091 */       showMessage(1);
/* 1092 */       if (!previousName.equals(getToolName()))
/* 1093 */         IJ.notifyEventListeners(4);
/* 1094 */     } else if ((item == this.pointItem) || (item == this.multiPointItem)) {
/* 1095 */       multiPointMode = item == this.multiPointItem;
/* 1096 */       Prefs.multiPointMode = multiPointMode;
/* 1097 */       repaintTool(7);
/* 1098 */       showMessage(7);
/* 1099 */       if (!previousName.equals(getToolName()))
/* 1100 */         IJ.notifyEventListeners(4);
/* 1101 */     } else if (item == this.straightLineItem) {
/* 1102 */       this.lineType = 4;
/* 1103 */       arrowMode = false;
/* 1104 */       setTool2(4);
/* 1105 */       showMessage(4);
/* 1106 */     } else if (item == this.polyLineItem) {
/* 1107 */       this.lineType = 5;
/* 1108 */       setTool2(5);
/* 1109 */       showMessage(5);
/* 1110 */     } else if (item == this.freeLineItem) {
/* 1111 */       this.lineType = 6;
/* 1112 */       setTool2(6);
/* 1113 */       showMessage(6);
/* 1114 */     } else if (item == this.arrowItem) {
/* 1115 */       this.lineType = 4;
/* 1116 */       arrowMode = true;
/* 1117 */       setTool2(4);
/* 1118 */       showMessage(4);
/*      */     } else {
/* 1120 */       String label = item.getActionCommand();
/* 1121 */       if (!label.equals("Help...")) this.currentSet = label;
/*      */ 
/* 1123 */       if (label.equals("Help...")) {
/* 1124 */         IJ.showMessage("Tool Switcher", "Use this drop down menu to switch to macro tool\nsets located in the ImageJ/macros/toolsets folder,\nor to revert to the ImageJ/macros/StartupMacros\nset. The default tool sets, which have names\nending in '*', are loaded from ij.jar.\n \nHold the shift key down while selecting a tool\nset to view its source code.\n \nSeveral example tool sets are available at\n<http://imagej.nih.gov/ij/macros/toolsets/>.");
/*      */ 
/* 1137 */         return;
/* 1138 */       }if (label.endsWith("*"))
/*      */       {
/* 1140 */         MacroInstaller mi = new MacroInstaller();
/* 1141 */         label = label.substring(0, label.length() - 1) + ".txt";
/* 1142 */         String path = "/macros/" + label;
/* 1143 */         if (IJ.shiftKeyDown()) {
/* 1144 */           String macros = mi.openFromIJJar(path);
/* 1145 */           Editor ed = new Editor();
/* 1146 */           ed.setSize(350, 300);
/* 1147 */           ed.create(label, macros);
/* 1148 */           IJ.setKeyUp(16);
/*      */         } else {
/* 1150 */           mi.installFromIJJar(path);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*      */         String path;
/*      */         String path;
/* 1153 */         if (label.equals("Startup Macros")) {
/* 1154 */           path = IJ.getDirectory("macros") + "StartupMacros.txt";
/*      */         }
/*      */         else
/*      */         {
/*      */           String path;
/* 1155 */           if (label.endsWith(" "))
/* 1156 */             path = IJ.getDirectory("macros") + "toolsets/" + label.substring(0, label.length() - 1) + ".ijm";
/*      */           else
/* 1158 */             path = IJ.getDirectory("macros") + "toolsets/" + label + ".txt"; 
/*      */         }
/*      */         try { if (IJ.shiftKeyDown()) {
/* 1161 */             IJ.open(path);
/* 1162 */             IJ.setKeyUp(16);
/*      */           } else {
/* 1164 */             new MacroInstaller().run(path);
/*      */           }
/*      */         } catch (Exception ex) {
/*      */         }
/*      */       }
/*      */     } }
/*      */ 
/*      */   public void actionPerformed(ActionEvent e) {
/* 1172 */     MenuItem item = (MenuItem)e.getSource();
/* 1173 */     String cmd = e.getActionCommand();
/* 1174 */     PopupMenu popup = (PopupMenu)item.getParent();
/* 1175 */     int tool = -1;
/* 1176 */     for (int i = 10; i < 23; i++) {
/* 1177 */       if (popup == this.menus[i]) {
/* 1178 */         tool = i;
/* 1179 */         break;
/*      */       }
/*      */     }
/* 1182 */     if (tool == -1) return;
/* 1183 */     if (this.macroInstaller != null)
/* 1184 */       this.macroInstaller.runMenuTool(this.names[tool], cmd);
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize() {
/* 1188 */     return this.ps;
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize() {
/* 1192 */     return this.ps;
/*      */   }
/*      */ 
/*      */   public void mouseMoved(MouseEvent e) {
/* 1196 */     int x = e.getX();
/* 1197 */     x = toolID(x / 26);
/* 1198 */     showMessage(x);
/*      */   }
/*      */ 
/*      */   public int addTool(String toolTip)
/*      */   {
/* 1206 */     int index = toolTip.indexOf('-');
/* 1207 */     boolean hasIcon = (index >= 0) && (toolTip.length() - index > 4);
/* 1208 */     int tool = -1;
/* 1209 */     if (this.names[10] == null)
/* 1210 */       tool = 10;
/* 1211 */     if (tool == -1) {
/* 1212 */       for (int i = 15; i <= 21; i++) {
/* 1213 */         if (this.names[i] == null) {
/* 1214 */           tool = i;
/* 1215 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1219 */     if (tool == -1) return -1;
/* 1220 */     if (hasIcon) {
/* 1221 */       this.icons[tool] = toolTip.substring(index + 1);
/* 1222 */       if ((index > 0) && (toolTip.charAt(index - 1) == ' '))
/* 1223 */         this.names[tool] = toolTip.substring(0, index - 1);
/*      */       else
/* 1225 */         this.names[tool] = toolTip.substring(0, index);
/*      */     } else {
/* 1227 */       if (toolTip.endsWith("-"))
/* 1228 */         toolTip = toolTip.substring(0, toolTip.length() - 1);
/* 1229 */       else if (toolTip.endsWith("- "))
/* 1230 */         toolTip = toolTip.substring(0, toolTip.length() - 2);
/* 1231 */       this.names[tool] = toolTip;
/*      */     }
/* 1233 */     if ((tool == current) && ((this.names[tool].indexOf("Action Tool") != -1) || (this.names[tool].indexOf("Unused Tool") != -1)))
/* 1234 */       setTool(0);
/* 1235 */     if (this.names[tool].endsWith(" Menu Tool"))
/* 1236 */       installMenu(tool);
/* 1237 */     return tool;
/*      */   }
/*      */ 
/*      */   void installMenu(int tool) {
/* 1241 */     Program pgm = this.macroInstaller.getProgram();
/* 1242 */     Hashtable h = pgm.getMenus();
/* 1243 */     if (h == null) return;
/* 1244 */     String[] commands = (String[])h.get(this.names[tool]);
/* 1245 */     if (commands == null) return;
/* 1246 */     if (this.menus[tool] == null) {
/* 1247 */       this.menus[tool] = new PopupMenu("");
/* 1248 */       if (Menus.getFontSize() != 0)
/* 1249 */         this.menus[tool].setFont(Menus.getFont());
/* 1250 */       add(this.menus[tool]);
/*      */     } else {
/* 1252 */       this.menus[tool].removeAll();
/* 1253 */     }for (int i = 0; i < commands.length; i++) {
/* 1254 */       if (commands[i].equals("-")) {
/* 1255 */         this.menus[tool].addSeparator();
/*      */       } else {
/* 1257 */         MenuItem mi = new MenuItem(commands[i]);
/* 1258 */         mi.addActionListener(this);
/* 1259 */         this.menus[tool].add(mi);
/*      */       }
/*      */     }
/* 1262 */     if (tool == current) setTool(0);
/*      */   }
/*      */ 
/*      */   public void addMacroTool(String name, MacroInstaller macroInstaller, int id)
/*      */   {
/* 1267 */     if (id == 0) {
/* 1268 */       for (int i = 10; i < 22; i++) {
/* 1269 */         this.names[i] = null;
/* 1270 */         this.icons[i] = null;
/* 1271 */         if (this.menus[i] != null) this.menus[i].removeAll();
/*      */       }
/*      */     }
/* 1274 */     this.macroInstaller = macroInstaller;
/* 1275 */     addTool(name);
/*      */   }
/*      */ 
/*      */   void runMacroTool(int id) {
/* 1279 */     if (this.macroInstaller != null)
/* 1280 */       this.macroInstaller.runMacroTool(this.names[id]);
/*      */   }
/*      */ 
/*      */   void showBrushDialog() {
/* 1284 */     GenericDialog gd = new GenericDialog("Selection Brush");
/* 1285 */     gd.addCheckbox("Enable selection brush", ovalType == 2);
/* 1286 */     gd.addNumericField("           Size:", brushSize, 0, 4, "pixels");
/* 1287 */     gd.showDialog();
/* 1288 */     if (gd.wasCanceled()) return;
/* 1289 */     if (gd.getNextBoolean())
/* 1290 */       ovalType = 2;
/* 1291 */     brushSize = (int)gd.getNextNumber();
/* 1292 */     if (brushSize < 1) brushSize = 1;
/* 1293 */     repaintTool(1);
/* 1294 */     ImagePlus img = WindowManager.getCurrentImage();
/* 1295 */     Roi roi = img != null ? img.getRoi() : null;
/* 1296 */     if ((roi != null) && (roi.getType() == 1) && (ovalType == 2))
/* 1297 */       img.killRoi();
/* 1298 */     Prefs.set("toolbar.brush.size", brushSize);
/*      */   }
/*      */ 
/*      */   void showAngleDialog() {
/* 1302 */     GenericDialog gd = new GenericDialog("Angle Tool");
/* 1303 */     gd.addCheckbox("Measure reflex angle", Prefs.reflexAngle);
/* 1304 */     gd.showDialog();
/* 1305 */     if (!gd.wasCanceled())
/* 1306 */       Prefs.reflexAngle = gd.getNextBoolean();
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.Toolbar
 * JD-Core Version:    0.6.2
 */