/*     */ package ij.plugin.frame;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GUI;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Plot;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.measure.CurveFitter;
/*     */ import ij.plugin.PlugIn;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Button;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Panel;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class Fitter extends PlugInFrame
/*     */   implements PlugIn, ItemListener, ActionListener
/*     */ {
/*     */   Choice fit;
/*     */   Button doIt;
/*     */   Button open;
/*     */   Button apply;
/*     */   Checkbox settings;
/*  27 */   String fitTypeStr = CurveFitter.fitList[0];
/*     */   TextArea textArea;
/*  30 */   double[] dx = { 0.0D, 1.0D, 2.0D, 3.0D, 4.0D, 5.0D };
/*  31 */   double[] dy = { 0.0D, 0.9D, 4.5D, 8.0D, 18.0D, 24.0D };
/*     */   double[] x;
/*     */   double[] y;
/*     */   static CurveFitter cf;
/*     */   static int fitType;
/*  36 */   static String equation = "y = a + b*x + c*x*x";
/*     */   static final int USER_DEFINED = 100;
/*     */ 
/*     */   public Fitter()
/*     */   {
/*  40 */     super("Curve Fitter");
/*  41 */     WindowManager.addWindow(this);
/*  42 */     Panel panel = new Panel();
/*  43 */     this.fit = new Choice();
/*  44 */     for (int i = 0; i < CurveFitter.fitList.length; i++)
/*  45 */       this.fit.addItem(CurveFitter.fitList[i]);
/*  46 */     this.fit.addItem("*User-defined*");
/*  47 */     this.fit.addItemListener(this);
/*  48 */     panel.add(this.fit);
/*  49 */     this.doIt = new Button(" Fit ");
/*  50 */     this.doIt.addActionListener(this);
/*  51 */     panel.add(this.doIt);
/*  52 */     this.open = new Button("Open");
/*  53 */     this.open.addActionListener(this);
/*  54 */     panel.add(this.open);
/*  55 */     this.apply = new Button("Apply");
/*  56 */     this.apply.addActionListener(this);
/*  57 */     panel.add(this.apply);
/*  58 */     this.settings = new Checkbox("Show settings", false);
/*  59 */     panel.add(this.settings);
/*  60 */     add("North", panel);
/*  61 */     String text = "";
/*  62 */     for (int i = 0; i < this.dx.length; i++)
/*  63 */       text = text + IJ.d2s(this.dx[i], 2) + "  " + IJ.d2s(this.dy[i], 2) + "\n";
/*  64 */     this.textArea = new TextArea("", 15, 30, 1);
/*     */ 
/*  66 */     this.textArea.setFont(new Font("Monospaced", 0, 12));
/*  67 */     if (IJ.isLinux()) this.textArea.setBackground(Color.white);
/*  68 */     this.textArea.append(text);
/*  69 */     add("Center", this.textArea);
/*  70 */     pack();
/*  71 */     GUI.center(this);
/*  72 */     show();
/*  73 */     IJ.register(Fitter.class);
/*     */   }
/*     */ 
/*     */   public void doFit(int fitType) {
/*  77 */     if (fitType >= CurveFitter.fitList.length)
/*  78 */       fitType = 100;
/*  79 */     fitType = fitType;
/*  80 */     if (!getData())
/*  81 */       return;
/*  82 */     cf = new CurveFitter(this.x, this.y);
/*  83 */     if (fitType == 100) {
/*  84 */       String eqn = getEquation();
/*  85 */       if (eqn == null) return;
/*  86 */       int params = cf.doCustomFit(eqn, null, this.settings.getState());
/*  87 */       if (params == 0) return; 
/*     */     }
/*  89 */     else { cf.doFit(fitType, this.settings.getState()); }
/*  90 */     IJ.log(cf.getResultString());
/*  91 */     plot(cf);
/*     */   }
/*     */ 
/*     */   String getEquation() {
/*  95 */     GenericDialog gd = new GenericDialog("Formula");
/*  96 */     gd.addStringField("Formula:", equation, 38);
/*  97 */     gd.showDialog();
/*  98 */     if (gd.wasCanceled())
/*  99 */       return null;
/* 100 */     equation = gd.getNextString();
/* 101 */     return equation;
/*     */   }
/*     */ 
/*     */   public static void plot(CurveFitter cf) {
/* 105 */     double[] x = cf.getXPoints();
/* 106 */     double[] y = cf.getYPoints();
/* 107 */     double[] a = Tools.getMinMax(x);
/* 108 */     double xmin = a[0]; double xmax = a[1];
/* 109 */     a = Tools.getMinMax(y);
/* 110 */     double ymin = a[0]; double ymax = a[1];
/* 111 */     float[] px = new float[100];
/* 112 */     float[] py = new float[100];
/* 113 */     double inc = (xmax - xmin) / 99.0D;
/* 114 */     double tmp = xmin;
/* 115 */     for (int i = 0; i < 100; i++) {
/* 116 */       px[i] = ((float)tmp);
/* 117 */       tmp += inc;
/*     */     }
/* 119 */     double[] params = cf.getParams();
/* 120 */     for (int i = 0; i < 100; i++)
/* 121 */       py[i] = ((float)cf.f(params, px[i]));
/* 122 */     a = Tools.getMinMax(py);
/* 123 */     ymin = Math.min(ymin, a[0]);
/* 124 */     ymax = Math.max(ymax, a[1]);
/* 125 */     Plot plot = new Plot(cf.getFormula(), "X", "Y", px, py);
/* 126 */     plot.setLimits(xmin, xmax, ymin, ymax);
/* 127 */     plot.addPoints(x, y, 0);
/* 128 */     double yloc = 0.1D;
/* 129 */     double yinc = 0.08500000000000001D;
/* 130 */     plot.addLabel(0.02D, yloc, cf.getName()); yloc += yinc;
/* 131 */     plot.addLabel(0.02D, yloc, cf.getFormula()); yloc += yinc;
/* 132 */     double[] p = cf.getParams();
/* 133 */     int n = cf.getNumParams();
/* 134 */     char pChar = 'a';
/* 135 */     for (int i = 0; i < n; i++) {
/* 136 */       plot.addLabel(0.02D, yloc, pChar + "=" + IJ.d2s(p[i], 4));
/* 137 */       yloc += yinc;
/* 138 */       pChar = (char)(pChar + '\001');
/*     */     }
/* 140 */     plot.addLabel(0.02D, yloc, "R^2=" + IJ.d2s(cf.getRSquared(), 3)); yloc += yinc;
/* 141 */     plot.show();
/*     */   }
/*     */   double sqr(double x) {
/* 144 */     return x * x;
/*     */   }
/*     */   boolean getData() {
/* 147 */     this.textArea.selectAll();
/* 148 */     String text = this.textArea.getText();
/* 149 */     text = zapGremlins(text);
/* 150 */     this.textArea.select(0, 0);
/* 151 */     StringTokenizer st = new StringTokenizer(text, " \t\n\r,");
/* 152 */     int nTokens = st.countTokens();
/* 153 */     if ((nTokens < 4) || (nTokens % 2 != 0))
/* 154 */       return false;
/* 155 */     int n = nTokens / 2;
/* 156 */     this.x = new double[n];
/* 157 */     this.y = new double[n];
/* 158 */     for (int i = 0; i < n; i++) {
/* 159 */       this.x[i] = getNum(st);
/* 160 */       this.y[i] = getNum(st);
/*     */     }
/* 162 */     return true;
/*     */   }
/*     */ 
/*     */   void applyFunction() {
/* 166 */     if (cf == null) {
/* 167 */       IJ.error("No function available");
/* 168 */       return;
/*     */     }
/* 170 */     ImagePlus img = WindowManager.getCurrentImage();
/* 171 */     if (img == null) {
/* 172 */       IJ.noImage();
/* 173 */       return;
/*     */     }
/* 175 */     if (img.getTitle().startsWith("y=")) {
/* 176 */       IJ.error("First select the image to be transformed");
/* 177 */       return;
/*     */     }
/* 179 */     double[] p = cf.getParams();
/* 180 */     int width = img.getWidth();
/* 181 */     int height = img.getHeight();
/* 182 */     int size = width * height;
/* 183 */     float[] data = new float[size];
/* 184 */     ImageProcessor ip = img.getProcessor();
/*     */ 
/* 186 */     for (int y = 0; y < height; y++) {
/* 187 */       for (int x = 0; x < width; x++) {
/* 188 */         float value = ip.getPixelValue(x, y);
/* 189 */         data[(y * width + x)] = ((float)cf.f(p, value));
/*     */       }
/*     */     }
/* 192 */     ImageProcessor ip2 = new FloatProcessor(width, height, data, ip.getColorModel());
/* 193 */     new ImagePlus(img.getTitle() + "-transformed", ip2).show();
/*     */   }
/*     */   double getNum(StringTokenizer st) {
/* 198 */     String token = st.nextToken();
/*     */     Double d;
/*     */     try { d = new Double(token); } catch (NumberFormatException e) {
/* 200 */       d = null;
/* 201 */     }if (d != null) {
/* 202 */       return d.doubleValue();
/*     */     }
/* 204 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   void open() {
/* 208 */     OpenDialog od = new OpenDialog("Open Text File...", "");
/* 209 */     String directory = od.getDirectory();
/* 210 */     String name = od.getFileName();
/* 211 */     if (name == null)
/* 212 */       return;
/* 213 */     String path = directory + name;
/* 214 */     this.textArea.selectAll();
/* 215 */     this.textArea.setText("");
/*     */     try {
/* 217 */       BufferedReader r = new BufferedReader(new FileReader(directory + name));
/*     */       while (true) {
/* 219 */         String s = r.readLine();
/* 220 */         if ((s == null) || 
/* 221 */           (s.length() > 100)) break;
/* 222 */         this.textArea.append(s + "\n");
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 226 */       IJ.error(e.getMessage());
/* 227 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/* 232 */     this.fitTypeStr = this.fit.getSelectedItem();
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 236 */     if (e.getSource() == this.doIt)
/* 237 */       doFit(this.fit.getSelectedIndex());
/* 238 */     else if (e.getSource() == this.apply)
/* 239 */       applyFunction();
/*     */     else
/* 241 */       open();
/*     */   }
/*     */ 
/*     */   String zapGremlins(String text)
/*     */   {
/* 249 */     char[] chars = new char[text.length()];
/* 250 */     chars = text.toCharArray();
/* 251 */     int count = 0;
/* 252 */     for (int i = 0; i < chars.length; i++) {
/* 253 */       char c = chars[i];
/* 254 */       if ((c != '\n') && (c != '\t') && ((c < ' ') || (c > ''))) {
/* 255 */         count++;
/* 256 */         chars[i] = ' ';
/*     */       }
/*     */     }
/* 259 */     if (count > 0) {
/* 260 */       return new String(chars);
/*     */     }
/* 262 */     return text;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.Fitter
 * JD-Core Version:    0.6.2
 */