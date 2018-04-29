/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.VirtualStack;
/*     */ import ij.io.FileInfo;
/*     */ import ij.io.FileOpener;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.io.TiffDecoder;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ import java.io.IOException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class FileInfoVirtualStack extends VirtualStack
/*     */   implements PlugIn
/*     */ {
/*     */   FileInfo[] info;
/*     */   int nImages;
/*     */ 
/*     */   public FileInfoVirtualStack()
/*     */   {
/*     */   }
/*     */ 
/*     */   public FileInfoVirtualStack(FileInfo fi)
/*     */   {
/*  21 */     this.info = new FileInfo[1];
/*  22 */     this.info[0] = fi;
/*  23 */     open(true);
/*     */   }
/*     */ 
/*     */   public FileInfoVirtualStack(FileInfo fi, boolean show)
/*     */   {
/*  29 */     this.info = new FileInfo[1];
/*  30 */     this.info[0] = fi;
/*  31 */     open(show);
/*     */   }
/*     */ 
/*     */   public void run(String arg) {
/*  35 */     OpenDialog od = new OpenDialog("Open TIFF", arg);
/*  36 */     String name = od.getFileName();
/*  37 */     if (name == null) return;
/*  38 */     if (name.endsWith(".zip")) {
/*  39 */       IJ.error("Virtual Stack", "ZIP compressed stacks not supported");
/*  40 */       return;
/*     */     }
/*  42 */     String dir = od.getDirectory();
/*  43 */     TiffDecoder td = new TiffDecoder(dir, name);
/*  44 */     if (IJ.debugMode) td.enableDebugging();
/*  45 */     IJ.showStatus("Decoding TIFF header...");
/*     */     try { this.info = td.getTiffInfo();
/*     */     } catch (IOException e) {
/*  48 */       String msg = e.getMessage();
/*  49 */       if ((msg == null) || (msg.equals(""))) msg = "" + e;
/*  50 */       IJ.error("TiffDecoder", msg);
/*  51 */       return;
/*     */     }
/*  53 */     if ((this.info == null) || (this.info.length == 0)) {
/*  54 */       IJ.error("Virtual Stack", "This does not appear to be a TIFF stack");
/*  55 */       return;
/*     */     }
/*  57 */     if (IJ.debugMode)
/*  58 */       IJ.log(this.info[0].debugInfo);
/*  59 */     open(true);
/*     */   }
/*     */ 
/*     */   void open(boolean show) {
/*  63 */     FileInfo fi = this.info[0];
/*  64 */     int n = fi.nImages;
/*  65 */     if ((this.info.length == 1) && (n > 1)) {
/*  66 */       this.info = new FileInfo[n];
/*  67 */       long size = fi.width * fi.height * fi.getBytesPerPixel();
/*  68 */       for (int i = 0; i < n; i++) {
/*  69 */         this.info[i] = ((FileInfo)fi.clone());
/*  70 */         this.info[i].nImages = 1;
/*  71 */         this.info[i].longOffset = (fi.getOffset() + i * (size + fi.gapBetweenImages));
/*     */       }
/*     */     }
/*  74 */     this.nImages = this.info.length;
/*  75 */     FileOpener fo = new FileOpener(this.info[0]);
/*  76 */     ImagePlus imp = fo.open(false);
/*  77 */     Properties props = fo.decodeDescriptionString(fi);
/*  78 */     ImagePlus imp2 = new ImagePlus(fi.fileName, this);
/*  79 */     imp2.setFileInfo(fi);
/*  80 */     if ((imp != null) && (props != null)) {
/*  81 */       setBitDepth(imp.getBitDepth());
/*  82 */       imp2.setCalibration(imp.getCalibration());
/*  83 */       imp2.setOverlay(imp.getOverlay());
/*  84 */       if (fi.info != null)
/*  85 */         imp2.setProperty("Info", fi.info);
/*  86 */       int channels = getInt(props, "channels");
/*  87 */       int slices = getInt(props, "slices");
/*  88 */       int frames = getInt(props, "frames");
/*  89 */       if (channels * slices * frames == this.nImages) {
/*  90 */         imp2.setDimensions(channels, slices, frames);
/*  91 */         if (getBoolean(props, "hyperstack"))
/*  92 */           imp2.setOpenAsHyperStack(true);
/*     */       }
/*  94 */       if ((channels > 1) && (fi.description != null)) {
/*  95 */         int mode = 1;
/*  96 */         if (fi.description.indexOf("mode=color") != -1)
/*  97 */           mode = 2;
/*  98 */         else if (fi.description.indexOf("mode=gray") != -1)
/*  99 */           mode = 3;
/* 100 */         imp2 = new CompositeImage(imp2, mode);
/*     */       }
/*     */     }
/* 103 */     if (show) imp2.show(); 
/*     */   }
/*     */ 
/*     */   int getInt(Properties props, String key)
/*     */   {
/* 107 */     Double n = getNumber(props, key);
/* 108 */     return n != null ? (int)n.doubleValue() : 1;
/*     */   }
/*     */ 
/*     */   Double getNumber(Properties props, String key) {
/* 112 */     String s = props.getProperty(key);
/* 113 */     if (s != null)
/*     */       try {
/* 115 */         return Double.valueOf(s);
/*     */       } catch (NumberFormatException e) {
/*     */       }
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */   boolean getBoolean(Properties props, String key) {
/* 122 */     String s = props.getProperty(key);
/* 123 */     return (s != null) && (s.equals("true"));
/*     */   }
/*     */ 
/*     */   public void deleteSlice(int n)
/*     */   {
/* 128 */     if ((n < 1) || (n > this.nImages))
/* 129 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 130 */     if (this.nImages < 1) return;
/* 131 */     for (int i = n; i < this.nImages; i++)
/* 132 */       this.info[(i - 1)] = this.info[i];
/* 133 */     this.info[(this.nImages - 1)] = null;
/* 134 */     this.nImages -= 1;
/*     */   }
/*     */ 
/*     */   public ImageProcessor getProcessor(int n)
/*     */   {
/* 141 */     if ((n < 1) || (n > this.nImages))
/* 142 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 143 */     if (IJ.debugMode) IJ.log("FileInfoVirtualStack: " + n + ", " + this.info[(n - 1)].getOffset());
/*     */ 
/* 145 */     this.info[(n - 1)].nImages = 1;
/* 146 */     FileOpener fo = new FileOpener(this.info[(n - 1)]);
/* 147 */     ImagePlus imp = fo.open(false);
/* 148 */     if (imp != null) {
/* 149 */       return imp.getProcessor();
/*     */     }
/* 151 */     int w = getWidth(); int h = getHeight();
/* 152 */     IJ.log("Read error or file not found (" + n + "): " + this.info[(n - 1)].directory + this.info[(n - 1)].fileName);
/* 153 */     switch (getBitDepth()) { case 8:
/* 154 */       return new ByteProcessor(w, h);
/*     */     case 16:
/* 155 */       return new ShortProcessor(w, h);
/*     */     case 24:
/* 156 */       return new ColorProcessor(w, h);
/*     */     case 32:
/* 157 */       return new FloatProcessor(w, h); }
/* 158 */     return null;
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 165 */     return this.nImages;
/*     */   }
/*     */ 
/*     */   public String getSliceLabel(int n)
/*     */   {
/* 170 */     if ((n < 1) || (n > this.nImages))
/* 171 */       throw new IllegalArgumentException("Argument out of range: " + n);
/* 172 */     if ((this.info[0].sliceLabels == null) || (this.info[0].sliceLabels.length != this.nImages)) {
/* 173 */       return null;
/*     */     }
/* 175 */     return this.info[0].sliceLabels[(n - 1)];
/*     */   }
/*     */ 
/*     */   public int getWidth() {
/* 179 */     return this.info[0].width;
/*     */   }
/*     */ 
/*     */   public int getHeight() {
/* 183 */     return this.info[0].height;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.FileInfoVirtualStack
 * JD-Core Version:    0.6.2
 */