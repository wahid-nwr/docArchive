/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ public class TwainImageInfo
/*    */   implements TwainConstants
/*    */ {
/*    */   TwainSource source;
/* 22 */   byte[] buf = new byte[42];
/*    */ 
/*    */   public TwainImageInfo(TwainSource paramTwainSource) {
/* 25 */     this.source = paramTwainSource;
/*    */   }
/*    */ 
/*    */   public void get() throws TwainIOException {
/* 29 */     this.source.call(2, 257, 1, this.buf);
/*    */   }
/*    */   public double getXResolution() {
/* 32 */     return jtwain.getFIX32(this.buf, 0); } 
/* 33 */   public double getYResolution() { return jtwain.getFIX32(this.buf, 4); } 
/* 34 */   public int getWidth() { return jtwain.getINT32(this.buf, 8); } 
/* 35 */   public int getHeight() { return jtwain.getINT32(this.buf, 12); } 
/* 36 */   public int getSamplesPerPixel() { return jtwain.getINT16(this.buf, 16); } 
/* 37 */   public int getBitsPerSample(int paramInt) { return jtwain.getINT16(this.buf, 18 + paramInt * 2); } 
/* 38 */   public int getBitsPerPixel() { return jtwain.getINT16(this.buf, 34); } 
/* 39 */   public boolean getPlanar() { return jtwain.getINT16(this.buf, 36) != 0; } 
/* 40 */   public int getPixelType() { return jtwain.getINT16(this.buf, 38); } 
/* 41 */   public int getCompression() { return jtwain.getINT16(this.buf, 40); }
/*    */ 
/*    */   public String toString() {
/* 44 */     String str = "TwainImageInfo\n";
/* 45 */     str = str + "\tx-resolution =" + getXResolution() + "\n";
/* 46 */     str = str + "\ty-resolution =" + getYResolution() + "\n";
/* 47 */     str = str + "\twidth =" + getWidth() + "\n";
/* 48 */     str = str + "\theight=" + getHeight() + "\n";
/* 49 */     int i = getSamplesPerPixel();
/* 50 */     str = str + "\tspp=" + i + "\n";
/* 51 */     for (int j = 0; j < i; j++) {
/* 52 */       str = str + "\tbps[" + j + "]=" + getBitsPerSample(j) + "\n";
/*    */     }
/* 54 */     str = str + "\tplanar=" + getPlanar() + "\n";
/* 55 */     str = str + "\tpixel type=" + getPixelType() + "\n";
/* 56 */     str = str + "\tcompression=" + getCompression() + "\n";
/* 57 */     return str;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainImageInfo
 * JD-Core Version:    0.6.2
 */