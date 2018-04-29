/*    */ package uk.co.mmscomputing.util;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JOptionPane;
/*    */ import javax.swing.JTabbedPane;
/*    */ 
/*    */ public class UtilTabCloseIcon
/*    */   implements Icon
/*    */ {
/*    */   private final Icon mIcon;
/* 17 */   private JTabbedPane mTabbedPane = null;
/* 18 */   private transient Rectangle mPosition = null;
/*    */   private boolean closed;
/*    */ 
/*    */   public UtilTabCloseIcon(Icon paramIcon)
/*    */   {
/* 26 */     this.mIcon = paramIcon;
/*    */   }
/*    */ 
/*    */   public UtilTabCloseIcon()
/*    */   {
/* 37 */     this.mIcon = new JarImageIcon(getClass(), "16x16/delete.png");
/*    */   }
/*    */ 
/*    */   public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
/*    */   {
/* 46 */     if (null == this.mTabbedPane)
/*    */     {
/* 48 */       this.mTabbedPane = ((JTabbedPane)paramComponent);
/* 49 */       this.mTabbedPane.addMouseListener(new MouseAdapter()
/*    */       {
/*    */         public void mouseReleased(MouseEvent paramAnonymousMouseEvent)
/*    */         {
/* 54 */           if ((!paramAnonymousMouseEvent.isConsumed()) && (UtilTabCloseIcon.this.mPosition.contains(paramAnonymousMouseEvent.getX(), paramAnonymousMouseEvent.getY())))
/*    */           {
/* 56 */             if (!UtilTabCloseIcon.this.closed) {
/* 57 */               if (JOptionPane.showConfirmDialog(new JFrame(), "Do you really want to discard this image ?", "Close", 0) == 0)
/*    */               {
/* 60 */                 UtilTabCloseIcon.this.closed = true;
/* 61 */                 int i = UtilTabCloseIcon.this.mTabbedPane.getSelectedIndex();
/* 62 */                 UtilTabCloseIcon.this.mTabbedPane.remove(i);
/* 63 */                 paramAnonymousMouseEvent.consume();
/*    */               } else {
/* 65 */                 UtilTabCloseIcon.this.closed = false;
/*    */               }
/*    */             }
/*    */           }
/*    */         }
/*    */       });
/*    */     }
/* 72 */     this.mPosition = new Rectangle(paramInt1, paramInt2, getIconWidth(), getIconHeight());
/* 73 */     this.mIcon.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
/*    */   }
/*    */ 
/*    */   public int getIconWidth()
/*    */   {
/* 82 */     return this.mIcon.getIconWidth();
/*    */   }
/*    */ 
/*    */   public int getIconHeight()
/*    */   {
/* 90 */     return this.mIcon.getIconHeight();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.UtilTabCloseIcon
 * JD-Core Version:    0.6.2
 */