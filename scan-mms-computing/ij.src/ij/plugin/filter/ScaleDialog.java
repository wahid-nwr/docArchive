/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.Roi;
/*     */ import ij.io.FileOpener;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Button;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Panel;
/*     */ 
/*     */ public class ScaleDialog
/*     */   implements PlugInFilter
/*     */ {
/*     */   private ImagePlus imp;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  17 */     this.imp = imp;
/*  18 */     IJ.register(ScaleDialog.class);
/*  19 */     return 159;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  23 */     double measured = 0.0D;
/*  24 */     double known = 0.0D;
/*  25 */     double aspectRatio = 1.0D;
/*  26 */     String unit = "pixel";
/*  27 */     boolean global1 = this.imp.getGlobalCalibration() != null;
/*     */ 
/*  29 */     Calibration cal = this.imp.getCalibration();
/*  30 */     Calibration calOrig = cal.copy();
/*  31 */     boolean isCalibrated = cal.scaled();
/*  32 */     String length = "0.00";
/*     */ 
/*  34 */     String scale = "<no scale>";
/*  35 */     int digits = 2;
/*     */ 
/*  37 */     Roi roi = this.imp.getRoi();
/*  38 */     if ((roi != null) && ((roi instanceof Line))) {
/*  39 */       measured = ((Line)roi).getRawLength();
/*  40 */       length = IJ.d2s(measured, 2);
/*     */     }
/*  42 */     if (isCalibrated) {
/*  43 */       if (measured != 0.0D) {
/*  44 */         known = measured * cal.pixelWidth;
/*     */       } else {
/*  46 */         measured = 1.0D / cal.pixelWidth;
/*  47 */         known = 1.0D;
/*     */       }
/*  49 */       double dscale = measured / known;
/*  50 */       digits = Tools.getDecimalPlaces(dscale, dscale);
/*  51 */       unit = cal.getUnit();
/*  52 */       scale = IJ.d2s(dscale, digits) + " pixels/" + unit;
/*  53 */       aspectRatio = cal.pixelHeight / cal.pixelWidth;
/*     */     }
/*     */ 
/*  56 */     digits = Tools.getDecimalPlaces(measured, measured);
/*  57 */     int asDigits = aspectRatio == 1.0D ? 1 : 3;
/*  58 */     SetScaleDialog gd = new SetScaleDialog("Set Scale", scale, length);
/*  59 */     gd.addNumericField("Distance in pixels:", measured, digits, 8, null);
/*  60 */     gd.addNumericField("Known distance:", known, 2, 8, null);
/*  61 */     gd.addNumericField("Pixel aspect ratio:", aspectRatio, asDigits, 8, null);
/*  62 */     gd.addStringField("Unit of length:", unit);
/*  63 */     gd.addPanel(makeButtonPanel(gd), 13, new Insets(5, 0, 0, 0));
/*  64 */     gd.setInsets(0, 30, 0);
/*  65 */     gd.addCheckbox("Global", global1);
/*  66 */     gd.setInsets(10, 0, 0);
/*  67 */     gd.addMessage("Scale: 12345.789 pixels per centimeter");
/*  68 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/analyze.html#scale");
/*  69 */     gd.showDialog();
/*  70 */     if (gd.wasCanceled())
/*  71 */       return;
/*  72 */     measured = gd.getNextNumber();
/*  73 */     known = gd.getNextNumber();
/*  74 */     aspectRatio = gd.getNextNumber();
/*  75 */     unit = gd.getNextString();
/*  76 */     if (unit.equals("A"))
/*  77 */       unit = "Å";
/*  78 */     boolean global2 = gd.getNextBoolean();
/*     */ 
/*  83 */     if ((measured == known) && (unit.equals("unit")))
/*  84 */       unit = "pixel";
/*  85 */     if ((measured <= 0.0D) || (known <= 0.0D) || (unit.startsWith("pixel")) || (unit.startsWith("Pixel")) || (unit.equals(""))) {
/*  86 */       cal.pixelWidth = 1.0D;
/*  87 */       cal.pixelHeight = 1.0D;
/*  88 */       cal.pixelDepth = 1.0D;
/*  89 */       cal.setUnit("pixel");
/*     */     } else {
/*  91 */       if ((gd.scaleChanged) || (IJ.macroRunning())) {
/*  92 */         cal.pixelWidth = (known / measured);
/*  93 */         cal.pixelDepth = cal.pixelWidth;
/*     */       }
/*  95 */       if (aspectRatio != 0.0D)
/*  96 */         cal.pixelHeight = (cal.pixelWidth * aspectRatio);
/*     */       else
/*  98 */         cal.pixelHeight = cal.pixelWidth;
/*  99 */       cal.setUnit(unit);
/*     */     }
/* 101 */     if (!cal.equals(calOrig))
/* 102 */       this.imp.setCalibration(cal);
/* 103 */     this.imp.setGlobalCalibration(global2 ? cal : null);
/* 104 */     if ((global2) || (global2 != global1))
/* 105 */       WindowManager.repaintImageWindows();
/*     */     else
/* 107 */       this.imp.repaintWindow();
/* 108 */     if ((global2) && (global2 != global1))
/* 109 */       FileOpener.setShowConflictMessage(true);
/*     */   }
/*     */ 
/*     */   Panel makeButtonPanel(SetScaleDialog gd)
/*     */   {
/* 114 */     Panel panel = new Panel();
/* 115 */     panel.setLayout(new FlowLayout(1, 0, 0));
/* 116 */     gd.unscaleButton = new Button("Click to Remove Scale");
/* 117 */     gd.unscaleButton.addActionListener(gd);
/* 118 */     panel.add(gd.unscaleButton);
/* 119 */     return panel;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.ScaleDialog
 * JD-Core Version:    0.6.2
 */