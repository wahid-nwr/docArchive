/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.io.FileInfo;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ 
/*     */ class FitsDecoder
/*     */ {
/*     */   private String directory;
/*     */   private String fileName;
/*     */   private DataInputStream f;
/*  56 */   private StringBuffer info = new StringBuffer(512);
/*     */   double bscale;
/*     */   double bzero;
/*     */ 
/*     */   public FitsDecoder(String directory, String fileName)
/*     */   {
/*  60 */     this.directory = directory;
/*  61 */     this.fileName = fileName;
/*     */   }
/*     */ 
/*     */   FileInfo getInfo() throws IOException {
/*  65 */     FileInfo fi = new FileInfo();
/*  66 */     fi.fileFormat = 4;
/*  67 */     fi.fileName = this.fileName;
/*  68 */     fi.directory = this.directory;
/*  69 */     fi.width = 0;
/*  70 */     fi.height = 0;
/*  71 */     fi.offset = 0;
/*     */ 
/*  73 */     InputStream is = new FileInputStream(this.directory + this.fileName);
/*  74 */     if (this.fileName.toLowerCase().endsWith(".gz")) is = new GZIPInputStream(is);
/*  75 */     this.f = new DataInputStream(is);
/*  76 */     String line = getString(80);
/*  77 */     this.info.append(line + "\n");
/*  78 */     if (!line.startsWith("SIMPLE")) {
/*  79 */       this.f.close(); return null;
/*  80 */     }int count = 1;
/*     */     while (true) {
/*  82 */       count++;
/*  83 */       line = getString(80);
/*  84 */       this.info.append(line + "\n");
/*     */ 
/*  87 */       int index = line.indexOf("=");
/*     */ 
/*  90 */       int commentIndex = line.indexOf("/", index);
/*  91 */       if (commentIndex < 0)
/*  92 */         commentIndex = line.length();
/*     */       String value;
/*     */       String key;
/*     */       String value;
/*  97 */       if (index >= 0) {
/*  98 */         String key = line.substring(0, index).trim();
/*  99 */         value = line.substring(index + 1, commentIndex).trim();
/*     */       } else {
/* 101 */         key = line.trim();
/* 102 */         value = "";
/*     */       }
/*     */ 
/* 106 */       if (key.equals("END")) {
/*     */         break;
/*     */       }
/* 109 */       if (key.equals("BITPIX")) {
/* 110 */         int bitsPerPixel = Integer.parseInt(value);
/* 111 */         if (bitsPerPixel == 8) {
/* 112 */           fi.fileType = 0;
/* 113 */         } else if (bitsPerPixel == 16) {
/* 114 */           fi.fileType = 1;
/* 115 */         } else if (bitsPerPixel == 32) {
/* 116 */           fi.fileType = 3;
/* 117 */         } else if (bitsPerPixel == -32) {
/* 118 */           fi.fileType = 4;
/* 119 */         } else if (bitsPerPixel == -64) {
/* 120 */           fi.fileType = 16;
/*     */         } else {
/* 122 */           IJ.error("BITPIX must be 8, 16, 32, -32 (float) or -64 (double).");
/* 123 */           this.f.close();
/* 124 */           return null;
/*     */         }
/* 126 */       } else if (key.equals("NAXIS1")) {
/* 127 */         fi.width = Integer.parseInt(value);
/* 128 */       } else if (key.equals("NAXIS2")) {
/* 129 */         fi.height = Integer.parseInt(value);
/* 130 */       } else if (key.equals("NAXIS3")) {
/* 131 */         fi.nImages = Integer.parseInt(value);
/* 132 */       } else if (key.equals("BSCALE")) {
/* 133 */         this.bscale = parseDouble(value);
/* 134 */       } else if (key.equals("BZERO")) {
/* 135 */         this.bzero = parseDouble(value);
/*     */       }
/* 137 */       if ((count > 360) && (fi.width == 0)) {
/* 138 */         this.f.close(); return null;
/*     */       }
/*     */     }
/* 141 */     this.f.close();
/* 142 */     fi.offset = (2880 + 2880 * ((count * 80 - 1) / 2880));
/* 143 */     return fi;
/*     */   }
/*     */ 
/*     */   String getString(int length) throws IOException {
/* 147 */     byte[] b = new byte[length];
/* 148 */     this.f.readFully(b);
/* 149 */     return new String(b);
/*     */   }
/*     */ 
/*     */   int getInteger(String s) {
/* 153 */     s = s.substring(10, 30);
/* 154 */     s = s.trim();
/* 155 */     return Integer.parseInt(s);
/*     */   }
/*     */ 
/*     */   double parseDouble(String s) throws NumberFormatException {
/* 159 */     Double d = new Double(s);
/* 160 */     return d.doubleValue();
/*     */   }
/*     */ 
/*     */   String getHeaderInfo() {
/* 164 */     return new String(this.info);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.FitsDecoder
 * JD-Core Version:    0.6.2
 */