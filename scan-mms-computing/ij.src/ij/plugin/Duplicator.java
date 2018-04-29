/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.LUT;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.event.TextListener;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Duplicator
/*     */   implements PlugIn, TextListener
/*     */ {
/*     */   private static boolean duplicateStack;
/*     */   private boolean duplicateSubstack;
/*     */   private int first;
/*     */   private int last;
/*     */   private Checkbox checkbox;
/*     */   private TextField rangeField;
/*     */   private TextField[] rangeFields;
/*     */   private int firstC;
/*     */   private int lastC;
/*     */   private int firstZ;
/*     */   private int lastZ;
/*     */   private int firstT;
/*     */   private int lastT;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  31 */     ImagePlus imp = IJ.getImage();
/*  32 */     int stackSize = imp.getStackSize();
/*  33 */     String title = imp.getTitle();
/*  34 */     String newTitle = WindowManager.getUniqueName(title);
/*  35 */     if ((!IJ.altKeyDown()) || (stackSize > 1)) {
/*  36 */       if ((imp.isHyperStack()) || (imp.isComposite())) {
/*  37 */         duplicateHyperstack(imp, newTitle);
/*  38 */         return;
/*     */       }
/*  40 */       newTitle = showDialog(imp, "Duplicate...", "Title: ", newTitle);
/*     */     }
/*  42 */     if (newTitle == null) {
/*  43 */       return;
/*     */     }
/*  45 */     Roi roi = imp.getRoi();
/*     */     ImagePlus imp2;
/*     */     ImagePlus imp2;
/*  46 */     if ((this.duplicateSubstack) && ((this.first > 1) || (this.last < stackSize))) {
/*  47 */       imp2 = run(imp, this.first, this.last);
/*     */     }
/*     */     else
/*     */     {
/*     */       ImagePlus imp2;
/*  48 */       if ((duplicateStack) || (imp.getStackSize() == 1))
/*  49 */         imp2 = run(imp);
/*     */       else
/*  51 */         imp2 = duplicateImage(imp); 
/*     */     }
/*  52 */     Calibration cal = imp2.getCalibration();
/*  53 */     if ((roi != null) && ((cal.xOrigin != 0.0D) || (cal.yOrigin != 0.0D))) {
/*  54 */       cal.xOrigin -= roi.getBounds().x;
/*  55 */       cal.yOrigin -= roi.getBounds().y;
/*     */     }
/*  57 */     imp2.setTitle(newTitle);
/*  58 */     imp2.show();
/*  59 */     if ((roi != null) && (roi.isArea()) && (roi.getType() != 0) && (roi.getBounds().width == imp2.getWidth()))
/*  60 */       imp2.restoreRoi();
/*     */   }
/*     */ 
/*     */   public ImagePlus run(ImagePlus imp)
/*     */   {
/*  65 */     if (Recorder.record) Recorder.recordCall("imp = new Duplicator().run(imp);");
/*  66 */     if (imp.getStackSize() == 1)
/*  67 */       return duplicateImage(imp);
/*  68 */     Rectangle rect = null;
/*  69 */     Roi roi = imp.getRoi();
/*  70 */     if ((roi != null) && (roi.isArea()))
/*  71 */       rect = roi.getBounds();
/*  72 */     ImageStack stack = imp.getStack();
/*  73 */     ImageStack stack2 = null;
/*  74 */     for (int i = 1; i <= stack.getSize(); i++) {
/*  75 */       ImageProcessor ip2 = stack.getProcessor(i);
/*  76 */       ip2.setRoi(rect);
/*  77 */       ip2 = ip2.crop();
/*  78 */       if (stack2 == null)
/*  79 */         stack2 = new ImageStack(ip2.getWidth(), ip2.getHeight(), imp.getProcessor().getColorModel());
/*  80 */       stack2.addSlice(stack.getSliceLabel(i), ip2);
/*     */     }
/*  82 */     ImagePlus imp2 = imp.createImagePlus();
/*  83 */     imp2.setStack("DUP_" + imp.getTitle(), stack2);
/*  84 */     int[] dim = imp.getDimensions();
/*  85 */     imp2.setDimensions(dim[2], dim[3], dim[4]);
/*  86 */     if (imp.isComposite()) {
/*  87 */       imp2 = new CompositeImage(imp2, 0);
/*  88 */       ((CompositeImage)imp2).copyLuts(imp);
/*     */     }
/*  90 */     if (imp.isHyperStack())
/*  91 */       imp2.setOpenAsHyperStack(true);
/*  92 */     Overlay overlay = imp.getOverlay();
/*  93 */     if ((overlay != null) && (!imp.getHideOverlay())) {
/*  94 */       overlay = overlay.duplicate();
/*  95 */       if (rect != null)
/*  96 */         overlay.translate(-rect.x, -rect.y);
/*  97 */       imp2.setOverlay(overlay);
/*     */     }
/*  99 */     return imp2;
/*     */   }
/*     */ 
/*     */   ImagePlus duplicateImage(ImagePlus imp) {
/* 103 */     ImageProcessor ip = imp.getProcessor();
/* 104 */     ImageProcessor ip2 = ip.crop();
/* 105 */     ImagePlus imp2 = imp.createImagePlus();
/* 106 */     imp2.setProcessor("DUP_" + imp.getTitle(), ip2);
/* 107 */     String info = (String)imp.getProperty("Info");
/* 108 */     if (info != null)
/* 109 */       imp2.setProperty("Info", info);
/* 110 */     if (imp.getStackSize() > 1) {
/* 111 */       ImageStack stack = imp.getStack();
/* 112 */       String label = stack.getSliceLabel(imp.getCurrentSlice());
/* 113 */       if ((label != null) && (label.indexOf('\n') > 0))
/* 114 */         imp2.setProperty("Info", label);
/* 115 */       if (imp.isComposite()) {
/* 116 */         LUT lut = ((CompositeImage)imp).getChannelLut();
/* 117 */         imp2.getProcessor().setColorModel(lut);
/*     */       }
/*     */     }
/* 120 */     Overlay overlay = imp.getOverlay();
/* 121 */     if ((overlay != null) && (!imp.getHideOverlay())) {
/* 122 */       overlay = overlay.duplicate();
/* 123 */       Rectangle r = ip.getRoi();
/* 124 */       if ((r.x > 0) || (r.y > 0))
/* 125 */         overlay.translate(-r.x, -r.y);
/* 126 */       imp2.setOverlay(overlay);
/*     */     }
/* 128 */     return imp2;
/*     */   }
/*     */ 
/*     */   public ImagePlus run(ImagePlus imp, int firstSlice, int lastSlice)
/*     */   {
/* 133 */     Rectangle rect = null;
/* 134 */     Roi roi = imp.getRoi();
/* 135 */     if ((roi != null) && (roi.isArea()))
/* 136 */       rect = roi.getBounds();
/* 137 */     ImageStack stack = imp.getStack();
/* 138 */     ImageStack stack2 = null;
/* 139 */     for (int i = firstSlice; i <= lastSlice; i++) {
/* 140 */       ImageProcessor ip2 = stack.getProcessor(i);
/* 141 */       ip2.setRoi(rect);
/* 142 */       ip2 = ip2.crop();
/* 143 */       if (stack2 == null)
/* 144 */         stack2 = new ImageStack(ip2.getWidth(), ip2.getHeight(), imp.getProcessor().getColorModel());
/* 145 */       stack2.addSlice(stack.getSliceLabel(i), ip2);
/*     */     }
/* 147 */     ImagePlus imp2 = imp.createImagePlus();
/* 148 */     imp2.setStack("DUP_" + imp.getTitle(), stack2);
/* 149 */     int size = stack2.getSize();
/* 150 */     boolean tseries = imp.getNFrames() == imp.getStackSize();
/* 151 */     if (tseries)
/* 152 */       imp2.setDimensions(1, 1, size);
/*     */     else
/* 154 */       imp2.setDimensions(1, size, 1);
/* 155 */     if (Recorder.record) Recorder.recordCall("imp = new Duplicator().run(imp, " + firstSlice + ", " + lastSlice + ");");
/* 156 */     return imp2;
/*     */   }
/*     */ 
/*     */   public ImagePlus run(ImagePlus imp, int firstC, int lastC, int firstZ, int lastZ, int firstT, int lastT)
/*     */   {
/* 161 */     Rectangle rect = null;
/* 162 */     Roi roi = imp.getRoi();
/* 163 */     if ((roi != null) && (roi.isArea()))
/* 164 */       rect = roi.getBounds();
/* 165 */     ImageStack stack = imp.getStack();
/* 166 */     ImageStack stack2 = null;
/* 167 */     for (int t = firstT; t <= lastT; t++) {
/* 168 */       for (int z = firstZ; z <= lastZ; z++) {
/* 169 */         for (int c = firstC; c <= lastC; c++) {
/* 170 */           int n1 = imp.getStackIndex(c, z, t);
/* 171 */           ImageProcessor ip = stack.getProcessor(n1);
/* 172 */           String label = stack.getSliceLabel(n1);
/* 173 */           ip.setRoi(rect);
/* 174 */           ip = ip.crop();
/* 175 */           if (stack2 == null)
/* 176 */             stack2 = new ImageStack(ip.getWidth(), ip.getHeight(), null);
/* 177 */           stack2.addSlice(label, ip);
/*     */         }
/*     */       }
/*     */     }
/* 181 */     ImagePlus imp2 = imp.createImagePlus();
/* 182 */     imp2.setStack("DUP_" + imp.getTitle(), stack2);
/* 183 */     imp2.setDimensions(lastC - firstC + 1, lastZ - firstZ + 1, lastT - firstT + 1);
/* 184 */     if (imp.isComposite()) {
/* 185 */       int mode = ((CompositeImage)imp).getMode();
/* 186 */       if (lastC > firstC) {
/* 187 */         imp2 = new CompositeImage(imp2, mode);
/* 188 */         int i2 = 1;
/* 189 */         for (int i = firstC; i <= lastC; i++) {
/* 190 */           LUT lut = ((CompositeImage)imp).getChannelLut(i);
/* 191 */           ((CompositeImage)imp2).setChannelLut(lut, i2++);
/*     */         }
/* 193 */       } else if (firstC == lastC) {
/* 194 */         LUT lut = ((CompositeImage)imp).getChannelLut(firstC);
/* 195 */         imp2.getProcessor().setColorModel(lut);
/* 196 */         imp2.setDisplayRange(lut.min, lut.max);
/*     */       }
/*     */     }
/* 199 */     imp2.setOpenAsHyperStack(true);
/* 200 */     if (Recorder.record)
/* 201 */       Recorder.recordCall("imp = new Duplicator().run(imp, " + firstC + ", " + lastC + ", " + firstZ + ", " + lastZ + ", " + firstT + ", " + lastT + ");");
/* 202 */     return imp2;
/*     */   }
/*     */ 
/*     */   String showDialog(ImagePlus imp, String title, String prompt, String defaultString) {
/* 206 */     int stackSize = imp.getStackSize();
/* 207 */     this.duplicateSubstack = ((stackSize > 1) && ((stackSize == imp.getNSlices()) || (stackSize == imp.getNFrames())));
/* 208 */     GenericDialog gd = new GenericDialog(title);
/* 209 */     gd.addStringField(prompt, defaultString, this.duplicateSubstack ? 15 : 20);
/* 210 */     if (stackSize > 1) {
/* 211 */       String msg = this.duplicateSubstack ? "Duplicate stack" : "Duplicate entire stack";
/* 212 */       gd.addCheckbox(msg, (duplicateStack) || (imp.isComposite()));
/* 213 */       if (this.duplicateSubstack) {
/* 214 */         gd.setInsets(2, 30, 3);
/* 215 */         gd.addStringField("Range:", "1-" + stackSize);
/* 216 */         Vector v = gd.getStringFields();
/* 217 */         this.rangeField = ((TextField)v.elementAt(1));
/* 218 */         this.rangeField.addTextListener(this);
/* 219 */         this.checkbox = ((Checkbox)gd.getCheckboxes().elementAt(0));
/*     */       }
/*     */     } else {
/* 222 */       duplicateStack = false;
/* 223 */     }gd.showDialog();
/* 224 */     if (gd.wasCanceled())
/* 225 */       return null;
/* 226 */     title = gd.getNextString();
/* 227 */     if (stackSize > 1) {
/* 228 */       duplicateStack = gd.getNextBoolean();
/* 229 */       if ((duplicateStack) && (this.duplicateSubstack)) {
/* 230 */         String[] range = Tools.split(gd.getNextString(), " -");
/* 231 */         double d1 = gd.parseDouble(range[0]);
/* 232 */         double d2 = range.length == 2 ? gd.parseDouble(range[1]) : (0.0D / 0.0D);
/* 233 */         this.first = (Double.isNaN(d1) ? 1 : (int)d1);
/* 234 */         this.last = (Double.isNaN(d2) ? stackSize : (int)d2);
/* 235 */         if (this.first < 1) this.first = 1;
/* 236 */         if (this.last > stackSize) this.last = stackSize;
/* 237 */         if (this.first > this.last) { this.first = 1; this.last = stackSize; }
/*     */       } else {
/* 239 */         this.first = 1;
/* 240 */         this.last = stackSize;
/*     */       }
/*     */     }
/* 243 */     return title;
/*     */   }
/*     */ 
/*     */   void duplicateHyperstack(ImagePlus imp, String newTitle) {
/* 247 */     newTitle = showHSDialog(imp, newTitle);
/* 248 */     if (newTitle == null)
/* 249 */       return;
/* 250 */     ImagePlus imp2 = null;
/* 251 */     Roi roi = imp.getRoi();
/* 252 */     if (!duplicateStack) {
/* 253 */       int nChannels = imp.getNChannels();
/* 254 */       boolean singleComposite = (imp.isComposite()) && (nChannels == imp.getStackSize());
/* 255 */       if ((!singleComposite) && (nChannels > 1) && (imp.isComposite()) && (((CompositeImage)imp).getMode() == 1)) {
/* 256 */         this.firstC = 1;
/* 257 */         this.lastC = nChannels;
/*     */       } else {
/* 259 */         this.firstC = (this.lastC = imp.getChannel());
/* 260 */       }this.firstZ = (this.lastZ = imp.getSlice());
/* 261 */       this.firstT = (this.lastT = imp.getFrame());
/*     */     }
/* 263 */     imp2 = run(imp, this.firstC, this.lastC, this.firstZ, this.lastZ, this.firstT, this.lastT);
/* 264 */     if (imp2 == null) return;
/* 265 */     Calibration cal = imp2.getCalibration();
/* 266 */     if ((roi != null) && ((cal.xOrigin != 0.0D) || (cal.yOrigin != 0.0D))) {
/* 267 */       cal.xOrigin -= roi.getBounds().x;
/* 268 */       cal.yOrigin -= roi.getBounds().y;
/*     */     }
/* 270 */     imp2.setTitle(newTitle);
/* 271 */     imp2.show();
/* 272 */     if ((roi != null) && (roi.isArea()) && (roi.getType() != 0) && (roi.getBounds().width == imp2.getWidth()))
/* 273 */       imp2.restoreRoi();
/* 274 */     if ((IJ.isMacro()) && (imp2.getWindow() != null))
/* 275 */       IJ.wait(50);
/*     */   }
/*     */ 
/*     */   String showHSDialog(ImagePlus imp, String newTitle) {
/* 279 */     int nChannels = imp.getNChannels();
/* 280 */     int nSlices = imp.getNSlices();
/* 281 */     int nFrames = imp.getNFrames();
/* 282 */     boolean composite = (imp.isComposite()) && (nChannels == imp.getStackSize());
/* 283 */     GenericDialog gd = new GenericDialog("Duplicate");
/* 284 */     gd.addStringField("Title:", newTitle, 15);
/* 285 */     gd.setInsets(12, 20, 8);
/* 286 */     gd.addCheckbox("Duplicate hyperstack", (duplicateStack) || (composite));
/* 287 */     int nRangeFields = 0;
/* 288 */     if (nChannels > 1) {
/* 289 */       gd.setInsets(2, 30, 3);
/* 290 */       gd.addStringField("Channels (c):", "1-" + nChannels);
/* 291 */       nRangeFields++;
/*     */     }
/* 293 */     if (nSlices > 1) {
/* 294 */       gd.setInsets(2, 30, 3);
/* 295 */       gd.addStringField("Slices (z):", "1-" + nSlices);
/* 296 */       nRangeFields++;
/*     */     }
/* 298 */     if (nFrames > 1) {
/* 299 */       gd.setInsets(2, 30, 3);
/* 300 */       gd.addStringField("Frames (t):", "1-" + nFrames);
/* 301 */       nRangeFields++;
/*     */     }
/* 303 */     Vector v = gd.getStringFields();
/* 304 */     this.rangeFields = new TextField[3];
/* 305 */     for (int i = 0; i < nRangeFields; i++) {
/* 306 */       this.rangeFields[i] = ((TextField)v.elementAt(i + 1));
/* 307 */       this.rangeFields[i].addTextListener(this);
/*     */     }
/* 309 */     this.checkbox = ((Checkbox)gd.getCheckboxes().elementAt(0));
/* 310 */     gd.showDialog();
/* 311 */     if (gd.wasCanceled())
/* 312 */       return null;
/* 313 */     newTitle = gd.getNextString();
/* 314 */     duplicateStack = gd.getNextBoolean();
/* 315 */     if (nChannels > 1) {
/* 316 */       String[] range = Tools.split(gd.getNextString(), " -");
/* 317 */       double c1 = gd.parseDouble(range[0]);
/* 318 */       double c2 = range.length == 2 ? gd.parseDouble(range[1]) : (0.0D / 0.0D);
/* 319 */       this.firstC = (Double.isNaN(c1) ? 1 : (int)c1);
/* 320 */       this.lastC = (Double.isNaN(c2) ? this.firstC : (int)c2);
/* 321 */       if (this.firstC < 1) this.firstC = 1;
/* 322 */       if (this.lastC > nChannels) this.lastC = nChannels;
/* 323 */       if (this.firstC > this.lastC) { this.firstC = 1; this.lastC = nChannels; }
/*     */     } else {
/* 325 */       this.firstC = (this.lastC = 1);
/* 326 */     }if (nSlices > 1) {
/* 327 */       String[] range = Tools.split(gd.getNextString(), " -");
/* 328 */       double z1 = gd.parseDouble(range[0]);
/* 329 */       double z2 = range.length == 2 ? gd.parseDouble(range[1]) : (0.0D / 0.0D);
/* 330 */       this.firstZ = (Double.isNaN(z1) ? 1 : (int)z1);
/* 331 */       this.lastZ = (Double.isNaN(z2) ? this.firstZ : (int)z2);
/* 332 */       if (this.firstZ < 1) this.firstZ = 1;
/* 333 */       if (this.lastZ > nSlices) this.lastZ = nSlices;
/* 334 */       if (this.firstZ > this.lastZ) { this.firstZ = 1; this.lastZ = nSlices; }
/*     */     } else {
/* 336 */       this.firstZ = (this.lastZ = 1);
/* 337 */     }if (nFrames > 1) {
/* 338 */       String[] range = Tools.split(gd.getNextString(), " -");
/* 339 */       double t1 = gd.parseDouble(range[0]);
/* 340 */       double t2 = range.length == 2 ? gd.parseDouble(range[1]) : (0.0D / 0.0D);
/* 341 */       this.firstT = (Double.isNaN(t1) ? 1 : (int)t1);
/* 342 */       this.lastT = (Double.isNaN(t2) ? this.firstT : (int)t2);
/* 343 */       if (this.firstT < 1) this.firstT = 1;
/* 344 */       if (this.lastT > nFrames) this.lastT = nFrames;
/* 345 */       if (this.firstT > this.lastT) { this.firstT = 1; this.lastT = nFrames; }
/*     */     } else {
/* 347 */       this.firstT = (this.lastT = 1);
/* 348 */     }return newTitle;
/*     */   }
/*     */ 
/*     */   public void textValueChanged(TextEvent e) {
/* 352 */     this.checkbox.setState(true);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Duplicator
 * JD-Core Version:    0.6.2
 */