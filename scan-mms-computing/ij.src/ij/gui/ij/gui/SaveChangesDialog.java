/*    */ package ij.gui;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.Macro;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Button;
/*    */ import java.awt.Component;
/*    */ import java.awt.Dialog;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.Font;
/*    */ import java.awt.Frame;
/*    */ import java.awt.Label;
/*    */ import java.awt.Panel;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.KeyEvent;
/*    */ import java.awt.event.KeyListener;
/*    */ 
/*    */ public class SaveChangesDialog extends Dialog
/*    */   implements ActionListener, KeyListener
/*    */ {
/*    */   private Button dontSave;
/*    */   private Button cancel;
/*    */   private Button save;
/*    */   private boolean cancelPressed;
/*    */   private boolean savePressed;
/*    */ 
/*    */   public SaveChangesDialog(Frame parent, String fileName)
/*    */   {
/* 13 */     super(parent, "Save?", true);
/* 14 */     setLayout(new BorderLayout());
/* 15 */     Panel panel = new Panel();
/* 16 */     panel.setLayout(new FlowLayout(0, 10, 10));
/*    */     Component message;
/*    */     Component message;
/* 18 */     if (fileName.startsWith("Save ")) {
/* 19 */       message = new Label(fileName);
/*    */     }
/*    */     else
/*    */     {
/*    */       Component message;
/* 21 */       if (fileName.length() > 22)
/* 22 */         message = new MultiLineLabel("Save changes to\n\"" + fileName + "\"?");
/*    */       else
/* 24 */         message = new Label("Save changes to \"" + fileName + "\"?");
/*    */     }
/* 26 */     message.setFont(new Font("Dialog", 1, 12));
/* 27 */     panel.add(message);
/* 28 */     add("Center", panel);
/*    */ 
/* 30 */     panel = new Panel();
/* 31 */     panel.setLayout(new FlowLayout(1, 8, 8));
/* 32 */     this.save = new Button("  Save  ");
/* 33 */     this.save.addActionListener(this);
/* 34 */     this.save.addKeyListener(this);
/* 35 */     this.cancel = new Button("  Cancel  ");
/* 36 */     this.cancel.addActionListener(this);
/* 37 */     this.cancel.addKeyListener(this);
/* 38 */     this.dontSave = new Button("Don't Save");
/* 39 */     this.dontSave.addActionListener(this);
/* 40 */     this.dontSave.addKeyListener(this);
/* 41 */     if (IJ.isMacintosh()) {
/* 42 */       panel.add(this.dontSave);
/* 43 */       panel.add(this.cancel);
/* 44 */       panel.add(this.save);
/*    */     } else {
/* 46 */       panel.add(this.save);
/* 47 */       panel.add(this.dontSave);
/* 48 */       panel.add(this.cancel);
/*    */     }
/* 50 */     add("South", panel);
/* 51 */     if (IJ.isMacintosh())
/* 52 */       setResizable(false);
/* 53 */     pack();
/* 54 */     GUI.center(this);
/* 55 */     show();
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e) {
/* 59 */     if (e.getSource() == this.cancel)
/* 60 */       this.cancelPressed = true;
/* 61 */     else if (e.getSource() == this.save)
/* 62 */       this.savePressed = true;
/* 63 */     closeDialog();
/*    */   }
/*    */ 
/*    */   public boolean cancelPressed()
/*    */   {
/* 68 */     if (this.cancelPressed)
/* 69 */       Macro.abort();
/* 70 */     return this.cancelPressed;
/*    */   }
/*    */ 
/*    */   public boolean savePressed()
/*    */   {
/* 75 */     return this.savePressed;
/*    */   }
/*    */ 
/*    */   void closeDialog()
/*    */   {
/* 80 */     dispose();
/*    */   }
/*    */ 
/*    */   public void keyPressed(KeyEvent e) {
/* 84 */     int keyCode = e.getKeyCode();
/* 85 */     IJ.setKeyDown(keyCode);
/* 86 */     if (keyCode == 10) {
/* 87 */       closeDialog();
/* 88 */     } else if (keyCode == 27) {
/* 89 */       this.cancelPressed = true;
/* 90 */       closeDialog();
/* 91 */       IJ.resetEscape();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void keyReleased(KeyEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void keyTyped(KeyEvent e)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.SaveChangesDialog
 * JD-Core Version:    0.6.2
 */