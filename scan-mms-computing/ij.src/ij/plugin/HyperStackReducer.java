/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.LUT;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Label;
/*     */ 
/*     */ public class HyperStackReducer
/*     */   implements PlugIn, DialogListener
/*     */ {
/*     */   ImagePlus imp;
/*     */   int channels1;
/*     */   int slices1;
/*     */   int frames1;
/*     */   int channels2;
/*     */   int slices2;
/*     */   int frames2;
/*     */   double imageSize;
/*  15 */   static boolean keep = true;
/*     */ 
/*     */   public HyperStackReducer()
/*     */   {
/*     */   }
/*     */ 
/*     */   public HyperStackReducer(ImagePlus imp)
/*     */   {
/*  23 */     this.imp = imp;
/*     */   }
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  28 */     this.imp = IJ.getImage();
/*  29 */     if (!this.imp.isHyperStack()) {
/*  30 */       IJ.error("Reducer", "HyperStack required");
/*  31 */       return;
/*     */     }
/*  33 */     int width = this.imp.getWidth();
/*  34 */     int height = this.imp.getHeight();
/*  35 */     this.imageSize = (width * height * this.imp.getBytesPerPixel() / 1048576.0D);
/*  36 */     this.channels1 = (this.channels2 = this.imp.getNChannels());
/*  37 */     this.slices1 = (this.slices2 = this.imp.getNSlices());
/*  38 */     this.frames1 = (this.frames2 = this.imp.getNFrames());
/*  39 */     if (!showDialog()) {
/*  40 */       return;
/*     */     }
/*  42 */     String title2 = keep ? WindowManager.getUniqueName(this.imp.getTitle()) : this.imp.getTitle();
/*  43 */     ImagePlus imp2 = null;
/*  44 */     if (keep) {
/*  45 */       imp2 = IJ.createImage(title2, this.imp.getBitDepth() + "-bit", width, height, this.channels2 * this.slices2 * this.frames2);
/*  46 */       if (imp2 == null) return;
/*  47 */       imp2.setDimensions(this.channels2, this.slices2, this.frames2);
/*  48 */       imp2.setCalibration(this.imp.getCalibration());
/*  49 */       imp2.setOpenAsHyperStack(true);
/*     */     } else {
/*  51 */       imp2 = this.imp.createHyperStack(title2, this.channels2, this.slices2, this.frames2, this.imp.getBitDepth());
/*  52 */     }reduce(imp2);
/*  53 */     if ((this.channels2 > 1) && (this.channels2 == this.imp.getNChannels()) && (this.imp.isComposite())) {
/*  54 */       int mode = ((CompositeImage)this.imp).getMode();
/*  55 */       imp2 = new CompositeImage(imp2, mode);
/*  56 */       ((CompositeImage)imp2).copyLuts(this.imp);
/*     */     } else {
/*  58 */       imp2.setDisplayRange(this.imp.getDisplayRangeMin(), this.imp.getDisplayRangeMax());
/*  59 */       if ((this.imp.isComposite()) && (((CompositeImage)this.imp).getMode() == 3))
/*  60 */         IJ.run(imp2, "Grays", "");
/*     */     }
/*  62 */     if ((this.imp.getWindow() == null) && (!keep)) {
/*  63 */       this.imp.setImage(imp2);
/*  64 */       return;
/*     */     }
/*  66 */     imp2.show();
/*     */ 
/*  68 */     if (!keep) {
/*  69 */       this.imp.changes = false;
/*  70 */       this.imp.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reduce(ImagePlus imp2) {
/*  75 */     int channels = imp2.getNChannels();
/*  76 */     int slices = imp2.getNSlices();
/*  77 */     int frames = imp2.getNFrames();
/*  78 */     int c1 = this.imp.getChannel();
/*  79 */     int z1 = this.imp.getSlice();
/*  80 */     int t1 = this.imp.getFrame();
/*  81 */     int i = 1;
/*  82 */     int n = channels * slices * frames;
/*  83 */     ImageStack stack = this.imp.getStack();
/*  84 */     ImageStack stack2 = imp2.getStack();
/*  85 */     for (int c = 1; c <= channels; c++) {
/*  86 */       if (channels == 1) c = c1;
/*  87 */       LUT lut = this.imp.isComposite() ? ((CompositeImage)this.imp).getChannelLut() : null;
/*  88 */       this.imp.setPositionWithoutUpdate(c, 1, 1);
/*  89 */       ImageProcessor ip = this.imp.getProcessor();
/*  90 */       double min = ip.getMin();
/*  91 */       double max = ip.getMax();
/*  92 */       for (int z = 1; z <= slices; z++) {
/*  93 */         if (slices == 1) z = z1;
/*  94 */         for (int t = 1; t <= frames; t++)
/*     */         {
/*  96 */           if (frames == 1) t = t1;
/*     */ 
/*  98 */           this.imp.setPositionWithoutUpdate(c, z, t);
/*  99 */           ip = this.imp.getProcessor();
/* 100 */           int n1 = this.imp.getStackIndex(c, z, t);
/* 101 */           String label = stack.getSliceLabel(n1);
/* 102 */           int n2 = imp2.getStackIndex(c, z, t);
/* 103 */           if (stack2.getPixels(n2) != null)
/* 104 */             stack2.getProcessor(n2).insert(ip, 0, 0);
/*     */           else
/* 106 */             stack2.setPixels(ip.getPixels(), n2);
/* 107 */           stack2.setSliceLabel(label, n2);
/*     */         }
/*     */       }
/* 110 */       if (lut != null) {
/* 111 */         if (imp2.isComposite())
/* 112 */           ((CompositeImage)imp2).setChannelLut(lut);
/*     */         else
/* 114 */           imp2.getProcessor().setColorModel(lut);
/*     */       }
/* 116 */       imp2.getProcessor().setMinAndMax(min, max);
/*     */     }
/* 118 */     this.imp.setPosition(c1, z1, t1);
/* 119 */     imp2.resetStack();
/* 120 */     imp2.setPosition(1, 1, 1);
/*     */   }
/*     */ 
/*     */   boolean showDialog() {
/* 124 */     GenericDialog gd = new GenericDialog("Reduce");
/* 125 */     gd.setInsets(10, 20, 5);
/* 126 */     gd.addMessage("Create Image With:");
/* 127 */     gd.setInsets(0, 35, 0);
/* 128 */     if (this.channels1 != 1) gd.addCheckbox("Channels (" + this.channels1 + ")", true);
/* 129 */     gd.setInsets(0, 35, 0);
/* 130 */     if (this.slices1 != 1) gd.addCheckbox("Slices (" + this.slices1 + ")", true);
/* 131 */     gd.setInsets(0, 35, 0);
/* 132 */     if (this.frames1 != 1) gd.addCheckbox("Frames (" + this.frames1 + ")", true);
/* 133 */     gd.setInsets(5, 20, 0);
/* 134 */     gd.addMessage(getNewDimensions() + "      ");
/* 135 */     gd.setInsets(15, 20, 0);
/* 136 */     gd.addCheckbox("Keep Source", keep);
/* 137 */     gd.addDialogListener(this);
/* 138 */     gd.showDialog();
/* 139 */     if (gd.wasCanceled()) {
/* 140 */       return false;
/*     */     }
/* 142 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 146 */     if (IJ.isMacOSX()) IJ.wait(100);
/* 147 */     if (this.channels1 != 1) this.channels2 = (gd.getNextBoolean() ? this.channels1 : 1);
/* 148 */     if (this.slices1 != 1) this.slices2 = (gd.getNextBoolean() ? this.slices1 : 1);
/* 149 */     if (this.frames1 != 1) this.frames2 = (gd.getNextBoolean() ? this.frames1 : 1);
/* 150 */     keep = gd.getNextBoolean();
/* 151 */     ((Label)gd.getMessage()).setText(getNewDimensions());
/* 152 */     return true;
/*     */   }
/*     */ 
/*     */   String getNewDimensions() {
/* 156 */     String s = this.channels2 + "x" + this.slices2 + "x" + this.frames2;
/* 157 */     s = s + " (" + (int)Math.round(this.imageSize * this.channels2 * this.slices2 * this.frames2) + "MB)";
/* 158 */     return s;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.HyperStackReducer
 * JD-Core Version:    0.6.2
 */