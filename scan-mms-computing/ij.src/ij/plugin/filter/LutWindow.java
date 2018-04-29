/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.text.TextWindow;
/*     */ import java.awt.Button;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Panel;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.image.IndexColorModel;
/*     */ 
/*     */ class LutWindow extends ImageWindow
/*     */   implements ActionListener
/*     */ {
/*     */   private Button button;
/*     */   private ImageProcessor ip;
/*     */ 
/*     */   LutWindow(ImagePlus imp, ImageCanvas ic, ImageProcessor ip)
/*     */   {
/* 114 */     super(imp, ic);
/* 115 */     this.ip = ip;
/* 116 */     addPanel();
/*     */   }
/*     */ 
/*     */   void addPanel() {
/* 120 */     Panel panel = new Panel();
/* 121 */     panel.setLayout(new FlowLayout(2));
/* 122 */     this.button = new Button(" List... ");
/* 123 */     this.button.addActionListener(this);
/* 124 */     panel.add(this.button);
/* 125 */     add(panel);
/* 126 */     pack();
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 130 */     Object b = e.getSource();
/* 131 */     if (b == this.button)
/* 132 */       list(this.ip);
/*     */   }
/*     */ 
/*     */   void list(ImageProcessor ip) {
/* 136 */     IndexColorModel icm = (IndexColorModel)ip.getColorModel();
/* 137 */     int size = icm.getMapSize();
/* 138 */     byte[] r = new byte[size];
/* 139 */     byte[] g = new byte[size];
/* 140 */     byte[] b = new byte[size];
/* 141 */     icm.getReds(r);
/* 142 */     icm.getGreens(g);
/* 143 */     icm.getBlues(b);
/* 144 */     StringBuffer sb = new StringBuffer();
/* 145 */     String headings = "Index\tRed\tGreen\tBlue";
/* 146 */     for (int i = 0; i < size; i++)
/* 147 */       sb.append(i + "\t" + (r[i] & 0xFF) + "\t" + (g[i] & 0xFF) + "\t" + (b[i] & 0xFF) + "\n");
/* 148 */     TextWindow tw = new TextWindow("LUT", headings, sb.toString(), 250, 400);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.LutWindow
 * JD-Core Version:    0.6.2
 */