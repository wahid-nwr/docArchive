/*     */ package ij.process;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class ShortBlitter
/*     */   implements Blitter
/*     */ {
/*     */   private ShortProcessor ip;
/*     */   private int width;
/*     */   private int height;
/*     */   private short[] pixels;
/*     */ 
/*     */   public void setTransparentColor(Color c)
/*     */   {
/*     */   }
/*     */ 
/*     */   public ShortBlitter(ShortProcessor ip)
/*     */   {
/*  16 */     this.ip = ip;
/*  17 */     this.width = ip.getWidth();
/*  18 */     this.height = ip.getHeight();
/*  19 */     this.pixels = ((short[])ip.getPixels());
/*     */   }
/*     */ 
/*     */   public void copyBits(ImageProcessor ip, int xloc, int yloc, int mode)
/*     */   {
/*  29 */     int srcWidth = ip.getWidth();
/*  30 */     int srcHeight = ip.getHeight();
/*  31 */     Rectangle r1 = new Rectangle(srcWidth, srcHeight);
/*  32 */     r1.setLocation(xloc, yloc);
/*  33 */     Rectangle r2 = new Rectangle(this.width, this.height);
/*  34 */     if (!r1.intersects(r2))
/*  35 */       return;
/*  36 */     short[] srcPixels = (short[])ip.getPixels();
/*     */ 
/*  38 */     r1 = r1.intersection(r2);
/*  39 */     int xSrcBase = xloc < 0 ? -xloc : 0;
/*  40 */     int ySrcBase = yloc < 0 ? -yloc : 0;
/*     */ 
/*  42 */     for (int y = r1.y; y < r1.y + r1.height; y++) {
/*  43 */       int srcIndex = (y - yloc) * srcWidth + (r1.x - xloc);
/*  44 */       int dstIndex = y * this.width + r1.x;
/*  45 */       switch (mode) { case 0:
/*     */       case 1:
/*     */       case 2:
/*  47 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  48 */           this.pixels[(dstIndex++)] = srcPixels[(srcIndex++)]; }
/*  49 */         break;
/*     */       case 14:
/*  51 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  52 */           int src = srcPixels[(srcIndex++)] & 0xFFFF;
/*     */           int dst;
/*     */           int dst;
/*  53 */           if (src == 0)
/*  54 */             dst = this.pixels[dstIndex];
/*     */           else
/*  56 */             dst = src;
/*  57 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/*  59 */         break;
/*     */       case 3:
/*  61 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  62 */           int dst = (srcPixels[(srcIndex++)] & 0xFFFF) + (this.pixels[dstIndex] & 0xFFFF);
/*  63 */           if (dst < 0) dst = 0;
/*  64 */           if (dst > 65535) dst = 65535;
/*  65 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/*  67 */         break;
/*     */       case 7:
/*  69 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  70 */           int dst = ((srcPixels[(srcIndex++)] & 0xFFFF) + (this.pixels[dstIndex] & 0xFFFF)) / 2;
/*  71 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/*  73 */         break;
/*     */       case 8:
/*  75 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  76 */           int dst = (this.pixels[dstIndex] & 0xFFFF) - (srcPixels[(srcIndex++)] & 0xFFFF);
/*  77 */           if (dst < 0) dst = -dst;
/*  78 */           if (dst > 65535) dst = 65535;
/*  79 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/*  81 */         break;
/*     */       case 4:
/*  83 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  84 */           int dst = (this.pixels[dstIndex] & 0xFFFF) - (srcPixels[(srcIndex++)] & 0xFFFF);
/*  85 */           if (dst < 0) dst = 0;
/*  86 */           if (dst > 65535) dst = 65535;
/*  87 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/*  89 */         break;
/*     */       case 5:
/*  91 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  92 */           int dst = (srcPixels[(srcIndex++)] & 0xFFFF) * (this.pixels[dstIndex] & 0xFFFF);
/*  93 */           if (dst < 0) dst = 0;
/*  94 */           if (dst > 65535) dst = 65535;
/*  95 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/*  97 */         break;
/*     */       case 6:
/*  99 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 100 */           int src = srcPixels[(srcIndex++)] & 0xFFFF;
/*     */           int dst;
/*     */           int dst;
/* 101 */           if (src == 0)
/* 102 */             dst = 65535;
/*     */           else
/* 104 */             dst = (this.pixels[dstIndex] & 0xFFFF) / src;
/* 105 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/* 107 */         break;
/*     */       case 9:
/* 109 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 110 */           int dst = srcPixels[(srcIndex++)] & this.pixels[dstIndex] & 0xFFFF;
/* 111 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/* 113 */         break;
/*     */       case 10:
/* 115 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 116 */           int dst = srcPixels[(srcIndex++)] | this.pixels[dstIndex];
/* 117 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/* 119 */         break;
/*     */       case 11:
/* 121 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 122 */           int dst = srcPixels[(srcIndex++)] ^ this.pixels[dstIndex];
/* 123 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/* 125 */         break;
/*     */       case 12:
/* 127 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 128 */           int src = srcPixels[(srcIndex++)] & 0xFFFF;
/* 129 */           int dst = this.pixels[dstIndex] & 0xFFFF;
/* 130 */           if (src < dst) dst = src;
/* 131 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/* 133 */         break;
/*     */       case 13:
/* 135 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 136 */           int src = srcPixels[(srcIndex++)] & 0xFFFF;
/* 137 */           int dst = this.pixels[dstIndex] & 0xFFFF;
/* 138 */           if (src > dst) dst = src;
/* 139 */           this.pixels[(dstIndex++)] = ((short)dst);
/*     */         }
/*     */       }
/*     */ 
/* 143 */       if (y % 20 == 0)
/* 144 */         ip.showProgress((y - r1.y) / r1.height);
/*     */     }
/* 146 */     ip.showProgress(1.0D);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ShortBlitter
 * JD-Core Version:    0.6.2
 */