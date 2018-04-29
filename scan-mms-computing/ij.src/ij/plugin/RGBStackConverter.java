/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageConverter;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Image;
/*     */ import java.awt.Label;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class RGBStackConverter
/*     */   implements PlugIn, DialogListener
/*     */ {
/*     */   private int channels1;
/*     */   private int slices1;
/*     */   private int frames1;
/*     */   private int slices2;
/*     */   private int frames2;
/*     */   private int width;
/*     */   private int height;
/*     */   private double imageSize;
/*  13 */   private static boolean staticKeep = true;
/*     */   private boolean keep;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  17 */     ImagePlus imp = IJ.getImage();
/*  18 */     if (!IJ.isMacro()) this.keep = staticKeep;
/*  19 */     CompositeImage cimg = imp.isComposite() ? (CompositeImage)imp : null;
/*  20 */     int size = imp.getStackSize();
/*  21 */     if (((size < 2) || (size > 3)) && (cimg == null)) {
/*  22 */       IJ.error("A 2 or 3 image stack, or a HyperStack, required");
/*  23 */       return;
/*     */     }
/*  25 */     int type = imp.getType();
/*  26 */     if ((cimg == null) && (type != 0) && (type != 1)) {
/*  27 */       IJ.error("8-bit or 16-bit grayscale stack required");
/*  28 */       return;
/*     */     }
/*  30 */     if (!imp.lock())
/*  31 */       return;
/*  32 */     Undo.reset();
/*  33 */     String title = imp.getTitle() + " (RGB)";
/*  34 */     if (cimg != null) {
/*  35 */       compositeToRGB(cimg, title);
/*  36 */     } else if (type == 1) {
/*  37 */       sixteenBitsToRGB(imp);
/*     */     } else {
/*  39 */       ImagePlus imp2 = imp.createImagePlus();
/*  40 */       imp2.setStack(title, imp.getStack());
/*  41 */       ImageConverter ic = new ImageConverter(imp2);
/*  42 */       ic.convertRGBStackToRGB();
/*  43 */       imp2.show();
/*     */     }
/*  45 */     imp.unlock();
/*     */   }
/*     */ 
/*     */   void compositeToRGB(CompositeImage imp, String title) {
/*  49 */     int channels = imp.getNChannels();
/*  50 */     int slices = imp.getNSlices();
/*  51 */     int frames = imp.getNFrames();
/*  52 */     int images = channels * slices * frames;
/*  53 */     if (channels == images) {
/*  54 */       compositeImageToRGB(imp, title);
/*  55 */       return;
/*     */     }
/*  57 */     this.width = imp.getWidth();
/*  58 */     this.height = imp.getHeight();
/*  59 */     this.imageSize = (this.width * this.height * 4.0D / 1048576.0D);
/*  60 */     this.channels1 = imp.getNChannels();
/*  61 */     this.slices1 = (this.slices2 = imp.getNSlices());
/*  62 */     this.frames1 = (this.frames2 = imp.getNFrames());
/*  63 */     int c1 = imp.getChannel();
/*  64 */     int z1 = imp.getSlice();
/*  65 */     int t2 = imp.getFrame();
/*  66 */     if (!showDialog()) {
/*  67 */       return;
/*     */     }
/*  69 */     String title2 = this.keep ? WindowManager.getUniqueName(imp.getTitle()) : imp.getTitle();
/*  70 */     ImagePlus imp2 = imp.createHyperStack(title2, 1, this.slices2, this.frames2, 24);
/*  71 */     convertHyperstack(imp, imp2);
/*  72 */     if ((imp.getWindow() == null) && (!this.keep)) {
/*  73 */       imp.setImage(imp2);
/*  74 */       return;
/*     */     }
/*  76 */     imp2.setOpenAsHyperStack((this.slices2 > 1) || (this.frames2 > 1));
/*  77 */     imp2.show();
/*  78 */     if (!this.keep) {
/*  79 */       imp.changes = false;
/*  80 */       imp.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void convertHyperstack(ImagePlus imp, ImagePlus imp2) {
/*  85 */     int slices = imp2.getNSlices();
/*  86 */     int frames = imp2.getNFrames();
/*  87 */     int c1 = imp.getChannel();
/*  88 */     int z1 = imp.getSlice();
/*  89 */     int t1 = imp.getFrame();
/*  90 */     int i = 1;
/*  91 */     int c = 1;
/*  92 */     ImageStack stack = imp.getStack();
/*  93 */     ImageStack stack2 = imp2.getStack();
/*  94 */     imp.setPositionWithoutUpdate(c, 1, 1);
/*  95 */     ImageProcessor ip = imp.getProcessor();
/*  96 */     double min = ip.getMin();
/*  97 */     double max = ip.getMax();
/*  98 */     for (int z = 1; z <= slices; z++) {
/*  99 */       if (slices == 1) z = z1;
/* 100 */       for (int t = 1; t <= frames; t++)
/*     */       {
/* 102 */         if (frames == 1) t = t1;
/*     */ 
/* 104 */         imp.setPositionWithoutUpdate(c, z, t);
/* 105 */         Image img = imp.getImage();
/* 106 */         int n2 = imp2.getStackIndex(c, z, t);
/* 107 */         stack2.setPixels(new ColorProcessor(img).getPixels(), n2);
/*     */       }
/*     */     }
/* 110 */     imp.setPosition(c1, z1, t1);
/* 111 */     imp2.resetStack();
/* 112 */     imp2.setPosition(1, 1, 1);
/*     */   }
/*     */ 
/*     */   void compositeImageToRGB(CompositeImage imp, String title) {
/* 116 */     if (imp.getMode() == 1) {
/* 117 */       ImagePlus imp2 = imp.createImagePlus();
/* 118 */       imp.updateImage();
/* 119 */       imp2.setProcessor(title, new ColorProcessor(imp.getImage()));
/* 120 */       imp2.show();
/* 121 */       return;
/*     */     }
/* 123 */     ImageStack stack = new ImageStack(imp.getWidth(), imp.getHeight());
/* 124 */     int c = imp.getChannel();
/* 125 */     int n = imp.getNChannels();
/* 126 */     for (int i = 1; i <= n; i++) {
/* 127 */       imp.setPositionWithoutUpdate(i, 1, 1);
/* 128 */       stack.addSlice(null, new ColorProcessor(imp.getImage()));
/*     */     }
/* 130 */     imp.setPosition(c, 1, 1);
/* 131 */     ImagePlus imp2 = imp.createImagePlus();
/* 132 */     imp2.setStack(title, stack);
/* 133 */     Object info = imp.getProperty("Info");
/* 134 */     if (info != null) imp2.setProperty("Info", info);
/* 135 */     imp2.show();
/*     */   }
/*     */ 
/*     */   void sixteenBitsToRGB(ImagePlus imp) {
/* 139 */     Roi roi = imp.getRoi();
/*     */     int height;
/*     */     Rectangle r;
/* 142 */     if (roi != null) {
/* 143 */       Rectangle r = roi.getBounds();
/* 144 */       int width = r.width;
/* 145 */       height = r.height;
/*     */     } else {
/* 147 */       r = new Rectangle(0, 0, imp.getWidth(), imp.getHeight());
/*     */     }
/* 149 */     ImageStack stack1 = imp.getStack();
/* 150 */     ImageStack stack2 = new ImageStack(r.width, r.height);
/* 151 */     for (int i = 1; i <= stack1.getSize(); i++) {
/* 152 */       ImageProcessor ip = stack1.getProcessor(i);
/* 153 */       ip.setRoi(r);
/* 154 */       ImageProcessor ip2 = ip.crop();
/* 155 */       ip2 = ip2.convertToByte(true);
/* 156 */       stack2.addSlice(null, ip2);
/*     */     }
/* 158 */     ImagePlus imp2 = imp.createImagePlus();
/* 159 */     imp2.setStack(imp.getTitle() + " (RGB)", stack2);
/* 160 */     ImageConverter ic = new ImageConverter(imp2);
/* 161 */     ic.convertRGBStackToRGB();
/* 162 */     imp2.show();
/*     */   }
/*     */ 
/*     */   boolean showDialog() {
/* 166 */     GenericDialog gd = new GenericDialog("Convert to RGB");
/* 167 */     gd.setInsets(10, 20, 5);
/* 168 */     gd.addMessage("Create RGB Image With:");
/* 169 */     gd.setInsets(0, 35, 0);
/* 170 */     if (this.slices1 != 1) gd.addCheckbox("Slices (" + this.slices1 + ")", true);
/* 171 */     gd.setInsets(0, 35, 0);
/* 172 */     if (this.frames1 != 1) gd.addCheckbox("Frames (" + this.frames1 + ")", true);
/* 173 */     gd.setInsets(5, 20, 0);
/* 174 */     gd.addMessage(getNewDimensions() + "      ");
/* 175 */     gd.setInsets(15, 20, 0);
/* 176 */     gd.addCheckbox("Keep Source", this.keep);
/* 177 */     gd.addDialogListener(this);
/* 178 */     gd.showDialog();
/* 179 */     if (gd.wasCanceled()) {
/* 180 */       return false;
/*     */     }
/* 182 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 186 */     if (IJ.isMacOSX()) IJ.wait(100);
/* 187 */     if (this.slices1 != 1) this.slices2 = (gd.getNextBoolean() ? this.slices1 : 1);
/* 188 */     if (this.frames1 != 1) this.frames2 = (gd.getNextBoolean() ? this.frames1 : 1);
/* 189 */     this.keep = gd.getNextBoolean();
/* 190 */     if (!IJ.isMacro()) staticKeep = this.keep;
/* 191 */     ((Label)gd.getMessage()).setText(getNewDimensions());
/* 192 */     return true;
/*     */   }
/*     */ 
/*     */   String getNewDimensions() {
/* 196 */     String s1 = this.slices2 > 1 ? "x" + this.slices2 : "";
/* 197 */     String s2 = this.frames2 > 1 ? "x" + this.frames2 : "";
/* 198 */     String s = this.width + "x" + this.height + s1 + s2;
/* 199 */     s = s + " (" + (int)Math.round(this.imageSize * this.slices2 * this.frames2) + "MB)";
/* 200 */     return s;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.RGBStackConverter
 * JD-Core Version:    0.6.2
 */