/*     */ package ij.process;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class DownsizeTable
/*     */ {
/*     */   public final int kernelSize;
/*     */   public final int srcStart;
/*     */   public final int srcEnd;
/*     */   public final int[] indices;
/*     */   public final float[] weights;
/*  38 */   private static final int[] kernelSizes = { 1, 2, 4 };
/*     */   private final int srcOrigin;
/*     */   private final int srcLength;
/*     */   private final double scale;
/*     */   private final int interpolationMethod;
/*     */   private static final int UNUSED = -1;
/*     */ 
/*     */   DownsizeTable(int srcSize, int srcOrigin, int srcLength, int dstSize, int interpolationMethod)
/*     */   {
/*  56 */     this.srcOrigin = srcOrigin;
/*  57 */     this.srcLength = srcLength;
/*  58 */     this.interpolationMethod = interpolationMethod;
/*  59 */     this.scale = (srcLength / dstSize);
/*  60 */     this.kernelSize = kernelSizes[interpolationMethod];
/*  61 */     int srcStartUncorr = (int)Math.ceil(1.0E-08D + srcIndex(-0.5D * this.kernelSize));
/*  62 */     this.srcStart = (srcStartUncorr < 0 ? 0 : srcStartUncorr);
/*  63 */     int srcEndUncorr = (int)Math.floor(1.0E-08D + srcIndex(dstSize - 1 + 0.5D * this.kernelSize));
/*  64 */     this.srcEnd = (srcEndUncorr >= srcSize ? srcSize - 1 : srcEndUncorr);
/*  65 */     int arraySize = (this.srcEnd - this.srcStart + 1) * this.kernelSize;
/*  66 */     this.indices = new int[arraySize];
/*  67 */     this.weights = new float[arraySize];
/*  68 */     Arrays.fill(this.indices, -1);
/*     */ 
/*  71 */     for (int dst = 0; dst < dstSize; dst++) {
/*  72 */       double sum = 0.0D;
/*  73 */       int lowestS = (int)Math.ceil(1.0E-08D + srcIndex(dst - 0.5D * this.kernelSize));
/*  74 */       int highestS = (int)Math.floor(-1.0E-08D + srcIndex(dst + 0.5D * this.kernelSize));
/*  75 */       for (int src = lowestS; src <= highestS; src++)
/*     */       {
/*  78 */         int s = src >= srcSize ? srcSize - 1 : src < 0 ? 0 : src;
/*  79 */         int p = (s - this.srcStart) * this.kernelSize;
/*     */ 
/*  81 */         while ((this.indices[p] != -1) && (this.indices[p] != dst)) {
/*  82 */           p++;
/*     */         }
/*  84 */         this.indices[p] = dst;
/*  85 */         float weight = kernel(dst - dstIndex(src));
/*  86 */         sum += weight;
/*  87 */         this.weights[p] += weight;
/*     */       }
/*     */ 
/*  91 */       int iStart = (lowestS - this.srcStart) * this.kernelSize;
/*  92 */       if (iStart < 0) iStart = 0;
/*  93 */       int iStop = (highestS - this.srcStart) * this.kernelSize + (this.kernelSize - 1);
/*  94 */       if (iStop >= this.indices.length) iStop = this.indices.length - 1;
/*     */ 
/*  96 */       for (int i = iStart; i <= iStop; i++)
/*  97 */         if (this.indices[i] == dst)
/*  98 */           this.weights[i] = ((float)(this.weights[i] / sum));
/*     */     }
/* 100 */     for (int i = 0; i < this.indices.length; i++)
/* 101 */       if (this.indices[i] == -1)
/* 102 */         this.indices[i] = 0;
/*     */   }
/*     */ 
/*     */   private double srcIndex(double dstIndex)
/*     */   {
/* 111 */     return this.srcOrigin - 0.5D + (dstIndex + 0.5D) * this.scale;
/*     */   }
/*     */ 
/*     */   private double dstIndex(int srcIndex) {
/* 115 */     return (srcIndex - this.srcOrigin + 0.5D) / this.scale - 0.5D;
/*     */   }
/*     */ 
/*     */   protected float kernel(double x) {
/* 119 */     switch (this.interpolationMethod) {
/*     */     case 0:
/* 121 */       return 1.0F;
/*     */     case 1:
/* 123 */       return 1.0F - (float)Math.abs(x);
/*     */     case 2:
/* 125 */       return (float)ImageProcessor.cubic(x);
/*     */     }
/* 127 */     return (0.0F / 0.0F);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.DownsizeTable
 * JD-Core Version:    0.6.2
 */