/*     */ package ij;
/*     */ 
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ 
/*     */ public class Undo
/*     */ {
/*     */   public static final int NOTHING = 0;
/*     */   public static final int FILTER = 1;
/*     */   public static final int TYPE_CONVERSION = 2;
/*     */   public static final int PASTE = 3;
/*     */   public static final int COMPOUND_FILTER = 4;
/*     */   public static final int COMPOUND_FILTER_DONE = 5;
/*     */   public static final int TRANSFORM = 6;
/*     */   public static final int OVERLAY_ADDITION = 7;
/*  23 */   private static int whatToUndo = 0;
/*     */   private static int imageID;
/*  25 */   private static ImageProcessor ipCopy = null;
/*     */   private static ImagePlus impCopy;
/*     */   private static Calibration calCopy;
/*     */ 
/*     */   public static void setup(int what, ImagePlus imp)
/*     */   {
/*  30 */     if (imp == null) {
/*  31 */       whatToUndo = 0;
/*  32 */       reset();
/*  33 */       return;
/*     */     }
/*     */ 
/*  36 */     if ((what == 1) && (whatToUndo == 4))
/*  37 */       return;
/*  38 */     if (what == 5) {
/*  39 */       if (whatToUndo == 4)
/*  40 */         whatToUndo = what;
/*  41 */       return;
/*     */     }
/*  43 */     whatToUndo = what;
/*  44 */     imageID = imp.getID();
/*  45 */     if (what == 2) {
/*  46 */       ipCopy = imp.getProcessor();
/*  47 */       calCopy = (Calibration)imp.getCalibration().clone();
/*  48 */     } else if (what == 6) {
/*  49 */       impCopy = new ImagePlus(imp.getTitle(), imp.getProcessor().duplicate());
/*  50 */     } else if (what == 4) {
/*  51 */       ImageProcessor ip = imp.getProcessor();
/*  52 */       if (ip != null)
/*  53 */         ipCopy = ip.duplicate();
/*     */       else
/*  55 */         ipCopy = null;
/*  56 */     } else if (what == 7) {
/*  57 */       impCopy = null;
/*  58 */       ipCopy = null;
/*     */     } else {
/*  60 */       ipCopy = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void reset() {
/*  65 */     if ((whatToUndo == 4) || (whatToUndo == 7))
/*  66 */       return;
/*  67 */     whatToUndo = 0;
/*  68 */     imageID = 0;
/*  69 */     ipCopy = null;
/*  70 */     impCopy = null;
/*  71 */     calCopy = null;
/*     */   }
/*     */ 
/*     */   public static void undo()
/*     */   {
/*  77 */     ImagePlus imp = WindowManager.getCurrentImage();
/*     */ 
/*  79 */     if ((imp == null) || (imageID != imp.getID())) {
/*  80 */       if ((imp != null) && (!IJ.macroRunning())) {
/*  81 */         ImageProcessor ip2 = imp.getProcessor();
/*  82 */         ip2.swapPixelArrays();
/*  83 */         imp.updateAndDraw();
/*     */       } else {
/*  85 */         reset();
/*  86 */       }return;
/*     */     }
/*  88 */     switch (whatToUndo) {
/*     */     case 1:
/*  90 */       ImageProcessor ip = imp.getProcessor();
/*  91 */       if (ip != null) {
/*  92 */         if (!IJ.macroRunning()) {
/*  93 */           ip.swapPixelArrays();
/*  94 */           imp.updateAndDraw();
/*  95 */           return;
/*     */         }
/*  97 */         ip.reset();
/*  98 */         imp.updateAndDraw(); } break;
/*     */     case 2:
/*     */     case 4:
/*     */     case 5:
/* 105 */       if (ipCopy != null) {
/* 106 */         if ((whatToUndo == 2) && (calCopy != null))
/* 107 */           imp.setCalibration(calCopy);
/* 108 */         if (swapImages(new ImagePlus("", ipCopy), imp)) {
/* 109 */           imp.updateAndDraw();
/* 110 */           return;
/*     */         }
/* 112 */         imp.setProcessor(null, ipCopy); } break;
/*     */     case 6:
/* 116 */       if (impCopy != null) {
/* 117 */         if (swapImages(impCopy, imp)) {
/* 118 */           imp.updateAndDraw();
/* 119 */           return;
/*     */         }
/* 121 */         imp.setProcessor(impCopy.getTitle(), impCopy.getProcessor()); } break;
/*     */     case 3:
/* 125 */       Roi roi = imp.getRoi();
/* 126 */       if (roi != null)
/* 127 */         roi.abortPaste(); break;
/*     */     case 7:
/* 130 */       Overlay overlay = imp.getOverlay();
/* 131 */       if (overlay == null) {
/* 132 */         IJ.beep(); return;
/* 133 */       }int size = overlay.size();
/* 134 */       if (size > 0) {
/* 135 */         overlay.remove(size - 1);
/* 136 */         imp.draw();
/*     */       } else {
/* 138 */         IJ.beep();
/* 139 */         return;
/*     */       }
/* 141 */       return;
/*     */     }
/* 143 */     reset();
/*     */   }
/*     */ 
/*     */   static boolean swapImages(ImagePlus imp1, ImagePlus imp2) {
/* 147 */     if ((imp1.getWidth() != imp2.getWidth()) || (imp1.getHeight() != imp2.getHeight()) || (imp1.getBitDepth() != imp2.getBitDepth()) || (IJ.macroRunning()))
/*     */     {
/* 149 */       return false;
/* 150 */     }ImageProcessor ip1 = imp1.getProcessor();
/* 151 */     ImageProcessor ip2 = imp2.getProcessor();
/* 152 */     double min1 = ip1.getMin();
/* 153 */     double max1 = ip1.getMax();
/* 154 */     double min2 = ip2.getMin();
/* 155 */     double max2 = ip2.getMax();
/* 156 */     ip2.setSnapshotPixels(ip1.getPixels());
/* 157 */     ip2.swapPixelArrays();
/* 158 */     ip1.setPixels(ip2.getSnapshotPixels());
/* 159 */     ip2.setSnapshotPixels(null);
/* 160 */     ip1.setMinAndMax(min2, max2);
/* 161 */     ip2.setMinAndMax(min1, max1);
/* 162 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.Undo
 * JD-Core Version:    0.6.2
 */