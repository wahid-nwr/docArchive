/*      */ package ij.process;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.Prefs;
/*      */ import ij.plugin.filter.Convolver;
/*      */ import java.awt.Color;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Image;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.DataBuffer;
/*      */ import java.awt.image.DataBufferByte;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.MemoryImageSource;
/*      */ import java.awt.image.PixelGrabber;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.io.PrintStream;
/*      */ import java.util.Random;
/*      */ 
/*      */ public class ByteProcessor extends ImageProcessor
/*      */ {
/*      */   static final int ERODE = 10;
/*      */   static final int DILATE = 11;
/*      */   protected byte[] pixels;
/*      */   protected byte[] snapshotPixels;
/*   18 */   private int bgColor = 255;
/*      */   private boolean bgColorSet;
/*   20 */   private int min = 0; private int max = 255;
/*      */   private int binaryCount;
/*      */   private int binaryBackground;
/*      */   static double oldx;
/*      */   static double oldy;
/*      */ 
/*      */   public ByteProcessor(Image img)
/*      */   {
/*   25 */     this.width = img.getWidth(null);
/*   26 */     this.height = img.getHeight(null);
/*   27 */     resetRoi();
/*   28 */     this.pixels = new byte[this.width * this.height];
/*   29 */     PixelGrabber pg = new PixelGrabber(img, 0, 0, this.width, this.height, false);
/*      */     try {
/*   31 */       pg.grabPixels();
/*      */     } catch (InterruptedException e) {
/*   33 */       System.err.println(e);
/*      */     }
/*   35 */     this.cm = pg.getColorModel();
/*   36 */     if ((this.cm instanceof IndexColorModel))
/*   37 */       this.pixels = ((byte[])pg.getPixels());
/*      */     else
/*   39 */       System.err.println("ByteProcessor: not 8-bit image");
/*   40 */     if (((IndexColorModel)this.cm).getTransparentPixel() != -1) {
/*   41 */       IndexColorModel icm = (IndexColorModel)this.cm;
/*   42 */       int mapSize = icm.getMapSize();
/*   43 */       byte[] reds = new byte[mapSize];
/*   44 */       byte[] greens = new byte[mapSize];
/*   45 */       byte[] blues = new byte[mapSize];
/*   46 */       icm.getReds(reds);
/*   47 */       icm.getGreens(greens);
/*   48 */       icm.getBlues(blues);
/*   49 */       this.cm = new IndexColorModel(8, mapSize, reds, greens, blues);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ByteProcessor(int width, int height)
/*      */   {
/*   55 */     this(width, height, new byte[width * height], null);
/*      */   }
/*      */ 
/*      */   public ByteProcessor(int width, int height, byte[] pixels, ColorModel cm)
/*      */   {
/*   60 */     if ((pixels != null) && (width * height != pixels.length))
/*   61 */       throw new IllegalArgumentException("width*height!=pixels.length");
/*   62 */     this.width = width;
/*   63 */     this.height = height;
/*   64 */     resetRoi();
/*   65 */     this.pixels = pixels;
/*   66 */     this.cm = cm;
/*      */   }
/*      */ 
/*      */   public ByteProcessor(BufferedImage bi)
/*      */   {
/*   71 */     if (bi.getType() != 10)
/*   72 */       throw new IllegalArgumentException("Type!=TYPE_BYTE_GRAYY");
/*   73 */     WritableRaster raster = bi.getRaster();
/*   74 */     DataBuffer buffer = raster.getDataBuffer();
/*   75 */     this.pixels = ((DataBufferByte)buffer).getData();
/*   76 */     this.width = raster.getWidth();
/*   77 */     this.height = raster.getHeight();
/*      */   }
/*      */ 
/*      */   public Image createImage() {
/*   81 */     if (this.cm == null) this.cm = getDefaultColorModel();
/*   82 */     if (IJ.isJava16()) return createBufferedImage();
/*   83 */     if (this.source == null) {
/*   84 */       this.source = new MemoryImageSource(this.width, this.height, this.cm, this.pixels, 0, this.width);
/*   85 */       this.source.setAnimated(true);
/*   86 */       this.source.setFullBufferUpdates(true);
/*   87 */       this.img = Toolkit.getDefaultToolkit().createImage(this.source);
/*   88 */     } else if (this.newPixels) {
/*   89 */       this.source.newPixels(this.pixels, this.cm, 0, this.width);
/*   90 */       this.newPixels = false;
/*      */     } else {
/*   92 */       this.source.newPixels();
/*   93 */     }return this.img;
/*      */   }
/*      */ 
/*      */   Image createBufferedImage() {
/*   97 */     if (this.raster == null) {
/*   98 */       SampleModel sm = getIndexSampleModel();
/*   99 */       DataBuffer db = new DataBufferByte(this.pixels, this.width * this.height, 0);
/*  100 */       this.raster = Raster.createWritableRaster(sm, db, null);
/*      */     }
/*  102 */     if ((this.image == null) || (this.cm != this.cm2)) {
/*  103 */       if (this.cm == null) this.cm = getDefaultColorModel();
/*  104 */       this.image = new BufferedImage(this.cm, this.raster, false, null);
/*  105 */       this.cm2 = this.cm;
/*      */     }
/*  107 */     return this.image;
/*      */   }
/*      */ 
/*      */   public BufferedImage getBufferedImage()
/*      */   {
/*  112 */     if (isDefaultLut()) {
/*  113 */       BufferedImage bi = new BufferedImage(this.width, this.height, 10);
/*  114 */       Graphics g = bi.createGraphics();
/*  115 */       g.drawImage(createImage(), 0, 0, null);
/*  116 */       return bi;
/*      */     }
/*  118 */     return (BufferedImage)createBufferedImage();
/*      */   }
/*      */ 
/*      */   public ImageProcessor createProcessor(int width, int height)
/*      */   {
/*  124 */     ImageProcessor ip2 = new ByteProcessor(width, height, new byte[width * height], getColorModel());
/*  125 */     if (this.baseCM != null)
/*  126 */       ip2.setMinAndMax(this.min, this.max);
/*  127 */     ip2.setInterpolationMethod(this.interpolationMethod);
/*  128 */     return ip2;
/*      */   }
/*      */ 
/*      */   public ImageProcessor crop() {
/*  132 */     ImageProcessor ip2 = createProcessor(this.roiWidth, this.roiHeight);
/*  133 */     byte[] pixels2 = (byte[])ip2.getPixels();
/*  134 */     for (int ys = this.roiY; ys < this.roiY + this.roiHeight; ys++) {
/*  135 */       int offset1 = (ys - this.roiY) * this.roiWidth;
/*  136 */       int offset2 = ys * this.width + this.roiX;
/*  137 */       for (int xs = 0; xs < this.roiWidth; xs++)
/*  138 */         pixels2[(offset1++)] = this.pixels[(offset2++)];
/*      */     }
/*  140 */     return ip2;
/*      */   }
/*      */ 
/*      */   public synchronized ImageProcessor duplicate()
/*      */   {
/*  145 */     ImageProcessor ip2 = createProcessor(this.width, this.height);
/*  146 */     byte[] pixels2 = (byte[])ip2.getPixels();
/*  147 */     System.arraycopy(this.pixels, 0, pixels2, 0, this.width * this.height);
/*  148 */     return ip2;
/*      */   }
/*      */ 
/*      */   public void snapshot()
/*      */   {
/*  153 */     this.snapshotWidth = this.width;
/*  154 */     this.snapshotHeight = this.height;
/*  155 */     if ((this.snapshotPixels == null) || ((this.snapshotPixels != null) && (this.snapshotPixels.length != this.pixels.length)))
/*  156 */       this.snapshotPixels = new byte[this.width * this.height];
/*  157 */     System.arraycopy(this.pixels, 0, this.snapshotPixels, 0, this.width * this.height);
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/*  162 */     if (this.snapshotPixels == null)
/*  163 */       return;
/*  164 */     System.arraycopy(this.snapshotPixels, 0, this.pixels, 0, this.width * this.height);
/*      */   }
/*      */ 
/*      */   public void swapPixelArrays()
/*      */   {
/*  169 */     if (this.snapshotPixels == null) return;
/*      */ 
/*  171 */     for (int i = 0; i < this.pixels.length; i++) {
/*  172 */       byte pixel = this.pixels[i];
/*  173 */       this.pixels[i] = this.snapshotPixels[i];
/*  174 */       this.snapshotPixels[i] = pixel;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void reset(ImageProcessor mask)
/*      */   {
/*  180 */     if ((mask == null) || (this.snapshotPixels == null))
/*  181 */       return;
/*  182 */     if ((mask.getWidth() != this.roiWidth) || (mask.getHeight() != this.roiHeight))
/*  183 */       throw new IllegalArgumentException(maskSizeError(mask));
/*  184 */     byte[] mpixels = (byte[])mask.getPixels();
/*  185 */     int y = this.roiY; for (int my = 0; y < this.roiY + this.roiHeight; my++) {
/*  186 */       int i = y * this.width + this.roiX;
/*  187 */       int mi = my * this.roiWidth;
/*  188 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  189 */         if (mpixels[(mi++)] == 0)
/*  190 */           this.pixels[i] = this.snapshotPixels[i];
/*  191 */         i++;
/*      */       }
/*  185 */       y++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setSnapshotPixels(Object pixels)
/*      */   {
/*  197 */     this.snapshotPixels = ((byte[])pixels);
/*  198 */     this.snapshotWidth = this.width;
/*  199 */     this.snapshotHeight = this.height;
/*      */   }
/*      */ 
/*      */   public Object getSnapshotPixels() {
/*  203 */     return this.snapshotPixels;
/*      */   }
/*      */ 
/*      */   public void fill(ImageProcessor mask)
/*      */   {
/*  209 */     if (mask == null) {
/*  210 */       fill(); return;
/*  211 */     }int roiWidth = this.roiWidth; int roiHeight = this.roiHeight;
/*  212 */     int roiX = this.roiX; int roiY = this.roiY;
/*  213 */     if ((mask.getWidth() != roiWidth) || (mask.getHeight() != roiHeight)) {
/*  214 */       mask = getMask();
/*  215 */       if ((mask == null) || (mask.getWidth() != roiWidth) || (mask.getHeight() != roiHeight))
/*  216 */         return;
/*      */     }
/*  218 */     byte[] mpixels = (byte[])mask.getPixels();
/*  219 */     int y = roiY; for (int my = 0; y < roiY + roiHeight; my++) {
/*  220 */       int i = y * this.width + roiX;
/*  221 */       int mi = my * roiWidth;
/*  222 */       for (int x = roiX; x < roiX + roiWidth; x++) {
/*  223 */         if (mpixels[(mi++)] != 0)
/*  224 */           this.pixels[i] = ((byte)this.fgColor);
/*  225 */         i++;
/*      */       }
/*  219 */       y++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getPixel(int x, int y)
/*      */   {
/*  231 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  232 */       return this.pixels[(y * this.width + x)] & 0xFF;
/*      */     }
/*  234 */     return 0;
/*      */   }
/*      */   public final int get(int x, int y) {
/*  237 */     return this.pixels[(y * this.width + x)] & 0xFF; } 
/*  238 */   public final void set(int x, int y, int value) { this.pixels[(y * this.width + x)] = ((byte)value); } 
/*  239 */   public final int get(int index) { return this.pixels[index] & 0xFF; } 
/*  240 */   public final void set(int index, int value) { this.pixels[index] = ((byte)value); } 
/*  241 */   public final float getf(int x, int y) { return this.pixels[(y * this.width + x)] & 0xFF; } 
/*  242 */   public final void setf(int x, int y, float value) { this.pixels[(y * this.width + x)] = ((byte)(int)value); } 
/*  243 */   public final float getf(int index) { return this.pixels[index] & 0xFF; } 
/*  244 */   public final void setf(int index, float value) { this.pixels[index] = ((byte)(int)value); }
/*      */ 
/*      */ 
/*      */   public double getInterpolatedPixel(double x, double y)
/*      */   {
/*  251 */     if (this.interpolationMethod == 2) {
/*  252 */       return getBicubicInterpolatedPixel(x, y, this);
/*      */     }
/*  254 */     if (x < 0.0D) x = 0.0D;
/*  255 */     if (x >= this.width - 1.0D) x = this.width - 1.001D;
/*  256 */     if (y < 0.0D) y = 0.0D;
/*  257 */     if (y >= this.height - 1.0D) y = this.height - 1.001D;
/*  258 */     return getInterpolatedPixel(x, y, this.pixels);
/*      */   }
/*      */ 
/*      */   public final int getPixelInterpolated(double x, double y)
/*      */   {
/*  263 */     if (this.interpolationMethod == 1) {
/*  264 */       if ((x < 0.0D) || (y < 0.0D) || (x >= this.width - 1) || (y >= this.height - 1)) {
/*  265 */         return 0;
/*      */       }
/*  267 */       return (int)Math.round(getInterpolatedPixel(x, y, this.pixels));
/*  268 */     }if (this.interpolationMethod == 2) {
/*  269 */       int value = (int)(getBicubicInterpolatedPixel(x, y, this) + 0.5D);
/*  270 */       if (value < 0) value = 0;
/*  271 */       if (value > 255) value = 255;
/*  272 */       return value;
/*      */     }
/*  274 */     return getPixel((int)(x + 0.5D), (int)(y + 0.5D));
/*      */   }
/*      */ 
/*      */   public float getPixelValue(int x, int y) {
/*  278 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  279 */       if (this.cTable == null) {
/*  280 */         return this.pixels[(y * this.width + x)] & 0xFF;
/*      */       }
/*  282 */       return this.cTable[(this.pixels[(y * this.width + x)] & 0xFF)];
/*      */     }
/*  284 */     return 0.0F;
/*      */   }
/*      */ 
/*      */   public void setColor(Color color)
/*      */   {
/*  290 */     this.drawingColor = color;
/*  291 */     this.fgColor = getBestIndex(color);
/*      */   }
/*      */ 
/*      */   public void setValue(double value)
/*      */   {
/*  296 */     this.fgColor = ((int)value);
/*  297 */     if (this.fgColor < 0) this.fgColor = 0;
/*  298 */     if (this.fgColor > 255) this.fgColor = 255;
/*      */   }
/*      */ 
/*      */   public void setBackgroundValue(double value)
/*      */   {
/*  303 */     this.bgColor = ((int)value);
/*  304 */     if (this.bgColor < 0) this.bgColor = 0;
/*  305 */     if (this.bgColor > 255) this.bgColor = 255;
/*  306 */     this.bgColorSet = true;
/*      */   }
/*      */ 
/*      */   public double getBackgroundValue()
/*      */   {
/*  311 */     return this.bgColor;
/*      */   }
/*      */ 
/*      */   public void putPixelValue(int x, int y, double value)
/*      */   {
/*  318 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  319 */       if (value > 255.0D)
/*  320 */         value = 255.0D;
/*  321 */       else if (value < 0.0D)
/*  322 */         value = 0.0D;
/*  323 */       this.pixels[(y * this.width + x)] = ((byte)(int)(value + 0.5D));
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void putPixel(int x, int y, int value)
/*      */   {
/*  331 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  332 */       if (value > 255) value = 255;
/*  333 */       if (value < 0) value = 0;
/*  334 */       this.pixels[(y * this.width + x)] = ((byte)value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawPixel(int x, int y)
/*      */   {
/*  340 */     if ((x >= this.clipXMin) && (x <= this.clipXMax) && (y >= this.clipYMin) && (y <= this.clipYMax))
/*  341 */       this.pixels[(y * this.width + x)] = ((byte)this.fgColor);
/*      */   }
/*      */ 
/*      */   public Object getPixels()
/*      */   {
/*  348 */     return this.pixels;
/*      */   }
/*      */ 
/*      */   public Object getPixelsCopy()
/*      */   {
/*  357 */     if ((this.snapshotPixels != null) && (this.snapshotCopyMode)) {
/*  358 */       this.snapshotCopyMode = false;
/*  359 */       return this.snapshotPixels;
/*      */     }
/*  361 */     byte[] pixels2 = new byte[this.width * this.height];
/*  362 */     System.arraycopy(this.pixels, 0, pixels2, 0, this.width * this.height);
/*  363 */     return pixels2;
/*      */   }
/*      */ 
/*      */   public void setPixels(Object pixels)
/*      */   {
/*  368 */     if ((pixels != null) && (this.pixels != null) && (((byte[])pixels).length != this.pixels.length))
/*  369 */       throw new IllegalArgumentException("");
/*  370 */     this.pixels = ((byte[])pixels);
/*  371 */     resetPixels(pixels);
/*  372 */     if (pixels == null) this.snapshotPixels = null;
/*  373 */     this.raster = null;
/*  374 */     this.image = null;
/*      */   }
/*      */ 
/*      */   public double getMin()
/*      */   {
/*  393 */     return this.min;
/*      */   }
/*      */ 
/*      */   public double getMax()
/*      */   {
/*  398 */     return this.max;
/*      */   }
/*      */ 
/*      */   public void setMinAndMax(double min, double max)
/*      */   {
/*  403 */     if (max < min)
/*  404 */       return;
/*  405 */     this.min = ((int)min);
/*  406 */     this.max = ((int)max);
/*  407 */     if (this.rLUT1 == null) {
/*  408 */       if (this.cm == null)
/*  409 */         makeDefaultColorModel();
/*  410 */       this.baseCM = this.cm;
/*  411 */       IndexColorModel m = (IndexColorModel)this.cm;
/*  412 */       this.rLUT1 = new byte[256]; this.gLUT1 = new byte[256]; this.bLUT1 = new byte[256];
/*  413 */       m.getReds(this.rLUT1); m.getGreens(this.gLUT1); m.getBlues(this.bLUT1);
/*  414 */       this.rLUT2 = new byte[256]; this.gLUT2 = new byte[256]; this.bLUT2 = new byte[256];
/*      */     }
/*  416 */     if (this.rLUT2 == null) {
/*  417 */       return;
/*      */     }
/*  419 */     for (int i = 0; i < 256; i++) {
/*  420 */       if (i < min) {
/*  421 */         this.rLUT2[i] = this.rLUT1[0];
/*  422 */         this.gLUT2[i] = this.gLUT1[0];
/*  423 */         this.bLUT2[i] = this.bLUT1[0];
/*  424 */       } else if (i > max) {
/*  425 */         this.rLUT2[i] = this.rLUT1['ÿ'];
/*  426 */         this.gLUT2[i] = this.gLUT1['ÿ'];
/*  427 */         this.bLUT2[i] = this.bLUT1['ÿ'];
/*      */       } else {
/*  429 */         int index = i - this.min;
/*  430 */         index = (int)(256.0D * index / (max - min));
/*  431 */         if (index < 0)
/*  432 */           index = 0;
/*  433 */         if (index > 255)
/*  434 */           index = 255;
/*  435 */         this.rLUT2[i] = this.rLUT1[index];
/*  436 */         this.gLUT2[i] = this.gLUT1[index];
/*  437 */         this.bLUT2[i] = this.bLUT1[index];
/*      */       }
/*      */     }
/*  440 */     this.cm = new IndexColorModel(8, 256, this.rLUT2, this.gLUT2, this.bLUT2);
/*  441 */     this.newPixels = true;
/*  442 */     if ((min == 0.0D) && (max == 255.0D)) this.source = null;
/*  443 */     this.minThreshold = -808080.0D;
/*      */   }
/*      */ 
/*      */   public void resetMinAndMax()
/*      */   {
/*  448 */     setMinAndMax(0.0D, 255.0D);
/*      */   }
/*      */ 
/*      */   public void setThreshold(double minThreshold, double maxThreshold, int lutUpdate) {
/*  452 */     super.setThreshold(minThreshold, maxThreshold, lutUpdate);
/*  453 */     if (this.minThreshold < 0.0D) this.minThreshold = 0.0D;
/*  454 */     if (this.maxThreshold > 255.0D) this.maxThreshold = 255.0D;
/*      */   }
/*      */ 
/*      */   public void copyBits(ImageProcessor ip, int xloc, int yloc, int mode)
/*      */   {
/*  460 */     ip = ip.convertToByte(true);
/*  461 */     new ByteBlitter(this).copyBits(ip, xloc, yloc, mode);
/*      */   }
/*      */ 
/*      */   public void applyTable(int[] lut)
/*      */   {
/*  468 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  469 */       int lineStart = y * this.width + this.roiX;
/*  470 */       int lineEnd = lineStart + this.roiWidth;
/*  471 */       int i = lineEnd;
/*      */       while (true) { i--; if (i < lineStart) break;
/*  472 */         this.pixels[i] = ((byte)lut[(this.pixels[i] & 0xFF)]);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void convolve3x3(int[] kernel)
/*      */   {
/*  480 */     int scale = 0;
/*  481 */     int k1 = kernel[0]; int k2 = kernel[1]; int k3 = kernel[2];
/*  482 */     int k4 = kernel[3]; int k5 = kernel[4]; int k6 = kernel[5];
/*  483 */     int k7 = kernel[6]; int k8 = kernel[7]; int k9 = kernel[8];
/*  484 */     for (int i = 0; i < kernel.length; i++)
/*  485 */       scale += kernel[i];
/*  486 */     if (scale == 0) scale = 1;
/*  487 */     int inc = this.roiHeight / 25;
/*  488 */     if (inc < 1) inc = 1;
/*  489 */     byte[] pixels2 = (byte[])getPixelsCopy();
/*  490 */     int xEnd = this.roiX + this.roiWidth;
/*  491 */     int yEnd = this.roiY + this.roiHeight;
/*  492 */     for (int y = this.roiY; y < yEnd; y++) {
/*  493 */       int p = this.roiX + y * this.width;
/*  494 */       int p6 = p - (this.roiX > 0 ? 1 : 0);
/*  495 */       int p3 = p6 - (y > 0 ? this.width : 0);
/*  496 */       int p9 = p6 + (y < this.height - 1 ? this.width : 0);
/*  497 */       int v2 = pixels2[p3] & 0xFF;
/*  498 */       int v5 = pixels2[p6] & 0xFF;
/*  499 */       int v8 = pixels2[p9] & 0xFF;
/*  500 */       if (this.roiX > 0) { p3++; p6++; p9++; }
/*  501 */       int v3 = pixels2[p3] & 0xFF;
/*  502 */       int v6 = pixels2[p6] & 0xFF;
/*  503 */       int v9 = pixels2[p9] & 0xFF;
/*  504 */       for (int x = this.roiX; x < xEnd; p++) {
/*  505 */         if (x < this.width - 1) { p3++; p6++; p9++; }
/*  506 */         int v1 = v2; v2 = v3;
/*  507 */         v3 = pixels2[p3] & 0xFF;
/*  508 */         int v4 = v5; v5 = v6;
/*  509 */         v6 = pixels2[p6] & 0xFF;
/*  510 */         int v7 = v8; v8 = v9;
/*  511 */         v9 = pixels2[p9] & 0xFF;
/*  512 */         int sum = k1 * v1 + k2 * v2 + k3 * v3 + k4 * v4 + k5 * v5 + k6 * v6 + k7 * v7 + k8 * v8 + k9 * v9;
/*      */ 
/*  515 */         sum = (sum + scale / 2) / scale;
/*  516 */         if (sum > 255) sum = 255;
/*  517 */         if (sum < 0) sum = 0;
/*  518 */         this.pixels[p] = ((byte)sum);
/*      */ 
/*  504 */         x++;
/*      */       }
/*      */ 
/*  520 */       if (y % inc == 0)
/*  521 */         showProgress((y - this.roiY) / this.roiHeight);
/*      */     }
/*  523 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void filter(int type)
/*      */   {
/*  537 */     int inc = this.roiHeight / 25;
/*  538 */     if (inc < 1) inc = 1;
/*      */ 
/*  540 */     byte[] pixels2 = (byte[])getPixelsCopy();
/*  541 */     if (this.width == 1) {
/*  542 */       filterEdge(type, pixels2, this.roiHeight, this.roiX, this.roiY, 0, 1);
/*  543 */       return;
/*      */     }
/*  545 */     int sum2 = 0; int sum = 0;
/*  546 */     int[] values = new int[10];
/*  547 */     if (type == 2) values = new int[10];
/*  548 */     int rowOffset = this.width;
/*      */ 
/*  550 */     int binaryForeground = 255 - this.binaryBackground;
/*  551 */     for (int y = this.yMin; y <= this.yMax; y++) {
/*  552 */       int offset = this.xMin + y * this.width;
/*  553 */       int p2 = pixels2[(offset - rowOffset - 1)] & 0xFF;
/*  554 */       int p3 = pixels2[(offset - rowOffset)] & 0xFF;
/*  555 */       int p5 = pixels2[(offset - 1)] & 0xFF;
/*  556 */       int p6 = pixels2[offset] & 0xFF;
/*  557 */       int p8 = pixels2[(offset + rowOffset - 1)] & 0xFF;
/*  558 */       int p9 = pixels2[(offset + rowOffset)] & 0xFF;
/*      */ 
/*  560 */       for (int x = this.xMin; x <= this.xMax; x++) {
/*  561 */         int p1 = p2; p2 = p3;
/*  562 */         p3 = pixels2[(offset - rowOffset + 1)] & 0xFF;
/*  563 */         int p4 = p5; p5 = p6;
/*  564 */         p6 = pixels2[(offset + 1)] & 0xFF;
/*  565 */         int p7 = p8; p8 = p9;
/*  566 */         p9 = pixels2[(offset + rowOffset + 1)] & 0xFF;
/*      */         int count;
/*  568 */         switch (type) {
/*      */         case 0:
/*  570 */           sum = (p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + 4) / 9;
/*  571 */           break;
/*      */         case 1:
/*  573 */           int sum1 = p1 + 2 * p2 + p3 - p7 - 2 * p8 - p9;
/*  574 */           sum2 = p1 + 2 * p4 + p7 - p3 - 2 * p6 - p9;
/*  575 */           sum = (int)Math.sqrt(sum1 * sum1 + sum2 * sum2);
/*  576 */           if (sum > 255) sum = 255; break;
/*      */         case 2:
/*  579 */           values[1] = p1; values[2] = p2; values[3] = p3; values[4] = p4; values[5] = p5;
/*  580 */           values[6] = p6; values[7] = p7; values[8] = p8; values[9] = p9;
/*  581 */           sum = findMedian(values);
/*  582 */           break;
/*      */         case 3:
/*  584 */           sum = p5;
/*  585 */           if (p1 < sum) sum = p1;
/*  586 */           if (p2 < sum) sum = p2;
/*  587 */           if (p3 < sum) sum = p3;
/*  588 */           if (p4 < sum) sum = p4;
/*  589 */           if (p6 < sum) sum = p6;
/*  590 */           if (p7 < sum) sum = p7;
/*  591 */           if (p8 < sum) sum = p8;
/*  592 */           if (p9 < sum) sum = p9; break;
/*      */         case 4:
/*  595 */           sum = p5;
/*  596 */           if (p1 > sum) sum = p1;
/*  597 */           if (p2 > sum) sum = p2;
/*  598 */           if (p3 > sum) sum = p3;
/*  599 */           if (p4 > sum) sum = p4;
/*  600 */           if (p6 > sum) sum = p6;
/*  601 */           if (p7 > sum) sum = p7;
/*  602 */           if (p8 > sum) sum = p8;
/*  603 */           if (p9 > sum) sum = p9; break;
/*      */         case 10:
/*  606 */           if (p5 == this.binaryBackground) {
/*  607 */             sum = this.binaryBackground;
/*      */           } else {
/*  609 */             count = 0;
/*  610 */             if (p1 == this.binaryBackground) count++;
/*  611 */             if (p2 == this.binaryBackground) count++;
/*  612 */             if (p3 == this.binaryBackground) count++;
/*  613 */             if (p4 == this.binaryBackground) count++;
/*  614 */             if (p6 == this.binaryBackground) count++;
/*  615 */             if (p7 == this.binaryBackground) count++;
/*  616 */             if (p8 == this.binaryBackground) count++;
/*  617 */             if (p9 == this.binaryBackground) count++;
/*  618 */             if (count >= this.binaryCount)
/*  619 */               sum = this.binaryBackground;
/*      */             else
/*  621 */               sum = binaryForeground;
/*      */           }
/*  623 */           break;
/*      */         case 11:
/*  625 */           if (p5 == binaryForeground) {
/*  626 */             sum = binaryForeground;
/*      */           } else {
/*  628 */             count = 0;
/*  629 */             if (p1 == binaryForeground) count++;
/*  630 */             if (p2 == binaryForeground) count++;
/*  631 */             if (p3 == binaryForeground) count++;
/*  632 */             if (p4 == binaryForeground) count++;
/*  633 */             if (p6 == binaryForeground) count++;
/*  634 */             if (p7 == binaryForeground) count++;
/*  635 */             if (p8 == binaryForeground) count++;
/*  636 */             if (p9 == binaryForeground) count++;
/*  637 */             if (count >= this.binaryCount)
/*  638 */               sum = binaryForeground;
/*      */             else
/*  640 */               sum = this.binaryBackground;  } break;
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 8:
/*  645 */         case 9: } this.pixels[(offset++)] = ((byte)sum);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  650 */     if (this.xMin == 1) filterEdge(type, pixels2, this.roiHeight, this.roiX, this.roiY, 0, 1);
/*  651 */     if (this.yMin == 1) filterEdge(type, pixels2, this.roiWidth, this.roiX, this.roiY, 1, 0);
/*  652 */     if (this.xMax == this.width - 2) filterEdge(type, pixels2, this.roiHeight, this.width - 1, this.roiY, 0, 1);
/*  653 */     if (this.yMax == this.height - 2) filterEdge(type, pixels2, this.roiWidth, this.roiX, this.height - 1, 1, 0);
/*      */   }
/*      */ 
/*      */   void filterEdge(int type, byte[] pixels2, int n, int x, int y, int xinc, int yinc)
/*      */   {
/*  659 */     int sum = 0;
/*      */ 
/*  661 */     int binaryForeground = 255 - this.binaryBackground;
/*  662 */     int bg = this.binaryBackground;
/*  663 */     int fg = binaryForeground;
/*      */ 
/*  665 */     for (int i = 0; i < n; i++)
/*      */     {
/*      */       int p9;
/*      */       int p1;
/*      */       int p2;
/*      */       int p3;
/*      */       int p4;
/*      */       int p5;
/*      */       int p6;
/*      */       int p7;
/*      */       int p8;
/*      */       int p9;
/*  666 */       if (((!Prefs.padEdges) && (type == 10)) || (type == 11)) {
/*  667 */         int p1 = getEdgePixel0(pixels2, bg, x - 1, y - 1); int p2 = getEdgePixel0(pixels2, bg, x, y - 1); int p3 = getEdgePixel0(pixels2, bg, x + 1, y - 1);
/*  668 */         int p4 = getEdgePixel0(pixels2, bg, x - 1, y); int p5 = getEdgePixel0(pixels2, bg, x, y); int p6 = getEdgePixel0(pixels2, bg, x + 1, y);
/*  669 */         int p7 = getEdgePixel0(pixels2, bg, x - 1, y + 1); int p8 = getEdgePixel0(pixels2, bg, x, y + 1); p9 = getEdgePixel0(pixels2, bg, x + 1, y + 1);
/*      */       }
/*      */       else
/*      */       {
/*      */         int p9;
/*  670 */         if ((Prefs.padEdges) && (type == 10)) {
/*  671 */           int p1 = getEdgePixel1(pixels2, fg, x - 1, y - 1); int p2 = getEdgePixel1(pixels2, fg, x, y - 1); int p3 = getEdgePixel1(pixels2, fg, x + 1, y - 1);
/*  672 */           int p4 = getEdgePixel1(pixels2, fg, x - 1, y); int p5 = getEdgePixel1(pixels2, fg, x, y); int p6 = getEdgePixel1(pixels2, fg, x + 1, y);
/*  673 */           int p7 = getEdgePixel1(pixels2, fg, x - 1, y + 1); int p8 = getEdgePixel1(pixels2, fg, x, y + 1); p9 = getEdgePixel1(pixels2, fg, x + 1, y + 1);
/*      */         } else {
/*  675 */           p1 = getEdgePixel(pixels2, x - 1, y - 1); p2 = getEdgePixel(pixels2, x, y - 1); p3 = getEdgePixel(pixels2, x + 1, y - 1);
/*  676 */           p4 = getEdgePixel(pixels2, x - 1, y); p5 = getEdgePixel(pixels2, x, y); p6 = getEdgePixel(pixels2, x + 1, y);
/*  677 */           p7 = getEdgePixel(pixels2, x - 1, y + 1); p8 = getEdgePixel(pixels2, x, y + 1); p9 = getEdgePixel(pixels2, x + 1, y + 1);
/*      */         }
/*      */       }
/*      */       int count;
/*  679 */       switch (type) {
/*      */       case 0:
/*  681 */         sum = (p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + 4) / 9;
/*  682 */         break;
/*      */       case 1:
/*  684 */         int sum1 = p1 + 2 * p2 + p3 - p7 - 2 * p8 - p9;
/*  685 */         int sum2 = p1 + 2 * p4 + p7 - p3 - 2 * p6 - p9;
/*  686 */         sum = (int)Math.sqrt(sum1 * sum1 + sum2 * sum2);
/*  687 */         if (sum > 255) sum = 255; break;
/*      */       case 3:
/*  690 */         sum = p5;
/*  691 */         if (p1 < sum) sum = p1;
/*  692 */         if (p2 < sum) sum = p2;
/*  693 */         if (p3 < sum) sum = p3;
/*  694 */         if (p4 < sum) sum = p4;
/*  695 */         if (p6 < sum) sum = p6;
/*  696 */         if (p7 < sum) sum = p7;
/*  697 */         if (p8 < sum) sum = p8;
/*  698 */         if (p9 < sum) sum = p9; break;
/*      */       case 4:
/*  701 */         sum = p5;
/*  702 */         if (p1 > sum) sum = p1;
/*  703 */         if (p2 > sum) sum = p2;
/*  704 */         if (p3 > sum) sum = p3;
/*  705 */         if (p4 > sum) sum = p4;
/*  706 */         if (p6 > sum) sum = p6;
/*  707 */         if (p7 > sum) sum = p7;
/*  708 */         if (p8 > sum) sum = p8;
/*  709 */         if (p9 > sum) sum = p9; break;
/*      */       case 10:
/*  712 */         if (p5 == this.binaryBackground) {
/*  713 */           sum = this.binaryBackground;
/*      */         } else {
/*  715 */           count = 0;
/*  716 */           if (p1 == this.binaryBackground) count++;
/*  717 */           if (p2 == this.binaryBackground) count++;
/*  718 */           if (p3 == this.binaryBackground) count++;
/*  719 */           if (p4 == this.binaryBackground) count++;
/*  720 */           if (p6 == this.binaryBackground) count++;
/*  721 */           if (p7 == this.binaryBackground) count++;
/*  722 */           if (p8 == this.binaryBackground) count++;
/*  723 */           if (p9 == this.binaryBackground) count++;
/*  724 */           if (count >= this.binaryCount)
/*  725 */             sum = this.binaryBackground;
/*      */           else
/*  727 */             sum = binaryForeground;
/*      */         }
/*  729 */         break;
/*      */       case 11:
/*  731 */         if (p5 == binaryForeground) {
/*  732 */           sum = binaryForeground;
/*      */         } else {
/*  734 */           count = 0;
/*  735 */           if (p1 == binaryForeground) count++;
/*  736 */           if (p2 == binaryForeground) count++;
/*  737 */           if (p3 == binaryForeground) count++;
/*  738 */           if (p4 == binaryForeground) count++;
/*  739 */           if (p6 == binaryForeground) count++;
/*  740 */           if (p7 == binaryForeground) count++;
/*  741 */           if (p8 == binaryForeground) count++;
/*  742 */           if (p9 == binaryForeground) count++;
/*  743 */           if (count >= this.binaryCount)
/*  744 */             sum = binaryForeground;
/*      */           else
/*  746 */             sum = this.binaryBackground;  } break;
/*      */       case 2:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  750 */       case 9: } this.pixels[(x + y * this.width)] = ((byte)sum);
/*  751 */       x += xinc; y += yinc;
/*      */     }
/*      */   }
/*      */ 
/*      */   final int getEdgePixel(byte[] pixels2, int x, int y) {
/*  756 */     if (x <= 0) x = 0;
/*  757 */     if (x >= this.width) x = this.width - 1;
/*  758 */     if (y <= 0) y = 0;
/*  759 */     if (y >= this.height) y = this.height - 1;
/*  760 */     return pixels2[(x + y * this.width)] & 0xFF;
/*      */   }
/*      */ 
/*      */   final int getEdgePixel1(byte[] pixels2, int foreground, int x, int y) {
/*  764 */     if ((x < 0) || (x > this.width - 1) || (y < 0) || (y > this.height - 1)) {
/*  765 */       return foreground;
/*      */     }
/*  767 */     return pixels2[(x + y * this.width)] & 0xFF;
/*      */   }
/*      */ 
/*      */   final int getEdgePixel0(byte[] pixels2, int background, int x, int y) {
/*  771 */     if ((x < 0) || (x > this.width - 1) || (y < 0) || (y > this.height - 1)) {
/*  772 */       return background;
/*      */     }
/*  774 */     return pixels2[(x + y * this.width)] & 0xFF;
/*      */   }
/*      */ 
/*      */   public void erode() {
/*  778 */     if (isInvertedLut())
/*  779 */       filter(3);
/*      */     else
/*  781 */       filter(4);
/*      */   }
/*      */ 
/*      */   public void dilate() {
/*  785 */     if (isInvertedLut())
/*  786 */       filter(4);
/*      */     else
/*  788 */       filter(3);
/*      */   }
/*      */ 
/*      */   public void erode(int count, int background) {
/*  792 */     this.binaryCount = count;
/*  793 */     this.binaryBackground = background;
/*  794 */     filter(10);
/*      */   }
/*      */ 
/*      */   public void dilate(int count, int background) {
/*  798 */     this.binaryCount = count;
/*  799 */     this.binaryBackground = background;
/*  800 */     filter(11);
/*      */   }
/*      */ 
/*      */   public void outline() {
/*  804 */     new BinaryProcessor(this).outline();
/*      */   }
/*      */ 
/*      */   public void skeletonize() {
/*  808 */     new BinaryProcessor(this).skeletonize();
/*      */   }
/*      */ 
/*      */   private final int findMedian(int[] values)
/*      */   {
/*  813 */     for (int i = 1; i <= 4; i++) {
/*  814 */       int max = 0;
/*  815 */       int mj = 1;
/*  816 */       for (int j = 1; j <= 9; j++)
/*  817 */         if (values[j] > max) {
/*  818 */           max = values[j];
/*  819 */           mj = j;
/*      */         }
/*  821 */       values[mj] = 0;
/*      */     }
/*  823 */     int max = 0;
/*  824 */     for (int j = 1; j <= 9; j++)
/*  825 */       if (values[j] > max)
/*  826 */         max = values[j];
/*  827 */     return max;
/*      */   }
/*      */ 
/*      */   public void medianFilter() {
/*  831 */     filter(2);
/*      */   }
/*      */ 
/*      */   public void noise(double range) {
/*  835 */     Random rnd = new Random();
/*      */ 
/*  838 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  839 */       int i = y * this.width + this.roiX;
/*  840 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  841 */         boolean inRange = false;
/*      */         do {
/*  843 */           int ran = (int)Math.round(rnd.nextGaussian() * range);
/*  844 */           int v = (this.pixels[i] & 0xFF) + ran;
/*  845 */           inRange = (v >= 0) && (v <= 255);
/*  846 */           if (inRange) this.pixels[i] = ((byte)v); 
/*      */         }
/*  847 */         while (!inRange);
/*  848 */         i++;
/*      */       }
/*  850 */       if (y % 20 == 0)
/*  851 */         showProgress((y - this.roiY) / this.roiHeight);
/*      */     }
/*  853 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void scale(double xScale, double yScale)
/*      */   {
/*  860 */     double xCenter = this.roiX + this.roiWidth / 2.0D;
/*  861 */     double yCenter = this.roiY + this.roiHeight / 2.0D;
/*      */ 
/*  863 */     if ((!this.bgColorSet) && (isInvertedLut())) this.bgColor = 0;
/*      */     int xmin;
/*      */     int xmax;
/*      */     int ymin;
/*      */     int ymax;
/*  865 */     if ((xScale > 1.0D) && (yScale > 1.0D))
/*      */     {
/*  867 */       int xmin = (int)(xCenter - (xCenter - this.roiX) * xScale);
/*  868 */       if (xmin < 0) xmin = 0;
/*  869 */       int xmax = xmin + (int)(this.roiWidth * xScale) - 1;
/*  870 */       if (xmax >= this.width) xmax = this.width - 1;
/*  871 */       int ymin = (int)(yCenter - (yCenter - this.roiY) * yScale);
/*  872 */       if (ymin < 0) ymin = 0;
/*  873 */       int ymax = ymin + (int)(this.roiHeight * yScale) - 1;
/*  874 */       if (ymax >= this.height) ymax = this.height - 1; 
/*      */     }
/*  876 */     else { xmin = this.roiX;
/*  877 */       xmax = this.roiX + this.roiWidth - 1;
/*  878 */       ymin = this.roiY;
/*  879 */       ymax = this.roiY + this.roiHeight - 1;
/*      */     }
/*  881 */     byte[] pixels2 = (byte[])getPixelsCopy();
/*  882 */     ImageProcessor ip2 = null;
/*  883 */     if (this.interpolationMethod == 2) {
/*  884 */       ip2 = new ByteProcessor(getWidth(), getHeight(), pixels2, null);
/*  885 */       ip2.setBackgroundValue(getBackgroundValue());
/*      */     }
/*  887 */     boolean checkCoordinates = (xScale < 1.0D) || (yScale < 1.0D);
/*      */ 
/*  890 */     if (this.interpolationMethod == 2) {
/*  891 */       for (int y = ymin; y <= ymax; y++) {
/*  892 */         double ys = (y - yCenter) / yScale + yCenter;
/*  893 */         int index1 = y * this.width + xmin;
/*  894 */         int index2 = this.width * (int)ys;
/*  895 */         for (int x = xmin; x <= xmax; x++) {
/*  896 */           double xs = (x - xCenter) / xScale + xCenter;
/*  897 */           int value = (int)(getBicubicInterpolatedPixel(xs, ys, ip2) + 0.5D);
/*  898 */           if (value < 0) value = 0;
/*  899 */           if (value > 255) value = 255;
/*  900 */           this.pixels[(index1++)] = ((byte)value);
/*      */         }
/*  902 */         if (y % 30 == 0) showProgress((y - ymin) / this.height); 
/*      */       }
/*      */     }
/*  905 */     else { double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  906 */       double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*  907 */       for (int y = ymin; y <= ymax; y++) {
/*  908 */         double ys = (y - yCenter) / yScale + yCenter;
/*  909 */         int ysi = (int)ys;
/*  910 */         if (ys < 0.0D) ys = 0.0D;
/*  911 */         if (ys >= ylimit) ys = ylimit2;
/*  912 */         int index1 = y * this.width + xmin;
/*  913 */         int index2 = this.width * (int)ys;
/*  914 */         for (int x = xmin; x <= xmax; x++) {
/*  915 */           double xs = (x - xCenter) / xScale + xCenter;
/*  916 */           int xsi = (int)xs;
/*  917 */           if ((checkCoordinates) && ((xsi < xmin) || (xsi > xmax) || (ysi < ymin) || (ysi > ymax))) {
/*  918 */             this.pixels[(index1++)] = ((byte)this.bgColor);
/*      */           }
/*  920 */           else if (this.interpolationMethod == 1) {
/*  921 */             if (xs < 0.0D) xs = 0.0D;
/*  922 */             if (xs >= xlimit) xs = xlimit2;
/*  923 */             this.pixels[(index1++)] = ((byte)((int)(getInterpolatedPixel(xs, ys, pixels2) + 0.5D) & 0xFF));
/*      */           } else {
/*  925 */             this.pixels[(index1++)] = pixels2[(index2 + xsi)];
/*      */           }
/*      */         }
/*  928 */         if (y % 30 == 0)
/*  929 */           showProgress((y - ymin) / this.height);
/*      */       }
/*      */     }
/*  932 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   private final double getInterpolatedPixel(double x, double y, byte[] pixels)
/*      */   {
/*  937 */     int xbase = (int)x;
/*  938 */     int ybase = (int)y;
/*  939 */     double xFraction = x - xbase;
/*  940 */     double yFraction = y - ybase;
/*  941 */     int offset = ybase * this.width + xbase;
/*  942 */     int lowerLeft = pixels[offset] & 0xFF;
/*      */ 
/*  945 */     int lowerRight = pixels[(offset + 1)] & 0xFF;
/*  946 */     int upperRight = pixels[(offset + this.width + 1)] & 0xFF;
/*  947 */     int upperLeft = pixels[(offset + this.width)] & 0xFF;
/*  948 */     double upperAverage = upperLeft + xFraction * (upperRight - upperLeft);
/*  949 */     double lowerAverage = lowerLeft + xFraction * (lowerRight - lowerLeft);
/*  950 */     return lowerAverage + yFraction * (upperAverage - lowerAverage);
/*      */   }
/*      */ 
/*      */   public ImageProcessor resize(int dstWidth, int dstHeight)
/*      */   {
/*  957 */     if ((this.roiWidth == dstWidth) && (this.roiHeight == dstHeight))
/*  958 */       return crop();
/*  959 */     double srcCenterX = this.roiX + this.roiWidth / 2.0D;
/*  960 */     double srcCenterY = this.roiY + this.roiHeight / 2.0D;
/*  961 */     double dstCenterX = dstWidth / 2.0D;
/*  962 */     double dstCenterY = dstHeight / 2.0D;
/*  963 */     double xScale = dstWidth / this.roiWidth;
/*  964 */     double yScale = dstHeight / this.roiHeight;
/*  965 */     if (this.interpolationMethod != 0) {
/*  966 */       dstCenterX += xScale / 2.0D;
/*  967 */       dstCenterY += yScale / 2.0D;
/*      */     }
/*  969 */     ImageProcessor ip2 = createProcessor(dstWidth, dstHeight);
/*  970 */     byte[] pixels2 = (byte[])ip2.getPixels();
/*      */ 
/*  973 */     if (this.interpolationMethod == 2) {
/*  974 */       for (int y = 0; y <= dstHeight - 1; y++) {
/*  975 */         double ys = (y - dstCenterY) / yScale + srcCenterY;
/*  976 */         int index1 = this.width * (int)ys;
/*  977 */         int index2 = y * dstWidth;
/*  978 */         for (int x = 0; x <= dstWidth - 1; x++) {
/*  979 */           double xs = (x - dstCenterX) / xScale + srcCenterX;
/*  980 */           int value = (int)(getBicubicInterpolatedPixel(xs, ys, this) + 0.5D);
/*  981 */           if (value < 0) value = 0;
/*  982 */           if (value > 255) value = 255;
/*  983 */           pixels2[(index2++)] = ((byte)value);
/*      */         }
/*  985 */         if (y % 30 == 0)
/*  986 */           showProgress(y / dstHeight);
/*      */       }
/*      */     } else {
/*  989 */       double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  990 */       double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*  991 */       for (int y = 0; y <= dstHeight - 1; y++) {
/*  992 */         double ys = (y - dstCenterY) / yScale + srcCenterY;
/*  993 */         if (this.interpolationMethod == 1) {
/*  994 */           if (ys < 0.0D) ys = 0.0D;
/*  995 */           if (ys >= ylimit) ys = ylimit2;
/*      */         }
/*  997 */         int index1 = this.width * (int)ys;
/*  998 */         int index2 = y * dstWidth;
/*  999 */         for (int x = 0; x <= dstWidth - 1; x++) {
/* 1000 */           double xs = (x - dstCenterX) / xScale + srcCenterX;
/* 1001 */           if (this.interpolationMethod == 1) {
/* 1002 */             if (xs < 0.0D) xs = 0.0D;
/* 1003 */             if (xs >= xlimit) xs = xlimit2;
/* 1004 */             pixels2[(index2++)] = ((byte)((int)(getInterpolatedPixel(xs, ys, this.pixels) + 0.5D) & 0xFF));
/*      */           } else {
/* 1006 */             pixels2[(index2++)] = this.pixels[(index1 + (int)xs)];
/*      */           }
/*      */         }
/* 1008 */         if (y % 30 == 0)
/* 1009 */           showProgress(y / dstHeight);
/*      */       }
/*      */     }
/* 1012 */     showProgress(1.0D);
/* 1013 */     return ip2;
/*      */   }
/*      */ 
/*      */   public void rotate(double angle)
/*      */   {
/* 1020 */     if (angle % 360.0D == 0.0D)
/* 1021 */       return;
/* 1022 */     byte[] pixels2 = (byte[])getPixelsCopy();
/* 1023 */     ImageProcessor ip2 = null;
/* 1024 */     if (this.interpolationMethod == 2) {
/* 1025 */       ip2 = new ByteProcessor(getWidth(), getHeight(), pixels2, null);
/* 1026 */       ip2.setBackgroundValue(getBackgroundValue());
/*      */     }
/* 1028 */     double centerX = this.roiX + (this.roiWidth - 1) / 2.0D;
/* 1029 */     double centerY = this.roiY + (this.roiHeight - 1) / 2.0D;
/* 1030 */     int xMax = this.roiX + this.roiWidth - 1;
/* 1031 */     if ((!this.bgColorSet) && (isInvertedLut())) this.bgColor = 0;
/*      */ 
/* 1033 */     double angleRadians = -angle / 57.295779513082323D;
/* 1034 */     double ca = Math.cos(angleRadians);
/* 1035 */     double sa = Math.sin(angleRadians);
/* 1036 */     double tmp1 = centerY * sa - centerX * ca;
/* 1037 */     double tmp2 = -centerX * sa - centerY * ca;
/*      */ 
/* 1040 */     double dwidth = this.width; double dheight = this.height;
/* 1041 */     double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/* 1042 */     double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*      */ 
/* 1044 */     if (this.interpolationMethod == 2)
/* 1045 */       for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/* 1046 */         int index = y * this.width + this.roiX;
/* 1047 */         double tmp3 = tmp1 - y * sa + centerX;
/* 1048 */         double tmp4 = tmp2 + y * ca + centerY;
/* 1049 */         for (int x = this.roiX; x <= xMax; x++) {
/* 1050 */           double xs = x * ca + tmp3;
/* 1051 */           double ys = x * sa + tmp4;
/* 1052 */           int value = (int)(getBicubicInterpolatedPixel(xs, ys, ip2) + 0.5D);
/* 1053 */           if (value < 0) value = 0;
/* 1054 */           if (value > 255) value = 255;
/* 1055 */           this.pixels[(index++)] = ((byte)value);
/*      */         }
/* 1057 */         if (y % 30 == 0) showProgress((y - this.roiY) / this.roiHeight);
/*      */       }
/*      */     else {
/* 1060 */       for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/* 1061 */         int index = y * this.width + this.roiX;
/* 1062 */         double tmp3 = tmp1 - y * sa + centerX;
/* 1063 */         double tmp4 = tmp2 + y * ca + centerY;
/* 1064 */         for (int x = this.roiX; x <= xMax; x++) {
/* 1065 */           double xs = x * ca + tmp3;
/* 1066 */           double ys = x * sa + tmp4;
/* 1067 */           if ((xs >= -0.01D) && (xs < dwidth) && (ys >= -0.01D) && (ys < dheight)) {
/* 1068 */             if (this.interpolationMethod == 1) {
/* 1069 */               if (xs < 0.0D) xs = 0.0D;
/* 1070 */               if (xs >= xlimit) xs = xlimit2;
/* 1071 */               if (ys < 0.0D) ys = 0.0D;
/* 1072 */               if (ys >= ylimit) ys = ylimit2;
/* 1073 */               this.pixels[(index++)] = ((byte)(int)(getInterpolatedPixel(xs, ys, pixels2) + 0.5D));
/*      */             } else {
/* 1075 */               int ixs = (int)(xs + 0.5D);
/* 1076 */               int iys = (int)(ys + 0.5D);
/* 1077 */               if (ixs >= this.width) ixs = this.width - 1;
/* 1078 */               if (iys >= this.height) iys = this.height - 1;
/* 1079 */               this.pixels[(index++)] = pixels2[(this.width * iys + ixs)];
/*      */             }
/*      */           }
/* 1082 */           else this.pixels[(index++)] = ((byte)this.bgColor);
/*      */         }
/* 1084 */         if (y % 30 == 0)
/* 1085 */           showProgress((y - this.roiY) / this.roiHeight);
/*      */       }
/*      */     }
/* 1088 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void flipVertical()
/*      */   {
/* 1094 */     for (int y = 0; y < this.roiHeight / 2; y++) {
/* 1095 */       int index1 = (this.roiY + y) * this.width + this.roiX;
/* 1096 */       int index2 = (this.roiY + this.roiHeight - 1 - y) * this.width + this.roiX;
/* 1097 */       for (int i = 0; i < this.roiWidth; i++) {
/* 1098 */         byte tmp = this.pixels[index1];
/* 1099 */         this.pixels[(index1++)] = this.pixels[index2];
/* 1100 */         this.pixels[(index2++)] = tmp;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int[] getHistogram() {
/* 1106 */     if (this.mask != null)
/* 1107 */       return getHistogram(this.mask);
/* 1108 */     int[] histogram = new int[256];
/* 1109 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/* 1110 */       int i = y * this.width + this.roiX;
/* 1111 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/* 1112 */         int v = this.pixels[(i++)] & 0xFF;
/* 1113 */         histogram[v] += 1;
/*      */       }
/*      */     }
/* 1116 */     return histogram;
/*      */   }
/*      */ 
/*      */   public int[] getHistogram(ImageProcessor mask) {
/* 1120 */     int rx = this.roiX; int ry = this.roiY; int rw = this.roiWidth; int rh = this.roiHeight;
/* 1121 */     if ((mask.getWidth() != rw) || (mask.getHeight() != rh)) {
/* 1122 */       throw new IllegalArgumentException(maskSizeError(mask));
/*      */     }
/* 1124 */     int[] histogram = new int[256];
/* 1125 */     byte[] mpixels = (byte[])mask.getPixels();
/* 1126 */     int y = ry; for (int my = 0; y < ry + rh; my++) {
/* 1127 */       int i = y * this.width + rx;
/* 1128 */       int mi = my * rw;
/* 1129 */       for (int x = rx; x < rx + rw; x++) {
/* 1130 */         if (mpixels[(mi++)] != 0) {
/* 1131 */           int v = this.pixels[i] & 0xFF;
/* 1132 */           histogram[v] += 1;
/*      */         }
/* 1134 */         i++;
/*      */       }
/* 1126 */       y++;
/*      */     }
/*      */ 
/* 1137 */     return histogram;
/*      */   }
/*      */ 
/*      */   public void threshold(int level)
/*      */   {
/* 1142 */     for (int i = 0; i < this.width * this.height; i++)
/* 1143 */       if ((this.pixels[i] & 0xFF) <= level)
/* 1144 */         this.pixels[i] = 0;
/*      */       else
/* 1146 */         this.pixels[i] = -1;
/*      */   }
/*      */ 
/*      */   public void applyLut()
/*      */   {
/* 1151 */     if (this.rLUT2 == null)
/* 1152 */       return;
/* 1153 */     if (isInvertedLut())
/* 1154 */       for (int i = 0; i < this.width * this.height; i++)
/* 1155 */         this.pixels[i] = ((byte)(255 - this.rLUT2[(this.pixels[i] & 0xFF)]));
/*      */     else
/* 1157 */       for (int i = 0; i < this.width * this.height; i++)
/* 1158 */         this.pixels[i] = this.rLUT2[(this.pixels[i] & 0xFF)];
/* 1159 */     setMinAndMax(0.0D, 255.0D);
/*      */   }
/*      */ 
/*      */   public void convolve(float[] kernel, int kernelWidth, int kernelHeight)
/*      */   {
/* 1164 */     ImageProcessor ip2 = convertToFloat();
/* 1165 */     ip2.setRoi(getRoi());
/* 1166 */     new Convolver().convolve(ip2, kernel, kernelWidth, kernelHeight);
/* 1167 */     ip2 = ip2.convertToByte(false);
/* 1168 */     byte[] pixels2 = (byte[])ip2.getPixels();
/* 1169 */     System.arraycopy(pixels2, 0, this.pixels, 0, this.pixels.length);
/*      */   }
/*      */ 
/*      */   public FloatProcessor[] toFloatProcessors() {
/* 1173 */     FloatProcessor[] fp = new FloatProcessor[1];
/* 1174 */     fp[0] = ((FloatProcessor)convertToFloat());
/* 1175 */     return fp;
/*      */   }
/*      */ 
/*      */   public void setFromFloatProcessors(FloatProcessor[] fp) {
/* 1179 */     ImageProcessor ip2 = fp[0].convertToByte(false);
/* 1180 */     setPixels(ip2.getPixels());
/*      */   }
/*      */ 
/*      */   public float[][] toFloatArrays() {
/* 1184 */     float[][] a = new float[1][];
/*      */ 
/* 1186 */     ImageProcessor fp = convertToFloat();
/* 1187 */     a[0] = ((float[])(float[])fp.getPixels());
/* 1188 */     return a;
/*      */   }
/*      */ 
/*      */   public void setFromFloatArrays(float[][] arrays) {
/* 1192 */     ImageProcessor ip2 = new FloatProcessor(this.roiWidth, this.roiHeight, arrays[0], null);
/* 1193 */     ip2 = ip2.convertToByte(false);
/* 1194 */     setPixels(ip2.getPixels());
/*      */   }
/*      */ 
/*      */   public FloatProcessor toFloat(int channelNumber, FloatProcessor fp)
/*      */   {
/* 1209 */     int size = this.width * this.height;
/* 1210 */     if ((fp == null) || (fp.getWidth() != this.width) || (fp.getHeight() != this.height))
/* 1211 */       fp = new FloatProcessor(this.width, this.height, new float[size], this.cm);
/* 1212 */     float[] fPixels = (float[])fp.getPixels();
/* 1213 */     for (int i = 0; i < size; i++)
/* 1214 */       fPixels[i] = (this.pixels[i] & 0xFF);
/* 1215 */     fp.setRoi(getRoi());
/* 1216 */     fp.setMask(this.mask);
/* 1217 */     fp.setMinAndMax(this.min, this.max);
/* 1218 */     fp.setThreshold(this.minThreshold, this.maxThreshold, 2);
/* 1219 */     return fp;
/*      */   }
/*      */ 
/*      */   public void setPixels(int channelNumber, FloatProcessor fp)
/*      */   {
/* 1228 */     float[] fPixels = (float[])fp.getPixels();
/*      */ 
/* 1230 */     int size = this.width * this.height;
/* 1231 */     for (int i = 0; i < size; i++) {
/* 1232 */       float value = fPixels[i] + 0.5F;
/* 1233 */       if (value < 0.0F) value = 0.0F;
/* 1234 */       if (value > 255.0F) value = 255.0F;
/* 1235 */       this.pixels[i] = ((byte)(int)value);
/*      */     }
/* 1237 */     setMinAndMax(fp.getMin(), fp.getMax());
/*      */   }
/*      */ 
/*      */   public boolean isBinary()
/*      */   {
/* 1242 */     for (int i = 0; i < this.width * this.height; i++) {
/* 1243 */       if ((this.pixels[i] != 0) && (this.pixels[i] != -1))
/* 1244 */         return false;
/*      */     }
/* 1246 */     return true;
/*      */   }
/*      */ 
/*      */   byte[] create8BitImage() {
/* 1250 */     return this.pixels;
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ByteProcessor
 * JD-Core Version:    0.6.2
 */