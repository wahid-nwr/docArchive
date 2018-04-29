/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.ImagePlus;
/*    */ import ij.ImageStack;
/*    */ import ij.WindowManager;
/*    */ import ij.plugin.ChannelSplitter;
/*    */ import ij.process.ImageProcessor;
/*    */ 
/*    */ public class RGBStackSplitter
/*    */   implements PlugInFilter
/*    */ {
/*    */   ImagePlus imp;
/*    */   public ImageStack red;
/*    */   public ImageStack green;
/*    */   public ImageStack blue;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 12 */     this.imp = imp;
/* 13 */     new ChannelSplitter().run(arg);
/* 14 */     return 4096;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void split(ImagePlus imp) {
/* 22 */     WindowManager.setTempCurrentImage(imp);
/* 23 */     new ChannelSplitter().run("");
/*    */   }
/*    */ 
/*    */   public void split(ImageStack rgb, boolean keepSource)
/*    */   {
/* 28 */     ImageStack[] channels = ChannelSplitter.splitRGB(rgb, keepSource);
/* 29 */     this.red = channels[0];
/* 30 */     this.green = channels[1];
/* 31 */     this.blue = channels[2];
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.RGBStackSplitter
 * JD-Core Version:    0.6.2
 */