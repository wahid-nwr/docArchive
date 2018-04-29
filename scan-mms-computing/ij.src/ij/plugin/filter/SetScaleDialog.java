/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Button;
/*     */ import java.awt.Label;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.util.Vector;
/*     */ 
/*     */ class SetScaleDialog extends GenericDialog
/*     */ {
/*     */   static final String NO_SCALE = "<no scale>";
/*     */   String initialScale;
/*     */   Button unscaleButton;
/*     */   String length;
/*     */   boolean scaleChanged;
/*     */ 
/*     */   public SetScaleDialog(String title, String scale, String length)
/*     */   {
/* 132 */     super(title);
/* 133 */     this.initialScale = scale;
/* 134 */     this.length = length;
/*     */   }
/*     */ 
/*     */   protected void setup() {
/* 138 */     this.initialScale += "                   ";
/* 139 */     setScale(this.initialScale);
/*     */   }
/*     */ 
/*     */   public void textValueChanged(TextEvent e) {
/* 143 */     Object source = e.getSource();
/* 144 */     if ((source == this.numberField.elementAt(0)) || (source == this.numberField.elementAt(1)))
/* 145 */       this.scaleChanged = true;
/* 146 */     Double d = getValue(((TextField)this.numberField.elementAt(0)).getText());
/* 147 */     if (d == null) {
/* 148 */       setScale("<no scale>"); return;
/* 149 */     }double measured = d.doubleValue();
/* 150 */     d = getValue(((TextField)this.numberField.elementAt(1)).getText());
/* 151 */     if (d == null) {
/* 152 */       setScale("<no scale>"); return;
/* 153 */     }double known = d.doubleValue();
/*     */ 
/* 155 */     String unit = ((TextField)this.stringField.elementAt(0)).getText();
/* 156 */     boolean noUnit = (unit.startsWith("pixel")) || (unit.startsWith("Pixel")) || (unit.equals(""));
/* 157 */     if ((known > 0.0D) && (noUnit) && (e.getSource() == this.numberField.elementAt(1))) {
/* 158 */       unit = "unit";
/* 159 */       ((TextField)this.stringField.elementAt(0)).setText(unit);
/*     */     }
/* 161 */     boolean noScale = (measured <= 0.0D) || (known <= 0.0D) || (noUnit);
/*     */     String theScale;
/*     */     String theScale;
/* 162 */     if (noScale) {
/* 163 */       theScale = "<no scale>";
/*     */     } else {
/* 165 */       double scale = measured / known;
/* 166 */       int digits = Tools.getDecimalPlaces(scale, scale);
/* 167 */       theScale = IJ.d2s(scale, digits) + (scale == 1.0D ? " pixel/" : " pixels/") + unit;
/*     */     }
/* 169 */     setScale(theScale);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 173 */     super.actionPerformed(e);
/* 174 */     if (e.getSource() == this.unscaleButton) {
/* 175 */       ((TextField)this.numberField.elementAt(0)).setText(this.length);
/* 176 */       ((TextField)this.numberField.elementAt(1)).setText("0.00");
/* 177 */       ((TextField)this.numberField.elementAt(2)).setText("1.0");
/* 178 */       ((TextField)this.stringField.elementAt(0)).setText("pixel");
/* 179 */       setScale("<no scale>");
/* 180 */       this.scaleChanged = true;
/* 181 */       if (IJ.isMacOSX()) {
/* 182 */         setVisible(false); setVisible(true);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 187 */   void setScale(String theScale) { ((Label)this.theLabel).setText("Scale: " + theScale); }
/*     */ 
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.SetScaleDialog
 * JD-Core Version:    0.6.2
 */