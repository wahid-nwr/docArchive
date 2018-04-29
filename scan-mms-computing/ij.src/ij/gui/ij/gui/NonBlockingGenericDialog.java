/*    */ package ij.gui;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.KeyEvent;
/*    */ 
/*    */ public class NonBlockingGenericDialog extends GenericDialog
/*    */ {
/*    */   public NonBlockingGenericDialog(String title)
/*    */   {
/* 10 */     super(title, null);
/* 11 */     setModal(false);
/*    */   }
/*    */ 
/*    */   public synchronized void showDialog() {
/* 15 */     super.showDialog();
/*    */     try {
/* 17 */       wait(); } catch (InterruptedException e) {
/*    */     }
/*    */   }
/*    */ 
/*    */   public synchronized void actionPerformed(ActionEvent e) {
/* 22 */     super.actionPerformed(e);
/* 23 */     if ((wasOKed()) || (wasCanceled()))
/* 24 */       notify();
/*    */   }
/*    */ 
/*    */   public synchronized void keyPressed(KeyEvent e) {
/* 28 */     super.keyPressed(e);
/* 29 */     if ((wasOKed()) || (wasCanceled()))
/* 30 */       notify();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.NonBlockingGenericDialog
 * JD-Core Version:    0.6.2
 */