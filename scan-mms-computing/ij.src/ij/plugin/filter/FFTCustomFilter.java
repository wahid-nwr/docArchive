/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.measure.Measurements;
/*     */ import ij.plugin.ContrastEnhancer;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FHT;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class FFTCustomFilter
/*     */   implements PlugInFilter, Measurements
/*     */ {
/*     */   private ImagePlus imp;
/*  15 */   private static int filterIndex = 1;
/*     */   private int slice;
/*     */   private int stackSize;
/*     */   private boolean done;
/*     */   private ImageProcessor filter;
/*     */   private static boolean processStack;
/*     */   private boolean padded;
/*     */   private int originalWidth;
/*     */   private int originalHeight;
/*  24 */   private Rectangle rect = new Rectangle();
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp) {
/*  27 */     this.imp = imp;
/*  28 */     if (imp == null) {
/*  29 */       IJ.noImage(); return 4096;
/*  30 */     }this.stackSize = imp.getStackSize();
/*  31 */     if (imp.getProperty("FHT") != null) {
/*  32 */       IJ.error("FFT Custom Filter", "Spatial domain (non-FFT) image required");
/*  33 */       return 4096;
/*     */     }
/*     */ 
/*  36 */     return processStack ? 63 : 31;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  40 */     this.slice += 1;
/*  41 */     if (this.done)
/*  42 */       return;
/*  43 */     FHT fht = newFHT(ip);
/*  44 */     if (this.slice == 1) {
/*  45 */       this.filter = getFilter(fht.getWidth());
/*  46 */       if (this.filter == null) {
/*  47 */         this.done = true;
/*  48 */         return;
/*     */       }
/*     */     }
/*  51 */     fht.transform();
/*  52 */     customFilter(fht);
/*  53 */     doInverseTransform(fht, ip);
/*  54 */     if (this.slice == 1)
/*  55 */       ip.resetMinAndMax();
/*  56 */     if (this.slice == this.stackSize) {
/*  57 */       new ContrastEnhancer().stretchHistogram(this.imp, 0.0D);
/*  58 */       this.imp.updateAndDraw();
/*     */     }
/*  60 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   void doInverseTransform(FHT fht, ImageProcessor ip) {
/*  64 */     showStatus("Inverse transform");
/*  65 */     fht.inverseTransform();
/*     */ 
/*  68 */     fht.resetMinAndMax();
/*  69 */     ImageProcessor ip2 = fht;
/*  70 */     fht.setRoi(this.rect.x, this.rect.y, this.rect.width, this.rect.height);
/*  71 */     ip2 = fht.crop();
/*  72 */     int bitDepth = fht.originalBitDepth > 0 ? fht.originalBitDepth : this.imp.getBitDepth();
/*  73 */     switch (bitDepth) { case 8:
/*  74 */       ip2 = ip2.convertToByte(true); break;
/*     */     case 16:
/*  75 */       ip2 = ip2.convertToShort(true); break;
/*     */     case 24:
/*  77 */       showStatus("Setting brightness");
/*  78 */       fht.rgb.setBrightness((FloatProcessor)ip2);
/*  79 */       ip2 = fht.rgb;
/*  80 */       fht.rgb = null;
/*  81 */       break;
/*     */     case 32:
/*     */     }
/*  84 */     ip.insert(ip2, 0, 0);
/*     */   }
/*     */ 
/*     */   FHT newFHT(ImageProcessor ip)
/*     */   {
/*  89 */     int width = ip.getWidth();
/*  90 */     int height = ip.getHeight();
/*  91 */     int maxN = Math.max(width, height);
/*  92 */     int size = 2;
/*  93 */     while (size < 1.5D * maxN) size *= 2;
/*  94 */     this.rect.x = ((int)Math.round((size - width) / 2.0D));
/*  95 */     this.rect.y = ((int)Math.round((size - height) / 2.0D));
/*  96 */     this.rect.width = width;
/*  97 */     this.rect.height = height;
/*  98 */     FFTFilter fftFilter = new FFTFilter();
/*     */     FHT fht;
/*  99 */     if ((ip instanceof ColorProcessor)) {
/* 100 */       showStatus("Extracting brightness");
/* 101 */       ImageProcessor ip2 = ((ColorProcessor)ip).getBrightness();
/* 102 */       FHT fht = new FHT(fftFilter.tileMirror(ip2, size, size, this.rect.x, this.rect.y));
/* 103 */       fht.rgb = ((ColorProcessor)ip.duplicate());
/*     */     } else {
/* 105 */       fht = new FHT(fftFilter.tileMirror(ip, size, size, this.rect.x, this.rect.y));
/* 106 */     }fht.originalWidth = this.originalWidth;
/* 107 */     fht.originalHeight = this.originalHeight;
/* 108 */     fht.originalBitDepth = this.imp.getBitDepth();
/* 109 */     return fht;
/*     */   }
/*     */ 
/*     */   void showStatus(String msg) {
/* 113 */     if (this.stackSize > 1)
/* 114 */       IJ.showStatus("FFT: " + this.slice + "/" + this.stackSize);
/*     */     else
/* 116 */       IJ.showStatus(msg);
/*     */   }
/*     */ 
/*     */   void customFilter(FHT fht) {
/* 120 */     int size = fht.getWidth();
/* 121 */     showStatus("Filtering");
/* 122 */     fht.swapQuadrants(this.filter);
/* 123 */     float[] fhtPixels = (float[])fht.getPixels();
/* 124 */     byte[] filterPixels = (byte[])this.filter.getPixels();
/* 125 */     for (int i = 0; i < fhtPixels.length; i++)
/* 126 */       fhtPixels[i] = ((float)(fhtPixels[i] * (filterPixels[i] & 0xFF) / 255.0D));
/* 127 */     fht.swapQuadrants(this.filter);
/*     */   }
/*     */ 
/*     */   ImageProcessor getFilter(int size) {
/* 131 */     int[] wList = WindowManager.getIDList();
/* 132 */     if ((wList == null) || (wList.length < 2)) {
/* 133 */       IJ.error("FFT", "A filter (as an open image) is required.");
/* 134 */       return null;
/*     */     }
/* 136 */     String[] titles = new String[wList.length];
/* 137 */     for (int i = 0; i < wList.length; i++) {
/* 138 */       ImagePlus imp = WindowManager.getImage(wList[i]);
/* 139 */       titles[i] = (imp != null ? imp.getTitle() : "");
/*     */     }
/* 141 */     if ((filterIndex < 0) || (filterIndex >= titles.length))
/* 142 */       filterIndex = 1;
/* 143 */     GenericDialog gd = new GenericDialog("FFT Filter");
/* 144 */     gd.addChoice("Filter:", titles, titles[filterIndex]);
/* 145 */     if (this.stackSize > 1)
/* 146 */       gd.addCheckbox("Process entire stack", processStack);
/* 147 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#fft-filter");
/* 148 */     gd.showDialog();
/* 149 */     if (gd.wasCanceled())
/* 150 */       return null;
/* 151 */     filterIndex = gd.getNextChoiceIndex();
/* 152 */     if (this.stackSize > 1)
/* 153 */       processStack = gd.getNextBoolean();
/* 154 */     ImagePlus filterImp = WindowManager.getImage(wList[filterIndex]);
/* 155 */     if (filterImp == this.imp) {
/* 156 */       IJ.error("FFT", "The filter cannot be the same as the image being filtered.");
/* 157 */       return null;
/*     */     }
/* 159 */     if (filterImp.getStackSize() > 1) {
/* 160 */       IJ.error("FFT", "The filter cannot be a stack.");
/* 161 */       return null;
/*     */     }
/* 163 */     ImageProcessor filter = filterImp.getProcessor();
/* 164 */     filter = filter.convertToByte(true);
/* 165 */     filter = resizeFilter(filter, size);
/*     */ 
/* 167 */     return filter;
/*     */   }
/*     */ 
/*     */   ImageProcessor resizeFilter(ImageProcessor ip, int maxN) {
/* 171 */     int width = ip.getWidth();
/* 172 */     int height = ip.getHeight();
/* 173 */     if ((width == maxN) && (height == maxN))
/* 174 */       return ip;
/* 175 */     showStatus("Scaling filter to " + maxN + "x" + maxN);
/* 176 */     return ip.resize(maxN, maxN);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.FFTCustomFilter
 * JD-Core Version:    0.6.2
 */