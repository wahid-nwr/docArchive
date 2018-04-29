/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.FileSaver;
/*     */ import ij.io.SaveDialog;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.LUT;
/*     */ import java.io.File;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class StackWriter
/*     */   implements PlugIn
/*     */ {
/*  18 */   private static String[] choices = { "BMP", "FITS", "GIF", "JPEG", "PGM", "PNG", "Raw", "Text", "TIFF", "ZIP" };
/*  19 */   private static String fileType = "TIFF";
/*  20 */   private static int ndigits = 4;
/*     */   private static boolean useLabels;
/*  22 */   private static boolean firstTime = true;
/*     */   private int startAt;
/*     */   private boolean hyperstack;
/*     */   private int[] dim;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  29 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  30 */     if ((imp == null) || ((imp != null) && (imp.getStackSize() < 2))) {
/*  31 */       IJ.error("Stack Writer", "This command requires a stack.");
/*  32 */       return;
/*     */     }
/*  34 */     int stackSize = imp.getStackSize();
/*  35 */     String name = imp.getTitle();
/*  36 */     int dotIndex = name.lastIndexOf(".");
/*  37 */     if (dotIndex >= 0)
/*  38 */       name = name.substring(0, dotIndex);
/*  39 */     this.hyperstack = imp.isHyperStack();
/*  40 */     LUT[] luts = null;
/*  41 */     int lutIndex = 0;
/*  42 */     int nChannels = imp.getNChannels();
/*  43 */     if (this.hyperstack) {
/*  44 */       this.dim = imp.getDimensions();
/*  45 */       if (imp.isComposite())
/*  46 */         luts = ((CompositeImage)imp).getLuts();
/*  47 */       if ((firstTime) && (ndigits == 4)) {
/*  48 */         ndigits = 3;
/*  49 */         firstTime = false;
/*     */       }
/*     */     }
/*     */ 
/*  53 */     GenericDialog gd = new GenericDialog("Save Image Sequence");
/*  54 */     gd.addChoice("Format:", choices, fileType);
/*  55 */     gd.addStringField("Name:", name, 12);
/*  56 */     if (!this.hyperstack)
/*  57 */       gd.addNumericField("Start At:", this.startAt, 0);
/*  58 */     gd.addNumericField("Digits (1-8):", ndigits, 0);
/*  59 */     if (!this.hyperstack)
/*  60 */       gd.addCheckbox("Use slice labels as file names", useLabels);
/*  61 */     gd.showDialog();
/*  62 */     if (gd.wasCanceled())
/*  63 */       return;
/*  64 */     fileType = gd.getNextChoice();
/*  65 */     name = gd.getNextString();
/*  66 */     if (!this.hyperstack)
/*  67 */       this.startAt = ((int)gd.getNextNumber());
/*  68 */     if (this.startAt < 0) this.startAt = 0;
/*  69 */     ndigits = (int)gd.getNextNumber();
/*  70 */     if (!this.hyperstack)
/*  71 */       useLabels = gd.getNextBoolean();
/*     */     else
/*  73 */       useLabels = false;
/*  74 */     int number = 0;
/*  75 */     if (ndigits < 1) ndigits = 1;
/*  76 */     if (ndigits > 8) ndigits = 8;
/*  77 */     int maxImages = (int)Math.pow(10.0D, ndigits);
/*  78 */     if ((stackSize > maxImages) && (!useLabels) && (!this.hyperstack)) {
/*  79 */       IJ.error("Stack Writer", "More than " + ndigits + " digits are required to generate \nunique file names for " + stackSize + " images.");
/*     */ 
/*  81 */       return;
/*     */     }
/*  83 */     String format = fileType.toLowerCase(Locale.US);
/*  84 */     if ((format.equals("gif")) && (!FileSaver.okForGif(imp)))
/*  85 */       return;
/*  86 */     if ((format.equals("fits")) && (!FileSaver.okForFits(imp))) {
/*  87 */       return;
/*     */     }
/*  89 */     if (format.equals("text"))
/*  90 */       format = "text image";
/*  91 */     String extension = "." + format;
/*  92 */     if (format.equals("tiff"))
/*  93 */       extension = ".tif";
/*  94 */     else if (format.equals("text image")) {
/*  95 */       extension = ".txt";
/*     */     }
/*  97 */     String title = "Save Image Sequence";
/*  98 */     String macroOptions = Macro.getOptions();
/*  99 */     String directory = null;
/* 100 */     if (macroOptions != null) {
/* 101 */       directory = Macro.getValue(macroOptions, title, null);
/* 102 */       if (directory != null) {
/* 103 */         File f = new File(directory);
/* 104 */         if ((!f.isDirectory()) && ((f.exists()) || (directory.lastIndexOf(".") > directory.length() - 5)))
/* 105 */           directory = f.getParent();
/* 106 */         if (!directory.endsWith(File.separator))
/* 107 */           directory = directory + File.separator;
/*     */       }
/*     */     }
/* 110 */     if (directory == null)
/* 111 */       if ((Prefs.useFileChooser) && (!IJ.isMacOSX())) {
/* 112 */         String digits = getDigits(number);
/* 113 */         SaveDialog sd = new SaveDialog(title, name + digits + extension, extension);
/* 114 */         String name2 = sd.getFileName();
/* 115 */         if (name2 == null)
/* 116 */           return;
/* 117 */         directory = sd.getDirectory();
/*     */       } else {
/* 119 */         directory = IJ.getDirectory(title);
/*     */       }
/* 121 */     if (directory == null) {
/* 122 */       return;
/*     */     }
/* 124 */     boolean isOverlay = (imp.getOverlay() != null) && (!imp.getHideOverlay());
/* 125 */     if ((!format.equals("jpeg")) && (!format.equals("png")))
/* 126 */       isOverlay = false;
/* 127 */     ImageStack stack = imp.getStack();
/* 128 */     ImagePlus imp2 = new ImagePlus();
/* 129 */     imp2.setTitle(imp.getTitle());
/* 130 */     Calibration cal = imp.getCalibration();
/* 131 */     int nSlices = stack.getSize();
/* 132 */     String label = null;
/* 133 */     imp.lock();
/* 134 */     for (int i = 1; i <= nSlices; i++) {
/* 135 */       IJ.showStatus("writing: " + i + "/" + nSlices);
/* 136 */       IJ.showProgress(i, nSlices);
/* 137 */       ImageProcessor ip = stack.getProcessor(i);
/* 138 */       if (isOverlay) {
/* 139 */         imp.setSliceWithoutUpdate(i);
/* 140 */         ip = imp.flatten().getProcessor();
/* 141 */       } else if ((luts != null) && (nChannels > 1) && (this.hyperstack)) {
/* 142 */         ip.setColorModel(luts[(lutIndex++)]);
/* 143 */         if (lutIndex >= luts.length) lutIndex = 0;
/*     */       }
/* 145 */       imp2.setProcessor(null, ip);
/* 146 */       String label2 = stack.getSliceLabel(i);
/* 147 */       if ((label2 != null) && (label2.indexOf("\n") != -1)) {
/* 148 */         imp2.setProperty("Info", label2);
/*     */       } else {
/* 150 */         Properties props = imp2.getProperties();
/* 151 */         if (props != null) props.remove("Info");
/*     */       }
/* 153 */       imp2.setCalibration(cal);
/* 154 */       String digits = getDigits(number++);
/* 155 */       if (useLabels) {
/* 156 */         label = stack.getShortSliceLabel(i);
/* 157 */         if ((label != null) && (label.equals(""))) label = null;
/* 158 */         if (label != null) label = label.replaceAll("/", "-");
/*     */       }
/*     */       String path;
/*     */       String path;
/* 160 */       if (label == null)
/* 161 */         path = directory + name + digits + extension;
/*     */       else
/* 163 */         path = directory + label + extension;
/* 164 */       if (Recorder.record)
/* 165 */         Recorder.disablePathRecording();
/* 166 */       IJ.saveAs(imp2, format, path);
/*     */     }
/* 168 */     imp.unlock();
/* 169 */     if (isOverlay) imp.setSlice(1);
/* 170 */     IJ.showStatus("");
/*     */   }
/*     */ 
/*     */   String getDigits(int n) {
/* 174 */     if (this.hyperstack) {
/* 175 */       int c = n % this.dim[2] + 1;
/* 176 */       int z = n / this.dim[2] % this.dim[3] + 1;
/* 177 */       int t = n / (this.dim[2] * this.dim[3]) % this.dim[4] + 1;
/* 178 */       String cs = ""; String zs = ""; String ts = "";
/* 179 */       if (this.dim[2] > 1) {
/* 180 */         cs = "00000000" + c;
/* 181 */         cs = "_c" + cs.substring(cs.length() - ndigits);
/*     */       }
/* 183 */       if (this.dim[3] > 1) {
/* 184 */         zs = "00000000" + z;
/* 185 */         zs = "_z" + zs.substring(zs.length() - ndigits);
/*     */       }
/* 187 */       if (this.dim[4] > 1) {
/* 188 */         ts = "00000000" + t;
/* 189 */         ts = "_t" + ts.substring(ts.length() - ndigits);
/*     */       }
/* 191 */       return ts + zs + cs;
/*     */     }
/* 193 */     String digits = "00000000" + (this.startAt + n);
/* 194 */     return digits.substring(digits.length() - ndigits);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.StackWriter
 * JD-Core Version:    0.6.2
 */