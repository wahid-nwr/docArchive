/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.process.ImageProcessor;
/*     */ 
/*     */ public class Concatenator
/*     */   implements PlugIn
/*     */ {
/*     */   ImagePlus imp1;
/*     */   ImagePlus imp2;
/*     */   static boolean keep;
/*  13 */   static String title = "Concatenated Stacks";
/*     */ 
/*     */   public void run(String arg) {
/*  16 */     if (!showDialog())
/*  17 */       return;
/*  18 */     ImagePlus imp3 = null;
/*  19 */     if ((this.imp1.isComposite()) || (this.imp1.isHyperStack())) {
/*  20 */       ImagePlus[] images = new ImagePlus[2];
/*  21 */       images[0] = this.imp1;
/*  22 */       images[1] = this.imp2;
/*  23 */       imp3 = concatenate(images, keep);
/*  24 */       if (imp3 == null)
/*  25 */         error();
/*     */       else
/*  27 */         imp3.setTitle(title);
/*     */     } else {
/*  29 */       imp3 = concatenate(this.imp1, this.imp2, keep);
/*  30 */     }if (imp3 != null) imp3.show(); 
/*     */   }
/*     */ 
/*     */   public ImagePlus concatenate(ImagePlus imp1, ImagePlus imp2, boolean keep)
/*     */   {
/*  34 */     if ((imp1.getType() != imp2.getType()) || (imp1.isHyperStack()) || (imp2.isHyperStack())) {
/*  35 */       error(); return null;
/*  36 */     }int width = imp1.getWidth();
/*  37 */     int height = imp1.getHeight();
/*  38 */     if ((width != imp2.getWidth()) || (height != imp2.getHeight())) {
/*  39 */       error(); return null;
/*  40 */     }ImageStack stack1 = imp1.getStack();
/*  41 */     ImageStack stack2 = imp2.getStack();
/*  42 */     int size1 = stack1.getSize();
/*  43 */     int size2 = stack2.getSize();
/*  44 */     ImageStack stack3 = imp1.createEmptyStack();
/*  45 */     int slice = 1;
/*  46 */     for (int i = 1; i <= size1; i++) {
/*  47 */       ImageProcessor ip = stack1.getProcessor(slice);
/*  48 */       String label = stack1.getSliceLabel(slice);
/*  49 */       if ((keep) || (imp1 == imp2)) {
/*  50 */         ip = ip.duplicate();
/*  51 */         slice++;
/*     */       } else {
/*  53 */         stack1.deleteSlice(slice);
/*  54 */       }stack3.addSlice(label, ip);
/*     */     }
/*  56 */     slice = 1;
/*  57 */     for (int i = 1; i <= size2; i++) {
/*  58 */       ImageProcessor ip = stack2.getProcessor(slice);
/*  59 */       String label = stack2.getSliceLabel(slice);
/*  60 */       if ((keep) || (imp1 == imp2)) {
/*  61 */         ip = ip.duplicate();
/*  62 */         slice++;
/*     */       } else {
/*  64 */         stack2.deleteSlice(slice);
/*  65 */       }stack3.addSlice(label, ip);
/*     */     }
/*  67 */     ImagePlus imp3 = new ImagePlus(title, stack3);
/*  68 */     imp3.setCalibration(imp1.getCalibration());
/*  69 */     if (!keep) {
/*  70 */       imp1.changes = false;
/*  71 */       imp1.close();
/*  72 */       if (imp1 != imp2) {
/*  73 */         imp2.changes = false;
/*  74 */         imp2.close();
/*     */       }
/*     */     }
/*  77 */     return imp3;
/*     */   }
/*     */ 
/*     */   public ImagePlus concatenate(ImagePlus[] images, boolean keepSourceImages) {
/*  81 */     int n = images.length;
/*  82 */     int width = images[0].getWidth();
/*  83 */     int height = images[0].getHeight();
/*  84 */     int bitDepth = images[0].getBitDepth();
/*  85 */     int channels = images[0].getNChannels();
/*  86 */     int slices = images[0].getNSlices();
/*  87 */     int frames = images[0].getNFrames();
/*  88 */     boolean concatSlices = (slices > 1) && (frames == 1);
/*  89 */     for (int i = 1; i < n; i++) {
/*  90 */       if (images[i].getNFrames() > 1) concatSlices = false;
/*  91 */       if ((images[i].getWidth() != width) || (images[i].getHeight() != height) || (images[i].getBitDepth() != bitDepth) || (images[i].getNChannels() != channels) || ((!concatSlices) && (images[i].getNSlices() != slices)))
/*     */       {
/*  96 */         return null;
/*     */       }
/*     */     }
/*  98 */     ImageStack stack2 = new ImageStack(width, height);
/*  99 */     int slices2 = 0; int frames2 = 0;
/* 100 */     for (int i = 0; i < n; i++) {
/* 101 */       ImageStack stack = images[i].getStack();
/* 102 */       slices = images[i].getNSlices();
/* 103 */       if (concatSlices) {
/* 104 */         slices = images[i].getNSlices();
/* 105 */         slices2 += slices;
/* 106 */         frames2 = frames;
/*     */       } else {
/* 108 */         frames = images[i].getNFrames();
/* 109 */         frames2 += frames;
/* 110 */         slices2 = slices;
/*     */       }
/* 112 */       for (int f = 1; f <= frames; f++) {
/* 113 */         for (int s = 1; s <= slices; s++) {
/* 114 */           for (int c = 1; c <= channels; c++) {
/* 115 */             int index = (f - 1) * channels * s + (s - 1) * channels + c;
/* 116 */             ImageProcessor ip = stack.getProcessor(index);
/* 117 */             if (keepSourceImages)
/* 118 */               ip = ip.duplicate();
/* 119 */             String label = stack.getSliceLabel(index);
/* 120 */             stack2.addSlice(label, ip);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 125 */     ImagePlus imp2 = new ImagePlus("Concatenated Images", stack2);
/* 126 */     imp2.setDimensions(channels, slices2, frames2);
/* 127 */     if (channels > 1) {
/* 128 */       int mode = 0;
/* 129 */       if (images[0].isComposite())
/* 130 */         mode = ((CompositeImage)images[0]).getMode();
/* 131 */       imp2 = new CompositeImage(imp2, mode);
/* 132 */       ((CompositeImage)imp2).copyLuts(images[0]);
/*     */     }
/* 134 */     if ((channels > 1) && (frames2 > 1))
/* 135 */       imp2.setOpenAsHyperStack(true);
/* 136 */     if (!keepSourceImages) {
/* 137 */       for (int i = 0; i < n; i++) {
/* 138 */         images[i].changes = false;
/* 139 */         images[i].close();
/*     */       }
/*     */     }
/* 142 */     return imp2;
/*     */   }
/*     */ 
/*     */   int getStackIndex(int channel, int slice, int frame, int nChannels, int nSlices, int nFrames) {
/* 146 */     return (frame - 1) * nChannels * nSlices + (slice - 1) * nChannels + channel;
/*     */   }
/*     */ 
/*     */   boolean showDialog() {
/* 150 */     int[] wList = WindowManager.getIDList();
/* 151 */     if (wList == null) {
/* 152 */       IJ.noImage();
/* 153 */       return false;
/*     */     }
/*     */ 
/* 156 */     String[] titles = new String[wList.length];
/* 157 */     for (int i = 0; i < wList.length; i++) {
/* 158 */       ImagePlus imp = WindowManager.getImage(wList[i]);
/* 159 */       titles[i] = (imp != null ? imp.getTitle() : "");
/*     */     }
/*     */ 
/* 162 */     GenericDialog gd = new GenericDialog("Concatenator");
/* 163 */     gd.addChoice("Stack1:", titles, titles[0]);
/* 164 */     gd.addChoice("Stack2:", titles, wList.length > 1 ? titles[1] : titles[0]);
/* 165 */     gd.addStringField("Title:", title, 16);
/* 166 */     gd.addCheckbox("Keep Source Stacks", keep);
/* 167 */     gd.showDialog();
/* 168 */     if (gd.wasCanceled())
/* 169 */       return false;
/* 170 */     int[] index = new int[3];
/* 171 */     int index1 = gd.getNextChoiceIndex();
/* 172 */     int index2 = gd.getNextChoiceIndex();
/* 173 */     title = gd.getNextString();
/* 174 */     keep = gd.getNextBoolean();
/*     */ 
/* 176 */     this.imp1 = WindowManager.getImage(wList[index1]);
/* 177 */     this.imp2 = WindowManager.getImage(wList[index2]);
/* 178 */     return true;
/*     */   }
/*     */ 
/*     */   void error() {
/* 182 */     IJ.showMessage("Concatenator", "This command requires two images with\nthe same dimesions and data type.");
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Concatenator
 * JD-Core Version:    0.6.2
 */