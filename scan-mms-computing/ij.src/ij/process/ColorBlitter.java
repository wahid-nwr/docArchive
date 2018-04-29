/*     */ package ij.process;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.ColorModel;
/*     */ 
/*     */ public class ColorBlitter
/*     */   implements Blitter
/*     */ {
/*     */   private ColorProcessor ip;
/*     */   private int width;
/*     */   private int height;
/*     */   private int[] pixels;
/*  11 */   private int transparent = 16777215;
/*     */ 
/*     */   public ColorBlitter(ColorProcessor ip)
/*     */   {
/*  15 */     this.ip = ip;
/*  16 */     this.width = ip.getWidth();
/*  17 */     this.height = ip.getHeight();
/*  18 */     this.pixels = ((int[])ip.getPixels());
/*     */   }
/*     */ 
/*     */   public void setTransparentColor(Color c) {
/*  22 */     this.transparent = (c.getRGB() & 0xFFFFFF);
/*     */   }
/*     */ 
/*     */   public void copyBits(ImageProcessor ip, int xloc, int yloc, int mode)
/*     */   {
/*  31 */     int srcWidth = ip.getWidth();
/*  32 */     int srcHeight = ip.getHeight();
/*  33 */     Rectangle rect1 = new Rectangle(srcWidth, srcHeight);
/*  34 */     rect1.setLocation(xloc, yloc);
/*  35 */     Rectangle rect2 = new Rectangle(this.width, this.height);
/*  36 */     if (!rect1.intersects(rect2))
/*     */       return;
/*     */     int[] srcPixels;
/*  38 */     if ((ip instanceof ByteProcessor)) {
/*  39 */       byte[] pixels8 = (byte[])ip.getPixels();
/*  40 */       ColorModel cm = ip.getColorModel();
/*  41 */       if (ip.isInvertedLut())
/*  42 */         cm = ip.getDefaultColorModel();
/*  43 */       int size = ip.getWidth() * ip.getHeight();
/*  44 */       int[] srcPixels = new int[size];
/*     */ 
/*  46 */       for (int i = 0; i < size; i++)
/*  47 */         srcPixels[i] = cm.getRGB(pixels8[i] & 0xFF);
/*     */     } else {
/*  49 */       srcPixels = (int[])ip.getPixels();
/*  50 */     }rect1 = rect1.intersection(rect2);
/*  51 */     int xSrcBase = xloc < 0 ? -xloc : 0;
/*  52 */     int ySrcBase = yloc < 0 ? -yloc : 0;
/*     */ 
/*  56 */     if ((mode == 0) || (mode == 2) || (mode == 14)) {
/*  57 */       for (int y = rect1.y; y < rect1.y + rect1.height; y++) {
/*  58 */         int srcIndex = (y - yloc) * srcWidth + (rect1.x - xloc);
/*  59 */         int dstIndex = y * this.width + rect1.x;
/*  60 */         int trancolor = mode == 14 ? 0 : this.transparent;
/*  61 */         if (mode == 0) {
/*  62 */           int i = rect1.width;
/*     */           while (true) { i--; if (i < 0) break;
/*  63 */             this.pixels[(dstIndex++)] = srcPixels[(srcIndex++)]; }
/*     */         } else {
/*  65 */           int i = rect1.width;
/*     */           while (true) { i--; if (i < 0) break;
/*  66 */             int src = srcPixels[(srcIndex++)];
/*  67 */             int dst = this.pixels[dstIndex];
/*  68 */             this.pixels[(dstIndex++)] = ((src & 0xFFFFFF) == trancolor ? dst : src);
/*     */           }
/*     */         }
/*     */       }
/*  72 */       return;
/*     */     }
/*     */ 
/*  75 */     for (int y = rect1.y; y < rect1.y + rect1.height; y++) {
/*  76 */       int srcIndex = (y - yloc) * srcWidth + (rect1.x - xloc);
/*  77 */       int dstIndex = y * this.width + rect1.x;
/*  78 */       int i = rect1.width;
/*     */       while (true) { i--; if (i < 0) break;
/*  79 */         int c1 = srcPixels[(srcIndex++)];
/*  80 */         int r1 = (c1 & 0xFF0000) >> 16;
/*  81 */         int g1 = (c1 & 0xFF00) >> 8;
/*  82 */         int b1 = c1 & 0xFF;
/*  83 */         int c2 = this.pixels[dstIndex];
/*  84 */         int r2 = (c2 & 0xFF0000) >> 16;
/*  85 */         int g2 = (c2 & 0xFF00) >> 8;
/*  86 */         int b2 = c2 & 0xFF;
/*  87 */         switch (mode) {
/*     */         case 1:
/*  89 */           break;
/*     */         case 3:
/*  91 */           r2 = r1 + r2; g2 = g1 + g2; b2 = b1 + b2;
/*  92 */           if (r2 > 255) r2 = 255; if (g2 > 255) g2 = 255; if (b2 > 255) b2 = 255; break;
/*     */         case 7:
/*  95 */           r2 = (r1 + r2) / 2; g2 = (g1 + g2) / 2; b2 = (b1 + b2) / 2;
/*  96 */           break;
/*     */         case 4:
/*  98 */           r2 -= r1; g2 -= g1; b2 -= b1;
/*  99 */           if (r2 < 0) r2 = 0; if (g2 < 0) g2 = 0; if (b2 < 0) b2 = 0; break;
/*     */         case 8:
/* 102 */           r2 -= r1; if (r2 < 0) r2 = -r2;
/* 103 */           g2 -= g1; if (g2 < 0) g2 = -g2;
/* 104 */           b2 -= b1; if (b2 < 0) b2 = -b2; break;
/*     */         case 5:
/* 107 */           r2 = r1 * r2; g2 = g1 * g2; b2 = b1 * b2;
/* 108 */           if (r2 > 255) r2 = 255; if (g2 > 255) g2 = 255; if (b2 > 255) b2 = 255; break;
/*     */         case 6:
/* 111 */           if (r1 == 0) r2 = 255; else r2 /= r1;
/* 112 */           if (g1 == 0) g2 = 255; else g2 /= g1;
/* 113 */           if (b1 == 0) b2 = 255; else b2 /= b1;
/* 114 */           break;
/*     */         case 9:
/* 116 */           r2 = r1 & r2; g2 = g1 & g2; b2 = b1 & b2;
/* 117 */           break;
/*     */         case 10:
/* 119 */           r2 = r1 | r2; g2 = g1 | g2; b2 = b1 | b2;
/* 120 */           break;
/*     */         case 11:
/* 122 */           r2 = r1 ^ r2; g2 = g1 ^ g2; b2 = b1 ^ b2;
/* 123 */           break;
/*     */         case 12:
/* 125 */           if (r1 < r2) r2 = r1;
/* 126 */           if (g1 < g2) g2 = g1;
/* 127 */           if (b1 < b2) b2 = b1; break;
/*     */         case 13:
/* 130 */           if (r1 > r2) r2 = r1;
/* 131 */           if (g1 > g2) g2 = g1;
/* 132 */           if (b1 > b2) b2 = b1; break;
/*     */         case 2:
/*     */         }
/* 135 */         this.pixels[(dstIndex++)] = (-16777216 + (r2 << 16) + (g2 << 8) + b2);
/*     */       }
/* 137 */       if (y % 20 == 0)
/* 138 */         ip.showProgress((y - rect1.y) / rect1.height);
/*     */     }
/* 140 */     ip.showProgress(1.0D);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ColorBlitter
 * JD-Core Version:    0.6.2
 */