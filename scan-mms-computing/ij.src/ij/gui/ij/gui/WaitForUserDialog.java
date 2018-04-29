/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.WindowManager;
/*     */ import ij.plugin.frame.RoiManager;
/*     */ import java.awt.Button;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class WaitForUserDialog extends Dialog
/*     */   implements ActionListener, KeyListener
/*     */ {
/*     */   protected Button button;
/*     */   protected MultiLineLabel label;
/*  17 */   protected static int xloc = -1; protected static int yloc = -1;
/*     */   private boolean escPressed;
/*     */ 
/*     */   public WaitForUserDialog(String title, String text)
/*     */   {
/*  21 */     super(getFrame(), title, false);
/*  22 */     this.label = new MultiLineLabel(text, 175);
/*  23 */     if (!IJ.isLinux()) this.label.setFont(new Font("SansSerif", 0, 14));
/*  24 */     if (IJ.isMacOSX()) {
/*  25 */       RoiManager rm = RoiManager.getInstance();
/*  26 */       if (rm != null) rm.runCommand("enable interrupts");
/*     */     }
/*  28 */     GridBagLayout gridbag = new GridBagLayout();
/*  29 */     GridBagConstraints c = new GridBagConstraints();
/*  30 */     setLayout(gridbag);
/*  31 */     c.insets = new Insets(6, 6, 0, 6);
/*  32 */     c.gridx = 0; c.gridy = 0; c.anchor = 17;
/*  33 */     add(this.label, c);
/*  34 */     this.button = new Button("  OK  ");
/*  35 */     this.button.addActionListener(this);
/*  36 */     this.button.addKeyListener(this);
/*  37 */     c.insets = new Insets(2, 6, 6, 6);
/*  38 */     c.gridx = 0; c.gridy = 2; c.anchor = 13;
/*  39 */     add(this.button, c);
/*  40 */     setResizable(false);
/*  41 */     addKeyListener(this);
/*  42 */     pack();
/*  43 */     if (xloc == -1)
/*  44 */       GUI.center(this);
/*     */     else
/*  46 */       setLocation(xloc, yloc);
/*  47 */     if (IJ.isJava15())
/*     */       try {
/*  49 */         Class windowClass = Class.forName("java.awt.Window");
/*  50 */         Method setAlwaysOnTop = windowClass.getDeclaredMethod("setAlwaysOnTop", new Class[] { Boolean.TYPE });
/*  51 */         Object[] arglist = new Object[1]; arglist[0] = new Boolean(true);
/*  52 */         setAlwaysOnTop.invoke(this, arglist); } catch (Exception e) {
/*     */       }
/*     */   }
/*     */ 
/*     */   public WaitForUserDialog(String text) {
/*  57 */     this("Action Required", text);
/*     */   }
/*     */ 
/*     */   public void show() {
/*  61 */     super.show();
/*     */ 
/*  63 */     synchronized (this) {
/*     */       try { wait(); } catch (InterruptedException e) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static Frame getFrame() {
/*  70 */     Frame win = WindowManager.getCurrentWindow();
/*  71 */     if (win == null) win = IJ.getInstance();
/*  72 */     return win;
/*     */   }
/*     */ 
/*     */   public void close() {
/*  76 */     synchronized (this) { notify(); }
/*  77 */     xloc = getLocation().x;
/*  78 */     yloc = getLocation().y;
/*     */ 
/*  80 */     dispose();
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/*  84 */     close();
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e) {
/*  88 */     int keyCode = e.getKeyCode();
/*  89 */     IJ.setKeyDown(keyCode);
/*  90 */     if ((keyCode == 10) || (keyCode == 27)) {
/*  91 */       this.escPressed = (keyCode == 27);
/*  92 */       close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean escPressed() {
/*  97 */     return this.escPressed;
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent e) {
/* 101 */     int keyCode = e.getKeyCode();
/* 102 */     IJ.setKeyUp(keyCode);
/*     */   }
/*     */ 
/*     */   public void keyTyped(KeyEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.WaitForUserDialog
 * JD-Core Version:    0.6.2
 */