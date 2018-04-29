/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Undo;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.ShapeRoi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.StackProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.event.TextListener;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Resizer
/*     */   implements PlugIn, TextListener, ItemListener
/*     */ {
/*     */   public static final int IN_PLACE = 16;
/*     */   public static final int SCALE_T = 32;
/*     */   private static int newWidth;
/*     */   private static int newHeight;
/*  16 */   private static boolean constrain = true;
/*  17 */   private static boolean averageWhenDownsizing = true;
/*  18 */   private static int interpolationMethod = 1;
/*  19 */   private String[] methods = ImageProcessor.getInterpolationMethods();
/*     */   private Vector fields;
/*     */   private Vector checkboxes;
/*     */   private double origWidth;
/*     */   private double origHeight;
/*     */   private boolean sizeToHeight;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  25 */     boolean crop = arg.equals("crop");
/*  26 */     ImagePlus imp = IJ.getImage();
/*  27 */     ImageProcessor ip = imp.getProcessor();
/*  28 */     Roi roi = imp.getRoi();
/*  29 */     if ((roi == null) && (crop)) {
/*  30 */       IJ.error("Crop", "Area selection required");
/*  31 */       return;
/*     */     }
/*  33 */     if ((roi != null) && (roi.isLine())) {
/*  34 */       IJ.error("The Crop and Adjust>Size commands\ndo not work with line selections.");
/*  35 */       return;
/*     */     }
/*  37 */     Rectangle r = ip.getRoi();
/*  38 */     this.origWidth = r.width;
/*  39 */     this.origHeight = r.height;
/*  40 */     this.sizeToHeight = false;
/*  41 */     boolean restoreRoi = (crop) && (roi != null) && (roi.getType() != 0);
/*  42 */     if (roi != null) {
/*  43 */       Rectangle b = roi.getBounds();
/*  44 */       int w = ip.getWidth();
/*  45 */       int h = ip.getHeight();
/*  46 */       if ((b.x < 0) || (b.y < 0) || (b.x + b.width > w) || (b.y + b.height > h)) {
/*  47 */         ShapeRoi shape1 = new ShapeRoi(roi);
/*  48 */         ShapeRoi shape2 = new ShapeRoi(new Roi(0, 0, w, h));
/*  49 */         roi = shape2.and(shape1);
/*  50 */         if (restoreRoi) imp.setRoi(roi);
/*     */       }
/*     */     }
/*  53 */     int stackSize = imp.getStackSize();
/*  54 */     int z1 = imp.getStackSize();
/*  55 */     int t1 = 0;
/*  56 */     int z2 = 0; int t2 = 0;
/*  57 */     int saveMethod = interpolationMethod;
/*  58 */     if (crop) {
/*  59 */       Rectangle bounds = roi.getBounds();
/*  60 */       newWidth = bounds.width;
/*  61 */       newHeight = bounds.height;
/*  62 */       interpolationMethod = 0;
/*     */     } else {
/*  64 */       if ((newWidth == 0) || (newHeight == 0)) {
/*  65 */         newWidth = (int)this.origWidth / 2;
/*  66 */         newHeight = (int)this.origHeight / 2;
/*     */       }
/*  68 */       if (constrain) newHeight = (int)(newWidth * (this.origHeight / this.origWidth));
/*  69 */       if (stackSize > 1) {
/*  70 */         newWidth = (int)this.origWidth;
/*  71 */         newHeight = (int)this.origHeight;
/*     */       }
/*  73 */       GenericDialog gd = new GenericDialog("Resize", IJ.getInstance());
/*  74 */       gd.addNumericField("Width (pixels):", newWidth, 0);
/*  75 */       gd.addNumericField("Height (pixels):", newHeight, 0);
/*  76 */       if (imp.isHyperStack()) {
/*  77 */         z1 = imp.getNSlices();
/*  78 */         t1 = imp.getNFrames();
/*     */       }
/*  80 */       if ((z1 > 1) && (z1 == stackSize))
/*  81 */         gd.addNumericField("Depth (images):", z1, 0);
/*  82 */       else if ((z1 > 1) && (z1 < stackSize))
/*  83 */         gd.addNumericField("Depth (slices):", z1, 0);
/*  84 */       if (t1 > 1)
/*  85 */         gd.addNumericField("Time (frames):", t1, 0);
/*  86 */       gd.addCheckbox("Constrain aspect ratio", constrain);
/*  87 */       gd.addCheckbox("Average when downsizing", averageWhenDownsizing);
/*  88 */       gd.addChoice("Interpolation:", this.methods, this.methods[interpolationMethod]);
/*  89 */       this.fields = gd.getNumericFields();
/*  90 */       for (int i = 0; i < 2; i++)
/*  91 */         ((TextField)this.fields.elementAt(i)).addTextListener(this);
/*  92 */       this.checkboxes = gd.getCheckboxes();
/*  93 */       ((Checkbox)this.checkboxes.elementAt(0)).addItemListener(this);
/*  94 */       gd.showDialog();
/*  95 */       if (gd.wasCanceled())
/*  96 */         return;
/*  97 */       newWidth = (int)gd.getNextNumber();
/*  98 */       newHeight = (int)gd.getNextNumber();
/*  99 */       if (z1 > 1)
/* 100 */         z2 = (int)gd.getNextNumber();
/* 101 */       if (t1 > 1)
/* 102 */         t2 = (int)gd.getNextNumber();
/* 103 */       if (gd.invalidNumber()) {
/* 104 */         IJ.error("Width or height are invalid.");
/* 105 */         return;
/*     */       }
/* 107 */       constrain = gd.getNextBoolean();
/* 108 */       averageWhenDownsizing = gd.getNextBoolean();
/* 109 */       interpolationMethod = gd.getNextChoiceIndex();
/* 110 */       if ((constrain) && (newWidth == 0))
/* 111 */         this.sizeToHeight = true;
/* 112 */       if ((newWidth <= 0.0D) && (!constrain)) newWidth = 50;
/* 113 */       if (newHeight <= 0.0D) newHeight = 50;
/*     */     }
/*     */ 
/* 116 */     if ((!crop) && (constrain)) {
/* 117 */       if (this.sizeToHeight)
/* 118 */         newWidth = (int)Math.round(newHeight * (this.origWidth / this.origHeight));
/*     */       else
/* 120 */         newHeight = (int)Math.round(newWidth * (this.origHeight / this.origWidth));
/*     */     }
/* 122 */     if ((ip.getWidth() == 1) || (ip.getHeight() == 1))
/* 123 */       ip.setInterpolationMethod(0);
/*     */     else
/* 125 */       ip.setInterpolationMethod(interpolationMethod);
/* 126 */     if ((!crop) && (stackSize == 1)) {
/* 127 */       Undo.setup(2, imp);
/*     */     }
/* 129 */     if ((roi != null) || (newWidth != this.origWidth) || (newHeight != this.origHeight)) {
/*     */       try {
/* 131 */         StackProcessor sp = new StackProcessor(imp.getStack(), ip);
/* 132 */         ImageStack s2 = sp.resize(newWidth, newHeight, averageWhenDownsizing);
/* 133 */         int newSize = s2.getSize();
/* 134 */         if ((s2.getWidth() > 0) && (newSize > 0)) {
/* 135 */           if (restoreRoi)
/* 136 */             imp.killRoi();
/* 137 */           Calibration cal = imp.getCalibration();
/* 138 */           if (cal.scaled()) {
/* 139 */             cal.pixelWidth *= this.origWidth / newWidth;
/* 140 */             cal.pixelHeight *= this.origHeight / newHeight;
/*     */           }
/* 142 */           if ((crop) && (roi != null) && ((cal.xOrigin != 0.0D) || (cal.yOrigin != 0.0D))) {
/* 143 */             cal.xOrigin -= roi.getBounds().x;
/* 144 */             cal.yOrigin -= roi.getBounds().y;
/*     */           }
/* 146 */           imp.setStack(null, s2);
/* 147 */           if ((restoreRoi) && (roi != null)) {
/* 148 */             roi.setLocation(0, 0);
/* 149 */             imp.setRoi(roi);
/* 150 */             imp.draw();
/*     */           }
/*     */         }
/* 153 */         if ((stackSize > 1) && (newSize < stackSize))
/* 154 */           IJ.error("ImageJ ran out of memory causing \nthe last " + (stackSize - newSize) + " slices to be lost.");
/*     */       } catch (OutOfMemoryError o) {
/* 156 */         IJ.outOfMemory("Resize");
/*     */       }
/* 158 */       imp.changes = true;
/* 159 */       if (crop) {
/* 160 */         interpolationMethod = saveMethod;
/*     */       }
/*     */     }
/* 163 */     ImagePlus imp2 = null;
/* 164 */     if ((z2 > 0) && (z2 != z1))
/* 165 */       imp2 = zScale(imp, z2, interpolationMethod + 16);
/* 166 */     if ((t2 > 0) && (t2 != t1))
/* 167 */       imp2 = zScale(imp2 != null ? imp2 : imp, t2, interpolationMethod + 16 + 32);
/* 168 */     if ((imp2 != null) && (imp2 != imp)) {
/* 169 */       imp.changes = false;
/* 170 */       imp.close();
/* 171 */       imp2.show();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ImagePlus zScale(ImagePlus imp, int newDepth, int interpolationMethod) {
/* 176 */     ImagePlus imp2 = null;
/* 177 */     if (imp.isHyperStack()) {
/* 178 */       imp2 = zScaleHyperstack(imp, newDepth, interpolationMethod);
/*     */     } else {
/* 180 */       boolean inPlace = (interpolationMethod & 0x10) != 0;
/* 181 */       interpolationMethod &= 15;
/* 182 */       int stackSize = imp.getStackSize();
/* 183 */       int bitDepth = imp.getBitDepth();
/* 184 */       if ((newDepth <= stackSize / 2) && (interpolationMethod == 0))
/* 185 */         imp2 = shrinkZ(imp, newDepth, inPlace);
/*     */       else
/* 187 */         imp2 = resizeZ(imp, newDepth, interpolationMethod);
/* 188 */       if (imp2 == null) return null;
/* 189 */       ImageProcessor ip = imp.getProcessor();
/* 190 */       double min = ip.getMin();
/* 191 */       double max = ip.getMax();
/* 192 */       if ((bitDepth == 16) || (bitDepth == 32))
/* 193 */         imp2.getProcessor().setMinAndMax(min, max);
/*     */     }
/* 195 */     if (imp2 == null) return null;
/* 196 */     if ((imp2 != imp) && (imp.isComposite())) {
/* 197 */       imp2 = new CompositeImage(imp2, ((CompositeImage)imp).getMode());
/* 198 */       ((CompositeImage)imp2).copyLuts(imp);
/*     */     }
/* 200 */     imp2.setCalibration(imp.getCalibration());
/* 201 */     Calibration cal = imp2.getCalibration();
/* 202 */     if (cal.scaled()) cal.pixelDepth *= imp.getNSlices() / imp2.getNSlices();
/* 203 */     Object info = imp.getProperty("Info");
/* 204 */     if (info != null) imp2.setProperty("Info", info);
/* 205 */     if (imp.isHyperStack())
/* 206 */       imp2.setOpenAsHyperStack(imp.isHyperStack());
/* 207 */     return imp2;
/*     */   }
/*     */ 
/*     */   private ImagePlus zScaleHyperstack(ImagePlus imp, int depth2, int interpolationMethod) {
/* 211 */     boolean inPlace = (interpolationMethod & 0x10) != 0;
/* 212 */     boolean scaleT = (interpolationMethod & 0x20) != 0;
/* 213 */     interpolationMethod &= 15;
/* 214 */     int channels = imp.getNChannels();
/* 215 */     int slices = imp.getNSlices();
/* 216 */     int frames = imp.getNFrames();
/* 217 */     int slices2 = slices;
/* 218 */     int frames2 = frames;
/* 219 */     int bitDepth = imp.getBitDepth();
/* 220 */     if ((slices == 1) && (frames > 1))
/* 221 */       scaleT = true;
/* 222 */     if (scaleT)
/* 223 */       frames2 = depth2;
/*     */     else
/* 225 */       slices2 = depth2;
/* 226 */     double scale = (depth2 - 1) / slices;
/* 227 */     if (scaleT) scale = (depth2 - 1) / frames;
/* 228 */     if ((scale <= 0.5D) && (interpolationMethod == 0))
/* 229 */       return shrinkHyperstack(imp, depth2, inPlace, scaleT);
/* 230 */     ImageStack stack1 = imp.getStack();
/* 231 */     int width = stack1.getWidth();
/* 232 */     int height = stack1.getHeight();
/* 233 */     ImagePlus imp2 = IJ.createImage(imp.getTitle(), bitDepth + "-bit", width, height, channels * slices2 * frames2);
/* 234 */     if (imp2 == null) return null;
/* 235 */     imp2.setDimensions(channels, slices2, frames2);
/* 236 */     ImageStack stack2 = imp2.getStack();
/* 237 */     ImageProcessor ip = imp.getProcessor();
/* 238 */     int count = 0;
/* 239 */     if (scaleT) {
/* 240 */       IJ.showStatus("T Scaling...");
/* 241 */       ImageProcessor xtPlane1 = ip.createProcessor(width, frames);
/* 242 */       xtPlane1.setInterpolationMethod(interpolationMethod);
/*     */ 
/* 244 */       Object xtpixels1 = xtPlane1.getPixels();
/* 245 */       int last = slices * channels * height - 1;
/* 246 */       for (int z = 1; z <= slices; z++)
/* 247 */         for (int c = 1; c <= channels; c++)
/* 248 */           for (int y = 0; y < height; y++) {
/* 249 */             IJ.showProgress(count++, last);
/* 250 */             for (int t = 1; t <= frames; t++) {
/* 251 */               int index = imp.getStackIndex(c, z, t);
/*     */ 
/* 253 */               Object pixels1 = stack1.getPixels(index);
/* 254 */               System.arraycopy(pixels1, y * width, xtpixels1, (t - 1) * width, width);
/*     */             }
/* 256 */             ImageProcessor xtPlane2 = xtPlane1.resize(width, depth2, averageWhenDownsizing);
/* 257 */             Object xtpixels2 = xtPlane2.getPixels();
/* 258 */             for (int t = 1; t <= frames2; t++) {
/* 259 */               int index = imp2.getStackIndex(c, z, t);
/*     */ 
/* 261 */               Object pixels2 = stack2.getPixels(index);
/* 262 */               System.arraycopy(xtpixels2, (t - 1) * width, pixels2, y * width, width);
/*     */             }
/*     */           }
/*     */     }
/*     */     else
/*     */     {
/* 268 */       IJ.showStatus("Z Scaling...");
/* 269 */       ImageProcessor xzPlane1 = ip.createProcessor(width, slices);
/* 270 */       xzPlane1.setInterpolationMethod(interpolationMethod);
/*     */ 
/* 272 */       Object xypixels1 = xzPlane1.getPixels();
/* 273 */       int last = frames * channels * height - 1;
/* 274 */       for (int t = 1; t <= frames; t++) {
/* 275 */         for (int c = 1; c <= channels; c++) {
/* 276 */           for (int y = 0; y < height; y++) {
/* 277 */             IJ.showProgress(count++, last);
/* 278 */             for (int z = 1; z <= slices; z++) {
/* 279 */               int index = imp.getStackIndex(c, z, t);
/* 280 */               Object pixels1 = stack1.getPixels(index);
/* 281 */               System.arraycopy(pixels1, y * width, xypixels1, (z - 1) * width, width);
/*     */             }
/* 283 */             ImageProcessor xzPlane2 = xzPlane1.resize(width, depth2, averageWhenDownsizing);
/* 284 */             Object xypixels2 = xzPlane2.getPixels();
/* 285 */             for (int z = 1; z <= slices2; z++) {
/* 286 */               int index = imp2.getStackIndex(c, z, t);
/* 287 */               Object pixels2 = stack2.getPixels(index);
/* 288 */               System.arraycopy(xypixels2, (z - 1) * width, pixels2, y * width, width);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 294 */     imp2.setDimensions(channels, slices2, frames2);
/* 295 */     return imp2;
/*     */   }
/*     */ 
/*     */   private ImagePlus shrinkHyperstack(ImagePlus imp, int newDepth, boolean inPlace, boolean scaleT) {
/* 299 */     int channels = imp.getNChannels();
/* 300 */     int slices = imp.getNSlices();
/* 301 */     int frames = imp.getNFrames();
/* 302 */     int factor = (int)Math.round(slices / newDepth);
/* 303 */     if (scaleT) factor = frames / newDepth;
/* 304 */     int zfactor = scaleT ? 1 : factor;
/* 305 */     int tfactor = scaleT ? factor : 1;
/* 306 */     ImageStack stack = imp.getStack();
/* 307 */     ImageStack stack2 = new ImageStack(imp.getWidth(), imp.getHeight());
/* 308 */     boolean virtual = stack.isVirtual();
/* 309 */     int slices2 = slices / zfactor + (slices % zfactor != 0 ? 1 : 0);
/* 310 */     int frames2 = frames / tfactor + (frames % tfactor != 0 ? 1 : 0);
/* 311 */     int n = channels * slices2 * frames2;
/* 312 */     int count = 1;
/* 313 */     for (int t = 1; t <= frames; t += tfactor) {
/* 314 */       for (int z = 1; z <= slices; z += zfactor) {
/* 315 */         for (int c = 1; c <= channels; c++) {
/* 316 */           int i = imp.getStackIndex(c, z, t);
/* 317 */           IJ.showProgress(i, n);
/* 318 */           ImageProcessor ip = stack.getProcessor(imp.getStackIndex(c, z, t));
/* 319 */           if (!inPlace) ip = ip.duplicate();
/*     */ 
/* 321 */           stack2.addSlice(stack.getSliceLabel(i), ip);
/*     */         }
/*     */       }
/*     */     }
/* 325 */     ImagePlus imp2 = new ImagePlus(imp.getTitle(), stack2);
/* 326 */     imp2.setDimensions(channels, slices2, frames2);
/* 327 */     IJ.showProgress(1.0D);
/* 328 */     return imp2;
/*     */   }
/*     */ 
/*     */   private ImagePlus shrinkZ(ImagePlus imp, int newDepth, boolean inPlace) {
/* 332 */     ImageStack stack = imp.getStack();
/* 333 */     int factor = imp.getStackSize() / newDepth;
/* 334 */     boolean virtual = stack.isVirtual();
/* 335 */     int n = stack.getSize();
/* 336 */     ImageStack stack2 = new ImageStack(stack.getWidth(), stack.getHeight());
/* 337 */     for (int i = 1; i <= n; i += factor) {
/* 338 */       if (virtual) IJ.showProgress(i, n);
/* 339 */       ImageProcessor ip2 = stack.getProcessor(i);
/* 340 */       if (!inPlace) ip2 = ip2.duplicate();
/* 341 */       stack2.addSlice(stack.getSliceLabel(i), ip2);
/*     */     }
/* 343 */     return new ImagePlus(imp.getTitle(), stack2);
/*     */   }
/*     */ 
/*     */   private ImagePlus resizeZ(ImagePlus imp, int newDepth, int interpolationMethod) {
/* 347 */     ImageStack stack1 = imp.getStack();
/* 348 */     int width = stack1.getWidth();
/* 349 */     int height = stack1.getHeight();
/* 350 */     int depth = stack1.getSize();
/* 351 */     int bitDepth = imp.getBitDepth();
/* 352 */     ImagePlus imp2 = IJ.createImage(imp.getTitle(), bitDepth + "-bit", width, height, newDepth);
/* 353 */     if (imp2 == null) return null;
/* 354 */     ImageStack stack2 = imp2.getStack();
/* 355 */     ImageProcessor ip = imp.getProcessor();
/* 356 */     ImageProcessor xzPlane1 = ip.createProcessor(width, depth);
/* 357 */     xzPlane1.setInterpolationMethod(interpolationMethod);
/*     */ 
/* 359 */     Object xypixels1 = xzPlane1.getPixels();
/* 360 */     IJ.showStatus("Z Scaling...");
/* 361 */     for (int y = 0; y < height; y++) {
/* 362 */       IJ.showProgress(y, height - 1);
/* 363 */       for (int z = 0; z < depth; z++) {
/* 364 */         Object pixels1 = stack1.getPixels(z + 1);
/* 365 */         System.arraycopy(pixels1, y * width, xypixels1, z * width, width);
/*     */       }
/* 367 */       ImageProcessor xzPlane2 = xzPlane1.resize(width, newDepth, averageWhenDownsizing);
/* 368 */       Object xypixels2 = xzPlane2.getPixels();
/* 369 */       for (int z = 0; z < newDepth; z++) {
/* 370 */         Object pixels2 = stack2.getPixels(z + 1);
/* 371 */         System.arraycopy(xypixels2, z * width, pixels2, y * width, width);
/*     */       }
/*     */     }
/* 374 */     return imp2;
/*     */   }
/*     */ 
/*     */   public void textValueChanged(TextEvent e) {
/* 378 */     TextField widthField = (TextField)this.fields.elementAt(0);
/* 379 */     TextField heightField = (TextField)this.fields.elementAt(1);
/* 380 */     int width = (int)Tools.parseDouble(widthField.getText(), -99.0D);
/* 381 */     int height = (int)Tools.parseDouble(heightField.getText(), -99.0D);
/* 382 */     if ((width == -99) || (height == -99))
/* 383 */       return;
/* 384 */     if (constrain)
/* 385 */       if (width != newWidth) {
/* 386 */         this.sizeToHeight = false;
/* 387 */         newWidth = width;
/* 388 */         updateFields();
/* 389 */       } else if (height != newHeight) {
/* 390 */         this.sizeToHeight = true;
/* 391 */         newHeight = height;
/* 392 */         updateFields();
/*     */       }
/*     */   }
/*     */ 
/*     */   void updateFields()
/*     */   {
/* 398 */     if (this.sizeToHeight) {
/* 399 */       newWidth = (int)(newHeight * (this.origWidth / this.origHeight));
/* 400 */       TextField widthField = (TextField)this.fields.elementAt(0);
/* 401 */       widthField.setText("" + newWidth);
/*     */     } else {
/* 403 */       newHeight = (int)(newWidth * (this.origHeight / this.origWidth));
/* 404 */       TextField heightField = (TextField)this.fields.elementAt(1);
/* 405 */       heightField.setText("" + newHeight);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/* 410 */     Checkbox cb = (Checkbox)this.checkboxes.elementAt(0);
/* 411 */     boolean newConstrain = cb.getState();
/* 412 */     if ((newConstrain) && (newConstrain != constrain))
/* 413 */       updateFields();
/* 414 */     constrain = newConstrain;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Resizer
 * JD-Core Version:    0.6.2
 */