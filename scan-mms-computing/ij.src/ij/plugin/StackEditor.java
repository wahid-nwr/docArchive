/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.LUT;
/*     */ import java.awt.Color;
/*     */ 
/*     */ public class StackEditor
/*     */   implements PlugIn
/*     */ {
/*     */   ImagePlus imp;
/*     */   int nSlices;
/*     */   int width;
/*     */   int height;
/*     */   static boolean deleteFrames;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  18 */     this.imp = IJ.getImage();
/*  19 */     this.nSlices = this.imp.getStackSize();
/*  20 */     this.width = this.imp.getWidth();
/*  21 */     this.height = this.imp.getHeight();
/*     */ 
/*  23 */     if (arg.equals("add"))
/*  24 */       addSlice();
/*  25 */     else if (arg.equals("delete"))
/*  26 */       deleteSlice();
/*  27 */     else if (arg.equals("toimages"))
/*  28 */       convertStackToImages(this.imp);
/*     */   }
/*     */ 
/*     */   void addSlice() {
/*  32 */     if ((this.imp.isComposite()) && (this.nSlices == this.imp.getNChannels())) {
/*  33 */       addChannel();
/*  34 */       return;
/*     */     }
/*  36 */     if (this.imp.isDisplayedHyperStack()) return;
/*  37 */     if (!this.imp.lock()) return;
/*  38 */     int id = 0;
/*  39 */     ImageStack stack = this.imp.getStack();
/*  40 */     if (stack.getSize() == 1) {
/*  41 */       String label = stack.getSliceLabel(1);
/*  42 */       if ((label != null) && (label.indexOf("\n") != -1))
/*  43 */         stack.setSliceLabel(null, 1);
/*  44 */       Object obj = this.imp.getProperty("Label");
/*  45 */       if ((obj != null) && ((obj instanceof String)))
/*  46 */         stack.setSliceLabel((String)obj, 1);
/*  47 */       id = this.imp.getID();
/*     */     }
/*  49 */     ImageProcessor ip = this.imp.getProcessor();
/*  50 */     int n = this.imp.getCurrentSlice();
/*  51 */     if (IJ.altKeyDown()) n--;
/*  52 */     stack.addSlice(null, ip.createProcessor(this.width, this.height), n);
/*  53 */     this.imp.setStack(null, stack);
/*  54 */     this.imp.setSlice(n + 1);
/*  55 */     this.imp.unlock();
/*  56 */     if (id != 0) IJ.selectWindow(id); 
/*     */   }
/*     */ 
/*     */   void deleteSlice()
/*     */   {
/*  60 */     if (this.nSlices < 2) {
/*  61 */       IJ.error("\"Delete Slice\" requires a stack"); return;
/*  62 */     }if ((this.imp.isComposite()) && (this.nSlices == this.imp.getNChannels())) {
/*  63 */       deleteChannel();
/*  64 */       return;
/*     */     }
/*  66 */     if (this.imp.isDisplayedHyperStack()) {
/*  67 */       deleteHyperstackSliceOrFrame();
/*  68 */       return;
/*     */     }
/*  70 */     if (!this.imp.lock()) return;
/*  71 */     ImageStack stack = this.imp.getStack();
/*  72 */     int n = this.imp.getCurrentSlice();
/*  73 */     stack.deleteSlice(n);
/*  74 */     if (stack.getSize() == 1) {
/*  75 */       String label = stack.getSliceLabel(1);
/*  76 */       if (label != null) this.imp.setProperty("Label", label);
/*     */     }
/*  78 */     this.imp.setStack(null, stack);
/*  79 */     if (n-- < 1) n = 1;
/*  80 */     this.imp.setSlice(n);
/*  81 */     this.imp.unlock();
/*     */   }
/*     */ 
/*     */   void addChannel() {
/*  85 */     int c = this.imp.getChannel();
/*  86 */     ImageStack stack = this.imp.getStack();
/*  87 */     CompositeImage ci = (CompositeImage)this.imp;
/*  88 */     LUT[] luts = ci.getLuts();
/*  89 */     ImageProcessor ip = stack.getProcessor(1);
/*  90 */     ImageProcessor ip2 = ip.createProcessor(ip.getWidth(), ip.getHeight());
/*  91 */     stack.addSlice(null, ip2, c);
/*  92 */     ImagePlus imp2 = this.imp.createImagePlus();
/*  93 */     imp2.setStack(stack);
/*  94 */     int n = imp2.getStackSize();
/*  95 */     imp2 = new CompositeImage(this.imp, ci.getMode());
/*  96 */     LUT lut = LUT.createLutFromColor(Color.white);
/*  97 */     int index = 0;
/*  98 */     for (int i = 1; i <= n; i++)
/*  99 */       if (c + 1 == index + 1) {
/* 100 */         ((CompositeImage)imp2).setChannelLut(lut, i);
/* 101 */         c = -1;
/*     */       } else {
/* 103 */         ((CompositeImage)imp2).setChannelLut(luts[(index++)], i);
/*     */       }
/* 105 */     this.imp.changes = false;
/* 106 */     this.imp.hide();
/* 107 */     imp2.show();
/*     */   }
/*     */ 
/*     */   void deleteChannel() {
/* 111 */     int c = this.imp.getChannel();
/* 112 */     ImageStack stack = this.imp.getStack();
/* 113 */     CompositeImage ci = (CompositeImage)this.imp;
/* 114 */     LUT[] luts = ci.getLuts();
/* 115 */     stack.deleteSlice(c);
/* 116 */     ImagePlus imp2 = this.imp.createImagePlus();
/* 117 */     imp2.setStack(stack);
/* 118 */     int n = imp2.getStackSize();
/* 119 */     int mode = ci.getMode();
/* 120 */     if ((mode == 1) && (n == 1))
/* 121 */       mode = 2;
/* 122 */     imp2 = new CompositeImage(this.imp, mode);
/* 123 */     int index = 0;
/* 124 */     for (int i = 1; i <= n; i++) {
/* 125 */       if (c == index + 1) index++;
/* 126 */       ((CompositeImage)imp2).setChannelLut(luts[(index++)], i);
/*     */     }
/* 128 */     this.imp.changes = false;
/* 129 */     this.imp.hide();
/* 130 */     imp2.show();
/*     */   }
/*     */ 
/*     */   void deleteHyperstackSliceOrFrame() {
/* 134 */     int channels = this.imp.getNChannels();
/* 135 */     int slices = this.imp.getNSlices();
/* 136 */     int frames = this.imp.getNFrames();
/* 137 */     int c1 = this.imp.getChannel();
/* 138 */     int z1 = this.imp.getSlice();
/* 139 */     int t1 = this.imp.getFrame();
/* 140 */     if ((frames > 1) && (slices == 1)) {
/* 141 */       deleteFrames = true;
/* 142 */     } else if ((frames == 1) && (slices > 1)) {
/* 143 */       deleteFrames = false;
/* 144 */     } else if ((slices > 1) && (frames > 1)) {
/* 145 */       GenericDialog gd = new GenericDialog("Delete Slice");
/* 146 */       gd.addCheckbox("Delete time point " + t1, deleteFrames);
/* 147 */       gd.showDialog();
/* 148 */       if (gd.wasCanceled()) return;
/* 149 */       deleteFrames = gd.getNextBoolean();
/*     */     } else {
/* 151 */       return;
/* 152 */     }if (!this.imp.lock()) return;
/* 153 */     ImageStack stack = this.imp.getStack();
/* 154 */     if (deleteFrames) {
/* 155 */       for (int z = slices; z >= 1; z--) {
/* 156 */         int index = this.imp.getStackIndex(channels, z, t1);
/* 157 */         for (int i = 0; i < channels; i++)
/* 158 */           stack.deleteSlice(index - i);
/*     */       }
/* 160 */       frames--;
/*     */     } else {
/* 162 */       for (int t = frames; t >= 1; t--) {
/* 163 */         int index = this.imp.getStackIndex(channels, z1, t);
/* 164 */         for (int i = 0; i < channels; i++)
/* 165 */           stack.deleteSlice(index - i);
/*     */       }
/* 167 */       slices--;
/*     */     }
/* 169 */     this.imp.setDimensions(channels, slices, frames);
/*     */ 
/* 172 */     this.imp.unlock();
/*     */   }
/*     */ 
/*     */   public void convertImagesToStack() {
/* 176 */     new ImagesToStack().run("");
/*     */   }
/*     */ 
/*     */   public void convertStackToImages(ImagePlus imp) {
/* 180 */     if (this.nSlices < 2) {
/* 181 */       IJ.error("\"Convert Stack to Images\" requires a stack"); return;
/* 182 */     }if (!imp.lock())
/* 183 */       return;
/* 184 */     ImageStack stack = imp.getStack();
/* 185 */     int size = stack.getSize();
/* 186 */     if ((size > 30) && (!IJ.isMacro())) {
/* 187 */       boolean ok = IJ.showMessageWithCancel("Convert to Images?", "Are you sure you want to convert this\nstack to " + size + " separate windows?");
/*     */ 
/* 190 */       if (!ok) {
/* 191 */         imp.unlock(); return;
/*     */       }
/*     */     }
/* 193 */     Calibration cal = imp.getCalibration();
/* 194 */     CompositeImage cimg = imp.isComposite() ? (CompositeImage)imp : null;
/* 195 */     if (imp.getNChannels() != imp.getStackSize()) cimg = null;
/* 196 */     for (int i = 1; i <= size; i++) {
/* 197 */       String label = stack.getShortSliceLabel(i);
/* 198 */       String title = (label != null) && (!label.equals("")) ? label : getTitle(imp, i);
/* 199 */       ImageProcessor ip = stack.getProcessor(i);
/* 200 */       if (cimg != null) {
/* 201 */         LUT lut = cimg.getChannelLut(i);
/* 202 */         if (lut != null) {
/* 203 */           ip.setColorModel(lut);
/* 204 */           ip.setMinAndMax(lut.min, lut.max);
/*     */         }
/*     */       }
/* 207 */       ImagePlus imp2 = new ImagePlus(title, ip);
/* 208 */       imp2.setCalibration(cal);
/* 209 */       String info = stack.getSliceLabel(i);
/* 210 */       if ((info != null) && (!info.equals(label)))
/* 211 */         imp2.setProperty("Info", info);
/* 212 */       imp2.show();
/*     */     }
/* 214 */     imp.changes = false;
/* 215 */     ImageWindow win = imp.getWindow();
/* 216 */     if (win != null)
/* 217 */       win.close();
/* 218 */     else if (Interpreter.isBatchMode())
/* 219 */       Interpreter.removeBatchModeImage(imp);
/* 220 */     imp.unlock();
/*     */   }
/*     */ 
/*     */   String getTitle(ImagePlus imp, int n) {
/* 224 */     String digits = "00000000" + n;
/* 225 */     return imp.getShortTitle() + "-" + digits.substring(digits.length() - 4, digits.length());
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.StackEditor
 * JD-Core Version:    0.6.2
 */