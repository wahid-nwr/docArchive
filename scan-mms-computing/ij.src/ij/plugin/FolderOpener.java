/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.VirtualStack;
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.Roi;
/*     */ import ij.io.FileInfo;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.io.Opener;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.DicomTools;
/*     */ import ij.util.StringSorter;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.io.File;
/*     */ 
/*     */ public class FolderOpener
/*     */   implements PlugIn
/*     */ {
/*  17 */   private static String[] excludedTypes = { ".txt", ".lut", ".roi", ".pty", ".hdr", ".java", ".ijm", ".py", ".js", ".bsh", ".xml" };
/*  18 */   private static boolean staticSortFileNames = true;
/*     */   private static boolean staticOpenAsVirtualStack;
/*     */   private boolean convertToRGB;
/*  21 */   private boolean sortFileNames = true;
/*     */   private boolean openAsVirtualStack;
/*  23 */   private double scale = 100.0D;
/*     */   private int n;
/*     */   private int start;
/*     */   private int increment;
/*     */   private String filter;
/*     */   private boolean isRegex;
/*     */   private FileInfo fi;
/*     */   private String info1;
/*     */   private ImagePlus image;
/*     */ 
/*     */   public static ImagePlus open(String path)
/*     */   {
/*  33 */     FolderOpener fo = new FolderOpener();
/*  34 */     fo.run(path);
/*  35 */     return fo.image;
/*     */   }
/*     */ 
/*     */   public ImagePlus openFolder(String path)
/*     */   {
/*  40 */     run(path);
/*  41 */     return this.image;
/*     */   }
/*     */ 
/*     */   public void run(String arg) {
/*  45 */     String directory = null;
/*  46 */     if ((arg != null) && (!arg.equals(""))) {
/*  47 */       directory = arg;
/*     */     } else {
/*  49 */       if (!IJ.macroRunning()) {
/*  50 */         this.sortFileNames = staticSortFileNames;
/*  51 */         this.openAsVirtualStack = staticOpenAsVirtualStack;
/*     */       }
/*  53 */       arg = null;
/*  54 */       String title = "Open Image Sequence...";
/*  55 */       String macroOptions = Macro.getOptions();
/*  56 */       if (macroOptions != null) {
/*  57 */         directory = Macro.getValue(macroOptions, title, null);
/*  58 */         if (directory != null) {
/*  59 */           directory = OpenDialog.lookupPathVariable(directory);
/*  60 */           File f = new File(directory);
/*  61 */           if ((!f.isDirectory()) && ((f.exists()) || (directory.lastIndexOf(".") > directory.length() - 5)))
/*  62 */             directory = f.getParent();
/*     */         }
/*     */       }
/*  65 */       if (directory == null)
/*  66 */         if ((Prefs.useFileChooser) && (!IJ.isMacOSX())) {
/*  67 */           OpenDialog od = new OpenDialog(title, arg);
/*  68 */           directory = od.getDirectory();
/*  69 */           String name = od.getFileName();
/*  70 */           if (name == null)
/*  71 */             return;
/*     */         } else {
/*  73 */           directory = IJ.getDirectory(title);
/*     */         }
/*     */     }
/*  76 */     if (directory == null)
/*  77 */       return;
/*  78 */     String[] list = new File(directory).list();
/*  79 */     if (list == null)
/*  80 */       return;
/*  81 */     String title = directory;
/*  82 */     if ((title.endsWith(File.separator)) || (title.endsWith("/")))
/*  83 */       title = title.substring(0, title.length() - 1);
/*  84 */     int index = title.lastIndexOf(File.separatorChar);
/*  85 */     if (index != -1) title = title.substring(index + 1);
/*  86 */     if (title.endsWith(":")) {
/*  87 */       title = title.substring(0, title.length() - 1);
/*     */     }
/*  89 */     IJ.register(FolderOpener.class);
/*  90 */     list = trimFileList(list);
/*  91 */     if (list == null) return;
/*  92 */     if (IJ.debugMode) IJ.log("FolderOpener: " + directory + " (" + list.length + " files)");
/*  93 */     int width = 0; int height = 0; int stackSize = 1; int bitDepth = 0;
/*  94 */     ImageStack stack = null;
/*  95 */     double min = 1.7976931348623157E+308D;
/*  96 */     double max = -1.797693134862316E+308D;
/*  97 */     Calibration cal = null;
/*  98 */     boolean allSameCalibration = true;
/*  99 */     IJ.resetEscape();
/* 100 */     Overlay overlay = null;
/*     */     try {
/* 102 */       for (int i = 0; i < list.length; i++) {
/* 103 */         IJ.redirectErrorMessages();
/* 104 */         Opener opener = new Opener();
/* 105 */         opener.setSilentMode(true);
/* 106 */         ImagePlus imp = opener.openImage(directory, list[i]);
/* 107 */         if (imp != null) {
/* 108 */           width = imp.getWidth();
/* 109 */           height = imp.getHeight();
/* 110 */           bitDepth = imp.getBitDepth();
/* 111 */           this.fi = imp.getOriginalFileInfo();
/* 112 */           if (arg == null) {
/* 113 */             if (showDialog(imp, list)) break;
/* 114 */             return;
/*     */           }
/* 116 */           this.n = list.length;
/* 117 */           this.start = 1;
/* 118 */           this.increment = 1;
/*     */ 
/* 120 */           break;
/*     */         }
/*     */       }
/* 123 */       if (width == 0) {
/* 124 */         IJ.error("Import Sequence", "This folder does not appear to contain any TIFF,\nJPEG, BMP, DICOM, GIF, FITS or PGM files.");
/*     */ 
/* 126 */         return;
/*     */       }
/*     */ 
/* 129 */       if ((this.filter != null) && ((this.filter.equals("")) || (this.filter.equals("*"))))
/* 130 */         this.filter = null;
/* 131 */       if (this.filter != null) {
/* 132 */         int filteredImages = 0;
/* 133 */         for (int i = 0; i < list.length; i++) {
/* 134 */           if ((this.isRegex) && (list[i].matches(this.filter)))
/* 135 */             filteredImages++;
/* 136 */           else if (list[i].indexOf(this.filter) >= 0)
/* 137 */             filteredImages++;
/*     */           else
/* 139 */             list[i] = null;
/*     */         }
/* 141 */         if (filteredImages == 0) {
/* 142 */           if (this.isRegex)
/* 143 */             IJ.error("Import Sequence", "None of the file names match the regular expression.");
/*     */           else
/* 145 */             IJ.error("Import Sequence", "None of the " + list.length + " files contain\n the string '" + this.filter + "' in their name.");
/* 146 */           return;
/*     */         }
/* 148 */         String[] list2 = new String[filteredImages];
/* 149 */         int j = 0;
/* 150 */         for (int i = 0; i < list.length; i++) {
/* 151 */           if (list[i] != null)
/* 152 */             list2[(j++)] = list[i];
/*     */         }
/* 154 */         list = list2;
/*     */       }
/* 156 */       if (this.sortFileNames) {
/* 157 */         list = sortFileList(list);
/*     */       }
/* 159 */       if (this.n < 1)
/* 160 */         this.n = list.length;
/* 161 */       if ((this.start < 1) || (this.start > list.length))
/* 162 */         this.start = 1;
/* 163 */       if (this.start + this.n - 1 > list.length)
/* 164 */         this.n = (list.length - this.start + 1);
/* 165 */       int count = 0;
/* 166 */       int counter = 0;
/* 167 */       ImagePlus imp = null;
/* 168 */       for (int i = this.start - 1; i < list.length; i++)
/* 169 */         if (counter++ % this.increment == 0)
/*     */         {
/* 171 */           Opener opener = new Opener();
/* 172 */           opener.setSilentMode(true);
/* 173 */           IJ.redirectErrorMessages();
/* 174 */           if ((!this.openAsVirtualStack) || (stack == null))
/* 175 */             imp = opener.openImage(directory, list[i]);
/* 176 */           if ((imp != null) && (stack == null)) {
/* 177 */             width = imp.getWidth();
/* 178 */             height = imp.getHeight();
/* 179 */             stackSize = imp.getStackSize();
/* 180 */             bitDepth = imp.getBitDepth();
/* 181 */             cal = imp.getCalibration();
/* 182 */             if (this.convertToRGB) bitDepth = 24;
/* 183 */             ColorModel cm = imp.getProcessor().getColorModel();
/* 184 */             if (this.openAsVirtualStack) {
/* 185 */               stack = new VirtualStack(width, height, cm, directory);
/* 186 */               ((VirtualStack)stack).setBitDepth(bitDepth);
/* 187 */             } else if (this.scale < 100.0D) {
/* 188 */               stack = new ImageStack((int)(width * this.scale / 100.0D), (int)(height * this.scale / 100.0D), cm);
/*     */             } else {
/* 190 */               stack = new ImageStack(width, height, cm);
/* 191 */             }this.info1 = ((String)imp.getProperty("Info"));
/*     */           }
/* 193 */           if (imp != null)
/*     */           {
/* 195 */             if ((imp.getWidth() != width) || (imp.getHeight() != height)) {
/* 196 */               IJ.log(list[i] + ": wrong size; " + width + "x" + height + " expected, " + imp.getWidth() + "x" + imp.getHeight() + " found");
/*     */             }
/*     */             else {
/* 199 */               String label = imp.getTitle();
/* 200 */               if (stackSize == 1) {
/* 201 */                 String info = (String)imp.getProperty("Info");
/* 202 */                 if (info != null)
/* 203 */                   label = label + "\n" + info;
/*     */               }
/* 205 */               if (imp.getCalibration().pixelWidth != cal.pixelWidth)
/* 206 */                 allSameCalibration = false;
/* 207 */               ImageStack inputStack = imp.getStack();
/* 208 */               Overlay overlay2 = imp.getOverlay();
/* 209 */               if ((overlay2 != null) && (!this.openAsVirtualStack)) {
/* 210 */                 if (overlay == null)
/* 211 */                   overlay = new Overlay();
/* 212 */                 for (int j = 0; j < overlay2.size(); j++) {
/* 213 */                   Roi roi = overlay2.get(j);
/* 214 */                   int position = roi.getPosition();
/* 215 */                   if (position == 0)
/* 216 */                     roi.setPosition(count + 1);
/* 217 */                   overlay.add(roi);
/*     */                 }
/*     */               }
/* 220 */               for (int slice = 1; slice <= stackSize; slice++) {
/* 221 */                 ImageProcessor ip = inputStack.getProcessor(slice);
/* 222 */                 String label2 = label;
/* 223 */                 if (stackSize > 1) {
/* 224 */                   String sliceLabel = inputStack.getSliceLabel(slice);
/* 225 */                   if (sliceLabel != null)
/* 226 */                     label2 = sliceLabel;
/* 227 */                   else if ((label2 != null) && (!label2.equals("")))
/* 228 */                     label2 = label2 + ":" + slice;
/*     */                 }
/* 230 */                 int bitDepth2 = imp.getBitDepth();
/* 231 */                 if (!this.openAsVirtualStack) {
/* 232 */                   if (this.convertToRGB) {
/* 233 */                     ip = ip.convertToRGB();
/* 234 */                     bitDepth2 = 24;
/*     */                   }
/* 236 */                   if (bitDepth2 != bitDepth) {
/* 237 */                     if (bitDepth == 8) {
/* 238 */                       ip = ip.convertToByte(true);
/* 239 */                       bitDepth2 = 8;
/* 240 */                     } else if (bitDepth == 24) {
/* 241 */                       ip = ip.convertToRGB();
/* 242 */                       bitDepth2 = 24;
/*     */                     }
/*     */                   }
/* 245 */                   if (bitDepth2 != bitDepth) {
/* 246 */                     IJ.log(list[i] + ": wrong bit depth; " + bitDepth + " expected, " + bitDepth2 + " found");
/* 247 */                     break;
/*     */                   }
/*     */                 }
/* 250 */                 if (slice == 1) count++;
/* 251 */                 IJ.showStatus(count + "/" + this.n);
/* 252 */                 IJ.showProgress(count, this.n);
/* 253 */                 if (this.scale < 100.0D)
/* 254 */                   ip = ip.resize((int)(width * this.scale / 100.0D), (int)(height * this.scale / 100.0D));
/* 255 */                 if (ip.getMin() < min) min = ip.getMin();
/* 256 */                 if (ip.getMax() > max) max = ip.getMax();
/*     */ 
/* 258 */                 if (this.openAsVirtualStack) {
/* 259 */                   if (slice == 1) ((VirtualStack)stack).addSlice(list[i]); 
/*     */                 }
/*     */                 else
/* 261 */                   stack.addSlice(label2, ip);
/*     */               }
/* 263 */               if (count >= this.n)
/*     */                 break;
/* 265 */               if (IJ.escapePressed()) {
/* 266 */                 IJ.beep(); break;
/*     */               }
/*     */             }
/*     */           }
/*     */         } } catch (OutOfMemoryError e) { IJ.outOfMemory("FolderOpener");
/* 271 */       if (stack != null) stack.trim();
/*     */     }
/* 273 */     if ((stack != null) && (stack.getSize() > 0)) {
/* 274 */       ImagePlus imp2 = new ImagePlus(title, stack);
/* 275 */       if ((imp2.getType() == 1) || (imp2.getType() == 2))
/* 276 */         imp2.getProcessor().setMinAndMax(min, max);
/* 277 */       if (this.fi == null)
/* 278 */         this.fi = new FileInfo();
/* 279 */       this.fi.fileFormat = 0;
/* 280 */       this.fi.fileName = "";
/* 281 */       this.fi.directory = directory;
/* 282 */       imp2.setFileInfo(this.fi);
/* 283 */       imp2.setOverlay(overlay);
/* 284 */       if (allSameCalibration)
/*     */       {
/* 286 */         if ((this.scale != 100.0D) && (cal.scaled())) {
/* 287 */           cal.pixelWidth /= this.scale / 100.0D;
/* 288 */           cal.pixelHeight /= this.scale / 100.0D;
/*     */         }
/* 290 */         if ((cal.pixelWidth != 1.0D) && (cal.pixelDepth == 1.0D))
/* 291 */           cal.pixelDepth = cal.pixelWidth;
/* 292 */         if ((cal.pixelWidth <= 0.0001D) && (cal.getUnit().equals("cm"))) {
/* 293 */           cal.pixelWidth *= 10000.0D;
/* 294 */           cal.pixelHeight *= 10000.0D;
/* 295 */           cal.pixelDepth *= 10000.0D;
/* 296 */           cal.setUnit("um");
/*     */         }
/* 298 */         imp2.setCalibration(cal);
/*     */       }
/* 300 */       if ((this.info1 != null) && (this.info1.lastIndexOf("7FE0,0010") > 0)) {
/* 301 */         stack = DicomTools.sort(stack);
/* 302 */         imp2.setStack(stack);
/* 303 */         double voxelDepth = DicomTools.getVoxelDepth(stack);
/* 304 */         if (voxelDepth > 0.0D) {
/* 305 */           if (IJ.debugMode) IJ.log("DICOM voxel depth set to " + voxelDepth + " (" + cal.pixelDepth + ")");
/* 306 */           cal.pixelDepth = voxelDepth;
/* 307 */           imp2.setCalibration(cal);
/*     */         }
/*     */       }
/* 310 */       if ((imp2.getStackSize() == 1) && (this.info1 != null))
/* 311 */         imp2.setProperty("Info", this.info1);
/* 312 */       if (arg == null)
/* 313 */         imp2.show();
/*     */       else
/* 315 */         this.image = imp2;
/*     */     }
/* 317 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   boolean showDialog(ImagePlus imp, String[] list) {
/* 321 */     int fileCount = list.length;
/* 322 */     FolderOpenerDialog gd = new FolderOpenerDialog("Sequence Options", imp, list);
/* 323 */     gd.addNumericField("Number of images:", fileCount, 0);
/* 324 */     gd.addNumericField("Starting image:", 1.0D, 0);
/* 325 */     gd.addNumericField("Increment:", 1.0D, 0);
/* 326 */     gd.addNumericField("Scale images:", this.scale, 0, 4, "%");
/* 327 */     gd.addStringField("File name contains:", "", 10);
/* 328 */     gd.addStringField("or enter pattern:", "", 10);
/* 329 */     gd.addCheckbox("Convert_to_RGB", this.convertToRGB);
/* 330 */     gd.addCheckbox("Sort names numerically", this.sortFileNames);
/* 331 */     gd.addCheckbox("Use virtual stack", this.openAsVirtualStack);
/* 332 */     gd.addMessage("10000 x 10000 x 1000 (100.3MB)");
/* 333 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/file.html#seq1");
/* 334 */     gd.showDialog();
/* 335 */     if (gd.wasCanceled())
/* 336 */       return false;
/* 337 */     this.n = ((int)gd.getNextNumber());
/* 338 */     this.start = ((int)gd.getNextNumber());
/* 339 */     this.increment = ((int)gd.getNextNumber());
/* 340 */     if (this.increment < 1)
/* 341 */       this.increment = 1;
/* 342 */     this.scale = gd.getNextNumber();
/* 343 */     if (this.scale < 5.0D) this.scale = 5.0D;
/* 344 */     if (this.scale > 100.0D) this.scale = 100.0D;
/* 345 */     this.filter = gd.getNextString();
/* 346 */     String regex = gd.getNextString();
/* 347 */     if (!regex.equals("")) {
/* 348 */       this.filter = regex;
/* 349 */       this.isRegex = true;
/*     */     }
/* 351 */     this.convertToRGB = gd.getNextBoolean();
/* 352 */     this.sortFileNames = gd.getNextBoolean();
/* 353 */     this.openAsVirtualStack = gd.getNextBoolean();
/* 354 */     if (this.openAsVirtualStack)
/* 355 */       this.scale = 100.0D;
/* 356 */     if (!IJ.macroRunning()) {
/* 357 */       staticSortFileNames = this.sortFileNames;
/* 358 */       staticOpenAsVirtualStack = this.openAsVirtualStack;
/*     */     }
/* 360 */     return true;
/*     */   }
/*     */ 
/*     */   public String[] trimFileList(String[] rawlist)
/*     */   {
/* 365 */     int count = 0;
/* 366 */     for (int i = 0; i < rawlist.length; i++) {
/* 367 */       String name = rawlist[i];
/* 368 */       if ((name.startsWith(".")) || (name.equals("Thumbs.db")) || (excludedFileType(name)))
/* 369 */         rawlist[i] = null;
/*     */       else
/* 371 */         count++;
/*     */     }
/* 373 */     if (count == 0) return null;
/* 374 */     String[] list = rawlist;
/* 375 */     if (count < rawlist.length) {
/* 376 */       list = new String[count];
/* 377 */       int index = 0;
/* 378 */       for (int i = 0; i < rawlist.length; i++) {
/* 379 */         if (rawlist[i] != null)
/* 380 */           list[(index++)] = rawlist[i];
/*     */       }
/*     */     }
/* 383 */     return list;
/*     */   }
/*     */ 
/*     */   public static boolean excludedFileType(String name)
/*     */   {
/* 388 */     if (name == null) return true;
/* 389 */     for (int i = 0; i < excludedTypes.length; i++) {
/* 390 */       if (name.endsWith(excludedTypes[i]))
/* 391 */         return true;
/*     */     }
/* 393 */     return false;
/*     */   }
/*     */ 
/*     */   public String[] sortFileList(String[] list)
/*     */   {
/* 398 */     int listLength = list.length;
/* 399 */     boolean allSameLength = true;
/* 400 */     int len0 = list[0].length();
/* 401 */     for (int i = 0; i < listLength; i++) {
/* 402 */       if (list[i].length() != len0) {
/* 403 */         allSameLength = false;
/* 404 */         break;
/*     */       }
/*     */     }
/* 407 */     if (allSameLength) {
/* 408 */       StringSorter.sort(list); return list;
/* 409 */     }int maxDigits = 15;
/* 410 */     String[] list2 = null;
/*     */ 
/* 412 */     for (int i = 0; i < listLength; i++) {
/* 413 */       int len = list[i].length();
/* 414 */       String num = "";
/* 415 */       for (int j = 0; j < len; j++) {
/* 416 */         char ch = list[i].charAt(j);
/* 417 */         if ((ch >= '0') && (ch <= '9')) num = num + ch;
/*     */       }
/* 419 */       if (list2 == null) list2 = new String[listLength];
/* 420 */       if (num.length() == 0) num = "aaaaaa";
/* 421 */       num = "000000000000000" + num;
/* 422 */       num = num.substring(num.length() - maxDigits);
/* 423 */       list2[i] = (num + list[i]);
/*     */     }
/* 425 */     if (list2 != null) {
/* 426 */       StringSorter.sort(list2);
/* 427 */       for (int i = 0; i < listLength; i++)
/* 428 */         list2[i] = list2[i].substring(maxDigits);
/* 429 */       return list2;
/*     */     }
/* 431 */     StringSorter.sort(list);
/* 432 */     return list;
/*     */   }
/*     */ 
/*     */   public void openAsVirtualStack(boolean b)
/*     */   {
/* 437 */     this.openAsVirtualStack = b;
/*     */   }
/*     */ 
/*     */   public void sortFileNames(boolean b) {
/* 441 */     this.sortFileNames = b;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.FolderOpener
 * JD-Core Version:    0.6.2
 */