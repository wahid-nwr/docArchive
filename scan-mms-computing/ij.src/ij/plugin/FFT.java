/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.measure.Measurements;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FHT;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.process.StackProcessor;
/*     */ import ij.util.Tools;
/*     */ 
/*     */ public class FFT
/*     */   implements PlugIn, Measurements
/*     */ {
/*  24 */   static boolean displayFFT = true;
/*     */   public static boolean displayRawPS;
/*     */   public static boolean displayFHT;
/*     */   public static boolean displayComplex;
/*     */   public static String fileName;
/*     */   private ImagePlus imp;
/*     */   private boolean padded;
/*     */   private int originalWidth;
/*     */   private int originalHeight;
/*  34 */   private int stackSize = 1;
/*  35 */   private int slice = 1;
/*     */   private boolean doFFT;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  39 */     if (arg.equals("options")) {
/*  40 */       showDialog();
/*  41 */       if (this.doFFT) arg = "fft"; else return;
/*     */     }
/*  43 */     this.imp = IJ.getImage();
/*  44 */     if (arg.equals("redisplay")) {
/*  45 */       redisplayPowerSpectrum(); return;
/*  46 */     }if (arg.equals("swap")) {
/*  47 */       swapQuadrants(this.imp.getStack()); this.imp.updateAndDraw(); return;
/*  48 */     }if (arg.equals("inverse")) {
/*  49 */       if (this.imp.getTitle().startsWith("FHT of")) {
/*  50 */         doFHTInverseTransform(); return;
/*  51 */       }if (this.imp.getStackSize() == 2) {
/*  52 */         doComplexInverseTransform(); return;
/*     */       }
/*     */     }
/*  54 */     ImageProcessor ip = this.imp.getProcessor();
/*  55 */     Object obj = this.imp.getProperty("FHT");
/*  56 */     FHT fht = (obj instanceof FHT) ? (FHT)obj : null;
/*  57 */     this.stackSize = this.imp.getStackSize();
/*     */ 
/*  59 */     if ((fht == null) && (arg.equals("inverse"))) {
/*  60 */       IJ.error("FFT", "Frequency domain image required");
/*     */       return;
/*     */     }
/*     */     boolean inverse;
/*  63 */     if (fht != null) {
/*  64 */       boolean inverse = true;
/*  65 */       this.imp.killRoi();
/*     */     } else {
/*  67 */       if (this.imp.getRoi() != null)
/*  68 */         ip = ip.crop();
/*  69 */       fht = newFHT(ip);
/*  70 */       inverse = false;
/*     */     }
/*  72 */     if (inverse) {
/*  73 */       doInverseTransform(fht);
/*     */     } else {
/*  75 */       fileName = this.imp.getTitle();
/*  76 */       doForewardTransform(fht);
/*     */     }
/*  78 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   void doInverseTransform(FHT fht) {
/*  82 */     fht = fht.getCopy();
/*  83 */     doMasking(fht);
/*  84 */     showStatus("Inverse transform");
/*  85 */     fht.inverseTransform();
/*  86 */     if (fht.quadrantSwapNeeded)
/*  87 */       fht.swapQuadrants();
/*  88 */     fht.resetMinAndMax();
/*  89 */     ImageProcessor ip2 = fht;
/*  90 */     if (fht.originalWidth > 0) {
/*  91 */       fht.setRoi(0, 0, fht.originalWidth, fht.originalHeight);
/*  92 */       ip2 = fht.crop();
/*     */     }
/*  94 */     int bitDepth = fht.originalBitDepth > 0 ? fht.originalBitDepth : this.imp.getBitDepth();
/*  95 */     switch (bitDepth) { case 8:
/*  96 */       ip2 = ip2.convertToByte(false); break;
/*     */     case 16:
/*  97 */       ip2 = ip2.convertToShort(false); break;
/*     */     case 24:
/*  99 */       showStatus("Setting brightness");
/* 100 */       if ((fht.rgb == null) || (ip2 == null)) {
/* 101 */         IJ.error("FFT", "Unable to set brightness");
/* 102 */         return;
/*     */       }
/* 104 */       ColorProcessor rgb = (ColorProcessor)fht.rgb.duplicate();
/* 105 */       rgb.setBrightness((FloatProcessor)ip2);
/* 106 */       ip2 = rgb;
/* 107 */       fht.rgb = null;
/* 108 */       break;
/*     */     case 32:
/*     */     }
/* 111 */     if ((bitDepth != 24) && (fht.originalColorModel != null))
/* 112 */       ip2.setColorModel(fht.originalColorModel);
/* 113 */     String title = this.imp.getTitle();
/* 114 */     if (title.startsWith("FFT of "))
/* 115 */       title = title.substring(7, title.length());
/* 116 */     ImagePlus imp2 = new ImagePlus("Inverse FFT of " + title, ip2);
/* 117 */     imp2.setCalibration(this.imp.getCalibration());
/* 118 */     imp2.show();
/*     */   }
/*     */ 
/*     */   void doForewardTransform(FHT fht) {
/* 122 */     showStatus("Foreward transform");
/* 123 */     fht.transform();
/* 124 */     showStatus("Calculating power spectrum");
/* 125 */     ImageProcessor ps = fht.getPowerSpectrum();
/* 126 */     if ((!displayFHT) && (!displayComplex) && (!displayRawPS))
/* 127 */       displayFFT = true;
/* 128 */     if (displayFFT) {
/* 129 */       ImagePlus imp2 = new ImagePlus("FFT of " + this.imp.getTitle(), ps);
/* 130 */       imp2.show();
/* 131 */       imp2.setProperty("FHT", fht);
/* 132 */       imp2.setCalibration(this.imp.getCalibration());
/*     */     }
/*     */   }
/*     */ 
/*     */   FHT newFHT(ImageProcessor ip)
/*     */   {
/*     */     FHT fht;
/* 138 */     if ((ip instanceof ColorProcessor)) {
/* 139 */       showStatus("Extracting brightness");
/* 140 */       ImageProcessor ip2 = ((ColorProcessor)ip).getBrightness();
/* 141 */       FHT fht = new FHT(pad(ip2));
/* 142 */       fht.rgb = ((ColorProcessor)ip.duplicate());
/*     */     } else {
/* 144 */       fht = new FHT(pad(ip));
/* 145 */     }if (this.padded) {
/* 146 */       fht.originalWidth = this.originalWidth;
/* 147 */       fht.originalHeight = this.originalHeight;
/*     */     }
/* 149 */     fht.originalBitDepth = this.imp.getBitDepth();
/* 150 */     fht.originalColorModel = ip.getColorModel();
/* 151 */     return fht;
/*     */   }
/*     */ 
/*     */   ImageProcessor pad(ImageProcessor ip) {
/* 155 */     this.originalWidth = ip.getWidth();
/* 156 */     this.originalHeight = ip.getHeight();
/* 157 */     int maxN = Math.max(this.originalWidth, this.originalHeight);
/* 158 */     int i = 2;
/* 159 */     while (i < maxN) i *= 2;
/* 160 */     if ((i == maxN) && (this.originalWidth == this.originalHeight)) {
/* 161 */       this.padded = false;
/* 162 */       return ip;
/*     */     }
/* 164 */     maxN = i;
/* 165 */     showStatus("Padding to " + maxN + "x" + maxN);
/* 166 */     ImageStatistics stats = ImageStatistics.getStatistics(ip, 2, null);
/* 167 */     ImageProcessor ip2 = ip.createProcessor(maxN, maxN);
/* 168 */     ip2.setValue(stats.mean);
/* 169 */     ip2.fill();
/* 170 */     ip2.insert(ip, 0, 0);
/* 171 */     this.padded = true;
/* 172 */     Undo.reset();
/*     */ 
/* 174 */     return ip2;
/*     */   }
/*     */ 
/*     */   void showStatus(String msg) {
/* 178 */     if (this.stackSize > 1)
/* 179 */       IJ.showStatus("FFT: " + this.slice + "/" + this.stackSize);
/*     */     else
/* 181 */       IJ.showStatus(msg);
/*     */   }
/*     */ 
/*     */   void doMasking(FHT ip) {
/* 185 */     if (this.stackSize > 1)
/* 186 */       return;
/* 187 */     float[] fht = (float[])ip.getPixels();
/* 188 */     ImageProcessor mask = this.imp.getProcessor();
/* 189 */     mask = mask.convertToByte(false);
/* 190 */     if ((mask.getWidth() != ip.getWidth()) || (mask.getHeight() != ip.getHeight()))
/* 191 */       return;
/* 192 */     ImageStatistics stats = ImageStatistics.getStatistics(mask, 16, null);
/* 193 */     if ((stats.histogram[0] == 0) && (stats.histogram['ÿ'] == 0))
/* 194 */       return;
/* 195 */     boolean passMode = stats.histogram['ÿ'] != 0;
/* 196 */     IJ.showStatus("Masking: " + (passMode ? "pass" : "filter"));
/* 197 */     mask = mask.duplicate();
/* 198 */     if (passMode)
/* 199 */       changeValuesAndSymmetrize(mask, (byte)-1, (byte)0);
/*     */     else {
/* 201 */       changeValuesAndSymmetrize(mask, (byte)0, (byte)-1);
/*     */     }
/* 203 */     for (int i = 0; i < 3; i++) {
/* 204 */       smooth(mask);
/*     */     }
/* 206 */     if ((IJ.debugMode) || (IJ.altKeyDown()))
/* 207 */       new ImagePlus("mask", mask.duplicate()).show();
/* 208 */     ip.swapQuadrants(mask);
/* 209 */     byte[] maskPixels = (byte[])mask.getPixels();
/* 210 */     for (int i = 0; i < fht.length; i++)
/* 211 */       fht[i] = ((float)(fht[i] * (maskPixels[i] & 0xFF) / 255.0D));
/*     */   }
/*     */ 
/*     */   void changeValuesAndSymmetrize(ImageProcessor ip, byte v1, byte v2)
/*     */   {
/* 221 */     byte[] pixels = (byte[])ip.getPixels();
/* 222 */     int n = ip.getWidth();
/* 223 */     for (int i = 0; i < pixels.length; i++)
/* 224 */       if (pixels[i] == v1) {
/* 225 */         if (i % n == 0) {
/* 226 */           if (i > 0) pixels[(n * n - i)] = v1; 
/*     */         }
/* 227 */         else if (i < n)
/* 228 */           pixels[(n - i)] = v1;
/*     */         else
/* 230 */           pixels[(n * (n + 1) - i)] = v1;
/*     */       }
/* 232 */       else pixels[i] = v2;
/*     */   }
/*     */ 
/*     */   static void smooth(ImageProcessor ip)
/*     */   {
/* 240 */     byte[] pixels = (byte[])ip.getPixels();
/* 241 */     byte[] pixels2 = (byte[])pixels.clone();
/* 242 */     int n = ip.getWidth();
/* 243 */     int[] iMinus = new int[n];
/* 244 */     int[] iPlus = new int[n];
/* 245 */     for (int i = 0; i < n; i++) {
/* 246 */       iMinus[i] = ((i - 1 + n) % n);
/* 247 */       iPlus[i] = ((i + 1) % n);
/*     */     }
/* 249 */     for (int y = 0; y < n; y++) {
/* 250 */       int offset1 = n * iMinus[y];
/* 251 */       int offset2 = n * y;
/* 252 */       int offset3 = n * iPlus[y];
/* 253 */       for (int x = 0; x < n; x++) {
/* 254 */         int sum = (pixels2[(offset1 + iMinus[x])] & 0xFF) + (pixels2[(offset1 + x)] & 0xFF) + (pixels2[(offset1 + iPlus[x])] & 0xFF) + (pixels2[(offset2 + iMinus[x])] & 0xFF) + (pixels2[(offset2 + x)] & 0xFF) + (pixels2[(offset2 + iPlus[x])] & 0xFF) + (pixels2[(offset3 + iMinus[x])] & 0xFF) + (pixels2[(offset3 + x)] & 0xFF) + (pixels2[(offset3 + iPlus[x])] & 0xFF);
/*     */ 
/* 263 */         pixels[(offset2 + x)] = ((byte)((sum + 4) / 9));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void redisplayPowerSpectrum() {
/* 269 */     FHT fht = (FHT)this.imp.getProperty("FHT");
/* 270 */     if (fht == null) {
/* 271 */       IJ.error("FFT", "Frequency domain image required"); return;
/* 272 */     }ImageProcessor ps = fht.getPowerSpectrum();
/* 273 */     this.imp.setProcessor(null, ps);
/*     */   }
/*     */ 
/*     */   void swapQuadrants(ImageStack stack) {
/* 277 */     FHT fht = new FHT(new FloatProcessor(1, 1));
/* 278 */     for (int i = 1; i <= stack.getSize(); i++)
/* 279 */       fht.swapQuadrants(stack.getProcessor(i));
/*     */   }
/*     */ 
/*     */   void showDialog() {
/* 283 */     GenericDialog gd = new GenericDialog("FFT Options");
/* 284 */     gd.setInsets(0, 20, 0);
/* 285 */     gd.addMessage("Display:");
/* 286 */     gd.setInsets(5, 35, 0);
/* 287 */     gd.addCheckbox("FFT window", displayFFT);
/* 288 */     gd.setInsets(0, 35, 0);
/* 289 */     gd.addCheckbox("Raw power spectrum", displayRawPS);
/* 290 */     gd.setInsets(0, 35, 0);
/* 291 */     gd.addCheckbox("Fast Hartley Transform", displayFHT);
/* 292 */     gd.setInsets(0, 35, 0);
/* 293 */     gd.addCheckbox("Complex Fourier Transform", displayComplex);
/* 294 */     gd.setInsets(8, 20, 0);
/* 295 */     gd.addCheckbox("Do forward transform", false);
/* 296 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#fft-options");
/* 297 */     gd.showDialog();
/* 298 */     if (gd.wasCanceled())
/* 299 */       return;
/* 300 */     displayFFT = gd.getNextBoolean();
/* 301 */     displayRawPS = gd.getNextBoolean();
/* 302 */     displayFHT = gd.getNextBoolean();
/* 303 */     displayComplex = gd.getNextBoolean();
/* 304 */     this.doFFT = gd.getNextBoolean();
/*     */   }
/*     */ 
/*     */   void doFHTInverseTransform() {
/* 308 */     FHT fht = new FHT(this.imp.getProcessor().duplicate());
/* 309 */     fht.inverseTransform();
/* 310 */     fht.resetMinAndMax();
/* 311 */     String name = WindowManager.getUniqueName(this.imp.getTitle().substring(7));
/* 312 */     new ImagePlus(name, fht).show();
/*     */   }
/*     */ 
/*     */   void doComplexInverseTransform() {
/* 316 */     ImageStack stack = this.imp.getStack();
/* 317 */     if (!stack.getSliceLabel(1).equals("Real"))
/* 318 */       return;
/* 319 */     int maxN = this.imp.getWidth();
/* 320 */     swapQuadrants(stack);
/* 321 */     float[] rein = (float[])stack.getPixels(1);
/* 322 */     float[] imin = (float[])stack.getPixels(2);
/* 323 */     float[] reout = new float[maxN * maxN];
/* 324 */     float[] imout = new float[maxN * maxN];
/* 325 */     c2c2DFFT(rein, imin, maxN, reout, imout);
/* 326 */     ImageStack stack2 = new ImageStack(maxN, maxN);
/* 327 */     swapQuadrants(stack);
/* 328 */     stack2.addSlice("Real", reout);
/* 329 */     stack2.addSlice("Imaginary", imout);
/* 330 */     stack2 = unpad(stack2);
/* 331 */     String name = WindowManager.getUniqueName(this.imp.getTitle().substring(10));
/* 332 */     ImagePlus imp2 = new ImagePlus(name, stack2);
/* 333 */     imp2.getProcessor().resetMinAndMax();
/* 334 */     imp2.show();
/*     */   }
/*     */ 
/*     */   ImageStack unpad(ImageStack stack) {
/* 338 */     Object w = this.imp.getProperty("FFT width");
/* 339 */     Object h = this.imp.getProperty("FFT height");
/* 340 */     if ((w == null) || (h == null)) return stack;
/* 341 */     int width = (int)Tools.parseDouble((String)w, 0.0D);
/* 342 */     int height = (int)Tools.parseDouble((String)h, 0.0D);
/* 343 */     if ((width == 0) || (height == 0) || ((width == stack.getWidth()) && (height == stack.getHeight())))
/* 344 */       return stack;
/* 345 */     StackProcessor sp = new StackProcessor(stack, null);
/* 346 */     ImageStack stack2 = sp.crop(0, 0, width, height);
/* 347 */     return stack2;
/*     */   }
/*     */ 
/*     */   void c2c2DFFT(float[] rein, float[] imin, int maxN, float[] reout, float[] imout)
/*     */   {
/* 354 */     FHT fht = new FHT(new FloatProcessor(maxN, maxN));
/* 355 */     float[] fhtpixels = (float[])fht.getPixels();
/*     */ 
/* 357 */     for (int iy = 0; iy < maxN; iy++)
/* 358 */       cplxFHT(iy, maxN, rein, imin, false, fhtpixels);
/* 359 */     fht.inverseTransform();
/*     */ 
/* 361 */     float[] hlp = new float[maxN * maxN];
/* 362 */     System.arraycopy(fhtpixels, 0, hlp, 0, maxN * maxN);
/*     */ 
/* 364 */     for (int iy = 0; iy < maxN; iy++)
/* 365 */       cplxFHT(iy, maxN, rein, imin, true, fhtpixels);
/* 366 */     fht.inverseTransform();
/* 367 */     System.arraycopy(hlp, 0, reout, 0, maxN * maxN);
/* 368 */     System.arraycopy(fhtpixels, 0, imout, 0, maxN * maxN);
/*     */   }
/*     */ 
/*     */   void cplxFHT(int row, int maxN, float[] re, float[] im, boolean reim, float[] fht)
/*     */   {
/* 375 */     int base = row * maxN;
/* 376 */     int offs = (maxN - row) % maxN * maxN;
/* 377 */     if (!reim)
/* 378 */       for (int c = 0; c < maxN; c++) {
/* 379 */         int l = offs + (maxN - c) % maxN;
/* 380 */         fht[(base + c)] = ((re[(base + c)] + re[l] - (im[(base + c)] - im[l])) * 0.5F);
/*     */       }
/*     */     else
/* 383 */       for (int c = 0; c < maxN; c++) {
/* 384 */         int l = offs + (maxN - c) % maxN;
/* 385 */         fht[(base + c)] = ((im[(base + c)] + im[l] + (re[(base + c)] - re[l])) * 0.5F);
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.FFT
 * JD-Core Version:    0.6.2
 */