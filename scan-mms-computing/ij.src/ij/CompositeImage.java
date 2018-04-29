/*     */ package ij;
/*     */ 
/*     */ import ij.io.FileInfo;
/*     */ import ij.plugin.frame.Channels;
/*     */ import ij.plugin.frame.ContrastAdjuster;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.LUT;
/*     */ import java.awt.Color;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.DataBuffer;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.MemoryImageSource;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ 
/*     */ public class CompositeImage extends ImagePlus
/*     */ {
/*     */   public static final int COMPOSITE = 1;
/*     */   public static final int COLOR = 2;
/*     */   public static final int GRAYSCALE = 3;
/*     */   public static final int TRANSPARENT = 4;
/*     */   public static final int MAX_CHANNELS = 7;
/*     */   int[] rgbPixels;
/*     */   boolean newPixels;
/*     */   MemoryImageSource imageSource;
/*     */   Image awtImage;
/*     */   WritableRaster rgbRaster;
/*     */   SampleModel rgbSampleModel;
/*     */   BufferedImage rgbImage;
/*     */   ColorModel rgbCM;
/*     */   ImageProcessor[] cip;
/*  24 */   Color[] colors = { Color.red, Color.green, Color.blue, Color.white, Color.cyan, Color.magenta, Color.yellow };
/*     */   LUT[] lut;
/*  26 */   int currentChannel = -1;
/*     */   int previousChannel;
/*  28 */   int currentSlice = 1;
/*  29 */   int currentFrame = 1;
/*     */   static int count;
/*     */   boolean singleChannel;
/*  32 */   boolean[] active = new boolean[7];
/*  33 */   int mode = 2;
/*     */   int bitDepth;
/*     */   double[] displayRanges;
/*     */   byte[][] channelLuts;
/*     */   boolean customLuts;
/*     */   boolean syncChannels;
/*     */   boolean channelsUpdated;
/*     */ 
/*     */   public CompositeImage(ImagePlus imp)
/*     */   {
/*  42 */     this(imp, 2);
/*     */   }
/*     */ 
/*     */   public CompositeImage(ImagePlus imp, int mode) {
/*  46 */     if ((mode < 1) || (mode > 3))
/*  47 */       mode = 2;
/*  48 */     this.mode = mode;
/*  49 */     int channels = imp.getNChannels();
/*  50 */     this.bitDepth = getBitDepth();
/*  51 */     if (IJ.debugMode) IJ.log("CompositeImage: " + imp + " " + mode + " " + channels);
/*     */ 
/*  53 */     boolean isRGB = imp.getBitDepth() == 24;
/*     */     ImageStack stack2;
/*     */     ImageStack stack2;
/*  54 */     if (isRGB) {
/*  55 */       if (imp.getImageStackSize() > 1)
/*  56 */         throw new IllegalArgumentException("RGB stacks not supported");
/*  57 */       stack2 = getRGBStack(imp);
/*     */     } else {
/*  59 */       stack2 = imp.getImageStack();
/*  60 */     }int stackSize = stack2.getSize();
/*  61 */     if ((channels == 1) && (isRGB)) channels = 3;
/*  62 */     if ((channels == 1) && (stackSize <= 7)) channels = stackSize;
/*  63 */     if ((channels < 1) || (stackSize % channels != 0))
/*  64 */       throw new IllegalArgumentException("stacksize not multiple of channels");
/*  65 */     if ((mode == 1) && (channels > 7))
/*  66 */       this.mode = 2;
/*  67 */     this.compositeImage = true;
/*  68 */     int z = imp.getNSlices();
/*  69 */     int t = imp.getNFrames();
/*  70 */     if ((channels == stackSize) || (channels * z * t != stackSize))
/*  71 */       setDimensions(channels, stackSize / channels, 1);
/*     */     else
/*  73 */       setDimensions(channels, z, t);
/*  74 */     setStack(imp.getTitle(), stack2);
/*  75 */     setCalibration(imp.getCalibration());
/*  76 */     FileInfo fi = imp.getOriginalFileInfo();
/*  77 */     if (fi != null) {
/*  78 */       this.displayRanges = fi.displayRanges;
/*  79 */       this.channelLuts = fi.channelLuts;
/*     */     }
/*  81 */     setFileInfo(fi);
/*  82 */     Object info = imp.getProperty("Info");
/*  83 */     if (info != null)
/*  84 */       setProperty("Info", imp.getProperty("Info"));
/*  85 */     if (mode == 1)
/*  86 */       for (int i = 0; i < 7; i++)
/*  87 */         this.active[i] = true;
/*     */     else {
/*  89 */       this.active[0] = true;
/*     */     }
/*  91 */     setRoi(imp.getRoi());
/*  92 */     setOverlay(imp.getOverlay());
/*  93 */     if (channels != stackSize)
/*  94 */       setOpenAsHyperStack(true);
/*     */   }
/*     */ 
/*     */   public Image getImage() {
/*  98 */     if (this.img == null)
/*  99 */       updateImage();
/* 100 */     return this.img;
/*     */   }
/*     */ 
/*     */   public void updateChannelAndDraw() {
/* 104 */     if (!this.customLuts) this.singleChannel = true;
/* 105 */     updateAndDraw();
/*     */   }
/*     */ 
/*     */   public void updateAllChannelsAndDraw() {
/* 109 */     if (this.mode != 1) {
/* 110 */       updateChannelAndDraw();
/*     */     } else {
/* 112 */       this.syncChannels = true;
/* 113 */       this.singleChannel = false;
/* 114 */       updateAndDraw();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ImageProcessor getChannelProcessor() {
/* 119 */     if ((this.cip != null) && (this.currentChannel != -1)) {
/* 120 */       return this.cip[this.currentChannel];
/*     */     }
/* 122 */     return getProcessor();
/*     */   }
/*     */ 
/*     */   void setup(int channels, ImageStack stack2) {
/* 126 */     setupLuts(channels);
/* 127 */     if (this.mode == 1) {
/* 128 */       this.cip = new ImageProcessor[channels];
/* 129 */       for (int i = 0; i < channels; i++) {
/* 130 */         this.cip[i] = stack2.getProcessor(i + 1);
/* 131 */         this.cip[i].setColorModel(this.lut[i]);
/* 132 */         this.cip[i].setMinAndMax(this.lut[i].min, this.lut[i].max);
/*     */       }
/* 134 */       this.currentSlice = (this.currentFrame = 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setupLuts(int channels) {
/* 139 */     if ((this.lut == null) || (this.lut.length < channels)) {
/* 140 */       if ((this.displayRanges != null) && (channels != this.displayRanges.length / 2))
/* 141 */         this.displayRanges = null;
/* 142 */       if ((this.displayRanges == null) && (this.ip.getMin() == 0.0D) && (this.ip.getMax() == 0.0D))
/* 143 */         this.ip.resetMinAndMax();
/* 144 */       this.lut = new LUT[channels];
/* 145 */       LUT lut2 = channels > 7 ? createLutFromColor(Color.white) : null;
/* 146 */       for (int i = 0; i < channels; i++) {
/* 147 */         if ((this.channelLuts != null) && (i < this.channelLuts.length)) {
/* 148 */           this.lut[i] = createLutFromBytes(this.channelLuts[i]);
/* 149 */           this.customLuts = true;
/* 150 */         } else if (i < 7) {
/* 151 */           this.lut[i] = createLutFromColor(this.colors[i]);
/*     */         } else {
/* 153 */           this.lut[i] = ((LUT)lut2.clone());
/* 154 */         }if (this.displayRanges != null) {
/* 155 */           this.lut[i].min = this.displayRanges[(i * 2)];
/* 156 */           this.lut[i].max = this.displayRanges[(i * 2 + 1)];
/*     */         } else {
/* 158 */           this.lut[i].min = this.ip.getMin();
/* 159 */           this.lut[i].max = this.ip.getMax();
/*     */         }
/*     */       }
/* 162 */       this.displayRanges = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resetDisplayRanges() {
/* 167 */     int channels = getNChannels();
/* 168 */     ImageStack stack2 = getImageStack();
/* 169 */     if ((this.lut == null) || (channels != this.lut.length) || (channels > stack2.getSize()) || (channels > 7))
/* 170 */       return;
/* 171 */     for (int i = 0; i < channels; i++) {
/* 172 */       ImageProcessor ip2 = stack2.getProcessor(i + 1);
/* 173 */       ip2.resetMinAndMax();
/* 174 */       this.lut[i].min = ip2.getMin();
/* 175 */       this.lut[i].max = ip2.getMax();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateAndDraw() {
/* 180 */     updateImage();
/* 181 */     if (this.win != null)
/* 182 */       notifyListeners(2);
/* 183 */     draw();
/*     */   }
/*     */ 
/*     */   public synchronized void updateImage() {
/* 187 */     int imageSize = this.width * this.height;
/* 188 */     int nChannels = getNChannels();
/*     */ 
/* 190 */     int ch = getChannel();
/*     */ 
/* 193 */     if (ch > nChannels) ch = nChannels;
/* 194 */     boolean newChannel = false;
/* 195 */     if (ch - 1 != this.currentChannel) {
/* 196 */       this.previousChannel = this.currentChannel;
/* 197 */       this.currentChannel = (ch - 1);
/* 198 */       newChannel = true;
/*     */     }
/*     */ 
/* 201 */     ImageProcessor ip = getProcessor();
/* 202 */     if (this.mode != 1) {
/* 203 */       if (newChannel) {
/* 204 */         setupLuts(nChannels);
/* 205 */         LUT cm = this.lut[this.currentChannel];
/* 206 */         if (this.mode == 2)
/* 207 */           ip.setColorModel(cm);
/* 208 */         if ((cm.min != 0.0D) || (cm.max != 0.0D))
/* 209 */           ip.setMinAndMax(cm.min, cm.max);
/* 210 */         if (!IJ.isMacro()) ContrastAdjuster.update();
/* 211 */         Frame channels = Channels.getInstance();
/* 212 */         for (int i = 0; i < 7; i++)
/* 213 */           this.active[i] = (i == this.currentChannel ? 1 : false);
/* 214 */         if (channels != null) ((Channels)channels).update();
/*     */       }
/* 216 */       this.img = ip.createImage();
/* 217 */       return;
/*     */     }
/*     */ 
/* 220 */     if (nChannels == 1) {
/* 221 */       this.cip = null;
/* 222 */       this.rgbPixels = null;
/* 223 */       this.awtImage = null;
/* 224 */       if (ip != null)
/* 225 */         this.img = ip.createImage();
/* 226 */       return;
/*     */     }
/*     */ 
/* 229 */     if ((this.cip == null) || (this.cip[0].getWidth() != this.width) || (this.cip[0].getHeight() != this.height) || (getBitDepth() != this.bitDepth)) {
/* 230 */       setup(nChannels, getImageStack());
/* 231 */       this.rgbPixels = null;
/* 232 */       this.rgbSampleModel = null;
/* 233 */       if (this.currentChannel >= nChannels) {
/* 234 */         setSlice(1);
/* 235 */         this.currentChannel = 0;
/* 236 */         newChannel = true;
/*     */       }
/* 238 */       this.bitDepth = getBitDepth();
/*     */     }
/*     */ 
/* 241 */     if (newChannel) {
/* 242 */       getProcessor().setMinAndMax(this.cip[this.currentChannel].getMin(), this.cip[this.currentChannel].getMax());
/* 243 */       if (!IJ.isMacro()) ContrastAdjuster.update();
/*     */ 
/*     */     }
/*     */ 
/* 247 */     if ((getSlice() != this.currentSlice) || (getFrame() != this.currentFrame) || (this.channelsUpdated)) {
/* 248 */       this.channelsUpdated = false;
/* 249 */       this.currentSlice = getSlice();
/* 250 */       this.currentFrame = getFrame();
/* 251 */       int position = getStackIndex(1, this.currentSlice, this.currentFrame);
/* 252 */       if (this.cip == null) return;
/* 253 */       for (int i = 0; i < nChannels; i++) {
/* 254 */         this.cip[i].setPixels(getImageStack().getProcessor(position + i).getPixels());
/*     */       }
/*     */     }
/* 257 */     if (this.rgbPixels == null) {
/* 258 */       this.rgbPixels = new int[imageSize];
/* 259 */       this.newPixels = true;
/* 260 */       this.imageSource = null;
/* 261 */       this.rgbRaster = null;
/* 262 */       this.rgbImage = null;
/*     */     }
/*     */ 
/* 265 */     this.cip[this.currentChannel].setMinAndMax(ip.getMin(), ip.getMax());
/* 266 */     if ((this.singleChannel) && (nChannels <= 3)) {
/* 267 */       switch (this.currentChannel) { case 0:
/* 268 */         this.cip[0].updateComposite(this.rgbPixels, 1); break;
/*     */       case 1:
/* 269 */         this.cip[1].updateComposite(this.rgbPixels, 2); break;
/*     */       case 2:
/* 270 */         this.cip[2].updateComposite(this.rgbPixels, 3); }
/*     */     }
/*     */     else {
/* 273 */       if (this.cip == null) return;
/* 274 */       if (this.syncChannels) {
/* 275 */         ImageProcessor ip2 = getProcessor();
/* 276 */         double min = ip2.getMin(); double max = ip2.getMax();
/* 277 */         for (int i = 0; i < nChannels; i++) {
/* 278 */           this.cip[i].setMinAndMax(min, max);
/* 279 */           this.lut[i].min = min;
/* 280 */           this.lut[i].max = max;
/*     */         }
/* 282 */         this.syncChannels = false;
/*     */       }
/* 284 */       if (this.active[0] != 0)
/* 285 */         this.cip[0].updateComposite(this.rgbPixels, 4);
/*     */       else
/* 287 */         for (int i = 1; i < imageSize; i++) this.rgbPixels[i] = 0;
/* 288 */       if (this.cip == null) return;
/* 289 */       for (int i = 1; i < nChannels; i++) {
/* 290 */         if (this.active[i] != 0) this.cip[i].updateComposite(this.rgbPixels, 5);
/*     */       }
/*     */     }
/* 293 */     if (IJ.isJava16())
/* 294 */       createBufferedImage();
/*     */     else
/* 296 */       createImage();
/* 297 */     if ((this.img == null) && (this.awtImage != null))
/* 298 */       this.img = this.awtImage;
/* 299 */     this.singleChannel = false;
/*     */   }
/*     */ 
/*     */   void createImage() {
/* 303 */     if (this.imageSource == null) {
/* 304 */       this.rgbCM = new DirectColorModel(32, 16711680, 65280, 255);
/* 305 */       this.imageSource = new MemoryImageSource(this.width, this.height, this.rgbCM, this.rgbPixels, 0, this.width);
/* 306 */       this.imageSource.setAnimated(true);
/* 307 */       this.imageSource.setFullBufferUpdates(true);
/* 308 */       this.awtImage = Toolkit.getDefaultToolkit().createImage(this.imageSource);
/* 309 */       this.newPixels = false;
/* 310 */     } else if (this.newPixels) {
/* 311 */       this.imageSource.newPixels(this.rgbPixels, this.rgbCM, 0, this.width);
/* 312 */       this.newPixels = false;
/*     */     } else {
/* 314 */       this.imageSource.newPixels();
/*     */     }
/*     */   }
/*     */ 
/*     */   void createBufferedImage() {
/* 319 */     if (this.rgbSampleModel == null)
/* 320 */       this.rgbSampleModel = getRGBSampleModel();
/* 321 */     if (this.rgbRaster == null) {
/* 322 */       DataBuffer dataBuffer = new DataBufferInt(this.rgbPixels, this.width * this.height, 0);
/* 323 */       this.rgbRaster = Raster.createWritableRaster(this.rgbSampleModel, dataBuffer, null);
/*     */     }
/* 325 */     if (this.rgbImage == null)
/* 326 */       this.rgbImage = new BufferedImage(this.rgbCM, this.rgbRaster, false, null);
/* 327 */     this.awtImage = this.rgbImage;
/*     */   }
/*     */ 
/*     */   SampleModel getRGBSampleModel() {
/* 331 */     this.rgbCM = new DirectColorModel(24, 16711680, 65280, 255);
/* 332 */     WritableRaster wr = this.rgbCM.createCompatibleWritableRaster(1, 1);
/* 333 */     SampleModel sampleModel = wr.getSampleModel();
/* 334 */     sampleModel = sampleModel.createCompatibleSampleModel(this.width, this.height);
/* 335 */     return sampleModel;
/*     */   }
/*     */ 
/*     */   ImageStack getRGBStack(ImagePlus imp)
/*     */   {
/* 360 */     ImageProcessor ip = imp.getProcessor();
/* 361 */     int w = ip.getWidth();
/* 362 */     int h = ip.getHeight();
/* 363 */     int size = w * h;
/* 364 */     byte[] r = new byte[size];
/* 365 */     byte[] g = new byte[size];
/* 366 */     byte[] b = new byte[size];
/* 367 */     ((ColorProcessor)ip).getRGB(r, g, b);
/* 368 */     ImageStack stack = new ImageStack(w, h);
/* 369 */     stack.addSlice("Red", r);
/* 370 */     stack.addSlice("Green", g);
/* 371 */     stack.addSlice("Blue", b);
/* 372 */     stack.setColorModel(ip.getDefaultColorModel());
/* 373 */     return stack;
/*     */   }
/*     */ 
/*     */   public LUT createLutFromColor(Color color) {
/* 377 */     return LUT.createLutFromColor(color);
/*     */   }
/*     */ 
/*     */   LUT createLutFromBytes(byte[] bytes) {
/* 381 */     if ((bytes == null) || (bytes.length != 768))
/* 382 */       return createLutFromColor(Color.white);
/* 383 */     byte[] r = new byte[256];
/* 384 */     byte[] g = new byte[256];
/* 385 */     byte[] b = new byte[256];
/* 386 */     for (int i = 0; i < 256; i++) r[i] = bytes[i];
/* 387 */     for (int i = 0; i < 256; i++) g[i] = bytes[(256 + i)];
/* 388 */     for (int i = 0; i < 256; i++) b[i] = bytes[(512 + i)];
/* 389 */     return new LUT(r, g, b);
/*     */   }
/*     */ 
/*     */   public Color getChannelColor() {
/* 393 */     if ((this.lut == null) || (this.mode == 3))
/* 394 */       return Color.black;
/* 395 */     IndexColorModel cm = this.lut[getChannelIndex()];
/* 396 */     if (cm == null)
/* 397 */       return Color.black;
/* 398 */     int index = cm.getMapSize() - 1;
/* 399 */     int r = cm.getRed(index);
/* 400 */     int g = cm.getGreen(index);
/* 401 */     int b = cm.getBlue(index);
/*     */ 
/* 403 */     if ((r < 100) || (g < 100) || (b < 100)) {
/* 404 */       return new Color(r, g, b);
/*     */     }
/* 406 */     return Color.black;
/*     */   }
/*     */ 
/*     */   public ImageProcessor getProcessor(int channel) {
/* 410 */     if ((this.cip == null) || (channel > this.cip.length)) {
/* 411 */       return null;
/*     */     }
/* 413 */     return this.cip[(channel - 1)];
/*     */   }
/*     */ 
/*     */   public boolean[] getActiveChannels() {
/* 417 */     return this.active;
/*     */   }
/*     */ 
/*     */   public void setMode(int mode) {
/* 421 */     if ((mode < 1) || (mode > 3))
/* 422 */       return;
/* 423 */     if ((mode == 1) && (getNChannels() > 7))
/* 424 */       mode = 2;
/* 425 */     for (int i = 0; i < 7; i++)
/* 426 */       this.active[i] = true;
/* 427 */     if ((this.mode != 1) && (mode == 1))
/* 428 */       this.img = null;
/* 429 */     this.mode = mode;
/* 430 */     if ((mode == 2) || (mode == 3)) {
/* 431 */       if (this.cip != null) {
/* 432 */         for (int i = 0; i < this.cip.length; i++) {
/* 433 */           if (this.cip[i] != null) this.cip[i].setPixels(null);
/* 434 */           this.cip[i] = null;
/*     */         }
/*     */       }
/* 437 */       this.cip = null;
/* 438 */       this.rgbPixels = null;
/* 439 */       this.awtImage = null;
/* 440 */       this.currentChannel = -1;
/*     */     }
/* 442 */     if ((mode == 3) || (mode == 4))
/* 443 */       this.ip.setColorModel(this.ip.getDefaultColorModel());
/* 444 */     Frame channels = Channels.getInstance();
/* 445 */     if (channels != null) ((Channels)channels).update(); 
/*     */   }
/*     */ 
/*     */   public int getMode()
/*     */   {
/* 449 */     return this.mode;
/*     */   }
/*     */ 
/*     */   public String getModeAsString() {
/* 453 */     switch (this.mode) { case 1:
/* 454 */       return "composite";
/*     */     case 2:
/* 455 */       return "color";
/*     */     case 3:
/* 456 */       return "grayscale";
/*     */     }
/* 458 */     return "";
/*     */   }
/*     */ 
/*     */   public LUT getChannelLut(int channel)
/*     */   {
/* 463 */     int channels = getNChannels();
/* 464 */     if (this.lut == null) setupLuts(channels);
/* 465 */     if ((channel < 1) || (channel > this.lut.length))
/* 466 */       throw new IllegalArgumentException("Channel out of range: " + channel);
/* 467 */     return this.lut[(channel - 1)];
/*     */   }
/*     */ 
/*     */   public LUT getChannelLut()
/*     */   {
/* 472 */     int c = getChannelIndex();
/* 473 */     return this.lut[c];
/*     */   }
/*     */ 
/*     */   public LUT[] getLuts()
/*     */   {
/* 478 */     int channels = getNChannels();
/* 479 */     if (this.lut == null) setupLuts(channels);
/* 480 */     LUT[] luts = new LUT[channels];
/* 481 */     for (int i = 0; i < channels; i++)
/* 482 */       luts[i] = ((LUT)this.lut[i].clone());
/* 483 */     return luts;
/*     */   }
/*     */ 
/*     */   public void setLuts(LUT[] luts)
/*     */   {
/* 488 */     int channels = getNChannels();
/* 489 */     if (this.lut == null) setupLuts(channels);
/* 490 */     if ((luts == null) || (luts.length < channels))
/* 491 */       throw new IllegalArgumentException("Lut array is null or too small");
/* 492 */     for (int i = 0; i < channels; i++)
/* 493 */       setChannelLut(luts[i], i + 1);
/*     */   }
/*     */ 
/*     */   public void copyLuts(ImagePlus imp)
/*     */   {
/* 500 */     int channels = getNChannels();
/* 501 */     if ((!imp.isComposite()) || (imp.getNChannels() != channels))
/* 502 */       return;
/* 503 */     CompositeImage ci = (CompositeImage)imp;
/* 504 */     LUT[] luts = ci.getLuts();
/* 505 */     if ((luts != null) && (luts.length == channels)) {
/* 506 */       this.lut = luts;
/* 507 */       this.cip = null;
/*     */     }
/* 509 */     int mode2 = ci.getMode();
/* 510 */     setMode(mode2);
/* 511 */     if (mode2 == 1) {
/* 512 */       boolean[] active2 = ci.getActiveChannels();
/* 513 */       for (int i = 0; i < 7; i++)
/* 514 */         this.active[i] = active2[i];
/*     */     }
/* 516 */     if (ci.hasCustomLuts())
/* 517 */       this.customLuts = true;
/*     */   }
/*     */ 
/*     */   int getChannelIndex() {
/* 521 */     int channels = getNChannels();
/* 522 */     if (this.lut == null) setupLuts(channels);
/* 523 */     int index = getChannel() - 1;
/* 524 */     return index;
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 528 */     setup(getNChannels(), getImageStack());
/*     */   }
/*     */ 
/*     */   public void setChannelLut(LUT table)
/*     */   {
/* 533 */     if (this.mode == 3) {
/* 534 */       getProcessor().setColorModel(table);
/*     */     } else {
/* 536 */       int c = getChannelIndex();
/* 537 */       double min = this.lut[c].min;
/* 538 */       double max = this.lut[c].max;
/* 539 */       this.lut[c] = table;
/* 540 */       this.lut[c].min = min;
/* 541 */       this.lut[c].max = max;
/* 542 */       if ((this.mode == 1) && (this.cip != null) && (c < this.cip.length)) {
/* 543 */         this.cip[c].setColorModel(this.lut[c]);
/* 544 */         this.imageSource = null;
/* 545 */         this.newPixels = true;
/* 546 */         this.img = null;
/*     */       }
/* 548 */       this.currentChannel = -1;
/* 549 */       if (!IJ.isMacro()) ContrastAdjuster.update();
/*     */     }
/* 551 */     this.customLuts = true;
/*     */   }
/*     */ 
/*     */   public void setChannelLut(LUT table, int channel)
/*     */   {
/* 556 */     int channels = getNChannels();
/* 557 */     if (this.lut == null) setupLuts(channels);
/* 558 */     if ((channel < 1) || (channel > this.lut.length))
/* 559 */       throw new IllegalArgumentException("Channel out of range");
/* 560 */     this.lut[(channel - 1)] = ((LUT)table.clone());
/* 561 */     this.cip = null;
/* 562 */     this.customLuts = true;
/*     */   }
/*     */ 
/*     */   public void setChannelColorModel(IndexColorModel cm)
/*     */   {
/* 567 */     setChannelLut(new LUT(cm, 0.0D, 0.0D));
/*     */   }
/*     */ 
/*     */   public void setDisplayRange(double min, double max) {
/* 571 */     this.ip.setMinAndMax(min, max);
/* 572 */     int c = getChannelIndex();
/* 573 */     this.lut[c].min = min;
/* 574 */     this.lut[c].max = max;
/*     */   }
/*     */ 
/*     */   public double getDisplayRangeMin() {
/* 578 */     if (this.lut != null) {
/* 579 */       return this.lut[getChannelIndex()].min;
/*     */     }
/* 581 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public double getDisplayRangeMax() {
/* 585 */     if (this.lut != null) {
/* 586 */       return this.lut[getChannelIndex()].max;
/*     */     }
/* 588 */     return 255.0D;
/*     */   }
/*     */ 
/*     */   public void resetDisplayRange() {
/* 592 */     this.ip.resetMinAndMax();
/* 593 */     int c = getChannelIndex();
/* 594 */     this.lut[c].min = this.ip.getMin();
/* 595 */     this.lut[c].max = this.ip.getMax();
/*     */   }
/*     */ 
/*     */   public boolean hasCustomLuts() {
/* 599 */     return (this.customLuts) && (this.mode != 3);
/*     */   }
/*     */ 
/*     */   public void setChannelsUpdated() {
/* 603 */     this.channelsUpdated = true;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.CompositeImage
 * JD-Core Version:    0.6.2
 */