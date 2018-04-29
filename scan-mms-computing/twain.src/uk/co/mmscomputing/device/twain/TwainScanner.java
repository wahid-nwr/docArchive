/*     */ package uk.co.mmscomputing.device.twain;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JComponent;
/*     */ import uk.co.mmscomputing.device.scanner.Scanner;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerIOException;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
/*     */ 
/*     */ public class TwainScanner extends Scanner
/*     */   implements TwainConstants
/*     */ {
/*     */   public TwainScanner()
/*     */   {
/*  18 */     this.metadata = new TwainIOMetadata();
/*  19 */     jtwain.setScanner(this);
/*     */   }
/*     */ 
/*     */   public void select()
/*     */     throws ScannerIOException
/*     */   {
/*  25 */     jtwain.select(this);
/*     */   }
/*     */ 
/*     */   public TwainIdentity[] getIdentities() {
/*  29 */     Vector localVector = new Vector();
/*     */     try {
/*  31 */       jtwain.getIdentities(this, localVector);
/*     */     } catch (Exception localException) {
/*  33 */       this.metadata.setException(localException);
/*  34 */       fireListenerUpdate(ScannerIOMetadata.EXCEPTION);
/*     */     }
/*  36 */     return (TwainIdentity[])localVector.toArray(new TwainIdentity[localVector.size()]);
/*     */   }
/*     */ 
/*     */   public String[] getDeviceNames() throws ScannerIOException {
/*  40 */     Vector localVector = new Vector();
/*     */ 
/*  42 */     jtwain.getIdentities(this, localVector);
/*     */ 
/*  44 */     String[] arrayOfString = new String[localVector.size()];
/*  45 */     Enumeration localEnumeration = localVector.elements();
/*  46 */     for (int i = 0; localEnumeration.hasMoreElements(); i++) {
/*  47 */       TwainIdentity localTwainIdentity = (TwainIdentity)localEnumeration.nextElement();
/*  48 */       arrayOfString[i] = localTwainIdentity.getProductName();
/*     */     }
/*  50 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   public void select(String paramString) throws ScannerIOException {
/*  54 */     jtwain.select(this, paramString);
/*     */   }
/*     */ 
/*     */   public String getSelectedDeviceName() throws ScannerIOException {
/*  58 */     return jtwain.getSource().getProductName();
/*     */   }
/*     */ 
/*     */   public void acquire() throws ScannerIOException {
/*  62 */     jtwain.acquire(this);
/*     */   }
/*     */ 
/*     */   public void setCancel(boolean paramBoolean) throws ScannerIOException {
/*  66 */     jtwain.setCancel(this, paramBoolean);
/*     */   }
/*     */ 
/*     */   void setImage(BufferedImage paramBufferedImage)
/*     */   {
/*     */     try
/*     */     {
/*  74 */       this.metadata.setImage(paramBufferedImage);
/*  75 */       fireListenerUpdate(ScannerIOMetadata.ACQUIRED);
/*     */     } catch (Exception localException) {
/*  77 */       this.metadata.setException(localException);
/*  78 */       fireListenerUpdate(ScannerIOMetadata.EXCEPTION);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setImage(File paramFile) {
/*     */     try {
/*  84 */       this.metadata.setFile(paramFile);
/*  85 */       fireListenerUpdate(ScannerIOMetadata.FILE);
/*     */     } catch (Exception localException) {
/*  87 */       this.metadata.setException(localException);
/*  88 */       fireListenerUpdate(ScannerIOMetadata.EXCEPTION);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setImageBuffer(TwainTransfer.MemoryTransfer.Info paramInfo)
/*     */   {
/*     */     try
/*     */     {
/*  96 */       ((TwainIOMetadata)this.metadata).setMemory(paramInfo);
/*  97 */       fireListenerUpdate(ScannerIOMetadata.MEMORY);
/*     */     } catch (Exception localException) {
/*  99 */       this.metadata.setException(localException);
/* 100 */       fireListenerUpdate(ScannerIOMetadata.EXCEPTION);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void negotiateCapabilities(TwainSource paramTwainSource)
/*     */   {
/* 107 */     ((TwainIOMetadata)this.metadata).setSource(paramTwainSource);
/* 108 */     fireListenerUpdate(ScannerIOMetadata.NEGOTIATE);
/* 109 */     if (this.metadata.getCancel())
/*     */       try {
/* 111 */         paramTwainSource.close();
/*     */       } catch (Exception localException) {
/* 113 */         this.metadata.setException(localException);
/* 114 */         fireListenerUpdate(ScannerIOMetadata.EXCEPTION);
/*     */       }
/*     */   }
/*     */ 
/*     */   void setState(TwainSource paramTwainSource)
/*     */   {
/* 120 */     this.metadata.setState(paramTwainSource.getState());
/* 121 */     ((TwainIOMetadata)this.metadata).setSource(paramTwainSource);
/* 122 */     fireListenerUpdate(ScannerIOMetadata.STATECHANGE);
/*     */   }
/*     */ 
/*     */   void signalInfo(String paramString, int paramInt) {
/* 126 */     this.metadata.setInfo(paramString + " [0x" + Integer.toHexString(paramInt) + "]");
/* 127 */     fireListenerUpdate(ScannerIOMetadata.INFO);
/*     */   }
/*     */ 
/*     */   void signalException(String paramString) {
/* 131 */     TwainIOException localTwainIOException = new TwainIOException(getClass().getName() + ".setException:\n    " + paramString);
/* 132 */     this.metadata.setException(localTwainIOException);
/* 133 */     fireListenerUpdate(ScannerIOMetadata.EXCEPTION);
/*     */   }
/*     */   public boolean isAPIInstalled() {
/* 136 */     return jtwain.isInstalled();
/*     */   }
/*     */   public JComponent getScanGUI() throws ScannerIOException {
/* 139 */     return getScanGUI(4);
/*     */   }
/*     */ 
/*     */   public JComponent getScanGUI(int paramInt) throws ScannerIOException {
/* 143 */     return new TwainPanel(this, paramInt);
/*     */   }
/*     */ 
/*     */   public static Scanner getDevice()
/*     */   {
/* 152 */     return new TwainScanner();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainScanner
 * JD-Core Version:    0.6.2
 */