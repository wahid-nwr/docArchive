/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.image.BufferedImage;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ class ClipedPanel extends JPanel
/*     */ {
/*     */   private Image image;
/*     */   int imageWidth;
/*     */   int imageHeight;
/*     */   BufferedImage imageb;
/*     */   Dimension size;
/*     */   Rectangle clip;
/*     */   boolean showClip;
/*     */ 
/*     */   ClipedPanel(Image image)
/*     */   {
/* 858 */     this.image = image;
/* 859 */     this.imageWidth = image.getWidth(null);
/* 860 */     this.imageHeight = image.getHeight(null);
/*     */ 
/* 862 */     this.imageb = ((BufferedImage)image);
/* 863 */     this.size = new Dimension(this.imageb.getWidth(), this.imageb.getHeight());
/* 864 */     this.showClip = false;
/*     */   }
/*     */ 
/*     */   protected void paintComponent(Graphics g)
/*     */   {
/* 871 */     super.paintComponent(g);
/* 872 */     Graphics2D g2 = (Graphics2D)g;
/* 873 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
/* 874 */       RenderingHints.VALUE_ANTIALIAS_ON);
/* 875 */     int x = (getWidth() - this.size.width) / 2;
/* 876 */     int y = (getHeight() - this.size.height) / 2;
/*     */ 
/* 881 */     g2.drawImage(this.imageb, x, y, this);
/*     */   }
/*     */ 
/*     */   public ClipedPanel getPanel()
/*     */   {
/* 887 */     return this;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/scanning.jar
 * Qualified Name:     ClipedPanel
 * JD-Core Version:    0.6.2
 */