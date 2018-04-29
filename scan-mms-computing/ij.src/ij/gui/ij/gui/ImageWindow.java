/*     */ package ij.gui;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Menus;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.io.FileSaver;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.measure.Calibration;
/*     */ import ij.plugin.frame.Channels;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.awt.event.MouseWheelListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.awt.event.WindowStateListener;
/*     */ 
/*     */ public class ImageWindow extends Frame
/*     */   implements FocusListener, WindowListener, WindowStateListener, MouseWheelListener
/*     */ {
/*     */   public static final int MIN_WIDTH = 128;
/*     */   public static final int MIN_HEIGHT = 32;
/*     */   protected ImagePlus imp;
/*     */   protected ImageJ ij;
/*     */   protected ImageCanvas ic;
/*  23 */   private double initialMagnification = 1.0D;
/*     */   private int newWidth;
/*     */   private int newHeight;
/*     */   protected boolean closed;
/*     */   private boolean newCanvas;
/*  27 */   private boolean unzoomWhenMinimizing = true;
/*     */   Rectangle maxWindowBounds;
/*     */   Rectangle maxBounds;
/*     */   long setMaxBoundsTime;
/*     */   private static final int XINC = 8;
/*     */   private static final int YINC = 12;
/*     */   private static final int TEXT_GAP = 10;
/*  35 */   private static int xbase = -1;
/*     */   private static int ybase;
/*     */   private static int xloc;
/*     */   private static int yloc;
/*     */   private static int count;
/*     */   private static boolean centerOnScreen;
/*     */   private static Point nextLocation;
/*  43 */   private int textGap = centerOnScreen ? 0 : 10;
/*     */   public boolean running;
/*     */   public boolean running2;
/*     */ 
/*     */   public ImageWindow(String title)
/*     */   {
/*  53 */     super(title);
/*     */   }
/*     */ 
/*     */   public ImageWindow(ImagePlus imp) {
/*  57 */     this(imp, null);
/*     */   }
/*     */ 
/*     */   public ImageWindow(ImagePlus imp, ImageCanvas ic) {
/*  61 */     super(imp.getTitle());
/*  62 */     if ((Prefs.blackCanvas) && (getClass().getName().equals("ij.gui.ImageWindow"))) {
/*  63 */       setForeground(Color.white);
/*  64 */       setBackground(Color.black);
/*     */     } else {
/*  66 */       setForeground(Color.black);
/*  67 */       if (IJ.isLinux())
/*  68 */         setBackground(ImageJ.backgroundColor);
/*     */       else
/*  70 */         setBackground(Color.white);
/*     */     }
/*  72 */     boolean hyperstack = imp.isHyperStack();
/*  73 */     this.ij = IJ.getInstance();
/*  74 */     this.imp = imp;
/*  75 */     if (ic == null) {
/*  76 */       ic = new ImageCanvas(imp); this.newCanvas = true;
/*  77 */     }this.ic = ic;
/*  78 */     ImageWindow previousWindow = imp.getWindow();
/*  79 */     setLayout(new ImageLayout(ic));
/*  80 */     add(ic);
/*  81 */     addFocusListener(this);
/*  82 */     addWindowListener(this);
/*  83 */     addWindowStateListener(this);
/*  84 */     addKeyListener(this.ij);
/*  85 */     setFocusTraversalKeysEnabled(false);
/*  86 */     if (!(this instanceof StackWindow))
/*  87 */       addMouseWheelListener(this);
/*  88 */     setResizable(true);
/*  89 */     WindowManager.addWindow(this);
/*  90 */     imp.setWindow(this);
/*  91 */     if (previousWindow != null) {
/*  92 */       if (this.newCanvas)
/*  93 */         setLocationAndSize(false);
/*     */       else
/*  95 */         ic.update(previousWindow.getCanvas());
/*  96 */       Point loc = previousWindow.getLocation();
/*  97 */       setLocation(loc.x, loc.y);
/*  98 */       if (!(this instanceof StackWindow)) {
/*  99 */         pack();
/* 100 */         show();
/*     */       }
/* 102 */       if (ic.getMagnification() != 0.0D)
/* 103 */         imp.setTitle(imp.getTitle());
/* 104 */       boolean unlocked = imp.lockSilently();
/* 105 */       boolean changes = imp.changes;
/* 106 */       imp.changes = false;
/* 107 */       previousWindow.close();
/* 108 */       imp.changes = changes;
/* 109 */       if (unlocked)
/* 110 */         imp.unlock();
/* 111 */       if ((hyperstack) && (this.imp != null))
/* 112 */         this.imp.setOpenAsHyperStack(true);
/* 113 */       WindowManager.setCurrentWindow(this);
/*     */     } else {
/* 115 */       setLocationAndSize(false);
/* 116 */       if ((this.ij != null) && (!IJ.isMacintosh())) {
/* 117 */         Image img = this.ij.getIconImage();
/* 118 */         if (img != null) try {
/* 119 */             setIconImage(img); } catch (Exception e) {  }
/*     */  
/*     */       }
/* 121 */       if (centerOnScreen) {
/* 122 */         GUI.center(this);
/* 123 */         centerOnScreen = false;
/* 124 */       } else if (nextLocation != null) {
/* 125 */         setLocation(nextLocation);
/* 126 */         nextLocation = null;
/*     */       }
/* 128 */       if ((Interpreter.isBatchMode()) || ((IJ.getInstance() == null) && ((this instanceof HistogramWindow)))) {
/* 129 */         WindowManager.setTempCurrentImage(imp);
/* 130 */         Interpreter.addBatchModeImage(imp);
/*     */       } else {
/* 132 */         show();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 137 */   private void setLocationAndSize(boolean updating) { int width = this.imp.getWidth();
/* 138 */     int height = this.imp.getHeight();
/* 139 */     Rectangle maxWindow = getMaxWindow(0, 0);
/*     */ 
/* 142 */     if (WindowManager.getWindowCount() <= 1)
/* 143 */       xbase = -1;
/* 144 */     if ((width > maxWindow.width / 2) && (xbase > maxWindow.x + 5 + 48))
/* 145 */       xbase = -1;
/* 146 */     if (xbase == -1) {
/* 147 */       count = 0;
/* 148 */       xbase = maxWindow.x + 5;
/* 149 */       if (width * 2 <= maxWindow.width)
/* 150 */         xbase = maxWindow.x + maxWindow.width / 2 - width / 2;
/* 151 */       ybase = maxWindow.y;
/* 152 */       xloc = xbase;
/* 153 */       yloc = ybase;
/*     */     }
/* 155 */     int x = xloc;
/* 156 */     int y = yloc;
/* 157 */     xloc += 8;
/* 158 */     yloc += 12;
/* 159 */     count += 1;
/* 160 */     if (count % 6 == 0) {
/* 161 */       xloc = xbase;
/* 162 */       yloc = ybase;
/*     */     }
/*     */ 
/* 165 */     int sliderHeight = (this instanceof StackWindow) ? 20 : 0;
/* 166 */     int screenHeight = maxWindow.y + maxWindow.height - sliderHeight;
/* 167 */     double mag = 1.0D;
/* 168 */     while ((xbase + 32 + width * mag > maxWindow.x + maxWindow.width) || (ybase + height * mag >= screenHeight))
/*     */     {
/* 170 */       double mag2 = ImageCanvas.getLowerZoomLevel(mag);
/* 171 */       if (mag2 == mag) break;
/* 172 */       mag = mag2;
/*     */     }
/*     */ 
/* 175 */     if (mag < 1.0D) {
/* 176 */       this.initialMagnification = mag;
/* 177 */       this.ic.setDrawingSize((int)(width * mag), (int)(height * mag));
/*     */     }
/* 179 */     this.ic.setMagnification(mag);
/* 180 */     if (y + height * mag > screenHeight)
/* 181 */       y = ybase;
/* 182 */     if (!updating) setLocation(x, y);
/* 183 */     if ((Prefs.open100Percent) && (this.ic.getMagnification() < 1.0D)) {
/* 184 */       while (this.ic.getMagnification() < 1.0D)
/* 185 */         this.ic.zoomIn(0, 0);
/* 186 */       setSize(Math.min(width, maxWindow.width - x), Math.min(height, screenHeight - y));
/* 187 */       validate();
/*     */     } else {
/* 189 */       pack();
/*     */     } }
/*     */ 
/*     */   Rectangle getMaxWindow(int xloc, int yloc) {
/* 193 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 194 */     Rectangle bounds = ge.getMaximumWindowBounds();
/*     */ 
/* 196 */     if (IJ.debugMode) IJ.log("getMaxWindow: " + bounds + "  " + xloc + "," + yloc);
/* 197 */     if ((xloc > bounds.x + bounds.width) || (yloc > bounds.y + bounds.height)) {
/* 198 */       Rectangle bounds2 = getSecondaryMonitorBounds(ge, xloc, yloc);
/* 199 */       if (bounds2 != null) return bounds2;
/*     */     }
/* 201 */     Dimension ijSize = this.ij != null ? this.ij.getSize() : new Dimension(0, 0);
/* 202 */     if (bounds.height > 600) {
/* 203 */       bounds.y += ijSize.height;
/* 204 */       bounds.height -= ijSize.height;
/*     */     }
/* 206 */     return bounds;
/*     */   }
/*     */ 
/*     */   private Rectangle getSecondaryMonitorBounds(GraphicsEnvironment ge, int xloc, int yloc)
/*     */   {
/* 211 */     GraphicsDevice[] gs = ge.getScreenDevices();
/* 212 */     for (int j = 0; j < gs.length; j++) {
/* 213 */       GraphicsDevice gd = gs[j];
/* 214 */       GraphicsConfiguration[] gc = gd.getConfigurations();
/* 215 */       for (int i = 0; i < gc.length; i++) {
/* 216 */         Rectangle bounds = gc[i].getBounds();
/*     */ 
/* 218 */         if ((bounds != null) && (bounds.contains(xloc, yloc)))
/* 219 */           return bounds;
/*     */       }
/*     */     }
/* 222 */     return null;
/*     */   }
/*     */ 
/*     */   public double getInitialMagnification() {
/* 226 */     return this.initialMagnification;
/*     */   }
/*     */ 
/*     */   public Insets getInsets()
/*     */   {
/* 231 */     Insets insets = super.getInsets();
/* 232 */     double mag = this.ic.getMagnification();
/* 233 */     int extraWidth = (int)((128.0D - this.imp.getWidth() * mag) / 2.0D);
/* 234 */     if (extraWidth < 0) extraWidth = 0;
/* 235 */     int extraHeight = (int)((32.0D - this.imp.getHeight() * mag) / 2.0D);
/* 236 */     if (extraHeight < 0) extraHeight = 0;
/* 237 */     insets = new Insets(insets.top + this.textGap + extraHeight, insets.left + extraWidth, insets.bottom + extraHeight, insets.right + extraWidth);
/* 238 */     return insets;
/*     */   }
/*     */ 
/*     */   public void drawInfo(Graphics g)
/*     */   {
/* 243 */     if (this.textGap != 0) {
/* 244 */       Insets insets = super.getInsets();
/* 245 */       if (this.imp.isComposite()) {
/* 246 */         CompositeImage ci = (CompositeImage)this.imp;
/* 247 */         if (ci.getMode() == 1)
/* 248 */           g.setColor(ci.getChannelColor());
/*     */       }
/* 250 */       g.drawString(createSubtitle(), insets.left + 5, insets.top + 10);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String createSubtitle()
/*     */   {
/* 256 */     String s = "";
/* 257 */     int nSlices = this.imp.getStackSize();
/* 258 */     if (nSlices > 1) {
/* 259 */       ImageStack stack = this.imp.getStack();
/* 260 */       int currentSlice = this.imp.getCurrentSlice();
/* 261 */       s = s + currentSlice + "/" + nSlices;
/* 262 */       String label = stack.getShortSliceLabel(currentSlice);
/* 263 */       if ((label != null) && (label.length() > 0)) {
/* 264 */         if (this.imp.isHyperStack()) label = label.replace(';', ' ');
/* 265 */         s = s + " (" + label + ")";
/*     */       }
/* 267 */       if (((this instanceof StackWindow)) && (this.running2)) {
/* 268 */         return s;
/*     */       }
/* 270 */       s = s + "; ";
/*     */     } else {
/* 272 */       String label = (String)this.imp.getProperty("Label");
/* 273 */       if (label != null) {
/* 274 */         int newline = label.indexOf('\n');
/* 275 */         if (newline > 0)
/* 276 */           label = label.substring(0, newline);
/* 277 */         int len = label.length();
/* 278 */         if ((len > 4) && (label.charAt(len - 4) == '.') && (!Character.isDigit(label.charAt(len - 1))))
/* 279 */           label = label.substring(0, len - 4);
/* 280 */         if (label.length() > 60)
/* 281 */           label = label.substring(0, 60);
/* 282 */         s = label + "; ";
/*     */       }
/*     */     }
/* 285 */     int type = this.imp.getType();
/* 286 */     Calibration cal = this.imp.getCalibration();
/* 287 */     if (cal.scaled()) {
/* 288 */       s = s + IJ.d2s(this.imp.getWidth() * cal.pixelWidth, 2) + "x" + IJ.d2s(this.imp.getHeight() * cal.pixelHeight, 2) + " " + cal.getUnits() + " (" + this.imp.getWidth() + "x" + this.imp.getHeight() + "); ";
/*     */     }
/*     */     else
/* 291 */       s = s + this.imp.getWidth() + "x" + this.imp.getHeight() + " pixels; ";
/* 292 */     double size = this.imp.getWidth() * this.imp.getHeight() * this.imp.getStackSize() / 1024.0D;
/* 293 */     switch (type) {
/*     */     case 0:
/*     */     case 3:
/* 296 */       s = s + "8-bit";
/* 297 */       break;
/*     */     case 1:
/* 299 */       s = s + "16-bit";
/* 300 */       size *= 2.0D;
/* 301 */       break;
/*     */     case 2:
/* 303 */       s = s + "32-bit";
/* 304 */       size *= 4.0D;
/* 305 */       break;
/*     */     case 4:
/* 307 */       s = s + "RGB";
/* 308 */       size *= 4.0D;
/*     */     }
/*     */ 
/* 311 */     if (this.imp.isInvertedLut())
/* 312 */       s = s + " (inverting LUT)";
/* 313 */     String s2 = null; String s3 = null;
/* 314 */     if (size < 1024.0D) {
/* 315 */       s2 = IJ.d2s(size, 0); s3 = "K";
/* 316 */     } else if (size < 10000.0D) {
/* 317 */       s2 = IJ.d2s(size / 1024.0D, 1); s3 = "MB";
/* 318 */     } else if (size < 1048576.0D) {
/* 319 */       s2 = IJ.d2s(Math.round(size / 1024.0D), 0); s3 = "MB";
/*     */     } else {
/* 321 */       s2 = IJ.d2s(size / 1048576.0D, 1); s3 = "GB";
/* 322 */     }if (s2.endsWith(".0")) s2 = s2.substring(0, s2.length() - 2);
/* 323 */     return s + "; " + s2 + s3;
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g)
/*     */   {
/* 328 */     drawInfo(g);
/* 329 */     Rectangle r = this.ic.getBounds();
/* 330 */     int extraWidth = 128 - r.width;
/* 331 */     int extraHeight = 32 - r.height;
/* 332 */     if ((extraWidth <= 0) && (extraHeight <= 0) && (!Prefs.noBorder) && (!IJ.isLinux()))
/* 333 */       g.drawRect(r.x - 1, r.y - 1, r.width + 1, r.height + 1);
/*     */   }
/*     */ 
/*     */   public boolean close()
/*     */   {
/* 339 */     boolean isRunning = (this.running) || (this.running2);
/* 340 */     this.running = (this.running2 = 0);
/* 341 */     boolean virtual = (this.imp.getStackSize() > 1) && (this.imp.getStack().isVirtual());
/* 342 */     if (isRunning) IJ.wait(500);
/* 343 */     if ((this.ij == null) || (IJ.getApplet() != null) || (Interpreter.isBatchMode()) || (IJ.macroRunning()) || (virtual))
/* 344 */       this.imp.changes = false;
/* 345 */     if (this.imp.changes)
/*     */     {
/* 347 */       String name = this.imp.getTitle();
/*     */       String msg;
/*     */       String msg;
/* 348 */       if (name.length() > 22)
/* 349 */         msg = "Save changes to\n\"" + name + "\"?";
/*     */       else
/* 351 */         msg = "Save changes to \"" + name + "\"?";
/* 352 */       YesNoCancelDialog d = new YesNoCancelDialog(this, "ImageJ", msg);
/* 353 */       if (d.cancelPressed())
/* 354 */         return false;
/* 355 */       if (d.yesPressed()) {
/* 356 */         FileSaver fs = new FileSaver(this.imp);
/* 357 */         if (!fs.save()) return false;
/*     */       }
/*     */     }
/* 360 */     this.closed = true;
/* 361 */     if (WindowManager.getWindowCount() == 0) {
/* 362 */       xloc = 0; yloc = 0;
/* 363 */     }WindowManager.removeWindow(this);
/*     */ 
/* 365 */     if ((this.ij != null) && (this.ij.quitting()))
/* 366 */       return true;
/* 367 */     dispose();
/* 368 */     if (this.imp != null)
/* 369 */       this.imp.flush();
/* 370 */     this.imp = null;
/* 371 */     return true;
/*     */   }
/*     */ 
/*     */   public ImagePlus getImagePlus() {
/* 375 */     return this.imp;
/*     */   }
/*     */ 
/*     */   public void setImage(ImagePlus imp2) {
/* 379 */     ImageCanvas ic = getCanvas();
/* 380 */     if ((ic == null) || (imp2 == null))
/* 381 */       return;
/* 382 */     this.imp = imp2;
/* 383 */     this.imp.setWindow(this);
/* 384 */     ic.updateImage(this.imp);
/* 385 */     ic.setImageUpdated();
/* 386 */     ic.repaint();
/* 387 */     repaint();
/*     */   }
/*     */ 
/*     */   public void updateImage(ImagePlus imp) {
/* 391 */     if (imp != this.imp)
/* 392 */       throw new IllegalArgumentException("imp!=this.imp");
/* 393 */     this.imp = imp;
/* 394 */     this.ic.updateImage(imp);
/* 395 */     setLocationAndSize(true);
/* 396 */     if ((this instanceof StackWindow)) {
/* 397 */       StackWindow sw = (StackWindow)this;
/* 398 */       int stackSize = imp.getStackSize();
/* 399 */       int nScrollbars = sw.getNScrollbars();
/* 400 */       if ((stackSize == 1) && (nScrollbars > 0))
/* 401 */         sw.removeScrollbars();
/* 402 */       else if ((stackSize > 1) && (nScrollbars == 0))
/* 403 */         sw.addScrollbars(imp);
/*     */     }
/* 405 */     pack();
/* 406 */     repaint();
/* 407 */     this.maxBounds = getMaximumBounds();
/* 408 */     setMaximizedBounds(this.maxBounds);
/* 409 */     this.setMaxBoundsTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public ImageCanvas getCanvas() {
/* 413 */     return this.ic;
/*     */   }
/*     */ 
/*     */   static ImagePlus getClipboard()
/*     */   {
/* 418 */     return ImagePlus.getClipboard();
/*     */   }
/*     */ 
/*     */   public Rectangle getMaximumBounds() {
/* 422 */     double width = this.imp.getWidth();
/* 423 */     double height = this.imp.getHeight();
/* 424 */     double iAspectRatio = width / height;
/* 425 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 426 */     Rectangle maxWindow = ge.getMaximumWindowBounds();
/* 427 */     this.maxWindowBounds = maxWindow;
/* 428 */     if (iAspectRatio / (maxWindow.width / maxWindow.height) > 0.75D) {
/* 429 */       maxWindow.y += 22;
/* 430 */       maxWindow.height -= 22;
/*     */     }
/* 432 */     Dimension extraSize = getExtraSize();
/* 433 */     double maxWidth = maxWindow.width - extraSize.width;
/* 434 */     double maxHeight = maxWindow.height - extraSize.height;
/* 435 */     double mAspectRatio = maxWidth / maxHeight;
/*     */     int wHeight;
/*     */     int wHeight;
/*     */     int wWidth;
/* 438 */     if (iAspectRatio >= mAspectRatio) {
/* 439 */       double mag = maxWidth / width;
/* 440 */       int wWidth = maxWindow.width;
/* 441 */       wHeight = (int)(height * mag + extraSize.height);
/*     */     } else {
/* 443 */       double mag = maxHeight / height;
/* 444 */       wHeight = maxWindow.height;
/* 445 */       wWidth = (int)(width * mag + extraSize.width);
/*     */     }
/* 447 */     int xloc = (int)(maxWidth - wWidth) / 2;
/* 448 */     if (xloc < 0) xloc = 0;
/* 449 */     return new Rectangle(xloc, maxWindow.y, wWidth, wHeight);
/*     */   }
/*     */ 
/*     */   Dimension getExtraSize() {
/* 453 */     Insets insets = getInsets();
/* 454 */     int extraWidth = insets.left + insets.right + 10;
/* 455 */     int extraHeight = insets.top + insets.bottom + 10;
/* 456 */     if (extraHeight == 20) extraHeight = 42;
/* 457 */     int members = getComponentCount();
/*     */ 
/* 459 */     for (int i = 1; i < members; i++) {
/* 460 */       Component m = getComponent(i);
/* 461 */       Dimension d = m.getPreferredSize();
/* 462 */       extraHeight += d.height + 5;
/* 463 */       if (IJ.debugMode) IJ.log(i + "  " + d.height + " " + extraHeight);
/*     */     }
/* 465 */     return new Dimension(extraWidth, extraHeight);
/*     */   }
/*     */ 
/*     */   public Component add(Component comp) {
/* 469 */     comp = super.add(comp);
/* 470 */     this.maxBounds = getMaximumBounds();
/*     */ 
/* 472 */     setMaximizedBounds(this.maxBounds);
/* 473 */     this.setMaxBoundsTime = System.currentTimeMillis();
/*     */ 
/* 475 */     return comp;
/*     */   }
/*     */ 
/*     */   public void maximize()
/*     */   {
/* 486 */     if (this.maxBounds == null)
/* 487 */       return;
/* 488 */     int width = this.imp.getWidth();
/* 489 */     int height = this.imp.getHeight();
/* 490 */     double aspectRatio = width / height;
/* 491 */     Dimension extraSize = getExtraSize();
/* 492 */     int extraHeight = extraSize.height;
/* 493 */     double mag = (this.maxBounds.height - extraHeight) / height;
/* 494 */     if (IJ.debugMode) IJ.log("maximize: " + mag + " " + this.ic.getMagnification() + " " + this.maxBounds);
/* 495 */     setSize(getMaximizedBounds().width, getMaximizedBounds().height);
/* 496 */     if ((mag > this.ic.getMagnification()) || (aspectRatio < 0.5D) || (aspectRatio > 2.0D)) {
/* 497 */       this.ic.setMagnification2(mag);
/* 498 */       this.ic.setSrcRect(new Rectangle(0, 0, width, height));
/* 499 */       this.ic.setDrawingSize((int)(width * mag), (int)(height * mag));
/* 500 */       validate();
/* 501 */       this.unzoomWhenMinimizing = true;
/*     */     } else {
/* 503 */       this.unzoomWhenMinimizing = false;
/*     */     }
/*     */   }
/*     */ 
/* 507 */   public void minimize() { if (this.unzoomWhenMinimizing)
/* 508 */       this.ic.unzoom();
/* 509 */     this.unzoomWhenMinimizing = true;
/*     */   }
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 514 */     return this.closed;
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e)
/*     */   {
/* 519 */     if ((!Interpreter.isBatchMode()) && (this.ij != null) && (!this.ij.quitting()))
/* 520 */       WindowManager.setCurrentWindow(this);
/*     */   }
/*     */ 
/*     */   public void windowActivated(WindowEvent e) {
/* 524 */     if (IJ.debugMode) IJ.log("windowActivated: " + this.imp.getTitle());
/* 525 */     ImageJ ij = IJ.getInstance();
/* 526 */     boolean quitting = (ij != null) && (ij.quitting());
/* 527 */     if ((IJ.isMacintosh()) && (ij != null) && (!quitting)) {
/* 528 */       IJ.wait(10);
/* 529 */       setMenuBar(Menus.getMenuBar());
/*     */     }
/* 531 */     this.imp.setActivated();
/* 532 */     if ((!this.closed) && (!quitting) && (!Interpreter.isBatchMode()))
/* 533 */       WindowManager.setCurrentWindow(this);
/* 534 */     Frame channels = Channels.getInstance();
/* 535 */     if ((channels != null) && (this.imp.isComposite()))
/* 536 */       ((Channels)channels).update();
/*     */   }
/*     */ 
/*     */   public void windowClosing(WindowEvent e)
/*     */   {
/* 541 */     if (this.closed)
/* 542 */       return;
/* 543 */     if (this.ij != null) {
/* 544 */       WindowManager.setCurrentWindow(this);
/* 545 */       IJ.doCommand("Close");
/*     */     }
/*     */     else {
/* 548 */       dispose();
/* 549 */       WindowManager.removeWindow(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void windowStateChanged(WindowEvent e) {
/* 554 */     int oldState = e.getOldState();
/* 555 */     int newState = e.getNewState();
/*     */ 
/* 557 */     if (((oldState & 0x6) == 0) && ((newState & 0x6) != 0))
/* 558 */       maximize();
/* 559 */     else if (((oldState & 0x6) != 0) && ((newState & 0x6) == 0))
/* 560 */       minimize(); 
/*     */   }
/*     */   public void windowClosed(WindowEvent e) {
/*     */   }
/*     */   public void windowDeactivated(WindowEvent e) {
/*     */   }
/*     */   public void focusLost(FocusEvent e) {
/*     */   }
/*     */   public void windowDeiconified(WindowEvent e) {  }
/*     */ 
/*     */   public void windowIconified(WindowEvent e) {  } 
/*     */   public void windowOpened(WindowEvent e) {  } 
/* 571 */   public void mouseWheelMoved(MouseWheelEvent event) { int rotation = event.getWheelRotation();
/* 572 */     int width = this.imp.getWidth();
/* 573 */     int height = this.imp.getHeight();
/* 574 */     Rectangle srcRect = this.ic.getSrcRect();
/* 575 */     int xstart = srcRect.x;
/* 576 */     int ystart = srcRect.y;
/* 577 */     if ((IJ.spaceBarDown()) || (srcRect.height == height)) {
/* 578 */       srcRect.x += rotation * Math.max(width / 200, 1);
/* 579 */       if (srcRect.x < 0) srcRect.x = 0;
/* 580 */       if (srcRect.x + srcRect.width > width) srcRect.x = (width - srcRect.width); 
/*     */     }
/* 582 */     else { srcRect.y += rotation * Math.max(height / 200, 1);
/* 583 */       if (srcRect.y < 0) srcRect.y = 0;
/* 584 */       if (srcRect.y + srcRect.height > height) srcRect.y = (height - srcRect.height);
/*     */     }
/* 586 */     if ((srcRect.x != xstart) || (srcRect.y != ystart))
/* 587 */       this.ic.repaint();
/*     */   }
/*     */ 
/*     */   public void copy(boolean cut)
/*     */   {
/* 593 */     this.imp.copy(cut);
/*     */   }
/*     */ 
/*     */   public void paste()
/*     */   {
/* 598 */     this.imp.paste();
/*     */   }
/*     */ 
/*     */   public void mouseMoved(int x, int y)
/*     */   {
/* 605 */     this.imp.mouseMoved(x, y);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 609 */     return this.imp != null ? this.imp.getTitle() : "";
/*     */   }
/*     */ 
/*     */   public static void centerNextImage()
/*     */   {
/* 615 */     centerOnScreen = true;
/*     */   }
/*     */ 
/*     */   public static void setNextLocation(Point loc)
/*     */   {
/* 620 */     nextLocation = loc;
/*     */   }
/*     */ 
/*     */   public void setLocationAndSize(int x, int y, int width, int height)
/*     */   {
/* 626 */     setBounds(x, y, width, height);
/* 627 */     getCanvas().fitToWindow();
/* 628 */     pack();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.ImageWindow
 * JD-Core Version:    0.6.2
 */