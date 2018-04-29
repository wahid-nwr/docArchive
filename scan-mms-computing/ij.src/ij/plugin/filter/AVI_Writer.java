/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.FileInfo;
/*     */ import ij.io.SaveDialog;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.Animator;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import javax.imageio.ImageIO;
/*     */ 
/*     */ public class AVI_Writer
/*     */   implements PlugInFilter
/*     */ {
/*     */   public static final int NO_COMPRESSION = 0;
/*     */   public static final int JPEG_COMPRESSION = 1196444237;
/*     */   public static final int PNG_COMPRESSION = 543649392;
/*     */   private static final int FOURCC_00db = 1650733104;
/*     */   private static final int FOURCC_00dc = 1667510320;
/*  37 */   private static int compressionIndex = 2;
/*  38 */   private static int jpegQuality = 90;
/*  39 */   private static final String[] COMPRESSION_STRINGS = { "Uncompressed", "PNG", "JPEG" };
/*  40 */   private static final int[] COMPRESSION_TYPES = { 0, 543649392, 1196444237 };
/*     */   private ImagePlus imp;
/*     */   private RandomAccessFile raFile;
/*     */   private int xDim;
/*     */   private int yDim;
/*     */   private int zDim;
/*     */   private int bytesPerPixel;
/*     */   private int frameDataSize;
/*     */   private int biCompression;
/*     */   private int linePad;
/*     */   private byte[] bufferWrite;
/*     */   private BufferedImage bufferedImage;
/*     */   private RaOutputStream raOutputStream;
/*     */   private long[] sizePointers;
/*     */   private int stackPointer;
/*     */ 
/*     */   public AVI_Writer()
/*     */   {
/*  53 */     this.sizePointers = new long[5];
/*     */   }
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  58 */     this.imp = imp;
/*  59 */     return 159;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip)
/*     */   {
/*  64 */     if (!showDialog(this.imp)) return;
/*  65 */     SaveDialog sd = new SaveDialog("Save as AVI...", this.imp.getTitle(), ".avi");
/*  66 */     String fileName = sd.getFileName();
/*  67 */     if (fileName == null)
/*  68 */       return;
/*  69 */     String fileDir = sd.getDirectory();
/*  70 */     FileInfo fi = this.imp.getOriginalFileInfo();
/*  71 */     if ((this.imp.getStack().isVirtual()) && (fileDir.equals(fi.directory)) && (fileName.equals(fi.fileName))) {
/*  72 */       IJ.error("AVI Writer", "Virtual stacks cannot be saved in place.");
/*  73 */       return;
/*     */     }
/*     */     try {
/*  76 */       writeImage(this.imp, fileDir + fileName, COMPRESSION_TYPES[compressionIndex], jpegQuality);
/*  77 */       IJ.showStatus("");
/*     */     } catch (IOException e) {
/*  79 */       IJ.error("AVI Writer", "An error occured writing the file.\n \n" + e);
/*     */     }
/*  81 */     IJ.showStatus("");
/*     */   }
/*     */ 
/*     */   private boolean showDialog(ImagePlus imp) {
/*  85 */     String options = Macro.getOptions();
/*  86 */     if ((options != null) && (options.indexOf("compression=") == -1))
/*  87 */       Macro.setOptions("compression=Uncompressed " + options);
/*  88 */     double fps = getFrameRate(imp);
/*  89 */     int decimalPlaces = (int)fps == fps ? 0 : 1;
/*  90 */     GenericDialog gd = new GenericDialog("Save as AVI...");
/*  91 */     gd.addChoice("Compression:", COMPRESSION_STRINGS, COMPRESSION_STRINGS[compressionIndex]);
/*     */ 
/*  93 */     gd.addNumericField("Frame Rate:", fps, decimalPlaces, 3, "fps");
/*  94 */     gd.showDialog();
/*  95 */     if (gd.wasCanceled())
/*  96 */       return false;
/*  97 */     compressionIndex = gd.getNextChoiceIndex();
/*     */ 
/*  99 */     fps = gd.getNextNumber();
/* 100 */     if (fps <= 0.5D) fps = 0.5D;
/*     */ 
/* 102 */     imp.getCalibration().fps = fps;
/* 103 */     return true;
/*     */   }
/*     */ 
/*     */   public void writeImage(ImagePlus imp, String path, int compression, int jpegQuality)
/*     */     throws IOException
/*     */   {
/* 109 */     if ((compression != 0) && (compression != 1196444237) && (compression != 543649392))
/* 110 */       throw new IllegalArgumentException("Unsupported Compression 0x" + Integer.toHexString(compression));
/* 111 */     this.biCompression = compression;
/* 112 */     if (jpegQuality < 0) jpegQuality = 0;
/* 113 */     if (jpegQuality > 100) jpegQuality = 100;
/* 114 */     jpegQuality = jpegQuality;
/* 115 */     File file = new File(path);
/* 116 */     this.raFile = new RandomAccessFile(file, "rw");
/* 117 */     this.raFile.setLength(0L);
/* 118 */     imp.startTiming();
/*     */ 
/* 121 */     boolean isComposite = imp.isComposite();
/* 122 */     boolean isHyperstack = imp.isHyperStack();
/* 123 */     boolean isOverlay = (imp.getOverlay() != null) && (!imp.getHideOverlay());
/* 124 */     this.xDim = imp.getWidth();
/* 125 */     this.yDim = imp.getHeight();
/* 126 */     this.zDim = imp.getStackSize();
/* 127 */     boolean saveFrames = false; boolean saveSlices = false; boolean saveChannels = false;
/* 128 */     int channels = imp.getNChannels();
/* 129 */     int slices = imp.getNSlices();
/* 130 */     int frames = imp.getNFrames();
/* 131 */     int channel = imp.getChannel();
/* 132 */     int slice = imp.getSlice();
/* 133 */     int frame = imp.getFrame();
/* 134 */     if ((isHyperstack) || (isComposite)) {
/* 135 */       if (frames > 1) {
/* 136 */         saveFrames = true;
/* 137 */         this.zDim = frames;
/* 138 */       } else if (slices > 1) {
/* 139 */         saveSlices = true;
/* 140 */         this.zDim = slices;
/* 141 */       } else if (channels > 1) {
/* 142 */         saveChannels = true;
/* 143 */         this.zDim = channels;
/*     */       } else {
/* 145 */         isHyperstack = false;
/*     */       }
/*     */     }
/* 148 */     if ((imp.getType() == 4) || (isComposite) || (this.biCompression == 1196444237) || (isOverlay))
/* 149 */       this.bytesPerPixel = 3;
/*     */     else
/* 151 */       this.bytesPerPixel = 1;
/* 152 */     boolean writeLUT = this.bytesPerPixel == 1;
/* 153 */     this.linePad = 0;
/* 154 */     int minLineLength = this.bytesPerPixel * this.xDim;
/* 155 */     if ((this.biCompression == 0) && (minLineLength % 4 != 0))
/* 156 */       this.linePad = (4 - minLineLength % 4);
/* 157 */     this.frameDataSize = ((this.bytesPerPixel * this.xDim + this.linePad) * this.yDim);
/* 158 */     int microSecPerFrame = (int)Math.round(1.0D / getFrameRate(imp) * 1000000.0D);
/*     */ 
/* 161 */     writeString("RIFF");
/* 162 */     chunkSizeHere();
/* 163 */     writeString("AVI ");
/* 164 */     writeString("LIST");
/* 165 */     chunkSizeHere();
/* 166 */     writeString("hdrl");
/* 167 */     writeString("avih");
/* 168 */     writeInt(56);
/*     */ 
/* 170 */     writeInt(microSecPerFrame);
/* 171 */     writeInt(0);
/* 172 */     writeInt(0);
/* 173 */     writeInt(16);
/*     */ 
/* 177 */     writeInt(this.zDim);
/* 178 */     writeInt(0);
/*     */ 
/* 180 */     writeInt(1);
/* 181 */     writeInt(0);
/* 182 */     writeInt(this.xDim);
/* 183 */     writeInt(this.yDim);
/* 184 */     writeInt(0);
/* 185 */     writeInt(0);
/* 186 */     writeInt(0);
/* 187 */     writeInt(0);
/*     */ 
/* 190 */     writeString("LIST");
/* 191 */     chunkSizeHere();
/* 192 */     writeString("strl");
/* 193 */     writeString("strh");
/* 194 */     writeInt(56);
/* 195 */     writeString("vids");
/* 196 */     writeString("DIB ");
/* 197 */     writeInt(0);
/* 198 */     writeInt(0);
/* 199 */     writeInt(0);
/* 200 */     writeInt(1);
/* 201 */     writeInt((int)Math.round(getFrameRate(imp)));
/* 202 */     writeInt(0);
/* 203 */     writeInt(this.zDim);
/*     */ 
/* 205 */     writeInt(0);
/*     */ 
/* 208 */     writeInt(-1);
/*     */ 
/* 211 */     writeInt(0);
/* 212 */     writeShort(0);
/* 213 */     writeShort(0);
/* 214 */     writeShort(0);
/* 215 */     writeShort(0);
/*     */ 
/* 217 */     writeString("strf");
/* 218 */     chunkSizeHere();
/* 219 */     writeInt(40);
/*     */ 
/* 222 */     writeInt(this.xDim);
/* 223 */     writeInt(this.yDim);
/*     */ 
/* 225 */     writeShort(1);
/* 226 */     writeShort((short)(8 * this.bytesPerPixel));
/* 227 */     writeInt(this.biCompression);
/* 228 */     int biSizeImage = this.biCompression == 0 ? 0 : this.xDim * this.yDim * this.bytesPerPixel;
/*     */ 
/* 230 */     writeInt(biSizeImage);
/* 231 */     writeInt(0);
/* 232 */     writeInt(0);
/* 233 */     writeInt(writeLUT ? 256 : 0);
/* 234 */     writeInt(0);
/*     */ 
/* 239 */     if (writeLUT)
/* 240 */       writeLUT(imp.getProcessor());
/* 241 */     chunkEndWriteSize();
/*     */ 
/* 243 */     writeString("strn");
/* 244 */     writeInt(16);
/* 245 */     writeString("");
/* 246 */     chunkEndWriteSize();
/* 247 */     chunkEndWriteSize();
/* 248 */     writeString("JUNK");
/* 249 */     chunkSizeHere();
/* 250 */     this.raFile.seek(4096L);
/* 251 */     chunkEndWriteSize();
/*     */ 
/* 253 */     writeString("LIST");
/* 254 */     chunkSizeHere();
/* 255 */     long moviPointer = this.raFile.getFilePointer();
/* 256 */     writeString("movi");
/*     */ 
/* 259 */     if (this.biCompression == 0)
/* 260 */       this.bufferWrite = new byte[this.frameDataSize];
/*     */     else
/* 262 */       this.raOutputStream = new RaOutputStream(this.raFile);
/* 263 */     int dataSignature = this.biCompression == 0 ? 1650733104 : 1667510320;
/* 264 */     int maxChunkLength = 0;
/* 265 */     int[] dataChunkOffset = new int[this.zDim];
/* 266 */     int[] dataChunkLength = new int[this.zDim];
/*     */ 
/* 269 */     for (int z = 0; z < this.zDim; z++) {
/* 270 */       IJ.showProgress(z, this.zDim);
/* 271 */       IJ.showStatus(z + "/" + this.zDim);
/* 272 */       ImageProcessor ip = null;
/* 273 */       if ((isComposite) || (isHyperstack) || (isOverlay)) {
/* 274 */         if (saveFrames)
/* 275 */           imp.setPositionWithoutUpdate(channel, slice, z + 1);
/* 276 */         else if (saveSlices)
/* 277 */           imp.setPositionWithoutUpdate(channel, z + 1, frame);
/* 278 */         else if (saveChannels)
/* 279 */           imp.setPositionWithoutUpdate(z + 1, slice, frame);
/* 280 */         ImagePlus imp2 = imp;
/* 281 */         if (isOverlay) {
/* 282 */           if ((!saveFrames) && (!saveSlices) && (!saveChannels))
/* 283 */             imp.setSliceWithoutUpdate(z + 1);
/* 284 */           imp2 = imp.flatten();
/*     */         }
/* 286 */         ip = new ColorProcessor(imp2.getImage());
/*     */       } else {
/* 288 */         ip = this.zDim == 1 ? imp.getProcessor() : imp.getStack().getProcessor(z + 1);
/* 289 */       }int chunkPointer = (int)this.raFile.getFilePointer();
/* 290 */       writeInt(dataSignature);
/* 291 */       chunkSizeHere();
/* 292 */       if (this.biCompression == 0) {
/* 293 */         if (this.bytesPerPixel == 1)
/* 294 */           writeByteFrame(ip);
/*     */         else
/* 296 */           writeRGBFrame(ip);
/*     */       }
/* 298 */       else writeCompressedFrame(ip);
/* 299 */       dataChunkOffset[z] = ((int)(chunkPointer - moviPointer));
/* 300 */       dataChunkLength[z] = ((int)(this.raFile.getFilePointer() - chunkPointer - 8L));
/* 301 */       if (maxChunkLength < dataChunkLength[z]) maxChunkLength = dataChunkLength[z];
/* 302 */       chunkEndWriteSize();
/*     */     }
/*     */ 
/* 308 */     chunkEndWriteSize();
/* 309 */     if ((isComposite) || (isHyperstack)) {
/* 310 */       imp.setPosition(channel, slice, frame);
/*     */     }
/*     */ 
/* 313 */     writeString("idx1");
/* 314 */     chunkSizeHere();
/* 315 */     for (int z = 0; z < this.zDim; z++) {
/* 316 */       writeInt(dataSignature);
/* 317 */       writeInt(16);
/*     */ 
/* 326 */       writeInt(dataChunkOffset[z]);
/*     */ 
/* 328 */       writeInt(dataChunkLength[z]);
/*     */     }
/* 330 */     chunkEndWriteSize();
/* 331 */     chunkEndWriteSize();
/* 332 */     this.raFile.close();
/* 333 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   private void chunkSizeHere()
/*     */     throws IOException
/*     */   {
/* 341 */     this.sizePointers[this.stackPointer] = this.raFile.getFilePointer();
/* 342 */     writeInt(0);
/* 343 */     this.stackPointer += 1;
/*     */   }
/*     */ 
/*     */   private void chunkEndWriteSize()
/*     */     throws IOException
/*     */   {
/* 349 */     this.stackPointer -= 1;
/* 350 */     long position = this.raFile.getFilePointer();
/* 351 */     this.raFile.seek(this.sizePointers[this.stackPointer]);
/* 352 */     writeInt((int)(position - (this.sizePointers[this.stackPointer] + 4L)));
/* 353 */     this.raFile.seek((position + 1L) / 2L * 2L);
/*     */   }
/*     */ 
/*     */   private void writeByteFrame(ImageProcessor ip)
/*     */     throws IOException
/*     */   {
/* 360 */     ip = ip.convertToByte(true);
/* 361 */     byte[] pixels = (byte[])ip.getPixels();
/* 362 */     int width = ip.getWidth();
/* 363 */     int height = ip.getHeight();
/* 364 */     int index = 0;
/* 365 */     for (int y = height - 1; y >= 0; y--) {
/* 366 */       int offset = y * width;
/* 367 */       for (int x = 0; x < width; x++)
/* 368 */         this.bufferWrite[(index++)] = pixels[(offset++)];
/* 369 */       for (int i = 0; i < this.linePad; i++)
/* 370 */         this.bufferWrite[(index++)] = 0;
/*     */     }
/* 372 */     this.raFile.write(this.bufferWrite);
/*     */   }
/*     */ 
/*     */   private void writeRGBFrame(ImageProcessor ip)
/*     */     throws IOException
/*     */   {
/* 380 */     ip = ip.convertToRGB();
/* 381 */     int[] pixels = (int[])ip.getPixels();
/* 382 */     int width = ip.getWidth();
/* 383 */     int height = ip.getHeight();
/* 384 */     int index = 0;
/* 385 */     for (int y = height - 1; y >= 0; y--) {
/* 386 */       int offset = y * width;
/* 387 */       for (int x = 0; x < width; x++) {
/* 388 */         int c = pixels[(offset++)];
/* 389 */         this.bufferWrite[(index++)] = ((byte)(c & 0xFF));
/* 390 */         this.bufferWrite[(index++)] = ((byte)((c & 0xFF00) >> 8));
/* 391 */         this.bufferWrite[(index++)] = ((byte)((c & 0xFF0000) >> 16));
/*     */       }
/* 393 */       for (int i = 0; i < this.linePad; i++)
/* 394 */         this.bufferWrite[(index++)] = 0;
/*     */     }
/* 396 */     this.raFile.write(this.bufferWrite);
/*     */   }
/*     */ 
/*     */   private void writeCompressedFrame(ImageProcessor ip)
/*     */     throws IOException
/*     */   {
/* 402 */     if (this.biCompression == 1196444237) {
/* 403 */       BufferedImage bi = getBufferedImage(ip);
/* 404 */       ImageIO.write(bi, "jpeg", this.raOutputStream);
/*     */     } else {
/* 406 */       BufferedImage bi = ip.getBufferedImage();
/* 407 */       ImageIO.write(bi, "png", this.raOutputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   private BufferedImage getBufferedImage(ImageProcessor ip) {
/* 412 */     BufferedImage bi = new BufferedImage(ip.getWidth(), ip.getHeight(), 1);
/* 413 */     Graphics2D g = (Graphics2D)bi.getGraphics();
/* 414 */     g.drawImage(ip.createImage(), 0, 0, null);
/* 415 */     return bi;
/*     */   }
/*     */ 
/*     */   private void writeLUT(ImageProcessor ip)
/*     */     throws IOException
/*     */   {
/* 421 */     IndexColorModel cm = (IndexColorModel)ip.getCurrentColorModel();
/* 422 */     int mapSize = cm.getMapSize();
/* 423 */     byte[] lutWrite = new byte[1024];
/* 424 */     for (int i = 0; i < 256; i++) {
/* 425 */       if (i < mapSize) {
/* 426 */         lutWrite[(4 * i)] = ((byte)cm.getBlue(i));
/* 427 */         lutWrite[(4 * i + 1)] = ((byte)cm.getGreen(i));
/* 428 */         lutWrite[(4 * i + 2)] = ((byte)cm.getRed(i));
/* 429 */         lutWrite[(4 * i + 3)] = 0;
/*     */       }
/*     */     }
/* 432 */     this.raFile.write(lutWrite);
/*     */   }
/*     */ 
/*     */   private double getFrameRate(ImagePlus imp) {
/* 436 */     double rate = imp.getCalibration().fps;
/* 437 */     if (rate == 0.0D)
/* 438 */       rate = Animator.getFrameRate();
/* 439 */     if (rate <= 0.5D) rate = 0.5D;
/*     */ 
/* 441 */     return rate;
/*     */   }
/*     */ 
/*     */   private void writeString(String s) throws IOException {
/* 445 */     byte[] bytes = s.getBytes("UTF8");
/* 446 */     this.raFile.write(bytes);
/*     */   }
/*     */ 
/*     */   private void writeInt(int v)
/*     */     throws IOException
/*     */   {
/* 452 */     this.raFile.write(v & 0xFF);
/* 453 */     this.raFile.write(v >>> 8 & 0xFF);
/* 454 */     this.raFile.write(v >>> 16 & 0xFF);
/* 455 */     this.raFile.write(v >>> 24 & 0xFF);
/*     */   }
/*     */ 
/*     */   private void writeShort(int v)
/*     */     throws IOException
/*     */   {
/* 462 */     this.raFile.write(v & 0xFF);
/* 463 */     this.raFile.write(v >>> 8 & 0xFF);
/*     */   }
/*     */ 
/*     */   class RaOutputStream extends OutputStream {
/*     */     RandomAccessFile raFile;
/*     */ 
/*     */     RaOutputStream(RandomAccessFile raFile) {
/* 470 */       this.raFile = raFile;
/*     */     }
/*     */ 
/*     */     public void write(int b) throws IOException {
/* 474 */       this.raFile.writeByte(b);
/*     */     }
/*     */ 
/*     */     public void write(byte[] b) throws IOException {
/* 478 */       this.raFile.write(b);
/*     */     }
/*     */ 
/*     */     public void write(byte[] b, int off, int len) throws IOException {
/* 482 */       this.raFile.write(b, off, len);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.AVI_Writer
 * JD-Core Version:    0.6.2
 */