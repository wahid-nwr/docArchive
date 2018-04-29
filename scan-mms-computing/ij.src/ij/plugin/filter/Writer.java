/*    */ package ij.plugin.filter;
/*    */ 
/*    */ import ij.ImagePlus;
/*    */ import ij.io.FileSaver;
/*    */ import ij.process.ImageProcessor;
/*    */ 
/*    */ public class Writer
/*    */   implements PlugInFilter
/*    */ {
/*    */   private String arg;
/*    */   private ImagePlus imp;
/*    */ 
/*    */   public int setup(String arg, ImagePlus imp)
/*    */   {
/* 13 */     this.arg = arg;
/* 14 */     this.imp = imp;
/* 15 */     return 159;
/*    */   }
/*    */ 
/*    */   public void run(ImageProcessor ip) {
/* 19 */     if (this.arg.equals("tiff"))
/* 20 */       new FileSaver(this.imp).saveAsTiff();
/* 21 */     else if (this.arg.equals("gif"))
/* 22 */       new FileSaver(this.imp).saveAsGif();
/* 23 */     else if (this.arg.equals("jpeg"))
/* 24 */       new FileSaver(this.imp).saveAsJpeg();
/* 25 */     else if (this.arg.equals("text"))
/* 26 */       new FileSaver(this.imp).saveAsText();
/* 27 */     else if (this.arg.equals("lut"))
/* 28 */       new FileSaver(this.imp).saveAsLut();
/* 29 */     else if (this.arg.equals("raw"))
/* 30 */       new FileSaver(this.imp).saveAsRaw();
/* 31 */     else if (this.arg.equals("zip"))
/* 32 */       new FileSaver(this.imp).saveAsZip();
/* 33 */     else if (this.arg.equals("bmp"))
/* 34 */       new FileSaver(this.imp).saveAsBmp();
/* 35 */     else if (this.arg.equals("png"))
/* 36 */       new FileSaver(this.imp).saveAsPng();
/* 37 */     else if (this.arg.equals("pgm"))
/* 38 */       new FileSaver(this.imp).saveAsPgm();
/* 39 */     else if (this.arg.equals("fits"))
/* 40 */       new FileSaver(this.imp).saveAsFits();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Writer
 * JD-Core Version:    0.6.2
 */