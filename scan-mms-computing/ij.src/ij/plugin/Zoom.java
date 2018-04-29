/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.Roi;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class Zoom
/*     */   implements PlugIn
/*     */ {
/*     */   public void run(String arg)
/*     */   {
/*  13 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  14 */     if (imp == null) {
/*  15 */       IJ.noImage(); return;
/*  16 */     }ImageCanvas ic = imp.getCanvas();
/*  17 */     if (ic == null) return;
/*  18 */     Point loc = ic.getCursorLoc();
/*  19 */     if (!ic.cursorOverImage()) {
/*  20 */       Rectangle srcRect = ic.getSrcRect();
/*  21 */       loc.x = (srcRect.x + srcRect.width / 2);
/*  22 */       loc.y = (srcRect.y + srcRect.height / 2);
/*     */     }
/*  24 */     int x = ic.screenX(loc.x);
/*  25 */     int y = ic.screenY(loc.y);
/*  26 */     if (arg.equals("in")) {
/*  27 */       ic.zoomIn(x, y);
/*  28 */       if (ic.getMagnification() <= 1.0D) imp.repaintWindow(); 
/*     */     }
/*  29 */     else if (arg.equals("out")) {
/*  30 */       ic.zoomOut(x, y);
/*  31 */       if (ic.getMagnification() < 1.0D) imp.repaintWindow(); 
/*     */     }
/*  32 */     else if (arg.equals("orig")) {
/*  33 */       ic.unzoom();
/*  34 */     } else if (arg.equals("100%")) {
/*  35 */       ic.zoom100Percent();
/*  36 */     } else if (arg.equals("to")) {
/*  37 */       zoomToSelection(imp, ic);
/*  38 */     } else if (arg.equals("set")) {
/*  39 */       setZoom(imp, ic);
/*  40 */     } else if (arg.equals("max")) {
/*  41 */       ImageWindow win = imp.getWindow();
/*  42 */       win.setBounds(win.getMaximumBounds());
/*  43 */       win.maximize();
/*     */     }
/*     */   }
/*     */ 
/*     */   void zoomToSelection(ImagePlus imp, ImageCanvas ic) {
/*  48 */     Roi roi = imp.getRoi();
/*  49 */     ic.unzoom();
/*  50 */     if (roi == null) return;
/*  51 */     Rectangle w = imp.getWindow().getBounds();
/*  52 */     Rectangle r = roi.getBounds();
/*  53 */     double mag = ic.getMagnification();
/*  54 */     int marginw = (int)(w.width - mag * imp.getWidth());
/*  55 */     int marginh = (int)(w.height - mag * imp.getHeight());
/*  56 */     int x = r.x + r.width / 2;
/*  57 */     int y = r.y + r.height / 2;
/*  58 */     mag = ImageCanvas.getHigherZoomLevel(mag);
/*  59 */     while ((r.width * mag < w.width - marginw) && (r.height * mag < w.height - marginh)) {
/*  60 */       ic.zoomIn(ic.screenX(x), ic.screenY(y));
/*  61 */       double cmag = ic.getMagnification();
/*  62 */       if (cmag == 32.0D) break;
/*  63 */       mag = ImageCanvas.getHigherZoomLevel(cmag);
/*  64 */       w = imp.getWindow().getBounds();
/*     */     }
/*     */   }
/*     */ 
/*     */   void setZoom(ImagePlus imp, ImageCanvas ic)
/*     */   {
/*  71 */     int x = imp.getWidth() / 2;
/*  72 */     int y = imp.getHeight() / 2;
/*  73 */     ImageWindow win = imp.getWindow();
/*  74 */     GenericDialog gd = new GenericDialog("Set Zoom");
/*  75 */     gd.addNumericField("Zoom:", ic.getMagnification() * 200.0D, 0, 4, "%");
/*  76 */     gd.addNumericField("X center:", x, 0, 5, "");
/*  77 */     gd.addNumericField("Y center:", y, 0, 5, "");
/*  78 */     gd.showDialog();
/*  79 */     if (gd.wasCanceled()) return;
/*  80 */     double mag = gd.getNextNumber() / 100.0D;
/*  81 */     x = (int)gd.getNextNumber();
/*  82 */     y = (int)gd.getNextNumber();
/*  83 */     if (x < 0) x = 0;
/*  84 */     if (y < 0) y = 0;
/*  85 */     if (x >= imp.getWidth()) x = imp.getWidth() - 1;
/*  86 */     if (y >= imp.getHeight()) y = imp.getHeight() - 1;
/*  87 */     if (mag <= 0.0D) mag = 1.0D;
/*  88 */     win.getCanvas().setMagnification(mag);
/*  89 */     double w = imp.getWidth() * mag;
/*  90 */     double h = imp.getHeight() * mag;
/*  91 */     Dimension screen = IJ.getScreenSize();
/*  92 */     if (w > screen.width - 20) w = screen.width - 20;
/*  93 */     if (h > screen.height - 50) h = screen.height - 50;
/*  94 */     int width = (int)(w / mag);
/*  95 */     int height = (int)(h / mag);
/*  96 */     x -= width / 2;
/*  97 */     y -= height / 2;
/*  98 */     if (x < 0) x = 0;
/*  99 */     if (y < 0) y = 0;
/* 100 */     ic.setSourceRect(new Rectangle(x, y, width, height));
/* 101 */     ic.setDrawingSize((int)w, (int)h);
/* 102 */     win.pack();
/* 103 */     ic.repaint();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Zoom
 * JD-Core Version:    0.6.2
 */