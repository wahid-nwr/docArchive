/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.process.StackStatistics;
/*     */ 
/*     */ public class Filters
/*     */   implements PlugInFilter
/*     */ {
/*  11 */   private static double sd = Prefs.getDouble("noise.sd", 25.0D);
/*     */   private String arg;
/*     */   private ImagePlus imp;
/*     */   private int slice;
/*     */   private boolean canceled;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  18 */     this.arg = arg;
/*  19 */     this.imp = imp;
/*  20 */     if (imp != null) {
/*  21 */       Roi roi = imp.getRoi();
/*  22 */       if ((roi != null) && (!roi.isArea()))
/*  23 */         imp.killRoi();
/*     */     }
/*  25 */     int flags = IJ.setupDialog(imp, 93);
/*  26 */     if (((flags & 0x20) != 0) && (imp.getType() == 1) && (imp.getStackSize() > 1) && (arg.equals("invert"))) {
/*  27 */       invert16BitStack(imp);
/*  28 */       return 4096;
/*     */     }
/*  30 */     return flags;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip)
/*     */   {
/*  35 */     if (this.arg.equals("invert")) {
/*  36 */       ip.invert();
/*  37 */       return;
/*     */     }
/*     */ 
/*  40 */     if (this.arg.equals("smooth")) {
/*  41 */       ip.setSnapshotCopyMode(true);
/*  42 */       ip.smooth();
/*  43 */       ip.setSnapshotCopyMode(false);
/*  44 */       return;
/*     */     }
/*     */ 
/*  47 */     if (this.arg.equals("sharpen")) {
/*  48 */       ip.setSnapshotCopyMode(true);
/*  49 */       ip.sharpen();
/*  50 */       ip.setSnapshotCopyMode(false);
/*  51 */       return;
/*     */     }
/*     */ 
/*  54 */     if (this.arg.equals("edge")) {
/*  55 */       ip.setSnapshotCopyMode(true);
/*  56 */       ip.findEdges();
/*  57 */       ip.setSnapshotCopyMode(false);
/*  58 */       return;
/*     */     }
/*     */ 
/*  61 */     if (this.arg.equals("add")) {
/*  62 */       ip.noise(25.0D);
/*  63 */       return;
/*     */     }
/*     */ 
/*  66 */     if (this.arg.equals("noise")) {
/*  67 */       if (this.canceled)
/*  68 */         return;
/*  69 */       this.slice += 1;
/*  70 */       if (this.slice == 1) {
/*  71 */         GenericDialog gd = new GenericDialog("Gaussian Noise");
/*  72 */         gd.addNumericField("Standard Deviation:", sd, 2);
/*  73 */         gd.showDialog();
/*  74 */         if (gd.wasCanceled()) {
/*  75 */           this.canceled = true;
/*  76 */           return;
/*     */         }
/*  78 */         sd = gd.getNextNumber();
/*     */       }
/*  80 */       ip.noise(sd);
/*  81 */       IJ.register(Filters.class);
/*  82 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   void invert16BitStack(ImagePlus imp)
/*     */   {
/*  88 */     imp.killRoi();
/*  89 */     imp.getCalibration().disableDensityCalibration();
/*  90 */     ImageStatistics stats = new StackStatistics(imp);
/*  91 */     ImageStack stack = imp.getStack();
/*  92 */     int nslices = stack.getSize();
/*  93 */     int min = (int)stats.min; int range = (int)(stats.max - stats.min);
/*  94 */     int n = imp.getWidth() * imp.getHeight();
/*  95 */     for (int slice = 1; slice <= nslices; slice++) {
/*  96 */       ImageProcessor ip = stack.getProcessor(slice);
/*  97 */       short[] pixels = (short[])ip.getPixels();
/*  98 */       for (int i = 0; i < n; i++) {
/*  99 */         int before = pixels[i] & 0xFFFF;
/* 100 */         pixels[i] = ((short)(range - ((pixels[i] & 0xFFFF) - min)));
/*     */       }
/*     */     }
/* 103 */     imp.setStack(null, stack);
/* 104 */     imp.setDisplayRange(0.0D, range);
/* 105 */     imp.updateAndDraw();
/*     */   }
/*     */ 
/*     */   public static double getSD()
/*     */   {
/* 110 */     return sd;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Filters
 * JD-Core Version:    0.6.2
 */