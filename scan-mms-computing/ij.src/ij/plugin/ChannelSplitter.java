/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.measure.Calibration;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class ChannelSplitter
/*     */   implements PlugIn
/*     */ {
/*     */   public void run(String arg)
/*     */   {
/*  14 */     ImagePlus imp = IJ.getImage();
/*  15 */     if (imp.isComposite()) {
/*  16 */       ImagePlus[] channels = split(imp);
/*  17 */       for (int i = 0; i < channels.length; i++)
/*  18 */         channels[i].show();
/*  19 */       imp.changes = false;
/*  20 */       imp.close();
/*  21 */     } else if (imp.getType() == 4) {
/*  22 */       splitRGB(imp);
/*     */     } else {
/*  24 */       IJ.error("Split Channels", "Multichannel image required");
/*     */     }
/*     */   }
/*     */ 
/*  28 */   private void splitRGB(ImagePlus imp) { boolean keepSource = IJ.altKeyDown();
/*  29 */     String title = imp.getTitle();
/*  30 */     Calibration cal = imp.getCalibration();
/*  31 */     ImageStack[] channels = splitRGB(imp.getStack(), keepSource);
/*  32 */     if (!keepSource) {
/*  33 */       imp.unlock(); imp.changes = false; imp.close();
/*  34 */     }ImagePlus rImp = new ImagePlus(title + " (red)", channels[0]);
/*  35 */     rImp.setCalibration(cal);
/*  36 */     rImp.show();
/*  37 */     if (IJ.isMacOSX()) IJ.wait(500);
/*  38 */     ImagePlus gImp = new ImagePlus(title + " (green)", channels[1]);
/*  39 */     gImp.setCalibration(cal);
/*  40 */     gImp.show();
/*  41 */     if (IJ.isMacOSX()) IJ.wait(500);
/*  42 */     ImagePlus bImp = new ImagePlus(title + " (blue)", channels[2]);
/*  43 */     bImp.setCalibration(cal);
/*  44 */     bImp.show();
/*     */   }
/*     */ 
/*     */   public static ImagePlus[] split(ImagePlus imp)
/*     */   {
/*  49 */     if (imp.getType() == 4) {
/*  50 */       return null;
/*     */     }
/*  52 */     int width = imp.getWidth();
/*  53 */     int height = imp.getHeight();
/*  54 */     int channels = imp.getNChannels();
/*  55 */     int slices = imp.getNSlices();
/*  56 */     int frames = imp.getNFrames();
/*  57 */     int bitDepth = imp.getBitDepth();
/*  58 */     int size = slices * frames;
/*  59 */     Vector images = new Vector();
/*  60 */     HyperStackReducer reducer = new HyperStackReducer(imp);
/*  61 */     for (int c = 1; c <= channels; c++) {
/*  62 */       ImageStack stack2 = new ImageStack(width, height, size);
/*  63 */       stack2.setPixels(imp.getProcessor().getPixels(), 1);
/*  64 */       ImagePlus imp2 = new ImagePlus("C" + c + "-" + imp.getTitle(), stack2);
/*  65 */       stack2.setPixels(null, 1);
/*  66 */       imp.setPosition(c, 1, 1);
/*  67 */       imp2.setDimensions(1, slices, frames);
/*  68 */       imp2.setCalibration(imp.getCalibration());
/*  69 */       reducer.reduce(imp2);
/*  70 */       if ((imp.isComposite()) && (((CompositeImage)imp).getMode() == 3))
/*  71 */         IJ.run(imp2, "Grays", "");
/*  72 */       if (imp2.getNDimensions() > 3)
/*  73 */         imp2.setOpenAsHyperStack(true);
/*  74 */       images.add(imp2);
/*     */     }
/*  76 */     ImagePlus[] array = new ImagePlus[images.size()];
/*  77 */     return (ImagePlus[])images.toArray(array);
/*     */   }
/*     */ 
/*     */   public static ImageStack[] splitRGB(ImageStack rgb, boolean keepSource)
/*     */   {
/*  83 */     int w = rgb.getWidth();
/*  84 */     int h = rgb.getHeight();
/*  85 */     ImageStack[] channels = new ImageStack[3];
/*  86 */     for (int i = 0; i < 3; i++) {
/*  87 */       channels[i] = new ImageStack(w, h);
/*     */     }
/*     */ 
/*  90 */     int slice = 1;
/*  91 */     int inc = keepSource ? 1 : 0;
/*  92 */     int n = rgb.getSize();
/*  93 */     for (int i = 1; i <= n; i++) {
/*  94 */       IJ.showStatus(i + "/" + n);
/*  95 */       byte[] r = new byte[w * h];
/*  96 */       byte[] g = new byte[w * h];
/*  97 */       byte[] b = new byte[w * h];
/*  98 */       ColorProcessor cp = (ColorProcessor)rgb.getProcessor(slice);
/*  99 */       slice += inc;
/* 100 */       cp.getRGB(r, g, b);
/* 101 */       if (!keepSource)
/* 102 */         rgb.deleteSlice(1);
/* 103 */       channels[0].addSlice(null, r);
/* 104 */       channels[1].addSlice(null, g);
/* 105 */       channels[2].addSlice(null, b);
/* 106 */       IJ.showProgress(i / n);
/*     */     }
/* 108 */     return channels;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ChannelSplitter
 * JD-Core Version:    0.6.2
 */