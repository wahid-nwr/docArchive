/*    */ package ij.gui;
/*    */ 
/*    */ import ij.IJ;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Button;
/*    */ import java.awt.Dialog;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.Font;
/*    */ import java.awt.Frame;
/*    */ import java.awt.Panel;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.KeyEvent;
/*    */ import java.awt.event.KeyListener;
/*    */ import java.awt.event.WindowEvent;
/*    */ import java.awt.event.WindowListener;
/*    */ 
/*    */ public class MessageDialog extends Dialog
/*    */   implements ActionListener, KeyListener, WindowListener
/*    */ {
/*    */   protected Button button;
/*    */   protected MultiLineLabel label;
/*    */ 
/*    */   public MessageDialog(Frame parent, String title, String message)
/*    */   {
/* 13 */     super(parent, title, true);
/* 14 */     setLayout(new BorderLayout());
/* 15 */     if (message == null) message = "";
/* 16 */     this.label = new MultiLineLabel(message);
/* 17 */     if (!IJ.isLinux()) this.label.setFont(new Font("SansSerif", 0, 14));
/* 18 */     Panel panel = new Panel();
/* 19 */     panel.setLayout(new FlowLayout(1, 15, 15));
/* 20 */     panel.add(this.label);
/* 21 */     add("Center", panel);
/* 22 */     this.button = new Button("  OK  ");
/* 23 */     this.button.addActionListener(this);
/* 24 */     this.button.addKeyListener(this);
/* 25 */     panel = new Panel();
/* 26 */     panel.setLayout(new FlowLayout());
/* 27 */     panel.add(this.button);
/* 28 */     add("South", panel);
/* 29 */     if (IJ.isMacintosh())
/* 30 */       setResizable(false);
/* 31 */     pack();
/* 32 */     GUI.center(this);
/* 33 */     addWindowListener(this);
/* 34 */     show();
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e) {
/* 38 */     dispose();
/*    */   }
/*    */ 
/*    */   public void keyPressed(KeyEvent e) {
/* 42 */     int keyCode = e.getKeyCode();
/* 43 */     IJ.setKeyDown(keyCode);
/* 44 */     if ((keyCode == 10) || (keyCode == 27))
/* 45 */       dispose();
/*    */   }
/*    */ 
/*    */   public void keyReleased(KeyEvent e) {
/* 49 */     int keyCode = e.getKeyCode();
/* 50 */     IJ.setKeyUp(keyCode);
/*    */   }
/*    */   public void keyTyped(KeyEvent e) {
/*    */   }
/*    */ 
/*    */   public void windowClosing(WindowEvent e) {
/* 56 */     dispose();
/*    */   }
/*    */ 
/*    */   public void windowActivated(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowOpened(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowClosed(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowIconified(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowDeiconified(WindowEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void windowDeactivated(WindowEvent e)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.MessageDialog
 * JD-Core Version:    0.6.2
 */