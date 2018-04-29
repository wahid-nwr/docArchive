/*     */ package ij.process;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.awt.image.ColorModel;
/*     */ 
/*     */ public class TypeConverter
/*     */ {
/*     */   private static final int BYTE = 0;
/*     */   private static final int SHORT = 1;
/*     */   private static final int FLOAT = 2;
/*     */   private static final int RGB = 3;
/*     */   private ImageProcessor ip;
/*     */   private int type;
/*  14 */   boolean doScaling = true;
/*     */   int width;
/*     */   int height;
/*     */ 
/*     */   public TypeConverter(ImageProcessor ip, boolean doScaling)
/*     */   {
/*  18 */     this.ip = ip;
/*  19 */     this.doScaling = doScaling;
/*  20 */     if ((ip instanceof ByteProcessor))
/*  21 */       this.type = 0;
/*  22 */     else if ((ip instanceof ShortProcessor))
/*  23 */       this.type = 1;
/*  24 */     else if ((ip instanceof FloatProcessor))
/*  25 */       this.type = 2;
/*     */     else
/*  27 */       this.type = 3;
/*  28 */     this.width = ip.getWidth();
/*  29 */     this.height = ip.getHeight();
/*     */   }
/*     */ 
/*     */   public ImageProcessor convertToByte()
/*     */   {
/*  34 */     switch (this.type) {
/*     */     case 0:
/*  36 */       return this.ip;
/*     */     case 1:
/*  38 */       return convertShortToByte();
/*     */     case 2:
/*  40 */       return convertFloatToByte();
/*     */     case 3:
/*  42 */       return convertRGBToByte();
/*     */     }
/*  44 */     return null;
/*     */   }
/*     */ 
/*     */   ByteProcessor convertShortToByte()
/*     */   {
/*  50 */     int size = this.width * this.height;
/*  51 */     short[] pixels16 = (short[])this.ip.getPixels();
/*  52 */     byte[] pixels8 = new byte[size];
/*  53 */     if (this.doScaling) {
/*  54 */       int min = (int)this.ip.getMin(); int max = (int)this.ip.getMax();
/*  55 */       double scale = 256.0D / (max - min + 1);
/*  56 */       for (int i = 0; i < size; i++) {
/*  57 */         int value = (pixels16[i] & 0xFFFF) - min;
/*  58 */         if (value < 0) value = 0;
/*  59 */         value = (int)(value * scale + 0.5D);
/*  60 */         if (value > 255) value = 255;
/*  61 */         pixels8[i] = ((byte)value);
/*     */       }
/*  63 */       return new ByteProcessor(this.width, this.height, pixels8, this.ip.getCurrentColorModel());
/*     */     }
/*     */ 
/*  66 */     for (int i = 0; i < size; i++) {
/*  67 */       int value = pixels16[i] & 0xFFFF;
/*  68 */       if (value > 255) value = 255;
/*  69 */       pixels8[i] = ((byte)value);
/*     */     }
/*  71 */     return new ByteProcessor(this.width, this.height, pixels8, this.ip.getColorModel());
/*     */   }
/*     */ 
/*     */   ByteProcessor convertFloatToByte()
/*     */   {
/*  77 */     if (this.doScaling) {
/*  78 */       Image img = this.ip.createImage();
/*  79 */       return new ByteProcessor(img);
/*     */     }
/*  81 */     ByteProcessor bp = new ByteProcessor(this.width, this.height);
/*  82 */     bp.setPixels(0, (FloatProcessor)this.ip);
/*  83 */     bp.setColorModel(this.ip.getColorModel());
/*  84 */     bp.resetMinAndMax();
/*  85 */     return bp;
/*     */   }
/*     */ 
/*     */   ByteProcessor convertRGBToByte()
/*     */   {
/* 100 */     int[] pixels32 = (int[])this.ip.getPixels();
/*     */ 
/* 103 */     double[] w = ColorProcessor.getWeightingFactors();
/* 104 */     double rw = w[0]; double gw = w[1]; double bw = w[2];
/* 105 */     byte[] pixels8 = new byte[this.width * this.height];
/* 106 */     for (int i = 0; i < this.width * this.height; i++) {
/* 107 */       int c = pixels32[i];
/* 108 */       int r = (c & 0xFF0000) >> 16;
/* 109 */       int g = (c & 0xFF00) >> 8;
/* 110 */       int b = c & 0xFF;
/* 111 */       pixels8[i] = ((byte)(int)(r * rw + g * gw + b * bw + 0.5D));
/*     */     }
/*     */ 
/* 114 */     return new ByteProcessor(this.width, this.height, pixels8, null);
/*     */   }
/*     */ 
/*     */   public ImageProcessor convertToShort()
/*     */   {
/* 119 */     switch (this.type) {
/*     */     case 0:
/* 121 */       return convertByteToShort();
/*     */     case 1:
/* 123 */       return this.ip;
/*     */     case 2:
/* 125 */       return convertFloatToShort();
/*     */     case 3:
/* 127 */       this.ip = convertRGBToByte();
/* 128 */       return convertByteToShort();
/*     */     }
/* 130 */     return null;
/*     */   }
/*     */ 
/*     */   ShortProcessor convertByteToShort()
/*     */   {
/* 136 */     if ((!this.ip.isDefaultLut()) && (!this.ip.isColorLut()) && (!this.ip.isInvertedLut()))
/*     */     {
/* 138 */       this.ip = convertToRGB();
/* 139 */       this.ip = convertRGBToByte();
/* 140 */       return convertByteToShort();
/*     */     }
/* 142 */     byte[] pixels8 = (byte[])this.ip.getPixels();
/* 143 */     short[] pixels16 = new short[this.width * this.height];
/* 144 */     int i = 0; for (int j = 0; i < this.width * this.height; i++)
/* 145 */       pixels16[i] = ((short)(pixels8[i] & 0xFF));
/* 146 */     return new ShortProcessor(this.width, this.height, pixels16, this.ip.getColorModel());
/*     */   }
/*     */ 
/*     */   ShortProcessor convertFloatToShort()
/*     */   {
/* 151 */     float[] pixels32 = (float[])this.ip.getPixels();
/* 152 */     short[] pixels16 = new short[this.width * this.height];
/* 153 */     double min = this.ip.getMin();
/* 154 */     double max = this.ip.getMax();
/*     */     double scale;
/*     */     double scale;
/* 156 */     if (max - min == 0.0D)
/* 157 */       scale = 1.0D;
/*     */     else {
/* 159 */       scale = 65535.0D / (max - min);
/*     */     }
/* 161 */     int i = 0; for (int j = 0; i < this.width * this.height; i++)
/*     */     {
/*     */       double value;
/*     */       double value;
/* 162 */       if (this.doScaling)
/* 163 */         value = (pixels32[i] - min) * scale;
/*     */       else
/* 165 */         value = pixels32[i];
/* 166 */       if (value < 0.0D) value = 0.0D;
/* 167 */       if (value > 65535.0D) value = 65535.0D;
/* 168 */       pixels16[i] = ((short)(int)(value + 0.5D));
/*     */     }
/* 170 */     return new ShortProcessor(this.width, this.height, pixels16, this.ip.getColorModel());
/*     */   }
/*     */ 
/*     */   public ImageProcessor convertToFloat(float[] ctable)
/*     */   {
/* 175 */     switch (this.type) {
/*     */     case 0:
/* 177 */       return convertByteToFloat(ctable);
/*     */     case 1:
/* 179 */       return convertShortToFloat(ctable);
/*     */     case 2:
/* 181 */       return this.ip;
/*     */     case 3:
/* 183 */       this.ip = convertRGBToByte();
/* 184 */       return convertByteToFloat(null);
/*     */     }
/* 186 */     return null;
/*     */   }
/*     */ 
/*     */   FloatProcessor convertByteToFloat(float[] cTable)
/*     */   {
/* 195 */     if ((!this.ip.isDefaultLut()) && (!this.ip.isColorLut()) && (!this.ip.isInvertedLut()))
/*     */     {
/* 197 */       this.ip = convertToRGB();
/* 198 */       this.ip = convertRGBToByte();
/* 199 */       return convertByteToFloat(null);
/*     */     }
/* 201 */     byte[] pixels8 = (byte[])this.ip.getPixels();
/* 202 */     float[] pixels32 = new float[this.width * this.height];
/*     */ 
/* 204 */     if ((cTable != null) && (cTable.length == 256))
/* 205 */       for (int i = 0; i < this.width * this.height; i++)
/* 206 */         pixels32[i] = cTable[(pixels8[i] & 0xFF)];
/*     */     else {
/* 208 */       for (int i = 0; i < this.width * this.height; i++)
/* 209 */         pixels32[i] = (pixels8[i] & 0xFF);
/*     */     }
/* 211 */     ColorModel cm = this.ip.getColorModel();
/* 212 */     return new FloatProcessor(this.width, this.height, pixels32, cm);
/*     */   }
/*     */ 
/*     */   FloatProcessor convertShortToFloat(float[] cTable)
/*     */   {
/* 220 */     short[] pixels16 = (short[])this.ip.getPixels();
/* 221 */     float[] pixels32 = new float[this.width * this.height];
/*     */ 
/* 223 */     if ((cTable != null) && (cTable.length == 65536))
/* 224 */       for (int i = 0; i < this.width * this.height; i++)
/* 225 */         pixels32[i] = cTable[(pixels16[i] & 0xFFFF)];
/*     */     else
/* 227 */       for (int i = 0; i < this.width * this.height; i++)
/* 228 */         pixels32[i] = (pixels16[i] & 0xFFFF);
/* 229 */     ColorModel cm = this.ip.getColorModel();
/* 230 */     return new FloatProcessor(this.width, this.height, pixels32, cm);
/*     */   }
/*     */ 
/*     */   public ImageProcessor convertToRGB()
/*     */   {
/* 235 */     if (this.type == 3) {
/* 236 */       return this.ip;
/*     */     }
/* 238 */     ImageProcessor ip2 = this.ip.convertToByte(this.doScaling);
/* 239 */     return new ColorProcessor(ip2.createImage());
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.TypeConverter
 * JD-Core Version:    0.6.2
 */