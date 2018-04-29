/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.process.ImageProcessor;
/*    */ 
/*    */ /** @deprecated */
/*    */ public class Resizer
/*    */   implements PlugInFilter
/*    */ {
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 12 */     if (imp != null)
/* 13 */       IJ.run(imp, "Resize...", "");
/* 14 */     return 4096;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Resizer
 * JD-Core Version:    0.6.2
 */