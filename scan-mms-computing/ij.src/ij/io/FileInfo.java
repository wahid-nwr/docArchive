/*     */ package ij.io;
/*     */ 
/*     */ import ij.VirtualStack;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class FileInfo
/*     */   implements Cloneable
/*     */ {
/*     */   public static final int GRAY8 = 0;
/*     */   public static final int GRAY16_SIGNED = 1;
/*     */   public static final int GRAY16_UNSIGNED = 2;
/*     */   public static final int GRAY32_INT = 3;
/*     */   public static final int GRAY32_FLOAT = 4;
/*     */   public static final int COLOR8 = 5;
/*     */   public static final int RGB = 6;
/*     */   public static final int RGB_PLANAR = 7;
/*     */   public static final int BITMAP = 8;
/*     */   public static final int ARGB = 9;
/*     */   public static final int BGR = 10;
/*     */   public static final int GRAY32_UNSIGNED = 11;
/*     */   public static final int RGB48 = 12;
/*     */   public static final int GRAY12_UNSIGNED = 13;
/*     */   public static final int GRAY24_UNSIGNED = 14;
/*     */   public static final int BARG = 15;
/*     */   public static final int GRAY64_FLOAT = 16;
/*     */   public static final int RGB48_PLANAR = 17;
/*     */   public static final int ABGR = 18;
/*     */   public static final int UNKNOWN = 0;
/*     */   public static final int RAW = 1;
/*     */   public static final int TIFF = 2;
/*     */   public static final int GIF_OR_JPG = 3;
/*     */   public static final int FITS = 4;
/*     */   public static final int BMP = 5;
/*     */   public static final int DICOM = 6;
/*     */   public static final int ZIP_ARCHIVE = 7;
/*     */   public static final int PGM = 8;
/*     */   public static final int IMAGEIO = 9;
/*     */   public static final int COMPRESSION_UNKNOWN = 0;
/*     */   public static final int COMPRESSION_NONE = 1;
/*     */   public static final int LZW = 2;
/*     */   public static final int LZW_WITH_DIFFERENCING = 3;
/*     */   public static final int JPEG = 4;
/*     */   public static final int PACK_BITS = 5;
/*     */   public static final int ZIP = 6;
/*     */   public int fileFormat;
/*     */   public int fileType;
/*     */   public String fileName;
/*     */   public String directory;
/*     */   public String url;
/*     */   public int width;
/*     */   public int height;
/* 101 */   public int offset = 0;
/*     */   public int nImages;
/*     */   public int gapBetweenImages;
/*     */   public boolean whiteIsZero;
/*     */   public boolean intelByteOrder;
/*     */   public int compression;
/*     */   public int[] stripOffsets;
/*     */   public int[] stripLengths;
/*     */   public int rowsPerStrip;
/*     */   public int lutSize;
/*     */   public byte[] reds;
/*     */   public byte[] greens;
/*     */   public byte[] blues;
/*     */   public Object pixels;
/*     */   public String debugInfo;
/*     */   public String[] sliceLabels;
/*     */   public String info;
/*     */   public InputStream inputStream;
/*     */   public VirtualStack virtualStack;
/* 121 */   public double pixelWidth = 1.0D;
/* 122 */   public double pixelHeight = 1.0D;
/* 123 */   public double pixelDepth = 1.0D;
/*     */   public String unit;
/*     */   public int calibrationFunction;
/*     */   public double[] coefficients;
/*     */   public String valueUnit;
/*     */   public double frameInterval;
/*     */   public String description;
/*     */   public long longOffset;
/*     */   public int[] metaDataTypes;
/*     */   public byte[][] metaData;
/*     */   public double[] displayRanges;
/*     */   public byte[][] channelLuts;
/*     */   public byte[] roi;
/*     */   public byte[][] overlay;
/*     */   public int samplesPerPixel;
/*     */   public String openNextDir;
/*     */   public String openNextName;
/*     */ 
/*     */   public FileInfo()
/*     */   {
/* 145 */     this.fileFormat = 0;
/* 146 */     this.fileType = 0;
/* 147 */     this.fileName = "Untitled";
/* 148 */     this.directory = "";
/* 149 */     this.url = "";
/* 150 */     this.nImages = 1;
/* 151 */     this.compression = 1;
/* 152 */     this.samplesPerPixel = 1;
/*     */   }
/*     */ 
/*     */   public final long getOffset()
/*     */   {
/* 157 */     return this.longOffset > 0L ? this.longOffset : this.offset & 0xFFFFFFFF;
/*     */   }
/*     */ 
/*     */   public int getBytesPerPixel()
/*     */   {
/* 162 */     switch (this.fileType) { case 0:
/*     */     case 5:
/*     */     case 8:
/* 163 */       return 1;
/*     */     case 1:
/*     */     case 2:
/* 164 */       return 2;
/*     */     case 3:
/*     */     case 4:
/*     */     case 9:
/*     */     case 11:
/*     */     case 14:
/*     */     case 15:
/*     */     case 18:
/* 165 */       return 4;
/*     */     case 6:
/*     */     case 7:
/*     */     case 10:
/* 166 */       return 3;
/*     */     case 12:
/*     */     case 17:
/* 167 */       return 6;
/*     */     case 16:
/* 168 */       return 8;
/* 169 */     case 13: } return 0;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 174 */     return "name=" + this.fileName + ", dir=" + this.directory + ", url=" + this.url + ", width=" + this.width + ", height=" + this.height + ", nImages=" + this.nImages + ", type=" + getType() + ", format=" + this.fileFormat + ", offset=" + getOffset() + ", whiteZero=" + (this.whiteIsZero ? "t" : "f") + ", Intel=" + (this.intelByteOrder ? "t" : "f") + ", lutSize=" + this.lutSize + ", comp=" + this.compression + ", ranges=" + (this.displayRanges != null ? "" + this.displayRanges.length / 2 : "null") + ", samples=" + this.samplesPerPixel;
/*     */   }
/*     */ 
/*     */   private String getType()
/*     */   {
/* 193 */     switch (this.fileType) { case 0:
/* 194 */       return "byte";
/*     */     case 1:
/* 195 */       return "short";
/*     */     case 2:
/* 196 */       return "ushort";
/*     */     case 3:
/* 197 */       return "int";
/*     */     case 11:
/* 198 */       return "uint";
/*     */     case 4:
/* 199 */       return "float";
/*     */     case 5:
/* 200 */       return "byte+lut";
/*     */     case 6:
/* 201 */       return "RGB";
/*     */     case 7:
/* 202 */       return "RGB(p)";
/*     */     case 12:
/* 203 */       return "RGB48";
/*     */     case 8:
/* 204 */       return "bitmap";
/*     */     case 9:
/* 205 */       return "ARGB";
/*     */     case 18:
/* 206 */       return "ABGR";
/*     */     case 10:
/* 207 */       return "BGR";
/*     */     case 15:
/* 208 */       return "BARG";
/*     */     case 16:
/* 209 */       return "double";
/*     */     case 17:
/* 210 */       return "RGB48(p)";
/*     */     case 13:
/* 211 */     case 14: } return "";
/*     */   }
/*     */ 
/*     */   public synchronized Object clone() {
/*     */     try {
/* 216 */       return super.clone(); } catch (CloneNotSupportedException e) {
/* 217 */     }return null;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.FileInfo
 * JD-Core Version:    0.6.2
 */