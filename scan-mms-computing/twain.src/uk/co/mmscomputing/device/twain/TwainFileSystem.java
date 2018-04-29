/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ public class TwainFileSystem
/*    */   implements TwainConstants
/*    */ {
/*    */   TwainSource source;
/*    */   byte[] buf;
/*    */ 
/*    */   TwainFileSystem(TwainSource paramTwainSource)
/*    */   {
/* 32 */     this.source = paramTwainSource;
/* 33 */     this.buf = new byte[1124];
/*    */   }
/*    */ 
/*    */   public void setInputName(String paramString) {
/* 37 */     int i = paramString.length();
/* 38 */     if (i < 255) {
/* 39 */       byte[] arrayOfByte = paramString.getBytes();
/* 40 */       for (int j = 0; j < i; j++) {
/* 41 */         this.buf[j] = arrayOfByte[j];
/*    */       }
/* 43 */       this.buf[i] = 0;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setAutomaticCaptureDirectory()
/*    */     throws TwainIOException
/*    */   {
/* 52 */     this.source.call(1, 14, 2059, this.buf);
/*    */   }
/*    */ 
/*    */   public void changeDirectory(String paramString) throws TwainIOException {
/* 56 */     setInputName(paramString);
/* 57 */     this.source.call(1, 14, 2049, this.buf);
/*    */   }
/*    */ 
/*    */   public void copy() throws TwainIOException {
/* 61 */     this.source.call(1, 14, 2058, this.buf);
/*    */   }
/*    */ 
/*    */   public void createDirectory() throws TwainIOException {
/* 65 */     this.source.call(1, 14, 2050, this.buf);
/*    */   }
/*    */ 
/*    */   public void delete() throws TwainIOException {
/* 69 */     this.source.call(1, 14, 2051, this.buf);
/*    */   }
/*    */ 
/*    */   public void formatMedia() throws TwainIOException {
/* 73 */     this.source.call(1, 14, 2052, this.buf);
/*    */   }
/*    */ 
/*    */   public void getClose() throws TwainIOException {
/* 77 */     this.source.call(1, 14, 2053, this.buf);
/*    */   }
/*    */ 
/*    */   public void getFirstFile()
/*    */     throws TwainIOException
/*    */   {
/* 83 */     this.source.call(1, 14, 2054, this.buf);
/*    */   }
/*    */ 
/*    */   public void getInfo() throws TwainIOException {
/* 87 */     this.source.call(1, 14, 2055, this.buf);
/*    */   }
/*    */ 
/*    */   public void getNextFile() throws TwainIOException {
/* 91 */     this.source.call(1, 14, 2056, this.buf);
/*    */   }
/*    */ 
/*    */   public void rename() throws TwainIOException {
/* 95 */     this.source.call(1, 14, 2057, this.buf);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainFileSystem
 * JD-Core Version:    0.6.2
 */