/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.OvalRoi;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SpecifyROI
/*     */   implements PlugIn, DialogListener
/*     */ {
/*     */   static double xRoi;
/*     */   static double yRoi;
/*     */   static double width;
/*     */   static double height;
/*     */   static boolean oval;
/*     */   static boolean centered;
/*     */   static boolean scaledUnits;
/*     */   static Rectangle prevRoi;
/*  40 */   static double prevPixelWidth = 1.0D;
/*     */   int iSlice;
/*     */   boolean bAbort;
/*     */   ImagePlus imp;
/*     */   Vector fields;
/*     */   Vector checkboxes;
/*     */   int stackSize;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  48 */     this.imp = IJ.getImage();
/*  49 */     this.stackSize = (this.imp != null ? this.imp.getStackSize() : 0);
/*  50 */     Roi roi = this.imp.getRoi();
/*  51 */     Calibration cal = this.imp.getCalibration();
/*  52 */     if ((roi != null) && (roi.getBounds().equals(prevRoi)) && (cal.pixelWidth == prevPixelWidth))
/*  53 */       roi = null;
/*  54 */     if (roi != null) {
/*  55 */       boolean rectOrOval = (roi != null) && ((roi.getType() == 0) || (roi.getType() == 1));
/*  56 */       oval = (rectOrOval) && (roi.getType() == 1);
/*  57 */       Rectangle r = roi.getBounds();
/*  58 */       width = r.width;
/*  59 */       height = r.height;
/*  60 */       xRoi = r.x;
/*  61 */       yRoi = r.y;
/*  62 */       if ((scaledUnits) && (cal.scaled())) {
/*  63 */         xRoi *= cal.pixelWidth;
/*  64 */         yRoi *= cal.pixelHeight;
/*  65 */         width *= cal.pixelWidth;
/*  66 */         height *= cal.pixelHeight;
/*     */       }
/*  68 */     } else if (!validDialogValues()) {
/*  69 */       width = this.imp.getWidth() / 2;
/*  70 */       height = this.imp.getHeight() / 2;
/*  71 */       xRoi = width / 2.0D;
/*  72 */       yRoi = height / 2.0D;
/*     */     }
/*  74 */     if (centered) {
/*  75 */       xRoi += width / 2.0D;
/*  76 */       yRoi += height / 2.0D;
/*     */     }
/*  78 */     this.iSlice = this.imp.getCurrentSlice();
/*  79 */     showDialog();
/*     */   }
/*     */ 
/*     */   boolean validDialogValues() {
/*  83 */     Calibration cal = this.imp.getCalibration();
/*  84 */     double pw = cal.pixelWidth; double ph = cal.pixelHeight;
/*  85 */     if ((width / pw < 1.5D) || (height / ph < 1.5D))
/*  86 */       return false;
/*  87 */     if ((xRoi / pw >= this.imp.getWidth()) || (yRoi / ph >= this.imp.getHeight()))
/*  88 */       return false;
/*  89 */     return true;
/*     */   }
/*     */ 
/*     */   void showDialog()
/*     */   {
/*  98 */     Calibration cal = this.imp.getCalibration();
/*  99 */     int digits = 0;
/* 100 */     if ((scaledUnits) && (cal.scaled()))
/* 101 */       digits = 2;
/* 102 */     Roi roi = this.imp.getRoi();
/* 103 */     if (roi == null)
/* 104 */       drawRoi();
/* 105 */     GenericDialog gd = new GenericDialog("Specify");
/* 106 */     gd.addNumericField("Width:", width, digits);
/* 107 */     gd.addNumericField("Height:", height, digits);
/* 108 */     gd.addNumericField("X Coordinate:", xRoi, digits);
/* 109 */     gd.addNumericField("Y Coordinate:", yRoi, digits);
/* 110 */     if (this.stackSize > 1)
/* 111 */       gd.addNumericField("Slice:", this.iSlice, 0);
/* 112 */     gd.addCheckbox("Oval", oval);
/* 113 */     gd.addCheckbox("Centered", centered);
/* 114 */     if (cal.scaled())
/* 115 */       gd.addCheckbox("Scaled Units (" + cal.getUnits() + ")", scaledUnits);
/* 116 */     this.fields = gd.getNumericFields();
/* 117 */     gd.addDialogListener(this);
/* 118 */     gd.showDialog();
/* 119 */     if (gd.wasCanceled())
/* 120 */       if (roi == null)
/* 121 */         this.imp.killRoi();
/*     */       else
/* 123 */         this.imp.setRoi(roi);
/*     */   }
/*     */ 
/*     */   void drawRoi()
/*     */   {
/* 128 */     int iX = (int)xRoi;
/* 129 */     int iY = (int)yRoi;
/* 130 */     if (centered) {
/* 131 */       iX = (int)(xRoi - width / 2.0D);
/* 132 */       iY = (int)(yRoi - height / 2.0D);
/*     */     }
/* 134 */     int iWidth = (int)width;
/* 135 */     int iHeight = (int)height;
/* 136 */     Calibration cal = this.imp.getCalibration();
/* 137 */     if ((scaledUnits) && (cal.scaled())) {
/* 138 */       iX = (int)Math.round(iX / cal.pixelWidth);
/* 139 */       iY = (int)Math.round(iY / cal.pixelHeight);
/* 140 */       iWidth = (int)Math.round(width / cal.pixelWidth);
/* 141 */       iHeight = (int)Math.round(height / cal.pixelHeight);
/* 142 */       prevPixelWidth = cal.pixelWidth;
/*     */     }
/*     */     Roi roi;
/*     */     Roi roi;
/* 145 */     if (oval)
/* 146 */       roi = new OvalRoi(iX, iY, iWidth, iHeight, this.imp);
/*     */     else
/* 148 */       roi = new Roi(iX, iY, iWidth, iHeight);
/* 149 */     this.imp.setRoi(roi);
/* 150 */     prevRoi = roi.getBounds();
/* 151 */     prevPixelWidth = cal.pixelWidth;
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 155 */     if (IJ.isMacOSX()) IJ.wait(100);
/* 156 */     width = gd.getNextNumber();
/* 157 */     height = gd.getNextNumber();
/* 158 */     xRoi = gd.getNextNumber();
/* 159 */     yRoi = gd.getNextNumber();
/* 160 */     if (this.stackSize > 1)
/* 161 */       this.iSlice = ((int)gd.getNextNumber());
/* 162 */     oval = gd.getNextBoolean();
/* 163 */     centered = gd.getNextBoolean();
/* 164 */     if (this.imp.getCalibration().scaled())
/* 165 */       scaledUnits = gd.getNextBoolean();
/* 166 */     if (gd.invalidNumber()) {
/* 167 */       return false;
/*     */     }
/* 169 */     if ((this.stackSize > 1) && (this.iSlice > 0) && (this.iSlice <= this.stackSize))
/* 170 */       this.imp.setSlice(this.iSlice);
/* 171 */     drawRoi();
/* 172 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.SpecifyROI
 * JD-Core Version:    0.6.2
 */