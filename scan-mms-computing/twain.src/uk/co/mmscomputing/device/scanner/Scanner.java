/*     */ package uk.co.mmscomputing.device.scanner;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ 
/*     */ public abstract class Scanner
/*     */ {
/*  15 */   protected static boolean installed = false;
/*     */ 
/*  19 */   protected Vector listeners = new Vector();
/*  20 */   protected ScannerIOMetadata metadata = null;
/*  21 */   protected boolean isbusy = false;
/*     */ 
/*     */   public abstract boolean isAPIInstalled();
/*     */ 
/*     */   public abstract void select() throws ScannerIOException;
/*     */ 
/*     */   public abstract String[] getDeviceNames() throws ScannerIOException;
/*     */ 
/*     */   public abstract void select(String paramString) throws ScannerIOException;
/*     */ 
/*     */   public abstract String getSelectedDeviceName() throws ScannerIOException;
/*     */ 
/*     */   public abstract void acquire() throws ScannerIOException;
/*     */ 
/*     */   public abstract void setCancel(boolean paramBoolean) throws ScannerIOException;
/*     */ 
/*  30 */   public boolean isBusy() { return this.isbusy; }
/*     */ 
/*     */   public void waitToExit() {
/*     */     try {
/*  34 */       while (isBusy()) {
/*  35 */         Thread.currentThread(); Thread.sleep(200L);
/*     */       }
/*     */     } catch (Exception localException) {
/*  38 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addListener(ScannerListener paramScannerListener) {
/*  43 */     this.listeners.add(paramScannerListener);
/*     */   }
/*     */ 
/*     */   public void removeListener(ScannerListener paramScannerListener) {
/*  47 */     this.listeners.remove(paramScannerListener);
/*     */   }
/*     */ 
/*     */   public void fireExceptionUpdate(Exception paramException) {
/*  51 */     this.metadata.setException(paramException);
/*  52 */     fireListenerUpdate(ScannerIOMetadata.EXCEPTION);
/*     */   }
/*     */ 
/*     */   public void fireListenerUpdate(ScannerIOMetadata.Type paramType) {
/*  56 */     if (paramType.equals(ScannerIOMetadata.STATECHANGE)) {
/*  57 */       this.isbusy = this.metadata.getDevice().isBusy();
/*     */     }
/*  59 */     for (Enumeration localEnumeration = this.listeners.elements(); localEnumeration.hasMoreElements(); ) {
/*  60 */       ScannerListener localScannerListener = (ScannerListener)localEnumeration.nextElement();
/*  61 */       localScannerListener.update(paramType, this.metadata);
/*     */     }
/*     */   }
/*     */ 
/*     */   public JComponent getScanGUI() throws ScannerIOException {
/*  66 */     return new JLabel("Dummy Scanner GUI");
/*     */   }
/*     */ 
/*     */   public JComponent getScanGUI(int paramInt) throws ScannerIOException {
/*  70 */     return new JLabel("Dummy Scanner GUI");
/*     */   }
/*     */ 
/*     */   public static Scanner getDevice() {
/*  74 */     String str1 = System.getProperty("os.name");
/*     */     String str2;
/*  76 */     if (str1.startsWith("Linux"))
/*  77 */       str2 = "uk.co.mmscomputing.device.sane.SaneScanner";
/*  78 */     else if (str1.startsWith("Windows")) {
/*  79 */       str2 = "uk.co.mmscomputing.device.twain.TwainScanner";
/*     */     }
/*     */     else
/*  82 */       return null;
/*     */     try
/*     */     {
/*  85 */       Scanner localScanner = (Scanner)Class.forName(str2).newInstance();
/*  86 */       if (localScanner.isAPIInstalled())
/*     */       {
/*  95 */         return localScanner;
/*     */       }
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/*  98 */       localIllegalAccessException.printStackTrace();
/*     */     } catch (InstantiationException localInstantiationException) {
/* 100 */       localInstantiationException.printStackTrace();
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 102 */       localClassNotFoundException.printStackTrace();
/*     */     }
/* 104 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.scanner.Scanner
 * JD-Core Version:    0.6.2
 */