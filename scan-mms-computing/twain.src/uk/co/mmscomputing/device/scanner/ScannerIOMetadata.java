/*    */ package uk.co.mmscomputing.device.scanner;
/*    */ 
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.File;
/*    */ 
/*    */ public abstract class ScannerIOMetadata
/*    */ {
/*  9 */   public static Type INFO = new Type();
/* 10 */   public static Type EXCEPTION = new Type();
/*    */ 
/* 12 */   public static Type ACQUIRED = new Type();
/* 13 */   public static Type FILE = new Type();
/* 14 */   public static Type MEMORY = new Type();
/* 15 */   public static Type NEGOTIATE = new Type();
/* 16 */   public static Type STATECHANGE = new Type();
/*    */ 
/* 18 */   private int laststate = 0; private int state = 0;
/* 19 */   private boolean cancel = false;
/* 20 */   private BufferedImage image = null;
/* 21 */   private File file = null;
/* 22 */   private String info = "";
/* 23 */   private Exception exception = null;
/*    */ 
/* 25 */   public void setState(int paramInt) { this.laststate = this.state; this.state = paramInt; } 
/* 26 */   public int getLastState() { return this.laststate; } 
/* 27 */   public int getState() { return this.state; } 
/* 28 */   public String getStateStr() { return "State " + this.state; } 
/* 29 */   public boolean isState(int paramInt) { return this.state == paramInt; } 
/*    */   public void setImage(BufferedImage paramBufferedImage) {
/* 31 */     this.image = paramBufferedImage; this.file = null; } 
/* 32 */   public BufferedImage getImage() { return this.image; } 
/*    */   public void setFile(File paramFile) {
/* 34 */     this.image = null; this.file = paramFile; } 
/* 35 */   public File getFile() { return this.file; } 
/*    */   public void setInfo(String paramString) {
/* 37 */     this.info = paramString; } 
/* 38 */   public String getInfo() { return this.info; } 
/*    */   public void setException(Exception paramException) {
/* 40 */     this.exception = paramException; } 
/* 41 */   public Exception getException() { return this.exception; } 
/*    */   public boolean getCancel() {
/* 43 */     return this.cancel; } 
/* 44 */   public void setCancel(boolean paramBoolean) { this.cancel = paramBoolean; }
/*    */ 
/*    */ 
/*    */   public abstract boolean isFinished();
/*    */ 
/*    */   public abstract ScannerDevice getDevice();
/*    */ 
/*    */   public static class Type
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.scanner.ScannerIOMetadata
 * JD-Core Version:    0.6.2
 */