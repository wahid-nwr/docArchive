/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.StackWindow;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.LUT;
/*     */ 
/*     */ public class HyperStackConverter
/*     */   implements PlugIn
/*     */ {
/*     */   static final int C = 0;
/*     */   static final int Z = 1;
/*     */   static final int T = 2;
/*     */   static final int CZT = 0;
/*     */   static final int CTZ = 1;
/*     */   static final int ZCT = 2;
/*     */   static final int ZTC = 3;
/*     */   static final int TCZ = 4;
/*     */   static final int TZC = 5;
/*  17 */   static final String[] orders = { "xyczt(default)", "xyctz", "xyzct", "xyztc", "xytcz", "xytzc" };
/*  18 */   static int order = 0;
/*  19 */   static boolean splitRGB = true;
/*     */ 
/*     */   public void run(String arg) {
/*  22 */     if (arg.equals("new")) {
/*  23 */       newHyperStack(); return;
/*  24 */     }ImagePlus imp = IJ.getImage();
/*  25 */     if (arg.equals("stacktohs"))
/*  26 */       convertStackToHS(imp);
/*  27 */     else if (arg.equals("hstostack"))
/*  28 */       convertHSToStack(imp);
/*     */   }
/*     */ 
/*     */   void convertStackToHS(ImagePlus imp)
/*     */   {
/*  34 */     int nChannels = imp.getNChannels();
/*  35 */     int nSlices = imp.getNSlices();
/*  36 */     int nFrames = imp.getNFrames();
/*  37 */     int stackSize = imp.getImageStackSize();
/*  38 */     if (stackSize == 1) {
/*  39 */       IJ.error("Stack to HyperStack", "Stack required");
/*  40 */       return;
/*     */     }
/*  42 */     boolean rgb = imp.getBitDepth() == 24;
/*  43 */     String[] modes = { "Composite", "Color", "Grayscale" };
/*  44 */     GenericDialog gd = new GenericDialog("Convert to HyperStack");
/*  45 */     gd.addChoice("Order:", orders, orders[order]);
/*  46 */     gd.addNumericField("Channels (c):", nChannels, 0);
/*  47 */     gd.addNumericField("Slices (z):", nSlices, 0);
/*  48 */     gd.addNumericField("Frames (t):", nFrames, 0);
/*  49 */     gd.addChoice("Display Mode:", modes, modes[1]);
/*  50 */     if (rgb) {
/*  51 */       gd.setInsets(15, 0, 0);
/*  52 */       gd.addCheckbox("Convert RGB to 3 Channel Hyperstack", splitRGB);
/*     */     }
/*  54 */     gd.showDialog();
/*  55 */     if (gd.wasCanceled()) return;
/*  56 */     order = gd.getNextChoiceIndex();
/*  57 */     nChannels = (int)gd.getNextNumber();
/*  58 */     nSlices = (int)gd.getNextNumber();
/*  59 */     nFrames = (int)gd.getNextNumber();
/*  60 */     int mode = gd.getNextChoiceIndex();
/*  61 */     if (rgb)
/*  62 */       splitRGB = gd.getNextBoolean();
/*  63 */     if ((rgb) && (splitRGB == true)) {
/*  64 */       new CompositeConverter().run(mode == 0 ? "composite" : "color");
/*  65 */       return;
/*     */     }
/*  67 */     if ((rgb) && (nChannels > 1)) {
/*  68 */       IJ.error("HyperStack Converter", "RGB stacks are limited to one channel");
/*  69 */       return;
/*     */     }
/*  71 */     if (nChannels * nSlices * nFrames != stackSize) {
/*  72 */       IJ.error("HyperStack Converter", "channels x slices x frames <> stack size");
/*  73 */       return;
/*     */     }
/*  75 */     imp.setDimensions(nChannels, nSlices, nFrames);
/*  76 */     if ((order != 0) && (imp.getStack().isVirtual())) {
/*  77 */       IJ.error("HyperStack Converter", "Virtual stacks must by in XYCZT order.");
/*     */     } else {
/*  79 */       shuffle(imp, order);
/*  80 */       ImagePlus imp2 = imp;
/*  81 */       if ((nChannels > 1) && (imp.getBitDepth() != 24)) {
/*  82 */         LUT[] luts = imp.getLuts();
/*  83 */         if ((luts != null) && (luts.length < nChannels)) luts = null;
/*  84 */         imp2 = new CompositeImage(imp, mode + 1);
/*  85 */         if (luts != null)
/*  86 */           ((CompositeImage)imp2).setLuts(luts);
/*  87 */       } else if (imp.getClass().getName().indexOf("Image5D") != -1) {
/*  88 */         imp2 = imp.createImagePlus();
/*  89 */         imp2.setStack(imp.getTitle(), imp.getImageStack());
/*  90 */         imp2.setDimensions(imp.getNChannels(), imp.getNSlices(), imp.getNFrames());
/*  91 */         imp2.getProcessor().resetMinAndMax();
/*     */       }
/*  93 */       imp2.setOpenAsHyperStack(true);
/*  94 */       new StackWindow(imp2);
/*  95 */       if (imp != imp2) {
/*  96 */         imp2.setOverlay(imp.getOverlay());
/*  97 */         imp.hide();
/*  98 */         WindowManager.setCurrentWindow(imp2.getWindow());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void shuffle(ImagePlus imp, int order)
/*     */   {
/* 107 */     int nChannels = imp.getNChannels();
/* 108 */     int nSlices = imp.getNSlices();
/* 109 */     int nFrames = imp.getNFrames();
/* 110 */     int first = 0; int middle = 1; int last = 2;
/* 111 */     int nFirst = nChannels; int nMiddle = nSlices; int nLast = nFrames;
/* 112 */     switch (order) { case 1:
/* 113 */       first = 0; middle = 2; last = 1;
/* 114 */       nFirst = nChannels; nMiddle = nFrames; nLast = nSlices;
/* 115 */       break;
/*     */     case 2:
/* 116 */       first = 1; middle = 0; last = 2;
/* 117 */       nFirst = nSlices; nMiddle = nChannels; nLast = nFrames;
/* 118 */       break;
/*     */     case 3:
/* 119 */       first = 1; middle = 2; last = 0;
/* 120 */       nFirst = nSlices; nMiddle = nFrames; nLast = nChannels;
/* 121 */       break;
/*     */     case 4:
/* 122 */       first = 2; middle = 0; last = 1;
/* 123 */       nFirst = nFrames; nMiddle = nChannels; nLast = nSlices;
/* 124 */       break;
/*     */     case 5:
/* 125 */       first = 2; middle = 1; last = 0;
/* 126 */       nFirst = nFrames; nMiddle = nSlices; nLast = nChannels;
/*     */     }
/*     */ 
/* 129 */     if (order != 0) {
/* 130 */       ImageStack stack = imp.getImageStack();
/* 131 */       Object[] images1 = stack.getImageArray();
/* 132 */       Object[] images2 = new Object[images1.length];
/* 133 */       System.arraycopy(images1, 0, images2, 0, images1.length);
/* 134 */       String[] labels1 = stack.getSliceLabels();
/* 135 */       String[] labels2 = new String[labels1.length];
/* 136 */       System.arraycopy(labels1, 0, labels2, 0, labels1.length);
/* 137 */       int[] index = new int[3];
/* 138 */       for (index[2] = 0; index[2] < nFrames; index[2] += 1)
/* 139 */         for (index[1] = 0; index[1] < nSlices; index[1] += 1)
/* 140 */           for (index[0] = 0; index[0] < nChannels; index[0] += 1) {
/* 141 */             int dstIndex = index[0] + index[1] * nChannels + index[2] * nChannels * nSlices;
/* 142 */             int srcIndex = index[first] + index[middle] * nFirst + index[last] * nFirst * nMiddle;
/* 143 */             images1[dstIndex] = images2[srcIndex];
/* 144 */             labels1[dstIndex] = labels2[srcIndex];
/*     */           }
/*     */     }
/*     */   }
/*     */ 
/*     */   void convertHSToStack(ImagePlus imp)
/*     */   {
/* 152 */     if (!imp.isHyperStack()) return;
/* 153 */     ImagePlus imp2 = imp;
/* 154 */     if (imp.isComposite()) {
/* 155 */       ImageStack stack = imp.getStack();
/* 156 */       imp2 = imp.createImagePlus();
/* 157 */       imp2.setStack(imp.getTitle(), stack);
/* 158 */       int[] dim = imp.getDimensions();
/* 159 */       imp2.setDimensions(dim[2], dim[3], dim[4]);
/* 160 */       ImageProcessor ip2 = imp2.getProcessor();
/* 161 */       ip2.setColorModel(ip2.getDefaultColorModel());
/*     */     }
/* 163 */     imp2.setOpenAsHyperStack(false);
/* 164 */     new StackWindow(imp2);
/* 165 */     if (imp != imp2) {
/* 166 */       imp2.setOverlay(imp.getOverlay());
/* 167 */       imp.hide();
/*     */     }
/*     */   }
/*     */ 
/*     */   void newHyperStack() {
/* 172 */     IJ.runMacroFile("ij.jar:HyperStackMaker", "");
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.HyperStackConverter
 * JD-Core Version:    0.6.2
 */