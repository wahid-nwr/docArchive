/*      */ package ij.process;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.plugin.filter.Convolver;
/*      */ import java.awt.Color;
/*      */ import java.awt.Image;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.DataBuffer;
/*      */ import java.awt.image.DataBufferByte;
/*      */ import java.awt.image.DataBufferUShort;
/*      */ import java.awt.image.MemoryImageSource;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.util.Random;
/*      */ 
/*      */ public class ShortProcessor extends ImageProcessor
/*      */ {
/*      */   private int min;
/*      */   private int max;
/*      */   private int snapshotMin;
/*      */   private int snapshotMax;
/*      */   private short[] pixels;
/*      */   private byte[] pixels8;
/*      */   private short[] snapshotPixels;
/*      */   private byte[] LUT;
/*      */   private boolean fixedScale;
/*      */ 
/*      */   public ShortProcessor(int width, int height, short[] pixels, ColorModel cm)
/*      */   {
/*   23 */     if ((pixels != null) && (width * height != pixels.length))
/*   24 */       throw new IllegalArgumentException("width*height!=pixels.length");
/*   25 */     init(width, height, pixels, cm);
/*      */   }
/*      */ 
/*      */   public ShortProcessor(int width, int height)
/*      */   {
/*   31 */     this(width, height, new short[width * height], null);
/*      */   }
/*      */ 
/*      */   public ShortProcessor(BufferedImage bi)
/*      */   {
/*   36 */     if (bi.getType() != 11)
/*   37 */       throw new IllegalArgumentException("Type!=TYPE_USHORT_GRAY");
/*   38 */     WritableRaster raster = bi.getRaster();
/*   39 */     DataBuffer buffer = raster.getDataBuffer();
/*   40 */     short[] data = ((DataBufferUShort)buffer).getData();
/*      */ 
/*   43 */     init(raster.getWidth(), raster.getHeight(), data, null);
/*      */   }
/*      */ 
/*      */   void init(int width, int height, short[] pixels, ColorModel cm) {
/*   47 */     this.width = width;
/*   48 */     this.height = height;
/*   49 */     this.pixels = pixels;
/*   50 */     this.cm = cm;
/*   51 */     resetRoi();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public ShortProcessor(int width, int height, short[] pixels, ColorModel cm, boolean unsigned)
/*      */   {
/*   60 */     this(width, height, pixels, cm);
/*      */   }
/*      */ 
/*      */   public ShortProcessor(int width, int height, boolean unsigned)
/*      */   {
/*   66 */     this(width, height);
/*      */   }
/*      */ 
/*      */   public void findMinAndMax() {
/*   70 */     if ((this.fixedScale) || (this.pixels == null))
/*   71 */       return;
/*   72 */     int size = this.width * this.height;
/*      */ 
/*   74 */     this.min = 65535;
/*   75 */     this.max = 0;
/*   76 */     for (int i = 0; i < size; i++) {
/*   77 */       int value = this.pixels[i] & 0xFFFF;
/*   78 */       if (value < this.min)
/*   79 */         this.min = value;
/*   80 */       if (value > this.max)
/*   81 */         this.max = value;
/*      */     }
/*   83 */     this.minMaxSet = true;
/*      */   }
/*      */ 
/*      */   public Image createImage()
/*      */   {
/*   88 */     boolean firstTime = this.pixels8 == null;
/*   89 */     if ((firstTime) || (!this.lutAnimation))
/*   90 */       create8BitImage();
/*   91 */     if (this.cm == null)
/*   92 */       makeDefaultColorModel();
/*   93 */     if (IJ.isJava16())
/*   94 */       return createBufferedImage();
/*   95 */     if (this.source == null) {
/*   96 */       this.source = new MemoryImageSource(this.width, this.height, this.cm, this.pixels8, 0, this.width);
/*   97 */       this.source.setAnimated(true);
/*   98 */       this.source.setFullBufferUpdates(true);
/*   99 */       this.img = Toolkit.getDefaultToolkit().createImage(this.source);
/*  100 */     } else if (this.newPixels) {
/*  101 */       this.source.newPixels(this.pixels8, this.cm, 0, this.width);
/*  102 */       this.newPixels = false;
/*      */     } else {
/*  104 */       this.source.newPixels();
/*  105 */     }this.lutAnimation = false;
/*  106 */     return this.img;
/*      */   }
/*      */ 
/*      */   byte[] create8BitImage()
/*      */   {
/*  111 */     int size = this.width * this.height;
/*  112 */     if (this.pixels8 == null) {
/*  113 */       this.pixels8 = new byte[size];
/*      */     }
/*  115 */     int min2 = (int)getMin(); int max2 = (int)getMax();
/*  116 */     double scale = 256.0D / (max2 - min2 + 1);
/*  117 */     for (int i = 0; i < size; i++) {
/*  118 */       int value = (this.pixels[i] & 0xFFFF) - min2;
/*  119 */       if (value < 0) value = 0;
/*  120 */       value = (int)(value * scale + 0.5D);
/*  121 */       if (value > 255) value = 255;
/*  122 */       this.pixels8[i] = ((byte)value);
/*      */     }
/*  124 */     return this.pixels8;
/*      */   }
/*      */ 
/*      */   Image createBufferedImage() {
/*  128 */     if (this.raster == null) {
/*  129 */       SampleModel sm = getIndexSampleModel();
/*  130 */       DataBuffer db = new DataBufferByte(this.pixels8, this.width * this.height, 0);
/*  131 */       this.raster = Raster.createWritableRaster(sm, db, null);
/*      */     }
/*  133 */     if ((this.image == null) || (this.cm != this.cm2)) {
/*  134 */       if (this.cm == null) this.cm = getDefaultColorModel();
/*  135 */       this.image = new BufferedImage(this.cm, this.raster, false, null);
/*  136 */       this.cm2 = this.cm;
/*      */     }
/*  138 */     this.lutAnimation = false;
/*  139 */     return this.image;
/*      */   }
/*      */ 
/*      */   public BufferedImage getBufferedImage()
/*      */   {
/*  144 */     return convertToByte(true).getBufferedImage();
/*      */   }
/*      */ 
/*      */   public BufferedImage get16BitBufferedImage()
/*      */   {
/*  149 */     BufferedImage bi = new BufferedImage(this.width, this.height, 11);
/*  150 */     Raster raster = bi.getData();
/*  151 */     DataBufferUShort db = (DataBufferUShort)raster.getDataBuffer();
/*  152 */     System.arraycopy(getPixels(), 0, db.getData(), 0, db.getData().length);
/*  153 */     bi.setData(raster);
/*  154 */     return bi;
/*      */   }
/*      */ 
/*      */   public ImageProcessor createProcessor(int width, int height)
/*      */   {
/*  159 */     ImageProcessor ip2 = new ShortProcessor(width, height, new short[width * height], getColorModel());
/*  160 */     ip2.setMinAndMax(getMin(), getMax());
/*  161 */     ip2.setInterpolationMethod(this.interpolationMethod);
/*  162 */     return ip2;
/*      */   }
/*      */ 
/*      */   public void snapshot() {
/*  166 */     this.snapshotWidth = this.width;
/*  167 */     this.snapshotHeight = this.height;
/*  168 */     this.snapshotMin = ((int)getMin());
/*  169 */     this.snapshotMax = ((int)getMax());
/*  170 */     if ((this.snapshotPixels == null) || ((this.snapshotPixels != null) && (this.snapshotPixels.length != this.pixels.length)))
/*  171 */       this.snapshotPixels = new short[this.width * this.height];
/*  172 */     System.arraycopy(this.pixels, 0, this.snapshotPixels, 0, this.width * this.height);
/*      */   }
/*      */ 
/*      */   public void reset() {
/*  176 */     if (this.snapshotPixels == null)
/*  177 */       return;
/*  178 */     this.min = this.snapshotMin;
/*  179 */     this.max = this.snapshotMax;
/*  180 */     this.minMaxSet = true;
/*  181 */     System.arraycopy(this.snapshotPixels, 0, this.pixels, 0, this.width * this.height);
/*      */   }
/*      */ 
/*      */   public void reset(ImageProcessor mask) {
/*  185 */     if ((mask == null) || (this.snapshotPixels == null))
/*  186 */       return;
/*  187 */     if ((mask.getWidth() != this.roiWidth) || (mask.getHeight() != this.roiHeight))
/*  188 */       throw new IllegalArgumentException(maskSizeError(mask));
/*  189 */     byte[] mpixels = (byte[])mask.getPixels();
/*  190 */     int y = this.roiY; for (int my = 0; y < this.roiY + this.roiHeight; my++) {
/*  191 */       int i = y * this.width + this.roiX;
/*  192 */       int mi = my * this.roiWidth;
/*  193 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  194 */         if (mpixels[(mi++)] == 0)
/*  195 */           this.pixels[i] = this.snapshotPixels[i];
/*  196 */         i++;
/*      */       }
/*  190 */       y++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void swapPixelArrays()
/*      */   {
/*  203 */     if (this.snapshotPixels == null) return;
/*      */ 
/*  205 */     for (int i = 0; i < this.pixels.length; i++) {
/*  206 */       short pixel = this.pixels[i];
/*  207 */       this.pixels[i] = this.snapshotPixels[i];
/*  208 */       this.snapshotPixels[i] = pixel;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setSnapshotPixels(Object pixels) {
/*  213 */     this.snapshotPixels = ((short[])pixels);
/*  214 */     this.snapshotWidth = this.width;
/*  215 */     this.snapshotHeight = this.height;
/*      */   }
/*      */ 
/*      */   public Object getSnapshotPixels() {
/*  219 */     return this.snapshotPixels;
/*      */   }
/*      */ 
/*      */   public double getMin()
/*      */   {
/*  229 */     if (!this.minMaxSet) findMinAndMax();
/*  230 */     return this.min;
/*      */   }
/*      */ 
/*      */   public double getMax()
/*      */   {
/*  235 */     if (!this.minMaxSet) findMinAndMax();
/*  236 */     return this.max;
/*      */   }
/*      */ 
/*      */   public void setMinAndMax(double minimum, double maximum)
/*      */   {
/*  246 */     if ((minimum == 0.0D) && (maximum == 0.0D)) {
/*  247 */       resetMinAndMax(); return;
/*  248 */     }if (minimum < 0.0D)
/*  249 */       minimum = 0.0D;
/*  250 */     if (maximum > 65535.0D)
/*  251 */       maximum = 65535.0D;
/*  252 */     this.min = ((int)minimum);
/*  253 */     this.max = ((int)maximum);
/*  254 */     this.fixedScale = true;
/*  255 */     this.minMaxSet = true;
/*  256 */     resetThreshold();
/*      */   }
/*      */ 
/*      */   public void resetMinAndMax()
/*      */   {
/*  263 */     this.fixedScale = false;
/*  264 */     findMinAndMax();
/*  265 */     resetThreshold();
/*      */   }
/*      */ 
/*      */   public int getPixel(int x, int y) {
/*  269 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  270 */       return this.pixels[(y * this.width + x)] & 0xFFFF;
/*      */     }
/*  272 */     return 0;
/*      */   }
/*      */ 
/*      */   public final int get(int x, int y) {
/*  276 */     return this.pixels[(y * this.width + x)] & 0xFFFF;
/*      */   }
/*      */ 
/*      */   public final void set(int x, int y, int value) {
/*  280 */     this.pixels[(y * this.width + x)] = ((short)value);
/*      */   }
/*      */ 
/*      */   public final int get(int index) {
/*  284 */     return this.pixels[index] & 0xFFFF;
/*      */   }
/*      */ 
/*      */   public final void set(int index, int value) {
/*  288 */     this.pixels[index] = ((short)value);
/*      */   }
/*      */ 
/*      */   public final float getf(int x, int y) {
/*  292 */     return this.pixels[(y * this.width + x)] & 0xFFFF;
/*      */   }
/*      */ 
/*      */   public final void setf(int x, int y, float value) {
/*  296 */     this.pixels[(y * this.width + x)] = ((short)(int)value);
/*      */   }
/*      */ 
/*      */   public final float getf(int index) {
/*  300 */     return this.pixels[index] & 0xFFFF;
/*      */   }
/*      */ 
/*      */   public final void setf(int index, float value) {
/*  304 */     this.pixels[index] = ((short)(int)value);
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
/*  326 */       return (int)Math.round(getInterpolatedPixel(x, y, this.pixels));
/*  327 */     }if (this.interpolationMethod == 2) {
/*  328 */       int value = (int)(getBicubicInterpolatedPixel(x, y, this) + 0.5D);
/*  329 */       if (value < 0) value = 0;
/*  330 */       if (value > 65535) value = 65535;
/*  331 */       return value;
/*      */     }
/*  333 */     return getPixel((int)(x + 0.5D), (int)(y + 0.5D));
/*      */   }
/*      */ 
/*      */   public final void putPixel(int x, int y, int value)
/*      */   {
/*  341 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  342 */       if (value > 65535) value = 65535;
/*  343 */       if (value < 0) value = 0;
/*  344 */       this.pixels[(y * this.width + x)] = ((short)value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void putPixelValue(int x, int y, double value)
/*      */   {
/*  355 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  356 */       if ((this.cTable != null) && (this.cTable[0] == -32768.0F))
/*  357 */         value += 32768.0D;
/*  358 */       if (value > 65535.0D)
/*  359 */         value = 65535.0D;
/*  360 */       else if (value < 0.0D)
/*  361 */         value = 0.0D;
/*  362 */       this.pixels[(y * this.width + x)] = ((short)(int)(value + 0.5D));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawPixel(int x, int y)
/*      */   {
/*  368 */     if ((x >= this.clipXMin) && (x <= this.clipXMax) && (y >= this.clipYMin) && (y <= this.clipYMax))
/*  369 */       putPixel(x, y, this.fgColor);
/*      */   }
/*      */ 
/*      */   public float getPixelValue(int x, int y)
/*      */   {
/*  377 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  378 */       if (this.cTable == null) {
/*  379 */         return this.pixels[(y * this.width + x)] & 0xFFFF;
/*      */       }
/*  381 */       return this.cTable[(this.pixels[(y * this.width + x)] & 0xFFFF)];
/*      */     }
/*  383 */     return 0.0F;
/*      */   }
/*      */ 
/*      */   public Object getPixels()
/*      */   {
/*  390 */     return this.pixels;
/*      */   }
/*      */ 
/*      */   public Object getPixelsCopy()
/*      */   {
/*  399 */     if ((this.snapshotPixels != null) && (this.snapshotCopyMode)) {
/*  400 */       this.snapshotCopyMode = false;
/*  401 */       return this.snapshotPixels;
/*      */     }
/*  403 */     short[] pixels2 = new short[this.width * this.height];
/*  404 */     System.arraycopy(this.pixels, 0, pixels2, 0, this.width * this.height);
/*  405 */     return pixels2;
/*      */   }
/*      */ 
/*      */   public void setPixels(Object pixels)
/*      */   {
/*  410 */     this.pixels = ((short[])pixels);
/*  411 */     resetPixels(pixels);
/*  412 */     if (pixels == null) this.snapshotPixels = null;
/*  413 */     if (pixels == null) this.pixels8 = null;
/*  414 */     this.raster = null;
/*      */   }
/*      */ 
/*      */   void getRow2(int x, int y, int[] data, int length)
/*      */   {
/*  419 */     for (int i = 0; i < length; i++)
/*  420 */       data[i] = (this.pixels[(y * this.width + x + i)] & 0xFFFF);
/*      */   }
/*      */ 
/*      */   void putColumn2(int x, int y, int[] data, int length)
/*      */   {
/*  425 */     for (int i = 0; i < length; i++)
/*  426 */       this.pixels[((y + i) * this.width + x)] = ((short)data[i]);
/*      */   }
/*      */ 
/*      */   public void copyBits(ImageProcessor ip, int xloc, int yloc, int mode)
/*      */   {
/*  432 */     ip = ip.convertToShort(false);
/*  433 */     new ShortBlitter(this).copyBits(ip, xloc, yloc, mode);
/*      */   }
/*      */ 
/*      */   public void applyTable(int[] lut)
/*      */   {
/*  438 */     if (lut.length != 65536) {
/*  439 */       throw new IllegalArgumentException("lut.length!=65536");
/*      */     }
/*  441 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  442 */       int lineStart = y * this.width + this.roiX;
/*  443 */       int lineEnd = lineStart + this.roiWidth;
/*  444 */       int i = lineEnd;
/*      */       while (true) { i--; if (i < lineStart) break;
/*  445 */         int v = lut[(this.pixels[i] & 0xFFFF)];
/*  446 */         this.pixels[i] = ((short)v);
/*      */       }
/*      */     }
/*  449 */     findMinAndMax();
/*      */   }
/*      */ 
/*      */   private void process(int op, double value)
/*      */   {
/*  454 */     double range = getMax() - getMin();
/*      */ 
/*  456 */     int offset = (this.cTable != null) && (this.cTable[0] == -32768.0F) ? 32768 : 0;
/*  457 */     int min2 = (int)getMin() - offset;
/*  458 */     int max2 = (int)getMax() - offset;
/*  459 */     int fgColor2 = this.fgColor - offset;
/*      */ 
/*  461 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  462 */       int i = y * this.width + this.roiX;
/*  463 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  464 */         int v1 = (this.pixels[i] & 0xFFFF) - offset;
/*      */         int v2;
/*      */         int v2;
/*      */         int v2;
/*      */         int v2;
/*      */         int v2;
/*  465 */         switch (op) {
/*      */         case 0:
/*  467 */           v2 = max2 - (v1 - min2);
/*      */ 
/*  469 */           break;
/*      */         case 1:
/*  471 */           v2 = fgColor2;
/*  472 */           break;
/*      */         case 2:
/*  474 */           v2 = v1 + (int)value;
/*  475 */           break;
/*      */         case 3:
/*  477 */           v2 = (int)Math.round(v1 * value);
/*  478 */           break;
/*      */         case 4:
/*  480 */           v2 = v1 & (int)value;
/*  481 */           break;
/*      */         case 5:
/*  483 */           v2 = v1 | (int)value;
/*  484 */           break;
/*      */         case 6:
/*  486 */           v2 = v1 ^ (int)value;
/*  487 */           break;
/*      */         case 7:
/*  489 */           if ((range <= 0.0D) || (v1 == min2))
/*  490 */             v2 = v1;
/*      */           else
/*  492 */             v2 = (int)(Math.exp(value * Math.log((v1 - min2) / range)) * range + min2);
/*  493 */           break;
/*      */         case 8:
/*  495 */           if (v1 <= 0)
/*  496 */             v2 = 0;
/*      */           else
/*  498 */             v2 = (int)(Math.log(v1) * (max2 / Math.log(max2)));
/*  499 */           break;
/*      */         case 13:
/*  501 */           v2 = (int)Math.exp(v1 * (Math.log(max2) / max2));
/*  502 */           break;
/*      */         case 11:
/*  504 */           double d1 = v1;
/*  505 */           v2 = (int)(d1 * d1);
/*  506 */           break;
/*      */         case 12:
/*  508 */           v2 = (int)Math.sqrt(v1);
/*  509 */           break;
/*      */         case 14:
/*  511 */           v2 = Math.abs(v1);
/*  512 */           break;
/*      */         case 9:
/*  514 */           if (v1 < value)
/*  515 */             v2 = (int)value;
/*      */           else
/*  517 */             v2 = v1;
/*  518 */           break;
/*      */         case 10:
/*  520 */           if (v1 > value)
/*  521 */             v2 = (int)value;
/*      */           else
/*  523 */             v2 = v1;
/*  524 */           break;
/*      */         default:
/*  526 */           v2 = v1;
/*      */         }
/*  528 */         v2 += offset;
/*  529 */         if (v2 < 0)
/*  530 */           v2 = 0;
/*  531 */         if (v2 > 65535)
/*  532 */           v2 = 65535;
/*  533 */         this.pixels[(i++)] = ((short)v2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void invert() {
/*  539 */     resetMinAndMax();
/*  540 */     process(0, 0.0D);
/*      */   }
/*      */   public void add(int value) {
/*  543 */     process(2, value); } 
/*  544 */   public void add(double value) { process(2, value); } 
/*  545 */   public void multiply(double value) { process(3, value); } 
/*  546 */   public void and(int value) { process(4, value); } 
/*  547 */   public void or(int value) { process(5, value); } 
/*  548 */   public void xor(int value) { process(6, value); } 
/*  549 */   public void gamma(double value) { process(7, value); } 
/*  550 */   public void log() { process(8, 0.0D); } 
/*  551 */   public void exp() { process(13, 0.0D); } 
/*  552 */   public void sqr() { process(11, 0.0D); } 
/*  553 */   public void sqrt() { process(12, 0.0D); } 
/*  554 */   public void abs() { process(14, 0.0D); } 
/*  555 */   public void min(double value) { process(9, value); } 
/*  556 */   public void max(double value) { process(10, value); }
/*      */ 
/*      */   public void fill()
/*      */   {
/*  560 */     process(1, 0.0D);
/*      */   }
/*      */ 
/*      */   public void fill(ImageProcessor mask)
/*      */   {
/*  566 */     if (mask == null) {
/*  567 */       fill(); return;
/*  568 */     }int roiWidth = this.roiWidth; int roiHeight = this.roiHeight;
/*  569 */     int roiX = this.roiX; int roiY = this.roiY;
/*  570 */     if ((mask.getWidth() != roiWidth) || (mask.getHeight() != roiHeight))
/*  571 */       return;
/*  572 */     byte[] mpixels = (byte[])mask.getPixels();
/*  573 */     int y = roiY; for (int my = 0; y < roiY + roiHeight; my++) {
/*  574 */       int i = y * this.width + roiX;
/*  575 */       int mi = my * roiWidth;
/*  576 */       for (int x = roiX; x < roiX + roiWidth; x++) {
/*  577 */         if (mpixels[(mi++)] != 0)
/*  578 */           this.pixels[i] = ((short)this.fgColor);
/*  579 */         i++;
/*      */       }
/*  573 */       y++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void convolve3x3(int[] kernel)
/*      */   {
/*  586 */     filter3x3(5, kernel);
/*      */   }
/*      */ 
/*      */   public void filter(int type)
/*      */   {
/*  591 */     filter3x3(type, null);
/*      */   }
/*      */ 
/*      */   void filter3x3(int type, int[] kernel)
/*      */   {
/*  600 */     int k1 = 0; int k2 = 0; int k3 = 0;
/*  601 */     int k4 = 0; int k5 = 0; int k6 = 0;
/*  602 */     int k7 = 0; int k8 = 0; int k9 = 0;
/*  603 */     int scale = 0;
/*  604 */     if (type == 5) {
/*  605 */       k1 = kernel[0]; k2 = kernel[1]; k3 = kernel[2];
/*  606 */       k4 = kernel[3]; k5 = kernel[4]; k6 = kernel[5];
/*  607 */       k7 = kernel[6]; k8 = kernel[7]; k9 = kernel[8];
/*  608 */       for (int i = 0; i < kernel.length; i++)
/*  609 */         scale += kernel[i];
/*  610 */       if (scale == 0) scale = 1;
/*      */     }
/*  612 */     int inc = this.roiHeight / 25;
/*  613 */     if (inc < 1) inc = 1;
/*      */ 
/*  615 */     short[] pixels2 = (short[])getPixelsCopy();
/*  616 */     int xEnd = this.roiX + this.roiWidth;
/*  617 */     int yEnd = this.roiY + this.roiHeight;
/*  618 */     for (int y = this.roiY; y < yEnd; y++) {
/*  619 */       int p = this.roiX + y * this.width;
/*  620 */       int p6 = p - (this.roiX > 0 ? 1 : 0);
/*  621 */       int p3 = p6 - (y > 0 ? this.width : 0);
/*  622 */       int p9 = p6 + (y < this.height - 1 ? this.width : 0);
/*  623 */       int v2 = pixels2[p3] & 0xFFFF;
/*  624 */       int v5 = pixels2[p6] & 0xFFFF;
/*  625 */       int v8 = pixels2[p9] & 0xFFFF;
/*  626 */       if (this.roiX > 0) { p3++; p6++; p9++; }
/*  627 */       int v3 = pixels2[p3] & 0xFFFF;
/*  628 */       int v6 = pixels2[p6] & 0xFFFF;
/*  629 */       int v9 = pixels2[p9] & 0xFFFF;
/*      */ 
/*  631 */       switch (type) {
/*      */       case 0:
/*  633 */         for (int x = this.roiX; x < xEnd; p++) {
/*  634 */           if (x < this.width - 1) { p3++; p6++; p9++; }
/*  635 */           int v1 = v2; v2 = v3;
/*  636 */           v3 = pixels2[p3] & 0xFFFF;
/*  637 */           int v4 = v5; v5 = v6;
/*  638 */           v6 = pixels2[p6] & 0xFFFF;
/*  639 */           int v7 = v8; v8 = v9;
/*  640 */           v9 = pixels2[p9] & 0xFFFF;
/*  641 */           this.pixels[p] = ((short)((v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8 + v9 + 4) / 9));
/*      */ 
/*  633 */           x++;
/*      */         }
/*      */ 
/*  643 */         break;
/*      */       case 1:
/*  645 */         for (int x = this.roiX; x < xEnd; p++) {
/*  646 */           if (x < this.width - 1) { p3++; p6++; p9++; }
/*  647 */           int v1 = v2; v2 = v3;
/*  648 */           v3 = pixels2[p3] & 0xFFFF;
/*  649 */           int v4 = v5; v5 = v6;
/*  650 */           v6 = pixels2[p6] & 0xFFFF;
/*  651 */           int v7 = v8; v8 = v9;
/*  652 */           v9 = pixels2[p9] & 0xFFFF;
/*  653 */           double sum1 = v1 + 2 * v2 + v3 - v7 - 2 * v8 - v9;
/*  654 */           double sum2 = v1 + 2 * v4 + v7 - v3 - 2 * v6 - v9;
/*  655 */           double result = Math.sqrt(sum1 * sum1 + sum2 * sum2);
/*  656 */           if (result > 65535.0D) result = 65535.0D;
/*  657 */           this.pixels[p] = ((short)(int)result);
/*      */ 
/*  645 */           x++;
/*      */         }
/*      */ 
/*  659 */         break;
/*      */       case 5:
/*  661 */         for (int x = this.roiX; x < xEnd; p++) {
/*  662 */           if (x < this.width - 1) { p3++; p6++; p9++; }
/*  663 */           int v1 = v2; v2 = v3;
/*  664 */           v3 = pixels2[p3] & 0xFFFF;
/*  665 */           int v4 = v5; v5 = v6;
/*  666 */           v6 = pixels2[p6] & 0xFFFF;
/*  667 */           int v7 = v8; v8 = v9;
/*  668 */           v9 = pixels2[p9] & 0xFFFF;
/*  669 */           int sum = k1 * v1 + k2 * v2 + k3 * v3 + k4 * v4 + k5 * v5 + k6 * v6 + k7 * v7 + k8 * v8 + k9 * v9;
/*      */ 
/*  672 */           sum = (sum + scale / 2) / scale;
/*  673 */           if (sum > 65535) sum = 65535;
/*  674 */           if (sum < 0) sum = 0;
/*  675 */           this.pixels[p] = ((short)sum);
/*      */ 
/*  661 */           x++;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  679 */       if (y % inc == 0)
/*  680 */         showProgress((y - this.roiY) / this.roiHeight);
/*      */     }
/*  682 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void rotate(double angle)
/*      */   {
/*  689 */     short[] pixels2 = (short[])getPixelsCopy();
/*  690 */     ImageProcessor ip2 = null;
/*  691 */     if (this.interpolationMethod == 2)
/*  692 */       ip2 = new ShortProcessor(getWidth(), getHeight(), pixels2, null);
/*  693 */     double centerX = this.roiX + (this.roiWidth - 1) / 2.0D;
/*  694 */     double centerY = this.roiY + (this.roiHeight - 1) / 2.0D;
/*  695 */     int xMax = this.roiX + this.roiWidth - 1;
/*      */ 
/*  697 */     double angleRadians = -angle / 57.295779513082323D;
/*  698 */     double ca = Math.cos(angleRadians);
/*  699 */     double sa = Math.sin(angleRadians);
/*  700 */     double tmp1 = centerY * sa - centerX * ca;
/*  701 */     double tmp2 = -centerX * sa - centerY * ca;
/*      */ 
/*  704 */     double dwidth = this.width; double dheight = this.height;
/*  705 */     double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  706 */     double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*      */ 
/*  708 */     int background = (this.cTable != null) && (this.cTable[0] == -32768.0F) ? 32768 : 0;
/*      */ 
/*  710 */     if (this.interpolationMethod == 2)
/*  711 */       for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  712 */         int index = y * this.width + this.roiX;
/*  713 */         double tmp3 = tmp1 - y * sa + centerX;
/*  714 */         double tmp4 = tmp2 + y * ca + centerY;
/*  715 */         for (int x = this.roiX; x <= xMax; x++) {
/*  716 */           double xs = x * ca + tmp3;
/*  717 */           double ys = x * sa + tmp4;
/*  718 */           int value = (int)(getBicubicInterpolatedPixel(xs, ys, ip2) + 0.5D);
/*  719 */           if (value < 0) value = 0;
/*  720 */           if (value > 65535) value = 65535;
/*  721 */           this.pixels[(index++)] = ((short)value);
/*      */         }
/*  723 */         if (y % 30 == 0) showProgress((y - this.roiY) / this.roiHeight);
/*      */       }
/*      */     else {
/*  726 */       for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  727 */         int index = y * this.width + this.roiX;
/*  728 */         double tmp3 = tmp1 - y * sa + centerX;
/*  729 */         double tmp4 = tmp2 + y * ca + centerY;
/*  730 */         for (int x = this.roiX; x <= xMax; x++) {
/*  731 */           double xs = x * ca + tmp3;
/*  732 */           double ys = x * sa + tmp4;
/*  733 */           if ((xs >= -0.01D) && (xs < dwidth) && (ys >= -0.01D) && (ys < dheight)) {
/*  734 */             if (this.interpolationMethod == 1) {
/*  735 */               if (xs < 0.0D) xs = 0.0D;
/*  736 */               if (xs >= xlimit) xs = xlimit2;
/*  737 */               if (ys < 0.0D) ys = 0.0D;
/*  738 */               if (ys >= ylimit) ys = ylimit2;
/*  739 */               this.pixels[(index++)] = ((short)(int)(getInterpolatedPixel(xs, ys, pixels2) + 0.5D));
/*      */             } else {
/*  741 */               int ixs = (int)(xs + 0.5D);
/*  742 */               int iys = (int)(ys + 0.5D);
/*  743 */               if (ixs >= this.width) ixs = this.width - 1;
/*  744 */               if (iys >= this.height) iys = this.height - 1;
/*  745 */               this.pixels[(index++)] = pixels2[(this.width * iys + ixs)];
/*      */             }
/*      */           }
/*  748 */           else this.pixels[(index++)] = ((short)background);
/*      */         }
/*  750 */         if (y % 30 == 0)
/*  751 */           showProgress((y - this.roiY) / this.roiHeight);
/*      */       }
/*      */     }
/*  754 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void flipVertical()
/*      */   {
/*  760 */     for (int y = 0; y < this.roiHeight / 2; y++) {
/*  761 */       int index1 = (this.roiY + y) * this.width + this.roiX;
/*  762 */       int index2 = (this.roiY + this.roiHeight - 1 - y) * this.width + this.roiX;
/*  763 */       for (int i = 0; i < this.roiWidth; i++) {
/*  764 */         short tmp = this.pixels[index1];
/*  765 */         this.pixels[(index1++)] = this.pixels[index2];
/*  766 */         this.pixels[(index2++)] = tmp;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void scale(double xScale, double yScale)
/*      */   {
/*  775 */     double xCenter = this.roiX + this.roiWidth / 2.0D;
/*  776 */     double yCenter = this.roiY + this.roiHeight / 2.0D;
/*      */     int xmin;
/*      */     int xmax;
/*      */     int ymin;
/*      */     int ymax;
/*  778 */     if ((xScale > 1.0D) && (yScale > 1.0D))
/*      */     {
/*  780 */       int xmin = (int)(xCenter - (xCenter - this.roiX) * xScale);
/*  781 */       if (xmin < 0) xmin = 0;
/*  782 */       int xmax = xmin + (int)(this.roiWidth * xScale) - 1;
/*  783 */       if (xmax >= this.width) xmax = this.width - 1;
/*  784 */       int ymin = (int)(yCenter - (yCenter - this.roiY) * yScale);
/*  785 */       if (ymin < 0) ymin = 0;
/*  786 */       int ymax = ymin + (int)(this.roiHeight * yScale) - 1;
/*  787 */       if (ymax >= this.height) ymax = this.height - 1; 
/*      */     }
/*  789 */     else { xmin = this.roiX;
/*  790 */       xmax = this.roiX + this.roiWidth - 1;
/*  791 */       ymin = this.roiY;
/*  792 */       ymax = this.roiY + this.roiHeight - 1;
/*      */     }
/*  794 */     short[] pixels2 = (short[])getPixelsCopy();
/*  795 */     ImageProcessor ip2 = null;
/*  796 */     if (this.interpolationMethod == 2)
/*  797 */       ip2 = new ShortProcessor(getWidth(), getHeight(), pixels2, null);
/*  798 */     boolean checkCoordinates = (xScale < 1.0D) || (yScale < 1.0D);
/*  799 */     short min2 = (short)(int)getMin();
/*      */ 
/*  802 */     if (this.interpolationMethod == 2) {
/*  803 */       for (int y = ymin; y <= ymax; y++) {
/*  804 */         double ys = (y - yCenter) / yScale + yCenter;
/*  805 */         int index = y * this.width + xmin;
/*  806 */         for (int x = xmin; x <= xmax; x++) {
/*  807 */           double xs = (x - xCenter) / xScale + xCenter;
/*  808 */           int value = (int)(getBicubicInterpolatedPixel(xs, ys, ip2) + 0.5D);
/*  809 */           if (value < 0) value = 0; if (value > 65535) value = 65535;
/*  810 */           this.pixels[(index++)] = ((short)value);
/*      */         }
/*  812 */         if (y % 30 == 0) showProgress((y - ymin) / this.height); 
/*      */       }
/*      */     }
/*  815 */     else { double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  816 */       double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*  817 */       for (int y = ymin; y <= ymax; y++) {
/*  818 */         double ys = (y - yCenter) / yScale + yCenter;
/*  819 */         int ysi = (int)ys;
/*  820 */         if (ys < 0.0D) ys = 0.0D;
/*  821 */         if (ys >= ylimit) ys = ylimit2;
/*  822 */         int index1 = y * this.width + xmin;
/*  823 */         int index2 = this.width * (int)ys;
/*  824 */         for (int x = xmin; x <= xmax; x++) {
/*  825 */           double xs = (x - xCenter) / xScale + xCenter;
/*  826 */           int xsi = (int)xs;
/*  827 */           if ((checkCoordinates) && ((xsi < xmin) || (xsi > xmax) || (ysi < ymin) || (ysi > ymax))) {
/*  828 */             this.pixels[(index1++)] = min2;
/*      */           }
/*  830 */           else if (this.interpolationMethod == 1) {
/*  831 */             if (xs < 0.0D) xs = 0.0D;
/*  832 */             if (xs >= xlimit) xs = xlimit2;
/*  833 */             this.pixels[(index1++)] = ((short)(int)(getInterpolatedPixel(xs, ys, pixels2) + 0.5D));
/*      */           } else {
/*  835 */             this.pixels[(index1++)] = pixels2[(index2 + xsi)];
/*      */           }
/*      */         }
/*  838 */         if (y % 30 == 0) showProgress((y - ymin) / this.height);
/*      */       }
/*      */     }
/*  841 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   private final double getInterpolatedPixel(double x, double y, short[] pixels)
/*      */   {
/*  846 */     int xbase = (int)x;
/*  847 */     int ybase = (int)y;
/*  848 */     double xFraction = x - xbase;
/*  849 */     double yFraction = y - ybase;
/*  850 */     int offset = ybase * this.width + xbase;
/*  851 */     int lowerLeft = pixels[offset] & 0xFFFF;
/*  852 */     int lowerRight = pixels[(offset + 1)] & 0xFFFF;
/*  853 */     int upperRight = pixels[(offset + this.width + 1)] & 0xFFFF;
/*  854 */     int upperLeft = pixels[(offset + this.width)] & 0xFFFF;
/*  855 */     double upperAverage = upperLeft + xFraction * (upperRight - upperLeft);
/*  856 */     double lowerAverage = lowerLeft + xFraction * (lowerRight - lowerLeft);
/*  857 */     return lowerAverage + yFraction * (upperAverage - lowerAverage);
/*      */   }
/*      */ 
/*      */   public ImageProcessor resize(int dstWidth, int dstHeight)
/*      */   {
/*  862 */     double srcCenterX = this.roiX + this.roiWidth / 2.0D;
/*  863 */     double srcCenterY = this.roiY + this.roiHeight / 2.0D;
/*  864 */     double dstCenterX = dstWidth / 2.0D;
/*  865 */     double dstCenterY = dstHeight / 2.0D;
/*  866 */     double xScale = dstWidth / this.roiWidth;
/*  867 */     double yScale = dstHeight / this.roiHeight;
/*  868 */     if (this.interpolationMethod != 0) {
/*  869 */       dstCenterX += xScale / 2.0D;
/*  870 */       dstCenterY += yScale / 2.0D;
/*      */     }
/*  872 */     ImageProcessor ip2 = createProcessor(dstWidth, dstHeight);
/*  873 */     short[] pixels2 = (short[])ip2.getPixels();
/*      */ 
/*  875 */     if (this.interpolationMethod == 2) {
/*  876 */       for (int y = 0; y <= dstHeight - 1; y++) {
/*  877 */         double ys = (y - dstCenterY) / yScale + srcCenterY;
/*  878 */         int index2 = y * dstWidth;
/*  879 */         for (int x = 0; x <= dstWidth - 1; x++) {
/*  880 */           double xs = (x - dstCenterX) / xScale + srcCenterX;
/*  881 */           int value = (int)(getBicubicInterpolatedPixel(xs, ys, this) + 0.5D);
/*  882 */           if (value < 0) value = 0; if (value > 65535) value = 65535;
/*  883 */           pixels2[(index2++)] = ((short)value);
/*      */         }
/*  885 */         if (y % 30 == 0) showProgress(y / dstHeight); 
/*      */       }
/*      */     }
/*  888 */     else { double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  889 */       double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*      */ 
/*  891 */       for (int y = 0; y <= dstHeight - 1; y++) {
/*  892 */         double ys = (y - dstCenterY) / yScale + srcCenterY;
/*  893 */         if (this.interpolationMethod == 1) {
/*  894 */           if (ys < 0.0D) ys = 0.0D;
/*  895 */           if (ys >= ylimit) ys = ylimit2;
/*      */         }
/*  897 */         int index1 = this.width * (int)ys;
/*  898 */         int index2 = y * dstWidth;
/*  899 */         for (int x = 0; x <= dstWidth - 1; x++) {
/*  900 */           double xs = (x - dstCenterX) / xScale + srcCenterX;
/*  901 */           if (this.interpolationMethod == 1) {
/*  902 */             if (xs < 0.0D) xs = 0.0D;
/*  903 */             if (xs >= xlimit) xs = xlimit2;
/*  904 */             pixels2[(index2++)] = ((short)(int)(getInterpolatedPixel(xs, ys, this.pixels) + 0.5D));
/*      */           } else {
/*  906 */             pixels2[(index2++)] = this.pixels[(index1 + (int)xs)];
/*      */           }
/*      */         }
/*  908 */         if (y % 30 == 0) showProgress(y / dstHeight);
/*      */       }
/*      */     }
/*  911 */     showProgress(1.0D);
/*  912 */     return ip2;
/*      */   }
/*      */ 
/*      */   public ImageProcessor crop() {
/*  916 */     ImageProcessor ip2 = createProcessor(this.roiWidth, this.roiHeight);
/*  917 */     short[] pixels2 = (short[])ip2.getPixels();
/*  918 */     for (int ys = this.roiY; ys < this.roiY + this.roiHeight; ys++) {
/*  919 */       int offset1 = (ys - this.roiY) * this.roiWidth;
/*  920 */       int offset2 = ys * this.width + this.roiX;
/*  921 */       for (int xs = 0; xs < this.roiWidth; xs++)
/*  922 */         pixels2[(offset1++)] = this.pixels[(offset2++)];
/*      */     }
/*  924 */     return ip2;
/*      */   }
/*      */ 
/*      */   public synchronized ImageProcessor duplicate()
/*      */   {
/*  929 */     ImageProcessor ip2 = createProcessor(this.width, this.height);
/*  930 */     short[] pixels2 = (short[])ip2.getPixels();
/*  931 */     System.arraycopy(this.pixels, 0, pixels2, 0, this.width * this.height);
/*  932 */     return ip2;
/*      */   }
/*      */ 
/*      */   public void setColor(Color color)
/*      */   {
/*  937 */     this.drawingColor = color;
/*  938 */     int bestIndex = getBestIndex(color);
/*  939 */     if ((bestIndex > 0) && (getMin() == 0.0D) && (getMax() == 0.0D)) {
/*  940 */       setValue(bestIndex);
/*  941 */       setMinAndMax(0.0D, 255.0D);
/*  942 */     } else if ((bestIndex == 0) && (getMin() > 0.0D) && ((color.getRGB() & 0xFFFFFF) == 0)) {
/*  943 */       if ((this.cTable != null) && (this.cTable[0] == -32768.0F))
/*  944 */         setValue(32768.0D);
/*      */       else
/*  946 */         setValue(0.0D);
/*      */     } else {
/*  948 */       this.fgColor = ((int)(getMin() + (getMax() - getMin()) * (bestIndex / 255.0D)));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setValue(double value) {
/*  953 */     this.fgColor = ((int)value);
/*  954 */     if (this.fgColor < 0) this.fgColor = 0;
/*  955 */     if (this.fgColor > 65535) this.fgColor = 65535;
/*      */   }
/*      */ 
/*      */   public void setBackgroundValue(double value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public double getBackgroundValue()
/*      */   {
/*  964 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public int[] getHistogram()
/*      */   {
/*  970 */     if (this.mask != null)
/*  971 */       return getHistogram(this.mask);
/*  972 */     int[] histogram = new int[65536];
/*  973 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  974 */       int i = y * this.width + this.roiX;
/*  975 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++)
/*  976 */         histogram[(this.pixels[(i++)] & 0xFFFF)] += 1;
/*      */     }
/*  978 */     return histogram;
/*      */   }
/*      */ 
/*      */   int[] getHistogram(ImageProcessor mask) {
/*  982 */     if ((mask.getWidth() != this.roiWidth) || (mask.getHeight() != this.roiHeight))
/*  983 */       throw new IllegalArgumentException(maskSizeError(mask));
/*  984 */     byte[] mpixels = (byte[])mask.getPixels();
/*  985 */     int[] histogram = new int[65536];
/*  986 */     int y = this.roiY; for (int my = 0; y < this.roiY + this.roiHeight; my++) {
/*  987 */       int i = y * this.width + this.roiX;
/*  988 */       int mi = my * this.roiWidth;
/*  989 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  990 */         if (mpixels[(mi++)] != 0)
/*  991 */           histogram[(this.pixels[i] & 0xFFFF)] += 1;
/*  992 */         i++;
/*      */       }
/*  986 */       y++;
/*      */     }
/*      */ 
/*  995 */     return histogram;
/*      */   }
/*      */ 
/*      */   public void setThreshold(double minThreshold, double maxThreshold, int lutUpdate) {
/*  999 */     if (minThreshold == -808080.0D) {
/* 1000 */       resetThreshold(); return;
/* 1001 */     }if (minThreshold < 0.0D) minThreshold = 0.0D;
/* 1002 */     if (maxThreshold > 65535.0D) maxThreshold = 65535.0D;
/* 1003 */     int min2 = (int)getMin(); int max2 = (int)getMax();
/* 1004 */     if (max2 > min2)
/*      */     {
/* 1006 */       double scale = 256.0D / (max2 - min2 + 1);
/* 1007 */       double minT = minThreshold - min2;
/* 1008 */       if (minT < 0.0D) minT = 0.0D;
/* 1009 */       minT = (int)(minT * scale + 0.5D);
/* 1010 */       if (minT > 255.0D) minT = 255.0D;
/*      */ 
/* 1012 */       double maxT = maxThreshold - min2;
/* 1013 */       if (maxT < 0.0D) maxT = 0.0D;
/* 1014 */       maxT = (int)(maxT * scale + 0.5D);
/* 1015 */       if (maxT > 255.0D) maxT = 255.0D;
/* 1016 */       super.setThreshold(minT, maxT, lutUpdate);
/*      */     } else {
/* 1018 */       super.resetThreshold();
/* 1019 */     }this.minThreshold = Math.round(minThreshold);
/* 1020 */     this.maxThreshold = Math.round(maxThreshold);
/*      */   }
/*      */ 
/*      */   public void convolve(float[] kernel, int kernelWidth, int kernelHeight)
/*      */   {
/* 1025 */     ImageProcessor ip2 = convertToFloat();
/* 1026 */     ip2.setRoi(getRoi());
/* 1027 */     new Convolver().convolve(ip2, kernel, kernelWidth, kernelHeight);
/* 1028 */     ip2 = ip2.convertToShort(false);
/* 1029 */     short[] pixels2 = (short[])ip2.getPixels();
/* 1030 */     System.arraycopy(pixels2, 0, this.pixels, 0, this.pixels.length);
/*      */   }
/*      */ 
/*      */   public void noise(double range) {
/* 1034 */     Random rnd = new Random();
/*      */ 
/* 1037 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/* 1038 */       int i = y * this.width + this.roiX;
/* 1039 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/* 1040 */         boolean inRange = false;
/*      */         do {
/* 1042 */           int ran = (int)Math.round(rnd.nextGaussian() * range);
/* 1043 */           int v = (this.pixels[i] & 0xFFFF) + ran;
/* 1044 */           inRange = (v >= 0) && (v <= 65535);
/* 1045 */           if (inRange) this.pixels[i] = ((short)v); 
/*      */         }
/* 1046 */         while (!inRange);
/* 1047 */         i++;
/*      */       }
/*      */     }
/* 1050 */     resetMinAndMax();
/*      */   }
/*      */ 
/*      */   public void threshold(int level) {
/* 1054 */     for (int i = 0; i < this.width * this.height; i++) {
/* 1055 */       if ((this.pixels[i] & 0xFFFF) <= level)
/* 1056 */         this.pixels[i] = 0;
/*      */       else
/* 1058 */         this.pixels[i] = 255;
/*      */     }
/* 1060 */     findMinAndMax();
/*      */   }
/*      */ 
/*      */   public FloatProcessor toFloat(int channelNumber, FloatProcessor fp)
/*      */   {
/* 1074 */     int size = this.width * this.height;
/* 1075 */     if ((fp == null) || (fp.getWidth() != this.width) || (fp.getHeight() != this.height))
/* 1076 */       fp = new FloatProcessor(this.width, this.height, new float[size], this.cm);
/* 1077 */     float[] fPixels = (float[])fp.getPixels();
/* 1078 */     for (int i = 0; i < size; i++)
/* 1079 */       fPixels[i] = (this.pixels[i] & 0xFFFF);
/* 1080 */     fp.setRoi(getRoi());
/* 1081 */     fp.setMask(this.mask);
/* 1082 */     fp.setMinAndMax(getMin(), getMax());
/* 1083 */     fp.setThreshold(this.minThreshold, this.maxThreshold, 2);
/* 1084 */     return fp;
/*      */   }
/*      */ 
/*      */   public void setPixels(int channelNumber, FloatProcessor fp)
/*      */   {
/* 1093 */     float[] fPixels = (float[])fp.getPixels();
/*      */ 
/* 1095 */     int size = this.width * this.height;
/* 1096 */     for (int i = 0; i < size; i++) {
/* 1097 */       float value = fPixels[i] + 0.5F;
/* 1098 */       if (value < 0.0F) value = 0.0F;
/* 1099 */       if (value > 65535.0F) value = 65535.0F;
/* 1100 */       this.pixels[i] = ((short)(int)value);
/*      */     }
/* 1102 */     setMinAndMax(fp.getMin(), fp.getMax());
/*      */   }
/*      */ 
/*      */   public double maxValue()
/*      */   {
/* 1107 */     return 65535.0D;
/*      */   }
/*      */ 
/*      */   public void medianFilter()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void erode()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void dilate()
/*      */   {
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ShortProcessor
 * JD-Core Version:    0.6.2
 */