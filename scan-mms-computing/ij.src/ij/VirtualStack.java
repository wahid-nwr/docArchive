/*     */ package ij;
/*     */ 
/*     */ import ij.io.Opener;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Font;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.io.File;
/*     */ 
/*     */ public class VirtualStack extends ImageStack
/*     */ {
/*     */   private static final int INITIAL_SIZE = 100;
/*     */   private String path;
/*     */   private int nSlices;
/*     */   private String[] names;
/*     */   private String[] labels;
/*     */   private int bitDepth;
/*     */ 
/*     */   public VirtualStack()
/*     */   {
/*     */   }
/*     */ 
/*     */   public VirtualStack(int width, int height, ColorModel cm, String path)
/*     */   {
/*  24 */     super(width, height, cm);
/*  25 */     this.path = path;
/*  26 */     this.names = new String[100];
/*  27 */     this.labels = new String[100];
/*     */   }
/*     */ 
/*     */   public void addSlice(String name)
/*     */   {
/*  33 */     if (name == null)
/*  34 */       throw new IllegalArgumentException("'name' is null!");
/*  35 */     this.nSlices += 1;
/*     */ 
/*  37 */     if (this.nSlices == this.names.length) {
/*  38 */       String[] tmp = new String[this.nSlices * 2];
/*  39 */       System.arraycopy(this.names, 0, tmp, 0, this.nSlices);
/*  40 */       this.names = tmp;
/*  41 */       tmp = new String[this.nSlices * 2];
/*  42 */       System.arraycopy(this.labels, 0, tmp, 0, this.nSlices);
/*  43 */       this.labels = tmp;
/*     */     }
/*  45 */     this.names[(this.nSlices - 1)] = name;
/*     */   }
/*     */ 
/*     */   public void addSlice(String sliceLabel, Object pixels)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addSlice(String sliceLabel, ImageProcessor ip)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addSlice(String sliceLabel, ImageProcessor ip, int n)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void deleteSlice(int n)
/*     */   {
/*  62 */     if ((n < 1) || (n > this.nSlices))
/*  63 */       throw new IllegalArgumentException("Argument out of range: " + n);
/*  64 */     if (this.nSlices < 1)
/*  65 */       return;
/*  66 */     for (int i = n; i < this.nSlices; i++)
/*  67 */       this.names[(i - 1)] = this.names[i];
/*  68 */     this.names[(this.nSlices - 1)] = null;
/*  69 */     this.nSlices -= 1;
/*     */   }
/*     */ 
/*     */   public void deleteLastSlice()
/*     */   {
/*  74 */     if (this.nSlices > 0)
/*  75 */       deleteSlice(this.nSlices);
/*     */   }
/*     */ 
/*     */   public Object getPixels(int n)
/*     */   {
/*  80 */     ImageProcessor ip = getProcessor(n);
/*  81 */     if (ip != null) {
/*  82 */       return ip.getPixels();
/*     */     }
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPixels(Object pixels, int n)
/*     */   {
/*     */   }
/*     */ 
/*     */   public ImageProcessor getProcessor(int n)
/*     */   {
/*  97 */     Opener opener = new Opener();
/*  98 */     opener.setSilentMode(true);
/*  99 */     IJ.redirectErrorMessages(true);
/* 100 */     ImagePlus imp = opener.openImage(this.path, this.names[(n - 1)]);
/* 101 */     IJ.redirectErrorMessages(false);
/* 102 */     ImageProcessor ip = null;
/* 103 */     int depthThisImage = 0;
/* 104 */     if (imp != null) {
/* 105 */       int w = imp.getWidth();
/* 106 */       int h = imp.getHeight();
/* 107 */       int type = imp.getType();
/* 108 */       ColorModel cm = imp.getProcessor().getColorModel();
/* 109 */       this.labels[(n - 1)] = ((String)imp.getProperty("Info"));
/* 110 */       depthThisImage = imp.getBitDepth();
/* 111 */       ip = imp.getProcessor();
/*     */     } else {
/* 113 */       File f = new File(this.path, this.names[(n - 1)]);
/* 114 */       String msg = f.exists() ? "Error opening " : "File not found: ";
/* 115 */       ip = new ByteProcessor(getWidth(), getHeight());
/* 116 */       ip.invert();
/* 117 */       int size = getHeight() / 20;
/* 118 */       if (size < 9) size = 9;
/* 119 */       Font font = new Font("Helvetica", 0, size);
/* 120 */       ip.setFont(font);
/* 121 */       ip.setAntialiasedText(true);
/* 122 */       ip.setColor(0);
/* 123 */       ip.drawString(msg + this.names[(n - 1)], size, size * 2);
/* 124 */       depthThisImage = 8;
/*     */     }
/* 126 */     if (depthThisImage != this.bitDepth) {
/* 127 */       switch (this.bitDepth) { case 8:
/* 128 */         ip = ip.convertToByte(true); break;
/*     */       case 16:
/* 129 */         ip = ip.convertToShort(true); break;
/*     */       case 24:
/* 130 */         ip = ip.convertToRGB(); break;
/*     */       case 32:
/* 131 */         ip = ip.convertToFloat();
/*     */       }
/*     */     }
/* 134 */     if ((ip.getWidth() != getWidth()) || (ip.getHeight() != getHeight())) {
/* 135 */       ImageProcessor ip2 = ip.createProcessor(getWidth(), getHeight());
/* 136 */       ip2.insert(ip, 0, 0);
/* 137 */       ip = ip2;
/*     */     }
/* 139 */     return ip;
/*     */   }
/*     */ 
/*     */   public int saveChanges(int n)
/*     */   {
/* 144 */     return -1;
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 149 */     return this.nSlices;
/*     */   }
/*     */ 
/*     */   public String getSliceLabel(int n)
/*     */   {
/* 154 */     String label = this.labels[(n - 1)];
/* 155 */     if (label == null)
/* 156 */       return this.names[(n - 1)];
/* 157 */     if (label.length() <= 60) {
/* 158 */       return label;
/*     */     }
/* 160 */     return this.names[(n - 1)] + "\n" + label;
/*     */   }
/*     */ 
/*     */   public Object[] getImageArray()
/*     */   {
/* 165 */     return null;
/*     */   }
/*     */ 
/*     */   public void setSliceLabel(String label, int n)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isVirtual()
/*     */   {
/* 174 */     return true;
/*     */   }
/*     */ 
/*     */   public void trim()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getDirectory()
/*     */   {
/* 183 */     return this.path;
/*     */   }
/*     */ 
/*     */   public String getFileName(int n)
/*     */   {
/* 188 */     return this.names[(n - 1)];
/*     */   }
/*     */ 
/*     */   public void setBitDepth(int bitDepth)
/*     */   {
/* 193 */     this.bitDepth = bitDepth;
/*     */   }
/*     */ 
/*     */   public int getBitDepth()
/*     */   {
/* 198 */     return this.bitDepth;
/*     */   }
/*     */ 
/*     */   public ImageStack sortDicom(String[] strings, String[] info, int maxDigits) {
/* 202 */     int n = getSize();
/* 203 */     String[] names2 = new String[n];
/* 204 */     for (int i = 0; i < n; i++)
/* 205 */       names2[i] = this.names[i];
/* 206 */     for (int i = 0; i < n; i++) {
/* 207 */       int slice = (int)Tools.parseDouble(strings[i].substring(strings[i].length() - maxDigits), 0.0D);
/* 208 */       if (slice == 0) return null;
/* 209 */       this.names[i] = names2[(slice - 1)];
/* 210 */       this.labels[i] = info[(slice - 1)];
/*     */     }
/* 212 */     return this;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.VirtualStack
 * JD-Core Version:    0.6.2
 */