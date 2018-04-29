/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Menus;
/*     */ import ij.WindowManager;
/*     */ import ij.io.FileInfo;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.LUT;
/*     */ import java.awt.Color;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class LutLoader extends ImagePlus
/*     */   implements PlugIn
/*     */ {
/*  16 */   private static String defaultDirectory = null;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  22 */     FileInfo fi = new FileInfo();
/*  23 */     fi.reds = new byte[256];
/*  24 */     fi.greens = new byte[256];
/*  25 */     fi.blues = new byte[256];
/*  26 */     fi.lutSize = 256;
/*  27 */     int nColors = 0;
/*     */ 
/*  29 */     if (arg.equals("invert")) {
/*  30 */       invertLut(); return;
/*  31 */     }if (arg.equals("fire"))
/*  32 */       nColors = fire(fi.reds, fi.greens, fi.blues);
/*  33 */     else if (arg.equals("grays"))
/*  34 */       nColors = grays(fi.reds, fi.greens, fi.blues);
/*  35 */     else if (arg.equals("ice"))
/*  36 */       nColors = ice(fi.reds, fi.greens, fi.blues);
/*  37 */     else if (arg.equals("spectrum"))
/*  38 */       nColors = spectrum(fi.reds, fi.greens, fi.blues);
/*  39 */     else if (arg.equals("3-3-2 RGB"))
/*  40 */       nColors = rgb332(fi.reds, fi.greens, fi.blues);
/*  41 */     else if (arg.equals("red"))
/*  42 */       nColors = primaryColor(4, fi.reds, fi.greens, fi.blues);
/*  43 */     else if (arg.equals("green"))
/*  44 */       nColors = primaryColor(2, fi.reds, fi.greens, fi.blues);
/*  45 */     else if (arg.equals("blue"))
/*  46 */       nColors = primaryColor(1, fi.reds, fi.greens, fi.blues);
/*  47 */     else if (arg.equals("cyan"))
/*  48 */       nColors = primaryColor(3, fi.reds, fi.greens, fi.blues);
/*  49 */     else if (arg.equals("magenta"))
/*  50 */       nColors = primaryColor(5, fi.reds, fi.greens, fi.blues);
/*  51 */     else if (arg.equals("yellow"))
/*  52 */       nColors = primaryColor(6, fi.reds, fi.greens, fi.blues);
/*  53 */     else if (arg.equals("redgreen"))
/*  54 */       nColors = redGreen(fi.reds, fi.greens, fi.blues);
/*  55 */     if (nColors > 0) {
/*  56 */       if (nColors < 256)
/*  57 */         interpolate(fi.reds, fi.greens, fi.blues, nColors);
/*  58 */       fi.fileName = arg;
/*  59 */       showLut(fi, true);
/*  60 */       Menus.updateMenus();
/*  61 */       return;
/*     */     }
/*  63 */     OpenDialog od = new OpenDialog("Open LUT...", arg);
/*  64 */     fi.directory = od.getDirectory();
/*  65 */     fi.fileName = od.getFileName();
/*  66 */     if (fi.fileName == null)
/*  67 */       return;
/*  68 */     if (openLut(fi))
/*  69 */       showLut(fi, arg.equals(""));
/*  70 */     IJ.showStatus("");
/*     */   }
/*     */ 
/*     */   void showLut(FileInfo fi, boolean showImage) {
/*  74 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  75 */     if (imp != null) {
/*  76 */       if (imp.getType() == 4) {
/*  77 */         IJ.error("Color tables cannot be assiged to RGB Images.");
/*     */       } else {
/*  79 */         ImageProcessor ip = imp.getChannelProcessor();
/*  80 */         IndexColorModel cm = new IndexColorModel(8, 256, fi.reds, fi.greens, fi.blues);
/*  81 */         if (imp.isComposite())
/*  82 */           ((CompositeImage)imp).setChannelColorModel(cm);
/*     */         else
/*  84 */           ip.setColorModel(cm);
/*  85 */         if (imp.getStackSize() > 1)
/*  86 */           imp.getStack().setColorModel(cm);
/*  87 */         imp.updateAndRepaintWindow();
/*     */       }
/*     */     }
/*  90 */     else createImage(fi, showImage); 
/*     */   }
/*     */ 
/*     */   void invertLut()
/*     */   {
/*  94 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  95 */     if (imp == null) {
/*  96 */       IJ.noImage(); return;
/*  97 */     }if (imp.getType() == 4) {
/*  98 */       IJ.error("RGB images do not use LUTs"); return;
/*  99 */     }if (imp.isComposite()) {
/* 100 */       CompositeImage ci = (CompositeImage)imp;
/* 101 */       LUT lut = ci.getChannelLut();
/* 102 */       if (lut != null)
/* 103 */         ci.setChannelLut(lut.createInvertedLut());
/*     */     } else {
/* 105 */       ImageProcessor ip = imp.getProcessor();
/* 106 */       ip.invertLut();
/* 107 */       if (imp.getStackSize() > 1)
/* 108 */         imp.getStack().setColorModel(ip.getColorModel());
/*     */     }
/* 110 */     imp.updateAndRepaintWindow();
/*     */   }
/*     */ 
/*     */   int fire(byte[] reds, byte[] greens, byte[] blues) {
/* 114 */     int[] r = { 0, 0, 1, 25, 49, 73, 98, 122, 146, 162, 173, 184, 195, 207, 217, 229, 240, 252, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 };
/* 115 */     int[] g = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 35, 57, 79, 101, 117, 133, 147, 161, 175, 190, 205, 219, 234, 248, 255, 255, 255, 255 };
/* 116 */     int[] b = { 0, 61, 96, 130, 165, 192, 220, 227, 210, 181, 151, 122, 93, 64, 35, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 35, 98, 160, 223, 255 };
/* 117 */     for (int i = 0; i < r.length; i++) {
/* 118 */       reds[i] = ((byte)r[i]);
/* 119 */       greens[i] = ((byte)g[i]);
/* 120 */       blues[i] = ((byte)b[i]);
/*     */     }
/* 122 */     return r.length;
/*     */   }
/*     */ 
/*     */   int grays(byte[] reds, byte[] greens, byte[] blues) {
/* 126 */     for (int i = 0; i < 256; i++) {
/* 127 */       reds[i] = ((byte)i);
/* 128 */       greens[i] = ((byte)i);
/* 129 */       blues[i] = ((byte)i);
/*     */     }
/* 131 */     return 256;
/*     */   }
/*     */ 
/*     */   int primaryColor(int color, byte[] reds, byte[] greens, byte[] blues) {
/* 135 */     for (int i = 0; i < 256; i++) {
/* 136 */       if ((color & 0x4) != 0)
/* 137 */         reds[i] = ((byte)i);
/* 138 */       if ((color & 0x2) != 0)
/* 139 */         greens[i] = ((byte)i);
/* 140 */       if ((color & 0x1) != 0)
/* 141 */         blues[i] = ((byte)i);
/*     */     }
/* 143 */     return 256;
/*     */   }
/*     */ 
/*     */   int ice(byte[] reds, byte[] greens, byte[] blues) {
/* 147 */     int[] r = { 0, 0, 0, 0, 0, 0, 19, 29, 50, 48, 79, 112, 134, 158, 186, 201, 217, 229, 242, 250, 250, 250, 250, 251, 250, 250, 250, 250, 251, 251, 243, 230 };
/* 148 */     int[] g = { 156, 165, 176, 184, 190, 196, 193, 184, 171, 162, 146, 125, 107, 93, 81, 87, 92, 97, 95, 93, 93, 90, 85, 69, 64, 54, 47, 35, 19, 0, 4, 0 };
/* 149 */     int[] b = { 140, 147, 158, 166, 170, 176, 209, 220, 234, 225, 236, 246, 250, 251, 250, 250, 245, 230, 230, 222, 202, 180, 163, 142, 123, 114, 106, 94, 84, 64, 26, 27 };
/* 150 */     for (int i = 0; i < r.length; i++) {
/* 151 */       reds[i] = ((byte)r[i]);
/* 152 */       greens[i] = ((byte)g[i]);
/* 153 */       blues[i] = ((byte)b[i]);
/*     */     }
/* 155 */     return r.length;
/*     */   }
/*     */ 
/*     */   int spectrum(byte[] reds, byte[] greens, byte[] blues)
/*     */   {
/* 160 */     for (int i = 0; i < 256; i++) {
/* 161 */       Color c = Color.getHSBColor(i / 255.0F, 1.0F, 1.0F);
/* 162 */       reds[i] = ((byte)c.getRed());
/* 163 */       greens[i] = ((byte)c.getGreen());
/* 164 */       blues[i] = ((byte)c.getBlue());
/*     */     }
/* 166 */     return 256;
/*     */   }
/*     */ 
/*     */   int rgb332(byte[] reds, byte[] greens, byte[] blues)
/*     */   {
/* 171 */     for (int i = 0; i < 256; i++) {
/* 172 */       reds[i] = ((byte)(i & 0xE0));
/* 173 */       greens[i] = ((byte)(i << 3 & 0xE0));
/* 174 */       blues[i] = ((byte)(i << 6 & 0xC0));
/*     */     }
/* 176 */     return 256;
/*     */   }
/*     */ 
/*     */   int redGreen(byte[] reds, byte[] greens, byte[] blues) {
/* 180 */     for (int i = 0; i < 128; i++) {
/* 181 */       reds[i] = ((byte)(i * 2));
/* 182 */       greens[i] = 0;
/* 183 */       blues[i] = 0;
/*     */     }
/* 185 */     for (int i = 128; i < 256; i++) {
/* 186 */       reds[i] = 0;
/* 187 */       greens[i] = ((byte)(i * 2));
/* 188 */       blues[i] = 0;
/*     */     }
/* 190 */     return 256;
/*     */   }
/*     */ 
/*     */   void interpolate(byte[] reds, byte[] greens, byte[] blues, int nColors) {
/* 194 */     byte[] r = new byte[nColors];
/* 195 */     byte[] g = new byte[nColors];
/* 196 */     byte[] b = new byte[nColors];
/* 197 */     System.arraycopy(reds, 0, r, 0, nColors);
/* 198 */     System.arraycopy(greens, 0, g, 0, nColors);
/* 199 */     System.arraycopy(blues, 0, b, 0, nColors);
/* 200 */     double scale = nColors / 256.0D;
/*     */ 
/* 203 */     for (int i = 0; i < 256; i++) {
/* 204 */       int i1 = (int)(i * scale);
/* 205 */       int i2 = i1 + 1;
/* 206 */       if (i2 == nColors) i2 = nColors - 1;
/* 207 */       double fraction = i * scale - i1;
/*     */ 
/* 209 */       reds[i] = ((byte)(int)((1.0D - fraction) * (r[i1] & 0xFF) + fraction * (r[i2] & 0xFF)));
/* 210 */       greens[i] = ((byte)(int)((1.0D - fraction) * (g[i1] & 0xFF) + fraction * (g[i2] & 0xFF)));
/* 211 */       blues[i] = ((byte)(int)((1.0D - fraction) * (b[i1] & 0xFF) + fraction * (b[i2] & 0xFF)));
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean openLut(FileInfo fi)
/*     */   {
/* 218 */     boolean isURL = (fi.url != null) && (!fi.url.equals(""));
/* 219 */     int length = 0;
/* 220 */     if (!isURL) {
/* 221 */       File f = new File(fi.directory + fi.fileName);
/* 222 */       length = (int)f.length();
/* 223 */       if (length > 10000) {
/* 224 */         error();
/* 225 */         return false;
/*     */       }
/*     */     }
/* 228 */     int size = 0;
/*     */     try {
/* 230 */       if (length > 768)
/* 231 */         size = openBinaryLut(fi, isURL, false);
/* 232 */       if ((size == 0) && ((length == 0) || (length == 768) || (length == 970)))
/* 233 */         size = openBinaryLut(fi, isURL, true);
/* 234 */       if ((size == 0) && (length > 768))
/* 235 */         size = openTextLut(fi);
/* 236 */       if (size == 0)
/* 237 */         error();
/*     */     } catch (IOException e) {
/* 239 */       IJ.error(e.getMessage());
/*     */     }
/* 241 */     return size == 256;
/*     */   }
/*     */ 
/*     */   void error() {
/* 245 */     IJ.error("This is not an ImageJ or NIH Image LUT, a 768 byte \nraw LUT, or a LUT in text format.");
/*     */   }
/*     */ 
/*     */   int openBinaryLut(FileInfo fi, boolean isURL, boolean raw)
/*     */     throws IOException
/*     */   {
/*     */     InputStream is;
/*     */     InputStream is;
/* 251 */     if (isURL)
/* 252 */       is = new URL(fi.url + fi.fileName).openStream();
/*     */     else
/* 254 */       is = new FileInputStream(fi.directory + fi.fileName);
/* 255 */     DataInputStream f = new DataInputStream(is);
/* 256 */     int nColors = 256;
/*     */     int filler;
/* 257 */     if (!raw)
/*     */     {
/* 259 */       int id = f.readInt();
/* 260 */       if (id != 1229147980) {
/* 261 */         f.close();
/* 262 */         return 0;
/*     */       }
/* 264 */       int version = f.readShort();
/* 265 */       nColors = f.readShort();
/* 266 */       int start = f.readShort();
/* 267 */       int end = f.readShort();
/* 268 */       long fill1 = f.readLong();
/* 269 */       long fill2 = f.readLong();
/* 270 */       filler = f.readInt();
/*     */     }
/*     */ 
/* 273 */     f.read(fi.reds, 0, nColors);
/* 274 */     f.read(fi.greens, 0, nColors);
/* 275 */     f.read(fi.blues, 0, nColors);
/* 276 */     if (nColors < 256)
/* 277 */       interpolate(fi.reds, fi.greens, fi.blues, nColors);
/* 278 */     f.close();
/* 279 */     return 256;
/*     */   }
/*     */ 
/*     */   int openTextLut(FileInfo fi) throws IOException {
/* 283 */     TextReader tr = new TextReader();
/* 284 */     tr.hideErrorMessages();
/* 285 */     ImageProcessor ip = tr.open(fi.directory + fi.fileName);
/* 286 */     if (ip == null)
/* 287 */       return 0;
/* 288 */     int width = ip.getWidth();
/* 289 */     int height = ip.getHeight();
/* 290 */     if ((width < 3) || (width > 4) || (height < 256) || (height > 258))
/* 291 */       return 0;
/* 292 */     int x = width == 4 ? 1 : 0;
/* 293 */     int y = height > 256 ? 1 : 0;
/* 294 */     ip.setRoi(x, y, 3, 256);
/* 295 */     ip = ip.crop();
/* 296 */     for (int i = 0; i < 256; i++) {
/* 297 */       fi.reds[i] = ((byte)(int)ip.getPixelValue(0, i));
/* 298 */       fi.greens[i] = ((byte)(int)ip.getPixelValue(1, i));
/* 299 */       fi.blues[i] = ((byte)(int)ip.getPixelValue(2, i));
/*     */     }
/* 301 */     return 256;
/*     */   }
/*     */ 
/*     */   void createImage(FileInfo fi, boolean show) {
/* 305 */     IndexColorModel cm = new IndexColorModel(8, 256, fi.reds, fi.greens, fi.blues);
/* 306 */     ByteProcessor bp = createImage(cm);
/* 307 */     setProcessor(fi.fileName, bp);
/* 308 */     if (show) show();
/*     */   }
/*     */ 
/*     */   public static IndexColorModel open(String path)
/*     */     throws IOException
/*     */   {
/* 314 */     return open(new FileInputStream(path));
/*     */   }
/*     */ 
/*     */   public static IndexColorModel open(InputStream stream)
/*     */     throws IOException
/*     */   {
/* 320 */     DataInputStream f = new DataInputStream(stream);
/* 321 */     byte[] reds = new byte[256];
/* 322 */     byte[] greens = new byte[256];
/* 323 */     byte[] blues = new byte[256];
/* 324 */     f.read(reds, 0, 256);
/* 325 */     f.read(greens, 0, 256);
/* 326 */     f.read(blues, 0, 256);
/* 327 */     f.close();
/* 328 */     return new IndexColorModel(8, 256, reds, greens, blues);
/*     */   }
/*     */ 
/*     */   public static ByteProcessor createImage(IndexColorModel cm)
/*     */   {
/* 333 */     int width = 256;
/* 334 */     int height = 32;
/* 335 */     byte[] pixels = new byte[width * height];
/* 336 */     ByteProcessor bp = new ByteProcessor(width, height, pixels, cm);
/* 337 */     int[] ramp = new int[width];
/* 338 */     for (int i = 0; i < width; i++)
/* 339 */       ramp[i] = i;
/* 340 */     for (int y = 0; y < height; y++)
/* 341 */       bp.putRow(0, y, ramp, width);
/* 342 */     return bp;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.LutLoader
 * JD-Core Version:    0.6.2
 */