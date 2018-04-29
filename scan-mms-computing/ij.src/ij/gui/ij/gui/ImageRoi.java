/*    */ package ij.gui;
/*    */ 
/*    */ import ij.ImagePlus;
/*    */ import ij.io.FileSaver;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.AlphaComposite;
/*    */ import java.awt.Color;
/*    */ import java.awt.Composite;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Image;
/*    */ import java.awt.image.BufferedImage;
/*    */ 
/*    */ public class ImageRoi extends Roi
/*    */ {
/*    */   private Image img;
/*    */   private Composite composite;
/* 14 */   private double opacity = 1.0D;
/*    */ 
/*    */   public ImageRoi(int x, int y, BufferedImage bi)
/*    */   {
/* 18 */     super(x, y, bi.getWidth(), bi.getHeight());
/* 19 */     this.img = bi;
/* 20 */     setStrokeColor(Color.black);
/*    */   }
/*    */ 
/*    */   public ImageRoi(int x, int y, ImageProcessor ip)
/*    */   {
/* 25 */     super(x, y, ip.getWidth(), ip.getHeight());
/* 26 */     this.img = ip.createImage();
/* 27 */     setStrokeColor(Color.black);
/*    */   }
/*    */ 
/*    */   public void draw(Graphics g) {
/* 31 */     Graphics2D g2d = (Graphics2D)g;
/* 32 */     double mag = getMagnification();
/* 33 */     int sx2 = screenX(this.x + this.width);
/* 34 */     int sy2 = screenY(this.y + this.height);
/* 35 */     Composite saveComposite = null;
/* 36 */     if (this.composite != null) {
/* 37 */       saveComposite = g2d.getComposite();
/* 38 */       g2d.setComposite(this.composite);
/*    */     }
/* 40 */     g.drawImage(this.img, screenX(this.x), screenY(this.y), sx2, sy2, 0, 0, this.img.getWidth(null), this.img.getHeight(null), null);
/* 41 */     if (this.composite != null) g2d.setComposite(saveComposite);
/*    */   }
/*    */ 
/*    */   public void setComposite(Composite composite)
/*    */   {
/* 46 */     this.composite = composite;
/*    */   }
/*    */ 
/*    */   public void setOpacity(double opacity)
/*    */   {
/* 52 */     if (opacity < 0.0D) opacity = 0.0D;
/* 53 */     if (opacity > 1.0D) opacity = 1.0D;
/* 54 */     this.opacity = opacity;
/* 55 */     if (opacity != 1.0D)
/* 56 */       this.composite = AlphaComposite.getInstance(3, (float)opacity);
/*    */     else
/* 58 */       this.composite = null;
/*    */   }
/*    */ 
/*    */   public byte[] getSerializedImage()
/*    */   {
/* 63 */     ImagePlus imp = new ImagePlus("", this.img);
/* 64 */     return new FileSaver(imp).serialize();
/*    */   }
/*    */ 
/*    */   public double getOpacity()
/*    */   {
/* 69 */     return this.opacity;
/*    */   }
/*    */ 
/*    */   public synchronized Object clone() {
/* 73 */     ImagePlus imp = new ImagePlus("", this.img);
/* 74 */     ImageRoi roi2 = new ImageRoi(this.x, this.y, imp.getProcessor());
/* 75 */     roi2.setOpacity(getOpacity());
/* 76 */     return roi2;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.ImageRoi
 * JD-Core Version:    0.6.2
 */