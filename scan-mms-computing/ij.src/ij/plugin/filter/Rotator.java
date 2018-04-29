/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.Undo;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Color;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ 
/*     */ public class Rotator
/*     */   implements ExtendedPlugInFilter, DialogListener
/*     */ {
/*  11 */   private int flags = 32863;
/*  12 */   private static double angle = 15.0D;
/*     */   private static boolean fillWithBackground;
/*     */   private static boolean enlarge;
/*  15 */   private static int gridLines = 1;
/*     */   private ImagePlus imp;
/*     */   private int bitDepth;
/*     */   private boolean canEnlarge;
/*     */   private boolean isEnlarged;
/*     */   private GenericDialog gd;
/*     */   private PlugInFilterRunner pfr;
/*  22 */   private String[] methods = ImageProcessor.getInterpolationMethods();
/*  23 */   private static int interpolationMethod = 1;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp) {
/*  26 */     this.imp = imp;
/*  27 */     if (imp != null) {
/*  28 */       this.bitDepth = imp.getBitDepth();
/*  29 */       Roi roi = imp.getRoi();
/*  30 */       Rectangle r = roi != null ? roi.getBounds() : null;
/*  31 */       this.canEnlarge = ((r == null) || ((r.x == 0) && (r.y == 0) && (r.width == imp.getWidth()) && (r.height == imp.getHeight())));
/*     */     }
/*  33 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  37 */     if ((enlarge) && (this.gd.wasOKed())) synchronized (this) {
/*  38 */         if (!this.isEnlarged) {
/*  39 */           enlargeCanvas();
/*  40 */           this.isEnlarged = true;
/*     */         }
/*     */       }
/*  43 */     if (this.isEnlarged) {
/*  44 */       int slice = this.pfr.getSliceNumber();
/*  45 */       if (this.imp.getStackSize() == 1)
/*  46 */         ip = this.imp.getProcessor();
/*     */       else
/*  48 */         ip = this.imp.getStack().getProcessor(slice);
/*     */     }
/*  50 */     ip.setInterpolationMethod(interpolationMethod);
/*  51 */     if (fillWithBackground) {
/*  52 */       Color bgc = Toolbar.getBackgroundColor();
/*  53 */       if (this.bitDepth == 8)
/*  54 */         ip.setBackgroundValue(ip.getBestIndex(bgc));
/*  55 */       else if (this.bitDepth == 24)
/*  56 */         ip.setBackgroundValue(bgc.getRGB());
/*     */     } else {
/*  58 */       ip.setBackgroundValue(0.0D);
/*  59 */     }ip.rotate(angle);
/*  60 */     if (!this.gd.wasOKed())
/*  61 */       drawGridLines(gridLines);
/*  62 */     if ((this.isEnlarged) && (this.imp.getStackSize() == 1)) {
/*  63 */       this.imp.changes = true;
/*  64 */       this.imp.updateAndDraw();
/*  65 */       Undo.setup(5, this.imp);
/*     */     }
/*     */   }
/*     */ 
/*     */   void enlargeCanvas() {
/*  70 */     this.imp.unlock();
/*  71 */     if (this.imp.getStackSize() == 1)
/*  72 */       Undo.setup(4, this.imp);
/*  73 */     IJ.run("Select All");
/*  74 */     IJ.run("Rotate...", "angle=" + angle);
/*  75 */     Roi roi = this.imp.getRoi();
/*  76 */     Rectangle r = roi.getBounds();
/*  77 */     if (r.width < this.imp.getWidth()) r.width = this.imp.getWidth();
/*  78 */     if (r.height < this.imp.getHeight()) r.height = this.imp.getHeight();
/*  79 */     IJ.showStatus("Rotate: Enlarging...");
/*  80 */     IJ.run("Canvas Size...", "width=" + r.width + " height=" + r.height + " position=Center " + (fillWithBackground ? "" : "zero"));
/*  81 */     IJ.showStatus("Rotating...");
/*     */   }
/*     */ 
/*     */   void drawGridLines(int lines)
/*     */   {
/*  86 */     ImageCanvas ic = this.imp.getCanvas();
/*  87 */     if (ic == null) return;
/*  88 */     if (lines == 0) { ic.setDisplayList(null); return; }
/*  89 */     GeneralPath path = new GeneralPath();
/*  90 */     float width = this.imp.getWidth();
/*  91 */     float height = this.imp.getHeight();
/*  92 */     float xinc = width / lines;
/*  93 */     float yinc = height / lines;
/*  94 */     float xstart = xinc / 2.0F;
/*  95 */     float ystart = yinc / 2.0F;
/*  96 */     for (int i = 0; i < lines; i++) {
/*  97 */       path.moveTo(xstart + xinc * i, 0.0F);
/*  98 */       path.lineTo(xstart + xinc * i, height);
/*  99 */       path.moveTo(0.0F, ystart + yinc * i);
/* 100 */       path.lineTo(width, ystart + yinc * i);
/*     */     }
/* 102 */     ic.setDisplayList(path, null, null);
/*     */   }
/*     */ 
/*     */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
/* 106 */     this.pfr = pfr;
/* 107 */     String macroOptions = Macro.getOptions();
/* 108 */     if (macroOptions != null) {
/* 109 */       if (macroOptions.indexOf(" interpolate") != -1)
/* 110 */         macroOptions.replaceAll(" interpolate", " interpolation=Bilinear");
/* 111 */       else if (macroOptions.indexOf(" interpolation=") == -1)
/* 112 */         macroOptions = macroOptions + " interpolation=None";
/* 113 */       Macro.setOptions(macroOptions);
/*     */     }
/* 115 */     this.gd = new GenericDialog("Rotate", IJ.getInstance());
/* 116 */     this.gd.addNumericField("Angle (degrees):", angle, (int)angle == angle ? 1 : 2);
/* 117 */     this.gd.addNumericField("Grid Lines:", gridLines, 0);
/* 118 */     this.gd.addChoice("Interpolation:", this.methods, this.methods[interpolationMethod]);
/* 119 */     if ((this.bitDepth == 8) || (this.bitDepth == 24))
/* 120 */       this.gd.addCheckbox("Fill with Background Color", fillWithBackground);
/* 121 */     if (this.canEnlarge)
/* 122 */       this.gd.addCheckbox("Enlarge Image to Fit Result", enlarge);
/*     */     else
/* 124 */       enlarge = false;
/* 125 */     this.gd.addPreviewCheckbox(pfr);
/* 126 */     this.gd.addDialogListener(this);
/* 127 */     this.gd.showDialog();
/* 128 */     drawGridLines(0);
/* 129 */     if (this.gd.wasCanceled()) {
/* 130 */       return 4096;
/*     */     }
/* 132 */     if (!enlarge)
/* 133 */       this.flags |= 16777216;
/* 134 */     else if (imp.getStackSize() == 1)
/* 135 */       this.flags |= 128;
/* 136 */     return IJ.setupDialog(imp, this.flags);
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 140 */     angle = gd.getNextNumber();
/*     */ 
/* 142 */     if (gd.invalidNumber()) {
/* 143 */       if (gd.wasOKed()) IJ.error("Angle is invalid.");
/* 144 */       return false;
/*     */     }
/* 146 */     gridLines = (int)gd.getNextNumber();
/* 147 */     interpolationMethod = gd.getNextChoiceIndex();
/* 148 */     if ((this.bitDepth == 8) || (this.bitDepth == 24))
/* 149 */       fillWithBackground = gd.getNextBoolean();
/* 150 */     if (this.canEnlarge)
/* 151 */       enlarge = gd.getNextBoolean();
/* 152 */     return true;
/*     */   }
/*     */ 
/*     */   public void setNPasses(int nPasses)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Rotator
 * JD-Core Version:    0.6.2
 */