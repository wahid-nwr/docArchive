/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Macro;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Plot;
/*     */ import ij.io.FileOpener;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.io.SaveDialog;
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.CurveFitter;
/*     */ import ij.measure.Measurements;
/*     */ import ij.plugin.TextReader;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Button;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Panel;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class Calibrator
/*     */   implements PlugInFilter, Measurements, ActionListener
/*     */ {
/*     */   private static final String NONE = "None";
/*     */   private static final String INVERTER = "Pixel Inverter";
/*     */   private static final String UNCALIBRATED_OD = "Uncalibrated OD";
/*     */   private static final String CUSTOM = "Custom";
/*     */   private static boolean showSettings;
/*     */   private boolean global1;
/*     */   private boolean global2;
/*     */   private ImagePlus imp;
/*     */   private int choiceIndex;
/*     */   private String[] functions;
/*  27 */   private int nFits = CurveFitter.fitList.length;
/*  28 */   private int spacerIndex = this.nFits + 1;
/*  29 */   private int inverterIndex = this.nFits + 2;
/*  30 */   private int odIndex = this.nFits + 3;
/*  31 */   private int customIndex = this.nFits + 4;
/*  32 */   private static String xText = "";
/*  33 */   private static String yText = "";
/*     */   private static boolean importedValues;
/*     */   private String unit;
/*  36 */   private double lx = 0.02D; private double ly = 0.1D;
/*     */   private int oldFunction;
/*     */   private String sumResiduals;
/*     */   private String fitGoodness;
/*     */   private Button open;
/*     */   private Button save;
/*     */   private GenericDialog gd;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  43 */     this.imp = imp;
/*  44 */     IJ.register(Calibrator.class);
/*  45 */     return 143;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  49 */     this.global1 = (this.imp.getGlobalCalibration() != null);
/*  50 */     if (!showDialog(this.imp))
/*  51 */       return;
/*  52 */     if (this.choiceIndex == this.customIndex) {
/*  53 */       showPlot(null, null, this.imp.getCalibration(), null);
/*  54 */       return;
/*  55 */     }if (this.imp.getType() == 2) {
/*  56 */       if (this.choiceIndex == 0)
/*  57 */         this.imp.getCalibration().setValueUnit(this.unit);
/*     */       else
/*  59 */         IJ.error("Calibrate", "Function must be \"None\" for 32-bit images,\nbut you can change the Unit.");
/*     */     }
/*  61 */     else calibrate(this.imp);
/*     */   }
/*     */ 
/*     */   public boolean showDialog(ImagePlus imp)
/*     */   {
/*  66 */     Calibration cal = imp.getCalibration();
/*  67 */     this.functions = getFunctionList(cal.getFunction() == 22);
/*  68 */     int function = cal.getFunction();
/*  69 */     this.oldFunction = function;
/*  70 */     double[] p = cal.getCoefficients();
/*  71 */     this.unit = cal.getValueUnit();
/*     */     String defaultChoice;
/*     */     String defaultChoice;
/*  72 */     if (function == 20) {
/*  73 */       defaultChoice = "None";
/*     */     }
/*     */     else
/*     */     {
/*     */       String defaultChoice;
/*  74 */       if ((function < this.nFits) && (function == 0) && (p != null) && (p[0] == 255.0D) && (p[1] == -1.0D)) {
/*  75 */         defaultChoice = "Pixel Inverter";
/*     */       }
/*     */       else
/*     */       {
/*     */         String defaultChoice;
/*  76 */         if (function < this.nFits) {
/*  77 */           defaultChoice = CurveFitter.fitList[function];
/*     */         }
/*     */         else
/*     */         {
/*     */           String defaultChoice;
/*  78 */           if (function == 21) {
/*  79 */             defaultChoice = "Uncalibrated OD";
/*     */           }
/*     */           else
/*     */           {
/*     */             String defaultChoice;
/*  80 */             if (function == 22)
/*  81 */               defaultChoice = "Custom";
/*     */             else
/*  83 */               defaultChoice = "None"; 
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  85 */     String tmpText = getMeans();
/*  86 */     if ((!importedValues) && (!tmpText.equals("")))
/*  87 */       xText = tmpText;
/*  88 */     this.gd = new GenericDialog("Calibrate...");
/*  89 */     this.gd.addChoice("Function:", this.functions, defaultChoice);
/*  90 */     this.gd.addStringField("Unit:", this.unit, 16);
/*  91 */     this.gd.addTextAreas(xText, yText, 20, 14);
/*     */ 
/*  93 */     this.gd.addPanel(makeButtonPanel(this.gd));
/*  94 */     this.gd.addCheckbox("Global calibration", this.global1);
/*     */ 
/*  96 */     this.gd.addHelp("http://imagej.nih.gov/ij/docs/menus/analyze.html#cal");
/*  97 */     this.gd.showDialog();
/*  98 */     if (this.gd.wasCanceled()) {
/*  99 */       return false;
/*     */     }
/* 101 */     this.choiceIndex = this.gd.getNextChoiceIndex();
/* 102 */     this.unit = this.gd.getNextString();
/* 103 */     xText = this.gd.getNextText();
/* 104 */     yText = this.gd.getNextText();
/* 105 */     this.global2 = this.gd.getNextBoolean();
/*     */ 
/* 107 */     return true;
/*     */   }
/*     */ 
/*     */   Panel makeButtonPanel(GenericDialog gd)
/*     */   {
/* 113 */     Panel buttons = new Panel();
/* 114 */     buttons.setLayout(new FlowLayout(1, 5, 0));
/* 115 */     this.open = new Button("Open...");
/* 116 */     this.open.addActionListener(this);
/* 117 */     buttons.add(this.open);
/* 118 */     this.save = new Button("Save...");
/* 119 */     this.save.addActionListener(this);
/* 120 */     buttons.add(this.save);
/* 121 */     return buttons;
/*     */   }
/*     */ 
/*     */   public void calibrate(ImagePlus imp) {
/* 125 */     Calibration cal = imp.getCalibration();
/* 126 */     Calibration calOrig = cal.copy();
/* 127 */     int function = 20;
/* 128 */     boolean is16Bits = imp.getType() == 1;
/* 129 */     double[] parameters = null;
/* 130 */     double[] x = null; double[] y = null;
/* 131 */     boolean zeroClip = false;
/* 132 */     if (this.choiceIndex <= 0) {
/* 133 */       if ((this.oldFunction == 20) && (!yText.equals("")) && (!xText.equals("")))
/* 134 */         IJ.error("Calibrate", "Please select a function");
/* 135 */       function = 20;
/* 136 */     } else if (this.choiceIndex <= this.nFits) {
/* 137 */       function = this.choiceIndex - 1;
/* 138 */       x = getData(xText);
/* 139 */       y = getData(yText);
/* 140 */       if ((!cal.calibrated()) || (y.length != 0) || (function != this.oldFunction)) {
/* 141 */         parameters = doCurveFitting(x, y, function);
/* 142 */         if (parameters == null)
/* 143 */           return;
/*     */       }
/* 145 */       if ((!is16Bits) && (function != 0)) {
/* 146 */         zeroClip = true;
/* 147 */         for (int i = 0; i < y.length; i++)
/* 148 */           if (y[i] < 0.0D) zeroClip = false; 
/*     */       }
/*     */     }
/* 150 */     else if (this.choiceIndex == this.inverterIndex) {
/* 151 */       function = 0;
/* 152 */       parameters = new double[2];
/* 153 */       if (is16Bits)
/* 154 */         parameters[0] = 65535.0D;
/*     */       else
/* 156 */         parameters[0] = 255.0D;
/* 157 */       parameters[1] = -1.0D;
/* 158 */       this.unit = "Inverted Gray Value";
/* 159 */     } else if (this.choiceIndex == this.odIndex) {
/* 160 */       if (is16Bits) {
/* 161 */         IJ.error("Calibrate", "Uncalibrated OD is not supported on 16-bit images.");
/* 162 */         return;
/*     */       }
/* 164 */       function = 21;
/* 165 */       this.unit = "Uncalibrated OD";
/*     */     }
/* 167 */     cal.setFunction(function, parameters, this.unit, zeroClip);
/* 168 */     if (!cal.equals(calOrig))
/* 169 */       imp.setCalibration(cal);
/* 170 */     imp.setGlobalCalibration(this.global2 ? cal : null);
/* 171 */     if ((this.global2) || (this.global2 != this.global1))
/* 172 */       WindowManager.repaintImageWindows();
/*     */     else
/* 174 */       imp.repaintWindow();
/* 175 */     if ((this.global2) && (this.global2 != this.global1))
/* 176 */       FileOpener.setShowConflictMessage(true);
/* 177 */     if (function != 20)
/* 178 */       showPlot(x, y, cal, this.fitGoodness);
/*     */   }
/*     */ 
/*     */   double[] doCurveFitting(double[] x, double[] y, int fitType) {
/* 182 */     if ((x.length != y.length) || (y.length == 0)) {
/* 183 */       IJ.error("Calibrate", "To create a calibration curve, the left column must\ncontain a list of measured mean pixel values and the\nright column must contain the same number of calibration\nstandard values. Use the Measure command to add mean\npixel value measurements to the left column.\n \n    Left column: " + x.length + " values\n" + "    Right column: " + y.length + " values\n");
/*     */ 
/* 193 */       return null;
/*     */     }
/* 195 */     int n = x.length;
/* 196 */     double xmin = 0.0D;
/*     */     double xmax;
/*     */     double xmax;
/* 197 */     if (this.imp.getType() == 1)
/* 198 */       xmax = 65535.0D;
/*     */     else
/* 200 */       xmax = 255.0D;
/* 201 */     double[] a = Tools.getMinMax(y);
/* 202 */     double ymin = a[0]; double ymax = a[1];
/* 203 */     CurveFitter cf = new CurveFitter(x, y);
/* 204 */     cf.doFit(fitType, showSettings);
/* 205 */     int np = cf.getNumParams();
/* 206 */     double[] p = cf.getParams();
/* 207 */     this.fitGoodness = IJ.d2s(cf.getRSquared(), 6);
/* 208 */     double[] parameters = new double[np];
/* 209 */     for (int i = 0; i < np; i++)
/* 210 */       parameters[i] = p[i];
/* 211 */     return parameters;
/*     */   }
/*     */ 
/*     */   void showPlot(double[] x, double[] y, Calibration cal, String rSquared) {
/* 215 */     if ((!cal.calibrated()) || ((IJ.macroRunning()) && (Macro.getOptions() != null))) {
/* 216 */       return;
/*     */     }
/* 218 */     float[] ctable = cal.getCTable();
/*     */     int xmax;
/*     */     int xmin;
/*     */     int xmax;
/* 219 */     if (ctable.length == 256) {
/* 220 */       int xmin = 0;
/* 221 */       xmax = 255;
/*     */     } else {
/* 223 */       xmin = 0;
/* 224 */       xmax = 65535;
/*     */     }
/* 226 */     int range = 256;
/* 227 */     float[] px = new float[range];
/* 228 */     float[] py = new float[range];
/* 229 */     for (int i = 0; i < range; i++)
/* 230 */       px[i] = ((float)(i / 255.0D * xmax));
/* 231 */     for (int i = 0; i < range; i++)
/* 232 */       py[i] = ctable[((int)px[i])];
/* 233 */     double[] a = Tools.getMinMax(py);
/* 234 */     double ymin = a[0];
/* 235 */     double ymax = a[1];
/* 236 */     int fit = cal.getFunction();
/* 237 */     String unit = cal.getValueUnit();
/* 238 */     Plot plot = new Plot("Calibration Function", "pixel value", unit, px, py);
/* 239 */     plot.setLimits(xmin, xmax, ymin, ymax);
/* 240 */     if ((x != null) && (y != null) && (x.length > 0) && (y.length > 0))
/* 241 */       plot.addPoints(x, y, 0);
/* 242 */     double[] p = cal.getCoefficients();
/* 243 */     if (fit <= 9) {
/* 244 */       drawLabel(plot, CurveFitter.fList[fit]);
/* 245 */       this.ly += 0.04D;
/*     */     }
/* 247 */     if (p != null) {
/* 248 */       int np = p.length;
/* 249 */       drawLabel(plot, "a=" + IJ.d2s(p[0], 6));
/* 250 */       drawLabel(plot, "b=" + IJ.d2s(p[1], 6));
/* 251 */       if (np >= 3)
/* 252 */         drawLabel(plot, "c=" + IJ.d2s(p[2], 6));
/* 253 */       if (np >= 4)
/* 254 */         drawLabel(plot, "d=" + IJ.d2s(p[3], 6));
/* 255 */       if (np >= 5)
/* 256 */         drawLabel(plot, "e=" + IJ.d2s(p[4], 6));
/* 257 */       this.ly += 0.04D;
/*     */     }
/* 259 */     if (rSquared != null) {
/* 260 */       drawLabel(plot, "R^2=" + rSquared); rSquared = null;
/* 261 */     }plot.show();
/*     */   }
/*     */ 
/*     */   void drawLabel(Plot plot, String label) {
/* 265 */     plot.addLabel(this.lx, this.ly, label);
/* 266 */     this.ly += 0.08D;
/*     */   }
/*     */ 
/*     */   double sqr(double x) {
/* 270 */     return x * x;
/*     */   }
/*     */   String[] getFunctionList(boolean custom) {
/* 273 */     int n = this.nFits + 4;
/* 274 */     if (custom) n++;
/* 275 */     String[] list = new String[n];
/* 276 */     list[0] = "None";
/* 277 */     for (int i = 0; i < this.nFits; i++)
/* 278 */       list[(1 + i)] = CurveFitter.fitList[i];
/* 279 */     list[this.spacerIndex] = "-";
/* 280 */     list[this.inverterIndex] = "Pixel Inverter";
/* 281 */     list[this.odIndex] = "Uncalibrated OD";
/* 282 */     if (custom)
/* 283 */       list[this.customIndex] = "Custom";
/* 284 */     return list;
/*     */   }
/*     */ 
/*     */   String getMeans() {
/* 288 */     float[] umeans = Analyzer.getUMeans();
/* 289 */     int count = Analyzer.getCounter();
/* 290 */     if ((umeans == null) || (count == 0))
/* 291 */       return "";
/* 292 */     if (count > 20)
/* 293 */       count = 20;
/* 294 */     String s = "";
/* 295 */     for (int i = 0; i < count; i++)
/* 296 */       s = s + IJ.d2s(umeans[i], 2) + "\n";
/* 297 */     importedValues = false;
/* 298 */     return s;
/*     */   }
/*     */ 
/*     */   double[] getData(String xData) {
/* 302 */     int len = xData.length();
/* 303 */     StringBuffer sb = new StringBuffer(len);
/* 304 */     for (int i = 0; i < len; i++) {
/* 305 */       char c = xData.charAt(i);
/* 306 */       if (((c >= '0') && (c <= '9')) || (c == '-') || (c == '.') || (c == ',') || (c == '\n') || (c == '\r') || (c == ' '))
/* 307 */         sb.append(c);
/*     */     }
/* 309 */     xData = sb.toString();
/*     */ 
/* 311 */     StringTokenizer st = new StringTokenizer(xData);
/* 312 */     int nTokens = st.countTokens();
/* 313 */     if (nTokens < 1)
/* 314 */       return new double[0];
/* 315 */     int n = nTokens;
/* 316 */     double[] data = new double[n];
/* 317 */     for (int i = 0; i < n; i++) {
/* 318 */       data[i] = getNum(st);
/*     */     }
/* 320 */     return data;
/*     */   }
/*     */   double getNum(StringTokenizer st) {
/* 325 */     String token = st.nextToken();
/*     */     Double d;
/*     */     try { d = new Double(token); } catch (NumberFormatException e) {
/* 327 */       d = null;
/* 328 */     }if (d != null) {
/* 329 */       return d.doubleValue();
/*     */     }
/* 331 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   void save() {
/* 335 */     TextArea ta1 = this.gd.getTextArea1();
/* 336 */     TextArea ta2 = this.gd.getTextArea2();
/* 337 */     ta1.selectAll();
/* 338 */     String text1 = ta1.getText();
/* 339 */     ta1.select(0, 0);
/* 340 */     ta2.selectAll();
/* 341 */     String text2 = ta2.getText();
/* 342 */     ta2.select(0, 0);
/* 343 */     double[] x = getData(text1);
/* 344 */     double[] y = getData(text2);
/* 345 */     SaveDialog sd = new SaveDialog("Save as Text...", "calibration", ".txt");
/* 346 */     String name = sd.getFileName();
/* 347 */     if (name == null)
/* 348 */       return;
/* 349 */     String directory = sd.getDirectory();
/* 350 */     PrintWriter pw = null;
/*     */     try {
/* 352 */       FileOutputStream fos = new FileOutputStream(directory + name);
/* 353 */       BufferedOutputStream bos = new BufferedOutputStream(fos);
/* 354 */       pw = new PrintWriter(bos);
/*     */     }
/*     */     catch (IOException e) {
/* 357 */       IJ.error("" + e);
/* 358 */       return;
/*     */     }
/* 360 */     IJ.wait(250);
/* 361 */     int n = Math.max(x.length, y.length);
/* 362 */     for (int i = 0; i < n; i++) {
/* 363 */       String xs = i < x.length ? "" + x[i] : x.length == 0 ? "" : "0";
/* 364 */       String ys = i < y.length ? "" + y[i] : y.length == 0 ? "" : "0";
/* 365 */       pw.println(xs + "\t" + ys);
/*     */     }
/* 367 */     pw.close();
/*     */   }
/*     */ 
/*     */   void open() {
/* 371 */     OpenDialog od = new OpenDialog("Open Calibration...", "");
/* 372 */     String directory = od.getDirectory();
/* 373 */     String name = od.getFileName();
/* 374 */     if (name == null)
/* 375 */       return;
/* 376 */     String path = directory + name;
/* 377 */     TextReader tr = new TextReader();
/* 378 */     ImageProcessor ip = tr.open(path);
/* 379 */     if (ip == null)
/* 380 */       return;
/* 381 */     int width = ip.getWidth();
/* 382 */     int height = ip.getHeight();
/* 383 */     if (((width != 1) && (width != 2)) || (height <= 1)) {
/* 384 */       IJ.error("Calibrate", "This appears to not be a one or two column text file");
/* 385 */       return;
/*     */     }
/* 387 */     StringBuffer sb = new StringBuffer();
/* 388 */     for (int i = 0; i < height; i++) {
/* 389 */       sb.append("" + ip.getPixelValue(0, i));
/* 390 */       sb.append("\n");
/*     */     }
/* 392 */     String s1 = null; String s2 = null;
/* 393 */     if (width == 2) {
/* 394 */       s1 = new String(sb);
/* 395 */       sb = new StringBuffer();
/* 396 */       for (int i = 0; i < height; i++) {
/* 397 */         sb.append("" + ip.getPixelValue(1, i));
/* 398 */         sb.append("\n");
/*     */       }
/* 400 */       s2 = new String(sb);
/*     */     } else {
/* 402 */       s2 = new String(sb);
/* 403 */     }if (s1 != null) {
/* 404 */       TextArea ta1 = this.gd.getTextArea1();
/* 405 */       ta1.selectAll();
/* 406 */       ta1.setText(s1);
/*     */     }
/* 408 */     TextArea ta2 = this.gd.getTextArea2();
/* 409 */     ta2.selectAll();
/* 410 */     ta2.setText(s2);
/* 411 */     importedValues = true;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 415 */     Object source = e.getSource();
/* 416 */     if (source == this.save)
/* 417 */       save();
/* 418 */     else if (source == this.open)
/* 419 */       open();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Calibrator
 * JD-Core Version:    0.6.2
 */