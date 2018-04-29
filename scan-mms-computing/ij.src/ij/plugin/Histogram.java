/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.HistogramWindow;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.YesNoCancelDialog;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.process.StackStatistics;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.event.TextListener;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Histogram
/*     */   implements PlugIn, TextListener
/*     */ {
/*  17 */   private static int nBins = 256;
/*  18 */   private static boolean useImageMinAndMax = true;
/*     */   private static double xMin;
/*     */   private static double xMax;
/*  20 */   private static String yMax = "Auto";
/*     */   private static boolean stackHistogram;
/*     */   private static int imageID;
/*     */   private Checkbox checkbox;
/*     */   private TextField minField;
/*     */   private TextField maxField;
/*     */   private String defaultMin;
/*     */   private String defaultMax;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  28 */     ImagePlus imp = IJ.getImage();
/*  29 */     int bitDepth = imp.getBitDepth();
/*  30 */     if ((bitDepth == 32) || (IJ.altKeyDown())) { IJ.setKeyUp(18);
/*  32 */       if (showDialog(imp));
/*     */     }
/*     */     else {
/*  35 */       int stackSize = imp.getStackSize();
/*  36 */       boolean noDialog = (stackSize == 1) || (imp.isComposite());
/*  37 */       if (stackSize == 3) {
/*  38 */         ImageStack stack = imp.getStack();
/*  39 */         String label1 = stack.getSliceLabel(1);
/*  40 */         if ("Hue".equals(label1))
/*  41 */           noDialog = true;
/*     */       }
/*  43 */       int flags = noDialog ? 0 : setupDialog(imp, 0);
/*  44 */       if (flags == 4096) return;
/*  45 */       stackHistogram = flags == 32;
/*  46 */       Calibration cal = imp.getCalibration();
/*  47 */       nBins = 256;
/*  48 */       if ((stackHistogram) && (((bitDepth == 8) && (!cal.calibrated())) || (bitDepth == 24))) {
/*  49 */         xMin = 0.0D;
/*  50 */         xMax = 256.0D;
/*  51 */         useImageMinAndMax = false;
/*     */       } else {
/*  53 */         useImageMinAndMax = true;
/*  54 */       }yMax = "Auto";
/*     */     }
/*  56 */     ImageStatistics stats = null;
/*  57 */     if (useImageMinAndMax) {
/*  58 */       xMin = 0.0D; xMax = 0.0D;
/*  59 */     }int iyMax = (int)Tools.parseDouble(yMax, 0.0D);
/*  60 */     boolean customHistogram = ((bitDepth == 8) || (bitDepth == 24)) && ((xMin != 0.0D) || (xMax != 0.0D) || (nBins != 256) || (iyMax > 0));
/*  61 */     ImageWindow.centerNextImage();
/*  62 */     if ((stackHistogram) || (customHistogram)) {
/*  63 */       ImagePlus imp2 = imp;
/*  64 */       if ((customHistogram) && (!stackHistogram) && (imp.getStackSize() > 1))
/*  65 */         imp2 = new ImagePlus("Temp", imp.getProcessor());
/*  66 */       stats = new StackStatistics(imp2, nBins, xMin, xMax);
/*  67 */       stats.histYMax = iyMax;
/*  68 */       new HistogramWindow("Histogram of " + imp.getShortTitle(), imp, stats);
/*     */     } else {
/*  70 */       new HistogramWindow("Histogram of " + imp.getShortTitle(), imp, nBins, xMin, xMax, iyMax);
/*     */     }
/*     */   }
/*     */ 
/*  74 */   boolean showDialog(ImagePlus imp) { ImageProcessor ip = imp.getProcessor();
/*  75 */     double min = ip.getMin();
/*  76 */     double max = ip.getMax();
/*  77 */     if ((imp.getID() != imageID) || ((min == xMin) && (min == xMax)))
/*  78 */       useImageMinAndMax = true;
/*  79 */     if ((imp.getID() != imageID) || (useImageMinAndMax)) {
/*  80 */       xMin = min;
/*  81 */       xMax = max;
/*  82 */       Calibration cal = imp.getCalibration();
/*  83 */       xMin = cal.getCValue(xMin);
/*  84 */       xMax = cal.getCValue(xMax);
/*     */     }
/*  86 */     this.defaultMin = IJ.d2s(xMin, 2);
/*  87 */     this.defaultMax = IJ.d2s(xMax, 2);
/*  88 */     imageID = imp.getID();
/*  89 */     int stackSize = imp.getStackSize();
/*  90 */     GenericDialog gd = new GenericDialog("Histogram");
/*  91 */     gd.addNumericField("Bins:", HistogramWindow.nBins, 0);
/*  92 */     gd.addCheckbox("Use min/max or:", useImageMinAndMax);
/*     */ 
/*  94 */     gd.addMessage("");
/*  95 */     int fwidth = 6;
/*  96 */     int nwidth = Math.max(IJ.d2s(xMin, 2).length(), IJ.d2s(xMax, 2).length());
/*  97 */     if (nwidth > fwidth) fwidth = nwidth;
/*  98 */     gd.addNumericField("X_Min:", xMin, 2, fwidth, null);
/*  99 */     gd.addNumericField("X_Max:", xMax, 2, fwidth, null);
/* 100 */     gd.addMessage(" ");
/* 101 */     gd.addStringField("Y_Max:", yMax, 6);
/* 102 */     if (stackSize > 1)
/* 103 */       gd.addCheckbox("Stack Histogram", stackHistogram);
/* 104 */     Vector numbers = gd.getNumericFields();
/* 105 */     this.minField = ((TextField)numbers.elementAt(1));
/* 106 */     this.minField.addTextListener(this);
/* 107 */     this.maxField = ((TextField)numbers.elementAt(2));
/* 108 */     this.maxField.addTextListener(this);
/* 109 */     this.checkbox = ((Checkbox)gd.getCheckboxes().elementAt(0));
/* 110 */     gd.showDialog();
/* 111 */     if (gd.wasCanceled())
/* 112 */       return false;
/* 113 */     nBins = (int)gd.getNextNumber();
/* 114 */     if ((nBins >= 2) && (nBins <= 1000))
/* 115 */       HistogramWindow.nBins = nBins;
/* 116 */     useImageMinAndMax = gd.getNextBoolean();
/* 117 */     xMin = gd.getNextNumber();
/* 118 */     xMax = gd.getNextNumber();
/* 119 */     yMax = gd.getNextString();
/* 120 */     stackHistogram = stackSize > 1 ? gd.getNextBoolean() : false;
/* 121 */     IJ.register(Histogram.class);
/* 122 */     return true; }
/*     */ 
/*     */   public void textValueChanged(TextEvent e)
/*     */   {
/* 126 */     boolean rangeChanged = (!this.defaultMin.equals(this.minField.getText())) || (!this.defaultMax.equals(this.maxField.getText()));
/*     */ 
/* 128 */     if (rangeChanged)
/* 129 */       this.checkbox.setState(false);
/*     */   }
/*     */ 
/*     */   int setupDialog(ImagePlus imp, int flags) {
/* 133 */     int stackSize = imp.getStackSize();
/* 134 */     if (stackSize > 1) {
/* 135 */       String macroOptions = Macro.getOptions();
/* 136 */       if (macroOptions != null) {
/* 137 */         if (macroOptions.indexOf("stack ") >= 0) {
/* 138 */           return flags + 32;
/*     */         }
/* 140 */         return flags;
/*     */       }
/* 142 */       YesNoCancelDialog d = new YesNoCancelDialog(IJ.getInstance(), "Histogram", "Include all " + stackSize + " images?");
/*     */ 
/* 144 */       if (d.cancelPressed())
/* 145 */         return 4096;
/* 146 */       if (d.yesPressed()) {
/* 147 */         if (Recorder.record)
/* 148 */           Recorder.recordOption("stack");
/* 149 */         return flags + 32;
/*     */       }
/* 151 */       if (Recorder.record)
/* 152 */         Recorder.recordOption("slice");
/*     */     }
/* 154 */     return flags;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Histogram
 * JD-Core Version:    0.6.2
 */