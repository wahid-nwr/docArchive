/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.process.FHT;
/*     */ import ij.process.ImageProcessor;
/*     */ 
/*     */ public class FFTMath
/*     */   implements PlugIn
/*     */ {
/*     */   private static final int CONJUGATE_MULTIPLY = 0;
/*     */   private static final int MULTIPLY = 1;
/*     */   private static final int DIVIDE = 2;
/*  13 */   private static String[] ops = { "Correlate", "Convolve", "Deconvolve" };
/*     */   private static int index1;
/*     */   private static int index2;
/*  16 */   private static int operation = 0;
/*  17 */   private static boolean doInverse = true;
/*  18 */   private static String title = "Result";
/*     */   private ImagePlus imp1;
/*     */   private ImagePlus imp2;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  22 */     if (showDialog())
/*  23 */       doMath(this.imp1, this.imp2);
/*     */   }
/*     */ 
/*     */   public boolean showDialog() {
/*  27 */     int[] wList = WindowManager.getIDList();
/*  28 */     if (wList == null) {
/*  29 */       IJ.noImage();
/*  30 */       return false;
/*     */     }
/*  32 */     String[] titles = new String[wList.length];
/*  33 */     for (int i = 0; i < wList.length; i++) {
/*  34 */       ImagePlus imp = WindowManager.getImage(wList[i]);
/*  35 */       if (imp != null)
/*  36 */         titles[i] = imp.getTitle();
/*     */       else
/*  38 */         titles[i] = "";
/*     */     }
/*  40 */     if (index1 >= titles.length) index1 = 0;
/*  41 */     if (index2 >= titles.length) index2 = 0;
/*  42 */     GenericDialog gd = new GenericDialog("FFT Math");
/*  43 */     gd.addChoice("Image1: ", titles, titles[index1]);
/*  44 */     gd.addChoice("Operation:", ops, ops[operation]);
/*  45 */     gd.addChoice("Image2: ", titles, titles[index2]);
/*  46 */     gd.addStringField("Result:", title);
/*  47 */     gd.addCheckbox("Do inverse transform", doInverse);
/*  48 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/process.html#fft-math");
/*  49 */     gd.showDialog();
/*  50 */     if (gd.wasCanceled())
/*  51 */       return false;
/*  52 */     index1 = gd.getNextChoiceIndex();
/*  53 */     operation = gd.getNextChoiceIndex();
/*  54 */     index2 = gd.getNextChoiceIndex();
/*  55 */     title = gd.getNextString();
/*  56 */     doInverse = gd.getNextBoolean();
/*  57 */     String title1 = titles[index1];
/*  58 */     String title2 = titles[index2];
/*  59 */     this.imp1 = WindowManager.getImage(wList[index1]);
/*  60 */     this.imp2 = WindowManager.getImage(wList[index2]);
/*  61 */     return true;
/*     */   }
/*     */ 
/*     */   public void doMath(ImagePlus imp1, ImagePlus imp2) {
/*  65 */     FHT h2 = null;
/*     */ 
/*  67 */     ImageProcessor fht1 = (ImageProcessor)imp1.getProperty("FHT");
/*     */     FHT h1;
/*     */     FHT h1;
/*  68 */     if (fht1 != null) {
/*  69 */       h1 = new FHT(fht1);
/*     */     } else {
/*  71 */       IJ.showStatus("Converting to float");
/*  72 */       ImageProcessor ip1 = imp1.getProcessor();
/*  73 */       h1 = new FHT(ip1);
/*     */     }
/*  75 */     ImageProcessor fht2 = (ImageProcessor)imp2.getProperty("FHT");
/*  76 */     if (fht2 != null) {
/*  77 */       h2 = new FHT(fht2);
/*     */     } else {
/*  79 */       ImageProcessor ip2 = imp2.getProcessor();
/*  80 */       if (imp2 != imp1)
/*  81 */         h2 = new FHT(ip2);
/*     */     }
/*  83 */     if (!h1.powerOf2Size()) {
/*  84 */       IJ.error("FFT Math", "Images must be a power of 2 size (256x256, 512x512, etc.)");
/*  85 */       return;
/*     */     }
/*  87 */     if (imp1.getWidth() != imp2.getWidth()) {
/*  88 */       IJ.error("FFT Math", "Images must be the same size");
/*  89 */       return;
/*     */     }
/*  91 */     if (fht1 == null) {
/*  92 */       IJ.showStatus("Transform image1");
/*  93 */       h1.transform();
/*     */     }
/*  95 */     if (fht2 == null) {
/*  96 */       if (h2 == null) {
/*  97 */         h2 = new FHT(h1.duplicate());
/*     */       } else {
/*  99 */         IJ.showStatus("Transform image2");
/* 100 */         h2.transform();
/*     */       }
/*     */     }
/* 103 */     FHT result = null;
/* 104 */     switch (operation) {
/*     */     case 0:
/* 106 */       IJ.showStatus("Complex conjugate multiply");
/* 107 */       result = h1.conjugateMultiply(h2);
/* 108 */       break;
/*     */     case 1:
/* 110 */       IJ.showStatus("Fourier domain multiply");
/* 111 */       result = h1.multiply(h2);
/* 112 */       break;
/*     */     case 2:
/* 114 */       IJ.showStatus("Fourier domain divide");
/* 115 */       result = h1.divide(h2);
/*     */     }
/*     */ 
/* 118 */     if (doInverse) {
/* 119 */       IJ.showStatus("Inverse transform");
/* 120 */       result.inverseTransform();
/* 121 */       IJ.showStatus("Swap quadrants");
/* 122 */       result.swapQuadrants();
/* 123 */       IJ.showStatus("Display image");
/* 124 */       result.resetMinAndMax();
/* 125 */       new ImagePlus(title, result).show();
/*     */     } else {
/* 127 */       IJ.showStatus("Power spectrum");
/* 128 */       ImageProcessor ps = result.getPowerSpectrum();
/* 129 */       ImagePlus imp3 = new ImagePlus(title, ps.convertToFloat());
/* 130 */       result.quadrantSwapNeeded = true;
/* 131 */       imp3.setProperty("FHT", result);
/* 132 */       imp3.show();
/*     */     }
/* 134 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.FFTMath
 * JD-Core Version:    0.6.2
 */