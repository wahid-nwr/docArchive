/*      */ package ij.plugin.frame;
/*      */ 
/*      */ import ij.IJ;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Label;
/*      */ 
/*      */ class TrimmedLabel extends Label
/*      */ {
/* 1238 */   int trim = IJ.isMacOSX() ? 0 : 6;
/*      */ 
/*      */   public TrimmedLabel(String title) {
/* 1241 */     super(title);
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize() {
/* 1245 */     return new Dimension(super.getMinimumSize().width, super.getMinimumSize().height - this.trim);
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize() {
/* 1249 */     return getMinimumSize();
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.TrimmedLabel
 * JD-Core Version:    0.6.2
 */