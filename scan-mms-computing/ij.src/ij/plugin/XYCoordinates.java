/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.SaveDialog;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ 
/*     */ public class XYCoordinates
/*     */   implements PlugIn
/*     */ {
/*     */   static boolean processStack;
/*     */   static boolean invertY;
/*     */   static boolean suppress;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  23 */     ImagePlus imp = IJ.getImage();
/*  24 */     ImageProcessor ip = imp.getProcessor();
/*  25 */     int width = imp.getWidth();
/*  26 */     int height = imp.getHeight();
/*  27 */     double background = ip.getPixelValue(0, 0);
/*  28 */     String bg = " \n";
/*  29 */     boolean rgb = imp.getBitDepth() == 24;
/*  30 */     if (rgb) {
/*  31 */       int c = ip.getPixel(0, 0);
/*  32 */       int r = (c & 0xFF0000) >> 16;
/*  33 */       int g = (c & 0xFF00) >> 8;
/*  34 */       int b = c & 0xFF;
/*  35 */       bg = r + "," + g + "," + b;
/*  36 */       bg = " \n    Background value: " + bg + "\n";
/*     */     }
/*  38 */     imp.killRoi();
/*     */ 
/*  40 */     int slices = imp.getStackSize();
/*  41 */     String msg = "This plugin writes to a text file the XY coordinates and\npixel value of all non-background pixels. Backround\ndefaults to be the value of the pixel in the upper\nleft corner of the image.\n" + bg;
/*     */ 
/*  48 */     GenericDialog gd = new GenericDialog("Save XY Coordinates");
/*  49 */     gd.addMessage(msg);
/*  50 */     int digits = (int)background == background ? 0 : 4;
/*  51 */     if (!rgb) {
/*  52 */       gd.setInsets(5, 35, 3);
/*  53 */       gd.addNumericField("Background value:", background, digits);
/*     */     }
/*  55 */     gd.setInsets(10, 35, 0);
/*  56 */     gd.addCheckbox("Invert y coordinates off (0 at top of image)", invertY);
/*  57 */     gd.setInsets(0, 35, 0);
/*  58 */     gd.addCheckbox("Suppress Log output", suppress);
/*  59 */     if (slices > 1) {
/*  60 */       gd.setInsets(0, 35, 0);
/*  61 */       gd.addCheckbox("Process all " + slices + " images", processStack);
/*     */     }
/*  63 */     gd.showDialog();
/*  64 */     if (gd.wasCanceled())
/*  65 */       return;
/*  66 */     if (!rgb)
/*  67 */       background = gd.getNextNumber();
/*  68 */     invertY = gd.getNextBoolean();
/*  69 */     suppress = gd.getNextBoolean();
/*  70 */     if (slices > 1)
/*  71 */       processStack = gd.getNextBoolean();
/*     */     else
/*  73 */       processStack = false;
/*  74 */     if (!processStack) slices = 1;
/*     */ 
/*  76 */     SaveDialog sd = new SaveDialog("Save Coordinates as Text...", imp.getTitle(), ".txt");
/*  77 */     String name = sd.getFileName();
/*  78 */     if (name == null)
/*  79 */       return;
/*  80 */     String directory = sd.getDirectory();
/*  81 */     PrintWriter pw = null;
/*     */     try {
/*  83 */       FileOutputStream fos = new FileOutputStream(directory + name);
/*  84 */       BufferedOutputStream bos = new BufferedOutputStream(fos);
/*  85 */       pw = new PrintWriter(bos);
/*     */     }
/*     */     catch (IOException e) {
/*  88 */       IJ.write("" + e);
/*  89 */       return;
/*     */     }
/*     */ 
/*  92 */     IJ.showStatus("Saving coordinates...");
/*  93 */     int count = 0;
/*     */ 
/*  96 */     int type = imp.getType();
/*  97 */     ImageStack stack = imp.getStack();
/*  98 */     for (int z = 0; z < slices; z++) {
/*  99 */       if (slices > 1) ip = stack.getProcessor(z + 1);
/* 100 */       String zstr = slices > 1 ? z + "\t" : "";
/* 101 */       for (int i = 0; i < height; i++) {
/* 102 */         int y = invertY ? i : height - 1 - i;
/* 103 */         for (int x = 0; x < width; x++) {
/* 104 */           float v = ip.getPixelValue(x, y);
/* 105 */           if (v != background) {
/* 106 */             if (type == 2) {
/* 107 */               pw.println(x + "\t" + (invertY ? y : height - 1 - y) + "\t" + zstr + v);
/* 108 */             } else if (rgb) {
/* 109 */               int c = ip.getPixel(x, y);
/* 110 */               int r = (c & 0xFF0000) >> 16;
/* 111 */               int g = (c & 0xFF00) >> 8;
/* 112 */               int b = c & 0xFF;
/* 113 */               pw.println(x + "\t" + (invertY ? y : height - 1 - y) + "\t" + zstr + r + "\t" + g + "\t" + b);
/*     */             } else {
/* 115 */               pw.println(x + "\t" + (invertY ? y : height - 1 - y) + "\t" + zstr + (int)v);
/* 116 */             }count++;
/*     */           }
/*     */         }
/* 119 */         if ((slices == 1) && (y % 10 == 0)) IJ.showProgress((height - y) / height);
/*     */       }
/* 121 */       if (slices > 1) IJ.showProgress(z + 1, slices);
/* 122 */       String img = slices > 1 ? "-" + (z + 1) : "";
/* 123 */       if (!suppress)
/* 124 */         IJ.log(imp.getTitle() + img + ": " + count + " pixels (" + IJ.d2s(count * 100.0D / (width * height)) + "%)\n");
/* 125 */       count = 0;
/*     */     }
/* 127 */     IJ.showProgress(1.0D);
/* 128 */     IJ.showStatus("");
/* 129 */     pw.close();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.XYCoordinates
 * JD-Core Version:    0.6.2
 */