/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.ImageStack;
/*    */ 
/*    */ public class StackReverser
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String arg)
/*    */   {
/*  9 */     ImagePlus imp = IJ.getImage();
/* 10 */     if (imp.getStackSize() == 1) {
/* 11 */       IJ.error("Flip Z", "This command requires a stack");
/* 12 */       return;
/*    */     }
/* 14 */     if (imp.isHyperStack()) {
/* 15 */       IJ.error("Flip Z", "This command does not currently work with hyperstacks.");
/* 16 */       return;
/*    */     }
/* 18 */     flipStack(imp);
/*    */   }
/*    */ 
/*    */   public void flipStack(ImagePlus imp) {
/* 22 */     ImageStack stack = imp.getStack();
/* 23 */     ImageStack stack2 = imp.createEmptyStack();
/*    */     int n;
/* 25 */     while ((n = stack.getSize()) > 0) {
/* 26 */       stack2.addSlice(stack.getSliceLabel(n), stack.getProcessor(n));
/* 27 */       stack.deleteLastSlice();
/*    */     }
/* 29 */     imp.setStack(null, stack2);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.StackReverser
 * JD-Core Version:    0.6.2
 */