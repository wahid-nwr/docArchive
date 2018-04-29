/*     */ package ij.process;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import java.awt.Color;
/*     */ 
/*     */ public class BinaryProcessor extends ByteProcessor
/*     */ {
/*   7 */   static int[] table = { 0, 0, 0, 1, 0, 0, 1, 3, 0, 0, 3, 1, 1, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 3, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 3, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 2, 0, 0, 0, 3, 1, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 1, 3, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 1, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0 };
/*     */   private ByteProcessor parent;
/*     */   static final int OUTLINE = 0;
/*     */ 
/*     */   public BinaryProcessor(ByteProcessor ip)
/*     */   {
/*  23 */     super(ip.getWidth(), ip.getHeight(), (byte[])ip.getPixels(), ip.getColorModel());
/*  24 */     setRoi(ip.getRoi());
/*  25 */     this.parent = ip;
/*     */   }
/*     */ 
/*     */   void process(int type, int count)
/*     */   {
/*  32 */     int inc = this.roiHeight / 25;
/*  33 */     if (inc < 1) inc = 1;
/*  34 */     int bgColor = 255;
/*  35 */     if (this.parent.isInvertedLut()) {
/*  36 */       bgColor = 0;
/*     */     }
/*  38 */     byte[] pixels2 = (byte[])this.parent.getPixelsCopy();
/*  39 */     int v = 0;
/*  40 */     int rowOffset = this.width;
/*  41 */     for (int y = this.yMin; y <= this.yMax; y++) {
/*  42 */       int offset = this.xMin + y * this.width;
/*  43 */       int p2 = pixels2[(offset - rowOffset - 1)] & 0xFF;
/*  44 */       int p3 = pixels2[(offset - rowOffset)] & 0xFF;
/*  45 */       int p5 = pixels2[(offset - 1)] & 0xFF;
/*  46 */       int p6 = pixels2[offset] & 0xFF;
/*  47 */       int p8 = pixels2[(offset + rowOffset - 1)] & 0xFF;
/*  48 */       int p9 = pixels2[(offset + rowOffset)] & 0xFF;
/*     */ 
/*  50 */       for (int x = this.xMin; x <= this.xMax; x++) {
/*  51 */         int p1 = p2; p2 = p3;
/*  52 */         p3 = pixels2[(offset - rowOffset + 1)] & 0xFF;
/*  53 */         int p4 = p5; p5 = p6;
/*  54 */         p6 = pixels2[(offset + 1)] & 0xFF;
/*  55 */         int p7 = p8; p8 = p9;
/*  56 */         p9 = pixels2[(offset + rowOffset + 1)] & 0xFF;
/*     */ 
/*  58 */         switch (type) {
/*     */         case 0:
/*  60 */           v = p5;
/*  61 */           if ((v != bgColor) && 
/*  62 */             (p1 != bgColor) && (p2 != bgColor) && (p3 != bgColor) && (p4 != bgColor) && (p6 != bgColor) && (p7 != bgColor) && (p8 != bgColor) && (p9 != bgColor))
/*     */           {
/*  64 */             v = bgColor;
/*     */           }
/*     */           break;
/*     */         }
/*     */ 
/*  69 */         this.pixels[(offset++)] = ((byte)v);
/*     */       }
/*  71 */       if (y % inc == 0)
/*  72 */         this.parent.showProgress((y - this.roiY) / this.roiHeight);
/*     */     }
/*  74 */     this.parent.hideProgress();
/*     */   }
/*     */ 
/*     */   public void skeletonize()
/*     */   {
/*  88 */     int pass = 0;
/*     */ 
/*  90 */     resetRoi();
/*  91 */     setColor(Color.white);
/*  92 */     moveTo(0, 0); lineTo(0, this.height - 1);
/*  93 */     moveTo(0, 0); lineTo(this.width - 1, 0);
/*  94 */     moveTo(this.width - 1, 0); lineTo(this.width - 1, this.height - 1);
/*  95 */     moveTo(0, this.height - 1); lineTo(this.width, this.height - 1);
/*  96 */     ImageStack movie = null;
/*  97 */     boolean debug = IJ.debugMode;
/*  98 */     if (debug) movie = new ImageStack(this.width, this.height); int pixelsRemoved;
/*     */     do {
/* 100 */       snapshot();
/* 101 */       if (debug) movie.addSlice("" + pass, duplicate());
/* 102 */       pixelsRemoved = thin(pass++, table);
/* 103 */       snapshot();
/* 104 */       if (debug) movie.addSlice("" + pass, duplicate());
/* 105 */       pixelsRemoved = thin(pass++, table);
/*     */     }
/* 107 */     while (pixelsRemoved > 0);
/* 108 */     if (debug) new ImagePlus("Skel Movie", movie).show();
/*     */   }
/*     */ 
/*     */   int thin(int pass, int[] table)
/*     */   {
/* 113 */     int inc = this.roiHeight / 25;
/* 114 */     if (inc < 1) inc = 1;
/* 115 */     int bgColor = 255;
/* 116 */     if (this.parent.isInvertedLut()) {
/* 117 */       bgColor = 0;
/*     */     }
/* 119 */     byte[] pixels2 = (byte[])getPixelsCopy();
/*     */ 
/* 121 */     int rowOffset = this.width;
/* 122 */     int pixelsRemoved = 0;
/* 123 */     int count = 100;
/* 124 */     for (int y = this.yMin; y <= this.yMax; y++) {
/* 125 */       int offset = this.xMin + y * this.width;
/* 126 */       for (int x = this.xMin; x <= this.xMax; x++) {
/* 127 */         int p5 = pixels2[offset] & 0xFF;
/* 128 */         int v = p5;
/* 129 */         if (v != bgColor) {
/* 130 */           int p1 = pixels2[(offset - rowOffset - 1)] & 0xFF;
/* 131 */           int p2 = pixels2[(offset - rowOffset)] & 0xFF;
/* 132 */           int p3 = pixels2[(offset - rowOffset + 1)] & 0xFF;
/* 133 */           int p4 = pixels2[(offset - 1)] & 0xFF;
/* 134 */           int p6 = pixels2[(offset + 1)] & 0xFF;
/* 135 */           int p7 = pixels2[(offset + rowOffset - 1)] & 0xFF;
/* 136 */           int p8 = pixels2[(offset + rowOffset)] & 0xFF;
/* 137 */           int p9 = pixels2[(offset + rowOffset + 1)] & 0xFF;
/* 138 */           int index = 0;
/* 139 */           if (p1 != bgColor) index |= 1;
/* 140 */           if (p2 != bgColor) index |= 2;
/* 141 */           if (p3 != bgColor) index |= 4;
/* 142 */           if (p6 != bgColor) index |= 8;
/* 143 */           if (p9 != bgColor) index |= 16;
/* 144 */           if (p8 != bgColor) index |= 32;
/* 145 */           if (p7 != bgColor) index |= 64;
/* 146 */           if (p4 != bgColor) index |= 128;
/* 147 */           int code = table[index];
/* 148 */           if ((pass & 0x1) == 1) {
/* 149 */             if ((code == 2) || (code == 3)) {
/* 150 */               v = bgColor;
/* 151 */               pixelsRemoved++;
/*     */             }
/*     */           }
/* 154 */           else if ((code == 1) || (code == 3)) {
/* 155 */             v = bgColor;
/* 156 */             pixelsRemoved++;
/*     */           }
/*     */         }
/*     */ 
/* 160 */         this.pixels[(offset++)] = ((byte)v);
/*     */       }
/* 162 */       if (y % inc == 0)
/* 163 */         showProgress((y - this.roiY) / this.roiHeight);
/*     */     }
/* 165 */     hideProgress();
/* 166 */     return pixelsRemoved;
/*     */   }
/*     */ 
/*     */   public void outline() {
/* 170 */     process(0, 0);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.BinaryProcessor
 * JD-Core Version:    0.6.2
 */