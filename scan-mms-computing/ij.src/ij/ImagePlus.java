/*      */ package ij;
/*      */ 
/*      */ import ij.gui.Arrow;
/*      */ import ij.gui.EllipseRoi;
/*      */ import ij.gui.FreehandRoi;
/*      */ import ij.gui.ImageCanvas;
/*      */ import ij.gui.ImageWindow;
/*      */ import ij.gui.Line;
/*      */ import ij.gui.OvalRoi;
/*      */ import ij.gui.Overlay;
/*      */ import ij.gui.PlotWindow;
/*      */ import ij.gui.PointRoi;
/*      */ import ij.gui.PolygonRoi;
/*      */ import ij.gui.Roi;
/*      */ import ij.gui.ShapeRoi;
/*      */ import ij.gui.StackWindow;
/*      */ import ij.gui.TextRoi;
/*      */ import ij.gui.Toolbar;
/*      */ import ij.io.FileInfo;
/*      */ import ij.io.FileOpener;
/*      */ import ij.io.Opener;
/*      */ import ij.macro.Interpreter;
/*      */ import ij.measure.Calibration;
/*      */ import ij.measure.Measurements;
/*      */ import ij.plugin.Duplicator;
/*      */ import ij.plugin.RectToolOptions;
/*      */ import ij.plugin.frame.ContrastAdjuster;
/*      */ import ij.plugin.frame.Recorder;
/*      */ import ij.process.ByteProcessor;
/*      */ import ij.process.ColorProcessor;
/*      */ import ij.process.FloatProcessor;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.process.ImageStatistics;
/*      */ import ij.process.LUT;
/*      */ import ij.process.ShortProcessor;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Canvas;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Image;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.PixelGrabber;
/*      */ import java.util.Properties;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class ImagePlus
/*      */   implements ImageObserver, Measurements, Cloneable
/*      */ {
/*      */   public static final int GRAY8 = 0;
/*      */   public static final int GRAY16 = 1;
/*      */   public static final int GRAY32 = 2;
/*      */   public static final int COLOR_256 = 3;
/*      */   public static final int COLOR_RGB = 4;
/*      */   public boolean changes;
/*      */   protected Image img;
/*      */   protected ImageProcessor ip;
/*      */   protected ImageWindow win;
/*      */   protected Roi roi;
/*      */   protected int currentSlice;
/*      */   protected static final int OPENED = 0;
/*      */   protected static final int CLOSED = 1;
/*      */   protected static final int UPDATED = 2;
/*      */   protected boolean compositeImage;
/*      */   protected int width;
/*      */   protected int height;
/*   61 */   protected boolean locked = false;
/*   62 */   protected int nChannels = 1;
/*   63 */   protected int nSlices = 1;
/*   64 */   protected int nFrames = 1;
/*      */ 
/*   66 */   private ImageJ ij = IJ.getInstance();
/*      */   private String title;
/*      */   private String url;
/*      */   private FileInfo fileInfo;
/*   70 */   private int imageType = 0;
/*      */   private ImageStack stack;
/*   72 */   private static int currentID = -1;
/*      */   private int ID;
/*      */   private static Component comp;
/*      */   private boolean imageLoaded;
/*      */   private int imageUpdateY;
/*      */   private int imageUpdateW;
/*      */   private Properties properties;
/*      */   private long startTime;
/*      */   private Calibration calibration;
/*      */   private static Calibration globalCalibration;
/*      */   private boolean activated;
/*      */   private boolean ignoreFlush;
/*      */   private boolean errorLoadingImage;
/*      */   private static ImagePlus clipboard;
/*   85 */   private static Vector listeners = new Vector();
/*      */   private boolean openAsHyperStack;
/*   87 */   private int[] position = { 1, 1, 1 };
/*      */   private boolean noUpdateMode;
/*      */   private ImageCanvas flatteningCanvas;
/*      */   private Overlay overlay;
/*      */   private boolean hideOverlay;
/*      */   private static int default16bitDisplayRange;
/*      */   long waitStart;
/* 1058 */   private int[] pvalue = new int[4];
/*      */   private int savex;
/*      */   private int savey;
/*      */ 
/*      */   public ImagePlus()
/*      */   {
/*   97 */     this.ID = (--currentID);
/*   98 */     this.title = "null";
/*      */   }
/*      */ 
/*      */   public ImagePlus(String title, Image img)
/*      */   {
/*  105 */     this.title = title;
/*  106 */     this.ID = (--currentID);
/*  107 */     if (img != null)
/*  108 */       setImage(img);
/*      */   }
/*      */ 
/*      */   public ImagePlus(String title, ImageProcessor ip)
/*      */   {
/*  113 */     setProcessor(title, ip);
/*  114 */     this.ID = (--currentID);
/*      */   }
/*      */ 
/*      */   public ImagePlus(String pathOrURL)
/*      */   {
/*  121 */     Opener opener = new Opener();
/*  122 */     ImagePlus imp = null;
/*  123 */     boolean isURL = pathOrURL.indexOf("://") > 0;
/*  124 */     if (isURL)
/*  125 */       imp = opener.openURL(pathOrURL);
/*      */     else
/*  127 */       imp = opener.openImage(pathOrURL);
/*  128 */     if (imp != null) {
/*  129 */       if (imp.getStackSize() > 1)
/*  130 */         setStack(imp.getTitle(), imp.getStack());
/*      */       else
/*  132 */         setProcessor(imp.getTitle(), imp.getProcessor());
/*  133 */       setCalibration(imp.getCalibration());
/*  134 */       this.properties = imp.getProperties();
/*  135 */       setFileInfo(imp.getOriginalFileInfo());
/*  136 */       setDimensions(imp.getNChannels(), imp.getNSlices(), imp.getNFrames());
/*  137 */       if (isURL)
/*  138 */         this.url = pathOrURL;
/*  139 */       this.ID = (--currentID);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ImagePlus(String title, ImageStack stack)
/*      */   {
/*  145 */     setStack(title, stack);
/*  146 */     this.ID = (--currentID);
/*      */   }
/*      */ 
/*      */   public synchronized boolean lock()
/*      */   {
/*  154 */     if (this.locked) {
/*  155 */       IJ.beep();
/*  156 */       IJ.showStatus("\"" + this.title + "\" is locked");
/*      */ 
/*  161 */       return false;
/*      */     }
/*  163 */     this.locked = true;
/*  164 */     if (IJ.debugMode) IJ.log(this.title + ": lock");
/*  165 */     return true;
/*      */   }
/*      */ 
/*      */   public synchronized boolean lockSilently()
/*      */   {
/*  172 */     if (this.locked) {
/*  173 */       return false;
/*      */     }
/*  175 */     this.locked = true;
/*  176 */     if (IJ.debugMode) IJ.log(this.title + ": lock silently");
/*  177 */     return true;
/*      */   }
/*      */ 
/*      */   public synchronized void unlock()
/*      */   {
/*  183 */     this.locked = false;
/*  184 */     if (IJ.debugMode) IJ.log(this.title + ": unlock"); 
/*      */   }
/*      */ 
/*      */   private void waitForImage(Image img)
/*      */   {
/*  188 */     if (comp == null) {
/*  189 */       comp = IJ.getInstance();
/*  190 */       if (comp == null)
/*  191 */         comp = new Canvas();
/*      */     }
/*  193 */     this.imageLoaded = false;
/*  194 */     if (!comp.prepareImage(img, this))
/*      */     {
/*  196 */       this.waitStart = System.currentTimeMillis();
/*  197 */       while ((!this.imageLoaded) && (!this.errorLoadingImage))
/*      */       {
/*  199 */         IJ.wait(30);
/*  200 */         if (this.imageUpdateW > 1) {
/*  201 */           double progress = this.imageUpdateY / this.imageUpdateW;
/*  202 */           if (progress >= 1.0D) {
/*  203 */             progress = 1.0D - (progress - 1.0D);
/*  204 */             if (progress < 0.0D) progress = 0.9D;
/*      */           }
/*  206 */           showProgress(progress);
/*      */         }
/*      */       }
/*  209 */       showProgress(1.0D);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void showProgress(double percent)
/*      */   {
/*  215 */     if (System.currentTimeMillis() - this.waitStart > 500L)
/*  216 */       IJ.showProgress(percent);
/*      */   }
/*      */ 
/*      */   public void draw()
/*      */   {
/*  224 */     if (this.win != null)
/*  225 */       this.win.getCanvas().repaint();
/*      */   }
/*      */ 
/*      */   public void draw(int x, int y, int width, int height)
/*      */   {
/*  230 */     if (this.win != null) {
/*  231 */       ImageCanvas ic = this.win.getCanvas();
/*  232 */       double mag = ic.getMagnification();
/*  233 */       x = ic.screenX(x);
/*  234 */       y = ic.screenY(y);
/*  235 */       width = (int)(width * mag);
/*  236 */       height = (int)(height * mag);
/*  237 */       ic.repaint(x, y, width, height);
/*  238 */       if ((listeners.size() > 0) && (this.roi != null) && (this.roi.getPasteMode() != -1))
/*  239 */         notifyListeners(2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateAndDraw()
/*      */   {
/*  248 */     if (this.ip != null) {
/*  249 */       if (this.win != null) {
/*  250 */         this.win.getCanvas().setImageUpdated();
/*  251 */         if (listeners.size() > 0) notifyListeners(2);
/*      */       }
/*  253 */       draw();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateChannelAndDraw()
/*      */   {
/*  262 */     updateAndDraw();
/*      */   }
/*      */ 
/*      */   public ImageProcessor getChannelProcessor()
/*      */   {
/*  269 */     return getProcessor();
/*      */   }
/*      */ 
/*      */   public LUT[] getLuts()
/*      */   {
/*  275 */     return null;
/*      */   }
/*      */ 
/*      */   public void repaintWindow()
/*      */   {
/*  290 */     if (this.win != null) {
/*  291 */       draw();
/*  292 */       this.win.repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateAndRepaintWindow()
/*      */   {
/*  301 */     if (this.win != null) {
/*  302 */       updateAndDraw();
/*  303 */       this.win.repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateImage()
/*      */   {
/*  310 */     if (this.ip != null)
/*  311 */       this.img = this.ip.createImage();
/*      */   }
/*      */ 
/*      */   public void hide()
/*      */   {
/*  316 */     if (this.win == null) {
/*  317 */       Interpreter.removeBatchModeImage(this);
/*  318 */       return;
/*      */     }
/*  320 */     boolean unlocked = lockSilently();
/*  321 */     this.changes = false;
/*  322 */     this.win.close();
/*  323 */     this.win = null;
/*  324 */     if (unlocked) unlock();
/*      */   }
/*      */ 
/*      */   public void close()
/*      */   {
/*  330 */     ImageWindow win = getWindow();
/*  331 */     if (win != null)
/*      */     {
/*  334 */       win.close();
/*      */     } else {
/*  336 */       if (WindowManager.getCurrentImage() == this)
/*  337 */         WindowManager.setTempCurrentImage(null);
/*  338 */       killRoi();
/*  339 */       Interpreter.removeBatchModeImage(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void show()
/*      */   {
/*  345 */     show("");
/*      */   }
/*      */ 
/*      */   public void show(String statusMessage)
/*      */   {
/*  351 */     if (this.win != null) return;
/*  352 */     if (((IJ.isMacro()) && (this.ij == null)) || (Interpreter.isBatchMode())) {
/*  353 */       if (isComposite()) ((CompositeImage)this).reset();
/*  354 */       ImagePlus img = WindowManager.getCurrentImage();
/*  355 */       if (img != null) img.saveRoi();
/*  356 */       WindowManager.setTempCurrentImage(this);
/*  357 */       Interpreter.addBatchModeImage(this);
/*  358 */       return;
/*      */     }
/*  360 */     if ((Prefs.useInvertingLut) && (getBitDepth() == 8) && (this.ip != null) && (!this.ip.isInvertedLut()) && (!this.ip.isColorLut()))
/*  361 */       invertLookupTable();
/*  362 */     this.img = getImage();
/*  363 */     if ((this.img != null) && (this.width >= 0) && (this.height >= 0)) {
/*  364 */       this.activated = false;
/*  365 */       int stackSize = getStackSize();
/*      */ 
/*  367 */       if (stackSize > 1)
/*  368 */         this.win = new StackWindow(this);
/*      */       else
/*  370 */         this.win = new ImageWindow(this);
/*  371 */       if (this.roi != null) this.roi.setImage(this);
/*  372 */       if ((this.overlay != null) && (getCanvas() != null))
/*  373 */         getCanvas().setOverlay(this.overlay);
/*  374 */       draw();
/*  375 */       IJ.showStatus(statusMessage);
/*  376 */       if (IJ.isMacro()) {
/*  377 */         long start = System.currentTimeMillis();
/*  378 */         while (!this.activated) {
/*  379 */           IJ.wait(5);
/*  380 */           if (System.currentTimeMillis() - start > 2000L) {
/*  381 */             WindowManager.setTempCurrentImage(this);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  386 */       if ((this.imageType == 1) && (default16bitDisplayRange != 0)) {
/*  387 */         resetDisplayRange();
/*  388 */         updateAndDraw();
/*      */       }
/*  390 */       if (stackSize > 1) {
/*  391 */         int c = getChannel();
/*  392 */         int z = getSlice();
/*  393 */         int t = getFrame();
/*  394 */         if ((c > 1) || (z > 1) || (t > 1))
/*  395 */           setPosition(c, z, t);
/*      */       }
/*  397 */       notifyListeners(0);
/*      */     }
/*      */   }
/*      */ 
/*      */   void invertLookupTable() {
/*  402 */     int nImages = getStackSize();
/*  403 */     this.ip.invertLut();
/*  404 */     if (nImages == 1) {
/*  405 */       this.ip.invert();
/*      */     } else {
/*  407 */       ImageStack stack2 = getStack();
/*  408 */       for (int i = 1; i <= nImages; i++)
/*  409 */         stack2.getProcessor(i).invert();
/*  410 */       stack2.setColorModel(this.ip.getColorModel());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setActivated()
/*      */   {
/*  416 */     this.activated = true;
/*      */   }
/*      */ 
/*      */   public Image getImage()
/*      */   {
/*  421 */     if ((this.img == null) && (this.ip != null))
/*  422 */       this.img = this.ip.createImage();
/*  423 */     return this.img;
/*      */   }
/*      */ 
/*      */   public BufferedImage getBufferedImage()
/*      */   {
/*  428 */     if (isComposite()) {
/*  429 */       return new ColorProcessor(getImage()).getBufferedImage();
/*      */     }
/*  431 */     return this.ip.getBufferedImage();
/*      */   }
/*      */ 
/*      */   public int getID()
/*      */   {
/*  436 */     return this.ID;
/*      */   }
/*      */ 
/*      */   public void setImage(Image img)
/*      */   {
/*  443 */     if ((img instanceof BufferedImage)) {
/*  444 */       BufferedImage bi = (BufferedImage)img;
/*  445 */       if (bi.getType() == 11) {
/*  446 */         setProcessor(null, new ShortProcessor(bi));
/*  447 */         return;
/*  448 */       }if (bi.getType() == 10) {
/*  449 */         setProcessor(null, new ByteProcessor(bi));
/*  450 */         return;
/*      */       }
/*      */     }
/*  453 */     this.roi = null;
/*  454 */     this.errorLoadingImage = false;
/*  455 */     waitForImage(img);
/*  456 */     if (this.errorLoadingImage)
/*  457 */       throw new IllegalStateException("Error loading image");
/*  458 */     this.img = img;
/*  459 */     int newWidth = img.getWidth(this.ij);
/*  460 */     int newHeight = img.getHeight(this.ij);
/*  461 */     boolean dimensionsChanged = (newWidth != this.width) || (newHeight != this.height);
/*  462 */     this.width = newWidth;
/*  463 */     this.height = newHeight;
/*  464 */     this.ip = null;
/*  465 */     this.stack = null;
/*  466 */     LookUpTable lut = new LookUpTable(img);
/*      */     int type;
/*      */     int type;
/*  468 */     if (lut.getMapSize() > 0)
/*      */     {
/*      */       int type;
/*  469 */       if (lut.isGrayscale())
/*  470 */         type = 0;
/*      */       else
/*  472 */         type = 3;
/*      */     } else {
/*  474 */       type = 4;
/*  475 */     }setType(type);
/*  476 */     setupProcessor();
/*  477 */     this.img = this.ip.createImage();
/*  478 */     if (this.win != null)
/*  479 */       if (dimensionsChanged)
/*  480 */         this.win = new ImageWindow(this);
/*      */       else
/*  482 */         repaintWindow();
/*      */   }
/*      */ 
/*      */   public void setImage(ImagePlus imp)
/*      */   {
/*  490 */     if (imp.getWindow() != null)
/*  491 */       imp = imp.duplicate();
/*  492 */     ImageStack stack2 = imp.getStack();
/*  493 */     if (imp.isHyperStack())
/*  494 */       setOpenAsHyperStack(true);
/*  495 */     setStack(stack2, imp.getNChannels(), imp.getNSlices(), imp.getNFrames());
/*      */   }
/*      */ 
/*      */   public void setProcessor(ImageProcessor ip)
/*      */   {
/*  500 */     setProcessor(null, ip);
/*      */   }
/*      */ 
/*      */   public void setProcessor(String title, ImageProcessor ip)
/*      */   {
/*  506 */     if ((ip == null) || (ip.getPixels() == null))
/*  507 */       throw new IllegalArgumentException("ip null or ip.getPixels() null");
/*  508 */     int stackSize = getStackSize();
/*  509 */     if ((stackSize > 1) && ((ip.getWidth() != this.width) || (ip.getHeight() != this.height)))
/*  510 */       throw new IllegalArgumentException("ip wrong size");
/*  511 */     if (stackSize <= 1) {
/*  512 */       this.stack = null;
/*  513 */       setCurrentSlice(1);
/*      */     }
/*  515 */     setProcessor2(title, ip, null);
/*      */   }
/*      */ 
/*      */   void setProcessor2(String title, ImageProcessor ip, ImageStack newStack) {
/*  519 */     if (title != null) setTitle(title);
/*  520 */     this.ip = ip;
/*  521 */     if (this.ij != null) ip.setProgressBar(this.ij.getProgressBar());
/*  522 */     int stackSize = 1;
/*  523 */     if (this.stack != null) {
/*  524 */       stackSize = this.stack.getSize();
/*  525 */       if (this.currentSlice > stackSize) setCurrentSlice(stackSize);
/*      */     }
/*  527 */     this.img = null;
/*  528 */     boolean dimensionsChanged = (this.width > 0) && (this.height > 0) && ((this.width != ip.getWidth()) || (this.height != ip.getHeight()));
/*  529 */     if (dimensionsChanged) this.roi = null;
/*      */     int type;
/*      */     int type;
/*  531 */     if ((ip instanceof ByteProcessor)) {
/*  532 */       type = 0;
/*      */     }
/*      */     else
/*      */     {
/*      */       int type;
/*  533 */       if ((ip instanceof ColorProcessor)) {
/*  534 */         type = 4;
/*      */       }
/*      */       else
/*      */       {
/*      */         int type;
/*  535 */         if ((ip instanceof ShortProcessor))
/*  536 */           type = 1;
/*      */         else
/*  538 */           type = 2; 
/*      */       }
/*      */     }
/*  539 */     if (this.width == 0)
/*  540 */       this.imageType = type;
/*      */     else
/*  542 */       setType(type);
/*  543 */     this.width = ip.getWidth();
/*  544 */     this.height = ip.getHeight();
/*  545 */     if (this.win != null) {
/*  546 */       if ((dimensionsChanged) && (stackSize == 1))
/*  547 */         this.win.updateImage(this);
/*  548 */       else if (newStack == null)
/*  549 */         repaintWindow();
/*  550 */       draw();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setStack(ImageStack stack)
/*      */   {
/*  556 */     setStack(null, stack);
/*      */   }
/*      */ 
/*      */   public void setStack(String title, ImageStack newStack)
/*      */   {
/*  562 */     int newStackSize = newStack.getSize();
/*  563 */     if (newStackSize == 0)
/*  564 */       throw new IllegalArgumentException("Stack is empty");
/*  565 */     if (!newStack.isVirtual()) {
/*  566 */       Object[] arrays = newStack.getImageArray();
/*  567 */       if ((arrays == null) || ((arrays.length > 0) && (arrays[0] == null)))
/*  568 */         throw new IllegalArgumentException("Stack pixel array null");
/*      */     }
/*  570 */     boolean sliderChange = false;
/*  571 */     if ((this.win != null) && ((this.win instanceof StackWindow))) {
/*  572 */       int nScrollbars = ((StackWindow)this.win).getNScrollbars();
/*  573 */       if ((nScrollbars > 0) && (newStackSize == 1))
/*  574 */         sliderChange = true;
/*  575 */       else if ((nScrollbars == 0) && (newStackSize > 1))
/*  576 */         sliderChange = true;
/*      */     }
/*  578 */     if (this.currentSlice < 1) setCurrentSlice(1);
/*  579 */     boolean resetCurrentSlice = this.currentSlice > newStackSize;
/*  580 */     if (resetCurrentSlice) setCurrentSlice(newStackSize);
/*  581 */     ImageProcessor ip = newStack.getProcessor(this.currentSlice);
/*  582 */     boolean dimensionsChanged = (this.width > 0) && (this.height > 0) && ((this.width != ip.getWidth()) || (this.height != ip.getHeight()));
/*  583 */     this.stack = newStack;
/*  584 */     setProcessor2(title, ip, newStack);
/*  585 */     if (this.win == null) {
/*  586 */       if (resetCurrentSlice) setSlice(this.currentSlice);
/*  587 */       return;
/*      */     }
/*  589 */     boolean invalidDimensions = (isDisplayedHyperStack()) && (!((StackWindow)this.win).validDimensions());
/*  590 */     if ((newStackSize > 1) && (!(this.win instanceof StackWindow))) {
/*  591 */       if (isDisplayedHyperStack()) setOpenAsHyperStack(true);
/*  592 */       this.win = new StackWindow(this, getCanvas());
/*  593 */       setPosition(1, 1, 1);
/*  594 */     } else if ((newStackSize > 1) && (invalidDimensions)) {
/*  595 */       if (isDisplayedHyperStack()) setOpenAsHyperStack(true);
/*  596 */       this.win = new StackWindow(this);
/*  597 */       setPosition(1, 1, 1);
/*  598 */     } else if ((dimensionsChanged) || (sliderChange)) {
/*  599 */       this.win.updateImage(this);
/*      */     } else {
/*  601 */       repaintWindow();
/*  602 */     }if (resetCurrentSlice) setSlice(this.currentSlice); 
/*      */   }
/*      */ 
/*      */   public void setStack(ImageStack stack, int nChannels, int nSlices, int nFrames)
/*      */   {
/*  606 */     if (nChannels * nSlices * nFrames != stack.getSize())
/*  607 */       throw new IllegalArgumentException("channels*slices*frames!=stackSize");
/*  608 */     this.nChannels = nChannels;
/*  609 */     this.nSlices = nSlices;
/*  610 */     this.nFrames = nFrames;
/*  611 */     if (isComposite())
/*  612 */       ((CompositeImage)this).setChannelsUpdated();
/*  613 */     setStack(null, stack);
/*      */   }
/*      */ 
/*      */   public void setFileInfo(FileInfo fi)
/*      */   {
/*  619 */     if (fi != null)
/*  620 */       fi.pixels = null;
/*  621 */     this.fileInfo = fi;
/*      */   }
/*      */ 
/*      */   public ImageWindow getWindow()
/*      */   {
/*  628 */     return this.win;
/*      */   }
/*      */ 
/*      */   public boolean isVisible()
/*      */   {
/*  633 */     return (this.win != null) && (this.win.isVisible());
/*      */   }
/*      */ 
/*      */   public void setWindow(ImageWindow win)
/*      */   {
/*  638 */     this.win = win;
/*  639 */     if (this.roi != null)
/*  640 */       this.roi.setImage(this);
/*      */   }
/*      */ 
/*      */   public ImageCanvas getCanvas()
/*      */   {
/*  646 */     return this.win != null ? this.win.getCanvas() : this.flatteningCanvas;
/*      */   }
/*      */ 
/*      */   public void setColor(Color c)
/*      */   {
/*  651 */     if (this.ip != null)
/*  652 */       this.ip.setColor(c);
/*      */   }
/*      */ 
/*      */   void setupProcessor() {
/*  656 */     if (this.imageType == 4) {
/*  657 */       if ((this.ip == null) || ((this.ip instanceof ByteProcessor)))
/*  658 */         this.ip = new ColorProcessor(getImage());
/*  659 */     } else if ((this.ip == null) || ((this.ip instanceof ColorProcessor)))
/*  660 */       this.ip = new ByteProcessor(getImage());
/*  661 */     if ((this.roi != null) && (this.roi.isArea()))
/*  662 */       this.ip.setRoi(this.roi.getBounds());
/*      */     else
/*  664 */       this.ip.resetRoi();
/*      */   }
/*      */ 
/*      */   public boolean isProcessor() {
/*  668 */     return this.ip != null;
/*      */   }
/*      */ 
/*      */   public ImageProcessor getProcessor()
/*      */   {
/*  677 */     if ((this.ip == null) && (this.img == null))
/*  678 */       return null;
/*  679 */     setupProcessor();
/*  680 */     if (!this.compositeImage)
/*  681 */       this.ip.setLineWidth(Line.getWidth());
/*  682 */     if (this.ij != null)
/*  683 */       this.ip.setProgressBar(this.ij.getProgressBar());
/*  684 */     Calibration cal = getCalibration();
/*  685 */     if (cal.calibrated())
/*  686 */       this.ip.setCalibrationTable(cal.getCTable());
/*      */     else
/*  688 */       this.ip.setCalibrationTable(null);
/*  689 */     if (Recorder.record) {
/*  690 */       Recorder recorder = Recorder.getInstance();
/*  691 */       if (recorder != null) recorder.imageUpdated(this);
/*      */     }
/*  693 */     return this.ip;
/*      */   }
/*      */ 
/*      */   public void trimProcessor()
/*      */   {
/*  699 */     ImageProcessor ip2 = this.ip;
/*  700 */     if ((!this.locked) && (ip2 != null)) {
/*  701 */       if (IJ.debugMode) IJ.log(this.title + ": trimProcessor");
/*  702 */       Roi roi2 = getRoi();
/*  703 */       if ((roi2 != null) && (roi2.getPasteMode() != -1))
/*  704 */         roi2.endPaste();
/*  705 */       ip2.setSnapshotPixels(null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ImageProcessor getMask()
/*      */   {
/*  712 */     if (this.roi == null) {
/*  713 */       if (this.ip != null) this.ip.resetRoi();
/*  714 */       return null;
/*      */     }
/*  716 */     ImageProcessor mask = this.roi.getMask();
/*  717 */     if (mask == null)
/*  718 */       return null;
/*  719 */     if ((this.ip != null) && (this.roi != null)) {
/*  720 */       this.ip.setMask(mask);
/*  721 */       this.ip.setRoi(this.roi.getBounds());
/*      */     }
/*  723 */     return mask;
/*      */   }
/*      */ 
/*      */   public ImageStatistics getStatistics()
/*      */   {
/*  745 */     return getStatistics(27);
/*      */   }
/*      */ 
/*      */   public ImageStatistics getStatistics(int mOptions)
/*      */   {
/*  766 */     return getStatistics(mOptions, 256, 0.0D, 0.0D);
/*      */   }
/*      */ 
/*      */   public ImageStatistics getStatistics(int mOptions, int nBins)
/*      */   {
/*  775 */     return getStatistics(mOptions, nBins, 0.0D, 0.0D);
/*      */   }
/*      */ 
/*      */   public ImageStatistics getStatistics(int mOptions, int nBins, double histMin, double histMax)
/*      */   {
/*  784 */     setupProcessor();
/*  785 */     if ((this.roi != null) && (this.roi.isArea()))
/*  786 */       this.ip.setRoi(this.roi);
/*      */     else
/*  788 */       this.ip.resetRoi();
/*  789 */     this.ip.setHistogramSize(nBins);
/*  790 */     Calibration cal = getCalibration();
/*  791 */     if ((getType() == 1) && ((histMin != 0.0D) || (histMax != 0.0D))) {
/*  792 */       histMin = cal.getRawValue(histMin); histMax = cal.getRawValue(histMax);
/*  793 */     }this.ip.setHistogramRange(histMin, histMax);
/*  794 */     ImageStatistics stats = ImageStatistics.getStatistics(this.ip, mOptions, cal);
/*  795 */     this.ip.setHistogramSize(256);
/*  796 */     this.ip.setHistogramRange(0.0D, 0.0D);
/*  797 */     return stats;
/*      */   }
/*      */ 
/*      */   public String getTitle()
/*      */   {
/*  802 */     if (this.title == null) {
/*  803 */       return "";
/*      */     }
/*  805 */     return this.title;
/*      */   }
/*      */ 
/*      */   public String getShortTitle()
/*      */   {
/*  811 */     String title = getTitle();
/*  812 */     int index = title.indexOf(' ');
/*  813 */     if (index > -1)
/*  814 */       title = title.substring(0, index);
/*  815 */     index = title.lastIndexOf('.');
/*  816 */     if (index > 0)
/*  817 */       title = title.substring(0, index);
/*  818 */     return title;
/*      */   }
/*      */ 
/*      */   public void setTitle(String title)
/*      */   {
/*  823 */     if (title == null)
/*  824 */       return;
/*  825 */     if (this.win != null) {
/*  826 */       if (this.ij != null)
/*  827 */         Menus.updateWindowMenuItem(this.title, title);
/*  828 */       String virtual = (this.stack != null) && (this.stack.isVirtual()) ? " (V)" : "";
/*  829 */       String global = getGlobalCalibration() != null ? " (G)" : "";
/*      */ 
/*  831 */       String scale = "";
/*  832 */       double magnification = this.win.getCanvas().getMagnification();
/*  833 */       if (magnification != 1.0D) {
/*  834 */         double percent = magnification * 100.0D;
/*  835 */         int digits = (percent > 100.0D) || (percent == (int)percent) ? 0 : 1;
/*  836 */         scale = " (" + IJ.d2s(percent, digits) + "%)";
/*      */       }
/*  838 */       this.win.setTitle(title + virtual + global + scale);
/*      */     }
/*  840 */     this.title = title;
/*      */   }
/*      */ 
/*      */   public int getWidth() {
/*  844 */     return this.width;
/*      */   }
/*      */ 
/*      */   public int getHeight() {
/*  848 */     return this.height;
/*      */   }
/*      */ 
/*      */   public int getStackSize()
/*      */   {
/*  853 */     if (this.stack == null) {
/*  854 */       return 1;
/*      */     }
/*  856 */     int slices = this.stack.getSize();
/*      */ 
/*  858 */     if (slices <= 0) slices = 1;
/*  859 */     return slices;
/*      */   }
/*      */ 
/*      */   public int getImageStackSize()
/*      */   {
/*  865 */     if (this.stack == null) {
/*  866 */       return 1;
/*      */     }
/*  868 */     int slices = this.stack.getSize();
/*  869 */     if (slices == 0) slices = 1;
/*  870 */     return slices;
/*      */   }
/*      */ 
/*      */   public void setDimensions(int nChannels, int nSlices, int nFrames)
/*      */   {
/*  879 */     if ((nChannels * nSlices * nFrames != getImageStackSize()) && (this.ip != null))
/*      */     {
/*  881 */       nChannels = 1;
/*  882 */       nSlices = getImageStackSize();
/*  883 */       nFrames = 1;
/*  884 */       if (isDisplayedHyperStack()) {
/*  885 */         setOpenAsHyperStack(false);
/*  886 */         new StackWindow(this);
/*  887 */         setSlice(1);
/*      */       }
/*      */     }
/*  890 */     boolean updateWin = (isDisplayedHyperStack()) && ((this.nChannels != nChannels) || (this.nSlices != nSlices) || (this.nFrames != nFrames));
/*  891 */     this.nChannels = nChannels;
/*  892 */     this.nSlices = nSlices;
/*  893 */     this.nFrames = nFrames;
/*  894 */     if (updateWin) {
/*  895 */       if (nSlices != getImageStackSize())
/*  896 */         setOpenAsHyperStack(true);
/*  897 */       this.ip = null; this.img = null;
/*  898 */       setPositionWithoutUpdate(getChannel(), getSlice(), getFrame());
/*  899 */       if (isComposite()) ((CompositeImage)this).reset();
/*  900 */       new StackWindow(this);
/*  901 */       setPosition(getChannel(), getSlice(), getFrame());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isHyperStack()
/*      */   {
/*  908 */     return (isDisplayedHyperStack()) || ((this.openAsHyperStack) && (getNDimensions() > 3));
/*      */   }
/*      */ 
/*      */   public int getNDimensions()
/*      */   {
/*  913 */     int dimensions = 2;
/*  914 */     int[] dim = getDimensions();
/*  915 */     if (dim[2] > 1) dimensions++;
/*  916 */     if (dim[3] > 1) dimensions++;
/*  917 */     if (dim[4] > 1) dimensions++;
/*  918 */     return dimensions;
/*      */   }
/*      */ 
/*      */   public boolean isDisplayedHyperStack()
/*      */   {
/*  923 */     return (this.win != null) && ((this.win instanceof StackWindow)) && (((StackWindow)this.win).isHyperStack());
/*      */   }
/*      */ 
/*      */   public int getNChannels()
/*      */   {
/*  928 */     verifyDimensions();
/*  929 */     return this.nChannels;
/*      */   }
/*      */ 
/*      */   public int getNSlices()
/*      */   {
/*  935 */     verifyDimensions();
/*  936 */     return this.nSlices;
/*      */   }
/*      */ 
/*      */   public int getNFrames()
/*      */   {
/*  941 */     verifyDimensions();
/*  942 */     return this.nFrames;
/*      */   }
/*      */ 
/*      */   public int[] getDimensions()
/*      */   {
/*  948 */     verifyDimensions();
/*  949 */     int[] d = new int[5];
/*  950 */     d[0] = this.width;
/*  951 */     d[1] = this.height;
/*  952 */     d[2] = this.nChannels;
/*  953 */     d[3] = this.nSlices;
/*  954 */     d[4] = this.nFrames;
/*  955 */     return d;
/*      */   }
/*      */ 
/*      */   void verifyDimensions() {
/*  959 */     int stackSize = getImageStackSize();
/*  960 */     if (this.nSlices == 1) {
/*  961 */       if ((this.nChannels > 1) && (this.nFrames == 1))
/*  962 */         this.nChannels = stackSize;
/*  963 */       else if ((this.nFrames > 1) && (this.nChannels == 1))
/*  964 */         this.nFrames = stackSize;
/*      */     }
/*  966 */     if (this.nChannels * this.nSlices * this.nFrames != stackSize) {
/*  967 */       this.nSlices = stackSize;
/*  968 */       this.nChannels = 1;
/*  969 */       this.nFrames = 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getType()
/*      */   {
/*  978 */     return this.imageType;
/*      */   }
/*      */ 
/*      */   public int getBitDepth()
/*      */   {
/*  983 */     int bitDepth = 0;
/*  984 */     switch (this.imageType) { case 0:
/*      */     case 3:
/*  985 */       bitDepth = 8; break;
/*      */     case 1:
/*  986 */       bitDepth = 16; break;
/*      */     case 2:
/*  987 */       bitDepth = 32; break;
/*      */     case 4:
/*  988 */       bitDepth = 24;
/*      */     }
/*  990 */     return bitDepth;
/*      */   }
/*      */ 
/*      */   public int getBytesPerPixel()
/*      */   {
/*  995 */     switch (this.imageType) { case 1:
/*  996 */       return 2;
/*      */     case 2:
/*      */     case 4:
/*  997 */       return 4;
/*  998 */     case 3: } return 1;
/*      */   }
/*      */ 
/*      */   protected void setType(int type)
/*      */   {
/* 1003 */     if ((type < 0) || (type > 4))
/* 1004 */       return;
/* 1005 */     int previousType = this.imageType;
/* 1006 */     this.imageType = type;
/* 1007 */     if (this.imageType != previousType) {
/* 1008 */       if (this.win != null)
/* 1009 */         Menus.updateMenus();
/* 1010 */       getLocalCalibration().setImage(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setProperty(String key, Object value)
/*      */   {
/* 1017 */     if (this.properties == null)
/* 1018 */       this.properties = new Properties();
/* 1019 */     if (value == null)
/* 1020 */       this.properties.remove(key);
/*      */     else
/* 1022 */       this.properties.put(key, value);
/*      */   }
/*      */ 
/*      */   public Object getProperty(String key)
/*      */   {
/* 1027 */     if (this.properties == null) {
/* 1028 */       return null;
/*      */     }
/* 1030 */     return this.properties.get(key);
/*      */   }
/*      */ 
/*      */   public Properties getProperties()
/*      */   {
/* 1035 */     return this.properties;
/*      */   }
/*      */ 
/*      */   public LookUpTable createLut()
/*      */   {
/* 1040 */     ImageProcessor ip2 = getProcessor();
/* 1041 */     if (ip2 != null) {
/* 1042 */       return new LookUpTable(ip2.getColorModel());
/*      */     }
/* 1044 */     return new LookUpTable(LookUpTable.createGrayscaleColorModel(false));
/*      */   }
/*      */ 
/*      */   public boolean isInvertedLut()
/*      */   {
/* 1050 */     if (this.ip == null) {
/* 1051 */       if (this.img == null)
/* 1052 */         return false;
/* 1053 */       setupProcessor();
/*      */     }
/* 1055 */     return this.ip.isInvertedLut();
/*      */   }
/*      */ 
/*      */   public int[] getPixel(int x, int y)
/*      */   {
/*      */     int tmp25_24 = (this.pvalue[2] = this.pvalue[3] = 0); this.pvalue[1] = tmp25_24; this.pvalue[0] = tmp25_24;
/* 1068 */     switch (this.imageType)
/*      */     {
/*      */     case 0:
/*      */     case 3:
/*      */       int index;
/*      */       int index;
/* 1071 */       if (this.ip != null) {
/* 1072 */         index = this.ip.getPixel(x, y);
/*      */       }
/*      */       else {
/* 1075 */         if (this.img == null) return this.pvalue;
/* 1076 */         PixelGrabber pg = new PixelGrabber(this.img, x, y, 1, 1, false);
/*      */         try { pg.grabPixels(); } catch (InterruptedException e) {
/* 1078 */           return this.pvalue;
/* 1079 */         }byte[] pixels8 = (byte[])pg.getPixels();
/* 1080 */         index = pixels8 != null ? pixels8[0] & 0xFF : 0;
/*      */       }
/* 1082 */       if (this.imageType != 3) {
/* 1083 */         this.pvalue[0] = index;
/* 1084 */         return this.pvalue;
/*      */       }
/* 1086 */       this.pvalue[3] = index;
/*      */     case 4:
/* 1089 */       int c = 0;
/* 1090 */       if ((this.imageType == 4) && (this.ip != null)) {
/* 1091 */         c = this.ip.getPixel(x, y);
/*      */       } else {
/* 1093 */         int[] pixels32 = new int[1];
/* 1094 */         if (this.img == null) return this.pvalue;
/* 1095 */         PixelGrabber pg = new PixelGrabber(this.img, x, y, 1, 1, pixels32, 0, this.width);
/*      */         try { pg.grabPixels(); } catch (InterruptedException e) {
/* 1097 */           return this.pvalue;
/* 1098 */         }c = pixels32[0];
/*      */       }
/* 1100 */       int r = (c & 0xFF0000) >> 16;
/* 1101 */       int g = (c & 0xFF00) >> 8;
/* 1102 */       int b = c & 0xFF;
/* 1103 */       this.pvalue[0] = r;
/* 1104 */       this.pvalue[1] = g;
/* 1105 */       this.pvalue[2] = b;
/* 1106 */       break;
/*      */     case 1:
/*      */     case 2:
/* 1108 */       if (this.ip != null) this.pvalue[0] = this.ip.getPixel(x, y);
/*      */       break;
/*      */     }
/* 1111 */     return this.pvalue;
/*      */   }
/*      */ 
/*      */   public ImageStack createEmptyStack()
/*      */   {
/*      */     ColorModel cm;
/*      */     ColorModel cm;
/* 1118 */     if (this.ip != null)
/* 1119 */       cm = this.ip.getColorModel();
/*      */     else
/* 1121 */       cm = createLut().getColorModel();
/* 1122 */     return new ImageStack(this.width, this.height, cm);
/*      */   }
/*      */ 
/*      */   public ImageStack getStack()
/*      */   {
/*      */     ImageStack s;
/* 1133 */     if (this.stack == null) {
/* 1134 */       ImageStack s = createEmptyStack();
/* 1135 */       ImageProcessor ip2 = getProcessor();
/* 1136 */       if (ip2 == null)
/* 1137 */         return s;
/* 1138 */       String info = (String)getProperty("Info");
/* 1139 */       String label = info != null ? getTitle() + "\n" + info : null;
/* 1140 */       s.addSlice(label, ip2);
/* 1141 */       s.update(ip2);
/*      */     } else {
/* 1143 */       s = this.stack;
/* 1144 */       if (this.ip != null) {
/* 1145 */         Calibration cal = getCalibration();
/* 1146 */         if (cal.calibrated())
/* 1147 */           this.ip.setCalibrationTable(cal.getCTable());
/*      */         else
/* 1149 */           this.ip.setCalibrationTable(null);
/*      */       }
/* 1151 */       s.update(this.ip);
/*      */     }
/* 1153 */     if (this.roi != null)
/* 1154 */       s.setRoi(this.roi.getBounds());
/*      */     else
/* 1156 */       s.setRoi(null);
/* 1157 */     return s;
/*      */   }
/*      */ 
/*      */   public ImageStack getImageStack()
/*      */   {
/* 1162 */     if (this.stack == null) {
/* 1163 */       return getStack();
/*      */     }
/* 1165 */     this.stack.update(this.ip);
/* 1166 */     return this.stack;
/*      */   }
/*      */ 
/*      */   public int getCurrentSlice()
/*      */   {
/* 1173 */     if (this.currentSlice < 1) setCurrentSlice(1);
/* 1174 */     if (this.currentSlice > getStackSize()) setCurrentSlice(getStackSize());
/* 1175 */     return this.currentSlice;
/*      */   }
/*      */ 
/*      */   final void setCurrentSlice(int slice) {
/* 1179 */     this.currentSlice = slice;
/* 1180 */     int stackSize = getStackSize();
/* 1181 */     if (this.nChannels == stackSize) updatePosition(this.currentSlice, 1, 1);
/* 1182 */     if (this.nSlices == stackSize) updatePosition(1, this.currentSlice, 1);
/* 1183 */     if (this.nFrames == stackSize) updatePosition(1, 1, this.currentSlice); 
/*      */   }
/*      */ 
/*      */   public int getChannel()
/*      */   {
/* 1187 */     return this.position[0];
/*      */   }
/*      */ 
/*      */   public int getSlice() {
/* 1191 */     return this.position[1];
/*      */   }
/*      */ 
/*      */   public int getFrame() {
/* 1195 */     return this.position[2];
/*      */   }
/*      */ 
/*      */   public void killStack() {
/* 1199 */     this.stack = null;
/* 1200 */     trimProcessor();
/*      */   }
/*      */ 
/*      */   public void setPosition(int channel, int slice, int frame)
/*      */   {
/* 1207 */     verifyDimensions();
/* 1208 */     if (channel < 1) channel = 1;
/* 1209 */     if (channel > this.nChannels) channel = this.nChannels;
/* 1210 */     if (slice < 1) slice = 1;
/* 1211 */     if (slice > this.nSlices) slice = this.nSlices;
/* 1212 */     if (frame < 1) frame = 1;
/* 1213 */     if (frame > this.nFrames) frame = this.nFrames;
/* 1214 */     if (isDisplayedHyperStack()) {
/* 1215 */       ((StackWindow)this.win).setPosition(channel, slice, frame);
/*      */     } else {
/* 1217 */       setSlice((frame - 1) * this.nChannels * this.nSlices + (slice - 1) * this.nChannels + channel);
/* 1218 */       updatePosition(channel, slice, frame);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setPositionWithoutUpdate(int channel, int slice, int frame)
/*      */   {
/* 1225 */     this.noUpdateMode = true;
/* 1226 */     setPosition(channel, slice, frame);
/* 1227 */     this.noUpdateMode = false;
/*      */   }
/*      */ 
/*      */   public int getStackIndex(int channel, int slice, int frame)
/*      */   {
/* 1232 */     if (channel < 1) channel = 1;
/* 1233 */     if (channel > this.nChannels) channel = this.nChannels;
/* 1234 */     if (slice < 1) slice = 1;
/* 1235 */     if (slice > this.nSlices) slice = this.nSlices;
/* 1236 */     if (frame < 1) frame = 1;
/* 1237 */     if (frame > this.nFrames) frame = this.nFrames;
/* 1238 */     return (frame - 1) * this.nChannels * this.nSlices + (slice - 1) * this.nChannels + channel;
/*      */   }
/*      */ 
/*      */   public void resetStack()
/*      */   {
/* 1243 */     if ((this.currentSlice == 1) && (this.stack != null) && (this.stack.getSize() > 0)) {
/* 1244 */       ColorModel cm = this.ip.getColorModel();
/* 1245 */       double min = this.ip.getMin();
/* 1246 */       double max = this.ip.getMax();
/* 1247 */       this.ip = this.stack.getProcessor(1);
/* 1248 */       this.ip.setColorModel(cm);
/* 1249 */       this.ip.setMinAndMax(min, max);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setPosition(int n)
/*      */   {
/* 1255 */     int[] pos = convertIndexToPosition(n);
/* 1256 */     setPosition(pos[0], pos[1], pos[2]);
/*      */   }
/*      */ 
/*      */   public int[] convertIndexToPosition(int n)
/*      */   {
/* 1261 */     if ((n < 1) || (n > getStackSize()))
/* 1262 */       throw new IllegalArgumentException("n out of range: " + n);
/* 1263 */     int[] position = new int[3];
/* 1264 */     int[] dim = getDimensions();
/* 1265 */     position[0] = ((n - 1) % dim[2] + 1);
/* 1266 */     position[1] = ((n - 1) / dim[2] % dim[3] + 1);
/* 1267 */     position[2] = ((n - 1) / (dim[2] * dim[3]) % dim[4] + 1);
/* 1268 */     return position;
/*      */   }
/*      */ 
/*      */   public synchronized void setSlice(int n)
/*      */   {
/* 1274 */     if ((this.stack == null) || ((n == this.currentSlice) && (this.ip != null))) {
/* 1275 */       if (!this.noUpdateMode)
/* 1276 */         updateAndRepaintWindow();
/* 1277 */       return;
/*      */     }
/* 1279 */     if ((n >= 1) && (n <= this.stack.getSize())) {
/* 1280 */       Roi roi = getRoi();
/* 1281 */       if (roi != null)
/* 1282 */         roi.endPaste();
/* 1283 */       if (isProcessor())
/* 1284 */         this.stack.setPixels(this.ip.getPixels(), this.currentSlice);
/* 1285 */       this.ip = getProcessor();
/* 1286 */       setCurrentSlice(n);
/* 1287 */       Object pixels = this.stack.getPixels(this.currentSlice);
/* 1288 */       if ((this.ip != null) && (pixels != null)) {
/* 1289 */         this.ip.setSnapshotPixels(null);
/* 1290 */         this.ip.setPixels(pixels);
/*      */       } else {
/* 1292 */         this.ip = this.stack.getProcessor(n);
/* 1293 */       }if ((this.win != null) && ((this.win instanceof StackWindow))) {
/* 1294 */         ((StackWindow)this.win).updateSliceSelector();
/*      */       }
/*      */ 
/* 1302 */       if (this.imageType == 4)
/* 1303 */         ContrastAdjuster.update();
/* 1304 */       if (!this.noUpdateMode)
/* 1305 */         updateAndRepaintWindow();
/*      */       else
/* 1307 */         this.img = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setSliceWithoutUpdate(int n)
/*      */   {
/* 1314 */     this.noUpdateMode = true;
/* 1315 */     setSlice(n);
/* 1316 */     this.noUpdateMode = false;
/*      */   }
/*      */ 
/*      */   public Roi getRoi()
/*      */   {
/* 1321 */     return this.roi;
/*      */   }
/*      */ 
/*      */   public void setRoi(Roi newRoi)
/*      */   {
/* 1327 */     setRoi(newRoi, true);
/*      */   }
/*      */ 
/*      */   public void setRoi(Roi newRoi, boolean updateDisplay)
/*      */   {
/* 1332 */     if (newRoi == null) {
/* 1333 */       killRoi(); return;
/* 1334 */     }if (newRoi.isVisible()) {
/* 1335 */       newRoi = (Roi)newRoi.clone();
/* 1336 */       if (newRoi == null) {
/* 1337 */         killRoi(); return;
/*      */       }
/*      */     }
/* 1339 */     Rectangle bounds = newRoi.getBounds();
/* 1340 */     if ((bounds.width == 0) && (bounds.height == 0) && (newRoi.getType() != 10) && (newRoi.getType() != 5)) {
/* 1341 */       killRoi(); return;
/* 1342 */     }this.roi = newRoi;
/* 1343 */     if (this.ip != null) {
/* 1344 */       this.ip.setMask(null);
/* 1345 */       if (this.roi.isArea())
/* 1346 */         this.ip.setRoi(bounds);
/*      */       else
/* 1348 */         this.ip.resetRoi();
/*      */     }
/* 1350 */     this.roi.setImage(this);
/* 1351 */     if (updateDisplay) draw();
/*      */   }
/*      */ 
/*      */   public void setRoi(int x, int y, int width, int height)
/*      */   {
/* 1356 */     setRoi(new Rectangle(x, y, width, height));
/*      */   }
/*      */ 
/*      */   public void setRoi(Rectangle r)
/*      */   {
/* 1361 */     setRoi(new Roi(r.x, r.y, r.width, r.height));
/*      */   }
/*      */ 
/*      */   public void createNewRoi(int sx, int sy)
/*      */   {
/* 1368 */     killRoi();
/* 1369 */     switch (Toolbar.getToolId()) {
/*      */     case 0:
/* 1371 */       int cornerDiameter = Toolbar.getRoundRectArcSize();
/* 1372 */       this.roi = new Roi(sx, sy, this, cornerDiameter);
/* 1373 */       if (cornerDiameter > 0) {
/* 1374 */         this.roi.setStrokeColor(Toolbar.getForegroundColor());
/* 1375 */         this.roi.setStrokeWidth(RectToolOptions.getDefaultStrokeWidth()); } break;
/*      */     case 1:
/* 1379 */       if (Toolbar.getOvalToolType() == 1)
/* 1380 */         this.roi = new EllipseRoi(sx, sy, this);
/*      */       else
/* 1382 */         this.roi = new OvalRoi(sx, sy, this);
/* 1383 */       break;
/*      */     case 2:
/*      */     case 5:
/*      */     case 14:
/* 1387 */       this.roi = new PolygonRoi(sx, sy, this);
/* 1388 */       break;
/*      */     case 3:
/*      */     case 6:
/* 1391 */       this.roi = new FreehandRoi(sx, sy, this);
/* 1392 */       break;
/*      */     case 4:
/* 1394 */       if ("arrow".equals(Toolbar.getToolName()))
/* 1395 */         this.roi = new Arrow(sx, sy, this);
/*      */       else
/* 1397 */         this.roi = new Line(sx, sy, this);
/* 1398 */       break;
/*      */     case 9:
/* 1400 */       this.roi = new TextRoi(sx, sy, this);
/* 1401 */       break;
/*      */     case 7:
/* 1403 */       this.roi = new PointRoi(sx, sy, this);
/* 1404 */       if ((Prefs.pointAutoMeasure) || ((Prefs.pointAutoNextSlice) && (!Prefs.pointAddToManager))) IJ.run("Measure");
/* 1405 */       if (Prefs.pointAddToManager) {
/* 1406 */         IJ.run("Add to Manager ");
/* 1407 */         ImageCanvas ic = getCanvas();
/* 1408 */         if ((ic != null) && (!ic.getShowAllROIs()))
/* 1409 */           ic.setShowAllROIs(true);
/*      */       }
/* 1411 */       if ((Prefs.pointAutoNextSlice) && (getStackSize() > 1)) {
/* 1412 */         IJ.run("Next Slice [>]");
/* 1413 */         killRoi();
/*      */       }break;
/*      */     case 8:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     }
/*      */   }
/*      */ 
/* 1423 */   public void killRoi() { if (this.roi != null) {
/* 1424 */       saveRoi();
/* 1425 */       this.roi = null;
/* 1426 */       if (this.ip != null)
/* 1427 */         this.ip.resetRoi();
/* 1428 */       draw();
/*      */     } }
/*      */ 
/*      */   public void saveRoi()
/*      */   {
/* 1433 */     if (this.roi != null) {
/* 1434 */       this.roi.endPaste();
/* 1435 */       Rectangle r = this.roi.getBounds();
/* 1436 */       if ((r.width > 0) || (r.height > 0)) {
/* 1437 */         Roi.previousRoi = (Roi)this.roi.clone();
/* 1438 */         if (IJ.debugMode) IJ.log("saveRoi: " + this.roi); 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void restoreRoi()
/*      */   {
/* 1444 */     if (Roi.previousRoi != null) {
/* 1445 */       Roi pRoi = Roi.previousRoi;
/* 1446 */       Rectangle r = pRoi.getBounds();
/* 1447 */       if ((r.width <= this.width) || (r.height <= this.height) || (isSmaller(pRoi))) {
/* 1448 */         this.roi = ((Roi)pRoi.clone());
/* 1449 */         this.roi.setImage(this);
/* 1450 */         if ((r.x >= this.width) || (r.y >= this.height) || (r.x + r.width <= 0) || (r.y + r.height <= 0))
/* 1451 */           this.roi.setLocation((this.width - r.width) / 2, (this.height - r.height) / 2);
/* 1452 */         else if ((r.width == this.width) && (r.height == this.height))
/* 1453 */           this.roi.setLocation(0, 0);
/* 1454 */         draw();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean isSmaller(Roi r) {
/* 1460 */     ImageProcessor mask = r.getMask();
/* 1461 */     if (mask == null) return false;
/* 1462 */     mask.setThreshold(255.0D, 255.0D, 2);
/* 1463 */     ImageStatistics stats = ImageStatistics.getStatistics(mask, 258, null);
/* 1464 */     return stats.area <= this.width * this.height;
/*      */   }
/*      */ 
/*      */   public void revert()
/*      */   {
/* 1469 */     if ((getStackSize() > 1) && (getStack().isVirtual()))
/* 1470 */       return;
/* 1471 */     FileInfo fi = getOriginalFileInfo();
/* 1472 */     boolean isFileInfo = (fi != null) && (fi.fileFormat != 0);
/* 1473 */     if ((!isFileInfo) && (this.url == null))
/* 1474 */       return;
/* 1475 */     if ((this.ij != null) && (this.changes) && (isFileInfo) && (!Interpreter.isBatchMode()) && (!IJ.isMacro()) && (!IJ.altKeyDown()) && 
/* 1476 */       (!IJ.showMessageWithCancel("Revert?", "Revert to saved version of\n\"" + getTitle() + "\"?"))) {
/* 1477 */       return;
/*      */     }
/* 1479 */     Roi saveRoi = null;
/* 1480 */     if (this.roi != null) {
/* 1481 */       this.roi.endPaste();
/* 1482 */       saveRoi = (Roi)this.roi.clone();
/*      */     }
/*      */ 
/* 1485 */     if (getStackSize() > 1) {
/* 1486 */       revertStack(fi);
/* 1487 */       return;
/*      */     }
/*      */ 
/* 1490 */     trimProcessor();
/* 1491 */     if ((isFileInfo) && ((this.url == null) || ((fi.directory != null) && (!fi.directory.equals(""))))) {
/* 1492 */       new FileOpener(fi).revertToSaved(this);
/* 1493 */     } else if (this.url != null) {
/* 1494 */       IJ.showStatus("Loading: " + this.url);
/* 1495 */       Opener opener = new Opener();
/*      */       try {
/* 1497 */         ImagePlus imp = opener.openURL(this.url);
/* 1498 */         if (imp != null)
/* 1499 */           setProcessor(null, imp.getProcessor()); 
/*      */       } catch (Exception e) {  }
/*      */ 
/* 1501 */       if ((getType() == 4) && (getTitle().endsWith(".jpg")))
/* 1502 */         Opener.convertGrayJpegTo8Bits(this);
/*      */     }
/* 1504 */     if ((Prefs.useInvertingLut) && (getBitDepth() == 8) && (this.ip != null) && (!this.ip.isInvertedLut()) && (!this.ip.isColorLut()))
/* 1505 */       invertLookupTable();
/* 1506 */     if (getProperty("FHT") != null) {
/* 1507 */       this.properties.remove("FHT");
/* 1508 */       if (getTitle().startsWith("FFT of "))
/* 1509 */         setTitle(getTitle().substring(7));
/*      */     }
/* 1511 */     ContrastAdjuster.update();
/* 1512 */     if (saveRoi != null) setRoi(saveRoi);
/* 1513 */     repaintWindow();
/* 1514 */     IJ.showStatus("");
/* 1515 */     this.changes = false;
/* 1516 */     notifyListeners(2);
/*      */   }
/*      */ 
/*      */   void revertStack(FileInfo fi) {
/* 1520 */     String path = null;
/* 1521 */     String url2 = null;
/* 1522 */     if ((this.url != null) && (!this.url.equals(""))) {
/* 1523 */       path = this.url;
/* 1524 */       url2 = this.url;
/* 1525 */     } else if ((fi != null) && (fi.directory != null) && (!fi.directory.equals(""))) {
/* 1526 */       path = fi.directory + fi.fileName;
/* 1527 */     } else if ((fi != null) && (fi.url != null) && (!fi.url.equals(""))) {
/* 1528 */       path = fi.url;
/* 1529 */       url2 = fi.url;
/*      */     } else {
/* 1531 */       return;
/*      */     }
/* 1533 */     IJ.showStatus("Loading: " + path);
/* 1534 */     ImagePlus imp = IJ.openImage(path);
/* 1535 */     if (imp != null) {
/* 1536 */       int n = imp.getStackSize();
/* 1537 */       int c = imp.getNChannels();
/* 1538 */       int z = imp.getNSlices();
/* 1539 */       int t = imp.getNFrames();
/* 1540 */       if ((z == n) || (t == n) || ((c == getNChannels()) && (z == getNSlices()) && (t == getNFrames()))) {
/* 1541 */         setCalibration(imp.getCalibration());
/* 1542 */         setStack(imp.getStack(), c, z, t);
/*      */       } else {
/* 1544 */         ImageWindow win = getWindow();
/* 1545 */         Point loc = null;
/* 1546 */         if (win != null) loc = win.getLocation();
/* 1547 */         this.changes = false;
/* 1548 */         close();
/* 1549 */         FileInfo fi2 = imp.getOriginalFileInfo();
/* 1550 */         if ((fi2 != null) && ((fi2.url == null) || (fi2.url.length() == 0))) {
/* 1551 */           fi2.url = url2;
/* 1552 */           imp.setFileInfo(fi2);
/*      */         }
/* 1554 */         ImageWindow.setNextLocation(loc);
/* 1555 */         imp.show();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public FileInfo getFileInfo()
/*      */   {
/* 1568 */     FileInfo fi = new FileInfo();
/* 1569 */     fi.width = this.width;
/* 1570 */     fi.height = this.height;
/* 1571 */     fi.nImages = getStackSize();
/* 1572 */     if (this.compositeImage)
/* 1573 */       fi.nImages = getImageStackSize();
/* 1574 */     fi.whiteIsZero = isInvertedLut();
/* 1575 */     fi.intelByteOrder = false;
/* 1576 */     setupProcessor();
/* 1577 */     if (fi.nImages == 1)
/* 1578 */       fi.pixels = this.ip.getPixels();
/*      */     else
/* 1580 */       fi.pixels = this.stack.getImageArray();
/* 1581 */     Calibration cal = getCalibration();
/* 1582 */     if (cal.scaled()) {
/* 1583 */       fi.pixelWidth = cal.pixelWidth;
/* 1584 */       fi.pixelHeight = cal.pixelHeight;
/* 1585 */       fi.unit = cal.getUnit();
/*      */     }
/* 1587 */     if (fi.nImages > 1)
/* 1588 */       fi.pixelDepth = cal.pixelDepth;
/* 1589 */     fi.frameInterval = cal.frameInterval;
/* 1590 */     if (cal.calibrated()) {
/* 1591 */       fi.calibrationFunction = cal.getFunction();
/* 1592 */       fi.coefficients = cal.getCoefficients();
/* 1593 */       fi.valueUnit = cal.getValueUnit();
/*      */     }
/* 1595 */     switch (this.imageType) { case 0:
/*      */     case 3:
/* 1597 */       LookUpTable lut = createLut();
/* 1598 */       if ((this.imageType == 3) || (!lut.isGrayscale()))
/* 1599 */         fi.fileType = 5;
/*      */       else
/* 1601 */         fi.fileType = 0;
/* 1602 */       fi.lutSize = lut.getMapSize();
/* 1603 */       fi.reds = lut.getReds();
/* 1604 */       fi.greens = lut.getGreens();
/* 1605 */       fi.blues = lut.getBlues();
/* 1606 */       break;
/*      */     case 1:
/* 1608 */       if ((this.compositeImage) && (fi.nImages == 3))
/* 1609 */         fi.fileType = 12;
/*      */       else
/* 1611 */         fi.fileType = 2;
/* 1612 */       break;
/*      */     case 2:
/* 1614 */       fi.fileType = 4;
/* 1615 */       break;
/*      */     case 4:
/* 1617 */       fi.fileType = 6;
/* 1618 */       break;
/*      */     }
/*      */ 
/* 1621 */     return fi;
/*      */   }
/*      */ 
/*      */   public FileInfo getOriginalFileInfo()
/*      */   {
/* 1630 */     if (((this.fileInfo == null ? 1 : 0) & (this.url != null ? 1 : 0)) != 0) {
/* 1631 */       this.fileInfo = new FileInfo();
/* 1632 */       this.fileInfo.width = this.width;
/* 1633 */       this.fileInfo.height = this.height;
/* 1634 */       this.fileInfo.url = this.url;
/* 1635 */       this.fileInfo.directory = null;
/*      */     }
/* 1637 */     return this.fileInfo;
/*      */   }
/*      */ 
/*      */   public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h)
/*      */   {
/* 1642 */     this.imageUpdateY = y;
/* 1643 */     this.imageUpdateW = w;
/* 1644 */     if ((flags & 0x40) != 0) {
/* 1645 */       this.errorLoadingImage = true;
/* 1646 */       return false;
/*      */     }
/* 1648 */     this.imageLoaded = ((flags & 0xB0) != 0);
/* 1649 */     return !this.imageLoaded;
/*      */   }
/*      */ 
/*      */   public synchronized void flush()
/*      */   {
/* 1655 */     notifyListeners(1);
/* 1656 */     if ((this.locked) || (this.ignoreFlush)) return;
/* 1657 */     this.ip = null;
/* 1658 */     if (this.roi != null) this.roi.setImage(null);
/* 1659 */     this.roi = null;
/* 1660 */     if (this.stack != null) {
/* 1661 */       Object[] arrays = this.stack.getImageArray();
/* 1662 */       if (arrays != null) {
/* 1663 */         for (int i = 0; i < arrays.length; i++)
/* 1664 */           arrays[i] = null;
/*      */       }
/*      */     }
/* 1667 */     this.stack = null;
/* 1668 */     this.img = null;
/* 1669 */     this.win = null;
/* 1670 */     if (this.roi != null) this.roi.setImage(null);
/* 1671 */     this.roi = null;
/* 1672 */     this.properties = null;
/* 1673 */     this.calibration = null;
/* 1674 */     this.overlay = null;
/* 1675 */     this.flatteningCanvas = null;
/*      */   }
/*      */ 
/*      */   public void setIgnoreFlush(boolean ignoreFlush) {
/* 1679 */     this.ignoreFlush = ignoreFlush;
/*      */   }
/*      */ 
/*      */   public ImagePlus duplicate()
/*      */   {
/* 1685 */     return new Duplicator().run(this);
/*      */   }
/*      */ 
/*      */   public ImagePlus createImagePlus()
/*      */   {
/* 1691 */     ImagePlus imp2 = new ImagePlus();
/* 1692 */     imp2.setType(getType());
/* 1693 */     imp2.setCalibration(getCalibration());
/* 1694 */     return imp2;
/*      */   }
/*      */ 
/*      */   public ImagePlus createHyperStack(String title, int channels, int slices, int frames, int bitDepth)
/*      */   {
/* 1701 */     int size = channels * slices * frames;
/* 1702 */     ImageStack stack2 = new ImageStack(this.width, this.height, size);
/* 1703 */     ImageProcessor ip2 = null;
/* 1704 */     switch (bitDepth) { case 8:
/* 1705 */       ip2 = new ByteProcessor(this.width, this.height); break;
/*      */     case 16:
/* 1706 */       ip2 = new ShortProcessor(this.width, this.height); break;
/*      */     case 24:
/* 1707 */       ip2 = new ColorProcessor(this.width, this.height); break;
/*      */     case 32:
/* 1708 */       ip2 = new FloatProcessor(this.width, this.height); break;
/*      */     default:
/* 1709 */       throw new IllegalArgumentException("Invalid bit depth");
/*      */     }
/* 1711 */     stack2.setPixels(ip2.getPixels(), 1);
/* 1712 */     ImagePlus imp2 = new ImagePlus(title, stack2);
/* 1713 */     stack2.setPixels(null, 1);
/* 1714 */     imp2.setDimensions(channels, slices, frames);
/* 1715 */     imp2.setCalibration(getCalibration());
/* 1716 */     imp2.setOpenAsHyperStack(true);
/* 1717 */     return imp2;
/*      */   }
/*      */ 
/*      */   public void copyScale(ImagePlus imp)
/*      */   {
/* 1722 */     if ((imp != null) && (globalCalibration == null))
/* 1723 */       setCalibration(imp.getCalibration());
/*      */   }
/*      */ 
/*      */   public void startTiming()
/*      */   {
/* 1730 */     this.startTime = System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */   public long getStartTime()
/*      */   {
/* 1736 */     return this.startTime;
/*      */   }
/*      */ 
/*      */   public Calibration getCalibration()
/*      */   {
/* 1742 */     if (globalCalibration != null) {
/* 1743 */       Calibration gc = globalCalibration.copy();
/* 1744 */       gc.setImage(this);
/* 1745 */       return gc;
/*      */     }
/* 1747 */     if (this.calibration == null)
/* 1748 */       this.calibration = new Calibration(this);
/* 1749 */     return this.calibration;
/*      */   }
/*      */ 
/*      */   public void setCalibration(Calibration cal)
/*      */   {
/* 1756 */     if (cal == null) {
/* 1757 */       this.calibration = null;
/*      */     } else {
/* 1759 */       this.calibration = cal.copy();
/* 1760 */       this.calibration.setImage(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setGlobalCalibration(Calibration global)
/*      */   {
/* 1767 */     if (global == null)
/* 1768 */       globalCalibration = null;
/*      */     else
/* 1770 */       globalCalibration = global.copy();
/*      */   }
/*      */ 
/*      */   public Calibration getGlobalCalibration()
/*      */   {
/* 1775 */     return globalCalibration;
/*      */   }
/*      */ 
/*      */   public Calibration getLocalCalibration()
/*      */   {
/* 1781 */     if (this.calibration == null)
/* 1782 */       this.calibration = new Calibration(this);
/* 1783 */     return this.calibration;
/*      */   }
/*      */ 
/*      */   public void mouseMoved(int x, int y)
/*      */   {
/* 1791 */     if (this.ij != null)
/* 1792 */       this.ij.showStatus(getLocationAsString(x, y) + getValueAsString(x, y));
/* 1793 */     this.savex = x; this.savey = y;
/*      */   }
/*      */ 
/*      */   public void updateStatusbarValue()
/*      */   {
/* 1803 */     IJ.showStatus(getLocationAsString(this.savex, this.savey) + getValueAsString(this.savex, this.savey));
/*      */   }
/*      */ 
/*      */   String getFFTLocation(int x, int y, Calibration cal) {
/* 1807 */     double center = this.width / 2.0D;
/* 1808 */     double r = Math.sqrt((x - center) * (x - center) + (y - center) * (y - center));
/* 1809 */     if (r < 1.0D) r = 1.0D;
/* 1810 */     double theta = Math.atan2(y - center, x - center);
/* 1811 */     theta = theta * 180.0D / 3.141592653589793D;
/* 1812 */     if (theta < 0.0D) theta = 360.0D + theta;
/* 1813 */     String s = "r=";
/* 1814 */     if (cal.scaled())
/* 1815 */       s = s + IJ.d2s(this.width / r * cal.pixelWidth, 2) + " " + cal.getUnit() + "/c (" + IJ.d2s(r, 0) + ")";
/*      */     else
/* 1817 */       s = s + IJ.d2s(this.width / r, 2) + " p/c (" + IJ.d2s(r, 0) + ")";
/* 1818 */     s = s + ", theta= " + IJ.d2s(theta, 2) + '°';
/* 1819 */     return s;
/*      */   }
/*      */ 
/*      */   public String getLocationAsString(int x, int y)
/*      */   {
/* 1824 */     Calibration cal = getCalibration();
/* 1825 */     if (getProperty("FHT") != null) {
/* 1826 */       return getFFTLocation(x, this.height - y - 1, cal);
/*      */     }
/* 1828 */     if (!IJ.altKeyDown()) {
/* 1829 */       String s = " x=" + d2s(cal.getX(x)) + ", y=" + d2s(cal.getY(y, this.height));
/* 1830 */       if (getStackSize() > 1) {
/* 1831 */         int z = isDisplayedHyperStack() ? getSlice() - 1 : getCurrentSlice() - 1;
/* 1832 */         s = s + ", z=" + d2s(cal.getZ(z));
/*      */       }
/* 1834 */       return s;
/*      */     }
/* 1836 */     String s = " x=" + x + ", y=" + y;
/* 1837 */     if (getStackSize() > 1) {
/* 1838 */       int z = isDisplayedHyperStack() ? getSlice() - 1 : getCurrentSlice() - 1;
/* 1839 */       s = s + ", z=" + z;
/*      */     }
/* 1841 */     return s;
/*      */   }
/*      */ 
/*      */   private String d2s(double n)
/*      */   {
/* 1846 */     return n == (int)n ? Integer.toString((int)n) : IJ.d2s(n);
/*      */   }
/*      */ 
/*      */   private String getValueAsString(int x, int y) {
/* 1850 */     if ((this.win != null) && ((this.win instanceof PlotWindow)))
/* 1851 */       return "";
/* 1852 */     Calibration cal = getCalibration();
/* 1853 */     int[] v = getPixel(x, y);
/* 1854 */     int type = getType();
/* 1855 */     switch (type) { case 0:
/*      */     case 1:
/*      */     case 3:
/* 1857 */       if (type == 3) {
/* 1858 */         if (cal.getCValue(v[3]) == v[3]) {
/* 1859 */           return ", index=" + v[3] + ", value=" + v[0] + "," + v[1] + "," + v[2];
/*      */         }
/* 1861 */         v[0] = v[3];
/*      */       }
/* 1863 */       double cValue = cal.getCValue(v[0]);
/* 1864 */       if (cValue == v[0]) {
/* 1865 */         return ", value=" + v[0];
/*      */       }
/* 1867 */       return ", value=" + IJ.d2s(cValue) + " (" + v[0] + ")";
/*      */     case 2:
/* 1869 */       return ", value=" + Float.intBitsToFloat(v[0]);
/*      */     case 4:
/* 1871 */       return ", value=" + v[0] + "," + v[1] + "," + v[2]; }
/* 1872 */     return "";
/*      */   }
/*      */ 
/*      */   public void copy(boolean cut)
/*      */   {
/* 1880 */     Roi roi = getRoi();
/* 1881 */     if ((roi != null) && (!roi.isArea())) {
/* 1882 */       IJ.error("Cut/Copy", "The Cut and Copy commands require\nan area selection, or no selection.");
/*      */ 
/* 1884 */       return;
/*      */     }
/* 1886 */     boolean batchMode = Interpreter.isBatchMode();
/* 1887 */     String msg = cut ? "Cutt" : "Copy";
/* 1888 */     if (!batchMode) IJ.showStatus(msg + "ing...");
/* 1889 */     ImageProcessor ip = getProcessor();
/*      */ 
/* 1891 */     Roi roi2 = null;
/* 1892 */     ImageProcessor ip2 = ip.crop();
/* 1893 */     if ((roi != null) && (roi.getType() != 0)) {
/* 1894 */       roi2 = (Roi)roi.clone();
/* 1895 */       Rectangle r = roi.getBounds();
/* 1896 */       if ((r.x < 0) || (r.y < 0) || (r.x + r.width > this.width) || (r.y + r.height > this.height)) {
/* 1897 */         roi2 = new ShapeRoi(roi2);
/* 1898 */         ShapeRoi image = new ShapeRoi(new Roi(0, 0, this.width, this.height));
/* 1899 */         roi2 = image.and((ShapeRoi)roi2);
/*      */       }
/*      */     }
/* 1902 */     clipboard = new ImagePlus("Clipboard", ip2);
/* 1903 */     if (roi2 != null) clipboard.setRoi(roi2);
/* 1904 */     if (cut) {
/* 1905 */       ip.snapshot();
/* 1906 */       ip.setColor(Toolbar.getBackgroundColor());
/* 1907 */       ip.fill();
/* 1908 */       if ((roi != null) && (roi.getType() != 0)) {
/* 1909 */         getMask();
/* 1910 */         ip.reset(ip.getMask());
/* 1911 */       }setColor(Toolbar.getForegroundColor());
/* 1912 */       Undo.setup(1, this);
/* 1913 */       updateAndDraw();
/*      */     }
/* 1915 */     int bytesPerPixel = 1;
/* 1916 */     switch (clipboard.getType()) { case 1:
/* 1917 */       bytesPerPixel = 2; break;
/*      */     case 2:
/*      */     case 4:
/* 1918 */       bytesPerPixel = 4;
/*      */     case 3:
/*      */     }
/*      */ 
/* 1922 */     if (!batchMode) {
/* 1923 */       msg = cut ? "Cut" : "Copy";
/* 1924 */       IJ.showStatus(msg + ": " + clipboard.getWidth() * clipboard.getHeight() * bytesPerPixel / 1024 + "k");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paste()
/*      */   {
/* 1933 */     if (clipboard == null) return;
/* 1934 */     int cType = clipboard.getType();
/* 1935 */     int iType = getType();
/*      */ 
/* 1937 */     int w = clipboard.getWidth();
/* 1938 */     int h = clipboard.getHeight();
/* 1939 */     Roi cRoi = clipboard.getRoi();
/* 1940 */     Rectangle r = null;
/* 1941 */     Roi roi = getRoi();
/* 1942 */     if (roi != null) {
/* 1943 */       r = roi.getBounds();
/*      */     }
/*      */ 
/* 1949 */     if ((r == null) || ((r != null) && ((w != r.width) || (h != r.height))))
/*      */     {
/* 1951 */       ImageCanvas ic = null;
/* 1952 */       if (this.win != null)
/* 1953 */         ic = this.win.getCanvas();
/* 1954 */       Rectangle srcRect = ic != null ? ic.getSrcRect() : new Rectangle(0, 0, this.width, this.height);
/* 1955 */       int xCenter = srcRect.x + srcRect.width / 2;
/* 1956 */       int yCenter = srcRect.y + srcRect.height / 2;
/* 1957 */       if ((cRoi != null) && (cRoi.getType() != 0)) {
/* 1958 */         cRoi.setImage(this);
/* 1959 */         cRoi.setLocation(xCenter - w / 2, yCenter - h / 2);
/* 1960 */         setRoi(cRoi);
/*      */       } else {
/* 1962 */         setRoi(xCenter - w / 2, yCenter - h / 2, w, h);
/* 1963 */       }roi = getRoi();
/*      */     }
/* 1965 */     if (IJ.isMacro())
/*      */     {
/* 1967 */       int pasteMode = Roi.getCurrentPasteMode();
/* 1968 */       boolean nonRect = roi.getType() != 0;
/* 1969 */       ImageProcessor ip = getProcessor();
/* 1970 */       if (nonRect) ip.snapshot();
/* 1971 */       r = roi.getBounds();
/* 1972 */       ip.copyBits(clipboard.getProcessor(), r.x, r.y, pasteMode);
/* 1973 */       if (nonRect) ip.reset(getMask());
/* 1974 */       updateAndDraw();
/*      */     }
/* 1976 */     else if (roi != null) {
/* 1977 */       roi.startPaste(clipboard);
/* 1978 */       Undo.setup(3, this);
/*      */     }
/* 1980 */     this.changes = true;
/*      */   }
/*      */ 
/*      */   public static ImagePlus getClipboard()
/*      */   {
/* 1985 */     return clipboard;
/*      */   }
/*      */ 
/*      */   public static void resetClipboard()
/*      */   {
/* 1990 */     clipboard = null;
/*      */   }
/*      */ 
/*      */   protected void notifyListeners(int id) {
/* 1994 */     synchronized (listeners) {
/* 1995 */       for (int i = 0; i < listeners.size(); i++) {
/* 1996 */         ImageListener listener = (ImageListener)listeners.elementAt(i);
/* 1997 */         switch (id) {
/*      */         case 0:
/* 1999 */           listener.imageOpened(this);
/* 2000 */           break;
/*      */         case 1:
/* 2002 */           listener.imageClosed(this);
/* 2003 */           break;
/*      */         case 2:
/* 2005 */           listener.imageUpdated(this);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void addImageListener(ImageListener listener)
/*      */   {
/* 2013 */     listeners.addElement(listener);
/*      */   }
/*      */ 
/*      */   public static void removeImageListener(ImageListener listener) {
/* 2017 */     listeners.removeElement(listener);
/*      */   }
/*      */ 
/*      */   public boolean isLocked()
/*      */   {
/* 2022 */     return this.locked;
/*      */   }
/*      */ 
/*      */   public void setOpenAsHyperStack(boolean openAsHyperStack) {
/* 2026 */     this.openAsHyperStack = openAsHyperStack;
/*      */   }
/*      */ 
/*      */   public boolean getOpenAsHyperStack() {
/* 2030 */     return this.openAsHyperStack;
/*      */   }
/*      */ 
/*      */   public boolean isComposite()
/*      */   {
/* 2035 */     return (this.compositeImage) && (this.nChannels >= 1) && ((this instanceof CompositeImage));
/*      */   }
/*      */ 
/*      */   public void setDisplayRange(double min, double max)
/*      */   {
/* 2041 */     if (this.ip != null)
/* 2042 */       this.ip.setMinAndMax(min, max);
/*      */   }
/*      */ 
/*      */   public double getDisplayRangeMin() {
/* 2046 */     return this.ip.getMin();
/*      */   }
/*      */ 
/*      */   public double getDisplayRangeMax() {
/* 2050 */     return this.ip.getMax();
/*      */   }
/*      */ 
/*      */   public void setDisplayRange(double min, double max, int channels)
/*      */   {
/* 2058 */     if ((this.ip instanceof ColorProcessor))
/* 2059 */       ((ColorProcessor)this.ip).setMinAndMax(min, max, channels);
/*      */     else
/* 2061 */       this.ip.setMinAndMax(min, max);
/*      */   }
/*      */ 
/*      */   public void resetDisplayRange() {
/* 2065 */     if ((this.imageType == 1) && (default16bitDisplayRange >= 8) && (default16bitDisplayRange <= 16) && (!getCalibration().isSigned16Bit()))
/* 2066 */       this.ip.setMinAndMax(0.0D, Math.pow(2.0D, default16bitDisplayRange) - 1.0D);
/*      */     else
/* 2068 */       this.ip.resetMinAndMax();
/*      */   }
/*      */ 
/*      */   public static void setDefault16bitRange(int bitDepth)
/*      */   {
/* 2074 */     if ((bitDepth != 8) && (bitDepth != 10) && (bitDepth != 12) && (bitDepth != 15) && (bitDepth != 16))
/* 2075 */       bitDepth = 0;
/* 2076 */     default16bitDisplayRange = bitDepth;
/*      */   }
/*      */ 
/*      */   public static int getDefault16bitRange()
/*      */   {
/* 2081 */     return default16bitDisplayRange;
/*      */   }
/*      */ 
/*      */   public void updatePosition(int c, int z, int t)
/*      */   {
/* 2086 */     this.position[0] = c;
/* 2087 */     this.position[1] = z;
/* 2088 */     this.position[2] = t;
/*      */   }
/*      */ 
/*      */   public ImagePlus flatten()
/*      */   {
/* 2093 */     ImagePlus imp2 = createImagePlus();
/* 2094 */     String title = "Flat_" + getTitle();
/* 2095 */     ImageCanvas ic2 = new ImageCanvas(imp2);
/* 2096 */     imp2.flatteningCanvas = ic2;
/* 2097 */     imp2.setRoi(getRoi());
/* 2098 */     ImageCanvas ic = getCanvas();
/* 2099 */     Overlay overlay2 = getOverlay();
/* 2100 */     ic2.setOverlay(overlay2);
/* 2101 */     if (ic != null) {
/* 2102 */       ic2.setShowAllROIs(ic.getShowAllROIs());
/*      */     }
/*      */ 
/* 2106 */     BufferedImage bi = new BufferedImage(this.width, this.height, 2);
/* 2107 */     Graphics g = bi.getGraphics();
/* 2108 */     g.drawImage(getImage(), 0, 0, null);
/* 2109 */     ic2.paint(g);
/* 2110 */     imp2.flatteningCanvas = null;
/* 2111 */     if (Recorder.record) Recorder.recordCall("imp = IJ.getImage().flatten();");
/* 2112 */     return new ImagePlus(title, new ColorProcessor(bi));
/*      */   }
/*      */ 
/*      */   public void setOverlay(Overlay overlay)
/*      */   {
/* 2123 */     ImageCanvas ic = getCanvas();
/* 2124 */     if (ic != null) {
/* 2125 */       ic.setOverlay(overlay);
/* 2126 */       overlay = null;
/*      */     } else {
/* 2128 */       this.overlay = overlay;
/* 2129 */     }setHideOverlay(false);
/*      */   }
/*      */ 
/*      */   public void setOverlay(Shape shape, Color color, BasicStroke stroke)
/*      */   {
/* 2139 */     if (shape == null) {
/* 2140 */       setOverlay(null); return;
/* 2141 */     }Roi roi = new ShapeRoi(shape);
/* 2142 */     roi.setStrokeColor(color);
/* 2143 */     roi.setStroke(stroke);
/* 2144 */     setOverlay(new Overlay(roi));
/*      */   }
/*      */ 
/*      */   public void setOverlay(Roi roi, Color strokeColor, int strokeWidth, Color fillColor)
/*      */   {
/* 2151 */     roi.setStrokeColor(strokeColor);
/* 2152 */     roi.setStrokeWidth(strokeWidth);
/* 2153 */     roi.setFillColor(fillColor);
/* 2154 */     setOverlay(new Overlay(roi));
/*      */   }
/*      */ 
/*      */   public Overlay getOverlay()
/*      */   {
/* 2159 */     ImageCanvas ic = getCanvas();
/* 2160 */     if (ic != null) {
/* 2161 */       return ic.getOverlay();
/*      */     }
/* 2163 */     return this.overlay;
/*      */   }
/*      */ 
/*      */   public void setHideOverlay(boolean hide) {
/* 2167 */     this.hideOverlay = hide;
/* 2168 */     ImageCanvas ic = getCanvas();
/* 2169 */     if ((ic != null) && (ic.getOverlay() != null))
/* 2170 */       ic.repaint();
/*      */   }
/*      */ 
/*      */   public boolean getHideOverlay() {
/* 2174 */     return this.hideOverlay;
/*      */   }
/*      */ 
/*      */   public synchronized Object clone()
/*      */   {
/*      */     try {
/* 2180 */       ImagePlus copy = (ImagePlus)super.clone();
/* 2181 */       copy.win = null;
/* 2182 */       return copy; } catch (CloneNotSupportedException e) {
/*      */     }
/* 2184 */     return null;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 2189 */     return "imp[" + getTitle() + " " + this.width + "x" + this.height + "x" + getStackSize() + "]";
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.ImagePlus
 * JD-Core Version:    0.6.2
 */