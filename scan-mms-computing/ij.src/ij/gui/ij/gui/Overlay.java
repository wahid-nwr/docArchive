/*     */ package ij.gui;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Overlay
/*     */ {
/*     */   private Vector list;
/*     */   private boolean label;
/*     */   private boolean drawNames;
/*     */   private boolean drawBackgrounds;
/*     */   private Color labelColor;
/*     */   private Font labelFont;
/*     */ 
/*     */   public Overlay()
/*     */   {
/*  18 */     this.list = new Vector();
/*     */   }
/*     */ 
/*     */   public Overlay(Roi roi)
/*     */   {
/*  23 */     this.list = new Vector();
/*  24 */     this.list.add(roi);
/*     */   }
/*     */ 
/*     */   public void add(Roi roi)
/*     */   {
/*  29 */     this.list.add(roi);
/*     */   }
/*     */ 
/*     */   public void addElement(Roi roi)
/*     */   {
/*  49 */     this.list.add(roi);
/*     */   }
/*     */ 
/*     */   public void remove(int index)
/*     */   {
/*  54 */     this.list.remove(index);
/*     */   }
/*     */ 
/*     */   public void remove(Roi roi)
/*     */   {
/*  59 */     this.list.remove(roi);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  64 */     this.list.clear();
/*     */   }
/*     */ 
/*     */   public Roi get(int index)
/*     */   {
/*  69 */     return (Roi)this.list.get(index);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  74 */     return this.list.size();
/*     */   }
/*     */ 
/*     */   public Roi[] toArray()
/*     */   {
/*  79 */     Roi[] array = new Roi[this.list.size()];
/*  80 */     return (Roi[])this.list.toArray(array);
/*     */   }
/*     */ 
/*     */   public void setStrokeColor(Color color)
/*     */   {
/*  85 */     Roi[] rois = toArray();
/*  86 */     for (int i = 0; i < rois.length; i++)
/*  87 */       rois[i].setStrokeColor(color);
/*     */   }
/*     */ 
/*     */   public void setFillColor(Color color)
/*     */   {
/*  92 */     Roi[] rois = toArray();
/*  93 */     for (int i = 0; i < rois.length; i++)
/*  94 */       rois[i].setFillColor(color);
/*     */   }
/*     */ 
/*     */   public void translate(int dx, int dy)
/*     */   {
/*  99 */     Roi[] rois = toArray();
/* 100 */     for (int i = 0; i < rois.length; i++) {
/* 101 */       Rectangle r = rois[i].getBounds();
/* 102 */       rois[i].setLocation(r.x + dx, r.y + dy);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Overlay duplicate()
/*     */   {
/* 116 */     Roi[] rois = toArray();
/* 117 */     Overlay overlay2 = new Overlay();
/* 118 */     for (int i = 0; i < rois.length; i++)
/* 119 */       overlay2.add((Roi)rois[i].clone());
/* 120 */     overlay2.drawLabels(this.label);
/* 121 */     overlay2.drawNames(this.drawNames);
/* 122 */     overlay2.drawBackgrounds(this.drawBackgrounds);
/* 123 */     overlay2.setLabelColor(this.labelColor);
/* 124 */     overlay2.setLabelFont(this.labelFont);
/* 125 */     return overlay2;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 129 */     return this.list.toString();
/*     */   }
/*     */ 
/*     */   public void drawLabels(boolean b) {
/* 133 */     this.label = b;
/*     */   }
/*     */ 
/*     */   public boolean getDrawLabels() {
/* 137 */     return this.label;
/*     */   }
/*     */ 
/*     */   public void drawNames(boolean b) {
/* 141 */     this.drawNames = b;
/*     */   }
/*     */ 
/*     */   public boolean getDrawNames() {
/* 145 */     return this.drawNames;
/*     */   }
/*     */ 
/*     */   public void drawBackgrounds(boolean b) {
/* 149 */     this.drawBackgrounds = b;
/*     */   }
/*     */ 
/*     */   public boolean getDrawBackgrounds() {
/* 153 */     return this.drawBackgrounds;
/*     */   }
/*     */ 
/*     */   public void setLabelColor(Color c) {
/* 157 */     this.labelColor = c;
/*     */   }
/*     */ 
/*     */   public Color getLabelColor() {
/* 161 */     return this.labelColor;
/*     */   }
/*     */ 
/*     */   public void setLabelFont(Font font) {
/* 165 */     this.labelFont = font;
/*     */   }
/*     */ 
/*     */   public Font getLabelFont()
/*     */   {
/* 171 */     return this.labelFont;
/*     */   }
/*     */   void setVector(Vector v) {
/* 174 */     this.list = v;
/*     */   }
/* 176 */   Vector getVector() { return this.list; }
/*     */ 
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.Overlay
 * JD-Core Version:    0.6.2
 */