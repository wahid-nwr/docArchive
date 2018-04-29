/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.gui.ColorChooser;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.SplineFitter;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Panel;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.awt.image.IndexColorModel;
/*     */ 
/*     */ class ColorPanel extends Panel
/*     */   implements MouseListener, MouseMotionListener
/*     */ {
/*     */   static final int entryWidth = 12;
/*     */   static final int entryHeight = 12;
/*  91 */   int rows = 16;
/*  92 */   int columns = 16;
/*  93 */   Color[] c = new Color[256];
/*     */   Color b;
/*     */   ColorProcessor cp;
/*     */   IndexColorModel origin;
/*     */   private ImagePlus imp;
/*  98 */   private int[] xSize = new int[256];
/*     */   private int[] redY;
/*     */   private int[] greenY;
/*     */   private int[] blueY;
/*     */   private int mapSize;
/*     */   private int x;
/*     */   private int y;
/*  99 */   private int initialC = -1; private int finalC = -1;
/*     */   private byte[] reds;
/*     */   private byte[] greens;
/*     */   private byte[] blues;
/*     */   private boolean updateLut;
/* 102 */   private static String[] choices = { "Replication", "Interpolation", "Spline Fitting" };
/* 103 */   private static String scaleMethod = choices[1];
/*     */   private int bitDepth;
/*     */ 
/*     */   ColorPanel(ImagePlus imp)
/*     */   {
/* 107 */     setup(imp);
/*     */   }
/*     */ 
/*     */   public void setup(ImagePlus imp) {
/* 111 */     if (imp == null) {
/* 112 */       IJ.noImage();
/* 113 */       return;
/*     */     }
/* 115 */     this.imp = imp;
/* 116 */     this.bitDepth = imp.getBitDepth();
/* 117 */     ImageProcessor ip = imp.getChannelProcessor();
/* 118 */     IndexColorModel cm = (IndexColorModel)ip.getColorModel();
/* 119 */     this.origin = cm;
/* 120 */     this.mapSize = cm.getMapSize();
/* 121 */     this.reds = new byte[256];
/* 122 */     this.greens = new byte[256];
/* 123 */     this.blues = new byte[256];
/* 124 */     cm.getReds(this.reds);
/* 125 */     cm.getGreens(this.greens);
/* 126 */     cm.getBlues(this.blues);
/* 127 */     addMouseListener(this);
/* 128 */     addMouseMotionListener(this);
/* 129 */     for (int index = 0; index < this.mapSize; index++)
/* 130 */       this.c[index] = new Color(this.reds[index] & 0xFF, this.greens[index] & 0xFF, this.blues[index] & 0xFF);
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize() {
/* 134 */     return new Dimension(this.columns * 12, this.rows * 12);
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize() {
/* 138 */     return new Dimension(this.columns * 12, this.rows * 12);
/*     */   }
/*     */ 
/*     */   int getMouseZone(int x, int y) {
/* 142 */     int horizontal = x / 12;
/* 143 */     int vertical = y / 12;
/* 144 */     int index = this.columns * vertical + horizontal;
/* 145 */     return index;
/*     */   }
/*     */ 
/*     */   public void colorRamp() {
/* 149 */     if (this.initialC > this.finalC) {
/* 150 */       int tmp = this.initialC;
/* 151 */       this.initialC = this.finalC;
/* 152 */       this.finalC = tmp;
/*     */     }
/* 154 */     float difference = this.finalC - this.initialC + 1;
/* 155 */     int start = (byte)this.c[this.initialC].getRed() & 0xFF;
/* 156 */     int end = (byte)this.c[this.finalC].getRed() & 0xFF;
/* 157 */     float rstep = (end - start) / difference;
/* 158 */     for (int index = this.initialC; index <= this.finalC; index++) {
/* 159 */       this.reds[index] = ((byte)(int)(start + (index - this.initialC) * rstep));
/*     */     }
/* 161 */     start = (byte)this.c[this.initialC].getGreen() & 0xFF;
/* 162 */     end = (byte)this.c[this.finalC].getGreen() & 0xFF;
/* 163 */     float gstep = (end - start) / difference;
/* 164 */     for (int index = this.initialC; index <= this.finalC; index++) {
/* 165 */       this.greens[index] = ((byte)(int)(start + (index - this.initialC) * gstep));
/*     */     }
/* 167 */     start = (byte)this.c[this.initialC].getBlue() & 0xFF;
/* 168 */     end = (byte)this.c[this.finalC].getBlue() & 0xFF;
/* 169 */     float bstep = (end - start) / difference;
/* 170 */     for (int index = this.initialC; index <= this.finalC; index++)
/* 171 */       this.blues[index] = ((byte)(int)(start + (index - this.initialC) * bstep));
/* 172 */     for (int index = this.initialC; index <= this.finalC; index++)
/* 173 */       this.c[index] = new Color(this.reds[index] & 0xFF, this.greens[index] & 0xFF, this.blues[index] & 0xFF);
/* 174 */     repaint();
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e) {
/* 178 */     this.x = e.getX();
/* 179 */     this.y = e.getY();
/* 180 */     this.initialC = getMouseZone(this.x, this.y);
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e) {
/* 184 */     this.x = e.getX();
/* 185 */     this.y = e.getY();
/* 186 */     this.finalC = getMouseZone(this.x, this.y);
/* 187 */     if ((this.initialC >= this.mapSize) && (this.finalC >= this.mapSize)) {
/* 188 */       this.initialC = (this.finalC = -1);
/* 189 */       return;
/*     */     }
/* 191 */     if (this.initialC >= this.mapSize)
/* 192 */       this.initialC = (this.mapSize - 1);
/* 193 */     if (this.finalC >= this.mapSize)
/* 194 */       this.finalC = (this.mapSize - 1);
/* 195 */     if (this.finalC < 0)
/* 196 */       this.finalC = 0;
/* 197 */     if (this.initialC == this.finalC) {
/* 198 */       this.b = this.c[this.finalC];
/* 199 */       ColorChooser cc = new ColorChooser("Color at Entry " + this.finalC, this.c[this.finalC], false);
/* 200 */       this.c[this.finalC] = cc.getColor();
/* 201 */       if (this.c[this.finalC] == null) {
/* 202 */         this.c[this.finalC] = this.b;
/*     */       }
/* 204 */       colorRamp();
/*     */     } else {
/* 206 */       this.b = this.c[this.initialC];
/* 207 */       ColorChooser icc = new ColorChooser("Initial Entry (" + this.initialC + ")", this.c[this.initialC], false);
/* 208 */       this.c[this.initialC] = icc.getColor();
/* 209 */       if (this.c[this.initialC] == null) {
/* 210 */         this.c[this.initialC] = this.b;
/* 211 */         this.initialC = (this.finalC = -1);
/* 212 */         return;
/*     */       }
/* 214 */       this.b = this.c[this.finalC];
/* 215 */       ColorChooser fcc = new ColorChooser("Final Entry (" + this.finalC + ")", this.c[this.finalC], false);
/* 216 */       this.c[this.finalC] = fcc.getColor();
/* 217 */       if (this.c[this.finalC] == null) {
/* 218 */         this.c[this.finalC] = this.b;
/* 219 */         this.initialC = (this.finalC = -1);
/* 220 */         return;
/*     */       }
/* 222 */       colorRamp();
/*     */     }
/* 224 */     this.initialC = (this.finalC = -1);
/* 225 */     applyLUT();
/*     */   }
/*     */   public void mouseClicked(MouseEvent e) {
/*     */   }
/*     */   public void mouseEntered(MouseEvent e) {
/*     */   }
/*     */   public void mouseExited(MouseEvent e) {
/*     */   }
/* 233 */   public void mouseDragged(MouseEvent e) { this.x = e.getX();
/* 234 */     this.y = e.getY();
/* 235 */     this.finalC = getMouseZone(this.x, this.y);
/* 236 */     IJ.showStatus("index=" + getIndex(this.finalC));
/* 237 */     repaint(); }
/*     */ 
/*     */   public void mouseMoved(MouseEvent e)
/*     */   {
/* 241 */     this.x = e.getX();
/* 242 */     this.y = e.getY();
/* 243 */     int entry = getMouseZone(this.x, this.y);
/* 244 */     if (entry < this.mapSize) {
/* 245 */       int red = this.reds[entry] & 0xFF;
/* 246 */       int green = this.greens[entry] & 0xFF;
/* 247 */       int blue = this.blues[entry] & 0xFF;
/* 248 */       IJ.showStatus("index=" + getIndex(entry) + ", color=" + red + "," + green + "," + blue);
/*     */     } else {
/* 250 */       IJ.showStatus("");
/*     */     }
/*     */   }
/*     */ 
/* 254 */   final String getIndex(int index) { if (this.bitDepth == 8)
/* 255 */       return "" + index;
/* 256 */     ImageProcessor ip = this.imp.getProcessor();
/* 257 */     double min = ip.getMin();
/* 258 */     double max = ip.getMax();
/* 259 */     Calibration cal = this.imp.getCalibration();
/* 260 */     min = cal.getCValue(min);
/* 261 */     max = cal.getCValue(max);
/* 262 */     double value = min + index / 255.0D * (max - min);
/* 263 */     int digits = max - min < 100.0D ? 2 : 0;
/* 264 */     return index + " (" + IJ.d2s(value, digits) + ")"; }
/*     */ 
/*     */   void open() {
/*     */     try {
/* 268 */       IJ.run("LUT... "); } catch (RuntimeException e) {
/*     */     }
/* 270 */     this.updateLut = true;
/* 271 */     repaint();
/*     */   }
/*     */ 
/*     */   void updateLut() {
/* 275 */     IndexColorModel cm = (IndexColorModel)this.imp.getChannelProcessor().getColorModel();
/* 276 */     if (this.mapSize == 0)
/* 277 */       return;
/* 278 */     cm.getReds(this.reds);
/* 279 */     cm.getGreens(this.greens);
/* 280 */     cm.getBlues(this.blues);
/* 281 */     for (int i = 0; i < this.mapSize; i++)
/* 282 */       this.c[i] = new Color(this.reds[i] & 0xFF, this.greens[i] & 0xFF, this.blues[i] & 0xFF);
/*     */   }
/*     */ 
/*     */   void invert() {
/* 286 */     byte[] reds2 = new byte[this.mapSize];
/* 287 */     byte[] greens2 = new byte[this.mapSize];
/* 288 */     byte[] blues2 = new byte[this.mapSize];
/* 289 */     for (int i = 0; i < this.mapSize; i++) {
/* 290 */       reds2[i] = ((byte)(this.reds[(this.mapSize - i - 1)] & 0xFF));
/* 291 */       greens2[i] = ((byte)(this.greens[(this.mapSize - i - 1)] & 0xFF));
/* 292 */       blues2[i] = ((byte)(this.blues[(this.mapSize - i - 1)] & 0xFF));
/*     */     }
/* 294 */     this.reds = reds2; this.greens = greens2; this.blues = blues2;
/* 295 */     for (int i = 0; i < this.mapSize; i++)
/* 296 */       this.c[i] = new Color(this.reds[i] & 0xFF, this.greens[i] & 0xFF, this.blues[i] & 0xFF);
/* 297 */     applyLUT();
/* 298 */     repaint();
/*     */   }
/*     */ 
/*     */   void resize() {
/* 302 */     GenericDialog sgd = new GenericDialog("LUT Editor");
/* 303 */     sgd.addNumericField("Number of Colors:", this.mapSize, 0);
/* 304 */     sgd.addChoice("Scale Using:", choices, scaleMethod);
/* 305 */     sgd.showDialog();
/* 306 */     if (sgd.wasCanceled()) {
/* 307 */       cancelLUT();
/* 308 */       return;
/*     */     }
/* 310 */     int newSize = (int)sgd.getNextNumber();
/* 311 */     if (newSize < 2) newSize = 2;
/* 312 */     if (newSize > 256) newSize = 256;
/* 313 */     scaleMethod = sgd.getNextChoice();
/* 314 */     scale(this.reds, this.greens, this.blues, newSize);
/* 315 */     this.mapSize = newSize;
/* 316 */     for (int i = 0; i < this.mapSize; i++)
/* 317 */       this.c[i] = new Color(this.reds[i] & 0xFF, this.greens[i] & 0xFF, this.blues[i] & 0xFF);
/* 318 */     applyLUT();
/* 319 */     repaint();
/*     */   }
/*     */ 
/*     */   void scale(byte[] reds, byte[] greens, byte[] blues, int newSize) {
/* 323 */     if (newSize == this.mapSize)
/* 324 */       return;
/* 325 */     if ((newSize < this.mapSize) || (scaleMethod.equals(choices[0])))
/* 326 */       scaleUsingReplication(reds, greens, blues, newSize);
/* 327 */     else if (scaleMethod.equals(choices[1]))
/* 328 */       scaleUsingInterpolation(reds, greens, blues, newSize);
/*     */     else
/* 330 */       scaleUsingSplineFitting(reds, greens, blues, newSize);
/*     */   }
/*     */ 
/*     */   void scaleUsingReplication(byte[] reds, byte[] greens, byte[] blues, int newSize) {
/* 334 */     byte[] reds2 = new byte[256];
/* 335 */     byte[] greens2 = new byte[256];
/* 336 */     byte[] blues2 = new byte[256];
/* 337 */     for (int i = 0; i < this.mapSize; i++) {
/* 338 */       reds2[i] = reds[i];
/* 339 */       greens2[i] = greens[i];
/* 340 */       blues2[i] = blues[i];
/*     */     }
/* 342 */     for (int i = 0; i < newSize; i++) {
/* 343 */       int index = (int)(i * (this.mapSize / newSize));
/* 344 */       reds[i] = reds2[index];
/* 345 */       greens[i] = greens2[index];
/* 346 */       blues[i] = blues2[index];
/*     */     }
/*     */   }
/*     */ 
/*     */   void scaleUsingInterpolation(byte[] reds, byte[] greens, byte[] blues, int newSize) {
/* 351 */     int[] r = new int[this.mapSize];
/* 352 */     int[] g = new int[this.mapSize];
/* 353 */     int[] b = new int[this.mapSize];
/* 354 */     for (int i = 0; i < this.mapSize; i++) {
/* 355 */       reds[i] &= 255;
/* 356 */       greens[i] &= 255;
/* 357 */       blues[i] &= 255;
/*     */     }
/* 359 */     double scale = (this.mapSize - 1) / (newSize - 1);
/*     */ 
/* 362 */     for (int i = 0; i < newSize; i++) {
/* 363 */       int i1 = (int)(i * scale);
/* 364 */       int i2 = i1 + 1;
/* 365 */       if (i2 == this.mapSize) i2 = this.mapSize - 1;
/* 366 */       double fraction = i * scale - i1;
/*     */ 
/* 368 */       reds[i] = ((byte)(int)((1.0D - fraction) * r[i1] + fraction * r[i2]));
/* 369 */       greens[i] = ((byte)(int)((1.0D - fraction) * g[i1] + fraction * g[i2]));
/* 370 */       blues[i] = ((byte)(int)((1.0D - fraction) * b[i1] + fraction * b[i2]));
/*     */     }
/*     */   }
/*     */ 
/*     */   void scaleUsingSplineFitting(byte[] reds, byte[] greens, byte[] blues, int newSize)
/*     */   {
/* 376 */     int[] reds2 = new int[this.mapSize];
/* 377 */     int[] greens2 = new int[this.mapSize];
/* 378 */     int[] blues2 = new int[this.mapSize];
/* 379 */     for (int i = 0; i < this.mapSize; i++) {
/* 380 */       reds[i] &= 255;
/* 381 */       greens[i] &= 255;
/* 382 */       blues[i] &= 255;
/*     */     }
/* 384 */     int[] xValues = new int[this.mapSize];
/* 385 */     for (int i = 0; i < this.mapSize; i++) {
/* 386 */       xValues[i] = ((int)(i * newSize / (this.mapSize - 1)));
/*     */     }
/*     */ 
/* 389 */     SplineFitter sfReds = new SplineFitter(xValues, reds2, this.mapSize);
/* 390 */     SplineFitter sfGreens = new SplineFitter(xValues, greens2, this.mapSize);
/* 391 */     SplineFitter sfBlues = new SplineFitter(xValues, blues2, this.mapSize);
/* 392 */     for (int i = 0; i < newSize; i++) {
/* 393 */       double v = Math.round(sfReds.evalSpline(xValues, reds2, this.mapSize, i));
/* 394 */       if (v < 0.0D) v = 0.0D; if (v > 255.0D) v = 255.0D; reds[i] = ((byte)(int)v);
/* 395 */       v = Math.round(sfGreens.evalSpline(xValues, greens2, this.mapSize, i));
/* 396 */       if (v < 0.0D) v = 0.0D; if (v > 255.0D) v = 255.0D; greens[i] = ((byte)(int)v);
/* 397 */       v = Math.round(sfBlues.evalSpline(xValues, blues2, this.mapSize, i));
/* 398 */       if (v < 0.0D) v = 0.0D; if (v > 255.0D) v = 255.0D; blues[i] = ((byte)(int)v);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cancelLUT() {
/* 403 */     if (this.mapSize == 0)
/* 404 */       return;
/* 405 */     this.origin.getReds(this.reds);
/* 406 */     this.origin.getGreens(this.greens);
/* 407 */     this.origin.getBlues(this.blues);
/* 408 */     this.mapSize = 256;
/* 409 */     applyLUT();
/*     */   }
/*     */ 
/*     */   public void applyLUT() {
/* 413 */     byte[] reds2 = this.reds; byte[] greens2 = this.greens; byte[] blues2 = this.blues;
/* 414 */     if (this.mapSize < 256) {
/* 415 */       reds2 = new byte[256];
/* 416 */       greens2 = new byte[256];
/* 417 */       blues2 = new byte[256];
/* 418 */       for (int i = 0; i < this.mapSize; i++) {
/* 419 */         reds2[i] = this.reds[i];
/* 420 */         greens2[i] = this.greens[i];
/* 421 */         blues2[i] = this.blues[i];
/*     */       }
/* 423 */       scale(reds2, greens2, blues2, 256);
/*     */     }
/* 425 */     IndexColorModel cm = new IndexColorModel(8, 256, reds2, greens2, blues2);
/* 426 */     ImageProcessor ip = this.imp.getChannelProcessor();
/* 427 */     ip.setColorModel(cm);
/* 428 */     if (this.imp.isComposite())
/* 429 */       ((CompositeImage)this.imp).setChannelColorModel(cm);
/* 430 */     if ((this.imp.getStackSize() > 1) && (!this.imp.isComposite()))
/* 431 */       this.imp.getStack().setColorModel(cm);
/* 432 */     this.imp.updateAndDraw();
/*     */   }
/*     */ 
/*     */   public void update(Graphics g) {
/* 436 */     paint(g);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g) {
/* 440 */     if (this.updateLut) {
/* 441 */       updateLut();
/* 442 */       this.updateLut = false;
/*     */     }
/* 444 */     int index = 0;
/* 445 */     for (int y = 0; y < this.rows; y++)
/* 446 */       for (int x = 0; x < this.columns; x++) {
/* 447 */         if (index >= this.mapSize) {
/* 448 */           g.setColor(Color.lightGray);
/* 449 */           g.fillRect(x * 12, y * 12, 12, 12);
/* 450 */         } else if (((index <= this.finalC) && (index >= this.initialC)) || ((index >= this.finalC) && (index <= this.initialC))) {
/* 451 */           g.setColor(this.c[index].brighter());
/* 452 */           g.fillRect(x * 12, y * 12, 12, 12);
/* 453 */           g.setColor(Color.white);
/* 454 */           g.drawRect(x * 12, y * 12, 12, 12);
/* 455 */           g.setColor(Color.black);
/* 456 */           g.drawLine(x * 12 + 12 - 1, y * 12, x * 12 + 12 - 1, y * 12 + 12);
/* 457 */           g.drawLine(x * 12, y * 12 + 12 - 1, x * 12 + 12 - 1, y * 12 + 12 - 1);
/* 458 */           g.setColor(Color.white);
/*     */         } else {
/* 460 */           g.setColor(this.c[index]);
/* 461 */           g.fillRect(x * 12, y * 12, 12, 12);
/* 462 */           g.setColor(Color.white);
/* 463 */           g.drawRect(x * 12, y * 12, 11, 11);
/* 464 */           g.setColor(Color.black);
/* 465 */           g.drawLine(x * 12, y * 12, x * 12 + 12 - 1, y * 12);
/* 466 */           g.drawLine(x * 12, y * 12, x * 12, y * 12 + 12 - 1);
/*     */         }
/* 468 */         index++;
/*     */       }
/*     */   }
/*     */ 
/*     */   int getMapSize()
/*     */   {
/* 474 */     return this.mapSize;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ColorPanel
 * JD-Core Version:    0.6.2
 */