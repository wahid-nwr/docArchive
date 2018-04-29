/*     */ package uk.co.mmscomputing.device.twain;
/*     */ 
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.beans.EventHandler;
/*     */ import java.io.PrintStream;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComponent;
/*     */ import uk.co.mmscomputing.device.scanner.Scanner;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerIOException;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerListener;
/*     */ import uk.co.mmscomputing.util.JarImageIcon;
/*     */ 
/*     */ public class TwainPanel extends JComponent
/*     */   implements TwainConstants, ScannerListener
/*     */ {
/*  13 */   Scanner scanner = null;
/*  14 */   JButton acqbutton = null;
/*  15 */   JButton selbutton = null;
/*  16 */   JCheckBox guicheckbox = null;
/*     */ 
/*     */   public TwainPanel(Scanner paramScanner, int paramInt)
/*     */     throws TwainIOException
/*     */   {
/*  36 */     this.scanner = paramScanner;
/*     */ 
/*  38 */     switch (paramInt) {
/*     */     case 0:
/*  40 */       this.acqbutton = new JButton("acquire");
/*  41 */       this.selbutton = new JButton("select");
/*  42 */       setLayout(new GridLayout(0, 1));
/*  43 */       break;
/*     */     case 1:
/*  45 */       this.acqbutton = new JButton("acquire");
/*  46 */       this.selbutton = new JButton("select");
/*  47 */       setLayout(new GridLayout(1, 0));
/*  48 */       break;
/*     */     case 2:
/*  50 */       this.acqbutton = new JButton("acquire", new JarImageIcon(getClass(), "16x16/scanner.png"));
/*  51 */       this.selbutton = new JButton("select", new JarImageIcon(getClass(), "16x16/list.png"));
/*  52 */       setLayout(new GridLayout(0, 1));
/*  53 */       break;
/*     */     case 3:
/*  55 */       this.acqbutton = new JButton("acquire", new JarImageIcon(getClass(), "16x16/scanner.png"));
/*  56 */       this.selbutton = new JButton("select", new JarImageIcon(getClass(), "16x16/list.png"));
/*  57 */       setLayout(new GridLayout(1, 0));
/*  58 */       break;
/*     */     case 4:
/*  60 */       this.acqbutton = new JButton("acquire", new JarImageIcon(getClass(), "32x32/scanner.png"));
/*  61 */       this.selbutton = new JButton("select", new JarImageIcon(getClass(), "32x32/list.png"));
/*  62 */       setLayout(new GridLayout(0, 1));
/*  63 */       break;
/*     */     case 5:
/*  65 */       this.acqbutton = new JButton("acquire", new JarImageIcon(getClass(), "32x32/scanner.png"));
/*  66 */       this.selbutton = new JButton("select", new JarImageIcon(getClass(), "32x32/list.png"));
/*  67 */       setLayout(new GridLayout(1, 0));
/*     */     }
/*     */ 
/*  71 */     this.acqbutton.addActionListener((ActionListener)EventHandler.create(ActionListener.class, this, "acquire"));
/*  72 */     add(this.acqbutton);
/*     */ 
/*  81 */     this.guicheckbox = new JCheckBox("Enable GUI");
/*  82 */     this.guicheckbox.setSelected(true);
/*  83 */     add(this.guicheckbox);
/*     */ 
/*  85 */     this.selbutton.addActionListener((ActionListener)EventHandler.create(ActionListener.class, this, "select"));
/*  86 */     add(this.selbutton);
/*     */ 
/*  88 */     paramScanner.addListener(this);
/*     */ 
/*  90 */     if (jtwain.getSource().isBusy()) {
/*  91 */       this.acqbutton.setEnabled(false);
/*  92 */       this.selbutton.setEnabled(false);
/*  93 */       this.guicheckbox.setEnabled(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void acquire() {
/*     */     try {
/*  99 */       this.scanner.acquire();
/*     */     } catch (ScannerIOException localScannerIOException) {
/* 101 */       this.scanner.fireExceptionUpdate(localScannerIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void select() {
/*     */     try {
/* 107 */       this.scanner.select();
/*     */     } catch (ScannerIOException localScannerIOException) {
/* 109 */       this.scanner.fireExceptionUpdate(localScannerIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void update(ScannerIOMetadata.Type paramType, ScannerIOMetadata paramScannerIOMetadata) {
/* 114 */     if ((paramScannerIOMetadata instanceof TwainIOMetadata)) {
/* 115 */       TwainIOMetadata localTwainIOMetadata = (TwainIOMetadata)paramScannerIOMetadata;
/* 116 */       TwainSource localTwainSource = localTwainIOMetadata.getSource();
/*     */ 
/* 118 */       if (paramType.equals(ScannerIOMetadata.STATECHANGE)) {
/* 119 */         if (localTwainIOMetadata.isState(3))
/* 120 */           if (localTwainSource.isBusy()) {
/* 121 */             this.acqbutton.setEnabled(false);
/* 122 */             this.selbutton.setEnabled(false);
/* 123 */             this.guicheckbox.setEnabled(false);
/*     */           } else {
/* 125 */             this.acqbutton.setEnabled(true);
/* 126 */             this.selbutton.setEnabled(true);
/* 127 */             this.guicheckbox.setEnabled(true);
/*     */           }
/*     */       }
/* 130 */       else if (paramType.equals(ScannerIOMetadata.NEGOTIATE))
/* 131 */         if (localTwainSource.isUIControllable()) {
/* 132 */           localTwainSource.setShowUI(this.guicheckbox.isSelected());
/*     */         } else {
/* 134 */           if (!this.guicheckbox.isSelected()) System.out.println("9\bCannot hide twain source's GUI.");
/* 135 */           this.guicheckbox.setSelected(true);
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainPanel
 * JD-Core Version:    0.6.2
 */