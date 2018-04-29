/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.print.PageFormat;
/*     */ import java.awt.print.Printable;
/*     */ import java.awt.print.PrinterException;
/*     */ import java.awt.print.PrinterJob;
/*     */ 
/*     */ public class Printer
/*     */   implements PlugInFilter, Printable
/*     */ {
/*     */   private ImagePlus imp;
/*  13 */   private static double scaling = 100.0D;
/*     */   private static boolean drawBorder;
/*  15 */   private static boolean center = true;
/*     */   private static boolean label;
/*     */   private static boolean printSelection;
/*     */   private static boolean rotate;
/*     */   private static boolean actualSize;
/*  20 */   private static int fontSize = 12;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp) {
/*  23 */     if (arg.equals("setup")) {
/*  24 */       pageSetup(); return 4096;
/*  25 */     }this.imp = imp;
/*  26 */     IJ.register(Printer.class);
/*  27 */     return 159;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  31 */     print(this.imp);
/*     */   }
/*     */ 
/*     */   void pageSetup() {
/*  35 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  36 */     Roi roi = imp != null ? imp.getRoi() : null;
/*  37 */     boolean isRoi = (roi != null) && (roi.isArea());
/*  38 */     GenericDialog gd = new GenericDialog("Page Setup");
/*  39 */     gd.addNumericField("Scale:", scaling, 0, 3, "%");
/*  40 */     gd.addCheckbox("Draw border", drawBorder);
/*  41 */     gd.addCheckbox("Center on page", center);
/*  42 */     gd.addCheckbox("Print title", label);
/*  43 */     if (isRoi)
/*  44 */       gd.addCheckbox("Selection only", printSelection);
/*  45 */     gd.addCheckbox("Rotate 90°", rotate);
/*  46 */     gd.addCheckbox("Print_actual size", actualSize);
/*  47 */     if (imp != null)
/*  48 */       gd.enableYesNoCancel(" OK ", "Print");
/*  49 */     gd.showDialog();
/*  50 */     if (gd.wasCanceled())
/*  51 */       return;
/*  52 */     scaling = gd.getNextNumber();
/*  53 */     if (scaling < 5.0D) scaling = 5.0D;
/*  54 */     drawBorder = gd.getNextBoolean();
/*  55 */     center = gd.getNextBoolean();
/*  56 */     label = gd.getNextBoolean();
/*  57 */     if (isRoi)
/*  58 */       printSelection = gd.getNextBoolean();
/*     */     else
/*  60 */       printSelection = false;
/*  61 */     rotate = gd.getNextBoolean();
/*  62 */     actualSize = gd.getNextBoolean();
/*  63 */     if ((!gd.wasOKed()) && (imp != null)) {
/*  64 */       this.imp = imp;
/*  65 */       print(imp);
/*     */     }
/*     */   }
/*     */ 
/*     */   void print(ImagePlus imp) {
/*  70 */     PrinterJob pj = PrinterJob.getPrinterJob();
/*  71 */     pj.setPrintable(this);
/*     */ 
/*  73 */     if ((IJ.macroRunning()) || (pj.printDialog())) {
/*  74 */       imp.startTiming();
/*     */       try { pj.print();
/*     */       } catch (PrinterException e) {
/*  77 */         IJ.log("" + e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int print(Graphics g, PageFormat pf, int pageIndex) {
/*  83 */     if (pageIndex != 0) return 1;
/*  84 */     Roi roi = this.imp.getRoi();
/*  85 */     ImagePlus imp2 = this.imp;
/*  86 */     if ((imp2.getOverlay() != null) && (!imp2.getHideOverlay())) {
/*  87 */       imp2.killRoi();
/*  88 */       imp2 = imp2.flatten();
/*     */     }
/*  90 */     ImageProcessor ip = imp2.getProcessor();
/*  91 */     if ((printSelection) && (roi != null) && (roi.isArea()))
/*  92 */       ip.setRoi(roi);
/*  93 */     ip = ip.crop();
/*  94 */     if (rotate) {
/*  95 */       ip = ip.rotateLeft();
/*     */     }
/*  97 */     int width = ip.getWidth();
/*  98 */     int height = ip.getHeight();
/*  99 */     int margin = 0;
/* 100 */     if (drawBorder) margin = 1;
/* 101 */     double scale = scaling / 100.0D;
/* 102 */     int dstWidth = (int)(width * scale);
/* 103 */     int dstHeight = (int)(height * scale);
/* 104 */     int pageX = (int)pf.getImageableX();
/* 105 */     int pageY = (int)pf.getImageableY();
/* 106 */     int dstX = pageX + margin;
/* 107 */     int dstY = pageY + margin;
/* 108 */     Image img = ip.createImage();
/* 109 */     double pageWidth = pf.getImageableWidth() - 2 * margin;
/* 110 */     double pageHeight = pf.getImageableHeight() - 2 * margin;
/* 111 */     if ((label) && (pageWidth - dstWidth < fontSize + 5)) {
/* 112 */       dstY += fontSize + 5;
/* 113 */       pageHeight -= fontSize + 5;
/*     */     }
/* 115 */     if (actualSize) {
/* 116 */       Calibration cal = this.imp.getCalibration();
/* 117 */       int unitIndex = ImageProperties.getUnitIndex(cal.getUnit());
/* 118 */       if (unitIndex != 10) {
/* 119 */         double unitsPerCm = ImageProperties.getUnitsPerCm(unitIndex);
/* 120 */         double widthInCm = width * cal.pixelWidth / unitsPerCm;
/* 121 */         double heightInCm = height * cal.pixelHeight / unitsPerCm;
/* 122 */         dstWidth = (int)(widthInCm * 28.346399999999999D * scale);
/* 123 */         dstHeight = (int)(heightInCm * 28.346399999999999D * scale);
/*     */       }
/* 125 */       if ((center) && (dstWidth < pageWidth) && (dstHeight < pageHeight)) {
/* 126 */         dstX = (int)(dstX + (pageWidth - dstWidth) / 2.0D);
/* 127 */         dstY = (int)(dstY + (pageHeight - dstHeight) / 2.0D);
/*     */       }
/* 129 */     } else if ((dstWidth > pageWidth) || (dstHeight > pageHeight))
/*     */     {
/* 131 */       double hscale = pageWidth / dstWidth;
/* 132 */       double vscale = pageHeight / dstHeight;
/* 133 */       double scale2 = hscale <= vscale ? hscale : vscale;
/* 134 */       dstWidth = (int)(dstWidth * scale2);
/* 135 */       dstHeight = (int)(dstHeight * scale2);
/* 136 */     } else if (center) {
/* 137 */       dstX = (int)(dstX + (pageWidth - dstWidth) / 2.0D);
/* 138 */       dstY = (int)(dstY + (pageHeight - dstHeight) / 2.0D);
/*     */     }
/* 140 */     g.drawImage(img, dstX, dstY, dstX + dstWidth, dstY + dstHeight, 0, 0, width, height, null);
/*     */ 
/* 144 */     if (drawBorder)
/* 145 */       g.drawRect(dstX - 1, dstY - 1, dstWidth + 1, dstHeight + 1);
/* 146 */     if (label) {
/* 147 */       g.setFont(new Font("SanSerif", 0, fontSize));
/* 148 */       g.setColor(Color.black);
/* 149 */       g.drawString(this.imp.getTitle(), pageX + 5, pageY + fontSize);
/*     */     }
/* 151 */     return 0;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Printer
 * JD-Core Version:    0.6.2
 */