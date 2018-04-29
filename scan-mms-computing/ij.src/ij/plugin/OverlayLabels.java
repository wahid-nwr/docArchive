/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.ImagePlus;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.DialogListener;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.gui.Overlay;
/*    */ import ij.util.Tools;
/*    */ import java.awt.AWTEvent;
/*    */ import java.awt.Checkbox;
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class OverlayLabels
/*    */   implements PlugIn, DialogListener
/*    */ {
/* 11 */   private static final String[] fontSizes = { "7", "8", "9", "10", "12", "14", "18", "24", "28", "36", "48", "72" };
/* 12 */   private static Overlay defaultOverlay = new Overlay();
/*    */   private ImagePlus imp;
/*    */   private Overlay overlay;
/*    */   private GenericDialog gd;
/*    */   private boolean showLabels;
/*    */   private boolean showNames;
/*    */   private boolean drawBackgrounds;
/*    */   private String colorName;
/*    */   private int fontSize;
/*    */   private boolean bold;
/*    */ 
/*    */   public void run(String arg)
/*    */   {
/* 24 */     this.imp = WindowManager.getCurrentImage();
/* 25 */     this.overlay = (this.imp != null ? this.imp.getOverlay() : null);
/* 26 */     if (this.overlay == null)
/* 27 */       this.overlay = defaultOverlay;
/* 28 */     showDialog();
/* 29 */     if (!this.gd.wasCanceled()) {
/* 30 */       defaultOverlay.drawLabels(this.overlay.getDrawLabels());
/* 31 */       defaultOverlay.drawNames(this.overlay.getDrawNames());
/* 32 */       defaultOverlay.drawBackgrounds(this.overlay.getDrawBackgrounds());
/* 33 */       defaultOverlay.setLabelColor(this.overlay.getLabelColor());
/* 34 */       defaultOverlay.setLabelFont(this.overlay.getLabelFont());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void showDialog() {
/* 39 */     this.showLabels = this.overlay.getDrawLabels();
/* 40 */     this.showNames = this.overlay.getDrawNames();
/* 41 */     this.drawBackgrounds = this.overlay.getDrawBackgrounds();
/* 42 */     this.colorName = Colors.getColorName(this.overlay.getLabelColor(), "white");
/* 43 */     this.fontSize = 12;
/* 44 */     Font font = this.overlay.getLabelFont();
/* 45 */     if (font != null) {
/* 46 */       this.fontSize = font.getSize();
/* 47 */       this.bold = (font.getStyle() == 1);
/*    */     }
/* 49 */     this.gd = new GenericDialog("Labels");
/* 50 */     this.gd.addChoice("Color:", Colors.colors, this.colorName);
/* 51 */     this.gd.addChoice("Font size:", fontSizes, "" + this.fontSize);
/* 52 */     this.gd.addCheckbox("Show labels", this.showLabels);
/* 53 */     this.gd.addCheckbox("Use names as labels", this.showNames);
/* 54 */     this.gd.addCheckbox("Draw backgrounds", this.drawBackgrounds);
/* 55 */     this.gd.addCheckbox("Bold", this.bold);
/* 56 */     this.gd.addDialogListener(this);
/* 57 */     this.gd.showDialog();
/*    */   }
/*    */ 
/*    */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/* 61 */     if (gd.wasCanceled()) return false;
/* 62 */     String colorName2 = this.colorName;
/* 63 */     boolean showLabels2 = this.showLabels;
/* 64 */     boolean showNames2 = this.showNames;
/* 65 */     boolean drawBackgrounds2 = this.drawBackgrounds;
/* 66 */     boolean bold2 = this.bold;
/* 67 */     int fontSize2 = this.fontSize;
/* 68 */     this.colorName = gd.getNextChoice();
/* 69 */     this.fontSize = ((int)Tools.parseDouble(gd.getNextChoice(), 12.0D));
/* 70 */     this.showLabels = gd.getNextBoolean();
/* 71 */     this.showNames = gd.getNextBoolean();
/* 72 */     this.drawBackgrounds = gd.getNextBoolean();
/* 73 */     this.bold = gd.getNextBoolean();
/* 74 */     boolean colorChanged = !this.colorName.equals(colorName2);
/* 75 */     boolean sizeChanged = this.fontSize != fontSize2;
/* 76 */     boolean changes = (this.showLabels != showLabels2) || (this.showNames != showNames2) || (this.drawBackgrounds != drawBackgrounds2) || (colorChanged) || (sizeChanged) || (this.bold != bold2);
/*    */ 
/* 79 */     if (changes) {
/* 80 */       if ((this.showNames) || (colorChanged) || (sizeChanged)) {
/* 81 */         this.showLabels = true;
/* 82 */         Vector checkboxes = gd.getCheckboxes();
/* 83 */         ((Checkbox)checkboxes.elementAt(0)).setState(true);
/*    */       }
/* 85 */       this.overlay.drawLabels(this.showLabels);
/* 86 */       this.overlay.drawNames(this.showNames);
/* 87 */       this.overlay.drawBackgrounds(this.drawBackgrounds);
/* 88 */       Color color = Colors.getColor(this.colorName, Color.white);
/* 89 */       this.overlay.setLabelColor(color);
/* 90 */       if ((sizeChanged) || (this.bold) || (this.bold != bold2))
/* 91 */         this.overlay.setLabelFont(new Font("SansSerif", this.bold ? 1 : 0, this.fontSize));
/* 92 */       if ((this.imp != null) && (this.imp.getOverlay() != null))
/* 93 */         this.imp.draw();
/*    */     }
/* 95 */     return true;
/*    */   }
/*    */ 
/*    */   static Overlay createOverlay() {
/* 99 */     return defaultOverlay.duplicate();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.OverlayLabels
 * JD-Core Version:    0.6.2
 */