/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.plugin.Colors;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ 
/*     */ public class RoiProperties
/*     */ {
/*     */   private Roi roi;
/*     */   private String title;
/*  13 */   private boolean showName = true;
/*     */   private boolean addToOverlay;
/*     */   private boolean overlayOptions;
/*     */   private boolean existingOverlay;
/*     */   private boolean setPositions;
/*  18 */   private static final String[] justNames = { "Left", "Center", "Right" };
/*     */ 
/*     */   public RoiProperties(String title, Roi roi)
/*     */   {
/*  22 */     if (roi == null)
/*  23 */       throw new IllegalArgumentException("ROI is null");
/*  24 */     this.title = title;
/*  25 */     this.showName = title.startsWith("Prop");
/*  26 */     this.addToOverlay = title.equals("Add to Overlay");
/*  27 */     this.overlayOptions = title.equals("Overlay Options");
/*  28 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  29 */     if (this.overlayOptions) {
/*  30 */       Overlay overlay = imp != null ? imp.getOverlay() : null;
/*  31 */       this.setPositions = (roi.getPosition() != 0);
/*  32 */       if (overlay != null)
/*  33 */         this.existingOverlay = true;
/*     */     }
/*  35 */     this.roi = roi;
/*     */   }
/*     */ 
/*     */   private String decodeColor(Color color, Color defaultColor) {
/*  39 */     if (color == null)
/*  40 */       color = defaultColor;
/*  41 */     String str = "#" + Integer.toHexString(color.getRGB());
/*  42 */     if ((str.length() == 9) && (str.startsWith("#ff")))
/*  43 */       str = "#" + str.substring(3);
/*  44 */     String lc = Colors.hexToColor(str);
/*  45 */     if (lc != null) str = lc;
/*  46 */     return str;
/*     */   }
/*     */ 
/*     */   public boolean showDialog()
/*     */   {
/*  51 */     Color strokeColor = null;
/*  52 */     Color fillColor = null;
/*  53 */     double strokeWidth = 1.0D;
/*  54 */     String name = this.roi.getName();
/*  55 */     boolean isRange = (name != null) && (name.startsWith("range: "));
/*  56 */     String nameLabel = isRange ? "Range:" : "Name:";
/*  57 */     if (isRange) name = name.substring(7);
/*  58 */     if (name == null) name = "";
/*  59 */     if ((!isRange) && ((this.roi instanceof ImageRoi)))
/*  60 */       return showImageDialog(name);
/*  61 */     if (this.roi.getStrokeColor() != null) strokeColor = this.roi.getStrokeColor();
/*  62 */     if (strokeColor == null) strokeColor = Roi.getColor();
/*  63 */     if (this.roi.getFillColor() != null) fillColor = this.roi.getFillColor();
/*  64 */     double width = this.roi.getStrokeWidth();
/*  65 */     if (width > 1.0D) strokeWidth = width;
/*  66 */     boolean isText = this.roi instanceof TextRoi;
/*  67 */     boolean isLine = this.roi.isLine();
/*  68 */     int justification = 0;
/*  69 */     if (isText) {
/*  70 */       TextRoi troi = (TextRoi)this.roi;
/*  71 */       Font font = troi.getCurrentFont();
/*  72 */       strokeWidth = font.getSize();
/*  73 */       justification = troi.getJustification();
/*     */     }
/*  75 */     String linec = strokeColor != null ? "#" + Integer.toHexString(strokeColor.getRGB()) : "none";
/*  76 */     if ((linec.length() == 9) && (linec.startsWith("#ff")))
/*  77 */       linec = "#" + linec.substring(3);
/*  78 */     String lc = Colors.hexToColor(linec);
/*  79 */     if (lc != null) linec = lc;
/*  80 */     String fillc = fillColor != null ? "#" + Integer.toHexString(fillColor.getRGB()) : "none";
/*  81 */     if (IJ.isMacro()) fillc = "none";
/*  82 */     int digits = (int)strokeWidth == strokeWidth ? 0 : 1;
/*  83 */     GenericDialog gd = new GenericDialog(this.title);
/*  84 */     if (this.showName)
/*  85 */       gd.addStringField(nameLabel, name, 15);
/*  86 */     gd.addStringField("Stroke color: ", linec);
/*  87 */     if (isText) {
/*  88 */       gd.addNumericField("Font size:", strokeWidth, digits);
/*  89 */       gd.addChoice("Justification:", justNames, justNames[justification]);
/*     */     } else {
/*  91 */       gd.addNumericField("Width:", strokeWidth, digits);
/*  92 */     }if (!isLine) {
/*  93 */       gd.addMessage("");
/*  94 */       gd.addStringField("Fill color: ", fillc);
/*     */     }
/*  96 */     if (this.addToOverlay)
/*  97 */       gd.addCheckbox("New overlay", false);
/*  98 */     if (this.overlayOptions) {
/*  99 */       if (this.existingOverlay) {
/* 100 */         gd.addCheckbox("Apply to current overlay", false);
/*     */       }
/* 102 */       gd.addCheckbox("Set stack positions", this.setPositions);
/*     */     }
/* 104 */     gd.showDialog();
/* 105 */     if (gd.wasCanceled()) return false;
/* 106 */     if (this.showName) {
/* 107 */       name = gd.getNextString();
/* 108 */       if (!isRange) this.roi.setName(name.length() > 0 ? name : null);
/*     */     }
/* 110 */     linec = gd.getNextString();
/* 111 */     strokeWidth = gd.getNextNumber();
/* 112 */     if (isText)
/* 113 */       justification = gd.getNextChoiceIndex();
/* 114 */     if (!isLine)
/* 115 */       fillc = gd.getNextString();
/* 116 */     boolean applyToOverlay = false;
/* 117 */     boolean newOverlay = this.addToOverlay ? gd.getNextBoolean() : false;
/* 118 */     if (this.overlayOptions) {
/* 119 */       if (this.existingOverlay)
/* 120 */         applyToOverlay = gd.getNextBoolean();
/* 121 */       this.setPositions = gd.getNextBoolean();
/* 122 */       this.roi.setPosition(this.setPositions ? 1 : 0);
/*     */     }
/* 124 */     strokeColor = Colors.decode(linec, Roi.getColor());
/* 125 */     fillColor = Colors.decode(fillc, null);
/* 126 */     if (isText) {
/* 127 */       TextRoi troi = (TextRoi)this.roi;
/* 128 */       Font font = troi.getCurrentFont();
/* 129 */       if ((int)strokeWidth != font.getSize()) {
/* 130 */         font = new Font(font.getName(), font.getStyle(), (int)strokeWidth);
/* 131 */         troi.setCurrentFont(font);
/*     */       }
/* 133 */       if (justification != troi.getJustification())
/* 134 */         troi.setJustification(justification);
/* 135 */     } else if ((strokeWidth != 1.0D) || (this.roi.getStroke() != null)) {
/* 136 */       this.roi.setStrokeWidth((float)strokeWidth);
/* 137 */     }this.roi.setStrokeColor(strokeColor);
/* 138 */     this.roi.setFillColor(fillColor);
/* 139 */     if (newOverlay) this.roi.setName("new-overlay");
/* 140 */     if (applyToOverlay) {
/* 141 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 142 */       if (imp == null)
/* 143 */         return true;
/* 144 */       Overlay overlay = imp.getOverlay();
/* 145 */       if (overlay == null)
/* 146 */         return true;
/* 147 */       Roi[] rois = overlay.toArray();
/* 148 */       for (int i = 0; i < rois.length; i++) {
/* 149 */         rois[i].setStrokeColor(strokeColor);
/* 150 */         rois[i].setStrokeWidth((float)strokeWidth);
/* 151 */         rois[i].setFillColor(fillColor);
/*     */       }
/* 153 */       imp.draw();
/*     */     }
/*     */ 
/* 157 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean showImageDialog(String name) {
/* 161 */     GenericDialog gd = new GenericDialog(this.title);
/* 162 */     gd.addStringField("Name:", name, 15);
/* 163 */     gd.addNumericField("Opacity (0-100%):", ((ImageRoi)this.roi).getOpacity() * 100.0D, 0);
/* 164 */     if (this.addToOverlay)
/* 165 */       gd.addCheckbox("New Overlay", false);
/* 166 */     gd.showDialog();
/* 167 */     if (gd.wasCanceled()) return false;
/* 168 */     name = gd.getNextString();
/* 169 */     this.roi.setName(name.length() > 0 ? name : null);
/* 170 */     double opacity = gd.getNextNumber() / 100.0D;
/* 171 */     ((ImageRoi)this.roi).setOpacity(opacity);
/* 172 */     boolean newOverlay = this.addToOverlay ? gd.getNextBoolean() : false;
/* 173 */     if (newOverlay) this.roi.setName("new-overlay");
/* 174 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.RoiProperties
 * JD-Core Version:    0.6.2
 */