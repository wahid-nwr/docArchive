/*     */ package ij.gui;
/*     */ 
/*     */ import ij.ImagePlus;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Polygon;
/*     */ 
/*     */ public class OvalRoi extends Roi
/*     */ {
/*     */   public OvalRoi(int x, int y, int width, int height)
/*     */   {
/*  14 */     super(x, y, width, height);
/*  15 */     this.type = 1;
/*     */   }
/*     */ 
/*     */   public OvalRoi(int x, int y, ImagePlus imp)
/*     */   {
/*  20 */     super(x, y, imp);
/*  21 */     this.type = 1;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public OvalRoi(int x, int y, int width, int height, ImagePlus imp) {
/*  26 */     this(x, y, width, height);
/*  27 */     setImage(imp);
/*     */   }
/*     */ 
/*     */   protected void moveHandle(int sx, int sy)
/*     */   {
/*  32 */     if (this.clipboard != null) return;
/*  33 */     int ox = this.ic.offScreenX(sx);
/*  34 */     int oy = this.ic.offScreenY(sy);
/*     */ 
/*  36 */     int x1 = this.x; int y1 = this.y; int x2 = this.x + this.width; int y2 = this.y + this.height; int xc = this.x + this.width / 2; int yc = this.y + this.height / 2;
/*  37 */     int w2 = (int)(0.14645D * this.width);
/*  38 */     int h2 = (int)(0.14645D * this.height);
/*     */     double asp;
/*  39 */     if ((this.width > 7) && (this.height > 7)) {
/*  40 */       double asp = this.width / this.height;
/*  41 */       this.asp_bk = asp;
/*     */     }
/*     */     else {
/*  44 */       asp = this.asp_bk;
/*     */     }
/*  46 */     switch (this.activeHandle) { case 0:
/*  47 */       this.x = (ox - w2); this.y = (oy - h2); break;
/*     */     case 1:
/*  48 */       this.y = oy; break;
/*     */     case 2:
/*  49 */       x2 = ox + w2; this.y = (oy - h2); break;
/*     */     case 3:
/*  50 */       x2 = ox; break;
/*     */     case 4:
/*  51 */       x2 = ox + w2; y2 = oy + h2; break;
/*     */     case 5:
/*  52 */       y2 = oy; break;
/*     */     case 6:
/*  53 */       this.x = (ox - w2); y2 = oy + h2; break;
/*     */     case 7:
/*  54 */       this.x = ox;
/*     */     }
/*     */ 
/*  57 */     if (this.x < x2) {
/*  58 */       this.width = (x2 - this.x);
/*     */     } else {
/*  60 */       this.width = 1; this.x = x2;
/*  61 */     }if (this.y < y2) {
/*  62 */       this.height = (y2 - this.y);
/*     */     } else {
/*  64 */       this.height = 1; this.y = y2;
/*  65 */     }if (this.center) {
/*  66 */       switch (this.activeHandle) {
/*     */       case 0:
/*  68 */         this.width = ((xc - this.x) * 2);
/*  69 */         this.height = ((yc - this.y) * 2);
/*  70 */         break;
/*     */       case 1:
/*  72 */         this.height = ((yc - this.y) * 2);
/*  73 */         break;
/*     */       case 2:
/*  75 */         this.width = ((x2 - xc) * 2);
/*  76 */         this.x = (x2 - this.width);
/*  77 */         this.height = ((yc - this.y) * 2);
/*  78 */         break;
/*     */       case 3:
/*  80 */         this.width = ((x2 - xc) * 2);
/*  81 */         this.x = (x2 - this.width);
/*  82 */         break;
/*     */       case 4:
/*  84 */         this.width = ((x2 - xc) * 2);
/*  85 */         this.x = (x2 - this.width);
/*  86 */         this.height = ((y2 - yc) * 2);
/*  87 */         this.y = (y2 - this.height);
/*  88 */         break;
/*     */       case 5:
/*  90 */         this.height = ((y2 - yc) * 2);
/*  91 */         this.y = (y2 - this.height);
/*  92 */         break;
/*     */       case 6:
/*  94 */         this.width = ((xc - this.x) * 2);
/*  95 */         this.height = ((y2 - yc) * 2);
/*  96 */         this.y = (y2 - this.height);
/*  97 */         break;
/*     */       case 7:
/*  99 */         this.width = ((xc - this.x) * 2);
/*     */       }
/*     */ 
/* 102 */       if (this.x >= x2) {
/* 103 */         this.width = 1;
/* 104 */         this.x = (x2 = xc);
/*     */       }
/* 106 */       if (this.y >= y2) {
/* 107 */         this.height = 1;
/* 108 */         this.y = (y2 = yc);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 113 */     if (this.constrain) {
/* 114 */       if ((this.activeHandle == 1) || (this.activeHandle == 5)) this.width = this.height; else {
/* 115 */         this.height = this.width;
/*     */       }
/* 117 */       if (this.x >= x2) {
/* 118 */         this.width = 1;
/* 119 */         this.x = (x2 = xc);
/*     */       }
/* 121 */       if (this.y >= y2) {
/* 122 */         this.height = 1;
/* 123 */         this.y = (y2 = yc);
/*     */       }
/* 125 */       switch (this.activeHandle) {
/*     */       case 0:
/* 127 */         this.x = (x2 - this.width);
/* 128 */         this.y = (y2 - this.height);
/* 129 */         break;
/*     */       case 1:
/* 131 */         this.x = (xc - this.width / 2);
/* 132 */         this.y = (y2 - this.height);
/* 133 */         break;
/*     */       case 2:
/* 135 */         this.y = (y2 - this.height);
/* 136 */         break;
/*     */       case 3:
/* 138 */         this.y = (yc - this.height / 2);
/* 139 */         break;
/*     */       case 5:
/* 141 */         this.x = (xc - this.width / 2);
/* 142 */         break;
/*     */       case 6:
/* 144 */         this.x = (x2 - this.width);
/* 145 */         break;
/*     */       case 7:
/* 147 */         this.y = (yc - this.height / 2);
/* 148 */         this.x = (x2 - this.width);
/*     */       case 4:
/*     */       }
/* 151 */       if (this.center) {
/* 152 */         this.x = (xc - this.width / 2);
/* 153 */         this.y = (yc - this.height / 2);
/*     */       }
/*     */     }
/*     */ 
/* 157 */     if ((this.aspect) && (!this.constrain)) {
/* 158 */       if ((this.activeHandle == 1) || (this.activeHandle == 5)) this.width = ((int)Math.rint(this.height * asp)); else {
/* 159 */         this.height = ((int)Math.rint(this.width / asp));
/*     */       }
/* 161 */       switch (this.activeHandle) {
/*     */       case 0:
/* 163 */         this.x = (x2 - this.width);
/* 164 */         this.y = (y2 - this.height);
/* 165 */         break;
/*     */       case 1:
/* 167 */         this.x = (xc - this.width / 2);
/* 168 */         this.y = (y2 - this.height);
/* 169 */         break;
/*     */       case 2:
/* 171 */         this.y = (y2 - this.height);
/* 172 */         break;
/*     */       case 3:
/* 174 */         this.y = (yc - this.height / 2);
/* 175 */         break;
/*     */       case 5:
/* 177 */         this.x = (xc - this.width / 2);
/* 178 */         break;
/*     */       case 6:
/* 180 */         this.x = (x2 - this.width);
/* 181 */         break;
/*     */       case 7:
/* 183 */         this.y = (yc - this.height / 2);
/* 184 */         this.x = (x2 - this.width);
/*     */       case 4:
/*     */       }
/* 187 */       if (this.center) {
/* 188 */         this.x = (xc - this.width / 2);
/* 189 */         this.y = (yc - this.height / 2);
/*     */       }
/*     */ 
/* 192 */       if (this.width < 8) {
/* 193 */         if (this.width < 1) this.width = 1;
/* 194 */         this.height = ((int)Math.rint(this.width / this.asp_bk));
/*     */       }
/* 196 */       if (this.height < 8) {
/* 197 */         if (this.height < 1) this.height = 1;
/* 198 */         this.width = ((int)Math.rint(this.height * this.asp_bk));
/*     */       }
/*     */     }
/*     */ 
/* 202 */     updateClipRect();
/* 203 */     this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/* 204 */     this.oldX = this.x; this.oldY = this.y;
/* 205 */     this.oldWidth = this.width; this.oldHeight = this.height;
/* 206 */     this.cachedMask = null;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g) {
/* 210 */     Color color = this.strokeColor != null ? this.strokeColor : ROIColor;
/* 211 */     if (this.fillColor != null) color = this.fillColor;
/* 212 */     g.setColor(color);
/* 213 */     this.mag = getMagnification();
/* 214 */     int sw = (int)(this.width * this.mag);
/* 215 */     int sh = (int)(this.height * this.mag);
/* 216 */     int sw2 = (int)(0.14645D * this.width * this.mag);
/* 217 */     int sh2 = (int)(0.14645D * this.height * this.mag);
/* 218 */     int sx1 = screenX(this.x);
/* 219 */     int sy1 = screenY(this.y);
/* 220 */     int sx2 = sx1 + sw / 2;
/* 221 */     int sy2 = sy1 + sh / 2;
/* 222 */     int sx3 = sx1 + sw;
/* 223 */     int sy3 = sy1 + sh;
/* 224 */     Graphics2D g2d = (Graphics2D)g;
/* 225 */     if (this.stroke != null)
/* 226 */       g2d.setStroke(getScaledStroke());
/* 227 */     if (this.fillColor != null)
/* 228 */       g.fillOval(sx1, sy1, sw, sh);
/*     */     else
/* 230 */       g.drawOval(sx1, sy1, sw, sh);
/* 231 */     if ((this.state != 0) && (this.clipboard == null) && (!this.overlay)) {
/* 232 */       int size2 = 2;
/* 233 */       drawHandle(g, sx1 + sw2 - size2, sy1 + sh2 - size2);
/* 234 */       drawHandle(g, sx3 - sw2 - size2, sy1 + sh2 - size2);
/* 235 */       drawHandle(g, sx3 - sw2 - size2, sy3 - sh2 - size2);
/* 236 */       drawHandle(g, sx1 + sw2 - size2, sy3 - sh2 - size2);
/* 237 */       drawHandle(g, sx2 - size2, sy1 - size2);
/* 238 */       drawHandle(g, sx3 - size2, sy2 - size2);
/* 239 */       drawHandle(g, sx2 - size2, sy3 - size2);
/* 240 */       drawHandle(g, sx1 - size2, sy2 - size2);
/*     */     }
/* 242 */     drawPreviousRoi(g);
/* 243 */     if (this.updateFullWindow) {
/* 244 */       this.updateFullWindow = false; this.imp.draw();
/* 245 */     }if (this.state != 3) showStatus();
/*     */   }
/*     */ 
/*     */   public void drawPixels(ImageProcessor ip)
/*     */   {
/* 250 */     Polygon p = getPolygon();
/* 251 */     if (p.npoints > 0) {
/* 252 */       int saveWidth = ip.getLineWidth();
/* 253 */       if (getStrokeWidth() > 1.0F)
/* 254 */         ip.setLineWidth(Math.round(getStrokeWidth()));
/* 255 */       ip.drawPolygon(p);
/* 256 */       ip.setLineWidth(saveWidth);
/*     */     }
/* 258 */     if ((Line.getWidth() > 1) || (getStrokeWidth() > 1.0F))
/* 259 */       this.updateFullWindow = true;
/*     */   }
/*     */ 
/*     */   public Polygon getPolygon()
/*     */   {
/* 264 */     ImageProcessor mask = getMask();
/* 265 */     Wand wand = new Wand(mask);
/* 266 */     wand.autoOutline(this.width / 2, this.height / 2, 255, 255);
/* 267 */     for (int i = 0; i < wand.npoints; i++) {
/* 268 */       wand.xpoints[i] += this.x;
/* 269 */       wand.ypoints[i] += this.y;
/*     */     }
/* 271 */     return new Polygon(wand.xpoints, wand.ypoints, wand.npoints);
/*     */   }
/*     */ 
/*     */   public boolean contains(int ox, int oy)
/*     */   {
/* 279 */     double a = this.width * 0.5D;
/* 280 */     double b = this.height * 0.5D;
/* 281 */     double cx = this.x + a - 0.5D;
/* 282 */     double cy = this.y + b - 0.5D;
/* 283 */     double dx = ox - cx;
/* 284 */     double dy = oy - cy;
/* 285 */     return dx * dx / (a * a) + dy * dy / (b * b) <= 1.0D;
/*     */   }
/*     */ 
/*     */   public int isHandle(int sx, int sy)
/*     */   {
/* 291 */     if ((this.clipboard != null) || (this.ic == null)) return -1;
/* 292 */     double mag = this.ic.getMagnification();
/* 293 */     int size = 8;
/* 294 */     int halfSize = size / 2;
/* 295 */     int sx1 = this.ic.screenX(this.x) - halfSize;
/* 296 */     int sy1 = this.ic.screenY(this.y) - halfSize;
/* 297 */     int sx3 = this.ic.screenX(this.x + this.width) - halfSize;
/* 298 */     int sy3 = this.ic.screenY(this.y + this.height) - halfSize;
/* 299 */     int sx2 = sx1 + (sx3 - sx1) / 2;
/* 300 */     int sy2 = sy1 + (sy3 - sy1) / 2;
/*     */ 
/* 302 */     int sw2 = (int)(0.14645D * (sx3 - sx1));
/* 303 */     int sh2 = (int)(0.14645D * (sy3 - sy1));
/*     */ 
/* 305 */     if ((sx >= sx1 + sw2) && (sx <= sx1 + sw2 + size) && (sy >= sy1 + sh2) && (sy <= sy1 + sh2 + size)) return 0;
/* 306 */     if ((sx >= sx2) && (sx <= sx2 + size) && (sy >= sy1) && (sy <= sy1 + size)) return 1;
/* 307 */     if ((sx >= sx3 - sw2) && (sx <= sx3 - sw2 + size) && (sy >= sy1 + sh2) && (sy <= sy1 + sh2 + size)) return 2;
/* 308 */     if ((sx >= sx3) && (sx <= sx3 + size) && (sy >= sy2) && (sy <= sy2 + size)) return 3;
/* 309 */     if ((sx >= sx3 - sw2) && (sx <= sx3 - sw2 + size) && (sy >= sy3 - sh2) && (sy <= sy3 - sh2 + size)) return 4;
/* 310 */     if ((sx >= sx2) && (sx <= sx2 + size) && (sy >= sy3) && (sy <= sy3 + size)) return 5;
/* 311 */     if ((sx >= sx1 + sw2) && (sx <= sx1 + sw2 + size) && (sy >= sy3 - sh2) && (sy <= sy3 - sh2 + size)) return 6;
/* 312 */     if ((sx >= sx1) && (sx <= sx1 + size) && (sy >= sy2) && (sy <= sy2 + size)) return 7;
/* 313 */     return -1;
/*     */   }
/*     */ 
/*     */   public ImageProcessor getMask() {
/* 317 */     if ((this.cachedMask != null) && (this.cachedMask.getPixels() != null))
/* 318 */       return this.cachedMask;
/* 319 */     ImageProcessor mask = new ByteProcessor(this.width, this.height);
/* 320 */     double a = this.width / 2.0D; double b = this.height / 2.0D;
/* 321 */     double a2 = a * a; double b2 = b * b;
/* 322 */     a -= 0.5D; b -= 0.5D;
/*     */ 
/* 325 */     byte[] pixels = (byte[])mask.getPixels();
/* 326 */     for (int y = 0; y < this.height; y++) {
/* 327 */       int offset = y * this.width;
/* 328 */       for (int x = 0; x < this.width; x++) {
/* 329 */         double xx = x - a;
/* 330 */         double yy = y - b;
/* 331 */         if (xx * xx / a2 + yy * yy / b2 <= 1.0D)
/* 332 */           pixels[(offset + x)] = -1;
/*     */       }
/*     */     }
/* 335 */     this.cachedMask = mask;
/* 336 */     return mask;
/*     */   }
/*     */ 
/*     */   public double getLength()
/*     */   {
/* 341 */     double pw = 1.0D; double ph = 1.0D;
/* 342 */     if (this.imp != null) {
/* 343 */       Calibration cal = this.imp.getCalibration();
/* 344 */       pw = cal.pixelWidth;
/* 345 */       ph = cal.pixelHeight;
/*     */     }
/* 347 */     return 3.141592653589793D * (this.width * pw + this.height * ph) / 2.0D;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.OvalRoi
 * JD-Core Version:    0.6.2
 */