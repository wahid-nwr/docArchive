/*     */ package ij;
/*     */ 
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.ColorModel;
/*     */ 
/*     */ public class ImageStack
/*     */ {
/*     */   static final int INITIAL_SIZE = 25;
/*     */   static final String outOfRange = "Argument out of range: ";
/*     */   private static final int BYTE = 0;
/*     */   private static final int SHORT = 1;
/*     */   private static final int FLOAT = 2;
/*     */   private static final int RGB = 3;
/*     */   private static final int UNKNOWN = -1;
/*  15 */   private int type = -1;
/*  16 */   private int nSlices = 0;
/*     */   private Object[] stack;
/*     */   private String[] label;
/*     */   private int width;
/*     */   private int height;
/*     */   private Rectangle roi;
/*     */   private ColorModel cm;
/*  22 */   private double min = 1.7976931348623157E+308D;
/*     */   private double max;
/*     */   private float[] cTable;
/*     */ 
/*     */   public ImageStack()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ImageStack(int width, int height)
/*     */   {
/*  31 */     this(width, height, null);
/*     */   }
/*     */ 
/*     */   public ImageStack(int width, int height, int size)
/*     */   {
/*  37 */     this.width = width;
/*  38 */     this.height = height;
/*  39 */     this.stack = new Object[size];
/*  40 */     this.label = new String[size];
/*  41 */     this.nSlices = size;
/*     */   }
/*     */ 
/*     */   public ImageStack(int width, int height, ColorModel cm)
/*     */   {
/*  46 */     this.width = width;
/*  47 */     this.height = height;
/*  48 */     this.cm = cm;
/*  49 */     this.stack = new Object[25];
/*  50 */     this.label = new String[25];
/*  51 */     this.nSlices = 0;
/*     */   }
/*     */ 
/*     */   public void addSlice(String sliceLabel, Object pixels)
/*     */   {
/*  56 */     if (pixels == null)
/*  57 */       throw new IllegalArgumentException("'pixels' is null!");
/*  58 */     if (!pixels.getClass().isArray())
/*  59 */       throw new IllegalArgumentException("'pixels' is not an array");
/*  60 */     int size = this.stack.length;
/*  61 */     this.nSlices += 1;
/*  62 */     if (this.nSlices >= size) {
/*  63 */       Object[] tmp1 = new Object[size * 2];
/*  64 */       System.arraycopy(this.stack, 0, tmp1, 0, size);
/*  65 */       this.stack = tmp1;
/*  66 */       String[] tmp2 = new String[size * 2];
/*  67 */       System.arraycopy(this.label, 0, tmp2, 0, size);
/*  68 */       this.label = tmp2;
/*     */     }
/*  70 */     this.stack[(this.nSlices - 1)] = pixels;
/*  71 */     this.label[(this.nSlices - 1)] = sliceLabel;
/*  72 */     if (this.type == -1)
/*  73 */       if ((pixels instanceof byte[]))
/*  74 */         this.type = 0;
/*  75 */       else if ((pixels instanceof short[]))
/*  76 */         this.type = 1;
/*  77 */       else if ((pixels instanceof float[]))
/*  78 */         this.type = 2;
/*  79 */       else if ((pixels instanceof int[]))
/*  80 */         this.type = 3;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void addUnsignedShortSlice(String sliceLabel, Object pixels)
/*     */   {
/*  89 */     addSlice(sliceLabel, pixels);
/*     */   }
/*     */ 
/*     */   public void addSlice(String sliceLabel, ImageProcessor ip)
/*     */   {
/*  94 */     if ((ip.getWidth() != this.width) || (ip.getHeight() != this.height))
/*  95 */       throw new IllegalArgumentException("Dimensions do not match");
/*  96 */     if (this.nSlices == 0) {
/*  97 */       this.cm = ip.getColorModel();
/*  98 */       this.min = ip.getMin();
/*  99 */       this.max = ip.getMax();
/*     */     }
/* 101 */     addSlice(sliceLabel, ip.getPixels());
/*     */   }
/*     */ 
/*     */   public void addSlice(String sliceLabel, ImageProcessor ip, int n)
/*     */   {
/* 107 */     if ((n < 0) || (n > this.nSlices))
/* 108 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 109 */     addSlice(sliceLabel, ip);
/* 110 */     Object tempSlice = this.stack[(this.nSlices - 1)];
/* 111 */     String tempLabel = this.label[(this.nSlices - 1)];
/* 112 */     int first = n > 0 ? n : 1;
/* 113 */     for (int i = this.nSlices - 1; i >= first; i--) {
/* 114 */       this.stack[i] = this.stack[(i - 1)];
/* 115 */       this.label[i] = this.label[(i - 1)];
/*     */     }
/* 117 */     this.stack[n] = tempSlice;
/* 118 */     this.label[n] = tempLabel;
/*     */   }
/*     */ 
/*     */   public void deleteSlice(int n)
/*     */   {
/* 123 */     if ((n < 1) || (n > this.nSlices))
/* 124 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 125 */     if (this.nSlices < 1)
/* 126 */       return;
/* 127 */     for (int i = n; i < this.nSlices; i++) {
/* 128 */       this.stack[(i - 1)] = this.stack[i];
/* 129 */       this.label[(i - 1)] = this.label[i];
/*     */     }
/* 131 */     this.stack[(this.nSlices - 1)] = null;
/* 132 */     this.label[(this.nSlices - 1)] = null;
/* 133 */     this.nSlices -= 1;
/*     */   }
/*     */ 
/*     */   public void deleteLastSlice()
/*     */   {
/* 138 */     if (this.nSlices > 0)
/* 139 */       deleteSlice(this.nSlices);
/*     */   }
/*     */ 
/*     */   public int getWidth() {
/* 143 */     return this.width;
/*     */   }
/*     */ 
/*     */   public int getHeight() {
/* 147 */     return this.height;
/*     */   }
/*     */ 
/*     */   public void setRoi(Rectangle roi) {
/* 151 */     this.roi = roi;
/*     */   }
/*     */ 
/*     */   public Rectangle getRoi() {
/* 155 */     if (this.roi == null) {
/* 156 */       return new Rectangle(0, 0, this.width, this.height);
/*     */     }
/* 158 */     return this.roi;
/*     */   }
/*     */ 
/*     */   public void update(ImageProcessor ip)
/*     */   {
/* 164 */     if (ip != null) {
/* 165 */       this.min = ip.getMin();
/* 166 */       this.max = ip.getMax();
/* 167 */       this.cTable = ip.getCalibrationTable();
/* 168 */       this.cm = ip.getColorModel();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getPixels(int n)
/*     */   {
/* 174 */     if ((n < 1) || (n > this.nSlices))
/* 175 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 176 */     return this.stack[(n - 1)];
/*     */   }
/*     */ 
/*     */   public void setPixels(Object pixels, int n)
/*     */   {
/* 182 */     if ((n < 1) || (n > this.nSlices))
/* 183 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 184 */     this.stack[(n - 1)] = pixels;
/*     */   }
/*     */ 
/*     */   public Object[] getImageArray()
/*     */   {
/* 192 */     return this.stack;
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 197 */     return this.nSlices;
/*     */   }
/*     */ 
/*     */   public String[] getSliceLabels()
/*     */   {
/* 205 */     if (this.nSlices == 0) {
/* 206 */       return null;
/*     */     }
/* 208 */     return this.label;
/*     */   }
/*     */ 
/*     */   public String getSliceLabel(int n)
/*     */   {
/* 215 */     if ((n < 1) || (n > this.nSlices))
/* 216 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 217 */     return this.label[(n - 1)];
/*     */   }
/*     */ 
/*     */   public String getShortSliceLabel(int n)
/*     */   {
/* 224 */     String shortLabel = getSliceLabel(n);
/* 225 */     if (shortLabel == null) return null;
/* 226 */     int newline = shortLabel.indexOf('\n');
/* 227 */     if (newline == 0) return null;
/* 228 */     if (newline > 0)
/* 229 */       shortLabel = shortLabel.substring(0, newline);
/* 230 */     int len = shortLabel.length();
/* 231 */     if ((len > 4) && (shortLabel.charAt(len - 4) == '.') && (!Character.isDigit(shortLabel.charAt(len - 1))))
/* 232 */       shortLabel = shortLabel.substring(0, len - 4);
/* 233 */     if (shortLabel.length() > 60)
/* 234 */       shortLabel = shortLabel.substring(0, 60);
/* 235 */     return shortLabel;
/*     */   }
/*     */ 
/*     */   public void setSliceLabel(String label, int n)
/*     */   {
/* 240 */     if ((n < 1) || (n > this.nSlices))
/* 241 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 242 */     this.label[(n - 1)] = label;
/*     */   }
/*     */ 
/*     */   public ImageProcessor getProcessor(int n)
/*     */   {
/* 250 */     if ((n < 1) || (n > this.nSlices))
/* 251 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 252 */     if (this.nSlices == 0)
/* 253 */       return null;
/* 254 */     if (this.stack[0] == null)
/* 255 */       throw new IllegalArgumentException("Pixel array is null");
/*     */     ImageProcessor ip;
/* 256 */     if ((this.stack[0] instanceof byte[])) {
/* 257 */       ip = new ByteProcessor(this.width, this.height, null, this.cm);
/*     */     }
/*     */     else
/*     */     {
/*     */       ImageProcessor ip;
/* 258 */       if ((this.stack[0] instanceof short[])) {
/* 259 */         ip = new ShortProcessor(this.width, this.height, null, this.cm);
/*     */       }
/*     */       else
/*     */       {
/*     */         ImageProcessor ip;
/* 260 */         if ((this.stack[0] instanceof int[])) {
/* 261 */           ip = new ColorProcessor(this.width, this.height, null);
/*     */         }
/*     */         else
/*     */         {
/*     */           ImageProcessor ip;
/* 262 */           if ((this.stack[0] instanceof float[]))
/* 263 */             ip = new FloatProcessor(this.width, this.height, null, this.cm);
/*     */           else
/* 265 */             throw new IllegalArgumentException("Unknown stack type");
/*     */         }
/*     */       }
/*     */     }
/*     */     ImageProcessor ip;
/* 266 */     ip.setPixels(this.stack[(n - 1)]);
/* 267 */     if ((this.min != 1.7976931348623157E+308D) && (ip != null) && (!(ip instanceof ColorProcessor)))
/* 268 */       ip.setMinAndMax(this.min, this.max);
/* 269 */     if (this.cTable != null)
/* 270 */       ip.setCalibrationTable(this.cTable);
/* 271 */     return ip;
/*     */   }
/*     */ 
/*     */   public void setColorModel(ColorModel cm)
/*     */   {
/* 276 */     this.cm = cm;
/*     */   }
/*     */ 
/*     */   public ColorModel getColorModel()
/*     */   {
/* 281 */     return this.cm;
/*     */   }
/*     */ 
/*     */   public boolean isRGB()
/*     */   {
/* 286 */     if ((this.nSlices == 3) && ((this.stack[0] instanceof byte[])) && (getSliceLabel(1) != null) && (getSliceLabel(1).equals("Red"))) {
/* 287 */       return true;
/*     */     }
/* 289 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isHSB()
/*     */   {
/* 294 */     if ((this.nSlices == 3) && (getSliceLabel(1) != null) && (getSliceLabel(1).equals("Hue"))) {
/* 295 */       return true;
/*     */     }
/* 297 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isVirtual()
/*     */   {
/* 303 */     return false;
/*     */   }
/*     */ 
/*     */   public void trim()
/*     */   {
/* 308 */     int n = (int)Math.round(Math.log(this.nSlices) + 1.0D);
/* 309 */     for (int i = 0; i < n; i++) {
/* 310 */       deleteLastSlice();
/* 311 */       System.gc();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 316 */     String v = isVirtual() ? "(V)" : "";
/* 317 */     return "stack[" + getWidth() + "x" + getHeight() + "x" + getSize() + v + "]";
/*     */   }
/*     */ 
/*     */   public final double getVoxel(int x, int y, int z)
/*     */   {
/* 322 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height) && (z >= 0) && (z < this.nSlices)) {
/* 323 */       switch (this.type) {
/*     */       case 0:
/* 325 */         byte[] bytes = (byte[])this.stack[z];
/* 326 */         return bytes[(y * this.width + x)] & 0xFF;
/*     */       case 1:
/* 328 */         short[] shorts = (short[])this.stack[z];
/* 329 */         return shorts[(y * this.width + x)] & 0xFFFF;
/*     */       case 2:
/* 331 */         float[] floats = (float[])this.stack[z];
/* 332 */         return floats[(y * this.width + x)];
/*     */       case 3:
/* 334 */         int[] ints = (int[])this.stack[z];
/* 335 */         return ints[(y * this.width + x)] & 0xFFFF;
/* 336 */       }return 0.0D;
/*     */     }
/*     */ 
/* 339 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public final void setVoxel(int x, int y, int z, double value)
/*     */   {
/* 344 */     if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height) && (z >= 0) && (z < this.nSlices))
/* 345 */       switch (this.type) {
/*     */       case 0:
/* 347 */         byte[] bytes = (byte[])this.stack[z];
/* 348 */         if (value > 255.0D)
/* 349 */           value = 255.0D;
/* 350 */         else if (value < 0.0D)
/* 351 */           value = 0.0D;
/* 352 */         bytes[(y * this.width + x)] = ((byte)(int)(value + 0.5D));
/* 353 */         break;
/*     */       case 1:
/* 355 */         short[] shorts = (short[])this.stack[z];
/* 356 */         if (value > 65535.0D)
/* 357 */           value = 65535.0D;
/* 358 */         else if (value < 0.0D)
/* 359 */           value = 0.0D;
/* 360 */         shorts[(y * this.width + x)] = ((short)(int)(value + 0.5D));
/* 361 */         break;
/*     */       case 2:
/* 363 */         float[] floats = (float[])this.stack[z];
/* 364 */         floats[(y * this.width + x)] = ((float)value);
/* 365 */         break;
/*     */       case 3:
/* 367 */         int[] ints = (int[])this.stack[z];
/* 368 */         ints[(y * this.width + x)] = ((int)value);
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.ImageStack
 * JD-Core Version:    0.6.2
 */