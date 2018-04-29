/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.StreamTokenizer;
/*     */ 
/*     */ public class TextReader
/*     */   implements PlugIn
/*     */ {
/*  15 */   int words = 0; int chars = 0; int lines = 0; int width = 1;
/*     */   String directory;
/*     */   String name;
/*     */   String path;
/*     */   boolean hideErrorMessages;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  20 */     if (showDialog()) {
/*  21 */       IJ.showStatus("Opening: " + this.path);
/*  22 */       ImageProcessor ip = open(this.path);
/*  23 */       if (ip != null)
/*  24 */         new ImagePlus(this.name, ip).show();
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean showDialog() {
/*  29 */     OpenDialog od = new OpenDialog("Open Text Image...", null);
/*  30 */     this.directory = od.getDirectory();
/*  31 */     this.name = od.getFileName();
/*  32 */     if (this.name != null)
/*  33 */       this.path = (this.directory + this.name);
/*  34 */     return this.name != null;
/*     */   }
/*     */ 
/*     */   public ImageProcessor open()
/*     */   {
/*  39 */     if (showDialog()) {
/*  40 */       return open(this.path);
/*     */     }
/*  42 */     return null;
/*     */   }
/*     */ 
/*     */   public ImageProcessor open(String path)
/*     */   {
/*  47 */     ImageProcessor ip = null;
/*     */     try {
/*  49 */       this.words = (this.chars = this.lines = 0);
/*  50 */       Reader r = new BufferedReader(new FileReader(path));
/*  51 */       countLines(r);
/*  52 */       r.close();
/*  53 */       r = new BufferedReader(new FileReader(path));
/*     */ 
/*  55 */       float[] pixels = new float[this.width * this.lines];
/*  56 */       ip = new FloatProcessor(this.width, this.lines, pixels, null);
/*  57 */       read(r, this.width * this.lines, pixels);
/*  58 */       r.close();
/*  59 */       ip.resetMinAndMax();
/*     */     }
/*     */     catch (IOException e) {
/*  62 */       String msg = e.getMessage();
/*  63 */       if ((msg == null) || (msg.equals("")))
/*  64 */         msg = "" + e;
/*  65 */       IJ.showProgress(1.0D);
/*  66 */       if (!this.hideErrorMessages)
/*  67 */         IJ.error("TextReader", msg);
/*  68 */       ip = null;
/*     */     }
/*  70 */     return ip;
/*     */   }
/*     */ 
/*     */   public void hideErrorMessages() {
/*  74 */     this.hideErrorMessages = true;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  79 */     return this.name;
/*     */   }
/*     */ 
/*     */   void countLines(Reader r) throws IOException {
/*  83 */     StreamTokenizer tok = new StreamTokenizer(r);
/*  84 */     int wordsPerLine = 0; int wordsInPreviousLine = 0;
/*  85 */     tok.resetSyntax();
/*  86 */     tok.wordChars(43, 43);
/*  87 */     tok.wordChars(45, 127);
/*  88 */     tok.whitespaceChars(0, 42);
/*  89 */     tok.whitespaceChars(44, 44);
/*     */ 
/*  92 */     tok.whitespaceChars(128, 255);
/*  93 */     tok.eolIsSignificant(true);
/*     */ 
/*  95 */     while (tok.nextToken() != -1) {
/*  96 */       switch (tok.ttype) {
/*     */       case 10:
/*  98 */         this.lines += 1;
/*  99 */         if (wordsPerLine == 0)
/* 100 */           this.lines -= 1;
/* 101 */         if (this.lines == 1)
/* 102 */           this.width = wordsPerLine;
/* 103 */         else if ((wordsPerLine != 0) && (wordsPerLine != wordsInPreviousLine))
/* 104 */           throw new IOException("Line " + this.lines + " is not the same length as the first line.");
/* 105 */         if (wordsPerLine != 0)
/* 106 */           wordsInPreviousLine = wordsPerLine;
/* 107 */         wordsPerLine = 0;
/* 108 */         if ((this.lines % 20 == 0) && (this.width > 1) && (this.lines <= this.width))
/* 109 */           IJ.showProgress(this.lines / this.width / 2.0D); break;
/*     */       case -3:
/* 112 */         this.words += 1;
/* 113 */         wordsPerLine++;
/*     */       }
/*     */     }
/*     */ 
/* 117 */     if (wordsPerLine == this.width)
/* 118 */       this.lines += 1;
/*     */   }
/*     */ 
/*     */   void read(Reader r, int size, float[] pixels) throws IOException {
/* 122 */     StreamTokenizer tok = new StreamTokenizer(r);
/* 123 */     tok.resetSyntax();
/* 124 */     tok.wordChars(43, 43);
/* 125 */     tok.wordChars(45, 127);
/* 126 */     tok.whitespaceChars(0, 42);
/* 127 */     tok.whitespaceChars(44, 44);
/*     */ 
/* 130 */     tok.whitespaceChars(128, 255);
/*     */ 
/* 133 */     int i = 0;
/* 134 */     int inc = size / 20;
/* 135 */     if (inc < 1)
/* 136 */       inc = 1;
/* 137 */     while (tok.nextToken() != -1) {
/* 138 */       if (tok.ttype == -3) {
/* 139 */         pixels[(i++)] = ((float)Tools.parseDouble(tok.sval, 0.0D));
/* 140 */         if (i == size)
/*     */           break;
/* 142 */         if (i % inc == 0)
/* 143 */           IJ.showProgress(0.5D + i / size / 2.0D);
/*     */       }
/*     */     }
/* 146 */     IJ.showProgress(1.0D);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.TextReader
 * JD-Core Version:    0.6.2
 */