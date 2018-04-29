/*     */ package ij.process;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.LookUpTable;
/*     */ import ij.Prefs;
/*     */ import ij.measure.Calibration;
/*     */ import java.awt.image.ColorModel;
/*     */ 
/*     */ public class ImageConverter
/*     */ {
/*     */   private ImagePlus imp;
/*     */   private int type;
/*  14 */   private static boolean doScaling = true;
/*     */ 
/*     */   public ImageConverter(ImagePlus imp)
/*     */   {
/*  18 */     this.imp = imp;
/*  19 */     this.type = imp.getType();
/*     */   }
/*     */ 
/*     */   public synchronized void convertToGray8()
/*     */   {
/*  24 */     if (this.imp.getStackSize() > 1)
/*  25 */       throw new IllegalArgumentException("Unsupported conversion");
/*  26 */     ImageProcessor ip = this.imp.getProcessor();
/*  27 */     if ((this.type == 1) || (this.type == 2)) {
/*  28 */       this.imp.setProcessor(null, ip.convertToByte(doScaling));
/*  29 */       this.imp.setCalibration(this.imp.getCalibration());
/*  30 */     } else if (this.type == 4) {
/*  31 */       this.imp.setProcessor(null, ip.convertToByte(doScaling));
/*  32 */     } else if (ip.isPseudoColorLut()) {
/*  33 */       boolean invertedLut = ip.isInvertedLut();
/*  34 */       ip.setColorModel(LookUpTable.createGrayscaleColorModel(invertedLut));
/*  35 */       this.imp.updateAndDraw();
/*     */     } else {
/*  37 */       ip = new ColorProcessor(this.imp.getImage());
/*  38 */       this.imp.setProcessor(null, ip.convertToByte(doScaling));
/*     */     }
/*  40 */     ImageProcessor ip2 = this.imp.getProcessor();
/*  41 */     if ((Prefs.useInvertingLut) && ((ip2 instanceof ByteProcessor)) && (!ip2.isInvertedLut()) && (!ip2.isColorLut())) {
/*  42 */       ip2.invertLut();
/*  43 */       ip2.invert();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void convertToGray16()
/*     */   {
/*  49 */     if (this.type == 1)
/*  50 */       return;
/*  51 */     if ((this.type != 0) && (this.type != 2) && (this.type != 4))
/*  52 */       throw new IllegalArgumentException("Unsupported conversion");
/*  53 */     ImageProcessor ip = this.imp.getProcessor();
/*  54 */     this.imp.trimProcessor();
/*  55 */     this.imp.setProcessor(null, ip.convertToShort(doScaling));
/*  56 */     this.imp.setCalibration(this.imp.getCalibration());
/*     */   }
/*     */ 
/*     */   public void convertToGray32()
/*     */   {
/*  61 */     if (this.type == 2)
/*  62 */       return;
/*  63 */     if ((this.type != 0) && (this.type != 1) && (this.type != 4))
/*  64 */       throw new IllegalArgumentException("Unsupported conversion");
/*  65 */     ImageProcessor ip = this.imp.getProcessor();
/*  66 */     this.imp.trimProcessor();
/*  67 */     Calibration cal = this.imp.getCalibration();
/*  68 */     this.imp.setProcessor(null, ip.convertToFloat());
/*  69 */     this.imp.setCalibration(cal);
/*     */   }
/*     */ 
/*     */   public void convertToRGB()
/*     */   {
/*  74 */     ImageProcessor ip = this.imp.getProcessor();
/*  75 */     this.imp.setProcessor(null, ip.convertToRGB());
/*  76 */     this.imp.setCalibration(this.imp.getCalibration());
/*     */   }
/*     */ 
/*     */   public void convertToRGBStack()
/*     */   {
/*  81 */     if (this.type != 4)
/*  82 */       throw new IllegalArgumentException("Image must be RGB");
/*     */     ColorProcessor cp;
/*     */     ColorProcessor cp;
/*  86 */     if (this.imp.getType() == 4)
/*  87 */       cp = (ColorProcessor)this.imp.getProcessor();
/*     */     else
/*  89 */       cp = new ColorProcessor(this.imp.getImage());
/*  90 */     int width = this.imp.getWidth();
/*  91 */     int height = this.imp.getHeight();
/*  92 */     byte[] R = new byte[width * height];
/*  93 */     byte[] G = new byte[width * height];
/*  94 */     byte[] B = new byte[width * height];
/*  95 */     cp.getRGB(R, G, B);
/*  96 */     this.imp.trimProcessor();
/*     */ 
/*  99 */     ColorModel cm = LookUpTable.createGrayscaleColorModel(false);
/* 100 */     ImageStack stack = new ImageStack(width, height, cm);
/* 101 */     stack.addSlice("Red", R);
/* 102 */     stack.addSlice("Green", G);
/* 103 */     stack.addSlice("Blue", B);
/* 104 */     this.imp.setStack(null, stack);
/* 105 */     this.imp.setDimensions(3, 1, 1);
/* 106 */     if (this.imp.isComposite())
/* 107 */       ((CompositeImage)this.imp).setMode(3);
/*     */   }
/*     */ 
/*     */   public void convertToHSB()
/*     */   {
/* 112 */     if (this.type != 4)
/* 113 */       throw new IllegalArgumentException("Image must be RGB");
/*     */     ColorProcessor cp;
/*     */     ColorProcessor cp;
/* 118 */     if (this.imp.getType() == 4)
/* 119 */       cp = (ColorProcessor)this.imp.getProcessor();
/*     */     else
/* 121 */       cp = new ColorProcessor(this.imp.getImage());
/* 122 */     ImageStack stack = cp.getHSBStack();
/* 123 */     this.imp.trimProcessor();
/* 124 */     this.imp.setStack(null, stack);
/* 125 */     this.imp.setDimensions(3, 1, 1);
/*     */   }
/*     */ 
/*     */   public void convertRGBStackToRGB()
/*     */   {
/* 131 */     int stackSize = this.imp.getStackSize();
/* 132 */     if ((stackSize < 2) || (stackSize > 3) || (this.type != 0))
/* 133 */       throw new IllegalArgumentException("2 or 3 slice 8-bit stack required");
/* 134 */     int width = this.imp.getWidth();
/* 135 */     int height = this.imp.getHeight();
/* 136 */     ImageStack stack = this.imp.getStack();
/* 137 */     byte[] R = (byte[])stack.getPixels(1);
/* 138 */     byte[] G = (byte[])stack.getPixels(2);
/*     */     byte[] B;
/*     */     byte[] B;
/* 140 */     if (stackSize > 2)
/* 141 */       B = (byte[])stack.getPixels(3);
/*     */     else
/* 143 */       B = new byte[width * height];
/* 144 */     this.imp.trimProcessor();
/* 145 */     ColorProcessor cp = new ColorProcessor(width, height);
/* 146 */     cp.setRGB(R, G, B);
/* 147 */     if (this.imp.isInvertedLut())
/* 148 */       cp.invert();
/* 149 */     this.imp.setImage(cp.createImage());
/* 150 */     this.imp.killStack();
/*     */   }
/*     */ 
/*     */   public void convertHSBToRGB()
/*     */   {
/* 155 */     if (this.imp.getStackSize() != 3)
/* 156 */       throw new IllegalArgumentException("3-slice 8-bit stack required");
/* 157 */     ImageStack stack = this.imp.getStack();
/* 158 */     byte[] H = (byte[])stack.getPixels(1);
/* 159 */     byte[] S = (byte[])stack.getPixels(2);
/* 160 */     byte[] B = (byte[])stack.getPixels(3);
/* 161 */     int width = this.imp.getWidth();
/* 162 */     int height = this.imp.getHeight();
/* 163 */     this.imp.trimProcessor();
/* 164 */     ColorProcessor cp = new ColorProcessor(width, height);
/* 165 */     cp.setHSB(H, S, B);
/* 166 */     this.imp.setImage(cp.createImage());
/* 167 */     this.imp.killStack();
/*     */   }
/*     */ 
/*     */   public void convertRGBtoIndexedColor(int nColors)
/*     */   {
/* 173 */     if (this.type != 4)
/* 174 */       throw new IllegalArgumentException("Image must be RGB");
/* 175 */     if (nColors < 2) nColors = 2;
/* 176 */     if (nColors > 256) nColors = 256;
/*     */ 
/* 179 */     IJ.showProgress(0.1D);
/* 180 */     IJ.showStatus("Grabbing pixels");
/* 181 */     int width = this.imp.getWidth();
/* 182 */     int height = this.imp.getHeight();
/* 183 */     ImageProcessor ip = this.imp.getProcessor();
/* 184 */     ip.snapshot();
/* 185 */     int[] pixels = (int[])ip.getPixels();
/* 186 */     this.imp.trimProcessor();
/*     */ 
/* 189 */     long start = System.currentTimeMillis();
/* 190 */     MedianCut mc = new MedianCut(pixels, width, height);
/* 191 */     ImageProcessor ip2 = mc.convertToByte(nColors);
/* 192 */     this.imp.setProcessor(null, ip2);
/*     */   }
/*     */ 
/*     */   public static void setDoScaling(boolean scaleConversions)
/*     */   {
/* 198 */     doScaling = scaleConversions;
/* 199 */     IJ.register(ImageConverter.class);
/*     */   }
/*     */ 
/*     */   public static boolean getDoScaling()
/*     */   {
/* 204 */     return doScaling;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ImageConverter
 * JD-Core Version:    0.6.2
 */