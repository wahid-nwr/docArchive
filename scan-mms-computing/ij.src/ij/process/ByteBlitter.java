/*     */ package ij.process;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class ByteBlitter
/*     */   implements Blitter
/*     */ {
/*     */   private ByteProcessor ip;
/*     */   private int width;
/*     */   private int height;
/*     */   private byte[] pixels;
/*  10 */   private int transparent = 255;
/*     */ 
/*     */   public ByteBlitter(ByteProcessor ip)
/*     */   {
/*  14 */     this.ip = ip;
/*  15 */     this.width = ip.getWidth();
/*  16 */     this.height = ip.getHeight();
/*  17 */     this.pixels = ((byte[])ip.getPixels());
/*     */   }
/*     */ 
/*     */   public void setTransparentColor(Color c) {
/*  21 */     this.transparent = this.ip.getBestIndex(c);
/*     */   }
/*     */ 
/*     */   public void copyBits(ImageProcessor ip, int xloc, int yloc, int mode)
/*     */   {
/*  32 */     int srcWidth = ip.getWidth();
/*  33 */     int srcHeight = ip.getHeight();
/*  34 */     Rectangle r1 = new Rectangle(srcWidth, srcHeight);
/*  35 */     r1.setLocation(xloc, yloc);
/*  36 */     Rectangle r2 = new Rectangle(this.width, this.height);
/*  37 */     if (!r1.intersects(r2))
/*     */       return;
/*     */     byte[] srcPixels;
/*  39 */     if ((ip instanceof ColorProcessor)) {
/*  40 */       int[] pixels32 = (int[])ip.getPixels();
/*  41 */       int size = ip.getWidth() * ip.getHeight();
/*  42 */       byte[] srcPixels = new byte[size];
/*  43 */       if (this.ip.isInvertedLut())
/*  44 */         for (int i = 0; i < size; i++)
/*  45 */           srcPixels[i] = ((byte)(255 - pixels32[i] & 0xFF));
/*     */       else
/*  47 */         for (int i = 0; i < size; i++)
/*  48 */           srcPixels[i] = ((byte)(pixels32[i] & 0xFF));
/*     */     } else {
/*  50 */       ip = ip.convertToByte(true);
/*  51 */       srcPixels = (byte[])ip.getPixels();
/*     */     }
/*  53 */     r1 = r1.intersection(r2);
/*  54 */     int xSrcBase = xloc < 0 ? -xloc : 0;
/*  55 */     int ySrcBase = yloc < 0 ? -yloc : 0;
/*     */ 
/*  57 */     for (int y = r1.y; y < r1.y + r1.height; y++) {
/*  58 */       int srcIndex = (y - yloc) * srcWidth + (r1.x - xloc);
/*  59 */       int dstIndex = y * this.width + r1.x;
/*  60 */       switch (mode) {
/*     */       case 0:
/*  62 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  63 */           this.pixels[(dstIndex++)] = srcPixels[(srcIndex++)]; }
/*  64 */         break;
/*     */       case 1:
/*  66 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  67 */           this.pixels[(dstIndex++)] = ((byte)(255 - srcPixels[(srcIndex++)] & 0xFF)); }
/*  68 */         break;
/*     */       case 2:
/*  70 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  71 */           int src = srcPixels[(srcIndex++)] & 0xFF;
/*     */           int dst;
/*     */           int dst;
/*  72 */           if (src == this.transparent)
/*  73 */             dst = this.pixels[dstIndex];
/*     */           else
/*  75 */             dst = src;
/*  76 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/*  78 */         break;
/*     */       case 14:
/*  80 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  81 */           int src = srcPixels[(srcIndex++)] & 0xFF;
/*     */           int dst;
/*     */           int dst;
/*  82 */           if (src == 0)
/*  83 */             dst = this.pixels[dstIndex];
/*     */           else
/*  85 */             dst = src;
/*  86 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/*  88 */         break;
/*     */       case 3:
/*  90 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  91 */           int dst = (srcPixels[(srcIndex++)] & 0xFF) + (this.pixels[dstIndex] & 0xFF);
/*  92 */           if (dst > 255) dst = 255;
/*  93 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/*  95 */         break;
/*     */       case 7:
/*  97 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/*  98 */           int dst = ((srcPixels[(srcIndex++)] & 0xFF) + (this.pixels[dstIndex] & 0xFF)) / 2;
/*  99 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/* 101 */         break;
/*     */       case 4:
/* 103 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 104 */           int dst = (this.pixels[dstIndex] & 0xFF) - (srcPixels[(srcIndex++)] & 0xFF);
/* 105 */           if (dst < 0) dst = 0;
/* 106 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/* 108 */         break;
/*     */       case 8:
/* 110 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 111 */           int dst = (this.pixels[dstIndex] & 0xFF) - (srcPixels[(srcIndex++)] & 0xFF);
/* 112 */           if (dst < 0) dst = -dst;
/* 113 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/* 115 */         break;
/*     */       case 5:
/* 117 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 118 */           int dst = (srcPixels[(srcIndex++)] & 0xFF) * (this.pixels[dstIndex] & 0xFF);
/* 119 */           if (dst > 255) dst = 255;
/* 120 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/* 122 */         break;
/*     */       case 6:
/* 124 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 125 */           int src = srcPixels[(srcIndex++)] & 0xFF;
/*     */           int dst;
/*     */           int dst;
/* 126 */           if (src == 0)
/* 127 */             dst = 255;
/*     */           else
/* 129 */             dst = (this.pixels[dstIndex] & 0xFF) / src;
/* 130 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/* 132 */         break;
/*     */       case 9:
/* 134 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 135 */           int dst = srcPixels[(srcIndex++)] & this.pixels[dstIndex];
/* 136 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/* 138 */         break;
/*     */       case 10:
/* 140 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 141 */           int dst = srcPixels[(srcIndex++)] | this.pixels[dstIndex];
/* 142 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/* 144 */         break;
/*     */       case 11:
/* 146 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 147 */           int dst = srcPixels[(srcIndex++)] ^ this.pixels[dstIndex];
/* 148 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/* 150 */         break;
/*     */       case 12:
/* 152 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 153 */           int src = srcPixels[(srcIndex++)] & 0xFF;
/* 154 */           int dst = this.pixels[dstIndex] & 0xFF;
/* 155 */           if (src < dst) dst = src;
/* 156 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/* 158 */         break;
/*     */       case 13:
/* 160 */         int i = r1.width;
/*     */         while (true) { i--; if (i < 0) break;
/* 161 */           int src = srcPixels[(srcIndex++)] & 0xFF;
/* 162 */           int dst = this.pixels[dstIndex] & 0xFF;
/* 163 */           if (src > dst) dst = src;
/* 164 */           this.pixels[(dstIndex++)] = ((byte)dst);
/*     */         }
/*     */       }
/*     */ 
/* 168 */       if (y % 20 == 0)
/* 169 */         ip.showProgress((y - r1.y) / r1.height);
/*     */     }
/* 171 */     ip.hideProgress();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.ByteBlitter
 * JD-Core Version:    0.6.2
 */