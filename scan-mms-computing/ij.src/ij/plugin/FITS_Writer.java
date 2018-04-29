/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.io.SaveDialog;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class FITS_Writer
/*     */   implements PlugIn
/*     */ {
/*     */   public void run(String path)
/*     */   {
/*  20 */     ImagePlus imp = IJ.getImage();
/*  21 */     ImageProcessor ip = imp.getProcessor();
/*  22 */     int numImages = imp.getImageStackSize();
/*  23 */     int bitDepth = imp.getBitDepth();
/*  24 */     if (bitDepth == 24) {
/*  25 */       IJ.error("RGB images are not supported");
/*  26 */       return;
/*     */     }
/*     */ 
/*  30 */     if ((path == null) || (path.trim().length() == 0)) {
/*  31 */       String title = "image.fits";
/*  32 */       SaveDialog sd = new SaveDialog("Write FITS image", title, ".fits");
/*  33 */       path = sd.getDirectory() + sd.getFileName();
/*     */     }
/*     */ 
/*  37 */     File f = new File(path);
/*  38 */     String directory = f.getParent() + File.separator;
/*  39 */     String name = f.getName();
/*  40 */     if (f.exists()) f.delete();
/*  41 */     int numBytes = 0;
/*     */ 
/*  44 */     if (bitDepth == 8)
/*  45 */       ip = ip.convertToShort(false);
/*  46 */     else if (imp.getCalibration().isSigned16Bit())
/*  47 */       ip = ip.convertToFloat();
/*  48 */     if ((ip instanceof ShortProcessor))
/*  49 */       numBytes = 2;
/*  50 */     else if ((ip instanceof FloatProcessor))
/*  51 */       numBytes = 4;
/*  52 */     int fillerLength = 2880 - numBytes * imp.getWidth() * imp.getHeight() % 2880;
/*     */ 
/*  55 */     String[] hdr = getHeader(imp);
/*  56 */     if (hdr == null)
/*  57 */       createHeader(path, ip, numBytes);
/*     */     else {
/*  59 */       copyHeader(hdr, path, ip, numBytes);
/*     */     }
/*     */ 
/*  62 */     writeData(path, ip);
/*  63 */     char[] endFiller = new char[fillerLength];
/*  64 */     appendFile(endFiller, path);
/*     */   }
/*     */ 
/*     */   void createHeader(String path, ImageProcessor ip, int numBytes)
/*     */   {
/*  71 */     int numCards = 7;
/*  72 */     String bitperpix = "";
/*  73 */     if (numBytes == 2) bitperpix = "                  16";
/*  74 */     else if (numBytes == 4) bitperpix = "                 -32";
/*  75 */     else if (numBytes == 1) bitperpix = "                   8";
/*  76 */     appendFile(writeCard("SIMPLE", "                   T", "Created by ImageJ FITS_Writer"), path);
/*  77 */     appendFile(writeCard("BITPIX", bitperpix, "number of bits per data pixel"), path);
/*  78 */     appendFile(writeCard("NAXIS", "                   2", "number of data axes"), path);
/*  79 */     appendFile(writeCard("NAXIS1", "                " + ip.getWidth(), "length of data axis 1"), path);
/*  80 */     appendFile(writeCard("NAXIS2", "                " + ip.getHeight(), "length of data axis 2"), path);
/*  81 */     if (numBytes == 2)
/*  82 */       appendFile(writeCard("BZERO", "               32768", "data range offset"), path);
/*     */     else
/*  84 */       appendFile(writeCard("BZERO", "                   0", "data range offset"), path);
/*  85 */     appendFile(writeCard("BSCALE", "                   1", "default scaling factor"), path);
/*     */ 
/*  87 */     int fillerSize = 2880 - (numCards * 80 + 3) % 2880;
/*  88 */     char[] end = new char[3];
/*  89 */     end[0] = 'E'; end[1] = 'N'; end[2] = 'D';
/*  90 */     char[] filler = new char[fillerSize];
/*  91 */     for (int i = 0; i < fillerSize; i++)
/*  92 */       filler[i] = ' ';
/*  93 */     appendFile(end, path);
/*  94 */     appendFile(filler, path);
/*     */   }
/*     */ 
/*     */   char[] writeCard(String title, String value, String comment)
/*     */   {
/* 101 */     char[] card = new char[80];
/* 102 */     for (int i = 0; i < 80; i++)
/* 103 */       card[i] = ' ';
/* 104 */     s2ch(title, card, 0);
/* 105 */     card[8] = '=';
/* 106 */     s2ch(value, card, 10);
/* 107 */     card[31] = '/';
/* 108 */     card[32] = ' ';
/* 109 */     s2ch(comment, card, 33);
/* 110 */     return card;
/*     */   }
/*     */ 
/*     */   void s2ch(String str, char[] ch, int offset)
/*     */   {
/* 117 */     int j = 0;
/* 118 */     for (int i = offset; (i < 80) && (i < str.length() + offset); i++)
/* 119 */       ch[i] = str.charAt(j++);
/*     */   }
/*     */ 
/*     */   void appendFile(char[] line, String path)
/*     */   {
/*     */     try
/*     */     {
/* 127 */       FileWriter output = new FileWriter(path, true);
/* 128 */       output.write(line);
/* 129 */       output.close();
/*     */     }
/*     */     catch (IOException e) {
/* 132 */       IJ.showStatus("Error writing file!");
/* 133 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   void writeData(String path, ImageProcessor ip)
/*     */   {
/* 141 */     int w = ip.getWidth();
/* 142 */     int h = ip.getHeight();
/* 143 */     if ((ip instanceof ShortProcessor)) {
/* 144 */       short[] pixels = (short[])ip.getPixels();
/*     */       try {
/* 146 */         DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path, true)));
/* 147 */         for (int i = h - 1; i >= 0; i--)
/* 148 */           for (int j = i * w; j < w * (i + 1); j++)
/* 149 */             dos.writeShort(pixels[j] ^ 0x8000);
/* 150 */         dos.close();
/*     */       }
/*     */       catch (IOException e) {
/* 153 */         IJ.showStatus("Error writing file!");
/* 154 */         return;
/*     */       }
/* 156 */     } else if ((ip instanceof FloatProcessor)) {
/* 157 */       float[] pixels = (float[])ip.getPixels();
/*     */       try {
/* 159 */         DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path, true)));
/* 160 */         for (int i = h - 1; i >= 0; i--)
/* 161 */           for (int j = i * w; j < w * (i + 1); j++)
/* 162 */             dos.writeFloat(pixels[j]);
/* 163 */         dos.close();
/*     */       }
/*     */       catch (IOException e) {
/* 166 */         IJ.showStatus("Error writing file!");
/* 167 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String[] getHeader(ImagePlus img)
/*     */   {
/* 182 */     String content = null;
/*     */ 
/* 184 */     int depth = img.getStackSize();
/* 185 */     if (depth == 1) {
/* 186 */       Properties props = img.getProperties();
/* 187 */       if (props == null)
/* 188 */         return null;
/* 189 */       content = props.getProperty("Info");
/*     */     }
/* 191 */     else if (depth > 1) {
/* 192 */       int slice = img.getCurrentSlice();
/* 193 */       ImageStack stack = img.getStack();
/* 194 */       content = stack.getSliceLabel(slice);
/*     */     }
/* 196 */     if (content == null) {
/* 197 */       return null;
/*     */     }
/*     */ 
/* 201 */     String[] lines = content.split("\n");
/*     */ 
/* 205 */     int istart = 0;
/* 206 */     while ((istart < lines.length) && 
/* 207 */       (!lines[istart].startsWith("SIMPLE"))) {
/* 206 */       istart++;
/*     */     }
/*     */ 
/* 209 */     if (istart == lines.length) return null;
/*     */ 
/* 211 */     for (int iend = istart + 1; 
/* 212 */       iend < lines.length; iend++) {
/* 213 */       String s = lines[iend].trim();
/* 214 */       if ((s.equals("END")) || (s.startsWith("END "))) break;
/*     */     }
/* 216 */     if (iend >= lines.length) return null;
/*     */ 
/* 218 */     int l = iend - istart + 1;
/* 219 */     String header = "";
/* 220 */     for (int i = 0; i < l; i++)
/* 221 */       header = header + lines[(istart + i)] + "\n";
/* 222 */     return header.split("\n");
/*     */   }
/*     */ 
/*     */   char[] eighty(String s)
/*     */   {
/* 229 */     char[] c = new char[80];
/* 230 */     int l = s.length();
/* 231 */     for (int i = 0; (i < l) && (i < 80); i++)
/* 232 */       c[i] = s.charAt(i);
/* 233 */     if (l < 80) {
/* 234 */       for (; l < 80; l++) c[l] = ' ';
/*     */     }
/* 236 */     return c;
/*     */   }
/*     */ 
/*     */   void copyHeader(String[] hdr, String path, ImageProcessor ip, int numBytes)
/*     */   {
/* 243 */     int numCards = 7;
/* 244 */     String bitperpix = "";
/*     */ 
/* 247 */     if (numBytes == 2) bitperpix = "                  16";
/* 248 */     else if (numBytes == 4) bitperpix = "                 -32";
/* 249 */     else if (numBytes == 1) bitperpix = "                   8";
/* 250 */     appendFile(writeCard("SIMPLE", "                   T", "Created by ImageJ FITS_Writer"), path);
/* 251 */     appendFile(writeCard("BITPIX", bitperpix, "number of bits per data pixel"), path);
/* 252 */     appendFile(writeCard("NAXIS", "                   2", "number of data axes"), path);
/* 253 */     appendFile(writeCard("NAXIS1", "                " + ip.getWidth(), "length of data axis 1"), path);
/* 254 */     appendFile(writeCard("NAXIS2", "                " + ip.getHeight(), "length of data axis 2"), path);
/* 255 */     if (numBytes == 2)
/* 256 */       appendFile(writeCard("BZERO", "               32768", "data range offset"), path);
/*     */     else
/* 258 */       appendFile(writeCard("BZERO", "                   0", "data range offset"), path);
/* 259 */     appendFile(writeCard("BSCALE", "                   1", "default scaling factor"), path);
/*     */ 
/* 264 */     for (int i = 0; i < hdr.length; i++) {
/* 265 */       String s = hdr[i];
/* 266 */       char[] card = eighty(s);
/* 267 */       if ((!s.startsWith("SIMPLE")) && (!s.startsWith("BITPIX")) && (!s.startsWith("NAXIS")) && (!s.startsWith("BZERO")) && (!s.startsWith("BSCALE")) && (!s.startsWith("END")) && (s.trim().length() > 1))
/*     */       {
/* 274 */         appendFile(card, path);
/* 275 */         numCards++;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 280 */     int fillerSize = 2880 - (numCards * 80 + 3) % 2880;
/* 281 */     char[] end = new char[3];
/* 282 */     end[0] = 'E'; end[1] = 'N'; end[2] = 'D';
/* 283 */     char[] filler = new char[fillerSize];
/* 284 */     for (int i = 0; i < fillerSize; i++)
/* 285 */       filler[i] = ' ';
/* 286 */     appendFile(end, path);
/* 287 */     appendFile(filler, path);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.FITS_Writer
 * JD-Core Version:    0.6.2
 */