/*     */ package ij.plugin.frame;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.Measurements;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.process.StackStatistics;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ 
/*     */ class ThresholdPlot extends Canvas
/*     */   implements Measurements, MouseListener
/*     */ {
/*     */   static final int WIDTH = 256;
/*     */   static final int HEIGHT = 48;
/* 718 */   double minThreshold = 85.0D;
/* 719 */   double maxThreshold = 170.0D;
/*     */   ImageStatistics stats;
/*     */   int[] histogram;
/*     */   Color[] hColors;
/*     */   int hmax;
/*     */   Image os;
/*     */   Graphics osg;
/*     */   int mode;
/*     */   int originalModeCount;
/*     */   double stackMin;
/*     */   double stackMax;
/*     */   int imageID2;
/*     */   boolean entireStack2;
/*     */   double mean2;
/*     */ 
/*     */   public ThresholdPlot()
/*     */   {
/* 734 */     addMouseListener(this);
/* 735 */     setSize(257, 49);
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize()
/*     */   {
/* 741 */     return new Dimension(257, 49);
/*     */   }
/*     */ 
/*     */   ImageStatistics setHistogram(ImagePlus imp, boolean entireStack) {
/* 745 */     double mean = entireStack ? imp.getProcessor().getStatistics().mean : 0.0D;
/* 746 */     if ((entireStack) && (this.stats != null) && (imp.getID() == this.imageID2) && (entireStack == this.entireStack2) && (mean == this.mean2))
/*     */     {
/* 748 */       return this.stats;
/* 749 */     }this.mean2 = mean;
/* 750 */     ImageProcessor ip = imp.getProcessor();
/* 751 */     this.stats = null;
/* 752 */     if (entireStack)
/* 753 */       this.stats = new StackStatistics(imp);
/* 754 */     if (!(ip instanceof ByteProcessor)) {
/* 755 */       if (entireStack) {
/* 756 */         if (imp.getLocalCalibration().isSigned16Bit()) {
/* 757 */           this.stats.min += 32768.0D; this.stats.max += 32768.0D;
/* 758 */         }this.stackMin = this.stats.min;
/* 759 */         this.stackMax = this.stats.max;
/* 760 */         ip.setMinAndMax(this.stackMin, this.stackMax);
/* 761 */         imp.updateAndDraw();
/*     */       } else {
/* 763 */         this.stackMin = (this.stackMax = 0.0D);
/* 764 */         if (this.entireStack2) {
/* 765 */           ip.resetMinAndMax();
/* 766 */           imp.updateAndDraw();
/*     */         }
/*     */       }
/* 769 */       Calibration cal = imp.getCalibration();
/* 770 */       if ((ip instanceof FloatProcessor)) {
/* 771 */         int digits = Math.max(Analyzer.getPrecision(), 2);
/* 772 */         IJ.showStatus("min=" + IJ.d2s(ip.getMin(), digits) + ", max=" + IJ.d2s(ip.getMax(), digits));
/*     */       } else {
/* 774 */         IJ.showStatus("min=" + (int)cal.getCValue(ip.getMin()) + ", max=" + (int)cal.getCValue(ip.getMax()));
/* 775 */       }ip = ip.convertToByte(true);
/* 776 */       ip.setColorModel(ip.getDefaultColorModel());
/*     */     }
/* 778 */     Roi roi = imp.getRoi();
/* 779 */     if ((roi != null) && (!roi.isArea())) roi = null;
/* 780 */     ip.setRoi(roi);
/* 781 */     if (this.stats == null)
/* 782 */       this.stats = ImageStatistics.getStatistics(ip, 25, null);
/* 783 */     int maxCount2 = 0;
/* 784 */     this.histogram = this.stats.histogram;
/* 785 */     this.originalModeCount = this.histogram[this.stats.mode];
/* 786 */     for (int i = 0; i < this.stats.nBins; i++)
/* 787 */       if ((this.histogram[i] > maxCount2) && (i != this.stats.mode))
/* 788 */         maxCount2 = this.histogram[i];
/* 789 */     this.hmax = this.stats.maxCount;
/* 790 */     if ((this.hmax > maxCount2 * 2) && (maxCount2 != 0)) {
/* 791 */       this.hmax = ((int)(maxCount2 * 1.5D));
/* 792 */       this.histogram[this.stats.mode] = this.hmax;
/*     */     }
/* 794 */     this.os = null;
/*     */ 
/* 796 */     ColorModel cm = ip.getColorModel();
/* 797 */     if (!(cm instanceof IndexColorModel))
/* 798 */       return null;
/* 799 */     IndexColorModel icm = (IndexColorModel)cm;
/* 800 */     int mapSize = icm.getMapSize();
/* 801 */     if (mapSize != 256)
/* 802 */       return null;
/* 803 */     byte[] r = new byte[256];
/* 804 */     byte[] g = new byte[256];
/* 805 */     byte[] b = new byte[256];
/* 806 */     icm.getReds(r);
/* 807 */     icm.getGreens(g);
/* 808 */     icm.getBlues(b);
/* 809 */     this.hColors = new Color[256];
/* 810 */     for (int i = 0; i < 256; i++)
/* 811 */       this.hColors[i] = new Color(r[i] & 0xFF, g[i] & 0xFF, b[i] & 0xFF);
/* 812 */     this.imageID2 = imp.getID();
/* 813 */     this.entireStack2 = entireStack;
/* 814 */     return this.stats;
/*     */   }
/*     */ 
/*     */   public void update(Graphics g) {
/* 818 */     paint(g);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g) {
/* 822 */     if (g == null) return;
/* 823 */     if (this.histogram != null) {
/* 824 */       if ((this.os == null) && (this.hmax > 0)) {
/* 825 */         this.os = createImage(256, 48);
/* 826 */         this.osg = this.os.getGraphics();
/* 827 */         this.osg.setColor(Color.white);
/* 828 */         this.osg.fillRect(0, 0, 256, 48);
/* 829 */         this.osg.setColor(Color.gray);
/* 830 */         for (int i = 0; i < 256; i++) {
/* 831 */           if (this.hColors != null) this.osg.setColor(this.hColors[i]);
/* 832 */           this.osg.drawLine(i, 48, i, 48 - 48 * this.histogram[i] / this.hmax);
/*     */         }
/* 834 */         this.osg.dispose();
/*     */       }
/* 836 */       if (this.os == null) return;
/* 837 */       g.drawImage(this.os, 0, 0, this);
/*     */     } else {
/* 839 */       g.setColor(Color.white);
/* 840 */       g.fillRect(0, 0, 256, 48);
/*     */     }
/* 842 */     g.setColor(Color.black);
/* 843 */     g.drawRect(0, 0, 256, 48);
/* 844 */     if (this.mode == 0) {
/* 845 */       g.setColor(Color.red);
/* 846 */     } else if (this.mode == 2) {
/* 847 */       g.setColor(Color.blue);
/* 848 */       g.drawRect(1, 1, (int)this.minThreshold - 2, 48);
/* 849 */       g.drawRect(1, 0, (int)this.minThreshold - 2, 0);
/* 850 */       g.setColor(Color.green);
/* 851 */       g.drawRect((int)this.maxThreshold + 1, 1, 256 - (int)this.maxThreshold, 48);
/* 852 */       g.drawRect((int)this.maxThreshold + 1, 0, 256 - (int)this.maxThreshold, 0);
/* 853 */       return;
/*     */     }
/* 855 */     g.drawRect((int)this.minThreshold, 1, (int)(this.maxThreshold - this.minThreshold), 48);
/* 856 */     g.drawLine((int)this.minThreshold, 0, (int)this.maxThreshold, 0);
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.ThresholdPlot
 * JD-Core Version:    0.6.2
 */