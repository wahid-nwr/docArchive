/*    */ package ij.gui;
/*    */ 
/*    */ import ij.util.Tools;
/*    */ import java.awt.Color;
/*    */ import java.awt.Frame;
/*    */ import java.awt.Insets;
/*    */ import java.awt.Scrollbar;
/*    */ import java.awt.TextField;
/*    */ import java.awt.event.AdjustmentEvent;
/*    */ import java.awt.event.AdjustmentListener;
/*    */ import java.awt.event.TextEvent;
/*    */ import java.awt.event.TextListener;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class ColorChooser
/*    */   implements TextListener, AdjustmentListener
/*    */ {
/*    */   Vector colors;
/*    */   Vector sliders;
/*    */   ColorPanel panel;
/*    */   Color initialColor;
/*    */   int red;
/*    */   int green;
/*    */   int blue;
/*    */   boolean useHSB;
/*    */   String title;
/*    */   Frame frame;
/*    */ 
/*    */   public ColorChooser(String title, Color initialColor, boolean useHSB)
/*    */   {
/* 22 */     this(title, initialColor, useHSB, null);
/*    */   }
/*    */ 
/*    */   public ColorChooser(String title, Color initialColor, boolean useHSB, Frame frame) {
/* 26 */     this.title = title;
/* 27 */     if (initialColor == null) initialColor = Color.black;
/* 28 */     this.initialColor = initialColor;
/* 29 */     this.red = initialColor.getRed();
/* 30 */     this.green = initialColor.getGreen();
/* 31 */     this.blue = initialColor.getBlue();
/* 32 */     this.useHSB = useHSB;
/* 33 */     this.frame = frame;
/*    */   }
/*    */ 
/*    */   public Color getColor()
/*    */   {
/* 38 */     GenericDialog gd = this.frame != null ? new GenericDialog(this.title, this.frame) : new GenericDialog(this.title);
/* 39 */     gd.addSlider("Red:", 0.0D, 255.0D, this.red);
/* 40 */     gd.addSlider("Green:", 0.0D, 255.0D, this.green);
/* 41 */     gd.addSlider("Blue:", 0.0D, 255.0D, this.blue);
/* 42 */     this.panel = new ColorPanel(this.initialColor);
/* 43 */     gd.addPanel(this.panel, 10, new Insets(10, 0, 0, 0));
/* 44 */     this.colors = gd.getNumericFields();
/* 45 */     for (int i = 0; i < this.colors.size(); i++)
/* 46 */       ((TextField)this.colors.elementAt(i)).addTextListener(this);
/* 47 */     this.sliders = gd.getSliders();
/* 48 */     for (int i = 0; i < this.sliders.size(); i++)
/* 49 */       ((Scrollbar)this.sliders.elementAt(i)).addAdjustmentListener(this);
/* 50 */     gd.showDialog();
/* 51 */     if (gd.wasCanceled()) return null;
/* 52 */     int red = (int)gd.getNextNumber();
/* 53 */     int green = (int)gd.getNextNumber();
/* 54 */     int blue = (int)gd.getNextNumber();
/* 55 */     return new Color(red, green, blue);
/*    */   }
/*    */ 
/*    */   public void textValueChanged(TextEvent e) {
/* 59 */     int red = (int)Tools.parseDouble(((TextField)this.colors.elementAt(0)).getText());
/* 60 */     int green = (int)Tools.parseDouble(((TextField)this.colors.elementAt(1)).getText());
/* 61 */     int blue = (int)Tools.parseDouble(((TextField)this.colors.elementAt(2)).getText());
/* 62 */     if (red < 0) red = 0; if (red > 255) red = 255;
/* 63 */     if (green < 0) green = 0; if (green > 255) green = 255;
/* 64 */     if (blue < 0) blue = 0; if (blue > 255) blue = 255;
/* 65 */     this.panel.setColor(new Color(red, green, blue));
/* 66 */     this.panel.repaint();
/*    */   }
/*    */ 
/*    */   public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
/* 70 */     Object source = e.getSource();
/*    */     TextField tf;
/* 71 */     for (int i = 0; i < this.sliders.size(); i++)
/* 72 */       if (source == this.sliders.elementAt(i)) {
/* 73 */         Scrollbar sb = (Scrollbar)source;
/* 74 */         tf = (TextField)this.colors.elementAt(i);
/*    */       }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.ColorChooser
 * JD-Core Version:    0.6.2
 */