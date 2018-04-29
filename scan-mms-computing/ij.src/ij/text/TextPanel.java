/*     */ package ij.text;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.Menus;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.SaveDialog;
/*     */ import ij.measure.ResultsTable;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.util.Tools;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.Panel;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.datatransfer.Clipboard;
/*     */ import java.awt.datatransfer.ClipboardOwner;
/*     */ import java.awt.datatransfer.StringSelection;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.AdjustmentListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.awt.event.MouseWheelListener;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class TextPanel extends Panel
/*     */   implements AdjustmentListener, MouseListener, MouseMotionListener, KeyListener, ClipboardOwner, ActionListener, MouseWheelListener, Runnable
/*     */ {
/*     */   static final int DOUBLE_CLICK_THRESHOLD = 650;
/*     */   int iGridWidth;
/*     */   int iGridHeight;
/*     */   int iX;
/*     */   int iY;
/*     */   String[] sColHead;
/*     */   Vector vData;
/*     */   int[] iColWidth;
/*     */   int iColCount;
/*     */   int iRowCount;
/*     */   int iRowHeight;
/*     */   int iFirstRow;
/*     */   Scrollbar sbHoriz;
/*     */   Scrollbar sbVert;
/*     */   int iSbWidth;
/*     */   int iSbHeight;
/*     */   boolean bDrag;
/*     */   int iXDrag;
/*     */   int iColDrag;
/*  43 */   boolean headings = true;
/*  44 */   String title = "";
/*     */   String labels;
/*     */   KeyListener keyListener;
/*  47 */   Cursor resizeCursor = new Cursor(11);
/*  48 */   Cursor defaultCursor = new Cursor(0);
/*  49 */   int selStart = -1; int selEnd = -1; int selOrigin = -1; int selLine = -1;
/*     */   TextCanvas tc;
/*     */   PopupMenu pm;
/*     */   boolean columnsManuallyAdjusted;
/*     */   long mouseDownTime;
/*     */   String filePath;
/*     */   ResultsTable rt;
/*     */   boolean unsavedLines;
/*     */ 
/*     */   public TextPanel()
/*     */   {
/*  61 */     this.tc = new TextCanvas(this);
/*  62 */     setLayout(new BorderLayout());
/*  63 */     add("Center", this.tc);
/*  64 */     this.sbHoriz = new Scrollbar(0);
/*  65 */     this.sbHoriz.addAdjustmentListener(this);
/*  66 */     this.sbHoriz.setFocusable(false);
/*  67 */     add("South", this.sbHoriz);
/*  68 */     this.sbVert = new Scrollbar(1);
/*  69 */     this.sbVert.addAdjustmentListener(this);
/*  70 */     this.sbVert.setFocusable(false);
/*  71 */     ImageJ ij = IJ.getInstance();
/*  72 */     if (ij != null) {
/*  73 */       this.sbHoriz.addKeyListener(ij);
/*  74 */       this.sbVert.addKeyListener(ij);
/*     */     }
/*  76 */     add("East", this.sbVert);
/*  77 */     addPopupMenu();
/*     */   }
/*     */ 
/*     */   public TextPanel(String title)
/*     */   {
/*  82 */     this();
/*  83 */     if (title.equals("Results")) {
/*  84 */       this.pm.addSeparator();
/*  85 */       addPopupItem("Clear Results");
/*  86 */       addPopupItem("Summarize");
/*  87 */       addPopupItem("Distribution...");
/*  88 */       addPopupItem("Set Measurements...");
/*  89 */       addPopupItem("Rename...");
/*  90 */       addPopupItem("Duplicate...");
/*     */     }
/*     */   }
/*     */ 
/*     */   void addPopupMenu() {
/*  95 */     this.pm = new PopupMenu();
/*  96 */     addPopupItem("Save As...");
/*  97 */     this.pm.addSeparator();
/*  98 */     addPopupItem("Cut");
/*  99 */     addPopupItem("Copy");
/* 100 */     addPopupItem("Clear");
/* 101 */     addPopupItem("Select All");
/* 102 */     add(this.pm);
/*     */   }
/*     */ 
/*     */   void addPopupItem(String s) {
/* 106 */     MenuItem mi = new MenuItem(s);
/* 107 */     mi.addActionListener(this);
/* 108 */     this.pm.add(mi);
/*     */   }
/*     */ 
/*     */   public synchronized void setColumnHeadings(String labels)
/*     */   {
/* 117 */     boolean sameLabels = labels.equals(this.labels);
/* 118 */     this.labels = labels;
/* 119 */     if (labels.equals("")) {
/* 120 */       this.iColCount = 1;
/* 121 */       this.sColHead = new String[1];
/* 122 */       this.sColHead[0] = "";
/*     */     } else {
/* 124 */       if (labels.endsWith("\t"))
/* 125 */         this.labels = labels.substring(0, labels.length() - 1);
/* 126 */       this.sColHead = Tools.split(this.labels, "\t");
/* 127 */       this.iColCount = this.sColHead.length;
/*     */     }
/* 129 */     flush();
/* 130 */     this.vData = new Vector();
/* 131 */     if ((this.iColWidth == null) || (this.iColWidth.length != this.iColCount) || (!sameLabels) || (this.iColCount == 1)) {
/* 132 */       this.iColWidth = new int[this.iColCount];
/* 133 */       this.columnsManuallyAdjusted = false;
/*     */     }
/* 135 */     this.iRowCount = 0;
/* 136 */     resetSelection();
/* 137 */     adjustHScroll();
/* 138 */     this.tc.repaint();
/*     */   }
/*     */ 
/*     */   public String getColumnHeadings()
/*     */   {
/* 143 */     return this.labels == null ? "" : this.labels;
/*     */   }
/*     */ 
/*     */   public synchronized void updateColumnHeadings(String labels) {
/* 147 */     this.labels = labels;
/* 148 */     if (labels.equals("")) {
/* 149 */       this.iColCount = 1;
/* 150 */       this.sColHead = new String[1];
/* 151 */       this.sColHead[0] = "";
/*     */     } else {
/* 153 */       if (labels.endsWith("\t"))
/* 154 */         this.labels = labels.substring(0, labels.length() - 1);
/* 155 */       this.sColHead = Tools.split(this.labels, "\t");
/* 156 */       this.iColCount = this.sColHead.length;
/* 157 */       this.iColWidth = new int[this.iColCount];
/* 158 */       this.columnsManuallyAdjusted = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFont(Font font, boolean antialiased) {
/* 163 */     this.tc.fFont = font;
/* 164 */     this.tc.iImage = null;
/* 165 */     this.tc.fMetrics = null;
/* 166 */     this.tc.antialiased = antialiased;
/* 167 */     this.iColWidth[0] = 0;
/* 168 */     if (isShowing()) updateDisplay();
/*     */   }
/*     */ 
/*     */   public void appendLine(String data)
/*     */   {
/* 173 */     if (this.vData == null)
/* 174 */       setColumnHeadings("");
/* 175 */     char[] chars = data.toCharArray();
/* 176 */     this.vData.addElement(chars);
/* 177 */     this.iRowCount += 1;
/* 178 */     if (isShowing()) {
/* 179 */       if ((this.iColCount == 1) && (this.tc.fMetrics != null)) {
/* 180 */         this.iColWidth[0] = Math.max(this.iColWidth[0], this.tc.fMetrics.charsWidth(chars, 0, chars.length));
/* 181 */         adjustHScroll();
/*     */       }
/* 183 */       updateDisplay();
/* 184 */       this.unsavedLines = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(String data)
/*     */   {
/* 190 */     if (data == null) data = "null";
/* 191 */     if (this.vData == null)
/* 192 */       setColumnHeadings("");
/*     */     while (true) {
/* 194 */       int p = data.indexOf('\n');
/* 195 */       if (p < 0) {
/* 196 */         appendWithoutUpdate(data);
/*     */       }
/*     */       else {
/* 199 */         appendWithoutUpdate(data.substring(0, p));
/* 200 */         data = data.substring(p + 1);
/* 201 */         if (data.equals("")) break;
/*     */       }
/*     */     }
/* 204 */     if (isShowing()) {
/* 205 */       updateDisplay();
/* 206 */       this.unsavedLines = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(ArrayList list)
/*     */   {
/* 212 */     if (list == null) return;
/* 213 */     if (this.vData == null) setColumnHeadings("");
/* 214 */     for (int i = 0; i < list.size(); i++)
/* 215 */       appendWithoutUpdate((String)list.get(i));
/* 216 */     if (isShowing()) {
/* 217 */       updateDisplay();
/* 218 */       this.unsavedLines = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void appendWithoutUpdate(String data)
/*     */   {
/* 224 */     if (this.vData != null) {
/* 225 */       char[] chars = data.toCharArray();
/* 226 */       this.vData.addElement(chars);
/* 227 */       this.iRowCount += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateDisplay() {
/* 232 */     this.iY = (this.iRowHeight * (this.iRowCount + 1));
/* 233 */     adjustVScroll();
/* 234 */     if ((this.iColCount > 1) && (this.iRowCount <= 10) && (!this.columnsManuallyAdjusted))
/* 235 */       this.iColWidth[0] = 0;
/* 236 */     this.tc.repaint();
/*     */   }
/*     */ 
/*     */   String getCell(int column, int row) {
/* 240 */     if ((column < 0) || (column >= this.iColCount) || (row < 0) || (row >= this.iRowCount))
/* 241 */       return null;
/* 242 */     return new String(this.tc.getChars(column, row));
/*     */   }
/*     */ 
/*     */   synchronized void adjustVScroll() {
/* 246 */     if (this.iRowHeight == 0) return;
/* 247 */     Dimension d = this.tc.getSize();
/* 248 */     int value = this.iY / this.iRowHeight;
/* 249 */     int visible = d.height / this.iRowHeight;
/* 250 */     int maximum = this.iRowCount + 1;
/* 251 */     if (visible < 0) visible = 0;
/* 252 */     if (visible > maximum) visible = maximum;
/* 253 */     if (value > maximum - visible) value = maximum - visible;
/* 254 */     this.sbVert.setValues(value, visible, 0, maximum);
/* 255 */     this.iY = (this.iRowHeight * value);
/*     */   }
/*     */ 
/*     */   synchronized void adjustHScroll() {
/* 259 */     if (this.iRowHeight == 0) return;
/* 260 */     Dimension d = this.tc.getSize();
/* 261 */     int w = 0;
/* 262 */     for (int i = 0; i < this.iColCount; i++)
/* 263 */       w += this.iColWidth[i];
/* 264 */     this.iGridWidth = w;
/* 265 */     this.sbHoriz.setValues(this.iX, d.width, 0, this.iGridWidth);
/* 266 */     this.iX = this.sbHoriz.getValue();
/*     */   }
/*     */ 
/*     */   public void adjustmentValueChanged(AdjustmentEvent e) {
/* 270 */     this.iX = this.sbHoriz.getValue();
/* 271 */     this.iY = (this.iRowHeight * this.sbVert.getValue());
/* 272 */     this.tc.repaint();
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e) {
/* 276 */     int x = e.getX(); int y = e.getY();
/* 277 */     if ((e.isPopupTrigger()) || (e.isMetaDown())) {
/* 278 */       this.pm.show(e.getComponent(), x, y);
/* 279 */     } else if (e.isShiftDown()) {
/* 280 */       extendSelection(x, y);
/*     */     } else {
/* 282 */       select(x, y);
/* 283 */       handleDoubleClick();
/*     */     }
/*     */   }
/*     */ 
/*     */   void handleDoubleClick() {
/* 288 */     if ((this.selStart < 0) || (this.selStart != this.selEnd) || (this.iColCount != 1)) return;
/* 289 */     boolean doubleClick = System.currentTimeMillis() - this.mouseDownTime <= 650L;
/* 290 */     this.mouseDownTime = System.currentTimeMillis();
/* 291 */     if (doubleClick) {
/* 292 */       char[] chars = (char[])this.vData.elementAt(this.selStart);
/* 293 */       String s = new String(chars);
/* 294 */       int index = s.indexOf(": ");
/* 295 */       if ((index > -1) && (!s.endsWith(": ")))
/* 296 */         s = s.substring(index + 2);
/* 297 */       if ((s.indexOf(File.separator) != -1) || (s.indexOf(".") != -1)) {
/* 298 */         this.filePath = s;
/* 299 */         Thread thread = new Thread(this, "Open");
/* 300 */         thread.setPriority(thread.getPriority() - 1);
/* 301 */         thread.start();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 309 */     if (this.filePath != null) IJ.open(this.filePath); 
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e)
/*     */   {
/* 313 */     if (this.bDrag) {
/* 314 */       setCursor(this.defaultCursor);
/* 315 */       this.bDrag = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent e) {
/* 320 */     int x = e.getX(); int y = e.getY();
/* 321 */     if (y <= this.iRowHeight) {
/* 322 */       int xb = x;
/* 323 */       x = x + this.iX - this.iGridWidth;
/* 324 */       for (int i = this.iColCount - 1; 
/* 325 */         (i >= 0) && (
/* 326 */         (x <= -7) || (x >= 7)); i--)
/*     */       {
/* 327 */         x += this.iColWidth[i];
/*     */       }
/* 329 */       if (i >= 0) {
/* 330 */         if (!this.bDrag) {
/* 331 */           setCursor(this.resizeCursor);
/* 332 */           this.bDrag = true;
/* 333 */           this.iXDrag = (xb - this.iColWidth[i]);
/* 334 */           this.iColDrag = i;
/*     */         }
/* 336 */         return;
/*     */       }
/*     */     }
/* 339 */     if (this.bDrag) {
/* 340 */       setCursor(this.defaultCursor);
/* 341 */       this.bDrag = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e) {
/* 346 */     if ((e.isPopupTrigger()) || (e.isMetaDown()))
/* 347 */       return;
/* 348 */     int x = e.getX(); int y = e.getY();
/* 349 */     if ((this.bDrag) && (x < this.tc.getSize().width)) {
/* 350 */       int w = x - this.iXDrag;
/* 351 */       if (w < 0) w = 0;
/* 352 */       this.iColWidth[this.iColDrag] = w;
/* 353 */       this.columnsManuallyAdjusted = true;
/* 354 */       adjustHScroll();
/* 355 */       this.tc.repaint();
/*     */     } else {
/* 357 */       extendSelection(x, y);
/*     */     }
/*     */   }
/*     */   public void mouseReleased(MouseEvent e) {
/*     */   }
/*     */   public void mouseClicked(MouseEvent e) {
/*     */   }
/*     */   public void mouseEntered(MouseEvent e) {
/*     */   }
/* 366 */   public void mouseWheelMoved(MouseWheelEvent event) { synchronized (this) {
/* 367 */       int rot = event.getWheelRotation();
/* 368 */       this.sbVert.setValue(this.sbVert.getValue() + rot);
/* 369 */       this.iY = (this.iRowHeight * this.sbVert.getValue());
/* 370 */       this.tc.repaint();
/*     */     } }
/*     */ 
/*     */   private void scroll(int inc)
/*     */   {
/* 375 */     synchronized (this) {
/* 376 */       this.sbVert.setValue(this.sbVert.getValue() + inc);
/* 377 */       this.iY = (this.iRowHeight * this.sbVert.getValue());
/* 378 */       this.tc.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addKeyListener(KeyListener listener)
/*     */   {
/* 384 */     this.keyListener = listener;
/*     */   }
/*     */ 
/*     */   public void addMouseListener(MouseListener listener) {
/* 388 */     this.tc.addMouseListener(listener);
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e) {
/* 392 */     int key = e.getKeyCode();
/* 393 */     if (key == 8)
/* 394 */       clearSelection();
/* 395 */     else if (key == 38)
/* 396 */       scroll(-1);
/* 397 */     else if (key == 40)
/* 398 */       scroll(1);
/* 399 */     else if ((this.keyListener != null) && (key != 83) && (key != 67) && (key != 88) && (key != 65))
/* 400 */       this.keyListener.keyPressed(e); 
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent e) {
/*     */   }
/*     */ 
/* 406 */   public void keyTyped(KeyEvent e) { if (this.keyListener != null)
/* 407 */       this.keyListener.keyTyped(e); }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 411 */     String cmd = e.getActionCommand();
/* 412 */     doCommand(cmd);
/*     */   }
/*     */ 
/*     */   void doCommand(String cmd) {
/* 416 */     if (cmd == null)
/* 417 */       return;
/* 418 */     if (cmd.equals("Save As..."))
/* 419 */       saveAs("");
/* 420 */     else if (cmd.equals("Cut"))
/* 421 */       cutSelection();
/* 422 */     else if (cmd.equals("Copy"))
/* 423 */       copySelection();
/* 424 */     else if (cmd.equals("Clear"))
/* 425 */       clearSelection();
/* 426 */     else if (cmd.equals("Select All"))
/* 427 */       selectAll();
/* 428 */     else if (cmd.equals("Rename..."))
/* 429 */       rename(null);
/* 430 */     else if (cmd.equals("Duplicate..."))
/* 431 */       duplicate();
/* 432 */     else if (cmd.equals("Summarize"))
/* 433 */       IJ.doCommand("Summarize");
/* 434 */     else if (cmd.equals("Distribution..."))
/* 435 */       IJ.doCommand("Distribution...");
/* 436 */     else if (cmd.equals("Clear Results"))
/* 437 */       IJ.doCommand("Clear Results");
/* 438 */     else if (cmd.equals("Set Measurements..."))
/* 439 */       IJ.doCommand("Set Measurements...");
/* 440 */     else if (cmd.equals("Options..."))
/* 441 */       IJ.doCommand("Input/Output..."); 
/*     */   }
/*     */ 
/*     */   public void lostOwnership(Clipboard clip, Transferable cont) {
/*     */   }
/*     */ 
/* 447 */   void rename(String title2) { if (this.rt == null) return;
/* 448 */     if ((title2 != null) && (title2.equals("")))
/* 449 */       title2 = null;
/* 450 */     Component comp = getParent();
/* 451 */     if ((comp == null) || (!(comp instanceof TextWindow)))
/* 452 */       return;
/* 453 */     TextWindow tw = (TextWindow)comp;
/* 454 */     if (title2 == null) {
/* 455 */       GenericDialog gd = new GenericDialog("Rename", tw);
/* 456 */       gd.addStringField("Title:", "Results2", 20);
/* 457 */       gd.showDialog();
/* 458 */       if (gd.wasCanceled())
/* 459 */         return;
/* 460 */       title2 = gd.getNextString();
/*     */     }
/* 462 */     String title1 = this.title;
/* 463 */     if ((this.title != null) && (this.title.equals("Results"))) {
/* 464 */       IJ.setTextPanel(null);
/* 465 */       Analyzer.setUnsavedMeasurements(false);
/* 466 */       Analyzer.setResultsTable(null);
/* 467 */       Analyzer.resetCounter();
/*     */     }
/* 469 */     if (title2.equals("Results"))
/*     */     {
/* 471 */       tw.dispose();
/* 472 */       WindowManager.removeWindow(tw);
/* 473 */       flush();
/* 474 */       this.rt.show("Results");
/*     */     } else {
/* 476 */       tw.setTitle(title2);
/* 477 */       int mbSize = tw.mb != null ? tw.mb.getMenuCount() : 0;
/* 478 */       if ((mbSize > 0) && (tw.mb.getMenu(mbSize - 1).getLabel().equals("Results")))
/* 479 */         tw.mb.remove(mbSize - 1);
/* 480 */       this.title = title2;
/*     */     }
/* 482 */     Menus.updateWindowMenuItem(title1, title2);
/* 483 */     if (Recorder.record)
/* 484 */       Recorder.recordString("IJ.renameResults(\"" + title2 + "\");\n"); }
/*     */ 
/*     */   void duplicate()
/*     */   {
/* 488 */     if (this.rt == null) return;
/* 489 */     ResultsTable rt2 = (ResultsTable)this.rt.clone();
/* 490 */     String title2 = IJ.getString("Title:", "Results2");
/* 491 */     if (!title2.equals("")) {
/* 492 */       if (title2.equals("Results")) title2 = "Results2";
/* 493 */       rt2.show(title2);
/*     */     }
/*     */   }
/*     */ 
/*     */   void select(int x, int y) {
/* 498 */     Dimension d = this.tc.getSize();
/* 499 */     if ((this.iRowHeight == 0) || (x > d.width) || (y > d.height))
/* 500 */       return;
/* 501 */     int r = y / this.iRowHeight - 1 + this.iFirstRow;
/* 502 */     int lineWidth = this.iGridWidth;
/* 503 */     if ((this.iColCount == 1) && (this.tc.fMetrics != null) && (r >= 0) && (r < this.iRowCount)) {
/* 504 */       char[] chars = (char[])this.vData.elementAt(r);
/* 505 */       lineWidth = Math.max(this.tc.fMetrics.charsWidth(chars, 0, chars.length), this.iGridWidth);
/*     */     }
/* 507 */     if ((r >= 0) && (r < this.iRowCount) && (x < lineWidth)) {
/* 508 */       this.selOrigin = r;
/* 509 */       this.selStart = r;
/* 510 */       this.selEnd = r;
/*     */     } else {
/* 512 */       resetSelection();
/* 513 */       this.selOrigin = r;
/* 514 */       if (r >= this.iRowCount) {
/* 515 */         this.selOrigin = (this.iRowCount - 1);
/*     */       }
/*     */     }
/* 518 */     this.tc.repaint();
/* 519 */     this.selLine = r;
/*     */   }
/*     */ 
/*     */   void extendSelection(int x, int y) {
/* 523 */     Dimension d = this.tc.getSize();
/* 524 */     if ((this.iRowHeight == 0) || (x > d.width) || (y > d.height))
/* 525 */       return;
/* 526 */     int r = y / this.iRowHeight - 1 + this.iFirstRow;
/*     */ 
/* 528 */     if ((r >= 0) && (r < this.iRowCount)) {
/* 529 */       if (r < this.selOrigin) {
/* 530 */         this.selStart = r;
/* 531 */         this.selEnd = this.selOrigin;
/*     */       }
/*     */       else {
/* 534 */         this.selStart = this.selOrigin;
/* 535 */         this.selEnd = r;
/*     */       }
/*     */     }
/* 538 */     this.tc.repaint();
/* 539 */     this.selLine = r;
/*     */   }
/*     */ 
/*     */   public int rowIndex(int y)
/*     */   {
/* 544 */     if (y > this.tc.getSize().height) {
/* 545 */       return -1;
/*     */     }
/* 547 */     return y / this.iRowHeight - 1 + this.iFirstRow;
/*     */   }
/*     */ 
/*     */   public int copySelection()
/*     */   {
/* 555 */     if ((Recorder.record) && (this.title.equals("Results")))
/* 556 */       Recorder.record("String.copyResults");
/* 557 */     if ((this.selStart == -1) || (this.selEnd == -1))
/* 558 */       return copyAll();
/* 559 */     StringBuffer sb = new StringBuffer();
/* 560 */     if ((Prefs.copyColumnHeaders) && (this.labels != null) && (!this.labels.equals("")) && (this.selStart == 0) && (this.selEnd == this.iRowCount - 1)) {
/* 561 */       if (Prefs.noRowNumbers) {
/* 562 */         String s = this.labels;
/* 563 */         int index = s.indexOf("\t");
/* 564 */         if (index != -1)
/* 565 */           s = s.substring(index + 1, s.length());
/* 566 */         sb.append(s);
/*     */       } else {
/* 568 */         sb.append(this.labels);
/* 569 */       }sb.append('\n');
/*     */     }
/* 571 */     for (int i = this.selStart; i <= this.selEnd; i++) {
/* 572 */       char[] chars = (char[])this.vData.elementAt(i);
/* 573 */       String s = new String(chars);
/* 574 */       if (s.endsWith("\t"))
/* 575 */         s = s.substring(0, s.length() - 1);
/* 576 */       if (Prefs.noRowNumbers) {
/* 577 */         int index = s.indexOf("\t");
/* 578 */         if (index != -1)
/* 579 */           s = s.substring(index + 1, s.length());
/* 580 */         sb.append(s);
/*     */       } else {
/* 582 */         sb.append(s);
/* 583 */       }if ((i < this.selEnd) || (this.selEnd > this.selStart)) sb.append('\n');
/*     */     }
/* 585 */     String s = new String(sb);
/* 586 */     Clipboard clip = getToolkit().getSystemClipboard();
/* 587 */     if (clip == null) return 0;
/* 588 */     StringSelection cont = new StringSelection(s);
/* 589 */     clip.setContents(cont, this);
/* 590 */     if (s.length() > 0) {
/* 591 */       IJ.showStatus(this.selEnd - this.selStart + 1 + " lines copied to clipboard");
/* 592 */       if ((getParent() instanceof ImageJ))
/* 593 */         Analyzer.setUnsavedMeasurements(false);
/*     */     }
/* 595 */     return s.length();
/*     */   }
/*     */ 
/*     */   int copyAll() {
/* 599 */     selectAll();
/* 600 */     int count = this.selEnd - this.selStart;
/* 601 */     if (count > 0)
/* 602 */       copySelection();
/* 603 */     resetSelection();
/* 604 */     this.unsavedLines = false;
/* 605 */     return count;
/*     */   }
/*     */ 
/*     */   void cutSelection() {
/* 609 */     if ((this.selStart == -1) || (this.selEnd == -1))
/* 610 */       selectAll();
/* 611 */     copySelection();
/* 612 */     clearSelection();
/*     */   }
/*     */ 
/*     */   public void clearSelection()
/*     */   {
/* 617 */     if ((this.selStart == -1) || (this.selEnd == -1)) {
/* 618 */       if (getLineCount() > 0)
/* 619 */         IJ.error("Selection required");
/* 620 */       return;
/*     */     }
/* 622 */     if (Recorder.record)
/* 623 */       Recorder.recordString("IJ.deleteRows(" + this.selStart + ", " + this.selEnd + ");\n");
/* 624 */     if ((this.selStart == 0) && (this.selEnd == this.iRowCount - 1)) {
/* 625 */       this.vData.removeAllElements();
/* 626 */       this.iRowCount = 0;
/* 627 */       if (this.rt != null)
/* 628 */         if ((IJ.isResultsWindow()) && (IJ.getTextPanel() == this)) {
/* 629 */           Analyzer.setUnsavedMeasurements(false);
/* 630 */           Analyzer.resetCounter();
/*     */         } else {
/* 632 */           this.rt.reset();
/*     */         }
/*     */     } else {
/* 635 */       int rowCount = this.iRowCount;
/* 636 */       boolean atEnd = rowCount - this.selEnd < 8;
/* 637 */       int count = this.selEnd - this.selStart + 1;
/* 638 */       for (int i = 0; i < count; i++) {
/* 639 */         this.vData.removeElementAt(this.selStart);
/* 640 */         this.iRowCount -= 1;
/*     */       }
/* 642 */       if ((this.rt != null) && (rowCount == this.rt.getCounter())) {
/* 643 */         for (int i = 0; i < count; i++)
/* 644 */           this.rt.deleteRow(this.selStart);
/* 645 */         this.rt.show(this.title);
/* 646 */         if (!atEnd) {
/* 647 */           this.iY = 0;
/* 648 */           this.tc.repaint();
/*     */         }
/*     */       }
/*     */     }
/* 652 */     this.selStart = -1; this.selEnd = -1; this.selOrigin = -1; this.selLine = -1;
/* 653 */     adjustVScroll();
/* 654 */     this.tc.repaint();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 659 */     if (this.vData == null) return;
/* 660 */     this.vData.removeAllElements();
/* 661 */     this.iRowCount = 0;
/* 662 */     this.selStart = -1; this.selEnd = -1; this.selOrigin = -1; this.selLine = -1;
/* 663 */     adjustVScroll();
/* 664 */     this.tc.repaint();
/*     */   }
/*     */ 
/*     */   public void selectAll()
/*     */   {
/* 669 */     if ((this.selStart == 0) && (this.selEnd == this.iRowCount - 1)) {
/* 670 */       resetSelection();
/* 671 */       return;
/*     */     }
/* 673 */     this.selStart = 0;
/* 674 */     this.selEnd = (this.iRowCount - 1);
/* 675 */     this.selOrigin = 0;
/* 676 */     this.tc.repaint();
/* 677 */     this.selLine = -1;
/*     */   }
/*     */ 
/*     */   public void resetSelection()
/*     */   {
/* 682 */     this.selStart = -1;
/* 683 */     this.selEnd = -1;
/* 684 */     this.selOrigin = -1;
/* 685 */     this.selLine = -1;
/* 686 */     if (this.iRowCount > 0)
/* 687 */       this.tc.repaint();
/*     */   }
/*     */ 
/*     */   public void setSelection(int startLine, int endLine)
/*     */   {
/* 692 */     if (startLine > endLine) endLine = startLine;
/* 693 */     if (startLine < 0) startLine = 0;
/* 694 */     if (endLine < 0) endLine = 0;
/* 695 */     if (startLine >= this.iRowCount) startLine = this.iRowCount - 1;
/* 696 */     if (endLine >= this.iRowCount) endLine = this.iRowCount - 1;
/* 697 */     this.selOrigin = startLine;
/* 698 */     this.selStart = startLine;
/* 699 */     this.selEnd = endLine;
/* 700 */     int vstart = this.sbVert.getValue();
/* 701 */     int visible = this.sbVert.getVisibleAmount() - 1;
/* 702 */     if (startLine < vstart) {
/* 703 */       this.sbVert.setValue(startLine);
/* 704 */       this.iY = (this.iRowHeight * startLine);
/* 705 */     } else if (endLine >= vstart + visible) {
/* 706 */       vstart = endLine - visible + 1;
/* 707 */       if (vstart < 0) vstart = 0;
/* 708 */       this.sbVert.setValue(vstart);
/* 709 */       this.iY = (this.iRowHeight * vstart);
/*     */     }
/* 711 */     this.tc.repaint();
/*     */   }
/*     */ 
/*     */   public void save(PrintWriter pw)
/*     */   {
/* 718 */     resetSelection();
/* 719 */     if ((this.labels != null) && (!this.labels.equals("")))
/* 720 */       pw.println(this.labels);
/* 721 */     for (int i = 0; i < this.iRowCount; i++) {
/* 722 */       char[] chars = (char[])this.vData.elementAt(i);
/* 723 */       String s = new String(chars);
/* 724 */       if (s.endsWith("\t"))
/* 725 */         s = s.substring(0, s.length() - 1);
/* 726 */       pw.println(s);
/*     */     }
/* 728 */     this.unsavedLines = false;
/*     */   }
/*     */ 
/*     */   public boolean saveAs(String path)
/*     */   {
/* 735 */     boolean isResults = (IJ.isResultsWindow()) && (IJ.getTextPanel() == this);
/* 736 */     boolean summarized = false;
/* 737 */     if (isResults) {
/* 738 */       String lastLine = this.iRowCount >= 2 ? getLine(this.iRowCount - 2) : null;
/* 739 */       summarized = (lastLine != null) && (lastLine.startsWith("Max"));
/*     */     }
/* 741 */     if ((this.rt != null) && (this.rt.getCounter() != 0) && (!summarized)) {
/* 742 */       if ((path == null) || (path.equals(""))) {
/* 743 */         IJ.wait(10);
/* 744 */         String name = isResults ? "Results" : this.title;
/* 745 */         SaveDialog sd = new SaveDialog("Save Results", name, Prefs.get("options.ext", ".xls"));
/* 746 */         String file = sd.getFileName();
/* 747 */         if (file == null) return false;
/* 748 */         path = sd.getDirectory() + file;
/*     */       }
/*     */       try {
/* 751 */         this.rt.saveAs(path);
/*     */       } catch (IOException e) {
/* 753 */         IJ.error("" + e);
/*     */       }
/*     */     } else {
/* 756 */       if (path.equals("")) {
/* 757 */         IJ.wait(10);
/* 758 */         boolean hasHeadings = !getColumnHeadings().equals("");
/* 759 */         String ext = (isResults) || (hasHeadings) ? Prefs.get("options.ext", ".xls") : ".txt";
/* 760 */         if (ext.equals(".csv")) ext = ".txt";
/* 761 */         SaveDialog sd = new SaveDialog("Save as Text", this.title, ext);
/* 762 */         String file = sd.getFileName();
/* 763 */         if (file == null) return false;
/* 764 */         path = sd.getDirectory() + file;
/*     */       }
/* 766 */       PrintWriter pw = null;
/*     */       try {
/* 768 */         FileOutputStream fos = new FileOutputStream(path);
/* 769 */         BufferedOutputStream bos = new BufferedOutputStream(fos);
/* 770 */         pw = new PrintWriter(bos);
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 774 */         return true;
/*     */       }
/* 776 */       save(pw);
/* 777 */       pw.close();
/*     */     }
/* 779 */     if (isResults) {
/* 780 */       Analyzer.setUnsavedMeasurements(false);
/* 781 */       if ((Recorder.record) && (!IJ.isMacro()))
/* 782 */         Recorder.record("saveAs", "Results", path);
/* 783 */     } else if (this.rt != null) {
/* 784 */       if ((Recorder.record) && (!IJ.isMacro()))
/* 785 */         Recorder.record("saveAs", "Results", path);
/*     */     }
/* 787 */     else if ((Recorder.record) && (!IJ.isMacro())) {
/* 788 */       Recorder.record("saveAs", "Text", path);
/*     */     }
/* 790 */     IJ.showStatus("");
/* 791 */     return true;
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/* 796 */     StringBuffer sb = new StringBuffer();
/* 797 */     if ((this.labels != null) && (!this.labels.equals(""))) {
/* 798 */       sb.append(this.labels);
/* 799 */       sb.append('\n');
/*     */     }
/* 801 */     for (int i = 0; i < this.iRowCount; i++) {
/* 802 */       char[] chars = (char[])this.vData.elementAt(i);
/* 803 */       sb.append(chars);
/* 804 */       sb.append('\n');
/*     */     }
/* 806 */     return new String(sb);
/*     */   }
/*     */ 
/*     */   public void setTitle(String title) {
/* 810 */     this.title = title;
/*     */   }
/*     */ 
/*     */   public int getLineCount()
/*     */   {
/* 815 */     return this.iRowCount;
/*     */   }
/*     */ 
/*     */   public String getLine(int index)
/*     */   {
/* 822 */     if ((index < 0) || (index >= this.iRowCount))
/* 823 */       throw new IllegalArgumentException("index out of range: " + index);
/* 824 */     return new String((char[])this.vData.elementAt(index));
/*     */   }
/*     */ 
/*     */   public void setLine(int index, String s)
/*     */   {
/* 831 */     if ((index < 0) || (index >= this.iRowCount))
/* 832 */       throw new IllegalArgumentException("index out of range: " + index);
/* 833 */     if (this.vData != null) {
/* 834 */       this.vData.setElementAt(s.toCharArray(), index);
/* 835 */       this.tc.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSelectionStart()
/*     */   {
/* 842 */     return this.selStart;
/*     */   }
/*     */ 
/*     */   public int getSelectionEnd()
/*     */   {
/* 848 */     return this.selEnd;
/*     */   }
/*     */ 
/*     */   public void setResultsTable(ResultsTable rt)
/*     */   {
/* 853 */     this.rt = rt;
/*     */   }
/*     */ 
/*     */   public ResultsTable getResultsTable()
/*     */   {
/* 858 */     return this.rt;
/*     */   }
/*     */ 
/*     */   public void scrollToTop() {
/* 862 */     this.sbVert.setValue(0);
/* 863 */     this.iY = 0;
/* 864 */     for (int i = 0; i < this.iColCount; i++)
/* 865 */       this.tc.calcAutoWidth(i);
/* 866 */     adjustHScroll();
/* 867 */     this.tc.repaint();
/*     */   }
/*     */ 
/*     */   void flush() {
/* 871 */     if (this.vData != null)
/* 872 */       this.vData.removeAllElements();
/* 873 */     this.vData = null;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.text.TextPanel
 * JD-Core Version:    0.6.2
 */