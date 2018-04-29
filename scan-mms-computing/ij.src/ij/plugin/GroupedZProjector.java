/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.gui.GenericDialog;
/*    */ 
/*    */ public class GroupedZProjector
/*    */   implements PlugIn
/*    */ {
/*  9 */   private static int method = 0;
/*    */   private int groupSize;
/*    */ 
/*    */   public void run(String arg)
/*    */   {
/* 13 */     ImagePlus imp = IJ.getImage();
/* 14 */     int size = imp.getStackSize();
/* 15 */     if (size == 1) {
/* 16 */       IJ.error("Z Project", "This command requires a stack");
/* 17 */       return;
/*    */     }
/* 19 */     if (imp.isHyperStack()) {
/* 20 */       new ZProjector().run("");
/* 21 */       return;
/*    */     }
/* 23 */     if (!showDialog(imp)) return;
/* 24 */     ImagePlus imp2 = groupZProject(imp, method, this.groupSize);
/* 25 */     if (imp != null) imp2.show(); 
/*    */   }
/*    */ 
/*    */   public ImagePlus groupZProject(ImagePlus imp, int method, int groupSize)
/*    */   {
/* 29 */     if ((method < 0) || (method >= ZProjector.METHODS.length))
/* 30 */       return null;
/* 31 */     imp.setDimensions(1, groupSize, imp.getStackSize() / groupSize);
/* 32 */     ZProjector zp = new ZProjector(imp);
/* 33 */     zp.setMethod(method);
/* 34 */     zp.setStartSlice(1);
/* 35 */     zp.setStopSlice(groupSize);
/* 36 */     zp.doHyperStackProjection(true);
/* 37 */     return zp.getProjection();
/*    */   }
/*    */ 
/*    */   boolean showDialog(ImagePlus imp) {
/* 41 */     int size = imp.getStackSize();
/* 42 */     GenericDialog gd = new GenericDialog("Z Project");
/* 43 */     gd.addChoice("Projection method", ZProjector.METHODS, ZProjector.METHODS[method]);
/* 44 */     gd.addNumericField("Group size:", size, 0);
/* 45 */     gd.showDialog();
/* 46 */     if (gd.wasCanceled()) return false;
/* 47 */     method = gd.getNextChoiceIndex();
/* 48 */     this.groupSize = ((int)gd.getNextNumber());
/* 49 */     if ((this.groupSize < 1) || (this.groupSize > size) || (size % this.groupSize != 0)) {
/* 50 */       IJ.error("ZProject", "Group size must divide evenly into the stack size.");
/* 51 */       return false;
/*    */     }
/* 53 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.GroupedZProjector
 * JD-Core Version:    0.6.2
 */