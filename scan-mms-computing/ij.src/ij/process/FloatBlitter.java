/*     */ package ij.process;
/*     */ 
/*     */ import ij.Prefs;
/*     */ import java.awt.Color;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class FloatBlitter
/*     */   implements Blitter
/*     */ {
/*  15 */   public static float divideByZeroValue = (float)Prefs.getDouble("div-by-zero", (1.0D / 0.0D));
/*     */   private FloatProcessor ip;
/*     */   private int width;
/*     */   private int height;
/*     */   private float[] pixels;
/*     */ 
/*     */   public FloatBlitter(FloatProcessor ip)
/*     */   {
/*  22 */     this.ip = ip;
/*  23 */     this.width = ip.getWidth();
/*  24 */     this.height = ip.getHeight();
/*  25 */     this.pixels = ((float[])ip.getPixels());
/*     */   }
/*     */ 
/*     */   public void setTransparentColor(Color c)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void copyBits(ImageProcessor ip, int xloc, int yloc, int mode)
/*     */   {
/*  38 */     if (!(ip instanceof FloatProcessor))
/*  39 */       ip = ip.convertToFloat();
/*  40 */     int srcWidth = ip.getWidth();
/*  41 */     int srcHeight = ip.getHeight();
/*  42 */     Rectangle r1 = new Rectangle(srcWidth, srcHeight);
/*  43 */     r1.setLocation(xloc, yloc);
/*  44 */     Rectangle r2 = new Rectangle(this.width, this.height);
/*  45 */     if (!r1.intersects(r2))
/*  46 */       return;
/*  47 */     float[] srcPixels = (float[])ip.getPixels();
/*  48 */     r1 = r1.intersection(r2);
/*  49 */     int xSrcBase = xloc < 0 ? -xloc : 0;
/*  50 */     int ySrcBase = yloc < 0 ? -yloc : 0;
/*  51 */     boolean useDBZValue = !Float.isInfinite(divideByZeroValue);
/*     */ 
/*  53 */     for (int y = r1.y; y < r1.y + r1.height; y++) {
/*  54 */       int srcIndex = (y - yloc) * srcWidth + (r1.x - xloc);
/*  55 */       int dstIndex = y * this.width + r1.x;
/*  56 */       switch (mode) { case 0:
/*     */       case 1:
/*     */       case 2:
/*  58 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  59 */           this.pixels[(dstIndex++)] = srcPixels[(srcIndex++)]; }
/*  60 */         break;
/*     */       case 14:
/*  62 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  63 */           float src = srcPixels[(srcIndex++)];
/*     */           float dst;
/*     */           float dst;
/*  64 */           if (src == 0.0F)
/*  65 */             dst = this.pixels[dstIndex];
/*     */           else
/*  67 */             dst = src;
/*  68 */           this.pixels[(dstIndex++)] = dst;
/*     */         }
/*  70 */         break;
/*     */       case 3:
/*  72 */         for (int i = r1.width; ; dstIndex++) { i--; if (i < 0) break;
/*  73 */           this.pixels[dstIndex] = (srcPixels[srcIndex] + this.pixels[dstIndex]);
/*     */ 
/*  72 */           srcIndex++;
/*     */         }
/*  74 */         break;
/*     */       case 7:
/*  76 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  77 */           float dst = (srcPixels[(srcIndex++)] + this.pixels[dstIndex]) / 2.0F;
/*  78 */           this.pixels[(dstIndex++)] = dst;
/*     */         }
/*  80 */         break;
/*     */       case 8:
/*  82 */         for (int i = r1.width; ; dstIndex++) { i--; if (i < 0) break;
/*  83 */           float dst = this.pixels[dstIndex] - srcPixels[srcIndex];
/*  84 */           this.pixels[dstIndex] = (dst < 0.0F ? -dst : dst);
/*     */ 
/*  82 */           srcIndex++;
/*     */         }
/*     */ 
/*  86 */         break;
/*     */       case 4:
/*  88 */         for (int i = r1.width; ; dstIndex++) { i--; if (i < 0) break;
/*  89 */           this.pixels[dstIndex] -= srcPixels[srcIndex];
/*     */ 
/*  88 */           srcIndex++;
/*     */         }
/*  90 */         break;
/*     */       case 5:
/*  92 */         for (int i = r1.width; ; dstIndex++) { i--; if (i < 0) break;
/*  93 */           this.pixels[dstIndex] = (srcPixels[srcIndex] * this.pixels[dstIndex]);
/*     */ 
/*  92 */           srcIndex++;
/*     */         }
/*  94 */         break;
/*     */       case 6:
/*  96 */         for (int i = r1.width; ; dstIndex++) { i--; if (i < 0) break;
/*  97 */           float src = srcPixels[srcIndex];
/*  98 */           if ((useDBZValue) && (src == 0.0D))
/*  99 */             this.pixels[dstIndex] = divideByZeroValue;
/*     */           else
/* 101 */             this.pixels[dstIndex] /= src;
/*  96 */           srcIndex++;
/*     */         }
/*     */ 
/* 103 */         break;
/*     */       case 9:
/* 105 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 106 */           float dst = (int)srcPixels[(srcIndex++)] & (int)this.pixels[dstIndex];
/* 107 */           this.pixels[(dstIndex++)] = dst;
/*     */         }
/* 109 */         break;
/*     */       case 10:
/* 111 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 112 */           float dst = (int)srcPixels[(srcIndex++)] | (int)this.pixels[dstIndex];
/* 113 */           this.pixels[(dstIndex++)] = dst;
/*     */         }
/* 115 */         break;
/*     */       case 11:
/* 117 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 118 */           float dst = (int)srcPixels[(srcIndex++)] ^ (int)this.pixels[dstIndex];
/* 119 */           this.pixels[(dstIndex++)] = dst;
/*     */         }
/* 121 */         break;
/*     */       case 12:
/* 123 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 124 */           float src = srcPixels[(srcIndex++)];
/* 125 */           float dst = this.pixels[dstIndex];
/* 126 */           if (src < dst) dst = src;
/* 127 */           this.pixels[(dstIndex++)] = dst;
/*     */         }
/* 129 */         break;
/*     */       case 13:
/* 131 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 132 */           float src = srcPixels[(srcIndex++)];
/* 133 */           float dst = this.pixels[dstIndex];
/* 134 */           if (src > dst) dst = src;
/* 135 */           this.pixels[(dstIndex++)] = dst;
/*     */         }
/*     */       }
/*     */ 
/* 139 */       if (y % 20 == 0)
/* 140 */         ip.showProgress((y - r1.y) / r1.height);
/*     */     }
/* 142 */     ip.showProgress(1.0D);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  16 */     if (divideByZeroValue == 3.4028235E+38F)
/*  17 */       divideByZeroValue = (1.0F / 1.0F);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.FloatBlitter
 * JD-Core Version:    0.6.2
 */