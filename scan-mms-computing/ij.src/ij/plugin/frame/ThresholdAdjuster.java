/*     */ package ij.plugin.frame;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GUI;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.TrimmedButton;
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.Measurements;
/*     */ import ij.plugin.PlugIn;
/*     */ import ij.process.AutoThresholder;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.process.ShortProcessor;
/*     */ import java.awt.Button;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Label;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Point;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.AdjustmentListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ 
/*     */ public class ThresholdAdjuster extends PlugInFrame
/*     */   implements PlugIn, Measurements, Runnable, ActionListener, AdjustmentListener, ItemListener
/*     */ {
/*     */   public static final String LOC_KEY = "threshold.loc";
/*     */   public static final String MODE_KEY = "threshold.mode";
/*     */   public static final String DARK_BACKGROUND = "threshold.dark";
/*     */   static final int RED = 0;
/*     */   static final int BLACK_AND_WHITE = 1;
/*     */   static final int OVER_UNDER = 2;
/*  22 */   static final String[] modes = { "Red", "B&W", "Over/Under" };
/*     */   static final double defaultMinThreshold = 85.0D;
/*     */   static final double defaultMaxThreshold = 170.0D;
/*     */   static final int DEFAULT = 0;
/*  26 */   static boolean fill1 = true;
/*  27 */   static boolean fill2 = true;
/*  28 */   static boolean useBW = true;
/*  29 */   static boolean backgroundToNaN = true;
/*     */   static ThresholdAdjuster instance;
/*  31 */   static int mode = 0;
/*  32 */   static String[] methodNames = AutoThresholder.getMethods();
/*  33 */   static String method = methodNames[0];
/*  34 */   static AutoThresholder thresholder = new AutoThresholder();
/*  35 */   ThresholdPlot plot = new ThresholdPlot();
/*     */   Thread thread;
/*  38 */   int minValue = -1;
/*  39 */   int maxValue = -1;
/*  40 */   int sliderRange = 256;
/*     */   boolean doAutoAdjust;
/*     */   boolean doReset;
/*     */   boolean doApplyLut;
/*     */   boolean doStateChange;
/*     */   boolean doSet;
/*     */   Panel panel;
/*     */   Button autoB;
/*     */   Button resetB;
/*     */   Button applyB;
/*     */   Button setB;
/*     */   int previousImageID;
/*     */   int previousImageType;
/*     */   double previousMin;
/*     */   double previousMax;
/*     */   int previousSlice;
/*     */   ImageJ ij;
/*     */   double minThreshold;
/*     */   double maxThreshold;
/*     */   Scrollbar minSlider;
/*     */   Scrollbar maxSlider;
/*     */   Label label1;
/*     */   Label label2;
/*     */   boolean done;
/*     */   boolean invertedLut;
/*     */   int lutColor;
/*     */   Choice methodChoice;
/*     */   Choice modeChoice;
/*     */   Checkbox darkBackground;
/*     */   Checkbox stackHistogram;
/*     */   boolean firstActivation;
/*     */   boolean useExistingTheshold;
/*     */   static final int RESET = 0;
/*     */   static final int AUTO = 1;
/*     */   static final int HIST = 2;
/*     */   static final int APPLY = 3;
/*     */   static final int STATE_CHANGE = 4;
/*     */   static final int MIN_THRESHOLD = 5;
/*     */   static final int MAX_THRESHOLD = 6;
/*     */   static final int SET = 7;
/*     */ 
/*     */   public ThresholdAdjuster()
/*     */   {
/*  63 */     super("Threshold");
/*  64 */     ImagePlus cimp = WindowManager.getCurrentImage();
/*  65 */     if ((cimp != null) && (cimp.getBitDepth() == 24)) {
/*  66 */       IJ.run(cimp, "Color Threshold...", "");
/*  67 */       return;
/*     */     }
/*  69 */     if (instance != null) {
/*  70 */       instance.firstActivation = true;
/*  71 */       WindowManager.toFront(instance);
/*  72 */       return;
/*     */     }
/*     */ 
/*  75 */     WindowManager.addWindow(this);
/*  76 */     instance = this;
/*  77 */     mode = (int)Prefs.get("threshold.mode", 0.0D);
/*  78 */     if ((mode < 0) || (mode > 2)) mode = 0;
/*  79 */     setLutColor(mode);
/*  80 */     IJ.register(PasteController.class);
/*     */ 
/*  82 */     this.ij = IJ.getInstance();
/*  83 */     Font font = new Font("SansSerif", 0, 10);
/*  84 */     GridBagLayout gridbag = new GridBagLayout();
/*  85 */     GridBagConstraints c = new GridBagConstraints();
/*  86 */     setLayout(gridbag);
/*     */ 
/*  89 */     int y = 0;
/*  90 */     c.gridx = 0;
/*  91 */     c.gridy = (y++);
/*  92 */     c.gridwidth = 2;
/*  93 */     c.fill = 1;
/*  94 */     c.anchor = 10;
/*  95 */     c.insets = new Insets(10, 10, 0, 10);
/*  96 */     add(this.plot, c);
/*  97 */     this.plot.addKeyListener(this.ij);
/*     */ 
/* 100 */     this.minSlider = new Scrollbar(0, this.sliderRange / 3, 1, 0, this.sliderRange);
/* 101 */     c.gridx = 0;
/* 102 */     c.gridy = (y++);
/* 103 */     c.gridwidth = 1;
/* 104 */     c.weightx = (IJ.isMacintosh() ? 90.0D : 100.0D);
/* 105 */     c.fill = 2;
/* 106 */     c.insets = new Insets(5, 10, 0, 0);
/* 107 */     add(this.minSlider, c);
/* 108 */     this.minSlider.addAdjustmentListener(this);
/* 109 */     this.minSlider.addKeyListener(this.ij);
/* 110 */     this.minSlider.setUnitIncrement(1);
/* 111 */     this.minSlider.setFocusable(false);
/*     */ 
/* 114 */     c.gridx = 1;
/* 115 */     c.gridwidth = 1;
/* 116 */     c.weightx = (IJ.isMacintosh() ? 10.0D : 0.0D);
/* 117 */     c.insets = new Insets(5, 0, 0, 10);
/* 118 */     this.label1 = new Label("       ", 2);
/* 119 */     this.label1.setFont(font);
/* 120 */     add(this.label1, c);
/*     */ 
/* 123 */     this.maxSlider = new Scrollbar(0, this.sliderRange * 2 / 3, 1, 0, this.sliderRange);
/* 124 */     c.gridx = 0;
/* 125 */     c.gridy = (y++);
/* 126 */     c.gridwidth = 1;
/* 127 */     c.weightx = 100.0D;
/* 128 */     c.insets = new Insets(2, 10, 0, 0);
/* 129 */     add(this.maxSlider, c);
/* 130 */     this.maxSlider.addAdjustmentListener(this);
/* 131 */     this.maxSlider.addKeyListener(this.ij);
/* 132 */     this.maxSlider.setUnitIncrement(1);
/* 133 */     this.maxSlider.setFocusable(false);
/*     */ 
/* 136 */     c.gridx = 1;
/* 137 */     c.gridwidth = 1;
/* 138 */     c.weightx = 0.0D;
/* 139 */     c.insets = new Insets(2, 0, 0, 10);
/* 140 */     this.label2 = new Label("       ", 2);
/* 141 */     this.label2.setFont(font);
/* 142 */     add(this.label2, c);
/*     */ 
/* 145 */     this.panel = new Panel();
/* 146 */     this.methodChoice = new Choice();
/* 147 */     for (int i = 0; i < methodNames.length; i++)
/* 148 */       this.methodChoice.addItem(methodNames[i]);
/* 149 */     this.methodChoice.select(method);
/* 150 */     this.methodChoice.addItemListener(this);
/*     */ 
/* 152 */     this.panel.add(this.methodChoice);
/* 153 */     this.modeChoice = new Choice();
/* 154 */     for (int i = 0; i < modes.length; i++)
/* 155 */       this.modeChoice.addItem(modes[i]);
/* 156 */     this.modeChoice.select(mode);
/* 157 */     this.modeChoice.addItemListener(this);
/*     */ 
/* 159 */     this.panel.add(this.modeChoice);
/* 160 */     c.gridx = 0;
/* 161 */     c.gridy = (y++);
/* 162 */     c.gridwidth = 2;
/* 163 */     c.insets = new Insets(8, 5, 0, 5);
/* 164 */     c.anchor = 10;
/* 165 */     c.fill = 0;
/* 166 */     add(this.panel, c);
/*     */ 
/* 169 */     this.panel = new Panel();
/* 170 */     boolean db = Prefs.get("threshold.dark", Prefs.blackBackground);
/* 171 */     this.darkBackground = new Checkbox("Dark background");
/* 172 */     this.darkBackground.setState(db);
/* 173 */     this.darkBackground.addItemListener(this);
/* 174 */     this.panel.add(this.darkBackground);
/* 175 */     this.stackHistogram = new Checkbox("Stack histogram");
/* 176 */     this.stackHistogram.setState(false);
/* 177 */     this.stackHistogram.addItemListener(this);
/* 178 */     this.panel.add(this.stackHistogram);
/* 179 */     c.gridx = 0;
/* 180 */     c.gridy = (y++);
/* 181 */     c.gridwidth = 2;
/* 182 */     c.insets = new Insets(5, 5, 0, 5);
/* 183 */     add(this.panel, c);
/*     */ 
/* 186 */     int trim = IJ.isMacOSX() ? 11 : 0;
/* 187 */     this.panel = new Panel();
/* 188 */     this.autoB = new TrimmedButton("Auto", trim);
/* 189 */     this.autoB.addActionListener(this);
/* 190 */     this.autoB.addKeyListener(this.ij);
/* 191 */     this.panel.add(this.autoB);
/* 192 */     this.applyB = new TrimmedButton("Apply", trim);
/* 193 */     this.applyB.addActionListener(this);
/* 194 */     this.applyB.addKeyListener(this.ij);
/* 195 */     this.panel.add(this.applyB);
/* 196 */     this.resetB = new TrimmedButton("Reset", trim);
/* 197 */     this.resetB.addActionListener(this);
/* 198 */     this.resetB.addKeyListener(this.ij);
/* 199 */     this.panel.add(this.resetB);
/* 200 */     this.setB = new TrimmedButton("Set", trim);
/* 201 */     this.setB.addActionListener(this);
/* 202 */     this.setB.addKeyListener(this.ij);
/* 203 */     this.panel.add(this.setB);
/* 204 */     c.gridx = 0;
/* 205 */     c.gridy = (y++);
/* 206 */     c.gridwidth = 2;
/* 207 */     c.insets = new Insets(0, 5, 10, 5);
/* 208 */     add(this.panel, c);
/*     */ 
/* 210 */     addKeyListener(this.ij);
/* 211 */     pack();
/* 212 */     Point loc = Prefs.getLocation("threshold.loc");
/* 213 */     if (loc != null)
/* 214 */       setLocation(loc);
/*     */     else
/* 216 */       GUI.center(this);
/* 217 */     if (IJ.isMacOSX()) setResizable(false);
/* 218 */     show();
/*     */ 
/* 220 */     this.thread = new Thread(this, "ThresholdAdjuster");
/*     */ 
/* 222 */     this.thread.start();
/* 223 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 224 */     if (imp != null) {
/* 225 */       this.useExistingTheshold = isThresholded(imp);
/* 226 */       setup(imp);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
/* 231 */     if (e.getSource() == this.minSlider)
/* 232 */       this.minValue = this.minSlider.getValue();
/*     */     else
/* 234 */       this.maxValue = this.maxSlider.getValue();
/* 235 */     notify();
/*     */   }
/*     */ 
/*     */   public synchronized void actionPerformed(ActionEvent e) {
/* 239 */     Button b = (Button)e.getSource();
/* 240 */     if (b == null) return;
/* 241 */     if (b == this.resetB)
/* 242 */       this.doReset = true;
/* 243 */     else if (b == this.autoB)
/* 244 */       this.doAutoAdjust = true;
/* 245 */     else if (b == this.applyB)
/* 246 */       this.doApplyLut = true;
/* 247 */     else if (b == this.setB)
/* 248 */       this.doSet = true;
/* 249 */     notify();
/*     */   }
/*     */ 
/*     */   void setLutColor(int mode) {
/* 253 */     switch (mode) {
/*     */     case 0:
/* 255 */       this.lutColor = 0;
/* 256 */       break;
/*     */     case 1:
/* 258 */       this.lutColor = 1;
/* 259 */       break;
/*     */     case 2:
/* 261 */       this.lutColor = 3;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void itemStateChanged(ItemEvent e)
/*     */   {
/* 267 */     Object source = e.getSource();
/* 268 */     if (source == this.methodChoice) {
/* 269 */       method = this.methodChoice.getSelectedItem();
/* 270 */       this.doAutoAdjust = true;
/* 271 */     } else if (source == this.modeChoice) {
/* 272 */       mode = this.modeChoice.getSelectedIndex();
/* 273 */       setLutColor(mode);
/* 274 */       this.doStateChange = true;
/*     */     } else {
/* 276 */       this.doAutoAdjust = true;
/* 277 */     }notify();
/*     */   }
/*     */ 
/*     */   ImageProcessor setup(ImagePlus imp)
/*     */   {
/* 282 */     int type = imp.getType();
/* 283 */     if ((type == 4) || ((imp.isComposite()) && (((CompositeImage)imp).getMode() == 1)))
/* 284 */       return null;
/* 285 */     ImageProcessor ip = imp.getProcessor();
/* 286 */     boolean minMaxChange = false;
/* 287 */     boolean not8Bits = (type == 1) || (type == 2);
/* 288 */     int slice = imp.getCurrentSlice();
/* 289 */     if (not8Bits)
/* 290 */       if ((ip.getMin() == this.plot.stackMin) && (ip.getMax() == this.plot.stackMax)) {
/* 291 */         minMaxChange = false;
/* 292 */       } else if ((ip.getMin() != this.previousMin) || (ip.getMax() != this.previousMax)) {
/* 293 */         minMaxChange = true;
/* 294 */         this.previousMin = ip.getMin();
/* 295 */         this.previousMax = ip.getMax();
/* 296 */       } else if (slice != this.previousSlice) {
/* 297 */         minMaxChange = true;
/*     */       }
/* 299 */     int id = imp.getID();
/* 300 */     if ((minMaxChange) || (id != this.previousImageID) || (type != this.previousImageType))
/*     */     {
/* 302 */       Undo.reset();
/* 303 */       if ((not8Bits) && (minMaxChange) && (!this.useExistingTheshold)) {
/* 304 */         ip.resetMinAndMax();
/* 305 */         imp.updateAndDraw();
/*     */       }
/* 307 */       this.useExistingTheshold = false;
/* 308 */       this.invertedLut = imp.isInvertedLut();
/* 309 */       this.minThreshold = ip.getMinThreshold();
/* 310 */       this.maxThreshold = ip.getMaxThreshold();
/* 311 */       ImageStatistics stats = this.plot.setHistogram(imp, entireStack(imp));
/* 312 */       if (this.minThreshold == -808080.0D) {
/* 313 */         autoSetLevels(ip, stats);
/*     */       } else {
/* 315 */         this.minThreshold = scaleDown(ip, this.minThreshold);
/* 316 */         this.maxThreshold = scaleDown(ip, this.maxThreshold);
/*     */       }
/* 318 */       scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/* 319 */       updateLabels(imp, ip);
/* 320 */       updatePlot();
/* 321 */       updateScrollBars();
/* 322 */       imp.updateAndDraw();
/*     */     }
/* 324 */     this.previousImageID = id;
/* 325 */     this.previousImageType = type;
/* 326 */     this.previousSlice = slice;
/* 327 */     return ip;
/*     */   }
/*     */ 
/*     */   boolean entireStack(ImagePlus imp) {
/* 331 */     return (this.stackHistogram != null) && (this.stackHistogram.getState()) && (imp.getStackSize() > 1);
/*     */   }
/*     */ 
/*     */   void autoSetLevels(ImageProcessor ip, ImageStatistics stats) {
/* 335 */     if ((stats == null) || (stats.histogram == null)) {
/* 336 */       this.minThreshold = 85.0D;
/* 337 */       this.maxThreshold = 170.0D;
/* 338 */       return;
/*     */     }
/*     */ 
/* 341 */     boolean darkb = (this.darkBackground != null) && (this.darkBackground.getState());
/* 342 */     int modifiedModeCount = stats.histogram[stats.mode];
/* 343 */     if (!method.equals(methodNames[0]))
/* 344 */       stats.histogram[stats.mode] = this.plot.originalModeCount;
/* 345 */     int threshold = thresholder.getThreshold(method, stats.histogram);
/* 346 */     stats.histogram[stats.mode] = modifiedModeCount;
/* 347 */     if (darkb) {
/* 348 */       if (this.invertedLut) {
/* 349 */         this.minThreshold = 0.0D; this.maxThreshold = threshold;
/*     */       } else {
/* 351 */         this.minThreshold = (threshold + 1); this.maxThreshold = 255.0D;
/*     */       }
/* 353 */     } else if (this.invertedLut) {
/* 354 */       this.minThreshold = (threshold + 1); this.maxThreshold = 255.0D;
/*     */     } else {
/* 356 */       this.minThreshold = 0.0D; this.maxThreshold = threshold;
/*     */     }
/* 358 */     if (this.minThreshold > 255.0D) this.minThreshold = 255.0D;
/* 359 */     if (Recorder.record) {
/* 360 */       boolean stack = (this.stackHistogram != null) && (this.stackHistogram.getState());
/* 361 */       String options = method + (darkb ? " dark" : "") + (stack ? " stack" : "");
/* 362 */       if (Recorder.scriptMode())
/* 363 */         Recorder.recordCall("IJ.setAutoThreshold(imp, \"" + options + "\");");
/*     */       else
/* 365 */         Recorder.record("setAutoThreshold", options);
/*     */     }
/*     */   }
/*     */ 
/*     */   void scaleUpAndSet(ImageProcessor ip, double minThreshold, double maxThreshold)
/*     */   {
/* 371 */     if ((!(ip instanceof ByteProcessor)) && (minThreshold != -808080.0D)) {
/* 372 */       double min = ip.getMin();
/* 373 */       double max = ip.getMax();
/* 374 */       if (max > min) {
/* 375 */         minThreshold = min + minThreshold / 255.0D * (max - min);
/* 376 */         maxThreshold = min + maxThreshold / 255.0D * (max - min);
/*     */       } else {
/* 378 */         minThreshold = maxThreshold = min;
/*     */       }
/*     */     }
/* 380 */     ip.setThreshold(minThreshold, maxThreshold, this.lutColor);
/* 381 */     ip.setSnapshotPixels(null);
/*     */   }
/*     */ 
/*     */   double scaleDown(ImageProcessor ip, double threshold)
/*     */   {
/* 386 */     if ((ip instanceof ByteProcessor))
/* 387 */       return threshold;
/* 388 */     double min = ip.getMin();
/* 389 */     double max = ip.getMax();
/* 390 */     if (max > min) {
/* 391 */       return (threshold - min) / (max - min) * 255.0D;
/*     */     }
/* 393 */     return -808080.0D;
/*     */   }
/*     */ 
/*     */   double scaleUp(ImageProcessor ip, double threshold)
/*     */   {
/* 398 */     double min = ip.getMin();
/* 399 */     double max = ip.getMax();
/* 400 */     if (max > min) {
/* 401 */       return min + threshold / 255.0D * (max - min);
/*     */     }
/* 403 */     return -808080.0D;
/*     */   }
/*     */ 
/*     */   void updatePlot() {
/* 407 */     this.plot.minThreshold = this.minThreshold;
/* 408 */     this.plot.maxThreshold = this.maxThreshold;
/* 409 */     this.plot.mode = mode;
/* 410 */     this.plot.repaint();
/*     */   }
/*     */ 
/*     */   void updateLabels(ImagePlus imp, ImageProcessor ip) {
/* 414 */     double min = ip.getMinThreshold();
/* 415 */     double max = ip.getMaxThreshold();
/* 416 */     if (min == -808080.0D) {
/* 417 */       this.label1.setText("");
/* 418 */       this.label2.setText("");
/*     */     } else {
/* 420 */       Calibration cal = imp.getCalibration();
/* 421 */       if (cal.calibrated()) {
/* 422 */         min = cal.getCValue((int)min);
/* 423 */         max = cal.getCValue((int)max);
/*     */       }
/* 425 */       if ((((int)min == min) && ((int)max == max)) || ((ip instanceof ShortProcessor))) {
/* 426 */         this.label1.setText("" + (int)min);
/* 427 */         this.label2.setText("" + (int)max);
/*     */       } else {
/* 429 */         this.label1.setText("" + IJ.d2s(min, 2));
/* 430 */         this.label2.setText("" + IJ.d2s(max, 2));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void updateScrollBars() {
/* 436 */     this.minSlider.setValue((int)this.minThreshold);
/* 437 */     this.maxSlider.setValue((int)this.maxThreshold);
/*     */   }
/*     */ 
/*     */   void doMasking(ImagePlus imp, ImageProcessor ip)
/*     */   {
/* 442 */     ImageProcessor mask = imp.getMask();
/* 443 */     if (mask != null)
/* 444 */       ip.reset(mask);
/*     */   }
/*     */ 
/*     */   void adjustMinThreshold(ImagePlus imp, ImageProcessor ip, double value) {
/* 448 */     if ((IJ.altKeyDown()) || (IJ.shiftKeyDown())) {
/* 449 */       double width = this.maxThreshold - this.minThreshold;
/* 450 */       if (width < 1.0D) width = 1.0D;
/* 451 */       this.minThreshold = value;
/* 452 */       this.maxThreshold = (this.minThreshold + width);
/* 453 */       if (this.minThreshold + width > 255.0D) {
/* 454 */         this.minThreshold = (255.0D - width);
/* 455 */         this.maxThreshold = (this.minThreshold + width);
/* 456 */         this.minSlider.setValue((int)this.minThreshold);
/*     */       }
/* 458 */       this.maxSlider.setValue((int)this.maxThreshold);
/* 459 */       scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/* 460 */       return;
/*     */     }
/* 462 */     this.minThreshold = value;
/* 463 */     if (this.maxThreshold < this.minThreshold) {
/* 464 */       this.maxThreshold = this.minThreshold;
/* 465 */       this.maxSlider.setValue((int)this.maxThreshold);
/*     */     }
/* 467 */     scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/*     */   }
/*     */ 
/*     */   void adjustMaxThreshold(ImagePlus imp, ImageProcessor ip, int cvalue) {
/* 471 */     this.maxThreshold = cvalue;
/* 472 */     if (this.minThreshold > this.maxThreshold) {
/* 473 */       this.minThreshold = this.maxThreshold;
/* 474 */       this.minSlider.setValue((int)this.minThreshold);
/*     */     }
/* 476 */     scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/* 477 */     IJ.setKeyUp(18);
/* 478 */     IJ.setKeyUp(16);
/*     */   }
/*     */ 
/*     */   void reset(ImagePlus imp, ImageProcessor ip) {
/* 482 */     ip.resetThreshold();
/* 483 */     ImageStatistics stats = this.plot.setHistogram(imp, entireStack(imp));
/* 484 */     if (!(ip instanceof ByteProcessor)) {
/* 485 */       if (entireStack(imp))
/* 486 */         ip.setMinAndMax(stats.min, stats.max);
/*     */       else
/* 488 */         ip.resetMinAndMax();
/*     */     }
/* 490 */     updateScrollBars();
/* 491 */     if (Recorder.record)
/* 492 */       if (Recorder.scriptMode())
/* 493 */         Recorder.recordCall("IJ.resetThreshold(imp);");
/*     */       else
/* 495 */         Recorder.record("resetThreshold");
/*     */   }
/*     */ 
/*     */   void doSet(ImagePlus imp, ImageProcessor ip)
/*     */   {
/* 500 */     double level1 = ip.getMinThreshold();
/* 501 */     double level2 = ip.getMaxThreshold();
/* 502 */     if (level1 == -808080.0D) {
/* 503 */       level1 = scaleUp(ip, 85.0D);
/* 504 */       level2 = scaleUp(ip, 170.0D);
/*     */     }
/* 506 */     Calibration cal = imp.getCalibration();
/* 507 */     int digits = ((ip instanceof FloatProcessor)) || (cal.calibrated()) ? 2 : 0;
/* 508 */     level1 = cal.getCValue(level1);
/* 509 */     level2 = cal.getCValue(level2);
/* 510 */     GenericDialog gd = new GenericDialog("Set Threshold Levels");
/* 511 */     gd.addNumericField("Lower Threshold Level: ", level1, digits);
/* 512 */     gd.addNumericField("Upper Threshold Level: ", level2, digits);
/* 513 */     gd.showDialog();
/* 514 */     if (gd.wasCanceled())
/* 515 */       return;
/* 516 */     level1 = gd.getNextNumber();
/* 517 */     level2 = gd.getNextNumber();
/* 518 */     level1 = cal.getRawValue(level1);
/* 519 */     level2 = cal.getRawValue(level2);
/* 520 */     if (level2 < level1)
/* 521 */       level2 = level1;
/* 522 */     double minDisplay = ip.getMin();
/* 523 */     double maxDisplay = ip.getMax();
/* 524 */     ip.resetMinAndMax();
/* 525 */     double minValue = ip.getMin();
/* 526 */     double maxValue = ip.getMax();
/* 527 */     if (level1 < minValue) level1 = minValue;
/* 528 */     if (level2 > maxValue) level2 = maxValue;
/* 529 */     IJ.wait(500);
/* 530 */     ip.setThreshold(level1, level2, this.lutColor);
/* 531 */     ip.setSnapshotPixels(null);
/* 532 */     setup(imp);
/* 533 */     if (Recorder.record)
/* 534 */       if (imp.getBitDepth() == 32) {
/* 535 */         if (Recorder.scriptMode())
/* 536 */           Recorder.recordCall("IJ.setThreshold(" + ip.getMinThreshold() + ", " + ip.getMaxThreshold() + ");");
/*     */         else
/* 538 */           Recorder.record("setThreshold", ip.getMinThreshold(), ip.getMaxThreshold());
/*     */       } else {
/* 540 */         int min = (int)ip.getMinThreshold();
/* 541 */         int max = (int)ip.getMaxThreshold();
/* 542 */         if (cal.isSigned16Bit()) {
/* 543 */           min = (int)cal.getCValue(level1);
/* 544 */           max = (int)cal.getCValue(level2);
/*     */         }
/* 546 */         if (Recorder.scriptMode())
/* 547 */           Recorder.recordCall("IJ.setThreshold(imp, " + min + ", " + max + ");");
/*     */         else
/* 549 */           Recorder.record("setThreshold", min, max);
/*     */       }
/*     */   }
/*     */ 
/*     */   void changeState(ImagePlus imp, ImageProcessor ip)
/*     */   {
/* 555 */     scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/* 556 */     updateScrollBars();
/*     */   }
/*     */ 
/*     */   void autoThreshold(ImagePlus imp, ImageProcessor ip) {
/* 560 */     ip.resetThreshold();
/* 561 */     this.previousImageID = 0;
/* 562 */     setup(imp);
/*     */   }
/*     */ 
/*     */   void apply(ImagePlus imp) {
/*     */     try {
/* 567 */       if (imp.getBitDepth() == 32) {
/* 568 */         GenericDialog gd = new GenericDialog("NaN Backround");
/* 569 */         gd.addCheckbox("Set Background Pixels to NaN", backgroundToNaN);
/* 570 */         gd.showDialog();
/* 571 */         if (gd.wasCanceled()) {
/* 572 */           runThresholdCommand();
/* 573 */           return;
/*     */         }
/* 575 */         backgroundToNaN = gd.getNextBoolean();
/* 576 */         if (backgroundToNaN) {
/* 577 */           Recorder.recordInMacros = true;
/* 578 */           IJ.run("NaN Background");
/* 579 */           Recorder.recordInMacros = false;
/*     */         } else {
/* 581 */           runThresholdCommand();
/*     */         }
/*     */       } else { runThresholdCommand(); }
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   void runThresholdCommand() {
/* 590 */     Recorder.recordInMacros = true;
/* 591 */     IJ.run("Convert to Mask");
/* 592 */     Recorder.recordInMacros = false;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 599 */     while (!this.done) {
/* 600 */       synchronized (this) {
/*     */         try { wait(); } catch (InterruptedException e) {
/*     */         }
/*     */       }
/* 604 */       doUpdate();
/*     */     }
/*     */   }
/*     */ 
/*     */   void doUpdate()
/*     */   {
/* 612 */     int min = this.minValue;
/* 613 */     int max = this.maxValue;
/*     */     int action;
/* 614 */     if (this.doReset) { action = 0; }
/*     */     else
/*     */     {
/* 615 */       int action;
/* 615 */       if (this.doAutoAdjust) { action = 1; }
/*     */       else
/*     */       {
/* 616 */         int action;
/* 616 */         if (this.doApplyLut) { action = 3; }
/*     */         else
/*     */         {
/* 617 */           int action;
/* 617 */           if (this.doStateChange) { action = 4; }
/*     */           else
/*     */           {
/* 618 */             int action;
/* 618 */             if (this.doSet) { action = 7; }
/*     */             else
/*     */             {
/* 619 */               int action;
/* 619 */               if (this.minValue >= 0) { action = 5; }
/*     */               else
/*     */               {
/* 620 */                 int action;
/* 620 */                 if (this.maxValue >= 0) action = 6;
/*     */                 else
/*     */                   return;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     int action;
/* 622 */     this.minValue = -1;
/* 623 */     this.maxValue = -1;
/* 624 */     this.doReset = false;
/* 625 */     this.doAutoAdjust = false;
/* 626 */     this.doApplyLut = false;
/* 627 */     this.doStateChange = false;
/* 628 */     this.doSet = false;
/* 629 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 630 */     if (imp == null) {
/* 631 */       IJ.beep();
/* 632 */       IJ.showStatus("No image");
/* 633 */       return;
/*     */     }
/* 635 */     ImageProcessor ip = setup(imp);
/* 636 */     if (ip == null) {
/* 637 */       imp.unlock();
/* 638 */       IJ.beep();
/* 639 */       if (imp.isComposite())
/* 640 */         IJ.showStatus("\"Composite\" mode images cannot be thresholded");
/*     */       else
/* 642 */         IJ.showStatus("RGB images cannot be thresholded");
/* 643 */       return;
/*     */     }
/*     */ 
/* 646 */     switch (action) { case 0:
/* 647 */       reset(imp, ip); break;
/*     */     case 1:
/* 648 */       autoThreshold(imp, ip); break;
/*     */     case 3:
/* 649 */       apply(imp); break;
/*     */     case 4:
/* 650 */       changeState(imp, ip); break;
/*     */     case 7:
/* 651 */       doSet(imp, ip); break;
/*     */     case 5:
/* 652 */       adjustMinThreshold(imp, ip, min); break;
/*     */     case 6:
/* 653 */       adjustMaxThreshold(imp, ip, max);
/*     */     case 2: }
/* 655 */     updatePlot();
/* 656 */     updateLabels(imp, ip);
/* 657 */     ip.setLutAnimation(true);
/* 658 */     imp.updateAndDraw();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 663 */     super.close();
/* 664 */     instance = null;
/* 665 */     this.done = true;
/* 666 */     Prefs.saveLocation("threshold.loc", getLocation());
/* 667 */     Prefs.set("threshold.mode", mode);
/* 668 */     Prefs.set("threshold.dark", this.darkBackground.getState());
/* 669 */     synchronized (this) {
/* 670 */       notify();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void windowActivated(WindowEvent e) {
/* 675 */     super.windowActivated(e);
/* 676 */     this.plot.requestFocus();
/* 677 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 678 */     if ((imp != null) && (this.firstActivation)) {
/* 679 */       this.previousImageID = 0;
/* 680 */       this.useExistingTheshold = isThresholded(imp);
/* 681 */       setup(imp);
/* 682 */       this.firstActivation = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean isThresholded(ImagePlus imp) {
/* 687 */     ImageProcessor ip = imp.getProcessor();
/* 688 */     return (ip.getMinThreshold() != -808080.0D) && (ip.isColorLut());
/*     */   }
/*     */ 
/*     */   public static void update()
/*     */   {
/* 693 */     if (instance != null) {
/* 694 */       ThresholdAdjuster ta = instance;
/* 695 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 696 */       if ((imp != null) && (ta.previousImageID == imp.getID())) {
/* 697 */         ta.previousImageID = 0;
/* 698 */         ta.setup(imp);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getMethod()
/*     */   {
/* 705 */     return method;
/*     */   }
/*     */ 
/*     */   public static String getMode()
/*     */   {
/* 710 */     return modes[mode];
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.ThresholdAdjuster
 * JD-Core Version:    0.6.2
 */