/*     */ package ij.measure;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.Roi;
/*     */ import ij.io.SaveDialog;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ByteStatistics;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.text.TextPanel;
/*     */ import ij.text.TextWindow;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Frame;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class ResultsTable
/*     */   implements Cloneable
/*     */ {
/*     */   public static final int MAX_COLUMNS = 150;
/*     */   public static final int COLUMN_NOT_FOUND = -1;
/*     */   public static final int COLUMN_IN_USE = -2;
/*     */   public static final int TABLE_FULL = -3;
/*     */   public static final int AREA = 0;
/*     */   public static final int MEAN = 1;
/*     */   public static final int STD_DEV = 2;
/*     */   public static final int MODE = 3;
/*     */   public static final int MIN = 4;
/*     */   public static final int MAX = 5;
/*     */   public static final int X_CENTROID = 6;
/*     */   public static final int Y_CENTROID = 7;
/*     */   public static final int X_CENTER_OF_MASS = 8;
/*     */   public static final int Y_CENTER_OF_MASS = 9;
/*     */   public static final int PERIMETER = 10;
/*     */   public static final int ROI_X = 11;
/*     */   public static final int ROI_Y = 12;
/*     */   public static final int ROI_WIDTH = 13;
/*     */   public static final int ROI_HEIGHT = 14;
/*     */   public static final int MAJOR = 15;
/*     */   public static final int MINOR = 16;
/*     */   public static final int ANGLE = 17;
/*     */   public static final int CIRCULARITY = 18;
/*     */   public static final int FERET = 19;
/*     */   public static final int INTEGRATED_DENSITY = 20;
/*     */   public static final int MEDIAN = 21;
/*     */   public static final int SKEWNESS = 22;
/*     */   public static final int KURTOSIS = 23;
/*     */   public static final int AREA_FRACTION = 24;
/*     */   public static final int RAW_INTEGRATED_DENSITY = 25;
/*     */   public static final int CHANNEL = 26;
/*     */   public static final int SLICE = 27;
/*     */   public static final int FRAME = 28;
/*     */   public static final int FERET_X = 29;
/*     */   public static final int FERET_Y = 30;
/*     */   public static final int FERET_ANGLE = 31;
/*     */   public static final int MIN_FERET = 32;
/*     */   public static final int ASPECT_RATIO = 33;
/*     */   public static final int ROUNDNESS = 34;
/*     */   public static final int SOLIDITY = 35;
/*     */   public static final int LAST_HEADING = 35;
/*  37 */   private static final String[] defaultHeadings = { "Area", "Mean", "StdDev", "Mode", "Min", "Max", "X", "Y", "XM", "YM", "Perim.", "BX", "BY", "Width", "Height", "Major", "Minor", "Angle", "Circ.", "Feret", "IntDen", "Median", "Skew", "Kurt", "%Area", "RawIntDen", "Ch", "Slice", "Frame", "FeretX", "FeretY", "FeretAngle", "MinFeret", "AR", "Round", "Solidity" };
/*     */ 
/*  42 */   private int maxRows = 100;
/*  43 */   private int maxColumns = 150;
/*  44 */   private String[] headings = new String[this.maxColumns];
/*  45 */   private boolean[] keep = new boolean[this.maxColumns];
/*     */   private int counter;
/*  47 */   private double[][] columns = new double[this.maxColumns][];
/*     */   private String[] rowLabels;
/*  49 */   private int lastColumn = -1;
/*     */   private StringBuffer sb;
/*  51 */   private int precision = 3;
/*  52 */   private String rowLabelHeading = "";
/*  53 */   private char delimiter = '\t';
/*     */   private boolean headingSet;
/*  55 */   private boolean showRowNumbers = true;
/*     */   private static DecimalFormat[] df;
/*     */   private static DecimalFormat[] sf;
/*     */   private static DecimalFormatSymbols dfs;
/*     */ 
/*     */   public static ResultsTable getResultsTable()
/*     */   {
/*  64 */     return Analyzer.getResultsTable();
/*     */   }
/*     */ 
/*     */   public static TextWindow getResultsWindow()
/*     */   {
/*  69 */     Frame f = WindowManager.getFrame("Results");
/*  70 */     if ((f == null) || (!(f instanceof TextWindow))) {
/*  71 */       return null;
/*     */     }
/*  73 */     return (TextWindow)f;
/*     */   }
/*     */ 
/*     */   public synchronized void incrementCounter()
/*     */   {
/*  78 */     this.counter += 1;
/*  79 */     if (this.counter == this.maxRows) {
/*  80 */       if (this.rowLabels != null) {
/*  81 */         String[] s = new String[this.maxRows * 2];
/*  82 */         System.arraycopy(this.rowLabels, 0, s, 0, this.maxRows);
/*  83 */         this.rowLabels = s;
/*     */       }
/*  85 */       for (int i = 0; i <= this.lastColumn; i++) {
/*  86 */         if (this.columns[i] != null) {
/*  87 */           double[] tmp = new double[this.maxRows * 2];
/*  88 */           System.arraycopy(this.columns[i], 0, tmp, 0, this.maxRows);
/*  89 */           this.columns[i] = tmp;
/*     */         }
/*     */       }
/*  92 */       this.maxRows *= 2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void addColumns()
/*     */   {
/* 100 */     String[] tmp1 = new String[this.maxColumns * 2];
/* 101 */     System.arraycopy(this.headings, 0, tmp1, 0, this.maxColumns);
/* 102 */     this.headings = tmp1;
/* 103 */     double[][] tmp2 = new double[this.maxColumns * 2][];
/* 104 */     for (int i = 0; i < this.maxColumns; i++)
/* 105 */       tmp2[i] = this.columns[i];
/* 106 */     this.columns = tmp2;
/* 107 */     boolean[] tmp3 = new boolean[this.maxColumns * 2];
/* 108 */     System.arraycopy(this.keep, 0, tmp3, 0, this.maxColumns);
/* 109 */     this.keep = tmp3;
/* 110 */     this.maxColumns *= 2;
/*     */   }
/*     */ 
/*     */   public int getCounter()
/*     */   {
/* 115 */     return this.counter;
/*     */   }
/*     */ 
/*     */   public void addValue(int column, double value)
/*     */   {
/* 120 */     if (column >= this.maxColumns)
/* 121 */       addColumns();
/* 122 */     if ((column < 0) || (column >= this.maxColumns))
/* 123 */       throw new IllegalArgumentException("Column out of range");
/* 124 */     if (this.counter == 0)
/* 125 */       throw new IllegalArgumentException("Counter==0");
/* 126 */     if (this.columns[column] == null) {
/* 127 */       this.columns[column] = new double[this.maxRows];
/* 128 */       if (this.headings[column] == null)
/* 129 */         this.headings[column] = "---";
/* 130 */       if (column > this.lastColumn) this.lastColumn = column;
/*     */     }
/* 132 */     this.columns[column][(this.counter - 1)] = value;
/*     */   }
/*     */ 
/*     */   public void addValue(String column, double value)
/*     */   {
/* 141 */     if (column == null)
/* 142 */       throw new IllegalArgumentException("Column is null");
/* 143 */     int index = getColumnIndex(column);
/* 144 */     if (index == -1)
/* 145 */       index = getFreeColumn(column);
/* 146 */     addValue(index, value);
/* 147 */     this.keep[index] = true;
/*     */   }
/*     */ 
/*     */   public void addLabel(String label)
/*     */   {
/* 152 */     if (this.rowLabelHeading.equals(""))
/* 153 */       this.rowLabelHeading = "Label";
/* 154 */     addLabel(this.rowLabelHeading, label);
/*     */   }
/*     */ 
/*     */   public void addLabel(String columnHeading, String label)
/*     */   {
/* 159 */     if (this.counter == 0)
/* 160 */       throw new IllegalArgumentException("Counter==0");
/* 161 */     if (this.rowLabels == null)
/* 162 */       this.rowLabels = new String[this.maxRows];
/* 163 */     this.rowLabels[(this.counter - 1)] = label;
/* 164 */     if (columnHeading != null)
/* 165 */       this.rowLabelHeading = columnHeading;
/*     */   }
/*     */ 
/*     */   public void setLabel(String label, int row)
/*     */   {
/* 173 */     if ((row < 0) || (row >= this.counter))
/* 174 */       throw new IllegalArgumentException("row>=counter");
/* 175 */     if (this.rowLabels == null)
/* 176 */       this.rowLabels = new String[this.maxRows];
/* 177 */     if (this.rowLabelHeading.equals(""))
/* 178 */       this.rowLabelHeading = "Label";
/* 179 */     this.rowLabels[row] = label;
/*     */   }
/*     */ 
/*     */   public void disableRowLabels()
/*     */   {
/* 184 */     if (this.rowLabelHeading.equals("Label"))
/* 185 */       this.rowLabels = null;
/*     */   }
/*     */ 
/*     */   public float[] getColumn(int column)
/*     */   {
/* 191 */     if ((column < 0) || (column >= this.maxColumns))
/* 192 */       throw new IllegalArgumentException("Index out of range: " + column);
/* 193 */     if (this.columns[column] == null) {
/* 194 */       return null;
/*     */     }
/* 196 */     float[] data = new float[this.counter];
/* 197 */     for (int i = 0; i < this.counter; i++)
/* 198 */       data[i] = ((float)this.columns[column][i]);
/* 199 */     return data;
/*     */   }
/*     */ 
/*     */   public double[] getColumnAsDoubles(int column)
/*     */   {
/* 206 */     if ((column < 0) || (column >= this.maxColumns))
/* 207 */       throw new IllegalArgumentException("Index out of range: " + column);
/* 208 */     if (this.columns[column] == null) {
/* 209 */       return null;
/*     */     }
/* 211 */     double[] data = new double[this.counter];
/* 212 */     for (int i = 0; i < this.counter; i++)
/* 213 */       data[i] = this.columns[column][i];
/* 214 */     return data;
/*     */   }
/*     */ 
/*     */   public boolean columnExists(int column)
/*     */   {
/* 220 */     if ((column < 0) || (column >= this.maxColumns)) {
/* 221 */       return false;
/*     */     }
/* 223 */     return this.columns[column] != null;
/*     */   }
/*     */ 
/*     */   public int getColumnIndex(String heading)
/*     */   {
/* 229 */     for (int i = 0; i < this.headings.length; i++) {
/* 230 */       if (this.headings[i] == null)
/* 231 */         return -1;
/* 232 */       if (this.headings[i].equals(heading))
/* 233 */         return i;
/*     */     }
/* 235 */     return -1;
/*     */   }
/*     */ 
/*     */   public int getFreeColumn(String heading)
/*     */   {
/* 242 */     for (int i = 0; i < this.headings.length; i++) {
/* 243 */       if (this.headings[i] == null) {
/* 244 */         this.columns[i] = new double[this.maxRows];
/* 245 */         this.headings[i] = heading;
/* 246 */         if (i > this.lastColumn) this.lastColumn = i;
/* 247 */         return i;
/*     */       }
/* 249 */       if (this.headings[i].equals(heading))
/* 250 */         return -2;
/*     */     }
/* 252 */     addColumns();
/* 253 */     this.lastColumn += 1;
/* 254 */     this.columns[this.lastColumn] = new double[this.maxRows];
/* 255 */     this.headings[this.lastColumn] = heading;
/* 256 */     return this.lastColumn;
/*     */   }
/*     */ 
/*     */   public double getValueAsDouble(int column, int row)
/*     */   {
/* 264 */     if ((column >= this.maxColumns) || (row >= this.counter))
/* 265 */       throw new IllegalArgumentException("Index out of range: " + column + "," + row);
/* 266 */     if (this.columns[column] == null)
/* 267 */       throw new IllegalArgumentException("Column not defined: " + column);
/* 268 */     return this.columns[column][row];
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public float getValue(int column, int row)
/*     */   {
/* 276 */     return (float)getValueAsDouble(column, row);
/*     */   }
/*     */ 
/*     */   public double getValue(String column, int row)
/*     */   {
/* 285 */     if ((row < 0) || (row >= getCounter()))
/* 286 */       throw new IllegalArgumentException("Row out of range");
/* 287 */     int col = getColumnIndex(column);
/* 288 */     if (col == -1) {
/* 289 */       throw new IllegalArgumentException("\"" + column + "\" column not found");
/*     */     }
/* 291 */     return getValueAsDouble(col, row);
/*     */   }
/*     */ 
/*     */   public String getLabel(int row)
/*     */   {
/* 296 */     if ((row < 0) || (row >= getCounter()))
/* 297 */       throw new IllegalArgumentException("Row out of range");
/* 298 */     String label = null;
/* 299 */     if ((this.rowLabels != null) && (this.rowLabels[row] != null))
/* 300 */       label = this.rowLabels[row];
/* 301 */     return label;
/*     */   }
/*     */ 
/*     */   public void setValue(String column, int row, double value)
/*     */   {
/* 310 */     if (column == null)
/* 311 */       throw new IllegalArgumentException("Column is null");
/* 312 */     int col = getColumnIndex(column);
/* 313 */     if (col == -1) {
/* 314 */       col = getFreeColumn(column);
/*     */     }
/* 316 */     setValue(col, row, value);
/*     */   }
/*     */ 
/*     */   public void setValue(int column, int row, double value)
/*     */   {
/* 322 */     if (column >= this.maxColumns)
/* 323 */       addColumns();
/* 324 */     if ((column < 0) || (column >= this.maxColumns))
/* 325 */       throw new IllegalArgumentException("Column out of range");
/* 326 */     if (row >= this.counter)
/* 327 */       throw new IllegalArgumentException("row>=counter");
/* 328 */     if (this.columns[column] == null) {
/* 329 */       this.columns[column] = new double[this.maxRows];
/* 330 */       if (column > this.lastColumn) this.lastColumn = column;
/*     */     }
/* 332 */     this.columns[column][row] = value;
/*     */   }
/*     */ 
/*     */   public String getColumnHeadings()
/*     */   {
/* 337 */     if ((this.headingSet) && (!this.rowLabelHeading.equals(""))) {
/* 338 */       for (int i = 0; i <= this.lastColumn; i++)
/* 339 */         if ((this.columns[i] != null) && (this.rowLabelHeading.equals(this.headings[i]))) {
/* 340 */           this.headings[i] = null; this.columns[i] = null;
/*     */         }
/* 342 */       this.headingSet = false;
/*     */     }
/* 344 */     StringBuffer sb = new StringBuffer(200);
/* 345 */     if (this.showRowNumbers)
/* 346 */       sb.append(" " + this.delimiter);
/* 347 */     if (this.rowLabels != null) {
/* 348 */       sb.append(this.rowLabelHeading + this.delimiter);
/*     */     }
/* 350 */     for (int i = 0; i <= this.lastColumn; i++) {
/* 351 */       if (this.columns[i] != null) {
/* 352 */         String heading = this.headings[i];
/* 353 */         if (heading == null) heading = "---";
/* 354 */         sb.append(heading);
/* 355 */         if (i != this.lastColumn) sb.append(this.delimiter);
/*     */       }
/*     */     }
/* 358 */     return new String(sb);
/*     */   }
/*     */ 
/*     */   public String getColumnHeading(int column)
/*     */   {
/* 363 */     if ((column < 0) || (column >= this.maxColumns))
/* 364 */       throw new IllegalArgumentException("Index out of range: " + column);
/* 365 */     return this.headings[column];
/*     */   }
/*     */ 
/*     */   public String getRowAsString(int row)
/*     */   {
/* 371 */     if ((row < 0) || (row >= this.counter))
/* 372 */       throw new IllegalArgumentException("Row out of range: " + row);
/* 373 */     if (this.sb == null)
/* 374 */       this.sb = new StringBuffer(200);
/*     */     else
/* 376 */       this.sb.setLength(0);
/* 377 */     if (this.showRowNumbers) {
/* 378 */       this.sb.append(Integer.toString(row + 1));
/* 379 */       this.sb.append(this.delimiter);
/*     */     }
/* 381 */     if (this.rowLabels != null) {
/* 382 */       if (this.rowLabels[row] != null) {
/* 383 */         String label = this.rowLabels[row];
/* 384 */         if (this.delimiter == ',') label = label.replaceAll(",", ";");
/* 385 */         this.sb.append(label);
/*     */       }
/* 387 */       this.sb.append(this.delimiter);
/*     */     }
/* 389 */     for (int i = 0; i <= this.lastColumn; i++) {
/* 390 */       if (this.columns[i] != null) {
/* 391 */         this.sb.append(n(this.columns[i][row]));
/* 392 */         if (i != this.lastColumn) this.sb.append(this.delimiter);
/*     */       }
/*     */     }
/* 395 */     return new String(this.sb);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setHeading(int column, String heading)
/*     */   {
/* 403 */     if ((column < 0) || (column >= this.headings.length))
/* 404 */       throw new IllegalArgumentException("Column out of range: " + column);
/* 405 */     this.headings[column] = heading;
/* 406 */     if (this.columns[column] == null)
/* 407 */       this.columns[column] = new double[this.maxRows];
/* 408 */     if (column > this.lastColumn) this.lastColumn = column;
/* 409 */     this.headingSet = true;
/*     */   }
/*     */ 
/*     */   public void setDefaultHeadings()
/*     */   {
/* 414 */     for (int i = 0; i < defaultHeadings.length; i++)
/* 415 */       this.headings[i] = defaultHeadings[i];
/*     */   }
/*     */ 
/*     */   public void setPrecision(int precision)
/*     */   {
/* 420 */     this.precision = precision;
/*     */   }
/*     */ 
/*     */   public void showRowNumbers(boolean showNumbers) {
/* 424 */     this.showRowNumbers = showNumbers;
/*     */   }
/*     */ 
/*     */   String n(double n)
/*     */   {
/*     */     String s;
/*     */     String s;
/* 429 */     if ((Math.round(n) == n) && (this.precision >= 0))
/* 430 */       s = d2s(n, 0);
/*     */     else
/* 432 */       s = d2s(n, this.precision);
/* 433 */     return s;
/*     */   }
/*     */ 
/*     */   public static String d2s(double n, int decimalPlaces)
/*     */   {
/* 443 */     if ((Double.isNaN(n)) || (Double.isInfinite(n)))
/* 444 */       return "" + n;
/* 445 */     if (n == 3.402823466385289E+38D)
/* 446 */       return "3.4e38";
/* 447 */     double np = n;
/* 448 */     if (n < 0.0D) np = -n;
/* 449 */     if (df == null) {
/* 450 */       dfs = new DecimalFormatSymbols(Locale.US);
/* 451 */       df = new DecimalFormat[10];
/* 452 */       df[0] = new DecimalFormat("0", dfs);
/* 453 */       df[1] = new DecimalFormat("0.0", dfs);
/* 454 */       df[2] = new DecimalFormat("0.00", dfs);
/* 455 */       df[3] = new DecimalFormat("0.000", dfs);
/* 456 */       df[4] = new DecimalFormat("0.0000", dfs);
/* 457 */       df[5] = new DecimalFormat("0.00000", dfs);
/* 458 */       df[6] = new DecimalFormat("0.000000", dfs);
/* 459 */       df[7] = new DecimalFormat("0.0000000", dfs);
/* 460 */       df[8] = new DecimalFormat("0.00000000", dfs);
/* 461 */       df[9] = new DecimalFormat("0.000000000", dfs);
/*     */     }
/* 463 */     if (((np < 0.001D) && (np != 0.0D) && (np < 1.0D / Math.pow(10.0D, decimalPlaces))) || (np > 999999999999.0D) || (decimalPlaces < 0)) {
/* 464 */       if (decimalPlaces < 0) {
/* 465 */         decimalPlaces = -decimalPlaces;
/* 466 */         if (decimalPlaces > 9) decimalPlaces = 9; 
/*     */       }
/* 468 */       else { decimalPlaces = 3; }
/* 469 */       if (sf == null) {
/* 470 */         sf = new DecimalFormat[10];
/* 471 */         sf[1] = new DecimalFormat("0.0E0", dfs);
/* 472 */         sf[2] = new DecimalFormat("0.00E0", dfs);
/* 473 */         sf[3] = new DecimalFormat("0.000E0", dfs);
/* 474 */         sf[4] = new DecimalFormat("0.0000E0", dfs);
/* 475 */         sf[5] = new DecimalFormat("0.00000E0", dfs);
/* 476 */         sf[6] = new DecimalFormat("0.000000E0", dfs);
/* 477 */         sf[7] = new DecimalFormat("0.0000000E0", dfs);
/* 478 */         sf[8] = new DecimalFormat("0.00000000E0", dfs);
/* 479 */         sf[9] = new DecimalFormat("0.000000000E0", dfs);
/*     */       }
/* 481 */       return sf[decimalPlaces].format(n);
/*     */     }
/* 483 */     if (decimalPlaces < 0) decimalPlaces = 0;
/* 484 */     if (decimalPlaces > 9) decimalPlaces = 9;
/* 485 */     return df[decimalPlaces].format(n);
/*     */   }
/*     */ 
/*     */   public synchronized void deleteRow(int row)
/*     */   {
/* 490 */     if ((this.counter == 0) || (row < 0) || (row > this.counter - 1)) return;
/* 491 */     if (this.rowLabels != null) {
/* 492 */       for (int i = row; i < this.counter - 1; i++)
/* 493 */         this.rowLabels[i] = this.rowLabels[(i + 1)];
/*     */     }
/* 495 */     for (int i = 0; i <= this.lastColumn; i++) {
/* 496 */       if (this.columns[i] != null) {
/* 497 */         for (int j = row; j < this.counter - 1; j++)
/* 498 */           this.columns[i][j] = this.columns[i][(j + 1)];
/*     */       }
/*     */     }
/* 501 */     this.counter -= 1;
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 506 */     this.counter = 0;
/* 507 */     this.maxRows = 100;
/* 508 */     for (int i = 0; i < this.maxColumns; i++) {
/* 509 */       this.columns[i] = null;
/* 510 */       this.headings[i] = null;
/* 511 */       this.keep[i] = false;
/*     */     }
/* 513 */     this.lastColumn = -1;
/* 514 */     this.rowLabels = null;
/*     */   }
/*     */ 
/*     */   public int getLastColumn()
/*     */   {
/* 519 */     return this.lastColumn;
/*     */   }
/*     */ 
/*     */   public void addResults()
/*     */   {
/* 524 */     if (this.counter == 1)
/* 525 */       IJ.setColumnHeadings(getColumnHeadings());
/* 526 */     TextPanel textPanel = IJ.getTextPanel();
/* 527 */     String s = getRowAsString(this.counter - 1);
/* 528 */     if (textPanel != null)
/* 529 */       textPanel.appendWithoutUpdate(s);
/*     */     else
/* 531 */       System.out.println(s);
/*     */   }
/*     */ 
/*     */   public void updateResults()
/*     */   {
/* 536 */     TextPanel textPanel = IJ.getTextPanel();
/* 537 */     if (textPanel != null) {
/* 538 */       textPanel.updateColumnHeadings(getColumnHeadings());
/* 539 */       textPanel.updateDisplay();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void show(String windowTitle)
/*     */   {
/* 548 */     if ((!windowTitle.equals("Results")) && (this == Analyzer.getResultsTable()))
/* 549 */       IJ.log("ResultsTable.show(): the system ResultTable should only be displayed in the \"Results\" window.");
/* 550 */     String tableHeadings = getColumnHeadings();
/*     */ 
/* 552 */     boolean newWindow = false;
/*     */     TextPanel tp;
/* 553 */     if (windowTitle.equals("Results")) {
/* 554 */       TextPanel tp = IJ.getTextPanel();
/* 555 */       if (tp == null) return;
/* 556 */       newWindow = tp.getLineCount() == 0;
/* 557 */       IJ.setColumnHeadings(tableHeadings);
/* 558 */       if (this != Analyzer.getResultsTable())
/* 559 */         Analyzer.setResultsTable(this);
/* 560 */       if (getCounter() > 0)
/* 561 */         Analyzer.setUnsavedMeasurements(true);
/*     */     } else {
/* 563 */       Frame frame = WindowManager.getFrame(windowTitle);
/*     */       TextWindow win;
/*     */       TextWindow win;
/* 565 */       if ((frame != null) && ((frame instanceof TextWindow)))
/* 566 */         win = (TextWindow)frame;
/*     */       else
/* 568 */         win = new TextWindow(windowTitle, "", 400, 300);
/* 569 */       tp = win.getTextPanel();
/* 570 */       tp.setColumnHeadings(tableHeadings);
/* 571 */       newWindow = tp.getLineCount() == 0;
/*     */     }
/* 573 */     tp.setResultsTable(this);
/* 574 */     int n = getCounter();
/* 575 */     if (n > 0) {
/* 576 */       if (tp.getLineCount() > 0) tp.clear();
/* 577 */       StringBuffer sb = new StringBuffer(n * tableHeadings.length());
/* 578 */       for (int i = 0; i < n; i++)
/* 579 */         sb.append(getRowAsString(i) + "\n");
/* 580 */       tp.append(new String(sb));
/*     */     }
/* 582 */     if (newWindow) tp.scrollToTop(); 
/*     */   }
/*     */ 
/*     */   public void update(int measurements, ImagePlus imp, Roi roi)
/*     */   {
/* 586 */     if ((roi == null) && (imp != null)) roi = imp.getRoi();
/* 587 */     ResultsTable rt2 = new ResultsTable();
/* 588 */     Analyzer analyzer = new Analyzer(imp, measurements, rt2);
/* 589 */     ImageProcessor ip = new ByteProcessor(1, 1);
/* 590 */     ImageStatistics stats = new ByteStatistics(ip, measurements, null);
/* 591 */     analyzer.saveResults(stats, roi);
/*     */ 
/* 593 */     int last = rt2.getLastColumn();
/*     */ 
/* 595 */     while (last + 1 >= getMaxColumns()) {
/* 596 */       addColumns();
/*     */     }
/*     */ 
/* 599 */     if (last < getLastColumn()) {
/* 600 */       last = getLastColumn();
/* 601 */       if (last >= rt2.getMaxColumns())
/* 602 */         last = rt2.getMaxColumns() - 1;
/*     */     }
/* 604 */     for (int i = 0; i <= last; i++)
/*     */     {
/* 606 */       if ((rt2.getColumn(i) != null) && (this.columns[i] == null)) {
/* 607 */         this.columns[i] = new double[this.maxRows];
/* 608 */         this.headings[i] = rt2.getColumnHeading(i);
/* 609 */         if (i > this.lastColumn) this.lastColumn = i; 
/*     */       }
/* 610 */       else if ((rt2.getColumn(i) == null) && (this.columns[i] != null) && (this.keep[i] == 0)) {
/* 611 */         this.columns[i] = null;
/*     */       }
/*     */     }
/* 613 */     if (rt2.getRowLabels() == null) {
/* 614 */       this.rowLabels = null;
/* 615 */     } else if ((rt2.getRowLabels() != null) && (this.rowLabels == null)) {
/* 616 */       this.rowLabels = new String[this.maxRows];
/* 617 */       this.rowLabelHeading = "Label";
/*     */     }
/* 619 */     if (getCounter() > 0) show("Results"); 
/*     */   }
/*     */ 
/*     */   int getMaxColumns()
/*     */   {
/* 623 */     return this.maxColumns;
/*     */   }
/*     */ 
/*     */   String[] getRowLabels() {
/* 627 */     return this.rowLabels;
/*     */   }
/*     */ 
/*     */   public static ResultsTable open(String path)
/*     */     throws IOException
/*     */   {
/* 634 */     String lineSeparator = "\n";
/* 635 */     String cellSeparator = ",\t";
/* 636 */     String text = IJ.openAsString(path);
/* 637 */     if (text.startsWith("Error:"))
/* 638 */       throw new IOException("text.substring(7)");
/* 639 */     String[] lines = Tools.split(text, "\n");
/* 640 */     if (lines.length == 0)
/* 641 */       throw new IOException("Table is empty or invalid");
/* 642 */     String[] headings = Tools.split(lines[0], ",\t");
/* 643 */     if (headings.length == 1)
/* 644 */       throw new IOException("This is not a tab or comma delimited text file.");
/* 645 */     int numbersInHeadings = 0;
/* 646 */     for (int i = 0; i < headings.length; i++) {
/* 647 */       if ((headings[i].equals("NaN")) || (!Double.isNaN(Tools.parseDouble(headings[i]))))
/* 648 */         numbersInHeadings++;
/*     */     }
/* 650 */     boolean allNumericHeadings = numbersInHeadings == headings.length;
/* 651 */     if (allNumericHeadings) {
/* 652 */       for (int i = 0; i < headings.length; i++)
/* 653 */         headings[i] = ("C" + (i + 1));
/*     */     }
/* 655 */     int firstColumn = headings[0].equals(" ") ? 1 : 0;
/* 656 */     for (int i = 0; i < headings.length; i++)
/* 657 */       headings[i] = headings[i].trim();
/* 658 */     int firstRow = allNumericHeadings ? 0 : 1;
/* 659 */     boolean labels = (firstColumn == 1) && (headings[1].equals("Label"));
/* 660 */     int rtn = 0;
/* 661 */     if ((!labels) && ((rtn = openNonNumericTable(path, lines, firstRow, ",\t")) == 2))
/* 662 */       return null;
/* 663 */     if ((!labels) && (rtn == 1)) labels = true;
/* 664 */     if (lines[0].startsWith("\t")) {
/* 665 */       String[] headings2 = new String[headings.length + 1];
/* 666 */       headings2[0] = " ";
/* 667 */       for (int i = 0; i < headings.length; i++)
/* 668 */         headings2[(i + 1)] = headings[i];
/* 669 */       headings = headings2;
/* 670 */       firstColumn = 1;
/*     */     }
/* 672 */     ResultsTable rt = new ResultsTable();
/* 673 */     for (int i = firstRow; i < lines.length; i++) {
/* 674 */       rt.incrementCounter();
/* 675 */       String[] items = Tools.split(lines[i], ",\t");
/* 676 */       for (int j = firstColumn; j < items.length; j++) {
/* 677 */         if ((j == 1) && (labels))
/* 678 */           rt.addLabel(headings[1], items[1]);
/* 679 */         else if (j < headings.length)
/* 680 */           rt.addValue(headings[j], Tools.parseDouble(items[j]));
/*     */       }
/*     */     }
/* 683 */     return rt;
/*     */   }
/*     */ 
/*     */   static int openNonNumericTable(String path, String[] lines, int firstRow, String cellSeparator) {
/* 687 */     if (lines.length < 2) return 0;
/* 688 */     String[] items = Tools.split(lines[1], cellSeparator);
/* 689 */     int nonNumericCount = 0;
/* 690 */     int nonNumericIndex = 0;
/* 691 */     for (int i = 0; i < items.length; i++) {
/* 692 */       if ((!items[i].equals("NaN")) && (Double.isNaN(Tools.parseDouble(items[i])))) {
/* 693 */         nonNumericCount++;
/* 694 */         nonNumericIndex = i;
/*     */       }
/*     */     }
/* 697 */     boolean csv = path.endsWith(".csv");
/* 698 */     if (nonNumericCount == 0)
/* 699 */       return 0;
/* 700 */     if ((nonNumericCount == 1) && (nonNumericIndex == 1))
/* 701 */       return 1;
/* 702 */     if (csv) lines[0] = lines[0].replaceAll(",", "\t");
/* 703 */     StringBuffer sb = new StringBuffer();
/* 704 */     for (int i = 1; i < lines.length; i++) {
/* 705 */       sb.append(lines[i]);
/* 706 */       sb.append("\n");
/*     */     }
/* 708 */     String str = sb.toString();
/* 709 */     if (csv) str = str.replaceAll(",", "\t");
/* 710 */     new TextWindow(new File(path).getName(), lines[0], str, 500, 400);
/* 711 */     return 2;
/*     */   }
/*     */ 
/*     */   public void saveAs(String path)
/*     */     throws IOException
/*     */   {
/* 719 */     if ((getCounter() == 0) && (this.lastColumn < 0)) return;
/* 720 */     if ((path == null) || (path.equals(""))) {
/* 721 */       SaveDialog sd = new SaveDialog("Save Results", "Results", Prefs.get("options.ext", ".xls"));
/* 722 */       String file = sd.getFileName();
/* 723 */       if (file == null) return;
/* 724 */       path = sd.getDirectory() + file;
/*     */     }
/* 726 */     this.delimiter = (path.endsWith(".csv") ? ',' : '\t');
/* 727 */     PrintWriter pw = null;
/* 728 */     FileOutputStream fos = new FileOutputStream(path);
/* 729 */     BufferedOutputStream bos = new BufferedOutputStream(fos);
/* 730 */     pw = new PrintWriter(bos);
/* 731 */     boolean saveShowRowNumbers = this.showRowNumbers;
/* 732 */     if (Prefs.dontSaveRowNumbers)
/* 733 */       this.showRowNumbers = false;
/* 734 */     if (!Prefs.dontSaveHeaders) {
/* 735 */       String headings = getColumnHeadings();
/* 736 */       pw.println(headings);
/*     */     }
/* 738 */     for (int i = 0; i < getCounter(); i++)
/* 739 */       pw.println(getRowAsString(i));
/* 740 */     this.showRowNumbers = saveShowRowNumbers;
/* 741 */     pw.close();
/* 742 */     this.delimiter = '\t';
/*     */   }
/*     */ 
/*     */   public static String getDefaultHeading(int index) {
/* 746 */     if ((index >= 0) && (index < defaultHeadings.length)) {
/* 747 */       return defaultHeadings[index];
/*     */     }
/* 749 */     return "null";
/*     */   }
/*     */ 
/*     */   public synchronized Object clone()
/*     */   {
/*     */     try {
/* 755 */       ResultsTable rt2 = (ResultsTable)super.clone();
/* 756 */       rt2.headings = new String[this.headings.length];
/* 757 */       for (int i = 0; i <= this.lastColumn; i++)
/* 758 */         rt2.headings[i] = this.headings[i];
/* 759 */       rt2.columns = new double[this.columns.length][];
/* 760 */       for (int i = 0; i <= this.lastColumn; i++) {
/* 761 */         if (this.columns[i] != null) {
/* 762 */           double[] data = new double[this.maxRows];
/* 763 */           for (int j = 0; j < this.counter; j++)
/* 764 */             data[j] = this.columns[i][j];
/* 765 */           rt2.columns[i] = data;
/*     */         }
/*     */       }
/* 768 */       if (this.rowLabels != null) {
/* 769 */         rt2.rowLabels = new String[this.rowLabels.length];
/* 770 */         for (int i = 0; i < this.counter; i++)
/* 771 */           rt2.rowLabels[i] = this.rowLabels[i];
/*     */       }
/* 773 */       return rt2; } catch (CloneNotSupportedException e) {
/*     */     }
/* 775 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 779 */     return "ctr=" + this.counter + ", hdr=" + getColumnHeadings();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.measure.ResultsTable
 * JD-Core Version:    0.6.2
 */