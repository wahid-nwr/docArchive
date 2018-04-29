/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.StackWindow;
/*     */ import ij.process.ImageProcessor;
/*     */ 
/*     */ public class SubstackMaker
/*     */   implements PlugIn
/*     */ {
/*  26 */   private static boolean delete = false;
/*     */ 
/*     */   public void run(String arg) {
/*  29 */     ImagePlus imp = IJ.getImage();
/*  30 */     if ((imp.isHyperStack()) || (imp.isComposite())) {
/*  31 */       IJ.error("Make Substack", "This command does not currently work with hyperstacks.");
/*  32 */       return;
/*     */     }
/*  34 */     String userInput = showDialog();
/*  35 */     if (userInput == null)
/*  36 */       return;
/*  37 */     ImagePlus imp2 = makeSubstack(imp, userInput);
/*  38 */     if (imp2 != null)
/*  39 */       imp2.show();
/*     */   }
/*     */ 
/*     */   public ImagePlus makeSubstack(ImagePlus imp, String userInput) {
/*  43 */     String stackTitle = "Substack (" + userInput + ")";
/*  44 */     if (stackTitle.length() > 25) {
/*  45 */       int idxA = stackTitle.indexOf(",", 18);
/*  46 */       int idxB = stackTitle.lastIndexOf(",");
/*  47 */       if ((idxA >= 1) && (idxB >= 1)) {
/*  48 */         String strA = stackTitle.substring(0, idxA);
/*  49 */         String strB = stackTitle.substring(idxB + 1);
/*  50 */         stackTitle = strA + ", ... " + strB;
/*     */       }
/*     */     }
/*  53 */     ImagePlus imp2 = null;
/*     */     try {
/*  55 */       int idx1 = userInput.indexOf("-");
/*  56 */       if (idx1 >= 1) {
/*  57 */         String rngStart = userInput.substring(0, idx1);
/*  58 */         String rngEnd = userInput.substring(idx1 + 1);
/*  59 */         Integer obj = new Integer(rngStart);
/*  60 */         int first = obj.intValue();
/*  61 */         int inc = 1;
/*  62 */         int idx2 = rngEnd.indexOf("-");
/*  63 */         if (idx2 >= 1) {
/*  64 */           String rngEndAndInc = rngEnd;
/*  65 */           rngEnd = rngEndAndInc.substring(0, idx2);
/*  66 */           String rngInc = rngEndAndInc.substring(idx2 + 1);
/*  67 */           obj = new Integer(rngInc);
/*  68 */           inc = obj.intValue();
/*     */         }
/*  70 */         obj = new Integer(rngEnd);
/*  71 */         int last = obj.intValue();
/*  72 */         imp2 = stackRange(imp, first, last, inc, stackTitle);
/*     */       } else {
/*  74 */         int count = 1;
/*  75 */         for (int j = 0; j < userInput.length(); j++) {
/*  76 */           char ch = Character.toLowerCase(userInput.charAt(j));
/*  77 */           if (ch == ',') count++;
/*     */         }
/*  79 */         int[] numList = new int[count];
/*  80 */         for (int i = 0; i < count; i++) {
/*  81 */           int idx2 = userInput.indexOf(",");
/*  82 */           if (idx2 > 0) {
/*  83 */             String num = userInput.substring(0, idx2);
/*  84 */             Integer obj = new Integer(num);
/*  85 */             numList[i] = obj.intValue();
/*  86 */             userInput = userInput.substring(idx2 + 1);
/*     */           }
/*     */           else {
/*  89 */             String num = userInput;
/*  90 */             Integer obj = new Integer(num);
/*  91 */             numList[i] = obj.intValue();
/*     */           }
/*     */         }
/*  94 */         imp2 = stackList(imp, count, numList, stackTitle);
/*     */       }
/*     */     } catch (Exception e) {
/*  97 */       IJ.error("Substack Maker", "Invalid input string:        \n \n  \"" + userInput + "\"");
/*     */     }
/*  99 */     return imp2;
/*     */   }
/*     */ 
/*     */   String showDialog() {
/* 103 */     GenericDialog gd = new GenericDialog("Substack Maker");
/* 104 */     gd.setInsets(10, 45, 0);
/* 105 */     gd.addMessage("Enter a range (e.g. 2-14), a range with increment\n(e.g. 1-100-2) or a list (e.g. 7,9,25,27)");
/* 106 */     gd.addStringField("Slices:", "", 40);
/* 107 */     gd.addCheckbox("Delete slices from original stack", delete);
/* 108 */     gd.showDialog();
/* 109 */     if (gd.wasCanceled()) {
/* 110 */       return null;
/*     */     }
/* 112 */     delete = gd.getNextBoolean();
/* 113 */     return gd.getNextString();
/*     */   }
/*     */ 
/*     */   ImagePlus stackList(ImagePlus imp, int count, int[] numList, String stackTitle)
/*     */     throws Exception
/*     */   {
/* 119 */     ImageStack stack = imp.getStack();
/* 120 */     ImageStack stack2 = null;
/* 121 */     Roi roi = imp.getRoi();
/* 122 */     int i = 0; for (int j = 0; i < count; i++) {
/* 123 */       int currSlice = numList[i] - j;
/* 124 */       ImageProcessor ip2 = stack.getProcessor(currSlice);
/* 125 */       ip2.setRoi(roi);
/* 126 */       ip2 = ip2.crop();
/* 127 */       if (stack2 == null)
/* 128 */         stack2 = new ImageStack(ip2.getWidth(), ip2.getHeight());
/* 129 */       stack2.addSlice(stack.getSliceLabel(currSlice), ip2);
/* 130 */       if (delete) {
/* 131 */         stack.deleteSlice(currSlice);
/* 132 */         j++;
/*     */       }
/*     */     }
/* 135 */     if (delete) {
/* 136 */       imp.setStack(stack);
/*     */ 
/* 138 */       ImageWindow win = imp.getWindow();
/* 139 */       StackWindow swin = (StackWindow)win;
/* 140 */       swin.updateSliceSelector();
/*     */     }
/* 142 */     ImagePlus impSubstack = imp.createImagePlus();
/* 143 */     impSubstack.setStack(stackTitle, stack2);
/* 144 */     impSubstack.setCalibration(imp.getCalibration());
/* 145 */     return impSubstack;
/*     */   }
/*     */ 
/*     */   ImagePlus stackRange(ImagePlus imp, int first, int last, int inc, String title) throws Exception
/*     */   {
/* 150 */     ImageStack stack = imp.getStack();
/* 151 */     ImageStack stack2 = null;
/* 152 */     Roi roi = imp.getRoi();
/* 153 */     int i = first; for (int j = 0; i <= last; i += inc)
/*     */     {
/* 155 */       int currSlice = i - j;
/* 156 */       ImageProcessor ip2 = stack.getProcessor(currSlice);
/* 157 */       ip2.setRoi(roi);
/* 158 */       ip2 = ip2.crop();
/* 159 */       if (stack2 == null)
/* 160 */         stack2 = new ImageStack(ip2.getWidth(), ip2.getHeight());
/* 161 */       stack2.addSlice(stack.getSliceLabel(currSlice), ip2);
/* 162 */       if (delete) {
/* 163 */         stack.deleteSlice(currSlice);
/* 164 */         j++;
/*     */       }
/*     */     }
/* 167 */     if (delete) {
/* 168 */       imp.setStack(stack);
/*     */ 
/* 170 */       ImageWindow win = imp.getWindow();
/* 171 */       StackWindow swin = (StackWindow)win;
/* 172 */       swin.updateSliceSelector();
/*     */     }
/* 174 */     ImagePlus substack = imp.createImagePlus();
/* 175 */     substack.setStack(title, stack2);
/* 176 */     substack.setCalibration(imp.getCalibration());
/* 177 */     return substack;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.SubstackMaker
 * JD-Core Version:    0.6.2
 */