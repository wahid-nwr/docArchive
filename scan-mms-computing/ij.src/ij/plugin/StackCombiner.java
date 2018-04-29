/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ 
/*     */ public class StackCombiner
/*     */   implements PlugIn
/*     */ {
/*     */   ImagePlus imp1;
/*     */   ImagePlus imp2;
/*     */   static boolean vertical;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  21 */     if (!showDialog())
/*  22 */       return;
/*  23 */     if ((this.imp1.getType() != this.imp2.getType()) || (this.imp1.isHyperStack()) || (this.imp2.isHyperStack())) {
/*  24 */       error(); return;
/*  25 */     }ImageStack stack1 = this.imp1.getStack();
/*  26 */     ImageStack stack2 = this.imp2.getStack();
/*  27 */     ImageStack stack3 = vertical ? combineVertically(stack1, stack2) : combineHorizontally(stack1, stack2);
/*  28 */     this.imp1.changes = false;
/*  29 */     this.imp1.close();
/*  30 */     this.imp2.changes = false;
/*  31 */     this.imp2.close();
/*  32 */     new ImagePlus("Combined Stacks", stack3).show();
/*     */   }
/*     */ 
/*     */   public ImageStack combineHorizontally(ImageStack stack1, ImageStack stack2) {
/*  36 */     int d1 = stack1.getSize();
/*  37 */     int d2 = stack2.getSize();
/*  38 */     int d3 = Math.max(d1, d2);
/*  39 */     int w1 = stack1.getWidth();
/*  40 */     int h1 = stack1.getHeight();
/*  41 */     int w2 = stack2.getWidth();
/*  42 */     int h2 = stack2.getHeight();
/*  43 */     int w3 = w1 + w2;
/*  44 */     int h3 = Math.max(h1, h2);
/*  45 */     ImageStack stack3 = new ImageStack(w3, h3, stack1.getColorModel());
/*  46 */     ImageProcessor ip = stack1.getProcessor(1);
/*     */ 
/*  48 */     Color background = Toolbar.getBackgroundColor();
/*  49 */     for (int i = 1; i <= d3; i++) {
/*  50 */       IJ.showProgress(i / d3);
/*  51 */       ImageProcessor ip3 = ip.createProcessor(w3, h3);
/*  52 */       if (h1 != h2) {
/*  53 */         ip3.setColor(background);
/*  54 */         ip3.fill();
/*     */       }
/*  56 */       if (i <= d1) {
/*  57 */         ip3.insert(stack1.getProcessor(1), 0, 0);
/*  58 */         if (stack2 != stack1)
/*  59 */           stack1.deleteSlice(1);
/*     */       }
/*  61 */       if (i <= d2) {
/*  62 */         ip3.insert(stack2.getProcessor(1), w1, 0);
/*  63 */         stack2.deleteSlice(1);
/*     */       }
/*  65 */       stack3.addSlice(null, ip3);
/*     */     }
/*  67 */     return stack3;
/*     */   }
/*     */ 
/*     */   public ImageStack combineVertically(ImageStack stack1, ImageStack stack2) {
/*  71 */     int d1 = stack1.getSize();
/*  72 */     int d2 = stack2.getSize();
/*  73 */     int d3 = Math.max(d1, d2);
/*  74 */     int w1 = stack1.getWidth();
/*  75 */     int h1 = stack1.getHeight();
/*  76 */     int w2 = stack2.getWidth();
/*  77 */     int h2 = stack2.getHeight();
/*  78 */     int w3 = Math.max(w1, w2);
/*  79 */     int h3 = h1 + h2;
/*  80 */     ImageStack stack3 = new ImageStack(w3, h3, stack1.getColorModel());
/*  81 */     ImageProcessor ip = stack1.getProcessor(1);
/*     */ 
/*  83 */     Color background = Toolbar.getBackgroundColor();
/*  84 */     for (int i = 1; i <= d3; i++) {
/*  85 */       IJ.showProgress(i / d3);
/*  86 */       ImageProcessor ip3 = ip.createProcessor(w3, h3);
/*  87 */       if (w1 != w2) {
/*  88 */         ip3.setColor(background);
/*  89 */         ip3.fill();
/*     */       }
/*  91 */       if (i <= d1) {
/*  92 */         ip3.insert(stack1.getProcessor(1), 0, 0);
/*  93 */         if (stack2 != stack1)
/*  94 */           stack1.deleteSlice(1);
/*     */       }
/*  96 */       if (i <= d2) {
/*  97 */         ip3.insert(stack2.getProcessor(1), 0, h1);
/*  98 */         stack2.deleteSlice(1);
/*     */       }
/* 100 */       stack3.addSlice(null, ip3);
/*     */     }
/* 102 */     return stack3;
/*     */   }
/*     */ 
/*     */   boolean showDialog() {
/* 106 */     int[] wList = WindowManager.getIDList();
/* 107 */     if ((wList == null) || (wList.length < 2)) {
/* 108 */       error();
/* 109 */       return false;
/*     */     }
/* 111 */     String[] titles = new String[wList.length];
/* 112 */     for (int i = 0; i < wList.length; i++) {
/* 113 */       ImagePlus imp = WindowManager.getImage(wList[i]);
/* 114 */       titles[i] = (imp != null ? imp.getTitle() : "");
/*     */     }
/* 116 */     GenericDialog gd = new GenericDialog("Combiner");
/* 117 */     gd.addChoice("Stack1:", titles, titles[0]);
/* 118 */     gd.addChoice("Stack2:", titles, titles[1]);
/* 119 */     gd.addCheckbox("Combine vertically", false);
/* 120 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/image.html#combine");
/* 121 */     gd.showDialog();
/* 122 */     if (gd.wasCanceled())
/* 123 */       return false;
/* 124 */     int[] index = new int[3];
/* 125 */     int index1 = gd.getNextChoiceIndex();
/* 126 */     int index2 = gd.getNextChoiceIndex();
/* 127 */     this.imp1 = WindowManager.getImage(wList[index1]);
/* 128 */     this.imp2 = WindowManager.getImage(wList[index2]);
/* 129 */     vertical = gd.getNextBoolean();
/* 130 */     return true;
/*     */   }
/*     */ 
/*     */   void error()
/*     */   {
/* 135 */     IJ.showMessage("StackCombiner", "This command requires two stacks\nthat are the same data type.");
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.StackCombiner
 * JD-Core Version:    0.6.2
 */