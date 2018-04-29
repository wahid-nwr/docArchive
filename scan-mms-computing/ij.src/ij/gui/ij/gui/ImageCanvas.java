/*      */ package ij.gui;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImageJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.Menus;
/*      */ import ij.Prefs;
/*      */ import ij.WindowManager;
/*      */ import ij.macro.Interpreter;
/*      */ import ij.macro.MacroRunner;
/*      */ import ij.plugin.WandToolOptions;
/*      */ import ij.plugin.frame.Recorder;
/*      */ import ij.plugin.frame.RoiManager;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.util.Java2;
/*      */ import ij.util.Tools;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Canvas;
/*      */ import java.awt.Color;
/*      */ import java.awt.Container;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.List;
/*      */ import java.awt.Point;
/*      */ import java.awt.PopupMenu;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.event.MouseMotionListener;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class ImageCanvas extends Canvas
/*      */   implements MouseListener, MouseMotionListener, Cloneable
/*      */ {
/*   22 */   protected static Cursor defaultCursor = new Cursor(0);
/*   23 */   protected static Cursor handCursor = new Cursor(12);
/*   24 */   protected static Cursor moveCursor = new Cursor(13);
/*   25 */   protected static Cursor crosshairCursor = new Cursor(1);
/*      */ 
/*   27 */   public static boolean usePointer = Prefs.usePointerCursor;
/*      */   protected ImagePlus imp;
/*      */   protected boolean imageUpdated;
/*      */   protected Rectangle srcRect;
/*      */   protected int imageWidth;
/*      */   protected int imageHeight;
/*      */   protected int xMouse;
/*      */   protected int yMouse;
/*   36 */   private boolean showCursorStatus = true;
/*      */   private int sx2;
/*      */   private int sy2;
/*      */   private boolean disablePopupMenu;
/*      */   private boolean showAllROIs;
/*      */   private static Color zoomIndicatorColor;
/*      */   private static Font smallFont;
/*      */   private static Font largeFont;
/*      */   private Font font;
/*      */   private Rectangle[] labelRects;
/*      */   private boolean maxBoundsReset;
/*      */   private Overlay overlay;
/*      */   private Overlay showAllList;
/*      */   private static final int LIST_OFFSET = 100000;
/*   47 */   private static Color showAllColor = Prefs.getColor("showcolor", new Color(0, 255, 255));
/*   48 */   private Color defaultColor = showAllColor;
/*      */   private static Color labelColor;
/*      */   private static Color bgColor;
/*      */   private int resetMaxBoundsCount;
/*      */   private Roi currentRoi;
/*      */   protected ImageJ ij;
/*      */   protected double magnification;
/*      */   protected int dstWidth;
/*      */   protected int dstHeight;
/*      */   protected int xMouseStart;
/*      */   protected int yMouseStart;
/*      */   protected int xSrcStart;
/*      */   protected int ySrcStart;
/*      */   protected int flags;
/*      */   private Image offScreenImage;
/*   64 */   private int offScreenWidth = 0;
/*   65 */   private int offScreenHeight = 0;
/*   66 */   private boolean mouseExited = true;
/*      */   private boolean customRoi;
/*      */   private boolean drawNames;
/*      */   long firstFrame;
/*      */   int frames;
/*      */   int fps;
/*      */   int count;
/*  661 */   private static final double[] zoomLevels = { 0.01388888888888889D, 0.02083333333333333D, 0.03125D, 0.04166666666666666D, 0.0625D, 0.08333333333333333D, 0.125D, 0.1666666666666667D, 0.25D, 0.3333333333333333D, 0.5D, 0.75D, 1.0D, 1.5D, 2.0D, 3.0D, 4.0D, 6.0D, 8.0D, 12.0D, 16.0D, 24.0D, 32.0D };
/*      */ 
/*      */   public ImageCanvas(ImagePlus imp)
/*      */   {
/*   72 */     this.imp = imp;
/*   73 */     this.ij = IJ.getInstance();
/*   74 */     int width = imp.getWidth();
/*   75 */     int height = imp.getHeight();
/*   76 */     this.imageWidth = width;
/*   77 */     this.imageHeight = height;
/*   78 */     this.srcRect = new Rectangle(0, 0, this.imageWidth, this.imageHeight);
/*   79 */     setDrawingSize(this.imageWidth, this.imageHeight);
/*   80 */     this.magnification = 1.0D;
/*   81 */     addMouseListener(this);
/*   82 */     addMouseMotionListener(this);
/*   83 */     addKeyListener(this.ij);
/*   84 */     setFocusTraversalKeysEnabled(false);
/*      */   }
/*      */ 
/*      */   void updateImage(ImagePlus imp) {
/*   88 */     this.imp = imp;
/*   89 */     int width = imp.getWidth();
/*   90 */     int height = imp.getHeight();
/*   91 */     this.imageWidth = width;
/*   92 */     this.imageHeight = height;
/*   93 */     this.srcRect = new Rectangle(0, 0, this.imageWidth, this.imageHeight);
/*   94 */     setDrawingSize(this.imageWidth, this.imageHeight);
/*   95 */     this.magnification = 1.0D;
/*      */   }
/*      */ 
/*      */   void update(ImageCanvas ic)
/*      */   {
/*  100 */     if ((ic == null) || (ic == this) || (ic.imp == null))
/*  101 */       return;
/*  102 */     if ((ic.imp.getWidth() != this.imageWidth) || (ic.imp.getHeight() != this.imageHeight))
/*  103 */       return;
/*  104 */     this.srcRect = new Rectangle(ic.srcRect.x, ic.srcRect.y, ic.srcRect.width, ic.srcRect.height);
/*  105 */     setMagnification(ic.magnification);
/*  106 */     setDrawingSize(ic.dstWidth, ic.dstHeight);
/*      */   }
/*      */ 
/*      */   public void setSourceRect(Rectangle r) {
/*  110 */     this.srcRect = r;
/*      */   }
/*      */ 
/*      */   void setSrcRect(Rectangle srcRect) {
/*  114 */     this.srcRect = srcRect;
/*      */   }
/*      */ 
/*      */   public Rectangle getSrcRect() {
/*  118 */     return this.srcRect;
/*      */   }
/*      */ 
/*      */   public void setDrawingSize(int width, int height) {
/*  122 */     this.dstWidth = width;
/*  123 */     this.dstHeight = height;
/*  124 */     setSize(this.dstWidth, this.dstHeight);
/*      */   }
/*      */ 
/*      */   public void setImageUpdated()
/*      */   {
/*  130 */     this.imageUpdated = true;
/*      */   }
/*      */ 
/*      */   public void update(Graphics g) {
/*  134 */     paint(g);
/*      */   }
/*      */ 
/*      */   public void paint(Graphics g) {
/*  138 */     Roi roi = this.imp.getRoi();
/*  139 */     if ((roi != null) || (this.showAllROIs) || (this.overlay != null)) {
/*  140 */       if (roi != null) roi.updatePaste();
/*  141 */       if ((!IJ.isMacOSX()) && (this.imageWidth != 0)) {
/*  142 */         paintDoubleBuffered(g);
/*  143 */         return;
/*      */       }
/*      */     }
/*      */     try {
/*  147 */       if (this.imageUpdated) {
/*  148 */         this.imageUpdated = false;
/*  149 */         this.imp.updateImage();
/*      */       }
/*  151 */       Java2.setBilinearInterpolation(g, Prefs.interpolateScaledImages);
/*  152 */       Image img = this.imp.getImage();
/*  153 */       if (img != null) {
/*  154 */         g.drawImage(img, 0, 0, (int)(this.srcRect.width * this.magnification), (int)(this.srcRect.height * this.magnification), this.srcRect.x, this.srcRect.y, this.srcRect.x + this.srcRect.width, this.srcRect.y + this.srcRect.height, null);
/*      */       }
/*  156 */       if (this.overlay != null) drawOverlay(g);
/*  157 */       if (this.showAllROIs) drawAllROIs(g);
/*  158 */       if (roi != null) drawRoi(roi, g);
/*  159 */       if ((this.srcRect.width < this.imageWidth) || (this.srcRect.height < this.imageHeight))
/*  160 */         drawZoomIndicator(g);
/*  161 */       if (IJ.debugMode) showFrameRate(g); 
/*      */     }
/*  163 */     catch (OutOfMemoryError e) { IJ.outOfMemory("Paint"); }
/*      */   }
/*      */ 
/*      */   private void drawRoi(Roi roi, Graphics g) {
/*  167 */     if (roi == this.currentRoi) {
/*  168 */       Color lineColor = roi.getStrokeColor();
/*  169 */       Color fillColor = roi.getFillColor();
/*  170 */       float lineWidth = roi.getStrokeWidth();
/*  171 */       roi.setStrokeColor(null);
/*  172 */       roi.setFillColor(null);
/*  173 */       boolean strokeSet = roi.getStroke() != null;
/*  174 */       if (strokeSet)
/*  175 */         roi.setStrokeWidth(1.0F);
/*  176 */       roi.draw(g);
/*  177 */       roi.setStrokeColor(lineColor);
/*  178 */       if (strokeSet)
/*  179 */         roi.setStrokeWidth(lineWidth);
/*  180 */       roi.setFillColor(fillColor);
/*  181 */       this.currentRoi = null;
/*      */     } else {
/*  183 */       roi.draw(g);
/*      */     }
/*      */   }
/*      */ 
/*  187 */   void drawAllROIs(Graphics g) { RoiManager rm = RoiManager.getInstance();
/*  188 */     if (rm == null) {
/*  189 */       rm = Interpreter.getBatchModeRoiManager();
/*  190 */       if ((rm != null) && (rm.getList().getItemCount() == 0))
/*  191 */         rm = null;
/*      */     }
/*  193 */     if (rm == null)
/*      */     {
/*  196 */       this.showAllROIs = false;
/*  197 */       repaint();
/*  198 */       return;
/*      */     }
/*  200 */     initGraphics(g, null, showAllColor);
/*  201 */     Hashtable rois = rm.getROIs();
/*  202 */     List list = rm.getList();
/*  203 */     boolean drawLabels = rm.getDrawLabels();
/*  204 */     this.currentRoi = null;
/*  205 */     int n = list.getItemCount();
/*  206 */     if (IJ.debugMode) IJ.log("paint: drawing " + n + " \"Show All\" ROIs");
/*  207 */     if ((this.labelRects == null) || (this.labelRects.length != n))
/*  208 */       this.labelRects = new Rectangle[n];
/*  209 */     if (!drawLabels)
/*  210 */       this.showAllList = new Overlay();
/*      */     else
/*  212 */       this.showAllList = null;
/*  213 */     if (this.imp == null)
/*  214 */       return;
/*  215 */     int currentImage = this.imp.getCurrentSlice();
/*  216 */     int channel = 0; int slice = 0; int frame = 0;
/*  217 */     boolean hyperstack = this.imp.isHyperStack();
/*  218 */     if (hyperstack) {
/*  219 */       channel = this.imp.getChannel();
/*  220 */       slice = this.imp.getSlice();
/*  221 */       frame = this.imp.getFrame();
/*      */     }
/*  223 */     this.drawNames = Prefs.useNamesAsLabels;
/*  224 */     for (int i = 0; i < n; i++) {
/*  225 */       String label = list.getItem(i);
/*  226 */       Roi roi = (Roi)rois.get(label);
/*  227 */       if (roi != null) {
/*  228 */         if (this.showAllList != null)
/*  229 */           this.showAllList.add(roi);
/*  230 */         if ((i < 200) && (drawLabels) && (roi == this.imp.getRoi()))
/*  231 */           this.currentRoi = roi;
/*  232 */         if ((Prefs.showAllSliceOnly) && (this.imp.getStackSize() > 1)) {
/*  233 */           if ((hyperstack) && (roi.getPosition() == 0)) {
/*  234 */             int c = roi.getCPosition();
/*  235 */             int z = roi.getZPosition();
/*  236 */             int t = roi.getTPosition();
/*  237 */             if (((c == 0) || (c == channel)) && ((z == 0) || (z == slice)) && ((t == 0) || (t == frame)))
/*  238 */               drawRoi(g, roi, drawLabels ? i : -1);
/*      */           } else {
/*  240 */             int position = roi.getPosition();
/*  241 */             if (position == 0)
/*  242 */               position = getSliceNumber(roi.getName());
/*  243 */             if ((position == 0) || (position == currentImage))
/*  244 */               drawRoi(g, roi, drawLabels ? i : -1);
/*      */           }
/*      */         }
/*  247 */         else drawRoi(g, roi, drawLabels ? i : -1); 
/*      */       }
/*      */     }
/*  249 */     ((Graphics2D)g).setStroke(Roi.onePixelWide);
/*  250 */     this.drawNames = false; }
/*      */ 
/*      */   public int getSliceNumber(String label)
/*      */   {
/*  254 */     if (label == null) return 0;
/*  255 */     int slice = 0;
/*  256 */     if ((label.length() >= 14) && (label.charAt(4) == '-') && (label.charAt(9) == '-'))
/*  257 */       slice = (int)Tools.parseDouble(label.substring(0, 4), -1.0D);
/*  258 */     else if ((label.length() >= 17) && (label.charAt(5) == '-') && (label.charAt(11) == '-'))
/*  259 */       slice = (int)Tools.parseDouble(label.substring(0, 5), -1.0D);
/*  260 */     else if ((label.length() >= 20) && (label.charAt(6) == '-') && (label.charAt(13) == '-'))
/*  261 */       slice = (int)Tools.parseDouble(label.substring(0, 6), -1.0D);
/*  262 */     return slice;
/*      */   }
/*      */ 
/*      */   void drawOverlay(Graphics g) {
/*  266 */     if ((this.imp != null) && (this.imp.getHideOverlay()))
/*  267 */       return;
/*  268 */     Color labelColor = this.overlay.getLabelColor();
/*  269 */     if (labelColor == null) labelColor = Color.white;
/*  270 */     initGraphics(g, labelColor, Roi.getColor());
/*  271 */     int n = this.overlay.size();
/*  272 */     if (IJ.debugMode) IJ.log("paint: drawing " + n + " ROI display list");
/*  273 */     int currentImage = this.imp != null ? this.imp.getCurrentSlice() : -1;
/*  274 */     if (this.imp.getStackSize() == 1)
/*  275 */       currentImage = -1;
/*  276 */     int channel = 0; int slice = 0; int frame = 0;
/*  277 */     boolean hyperstack = this.imp.isHyperStack();
/*  278 */     if (hyperstack) {
/*  279 */       channel = this.imp.getChannel();
/*  280 */       slice = this.imp.getSlice();
/*  281 */       frame = this.imp.getFrame();
/*      */     }
/*  283 */     this.drawNames = this.overlay.getDrawNames();
/*  284 */     boolean drawLabels = (this.drawNames) || (this.overlay.getDrawLabels());
/*  285 */     this.font = this.overlay.getLabelFont();
/*  286 */     for (int i = 0; (i < n) && 
/*  287 */       (this.overlay != null); i++)
/*      */     {
/*  288 */       Roi roi = this.overlay.get(i);
/*  289 */       if ((hyperstack) && (roi.getPosition() == 0)) {
/*  290 */         int c = roi.getCPosition();
/*  291 */         int z = roi.getZPosition();
/*  292 */         int t = roi.getTPosition();
/*  293 */         if (((c == 0) || (c == channel)) && ((z == 0) || (z == slice)) && ((t == 0) || (t == frame)))
/*  294 */           drawRoi(g, roi, drawLabels ? i + 100000 : -1);
/*      */       } else {
/*  296 */         int position = roi.getPosition();
/*  297 */         if ((position == 0) || (position == currentImage))
/*  298 */           drawRoi(g, roi, drawLabels ? i + 100000 : -1);
/*      */       }
/*      */     }
/*  301 */     ((Graphics2D)g).setStroke(Roi.onePixelWide);
/*  302 */     this.drawNames = false;
/*  303 */     this.font = null;
/*      */   }
/*      */ 
/*      */   void initGraphics(Graphics g, Color textColor, Color defaultColor) {
/*  307 */     if (smallFont == null) {
/*  308 */       smallFont = new Font("SansSerif", 0, 9);
/*  309 */       largeFont = new Font("SansSerif", 0, 12);
/*      */     }
/*  311 */     if (textColor != null) {
/*  312 */       labelColor = textColor;
/*  313 */       if ((this.overlay != null) && (this.overlay.getDrawBackgrounds()))
/*  314 */         bgColor = new Color(255 - labelColor.getRed(), 255 - labelColor.getGreen(), 255 - labelColor.getBlue());
/*      */       else
/*  316 */         bgColor = null;
/*      */     } else {
/*  318 */       int red = defaultColor.getRed();
/*  319 */       int green = defaultColor.getGreen();
/*  320 */       int blue = defaultColor.getBlue();
/*  321 */       if ((red + green + blue) / 3 < 128)
/*  322 */         labelColor = Color.white;
/*      */       else
/*  324 */         labelColor = Color.black;
/*  325 */       bgColor = defaultColor;
/*      */     }
/*  327 */     this.defaultColor = defaultColor;
/*  328 */     g.setColor(defaultColor);
/*      */   }
/*      */ 
/*      */   void drawRoi(Graphics g, Roi roi, int index) {
/*  332 */     int type = roi.getType();
/*  333 */     ImagePlus imp2 = roi.getImage();
/*  334 */     roi.setImage(this.imp);
/*  335 */     Color saveColor = roi.getStrokeColor();
/*  336 */     if (saveColor == null)
/*  337 */       roi.setStrokeColor(this.defaultColor);
/*  338 */     if ((roi instanceof TextRoi))
/*  339 */       ((TextRoi)roi).drawText(g);
/*      */     else
/*  341 */       roi.drawOverlay(g);
/*  342 */     roi.setStrokeColor(saveColor);
/*  343 */     if (index >= 0) {
/*  344 */       if (roi == this.currentRoi)
/*  345 */         g.setColor(Roi.getColor());
/*      */       else
/*  347 */         g.setColor(this.defaultColor);
/*  348 */       drawRoiLabel(g, index, roi);
/*      */     }
/*  350 */     if (imp2 != null)
/*  351 */       roi.setImage(imp2);
/*      */     else
/*  353 */       roi.setImage(null);
/*      */   }
/*      */ 
/*      */   void drawRoiLabel(Graphics g, int index, Roi roi) {
/*  357 */     Rectangle r = roi.getBounds();
/*  358 */     int x = screenX(r.x);
/*  359 */     int y = screenY(r.y);
/*  360 */     double mag = getMagnification();
/*  361 */     int width = (int)(r.width * mag);
/*  362 */     int height = (int)(r.height * mag);
/*  363 */     int size = (width > 40) && (height > 40) ? 12 : 9;
/*  364 */     if (this.font != null) {
/*  365 */       g.setFont(this.font);
/*  366 */       size = this.font.getSize();
/*  367 */     } else if (size == 12) {
/*  368 */       g.setFont(largeFont);
/*      */     } else {
/*  370 */       g.setFont(smallFont);
/*  371 */     }boolean drawingList = index >= 100000;
/*  372 */     if (drawingList) index -= 100000;
/*  373 */     String label = "" + (index + 1);
/*  374 */     if ((this.drawNames) && (roi.getName() != null))
/*  375 */       label = roi.getName();
/*  376 */     FontMetrics metrics = g.getFontMetrics();
/*  377 */     int w = metrics.stringWidth(label);
/*  378 */     x = x + width / 2 - w / 2;
/*  379 */     y = y + height / 2 + Math.max(size / 2, 6);
/*  380 */     int h = metrics.getAscent() + metrics.getDescent();
/*  381 */     if (bgColor != null) {
/*  382 */       g.setColor(bgColor);
/*  383 */       g.fillRoundRect(x - 1, y - h + 2, w + 1, h - 3, 5, 5);
/*      */     }
/*  385 */     if ((!drawingList) && (this.labelRects != null) && (index < this.labelRects.length))
/*  386 */       this.labelRects[index] = new Rectangle(x - 1, y - h + 2, w + 1, h);
/*  387 */     g.setColor(labelColor);
/*  388 */     g.drawString(label, x, y - 2);
/*  389 */     g.setColor(this.defaultColor);
/*      */   }
/*      */ 
/*      */   void drawZoomIndicator(Graphics g) {
/*  393 */     int x1 = 10;
/*  394 */     int y1 = 10;
/*  395 */     double aspectRatio = this.imageHeight / this.imageWidth;
/*  396 */     int w1 = 64;
/*  397 */     if (aspectRatio > 1.0D)
/*  398 */       w1 = (int)(w1 / aspectRatio);
/*  399 */     int h1 = (int)(w1 * aspectRatio);
/*  400 */     if (w1 < 4) w1 = 4;
/*  401 */     if (h1 < 4) h1 = 4;
/*  402 */     int w2 = (int)(w1 * (this.srcRect.width / this.imageWidth));
/*  403 */     int h2 = (int)(h1 * (this.srcRect.height / this.imageHeight));
/*  404 */     if (w2 < 1) w2 = 1;
/*  405 */     if (h2 < 1) h2 = 1;
/*  406 */     int x2 = (int)(w1 * (this.srcRect.x / this.imageWidth));
/*  407 */     int y2 = (int)(h1 * (this.srcRect.y / this.imageHeight));
/*  408 */     if (zoomIndicatorColor == null)
/*  409 */       zoomIndicatorColor = new Color(128, 128, 255);
/*  410 */     g.setColor(zoomIndicatorColor);
/*  411 */     ((Graphics2D)g).setStroke(Roi.onePixelWide);
/*  412 */     g.drawRect(x1, y1, w1, h1);
/*  413 */     if ((w2 * h2 <= 200) || (w2 < 10) || (h2 < 10))
/*  414 */       g.fillRect(x1 + x2, y1 + y2, w2, h2);
/*      */     else
/*  416 */       g.drawRect(x1 + x2, y1 + y2, w2, h2);
/*      */   }
/*      */ 
/*      */   void paintDoubleBuffered(Graphics g)
/*      */   {
/*  422 */     int srcRectWidthMag = (int)(this.srcRect.width * this.magnification);
/*  423 */     int srcRectHeightMag = (int)(this.srcRect.height * this.magnification);
/*  424 */     if ((this.offScreenImage == null) || (this.offScreenWidth != srcRectWidthMag) || (this.offScreenHeight != srcRectHeightMag)) {
/*  425 */       this.offScreenImage = createImage(srcRectWidthMag, srcRectHeightMag);
/*  426 */       this.offScreenWidth = srcRectWidthMag;
/*  427 */       this.offScreenHeight = srcRectHeightMag;
/*      */     }
/*  429 */     Roi roi = this.imp.getRoi();
/*      */     try {
/*  431 */       if (this.imageUpdated) {
/*  432 */         this.imageUpdated = false;
/*  433 */         this.imp.updateImage();
/*      */       }
/*  435 */       Graphics offScreenGraphics = this.offScreenImage.getGraphics();
/*  436 */       Java2.setBilinearInterpolation(offScreenGraphics, Prefs.interpolateScaledImages);
/*  437 */       Image img = this.imp.getImage();
/*  438 */       if (img != null) {
/*  439 */         offScreenGraphics.drawImage(img, 0, 0, srcRectWidthMag, srcRectHeightMag, this.srcRect.x, this.srcRect.y, this.srcRect.x + this.srcRect.width, this.srcRect.y + this.srcRect.height, null);
/*      */       }
/*  441 */       if (this.overlay != null) drawOverlay(offScreenGraphics);
/*  442 */       if (this.showAllROIs) drawAllROIs(offScreenGraphics);
/*  443 */       if (roi != null) drawRoi(roi, offScreenGraphics);
/*  444 */       if ((this.srcRect.width < this.imageWidth) || (this.srcRect.height < this.imageHeight))
/*  445 */         drawZoomIndicator(offScreenGraphics);
/*  446 */       if (IJ.debugMode) showFrameRate(offScreenGraphics);
/*  447 */       g.drawImage(this.offScreenImage, 0, 0, null);
/*      */     } catch (OutOfMemoryError e) {
/*  449 */       IJ.outOfMemory("Paint");
/*      */     }
/*      */   }
/*      */ 
/*  453 */   public void resetDoubleBuffer() { this.offScreenImage = null; }
/*      */ 
/*      */ 
/*      */   void showFrameRate(Graphics g)
/*      */   {
/*  460 */     this.frames += 1;
/*  461 */     if (System.currentTimeMillis() > this.firstFrame + 1000L) {
/*  462 */       this.firstFrame = System.currentTimeMillis();
/*  463 */       this.fps = this.frames;
/*  464 */       this.frames = 0;
/*      */     }
/*  466 */     g.setColor(Color.white);
/*  467 */     g.fillRect(10, 12, 50, 15);
/*  468 */     g.setColor(Color.black);
/*  469 */     g.drawString((int)(this.fps + 0.5D) + " fps", 10, 25);
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize() {
/*  473 */     return new Dimension(this.dstWidth, this.dstHeight);
/*      */   }
/*      */ 
/*      */   public Point getCursorLoc()
/*      */   {
/*  490 */     return new Point(this.xMouse, this.yMouse);
/*      */   }
/*      */ 
/*      */   public boolean cursorOverImage()
/*      */   {
/*  495 */     return !this.mouseExited;
/*      */   }
/*      */ 
/*      */   public int getModifiers()
/*      */   {
/*  500 */     return this.flags;
/*      */   }
/*      */ 
/*      */   public ImagePlus getImage()
/*      */   {
/*  505 */     return this.imp;
/*      */   }
/*      */ 
/*      */   public void setCursor(int sx, int sy, int ox, int oy)
/*      */   {
/*  510 */     this.xMouse = ox;
/*  511 */     this.yMouse = oy;
/*  512 */     this.mouseExited = false;
/*  513 */     Roi roi = this.imp.getRoi();
/*  514 */     ImageWindow win = this.imp.getWindow();
/*  515 */     if (win == null)
/*  516 */       return;
/*  517 */     if (IJ.spaceBarDown()) {
/*  518 */       setCursor(handCursor);
/*  519 */       return;
/*      */     }
/*  521 */     int id = Toolbar.getToolId();
/*  522 */     switch (Toolbar.getToolId()) {
/*      */     case 11:
/*  524 */       setCursor(moveCursor);
/*  525 */       break;
/*      */     case 12:
/*  527 */       setCursor(handCursor);
/*  528 */       break;
/*      */     default:
/*  530 */       if ((id == 10) || (id >= 15)) {
/*  531 */         if (Prefs.usePointerCursor) setCursor(defaultCursor); else setCursor(crosshairCursor); 
/*      */       }
/*  532 */       else if ((roi != null) && (roi.getState() != 0) && (roi.isHandle(sx, sy) >= 0))
/*  533 */         setCursor(handCursor);
/*  534 */       else if ((Prefs.usePointerCursor) || ((roi != null) && (roi.getState() != 0) && (roi.contains(ox, oy))))
/*  535 */         setCursor(defaultCursor);
/*      */       else
/*  537 */         setCursor(crosshairCursor);
/*      */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int offScreenX(int sx) {
/*  543 */     return this.srcRect.x + (int)(sx / this.magnification);
/*      */   }
/*      */ 
/*      */   public int offScreenY(int sy)
/*      */   {
/*  548 */     return this.srcRect.y + (int)(sy / this.magnification);
/*      */   }
/*      */ 
/*      */   public double offScreenXD(int sx)
/*      */   {
/*  553 */     return this.srcRect.x + sx / this.magnification;
/*      */   }
/*      */ 
/*      */   public double offScreenYD(int sy)
/*      */   {
/*  558 */     return this.srcRect.y + sy / this.magnification;
/*      */   }
/*      */ 
/*      */   public int screenX(int ox)
/*      */   {
/*  564 */     return (int)((ox - this.srcRect.x) * this.magnification);
/*      */   }
/*      */ 
/*      */   public int screenY(int oy)
/*      */   {
/*  569 */     return (int)((oy - this.srcRect.y) * this.magnification);
/*      */   }
/*      */ 
/*      */   public int screenXD(double ox)
/*      */   {
/*  574 */     return (int)((ox - this.srcRect.x) * this.magnification);
/*      */   }
/*      */ 
/*      */   public int screenYD(double oy)
/*      */   {
/*  579 */     return (int)((oy - this.srcRect.y) * this.magnification);
/*      */   }
/*      */ 
/*      */   public double getMagnification() {
/*  583 */     return this.magnification;
/*      */   }
/*      */ 
/*      */   public void setMagnification(double magnification) {
/*  587 */     setMagnification2(magnification);
/*      */   }
/*      */ 
/*      */   void setMagnification2(double magnification) {
/*  591 */     if (magnification > 32.0D) magnification = 32.0D;
/*  592 */     if (magnification < 0.03125D) magnification = 0.03125D;
/*  593 */     this.magnification = magnification;
/*  594 */     this.imp.setTitle(this.imp.getTitle());
/*      */   }
/*      */ 
/*      */   void resizeCanvas(int width, int height)
/*      */   {
/*  599 */     ImageWindow win = this.imp.getWindow();
/*      */ 
/*  601 */     if ((!this.maxBoundsReset) && ((width > this.dstWidth) || (height > this.dstHeight)) && (win != null) && (win.maxBounds != null) && (width != win.maxBounds.width - 10)) {
/*  602 */       if (this.resetMaxBoundsCount != 0)
/*  603 */         resetMaxBounds();
/*  604 */       this.resetMaxBoundsCount += 1;
/*      */     }
/*  606 */     if (IJ.altKeyDown()) {
/*  607 */       fitToWindow(); return;
/*  608 */     }if ((this.srcRect.width < this.imageWidth) || (this.srcRect.height < this.imageHeight)) {
/*  609 */       if (width > this.imageWidth * this.magnification)
/*  610 */         width = (int)(this.imageWidth * this.magnification);
/*  611 */       if (height > this.imageHeight * this.magnification)
/*  612 */         height = (int)(this.imageHeight * this.magnification);
/*  613 */       setDrawingSize(width, height);
/*  614 */       this.srcRect.width = ((int)(this.dstWidth / this.magnification));
/*  615 */       this.srcRect.height = ((int)(this.dstHeight / this.magnification));
/*  616 */       if (this.srcRect.x + this.srcRect.width > this.imageWidth)
/*  617 */         this.srcRect.x = (this.imageWidth - this.srcRect.width);
/*  618 */       if (this.srcRect.y + this.srcRect.height > this.imageHeight)
/*  619 */         this.srcRect.y = (this.imageHeight - this.srcRect.height);
/*  620 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fitToWindow()
/*      */   {
/*  626 */     ImageWindow win = this.imp.getWindow();
/*  627 */     if (win == null) return;
/*  628 */     Rectangle bounds = win.getBounds();
/*  629 */     Insets insets = win.getInsets();
/*  630 */     int sliderHeight = (win instanceof StackWindow) ? 20 : 0;
/*  631 */     double xmag = (bounds.width - 10) / this.srcRect.width;
/*  632 */     double ymag = (bounds.height - (10 + insets.top + sliderHeight)) / this.srcRect.height;
/*  633 */     setMagnification(Math.min(xmag, ymag));
/*  634 */     int width = (int)(this.imageWidth * this.magnification);
/*  635 */     int height = (int)(this.imageHeight * this.magnification);
/*  636 */     if ((width == this.dstWidth) && (height == this.dstHeight)) return;
/*  637 */     this.srcRect = new Rectangle(0, 0, this.imageWidth, this.imageHeight);
/*  638 */     setDrawingSize(width, height);
/*  639 */     getParent().doLayout();
/*      */   }
/*      */ 
/*      */   void setMaxBounds() {
/*  643 */     if (this.maxBoundsReset) {
/*  644 */       this.maxBoundsReset = false;
/*  645 */       ImageWindow win = this.imp.getWindow();
/*  646 */       if ((win != null) && (!IJ.isLinux()) && (win.maxBounds != null)) {
/*  647 */         win.setMaximizedBounds(win.maxBounds);
/*  648 */         win.setMaxBoundsTime = System.currentTimeMillis();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void resetMaxBounds() {
/*  654 */     ImageWindow win = this.imp.getWindow();
/*  655 */     if ((win != null) && (System.currentTimeMillis() - win.setMaxBoundsTime > 500L)) {
/*  656 */       win.setMaximizedBounds(win.maxWindowBounds);
/*  657 */       this.maxBoundsReset = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static double getLowerZoomLevel(double currentMag)
/*      */   {
/*  667 */     double newMag = zoomLevels[0];
/*  668 */     for (int i = 0; (i < zoomLevels.length) && 
/*  669 */       (zoomLevels[i] < currentMag); i++)
/*      */     {
/*  670 */       newMag = zoomLevels[i];
/*      */     }
/*      */ 
/*  674 */     return newMag;
/*      */   }
/*      */ 
/*      */   public static double getHigherZoomLevel(double currentMag) {
/*  678 */     double newMag = 32.0D;
/*  679 */     for (int i = zoomLevels.length - 1; (i >= 0) && 
/*  680 */       (zoomLevels[i] > currentMag); i--)
/*      */     {
/*  681 */       newMag = zoomLevels[i];
/*      */     }
/*      */ 
/*  685 */     return newMag;
/*      */   }
/*      */ 
/*      */   public void zoomIn(int sx, int sy)
/*      */   {
/*  693 */     if (this.magnification >= 32.0D) return;
/*  694 */     double newMag = getHigherZoomLevel(this.magnification);
/*  695 */     int newWidth = (int)(this.imageWidth * newMag);
/*  696 */     int newHeight = (int)(this.imageHeight * newMag);
/*  697 */     Dimension newSize = canEnlarge(newWidth, newHeight);
/*  698 */     if (newSize != null) {
/*  699 */       setDrawingSize(newSize.width, newSize.height);
/*  700 */       if ((newSize.width != newWidth) || (newSize.height != newHeight))
/*  701 */         adjustSourceRect(newMag, sx, sy);
/*      */       else
/*  703 */         setMagnification(newMag);
/*  704 */       this.imp.getWindow().pack();
/*      */     } else {
/*  706 */       adjustSourceRect(newMag, sx, sy);
/*  707 */     }repaint();
/*  708 */     if ((this.srcRect.width < this.imageWidth) || (this.srcRect.height < this.imageHeight))
/*  709 */       resetMaxBounds();
/*      */   }
/*      */ 
/*      */   void adjustSourceRect(double newMag, int x, int y)
/*      */   {
/*  714 */     int w = (int)Math.round(this.dstWidth / newMag);
/*  715 */     if (w * newMag < this.dstWidth) w++;
/*  716 */     int h = (int)Math.round(this.dstHeight / newMag);
/*  717 */     if (h * newMag < this.dstHeight) h++;
/*  718 */     x = offScreenX(x);
/*  719 */     y = offScreenY(y);
/*  720 */     Rectangle r = new Rectangle(x - w / 2, y - h / 2, w, h);
/*  721 */     if (r.x < 0) r.x = 0;
/*  722 */     if (r.y < 0) r.y = 0;
/*  723 */     if (r.x + w > this.imageWidth) r.x = (this.imageWidth - w);
/*  724 */     if (r.y + h > this.imageHeight) r.y = (this.imageHeight - h);
/*  725 */     this.srcRect = r;
/*  726 */     setMagnification(newMag);
/*      */   }
/*      */ 
/*      */   protected Dimension canEnlarge(int newWidth, int newHeight)
/*      */   {
/*  732 */     ImageWindow win = this.imp.getWindow();
/*  733 */     if (win == null) return null;
/*  734 */     Rectangle r1 = win.getBounds();
/*  735 */     Insets insets = win.getInsets();
/*  736 */     Point loc = getLocation();
/*  737 */     if ((loc.x > insets.left + 5) || (loc.y > insets.top + 5)) {
/*  738 */       r1.width = (newWidth + insets.left + insets.right + 10);
/*  739 */       r1.height = (newHeight + insets.top + insets.bottom + 10);
/*  740 */       if ((win instanceof StackWindow)) r1.height += 20; 
/*      */     }
/*  742 */     else { r1.width = (r1.width - this.dstWidth + newWidth + 10);
/*  743 */       r1.height = (r1.height - this.dstHeight + newHeight + 10);
/*      */     }
/*  745 */     Rectangle max = win.getMaxWindow(r1.x, r1.y);
/*  746 */     boolean fitsHorizontally = r1.x + r1.width < max.x + max.width;
/*  747 */     boolean fitsVertically = r1.y + r1.height < max.y + max.height;
/*  748 */     if ((fitsHorizontally) && (fitsVertically))
/*  749 */       return new Dimension(newWidth, newHeight);
/*  750 */     if ((fitsVertically) && (newHeight < this.dstWidth))
/*  751 */       return new Dimension(this.dstWidth, newHeight);
/*  752 */     if ((fitsHorizontally) && (newWidth < this.dstHeight)) {
/*  753 */       return new Dimension(newWidth, this.dstHeight);
/*      */     }
/*  755 */     return null;
/*      */   }
/*      */ 
/*      */   public void zoomOut(int x, int y)
/*      */   {
/*  762 */     if (this.magnification <= 0.03125D)
/*  763 */       return;
/*  764 */     double oldMag = this.magnification;
/*  765 */     double newMag = getLowerZoomLevel(this.magnification);
/*  766 */     double srcRatio = this.srcRect.width / this.srcRect.height;
/*  767 */     double imageRatio = this.imageWidth / this.imageHeight;
/*  768 */     double initialMag = this.imp.getWindow().getInitialMagnification();
/*  769 */     if (Math.abs(srcRatio - imageRatio) > 0.05D) {
/*  770 */       double scale = oldMag / newMag;
/*  771 */       int newSrcWidth = (int)Math.round(this.srcRect.width * scale);
/*  772 */       int newSrcHeight = (int)Math.round(this.srcRect.height * scale);
/*  773 */       if (newSrcWidth > this.imageWidth) newSrcWidth = this.imageWidth;
/*  774 */       if (newSrcHeight > this.imageHeight) newSrcHeight = this.imageHeight;
/*  775 */       int newSrcX = this.srcRect.x - (newSrcWidth - this.srcRect.width) / 2;
/*  776 */       int newSrcY = this.srcRect.y - (newSrcHeight - this.srcRect.height) / 2;
/*  777 */       if (newSrcX < 0) newSrcX = 0;
/*  778 */       if (newSrcY < 0) newSrcY = 0;
/*  779 */       this.srcRect = new Rectangle(newSrcX, newSrcY, newSrcWidth, newSrcHeight);
/*      */ 
/*  781 */       int newDstWidth = (int)(this.srcRect.width * newMag);
/*  782 */       int newDstHeight = (int)(this.srcRect.height * newMag);
/*  783 */       setMagnification(newMag);
/*  784 */       setMaxBounds();
/*      */ 
/*  786 */       if ((newDstWidth < this.dstWidth) || (newDstHeight < this.dstHeight))
/*      */       {
/*  788 */         setDrawingSize(newDstWidth, newDstHeight);
/*  789 */         this.imp.getWindow().pack();
/*      */       } else {
/*  791 */         repaint();
/*  792 */       }return;
/*      */     }
/*  794 */     if (this.imageWidth * newMag > this.dstWidth) {
/*  795 */       int w = (int)Math.round(this.dstWidth / newMag);
/*  796 */       if (w * newMag < this.dstWidth) w++;
/*  797 */       int h = (int)Math.round(this.dstHeight / newMag);
/*  798 */       if (h * newMag < this.dstHeight) h++;
/*  799 */       x = offScreenX(x);
/*  800 */       y = offScreenY(y);
/*  801 */       Rectangle r = new Rectangle(x - w / 2, y - h / 2, w, h);
/*  802 */       if (r.x < 0) r.x = 0;
/*  803 */       if (r.y < 0) r.y = 0;
/*  804 */       if (r.x + w > this.imageWidth) r.x = (this.imageWidth - w);
/*  805 */       if (r.y + h > this.imageHeight) r.y = (this.imageHeight - h);
/*  806 */       this.srcRect = r;
/*      */     } else {
/*  808 */       this.srcRect = new Rectangle(0, 0, this.imageWidth, this.imageHeight);
/*  809 */       setDrawingSize((int)(this.imageWidth * newMag), (int)(this.imageHeight * newMag));
/*      */ 
/*  811 */       this.imp.getWindow().pack();
/*      */     }
/*      */ 
/*  814 */     setMagnification(newMag);
/*      */ 
/*  816 */     setMaxBounds();
/*  817 */     repaint();
/*      */   }
/*      */ 
/*      */   public void unzoom()
/*      */   {
/*  822 */     double imag = this.imp.getWindow().getInitialMagnification();
/*  823 */     if (this.magnification == imag)
/*  824 */       return;
/*  825 */     this.srcRect = new Rectangle(0, 0, this.imageWidth, this.imageHeight);
/*  826 */     ImageWindow win = this.imp.getWindow();
/*  827 */     setDrawingSize((int)(this.imageWidth * imag), (int)(this.imageHeight * imag));
/*  828 */     setMagnification(imag);
/*  829 */     setMaxBounds();
/*  830 */     win.pack();
/*  831 */     setMaxBounds();
/*  832 */     repaint();
/*      */   }
/*      */ 
/*      */   public void zoom100Percent()
/*      */   {
/*  837 */     if (this.magnification == 1.0D)
/*  838 */       return;
/*  839 */     double imag = this.imp.getWindow().getInitialMagnification();
/*  840 */     if (this.magnification != imag)
/*  841 */       unzoom();
/*  842 */     if (this.magnification == 1.0D)
/*  843 */       return;
/*  844 */     if (this.magnification < 1.0D)
/*  845 */       while (this.magnification < 1.0D)
/*  846 */         zoomIn(this.imageWidth / 2, this.imageHeight / 2);
/*  847 */     if (this.magnification > 1.0D) {
/*  848 */       while (this.magnification > 1.0D)
/*  849 */         zoomOut(this.imageWidth / 2, this.imageHeight / 2);
/*      */     }
/*  851 */     return;
/*  852 */     int x = this.xMouse; int y = this.yMouse;
/*  853 */     if (this.mouseExited) {
/*  854 */       x = this.imageWidth / 2;
/*  855 */       y = this.imageHeight / 2;
/*      */     }
/*  857 */     int sx = screenX(x);
/*  858 */     int sy = screenY(y);
/*  859 */     adjustSourceRect(1.0D, sx, sy);
/*  860 */     repaint();
/*      */   }
/*      */ 
/*      */   protected void scroll(int sx, int sy) {
/*  864 */     int ox = this.xSrcStart + (int)(sx / this.magnification);
/*  865 */     int oy = this.ySrcStart + (int)(sy / this.magnification);
/*      */ 
/*  867 */     int newx = this.xSrcStart + (this.xMouseStart - ox);
/*  868 */     int newy = this.ySrcStart + (this.yMouseStart - oy);
/*  869 */     if (newx < 0) newx = 0;
/*  870 */     if (newy < 0) newy = 0;
/*  871 */     if (newx + this.srcRect.width > this.imageWidth) newx = this.imageWidth - this.srcRect.width;
/*  872 */     if (newy + this.srcRect.height > this.imageHeight) newy = this.imageHeight - this.srcRect.height;
/*  873 */     this.srcRect.x = newx;
/*  874 */     this.srcRect.y = newy;
/*      */ 
/*  876 */     this.imp.draw();
/*  877 */     Thread.yield();
/*      */   }
/*      */ 
/*      */   Color getColor(int index) {
/*  881 */     IndexColorModel cm = (IndexColorModel)this.imp.getProcessor().getColorModel();
/*      */ 
/*  883 */     return new Color(cm.getRGB(index));
/*      */   }
/*      */ 
/*      */   protected void setDrawingColor(int ox, int oy, boolean setBackground)
/*      */   {
/*  888 */     int type = this.imp.getType();
/*  889 */     int[] v = this.imp.getPixel(ox, oy);
/*  890 */     switch (type) {
/*      */     case 0:
/*  892 */       if (setBackground)
/*  893 */         setBackgroundColor(getColor(v[0]));
/*      */       else
/*  895 */         setForegroundColor(getColor(v[0]));
/*  896 */       break;
/*      */     case 1:
/*      */     case 2:
/*  899 */       double min = this.imp.getProcessor().getMin();
/*  900 */       double max = this.imp.getProcessor().getMax();
/*  901 */       double value = type == 2 ? Float.intBitsToFloat(v[0]) : v[0];
/*  902 */       int index = (int)(255.0D * ((value - min) / (max - min)));
/*  903 */       if (index < 0) index = 0;
/*  904 */       if (index > 255) index = 255;
/*  905 */       if (setBackground)
/*  906 */         setBackgroundColor(getColor(index));
/*      */       else
/*  908 */         setForegroundColor(getColor(index));
/*  909 */       break;
/*      */     case 3:
/*      */     case 4:
/*  912 */       Color c = new Color(v[0], v[1], v[2]);
/*  913 */       if (setBackground)
/*  914 */         setBackgroundColor(c);
/*      */       else
/*  916 */         setForegroundColor(c);
/*  917 */       break;
/*      */     }
/*      */     Color c;
/*      */     Color c;
/*  921 */     if (setBackground) {
/*  922 */       c = Toolbar.getBackgroundColor();
/*      */     } else {
/*  924 */       c = Toolbar.getForegroundColor();
/*  925 */       this.imp.setColor(c);
/*      */     }
/*  927 */     IJ.showStatus("(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")");
/*      */   }
/*      */ 
/*      */   private void setForegroundColor(Color c) {
/*  931 */     Toolbar.setForegroundColor(c);
/*  932 */     if (Recorder.record)
/*  933 */       Recorder.record("setForegroundColor", c.getRed(), c.getGreen(), c.getBlue());
/*      */   }
/*      */ 
/*      */   private void setBackgroundColor(Color c) {
/*  937 */     Toolbar.setBackgroundColor(c);
/*  938 */     if (Recorder.record)
/*  939 */       Recorder.record("setBackgroundColor", c.getRed(), c.getGreen(), c.getBlue());
/*      */   }
/*      */ 
/*      */   public void mousePressed(MouseEvent e)
/*      */   {
/*  944 */     this.showCursorStatus = true;
/*  945 */     int toolID = Toolbar.getToolId();
/*  946 */     ImageWindow win = this.imp.getWindow();
/*  947 */     if ((win != null) && (win.running2) && (toolID != 11)) {
/*  948 */       if ((win instanceof StackWindow))
/*  949 */         ((StackWindow)win).setAnimate(false);
/*      */       else
/*  951 */         win.running2 = false;
/*  952 */       return;
/*      */     }
/*      */ 
/*  955 */     int x = e.getX();
/*  956 */     int y = e.getY();
/*  957 */     this.flags = e.getModifiers();
/*      */ 
/*  960 */     if ((toolID != 11) && ((e.isPopupTrigger()) || ((!IJ.isMacintosh()) && ((this.flags & 0x4) != 0)))) {
/*  961 */       handlePopupMenu(e);
/*  962 */       return;
/*      */     }
/*      */ 
/*  965 */     int ox = offScreenX(x);
/*  966 */     int oy = offScreenY(y);
/*  967 */     this.xMouse = ox; this.yMouse = oy;
/*  968 */     if (IJ.spaceBarDown())
/*      */     {
/*  970 */       setupScroll(ox, oy);
/*  971 */       return;
/*      */     }
/*  973 */     if (this.showAllROIs) {
/*  974 */       Roi roi = this.imp.getRoi();
/*  975 */       if (((roi == null) || ((!roi.contains(ox, oy)) && (roi.isHandle(x, y) < 0))) && (roiManagerSelect(x, y)))
/*  976 */         return;
/*      */     }
/*  978 */     if ((this.customRoi) && (this.overlay != null)) {
/*  979 */       return;
/*      */     }
/*  981 */     switch (toolID) {
/*      */     case 11:
/*  983 */       if (IJ.shiftKeyDown()) {
/*  984 */         zoomToSelection(ox, oy);
/*  985 */       } else if ((this.flags & 0xE) != 0)
/*      */       {
/*  987 */         zoomOut(x, y);
/*  988 */         if (getMagnification() < 1.0D)
/*  989 */           this.imp.repaintWindow();
/*      */       }
/*      */       else {
/*  992 */         zoomIn(x, y);
/*  993 */         if (getMagnification() <= 1.0D)
/*  994 */           this.imp.repaintWindow();  } break;
/*      */     case 12:
/*  998 */       setupScroll(ox, oy);
/*  999 */       break;
/*      */     case 13:
/* 1001 */       setDrawingColor(ox, oy, IJ.altKeyDown());
/* 1002 */       break;
/*      */     case 8:
/* 1004 */       Roi roi = this.imp.getRoi();
/* 1005 */       if ((roi != null) && (roi.contains(ox, oy))) {
/* 1006 */         Rectangle r = roi.getBounds();
/* 1007 */         if ((r.width == this.imageWidth) && (r.height == this.imageHeight)) {
/* 1008 */           this.imp.killRoi();
/* 1009 */         } else if (!e.isAltDown()) {
/* 1010 */           handleRoiMouseDown(e);
/* 1011 */           return;
/*      */         }
/*      */       }
/* 1014 */       if (roi != null) {
/* 1015 */         int handle = roi.isHandle(x, y);
/* 1016 */         if (handle >= 0) {
/* 1017 */           roi.mouseDownInHandle(handle, x, y);
/* 1018 */           return;
/*      */         }
/*      */       }
/* 1021 */       setRoiModState(e, roi, -1);
/* 1022 */       String mode = WandToolOptions.getMode();
/* 1023 */       double tolerance = WandToolOptions.getTolerance();
/* 1024 */       int npoints = IJ.doWand(ox, oy, tolerance, mode);
/* 1025 */       if ((Recorder.record) && (npoints > 0))
/* 1026 */         if ((tolerance == 0.0D) && (mode.equals("Legacy")))
/* 1027 */           Recorder.record("doWand", ox, oy);
/*      */         else
/* 1029 */           Recorder.recordString("doWand(" + ox + ", " + oy + ", " + tolerance + ", \"" + mode + "\");\n"); 
/* 1029 */       break;
/*      */     case 1:
/* 1033 */       if (Toolbar.getBrushSize() > 0)
/* 1034 */         new RoiBrush();
/*      */       else
/* 1036 */         handleRoiMouseDown(e);
/* 1037 */       break;
/*      */     case 10:
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/*      */     case 21:
/*      */     case 22:
/* 1041 */       Toolbar.getInstance().runMacroTool(toolID);
/* 1042 */       break;
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 9:
/*      */     case 14:
/*      */     default:
/* 1044 */       handleRoiMouseDown(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean roiManagerSelect(int x, int y) {
/* 1049 */     RoiManager rm = RoiManager.getInstance();
/* 1050 */     if (rm == null) return false;
/* 1051 */     Hashtable rois = rm.getROIs();
/* 1052 */     List list = rm.getList();
/* 1053 */     int n = list.getItemCount();
/* 1054 */     if ((this.labelRects == null) || (this.labelRects.length != n)) return false;
/* 1055 */     boolean stackMode = (this.imp != null) && (this.imp.getStackSize() > 1) && (Prefs.showAllSliceOnly);
/* 1056 */     for (int i = 0; i < n; i++) {
/* 1057 */       if ((this.labelRects[i] != null) && (this.labelRects[i].contains(x, y)))
/* 1058 */         if (stackMode)
/*      */         {
/* 1059 */           int slice = getSliceNumber(list.getItem(i));
/* 1060 */           if ((slice != this.imp.getCurrentSlice()) && (slice != 0));
/*      */         }
/*      */         else
/*      */         {
/* 1066 */           new MacroRunner("roiManager('select', " + i + "); roiManager('update2');");
/* 1067 */           return true;
/*      */         }
/*      */     }
/* 1070 */     return false;
/*      */   }
/*      */ 
/*      */   void zoomToSelection(int x, int y) {
/* 1074 */     IJ.setKeyUp(-1);
/* 1075 */     String macro = "args = split(getArgument);\nx1=parseInt(args[0]); y1=parseInt(args[1]); flags=20;\nwhile (flags&20!=0) {\ngetCursorLoc(x2, y2, z, flags);\nif (x2>=x1) x=x1; else x=x2;\nif (y2>=y1) y=y1; else y=y2;\nmakeRectangle(x, y, abs(x2-x1), abs(y2-y1));\nwait(10);\n}\nrun('To Selection');\n";
/*      */ 
/* 1086 */     new MacroRunner(macro, x + " " + y);
/*      */   }
/*      */ 
/*      */   protected void setupScroll(int ox, int oy) {
/* 1090 */     this.xMouseStart = ox;
/* 1091 */     this.yMouseStart = oy;
/* 1092 */     this.xSrcStart = this.srcRect.x;
/* 1093 */     this.ySrcStart = this.srcRect.y;
/*      */   }
/*      */ 
/*      */   protected void handlePopupMenu(MouseEvent e) {
/* 1097 */     if (this.disablePopupMenu) return;
/* 1098 */     if (IJ.debugMode) IJ.log("show popup: " + (e.isPopupTrigger() ? "true" : "false"));
/* 1099 */     int x = e.getX();
/* 1100 */     int y = e.getY();
/* 1101 */     Roi roi = this.imp.getRoi();
/* 1102 */     if ((roi != null) && ((roi.getType() == 2) || (roi.getType() == 6) || (roi.getType() == 8)) && (roi.getState() == 0))
/*      */     {
/* 1104 */       roi.handleMouseUp(x, y);
/* 1105 */       roi.handleMouseUp(x, y);
/* 1106 */       return;
/*      */     }
/* 1108 */     PopupMenu popup = Menus.getPopupMenu();
/* 1109 */     if (popup != null) {
/* 1110 */       add(popup);
/* 1111 */       if (IJ.isMacOSX()) IJ.wait(10);
/* 1112 */       popup.show(this, x, y);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void mouseExited(MouseEvent e)
/*      */   {
/* 1118 */     ImageWindow win = this.imp.getWindow();
/* 1119 */     if (win != null)
/* 1120 */       setCursor(defaultCursor);
/* 1121 */     IJ.showStatus("");
/* 1122 */     this.mouseExited = true;
/*      */   }
/*      */ 
/*      */   public void mouseDragged(MouseEvent e)
/*      */   {
/* 1152 */     int x = e.getX();
/* 1153 */     int y = e.getY();
/* 1154 */     this.xMouse = offScreenX(x);
/* 1155 */     this.yMouse = offScreenY(y);
/* 1156 */     this.flags = e.getModifiers();
/*      */ 
/* 1158 */     if (this.flags == 0)
/* 1159 */       this.flags = 16;
/* 1160 */     if ((Toolbar.getToolId() == 12) || (IJ.spaceBarDown())) {
/* 1161 */       scroll(x, y);
/*      */     } else {
/* 1163 */       IJ.setInputEvent(e);
/* 1164 */       Roi roi = this.imp.getRoi();
/* 1165 */       if (roi != null)
/* 1166 */         roi.handleMouseDrag(x, y, this.flags);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void handleRoiMouseDown(MouseEvent e) {
/* 1171 */     int sx = e.getX();
/* 1172 */     int sy = e.getY();
/* 1173 */     int ox = offScreenX(sx);
/* 1174 */     int oy = offScreenY(sy);
/* 1175 */     Roi roi = this.imp.getRoi();
/* 1176 */     int handle = roi != null ? roi.isHandle(sx, sy) : -1;
/* 1177 */     boolean multiPointMode = (roi != null) && ((roi instanceof PointRoi)) && (handle == -1) && (Toolbar.getToolId() == 7) && (Toolbar.getMultiPointMode());
/*      */ 
/* 1179 */     if (multiPointMode) {
/* 1180 */       this.imp.setRoi(((PointRoi)roi).addPoint(ox, oy));
/* 1181 */       return;
/*      */     }
/* 1183 */     setRoiModState(e, roi, handle);
/* 1184 */     if (roi != null) {
/* 1185 */       if (handle >= 0) {
/* 1186 */         roi.mouseDownInHandle(handle, sx, sy);
/* 1187 */         return;
/*      */       }
/* 1189 */       Rectangle r = roi.getBounds();
/* 1190 */       int type = roi.getType();
/* 1191 */       if ((type == 0) && (r.width == this.imp.getWidth()) && (r.height == this.imp.getHeight()) && (roi.getPasteMode() == -1) && (!(roi instanceof ImageRoi)))
/*      */       {
/* 1193 */         this.imp.killRoi();
/* 1194 */         return;
/*      */       }
/* 1196 */       if (roi.contains(ox, oy)) {
/* 1197 */         if (roi.modState == 0) {
/* 1198 */           roi.handleMouseDown(sx, sy);
/*      */         } else {
/* 1200 */           this.imp.killRoi();
/* 1201 */           this.imp.createNewRoi(sx, sy);
/*      */         }
/* 1203 */         return;
/*      */       }
/* 1205 */       if (((type == 2) || (type == 6) || (type == 8)) && (roi.getState() == 0))
/*      */       {
/* 1207 */         return;
/* 1208 */       }int tool = Toolbar.getToolId();
/* 1209 */       if (((tool == 2) || (tool == 5) || (tool == 14)) && (!IJ.shiftKeyDown()) && (!IJ.altKeyDown())) {
/* 1210 */         this.imp.killRoi();
/* 1211 */         return;
/*      */       }
/*      */     }
/* 1214 */     this.imp.createNewRoi(sx, sy);
/*      */   }
/*      */ 
/*      */   void setRoiModState(MouseEvent e, Roi roi, int handle) {
/* 1218 */     if ((roi == null) || ((handle >= 0) && (roi.modState == 0)))
/* 1219 */       return;
/* 1220 */     if (roi.state == 0)
/* 1221 */       return;
/* 1222 */     int tool = Toolbar.getToolId();
/* 1223 */     if ((tool > 3) && (tool != 8) && (tool != 7)) {
/* 1224 */       roi.modState = 0; return;
/* 1225 */     }if (e.isShiftDown())
/* 1226 */       roi.modState = 1;
/* 1227 */     else if (e.isAltDown())
/* 1228 */       roi.modState = 2;
/*      */     else
/* 1230 */       roi.modState = 0;
/*      */   }
/*      */ 
/*      */   public void disablePopupMenu(boolean status)
/*      */   {
/* 1236 */     this.disablePopupMenu = status;
/*      */   }
/*      */ 
/*      */   public void setShowAllROIs(boolean showAllROIs)
/*      */   {
/* 1241 */     this.showAllROIs = showAllROIs;
/*      */   }
/*      */ 
/*      */   public boolean getShowAllROIs()
/*      */   {
/* 1246 */     return this.showAllROIs;
/*      */   }
/*      */ 
/*      */   public Overlay getShowAllList()
/*      */   {
/* 1251 */     if (!this.showAllROIs) return null;
/* 1252 */     if (this.showAllList != null) return this.showAllList;
/* 1253 */     RoiManager rm = RoiManager.getInstance();
/* 1254 */     if (rm == null) return null;
/* 1255 */     Roi[] rois = rm.getRoisAsArray();
/* 1256 */     if (rois.length == 0) return null;
/* 1257 */     Overlay overlay = new Overlay();
/* 1258 */     for (int i = 0; i < rois.length; i++)
/* 1259 */       overlay.add((Roi)rois[i].clone());
/* 1260 */     return overlay;
/*      */   }
/*      */ 
/*      */   public static Color getShowAllColor()
/*      */   {
/* 1265 */     if ((showAllColor != null) && (showAllColor.getRGB() == -8323073))
/* 1266 */       showAllColor = Color.cyan;
/* 1267 */     return showAllColor;
/*      */   }
/*      */ 
/*      */   public static void setShowAllColor(Color c)
/*      */   {
/* 1272 */     if (c == null) return;
/* 1273 */     showAllColor = c;
/* 1274 */     labelColor = null;
/* 1275 */     ImagePlus img = WindowManager.getCurrentImage();
/* 1276 */     if (img != null) {
/* 1277 */       ImageCanvas ic = img.getCanvas();
/* 1278 */       if ((ic != null) && (ic.getShowAllROIs())) img.draw();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setOverlay(Overlay overlay)
/*      */   {
/* 1284 */     this.overlay = overlay;
/* 1285 */     repaint();
/*      */   }
/*      */ 
/*      */   public Overlay getOverlay()
/*      */   {
/* 1290 */     return this.overlay;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setDisplayList(Vector list)
/*      */   {
/* 1298 */     if (list != null) {
/* 1299 */       Overlay list2 = new Overlay();
/* 1300 */       list2.setVector(list);
/* 1301 */       setOverlay(list2);
/*      */     } else {
/* 1303 */       setOverlay(null);
/* 1304 */     }if (this.overlay != null)
/* 1305 */       this.overlay.drawLabels((this.overlay.size() > 0) && (this.overlay.get(0).getStrokeColor() == null));
/*      */     else
/* 1307 */       this.customRoi = false;
/* 1308 */     repaint();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setDisplayList(Shape shape, Color color, BasicStroke stroke)
/*      */   {
/* 1316 */     if (shape == null) {
/* 1317 */       setOverlay(null); return;
/* 1318 */     }Roi roi = new ShapeRoi(shape);
/* 1319 */     roi.setStrokeColor(color);
/* 1320 */     roi.setStroke(stroke);
/* 1321 */     Overlay list = new Overlay();
/* 1322 */     list.add(roi);
/* 1323 */     setOverlay(list);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setDisplayList(Roi roi, Color color)
/*      */   {
/* 1331 */     roi.setStrokeColor(color);
/* 1332 */     Overlay list = new Overlay();
/* 1333 */     list.add(roi);
/* 1334 */     setOverlay(list);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Vector getDisplayList()
/*      */   {
/* 1342 */     if (this.overlay == null) return null;
/* 1343 */     Vector displayList = new Vector();
/* 1344 */     for (int i = 0; i < this.overlay.size(); i++)
/* 1345 */       displayList.add(this.overlay.get(i));
/* 1346 */     return displayList;
/*      */   }
/*      */ 
/*      */   public void setCustomRoi(boolean customRoi)
/*      */   {
/* 1351 */     this.customRoi = customRoi;
/*      */   }
/*      */ 
/*      */   public boolean getCustomRoi() {
/* 1355 */     return this.customRoi;
/*      */   }
/*      */ 
/*      */   public void setShowCursorStatus(boolean status)
/*      */   {
/* 1361 */     this.showCursorStatus = status;
/* 1362 */     if (status == true) {
/* 1363 */       this.sx2 = (this.sy2 = -1000);
/*      */     } else {
/* 1365 */       this.sx2 = screenX(this.xMouse);
/* 1366 */       this.sy2 = screenY(this.yMouse);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void mouseReleased(MouseEvent e) {
/* 1371 */     this.flags = e.getModifiers();
/* 1372 */     this.flags &= -17;
/* 1373 */     this.flags &= -9;
/* 1374 */     this.flags &= -5;
/* 1375 */     Roi roi = this.imp.getRoi();
/* 1376 */     if (roi != null) {
/* 1377 */       Rectangle r = roi.getBounds();
/* 1378 */       int type = roi.getType();
/* 1379 */       if (((r.width == 0) || (r.height == 0)) && (type != 2) && (type != 6) && (type != 8) && (type != 5) && (!(roi instanceof TextRoi)) && (roi.getState() == 0) && (type != 10))
/*      */       {
/* 1384 */         this.imp.killRoi();
/*      */       } else {
/* 1386 */         roi.handleMouseUp(e.getX(), e.getY());
/* 1387 */         if ((roi.getType() == 5) && (roi.getLength() == 0.0D))
/* 1388 */           this.imp.killRoi();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void mouseMoved(MouseEvent e)
/*      */   {
/* 1395 */     int sx = e.getX();
/* 1396 */     int sy = e.getY();
/* 1397 */     int ox = offScreenX(sx);
/* 1398 */     int oy = offScreenY(sy);
/* 1399 */     this.flags = e.getModifiers();
/* 1400 */     setCursor(sx, sy, ox, oy);
/* 1401 */     IJ.setInputEvent(e);
/* 1402 */     Roi roi = this.imp.getRoi();
/* 1403 */     if ((roi != null) && ((roi.getType() == 2) || (roi.getType() == 6) || (roi.getType() == 8)) && (roi.getState() == 0))
/*      */     {
/* 1405 */       PolygonRoi pRoi = (PolygonRoi)roi;
/* 1406 */       pRoi.handleMouseMove(ox, oy);
/*      */     }
/* 1408 */     else if ((ox < this.imageWidth) && (oy < this.imageHeight)) {
/* 1409 */       ImageWindow win = this.imp.getWindow();
/*      */ 
/* 1412 */       if ((sx - this.sx2) * (sx - this.sx2) + (sy - this.sy2) * (sy - this.sy2) > 144)
/* 1413 */         this.showCursorStatus = true;
/* 1414 */       if ((win != null) && (this.showCursorStatus)) win.mouseMoved(ox, oy); 
/*      */     }
/* 1416 */     else { IJ.showStatus(""); }
/*      */ 
/*      */   }
/*      */ 
/*      */   public void mouseClicked(MouseEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void mouseEntered(MouseEvent e)
/*      */   {
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.ImageCanvas
 * JD-Core Version:    0.6.2
 */