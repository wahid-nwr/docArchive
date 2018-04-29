/*     */ package ij.measure;
/*     */ 
/*     */ import ij.ImagePlus;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ 
/*     */ public class Calibration
/*     */   implements Cloneable
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
/*     */   public static final int NONE = 20;
/*     */   public static final int UNCALIBRATED_OD = 21;
/*     */   public static final int CUSTOM = 22;
/*     */   public static final String DEFAULT_VALUE_UNIT = "Gray Value";
/*  15 */   public double pixelWidth = 1.0D;
/*     */ 
/*  18 */   public double pixelHeight = 1.0D;
/*     */ 
/*  21 */   public double pixelDepth = 1.0D;
/*     */   public double frameInterval;
/*     */   public double fps;
/*     */   private static boolean loopBackAndForth;
/*  31 */   public boolean loop = loopBackAndForth;
/*     */   public double xOrigin;
/*     */   public double yOrigin;
/*     */   public double zOrigin;
/*     */   public String info;
/*     */   private double[] coefficients;
/*  51 */   private String unit = "pixel";
/*     */   private String yunit;
/*     */   private String zunit;
/*     */   private String units;
/*  63 */   private String valueUnit = "Gray Value";
/*     */ 
/*  66 */   private String timeUnit = "sec";
/*     */ 
/*  69 */   private int function = 20;
/*     */   private float[] cTable;
/*     */   private boolean invertedLut;
/*  75 */   private int bitDepth = 8;
/*     */   private boolean zeroClip;
/*     */   private boolean invertY;
/*     */ 
/*     */   public Calibration(ImagePlus imp)
/*     */   {
/*  81 */     if (imp != null) {
/*  82 */       this.bitDepth = imp.getBitDepth();
/*  83 */       this.invertedLut = imp.isInvertedLut();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Calibration()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean scaled()
/*     */   {
/*  94 */     return (this.pixelWidth != 1.0D) || (this.pixelHeight != 1.0D) || (this.pixelDepth != 1.0D) || (!this.unit.equals("pixel"));
/*     */   }
/*     */ 
/*     */   public void setUnit(String unit)
/*     */   {
/*  99 */     if ((unit == null) || (unit.equals(""))) {
/* 100 */       this.unit = "pixel";
/*     */     } else {
/* 102 */       if (unit.equals("um")) unit = "µm";
/* 103 */       this.unit = unit;
/*     */     }
/* 105 */     this.units = null;
/*     */   }
/*     */ 
/*     */   public void setXUnit(String unit)
/*     */   {
/* 110 */     setUnit(unit);
/*     */   }
/*     */ 
/*     */   public void setYUnit(String unit)
/*     */   {
/* 115 */     this.yunit = unit;
/*     */   }
/*     */ 
/*     */   public void setZUnit(String unit)
/*     */   {
/* 120 */     this.zunit = unit;
/*     */   }
/*     */ 
/*     */   public String getUnit()
/*     */   {
/* 125 */     return this.unit;
/*     */   }
/*     */ 
/*     */   public String getXUnit()
/*     */   {
/* 130 */     return this.unit;
/*     */   }
/*     */ 
/*     */   public String getYUnit()
/*     */   {
/* 135 */     return this.yunit != null ? this.yunit : this.unit;
/*     */   }
/*     */ 
/*     */   public String getZUnit()
/*     */   {
/* 140 */     return this.zunit != null ? this.zunit : this.unit;
/*     */   }
/*     */ 
/*     */   public String getUnits()
/*     */   {
/* 145 */     if (this.units == null) {
/* 146 */       if (this.unit.equals("pixel"))
/* 147 */         this.units = "pixels";
/* 148 */       else if (this.unit.equals("micron"))
/* 149 */         this.units = "microns";
/* 150 */       else if (this.unit.equals("inch"))
/* 151 */         this.units = "inches";
/*     */       else
/* 153 */         this.units = this.unit;
/*     */     }
/* 155 */     return this.units;
/*     */   }
/*     */ 
/*     */   public void setTimeUnit(String unit)
/*     */   {
/* 160 */     if ((unit == null) || (unit.equals("")))
/* 161 */       this.timeUnit = "sec";
/*     */     else
/* 163 */       this.timeUnit = unit;
/*     */   }
/*     */ 
/*     */   public String getTimeUnit()
/*     */   {
/* 168 */     return this.timeUnit;
/*     */   }
/*     */ 
/*     */   public double getX(double x)
/*     */   {
/* 173 */     return (x - this.xOrigin) * this.pixelWidth;
/*     */   }
/*     */ 
/*     */   public double getY(double y)
/*     */   {
/* 178 */     return (y - this.yOrigin) * this.pixelHeight;
/*     */   }
/*     */ 
/*     */   public double getY(double y, int imageHeight)
/*     */   {
/* 184 */     if ((this.invertY) || ((Analyzer.getMeasurements() & 0x1000) != 0)) {
/* 185 */       if (this.yOrigin != 0.0D) {
/* 186 */         return (this.yOrigin - y) * this.pixelHeight;
/*     */       }
/* 188 */       return (imageHeight - y - 1.0D) * this.pixelHeight;
/*     */     }
/* 190 */     return (y - this.yOrigin) * this.pixelHeight;
/*     */   }
/*     */ 
/*     */   public double getZ(double z)
/*     */   {
/* 195 */     return (z - this.zOrigin) * this.pixelDepth;
/*     */   }
/*     */ 
/*     */   public double getRawX(double x)
/*     */   {
/* 200 */     return x / this.pixelWidth + this.xOrigin;
/*     */   }
/*     */ 
/*     */   public double getRawY(double y)
/*     */   {
/* 205 */     return y / this.pixelHeight + this.yOrigin;
/*     */   }
/*     */ 
/*     */   public double getRawY(double y, int imageHeight)
/*     */   {
/* 211 */     if ((this.invertY) || ((Analyzer.getMeasurements() & 0x1000) != 0)) {
/* 212 */       if (this.yOrigin != 0.0D) {
/* 213 */         return this.yOrigin - y / this.pixelHeight;
/*     */       }
/* 215 */       return imageHeight - y / this.pixelHeight - 1.0D;
/*     */     }
/* 217 */     return y / this.pixelHeight + this.yOrigin;
/*     */   }
/*     */ 
/*     */   public void setFunction(int function, double[] coefficients, String unit)
/*     */   {
/* 226 */     setFunction(function, coefficients, unit, false);
/*     */   }
/*     */ 
/*     */   public void setFunction(int function, double[] coefficients, String unit, boolean zeroClip) {
/* 230 */     if (function == 20) {
/* 231 */       disableDensityCalibration(); return;
/* 232 */     }if ((coefficients == null) && (function >= 0) && (function <= 10))
/* 233 */       return;
/* 234 */     this.function = function;
/* 235 */     this.coefficients = coefficients;
/* 236 */     this.zeroClip = zeroClip;
/* 237 */     if (unit != null)
/* 238 */       this.valueUnit = unit;
/* 239 */     this.cTable = null;
/*     */   }
/*     */ 
/*     */   public void setImage(ImagePlus imp)
/*     */   {
/* 244 */     if (imp == null)
/* 245 */       return;
/* 246 */     int type = imp.getType();
/* 247 */     int newBitDepth = imp.getBitDepth();
/* 248 */     if ((newBitDepth == 16) && (imp.getLocalCalibration().isSigned16Bit())) {
/* 249 */       double[] coeff = new double[2]; coeff[0] = -32768.0D; coeff[1] = 1.0D;
/* 250 */       setFunction(0, coeff, "Gray Value");
/* 251 */     } else if ((newBitDepth != this.bitDepth) || (type == 2) || (type == 4)) {
/* 252 */       String saveUnit = this.valueUnit;
/* 253 */       disableDensityCalibration();
/* 254 */       if (type == 2) this.valueUnit = saveUnit;
/*     */     }
/* 256 */     this.bitDepth = newBitDepth;
/*     */   }
/*     */ 
/*     */   public void disableDensityCalibration() {
/* 260 */     this.function = 20;
/* 261 */     this.coefficients = null;
/* 262 */     this.cTable = null;
/* 263 */     this.valueUnit = "Gray Value";
/*     */   }
/*     */ 
/*     */   public String getValueUnit()
/*     */   {
/* 268 */     return this.valueUnit;
/*     */   }
/*     */ 
/*     */   public void setValueUnit(String unit)
/*     */   {
/* 273 */     if (unit != null)
/* 274 */       this.valueUnit = unit;
/*     */   }
/*     */ 
/*     */   public double[] getCoefficients()
/*     */   {
/* 279 */     return this.coefficients;
/*     */   }
/*     */ 
/*     */   public boolean calibrated()
/*     */   {
/* 284 */     return this.function != 20;
/*     */   }
/*     */ 
/*     */   public int getFunction()
/*     */   {
/* 289 */     return this.function;
/*     */   }
/*     */ 
/*     */   public float[] getCTable()
/*     */   {
/* 296 */     if (this.cTable == null)
/* 297 */       makeCTable();
/* 298 */     return this.cTable;
/*     */   }
/*     */ 
/*     */   public void setCTable(float[] table, String unit)
/*     */   {
/* 304 */     if (table == null) {
/* 305 */       disableDensityCalibration(); return;
/* 306 */     }if ((this.bitDepth == 16) && (table.length != 65536))
/* 307 */       throw new IllegalArgumentException("Table.length!=65536");
/* 308 */     this.cTable = table;
/* 309 */     this.function = 22;
/* 310 */     this.coefficients = null;
/* 311 */     this.zeroClip = false;
/* 312 */     if (unit != null) this.valueUnit = unit; 
/*     */   }
/*     */ 
/*     */   void makeCTable()
/*     */   {
/* 316 */     if (this.bitDepth == 16) {
/* 317 */       make16BitCTable(); return;
/* 318 */     }if (this.bitDepth != 8)
/* 319 */       return;
/* 320 */     if (this.function == 21) {
/* 321 */       this.cTable = new float[256];
/* 322 */       for (int i = 0; i < 256; i++)
/* 323 */         this.cTable[i] = ((float)od(i));
/* 324 */     } else if ((this.function >= 0) && (this.function <= 10) && (this.coefficients != null)) {
/* 325 */       this.cTable = new float[256];
/*     */ 
/* 327 */       for (int i = 0; i < 256; i++) {
/* 328 */         double value = CurveFitter.f(this.function, this.coefficients, i);
/* 329 */         if ((this.zeroClip) && (value < 0.0D))
/* 330 */           this.cTable[i] = 0.0F;
/*     */         else
/* 332 */           this.cTable[i] = ((float)value);
/*     */       }
/*     */     } else {
/* 335 */       this.cTable = null;
/*     */     }
/*     */   }
/*     */ 
/* 339 */   void make16BitCTable() { if ((this.function >= 0) && (this.function <= 10) && (this.coefficients != null)) {
/* 340 */       this.cTable = new float[65536];
/* 341 */       for (int i = 0; i < 65536; i++)
/* 342 */         this.cTable[i] = ((float)CurveFitter.f(this.function, this.coefficients, i));
/*     */     } else {
/* 344 */       this.cTable = null;
/*     */     } }
/*     */ 
/*     */   double od(double v) {
/* 348 */     if (this.invertedLut) {
/* 349 */       if (v == 255.0D) v = 254.5D;
/* 350 */       return 0.434294481D * Math.log(255.0D / (255.0D - v));
/*     */     }
/* 352 */     if (v == 0.0D) v = 0.5D;
/* 353 */     return 0.434294481D * Math.log(255.0D / v);
/*     */   }
/*     */ 
/*     */   public double getCValue(int value)
/*     */   {
/* 359 */     if (this.function == 20)
/* 360 */       return value;
/* 361 */     if ((this.function >= 0) && (this.function <= 10) && (this.coefficients != null)) {
/* 362 */       double v = CurveFitter.f(this.function, this.coefficients, value);
/* 363 */       if ((this.zeroClip) && (v < 0.0D)) {
/* 364 */         return 0.0D;
/*     */       }
/* 366 */       return v;
/*     */     }
/* 368 */     if (this.cTable == null)
/* 369 */       makeCTable();
/* 370 */     if ((this.cTable != null) && (value >= 0) && (value < this.cTable.length)) {
/* 371 */       return this.cTable[value];
/*     */     }
/* 373 */     return value;
/*     */   }
/*     */ 
/*     */   public double getCValue(double value)
/*     */   {
/* 378 */     if (this.function == 20) {
/* 379 */       return value;
/*     */     }
/* 381 */     if ((this.function >= 0) && (this.function <= 10) && (this.coefficients != null)) {
/* 382 */       double v = CurveFitter.f(this.function, this.coefficients, value);
/* 383 */       if ((this.zeroClip) && (v < 0.0D)) {
/* 384 */         return 0.0D;
/*     */       }
/* 386 */       return v;
/*     */     }
/* 388 */     return getCValue((int)value);
/*     */   }
/*     */ 
/*     */   public double getRawValue(double value)
/*     */   {
/* 394 */     if (this.function == 20)
/* 395 */       return value;
/* 396 */     if ((this.function == 0) && (this.coefficients != null) && (this.coefficients.length == 2) && (this.coefficients[1] != 0.0D))
/* 397 */       return (value - this.coefficients[0]) / this.coefficients[1];
/* 398 */     if (this.cTable == null)
/* 399 */       makeCTable();
/* 400 */     float fvalue = (float)value;
/* 401 */     float smallestDiff = 3.4028235E+38F;
/*     */ 
/* 403 */     int index = 0;
/* 404 */     for (int i = 0; i < this.cTable.length; i++) {
/* 405 */       float diff = fvalue - this.cTable[i];
/* 406 */       if (diff < 0.0F) diff = -diff;
/* 407 */       if (diff < smallestDiff) {
/* 408 */         smallestDiff = diff;
/* 409 */         index = i;
/*     */       }
/*     */     }
/* 412 */     return index;
/*     */   }
/*     */ 
/*     */   public Calibration copy()
/*     */   {
/* 417 */     return (Calibration)clone();
/*     */   }
/*     */   public synchronized Object clone() {
/*     */     try {
/* 421 */       return super.clone(); } catch (CloneNotSupportedException e) {
/* 422 */     }return null;
/*     */   }
/*     */ 
/*     */   public boolean equals(Calibration cal)
/*     */   {
/* 427 */     if (cal == null)
/* 428 */       return false;
/* 429 */     boolean equal = true;
/* 430 */     if ((cal.pixelWidth != this.pixelWidth) || (cal.pixelHeight != this.pixelHeight) || (cal.pixelDepth != this.pixelDepth))
/* 431 */       equal = false;
/* 432 */     if (!cal.unit.equals(this.unit))
/* 433 */       equal = false;
/* 434 */     if ((!cal.valueUnit.equals(this.valueUnit)) || (cal.function != this.function))
/* 435 */       equal = false;
/* 436 */     return equal;
/*     */   }
/*     */ 
/*     */   public boolean isSigned16Bit()
/*     */   {
/* 441 */     return (this.bitDepth == 16) && (this.function >= 0) && (this.function <= 10) && (this.coefficients != null) && (this.coefficients[0] == -32768.0D) && (this.coefficients[1] == 1.0D);
/*     */   }
/*     */ 
/*     */   public void setSigned16BitCalibration()
/*     */   {
/* 447 */     double[] coeff = new double[2];
/* 448 */     coeff[0] = -32768.0D;
/* 449 */     coeff[1] = 1.0D;
/* 450 */     setFunction(0, coeff, "Gray Value");
/*     */   }
/*     */ 
/*     */   public boolean zeroClip()
/*     */   {
/* 455 */     return this.zeroClip;
/*     */   }
/*     */ 
/*     */   public void setInvertY(boolean invertYCoordinates)
/*     */   {
/* 460 */     this.invertY = invertYCoordinates;
/*     */   }
/*     */ 
/*     */   public static void setLoopBackAndForth(boolean loop)
/*     */   {
/* 465 */     loopBackAndForth = loop;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 469 */     return "w=" + this.pixelWidth + ", h=" + this.pixelHeight + ", d=" + this.pixelDepth + ", unit=" + this.unit + ", f=" + this.function + ", nc=" + (this.coefficients != null ? "" + this.coefficients.length : "null") + ", table=" + (this.cTable != null ? "" + this.cTable.length : "null") + ", vunit=" + this.valueUnit;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.measure.Calibration
 * JD-Core Version:    0.6.2
 */