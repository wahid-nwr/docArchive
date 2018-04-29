/*     */ package ij.process;
/*     */ 
/*     */ import ij.IJ;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class PolygonFiller
/*     */ {
/*  10 */   int BLACK = -16777216; int WHITE = -1;
/*     */   int edges;
/*     */   int activeEdges;
/*     */   int[] x;
/*     */   int[] y;
/*     */   int n;
/*     */   double[] ex;
/*     */   int[] ey1;
/*     */   int[] ey2;
/*     */   double[] eslope;
/*     */   int[] sedge;
/*     */   int[] aedge;
/*     */ 
/*     */   public PolygonFiller()
/*     */   {
/*     */   }
/*     */ 
/*     */   public PolygonFiller(int[] x, int[] y, int n)
/*     */   {
/*  37 */     setPolygon(x, y, n);
/*     */   }
/*     */ 
/*     */   public void setPolygon(int[] x, int[] y, int n)
/*     */   {
/*  42 */     this.x = x;
/*  43 */     this.y = y;
/*  44 */     this.n = n;
/*     */   }
/*     */ 
/*     */   void allocateArrays(int n) {
/*  48 */     if ((this.ex == null) || (n > this.ex.length)) {
/*  49 */       this.ex = new double[n];
/*  50 */       this.ey1 = new int[n];
/*  51 */       this.ey2 = new int[n];
/*  52 */       this.sedge = new int[n];
/*  53 */       this.aedge = new int[n];
/*  54 */       this.eslope = new double[n];
/*     */     }
/*     */   }
/*     */ 
/*     */   void buildEdgeTable(int[] x, int[] y, int n)
/*     */   {
/*  61 */     this.edges = 0;
/*  62 */     for (int i = 0; i < n; i++) {
/*  63 */       int iplus1 = i == n - 1 ? 0 : i + 1;
/*  64 */       int y1 = y[i]; int y2 = y[iplus1];
/*  65 */       int x1 = x[i]; int x2 = x[iplus1];
/*  66 */       if (y1 != y2)
/*     */       {
/*  68 */         if (y1 > y2) {
/*  69 */           int tmp = y1;
/*  70 */           y1 = y2; y2 = tmp;
/*  71 */           tmp = x1;
/*  72 */           x1 = x2; x2 = tmp;
/*     */         }
/*  74 */         double slope = (x2 - x1) / (y2 - y1);
/*  75 */         this.ex[this.edges] = (x1 + slope / 2.0D);
/*  76 */         this.ey1[this.edges] = y1;
/*  77 */         this.ey2[this.edges] = y2;
/*  78 */         this.eslope[this.edges] = slope;
/*  79 */         this.edges += 1;
/*     */       }
/*     */     }
/*  81 */     for (int i = 0; i < this.edges; i++)
/*  82 */       this.sedge[i] = i;
/*  83 */     this.activeEdges = 0;
/*     */   }
/*     */ 
/*     */   void addToSortedTable(int edge)
/*     */   {
/*  91 */     int index = 0;
/*  92 */     while ((index < this.edges) && (this.ey1[this.edges] > this.ey1[this.sedge[index]])) {
/*  93 */       index++;
/*     */     }
/*  95 */     for (int i = this.edges - 1; i >= index; i--) {
/*  96 */       this.sedge[(i + 1)] = this.sedge[i];
/*     */     }
/*     */ 
/*  99 */     this.sedge[index] = this.edges;
/*     */   }
/*     */ 
/*     */   public void fill(ImageProcessor ip, Rectangle r)
/*     */   {
/* 104 */     ip.fill(getMask(r.width, r.height));
/*     */   }
/*     */ 
/*     */   public ImageProcessor getMask(int width, int height)
/*     */   {
/* 109 */     allocateArrays(this.n);
/* 110 */     buildEdgeTable(this.x, this.y, this.n);
/*     */ 
/* 113 */     ImageProcessor mask = new ByteProcessor(width, height);
/* 114 */     byte[] pixels = (byte[])mask.getPixels();
/* 115 */     for (int y = 0; y < height; y++) {
/* 116 */       removeInactiveEdges(y);
/* 117 */       activateEdges(y);
/* 118 */       int offset = y * width;
/* 119 */       for (int i = 0; i < this.activeEdges; i += 2) {
/* 120 */         int x1 = (int)(this.ex[this.aedge[i]] + 0.5D);
/* 121 */         if (x1 < 0) x1 = 0;
/* 122 */         if (x1 > width) x1 = width;
/* 123 */         int x2 = (int)(this.ex[this.aedge[(i + 1)]] + 0.5D);
/* 124 */         if (x2 < 0) x2 = 0;
/* 125 */         if (x2 > width) x2 = width;
/*     */ 
/* 127 */         for (int x = x1; x < x2; x++)
/* 128 */           pixels[(offset + x)] = -1;
/*     */       }
/* 130 */       updateXCoordinates();
/*     */     }
/* 132 */     return mask;
/*     */   }
/*     */ 
/*     */   void updateXCoordinates()
/*     */   {
/* 138 */     double x1 = -1.797693134862316E+308D;
/* 139 */     boolean sorted = true;
/* 140 */     for (int i = 0; i < this.activeEdges; i++) {
/* 141 */       int index = this.aedge[i];
/* 142 */       double x2 = this.ex[index] + this.eslope[index];
/* 143 */       this.ex[index] = x2;
/* 144 */       if (x2 < x1) sorted = false;
/* 145 */       x1 = x2;
/*     */     }
/* 147 */     if (!sorted)
/* 148 */       sortActiveEdges();
/*     */   }
/*     */ 
/*     */   void sortActiveEdges()
/*     */   {
/* 154 */     for (int i = 0; i < this.activeEdges; i++) {
/* 155 */       int min = i;
/* 156 */       for (int j = i; j < this.activeEdges; j++)
/* 157 */         if (this.ex[this.aedge[j]] < this.ex[this.aedge[min]]) min = j;
/* 158 */       int tmp = this.aedge[min];
/* 159 */       this.aedge[min] = this.aedge[i];
/* 160 */       this.aedge[i] = tmp;
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeInactiveEdges(int y)
/*     */   {
/* 166 */     int i = 0;
/* 167 */     while (i < this.activeEdges) {
/* 168 */       int index = this.aedge[i];
/* 169 */       if ((y < this.ey1[index]) || (y >= this.ey2[index])) {
/* 170 */         for (int j = i; j < this.activeEdges - 1; j++)
/* 171 */           this.aedge[j] = this.aedge[(j + 1)];
/* 172 */         this.activeEdges -= 1;
/*     */       } else {
/* 174 */         i++;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void activateEdges(int y) {
/* 180 */     for (int i = 0; i < this.edges; i++) {
/* 181 */       int edge = this.sedge[i];
/* 182 */       if (y == this.ey1[edge]) {
/* 183 */         int index = 0;
/* 184 */         while ((index < this.activeEdges) && (this.ex[edge] > this.ex[this.aedge[index]]))
/* 185 */           index++;
/* 186 */         for (int j = this.activeEdges - 1; j >= index; j--)
/* 187 */           this.aedge[(j + 1)] = this.aedge[j];
/* 188 */         this.aedge[index] = edge;
/* 189 */         this.activeEdges += 1;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void printEdges()
/*     */   {
/* 196 */     for (int i = 0; i < this.edges; i++) {
/* 197 */       int index = this.sedge[i];
/* 198 */       IJ.log(i + "\t" + this.ex[index] + "  " + this.ey1[index] + "  " + this.ey2[index] + "  " + IJ.d2s(this.eslope[index], 2));
/*     */     }
/*     */   }
/*     */ 
/*     */   void printActiveEdges()
/*     */   {
/* 204 */     for (int i = 0; i < this.activeEdges; i++) {
/* 205 */       int index = this.aedge[i];
/* 206 */       IJ.log(i + "\t" + this.ex[index] + "  " + this.ey1[index] + "  " + this.ey2[index]);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.PolygonFiller
 * JD-Core Version:    0.6.2
 */