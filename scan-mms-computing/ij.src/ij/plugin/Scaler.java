/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.StackProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Color;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.event.TextListener;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Scaler
/*     */   implements PlugIn, TextListener, FocusListener
/*     */ {
/*     */   private ImagePlus imp;
/*  14 */   private static String xstr = "0.5";
/*  15 */   private static String ystr = "0.5";
/*  16 */   private String zstr = "1.0";
/*     */   private static int newWidth;
/*     */   private static int newHeight;
/*     */   private int newDepth;
/*  19 */   private static boolean averageWhenDownsizing = true;
/*  20 */   private static boolean newWindow = true;
/*  21 */   private static int interpolationMethod = 1;
/*  22 */   private String[] methods = ImageProcessor.getInterpolationMethods();
/*     */   private static boolean fillWithBackground;
/*  24 */   private static boolean processStack = true;
/*     */   private double xscale;
/*     */   private double yscale;
/*     */   private double zscale;
/*  26 */   private String title = "Untitled";
/*     */   private Vector fields;
/*     */   private double bgValue;
/*  29 */   private boolean constainAspectRatio = true;
/*     */   private TextField xField;
/*     */   private TextField yField;
/*     */   private TextField zField;
/*     */   private TextField widthField;
/*     */   private TextField heightField;
/*     */   private TextField depthField;
/*     */   private Rectangle r;
/*     */   private Object fieldWithFocus;
/*     */   private int oldDepth;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  36 */     this.imp = IJ.getImage();
/*  37 */     Roi roi = this.imp.getRoi();
/*  38 */     if ((roi != null) && (!roi.isArea()))
/*  39 */       this.imp.killRoi();
/*  40 */     ImageProcessor ip = this.imp.getProcessor();
/*  41 */     if (!showDialog(ip))
/*  42 */       return;
/*  43 */     if ((this.newDepth > 0) && (this.newDepth != this.imp.getStackSize())) {
/*  44 */       newWindow = true;
/*  45 */       processStack = true;
/*     */     }
/*  47 */     if ((ip.getWidth() > 1) && (ip.getHeight() > 1))
/*  48 */       ip.setInterpolationMethod(interpolationMethod);
/*     */     else
/*  50 */       ip.setInterpolationMethod(0);
/*  51 */     ip.setBackgroundValue(this.bgValue);
/*  52 */     this.imp.startTiming();
/*     */     try {
/*  54 */       if ((newWindow) && (this.imp.getStackSize() > 1) && (processStack))
/*  55 */         createNewStack(this.imp, ip);
/*     */       else
/*  57 */         scale(ip);
/*     */     }
/*     */     catch (OutOfMemoryError o) {
/*  60 */       IJ.outOfMemory("Scale");
/*     */     }
/*  62 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   void createNewStack(ImagePlus imp, ImageProcessor ip) {
/*  66 */     int nSlices = imp.getStackSize();
/*  67 */     int w = imp.getWidth(); int h = imp.getHeight();
/*  68 */     ImagePlus imp2 = imp.createImagePlus();
/*  69 */     if ((newWidth != w) || (newHeight != h)) {
/*  70 */       Rectangle r = ip.getRoi();
/*  71 */       boolean crop = (r.width != imp.getWidth()) || (r.height != imp.getHeight());
/*  72 */       ImageStack stack1 = imp.getStack();
/*  73 */       ImageStack stack2 = new ImageStack(newWidth, newHeight);
/*     */ 
/*  75 */       int method = interpolationMethod;
/*  76 */       if ((w == 1) || (h == 1))
/*  77 */         method = 0;
/*  78 */       for (int i = 1; i <= nSlices; i++) {
/*  79 */         IJ.showStatus("Scale: " + i + "/" + nSlices);
/*  80 */         ImageProcessor ip1 = stack1.getProcessor(i);
/*  81 */         String label = stack1.getSliceLabel(i);
/*  82 */         if (crop) {
/*  83 */           ip1.setRoi(r);
/*  84 */           ip1 = ip1.crop();
/*     */         }
/*  86 */         ip1.setInterpolationMethod(method);
/*  87 */         ImageProcessor ip2 = ip1.resize(newWidth, newHeight, averageWhenDownsizing);
/*  88 */         if (ip2 != null)
/*  89 */           stack2.addSlice(label, ip2);
/*  90 */         IJ.showProgress(i, nSlices);
/*     */       }
/*  92 */       imp2.setStack(this.title, stack2);
/*  93 */       Calibration cal = imp2.getCalibration();
/*  94 */       if (cal.scaled()) {
/*  95 */         cal.pixelWidth *= 1.0D / this.xscale;
/*  96 */         cal.pixelHeight *= 1.0D / this.yscale;
/*     */       }
/*  98 */       IJ.showProgress(1.0D);
/*     */     } else {
/* 100 */       imp2.setStack(this.title, imp.getStack());
/* 101 */     }int[] dim = imp.getDimensions();
/* 102 */     imp2.setDimensions(dim[2], dim[3], dim[4]);
/* 103 */     if (imp.isComposite()) {
/* 104 */       imp2 = new CompositeImage(imp2, ((CompositeImage)imp).getMode());
/* 105 */       ((CompositeImage)imp2).copyLuts(imp);
/*     */     }
/* 107 */     if (imp.isHyperStack())
/* 108 */       imp2.setOpenAsHyperStack(true);
/* 109 */     if ((this.newDepth > 0) && (this.newDepth != this.oldDepth))
/* 110 */       imp2 = new Resizer().zScale(imp2, this.newDepth, interpolationMethod);
/* 111 */     if (imp2 != null) {
/* 112 */       imp2.show();
/* 113 */       imp2.changes = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   void scale(ImageProcessor ip) {
/* 118 */     if (newWindow) {
/* 119 */       Rectangle r = ip.getRoi();
/* 120 */       ImagePlus imp2 = this.imp.createImagePlus();
/* 121 */       imp2.setProcessor(this.title, ip.resize(newWidth, newHeight, averageWhenDownsizing));
/* 122 */       Calibration cal = imp2.getCalibration();
/* 123 */       if (cal.scaled()) {
/* 124 */         cal.pixelWidth *= 1.0D / this.xscale;
/* 125 */         cal.pixelHeight *= 1.0D / this.yscale;
/*     */       }
/* 127 */       imp2.show();
/* 128 */       this.imp.trimProcessor();
/* 129 */       imp2.trimProcessor();
/* 130 */       imp2.changes = true;
/*     */     } else {
/* 132 */       if ((processStack) && (this.imp.getStackSize() > 1)) {
/* 133 */         Undo.reset();
/* 134 */         StackProcessor sp = new StackProcessor(this.imp.getStack(), ip);
/* 135 */         sp.scale(this.xscale, this.yscale, this.bgValue);
/*     */       } else {
/* 137 */         ip.snapshot();
/* 138 */         Undo.setup(1, this.imp);
/* 139 */         ip.setSnapshotCopyMode(true);
/* 140 */         ip.scale(this.xscale, this.yscale);
/* 141 */         ip.setSnapshotCopyMode(false);
/*     */       }
/* 143 */       this.imp.killRoi();
/* 144 */       this.imp.updateAndDraw();
/* 145 */       this.imp.changes = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean showDialog(ImageProcessor ip) {
/* 150 */     String macroOptions = Macro.getOptions();
/* 151 */     if (macroOptions != null) {
/* 152 */       if (macroOptions.indexOf(" interpolate") != -1)
/* 153 */         macroOptions.replaceAll(" interpolate", " interpolation=Bilinear");
/* 154 */       else if (macroOptions.indexOf(" interpolation=") == -1)
/* 155 */         macroOptions = macroOptions + " interpolation=None";
/* 156 */       Macro.setOptions(macroOptions);
/*     */     }
/* 158 */     int bitDepth = this.imp.getBitDepth();
/* 159 */     int stackSize = this.imp.getStackSize();
/* 160 */     boolean isStack = stackSize > 1;
/* 161 */     this.oldDepth = stackSize;
/* 162 */     if (isStack) {
/* 163 */       xstr = "1.0";
/* 164 */       ystr = "1.0";
/* 165 */       this.zstr = "1.0";
/*     */     }
/* 167 */     this.r = ip.getRoi();
/* 168 */     int width = newWidth;
/* 169 */     if (width == 0) width = this.r.width;
/* 170 */     int height = (int)(width * this.r.height / this.r.width);
/* 171 */     this.xscale = Tools.parseDouble(xstr, 0.0D);
/* 172 */     this.yscale = Tools.parseDouble(ystr, 0.0D);
/* 173 */     this.zscale = 1.0D;
/* 174 */     if ((this.xscale != 0.0D) && (this.yscale != 0.0D)) {
/* 175 */       width = (int)(this.r.width * this.xscale);
/* 176 */       height = (int)(this.r.height * this.yscale);
/*     */     } else {
/* 178 */       xstr = "-";
/* 179 */       ystr = "-";
/*     */     }
/* 181 */     GenericDialog gd = new GenericDialog("Scale");
/* 182 */     gd.addStringField("X Scale:", xstr);
/* 183 */     gd.addStringField("Y Scale:", ystr);
/* 184 */     if (isStack)
/* 185 */       gd.addStringField("Z Scale:", this.zstr);
/* 186 */     gd.setInsets(5, 0, 5);
/* 187 */     gd.addStringField("Width (pixels):", "" + width);
/* 188 */     gd.addStringField("Height (pixels):", "" + height);
/* 189 */     if (isStack) {
/* 190 */       String label = "Depth (images):";
/* 191 */       if (this.imp.isHyperStack()) {
/* 192 */         int slices = this.imp.getNSlices();
/* 193 */         int frames = this.imp.getNFrames();
/* 194 */         if ((slices == 1) && (frames > 1)) {
/* 195 */           label = "Depth (frames):";
/* 196 */           this.oldDepth = frames;
/*     */         } else {
/* 198 */           label = "Depth (slices):";
/* 199 */           this.oldDepth = slices;
/*     */         }
/*     */       }
/* 202 */       gd.addStringField(label, "" + this.oldDepth);
/*     */     }
/* 204 */     this.fields = gd.getStringFields();
/* 205 */     for (int i = 0; i < this.fields.size(); i++) {
/* 206 */       ((TextField)this.fields.elementAt(i)).addTextListener(this);
/* 207 */       ((TextField)this.fields.elementAt(i)).addFocusListener(this);
/*     */     }
/* 209 */     this.xField = ((TextField)this.fields.elementAt(0));
/* 210 */     this.yField = ((TextField)this.fields.elementAt(1));
/* 211 */     if (isStack) {
/* 212 */       this.zField = ((TextField)this.fields.elementAt(2));
/* 213 */       this.widthField = ((TextField)this.fields.elementAt(3));
/* 214 */       this.heightField = ((TextField)this.fields.elementAt(4));
/* 215 */       this.depthField = ((TextField)this.fields.elementAt(5));
/*     */     } else {
/* 217 */       this.widthField = ((TextField)this.fields.elementAt(2));
/* 218 */       this.heightField = ((TextField)this.fields.elementAt(3));
/*     */     }
/* 220 */     this.fieldWithFocus = this.xField;
/* 221 */     gd.addChoice("Interpolation:", this.methods, this.methods[interpolationMethod]);
/* 222 */     if ((bitDepth == 8) || (bitDepth == 24))
/* 223 */       gd.addCheckbox("Fill with background color", fillWithBackground);
/* 224 */     gd.addCheckbox("Average when downsizing", averageWhenDownsizing);
/* 225 */     if (isStack)
/* 226 */       gd.addCheckbox("Process entire stack", processStack);
/* 227 */     gd.addCheckbox("Create new window", newWindow);
/* 228 */     this.title = WindowManager.getUniqueName(this.imp.getTitle());
/* 229 */     gd.setInsets(10, 0, 0);
/* 230 */     gd.addStringField("Title:", this.title, 12);
/* 231 */     gd.showDialog();
/* 232 */     if (gd.wasCanceled())
/* 233 */       return false;
/* 234 */     xstr = gd.getNextString();
/* 235 */     ystr = gd.getNextString();
/* 236 */     this.xscale = Tools.parseDouble(xstr, 0.0D);
/* 237 */     this.yscale = Tools.parseDouble(ystr, 0.0D);
/* 238 */     if (isStack) {
/* 239 */       this.zstr = gd.getNextString();
/* 240 */       this.zscale = Tools.parseDouble(ystr, 0.0D);
/*     */     }
/* 242 */     String wstr = gd.getNextString();
/* 243 */     newWidth = (int)Tools.parseDouble(wstr, 0.0D);
/* 244 */     newHeight = (int)Tools.parseDouble(gd.getNextString(), 0.0D);
/* 245 */     if ((newHeight != 0) && ((wstr.equals("-")) || (wstr.equals("0"))))
/* 246 */       newWidth = (int)(newHeight * this.r.width / this.r.height);
/* 247 */     if ((newWidth == 0) || (newHeight == 0)) {
/* 248 */       IJ.error("Scaler", "Width or height is 0");
/* 249 */       return false;
/*     */     }
/* 251 */     if ((this.xscale > 0.0D) && (this.yscale > 0.0D)) {
/* 252 */       newWidth = (int)(this.r.width * this.xscale);
/* 253 */       newHeight = (int)(this.r.height * this.yscale);
/*     */     }
/* 255 */     if (isStack)
/* 256 */       this.newDepth = ((int)Tools.parseDouble(gd.getNextString(), 0.0D));
/* 257 */     interpolationMethod = gd.getNextChoiceIndex();
/* 258 */     if ((bitDepth == 8) || (bitDepth == 24))
/* 259 */       fillWithBackground = gd.getNextBoolean();
/* 260 */     averageWhenDownsizing = gd.getNextBoolean();
/* 261 */     if (isStack)
/* 262 */       processStack = gd.getNextBoolean();
/* 263 */     newWindow = gd.getNextBoolean();
/* 264 */     if (this.xscale == 0.0D) {
/* 265 */       this.xscale = (newWidth / this.r.width);
/* 266 */       this.yscale = (newHeight / this.r.height);
/*     */     }
/* 268 */     this.title = gd.getNextString();
/*     */ 
/* 270 */     if (fillWithBackground) {
/* 271 */       Color bgc = Toolbar.getBackgroundColor();
/* 272 */       if (bitDepth == 8)
/* 273 */         this.bgValue = ip.getBestIndex(bgc);
/* 274 */       else if (bitDepth == 24)
/* 275 */         this.bgValue = bgc.getRGB();
/*     */     } else {
/* 277 */       this.bgValue = 0.0D;
/* 278 */     }return true;
/*     */   }
/*     */ 
/*     */   public void textValueChanged(TextEvent e) {
/* 282 */     Object source = e.getSource();
/* 283 */     double newXScale = this.xscale;
/* 284 */     double newYScale = this.yscale;
/* 285 */     double newZScale = this.zscale;
/* 286 */     if ((source == this.xField) && (this.fieldWithFocus == this.xField)) {
/* 287 */       String newXText = this.xField.getText();
/* 288 */       newXScale = Tools.parseDouble(newXText, 0.0D);
/* 289 */       if (newXScale == 0.0D) return;
/* 290 */       if (newXScale != this.xscale) {
/* 291 */         int newWidth = (int)(newXScale * this.r.width);
/* 292 */         this.widthField.setText("" + newWidth);
/* 293 */         if (this.constainAspectRatio) {
/* 294 */           this.yField.setText(newXText);
/* 295 */           int newHeight = (int)(newXScale * this.r.height);
/* 296 */           this.heightField.setText("" + newHeight);
/*     */         }
/*     */       }
/* 299 */     } else if ((source == this.yField) && (this.fieldWithFocus == this.yField)) {
/* 300 */       String newYText = this.yField.getText();
/* 301 */       newYScale = Tools.parseDouble(newYText, 0.0D);
/* 302 */       if (newYScale == 0.0D) return;
/* 303 */       if (newYScale != this.yscale) {
/* 304 */         int newHeight = (int)(newYScale * this.r.height);
/* 305 */         this.heightField.setText("" + newHeight);
/*     */       }
/* 307 */     } else if ((source == this.zField) && (this.fieldWithFocus == this.zField)) {
/* 308 */       String newZText = this.zField.getText();
/* 309 */       newZScale = Tools.parseDouble(newZText, 0.0D);
/* 310 */       if (newZScale == 0.0D) return;
/* 311 */       if (newZScale != this.zscale) {
/* 312 */         int newDepth = (int)(newZScale * this.imp.getStackSize());
/* 313 */         this.depthField.setText("" + newDepth);
/*     */       }
/* 315 */     } else if ((source == this.widthField) && (this.fieldWithFocus == this.widthField)) {
/* 316 */       int newWidth = (int)Tools.parseDouble(this.widthField.getText(), 0.0D);
/* 317 */       if (newWidth != 0) {
/* 318 */         int newHeight = (int)(newWidth * this.r.height / this.r.width);
/* 319 */         this.heightField.setText("" + newHeight);
/* 320 */         this.xField.setText("-");
/* 321 */         this.yField.setText("-");
/* 322 */         newXScale = 0.0D;
/* 323 */         newYScale = 0.0D;
/*     */       }
/* 325 */     } else if ((source == this.depthField) && (this.fieldWithFocus == this.depthField)) {
/* 326 */       int newDepth = (int)Tools.parseDouble(this.depthField.getText(), 0.0D);
/* 327 */       if (newDepth != 0) {
/* 328 */         this.zField.setText("-");
/* 329 */         newZScale = 0.0D;
/*     */       }
/*     */     }
/* 332 */     this.xscale = newXScale;
/* 333 */     this.yscale = newYScale;
/* 334 */     this.zscale = newZScale;
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e) {
/* 338 */     this.fieldWithFocus = e.getSource();
/* 339 */     if (this.fieldWithFocus == this.widthField)
/* 340 */       this.constainAspectRatio = true;
/* 341 */     else if (this.fieldWithFocus == this.yField)
/* 342 */       this.constainAspectRatio = false;
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Scaler
 * JD-Core Version:    0.6.2
 */