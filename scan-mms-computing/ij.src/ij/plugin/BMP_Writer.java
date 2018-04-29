/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.LookUpTable;
/*     */ import ij.WindowManager;
/*     */ import ij.io.SaveDialog;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Image;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.FileOutputStream;
/*     */ 
/*     */ public class BMP_Writer
/*     */   implements PlugIn
/*     */ {
/*     */   private static final int BITMAPFILEHEADER_SIZE = 14;
/*     */   private static final int BITMAPINFOHEADER_SIZE = 40;
/*  18 */   private byte[] bitmapFileHeader = new byte[14];
/*  19 */   private byte[] bfType = { 66, 77 };
/*  20 */   private int bfSize = 0;
/*  21 */   private int bfReserved1 = 0;
/*  22 */   private int bfReserved2 = 0;
/*  23 */   private int bfOffBits = 54;
/*     */ 
/*  25 */   private byte[] bitmapInfoHeader = new byte[40];
/*  26 */   private int biSize = 40;
/*  27 */   private int biWidth = 0;
/*  28 */   private int padWidth = 0;
/*  29 */   private int biHeight = 0;
/*  30 */   private int biPlanes = 1;
/*  31 */   private int biBitCount = 24;
/*  32 */   private int biCompression = 0;
/*  33 */   private int biSizeImage = 0;
/*  34 */   private int biXPelsPerMeter = 0;
/*  35 */   private int biYPelsPerMeter = 0;
/*  36 */   private int biClrUsed = 0;
/*  37 */   private int biClrImportant = 0;
/*     */   private int[] intBitmap;
/*     */   private byte[] byteBitmap;
/*     */   private FileOutputStream fo;
/*     */   private BufferedOutputStream bfo;
/*     */   ImagePlus imp;
/*     */ 
/*     */   public void run(String path)
/*     */   {
/*  47 */     IJ.showProgress(0.0D);
/*  48 */     this.imp = WindowManager.getCurrentImage();
/*  49 */     if (this.imp == null) {
/*  50 */       IJ.noImage(); return;
/*     */     }try {
/*  52 */       writeImage(this.imp, path);
/*     */     } catch (Exception e) {
/*  54 */       String msg = e.getMessage();
/*  55 */       if ((msg == null) || (msg.equals("")))
/*  56 */         msg = "" + e;
/*  57 */       IJ.error("BMP Writer", "An error occured writing the file.\n \n" + msg);
/*     */     }
/*  59 */     IJ.showProgress(1.0D);
/*  60 */     IJ.showStatus("");
/*     */   }
/*     */ 
/*     */   void writeImage(ImagePlus imp, String path) throws Exception {
/*  64 */     if (imp.getBitDepth() == 24) {
/*  65 */       this.biBitCount = 24;
/*     */     } else {
/*  67 */       this.biBitCount = 8;
/*  68 */       LookUpTable lut = imp.createLut();
/*  69 */       this.biClrUsed = lut.getMapSize();
/*  70 */       this.bfOffBits += this.biClrUsed * 4;
/*     */     }
/*  72 */     if ((path == null) || (path.equals(""))) {
/*  73 */       String prompt = "Save as " + this.biBitCount + " bit BMP";
/*  74 */       SaveDialog sd = new SaveDialog(prompt, imp.getTitle(), ".bmp");
/*  75 */       if (sd.getFileName() == null)
/*  76 */         return;
/*  77 */       path = sd.getDirectory() + sd.getFileName();
/*     */     }
/*  79 */     imp.startTiming();
/*  80 */     saveBitmap(path, imp.getImage(), imp.getWidth(), imp.getHeight());
/*     */   }
/*     */ 
/*     */   public void saveBitmap(String parFilename, Image parImage, int parWidth, int parHeight) throws Exception
/*     */   {
/*  85 */     this.fo = new FileOutputStream(parFilename);
/*  86 */     this.bfo = new BufferedOutputStream(this.fo);
/*  87 */     save(parImage, parWidth, parHeight);
/*  88 */     this.bfo.close();
/*  89 */     this.fo.close();
/*     */   }
/*     */ 
/*     */   private void save(Image parImage, int parWidth, int parHeight)
/*     */     throws Exception
/*     */   {
/* 101 */     convertImage(parImage, parWidth, parHeight);
/* 102 */     writeBitmapFileHeader();
/* 103 */     writeBitmapInfoHeader();
/* 104 */     if (this.biBitCount == 8)
/* 105 */       writeBitmapPalette();
/* 106 */     writeBitmap();
/*     */   }
/*     */ 
/*     */   private void writeBitmapPalette() throws Exception {
/* 110 */     LookUpTable lut = this.imp.createLut();
/* 111 */     byte[] g = lut.getGreens();
/* 112 */     byte[] r = lut.getReds();
/* 113 */     byte[] b = lut.getBlues();
/* 114 */     for (int i = 0; i < lut.getMapSize(); i++) {
/* 115 */       this.bfo.write(b[i]);
/* 116 */       this.bfo.write(g[i]);
/* 117 */       this.bfo.write(r[i]);
/* 118 */       this.bfo.write(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean convertImage(Image parImage, int parWidth, int parHeight)
/*     */   {
/* 129 */     if (this.biBitCount == 24)
/* 130 */       this.intBitmap = ((int[])this.imp.getProcessor().getPixels());
/*     */     else
/* 132 */       this.byteBitmap = ((byte[])this.imp.getProcessor().convertToByte(true).getPixels());
/* 133 */     this.biWidth = parWidth;
/* 134 */     this.biHeight = parHeight;
/*     */     int pad;
/*     */     int pad;
/* 135 */     if (this.biBitCount == 24)
/* 136 */       pad = 4 - this.biWidth * 3 % 4;
/*     */     else
/* 138 */       pad = 4 - this.biWidth % 4;
/* 139 */     if (pad == 4)
/* 140 */       pad = 0;
/* 141 */     this.padWidth = (this.biWidth * (this.biBitCount == 24 ? 3 : 1) + pad);
/* 142 */     return true;
/*     */   }
/*     */ 
/*     */   private void writeBitmap()
/*     */     throws Exception
/*     */   {
/* 156 */     byte[] rgb = new byte[3];
/*     */     int pad;
/*     */     int pad;
/* 157 */     if (this.biBitCount == 24)
/* 158 */       pad = 4 - this.biWidth * 3 % 4;
/*     */     else
/* 160 */       pad = 4 - this.biWidth % 4;
/* 161 */     if (pad == 4) {
/* 162 */       pad = 0;
/*     */     }
/* 164 */     int counter = 0;
/* 165 */     for (int row = this.biHeight; row > 0; row--) {
/* 166 */       if (row % 20 == 0)
/* 167 */         IJ.showProgress((this.biHeight - row) / this.biHeight);
/* 168 */       for (int col = 0; col < this.biWidth; col++) {
/* 169 */         if (this.biBitCount == 24) {
/* 170 */           int value = this.intBitmap[((row - 1) * this.biWidth + col)];
/* 171 */           rgb[0] = ((byte)(value & 0xFF));
/* 172 */           rgb[1] = ((byte)(value >> 8 & 0xFF));
/* 173 */           rgb[2] = ((byte)(value >> 16 & 0xFF));
/* 174 */           this.bfo.write(rgb);
/*     */         } else {
/* 176 */           this.bfo.write(this.byteBitmap[((row - 1) * this.biWidth + col)]);
/* 177 */         }counter++;
/*     */       }
/* 179 */       for (int i = 1; i <= pad; i++)
/* 180 */         this.bfo.write(0);
/* 181 */       counter += pad;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeBitmapFileHeader()
/*     */     throws Exception
/*     */   {
/* 192 */     this.fo.write(this.bfType);
/*     */ 
/* 194 */     this.bfSize = (this.bfOffBits + this.padWidth * this.biHeight);
/* 195 */     this.fo.write(intToDWord(this.bfSize));
/* 196 */     this.fo.write(intToWord(this.bfReserved1));
/* 197 */     this.fo.write(intToWord(this.bfReserved2));
/* 198 */     this.fo.write(intToDWord(this.bfOffBits));
/*     */   }
/*     */ 
/*     */   private void writeBitmapInfoHeader()
/*     */     throws Exception
/*     */   {
/* 209 */     this.fo.write(intToDWord(this.biSize));
/* 210 */     this.fo.write(intToDWord(this.biWidth));
/* 211 */     this.fo.write(intToDWord(this.biHeight));
/* 212 */     this.fo.write(intToWord(this.biPlanes));
/* 213 */     this.fo.write(intToWord(this.biBitCount));
/* 214 */     this.fo.write(intToDWord(this.biCompression));
/* 215 */     this.fo.write(intToDWord(this.biSizeImage));
/* 216 */     this.fo.write(intToDWord(this.biXPelsPerMeter));
/* 217 */     this.fo.write(intToDWord(this.biYPelsPerMeter));
/* 218 */     this.fo.write(intToDWord(this.biClrUsed));
/* 219 */     this.fo.write(intToDWord(this.biClrImportant));
/*     */   }
/*     */ 
/*     */   private byte[] intToWord(int parValue)
/*     */   {
/* 229 */     byte[] retValue = new byte[2];
/* 230 */     retValue[0] = ((byte)(parValue & 0xFF));
/* 231 */     retValue[1] = ((byte)(parValue >> 8 & 0xFF));
/* 232 */     return retValue;
/*     */   }
/*     */ 
/*     */   private byte[] intToDWord(int parValue)
/*     */   {
/* 242 */     byte[] retValue = new byte[4];
/* 243 */     retValue[0] = ((byte)(parValue & 0xFF));
/* 244 */     retValue[1] = ((byte)(parValue >> 8 & 0xFF));
/* 245 */     retValue[2] = ((byte)(parValue >> 16 & 0xFF));
/* 246 */     retValue[3] = ((byte)(parValue >> 24 & 0xFF));
/* 247 */     return retValue;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.BMP_Writer
 * JD-Core Version:    0.6.2
 */