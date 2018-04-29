/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.ImagePlus;
/*    */ import ij.plugin.Duplicator;
/*    */ import ij.process.ImageProcessor;
/*    */ 
/*    */ /** @deprecated */
/*    */ public class Duplicater
/*    */   implements PlugInFilter
/*    */ {
/*    */   ImagePlus imp;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 14 */     this.imp = imp;
/* 15 */     return 159;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/*    */   }
/*    */ 
/*    */   public ImagePlus duplicateStack(ImagePlus imp, String newTitle) {
/* 22 */     ImagePlus imp2 = new Duplicator().run(imp);
/* 23 */     imp2.setTitle(newTitle);
/* 24 */     return imp2;
/*    */   }
/*    */ 
/*    */   public ImagePlus duplicateSubstack(ImagePlus imp, String newTitle, int first, int last) {
/* 28 */     ImagePlus imp2 = new Duplicator().run(imp, first, last);
/* 29 */     imp2.setTitle(newTitle);
/* 30 */     return imp2;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Duplicater
 * JD-Core Version:    0.6.2
 */