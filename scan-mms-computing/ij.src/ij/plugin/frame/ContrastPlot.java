/*      */ package ij.plugin.frame;
/*      */ 
/*      */ import ij.process.ImageStatistics;
/*      */ import java.awt.Canvas;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Image;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ 
/*      */ class ContrastPlot extends Canvas
/*      */   implements MouseListener
/*      */ {
/*      */   static final int WIDTH = 128;
/*      */   static final int HEIGHT = 64;
/* 1127 */   double defaultMin = 0.0D;
/* 1128 */   double defaultMax = 255.0D;
/* 1129 */   double min = 0.0D;
/* 1130 */   double max = 255.0D;
/*      */   int[] histogram;
/*      */   int hmax;
/*      */   Image os;
/*      */   Graphics osg;
/* 1135 */   Color color = Color.gray;
/*      */ 
/*      */   public ContrastPlot() {
/* 1138 */     addMouseListener(this);
/* 1139 */     setSize(129, 65);
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize()
/*      */   {
/* 1145 */     return new Dimension(129, 65);
/*      */   }
/*      */ 
/*      */   void setHistogram(ImageStatistics stats, Color color) {
/* 1149 */     this.color = color;
/* 1150 */     this.histogram = stats.histogram;
/* 1151 */     if (this.histogram.length != 256) {
/* 1152 */       this.histogram = null; return;
/* 1153 */     }for (int i = 0; i < 128; i++)
/* 1154 */       this.histogram[i] = ((this.histogram[(2 * i)] + this.histogram[(2 * i + 1)]) / 2);
/* 1155 */     int maxCount = 0;
/* 1156 */     int mode = 0;
/* 1157 */     for (int i = 0; i < 128; i++) {
/* 1158 */       if (this.histogram[i] > maxCount) {
/* 1159 */         maxCount = this.histogram[i];
/* 1160 */         mode = i;
/*      */       }
/*      */     }
/* 1163 */     int maxCount2 = 0;
/* 1164 */     for (int i = 0; i < 128; i++) {
/* 1165 */       if ((this.histogram[i] > maxCount2) && (i != mode))
/* 1166 */         maxCount2 = this.histogram[i];
/*      */     }
/* 1168 */     this.hmax = stats.maxCount;
/* 1169 */     if ((this.hmax > maxCount2 * 2) && (maxCount2 != 0)) {
/* 1170 */       this.hmax = ((int)(maxCount2 * 1.5D));
/* 1171 */       this.histogram[mode] = this.hmax;
/*      */     }
/* 1173 */     this.os = null;
/*      */   }
/*      */ 
/*      */   public void update(Graphics g) {
/* 1177 */     paint(g);
/*      */   }
/*      */ 
/*      */   public void paint(Graphics g)
/*      */   {
/* 1182 */     double scale = 128.0D / (this.defaultMax - this.defaultMin);
/* 1183 */     double slope = 0.0D;
/* 1184 */     if (this.max != this.min)
/* 1185 */       slope = 64.0D / (this.max - this.min);
/*      */     int y1;
/*      */     int x1;
/*      */     int y1;
/* 1186 */     if (this.min >= this.defaultMin) {
/* 1187 */       int x1 = (int)(scale * (this.min - this.defaultMin));
/* 1188 */       y1 = 64;
/*      */     } else {
/* 1190 */       x1 = 0;
/*      */       int y1;
/* 1191 */       if (this.max > this.min)
/* 1192 */         y1 = 64 - (int)((this.defaultMin - this.min) * slope);
/*      */       else
/* 1194 */         y1 = 64;
/*      */     }
/*      */     int y2;
/*      */     int x2;
/*      */     int y2;
/* 1196 */     if (this.max <= this.defaultMax) {
/* 1197 */       int x2 = (int)(scale * (this.max - this.defaultMin));
/* 1198 */       y2 = 0;
/*      */     } else {
/* 1200 */       x2 = 128;
/*      */       int y2;
/* 1201 */       if (this.max > this.min)
/* 1202 */         y2 = 64 - (int)((this.defaultMax - this.min) * slope);
/*      */       else
/* 1204 */         y2 = 0;
/*      */     }
/* 1206 */     if (this.histogram != null) {
/* 1207 */       if ((this.os == null) && (this.hmax != 0)) {
/* 1208 */         this.os = createImage(128, 64);
/* 1209 */         this.osg = this.os.getGraphics();
/* 1210 */         this.osg.setColor(Color.white);
/* 1211 */         this.osg.fillRect(0, 0, 128, 64);
/* 1212 */         this.osg.setColor(this.color);
/* 1213 */         for (int i = 0; i < 128; i++)
/* 1214 */           this.osg.drawLine(i, 64, i, 64 - 64 * this.histogram[i] / this.hmax);
/* 1215 */         this.osg.dispose();
/*      */       }
/* 1217 */       if (this.os != null) g.drawImage(this.os, 0, 0, this); 
/*      */     }
/* 1219 */     else { g.setColor(Color.white);
/* 1220 */       g.fillRect(0, 0, 128, 64);
/*      */     }
/* 1222 */     g.setColor(Color.black);
/* 1223 */     g.drawLine(x1, y1, x2, y2);
/* 1224 */     g.drawLine(x2, 59, x2, 64);
/* 1225 */     g.drawRect(0, 0, 128, 64);
/*      */   }
/*      */ 
/*      */   public void mousePressed(MouseEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void mouseReleased(MouseEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void mouseExited(MouseEvent e)
/*      */   {
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
 * Qualified Name:     ij.plugin.frame.ContrastPlot
 * JD-Core Version:    0.6.2
 */