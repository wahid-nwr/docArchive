/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CommandListener;
/*     */ import ij.CompositeImage;
/*     */ import ij.Executer;
/*     */ import ij.IJ;
/*     */ import ij.ImageListener;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.ScrollbarWithLabel;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ShortProcessor;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.AdjustmentListener;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.awt.event.MouseWheelListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.image.ColorModel;
/*     */ 
/*     */ public class Orthogonal_Views
/*     */   implements PlugIn, MouseListener, MouseMotionListener, KeyListener, ActionListener, ImageListener, WindowListener, AdjustmentListener, MouseWheelListener, FocusListener, CommandListener
/*     */ {
/*     */   private ImageWindow win;
/*     */   private ImagePlus imp;
/*     */   private boolean rgb;
/*     */   private ImageStack imageStack;
/*     */   private boolean hyperstack;
/*     */   private int currentChannel;
/*     */   private int currentFrame;
/*     */   private int currentMode;
/*     */   private ImageCanvas canvas;
/*     */   private static final int H_ROI = 0;
/*     */   private static final int H_ZOOM = 1;
/*  54 */   private static boolean sticky = true;
/*     */   private static int xzID;
/*     */   private static int yzID;
/*     */   private static Orthogonal_Views instance;
/*     */   private ImagePlus xz_image;
/*     */   private ImagePlus yz_image;
/*     */   private ImageProcessor fp1;
/*     */   private ImageProcessor fp2;
/*     */   private double ax;
/*     */   private double ay;
/*     */   private double az;
/*     */   private boolean rotateYZ;
/*     */   private boolean flipXZ;
/*     */   private int xyX;
/*     */   private int xyY;
/*     */   private Calibration cal;
/*     */   private Calibration cal_xz;
/*     */   private Calibration cal_yz;
/*     */   private double magnification;
/*     */   private Color color;
/*     */   private Updater updater;
/*     */   private double min;
/*     */   private double max;
/*     */   private Dimension screen;
/*     */   private boolean syncZoom;
/*     */   private Point crossLoc;
/*     */   private boolean firstTime;
/*     */   private static int previousID;
/*     */   private static int previousX;
/*     */   private static int previousY;
/*     */   private Rectangle startingSrcRect;
/*     */ 
/*     */   public Orthogonal_Views()
/*     */   {
/*  61 */     this.rotateYZ = Prefs.rotateYZ;
/*  62 */     this.flipXZ = Prefs.flipXZ;
/*     */ 
/*  65 */     this.cal = null; this.cal_xz = new Calibration(); this.cal_yz = new Calibration();
/*  66 */     this.magnification = 1.0D;
/*  67 */     this.color = Roi.getColor();
/*  68 */     this.updater = new Updater();
/*     */ 
/*  70 */     this.screen = IJ.getScreenSize();
/*  71 */     this.syncZoom = true;
/*     */ 
/*  73 */     this.firstTime = true;
/*     */   }
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  78 */     this.imp = IJ.getImage();
/*  79 */     if (instance != null) {
/*  80 */       instance.dispose();
/*  81 */       return;
/*     */     }
/*  83 */     if (this.imp.getStackSize() == 1) {
/*  84 */       IJ.error("Othogonal Views", "This command requires a stack.");
/*  85 */       return;
/*     */     }
/*  87 */     this.hyperstack = this.imp.isHyperStack();
/*  88 */     if (((this.hyperstack) || (this.imp.isComposite())) && (this.imp.getNSlices() <= 1)) {
/*  89 */       IJ.error("Othogonal Views", "This command requires a stack, or a hypertack with Z>1.");
/*  90 */       return;
/*     */     }
/*  92 */     this.yz_image = WindowManager.getImage(yzID);
/*  93 */     this.rgb = ((this.imp.getBitDepth() == 24) || (this.hyperstack));
/*  94 */     int yzBitDepth = this.hyperstack ? 24 : this.imp.getBitDepth();
/*  95 */     if ((this.yz_image == null) || (this.yz_image.getHeight() != this.imp.getHeight()) || (this.yz_image.getBitDepth() != yzBitDepth))
/*  96 */       this.yz_image = new ImagePlus();
/*  97 */     this.xz_image = WindowManager.getImage(xzID);
/*     */ 
/*  99 */     if ((this.xz_image == null) || (this.xz_image.getWidth() != this.imp.getWidth()) || (this.xz_image.getBitDepth() != yzBitDepth))
/* 100 */       this.xz_image = new ImagePlus();
/* 101 */     instance = this;
/* 102 */     ImageProcessor ip = this.hyperstack ? new ColorProcessor(this.imp.getImage()) : this.imp.getProcessor();
/* 103 */     this.min = ip.getMin();
/* 104 */     this.max = ip.getMax();
/* 105 */     this.cal = this.imp.getCalibration();
/* 106 */     double calx = this.cal.pixelWidth;
/* 107 */     double caly = this.cal.pixelHeight;
/* 108 */     double calz = this.cal.pixelDepth;
/* 109 */     this.ax = 1.0D;
/* 110 */     this.ay = (caly / calx);
/* 111 */     this.az = (calz / calx);
/* 112 */     this.win = this.imp.getWindow();
/* 113 */     this.canvas = this.win.getCanvas();
/* 114 */     addListeners(this.canvas);
/* 115 */     this.magnification = this.canvas.getMagnification();
/* 116 */     this.imp.killRoi();
/* 117 */     Rectangle r = this.canvas.getSrcRect();
/* 118 */     if (this.imp.getID() == previousID)
/* 119 */       this.crossLoc = new Point(previousX, previousY);
/*     */     else
/* 121 */       this.crossLoc = new Point(r.x + r.width / 2, r.y + r.height / 2);
/* 122 */     this.imageStack = getStack();
/* 123 */     calibrate();
/* 124 */     if (createProcessors(this.imageStack)) {
/* 125 */       if ((ip.isColorLut()) || (ip.isInvertedLut())) {
/* 126 */         ColorModel cm = ip.getColorModel();
/* 127 */         this.fp1.setColorModel(cm);
/* 128 */         this.fp2.setColorModel(cm);
/*     */       }
/* 130 */       update();
/*     */     } else {
/* 132 */       dispose();
/*     */     }
/*     */   }
/*     */ 
/* 136 */   private ImageStack getStack() { if (this.imp.isHyperStack()) {
/* 137 */       int slices = this.imp.getNSlices();
/* 138 */       ImageStack stack = new ImageStack(this.imp.getWidth(), this.imp.getHeight());
/* 139 */       int c = this.imp.getChannel(); int z = this.imp.getSlice(); int t = this.imp.getFrame();
/* 140 */       for (int i = 1; i <= slices; i++) {
/* 141 */         this.imp.setPositionWithoutUpdate(c, i, t);
/* 142 */         stack.addSlice(null, new ColorProcessor(this.imp.getImage()));
/*     */       }
/* 144 */       this.imp.setPosition(c, z, t);
/* 145 */       this.currentChannel = c;
/* 146 */       this.currentFrame = t;
/* 147 */       if (this.imp.isComposite())
/* 148 */         this.currentMode = ((CompositeImage)this.imp).getMode();
/* 149 */       return stack;
/*     */     }
/* 151 */     return this.imp.getStack(); }
/*     */ 
/*     */   private void addListeners(ImageCanvas canvass)
/*     */   {
/* 155 */     this.canvas.addMouseListener(this);
/* 156 */     this.canvas.addMouseMotionListener(this);
/* 157 */     this.canvas.addKeyListener(this);
/* 158 */     this.win.addWindowListener(this);
/* 159 */     this.win.addMouseWheelListener(this);
/* 160 */     this.win.addFocusListener(this);
/* 161 */     Component[] c = this.win.getComponents();
/*     */ 
/* 163 */     ((ScrollbarWithLabel)c[1]).addAdjustmentListener(this);
/* 164 */     ImagePlus.addImageListener(this);
/* 165 */     Executer.addCommandListener(this);
/*     */   }
/*     */ 
/*     */   private void calibrate() {
/* 169 */     String unit = this.cal.getUnit();
/* 170 */     double o_depth = this.cal.pixelDepth;
/* 171 */     double o_height = this.cal.pixelHeight;
/* 172 */     double o_width = this.cal.pixelWidth;
/* 173 */     this.cal_yz.setUnit(unit);
/* 174 */     if (this.rotateYZ) {
/* 175 */       this.cal_yz.pixelHeight = (o_depth / this.az);
/* 176 */       this.cal_yz.pixelWidth = o_height;
/*     */     } else {
/* 178 */       this.cal_yz.pixelWidth = (o_depth / this.az);
/* 179 */       this.cal_yz.pixelHeight = o_height;
/*     */     }
/* 181 */     this.yz_image.setCalibration(this.cal_yz);
/* 182 */     this.cal_xz.setUnit(unit);
/* 183 */     this.cal_xz.pixelWidth = o_width;
/* 184 */     this.cal_xz.pixelHeight = (o_depth / this.az);
/* 185 */     this.xz_image.setCalibration(this.cal_xz);
/*     */   }
/*     */ 
/*     */   private void updateMagnification(int x, int y) {
/* 189 */     double magnification = this.win.getCanvas().getMagnification();
/* 190 */     int z = this.imp.getSlice() - 1;
/* 191 */     ImageWindow xz_win = this.xz_image.getWindow();
/* 192 */     if (xz_win == null) return;
/* 193 */     ImageCanvas xz_ic = xz_win.getCanvas();
/* 194 */     double xz_mag = xz_ic.getMagnification();
/* 195 */     double arat = this.az / this.ax;
/* 196 */     int zcoord = (int)(arat * z);
/* 197 */     if (this.flipXZ) zcoord = (int)(arat * (this.imp.getNSlices() - z));
/* 198 */     while (xz_mag < magnification) {
/* 199 */       xz_ic.zoomIn(xz_ic.screenX(x), xz_ic.screenY(zcoord));
/* 200 */       xz_mag = xz_ic.getMagnification();
/*     */     }
/* 202 */     while (xz_mag > magnification) {
/* 203 */       xz_ic.zoomOut(xz_ic.screenX(x), xz_ic.screenY(zcoord));
/* 204 */       xz_mag = xz_ic.getMagnification();
/*     */     }
/* 206 */     ImageWindow yz_win = this.yz_image.getWindow();
/* 207 */     if (yz_win == null) return;
/* 208 */     ImageCanvas yz_ic = yz_win.getCanvas();
/* 209 */     double yz_mag = yz_ic.getMagnification();
/* 210 */     zcoord = (int)(arat * z);
/* 211 */     while (yz_mag < magnification)
/*     */     {
/* 213 */       yz_ic.zoomIn(yz_ic.screenX(zcoord), yz_ic.screenY(y));
/* 214 */       yz_mag = yz_ic.getMagnification();
/*     */     }
/* 216 */     while (yz_mag > magnification) {
/* 217 */       yz_ic.zoomOut(yz_ic.screenX(zcoord), yz_ic.screenY(y));
/* 218 */       yz_mag = yz_ic.getMagnification();
/*     */     }
/*     */   }
/*     */ 
/*     */   void updateViews(Point p, ImageStack is) {
/* 223 */     if (this.fp1 == null) return;
/* 224 */     updateXZView(p, is);
/*     */ 
/* 226 */     double arat = this.az / this.ax;
/* 227 */     int width2 = this.fp1.getWidth();
/* 228 */     int height2 = (int)Math.round(this.fp1.getHeight() * this.az);
/* 229 */     if ((width2 != this.fp1.getWidth()) || (height2 != this.fp1.getHeight())) {
/* 230 */       this.fp1.setInterpolate(true);
/* 231 */       ImageProcessor sfp1 = this.fp1.resize(width2, height2);
/* 232 */       if (!this.rgb) sfp1.setMinAndMax(this.min, this.max);
/* 233 */       this.xz_image.setProcessor("XZ " + p.y, sfp1);
/*     */     } else {
/* 235 */       if (!this.rgb) this.fp1.setMinAndMax(this.min, this.max);
/* 236 */       this.xz_image.setProcessor("XZ " + p.y, this.fp1);
/*     */     }
/*     */ 
/* 239 */     if (this.rotateYZ)
/* 240 */       updateYZView(p, is);
/*     */     else {
/* 242 */       updateZYView(p, is);
/*     */     }
/* 244 */     width2 = (int)Math.round(this.fp2.getWidth() * this.az);
/* 245 */     height2 = this.fp2.getHeight();
/* 246 */     String title = "YZ ";
/* 247 */     if (this.rotateYZ) {
/* 248 */       width2 = this.fp2.getWidth();
/* 249 */       height2 = (int)Math.round(this.fp2.getHeight() * this.az);
/* 250 */       title = "ZY ";
/*     */     }
/*     */ 
/* 253 */     if ((width2 != this.fp2.getWidth()) || (height2 != this.fp2.getHeight())) {
/* 254 */       this.fp2.setInterpolate(true);
/* 255 */       ImageProcessor sfp2 = this.fp2.resize(width2, height2);
/* 256 */       if (!this.rgb) sfp2.setMinAndMax(this.min, this.max);
/* 257 */       this.yz_image.setProcessor(title + p.x, sfp2);
/*     */     } else {
/* 259 */       if (!this.rgb) this.fp2.setMinAndMax(this.min, this.max);
/* 260 */       this.yz_image.setProcessor(title + p.x, this.fp2);
/*     */     }
/*     */ 
/* 263 */     calibrate();
/* 264 */     if (this.yz_image.getWindow() == null) {
/* 265 */       this.yz_image.show();
/* 266 */       ImageCanvas ic = this.yz_image.getCanvas();
/* 267 */       ic.addKeyListener(this);
/* 268 */       ic.addMouseListener(this);
/* 269 */       ic.addMouseMotionListener(this);
/* 270 */       ic.setCustomRoi(true);
/* 271 */       this.yz_image.getWindow().addMouseWheelListener(this);
/* 272 */       yzID = this.yz_image.getID();
/*     */     } else {
/* 274 */       ImageCanvas ic = this.yz_image.getWindow().getCanvas();
/* 275 */       ic.addMouseListener(this);
/* 276 */       ic.addMouseMotionListener(this);
/* 277 */       ic.setCustomRoi(true);
/*     */     }
/* 279 */     if (this.xz_image.getWindow() == null) {
/* 280 */       this.xz_image.show();
/* 281 */       ImageCanvas ic = this.xz_image.getCanvas();
/* 282 */       ic.addKeyListener(this);
/* 283 */       ic.addMouseListener(this);
/* 284 */       ic.addMouseMotionListener(this);
/* 285 */       ic.setCustomRoi(true);
/* 286 */       this.xz_image.getWindow().addMouseWheelListener(this);
/* 287 */       xzID = this.xz_image.getID();
/*     */     } else {
/* 289 */       ImageCanvas ic = this.xz_image.getWindow().getCanvas();
/* 290 */       ic.addMouseListener(this);
/* 291 */       ic.addMouseMotionListener(this);
/* 292 */       ic.setCustomRoi(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   void arrangeWindows(boolean sticky)
/*     */   {
/* 298 */     ImageWindow xyWin = this.imp.getWindow();
/* 299 */     if (xyWin == null) return;
/* 300 */     Point loc = xyWin.getLocation();
/* 301 */     if ((this.xyX != loc.x) || (this.xyY != loc.y)) {
/* 302 */       this.xyX = loc.x;
/* 303 */       this.xyY = loc.y;
/* 304 */       ImageWindow yzWin = null;
/* 305 */       long start = System.currentTimeMillis();
/* 306 */       while ((yzWin == null) && (System.currentTimeMillis() - start <= 2500L)) {
/* 307 */         yzWin = this.yz_image.getWindow();
/* 308 */         if (yzWin == null) IJ.wait(50);
/*     */       }
/* 310 */       if (yzWin != null)
/* 311 */         yzWin.setLocation(this.xyX + xyWin.getWidth(), this.xyY);
/* 312 */       ImageWindow xzWin = null;
/* 313 */       start = System.currentTimeMillis();
/* 314 */       while ((xzWin == null) && (System.currentTimeMillis() - start <= 2500L)) {
/* 315 */         xzWin = this.xz_image.getWindow();
/* 316 */         if (xzWin == null) IJ.wait(50);
/*     */       }
/* 318 */       if (xzWin != null)
/* 319 */         xzWin.setLocation(this.xyX, this.xyY + xyWin.getHeight());
/* 320 */       if (this.firstTime) {
/* 321 */         this.imp.getWindow().toFront();
/* 322 */         if (this.hyperstack)
/* 323 */           this.imp.setPosition(this.imp.getChannel(), this.imp.getNSlices() / 2, this.imp.getFrame());
/*     */         else
/* 325 */           this.imp.setSlice(this.imp.getNSlices() / 2);
/* 326 */         this.firstTime = false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean createProcessors(ImageStack is)
/*     */   {
/* 336 */     ImageProcessor ip = is.getProcessor(1);
/* 337 */     int width = is.getWidth();
/* 338 */     int height = is.getHeight();
/* 339 */     int ds = is.getSize();
/* 340 */     double arat = 1.0D;
/* 341 */     double brat = 1.0D;
/* 342 */     int za = (int)(ds * arat);
/* 343 */     int zb = (int)(ds * brat);
/*     */ 
/* 346 */     if ((ip instanceof FloatProcessor)) {
/* 347 */       this.fp1 = new FloatProcessor(width, za);
/* 348 */       if (this.rotateYZ)
/* 349 */         this.fp2 = new FloatProcessor(height, zb);
/*     */       else
/* 351 */         this.fp2 = new FloatProcessor(zb, height);
/* 352 */       return true;
/*     */     }
/*     */ 
/* 355 */     if ((ip instanceof ByteProcessor)) {
/* 356 */       this.fp1 = new ByteProcessor(width, za);
/* 357 */       if (this.rotateYZ)
/* 358 */         this.fp2 = new ByteProcessor(height, zb);
/*     */       else
/* 360 */         this.fp2 = new ByteProcessor(zb, height);
/* 361 */       return true;
/*     */     }
/*     */ 
/* 364 */     if ((ip instanceof ShortProcessor)) {
/* 365 */       this.fp1 = new ShortProcessor(width, za);
/* 366 */       if (this.rotateYZ)
/* 367 */         this.fp2 = new ShortProcessor(height, zb);
/*     */       else {
/* 369 */         this.fp2 = new ShortProcessor(zb, height);
/*     */       }
/* 371 */       return true;
/*     */     }
/*     */ 
/* 374 */     if ((ip instanceof ColorProcessor)) {
/* 375 */       this.fp1 = new ColorProcessor(width, za);
/* 376 */       if (this.rotateYZ)
/* 377 */         this.fp2 = new ColorProcessor(height, zb);
/*     */       else
/* 379 */         this.fp2 = new ColorProcessor(zb, height);
/* 380 */       return true;
/*     */     }
/*     */ 
/* 383 */     return false;
/*     */   }
/*     */ 
/*     */   void updateXZView(Point p, ImageStack is) {
/* 387 */     int width = is.getWidth();
/* 388 */     int size = is.getSize();
/* 389 */     ImageProcessor ip = is.getProcessor(1);
/*     */ 
/* 391 */     int y = p.y;
/*     */ 
/* 393 */     if ((ip instanceof ShortProcessor)) {
/* 394 */       short[] newpix = new short[width * size];
/* 395 */       for (int i = 0; i < size; i++) {
/* 396 */         Object pixels = is.getPixels(i + 1);
/* 397 */         if (this.flipXZ)
/* 398 */           System.arraycopy(pixels, width * y, newpix, width * (size - i - 1), width);
/*     */         else
/* 400 */           System.arraycopy(pixels, width * y, newpix, width * i, width);
/*     */       }
/* 402 */       this.fp1.setPixels(newpix);
/* 403 */       return;
/*     */     }
/*     */ 
/* 406 */     if ((ip instanceof ByteProcessor)) {
/* 407 */       byte[] newpix = new byte[width * size];
/* 408 */       for (int i = 0; i < size; i++) {
/* 409 */         Object pixels = is.getPixels(i + 1);
/* 410 */         if (this.flipXZ)
/* 411 */           System.arraycopy(pixels, width * y, newpix, width * (size - i - 1), width);
/*     */         else
/* 413 */           System.arraycopy(pixels, width * y, newpix, width * i, width);
/*     */       }
/* 415 */       this.fp1.setPixels(newpix);
/* 416 */       return;
/*     */     }
/*     */ 
/* 419 */     if ((ip instanceof FloatProcessor)) {
/* 420 */       float[] newpix = new float[width * size];
/* 421 */       for (int i = 0; i < size; i++) {
/* 422 */         Object pixels = is.getPixels(i + 1);
/* 423 */         if (this.flipXZ)
/* 424 */           System.arraycopy(pixels, width * y, newpix, width * (size - i - 1), width);
/*     */         else
/* 426 */           System.arraycopy(pixels, width * y, newpix, width * i, width);
/*     */       }
/* 428 */       this.fp1.setPixels(newpix);
/* 429 */       return;
/*     */     }
/*     */ 
/* 432 */     if ((ip instanceof ColorProcessor)) {
/* 433 */       int[] newpix = new int[width * size];
/* 434 */       for (int i = 0; i < size; i++) {
/* 435 */         Object pixels = is.getPixels(i + 1);
/* 436 */         if (this.flipXZ)
/* 437 */           System.arraycopy(pixels, width * y, newpix, width * (size - i - 1), width);
/*     */         else
/* 439 */           System.arraycopy(pixels, width * y, newpix, width * i, width);
/*     */       }
/* 441 */       this.fp1.setPixels(newpix);
/* 442 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   void updateYZView(Point p, ImageStack is)
/*     */   {
/* 448 */     int width = is.getWidth();
/* 449 */     int height = is.getHeight();
/* 450 */     int ds = is.getSize();
/* 451 */     ImageProcessor ip = is.getProcessor(1);
/* 452 */     int x = p.x;
/*     */ 
/* 454 */     if ((ip instanceof FloatProcessor)) {
/* 455 */       float[] newpix = new float[ds * height];
/* 456 */       for (int i = 0; i < ds; i++) {
/* 457 */         float[] pixels = (float[])is.getPixels(i + 1);
/* 458 */         for (int j = 0; j < height; j++)
/* 459 */           newpix[((ds - i - 1) * height + j)] = pixels[(x + j * width)];
/*     */       }
/* 461 */       this.fp2.setPixels(newpix);
/*     */     }
/*     */ 
/* 464 */     if ((ip instanceof ByteProcessor)) {
/* 465 */       byte[] newpix = new byte[ds * height];
/* 466 */       for (int i = 0; i < ds; i++) {
/* 467 */         byte[] pixels = (byte[])is.getPixels(i + 1);
/* 468 */         for (int j = 0; j < height; j++)
/* 469 */           newpix[((ds - i - 1) * height + j)] = pixels[(x + j * width)];
/*     */       }
/* 471 */       this.fp2.setPixels(newpix);
/*     */     }
/*     */ 
/* 474 */     if ((ip instanceof ShortProcessor)) {
/* 475 */       short[] newpix = new short[ds * height];
/* 476 */       for (int i = 0; i < ds; i++) {
/* 477 */         short[] pixels = (short[])is.getPixels(i + 1);
/* 478 */         for (int j = 0; j < height; j++)
/* 479 */           newpix[((ds - i - 1) * height + j)] = pixels[(x + j * width)];
/*     */       }
/* 481 */       this.fp2.setPixels(newpix);
/*     */     }
/*     */ 
/* 484 */     if ((ip instanceof ColorProcessor)) {
/* 485 */       int[] newpix = new int[ds * height];
/* 486 */       for (int i = 0; i < ds; i++) {
/* 487 */         int[] pixels = (int[])is.getPixels(i + 1);
/* 488 */         for (int j = 0; j < height; j++)
/* 489 */           newpix[((ds - i - 1) * height + j)] = pixels[(x + j * width)];
/*     */       }
/* 491 */       this.fp2.setPixels(newpix);
/*     */     }
/* 493 */     if (!this.flipXZ) this.fp2.flipVertical();
/*     */   }
/*     */ 
/*     */   void updateZYView(Point p, ImageStack is)
/*     */   {
/* 498 */     int width = is.getWidth();
/* 499 */     int height = is.getHeight();
/* 500 */     int ds = is.getSize();
/* 501 */     ImageProcessor ip = is.getProcessor(1);
/* 502 */     int x = p.x;
/*     */ 
/* 504 */     if ((ip instanceof FloatProcessor)) {
/* 505 */       float[] newpix = new float[ds * height];
/* 506 */       for (int i = 0; i < ds; i++) {
/* 507 */         float[] pixels = (float[])is.getPixels(i + 1);
/* 508 */         for (int y = 0; y < height; y++)
/* 509 */           newpix[(i + y * ds)] = pixels[(x + y * width)];
/*     */       }
/* 511 */       this.fp2.setPixels(newpix);
/*     */     }
/*     */ 
/* 514 */     if ((ip instanceof ByteProcessor)) {
/* 515 */       byte[] newpix = new byte[ds * height];
/* 516 */       for (int i = 0; i < ds; i++) {
/* 517 */         byte[] pixels = (byte[])is.getPixels(i + 1);
/* 518 */         for (int y = 0; y < height; y++)
/* 519 */           newpix[(i + y * ds)] = pixels[(x + y * width)];
/*     */       }
/* 521 */       this.fp2.setPixels(newpix);
/*     */     }
/*     */ 
/* 524 */     if ((ip instanceof ShortProcessor)) {
/* 525 */       short[] newpix = new short[ds * height];
/* 526 */       for (int i = 0; i < ds; i++) {
/* 527 */         short[] pixels = (short[])is.getPixels(i + 1);
/* 528 */         for (int y = 0; y < height; y++)
/* 529 */           newpix[(i + y * ds)] = pixels[(x + y * width)];
/*     */       }
/* 531 */       this.fp2.setPixels(newpix);
/*     */     }
/*     */ 
/* 534 */     if ((ip instanceof ColorProcessor)) {
/* 535 */       int[] newpix = new int[ds * height];
/* 536 */       for (int i = 0; i < ds; i++) {
/* 537 */         int[] pixels = (int[])is.getPixels(i + 1);
/* 538 */         for (int y = 0; y < height; y++)
/* 539 */           newpix[(i + y * ds)] = pixels[(x + y * width)];
/*     */       }
/* 541 */       this.fp2.setPixels(newpix);
/*     */     }
/*     */   }
/*     */ 
/*     */   void drawCross(ImagePlus imp, Point p, GeneralPath path)
/*     */   {
/* 548 */     int width = imp.getWidth();
/* 549 */     int height = imp.getHeight();
/* 550 */     float x = p.x;
/* 551 */     float y = p.y;
/* 552 */     path.moveTo(0.0F, y);
/* 553 */     path.lineTo(width, y);
/* 554 */     path.moveTo(x, 0.0F);
/* 555 */     path.lineTo(x, height);
/*     */   }
/*     */ 
/*     */   void dispose() {
/* 559 */     this.updater.quit();
/* 560 */     this.updater = null;
/* 561 */     this.imp.setOverlay(null);
/* 562 */     this.canvas.removeMouseListener(this);
/* 563 */     this.canvas.removeMouseMotionListener(this);
/* 564 */     this.canvas.removeKeyListener(this);
/* 565 */     this.canvas.setCustomRoi(false);
/* 566 */     this.xz_image.setOverlay(null);
/* 567 */     ImageWindow win1 = this.xz_image.getWindow();
/* 568 */     if (win1 != null) {
/* 569 */       win1.removeMouseWheelListener(this);
/* 570 */       ImageCanvas ic = win1.getCanvas();
/* 571 */       if (ic != null) {
/* 572 */         ic.removeKeyListener(this);
/* 573 */         ic.removeMouseListener(this);
/* 574 */         ic.removeMouseMotionListener(this);
/* 575 */         ic.setCustomRoi(false);
/*     */       }
/*     */     }
/* 578 */     this.yz_image.setOverlay(null);
/* 579 */     ImageWindow win2 = this.yz_image.getWindow();
/* 580 */     if (win2 != null) {
/* 581 */       win2.removeMouseWheelListener(this);
/* 582 */       ImageCanvas ic = win2.getCanvas();
/* 583 */       if (ic != null) {
/* 584 */         ic.removeKeyListener(this);
/* 585 */         ic.removeMouseListener(this);
/* 586 */         ic.removeMouseMotionListener(this);
/* 587 */         ic.setCustomRoi(false);
/*     */       }
/*     */     }
/* 590 */     ImagePlus.removeImageListener(this);
/* 591 */     Executer.removeCommandListener(this);
/* 592 */     this.win.removeWindowListener(this);
/* 593 */     this.win.removeFocusListener(this);
/* 594 */     this.win.setResizable(true);
/* 595 */     instance = null;
/* 596 */     previousID = this.imp.getID();
/* 597 */     previousX = this.crossLoc.x;
/* 598 */     previousY = this.crossLoc.y;
/* 599 */     this.imageStack = null;
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e) {
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e) {
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e) {
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e) {
/* 612 */     ImageCanvas xyCanvas = this.imp.getCanvas();
/* 613 */     this.startingSrcRect = ((Rectangle)xyCanvas.getSrcRect().clone());
/* 614 */     mouseDragged(e);
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e) {
/* 618 */     if (IJ.spaceBarDown())
/* 619 */       return;
/* 620 */     if (e.getSource().equals(this.canvas)) {
/* 621 */       this.crossLoc = this.canvas.getCursorLoc();
/* 622 */     } else if (e.getSource().equals(this.xz_image.getCanvas())) {
/* 623 */       this.crossLoc.x = this.xz_image.getCanvas().getCursorLoc().x;
/* 624 */       int pos = this.xz_image.getCanvas().getCursorLoc().y;
/* 625 */       int z = (int)Math.round(pos / this.az);
/* 626 */       int slice = this.flipXZ ? this.imp.getNSlices() - z : z + 1;
/* 627 */       if (this.hyperstack)
/* 628 */         this.imp.setPosition(this.imp.getChannel(), slice, this.imp.getFrame());
/*     */       else
/* 630 */         this.imp.setSlice(slice);
/* 631 */     } else if (e.getSource().equals(this.yz_image.getCanvas()))
/*     */     {
/*     */       int pos;
/*     */       int pos;
/* 633 */       if (this.rotateYZ) {
/* 634 */         this.crossLoc.y = this.yz_image.getCanvas().getCursorLoc().x;
/* 635 */         pos = this.yz_image.getCanvas().getCursorLoc().y;
/*     */       } else {
/* 637 */         this.crossLoc.y = this.yz_image.getCanvas().getCursorLoc().y;
/* 638 */         pos = this.yz_image.getCanvas().getCursorLoc().x;
/*     */       }
/* 640 */       int z = (int)Math.round(pos / this.az);
/* 641 */       if (this.hyperstack)
/* 642 */         this.imp.setPosition(this.imp.getChannel(), z + 1, this.imp.getFrame());
/*     */       else
/* 644 */         this.imp.setSlice(z + 1);
/*     */     }
/* 646 */     update();
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e) {
/* 650 */     ImageCanvas ic = this.imp.getCanvas();
/* 651 */     Rectangle srcRect = ic.getSrcRect();
/* 652 */     if ((srcRect.x != this.startingSrcRect.x) || (srcRect.y != this.startingSrcRect.y))
/*     */     {
/* 654 */       int dy = srcRect.y - this.startingSrcRect.y;
/* 655 */       ImageCanvas yzic = this.yz_image.getCanvas();
/* 656 */       Rectangle yzSrcRect = yzic.getSrcRect();
/* 657 */       if (this.rotateYZ) {
/* 658 */         yzSrcRect.x += dy;
/* 659 */         if (yzSrcRect.x < 0)
/* 660 */           yzSrcRect.x = 0;
/* 661 */         if (yzSrcRect.x > this.yz_image.getWidth() - yzSrcRect.width)
/* 662 */           yzSrcRect.y = (this.yz_image.getWidth() - yzSrcRect.width);
/*     */       } else {
/* 664 */         yzSrcRect.y += dy;
/* 665 */         if (yzSrcRect.y < 0)
/* 666 */           yzSrcRect.y = 0;
/* 667 */         if (yzSrcRect.y > this.yz_image.getHeight() - yzSrcRect.height)
/* 668 */           yzSrcRect.y = (this.yz_image.getHeight() - yzSrcRect.height);
/*     */       }
/* 670 */       yzic.repaint();
/* 671 */       int dx = srcRect.x - this.startingSrcRect.x;
/* 672 */       ImageCanvas xzic = this.xz_image.getCanvas();
/* 673 */       Rectangle xzSrcRect = xzic.getSrcRect();
/* 674 */       xzSrcRect.x += dx;
/* 675 */       if (xzSrcRect.x < 0)
/* 676 */         xzSrcRect.x = 0;
/* 677 */       if (xzSrcRect.x > this.xz_image.getWidth() - xzSrcRect.width)
/* 678 */         xzSrcRect.x = (this.xz_image.getWidth() - xzSrcRect.width);
/* 679 */       xzic.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   void update()
/*     */   {
/* 688 */     if (this.updater != null)
/* 689 */       this.updater.doUpdate();
/*     */   }
/*     */ 
/*     */   private void exec() {
/* 693 */     if (this.canvas == null) return;
/* 694 */     int width = this.imp.getWidth();
/* 695 */     int height = this.imp.getHeight();
/* 696 */     if (this.hyperstack) {
/* 697 */       int c = this.imp.getChannel();
/* 698 */       int t = this.imp.getFrame();
/* 699 */       if ((c != this.currentChannel) || (t != this.currentFrame))
/* 700 */         this.imageStack = null;
/* 701 */       if (this.imp.isComposite()) {
/* 702 */         int mode = ((CompositeImage)this.imp).getMode();
/* 703 */         if (mode != this.currentMode)
/* 704 */           this.imageStack = null;
/*     */       }
/*     */     }
/* 707 */     ImageStack is = this.imageStack;
/* 708 */     if (is == null)
/* 709 */       is = this.imageStack = getStack();
/* 710 */     double arat = this.az / this.ax;
/* 711 */     double brat = this.az / this.ay;
/* 712 */     Point p = this.crossLoc;
/* 713 */     if (p.y >= height) p.y = (height - 1);
/* 714 */     if (p.x >= width) p.x = (width - 1);
/* 715 */     if (p.x < 0) p.x = 0;
/* 716 */     if (p.y < 0) p.y = 0;
/* 717 */     updateViews(p, is);
/* 718 */     GeneralPath path = new GeneralPath();
/* 719 */     drawCross(this.imp, p, path);
/* 720 */     this.imp.setOverlay(path, this.color, new BasicStroke(1.0F));
/* 721 */     this.canvas.setCustomRoi(true);
/* 722 */     updateCrosses(p.x, p.y, arat, brat);
/* 723 */     if (this.syncZoom) updateMagnification(p.x, p.y);
/* 724 */     arrangeWindows(sticky);
/*     */   }
/*     */ 
/*     */   private void updateCrosses(int x, int y, double arat, double brat)
/*     */   {
/* 729 */     int z = this.imp.getNSlices();
/* 730 */     int zlice = this.imp.getSlice() - 1;
/* 731 */     int zcoord = (int)Math.round(arat * zlice);
/* 732 */     if (this.flipXZ) zcoord = (int)Math.round(arat * (z - zlice));
/*     */ 
/* 734 */     ImageCanvas xzCanvas = this.xz_image.getCanvas();
/* 735 */     Point p = new Point(x, zcoord);
/* 736 */     GeneralPath path = new GeneralPath();
/* 737 */     drawCross(this.xz_image, p, path);
/* 738 */     this.xz_image.setOverlay(path, this.color, new BasicStroke(1.0F));
/* 739 */     if (this.rotateYZ) {
/* 740 */       if (this.flipXZ)
/* 741 */         zcoord = (int)Math.round(brat * (z - zlice));
/*     */       else
/* 743 */         zcoord = (int)Math.round(brat * zlice);
/* 744 */       p = new Point(y, zcoord);
/*     */     } else {
/* 746 */       zcoord = (int)Math.round(arat * zlice);
/* 747 */       p = new Point(zcoord, y);
/*     */     }
/* 749 */     path = new GeneralPath();
/* 750 */     drawCross(this.yz_image, p, path);
/* 751 */     this.yz_image.setOverlay(path, this.color, new BasicStroke(1.0F));
/* 752 */     IJ.showStatus(this.imp.getLocationAsString(this.crossLoc.x, this.crossLoc.y));
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent e) {
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e) {
/* 759 */     int key = e.getKeyCode();
/* 760 */     if (key == 27) {
/* 761 */       IJ.beep();
/* 762 */       dispose();
/* 763 */     } else if (IJ.shiftKeyDown()) {
/* 764 */       int width = this.imp.getWidth(); int height = this.imp.getHeight();
/* 765 */       switch (key) { case 37:
/* 766 */         this.crossLoc.x -= 1; if (this.crossLoc.x < 0) this.crossLoc.x = 0; break;
/*     */       case 39:
/* 767 */         this.crossLoc.x += 1; if (this.crossLoc.x >= width) this.crossLoc.x = (width - 1); break;
/*     */       case 38:
/* 768 */         this.crossLoc.y -= 1; if (this.crossLoc.y < 0) this.crossLoc.y = 0; break;
/*     */       case 40:
/* 769 */         this.crossLoc.y += 1; if (this.crossLoc.y >= height) this.crossLoc.y = (height - 1); break;
/*     */       default:
/* 770 */         return;
/*     */       }
/* 772 */       update();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent e) {
/*     */   }
/*     */ 
/*     */   public void keyTyped(KeyEvent e) {
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent ev) {
/*     */   }
/*     */ 
/*     */   public void imageClosed(ImagePlus imp) {
/* 786 */     dispose();
/*     */   }
/*     */ 
/*     */   public void imageOpened(ImagePlus imp) {
/*     */   }
/*     */ 
/*     */   public void imageUpdated(ImagePlus imp) {
/* 793 */     if (imp == this.imp) {
/* 794 */       ImageProcessor ip = imp.getProcessor();
/* 795 */       this.min = ip.getMin();
/* 796 */       this.max = ip.getMax();
/* 797 */       update();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String commandExecuting(String command) {
/* 802 */     if ((command.equals("In")) || (command.equals("Out"))) {
/* 803 */       ImagePlus cimp = WindowManager.getCurrentImage();
/* 804 */       if (cimp == null) return command;
/* 805 */       if (cimp == this.imp) {
/* 806 */         ImageCanvas ic = this.imp.getCanvas();
/* 807 */         if (ic == null) return null;
/* 808 */         int x = ic.screenX(this.crossLoc.x);
/* 809 */         int y = ic.screenY(this.crossLoc.y);
/* 810 */         if (command.equals("In")) {
/* 811 */           ic.zoomIn(x, y);
/* 812 */           if (ic.getMagnification() <= 1.0D) this.imp.repaintWindow(); 
/*     */         }
/* 814 */         else { ic.zoomOut(x, y);
/* 815 */           if (ic.getMagnification() < 1.0D) this.imp.repaintWindow();
/*     */         }
/* 817 */         this.xyX = this.crossLoc.x; this.xyY = this.crossLoc.y;
/* 818 */         update();
/* 819 */         return null;
/* 820 */       }if ((cimp == this.xz_image) || (cimp == this.yz_image)) {
/* 821 */         this.syncZoom = false;
/* 822 */         return command;
/*     */       }
/* 824 */       return command;
/* 825 */     }if ((command.equals("Flip Vertically")) && (this.xz_image != null)) {
/* 826 */       if (this.xz_image == WindowManager.getCurrentImage()) {
/* 827 */         this.flipXZ = (!this.flipXZ);
/* 828 */         update();
/* 829 */         return null;
/*     */       }
/* 831 */       return command;
/*     */     }
/* 833 */     return command;
/*     */   }
/*     */ 
/*     */   public void windowActivated(WindowEvent e) {
/* 837 */     arrangeWindows(sticky);
/*     */   }
/*     */ 
/*     */   public void windowClosed(WindowEvent e) {
/*     */   }
/*     */ 
/*     */   public void windowClosing(WindowEvent e) {
/* 844 */     dispose();
/*     */   }
/*     */ 
/*     */   public void windowDeactivated(WindowEvent e) {
/*     */   }
/*     */ 
/*     */   public void windowDeiconified(WindowEvent e) {
/* 851 */     arrangeWindows(sticky);
/*     */   }
/*     */ 
/*     */   public void windowIconified(WindowEvent e) {
/*     */   }
/*     */ 
/*     */   public void windowOpened(WindowEvent e) {
/*     */   }
/*     */ 
/*     */   public void adjustmentValueChanged(AdjustmentEvent e) {
/* 861 */     update();
/*     */   }
/*     */ 
/*     */   public void mouseWheelMoved(MouseWheelEvent e) {
/* 865 */     if (e.getSource().equals(this.xz_image.getWindow()))
/* 866 */       this.crossLoc.y += e.getWheelRotation();
/* 867 */     else if (e.getSource().equals(this.yz_image.getWindow())) {
/* 868 */       this.crossLoc.x += e.getWheelRotation();
/*     */     }
/* 870 */     update();
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e) {
/* 874 */     ImageCanvas ic = this.imp.getCanvas();
/* 875 */     if (ic != null) this.canvas.requestFocus();
/* 876 */     arrangeWindows(sticky);
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e) {
/* 880 */     arrangeWindows(sticky);
/*     */   }
/*     */ 
/*     */   public static ImagePlus getImage() {
/* 884 */     if (instance != null) {
/* 885 */       return instance.imp;
/*     */     }
/* 887 */     return null;
/*     */   }
/*     */ 
/*     */   public static synchronized boolean isOrthoViewsImage(ImagePlus imp) {
/* 891 */     if ((imp == null) || (instance == null)) {
/* 892 */       return false;
/*     */     }
/* 894 */     return (imp == instance.imp) || (imp == instance.xz_image) || (imp == instance.yz_image);
/*     */   }
/*     */ 
/*     */   public static Orthogonal_Views getInstance() {
/* 898 */     return instance;
/*     */   }
/*     */ 
/*     */   public int[] getCrossLoc() {
/* 902 */     int[] loc = new int[3];
/* 903 */     loc[0] = this.crossLoc.x;
/* 904 */     loc[1] = this.crossLoc.y;
/* 905 */     loc[2] = (this.imp.getSlice() - 1);
/* 906 */     return loc;
/*     */   }
/*     */ 
/*     */   public void setCrossLoc(int x, int y, int z) {
/* 910 */     this.crossLoc.setLocation(x, y);
/* 911 */     if (this.hyperstack)
/* 912 */       this.imp.setPosition(this.imp.getChannel(), z + 1, this.imp.getFrame());
/*     */     else
/* 914 */       this.imp.setSlice(z + 1);
/* 915 */     update();
/*     */   }
/*     */ 
/*     */   public ImagePlus getXZImage() {
/* 919 */     return this.xz_image;
/*     */   }
/*     */ 
/*     */   public ImagePlus getYZImage() {
/* 923 */     return this.yz_image;
/*     */   }
/*     */ 
/*     */   private class Updater extends Thread
/*     */   {
/* 933 */     long request = 0L;
/*     */ 
/*     */     Updater()
/*     */     {
/* 937 */       super();
/* 938 */       setPriority(5);
/* 939 */       start();
/*     */     }
/*     */ 
/*     */     void doUpdate() {
/* 943 */       if (isInterrupted()) return;
/* 944 */       synchronized (this) {
/* 945 */         this.request += 1L;
/* 946 */         notify();
/*     */       }
/*     */     }
/*     */ 
/*     */     void quit() {
/* 951 */       IJ.wait(10);
/* 952 */       interrupt();
/* 953 */       synchronized (this) {
/* 954 */         notify();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run() {
/* 959 */       while (!isInterrupted())
/*     */         try
/*     */         {
/*     */           long r;
/* 962 */           synchronized (this) {
/* 963 */             r = this.request;
/*     */           }
/*     */ 
/* 966 */           if (r > 0L)
/* 967 */             Orthogonal_Views.this.exec();
/* 968 */           synchronized (this) {
/* 969 */             if (r == this.request) {
/* 970 */               this.request = 0L;
/* 971 */               wait();
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Orthogonal_Views
 * JD-Core Version:    0.6.2
 */