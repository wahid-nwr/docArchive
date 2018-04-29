/*     */ package ij.process;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class EllipseFitter
/*     */ {
/*     */   static final double HALFPI = 1.5707963267949D;
/*     */   public double xCenter;
/*     */   public double yCenter;
/*     */   public double major;
/*     */   public double minor;
/*     */   public double angle;
/*     */   public double theta;
/*     */   public int[] xCoordinates;
/*     */   public int[] yCoordinates;
/*  94 */   public int nCoordinates = 0;
/*     */   private int bitCount;
/*     */   private double xsum;
/*     */   private double ysum;
/*     */   private double x2sum;
/*     */   private double y2sum;
/*     */   private double xysum;
/*     */   private byte[] mask;
/*     */   private int left;
/*     */   private int top;
/*     */   private int width;
/*     */   private int height;
/*     */   private double n;
/*     */   private double xm;
/*     */   private double ym;
/*     */   private double u20;
/*     */   private double u02;
/*     */   private double u11;
/*     */   private ImageProcessor ip;
/*     */   private boolean record;
/*     */ 
/*     */   public void fit(ImageProcessor ip, ImageStatistics stats)
/*     */   {
/* 111 */     this.ip = ip;
/* 112 */     this.mask = ip.getMaskArray();
/* 113 */     Rectangle r = ip.getRoi();
/* 114 */     this.left = r.x;
/* 115 */     this.top = r.y;
/* 116 */     this.width = r.width;
/* 117 */     this.height = r.height;
/* 118 */     getEllipseParam();
/*     */   }
/*     */ 
/*     */   void getEllipseParam() {
/* 122 */     double sqrtPi = 1.772453851D;
/*     */ 
/* 126 */     if (this.mask == null) {
/* 127 */       this.major = (this.width * 2 / sqrtPi);
/* 128 */       this.minor = (this.height * 2 / sqrtPi);
/* 129 */       this.angle = 0.0D;
/* 130 */       this.theta = 0.0D;
/* 131 */       if (this.major < this.minor) {
/* 132 */         double tmp = this.major;
/* 133 */         this.major = this.minor;
/* 134 */         this.minor = tmp;
/* 135 */         this.angle = 90.0D;
/* 136 */         this.theta = 1.570796326794897D;
/*     */       }
/* 138 */       this.xCenter = (this.left + this.width / 2.0D);
/* 139 */       this.yCenter = (this.top + this.height / 2.0D);
/* 140 */       return;
/*     */     }
/*     */ 
/* 143 */     computeSums();
/* 144 */     getMoments();
/* 145 */     double m4 = 4.0D * Math.abs(this.u02 * this.u20 - this.u11 * this.u11);
/* 146 */     if (m4 < 1.0E-06D)
/* 147 */       m4 = 1.0E-06D;
/* 148 */     double a11 = this.u02 / m4;
/* 149 */     double a12 = this.u11 / m4;
/* 150 */     double a22 = this.u20 / m4;
/* 151 */     double xoffset = this.xm;
/* 152 */     double yoffset = this.ym;
/*     */ 
/* 154 */     double tmp = a11 - a22;
/* 155 */     if (tmp == 0.0D)
/* 156 */       tmp = 1.0E-06D;
/* 157 */     this.theta = (0.5D * Math.atan(2.0D * a12 / tmp));
/* 158 */     if (this.theta < 0.0D)
/* 159 */       this.theta += 1.5707963267949D;
/* 160 */     if (a12 > 0.0D)
/* 161 */       this.theta += 1.5707963267949D;
/* 162 */     else if (a12 == 0.0D)
/* 163 */       if (a22 > a11) {
/* 164 */         this.theta = 0.0D;
/* 165 */         tmp = a22;
/* 166 */         a22 = a11;
/* 167 */         a11 = tmp;
/* 168 */       } else if (a11 != a22) {
/* 169 */         this.theta = 1.5707963267949D;
/*     */       }
/* 171 */     tmp = Math.sin(this.theta);
/* 172 */     if (tmp == 0.0D)
/* 173 */       tmp = 1.0E-06D;
/* 174 */     double z = a12 * Math.cos(this.theta) / tmp;
/* 175 */     this.major = Math.sqrt(1.0D / Math.abs(a22 + z));
/* 176 */     this.minor = Math.sqrt(1.0D / Math.abs(a11 - z));
/* 177 */     double scale = Math.sqrt(this.bitCount / (3.141592653589793D * this.major * this.minor));
/* 178 */     this.major = (this.major * scale * 2.0D);
/* 179 */     this.minor = (this.minor * scale * 2.0D);
/* 180 */     this.angle = (180.0D * this.theta / 3.141592653589793D);
/* 181 */     if (this.angle == 180.0D)
/* 182 */       this.angle = 0.0D;
/* 183 */     if (this.major < this.minor) {
/* 184 */       tmp = this.major;
/* 185 */       this.major = this.minor;
/* 186 */       this.minor = tmp;
/*     */     }
/* 188 */     this.xCenter = (this.left + xoffset + 0.5D);
/* 189 */     this.yCenter = (this.top + yoffset + 0.5D);
/*     */   }
/*     */ 
/*     */   void computeSums() {
/* 193 */     this.xsum = 0.0D;
/* 194 */     this.ysum = 0.0D;
/* 195 */     this.x2sum = 0.0D;
/* 196 */     this.y2sum = 0.0D;
/* 197 */     this.xysum = 0.0D;
/*     */ 
/* 201 */     for (int y = 0; y < this.height; y++) {
/* 202 */       int bitcountOfLine = 0;
/* 203 */       int xSumOfLine = 0;
/* 204 */       int offset = y * this.width;
/* 205 */       for (int x = 0; x < this.width; x++) {
/* 206 */         if (this.mask[(offset + x)] != 0) {
/* 207 */           bitcountOfLine++;
/* 208 */           xSumOfLine += x;
/* 209 */           this.x2sum += x * x;
/*     */         }
/*     */       }
/* 212 */       this.xsum += xSumOfLine;
/* 213 */       this.ysum += bitcountOfLine * y;
/* 214 */       double ye = y;
/* 215 */       double xe = xSumOfLine;
/* 216 */       this.xysum += xe * ye;
/* 217 */       this.y2sum += ye * ye * bitcountOfLine;
/* 218 */       this.bitCount += bitcountOfLine;
/*     */     }
/*     */   }
/*     */ 
/*     */   void getMoments()
/*     */   {
/* 225 */     if (this.bitCount == 0) {
/* 226 */       return;
/*     */     }
/* 228 */     this.x2sum += 0.08333333D * this.bitCount;
/* 229 */     this.y2sum += 0.08333333D * this.bitCount;
/* 230 */     this.n = this.bitCount;
/* 231 */     double x1 = this.xsum / this.n;
/* 232 */     double y1 = this.ysum / this.n;
/* 233 */     double x2 = this.x2sum / this.n;
/* 234 */     double y2 = this.y2sum / this.n;
/* 235 */     double xy = this.xysum / this.n;
/* 236 */     this.xm = x1;
/* 237 */     this.ym = y1;
/* 238 */     this.u20 = (x2 - x1 * x1);
/* 239 */     this.u02 = (y2 - y1 * y1);
/* 240 */     this.u11 = (xy - x1 * y1);
/*     */   }
/*     */ 
/*     */   public void drawEllipse(ImageProcessor ip)
/*     */   {
/* 268 */     if ((this.major == 0.0D) && (this.minor == 0.0D))
/* 269 */       return;
/* 270 */     int xc = (int)Math.round(this.xCenter);
/* 271 */     int yc = (int)Math.round(this.yCenter);
/* 272 */     int maxY = ip.getHeight();
/*     */ 
/* 276 */     int[] txmin = new int[maxY];
/* 277 */     int[] txmax = new int[maxY];
/*     */ 
/* 280 */     double sint = Math.sin(this.theta);
/* 281 */     double cost = Math.cos(this.theta);
/* 282 */     double rmajor2 = 1.0D / sqr(this.major / 2.0D);
/* 283 */     double rminor2 = 1.0D / sqr(this.minor / 2.0D);
/* 284 */     double g11 = rmajor2 * sqr(cost) + rminor2 * sqr(sint);
/* 285 */     double g12 = (rmajor2 - rminor2) * sint * cost;
/* 286 */     double g22 = rmajor2 * sqr(sint) + rminor2 * sqr(cost);
/* 287 */     double k1 = -g12 / g11;
/* 288 */     double k2 = (sqr(g12) - g11 * g22) / sqr(g11);
/* 289 */     double k3 = 1.0D / g11;
/* 290 */     int ymax = (int)Math.floor(Math.sqrt(Math.abs(k3 / k2)));
/* 291 */     if (ymax > maxY)
/* 292 */       ymax = maxY;
/* 293 */     if (ymax < 1)
/* 294 */       ymax = 1;
/* 295 */     int ymin = -ymax;
/*     */ 
/* 297 */     for (int y = 0; y <= ymax; y++)
/*     */     {
/* 299 */       double j2 = Math.sqrt(k2 * sqr(y) + k3);
/* 300 */       double j1 = k1 * y;
/* 301 */       txmin[y] = ((int)Math.round(j1 - j2));
/* 302 */       txmax[y] = ((int)Math.round(j1 + j2));
/*     */     }
/* 304 */     if (this.record) {
/* 305 */       this.xCoordinates[this.nCoordinates] = (xc + txmin[(ymax - 1)]);
/* 306 */       this.yCoordinates[this.nCoordinates] = (yc + ymin);
/* 307 */       this.nCoordinates += 1;
/*     */     } else {
/* 309 */       ip.moveTo(xc + txmin[(ymax - 1)], yc + ymin);
/* 310 */     }for (int y = ymin; y < ymax; y++) {
/* 311 */       int x = y < 0 ? txmax[(-y)] : -txmin[y];
/* 312 */       if (this.record) {
/* 313 */         this.xCoordinates[this.nCoordinates] = (xc + x);
/* 314 */         this.yCoordinates[this.nCoordinates] = (yc + y);
/* 315 */         this.nCoordinates += 1;
/*     */       } else {
/* 317 */         ip.lineTo(xc + x, yc + y);
/*     */       }
/*     */     }
/* 319 */     for (int y = ymax; y > ymin; y--) {
/* 320 */       int x = y < 0 ? txmin[(-y)] : -txmax[y];
/* 321 */       if (this.record) {
/* 322 */         this.xCoordinates[this.nCoordinates] = (xc + x);
/* 323 */         this.yCoordinates[this.nCoordinates] = (yc + y);
/* 324 */         this.nCoordinates += 1;
/*     */       } else {
/* 326 */         ip.lineTo(xc + x, yc + y);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void makeRoi(ImageProcessor ip)
/*     */   {
/* 333 */     this.record = true;
/* 334 */     int size = ip.getHeight() * 3;
/* 335 */     this.xCoordinates = new int[size];
/* 336 */     this.yCoordinates = new int[size];
/* 337 */     this.nCoordinates = 0;
/* 338 */     drawEllipse(ip);
/* 339 */     this.record = false;
/*     */   }
/*     */ 
/*     */   private double sqr(double x) {
/* 343 */     return x * x;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.EllipseFitter
 * JD-Core Version:    0.6.2
 */