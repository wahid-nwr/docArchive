/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.FileInfo;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ 
/*     */ public class ImagesToStack
/*     */   implements PlugIn
/*     */ {
/*     */   private static final int rgb = 33;
/*     */   private static final int COPY_CENTER = 0;
/*     */   private static final int COPY_TOP_LEFT = 1;
/*     */   private static final int SCALE_SMALL = 2;
/*     */   private static final int SCALE_LARGE = 3;
/*  14 */   private static final String[] methods = { "Copy (center)", "Copy (top-left)", "Scale (smallest)", "Scale (largest)" };
/*  15 */   private static int method = 0;
/*     */   private static boolean bicubic;
/*     */   private static boolean keep;
/*  18 */   private static boolean titlesAsLabels = true;
/*     */   private String filter;
/*     */   private int width;
/*     */   private int height;
/*     */   private int maxWidth;
/*     */   private int maxHeight;
/*     */   private int minWidth;
/*     */   private int minHeight;
/*     */   private int minSize;
/*     */   private int maxSize;
/*     */   private Calibration cal2;
/*     */   private int stackType;
/*     */   private ImagePlus[] image;
/*  27 */   private String name = "Stack";
/*     */ 
/*     */   public void run(String arg) {
/*  30 */     convertImagesToStack();
/*     */   }
/*     */ 
/*     */   public void convertImagesToStack() {
/*  34 */     boolean scale = false;
/*  35 */     int[] wList = WindowManager.getIDList();
/*  36 */     if (wList == null) {
/*  37 */       IJ.error("No images are open.");
/*  38 */       return;
/*     */     }
/*     */ 
/*  41 */     int count = 0;
/*  42 */     this.image = new ImagePlus[wList.length];
/*  43 */     for (int i = 0; i < wList.length; i++) {
/*  44 */       ImagePlus imp = WindowManager.getImage(wList[i]);
/*  45 */       if (imp.getStackSize() == 1)
/*  46 */         this.image[(count++)] = imp;
/*     */     }
/*  48 */     if (count < 2) {
/*  49 */       IJ.error("Images to Stack", "There must be at least two open images.");
/*  50 */       return;
/*     */     }
/*     */ 
/*  53 */     this.filter = null;
/*  54 */     count = findMinMaxSize(count);
/*  55 */     boolean sizesDiffer = (this.width != this.minWidth) || (this.height != this.minHeight);
/*  56 */     boolean showDialog = true;
/*  57 */     String macroOptions = Macro.getOptions();
/*  58 */     if ((IJ.macroRunning()) && (macroOptions == null)) {
/*  59 */       if (sizesDiffer) {
/*  60 */         IJ.error("Images are not all the same size");
/*  61 */         return;
/*     */       }
/*  63 */       showDialog = false;
/*     */     }
/*  65 */     if (showDialog) {
/*  66 */       GenericDialog gd = new GenericDialog("Images to Stack");
/*  67 */       if (sizesDiffer) {
/*  68 */         String msg = "The " + count + " images differ in size (smallest=" + this.minWidth + "x" + this.minHeight + ",\nlargest=" + this.maxWidth + "x" + this.maxHeight + "). They will be converted\nto a stack using the specified method.";
/*     */ 
/*  70 */         gd.setInsets(0, 0, 5);
/*  71 */         gd.addMessage(msg);
/*  72 */         gd.addChoice("Method:", methods, methods[method]);
/*     */       }
/*  74 */       gd.addStringField("Name:", this.name, 12);
/*  75 */       gd.addStringField("Title Contains:", "", 12);
/*  76 */       if (sizesDiffer)
/*  77 */         gd.addCheckbox("Bicubic Interpolation", bicubic);
/*  78 */       gd.addCheckbox("Use Titles as Labels", titlesAsLabels);
/*  79 */       gd.addCheckbox("Keep Source Images", keep);
/*  80 */       gd.showDialog();
/*  81 */       if (gd.wasCanceled()) return;
/*  82 */       if (sizesDiffer)
/*  83 */         method = gd.getNextChoiceIndex();
/*  84 */       this.name = gd.getNextString();
/*  85 */       this.filter = gd.getNextString();
/*  86 */       if (sizesDiffer)
/*  87 */         bicubic = gd.getNextBoolean();
/*  88 */       titlesAsLabels = gd.getNextBoolean();
/*  89 */       keep = gd.getNextBoolean();
/*  90 */       if ((this.filter != null) && ((this.filter.equals("")) || (this.filter.equals("*"))))
/*  91 */         this.filter = null;
/*  92 */       if (this.filter != null) {
/*  93 */         count = findMinMaxSize(count);
/*  94 */         if (count == 0)
/*  95 */           IJ.error("Images to Stack", "None of the images have a title containing \"" + this.filter + "\"");
/*     */       }
/*     */     }
/*     */     else {
/*  99 */       keep = false;
/* 100 */     }if (method == 2) {
/* 101 */       this.width = this.minWidth;
/* 102 */       this.height = this.minHeight;
/* 103 */     } else if (method == 3) {
/* 104 */       this.width = this.maxWidth;
/* 105 */       this.height = this.maxHeight;
/*     */     }
/*     */ 
/* 108 */     double min = 1.7976931348623157E+308D;
/* 109 */     double max = -1.797693134862316E+308D;
/* 110 */     ImageStack stack = new ImageStack(this.width, this.height);
/* 111 */     FileInfo fi = this.image[0].getOriginalFileInfo();
/* 112 */     if ((fi != null) && (fi.directory == null)) fi = null;
/* 113 */     for (int i = 0; i < count; i++) {
/* 114 */       ImageProcessor ip = this.image[i].getProcessor();
/* 115 */       if (ip == null) break;
/* 116 */       if (ip.getMin() < min) min = ip.getMin();
/* 117 */       if (ip.getMax() > max) max = ip.getMax();
/* 118 */       String label = titlesAsLabels ? this.image[i].getTitle() : null;
/* 119 */       if (label != null) {
/* 120 */         String info = (String)this.image[i].getProperty("Info");
/* 121 */         if (info != null) label = label + "\n" + info;
/*     */       }
/* 123 */       if (fi != null) {
/* 124 */         FileInfo fi2 = this.image[i].getOriginalFileInfo();
/* 125 */         if ((fi2 != null) && (!fi.directory.equals(fi2.directory)))
/* 126 */           fi = null;
/*     */       }
/* 128 */       switch (this.stackType) { case 16:
/* 129 */         ip = ip.convertToShort(false); break;
/*     */       case 32:
/* 130 */         ip = ip.convertToFloat(); break;
/*     */       case 33:
/* 131 */         ip = ip.convertToRGB(); break;
/*     */       }
/*     */ 
/* 134 */       if ((ip.getWidth() != this.width) || (ip.getHeight() != this.height)) {
/* 135 */         switch (method) { case 0:
/*     */         case 1:
/* 137 */           ImageProcessor ip2 = null;
/* 138 */           switch (this.stackType) { case 8:
/* 139 */             ip2 = new ByteProcessor(this.width, this.height); break;
/*     */           case 16:
/* 140 */             ip2 = new ShortProcessor(this.width, this.height); break;
/*     */           case 32:
/* 141 */             ip2 = new FloatProcessor(this.width, this.height); break;
/*     */           case 33:
/* 142 */             ip2 = new ColorProcessor(this.width, this.height);
/*     */           }
/* 144 */           int xoff = 0; int yoff = 0;
/* 145 */           if (method == 0) {
/* 146 */             xoff = (this.width - ip.getWidth()) / 2;
/* 147 */             yoff = (this.height - ip.getHeight()) / 2;
/*     */           }
/* 149 */           ip2.insert(ip, xoff, yoff);
/* 150 */           ip = ip2;
/* 151 */           break;
/*     */         case 2:
/*     */         case 3:
/* 153 */           ip.setInterpolationMethod(bicubic ? 2 : 1);
/* 154 */           ip.resetRoi();
/* 155 */           ip = ip.resize(this.width, this.height);
/*     */         }
/*     */       }
/* 158 */       else if (keep)
/* 159 */         ip = ip.duplicate();
/* 160 */       stack.addSlice(label, ip);
/* 161 */       if (!keep) {
/* 162 */         this.image[i].changes = false;
/* 163 */         this.image[i].close();
/*     */       }
/*     */     }
/* 166 */     if (stack.getSize() == 0) return;
/* 167 */     ImagePlus imp = new ImagePlus(this.name, stack);
/* 168 */     if ((this.stackType == 16) || (this.stackType == 32))
/* 169 */       imp.getProcessor().setMinAndMax(min, max);
/* 170 */     if (this.cal2 != null)
/* 171 */       imp.setCalibration(this.cal2);
/* 172 */     if (fi != null) {
/* 173 */       fi.fileName = "";
/* 174 */       fi.nImages = imp.getStackSize();
/* 175 */       imp.setFileInfo(fi);
/*     */     }
/* 177 */     imp.show();
/*     */   }
/*     */ 
/*     */   final int findMinMaxSize(int count) {
/* 181 */     int index = 0;
/* 182 */     this.stackType = 8;
/* 183 */     this.width = 0;
/* 184 */     this.height = 0;
/* 185 */     this.cal2 = this.image[0].getCalibration();
/* 186 */     this.maxWidth = 0;
/* 187 */     this.maxHeight = 0;
/* 188 */     this.minWidth = 2147483647;
/* 189 */     this.minHeight = 2147483647;
/* 190 */     this.minSize = 2147483647;
/* 191 */     this.maxSize = 0;
/* 192 */     for (int i = 0; i < count; i++)
/* 193 */       if (!exclude(this.image[i].getTitle())) {
/* 194 */         if (this.image[i].getType() == 3)
/* 195 */           this.stackType = 33;
/* 196 */         int type = this.image[i].getBitDepth();
/* 197 */         if (type == 24) type = 33;
/* 198 */         if (type > this.stackType) this.stackType = type;
/* 199 */         int w = this.image[i].getWidth(); int h = this.image[i].getHeight();
/* 200 */         if (w > this.width) this.width = w;
/* 201 */         if (h > this.height) this.height = h;
/* 202 */         int size = w * h;
/* 203 */         if (size < this.minSize) {
/* 204 */           this.minSize = size;
/* 205 */           this.minWidth = w;
/* 206 */           this.minHeight = h;
/*     */         }
/* 208 */         if (size > this.maxSize) {
/* 209 */           this.maxSize = size;
/* 210 */           this.maxWidth = w;
/* 211 */           this.maxHeight = h;
/*     */         }
/* 213 */         Calibration cal = this.image[i].getCalibration();
/* 214 */         if (!this.image[i].getCalibration().equals(this.cal2))
/* 215 */           this.cal2 = null;
/* 216 */         this.image[(index++)] = this.image[i];
/*     */       }
/* 218 */     return index;
/*     */   }
/*     */ 
/*     */   final boolean exclude(String title) {
/* 222 */     return (this.filter != null) && (title != null) && (title.indexOf(this.filter) == -1);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ImagesToStack
 * JD-Core Version:    0.6.2
 */