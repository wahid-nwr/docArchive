/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImageJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.gui.ImageWindow;
/*    */ import ij.process.ColorProcessor;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.Image;
/*    */ import java.awt.image.ImageProducer;
/*    */ import java.net.URL;
/*    */ 
/*    */ public class AboutBox
/*    */   implements PlugIn
/*    */ {
/*    */   static final int SMALL_FONT = 14;
/*    */   static final int LARGE_FONT = 30;
/*    */ 
/*    */   public void run(String arg)
/*    */   {
/* 16 */     System.gc();
/* 17 */     int lines = 7;
/* 18 */     String[] text = new String[lines];
/* 19 */     text[0] = "ImageJ 1.45s";
/* 20 */     text[1] = "Wayne Rasband";
/* 21 */     text[2] = "National Institutes of Health, USA";
/* 22 */     text[3] = "http://imagej.nih.gov/ij";
/* 23 */     text[4] = ("Java " + System.getProperty("java.version") + (IJ.is64Bit() ? " (64-bit)" : " (32-bit)"));
/* 24 */     text[5] = IJ.freeMemory();
/* 25 */     text[6] = "ImageJ is in the public domain";
/* 26 */     ImageProcessor ip = null;
/* 27 */     ImageJ ij = IJ.getInstance();
/* 28 */     URL url = ij.getClass().getResource("/about.jpg");
/* 29 */     if (url != null) {
/* 30 */       Image img = null;
/*    */       try { img = ij.createImage((ImageProducer)url.getContent()); } catch (Exception e) {
/*    */       }
/* 33 */       if (img != null) {
/* 34 */         ImagePlus imp = new ImagePlus("", img);
/* 35 */         ip = imp.getProcessor();
/*    */       }
/*    */     }
/* 38 */     if (ip == null)
/* 39 */       ip = new ColorProcessor(55, 45);
/* 40 */     ip = ip.resize(ip.getWidth() * 4, ip.getHeight() * 4);
/* 41 */     ip.setFont(new Font("SansSerif", 0, 30));
/* 42 */     ip.setAntialiasedText(true);
/* 43 */     int[] widths = new int[lines];
/* 44 */     widths[0] = ip.getStringWidth(text[0]);
/* 45 */     ip.setFont(new Font("SansSerif", 0, 14));
/* 46 */     for (int i = 1; i < lines - 1; i++)
/* 47 */       widths[i] = ip.getStringWidth(text[i]);
/* 48 */     int max = 0;
/* 49 */     for (int i = 0; i < lines - 1; i++)
/* 50 */       if (widths[i] > max)
/* 51 */         max = widths[i];
/* 52 */     ip.setColor(new Color(255, 255, 140));
/* 53 */     ip.setFont(new Font("SansSerif", 0, 30));
/* 54 */     int y = 45;
/* 55 */     ip.drawString(text[0], x(text[0], ip, max), y);
/* 56 */     ip.setFont(new Font("SansSerif", 0, 14));
/* 57 */     y += 30;
/* 58 */     ip.drawString(text[1], x(text[1], ip, max), y);
/* 59 */     y += 18;
/* 60 */     ip.drawString(text[2], x(text[2], ip, max), y);
/* 61 */     y += 18;
/* 62 */     ip.drawString(text[3], x(text[3], ip, max), y);
/* 63 */     y += 18;
/* 64 */     ip.drawString(text[4], x(text[4], ip, max), y);
/* 65 */     if (IJ.maxMemory() > 0L) {
/* 66 */       y += 18;
/* 67 */       ip.drawString(text[5], x(text[5], ip, max), y);
/*    */     }
/* 69 */     ip.drawString(text[6], ip.getWidth() - ip.getStringWidth(text[6]) - 10, ip.getHeight() - 3);
/* 70 */     ImageWindow.centerNextImage();
/* 71 */     new ImagePlus("About ImageJ", ip).show();
/*    */   }
/*    */ 
/*    */   int x(String text, ImageProcessor ip, int max) {
/* 75 */     return ip.getWidth() - max + (max - ip.getStringWidth(text)) / 2 - 10;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.AboutBox
 * JD-Core Version:    0.6.2
 */