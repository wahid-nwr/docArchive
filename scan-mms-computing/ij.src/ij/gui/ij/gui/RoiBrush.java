/*    */ package ij.gui;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.WindowManager;
/*    */ import java.awt.Point;
/*    */ import java.awt.Polygon;
/*    */ 
/*    */ class RoiBrush
/*    */   implements Runnable
/*    */ {
/*  7 */   static int ADD = 0; static int SUBTRACT = 1;
/*  8 */   static int leftClick = 16; static int alt = 9; static int shift = 1;
/*    */   private Polygon poly;
/*    */   private Point previousP;
/* 11 */   private int mode = ADD;
/*    */ 
/*    */   RoiBrush() {
/* 14 */     Thread thread = new Thread(this, "RoiBrush");
/* 15 */     thread.start();
/*    */   }
/*    */ 
/*    */   public void run() {
/* 19 */     int size = Toolbar.getBrushSize();
/* 20 */     ImagePlus img = WindowManager.getCurrentImage();
/* 21 */     if (img == null) return;
/* 22 */     ImageCanvas ic = img.getCanvas();
/* 23 */     if (ic == null) return;
/* 24 */     Roi roi = img.getRoi();
/* 25 */     if ((roi != null) && (!roi.isArea()))
/* 26 */       img.killRoi();
/* 27 */     Point p = ic.getCursorLoc();
/* 28 */     if ((roi != null) && (!roi.contains(p.x, p.y)))
/* 29 */       this.mode = SUBTRACT;
/*    */     while (true)
/*    */     {
/* 32 */       p = ic.getCursorLoc();
/* 33 */       if (p.equals(this.previousP)) {
/* 34 */         IJ.wait(1); } else {
/* 35 */         this.previousP = p;
/* 36 */         int flags = ic.getModifiers();
/* 37 */         if ((flags & leftClick) == 0) return;
/* 38 */         if ((flags & shift) != 0)
/* 39 */           this.mode = ADD;
/* 40 */         else if ((flags & alt) != 0)
/* 41 */           this.mode = SUBTRACT;
/* 42 */         if (this.mode == ADD)
/* 43 */           addCircle(img, p.x, p.y, size);
/*    */         else
/* 45 */           subtractCircle(img, p.x, p.y, size); 
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/* 50 */   void addCircle(ImagePlus img, int x, int y, int width) { Roi roi = img.getRoi();
/* 51 */     Roi roi2 = roi;
/* 52 */     if (roi2 != null) {
/* 53 */       if (!(roi2 instanceof ShapeRoi))
/* 54 */         roi2 = new ShapeRoi(roi2);
/* 55 */       ((ShapeRoi)roi2).or(getCircularRoi(x, y, width));
/* 56 */       roi2.copyAttributes(roi);
/*    */     } else {
/* 58 */       roi2 = new OvalRoi(x - width / 2, y - width / 2, width, width);
/* 59 */     }img.setRoi(roi2); }
/*    */ 
/*    */   void subtractCircle(ImagePlus img, int x, int y, int width)
/*    */   {
/* 63 */     Roi roi = img.getRoi();
/* 64 */     Roi roi2 = roi;
/* 65 */     if (roi2 != null) {
/* 66 */       if (!(roi2 instanceof ShapeRoi))
/* 67 */         roi2 = new ShapeRoi(roi2);
/* 68 */       ((ShapeRoi)roi2).not(getCircularRoi(x, y, width));
/* 69 */       roi2.copyAttributes(roi);
/* 70 */       img.setRoi(roi2);
/*    */     }
/*    */   }
/*    */ 
/*    */   ShapeRoi getCircularRoi(int x, int y, int width)
/*    */   {
/* 76 */     if (this.poly == null) {
/* 77 */       Roi roi = new OvalRoi(x - width / 2, y - width / 2, width, width);
/* 78 */       this.poly = roi.getPolygon();
/* 79 */       for (int i = 0; i < this.poly.npoints; i++) {
/* 80 */         this.poly.xpoints[i] -= x;
/* 81 */         this.poly.ypoints[i] -= y;
/*    */       }
/*    */     }
/* 84 */     return new ShapeRoi(x, y, this.poly);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.RoiBrush
 * JD-Core Version:    0.6.2
 */