/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ public class TwainImageLayout
/*    */   implements TwainConstants
/*    */ {
/*    */   TwainSource source;
/* 23 */   byte[] buf = new byte[28];
/*    */ 
/*    */   public TwainImageLayout(TwainSource paramTwainSource) {
/* 26 */     this.source = paramTwainSource;
/*    */   }
/*    */ 
/*    */   public void get() throws TwainIOException {
/* 30 */     this.source.call(2, 258, 1, this.buf);
/*    */   }
/*    */ 
/*    */   public void getDefault() throws TwainIOException {
/* 34 */     this.source.call(2, 258, 3, this.buf);
/*    */   }
/*    */ 
/*    */   public void set() throws TwainIOException {
/* 38 */     this.source.call(2, 258, 6, this.buf);
/*    */   }
/*    */ 
/*    */   public void reset() throws TwainIOException {
/* 42 */     this.source.call(2, 258, 7, this.buf);
/*    */   }
/*    */   public double getLeft() {
/* 45 */     return jtwain.getFIX32(this.buf, 0); } 
/* 46 */   public void setLeft(double paramDouble) { jtwain.setFIX32(this.buf, 0, paramDouble); } 
/* 47 */   public double getTop() { return jtwain.getFIX32(this.buf, 4); } 
/* 48 */   public void setTop(double paramDouble) { jtwain.setFIX32(this.buf, 4, paramDouble); } 
/* 49 */   public double getRight() { return jtwain.getFIX32(this.buf, 8); } 
/* 50 */   public void setRight(double paramDouble) { jtwain.setFIX32(this.buf, 8, paramDouble); } 
/* 51 */   public double getBottom() { return jtwain.getFIX32(this.buf, 12); } 
/* 52 */   public void setBottom(double paramDouble) { jtwain.setFIX32(this.buf, 12, paramDouble); } 
/*    */   public int getDocumentNumber() {
/* 54 */     return jtwain.getINT32(this.buf, 16); } 
/* 55 */   public void setDocumentNumber(int paramInt) { jtwain.setINT32(this.buf, 16, paramInt); } 
/* 56 */   public int getPageNumber() { return jtwain.getINT32(this.buf, 20); } 
/* 57 */   public void setPageNumber(int paramInt) { jtwain.setINT32(this.buf, 20, paramInt); } 
/* 58 */   public int getFrameNumber() { return jtwain.getINT32(this.buf, 24); } 
/* 59 */   public void setFrameNumber(int paramInt) { jtwain.setINT32(this.buf, 24, paramInt); }
/*    */ 
/*    */   public String toString() {
/* 62 */     String str = "TwainImageLayout\n";
/* 63 */     str = str + "\tleft   =" + getLeft() + "\n";
/* 64 */     str = str + "\ttop    =" + getTop() + "\n";
/* 65 */     str = str + "\tright  =" + getRight() + "\n";
/* 66 */     str = str + "\tbottom =" + getBottom() + "\n";
/*    */ 
/* 68 */     str = str + "\tdocument number =" + getDocumentNumber() + "\n";
/* 69 */     str = str + "\tpage number     =" + getPageNumber() + "\n";
/* 70 */     str = str + "\tframe number    =" + getFrameNumber() + "\n";
/* 71 */     return str;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainImageLayout
 * JD-Core Version:    0.6.2
 */