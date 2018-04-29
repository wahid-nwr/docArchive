/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.VirtualStack;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class ListVirtualStack extends VirtualStack
/*     */   implements PlugIn
/*     */ {
/*     */   private static boolean virtual;
/*     */   private String[] list;
/*     */   private String[] labels;
/*     */   private int nImages;
/*     */   private int imageWidth;
/*     */   private int imageHeight;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  21 */     OpenDialog od = new OpenDialog("Open Image List", arg);
/*  22 */     String name = od.getFileName();
/*  23 */     if (name == null) return;
/*  24 */     String dir = od.getDirectory();
/*     */ 
/*  26 */     this.list = open(dir + name);
/*  27 */     if (this.list == null) return;
/*  28 */     this.nImages = this.list.length;
/*  29 */     this.labels = new String[this.nImages];
/*     */ 
/*  32 */     if (this.list.length == 0) {
/*  33 */       IJ.error("Stack From List", "The file path list is empty");
/*  34 */       return;
/*     */     }
/*  36 */     if (!this.list[0].startsWith("http://")) {
/*  37 */       File f = new File(this.list[0]);
/*  38 */       if (!f.exists()) {
/*  39 */         IJ.error("Stack From List", "The first file on the list does not exist:\n \n" + this.list[0]);
/*  40 */         return;
/*     */       }
/*     */     }
/*  43 */     ImagePlus imp = IJ.openImage(this.list[0]);
/*  44 */     if (imp == null) return;
/*  45 */     this.imageWidth = imp.getWidth();
/*  46 */     this.imageHeight = imp.getHeight();
/*  47 */     setBitDepth(imp.getBitDepth());
/*  48 */     ImageStack stack = this;
/*  49 */     if (!showDialog(imp)) return;
/*  50 */     if (!virtual)
/*  51 */       stack = convertToRealStack(imp);
/*  52 */     ImagePlus imp2 = new ImagePlus(name, stack);
/*  53 */     imp2.setCalibration(imp.getCalibration());
/*  54 */     imp2.show();
/*     */   }
/*     */ 
/*     */   boolean showDialog(ImagePlus imp) {
/*  58 */     double bytesPerPixel = 1.0D;
/*  59 */     switch (imp.getType()) {
/*     */     case 1:
/*  61 */       bytesPerPixel = 2.0D; break;
/*     */     case 2:
/*     */     case 4:
/*  64 */       bytesPerPixel = 4.0D;
/*     */     case 3:
/*  66 */     }double size = this.imageWidth * this.imageHeight * bytesPerPixel / 1048576.0D;
/*  67 */     int digits = size * getSize() < 10.0D ? 1 : 0;
/*  68 */     String size1 = IJ.d2s(size * getSize(), digits) + " MB";
/*  69 */     String size2 = IJ.d2s(size, 1) + " MB";
/*  70 */     GenericDialog gd = new GenericDialog("Open Stack From List");
/*  71 */     gd.addCheckbox("Use Virtual Stack", virtual);
/*  72 */     gd.addMessage("This " + this.imageWidth + "x" + this.imageHeight + "x" + getSize() + " stack will require " + size1 + ",\n or " + size2 + " if opened as a virtual stack.");
/*  73 */     gd.showDialog();
/*  74 */     if (gd.wasCanceled()) return false;
/*  75 */     virtual = gd.getNextBoolean();
/*  76 */     return true;
/*     */   }
/*     */ 
/*     */   ImageStack convertToRealStack(ImagePlus imp) {
/*  80 */     ImageStack stack2 = new ImageStack(this.imageWidth, this.imageHeight, imp.getProcessor().getColorModel());
/*  81 */     int n = getSize();
/*  82 */     for (int i = 1; i <= getSize(); i++) {
/*  83 */       IJ.showProgress(i, n);
/*  84 */       IJ.showStatus("Opening: " + i + "/" + n);
/*  85 */       ImageProcessor ip2 = getProcessor(i);
/*  86 */       if (ip2 != null)
/*  87 */         stack2.addSlice(getSliceLabel(i), ip2);
/*     */     }
/*  89 */     return stack2;
/*     */   }
/*     */ 
/*     */   String[] open(String path) {
/*  93 */     if (path.startsWith("http://"))
/*  94 */       return openUrl(path);
/*  95 */     Vector v = new Vector();
/*  96 */     File file = new File(path);
/*     */     try {
/*  98 */       BufferedReader r = new BufferedReader(new FileReader(file));
/*     */       while (true) {
/* 100 */         String s = r.readLine();
/* 101 */         if ((s == null) || (s.equals("")) || (s.startsWith(" "))) {
/*     */           break;
/*     */         }
/* 104 */         v.addElement(s);
/*     */       }
/* 106 */       r.close();
/* 107 */       String[] list = new String[v.size()];
/* 108 */       v.copyInto((String[])list);
/* 109 */       return list;
/*     */     }
/*     */     catch (Exception e) {
/* 112 */       IJ.error("Open List Error \n\"" + e.getMessage() + "\"\n");
/*     */     }
/* 114 */     return null;
/*     */   }
/*     */ 
/*     */   String[] openUrl(String url) {
/* 118 */     String str = IJ.openUrlAsString(url);
/* 119 */     if (str.startsWith("<Error: ")) {
/* 120 */       IJ.error("Stack From List", str);
/* 121 */       return null;
/*     */     }
/* 123 */     return Tools.split(str, "\n");
/*     */   }
/*     */ 
/*     */   public void deleteSlice(int n)
/*     */   {
/* 128 */     if ((n < 1) || (n > this.nImages))
/* 129 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 130 */     if (this.nImages < 1) return;
/* 131 */     for (int i = n; i < this.nImages; i++)
/* 132 */       this.list[(i - 1)] = this.list[i];
/* 133 */     this.list[(this.nImages - 1)] = null;
/* 134 */     this.nImages -= 1;
/*     */   }
/*     */ 
/*     */   public ImageProcessor getProcessor(int n)
/*     */   {
/* 141 */     if ((n < 1) || (n > this.nImages))
/* 142 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 143 */     IJ.redirectErrorMessages();
/* 144 */     String url = this.list[(n - 1)];
/* 145 */     ImagePlus imp = null;
/* 146 */     if (url.length() > 0)
/* 147 */       imp = IJ.openImage(url);
/* 148 */     if (imp != null) {
/* 149 */       this.labels[(n - 1)] = (new File(this.list[(n - 1)]).getName() + "\n" + (String)imp.getProperty("Info"));
/* 150 */       ImageProcessor ip = imp.getProcessor();
/* 151 */       int bitDepth = getBitDepth();
/* 152 */       if (imp.getBitDepth() != bitDepth) {
/* 153 */         switch (bitDepth) { case 8:
/* 154 */           ip = ip.convertToByte(true); break;
/*     */         case 16:
/* 155 */           ip = ip.convertToShort(true); break;
/*     */         case 24:
/* 156 */           ip = ip.convertToRGB(); break;
/*     */         case 32:
/* 157 */           ip = ip.convertToFloat();
/*     */         }
/*     */       }
/* 160 */       if ((ip.getWidth() != this.imageWidth) || (ip.getHeight() != this.imageHeight))
/* 161 */         ip = ip.resize(this.imageWidth, this.imageHeight);
/* 162 */       return ip;
/*     */     }
/* 164 */     ImageProcessor ip = null;
/* 165 */     switch (getBitDepth()) { case 8:
/* 166 */       ip = new ByteProcessor(this.imageWidth, this.imageHeight); break;
/*     */     case 16:
/* 167 */       ip = new ShortProcessor(this.imageWidth, this.imageHeight); break;
/*     */     case 24:
/* 168 */       ip = new ColorProcessor(this.imageWidth, this.imageHeight); break;
/*     */     case 32:
/* 169 */       ip = new FloatProcessor(this.imageWidth, this.imageHeight);
/*     */     }
/* 171 */     return ip;
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 177 */     return this.nImages;
/*     */   }
/*     */ 
/*     */   public String getSliceLabel(int n)
/*     */   {
/* 182 */     if ((n < 1) || (n > this.nImages))
/* 183 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 184 */     if (this.labels[(n - 1)] != null) {
/* 185 */       return this.labels[(n - 1)];
/*     */     }
/* 187 */     return new File(this.list[(n - 1)]).getName();
/*     */   }
/*     */ 
/*     */   public int getWidth() {
/* 191 */     return this.imageWidth;
/*     */   }
/*     */ 
/*     */   public int getHeight() {
/* 195 */     return this.imageHeight;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ListVirtualStack
 * JD-Core Version:    0.6.2
 */