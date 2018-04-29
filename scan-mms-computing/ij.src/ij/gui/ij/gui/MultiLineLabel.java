/*    */ package ij.gui;
/*    */ 
/*    */ import ij.IJ;
/*    */ import java.awt.Canvas;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Font;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.RenderingHints;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class MultiLineLabel extends Canvas
/*    */ {
/*    */   String[] lines;
/*    */   int num_lines;
/* 10 */   int margin_width = 6;
/* 11 */   int margin_height = 6;
/*    */   int line_height;
/*    */   int line_ascent;
/*    */   int[] line_widths;
/*    */   int min_width;
/*    */   int max_width;
/*    */ 
/*    */   public MultiLineLabel(String label)
/*    */   {
/* 19 */     this(label, 0);
/*    */   }
/*    */ 
/*    */   public MultiLineLabel(String label, int minimumWidth)
/*    */   {
/* 24 */     StringTokenizer t = new StringTokenizer(label, "\n");
/* 25 */     this.num_lines = t.countTokens();
/* 26 */     this.lines = new String[this.num_lines];
/* 27 */     this.line_widths = new int[this.num_lines];
/* 28 */     for (int i = 0; i < this.num_lines; i++) this.lines[i] = t.nextToken();
/* 29 */     this.min_width = minimumWidth;
/*    */   }
/*    */ 
/*    */   protected void measure()
/*    */   {
/* 35 */     FontMetrics fm = getFontMetrics(getFont());
/*    */ 
/* 37 */     if (fm == null) return;
/*    */ 
/* 39 */     this.line_height = fm.getHeight();
/* 40 */     this.line_ascent = fm.getAscent();
/* 41 */     this.max_width = 0;
/* 42 */     for (int i = 0; i < this.num_lines; i++) {
/* 43 */       this.line_widths[i] = fm.stringWidth(this.lines[i]);
/* 44 */       if (this.line_widths[i] > this.max_width) this.max_width = this.line_widths[i];
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setFont(Font f)
/*    */   {
/* 50 */     super.setFont(f);
/* 51 */     measure();
/* 52 */     repaint();
/*    */   }
/*    */ 
/*    */   public void addNotify()
/*    */   {
/* 62 */     super.addNotify();
/* 63 */     measure();
/*    */   }
/*    */ 
/*    */   public Dimension getPreferredSize()
/*    */   {
/* 70 */     return new Dimension(Math.max(this.min_width, this.max_width + 2 * this.margin_width), this.num_lines * this.line_height + 2 * this.margin_height);
/*    */   }
/*    */ 
/*    */   public Dimension getMinimumSize()
/*    */   {
/* 78 */     return new Dimension(Math.max(this.min_width, this.max_width), this.num_lines * this.line_height);
/*    */   }
/*    */ 
/*    */   public void paint(Graphics g)
/*    */   {
/* 84 */     Dimension d = getSize();
/* 85 */     if (!IJ.isLinux()) setAntialiasedText(g);
/* 86 */     int y = this.line_ascent + (d.height - this.num_lines * this.line_height) / 2;
/* 87 */     for (int i = 0; i < this.num_lines; y += this.line_height) {
/* 88 */       int x = this.margin_width;
/* 89 */       g.drawString(this.lines[i], x, y);
/*    */ 
/* 87 */       i++;
/*    */     }
/*    */   }
/*    */ 
/*    */   void setAntialiasedText(Graphics g)
/*    */   {
/* 94 */     Graphics2D g2d = (Graphics2D)g;
/* 95 */     g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.MultiLineLabel
 * JD-Core Version:    0.6.2
 */