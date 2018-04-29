/*     */ package ij.text;
/*     */ 
/*     */ import ij.util.Java2;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Vector;
/*     */ 
/*     */ class TextCanvas extends Canvas
/*     */ {
/*     */   TextPanel tp;
/*     */   Font fFont;
/*     */   FontMetrics fMetrics;
/*     */   Graphics gImage;
/*     */   Image iImage;
/*     */   boolean antialiased;
/*     */ 
/*     */   TextCanvas(TextPanel tp)
/*     */   {
/*  16 */     this.tp = tp;
/*  17 */     addMouseListener(tp);
/*  18 */     addMouseMotionListener(tp);
/*  19 */     addKeyListener(tp);
/*  20 */     addMouseWheelListener(tp);
/*     */   }
/*     */ 
/*     */   public void setBounds(int x, int y, int width, int height) {
/*  24 */     super.setBounds(x, y, width, height);
/*  25 */     this.tp.adjustVScroll();
/*  26 */     this.tp.adjustHScroll();
/*  27 */     this.iImage = null;
/*     */   }
/*     */ 
/*     */   public void update(Graphics g) {
/*  31 */     paint(g);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g) {
/*  35 */     if ((this.tp == null) || (g == null)) return;
/*  36 */     Dimension d = getSize();
/*  37 */     int iWidth = d.width;
/*  38 */     int iHeight = d.height;
/*     */ 
/*  40 */     if ((iWidth <= 0) || (iHeight <= 0)) return;
/*  41 */     g.setColor(Color.lightGray);
/*  42 */     if (this.iImage == null)
/*  43 */       makeImage(iWidth, iHeight);
/*  44 */     if ((this.tp.iRowHeight == 0) || ((this.tp.iColWidth[0] == 0) && (this.tp.iRowCount > 0))) {
/*  45 */       this.tp.iRowHeight = (this.fMetrics.getHeight() + 2);
/*  46 */       for (int i = 0; i < this.tp.iColCount; i++)
/*  47 */         calcAutoWidth(i);
/*  48 */       this.tp.adjustHScroll();
/*  49 */       this.tp.adjustVScroll();
/*     */     }
/*  51 */     this.gImage.setColor(Color.white);
/*  52 */     this.gImage.fillRect(0, 0, iWidth, iHeight);
/*  53 */     if (this.tp.headings)
/*  54 */       drawColumnLabels(iWidth);
/*  55 */     int y = this.tp.iRowHeight + 1 - this.tp.iY;
/*  56 */     int j = 0;
/*  57 */     while (y < this.tp.iRowHeight + 1) {
/*  58 */       j++;
/*  59 */       y += this.tp.iRowHeight;
/*     */     }
/*  61 */     this.tp.iFirstRow = j;
/*  62 */     y = this.tp.iRowHeight + 1;
/*  63 */     for (; (y < iHeight) && (j < this.tp.iRowCount); y += this.tp.iRowHeight) {
/*  64 */       int x = -this.tp.iX;
/*  65 */       for (int i = 0; i < this.tp.iColCount; i++) {
/*  66 */         int w = this.tp.iColWidth[i];
/*  67 */         Color b = Color.white; Color t = Color.black;
/*  68 */         if ((j >= this.tp.selStart) && (j <= this.tp.selEnd)) {
/*  69 */           int w2 = w;
/*  70 */           if (this.tp.iColCount == 1)
/*  71 */             w2 = iWidth;
/*  72 */           b = Color.black;
/*  73 */           t = Color.white;
/*  74 */           this.gImage.setColor(b);
/*  75 */           this.gImage.fillRect(x, y, w2 - 1, this.tp.iRowHeight);
/*     */         }
/*  77 */         this.gImage.setColor(t);
/*  78 */         char[] chars = getChars(i, j);
/*  79 */         if (chars != null)
/*  80 */           this.gImage.drawChars(chars, 0, chars.length, x + 2, y + this.tp.iRowHeight - 5);
/*  81 */         x += w;
/*     */       }
/*  63 */       j++;
/*     */     }
/*     */ 
/*  84 */     if (this.iImage != null)
/*  85 */       g.drawImage(this.iImage, 0, 0, null);
/*     */   }
/*     */ 
/*     */   void makeImage(int iWidth, int iHeight) {
/*  89 */     this.iImage = createImage(iWidth, iHeight);
/*  90 */     if (this.gImage != null)
/*  91 */       this.gImage.dispose();
/*  92 */     this.gImage = this.iImage.getGraphics();
/*  93 */     this.gImage.setFont(this.fFont);
/*  94 */     Java2.setAntialiasedText(this.gImage, this.antialiased);
/*  95 */     if (this.fMetrics == null)
/*  96 */       this.fMetrics = this.gImage.getFontMetrics();
/*     */   }
/*     */ 
/*     */   void drawColumnLabels(int iWidth) {
/* 100 */     this.gImage.setColor(Color.darkGray);
/* 101 */     this.gImage.drawLine(0, this.tp.iRowHeight, iWidth, this.tp.iRowHeight);
/* 102 */     int x = -this.tp.iX;
/* 103 */     for (int i = 0; i < this.tp.iColCount; i++) {
/* 104 */       int w = this.tp.iColWidth[i];
/* 105 */       this.gImage.setColor(Color.lightGray);
/* 106 */       this.gImage.fillRect(x + 1, 0, w, this.tp.iRowHeight);
/* 107 */       this.gImage.setColor(Color.black);
/* 108 */       if (this.tp.sColHead[i] != null)
/* 109 */         this.gImage.drawString(this.tp.sColHead[i], x + 2, this.tp.iRowHeight - 5);
/* 110 */       if (this.tp.iColCount > 1) {
/* 111 */         this.gImage.setColor(Color.darkGray);
/* 112 */         this.gImage.drawLine(x + w - 1, 0, x + w - 1, this.tp.iRowHeight - 1);
/* 113 */         this.gImage.setColor(Color.white);
/* 114 */         this.gImage.drawLine(x + w, 0, x + w, this.tp.iRowHeight - 1);
/*     */       }
/* 116 */       x += w;
/*     */     }
/* 118 */     this.gImage.setColor(Color.lightGray);
/* 119 */     this.gImage.fillRect(0, 0, 1, this.tp.iRowHeight);
/* 120 */     this.gImage.fillRect(x + 1, 0, iWidth - x, this.tp.iRowHeight);
/*     */ 
/* 122 */     this.gImage.setColor(Color.darkGray);
/* 123 */     this.gImage.drawLine(0, 0, iWidth, 0);
/*     */   }
/*     */ 
/*     */   char[] getChars(int column, int row) {
/* 127 */     if (this.tp == null) return null;
/* 128 */     if (row >= this.tp.vData.size())
/* 129 */       return null;
/* 130 */     char[] chars = (char[])this.tp.vData.elementAt(row);
/* 131 */     if (chars.length == 0) {
/* 132 */       return null;
/*     */     }
/* 134 */     if (this.tp.iColCount == 1)
/*     */     {
/* 139 */       return chars;
/*     */     }
/*     */ 
/* 142 */     int start = 0;
/* 143 */     int tabs = 0;
/* 144 */     int length = chars.length;
/*     */ 
/* 146 */     while (column > tabs) {
/* 147 */       if (chars[start] == '\t')
/* 148 */         tabs++;
/* 149 */       start++;
/* 150 */       if (start >= length)
/* 151 */         return null;
/*     */     }
/* 153 */     if ((start < 0) || (start >= chars.length)) {
/* 154 */       System.out.println("start=" + start + ", chars.length=" + chars.length);
/* 155 */       return null;
/*     */     }
/* 157 */     if (chars[start] == '\t') {
/* 158 */       return null;
/*     */     }
/* 160 */     int end = start;
/* 161 */     while ((chars[end] != '\t') && (end < length - 1))
/* 162 */       end++;
/* 163 */     if (chars[end] == '\t') {
/* 164 */       end--;
/*     */     }
/* 166 */     char[] chars2 = new char[end - start + 1];
/* 167 */     int i = 0; for (int j = start; i < chars2.length; j++) {
/* 168 */       chars2[i] = chars[j];
/*     */ 
/* 167 */       i++;
/*     */     }
/*     */ 
/* 170 */     return chars2;
/*     */   }
/*     */ 
/*     */   void calcAutoWidth(int column) {
/* 174 */     if ((this.tp.sColHead == null) || (column >= this.tp.iColWidth.length) || (this.gImage == null))
/* 175 */       return;
/* 176 */     if (this.fMetrics == null)
/* 177 */       this.fMetrics = this.gImage.getFontMetrics();
/* 178 */     int w = 15;
/*     */     int maxRows;
/*     */     int maxRows;
/* 180 */     if (this.tp.iColCount == 1) {
/* 181 */       maxRows = 100;
/*     */     } else {
/* 183 */       maxRows = 20;
/* 184 */       if ((column == 0) && (this.tp.sColHead[0].equals(" "))) {
/* 185 */         w += 5;
/*     */       } else {
/* 187 */         char[] chars = this.tp.sColHead[column].toCharArray();
/* 188 */         w = Math.max(w, this.fMetrics.charsWidth(chars, 0, chars.length));
/*     */       }
/*     */     }
/* 191 */     int rowCount = Math.min(this.tp.iRowCount, maxRows);
/* 192 */     for (int row = 0; row < rowCount; row++) {
/* 193 */       char[] chars = getChars(column, row);
/* 194 */       if (chars != null) {
/* 195 */         w = Math.max(w, this.fMetrics.charsWidth(chars, 0, chars.length));
/*     */       }
/*     */     }
/* 198 */     char[] chars = this.tp.iRowCount > 0 ? getChars(column, this.tp.iRowCount - 1) : null;
/* 199 */     if (chars != null)
/* 200 */       w = Math.max(w, this.fMetrics.charsWidth(chars, 0, chars.length));
/* 201 */     this.tp.iColWidth[column] = (w + 15);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.text.TextCanvas
 * JD-Core Version:    0.6.2
 */