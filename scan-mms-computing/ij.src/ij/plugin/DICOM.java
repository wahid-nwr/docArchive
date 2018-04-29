/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.io.FileInfo;
/*     */ import ij.io.FileOpener;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class DICOM extends ImagePlus
/*     */   implements PlugIn
/*     */ {
/*  55 */   private boolean showErrors = true;
/*     */   private boolean gettingInfo;
/*     */   private BufferedInputStream inputStream;
/*     */   private String info;
/*     */ 
/*     */   public DICOM()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DICOM(InputStream is)
/*     */   {
/*  73 */     this(new BufferedInputStream(is));
/*     */   }
/*     */ 
/*     */   public DICOM(BufferedInputStream bis)
/*     */   {
/*  78 */     this.inputStream = bis;
/*     */   }
/*     */ 
/*     */   public void run(String arg) {
/*  82 */     OpenDialog od = new OpenDialog("Open Dicom...", arg);
/*  83 */     String directory = od.getDirectory();
/*  84 */     String fileName = od.getFileName();
/*  85 */     if (fileName == null) {
/*  86 */       return;
/*     */     }
/*  88 */     DicomDecoder dd = new DicomDecoder(directory, fileName);
/*  89 */     dd.inputStream = this.inputStream;
/*  90 */     FileInfo fi = null;
/*     */     try {
/*  92 */       fi = dd.getFileInfo();
/*     */     } catch (IOException e) {
/*  94 */       String msg = e.getMessage();
/*  95 */       IJ.showStatus("");
/*  96 */       if ((msg.indexOf("EOF") < 0) && (this.showErrors)) {
/*  97 */         IJ.error("DicomDecoder", e.getClass().getName() + "\n \n" + msg);
/*  98 */         return;
/*  99 */       }if ((!dd.dicmFound()) && (this.showErrors)) {
/* 100 */         msg = "This does not appear to be a valid\nDICOM file. It does not have the\ncharacters 'DICM' at offset 128.";
/*     */ 
/* 103 */         IJ.error("DicomDecoder", msg);
/* 104 */         return;
/*     */       }
/*     */     }
/* 107 */     if (this.gettingInfo) {
/* 108 */       this.info = dd.getDicomInfo();
/* 109 */       return;
/*     */     }
/* 111 */     if ((fi != null) && (fi.width > 0) && (fi.height > 0) && (fi.offset > 0)) {
/* 112 */       FileOpener fo = new FileOpener(fi);
/* 113 */       ImagePlus imp = fo.open(false);
/* 114 */       ImageProcessor ip = imp.getProcessor();
/* 115 */       if (Prefs.openDicomsAsFloat) {
/* 116 */         ip = ip.convertToFloat();
/* 117 */         if (dd.rescaleSlope != 1.0D)
/* 118 */           ip.multiply(dd.rescaleSlope);
/* 119 */         if (dd.rescaleIntercept != 0.0D)
/* 120 */           ip.add(dd.rescaleIntercept);
/* 121 */         imp.setProcessor(ip);
/* 122 */       } else if (fi.fileType == 1) {
/* 123 */         if ((dd.rescaleIntercept != 0.0D) && (dd.rescaleSlope == 1.0D))
/* 124 */           ip.add(dd.rescaleIntercept);
/* 125 */       } else if ((dd.rescaleIntercept != 0.0D) && ((dd.rescaleSlope == 1.0D) || (fi.fileType == 0))) {
/* 126 */         double[] coeff = new double[2];
/* 127 */         coeff[0] = dd.rescaleIntercept;
/* 128 */         coeff[1] = dd.rescaleSlope;
/* 129 */         imp.getCalibration().setFunction(0, coeff, "Gray Value");
/*     */       }
/* 131 */       if (dd.windowWidth > 0.0D) {
/* 132 */         double min = dd.windowCenter - dd.windowWidth / 2.0D;
/* 133 */         double max = dd.windowCenter + dd.windowWidth / 2.0D;
/* 134 */         if (Prefs.openDicomsAsFloat) {
/* 135 */           min -= dd.rescaleIntercept;
/* 136 */           max -= dd.rescaleIntercept;
/*     */         } else {
/* 138 */           Calibration cal = imp.getCalibration();
/* 139 */           min = cal.getRawValue(min);
/* 140 */           max = cal.getRawValue(max);
/*     */         }
/* 142 */         ip.setMinAndMax(min, max);
/* 143 */         if (IJ.debugMode) IJ.log("window: " + min + "-" + max);
/*     */       }
/* 145 */       if (imp.getStackSize() > 1)
/* 146 */         setStack(fileName, imp.getStack());
/*     */       else
/* 148 */         setProcessor(fileName, imp.getProcessor());
/* 149 */       setCalibration(imp.getCalibration());
/* 150 */       setProperty("Info", dd.getDicomInfo());
/* 151 */       setFileInfo(fi);
/* 152 */       if (arg.equals("")) show(); 
/*     */     }
/* 153 */     else if (this.showErrors) {
/* 154 */       IJ.error("DicomDecoder", "Unable to decode DICOM header.");
/* 155 */     }IJ.showStatus("");
/*     */   }
/*     */ 
/*     */   public void open(String path)
/*     */   {
/* 171 */     this.showErrors = false;
/* 172 */     run(path);
/*     */   }
/*     */ 
/*     */   public String getInfo(String path)
/*     */   {
/* 177 */     this.showErrors = false;
/* 178 */     this.gettingInfo = true;
/* 179 */     run(path);
/* 180 */     return this.info;
/*     */   }
/*     */ 
/*     */   void convertToUnsigned(ImagePlus imp, FileInfo fi)
/*     */   {
/* 185 */     ImageProcessor ip = imp.getProcessor();
/* 186 */     short[] pixels = (short[])ip.getPixels();
/* 187 */     int min = 2147483647;
/*     */ 
/* 189 */     for (int i = 0; i < pixels.length; i++) {
/* 190 */       int value = pixels[i] & 0xFFFF;
/* 191 */       if (value < min)
/* 192 */         min = value;
/*     */     }
/* 194 */     if (IJ.debugMode) IJ.log("min: " + (min - 32768));
/* 195 */     if (min >= 32768) {
/* 196 */       for (int i = 0; i < pixels.length; i++)
/* 197 */         pixels[i] = ((short)(pixels[i] - 32768));
/* 198 */       ip.resetMinAndMax();
/* 199 */       Calibration cal = imp.getCalibration();
/* 200 */       cal.setFunction(20, null, "Gray Value");
/* 201 */       fi.fileType = 2;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.DICOM
 * JD-Core Version:    0.6.2
 */