/*     */ package ij.measure;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.macro.Program;
/*     */ import ij.macro.Tokenizer;
/*     */ 
/*     */ public class CurveFitter
/*     */ {
/*     */   public static final int STRAIGHT_LINE = 0;
/*     */   public static final int POLY2 = 1;
/*     */   public static final int POLY3 = 2;
/*     */   public static final int POLY4 = 3;
/*     */   public static final int EXPONENTIAL = 4;
/*     */   public static final int POWER = 5;
/*     */   public static final int LOG = 6;
/*     */   public static final int RODBARD = 7;
/*     */   public static final int GAMMA_VARIATE = 8;
/*     */   public static final int LOG2 = 9;
/*     */   public static final int RODBARD2 = 10;
/*     */   public static final int EXP_WITH_OFFSET = 11;
/*     */   public static final int GAUSSIAN = 12;
/*     */   public static final int EXP_RECOVERY = 13;
/*     */   private static final int CUSTOM = 20;
/*     */   public static final int IterFactor = 500;
/*  33 */   public static final String[] fitList = { "Straight Line", "2nd Degree Polynomial", "3rd Degree Polynomial", "4th Degree Polynomial", "Exponential", "Power", "Log", "Rodbard", "Gamma Variate", "y = a+b*ln(x-c)", "Rodbard (NIH Image)", "Exponential with Offset", "Gaussian", "Exponential Recovery" };
/*     */ 
/*  38 */   public static final String[] fList = { "y = a+bx", "y = a+bx+cx^2", "y = a+bx+cx^2+dx^3", "y = a+bx+cx^2+dx^3+ex^4", "y = a*exp(bx)", "y = ax^b", "y = a*ln(bx)", "y = d+(a-d)/(1+(x/c)^b)", "y = a*(x-b)^c*exp(-(x-b)/d)", "y = a+b*ln(x-c)", "y = d+(a-d)/(1+(x/c)^b)", "y = a*exp(-bx) + c", "y = a + (b-a)*exp(-(x-c)*(x-c)/(2*d*d))", "y=a*(1-exp(-b*x)) + c" };
/*     */   private static final double alpha = -1.0D;
/*     */   private static final double beta = 0.5D;
/*     */   private static final double gamma = 2.0D;
/*     */   private static final double root2 = 1.414214D;
/*     */   private int fit;
/*     */   private double[] xData;
/*     */   private double[] yData;
/*     */   private int numPoints;
/*     */   private int numParams;
/*     */   private int numVertices;
/*     */   private int worst;
/*     */   private int nextWorst;
/*     */   private int best;
/*     */   private double[][] simp;
/*     */   private double[] next;
/*     */   private int numIter;
/*     */   private int maxIter;
/*     */   private int restarts;
/*  62 */   private static int defaultRestarts = 2;
/*     */   private int nRestarts;
/*  64 */   private static double maxError = 1.0E-10D;
/*     */   private double[] initialParams;
/*     */   private long time;
/*     */   private String customFormula;
/*     */   private int customParamCount;
/*     */   private Interpreter macro;
/*     */   private double[] initialValues;
/*     */ 
/*     */   public CurveFitter(double[] xData, double[] yData)
/*     */   {
/*  74 */     this.xData = xData;
/*  75 */     this.yData = yData;
/*  76 */     this.numPoints = xData.length;
/*     */   }
/*     */ 
/*     */   public void doFit(int fitType)
/*     */   {
/*  87 */     doFit(fitType, false);
/*     */   }
/*     */ 
/*     */   public void doFit(int fitType, boolean showSettings) {
/*  91 */     if ((fitType < 0) || ((fitType > 13) && (fitType != 20)))
/*  92 */       throw new IllegalArgumentException("Invalid fit type");
/*  93 */     int saveFitType = fitType;
/*  94 */     if (fitType == 10)
/*     */     {
/*  96 */       double[] temp = this.xData;
/*  97 */       this.xData = this.yData;
/*  98 */       this.yData = temp;
/*  99 */       fitType = 7;
/*     */     }
/* 101 */     this.fit = fitType;
/* 102 */     initialize();
/* 103 */     if (this.initialParams != null) {
/* 104 */       for (int i = 0; i < this.numParams; i++)
/* 105 */         this.simp[0][i] = this.initialParams[i];
/* 106 */       this.initialParams = null;
/*     */     }
/* 108 */     if (showSettings) settingsDialog();
/* 109 */     long startTime = System.currentTimeMillis();
/* 110 */     restart(0);
/*     */ 
/* 112 */     this.numIter = 0;
/* 113 */     boolean done = false;
/* 114 */     double[] center = new double[this.numParams];
/* 115 */     while (!done) {
/* 116 */       this.numIter += 1;
/* 117 */       for (int i = 0; i < this.numParams; i++) center[i] = 0.0D;
/*     */ 
/* 119 */       for (int i = 0; i < this.numVertices; i++) {
/* 120 */         if (i != this.worst)
/* 121 */           for (int j = 0; j < this.numParams; j++)
/* 122 */             center[j] += this.simp[i][j];
/*     */       }
/* 124 */       for (int i = 0; i < this.numParams; i++) {
/* 125 */         center[i] /= this.numParams;
/* 126 */         this.next[i] = (center[i] + -1.0D * (this.simp[this.worst][i] - center[i]));
/*     */       }
/* 128 */       sumResiduals(this.next);
/*     */ 
/* 130 */       if (this.next[this.numParams] <= this.simp[this.best][this.numParams]) {
/* 131 */         newVertex();
/*     */ 
/* 133 */         for (int i = 0; i < this.numParams; i++)
/* 134 */           this.next[i] = (center[i] + 2.0D * (this.simp[this.worst][i] - center[i]));
/* 135 */         sumResiduals(this.next);
/*     */ 
/* 137 */         if (this.next[this.numParams] <= this.simp[this.worst][this.numParams]) {
/* 138 */           newVertex();
/*     */         }
/*     */       }
/* 141 */       else if (this.next[this.numParams] <= this.simp[this.nextWorst][this.numParams]) {
/* 142 */         newVertex();
/*     */       }
/*     */       else
/*     */       {
/* 146 */         for (int i = 0; i < this.numParams; i++)
/* 147 */           this.next[i] = (center[i] + 0.5D * (this.simp[this.worst][i] - center[i]));
/* 148 */         sumResiduals(this.next);
/*     */ 
/* 150 */         if (this.next[this.numParams] <= this.simp[this.nextWorst][this.numParams]) {
/* 151 */           newVertex();
/*     */         }
/*     */         else
/*     */         {
/* 155 */           for (int i = 0; i < this.numVertices; i++) {
/* 156 */             if (i != this.best) {
/* 157 */               for (int j = 0; j < this.numVertices; j++)
/* 158 */                 this.simp[i][j] = (0.5D * (this.simp[i][j] + this.simp[this.best][j]));
/* 159 */               sumResiduals(this.simp[i]);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 164 */       order();
/*     */ 
/* 166 */       double rtol = 2.0D * Math.abs(this.simp[this.best][this.numParams] - this.simp[this.worst][this.numParams]) / (Math.abs(this.simp[this.best][this.numParams]) + Math.abs(this.simp[this.worst][this.numParams]) + 1.0E-10D);
/*     */ 
/* 169 */       if (this.numIter >= this.maxIter) {
/* 170 */         done = true;
/* 171 */       } else if (rtol < maxError) {
/* 172 */         this.restarts -= 1;
/* 173 */         if (this.restarts < 0)
/* 174 */           done = true;
/*     */         else
/* 176 */           restart(this.best);
/*     */       }
/*     */     }
/* 179 */     this.fit = saveFitType;
/* 180 */     this.time = (System.currentTimeMillis() - startTime);
/*     */   }
/*     */ 
/*     */   public int doCustomFit(String equation, double[] initialValues, boolean showSettings) {
/* 184 */     this.customFormula = null;
/* 185 */     this.customParamCount = 0;
/* 186 */     Program pgm = new Tokenizer().tokenize(equation);
/* 187 */     if (!pgm.hasWord("y")) return 0;
/* 188 */     if (!pgm.hasWord("x")) return 0;
/* 189 */     String[] params = { "a", "b", "c", "d", "e", "f" };
/* 190 */     for (int i = 0; i < params.length; i++) {
/* 191 */       if (pgm.hasWord(params[i]))
/* 192 */         this.customParamCount += 1;
/*     */     }
/* 194 */     if (this.customParamCount == 0)
/* 195 */       return 0;
/* 196 */     this.customFormula = equation;
/* 197 */     String code = "var x, a, b, c, d, e, f;\nfunction dummy() {}\n" + equation + ";\n";
/*     */ 
/* 201 */     this.macro = new Interpreter();
/* 202 */     this.macro.run(code, null);
/* 203 */     if (this.macro.wasError())
/* 204 */       return 0;
/* 205 */     this.initialValues = initialValues;
/* 206 */     doFit(20, showSettings);
/* 207 */     return this.customParamCount;
/*     */   }
/*     */ 
/*     */   private void settingsDialog()
/*     */   {
/* 212 */     GenericDialog gd = new GenericDialog("Simplex Fitting Options");
/* 213 */     gd.addMessage("Function name: " + getName() + "\n" + "Formula: " + getFormula());
/*     */ 
/* 215 */     char pChar = 'a';
/* 216 */     for (int i = 0; i < this.numParams; i++) {
/* 217 */       gd.addNumericField("Initial " + new Character(pChar).toString() + ":", this.simp[0][i], 2);
/* 218 */       pChar = (char)(pChar + '\001');
/*     */     }
/* 220 */     gd.addNumericField("Maximum iterations:", this.maxIter, 0);
/* 221 */     gd.addNumericField("Number of restarts:", defaultRestarts, 0);
/* 222 */     gd.addNumericField("Error tolerance [1*10^(-x)]:", -(Math.log(maxError) / Math.log(10.0D)), 0);
/* 223 */     gd.showDialog();
/* 224 */     if ((gd.wasCanceled()) || (gd.invalidNumber())) {
/* 225 */       IJ.error("Parameter setting canceled.\nUsing default parameters.");
/*     */     }
/*     */ 
/* 228 */     for (int i = 0; i < this.numParams; i++) {
/* 229 */       this.simp[0][i] = gd.getNextNumber();
/*     */     }
/* 231 */     this.maxIter = ((int)gd.getNextNumber());
/* 232 */     defaultRestarts = this.restarts = (int)gd.getNextNumber();
/* 233 */     maxError = Math.pow(10.0D, -gd.getNextNumber());
/*     */   }
/*     */ 
/*     */   void initialize()
/*     */   {
/* 239 */     this.numParams = getNumParams();
/* 240 */     this.numVertices = (this.numParams + 1);
/* 241 */     this.simp = new double[this.numVertices][this.numVertices];
/* 242 */     this.next = new double[this.numVertices];
/*     */ 
/* 244 */     double firstx = this.xData[0];
/* 245 */     double firsty = this.yData[0];
/* 246 */     double lastx = this.xData[(this.numPoints - 1)];
/* 247 */     double lasty = this.yData[(this.numPoints - 1)];
/* 248 */     double xmean = (firstx + lastx) / 2.0D;
/* 249 */     double ymean = (firsty + lasty) / 2.0D;
/* 250 */     double miny = firsty; double maxy = firsty;
/* 251 */     if (this.fit == 12)
/* 252 */       for (int i = 1; i < this.numPoints; i++) {
/* 253 */         if (this.yData[i] > maxy) maxy = this.yData[i];
/* 254 */         if (this.yData[i] < miny) miny = this.yData[i];
/*     */       }
/*     */     double slope;
/*     */     double slope;
/* 258 */     if (lastx - firstx != 0.0D)
/* 259 */       slope = (lasty - firsty) / (lastx - firstx);
/*     */     else
/* 261 */       slope = 1.0D;
/* 262 */     double yintercept = firsty - slope * firstx;
/* 263 */     if (this.maxIter == 0)
/* 264 */       this.maxIter = (500 * this.numParams * this.numParams);
/* 265 */     this.restarts = defaultRestarts;
/* 266 */     this.nRestarts = 0;
/* 267 */     switch (this.fit) {
/*     */     case 0:
/* 269 */       this.simp[0][0] = yintercept;
/* 270 */       this.simp[0][1] = slope;
/* 271 */       break;
/*     */     case 1:
/* 273 */       this.simp[0][0] = yintercept;
/* 274 */       this.simp[0][1] = slope;
/* 275 */       this.simp[0][2] = 0.0D;
/* 276 */       break;
/*     */     case 2:
/* 278 */       this.simp[0][0] = yintercept;
/* 279 */       this.simp[0][1] = slope;
/* 280 */       this.simp[0][2] = 0.0D;
/* 281 */       this.simp[0][3] = 0.0D;
/* 282 */       break;
/*     */     case 3:
/* 284 */       this.simp[0][0] = yintercept;
/* 285 */       this.simp[0][1] = slope;
/* 286 */       this.simp[0][2] = 0.0D;
/* 287 */       this.simp[0][3] = 0.0D;
/* 288 */       this.simp[0][4] = 0.0D;
/* 289 */       break;
/*     */     case 4:
/* 291 */       this.simp[0][0] = 0.1D;
/* 292 */       this.simp[0][1] = 0.01D;
/* 293 */       break;
/*     */     case 11:
/* 295 */       this.simp[0][0] = 0.1D;
/* 296 */       this.simp[0][1] = 0.01D;
/* 297 */       this.simp[0][2] = 0.1D;
/* 298 */       break;
/*     */     case 13:
/* 300 */       this.simp[0][0] = 0.1D;
/* 301 */       this.simp[0][1] = 0.01D;
/* 302 */       this.simp[0][2] = 0.1D;
/* 303 */       break;
/*     */     case 12:
/* 305 */       this.simp[0][0] = miny;
/* 306 */       this.simp[0][1] = maxy;
/* 307 */       this.simp[0][2] = xmean;
/* 308 */       this.simp[0][3] = 3.0D;
/* 309 */       break;
/*     */     case 5:
/* 311 */       this.simp[0][0] = 0.0D;
/* 312 */       this.simp[0][1] = 1.0D;
/* 313 */       break;
/*     */     case 6:
/* 315 */       this.simp[0][0] = 1.0D;
/* 316 */       this.simp[0][1] = 1.0D;
/* 317 */       break;
/*     */     case 7:
/*     */     case 10:
/* 319 */       this.simp[0][0] = firsty;
/* 320 */       this.simp[0][1] = 1.0D;
/* 321 */       this.simp[0][2] = xmean;
/* 322 */       this.simp[0][3] = lasty;
/* 323 */       break;
/*     */     case 8:
/* 330 */       this.simp[0][0] = firstx;
/* 331 */       double ab = this.xData[getMax(this.yData)] - firstx;
/* 332 */       this.simp[0][2] = Math.sqrt(ab);
/* 333 */       this.simp[0][3] = Math.sqrt(ab);
/* 334 */       this.simp[0][1] = (this.yData[getMax(this.yData)] / (Math.pow(ab, this.simp[0][2]) * Math.exp(-ab / this.simp[0][3])));
/* 335 */       break;
/*     */     case 9:
/* 337 */       this.simp[0][0] = 0.5D;
/* 338 */       this.simp[0][1] = 0.05D;
/* 339 */       this.simp[0][2] = 0.0D;
/* 340 */       break;
/*     */     case 20:
/* 342 */       if (this.macro == null)
/* 343 */         throw new IllegalArgumentException("No custom formula!");
/* 344 */       if ((this.initialValues != null) && (this.initialValues.length >= this.numParams))
/* 345 */         for (int i = 0; i < this.numParams; i++)
/* 346 */           this.simp[0][i] = this.initialValues[i];
/*     */       else
/* 348 */         for (int i = 0; i < this.numParams; i++)
/* 349 */           this.simp[0][i] = 1.0D;  break;
/*     */     case 14:
/*     */     case 15:
/*     */     case 16:
/*     */     case 17:
/*     */     case 18:
/*     */     case 19: } 
/*     */   }
/*     */ 
/* 358 */   void restart(int n) { for (int i = 0; i < this.numParams; i++) {
/* 359 */       this.simp[0][i] = this.simp[n][i];
/*     */     }
/* 361 */     sumResiduals(this.simp[0]);
/* 362 */     double[] step = new double[this.numParams];
/* 363 */     for (int i = 0; i < this.numParams; i++) {
/* 364 */       step[i] = (this.simp[0][i] / 2.0D);
/* 365 */       if (step[i] == 0.0D) {
/* 366 */         step[i] = 0.01D;
/*     */       }
/*     */     }
/* 369 */     double[] p = new double[this.numParams];
/* 370 */     double[] q = new double[this.numParams];
/* 371 */     for (int i = 0; i < this.numParams; i++) {
/* 372 */       p[i] = (step[i] * (Math.sqrt(this.numVertices) + this.numParams - 1.0D) / (this.numParams * 1.414214D));
/* 373 */       q[i] = (step[i] * (Math.sqrt(this.numVertices) - 1.0D) / (this.numParams * 1.414214D));
/*     */     }
/*     */ 
/* 376 */     for (int i = 1; i < this.numVertices; i++) {
/* 377 */       for (int j = 0; j < this.numParams; j++) {
/* 378 */         this.simp[i][j] = (this.simp[(i - 1)][j] + q[j]);
/*     */       }
/* 380 */       this.simp[i][(i - 1)] += p[(i - 1)];
/* 381 */       sumResiduals(this.simp[i]);
/*     */     }
/*     */ 
/* 384 */     this.best = 0;
/* 385 */     this.worst = 0;
/* 386 */     this.nextWorst = 0;
/* 387 */     order();
/* 388 */     this.nRestarts += 1;
/*     */   }
/*     */ 
/*     */   void showSimplex(int iter)
/*     */   {
/* 393 */     IJ.log("" + iter);
/* 394 */     for (int i = 0; i < this.numVertices; i++) {
/* 395 */       String s = "";
/* 396 */       for (int j = 0; j < this.numVertices; j++)
/* 397 */         s = s + "  " + IJ.d2s(this.simp[i][j], 6);
/* 398 */       IJ.log(s);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getNumParams()
/*     */   {
/* 404 */     switch (this.fit) { case 0:
/* 405 */       return 2;
/*     */     case 1:
/* 406 */       return 3;
/*     */     case 2:
/* 407 */       return 4;
/*     */     case 3:
/* 408 */       return 5;
/*     */     case 4:
/* 409 */       return 2;
/*     */     case 5:
/* 410 */       return 2;
/*     */     case 6:
/* 411 */       return 2;
/*     */     case 7:
/*     */     case 10:
/* 412 */       return 4;
/*     */     case 8:
/* 413 */       return 4;
/*     */     case 9:
/* 414 */       return 3;
/*     */     case 11:
/* 415 */       return 3;
/*     */     case 12:
/* 416 */       return 4;
/*     */     case 13:
/* 417 */       return 3;
/*     */     case 20:
/* 418 */       return this.customParamCount;
/*     */     case 14:
/*     */     case 15:
/*     */     case 16:
/*     */     case 17:
/*     */     case 18:
/* 420 */     case 19: } return 0;
/*     */   }
/*     */ 
/*     */   public double f(double[] p, double x)
/*     */   {
/* 425 */     if (this.fit == 20) {
/* 426 */       this.macro.setVariable("x", x);
/* 427 */       this.macro.setVariable("a", p[0]);
/* 428 */       if (this.customParamCount > 1) this.macro.setVariable("b", p[1]);
/* 429 */       if (this.customParamCount > 2) this.macro.setVariable("c", p[2]);
/* 430 */       if (this.customParamCount > 3) this.macro.setVariable("d", p[3]);
/* 431 */       if (this.customParamCount > 4) this.macro.setVariable("e", p[4]);
/* 432 */       if (this.customParamCount > 5) this.macro.setVariable("f", p[5]);
/* 433 */       this.macro.run(21);
/* 434 */       return this.macro.getVariable("y");
/*     */     }
/* 436 */     return f(this.fit, p, x);
/*     */   }
/*     */ 
/*     */   public static double f(int fit, double[] p, double x)
/*     */   {
/*     */     double y;
/* 442 */     switch (fit) {
/*     */     case 0:
/* 444 */       return p[0] + p[1] * x;
/*     */     case 1:
/* 446 */       return p[0] + p[1] * x + p[2] * x * x;
/*     */     case 2:
/* 448 */       return p[0] + p[1] * x + p[2] * x * x + p[3] * x * x * x;
/*     */     case 3:
/* 450 */       return p[0] + p[1] * x + p[2] * x * x + p[3] * x * x * x + p[4] * x * x * x * x;
/*     */     case 4:
/* 452 */       return p[0] * Math.exp(p[1] * x);
/*     */     case 11:
/* 454 */       return p[0] * Math.exp(p[1] * x * -1.0D) + p[2];
/*     */     case 13:
/* 456 */       return p[0] * (1.0D - Math.exp(-p[1] * x)) + p[2];
/*     */     case 12:
/* 458 */       return p[0] + (p[1] - p[0]) * Math.exp(-(x - p[2]) * (x - p[2]) / (2.0D * p[3] * p[3]));
/*     */     case 5:
/* 460 */       if (x == 0.0D) {
/* 461 */         return 0.0D;
/*     */       }
/* 463 */       return p[0] * Math.exp(p[1] * Math.log(x));
/*     */     case 6:
/* 465 */       if (x == 0.0D)
/* 466 */         x = 0.5D;
/* 467 */       return p[0] * Math.log(p[1] * x);
/*     */     case 7:
/*     */       double ex;
/*     */       double ex;
/* 470 */       if (x == 0.0D)
/* 471 */         ex = 0.0D;
/*     */       else
/* 473 */         ex = Math.exp(Math.log(x / p[2]) * p[1]);
/* 474 */       y = p[0] - p[3];
/* 475 */       y /= (1.0D + ex);
/* 476 */       return y + p[3];
/*     */     case 8:
/* 478 */       if (p[0] >= x) return 0.0D;
/* 479 */       if (p[1] <= 0.0D) return -100000.0D;
/* 480 */       if (p[2] <= 0.0D) return -100000.0D;
/* 481 */       if (p[3] <= 0.0D) return -100000.0D;
/*     */ 
/* 483 */       double pw = Math.pow(x - p[0], p[2]);
/* 484 */       double e = Math.exp(-(x - p[0]) / p[3]);
/* 485 */       return p[1] * pw * e;
/*     */     case 9:
/* 487 */       double tmp = x - p[2];
/* 488 */       if (tmp < 0.001D) tmp = 0.001D;
/* 489 */       return p[0] + p[1] * Math.log(tmp);
/*     */     case 10:
/*     */       double y;
/* 491 */       if (x <= p[0]) {
/* 492 */         y = 0.0D;
/*     */       } else {
/* 494 */         y = (p[0] - x) / (x - p[3]);
/* 495 */         y = Math.exp(Math.log(y) * (1.0D / p[1]));
/* 496 */         y *= p[2];
/*     */       }
/* 498 */       return y;
/*     */     }
/* 500 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public double[] getParams()
/*     */   {
/* 506 */     order();
/* 507 */     return this.simp[this.best];
/*     */   }
/*     */ 
/*     */   public double[] getResiduals()
/*     */   {
/* 512 */     int saveFit = this.fit;
/* 513 */     if (this.fit == 10) this.fit = 7;
/* 514 */     double[] params = getParams();
/* 515 */     double[] residuals = new double[this.numPoints];
/* 516 */     if (this.fit == 20)
/* 517 */       for (int i = 0; i < this.numPoints; i++)
/* 518 */         residuals[i] = (this.yData[i] - f(params, this.xData[i]));
/*     */     else {
/* 520 */       for (int i = 0; i < this.numPoints; i++)
/* 521 */         residuals[i] = (this.yData[i] - f(this.fit, params, this.xData[i]));
/*     */     }
/* 523 */     this.fit = saveFit;
/* 524 */     return residuals;
/*     */   }
/*     */ 
/*     */   public double getSumResidualsSqr()
/*     */   {
/* 531 */     double sumResidualsSqr = getParams()[getNumParams()];
/* 532 */     return sumResidualsSqr;
/*     */   }
/*     */ 
/*     */   public double getSD()
/*     */   {
/* 537 */     double[] residuals = getResiduals();
/* 538 */     int n = residuals.length;
/* 539 */     double sum = 0.0D; double sum2 = 0.0D;
/* 540 */     for (int i = 0; i < n; i++) {
/* 541 */       sum += residuals[i];
/* 542 */       sum2 += residuals[i] * residuals[i];
/*     */     }
/* 544 */     double stdDev = (n * sum2 - sum * sum) / n;
/* 545 */     return Math.sqrt(stdDev / (n - 1.0D));
/*     */   }
/*     */ 
/*     */   public double getRSquared()
/*     */   {
/* 557 */     double sumY = 0.0D;
/* 558 */     for (int i = 0; i < this.numPoints; i++) sumY += this.yData[i];
/* 559 */     double mean = sumY / this.numPoints;
/* 560 */     double sumMeanDiffSqr = 0.0D;
/* 561 */     for (int i = 0; i < this.numPoints; i++)
/* 562 */       sumMeanDiffSqr += sqr(this.yData[i] - mean);
/* 563 */     double rSquared = 0.0D;
/* 564 */     if (sumMeanDiffSqr > 0.0D)
/* 565 */       rSquared = 1.0D - getSumResidualsSqr() / sumMeanDiffSqr;
/* 566 */     return rSquared;
/*     */   }
/*     */ 
/*     */   public double getFitGoodness()
/*     */   {
/* 571 */     double sumY = 0.0D;
/* 572 */     for (int i = 0; i < this.numPoints; i++) sumY += this.yData[i];
/* 573 */     double mean = sumY / this.numPoints;
/* 574 */     double sumMeanDiffSqr = 0.0D;
/* 575 */     int degreesOfFreedom = this.numPoints - getNumParams();
/* 576 */     double fitGoodness = 0.0D;
/* 577 */     for (int i = 0; i < this.numPoints; i++) {
/* 578 */       sumMeanDiffSqr += sqr(this.yData[i] - mean);
/*     */     }
/* 580 */     if ((sumMeanDiffSqr > 0.0D) && (degreesOfFreedom != 0)) {
/* 581 */       fitGoodness = 1.0D - getSumResidualsSqr() / degreesOfFreedom * (this.numPoints / sumMeanDiffSqr);
/*     */     }
/* 583 */     return fitGoodness;
/*     */   }
/*     */ 
/*     */   public String getResultString()
/*     */   {
/* 590 */     String results = "\nFormula: " + getFormula() + "\nTime: " + this.time + "ms" + "\nNumber of iterations: " + getIterations() + " (" + getMaxIterations() + ")" + "\nNumber of restarts: " + (this.nRestarts - 1) + " (" + defaultRestarts + ")" + "\nSum of residuals squared: " + IJ.d2s(getSumResidualsSqr(), 4) + "\nStandard deviation: " + IJ.d2s(getSD(), 4) + "\nR^2: " + IJ.d2s(getRSquared(), 4) + "\nParameters:";
/*     */ 
/* 598 */     char pChar = 'a';
/* 599 */     double[] pVal = getParams();
/* 600 */     for (int i = 0; i < this.numParams; i++) {
/* 601 */       results = results + "\n  " + pChar + " = " + IJ.d2s(pVal[i], 4);
/* 602 */       pChar = (char)(pChar + '\001');
/*     */     }
/* 604 */     return results;
/*     */   }
/*     */   double sqr(double d) {
/* 607 */     return d * d;
/*     */   }
/*     */ 
/*     */   void sumResiduals(double[] x) {
/* 611 */     x[this.numParams] = 0.0D;
/* 612 */     if (this.fit == 20)
/* 613 */       for (int i = 0; i < this.numPoints; i++)
/* 614 */         x[this.numParams] += sqr(f(x, this.xData[i]) - this.yData[i]);
/*     */     else
/* 616 */       for (int i = 0; i < this.numPoints; i++)
/* 617 */         x[this.numParams] += sqr(f(this.fit, x, this.xData[i]) - this.yData[i]);
/*     */   }
/*     */ 
/*     */   void newVertex()
/*     */   {
/* 623 */     for (int i = 0; i < this.numVertices; i++)
/* 624 */       this.simp[this.worst][i] = this.next[i];
/*     */   }
/*     */ 
/*     */   void order()
/*     */   {
/* 629 */     for (int i = 0; i < this.numVertices; i++) {
/* 630 */       if (this.simp[i][this.numParams] < this.simp[this.best][this.numParams]) this.best = i;
/* 631 */       if (this.simp[i][this.numParams] > this.simp[this.worst][this.numParams]) this.worst = i;
/*     */     }
/* 633 */     this.nextWorst = this.best;
/* 634 */     for (int i = 0; i < this.numVertices; i++)
/* 635 */       if ((i != this.worst) && 
/* 636 */         (this.simp[i][this.numParams] > this.simp[this.nextWorst][this.numParams])) this.nextWorst = i;
/*     */   }
/*     */ 
/*     */   public int getIterations()
/*     */   {
/* 644 */     return this.numIter;
/*     */   }
/*     */ 
/*     */   public int getMaxIterations()
/*     */   {
/* 649 */     return this.maxIter;
/*     */   }
/*     */ 
/*     */   public void setMaxIterations(int x)
/*     */   {
/* 654 */     this.maxIter = x;
/*     */   }
/*     */ 
/*     */   public int getRestarts()
/*     */   {
/* 659 */     return defaultRestarts;
/*     */   }
/*     */ 
/*     */   public void setRestarts(int n)
/*     */   {
/* 664 */     defaultRestarts = n;
/*     */   }
/*     */ 
/*     */   public void setInitialParameters(double[] params)
/*     */   {
/* 669 */     this.initialParams = params;
/*     */   }
/*     */ 
/*     */   public static int getMax(double[] array)
/*     */   {
/* 679 */     double max = array[0];
/* 680 */     int index = 0;
/* 681 */     for (int i = 1; i < array.length; i++) {
/* 682 */       if (max < array[i]) {
/* 683 */         max = array[i];
/* 684 */         index = i;
/*     */       }
/*     */     }
/* 687 */     return index;
/*     */   }
/*     */ 
/*     */   public double[] getXPoints() {
/* 691 */     return this.xData;
/*     */   }
/*     */ 
/*     */   public double[] getYPoints() {
/* 695 */     return this.yData;
/*     */   }
/*     */ 
/*     */   public int getFit() {
/* 699 */     return this.fit;
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 703 */     if (this.fit == 20) {
/* 704 */       return "User-defined";
/*     */     }
/* 706 */     return fitList[this.fit];
/*     */   }
/*     */ 
/*     */   public String getFormula() {
/* 710 */     if (this.fit == 20) {
/* 711 */       return this.customFormula;
/*     */     }
/* 713 */     return fList[this.fit];
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.measure.CurveFitter
 * JD-Core Version:    0.6.2
 */