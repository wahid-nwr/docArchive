/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.HistogramWindow;
/*     */ import ij.measure.ResultsTable;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.process.StackStatistics;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.event.TextListener;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Distribution
/*     */   implements PlugIn, TextListener
/*     */ {
/*  19 */   static String parameter = "Area";
/*  20 */   static boolean autoBinning = true;
/*  21 */   static int nBins = 10;
/*  22 */   static String range = "0-0";
/*     */   Checkbox checkbox;
/*     */   TextField nBinsField;
/*     */   TextField rangeField;
/*     */   String defaultNBins;
/*     */   String defaultRange;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  28 */     ResultsTable rt = ResultsTable.getResultsTable();
/*  29 */     int count = rt.getCounter();
/*  30 */     if (count == 0) {
/*  31 */       IJ.error("Distribution", "The \"Results\" table is empty");
/*  32 */       return;
/*     */     }
/*  34 */     String head = rt.getColumnHeadings();
/*     */ 
/*  37 */     StringTokenizer t = new StringTokenizer(head, "\t");
/*  38 */     int tokens = t.countTokens() - 1;
/*  39 */     String[] strings = new String[tokens];
/*  40 */     strings[0] = t.nextToken();
/*  41 */     for (int i = 0; i < tokens; i++) {
/*  42 */       strings[i] = t.nextToken();
/*     */     }
/*  44 */     this.defaultNBins = ("" + nBins);
/*  45 */     this.defaultRange = range;
/*  46 */     GenericDialog gd = new GenericDialog("Distribution");
/*  47 */     gd.addChoice("Parameter: ", strings, strings[getIndex(strings)]);
/*  48 */     gd.addMessage("Data points: " + count);
/*  49 */     gd.addCheckbox("Automatic binning", autoBinning);
/*  50 */     gd.addNumericField("or specify bins:", nBins, 0);
/*  51 */     gd.addStringField("and range:", range);
/*     */ 
/*  53 */     Vector v = gd.getNumericFields();
/*  54 */     this.nBinsField = ((TextField)v.elementAt(0));
/*  55 */     this.nBinsField.addTextListener(this);
/*  56 */     v = gd.getStringFields();
/*  57 */     this.rangeField = ((TextField)v.elementAt(0));
/*  58 */     this.rangeField.addTextListener(this);
/*  59 */     this.checkbox = ((Checkbox)gd.getCheckboxes().elementAt(0));
/*  60 */     gd.showDialog();
/*  61 */     if (gd.wasCanceled()) {
/*  62 */       return;
/*     */     }
/*  64 */     parameter = gd.getNextChoice();
/*  65 */     autoBinning = gd.getNextBoolean();
/*  66 */     double nMin = 0.0D; double nMax = 0.0D;
/*  67 */     if (!autoBinning) {
/*  68 */       nBins = (int)gd.getNextNumber();
/*  69 */       range = gd.getNextString();
/*  70 */       String[] minAndMax = Tools.split(range, " -");
/*  71 */       nMin = Tools.parseDouble(minAndMax[0]);
/*  72 */       nMax = minAndMax.length == 2 ? Tools.parseDouble(minAndMax[1]) : (0.0D / 0.0D);
/*  73 */       if ((Double.isNaN(nMin)) || (Double.isNaN(nMax))) {
/*  74 */         nMin = 0.0D; nMax = 0.0D; range = "0-0";
/*     */       }
/*     */     }
/*  77 */     float[] data = null;
/*  78 */     int index = rt.getColumnIndex(parameter);
/*  79 */     if (index >= 0)
/*  80 */       data = rt.getColumn(index);
/*  81 */     if (data == null) {
/*  82 */       IJ.error("Distribution", "No available results: \"" + parameter + "\"");
/*  83 */       return;
/*     */     }
/*     */ 
/*  86 */     float[] pars = new float[11];
/*  87 */     stats(count, data, pars);
/*  88 */     if (autoBinning)
/*     */     {
/*  91 */       float binWidth = (float)(3.49D * pars[7] * (float)Math.pow(count, -0.3333333333333333D));
/*  92 */       nBins = (int)Math.floor((pars[4] - pars[3]) / binWidth + 0.5D);
/*  93 */       if (nBins < 2) nBins = 2;
/*     */     }
/*     */ 
/*  96 */     ImageProcessor ip = new FloatProcessor(count, 1, data, null);
/*  97 */     ImagePlus imp = new ImagePlus("", ip);
/*  98 */     ImageStatistics stats = new StackStatistics(imp, nBins, nMin, nMax);
/*  99 */     int maxCount = 0;
/* 100 */     for (int i = 0; i < stats.histogram.length; i++) {
/* 101 */       if (stats.histogram[i] > maxCount)
/* 102 */         maxCount = stats.histogram[i];
/*     */     }
/* 104 */     stats.histYMax = maxCount;
/* 105 */     new HistogramWindow(parameter + " Distribution", imp, stats);
/*     */   }
/*     */ 
/*     */   int getIndex(String[] strings) {
/* 109 */     for (int i = 0; i < strings.length; i++) {
/* 110 */       if (strings[i].equals(parameter))
/* 111 */         return i;
/*     */     }
/* 113 */     return 0;
/*     */   }
/*     */ 
/*     */   public void textValueChanged(TextEvent e) {
/* 117 */     if (!this.defaultNBins.equals(this.nBinsField.getText()))
/* 118 */       this.checkbox.setState(false);
/* 119 */     if (!this.defaultRange.equals(this.rangeField.getText()))
/* 120 */       this.checkbox.setState(false);
/*     */   }
/*     */ 
/*     */   void stats(int nc, float[] data, float[] pars)
/*     */   {
/* 126 */     float s = 0.0F; float min = 3.4028235E+38F; float max = -3.402824E+38F; float totl = 0.0F; float ave = 0.0F; float adev = 0.0F; float sdev = 0.0F; float var = 0.0F; float skew = 0.0F; float kurt = 0.0F;
/*     */ 
/* 128 */     for (int i = 0; i < nc; i++) {
/* 129 */       totl += data[i];
/*     */ 
/* 131 */       if (data[i] < min) min = data[i];
/* 132 */       if (data[i] > max) max = data[i];
/*     */     }
/*     */ 
/* 135 */     ave = totl / nc;
/*     */ 
/* 137 */     for (i = 0; i < nc; i++) {
/* 138 */       s = data[i] - ave;
/* 139 */       adev += Math.abs(s);
/* 140 */       float p = s * s;
/* 141 */       var += p;
/* 142 */       p *= s;
/* 143 */       skew += p;
/* 144 */       p *= s;
/* 145 */       kurt += p;
/*     */     }
/*     */ 
/* 148 */     adev /= nc;
/* 149 */     var /= (nc - 1);
/* 150 */     sdev = (float)Math.sqrt(var);
/*     */ 
/* 152 */     if (var > 0.0F) {
/* 153 */       skew /= nc * (float)Math.pow(sdev, 3.0D);
/* 154 */       kurt = kurt / (nc * (float)Math.pow(var, 2.0D)) - 3.0F;
/*     */     }
/* 156 */     pars[1] = nc;
/* 157 */     pars[2] = totl;
/* 158 */     pars[3] = min;
/* 159 */     pars[4] = max;
/* 160 */     pars[5] = ave;
/* 161 */     pars[6] = adev;
/* 162 */     pars[7] = sdev;
/* 163 */     pars[8] = var;
/* 164 */     pars[9] = skew;
/* 165 */     pars[10] = kurt;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Distribution
 * JD-Core Version:    0.6.2
 */