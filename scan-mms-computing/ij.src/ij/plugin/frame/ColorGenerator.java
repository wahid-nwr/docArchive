/*     */ package ij.plugin.frame;
/*     */ 
/*     */ import ij.gui.Toolbar;
/*     */ import ij.process.ColorProcessor;
/*     */ import java.awt.Color;
/*     */ 
/*     */ class ColorGenerator extends ColorProcessor
/*     */ {
/*     */   int w;
/*     */   int h;
/*  58 */   int[] colors = { 16711680, 65280, 255, 16777215, 65535, 16711935, 16776960, 0 };
/*     */ 
/*     */   public ColorGenerator(int width, int height, int[] pixels) {
/*  61 */     super(width, height, pixels);
/*     */   }
/*     */   void drawColors(int colorWidth, int colorHeight, int columns, int rows) {
/*  64 */     this.w = colorWidth;
/*  65 */     this.h = colorHeight;
/*  66 */     setColor(16777215);
/*  67 */     setRoi(0, 0, 110, 320);
/*  68 */     fill();
/*  69 */     drawRamp();
/*  70 */     resetBW();
/*  71 */     flipper();
/*  72 */     drawLine(0, 256, 110, 256);
/*     */ 
/*  74 */     int x = 1;
/*  75 */     int y = 0;
/*  76 */     refreshBackground();
/*  77 */     refreshForeground();
/*     */ 
/*  80 */     float saturation = 1.0F; float brightness = 1.0F;
/*  81 */     double w = colorWidth; double h = colorHeight;
/*  82 */     for (x = 2; x < 10; x++) {
/*  83 */       for (y = 0; y < 32; y++) {
/*  84 */         float hue = (float)(y / (2.0D * h) - 0.15D);
/*  85 */         if (x < 6) {
/*  86 */           saturation = 1.0F;
/*  87 */           brightness = (float)(x * 4 / w);
/*     */         } else {
/*  89 */           saturation = 1.0F - (float)((5 - x) * -4 / w);
/*  90 */           brightness = 1.0F;
/*     */         }
/*  92 */         Color c = Color.getHSBColor(hue, saturation, brightness);
/*  93 */         setRoi(x * (int)(w / 2.0D), y * (int)(h / 2.0D), (int)w / 2, (int)h / 2);
/*  94 */         setColor(c);
/*  95 */         fill();
/*     */       }
/*     */     }
/*  98 */     drawSpectrum(h);
/*  99 */     resetRoi();
/*     */   }
/*     */ 
/*     */   void drawColor(int x, int y, Color c) {
/* 103 */     setRoi(x * this.w, y * this.h, this.w, this.h);
/* 104 */     setColor(c);
/* 105 */     fill();
/*     */   }
/*     */ 
/*     */   public void refreshBackground()
/*     */   {
/* 110 */     setColor(4473924);
/* 111 */     drawRect(this.w * 2 - 12, 276, this.w * 2 + 4, this.h * 2 + 4);
/* 112 */     setColor(10066329);
/* 113 */     drawRect(this.w * 2 - 11, 277, this.w * 2 + 2, this.h * 2 + 2);
/* 114 */     setRoi(this.w * 2 - 10, 278, this.w * 2, this.h * 2);
/* 115 */     setColor(Toolbar.getBackgroundColor());
/* 116 */     fill();
/*     */   }
/*     */ 
/*     */   public void refreshForeground()
/*     */   {
/* 121 */     setColor(4473924);
/* 122 */     drawRect(8, 266, this.w * 2 + 4, this.h * 2 + 4);
/* 123 */     setColor(10066329);
/* 124 */     drawRect(9, 267, this.w * 2 + 2, this.h * 2 + 2);
/* 125 */     setRoi(10, 268, this.w * 2, this.h * 2);
/* 126 */     setColor(Toolbar.getForegroundColor());
/* 127 */     fill();
/*     */   }
/*     */ 
/*     */   void drawSpectrum(double h)
/*     */   {
/* 132 */     for (int x = 5; x < 7; x++) {
/* 133 */       for (int y = 0; y < 32; y++) {
/* 134 */         float hue = (float)(y / (2.0D * h) - 0.15D);
/* 135 */         Color c = Color.getHSBColor(hue, 1.0F, 1.0F);
/* 136 */         setRoi(x * (this.w / 2), y * (int)(h / 2.0D), this.w / 2, (int)h / 2);
/* 137 */         setColor(c);
/* 138 */         fill();
/*     */       }
/*     */     }
/* 141 */     setRoi(55, 32, 22, 16);
/* 142 */     setColor(16711680);
/* 143 */     fill();
/* 144 */     setRoi(55, 120, 22, 16);
/* 145 */     setColor(65280);
/* 146 */     fill();
/* 147 */     setRoi(55, 208, 22, 16);
/* 148 */     setColor(255);
/* 149 */     fill();
/* 150 */     setRoi(55, 80, 22, 8);
/* 151 */     setColor(16776960);
/* 152 */     fill();
/* 153 */     setRoi(55, 168, 22, 8);
/* 154 */     setColor(65535);
/* 155 */     fill();
/* 156 */     setRoi(55, 248, 22, 8);
/* 157 */     setColor(16711935);
/* 158 */     fill();
/*     */   }
/*     */ 
/*     */   void drawRamp()
/*     */   {
/* 163 */     for (int x = 0; x < this.w; x++)
/* 164 */       for (double y = 0.0D; y < this.h * 16; y += 1.0D)
/*     */       {
/*     */         int b;
/*     */         int g;
/* 165 */         int r = g = b = (byte)(int)y;
/* 166 */         this.pixels[((int)y * this.width + x)] = (0xFF000000 | r << 16 & 0xFF0000 | g << 8 & 0xFF00 | b & 0xFF);
/*     */       }
/*     */   }
/*     */ 
/*     */   void resetBW()
/*     */   {
/* 172 */     setColor(0);
/* 173 */     drawRect(92, 300, 9, 7);
/* 174 */     setColor(0);
/* 175 */     setRoi(88, 297, 9, 7);
/* 176 */     fill();
/*     */   }
/*     */ 
/*     */   void flipper() {
/* 180 */     int xa = 90;
/* 181 */     int ya = 272;
/* 182 */     setColor(0);
/* 183 */     drawLine(xa, ya, xa + 9, ya + 9);
/* 184 */     drawLine(xa + 1, ya, xa + 9, ya + 8);
/* 185 */     drawLine(xa, ya + 1, xa + 8, ya + 9);
/* 186 */     drawLine(xa, ya, xa, ya + 5);
/* 187 */     drawLine(xa + 1, ya + 1, xa + 1, ya + 6);
/* 188 */     drawLine(xa, ya, xa + 5, ya);
/* 189 */     drawLine(xa + 1, ya + 1, xa + 6, ya + 1);
/* 190 */     drawLine(xa + 9, ya + 9, xa + 9, ya + 4);
/* 191 */     drawLine(xa + 8, ya + 8, xa + 8, ya + 3);
/* 192 */     drawLine(xa + 9, ya + 9, xa + 4, ya + 9);
/* 193 */     drawLine(xa + 8, ya + 8, xa + 3, ya + 8);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.ColorGenerator
 * JD-Core Version:    0.6.2
 */