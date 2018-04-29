/*      */ package ij.process;
/*      */ 
/*      */ import ij.plugin.filter.Convolver;
/*      */ import java.awt.Color;
/*      */ import java.awt.Image;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.MemoryImageSource;
/*      */ import java.util.Random;
/*      */ 
/*      */ public class FloatProcessor extends ImageProcessor
/*      */ {
/*      */   private float min;
/*      */   private float max;
/*      */   private float snapshotMin;
/*      */   private float snapshotMax;
/*      */   private float[] pixels;
/*      */   protected byte[] pixels8;
/*   14 */   private float[] snapshotPixels = null;
/*   15 */   private float fillColor = 3.4028235E+38F;
/*   16 */   private boolean fixedScale = false;
/*      */ 
/*      */   public FloatProcessor(int width, int height, float[] pixels, ColorModel cm)
/*      */   {
/*   21 */     if ((pixels != null) && (width * height != pixels.length))
/*   22 */       throw new IllegalArgumentException("width*height!=pixels.length");
/*   23 */     this.width = width;
/*   24 */     this.height = height;
/*   25 */     this.pixels = pixels;
/*   26 */     this.cm = cm;
/*   27 */     resetRoi();
/*      */   }
/*      */ 
/*      */   public FloatProcessor(int width, int height)
/*      */   {
/*   33 */     this(width, height, new float[width * height], null);
/*      */   }
/*      */ 
/*      */   public FloatProcessor(int width, int height, int[] pixels)
/*      */   {
/*   38 */     this(width, height);
/*   39 */     for (int i = 0; i < pixels.length; i++)
/*   40 */       this.pixels[i] = pixels[i];
/*      */   }
/*      */ 
/*      */   public FloatProcessor(int width, int height, double[] pixels)
/*      */   {
/*   45 */     this(width, height);
/*   46 */     for (int i = 0; i < pixels.length; i++)
/*   47 */       this.pixels[i] = ((float)pixels[i]);
/*      */   }
/*      */ 
/*      */   public FloatProcessor(float[][] array)
/*      */   {
/*   52 */     this.width = array.length;
/*   53 */     this.height = array[0].length;
/*   54 */     this.pixels = new float[this.width * this.height];
/*   55 */     int i = 0;
/*   56 */     for (int y = 0; y < this.height; y++) {
/*   57 */       for (int x = 0; x < this.width; x++) {
/*   58 */         this.pixels[(i++)] = array[x][y];
/*      */       }
/*      */     }
/*   61 */     resetRoi();
/*      */   }
/*      */ 
/*      */   public FloatProcessor(int[][] array)
/*      */   {
/*   66 */     this.width = array.length;
/*   67 */     this.height = array[0].length;
/*   68 */     this.pixels = new float[this.width * this.height];
/*   69 */     int i = 0;
/*   70 */     for (int y = 0; y < this.height; y++)
/*   71 */       for (int x = 0; x < this.width; x++)
/*   72 */         this.pixels[(i++)] = array[x][y];
/*      */   }
/*      */ 
/*      */   public void findMinAndMax()
/*      */   {
/*   88 */     if (this.fixedScale)
/*   89 */       return;
/*   90 */     this.min = 3.4028235E+38F;
/*   91 */     this.max = -3.402824E+38F;
/*   92 */     for (int i = 0; i < this.width * this.height; i++) {
/*   93 */       float value = this.pixels[i];
/*   94 */       if (!Float.isInfinite(value)) {
/*   95 */         if (value < this.min)
/*   96 */           this.min = value;
/*   97 */         if (value > this.max)
/*   98 */           this.max = value;
/*      */       }
/*      */     }
/*  101 */     this.minMaxSet = true;
/*  102 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void setMinAndMax(double minimum, double maximum)
/*      */   {
/*  112 */     if ((minimum == 0.0D) && (maximum == 0.0D)) {
/*  113 */       resetMinAndMax(); return;
/*  114 */     }this.min = ((float)minimum);
/*  115 */     this.max = ((float)maximum);
/*  116 */     this.fixedScale = true;
/*  117 */     this.minMaxSet = true;
/*  118 */     resetThreshold();
/*      */   }
/*      */ 
/*      */   public void resetMinAndMax()
/*      */   {
/*  125 */     this.fixedScale = false;
/*  126 */     findMinAndMax();
/*  127 */     resetThreshold();
/*      */   }
/*      */ 
/*      */   public double getMin()
/*      */   {
/*  132 */     if (!this.minMaxSet) findMinAndMax();
/*  133 */     return this.min;
/*      */   }
/*      */ 
/*      */   public double getMax()
/*      */   {
/*  138 */     if (!this.minMaxSet) findMinAndMax();
/*  139 */     return this.max;
/*      */   }
/*      */ 
/*      */   public Image createImage() {
/*  143 */     boolean firstTime = this.pixels8 == null;
/*  144 */     if ((firstTime) || (!this.lutAnimation))
/*  145 */       create8BitImage();
/*  146 */     if (this.cm == null)
/*  147 */       makeDefaultColorModel();
/*  148 */     if (this.source == null) {
/*  149 */       this.source = new MemoryImageSource(this.width, this.height, this.cm, this.pixels8, 0, this.width);
/*  150 */       this.source.setAnimated(true);
/*  151 */       this.source.setFullBufferUpdates(true);
/*  152 */       this.img = Toolkit.getDefaultToolkit().createImage(this.source);
/*  153 */     } else if (this.newPixels) {
/*  154 */       this.source.newPixels(this.pixels8, this.cm, 0, this.width);
/*  155 */       this.newPixels = false;
/*      */     } else {
/*  157 */       this.source.newPixels();
/*  158 */     }this.lutAnimation = false;
/*  159 */     return this.img;
/*      */   }
/*      */ 
/*      */   protected byte[] create8BitImage()
/*      */   {
/*  164 */     int size = this.width * this.height;
/*  165 */     if (this.pixels8 == null) {
/*  166 */       this.pixels8 = new byte[size];
/*      */     }
/*      */ 
/*  169 */     float min2 = (float)getMin(); float max2 = (float)getMax();
/*  170 */     float scale = 255.0F / (max2 - min2);
/*  171 */     for (int i = 0; i < size; i++) {
/*  172 */       float value = this.pixels[i] - min2;
/*  173 */       if (value < 0.0F) value = 0.0F;
/*  174 */       int ivalue = (int)(value * scale + 0.5F);
/*  175 */       if (ivalue > 255) ivalue = 255;
/*  176 */       this.pixels8[i] = ((byte)ivalue);
/*      */     }
/*  178 */     return this.pixels8;
/*      */   }
/*      */ 
/*      */   public BufferedImage getBufferedImage()
/*      */   {
/*  183 */     return convertToByte(true).getBufferedImage();
/*      */   }
/*      */ 
/*      */   public ImageProcessor createProcessor(int width, int height)
/*      */   {
/*  188 */     ImageProcessor ip2 = new FloatProcessor(width, height, new float[width * height], getColorModel());
/*  189 */     ip2.setMinAndMax(getMin(), getMax());
/*  190 */     ip2.setInterpolationMethod(this.interpolationMethod);
/*  191 */     return ip2;
/*      */   }
/*      */ 
/*      */   public void snapshot() {
/*  195 */     this.snapshotWidth = this.width;
/*  196 */     this.snapshotHeight = this.height;
/*  197 */     this.snapshotMin = ((float)getMin());
/*  198 */     this.snapshotMax = ((float)getMax());
/*  199 */     if ((this.snapshotPixels == null) || ((this.snapshotPixels != null) && (this.snapshotPixels.length != this.pixels.length)))
/*  200 */       this.snapshotPixels = new float[this.width * this.height];
/*  201 */     System.arraycopy(this.pixels, 0, this.snapshotPixels, 0, this.width * this.height);
/*      */   }
/*      */ 
/*      */   public void reset() {
/*  205 */     if (this.snapshotPixels == null)
/*  206 */       return;
/*  207 */     this.min = this.snapshotMin;
/*  208 */     this.max = this.snapshotMax;
/*  209 */     this.minMaxSet = true;
/*  210 */     System.arraycopy(this.snapshotPixels, 0, this.pixels, 0, this.width * this.height);
/*      */   }
/*      */ 
/*      */   public void reset(ImageProcessor mask) {
/*  214 */     if ((mask == null) || (this.snapshotPixels == null))
/*  215 */       return;
/*  216 */     if ((mask.getWidth() != this.roiWidth) || (mask.getHeight() != this.roiHeight))
/*  217 */       throw new IllegalArgumentException(maskSizeError(mask));
/*  218 */     byte[] mpixels = (byte[])mask.getPixels();
/*  219 */     int y = this.roiY; for (int my = 0; y < this.roiY + this.roiHeight; my++) {
/*  220 */       int i = y * this.width + this.roiX;
/*  221 */       int mi = my * this.roiWidth;
/*  222 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  223 */         if (mpixels[(mi++)] == 0)
/*  224 */           this.pixels[i] = this.snapshotPixels[i];
/*  225 */         i++;
/*      */       }
/*  219 */       y++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void swapPixelArrays()
/*      */   {
/*  232 */     if (this.snapshotPixels == null) return;
/*      */ 
/*  234 */     for (int i = 0; i < this.pixels.length; i++) {
/*  235 */       float pixel = this.pixels[i];
/*  236 */       this.pixels[i] = this.snapshotPixels[i];
/*  237 */       this.snapshotPixels[i] = pixel;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setSnapshotPixels(Object pixels) {
/*  242 */     this.snapshotPixels = ((float[])pixels);
/*  243 */     this.snapshotWidth = this.width;
/*  244 */     this.snapshotHeight = this.height;
/*      */   }
/*      */ 
/*      */   public Object getSnapshotPixels() {
/*  248 */     return this.snapshotPixels;
/*      */   }
/*      */ 
/*      */   public int getPixel(int x, int y)
/*      */   {
/*  254 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  255 */       return Float.floatToIntBits(this.pixels[(y * this.width + x)]);
/*      */     }
/*  257 */     return 0;
/*      */   }
/*      */ 
/*      */   public final int get(int x, int y) {
/*  261 */     return Float.floatToIntBits(this.pixels[(y * this.width + x)]);
/*      */   }
/*      */ 
/*      */   public final void set(int x, int y, int value) {
/*  265 */     this.pixels[(y * this.width + x)] = Float.intBitsToFloat(value);
/*      */   }
/*      */ 
/*      */   public final int get(int index) {
/*  269 */     return Float.floatToIntBits(this.pixels[index]);
/*      */   }
/*      */ 
/*      */   public final void set(int index, int value) {
/*  273 */     this.pixels[index] = Float.intBitsToFloat(value);
/*      */   }
/*      */ 
/*      */   public final float getf(int x, int y) {
/*  277 */     return this.pixels[(y * this.width + x)];
/*      */   }
/*      */ 
/*      */   public final void setf(int x, int y, float value) {
/*  281 */     this.pixels[(y * this.width + x)] = value;
/*      */   }
/*      */ 
/*      */   public final float getf(int index) {
/*  285 */     return this.pixels[index];
/*      */   }
/*      */ 
/*      */   public final void setf(int index, float value) {
/*  289 */     this.pixels[index] = value;
/*      */   }
/*      */ 
/*      */   public int[] getPixel(int x, int y, int[] iArray)
/*      */   {
/*  297 */     if (iArray == null) iArray = new int[1];
/*  298 */     iArray[0] = ((int)getPixelValue(x, y));
/*  299 */     return iArray;
/*      */   }
/*      */ 
/*      */   public final void putPixel(int x, int y, int[] iArray)
/*      */   {
/*  304 */     putPixelValue(x, y, iArray[0]);
/*      */   }
/*      */ 
/*      */   public double getInterpolatedPixel(double x, double y)
/*      */   {
/*  310 */     if (this.interpolationMethod == 2) {
/*  311 */       return getBicubicInterpolatedPixel(x, y, this);
/*      */     }
/*  313 */     if (x < 0.0D) x = 0.0D;
/*  314 */     if (x >= this.width - 1.0D) x = this.width - 1.001D;
/*  315 */     if (y < 0.0D) y = 0.0D;
/*  316 */     if (y >= this.height - 1.0D) y = this.height - 1.001D;
/*  317 */     return getInterpolatedPixel(x, y, this.pixels);
/*      */   }
/*      */ 
/*      */   public final int getPixelInterpolated(double x, double y)
/*      */   {
/*  322 */     if (this.interpolationMethod == 1) {
/*  323 */       if ((x < 0.0D) || (y < 0.0D) || (x >= this.width - 1) || (y >= this.height - 1)) {
/*  324 */         return 0;
/*      */       }
/*  326 */       return Float.floatToIntBits((float)getInterpolatedPixel(x, y, this.pixels));
/*  327 */     }if (this.interpolationMethod == 2) {
/*  328 */       return Float.floatToIntBits((float)getBicubicInterpolatedPixel(x, y, this));
/*      */     }
/*  330 */     return getPixel((int)(x + 0.5D), (int)(y + 0.5D));
/*      */   }
/*      */ 
/*      */   public final void putPixel(int x, int y, int value)
/*      */   {
/*  336 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height))
/*  337 */       this.pixels[(y * this.width + x)] = Float.intBitsToFloat(value);
/*      */   }
/*      */ 
/*      */   public void putPixelValue(int x, int y, double value)
/*      */   {
/*  342 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height))
/*  343 */       this.pixels[(y * this.width + x)] = ((float)value);
/*      */   }
/*      */ 
/*      */   public float getPixelValue(int x, int y)
/*      */   {
/*  348 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  349 */       return this.pixels[(y * this.width + x)];
/*      */     }
/*  351 */     return 0.0F;
/*      */   }
/*      */ 
/*      */   public void drawPixel(int x, int y)
/*      */   {
/*  356 */     if ((x >= this.clipXMin) && (x <= this.clipXMax) && (y >= this.clipYMin) && (y <= this.clipYMax))
/*  357 */       putPixel(x, y, Float.floatToIntBits(this.fillColor));
/*      */   }
/*      */ 
/*      */   public Object getPixels()
/*      */   {
/*  363 */     return this.pixels;
/*      */   }
/*      */ 
/*      */   public Object getPixelsCopy()
/*      */   {
/*  372 */     if ((this.snapshotCopyMode) && (this.snapshotPixels != null)) {
/*  373 */       this.snapshotCopyMode = false;
/*  374 */       return this.snapshotPixels;
/*      */     }
/*  376 */     float[] pixels2 = new float[this.width * this.height];
/*  377 */     System.arraycopy(this.pixels, 0, pixels2, 0, this.width * this.height);
/*  378 */     return pixels2;
/*      */   }
/*      */ 
/*      */   public void setPixels(Object pixels)
/*      */   {
/*  383 */     this.pixels = ((float[])pixels);
/*  384 */     resetPixels(pixels);
/*  385 */     if (pixels == null) this.snapshotPixels = null;
/*  386 */     if (pixels == null) this.pixels8 = null;
/*      */   }
/*      */ 
/*      */   public void copyBits(ImageProcessor ip, int xloc, int yloc, int mode)
/*      */   {
/*  392 */     ip = ip.convertToFloat();
/*  393 */     new FloatBlitter(this).copyBits(ip, xloc, yloc, mode);
/*      */   }
/*      */ 
/*      */   public void applyTable(int[] lut)
/*      */   {
/*      */   }
/*      */ 
/*      */   private void process(int op, double value) {
/*  401 */     float c = (float)value;
/*  402 */     float min2 = 0.0F; float max2 = 0.0F;
/*  403 */     if (op == 0) {
/*  404 */       min2 = (float)getMin(); max2 = (float)getMax();
/*  405 */     }for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  406 */       int i = y * this.width + this.roiX;
/*  407 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  408 */         float v1 = this.pixels[i];
/*      */         float v2;
/*      */         float v2;
/*      */         float v2;
/*      */         float v2;
/*      */         float v2;
/*      */         float v2;
/*  409 */         switch (op) {
/*      */         case 0:
/*  411 */           v2 = max2 - (v1 - min2);
/*  412 */           break;
/*      */         case 1:
/*  414 */           v2 = this.fillColor;
/*  415 */           break;
/*      */         case 2:
/*  417 */           v2 = v1 + c;
/*  418 */           break;
/*      */         case 3:
/*  420 */           v2 = v1 * c;
/*  421 */           break;
/*      */         case 7:
/*  423 */           if (v1 <= 0.0F)
/*  424 */             v2 = 0.0F;
/*      */           else
/*  426 */             v2 = (float)Math.exp(c * Math.log(v1));
/*  427 */           break;
/*      */         case 8:
/*  429 */           if (v1 <= 0.0F)
/*  430 */             v2 = 0.0F;
/*      */           else
/*  432 */             v2 = (float)Math.log(v1);
/*  433 */           break;
/*      */         case 13:
/*  435 */           v2 = (float)Math.exp(v1);
/*  436 */           break;
/*      */         case 11:
/*  438 */           v2 = v1 * v1;
/*  439 */           break;
/*      */         case 12:
/*  441 */           if (v1 <= 0.0F)
/*  442 */             v2 = 0.0F;
/*      */           else
/*  444 */             v2 = (float)Math.sqrt(v1);
/*  445 */           break;
/*      */         case 14:
/*  447 */           v2 = Math.abs(v1);
/*  448 */           break;
/*      */         case 9:
/*  450 */           if (v1 < value)
/*  451 */             v2 = (float)value;
/*      */           else
/*  453 */             v2 = v1;
/*  454 */           break;
/*      */         case 10:
/*  456 */           if (v1 > value)
/*  457 */             v2 = (float)value;
/*      */           else
/*  459 */             v2 = v1;
/*  460 */           break;
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         default:
/*  462 */           v2 = v1;
/*      */         }
/*  464 */         this.pixels[(i++)] = v2;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*  469 */   public void invert() { process(0, 0.0D); } 
/*  470 */   public void add(int value) { process(2, value); } 
/*  471 */   public void add(double value) { process(2, value); } 
/*  472 */   public void multiply(double value) { process(3, value); } 
/*      */   public void and(int value) {  } 
/*      */   public void or(int value) {  } 
/*      */   public void xor(int value) {  } 
/*  476 */   public void gamma(double value) { process(7, value); } 
/*  477 */   public void log() { process(8, 0.0D); } 
/*  478 */   public void exp() { process(13, 0.0D); } 
/*  479 */   public void sqr() { process(11, 0.0D); } 
/*  480 */   public void sqrt() { process(12, 0.0D); } 
/*  481 */   public void abs() { process(14, 0.0D); } 
/*  482 */   public void min(double value) { process(9, value); } 
/*  483 */   public void max(double value) { process(10, value); }
/*      */ 
/*      */   public void fill()
/*      */   {
/*  487 */     process(1, 0.0D);
/*      */   }
/*      */ 
/*      */   public void fill(ImageProcessor mask)
/*      */   {
/*  492 */     if (mask == null) {
/*  493 */       fill(); return;
/*  494 */     }int roiWidth = this.roiWidth; int roiHeight = this.roiHeight;
/*  495 */     int roiX = this.roiX; int roiY = this.roiY;
/*  496 */     if ((mask.getWidth() != roiWidth) || (mask.getHeight() != roiHeight))
/*  497 */       return;
/*  498 */     byte[] mpixels = (byte[])mask.getPixels();
/*  499 */     int y = roiY; for (int my = 0; y < roiY + roiHeight; my++) {
/*  500 */       int i = y * this.width + roiX;
/*  501 */       int mi = my * roiWidth;
/*  502 */       for (int x = roiX; x < roiX + roiWidth; x++) {
/*  503 */         if (mpixels[(mi++)] != 0)
/*  504 */           this.pixels[i] = this.fillColor;
/*  505 */         i++;
/*      */       }
/*  499 */       y++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void convolve3x3(int[] kernel)
/*      */   {
/*  512 */     filter3x3(5, kernel);
/*      */   }
/*      */ 
/*      */   public void filter(int type)
/*      */   {
/*  517 */     filter3x3(type, null);
/*      */   }
/*      */ 
/*      */   void filter3x3(int type, int[] kernel)
/*      */   {
/*  526 */     float k1 = 0.0F; float k2 = 0.0F; float k3 = 0.0F;
/*  527 */     float k4 = 0.0F; float k5 = 0.0F; float k6 = 0.0F;
/*  528 */     float k7 = 0.0F; float k8 = 0.0F; float k9 = 0.0F;
/*  529 */     float scale = 0.0F;
/*  530 */     if (type == 5) {
/*  531 */       k1 = kernel[0]; k2 = kernel[1]; k3 = kernel[2];
/*  532 */       k4 = kernel[3]; k5 = kernel[4]; k6 = kernel[5];
/*  533 */       k7 = kernel[6]; k8 = kernel[7]; k9 = kernel[8];
/*  534 */       for (int i = 0; i < kernel.length; i++)
/*  535 */         scale += kernel[i];
/*  536 */       if (scale == 0.0F) scale = 1.0F;
/*  537 */       scale = 1.0F / scale;
/*      */     }
/*  539 */     int inc = this.roiHeight / 25;
/*  540 */     if (inc < 1) inc = 1;
/*      */ 
/*  542 */     float[] pixels2 = (float[])getPixelsCopy();
/*      */ 
/*  544 */     int xEnd = this.roiX + this.roiWidth;
/*  545 */     int yEnd = this.roiY + this.roiHeight;
/*  546 */     for (int y = this.roiY; y < yEnd; y++) {
/*  547 */       int p = this.roiX + y * this.width;
/*  548 */       int p6 = p - (this.roiX > 0 ? 1 : 0);
/*  549 */       int p3 = p6 - (y > 0 ? this.width : 0);
/*  550 */       int p9 = p6 + (y < this.height - 1 ? this.width : 0);
/*  551 */       float v2 = pixels2[p3];
/*  552 */       float v5 = pixels2[p6];
/*  553 */       float v8 = pixels2[p9];
/*  554 */       if (this.roiX > 0) { p3++; p6++; p9++; }
/*  555 */       float v3 = pixels2[p3];
/*  556 */       float v6 = pixels2[p6];
/*  557 */       float v9 = pixels2[p9];
/*      */ 
/*  559 */       switch (type) {
/*      */       case 0:
/*  561 */         for (int x = this.roiX; x < xEnd; p++) {
/*  562 */           if (x < this.width - 1) { p3++; p6++; p9++; }
/*  563 */           float v1 = v2; v2 = v3;
/*  564 */           v3 = pixels2[p3];
/*  565 */           float v4 = v5; v5 = v6;
/*  566 */           v6 = pixels2[p6];
/*  567 */           float v7 = v8; v8 = v9;
/*  568 */           v9 = pixels2[p9];
/*  569 */           this.pixels[p] = ((v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8 + v9) * 0.1111111F);
/*      */ 
/*  561 */           x++;
/*      */         }
/*      */ 
/*  571 */         break;
/*      */       case 1:
/*  573 */         for (int x = this.roiX; x < xEnd; p++) {
/*  574 */           if (x < this.width - 1) { p3++; p6++; p9++; }
/*  575 */           float v1 = v2; v2 = v3;
/*  576 */           v3 = pixels2[p3];
/*  577 */           float v4 = v5; v5 = v6;
/*  578 */           v6 = pixels2[p6];
/*  579 */           float v7 = v8; v8 = v9;
/*  580 */           v9 = pixels2[p9];
/*  581 */           float sum1 = v1 + 2.0F * v2 + v3 - v7 - 2.0F * v8 - v9;
/*  582 */           float sum2 = v1 + 2.0F * v4 + v7 - v3 - 2.0F * v6 - v9;
/*  583 */           this.pixels[p] = ((float)Math.sqrt(sum1 * sum1 + sum2 * sum2));
/*      */ 
/*  573 */           x++;
/*      */         }
/*      */ 
/*  585 */         break;
/*      */       case 5:
/*  587 */         for (int x = this.roiX; x < xEnd; p++) {
/*  588 */           if (x < this.width - 1) { p3++; p6++; p9++; }
/*  589 */           float v1 = v2; v2 = v3;
/*  590 */           v3 = pixels2[p3];
/*  591 */           float v4 = v5; v5 = v6;
/*  592 */           v6 = pixels2[p6];
/*  593 */           float v7 = v8; v8 = v9;
/*  594 */           v9 = pixels2[p9];
/*  595 */           float sum = k1 * v1 + k2 * v2 + k3 * v3 + k4 * v4 + k5 * v5 + k6 * v6 + k7 * v7 + k8 * v8 + k9 * v9;
/*      */ 
/*  598 */           sum *= scale;
/*  599 */           this.pixels[p] = sum;
/*      */ 
/*  587 */           x++;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  603 */       if (y % inc == 0)
/*  604 */         showProgress((y - this.roiY) / this.roiHeight);
/*      */     }
/*  606 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void rotate(double angle)
/*      */   {
/*  613 */     float[] pixels2 = (float[])getPixelsCopy();
/*  614 */     ImageProcessor ip2 = null;
/*  615 */     if (this.interpolationMethod == 2)
/*  616 */       ip2 = new FloatProcessor(getWidth(), getHeight(), pixels2, null);
/*  617 */     double centerX = this.roiX + (this.roiWidth - 1) / 2.0D;
/*  618 */     double centerY = this.roiY + (this.roiHeight - 1) / 2.0D;
/*  619 */     int xMax = this.roiX + this.roiWidth - 1;
/*      */ 
/*  621 */     double angleRadians = -angle / 57.295779513082323D;
/*  622 */     double ca = Math.cos(angleRadians);
/*  623 */     double sa = Math.sin(angleRadians);
/*  624 */     double tmp1 = centerY * sa - centerX * ca;
/*  625 */     double tmp2 = -centerX * sa - centerY * ca;
/*      */ 
/*  629 */     if (this.interpolationMethod == 2) {
/*  630 */       for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  631 */         int index = y * this.width + this.roiX;
/*  632 */         double tmp3 = tmp1 - y * sa + centerX;
/*  633 */         double tmp4 = tmp2 + y * ca + centerY;
/*  634 */         for (int x = this.roiX; x <= xMax; x++) {
/*  635 */           double xs = x * ca + tmp3;
/*  636 */           double ys = x * sa + tmp4;
/*  637 */           this.pixels[(index++)] = ((float)getBicubicInterpolatedPixel(xs, ys, ip2));
/*      */         }
/*  639 */         if (y % 30 == 0) showProgress((y - this.roiY) / this.roiHeight); 
/*      */       }
/*      */     }
/*  642 */     else { double dwidth = this.width; double dheight = this.height;
/*  643 */       double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  644 */       double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*  645 */       for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  646 */         int index = y * this.width + this.roiX;
/*  647 */         double tmp3 = tmp1 - y * sa + centerX;
/*  648 */         double tmp4 = tmp2 + y * ca + centerY;
/*  649 */         for (int x = this.roiX; x <= xMax; x++) {
/*  650 */           double xs = x * ca + tmp3;
/*  651 */           double ys = x * sa + tmp4;
/*  652 */           if ((xs >= -0.01D) && (xs < dwidth) && (ys >= -0.01D) && (ys < dheight)) {
/*  653 */             if (this.interpolationMethod == 1) {
/*  654 */               if (xs < 0.0D) xs = 0.0D;
/*  655 */               if (xs >= xlimit) xs = xlimit2;
/*  656 */               if (ys < 0.0D) ys = 0.0D;
/*  657 */               if (ys >= ylimit) ys = ylimit2;
/*  658 */               this.pixels[(index++)] = ((float)getInterpolatedPixel(xs, ys, pixels2));
/*      */             } else {
/*  660 */               int ixs = (int)(xs + 0.5D);
/*  661 */               int iys = (int)(ys + 0.5D);
/*  662 */               if (ixs >= this.width) ixs = this.width - 1;
/*  663 */               if (iys >= this.height) iys = this.height - 1;
/*  664 */               this.pixels[(index++)] = pixels2[(this.width * iys + ixs)];
/*      */             }
/*      */           }
/*  667 */           else this.pixels[(index++)] = 0.0F;
/*      */         }
/*  669 */         if (y % 30 == 0)
/*  670 */           showProgress((y - this.roiY) / this.roiHeight);
/*      */       }
/*      */     }
/*  673 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void flipVertical()
/*      */   {
/*  679 */     for (int y = 0; y < this.roiHeight / 2; y++) {
/*  680 */       int index1 = (this.roiY + y) * this.width + this.roiX;
/*  681 */       int index2 = (this.roiY + this.roiHeight - 1 - y) * this.width + this.roiX;
/*  682 */       for (int i = 0; i < this.roiWidth; i++) {
/*  683 */         float tmp = this.pixels[index1];
/*  684 */         this.pixels[(index1++)] = this.pixels[index2];
/*  685 */         this.pixels[(index2++)] = tmp;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void noise(double range) {
/*  691 */     Random rnd = new Random();
/*  692 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  693 */       int i = y * this.width + this.roiX;
/*  694 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  695 */         float RandomBrightness = (float)(rnd.nextGaussian() * range);
/*  696 */         this.pixels[i] += RandomBrightness;
/*  697 */         i++;
/*      */       }
/*      */     }
/*  700 */     resetMinAndMax();
/*      */   }
/*      */ 
/*      */   public ImageProcessor crop() {
/*  704 */     ImageProcessor ip2 = createProcessor(this.roiWidth, this.roiHeight);
/*  705 */     float[] pixels2 = (float[])ip2.getPixels();
/*  706 */     for (int ys = this.roiY; ys < this.roiY + this.roiHeight; ys++) {
/*  707 */       int offset1 = (ys - this.roiY) * this.roiWidth;
/*  708 */       int offset2 = ys * this.width + this.roiX;
/*  709 */       for (int xs = 0; xs < this.roiWidth; xs++)
/*  710 */         pixels2[(offset1++)] = this.pixels[(offset2++)];
/*      */     }
/*  712 */     return ip2;
/*      */   }
/*      */ 
/*      */   public synchronized ImageProcessor duplicate()
/*      */   {
/*  717 */     ImageProcessor ip2 = createProcessor(this.width, this.height);
/*  718 */     float[] pixels2 = (float[])ip2.getPixels();
/*  719 */     System.arraycopy(this.pixels, 0, pixels2, 0, this.width * this.height);
/*  720 */     return ip2;
/*      */   }
/*      */ 
/*      */   public void scale(double xScale, double yScale)
/*      */   {
/*  727 */     double xCenter = this.roiX + this.roiWidth / 2.0D;
/*  728 */     double yCenter = this.roiY + this.roiHeight / 2.0D;
/*      */     int xmin;
/*      */     int xmax;
/*      */     int ymin;
/*      */     int ymax;
/*  731 */     if ((xScale > 1.0D) && (yScale > 1.0D))
/*      */     {
/*  733 */       int xmin = (int)(xCenter - (xCenter - this.roiX) * xScale);
/*  734 */       if (xmin < 0) xmin = 0;
/*  735 */       int xmax = xmin + (int)(this.roiWidth * xScale) - 1;
/*  736 */       if (xmax >= this.width) xmax = this.width - 1;
/*  737 */       int ymin = (int)(yCenter - (yCenter - this.roiY) * yScale);
/*  738 */       if (ymin < 0) ymin = 0;
/*  739 */       int ymax = ymin + (int)(this.roiHeight * yScale) - 1;
/*  740 */       if (ymax >= this.height) ymax = this.height - 1; 
/*      */     }
/*  742 */     else { xmin = this.roiX;
/*  743 */       xmax = this.roiX + this.roiWidth - 1;
/*  744 */       ymin = this.roiY;
/*  745 */       ymax = this.roiY + this.roiHeight - 1;
/*      */     }
/*  747 */     float[] pixels2 = (float[])getPixelsCopy();
/*  748 */     ImageProcessor ip2 = null;
/*  749 */     if (this.interpolationMethod == 2)
/*  750 */       ip2 = new FloatProcessor(getWidth(), getHeight(), pixels2, null);
/*  751 */     boolean checkCoordinates = (xScale < 1.0D) || (yScale < 1.0D);
/*      */ 
/*  754 */     if (this.interpolationMethod == 2) {
/*  755 */       for (int y = ymin; y <= ymax; y++) {
/*  756 */         double ys = (y - yCenter) / yScale + yCenter;
/*  757 */         int index1 = y * this.width + xmin;
/*  758 */         for (int x = xmin; x <= xmax; x++) {
/*  759 */           double xs = (x - xCenter) / xScale + xCenter;
/*  760 */           this.pixels[(index1++)] = ((float)getBicubicInterpolatedPixel(xs, ys, ip2));
/*      */         }
/*  762 */         if (y % 30 == 0) showProgress((y - ymin) / this.height); 
/*      */       }
/*      */     }
/*  765 */     else { double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  766 */       double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*  767 */       for (int y = ymin; y <= ymax; y++) {
/*  768 */         double ys = (y - yCenter) / yScale + yCenter;
/*  769 */         int ysi = (int)ys;
/*  770 */         if (ys < 0.0D) ys = 0.0D;
/*  771 */         if (ys >= ylimit) ys = ylimit2;
/*  772 */         int index1 = y * this.width + xmin;
/*  773 */         int index2 = this.width * (int)ys;
/*  774 */         for (int x = xmin; x <= xmax; x++) {
/*  775 */           double xs = (x - xCenter) / xScale + xCenter;
/*  776 */           int xsi = (int)xs;
/*  777 */           if ((checkCoordinates) && ((xsi < xmin) || (xsi > xmax) || (ysi < ymin) || (ysi > ymax))) {
/*  778 */             this.pixels[(index1++)] = ((float)getMin());
/*      */           }
/*  780 */           else if (this.interpolationMethod == 1) {
/*  781 */             if (xs < 0.0D) xs = 0.0D;
/*  782 */             if (xs >= xlimit) xs = xlimit2;
/*  783 */             this.pixels[(index1++)] = ((float)getInterpolatedPixel(xs, ys, pixels2));
/*      */           } else {
/*  785 */             this.pixels[(index1++)] = pixels2[(index2 + xsi)];
/*      */           }
/*      */         }
/*  788 */         if (y % 30 == 0) showProgress((y - ymin) / this.height);
/*      */       }
/*      */     }
/*  791 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   private final double getInterpolatedPixel(double x, double y, float[] pixels)
/*      */   {
/*  796 */     int xbase = (int)x;
/*  797 */     int ybase = (int)y;
/*  798 */     double xFraction = x - xbase;
/*  799 */     double yFraction = y - ybase;
/*  800 */     int offset = ybase * this.width + xbase;
/*  801 */     double lowerLeft = pixels[offset];
/*  802 */     double lowerRight = pixels[(offset + 1)];
/*  803 */     double upperRight = pixels[(offset + this.width + 1)];
/*  804 */     double upperLeft = pixels[(offset + this.width)];
/*  805 */     double upperAverage = upperLeft + xFraction * (upperRight - upperLeft);
/*  806 */     double lowerAverage = lowerLeft + xFraction * (lowerRight - lowerLeft);
/*  807 */     return lowerAverage + yFraction * (upperAverage - lowerAverage);
/*      */   }
/*      */ 
/*      */   public ImageProcessor resize(int dstWidth, int dstHeight)
/*      */   {
/*  812 */     double srcCenterX = this.roiX + this.roiWidth / 2.0D;
/*  813 */     double srcCenterY = this.roiY + this.roiHeight / 2.0D;
/*  814 */     double dstCenterX = dstWidth / 2.0D;
/*  815 */     double dstCenterY = dstHeight / 2.0D;
/*  816 */     double xScale = dstWidth / this.roiWidth;
/*  817 */     double yScale = dstHeight / this.roiHeight;
/*  818 */     if (this.interpolationMethod != 0) {
/*  819 */       dstCenterX += xScale / 2.0D;
/*  820 */       dstCenterY += yScale / 2.0D;
/*      */     }
/*  822 */     ImageProcessor ip2 = createProcessor(dstWidth, dstHeight);
/*  823 */     float[] pixels2 = (float[])ip2.getPixels();
/*      */ 
/*  825 */     if (this.interpolationMethod == 2) {
/*  826 */       for (int y = 0; y <= dstHeight - 1; y++) {
/*  827 */         double ys = (y - dstCenterY) / yScale + srcCenterY;
/*  828 */         int index = y * dstWidth;
/*  829 */         for (int x = 0; x <= dstWidth - 1; x++) {
/*  830 */           double xs = (x - dstCenterX) / xScale + srcCenterX;
/*  831 */           pixels2[(index++)] = ((float)getBicubicInterpolatedPixel(xs, ys, this));
/*      */         }
/*  833 */         if (y % 30 == 0) showProgress(y / dstHeight); 
/*      */       }
/*      */     }
/*  836 */     else { double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  837 */       double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*      */ 
/*  839 */       for (int y = 0; y <= dstHeight - 1; y++) {
/*  840 */         double ys = (y - dstCenterY) / yScale + srcCenterY;
/*  841 */         if (this.interpolationMethod == 1) {
/*  842 */           if (ys < 0.0D) ys = 0.0D;
/*  843 */           if (ys >= ylimit) ys = ylimit2;
/*      */         }
/*  845 */         int index1 = this.width * (int)ys;
/*  846 */         int index2 = y * dstWidth;
/*  847 */         for (int x = 0; x <= dstWidth - 1; x++) {
/*  848 */           double xs = (x - dstCenterX) / xScale + srcCenterX;
/*  849 */           if (this.interpolationMethod == 1) {
/*  850 */             if (xs < 0.0D) xs = 0.0D;
/*  851 */             if (xs >= xlimit) xs = xlimit2;
/*  852 */             pixels2[(index2++)] = ((float)getInterpolatedPixel(xs, ys, this.pixels));
/*      */           } else {
/*  854 */             pixels2[(index2++)] = this.pixels[(index1 + (int)xs)];
/*      */           }
/*      */         }
/*  856 */         if (y % 30 == 0) showProgress(y / dstHeight);
/*      */       }
/*      */     }
/*  859 */     showProgress(1.0D);
/*  860 */     return ip2;
/*      */   }
/*      */ 
/*      */   FloatProcessor downsize(int dstWidth, int dstHeight) {
/*  864 */     FloatProcessor ip2 = this;
/*  865 */     if (dstWidth < this.roiWidth) {
/*  866 */       ip2 = ip2.downsize1D(dstWidth, this.roiHeight, true);
/*  867 */       ip2.setRoi(0, 0, dstWidth, this.roiHeight);
/*      */     }
/*  869 */     if (dstHeight < this.roiHeight)
/*  870 */       ip2 = ip2.downsize1D(ip2.getRoi().width, dstHeight, false);
/*  871 */     if ((ip2.getWidth() != dstWidth) || (ip2.getHeight() != dstHeight))
/*  872 */       ip2 = (FloatProcessor)ip2.resize(dstWidth, dstHeight);
/*  873 */     return ip2;
/*      */   }
/*      */ 
/*      */   private FloatProcessor downsize1D(int dstWidth, int dstHeight, boolean xDirection)
/*      */   {
/*  878 */     int srcPointInc = xDirection ? 1 : this.width;
/*  879 */     int srcLineInc = xDirection ? this.width : 1;
/*  880 */     int dstPointInc = xDirection ? 1 : dstWidth;
/*  881 */     int dstLineInc = xDirection ? dstWidth : 1;
/*  882 */     int srcLine0 = xDirection ? this.roiY : this.roiX;
/*  883 */     int dstLines = xDirection ? dstHeight : dstWidth;
/*  884 */     DownsizeTable dt = xDirection ? new DownsizeTable(getWidth(), this.roiX, this.roiWidth, dstWidth, this.interpolationMethod) : new DownsizeTable(getHeight(), this.roiY, this.roiHeight, dstHeight, this.interpolationMethod);
/*      */ 
/*  887 */     FloatProcessor ip2 = (FloatProcessor)createProcessor(dstWidth, dstHeight);
/*  888 */     float[] pixels = (float[])getPixels();
/*  889 */     float[] pixels2 = (float[])ip2.getPixels();
/*  890 */     int srcLine = srcLine0; for (int dstLine = 0; dstLine < dstLines; dstLine++) {
/*  891 */       int dstLineOffset = dstLine * dstLineInc;
/*  892 */       int tablePointer = 0;
/*  893 */       int srcPoint = dt.srcStart; int p = srcPoint * srcPointInc + srcLine * srcLineInc;
/*  894 */       for (; srcPoint <= dt.srcEnd; p += srcPointInc) {
/*  895 */         float v = pixels[p];
/*  896 */         for (int i = 0; i < dt.kernelSize; tablePointer++) {
/*  897 */           pixels2[(dstLineOffset + dt.indices[tablePointer] * dstPointInc)] += v * dt.weights[tablePointer];
/*      */ 
/*  896 */           i++;
/*      */         }
/*  894 */         srcPoint++;
/*      */       }
/*  890 */       srcLine++;
/*      */     }
/*      */ 
/*  900 */     return ip2;
/*      */   }
/*      */ 
/*      */   public double getBicubicInterpolatedPixel(double x0, double y0, ImageProcessor ip2)
/*      */   {
/*  907 */     int u0 = (int)Math.floor(x0);
/*  908 */     int v0 = (int)Math.floor(y0);
/*  909 */     if ((u0 <= 0) || (u0 >= this.width - 2) || (v0 <= 0) || (v0 >= this.height - 2))
/*  910 */       return ip2.getBilinearInterpolatedPixel(x0, y0);
/*  911 */     double q = 0.0D;
/*  912 */     for (int j = 0; j <= 3; j++) {
/*  913 */       int v = v0 - 1 + j;
/*  914 */       double p = 0.0D;
/*  915 */       for (int i = 0; i <= 3; i++) {
/*  916 */         int u = u0 - 1 + i;
/*  917 */         p += ip2.getf(u, v) * cubic(x0 - u);
/*      */       }
/*  919 */       q += p * cubic(y0 - v);
/*      */     }
/*  921 */     return q;
/*      */   }
/*      */ 
/*      */   public void setColor(Color color)
/*      */   {
/*  926 */     this.drawingColor = color;
/*  927 */     int bestIndex = getBestIndex(color);
/*  928 */     if ((bestIndex > 0) && (getMin() == 0.0D) && (getMax() == 0.0D)) {
/*  929 */       this.fillColor = bestIndex;
/*  930 */       setMinAndMax(0.0D, 255.0D);
/*  931 */     } else if ((bestIndex == 0) && (getMin() > 0.0D) && ((color.getRGB() & 0xFFFFFF) == 0)) {
/*  932 */       this.fillColor = 0.0F;
/*      */     } else {
/*  934 */       this.fillColor = ((float)(getMin() + (getMax() - getMin()) * (bestIndex / 255.0D)));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setValue(double value) {
/*  939 */     this.fillColor = ((float)value);
/*      */   }
/*      */ 
/*      */   public void setBackgroundValue(double value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public double getBackgroundValue()
/*      */   {
/*  948 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public void setThreshold(double minThreshold, double maxThreshold, int lutUpdate) {
/*  952 */     if (minThreshold == -808080.0D) {
/*  953 */       resetThreshold(); return;
/*  954 */     }if (getMax() > getMin()) {
/*  955 */       double minT = Math.round((minThreshold - getMin()) / (getMax() - getMin()) * 255.0D);
/*  956 */       double maxT = Math.round((maxThreshold - getMin()) / (getMax() - getMin()) * 255.0D);
/*  957 */       super.setThreshold(minT, maxT, lutUpdate);
/*      */     } else {
/*  959 */       super.resetThreshold();
/*  960 */     }this.minThreshold = minThreshold;
/*  961 */     this.maxThreshold = maxThreshold;
/*      */   }
/*      */ 
/*      */   public void convolve(float[] kernel, int kernelWidth, int kernelHeight)
/*      */   {
/*  966 */     snapshot();
/*  967 */     new Convolver().convolve(this, kernel, kernelWidth, kernelHeight);
/*      */   }
/*      */   public void threshold(int level) {
/*      */   }
/*      */   public void autoThreshold() {
/*      */   }
/*      */   public void medianFilter() {
/*      */   }
/*      */ 
/*      */   public int[] getHistogram() {
/*  977 */     return null;
/*      */   }
/*      */ 
/*      */   public void erode()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void dilate()
/*      */   {
/*      */   }
/*      */ 
/*      */   public FloatProcessor toFloat(int channelNumber, FloatProcessor fp) {
/*  989 */     return this;
/*      */   }
/*      */ 
/*      */   public void setPixels(int channelNumber, FloatProcessor fp)
/*      */   {
/*  998 */     if (fp.getPixels() != getPixels())
/*  999 */       setPixels(fp.getPixels());
/* 1000 */     setMinAndMax(fp.getMin(), fp.getMax());
/*      */   }
/*      */ 
/*      */   public double minValue()
/*      */   {
/* 1005 */     return 1.401298464324817E-45D;
/*      */   }
/*      */ 
/*      */   public double maxValue()
/*      */   {
/* 1010 */     return 3.402823466385289E+38D;
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.FloatProcessor
 * JD-Core Version:    0.6.2
 */