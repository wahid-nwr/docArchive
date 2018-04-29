/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.Menus;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.Roi;
/*     */ import ij.process.ImageConverter;
/*     */ import ij.process.StackConverter;
/*     */ 
/*     */ public class Converter
/*     */   implements PlugIn
/*     */ {
/*     */   public static boolean newWindowCreated;
/*     */   private ImagePlus imp;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  15 */     this.imp = WindowManager.getCurrentImage();
/*  16 */     if (this.imp != null) {
/*  17 */       if ((this.imp.isComposite()) && (arg.equals("RGB Color"))) {
/*  18 */         new RGBStackConverter().run("");
/*  19 */       } else if (this.imp.lock()) {
/*  20 */         convert(arg);
/*  21 */         this.imp.unlock();
/*     */       }
/*     */     }
/*  24 */     else IJ.noImage();
/*     */   }
/*     */ 
/*     */   public void convert(String item)
/*     */   {
/*  31 */     int type = this.imp.getType();
/*  32 */     ImageStack stack = null;
/*  33 */     if (this.imp.getStackSize() > 1)
/*  34 */       stack = this.imp.getStack();
/*  35 */     String msg = "Converting to " + item;
/*  36 */     IJ.showStatus(msg + "...");
/*  37 */     long start = System.currentTimeMillis();
/*  38 */     Roi roi = this.imp.getRoi();
/*  39 */     this.imp.killRoi();
/*  40 */     boolean saveChanges = this.imp.changes;
/*  41 */     this.imp.changes = (IJ.getApplet() == null);
/*  42 */     ImageWindow win = this.imp.getWindow();
/*     */     try {
/*  44 */       if (stack != null) {
/*  45 */         boolean wasVirtual = stack.isVirtual();
/*     */ 
/*  47 */         if ((stack.isRGB()) && (item.equals("RGB Color"))) {
/*  48 */           new ImageConverter(this.imp).convertRGBStackToRGB();
/*  49 */           if (win != null) new ImageWindow(this.imp, this.imp.getCanvas()); 
/*     */         }
/*  50 */         else if ((stack.isHSB()) && (item.equals("RGB Color"))) {
/*  51 */           new ImageConverter(this.imp).convertHSBToRGB();
/*  52 */           if (win != null) new ImageWindow(this.imp, this.imp.getCanvas()); 
/*     */         }
/*  53 */         else if (item.equals("8-bit")) {
/*  54 */           new StackConverter(this.imp).convertToGray8();
/*  55 */         } else if (item.equals("16-bit")) {
/*  56 */           new StackConverter(this.imp).convertToGray16();
/*  57 */         } else if (item.equals("32-bit")) {
/*  58 */           new StackConverter(this.imp).convertToGray32();
/*  59 */         } else if (item.equals("RGB Color")) {
/*  60 */           new StackConverter(this.imp).convertToRGB();
/*  61 */         } else if (item.equals("RGB Stack")) {
/*  62 */           new StackConverter(this.imp).convertToRGBHyperstack();
/*  63 */         } else if (item.equals("HSB Stack")) {
/*  64 */           new StackConverter(this.imp).convertToHSBHyperstack();
/*  65 */         } else if (item.equals("8-bit Color")) {
/*  66 */           int nColors = getNumber();
/*  67 */           if (nColors != 0)
/*  68 */             new StackConverter(this.imp).convertToIndexedColor(nColors); 
/*     */         } else { throw new IllegalArgumentException(); }
/*  70 */         if (wasVirtual) this.imp.setTitle(this.imp.getTitle()); 
/*     */       }
/*     */       else
/*     */       {
/*  73 */         Undo.setup(2, this.imp);
/*  74 */         ImageConverter ic = new ImageConverter(this.imp);
/*  75 */         if (item.equals("8-bit")) {
/*  76 */           ic.convertToGray8();
/*  77 */         } else if (item.equals("16-bit")) {
/*  78 */           ic.convertToGray16();
/*  79 */         } else if (item.equals("32-bit")) {
/*  80 */           ic.convertToGray32();
/*  81 */         } else if (item.equals("RGB Stack")) {
/*  82 */           Undo.reset();
/*  83 */           ic.convertToRGBStack();
/*  84 */         } else if (item.equals("HSB Stack")) {
/*  85 */           Undo.reset();
/*  86 */           ic.convertToHSB();
/*  87 */         } else if (item.equals("RGB Color")) {
/*  88 */           ic.convertToRGB();
/*  89 */         } else if (item.equals("8-bit Color")) {
/*  90 */           int nColors = getNumber();
/*  91 */           start = System.currentTimeMillis();
/*  92 */           if (nColors != 0)
/*  93 */             ic.convertRGBtoIndexedColor(nColors);
/*     */         } else {
/*  95 */           this.imp.changes = saveChanges;
/*  96 */           return;
/*     */         }
/*  98 */         IJ.showProgress(1.0D);
/*     */       }
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 103 */       unsupportedConversion(this.imp);
/* 104 */       IJ.showStatus("");
/* 105 */       Undo.reset();
/* 106 */       this.imp.changes = saveChanges;
/* 107 */       Menus.updateMenus();
/* 108 */       Macro.abort();
/* 109 */       return;
/*     */     }
/* 111 */     if (roi != null)
/* 112 */       this.imp.setRoi(roi);
/* 113 */     IJ.showTime(this.imp, start, "");
/* 114 */     this.imp.repaintWindow();
/* 115 */     Menus.updateMenus();
/*     */   }
/*     */ 
/*     */   void unsupportedConversion(ImagePlus imp) {
/* 119 */     IJ.error("Converter", "Supported Conversions:\n \n8-bit -> 16-bit*\n8-bit -> 32-bit*\n8-bit -> RGB Color*\n16-bit -> 8-bit*\n16-bit -> 32-bit*\n16-bit -> RGB Color*\n32-bit -> 8-bit*\n32-bit -> 16-bit\n32-bit -> RGB Color*\n8-bit Color -> 8-bit (grayscale)*\n8-bit Color -> RGB Color\nRGB Color -> 8-bit (grayscale)*\nRGB Color -> 8-bit Color*\nRGB Color -> RGB Stack*\nRGB Color -> HSB Stack*\nRGB Stack -> RGB Color\nHSB Stack -> RGB Color\n \n* works with stacks\n");
/*     */   }
/*     */ 
/*     */   int getNumber()
/*     */   {
/* 145 */     if (this.imp.getType() != 4)
/* 146 */       return 256;
/* 147 */     GenericDialog gd = new GenericDialog("MedianCut");
/* 148 */     gd.addNumericField("Number of Colors (2-256):", 256.0D, 0);
/* 149 */     gd.showDialog();
/* 150 */     if (gd.wasCanceled())
/* 151 */       return 0;
/* 152 */     int n = (int)gd.getNextNumber();
/* 153 */     if (n < 2) n = 2;
/* 154 */     if (n > 256) n = 256;
/* 155 */     return n;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Converter
 * JD-Core Version:    0.6.2
 */