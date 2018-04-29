/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.ImageWindow;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ 
/*     */ public class WindowOrganizer
/*     */   implements PlugIn
/*     */ {
/*     */   private static final int XSTART = 4;
/*     */   private static final int YSTART = 80;
/*     */   private static final int XOFFSET = 8;
/*     */   private static final int YOFFSET = 24;
/*     */   private static final int MAXSTEP = 200;
/*     */   private static final int GAP = 2;
/*  11 */   private int titlebarHeight = IJ.isMacintosh() ? 40 : 20;
/*     */ 
/*     */   public void run(String arg) {
/*  14 */     int[] wList = WindowManager.getIDList();
/*  15 */     if (arg.equals("show")) {
/*  16 */       showAll(wList); return;
/*  17 */     }if (wList == null) {
/*  18 */       IJ.noImage();
/*  19 */       return;
/*     */     }
/*  21 */     if (arg.equals("tile"))
/*  22 */       tileWindows(wList);
/*     */     else
/*  24 */       cascadeWindows(wList);
/*     */   }
/*     */ 
/*     */   void tileWindows(int[] wList) {
/*  28 */     Dimension screen = IJ.getScreenSize();
/*  29 */     int minWidth = 2147483647;
/*  30 */     int minHeight = 2147483647;
/*  31 */     boolean allSameSize = true;
/*  32 */     int width = 0; int height = 0;
/*  33 */     double totalWidth = 0.0D;
/*  34 */     double totalHeight = 0.0D;
/*  35 */     for (int i = 0; i < wList.length; i++) {
/*  36 */       ImageWindow win = getWindow(wList[i]);
/*  37 */       if (win != null)
/*     */       {
/*  39 */         Dimension d = win.getSize();
/*  40 */         int w = d.width;
/*  41 */         int h = d.height + this.titlebarHeight;
/*  42 */         if (i == 0) {
/*  43 */           width = w;
/*  44 */           height = h;
/*     */         }
/*  46 */         if ((w != width) || (h != height))
/*  47 */           allSameSize = false;
/*  48 */         if (w < minWidth)
/*  49 */           minWidth = w;
/*  50 */         if (h < minHeight)
/*  51 */           minHeight = h;
/*  52 */         totalWidth += w;
/*  53 */         totalHeight += h;
/*     */       }
/*     */     }
/*  55 */     int nPics = wList.length;
/*  56 */     double averageWidth = totalWidth / nPics;
/*  57 */     double averageHeight = totalHeight / nPics;
/*  58 */     int tileWidth = (int)averageWidth;
/*  59 */     int tileHeight = (int)averageHeight;
/*     */ 
/*  61 */     int hspace = screen.width - 4;
/*  62 */     if (tileWidth > hspace)
/*  63 */       tileWidth = hspace;
/*  64 */     int vspace = screen.height - 80;
/*  65 */     if (tileHeight > vspace)
/*  66 */       tileHeight = vspace;
/*     */     boolean theyFit;
/*     */     do
/*     */     {
/*  70 */       hloc = 4;
/*  71 */       vloc = 80;
/*  72 */       theyFit = true;
/*  73 */       int i = 0;
/*     */       do {
/*  75 */         i++;
/*  76 */         if (hloc + tileWidth > screen.width) {
/*  77 */           hloc = 4;
/*  78 */           vloc += tileHeight;
/*  79 */           if (vloc + tileHeight > screen.height)
/*  80 */             theyFit = false;
/*     */         }
/*  82 */         hloc = hloc + tileWidth + 2;
/*  83 */       }while ((theyFit) && (i < nPics));
/*  84 */       if (!theyFit) {
/*  85 */         tileWidth = (int)(tileWidth * 0.98D + 0.5D);
/*  86 */         tileHeight = (int)(tileHeight * 0.98D + 0.5D);
/*     */       }
/*     */     }
/*  88 */     while (!theyFit);
/*  89 */     int nColumns = (screen.width - 4) / (tileWidth + 2);
/*  90 */     int nRows = nPics / nColumns;
/*  91 */     if (nPics % nColumns != 0)
/*  92 */       nRows++;
/*  93 */     int hloc = 4;
/*  94 */     int vloc = 80;
/*     */ 
/*  96 */     for (int i = 0; i < nPics; i++) {
/*  97 */       if (hloc + tileWidth > screen.width) {
/*  98 */         hloc = 4;
/*  99 */         vloc += tileHeight;
/*     */       }
/* 101 */       ImageWindow win = getWindow(wList[i]);
/* 102 */       if (win != null) {
/* 103 */         win.setLocation(hloc, vloc);
/*     */ 
/* 105 */         ImageCanvas canvas = win.getCanvas();
/* 106 */         while ((win.getSize().width * 0.85D >= tileWidth) && (canvas.getMagnification() > 0.03125D))
/* 107 */           canvas.zoomOut(0, 0);
/* 108 */         win.toFront();
/*     */       }
/* 110 */       hloc += tileWidth + 2;
/*     */     }
/*     */   }
/*     */ 
/*     */   ImageWindow getWindow(int id) {
/* 115 */     ImageWindow win = null;
/* 116 */     ImagePlus imp = WindowManager.getImage(id);
/* 117 */     if (imp != null)
/* 118 */       win = imp.getWindow();
/* 119 */     return win;
/*     */   }
/*     */ 
/*     */   void cascadeWindows(int[] wList) {
/* 123 */     Dimension screen = IJ.getScreenSize();
/* 124 */     int x = 4;
/* 125 */     int y = 80;
/* 126 */     int xstep = 0;
/* 127 */     int xstart = 4;
/* 128 */     for (int i = 0; i < wList.length; i++) {
/* 129 */       ImageWindow win = getWindow(wList[i]);
/* 130 */       if (win != null)
/*     */       {
/* 132 */         Dimension d = win.getSize();
/* 133 */         if (i == 0) {
/* 134 */           xstep = (int)(d.width * 0.8D);
/* 135 */           if (xstep > 200)
/* 136 */             xstep = 200;
/*     */         }
/* 138 */         if (y + d.height * 0.67D > screen.height) {
/* 139 */           xstart += xstep;
/* 140 */           if (xstart + d.width * 0.67D > screen.width)
/* 141 */             xstart = 12;
/* 142 */           x = xstart;
/* 143 */           y = 80;
/*     */         }
/* 145 */         win.setLocation(x, y);
/* 146 */         win.toFront();
/* 147 */         x += 8;
/* 148 */         y += 24;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 153 */   void showAll(int[] wList) { if (wList != null) {
/* 154 */       for (int i = 0; i < wList.length; i++) {
/* 155 */         ImageWindow win = getWindow(wList[i]);
/* 156 */         if (win != null) {
/* 157 */           WindowManager.toFront(win);
/*     */         }
/*     */       }
/*     */     }
/* 161 */     Frame[] frames = WindowManager.getNonImageWindows();
/* 162 */     if (frames != null) {
/* 163 */       for (int i = 0; i < frames.length; i++)
/* 164 */         WindowManager.toFront(frames[i]);
/*     */     }
/* 166 */     IJ.getInstance().toFront();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.WindowOrganizer
 * JD-Core Version:    0.6.2
 */