/*    */ package ij.gui;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.util.Java2;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Color;
/*    */ import java.awt.Container;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JDialog;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class HTMLDialog extends JDialog
/*    */   implements ActionListener
/*    */ {
/*    */   public HTMLDialog(String title, String message)
/*    */   {
/* 10 */     super(IJ.getInstance(), title, true);
/* 11 */     Java2.setSystemLookAndFeel();
/* 12 */     Container container = getContentPane();
/* 13 */     container.setLayout(new BorderLayout());
/* 14 */     if (message == null) message = "";
/* 15 */     JLabel label = new JLabel(message);
/* 16 */     JPanel panel = new JPanel();
/* 17 */     panel.setLayout(new FlowLayout(1, 15, 15));
/* 18 */     panel.add(label);
/* 19 */     container.add(panel, "Center");
/* 20 */     JButton button = new JButton("OK");
/* 21 */     button.addActionListener(this);
/* 22 */     panel = new JPanel();
/* 23 */     panel.add(button);
/* 24 */     container.add(panel, "South");
/* 25 */     setForeground(Color.black);
/* 26 */     pack();
/* 27 */     GUI.center(this);
/* 28 */     show();
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 33 */     dispose();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.HTMLDialog
 * JD-Core Version:    0.6.2
 */