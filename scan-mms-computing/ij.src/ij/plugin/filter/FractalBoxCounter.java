/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Plot;
/*     */ import ij.measure.CurveFitter;
/*     */ import ij.measure.ResultsTable;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class FractalBoxCounter
/*     */   implements PlugInFilter
/*     */ {
/*  33 */   static String sizes = "2,3,4,6,8,12,16,32,64";
/*     */   static boolean blackBackground;
/*     */   int[] boxSizes;
/*     */   float[] boxCountSums;
/*     */   int maxBoxSize;
/*     */   int[] counts;
/*     */   Rectangle roi;
/*     */   int foreground;
/*     */   ImagePlus imp;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  44 */     this.imp = imp;
/*  45 */     return 129;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip)
/*     */   {
/*  50 */     GenericDialog gd = new GenericDialog("Fractal Box Counter", IJ.getInstance());
/*  51 */     gd.addStringField("Box Sizes:", sizes, 20);
/*  52 */     gd.addCheckbox("Black Background", blackBackground);
/*     */ 
/*  54 */     gd.showDialog();
/*  55 */     if (gd.wasCanceled()) {
/*  56 */       return;
/*     */     }
/*  58 */     String s = gd.getNextString();
/*     */ 
/*  60 */     blackBackground = gd.getNextBoolean();
/*     */ 
/*  62 */     if (s.equals(""))
/*  63 */       return;
/*  64 */     this.boxSizes = s2ints(s);
/*  65 */     if ((this.boxSizes == null) || (this.boxSizes.length < 1))
/*  66 */       return;
/*  67 */     this.boxCountSums = new float[this.boxSizes.length];
/*  68 */     sizes = s;
/*  69 */     for (int i = 0; i < this.boxSizes.length; i++)
/*  70 */       this.maxBoxSize = Math.max(this.maxBoxSize, this.boxSizes[i]);
/*  71 */     this.counts = new int[this.maxBoxSize * this.maxBoxSize + 1];
/*  72 */     this.imp.killRoi();
/*  73 */     if (!ip.isBinary()) {
/*  74 */       IJ.error("8-bit binary image (0 and 255) required.");
/*  75 */       return;
/*     */     }
/*  77 */     if (blackBackground)
/*  78 */       this.foreground = 255;
/*     */     else
/*  80 */       this.foreground = 0;
/*  81 */     if (ip.isInvertedLut())
/*  82 */       this.foreground = (255 - this.foreground);
/*  83 */     doBoxCounts(ip);
/*  84 */     IJ.register(FractalBoxCounter.class);
/*     */   }
/*     */ 
/*     */   public int[] s2ints(String s)
/*     */   {
/*  91 */     StringTokenizer st = new StringTokenizer(s, ", \t");
/*  92 */     int nInts = st.countTokens();
/*  93 */     int[] ints = new int[nInts];
/*  94 */     for (int i = 0; i < nInts; i++) try {
/*  95 */         ints[i] = Integer.parseInt(st.nextToken()); } catch (NumberFormatException e) {
/*  96 */         IJ.log("" + e); return null;
/*     */       }
/*  98 */     return ints;
/*     */   }
/*     */ 
/*     */   boolean FindMargins(ImageProcessor ip) {
/* 102 */     if (IJ.debugMode) IJ.log("FindMargins");
/* 103 */     int[] histogram = new int[256];
/* 104 */     int width = this.imp.getWidth();
/* 105 */     int height = this.imp.getHeight();
/*     */ 
/* 109 */     int left = -1;
/*     */     do {
/* 111 */       left++;
/* 112 */       if (left >= width) {
/* 113 */         IJ.error("No non-backround pixels found.");
/* 114 */         return false;
/*     */       }
/* 116 */       ip.setRoi(left, 0, 1, height);
/* 117 */       histogram = ip.getHistogram();
/* 118 */     }while (histogram[this.foreground] == 0);
/*     */ 
/* 121 */     int top = -1;
/*     */     do {
/* 123 */       top++;
/* 124 */       ip.setRoi(left, top, width - left, 1);
/* 125 */       histogram = ip.getHistogram();
/* 126 */     }while (histogram[this.foreground] == 0);
/*     */ 
/* 129 */     int right = width + 1;
/*     */     do {
/* 131 */       right--;
/* 132 */       ip.setRoi(right - 1, top, 1, height - top);
/* 133 */       histogram = ip.getHistogram();
/* 134 */     }while (histogram[this.foreground] == 0);
/*     */ 
/* 137 */     int bottom = height + 1;
/*     */     do {
/* 139 */       bottom--;
/* 140 */       ip.setRoi(left, bottom - 1, right - left, 1);
/* 141 */       histogram = ip.getHistogram();
/* 142 */     }while (histogram[this.foreground] == 0);
/*     */ 
/* 144 */     this.roi = new Rectangle(left, top, right - left, bottom - top);
/* 145 */     return true;
/*     */   }
/*     */ 
/*     */   int count(int size, ImageProcessor ip) {
/* 149 */     int[] histogram = new int[256];
/*     */ 
/* 151 */     int x = this.roi.x;
/* 152 */     int y = this.roi.y;
/* 153 */     int w = size <= this.roi.width ? size : this.roi.width;
/* 154 */     int h = size <= this.roi.height ? size : this.roi.height;
/* 155 */     int right = this.roi.x + this.roi.width;
/* 156 */     int bottom = this.roi.y + this.roi.height;
/* 157 */     int maxCount = size * size;
/*     */ 
/* 159 */     for (int i = 1; i <= maxCount; i++)
/* 160 */       this.counts[i] = 0;
/* 161 */     boolean done = false;
/*     */     do {
/* 163 */       ip.setRoi(x, y, w, h);
/* 164 */       histogram = ip.getHistogram();
/* 165 */       this.counts[histogram[this.foreground]] += 1;
/* 166 */       x += size;
/* 167 */       if (x + size >= right) {
/* 168 */         w = right - x;
/* 169 */         if (x >= right) {
/* 170 */           w = size;
/* 171 */           x = this.roi.x;
/* 172 */           y += size;
/* 173 */           if (y + size >= bottom)
/* 174 */             h = bottom - y;
/* 175 */           done = y >= bottom;
/*     */         }
/*     */       }
/*     */     }
/* 178 */     while (!done);
/* 179 */     int boxSum = 0;
/*     */ 
/* 181 */     for (int i = 1; i <= maxCount; i++) {
/* 182 */       int nBoxes = this.counts[i];
/* 183 */       if (nBoxes != 0)
/* 184 */         boxSum += nBoxes;
/*     */     }
/* 186 */     return boxSum;
/*     */   }
/*     */ 
/*     */   double plot() {
/* 190 */     int n = this.boxSizes.length;
/* 191 */     float[] sizes = new float[this.boxSizes.length];
/* 192 */     for (int i = 0; i < n; i++)
/* 193 */       sizes[i] = ((float)Math.log(this.boxSizes[i]));
/* 194 */     CurveFitter cf = new CurveFitter(Tools.toDouble(sizes), Tools.toDouble(this.boxCountSums));
/* 195 */     cf.doFit(0);
/* 196 */     double[] p = cf.getParams();
/* 197 */     double D = -p[1];
/* 198 */     String label = "D=" + IJ.d2s(D, 4);
/* 199 */     float[] px = new float[100];
/* 200 */     float[] py = new float[100];
/* 201 */     double[] a = Tools.getMinMax(sizes);
/* 202 */     double xmin = a[0]; double xmax = a[1];
/* 203 */     a = Tools.getMinMax(this.boxCountSums);
/* 204 */     double ymin = a[0]; double ymax = a[1];
/* 205 */     double inc = (xmax - xmin) / 99.0D;
/* 206 */     double tmp = xmin;
/* 207 */     for (int i = 0; i < 100; i++) {
/* 208 */       px[i] = ((float)tmp);
/* 209 */       tmp += inc;
/*     */     }
/* 211 */     for (int i = 0; i < 100; i++)
/* 212 */       py[i] = ((float)cf.f(p, px[i]));
/* 213 */     a = Tools.getMinMax(py);
/* 214 */     ymin = Math.min(ymin, a[0]);
/* 215 */     ymax = Math.max(ymax, a[1]);
/* 216 */     Plot plot = new Plot("Plot", "log (box size)", "log (count)", px, py);
/* 217 */     plot.setLimits(xmin, xmax, ymin, ymax);
/* 218 */     plot.addPoints(sizes, this.boxCountSums, 0);
/* 219 */     plot.addLabel(0.8D, 0.2D, label);
/* 220 */     plot.show();
/* 221 */     return D;
/*     */   }
/*     */ 
/*     */   void doBoxCounts(ImageProcessor ip) {
/* 225 */     if (!FindMargins(ip))
/* 226 */       return;
/* 227 */     ResultsTable rt = ResultsTable.getResultsTable();
/* 228 */     rt.incrementCounter();
/* 229 */     rt.setLabel(this.imp.getShortTitle(), rt.getCounter() - 1);
/* 230 */     for (int i = 0; i < this.boxSizes.length; i++) {
/* 231 */       int boxSum = count(this.boxSizes[i], ip);
/* 232 */       rt.addValue("C" + this.boxSizes[i], boxSum);
/* 233 */       this.boxCountSums[i] = ((float)Math.log(boxSum));
/*     */     }
/* 235 */     double D = plot();
/* 236 */     rt.addValue("D", D);
/* 237 */     rt.show("Results");
/* 238 */     this.imp.killRoi();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.FractalBoxCounter
 * JD-Core Version:    0.6.2
 */