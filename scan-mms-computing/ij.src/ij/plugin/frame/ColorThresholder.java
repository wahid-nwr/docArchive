/*      */ package ij.plugin.frame;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImageJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.ImageStack;
/*      */ import ij.Prefs;
/*      */ import ij.Undo;
/*      */ import ij.WindowManager;
/*      */ import ij.gui.GUI;
/*      */ import ij.gui.Roi;
/*      */ import ij.gui.TrimmedButton;
/*      */ import ij.measure.Measurements;
/*      */ import ij.plugin.PlugIn;
/*      */ import ij.process.AutoThresholder;
/*      */ import ij.process.ByteProcessor;
/*      */ import ij.process.ColorProcessor;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.process.ImageStatistics;
/*      */ import java.awt.Button;
/*      */ import java.awt.Canvas;
/*      */ import java.awt.Checkbox;
/*      */ import java.awt.CheckboxGroup;
/*      */ import java.awt.Choice;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Label;
/*      */ import java.awt.Panel;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Scrollbar;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.AdjustmentEvent;
/*      */ import java.awt.event.AdjustmentListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.IndexColorModel;
/*      */ 
/*      */ public class ColorThresholder extends PlugInFrame
/*      */   implements PlugIn, Measurements, ActionListener, AdjustmentListener, FocusListener, ItemListener, Runnable
/*      */ {
/*      */   private static final int HSB = 0;
/*      */   private static final int RGB = 1;
/*      */   private static final int LAB = 2;
/*      */   private static final int YUV = 3;
/*   49 */   private static final String[] colorSpaces = { "HSB", "RGB", "Lab", "YUV" };
/*   50 */   private boolean flag = false;
/*   51 */   private int colorSpace = 0;
/*      */   private Thread thread;
/*      */   private static Frame instance;
/*   55 */   private BandPlot plot = new BandPlot();
/*   56 */   private BandPlot splot = new BandPlot();
/*   57 */   private BandPlot bplot = new BandPlot();
/*   58 */   private int sliderRange = 256;
/*      */   private Panel panel;
/*      */   private Panel panelt;
/*      */   private Button originalB;
/*      */   private Button filteredB;
/*      */   private Button stackB;
/*      */   private Button helpB;
/*      */   private Button sampleB;
/*      */   private Button resetallB;
/*      */   private Button newB;
/*      */   private Button macroB;
/*      */   private Button selectB;
/*      */   private Checkbox bandPassH;
/*      */   private Checkbox bandPassS;
/*      */   private Checkbox bandPassB;
/*      */   private Checkbox darkBackground;
/*      */   private CheckboxGroup colourMode;
/*      */   private Choice colorSpaceChoice;
/*      */   private Choice methodChoice;
/*      */   private Choice modeChoice;
/*   64 */   private int previousImageID = -1;
/*   65 */   private int previousSlice = -1;
/*      */   private ImageJ ij;
/*   67 */   private int minHue = 0; private int minSat = 0; private int minBri = 0;
/*   68 */   private int maxHue = 255; private int maxSat = 255; private int maxBri = 255;
/*      */   private Scrollbar minSlider;
/*      */   private Scrollbar maxSlider;
/*      */   private Scrollbar minSlider2;
/*      */   private Scrollbar maxSlider2;
/*      */   private Scrollbar minSlider3;
/*      */   private Scrollbar maxSlider3;
/*      */   private Label label1;
/*      */   private Label label2;
/*      */   private Label label3;
/*      */   private Label label4;
/*      */   private Label label5;
/*      */   private Label label6;
/*      */   private Label labelh;
/*      */   private Label labels;
/*      */   private Label labelb;
/*      */   private Label labelf;
/*      */   private boolean done;
/*      */   private byte[] hSource;
/*      */   private byte[] sSource;
/*      */   private byte[] bSource;
/*      */   private boolean applyingStack;
/*      */   private static final int DEFAULT = 0;
/*   76 */   private static String[] methodNames = AutoThresholder.getMethods();
/*   77 */   private static String method = methodNames[0];
/*   78 */   private static AutoThresholder thresholder = new AutoThresholder();
/*      */   private static final int RED = 0;
/*      */   private static final int WHITE = 1;
/*      */   private static final int BLACK = 2;
/*      */   private static final int BLACK_AND_WHITE = 3;
/*   80 */   private static final String[] modes = { "Red", "White", "Black", "B&W" };
/*   81 */   private static int mode = 0;
/*      */   private int numSlices;
/*      */   private ImageStack stack;
/*      */   private int width;
/*      */   private int height;
/*      */   private int numPixels;
/*      */ 
/*      */   public ColorThresholder()
/*      */   {
/*   88 */     super("Threshold Color (experimental)");
/*   89 */     if (instance != null) {
/*   90 */       WindowManager.toFront(instance);
/*   91 */       return;
/*      */     }
/*   93 */     this.thread = new Thread(this, "BandAdjuster");
/*   94 */     WindowManager.addWindow(this);
/*   95 */     instance = this;
/*      */ 
/*   97 */     this.ij = IJ.getInstance();
/*   98 */     Font font = new Font("SansSerif", 0, 10);
/*   99 */     GridBagLayout gridbag = new GridBagLayout();
/*  100 */     GridBagConstraints c = new GridBagConstraints();
/*  101 */     setLayout(gridbag);
/*      */ 
/*  103 */     int y = 0;
/*  104 */     c.gridx = 0;
/*  105 */     c.gridy = y;
/*  106 */     c.gridwidth = 1;
/*  107 */     c.weightx = 0.0D;
/*  108 */     c.insets = new Insets(5, 0, 0, 0);
/*  109 */     this.labelh = new Label("Hue", 1);
/*  110 */     add(this.labelh, c);
/*      */ 
/*  112 */     c.gridx = 1;
/*  113 */     c.gridy = (y++);
/*  114 */     c.gridwidth = 1;
/*  115 */     c.weightx = 0.0D;
/*  116 */     c.insets = new Insets(7, 0, 0, 0);
/*  117 */     this.labelf = new Label("", 2);
/*  118 */     add(this.labelf, c);
/*      */ 
/*  121 */     c.gridx = 0;
/*  122 */     c.gridy = y;
/*  123 */     c.gridwidth = 1;
/*  124 */     c.fill = 1;
/*  125 */     c.anchor = 10;
/*  126 */     c.insets = new Insets(0, 5, 0, 0);
/*  127 */     add(this.plot, c);
/*      */ 
/*  130 */     this.bandPassH = new Checkbox("Pass");
/*  131 */     this.bandPassH.addItemListener(this);
/*  132 */     this.bandPassH.setState(true);
/*  133 */     c.gridx = 1;
/*  134 */     c.gridy = (y++);
/*  135 */     c.gridwidth = 2;
/*  136 */     c.insets = new Insets(5, 5, 0, 5);
/*  137 */     add(this.bandPassH, c);
/*      */ 
/*  140 */     this.minSlider = new Scrollbar(0, 0, 1, 0, this.sliderRange);
/*  141 */     c.gridx = 0;
/*  142 */     c.gridy = (y++);
/*  143 */     c.gridwidth = 1;
/*  144 */     c.weightx = (IJ.isMacintosh() ? 90.0D : 100.0D);
/*  145 */     c.fill = 2;
/*  146 */     c.insets = new Insets(5, 5, 0, 0);
/*      */ 
/*  148 */     add(this.minSlider, c);
/*  149 */     this.minSlider.addAdjustmentListener(this);
/*  150 */     this.minSlider.setUnitIncrement(1);
/*      */ 
/*  153 */     c.gridx = 1;
/*  154 */     c.gridwidth = 1;
/*  155 */     c.weightx = (IJ.isMacintosh() ? 10.0D : 0.0D);
/*  156 */     c.insets = new Insets(5, 0, 0, 0);
/*  157 */     this.label1 = new Label("       ", 0);
/*  158 */     this.label1.setFont(font);
/*  159 */     add(this.label1, c);
/*      */ 
/*  162 */     this.maxSlider = new Scrollbar(0, 0, 1, 0, this.sliderRange);
/*  163 */     c.gridx = 0;
/*  164 */     c.gridy = y;
/*  165 */     c.gridwidth = 1;
/*  166 */     c.weightx = 100.0D;
/*  167 */     c.insets = new Insets(5, 5, 0, 0);
/*  168 */     add(this.maxSlider, c);
/*  169 */     this.maxSlider.addAdjustmentListener(this);
/*  170 */     this.maxSlider.setUnitIncrement(1);
/*      */ 
/*  173 */     c.gridx = 1;
/*  174 */     c.gridwidth = 1;
/*  175 */     c.gridy = (y++);
/*  176 */     c.weightx = 0.0D;
/*  177 */     c.insets = new Insets(5, 0, 0, 0);
/*  178 */     this.label2 = new Label("       ", 0);
/*  179 */     this.label2.setFont(font);
/*  180 */     add(this.label2, c);
/*      */ 
/*  183 */     c.gridx = 0;
/*  184 */     c.gridy = (y++);
/*  185 */     c.gridwidth = 1;
/*  186 */     c.weightx = 0.0D;
/*  187 */     c.insets = new Insets(10, 0, 0, 0);
/*  188 */     this.labels = new Label("Saturation", 1);
/*  189 */     add(this.labels, c);
/*      */ 
/*  192 */     c.gridx = 0;
/*  193 */     c.gridy = y;
/*  194 */     c.gridwidth = 1;
/*  195 */     c.fill = 1;
/*  196 */     c.anchor = 10;
/*  197 */     c.insets = new Insets(0, 5, 0, 0);
/*  198 */     add(this.splot, c);
/*      */ 
/*  201 */     this.bandPassS = new Checkbox("Pass");
/*  202 */     this.bandPassS.addItemListener(this);
/*  203 */     this.bandPassS.setState(true);
/*  204 */     c.gridx = 1;
/*  205 */     c.gridy = (y++);
/*  206 */     c.gridwidth = 2;
/*  207 */     c.insets = new Insets(5, 5, 0, 5);
/*  208 */     add(this.bandPassS, c);
/*      */ 
/*  211 */     this.minSlider2 = new Scrollbar(0, 0, 1, 0, this.sliderRange);
/*  212 */     c.gridx = 0;
/*  213 */     c.gridy = (y++);
/*  214 */     c.gridwidth = 1;
/*  215 */     c.weightx = (IJ.isMacintosh() ? 90.0D : 100.0D);
/*  216 */     c.fill = 2;
/*  217 */     c.insets = new Insets(5, 5, 0, 0);
/*  218 */     add(this.minSlider2, c);
/*  219 */     this.minSlider2.addAdjustmentListener(this);
/*  220 */     this.minSlider2.setUnitIncrement(1);
/*      */ 
/*  223 */     c.gridx = 1;
/*  224 */     c.gridwidth = 1;
/*  225 */     c.weightx = (IJ.isMacintosh() ? 10.0D : 0.0D);
/*  226 */     c.insets = new Insets(5, 0, 0, 0);
/*  227 */     this.label3 = new Label("       ", 0);
/*  228 */     this.label3.setFont(font);
/*  229 */     add(this.label3, c);
/*      */ 
/*  232 */     this.maxSlider2 = new Scrollbar(0, 0, 1, 0, this.sliderRange);
/*  233 */     c.gridx = 0;
/*  234 */     c.gridy = (y++);
/*  235 */     c.gridwidth = 1;
/*  236 */     c.weightx = 100.0D;
/*  237 */     c.insets = new Insets(5, 5, 0, 0);
/*  238 */     add(this.maxSlider2, c);
/*  239 */     this.maxSlider2.addAdjustmentListener(this);
/*  240 */     this.maxSlider2.setUnitIncrement(1);
/*      */ 
/*  243 */     c.gridx = 1;
/*  244 */     c.gridwidth = 1;
/*  245 */     c.weightx = 0.0D;
/*  246 */     c.insets = new Insets(5, 0, 0, 0);
/*  247 */     this.label4 = new Label("       ", 0);
/*  248 */     this.label4.setFont(font);
/*  249 */     add(this.label4, c);
/*      */ 
/*  252 */     c.gridx = 0;
/*  253 */     c.gridwidth = 1;
/*  254 */     c.gridy = (y++);
/*  255 */     c.weightx = 0.0D;
/*  256 */     c.insets = new Insets(10, 0, 0, 0);
/*  257 */     this.labelb = new Label("Brightness", 1);
/*  258 */     add(this.labelb, c);
/*      */ 
/*  260 */     c.gridx = 0;
/*  261 */     c.gridwidth = 1;
/*  262 */     c.gridy = y;
/*  263 */     c.fill = 1;
/*  264 */     c.anchor = 10;
/*  265 */     c.insets = new Insets(0, 5, 0, 0);
/*  266 */     add(this.bplot, c);
/*      */ 
/*  269 */     this.bandPassB = new Checkbox("Pass");
/*  270 */     this.bandPassB.addItemListener(this);
/*  271 */     this.bandPassB.setState(true);
/*  272 */     c.gridx = 1;
/*  273 */     c.gridy = (y++);
/*  274 */     c.gridwidth = 2;
/*  275 */     c.insets = new Insets(5, 5, 0, 5);
/*  276 */     add(this.bandPassB, c);
/*      */ 
/*  279 */     this.minSlider3 = new Scrollbar(0, 0, 1, 0, this.sliderRange);
/*  280 */     c.gridx = 0;
/*  281 */     c.gridy = (y++);
/*  282 */     c.gridwidth = 1;
/*  283 */     c.weightx = (IJ.isMacintosh() ? 90.0D : 100.0D);
/*  284 */     c.fill = 2;
/*  285 */     c.insets = new Insets(5, 5, 0, 0);
/*  286 */     add(this.minSlider3, c);
/*  287 */     this.minSlider3.addAdjustmentListener(this);
/*  288 */     this.minSlider3.setUnitIncrement(1);
/*      */ 
/*  291 */     c.gridx = 1;
/*  292 */     c.gridwidth = 1;
/*  293 */     c.weightx = (IJ.isMacintosh() ? 10.0D : 0.0D);
/*  294 */     c.insets = new Insets(5, 0, 0, 0);
/*  295 */     this.label5 = new Label("       ", 0);
/*  296 */     this.label5.setFont(font);
/*  297 */     add(this.label5, c);
/*      */ 
/*  300 */     this.maxSlider3 = new Scrollbar(0, 0, 1, 0, this.sliderRange);
/*  301 */     c.gridx = 0;
/*  302 */     c.gridy = (y++);
/*  303 */     c.gridwidth = 1;
/*  304 */     c.weightx = 100.0D;
/*  305 */     c.insets = new Insets(5, 5, 0, 0);
/*  306 */     add(this.maxSlider3, c);
/*  307 */     this.maxSlider3.addAdjustmentListener(this);
/*  308 */     this.maxSlider3.setUnitIncrement(1);
/*      */ 
/*  311 */     c.gridx = 1;
/*  312 */     c.gridwidth = 1;
/*  313 */     c.weightx = 0.0D;
/*  314 */     c.insets = new Insets(5, 0, 0, 0);
/*  315 */     this.label6 = new Label("       ", 0);
/*  316 */     this.label6.setFont(font);
/*  317 */     add(this.label6, c);
/*      */ 
/*  319 */     GridBagLayout gridbag2 = new GridBagLayout();
/*  320 */     GridBagConstraints c2 = new GridBagConstraints();
/*  321 */     int y2 = 0;
/*  322 */     Panel panel = new Panel();
/*  323 */     panel.setLayout(gridbag2);
/*      */ 
/*  326 */     c2.gridx = 0; c2.gridy = y2;
/*  327 */     c2.anchor = 13;
/*  328 */     c2.gridwidth = 1;
/*  329 */     c2.insets = new Insets(5, 0, 0, 0);
/*  330 */     Label theLabel = new Label("Thresholding method:");
/*  331 */     gridbag2.setConstraints(theLabel, c2);
/*  332 */     panel.add(theLabel);
/*  333 */     this.methodChoice = new Choice();
/*  334 */     for (int i = 0; i < methodNames.length; i++)
/*  335 */       this.methodChoice.addItem(methodNames[i]);
/*  336 */     this.methodChoice.select(method);
/*  337 */     this.methodChoice.addItemListener(this);
/*  338 */     c2.gridx = 1; c2.gridy = y2;
/*  339 */     c2.anchor = 17;
/*  340 */     gridbag2.setConstraints(this.methodChoice, c2);
/*  341 */     panel.add(this.methodChoice);
/*  342 */     y2++;
/*      */ 
/*  345 */     c2.gridx = 0; c2.gridy = y2;
/*  346 */     c2.anchor = 13;
/*  347 */     c2.insets = new Insets(0, 0, 0, 0);
/*  348 */     theLabel = new Label("Threshold color:");
/*  349 */     gridbag2.setConstraints(theLabel, c2);
/*  350 */     panel.add(theLabel);
/*  351 */     this.modeChoice = new Choice();
/*  352 */     for (int i = 0; i < modes.length; i++)
/*  353 */       this.modeChoice.addItem(modes[i]);
/*  354 */     this.modeChoice.select(mode);
/*  355 */     this.modeChoice.addItemListener(this);
/*  356 */     c2.gridx = 1; c2.gridy = y2;
/*  357 */     c2.anchor = 17;
/*  358 */     gridbag2.setConstraints(this.modeChoice, c2);
/*  359 */     panel.add(this.modeChoice);
/*  360 */     y2++;
/*      */ 
/*  363 */     c2.gridx = 0; c2.gridy = y2;
/*  364 */     c2.anchor = 13;
/*  365 */     theLabel = new Label("Color space:");
/*  366 */     gridbag2.setConstraints(theLabel, c2);
/*  367 */     panel.add(theLabel);
/*  368 */     this.colorSpaceChoice = new Choice();
/*  369 */     for (int i = 0; i < colorSpaces.length; i++)
/*  370 */       this.colorSpaceChoice.addItem(colorSpaces[i]);
/*  371 */     this.colorSpaceChoice.select(0);
/*  372 */     this.colorSpaceChoice.addItemListener(this);
/*  373 */     c2.gridx = 1; c2.gridy = y2;
/*  374 */     c2.anchor = 17;
/*  375 */     gridbag2.setConstraints(this.colorSpaceChoice, c2);
/*  376 */     panel.add(this.colorSpaceChoice);
/*  377 */     y2++;
/*      */ 
/*  379 */     c.gridx = 0;
/*  380 */     c.gridy = (y++);
/*  381 */     c.gridwidth = 2;
/*  382 */     c.insets = new Insets(5, 0, 0, 0);
/*  383 */     c.anchor = 10;
/*  384 */     c.fill = 0;
/*  385 */     add(panel, c);
/*      */ 
/*  388 */     this.panelt = new Panel();
/*  389 */     boolean db = Prefs.get("cthresholder.dark", true);
/*  390 */     this.darkBackground = new Checkbox("Dark background", db);
/*  391 */     this.darkBackground.addItemListener(this);
/*  392 */     this.panelt.add(this.darkBackground);
/*      */ 
/*  394 */     c.gridx = 0;
/*  395 */     c.gridy = (y++);
/*  396 */     c.gridwidth = 2;
/*  397 */     c.insets = new Insets(0, 0, 0, 0);
/*  398 */     add(this.panelt, c);
/*      */ 
/*  401 */     int trim = IJ.isMacOSX() ? 10 : 0;
/*  402 */     panel = new Panel();
/*  403 */     panel.setLayout(new GridLayout(0, 4, 0, 0));
/*  404 */     this.originalB = new TrimmedButton("Original", trim);
/*      */ 
/*  406 */     this.originalB.addActionListener(this);
/*  407 */     this.originalB.addKeyListener(this.ij);
/*  408 */     panel.add(this.originalB);
/*      */ 
/*  410 */     this.filteredB = new TrimmedButton("Filtered", trim);
/*  411 */     this.filteredB.setEnabled(false);
/*  412 */     this.filteredB.addActionListener(this);
/*  413 */     this.filteredB.addKeyListener(this.ij);
/*  414 */     panel.add(this.filteredB);
/*      */ 
/*  416 */     this.selectB = new TrimmedButton("Select", trim);
/*  417 */     this.selectB.addActionListener(this);
/*  418 */     this.selectB.addKeyListener(this.ij);
/*  419 */     panel.add(this.selectB);
/*      */ 
/*  421 */     this.sampleB = new TrimmedButton("Sample", trim);
/*  422 */     this.sampleB.addActionListener(this);
/*  423 */     this.sampleB.addKeyListener(this.ij);
/*  424 */     panel.add(this.sampleB);
/*      */ 
/*  426 */     this.stackB = new TrimmedButton("Stack", trim);
/*  427 */     this.stackB.addActionListener(this);
/*  428 */     this.stackB.addKeyListener(this.ij);
/*  429 */     panel.add(this.stackB);
/*      */ 
/*  431 */     this.macroB = new TrimmedButton("Macro", trim);
/*  432 */     this.macroB.addActionListener(this);
/*  433 */     this.macroB.addKeyListener(this.ij);
/*  434 */     panel.add(this.macroB);
/*      */ 
/*  436 */     this.helpB = new TrimmedButton("Help", trim);
/*  437 */     this.helpB.addActionListener(this);
/*  438 */     this.helpB.addKeyListener(this.ij);
/*  439 */     panel.add(this.helpB);
/*      */ 
/*  441 */     c.gridx = 0;
/*  442 */     c.gridy = (y++);
/*  443 */     c.gridwidth = 2;
/*  444 */     c.insets = new Insets(5, 5, 10, 5);
/*  445 */     gridbag.setConstraints(panel, c);
/*  446 */     add(panel);
/*      */ 
/*  448 */     addKeyListener(this.ij);
/*  449 */     pack();
/*  450 */     GUI.center(this);
/*  451 */     setVisible(true);
/*      */ 
/*  453 */     this.thread.start();
/*  454 */     if (!checkImage()) return;
/*  455 */     synchronized (this) {
/*  456 */       notify();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void run() {
/*  461 */     while (!this.done)
/*  462 */       synchronized (this) {
/*      */         try { wait(); } catch (InterruptedException e) {
/*      */         }
/*  465 */         if (!this.done) {
/*  466 */           ImagePlus imp = WindowManager.getCurrentImage();
/*  467 */           if (imp != null) {
/*  468 */             reset(imp);
/*  469 */             apply(imp);
/*  470 */             imp.updateAndDraw();
/*      */           }
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   public synchronized void adjustmentValueChanged(AdjustmentEvent e)
/*      */   {
/*  478 */     if (IJ.debugMode) IJ.log("ColorThresholder.adjustmentValueChanged ");
/*  479 */     if (!checkImage()) return;
/*  480 */     if (e.getSource() == this.minSlider)
/*  481 */       adjustMinHue(this.minSlider.getValue());
/*  482 */     else if (e.getSource() == this.maxSlider)
/*  483 */       adjustMaxHue(this.maxSlider.getValue());
/*  484 */     else if (e.getSource() == this.minSlider2)
/*  485 */       adjustMinSat(this.minSlider2.getValue());
/*  486 */     else if (e.getSource() == this.maxSlider2)
/*  487 */       adjustMaxSat(this.maxSlider2.getValue());
/*  488 */     else if (e.getSource() == this.minSlider3)
/*  489 */       adjustMinBri(this.minSlider3.getValue());
/*  490 */     else if (e.getSource() == this.maxSlider3) {
/*  491 */       adjustMaxBri(this.maxSlider3.getValue());
/*      */     }
/*  493 */     updateLabels();
/*  494 */     updatePlot();
/*  495 */     notify();
/*      */   }
/*      */ 
/*      */   public synchronized void itemStateChanged(ItemEvent e) {
/*  499 */     if (IJ.debugMode) IJ.log("ColorThresolder.itemStateChanged");
/*  500 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  501 */     if (imp == null) return;
/*  502 */     Object source = e.getSource();
/*  503 */     if (source == this.methodChoice) {
/*  504 */       method = this.methodChoice.getSelectedItem();
/*  505 */     } else if (source == this.modeChoice) {
/*  506 */       mode = this.modeChoice.getSelectedIndex();
/*  507 */     } else if (source == this.colorSpaceChoice) {
/*  508 */       this.colorSpace = ((Choice)source).getSelectedIndex();
/*  509 */       this.flag = true;
/*      */ 
/*  511 */       this.filteredB.setEnabled(false);
/*  512 */       this.minHue = (this.minSat = this.minBri = 0);
/*  513 */       this.maxHue = (this.maxSat = this.maxBri = 'ÿ');
/*  514 */       this.bandPassH.setState(true);
/*  515 */       this.bandPassS.setState(true);
/*  516 */       this.bandPassB.setState(true);
/*  517 */     } else if (source != this.darkBackground);
/*  519 */     reset(imp);
/*  520 */     if ((source == this.methodChoice) || (source == this.colorSpaceChoice) || (source == this.darkBackground))
/*  521 */       autoSetThreshold();
/*  522 */     checkImage();
/*  523 */     updateNames();
/*  524 */     notify();
/*      */   }
/*      */ 
/*      */   public void focusGained(FocusEvent e)
/*      */   {
/*  529 */     if (IJ.debugMode) IJ.log("ColorThresolder.focusGained");
/*  530 */     checkImage();
/*      */   }
/*      */   public void focusLost(FocusEvent e) {
/*      */   }
/*      */ 
/*      */   public void actionPerformed(ActionEvent e) {
/*  536 */     if (IJ.debugMode) IJ.log("ColorThresholder.actionPerformed");
/*  537 */     Button b = (Button)e.getSource();
/*  538 */     if (b == null) return;
/*  539 */     boolean imageThere = (b == this.sampleB) || (checkImage());
/*  540 */     if (imageThere) {
/*  541 */       ImagePlus imp = WindowManager.getCurrentImage();
/*  542 */       if (imp == null) return;
/*  543 */       if (b == this.originalB) {
/*  544 */         reset(imp);
/*  545 */         imp.setProperty("OriginalImage", null);
/*  546 */         this.filteredB.setEnabled(true);
/*  547 */       } else if (b == this.filteredB) {
/*  548 */         reset(imp);
/*  549 */         apply(imp);
/*  550 */       } else if (b == this.sampleB) {
/*  551 */         sample();
/*  552 */         apply(imp);
/*  553 */       } else if (b == this.selectB) {
/*  554 */         createSelection();
/*  555 */       } else if (b == this.stackB) {
/*  556 */         applyStack(); } else {
/*  557 */         if (b == this.macroB) {
/*  558 */           generateMacro();
/*  559 */           return;
/*  560 */         }if (b == this.helpB) {
/*  561 */           showHelp();
/*  562 */           return;
/*      */         }
/*      */       }
/*  564 */       updatePlot();
/*  565 */       updateLabels();
/*  566 */       imp.updateAndDraw();
/*      */     } else {
/*  568 */       IJ.beep();
/*  569 */       IJ.showStatus("No Image");
/*      */     }
/*      */   }
/*      */ 
/*      */   void createSelection()
/*      */   {
/*  576 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  577 */     if (imp == null) return;
/*  578 */     imp.killRoi();
/*  579 */     mode = 3;
/*  580 */     apply(imp);
/*  581 */     IJ.run(imp, "8-bit", "");
/*      */ 
/*  584 */     IJ.run(imp, "Create Selection", "");
/*  585 */     IJ.run(imp, "RGB Color", "");
/*  586 */     reset(imp);
/*      */   }
/*      */ 
/*      */   void generateMacro() {
/*  590 */     if (!Recorder.record) {
/*  591 */       IJ.error("Threshold Color", "Command recorder is not running");
/*  592 */       return;
/*      */     }
/*  594 */     Recorder.recordString("// Color Thresholder " + IJ.getVersion() + "\n");
/*  595 */     Recorder.recordString("// Autogenerated macro, single images only!\n");
/*  596 */     Recorder.recordString("min=newArray(3);\n");
/*  597 */     Recorder.recordString("max=newArray(3);\n");
/*  598 */     Recorder.recordString("filter=newArray(3);\n");
/*  599 */     Recorder.recordString("a=getTitle();\n");
/*  600 */     if (this.colorSpace == 0) {
/*  601 */       Recorder.recordString("run(\"HSB Stack\");\n");
/*  602 */       Recorder.recordString("run(\"Convert Stack to Images\");\n");
/*  603 */       Recorder.recordString("selectWindow(\"Hue\");\n");
/*  604 */       Recorder.recordString("rename(\"0\");\n");
/*  605 */       Recorder.recordString("selectWindow(\"Saturation\");\n");
/*  606 */       Recorder.recordString("rename(\"1\");\n");
/*  607 */       Recorder.recordString("selectWindow(\"Brightness\");\n");
/*  608 */       Recorder.recordString("rename(\"2\");\n");
/*      */     } else {
/*  610 */       if (this.colorSpace == 2)
/*  611 */         Recorder.recordString("call(\"ij.plugin.frame.ColorThresholder.RGBtoLab\");\n");
/*  612 */       if (this.colorSpace == 3)
/*  613 */         Recorder.recordString("call(\"ij.plugin.frame.ColorThresholder.RGBtoYUV\");\n");
/*  614 */       Recorder.recordString("run(\"RGB Stack\");\n");
/*  615 */       Recorder.recordString("run(\"Convert Stack to Images\");\n");
/*  616 */       Recorder.recordString("selectWindow(\"Red\");\n");
/*  617 */       Recorder.recordString("rename(\"0\");\n");
/*  618 */       Recorder.recordString("selectWindow(\"Green\");\n");
/*  619 */       Recorder.recordString("rename(\"1\");\n");
/*  620 */       Recorder.recordString("selectWindow(\"Blue\");\n");
/*  621 */       Recorder.recordString("rename(\"2\");\n");
/*      */     }
/*  623 */     Recorder.recordString("min[0]=" + this.minSlider.getValue() + ";\n");
/*  624 */     Recorder.recordString("max[0]=" + this.maxSlider.getValue() + ";\n");
/*      */ 
/*  626 */     if (this.bandPassH.getState())
/*  627 */       Recorder.recordString("filter[0]=\"pass\";\n");
/*      */     else {
/*  629 */       Recorder.recordString("filter[0]=\"stop\";\n");
/*      */     }
/*  631 */     Recorder.recordString("min[1]=" + this.minSlider2.getValue() + ";\n");
/*  632 */     Recorder.recordString("max[1]=" + this.maxSlider2.getValue() + ";\n");
/*      */ 
/*  634 */     if (this.bandPassS.getState())
/*  635 */       Recorder.recordString("filter[1]=\"pass\";\n");
/*      */     else
/*  637 */       Recorder.recordString("filter[1]=\"stop\";\n");
/*  638 */     Recorder.recordString("min[2]=" + this.minSlider3.getValue() + ";\n");
/*  639 */     Recorder.recordString("max[2]=" + this.maxSlider3.getValue() + ";\n");
/*      */ 
/*  641 */     if (this.bandPassB.getState())
/*  642 */       Recorder.recordString("filter[2]=\"pass\";\n");
/*      */     else {
/*  644 */       Recorder.recordString("filter[2]=\"stop\";\n");
/*      */     }
/*  646 */     Recorder.recordString("for (i=0;i<3;i++){\n");
/*  647 */     Recorder.recordString("  selectWindow(\"\"+i);\n");
/*  648 */     Recorder.recordString("  setThreshold(min[i], max[i]);\n");
/*  649 */     Recorder.recordString("  run(\"Convert to Mask\");\n");
/*  650 */     Recorder.recordString("  if (filter[i]==\"stop\")  run(\"Invert\");\n");
/*  651 */     Recorder.recordString("}\n");
/*  652 */     Recorder.recordString("imageCalculator(\"AND create\", \"0\",\"1\");\n");
/*  653 */     Recorder.recordString("imageCalculator(\"AND create\", \"Result of 0\",\"2\");\n");
/*  654 */     Recorder.recordString("for (i=0;i<3;i++){\n");
/*  655 */     Recorder.recordString("  selectWindow(\"\"+i);\n");
/*  656 */     Recorder.recordString("  close();\n");
/*  657 */     Recorder.recordString("}\n");
/*  658 */     Recorder.recordString("selectWindow(\"Result of 0\");\n");
/*  659 */     Recorder.recordString("close();\n");
/*  660 */     Recorder.recordString("selectWindow(\"Result of Result of 0\");\n");
/*      */ 
/*  664 */     Recorder.recordString("rename(a);\n");
/*  665 */     Recorder.recordString("// Colour Thresholding-------------\n");
/*      */   }
/*      */ 
/*      */   void showHelp() {
/*  669 */     IJ.showMessage("Help", "Color Thresholder\n \nModification of Bob Dougherty's BandPass2 plugin by G.Landini\nto threshold 24 bit RGB images based on HSB, RGB, CIE Lab \nor YUV components.\n \n[Pass]: Everything within range is thresholded, otherwise,\neverything outside range is thresholded.\n \n[Default] [Huang] [Intermodes] [etc.]: Selects the automatic\nthresholding method.\n \n[Red] [White] [Black] [B&W]: Selects the threshold color.\n \n[Dark background]: Auto-thresholding methods assume\nlight features and dark background.\n \n[Original]: Shows the original image and updates the buffer\nwhen switching to another image.\n \n[Filtered]: Shows the filtered image.\n \n[Stack]: Processes the rest of the slices in the stack (if any)\nusing the current settings.\n \n[Macro]: Creates a macro based on the current settings which\nis sent to the macro Recorder window, if open.\n \n[Sample]: (experimental) Sets the ranges of the filters based\non the pixel value components in a user-defined ROI.\n \n[HSB] [RGB] [CIE Lab] [YUV]: Selects the color space.\n \n");
/*      */   }
/*      */ 
/*      */   void sample()
/*      */   {
/*  694 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  695 */     if ((imp == null) || (imp.getBitDepth() != 24))
/*  696 */       return;
/*  697 */     reset(imp);
/*      */ 
/*  700 */     int[] bin = new int[256];
/*  701 */     int counter = 0; int pi = 0; int rangePassH = 0; int rangeStopH = 0; int rangePassL = 0; int rangeStopL = 0;
/*  702 */     int snumPixels = 0;
/*      */ 
/*  704 */     Roi roi = imp.getRoi();
/*  705 */     if (roi == null) {
/*  706 */       IJ.error("Selection required");
/*  707 */       return;
/*      */     }
/*      */ 
/*  710 */     ImageProcessor mask = roi.getMask();
/*      */ 
/*  712 */     Rectangle r = roi.getBoundingRect();
/*      */ 
/*  715 */     ImageProcessor ip = imp.getProcessor();
/*      */ 
/*  718 */     if (mask == null) {
/*  719 */       snumPixels = r.width * r.height;
/*      */     }
/*      */     else {
/*  722 */       snumPixels = 0;
/*  723 */       for (int j = 0; j < r.height; j++) {
/*  724 */         for (int i = 0; i < r.width; i++) {
/*  725 */           if (mask.getPixel(i, j) != 0) {
/*  726 */             snumPixels++;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  732 */     byte[] hsSource = new byte[snumPixels];
/*  733 */     byte[] ssSource = new byte[snumPixels];
/*  734 */     byte[] bsSource = new byte[snumPixels];
/*  735 */     int[] pixs = new int[snumPixels];
/*      */ 
/*  738 */     if (mask == null) {
/*  739 */       for (int j = 0; j < r.height; j++) {
/*  740 */         for (int i = 0; i < r.width; i++) {
/*  741 */           pixs[(counter++)] = ip.getPixel(i + r.x, j + r.y);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  746 */     for (int j = 0; j < r.height; j++) {
/*  747 */       for (int i = 0; i < r.width; i++) {
/*  748 */         if (mask.getPixel(i, j) != 0) {
/*  749 */           pixs[(counter++)] = ip.getPixel(i + r.x, j + r.y);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  754 */     imp.killRoi();
/*      */ 
/*  758 */     ColorProcessor cp2 = new ColorProcessor(1, snumPixels, pixs);
/*      */ 
/*  760 */     int iminhue = 256; int imaxhue = -1; int iminsat = 256; int imaxsat = -1; int iminbri = 256; int imaxbri = -1;
/*  761 */     int iminred = 256; int imaxred = -1; int imingre = 256; int imaxgre = -1; int iminblu = 256; int imaxblu = -1;
/*      */ 
/*  763 */     if (this.colorSpace == 1)
/*  764 */       cp2.getRGB(hsSource, ssSource, bsSource);
/*  765 */     else if (this.colorSpace == 0)
/*  766 */       cp2.getHSB(hsSource, ssSource, bsSource);
/*  767 */     else if (this.colorSpace == 2)
/*  768 */       getLab(cp2, hsSource, ssSource, bsSource);
/*  769 */     else if (this.colorSpace == 3) {
/*  770 */       getYUV(cp2, hsSource, ssSource, bsSource);
/*      */     }
/*      */ 
/*  773 */     for (int i = 0; i < snumPixels; i++) {
/*  774 */       bin[(hsSource[i] & 0xFF)] = 1;
/*  775 */       if ((hsSource[i] & 0xFF) > imaxhue) imaxhue = hsSource[i] & 0xFF;
/*  776 */       if ((hsSource[i] & 0xFF) < iminhue) iminhue = hsSource[i] & 0xFF;
/*  777 */       if ((ssSource[i] & 0xFF) > imaxsat) imaxsat = ssSource[i] & 0xFF;
/*  778 */       if ((ssSource[i] & 0xFF) < iminsat) iminsat = ssSource[i] & 0xFF;
/*  779 */       if ((bsSource[i] & 0xFF) > imaxbri) imaxbri = bsSource[i] & 0xFF;
/*  780 */       if ((bsSource[i] & 0xFF) < iminbri) iminbri = bsSource[i] & 0xFF;
/*      */ 
/*      */     }
/*      */ 
/*  784 */     if (this.colorSpace == 0) {
/*  785 */       int gap = 0; int maxgap = 0; int maxgapst = -1; int maxgapen = -1; int gapst = 0;
/*      */ 
/*  787 */       if (bin[0] == 0) {
/*  788 */         gapst = 0;
/*  789 */         gap = 1;
/*      */       }
/*      */ 
/*  792 */       for (i = 1; i < 256; i++)
/*      */       {
/*  794 */         if (bin[i] == 0) {
/*  795 */           if (bin[(i - 1)] > 0) {
/*  796 */             gap = 1;
/*  797 */             gapst = i;
/*      */           }
/*      */           else {
/*  800 */             gap++;
/*      */           }
/*  802 */           if (gap > maxgap) {
/*  803 */             maxgap = gap;
/*  804 */             maxgapst = gapst;
/*  805 */             maxgapen = i;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  810 */       for (i = 0; i < 256; i++) {
/*  811 */         if (bin[i] > 0) {
/*  812 */           rangePassL = i;
/*  813 */           break;
/*      */         }
/*      */       }
/*  816 */       for (i = 255; i >= 0; i--) {
/*  817 */         if (bin[i] > 0) {
/*  818 */           rangePassH = i;
/*  819 */           break;
/*      */         }
/*      */       }
/*  822 */       if (rangePassH - rangePassL < maxgap) {
/*  823 */         this.bandPassH.setState(true);
/*  824 */         iminhue = rangePassL;
/*  825 */         imaxhue = rangePassH;
/*      */       }
/*      */       else {
/*  828 */         this.bandPassH.setState(false);
/*  829 */         iminhue = maxgapst;
/*  830 */         imaxhue = maxgapen;
/*      */       }
/*      */     }
/*      */     else {
/*  834 */       this.bandPassH.setState(true);
/*      */     }
/*      */ 
/*  837 */     adjustMinHue(iminhue);
/*  838 */     this.minSlider.setValue(iminhue);
/*  839 */     adjustMaxHue(imaxhue);
/*  840 */     this.maxSlider.setValue(imaxhue);
/*  841 */     adjustMinSat(iminsat);
/*  842 */     this.minSlider2.setValue(iminsat);
/*  843 */     adjustMaxSat(imaxsat);
/*  844 */     this.maxSlider2.setValue(imaxsat);
/*  845 */     adjustMinBri(iminbri);
/*  846 */     this.minSlider3.setValue(iminbri);
/*  847 */     adjustMaxBri(imaxbri);
/*  848 */     this.maxSlider3.setValue(imaxbri);
/*      */   }
/*      */ 
/*      */   private boolean checkImage()
/*      */   {
/*  855 */     if (IJ.debugMode) IJ.log("ColorThresholder.checkImage");
/*  856 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  857 */     if ((imp == null) || (imp.getBitDepth() != 24)) {
/*  858 */       IJ.beep();
/*  859 */       IJ.showStatus("No RGB image");
/*  860 */       return false;
/*      */     }
/*  862 */     imp.killRoi();
/*  863 */     boolean ok = setup(imp);
/*  864 */     return ok;
/*      */   }
/*      */ 
/*      */   boolean setup(ImagePlus imp) {
/*  868 */     if (IJ.debugMode) IJ.log("ColorThresholder.setup");
/*      */ 
/*  870 */     int type = imp.getType();
/*  871 */     if (type != 4)
/*  872 */       return false;
/*  873 */     ImageProcessor ip = imp.getProcessor();
/*  874 */     int id = imp.getID();
/*  875 */     int slice = imp.getCurrentSlice();
/*  876 */     if ((id != this.previousImageID) || (slice != this.previousSlice) || (this.flag)) {
/*  877 */       Undo.reset();
/*  878 */       this.flag = false;
/*  879 */       this.numSlices = imp.getStackSize();
/*  880 */       this.stack = imp.getStack();
/*  881 */       this.width = this.stack.getWidth();
/*  882 */       this.height = this.stack.getHeight();
/*  883 */       this.numPixels = (this.width * this.height);
/*      */ 
/*  885 */       this.hSource = new byte[this.numPixels];
/*  886 */       this.sSource = new byte[this.numPixels];
/*  887 */       this.bSource = new byte[this.numPixels];
/*      */ 
/*  889 */       ImageProcessor mask = new ByteProcessor(this.width, this.height);
/*  890 */       imp.setProperty("Mask", mask);
/*      */ 
/*  893 */       ColorProcessor cp = (ColorProcessor)ip;
/*  894 */       IJ.showStatus("Converting colour space...");
/*  895 */       if (this.colorSpace == 1)
/*  896 */         cp.getRGB(this.hSource, this.sSource, this.bSource);
/*  897 */       else if (this.colorSpace == 0)
/*  898 */         cp.getHSB(this.hSource, this.sSource, this.bSource);
/*  899 */       else if (this.colorSpace == 2)
/*  900 */         getLab(cp, this.hSource, this.sSource, this.bSource);
/*  901 */       else if (this.colorSpace == 3) {
/*  902 */         getYUV(cp, this.hSource, this.sSource, this.bSource);
/*      */       }
/*  904 */       IJ.showStatus("");
/*      */ 
/*  908 */       byte[] reds = new byte[256];
/*  909 */       byte[] greens = new byte[256];
/*  910 */       byte[] blues = new byte[256];
/*  911 */       for (int i = 0; i < 256; i++) {
/*  912 */         Color c = Color.getHSBColor(i / 255.0F, 1.0F, 1.0F);
/*  913 */         reds[i] = ((byte)c.getRed());
/*  914 */         greens[i] = ((byte)c.getGreen());
/*  915 */         blues[i] = ((byte)c.getBlue());
/*      */       }
/*  917 */       ColorModel cm = new IndexColorModel(8, 256, reds, greens, blues);
/*      */ 
/*  922 */       ByteProcessor bpHue = new ByteProcessor(this.width, this.height, this.hSource, cm);
/*  923 */       ImagePlus impHue = new ImagePlus("Hue", bpHue);
/*      */ 
/*  926 */       ByteProcessor bpSat = new ByteProcessor(this.width, this.height, this.sSource, cm);
/*  927 */       ImagePlus impSat = new ImagePlus("Sat", bpSat);
/*      */ 
/*  930 */       ByteProcessor bpBri = new ByteProcessor(this.width, this.height, this.bSource, cm);
/*  931 */       ImagePlus impBri = new ImagePlus("Bri", bpBri);
/*      */ 
/*  934 */       this.plot.setHistogram(impHue, 0);
/*  935 */       this.splot.setHistogram(impSat, 1);
/*  936 */       this.bplot.setHistogram(impBri, 2);
/*      */ 
/*  938 */       if (!this.applyingStack)
/*  939 */         autoSetThreshold();
/*  940 */       imp.updateAndDraw();
/*      */     }
/*  942 */     this.previousImageID = id;
/*  943 */     this.previousSlice = slice;
/*  944 */     return ip != null;
/*      */   }
/*      */ 
/*      */   void autoSetThreshold() {
/*  948 */     if (IJ.debugMode) IJ.log("ColorThresholder.autoSetThreshold");
/*  949 */     boolean darkb = (this.darkBackground != null) && (this.darkBackground.getState());
/*      */     int[] histogram;
/*      */     int threshold;
/*  950 */     switch (this.colorSpace) {
/*      */     case 0:
/*  952 */       histogram = this.bplot.getHistogram();
/*  953 */       if (histogram == null) return;
/*  954 */       threshold = thresholder.getThreshold(method, histogram);
/*  955 */       if (darkb) {
/*  956 */         this.minBri = (threshold + 1);
/*  957 */         this.maxBri = 255;
/*      */       } else {
/*  959 */         this.minBri = 0;
/*  960 */         this.maxBri = threshold;
/*      */       }
/*  962 */       break;
/*      */     case 1:
/*  964 */       int[] rhistogram = this.plot.getHistogram();
/*  965 */       threshold = thresholder.getThreshold(method, rhistogram);
/*  966 */       if (darkb) {
/*  967 */         this.minHue = (threshold + 1);
/*  968 */         this.maxHue = 255;
/*      */       } else {
/*  970 */         this.minHue = 0;
/*  971 */         this.maxHue = threshold;
/*      */       }
/*  973 */       int[] ghistogram = this.splot.getHistogram();
/*  974 */       threshold = thresholder.getThreshold(method, ghistogram);
/*  975 */       if (darkb) {
/*  976 */         this.minSat = (threshold + 1);
/*  977 */         this.maxSat = 255;
/*      */       } else {
/*  979 */         this.minSat = 0;
/*  980 */         this.maxSat = threshold;
/*      */       }
/*  982 */       int[] bhistogram = this.bplot.getHistogram();
/*  983 */       threshold = thresholder.getThreshold(method, bhistogram);
/*  984 */       if (darkb) {
/*  985 */         this.minBri = (threshold + 1);
/*  986 */         this.maxBri = 255;
/*      */       } else {
/*  988 */         this.minBri = 0;
/*  989 */         this.maxBri = threshold;
/*      */       }
/*  991 */       break;
/*      */     case 2:
/*      */     case 3:
/*  993 */       histogram = this.plot.getHistogram();
/*  994 */       threshold = thresholder.getThreshold(method, histogram);
/*  995 */       if (darkb) {
/*  996 */         this.minHue = (threshold + 1);
/*  997 */         this.maxHue = 255;
/*      */       } else {
/*  999 */         this.minHue = 0;
/* 1000 */         this.maxHue = threshold;
/*      */       }
/*      */       break;
/*      */     }
/* 1004 */     updateScrollBars();
/* 1005 */     updateLabels();
/* 1006 */     updatePlot();
/*      */   }
/*      */ 
/*      */   void updatePlot() {
/* 1010 */     this.plot.minHue = this.minHue;
/* 1011 */     this.plot.maxHue = this.maxHue;
/* 1012 */     this.plot.repaint();
/* 1013 */     this.splot.minHue = this.minSat;
/* 1014 */     this.splot.maxHue = this.maxSat;
/* 1015 */     this.splot.repaint();
/* 1016 */     this.bplot.minHue = this.minBri;
/* 1017 */     this.bplot.maxHue = this.maxBri;
/* 1018 */     this.bplot.repaint();
/*      */   }
/*      */ 
/*      */   void updateLabels() {
/* 1022 */     this.label1.setText("" + this.minHue);
/* 1023 */     this.label2.setText("" + this.maxHue);
/* 1024 */     this.label3.setText("" + this.minSat);
/* 1025 */     this.label4.setText("" + this.maxSat);
/* 1026 */     this.label5.setText("" + this.minBri);
/* 1027 */     this.label6.setText("" + this.maxBri);
/*      */   }
/*      */ 
/*      */   void updateNames() {
/* 1031 */     if (this.colorSpace == 1) {
/* 1032 */       this.labelh.setText("Red");
/* 1033 */       this.labels.setText("Green");
/* 1034 */       this.labelb.setText("Blue");
/* 1035 */     } else if (this.colorSpace == 0) {
/* 1036 */       this.labelh.setText("Hue");
/* 1037 */       this.labels.setText("Saturation");
/* 1038 */       this.labelb.setText("Brightness");
/* 1039 */     } else if (this.colorSpace == 2) {
/* 1040 */       this.labelh.setText("L*");
/* 1041 */       this.labels.setText("a*");
/* 1042 */       this.labelb.setText("b*");
/* 1043 */     } else if (this.colorSpace == 3) {
/* 1044 */       this.labelh.setText("Y");
/* 1045 */       this.labels.setText("U");
/* 1046 */       this.labelb.setText("V");
/*      */     }
/*      */   }
/*      */ 
/*      */   void updateScrollBars() {
/* 1051 */     this.minSlider.setValue(this.minHue);
/* 1052 */     this.maxSlider.setValue(this.maxHue);
/* 1053 */     this.minSlider2.setValue(this.minSat);
/* 1054 */     this.maxSlider2.setValue(this.maxSat);
/* 1055 */     this.minSlider3.setValue(this.minBri);
/* 1056 */     this.maxSlider3.setValue(this.maxBri);
/*      */   }
/*      */ 
/*      */   void adjustMinHue(int value) {
/* 1060 */     this.minHue = value;
/* 1061 */     if (this.maxHue < this.minHue) {
/* 1062 */       this.maxHue = this.minHue;
/* 1063 */       this.maxSlider.setValue(this.maxHue);
/*      */     }
/*      */   }
/*      */ 
/*      */   void adjustMaxHue(int value) {
/* 1068 */     this.maxHue = value;
/* 1069 */     if (this.minHue > this.maxHue) {
/* 1070 */       this.minHue = this.maxHue;
/* 1071 */       this.minSlider.setValue(this.minHue);
/*      */     }
/*      */   }
/*      */ 
/*      */   void adjustMinSat(int value) {
/* 1076 */     this.minSat = value;
/* 1077 */     if (this.maxSat < this.minSat) {
/* 1078 */       this.maxSat = this.minSat;
/* 1079 */       this.maxSlider2.setValue(this.maxSat);
/*      */     }
/*      */   }
/*      */ 
/*      */   void adjustMaxSat(int value) {
/* 1084 */     this.maxSat = value;
/* 1085 */     if (this.minSat > this.maxSat) {
/* 1086 */       this.minSat = this.maxSat;
/* 1087 */       this.minSlider2.setValue(this.minSat);
/*      */     }
/*      */   }
/*      */ 
/*      */   void adjustMinBri(int value) {
/* 1092 */     this.minBri = value;
/* 1093 */     if (this.maxBri < this.minBri) {
/* 1094 */       this.maxBri = this.minBri;
/* 1095 */       this.maxSlider3.setValue(this.maxBri);
/*      */     }
/*      */   }
/*      */ 
/*      */   void adjustMaxBri(int value) {
/* 1100 */     this.maxBri = value;
/* 1101 */     if (this.minBri > this.maxBri) {
/* 1102 */       this.minBri = this.maxBri;
/* 1103 */       this.minSlider3.setValue(this.minBri);
/*      */     }
/*      */   }
/*      */ 
/*      */   void apply(ImagePlus imp) {
/* 1108 */     if (IJ.debugMode) IJ.log("ColorThresholder.apply");
/* 1109 */     ImageProcessor fillMaskIP = (ImageProcessor)imp.getProperty("Mask");
/* 1110 */     if (fillMaskIP == null) return;
/* 1111 */     byte[] fillMask = (byte[])fillMaskIP.getPixels();
/* 1112 */     byte fill = -1;
/* 1113 */     byte keep = 0;
/*      */ 
/* 1115 */     if ((this.bandPassH.getState()) && (this.bandPassS.getState()) && (this.bandPassB.getState())) {
/* 1116 */       for (int j = 0; j < this.numPixels; j++) {
/* 1117 */         int hue = this.hSource[j] & 0xFF;
/* 1118 */         int sat = this.sSource[j] & 0xFF;
/* 1119 */         int bri = this.bSource[j] & 0xFF;
/* 1120 */         if ((hue < this.minHue) || (hue > this.maxHue) || (sat < this.minSat) || (sat > this.maxSat) || (bri < this.minBri) || (bri > this.maxBri))
/* 1121 */           fillMask[j] = keep;
/*      */         else
/* 1123 */           fillMask[j] = fill;
/*      */       }
/*      */     }
/* 1126 */     else if ((!this.bandPassH.getState()) && (!this.bandPassS.getState()) && (!this.bandPassB.getState())) {
/* 1127 */       for (int j = 0; j < this.numPixels; j++) {
/* 1128 */         int hue = this.hSource[j] & 0xFF;
/* 1129 */         int sat = this.sSource[j] & 0xFF;
/* 1130 */         int bri = this.bSource[j] & 0xFF;
/* 1131 */         if (((hue >= this.minHue) && (hue <= this.maxHue)) || ((sat >= this.minSat) && (sat <= this.maxSat)) || ((bri >= this.minBri) && (bri <= this.maxBri)))
/* 1132 */           fillMask[j] = keep;
/*      */         else
/* 1134 */           fillMask[j] = fill;
/*      */       }
/*      */     }
/* 1137 */     else if ((this.bandPassH.getState()) && (this.bandPassS.getState()) && (!this.bandPassB.getState())) {
/* 1138 */       for (int j = 0; j < this.numPixels; j++) {
/* 1139 */         int hue = this.hSource[j] & 0xFF;
/* 1140 */         int sat = this.sSource[j] & 0xFF;
/* 1141 */         int bri = this.bSource[j] & 0xFF;
/* 1142 */         if ((hue < this.minHue) || (hue > this.maxHue) || (sat < this.minSat) || (sat > this.maxSat) || ((bri >= this.minBri) && (bri <= this.maxBri)))
/* 1143 */           fillMask[j] = keep;
/*      */         else
/* 1145 */           fillMask[j] = fill;
/*      */       }
/*      */     }
/* 1148 */     else if ((!this.bandPassH.getState()) && (!this.bandPassS.getState()) && (this.bandPassB.getState())) {
/* 1149 */       for (int j = 0; j < this.numPixels; j++) {
/* 1150 */         int hue = this.hSource[j] & 0xFF;
/* 1151 */         int sat = this.sSource[j] & 0xFF;
/* 1152 */         int bri = this.bSource[j] & 0xFF;
/* 1153 */         if (((hue >= this.minHue) && (hue <= this.maxHue)) || ((sat >= this.minSat) && (sat <= this.maxSat)) || (bri < this.minBri) || (bri > this.maxBri))
/* 1154 */           fillMask[j] = keep;
/*      */         else
/* 1156 */           fillMask[j] = fill;
/*      */       }
/*      */     }
/* 1159 */     else if ((this.bandPassH.getState()) && (!this.bandPassS.getState()) && (!this.bandPassB.getState())) {
/* 1160 */       for (int j = 0; j < this.numPixels; j++) {
/* 1161 */         int hue = this.hSource[j] & 0xFF;
/* 1162 */         int sat = this.sSource[j] & 0xFF;
/* 1163 */         int bri = this.bSource[j] & 0xFF;
/* 1164 */         if ((hue < this.minHue) || (hue > this.maxHue) || ((sat >= this.minSat) && (sat <= this.maxSat)) || ((bri >= this.minBri) && (bri <= this.maxBri)))
/* 1165 */           fillMask[j] = keep;
/*      */         else
/* 1167 */           fillMask[j] = fill;
/*      */       }
/*      */     }
/* 1170 */     else if ((!this.bandPassH.getState()) && (this.bandPassS.getState()) && (this.bandPassB.getState())) {
/* 1171 */       for (int j = 0; j < this.numPixels; j++) {
/* 1172 */         int hue = this.hSource[j] & 0xFF;
/* 1173 */         int sat = this.sSource[j] & 0xFF;
/* 1174 */         int bri = this.bSource[j] & 0xFF;
/* 1175 */         if (((hue >= this.minHue) && (hue <= this.maxHue)) || (sat < this.minSat) || (sat > this.maxSat) || (bri < this.minBri) || (bri > this.maxBri))
/* 1176 */           fillMask[j] = keep;
/*      */         else
/* 1178 */           fillMask[j] = fill;
/*      */       }
/*      */     }
/* 1181 */     else if ((!this.bandPassH.getState()) && (this.bandPassS.getState()) && (!this.bandPassB.getState())) {
/* 1182 */       for (int j = 0; j < this.numPixels; j++) {
/* 1183 */         int hue = this.hSource[j] & 0xFF;
/* 1184 */         int sat = this.sSource[j] & 0xFF;
/* 1185 */         int bri = this.bSource[j] & 0xFF;
/* 1186 */         if (((hue >= this.minHue) && (hue <= this.maxHue)) || (sat < this.minSat) || (sat > this.maxSat) || ((bri >= this.minBri) && (bri <= this.maxBri)))
/* 1187 */           fillMask[j] = keep;
/*      */         else
/* 1189 */           fillMask[j] = fill;
/*      */       }
/*      */     }
/* 1192 */     else if ((this.bandPassH.getState()) && (!this.bandPassS.getState()) && (this.bandPassB.getState())) {
/* 1193 */       for (int j = 0; j < this.numPixels; j++) {
/* 1194 */         int hue = this.hSource[j] & 0xFF;
/* 1195 */         int sat = this.sSource[j] & 0xFF;
/* 1196 */         int bri = this.bSource[j] & 0xFF;
/* 1197 */         if ((hue < this.minHue) || (hue > this.maxHue) || ((sat >= this.minSat) && (sat <= this.maxSat)) || (bri < this.minBri) || (bri > this.maxBri))
/* 1198 */           fillMask[j] = keep;
/*      */         else {
/* 1200 */           fillMask[j] = fill;
/*      */         }
/*      */       }
/*      */     }
/* 1204 */     ImageProcessor ip = imp.getProcessor();
/* 1205 */     if (ip == null) return;
/* 1206 */     if (mode == 3) {
/* 1207 */       int[] pixels = (int[])ip.getPixels();
/* 1208 */       int fcolor = Prefs.blackBackground ? -1 : -16777216;
/* 1209 */       int bcolor = Prefs.blackBackground ? -16777216 : -1;
/* 1210 */       for (int i = 0; i < this.numPixels; i++)
/* 1211 */         if (fillMask[i] != 0)
/* 1212 */           pixels[i] = fcolor;
/*      */         else
/* 1214 */           pixels[i] = bcolor;
/*      */     }
/*      */     else {
/* 1217 */       ip.setColor(thresholdColor());
/* 1218 */       ip.fill(fillMaskIP);
/*      */     }
/*      */   }
/*      */ 
/*      */   Color thresholdColor() {
/* 1223 */     Color color = null;
/* 1224 */     switch (mode) { case 0:
/* 1225 */       color = Color.red; break;
/*      */     case 1:
/* 1226 */       color = Color.white; break;
/*      */     case 2:
/* 1227 */       color = Color.black; break;
/*      */     case 3:
/* 1228 */       color = Color.black;
/*      */     }
/* 1230 */     return color;
/*      */   }
/*      */ 
/*      */   void applyStack() {
/* 1234 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1235 */     if (imp == null) return;
/* 1236 */     this.applyingStack = true;
/* 1237 */     for (int i = 1; i <= this.numSlices; i++) {
/* 1238 */       imp.setSlice(i);
/* 1239 */       if (!checkImage()) return;
/* 1240 */       apply(imp);
/*      */     }
/* 1242 */     this.applyingStack = false;
/*      */   }
/*      */ 
/*      */   void reset(ImagePlus imp)
/*      */   {
/* 1247 */     if (IJ.debugMode) IJ.log("ColorThresholder.reset");
/* 1248 */     ImageProcessor ip = imp.getProcessor();
/* 1249 */     ImagePlus originalImage = (ImagePlus)imp.getProperty("OriginalImage");
/* 1250 */     if (originalImage == null) {
/* 1251 */       originalImage = imp.createImagePlus();
/* 1252 */       originalImage.setTitle(imp.getTitle() + " (Original)");
/* 1253 */       originalImage.setProcessor(ip.duplicate());
/* 1254 */       imp.setProperty("OriginalImage", originalImage);
/*      */     }
/* 1256 */     int[] restore = (int[])originalImage.getProcessor().getPixels();
/* 1257 */     int[] pixels = (int[])ip.getPixels();
/* 1258 */     for (int i = 0; i < this.numPixels; i++)
/* 1259 */       pixels[i] = restore[i];
/*      */   }
/*      */ 
/*      */   public void windowActivated(WindowEvent e) {
/* 1263 */     if (IJ.debugMode) IJ.log("ColorThresholder.windowActivated ");
/* 1264 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1265 */     if ((imp == null) || (imp.getBitDepth() != 24)) {
/* 1266 */       IJ.beep();
/* 1267 */       IJ.showStatus("No RGB image");
/*      */     } else {
/* 1269 */       setup(imp);
/*      */ 
/* 1271 */       this.filteredB.setEnabled(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void close() {
/* 1276 */     super.close();
/* 1277 */     instance = null;
/* 1278 */     this.done = true;
/* 1279 */     Prefs.set("cthresholder.dark", this.darkBackground.getState());
/* 1280 */     synchronized (this) {
/* 1281 */       notify();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void getLab(ImageProcessor ip, byte[] L, byte[] a, byte[] b)
/*      */   {
/* 1289 */     int i = 0;
/*      */ 
/* 1293 */     double ot = 0.3333333333333333D; double cont = 0.1379310344827586D;
/*      */ 
/* 1295 */     int width = ip.getWidth();
/* 1296 */     int height = ip.getHeight();
/*      */ 
/* 1298 */     for (int y = 0; y < height; y++)
/* 1299 */       for (int x = 0; x < width; x++) {
/* 1300 */         int c = ip.getPixel(x, y);
/*      */ 
/* 1303 */         double rf = ((c & 0xFF0000) >> 16) / 255.0D;
/* 1304 */         double gf = ((c & 0xFF00) >> 8) / 255.0D;
/* 1305 */         double bf = (c & 0xFF) / 255.0D;
/*      */ 
/* 1309 */         double X = 0.430587D * rf + 0.341545D * gf + 0.178336D * bf;
/* 1310 */         double Y = 0.222021D * rf + 0.706645D * gf + 0.0713342D * bf;
/* 1311 */         double Z = 0.0201837D * rf + 0.129551D * gf + 0.939234D * bf;
/*      */         double fX;
/*      */         double fX;
/* 1314 */         if (X > 0.008855999999999999D)
/* 1315 */           fX = Math.pow(X, ot);
/*      */         else
/* 1317 */           fX = 7.78707D * X + cont;
/*      */         double fY;
/*      */         double fY;
/* 1320 */         if (Y > 0.008855999999999999D)
/* 1321 */           fY = Math.pow(Y, ot);
/*      */         else
/* 1323 */           fY = 7.78707D * Y + cont;
/*      */         double fZ;
/*      */         double fZ;
/* 1325 */         if (Z > 0.008855999999999999D)
/* 1326 */           fZ = Math.pow(Z, ot);
/*      */         else {
/* 1328 */           fZ = 7.78707D * Z + cont;
/*      */         }
/* 1330 */         double La = 116.0D * fY - 16.0D;
/* 1331 */         double aa = 500.0D * (fX - fY);
/* 1332 */         double bb = 200.0D * (fY - fZ);
/*      */ 
/* 1335 */         La = (int)(La * 2.55D);
/* 1336 */         aa = (int)Math.floor(1.0625D * aa + 128.0D + 0.5D);
/* 1337 */         bb = (int)Math.floor(1.0625D * bb + 128.0D + 0.5D);
/*      */ 
/* 1346 */         L[i] = ((byte)((int)(La > 255.0D ? 255.0D : La < 0.0D ? 0.0D : La) & 0xFF));
/* 1347 */         a[i] = ((byte)((int)(aa > 255.0D ? 255.0D : aa < 0.0D ? 0.0D : aa) & 0xFF));
/* 1348 */         b[i] = ((byte)((int)(bb > 255.0D ? 255.0D : bb < 0.0D ? 0.0D : bb) & 0xFF));
/* 1349 */         i++;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void getYUV(ImageProcessor ip, byte[] Y, byte[] U, byte[] V)
/*      */   {
/* 1366 */     int i = 0;
/*      */ 
/* 1369 */     int width = ip.getWidth();
/* 1370 */     int height = ip.getHeight();
/*      */ 
/* 1372 */     for (int y = 0; y < height; y++)
/* 1373 */       for (int x = 0; x < width; x++) {
/* 1374 */         int c = ip.getPixel(x, y);
/*      */ 
/* 1376 */         int r = (c & 0xFF0000) >> 16;
/* 1377 */         int g = (c & 0xFF00) >> 8;
/* 1378 */         int b = c & 0xFF;
/*      */ 
/* 1381 */         double yf = 0.299D * r + 0.587D * g + 0.114D * b;
/* 1382 */         Y[i] = ((byte)(int)Math.floor(yf + 0.5D));
/* 1383 */         U[i] = ((byte)(128 + (int)Math.floor(0.493D * (b - yf) + 0.5D)));
/* 1384 */         V[i] = ((byte)(128 + (int)Math.floor(0.877D * (r - yf) + 0.5D)));
/*      */ 
/* 1390 */         i++;
/*      */       }
/*      */   }
/*      */ 
/*      */   public static void RGBtoLab()
/*      */   {
/* 1403 */     ImagePlus imp = IJ.getImage();
/* 1404 */     if (imp.getBitDepth() == 24) {
/* 1405 */       RGBtoLab(imp.getProcessor());
/* 1406 */       imp.updateAndDraw();
/*      */     }
/*      */   }
/*      */ 
/*      */   static void RGBtoLab(ImageProcessor ip) {
/* 1411 */     int xe = ip.getWidth();
/* 1412 */     int ye = ip.getHeight();
/* 1413 */     int i = 0;
/*      */ 
/* 1417 */     double ot = 0.3333333333333333D; double cont = 0.1379310344827586D;
/*      */ 
/* 1419 */     ImagePlus imp = WindowManager.getCurrentImage();
/*      */ 
/* 1421 */     for (int y = 0; y < ye; y++)
/* 1422 */       for (int x = 0; x < xe; x++) {
/* 1423 */         int c = ip.getPixel(x, y);
/*      */ 
/* 1426 */         double rf = ((c & 0xFF0000) >> 16) / 255.0D;
/* 1427 */         double gf = ((c & 0xFF00) >> 8) / 255.0D;
/* 1428 */         double bf = (c & 0xFF) / 255.0D;
/*      */ 
/* 1431 */         double X = 0.430587D * rf + 0.341545D * gf + 0.178336D * bf;
/* 1432 */         double Y = 0.222021D * rf + 0.706645D * gf + 0.0713342D * bf;
/* 1433 */         double Z = 0.0201837D * rf + 0.129551D * gf + 0.939234D * bf;
/*      */         double fX;
/*      */         double fX;
/* 1436 */         if (X > 0.008855999999999999D)
/* 1437 */           fX = Math.pow(X, ot);
/*      */         else
/* 1439 */           fX = 7.78707D * X + cont;
/*      */         double fY;
/*      */         double fY;
/* 1441 */         if (Y > 0.008855999999999999D)
/* 1442 */           fY = Math.pow(Y, ot);
/*      */         else
/* 1444 */           fY = 7.78707D * Y + cont;
/*      */         double fZ;
/*      */         double fZ;
/* 1446 */         if (Z > 0.008855999999999999D)
/* 1447 */           fZ = Math.pow(Z, ot);
/*      */         else {
/* 1449 */           fZ = 7.78707D * Z + cont;
/*      */         }
/* 1451 */         double La = 116.0D * fY - 16.0D;
/* 1452 */         double aa = 500.0D * (fX - fY);
/* 1453 */         double bb = 200.0D * (fY - fZ);
/*      */ 
/* 1457 */         La *= 2.55D;
/* 1458 */         aa = Math.floor(1.0625D * aa + 128.0D + 0.5D);
/* 1459 */         bb = Math.floor(1.0625D * bb + 128.0D + 0.5D);
/*      */ 
/* 1462 */         int Li = (int)(La > 255.0D ? 255.0D : La < 0.0D ? 0.0D : La);
/* 1463 */         int ai = (int)(aa > 255.0D ? 255.0D : aa < 0.0D ? 0.0D : aa);
/* 1464 */         int bi = (int)(bb > 255.0D ? 255.0D : bb < 0.0D ? 0.0D : bb);
/* 1465 */         ip.putPixel(x, y, ((Li & 0xFF) << 16) + ((ai & 0xFF) << 8) + (bi & 0xFF));
/*      */       }
/*      */   }
/*      */ 
/*      */   public static void RGBtoYUV()
/*      */   {
/* 1475 */     ImagePlus imp = IJ.getImage();
/* 1476 */     if (imp.getBitDepth() == 24) {
/* 1477 */       RGBtoLab(imp.getProcessor());
/* 1478 */       imp.updateAndDraw();
/*      */     }
/*      */   }
/*      */ 
/*      */   static void RGBtoYUV(ImageProcessor ip) {
/* 1483 */     int xe = ip.getWidth();
/* 1484 */     int ye = ip.getHeight();
/* 1485 */     int i = 0;
/*      */ 
/* 1488 */     ImagePlus imp = WindowManager.getCurrentImage();
/*      */ 
/* 1490 */     for (int y = 0; y < ye; y++)
/* 1491 */       for (int x = 0; x < xe; x++) {
/* 1492 */         int c = ip.getPixel(x, y);
/*      */ 
/* 1494 */         int r = (c & 0xFF0000) >> 16;
/* 1495 */         int g = (c & 0xFF00) >> 8;
/* 1496 */         int b = c & 0xFF;
/*      */ 
/* 1499 */         double yf = 0.299D * r + 0.587D * g + 0.114D * b;
/* 1500 */         int Y = (int)Math.floor(yf + 0.5D);
/* 1501 */         int U = 128 + (int)Math.floor(0.493D * (b - yf) + 0.5D);
/* 1502 */         int V = 128 + (int)Math.floor(0.877D * (r - yf) + 0.5D);
/*      */ 
/* 1504 */         ip.putPixel(x, y, (((Y > 255 ? 255 : Y < 0 ? 0 : Y) & 0xFF) << 16) + (((U > 255 ? 255 : U < 0 ? 0 : U) & 0xFF) << 8) + ((V > 255 ? 255 : V < 0 ? 0 : V) & 0xFF));
/*      */ 
/* 1508 */         ip.putPixel(x, y, ((Y & 0xFF) << 16) + ((U & 0xFF) << 8) + (V & 0xFF)); }   } 
/* 1517 */   class BandPlot extends Canvas implements Measurements, MouseListener { final int WIDTH = 256; final int HEIGHT = 64;
/* 1518 */     double minHue = 0.0D; double minSat = 0.0D; double minBri = 0.0D;
/* 1519 */     double maxHue = 255.0D; double maxSat = 255.0D; double maxBri = 255.0D;
/*      */     int[] histogram;
/*      */     Color[] hColors;
/*      */     int hmax;
/*      */     Image os;
/*      */     Graphics osg;
/*      */ 
/* 1527 */     public BandPlot() { addMouseListener(this);
/* 1528 */       setSize(257, 65);
/*      */     }
/*      */ 
/*      */     public Dimension getPreferredSize()
/*      */     {
/* 1534 */       return new Dimension(257, 65);
/*      */     }
/*      */ 
/*      */     void setHistogram(ImagePlus imp, int j) {
/* 1538 */       ImageProcessor ip = imp.getProcessor();
/* 1539 */       ImageStatistics stats = ImageStatistics.getStatistics(ip, 9, null);
/* 1540 */       int maxCount2 = 0;
/* 1541 */       this.histogram = stats.histogram;
/* 1542 */       for (int i = 0; i < stats.nBins; i++) {
/* 1543 */         if (this.histogram[i] > maxCount2) maxCount2 = this.histogram[i];
/*      */       }
/*      */ 
/* 1546 */       this.hmax = ((int)(maxCount2 * 1.15D));
/* 1547 */       this.os = null;
/* 1548 */       ColorModel cm = ip.getColorModel();
/* 1549 */       if (!(cm instanceof IndexColorModel))
/* 1550 */         return;
/* 1551 */       IndexColorModel icm = (IndexColorModel)cm;
/* 1552 */       int mapSize = icm.getMapSize();
/* 1553 */       if (mapSize != 256)
/* 1554 */         return;
/* 1555 */       byte[] r = new byte[256];
/* 1556 */       byte[] g = new byte[256];
/* 1557 */       byte[] b = new byte[256];
/* 1558 */       icm.getReds(r);
/* 1559 */       icm.getGreens(g);
/* 1560 */       icm.getBlues(b);
/* 1561 */       this.hColors = new Color[256];
/*      */ 
/* 1563 */       if (ColorThresholder.this.colorSpace == 1) {
/* 1564 */         if (j == 0) {
/* 1565 */           for (int i = 0; i < 256; i++)
/* 1566 */             this.hColors[i] = new Color(i & 0xFF, 0, 0);
/*      */         }
/* 1568 */         else if (j == 1) {
/* 1569 */           for (int i = 0; i < 256; i++)
/* 1570 */             this.hColors[i] = new Color(0, i & 0xFF, 0);
/*      */         }
/* 1572 */         else if (j == 2) {
/* 1573 */           for (int i = 0; i < 256; i++)
/* 1574 */             this.hColors[i] = new Color(0, 0, i & 0xFF);
/*      */         }
/*      */       }
/* 1577 */       else if (ColorThresholder.this.colorSpace == 0) {
/* 1578 */         if (j == 0) {
/* 1579 */           for (int i = 0; i < 256; i++)
/* 1580 */             this.hColors[i] = new Color(r[i] & 0xFF, g[i] & 0xFF, b[i] & 0xFF);
/*      */         }
/* 1582 */         else if (j == 1) {
/* 1583 */           for (int i = 0; i < 256; i++) {
/* 1584 */             this.hColors[i] = new Color(255, 255 - i & 0xFF, 255 - i & 0xFF);
/*      */           }
/*      */         }
/* 1587 */         else if (j == 2)
/* 1588 */           for (int i = 0; i < 256; i++)
/*      */           {
/* 1590 */             this.hColors[i] = new Color(i & 0xFF, i & 0xFF, i & 0xFF);
/*      */           }
/*      */       }
/* 1593 */       else if (ColorThresholder.this.colorSpace == 2) {
/* 1594 */         if (j == 0) {
/* 1595 */           for (int i = 0; i < 256; i++)
/* 1596 */             this.hColors[i] = new Color(i & 0xFF, i & 0xFF, i & 0xFF);
/*      */         }
/* 1598 */         else if (j == 1) {
/* 1599 */           for (int i = 0; i < 256; i++)
/* 1600 */             this.hColors[i] = new Color(i & 0xFF, 255 - i & 0xFF, 0);
/*      */         }
/* 1602 */         else if (j == 2) {
/* 1603 */           for (int i = 0; i < 256; i++)
/* 1604 */             this.hColors[i] = new Color(i & 0xFF, i & 0xFF, 255 - i & 0xFF);
/*      */         }
/*      */       }
/* 1607 */       else if (ColorThresholder.this.colorSpace == 3)
/* 1608 */         if (j == 0) {
/* 1609 */           for (int i = 0; i < 256; i++)
/* 1610 */             this.hColors[i] = new Color(i & 0xFF, i & 0xFF, i & 0xFF);
/*      */         }
/* 1612 */         else if (j == 1) {
/* 1613 */           for (int i = 0; i < 256; i++)
/* 1614 */             this.hColors[i] = new Color((int)(36.0D + (255 - i) / 1.4D) & 0xFF, 255 - i & 0xFF, i & 0xFF);
/*      */         }
/* 1616 */         else if (j == 2)
/* 1617 */           for (int i = 0; i < 256; i++)
/* 1618 */             this.hColors[i] = new Color(i & 0xFF, 255 - i & 0xFF, (int)(83.0D + (255 - i) / 2.87D) & 0xFF);
/*      */     }
/*      */ 
/*      */     int[] getHistogram()
/*      */     {
/* 1625 */       return this.histogram;
/*      */     }
/*      */ 
/*      */     public void update(Graphics g) {
/* 1629 */       paint(g);
/*      */     }
/*      */ 
/*      */     public void paint(Graphics g) {
/* 1633 */       int hHist = 0;
/* 1634 */       if (this.histogram != null) {
/* 1635 */         if (this.os == null) {
/* 1636 */           this.os = createImage(256, 64);
/* 1637 */           this.osg = this.os.getGraphics();
/*      */ 
/* 1639 */           this.osg.setColor(new Color(140, 152, 144));
/* 1640 */           this.osg.fillRect(0, 0, 256, 64);
/* 1641 */           for (int i = 0; i < 256; i++) {
/* 1642 */             if (this.hColors != null) this.osg.setColor(this.hColors[i]);
/* 1643 */             hHist = 64 - 64 * this.histogram[i] / this.hmax - 6;
/* 1644 */             this.osg.drawLine(i, 64, i, hHist);
/* 1645 */             this.osg.setColor(Color.black);
/* 1646 */             this.osg.drawLine(i, hHist, i, hHist);
/*      */           }
/* 1648 */           this.osg.dispose();
/*      */         }
/* 1650 */         if (this.os != null) g.drawImage(this.os, 0, 0, this); 
/*      */       }
/* 1652 */       else { g.setColor(Color.white);
/* 1653 */         g.fillRect(0, 0, 256, 64);
/*      */       }
/* 1655 */       g.setColor(Color.black);
/* 1656 */       g.drawLine(0, 58, 256, 58);
/* 1657 */       g.drawRect(0, 0, 256, 64);
/* 1658 */       g.drawRect((int)this.minHue, 1, (int)(this.maxHue - this.minHue), 57);
/*      */     }
/*      */ 
/*      */     public void mousePressed(MouseEvent e)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseReleased(MouseEvent e)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseExited(MouseEvent e)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseClicked(MouseEvent e)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseEntered(MouseEvent e)
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.ColorThresholder
 * JD-Core Version:    0.6.2
 */