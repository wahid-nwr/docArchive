/*     */ package ij.io;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.FileInfoVirtualStack;
/*     */ import ij.plugin.FolderOpener;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.io.File;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class ImportDialog
/*     */ {
/*     */   private String fileName;
/*     */   private String directory;
/*     */   static final String TYPE = "raw.type";
/*     */   static final String WIDTH = "raw.width";
/*     */   static final String HEIGHT = "raw.height";
/*     */   static final String OFFSET = "raw.offset";
/*     */   static final String N = "raw.n";
/*     */   static final String GAP = "raw.gap";
/*     */   static final String OPTIONS = "raw.options";
/*     */   static final int WHITE_IS_ZERO = 1;
/*     */   static final int INTEL_BYTE_ORDER = 2;
/*     */   static final int OPEN_ALL = 4;
/*  33 */   private static int choiceSelection = Prefs.getInt("raw.type", 0);
/*  34 */   private static int width = Prefs.getInt("raw.width", 512);
/*  35 */   private static int height = Prefs.getInt("raw.height", 512);
/*  36 */   private static long offset = Prefs.getInt("raw.offset", 0);
/*  37 */   private static int nImages = Prefs.getInt("raw.n", 1);
/*  38 */   private static int gapBetweenImages = Prefs.getInt("raw.gap", 0);
/*     */ 
/*  49 */   private static int options = Prefs.getInt("raw.options", 0);
/*  50 */   private static boolean whiteIsZero = (options & 0x1) != 0;
/*  51 */   private static boolean intelByteOrder = (options & 0x2) != 0;
/*     */   private static boolean virtual;
/*     */   private boolean openAll;
/*     */   private static FileInfo lastFileInfo;
/*  44 */   private static String[] types = { "8-bit", "16-bit Signed", "16-bit Unsigned", "32-bit Signed", "32-bit Unsigned", "32-bit Real", "64-bit Real", "24-bit RGB", "24-bit RGB Planar", "24-bit BGR", "24-bit Integer", "32-bit ARGB", "32-bit ABGR", "1-bit Bitmap" };
/*     */ 
/*     */   public ImportDialog(String fileName, String directory)
/*     */   {
/*  56 */     this.fileName = fileName;
/*  57 */     this.directory = directory;
/*  58 */     IJ.showStatus("Importing: " + fileName);
/*     */   }
/*     */ 
/*     */   public ImportDialog() {
/*     */   }
/*     */ 
/*     */   boolean showDialog() {
/*  65 */     if (choiceSelection >= types.length)
/*  66 */       choiceSelection = 0;
/*  67 */     GenericDialog gd = new GenericDialog("Import...", IJ.getInstance());
/*  68 */     gd.addChoice("Image type:", types, types[choiceSelection]);
/*  69 */     gd.addNumericField("Width:", width, 0, 6, "pixels");
/*  70 */     gd.addNumericField("Height:", height, 0, 6, "pixels");
/*  71 */     gd.addNumericField("Offset to first image:", offset, 0, 6, "bytes");
/*  72 */     gd.addNumericField("Number of images:", nImages, 0, 6, null);
/*  73 */     gd.addNumericField("Gap between images:", gapBetweenImages, 0, 6, "bytes");
/*  74 */     gd.addCheckbox("White is zero", whiteIsZero);
/*  75 */     gd.addCheckbox("Little-endian byte order", intelByteOrder);
/*  76 */     gd.addCheckbox("Open all files in folder", this.openAll);
/*  77 */     gd.addCheckbox("Use virtual stack", virtual);
/*  78 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/file.html#raw");
/*  79 */     gd.showDialog();
/*  80 */     if (gd.wasCanceled())
/*  81 */       return false;
/*  82 */     choiceSelection = gd.getNextChoiceIndex();
/*  83 */     width = (int)gd.getNextNumber();
/*  84 */     height = (int)gd.getNextNumber();
/*  85 */     offset = ()gd.getNextNumber();
/*  86 */     nImages = (int)gd.getNextNumber();
/*  87 */     gapBetweenImages = (int)gd.getNextNumber();
/*  88 */     whiteIsZero = gd.getNextBoolean();
/*  89 */     intelByteOrder = gd.getNextBoolean();
/*  90 */     this.openAll = gd.getNextBoolean();
/*  91 */     virtual = gd.getNextBoolean();
/*  92 */     IJ.register(ImportDialog.class);
/*  93 */     return true;
/*     */   }
/*     */ 
/*     */   void openAll(String[] list, FileInfo fi)
/*     */   {
/*  99 */     FolderOpener fo = new FolderOpener();
/* 100 */     list = fo.trimFileList(list);
/* 101 */     list = fo.sortFileList(list);
/* 102 */     if (list == null) return;
/* 103 */     ImageStack stack = null;
/* 104 */     ImagePlus imp = null;
/* 105 */     double min = 1.7976931348623157E+308D;
/* 106 */     double max = -1.797693134862316E+308D;
/* 107 */     for (int i = 0; i < list.length; i++)
/* 108 */       if (!list[i].startsWith("."))
/*     */       {
/* 110 */         fi.fileName = list[i];
/* 111 */         imp = new FileOpener(fi).open(false);
/* 112 */         if (imp == null) {
/* 113 */           IJ.log(list[i] + ": unable to open");
/*     */         } else {
/* 115 */           if (stack == null)
/* 116 */             stack = imp.createEmptyStack();
/*     */           try {
/* 118 */             ImageProcessor ip = imp.getProcessor();
/* 119 */             if (ip.getMin() < min) min = ip.getMin();
/* 120 */             if (ip.getMax() > max) max = ip.getMax();
/* 121 */             stack.addSlice(list[i], ip);
/*     */           }
/*     */           catch (OutOfMemoryError e) {
/* 124 */             IJ.outOfMemory("OpenAll");
/* 125 */             stack.trim();
/* 126 */             break;
/*     */           }
/* 128 */           IJ.showStatus(stack.getSize() + 1 + ": " + list[i]);
/*     */         }
/*     */       }
/* 131 */     if (stack != null) {
/* 132 */       imp = new ImagePlus("Imported Stack", stack);
/* 133 */       if ((imp.getBitDepth() == 16) || (imp.getBitDepth() == 32))
/* 134 */         imp.getProcessor().setMinAndMax(min, max);
/* 135 */       Calibration cal = imp.getCalibration();
/* 136 */       if (fi.fileType == 1)
/* 137 */         cal.setSigned16BitCalibration();
/* 138 */       imp.show();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void openImage()
/*     */   {
/* 145 */     FileInfo fi = getFileInfo();
/* 146 */     if (fi == null) return;
/* 147 */     if (this.openAll) {
/* 148 */       if (virtual) {
/* 149 */         virtual = false;
/* 150 */         IJ.error("Import Raw", "\"Open All\" does not currently support virtual stacks");
/* 151 */         return;
/*     */       }
/* 153 */       String[] list = new File(this.directory).list();
/* 154 */       if (list == null) return;
/* 155 */       openAll(list, fi);
/* 156 */     } else if (virtual) {
/* 157 */       new FileInfoVirtualStack(fi);
/*     */     } else {
/* 159 */       FileOpener fo = new FileOpener(fi);
/* 160 */       ImagePlus imp = fo.open(false);
/* 161 */       if (imp != null) {
/* 162 */         imp.show();
/* 163 */         int n = imp.getStackSize();
/* 164 */         if (n > 1) {
/* 165 */           imp.setSlice(n / 2);
/* 166 */           ImageProcessor ip = imp.getProcessor();
/* 167 */           ip.resetMinAndMax();
/* 168 */           imp.setDisplayRange(ip.getMin(), ip.getMax());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public FileInfo getFileInfo()
/*     */   {
/* 178 */     if (!showDialog())
/* 179 */       return null;
/* 180 */     String imageType = types[choiceSelection];
/* 181 */     FileInfo fi = new FileInfo();
/* 182 */     fi.fileFormat = 1;
/* 183 */     fi.fileName = this.fileName;
/* 184 */     fi.directory = this.directory;
/* 185 */     fi.width = width;
/* 186 */     fi.height = height;
/* 187 */     if (offset > 2147483647L)
/* 188 */       fi.longOffset = offset;
/*     */     else
/* 190 */       fi.offset = ((int)offset);
/* 191 */     fi.nImages = nImages;
/* 192 */     fi.gapBetweenImages = gapBetweenImages;
/* 193 */     fi.intelByteOrder = intelByteOrder;
/* 194 */     fi.whiteIsZero = whiteIsZero;
/* 195 */     if (imageType.equals("8-bit"))
/* 196 */       fi.fileType = 0;
/* 197 */     else if (imageType.equals("16-bit Signed"))
/* 198 */       fi.fileType = 1;
/* 199 */     else if (imageType.equals("16-bit Unsigned"))
/* 200 */       fi.fileType = 2;
/* 201 */     else if (imageType.equals("32-bit Signed"))
/* 202 */       fi.fileType = 3;
/* 203 */     else if (imageType.equals("32-bit Unsigned"))
/* 204 */       fi.fileType = 11;
/* 205 */     else if (imageType.equals("32-bit Real"))
/* 206 */       fi.fileType = 4;
/* 207 */     else if (imageType.equals("64-bit Real"))
/* 208 */       fi.fileType = 16;
/* 209 */     else if (imageType.equals("24-bit RGB"))
/* 210 */       fi.fileType = 6;
/* 211 */     else if (imageType.equals("24-bit RGB Planar"))
/* 212 */       fi.fileType = 7;
/* 213 */     else if (imageType.equals("24-bit BGR"))
/* 214 */       fi.fileType = 10;
/* 215 */     else if (imageType.equals("24-bit Integer"))
/* 216 */       fi.fileType = 14;
/* 217 */     else if (imageType.equals("32-bit ARGB"))
/* 218 */       fi.fileType = 9;
/* 219 */     else if (imageType.equals("32-bit ABGR"))
/* 220 */       fi.fileType = 18;
/* 221 */     else if (imageType.equals("1-bit Bitmap"))
/* 222 */       fi.fileType = 8;
/*     */     else
/* 224 */       fi.fileType = 0;
/* 225 */     if (IJ.debugMode) IJ.log("ImportDialog: " + fi);
/* 226 */     lastFileInfo = (FileInfo)fi.clone();
/* 227 */     return fi;
/*     */   }
/*     */ 
/*     */   public static void savePreferences(Properties prefs)
/*     */   {
/* 232 */     prefs.put("raw.type", Integer.toString(choiceSelection));
/* 233 */     prefs.put("raw.width", Integer.toString(width));
/* 234 */     prefs.put("raw.height", Integer.toString(height));
/* 235 */     prefs.put("raw.offset", Integer.toString(offset > 2147483647L ? 0 : (int)offset));
/* 236 */     prefs.put("raw.n", Integer.toString(nImages));
/* 237 */     prefs.put("raw.gap", Integer.toString(gapBetweenImages));
/* 238 */     int options = 0;
/* 239 */     if (whiteIsZero)
/* 240 */       options |= 1;
/* 241 */     if (intelByteOrder) {
/* 242 */       options |= 2;
/*     */     }
/*     */ 
/* 245 */     prefs.put("raw.options", Integer.toString(options));
/*     */   }
/*     */ 
/*     */   public static FileInfo getLastFileInfo()
/*     */   {
/* 251 */     return lastFileInfo;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.ImportDialog
 * JD-Core Version:    0.6.2
 */