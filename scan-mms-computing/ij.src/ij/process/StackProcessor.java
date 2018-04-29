/*     */ package ij.process;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageStack;
/*     */ import ij.macro.Interpreter;
/*     */ 
/*     */ public class StackProcessor
/*     */ {
/*     */   private ImageStack stack;
/*     */   private ImageProcessor ip;
/*     */   int nSlices;
/*     */   double xScale;
/*     */   double yScale;
/*     */   int[] table;
/*     */   double fillValue;
/*     */   static final int FLIPH = 0;
/*     */   static final int FLIPV = 1;
/*     */   static final int SCALE = 2;
/*     */   static final int INVERT = 3;
/*     */   static final int APPLY_TABLE = 4;
/*     */   static final int SCALE_WITH_FILL = 5;
/*     */ 
/*     */   public StackProcessor(ImageStack stack, ImageProcessor ip)
/*     */   {
/*  25 */     this.stack = stack;
/*  26 */     this.ip = ip;
/*  27 */     this.nSlices = stack.getSize();
/*  28 */     if ((this.nSlices > 1) && (ip != null))
/*  29 */       ip.setProgressBar(null);
/*     */   }
/*     */ 
/*     */   void process(int command)
/*     */   {
/*  35 */     String s = "";
/*  36 */     ImageProcessor ip2 = this.stack.getProcessor(1);
/*  37 */     switch (command) { case 0:
/*     */     case 1:
/*  38 */       s = "Flip: "; break;
/*     */     case 2:
/*  39 */       s = "Scale: "; break;
/*     */     case 5:
/*  40 */       s = "Scale: "; ip2.setBackgroundValue(this.fillValue); break;
/*     */     case 3:
/*  41 */       s = "Invert: "; break;
/*     */     case 4:
/*  42 */       s = "Apply: ";
/*     */     }
/*  44 */     ip2.setRoi(this.ip.getRoi());
/*  45 */     ip2.setInterpolate(this.ip.getInterpolate());
/*  46 */     for (int i = 1; i <= this.nSlices; i++) {
/*  47 */       showStatus(s, i, this.nSlices);
/*  48 */       ip2.setPixels(this.stack.getPixels(i));
/*  49 */       if ((this.nSlices == 1) && (i == 1) && (command == 2))
/*  50 */         ip2.snapshot();
/*  51 */       switch (command) { case 0:
/*  52 */         ip2.flipHorizontal(); break;
/*     */       case 1:
/*  53 */         ip2.flipVertical(); break;
/*     */       case 2:
/*     */       case 5:
/*  54 */         ip2.scale(this.xScale, this.yScale); break;
/*     */       case 3:
/*  55 */         ip2.invert(); break;
/*     */       case 4:
/*  56 */         ip2.applyTable(this.table);
/*     */       }
/*  58 */       IJ.showProgress(i / this.nSlices);
/*     */     }
/*  60 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   public void invert() {
/*  64 */     process(3);
/*     */   }
/*     */ 
/*     */   public void flipHorizontal() {
/*  68 */     process(0);
/*     */   }
/*     */ 
/*     */   public void flipVertical() {
/*  72 */     process(1);
/*     */   }
/*     */ 
/*     */   public void applyTable(int[] table) {
/*  76 */     this.table = table;
/*  77 */     process(4);
/*     */   }
/*     */ 
/*     */   public void scale(double xScale, double yScale) {
/*  81 */     this.xScale = xScale;
/*  82 */     this.yScale = yScale;
/*  83 */     process(2);
/*     */   }
/*     */ 
/*     */   public void scale(double xScale, double yScale, double fillValue) {
/*  87 */     this.xScale = xScale;
/*  88 */     this.yScale = yScale;
/*  89 */     this.fillValue = fillValue;
/*  90 */     process(5);
/*     */   }
/*     */ 
/*     */   public ImageStack resize(int newWidth, int newHeight)
/*     */   {
/*  97 */     return resize(newWidth, newHeight, false);
/*     */   }
/*     */ 
/*     */   public ImageStack resize(int newWidth, int newHeight, boolean averageWhenDownsizing) {
/* 101 */     ImageStack stack2 = new ImageStack(newWidth, newHeight);
/*     */     try
/*     */     {
/* 104 */       for (int i = 1; i <= this.nSlices; i++) {
/* 105 */         showStatus("Resize: ", i, this.nSlices);
/* 106 */         this.ip.setPixels(this.stack.getPixels(1));
/* 107 */         String label = this.stack.getSliceLabel(1);
/* 108 */         this.stack.deleteSlice(1);
/* 109 */         ImageProcessor ip2 = this.ip.resize(newWidth, newHeight, averageWhenDownsizing);
/* 110 */         if (ip2 != null)
/* 111 */           stack2.addSlice(label, ip2);
/* 112 */         IJ.showProgress(i / this.nSlices);
/*     */       }
/* 114 */       IJ.showProgress(1.0D);
/*     */     } catch (OutOfMemoryError o) {
/* 116 */       while (this.stack.getSize() > 1)
/* 117 */         this.stack.deleteLastSlice();
/* 118 */       IJ.outOfMemory("StackProcessor.resize");
/* 119 */       IJ.showProgress(1.0D);
/*     */     }
/* 121 */     return stack2;
/*     */   }
/*     */ 
/*     */   public ImageStack crop(int x, int y, int width, int height)
/*     */   {
/* 126 */     ImageStack stack2 = new ImageStack(width, height);
/*     */ 
/* 128 */     for (int i = 1; i <= this.nSlices; i++) {
/* 129 */       ImageProcessor ip1 = this.stack.getProcessor(1);
/* 130 */       ip1.setRoi(x, y, width, height);
/* 131 */       String label = this.stack.getSliceLabel(1);
/* 132 */       this.stack.deleteSlice(1);
/* 133 */       ImageProcessor ip2 = ip1.crop();
/* 134 */       stack2.addSlice(label, ip2);
/* 135 */       IJ.showProgress(i / this.nSlices);
/*     */     }
/* 137 */     IJ.showProgress(1.0D);
/* 138 */     return stack2;
/*     */   }
/*     */ 
/*     */   ImageStack rotate90Degrees(boolean clockwise) {
/* 142 */     ImageStack stack2 = new ImageStack(this.stack.getHeight(), this.stack.getWidth());
/*     */ 
/* 144 */     for (int i = 1; i <= this.nSlices; i++) {
/* 145 */       showStatus("Rotate: ", i, this.nSlices);
/* 146 */       this.ip.setPixels(this.stack.getPixels(1));
/* 147 */       String label = this.stack.getSliceLabel(1);
/* 148 */       this.stack.deleteSlice(1);
/*     */       ImageProcessor ip2;
/*     */       ImageProcessor ip2;
/* 149 */       if (clockwise)
/* 150 */         ip2 = this.ip.rotateRight();
/*     */       else
/* 152 */         ip2 = this.ip.rotateLeft();
/* 153 */       if (ip2 != null)
/* 154 */         stack2.addSlice(label, ip2);
/* 155 */       if (!Interpreter.isBatchMode())
/* 156 */         IJ.showProgress(i / this.nSlices);
/*     */     }
/* 158 */     if (!Interpreter.isBatchMode())
/* 159 */       IJ.showProgress(1.0D);
/* 160 */     return stack2;
/*     */   }
/*     */ 
/*     */   public ImageStack rotateRight() {
/* 164 */     return rotate90Degrees(true);
/*     */   }
/*     */ 
/*     */   public ImageStack rotateLeft() {
/* 168 */     return rotate90Degrees(false);
/*     */   }
/*     */ 
/*     */   public void copyBits(ImageProcessor src, int xloc, int yloc, int mode) {
/* 172 */     copyBits(src, null, xloc, yloc, mode);
/*     */   }
/*     */ 
/*     */   public void copyBits(ImageStack src, int xloc, int yloc, int mode) {
/* 176 */     copyBits(null, src, xloc, yloc, mode);
/*     */   }
/*     */ 
/*     */   private void copyBits(ImageProcessor srcIp, ImageStack srcStack, int xloc, int yloc, int mode) {
/* 180 */     int inc = this.nSlices / 20;
/* 181 */     if (inc < 1) inc = 1;
/* 182 */     boolean stackSource = srcIp == null;
/* 183 */     for (int i = 1; i <= this.nSlices; i++) {
/* 184 */       if (stackSource)
/* 185 */         srcIp = srcStack.getProcessor(i);
/* 186 */       ImageProcessor dstIp = this.stack.getProcessor(i);
/* 187 */       dstIp.copyBits(srcIp, xloc, yloc, mode);
/* 188 */       if (i % inc == 0) IJ.showProgress(i / this.nSlices);
/*     */     }
/* 190 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   void showStatus(String s, int n, int total) {
/* 194 */     IJ.showStatus(s + n + "/" + total);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.StackProcessor
 * JD-Core Version:    0.6.2
 */