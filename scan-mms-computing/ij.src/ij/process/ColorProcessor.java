/*      */ package ij.process;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImageStack;
/*      */ import java.awt.Color;
/*      */ import java.awt.Image;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.DataBuffer;
/*      */ import java.awt.image.DataBufferInt;
/*      */ import java.awt.image.DirectColorModel;
/*      */ import java.awt.image.MemoryImageSource;
/*      */ import java.awt.image.PixelGrabber;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.awt.image.WritableRaster;
/*      */ 
/*      */ public class ColorProcessor extends ImageProcessor
/*      */ {
/*      */   protected int[] pixels;
/*   16 */   protected int[] snapshotPixels = null;
/*   17 */   private int bgColor = -1;
/*   18 */   private int min = 0; private int max = 255;
/*      */   private WritableRaster rgbRaster;
/*      */   private SampleModel rgbSampleModel;
/*   25 */   private static double rWeight = 0.3333333333333333D; private static double gWeight = 0.3333333333333333D; private static double bWeight = 0.3333333333333333D;
/*      */   public static final int RGB_NOISE = 0;
/*      */   public static final int RGB_MEDIAN = 1;
/*      */   public static final int RGB_FIND_EDGES = 2;
/*      */   public static final int RGB_ERODE = 3;
/*      */   public static final int RGB_DILATE = 4;
/*      */   public static final int RGB_THRESHOLD = 5;
/*      */   public static final int RGB_ROTATE = 6;
/*      */   public static final int RGB_SCALE = 7;
/*      */   public static final int RGB_RESIZE = 8;
/*      */   public static final int RGB_TRANSLATE = 9;
/*      */ 
/*      */   public ColorProcessor(Image img)
/*      */   {
/*   29 */     this.width = img.getWidth(null);
/*   30 */     this.height = img.getHeight(null);
/*   31 */     this.pixels = new int[this.width * this.height];
/*   32 */     PixelGrabber pg = new PixelGrabber(img, 0, 0, this.width, this.height, this.pixels, 0, this.width);
/*      */     try {
/*   34 */       pg.grabPixels(); } catch (InterruptedException e) {
/*      */     }
/*   36 */     createColorModel();
/*   37 */     this.fgColor = -16777216;
/*   38 */     resetRoi();
/*      */   }
/*      */ 
/*      */   public ColorProcessor(int width, int height)
/*      */   {
/*   43 */     this(width, height, new int[width * height]);
/*      */   }
/*      */ 
/*      */   public ColorProcessor(int width, int height, int[] pixels)
/*      */   {
/*   48 */     if ((pixels != null) && (width * height != pixels.length))
/*   49 */       throw new IllegalArgumentException("width*height!=pixels.length");
/*   50 */     this.width = width;
/*   51 */     this.height = height;
/*   52 */     createColorModel();
/*   53 */     this.fgColor = -16777216;
/*   54 */     resetRoi();
/*   55 */     this.pixels = pixels;
/*      */   }
/*      */ 
/*      */   void createColorModel()
/*      */   {
/*   60 */     this.cm = new DirectColorModel(24, 16711680, 65280, 255);
/*      */   }
/*      */ 
/*      */   public Image createImage() {
/*   64 */     if (IJ.isJava16())
/*   65 */       return createBufferedImage();
/*   66 */     if (this.source == null) {
/*   67 */       this.source = new MemoryImageSource(this.width, this.height, this.cm, this.pixels, 0, this.width);
/*   68 */       this.source.setAnimated(true);
/*   69 */       this.source.setFullBufferUpdates(true);
/*   70 */       this.img = Toolkit.getDefaultToolkit().createImage(this.source);
/*   71 */     } else if (this.newPixels) {
/*   72 */       this.source.newPixels(this.pixels, this.cm, 0, this.width);
/*   73 */       this.newPixels = false;
/*      */     } else {
/*   75 */       this.source.newPixels();
/*   76 */     }return this.img;
/*      */   }
/*      */ 
/*      */   Image createBufferedImage() {
/*   80 */     if (this.rgbSampleModel == null)
/*   81 */       this.rgbSampleModel = getRGBSampleModel();
/*   82 */     if (this.rgbRaster == null) {
/*   83 */       DataBuffer dataBuffer = new DataBufferInt(this.pixels, this.width * this.height, 0);
/*   84 */       this.rgbRaster = Raster.createWritableRaster(this.rgbSampleModel, dataBuffer, null);
/*      */     }
/*   86 */     if (this.image == null) {
/*   87 */       this.image = new BufferedImage(this.cm, this.rgbRaster, false, null);
/*      */     }
/*   89 */     return this.image;
/*      */   }
/*      */ 
/*      */   SampleModel getRGBSampleModel() {
/*   93 */     WritableRaster wr = this.cm.createCompatibleWritableRaster(1, 1);
/*   94 */     SampleModel sampleModel = wr.getSampleModel();
/*   95 */     sampleModel = sampleModel.createCompatibleSampleModel(this.width, this.height);
/*   96 */     return sampleModel;
/*      */   }
/*      */ 
/*      */   public ImageProcessor createProcessor(int width, int height)
/*      */   {
/*  101 */     ImageProcessor ip2 = new ColorProcessor(width, height);
/*  102 */     ip2.setInterpolationMethod(this.interpolationMethod);
/*  103 */     return ip2;
/*      */   }
/*      */ 
/*      */   public Color getColor(int x, int y) {
/*  107 */     int c = this.pixels[(y * this.width + x)];
/*  108 */     int r = (c & 0xFF0000) >> 16;
/*  109 */     int g = (c & 0xFF00) >> 8;
/*  110 */     int b = c & 0xFF;
/*  111 */     return new Color(r, g, b);
/*      */   }
/*      */ 
/*      */   public void setColor(Color color)
/*      */   {
/*  117 */     this.fgColor = color.getRGB();
/*  118 */     this.drawingColor = color;
/*      */   }
/*      */ 
/*      */   public void setColor(int color)
/*      */   {
/*  124 */     this.fgColor = color;
/*      */   }
/*      */ 
/*      */   public void setValue(double value)
/*      */   {
/*  129 */     this.fgColor = ((int)value);
/*      */   }
/*      */ 
/*      */   public void setBackgroundValue(double value)
/*      */   {
/*  134 */     this.bgColor = ((int)value);
/*      */   }
/*      */ 
/*      */   public double getBackgroundValue()
/*      */   {
/*  139 */     return this.bgColor;
/*      */   }
/*      */ 
/*      */   public double getMin()
/*      */   {
/*  144 */     return this.min;
/*      */   }
/*      */ 
/*      */   public double getMax()
/*      */   {
/*  150 */     return this.max;
/*      */   }
/*      */ 
/*      */   public void setMinAndMax(double min, double max)
/*      */   {
/*  156 */     setMinAndMax(min, max, 7);
/*      */   }
/*      */ 
/*      */   public void setMinAndMax(double min, double max, int channels) {
/*  160 */     if (max < min)
/*  161 */       return;
/*  162 */     this.min = ((int)min);
/*  163 */     this.max = ((int)max);
/*      */ 
/*  165 */     int[] lut = new int[256];
/*  166 */     for (int i = 0; i < 256; i++) {
/*  167 */       int v = i - this.min;
/*  168 */       v = (int)(256.0D * v / (max - min));
/*  169 */       if (v < 0)
/*  170 */         v = 0;
/*  171 */       if (v > 255)
/*  172 */         v = 255;
/*  173 */       lut[i] = v;
/*      */     }
/*  175 */     reset();
/*  176 */     if (channels == 7)
/*  177 */       applyTable(lut);
/*      */     else
/*  179 */       applyTable(lut, channels);
/*      */   }
/*      */ 
/*      */   public void snapshot()
/*      */   {
/*  184 */     this.snapshotWidth = this.width;
/*  185 */     this.snapshotHeight = this.height;
/*  186 */     if ((this.snapshotPixels == null) || ((this.snapshotPixels != null) && (this.snapshotPixels.length != this.pixels.length)))
/*  187 */       this.snapshotPixels = new int[this.width * this.height];
/*  188 */     System.arraycopy(this.pixels, 0, this.snapshotPixels, 0, this.width * this.height);
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/*  193 */     if (this.snapshotPixels == null)
/*  194 */       return;
/*  195 */     System.arraycopy(this.snapshotPixels, 0, this.pixels, 0, this.width * this.height);
/*      */   }
/*      */ 
/*      */   public void reset(ImageProcessor mask)
/*      */   {
/*  200 */     if ((mask == null) || (this.snapshotPixels == null))
/*  201 */       return;
/*  202 */     if ((mask.getWidth() != this.roiWidth) || (mask.getHeight() != this.roiHeight))
/*  203 */       throw new IllegalArgumentException(maskSizeError(mask));
/*  204 */     byte[] mpixels = (byte[])mask.getPixels();
/*  205 */     int y = this.roiY; for (int my = 0; y < this.roiY + this.roiHeight; my++) {
/*  206 */       int i = y * this.width + this.roiX;
/*  207 */       int mi = my * this.roiWidth;
/*  208 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  209 */         if (mpixels[(mi++)] == 0)
/*  210 */           this.pixels[i] = this.snapshotPixels[i];
/*  211 */         i++;
/*      */       }
/*  205 */       y++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void swapPixelArrays()
/*      */   {
/*  218 */     if (this.snapshotPixels == null) return;
/*      */ 
/*  220 */     for (int i = 0; i < this.pixels.length; i++) {
/*  221 */       int pixel = this.pixels[i];
/*  222 */       this.pixels[i] = this.snapshotPixels[i];
/*  223 */       this.snapshotPixels[i] = pixel;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setSnapshotPixels(Object pixels) {
/*  228 */     this.snapshotPixels = ((int[])pixels);
/*  229 */     this.snapshotWidth = this.width;
/*  230 */     this.snapshotHeight = this.height;
/*      */   }
/*      */ 
/*      */   public Object getSnapshotPixels()
/*      */   {
/*  235 */     return this.snapshotPixels;
/*      */   }
/*      */ 
/*      */   public void fill(ImageProcessor mask)
/*      */   {
/*  241 */     if (mask == null) {
/*  242 */       fill(); return;
/*  243 */     }int roiWidth = this.roiWidth; int roiHeight = this.roiHeight;
/*  244 */     int roiX = this.roiX; int roiY = this.roiY;
/*  245 */     if ((mask.getWidth() != roiWidth) || (mask.getHeight() != roiHeight))
/*  246 */       return;
/*  247 */     byte[] mpixels = (byte[])mask.getPixels();
/*  248 */     int y = roiY; for (int my = 0; y < roiY + roiHeight; my++) {
/*  249 */       int i = y * this.width + roiX;
/*  250 */       int mi = my * roiWidth;
/*  251 */       for (int x = roiX; x < roiX + roiWidth; x++) {
/*  252 */         if (mpixels[(mi++)] != 0)
/*  253 */           this.pixels[i] = this.fgColor;
/*  254 */         i++;
/*      */       }
/*  248 */       y++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getPixelsCopy()
/*      */   {
/*  265 */     if ((this.snapshotPixels != null) && (this.snapshotCopyMode)) {
/*  266 */       this.snapshotCopyMode = false;
/*  267 */       return this.snapshotPixels;
/*      */     }
/*  269 */     int[] pixels2 = new int[this.width * this.height];
/*  270 */     System.arraycopy(this.pixels, 0, pixels2, 0, this.width * this.height);
/*  271 */     return pixels2;
/*      */   }
/*      */ 
/*      */   public int getPixel(int x, int y)
/*      */   {
/*  276 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  277 */       return this.pixels[(y * this.width + x)];
/*      */     }
/*  279 */     return 0;
/*      */   }
/*      */ 
/*      */   public final int get(int x, int y) {
/*  283 */     return this.pixels[(y * this.width + x)];
/*      */   }
/*      */ 
/*      */   public final void set(int x, int y, int value) {
/*  287 */     this.pixels[(y * this.width + x)] = value;
/*      */   }
/*      */ 
/*      */   public final int get(int index) {
/*  291 */     return this.pixels[index];
/*      */   }
/*      */   public final void set(int index, int value) {
/*  294 */     this.pixels[index] = value;
/*      */   }
/*      */ 
/*      */   public final float getf(int x, int y) {
/*  298 */     return getf(y * this.width + x);
/*      */   }
/*      */ 
/*      */   public final void setf(int x, int y, float value) {
/*  302 */     this.pixels[(y * this.width + x)] = ((int)value);
/*      */   }
/*      */ 
/*      */   public final float getf(int index) {
/*  306 */     int c = this.pixels[index];
/*  307 */     int r = (c & 0xFF0000) >> 16;
/*  308 */     int g = (c & 0xFF00) >> 8;
/*  309 */     int b = c & 0xFF;
/*  310 */     return (float)(r * rWeight + g * gWeight + b * bWeight);
/*      */   }
/*      */ 
/*      */   public final void setf(int index, float value)
/*      */   {
/*  318 */     this.pixels[index] = ((int)value);
/*      */   }
/*      */ 
/*      */   public int[] getPixel(int x, int y, int[] iArray)
/*      */   {
/*  325 */     if (iArray == null) iArray = new int[3];
/*  326 */     int c = getPixel(x, y);
/*  327 */     iArray[0] = ((c & 0xFF0000) >> 16);
/*  328 */     iArray[1] = ((c & 0xFF00) >> 8);
/*  329 */     iArray[2] = (c & 0xFF);
/*  330 */     return iArray;
/*      */   }
/*      */ 
/*      */   public final void putPixel(int x, int y, int[] iArray)
/*      */   {
/*  336 */     int r = iArray[0]; int g = iArray[1]; int b = iArray[2];
/*  337 */     putPixel(x, y, (r << 16) + (g << 8) + b);
/*      */   }
/*      */ 
/*      */   public double getInterpolatedPixel(double x, double y)
/*      */   {
/*  342 */     int ix = (int)(x + 0.5D);
/*  343 */     int iy = (int)(y + 0.5D);
/*  344 */     if (ix < 0) ix = 0;
/*  345 */     if (ix >= this.width) ix = this.width - 1;
/*  346 */     if (iy < 0) iy = 0;
/*  347 */     if (iy >= this.height) iy = this.height - 1;
/*  348 */     return getPixelValue(ix, iy);
/*      */   }
/*      */ 
/*      */   public final int getPixelInterpolated(double x, double y) {
/*  352 */     if ((x < 0.0D) || (y < 0.0D) || (x >= this.width - 1) || (y >= this.height - 1)) {
/*  353 */       return 0;
/*      */     }
/*  355 */     return getInterpolatedPixel(x, y, this.pixels);
/*      */   }
/*      */ 
/*      */   public final void putPixel(int x, int y, int value)
/*      */   {
/*  360 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height))
/*  361 */       this.pixels[(y * this.width + x)] = value;
/*      */   }
/*      */ 
/*      */   public void putPixelValue(int x, int y, double value)
/*      */   {
/*  368 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  369 */       if (value > 255.0D)
/*  370 */         value = 255.0D;
/*  371 */       else if (value < 0.0D)
/*  372 */         value = 0.0D;
/*  373 */       int gray = (int)(value + 0.5D);
/*  374 */       this.pixels[(y * this.width + x)] = (-16777216 + (gray << 16) + (gray << 8) + gray);
/*      */     }
/*      */   }
/*      */ 
/*      */   public float getPixelValue(int x, int y)
/*      */   {
/*  384 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)) {
/*  385 */       int c = this.pixels[(y * this.width + x)];
/*  386 */       int r = (c & 0xFF0000) >> 16;
/*  387 */       int g = (c & 0xFF00) >> 8;
/*  388 */       int b = c & 0xFF;
/*  389 */       return (float)(r * rWeight + g * gWeight + b * bWeight);
/*      */     }
/*      */ 
/*  392 */     return 0.0F;
/*      */   }
/*      */ 
/*      */   public void drawPixel(int x, int y)
/*      */   {
/*  398 */     if ((x >= this.clipXMin) && (x <= this.clipXMax) && (y >= this.clipYMin) && (y <= this.clipYMax))
/*  399 */       this.pixels[(y * this.width + x)] = this.fgColor;
/*      */   }
/*      */ 
/*      */   public Object getPixels()
/*      */   {
/*  406 */     return this.pixels;
/*      */   }
/*      */ 
/*      */   public void setPixels(Object pixels)
/*      */   {
/*  411 */     this.pixels = ((int[])pixels);
/*  412 */     resetPixels(pixels);
/*  413 */     if (pixels == null) this.snapshotPixels = null;
/*  414 */     this.rgbRaster = null;
/*  415 */     this.image = null;
/*      */   }
/*      */ 
/*      */   public void getHSB(byte[] H, byte[] S, byte[] B)
/*      */   {
/*  422 */     float[] hsb = new float[3];
/*  423 */     for (int i = 0; i < this.width * this.height; i++) {
/*  424 */       int c = this.pixels[i];
/*  425 */       int r = (c & 0xFF0000) >> 16;
/*  426 */       int g = (c & 0xFF00) >> 8;
/*  427 */       int b = c & 0xFF;
/*  428 */       hsb = Color.RGBtoHSB(r, g, b, hsb);
/*  429 */       H[i] = ((byte)(int)(hsb[0] * 255.0D));
/*  430 */       S[i] = ((byte)(int)(hsb[1] * 255.0D));
/*  431 */       B[i] = ((byte)(int)(hsb[2] * 255.0D));
/*      */     }
/*      */   }
/*      */ 
/*      */   public ImageStack getHSBStack()
/*      */   {
/*  438 */     int width = getWidth();
/*  439 */     int height = getHeight();
/*  440 */     byte[] H = new byte[width * height];
/*  441 */     byte[] S = new byte[width * height];
/*  442 */     byte[] B = new byte[width * height];
/*  443 */     getHSB(H, S, B);
/*  444 */     ColorModel cm = getDefaultColorModel();
/*  445 */     ImageStack stack = new ImageStack(width, height, cm);
/*  446 */     stack.addSlice("Hue", H);
/*  447 */     stack.addSlice("Saturation", S);
/*  448 */     stack.addSlice("Brightness", B);
/*  449 */     return stack;
/*      */   }
/*      */ 
/*      */   public FloatProcessor getBrightness()
/*      */   {
/*  455 */     int size = this.width * this.height;
/*  456 */     float[] brightness = new float[size];
/*  457 */     float[] hsb = new float[3];
/*  458 */     for (int i = 0; i < size; i++) {
/*  459 */       int c = this.pixels[i];
/*  460 */       int r = (c & 0xFF0000) >> 16;
/*  461 */       int g = (c & 0xFF00) >> 8;
/*  462 */       int b = c & 0xFF;
/*  463 */       hsb = Color.RGBtoHSB(r, g, b, hsb);
/*  464 */       brightness[i] = hsb[2];
/*      */     }
/*  466 */     return new FloatProcessor(this.width, this.height, brightness, null);
/*      */   }
/*      */ 
/*      */   public void getRGB(byte[] R, byte[] G, byte[] B)
/*      */   {
/*  472 */     for (int i = 0; i < this.width * this.height; i++) {
/*  473 */       int c = this.pixels[i];
/*  474 */       int r = (c & 0xFF0000) >> 16;
/*  475 */       int g = (c & 0xFF00) >> 8;
/*  476 */       int b = c & 0xFF;
/*  477 */       R[i] = ((byte)r);
/*  478 */       G[i] = ((byte)g);
/*  479 */       B[i] = ((byte)b);
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte[] getChannel(int channel)
/*      */   {
/*  485 */     int size = this.width * this.height;
/*  486 */     byte[] bytes = new byte[size];
/*      */ 
/*  488 */     switch (channel) {
/*      */     case 1:
/*  490 */       for (int i = 0; i < size; i++) {
/*  491 */         int c = this.pixels[i];
/*  492 */         int r = (c & 0xFF0000) >> 16;
/*  493 */         bytes[i] = ((byte)r);
/*      */       }
/*  495 */       break;
/*      */     case 2:
/*  497 */       for (int i = 0; i < size; i++) {
/*  498 */         int c = this.pixels[i];
/*  499 */         int g = (c & 0xFF00) >> 8;
/*  500 */         bytes[i] = ((byte)g);
/*      */       }
/*  502 */       break;
/*      */     case 3:
/*  504 */       for (int i = 0; i < size; i++) {
/*  505 */         int c = this.pixels[i];
/*  506 */         int b = c & 0xFF;
/*  507 */         bytes[i] = ((byte)b);
/*      */       }
/*      */     }
/*      */ 
/*  511 */     return bytes;
/*      */   }
/*      */ 
/*      */   public void setRGB(byte[] R, byte[] G, byte[] B)
/*      */   {
/*  517 */     for (int i = 0; i < this.width * this.height; i++)
/*  518 */       this.pixels[i] = (0xFF000000 | (R[i] & 0xFF) << 16 | (G[i] & 0xFF) << 8 | B[i] & 0xFF);
/*      */   }
/*      */ 
/*      */   public void setHSB(byte[] H, byte[] S, byte[] B)
/*      */   {
/*  525 */     for (int i = 0; i < this.width * this.height; i++) {
/*  526 */       float hue = (float)((H[i] & 0xFF) / 255.0D);
/*  527 */       float saturation = (float)((S[i] & 0xFF) / 255.0D);
/*  528 */       float brightness = (float)((B[i] & 0xFF) / 255.0D);
/*  529 */       this.pixels[i] = Color.HSBtoRGB(hue, saturation, brightness);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBrightness(FloatProcessor fp)
/*      */   {
/*  536 */     int size = this.width * this.height;
/*  537 */     float[] hsb = new float[3];
/*  538 */     float[] brightness = (float[])fp.getPixels();
/*  539 */     if (brightness.length != size)
/*  540 */       throw new IllegalArgumentException("fp is wrong size");
/*  541 */     for (int i = 0; i < size; i++) {
/*  542 */       int c = this.pixels[i];
/*  543 */       int r = (c & 0xFF0000) >> 16;
/*  544 */       int g = (c & 0xFF00) >> 8;
/*  545 */       int b = c & 0xFF;
/*  546 */       hsb = Color.RGBtoHSB(r, g, b, hsb);
/*  547 */       float bvalue = brightness[i];
/*  548 */       if (bvalue < 0.0F) bvalue = 0.0F;
/*  549 */       if (bvalue > 1.0F) bvalue = 1.0F;
/*  550 */       this.pixels[i] = Color.HSBtoRGB(hsb[0], hsb[1], bvalue);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void copyBits(ImageProcessor ip, int xloc, int yloc, int mode)
/*      */   {
/*  557 */     ip = ip.convertToRGB();
/*  558 */     new ColorBlitter(this).copyBits(ip, xloc, yloc, mode);
/*      */   }
/*      */ 
/*      */   public void applyTable(int[] lut)
/*      */   {
/*  565 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  566 */       int i = y * this.width + this.roiX;
/*  567 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  568 */         int c = this.pixels[i];
/*  569 */         int r = lut[((c & 0xFF0000) >> 16)];
/*  570 */         int g = lut[((c & 0xFF00) >> 8)];
/*  571 */         int b = lut[(c & 0xFF)];
/*  572 */         this.pixels[i] = (-16777216 + (r << 16) + (g << 8) + b);
/*  573 */         i++;
/*      */       }
/*      */     }
/*  576 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void applyTable(int[] lut, int channels) {
/*  580 */     int r = 0; int g = 0; int b = 0;
/*  581 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  582 */       int i = y * this.width + this.roiX;
/*  583 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/*  584 */         int c = this.pixels[i];
/*  585 */         if (channels == 4) {
/*  586 */           r = lut[((c & 0xFF0000) >> 16)];
/*  587 */           g = (c & 0xFF00) >> 8;
/*  588 */           b = c & 0xFF;
/*  589 */         } else if (channels == 2) {
/*  590 */           r = (c & 0xFF0000) >> 16;
/*  591 */           g = lut[((c & 0xFF00) >> 8)];
/*  592 */           b = c & 0xFF;
/*  593 */         } else if (channels == 1) {
/*  594 */           r = (c & 0xFF0000) >> 16;
/*  595 */           g = (c & 0xFF00) >> 8;
/*  596 */           b = lut[(c & 0xFF)];
/*  597 */         } else if ((channels & 0x6) == 6) {
/*  598 */           r = lut[((c & 0xFF0000) >> 16)];
/*  599 */           g = lut[((c & 0xFF00) >> 8)];
/*  600 */           b = c & 0xFF;
/*  601 */         } else if ((channels & 0x5) == 5) {
/*  602 */           r = lut[((c & 0xFF0000) >> 16)];
/*  603 */           g = (c & 0xFF00) >> 8;
/*  604 */           b = lut[(c & 0xFF)];
/*  605 */         } else if ((channels & 0x3) == 3) {
/*  606 */           r = (c & 0xFF0000) >> 16;
/*  607 */           g = lut[((c & 0xFF00) >> 8)];
/*  608 */           b = lut[(c & 0xFF)];
/*      */         }
/*  610 */         this.pixels[i] = (-16777216 + (r << 16) + (g << 8) + b);
/*  611 */         i++;
/*      */       }
/*      */     }
/*  614 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void fill()
/*      */   {
/*  619 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  620 */       int i = y * this.width + this.roiX;
/*  621 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++)
/*  622 */         this.pixels[(i++)] = this.fgColor;
/*  623 */       if (y % 20 == 0)
/*  624 */         showProgress((y - this.roiY) / this.roiHeight);
/*      */     }
/*  626 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void filterRGB(int type, double arg)
/*      */   {
/*  635 */     filterRGB(type, arg, 0.0D);
/*      */   }
/*      */ 
/*      */   final ImageProcessor filterRGB(int type, double arg, double arg2) {
/*  639 */     showProgress(0.01D);
/*  640 */     byte[] R = new byte[this.width * this.height];
/*  641 */     byte[] G = new byte[this.width * this.height];
/*  642 */     byte[] B = new byte[this.width * this.height];
/*  643 */     getRGB(R, G, B);
/*  644 */     Rectangle roi = new Rectangle(this.roiX, this.roiY, this.roiWidth, this.roiHeight);
/*      */ 
/*  646 */     ByteProcessor r = new ByteProcessor(this.width, this.height, R, null);
/*  647 */     r.setRoi(roi);
/*  648 */     ByteProcessor g = new ByteProcessor(this.width, this.height, G, null);
/*  649 */     g.setRoi(roi);
/*  650 */     ByteProcessor b = new ByteProcessor(this.width, this.height, B, null);
/*  651 */     b.setRoi(roi);
/*  652 */     r.setBackgroundValue((this.bgColor & 0xFF0000) >> 16);
/*  653 */     g.setBackgroundValue((this.bgColor & 0xFF00) >> 8);
/*  654 */     b.setBackgroundValue(this.bgColor & 0xFF);
/*  655 */     r.setInterpolationMethod(this.interpolationMethod);
/*  656 */     g.setInterpolationMethod(this.interpolationMethod);
/*  657 */     b.setInterpolationMethod(this.interpolationMethod);
/*      */ 
/*  659 */     showProgress(0.15D);
/*  660 */     switch (type) {
/*      */     case 0:
/*  662 */       r.noise(arg); showProgress(0.4D);
/*  663 */       g.noise(arg); showProgress(0.65D);
/*  664 */       b.noise(arg); showProgress(0.9D);
/*  665 */       break;
/*      */     case 1:
/*  667 */       r.medianFilter(); showProgress(0.4D);
/*  668 */       g.medianFilter(); showProgress(0.65D);
/*  669 */       b.medianFilter(); showProgress(0.9D);
/*  670 */       break;
/*      */     case 2:
/*  672 */       r.findEdges(); showProgress(0.4D);
/*  673 */       g.findEdges(); showProgress(0.65D);
/*  674 */       b.findEdges(); showProgress(0.9D);
/*  675 */       break;
/*      */     case 3:
/*  677 */       r.erode(); showProgress(0.4D);
/*  678 */       g.erode(); showProgress(0.65D);
/*  679 */       b.erode(); showProgress(0.9D);
/*  680 */       break;
/*      */     case 4:
/*  682 */       r.dilate(); showProgress(0.4D);
/*  683 */       g.dilate(); showProgress(0.65D);
/*  684 */       b.dilate(); showProgress(0.9D);
/*  685 */       break;
/*      */     case 5:
/*  687 */       r.autoThreshold(); showProgress(0.4D);
/*  688 */       g.autoThreshold(); showProgress(0.65D);
/*  689 */       b.autoThreshold(); showProgress(0.9D);
/*  690 */       break;
/*      */     case 6:
/*  692 */       IJ.showStatus("Rotating red");
/*  693 */       r.rotate(arg); showProgress(0.4D);
/*  694 */       IJ.showStatus("Rotating green");
/*  695 */       g.rotate(arg); showProgress(0.65D);
/*  696 */       IJ.showStatus("Rotating blue");
/*  697 */       b.rotate(arg); showProgress(0.9D);
/*  698 */       break;
/*      */     case 7:
/*  700 */       IJ.showStatus("Scaling red");
/*  701 */       r.scale(arg, arg2); showProgress(0.4D);
/*  702 */       IJ.showStatus("Scaling green");
/*  703 */       g.scale(arg, arg2); showProgress(0.65D);
/*  704 */       IJ.showStatus("Scaling blue");
/*  705 */       b.scale(arg, arg2); showProgress(0.9D);
/*  706 */       break;
/*      */     case 8:
/*  708 */       int w = (int)arg; int h = (int)arg2;
/*  709 */       IJ.showStatus("Resizing red");
/*  710 */       ImageProcessor r2 = r.resize(w, h); showProgress(0.4D);
/*  711 */       IJ.showStatus("Resizing green");
/*  712 */       ImageProcessor g2 = g.resize(w, h); showProgress(0.65D);
/*  713 */       IJ.showStatus("Resizing blue");
/*  714 */       ImageProcessor b2 = b.resize(w, h); showProgress(0.9D);
/*  715 */       R = (byte[])r2.getPixels();
/*  716 */       G = (byte[])g2.getPixels();
/*  717 */       B = (byte[])b2.getPixels();
/*  718 */       ColorProcessor ip2 = new ColorProcessor(w, h);
/*  719 */       ip2.setRGB(R, G, B);
/*  720 */       showProgress(1.0D);
/*  721 */       return ip2;
/*      */     case 9:
/*  723 */       IJ.showStatus("Translating red");
/*  724 */       r.translate(arg, arg2); showProgress(0.4D);
/*  725 */       IJ.showStatus("Translating green");
/*  726 */       g.translate(arg, arg2); showProgress(0.65D);
/*  727 */       IJ.showStatus("Translating blue");
/*  728 */       b.translate(arg, arg2); showProgress(0.9D);
/*      */     }
/*      */ 
/*  732 */     R = (byte[])r.getPixels();
/*  733 */     G = (byte[])g.getPixels();
/*  734 */     B = (byte[])b.getPixels();
/*      */ 
/*  736 */     setRGB(R, G, B);
/*  737 */     showProgress(1.0D);
/*  738 */     return null;
/*      */   }
/*      */ 
/*      */   public void noise(double range) {
/*  742 */     filterRGB(0, range);
/*      */   }
/*      */ 
/*      */   public void medianFilter() {
/*  746 */     filterRGB(1, 0.0D);
/*      */   }
/*      */ 
/*      */   public void findEdges() {
/*  750 */     filterRGB(2, 0.0D);
/*      */   }
/*      */ 
/*      */   public void erode() {
/*  754 */     filterRGB(3, 0.0D);
/*      */   }
/*      */ 
/*      */   public void dilate() {
/*  758 */     filterRGB(4, 0.0D);
/*      */   }
/*      */ 
/*      */   public void autoThreshold()
/*      */   {
/*  763 */     filterRGB(5, 0.0D);
/*      */   }
/*      */ 
/*      */   public void scale(double xScale, double yScale)
/*      */   {
/*  770 */     if (this.interpolationMethod == 2) {
/*  771 */       filterRGB(7, xScale, yScale);
/*  772 */       return;
/*      */     }
/*  774 */     double xCenter = this.roiX + this.roiWidth / 2.0D;
/*  775 */     double yCenter = this.roiY + this.roiHeight / 2.0D;
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
/*  794 */     int[] pixels2 = (int[])getPixelsCopy();
/*  795 */     boolean checkCoordinates = (xScale < 1.0D) || (yScale < 1.0D);
/*      */ 
/*  798 */     double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  799 */     double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*  800 */     for (int y = ymin; y <= ymax; y++) {
/*  801 */       double ys = (y - yCenter) / yScale + yCenter;
/*  802 */       int ysi = (int)ys;
/*  803 */       if (ys < 0.0D) ys = 0.0D;
/*  804 */       if (ys >= ylimit) ys = ylimit2;
/*  805 */       int index1 = y * this.width + xmin;
/*  806 */       int index2 = this.width * (int)ys;
/*  807 */       for (int x = xmin; x <= xmax; x++) {
/*  808 */         double xs = (x - xCenter) / xScale + xCenter;
/*  809 */         int xsi = (int)xs;
/*  810 */         if ((checkCoordinates) && ((xsi < xmin) || (xsi > xmax) || (ysi < ymin) || (ysi > ymax))) {
/*  811 */           this.pixels[(index1++)] = this.bgColor;
/*      */         }
/*  813 */         else if (this.interpolationMethod == 1) {
/*  814 */           if (xs < 0.0D) xs = 0.0D;
/*  815 */           if (xs >= xlimit) xs = xlimit2;
/*  816 */           this.pixels[(index1++)] = getInterpolatedPixel(xs, ys, pixels2);
/*      */         } else {
/*  818 */           this.pixels[(index1++)] = pixels2[(index2 + xsi)];
/*      */         }
/*      */       }
/*  821 */       if (y % 20 == 0)
/*  822 */         showProgress((y - ymin) / this.height);
/*      */     }
/*  824 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public ImageProcessor crop() {
/*  828 */     int[] pixels2 = new int[this.roiWidth * this.roiHeight];
/*  829 */     for (int ys = this.roiY; ys < this.roiY + this.roiHeight; ys++) {
/*  830 */       int offset1 = (ys - this.roiY) * this.roiWidth;
/*  831 */       int offset2 = ys * this.width + this.roiX;
/*  832 */       for (int xs = 0; xs < this.roiWidth; xs++)
/*  833 */         pixels2[(offset1++)] = this.pixels[(offset2++)];
/*      */     }
/*  835 */     return new ColorProcessor(this.roiWidth, this.roiHeight, pixels2);
/*      */   }
/*      */ 
/*      */   public synchronized ImageProcessor duplicate()
/*      */   {
/*  840 */     int[] pixels2 = new int[this.width * this.height];
/*  841 */     System.arraycopy(this.pixels, 0, pixels2, 0, this.width * this.height);
/*  842 */     return new ColorProcessor(this.width, this.height, pixels2);
/*      */   }
/*      */ 
/*      */   public int getInterpolatedRGBPixel(double x, double y)
/*      */   {
/*  847 */     if ((this.width == 1) || (this.height == 1))
/*  848 */       return getPixel((int)x, (int)y);
/*  849 */     if (x < 0.0D) x = 0.0D;
/*  850 */     if (x >= this.width - 1.0D)
/*  851 */       x = this.width - 1.001D;
/*  852 */     if (y < 0.0D) y = 0.0D;
/*  853 */     if (y >= this.height - 1.0D) y = this.height - 1.001D;
/*  854 */     return getInterpolatedPixel(x, y, this.pixels);
/*      */   }
/*      */ 
/*      */   private final int getInterpolatedPixel(double x, double y, int[] pixels)
/*      */   {
/*  859 */     int xbase = (int)x;
/*  860 */     int ybase = (int)y;
/*  861 */     double xFraction = x - xbase;
/*  862 */     double yFraction = y - ybase;
/*  863 */     int offset = ybase * this.width + xbase;
/*      */ 
/*  865 */     int lowerLeft = pixels[offset];
/*  866 */     int rll = (lowerLeft & 0xFF0000) >> 16;
/*  867 */     int gll = (lowerLeft & 0xFF00) >> 8;
/*  868 */     int bll = lowerLeft & 0xFF;
/*      */ 
/*  870 */     int lowerRight = pixels[(offset + 1)];
/*  871 */     int rlr = (lowerRight & 0xFF0000) >> 16;
/*  872 */     int glr = (lowerRight & 0xFF00) >> 8;
/*  873 */     int blr = lowerRight & 0xFF;
/*      */ 
/*  875 */     int upperRight = pixels[(offset + this.width + 1)];
/*  876 */     int rur = (upperRight & 0xFF0000) >> 16;
/*  877 */     int gur = (upperRight & 0xFF00) >> 8;
/*  878 */     int bur = upperRight & 0xFF;
/*      */ 
/*  880 */     int upperLeft = pixels[(offset + this.width)];
/*  881 */     int rul = (upperLeft & 0xFF0000) >> 16;
/*  882 */     int gul = (upperLeft & 0xFF00) >> 8;
/*  883 */     int bul = upperLeft & 0xFF;
/*      */ 
/*  887 */     double upperAverage = rul + xFraction * (rur - rul);
/*  888 */     double lowerAverage = rll + xFraction * (rlr - rll);
/*  889 */     int r = (int)(lowerAverage + yFraction * (upperAverage - lowerAverage) + 0.5D);
/*  890 */     upperAverage = gul + xFraction * (gur - gul);
/*  891 */     lowerAverage = gll + xFraction * (glr - gll);
/*  892 */     int g = (int)(lowerAverage + yFraction * (upperAverage - lowerAverage) + 0.5D);
/*  893 */     upperAverage = bul + xFraction * (bur - bul);
/*  894 */     lowerAverage = bll + xFraction * (blr - bll);
/*  895 */     int b = (int)(lowerAverage + yFraction * (upperAverage - lowerAverage) + 0.5D);
/*      */ 
/*  897 */     return 0xFF000000 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
/*      */   }
/*      */ 
/*      */   public ImageProcessor resize(int dstWidth, int dstHeight)
/*      */   {
/*  904 */     if (this.interpolationMethod == 2)
/*  905 */       return filterRGB(8, dstWidth, dstHeight);
/*  906 */     double srcCenterX = this.roiX + this.roiWidth / 2.0D;
/*  907 */     double srcCenterY = this.roiY + this.roiHeight / 2.0D;
/*  908 */     double dstCenterX = dstWidth / 2.0D;
/*  909 */     double dstCenterY = dstHeight / 2.0D;
/*  910 */     double xScale = dstWidth / this.roiWidth;
/*  911 */     double yScale = dstHeight / this.roiHeight;
/*  912 */     double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  913 */     double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*  914 */     if (this.interpolationMethod == 1)
/*      */     {
/*  917 */       dstCenterX += xScale / 2.0D;
/*  918 */       dstCenterY += yScale / 2.0D;
/*      */     }
/*  920 */     ImageProcessor ip2 = createProcessor(dstWidth, dstHeight);
/*  921 */     int[] pixels2 = (int[])ip2.getPixels();
/*      */ 
/*  924 */     for (int y = 0; y <= dstHeight - 1; y++) {
/*  925 */       double ys = (y - dstCenterY) / yScale + srcCenterY;
/*  926 */       if (this.interpolationMethod == 1) {
/*  927 */         if (ys < 0.0D) ys = 0.0D;
/*  928 */         if (ys >= ylimit) ys = ylimit2;
/*      */       }
/*  930 */       int index1 = this.width * (int)ys;
/*  931 */       int index2 = y * dstWidth;
/*  932 */       for (int x = 0; x <= dstWidth - 1; x++) {
/*  933 */         double xs = (x - dstCenterX) / xScale + srcCenterX;
/*  934 */         if (this.interpolationMethod == 1) {
/*  935 */           if (xs < 0.0D) xs = 0.0D;
/*  936 */           if (xs >= xlimit) xs = xlimit2;
/*  937 */           pixels2[(index2++)] = getInterpolatedPixel(xs, ys, this.pixels);
/*      */         } else {
/*  939 */           pixels2[(index2++)] = this.pixels[(index1 + (int)xs)];
/*      */         }
/*      */       }
/*  941 */       if (y % 20 == 0)
/*  942 */         showProgress(y / dstHeight);
/*      */     }
/*  944 */     showProgress(1.0D);
/*  945 */     return ip2;
/*      */   }
/*      */ 
/*      */   public ImageProcessor makeThumbnail(int width2, int height2, double smoothFactor)
/*      */   {
/*  951 */     return resize(width2, height2, true);
/*      */   }
/*      */ 
/*      */   public void rotate(double angle)
/*      */   {
/*  958 */     if (angle % 360.0D == 0.0D)
/*  959 */       return;
/*  960 */     if (this.interpolationMethod == 2) {
/*  961 */       filterRGB(6, angle);
/*  962 */       return;
/*      */     }
/*  964 */     int[] pixels2 = (int[])getPixelsCopy();
/*  965 */     double centerX = this.roiX + (this.roiWidth - 1) / 2.0D;
/*  966 */     double centerY = this.roiY + (this.roiHeight - 1) / 2.0D;
/*  967 */     int xMax = this.roiX + this.roiWidth - 1;
/*      */ 
/*  969 */     double angleRadians = -angle / 57.295779513082323D;
/*  970 */     double ca = Math.cos(angleRadians);
/*  971 */     double sa = Math.sin(angleRadians);
/*  972 */     double tmp1 = centerY * sa - centerX * ca;
/*  973 */     double tmp2 = -centerX * sa - centerY * ca;
/*      */ 
/*  976 */     double dwidth = this.width; double dheight = this.height;
/*  977 */     double xlimit = this.width - 1.0D; double xlimit2 = this.width - 1.001D;
/*  978 */     double ylimit = this.height - 1.0D; double ylimit2 = this.height - 1.001D;
/*      */ 
/*  980 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/*  981 */       int index = y * this.width + this.roiX;
/*  982 */       double tmp3 = tmp1 - y * sa + centerX;
/*  983 */       double tmp4 = tmp2 + y * ca + centerY;
/*  984 */       for (int x = this.roiX; x <= xMax; x++) {
/*  985 */         double xs = x * ca + tmp3;
/*  986 */         double ys = x * sa + tmp4;
/*  987 */         if ((xs >= -0.01D) && (xs < dwidth) && (ys >= -0.01D) && (ys < dheight)) {
/*  988 */           if (this.interpolationMethod == 1) {
/*  989 */             if (xs < 0.0D) xs = 0.0D;
/*  990 */             if (xs >= xlimit) xs = xlimit2;
/*  991 */             if (ys < 0.0D) ys = 0.0D;
/*  992 */             if (ys >= ylimit) ys = ylimit2;
/*  993 */             this.pixels[(index++)] = getInterpolatedPixel(xs, ys, pixels2);
/*      */           } else {
/*  995 */             int ixs = (int)(xs + 0.5D);
/*  996 */             int iys = (int)(ys + 0.5D);
/*  997 */             if (ixs >= this.width) ixs = this.width - 1;
/*  998 */             if (iys >= this.height) iys = this.height - 1;
/*  999 */             this.pixels[(index++)] = pixels2[(this.width * iys + ixs)];
/*      */           }
/*      */         }
/* 1002 */         else this.pixels[(index++)] = this.bgColor;
/*      */       }
/* 1004 */       if (y % 30 == 0)
/* 1005 */         showProgress((y - this.roiY) / this.roiHeight);
/*      */     }
/* 1007 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void flipVertical()
/*      */   {
/* 1013 */     for (int y = 0; y < this.roiHeight / 2; y++) {
/* 1014 */       int index1 = (this.roiY + y) * this.width + this.roiX;
/* 1015 */       int index2 = (this.roiY + this.roiHeight - 1 - y) * this.width + this.roiX;
/* 1016 */       for (int i = 0; i < this.roiWidth; i++) {
/* 1017 */         int tmp = this.pixels[index1];
/* 1018 */         this.pixels[(index1++)] = this.pixels[index2];
/* 1019 */         this.pixels[(index2++)] = tmp;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void convolve3x3(int[] kernel)
/*      */   {
/* 1027 */     int k1 = kernel[0]; int k2 = kernel[1]; int k3 = kernel[2];
/* 1028 */     int k4 = kernel[3]; int k5 = kernel[4]; int k6 = kernel[5];
/* 1029 */     int k7 = kernel[6]; int k8 = kernel[7]; int k9 = kernel[8];
/*      */ 
/* 1031 */     int scale = 0;
/* 1032 */     for (int i = 0; i < kernel.length; i++)
/* 1033 */       scale += kernel[i];
/* 1034 */     if (scale == 0) scale = 1;
/* 1035 */     int inc = this.roiHeight / 25;
/* 1036 */     if (inc < 1) inc = 1;
/*      */ 
/* 1038 */     int[] pixels2 = (int[])getPixelsCopy();
/*      */ 
/* 1040 */     int rsum = 0; int gsum = 0; int bsum = 0;
/* 1041 */     int rowOffset = this.width;
/* 1042 */     for (int y = this.yMin; y <= this.yMax; y++) {
/* 1043 */       int offset = this.xMin + y * this.width;
/* 1044 */       int p1 = 0;
/* 1045 */       int p2 = pixels2[(offset - rowOffset - 1)];
/* 1046 */       int p3 = pixels2[(offset - rowOffset)];
/* 1047 */       int p4 = 0;
/* 1048 */       int p5 = pixels2[(offset - 1)];
/* 1049 */       int p6 = pixels2[offset];
/* 1050 */       int p7 = 0;
/* 1051 */       int p8 = pixels2[(offset + rowOffset - 1)];
/* 1052 */       int p9 = pixels2[(offset + rowOffset)];
/*      */ 
/* 1054 */       for (int x = this.xMin; x <= this.xMax; x++) {
/* 1055 */         p1 = p2; p2 = p3;
/* 1056 */         p3 = pixels2[(offset - rowOffset + 1)];
/* 1057 */         p4 = p5; p5 = p6;
/* 1058 */         p6 = pixels2[(offset + 1)];
/* 1059 */         p7 = p8; p8 = p9;
/* 1060 */         p9 = pixels2[(offset + rowOffset + 1)];
/*      */ 
/* 1062 */         rsum = k1 * ((p1 & 0xFF0000) >> 16) + k2 * ((p2 & 0xFF0000) >> 16) + k3 * ((p3 & 0xFF0000) >> 16) + k4 * ((p4 & 0xFF0000) >> 16) + k5 * ((p5 & 0xFF0000) >> 16) + k6 * ((p6 & 0xFF0000) >> 16) + k7 * ((p7 & 0xFF0000) >> 16) + k8 * ((p8 & 0xFF0000) >> 16) + k9 * ((p9 & 0xFF0000) >> 16);
/*      */ 
/* 1071 */         rsum /= scale;
/* 1072 */         if (rsum > 255) rsum = 255;
/* 1073 */         if (rsum < 0) rsum = 0;
/*      */ 
/* 1075 */         gsum = k1 * ((p1 & 0xFF00) >> 8) + k2 * ((p2 & 0xFF00) >> 8) + k3 * ((p3 & 0xFF00) >> 8) + k4 * ((p4 & 0xFF00) >> 8) + k5 * ((p5 & 0xFF00) >> 8) + k6 * ((p6 & 0xFF00) >> 8) + k7 * ((p7 & 0xFF00) >> 8) + k8 * ((p8 & 0xFF00) >> 8) + k9 * ((p9 & 0xFF00) >> 8);
/*      */ 
/* 1084 */         gsum /= scale;
/* 1085 */         if (gsum > 255) gsum = 255;
/* 1086 */         else if (gsum < 0) gsum = 0;
/*      */ 
/* 1088 */         bsum = k1 * (p1 & 0xFF) + k2 * (p2 & 0xFF) + k3 * (p3 & 0xFF) + k4 * (p4 & 0xFF) + k5 * (p5 & 0xFF) + k6 * (p6 & 0xFF) + k7 * (p7 & 0xFF) + k8 * (p8 & 0xFF) + k9 * (p9 & 0xFF);
/*      */ 
/* 1097 */         bsum /= scale;
/* 1098 */         if (bsum > 255) bsum = 255;
/* 1099 */         if (bsum < 0) bsum = 0;
/*      */ 
/* 1101 */         this.pixels[(offset++)] = (0xFF000000 | rsum << 16 & 0xFF0000 | gsum << 8 & 0xFF00 | bsum & 0xFF);
/*      */       }
/*      */ 
/* 1106 */       if (y % inc == 0)
/* 1107 */         showProgress((y - this.roiY) / this.roiHeight);
/*      */     }
/* 1109 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public void filter(int type)
/*      */   {
/* 1115 */     int inc = this.roiHeight / 25;
/* 1116 */     if (inc < 1) inc = 1;
/*      */ 
/* 1118 */     int[] pixels2 = (int[])getPixelsCopy();
/* 1119 */     int rsum = 0; int gsum = 0; int bsum = 0;
/* 1120 */     int rowOffset = this.width;
/* 1121 */     for (int y = this.yMin; y <= this.yMax; y++) {
/* 1122 */       int offset = this.xMin + y * this.width;
/* 1123 */       int p1 = 0;
/* 1124 */       int p2 = pixels2[(offset - rowOffset - 1)];
/* 1125 */       int p3 = pixels2[(offset - rowOffset)];
/* 1126 */       int p4 = 0;
/* 1127 */       int p5 = pixels2[(offset - 1)];
/* 1128 */       int p6 = pixels2[offset];
/* 1129 */       int p7 = 0;
/* 1130 */       int p8 = pixels2[(offset + rowOffset - 1)];
/* 1131 */       int p9 = pixels2[(offset + rowOffset)];
/*      */ 
/* 1133 */       for (int x = this.xMin; x <= this.xMax; x++) {
/* 1134 */         p1 = p2; p2 = p3;
/* 1135 */         p3 = pixels2[(offset - rowOffset + 1)];
/* 1136 */         p4 = p5; p5 = p6;
/* 1137 */         p6 = pixels2[(offset + 1)];
/* 1138 */         p7 = p8; p8 = p9;
/* 1139 */         p9 = pixels2[(offset + rowOffset + 1)];
/* 1140 */         rsum = (p1 & 0xFF0000) + (p2 & 0xFF0000) + (p3 & 0xFF0000) + (p4 & 0xFF0000) + (p5 & 0xFF0000) + (p6 & 0xFF0000) + (p7 & 0xFF0000) + (p8 & 0xFF0000) + (p9 & 0xFF0000);
/*      */ 
/* 1142 */         gsum = (p1 & 0xFF00) + (p2 & 0xFF00) + (p3 & 0xFF00) + (p4 & 0xFF00) + (p5 & 0xFF00) + (p6 & 0xFF00) + (p7 & 0xFF00) + (p8 & 0xFF00) + (p9 & 0xFF00);
/*      */ 
/* 1144 */         bsum = (p1 & 0xFF) + (p2 & 0xFF) + (p3 & 0xFF) + (p4 & 0xFF) + (p5 & 0xFF) + (p6 & 0xFF) + (p7 & 0xFF) + (p8 & 0xFF) + (p9 & 0xFF);
/*      */ 
/* 1146 */         this.pixels[(offset++)] = (0xFF000000 | rsum / 9 & 0xFF0000 | gsum / 9 & 0xFF00 | bsum / 9);
/*      */       }
/* 1148 */       if (y % inc == 0)
/* 1149 */         showProgress((y - this.roiY) / this.roiHeight);
/*      */     }
/* 1151 */     showProgress(1.0D);
/*      */   }
/*      */ 
/*      */   public int[] getHistogram() {
/* 1155 */     if (this.mask != null) {
/* 1156 */       return getHistogram(this.mask);
/*      */     }
/* 1158 */     int[] histogram = new int[256];
/* 1159 */     for (int y = this.roiY; y < this.roiY + this.roiHeight; y++) {
/* 1160 */       int i = y * this.width + this.roiX;
/* 1161 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/* 1162 */         int c = this.pixels[(i++)];
/* 1163 */         int r = (c & 0xFF0000) >> 16;
/* 1164 */         int g = (c & 0xFF00) >> 8;
/* 1165 */         int b = c & 0xFF;
/* 1166 */         int v = (int)(r * rWeight + g * gWeight + b * bWeight + 0.5D);
/* 1167 */         histogram[v] += 1;
/*      */       }
/* 1169 */       if (y % 20 == 0)
/* 1170 */         showProgress((y - this.roiY) / this.roiHeight);
/*      */     }
/* 1172 */     showProgress(1.0D);
/* 1173 */     return histogram;
/*      */   }
/*      */ 
/*      */   public int[] getHistogram(ImageProcessor mask)
/*      */   {
/* 1178 */     if ((mask.getWidth() != this.roiWidth) || (mask.getHeight() != this.roiHeight))
/* 1179 */       throw new IllegalArgumentException(maskSizeError(mask));
/* 1180 */     byte[] mpixels = (byte[])mask.getPixels();
/*      */ 
/* 1182 */     int[] histogram = new int[256];
/* 1183 */     int y = this.roiY; for (int my = 0; y < this.roiY + this.roiHeight; my++) {
/* 1184 */       int i = y * this.width + this.roiX;
/* 1185 */       int mi = my * this.roiWidth;
/* 1186 */       for (int x = this.roiX; x < this.roiX + this.roiWidth; x++) {
/* 1187 */         if (mpixels[(mi++)] != 0) {
/* 1188 */           int c = this.pixels[i];
/* 1189 */           int r = (c & 0xFF0000) >> 16;
/* 1190 */           int g = (c & 0xFF00) >> 8;
/* 1191 */           int b = c & 0xFF;
/* 1192 */           int v = (int)(r * rWeight + g * gWeight + b * bWeight + 0.5D);
/* 1193 */           histogram[v] += 1;
/*      */         }
/* 1195 */         i++;
/*      */       }
/* 1197 */       if (y % 20 == 0)
/* 1198 */         showProgress((y - this.roiY) / this.roiHeight);
/* 1183 */       y++;
/*      */     }
/*      */ 
/* 1200 */     showProgress(1.0D);
/* 1201 */     return histogram;
/*      */   }
/*      */ 
/*      */   public void convolve(float[] kernel, int kernelWidth, int kernelHeight)
/*      */   {
/* 1206 */     int size = this.width * this.height;
/* 1207 */     byte[] r = new byte[size];
/* 1208 */     byte[] g = new byte[size];
/* 1209 */     byte[] b = new byte[size];
/* 1210 */     getRGB(r, g, b);
/* 1211 */     ImageProcessor rip = new ByteProcessor(this.width, this.height, r, null);
/* 1212 */     ImageProcessor gip = new ByteProcessor(this.width, this.height, g, null);
/* 1213 */     ImageProcessor bip = new ByteProcessor(this.width, this.height, b, null);
/* 1214 */     ImageProcessor ip2 = rip.convertToFloat();
/* 1215 */     Rectangle roi = getRoi();
/* 1216 */     ip2.setRoi(roi);
/* 1217 */     ip2.convolve(kernel, kernelWidth, kernelHeight);
/* 1218 */     ImageProcessor r2 = ip2.convertToByte(false);
/* 1219 */     ip2 = gip.convertToFloat();
/* 1220 */     ip2.setRoi(roi);
/* 1221 */     ip2.convolve(kernel, kernelWidth, kernelHeight);
/* 1222 */     ImageProcessor g2 = ip2.convertToByte(false);
/* 1223 */     ip2 = bip.convertToFloat();
/* 1224 */     ip2.setRoi(roi);
/* 1225 */     ip2.convolve(kernel, kernelWidth, kernelHeight);
/* 1226 */     ImageProcessor b2 = ip2.convertToByte(false);
/* 1227 */     setRGB((byte[])r2.getPixels(), (byte[])g2.getPixels(), (byte[])b2.getPixels());
/*      */   }
/*      */ 
/*      */   public static void setWeightingFactors(double rFactor, double gFactor, double bFactor)
/*      */   {
/* 1235 */     rWeight = rFactor;
/* 1236 */     gWeight = gFactor;
/* 1237 */     bWeight = bFactor;
/*      */   }
/*      */ 
/*      */   public static double[] getWeightingFactors()
/*      */   {
/* 1243 */     double[] weights = new double[3];
/* 1244 */     weights[0] = rWeight;
/* 1245 */     weights[1] = gWeight;
/* 1246 */     weights[2] = bWeight;
/* 1247 */     return weights;
/*      */   }
/*      */ 
/*      */   public boolean isInvertedLut()
/*      */   {
/* 1252 */     return false;
/*      */   }
/*      */ 
/*      */   public int getBestIndex(Color c)
/*      */   {
/* 1257 */     return 0;
/*      */   }
/*      */ 
/*      */   public void invertLut()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void updateComposite(int[] rgbPixels, int channel)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void threshold(int level) {
/*      */   }
/*      */ 
/*      */   public int getNChannels() {
/* 1272 */     return 3;
/*      */   }
/*      */ 
/*      */   public FloatProcessor toFloat(int channelNumber, FloatProcessor fp)
/*      */   {
/* 1284 */     int size = this.width * this.height;
/* 1285 */     if ((fp == null) || (fp.getWidth() != this.width) || (fp.getHeight() != this.height))
/* 1286 */       fp = new FloatProcessor(this.width, this.height, new float[size], null);
/* 1287 */     float[] fPixels = (float[])fp.getPixels();
/* 1288 */     int shift = 16 - 8 * channelNumber;
/* 1289 */     int byteMask = 255 << shift;
/* 1290 */     for (int i = 0; i < size; i++)
/* 1291 */       fPixels[i] = ((this.pixels[i] & byteMask) >> shift);
/* 1292 */     fp.setRoi(getRoi());
/* 1293 */     fp.setMask(this.mask);
/* 1294 */     fp.setMinAndMax(0.0D, 255.0D);
/* 1295 */     return fp;
/*      */   }
/*      */ 
/*      */   public void setPixels(int channelNumber, FloatProcessor fp)
/*      */   {
/* 1303 */     float[] fPixels = (float[])fp.getPixels();
/*      */ 
/* 1305 */     int size = this.width * this.height;
/* 1306 */     int shift = 16 - 8 * channelNumber;
/* 1307 */     int resetMask = 0xFFFFFFFF ^ 255 << shift;
/* 1308 */     for (int i = 0; i < size; i++) {
/* 1309 */       float value = fPixels[i] + 0.5F;
/* 1310 */       if (value < 0.0F) value = 0.0F;
/* 1311 */       if (value > 255.0F) value = 255.0F;
/* 1312 */       this.pixels[i] = (this.pixels[i] & resetMask | (int)value << shift);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ColorProcessor
 * JD-Core Version:    0.6.2
 */