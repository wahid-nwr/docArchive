/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.LUT;
/*     */ import java.awt.Color;
/*     */ import java.awt.image.IndexColorModel;
/*     */ 
/*     */ public class RGBStackMerge
/*     */   implements PlugIn
/*     */ {
/*  10 */   private static boolean staticCreateComposite = true;
/*     */   private static boolean staticKeep;
/*     */   private static boolean staticIgnoreLuts;
/*     */   private ImagePlus imp;
/*     */   private byte[] blank;
/*     */   private boolean ignoreLuts;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  18 */     this.imp = WindowManager.getCurrentImage();
/*  19 */     mergeStacks();
/*     */   }
/*     */ 
/*     */   public static ImagePlus mergeChannels(ImagePlus[] images, boolean keepSourceImages) {
/*  23 */     RGBStackMerge rgbsm = new RGBStackMerge();
/*  24 */     return rgbsm.mergeHyperstacks(images, keepSourceImages);
/*     */   }
/*     */ 
/*     */   public void mergeStacks()
/*     */   {
/*  29 */     int[] wList = WindowManager.getIDList();
/*  30 */     if (wList == null) {
/*  31 */       error("No images are open.");
/*  32 */       return;
/*     */     }
/*     */ 
/*  35 */     String[] titles = new String[wList.length + 1];
/*  36 */     for (int i = 0; i < wList.length; i++) {
/*  37 */       ImagePlus imp = WindowManager.getImage(wList[i]);
/*  38 */       titles[i] = (imp != null ? imp.getTitle() : "");
/*     */     }
/*  40 */     String none = "*None*";
/*  41 */     titles[wList.length] = none;
/*  42 */     boolean createComposite = staticCreateComposite;
/*  43 */     boolean keep = staticKeep;
/*  44 */     this.ignoreLuts = staticIgnoreLuts;
/*  45 */     if (IJ.isMacro()) {
/*  46 */       createComposite = keep = this.ignoreLuts = 0;
/*     */     }
/*  48 */     GenericDialog gd = new GenericDialog("Color Merge");
/*  49 */     gd.addChoice("Red:", titles, titles[0]);
/*  50 */     gd.addChoice("Green:", titles, titles[1]);
/*  51 */     String title3 = (titles.length > 2) && (!IJ.macroRunning()) ? titles[2] : none;
/*  52 */     gd.addChoice("Blue:", titles, title3);
/*  53 */     String title4 = (titles.length > 3) && (!IJ.macroRunning()) ? titles[3] : none;
/*  54 */     gd.addChoice("Gray:", titles, title4);
/*  55 */     gd.addCheckbox("Create composite", createComposite);
/*  56 */     gd.addCheckbox("Keep source images", keep);
/*  57 */     gd.addCheckbox("Ignore source LUTs", this.ignoreLuts);
/*  58 */     gd.showDialog();
/*  59 */     if (gd.wasCanceled())
/*  60 */       return;
/*  61 */     int[] index = new int[4];
/*  62 */     index[0] = gd.getNextChoiceIndex();
/*  63 */     index[1] = gd.getNextChoiceIndex();
/*  64 */     index[2] = gd.getNextChoiceIndex();
/*  65 */     index[3] = gd.getNextChoiceIndex();
/*  66 */     createComposite = gd.getNextBoolean();
/*  67 */     keep = gd.getNextBoolean();
/*  68 */     this.ignoreLuts = gd.getNextBoolean();
/*  69 */     if (!IJ.isMacro()) {
/*  70 */       staticCreateComposite = createComposite;
/*  71 */       staticKeep = keep;
/*  72 */       staticIgnoreLuts = this.ignoreLuts;
/*     */     }
/*     */ 
/*  75 */     ImagePlus[] images = new ImagePlus[4];
/*  76 */     int stackSize = 0;
/*  77 */     int width = 0;
/*  78 */     int height = 0;
/*  79 */     int bitDepth = 0;
/*  80 */     int slices = 0;
/*  81 */     int frames = 0;
/*  82 */     for (int i = 0; i < 4; i++)
/*     */     {
/*  84 */       if (index[i] < wList.length) {
/*  85 */         images[i] = WindowManager.getImage(wList[index[i]]);
/*  86 */         if (width == 0) {
/*  87 */           width = images[i].getWidth();
/*  88 */           height = images[i].getHeight();
/*  89 */           stackSize = images[i].getStackSize();
/*  90 */           bitDepth = images[i].getBitDepth();
/*  91 */           slices = images[i].getNSlices();
/*  92 */           frames = images[i].getNFrames();
/*     */         }
/*     */       }
/*     */     }
/*  96 */     if (width == 0) {
/*  97 */       error("There must be at least one source image or stack.");
/*  98 */       return;
/*     */     }
/*     */ 
/* 101 */     boolean mergeHyperstacks = false;
/* 102 */     for (int i = 0; i < 4; i++) {
/* 103 */       ImagePlus img = images[i];
/* 104 */       if (img != null) {
/* 105 */         if (img.getStackSize() != stackSize) {
/* 106 */           error("The source stacks must have the same number of images.");
/* 107 */           return;
/*     */         }
/* 109 */         if (img.isHyperStack()) {
/* 110 */           if (img.isComposite()) {
/* 111 */             CompositeImage ci = (CompositeImage)img;
/* 112 */             if (ci.getMode() != 1) {
/* 113 */               ci.setMode(1);
/* 114 */               img.updateAndDraw();
/* 115 */               if (!IJ.isMacro()) IJ.run("Channels Tool...");
/* 116 */               return;
/*     */             }
/*     */           }
/* 119 */           if (bitDepth == 24) {
/* 120 */             error("Source hyperstacks cannot be RGB.");
/* 121 */             return;
/*     */           }
/* 123 */           if (img.getNChannels() > 1) {
/* 124 */             error("Source hyperstacks cannot have more than 1 channel.");
/* 125 */             return;
/*     */           }
/* 127 */           if ((img.getNSlices() != slices) || (img.getNFrames() != frames)) {
/* 128 */             error("Source hyperstacks must have the same dimensions.");
/* 129 */             return;
/*     */           }
/* 131 */           mergeHyperstacks = true;
/*     */         }
/* 133 */         if ((img.getWidth() != width) || (images[i].getHeight() != height)) {
/* 134 */           error("The source images or stacks must have the same width and height.");
/* 135 */           return;
/*     */         }
/*     */ 
/* 143 */         if ((createComposite) && (img.getBitDepth() != bitDepth)) {
/* 144 */           error("The source images must have the same bit depth.");
/* 145 */           return;
/*     */         }
/*     */       }
/*     */     }
/* 149 */     ImageStack[] stacks = new ImageStack[4];
/* 150 */     stacks[0] = (images[0] != null ? images[0].getStack() : null);
/* 151 */     stacks[1] = (images[1] != null ? images[1].getStack() : null);
/* 152 */     stacks[2] = (images[2] != null ? images[2].getStack() : null);
/* 153 */     stacks[3] = (images[3] != null ? images[3].getStack() : null);
/* 154 */     String macroOptions = Macro.getOptions();
/* 155 */     if ((macroOptions != null) && (macroOptions.indexOf("gray=") == -1)) {
/* 156 */       stacks[3] = null;
/*     */     }
/* 158 */     boolean fourChannelRGB = (!createComposite) && (stacks[3] != null);
/* 159 */     if (fourChannelRGB) {
/* 160 */       createComposite = true;
/*     */     }
/* 162 */     if (stacks[3] != null)
/* 163 */       createComposite = true;
/* 164 */     for (int i = 0; i < 4; i++)
/* 165 */       if ((images[i] != null) && (images[i].getBitDepth() == 24))
/* 166 */         createComposite = false;
/* 169 */     ImagePlus imp2;
/* 168 */     if ((createComposite) || (mergeHyperstacks)) { ImagePlus imp2 = mergeHyperstacks(images, keep);
/* 170 */       if (imp2 != null);
/*     */     } else {
/* 172 */       ImageStack rgb = mergeStacks(width, height, stackSize, stacks[0], stacks[1], stacks[2], keep);
/* 173 */       imp2 = new ImagePlus("RGB", rgb);
/*     */     }
/* 175 */     if (images[0] != null)
/* 176 */       imp2.setCalibration(images[0].getCalibration());
/* 177 */     if (!keep) {
/* 178 */       for (int i = 0; i < 4; i++) {
/* 179 */         if ((images[i] != null) && (images[i].getWindow() != null)) {
/* 180 */           images[i].changes = false;
/* 181 */           images[i].close();
/*     */         }
/*     */       }
/*     */     }
/* 185 */     if (fourChannelRGB) {
/* 186 */       if (imp2.getStackSize() == 1) {
/* 187 */         imp2 = imp2.flatten();
/* 188 */         imp2.setTitle("RGB");
/*     */       } else {
/* 190 */         imp2.setTitle("RGB");
/* 191 */         IJ.run(imp2, "RGB Color", "slices");
/*     */       }
/*     */     }
/* 194 */     imp2.show();
/*     */   }
/*     */ 
/*     */   public ImagePlus mergeHyperstacks(ImagePlus[] images, boolean keep)
/*     */   {
/* 199 */     int n = images.length;
/* 200 */     int channels = 0;
/* 201 */     for (int i = 0; i < n; i++) {
/* 202 */       if (images[i] != null) channels++;
/*     */     }
/* 204 */     if (channels < 2) return null;
/* 205 */     ImagePlus[] images2 = new ImagePlus[channels];
/* 206 */     Color[] defaultColors = { Color.red, Color.green, Color.blue, Color.white };
/* 207 */     Color[] colors = new Color[channels];
/* 208 */     int j = 0;
/* 209 */     for (int i = 0; i < n; i++) {
/* 210 */       if (images[i] != null) {
/* 211 */         images2[j] = images[i];
/* 212 */         if (i < defaultColors.length)
/* 213 */           colors[j] = defaultColors[i];
/* 214 */         j++;
/*     */       }
/*     */     }
/* 217 */     images = images2;
/* 218 */     ImageStack[] stacks = new ImageStack[channels];
/* 219 */     for (int i = 0; i < channels; i++) {
/* 220 */       ImagePlus imp2 = images[i];
/* 221 */       if (isDuplicate(i, images))
/* 222 */         imp2 = imp2.duplicate();
/* 223 */       stacks[i] = imp2.getStack();
/*     */     }
/* 225 */     ImagePlus imp = images[0];
/* 226 */     int w = imp.getWidth();
/* 227 */     int h = imp.getHeight();
/* 228 */     int slices = imp.getNSlices();
/* 229 */     int frames = imp.getNFrames();
/* 230 */     ImageStack stack2 = new ImageStack(w, h);
/*     */ 
/* 232 */     int[] index = new int[channels];
/* 233 */     for (int t = 0; t < frames; t++) {
/* 234 */       for (int z = 0; z < slices; z++) {
/* 235 */         for (int c = 0; c < channels; c++) {
/* 236 */           ImageProcessor ip = stacks[c].getProcessor(index[c] + 1);
/* 237 */           if (keep)
/* 238 */             ip = ip.duplicate();
/* 239 */           stack2.addSlice(null, ip);
/* 240 */           if (keep)
/* 241 */             index[c] += 1;
/*     */           else
/* 243 */             stacks[c].deleteSlice(1);
/*     */         }
/*     */       }
/*     */     }
/* 247 */     String title = imp.getTitle();
/* 248 */     if (title.startsWith("C1-"))
/* 249 */       title = title.substring(3);
/*     */     else
/* 251 */       title = frames > 1 ? "Merged" : "Composite";
/* 252 */     ImagePlus imp2 = new ImagePlus(title, stack2);
/* 253 */     imp2.setDimensions(channels, slices, frames);
/* 254 */     imp2 = new CompositeImage(imp2, 1);
/* 255 */     for (int c = 0; c < channels; c++) {
/* 256 */       ImageProcessor ip = images[c].getProcessor();
/* 257 */       IndexColorModel cm = (IndexColorModel)ip.getColorModel();
/* 258 */       LUT lut = null;
/* 259 */       if ((c < colors.length) && (colors[c] != null) && ((this.ignoreLuts) || (!ip.isColorLut()))) {
/* 260 */         lut = LUT.createLutFromColor(colors[c]);
/* 261 */         lut.min = ip.getMin();
/* 262 */         lut.max = ip.getMax();
/*     */       } else {
/* 264 */         lut = new LUT(cm, ip.getMin(), ip.getMax());
/* 265 */       }((CompositeImage)imp2).setChannelLut(lut, c + 1);
/*     */     }
/* 267 */     imp2.setOpenAsHyperStack(true);
/* 268 */     return imp2;
/*     */   }
/*     */ 
/*     */   private boolean isDuplicate(int index, ImagePlus[] images) {
/* 272 */     int count = 0;
/* 273 */     for (int i = 0; i < index; i++) {
/* 274 */       if (images[index] == images[i])
/* 275 */         return true;
/*     */     }
/* 277 */     return false;
/*     */   }
/*     */ 
/*     */   public ImagePlus createComposite(int w, int h, int d, ImageStack[] stacks, boolean keep)
/*     */   {
/* 282 */     ImagePlus[] images = new ImagePlus[stacks.length];
/* 283 */     for (int i = 0; i < stacks.length; i++)
/* 284 */       images[i] = new ImagePlus("" + i, stacks[i]);
/* 285 */     return mergeHyperstacks(images, keep);
/*     */   }
/*     */ 
/*     */   public ImageStack mergeStacks(int w, int h, int d, ImageStack red, ImageStack green, ImageStack blue, boolean keep) {
/* 289 */     ImageStack rgb = new ImageStack(w, h);
/* 290 */     int inc = d / 10;
/* 291 */     if (inc < 1) inc = 1;
/*     */ 
/* 293 */     int slice = 1;
/* 294 */     this.blank = new byte[w * h];
/*     */ 
/* 296 */     boolean invertedRed = red != null ? red.getProcessor(1).isInvertedLut() : false;
/* 297 */     boolean invertedGreen = green != null ? green.getProcessor(1).isInvertedLut() : false;
/* 298 */     boolean invertedBlue = blue != null ? blue.getProcessor(1).isInvertedLut() : false;
/*     */     try {
/* 300 */       for (int i = 1; i <= d; i++) {
/* 301 */         ColorProcessor cp = new ColorProcessor(w, h);
/* 302 */         byte[] redPixels = getPixels(red, slice, 0);
/* 303 */         byte[] greenPixels = getPixels(green, slice, 1);
/* 304 */         byte[] bluePixels = getPixels(blue, slice, 2);
/* 305 */         if (invertedRed) redPixels = invert(redPixels);
/* 306 */         if (invertedGreen) greenPixels = invert(greenPixels);
/* 307 */         if (invertedBlue) bluePixels = invert(bluePixels);
/* 308 */         cp.setRGB(redPixels, greenPixels, bluePixels);
/* 309 */         if (keep) {
/* 310 */           slice++;
/*     */         } else {
/* 312 */           if (red != null) red.deleteSlice(1);
/* 313 */           if ((green != null) && (green != red)) green.deleteSlice(1);
/* 314 */           if ((blue != null) && (blue != red) && (blue != green)) blue.deleteSlice(1);
/*     */         }
/* 316 */         rgb.addSlice(null, cp);
/* 317 */         if (i % inc == 0) IJ.showProgress(i / d);
/*     */       }
/* 319 */       IJ.showProgress(1.0D);
/*     */     } catch (OutOfMemoryError o) {
/* 321 */       IJ.outOfMemory("Merge Stacks");
/* 322 */       IJ.showProgress(1.0D);
/*     */     }
/* 324 */     return rgb;
/*     */   }
/*     */ 
/*     */   byte[] getPixels(ImageStack stack, int slice, int color) {
/* 328 */     if (stack == null)
/* 329 */       return this.blank;
/* 330 */     Object pixels = stack.getPixels(slice);
/* 331 */     if (!(pixels instanceof int[])) {
/* 332 */       if ((pixels instanceof byte[])) {
/* 333 */         return (byte[])pixels;
/*     */       }
/* 335 */       ImageProcessor ip = stack.getProcessor(slice);
/* 336 */       ip = ip.convertToByte(true);
/* 337 */       return (byte[])ip.getPixels();
/*     */     }
/*     */ 
/* 341 */     int size = stack.getWidth() * stack.getHeight();
/* 342 */     byte[] r = new byte[size];
/* 343 */     byte[] g = new byte[size];
/* 344 */     byte[] b = new byte[size];
/* 345 */     ColorProcessor cp = (ColorProcessor)stack.getProcessor(slice);
/* 346 */     cp.getRGB(r, g, b);
/* 347 */     switch (color) { case 0:
/* 348 */       return r;
/*     */     case 1:
/* 349 */       return g;
/*     */     case 2:
/* 350 */       return b;
/*     */     }
/*     */ 
/* 353 */     return null;
/*     */   }
/*     */ 
/*     */   byte[] invert(byte[] pixels) {
/* 357 */     byte[] pixels2 = new byte[pixels.length];
/* 358 */     System.arraycopy(pixels, 0, pixels2, 0, pixels.length);
/* 359 */     for (int i = 0; i < pixels2.length; i++)
/* 360 */       pixels2[i] = ((byte)(255 - pixels2[i] & 0xFF));
/* 361 */     return pixels2;
/*     */   }
/*     */ 
/*     */   void error(String msg) {
/* 365 */     IJ.error("Merge Channels", msg);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.RGBStackMerge
 * JD-Core Version:    0.6.2
 */