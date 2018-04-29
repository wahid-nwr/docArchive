/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.plugin.frame.ThresholdAdjuster;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.FloodFiller;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.AWTEvent;
/*     */ 
/*     */ public class Binary
/*     */   implements ExtendedPlugInFilter, DialogListener
/*     */ {
/*     */   static final int MAX_ITERATIONS = 100;
/*     */   static final String NO_OPERATION = "Nothing";
/*  17 */   static final String[] outputTypes = { "Overwrite", "8-bit", "16-bit", "32-bit" };
/*  18 */   static final String[] operations = { "Nothing", "Erode", "Dilate", "Open", "Close", "Outline", "Fill Holes", "Skeletonize" };
/*     */ 
/*  21 */   static int iterations = 1;
/*  22 */   static int count = 1;
/*  23 */   String operation = "Nothing";
/*     */   String arg;
/*     */   ImagePlus imp;
/*     */   PlugInFilterRunner pfr;
/*     */   boolean doOptions;
/*     */   boolean previewing;
/*     */   boolean escapePressed;
/*     */   int foreground;
/*     */   int background;
/*  32 */   int flags = 16941123;
/*     */   int nPasses;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  36 */     this.arg = arg;
/*  37 */     IJ.register(Binary.class);
/*  38 */     this.doOptions = arg.equals("options");
/*  39 */     if (this.doOptions) {
/*  40 */       if (imp == null) return 512;
/*  41 */       ImageProcessor ip = imp.getProcessor();
/*  42 */       if (!(ip instanceof ByteProcessor)) return 512;
/*  43 */       if (!((ByteProcessor)ip).isBinary()) return 512;
/*     */     }
/*  45 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
/*  49 */     if (this.doOptions) {
/*  50 */       this.imp = imp;
/*  51 */       this.pfr = pfr;
/*  52 */       GenericDialog gd = new GenericDialog("Binary Options");
/*  53 */       gd.addNumericField("Iterations (1-100):", iterations, 0, 3, "");
/*  54 */       gd.addNumericField("Count (1-8):", count, 0, 3, "");
/*  55 */       gd.addCheckbox("Black background", Prefs.blackBackground);
/*  56 */       gd.addCheckbox("Pad edges when eroding", Prefs.padEdges);
/*  57 */       gd.addChoice("EDM output:", outputTypes, outputTypes[EDM.getOutputType()]);
/*  58 */       if (imp != null) {
/*  59 */         gd.addChoice("Do:", operations, this.operation);
/*  60 */         gd.addPreviewCheckbox(pfr);
/*  61 */         gd.addDialogListener(this);
/*  62 */         this.previewing = true;
/*     */       }
/*  64 */       gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#options");
/*  65 */       gd.showDialog();
/*  66 */       this.previewing = false;
/*  67 */       if (gd.wasCanceled()) return 4096;
/*  68 */       if (imp == null) {
/*  69 */         dialogItemChanged(gd, null);
/*  70 */         return 4096;
/*     */       }
/*  72 */       return this.operation.equals("Nothing") ? 4096 : IJ.setupDialog(imp, this.flags);
/*     */     }
/*  74 */     if (!((ByteProcessor)imp.getProcessor()).isBinary()) {
/*  75 */       IJ.error("8-bit binary (black and white only) image required.");
/*  76 */       return 4096;
/*     */     }
/*  78 */     return IJ.setupDialog(imp, this.flags);
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
/*     */   {
/*  83 */     iterations = (int)gd.getNextNumber();
/*  84 */     count = (int)gd.getNextNumber();
/*  85 */     boolean bb = Prefs.blackBackground;
/*  86 */     Prefs.blackBackground = gd.getNextBoolean();
/*  87 */     if (Prefs.blackBackground != bb)
/*  88 */       ThresholdAdjuster.update();
/*  89 */     Prefs.padEdges = gd.getNextBoolean();
/*  90 */     EDM.setOutputType(gd.getNextChoiceIndex());
/*  91 */     boolean isInvalid = gd.invalidNumber();
/*  92 */     if (iterations < 1) { iterations = 1; isInvalid = true; }
/*  93 */     if (iterations > 100) { iterations = 100; isInvalid = true; }
/*  94 */     if (count < 1) { count = 1; isInvalid = true; }
/*  95 */     if (count > 8) { count = 8; isInvalid = true; }
/*  96 */     if (isInvalid) return false;
/*  97 */     if (this.imp != null) {
/*  98 */       this.operation = gd.getNextChoice();
/*  99 */       this.arg = this.operation.toLowerCase();
/*     */     }
/* 101 */     return true;
/*     */   }
/*     */ 
/*     */   public void setNPasses(int nPasses) {
/* 105 */     this.nPasses = nPasses;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/* 109 */     this.foreground = (Prefs.blackBackground ? 255 : 0);
/* 110 */     if (ip.isInvertedLut())
/* 111 */       this.foreground = (255 - this.foreground);
/* 112 */     this.background = (255 - this.foreground);
/* 113 */     ip.setSnapshotCopyMode(true);
/* 114 */     if (this.arg.equals("outline")) {
/* 115 */       outline(ip);
/* 116 */     } else if (this.arg.startsWith("fill")) {
/* 117 */       fill(ip, this.foreground, this.background);
/* 118 */     } else if (this.arg.startsWith("skel")) {
/* 119 */       ip.resetRoi(); skeletonize(ip);
/* 120 */     } else if ((this.arg.equals("erode")) || (this.arg.equals("dilate"))) {
/* 121 */       doIterations((ByteProcessor)ip, this.arg);
/* 122 */     } else if (this.arg.equals("open")) {
/* 123 */       doIterations(ip, "erode");
/* 124 */       doIterations(ip, "dilate");
/* 125 */     } else if (this.arg.equals("close")) {
/* 126 */       doIterations(ip, "dilate");
/* 127 */       doIterations(ip, "erode");
/*     */     }
/* 129 */     ip.setSnapshotCopyMode(false);
/* 130 */     ip.setBinaryThreshold();
/*     */   }
/*     */ 
/*     */   void doIterations(ImageProcessor ip, String mode) {
/* 134 */     if (this.escapePressed) return;
/* 135 */     if ((!this.previewing) && (iterations > 1))
/* 136 */       IJ.showStatus(this.arg + "... press ESC to cancel");
/* 137 */     for (int i = 0; i < iterations; i++) {
/* 138 */       if (Thread.currentThread().isInterrupted()) return;
/* 139 */       if (IJ.escapePressed()) {
/* 140 */         this.escapePressed = true;
/* 141 */         ip.reset();
/* 142 */         return;
/*     */       }
/* 144 */       if (this.nPasses <= 1) IJ.showProgress(i + 1, iterations);
/* 145 */       if (mode.equals("erode"))
/* 146 */         ((ByteProcessor)ip).erode(count, this.background);
/*     */       else
/* 148 */         ((ByteProcessor)ip).dilate(count, this.background);
/*     */     }
/*     */   }
/*     */ 
/*     */   void outline(ImageProcessor ip) {
/* 153 */     if (Prefs.blackBackground) ip.invert();
/* 154 */     ((ByteProcessor)ip).outline();
/* 155 */     if (Prefs.blackBackground) ip.invert(); 
/*     */   }
/*     */ 
/*     */   void skeletonize(ImageProcessor ip)
/*     */   {
/* 159 */     if (Prefs.blackBackground) ip.invert();
/* 160 */     boolean edgePixels = hasEdgePixels(ip);
/* 161 */     ImageProcessor ip2 = expand(ip, edgePixels);
/* 162 */     ((ByteProcessor)ip2).skeletonize();
/* 163 */     ip = shrink(ip, ip2, edgePixels);
/* 164 */     if (Prefs.blackBackground) ip.invert(); 
/*     */   }
/*     */ 
/*     */   boolean hasEdgePixels(ImageProcessor ip)
/*     */   {
/* 168 */     int width = ip.getWidth();
/* 169 */     int height = ip.getHeight();
/* 170 */     boolean edgePixels = false;
/* 171 */     for (int x = 0; x < width; x++) {
/* 172 */       if (ip.getPixel(x, 0) == this.foreground)
/* 173 */         edgePixels = true;
/*     */     }
/* 175 */     for (int x = 0; x < width; x++) {
/* 176 */       if (ip.getPixel(x, height - 1) == this.foreground)
/* 177 */         edgePixels = true;
/*     */     }
/* 179 */     for (int y = 0; y < height; y++) {
/* 180 */       if (ip.getPixel(0, y) == this.foreground)
/* 181 */         edgePixels = true;
/*     */     }
/* 183 */     for (int y = 0; y < height; y++) {
/* 184 */       if (ip.getPixel(width - 1, y) == this.foreground)
/* 185 */         edgePixels = true;
/*     */     }
/* 187 */     return edgePixels;
/*     */   }
/*     */ 
/*     */   ImageProcessor expand(ImageProcessor ip, boolean hasEdgePixels) {
/* 191 */     if (hasEdgePixels) {
/* 192 */       ImageProcessor ip2 = ip.createProcessor(ip.getWidth() + 2, ip.getHeight() + 2);
/* 193 */       if (this.foreground == 0) {
/* 194 */         ip2.setColor(255);
/* 195 */         ip2.fill();
/*     */       }
/* 197 */       ip2.insert(ip, 1, 1);
/*     */ 
/* 199 */       return ip2;
/*     */     }
/* 201 */     return ip;
/*     */   }
/*     */ 
/*     */   ImageProcessor shrink(ImageProcessor ip, ImageProcessor ip2, boolean hasEdgePixels) {
/* 205 */     if (hasEdgePixels) {
/* 206 */       int width = ip.getWidth();
/* 207 */       int height = ip.getHeight();
/* 208 */       for (int y = 0; y < height; y++)
/* 209 */         for (int x = 0; x < width; x++)
/* 210 */           ip.putPixel(x, y, ip2.getPixel(x + 1, y + 1));
/*     */     }
/* 212 */     return ip;
/*     */   }
/*     */ 
/*     */   void fill(ImageProcessor ip, int foreground, int background)
/*     */   {
/* 218 */     int width = ip.getWidth();
/* 219 */     int height = ip.getHeight();
/* 220 */     FloodFiller ff = new FloodFiller(ip);
/* 221 */     ip.setColor(127);
/* 222 */     for (int y = 0; y < height; y++) {
/* 223 */       if (ip.getPixel(0, y) == background) ff.fill(0, y);
/* 224 */       if (ip.getPixel(width - 1, y) == background) ff.fill(width - 1, y);
/*     */     }
/* 226 */     for (int x = 0; x < width; x++) {
/* 227 */       if (ip.getPixel(x, 0) == background) ff.fill(x, 0);
/* 228 */       if (ip.getPixel(x, height - 1) == background) ff.fill(x, height - 1);
/*     */     }
/* 230 */     byte[] pixels = (byte[])ip.getPixels();
/* 231 */     int n = width * height;
/* 232 */     for (int i = 0; i < n; i++)
/* 233 */       if (pixels[i] == 127)
/* 234 */         pixels[i] = ((byte)background);
/*     */       else
/* 236 */         pixels[i] = ((byte)foreground);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Binary
 * JD-Core Version:    0.6.2
 */