/*      */ package ij.plugin.frame;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImageJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.Macro;
/*      */ import ij.Prefs;
/*      */ import ij.Undo;
/*      */ import ij.WindowManager;
/*      */ import ij.gui.ColorChooser;
/*      */ import ij.gui.GUI;
/*      */ import ij.gui.GenericDialog;
/*      */ import ij.gui.ImageCanvas;
/*      */ import ij.gui.ImageRoi;
/*      */ import ij.gui.MessageDialog;
/*      */ import ij.gui.Plot;
/*      */ import ij.gui.PointRoi;
/*      */ import ij.gui.ProfilePlot;
/*      */ import ij.gui.Roi;
/*      */ import ij.gui.RoiProperties;
/*      */ import ij.gui.ShapeRoi;
/*      */ import ij.gui.TextRoi;
/*      */ import ij.gui.Toolbar;
/*      */ import ij.gui.YesNoCancelDialog;
/*      */ import ij.io.OpenDialog;
/*      */ import ij.io.Opener;
/*      */ import ij.io.RoiDecoder;
/*      */ import ij.io.RoiEncoder;
/*      */ import ij.io.SaveDialog;
/*      */ import ij.macro.Interpreter;
/*      */ import ij.macro.MacroRunner;
/*      */ import ij.measure.Calibration;
/*      */ import ij.measure.ResultsTable;
/*      */ import ij.plugin.Colors;
/*      */ import ij.plugin.filter.Analyzer;
/*      */ import ij.plugin.filter.Filler;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.process.ImageStatistics;
/*      */ import ij.util.StringSorter;
/*      */ import ij.util.Tools;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Button;
/*      */ import java.awt.Checkbox;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Frame;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.List;
/*      */ import java.awt.MenuItem;
/*      */ import java.awt.Panel;
/*      */ import java.awt.Point;
/*      */ import java.awt.Polygon;
/*      */ import java.awt.PopupMenu;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.event.MouseWheelListener;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipInputStream;
/*      */ import java.util.zip.ZipOutputStream;
/*      */ 
/*      */ public class RoiManager extends PlugInFrame
/*      */   implements ActionListener, ItemListener, MouseListener, MouseWheelListener
/*      */ {
/*      */   public static final String LOC_KEY = "manager.loc";
/*      */   private static final int BUTTONS = 11;
/*      */   private static final int DRAW = 0;
/*      */   private static final int FILL = 1;
/*      */   private static final int LABEL = 2;
/*      */   private static final int SHOW_ALL = 0;
/*      */   private static final int SHOW_NONE = 1;
/*      */   private static final int LABELS = 2;
/*      */   private static final int NO_LABELS = 3;
/*      */   private static final int MENU = 0;
/*      */   private static final int COMMAND = 1;
/*   25 */   private static int rows = 15;
/*   26 */   private static int lastNonShiftClick = -1;
/*   27 */   private static boolean allowMultipleSelections = true;
/*   28 */   private static String moreButtonLabel = "More »";
/*      */   private Panel panel;
/*      */   private static Frame instance;
/*   31 */   private static int colorIndex = 4;
/*      */   private List list;
/*   33 */   private Hashtable rois = new Hashtable();
/*      */   private boolean canceled;
/*      */   private boolean macro;
/*      */   private boolean ignoreInterrupts;
/*      */   private PopupMenu pm;
/*      */   private Button moreButton;
/*      */   private Button colorButton;
/*   39 */   private Checkbox showAllCheckbox = new Checkbox("Show All", false);
/*   40 */   private Checkbox labelsCheckbox = new Checkbox("Edit Mode", false);
/*      */ 
/*   42 */   private static boolean measureAll = true;
/*   43 */   private static boolean onePerSlice = true;
/*      */   private static boolean restoreCentered;
/*      */   private int prevID;
/*      */   private boolean noUpdateMode;
/*   47 */   private int defaultLineWidth = 1;
/*      */   private Color defaultColor;
/*   49 */   private boolean firstTime = true;
/*      */   private int[] selectedIndexes;
/*      */ 
/*      */   public RoiManager()
/*      */   {
/*   53 */     super("ROI Manager");
/*   54 */     if (instance != null) {
/*   55 */       WindowManager.toFront(instance);
/*   56 */       return;
/*      */     }
/*   58 */     instance = this;
/*   59 */     this.list = new List(rows, allowMultipleSelections);
/*   60 */     showWindow();
/*      */   }
/*      */ 
/*      */   public RoiManager(boolean hideWindow) {
/*   64 */     super("ROI Manager");
/*   65 */     this.list = new List(rows, allowMultipleSelections);
/*      */   }
/*      */ 
/*      */   void showWindow() {
/*   69 */     ImageJ ij = IJ.getInstance();
/*   70 */     addKeyListener(ij);
/*   71 */     addMouseListener(this);
/*   72 */     addMouseWheelListener(this);
/*   73 */     WindowManager.addWindow(this);
/*      */ 
/*   75 */     setLayout(new BorderLayout());
/*   76 */     this.list.add("012345678901234");
/*   77 */     this.list.addItemListener(this);
/*   78 */     this.list.addKeyListener(ij);
/*   79 */     this.list.addMouseListener(this);
/*   80 */     this.list.addMouseWheelListener(this);
/*   81 */     if (IJ.isLinux()) this.list.setBackground(Color.white);
/*   82 */     add("Center", this.list);
/*   83 */     this.panel = new Panel();
/*   84 */     int nButtons = 11;
/*   85 */     this.panel.setLayout(new GridLayout(nButtons, 1, 5, 0));
/*   86 */     addButton("Add [t]");
/*   87 */     addButton("Update");
/*   88 */     addButton("Delete");
/*   89 */     addButton("Rename...");
/*   90 */     addButton("Measure");
/*   91 */     addButton("Deselect");
/*   92 */     addButton("Properties...");
/*   93 */     addButton("Flatten [F]");
/*   94 */     addButton(moreButtonLabel);
/*   95 */     this.showAllCheckbox.addItemListener(this);
/*   96 */     this.panel.add(this.showAllCheckbox);
/*   97 */     this.labelsCheckbox.addItemListener(this);
/*   98 */     this.panel.add(this.labelsCheckbox);
/*   99 */     add("East", this.panel);
/*  100 */     addPopupMenu();
/*  101 */     pack();
/*  102 */     Dimension size = getSize();
/*  103 */     if (size.width > 270)
/*  104 */       setSize(size.width - 40, size.height);
/*  105 */     this.list.remove(0);
/*  106 */     Point loc = Prefs.getLocation("manager.loc");
/*  107 */     if (loc != null)
/*  108 */       setLocation(loc);
/*      */     else
/*  110 */       GUI.center(this);
/*  111 */     show();
/*  112 */     if ((IJ.isMacOSX()) && (IJ.isJava16())) {
/*  113 */       this.list.setMultipleMode(false);
/*  114 */       this.list.setMultipleMode(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   void addButton(String label)
/*      */   {
/*  125 */     Button b = new Button(label);
/*  126 */     b.addActionListener(this);
/*  127 */     b.addKeyListener(IJ.getInstance());
/*  128 */     b.addMouseListener(this);
/*  129 */     if (label.equals(moreButtonLabel)) this.moreButton = b;
/*  130 */     this.panel.add(b);
/*      */   }
/*      */ 
/*      */   void addPopupMenu() {
/*  134 */     this.pm = new PopupMenu();
/*      */ 
/*  136 */     addPopupItem("Open...");
/*  137 */     addPopupItem("Save...");
/*  138 */     addPopupItem("Fill");
/*  139 */     addPopupItem("Draw");
/*  140 */     addPopupItem("AND");
/*  141 */     addPopupItem("OR (Combine)");
/*  142 */     addPopupItem("XOR");
/*  143 */     addPopupItem("Split");
/*  144 */     addPopupItem("Add Particles");
/*  145 */     addPopupItem("Multi Measure");
/*  146 */     addPopupItem("Multi Plot");
/*  147 */     addPopupItem("Sort");
/*  148 */     addPopupItem("Specify...");
/*  149 */     addPopupItem("Remove Slice Info");
/*  150 */     addPopupItem("Help");
/*  151 */     addPopupItem("Options...");
/*  152 */     add(this.pm);
/*      */   }
/*      */ 
/*      */   void addPopupItem(String s) {
/*  156 */     MenuItem mi = new MenuItem(s);
/*  157 */     mi.addActionListener(this);
/*  158 */     this.pm.add(mi);
/*      */   }
/*      */ 
/*      */   public void actionPerformed(ActionEvent e) {
/*  162 */     String label = e.getActionCommand();
/*  163 */     if (label == null)
/*  164 */       return;
/*  165 */     String command = label;
/*  166 */     if (command.equals("Add [t]")) {
/*  167 */       runCommand("add");
/*  168 */     } else if (command.equals("Update")) {
/*  169 */       update(true);
/*  170 */     } else if (command.equals("Delete")) {
/*  171 */       delete(false);
/*  172 */     } else if (command.equals("Rename...")) {
/*  173 */       rename(null);
/*  174 */     } else if (command.equals("Properties...")) {
/*  175 */       setProperties(null, -1, null);
/*  176 */     } else if (command.equals("Flatten [F]")) {
/*  177 */       flatten();
/*  178 */     } else if (command.equals("Measure")) {
/*  179 */       measure(0);
/*  180 */     } else if (command.equals("Open...")) {
/*  181 */       open(null);
/*  182 */     } else if (command.equals("Save...")) {
/*  183 */       save();
/*  184 */     } else if (command.equals("Fill")) {
/*  185 */       drawOrFill(1);
/*  186 */     } else if (command.equals("Draw")) {
/*  187 */       drawOrFill(0);
/*  188 */     } else if (command.equals("Deselect")) {
/*  189 */       select(-1);
/*  190 */     } else if (command.equals(moreButtonLabel)) {
/*  191 */       Point ploc = this.panel.getLocation();
/*  192 */       Point bloc = this.moreButton.getLocation();
/*  193 */       this.pm.show(this, ploc.x, bloc.y);
/*  194 */     } else if (command.equals("OR (Combine)")) {
/*  195 */       combine();
/*  196 */     } else if (command.equals("Split")) {
/*  197 */       split();
/*  198 */     } else if (command.equals("AND")) {
/*  199 */       and();
/*  200 */     } else if (command.equals("XOR")) {
/*  201 */       xor();
/*  202 */     } else if (command.equals("Add Particles")) {
/*  203 */       addParticles();
/*  204 */     } else if (command.equals("Multi Measure")) {
/*  205 */       multiMeasure();
/*  206 */     } else if (command.equals("Multi Plot")) {
/*  207 */       multiPlot();
/*  208 */     } else if (command.equals("Sort")) {
/*  209 */       sort();
/*  210 */     } else if (command.equals("Specify...")) {
/*  211 */       specify();
/*  212 */     } else if (command.equals("Remove Slice Info")) {
/*  213 */       removeSliceInfo();
/*  214 */     } else if (command.equals("Help")) {
/*  215 */       help();
/*  216 */     } else if (command.equals("Options...")) {
/*  217 */       options();
/*  218 */     } else if (command.equals("\"Show All\" Color...")) {
/*  219 */       setShowAllColor();
/*      */     }
/*      */   }
/*      */ 
/*  223 */   public void itemStateChanged(ItemEvent e) { Object source = e.getSource();
/*  224 */     if (source == this.showAllCheckbox) {
/*  225 */       if (this.firstTime)
/*  226 */         this.labelsCheckbox.setState(true);
/*  227 */       showAll(this.showAllCheckbox.getState() ? 0 : 1);
/*  228 */       this.firstTime = false;
/*  229 */       return;
/*      */     }
/*  231 */     if (source == this.labelsCheckbox) {
/*  232 */       if (this.firstTime)
/*  233 */         this.showAllCheckbox.setState(true);
/*  234 */       boolean editState = this.labelsCheckbox.getState();
/*  235 */       boolean showAllState = this.showAllCheckbox.getState();
/*  236 */       if ((!showAllState) && (!editState)) {
/*  237 */         showAll(1);
/*      */       } else {
/*  239 */         showAll(editState ? 2 : 3);
/*  240 */         if (editState) this.showAllCheckbox.setState(true);
/*      */       }
/*  242 */       this.firstTime = false;
/*  243 */       return;
/*      */     }
/*  245 */     if ((e.getStateChange() == 1) && (!this.ignoreInterrupts)) {
/*  246 */       int index = 0;
/*      */       try {
/*  248 */         index = Integer.parseInt(e.getItem().toString()); } catch (NumberFormatException ex) {
/*      */       }
/*  250 */       if (index < 0) index = 0;
/*  251 */       if (!IJ.isMacintosh()) {
/*  252 */         if (!IJ.shiftKeyDown()) lastNonShiftClick = index;
/*  253 */         if ((!IJ.shiftKeyDown()) && (!IJ.controlKeyDown())) {
/*  254 */           int[] indexes = getSelectedIndexes();
/*  255 */           for (int i = 0; i < indexes.length; i++)
/*  256 */             if (indexes[i] != index)
/*  257 */               this.list.deselect(indexes[i]);
/*      */         }
/*  259 */         else if ((IJ.shiftKeyDown()) && (lastNonShiftClick >= 0) && (lastNonShiftClick < this.list.getItemCount())) {
/*  260 */           int firstIndex = Math.min(index, lastNonShiftClick);
/*  261 */           int lastIndex = Math.max(index, lastNonShiftClick);
/*  262 */           int[] indexes = getSelectedIndexes();
/*  263 */           for (int i = 0; i < indexes.length; i++)
/*  264 */             if ((indexes[i] < firstIndex) || (indexes[i] > lastIndex))
/*  265 */               this.list.deselect(indexes[i]);
/*  266 */           for (int i = firstIndex; i <= lastIndex; i++)
/*  267 */             this.list.select(i);
/*      */         }
/*      */       }
/*  270 */       if (WindowManager.getCurrentImage() != null) {
/*  271 */         restore(getImage(), index, true);
/*  272 */         if (record())
/*  273 */           if (Recorder.scriptMode())
/*  274 */             Recorder.recordCall("rm.select(imp, " + index + ");");
/*      */           else
/*  276 */             Recorder.record("roiManager", "Select", index);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void add(boolean shiftKeyDown, boolean altKeyDown)
/*      */   {
/*  283 */     if (shiftKeyDown)
/*  284 */       addAndDraw(altKeyDown);
/*  285 */     else if (altKeyDown)
/*  286 */       addRoi(true);
/*      */     else
/*  288 */       addRoi(false);
/*      */   }
/*      */ 
/*      */   public void addRoi(Roi roi)
/*      */   {
/*  293 */     addRoi(roi, false, null, -1);
/*      */   }
/*      */ 
/*      */   boolean addRoi(boolean promptForName) {
/*  297 */     return addRoi(null, promptForName, null, -1);
/*      */   }
/*      */ 
/*      */   boolean addRoi(Roi roi, boolean promptForName, Color color, int lineWidth) {
/*  301 */     ImagePlus imp = roi == null ? getImage() : WindowManager.getCurrentImage();
/*  302 */     if (roi == null) {
/*  303 */       if (imp == null)
/*  304 */         return false;
/*  305 */       roi = imp.getRoi();
/*  306 */       if (roi == null) {
/*  307 */         error("The active image does not have a selection.");
/*  308 */         return false;
/*      */       }
/*      */     }
/*  311 */     if ((color == null) && (roi.getStrokeColor() != null))
/*  312 */       color = roi.getStrokeColor();
/*  313 */     else if ((color == null) && (this.defaultColor != null))
/*  314 */       color = this.defaultColor;
/*  315 */     if (lineWidth < 0) {
/*  316 */       int sw = (int)roi.getStrokeWidth();
/*  317 */       lineWidth = sw > 1 ? sw : this.defaultLineWidth;
/*      */     }
/*  319 */     if (lineWidth > 100) lineWidth = 1;
/*  320 */     int n = this.list.getItemCount();
/*  321 */     if ((n > 0) && (!IJ.isMacro()) && (imp != null))
/*      */     {
/*  323 */       String label = this.list.getItem(n - 1);
/*  324 */       Roi roi2 = (Roi)this.rois.get(label);
/*  325 */       if (roi2 != null) {
/*  326 */         int slice2 = getSliceNumber(roi2, label);
/*  327 */         if ((roi.equals(roi2)) && ((slice2 == -1) || (slice2 == imp.getCurrentSlice())) && (imp.getID() == this.prevID) && (!Interpreter.isBatchMode()))
/*  328 */           return false;
/*      */       }
/*      */     }
/*  331 */     this.prevID = (imp != null ? imp.getID() : 0);
/*  332 */     String name = roi.getName();
/*  333 */     if (isStandardName(name))
/*  334 */       name = null;
/*  335 */     String label = name != null ? name : getLabel(imp, roi, -1);
/*  336 */     if (promptForName)
/*  337 */       label = promptForName(label);
/*      */     else
/*  339 */       label = getUniqueName(label);
/*  340 */     if (label == null) return false;
/*  341 */     this.list.add(label);
/*  342 */     roi.setName(label);
/*  343 */     Roi roiCopy = (Roi)roi.clone();
/*  344 */     if (lineWidth > 1)
/*  345 */       roiCopy.setStrokeWidth(lineWidth);
/*  346 */     if (color != null)
/*  347 */       roiCopy.setStrokeColor(color);
/*  348 */     this.rois.put(label, roiCopy);
/*  349 */     updateShowAll();
/*  350 */     if (record())
/*  351 */       recordAdd(this.defaultColor, this.defaultLineWidth);
/*  352 */     return true;
/*      */   }
/*      */ 
/*      */   void recordAdd(Color color, int lineWidth) {
/*  356 */     if (Recorder.scriptMode())
/*  357 */       Recorder.recordCall("rm.addRoi(imp.getRoi());");
/*  358 */     else if ((color != null) && (lineWidth == 1))
/*  359 */       Recorder.recordString("roiManager(\"Add\", \"" + getHex(color) + "\");\n");
/*  360 */     else if (lineWidth > 1)
/*  361 */       Recorder.recordString("roiManager(\"Add\", \"" + getHex(color) + "\", " + lineWidth + ");\n");
/*      */     else
/*  363 */       Recorder.record("roiManager", "Add");
/*      */   }
/*      */ 
/*      */   String getHex(Color color) {
/*  367 */     if (color == null) color = ImageCanvas.getShowAllColor();
/*  368 */     String hex = Integer.toHexString(color.getRGB());
/*  369 */     if (hex.length() == 8) hex = hex.substring(2);
/*  370 */     return hex;
/*      */   }
/*      */ 
/*      */   public void add(ImagePlus imp, Roi roi, int n)
/*      */   {
/*  376 */     if (roi == null) return;
/*  377 */     String label = roi.getName();
/*  378 */     if (label == null)
/*  379 */       label = getLabel(imp, roi, n);
/*  380 */     if (label == null) return;
/*  381 */     this.list.add(label);
/*  382 */     roi.setName(label);
/*  383 */     this.rois.put(label, (Roi)roi.clone());
/*      */   }
/*      */ 
/*      */   boolean isStandardName(String name) {
/*  387 */     if (name == null) return false;
/*  388 */     boolean isStandard = false;
/*  389 */     int len = name.length();
/*  390 */     if ((len >= 14) && (name.charAt(4) == '-') && (name.charAt(9) == '-'))
/*  391 */       isStandard = true;
/*  392 */     else if ((len >= 17) && (name.charAt(5) == '-') && (name.charAt(11) == '-'))
/*  393 */       isStandard = true;
/*  394 */     else if ((len >= 9) && (name.charAt(4) == '-'))
/*  395 */       isStandard = true;
/*  396 */     else if ((len >= 11) && (name.charAt(5) == '-'))
/*  397 */       isStandard = true;
/*  398 */     return isStandard;
/*      */   }
/*      */ 
/*      */   String getLabel(ImagePlus imp, Roi roi, int n) {
/*  402 */     Rectangle r = roi.getBounds();
/*  403 */     int xc = r.x + r.width / 2;
/*  404 */     int yc = r.y + r.height / 2;
/*  405 */     if (n >= 0) {
/*  406 */       xc = yc; yc = n;
/*  407 */     }if (xc < 0) xc = 0;
/*  408 */     if (yc < 0) yc = 0;
/*  409 */     int digits = 4;
/*  410 */     String xs = "" + xc;
/*  411 */     if (xs.length() > digits) digits = xs.length();
/*  412 */     String ys = "" + yc;
/*  413 */     if (ys.length() > digits) digits = ys.length();
/*  414 */     if ((digits == 4) && (imp != null) && (imp.getStackSize() >= 10000)) digits = 5;
/*  415 */     xs = "000000" + xc;
/*  416 */     ys = "000000" + yc;
/*  417 */     String label = ys.substring(ys.length() - digits) + "-" + xs.substring(xs.length() - digits);
/*  418 */     if ((imp != null) && (imp.getStackSize() > 1)) {
/*  419 */       int slice = roi.getPosition();
/*  420 */       if (slice == 0)
/*  421 */         slice = imp.getCurrentSlice();
/*  422 */       String zs = "000000" + slice;
/*  423 */       label = zs.substring(zs.length() - digits) + "-" + label;
/*  424 */       roi.setPosition(slice);
/*      */     }
/*  426 */     return label;
/*      */   }
/*      */ 
/*      */   void addAndDraw(boolean altKeyDown) {
/*  430 */     if (altKeyDown) { if (addRoi(true)); }
/*  432 */     else if (!addRoi(false))
/*  433 */       return;
/*  434 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  435 */     if (imp != null) {
/*  436 */       Undo.setup(4, imp);
/*  437 */       IJ.run(imp, "Draw", "slice");
/*  438 */       Undo.setup(5, imp);
/*      */     }
/*  440 */     if (record()) Recorder.record("roiManager", "Add & Draw"); 
/*      */   }
/*      */ 
/*      */   boolean delete(boolean replacing)
/*      */   {
/*  444 */     int count = this.list.getItemCount();
/*  445 */     if (count == 0)
/*  446 */       return error("The list is empty.");
/*  447 */     int[] index = getSelectedIndexes();
/*  448 */     if ((index.length == 0) || ((replacing) && (count > 1))) {
/*  449 */       String msg = "Delete all items on the list?";
/*  450 */       if (replacing)
/*  451 */         msg = "Replace items on the list?";
/*  452 */       this.canceled = false;
/*  453 */       if ((!IJ.isMacro()) && (!this.macro)) {
/*  454 */         YesNoCancelDialog d = new YesNoCancelDialog(this, "ROI Manager", msg);
/*  455 */         if (d.cancelPressed()) {
/*  456 */           this.canceled = true; return false;
/*  457 */         }if (!d.yesPressed()) return false;
/*      */       }
/*  459 */       index = getAllIndexes();
/*      */     }
/*  461 */     if ((count == index.length) && (!replacing)) {
/*  462 */       this.rois.clear();
/*  463 */       this.list.removeAll();
/*      */     } else {
/*  465 */       for (int i = count - 1; i >= 0; i--) {
/*  466 */         boolean delete = false;
/*  467 */         for (int j = 0; j < index.length; j++) {
/*  468 */           if (index[j] == i)
/*  469 */             delete = true;
/*      */         }
/*  471 */         if (delete) {
/*  472 */           this.rois.remove(this.list.getItem(i));
/*  473 */           this.list.remove(i);
/*      */         }
/*      */       }
/*      */     }
/*  477 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  478 */     if ((count > 1) && (index.length == 1) && (imp != null))
/*  479 */       imp.killRoi();
/*  480 */     updateShowAll();
/*  481 */     if (record()) Recorder.record("roiManager", "Delete");
/*  482 */     return true;
/*      */   }
/*      */ 
/*      */   boolean update(boolean clone) {
/*  486 */     ImagePlus imp = getImage();
/*  487 */     if (imp == null) return false;
/*  488 */     ImageCanvas ic = imp.getCanvas();
/*  489 */     boolean showingAll = (ic != null) && (ic.getShowAllROIs());
/*  490 */     Roi roi = imp.getRoi();
/*  491 */     if (roi == null) {
/*  492 */       error("The active image does not have a selection.");
/*  493 */       return false;
/*      */     }
/*  495 */     int index = this.list.getSelectedIndex();
/*  496 */     if ((index < 0) && (!showingAll))
/*  497 */       return error("Exactly one item in the list must be selected.");
/*  498 */     if (index >= 0) {
/*  499 */       String name = this.list.getItem(index);
/*  500 */       this.rois.remove(name);
/*  501 */       if (clone) {
/*  502 */         Roi roi2 = (Roi)roi.clone();
/*  503 */         int position = roi.getPosition();
/*  504 */         if (imp.getStackSize() > 1)
/*  505 */           roi2.setPosition(imp.getCurrentSlice());
/*  506 */         roi.setName(name);
/*  507 */         roi2.setName(name);
/*  508 */         this.rois.put(name, roi2);
/*      */       } else {
/*  510 */         this.rois.put(name, roi);
/*      */       }
/*      */     }
/*  512 */     if (record()) Recorder.record("roiManager", "Update");
/*  513 */     if (showingAll) imp.draw();
/*  514 */     return true;
/*      */   }
/*      */ 
/*      */   boolean rename(String name2) {
/*  518 */     int index = this.list.getSelectedIndex();
/*  519 */     if (index < 0)
/*  520 */       return error("Exactly one item in the list must be selected.");
/*  521 */     String name = this.list.getItem(index);
/*  522 */     if (name2 == null) name2 = promptForName(name);
/*  523 */     if (name2 == null) return false;
/*  524 */     Roi roi = (Roi)this.rois.get(name);
/*  525 */     this.rois.remove(name);
/*  526 */     roi.setName(name2);
/*  527 */     this.rois.put(name2, roi);
/*  528 */     this.list.replaceItem(name2, index);
/*  529 */     this.list.select(index);
/*  530 */     if ((Prefs.useNamesAsLabels) && (this.labelsCheckbox.getState())) {
/*  531 */       ImagePlus imp = WindowManager.getCurrentImage();
/*  532 */       if (imp != null) imp.draw();
/*      */     }
/*  534 */     if (record())
/*  535 */       Recorder.record("roiManager", "Rename", name2);
/*  536 */     return true;
/*      */   }
/*      */ 
/*      */   String promptForName(String name) {
/*  540 */     GenericDialog gd = new GenericDialog("ROI Manager");
/*  541 */     gd.addStringField("Rename As:", name, 20);
/*  542 */     gd.showDialog();
/*  543 */     if (gd.wasCanceled())
/*  544 */       return null;
/*  545 */     String name2 = gd.getNextString();
/*  546 */     name2 = getUniqueName(name2);
/*  547 */     return name2;
/*      */   }
/*      */ 
/*      */   boolean restore(ImagePlus imp, int index, boolean setSlice) {
/*  551 */     String label = this.list.getItem(index);
/*  552 */     Roi roi = (Roi)this.rois.get(label);
/*  553 */     if ((imp == null) || (roi == null))
/*  554 */       return false;
/*  555 */     if (setSlice) {
/*  556 */       int n = getSliceNumber(roi, label);
/*  557 */       if ((n >= 1) && (n <= imp.getStackSize())) {
/*  558 */         if ((imp.isHyperStack()) || (imp.isComposite()))
/*  559 */           imp.setPosition(n);
/*      */         else
/*  561 */           imp.setSlice(n);
/*      */       }
/*      */     }
/*  564 */     Roi roi2 = (Roi)roi.clone();
/*  565 */     Calibration cal = imp.getCalibration();
/*  566 */     Rectangle r = roi2.getBounds();
/*  567 */     int width = imp.getWidth(); int height = imp.getHeight();
/*  568 */     if (restoreCentered) {
/*  569 */       ImageCanvas ic = imp.getCanvas();
/*  570 */       if (ic != null) {
/*  571 */         Rectangle r1 = ic.getSrcRect();
/*  572 */         Rectangle r2 = roi2.getBounds();
/*  573 */         roi2.setLocation(r1.x + r1.width / 2 - r2.width / 2, r1.y + r1.height / 2 - r2.height / 2);
/*      */       }
/*      */     }
/*  576 */     if ((r.x >= width) || (r.y >= height) || (r.x + r.width <= 0) || (r.y + r.height <= 0))
/*  577 */       roi2.setLocation((width - r.width) / 2, (height - r.height) / 2);
/*  578 */     if (this.noUpdateMode) {
/*  579 */       imp.setRoi(roi2, false);
/*  580 */       this.noUpdateMode = false;
/*      */     } else {
/*  582 */       imp.setRoi(roi2, true);
/*  583 */     }return true;
/*      */   }
/*      */ 
/*      */   boolean restoreWithoutUpdate(int index) {
/*  587 */     this.noUpdateMode = true;
/*  588 */     return restore(getImage(), index, false);
/*      */   }
/*      */ 
/*      */   public int getSliceNumber(String label)
/*      */   {
/*  594 */     int slice = -1;
/*  595 */     if ((label.length() >= 14) && (label.charAt(4) == '-') && (label.charAt(9) == '-'))
/*  596 */       slice = (int)Tools.parseDouble(label.substring(0, 4), -1.0D);
/*  597 */     else if ((label.length() >= 17) && (label.charAt(5) == '-') && (label.charAt(11) == '-'))
/*  598 */       slice = (int)Tools.parseDouble(label.substring(0, 5), -1.0D);
/*  599 */     else if ((label.length() >= 20) && (label.charAt(6) == '-') && (label.charAt(13) == '-'))
/*  600 */       slice = (int)Tools.parseDouble(label.substring(0, 6), -1.0D);
/*  601 */     return slice;
/*      */   }
/*      */ 
/*      */   int getSliceNumber(Roi roi, String label)
/*      */   {
/*  607 */     int slice = roi != null ? roi.getPosition() : -1;
/*  608 */     if (slice == 0)
/*  609 */       slice = -1;
/*  610 */     if (slice == -1)
/*  611 */       slice = getSliceNumber(label);
/*  612 */     return slice;
/*      */   }
/*      */ 
/*      */   void open(String path) {
/*  616 */     Macro.setOptions(null);
/*  617 */     String name = null;
/*  618 */     if ((path == null) || (path.equals(""))) {
/*  619 */       OpenDialog od = new OpenDialog("Open Selection(s)...", "");
/*  620 */       String directory = od.getDirectory();
/*  621 */       name = od.getFileName();
/*  622 */       if (name == null)
/*  623 */         return;
/*  624 */       path = directory + name;
/*      */     }
/*  626 */     if (record()) Recorder.record("roiManager", "Open", path);
/*  627 */     if (path.endsWith(".zip")) {
/*  628 */       openZip(path);
/*  629 */       return;
/*      */     }
/*  631 */     Opener o = new Opener();
/*  632 */     if (name == null) name = o.getName(path);
/*  633 */     Roi roi = o.openRoi(path);
/*  634 */     if (roi != null) {
/*  635 */       if (name.endsWith(".roi"))
/*  636 */         name = name.substring(0, name.length() - 4);
/*  637 */       name = getUniqueName(name);
/*  638 */       this.list.add(name);
/*  639 */       this.rois.put(name, roi);
/*      */     }
/*  641 */     updateShowAll();
/*      */   }
/*      */ 
/*      */   void openZip(String path)
/*      */   {
/*  646 */     ZipInputStream in = null;
/*      */ 
/*  648 */     int nRois = 0;
/*      */     try {
/*  650 */       in = new ZipInputStream(new FileInputStream(path));
/*  651 */       byte[] buf = new byte[1024];
/*      */ 
/*  653 */       ZipEntry entry = in.getNextEntry();
/*  654 */       while (entry != null) {
/*  655 */         String name = entry.getName();
/*  656 */         if (name.endsWith(".roi")) {
/*  657 */           ByteArrayOutputStream out = new ByteArrayOutputStream();
/*      */           int len;
/*  658 */           while ((len = in.read(buf)) > 0)
/*  659 */             out.write(buf, 0, len);
/*  660 */           out.close();
/*  661 */           byte[] bytes = out.toByteArray();
/*  662 */           RoiDecoder rd = new RoiDecoder(bytes, name);
/*  663 */           Roi roi = rd.getRoi();
/*  664 */           if (roi != null) {
/*  665 */             name = name.substring(0, name.length() - 4);
/*  666 */             name = getUniqueName(name);
/*  667 */             this.list.add(name);
/*  668 */             this.rois.put(name, roi);
/*  669 */             nRois++;
/*      */           }
/*      */         }
/*  672 */         entry = in.getNextEntry();
/*      */       }
/*  674 */       in.close(); } catch (IOException e) {
/*  675 */       error(e.toString());
/*  676 */     }if (nRois == 0)
/*  677 */       error("This ZIP archive does not appear to contain \".roi\" files");
/*  678 */     updateShowAll();
/*      */   }
/*      */ 
/*      */   String getUniqueName(String name)
/*      */   {
/*  683 */     String name2 = name;
/*  684 */     int n = 1;
/*  685 */     Roi roi2 = (Roi)this.rois.get(name2);
/*  686 */     while (roi2 != null) {
/*  687 */       roi2 = (Roi)this.rois.get(name2);
/*  688 */       if (roi2 != null) {
/*  689 */         int lastDash = name2.lastIndexOf("-");
/*  690 */         if ((lastDash != -1) && (name2.length() - lastDash < 5))
/*  691 */           name2 = name2.substring(0, lastDash);
/*  692 */         name2 = name2 + "-" + n;
/*  693 */         n++;
/*      */       }
/*  695 */       roi2 = (Roi)this.rois.get(name2);
/*      */     }
/*  697 */     return name2;
/*      */   }
/*      */ 
/*      */   boolean save() {
/*  701 */     if (this.list.getItemCount() == 0)
/*  702 */       return error("The selection list is empty.");
/*  703 */     int[] indexes = getSelectedIndexes();
/*  704 */     if (indexes.length == 0)
/*  705 */       indexes = getAllIndexes();
/*  706 */     if (indexes.length > 1)
/*  707 */       return saveMultiple(indexes, null);
/*  708 */     String name = this.list.getItem(indexes[0]);
/*  709 */     Macro.setOptions(null);
/*  710 */     SaveDialog sd = new SaveDialog("Save Selection...", name, ".roi");
/*  711 */     String name2 = sd.getFileName();
/*  712 */     if (name2 == null)
/*  713 */       return false;
/*  714 */     String dir = sd.getDirectory();
/*  715 */     Roi roi = (Roi)this.rois.get(name);
/*  716 */     this.rois.remove(name);
/*  717 */     if (!name2.endsWith(".roi")) name2 = name2 + ".roi";
/*  718 */     String newName = name2.substring(0, name2.length() - 4);
/*  719 */     this.rois.put(newName, roi);
/*  720 */     roi.setName(newName);
/*  721 */     this.list.replaceItem(newName, indexes[0]);
/*  722 */     RoiEncoder re = new RoiEncoder(dir + name2);
/*      */     try {
/*  724 */       re.write(roi);
/*      */     } catch (IOException e) {
/*  726 */       IJ.error("ROI Manager", e.getMessage());
/*      */     }
/*  728 */     return true;
/*      */   }
/*      */ 
/*      */   boolean saveMultiple(int[] indexes, String path) {
/*  732 */     Macro.setOptions(null);
/*  733 */     if (path == null) {
/*  734 */       SaveDialog sd = new SaveDialog("Save ROIs...", "RoiSet", ".zip");
/*  735 */       String name = sd.getFileName();
/*  736 */       if (name == null)
/*  737 */         return false;
/*  738 */       if ((!name.endsWith(".zip")) && (!name.endsWith(".ZIP")))
/*  739 */         name = name + ".zip";
/*  740 */       String dir = sd.getDirectory();
/*  741 */       path = dir + name;
/*      */     }
/*      */     try {
/*  744 */       ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path));
/*  745 */       DataOutputStream out = new DataOutputStream(new BufferedOutputStream(zos));
/*  746 */       RoiEncoder re = new RoiEncoder(out);
/*  747 */       for (int i = 0; i < indexes.length; i++) {
/*  748 */         String label = this.list.getItem(indexes[i]);
/*  749 */         Roi roi = (Roi)this.rois.get(label);
/*  750 */         if (!label.endsWith(".roi")) label = label + ".roi";
/*  751 */         zos.putNextEntry(new ZipEntry(label));
/*  752 */         re.write(roi);
/*  753 */         out.flush();
/*      */       }
/*  755 */       out.close();
/*      */     }
/*      */     catch (IOException e) {
/*  758 */       error("" + e);
/*  759 */       return false;
/*      */     }
/*  761 */     if (record()) Recorder.record("roiManager", "Save", path);
/*  762 */     return true;
/*      */   }
/*      */ 
/*      */   boolean measure(int mode) {
/*  766 */     ImagePlus imp = getImage();
/*  767 */     if (imp == null)
/*  768 */       return false;
/*  769 */     int[] indexes = getSelectedIndexes();
/*  770 */     if (indexes.length == 0)
/*  771 */       indexes = getAllIndexes();
/*  772 */     if (indexes.length == 0) return false;
/*  773 */     boolean allSliceOne = true;
/*  774 */     for (int i = 0; i < indexes.length; i++) {
/*  775 */       String label = this.list.getItem(indexes[i]);
/*  776 */       Roi roi = (Roi)this.rois.get(label);
/*  777 */       if (getSliceNumber(roi, label) > 1) allSliceOne = false;
/*      */     }
/*  779 */     int measurements = Analyzer.getMeasurements();
/*  780 */     if (imp.getStackSize() > 1)
/*  781 */       Analyzer.setMeasurements(measurements | 0x100000);
/*  782 */     int currentSlice = imp.getCurrentSlice();
/*  783 */     for (int i = 0; i < indexes.length; i++) {
/*  784 */       if (!restore(getImage(), indexes[i], !allSliceOne)) break;
/*  785 */       IJ.run("Measure");
/*      */     }
/*      */ 
/*  789 */     imp.setSlice(currentSlice);
/*  790 */     Analyzer.setMeasurements(measurements);
/*  791 */     if (indexes.length > 1)
/*  792 */       IJ.run("Select None");
/*  793 */     if (record()) Recorder.record("roiManager", "Measure");
/*  794 */     return true;
/*      */   }
/*      */ 
/*      */   boolean multiMeasure()
/*      */   {
/*  816 */     ImagePlus imp = getImage();
/*  817 */     if (imp == null) return false;
/*  818 */     int[] indexes = getSelectedIndexes();
/*  819 */     if (indexes.length == 0)
/*  820 */       indexes = getAllIndexes();
/*  821 */     if (indexes.length == 0) return false;
/*  822 */     int measurements = Analyzer.getMeasurements();
/*      */ 
/*  824 */     int nSlices = imp.getStackSize();
/*  825 */     if (IJ.isMacro()) {
/*  826 */       if (nSlices > 1) measureAll = true;
/*  827 */       onePerSlice = true;
/*      */     } else {
/*  829 */       GenericDialog gd = new GenericDialog("Multi Measure");
/*  830 */       if (nSlices > 1)
/*  831 */         gd.addCheckbox("Measure All " + nSlices + " Slices", measureAll);
/*  832 */       gd.addCheckbox("One Row Per Slice", onePerSlice);
/*  833 */       int columns = getColumnCount(imp, measurements) * indexes.length;
/*  834 */       String str = nSlices == 1 ? "this option" : "both options";
/*  835 */       gd.setInsets(10, 25, 0);
/*  836 */       gd.addMessage("Enabling " + str + " will result\n" + "in a table with " + columns + " columns.");
/*      */ 
/*  840 */       gd.showDialog();
/*  841 */       if (gd.wasCanceled()) return false;
/*  842 */       if (nSlices > 1)
/*  843 */         measureAll = gd.getNextBoolean();
/*  844 */       onePerSlice = gd.getNextBoolean();
/*      */     }
/*  846 */     if (!measureAll) nSlices = 1;
/*  847 */     int currentSlice = imp.getCurrentSlice();
/*      */ 
/*  849 */     if (!onePerSlice) {
/*  850 */       int measurements2 = nSlices > 1 ? measurements | 0x100000 : measurements;
/*  851 */       ResultsTable rt = new ResultsTable();
/*  852 */       Analyzer analyzer = new Analyzer(imp, measurements2, rt);
/*  853 */       for (int slice = 1; slice <= nSlices; slice++) {
/*  854 */         if (nSlices > 1) imp.setSliceWithoutUpdate(slice);
/*  855 */         for (int i = 0; (i < indexes.length) && 
/*  856 */           (restoreWithoutUpdate(indexes[i])); i++)
/*      */         {
/*  857 */           analyzer.measure();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  862 */       rt.show("Results");
/*  863 */       if (nSlices > 1) imp.setSlice(currentSlice);
/*  864 */       return true;
/*      */     }
/*      */ 
/*  867 */     Analyzer aSys = new Analyzer(imp);
/*  868 */     ResultsTable rtSys = Analyzer.getResultsTable();
/*  869 */     ResultsTable rtMulti = new ResultsTable();
/*  870 */     Analyzer aMulti = new Analyzer(imp, measurements, rtMulti);
/*      */ 
/*  872 */     for (int slice = 1; slice <= nSlices; slice++) {
/*  873 */       int sliceUse = slice;
/*  874 */       if (nSlices == 1) sliceUse = currentSlice;
/*  875 */       imp.setSliceWithoutUpdate(sliceUse);
/*  876 */       rtMulti.incrementCounter();
/*  877 */       int roiIndex = 0;
/*  878 */       for (int i = 0; (i < indexes.length) && 
/*  879 */         (restoreWithoutUpdate(indexes[i])); i++)
/*      */       {
/*  880 */         roiIndex++;
/*  881 */         aSys.measure();
/*  882 */         for (int j = 0; j <= rtSys.getLastColumn(); j++) {
/*  883 */           float[] col = rtSys.getColumn(j);
/*  884 */           String head = rtSys.getColumnHeading(j);
/*  885 */           String suffix = "" + roiIndex;
/*  886 */           Roi roi = imp.getRoi();
/*  887 */           if (roi != null) {
/*  888 */             String name = roi.getName();
/*  889 */             if ((name != null) && (name.length() > 0) && ((name.length() < 9) || (!Character.isDigit(name.charAt(0)))))
/*  890 */               suffix = "(" + name + ")";
/*      */           }
/*  892 */           if ((head != null) && (col != null) && (!head.equals("Slice"))) {
/*  893 */             rtMulti.addValue(head + suffix, rtSys.getValue(j, rtSys.getCounter() - 1));
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  901 */     rtMulti.show("Results");
/*      */ 
/*  903 */     imp.setSlice(currentSlice);
/*  904 */     if (indexes.length > 1)
/*  905 */       IJ.run("Select None");
/*  906 */     if (record()) Recorder.record("roiManager", "Multi Measure");
/*  907 */     return true;
/*      */   }
/*      */ 
/*      */   int getColumnCount(ImagePlus imp, int measurements) {
/*  911 */     ImageStatistics stats = imp.getStatistics(measurements);
/*  912 */     ResultsTable rt = new ResultsTable();
/*  913 */     Analyzer analyzer = new Analyzer(imp, measurements, rt);
/*  914 */     analyzer.saveResults(stats, null);
/*  915 */     int count = 0;
/*  916 */     for (int i = 0; i <= rt.getLastColumn(); i++) {
/*  917 */       float[] col = rt.getColumn(i);
/*  918 */       String head = rt.getColumnHeading(i);
/*  919 */       if ((head != null) && (col != null))
/*  920 */         count++;
/*      */     }
/*  922 */     return count;
/*      */   }
/*      */ 
/*      */   void multiPlot() {
/*  926 */     ImagePlus imp = getImage();
/*  927 */     if (imp == null) return;
/*  928 */     int[] indexes = getSelectedIndexes();
/*  929 */     if (indexes.length == 0) indexes = getAllIndexes();
/*  930 */     int n = indexes.length;
/*  931 */     if (n == 0) return;
/*  932 */     Color[] colors = { Color.blue, Color.green, Color.magenta, Color.red, Color.cyan, Color.yellow };
/*  933 */     if (n > colors.length) {
/*  934 */       colors = new Color[n];
/*  935 */       double c = 0.0D;
/*  936 */       double inc = 150.0D / n;
/*  937 */       for (int i = 0; i < n; i++) {
/*  938 */         colors[i] = new Color((int)c, (int)c, (int)c);
/*  939 */         c += inc;
/*      */       }
/*      */     }
/*  942 */     int currentSlice = imp.getCurrentSlice();
/*  943 */     double[][] x = new double[n][];
/*  944 */     double[][] y = new double[n][];
/*  945 */     double minY = 1.7976931348623157E+308D;
/*  946 */     double maxY = -1.797693134862316E+308D;
/*  947 */     int maxX = 0;
/*  948 */     Calibration cal = imp.getCalibration();
/*  949 */     double xinc = cal.pixelWidth;
/*  950 */     for (int i = 0; (i < indexes.length) && 
/*  951 */       (restore(getImage(), indexes[i], true)); i++)
/*      */     {
/*  952 */       Roi roi = imp.getRoi();
/*  953 */       if (roi == null) break;
/*  954 */       if ((roi.isArea()) && (roi.getType() != 0))
/*  955 */         IJ.run(imp, "Area to Line", "");
/*  956 */       ProfilePlot pp = new ProfilePlot(imp, IJ.altKeyDown());
/*  957 */       y[i] = pp.getProfile();
/*  958 */       if (y[i] == null) break;
/*  959 */       if (y[i].length > maxX) maxX = y[i].length;
/*  960 */       double[] a = Tools.getMinMax(y[i]);
/*  961 */       if (a[0] < minY) minY = a[0];
/*  962 */       if (a[1] > maxY) maxY = a[1];
/*  963 */       double[] xx = new double[y[i].length];
/*  964 */       for (int j = 0; j < xx.length; j++)
/*  965 */         xx[j] = (j * xinc);
/*  966 */       x[i] = xx;
/*      */     }
/*  968 */     String xlabel = "Distance (" + cal.getUnits() + ")";
/*  969 */     Plot plot = new Plot("Profiles", xlabel, "Value", x[0], y[0]);
/*  970 */     plot.setLimits(0.0D, maxX * xinc, minY, maxY);
/*  971 */     for (int i = 1; i < indexes.length; i++) {
/*  972 */       plot.setColor(colors[i]);
/*  973 */       if (x[i] != null)
/*  974 */         plot.addPoints(x[i], y[i], 2);
/*      */     }
/*  976 */     plot.setColor(colors[0]);
/*  977 */     if (x[0] != null)
/*  978 */       plot.show();
/*  979 */     imp.setSlice(currentSlice);
/*  980 */     if (indexes.length > 1)
/*  981 */       IJ.run("Select None");
/*  982 */     if (record()) Recorder.record("roiManager", "Multi Plot"); 
/*      */   }
/*      */ 
/*      */   boolean drawOrFill(int mode)
/*      */   {
/*  986 */     int[] indexes = getSelectedIndexes();
/*  987 */     if (indexes.length == 0)
/*  988 */       indexes = getAllIndexes();
/*  989 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  990 */     imp.killRoi();
/*  991 */     ImageProcessor ip = imp.getProcessor();
/*  992 */     ip.setColor(Toolbar.getForegroundColor());
/*  993 */     ip.snapshot();
/*  994 */     Undo.setup(1, imp);
/*  995 */     Filler filler = mode == 2 ? new Filler() : null;
/*  996 */     int slice = imp.getCurrentSlice();
/*  997 */     for (int i = 0; i < indexes.length; i++) {
/*  998 */       String name = this.list.getItem(indexes[i]);
/*  999 */       Roi roi = (Roi)this.rois.get(name);
/* 1000 */       int type = roi.getType();
/* 1001 */       if (roi != null) {
/* 1002 */         if ((mode == 1) && ((type == 6) || (type == 7) || (type == 8)))
/* 1003 */           mode = 0;
/* 1004 */         int slice2 = getSliceNumber(roi, name);
/* 1005 */         if ((slice2 >= 1) && (slice2 <= imp.getStackSize())) {
/* 1006 */           imp.setSlice(slice2);
/* 1007 */           ip = imp.getProcessor();
/* 1008 */           ip.setColor(Toolbar.getForegroundColor());
/* 1009 */           if (slice2 != slice) Undo.reset();
/*      */         }
/* 1011 */         switch (mode) { case 0:
/* 1012 */           roi.drawPixels(ip); break;
/*      */         case 1:
/* 1013 */           ip.fill(roi); break;
/*      */         case 2:
/* 1015 */           roi.drawPixels(ip);
/* 1016 */           filler.drawLabel(imp, ip, i + 1, roi.getBounds());
/*      */         }
/*      */       }
/*      */     }
/* 1020 */     ImageCanvas ic = imp.getCanvas();
/* 1021 */     if (ic != null) ic.setShowAllROIs(false);
/* 1022 */     imp.updateAndDraw();
/* 1023 */     return true;
/*      */   }
/*      */ 
/*      */   void setProperties(Color color, int lineWidth, Color fillColor) {
/* 1027 */     boolean showDialog = (color == null) && (lineWidth == -1) && (fillColor == null);
/* 1028 */     int[] indexes = getSelectedIndexes();
/* 1029 */     if (indexes.length == 0)
/* 1030 */       indexes = getAllIndexes();
/* 1031 */     int n = indexes.length;
/* 1032 */     if (n == 0) return;
/* 1033 */     Roi rpRoi = null;
/* 1034 */     String rpName = null;
/* 1035 */     Font font = null;
/* 1036 */     int justification = 0;
/* 1037 */     double opacity = -1.0D;
/* 1038 */     if (showDialog) {
/* 1039 */       String label = this.list.getItem(indexes[0]);
/* 1040 */       rpRoi = (Roi)this.rois.get(label);
/* 1041 */       if (n == 1) {
/* 1042 */         fillColor = rpRoi.getFillColor();
/* 1043 */         rpName = rpRoi.getName();
/*      */       }
/* 1045 */       if (rpRoi.getStrokeColor() == null)
/* 1046 */         rpRoi.setStrokeColor(ImageCanvas.getShowAllColor());
/* 1047 */       rpRoi = (Roi)rpRoi.clone();
/* 1048 */       if (n > 1)
/* 1049 */         rpRoi.setName("range: " + (indexes[0] + 1) + "-" + (indexes[(n - 1)] + 1));
/* 1050 */       rpRoi.setFillColor(fillColor);
/* 1051 */       RoiProperties rp = new RoiProperties("Properties", rpRoi);
/* 1052 */       if (!rp.showDialog())
/* 1053 */         return;
/* 1054 */       lineWidth = (int)rpRoi.getStrokeWidth();
/* 1055 */       this.defaultLineWidth = lineWidth;
/* 1056 */       color = rpRoi.getStrokeColor();
/* 1057 */       fillColor = rpRoi.getFillColor();
/* 1058 */       this.defaultColor = color;
/* 1059 */       if ((rpRoi instanceof TextRoi)) {
/* 1060 */         font = ((TextRoi)rpRoi).getCurrentFont();
/* 1061 */         justification = ((TextRoi)rpRoi).getJustification();
/*      */       }
/* 1063 */       if ((rpRoi instanceof ImageRoi))
/* 1064 */         opacity = ((ImageRoi)rpRoi).getOpacity();
/*      */     }
/* 1066 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1067 */     if ((n == this.list.getItemCount()) && (n > 1) && (!IJ.isMacro())) {
/* 1068 */       GenericDialog gd = new GenericDialog("ROI Manager");
/* 1069 */       gd.addMessage("Apply changes to all " + n + " selections?");
/* 1070 */       gd.showDialog();
/* 1071 */       if (gd.wasCanceled()) return;
/*      */     }
/* 1073 */     for (int i = 0; i < n; i++) {
/* 1074 */       String label = this.list.getItem(indexes[i]);
/* 1075 */       Roi roi = (Roi)this.rois.get(label);
/*      */ 
/* 1077 */       if (color != null) roi.setStrokeColor(color);
/* 1078 */       if (lineWidth >= 0) roi.setStrokeWidth(lineWidth);
/* 1079 */       roi.setFillColor(fillColor);
/* 1080 */       if ((roi != null) && ((roi instanceof TextRoi))) {
/* 1081 */         roi.setImage(imp);
/* 1082 */         if (font != null)
/* 1083 */           ((TextRoi)roi).setCurrentFont(font);
/* 1084 */         ((TextRoi)roi).setJustification(justification);
/* 1085 */         roi.setImage(null);
/*      */       }
/* 1087 */       if ((roi != null) && ((roi instanceof ImageRoi)) && (opacity != -1.0D))
/* 1088 */         ((ImageRoi)roi).setOpacity(opacity);
/*      */     }
/* 1090 */     if ((rpRoi != null) && (rpName != null) && (!rpRoi.getName().equals(rpName)))
/* 1091 */       rename(rpRoi.getName());
/* 1092 */     ImageCanvas ic = imp != null ? imp.getCanvas() : null;
/* 1093 */     Roi roi = imp != null ? imp.getRoi() : null;
/* 1094 */     boolean showingAll = (ic != null) && (ic.getShowAllROIs());
/* 1095 */     if ((roi != null) && ((n == 1) || (!showingAll))) {
/* 1096 */       if (lineWidth >= 0) roi.setStrokeWidth(lineWidth);
/* 1097 */       if (color != null) roi.setStrokeColor(color);
/* 1098 */       if (fillColor != null) roi.setFillColor(fillColor);
/* 1099 */       if ((roi != null) && ((roi instanceof TextRoi))) {
/* 1100 */         ((TextRoi)roi).setCurrentFont(font);
/* 1101 */         ((TextRoi)roi).setJustification(justification);
/*      */       }
/* 1103 */       if ((roi != null) && ((roi instanceof ImageRoi)) && (opacity != -1.0D))
/* 1104 */         ((ImageRoi)roi).setOpacity(opacity);
/*      */     }
/* 1106 */     if ((lineWidth > 1) && (!showingAll) && (roi == null)) {
/* 1107 */       showAll(0);
/* 1108 */       showingAll = true;
/*      */     }
/* 1110 */     if (imp != null) imp.draw();
/* 1111 */     if (record())
/* 1112 */       if (fillColor != null) {
/* 1113 */         Recorder.record("roiManager", "Set Fill Color", Colors.colorToString(fillColor));
/*      */       } else {
/* 1115 */         Recorder.record("roiManager", "Set Color", Colors.colorToString(color != null ? color : Color.red));
/* 1116 */         Recorder.record("roiManager", "Set Line Width", lineWidth);
/*      */       }
/*      */   }
/*      */ 
/*      */   void flatten()
/*      */   {
/* 1122 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1123 */     if (imp == null) {
/* 1124 */       IJ.noImage(); return;
/* 1125 */     }ImageCanvas ic = imp.getCanvas();
/* 1126 */     if ((!ic.getShowAllROIs()) && (ic.getDisplayList() == null) && (imp.getRoi() == null))
/* 1127 */       error("Image does not have an overlay or ROI");
/*      */     else
/* 1129 */       IJ.doCommand("Flatten");
/*      */   }
/*      */ 
/*      */   public boolean getDrawLabels() {
/* 1133 */     return this.labelsCheckbox.getState();
/*      */   }
/*      */ 
/*      */   void combine() {
/* 1137 */     ImagePlus imp = getImage();
/* 1138 */     if (imp == null) return;
/* 1139 */     int[] indexes = getSelectedIndexes();
/* 1140 */     if (indexes.length == 1) {
/* 1141 */       error("More than one item must be selected, or none");
/* 1142 */       return;
/*      */     }
/* 1144 */     if (indexes.length == 0)
/* 1145 */       indexes = getAllIndexes();
/* 1146 */     int nPointRois = 0;
/* 1147 */     for (int i = 0; i < indexes.length; i++) {
/* 1148 */       Roi roi = (Roi)this.rois.get(this.list.getItem(indexes[i]));
/* 1149 */       if (roi.getType() != 10) break;
/* 1150 */       nPointRois++;
/*      */     }
/*      */ 
/* 1154 */     if (nPointRois == indexes.length)
/* 1155 */       combinePoints(imp, indexes);
/*      */     else
/* 1157 */       combineRois(imp, indexes);
/* 1158 */     if (record()) Recorder.record("roiManager", "Combine"); 
/*      */   }
/*      */ 
/*      */   void combineRois(ImagePlus imp, int[] indexes)
/*      */   {
/* 1162 */     ShapeRoi s1 = null; ShapeRoi s2 = null;
/* 1163 */     for (int i = 0; i < indexes.length; i++) {
/* 1164 */       Roi roi = (Roi)this.rois.get(this.list.getItem(indexes[i]));
/* 1165 */       if ((!roi.isLine()) && (roi.getType() != 10))
/*      */       {
/* 1167 */         if (s1 == null) {
/* 1168 */           if ((roi instanceof ShapeRoi))
/* 1169 */             s1 = (ShapeRoi)roi;
/*      */           else
/* 1171 */             s1 = new ShapeRoi(roi);
/* 1172 */           if (s1 != null);
/*      */         } else {
/* 1174 */           if ((roi instanceof ShapeRoi))
/* 1175 */             s2 = (ShapeRoi)roi;
/*      */           else
/* 1177 */             s2 = new ShapeRoi(roi);
/* 1178 */           if ((s2 != null) && 
/* 1179 */             (roi.isArea()))
/* 1180 */             s1.or(s2); 
/*      */         }
/*      */       }
/*      */     }
/* 1183 */     if (s1 != null)
/* 1184 */       imp.setRoi(s1);
/*      */   }
/*      */ 
/*      */   void combinePoints(ImagePlus imp, int[] indexes) {
/* 1188 */     int n = indexes.length;
/* 1189 */     Polygon[] p = new Polygon[n];
/* 1190 */     int points = 0;
/* 1191 */     for (int i = 0; i < n; i++) {
/* 1192 */       Roi roi = (Roi)this.rois.get(this.list.getItem(indexes[i]));
/* 1193 */       p[i] = roi.getPolygon();
/* 1194 */       points += p[i].npoints;
/*      */     }
/* 1196 */     if (points == 0) return;
/* 1197 */     int[] xpoints = new int[points];
/* 1198 */     int[] ypoints = new int[points];
/* 1199 */     int index = 0;
/* 1200 */     for (int i = 0; i < p.length; i++) {
/* 1201 */       for (int j = 0; j < p[i].npoints; j++) {
/* 1202 */         xpoints[index] = p[i].xpoints[j];
/* 1203 */         ypoints[index] = p[i].ypoints[j];
/* 1204 */         index++;
/*      */       }
/*      */     }
/* 1207 */     imp.setRoi(new PointRoi(xpoints, ypoints, xpoints.length));
/*      */   }
/*      */ 
/*      */   void and() {
/* 1211 */     ImagePlus imp = getImage();
/* 1212 */     if (imp == null) return;
/* 1213 */     int[] indexes = getSelectedIndexes();
/* 1214 */     if (indexes.length == 1) {
/* 1215 */       error("More than one item must be selected, or none");
/* 1216 */       return;
/*      */     }
/* 1218 */     if (indexes.length == 0)
/* 1219 */       indexes = getAllIndexes();
/* 1220 */     ShapeRoi s1 = null; ShapeRoi s2 = null;
/* 1221 */     for (int i = 0; i < indexes.length; i++) {
/* 1222 */       Roi roi = (Roi)this.rois.get(this.list.getItem(indexes[i]));
/* 1223 */       if (roi.isArea())
/* 1224 */         if (s1 == null) {
/* 1225 */           if ((roi instanceof ShapeRoi))
/* 1226 */             s1 = (ShapeRoi)roi.clone();
/*      */           else
/* 1228 */             s1 = new ShapeRoi(roi);
/* 1229 */           if (s1 != null);
/*      */         } else {
/* 1231 */           if ((roi instanceof ShapeRoi))
/* 1232 */             s2 = (ShapeRoi)roi.clone();
/*      */           else
/* 1234 */             s2 = new ShapeRoi(roi);
/* 1235 */           if (s2 != null)
/* 1236 */             s1.and(s2);
/*      */         }
/*      */     }
/* 1239 */     if (s1 != null) imp.setRoi(s1);
/* 1240 */     if (record()) Recorder.record("roiManager", "AND"); 
/*      */   }
/*      */ 
/*      */   void xor()
/*      */   {
/* 1244 */     ImagePlus imp = getImage();
/* 1245 */     if (imp == null) return;
/* 1246 */     int[] indexes = getSelectedIndexes();
/* 1247 */     if (indexes.length == 1) {
/* 1248 */       error("More than one item must be selected, or none");
/* 1249 */       return;
/*      */     }
/* 1251 */     if (indexes.length == 0)
/* 1252 */       indexes = getAllIndexes();
/* 1253 */     ShapeRoi s1 = null; ShapeRoi s2 = null;
/* 1254 */     for (int i = 0; i < indexes.length; i++) {
/* 1255 */       Roi roi = (Roi)this.rois.get(this.list.getItem(indexes[i]));
/* 1256 */       if (roi.isArea())
/* 1257 */         if (s1 == null) {
/* 1258 */           if ((roi instanceof ShapeRoi))
/* 1259 */             s1 = (ShapeRoi)roi.clone();
/*      */           else
/* 1261 */             s1 = new ShapeRoi(roi);
/* 1262 */           if (s1 != null);
/*      */         } else {
/* 1264 */           if ((roi instanceof ShapeRoi))
/* 1265 */             s2 = (ShapeRoi)roi.clone();
/*      */           else
/* 1267 */             s2 = new ShapeRoi(roi);
/* 1268 */           if (s2 != null)
/* 1269 */             s1.xor(s2);
/*      */         }
/*      */     }
/* 1272 */     if (s1 != null) imp.setRoi(s1);
/* 1273 */     if (record()) Recorder.record("roiManager", "XOR"); 
/*      */   }
/*      */ 
/*      */   void addParticles()
/*      */   {
/* 1277 */     String err = IJ.runMacroFile("ij.jar:AddParticles", null);
/* 1278 */     if ((err != null) && (err.length() > 0))
/* 1279 */       error(err);
/*      */   }
/*      */ 
/*      */   void sort() {
/* 1283 */     int n = this.rois.size();
/* 1284 */     if (n == 0) return;
/* 1285 */     String[] labels = new String[n];
/* 1286 */     int index = 0;
/* 1287 */     for (Enumeration en = this.rois.keys(); en.hasMoreElements(); )
/* 1288 */       labels[(index++)] = ((String)en.nextElement());
/* 1289 */     this.list.removeAll();
/* 1290 */     StringSorter.sort(labels);
/* 1291 */     for (int i = 0; i < labels.length; i++)
/* 1292 */       this.list.add(labels[i]);
/* 1293 */     if (record()) Recorder.record("roiManager", "Sort"); 
/*      */   }
/*      */ 
/*      */   void specify() {
/*      */     try { IJ.run("Specify..."); } catch (Exception e) {
/* 1298 */       return;
/* 1299 */     }runCommand("add");
/*      */   }
/*      */ 
/*      */   void removeSliceInfo() {
/* 1303 */     int[] indexes = getSelectedIndexes();
/* 1304 */     if (indexes.length == 0)
/* 1305 */       indexes = getAllIndexes();
/* 1306 */     for (int i = 0; i < indexes.length; i++) {
/* 1307 */       int index = indexes[i];
/* 1308 */       String name = this.list.getItem(index);
/* 1309 */       int n = getSliceNumber(name);
/* 1310 */       if (n != -1) {
/* 1311 */         String name2 = name.substring(5, name.length());
/* 1312 */         name2 = getUniqueName(name2);
/* 1313 */         Roi roi = (Roi)this.rois.get(name);
/* 1314 */         this.rois.remove(name);
/* 1315 */         roi.setName(name2);
/* 1316 */         roi.setPosition(0);
/* 1317 */         this.rois.put(name2, roi);
/* 1318 */         this.list.replaceItem(name2, index);
/*      */       }
/*      */     }
/* 1320 */     if (record()) Recorder.record("roiManager", "Remove Slice Info"); 
/*      */   }
/*      */ 
/*      */   void help()
/*      */   {
/* 1324 */     String macro = "run('URL...', 'url=http://imagej.nih.gov/ij/docs/menus/analyze.html#manager');";
/* 1325 */     new MacroRunner(macro);
/*      */   }
/*      */ 
/*      */   void options() {
/* 1329 */     Color c = ImageCanvas.getShowAllColor();
/* 1330 */     GenericDialog gd = new GenericDialog("Options");
/* 1331 */     gd.addPanel(makeButtonPanel(gd), 10, new Insets(5, 0, 0, 0));
/* 1332 */     gd.addCheckbox("Associate \"Show All\" ROIs with slices", Prefs.showAllSliceOnly);
/* 1333 */     gd.addCheckbox("Restore ROIs centered", restoreCentered);
/* 1334 */     gd.addCheckbox("Use ROI names as labels", Prefs.useNamesAsLabels);
/* 1335 */     gd.showDialog();
/* 1336 */     if (gd.wasCanceled()) {
/* 1337 */       if (c != ImageCanvas.getShowAllColor())
/* 1338 */         ImageCanvas.setShowAllColor(c);
/* 1339 */       return;
/*      */     }
/* 1341 */     Prefs.showAllSliceOnly = gd.getNextBoolean();
/* 1342 */     restoreCentered = gd.getNextBoolean();
/* 1343 */     Prefs.useNamesAsLabels = gd.getNextBoolean();
/* 1344 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1345 */     if (imp != null) imp.draw();
/* 1346 */     if (record()) {
/* 1347 */       Recorder.record("roiManager", "Associate", Prefs.showAllSliceOnly ? "true" : "false");
/* 1348 */       Recorder.record("roiManager", "Centered", restoreCentered ? "true" : "false");
/* 1349 */       Recorder.record("roiManager", "UseNames", Prefs.useNamesAsLabels ? "true" : "false");
/*      */     }
/*      */   }
/*      */ 
/*      */   Panel makeButtonPanel(GenericDialog gd) {
/* 1354 */     Panel panel = new Panel();
/*      */ 
/* 1356 */     this.colorButton = new Button("\"Show All\" Color...");
/* 1357 */     this.colorButton.addActionListener(this);
/* 1358 */     panel.add(this.colorButton);
/* 1359 */     return panel;
/*      */   }
/*      */ 
/*      */   void setShowAllColor() {
/* 1363 */     ColorChooser cc = new ColorChooser("\"Show All\" Color", ImageCanvas.getShowAllColor(), false);
/* 1364 */     ImageCanvas.setShowAllColor(cc.getColor());
/*      */   }
/*      */ 
/*      */   void split() {
/* 1368 */     ImagePlus imp = getImage();
/* 1369 */     if (imp == null) return;
/* 1370 */     Roi roi = imp.getRoi();
/* 1371 */     if ((roi == null) || (roi.getType() != 9)) {
/* 1372 */       error("Image with composite selection required");
/* 1373 */       return;
/*      */     }
/* 1375 */     boolean record = Recorder.record;
/* 1376 */     Recorder.record = false;
/* 1377 */     Roi[] rois = ((ShapeRoi)roi).getRois();
/* 1378 */     for (int i = 0; i < rois.length; i++) {
/* 1379 */       imp.setRoi(rois[i]);
/* 1380 */       addRoi(false);
/*      */     }
/* 1382 */     Recorder.record = record;
/* 1383 */     if (record()) Recorder.record("roiManager", "Split"); 
/*      */   }
/*      */ 
/*      */   void showAll(int mode)
/*      */   {
/* 1387 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1388 */     if (imp == null) {
/* 1389 */       error("There are no images open."); return;
/* 1390 */     }ImageCanvas ic = imp.getCanvas();
/* 1391 */     if (ic == null) return;
/* 1392 */     boolean showAll = mode == 0;
/* 1393 */     if (mode == 2) {
/* 1394 */       showAll = true;
/* 1395 */       if (record())
/* 1396 */         Recorder.record("roiManager", "Show All with labels");
/* 1397 */     } else if (mode == 3) {
/* 1398 */       showAll = true;
/* 1399 */       if (record())
/* 1400 */         Recorder.record("roiManager", "Show All without labels");
/*      */     }
/* 1402 */     if (showAll) imp.killRoi();
/* 1403 */     ic.setShowAllROIs(showAll);
/* 1404 */     if (record())
/* 1405 */       Recorder.record("roiManager", showAll ? "Show All" : "Show None");
/* 1406 */     imp.draw();
/*      */   }
/*      */ 
/*      */   void updateShowAll() {
/* 1410 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1411 */     if (imp == null) return;
/* 1412 */     ImageCanvas ic = imp.getCanvas();
/* 1413 */     if ((ic != null) && (ic.getShowAllROIs()))
/* 1414 */       imp.draw();
/*      */   }
/*      */ 
/*      */   int[] getAllIndexes() {
/* 1418 */     int count = this.list.getItemCount();
/* 1419 */     int[] indexes = new int[count];
/* 1420 */     for (int i = 0; i < count; i++)
/* 1421 */       indexes[i] = i;
/* 1422 */     return indexes;
/*      */   }
/*      */ 
/*      */   ImagePlus getImage() {
/* 1426 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1427 */     if (imp == null) {
/* 1428 */       error("There are no images open.");
/* 1429 */       return null;
/*      */     }
/* 1431 */     return imp;
/*      */   }
/*      */ 
/*      */   boolean error(String msg) {
/* 1435 */     new MessageDialog(this, "ROI Manager", msg);
/* 1436 */     Macro.abort();
/* 1437 */     return false;
/*      */   }
/*      */ 
/*      */   public void processWindowEvent(WindowEvent e) {
/* 1441 */     super.processWindowEvent(e);
/* 1442 */     if (e.getID() == 201) {
/* 1443 */       instance = null;
/*      */     }
/* 1445 */     if (!IJ.isMacro())
/* 1446 */       this.ignoreInterrupts = false;
/*      */   }
/*      */ 
/*      */   public static RoiManager getInstance()
/*      */   {
/* 1452 */     return (RoiManager)instance;
/*      */   }
/*      */ 
/*      */   public static RoiManager getInstance2()
/*      */   {
/* 1458 */     RoiManager rm = getInstance();
/* 1459 */     if ((rm == null) && (IJ.isMacro()))
/* 1460 */       rm = Interpreter.getBatchModeRoiManager();
/* 1461 */     return rm;
/*      */   }
/*      */ 
/*      */   public Hashtable getROIs()
/*      */   {
/* 1471 */     return this.rois;
/*      */   }
/*      */ 
/*      */   public List getList()
/*      */   {
/* 1479 */     return this.list;
/*      */   }
/*      */ 
/*      */   public int getCount()
/*      */   {
/* 1484 */     return this.list.getItemCount();
/*      */   }
/*      */ 
/*      */   public Roi[] getRoisAsArray()
/*      */   {
/* 1489 */     int n = this.list.getItemCount();
/* 1490 */     Roi[] array = new Roi[n];
/* 1491 */     for (int i = 0; i < n; i++) {
/* 1492 */       String label = this.list.getItem(i);
/* 1493 */       array[i] = ((Roi)this.rois.get(label));
/*      */     }
/* 1495 */     return array;
/*      */   }
/*      */ 
/*      */   public Roi[] getSelectedRoisAsArray()
/*      */   {
/* 1501 */     int[] indexes = getSelectedIndexes();
/* 1502 */     if (indexes.length == 0)
/* 1503 */       indexes = getAllIndexes();
/* 1504 */     int n = indexes.length;
/* 1505 */     Roi[] array = new Roi[n];
/* 1506 */     for (int i = 0; i < n; i++) {
/* 1507 */       String label = this.list.getItem(indexes[i]);
/* 1508 */       array[i] = ((Roi)this.rois.get(label));
/*      */     }
/* 1510 */     return array;
/*      */   }
/*      */ 
/*      */   public String getName(int index)
/*      */   {
/* 1516 */     if ((index >= 0) && (index < this.list.getItemCount())) {
/* 1517 */       return this.list.getItem(index);
/*      */     }
/* 1519 */     return null;
/*      */   }
/*      */ 
/*      */   public static String getName(String index)
/*      */   {
/* 1529 */     int i = (int)Tools.parseDouble(index, -1.0D);
/* 1530 */     RoiManager instance = getInstance();
/* 1531 */     if ((instance != null) && (i >= 0) && (i < instance.list.getItemCount())) {
/* 1532 */       return instance.list.getItem(i);
/*      */     }
/* 1534 */     return "null";
/*      */   }
/*      */ 
/*      */   public boolean runCommand(String cmd)
/*      */   {
/* 1542 */     cmd = cmd.toLowerCase();
/* 1543 */     this.macro = true;
/* 1544 */     boolean ok = true;
/* 1545 */     if (cmd.equals("add")) {
/* 1546 */       boolean shift = IJ.shiftKeyDown();
/* 1547 */       boolean alt = IJ.altKeyDown();
/* 1548 */       if (Interpreter.isBatchMode()) {
/* 1549 */         shift = false;
/* 1550 */         alt = false;
/*      */       }
/* 1552 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 1553 */       Roi roi = imp != null ? imp.getRoi() : null;
/* 1554 */       if (roi != null) roi.setPosition(0);
/* 1555 */       add(shift, alt);
/* 1556 */     } else if (cmd.equals("add & draw")) {
/* 1557 */       addAndDraw(false);
/* 1558 */     } else if (cmd.equals("update")) {
/* 1559 */       update(true);
/* 1560 */     } else if (cmd.equals("update2")) {
/* 1561 */       update(false);
/* 1562 */     } else if (cmd.equals("delete")) {
/* 1563 */       delete(false);
/* 1564 */     } else if (cmd.equals("measure")) {
/* 1565 */       measure(1);
/* 1566 */     } else if (cmd.equals("draw")) {
/* 1567 */       drawOrFill(0);
/* 1568 */     } else if (cmd.equals("fill")) {
/* 1569 */       drawOrFill(1);
/* 1570 */     } else if (cmd.equals("label")) {
/* 1571 */       drawOrFill(2);
/* 1572 */     } else if (cmd.equals("and")) {
/* 1573 */       and();
/* 1574 */     } else if ((cmd.equals("or")) || (cmd.equals("combine"))) {
/* 1575 */       combine();
/* 1576 */     } else if (cmd.equals("xor")) {
/* 1577 */       xor();
/* 1578 */     } else if (cmd.equals("split")) {
/* 1579 */       split();
/* 1580 */     } else if (cmd.equals("sort")) {
/* 1581 */       sort();
/* 1582 */     } else if (cmd.equals("multi measure")) {
/* 1583 */       multiMeasure();
/* 1584 */     } else if (cmd.equals("multi plot")) {
/* 1585 */       multiPlot();
/* 1586 */     } else if (cmd.equals("show all")) {
/* 1587 */       if (WindowManager.getCurrentImage() != null) {
/* 1588 */         showAll(0);
/* 1589 */         this.showAllCheckbox.setState(true);
/*      */       }
/* 1591 */     } else if (cmd.equals("show none")) {
/* 1592 */       if (WindowManager.getCurrentImage() != null) {
/* 1593 */         showAll(1);
/* 1594 */         this.showAllCheckbox.setState(false);
/*      */       }
/* 1596 */     } else if (cmd.equals("show all with labels")) {
/* 1597 */       this.labelsCheckbox.setState(true);
/* 1598 */       showAll(2);
/* 1599 */       if (Interpreter.isBatchMode()) IJ.wait(250); 
/*      */     }
/* 1600 */     else if (cmd.equals("show all without labels")) {
/* 1601 */       this.labelsCheckbox.setState(false);
/* 1602 */       showAll(3);
/* 1603 */       if (Interpreter.isBatchMode()) IJ.wait(250); 
/*      */     }
/* 1604 */     else if ((cmd.equals("deselect")) || (cmd.indexOf("all") != -1)) {
/* 1605 */       if (IJ.isMacOSX()) this.ignoreInterrupts = true;
/* 1606 */       select(-1);
/* 1607 */       IJ.wait(50);
/* 1608 */     } else if (cmd.equals("reset")) {
/* 1609 */       if ((IJ.isMacOSX()) && (IJ.isMacro()))
/* 1610 */         this.ignoreInterrupts = true;
/* 1611 */       this.list.removeAll();
/* 1612 */       this.rois.clear();
/* 1613 */       updateShowAll();
/* 1614 */     } else if (!cmd.equals("debug"))
/*      */     {
/* 1618 */       if (cmd.equals("enable interrupts"))
/* 1619 */         this.ignoreInterrupts = false;
/* 1620 */       else if (cmd.equals("remove slice info"))
/* 1621 */         removeSliceInfo();
/*      */       else
/* 1623 */         ok = false; 
/*      */     }
/* 1624 */     this.macro = false;
/* 1625 */     return ok;
/*      */   }
/*      */ 
/*      */   public boolean runCommand(String cmd, String name)
/*      */   {
/* 1631 */     cmd = cmd.toLowerCase();
/* 1632 */     this.macro = true;
/* 1633 */     if (cmd.equals("open")) {
/* 1634 */       open(name);
/* 1635 */       this.macro = false;
/* 1636 */       return true;
/* 1637 */     }if (cmd.equals("save")) {
/* 1638 */       if ((!name.endsWith(".zip")) && (!name.equals("")))
/* 1639 */         return error("Name must end with '.zip'");
/* 1640 */       if (this.list.getItemCount() == 0)
/* 1641 */         return error("The selection list is empty.");
/* 1642 */       int[] indexes = getAllIndexes();
/* 1643 */       boolean ok = false;
/* 1644 */       if (name.equals(""))
/* 1645 */         ok = saveMultiple(indexes, null);
/*      */       else
/* 1647 */         ok = saveMultiple(indexes, name);
/* 1648 */       this.macro = false;
/* 1649 */       return ok;
/* 1650 */     }if (cmd.equals("rename")) {
/* 1651 */       rename(name);
/* 1652 */       this.macro = false;
/* 1653 */       return true;
/* 1654 */     }if (cmd.equals("set color")) {
/* 1655 */       Color color = Colors.decode(name, Color.cyan);
/* 1656 */       setProperties(color, -1, null);
/* 1657 */       this.macro = false;
/* 1658 */       return true;
/* 1659 */     }if (cmd.equals("set fill color")) {
/* 1660 */       Color fillColor = Colors.decode(name, Color.cyan);
/* 1661 */       setProperties(null, -1, fillColor);
/* 1662 */       this.macro = false;
/* 1663 */       return true;
/* 1664 */     }if (cmd.equals("set line width")) {
/* 1665 */       int lineWidth = (int)Tools.parseDouble(name, 0.0D);
/* 1666 */       if (lineWidth >= 0)
/* 1667 */         setProperties(null, lineWidth, null);
/* 1668 */       this.macro = false;
/* 1669 */       return true;
/* 1670 */     }if (cmd.equals("associate")) {
/* 1671 */       Prefs.showAllSliceOnly = name.equals("true");
/* 1672 */       this.macro = false;
/* 1673 */       return true;
/* 1674 */     }if (cmd.equals("centered")) {
/* 1675 */       restoreCentered = name.equals("true");
/* 1676 */       this.macro = false;
/* 1677 */       return true;
/* 1678 */     }if (cmd.equals("usenames")) {
/* 1679 */       Prefs.useNamesAsLabels = name.equals("true");
/* 1680 */       this.macro = false;
/* 1681 */       if (this.labelsCheckbox.getState()) {
/* 1682 */         ImagePlus imp = WindowManager.getCurrentImage();
/* 1683 */         if (imp != null) imp.draw();
/*      */       }
/* 1685 */       return true;
/*      */     }
/* 1687 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean runCommand(String cmd, String hexColor, double lineWidth)
/*      */   {
/* 1693 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1694 */     Roi roi = imp != null ? imp.getRoi() : null;
/* 1695 */     if (roi != null) roi.setPosition(0);
/* 1696 */     if ((hexColor == null) && (lineWidth == 1.0D) && (IJ.altKeyDown()) && (!Interpreter.isBatchMode())) {
/* 1697 */       addRoi(true);
/*      */     } else {
/* 1699 */       Color color = hexColor != null ? Colors.decode(hexColor, Color.cyan) : null;
/* 1700 */       addRoi(null, false, color, (int)Math.round(lineWidth));
/*      */     }
/* 1702 */     return true;
/*      */   }
/*      */ 
/*      */   public void select(int index)
/*      */   {
/* 1707 */     select(null, index);
/*      */   }
/*      */ 
/*      */   public void select(ImagePlus imp, int index)
/*      */   {
/* 1712 */     this.selectedIndexes = null;
/* 1713 */     int n = this.list.getItemCount();
/* 1714 */     if (index < 0) {
/* 1715 */       for (int i = 0; i < n; i++)
/* 1716 */         this.list.deselect(i);
/* 1717 */       if (record()) Recorder.record("roiManager", "Deselect");
/* 1718 */       return;
/*      */     }
/* 1720 */     if (index >= n) return;
/* 1721 */     boolean mm = this.list.isMultipleMode();
/* 1722 */     if (mm) this.list.setMultipleMode(false);
/* 1723 */     int delay = 1;
/* 1724 */     long start = System.currentTimeMillis();
/*      */     while (true) {
/* 1726 */       this.list.select(index);
/* 1727 */       if (delay > 1) IJ.wait(delay);
/* 1728 */       if (this.list.isIndexSelected(index))
/*      */         break;
/* 1730 */       for (int i = 0; i < n; i++)
/* 1731 */         if (this.list.isSelected(i)) this.list.deselect(i);
/* 1732 */       IJ.wait(delay);
/* 1733 */       delay *= 2; if (delay > 32) delay = 32;
/* 1734 */       if (System.currentTimeMillis() - start > 1000L)
/* 1735 */         error("Failed to select ROI " + index);
/*      */     }
/* 1737 */     if (imp == null) imp = getImage();
/* 1738 */     restore(imp, index, true);
/* 1739 */     if (mm) this.list.setMultipleMode(true); 
/*      */   }
/*      */ 
/*      */   public void select(int index, boolean shiftKeyDown, boolean altKeyDown)
/*      */   {
/* 1743 */     if ((!shiftKeyDown) && (!altKeyDown))
/* 1744 */       select(index);
/* 1745 */     ImagePlus imp = IJ.getImage();
/* 1746 */     if (imp == null) return;
/* 1747 */     Roi previousRoi = imp.getRoi();
/* 1748 */     if (previousRoi == null) {
/* 1749 */       select(index); return;
/* 1750 */     }Roi.previousRoi = (Roi)previousRoi.clone();
/* 1751 */     String label = this.list.getItem(index);
/* 1752 */     Roi roi = (Roi)this.rois.get(label);
/* 1753 */     if (roi != null) {
/* 1754 */       roi.setImage(imp);
/* 1755 */       roi.update(shiftKeyDown, altKeyDown);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setEditMode(ImagePlus imp, boolean editMode) {
/* 1760 */     ImageCanvas ic = imp.getCanvas();
/* 1761 */     boolean showAll = false;
/* 1762 */     if (ic != null) {
/* 1763 */       showAll = ic.getShowAllROIs() | editMode;
/* 1764 */       ic.setShowAllROIs(showAll);
/* 1765 */       imp.draw();
/*      */     }
/* 1767 */     this.showAllCheckbox.setState(showAll);
/* 1768 */     this.labelsCheckbox.setState(editMode);
/*      */   }
/*      */ 
/*      */   public void close()
/*      */   {
/* 1790 */     super.close();
/* 1791 */     instance = null;
/* 1792 */     Prefs.saveLocation("manager.loc", getLocation());
/*      */   }
/*      */ 
/*      */   public void mousePressed(MouseEvent e) {
/* 1796 */     int x = e.getX(); int y = e.getY();
/* 1797 */     if ((e.isPopupTrigger()) || (e.isMetaDown()))
/* 1798 */       this.pm.show(e.getComponent(), x, y);
/*      */   }
/*      */ 
/*      */   public void mouseWheelMoved(MouseWheelEvent event) {
/* 1802 */     synchronized (this) {
/* 1803 */       int index = this.list.getSelectedIndex();
/* 1804 */       int rot = event.getWheelRotation();
/* 1805 */       if (rot < -1) rot = -1;
/* 1806 */       if (rot > 1) rot = 1;
/* 1807 */       index += rot;
/* 1808 */       if (index < 0) index = 0;
/* 1809 */       if (index >= this.list.getItemCount()) index = this.list.getItemCount();
/*      */ 
/* 1811 */       select(index);
/* 1812 */       if (IJ.isWindows())
/* 1813 */         this.list.requestFocusInWindow();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setSelectedIndexes(int[] indexes)
/*      */   {
/* 1823 */     int count = getCount();
/* 1824 */     if (count == 0) return;
/* 1825 */     for (int i = 0; i < indexes.length; i++) {
/* 1826 */       if (indexes[i] < 0) indexes[i] = 0;
/* 1827 */       if (indexes[i] >= count) indexes[i] = (count - 1);
/*      */     }
/* 1829 */     this.selectedIndexes = indexes;
/*      */   }
/*      */ 
/*      */   private int[] getSelectedIndexes() {
/* 1833 */     if (this.selectedIndexes != null) {
/* 1834 */       int[] indexes = this.selectedIndexes;
/* 1835 */       this.selectedIndexes = null;
/* 1836 */       return indexes;
/*      */     }
/* 1838 */     return this.list.getSelectedIndexes();
/*      */   }
/*      */ 
/*      */   private boolean record() {
/* 1842 */     return (Recorder.record) && (!IJ.isMacro());
/*      */   }
/*      */ 
/*      */   public void mouseReleased(MouseEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void mouseClicked(MouseEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void mouseEntered(MouseEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void mouseExited(MouseEvent e)
/*      */   {
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.RoiManager
 * JD-Core Version:    0.6.2
 */