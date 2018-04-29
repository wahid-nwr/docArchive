/*     */ package ij.process;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.LookUpTable;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.CompositeConverter;
/*     */ 
/*     */ public class StackConverter
/*     */ {
/*     */   ImagePlus imp;
/*     */   int type;
/*     */   int nSlices;
/*     */   int width;
/*     */   int height;
/*     */ 
/*     */   public StackConverter(ImagePlus imp)
/*     */   {
/*  15 */     this.imp = imp;
/*  16 */     this.type = imp.getType();
/*  17 */     this.nSlices = imp.getStackSize();
/*  18 */     if (this.nSlices < 2)
/*  19 */       throw new IllegalArgumentException("Stack required");
/*  20 */     this.width = imp.getWidth();
/*  21 */     this.height = imp.getHeight();
/*     */   }
/*     */ 
/*     */   public void convertToGray8()
/*     */   {
/*  26 */     ImageStack stack1 = this.imp.getStack();
/*  27 */     int currentSlice = this.imp.getCurrentSlice();
/*  28 */     ImageProcessor ip = this.imp.getProcessor();
/*  29 */     boolean colorLut = ip.isColorLut();
/*  30 */     boolean pseudoColorLut = (colorLut) && (ip.isPseudoColorLut());
/*  31 */     if ((this.type == 0) && (pseudoColorLut)) {
/*  32 */       boolean invertedLut = ip.isInvertedLut();
/*  33 */       ip.setColorModel(LookUpTable.createGrayscaleColorModel(invertedLut));
/*  34 */       stack1.setColorModel(ip.getColorModel());
/*  35 */       this.imp.updateAndDraw();
/*  36 */       return;
/*     */     }
/*  38 */     if ((this.type == 4) || (this.type == 3) || (colorLut)) {
/*  39 */       convertRGBToGray8();
/*  40 */       this.imp.setSlice(currentSlice);
/*  41 */       return;
/*     */     }
/*     */ 
/*  44 */     ImageStack stack2 = new ImageStack(this.width, this.height);
/*     */ 
/*  47 */     double min = ip.getMin();
/*  48 */     double max = ip.getMax();
/*  49 */     int inc = this.nSlices / 20;
/*  50 */     if (inc < 1) inc = 1;
/*  51 */     LUT[] luts = this.imp.isComposite() ? ((CompositeImage)this.imp).getLuts() : null;
/*  52 */     for (int i = 1; i <= this.nSlices; i++) {
/*  53 */       String label = stack1.getSliceLabel(1);
/*  54 */       ip = stack1.getProcessor(1);
/*  55 */       stack1.deleteSlice(1);
/*  56 */       if (luts != null) {
/*  57 */         int index = (i - 1) % luts.length;
/*  58 */         min = luts[index].min;
/*  59 */         max = luts[index].max;
/*     */       }
/*  61 */       ip.setMinAndMax(min, max);
/*  62 */       boolean scale = ImageConverter.getDoScaling();
/*  63 */       stack2.addSlice(label, ip.convertToByte(scale));
/*  64 */       if (i % inc == 0) {
/*  65 */         IJ.showProgress(i / this.nSlices);
/*  66 */         IJ.showStatus("Converting to 8-bits: " + i + "/" + this.nSlices);
/*     */       }
/*     */     }
/*  69 */     this.imp.setStack(null, stack2);
/*  70 */     this.imp.setCalibration(this.imp.getCalibration());
/*  71 */     if (this.imp.isComposite()) {
/*  72 */       ((CompositeImage)this.imp).resetDisplayRanges();
/*  73 */       ((CompositeImage)this.imp).updateAllChannelsAndDraw();
/*     */     }
/*  75 */     this.imp.setSlice(currentSlice);
/*  76 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   void convertRGBToGray8()
/*     */   {
/*  81 */     ImageStack stack1 = this.imp.getStack();
/*  82 */     ImageStack stack2 = new ImageStack(this.width, this.height);
/*     */ 
/*  86 */     int inc = this.nSlices / 20;
/*  87 */     if (inc < 1) inc = 1;
/*  88 */     for (int i = 1; i <= this.nSlices; i++) {
/*  89 */       String label = stack1.getSliceLabel(1);
/*  90 */       ImageProcessor ip = stack1.getProcessor(1);
/*  91 */       stack1.deleteSlice(1);
/*  92 */       if ((ip instanceof ByteProcessor))
/*  93 */         ip = new ColorProcessor(ip.createImage());
/*  94 */       boolean scale = ImageConverter.getDoScaling();
/*  95 */       stack2.addSlice(label, ip.convertToByte(scale));
/*  96 */       if (i % inc == 0) {
/*  97 */         IJ.showProgress(i / this.nSlices);
/*  98 */         IJ.showStatus("Converting to 8-bits: " + i + "/" + this.nSlices);
/*     */       }
/*     */     }
/* 101 */     this.imp.setStack(null, stack2);
/* 102 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   public void convertToGray16()
/*     */   {
/* 107 */     if (this.type == 1)
/* 108 */       return;
/* 109 */     if ((this.type != 0) && (this.type != 2))
/* 110 */       throw new IllegalArgumentException("Unsupported conversion");
/* 111 */     ImageStack stack1 = this.imp.getStack();
/* 112 */     ImageStack stack2 = new ImageStack(this.width, this.height);
/*     */ 
/* 114 */     int inc = this.nSlices / 20;
/* 115 */     if (inc < 1) inc = 1;
/* 116 */     boolean scale = (this.type == 2) && (ImageConverter.getDoScaling());
/*     */ 
/* 118 */     for (int i = 1; i <= this.nSlices; i++) {
/* 119 */       String label = stack1.getSliceLabel(1);
/* 120 */       ImageProcessor ip1 = stack1.getProcessor(1);
/* 121 */       ImageProcessor ip2 = ip1.convertToShort(scale);
/* 122 */       stack1.deleteSlice(1);
/* 123 */       stack2.addSlice(label, ip2);
/* 124 */       if (i % inc == 0) {
/* 125 */         IJ.showProgress(i / this.nSlices);
/* 126 */         IJ.showStatus("Converting to 16-bits: " + i + "/" + this.nSlices);
/*     */       }
/*     */     }
/* 129 */     IJ.showProgress(1.0D);
/* 130 */     this.imp.setStack(null, stack2);
/*     */   }
/*     */ 
/*     */   public void convertToGray32()
/*     */   {
/* 135 */     if (this.type == 2)
/* 136 */       return;
/* 137 */     if ((this.type != 0) && (this.type != 1))
/* 138 */       throw new IllegalArgumentException("Unsupported conversion");
/* 139 */     ImageStack stack1 = this.imp.getStack();
/* 140 */     ImageStack stack2 = new ImageStack(this.width, this.height);
/*     */ 
/* 142 */     int inc = this.nSlices / 20;
/* 143 */     if (inc < 1) inc = 1;
/*     */ 
/* 145 */     Calibration cal = this.imp.getCalibration();
/* 146 */     for (int i = 1; i <= this.nSlices; i++) {
/* 147 */       String label = stack1.getSliceLabel(1);
/* 148 */       ImageProcessor ip1 = stack1.getProcessor(1);
/* 149 */       ip1.setCalibrationTable(cal.getCTable());
/* 150 */       ImageProcessor ip2 = ip1.convertToFloat();
/* 151 */       stack1.deleteSlice(1);
/* 152 */       stack2.addSlice(label, ip2);
/* 153 */       if (i % inc == 0) {
/* 154 */         IJ.showProgress(i / this.nSlices);
/* 155 */         IJ.showStatus("Converting to 32-bits: " + i + "/" + this.nSlices);
/*     */       }
/*     */     }
/* 158 */     IJ.showProgress(1.0D);
/* 159 */     this.imp.setStack(null, stack2);
/* 160 */     this.imp.setCalibration(this.imp.getCalibration());
/*     */   }
/*     */ 
/*     */   public void convertToRGB()
/*     */   {
/* 165 */     if (this.imp.isComposite())
/* 166 */       throw new IllegalArgumentException("Use Image>Color>Stack to RGB");
/* 167 */     ImageStack stack1 = this.imp.getStack();
/* 168 */     ImageStack stack2 = new ImageStack(this.width, this.height);
/*     */ 
/* 170 */     int inc = this.nSlices / 20;
/* 171 */     if (inc < 1) inc = 1;
/*     */ 
/* 173 */     Calibration cal = this.imp.getCalibration();
/* 174 */     for (int i = 1; i <= this.nSlices; i++) {
/* 175 */       String label = stack1.getSliceLabel(i);
/* 176 */       ImageProcessor ip1 = stack1.getProcessor(i);
/* 177 */       ImageProcessor ip2 = ip1.convertToRGB();
/* 178 */       stack2.addSlice(label, ip2);
/* 179 */       if (i % inc == 0) {
/* 180 */         IJ.showProgress(i / this.nSlices);
/* 181 */         IJ.showStatus("Converting to RGB: " + i + "/" + this.nSlices);
/*     */       }
/*     */     }
/* 184 */     IJ.showProgress(1.0D);
/* 185 */     this.imp.setStack(null, stack2);
/* 186 */     this.imp.setCalibration(this.imp.getCalibration());
/*     */   }
/*     */ 
/*     */   public void convertToRGBHyperstack()
/*     */   {
/* 192 */     if (this.type != 4)
/* 193 */       throw new IllegalArgumentException("RGB stack required");
/* 194 */     new CompositeConverter().run("composite");
/*     */   }
/*     */ 
/*     */   public void convertToHSBHyperstack()
/*     */   {
/* 200 */     if (this.type != 4)
/* 201 */       throw new IllegalArgumentException("RGB stack required");
/* 202 */     ImageStack stack1 = this.imp.getStack();
/* 203 */     ImageStack stack2 = new ImageStack(this.width, this.height);
/* 204 */     int nSlices = stack1.getSize();
/* 205 */     Calibration cal = this.imp.getCalibration();
/* 206 */     int inc = nSlices / 20;
/* 207 */     if (inc < 1) inc = 1;
/* 208 */     for (int i = 1; i <= nSlices; i++) {
/* 209 */       String label = stack1.getSliceLabel(i);
/* 210 */       ColorProcessor cp = (ColorProcessor)stack1.getProcessor(i);
/* 211 */       ImageStack stackHSB = cp.getHSBStack();
/* 212 */       stack2.addSlice(label, stackHSB.getProcessor(1));
/* 213 */       stack2.addSlice(label, stackHSB.getProcessor(2));
/* 214 */       stack2.addSlice(label, stackHSB.getProcessor(3));
/* 215 */       if (i % inc == 0) {
/* 216 */         IJ.showProgress(i / nSlices);
/* 217 */         IJ.showStatus("Converting to HSB: " + i + "/" + nSlices);
/*     */       }
/*     */     }
/* 220 */     IJ.showProgress(1.0D);
/* 221 */     this.imp.setStack(null, stack2);
/* 222 */     this.imp.setCalibration(cal);
/* 223 */     this.imp.setDimensions(3, nSlices, 1);
/* 224 */     CompositeImage ci = new CompositeImage(this.imp, 3);
/* 225 */     ci.show();
/* 226 */     this.imp.hide();
/*     */   }
/*     */ 
/*     */   public void convertToIndexedColor(int nColors)
/*     */   {
/* 232 */     if (this.type != 4)
/* 233 */       throw new IllegalArgumentException("RGB stack required");
/* 234 */     ImageStack stack = this.imp.getStack();
/* 235 */     int size = stack.getSize();
/* 236 */     ImageProcessor montage = new ColorProcessor(this.width * size, this.height);
/* 237 */     for (int i = 0; i < size; i++)
/* 238 */       montage.insert(stack.getProcessor(i + 1), i * this.width, 0);
/* 239 */     MedianCut mc = new MedianCut((ColorProcessor)montage);
/* 240 */     montage = mc.convertToByte(nColors);
/* 241 */     ImageStack stack2 = new ImageStack(this.width, this.height);
/* 242 */     for (int i = 0; i < size; i++) {
/* 243 */       montage.setRoi(i * this.width, 0, this.width, this.height);
/* 244 */       stack2.addSlice(null, montage.crop());
/*     */     }
/* 246 */     this.imp.setStack(null, stack2);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.StackConverter
 * JD-Core Version:    0.6.2
 */