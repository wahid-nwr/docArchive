/*     */ package ij.process;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class FloodFiller
/*     */ {
/*  13 */   int maxStackSize = 500;
/*  14 */   int[] xstack = new int[this.maxStackSize];
/*  15 */   int[] ystack = new int[this.maxStackSize];
/*     */   int stackSize;
/*     */   ImageProcessor ip;
/*     */   int max;
/*     */   boolean isFloat;
/* 133 */   int count = 0;
/*     */ 
/*     */   public FloodFiller(ImageProcessor ip)
/*     */   {
/*  22 */     this.ip = ip;
/*  23 */     this.isFloat = (ip instanceof FloatProcessor);
/*     */   }
/*     */ 
/*     */   public boolean fill(int x, int y)
/*     */   {
/*  29 */     int width = this.ip.getWidth();
/*  30 */     int height = this.ip.getHeight();
/*  31 */     int color = this.ip.getPixel(x, y);
/*  32 */     fillLine(this.ip, x, x, y);
/*  33 */     int newColor = this.ip.getPixel(x, y);
/*  34 */     this.ip.putPixel(x, y, color);
/*  35 */     if (color == newColor) return false;
/*  36 */     this.stackSize = 0;
/*  37 */     push(x, y);
/*     */     while (true) {
/*  39 */       x = popx();
/*  40 */       if (x == -1) return true;
/*  41 */       y = popy();
/*  42 */       if (this.ip.getPixel(x, y) == color) {
/*  43 */         int x1 = x; int x2 = x;
/*  44 */         while ((this.ip.getPixel(x1, y) == color) && (x1 >= 0)) x1--;
/*  45 */         x1++;
/*  46 */         while ((this.ip.getPixel(x2, y) == color) && (x2 < width)) x2++;
/*  47 */         x2--;
/*  48 */         fillLine(this.ip, x1, x2, y);
/*  49 */         boolean inScanLine = false;
/*  50 */         for (int i = x1; i <= x2; i++)
/*  51 */           if ((!inScanLine) && (y > 0) && (this.ip.getPixel(i, y - 1) == color)) {
/*  52 */             push(i, y - 1); inScanLine = true;
/*  53 */           } else if ((inScanLine) && (y > 0) && (this.ip.getPixel(i, y - 1) != color)) {
/*  54 */             inScanLine = false;
/*     */           }
/*  56 */         inScanLine = false;
/*  57 */         for (int i = x1; i <= x2; i++)
/*  58 */           if ((!inScanLine) && (y < height - 1) && (this.ip.getPixel(i, y + 1) == color)) {
/*  59 */             push(i, y + 1); inScanLine = true;
/*  60 */           } else if ((inScanLine) && (y < height - 1) && (this.ip.getPixel(i, y + 1) != color)) {
/*  61 */             inScanLine = false;
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean fill8(int x, int y)
/*     */   {
/*  69 */     int width = this.ip.getWidth();
/*  70 */     int height = this.ip.getHeight();
/*  71 */     int color = this.ip.getPixel(x, y);
/*  72 */     int wm1 = width - 1;
/*  73 */     int hm1 = height - 1;
/*  74 */     fillLine(this.ip, x, x, y);
/*  75 */     int newColor = this.ip.getPixel(x, y);
/*  76 */     this.ip.putPixel(x, y, color);
/*  77 */     if (color == newColor) return false;
/*  78 */     this.stackSize = 0;
/*  79 */     push(x, y);
/*     */     while (true) {
/*  81 */       x = popx();
/*  82 */       if (x == -1) return true;
/*  83 */       y = popy();
/*  84 */       int x1 = x; int x2 = x;
/*  85 */       if (this.ip.getPixel(x1, y) == color) {
/*  86 */         while ((this.ip.getPixel(x1, y) == color) && (x1 >= 0)) x1--;
/*  87 */         x1++;
/*  88 */         while ((this.ip.getPixel(x2, y) == color) && (x2 < width)) x2++;
/*  89 */         x2--;
/*  90 */         fillLine(this.ip, x1, x2, y);
/*     */       }
/*  92 */       if (y > 0) {
/*  93 */         if ((x1 > 0) && 
/*  94 */           (this.ip.getPixel(x1 - 1, y - 1) == color)) {
/*  95 */           push(x1 - 1, y - 1);
/*     */         }
/*     */ 
/*  98 */         if ((x2 < wm1) && 
/*  99 */           (this.ip.getPixel(x2 + 1, y - 1) == color)) {
/* 100 */           push(x2 + 1, y - 1);
/*     */         }
/*     */       }
/*     */ 
/* 104 */       if (y < hm1) {
/* 105 */         if ((x1 > 0) && 
/* 106 */           (this.ip.getPixel(x1 - 1, y + 1) == color)) {
/* 107 */           push(x1 - 1, y + 1);
/*     */         }
/*     */ 
/* 110 */         if ((x2 < wm1) && 
/* 111 */           (this.ip.getPixel(x2 + 1, y + 1) == color)) {
/* 112 */           push(x2 + 1, y + 1);
/*     */         }
/*     */       }
/*     */ 
/* 116 */       boolean inScanLine = false;
/* 117 */       for (int i = x1; i <= x2; i++)
/* 118 */         if ((!inScanLine) && (y > 0) && (this.ip.getPixel(i, y - 1) == color)) {
/* 119 */           push(i, y - 1); inScanLine = true;
/* 120 */         } else if ((inScanLine) && (y > 0) && (this.ip.getPixel(i, y - 1) != color)) {
/* 121 */           inScanLine = false;
/*     */         }
/* 123 */       inScanLine = false;
/* 124 */       for (int i = x1; i <= x2; i++)
/* 125 */         if ((!inScanLine) && (y < hm1) && (this.ip.getPixel(i, y + 1) == color)) {
/* 126 */           push(i, y + 1); inScanLine = true;
/* 127 */         } else if ((inScanLine) && (y < hm1) && (this.ip.getPixel(i, y + 1) != color)) {
/* 128 */           inScanLine = false;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void particleAnalyzerFill(int x, int y, double level1, double level2, ImageProcessor mask, Rectangle bounds)
/*     */   {
/* 138 */     int width = this.ip.getWidth();
/* 139 */     int height = this.ip.getHeight();
/* 140 */     mask.setColor(0);
/* 141 */     mask.fill();
/* 142 */     mask.setColor(255);
/* 143 */     this.stackSize = 0;
/* 144 */     push(x, y);
/*     */     while (true) {
/* 146 */       x = popx();
/* 147 */       if (x == -1) return;
/* 148 */       y = popy();
/* 149 */       if (inParticle(x, y, level1, level2)) {
/* 150 */         int x1 = x; int x2 = x;
/* 151 */         while ((inParticle(x1, y, level1, level2)) && (x1 >= 0)) x1--;
/* 152 */         x1++;
/* 153 */         while ((inParticle(x2, y, level1, level2)) && (x2 < width)) x2++;
/* 154 */         x2--;
/* 155 */         fillLine(mask, x1 - bounds.x, x2 - bounds.x, y - bounds.y);
/* 156 */         fillLine(this.ip, x1, x2, y);
/* 157 */         boolean inScanLine = false;
/* 158 */         if (x1 > 0) x1--; if (x2 < width - 1) x2++;
/* 159 */         for (int i = x1; i <= x2; i++)
/* 160 */           if ((!inScanLine) && (y > 0) && (inParticle(i, y - 1, level1, level2))) {
/* 161 */             push(i, y - 1); inScanLine = true;
/* 162 */           } else if ((inScanLine) && (y > 0) && (!inParticle(i, y - 1, level1, level2))) {
/* 163 */             inScanLine = false;
/*     */           }
/* 165 */         inScanLine = false;
/* 166 */         for (int i = x1; i <= x2; i++)
/* 167 */           if ((!inScanLine) && (y < height - 1) && (inParticle(i, y + 1, level1, level2))) {
/* 168 */             push(i, y + 1); inScanLine = true;
/* 169 */           } else if ((inScanLine) && (y < height - 1) && (!inParticle(i, y + 1, level1, level2))) {
/* 170 */             inScanLine = false;
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 176 */   final boolean inParticle(int x, int y, double level1, double level2) { if (this.isFloat) {
/* 177 */       return (this.ip.getPixelValue(x, y) >= level1) && (this.ip.getPixelValue(x, y) <= level2);
/*     */     }
/* 179 */     int v = this.ip.getPixel(x, y);
/* 180 */     return (v >= level1) && (v <= level2);
/*     */   }
/*     */ 
/*     */   final void push(int x, int y)
/*     */   {
/* 185 */     this.stackSize += 1;
/* 186 */     if (this.stackSize == this.maxStackSize) {
/* 187 */       int[] newXStack = new int[this.maxStackSize * 2];
/* 188 */       int[] newYStack = new int[this.maxStackSize * 2];
/* 189 */       System.arraycopy(this.xstack, 0, newXStack, 0, this.maxStackSize);
/* 190 */       System.arraycopy(this.ystack, 0, newYStack, 0, this.maxStackSize);
/* 191 */       this.xstack = newXStack;
/* 192 */       this.ystack = newYStack;
/* 193 */       this.maxStackSize *= 2;
/*     */     }
/* 195 */     this.xstack[(this.stackSize - 1)] = x;
/* 196 */     this.ystack[(this.stackSize - 1)] = y;
/*     */   }
/*     */ 
/*     */   final int popx() {
/* 200 */     if (this.stackSize == 0) {
/* 201 */       return -1;
/*     */     }
/* 203 */     return this.xstack[(this.stackSize - 1)];
/*     */   }
/*     */ 
/*     */   final int popy() {
/* 207 */     int value = this.ystack[(this.stackSize - 1)];
/* 208 */     this.stackSize -= 1;
/* 209 */     return value;
/*     */   }
/*     */ 
/*     */   final void fillLine(ImageProcessor ip, int x1, int x2, int y) {
/* 213 */     if (x1 > x2) { int t = x1; x1 = x2; x2 = t; }
/* 214 */     for (int x = x1; x <= x2; x++)
/* 215 */       ip.drawPixel(x, y);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.FloodFiller
 * JD-Core Version:    0.6.2
 */