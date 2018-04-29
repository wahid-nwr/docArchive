/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.GenericDialog;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Label;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.util.Vector;
/*     */ 
/*     */ class FolderOpenerDialog extends GenericDialog
/*     */ {
/*     */   ImagePlus imp;
/*     */   int fileCount;
/*     */   boolean eightBits;
/*     */   boolean rgb;
/*     */   String[] list;
/*     */   boolean isRegex;
/*     */ 
/*     */   public FolderOpenerDialog(String title, ImagePlus imp, String[] list)
/*     */   {
/* 455 */     super(title);
/* 456 */     this.imp = imp;
/* 457 */     this.list = list;
/* 458 */     this.fileCount = list.length;
/*     */   }
/*     */ 
/*     */   protected void setup() {
/* 462 */     this.eightBits = ((Checkbox)this.checkbox.elementAt(0)).getState();
/* 463 */     this.rgb = ((Checkbox)this.checkbox.elementAt(1)).getState();
/* 464 */     setStackInfo();
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/*     */   }
/*     */ 
/*     */   public void textValueChanged(TextEvent e) {
/* 471 */     setStackInfo();
/*     */   }
/*     */ 
/*     */   void setStackInfo() {
/* 475 */     int width = this.imp.getWidth();
/* 476 */     int height = this.imp.getHeight();
/* 477 */     int depth = this.imp.getStackSize();
/* 478 */     int bytesPerPixel = 1;
/* 479 */     int n = getNumber(this.numberField.elementAt(0));
/* 480 */     int start = getNumber(this.numberField.elementAt(1));
/* 481 */     int inc = getNumber(this.numberField.elementAt(2));
/* 482 */     double scale = getNumber(this.numberField.elementAt(3));
/* 483 */     if (scale < 5.0D) scale = 5.0D;
/* 484 */     if (scale > 100.0D) scale = 100.0D;
/*     */ 
/* 486 */     if (n < 1) n = this.fileCount;
/* 487 */     if ((start < 1) || (start > this.fileCount)) start = 1;
/* 488 */     if (start + n - 1 > this.fileCount)
/* 489 */       n = this.fileCount - start + 1;
/* 490 */     if (inc < 1) inc = 1;
/* 491 */     TextField tf = (TextField)this.stringField.elementAt(0);
/* 492 */     String filter = tf.getText();
/* 493 */     tf = (TextField)this.stringField.elementAt(1);
/* 494 */     String regex = tf.getText();
/* 495 */     if (!regex.equals("")) {
/* 496 */       filter = regex;
/* 497 */       this.isRegex = true;
/*     */     }
/* 499 */     if ((!filter.equals("")) && (!filter.equals("*"))) {
/* 500 */       int n2 = 0;
/* 501 */       for (int i = 0; i < this.list.length; i++) {
/* 502 */         if ((this.isRegex) && (this.list[i].matches(filter)))
/* 503 */           n2++;
/* 504 */         else if (this.list[i].indexOf(filter) >= 0)
/* 505 */           n2++;
/*     */       }
/* 507 */       if (n2 < n) n = n2;
/*     */     }
/* 509 */     switch (this.imp.getType()) {
/*     */     case 1:
/* 511 */       bytesPerPixel = 2; break;
/*     */     case 2:
/*     */     case 4:
/* 514 */       bytesPerPixel = 4;
/*     */     case 3:
/* 516 */     }if (this.eightBits)
/* 517 */       bytesPerPixel = 1;
/* 518 */     if (this.rgb)
/* 519 */       bytesPerPixel = 4;
/* 520 */     width = (int)(width * scale / 100.0D);
/* 521 */     height = (int)(height * scale / 100.0D);
/* 522 */     int n2 = (this.fileCount - start + 1) * depth / inc;
/* 523 */     if (n2 < 0) n2 = 0;
/* 524 */     if (n2 > n) n2 = n;
/* 525 */     double size = width * height * n2 * bytesPerPixel / 1048576.0D;
/* 526 */     ((Label)this.theLabel).setText(width + " x " + height + " x " + n2 + " (" + IJ.d2s(size, 1) + "MB)");
/*     */   }
/* 530 */   public int getNumber(Object field) { TextField tf = (TextField)field;
/* 531 */     String theText = tf.getText();
/*     */     Double d;
/*     */     try {
/* 534 */       d = new Double(theText);
/*     */     } catch (NumberFormatException e) {
/* 536 */       d = null;
/*     */     }
/* 538 */     if (d != null) {
/* 539 */       return (int)d.doubleValue();
/*     */     }
/* 541 */     return 0;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.FolderOpenerDialog
 * JD-Core Version:    0.6.2
 */