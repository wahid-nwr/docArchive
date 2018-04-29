/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.ImageStack;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.process.ImageProcessor;
/*    */ 
/*    */ public class StackInserter
/*    */   implements PlugIn
/*    */ {
/*    */   private static int index1;
/*    */   private static int index2;
/*    */   private static int x;
/*    */   private static int y;
/*    */ 
/*    */   public void run(String arg)
/*    */   {
/* 17 */     int[] wList = WindowManager.getIDList();
/* 18 */     if (wList == null) {
/* 19 */       IJ.showMessage("Stack Inserter", "No windows are open.");
/* 20 */       return;
/*    */     }
/* 22 */     if (wList.length == 1) {
/* 23 */       IJ.showMessage("Stack Inserter", "At least two windows must be open, \nincluding at least one stack.");
/* 24 */       return;
/*    */     }
/* 26 */     String[] titles = new String[wList.length];
/* 27 */     for (int i = 0; i < wList.length; i++) {
/* 28 */       ImagePlus imp = WindowManager.getImage(wList[i]);
/* 29 */       if (imp != null)
/* 30 */         titles[i] = imp.getTitle();
/*    */       else
/* 32 */         titles[i] = "";
/*    */     }
/* 34 */     if (index1 >= titles.length) index1 = 0;
/* 35 */     if (index2 >= titles.length) index2 = 0;
/* 36 */     GenericDialog gd = new GenericDialog("Stack Inserter");
/* 37 */     gd.addChoice("Source Image or Stack: ", titles, titles[index1]);
/* 38 */     gd.addChoice("Destination Stack: ", titles, titles[index2]);
/* 39 */     gd.addNumericField("X Location: ", 0.0D, 0);
/* 40 */     gd.addNumericField("Y Location: ", 0.0D, 0);
/* 41 */     gd.showDialog();
/* 42 */     if (gd.wasCanceled())
/* 43 */       return;
/* 44 */     index1 = gd.getNextChoiceIndex();
/* 45 */     index2 = gd.getNextChoiceIndex();
/* 46 */     x = (int)gd.getNextNumber();
/* 47 */     y = (int)gd.getNextNumber();
/* 48 */     String title1 = titles[index1];
/* 49 */     String title2 = titles[index2];
/* 50 */     ImagePlus imp1 = WindowManager.getImage(wList[index1]);
/* 51 */     ImagePlus imp2 = WindowManager.getImage(wList[index2]);
/* 52 */     if (imp1.getType() != imp2.getType()) {
/* 53 */       IJ.showMessage("Stack Inserter", "The source and destination must be the same type.");
/* 54 */       return;
/*    */     }
/* 56 */     if (imp2.getStackSize() == 1) {
/* 57 */       IJ.showMessage("Stack Inserter", "The destination must be a stack.");
/* 58 */       return;
/*    */     }
/* 60 */     if (imp1 == imp2) {
/* 61 */       IJ.showMessage("Stack Inserter", "The source and destination must be different.");
/* 62 */       return;
/*    */     }
/* 64 */     insert(imp1, imp2, x, y);
/*    */   }
/*    */ 
/*    */   public void insert(ImagePlus imp1, ImagePlus imp2, int x, int y) {
/* 68 */     ImageStack stack1 = imp1.getStack();
/* 69 */     ImageStack stack2 = imp2.getStack();
/* 70 */     int size1 = stack1.getSize();
/* 71 */     int size2 = stack2.getSize();
/*    */ 
/* 73 */     for (int i = 1; i <= size2; i++) {
/* 74 */       ImageProcessor ip1 = stack1.getProcessor(i <= size1 ? i : size1);
/* 75 */       ImageProcessor ip2 = stack2.getProcessor(i);
/* 76 */       ip2.insert(ip1, x, y);
/* 77 */       stack2.setPixels(ip2.getPixels(), i);
/*    */     }
/* 79 */     imp2.setStack(null, stack2);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.StackInserter
 * JD-Core Version:    0.6.2
 */