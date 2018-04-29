/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.Undo;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ 
/*     */ public class CanvasResizer
/*     */   implements PlugIn
/*     */ {
/*  14 */   boolean zeroFill = Prefs.get("resizer.zero", false);
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  18 */     boolean fIsStack = false;
/*     */ 
/*  20 */     ImagePlus imp = IJ.getImage();
/*  21 */     int wOld = imp.getWidth();
/*  22 */     int hOld = imp.getHeight();
/*     */ 
/*  24 */     ImageStack stackOld = imp.getStack();
/*  25 */     if ((stackOld != null) && (stackOld.getSize() > 1)) {
/*  26 */       fIsStack = true;
/*     */     }
/*  28 */     String[] sPositions = { "Top-Left", "Top-Center", "Top-Right", "Center-Left", "Center", "Center-Right", "Bottom-Left", "Bottom-Center", "Bottom-Right" };
/*     */ 
/*  34 */     String strTitle = fIsStack ? "Resize Stack Canvas" : "Resize Image Canvas";
/*  35 */     GenericDialog gd = new GenericDialog(strTitle);
/*  36 */     gd.addNumericField("Width:", wOld, 0, 5, "pixels");
/*  37 */     gd.addNumericField("Height:", hOld, 0, 5, "pixels");
/*  38 */     gd.addChoice("Position:", sPositions, sPositions[4]);
/*  39 */     gd.addCheckbox("Zero Fill", this.zeroFill);
/*  40 */     gd.showDialog();
/*  41 */     if (gd.wasCanceled()) {
/*  42 */       return;
/*     */     }
/*  44 */     int wNew = (int)gd.getNextNumber();
/*  45 */     int hNew = (int)gd.getNextNumber();
/*  46 */     int iPos = gd.getNextChoiceIndex();
/*  47 */     this.zeroFill = gd.getNextBoolean();
/*  48 */     Prefs.set("resizer.zero", this.zeroFill);
/*     */ 
/*  51 */     int xC = (wNew - wOld) / 2;
/*  52 */     int xR = wNew - wOld;
/*  53 */     int yC = (hNew - hOld) / 2;
/*  54 */     int yB = hNew - hOld;
/*     */     int xOff;
/*     */     int yOff;
/*  56 */     switch (iPos) {
/*     */     case 0:
/*  58 */       xOff = 0; yOff = 0; break;
/*     */     case 1:
/*  60 */       xOff = xC; yOff = 0; break;
/*     */     case 2:
/*  62 */       xOff = xR; yOff = 0; break;
/*     */     case 3:
/*  64 */       xOff = 0; yOff = yC; break;
/*     */     case 4:
/*  66 */       xOff = xC; yOff = yC; break;
/*     */     case 5:
/*  68 */       xOff = xR; yOff = yC; break;
/*     */     case 6:
/*  70 */       xOff = 0; yOff = yB; break;
/*     */     case 7:
/*  72 */       xOff = xC; yOff = yB; break;
/*     */     case 8:
/*  74 */       xOff = xR; yOff = yB; break;
/*     */     default:
/*  76 */       xOff = xC; yOff = yC;
/*     */     }
/*     */ 
/*  79 */     if (fIsStack) {
/*  80 */       ImageStack stackNew = expandStack(stackOld, wNew, hNew, xOff, yOff);
/*  81 */       imp.setStack(null, stackNew);
/*     */     } else {
/*  83 */       if (!IJ.macroRunning())
/*  84 */         Undo.setup(4, imp);
/*  85 */       ImageProcessor newIP = expandImage(imp.getProcessor(), wNew, hNew, xOff, yOff);
/*  86 */       imp.setProcessor(null, newIP);
/*  87 */       if (!IJ.macroRunning())
/*  88 */         Undo.setup(5, imp);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ImageStack expandStack(ImageStack stackOld, int wNew, int hNew, int xOff, int yOff) {
/*  93 */     int nFrames = stackOld.getSize();
/*  94 */     ImageProcessor ipOld = stackOld.getProcessor(1);
/*  95 */     Color colorBack = Toolbar.getBackgroundColor();
/*     */ 
/*  97 */     ImageStack stackNew = new ImageStack(wNew, hNew, stackOld.getColorModel());
/*     */ 
/* 100 */     for (int i = 1; i <= nFrames; i++) {
/* 101 */       IJ.showProgress(i / nFrames);
/* 102 */       ImageProcessor ipNew = ipOld.createProcessor(wNew, hNew);
/* 103 */       if (this.zeroFill)
/* 104 */         ipNew.setValue(0.0D);
/*     */       else
/* 106 */         ipNew.setColor(colorBack);
/* 107 */       ipNew.fill();
/* 108 */       ipNew.insert(stackOld.getProcessor(i), xOff, yOff);
/* 109 */       stackNew.addSlice(stackOld.getSliceLabel(i), ipNew);
/*     */     }
/* 111 */     return stackNew;
/*     */   }
/*     */ 
/*     */   public ImageProcessor expandImage(ImageProcessor ipOld, int wNew, int hNew, int xOff, int yOff) {
/* 115 */     ImageProcessor ipNew = ipOld.createProcessor(wNew, hNew);
/* 116 */     if (this.zeroFill)
/* 117 */       ipNew.setValue(0.0D);
/*     */     else
/* 119 */       ipNew.setColor(Toolbar.getBackgroundColor());
/* 120 */     ipNew.fill();
/* 121 */     ipNew.insert(ipOld, xOff, yOff);
/* 122 */     return ipNew;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.CanvasResizer
 * JD-Core Version:    0.6.2
 */