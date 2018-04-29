/*    */ package uk.co.mmscomputing.util;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.io.PrintStream;
/*    */ import java.util.Hashtable;
/*    */ import java.util.Map;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JTextField;
/*    */ import javax.swing.border.EtchedBorder;
/*    */ import javax.swing.event.DocumentEvent;
/*    */ import javax.swing.event.DocumentListener;
/*    */ import javax.swing.text.Document;
/*    */ import javax.swing.text.Position;
/*    */ 
/*    */ public class UtilTextField
/*    */   implements DocumentListener
/*    */ {
/*    */   private Document td;
/*    */ 
/*    */   public UtilTextField(JPanel paramJPanel, Hashtable paramHashtable, String paramString1, String paramString2, String paramString3, int paramInt)
/*    */   {
/* 15 */     if (paramString3 != null) {
/* 16 */       localObject = new JLabel(paramString3);
/* 17 */       ((JLabel)localObject).setBorder(new EtchedBorder());
/* 18 */       paramJPanel.add((Component)localObject);
/*    */     }
/* 20 */     Object localObject = new JTextField(paramInt);
/* 21 */     ((JTextField)localObject).setText(getString(paramHashtable, paramString1, paramString2));
/* 22 */     this.td = ((JTextField)localObject).getDocument();
/* 23 */     this.td.addDocumentListener(this);
/* 24 */     this.td.putProperty("key", paramString1);
/* 25 */     this.td.putProperty("map", paramHashtable);
/* 26 */     paramJPanel.add((Component)localObject);
/*    */   }
/*    */ 
/*    */   public void addDocumentListener(DocumentListener paramDocumentListener) {
/* 30 */     this.td.addDocumentListener(paramDocumentListener);
/*    */   }
/*    */   public void changedUpdate(DocumentEvent paramDocumentEvent) {
/* 33 */     setMap(paramDocumentEvent); } 
/* 34 */   public void insertUpdate(DocumentEvent paramDocumentEvent) { setMap(paramDocumentEvent); } 
/* 35 */   public void removeUpdate(DocumentEvent paramDocumentEvent) { setMap(paramDocumentEvent); }
/*    */ 
/*    */   protected String getString(Hashtable paramHashtable, String paramString1, String paramString2)
/*    */   {
/* 39 */     Object localObject = paramHashtable.get(paramString1);
/* 40 */     if (localObject == null) {
/* 41 */       paramHashtable.put(paramString1, paramString2);
/* 42 */       return paramString2;
/* 43 */     }if ((localObject instanceof String)) {
/* 44 */       return (String)localObject;
/*    */     }
/* 46 */     System.out.println("9\b" + getClass().getName() + "\n\tExpect Type String for key [" + paramString1 + "].");
/* 47 */     return "";
/*    */   }
/*    */ 
/*    */   protected void setMap(DocumentEvent paramDocumentEvent)
/*    */   {
/*    */     try {
/* 53 */       Document localDocument = paramDocumentEvent.getDocument();
/* 54 */       Map localMap = (Map)localDocument.getProperty("map");
/* 55 */       String str1 = (String)localDocument.getProperty("key");
/* 56 */       String str2 = localDocument.getText(localDocument.getStartPosition().getOffset(), localDocument.getEndPosition().getOffset()).trim();
/* 57 */       localMap.put(str1, str2);
/*    */     } catch (Exception localException) {
/* 59 */       System.out.println("9\b" + localException.getMessage());
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.UtilTextField
 * JD-Core Version:    0.6.2
 */