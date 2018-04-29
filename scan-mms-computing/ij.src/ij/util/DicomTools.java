/*     */ package ij.util;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.VirtualStack;
/*     */ import ij.plugin.DICOM;
/*     */ import ij.process.ImageProcessor;
/*     */ 
/*     */ public class DicomTools
/*     */ {
/*     */   private static final int MAX_DIGITS = 5;
/*     */   private static String[] sliceLabels;
/*     */ 
/*     */   public static ImageStack sort(ImageStack stack)
/*     */   {
/*  13 */     if (IJ.debugMode) IJ.log("Sorting by DICOM image number");
/*  14 */     if (stack.getSize() == 1) return stack;
/*  15 */     String[] strings = getSortStrings(stack, "0020,0013");
/*  16 */     if (strings == null) return stack;
/*  17 */     StringSorter.sort(strings);
/*  18 */     ImageStack stack2 = null;
/*  19 */     if (stack.isVirtual())
/*  20 */       stack2 = ((VirtualStack)stack).sortDicom(strings, sliceLabels, 5);
/*     */     else
/*  22 */       stack2 = sortStack(stack, strings);
/*  23 */     return stack2 != null ? stack2 : stack;
/*     */   }
/*     */ 
/*     */   private static ImageStack sortStack(ImageStack stack, String[] strings) {
/*  27 */     ImageProcessor ip = stack.getProcessor(1);
/*  28 */     ImageStack stack2 = new ImageStack(ip.getWidth(), ip.getHeight(), ip.getColorModel());
/*  29 */     for (int i = 0; i < stack.getSize(); i++) {
/*  30 */       int slice = (int)Tools.parseDouble(strings[i].substring(strings[i].length() - 5), 0.0D);
/*  31 */       if (slice == 0) return null;
/*  32 */       stack2.addSlice(sliceLabels[(slice - 1)], stack.getPixels(slice));
/*     */     }
/*  34 */     stack2.update(stack.getProcessor(1));
/*  35 */     return stack2;
/*     */   }
/*     */ 
/*     */   private static String[] getSortStrings(ImageStack stack, String tag) {
/*  39 */     double series = getSeriesNumber(getSliceLabel(stack, 1));
/*  40 */     int n = stack.getSize();
/*  41 */     String[] values = new String[n];
/*  42 */     sliceLabels = new String[n];
/*  43 */     for (int i = 1; i <= n; i++) {
/*  44 */       String tags = getSliceLabel(stack, i);
/*  45 */       if (tags == null) return null;
/*  46 */       sliceLabels[(i - 1)] = tags;
/*  47 */       double value = getNumericTag(tags, tag);
/*  48 */       if (Double.isNaN(value)) {
/*  49 */         if (IJ.debugMode) IJ.log("  " + tag + "  tag missing in slice " + i);
/*  50 */         return null;
/*     */       }
/*  52 */       if (getSeriesNumber(tags) != series) {
/*  53 */         if (IJ.debugMode) IJ.log("  all slices must be part of the same series");
/*  54 */         return null;
/*     */       }
/*  56 */       values[(i - 1)] = (toString(value, 5) + toString(i, 5));
/*     */     }
/*  58 */     return values;
/*     */   }
/*     */ 
/*     */   private static String toString(double value, int width) {
/*  62 */     String s = "       " + IJ.d2s(value, 0);
/*  63 */     return s.substring(s.length() - 5);
/*     */   }
/*     */ 
/*     */   private static String getSliceLabel(ImageStack stack, int n) {
/*  67 */     String info = stack.getSliceLabel(n);
/*  68 */     if (((info == null) || (info.length() < 100)) && (stack.isVirtual())) {
/*  69 */       String dir = ((VirtualStack)stack).getDirectory();
/*  70 */       String name = ((VirtualStack)stack).getFileName(n);
/*  71 */       DICOM reader = new DICOM();
/*  72 */       info = reader.getInfo(dir + name);
/*  73 */       if (info != null)
/*  74 */         info = name + "\n" + info;
/*     */     }
/*  76 */     return info;
/*     */   }
/*     */ 
/*     */   public static double getVoxelDepth(ImageStack stack)
/*     */   {
/*  82 */     if (stack.isVirtual()) stack.getProcessor(1);
/*  83 */     String pos0 = getTag(stack.getSliceLabel(1), "0020,0032");
/*  84 */     String posn = null;
/*  85 */     double voxelDepth = -1.0D;
/*  86 */     if (pos0 != null) {
/*  87 */       String[] xyz = pos0.split("\\\\");
/*  88 */       if (xyz.length != 3) return voxelDepth;
/*  89 */       double z0 = Double.parseDouble(xyz[2]);
/*  90 */       if (stack.isVirtual()) stack.getProcessor(stack.getSize());
/*  91 */       posn = getTag(stack.getSliceLabel(stack.getSize()), "0020,0032");
/*  92 */       if (posn == null) return voxelDepth;
/*  93 */       xyz = posn.split("\\\\");
/*  94 */       if (xyz.length != 3) return voxelDepth;
/*  95 */       double zn = Double.parseDouble(xyz[2]);
/*  96 */       voxelDepth = Math.abs((zn - z0) / (stack.getSize() - 1));
/*     */     }
/*  98 */     if (IJ.debugMode) IJ.log("DicomTools.getVoxelDepth: " + voxelDepth + "  " + pos0 + "  " + posn);
/*  99 */     return voxelDepth;
/*     */   }
/*     */ 
/*     */   public static String getTag(ImagePlus imp, String id)
/*     */   {
/* 105 */     String metadata = null;
/* 106 */     if (imp.getStackSize() > 1) {
/* 107 */       ImageStack stack = imp.getStack();
/* 108 */       String label = stack.getSliceLabel(imp.getCurrentSlice());
/* 109 */       if ((label != null) && (label.indexOf('\n') > 0)) metadata = label;
/*     */     }
/* 111 */     if (metadata == null)
/* 112 */       metadata = (String)imp.getProperty("Info");
/* 113 */     return getTag(metadata, id);
/*     */   }
/*     */ 
/*     */   private static double getSeriesNumber(String tags) {
/* 117 */     double series = getNumericTag(tags, "0020,0011");
/* 118 */     if (Double.isNaN(series)) series = 0.0D;
/* 119 */     return series;
/*     */   }
/*     */ 
/*     */   private static double getNumericTag(String hdr, String tag) {
/* 123 */     String value = getTag(hdr, tag);
/* 124 */     if (value == null) return (0.0D / 0.0D);
/* 125 */     int index3 = value.indexOf("\\");
/* 126 */     if (index3 > 0)
/* 127 */       value = value.substring(0, index3);
/* 128 */     return Tools.parseDouble(value);
/*     */   }
/*     */ 
/*     */   private static String getTag(String hdr, String tag) {
/* 132 */     if (hdr == null) return null;
/* 133 */     int index1 = hdr.indexOf(tag);
/* 134 */     if (index1 == -1) return null;
/*     */ 
/* 136 */     if (hdr.charAt(index1 + 11) == '>')
/*     */     {
/* 138 */       index1 = hdr.indexOf(tag, index1 + 10);
/* 139 */       if (index1 == -1) return null;
/*     */     }
/* 141 */     index1 = hdr.indexOf(":", index1);
/* 142 */     if (index1 == -1) return null;
/* 143 */     int index2 = hdr.indexOf("\n", index1);
/* 144 */     String value = hdr.substring(index1 + 1, index2);
/* 145 */     return value;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.util.DicomTools
 * JD-Core Version:    0.6.2
 */