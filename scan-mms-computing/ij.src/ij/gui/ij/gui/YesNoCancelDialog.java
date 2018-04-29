/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Button;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Panel;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ 
/*     */ public class YesNoCancelDialog extends Dialog
/*     */   implements ActionListener, KeyListener
/*     */ {
/*     */   private Button yesB;
/*     */   private Button noB;
/*     */   private Button cancelB;
/*     */   private boolean cancelPressed;
/*     */   private boolean yesPressed;
/*  11 */   private boolean firstPaint = true;
/*     */ 
/*     */   public YesNoCancelDialog(Frame parent, String title, String msg) {
/*  14 */     super(parent, title, true);
/*  15 */     setLayout(new BorderLayout());
/*  16 */     Panel panel = new Panel();
/*  17 */     panel.setLayout(new FlowLayout(0, 10, 10));
/*  18 */     MultiLineLabel message = new MultiLineLabel(msg);
/*  19 */     message.setFont(new Font("Dialog", 0, 12));
/*  20 */     panel.add(message);
/*  21 */     add("North", panel);
/*     */ 
/*  23 */     panel = new Panel();
/*  24 */     panel.setLayout(new FlowLayout(2, 15, 8));
/*  25 */     if ((IJ.isMacintosh()) && (msg.startsWith("Save"))) {
/*  26 */       this.yesB = new Button("  Save  ");
/*  27 */       this.noB = new Button("Don't Save");
/*  28 */       this.cancelB = new Button("  Cancel  ");
/*     */     } else {
/*  30 */       this.yesB = new Button("  Yes  ");
/*  31 */       this.noB = new Button("  No  ");
/*  32 */       this.cancelB = new Button(" Cancel ");
/*     */     }
/*  34 */     this.yesB.addActionListener(this);
/*  35 */     this.noB.addActionListener(this);
/*  36 */     this.cancelB.addActionListener(this);
/*  37 */     this.yesB.addKeyListener(this);
/*  38 */     this.noB.addKeyListener(this);
/*  39 */     this.cancelB.addKeyListener(this);
/*  40 */     if (IJ.isMacintosh()) {
/*  41 */       panel.add(this.noB);
/*  42 */       panel.add(this.cancelB);
/*  43 */       panel.add(this.yesB);
/*  44 */       setResizable(false);
/*     */     } else {
/*  46 */       panel.add(this.yesB);
/*  47 */       panel.add(this.noB);
/*  48 */       panel.add(this.cancelB);
/*     */     }
/*  50 */     add("South", panel);
/*  51 */     pack();
/*  52 */     GUI.center(this);
/*  53 */     show();
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/*  57 */     if (e.getSource() == this.cancelB)
/*  58 */       this.cancelPressed = true;
/*  59 */     else if (e.getSource() == this.yesB)
/*  60 */       this.yesPressed = true;
/*  61 */     closeDialog();
/*     */   }
/*     */ 
/*     */   public boolean cancelPressed()
/*     */   {
/*  66 */     return this.cancelPressed;
/*     */   }
/*     */ 
/*     */   public boolean yesPressed()
/*     */   {
/*  71 */     return this.yesPressed;
/*     */   }
/*     */ 
/*     */   void closeDialog() {
/*  75 */     dispose();
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e) {
/*  79 */     int keyCode = e.getKeyCode();
/*  80 */     IJ.setKeyDown(keyCode);
/*  81 */     if ((keyCode == 10) || (keyCode == 89) || (keyCode == 83)) {
/*  82 */       this.yesPressed = true;
/*  83 */       closeDialog();
/*  84 */     } else if ((keyCode == 78) || (keyCode == 68)) {
/*  85 */       closeDialog();
/*  86 */     } else if ((keyCode == 27) || (keyCode == 67)) {
/*  87 */       this.cancelPressed = true;
/*  88 */       closeDialog();
/*  89 */       IJ.resetEscape();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent e) {
/*  94 */     int keyCode = e.getKeyCode();
/*  95 */     IJ.setKeyUp(keyCode);
/*     */   }
/*     */   public void keyTyped(KeyEvent e) {
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g) {
/* 101 */     super.paint(g);
/* 102 */     if (this.firstPaint) {
/* 103 */       this.yesB.requestFocus();
/* 104 */       this.firstPaint = false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.YesNoCancelDialog
 * JD-Core Version:    0.6.2
 */