/*     */ package ij.plugin.frame;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GUI;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.PolygonRoi;
/*     */ import ij.gui.Roi;
/*     */ import ij.plugin.PlugIn;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Point;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.AdjustmentListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.event.TextListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ 
/*     */ public class LineWidthAdjuster extends PlugInFrame
/*     */   implements PlugIn, Runnable, AdjustmentListener, TextListener, ItemListener
/*     */ {
/*     */   public static final String LOC_KEY = "line.loc";
/*  18 */   int sliderRange = 300;
/*     */   Scrollbar slider;
/*     */   int value;
/*     */   boolean setText;
/*     */   static LineWidthAdjuster instance;
/*     */   Thread thread;
/*     */   boolean done;
/*     */   TextField tf;
/*     */   Checkbox checkbox;
/*     */ 
/*     */   public LineWidthAdjuster()
/*     */   {
/*  29 */     super("Line Width");
/*  30 */     if (instance != null) {
/*  31 */       WindowManager.toFront(instance);
/*  32 */       return;
/*     */     }
/*  34 */     WindowManager.addWindow(this);
/*  35 */     instance = this;
/*  36 */     this.slider = new Scrollbar(0, Line.getWidth(), 1, 1, this.sliderRange + 1);
/*  37 */     this.slider.setFocusable(false);
/*     */ 
/*  39 */     Panel panel = new Panel();
/*  40 */     int margin = IJ.isMacOSX() ? 5 : 0;
/*  41 */     GridBagLayout grid = new GridBagLayout();
/*  42 */     GridBagConstraints c = new GridBagConstraints();
/*  43 */     panel.setLayout(grid);
/*  44 */     c.gridx = 0; c.gridy = 0;
/*  45 */     c.gridwidth = 1;
/*  46 */     c.ipadx = 100;
/*  47 */     c.insets = new Insets(margin, 15, margin, 5);
/*  48 */     c.anchor = 10;
/*  49 */     grid.setConstraints(this.slider, c);
/*  50 */     panel.add(this.slider);
/*  51 */     c.ipadx = 0;
/*  52 */     c.gridx = 1;
/*  53 */     c.insets = new Insets(margin, 5, margin, 15);
/*  54 */     this.tf = new TextField("" + Line.getWidth(), 4);
/*  55 */     this.tf.addTextListener(this);
/*  56 */     grid.setConstraints(this.tf, c);
/*  57 */     panel.add(this.tf);
/*     */ 
/*  59 */     c.gridx = 2;
/*  60 */     c.insets = new Insets(margin, 25, margin, 5);
/*  61 */     this.checkbox = new Checkbox("Spline Fit", isSplineFit());
/*  62 */     this.checkbox.addItemListener(this);
/*  63 */     panel.add(this.checkbox);
/*     */ 
/*  65 */     add(panel, "Center");
/*  66 */     this.slider.addAdjustmentListener(this);
/*  67 */     this.slider.setUnitIncrement(1);
/*     */ 
/*  69 */     pack();
/*  70 */     Point loc = Prefs.getLocation("line.loc");
/*  71 */     if (loc != null)
/*  72 */       setLocation(loc);
/*     */     else
/*  74 */       GUI.center(this);
/*  75 */     setResizable(false);
/*  76 */     show();
/*  77 */     this.thread = new Thread(this, "LineWidthAdjuster");
/*  78 */     this.thread.start();
/*  79 */     setup();
/*  80 */     addKeyListener(IJ.getInstance());
/*     */   }
/*     */ 
/*     */   public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
/*  84 */     this.value = this.slider.getValue();
/*  85 */     this.setText = true;
/*  86 */     notify();
/*     */   }
/*     */ 
/*     */   public synchronized void textValueChanged(TextEvent e) {
/*  90 */     int width = (int)Tools.parseDouble(this.tf.getText(), -1.0D);
/*     */ 
/*  92 */     if (width == -1) return;
/*  93 */     if (width < 0) width = 1;
/*  94 */     if (width != Line.getWidth()) {
/*  95 */       this.slider.setValue(width);
/*  96 */       this.value = width;
/*  97 */       notify();
/*     */     }
/*     */   }
/*     */ 
/*     */   void setup() {
/*     */   }
/*     */ 
/*     */   public void run() {
/* 105 */     while (!this.done)
/* 106 */       synchronized (this) {
/*     */         try { wait(); } catch (InterruptedException e) {
/*     */         }
/* 109 */         if (this.done) return;
/* 110 */         Line.setWidth(this.value);
/* 111 */         if (this.setText) this.tf.setText("" + this.value);
/* 112 */         this.setText = false;
/* 113 */         updateRoi();
/*     */       }
/*     */   }
/*     */ 
/*     */   private static void updateRoi()
/*     */   {
/* 119 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 120 */     if (imp != null) {
/* 121 */       Roi roi = imp.getRoi();
/* 122 */       if ((roi != null) && (roi.isLine())) {
/* 123 */         roi.updateWideLine(Line.getWidth()); imp.draw(); return;
/*     */       }
/*     */     }
/* 125 */     if (Roi.previousRoi == null) return;
/* 126 */     int id = Roi.previousRoi.getImageID();
/* 127 */     if (id >= 0) return;
/* 128 */     imp = WindowManager.getImage(id);
/* 129 */     if (imp == null) return;
/* 130 */     Roi roi = imp.getRoi();
/* 131 */     if ((roi != null) && (roi.isLine())) {
/* 132 */       roi.updateWideLine(Line.getWidth());
/* 133 */       imp.draw();
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean isSplineFit() {
/* 138 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 139 */     if (imp == null) return false;
/* 140 */     Roi roi = imp.getRoi();
/* 141 */     if (roi == null) return false;
/* 142 */     if (!(roi instanceof PolygonRoi)) return false;
/* 143 */     return ((PolygonRoi)roi).isSplineFit();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 148 */     super.close();
/* 149 */     instance = null;
/* 150 */     this.done = true;
/* 151 */     Prefs.saveLocation("line.loc", getLocation());
/* 152 */     synchronized (this) { notify(); }
/*     */   }
/*     */ 
/*     */   public void windowActivated(WindowEvent e) {
/* 156 */     super.windowActivated(e);
/* 157 */     this.checkbox.setState(isSplineFit());
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/* 161 */     boolean selected = e.getStateChange() == 1;
/* 162 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 163 */     if (imp == null) {
/* 164 */       this.checkbox.setState(false); return;
/* 165 */     }Roi roi = imp.getRoi();
/* 166 */     if ((roi == null) || (!(roi instanceof PolygonRoi))) {
/* 167 */       this.checkbox.setState(false); return;
/* 168 */     }int type = roi.getType();
/* 169 */     if ((type == 3) || (type == 7)) {
/* 170 */       this.checkbox.setState(false); return;
/* 171 */     }PolygonRoi poly = (PolygonRoi)roi;
/* 172 */     boolean splineFit = poly.isSplineFit();
/* 173 */     if ((selected) && (!splineFit)) {
/* 174 */       poly.fitSpline(); imp.draw();
/* 175 */     } else if ((!selected) && (splineFit)) {
/* 176 */       poly.removeSplineFit(); imp.draw();
/*     */     }
/*     */   }
/*     */ 
/* 180 */   public static void update() { if (instance == null) return;
/* 181 */     instance.checkbox.setState(instance.isSplineFit());
/* 182 */     int sliderWidth = instance.slider.getValue();
/* 183 */     int lineWidth = Line.getWidth();
/* 184 */     if ((lineWidth != sliderWidth) && (lineWidth <= 200)) {
/* 185 */       instance.slider.setValue(lineWidth);
/* 186 */       instance.tf.setText("" + lineWidth);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.LineWidthAdjuster
 * JD-Core Version:    0.6.2
 */