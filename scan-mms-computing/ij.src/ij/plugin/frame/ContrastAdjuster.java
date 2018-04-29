/*      */ package ij.plugin.frame;
/*      */ 
/*      */ import ij.CompositeImage;
/*      */ import ij.IJ;
/*      */ import ij.ImageJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.ImageStack;
/*      */ import ij.Prefs;
/*      */ import ij.Undo;
/*      */ import ij.WindowManager;
/*      */ import ij.gui.GUI;
/*      */ import ij.gui.GenericDialog;
/*      */ import ij.gui.Roi;
/*      */ import ij.gui.TrimmedButton;
/*      */ import ij.gui.YesNoCancelDialog;
/*      */ import ij.measure.Calibration;
/*      */ import ij.process.ByteProcessor;
/*      */ import ij.process.ColorProcessor;
/*      */ import ij.process.FloatProcessor;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.process.ImageStatistics;
/*      */ import ij.process.LUT;
/*      */ import ij.process.ShortProcessor;
/*      */ import ij.process.StackStatistics;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Button;
/*      */ import java.awt.Choice;
/*      */ import java.awt.Color;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Font;
/*      */ import java.awt.Frame;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Label;
/*      */ import java.awt.Panel;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Scrollbar;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.AdjustmentEvent;
/*      */ import java.awt.event.AdjustmentListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.WindowEvent;
/*      */ 
/*      */ public class ContrastAdjuster extends PlugInFrame
/*      */   implements Runnable, ActionListener, AdjustmentListener, ItemListener
/*      */ {
/*      */   public static final String LOC_KEY = "b&c.loc";
/*      */   static final int AUTO_THRESHOLD = 5000;
/*   21 */   static final String[] channelLabels = { "Red", "Green", "Blue", "Cyan", "Magenta", "Yellow", "All" };
/*   22 */   static final String[] altChannelLabels = { "Channel 1", "Channel 2", "Channel 3", "Channel 4", "Channel 5", "Channel 6", "All" };
/*   23 */   static final int[] channelConstants = { 4, 2, 1, 3, 5, 6, 7 };
/*   24 */   static final String[] ranges = { "Automatic", "8-bit (0-255)", "10-bit (0-1023)", "12-bit (0-4095)", "15-bit (0-32767)", "16-bit (0-65535)" };
/*      */ 
/*   26 */   ContrastPlot plot = new ContrastPlot();
/*      */   Thread thread;
/*      */   private static Frame instance;
/*   30 */   int minSliderValue = -1; int maxSliderValue = -1; int brightnessValue = -1; int contrastValue = -1;
/*   31 */   int sliderRange = 256;
/*      */   boolean doAutoAdjust;
/*      */   boolean doReset;
/*      */   boolean doSet;
/*      */   boolean doApplyLut;
/*      */   Panel panel;
/*      */   Panel tPanel;
/*      */   Button autoB;
/*      */   Button resetB;
/*      */   Button setB;
/*      */   Button applyB;
/*      */   int previousImageID;
/*      */   int previousType;
/*   38 */   int previousSlice = 1;
/*      */   Object previousSnapshot;
/*      */   ImageJ ij;
/*      */   double min;
/*      */   double max;
/*      */   double previousMin;
/*      */   double previousMax;
/*      */   double defaultMin;
/*      */   double defaultMax;
/*      */   int contrast;
/*      */   int brightness;
/*      */   boolean RGBImage;
/*      */   Scrollbar minSlider;
/*      */   Scrollbar maxSlider;
/*      */   Scrollbar contrastSlider;
/*      */   Scrollbar brightnessSlider;
/*      */   Label minLabel;
/*      */   Label maxLabel;
/*      */   Label windowLabel;
/*      */   Label levelLabel;
/*      */   boolean done;
/*      */   int autoThreshold;
/*      */   GridBagLayout gridbag;
/*      */   GridBagConstraints c;
/*   52 */   int y = 0;
/*      */   boolean windowLevel;
/*      */   boolean balance;
/*   54 */   Font monoFont = new Font("Monospaced", 0, 12);
/*   55 */   Font sanFont = new Font("SansSerif", 0, 12);
/*   56 */   int channels = 7;
/*      */   Choice choice;
/*      */   static final int RESET = 0;
/*      */   static final int AUTO = 1;
/*      */   static final int SET = 2;
/*      */   static final int APPLY = 3;
/*      */   static final int THRESHOLD = 4;
/*      */   static final int MIN = 5;
/*      */   static final int MAX = 6;
/*      */   static final int BRIGHTNESS = 7;
/*      */   static final int CONTRAST = 8;
/*      */   static final int UPDATE = 9;
/*      */ 
/*      */   public ContrastAdjuster()
/*      */   {
/*   60 */     super("B&C");
/*      */   }
/*      */ 
/*      */   public void run(String arg) {
/*   64 */     this.windowLevel = arg.equals("wl");
/*   65 */     this.balance = arg.equals("balance");
/*   66 */     if (this.windowLevel) {
/*   67 */       setTitle("W&L");
/*   68 */     } else if (this.balance) {
/*   69 */       setTitle("Color");
/*   70 */       this.channels = 4;
/*      */     }
/*      */ 
/*   73 */     if (instance != null) {
/*   74 */       if (!instance.getTitle().equals(getTitle())) {
/*   75 */         ContrastAdjuster ca = (ContrastAdjuster)instance;
/*   76 */         Prefs.saveLocation("b&c.loc", ca.getLocation());
/*   77 */         ca.close();
/*      */       } else {
/*   79 */         WindowManager.toFront(instance);
/*   80 */         return;
/*      */       }
/*      */     }
/*   83 */     instance = this;
/*   84 */     IJ.register(ContrastAdjuster.class);
/*   85 */     WindowManager.addWindow(this);
/*      */ 
/*   87 */     this.ij = IJ.getInstance();
/*   88 */     this.gridbag = new GridBagLayout();
/*   89 */     this.c = new GridBagConstraints();
/*   90 */     setLayout(this.gridbag);
/*      */ 
/*   93 */     this.c.gridx = 0;
/*   94 */     this.y = 0;
/*   95 */     this.c.gridy = (this.y++);
/*   96 */     this.c.fill = 1;
/*   97 */     this.c.anchor = 10;
/*   98 */     this.c.insets = new Insets(10, 10, 0, 10);
/*   99 */     this.gridbag.setConstraints(this.plot, this.c);
/*  100 */     add(this.plot);
/*  101 */     this.plot.addKeyListener(this.ij);
/*      */ 
/*  104 */     if (!this.windowLevel) {
/*  105 */       this.panel = new Panel();
/*  106 */       this.c.gridy = (this.y++);
/*  107 */       this.c.insets = new Insets(0, 10, 0, 10);
/*  108 */       this.gridbag.setConstraints(this.panel, this.c);
/*  109 */       this.panel.setLayout(new BorderLayout());
/*  110 */       this.minLabel = new Label("      ", 0);
/*  111 */       this.minLabel.setFont(this.monoFont);
/*  112 */       this.panel.add("West", this.minLabel);
/*  113 */       this.maxLabel = new Label("      ", 2);
/*  114 */       this.maxLabel.setFont(this.monoFont);
/*  115 */       this.panel.add("East", this.maxLabel);
/*  116 */       add(this.panel);
/*      */     }
/*      */ 
/*  120 */     if (!this.windowLevel) {
/*  121 */       this.minSlider = new Scrollbar(0, this.sliderRange / 2, 1, 0, this.sliderRange);
/*  122 */       this.c.gridy = (this.y++);
/*  123 */       this.c.insets = new Insets(2, 10, 0, 10);
/*  124 */       this.gridbag.setConstraints(this.minSlider, this.c);
/*  125 */       add(this.minSlider);
/*  126 */       this.minSlider.addAdjustmentListener(this);
/*  127 */       this.minSlider.addKeyListener(this.ij);
/*  128 */       this.minSlider.setUnitIncrement(1);
/*  129 */       this.minSlider.setFocusable(false);
/*  130 */       addLabel("Minimum", null);
/*      */     }
/*      */ 
/*  134 */     if (!this.windowLevel) {
/*  135 */       this.maxSlider = new Scrollbar(0, this.sliderRange / 2, 1, 0, this.sliderRange);
/*  136 */       this.c.gridy = (this.y++);
/*  137 */       this.c.insets = new Insets(2, 10, 0, 10);
/*  138 */       this.gridbag.setConstraints(this.maxSlider, this.c);
/*  139 */       add(this.maxSlider);
/*  140 */       this.maxSlider.addAdjustmentListener(this);
/*  141 */       this.maxSlider.addKeyListener(this.ij);
/*  142 */       this.maxSlider.setUnitIncrement(1);
/*  143 */       this.maxSlider.setFocusable(false);
/*  144 */       addLabel("Maximum", null);
/*      */     }
/*      */ 
/*  148 */     this.brightnessSlider = new Scrollbar(0, this.sliderRange / 2, 1, 0, this.sliderRange);
/*  149 */     this.c.gridy = (this.y++);
/*  150 */     this.c.insets = new Insets(this.windowLevel ? 12 : 2, 10, 0, 10);
/*  151 */     this.gridbag.setConstraints(this.brightnessSlider, this.c);
/*  152 */     add(this.brightnessSlider);
/*  153 */     this.brightnessSlider.addAdjustmentListener(this);
/*  154 */     this.brightnessSlider.addKeyListener(this.ij);
/*  155 */     this.brightnessSlider.setUnitIncrement(1);
/*  156 */     this.brightnessSlider.setFocusable(false);
/*  157 */     if (this.windowLevel)
/*  158 */       addLabel("Level: ", this.levelLabel = new TrimmedLabel("        "));
/*      */     else {
/*  160 */       addLabel("Brightness", null);
/*      */     }
/*      */ 
/*  163 */     if (!this.balance) {
/*  164 */       this.contrastSlider = new Scrollbar(0, this.sliderRange / 2, 1, 0, this.sliderRange);
/*  165 */       this.c.gridy = (this.y++);
/*  166 */       this.c.insets = new Insets(2, 10, 0, 10);
/*  167 */       this.gridbag.setConstraints(this.contrastSlider, this.c);
/*  168 */       add(this.contrastSlider);
/*  169 */       this.contrastSlider.addAdjustmentListener(this);
/*  170 */       this.contrastSlider.addKeyListener(this.ij);
/*  171 */       this.contrastSlider.setUnitIncrement(1);
/*  172 */       this.contrastSlider.setFocusable(false);
/*  173 */       if (this.windowLevel)
/*  174 */         addLabel("Window: ", this.windowLabel = new TrimmedLabel("        "));
/*      */       else {
/*  176 */         addLabel("Contrast", null);
/*      */       }
/*      */     }
/*      */ 
/*  180 */     if (this.balance) {
/*  181 */       this.c.gridy = (this.y++);
/*  182 */       this.c.insets = new Insets(5, 10, 0, 10);
/*  183 */       this.choice = new Choice();
/*  184 */       addBalanceChoices();
/*  185 */       this.gridbag.setConstraints(this.choice, this.c);
/*  186 */       this.choice.addItemListener(this);
/*      */ 
/*  188 */       add(this.choice);
/*      */     }
/*      */ 
/*  192 */     int trim = IJ.isMacOSX() ? 20 : 0;
/*  193 */     this.panel = new Panel();
/*  194 */     this.panel.setLayout(new GridLayout(0, 2, 0, 0));
/*  195 */     this.autoB = new TrimmedButton("Auto", trim);
/*  196 */     this.autoB.addActionListener(this);
/*  197 */     this.autoB.addKeyListener(this.ij);
/*  198 */     this.panel.add(this.autoB);
/*  199 */     this.resetB = new TrimmedButton("Reset", trim);
/*  200 */     this.resetB.addActionListener(this);
/*  201 */     this.resetB.addKeyListener(this.ij);
/*  202 */     this.panel.add(this.resetB);
/*  203 */     this.setB = new TrimmedButton("Set", trim);
/*  204 */     this.setB.addActionListener(this);
/*  205 */     this.setB.addKeyListener(this.ij);
/*  206 */     this.panel.add(this.setB);
/*  207 */     this.applyB = new TrimmedButton("Apply", trim);
/*  208 */     this.applyB.addActionListener(this);
/*  209 */     this.applyB.addKeyListener(this.ij);
/*  210 */     this.panel.add(this.applyB);
/*  211 */     this.c.gridy = (this.y++);
/*  212 */     this.c.insets = new Insets(8, 5, 10, 5);
/*  213 */     this.gridbag.setConstraints(this.panel, this.c);
/*  214 */     add(this.panel);
/*      */ 
/*  216 */     addKeyListener(this.ij);
/*  217 */     pack();
/*  218 */     Point loc = Prefs.getLocation("b&c.loc");
/*  219 */     if (loc != null)
/*  220 */       setLocation(loc);
/*      */     else
/*  222 */       GUI.center(this);
/*  223 */     if (IJ.isMacOSX()) setResizable(false);
/*  224 */     show();
/*      */ 
/*  226 */     this.thread = new Thread(this, "ContrastAdjuster");
/*      */ 
/*  228 */     this.thread.start();
/*  229 */     setup();
/*      */   }
/*      */ 
/*      */   void addBalanceChoices() {
/*  233 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  234 */     if ((imp != null) && (imp.isComposite()))
/*  235 */       for (int i = 0; i < altChannelLabels.length; i++)
/*  236 */         this.choice.addItem(altChannelLabels[i]);
/*      */     else
/*  238 */       for (int i = 0; i < channelLabels.length; i++)
/*  239 */         this.choice.addItem(channelLabels[i]);
/*      */   }
/*      */ 
/*      */   void addLabel(String text, Label label2)
/*      */   {
/*  244 */     if ((label2 == null) && (IJ.isMacOSX())) text = text + "    ";
/*  245 */     this.panel = new Panel();
/*  246 */     this.c.gridy = (this.y++);
/*  247 */     int bottomInset = IJ.isMacOSX() ? 4 : 0;
/*  248 */     this.c.insets = new Insets(0, 10, bottomInset, 0);
/*  249 */     this.gridbag.setConstraints(this.panel, this.c);
/*  250 */     this.panel.setLayout(new FlowLayout(label2 == null ? 1 : 0, 0, 0));
/*  251 */     Label label = new TrimmedLabel(text);
/*  252 */     label.setFont(this.sanFont);
/*  253 */     this.panel.add(label);
/*  254 */     if (label2 != null) {
/*  255 */       label2.setFont(this.monoFont);
/*  256 */       label2.setAlignment(0);
/*  257 */       this.panel.add(label2);
/*      */     }
/*  259 */     add(this.panel);
/*      */   }
/*      */ 
/*      */   void setup() {
/*  263 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  264 */     if (imp != null) {
/*  265 */       setup(imp);
/*  266 */       updatePlot();
/*  267 */       updateLabels(imp);
/*  268 */       imp.updateAndDraw();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
/*  273 */     Object source = e.getSource();
/*  274 */     if (source == this.minSlider)
/*  275 */       this.minSliderValue = this.minSlider.getValue();
/*  276 */     else if (source == this.maxSlider)
/*  277 */       this.maxSliderValue = this.maxSlider.getValue();
/*  278 */     else if (source == this.contrastSlider)
/*  279 */       this.contrastValue = this.contrastSlider.getValue();
/*      */     else
/*  281 */       this.brightnessValue = this.brightnessSlider.getValue();
/*  282 */     notify();
/*      */   }
/*      */ 
/*      */   public synchronized void actionPerformed(ActionEvent e) {
/*  286 */     Button b = (Button)e.getSource();
/*  287 */     if (b == null) return;
/*  288 */     if (b == this.resetB)
/*  289 */       this.doReset = true;
/*  290 */     else if (b == this.autoB)
/*  291 */       this.doAutoAdjust = true;
/*  292 */     else if (b == this.setB)
/*  293 */       this.doSet = true;
/*  294 */     else if (b == this.applyB)
/*  295 */       this.doApplyLut = true;
/*  296 */     notify();
/*      */   }
/*      */ 
/*      */   ImageProcessor setup(ImagePlus imp) {
/*  300 */     Roi roi = imp.getRoi();
/*  301 */     if (roi != null) roi.endPaste();
/*  302 */     ImageProcessor ip = imp.getProcessor();
/*  303 */     int type = imp.getType();
/*  304 */     int slice = imp.getCurrentSlice();
/*  305 */     this.RGBImage = (type == 4);
/*  306 */     boolean snapshotChanged = (this.RGBImage) && (this.previousSnapshot != null) && (((ColorProcessor)ip).getSnapshotPixels() != this.previousSnapshot);
/*  307 */     if ((imp.getID() != this.previousImageID) || (snapshotChanged) || (type != this.previousType) || (slice != this.previousSlice))
/*  308 */       setupNewImage(imp, ip);
/*  309 */     this.previousImageID = imp.getID();
/*  310 */     this.previousType = type;
/*  311 */     this.previousSlice = slice;
/*  312 */     return ip;
/*      */   }
/*      */ 
/*      */   void setupNewImage(ImagePlus imp, ImageProcessor ip)
/*      */   {
/*  317 */     Undo.reset();
/*  318 */     this.previousMin = this.min;
/*  319 */     this.previousMax = this.max;
/*  320 */     if (this.RGBImage) {
/*  321 */       ip.snapshot();
/*  322 */       this.previousSnapshot = ((ColorProcessor)ip).getSnapshotPixels();
/*      */     } else {
/*  324 */       this.previousSnapshot = null;
/*  325 */     }double min2 = imp.getDisplayRangeMin();
/*  326 */     double max2 = imp.getDisplayRangeMax();
/*  327 */     if (imp.getType() == 4) {
/*  328 */       min2 = 0.0D; max2 = 255.0D;
/*  329 */     }if (((ip instanceof ShortProcessor)) || ((ip instanceof FloatProcessor))) {
/*  330 */       imp.resetDisplayRange();
/*  331 */       this.defaultMin = imp.getDisplayRangeMin();
/*  332 */       this.defaultMax = imp.getDisplayRangeMax();
/*      */     } else {
/*  334 */       this.defaultMin = 0.0D;
/*  335 */       this.defaultMax = 255.0D;
/*      */     }
/*  337 */     setMinAndMax(imp, min2, max2);
/*  338 */     this.min = imp.getDisplayRangeMin();
/*  339 */     this.max = imp.getDisplayRangeMax();
/*  340 */     if (IJ.debugMode) {
/*  341 */       IJ.log("min: " + this.min);
/*  342 */       IJ.log("max: " + this.max);
/*  343 */       IJ.log("defaultMin: " + this.defaultMin);
/*  344 */       IJ.log("defaultMax: " + this.defaultMax);
/*      */     }
/*  346 */     this.plot.defaultMin = this.defaultMin;
/*  347 */     this.plot.defaultMax = this.defaultMax;
/*      */ 
/*  349 */     int valueRange = (int)(this.defaultMax - this.defaultMin);
/*  350 */     int newSliderRange = valueRange;
/*  351 */     if ((newSliderRange > 640) && (newSliderRange < 1280))
/*  352 */       newSliderRange /= 2;
/*  353 */     else if (newSliderRange >= 1280)
/*  354 */       newSliderRange /= 5;
/*  355 */     if (newSliderRange < 256) newSliderRange = 256;
/*  356 */     if (newSliderRange > 1024) newSliderRange = 1024;
/*  357 */     double displayRange = this.max - this.min;
/*  358 */     if ((valueRange >= 1280) && (valueRange != 0) && (displayRange / valueRange < 0.25D)) {
/*  359 */       newSliderRange = (int)(newSliderRange * 1.6666D);
/*      */     }
/*  361 */     if (newSliderRange != this.sliderRange) {
/*  362 */       this.sliderRange = newSliderRange;
/*  363 */       updateScrollBars(null, true);
/*      */     } else {
/*  365 */       updateScrollBars(null, false);
/*  366 */     }if (this.balance) {
/*  367 */       if (imp.isComposite()) {
/*  368 */         int channel = imp.getChannel();
/*  369 */         if (channel <= 4) {
/*  370 */           this.choice.select(channel - 1);
/*  371 */           this.channels = channelConstants[(channel - 1)];
/*      */         }
/*  373 */         if (this.choice.getItem(0).equals("Red")) {
/*  374 */           this.choice.removeAll();
/*  375 */           addBalanceChoices();
/*      */         }
/*      */       }
/*  378 */       else if (this.choice.getItem(0).equals("Channel 1")) {
/*  379 */         this.choice.removeAll();
/*  380 */         addBalanceChoices();
/*      */       }
/*      */     }
/*      */ 
/*  384 */     if (!this.doReset)
/*  385 */       plotHistogram(imp);
/*  386 */     this.autoThreshold = 0;
/*  387 */     if (imp.isComposite())
/*  388 */       IJ.setKeyUp(16);
/*      */   }
/*      */ 
/*      */   void setMinAndMax(ImagePlus imp, double min, double max) {
/*  392 */     boolean rgb = imp.getType() == 4;
/*  393 */     if ((this.channels != 7) && (rgb))
/*  394 */       imp.setDisplayRange(min, max, this.channels);
/*      */     else
/*  396 */       imp.setDisplayRange(min, max);
/*  397 */     if (!rgb)
/*  398 */       imp.getProcessor().setSnapshotPixels(null);
/*      */   }
/*      */ 
/*      */   void updatePlot()
/*      */   {
/*  403 */     this.plot.min = this.min;
/*  404 */     this.plot.max = this.max;
/*  405 */     this.plot.repaint();
/*      */   }
/*      */ 
/*      */   void updateLabels(ImagePlus imp) {
/*  409 */     double min = imp.getDisplayRangeMin();
/*  410 */     double max = imp.getDisplayRangeMax();
/*  411 */     int type = imp.getType();
/*  412 */     Calibration cal = imp.getCalibration();
/*  413 */     boolean realValue = type == 2;
/*  414 */     if (cal.calibrated()) {
/*  415 */       min = cal.getCValue((int)min);
/*  416 */       max = cal.getCValue((int)max);
/*  417 */       if (type != 1)
/*  418 */         realValue = true;
/*      */     }
/*  420 */     int digits = realValue ? 2 : 0;
/*  421 */     if (this.windowLevel)
/*      */     {
/*  423 */       double window = max - min;
/*  424 */       double level = min + window / 2.0D;
/*  425 */       this.windowLabel.setText(IJ.d2s(window, digits));
/*  426 */       this.levelLabel.setText(IJ.d2s(level, digits));
/*      */     } else {
/*  428 */       this.minLabel.setText(IJ.d2s(min, digits));
/*  429 */       this.maxLabel.setText(IJ.d2s(max, digits));
/*      */     }
/*      */   }
/*      */ 
/*      */   void updateScrollBars(Scrollbar sb, boolean newRange) {
/*  434 */     if ((sb == null) || (sb != this.contrastSlider)) {
/*  435 */       double mid = this.sliderRange / 2;
/*  436 */       double c = (this.defaultMax - this.defaultMin) / (this.max - this.min) * mid;
/*  437 */       if (c > mid)
/*  438 */         c = this.sliderRange - (this.max - this.min) / (this.defaultMax - this.defaultMin) * mid;
/*  439 */       this.contrast = ((int)c);
/*  440 */       if (this.contrastSlider != null) {
/*  441 */         if (newRange)
/*  442 */           this.contrastSlider.setValues(this.contrast, 1, 0, this.sliderRange);
/*      */         else
/*  444 */           this.contrastSlider.setValue(this.contrast);
/*      */       }
/*      */     }
/*  447 */     if ((sb == null) || (sb != this.brightnessSlider)) {
/*  448 */       double level = this.min + (this.max - this.min) / 2.0D;
/*  449 */       double normalizedLevel = 1.0D - (level - this.defaultMin) / (this.defaultMax - this.defaultMin);
/*  450 */       this.brightness = ((int)(normalizedLevel * this.sliderRange));
/*  451 */       if (newRange)
/*  452 */         this.brightnessSlider.setValues(this.brightness, 1, 0, this.sliderRange);
/*      */       else
/*  454 */         this.brightnessSlider.setValue(this.brightness);
/*      */     }
/*  456 */     if ((this.minSlider != null) && ((sb == null) || (sb != this.minSlider))) {
/*  457 */       if (newRange)
/*  458 */         this.minSlider.setValues(scaleDown(this.min), 1, 0, this.sliderRange);
/*      */       else
/*  460 */         this.minSlider.setValue(scaleDown(this.min));
/*      */     }
/*  462 */     if ((this.maxSlider != null) && ((sb == null) || (sb != this.maxSlider)))
/*  463 */       if (newRange)
/*  464 */         this.maxSlider.setValues(scaleDown(this.max), 1, 0, this.sliderRange);
/*      */       else
/*  466 */         this.maxSlider.setValue(scaleDown(this.max));
/*      */   }
/*      */ 
/*      */   int scaleDown(double v)
/*      */   {
/*  471 */     if (v < this.defaultMin) v = this.defaultMin;
/*  472 */     if (v > this.defaultMax) v = this.defaultMax;
/*  473 */     return (int)((v - this.defaultMin) * (this.sliderRange - 1.0D) / (this.defaultMax - this.defaultMin));
/*      */   }
/*      */ 
/*      */   void doMasking(ImagePlus imp, ImageProcessor ip)
/*      */   {
/*  478 */     ImageProcessor mask = imp.getMask();
/*  479 */     if (mask != null)
/*  480 */       ip.reset(mask);
/*      */   }
/*      */ 
/*      */   void adjustMin(ImagePlus imp, ImageProcessor ip, double minvalue) {
/*  484 */     this.min = (this.defaultMin + minvalue * (this.defaultMax - this.defaultMin) / (this.sliderRange - 1.0D));
/*  485 */     if (this.max > this.defaultMax)
/*  486 */       this.max = this.defaultMax;
/*  487 */     if (this.min > this.max)
/*  488 */       this.max = this.min;
/*  489 */     setMinAndMax(imp, this.min, this.max);
/*  490 */     if (this.min == this.max)
/*  491 */       setThreshold(ip);
/*  492 */     if (this.RGBImage) doMasking(imp, ip);
/*  493 */     updateScrollBars(this.minSlider, false);
/*      */   }
/*      */ 
/*      */   void adjustMax(ImagePlus imp, ImageProcessor ip, double maxvalue) {
/*  497 */     this.max = (this.defaultMin + maxvalue * (this.defaultMax - this.defaultMin) / (this.sliderRange - 1.0D));
/*      */ 
/*  499 */     if (this.min < this.defaultMin)
/*  500 */       this.min = this.defaultMin;
/*  501 */     if (this.max < this.min)
/*  502 */       this.min = this.max;
/*  503 */     setMinAndMax(imp, this.min, this.max);
/*  504 */     if (this.min == this.max)
/*  505 */       setThreshold(ip);
/*  506 */     if (this.RGBImage) doMasking(imp, ip);
/*  507 */     updateScrollBars(this.maxSlider, false);
/*      */   }
/*      */ 
/*      */   void adjustBrightness(ImagePlus imp, ImageProcessor ip, double bvalue) {
/*  511 */     double center = this.defaultMin + (this.defaultMax - this.defaultMin) * ((this.sliderRange - bvalue) / this.sliderRange);
/*  512 */     double width = this.max - this.min;
/*  513 */     this.min = (center - width / 2.0D);
/*  514 */     this.max = (center + width / 2.0D);
/*  515 */     setMinAndMax(imp, this.min, this.max);
/*  516 */     if (this.min == this.max)
/*  517 */       setThreshold(ip);
/*  518 */     if (this.RGBImage) doMasking(imp, ip);
/*  519 */     updateScrollBars(this.brightnessSlider, false);
/*      */   }
/*      */ 
/*      */   void adjustContrast(ImagePlus imp, ImageProcessor ip, int cvalue)
/*      */   {
/*  524 */     double center = this.min + (this.max - this.min) / 2.0D;
/*  525 */     double range = this.defaultMax - this.defaultMin;
/*  526 */     double mid = this.sliderRange / 2;
/*      */     double slope;
/*      */     double slope;
/*  527 */     if (cvalue <= mid)
/*  528 */       slope = cvalue / mid;
/*      */     else
/*  530 */       slope = mid / (this.sliderRange - cvalue);
/*  531 */     if (slope > 0.0D) {
/*  532 */       this.min = (center - 0.5D * range / slope);
/*  533 */       this.max = (center + 0.5D * range / slope);
/*      */     }
/*  535 */     setMinAndMax(imp, this.min, this.max);
/*  536 */     if (this.RGBImage) doMasking(imp, ip);
/*  537 */     updateScrollBars(this.contrastSlider, false);
/*      */   }
/*      */ 
/*      */   void reset(ImagePlus imp, ImageProcessor ip) {
/*  541 */     if (this.RGBImage)
/*  542 */       ip.reset();
/*  543 */     if (((ip instanceof ShortProcessor)) || ((ip instanceof FloatProcessor))) {
/*  544 */       imp.resetDisplayRange();
/*  545 */       this.defaultMin = imp.getDisplayRangeMin();
/*  546 */       this.defaultMax = imp.getDisplayRangeMax();
/*  547 */       this.plot.defaultMin = this.defaultMin;
/*  548 */       this.plot.defaultMax = this.defaultMax;
/*      */     }
/*  550 */     this.min = this.defaultMin;
/*  551 */     this.max = this.defaultMax;
/*  552 */     setMinAndMax(imp, this.min, this.max);
/*  553 */     updateScrollBars(null, false);
/*  554 */     plotHistogram(imp);
/*  555 */     this.autoThreshold = 0;
/*      */   }
/*      */ 
/*      */   void plotHistogram(ImagePlus imp)
/*      */   {
/*      */     ImageStatistics stats;
/*      */     ImageStatistics stats;
/*  560 */     if ((this.balance) && ((this.channels == 4) || (this.channels == 2) || (this.channels == 1)) && (imp.getType() == 4)) {
/*  561 */       int w = imp.getWidth();
/*  562 */       int h = imp.getHeight();
/*  563 */       byte[] r = new byte[w * h];
/*  564 */       byte[] g = new byte[w * h];
/*  565 */       byte[] b = new byte[w * h];
/*  566 */       ((ColorProcessor)imp.getProcessor()).getRGB(r, g, b);
/*  567 */       byte[] pixels = null;
/*  568 */       if (this.channels == 4)
/*  569 */         pixels = r;
/*  570 */       else if (this.channels == 2)
/*  571 */         pixels = g;
/*  572 */       else if (this.channels == 1)
/*  573 */         pixels = b;
/*  574 */       ImageProcessor ip = new ByteProcessor(w, h, pixels, null);
/*  575 */       stats = ImageStatistics.getStatistics(ip, 0, imp.getCalibration());
/*      */     } else {
/*  577 */       int range = imp.getType() == 1 ? ImagePlus.getDefault16bitRange() : 0;
/*      */       ImageStatistics stats;
/*  578 */       if ((range != 0) && (imp.getProcessor().getMax() == Math.pow(2.0D, range) - 1.0D) && (!imp.getCalibration().isSigned16Bit())) {
/*  579 */         ImagePlus imp2 = new ImagePlus("Temp", imp.getProcessor());
/*  580 */         stats = new StackStatistics(imp2, 256, 0.0D, Math.pow(2.0D, range));
/*      */       } else {
/*  582 */         stats = imp.getStatistics();
/*      */       }
/*      */     }
/*  584 */     Color color = Color.gray;
/*  585 */     if ((imp.isComposite()) && ((!this.balance) || (this.channels != 7)))
/*  586 */       color = ((CompositeImage)imp).getChannelColor();
/*  587 */     this.plot.setHistogram(stats, color);
/*      */   }
/*      */ 
/*      */   void apply(ImagePlus imp, ImageProcessor ip) {
/*  591 */     if ((this.balance) && (imp.isComposite()))
/*  592 */       return;
/*  593 */     String option = null;
/*  594 */     if (this.RGBImage)
/*  595 */       imp.unlock();
/*  596 */     if (!imp.lock())
/*  597 */       return;
/*  598 */     if (this.RGBImage) {
/*  599 */       if (imp.getStackSize() > 1) {
/*  600 */         applyRGBStack(imp);
/*      */       } else {
/*  602 */         ip.snapshot();
/*  603 */         reset(imp, ip);
/*  604 */         imp.changes = true;
/*  605 */         if (Recorder.record) {
/*  606 */           if (Recorder.scriptMode())
/*  607 */             Recorder.recordCall("IJ.run(imp, \"Apply LUT\", \"\");");
/*      */           else
/*  609 */             Recorder.record("run", "Apply LUT");
/*      */         }
/*      */       }
/*  612 */       imp.unlock();
/*  613 */       return;
/*      */     }
/*  615 */     if (imp.isComposite()) {
/*  616 */       imp.unlock();
/*  617 */       ((CompositeImage)imp).updateAllChannelsAndDraw();
/*  618 */       return;
/*      */     }
/*  620 */     if (imp.getType() != 0) {
/*  621 */       IJ.beep();
/*  622 */       IJ.showStatus("Apply requires an 8-bit grayscale image or an RGB stack");
/*  623 */       imp.unlock();
/*  624 */       return;
/*      */     }
/*  626 */     int[] table = new int[256];
/*  627 */     int min = (int)imp.getDisplayRangeMin();
/*  628 */     int max = (int)imp.getDisplayRangeMax();
/*  629 */     for (int i = 0; i < 256; i++) {
/*  630 */       if (i <= min)
/*  631 */         table[i] = 0;
/*  632 */       else if (i >= max)
/*  633 */         table[i] = 255;
/*      */       else
/*  635 */         table[i] = ((int)((i - min) / (max - min) * 255.0D));
/*      */     }
/*  637 */     ip.setRoi(imp.getRoi());
/*  638 */     if (imp.getStackSize() > 1) {
/*  639 */       ImageStack stack = imp.getStack();
/*  640 */       YesNoCancelDialog d = new YesNoCancelDialog(this, "Entire Stack?", "Apply LUT to all " + stack.getSize() + " slices in the stack?");
/*      */ 
/*  642 */       if (d.cancelPressed()) {
/*  643 */         imp.unlock(); return;
/*  644 */       }if (d.yesPressed()) {
/*  645 */         int current = imp.getCurrentSlice();
/*  646 */         ImageProcessor mask = imp.getMask();
/*  647 */         for (int i = 1; i <= imp.getStackSize(); i++) {
/*  648 */           imp.setSlice(i);
/*  649 */           ip = imp.getProcessor();
/*  650 */           if (mask != null) ip.snapshot();
/*  651 */           ip.applyTable(table);
/*  652 */           ip.reset(mask);
/*      */         }
/*  654 */         imp.setSlice(current);
/*  655 */         option = "stack";
/*      */       } else {
/*  657 */         if (ip.getMask() != null) ip.snapshot();
/*  658 */         ip.applyTable(table);
/*  659 */         ip.reset(ip.getMask());
/*  660 */         option = "slice";
/*      */       }
/*      */     } else {
/*  663 */       if (ip.getMask() != null) ip.snapshot();
/*  664 */       ip.applyTable(table);
/*  665 */       ip.reset(ip.getMask());
/*      */     }
/*  667 */     reset(imp, ip);
/*  668 */     imp.changes = true;
/*  669 */     imp.unlock();
/*  670 */     if (Recorder.record)
/*  671 */       if (Recorder.scriptMode()) {
/*  672 */         if (option == null) option = "";
/*  673 */         Recorder.recordCall("IJ.run(imp, \"Apply LUT\", \"" + option + "\");");
/*      */       }
/*  675 */       else if (option != null) {
/*  676 */         Recorder.record("run", "Apply LUT", option);
/*      */       } else {
/*  678 */         Recorder.record("run", "Apply LUT");
/*      */       }
/*      */   }
/*      */ 
/*      */   void applyRGBStack(ImagePlus imp)
/*      */   {
/*  684 */     double min = imp.getDisplayRangeMin();
/*  685 */     double max = imp.getDisplayRangeMax();
/*  686 */     if (IJ.debugMode) IJ.log("applyRGBStack: " + min + "-" + max);
/*  687 */     int current = imp.getCurrentSlice();
/*  688 */     int n = imp.getStackSize();
/*  689 */     if (!IJ.showMessageWithCancel("Update Entire Stack?", "Apply brightness and contrast settings\nto all " + n + " slices in the stack?\n \n" + "NOTE: There is no Undo for this operation."))
/*      */     {
/*  693 */       return;
/*  694 */     }ImageProcessor mask = imp.getMask();
/*  695 */     Rectangle roi = imp.getRoi() != null ? imp.getRoi().getBounds() : null;
/*  696 */     ImageStack stack = imp.getStack();
/*  697 */     for (int i = 1; i <= n; i++) {
/*  698 */       IJ.showProgress(i, n);
/*  699 */       IJ.showStatus(i + "/" + n);
/*  700 */       if (i != current) {
/*  701 */         ImageProcessor ip = stack.getProcessor(i);
/*  702 */         ip.setRoi(roi);
/*  703 */         if (mask != null) ip.snapshot();
/*  704 */         if (this.channels != 7)
/*  705 */           ((ColorProcessor)ip).setMinAndMax(min, max, this.channels);
/*      */         else
/*  707 */           ip.setMinAndMax(min, max);
/*  708 */         if (mask != null) ip.reset(mask);
/*      */       }
/*      */     }
/*  711 */     imp.setStack(null, stack);
/*  712 */     imp.setSlice(current);
/*  713 */     imp.changes = true;
/*  714 */     if (Recorder.record)
/*  715 */       if (Recorder.scriptMode())
/*  716 */         Recorder.recordCall("IJ.run(imp, \"Apply LUT\", \"stack\");");
/*      */       else
/*  718 */         Recorder.record("run", "Apply LUT", "stack");
/*      */   }
/*      */ 
/*      */   void setThreshold(ImageProcessor ip)
/*      */   {
/*  723 */     if (!(ip instanceof ByteProcessor))
/*  724 */       return;
/*  725 */     if (((ByteProcessor)ip).isInvertedLut())
/*  726 */       ip.setThreshold(this.max, 255.0D, 2);
/*      */     else
/*  728 */       ip.setThreshold(0.0D, this.max, 2);
/*      */   }
/*      */ 
/*      */   void autoAdjust(ImagePlus imp, ImageProcessor ip) {
/*  732 */     if (this.RGBImage)
/*  733 */       ip.reset();
/*  734 */     Calibration cal = imp.getCalibration();
/*  735 */     imp.setCalibration(null);
/*  736 */     ImageStatistics stats = imp.getStatistics();
/*  737 */     imp.setCalibration(cal);
/*  738 */     int limit = stats.pixelCount / 10;
/*  739 */     int[] histogram = stats.histogram;
/*  740 */     if (this.autoThreshold < 10)
/*  741 */       this.autoThreshold = 5000;
/*      */     else
/*  743 */       this.autoThreshold /= 2; int threshold = stats.pixelCount / this.autoThreshold;
/*  745 */     int i = -1;
/*  746 */     boolean found = false;
/*      */     int count;
/*      */     do { i++;
/*  750 */       count = histogram[i];
/*  751 */       if (count > limit) count = 0;
/*  752 */       found = count > threshold; }
/*  753 */     while ((!found) && (i < 255));
/*  754 */     int hmin = i;
/*  755 */     i = 256;
/*      */     do {
/*  757 */       i--;
/*  758 */       count = histogram[i];
/*  759 */       if (count > limit) count = 0;
/*  760 */       found = count > threshold;
/*  761 */     }while ((!found) && (i > 0));
/*  762 */     int hmax = i;
/*  763 */     Roi roi = imp.getRoi();
/*  764 */     if (hmax >= hmin) {
/*  765 */       if (this.RGBImage) imp.killRoi();
/*  766 */       this.min = (stats.histMin + hmin * stats.binSize);
/*  767 */       this.max = (stats.histMin + hmax * stats.binSize);
/*  768 */       if (this.min == this.max) {
/*  769 */         this.min = stats.min; this.max = stats.max;
/*  770 */       }setMinAndMax(imp, this.min, this.max);
/*  771 */       if ((this.RGBImage) && (roi != null)) imp.setRoi(roi); 
/*      */     }
/*  773 */     else { reset(imp, ip);
/*  774 */       return;
/*      */     }
/*  776 */     updateScrollBars(null, false);
/*      */ 
/*  782 */     if (Recorder.record)
/*  783 */       if (Recorder.scriptMode())
/*  784 */         Recorder.recordCall("IJ.run(imp, \"Enhance Contrast\", \"saturated=0.35\");");
/*      */       else
/*  786 */         Recorder.record("run", "Enhance Contrast", "saturated=0.35");
/*      */   }
/*      */ 
/*      */   void setMinAndMax(ImagePlus imp, ImageProcessor ip)
/*      */   {
/*  791 */     this.min = imp.getDisplayRangeMin();
/*  792 */     this.max = imp.getDisplayRangeMax();
/*  793 */     Calibration cal = imp.getCalibration();
/*  794 */     int digits = ((ip instanceof FloatProcessor)) || (cal.calibrated()) ? 2 : 0;
/*  795 */     double minValue = cal.getCValue(this.min);
/*  796 */     double maxValue = cal.getCValue(this.max);
/*  797 */     int channels = imp.getNChannels();
/*  798 */     GenericDialog gd = new GenericDialog("Set Display Range");
/*  799 */     gd.addNumericField("Minimum displayed value: ", minValue, digits);
/*  800 */     gd.addNumericField("Maximum displayed value: ", maxValue, digits);
/*  801 */     gd.addChoice("Unsigned 16-bit range:", ranges, ranges[getRangeIndex()]);
/*  802 */     String label = "Propagate to all other ";
/*  803 */     label = label + "open images";
/*  804 */     gd.addCheckbox(label, false);
/*  805 */     boolean allChannels = false;
/*  806 */     if ((imp.isComposite()) && (channels > 1)) {
/*  807 */       label = "Propagate to the other ";
/*  808 */       label = label + (channels - 1) + " channels of this image";
/*  809 */       gd.addCheckbox(label, allChannels);
/*      */     }
/*  811 */     gd.showDialog();
/*  812 */     if (gd.wasCanceled())
/*  813 */       return;
/*  814 */     minValue = gd.getNextNumber();
/*  815 */     maxValue = gd.getNextNumber();
/*  816 */     minValue = cal.getRawValue(minValue);
/*  817 */     maxValue = cal.getRawValue(maxValue);
/*  818 */     int rangeIndex = gd.getNextChoiceIndex();
/*  819 */     int range1 = ImagePlus.getDefault16bitRange();
/*  820 */     int range2 = setRange(rangeIndex);
/*  821 */     if ((range1 != range2) && (imp.getType() == 1) && (!cal.isSigned16Bit())) {
/*  822 */       reset(imp, ip);
/*  823 */       minValue = imp.getDisplayRangeMin();
/*  824 */       maxValue = imp.getDisplayRangeMax();
/*      */     }
/*  826 */     boolean propagate = gd.getNextBoolean();
/*  827 */     if ((imp.isComposite()) && (channels > 1))
/*  828 */       allChannels = gd.getNextBoolean();
/*  829 */     if (maxValue >= minValue) {
/*  830 */       this.min = minValue;
/*  831 */       this.max = maxValue;
/*  832 */       setMinAndMax(imp, this.min, this.max);
/*  833 */       updateScrollBars(null, false);
/*  834 */       if (this.RGBImage) doMasking(imp, ip);
/*  835 */       if (allChannels) {
/*  836 */         int channel = imp.getChannel();
/*  837 */         for (int c = 1; c <= channels; c++) {
/*  838 */           imp.setPositionWithoutUpdate(c, imp.getSlice(), imp.getFrame());
/*  839 */           imp.setDisplayRange(this.min, this.max);
/*      */         }
/*  841 */         ((CompositeImage)imp).reset();
/*  842 */         imp.setPosition(channel, imp.getSlice(), imp.getFrame());
/*      */       }
/*  844 */       if (propagate)
/*  845 */         propagate(imp);
/*  846 */       if (Recorder.record) {
/*  847 */         if (imp.getBitDepth() == 32) {
/*  848 */           recordSetMinAndMax(this.min, this.max);
/*      */         } else {
/*  850 */           int imin = (int)this.min;
/*  851 */           int imax = (int)this.max;
/*  852 */           if (cal.isSigned16Bit()) {
/*  853 */             imin = (int)cal.getCValue(imin);
/*  854 */             imax = (int)cal.getCValue(imax);
/*      */           }
/*  856 */           recordSetMinAndMax(imin, imax);
/*      */         }
/*  858 */         if (Recorder.scriptMode())
/*  859 */           Recorder.recordCall("ImagePlus.setDefault16bitRange(" + range2 + ");");
/*      */         else
/*  861 */           Recorder.recordString("call(\"ij.ImagePlus.setDefault16bitRange\", " + range2 + ");\n");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void propagate(ImagePlus img)
/*      */   {
/*  868 */     int[] list = WindowManager.getIDList();
/*  869 */     if (list == null) return;
/*  870 */     int nImages = list.length;
/*  871 */     if (nImages <= 1) return;
/*  872 */     ImageProcessor ip = img.getProcessor();
/*  873 */     double min = ip.getMin();
/*  874 */     double max = ip.getMax();
/*  875 */     int depth = img.getBitDepth();
/*  876 */     if (depth == 24) return;
/*  877 */     int id = img.getID();
/*  878 */     if (img.isComposite()) {
/*  879 */       int nChannels = img.getNChannels();
/*  880 */       for (int i = 0; i < nImages; i++) {
/*  881 */         ImagePlus img2 = WindowManager.getImage(list[i]);
/*  882 */         if (img2 != null) {
/*  883 */           int nChannels2 = img2.getNChannels();
/*  884 */           if ((img2.isComposite()) && (img2.getBitDepth() == depth) && (img2.getID() != id) && (img2.getNChannels() == nChannels) && (img2.getWindow() != null))
/*      */           {
/*  886 */             int channel = img2.getChannel();
/*  887 */             for (int c = 1; c <= nChannels; c++) {
/*  888 */               LUT lut = ((CompositeImage)img).getChannelLut(c);
/*  889 */               img2.setPosition(c, img2.getSlice(), img2.getFrame());
/*  890 */               img2.setDisplayRange(lut.min, lut.max);
/*  891 */               img2.updateAndDraw();
/*      */             }
/*  893 */             img2.setPosition(channel, img2.getSlice(), img2.getFrame());
/*      */           }
/*      */         }
/*      */       }
/*      */     } else { for (int i = 0; i < nImages; i++) {
/*  898 */         ImagePlus img2 = WindowManager.getImage(list[i]);
/*  899 */         if ((img2 != null) && (img2.getBitDepth() == depth) && (img2.getID() != id) && (img2.getNChannels() == 1) && (img2.getWindow() != null))
/*      */         {
/*  901 */           ImageProcessor ip2 = img2.getProcessor();
/*  902 */           ip2.setMinAndMax(min, max);
/*  903 */           img2.updateAndDraw();
/*      */         }
/*      */       } }
/*      */   }
/*      */ 
/*      */   int getRangeIndex()
/*      */   {
/*  910 */     int range = ImagePlus.getDefault16bitRange();
/*  911 */     int index = 0;
/*  912 */     if (range == 8) index = 1;
/*  913 */     else if (range == 10) index = 2;
/*  914 */     else if (range == 12) index = 3;
/*  915 */     else if (range == 15) index = 4;
/*  916 */     else if (range == 16) index = 5;
/*  917 */     return index;
/*      */   }
/*      */ 
/*      */   int setRange(int index) {
/*  921 */     int range = 0;
/*  922 */     if (index == 1) range = 8;
/*  923 */     else if (index == 2) range = 10;
/*  924 */     else if (index == 3) range = 12;
/*  925 */     else if (index == 4) range = 15;
/*  926 */     else if (index == 5) range = 16;
/*  927 */     ImagePlus.setDefault16bitRange(range);
/*  928 */     return range;
/*      */   }
/*      */ 
/*      */   void setWindowLevel(ImagePlus imp, ImageProcessor ip) {
/*  932 */     this.min = imp.getDisplayRangeMin();
/*  933 */     this.max = imp.getDisplayRangeMax();
/*  934 */     Calibration cal = imp.getCalibration();
/*  935 */     int digits = ((ip instanceof FloatProcessor)) || (cal.calibrated()) ? 2 : 0;
/*  936 */     double minValue = cal.getCValue(this.min);
/*  937 */     double maxValue = cal.getCValue(this.max);
/*      */ 
/*  939 */     double windowValue = maxValue - minValue;
/*  940 */     double levelValue = minValue + windowValue / 2.0D;
/*  941 */     GenericDialog gd = new GenericDialog("Set W&L");
/*  942 */     gd.addNumericField("Window Center (Level): ", levelValue, digits);
/*  943 */     gd.addNumericField("Window Width: ", windowValue, digits);
/*  944 */     gd.addCheckbox("Propagate to all open images", false);
/*  945 */     gd.showDialog();
/*  946 */     if (gd.wasCanceled())
/*  947 */       return;
/*  948 */     levelValue = gd.getNextNumber();
/*  949 */     windowValue = gd.getNextNumber();
/*  950 */     minValue = levelValue - windowValue / 2.0D;
/*  951 */     maxValue = levelValue + windowValue / 2.0D;
/*  952 */     minValue = cal.getRawValue(minValue);
/*  953 */     maxValue = cal.getRawValue(maxValue);
/*  954 */     boolean propagate = gd.getNextBoolean();
/*  955 */     if (maxValue >= minValue) {
/*  956 */       this.min = minValue;
/*  957 */       this.max = maxValue;
/*  958 */       setMinAndMax(imp, minValue, maxValue);
/*  959 */       updateScrollBars(null, false);
/*  960 */       if (this.RGBImage) doMasking(imp, ip);
/*  961 */       if (propagate)
/*  962 */         IJ.runMacroFile("ij.jar:PropagateMinAndMax");
/*  963 */       if (Recorder.record)
/*  964 */         if (imp.getBitDepth() == 32) {
/*  965 */           recordSetMinAndMax(this.min, this.max);
/*      */         } else {
/*  967 */           int imin = (int)this.min;
/*  968 */           int imax = (int)this.max;
/*  969 */           if (cal.isSigned16Bit()) {
/*  970 */             imin = (int)cal.getCValue(imin);
/*  971 */             imax = (int)cal.getCValue(imax);
/*      */           }
/*  973 */           recordSetMinAndMax(imin, imax);
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   void recordSetMinAndMax(double min, double max)
/*      */   {
/*  980 */     if (((int)min == min) && ((int)max == max)) {
/*  981 */       int imin = (int)min; int imax = (int)max;
/*  982 */       if (Recorder.scriptMode())
/*  983 */         Recorder.recordCall("IJ.setMinAndMax(imp, " + imin + ", " + imax + ");");
/*      */       else
/*  985 */         Recorder.record("setMinAndMax", imin, imax);
/*      */     }
/*  987 */     else if (Recorder.scriptMode()) {
/*  988 */       Recorder.recordCall("IJ.setMinAndMax(imp, " + min + ", " + max + ");");
/*      */     } else {
/*  990 */       Recorder.record("setMinAndMax", min, max);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/*  999 */     while (!this.done) {
/* 1000 */       synchronized (this) {
/*      */         try { wait(); } catch (InterruptedException e) {
/*      */         }
/*      */       }
/* 1004 */       doUpdate();
/*      */     }
/*      */   }
/*      */ 
/*      */   void doUpdate()
/*      */   {
/* 1012 */     int minvalue = this.minSliderValue;
/* 1013 */     int maxvalue = this.maxSliderValue;
/* 1014 */     int bvalue = this.brightnessValue;
/* 1015 */     int cvalue = this.contrastValue;
/*      */     int action;
/* 1016 */     if (this.doReset) { action = 0; }
/*      */     else
/*      */     {
/* 1017 */       int action;
/* 1017 */       if (this.doAutoAdjust) { action = 1; }
/*      */       else
/*      */       {
/* 1018 */         int action;
/* 1018 */         if (this.doSet) { action = 2; }
/*      */         else
/*      */         {
/* 1019 */           int action;
/* 1019 */           if (this.doApplyLut) { action = 3; }
/*      */           else
/*      */           {
/* 1020 */             int action;
/* 1020 */             if (this.minSliderValue >= 0) { action = 5; }
/*      */             else
/*      */             {
/* 1021 */               int action;
/* 1021 */               if (this.maxSliderValue >= 0) { action = 6; }
/*      */               else
/*      */               {
/* 1022 */                 int action;
/* 1022 */                 if (this.brightnessValue >= 0) { action = 7; }
/*      */                 else
/*      */                 {
/* 1023 */                   int action;
/* 1023 */                   if (this.contrastValue >= 0) action = 8;
/*      */                   else
/*      */                     return;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     int action;
/* 1025 */     this.minSliderValue = (this.maxSliderValue = this.brightnessValue = this.contrastValue = -1);
/* 1026 */     this.doReset = (this.doAutoAdjust = this.doSet = this.doApplyLut = 0);
/* 1027 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1028 */     if (imp == null) {
/* 1029 */       IJ.beep();
/* 1030 */       IJ.showStatus("No image");
/* 1031 */       return;
/*      */     }
/* 1033 */     ImageProcessor ip = imp.getProcessor();
/* 1034 */     if ((this.RGBImage) && (!imp.lock())) {
/* 1035 */       imp = null; return;
/*      */     }
/* 1037 */     switch (action) {
/*      */     case 0:
/* 1039 */       reset(imp, ip);
/* 1040 */       if (Recorder.record)
/* 1041 */         if (Recorder.scriptMode())
/* 1042 */           Recorder.recordCall("IJ.resetMinAndMax(imp);");
/*      */         else
/* 1044 */           Recorder.record("resetMinAndMax"); 
/* 1044 */       break;
/*      */     case 1:
/* 1047 */       autoAdjust(imp, ip); break;
/*      */     case 2:
/* 1048 */       if (this.windowLevel) setWindowLevel(imp, ip); else setMinAndMax(imp, ip); break;
/*      */     case 3:
/* 1049 */       apply(imp, ip); break;
/*      */     case 5:
/* 1050 */       adjustMin(imp, ip, minvalue); break;
/*      */     case 6:
/* 1051 */       adjustMax(imp, ip, maxvalue); break;
/*      */     case 7:
/* 1052 */       adjustBrightness(imp, ip, bvalue); break;
/*      */     case 8:
/* 1053 */       adjustContrast(imp, ip, cvalue);
/*      */     case 4:
/* 1055 */     }updatePlot();
/* 1056 */     updateLabels(imp);
/* 1057 */     if (((IJ.shiftKeyDown()) || ((this.balance) && (this.channels == 7))) && (imp.isComposite()))
/* 1058 */       ((CompositeImage)imp).updateAllChannelsAndDraw();
/*      */     else
/* 1060 */       imp.updateChannelAndDraw();
/* 1061 */     if (this.RGBImage)
/* 1062 */       imp.unlock();
/*      */   }
/*      */ 
/*      */   public void close()
/*      */   {
/* 1067 */     super.close();
/* 1068 */     instance = null;
/* 1069 */     this.done = true;
/* 1070 */     Prefs.saveLocation("b&c.loc", getLocation());
/* 1071 */     synchronized (this) {
/* 1072 */       notify();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void windowActivated(WindowEvent e) {
/* 1077 */     super.windowActivated(e);
/* 1078 */     if (IJ.isMacro())
/*      */     {
/* 1080 */       ImagePlus imp2 = WindowManager.getCurrentImage();
/* 1081 */       if ((imp2 != null) && (imp2.getBitDepth() == 24)) {
/* 1082 */         return;
/*      */       }
/*      */     }
/* 1085 */     this.previousImageID = 0;
/* 1086 */     setup();
/* 1087 */     WindowManager.setWindow(this);
/*      */   }
/*      */ 
/*      */   public synchronized void itemStateChanged(ItemEvent e) {
/* 1091 */     int index = this.choice.getSelectedIndex();
/* 1092 */     this.channels = channelConstants[index];
/* 1093 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 1094 */     if ((imp != null) && (imp.isComposite())) {
/* 1095 */       if (index + 1 <= imp.getNChannels()) {
/* 1096 */         imp.setPosition(index + 1, imp.getSlice(), imp.getFrame());
/*      */       } else {
/* 1098 */         this.choice.select(channelLabels.length - 1);
/* 1099 */         this.channels = 7;
/*      */       }
/*      */     }
/* 1102 */     else this.doReset = true;
/* 1103 */     notify();
/*      */   }
/*      */ 
/*      */   public void updateAndDraw()
/*      */   {
/* 1108 */     this.previousImageID = 0;
/* 1109 */     toFront();
/*      */   }
/*      */ 
/*      */   public static void update()
/*      */   {
/* 1114 */     if (instance != null) {
/* 1115 */       ContrastAdjuster ca = (ContrastAdjuster)instance;
/* 1116 */       ca.previousImageID = 0;
/* 1117 */       ca.setup();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.ContrastAdjuster
 * JD-Core Version:    0.6.2
 */