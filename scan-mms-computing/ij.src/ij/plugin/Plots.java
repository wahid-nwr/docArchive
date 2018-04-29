/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ 
/*     */ class Plots extends ImagePlus
/*     */ {
/*     */   public void show()
/*     */   {
/* 468 */     this.img = this.ip.createImage();
/* 469 */     ImageCanvas ic = new PlotsCanvas(this);
/* 470 */     this.win = new ImageWindow(this, ic);
/* 471 */     IJ.showStatus("");
/* 472 */     if (ic.getMagnification() == 1.0D)
/* 473 */       return;
/* 474 */     while (ic.getMagnification() < 1.0D)
/* 475 */       ic.zoomIn(0, 0);
/* 476 */     Point loc = this.win.getLocation();
/* 477 */     int w = getWidth() + 20;
/* 478 */     int h = getHeight() + 30;
/* 479 */     Dimension screen = IJ.getScreenSize();
/* 480 */     if (loc.x + w > screen.width)
/* 481 */       w = screen.width - loc.x - 20;
/* 482 */     if (loc.y + h > screen.height)
/* 483 */       h = screen.height - loc.y - 30;
/* 484 */     this.win.setSize(w, h);
/* 485 */     this.win.validate();
/* 486 */     repaintWindow();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Plots
 * JD-Core Version:    0.6.2
 */