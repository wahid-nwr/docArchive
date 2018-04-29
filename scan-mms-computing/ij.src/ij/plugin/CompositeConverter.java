/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.CompositeImage;
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.ImageStack;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.gui.ImageWindow;
/*    */ import ij.process.ColorProcessor;
/*    */ import java.awt.Point;
/*    */ 
/*    */ public class CompositeConverter
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String arg)
/*    */   {
/* 13 */     String[] modes = { "Composite", "Color", "Grayscale" };
/* 14 */     ImagePlus imp = IJ.getImage();
/* 15 */     if (imp.isComposite()) {
/* 16 */       CompositeImage ci = (CompositeImage)imp;
/* 17 */       if (ci.getMode() != 1) {
/* 18 */         ci.setMode(1);
/* 19 */         ci.updateAndDraw();
/*    */       }
/* 21 */       if (!IJ.isMacro()) IJ.run("Channels Tool...");
/* 22 */       return;
/*    */     }
/* 24 */     String mode = modes[0];
/* 25 */     int z = imp.getStackSize();
/* 26 */     int c = imp.getNChannels();
/* 27 */     if (c == 1) {
/* 28 */       c = z;
/* 29 */       imp.setDimensions(c, 1, 1);
/* 30 */       if (c > 7) mode = modes[2];
/*    */     }
/* 32 */     if (imp.getBitDepth() == 24) {
/* 33 */       if (z > 1)
/* 34 */         convertRGBToCompositeStack(imp, arg);
/*    */       else
/* 36 */         convertRGBToCompositeImage(imp);
/* 37 */       if (!IJ.isMacro()) IJ.run("Channels Tool..."); 
/*    */     }
/* 38 */     else if ((c >= 2) || ((IJ.macroRunning()) && (c >= 1))) {
/* 39 */       GenericDialog gd = new GenericDialog("Make Composite");
/* 40 */       gd.addChoice("Display Mode:", modes, mode);
/* 41 */       gd.showDialog();
/* 42 */       if (gd.wasCanceled()) return;
/* 43 */       int index = gd.getNextChoiceIndex();
/* 44 */       CompositeImage ci = new CompositeImage(imp, index + 1);
/* 45 */       ci.show();
/* 46 */       imp.hide();
/* 47 */       if (!IJ.isMacro()) IJ.run("Channels Tool..."); 
/*    */     }
/* 49 */     else { IJ.error("To create a composite, the current image must be\n a stack with at least 2 channels or be in RGB format."); }
/*    */   }
/*    */ 
/*    */   void convertRGBToCompositeImage(ImagePlus imp) {
/* 53 */     ImageWindow win = imp.getWindow();
/* 54 */     Point loc = win != null ? win.getLocation() : null;
/* 55 */     ImagePlus imp2 = new CompositeImage(imp, 1);
/* 56 */     if (loc != null) ImageWindow.setNextLocation(loc);
/* 57 */     imp2.show();
/* 58 */     imp.hide();
/* 59 */     WindowManager.setCurrentWindow(imp2.getWindow());
/*    */   }
/*    */ 
/*    */   void convertRGBToCompositeStack(ImagePlus imp, String arg) {
/* 63 */     int width = imp.getWidth();
/* 64 */     int height = imp.getHeight();
/* 65 */     ImageStack stack1 = imp.getStack();
/* 66 */     int n = stack1.getSize();
/* 67 */     ImageStack stack2 = new ImageStack(width, height);
/* 68 */     for (int i = 0; i < n; i++) {
/* 69 */       ColorProcessor ip = (ColorProcessor)stack1.getProcessor(1);
/* 70 */       stack1.deleteSlice(1);
/* 71 */       byte[] R = new byte[width * height];
/* 72 */       byte[] G = new byte[width * height];
/* 73 */       byte[] B = new byte[width * height];
/* 74 */       ip.getRGB(R, G, B);
/* 75 */       stack2.addSlice(null, R);
/* 76 */       stack2.addSlice(null, G);
/* 77 */       stack2.addSlice(null, B);
/*    */     }
/* 79 */     n *= 3;
/* 80 */     imp.changes = false;
/* 81 */     ImageWindow win = imp.getWindow();
/* 82 */     Point loc = win != null ? win.getLocation() : null;
/* 83 */     ImagePlus imp2 = new ImagePlus(imp.getTitle(), stack2);
/* 84 */     imp2.setDimensions(3, n / 3, 1);
/* 85 */     int mode = (arg != null) && (arg.equals("color")) ? 2 : 1;
/* 86 */     imp2 = new CompositeImage(imp2, mode);
/* 87 */     if (loc != null) ImageWindow.setNextLocation(loc);
/* 88 */     imp2.show();
/* 89 */     imp.changes = false;
/* 90 */     imp.close();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.CompositeConverter
 * JD-Core Version:    0.6.2
 */